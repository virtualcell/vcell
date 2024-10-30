package org.vcell.cli.run;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;

import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Predicate;


@Tag("Fast")
public class BiosimulationsFiles {

    private final static String[] allProjectIDs = new String[]{
            "BIOMD0000000003_tellurium_A_minimal_cascade_model_for_th",
            "BIOMD0000000006_tellurium_Modeling_the_cell_division_cyc",
            "BIOMD0000000036_tellurium_A_simple_model_of_circadian_rh",
            "BIOMD0000000300"
    };

    public static String[] getProjectIDs() {
        Predicate<String> testFilter = t -> true;

        return Arrays.stream(allProjectIDs).filter(testFilter).toArray(String[]::new);
    }

    public static InputStream getOmex(String projectID) {
        if (!Arrays.stream(allProjectIDs).anyMatch(pid -> pid.equals(projectID))) {
            throw new RuntimeException("project "+projectID+" not in project list");
        }
        return getFileFromResourceAsStream(projectID + ".spec.omex");
    }

     private static InputStream getFileFromResourceAsStream(String fileName) {
        InputStream inputStream = BiosimulationsFiles.class.getResourceAsStream("/BiosimulationsOmexWithResults/"+fileName);
        if (inputStream == null) {
            throw new RuntimeException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    public static InputStream getH5(String projectID) {
        if (!Arrays.stream(allProjectIDs).anyMatch(pid -> pid.equals(projectID))) {
            throw new RuntimeException("project "+projectID+" not in project list");
        }
        return getFileFromResourceAsStream(projectID + ".h5");
    }

    @Test
    public void test_read_omex_file() {
        InputStream inputStream = getOmex(allProjectIDs[0]);
        Assertions.assertTrue(inputStream != null);
    }

    @Test
    public void test_read_H5_file() {
        InputStream inputStream = getH5(allProjectIDs[0]);
        Assertions.assertTrue(inputStream != null);
    }

}
