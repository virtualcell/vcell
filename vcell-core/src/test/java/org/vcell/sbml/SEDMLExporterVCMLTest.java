package org.vcell.sbml;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sedml.ModelFormat;
import org.vcell.test.SEDML_VCML_IT;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
@Category({SEDML_VCML_IT.class})
public class SEDMLExporterVCMLTest extends SEDMLExporterCommon {

	public SEDMLExporterVCMLTest(TestCase testCase){
		super(testCase);
	}

	static Set<String> slowTestSet(){
		Set<String> slowModels = new HashSet<>();
		return slowModels;
	}

	public static Set<String> outOfMemorySet() {
		Set<String> outOfMemoryModels = new HashSet<>();
		outOfMemoryModels.add("biomodel_101963252.vcml"); // FAULT.JAVA_HEAP_SPACE
		outOfMemoryModels.add("biomodel_26455186.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
		outOfMemoryModels.add("biomodel_27192647.vcml");  // FAULT.OUT_OF_MEMORY - GC Overhead Limit Exceeded
		outOfMemoryModels.add("biomodel_27192717.vcml");  // FAULT.OUT_OF_MEMORY) - Java heap space: failed reallocation of scalar replaced objects
		return outOfMemoryModels;
	}

	/**
	 * each file in the knownFaultsMap hold known problems in the current software
	 */
	@Override
	public Map<String, FAULT> knownFaults() {
		HashMap<String, FAULT> faults = new HashMap();
		faults.put("biomodel_123269393.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_r7 - biomodel needs fixing
		faults.put("biomodel_188880263.vcml", FAULT.SEDML_UNSUPPORTED_ENTITY); // Unsupported entity in VCML model export: class cbit.vcell.mapping.ParameterContext$LocalParameter"
		faults.put("biomodel_220138948.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_Uptake invalid override.
		faults.put("biomodel_55178308.vcml", FAULT.MATHOVERRIDES_INVALID); // VolFract_ER_spine invalid override.
		faults.put("biomodel_84982474.vcml", FAULT.UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM); // not supported nonspatial histogram
		return faults;
	}

	@Override
	public Map<String, SEDML_FAULT> knownSEDMLFaults() {
		HashMap<String, SEDML_FAULT> faults = new HashMap();
		faults.put("biomodel_28625786.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  NON_UNIQUE_IDS:    Each identified SED object must have a unique id. Multiple objects have the following ids:",[["compartmental"]]
		faults.put("biomodel_60203358.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  NON_UNIQUE_IDS:    Each identified SED object must have a unique id. Multiple objects have the following ids:",[["IKsum"]]
		faults.put("biomodel_82065439.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  NON_UNIQUE_IDS:    Each identified SED object must have a unique id. Multiple objects have the following ids:",[["compartmental"]]
//		faults.put("biomodel_165181964.vcml", SEDML_FAULT.OMEX_VALIDATION_ERRORS); //  EXCEPTION IN VALIDATOR: ValueError: 'KISAO' is not an id for a KiSAO term.
		return faults;
	}


	@Parameterized.Parameters
	public static Collection<TestCase> testCases() {
		Predicate<String> skipFilter_VCML = (t) ->
				!outOfMemorySet().contains(t) &&
				!largeFileSet().contains(t) &&
				!slowTestSet().contains(t) &&
				!t.equals("biomodel_165181964.vcml"); // skip this test because it causes exception in SEDML processing
		Stream<TestCase> vcml_test_cases = Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter_VCML).map(fname -> new TestCase(fname, ModelFormat.VCML));
		List<TestCase> testCases = vcml_test_cases.collect(Collectors.toList());
		return testCases;
//		return Arrays.asList(
//				new TestCase("biomodel_123269393.vcml", ModelFormat.VCML),
//				new TestCase("biomodel_188880263.vcml", ModelFormat.VCML),
//				new TestCase("biomodel_220138948.vcml", ModelFormat.VCML),
//				new TestCase("biomodel_28625786.vcml", ModelFormat.VCML),
//				new TestCase("biomodel_55178308.vcml", ModelFormat.VCML),
//				new TestCase("biomodel_60203358.vcml", ModelFormat.VCML),
//				new TestCase("biomodel_82065439.vcml", ModelFormat.VCML),
//				new TestCase("biomodel_84982474.vcml", ModelFormat.VCML)
//		);
	}

	@Test
	public void test_sedml_roundtrip() throws Exception {
		if (knownFaults().containsKey(testCase.filename)) {
			return; // skip known faults
		}
		sedml_roundtrip_common();
	}

}
