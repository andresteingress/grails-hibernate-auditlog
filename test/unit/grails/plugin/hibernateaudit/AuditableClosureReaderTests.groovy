package grails.plugin.hibernateaudit

import org.junit.Test

class AuditableClosureReaderTests {

    @Test
    void readIgnoreList() {
        assert ['surName'] == AuditableClosureReader.ignoreList(TestPerson.class)
    }

    @Test
    void readIncludeList() {
        assert ['name'] == AuditableClosureReader.includeList(TestPerson.class)
    }

}
