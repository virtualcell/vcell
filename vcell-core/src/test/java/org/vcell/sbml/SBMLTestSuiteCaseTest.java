package org.vcell.sbml;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.ErrorTolerance;
import cbit.vcell.solver.ExplicitOutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.sbml.vcell.SBMLSymbolMapping;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * SBML Test Suite cases that import, generate math and solve within the suite's tolerances, run end to end with
 * the ODE solver. Each case here was a failure fixed by the change that added it (one case per cause), so the
 * list only grows. {@code -Dsbml.cases=1,2,3} runs other cases instead, for triage.
 */
@Tag("SBML_IT")
public class SBMLTestSuiteCaseTest {

	/** Cases fixed in the SBML importer, by cause. */
	private static final int[] FIXED = {
			// elements with no math (SBML L3V2), which have no effect
			1464, 1555, 1600, 1602,
			// the rateOf csymbol: of a rate rule's variable, a species changed by reactions, a constant
			1269, 1455, 1461, 1525, 1529,
			// no ODEs: rules, constants and events only
			1289, 1290, 1485,
			// SBML L3V2 MathML: quotient, rem, implies, max and min of any arity
			1272, 1273, 1274, 1275, 1276, 1277, 1278, 1279, 1280, 1281, 1495, 1496, 1497,
			// 0-dimensional compartments
			97, 155, 321, 1809,
	};

	/** Cases the importer refuses with a message naming what it does not support, instead of importing them wrong. */
	private static final Map<Integer, String> REFUSED = Map.of(
			1393, "'comp'",                 // comp package: jsbml drops its elements, so the model would be incomplete
			975, "Conversion factors");     // conversion factors: VCell has nothing to apply them with

	@BeforeAll
	public static void before() {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
	}

	public static List<Integer> testCases() {
		String override = System.getProperty("sbml.cases");
		if (override != null && !override.isBlank()) {
			return Arrays.stream(override.split(",")).map(String::trim).map(Integer::valueOf).toList();
		}
		return Arrays.stream(FIXED).boxed().toList();
	}

	@ParameterizedTest
	@MethodSource("testCases")
	public void solvesWithinTolerance(Integer testCase) throws Exception {
		TLogger vcl = new TLogger();
		InputStream sbml = SbmlTestSuiteFiles.getSbmlTestCase(testCase);
		SBMLImporter importer = new SBMLImporter(sbml, vcl, false);
		BioModel bioModel = importer.getBioModel();
		SBMLSymbolMapping symbolMapping = importer.getSymbolMapping();
		bioModel.updateAll(false);
		assertTrue(vcl.highPriority.isEmpty(), "case " + testCase + ": import reported errors: " + vcl.highPriority);

		SimulationContext simContext = bioModel.getSimulationContext(0);
		MathDescription mathDesc = simContext.getMathDescription();

		SBMLResults expected = SBMLResults.fromCSV(SbmlTestSuiteFiles.getSbmlTestCaseResultsAsCSV(testCase));
		SBMLSimulationSpec spec = SBMLSimulationSpec.fromSpecFile(SbmlTestSuiteFiles.getSbmlTestCaseSettingsAsText(testCase));
		Simulation sim = new Simulation(mathDesc, simContext);
		sim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0.0, spec.start + spec.duration));
		sim.getSolverTaskDescription().setOutputTimeSpec(new ExplicitOutputTimeSpec(expected.values.get(0)));
		sim.getSolverTaskDescription().setErrorTolerance(new ErrorTolerance(1e-12, 1e-9));
		Path workingDir = Files.createTempDirectory("sbml-test-suite-case-");
		SBMLResults computed = new SBMLSolver().simulate(workingDir.toFile(), sim, spec, simContext, symbolMapping);
		boolean equivalent = SBMLResults.compareEquivalent(expected, computed, spec);
		if (!equivalent) {
			System.err.println(SBMLResults.toCSV(expected, computed));
		}
		assertTrue(equivalent, "case " + testCase + ": not within the suite's tolerance");
	}

	public static List<Integer> refusedCases() {
		return REFUSED.keySet().stream().sorted().toList();
	}

	@ParameterizedTest
	@MethodSource("refusedCases")
	public void refusedWithAMessage(Integer testCase) throws Exception {
		TLogger vcl = new TLogger();
		String expected = REFUSED.get(testCase);
		try {
			new SBMLImporter(SbmlTestSuiteFiles.getSbmlTestCase(testCase), vcl, false).getBioModel();
		} catch (Exception e) {
			assertTrue(e.getMessage().contains(expected), "case " + testCase + ": " + e.getMessage());
			return;
		}
		if (vcl.highPriority.stream().noneMatch(m -> m.contains(expected))) {
			fail("case " + testCase + " imported without a message containing " + expected + ": " + vcl.highPriority);
		}
	}
}
