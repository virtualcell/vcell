package org.vcell.cli.run;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIRecordable;
import org.vcell.cli.PythonStreamException;
import org.vcell.sbml.vcell.SBMLImportException;
import org.vcell.trace.Span;
import org.vcell.trace.TraceEvent;
import org.vcell.trace.Tracer;
import org.vcell.util.VCellUtilityHub;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Tag("BSTS_IT")
public class BSTSBasedOmexExecTest {

	@BeforeAll
	public static void setup() throws PythonStreamException, IOException {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
		VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);

		PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, new File("../vcell-cli-utils").getAbsolutePath());
		VCMongoMessage.enabled = false;

		CLIPythonManager.getInstance().instantiatePythonProcess();
	}

	@AfterAll
	public static void teardown() throws Exception {
		CLIPythonManager.getInstance().closePythonProcess();
		VCellUtilityHub.shutdown();
	}

	@SuppressWarnings("unused")
	public enum FAULT {
		ARRAY_INDEX_OUT_OF_BOUNDS,
		BAD_EULER_FORWARD,
		DIVIDE_BY_ZERO,
		EXPRESSIONS_DIFFERENT,
		EXPRESSION_BINDING,
		GEOMETRY_SPEC_DIFFERENT,
		HDF5_FILE_ALREADY_EXISTS, // reports.h5 file already exists, so action is blocked. Fixed in branch to be merged in.
		MATHOVERRIDES_SurfToVol,
		MATH_GENERATION_FAILURE,
		MATH_OVERRIDES_A_FUNCTION,
		MATH_OVERRIDES_INVALID,
		NULL_POINTER_EXCEPTION,
		OPERATION_NOT_SUPPORTED, // VCell simply doesn't have the necessary features to run this archive.
		SBML_IMPORT_FAILURE,
		SEDML_DIFF_NUMBER_OF_BIOMODELS,
		SEDML_ERRONEOUS_UNIT_SYSTEM,
		SEDML_ERROR_CONSTRUCTING_SIMCONTEXT,
		SEDML_MATH_OVERRIDE_NAMES_DIFFERENT,
		SEDML_MATH_OVERRIDE_NOT_EQUIVALENT,
		SEDML_NONSPATIAL_STOCH_HISTOGRAM,
		SEDML_NO_MODELS_IN_OMEX,
		SEDML_SIMCONTEXT_NOT_FOUND_BY_NAME,
		SEDML_SIMULATION_NOT_FOUND_BY_NAME,
		SEDML_UNSUPPORTED_ENTITY,
		SEDML_UNSUPPORTED_MODEL_REFERENCE, // Model refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)
		TOO_SLOW,
		UNCATETORIZED_FAULT,
		UNITS_EXCEPTION,
		UNKNOWN_IDENTIFIER,
		SEDML_NO_SEDMLS_TO_EXECUTE, SEDML_PREPROCESS_FAILURE, UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM

	}

