package grails.plugin.hibernateaudit

/**
 * Prepare the audit log event right before it is stored in the DB.
 */
class AuditLogEventPreparation {

    final AuditLogListener auditLogListener

    protected AuditLogEventPreparation(AuditLogListener listener)  {
        this.auditLogListener = listener
    }

    AuditLogEvent prepare(AuditLogEvent auditLogEvent)  {
        auditLogEvent.dateCreated = new Date()
        auditLogEvent.newValue = truncate(auditLogEvent.newValue)
        auditLogEvent.oldValue = truncate(auditLogEvent.oldValue)
        auditLogEvent
    }

    protected String truncate(String value)  {
        if (!value) return value
        def maxLength = Math.min(value.length(), auditLogListener.truncateLength ?: Integer.MAX_VALUE)
        return value.substring(0, maxLength)
    }
}
