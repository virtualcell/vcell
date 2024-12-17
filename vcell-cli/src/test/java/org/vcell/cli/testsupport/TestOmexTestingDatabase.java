package org.vcell.cli.testsupport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;


public class TestOmexTestingDatabase {

    @Test
    void omexTestCasesLoad() throws IOException {
        List<OmexTestCase> allTestCases = OmexTestingDatabase.loadOmexTestCases();
        Assertions.assertFalse(allTestCases.isEmpty());
    }

    @Test
    void execSummariesLoad() throws IOException {
        String omexExecSummaryJson =
        """
        {"file_path": "test1.omex", "status": "PASSED"}
        {"file_path": "test2.omex", "status": "PASSED", "failure_type": null, "failure_desc": null}
        {"file_path": "aaa.omex", "status": "FAILED", "failure_type": "SBML_IMPORT_FAILURE", "failure_desc": "bad stuff"}
        """;

        List<OmexExecSummary> allExecSummaries = OmexTestingDatabase.loadOmexExecSummaries(omexExecSummaryJson);
        Assertions.assertEquals(3, allExecSummaries.size());
    }

    @Test
    void queryOmexTestCase() throws IOException {
        String[] shouldFindUnique = {
                "sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/2.execution-should-succeed.omex",
                "bsts-omex/misc-projects/BIOMD0000000842.omex",
                "omex_files/BIOMD0000000842.omex"
        };
        String[] shouldFindMany = {
                "11.omex",
                "BIOMD0000000842.omex",
                "2.execution-should-succeed.omex"
        };
        String[] shouldNotFind = {
                "11111.omex",
                "2.execution-should-succeed",
                "sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges"
        };
        List<OmexTestCase> allTestCases = OmexTestingDatabase.loadOmexTestCases();
        for (String path : shouldFindUnique) {
            List<OmexTestCase> omexTestCase = OmexTestingDatabase.queryOmexTestCase(allTestCases, path);
            Assertions.assertEquals(1, omexTestCase.size());
        }
        for (String path : shouldFindMany) {
            List<OmexTestCase> omexTestCase = OmexTestingDatabase.queryOmexTestCase(allTestCases, path);
            Assertions.assertTrue(omexTestCase.size() > 1);
        }
        for (String path : shouldNotFind) {
            List<OmexTestCase> omexTestCase = OmexTestingDatabase.queryOmexTestCase(allTestCases, path);
            Assertions.assertTrue(omexTestCase.isEmpty());
        }
    }

}
