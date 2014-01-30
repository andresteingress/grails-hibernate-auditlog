package com.ariadne.auditlog

import com.ariadne.domain.TestPerson
import org.junit.Test

/**
 * @author andre
 */
class AuditClosureLookupTests {

    @Test
    void readIgnoreList() {
        assert ['surName'] == AuditClosureLookup.ignoreList(TestPerson.class)
    }

    @Test
    void readIncludeList() {
        assert ['name'] == AuditClosureLookup.includeList(TestPerson.class)
    }

}
