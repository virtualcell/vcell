package org.vcell.data.ode;

import cbit.vcell.math.FunctionColumnDescription;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.parser.Expression;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.LangevinBatchResultSet;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.ode.ODESolverResultSet;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Selection;
import org.vcell.data.Variable;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class OdeDatasetsTest {

	/** t, A, B and a function C = A + B, three time points. */
	private static ODESolverResultSet smallResultSet() throws Exception {
		ODESolverResultSet resultSet = new ODESolverResultSet();
		resultSet.addDataColumn(new ODESolverResultSetColumnDescription("t"));
		resultSet.addDataColumn(new ODESolverResultSetColumnDescription("A", null, "Species A"));
		resultSet.addDataColumn(new ODESolverResultSetColumnDescription("B"));
		resultSet.addRow(new double[]{0.0, 1.0, 10.0});
		resultSet.addRow(new double[]{0.5, 2.0, 20.0});
		resultSet.addRow(new double[]{1.0, 3.0, 30.0});
		resultSet.addFunctionColumn(new FunctionColumnDescription(new Expression("A + B"), "C", null, "C", false));
		return resultSet;
	}

	@Test
	public void timeColumnBecomesTimeDimension() throws Exception {
		Dataset dataset = OdeDatasets.fromResultSet("small", smallResultSet());
		Dimension.Numeric time = (Dimension.Numeric) dataset.dimension(Dimension.TIME);
		assertArrayEquals(new double[]{0.0, 0.5, 1.0}, time.values());
		assertEquals(List.of("A", "B", "C"), dataset.variables().stream().map(Variable::name).toList(), "the index column is not a variable");
		assertArrayEquals(new double[]{1, 2, 3}, dataset.values("A").toArray());
		assertEquals("Species A", dataset.variable("A").attributes().get(OdeDatasets.DISPLAY_NAME));
	}

	@Test
	public void functionColumnsAreEvaluatedAndKeepTheirExpression() throws Exception {
		Dataset dataset = OdeDatasets.fromResultSet("small", smallResultSet());
		Variable c = dataset.variable("C");
		assertEquals(Variable.Role.FUNCTION, c.role());
		assertNotNull(c.expression());
		assertTrue(c.expression().contains("A") && c.expression().contains("B"));
		assertArrayEquals(new double[]{11, 22, 33}, dataset.values("C").toArray());
	}

	@Test
	public void trialColumnBecomesTrialDimension() throws Exception {
		ODESolverResultSet histogram = new ODESolverResultSet();
		histogram.addDataColumn(new ODESolverResultSetColumnDescription(SimDataConstants.HISTOGRAM_INDEX_NAME));
		histogram.addDataColumn(new ODESolverResultSetColumnDescription("X"));
		histogram.addRow(new double[]{1, 5});
		histogram.addRow(new double[]{2, 7});
		Dataset dataset = OdeDatasets.fromResultSet("trials", histogram);
		assertFalse(dataset.hasDimension(Dimension.TIME));
		assertArrayEquals(new double[]{1, 2}, ((Dimension.Numeric) dataset.dimension(Dimension.TRIAL)).values());
		assertEquals(List.of(Dimension.TRIAL), dataset.variable("X").dimensions());
	}

	@Test
	public void resultSetWithoutIndexColumnIsRejected() {
		ODESolverResultSet noIndex = new ODESolverResultSet();
		noIndex.addDataColumn(new ODESolverResultSetColumnDescription("X"));
		assertThrows(IllegalArgumentException.class, () -> OdeDatasets.fromResultSet("x", noIndex));
	}

	@Test
	public void roundTripsBackToAResultSet() throws Exception {
		ODESolverResultSet original = smallResultSet();
		ODESolverResultSet roundTripped = OdeDatasets.toResultSet(OdeDatasets.fromResultSet("small", original));
		for (String column : List.of("t", "A", "B", "C"))
			assertArrayEquals(original.extractColumn(original.findColumn(column)), roundTripped.extractColumn(roundTripped.findColumn(column)), column);
		ODESolverResultSetColumnDescription a = (ODESolverResultSetColumnDescription) roundTripped.getColumnDescriptions(roundTripped.findColumn("A"));
		assertEquals("Species A", a.getDisplayName());
	}

	@Test
	public void toResultSetNeedsOneDimension() throws Exception {
		Dataset stacked = OdeDatasets.fromJobs("jobs", List.of(smallResultSet(), smallResultSet()));
		assertThrows(IllegalArgumentException.class, () -> OdeDatasets.toResultSet(stacked));
		// narrowing to one job makes it convertible again
		assertNotNull(OdeDatasets.toResultSet(stacked.select(Selection.all().at(Dimension.JOB, 1))));
	}

	@Test
	public void langevinStatisticsBecomeStatisticDimension(@TempDir Path userDir) throws Exception {
		String base = "cbit/vcell/simdata/langevin/batch/";
		for (String file : List.of("SimID_303404574_0_.log", "SimID_303404574_0_0.log", "SimID_303404574_0_.ida", "SimID_303404574_0_.functions",
				"SimID_303404574_0__Avg.ida", "SimID_303404574_0__Min.ida", "SimID_303404574_0__Max.ida", "SimID_303404574_0__Std.ida",
				"SimID_303404574_0__clusters_counts.csv", "SimID_303404574_0__clusters_mean.csv", "SimID_303404574_0__clusters_overall.csv"))
			Files.write(userDir.resolve(file), Resources.toByteArray(Resources.getResource(base + file)));
		VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(
				new VCSimulationIdentifier(new KeyValue("303404574"), new User("temp", new KeyValue("1"))), 0);
		Cachetable cache = new Cachetable(10 * Cachetable.minute, 100_000_000);
		cache.put(vcdID, new SimulationData(vcdID, userDir.toFile(), userDir.toFile(), null));
		LangevinBatchResultSet batch = new DataSetControllerImpl(cache, userDir.toFile(), userDir.toFile()).getLangevinBatchResultSet(vcdID);

		Dataset statistics = OdeDatasets.fromLangevinBatch("langevin", batch);
		assertEquals(OdeDatasets.LANGEVIN_STATISTICS, ((Dimension.Labeled) statistics.dimension(Dimension.STATISTIC)).labels());
		String variable = statistics.variables().get(0).name();
		assertEquals(List.of(Dimension.STATISTIC, Dimension.TIME), statistics.variable(variable).dimensions());
		ODESimData max = batch.getOdeSimDataMax();
		assertArrayEquals(max.extractColumn(max.findColumn(variable)),
				statistics.select(Selection.all().at(Dimension.STATISTIC, 2)).values(variable).toArray(), "statistic 2 is max");
	}

	@Test
	public void parameterScanJobsStackAlongJobDimension(@TempDir Path temp) throws Exception {
		List<ODESimData> jobs = new ArrayList<>();
		File noFunctions = Files.writeString(temp.resolve("none.functions"), "").toFile();
		VCSimulationIdentifier simulation = new VCSimulationIdentifier(new KeyValue("284673710"), new User("temp", new KeyValue("1")));
		for (int job = 0; job < 3; job++) {
			String name = "SimID_284673710_" + job + "_.ida";
			File file = temp.resolve(name).toFile();
			Files.write(file.toPath(), Resources.toByteArray(Resources.getResource("cbit/vcell/simdata/" + name)));
			jobs.add(ODESimData.readIDADataFile(new VCSimulationDataIdentifier(simulation, job), file, 0, noFunctions));
		}
		Dataset scan = OdeDatasets.fromJobs("scan", jobs);

		assertEquals(3, scan.dimension(Dimension.JOB).size());
		String variable = scan.variables().get(0).name();
		assertEquals(List.of(Dimension.JOB, Dimension.TIME), scan.variable(variable).dimensions());
		for (int job = 0; job < 3; job++) {
			ODESimData expected = jobs.get(job);
			assertArrayEquals(expected.extractColumn(expected.findColumn(variable)),
					scan.select(Selection.all().at(Dimension.JOB, job)).values(variable).toArray(), "job " + job);
		}
	}
}
