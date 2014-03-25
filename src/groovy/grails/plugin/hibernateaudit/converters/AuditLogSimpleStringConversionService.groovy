package grails.plugin.hibernateaudit.converters

/**
 * Converts data types to their String representation. This default implementation converts the given
 * object into its String representation.
 */
class AuditLogSimpleStringConversionService implements AuditLogConversionService {

    String convert(def object)  {
        def value = object
        if (value == null) return null

        prepare(value)
    }

    @Override
    Object prepare(object) {
        def value = object
        if (value == null) return null

        if (value.hasProperty('id'))  {
            value = value.id
        }

        return value
    }
}
