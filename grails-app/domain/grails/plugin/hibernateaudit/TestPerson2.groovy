package grails.plugin.hibernateaudit

import grails.plugin.hibernateaudit.domain.AuditLogType

class TestPerson2 {

    static auditable = [
            insertAuditLogType: AuditLogType.MEDIUM,
            updateAuditLogType: AuditLogType.MEDIUM,
            deleteAuditLogType: AuditLogType.MEDIUM,

            include: 'name',
            exclude: 'surName']

    String name
    String surName

    static constraints = {
    }
}
