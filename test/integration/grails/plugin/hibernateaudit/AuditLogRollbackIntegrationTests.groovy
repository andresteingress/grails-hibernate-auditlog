package grails.plugin.hibernateaudit

import grails.plugin.hibernateaudit.AuditLogListener
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.haudit.AuditLogEvent
import org.grails.haudit.Person
import org.junit.Before
import org.junit.Test
import org.springframework.transaction.TransactionStatus

class AuditLogRollbackIntegrationTests extends GroovyTestCase {

    static transactional = false

    AuditLogListener auditLogListener

    @Before
    void setUp() {
        auditLogListener.defaultIncludeList = ['name']
        auditLogListener.defaultIgnoreList = []

        auditLogListener.actorClosure = { GrailsWebRequest request, GrailsHttpSession session -> "system" }
    }

    @Test
    void insertEventWithRollback() {
        try {
            Person.withTransaction { TransactionStatus status ->
                new Person(name: "Andre", surName: "Steingress").save(flush: true)
                throw new RuntimeException('some exception ...')
            }
        } catch (e) {}

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassName("1", Person.class.simpleName)
        assert auditLog == null

    }
}
