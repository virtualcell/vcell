package org.vcell.cli.run;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.cli.testsupport.OmexTestCase;
import org.vcell.cli.testsupport.OmexTestingDatabase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;


@Tag("Fast")
public class BSTSBasedTestSuiteFiles {


    public static OmexTestCase[] getBSTSTestCases() throws IOException {
        List<OmexTestCase> allTestCases = OmexTestingDatabase.loadOmexTestCases();
        Predicate<OmexTestCase> testFilter = t -> t.test_collection.repo == OmexTestingDatabase.TestDataRepo.vcell;

        return allTestCases.stream().filter(testFilter).toArray(OmexTestCase[]::new);
    }

    public static InputStream getBSTSTestCase(OmexTestCase testCase) throws FileNotFoundException {
        String fullPath = testCase.test_collection.pathPrefix + "/" + testCase.file_path;
        // keep the path starting with "/bsts-omex"
        String path = fullPath.substring(fullPath.indexOf("/bsts-omex"));
        return getFileFromResourceAsStream(path);
    }

     private static InputStream getFileFromResourceAsStream(String fileName) throws FileNotFoundException {
        InputStream inputStream = BSTSBasedTestSuiteFiles.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    @Test
    public void test_read_BSTS_omex_file() throws IOException {
        List<OmexTestCase> allTestCases = OmexTestingDatabase.loadOmexTestCases();
        InputStream inputStream = getBSTSTestCase(allTestCases.get(0));
        Assertions.assertTrue(inputStream != null);
    }

}
