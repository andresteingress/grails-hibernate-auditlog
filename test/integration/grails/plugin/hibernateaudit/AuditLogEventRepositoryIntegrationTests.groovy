package grails.plugin.hibernateaudit

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.junit.Before
import org.junit.Test

class AuditLogEventRepositoryIntegrationTests extends GroovyTestCase {

    AuditLogListener auditLogListener
    AuditLogEventRepository auditEventLogRepository

    @Before
    void setUp() {
        auditLogListener.defaultIncludeList = ['name']
        auditLogListener.defaultExcludeList = []

        auditLogListener.actorClosure = { GrailsWebRequest request, GrailsHttpSession session -> "system" }

        auditEventLogRepository = new AuditLogEventRepository(auditLogListener)
    }

    @Test
    void testInsertEvent() {
        def p = new Person(name: "Andre", surName: "Steingress").save(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassName('Person')
        assert auditLogEvent != null

        assert auditLogEvent.actor == 'system'
        assert auditLogEvent.className == 'Person'
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_INSERT
        assert auditLogEvent.newValue == 'Andre'
        assert auditLogEvent.oldValue == null
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.propertyName == 'name'
    }

    @Test
    void testUpdateEvent() {
        def p = new Person(name: "Andre", surName: "Steingress").save(flush: true)
        p.name = 'Max'
        p.save(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassNameAndEventName('Person', AuditLogEventRepository.EVENT_NAME_UPDATE)
        assert auditLogEvent != null

        assert auditLogEvent.actor == 'system'
        assert auditLogEvent.className == 'Person'
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_UPDATE
        assert auditLogEvent.newValue == 'Max'
        assert auditLogEvent.oldValue == 'Andre'
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.propertyName == 'name'
    }
}
