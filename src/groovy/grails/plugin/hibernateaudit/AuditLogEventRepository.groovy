package grails.plugin.hibernateaudit

import org.hibernate.StatelessSession

/**
 * DAO for audit log events.
 */
class AuditLogEventRepository {

    static final String EVENT_NAME_INSERT = "INSERT"
    static final String EVENT_NAME_UPDATE = "UPDATE"
    static final String EVENT_NAME_DELETE = "DELETE"

    final AuditLogListener auditLogListener
    final AuditLogEventPreparation auditLogEventPreparation

    protected AuditLogEventRepository(AuditLogListener listener)  {
        this.auditLogListener = listener
        this.auditLogEventPreparation = new AuditLogEventPreparation(auditLogListener)

    }
    def insert(AuditableDomainObject domain) {
        withStatelessSession { StatelessSession session ->
            def type = auditLogListener.defaultInsertAuditLogType
            if (type in [AuditLogType.FULL, AuditLogType.MEDIUM])  {
                def map = domain.toMap()
                map.each { key, value ->
                    this."saveAuditLogEvent${type.toString()}"(session, EVENT_NAME_INSERT, domain, key, value)
                }
            }

            if (type == AuditLogType.SHORT)  {
                saveAuditLogEventSHORT(session, EVENT_NAME_INSERT, domain)
            }
        }
    }

    def update(AuditableDomainObject domain)  {
        Collection<String> dirtyProperties = domain.dirtyPropertyNames
        if (!dirtyProperties) return

        withStatelessSession { StatelessSession session ->
            def type = auditLogListener.defaultUpdateAuditLogType
            if (type in [AuditLogType.FULL, AuditLogType.MEDIUM])  {
                Map newMap = domain.toMap(dirtyProperties)
                Map oldMap = domain.toPersistentValueMap(dirtyProperties)

                newMap.each { String key, def value ->
                    def oldValue = oldMap[key]
                    if (oldValue != value)  {
                        this."saveAuditLogEvent${type.toString()}"(session, EVENT_NAME_UPDATE, domain, key, value, oldValue)
                    }
                }
            }

            if (type == AuditLogType.SHORT)  {
                saveAuditLogEventSHORT(session, EVENT_NAME_UPDATE, domain)
            }
        }
    }

    def delete(AuditableDomainObject domain) {
        withStatelessSession { StatelessSession session ->
            def type = auditLogListener.defaultDeleteAuditLogType
            if (type in [AuditLogType.FULL, AuditLogType.MEDIUM])  {
                def map = domain.toMap()
                map.each { key, value ->
                    this."saveAuditLogEvent${type.toString()}"(session, EVENT_NAME_DELETE, domain, key, value)
                }
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

    protected void saveAuditLogEventFULL(StatelessSession session, String eventName, AuditableDomainObject domain, String key, value, oldValue = null) {
        def audit = auditLogEventPreparation.prepare new AuditLogEvent(
                actor: auditLogListener.getActor(),
                uri: auditLogListener.getUri(),
                className: domain.className,
                eventName: eventName,
                persistedObjectId: domain.id?.toString() ?: "NA",
                propertyName: key,
                oldValue: oldValue,
                newValue: value)

        if (!audit.validate()) throw new RuntimeException("Audit log event validation failed: ${audit.errors}")

        session.insert(audit)
    }

    protected void saveAuditLogEventMEDIUM(StatelessSession session, String eventName, AuditableDomainObject domain, String key, value, oldValue = null) {
        def audit = auditLogEventPreparation.prepare new AuditLogEvent(
                actor: "",
                uri: "",
                className: domain.className,
                eventName: eventName,
                persistedObjectId: domain.id?.toString() ?: "NA",
                propertyName: key,
                oldValue: oldValue,
                newValue: value)

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
                propertyName: null,
                oldValue: null,
                newValue: null)

        if (!audit.validate()) throw new RuntimeException("Audit log event validation failed: ${audit.errors}")

        session.insert(audit)
    }
}