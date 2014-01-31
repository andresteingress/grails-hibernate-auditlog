import grails.plugin.hibernateaudit.HibernateAuditLogPluginSupport

class HibernateAuditLogPlugin {

    def version = "0.1"
    def grailsVersion = "2.1 > *"
    def dependsOn = [hibernate: "2.1 > *"]

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
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

    def doWithApplicationContext = HibernateAuditLogPluginSupport.doWithSpring

    def onChange = { event ->

    }

    def onConfigChange = { event ->

    }
}
