package org.vcell.sbml;

import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.ModelUnitConverter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.MathOverrides;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationSymbolTable;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import com.google.common.io.Files;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sedml.*;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public abstract class SEDMLExporterCommon {

	record UnsupportedApplication(String filename, String applicationName, String reason) {
	}

	static class TestCase {
		public final String filename;
		public final ModelFormat modelFormat;

		public TestCase(String filename, ModelFormat modelFormat){
			this.filename = filename;
			this.modelFormat = modelFormat;
		}
	}


	private static boolean bDebug = false;

	//
	// save state for zero side-effect
	//
	private static String previousInstalldirPropertyValue;
	private static String previousWorkingdirPropertyValue;
	private static boolean previousWriteDebugFiles;

	public enum FAULT {
		EXPRESSIONS_DIFFERENT,
		EQUATION_REMOVED,
		EXPRESSION_BINDING,
		MATH_GENERATION_FAILURE,
		MATHOVERRIDES_INVALID,
		MATHOVERRIDES_SurfToVol,
		MATHOVERRIDES_A_FUNCTION,
		UNITS_EXCEPTION,
		TOO_SLOW,
		GEOMETRYSPEC_DIFFERENT,
		NULL_POINTER_EXCEPTION,
		UNKNOWN_IDENTIFIER,
		SBML_IMPORT_FAILURE,
		DIVIDE_BY_ZERO,
		UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM,
		SEDML_UNSUPPORTED_ENTITY
	};

	public enum SEDML_FAULT {
		DIFF_NUMBER_OF_BIOMODELS,
		OMEX_PARSER_ERRORS,
		OMEX_VALIDATION_ERRORS,
		NO_MODELS_IN_OMEX,
		ERROR_CONSTRUCTING_SIMCONTEXT,
		NONSPATIAL_STOCH_HISTOGRAM,
		MATH_OVERRIDE_NOT_EQUIVALENT,
		MATH_OVERRIDE_NAMES_DIFFERENT,
		SIMCONTEXT_NOT_FOUND_BY_NAME,
		MATH_DIFFERENT,
		SIMULATION_NOT_FOUND_BY_NAME
	};

	@BeforeAll
	public static void setup(){
		previousInstalldirPropertyValue = PropertyLoader.getProperty(PropertyLoader.installationRoot, null);
		PropertyLoader.setProperty(PropertyLoader.installationRoot, "..");
		previousWorkingdirPropertyValue = PropertyLoader.getProperty(PropertyLoader.cliWorkingDir, null);
		PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, "../vcell-cli-utils");
		previousWriteDebugFiles = SBMLExporter.bWriteDebugFiles;
		SBMLExporter.bWriteDebugFiles = bDebug;
	}

	@AfterAll
	public static void teardown() throws IOException {
		if (previousInstalldirPropertyValue!=null) {
			PropertyLoader.setProperty(PropertyLoader.installationRoot, previousInstalldirPropertyValue);
		}
		if (previousWorkingdirPropertyValue!=null) {
			PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, previousWorkingdirPropertyValue);
		}
		SBMLExporter.bWriteDebugFiles = previousWriteDebugFiles;
	}

	/**
	 * each file in largeFileSet is > 500K on disk and is not included in the test suite.
	 * @return
	 */
	public static Set<String> largeFileSet() {
		Set<String> largeFiles = new HashSet<>();
		largeFiles.add("biomodel_101963252.vcml");
		return largeFiles;
	}

	/**
	 * each file in the knownFaultsMap hold known problems in the current software
	 */
	abstract Map<String, FAULT> knownFaults();


	abstract Map<String, SEDML_FAULT> knownSEDMLFaults();

	abstract Set<UnsupportedApplication> unsupportedApplications();


	void sedml_roundtrip_common(TestCase testCase) throws Exception {
		String test_case_name = testCase.modelFormat+"."+testCase.filename;
		InputStream testFileInputStream = VcmlTestSuiteFiles.getVcmlTestCase(testCase.filename);
		String vcmlStr = new BufferedReader(new InputStreamReader(testFileInputStream))
				.lines().collect(Collectors.joining("\n"));

		BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlStr));

		bioModel.updateAll(false);

		Predicate<Simulation> simulationExportFilter = sim -> true;
		List<Simulation> simsToExport = Arrays.stream(bioModel.getSimulations()).filter(simulationExportFilter).collect(Collectors.toList());

		// we replace the obsolete solver with the fully supported equivalent
		for (Simulation simulation : simsToExport) {
			if (simulation.getSolverTaskDescription().getSolverDescription().equals(SolverDescription.FiniteVolume)) {
				simulation.getSolverTaskDescription().setSolverDescription(SolverDescription.SundialsPDE);
			}
		}
		File outputDir = Files.createTempDir();
		String jsonFullyQualifiedName = new File(outputDir, test_case_name + ".json").getAbsolutePath();
		System.out.println(jsonFullyQualifiedName);

		boolean bHasPython = true;
		boolean bRoundTripSBMLValidation = true;
		boolean bWriteOmexArchive = true;
		File omexFile = new File(outputDir, test_case_name + ".omex");
		Optional<PublicationMetadata> publicationMetadata = Optional.empty();
		Set<UnsupportedApplication> unsupportedApplications = SEDMLExporter.getUnsupportedApplicationMap(bioModel, testCase.modelFormat)
				.entrySet().stream().map(e -> new UnsupportedApplication(testCase.filename, e.getKey(), e.getValue())).collect(Collectors.toSet());
		Set<UnsupportedApplication> declaredUnsupportedApplications = unsupportedApplications().stream()
				.filter(ua -> ua.filename.equals(testCase.filename)).collect(Collectors.toSet());
		if (!declaredUnsupportedApplications.equals(unsupportedApplications)){
			System.err.println("declared unsupported applications for model "+test_case_name+" do not match actual, add the following to unsupportedApplications():");
			for (UnsupportedApplication ua : unsupportedApplications){
				System.err.println("unsupportedApplications.add(new UnsupportedApplication(\""+ua.filename+"\",\""+ua.applicationName+"\",\""+ua.reason+"\"));");
			}
			assertEquals(declaredUnsupportedApplications, unsupportedApplications,
					"declared unsupported applications for model "+test_case_name+" do not match actual:\ndeclared:\n"+declaredUnsupportedApplications+"\nfound\n"+unsupportedApplications);
		}
		assertEquals(declaredUnsupportedApplications, unsupportedApplications,
				"declared unsupported applications for model "+test_case_name+" do not match actual:\ndeclared:\n"+declaredUnsupportedApplications+"\nfound\n"+unsupportedApplications);
		Predicate<SimulationContext> simContextFilter = (SimulationContext sc) -> unsupportedApplications.stream().noneMatch(ua -> ua.applicationName.equals(sc.getName()));
		try {
			List<SEDMLTaskRecord> sedmlTaskRecords = SEDMLExporter.writeBioModel(
					bioModel, publicationMetadata, omexFile, testCase.modelFormat, simContextFilter, bHasPython, bRoundTripSBMLValidation, bWriteOmexArchive);

			boolean bAnyFailures = false;
			for (SEDMLTaskRecord sedmlTaskRecord : sedmlTaskRecords) {
				boolean bFailed = false;
				switch (sedmlTaskRecord.getTaskType()) {
					case BIOMODEL: {
						break;
					}
					case SIMCONTEXT: {
						bFailed = sedmlTaskRecord.getTaskResult() == TaskResult.FAILED &&
								(sedmlTaskRecord.getException() == null || !sedmlTaskRecord.getException().getClass().equals(UnsupportedSbmlExportException.class));
						break;
					}
					case UNITS:
					case SIMULATION: {
						bFailed = sedmlTaskRecord.getTaskResult() == TaskResult.FAILED;
						break;
					}
				}
				bAnyFailures |= bFailed;
				if (!knownFaults().containsKey(testCase.filename)) {
					// skip assert if the model is known to have a problem
					assertFalse(bFailed, sedmlTaskRecord.getCSV());
				}
			}
			FAULT knownFault = knownFaults().get(testCase.filename);
			if (knownFault != null){
				// if in the knownFaults list, the model should fail
				// this is how we can keep track of our list of known problems
				// if we fix a model without removing it from the knownFaults map, then this test will fail
                assertTrue(bAnyFailures, "expecting fault " + knownFault.name() + " in " + test_case_name + " but it passed, Please remove from SEDMLExporterTest.knownFaults()");
			}

			if (testCase.modelFormat == ModelFormat.VCML){
				System.err.println("skipping re-importing SEDML for this test case, not yet supported for VCML");
				return;
			}
			SBMLExporter.MemoryVCLogger memoryVCLogger = new SBMLExporter.MemoryVCLogger();
			List<BioModel> bioModels = XmlHelper.readOmex(omexFile, memoryVCLogger);
            Assertions.assertTrue(memoryVCLogger.highPriority.size() == 0);

			File tempDir = Files.createTempDir();
			String origVcmlPath = new File(tempDir, "orig.vcml").getAbsolutePath();
			XmlUtil.writeXMLStringToFile(XmlHelper.bioModelToXML(bioModel), origVcmlPath, true);

			if (bDebug) {
				SBMLExporter.bWriteDebugFiles=true;
				for (int i = 0; i < bioModels.size(); i++) {
					String rereadVcmlPath = new File(tempDir, "reread_" + i + ".vcml").getAbsolutePath();
					XmlUtil.writeXMLStringToFile(XmlHelper.bioModelToXML(bioModels.get(i)), rereadVcmlPath, true);
				}
				System.err.println("wrote original and final BioModel VCML files to " + tempDir.getAbsolutePath());
			}

			assertEquals(1, bioModels.size(), "expecting 1 biomodel in round trip");
			BioModel importedBioModel = bioModels.get(0);

			boolean hasAnyOverrides = Arrays.stream(bioModel.getSimulations()).anyMatch(s -> s.getMathOverrides().getSize() > 0);
			if (testCase.modelFormat == ModelFormat.SBML && hasAnyOverrides){
				importedBioModel = ModelUnitConverter.createBioModelWithNewUnitSystem(importedBioModel, bioModel.getModel().getUnitSystem());
			}

			//
			// compare Math Overrides for each simulation
			//
			for (Simulation simToExport : simsToExport){
				SimulationContext simContextToExport = bioModel.getSimulationContext(simToExport);
				String simContextName = simContextToExport.getName();
				String simName = simToExport.getName();
				SimulationContext simContextRoundTripped = importedBioModel.getSimulationContext(simContextName);
				assertNotNull(simContextRoundTripped, "roundtripped simulationContext not found with name '" + simContextName + "'");
				Simulation simRoundTripped = simContextRoundTripped.getSimulation(simName);
				assertNotNull(simRoundTripped, "roundtripped simulation not found with name '" + simName + "'");
				boolean mathOverrideEquiv = simToExport.getMathOverrides().compareEquivalent(simRoundTripped.getMathOverrides());
				if (simToExport.getNumTrials()>1){
					throw new SEDMLExporter.SEDMLExportException("trials not suppported for SEDML export");
				}
				if (!mathOverrideEquiv){
					//
					// math overrides didn't compare as equivalent, if overridden names are different, try substituting into math and comparing math
					//
					List<String> oldOverrideNames = Arrays.stream(simToExport.getMathOverrides().getOverridenConstantNames()).sorted().collect(Collectors.toList());
					List<String> newOverrideNames = Arrays.stream(simRoundTripped.getMathOverrides().getOverridenConstantNames()).sorted().collect(Collectors.toList());
					if (!oldOverrideNames.equals(newOverrideNames) && (simToExport.getScanCount() == simRoundTripped.getScanCount())){
						// simulation scan counts are the same, but overridden constants have different names, try substituting them into the math and compare the maths.
						System.out.println("old names: "+oldOverrideNames+", new names: "+newOverrideNames);
						for (int scan = 0; scan < simToExport.getScanCount(); scan++){
							MathOverrides.ScanIndex scanIndex = new MathOverrides.ScanIndex(scan);
							MathDescription oldMathDescription = new SimulationSymbolTable(simToExport, scanIndex).getMathDescription();
							MathDescription newMathDescription = new SimulationSymbolTable(simRoundTripped, scanIndex).getMathDescription();
							MathCompareResults subCompareResults = MathDescription.testEquivalency(
									SimulationSymbolTable.createMathSymbolTableFactory(), oldMathDescription, newMathDescription);
                            assertTrue(subCompareResults.isEquivalent(), "math overrides names not equivalent for simulation '" + simName + "' " +
                                    "in simContext '" + simContextName + "'");
							mathOverrideEquiv = true;
						}
					}
				}
                assertTrue(mathOverrideEquiv, "math overrides not equivalent for simulation '" + simName + "' in simContext '" + simContextName + "'");
			}

			assertNull(knownSEDMLFaults().get(testCase.filename), "file " + test_case_name + " passed SEDML Round trip, but knownSEDMLFault was set");
		}catch (Exception | AssertionError e){
			if (e instanceof OmexPythonUtils.OmexValidationException){
				OmexPythonUtils.OmexValidationException validationException = (OmexPythonUtils.OmexValidationException) e;
				if (validationException.errors.stream()
						.anyMatch(err -> err.type == OmexPythonUtils.OmexValidationErrorType.OMEX_PARSE_ERROR)){
					if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.OMEX_PARSER_ERRORS) {
						System.err.println("Expected error: "+e.getMessage());
						return;
					}else{
						System.err.println("add SEDML_FAULT.OMEX_PARSER_ERRORS to "+test_case_name+": "+e.getMessage());
					}
				}
				if (validationException.errors.stream()
						.anyMatch(err -> err.type == OmexPythonUtils.OmexValidationErrorType.OMEX_VALIDATION_ERROR)){
					if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.OMEX_VALIDATION_ERRORS) {
						System.err.println("Expected error: "+e.getMessage());
						return;
					}else{
						System.err.println("add SEDML_FAULT.OMEX_VALIDATION_ERRORS to "+test_case_name+": "+e.getMessage());
					}
				}
			}
			if (e.getMessage()!=null && e.getMessage().contains("There are no models in ")){
				if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.NO_MODELS_IN_OMEX) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				}else{
					System.err.println("add SEDML_FAULT.NO_MODELS_IN_OMEX to "+test_case_name);
				}
			}
			if (e.getMessage()!=null && e.getMessage().contains("expecting 1 biomodel in round trip")){
				if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.DIFF_NUMBER_OF_BIOMODELS) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				}else{
					System.err.println("add SEDML_FAULT.DIFF_NUMBER_OF_BIOMODELS to "+test_case_name);
				}
			}
			if (e.getMessage()!=null && e.getMessage().contains("Error constructing a new simulation context")) {
				if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.ERROR_CONSTRUCTING_SIMCONTEXT) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				} else {
					System.err.println("add SEDML_FAULT.ERROR_CONSTRUCTING_SIMCONTEXT to " + test_case_name);
				}
			}
			if (e.getMessage()!=null && e.getMessage().contains("non-spatial stochastic simulation with histogram option to SEDML not supported")) {
				if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.NONSPATIAL_STOCH_HISTOGRAM) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				} else {
					System.err.println("add SEDML_FAULT.NONSPATIAL_STOCH_HISTOGRAM to " + test_case_name);
				}
			}
			if (e.getMessage()!=null && e.getMessage().contains("math overrides not equivalent for simulation")) {
				if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				} else {
					System.err.println("add SEDML_FAULT.MATH_OVERRIDE_NOT_EQUIVALENT to " + test_case_name);
				}
			}
			if (e.getMessage()!=null && e.getMessage().contains("math overrides names not equivalent for simulation")) {
				if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				} else {
					System.err.println("add SEDML_FAULT.MATH_OVERRIDE_NAMES_DIFFERENT to " + test_case_name);
				}
			}
			if (e.getMessage()!=null && e.getMessage().contains("roundtripped simulationContext not found with name")) {
				if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				} else {
					System.err.println("add SEDML_FAULT.SIMCONTEXT_NOT_FOUND_BY_NAME to " + test_case_name);
				}
			}
			if (e.getMessage()!=null && e.getMessage().contains("roundtripped simulation not found with name")) {
				if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				} else {
					System.err.println("add SEDML_FAULT.SIMULATION_NOT_FOUND_BY_NAME to " + test_case_name);
				}
			}
			if (e.getMessage()!=null && e.getMessage().contains("could not be exported to SBML :MathDescriptions not equivalent after VCML->SBML->VCML")) {
				if (knownSEDMLFaults().get(testCase.filename) == SEDML_FAULT.MATH_DIFFERENT) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				} else {
					System.err.println("add SEDML_FAULT.MATH_DIFFERENT to " + test_case_name);
				}
			}
			throw e;
		}
	}

}
