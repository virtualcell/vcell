package org.vcell.sedml.testsupport;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Pair;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class OmexTestReport {
    private final static Logger logger = LogManager.getLogger(OmexTestReport.class);

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
    public final Map<FailureType, Integer> historicalFailureTypeCounts = new HashMap<>();
    public final Map<FailureType, Integer> currentFailureTypeCounts = new HashMap<>();

    public OmexTestReport(List<OmexTestCase> testCases, List<OmexExecSummary> execSummaries) {
        // find common path prefix of list of Path objects
        Path commonPrefix = PathUtils.findCommonPrefix(execSummaries.stream().map(tc -> Paths.get(tc.file_path)).toList());

        unmatchedTestCases.addAll(testCases);

        for (OmexTestCase testCase : testCases) {
            if (testCase.known_status != OmexTestCase.Status.FAIL) {
                continue;
            }

            this.historicalFailureTypeCounts.put(testCase.known_failure_type, this.historicalFailureTypeCounts.getOrDefault(testCase.known_failure_type, 0) + 1);
        }

        for (OmexExecSummary execSummary : execSummaries) {
            // Collect Failure Types
            if (execSummary.status == OmexExecSummary.ActualStatus.FAILED){
                this.currentFailureTypeCounts.put(execSummary.failure_type , this.currentFailureTypeCounts.getOrDefault(execSummary.failure_type , 0) + 1);
            }

            // find matching test case
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

            // Compare Historical to Current
            OmexTestCase updatedTestCase = null;
            if (execSummary.status == OmexExecSummary.ActualStatus.PASSED){
                if (matchingTestCase.known_status == OmexTestCase.Status.FAIL || matchingTestCase.known_status == OmexTestCase.Status.SKIP || matchingTestCase.known_status == null) {
                    System.out.println("Test case marked as FAIL or SKIP, type="+matchingTestCase.known_failure_type+", but passed: " + execSummary.file_path);
                    updatedTestCase = new OmexTestCase(matchingTestCase.test_collection, matchingTestCase.file_path,
                                    matchingTestCase.should_fail, OmexTestCase.Status.PASS, null, null);
                }
            }
            if (execSummary.status == OmexExecSummary.ActualStatus.FAILED){ // Includes things we should skip
                if (matchingTestCase.known_status == OmexTestCase.Status.PASS || matchingTestCase.known_status == null) {
                    System.out.println("Test case marked as "+matchingTestCase.known_status+", but failed "+execSummary.failure_type+": "+execSummary.failure_desc+", "+execSummary.file_path);
                    updatedTestCase = new OmexTestCase(matchingTestCase.test_collection, matchingTestCase.file_path,
                            matchingTestCase.should_fail, OmexTestCase.Status.FAIL,
                            execSummary.failure_type, execSummary.failure_desc);
                } else if (matchingTestCase.known_status == OmexTestCase.Status.SKIP) { // If we set to skip, and it failed as expected, we're fine!
                    boolean multipleFailuresAllowed = matchingTestCase.known_failure_type == FailureType.UNSTABLE_EXECUTION;
                    if (multipleFailuresAllowed && matchingTestCase.known_failure_desc != null && !matchingTestCase.known_failure_desc.contains(execSummary.failure_type.name())){
                        System.out.println("Test case marked as `UNSTABLE_EXECUTION` failed with new type of failure ("+execSummary.failure_type+": "+execSummary.failure_desc+", "+execSummary.file_path+")!!");
                        updatedTestCase = new OmexTestCase(matchingTestCase.test_collection, matchingTestCase.file_path,
                                matchingTestCase.should_fail, OmexTestCase.Status.SKIP,
                                execSummary.failure_type, execSummary.failure_desc);
                    } else if (!multipleFailuresAllowed && execSummary.failure_type != matchingTestCase.known_failure_type) {
                        System.out.println("Test case marked as SKIP ("+matchingTestCase.known_failure_type+") but failed unexpectedly with "+execSummary.failure_type+": "+execSummary.failure_desc+", "+execSummary.file_path);
                        updatedTestCase = new OmexTestCase(matchingTestCase.test_collection, matchingTestCase.file_path,
                                matchingTestCase.should_fail, OmexTestCase.Status.SKIP,
                                execSummary.failure_type, execSummary.failure_desc);
                    }
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

    public String toMarkdown() {
        MarkdownTable resultsStatistics = OmexTestReport.generateResultStatistics(this.statistics);
        MarkdownTable errorTypeStatistics = OmexTestReport.generateErrorTypeStatistics(this.historicalFailureTypeCounts, this.currentFailureTypeCounts);
        MarkdownTable incomparableStatistics = OmexTestReport.generateIncomparableStatistics(this.statistics, this.unmatchedTestCases, this.unmatchedExecSummaries);
        MarkdownTable unmatchedResultStatistics = OmexTestReport.generateUnmatchedResultStatistics(this.testCaseChanges);

        StringBuilder sb = new StringBuilder();
        sb.append("# Test Report\n");
        sb.append("## Results Statistics:\n");
        sb.append(resultsStatistics.getMarkdownTable()).append("\n");
        sb.append("## Error Type Statistics:\n");
        sb.append(errorTypeStatistics.getMarkdownTable()).append("\n");

        if (this.statistics.unmatchedExecutionsCount > 0 || this.statistics.unmatchedTestCaseCount > 0 )
            sb.append("## Incomparable Results Statistics:\n").append(incomparableStatistics.getMarkdownTable()).append("\n");
        if (!this.testCaseChanges.isEmpty())
            sb.append("## Unmatched Results Statistics:\n").append(unmatchedResultStatistics.getMarkdownTable()).append("\n");
        return sb.toString();
    }

    private static MarkdownTable generateResultStatistics(OmexTestStatistics statistics){
        MarkdownTable resultStatistics = new MarkdownTable();
        resultStatistics.resizeRowAndColumnLabels(4, 2);
        resultStatistics.setColumnTitle(0, "Historical");
        resultStatistics.setColumnTitle(1, "Current");
        resultStatistics.setRowTitle(0, "Passes");
        resultStatistics.setRowTitle(1, "Failures");
        resultStatistics.setRowTitle(2, "Unknown");
        resultStatistics.setRowTitle(3, "Total");
        resultStatistics.setTableValue(0,0, statistics.testCasePassCount);
        resultStatistics.setTableValue(1,0, statistics.testCaseFailCount);
        resultStatistics.setTableValue(2,0, statistics.testCaseCount - (statistics.testCaseFailCount + statistics.testCasePassCount));
        resultStatistics.setTableValue(3,0, statistics.testCaseCount);
        resultStatistics.setTableValue(0,1, statistics.passedExecutionsCount);
        resultStatistics.setTableValue(1,1, statistics.failedExecutionsCount);
        resultStatistics.setTableValue(2,1, "---");
        resultStatistics.setTableValue(3,1, statistics.totalExecutions);
        return resultStatistics;
    }

    private static MarkdownTable generateErrorTypeStatistics(Map<FailureType, Integer> historicalFailures,
                                                             Map<FailureType, Integer> currentFailures){
        MarkdownTable errorTypeStatistics = new MarkdownTable();
        List<Pair<FailureType, Pair<Integer, Integer>>> failureTypeList = OmexTestReport.getFailureTypePairings(historicalFailures, currentFailures);

        errorTypeStatistics.resizeRowAndColumnLabels(failureTypeList.size(), 3);
        errorTypeStatistics.setColumnTitle(0, "Historical");
        errorTypeStatistics.setColumnTitle(1, "Current");
        errorTypeStatistics.setColumnTitle(2, "Change");
        for (int i = 0; i < failureTypeList.size(); i++){
            Pair<FailureType, Pair<Integer, Integer>> pairing = failureTypeList.get(i);
            String failureTypeString = pairing.one == null ? "SKIPPED" : pairing.one.toString();
            errorTypeStatistics.setRowTitle(i, failureTypeString);

            errorTypeStatistics.setTableValue(i, 0, pairing.two.one);
            errorTypeStatistics.setTableValue(i, 1, pairing.two.two);
            int comparison = pairing.two.two - pairing.two.one;
            errorTypeStatistics.setTableValue(i, 2, (comparison > 0 ? "↑" : (comparison < 0 ? "↓" : "-")));
        }
        return errorTypeStatistics;
    }

    private static List<Pair<FailureType, Pair<Integer, Integer>>> getFailureTypePairings(Map<FailureType, Integer> historicalFailures,
                                                                                          Map<FailureType, Integer> currentFailures){
        Set<FailureType> encounteredErrorTypes = new HashSet<>(historicalFailures.keySet());
        encounteredErrorTypes.addAll(currentFailures.keySet());
        List<Pair<FailureType, Pair<Integer, Integer>>> failureTypeList = new ArrayList<>();
        for (FailureType type : FailureType.values()){ // We want to
            if (!encounteredErrorTypes.contains(type)) continue;
            Pair<Integer, Integer> historicalAndCurrentCount = new Pair<>(
                    historicalFailures.getOrDefault(type, 0),
                    currentFailures.getOrDefault(type, 0)
            );
            failureTypeList.add(new Pair<>(type, historicalAndCurrentCount));
        }
        return failureTypeList;
    }

    private static MarkdownTable generateIncomparableStatistics(OmexTestStatistics statistics,
                                                                List<OmexTestCase> unmatchedTestCases,
                                                                List<OmexExecSummary> unmatchedExecSummaries){
        MarkdownTable incomparableStatistics = new MarkdownTable();
        int allUnmatched = statistics.unmatchedTestCaseCount + statistics.unmatchedExecutionsCount;
        incomparableStatistics.resizeRowAndColumnLabels(allUnmatched, 2);
        incomparableStatistics.setColumnTitle(0, "Historically Ran");
        incomparableStatistics.setColumnTitle(1, "Currently Ran");
        Map<String, Boolean> nameToIfHistoricalMap = new HashMap<>();
        if (statistics.unmatchedTestCaseCount > 0) for (OmexTestCase testCase : unmatchedTestCases){
            nameToIfHistoricalMap.put(testCase.file_path, true);
        }
        if (statistics.unmatchedExecutionsCount > 0) for (OmexExecSummary execSummary : unmatchedExecSummaries){
            nameToIfHistoricalMap.put((new File(execSummary.file_path).getName()), false);
        }
        List<String> unmatchedNames = new ArrayList<>(nameToIfHistoricalMap.keySet());
        unmatchedNames.sort(Comparator.naturalOrder());
        for (int i = 0; i < unmatchedNames.size(); i++){
            String name = unmatchedNames.get(i);
            incomparableStatistics.setRowTitle(i, name);
            // We want to put a checkmark in the correct column, based on which actually ran the test
            int matchedColumn = nameToIfHistoricalMap.get(name) ? 0 : 1;
            incomparableStatistics.setTableValue(i, matchedColumn, "✔");
            incomparableStatistics.setTableValue(i, 1 - matchedColumn, "✘");
        }
        return incomparableStatistics;
    }

    private static MarkdownTable generateUnmatchedResultStatistics(List<OmexTestCaseChange> testCaseChanges){
        MarkdownTable unmatchedResultStatistics = new MarkdownTable();
        unmatchedResultStatistics.resizeRowAndColumnLabels(testCaseChanges.size(), 2);
        unmatchedResultStatistics.setColumnTitle(0, "Historical");
        unmatchedResultStatistics.setColumnTitle(1, "Current");
        for (int i = 0; i < testCaseChanges.size(); i++){
            OmexTestCaseChange testCaseChange = testCaseChanges.get(i);
            unmatchedResultStatistics.setRowTitle(i, testCaseChange.original.file_path);

            String historicalResult = testCaseChange.original.known_status == null ? "<not run>" : switch (testCaseChange.original.known_status){
                case PASS:
                    yield "PASSED";
                case SKIP:
                    yield String.format("SKIPPED (%s)", testCaseChange.original.known_failure_type.name());
                case FAIL:
                    yield String.format("FAILED (%s)", testCaseChange.original.known_failure_type.name());
            };
            unmatchedResultStatistics.setTableValue(i, 0, historicalResult);

            String currentResult = testCaseChange.updated.known_status == null ? "<not run>" : switch (testCaseChange.updated.known_status){
                case PASS:
                    yield "PASSED";
                case SKIP:
                    String formatStringSkip = testCaseChange.original.known_failure_type == FailureType.UNSTABLE_EXECUTION ? "NEW %s" : "SKIPPED (%s)";
                    yield String.format(formatStringSkip, testCaseChange.updated.known_failure_type.name());
                case FAIL:
                    String formatStringFail = testCaseChange.original.known_failure_type == FailureType.UNSTABLE_EXECUTION ? "NEW %s" : "FAILED (%s)";
                    yield String.format(formatStringFail, testCaseChange.updated.known_failure_type.name());
            };
            unmatchedResultStatistics.setTableValue(i, 1, currentResult);
        }
        return unmatchedResultStatistics;
    }

    public static class MarkdownTable {
        private String[] rowTitles, columnTitles;
        private String[] values;

        public MarkdownTable(){
            this.rowTitles = new String[0]; this.columnTitles = new String[0];
            this.values = new String[0];
        }

        /**
         * Retrieves the value at the target coordinate.
         * Throws IllegalArgumentException if request would cause an IndexOutOfBounds exception.
         * @param row the row index to look-up
         * @param column the column index to look-up
         * @return the value in the table, as a String.
         */
        public String getTableValue(int row, int column){
            if (row >= this.rowTitles.length) throw new IllegalArgumentException(String.format("Row index `%d` is out of bounds (size: %d)", row, this.rowTitles.length));
            if (column >= this.columnTitles.length) throw new IllegalArgumentException(String.format("Column index `%d` is out of bounds (size: %d)", column, this.columnTitles.length));
            return this.values[row * this.columnTitles.length + column];
        }

        /**
         * Sets the value at the target coordinate. Converts from whatever type provided into a String.
         * Throws IllegalArgumentException if request would cause an IndexOutOfBounds exception.
         * @param row the row index to apply to
         * @param column the column index to apply to
         * @return the previous value stored at the target coordinate.
         */
        public String setTableValue(int row, int column, Object value){
            // Validate row / column pair
            if (row >= this.rowTitles.length) throw new IllegalArgumentException(String.format("Row index `%d` is out of bounds (size: %d)", row, this.rowTitles.length));
            if (column >= this.columnTitles.length) throw new IllegalArgumentException(String.format("Column index `%d` is out of bounds (size: %d)", column, this.columnTitles.length));

            // Maybe save some time.
            if (Objects.equals(value, this.values[row * this.columnTitles.length + column])) return (String)value;
            // Convert value to String.
            String newValue;
            // Organize by likely-hood of being passed for efficiency
            if (value instanceof String stringValue) newValue = stringValue;
            else if (value instanceof Double doubleValue) newValue = Double.toString(doubleValue);
            else if (value instanceof Integer integerValue) newValue = Integer.toString(integerValue);
            else if (value instanceof Long longValue) newValue = Long.toString(longValue);
            else if (value instanceof Float floatValue) newValue = Float.toString(floatValue);
            else if (value instanceof Boolean booleanValue) newValue = booleanValue ? "true" : "false";
            else if (value instanceof Byte byteValue) newValue = String.format("0x%02X", byteValue);
            else if (value instanceof Short shortValue) newValue = Short.toString(shortValue);
            else if (value instanceof Character charValue) newValue = Character.toString(charValue);
            else if (value == null) newValue = "<null>";
            else newValue = value.toString();

            // Do the swap!
            String oldValue = this.values[row * this.columnTitles.length + column];
            this.values[row * this.columnTitles.length + column] = newValue;
            return oldValue;
        }

        public void setNumRows(int number){
            if (number == this.rowTitles.length) return;
            this.resizeRowLabels(number);
        }

        public void setNumColumns(int number){
            if (number == this.columnTitles.length) return;
            this.resizeColumnLabels(number);
        }

        public void setNumRowsAndColums(int rowNumber, int colNumber){
            if (rowNumber == this.rowTitles.length && colNumber == this.columnTitles.length) return;
            this.resizeRowAndColumnLabels(rowNumber, colNumber);
        }

        public void setRowTitle(int rowIndex, String title) {
            if (rowIndex >= this.rowTitles.length) this.resizeRowLabels(rowIndex + 1);
            this.rowTitles[rowIndex] = title;
        }

        public void setColumnTitle(int colIndex, String title) {
            if (colIndex >= this.columnTitles.length) this.resizeColumnLabels(colIndex + 1);
            this.columnTitles[colIndex] = title;
        }

        public String getMarkdownTable(){
            Map<Integer, Integer> maxLengthInColumn = new HashMap<>();
            for (int i = 0; i < this.columnTitles.length + 1; i ++) maxLengthInColumn.put(i, 0);
            String[][] elementMapping = new String[this.rowTitles.length + 2][this.columnTitles.length + 1];
            for (int rowIndex = -2; rowIndex < this.rowTitles.length; rowIndex++){
                for (int colIndex = -1; colIndex < this.columnTitles.length; colIndex++){
                    if (rowIndex == -2){
                        if (colIndex == -1){
                            elementMapping[0][0] = "   "; // We do three spaces to make sure the min-width is 3; needed for the header line!
                            maxLengthInColumn.put(0, 3);
                            continue; // Top left should be empty!
                        }
                        elementMapping[0][colIndex + 1] = this.columnTitles[colIndex];
                        if (this.columnTitles[colIndex].length() > maxLengthInColumn.get(colIndex + 1))
                            maxLengthInColumn.put(colIndex + 1, this.columnTitles[colIndex].length());
                        continue;
                    } else if (rowIndex == -1){
                        elementMapping[1][colIndex + 1] = "-";
                        // We know the maxLength in any column will be 3 or greater, so don't bother checking...
                        continue;
                    }
                    // Now we process "normal" rows
                    if (colIndex == -1){
                        elementMapping[rowIndex + 2][0] = this.rowTitles[rowIndex];
                        if (this.rowTitles[rowIndex].length() > maxLengthInColumn.get(0))
                            maxLengthInColumn.put(0, this.rowTitles[rowIndex].length());
                        continue;
                    }
                    // This should be an element in the table!
                    String tableValue = this.getTableValue(rowIndex, colIndex);
                    elementMapping[rowIndex + 2][colIndex + 1] = tableValue;
                    if (tableValue.length() > maxLengthInColumn.get(colIndex + 1))
                        maxLengthInColumn.put(colIndex + 1, tableValue.length());
                }
            }
            // We've completed our parsing, now let's build the table
            StringBuilder tableBuilder = new StringBuilder();
            for (String[] row : elementMapping){
                tableBuilder.append("|");
                for (int colIndex = 0; colIndex < row.length; colIndex++){
                    String rawValue = row[colIndex];
                    if ("-".equals(rawValue)){
                        String headerLine = rawValue.repeat(maxLengthInColumn.get(colIndex));
                        tableBuilder.append(":").append(headerLine).append(":|");
                        continue;
                    }
                    int numRepeats = maxLengthInColumn.get(colIndex) - rawValue.length();
                    tableBuilder.append(" ").append(rawValue).append(" ".repeat(numRepeats)).append(" |");
                }
                tableBuilder.append("\n");
            }
            return tableBuilder.toString();
        }

        private void resizeRowLabels(int newSize){
            if (newSize == this.rowTitles.length) return;
            int oldRowSize = this.rowTitles.length;
            String[] newRowTitles = new String[newSize];
            System.arraycopy(this.rowTitles, 0, newRowTitles, 0, java.lang.Math.min(this.rowTitles.length, newSize));
            this.rowTitles = newRowTitles;
            scaleTableValues(oldRowSize, this.columnTitles.length); // We've changed table dimensions; need to scale!
        }

        private void resizeColumnLabels(int newSize){
            if (newSize == this.columnTitles.length) return;
            int oldColumnSize = this.columnTitles.length;
            String[] newColumnTitles = new String[newSize];
            System.arraycopy(this.columnTitles, 0, newColumnTitles, 0, java.lang.Math.min(this.columnTitles.length, newSize));
            this.columnTitles = newColumnTitles;
            scaleTableValues(this.rowTitles.length, oldColumnSize); // We've changed table dimensions; need to scale!
        }

        private void resizeRowAndColumnLabels(int newRowSize, int newColumnSize){
            if (newRowSize == this.rowTitles.length && newColumnSize == this.columnTitles.length) return;
            int oldRowSize = this.rowTitles.length, oldColumnSize = this.columnTitles.length;
            String[] newRowTitles = new String[newRowSize];
            String[] newColumnTitles = new String[newColumnSize];
            System.arraycopy(this.rowTitles, 0, newRowTitles, 0, java.lang.Math.min(this.rowTitles.length, newRowSize));
            System.arraycopy(this.columnTitles, 0, newColumnTitles, 0, java.lang.Math.min(this.columnTitles.length, newColumnSize));
            this.rowTitles = newRowTitles;
            this.columnTitles = newColumnTitles;
            scaleTableValues(oldRowSize, oldColumnSize); // We've changed table dimensions; need to scale!
        }


        /**
         * Since we track the table dimensions using the size of the label arrays, this method needs to be called after
         * label arrays have been modified; which in practice is actually sensible and useful.
         */
        private void scaleTableValues(int oldRowLength, int oldColumnLength){
            String[] newValues = new String[this.rowTitles.length * this.columnTitles.length];
            if (oldRowLength > this.rowTitles.length || oldColumnLength > this.columnTitles.length)
                if (logger.isDebugEnabled()) logger.warn("Downscaling of table; some data may be lost!");
            int safeRowLength = java.lang.Math.min(this.rowTitles.length, oldRowLength);
            int safeColumnLength = java.lang.Math.min(this.columnTitles.length, oldColumnLength);
            for (int rowIter = 0; rowIter < safeRowLength; rowIter++){
                System.arraycopy(this.values, rowIter * oldColumnLength, newValues, rowIter * this.columnTitles.length, safeColumnLength);
            }
            this.values = newValues;
        }
    }


}
