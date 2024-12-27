package org.vcell.cli.run;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.sedml.testsupport.OmexTestCase;
import org.vcell.sedml.testsupport.OmexTestingDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;


@Tag("Fast")
public class QuantOmexExecFiles {

    public static OmexTestCase[] getTestCases() throws IOException {
        List<OmexTestCase> allTestCases = OmexTestingDatabase.loadOmexTestCases();
        Predicate<OmexTestCase> testFilter = t -> t.test_collection == OmexTestingDatabase.TestCollection.VCELL_QUANT_OMEX;

        return allTestCases.stream().filter(testFilter).toArray(OmexTestCase[]::new);
    }

    public static InputStream getOmex(OmexTestCase testCase) {
        String fullPath = testCase.test_collection.pathPrefix + "/" + testCase.file_path;
        String path = fullPath.substring(fullPath.indexOf("/OmexWithThirdPartyResults"));
        return getFileFromResourceAsStream(path + ".spec.omex");
    }

     private static InputStream getFileFromResourceAsStream(String path) {
        InputStream inputStream = QuantOmexExecFiles.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("file not found! " + path);
        } else {
            return inputStream;
        }
    }

    public static InputStream getH5(OmexTestCase testCase) {
        String fullPath = testCase.test_collection.pathPrefix + "/" + testCase.file_path;
        String path = fullPath.substring(fullPath.indexOf("/OmexWithThirdPartyResults"));
        return getFileFromResourceAsStream(path + ".h5");
    }

    @Test
    public void test_read_omex_file() throws IOException {
        InputStream inputStream = getOmex(getTestCases()[0]);
        Assertions.assertTrue(inputStream != null);
    }

    @Test
    public void test_read_H5_file() throws IOException {
        InputStream inputStream = getH5(getTestCases()[0]);
        Assertions.assertTrue(inputStream != null);
    }

}
