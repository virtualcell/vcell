package org.vcell.sbml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@Tag("Fast")
public class SbmlTestSuiteFiles {

    private final static int[] allTestCases = IntStream.rangeClosed(1, 1821).toArray();
    private final static String SbmlTestSuiteFilenameFormatPattern_l3v2 = "sbml-test-suite/cases/semantic/%05d-sbml-l3v2.xml";
    private final static String SbmlTestSuiteFilenameFormatPattern_l3v1 = "sbml-test-suite/cases/semantic/%05d-sbml-l3v1.xml";
    private final static String SbmlTestSuiteFilenameFormatPattern_l2v5 = "sbml-test-suite/cases/semantic/%05d-sbml-l2v5.xml";
    private final static String SbmlTestSuiteResultsCSVPattern = "sbml-test-suite/cases/semantic/%05d-results.csv";
    private final static String SbmlTestSuiteSettingsPattern = "sbml-test-suite/cases/semantic/%05d-settings.txt";

    public static int[] getSbmlTestSuiteCases() {
        IntPredicate testFilter = t -> (t >= 0) && (t < 1200);

        return Arrays.stream(allTestCases).filter(testFilter).toArray();
    }

    public static int[] getBiomodelsModels() {
        return new int[] { 529 };
    }

    public static InputStream getSbmlTestCase(int testCaseNumber) {
        int[] testCases = getSbmlTestSuiteCases();
        if (!Arrays.stream(testCases).anyMatch(i -> i == testCaseNumber)) {
            throw new RuntimeException("file not found for SBML Test Suite test "+testCaseNumber);
        }
        try {
            return getFileFromResourceAsStream(String.format(SbmlTestSuiteFilenameFormatPattern_l3v2, testCaseNumber));
        }catch (FileNotFoundException e){
            try {
                return getFileFromResourceAsStream(String.format(SbmlTestSuiteFilenameFormatPattern_l3v1, testCaseNumber));
            }catch (FileNotFoundException e1){
                try {
                    return getFileFromResourceAsStream(String.format(SbmlTestSuiteFilenameFormatPattern_l2v5, testCaseNumber));
                }catch (FileNotFoundException e2){
                    throw new RuntimeException("failed to find L3V2 or L3V1 or L2V5 file: "+e2.getMessage(), e2);
                }
            }
        }
    }

    public static String getSbmlTestCaseSettingsAsText(int testCaseNumber) throws FileNotFoundException {
        return getFileContentsAsString(testCaseNumber, SbmlTestSuiteSettingsPattern);
    }

    public static String getSbmlTestCaseResultsAsCSV(int testCaseNumber) throws FileNotFoundException {
        return getFileContentsAsString(testCaseNumber, SbmlTestSuiteResultsCSVPattern);
    }

    private static String getFileContentsAsString(int testCaseNumber, String filePattern) throws FileNotFoundException {
        int[] testCases = getSbmlTestSuiteCases();
        if (!Arrays.stream(testCases).anyMatch(i -> i == testCaseNumber)) {
            throw new RuntimeException("test case not found for SBML Test Suite test "+testCaseNumber);
        }
        InputStream is = getFileFromResourceAsStream(String.format(filePattern, testCaseNumber));
        String textContent = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                            .lines().collect(Collectors.joining("\n"));
        return textContent;
    }

    private static InputStream getFileFromResourceAsStream(String fileName) throws FileNotFoundException {
        InputStream inputStream = SbmlTestSuiteFiles.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    @Test
    public void test_out_of_bounds_exception() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            getSbmlTestCase(10000);
        });
    }

    @Test
    public void test_read_sbmlFile() throws URISyntaxException {
        InputStream inputStream = getSbmlTestCase(getSbmlTestSuiteCases()[0]);
        assertNotNull(inputStream);
    }

    @Test
    public void test_read_settings() throws FileNotFoundException {
        String textContent = getSbmlTestCaseSettingsAsText(getSbmlTestSuiteCases()[0]);
        assertNotNull(textContent);
    }

    @Test
    public void test_read_results() throws FileNotFoundException {
        String textContent = getSbmlTestCaseResultsAsCSV(getSbmlTestSuiteCases()[0]);
        assertNotNull(textContent);
    }


}
