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
		faults.put("lumped_reaction_proper_size_in_rate.vcml:Application0", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =LEGACY= MathDifferent:DifferentExpression:expressions are different: ' - (0.001660538783162726 * s1)' vs ' -
		faults.put("biomodel_47429473.vcml:NWASP at Lam Tip in 3D Geometry", MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTINV_EXPRESSION); // =LEGACY= MathDifferent:DifferentFastInvExpression:could not find a match for fast invariant expression'Expres
		faults.put("biomodel_55178308.vcml:Spatial 1 - 3D -  electrophysiology", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // =LEGACY= MathDifferent:FailedUnknown:line #630 Exception: variable Na not defined
		return faults;
	}

	public static Map<String, MathCompareResults.Decision> knownReductionFaults() {
		HashMap<String, MathCompareResults.Decision> faults = new HashMap();
		faults.put("biomodel_100961371.vcml:modelinOct2010", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: ' - (9.963232698976356E-4 * ((1.2000000
		faults.put("biomodel_148700996.vcml:Fast B Compartmental", MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTINV_EXPRESSION); // =REDUCED= MathDifferent:DifferentFastInvExpression:could not find a match for fast invariant expression'Expres
		faults.put("biomodel_148700996.vcml:2DGeom nonuniform dens v2", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_156134818.vcml:unnamed", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '( - (7.961783439490447E-5 * EGF_EGFR_2
		faults.put("biomodel_17257105.vcml:compartmental", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((0.0011 * (L_b1AR_cell + L_b1AR_Gs_ce
		faults.put("biomodel_17257105.vcml:spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_209284198.vcml:fast compart with 3D S:V & V:V", MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTINV_EXPRESSION); // =REDUCED= MathDifferent:DifferentFastInvExpression:could not find a match for fast invariant expression'Expres
		faults.put("biomodel_2917788.vcml:Figure 4.3: Fast Approximation", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '0.0' vs '( - ((20.0 * x_o) - (50.0 * x
		faults.put("biomodel_32568171.vcml:Sim5_1", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:GoodModel17_5", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:Furaptra", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_1AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:Furaptra_1AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_1AP_v2", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP_v2", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_1AP_v3", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP_v3", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:Furaptra_1AP_v3", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP_v4", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568171.vcml:SyGCaMP2_80Hz_v4", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_1AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_20AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_1AP_v2", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_20AP_v2", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_1AP_v3", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_20AP_v4", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_32568356.vcml:SyGCaMP2_80Hz_v4", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_38086434.vcml:CALI, fine mesh", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_38086434.vcml:CALI, VASP=0, fine mesh", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_38086434.vcml:CALI experiment with FRAP intensities", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_38086434.vcml:CALI without KND CP=1.5", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_38086434.vcml:CALI experiment with R=5", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_49411430.vcml:muscle PI release", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_49411430.vcml:POmbe actin pi release", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_49411430.vcml:pombe actin polymerization", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_49411430.vcml:0913", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_49411430.vcml:0930", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_49411430.vcml:1002", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((5.0E-4 * ADPPIFactin_PI_release) - (
		faults.put("biomodel_59280306.vcml:compartmental", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((0.0011 * (L_b1AR_cell + L_b1AR_Gs_ce
		faults.put("biomodel_59280306.vcml:spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60203358.vcml:AP", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((0.010526315789473684 * I1Na_Sarcopla
		faults.put("biomodel_60203358.vcml:IKsum", MathCompareResults.Decision.MathDifferent_FAILURE_UNKNOWN); // =REDUCED= MathDifferent:FailedUnknown:failed to compare math: too many failed evaluations (0 of 20) (java.lang
		faults.put("biomodel_60203358.vcml:AP with pulse", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '((0.010526315789473684 * I1Na_Sarcopla
		faults.put("biomodel_60227051.vcml:3D", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:2Dprova3D", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:3Dsph", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:3DclustB", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:3DclustBCh", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:3D clust Ch", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:3D clust ch piccolo", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:clust pic double", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_60227051.vcml:clust pic double sx dx", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS); // =REDUCED= MathDifferent:DifferentNumberOfExpressions:fast invariants have different number of expressions
		faults.put("biomodel_66264973.vcml:compartmental - IC-G2736X", MathCompareResults.Decision.MathDifferent_EQUATION_ADDED); // =REDUCED= MathDifferent:EquationAdded:only one mathDescription had equation for 'Compartment::ICpeptide_mAtaxi
		faults.put("biomodel_66265579.vcml:compartmental - IC4", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '0.0' vs '((0.005860724109735934 * ((17
		faults.put("biomodel_91134339.vcml:test", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '( - ((0.03 * C_Unnamed_compartment * (
		faults.put("biomodel_9254662.vcml:FastBuffExpReceptor", MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTINV_EXPRESSION); // =REDUCED= MathDifferent:DifferentFastInvExpression:could not find a match for fast invariant expression'Expres
		faults.put("biomodel_98147638.vcml:compartmental - IC4", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION); // =REDUCED= MathDifferent:DifferentExpression:expressions are different: '0.0' vs '((0.005860724109735934 * ((17
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
if (true
&& !filename.equals("biomodel_97705317.vcml")
&& !filename.equals("biomodel_83651737.vcml")
&& !filename.equals("biomodel_97075423.vcml")
&& !filename.equals("biomodel_97705317.vcml")
&& !filename.equals("biomodel_97786619.vcml")
&& !filename.equals("biomodel_97786886.vcml")
&& !filename.equals("biomodel_97787114.vcml")
&& !filename.equals("biomodel_98730962.vcml")
) continue;
			String vcmlStr;
			try (InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);) {
				vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
						.lines().collect(Collectors.joining("\n"));
			}
			BioModel biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
//if (biomodel.getModel().getStructureTopology().isEmpty()) continue;
			for (SimulationContext simContext : biomodel.getSimulationContexts()){
				String test_case_name = filename + ":" + simContext.getName();
//if (!knownLegacyFaults().containsKey(test_case_name)) continue;
				appTestCases.add(test_case_name);
			}
		}
		return appTestCases;
//		return Arrays.asList("biomodel_12522025.vcml:purkinje9");
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

		SimulationContext orig_simContext = orig_biomodel.getSimulationContext(simContextName);
		MathDescription originalMath = orig_simContext.getMathDescription();
		MathDescription origMathClone = new MathDescription(originalMath); // test round trip to/from MathDescription.readFromDatabase()
		SimulationContext new_simContext = transformed_biomodel.getSimulationContexts(orig_simContext.getName());
		//new_simContext.setUsingMassConservationModelReduction(false);
		try {
			if (bTransformKMOLE) BioModelTransforms.restoreOldReservedSymbolsIfNeeded(transformed_biomodel);
			new_simContext.updateAll(false);
		} finally {
			if (bTransformKMOLE) BioModelTransforms.restoreLatestReservedSymbols(transformed_biomodel);
		}
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
			// try again using non-reduced math
			new_simContext.setUsingMassConservationModelReduction(false);
			new_simContext.updateAll(false);
			newMath = new_simContext.getMathDescription();
			MathCompareResults results2 = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);

			if (!results2.isEquivalent()) {
				try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(knownProblemFile, true));) {
					bufferedWriter.write("faults.put(\"" + filename_colon_appname + "\", MathCompareResults.Decision." + results2.decision + "); // =LEGACY= "
							+ results2.toDatabaseStatus().substring(0, Math.min(results2.toDatabaseStatus().length(), 100)) + "\n");
				}
				if (knownFault == null) {
					Assert.fail("'" + filename_colon_appname + "' expecting equivalent, " +
							"computed '" + results2.decision + "', " +
							"details: " + results2.toDatabaseStatus());
				} else {
					Assert.assertTrue("'" + filename_colon_appname + "' expecting '" + knownFault + "', " +
									"computed '" + results2.decision + "', " +
									"details: " + results2.toDatabaseStatus(),
							knownFault == results2.decision);
				}
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
	@Ignore
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
		fullSimContext.setUsingMassConservationModelReduction(false);
		fullSimContext.updateAll(false);
		MathDescription fullMath = fullSimContext.getMathDescription();

		MathCompareResults reductionCompareResults = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), reducedMath, fullMath);

		MathCompareResults.Decision knownReductionFault = knownReductionFaults().get(filename_colon_appname);
		if (reductionCompareResults.isEquivalent() && knownReductionFault != null){
			Assert.fail("math equivalent for '"+filename_colon_appname+"', but expecting '"+knownReductionFault+"', remove known fault");
		}
		if (!reductionCompareResults.isEquivalent()){
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(knownProblemFile, true));){
				bufferedWriter.write("faults.put(\""+filename_colon_appname+"\", MathCompareResults.Decision."+reductionCompareResults.decision+"); // =REDUCED= "
						+reductionCompareResults.toDatabaseStatus().substring(0, Math.min(reductionCompareResults.toDatabaseStatus().length(), 100))+"\n");
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
