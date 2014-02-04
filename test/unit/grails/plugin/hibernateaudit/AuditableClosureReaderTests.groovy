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

    @Test
    void readIncludeListSingleElement() {
        assert ['name'] == AuditableClosureReader.includeList(TestPerson2.class)
    }

    @Test
    void readExcludeListSingleElement() {
        assert ['surName'] == AuditableClosureReader.excludeList(TestPerson2.class)
    }
}

