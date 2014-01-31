package org.grails.haudit

import grails.util.Holders

/**
 * AuditLogEvents are reported to the AuditLog table this requires you to set up a table or allow
 * Grails to create a table for you.
 */
class AuditLogEvent implements Serializable {

    static MAX_SIZE = 65534

    Date dateCreated

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

        oldValue(nullable: true, maxSize: MAX_SIZE)
        newValue(nullable: true, maxSize: MAX_SIZE)
    }

    static mapping = {
        table Holders.config.auditLog.tablename ?: 'audit_log'
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
