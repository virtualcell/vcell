package cbit.vcell.export.server;

import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.WritableGroup;
import io.jhdf.api.Dataset;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Characterises the small datasets and attributes the ASCII/HDF5 export writes alongside its spatial data - the
 * time array, curve indexes and distances, point labels and so on.
 * <p>
 * These say what a downloaded export actually contains, and are the reference for moving this writing off the
 * native hdf.hdf5lib binding to jhdf (jhdf#654). The shapes matter: several are written from a flat Java list
 * with two dimensional dims, so the data is reshaped on the way in.
 */
@Tag("Fast")
public class ASCIIExporterHdf5HelpersTest {

	/** Runs something against a group in a throwaway HDF5 file and hands back the file to read. */
	private static Path writtenBy(GroupWriter groupWriter) throws Exception {
		Path file = Files.createTempFile("asciiExportHelpers", ".hdf5");
		Files.deleteIfExists(file);
		try (WritableHdfFile writableHdfFile = HdfFile.write(file)) {
			groupWriter.write(writableHdfFile.putGroup("group"));
		}
		return file;
	}

	@FunctionalInterface
	private interface GroupWriter {
		void write(WritableGroup group) throws Exception;
	}

	@Test
	public void doublesFromAnArrayAreOneDimensional() throws Exception {
		double[] times = {0.0, 0.5, 1.25};
		Path file = writtenBy(group ->
			ASCIIExporter.insertDoubles(group, "TIMES", new long[]{times.length}, times));

		try (HdfFile hdfFile = new HdfFile(file)) {
			Dataset dataset = hdfFile.getDatasetByPath("group/TIMES");
			assertArrayEquals(new int[]{3}, dataset.getDimensions());
			assertArrayEquals(times, (double[]) dataset.getData());
		}
		Files.deleteIfExists(file);
	}

	/** The values arrive flat but the dataset is two dimensional, so the list is reshaped by the dims. */
	@Test
	public void doublesFromAListAreReshapedByTheDims() throws Exception {
		List<Double> flat = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0));
		Path file = writtenBy(group ->
			ASCIIExporter.insertDoubles(group, "CURVEVALS", new long[]{2, 3}, flat));

		try (HdfFile hdfFile = new HdfFile(file)) {
			Dataset dataset = hdfFile.getDatasetByPath("group/CURVEVALS");
			assertArrayEquals(new int[]{2, 3}, dataset.getDimensions());
			assertArrayEquals(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}},
				(double[][]) dataset.getData());
		}
		Files.deleteIfExists(file);
	}

	@Test
	public void intsAreOneDimensional() throws Exception {
		int[] bounds = {3, 11};
		Path file = writtenBy(group ->
			ASCIIExporter.insertInts(group, "TIMEBOUNDS", new long[]{bounds.length}, bounds));

		try (HdfFile hdfFile = new HdfFile(file)) {
			Dataset dataset = hdfFile.getDatasetByPath("group/TIMEBOUNDS");
			assertArrayEquals(new int[]{2}, dataset.getDimensions());
			assertArrayEquals(bounds, (int[]) dataset.getData());
		}
		Files.deleteIfExists(file);
	}

	/**
	 * Strings are written fixed width, padded to the longest, with the dims the caller gave - which for point
	 * labels is a row rather than a plain list.
	 */
	@Test
	public void stringsArePaddedToTheLongestAndKeepTheGivenShape() throws Exception {
		List<String> labels = new ArrayList<>(Arrays.asList("point one", "p2", "third point!"));
		Path file = writtenBy(group ->
			ASCIIExporter.insertStrings(group, "POINTINFO", new long[]{1, labels.size()}, labels));

		try (HdfFile hdfFile = new HdfFile(file)) {
			Dataset dataset = hdfFile.getDatasetByPath("group/POINTINFO");
			assertArrayEquals(new int[]{1, 3}, dataset.getDimensions());
			String[][] values = (String[][]) dataset.getData();
			assertEquals("point one", values[0][0].trim());
			assertEquals("p2", values[0][1].trim());
			assertEquals("third point!", values[0][2].trim());
		}
		Files.deleteIfExists(file);
	}

	@Test
	public void attributesAreScalarStrings() throws Exception {
		Path file = writtenBy(group ->
			ASCIIExporter.insertAttribute(group, "CURVECROSSMEMBRINDEX Info", "crosses membrane at 3"));

		try (HdfFile hdfFile = new HdfFile(file)) {
			Object value = hdfFile.getByPath("group").getAttribute("CURVECROSSMEMBRINDEX Info").getData();
			assertEquals("crosses membrane at 3", value.toString().trim());
		}
		Files.deleteIfExists(file);
	}
}
