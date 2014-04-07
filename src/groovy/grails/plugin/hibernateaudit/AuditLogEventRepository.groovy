package grails.plugin.hibernateaudit

import grails.plugin.hibernateaudit.converters.AuditLogConversionService
import grails.plugin.hibernateaudit.domain.AuditLogType
import grails.plugin.hibernateaudit.domain.AuditableDomainObject
import grails.plugin.hibernateaudit.validation.AuditLogEventPreparation
import org.hibernate.StatelessSession

/**
 * DAO for audit log events.
 */
class AuditLogEventRepository {

    static final String EVENT_NAME_INSERT = "INSERT"
    static final String EVENT_NAME_UPDATE = "UPDATE"
    static final String EVENT_NAME_DELETE = "DELETE"

    AuditLogListener auditLogListener
    AuditLogEventPreparation auditLogEventPreparation
    AuditLogConversionService auditLogConversionService

    def insert(AuditableDomainObject domain) {
        def type = domain.insertAuditLogType()
        if (type == AuditLogType.NONE) return

        withStatelessSession { StatelessSession session ->
            if (type == AuditLogType.FULL)  {
                def map = domain.toMap()
                saveAuditLogEventFULL(session, EVENT_NAME_INSERT, domain, auditLogConversionService.convert(map))
            }

            if (type == AuditLogType.SHORT)  {
                saveAuditLogEventSHORT(session, EVENT_NAME_INSERT, domain)
            }
        }
    }

    def update(AuditableDomainObject domain)  {
        def type = domain.updateAuditLogType()
        if (type == AuditLogType.NONE) return

        Collection<String> dirtyProperties = domain.dirtyPropertyNames
        if (!dirtyProperties) return

        withStatelessSession { StatelessSession session ->
            if (type == AuditLogType.FULL)  {
                Map newMap = domain.toDirtyPropertiesMap()
                Map oldMap = domain.toPersistentValueMap(dirtyProperties)

                def changeMap = newMap.findAll { String key, def value ->
                    def oldValue = oldMap[key]
                    return (oldValue != value)
                }

                if (changeMap)
                    saveAuditLogEventFULL(session, EVENT_NAME_UPDATE, domain, auditLogConversionService.convert(changeMap))
            }

            if (type == AuditLogType.SHORT)  {
                saveAuditLogEventSHORT(session, EVENT_NAME_UPDATE, domain)
            }
        }
    }

    def delete(AuditableDomainObject domain) {
        def type = domain.deleteAuditLogType()
        if (type == AuditLogType.NONE) return

        withStatelessSession { StatelessSession session ->
            if (type == AuditLogType.FULL)  {
                def map = domain.toMap()
                saveAuditLogEventFULL(session, EVENT_NAME_DELETE, domain, auditLogConversionService.convert(map))
            }

            if (type == AuditLogType.SHORT)  {
                saveAuditLogEventSHORT(session, EVENT_NAME_DELETE, domain)
            }
        }
    }

    protected void withStatelessSession(Closure c)  {
        def session = auditLogListener.sessionFactory.openStatelessSession()
        try {
            c.call(session)
        } finally {
            session.close()
        }
    }

    protected void saveAuditLogEventFULL(StatelessSession session, String eventName, AuditableDomainObject domain, String value) {
        def audit = auditLogEventPreparation.prepare new AuditLogEvent(
                actor: auditLogListener.getActor(),
                uri: auditLogListener.getUri(),
                className: domain.className,
                eventName: eventName,
                persistedObjectId: domain.id?.toString() ?: "NA",
                value: value)

        if (!audit.validate()) throw new RuntimeException("Audit log event validation failed: ${audit.errors}")

        session.insert(audit)
    }

    protected void saveAuditLogEventSHORT(StatelessSession session, String eventName, AuditableDomainObject domain) {
        def audit = auditLogEventPreparation.prepare new AuditLogEvent(
                actor: "",
                uri: "",
                className: domain.className,
                eventName: eventName,
                persistedObjectId: domain.id?.toString() ?: "NA",
                value: null)

        if (!audit.validate()) throw new RuntimeException("Audit log event validation failed: ${audit.errors}")

        session.insert(audit)
    }
}