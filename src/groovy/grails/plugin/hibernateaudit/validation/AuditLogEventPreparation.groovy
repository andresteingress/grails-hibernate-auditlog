package grails.plugin.hibernateaudit.validation

import grails.plugin.hibernateaudit.AuditLogEvent
import grails.plugin.hibernateaudit.AuditLogListener

/**
 * Prepare the audit log event right before it is stored in the DB.
 */
class AuditLogEventPreparation {

    AuditLogListener auditLogListener

    AuditLogEvent prepare(AuditLogEvent auditLogEvent)  {
        auditLogEvent.dateCreated = new Date()
        auditLogEvent
    }
}
