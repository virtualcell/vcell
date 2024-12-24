package org.vcell.cli.testsupport;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;


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
        List<Path> shouldFindUnique = List.of(
                Paths.get("sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/2.execution-should-succeed.omex"),
                Paths.get("bsts-omex/misc-projects/BIOMD0000000842.omex"),
                Paths.get("omex_files/BIOMD0000000842.omex")
        );
        List<Path> shouldFindMany = List.of(
                Paths.get("/root/BIOMD0000000842.omex"),
                Paths.get("/root/2.execution-should-succeed.omex")
        );
        List<Path> shouldNotFind = List.of(
                Paths.get("11111.omex"),
                Paths.get("2.execution-should-succeed"),
                Paths.get("sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges")
        );
        List<OmexTestCase> allTestCases = OmexTestingDatabase.loadOmexTestCases();
        for (Path path : shouldFindUnique) {
            List<OmexTestCase> omexTestCase = OmexTestingDatabase.queryOmexTestCase(allTestCases, path, PathUtils.findCommonPrefix(shouldFindUnique));
            Assertions.assertEquals(1, omexTestCase.size());
        }
        for (Path path : shouldFindMany) {
            List<OmexTestCase> omexTestCase = OmexTestingDatabase.queryOmexTestCase(allTestCases, path, PathUtils.findCommonPrefix(shouldFindMany));
            Assertions.assertTrue(omexTestCase.size() > 1);
        }
        for (Path path : shouldNotFind) {
            List<OmexTestCase> omexTestCase = OmexTestingDatabase.queryOmexTestCase(allTestCases, path, PathUtils.findCommonPrefix(shouldNotFind));
            Assertions.assertTrue(omexTestCase.isEmpty());
        }
    }

    // test generateReport
    @Test
    void generateReport() throws IOException {
        // read the line delimited json file in resources path /org/vcell/cli/testsupport/fake_test_cases.ndjson into a String
        String test_cases_ndjson = new String(getClass().getResourceAsStream("/org/vcell/cli/testsupport/fake_test_cases.ndjson").readAllBytes());

        String exec_summary_ndjson = new String(getClass().getResourceAsStream("/org/vcell/cli/testsupport/fake_exec_summaries.ndjson").readAllBytes());

        List<OmexTestCase> testCases = OmexTestingDatabase.parseOmexTestCases(test_cases_ndjson);
        OmexTestingDatabase.TestCollection testCollection = OmexTestingDatabase.TestCollection.SYSBIO_BIOMD;
        testCases = testCases.stream().filter(c -> c.test_collection == testCollection).toList();
        List<OmexExecSummary> omexExecSummaries = OmexTestingDatabase.loadOmexExecSummaries(exec_summary_ndjson);
        OmexTestReport report = OmexTestingDatabase.generateReport(testCases, omexExecSummaries);
        Assertions.assertEquals(50, report.getStatistics().totalExecutions);
        Assertions.assertEquals(54, report.getStatistics().testCaseCount);
        Assertions.assertEquals(5, report.getStatistics().failedExecutionsCount);
        Assertions.assertEquals(45, report.getStatistics().passedExecutionsCount);
        Assertions.assertEquals(2, report.getStatistics().unmatchedExecutionsCount);
        Assertions.assertEquals(6, report.getStatistics().unmatchedTestCaseCount);
        Assertions.assertEquals(4, report.getStatistics().testCaseChangeCount);
        Assertions.assertEquals(2, report.unmatchedExecSummaries.size());
        Assertions.assertEquals(6, report.unmatchedTestCases.size());
        Assertions.assertEquals(4, report.testCaseChanges.size());

        String reportYaml = report.toYaml();
        System.out.println(reportYaml);

        String reportMarkdown = report.toMarkdown();
        System.out.println(reportMarkdown);
    }

}
