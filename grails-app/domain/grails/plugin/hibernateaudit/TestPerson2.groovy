package grails.plugin.hibernateaudit

class TestPerson2 {

    static auditable = [
            logType: [insert: AuditLogType.FULL, update: AuditLogType.FULL],
            include: 'name',
            exclude: 'surName']

    String name
    String surName

    static constraints = {
    }
}
