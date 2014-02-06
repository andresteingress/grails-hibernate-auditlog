package grails.plugin.hibernateaudit

import grails.plugin.hibernateaudit.domain.AuditableDomainObject
import groovy.util.logging.Log4j

/**
 * Central class that dispatches to the underlying audit log event table.
 */
@Log4j
class AuditLogEventActions {

    AuditLogListener auditLogListener
    AuditLogEventRepository auditLogEventRepository

    void onInsert(def domain)  {
        try {
            auditLogEventRepository.insert(new AuditableDomainObject(auditLogListener, domain))
        }
        catch (e) {
            log.error "Audit plugin unable to process insert event for ${domain.class.name}", e
        }
    }

    void onBeforeUpdate(def domain)  {
        try {
            def dirtyProperties = domain.dirtyPropertyNames
            if (!dirtyProperties) return

            auditLogEventRepository.update(new AuditableDomainObject(auditLogListener, domain))
        }
        catch (e) {
            log.error "Audit plugin unable to process update event for ${domain.class.name}", e
        }
    }

    void onBeforeDelete(def domain)  {
        try {
            auditLogEventRepository.delete(new AuditableDomainObject(auditLogListener, domain))
        }
        catch (e) {
            log.error "Audit plugin unable to process delete event for ${domain.class.name}", e
        }
    }

}
