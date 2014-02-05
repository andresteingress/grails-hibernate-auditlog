package grails.plugin.hibernateaudit

import org.grails.datastore.mapping.reflect.ClassPropertyFetcher

/**
 * Looks up values from the static audible closure or Boolean value.
 */
class AuditableClosureReader {

    static final String PROPERTY_NAME = "auditable"

    static final String INCLUDE = "include"
    static final String EXCLUDE = "exclude"

    // audit log type local config options
    static final String INSERT_AUDIT_LOG_TYPE = "insertAuditLogType"
    static final String UPDATE_AUDIT_LOG_TYPE = "updateAuditLogType"
    static final String DELETE_AUDIT_LOG_TYPE = "deleteAuditLogType"

    static boolean isAuditable(Class<?> cls)  {
        if (cls == null) return false

        def clazz = ClassPropertyFetcher.forClass(cls)
        if (!clazz) return false

        def value = clazz.getPropertyValue(PROPERTY_NAME)
        return value == true || value instanceof Map
    }

    static AuditLogType insertAuditLogType(Class<?> cls, AuditLogType defaultValue = AuditLogType.FULL)  {
        return auditLogType(cls, INSERT_AUDIT_LOG_TYPE, defaultValue)
    }

    static AuditLogType updateAuditLogType(Class<?> cls, AuditLogType defaultValue = AuditLogType.FULL)  {
        return auditLogType(cls, UPDATE_AUDIT_LOG_TYPE, defaultValue)
    }

    static AuditLogType deleteAuditLogType(Class<?> cls, AuditLogType defaultValue = AuditLogType.FULL)  {
        return auditLogType(cls, DELETE_AUDIT_LOG_TYPE, defaultValue)
    }

    static AuditLogType auditLogType(Class<?> cls, String key, AuditLogType defaultValue)  {
        def auditableMap = getAuditableMapIfAvailable(cls) 
        if (!auditableMap) return defaultValue
 
        return (auditableMap[key] ?: defaultValue) as AuditLogType
    }

    static List<String> excludeList(Class<?> cls, List<String> defaultValues = []) {
        return lookupAuditableMapValue(cls, EXCLUDE, defaultValues)
    }

    static List<String> includeList(Class<?> cls, List<String> defaultValues = []) {
        return lookupAuditableMapValue(cls, INCLUDE, defaultValues)
    }

    protected static List<String> lookupAuditableMapValue(Class<?> cls, String key, List<String> defaultValue) {
        def auditableMap = getAuditableMapIfAvailable(cls)
        if (!auditableMap) return defaultValue
        def val = auditableMap[key] ?: defaultValue

        // wrap in a list if it's a single element
        if (!(val instanceof List)) {
            val = [val]
        }

        return val as List<String>
    }

    protected static Map getAuditableMapIfAvailable(Class<?> cls) {
        def clazz = ClassPropertyFetcher.forClass(cls)
        if (!clazz) return [:]

        def value = clazz.getPropertyValue(PROPERTY_NAME)
        value instanceof Map ? value as Map : [:]
    }
}
