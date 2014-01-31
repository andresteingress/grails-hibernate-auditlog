import org.grails.haudit.AuditLogListener
import org.grails.haudit.AuditLogListenerUtil
import org.grails.haudit.AuditLogEvent

// Place your Spring DSL code here
beans = {
    auditLogListener(AuditLogListener, ref('hibernateDatastore'))  {
        grailsApplication = ref('grailsApplication')
        sessionFactory = ref('sessionFactory')

        sessionAttribute = grailsApplication.config.auditLog.sessionAttribute ?: ""
        actorKey = grailsApplication.config.auditLog.actorKey ?: ""
        truncateLength = grailsApplication.config.auditLog.truncateLength ?: AuditLogEvent.MAX_SIZE
        actorClosure = grailsApplication.config.auditLog.actorClosure ?: AuditLogListenerUtil.actorDefaultGetter
        defaultIgnoreList = grailsApplication.config.auditLog.defaultIgnore?.asImmutable() ?: ['version', 'lastUpdated'].asImmutable()
    }
}