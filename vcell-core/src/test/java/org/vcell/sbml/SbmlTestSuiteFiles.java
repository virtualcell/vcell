package org.vcell.sbml;

import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;


public class SbmlTestSuiteFiles {

    private final static int[] allTestCases = IntStream.rangeClosed(1, 1821).toArray();
    private final static String SbmlTestSuiteFilenameFormatPattern = "sbml-test-suite/cases/semantic/%05d-sbml-l3v2.xml";
    private final static String BiomodelsFilenameFormatPattern = "BIO%010d_urn.xml";

    public static int[] getSbmlTestSuiteCases() {
        IntPredicate testFilter = t ->
                (t >= 0) && (t < 527)
                        && (t != 59) && (t != 68) && (t != 69) && (t != 70) && (t != 96)
                        && (t != 129) && (t != 130) && (t != 131) && (t != 134)
                        && (t != 388) && (t != 391) && (t != 394) && (t != 445)
                        && (t != 448) && (t != 451) && (t != 516) && (t != 516)
                        && (t != 517) && (t != 518) && (t != 519) && (t != 520)
                        && (t != 521);

        return Arrays.stream(allTestCases).filter(testFilter).toArray();
    }

    public static int[] getBiomodelsModels() {
        return new int[] { 529 };
    }

    public static InputStream getSbmlTestCase(int testCaseNumber) {
        int[] testCases = getSbmlTestSuiteCases();
        if (!Arrays.stream(testCases).anyMatch(i -> i == testCaseNumber)) {
            throw new RuntimeException("file not found");
        }
        return getFileFromResourceAsStream(String.format(SbmlTestSuiteFilenameFormatPattern, testCaseNumber));
    }

    public static InputStream getBioModelsModel(int biomodelsId) {
        int[] testCases = getBiomodelsModels();
        if (!Arrays.stream(testCases).anyMatch(i -> i == biomodelsId)) {
            throw new RuntimeException("file not found");
        }
        return getFileFromResourceAsStream(String.format(BiomodelsFilenameFormatPattern, biomodelsId));
    }


    private static InputStream getFileFromResourceAsStream(String fileName) {
        InputStream inputStream = SbmlTestSuiteFiles.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    @Test(expected = RuntimeException.class)
    public void test_out_of_bounds_exception() throws URISyntaxException {
        getSbmlTestCase(10000);
    }

    @Test
    public void test_read_sbmlFile() throws URISyntaxException {
        InputStream inputStream = getSbmlTestCase(2);
        Assert.assertTrue(inputStream != null);
    }


}
