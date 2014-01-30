package com.ariadne.auditlog

import com.ariadne.domain.AuditLogEvent

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
        def map = domain.toMap()
        map.each { key, value ->
            saveAuditLogEvent(EVENT_NAME_INSERT, domain, key, value)
        }
    }

    def update(AuditableDomainObject domain)  {
        Collection<String> dirtyProperties = domain.dirtyPropertyNames
        if (!dirtyProperties) return

        Map newMap = domain.toMap(dirtyProperties)
        Map oldMap = domain.toPersistentValueMap(dirtyProperties)

        newMap.each { String key, def value ->
            def oldValue = oldMap[key]
            if (oldValue != value)  {
                saveAuditLogEvent(EVENT_NAME_UPDATE, domain, key, value, oldValue)
            }
        }
    }

    def delete(AuditableDomainObject domain) {
        def map = domain.toMap()
        map.each { key, value ->
            saveAuditLogEvent(EVENT_NAME_DELETE, domain, key, value)
        }
    }

    protected void saveAuditLogEvent(String eventName, AuditableDomainObject domain, String key, value, oldValue = null) {
        def audit = auditLogEventPreparation.prepare new AuditLogEvent(
                actor: auditLogListener.getActor(),
                uri: auditLogListener.getUri(),
                className: domain.className,
                eventName: eventName,
                persistedObjectId: domain.id?.toString() ?: "NA",
                propertyName: key,
                oldValue: oldValue,
                newValue: value)

        AuditLogEvent.withNewSession {
            audit.merge(flush: true, failOnError: true)
        }
    }
}