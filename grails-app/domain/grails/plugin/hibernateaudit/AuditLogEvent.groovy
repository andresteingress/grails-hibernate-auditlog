package grails.plugin.hibernateaudit

import grails.util.Holders

/**
 * Audit log event domain class representing changes to Grails domain objects.
 */
class AuditLogEvent implements Serializable {

    Date dateCreated

    // principal and URI
    String actor
    String uri

    // event domain object info
    String eventName
    String className
    String persistedObjectId

    // serialized object state
    String value

    static constraints = {
        actor(nullable: true)
        uri(nullable: true)
        className(nullable: true)
        persistedObjectId(nullable: true)
        eventName(nullable: true)

        value(nullable: true)
    }

    static mapping = {
        table Holders.config.auditLog.tablename ?: 'audit_log'
        value type: 'text'
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
                value: value
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
