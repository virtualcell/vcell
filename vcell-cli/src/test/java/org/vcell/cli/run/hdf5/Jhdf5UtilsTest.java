package org.vcell.cli.run.hdf5;

import cbit.vcell.export.server.JhdfUtils;
import io.jhdf.HdfFile;
import io.jhdf.WritableDatasetImpl;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.WritableGroup;
import io.jhdf.api.WritiableDataset;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Tag("Fast")
public class Jhdf5UtilsTest {

    @Test
    public void testCreateMultidimensionalArray_1D() {
        long[] dims = new long[]{3};
        double[] flattenedData = new double[]{1, 2, 3};
        Object multidimensionalArray = JhdfUtils.createMultidimensionalArray(dims, flattenedData);
        assert multidimensionalArray instanceof double[];
        double[] data = (double[]) multidimensionalArray;
        assert data.length == 3;
        assert data[0] == 1;
        assert data[1] == 2;
        assert data[2] == 3;
    }

    @Test
    public void testCreateMultidimensionalArray_2D() {
        long[] dims = new long[]{2, 3};
        double[] flattenedData = new double[]{1, 2, 3, 4, 5, 6};
        Object multidimensionalArray = JhdfUtils.createMultidimensionalArray(dims, flattenedData);
        assert multidimensionalArray instanceof double[][];
        double[][] data = (double[][]) multidimensionalArray;
        assert data.length == 2;
        assert data[0].length == 3;
        assert data[0][0] == 1;
        assert data[0][1] == 2;
        assert data[0][2] == 3;
        assert data[1][0] == 4;
        assert data[1][1] == 5;
        assert data[1][2] == 6;
    }

    @Test
    public void testCreateMultidimensionalArray_3D() {
        long[] dims = new long[]{2, 3, 4};
        double[] flattenedData = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24};
        Object multidimensionalArray = JhdfUtils.createMultidimensionalArray(dims, flattenedData);
        assert multidimensionalArray instanceof double[][][];
        double[][][] data = (double[][][]) multidimensionalArray;
        assert data.length == 2;
        assert data[0].length == 3;
        assert data[0][0].length == 4;
        assert data[0][0][0] == 1;
        assert data[0][0][1] == 2;
        assert data[0][0][2] == 3;
        assert data[0][0][3] == 4;
        assert data[0][1][0] == 5;
        assert data[0][1][1] == 6;
        assert data[0][1][2] == 7;
        assert data[0][1][3] == 8;
        assert data[0][2][0] == 9;
        assert data[0][2][1] == 10;
        assert data[0][2][2] == 11;
        assert data[0][2][3] == 12;
        assert data[1][0][0] == 13;
        assert data[1][0][1] == 14;
        assert data[1][0][2] == 15;
        assert data[1][0][3] == 16;
        assert data[1][1][0] == 17;
        assert data[1][1][1] == 18;
        assert data[1][1][2] == 19;
        assert data[1][1][3] == 20;
        assert data[1][2][0] == 21;
        assert data[1][2][1] == 22;
        assert data[1][2][2] == 23;
        assert data[1][2][3] == 24;
    }

    @Test
    public void testStringAttributes() throws IOException {
        Path tempFile = Files.createTempFile(this.getClass().getSimpleName(), ".hdf5");
        WritableHdfFile writableHdfFile = HdfFile.write(tempFile);

        // Write a dataset with string attributes
        WritiableDataset writiableDataset = writableHdfFile.putDataset("dataset", new double[] {0.1, 0.2, 0.3});
        JhdfUtils.putAttribute(writiableDataset, "labels", Arrays.asList("vv", "xx", "abcdef"));
        JhdfUtils.putAttribute(writiableDataset, "units", Arrays.asList("", "1", "mm2"));
        writableHdfFile.close();

        // Now read it back
        try (HdfFile hdfFile = new HdfFile(tempFile)) {
            Dataset dataset = hdfFile.getDatasetByPath("dataset");

            // Expected :["vv", "xx", "abcdef"]
            // Actual   :["vv", "cdedf", ""]
            Assertions.assertEquals(Arrays.asList("vv", "xx", "abcdef"), List.of((String[])dataset.getAttribute("labels").getData()));

            // Expected :["", "1", "mm2"]
            // Actual   :["", "m2", ""]
            Assertions.assertEquals(Arrays.asList("", "1", "mm2"), List.of((String[])dataset.getAttribute("units").getData()));
        } finally {
            tempFile.toFile().delete();
        }

    }

    @Test
    public void testAddingAndGettingGroupByPath() throws IOException {
        Path tempFile = Files.createTempFile(this.getClass().getSimpleName(), ".hdf5");
        try(WritableHdfFile writableHdfFile = HdfFile.write(tempFile)){
            WritableGroup subGroup = JhdfUtils.addGroupByPath(writableHdfFile, "/path/to", true);
            Assertions.assertNotNull(JhdfUtils.getGroupByPath(writableHdfFile, "path/to"));
            WritableGroup newGroup = JhdfUtils.addGroupByPath(subGroup, "the");
                    Assertions.assertThrows(IllegalArgumentException.class,
                    () -> JhdfUtils.addGroupByPath(writableHdfFile, "path/to/the/fictitious/group"));
            newGroup.putDataset("dataset", new double[] {0.1, 0.2, 0.3});
            Assertions.assertThrows(IllegalArgumentException.class, () -> JhdfUtils.addGroupByPath(writableHdfFile, "path/to/the/dataset"));
            WritableGroup alsoNewGroup = JhdfUtils.getGroupByPath(writableHdfFile, "path/to/the");
            Assertions.assertEquals(newGroup, alsoNewGroup);
            Assertions.assertThrows(IllegalArgumentException.class, ()-> JhdfUtils.getGroupByPath(writableHdfFile, "path/to/the/dataset"));
            WritableDatasetImpl dataset = JhdfUtils.getDatasetByPath(writableHdfFile, "path/to/the/dataset");
            WritableDatasetImpl alsoDataset = JhdfUtils.getDatasetByPath(alsoNewGroup, "dataset");
            Assertions.assertEquals(dataset, alsoDataset);
            Assertions.assertThrows(IllegalArgumentException.class, ()-> JhdfUtils.getDatasetByPath(subGroup, "the"));
        }
    }
}
