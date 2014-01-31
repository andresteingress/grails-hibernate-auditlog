import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest

// All audit log configuration variables
auditLog.disabled = false        // globally disable audit logging

auditLog.sessionAttribute = ""   // the session attribute under which the actor name is found
auditLog.actorKey = ""           // the request attribute key under which the actor name is found

auditLog.actorClosure = {  GrailsWebRequest request, GrailsHttpSession session ->
    if (request.applicationContext.springSecurityService.principal instanceof String){
        return request.applicationContext.springSecurityService.principal
    }
    def username = request.applicationContext.springSecurityService.principal?.username
    if (SpringSecurityUtils.isSwitched()){
        username = SpringSecurityUtils.switchedUserOriginalUsername+" AS "+username
    }
    return username
}

auditLog.defaultInclude = []     // can specify a list of included properties - all others are automatically excluded
auditLog.defaultIgnore = []      // can specify a list of properties that are ignored by the audit log

// audit log persistence settings
auditLog.tablename = null        // custom AuditLog table name
auditLog.truncateLength = null   // can provide a maximum length for values in the audit log

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

log4j = {
         error 'org.codehaus.groovy.grails.plugins',            // plugins
               'org.springframework',
               'org.hibernate',
               'net.sf.ehcache.hibernate'
}
