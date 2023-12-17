package org.vcell.sbml;

import org.junit.jupiter.api.Tag;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

@Category(Fast.class)
@Tag("Fast")
public class BMDB_SBML_Files {

    public final static int[] allCuratedModels = IntStream.rangeClosed(1, 1064)
            .filter(n -> !Arrays.asList(649,694,992,993,1049,1050,1051).contains(n)).toArray();
    private final static String BiomodelsCuratedFileFormatPattern = "sbml-biomodels-curated/BIOMD%010d.xml";

    public static int[] getBiomodelDB_curatedModelNumbers() {
        // only those Biomodels Database models which are committed to the vcell repo
        // Uncomment "includeFilter = n -> true;" to run all tests (must have the other models installed in proper directory).
        IntPredicate includeFilter = n -> Arrays.asList(48,205,264,427,452,453,594,595,623,826).contains(n);
        //IntPredicate includeFilter = n -> true;

        IntPredicate testFilter = n -> includeFilter.test(n);

        return Arrays.stream(allCuratedModels).filter(testFilter).toArray();
    }

    public static InputStream getBiomodelsDbCuratedModel(int modelNumber) {
        int[] allModelNumbers = getBiomodelDB_curatedModelNumbers();
        if (!Arrays.stream(allModelNumbers).anyMatch(i -> i == modelNumber)) {
            throw new RuntimeException("model number "+modelNumber+" not in list of models");
        }
        try {
            return getFileFromResourceAsStream(modelNumber);
        }catch (FileNotFoundException e){
            throw new RuntimeException("failed to find file file: "+e.getMessage(), e);
        }
    }

     static InputStream getFileFromResourceAsStream(int modelNumber) throws FileNotFoundException {
        String fileName = String.format(BiomodelsCuratedFileFormatPattern, modelNumber);
        InputStream inputStream = BMDB_SBML_Files.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    @Test(expected = RuntimeException.class)
    public void test_out_of_bounds_exception() {
        getBiomodelsDbCuratedModel(10000);
    }

    @Test
    public void test_read_sbmlFile() {
        InputStream inputStream = getBiomodelsDbCuratedModel(getBiomodelDB_curatedModelNumbers()[0]);
        Assert.assertTrue(inputStream != null);
    }


}
