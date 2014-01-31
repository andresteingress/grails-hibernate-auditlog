package grails.plugin.hibernateaudit

import groovy.util.logging.Commons

/**
 * Central class that dispatches to the underlying audit log event table.
 */
@Commons
class AuditLogEventActions {

    final AuditLogListener auditLogListener
    final AuditLogEventRepository auditEventLogRepository

    protected AuditLogEventActions(AuditLogListener listener)  {
        this.auditLogListener = listener
        this.auditEventLogRepository = new AuditLogEventRepository(auditLogListener)
    }

    void onInsert(def domain)  {
        try {
            auditEventLogRepository.insert(new AuditableDomainObject(auditLogListener, domain))
        }
        catch (e) {
            log.error "Audit plugin unable to process insert event for ${domain.class.name}", e
        }
    }

    void onBeforeUpdate(def domain)  {
        try {
            def dirtyProperties = domain.dirtyPropertyNames
            if (!dirtyProperties) return

            auditEventLogRepository.update(new AuditableDomainObject(auditLogListener, domain))
        }
        catch (e) {
            log.error "Audit plugin unable to process update event for ${domain.class.name}", e
        }
    }

    void onBeforeDelete(def domain)  {
        try {
            auditEventLogRepository.delete(new AuditableDomainObject(auditLogListener, domain))
        }
        catch (e) {
            log.error "Audit plugin unable to process delete event for ${domain.class.name}", e
        }
    }

}
