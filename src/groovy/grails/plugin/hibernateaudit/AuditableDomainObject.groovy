package grails.plugin.hibernateaudit

import grails.util.Holders
import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.commons.GrailsDomainClass

/**
 * Encapsulates a domain object and can be used to convert it into a map.
 */
@Log4j
class AuditableDomainObject {

    final AuditLogListener logListener
    final def domain
    final GrailsDomainClass domainClass

    final Collection<String> properties

    protected AuditableDomainObject(AuditLogListener logListener, def domain)  {
        this.logListener = logListener
        this.domain = domain
        this.domainClass = Holders.grailsApplication.getDomainClass(domain.class.name) as GrailsDomainClass
        this.properties = domainClass.persistentProperties*.name
    }

    private Collection<String> filterProperties(Collection<String> properties, domain) {
        // remove all ignored properties
        properties.removeAll(AuditableClosureReader.ignoreList(domain.class, logListener.defaultIgnoreList))

        if (logListener.defaultIncludeList)  {
            // intersect with included properties
            properties = properties.intersect(AuditableClosureReader.includeList(domain.class, logListener.defaultIncludeList))
        }

        properties
    }

    Map<String, Object> toMap() {
        toMap(properties)
    }

    Map<String, Object> toMap(Collection<String> propertyNames) {
        filterProperties(propertyNames, domain).collectEntries { [it, domain."$it"] }
    }

    Map<String, Object> toPersistentValueMap(Collection<String> propertyNames) {
        filterProperties(propertyNames, domain).collectEntries { [it, domain.getPersistentValue(it)] }
    }

    def getId() {
        def identifier = domainClass.getIdentifier()
        return domain."${identifier.name}"
    }

    String getClassName() {
        return domainClass.shortName
    }

    List<String> getDirtyPropertyNames() {
        return domain.dirtyPropertyNames
    }
}
