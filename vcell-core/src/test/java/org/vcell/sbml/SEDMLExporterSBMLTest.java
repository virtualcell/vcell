package org.vcell.sbml;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sedml.ModelFormat;
import org.vcell.test.SEDML_SBML_IT;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
@Category({SEDML_SBML_IT.class})
public class SEDMLExporterSBMLTest extends SEDMLExporterCommon {

	public SEDMLExporterSBMLTest(TestCase testCase){
		super(testCase);
	}

	/**
	 * 	each file in the slowTestSet takes > 10s on disk and is not included in the unit test (move to integration testing)
	 */
	static Set<String> slowTestSet() {
		Set<String> slowModels = new HashSet<>();
		slowModels.add("biomodel_101981216.vcml");    // 29s
		slowModels.add("biomodel_147699816.vcml");    // 23s
		slowModels.add("biomodel_17326658.vcml");     // 24s
		slowModels.add("biomodel_188880263.vcml");    // 89s
		slowModels.add("biomodel_200301029.vcml");    // 124s (and fails with java.lang.OutOfMemoryError: GC overhead limit exceeded)
		slowModels.add("biomodel_200301683.vcml");    // 37s
		slowModels.add("biomodel_28625786.vcml");     // 57s
		slowModels.add("biomodel_34826524.vcml");     // 129s
		slowModels.add("biomodel_47429473.vcml");     // 194s
		slowModels.add("biomodel_59361239.vcml");     // 116s
		slowModels.add("biomodel_60799209.vcml");     // 41s
		slowModels.add("biomodel_61699798.vcml");     // 69s
		slowModels.add("biomodel_62467093.vcml");     // 23s
		slowModels.add("biomodel_62477836.vcml");     // 30s
		slowModels.add("biomodel_62585003.vcml");     // 24s
		slowModels.add("biomodel_66264206.vcml");     // 45s
		slowModels.add("biomodel_93313420.vcml");     // 137s
		slowModels.add("biomodel_9590643.vcml");      // 40s
		return slowModels;
	}

