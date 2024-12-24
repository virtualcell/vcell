package org.vcell.cli.testsupport;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class OmexTestReport {

    public static class OmexTestCaseChange {
        public final OmexTestCase original;
        public final OmexTestCase updated;

        public OmexTestCaseChange(OmexTestCase original, OmexTestCase updated) {
            this.original = original;
            this.updated = updated;
        }

    }

    public static class OmexTestStatistics {
        public int testCaseCount;
        public int testCaseFailCount;
        public int testCasePassCount;
        public int testCaseChangeCount;
        public int unmatchedTestCaseCount;
        public int unmatchedExecutionsCount;
        public int failedExecutionsCount;
        public int passedExecutionsCount;
        public int totalExecutions;
    }

    public final List<OmexTestCaseChange> testCaseChanges = new ArrayList<>();
    public final List<OmexExecSummary> unmatchedExecSummaries = new ArrayList<>();
    public final List<OmexTestCase> unmatchedTestCases = new ArrayList<>();
    public final OmexTestStatistics statistics = new OmexTestStatistics();

    public OmexTestReport(List<OmexTestCase> testCases, List<OmexExecSummary> execSummaries) {
        // find common path prefix of list of Path objects
        Path commonPrefix = PathUtils.findCommonPrefix(execSummaries.stream().map(tc -> Paths.get(tc.file_path)).toList());

        unmatchedTestCases.addAll(testCases);
        for (OmexExecSummary execSummary : execSummaries) {
            List<OmexTestCase> matchingTestCases = OmexTestingDatabase.queryOmexTestCase(testCases, Paths.get(execSummary.file_path), commonPrefix);
            if (matchingTestCases.isEmpty()) {
                System.out.println("No test case found for: " + execSummary.file_path);
                unmatchedExecSummaries.add(execSummary);
                continue;
            }
            if (matchingTestCases.size()>1){
                throw new RuntimeException("Multiple test cases "+matchingTestCases+" found for: " + execSummary.file_path);
            }
            OmexTestCase matchingTestCase = matchingTestCases.get(0);
            unmatchedTestCases.remove(matchingTestCase);
            OmexTestCase updatedTestCase = null;
            if (execSummary.status == OmexExecSummary.ActualStatus.PASSED){
                if (matchingTestCase.known_status == OmexTestCase.Status.FAIL || matchingTestCase.known_status == OmexTestCase.Status.SKIP || matchingTestCase.known_status == null) {
                    System.out.println("Test case marked as FAIL or SKIP, type="+matchingTestCase.known_failure_type+", but passed: " + execSummary.file_path);
                    updatedTestCase = new OmexTestCase(matchingTestCase.test_collection, matchingTestCase.file_path,
                                    matchingTestCase.should_fail, OmexTestCase.Status.PASS, null, null);
                }
            }
            if (execSummary.status == OmexExecSummary.ActualStatus.FAILED){
                if (matchingTestCase.known_status == OmexTestCase.Status.PASS || matchingTestCase.known_status == null) {
                    System.out.println("Test case marked as "+matchingTestCase.known_status+", but failed "+execSummary.failure_type+": "+execSummary.failure_desc+", "+execSummary.file_path);
                    updatedTestCase = new OmexTestCase(matchingTestCase.test_collection, matchingTestCase.file_path,
                            matchingTestCase.should_fail, OmexTestCase.Status.FAIL,
                            execSummary.failure_type, execSummary.failure_desc);
                } else if (matchingTestCase.known_status == OmexTestCase.Status.SKIP) {
                    System.out.println("Test case marked as SKIP and failed with "+execSummary.failure_type+": "+execSummary.failure_desc+", "+execSummary.file_path);
                    updatedTestCase = new OmexTestCase(matchingTestCase.test_collection, matchingTestCase.file_path,
                            matchingTestCase.should_fail, OmexTestCase.Status.SKIP,
                            execSummary.failure_type, execSummary.failure_desc);
                } else if (matchingTestCase.known_status == OmexTestCase.Status.FAIL) {
                    if (matchingTestCase.known_failure_type == null || !matchingTestCase.known_failure_type.equals(execSummary.failure_type)) {
                        System.out.println("Test case marked as FAIL with different type "+matchingTestCase.known_failure_type+", but failed with "+execSummary.failure_type+": "+execSummary.failure_desc+", "+execSummary.file_path);
                        updatedTestCase = new OmexTestCase(matchingTestCase.test_collection, matchingTestCase.file_path,
                                matchingTestCase.should_fail, OmexTestCase.Status.FAIL,
                                execSummary.failure_type, execSummary.failure_desc);
                    }
                }
            }
            if (updatedTestCase != null) {
                testCaseChanges.add(new OmexTestCaseChange(matchingTestCase, updatedTestCase));
            }
        }
        statistics.testCaseCount = testCases.size();
        statistics.testCasePassCount = (int) testCases.stream().filter(tc -> tc.known_status == OmexTestCase.Status.PASS).count();
        statistics.testCaseFailCount = (int) testCases.stream().filter(tc -> tc.known_status == OmexTestCase.Status.FAIL).count();
        statistics.totalExecutions = execSummaries.size();
        statistics.failedExecutionsCount = (int) execSummaries.stream().filter(s -> s.status == OmexExecSummary.ActualStatus.FAILED).count();
        statistics.passedExecutionsCount = (int) execSummaries.stream().filter(s -> s.status == OmexExecSummary.ActualStatus.PASSED).count();
        statistics.testCaseChangeCount = testCaseChanges.size();
        statistics.unmatchedTestCaseCount = unmatchedTestCases.size();
        statistics.unmatchedExecutionsCount = unmatchedExecSummaries.size();
    }

    // find test cases which were not run, they don't have execSummaries
    public List<OmexTestCase> findUnexecutedTestCases() {
        return unmatchedTestCases;
    }

    // find execSummaries which don't have matching test cases
    public List<OmexExecSummary> findUnmatchedExecSummaries() {
        return unmatchedExecSummaries;
    }

    // find test cases which should be updated
    public List<OmexTestCaseChange> findTestCaseChanges() {
        return testCaseChanges;
    }

    public OmexTestStatistics getStatistics() {
        return statistics;
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toYaml() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toMarkdown() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringBuilder sb = new StringBuilder();
        sb.append("# Test Report\n");
        int testCasesFailed = statistics.testCaseFailCount;
        int testCasesPassed = statistics.testCasePassCount;
        int testCasesUnknown = statistics.testCaseCount - testCasesFailed - testCasesPassed;
        sb.append("## Historical Test Case Records: "+statistics.testCaseCount+" (KNOWN PASSES = "+statistics.testCasePassCount+", KNOWN FAILURES = "+statistics.testCaseFailCount+", STATUS NOT RECORDED = "+testCasesUnknown).append(")\n");
        sb.append("## New Test Executions: "+statistics.totalExecutions+" (PASSES = "+statistics.passedExecutionsCount+", FAILURES = "+statistics.failedExecutionsCount).append(")\n");

        if (statistics.unmatchedTestCaseCount > 0) {
            sb.append("## Historical Test Case Records without matching Test Executions: ").append(statistics.unmatchedTestCaseCount).append("\n");
            int count = 0;
            for (OmexTestCase testCase : unmatchedTestCases) {
                sb.append(" - ").append(testCase.test_collection).append(" : ").append(testCase.file_path).append("\n");
                count++;
                if (count > 10) {
                    sb.append(" - ...").append("\n");
                    break;
                }
            }
        } else {
            sb.append("## All Historical Test Case Records have matching Test Executions\n");
        }

        if (statistics.unmatchedExecutionsCount > 0) {
            sb.append("## New Test Executions without matching Historical Test Case Records: ").append(statistics.unmatchedExecutionsCount).append("\n");
            int count = 0;
            for (OmexExecSummary execSummary : unmatchedExecSummaries) {
                sb.append(" - ").append(execSummary.file_path).append("\n");
                count++;
                if (count > 10) {
                    sb.append(" - ...").append("\n");
                    break;
                }
            }
        } else {
            sb.append("## All New Test Executions have matching Historical Test Case Records\n");
        }

        if (!testCaseChanges.isEmpty()) {
            sb.append("## New Test Executions which differ from Historical Test Case Records: ").append(statistics.testCaseChangeCount).append("\n");
            for (OmexTestCaseChange testCaseChange : testCaseChanges) {
                String diff = "(" + testCaseChange.original.known_status + ":" + testCaseChange.original.known_failure_type + ") -> (" + testCaseChange.updated.known_status + ":" + testCaseChange.updated.known_failure_type + ")";
                sb.append(" - ").append(testCaseChange.original.file_path).append(": " + diff + " ===New==> `").append(objectMapper.writeValueAsString(testCaseChange.updated)).append("`\n");
            }
        } else {
            sb.append("## No New Test Executions differ from Historical Test Case Records\n");
        }

        return sb.toString();
    }

}
