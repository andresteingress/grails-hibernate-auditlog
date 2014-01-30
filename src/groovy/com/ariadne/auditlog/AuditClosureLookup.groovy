package com.ariadne.auditlog

import org.grails.datastore.mapping.reflect.ClassPropertyFetcher

/**
 * Handles looking up values from the static audible closure or Boolean value.
 */
class AuditClosureLookup {

    static final String PROPERTY_NAME = "auditable"

    static final String INCLUDE = "include"
    static final String IGNORE  = "ignore"

    static List<String> ignoreList(Class<?> cls, List<String> defaultValues = []) {
        return lookupAuditableMapValue(cls, IGNORE, defaultValues)
    }

    static List<String> includeList(Class<?> cls, List<String> defaultValues = []) {
        return lookupAuditableMapValue(cls, INCLUDE, defaultValues)
    }

    protected static <T> T lookupAuditableMapValue(Class<?> cls, String key, def defaultValue) {
        def auditableMap = getAuditableMapIfAvailable(cls)
        if (!auditableMap) return defaultValue
        return (auditableMap[key] ?: defaultValue) as T
    }

    protected static Map getAuditableMapIfAvailable(Class<?> cls) {
        def clazz = ClassPropertyFetcher.forClass(cls)
        if (!clazz) return [:]

        def value = clazz.getPropertyValue(PROPERTY_NAME)
        value instanceof Map ? value as Map : [:]
    }
}
