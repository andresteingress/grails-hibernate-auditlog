package com.ariadne.auditlog

import com.ariadne.domain.AuditLogEvent
import com.ariadne.domain.Person
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.hibernate.SessionFactory
import org.junit.Before
import org.junit.Test

class AuditLogIntegrationTests extends GroovyTestCase {

    AuditLogListener auditLogListener

    @Before
    void setUp() {
        auditLogListener.verbose = true

        auditLogListener.defaultIncludeList = ['name']
        auditLogListener.defaultIgnoreList = []

        auditLogListener.actorClosure = { GrailsWebRequest request, GrailsHttpSession session -> "system" }
    }

    @Test
    void testInsertEvent() {
        def p = new Person(name: "Andre", surName: "Steingress").save()

        def auditLog = AuditLogEvent.findByPersistedObjectIdAndClassName(p.id as String, Person.class.simpleName)
        assert auditLog

        assert auditLog.eventName == 'INSERT'

        assert auditLog.persistedObjectId == p.id as String
        assert auditLog.className == Person.class.simpleName
        assert auditLog.propertyName == 'name'

        assert auditLog.dateCreated != null
        assert auditLog.lastUpdated != null

        assert auditLog.actor == "system"

    }

    SessionFactory sessionFactory

    @Test
    void testUpdateEvent() {
        def p = new Person(name: "Andre", surName: "Steingress").save()

        sessionFactory.currentSession.flush()

        p.name = 'Maxi'
        p.surName = 'Mustermann'
        p.save()

        sessionFactory.currentSession.flush()

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
        assert auditLog.lastUpdated != null

        assert auditLog.actor == "system"
    }
}
