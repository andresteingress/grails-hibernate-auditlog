package grails.plugin.hibernateaudit.reflect

import grails.plugin.hibernateaudit.domain.AuditLogType
import grails.plugin.hibernateaudit.TestPerson
import grails.plugin.hibernateaudit.TestPerson2
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

    @Test
    void readInsertAuditLogType() {
        assert AuditLogType.FULL == AuditableClosureReader.insertAuditLogType(TestPerson2.class)
    }

    @Test
    void readUpdateAuditLogType() {
        assert AuditLogType.FULL == AuditableClosureReader.updateAuditLogType(TestPerson2.class)
    }

    @Test
    void readDeleteAuditLogType() {
        assert AuditLogType.FULL == AuditableClosureReader.deleteAuditLogType(TestPerson2.class)
    }
}

