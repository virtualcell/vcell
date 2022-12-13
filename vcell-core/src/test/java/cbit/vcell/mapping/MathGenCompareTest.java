package cbit.vcell.mapping;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.BioModelTransforms;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.VcmlTestSuiteFiles;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
public class MathGenCompareTest {

	private String filename;

	public MathGenCompareTest(String filename){
		this.filename = filename;
	}

	@BeforeClass
	public static void printSkippedModels() {
		for (String filename : outOfMemorySet()){
			System.err.println("skipping - out of memory: "+filename);
		}
		for (String filename : largeFileSet()){
			System.err.println("skipping - too large (model not in repo): "+filename);
		}
		for (String filename : slowTestSet()){
			System.err.println("skipping - Math generation too slow for unit tests: "+filename);
		}
	}

	/**
	 * each file in largeFileSet is > 500K on disk and is not included in the test suite.
	 * @return
	 */
	public static Set<String> largeFileSet() {
		Set<String> largeFiles = new HashSet<>();
		largeFiles.add("biomodel_101963252.vcml");
		largeFiles.add("biomodel_189321805.vcml");
		largeFiles.add("biomodel_200301029.vcml");
		largeFiles.add("biomodel_26455186.vcml");
		largeFiles.add("biomodel_27192717.vcml");
		largeFiles.add("biomodel_28625786.vcml");
		largeFiles.add("biomodel_34826524.vcml");
		largeFiles.add("biomodel_38086434.vcml");
		largeFiles.add("biomodel_47429473.vcml");
		largeFiles.add("biomodel_55178308.vcml");
		largeFiles.add("biomodel_59361239.vcml");
		largeFiles.add("biomodel_60799209.vcml");
		largeFiles.add("biomodel_61699798.vcml");
		largeFiles.add("biomodel_81992349.vcml");
		largeFiles.add("biomodel_83091496.vcml");
		largeFiles.add("biomodel_84275910.vcml");
		largeFiles.add("biomodel_93313420.vcml");
		largeFiles.add("biomodel_98150237.vcml");
		return largeFiles;
	}

	/**
	 * 	each file in the slowTestSet takes > 10s on disk and is not included in the unit test (move to integration testing)
	 */
	public static Set<String> slowTestSet() {
		Set<String> slowModels = new HashSet<>();
		slowModels.add("biomodel_62467093.vcml"); // 44s
		slowModels.add("biomodel_62477836.vcml"); // 59s
		slowModels.add("biomodel_62585003.vcml"); // 33s
		return slowModels;
	}

	public static Set<String> outOfMemorySet() {
		Set<String> outOfMemoryModels = new HashSet<>();
		return outOfMemoryModels;
	}

