package grails.plugin.hibernateaudit

/**
 * An enumeration of various audit log types. An audit log type
 * specifies the amount of data to be logged.
 */
enum AuditLogType {

    SHORT,  // log only domain id + domain class + date created

    MEDIUM, // log including property values changes without actor and URI

    FULL,   // log everything including property changes, actor and URI

    NONE    // log disabled

}