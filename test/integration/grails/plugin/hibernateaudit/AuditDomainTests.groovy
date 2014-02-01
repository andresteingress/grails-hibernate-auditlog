package grails.plugin.hibernateaudit

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.junit.Before
import org.junit.Test

class AuditDomainTests extends GroovyTestCase {

    AuditLogListener auditLogListener

    @Before
    void setUp() {
        auditLogListener.defaultIncludeList = ['name']
        auditLogListener.defaultExcludeList = []

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
