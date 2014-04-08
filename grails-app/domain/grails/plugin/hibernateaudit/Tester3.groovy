package grails.plugin.hibernateaudit

class Tester3 {

    static auditable = true

    String name
    String surName

    BigDecimal money

    static constraints = {
    }
}
