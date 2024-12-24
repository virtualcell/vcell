package org.vcell.cli.testsupport;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
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

    @Option(names = { "-d", "--debug" }, description = "enable debug logging")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            // read test cases and filter by collections
            Predicate<OmexTestCase> omexTestCasePredicate = tc -> collections.contains(tc.test_collection);
            final List<OmexTestCase> testCaseList;
            if (testCasesNdjsonFile != null) {
                String test_cases_contents = Files.readString(testCasesNdjsonFile.toPath());
                testCaseList = OmexTestingDatabase.parseOmexTestCases(test_cases_contents).stream().filter(omexTestCasePredicate).toList();
            } else {
                // if file not specified, use embedded test cases
                testCaseList = OmexTestingDatabase.loadOmexTestCases().stream().filter(omexTestCasePredicate).toList();
            }

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
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            logger.debug("Completed all exports");
        }
    }
}
