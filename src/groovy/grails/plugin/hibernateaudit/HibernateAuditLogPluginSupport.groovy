package grails.plugin.hibernateaudit

import grails.plugin.hibernateaudit.converters.AuditLogJsonConversionService
import groovy.util.logging.Log4j

/**
 * Hibernate audit plugin support class.
 */
@Log4j
class HibernateAuditLogPluginSupport {

    static doWithSpring = {
        if (!application.config.auditLog.disabled)  {

            log.info "Hibernate Audit Log Plugin enabled."

            auditLogListener(AuditLogListener, ref('hibernateDatastore'))  {
                grailsApplication = application
                sessionFactory = ref('sessionFactory')

                defaultInsertAuditLogType = application.config.auditLog.defaultInsertAuditLogType ?: AuditLogType.FULL
                defaultUpdateAuditLogType = application.config.auditLog.defaultUpdateAuditLogType ?: AuditLogType.FULL
                defaultDeleteAuditLogType = application.config.auditLog.defaultDeleteAuditLogType ?: AuditLogType.FULL

                sessionAttribute = application.config.auditLog.sessionAttribute ?: ""
                actorKey = application.config.auditLog.actorKey ?: ""
                truncateLength = application.config.auditLog.truncateLength ?: AuditLogEvent.MAX_SIZE
                // actorClosure = application.config.auditLog.actorClosure
                defaultExcludeList = application.config.auditLog.defaultExclude?.asImmutable() ?: ['version', 'lastUpdated'].asImmutable()

                auditLogEventActions = ref('auditLogEventActions')
            }

            auditLogEventPreparation(AuditLogEventPreparation)  {
                auditLogListener = ref('auditLogListener')
            }

            // might be overridden by applications by using AuditLogSimpleStringConversionService
            auditLogConversionService(AuditLogJsonConversionService)

            auditLogEventRepository(AuditLogEventRepository) {
                auditLogListener = ref('auditLogListener')
                auditLogEventPreparation = ref('auditLogEventPreparation')
                auditLogConversionService = ref('auditLogConversionService')
            }

            auditLogEventActions(AuditLogEventActions)  {
                auditLogListener = ref('auditLogListener')
                auditLogEventRepository = ref('auditLogEventRepository')
            }
        }
    }

    static onConfigChange = { event ->
        def config = event.source
        def appCtx = event.ctx

        if (appCtx.auditLogListener)  {
            log.info "Reloading Hibernate Audit Log Plugin configuration"

            def listener = appCtx.auditLogListener

            listener.defaultInsertAuditLogType = application.config.auditLog.defaultInsertAuditLogType ?: AuditLogType.FULL
            listener.defaultUpdateAuditLogType = application.config.auditLog.defaultUpdateAuditLogType ?: AuditLogType.FULL
            listener.defaultDeleteAuditLogType = application.config.auditLog.defaultDeleteAuditLogType ?: AuditLogType.FULL

            listener.sessionAttribute = config.auditLog.sessionAttribute ?: ""
            listener.actorKey = config.auditLog.actorKey ?: ""
            listener.truncateLength = config.auditLog.truncateLength ?: AuditLogEvent.MAX_SIZE
            // listener.actorClosure = application.config.auditLog.actorClosure
            listener.defaultExcludeList = config.auditLog.defaultExclude?.asImmutable() ?: ['version', 'lastUpdated'].asImmutable()
        }
    }
}