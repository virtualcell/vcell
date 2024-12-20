package org.vcell.cli.testsupport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        String test_cases_ndjson = """
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000039.omex","should_fail":false,"known_status":null,"known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000060.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000080.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000100.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000120.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000140.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000160.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000180.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000200.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000220.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000240.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000260.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000280.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000300.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000320.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000340.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000360.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000380.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000400.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000420.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000421.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000440.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000460.omex","should_fail":false,"known_status":null,"known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000480.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000500.omex","should_fail":false,"known_status":null,"known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000520.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000540.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000560.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000580.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000600.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000620.omex","should_fail":false,"known_status":null,"known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000640.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000660.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000680.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000700.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000720.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000740.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000760.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000780.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000800.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000820.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000840.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000860.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000880.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000900.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000920.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000940.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000960.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000000980.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000001000.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000001020.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000001040.omex","should_fail":false,"known_status":"FAIL","known_failure_type":"UNCATETORIZED_FAULT","known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000001060.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                {"test_collection":"SYSBIO_BIOMD","file_path":"BIOMD0000001080.omex","should_fail":false,"known_status":"PASS","known_failure_type":null,"known_failure_desc":null}
                                
                """;
        String exec_summary_ndjson = """
                {  "file_path": "/root/BIOMD0000000020.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000040.omex",  "status": "FAILED",  "failure_type": "SBML_IMPORT_FAILURE",  "failure_desc": "Unable to initialize bioModel for the given selection: Failed to translate SBML model into BioModel: org.vcell.sbml.vcell.SBMLImportException: Non-numeric stoichiometry ('f' for product 'Br' in reaction 'Reaction5') not handled in VCell at this time. org.vcell.sbml.vcell.SBMLImportException: Failed to translate SBML model into BioModel: org.vcell.sbml.vcell.SBMLImportException: Non-numeric stoichiometry ('f' for product 'Br' in reaction 'Reaction5') not handled in VCell at this time." }
                {  "file_path": "/root/BIOMD0000000060.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000080.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000100.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000120.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000140.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000240.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000260.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000280.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000300.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000320.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000340.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000360.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000380.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000400.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000420.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000440.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000460.omex",  "status": "FAILED",  "failure_type": "SBML_IMPORT_FAILURE",  "failure_desc": "Unable to initialize bioModel for the given selection: couldn't find SBase with sid=null in SBMLSymbolMapping org.vcell.sbml.vcell.SBMLImportException: couldn't find SBase with sid=null in SBMLSymbolMapping" }
                {  "file_path": "/root/BIOMD0000000480.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000500.omex",  "status": "FAILED",  "failure_type": "UNCATETORIZED_FAULT",  "failure_desc": "Unable to initialize bioModel for the given selection: Cannot invoke \\"cbit.vcell.solver.Simulation.getImportedTaskID()\\" because \\"simulation\\" is null java.lang.NullPointerException: Cannot invoke \\"cbit.vcell.solver.Simulation.getImportedTaskID()\\" because \\"simulation\\" is null" }
                {  "file_path": "/root/BIOMD0000000520.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000540.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000560.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000580.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000600.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000620.omex",  "status": "FAILED",  "failure_type": "UNCATETORIZED_FAULT",  "failure_desc": "Failed execution: Model 'BIOMD0000000620_BIOMD0000000620_url.sedml_BIOMD0000000620_url' Task 'task1'. java.lang.RuntimeException: Could not execute code: CVODE solver failed : at time 5.5, discontinuity (t == Anakinra_dose_counter) evaluated to TRUE, solver assumed FALSE\\n\\n\\n\\n(/usr/local/app/vcell/installDir/localsolvers/linux64/SundialsSolverStandalone_x64 /tmp/VCell_CLI_193e0a5db7a967308657490923326/BIOMD0000000620/BIOMD0000000620_url.sedml/SimID_411786192_0_.cvodeInput /tmp/VCell_CLI_193e0a5db7a967308657490923326/BIOMD0000000620/BIOMD0000000620_url.sedml/SimID_411786192_0_.ida) " }
                {  "file_path": "/root/BIOMD0000000640.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000660.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000680.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000700.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000720.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000740.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000760.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000780.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000800.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000820.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000840.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000860.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000880.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000900.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000920.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000940.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000960.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000000980.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000001000.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000001020.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000001040.omex",  "status": "FAILED",  "failure_type": "SBML_IMPORT_FAILURE",  "failure_desc": "Unable to initialize bioModel for the given selection: Failed to translate SBML model into BioModel: Error binding global parameter 'Summary_flux_to_RBC' to model: 'Vin' is either not found in your model or is not allowed to be used in the current context. Check that you have provided the correct and full name (e.g. Ca_Cytosol). org.vcell.sbml.vcell.SBMLImportException: Failed to translate SBML model into BioModel: Error binding global parameter 'Summary_flux_to_RBC' to model: 'Vin' is either not found in your model or is not allowed to be used in the current context. Check that you have provided the correct and full name (e.g. Ca_Cytosol)." }
                {  "file_path": "/root/BIOMD0000001060.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                {  "file_path": "/root/BIOMD0000001080.omex",  "status": "PASSED",  "failure_type": null,  "failure_desc": null }
                """;

        List<OmexTestCase> testCases = OmexTestingDatabase.parseOmexTestCases(test_cases_ndjson);
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
    }

}
