package cbit.vcell.simdata;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;

/**
 * Exports a small table and reads the file back, so the structure the exporter promises — one group
 * per parameter-scan set, a [column][row] dataset of doubles, and the labelling attributes a
 * consumer reads — is pinned rather than assumed.
 */
@Tag("Fast")
public class UiTableExporterToHDF5Test {

	private static final String[] COLUMN_NAMES = { "t", "A", "B" };

	/** three timepoints of two variables, as the plot table holds them */
	private static Object[][] table() {
		return new Object[][] {
				{ 0.0, 1.0, 10.0 },
				{ 0.5, 2.0, 20.0 },
				{ 1.0, 3.0, 30.0 },
		};
	}

	@Test
	public void exportsATableThatReadsBack() throws Exception {
		File exported = UiTableExporterToHDF5.exportTableToHDF5(false, "0", new int[] { 0, 1, 2 },
				new int[] { 0, 1, 2 }, "t", "a description", COLUMN_NAMES,
				new String[0], new Double[0][0], table());
		exported.deleteOnExit();
		Assertions.assertTrue(exported.length() > 0, "the export is not empty");

		try (HdfFile file = new HdfFile(exported.toPath())) {
			Group set = (Group) file.getChildren().get("Set 0");
			Assertions.assertNotNull(set, "one group per parameter-scan set");

			Dataset data = (Dataset) set.getChildren().get("data");
			Assertions.assertArrayEquals(new int[] { 3, 3 }, data.getDimensions(),
					"[column][row]: the x column plus two variables, three timepoints each");

			double[][] values = (double[][]) data.getData();
			Assertions.assertArrayEquals(new double[] { 0.0, 0.5, 1.0 }, values[0], 1e-12, "the x column");
			Assertions.assertArrayEquals(new double[] { 1.0, 2.0, 3.0 }, values[1], 1e-12);
			Assertions.assertArrayEquals(new double[] { 10.0, 20.0, 30.0 }, values[2], 1e-12);

			Assertions.assertEquals("ODE Data Export", attribute(data, "_type"));
			Assertions.assertEquals("report", attribute(data, "id"));
			Assertions.assertEquals(List.of("t", "A", "B"), Arrays.asList((String[]) data.getAttribute("dataSetLabels").getData()));
			Assertions.assertEquals(List.of("float64", "float64", "float64"),
					Arrays.asList((String[]) data.getAttribute("dataSetDataTypes").getData()));

			Assertions.assertEquals("a description",
					((String[]) file.getAttribute("dataSourceDescr").getData())[0]);
		}
	}

	private static String attribute(Dataset dataset, String name) {
		Object value = dataset.getAttribute(name).getData();
		return value instanceof String[] ? ((String[]) value)[0] : String.valueOf(value);
	}
}
