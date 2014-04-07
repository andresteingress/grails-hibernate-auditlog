package grails.plugin.hibernateaudit

import grails.plugin.hibernateaudit.domain.AuditLogType
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

        auditLogListener.defaultInsertAuditLogType = AuditLogType.FULL
        auditLogListener.defaultUpdateAuditLogType = AuditLogType.FULL
        auditLogListener.defaultDeleteAuditLogType = AuditLogType.FULL

        auditLogListener.actorClosure = { GrailsWebRequest request, GrailsHttpSession session -> "system" }
    }

    @Test
    void testInsertEventFull() {
        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassName('Tester')
        assert auditLogEvent != null

        assert auditLogEvent.actor == 'system'
        assert auditLogEvent.className == 'Tester'
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_INSERT
        assert auditLogEvent.value == '{"name":"Andre"}'
        assert auditLogEvent.persistedObjectId == p.id as String
    }

    @Test
    void testInsertEventShort() {
        auditLogListener.defaultInsertAuditLogType = AuditLogType.SHORT

        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassName('Tester')
        assert auditLogEvent != null

        assert auditLogEvent.actor == null
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.className == 'Tester'
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_INSERT
        assert auditLogEvent.value == null
    }

    @Test
    void testUpdateEventFull() {
        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)
        p.name = 'Max'
        p.save(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassNameAndEventName('Tester', AuditLogEventRepository.EVENT_NAME_UPDATE)
        assert auditLogEvent != null

        assert auditLogEvent.actor == 'system'
        assert auditLogEvent.className == 'Tester'
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_UPDATE
        assert auditLogEvent.value == '{"name":"Max"}'
        assert auditLogEvent.persistedObjectId == p.id as String
    }

    @Test
    void testUpdateEventShort() {
        auditLogListener.defaultUpdateAuditLogType = AuditLogType.SHORT

        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)
        p.name = 'Max'
        p.save(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassNameAndEventName('Tester', AuditLogEventRepository.EVENT_NAME_UPDATE)
        assert auditLogEvent != null

        assert auditLogEvent.actor == null
        assert auditLogEvent.className == 'Tester'
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_UPDATE
        assert auditLogEvent.value == null
    }

    @Test
    void testDeleteEventFull() {
        auditLogListener.defaultInsertAuditLogType = AuditLogType.NONE
        auditLogListener.defaultUpdateAuditLogType = AuditLogType.NONE
        auditLogListener.defaultDeleteAuditLogType = AuditLogType.FULL

        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)
        p.name = 'Max'
        p.save(flush: true)
        p.delete(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassNameAndEventName('Tester', AuditLogEventRepository.EVENT_NAME_DELETE)
        assert auditLogEvent != null

        assert auditLogEvent.actor == 'system'
        assert auditLogEvent.className == 'Tester'
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_DELETE
        assert auditLogEvent.value == '{"name":"Max"}'
        assert auditLogEvent.persistedObjectId == p.id as String
    }

    @Test
    void testDeleteEventShort() {
        auditLogListener.defaultInsertAuditLogType = AuditLogType.NONE
        auditLogListener.defaultUpdateAuditLogType = AuditLogType.NONE
        auditLogListener.defaultDeleteAuditLogType = AuditLogType.SHORT

        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)
        p.name = 'Max'
        p.save(flush: true)
        p.delete(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassNameAndEventName('Tester', AuditLogEventRepository.EVENT_NAME_DELETE)
        assert auditLogEvent != null

        assert auditLogEvent.actor == null
        assert auditLogEvent.className == 'Tester'
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_DELETE
        assert auditLogEvent.value == null
    }
}
