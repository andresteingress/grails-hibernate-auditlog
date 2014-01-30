import com.ariadne.auditlog.AuditLog
import org.codehaus.groovy.grails.commons.GrailsApplication

class BootStrap {

    GrailsApplication grailsApplication

    def init = { servletContext ->
        AuditLog.bootstrap grailsApplication
    }

    void activateAuditLog() {

    }

    def destroy = {
    }
}
