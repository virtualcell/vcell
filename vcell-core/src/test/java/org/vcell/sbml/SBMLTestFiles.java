package org.vcell.sbml;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

public class SBMLTestFiles {

    public final static String[] allTestFiles = new String[]{
            // sbml-biomodels-db/
            "BIOMD0000000734.xml",
    };


    public static InputStream getSBMLTestCase(String testFile) {
        if (!Arrays.stream(allTestFiles).anyMatch(file -> file.equals(testFile))) {
            throw new RuntimeException("file not found for VCell Published Test Suite test "+testFile);
        }
        try {
            return getFileFromResourceAsStream("sbml-biomodels-db/"+testFile);
        }catch (FileNotFoundException e){
            throw new RuntimeException("failed to find test case file '"+testFile+"' in sbml-biomodels-db/: "+e.getMessage(), e);
        }
    }

    private static InputStream getFileFromResourceAsStream(String fileName) throws FileNotFoundException {
        InputStream inputStream = VcmlTestSuiteFiles.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    @Test
    public void test_read_sbmlFile() {
        InputStream inputStream = getSBMLTestCase(allTestFiles[0]);
        Assert.assertTrue(inputStream != null);
    }

}
