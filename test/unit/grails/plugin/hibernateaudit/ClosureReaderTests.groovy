package grails.plugin.hibernateaudit

import org.grails.haudit.TestPerson
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
