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
        assert JsonOutput.toJson(date) == auditLogJsonConversionService.convert(date)
    }

    @Test
    void stringJsonString() {
        def s = "Max"
        assert '"Max"' == auditLogJsonConversionService.convert(s)
    }

    enum TestEnum { ONE, TWO, THREE }

    @Test
    void enumerationValueToString() {
        def test = TestEnum.TWO
        assert '"TWO"' == auditLogJsonConversionService.convert(test)
    }
}
