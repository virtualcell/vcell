package org.vcell.data.io;

import cbit.vcell.export.server.ASCIIExporter;
import cbit.vcell.export.server.ASCIISpecs;
import cbit.vcell.export.server.ExportEnums;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportOutput;
import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.FileDataContainerManager;
import cbit.vcell.export.server.GeometrySpecs;
import cbit.vcell.export.server.JobRequest;
import cbit.vcell.export.server.TimeSpecs;
import cbit.vcell.export.server.VariableSpecs;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.vcell.data.Dataset;
import org.vcell.data.Dimension;
import org.vcell.data.Selection;
import org.vcell.data.Variable;
import org.vcell.data.ode.OdeDatasets;
import org.vcell.util.Coordinate;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Golden comparison: the existing {@link ASCIIExporter} and the new pipeline (adapter → select → writer) export the
 * same nonspatial results, read through the same {@link DataServerImpl}, and must produce the same CSV body.
 * The old export prefixes a simulation description header the new writer does not produce; everything from
 * "Variable values over" onwards is compared byte for byte.
 */
@Tag("Fast")
public class OdeCsvGoldenTest {

	private static final User USER = new User("temp", new KeyValue("1"));

	/**
	 * A fixture: the simulation key, the resource files that make up its solver output, and optionally a .functions
	 * file to write alongside them.
	 */
	private record Fixture(String simKey, String resourceDirectory, List<String> files, String functions) {
		@Override
		public String toString() {
			return this.simKey;
		}
	}

	private static final Fixture EULER_ODE = new Fixture("303690651", "cbit/vcell/simdata/langevin/euler/",
			List.of("SimID_303690651_0_.log", "SimID_303690651_0_.ode"), null);
	// IDA output plus a user function over two of its columns, so both exporters must evaluate a function column
	private static final Fixture IDA_WITH_FUNCTIONS = new Fixture("303404574", "cbit/vcell/simdata/langevin/batch/",
			List.of("SimID_303404574_0_.log", "SimID_303404574_0_.ida"),
			"Ligand_sum; (FREE_Ligand + (2.0 * BOUND_Ligand)); ; Nonspatial_VariableType; false\n");

	static Stream<Arguments> cases() {
		List<Arguments> cases = new ArrayList<>();
		for (Fixture fixture : List.of(EULER_ODE, IDA_WITH_FUNCTIONS)) {
			for (boolean switchRowsColumns : new boolean[]{false, true}) {
				cases.add(Arguments.of(fixture, switchRowsColumns, false)); // every time point, variables in file order
				cases.add(Arguments.of(fixture, switchRowsColumns, true));  // a time sub-range, variables reversed
			}
		}
		return cases.stream();
	}

	@ParameterizedTest(name = "{0} switchRowsColumns={1} subset={2}")
	@MethodSource("cases")
	public void newPipelineMatchesASCIIExporter(Fixture fixture, boolean switchRowsColumns, boolean subset, @TempDir Path userDir) throws Exception {
		for (String file : fixture.files())
			Files.write(userDir.resolve(file), Resources.toByteArray(Resources.getResource(fixture.resourceDirectory() + file)));
		if (fixture.functions() != null)
			Files.writeString(userDir.resolve("SimID_" + fixture.simKey() + "_0_.functions"), fixture.functions());
		VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(new VCSimulationIdentifier(new KeyValue(fixture.simKey()), USER), 0);
		Cachetable cache = new Cachetable(10 * Cachetable.minute, 100_000_000);
		cache.put(vcdID, new SimulationData(vcdID, userDir.toFile(), userDir.toFile(), null));
		ExportServiceImpl exportService = new ExportServiceImpl();
		DataServerImpl dataServer = new DataServerImpl(new DataSetControllerImpl(cache, userDir.toFile(), userDir.toFile()), exportService);

		ODESimData odeSimData = dataServer.getODEData(USER, vcdID);
		double[] times = odeSimData.extractColumn(odeSimData.findColumn(OdeDatasets.TIME_COLUMN));
		List<String> variables = OdeCsvGoldenTest.nonIndexColumns(odeSimData);
		int begin = 0;
		int end = times.length - 1;
		if (subset) {
			begin = 2;
			end = times.length - 3;
			Collections.reverse(variables);
		}
		assertTrue(variables.size() > 1 && end > begin, "fixture too small to be a meaningful comparison");

		String expected = OdeCsvGoldenTest.legacyBody(dataServer, exportService, vcdID, variables, times, begin, end, switchRowsColumns);

		Dataset dataset = OdeDatasets.fromResultSet(fixture.simKey(), odeSimData)
				.select(Selection.all().range(Dimension.TIME, begin, end + 1).variables(variables));
		String actual = new CsvDatasetWriter(CsvDatasetWriter.Layout.VCELL_LEGACY, switchRowsColumns, null).toCsv(dataset);

		assertEquals(expected, actual);
		if (fixture == IDA_WITH_FUNCTIONS)
			assertFalse(dataset.variables(Variable.Role.FUNCTION).isEmpty(), "this fixture should exercise function columns");
	}

	private static List<String> nonIndexColumns(RowColumnResultSet resultSet) {
		List<String> names = new ArrayList<>();
		for (var column : resultSet.getColumnDescriptions())
			if (!column.getName().equals(OdeDatasets.TIME_COLUMN)) names.add(column.getName());
		return names;
	}

	/** Runs the existing exporter and returns its CSV from the body's first line on. */
	private static String legacyBody(DataServerImpl dataServer, ExportServiceImpl exportService, VCSimulationDataIdentifier vcdID,
									 List<String> variables, double[] times, int begin, int end, boolean switchRowsColumns) throws Exception {
		ExportSpecs exportSpecs = new ExportSpecs(vcdID, ExportFormat.CSV,
				new VariableSpecs(variables.toArray(new String[0]), ExportEnums.VariableMode.VARIABLE_MULTI),
				new TimeSpecs(begin, end, times, ExportEnums.TimeMode.TIME_RANGE),
				new GeometrySpecs(new SpatialSelection[0], Coordinate.Axis.Z, 0, ExportEnums.GeometryMode.GEOMETRY_SELECTIONS),
				new ASCIISpecs(null, ExportEnums.ExportableDataType.ODE_VARIABLE_DATA, ExportFormat.CSV, null, false, switchRowsColumns),
				"sim", "context");
		FileDataContainerManager containers = new FileDataContainerManager();
		try {
			Collection<ExportOutput> outputs = new ASCIIExporter(exportService.getEventController()).makeASCIIData(
					new OutputContext(new AnnotatedFunction[0]), JobRequest.createExportJobRequest(USER), USER, dataServer, exportSpecs, containers);
			assertEquals(1, outputs.size());
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			outputs.iterator().next().writeDataToOutputStream(bytes, containers);
			String csv = bytes.toString(StandardCharsets.UTF_8);
			int bodyStart = csv.indexOf("Variable values over");
			assertTrue(bodyStart >= 0, "unexpected legacy output:\n" + csv);
			return csv.substring(bodyStart);
		} finally {
			containers.closeAllAndDelete();
		}
	}
}
