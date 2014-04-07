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
        auditLogEvent.value = truncate(auditLogEvent.value)
        auditLogEvent
    }

    protected String truncate(String value)  {
        if (!value) return value
        def maxLength = Math.min(value.length(), auditLogListener.truncateLength ?: Integer.MAX_VALUE)
        return value.substring(0, maxLength)
    }
}
