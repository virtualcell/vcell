package cbit.vcell.biomodel;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.vcell.sbml.VcmlTestSuiteFiles;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

@Tag("MathGen_IT")
public class MathOverrideApplyTest {

	private static String previousInstalldirPropertyValue;
	private static File codeKnownProblemFile;
	private static File csvKnownProblemFile;

	@BeforeAll
	public static void setup() throws IOException {
		previousInstalldirPropertyValue = PropertyLoader.getProperty("vcell.installDir", null);
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
		codeKnownProblemFile = Files.createTempFile("OverridesApplyTest","code_KnownProblems").toFile();
		csvKnownProblemFile = Files.createTempFile("OverridesApplyTest","csv_KnownProblems").toFile();
		System.err.println("code known problem file: "+codeKnownProblemFile.getAbsolutePath());
		System.err.println("csv known problem file: "+csvKnownProblemFile.getAbsolutePath());
	}

	@AfterAll
	public static void teardown() {
		if (previousInstalldirPropertyValue!=null) {
			PropertyLoader.setProperty(PropertyLoader.installationRoot, previousInstalldirPropertyValue);
		}
		System.err.println("code known problem file: "+codeKnownProblemFile.getAbsolutePath());
		System.err.println("csv known problem file: "+csvKnownProblemFile.getAbsolutePath());
	}

	@BeforeAll
	public static void printSkippedModels() {
		for (String filename : outOfMemoryFileSet()){
			System.err.println("skipping - out of memory: "+filename);
		}
		for (String filename : largeFileSet()){
			System.err.println("skipping - too large (model not in repo): "+filename);
		}
		for (String filename : slowFileSet()){
			System.err.println("skipping - Math generation too slow for unit tests: "+filename);
		}
	}

	/**
	 * each file in largeFileSet too large on disk and is not included in the test suite.
	 */
	public static Set<String> largeFileSet() {
		Set<String> largeFiles = new HashSet<>();
		return largeFiles;
	}

	/**
	 * 	each file in the slowTestSet is not included in the unit test (move to integration testing)
	 */
	public static Set<String> slowFileSet() {
		Set<String> slowModels = new HashSet<>();
		slowModels.add("biomodel_188880263.vcml");
		slowModels.add("biomodel_200301029.vcml");
		slowModels.add("biomodel_34826524.vcml");
		slowModels.add("biomodel_38086434.vcml");
		slowModels.add("biomodel_47429473.vcml");
		slowModels.add("biomodel_61699798.vcml");
		slowModels.add("biomodel_98150237.vcml");
		return slowModels;
	}

	public static Set<String> knownFaults() {
		Set<String> knownFault = new HashSet<>();
		knownFault.add("biomodel_55178308.vcml"); // variable 'VolFract_ER_spine' not found in math
		knownFault.add("biomodel_101981216.vcml"); // variable 'Factin_diffusionRate' not found in math
		knownFault.add("biomodel_105608907.vcml"); // variable 'Factin_diffusionRate' not found in math
		return knownFault;
	}

	/**
	 * each file in outOfMemoryFileSet cannot be run without large memory (not included in test suite).
	 */
	public static Set<String> outOfMemoryFileSet() {
		Set<String> outOfMemoryModels = new HashSet<>();
		outOfMemoryModels.add("biomodel_101963252.vcml"); // FAULT.JAVA_HEAP_SPACE
		return outOfMemoryModels;
	}


	/**
	 * process all tests that are available - the slow set is parsed only and not processed.
	 */
	public static Collection<String> testCases() throws XmlParseException, IOException {
		Predicate<String> skipFilter = (t) -> !outOfMemoryFileSet().contains(t) && !largeFileSet().contains(t) && !slowFileSet().contains(t);
		List<String> filenames = Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter).collect(Collectors.toList());

		ArrayList<String> appTestCases = new ArrayList<>();
		for (String filename : filenames){
//if (true
//&& !filename.equals("biomodel_28555193.vcml")
//&& !filename.equals("biomodel_28572365.vcml")
//) continue;
			String vcmlStr;
			try (InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);) {
				vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
						.lines().collect(Collectors.joining("\n"));
			}
			BioModel biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
			for (SimulationContext simContext : biomodel.getSimulationContexts()){
				// filter out simcontexts which do not have math overrides in any simulations
				boolean bHasMathOverrides = false;
				for (Simulation sim : simContext.getSimulations()){
					if (sim.getMathOverrides().hasOverrides()){
						bHasMathOverrides = true;
						break;
					}
				}
				if (bHasMathOverrides) {
					String test_case_name = filename + ":" + simContext.getName();
					appTestCases.add(test_case_name);
				}
			}
		}
		return appTestCases;
//		return Arrays.asList("biomodel_34826524.vcml:NWasp Activation Cap=2");
	}


	@ParameterizedTest
	@MethodSource("testCases")
	public void test_mathOverrideApply(String filename_colon_appname) throws Exception {
		String[] tokens = filename_colon_appname.split(":");
		String filename = tokens[0];
		String simContextName = filename_colon_appname.substring(filename.length()+1);
		String vcmlStr;
		try (InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);) {
			vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
					.lines().collect(Collectors.joining("\n"));
		}

		System.out.println("testing test case "+filename_colon_appname);
		BioModel orig_biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		orig_biomodel.refreshDependencies();

		List<String> simNamesWithOverrides = new ArrayList<>(); // zero or one for each simulation context.
		SimulationContext sc = orig_biomodel.getSimulationContext(simContextName);
		for (Simulation sim : sc.getSimulations()){
			if (sim.getMathOverrides().hasOverrides()){
				simNamesWithOverrides.add(sim.getName());
				break;
			}
		}
		for (String simNameWithOverride : simNamesWithOverrides){
			BioModel transformed_biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
			transformed_biomodel.refreshDependencies();
			Simulation sim = transformed_biomodel.getSimulation(simNameWithOverride);
			try {
				for (int scanIndex = 0; scanIndex < sim.getScanCount(); scanIndex++) {
					BioModelTransforms.applyMathOverrides(sim, new MathOverrides.ScanIndex(scanIndex), transformed_biomodel);
				}
				// for now, if it doesn't throw an exception, then it passes
				if (knownFaults().contains(filename)){
					// some applications may pass and others fail, e.g. 'biomodel_55178308.vcml:Spatial 1 - 3D -  electrophysiology' passes but rest fail
//					Assert.fail("applying math overrides succeeded, but '"+filename_colon_appname+"' in known faults list, remove from known faults list");
				}
			}catch (Exception e){
				if (!knownFaults().contains(filename)){
					e.printStackTrace();
					fail("applying math overrides failed unexpectedly for '" + filename_colon_appname + "', add to known faults: " + e.getMessage());
				}
			}
		}

	}

}
