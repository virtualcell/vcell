package cbit.vcell.export.server;

import io.jhdf.HdfFile;
import io.jhdf.WritableHdfFile;
import io.jhdf.api.WritableGroup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Curve values used to be collected into a list and written once at the end. They are now written a row at a time
 * as they are produced, which has to put exactly the same numbers in exactly the same places.
 */
@Tag("Fast")
public class ASCIIExporterCurveValuesStreamingTest {

	private static final int ROWS = 4;
	private static final int COLUMNS = 3;

	/** What the old path produced: collect everything flat, reshape by the dims, write once. */
	private static double[][] writtenAllAtOnce(List<Double> values) throws Exception {
		Path file = Files.createTempFile("collected", ".hdf5");
		Files.deleteIfExists(file);
		try (WritableHdfFile writableHdfFile = HdfFile.write(file)) {
			ASCIIExporter.insertDoubles(writableHdfFile.putGroup("g"), "CURVEVALS",
				new long[]{ROWS, COLUMNS}, values);
		}
		try (HdfFile hdfFile = new HdfFile(file)) {
			return (double[][]) hdfFile.getDatasetByPath("g/CURVEVALS").getData();
		} finally {
			Files.deleteIfExists(file);
		}
	}

	/** What the new path produces: hand the same values over one at a time. */
	private static double[][] writtenAsProduced(List<Double> values) throws Exception {
		Path file = Files.createTempFile("streamed", ".hdf5");
		Files.deleteIfExists(file);
		try (WritableHdfFile writableHdfFile = HdfFile.write(file)) {
			WritableGroup group = writableHdfFile.putGroup("g");
			try (ASCIIExporter.RowStreamer streamer =
					 new ASCIIExporter.RowStreamer(group, "CURVEVALS", ROWS, COLUMNS)) {
				for (Double value : values) {
					streamer.add(value);
				}
			}
		}
		try (HdfFile hdfFile = new HdfFile(file)) {
			return (double[][]) hdfFile.getDatasetByPath("g/CURVEVALS").getData();
		} finally {
			Files.deleteIfExists(file);
		}
	}

	private static List<Double> values(int count) {
		List<Double> values = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			values.add(i * 1.5 - 2);
		}
		return values;
	}

	@Test
	public void streamingRowsMatchesCollectingThemFirst() throws Exception {
		List<Double> values = values(ROWS * COLUMNS);
		assertArrayEquals(writtenAllAtOnce(values), writtenAsProduced(values));
	}

	/**
	 * A membrane curve appends a second block of values after the first, so more arrive than the dataset holds.
	 * Writing a longer buffer into a bounded dataspace kept the first of them, and so does this.
	 */
	@Test
	public void valuesBeyondTheDatasetAreDropped() throws Exception {
		List<Double> tooMany = values(ROWS * COLUMNS + 7);
		assertArrayEquals(writtenAllAtOnce(tooMany), writtenAsProduced(tooMany));
	}

	/**
	 * A curve that produced fewer values than the dataset holds still has to leave a complete dataset behind,
	 * because a chunk that is never written fails the file's close. The old path had no answer here at all: it
	 * reshaped the flat list by the dims and ran off the end of it, so this is strictly better behaved rather
	 * than a behaviour that is being preserved.
	 */
	@Test
	public void aShortCurveStillLeavesACompleteDataset() throws Exception {
		List<Double> tooFew = values(ROWS * COLUMNS - 4);
		double[][] streamed = writtenAsProduced(tooFew);

		assertArrayEquals(new int[]{ROWS, COLUMNS}, new int[]{streamed.length, streamed[0].length});
		assertArrayEquals(new double[]{-2.0, -0.5, 1.0}, streamed[0]);
		assertArrayEquals(new double[]{0.0, 0.0, 0.0}, streamed[ROWS - 1]);
	}
}
