package grails.plugin.hibernateaudit

class TestPerson {

    static auditable = [ignore: ['surName'], include: ['name']]

    String name
    String surName

    static constraints = {
    }
}
