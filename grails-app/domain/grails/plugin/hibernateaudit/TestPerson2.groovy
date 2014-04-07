package grails.plugin.hibernateaudit

import grails.plugin.hibernateaudit.domain.AuditLogType

class TestPerson2 {

    static auditable = [
            insertAuditLogType: AuditLogType.FULL,
            updateAuditLogType: AuditLogType.FULL,
            deleteAuditLogType: AuditLogType.FULL,

            include: 'name',
            exclude: 'surName']

    String name
    String surName

    static constraints = {
    }
}
