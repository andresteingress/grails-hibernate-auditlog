package grails.plugin.hibernateaudit

class TestPerson6 {

    static auditable = true

    static hasMany = [testPerson5 : TestPerson5]
}
