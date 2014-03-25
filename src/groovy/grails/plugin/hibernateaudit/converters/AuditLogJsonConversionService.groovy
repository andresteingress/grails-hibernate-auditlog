package grails.plugin.hibernateaudit.converters

import groovy.json.JsonOutput

/**
 * Converts data types to their String representation. This default implementation converts the given
 * object into a JSON-compliant String.
 */
class AuditLogJsonConversionService implements AuditLogConversionService {

    String convert(def object)  {
        def value = object

        if (value == null) return null
        if (value.hasProperty('id'))  {
            value = value.id
        }

        JsonOutput.toJson(value)
    }
}
