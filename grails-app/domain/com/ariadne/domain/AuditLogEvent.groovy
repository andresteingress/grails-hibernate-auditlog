package com.ariadne.domain

import grails.util.Holders

/**
 * AuditLogEvents are reported to the AuditLog table this requires you to set up a table or allow
 * Grails to create a table for you.
 */
class AuditLogEvent implements Serializable {

    static Long MAX_SIZE = 65534

    static auditable = false

    Date dateCreated
    Date lastUpdated

    String actor
    String uri
    String className
    String persistedObjectId

    String eventName
    String propertyName
    String oldValue
    String newValue

    static constraints = {
        actor(nullable: true)
        uri(nullable: true)
        className(nullable: true)
        persistedObjectId(nullable: true)
        eventName(nullable: true)
        propertyName(nullable: true)

        oldValue(nullable: true, maxSize: 65534)
        newValue(nullable: true, maxSize: 65534)
    }

    static mapping = {
        table Holders.config.auditLog.tablename ?: 'audit_log'

        // Disable caching by setting auditLog.cacheDisabled = true in your app's Config.groovy
        if (!Holders.config.auditLog.cacheDisabled) {
            cache usage: 'read-only', include: 'non-lazy'
        }

        version false
    }

    /**
     * A very Groovy de-serializer that maps a stored map onto the object
     * assuming that the keys match attribute properties.
     */
    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        def map = input.readObject()
        map.each { k, v -> this."$k" = v }
    }

    /**
     * Because Closures do not serialize we can't send the constraints closure
     * to the Serialize API so we have to have a custom serializer to allow for
     * this object to show up inside a webFlow context.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        def map = [
                id: id,
                dateCreated: dateCreated,
                lastUpdated: lastUpdated,

                actor: actor,
                uri: uri,
                className: className,
                persistedObjectId: persistedObjectId,

                eventName: eventName,
                propertyName: propertyName,
                oldValue: oldValue,
                newValue: newValue,
        ]
        out.writeObject(map)
    }

    String toString() {
        String actorStr = actor ? "user ${actor}" : "user ?"
        "audit log ${dateCreated} ${actorStr} " +
                "${eventName} ${className} " +
                "id:${persistedObjectId}"
    }
}
