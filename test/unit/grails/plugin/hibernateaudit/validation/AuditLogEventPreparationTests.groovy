package grails.plugin.hibernateaudit.validation

import grails.plugin.hibernateaudit.AuditLogEvent
import grails.plugin.hibernateaudit.AuditLogListener
import grails.plugin.hibernateaudit.validation.AuditLogEventPreparation
import org.junit.Test

class AuditLogEventPreparationTests {

    @Test
    void dateCreated() {

        def listener = new AuditLogListener(null)
        listener.truncateLength = 10

        def preparation = new AuditLogEventPreparation()
        preparation.auditLogListener = listener

        def auditLogEvent = new AuditLogEvent(newValue: "0123456789a")

        assert preparation.prepare(auditLogEvent).dateCreated != null
    }

    @Test
    void truncateNewValue() {

        def listener = new AuditLogListener(null)
        listener.truncateLength = 10

        def preparation = new AuditLogEventPreparation()
        preparation.auditLogListener = listener
        def auditLogEvent = new AuditLogEvent(newValue: "0123456789a")

        assert preparation.prepare(auditLogEvent).newValue == "0123456789"
    }

    @Test
    void truncateOldValue() {

        def listener = new AuditLogListener(null)
        listener.truncateLength = 10

        def preparation = new AuditLogEventPreparation()
        preparation.auditLogListener = listener

        def auditLogEvent = new AuditLogEvent(oldValue: "0123456789a")

        assert preparation.prepare(auditLogEvent).oldValue == "0123456789"
    }
}
