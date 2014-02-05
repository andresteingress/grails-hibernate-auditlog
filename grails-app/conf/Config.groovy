import grails.plugin.hibernateaudit.AuditLogType

// All audit log configuration variables
auditLog.disabled = false        // globally disable audit logging

// Configure insert, update, delete logging
auditLog.defaultInsertAuditLogType = AuditLogType.SHORT
auditLog.defaultUpdateAuditLogType = AuditLogType.MEDIUM
auditLog.defaultDeleteAuditLogType = AuditLogType.NONE

auditLog.sessionAttribute = ""   // the session attribute under which the actor name is found
auditLog.actorKey = ""           // the request attribute key under which the actor name is found
auditLog.actorClosure = null

auditLog.defaultInclude = []     // can specify a list of included properties - all others are automatically excluded
auditLog.defaultExclude = []      // can specify a list of properties that are ignored by the audit log

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
