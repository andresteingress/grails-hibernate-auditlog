Grails Hibernate Audit Log Plugin
===============

Grails plugin enabling _audit logging_ for Hibernate domain classes.

Here is a default configuration:

```groovy
auditLog.disabled = false        // globally disable audit logging

auditLog.sessionAttribute = ""   // the session attribute under which the actor name is found
auditLog.actorKey = ""           // the request attribute key under which the actor name is found

auditLog.actorClosure = {  GrailsWebRequest request, GrailsHttpSession session ->
   if (request.applicationContext.springSecurityService.principal instanceof String){
       return request.applicationContext.springSecurityService.principal
   }
   def username = request.applicationContext.springSecurityService.principal?.username
   if (SpringSecurityUtils.isSwitched()){
       username = SpringSecurityUtils.switchedUserOriginalUsername+" AS "+username
   }
   return username
}

auditLog.defaultInclude = []     // can specify a list of included properties - all others are automatically excluded
auditLog.defaultExclude = []      // can specify a list of properties that are ignored by the audit log

// audit log persistence settings
auditLog.tablename = null        // custom AuditLog table name
auditLog.truncateLength = null   // can provide a maximum length for values in the audit log
```

Once a domain class is flagged with the static `auditable` property, the domain class is taken into account for
audit logging.

```groovy

class Person {
    static auditable = true

    String name
    String surName
}
```

On every insert or update operation, the Hibernate audit log plugin creates a new instance of its `AuditLogEvent`
domain class and stores it in the current transaction. A separate `AuditLogEvent` is created for every property change
the occurred to the target domain class.

```groovy
class AuditLogEvent {

    Date dateCreated

    String actor
    String uri
    String className
    String persistedObjectId

    String eventName
    String propertyName
    String oldValue
    String newValue

    // ...
}
```

The default name for the audit log event table is `audit_log` but can be changed with the `auditLog.tablename`
configuration property.

Without further configuration all domain class properties are eligible for audit logging. To narrowing down exact
 properties to include or exclude, the `auditable` property might specify a `Map`:

```groovy

class Person {
    static auditable = [ include: ['name'] ]  // exclusive include

    String name
    String surName
}
```

or

```groovy

class Person {
    static auditable = [ exclude: ['name'] ]  // inclusive exclude

    String name
    String surName
}
```