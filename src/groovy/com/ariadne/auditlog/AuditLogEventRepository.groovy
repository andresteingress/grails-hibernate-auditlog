package com.ariadne.auditlog

import com.ariadne.domain.AuditLogEvent
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
            def map = domain.toMap()
            map.each { key, value ->
                saveAuditLogEvent(session, EVENT_NAME_INSERT, domain, key, value)
            }
        }
    }

    def update(AuditableDomainObject domain)  {
        Collection<String> dirtyProperties = domain.dirtyPropertyNames
        if (!dirtyProperties) return

        Map newMap = domain.toMap(dirtyProperties)
        Map oldMap = domain.toPersistentValueMap(dirtyProperties)

        withStatelessSession { StatelessSession session ->
            newMap.each { String key, def value ->
                def oldValue = oldMap[key]
                if (oldValue != value)  {
                    saveAuditLogEvent(session, EVENT_NAME_UPDATE, domain, key, value, oldValue)
                }
            }
        }
    }

    def delete(AuditableDomainObject domain) {
        def map = domain.toMap()
        withStatelessSession { StatelessSession session ->
            map.each { key, value ->
                saveAuditLogEvent(session, EVENT_NAME_DELETE, domain, key, value)
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

    protected void saveAuditLogEvent(StatelessSession session, String eventName, AuditableDomainObject domain, String key, value, oldValue = null) {
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
}