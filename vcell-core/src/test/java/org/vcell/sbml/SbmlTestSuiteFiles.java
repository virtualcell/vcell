package org.vcell.sbml;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;


public class SbmlTestSuiteFiles {

    private final static int[] allTestCases = IntStream.rangeClosed(1, 1821).toArray();
    private final static String SbmlTestSuiteFilenameFormatPattern_l3v2 = "sbml-test-suite/cases/semantic/%05d-sbml-l3v2.xml";
    private final static String SbmlTestSuiteFilenameFormatPattern_l3v1 = "sbml-test-suite/cases/semantic/%05d-sbml-l3v1.xml";
    private final static String SbmlTestSuiteFilenameFormatPattern_l2v5 = "sbml-test-suite/cases/semantic/%05d-sbml-l2v5.xml";
    private final static String BiomodelsFilenameFormatPattern = "BIO%010d_urn.xml";

    public static int[] getSbmlTestSuiteCases() {
        IntPredicate testFilter = t -> (t >= 1200) && (t < 1300);

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

    public static InputStream getBioModelsModel(int biomodelsId) {
        int[] testCases = getBiomodelsModels();
        if (!Arrays.stream(testCases).anyMatch(i -> i == biomodelsId)) {
            throw new RuntimeException("file not found for biomodels id "+biomodelsId);
        }
        try {
            return getFileFromResourceAsStream(String.format(BiomodelsFilenameFormatPattern, biomodelsId));
        }catch (FileNotFoundException e){
            throw new RuntimeException("File Not Found: "+e.getMessage(),e);
        }
    }


    private static InputStream getFileFromResourceAsStream(String fileName) throws FileNotFoundException {
        InputStream inputStream = SbmlTestSuiteFiles.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
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
        InputStream inputStream = getSbmlTestCase(getSbmlTestSuiteCases()[0]);
        Assert.assertTrue(inputStream != null);
    }


}
