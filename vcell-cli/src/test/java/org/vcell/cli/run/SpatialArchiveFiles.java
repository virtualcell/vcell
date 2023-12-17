package org.vcell.cli.run;

import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Predicate;

@Category(Fast.class)
@Tag("Fast")
public class SpatialArchiveFiles {
    private final static String[] allTestFiles = new String[]{
            "TinySpatialProject.omex",
            "SimpleSpatialModel.omex"
    };

    public static String[] getSpatialTestCases() {
        Predicate<String> testFilter = t -> true;

        return Arrays.stream(allTestFiles).filter(testFilter).toArray(String[]::new);
    }

    public static InputStream getSpatialTestCase(String testFile) {
        if (Arrays.stream(allTestFiles).noneMatch(file -> file.equals(testFile))) {
            throw new RuntimeException("file not found for VCell Published Test Suite test "+testFile);
        }
        try {
            return getFileFromResourceAsStream(testFile);
        }catch (FileNotFoundException e){
            throw new RuntimeException("failed to find test case file '"+testFile+"': " + e.getMessage(), e);
        }
    }

    private static InputStream getFileFromResourceAsStream(String fileName) throws FileNotFoundException {
        Class<org.vcell.cli.run.SpatialArchiveFiles> spatialFilesClass = org.vcell.cli.run.SpatialArchiveFiles.class;
        InputStream nextTestFile = spatialFilesClass.getResourceAsStream("/spatial/" + fileName);
        if (nextTestFile == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            return nextTestFile;
        }
    }

    @Test
    public void test_read_Spatial_omex_file() {
        InputStream inputStream = getSpatialTestCase(allTestFiles[0]);
        Assert.assertNotNull(inputStream);
    }
}
