package grails.plugin.hibernateaudit

class Person {

    static auditable = true

    String name
    String surName

    static constraints = {
    }
}
