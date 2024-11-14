package cbit.vcell.export.server;

import io.jhdf.api.Attribute;
import io.jhdf.api.WritableGroup;
import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import io.jhdf.api.WritableNode;
import org.vcell.util.trees.Tree;
import io.jhdf.object.datatype.CompoundDataType;

import java.util.*;
import java.util.List;

public class JhdfUtils {
    public static Object createMultidimensionalArray(long[] dataDimensions, double[] flattenedDataBuffer) {

        int dim0 = (int) dataDimensions[0];
        Integer dim1 = (dataDimensions.length > 1) ? (int) dataDimensions[1] : null;
        Integer dim2 = (dataDimensions.length > 2) ? (int) dataDimensions[2] : null;
        Integer dim3 = (dataDimensions.length > 3) ? (int) dataDimensions[3] : null;
        Integer dim4 = (dataDimensions.length > 4) ? (int) dataDimensions[4] : null;
        Integer dim5 = (dataDimensions.length > 5) ? (int) dataDimensions[5] : null;
        Integer dim6 = (dataDimensions.length > 6) ? (int) dataDimensions[6] : null;
        Integer dim7 = (dataDimensions.length > 7) ? (int) dataDimensions[7] : null;

        // load data from flattenedDataBuffer into correctly dimensioned double array
        switch (dataDimensions.length) {
            case 1: {
                double[] data = new double[dim0];
                System.arraycopy(flattenedDataBuffer, 0, data, 0, flattenedDataBuffer.length);
                return data;
            }
            case 2: {
                double[][] data = new double[dim0][dim1];
                int index2 = 0;
                for (int i = 0; i < dim0; i++) {
                    for (int j = 0; j < dim1; j++) {
                        data[i][j] = flattenedDataBuffer[index2];
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
                            data[i][j][k] = flattenedDataBuffer[index3];
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
                                data[i][j][k][l] = flattenedDataBuffer[index4];
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
                                    data[i][j][k][l][m] = flattenedDataBuffer[index5];
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
                                        data[i][j][k][l][m][n] = flattenedDataBuffer[index6];
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
                                            data[i][j][k][l][m][n][o] = flattenedDataBuffer[index7];
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
                                                data[i][j][k][l][m][n][o][p] = flattenedDataBuffer[index8];
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

    public static void putAttribute(WritableNode node, String name, List<String> values) {
        if (values.contains(null)) {
            //
            // replace all null entries with empty strings
            // e.g. for a list of ["a", null, "abc"], replace null with ["a", "" ,"abc"]
            //
            String[] paddedValues = values.stream().map(s -> s == null ? "" : s).toArray(String[]::new);
            node.putAttribute(name, paddedValues);
        } else {
            node.putAttribute(name, values.toArray(new String[0]));
        }
    }

    public static void putAttribute(WritableNode node, String name, String value) {
        node.putAttribute(name, value);
    }

    public static void putAttribute(WritableNode node, String name, double[] value) {
        node.putAttribute(name, value);
    }

    public static Map<String, WritableGroup> addGroups(WritableGroup hdf5Group, Tree<String> tree, String ... startingPath){
        Map<String, WritableGroup> pathToGroupMapping = new HashMap<>();
        String pathString = String.join("/", startingPath);
        for (String child : tree.getChildren(startingPath)){
            WritableGroup newGroup = hdf5Group.putGroup(child);
            if (newGroup == null) throw new RuntimeException("Null group generated!");
            String childPathString = pathString + (pathString.isEmpty() ? "" : "/") + child;
            JhdfUtils.putAttribute(newGroup,"combineArchiveLocation", childPathString);
            JhdfUtils.putAttribute(newGroup,"uri", childPathString);
            pathToGroupMapping.put(childPathString, newGroup);
            String[] childPath = Arrays.copyOf(startingPath, startingPath.length + 1);
            childPath[childPath.length - 1] = child;
            pathToGroupMapping.putAll(JhdfUtils.addGroups(newGroup, tree, childPath));
        }
        return pathToGroupMapping;
    }

    public static List<Node> getChildren(HdfFile hdfFile, Group group) {
        return hdfFile.getChildren().values().stream()
                .filter(node -> node.getParent() == group).toList();
    }

    public static List<Group> getChildGroups(HdfFile hdfFile, Group group) {
        return hdfFile.getChildren().values().stream()
                .filter(node -> node.getParent() == group)
                .filter(node -> node instanceof Group)
                .map(node -> (Group)node).toList();
    }

    public static List<Dataset> getChildDatasets(HdfFile hdfFile, Group group) {
        return hdfFile.getChildren().values().stream()
                .filter(node -> node.getParent() == group)
                .filter(node -> node instanceof Dataset)
                .map(node -> (Dataset)node).toList();
    }

    public static String[] getCompoundDatasetMemberNames(Dataset cds) {
        if (!cds.isCompound()) {
            throw new IllegalArgumentException("Dataset is not compound.");
        }
        if (cds.getDataType() instanceof CompoundDataType compoundDataType) {
            return compoundDataType.getMembers().stream()
                    .map(CompoundDataType.CompoundDataMember::getName)
                    .toArray(String[]::new);
        } else {
            throw new IllegalArgumentException("Dataset is not compound.");
        }
    }
}
