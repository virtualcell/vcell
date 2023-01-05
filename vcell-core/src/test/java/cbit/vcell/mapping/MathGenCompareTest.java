package cbit.vcell.mapping;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelTransforms;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.VcmlTestSuiteFiles;
import org.vcell.test.MathGen_IT;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Category(MathGen_IT.class)
@RunWith(Parameterized.class)
public class MathGenCompareTest {

	private String filename_colon_appname;

	private static String previousInstalldirPropertyValue;
	private static File knownProblemFile;

	public MathGenCompareTest(String filename_colon_appname){
		this.filename_colon_appname = filename_colon_appname;
	}


	@BeforeClass
	public static void setup() throws IOException {
		previousInstalldirPropertyValue = System.getProperty("vcell.installDir");
		System.setProperty("vcell.installDir", "..");
		NativeLib.combinej.load();
		knownProblemFile = Files.createTempFile("MathGenCompareTest","knownProblems").toFile();
		System.err.println("known problem file: "+knownProblemFile.getAbsolutePath());
	}

	@AfterClass
	public static void teardown() {
		if (previousInstalldirPropertyValue!=null) {
			System.setProperty("vcell.installDir", previousInstalldirPropertyValue);
		}
		System.err.println("known problem file: "+knownProblemFile.getAbsolutePath());
	}

	@BeforeClass
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
		return slowModels;
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
	 * each file in the knownFaultsMap hold known problems in the current software
	 */
	public static Map<String, MathCompareResults.Decision> knownLegacyFaults() {
		HashMap<String, MathCompareResults.Decision> faults = new HashMap();
		faults.put("lumped_reaction_proper_size_in_rate.vcml:Application0", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_12522025.vcml:purkinje9", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_12522025.vcml:purkinje9-ss", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_12522025_spatial.vcml:purkinje9", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_16404713.vcml:PDGFgradient_TIRF", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403233.vcml:Spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403238.vcml:Spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403244.vcml:spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403250.vcml:Spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403358.vcml:Spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403576.vcml:Spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_34855932.vcml:cell4", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_34855932.vcml:cell5", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_34855932.vcml:cell6", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_34855932.vcml:cell7", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_36275161.vcml:PDGFgradient_TIRF", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_55178308.vcml:Spatial 1 - PIP2 seq at PSD - HALF", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_18894555.vcml:compartmental", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_47429473.vcml:Steady State Turnover", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWASP at Lam Tip in 3D Geometry", MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTINV_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=1", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=2", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=4", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:Nwasp Activation Cap=4 Cof=10", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=2 Cof=10", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=4 Prof=20", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=4 Prof=20 Cof=10", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=1 Cof=10", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=1 Prof=20", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=1 Prof=20 Cof=10", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=2 Prof=20", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWASP Activation Cap=2 Prof=20 Cof = 10", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_47429473.vcml:NWASP Activation Cap=1 Prof=20 No Arp Binding", MathCompareResults.Decision.MathDifferent_EQUATION_REMOVED);
		faults.put("biomodel_55178308.vcml:Spatial 1 - 3D -  electrophysiology", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_59361239.vcml:full model", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_59361239.vcml:individual knockouts", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_59361239.vcml:time delay 60 s", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_59361239.vcml:receptor density", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_59361239.vcml:sensitivity analysis", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_59361239.vcml:individual knockouts with delay", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		faults.put("biomodel_59361239.vcml:integrin knockout", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // create canonicalMath cannot handle function of variables of type 'FilamentVariable'
		return faults;
	}

	public static Map<String, MathCompareResults.Decision> knownReductionFaults() {
		HashMap<String, MathCompareResults.Decision> faults = new HashMap();
		return faults;
	}

	/**
	 * process all tests that are available - the slow set is parsed only and not processed.
	 */
	@Parameterized.Parameters
	public static Collection<String> testCases() throws XmlParseException, IOException {
		Predicate<String> skipFilter = (t) -> !outOfMemoryFileSet().contains(t) && !largeFileSet().contains(t) && !slowFileSet().contains(t);
		List<String> filenames = Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter).collect(Collectors.toList());

		ArrayList<String> appTestCases = new ArrayList<>();
		for (String filename : filenames){
			String vcmlStr;
			try (InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);) {
				vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
						.lines().collect(Collectors.joining("\n"));
			}
			BioModel biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
			for (SimulationContext simContext : biomodel.getSimulationContexts()){
				String test_case_name = filename + ":" + simContext.getName();
//if (!knownReductionFaults().containsKey(test_case_name)) continue;
				appTestCases.add(test_case_name);
			}
		}
		return appTestCases;
	}


