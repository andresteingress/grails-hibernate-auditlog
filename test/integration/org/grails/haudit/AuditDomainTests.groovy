package org.grails.haudit

import org.grails.haudit.Person
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.haudit.AuditLogListener
import org.grails.haudit.AuditableDomainObject
import org.junit.Before
import org.junit.Test

class AuditDomainTests extends GroovyTestCase {

    AuditLogListener auditLogListener

    @Before
    void setUp() {
        auditLogListener.defaultIncludeList = ['name']
        auditLogListener.defaultIgnoreList = []

        auditLogListener.actorClosure = { GrailsWebRequest request, GrailsHttpSession session -> "system" }
    }

    @Test
    void createDomain() {
        def p = new Person(name: "Andre", surName: "Steingress").save()
        def auditDomain = new AuditableDomainObject(auditLogListener, p)

        assert auditDomain.logListener != null
        assert auditDomain.className == 'Person'
        assert auditDomain.id == p.id
        assert auditDomain.domainClass != null
        assert auditDomain.toMap() == [name: "Andre"]
    }
}
