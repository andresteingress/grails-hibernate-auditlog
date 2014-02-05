package grails.plugin.hibernateaudit

class TestPerson3 {

    static auditable = [
            insertAuditLogType: AuditLogType.SHORT,
            updateAuditLogType: AuditLogType.SHORT,
            deleteAuditLogType: AuditLogType.SHORT,

            include: 'name']

    String name
    String surName

    static constraints = {
    }
}
