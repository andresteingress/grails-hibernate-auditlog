package grails.plugin.hibernateaudit.domain

/**
 * An enumeration of various audit log types. An audit log type
 * specifies the amount of data to be logged.
 */
enum AuditLogType {

    SHORT,  // log only domain id + domain class + date created

    FULL,   // log everything including property changes, actor and URI

    NONE    // log disabled

}