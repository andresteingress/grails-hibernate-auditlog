package grails.plugin.hibernateaudit

class TestPerson4 {

    static auditable = true

    TestPerson5 testPerson5

    static constraints = {
    	testPerson5(nullable: true)
    }
}
