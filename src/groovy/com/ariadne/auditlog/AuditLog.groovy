package com.ariadne.auditlog

import com.ariadne.domain.AuditLogEvent
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * Helper class to bootstrap the audit log mechanism.
 */
class AuditLog {

    static void bootstrap(GrailsApplication grailsApplication)  {
        if (!grailsApplication.config.auditLog.disabled) {
            grailsApplication.mainContext.eventTriggeringInterceptor.datastores.each { key, datastore ->

                def listener = new AuditLogListener(datastore)
                listener.grailsApplication = grailsApplication
                listener.verbose = grailsApplication.config.auditLog.verbose ?: false
                listener.transactional = grailsApplication.config.auditLog.transactional ?: false
                listener.sessionAttribute = grailsApplication.config.auditLog.sessionAttribute ?: ""
                listener.actorKey = grailsApplication.config.auditLog.actorKey ?: ""
                listener.truncateLength = grailsApplication.config.auditLog.truncateLength ?: AuditLogEvent.MAX_SIZE
                listener.actorClosure = grailsApplication.config.auditLog.actorClosure ?: AuditLogListenerUtil.actorDefaultGetter
                listener.defaultIgnoreList = grailsApplication.config.auditLog.defaultIgnore?.asImmutable() ?: ['version', 'lastUpdated'].asImmutable()
                listener.defaultMaskList = grailsApplication.config.auditLog.defaultMask?.asImmutable() ?: ['password'].asImmutable()
                listener.propertyMask = grailsApplication.config.auditLog.propertyMask ?: "**********"
                listener.replacementPatterns = grailsApplication.config.auditLog.replacementPatterns
                listener.logIds = grailsApplication.config.auditLog.logIds ?: false

                grailsApplication.mainContext.addApplicationListener(listener)
            }
        }
    }
}
