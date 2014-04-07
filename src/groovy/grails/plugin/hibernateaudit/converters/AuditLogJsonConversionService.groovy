package grails.plugin.hibernateaudit.converters

import groovy.json.JsonOutput

/**
 * Converts data types to their String representation. This default implementation converts the given
 * object into a JSON-compliant String.
 */
class AuditLogJsonConversionService implements AuditLogConversionService {

    @Override
    String convert(Map<String, Object> object)  {
        def value = object
        if (value == null) return null

        JsonOutput.toJson(prepare(value))
    }

    @Override
    Object prepare(Map<String, Object> object) {
        return object.collectEntries { key, val ->
            if (val.hasProperty('id'))  {
                val = val.id
            }

            if (val instanceof Collection)  {
                if (val.isEmpty()) return val

                def firstObject = val.first()
                if (firstObject.hasProperty('id'))  {
                    val = val*.id.sort()
                }
            }

            [key, val]
        }
    }
}
