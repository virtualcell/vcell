package org.vcell.data.io;

import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vcell.data.Dimension;
import org.vcell.data.Topology;
import org.vcell.data.array.ArrayBackend;
import org.vcell.data.sedml.SedmlOutputs;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class BiosimulationsHdf5DatasetWriterTest {

	private static final ArrayBackend BACKEND = ArrayBackend.DEFAULT;
	private static final List<String> SCAN_THEN_TIME = List.of("k1", Dimension.TIME);

	/** A report of two data sets over a 2-value scan of k1 and 3 time points; value = 100 * dataSet + 10 * job + point. */
	private static org.vcell.data.Dataset scannedReport() {
		return SedmlOutputs.report("dir/sim.sedml", "report1", "Report One", SedmlOutputs.OutputType.REPORT)
				.dimension(new Dimension.Numeric("k1", new double[]{0.5, 2.0}))
				.dimension(new Dimension.Indexed(Dimension.TIME, 3))
				.variable(SedmlOutputs.dataSet("time", "Time", null, null, null, SCAN_THEN_TIME), BACKEND.wrap(new double[]{0, 1, 2, 10, 11, 12}, 2, 3))
				.variable(SedmlOutputs.dataSet("S1", "S1", "Species 1", null, null, SCAN_THEN_TIME), BACKEND.wrap(new double[]{100, 101, 102, 110, 111, 112}, 2, 3))
				.build();
	}

	/** Two curves sharing their x values (time), each a data generator padded to the plot's 2 points. */
	private static org.vcell.data.Dataset plot() {
		List<String> time = List.of(Dimension.TIME);
		return SedmlOutputs.report("other.sedml", SedmlOutputs.PLOT_PREFIX + "plot1", null, SedmlOutputs.OutputType.PLOT_2D)
				.dimension(new Dimension.Indexed(Dimension.TIME, 2))
				.variable(SedmlOutputs.xData("dg_time", null, SedmlOutputs.TIME_SYMBOL, time), BACKEND.wrap(new double[]{0, Double.NaN}, 2))
				.variable(SedmlOutputs.curve("c1", "Curve 1", "dg_time", "dg_s1", "S1", time), BACKEND.wrap(new double[]{1, Double.NaN}, 2))
				.variable(SedmlOutputs.curve("c2", null, "dg_time", "dg_s2", null, time), BACKEND.wrap(new double[]{2, Double.NaN}, 2))
				.build();
	}

	@Test
	public void scannedReportLayout(@TempDir Path temp) throws Exception {
		Path file = temp.resolve("reports.h5");
		new BiosimulationsHdf5DatasetWriter().write(List.of(scannedReport()), file);
		try (HdfFile hdf = new HdfFile(file)) {
			assertEquals("dir", hdf.getChild("dir").getAttribute("uri").getData());
			assertEquals("dir/sim.sedml", hdf.getByPath("dir/sim.sedml").getAttribute("combineArchiveLocation").getData());

			Dataset report = hdf.getDatasetByPath("dir/sim.sedml/report1");
			assertArrayEquals(new int[]{2, 2, 1, 3}, report.getDimensions(), "[data set, scan, sub-task, time]");
			double[][][][] data = (double[][][][]) report.getData();
			assertArrayEquals(new double[]{110, 111, 112}, data[1][1][0]);
			assertArrayEquals(new double[]{0, 1, 2}, data[0][0][0]);

			assertEquals("SedReport", report.getAttribute("_type").getData());
			assertArrayEquals(new String[]{"time", "S1"}, (String[]) report.getAttribute("sedmlDataSetIds").getData());
			assertArrayEquals(new String[]{"", "Species 1"}, (String[]) report.getAttribute("sedmlDataSetNames").getData(), "missing names are empty");
			assertArrayEquals(new String[]{"Time", "S1"}, (String[]) report.getAttribute("sedmlDataSetLabels").getData());
			assertArrayEquals(new String[]{"float64", "float64"}, (String[]) report.getAttribute("sedmlDataSetDataTypes").getData());
			assertArrayEquals(new String[]{"3", "3", "3", "3"}, (String[]) report.getAttribute("sedmlDataSetShapes").getData(), "one per data set per job");
			assertArrayEquals(new String[]{"[0.5, 2.0]"}, (String[]) report.getAttribute("sedmlRepeatedTaskValues").getData());
			assertArrayEquals(new String[]{"k1"}, (String[]) report.getAttribute("sedmlRepeatedTaskParameterNames").getData());
			assertEquals("report1", report.getAttribute("sedmlId").getData());
			assertEquals("Report One", report.getAttribute("sedmlName").getData());
			assertEquals("dir/sim.sedml/report1", report.getAttribute("uri").getData());
		}
	}

	@Test
	public void subTaskDimensionsCanBeLeftOut(@TempDir Path temp) throws Exception {
		Path file = temp.resolve("reports.h5");
		new BiosimulationsHdf5DatasetWriter(false).write(List.of(scannedReport()), file);
		try (HdfFile hdf = new HdfFile(file)) {
			assertArrayEquals(new int[]{2, 2, 3}, hdf.getDatasetByPath("dir/sim.sedml/report1").getDimensions());
		}
	}

	@Test
	public void unscannedPlotAndSeveralDocuments(@TempDir Path temp) throws Exception {
		Path file = temp.resolve("reports.h5");
		new BiosimulationsHdf5DatasetWriter().write(List.of(scannedReport(), plot()), file);
		try (HdfFile hdf = new HdfFile(file)) {
			Dataset plot = hdf.getDatasetByPath("other.sedml/plot1");
			assertArrayEquals(new int[]{3, 2}, plot.getDimensions(), "one data set per data generator, x first");
			double[][] data = (double[][]) plot.getData();
			assertEquals(0, data[0][0]);
			assertEquals(2, data[2][0]);
			assertTrue(Double.isNaN(data[1][1]), "padding survives");
			assertArrayEquals(new String[]{"dg_time", "dg_s1", "dg_s2"}, (String[]) plot.getAttribute("sedmlDataSetIds").getData());
			assertArrayEquals(new String[]{"__plot__plot1.c1::X", "__plot__plot1.c1::X", "__plot__plot1.c2::X"},
					(String[]) plot.getAttribute("sedmlDataSetLabels").getData(), "labelled by the first curve using each, x or y");
			assertArrayEquals(new String[]{"", "S1", ""}, (String[]) plot.getAttribute("sedmlDataSetNames").getData(), "data generator names");
			assertEquals("SedPlot2D", plot.getAttribute("_type").getData());
			assertEquals("plot1", plot.getAttribute("sedmlName").getData(), "an unnamed output is named by its id");
			assertNull(plot.getAttribute("sedmlRepeatedTaskValues"));
			assertNull(plot.getAttribute("sedmlRepeatedTaskParameterNames"));
		}
	}

	@Test
	public void vcellPrefixesAreLeftOutOfIdsAndTypeFollowsThem(@TempDir Path temp) throws Exception {
		List<String> time = List.of(Dimension.TIME);
		org.vcell.data.Dataset prefixed = SedmlOutputs.report("a.sedml", "__plot__p1", "P", SedmlOutputs.OutputType.PLOT_2D)
				.dimension(new Dimension.Indexed(Dimension.TIME, 1))
				.variable(SedmlOutputs.xData("t", null, null, time), BACKEND.wrap(new double[]{0}, 1))
				.variable(SedmlOutputs.curve("c", null, "t", SedmlOutputs.RESERVED_DATA_SET_PREFIX + "p1_S1", null, time), BACKEND.wrap(new double[]{1}, 1))
				.build();
		org.vcell.data.Dataset foreignPlot = SedmlOutputs.report("a.sedml", "plotA", null, SedmlOutputs.OutputType.PLOT_2D)
				.dimension(new Dimension.Indexed(Dimension.TIME, 1))
				.variable(SedmlOutputs.xData("t", null, null, time), BACKEND.wrap(new double[]{0}, 1))
				.variable(SedmlOutputs.curve("c", null, "t", "y", null, time), BACKEND.wrap(new double[]{1}, 1))
				.build();
		Path file = temp.resolve("reports.h5");
		new BiosimulationsHdf5DatasetWriter().write(List.of(prefixed, foreignPlot), file);
		try (HdfFile hdf = new HdfFile(file)) {
			Dataset p1 = hdf.getDatasetByPath("a.sedml/p1");
			assertArrayEquals(new String[]{"t", "S1"}, (String[]) p1.getAttribute("sedmlDataSetIds").getData());
			assertEquals("SedPlot2D", p1.getAttribute("_type").getData());
			assertEquals("SedReport", hdf.getDatasetByPath("a.sedml/plotA").getAttribute("_type").getData(),
					"typed by VCell's plot prefix, as the CLI always has (issue 2192)");
		}
	}

	@Test
	public void rejectsWhatTheLayoutCannotHold(@TempDir Path temp) {
		BiosimulationsHdf5DatasetWriter writer = new BiosimulationsHdf5DatasetWriter();
		org.vcell.data.Dataset unlabeledScan = SedmlOutputs.report("a.sedml", "r", null, SedmlOutputs.OutputType.REPORT)
				.dimension(new Dimension.Indexed(Dimension.JOB, 2))
				.dimension(new Dimension.Indexed(Dimension.TIME, 1))
				.variable(SedmlOutputs.dataSet("x", "x", null, null, null, List.of(Dimension.JOB, Dimension.TIME)), BACKEND.wrap(new double[]{1, 2}, 2, 1))
				.build();
		assertThrows(IllegalArgumentException.class, () -> writer.write(List.of(unlabeledScan), temp.resolve("a.h5")), "scan dimensions need values");

		org.vcell.data.Dataset ragged = SedmlOutputs.report("a.sedml", "r", null, SedmlOutputs.OutputType.REPORT)
				.dimension(new Dimension.Numeric("k", new double[]{1, 2}))
				.dimension(new Dimension.Indexed(Dimension.TIME, 1))
				.variable(SedmlOutputs.dataSet("x", "x", null, null, null, List.of("k", Dimension.TIME)), BACKEND.wrap(new double[]{1, 2}, 2, 1))
				.variable(SedmlOutputs.dataSet("y", "y", null, null, null, List.of(Dimension.TIME)), BACKEND.wrap(new double[]{1}, 1))
				.build();
		assertThrows(IllegalArgumentException.class, () -> writer.write(List.of(ragged), temp.resolve("b.h5")), "data sets must stack");

		org.vcell.data.Dataset notSedml = org.vcell.data.Dataset.builder("plain").build();
		assertThrows(IllegalArgumentException.class, () -> writer.write(List.of(notSedml), temp.resolve("c.h5")));

		org.vcell.data.Dataset spatial = SedmlOutputs.report("a.sedml", "r", null, SedmlOutputs.OutputType.REPORT)
				.dimension(new Dimension.Indexed("x", 2))
				.topology(new Topology() {
					public String kind() { return "test"; }
					public Set<String> spatialDimensions() { return Set.of("x"); }
				})
				.variable(SedmlOutputs.dataSet("v", "v", null, null, null, List.of("x")), BACKEND.wrap(new double[]{1, 2}, 2))
				.build();
		assertThrows(UnsupportedOperationException.class, () -> writer.write(List.of(spatial), temp.resolve("d.h5")));
	}
}
