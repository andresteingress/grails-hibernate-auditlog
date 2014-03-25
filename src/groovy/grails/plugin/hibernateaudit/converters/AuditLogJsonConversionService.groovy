package grails.plugin.hibernateaudit.converters

import groovy.json.JsonOutput

/**
 * Converts data types to their String representation. This default implementation converts the given
 * object into a JSON-compliant String.
 */
class AuditLogJsonConversionService implements AuditLogConversionService {

    @Override
    String convert(def object)  {
        def value = object
        if (value == null) return null

        JsonOutput.toJson(prepare(value))
    }

    @Override
    Object prepare(object) {
        def value = object
        if (value == null) return null

        if (value.hasProperty('id'))  {
            value = value.id
        }

        if (value instanceof Collection)  {
            if (value.isEmpty()) return value

            def firstObject = value.first()
            if (firstObject.hasProperty('id'))  {
                value = value*.id.sort()
            }
        }

        return value
    }
}
