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
        def p = new Person(name: "Andre", surName: "Steingress").save(flush: true)

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassName(p.id as String, Person.class.simpleName)
        assert auditLog

        assert auditLog.eventName == 'INSERT'

        assert auditLog.persistedObjectId == p.id as String
        assert auditLog.className == Person.class.simpleName
        assert auditLog.propertyName == 'name'

        assert auditLog.dateCreated != null

        assert auditLog.actor == "system"

    }

    @Test
    void updateEvent() {
        def p = new Person(name: "Andre", surName: "Steingress").save(flush: true)

        p.name = 'Maxi'
        p.surName = 'Mustermann'
        p.save(flush: true)

        assert ['INSERT', 'UPDATE'] == AuditLogEvent.list(order: 'asc', sort: 'id')*.eventName

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassNameAndEventName(p.id as String, Person.class.simpleName, "UPDATE")
        assert auditLog

        assert auditLog.eventName == 'UPDATE'

        assert auditLog.persistedObjectId == p.id as String
        assert auditLog.className == Person.class.simpleName
        assert auditLog.propertyName == 'name'
        assert auditLog.newValue == 'Maxi'
        assert auditLog.oldValue == 'Andre'

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
}