	@Test
	public void test_legacy_math_compare() throws Exception {
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

		BioModel transformed_biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		transformed_biomodel.refreshDependencies();

		boolean bTransformKMOLE = true;
		boolean bTransformVariables = false;

		if (bTransformKMOLE){
			BioModelTransforms.restoreOldReservedSymbolsIfNeeded(transformed_biomodel);
		}

		SimulationContext orig_simContext = orig_biomodel.getSimulationContext(simContextName);
		MathDescription originalMath = orig_simContext.getMathDescription();
		MathDescription origMathClone = new MathDescription(originalMath); // test round trip to/from MathDescription.readFromDatabase()
		SimulationContext new_simContext = transformed_biomodel.getSimulationContexts(orig_simContext.getName());
		new_simContext.setUsingMassConservationModelReduction(false);
		new_simContext.updateAll(false);
		MathDescription newMath = new_simContext.getMathDescription();
		MathDescription newMathClone = new MathDescription(newMath); // test round trip to/from MathDescription.readFromDatabase()
		MathCompareResults results = null;
		if (bTransformVariables) {
			results = MathDescription.testEquivalencyWithRename(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);
		}else {
			results = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);
		}
		MathCompareResults.Decision knownFault = knownLegacyFaults().get(filename_colon_appname);
		if (results.isEquivalent() && knownFault != null){
			Assert.fail("math equivalent for '"+filename_colon_appname+"', but expecting '"+knownFault+"', remove known fault");
		}
		if (!results.isEquivalent()){
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(knownProblemFile, true));){
				bufferedWriter.write("faults.put(\""+filename_colon_appname+"\", MathCompareResults.Decision."+results.decision+"); // legacy compare\n");
			}
			if (knownFault == null) {
				Assert.fail("'"+filename_colon_appname+"' expecting equivalent, " +
						"computed '"+results.decision+"', " +
						"details: " + results.toDatabaseStatus());
			}else{
				Assert.assertTrue("'"+filename_colon_appname+"' expecting '"+knownFault+"', " +
								"computed '"+results.decision+"', " +
								"details: " + results.toDatabaseStatus(),
						knownFault == results.decision);
			}
		}
	}

	@Test
	@Ignore
	public void test_identity_math_compare() throws Exception {
		String[] tokens = filename_colon_appname.split(":");
		String filename = tokens[0];
		String simContextName = filename_colon_appname.substring(filename.length() + 1);
		String vcmlStr;
		try (InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);) {
			vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
					.lines().collect(Collectors.joining("\n"));
		}

		System.out.println("testing test case " + filename_colon_appname);

		//
		// read in 'reduced' model: generate math for application <<with>> model reduction from mass conservation
		//
		BioModel reducedBiomodel1 = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		reducedBiomodel1.refreshDependencies();
		SimulationContext reducedSimContext1 = reducedBiomodel1.getSimulationContext(simContextName);
		reducedSimContext1.setUsingMassConservationModelReduction(true);
		reducedSimContext1.updateAll(false);
		MathDescription reducedMath1 = reducedSimContext1.getMathDescription();

		BioModel reducedBiomodel2 = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		reducedBiomodel2.refreshDependencies();
		SimulationContext reducedSimContext2 = reducedBiomodel2.getSimulationContext(simContextName);
		reducedSimContext2.setUsingMassConservationModelReduction(true);
		reducedSimContext2.updateAll(false);
		MathDescription reducedMath2 = reducedSimContext2.getMathDescription();

		//
		// compare identity (math against clone of itself - using the reduced model)
		//
		MathCompareResults identityCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), reducedMath1, reducedMath2);
		Assert.assertTrue("math not equivalent for '" + filename_colon_appname + "': " + identityCompareResults.toDatabaseStatus(), identityCompareResults.isEquivalent());
	}

	@Test
	public void test_model_reduction_math_compare() throws Exception {
		String[] tokens = filename_colon_appname.split(":");
		String filename = tokens[0];
		String simContextName = filename_colon_appname.substring(filename.length()+1);
		String vcmlStr;
		try (InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);) {
			vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
					.lines().collect(Collectors.joining("\n"));
		}

		System.out.println("testing test case "+filename_colon_appname);

		//
		// read in 'reduced' model: generate math for application <<with>> model reduction from mass conservation
		//  - but skip entire test if not ODE or PDE (we don't do model reduction for stochastic models)
		//
		BioModel reducedBiomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		SimulationContext reducedSimContext = reducedBiomodel.getSimulationContext(simContextName);
		if (reducedSimContext.getApplicationType() != SimulationContext.Application.NETWORK_DETERMINISTIC){
			return; // not ODEs or PDEs, so no model reduction - nothing to test.
		}
		reducedBiomodel.refreshDependencies();
		reducedSimContext.setUsingMassConservationModelReduction(true);
		reducedSimContext.updateAll(false);
		MathDescription reducedMath = reducedSimContext.getMathDescription();


		//
		// for ODE applications, ensure that disabling mass conservation compares as equivalent to generating with mass conservation
		//
		// read in 'full' model: generate math for application <<without>> model reduction from mass conservation
		//
		BioModel fullBiomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		fullBiomodel.refreshDependencies();
		SimulationContext fullSimContext = fullBiomodel.getSimulationContexts(simContextName);
		reducedSimContext.setUsingMassConservationModelReduction(false);
		fullSimContext.updateAll(false);
		MathDescription fullMath = fullSimContext.getMathDescription();

		MathCompareResults reductionCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), reducedMath, fullMath);

		MathCompareResults.Decision knownReductionFault = knownReductionFaults().get(filename_colon_appname);
		if (reductionCompareResults.isEquivalent() && knownReductionFault != null){
			Assert.fail("math equivalent for '"+filename_colon_appname+"', but expecting '"+knownReductionFault+"', remove known fault");
		}
		if (!reductionCompareResults.isEquivalent()){
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(knownProblemFile, true));){
				bufferedWriter.write("faults.put(\""+filename_colon_appname+"\", MathCompareResults.Decision."+reductionCompareResults.decision+"); // reduction compare\n");
			}
			if (knownReductionFault == null) {
				Assert.fail("'"+filename_colon_appname+"' expecting equivalent, " +
						"computed '"+reductionCompareResults.decision+"', " +
						"details: " + reductionCompareResults.toDatabaseStatus());
			}else{
				Assert.assertTrue("'"+filename_colon_appname+"' expecting '"+knownReductionFault+"', " +
								"computed '"+reductionCompareResults.decision+"', " +
								"details: " + reductionCompareResults.toDatabaseStatus(),
						knownReductionFault == reductionCompareResults.decision);
			}
		}
	}


}
