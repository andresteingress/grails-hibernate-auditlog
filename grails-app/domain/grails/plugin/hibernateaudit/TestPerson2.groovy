package grails.plugin.hibernateaudit

class TestPerson2 {

    static auditable = [include: 'name', exclude: 'surName']

    String name
    String surName

    static constraints = {
    }
}
