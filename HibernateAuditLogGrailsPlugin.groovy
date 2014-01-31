import grails.plugin.hibernateaudit.HibernateAuditLogPluginSupport

/**
 * Grails plugin descriptor
 */
class HibernateAuditLogGrailsPlugin {

    def version = "0.1-SNAPSHOT"
    def grailsVersion = "2.1 > *"

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp",

        // exclude the test domain classes
        "grails-app/domain/grails/plugin/hibernateaudit/Person.groovy",
        "grails-app/domain/grails/plugin/hibernateaudit/TestPerson.groovy"
    ]

    def title = "Grails Hibernate Audit Event Log Plugin"
    def author = "Andre Steingress"
    def authorEmail = "me@andresteingress.com"
    def description = '''\
Enables audit logging for Grails domain classes based on the Hibernate datastore.
'''

    def documentation = "https://github.com/andresteingress/grails-auditlog"
    def license = "MIT"
    def developers = [ [ name: "Andre Steingress", email: "me@andresteingress.com" ]]
    def issueManagement = [ system: "GIT", url: "https://github.com/andresteingress/grails-auditlog/issues" ]
    def scm = [ url: "https://github.com/andresteingress/grails-auditlog" ]

    def observe = ['domain']

    def doWithSpring = HibernateAuditLogPluginSupport.doWithSpring

    def onChange = { event ->
        def source = event.source
        def grailsApplication = event.application
        def applicationContext = event.ctx

        println 'domain class RELOADED'
    }

    def onConfigChange = { event ->

        println  'configuration changed'

    }
}
