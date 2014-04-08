package grails.plugin.hibernateaudit

import grails.plugin.hibernateaudit.domain.AuditLogType
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class AuditLogIntegrationTests extends GroovyTestCase {

    AuditLogListener auditLogListener

    @Before
    void setUp() {
        auditLogListener.defaultInsertAuditLogType = AuditLogType.FULL
        auditLogListener.defaultUpdateAuditLogType = AuditLogType.FULL
        auditLogListener.defaultDeleteAuditLogType = AuditLogType.FULL

        auditLogListener.defaultIncludeList = ['name']
        auditLogListener.defaultExcludeList = []

        auditLogListener.actorClosure = { GrailsWebRequest request, GrailsHttpSession session -> "system" }
    }

    @Test
    void insertEvent() {
        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassName(p.id as String, Tester.class.simpleName)
        assert auditLog

        assert auditLog.eventName == 'INSERT'

        assert auditLog.persistedObjectId == p.id as String
        assert auditLog.className == Tester.class.simpleName
        assert auditLog.value == '{"name":"Andre"}'

        assert auditLog.dateCreated != null

        assert auditLog.actor == "system"

    }

    @Test
    void insertEventWithoutDefaultInclude() {
        auditLogListener.defaultIncludeList = []

        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassName(p.id as String, Tester.class.simpleName)
        assert auditLog

        assert auditLog.eventName == 'INSERT'

        assert auditLog.persistedObjectId == p.id as String
        assert auditLog.className == Tester.class.simpleName
        assert auditLog.value == '{"name":"Andre","surName":"Steingress"}'
    }

    @Test
    void insertDisabledEvent() {
        auditLogListener.defaultInsertAuditLogType = AuditLogType.NONE

        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassName(p.id as String, Tester.class.simpleName)
        assert auditLog == null
    }

    @Test
    void updateEvent() {
        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)

        p.name = 'Maxi'
        p.surName = 'Mustermann'
        p.save(flush: true)

        assert ['INSERT', 'UPDATE'] == AuditLogEvent.list(order: 'asc', sort: 'id')*.eventName

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassNameAndEventName(p.id as String, Tester.class.simpleName, "UPDATE")
        assert auditLog

        assert auditLog.eventName == 'UPDATE'

        assert auditLog.persistedObjectId == p.id as String
        assert auditLog.className == Tester.class.simpleName
        assert auditLog.value == '{"name":"Maxi"}'

        assert auditLog.dateCreated != null

        assert auditLog.actor == "system"
    }

    @Test
    void updateEventWithoutDefaultIncludeList() {
        auditLogListener.defaultIncludeList = []

        def p = new Tester(name: "Andre", surName: "Steingress").save(flush: true)

        p.name = 'Maxi'
        p.surName = 'Mustermann'
        p.save(flush: true)

        assert ['INSERT', 'UPDATE'] == AuditLogEvent.list(order: 'asc', sort: 'id')*.eventName

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassNameAndEventName(p.id as String, Tester.class.simpleName, "UPDATE")
        assert auditLog

        assert auditLog.eventName == 'UPDATE'
        assert auditLog.value == '{"name":"Maxi","surName":"Mustermann"}'
    }

    @Test
    void updateEventWithoutDefaultIncludeListAndDate() {
        auditLogListener.defaultIncludeList = []

        def p = new Tester2(name: "Andre", surName: "Steingress", birthDate: Date.parse('dd.MM.yyyy', '01.01.1900')).save(flush: true)
        p.save(flush: true)

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassNameAndEventName(p.id as String, Tester2.class.simpleName, "INSERT")
        assert auditLog

        assert auditLog.eventName == 'INSERT'
        assert auditLog.value == '{"birthDate":"1899-12-31T23:00:00+0000","name":"Andre","surName":"Steingress"}'
    }

    @Test
    void updateEventWithoutDefaultIncludeListAndBigDecimal() {
        auditLogListener.defaultIncludeList = []

        def p = new Tester3(name: "Andre", surName: "Steingress", money: 42.42).save(flush: true)
        p.save(flush: true)

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassNameAndEventName(p.id as String, Tester3.class.simpleName, "INSERT")
        assert auditLog

        assert auditLog.eventName == 'INSERT'
        assert auditLog.value == '{"money":42.42,"name":"Andre","surName":"Steingress"}'
    }

    @Test
    void insertEventWithLocalLists() {

        auditLogListener.defaultIncludeList = []
        auditLogListener.defaultExcludeList = []

        def p = new TestPerson(name: "Andre", surName: "Steingress").save(flush: true)

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassName(p.id as String, TestPerson.class.simpleName)
        assert auditLog

        assert auditLog.eventName == 'INSERT'

        assert auditLog.persistedObjectId == p.id as String
        assert auditLog.className == TestPerson.class.simpleName
        assert auditLog.value == '{"name":"Andre"}'

        assert auditLog.dateCreated != null

        assert auditLog.actor == "system"

    }

    @Test
    void insertEventWithShortAuditLogType() {
        def p = new TestPerson3(name: "Andre", surName: "Steingress").save(flush: true)

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassName(p.id as String, TestPerson3.class.simpleName)
        assert auditLog

        assert auditLog.eventName == 'INSERT'
        assert auditLog.persistedObjectId == p.id as String
        assert auditLog.className == TestPerson3.class.simpleName
        assert auditLog.dateCreated != null

        assert auditLog.value == null
        assert auditLog.actor == null
    }

    @Test
    void updateEventWithShortAuditType() {
        def p = new TestPerson3(name: "Andre", surName: "Steingress").save(flush: true)

        p.name = '"Maxi"'
        p.surName = 'Mustermann'
        p.save(flush: true)

        assert ['INSERT', 'UPDATE'] == AuditLogEvent.list(order: 'asc', sort: 'id')*.eventName

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassNameAndEventName(p.id as String, TestPerson3.class.simpleName, "UPDATE")
        assert auditLog

        assert auditLog.eventName == 'UPDATE'

        assert auditLog.persistedObjectId == p.id as String
        assert auditLog.className == TestPerson3.class.simpleName
        assert auditLog.dateCreated != null

        assert auditLog.actor == null
        assert auditLog.value == null
    }

    @Test
    void updateEventWithOneToOneRelationship() {
        auditLogListener.defaultIncludeList = []
        auditLogListener.defaultInsertAuditLogType = AuditLogType.SHORT

        def parent = new TestPerson4().save(flush: true)
        def child  = new TestPerson5(name: "Max", surName: "Mustermann").save(flush: true)

        parent.testPerson5 = child
        parent.save(flush: true)

        assert ['INSERT', 'INSERT', 'UPDATE'] == AuditLogEvent.list(order: 'asc', sort: 'id')*.eventName

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassNameAndEventName(parent.id as String, 'TestPerson4', 'UPDATE')
        assert auditLog
        assert auditLog.value == '{"testPerson5":' + child.id + '}'
    }

    @Test
    void oneToOneChangingBackAndForth() {
        auditLogListener.defaultIncludeList = []
        auditLogListener.defaultInsertAuditLogType = AuditLogType.SHORT

        def parent = new TestPerson4().save(flush: true)
        def child  = new TestPerson5(name: "Max", surName: "Mustermann").save(flush: true)

        parent.testPerson5 = child
        parent.save(flush: true)
        
        parent.testPerson5 = null
        parent.save(flush: true)

        def logs = AuditLogEvent.findAllByPersistedObjectIdAndClassNameAndEventName(parent.id as String, 'TestPerson4', 'UPDATE')
        assert logs
        assert logs.size() == 2
    }

    @Test
    void updateEventWithManyToOneRelationship() {
        auditLogListener.defaultIncludeList = []
        auditLogListener.defaultInsertAuditLogType = AuditLogType.SHORT

        def parent = new TestPerson6().save(flush: true)
        def child1  = new TestPerson5(name: "Max", surName: "Mustermann").save(flush: true)
        def child2  = new TestPerson5(name: "Erika", surName: "Mustermann").save(flush: true)

        parent.addToTestPerson5(child1)
        parent.addToTestPerson5(child2)
        parent.save(flush: true)

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassNameAndEventName(parent.id as String, 'TestPerson6', 'UPDATE')
        assert auditLog
        assert auditLog.value == "{\"testPerson5\":[${child1.id},${child2.id}]}"
    }
}