	/**
	 * each file in the knownFaultsMap hold known problems in the current software
	 */
	public static Map<String, MathCompareResults.Decision> knownFaults() {
		HashMap<String, MathCompareResults.Decision> faults = new HashMap();
		faults.put("lumped_reaction_proper_size_in_rate.vcml:Application0", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_116929912.vcml:Channel_test", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_116929971.vcml:Application0", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_116930032.vcml:Application0", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_12119723.vcml:7_12_00_model1", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_12522025.vcml:purkinje9", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("cbit.vcell.mapping.MathGenCompareTest#test_parse_vcml", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_17257105.vcml:spatial", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_17326658.vcml:Mg Green - 500 uM", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS);
		faults.put("biomodel_18894555.vcml:compartmental", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_20754836.vcml:n1", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_22403233.vcml:Spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403238.vcml:Spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403244.vcml:spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403250.vcml:Spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403358.vcml:Spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22403576.vcml:Spatial", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_22681429.vcml:Biophysical Letters", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_26454052.vcml:1D PIP2_fits 3D model that uses constructed 3D geometry", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_26454463.vcml:3D PIP2_constructed geometry", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_27071354.vcml:3d image", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_27072412.vcml:3d image", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_27072419.vcml:3d image", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_27072426.vcml:3d image", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_27192647.vcml:3D PIP2_constructed geometry", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_28730491.vcml:Needle", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:10 spines", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31584491.vcml:baseline condition", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:Sim5_1", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:SyGCaMP2_1AP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32579611.vcml:without PDE4A", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_34855932.vcml:cell4", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_36275161.vcml:PDGFgradient_TIRF", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_40882931.vcml:3d image", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_40883478.vcml:3d image", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_40883509.vcml:3d image", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_43726934.vcml:CAT diffusion", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_50584157.vcml:3d image", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_55396830.vcml:phloem_test", MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION);
		faults.put("biomodel_59280306.vcml:spatial", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_6436213.vcml:FrapIt", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_65311813.vcml:Stochastic", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_9254662.vcml:FastBuffExpReceptor", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_EXPRESSIONS);
		faults.put("biomodel_9590643.vcml:2-D", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_12119723.vcml:7_12_00_model2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_12522025.vcml:purkinje9-ss", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_12522025_spatial.vcml:purkinje9", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_26454052.vcml:1D PIP2_fits 3D model that uses experimentally derived 3D geometry", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_27192647.vcml:3D PIP2_experimentally derived geometry", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_28730491.vcml:Uniform", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:1X RIIb 1s dendritic cAMP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31584491.vcml:equal affinity", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:GoodModel17_5", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:SyGCaMP2_20AP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32579611.vcml:without PDE1C", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_34855932.vcml:cell5", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_43726934.vcml:compartmental", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_9590643.vcml:3-D", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_12119723.vcml:7_12_00_model3", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_27192647.vcml:3D PIP2_D=1_constructed geometry", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:1X RIIb 1s spine cAMP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31584491.vcml:half MAP2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:Sim2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:SyGCaMP2_1AP_v2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32579611.vcml:both PDEs", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_34855932.vcml:cell6", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_43726934.vcml:10 spine base condition", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_9590643.vcml:3-D-10 um/s2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:2X RIIb 1s dendritic cAMP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:Furaptra", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:SyGCaMP2_20AP_v2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32579611.vcml:no pde activity", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_34855932.vcml:cell7", MathCompareResults.Decision.MathDifferent_DIFFERENT_NUMBER_OF_VARIABLES);
		faults.put("biomodel_43726934.vcml:10 spine 10 fold Kr", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_9590643.vcml:3-D farther apart", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:6x RIIb 1s dendritic cAMP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:SyGCaMP2_1AP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:GCaMP2_1AP_v2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_43726934.vcml:10 spine 30% excess RIIb", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_9590643.vcml:3-D one chromosone away", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:2X RIIb 1s spine cAMP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:GCaMP2_20AP_v2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_9590643.vcml:3-D very far apart", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:6X RIIb 1s spine cAMP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:Furaptra_1AP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:SyGCaMP2_1AP_v3", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_9590643.vcml:3-D vfa - 2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:6X RIIb diffusible 1s dendritic cAMP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:SyGCaMP2_1AP_v2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:GCaMP2_1AP_v4", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:2X RIIb 1s dendritic cAMP fast CAT", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP_v2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:Furaptra_1AP_v4", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:6X RIIb 1s dendritic cAMP fast CAT", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:GCaMP2_1AP_v2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:SyGCaMP2_20AP_v4", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:6X RIIb diffusible 1s dendritic cAMP fast CAT", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:GCaMP2_20AP_v2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568356.vcml:SyGCaMP2_80Hz_v4", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:Dose-response", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:Furaptra_1AP_v2", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:2X RIIb 1s cAMP overall", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:SyGCaMP2_1AP_v3", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:6X RIIb 1s cAMP overall", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP_v3", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_31523791.vcml:6X uniform RIIb 1s cAMP overall", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:Furaptra_1AP_v3", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:GCaMP2_1AP_v3", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:GCaMP2_1AP_v4", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:Furaptra_1AP_v4", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:SyGCaMP2_20AP_v4", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:SyGCaMP2_80Hz_v4", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		faults.put("biomodel_32568171.vcml:GCaMP2_1AP", MathCompareResults.Decision.MathDifferent_VARIABLE_NOT_FOUND_AS_FUNCTION);
		return faults;
	}

	/**
	 * process all tests that are available - the slow set is parsed only and not processed.
	 */
	@Parameterized.Parameters
	public static Collection<String> testCases() {
		Predicate<String> diff_num_expr_filter = (t) -> knownFaults().containsKey(t) && knownFaults().get(t) == MathCompareResults.Decision.MathDifferent_DIFFERENT_EXPRESSION;
		Predicate<String> skipFilter = (t) -> !outOfMemorySet().contains(t) && !largeFileSet().contains(t);
		return Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter).collect(Collectors.toList());
	}

	@Test
	public void test_parse_vcml() throws Exception {
		InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(filename);
		String vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
				.lines().collect(Collectors.joining("\n"));

		System.out.println("testing test case "+filename);
		BioModel orig_biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		orig_biomodel.refreshDependencies();

		BioModel transformed_biomodel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));
		transformed_biomodel.refreshDependencies();

		boolean bTransformKMOLE = true;
		boolean bTransformVariables = false;

		if (bTransformKMOLE){
			BioModelTransforms.restoreOldReservedSymbolsIfNeeded(transformed_biomodel);
		}

		for (SimulationContext orig_simContext : orig_biomodel.getSimulationContexts()) {
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
			String applicationKey = filename+":"+new_simContext.getName();
			MathCompareResults.Decision knownFault = knownFaults().get(applicationKey);
			if (results.isEquivalent() && knownFault != null){
				Assert.fail("math equivalent for '"+applicationKey+"', but expecting '"+knownFault+"', remove known fault");
			}
			if (!results.isEquivalent()){
				if (knownFault == null) {
					Assert.fail("'"+applicationKey+"' expecting equivalent, " +
							"computed '"+results.decision+"', " +
							"details: " + results.toDatabaseStatus());
				}else{
					Assert.assertTrue("'"+applicationKey+"' expecting '"+knownFault+"', " +
									"computed '"+results.decision+"', " +
									"details: " + results.toDatabaseStatus(),
							knownFault == results.decision);
				}
			}
		}
	}

}
