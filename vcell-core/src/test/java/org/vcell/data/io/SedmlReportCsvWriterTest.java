package org.vcell.data.io;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.array.ArrayBackend;
import org.vcell.data.sedml.SedmlOutputs;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class SedmlReportCsvWriterTest {

	private static final ArrayBackend BACKEND = ArrayBackend.DEFAULT;
	private static final List<String> SCAN_THEN_TIME = List.of("k1", Dimension.TIME);

	@Test
	public void rowPerDataSetPerJob() {
		Dataset report = SedmlOutputs.report("sim.sedml", "report1", null, SedmlOutputs.OutputType.REPORT)
				.dimension(new Dimension.Numeric("k1", new double[]{0.5, 2.0}))
				.dimension(new Dimension.Indexed(Dimension.TIME, 2))
				.variable(SedmlOutputs.dataSet("time_tsk", "Time", null, "dg_time", "time", SCAN_THEN_TIME), BACKEND.wrap(new double[]{0, 1, 0, 1}, 2, 2))
				.variable(SedmlOutputs.dataSet(SedmlOutputs.RESERVED_DATA_SET_PREFIX + "S1", "S1, free", null, "dg_s1", null, SCAN_THEN_TIME),
						BACKEND.wrap(new double[]{5, 4, 6, Double.NaN}, 2, 2))
				.build();
		assertEquals("""
				time_tsk_0_,Time(0),time(0),0.0,1.0
				VCell::S1_0_,"S1, free"(0),dg_s1(0),5.0,4.0
				VCell::S1_1_,"S1, free"(1),dg_s1(1),6.0,NaN
				""", new SedmlReportCsvWriter().toCsv(report),
				"time once; reserved prefix shown as VCell::; a nameless generator by id; comma entries quoted");
	}

	@Test
	public void unscannedReportIsJobZero() {
		Dataset report = SedmlOutputs.report("sim.sedml", "r", null, SedmlOutputs.OutputType.REPORT)
				.dimension(new Dimension.Indexed(Dimension.TIME, 1))
				.variable(SedmlOutputs.dataSet("x", "x", null, "dg", "", List.of(Dimension.TIME)), BACKEND.wrap(new double[]{3}, 1))
				.build();
		assertEquals("x_0_,x(0),dg(0),3.0\n", new SedmlReportCsvWriter().toCsv(report), "an empty generator name falls back to its id");
	}
}
