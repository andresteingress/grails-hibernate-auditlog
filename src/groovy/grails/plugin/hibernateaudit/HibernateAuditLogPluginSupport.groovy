package grails.plugin.hibernateaudit
/**
 * Hibernate audit plugin support class.
 */
class HibernateAuditLogPluginSupport {

    static doWithSpring = {
        auditLogListener(AuditLogListener, ref('hibernateDatastore'))  {
            grailsApplication = application
            sessionFactory = ref('sessionFactory')

            sessionAttribute = application.config.auditLog.sessionAttribute ?: ""
            actorKey = application.config.auditLog.actorKey ?: ""
            truncateLength = application.config.auditLog.truncateLength ?: AuditLogEvent.MAX_SIZE
            // actorClosure = application.config.auditLog.actorClosure ?: AuditLogListenerUtil.actorDefaultGetter
            defaultIgnoreList = application.config.auditLog.defaultIgnore?.asImmutable() ?: ['version', 'lastUpdated'].asImmutable()
        }
    }
}