	static Set<String> outOfMemorySet() {
		Set<String> outOfMemoryModels = new HashSet<>();
		outOfMemoryModels.add("biomodel_101963252.vcml"); // FAULT.JAVA_HEAP_SPACE
		outOfMemoryModels.add("biomodel_200301029.vcml"); // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded (and 124s)
		outOfMemoryModels.add("biomodel_26455186.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
		outOfMemoryModels.add("biomodel_27192647.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
		outOfMemoryModels.add("biomodel_27192717.vcml");  // FAULT.OUT_OF_MEMORY) - Java heap space: failed reallocation of scalar replaced objects
		return outOfMemoryModels;
	}

	@Override
	Map<String, SEDMLExporterCommon.FAULT> knownFaults() {
		HashMap<String, FAULT> faults = new HashMap<>();
		faults.put("biomodel_123269393.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_r7 - biomodel needs fixing
		faults.put("biomodel_124562627.vcml", FAULT.NULL_POINTER_EXCEPTION); // CSG/analytic geometry issue
		faults.put("biomodel_156134818.vcml", FAULT.UNKNOWN_IDENTIFIER);  // species named I conflicts with membrane parameter I
		faults.put("biomodel_220138948.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_Uptake invalid override.
		faults.put("biomodel_84982474.vcml", FAULT.UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM); // not supported nonspatial histogram
		return faults;
	}

	@Override
	Map<String, SEDMLExporterCommon.SEDML_FAULT> knownSEDMLFaults() {
		HashMap<String, SEDML_FAULT> faults = new HashMap<>();
		faults.put("__export_adv_test.vcml", SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME);  // roundtripped simulation not found with name 'spatialnoscan'
		faults.put("biomodel_100596964.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_100961371.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_113655498.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116929912.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116929971.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116930032.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_123269393.vcml", SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME); // roundtripped simulation not found with name 'Simulation2'
		faults.put("biomodel_123465498.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_123465505.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_124562627.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_12522025.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT);  // simulation 'Simulation 1' in simContext 'purkinje9-ss'
		faults.put("biomodel_13717231.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);  // roundtripped simulationContext not found with name 'double -'
		faults.put("biomodel_13736736.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_145545992.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_148700996.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // simulation 'Full sim' in simContext 'Fast B Compartmental'
		faults.put("biomodel_154961582.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_155016832.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_156134818.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_16763273.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_16804037.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_168717401.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_169993006.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_17098642.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_17257105.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT);  // simulation '1 uM iso' in simContext 'compartmental'
		faults.put("biomodel_188880263.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_18894555.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT);  // simulation 'baseline' in simContext 'compartmental'
		faults.put("biomodel_189321805.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);  // new
		faults.put("biomodel_189512756.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_189513183.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_20253928.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_211839191.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_220138948.vcml", SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME); // roundtripped simulation not found with name 'Uptake_Larger Jumps'
		faults.put("biomodel_22403233.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403238.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403244.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403250.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403358.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_22403576.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_225440511.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_232498815.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2912851.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2913730.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2915537.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917738.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917788.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2917999.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2930915.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_2962862.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_32568171.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_32568356.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_34826524.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Cap=1, cof, prof varied' in simContext 'Steady State Turnover'
		faults.put("biomodel_34855932.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'INIT' in simContext 'cell4'
		faults.put("biomodel_38086434.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX); // new
		faults.put("biomodel_55178308.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX); // new
		faults.put("biomodel_55396830.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_59280306.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // simulation '1 uM iso' in simContext 'compartmental'
		faults.put("biomodel_60203358.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_60227051.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_63307133.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_65311813.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // simulation 'Copy of Copy of Simulation 101x101x36' in simContext '3d image'
		faults.put("biomodel_66264973.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // simulation 'Simulation - local sequestration' in simContext 'compartmental - IC-G2736X'
		faults.put("biomodel_66265579.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_74924130.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // simulation 'Simulation0' in simContext 'Application0'
		faults.put("biomodel_77305266.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_7803961.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_7803976.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_81284732.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_82250339.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_83446023.vcml", SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT); // simulation 'Simulation0' in simContext 'compartmental'
		faults.put("biomodel_83462243.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // roundtripped simulationContext not found with name 'spatial-hybrid'
		faults.put("biomodel_83651737.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);
		faults.put("biomodel_84982474.vcml", SEDML_FAULT.DIFF_NUMBER_OF_BIOMODELS); // not supported nonspatial histogram
		faults.put("biomodel_9254662.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_92705462.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);  // roundtripped simulationContext not found with name 'stoch'
		faults.put("biomodel_97075423.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_97705317.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_97786619.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_97786886.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // roundtripped simulationContext not found with name 'hybrid'
		faults.put("biomodel_97787114.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // roundtripped simulationContext not found with name 'hybrid'
		faults.put("biomodel_98147638.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98150237.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX); // new
		faults.put("biomodel_98174143.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98296160.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98730962.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // roundtripped simulationContext not found with name 'Application0'
		faults.put("biomodel_165181964.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		return faults;
	}

	@Parameterized.Parameters
	public static Collection<TestCase> testCases() {
		Predicate<String> skipFilter_SBML = (t) -> !outOfMemorySet().contains(t) && !largeFileSet().contains(t) && !slowTestSet().contains(t);
		Stream<TestCase> sbml_test_cases = Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter_SBML).map(fname -> new TestCase(fname, ModelFormat.SBML));
		List<TestCase> testCases = sbml_test_cases.collect(Collectors.toList());
		return testCases;
		//return Arrays.asList(new TestCase("biomodel_101981216.vcml", ModelFormat.VCML));
	}

	@Test
	public void test_sedml_roundtrip_SBML() throws Exception {
		if (knownSEDMLFaults().get(testCase.filename) != SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT
				&& knownSEDMLFaults().get(testCase.filename) != SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT){
			return;
		}
		sedml_roundtrip_common();
	}

}
