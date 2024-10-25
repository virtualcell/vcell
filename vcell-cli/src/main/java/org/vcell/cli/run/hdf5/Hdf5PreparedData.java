package org.vcell.cli.run.hdf5;

public class Hdf5PreparedData {
    public String sedmlId;
    public long[] dataDimensions;
    public double[] flattenedDataBuffer;

    public Object createMultidimensionalArray() {

        int dim0 = (int) this.dataDimensions[0];
        Integer dim1 = (this.dataDimensions.length > 1) ? (int) this.dataDimensions[1] : null;
        Integer dim2 = (this.dataDimensions.length > 2) ? (int) this.dataDimensions[2] : null;
        Integer dim3 = (this.dataDimensions.length > 3) ? (int) this.dataDimensions[3] : null;
        Integer dim4 = (this.dataDimensions.length > 4) ? (int) this.dataDimensions[4] : null;
        Integer dim5 = (this.dataDimensions.length > 5) ? (int) this.dataDimensions[5] : null;
        Integer dim6 = (this.dataDimensions.length > 6) ? (int) this.dataDimensions[6] : null;
        Integer dim7 = (this.dataDimensions.length > 7) ? (int) this.dataDimensions[7] : null;

        // load data from this.flattenedDataBuffer into correctly dimensioned double array
        switch (this.dataDimensions.length) {
            case 1: {
                double[] data = new double[dim0];
                System.arraycopy(this.flattenedDataBuffer, 0, data, 0, this.flattenedDataBuffer.length);
                return data;
            }
            case 2: {
                double[][] data = new double[dim0][dim1];
                int index2 = 0;
                for (int i = 0; i < dim0; i++) {
                    for (int j = 0; j < dim1; j++) {
                        data[i][j] = this.flattenedDataBuffer[index2];
                        index2++;
                    }
                }
                return data;
            }
            case 3: {
                double[][][] data = new double[dim0][dim1][dim2];
                int index3 = 0;
                for (int i = 0; i < dim0; i++) {
                    for (int j = 0; j < dim1; j++) {
                        for (int k = 0; k < dim2; k++) {
                            data[i][j][k] = this.flattenedDataBuffer[index3];
                            index3++;
                        }
                    }
                }
                return data;
            }
            case 4: {
                double[][][][] data = new double[dim0][dim1][dim2][dim3];
                int index4 = 0;
                for (int i = 0; i < dim0; i++) {
                    for (int j = 0; j < dim1; j++) {
                        for (int k = 0; k < dim2; k++) {
                            for (int l = 0; l < dim3; l++) {
                                data[i][j][k][l] = this.flattenedDataBuffer[index4];
                                index4++;
                            }
                        }
                    }
                }
                return data;
            }
            case 5: {
                double[][][][][] data = new double[dim0][dim1][dim2][dim3][dim4];
                int index5 = 0;
                for (int i = 0; i < dim0; i++) {
                    for (int j = 0; j < dim1; j++) {
                        for (int k = 0; k < dim2; k++) {
                            for (int l = 0; l < dim3; l++) {
                                for (int m = 0; m < dim4; m++) {
                                    data[i][j][k][l][m] = this.flattenedDataBuffer[index5];
                                    index5++;
                                }
                            }
                        }
                    }
                }
                return data;
            }
            case 6: {
                double[][][][][][] data = new double[dim0][dim1][dim2][dim3][dim4][dim5];
                int index6 = 0;
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < dim1; j++) {
                        for (int k = 0; k < dim2; k++) {
                            for (int l = 0; l < dim3; l++) {
                                for (int m = 0; m < dim4; m++) {
                                    for (int n = 0; n < dim5; n++) {
                                        data[i][j][k][l][m][n] = this.flattenedDataBuffer[index6];
                                        index6++;
                                    }
                                }
                            }
                        }
                    }
                }
                return data;
            }
            case 7: {
                double[][][][][][][] data = new double[dim0][dim1][dim2][dim3][dim4][dim5][dim6];
                int index7 = 0;
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < dim1; j++) {
                        for (int k = 0; k < dim2; k++) {
                            for (int l = 0; l < dim3; l++) {
                                for (int m = 0; m < dim4; m++) {
                                    for (int n = 0; n < dim5; n++) {
                                        for (int o = 0; o < dim6; o++) {
                                            data[i][j][k][l][m][n][o] = this.flattenedDataBuffer[index7];
                                            index7++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return data;
            }
            case 8: {
                double[][][][][][][][] data = new double[dim0][dim1][dim2][dim3][dim4][dim5][dim6][dim7];
                int index8 = 0;
                for (int i = 0; i < data.length; i++) {
                    for (int j = 0; j < dim1; j++) {
                        for (int k = 0; k < dim2; k++) {
                            for (int l = 0; l < dim3; l++) {
                                for (int m = 0; m < dim4; m++) {
                                    for (int n = 0; n < dim5; n++) {
                                        for (int o = 0; o < dim6; o++) {
                                            for (int p = 0; p < dim7; p++) {
                                                data[i][j][k][l][m][n][o][p] = this.flattenedDataBuffer[index8];
                                                index8++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return data;
            }
            default:
                throw new IllegalArgumentException("Cannot create a dataset with more than 9 dimensions.");
        }
    }

}
