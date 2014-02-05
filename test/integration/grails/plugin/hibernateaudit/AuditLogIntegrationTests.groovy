package grails.plugin.hibernateaudit

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.junit.Before
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
        assert auditLog.propertyName == 'name'

        assert auditLog.dateCreated != null

        assert auditLog.actor == "system"

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
        assert auditLog.propertyName == 'name'
        assert auditLog.newValue == '"Maxi"'
        assert auditLog.oldValue == '"Andre"'

        assert auditLog.dateCreated != null

        assert auditLog.actor == "system"
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
        assert auditLog.propertyName == 'name'

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

        assert auditLog.propertyName == null
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
        assert auditLog.propertyName == null
        assert auditLog.newValue == null
        assert auditLog.oldValue == null
    }
}
