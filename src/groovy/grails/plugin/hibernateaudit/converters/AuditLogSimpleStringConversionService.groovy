package grails.plugin.hibernateaudit.converters
/**
 * Converts data types to their String representation. This default implementation converts the given
 * object into its String representation.
 */
class AuditLogSimpleStringConversionService implements AuditLogConversionService {

    String convert(def object)  {
        if (object == null) return null

        object as String
    }
}