//	@SuppressWarnings("unused")
//	static Set<String> needToCurateModels(){
//		// These models are skipped by BSTS, and we need to look to see if that was because we don't have a KISAO exact match
//		HashSet<String> skippedModels = new HashSet<>();
//		skippedModels.add("sbml-core/Edelstein-Biol-Cybern-1996-Nicotinic-excitation.omex");
//		skippedModels.add("sbml-core/Szymanska-J-Theor-Biol-2009-HSP-synthesis.omex");
//		skippedModels.add("sbml-core/Tomida-EMBO-J-2003-NFAT-translocation.omex");
//		skippedModels.add("sbml-core/Varusai-Sci-Rep-2018-mTOR-signaling-LSODA-LSODAR-SBML.omex");
//		skippedModels.add("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-discrete-SSA.omex");
//		return skippedModels;
//	}

	static Set<String> blacklistedModels(){
		HashSet<String> blacklistSet = new HashSet<>();
		// We don't support the following tests because they require a model change for SBML Level
		blacklistSet.add("synths/sedml/SimulatorSupportsRepeatedTasksWithChanges/2.execution-should-succeed.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsComputeModelChanges/1.execute-should-fail.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsComputeModelChanges/2.execution-should-succeed.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsModelAttributeChanges/2.execution-should-succeed.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsRepeatedTasksWithFunctionalRangeVariables/1.execute-should-fail.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsRepeatedTasksWithFunctionalRangeVariables/2.execution-should-succeed.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsRepeatedTasksWithChanges/1.execute-should-fail.omex");
		return blacklistSet;
	}

	static Map<String, FAULT> knownFaults() {
		HashMap<String, FAULT> faults = new HashMap<>();
		faults.put("synths/sedml/SimulatorSupportsModelAttributeChanges/1.execute-should-fail.omex",
				FAULT.SEDML_ERRONEOUS_UNIT_SYSTEM);
		faults.put("synths/sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/1.execute-should-fail.omex",
				FAULT.SEDML_ERRONEOUS_UNIT_SYSTEM);
		faults.put("synths/sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/3.execute-should-fail.omex",
				FAULT.SEDML_ERRONEOUS_UNIT_SYSTEM);


		//faults.put("misc-projects/BIOMD0000000005.omex", null); // works
		faults.put("misc-projects/BIOMD0000000175.omex", FAULT.MATH_GENERATION_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_6652012719407098827omex) | SIMULATIONS_RUN(runSimulations) | **** Error: Unable to initialize bioModel for the given selection: MappingException occurred: failed to generate math: Unable to sort, unknown identifier I_Net_E44PPI3K_binding
		faults.put("misc-projects/BIOMD0000000302.omex", FAULT.MATH_GENERATION_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_8639905465728503850omex) | SIMULATIONS_RUN(runSimulations) | **** Error: Unable to initialize bioModel for the given selection: MappingException occurred: failed to generate math: generated an invalid mathDescription: Initial condition for variable 'h_post' references variable 'V_post'. Initial conditions cannot reference variables.
		//faults.put("misc-projects/BIOMD0000000520.omex", null); // works!!
		faults.put("misc-projects/BIOMD0000000561.omex", FAULT.UNCATETORIZED_FAULT); // (not a dynamic system - can't solve) | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_4023640363466179720omex) | SIMULATIONS_RUN(runSimulations) | BioModel(BSTS_OmexFile_4023640363466179720omex_Martins2013.sedml_model) | SIMULATION_RUN(task1_task1) | **** Error: Failed execution: Model 'BSTS_OmexFile_4023640363466179720omex_Martins2013.sedml_model' Task 'task1'.
		//faults.put("misc-projects/BIOMD0000000569.omex", null); // works!!
		faults.put("misc-projects/BIOMD0000000618.omex", FAULT.SBML_IMPORT_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_13012177097014737572omex) | SIMULATIONS_RUN(runSimulations) | **** Error: Error processing model: model2 - couldn't find SBase with sid=null in SBMLSymbolMapping
		//faults.put("misc-projects/BIOMD0000000651.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000668.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000669.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000676.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000718.omex", null); // works!!
		faults.put("misc-projects/BIOMD0000000731.omex", FAULT.SBML_IMPORT_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_7811157837075929926omex) | SIMULATIONS_RUN(runSimulations) | **** Error: Unable to initialize bioModel for the given selection: Failed to translate SBML model into BioModel: Error binding global parameter 'Treg_origin_fraction_CD4' to model: 'func_TRegs_Production_from_CD4' is either not found in your model or is not allowed to be used in the current context. Check that you have provided the correct and full name (e.g. Ca_Cytosol).
		//faults.put("misc-projects/BIOMD0000000842.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000843.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000932.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000944.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000951.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000957.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000968.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000972.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000973.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000983.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000985.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000989.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000991.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000997.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000001010.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000001014.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000001018.omex", null); // works!!
		faults.put("misc-projects/BIOMD0000001061.omex", FAULT.SEDML_NO_SEDMLS_TO_EXECUTE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_6151950245083004772omex) | **** Error: writeErrorList(): BSTS_OmexFile_6151950245083004772omex | java.lang.RuntimeException: There are no SED-MLs in the archive to execute
		faults.put("misc-projects/BIOMD0000001064.omex", FAULT.SEDML_NO_SEDMLS_TO_EXECUTE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_855930052238018769omex) | **** Error: writeErrorList(): BSTS_OmexFile_855930052238018769omex | java.lang.RuntimeException: There are no SED-MLs in the archive to execute
		//faults.put("misc-projects/BIOMD0000001072.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000001077.omex", null); // works!!

		return faults;
	}

	public static Collection<String> testCases() {
		Set<String> modelsToFilter = new HashSet<>();
		Predicate<String> filter;

//		modelsToFilter.addAll(needToCurateModels()); // Comment out if checking that current version will satisfy BSTS
		modelsToFilter.addAll(blacklistedModels());
		filter = (t) -> !modelsToFilter.contains(t);

		return Arrays.stream(BSTSBasedTestSuiteFiles.getBSTSTestCases()).filter(filter).collect(Collectors.toList());
//		return Arrays.asList(
//				"misc-projects/BIOMD0000000561.omex",
//				"misc-projects/BIOMD0000000679.omex",
//				"misc-projects/BIOMD0000000680.omex",
//				"misc-projects/BIOMD0000000681.omex",
//				"misc-projects/BIOMD0000000684.omex",
//				"misc-projects/BIOMD0000000724.omex",
//				"misc-projects/BIOMD0000000915.omex"
//				"misc-projects/BIOMD0000000302.omex",
//				"misc-projects/BIOMD0000000175.omex",
//				"misc-projects/BIOMD0000000618.omex",
//				"misc-projects/BIOMD0000000569.omex",
//				"misc-projects/BIOMD0000000973.omex",
//				"misc-projects/BIOMD0000000520.omex",
//				"misc-projects/BIOMD0000000731.omex",
//				"misc-projects/BIOMD0000001061.omex",
//				"misc-projects/BIOMD0000001064.omex",
//				"misc-projects/BIOMD0000000651.omex",
//				"misc-projects/BIOMD0000000668.omex",
//				"misc-projects/BIOMD0000000669.omex",
//				"misc-projects/BIOMD0000000676.omex",
//				"misc-projects/BIOMD0000000718.omex",
//				"misc-projects/BIOMD0000000842.omex",
//				"misc-projects/BIOMD0000000843.omex",
//				"misc-projects/BIOMD0000000932.omex",
//				"misc-projects/BIOMD0000000944.omex",
//				"misc-projects/BIOMD0000000951.omex",
//				"misc-projects/BIOMD0000000957.omex",
//				"misc-projects/BIOMD0000000968.omex",
//				"misc-projects/BIOMD0000000972.omex",
//				"misc-projects/BIOMD0000000983.omex",
//				"misc-projects/BIOMD0000000985.omex",
//				"misc-projects/BIOMD0000000989.omex",
//				"misc-projects/BIOMD0000000991.omex",
//				"misc-projects/BIOMD0000000997.omex",
//				"misc-projects/BIOMD0000001010.omex",
//				"misc-projects/BIOMD0000001014.omex",
//				"misc-projects/BIOMD0000001018.omex",
//				"misc-projects/BIOMD0000001072.omex",
//				"misc-projects/BIOMD0000001077.omex"
//				);
	}

	static class TestRecorder implements CLIRecordable {
		public TestRecorder() {
			Tracer.clearTraceEvents();
		}

		@Override
		public void writeDetailedErrorList(Exception e, String message) {
			System.err.println("writeDetailedErrorList(): " + message);
			Tracer.failure(e, "writeDetailedErrorList(): " + message);
		}
		@Override
		public void writeFullSuccessList(String message) {
			System.out.println("writeFullSuccessList(): " + message);
			Tracer.success("writeFullSuccessList(): " + message);
		}
		@Override
		public void writeErrorList(Exception e, String message) {
			System.err.println("writeErrorList(): " + message);
			Tracer.failure(e, "writeErrorList(): " + message);
		}
		@Override
		public void writeDetailedResultList(String message) {
			System.out.println("writeDetailedResultList(): " + message);
			Tracer.success("writeDetailedResultList(): " + message);
		}
		@Override
		public void writeSpatialList(String message) {
			System.out.println("writeSpatialList(): " + message);
			Tracer.success("writeSpatialList(): " + message);
		}
		@Override
		public void writeImportErrorList(Exception e, String message) {
			System.err.println("writeImportErrorList(): " + message);
			Tracer.failure(e, "writeImportErrorList(): " + message);
		}
	}


	@ParameterizedTest
	@MethodSource("testCases")
	public void testBSTSBasedOmex(String testCaseFilename) throws Exception {
		FAULT knownFault = knownFaults().get(testCaseFilename);
		try {
			System.out.println("running test " + testCaseFilename);

			Path outdirPath = Files.createTempDirectory("BSTS_OmexExecTest");
			InputStream omexInputStream = BSTSBasedTestSuiteFiles.getBSTSTestCase(testCaseFilename);
			Path omexFile = Files.createTempFile("BSTS_OmexFile_", "omex");
			FileUtils.copyInputStreamToFile(omexInputStream, omexFile.toFile());

			TestRecorder cliRecorder = new TestRecorder();
			ExecuteImpl.singleMode(omexFile.toFile(), outdirPath.toFile(), cliRecorder);
			List<TraceEvent> errorEvents = Tracer.getErrors();
			String errorMessages = (errorEvents.isEmpty()) ? "" : errorEvents.get(0).message + " " + errorEvents.get(0).exception;
			assertTrue(errorEvents.isEmpty(), "failure: '" + errorMessages + "'");
			if (knownFault != null) {
				fail("Expected error " + knownFault.name() + " but found no error");
			}
		} catch (Exception | AssertionFailedError e){
			System.err.println("========== Begin Tracer report ==========");
			Tracer.reportErrors(true);
			System.err.println("=========== End Tracer report ===========");
			e.printStackTrace(System.err);
			List<org.vcell.trace.TraceEvent> errorEvents = Tracer.getErrors();
			FAULT fault = this.determineFault(e, errorEvents);
			if (knownFault != null) {
				if (knownFault == fault) {
					System.err.println("Found expected error " + knownFault.name() + ": " + e.getMessage());
					return;
				} else {
					fail("Expected error " + knownFault.name() + " but found error " + fault.name() + ": " + e.getMessage());
					return;
				}
			} else {
				fail("unexpected error, add FAULT." + fault.name() + " to " + testCaseFilename);
			}
		
			throw new Exception("Test error: " + testCaseFilename + " failed improperly", e);
		}
	}

	private FAULT determineFault(Throwable caughtException, List<TraceEvent> errorEvents){ // Throwable because Assertion Error
		String errorMessage = caughtException.getMessage();
		if (errorMessage == null) errorMessage = ""; // Prevent nullptr exception

		if (caughtException instanceof Error && caughtException.getCause() != null)
			errorMessage = caughtException.getCause().getMessage();

		if (errorMessage.contains("refers to either a non-existent model")) { //"refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)"
			return FAULT.SEDML_UNSUPPORTED_MODEL_REFERENCE;
		} else if (errorMessage.contains("System IO encountered a fatal error")){
			Throwable subException = caughtException.getCause();
			//String subMessage = (subException == null) ? "" : subException.getMessage();
			if (subException instanceof FileAlreadyExistsException){
				return FAULT.HDF5_FILE_ALREADY_EXISTS;
			}
		} else if (errorMessage.contains("error while processing outputs: null")){
			Throwable subException = caughtException.getCause();
			if (subException instanceof ArrayIndexOutOfBoundsException){
				return FAULT.ARRAY_INDEX_OUT_OF_BOUNDS;
			}
		} else if (errorMessage.contains("nconsistent unit system in SBML model") ||
				errorMessage.contains("ust be of type")){
			return FAULT.SEDML_ERRONEOUS_UNIT_SYSTEM;
		} else if (errorMessage.contains("There are no SED-MLs in the archive to execute")) {
			return FAULT.SEDML_NO_SEDMLS_TO_EXECUTE;
		} else if (errorMessage.contains("MappingException occurred: failed to generate math")) {
			return FAULT.MATH_GENERATION_FAILURE;
		}

		// else check Tracer error events for known faults
		for (TraceEvent event : errorEvents) {
			if (event.hasException(SBMLImportException.class)) {
				return FAULT.SBML_IMPORT_FAILURE;
			}
			if (event.span.getNestedContextName().contains(Span.ContextType.PROCESSING_SEDML.name()+"(preProcessDoc)")){
				return FAULT.SEDML_PREPROCESS_FAILURE;
			}
			if (event.hasException(MappingException.class)) {
				return FAULT.MATH_GENERATION_FAILURE;
			}
		}

		return FAULT.UNCATETORIZED_FAULT;
	}

}
