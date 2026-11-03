package org.vcell.data.io;

import io.jhdf.HdfFile;
import io.jhdf.api.Attribute;
import io.jhdf.api.Node;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Variable;
import org.vcell.data.array.ArrayBackend;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class DatasetWritersTest {

	private static final ArrayBackend BACKEND = ArrayBackend.DEFAULT;

	/** A over (time); S over (statistic, time); p a coordinate over (statistic). */
	private static Dataset sample() {
		return Dataset.builder("sample")
				.dimension(new Dimension.Labeled(Dimension.STATISTIC, List.of("mean", "max")))
				.dimension(new Dimension.Numeric(Dimension.TIME, new double[]{0.0, 0.5}, "s"))
				.variable(new Variable("A", List.of(Dimension.TIME), "uM", Variable.Role.DATA, null, "cyt", Map.of("displayName", "Species A")),
						BACKEND.wrap(new double[]{1, 2}, 2))
				.variable(Variable.data("S", List.of(Dimension.STATISTIC, Dimension.TIME)), BACKEND.wrap(new double[]{10, 20, 30, 40}, 2, 2))
				.variable(new Variable("F", List.of(Dimension.TIME), null, Variable.Role.FUNCTION, "A * 2", null, null),
						BACKEND.wrap(new double[]{2, 4}, 2))
				.variable(new Variable("p", List.of(Dimension.STATISTIC), null, Variable.Role.COORDINATE, null, null, null),
						BACKEND.wrap(new double[]{7, 8}, 2))
				.attribute("simulation", "test")
				.build();
	}

	@Test
	public void csvTableExpandsExtraDimensionsIntoColumns() {
		String csv = new CsvDatasetWriter().toCsv(sample());
		assertEquals("""
				time,A,S[statistic=mean],S[statistic=max],F
				0.0,1.0,10.0,30.0,2.0
				0.5,2.0,20.0,40.0,4.0
				""", csv);
	}

	@Test
	public void csvQuotesHeadersThatContainCommas() {
		Dataset twoExtra = Dataset.builder("two")
				.dimension(new Dimension.Indexed(Dimension.JOB, 1))
				.dimension(new Dimension.Labeled(Dimension.STATISTIC, List.of("mean")))
				.dimension(new Dimension.Numeric(Dimension.TIME, new double[]{0.0}))
				.variable(Variable.data("X", List.of(Dimension.JOB, Dimension.STATISTIC, Dimension.TIME)), BACKEND.wrap(new double[]{5}, 1, 1, 1))
				.build();
		assertEquals("""
				time,"X[job=0,statistic=mean]"
				0.0,5.0
				""", new CsvDatasetWriter().toCsv(twoExtra));
	}

	@Test
	public void csvTableTransposed() {
		String csv = new CsvDatasetWriter(CsvDatasetWriter.Layout.TABLE, true, null).toCsv(sample());
		assertEquals("""
				time,0.0,0.5
				A,1.0,2.0
				S[statistic=mean],10.0,20.0
				S[statistic=max],30.0,40.0
				F,2.0,4.0
				""", csv);
	}

	@Test
	public void csvRowsCanFollowAnotherDimension() {
		Dataset statisticsOnly = sample().select(org.vcell.data.Selection.all().variables("S"));
		String csv = new CsvDatasetWriter(CsvDatasetWriter.Layout.TABLE, false, Dimension.STATISTIC).toCsv(statisticsOnly);
		assertEquals("""
				statistic,S[time=0.0],S[time=0.5]
				mean,10.0,20.0
				max,30.0,40.0
				""", csv);
	}

	@Test
	public void csvLegacyLayoutNeedsTimeOrTrialRows() {
		CsvDatasetWriter legacy = new CsvDatasetWriter(CsvDatasetWriter.Layout.VCELL_LEGACY, false, Dimension.STATISTIC);
		assertThrows(IllegalArgumentException.class, () -> legacy.toCsv(sample().select(org.vcell.data.Selection.all().variables("S"))));
	}

	@Test
	public void hdf5RoundTrip(@TempDir Path temp) throws Exception {
		Path file = temp.resolve("sample.h5");
		new Hdf5DatasetWriter().write(sample(), file);
		assertTrue(Files.size(file) > 0);

		try (HdfFile hdf = new HdfFile(file)) {
			assertEquals("sample", hdf.getAttribute("name").getData());
			assertEquals("nonspatial", hdf.getAttribute("topology").getData());
			assertEquals("test", hdf.getAttribute("simulation").getData());

			assertArrayEquals(new double[]{0.0, 0.5}, (double[]) hdf.getDatasetByPath("dimensions/time").getData());
			assertEquals("s", hdf.getDatasetByPath("dimensions/time").getAttribute("unit").getData());
			assertArrayEquals(new String[]{"mean", "max"}, (String[]) hdf.getDatasetByPath("dimensions/statistic").getData());

			Node a = hdf.getDatasetByPath("variables/A");
			assertArrayEquals(new double[]{1, 2}, (double[]) hdf.getDatasetByPath("variables/A").getData());
			assertArrayEquals(new String[]{"time"}, (String[]) a.getAttribute("dimensions").getData());
			assertEquals("uM", a.getAttribute("unit").getData());
			assertEquals("cyt", a.getAttribute("domain").getData());
			assertEquals("Species A", a.getAttribute("displayName").getData());

			double[][] s = (double[][]) hdf.getDatasetByPath("variables/S").getData();
			assertArrayEquals(new double[]{10, 20}, s[0]);
			assertArrayEquals(new double[]{30, 40}, s[1]);
			assertArrayEquals(new String[]{"statistic", "time"}, (String[]) hdf.getDatasetByPath("variables/S").getAttribute("dimensions").getData());

			Attribute expression = hdf.getDatasetByPath("variables/F").getAttribute("expression");
			assertEquals("A * 2", expression.getData());
			assertEquals("FUNCTION", hdf.getDatasetByPath("variables/F").getAttribute("role").getData());
			assertEquals("COORDINATE", hdf.getDatasetByPath("variables/p").getAttribute("role").getData(), "coordinate variables are written too");
		}
	}

	@Test
	public void hdf5RejectsReservedAttributeNames(@TempDir Path temp) {
		Dataset clash = Dataset.builder("clash").attribute("topology", "mine").build();
		assertThrows(IllegalArgumentException.class, () -> new Hdf5DatasetWriter().write(clash, temp.resolve("clash.h5")));
	}
}
