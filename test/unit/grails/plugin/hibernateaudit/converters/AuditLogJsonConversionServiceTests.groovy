package grails.plugin.hibernateaudit.converters

import groovy.json.JsonOutput
import org.junit.Before
import org.junit.Test

class AuditLogJsonConversionServiceTests {

    AuditLogJsonConversionService auditLogJsonConversionService

    @Before
    void setUp() {
        auditLogJsonConversionService = new AuditLogJsonConversionService()
    }

    @Test
    void dateToJsonString() {
        def date = new Date()
        assert JsonOutput.toJson([date: date]) == auditLogJsonConversionService.convert([date: date])
    }

    @Test
    void stringJsonString() {
        def s = "Max"
        assert '{"s":"Max"}' == auditLogJsonConversionService.convert([s: s])
    }

    enum TestEnum { ONE, TWO, THREE }

    @Test
    void enumerationValueToString() {
        def test = TestEnum.TWO
        assert '{"test":"TWO"}' == auditLogJsonConversionService.convert([test: test])
    }
}
