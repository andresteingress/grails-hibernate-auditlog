package grails.plugin.hibernateaudit

import org.junit.Test

class AuditableClosureReaderTests {

    @Test
    void readExcludeList() {
        assert ['surName'] == AuditableClosureReader.excludeList(TestPerson.class)
    }

    @Test
    void readIncludeList() {
        assert ['name'] == AuditableClosureReader.includeList(TestPerson.class)
    }

}
