package com.ariadne.auditlog

import com.ariadne.domain.AuditLogEvent
import groovy.util.logging.Commons
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.engine.event.*
import org.springframework.context.ApplicationEvent
import org.springframework.web.context.request.RequestContextHolder

/**
 * Grails interceptor for logging saves, updates, deletes and acting on
 * individual properties changes and delegating calls back to the Domain Class
 */
@Commons
class AuditLogListener extends AbstractPersistenceEventListener {

    GrailsApplication grailsApplication

    Integer truncateLength = AuditLogEvent.MAX_SIZE

    String sessionAttribute
    String actorKey
    Closure actorClosure

    // Global list of attribute changes to ignore, defaults to ['version', 'lastUpdated']
    List<String> defaultIncludeList
    List<String> defaultIgnoreList

    final AuditLogEventActions auditClosureActions

    AuditLogListener(Datastore datastore) {
        super(datastore)
        this.auditClosureActions = new AuditLogEventActions(this)
    }

    @Override
    boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType.isAssignableFrom(PostInsertEvent) ||
                eventType.isAssignableFrom(PreUpdateEvent) ||
                eventType.isAssignableFrom(PreDeleteEvent)
    }

    void setActorClosure(Closure closure) {
        closure.delegate = this
        closure.properties['log'] = log
        actorClosure = closure
    }

    String getActor() {
        def actor = null
        if (actorClosure) {
            def attr = RequestContextHolder.getRequestAttributes()
            def session = attr?.session
            if (attr && session) {
                try {
                    actor = actorClosure.call(attr, session)
                }
                catch(ex) {
                    log.error "The auditLog.actorClosure threw this exception", ex
                    log.error "The auditLog.actorClosure will be disabled now."
                    actorClosure = null
                }
            }
            // If we couldn't find an actor, use the configured default or just 'system'
            if (!actor) {
                actor = grailsApplication.config.auditLog.defaultActor ?: 'system'
            }
        }
        return actor?.toString()
    }

    String getUri() {
        def attr = RequestContextHolder?.getRequestAttributes()
        return (attr?.currentRequest?.uri?.toString()) ?: null
    }

    @Override
    protected void onPersistenceEvent(AbstractPersistenceEvent event) {
        if (ClosureReader.isAuditable(event.entityObject?.class)) {
            log.trace "Audit logging: ${event.eventType.name()} for ${event.entityObject.class.name}"

            switch(event.eventType) {
                case EventType.PostInsert:
                    onPostInsert(event as PostInsertEvent)
                    break
                case EventType.PreUpdate:
                    onPreUpdate(event as PreUpdateEvent)
                    break
                case EventType.PreDelete:
                    onPreDelete(event as PreDeleteEvent)
                    break
            }
        }
    }

    protected void onPreDelete(PreDeleteEvent event) {
        def domain = event.entityObject
        auditClosureActions.onBeforeDelete(domain)
    }

    protected void onPostInsert(PostInsertEvent event) {
        def domain = event.entityObject
        auditClosureActions.onInsert(domain)
    }

    protected void onPreUpdate(PreUpdateEvent event) {
        def domain = event.entityObject
        auditClosureActions.onBeforeUpdate(domain)
    }
}
