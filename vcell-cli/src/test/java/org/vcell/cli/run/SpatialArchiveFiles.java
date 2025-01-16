package org.vcell.cli.run;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.sedml.testsupport.OmexTestCase;
import org.vcell.sedml.testsupport.OmexTestingDatabase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("Fast")
public class SpatialArchiveFiles {

    public static OmexTestCase[] getSpatialTestCases() throws IOException {
        List<OmexTestCase> allTestCases = OmexTestingDatabase.loadOmexTestCases();
        Predicate<OmexTestCase> testFilter = t -> t.test_collection == OmexTestingDatabase.TestCollection.VCELL_SPATIAL;

        return allTestCases.stream().filter(testFilter).toArray(OmexTestCase[]::new);
    }

    public static InputStream getOmex(OmexTestCase testCase) {
        String fullPath = testCase.test_collection.pathPrefix + "/" + testCase.file_path;
        String path = fullPath.substring(fullPath.indexOf("/spatial"));
        return getFileFromResourceAsStream(path + ".omex");
    }

    private static InputStream getFileFromResourceAsStream(String path) {
        InputStream inputStream = SpatialArchiveFiles.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("file not found! " + path);
        } else {
            return inputStream;
        }
    }

    @Test
    public void test_read_omex_file() throws IOException {
        InputStream inputStream = getOmex(getSpatialTestCases()[0]);
        Assertions.assertTrue(inputStream != null);
    }
}
