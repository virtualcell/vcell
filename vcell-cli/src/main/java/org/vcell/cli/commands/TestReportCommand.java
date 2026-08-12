package org.vcell.cli.commands;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.vcell.sedml.testsupport.OmexExecSummary;
import org.vcell.sedml.testsupport.OmexTestCase;
import org.vcell.sedml.testsupport.OmexTestReport;
import org.vcell.sedml.testsupport.OmexTestingDatabase;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

@Command(name = "test-report", description = "create test reports for a test suite")
public class TestReportCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(TestReportCommand.class);

    @Option(names = { "-t", "--test-cases" }, required = false, description = "[optional] test cases file - defaults to embedded test cases")
    private File testCasesNdjsonFile;

    @Option(names = { "-e", "--exec-summaries" }, required = true, description = "test results file e.g. exec_summary.ndjson")
    private File execSummaryNdjsonFile;

    // list of OmexTestingDatabase.TestCollection objects to include
    @Option(names = { "-c", "--collections" }, required = true, description = "list of test collections to include")
    private List<OmexTestingDatabase.TestCollection> collections;

    @Option(names = { "--report-md" }, required = true, description = "filename for generated markdown test report (e.g. ./report.md)")
    private File reportFile_md;

    @Option(names = { "--report-json" }, required = false, description = "filename for generated json test report (e.g. ./report.json)")
    private File reportFile_json;

    @Option(names = { "--update-test-cases" }, required = false, description = "[optional] write the test cases file the current run implies, for review as a diff against the documented baseline")
    private File updatedTestCasesFile = null;

    @Option(names = { "--fail-on-change" }, description = "exit non-zero if any result differs from the documented baseline (changed, stopped running, or undocumented)")
    private boolean bFailOnChange = false;

    @Option(names = { "--summary-file" }, required = false, description = "[optional] write a one-line PASS/FAIL summary, for a chat notification")
    private File summaryFile = null;

    @Option(names = { "-d", "--debug" }, description = "enable debug logging")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            // read test cases; keep the full set as well as the filtered one, so that
            // --update-test-cases can rewrite the whole file and stay diffable against the
            // committed baseline instead of dropping the collections this run did not cover
            Predicate<OmexTestCase> omexTestCasePredicate = tc -> collections.contains(tc.test_collection);
            final List<OmexTestCase> allTestCases;
            if (testCasesNdjsonFile != null) {
                String test_cases_contents = Files.readString(testCasesNdjsonFile.toPath());
                allTestCases = OmexTestingDatabase.parseOmexTestCases(test_cases_contents);
            } else {
                // if file not specified, use embedded test cases
                allTestCases = OmexTestingDatabase.loadOmexTestCases();
            }
            final List<OmexTestCase> testCaseList = allTestCases.stream().filter(omexTestCasePredicate).toList();

            // read exec summaries
            String exec_summary_contents = Files.readString(execSummaryNdjsonFile.toPath());
            List<OmexExecSummary> execSummaries = OmexTestingDatabase.loadOmexExecSummaries(exec_summary_contents);

            // generate report
            OmexTestReport report = OmexTestingDatabase.generateReport(testCaseList, execSummaries);
            if (reportFile_json != null) {
                Files.writeString(reportFile_json.toPath(), report.toJson());
            }
            if (reportFile_md != null) {
                Files.writeString(reportFile_md.toPath(), report.toMarkdown());
            }
            if (updatedTestCasesFile != null) {
                // the whole collection, not just the changes, so this is diffable against the committed file
                Files.writeString(updatedTestCasesFile.toPath(),
                        OmexTestingDatabase.writeOmexTestCases(report.applyChangesTo(allTestCases)));
            }

            final OmexTestReport.Regressions regressions = report.findRegressions();
            final String summary = (regressions.isEmpty() ? "PASS" : "FAIL") + ": " + regressions.summary();
            if (summaryFile != null) {
                Files.writeString(summaryFile.toPath(), summary + "\n");
            }
            logger.info(summary);

            if (bFailOnChange && !regressions.isEmpty()) {
                // named individually: "3 changed" and "11 stopped running" call for different responses
                for (OmexTestReport.OmexTestCaseChange change : regressions.changed) {
                    logger.error("changed: " + change.original.file_path
                            + ": documented " + change.original.known_status + "/" + change.original.known_failure_type
                            + ", observed " + change.updated.known_status + "/" + change.updated.known_failure_type);
                }
                for (OmexTestCase testCase : regressions.disappeared) {
                    logger.error("stopped running: " + testCase.file_path
                            + " (documented " + testCase.known_status + ")");
                }
                for (OmexExecSummary execSummary : regressions.undocumented) {
                    logger.error("undocumented: " + execSummary.file_path
                            + " (observed " + execSummary.status + ")");
                }
                logger.error("accept these results by committing the file written with --update-test-cases");
                return 1;
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            logger.debug("Completed all exports");
        }
    }
}
