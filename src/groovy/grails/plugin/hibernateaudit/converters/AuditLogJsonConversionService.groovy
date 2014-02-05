package grails.plugin.hibernateaudit.converters

import groovy.json.JsonOutput

/**
 * Converts data types to their String representation. This default implementation converts the given
 * object into a JSON-compliant String.
 */
class AuditLogJsonConversionService implements AuditLogConversionService {

    String convert(def object)  {
        if (object == null) return null

        JsonOutput.toJson(object)
    }
}
