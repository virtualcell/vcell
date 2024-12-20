package org.vcell.cli.testsupport;


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

}
