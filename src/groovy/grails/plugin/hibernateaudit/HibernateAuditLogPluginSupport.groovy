package grails.plugin.hibernateaudit

import org.grails.haudit.AuditLogEvent

/**
 * Hibernate audit plugin support class.
 */
class HibernateAuditLogPluginSupport {

    static doWithSpring = {
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
}
