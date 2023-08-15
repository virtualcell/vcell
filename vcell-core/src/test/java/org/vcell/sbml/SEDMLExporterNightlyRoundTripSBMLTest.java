package org.vcell.sbml;

import cbit.vcell.resource.NativeLib;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sedml.ModelFormat;
import org.vcell.test.RoundTrip;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
@Category({RoundTrip.class})
public class SEDMLExporterNightlyRoundTripSBMLTest extends SEDMLExporterNightlyRoundTrip {

	@BeforeClass
	public static void setup(){
		SEDMLExporterNightlyRoundTrip.setup();
	}

	@AfterClass
	public static void teardown() throws IOException {
		SEDMLExporterNightlyRoundTrip.teardown();
	}

	public SEDMLExporterNightlyRoundTripSBMLTest(TestCase testCase){
		super(testCase);
	}

	@Override
	Map<String, FAULT> knownFaults() {
		HashMap<String, FAULT> faults = new HashMap<>();
		faults.put("biomodel_123269393.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_r7 - biomodel needs fixing - MathOverrides has entry for non-existent parameter Kf_r7
		faults.put("biomodel_124562627.vcml", FAULT.NULL_POINTER_EXCEPTION); // CSG/analytic geometry issue - SBML model does not have any geometryDefinition. Cannot proceed with import
		faults.put("biomodel_156134818.vcml", FAULT.UNKNOWN_IDENTIFIER);  // species named 'I' conflicts with membrane parameter I - Unable to sort, unknown identifier I_Gs_GDI_accel_deg_copy6
		faults.put("biomodel_220138948.vcml", FAULT.MATHOVERRIDES_INVALID); // Kf_Uptake invalid override - MathOverrides has entry for non-existent parameter Kf_Uptake
		faults.put("biomodel_84982474.vcml", FAULT.UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM); // not supported nonspatial histogram


		return faults;
	}

	@Override
	Map<String, SEDML_FAULT> knownSEDMLFaults() {
		HashMap<String, SEDML_FAULT> faults = new HashMap<>();
		faults.put("__export_adv_test.vcml", SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME);  // round-tripped simulation not found with name 'spatialnoscan'
		faults.put("biomodel_100596964.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_100961371.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_113655498.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116929912.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116929971.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_116930032.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_123269393.vcml", SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME); // round-tripped simulation not found with name 'Simulation2'
		faults.put("biomodel_123465498.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_123465505.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_124562627.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_12522025.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT);  // simulation 'Simulation 1' in simContext 'purkinje9-ss'
		faults.put("biomodel_13717231.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);  // round-tripped simulationContext not found with name 'double -'
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
		faults.put("biomodel_220138948.vcml", SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME); // round-tripped simulation not found with name 'Uptake_Larger Jumps'
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
		faults.put("biomodel_65311813.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // simulation 'Copy of Simulation 101x101x36' in simContext '3d image'
		faults.put("biomodel_66264973.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // simulation 'Simulation - local sequestration' in simContext 'compartmental - IC-G2736X'
		faults.put("biomodel_66265579.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_74924130.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // simulation 'Simulation0' in simContext 'Application0'
		faults.put("biomodel_77305266.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_7803961.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_7803976.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_81284732.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_82250339.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_83446023.vcml", SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT); // can't find math override for Kf_dimerization
		faults.put("biomodel_83462243.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // round-tripped simulationContext not found with name 'spatial-hybrid'
		faults.put("biomodel_83651737.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);
		faults.put("biomodel_84982474.vcml", SEDML_FAULT.DIFF_NUMBER_OF_BIOMODELS); // not supported non-spatial histogram
		faults.put("biomodel_9254662.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_92705462.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME);  // round-tripped simulationContext not found with name 'stoch'
		faults.put("biomodel_97075423.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_97705317.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_97786619.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_97786886.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // round-tripped simulationContext not found with name 'hybrid'
		faults.put("biomodel_97787114.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // round-tripped simulationContext not found with name 'hybrid'
		faults.put("biomodel_98147638.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98150237.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX); // new
		faults.put("biomodel_98174143.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98296160.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);
		faults.put("biomodel_98730962.vcml", SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME); // round-tripped simulationContext not found with name 'Application0'
		faults.put("biomodel_165181964.vcml", SEDML_FAULT.NO_MODELS_IN_OMEX);


		// SEDML Validator Errors
		faults.put("biomodel_82065439.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  NON_UNIQUE_IDS:    Each identified SED object must have a unique id. Multiple objects have the following ids:",[["compartmental"]]
		faults.put("biomodel_220138948.vcml",SEDML_FAULT.OMEX_VALIDATION_ERRORS);  //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfSpecies/sbml:species[@id='OAT1']/@initialConcentration` does not match any elements of model `_0D`.
		faults.put("biomodel_31523791.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);      //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfSpecies/sbml:species[@id='cAMP_Intracellular']/@initialConcentration` does not match any elements of model `Dose_response`.
		faults.put("biomodel_34855932.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);      //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfParameters/sbml:parameter[@id='Kf_GPCR_to_ICSC']/@value` does not match any elements of model `cell5`
		faults.put("biomodel_40882931.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfSpecies/sbml:species[@id='ZO1staticF_PM']/@initialConcentration` does not match any elements of model `_3d_image`
		faults.put("biomodel_40883509.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfSpecies/sbml:species[@id='PIK_PM']/@initialConcentration` does not match any elements of model `_3d_image`
		faults.put("biomodel_65311813.vcml", SEDML_FAULT.OMEX_PARSER_ERRORS);  //  XPATH_BAD:  XPath `/sbml:sbml/sbml:model/sbml:listOfParameters/sbml:parameter[@id='Ran_nuc_diff']/@value` does not match any elements of model `_3d_image_0`
		return faults;
	}
	@Parameterized.Parameters
	public static Collection<TestCase> testCases() {
		File testResourceDir;

		// Check that the env var for the input directory has been set.
		if (SEDMLExporterNightlyRoundTrip.locationOfVcmlToTest == null){
			// We'll try to get the value again.
			Map<String, String> allEnv = System.getenv();
			SEDMLExporterNightlyRoundTrip.locationOfVcmlToTest = System.getenv(SEDMLExporterNightlyRoundTrip.LOCATION_ENV_VAR);
		}
		if (SEDMLExporterNightlyRoundTrip.locationOfVcmlToTest == null)
			throw new RuntimeException(
					String.format("Please set the `%s` environment variable with the path to the input file directory",
							SEDMLExporterNightlyRoundTrip.LOCATION_ENV_VAR));

		// Input validate the env var's value
		try {
			testResourceDir = (new File(SEDMLExporterNightlyRoundTrip.locationOfVcmlToTest)).getCanonicalFile();
		} catch (IOException e){
			String message = String.format("Unable to determine the canonical path to provided directory `%s`.\n"
					+ "\tPlease check that the `%s` environment variable has been set properly",
						SEDMLExporterNightlyRoundTrip.locationOfVcmlToTest, SEDMLExporterNightlyRoundTrip.LOCATION_ENV_VAR);
			throw new RuntimeException(message, e);
		}
		// Validate the status of the input directory as a valid directory full of VCMLs
		if (!testResourceDir.isDirectory()) throw new RuntimeException(
				String.format("Path `%s` provided by `%s` is not a directory", testResourceDir.getPath(),
						SEDMLExporterNightlyRoundTrip.LOCATION_ENV_VAR));

		// Prepare Filtering
		FilenameFilter exemptFilesFilter = new NightlyRoundTripVCMLFilenameFilter();
		Predicate<String> skipFilter_VCML = (t) -> exemptFilesFilter.accept(testResourceDir, t);

		// Collect Filenames to test with.
		File[] filenamesToTest = testResourceDir.listFiles(exemptFilesFilter);
		Stream<TestCase> vcmlTestCases = Arrays.stream(filenamesToTest).map(file -> new TestCase(file, ModelFormat.SBML));

		//Stream<TestCase> sbml_test_cases = Arrays.stream(VcmlTestSuiteFiles.getVcmlTestCases()).filter(skipFilter_VCML).map(fName -> new TestCase(fName, ModelFormat.SBML));
		//return sbml_test_cases.collect(Collectors.toList());
		return vcmlTestCases.collect(Collectors.toList());
//		return Arrays.asList(
//				new TestCase("biomodel_31523791.vcml", ModelFormat.SBML),
//				new TestCase("biomodel_34855932.vcml", ModelFormat.SBML),
//				new TestCase("biomodel_40882931.vcml", ModelFormat.SBML),
//				new TestCase("biomodel_40883509.vcml", ModelFormat.SBML),
//				new TestCase("biomodel_65311813.vcml", ModelFormat.SBML),
//				new TestCase("biomodel_82065439.vcml", ModelFormat.SBML)
//				);
	}

	@Test
	public void test_sedml_roundtrip_SBML() throws Exception {
		if (knownFaults().containsKey(testCase.VCML.getName())) {
			return; // skip known faults
		}
//		if (knownSEDMLFaults().get(testCase.filename) != SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT
//				&& knownSEDMLFaults().get(testCase.filename) != SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT){
//			return;
//		}
		sedml_roundtrip_common();
	}

}
