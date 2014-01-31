package grails.plugin.hibernateaudit

import groovy.util.logging.Commons

/**
 * Hibernate audit plugin support class.
 */
@Commons
class HibernateAuditLogPluginSupport {

    static doWithSpring = {
        if (!application.config.auditLog.disabled)  {

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

    static onConfigChange = { event ->
        def config = event.source
        def appCtx = event.ctx

        if (appCtx.auditLogListener)  {
            def listener = appCtx.auditLogListener
            listener.sessionAttribute = config.auditLog.sessionAttribute ?: ""
            listener.actorKey = config.auditLog.actorKey ?: ""
            listener.truncateLength = config.auditLog.truncateLength ?: AuditLogEvent.MAX_SIZE
            // listener.actorClosure = application.config.auditLog.actorClosure ?: AuditLogListenerUtil.actorDefaultGetter
            listener.defaultIgnoreList = config.auditLog.defaultIgnore?.asImmutable() ?: ['version', 'lastUpdated'].asImmutable()
        }
    }
}