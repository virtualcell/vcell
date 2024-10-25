package org.vcell.cli.run.hdf5;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class Hdf5PreparedDataTest {

    @Test
    public void testCreateMultidimensionalArray_1D() {
        Hdf5PreparedData preparedData = new Hdf5PreparedData();
        preparedData.dataDimensions = new long[]{3};
        preparedData.flattenedDataBuffer = new double[]{1, 2, 3};
        Object multidimensionalArray = preparedData.createMultidimensionalArray();
        assert multidimensionalArray instanceof double[];
        double[] data = (double[]) multidimensionalArray;
        assert data.length == 3;
        assert data[0] == 1;
        assert data[1] == 2;
        assert data[2] == 3;
    }

    @Test
    public void testCreateMultidimensionalArray_2D() {
        Hdf5PreparedData preparedData = new Hdf5PreparedData();
        preparedData.dataDimensions = new long[]{2, 3};
        preparedData.flattenedDataBuffer = new double[]{1, 2, 3, 4, 5, 6};
        Object multidimensionalArray = preparedData.createMultidimensionalArray();
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
        Hdf5PreparedData preparedData = new Hdf5PreparedData();
        preparedData.dataDimensions = new long[]{2, 3, 4};
        preparedData.flattenedDataBuffer = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        Object multidimensionalArray = preparedData.createMultidimensionalArray();
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
        assert data[1][0][0] == 8;
        assert data[1][0][1] == 9;
        assert data[1][0][2] == 10;
        assert data[1][0][3] == 11;
        assert data[1][1][0] == 12;
    }
}
