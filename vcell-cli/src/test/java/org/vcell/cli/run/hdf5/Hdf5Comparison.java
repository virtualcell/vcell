package org.vcell.cli.run.hdf5;

import io.jhdf.HdfFile;
import io.jhdf.api.Attribute;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.BiFunction;

/**
 * Compares two HDF5 files by content: the same groups and datasets at the same paths, the same dataset shapes and
 * values (exactly, NaN equal to NaN), and the same attributes with the same values. Order of children and attributes
 * is not compared, as it carries no meaning in these files.
 */
public final class Hdf5Comparison {
    private Hdf5Comparison() { }

    /** @return a description of every difference, empty when the files hold the same content */
    public static List<String> differences(Path expected, Path actual) {
        return Hdf5Comparison.differences(expected, actual, (name, value) -> value);
    }

    /**
     * @param expectedAttributes applied to each expected attribute's (name, value) before comparing, for known
     *                           differences the comparison should look past
     * @return a description of every difference, empty when the files hold the same content
     */
    public static List<String> differences(Path expected, Path actual, BiFunction<String, Object, Object> expectedAttributes) {
        List<String> differences = new ArrayList<>();
        try (HdfFile expectedFile = new HdfFile(expected); HdfFile actualFile = new HdfFile(actual)) {
            Hdf5Comparison.compareNodes(expectedFile, actualFile, expectedAttributes, differences);
        }
        return differences;
    }

    /** @return the shape of every dataset in the file, by path */
    public static Map<String, int[]> datasetShapes(Path file) {
        Map<String, int[]> shapes = new java.util.TreeMap<>();
        try (HdfFile hdf = new HdfFile(file)) {
            Hdf5Comparison.collectShapes(hdf, shapes);
        }
        return shapes;
    }

    private static void collectShapes(Node node, Map<String, int[]> shapes) {
        if (node instanceof Dataset dataset) shapes.put(dataset.getPath(), dataset.getDimensions());
        else if (node instanceof Group group) for (Node child : group.getChildren().values()) Hdf5Comparison.collectShapes(child, shapes);
    }

    private static void compareNodes(Node expected, Node actual, BiFunction<String, Object, Object> expectedAttributes, List<String> differences) {
        String path = expected.getPath();
        Hdf5Comparison.compareAttributes(path, expected.getAttributes(), actual.getAttributes(), expectedAttributes, differences);
        if (expected instanceof Group expectedGroup) {
            if (!(actual instanceof Group actualGroup)) {
                differences.add(path + ": expected a group");
                return;
            }
            Map<String, Node> expectedChildren = expectedGroup.getChildren(), actualChildren = actualGroup.getChildren();
            TreeSet<String> names = new TreeSet<>(expectedChildren.keySet());
            names.addAll(actualChildren.keySet());
            for (String name : names) {
                Node expectedChild = expectedChildren.get(name), actualChild = actualChildren.get(name);
                if (expectedChild == null) differences.add(path + name + ": unexpected");
                else if (actualChild == null) differences.add(path + name + ": missing");
                else Hdf5Comparison.compareNodes(expectedChild, actualChild, expectedAttributes, differences);
            }
        } else if (expected instanceof Dataset expectedDataset) {
            if (!(actual instanceof Dataset actualDataset)) {
                differences.add(path + ": expected a dataset");
                return;
            }
            if (!Arrays.equals(expectedDataset.getDimensions(), actualDataset.getDimensions()))
                differences.add(path + ": shape " + Arrays.toString(actualDataset.getDimensions()) + ", expected " + Arrays.toString(expectedDataset.getDimensions()));
            else if (!Objects.deepEquals(expectedDataset.getData(), actualDataset.getData()))
                differences.add(path + ": values differ");
        }
    }

    private static void compareAttributes(String path, Map<String, Attribute> expected, Map<String, Attribute> actual,
                                          BiFunction<String, Object, Object> expectedAttributes, List<String> differences) {
        TreeSet<String> names = new TreeSet<>(expected.keySet());
        names.addAll(actual.keySet());
        for (String name : names) {
            Attribute expectedAttribute = expected.get(name), actualAttribute = actual.get(name);
            if (expectedAttribute == null) differences.add(path + "@" + name + ": unexpected");
            else if (actualAttribute == null) differences.add(path + "@" + name + ": missing");
            else {
                Object expectedValue = expectedAttributes.apply(name, expectedAttribute.getData());
                if (!Objects.deepEquals(expectedValue, actualAttribute.getData()))
                    differences.add(path + "@" + name + ": " + Hdf5Comparison.describe(actualAttribute.getData()) + ", expected " + Hdf5Comparison.describe(expectedValue));
            }
        }
    }

    private static String describe(Object value) {
        if (value instanceof Object[] array) return Arrays.deepToString(array);
        if (value instanceof double[] array) return Arrays.toString(array);
        return String.valueOf(value);
    }
}
