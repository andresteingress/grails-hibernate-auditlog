package org.grails.haudit

import org.grails.haudit.TestPerson
import org.grails.haudit.ClosureReader
import org.junit.Test

class ClosureReaderTests {

    @Test
    void readIgnoreList() {
        assert ['surName'] == ClosureReader.ignoreList(TestPerson.class)
    }

    @Test
    void readIncludeList() {
        assert ['name'] == ClosureReader.includeList(TestPerson.class)
    }

}
