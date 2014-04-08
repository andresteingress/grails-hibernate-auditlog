package grails.plugin.hibernateaudit

class Tester2 {

    static auditable = true

    String name
    String surName

    Date birthDate

    static constraints = {
    }
}
