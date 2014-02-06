package grails.plugin.hibernateaudit.domain

import grails.plugin.hibernateaudit.AuditLogListener
import grails.plugin.hibernateaudit.TestPerson3
import grails.plugin.hibernateaudit.TestPerson4
import grails.plugin.hibernateaudit.TestPerson5
import grails.plugin.hibernateaudit.Tester
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.junit.Before
import org.junit.Test

class AuditableDomainObjectTests extends GroovyTestCase {

    AuditLogListener auditLogListener

    @Before
    void setUp() {
        auditLogListener.defaultIncludeList = ['name']
        auditLogListener.defaultExcludeList = []

        auditLogListener.actorClosure = { GrailsWebRequest request, GrailsHttpSession session -> "system" }
    }

    @Test
    void createDomain() {
        def p = new Tester(name: "Andre", surName: "Steingress").save()
        def auditDomain = new AuditableDomainObject(auditLogListener, p)

        assert auditDomain.logListener != null
        assert auditDomain.className == 'Tester'
        assert auditDomain.id == p.id
        assert auditDomain.domainClass != null
        assert auditDomain.toMap() == [name: "Andre"]
    }

    @Test
    void intersectIncludeProperties() {

        auditLogListener.defaultIncludeList = []
        auditLogListener.defaultExcludeList = []

        def p = new TestPerson3(name: "Andre", surName: "Steingress").save()
        def auditDomain = new AuditableDomainObject(auditLogListener, p)

        assert auditDomain.toMap() == [name: "Andre"]
    }

    @Test
    void oneToOneProperties() {

        auditLogListener.defaultIncludeList = []
        auditLogListener.defaultExcludeList = []

        def p = new TestPerson4().save()
        def auditDomain = new AuditableDomainObject(auditLogListener, p)

        assert auditDomain.oneToOneProperties == ['testPerson5']
    }
}
