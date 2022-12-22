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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.VcmlTestSuiteFiles;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
	 * each file in largeFileSet is > 500K on disk and is not included in the test suite.
	 * @return
	 */
	public static Set<String> largeFileSet() {
		Set<String> largeFiles = new HashSet<>();
//		largeFiles.add("biomodel_101963252.vcml");
//		largeFiles.add("biomodel_189321805.vcml");
//		largeFiles.add("biomodel_200301029.vcml");
//		largeFiles.add("biomodel_26455186.vcml");
//		largeFiles.add("biomodel_27192717.vcml");
//		largeFiles.add("biomodel_28625786.vcml");
//		largeFiles.add("biomodel_34826524.vcml");
//		largeFiles.add("biomodel_38086434.vcml");
//		largeFiles.add("biomodel_47429473.vcml");
//		largeFiles.add("biomodel_55178308.vcml");
//		largeFiles.add("biomodel_59361239.vcml");
//		largeFiles.add("biomodel_60799209.vcml");
//		largeFiles.add("biomodel_61699798.vcml");
//		largeFiles.add("biomodel_81992349.vcml");
//		largeFiles.add("biomodel_83091496.vcml");
//		largeFiles.add("biomodel_84275910.vcml");
//		largeFiles.add("biomodel_93313420.vcml");
//		largeFiles.add("biomodel_98150237.vcml");
		return largeFiles;
	}

	/**
	 * 	each file in the slowTestSet takes > 10s on disk and is not included in the unit test (move to integration testing)
	 */
	public static Set<String> slowFileSet() {
		Set<String> slowModels = new HashSet<>();
//		slowModels.add("biomodel_62467093.vcml"); // 44s
//		slowModels.add("biomodel_62477836.vcml"); // 59s
//		slowModels.add("biomodel_62585003.vcml"); // 33s
		return slowModels;
	}

	public static Set<String> outOfMemoryFileSet() {
		Set<String> outOfMemoryModels = new HashSet<>();
		outOfMemoryModels.add("biomodel_101963252.vcml"); // FAULT.JAVA_HEAP_SPACE
//		outOfMemoryModels.add("biomodel_26455186.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
//		outOfMemoryModels.add("biomodel_27192647.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
//		outOfMemoryModels.add("biomodel_27192717.vcml");  // FAULT.OUT_OF_MEMORY) - Java heap space: failed reallocation of scalar replaced objects
		return outOfMemoryModels;
	}

	/**
	 * each file in the knownFaultsMap hold known problems in the current software
	 */
	public static Map<String, MathCompareResults.Decision> knownFaults() {
		HashMap<String, MathCompareResults.Decision> faults = new HashMap();
		faults.put("lumped_reaction_proper_size_in_rate.vcml:Application0", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_18894555.vcml:compartmental", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_43726934.vcml:compartmental", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_47429473.vcml:Steady State Turnover", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWASP at Lam Tip in 3D Geometry", MathCompareResults.Decision.MathDifferent_DIFFERENT_FASTINV_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=1", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=2", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=4", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:Nwasp Activation Cap=4 Cof=10", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=2 Cof=10", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=4 Prof=20", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=4 Prof=20 Cof=10", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=1 Cof=10", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=1 Prof=20", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=1 Prof=20 Cof=10", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWasp Activation Cap=2 Prof=20", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWASP Activation Cap=2 Prof=20 Cof = 10", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_47429473.vcml:NWASP Activation Cap=1 Prof=20 No Arp Binding", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_55178308.vcml:Spatial 1 - 3D -  electrophysiology", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_59361239.vcml:full model", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_59361239.vcml:individual knockouts", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_59361239.vcml:time delay 60 s", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_59361239.vcml:receptor density", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_59361239.vcml:sensitivity analysis", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_59361239.vcml:individual knockouts with delay", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_59361239.vcml:integrin knockout", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		return faults;
	}

	/**
	 * process all tests that are available - the slow set is parsed only and not processed.
	 */
	@Parameterized.Parameters
	public static Collection<String> testCases() throws XmlParseException, IOException {
		Predicate<String> diff_num_expr_filter = (t) -> knownFaults().containsKey(t) && knownFaults().get(t) == MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION;
		Predicate<String> skipFilter = (t) -> !outOfMemoryFileSet().contains(t) && !largeFileSet().contains(t);
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
				appTestCases.add(filename+":"+simContext.getName());
			}
		}
		return appTestCases;
	}
	
	
	@Test
	public void test_math_compare() throws Exception {
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
		new_simContext.updateAll(false);
		MathDescription newMath = new_simContext.getMathDescription();
		MathDescription newMathClone = new MathDescription(newMath); // test round trip to/from MathDescription.readFromDatabase()
		MathCompareResults results = null;
		if (bTransformVariables) {
			results = MathDescription.testEquivalencyWithRename(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);
		}else {
			results = MathDescription.testEquivalency(SimulationSymbolTable.createMathSymbolTableFactory(), originalMath, newMath);
		}
		MathCompareResults.Decision knownFault = knownFaults().get(filename_colon_appname);
		if (results.isEquivalent() && knownFault != null){
			Assert.fail("math equivalent for '"+filename_colon_appname+"', but expecting '"+knownFault+"', remove known fault");
		}
		if (!results.isEquivalent()){
			try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(knownProblemFile, true));){
				bufferedWriter.write("faults.put(\""+filename_colon_appname+"\", MathCompareResults.Decision."+results.decision+");\n");
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

}
