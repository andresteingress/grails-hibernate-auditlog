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

        auditLogListener.defaultInsertAuditLogType = AuditLogType.FULL
        auditLogListener.defaultUpdateAuditLogType = AuditLogType.FULL
        auditLogListener.defaultDeleteAuditLogType = AuditLogType.FULL

        auditLogListener.actorClosure = { GrailsWebRequest request, GrailsHttpSession session -> "system" }

        auditEventLogRepository = new AuditLogEventRepository(auditLogListener)
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
        assert auditLogEvent.newValue == '"Andre"'
        assert auditLogEvent.oldValue == null
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.propertyName == 'name'
    }

    @Test
    void testInsertEventMedium() {
        auditLogListener.defaultInsertAuditLogType = AuditLogType.MEDIUM

        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassName('Tester')
        assert auditLogEvent != null

        assert auditLogEvent.actor == null
        assert auditLogEvent.className == 'Tester'
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_INSERT
        assert auditLogEvent.newValue == '"Andre"'
        assert auditLogEvent.oldValue == null
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.propertyName == 'name'
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
        assert auditLogEvent.newValue == null
        assert auditLogEvent.oldValue == null
        assert auditLogEvent.propertyName == null
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
        assert auditLogEvent.newValue == '"Max"'
        assert auditLogEvent.oldValue == '"Andre"'
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.propertyName == 'name'
    }

    @Test
    void testUpdateEventMedium() {
        auditLogListener.defaultUpdateAuditLogType = AuditLogType.MEDIUM

        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)
        p.name = 'Max'
        p.save(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassNameAndEventName('Tester', AuditLogEventRepository.EVENT_NAME_UPDATE)
        assert auditLogEvent != null

        assert auditLogEvent.actor == null
        assert auditLogEvent.className == 'Tester'
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_UPDATE
        assert auditLogEvent.newValue == '"Max"'
        assert auditLogEvent.oldValue == '"Andre"'
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.propertyName == 'name'
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
        assert auditLogEvent.newValue == null
        assert auditLogEvent.oldValue == null
        assert auditLogEvent.propertyName == null
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
        assert auditLogEvent.newValue == '"Max"'
        assert auditLogEvent.oldValue == null
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.propertyName == 'name'
    }

    @Test
    void testDeleteEventMedium() {
        auditLogListener.defaultInsertAuditLogType = AuditLogType.NONE
        auditLogListener.defaultUpdateAuditLogType = AuditLogType.NONE
        auditLogListener.defaultDeleteAuditLogType = AuditLogType.MEDIUM

        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)
        p.name = 'Max'
        p.save(flush: true)
        p.delete(flush: true)

        def auditLogEvent = AuditLogEvent.findByClassNameAndEventName('Tester', AuditLogEventRepository.EVENT_NAME_DELETE)
        assert auditLogEvent != null

        assert auditLogEvent.actor == null
        assert auditLogEvent.className == 'Tester'
        assert auditLogEvent.dateCreated != null
        assert auditLogEvent.eventName == AuditLogEventRepository.EVENT_NAME_DELETE
        assert auditLogEvent.newValue == '"Max"'
        assert auditLogEvent.oldValue == null
        assert auditLogEvent.persistedObjectId == p.id as String
        assert auditLogEvent.propertyName == 'name'
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
        assert auditLogEvent.newValue == null
        assert auditLogEvent.oldValue == null
        assert auditLogEvent.propertyName == null
    }
}
