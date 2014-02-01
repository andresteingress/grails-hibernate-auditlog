package grails.plugin.hibernateaudit

class TestPerson {

    static auditable = [exclude: ['surName'], include: ['name']]

    String name
    String surName

    static constraints = {
    }
}
