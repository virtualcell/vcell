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

@Command(name = "test-report", description = "create test reports for a test suite")
public class TestReportCommand implements Callable<Integer> {

    private final static Logger logger = LogManager.getLogger(TestReportCommand.class);

    @Option(names = { "-t", "--test-cases" }, description = "[optional] test cases file - defaults to embedded test cases")
    private File testCasesNdjsonFile;

    @Option(names = { "-e", "--exec-summaries" }, required = true, description = "test results file e.g. exec_summary.ndjson")
    private File execSummaryNdjsonFile;

    @Option(names = { "-r", "--report" }, required = false, description = "filename for generated test report (e.g. ./report.md)")
    private File reportFile;

    // flag weather output should be json or yaml
    @Option(names = { "-j", "--json" }, description = "output report in json format")
    private boolean bJson = false;

    @Option(names = { "-d", "--debug" }, description = "enable debug logging")
    private boolean bDebug = false;

    public Integer call() {
        Level logLevel = bDebug ? Level.DEBUG : logger.getLevel(); 
        
        LoggerContext config = (LoggerContext)(LogManager.getContext(false));
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("org.vcell").getName()).setLevel(logLevel);
        config.getConfiguration().getLoggerConfig(LogManager.getLogger("cbit").getName()).setLevel(logLevel);
        config.updateLoggers();

        try {
            List<OmexTestCase> testCaseList = OmexTestingDatabase.loadOmexTestCases();
            // read fully text file into a string from file 'execSummaryNdjsonFile'
            String exec_summary_contents = Files.readString(execSummaryNdjsonFile.toPath());
            List<OmexExecSummary> execSummaries = OmexTestingDatabase.loadOmexExecSummaries(exec_summary_contents);
            OmexTestReport report = OmexTestingDatabase.generateReport(testCaseList, execSummaries);
            if (reportFile != null) {
                Files.writeString(reportFile.toPath(), bJson ? report.toJson() : report.toYaml());
            } else {
                System.out.println(bJson ? report.toJson() : report.toYaml());
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            logger.debug("Completed all exports");
        }
    }
}
