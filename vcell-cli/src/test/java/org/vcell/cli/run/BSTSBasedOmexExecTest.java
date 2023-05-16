package org.vcell.cli.run;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIRecordable;
import org.vcell.cli.PythonStreamException;
import org.vcell.test.BSTS_IT;
import org.vcell.util.VCellUtilityHub;

@RunWith(Parameterized.class)
@Category(BSTS_IT.class)
public class BSTSBasedOmexExecTest {
	private final String testCaseFilename;

	public BSTSBasedOmexExecTest(String testCase){
		this.testCaseFilename = testCase;
	}

	@BeforeClass
	public static void setup() throws PythonStreamException, IOException {
		System.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
		NativeLib.HDF5.load();
		NativeLib.combinej.load();
		VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);

		System.setProperty(PropertyLoader.cliWorkingDir, new File("../vcell-cli-utils").getAbsolutePath());
		VCMongoMessage.enabled = false;

		CLIPythonManager.getInstance().instantiatePythonProcess();
	}

	@BeforeClass
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
		UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM

	}

	@SuppressWarnings("unused")
	static Set<String> needToCurateModels(){
		// These models are skipped by BSTS, and we need to look to see if that was because we don't have a KISAO exact match
		HashSet<String> skippedModels = new HashSet<>();
		skippedModels.add("sbml-core/Edelstein-Biol-Cybern-1996-Nicotinic-excitation.omex");
		skippedModels.add("sbml-core/Szymanska-J-Theor-Biol-2009-HSP-synthesis.omex");
		skippedModels.add("sbml-core/Tomida-EMBO-J-2003-NFAT-translocation.omex");
		skippedModels.add("sbml-core/Varusai-Sci-Rep-2018-mTOR-signaling-LSODA-LSODAR-SBML.omex");
		skippedModels.add("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-discrete-SSA.omex");
		return skippedModels;
	}

	static Set<String> blacklistedModels(){
		HashSet<String> blacklistSet = new HashSet<>();
		// We don't support the following tests yet
		blacklistSet.add("synths/sedml/SimulatorSupportsRepeatedTasksWithSubTasksOfMixedTypes/1.execution-should-succeed.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsRepeatedTasksWithSubTasksOfMixedTypes/2.execution-should-succeed.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsDataGeneratorsWithDifferentShapes/1.execution-should-succeed.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsDataSetsWithDifferentShapes/1.execution-should-succeed.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsRepeatedTasksWithMultipleSubTasks/1.execution-should-succeed.omex");
		blacklistSet.add("synths/sedml/SimulatorSupportsUniformTimeCoursesWithNonZeroInitialTimes/1.execution-should-succeed.omex");
		blacklistSet.add("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock.omex");
		return blacklistSet;
	}

	static Map<String, FAULT> knownFaults() {
		HashMap<String, FAULT> faults = new HashMap<>();
		faults.put("synths/sedml/SimulatorSupportsDataSetsWithDifferentShapes/1.execution-should-succeed.omex",
				FAULT.ARRAY_INDEX_OUT_OF_BOUNDS);
		faults.put("synths/sedml/SimulatorSupportsModelAttributeChanges/1.execute-should-fail.omex",
				FAULT.SEDML_ERRONEOUS_UNIT_SYSTEM);
		faults.put("synths/sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/1.execute-should-fail.omex",
				FAULT.SEDML_ERRONEOUS_UNIT_SYSTEM);
		faults.put("synths/sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/3.execute-should-fail.omex",
				FAULT.SEDML_ERRONEOUS_UNIT_SYSTEM);
		return faults;
	}

	@Parameterized.Parameters
	public static Collection<String> testCases() {
		Set<String> modelsToFilter = new HashSet<>();
		Predicate<String> filter;

		modelsToFilter.addAll(needToCurateModels()); // Comment out if checking that current version will satisfy BSTS
		modelsToFilter.addAll(blacklistedModels());
		filter = (t) -> !modelsToFilter.contains(t);

		return Arrays.stream(BSTSBasedTestSuiteFiles.getBSTSTestCases()).filter(filter).collect(Collectors.toList());
	}

	@Test
	public void testBSTSBasedOmex() throws Exception {
		FAULT knownFault = knownFaults().get(this.testCaseFilename);
		try {
			System.out.println("running test " + this.testCaseFilename);
			final boolean[] bFailed = new boolean[1];
			final String[] errorMessage = new String[1];

			Path outdirPath = Files.createTempDirectory("BSTS_OmexExecTest");
			CLIRecordable cliRecorder = new CLIRecordable() {
				@Override
				public void writeDetailedErrorList(String message) {
					System.err.println("writeDetailedErrorList(): " + message);
					bFailed[0] = true;
					errorMessage[0] = message;
				}
				@Override
				public void writeFullSuccessList(String message) {
					System.out.println("writeFullSuccessList(): " + message);
				}
				@Override
				public void writeErrorList(String message) {
					System.err.println("writeErrorList(): " + message);
					bFailed[0] = true;
					errorMessage[0] = message;
				}
				@Override
				public void writeDetailedResultList(String message) {
					System.out.println("writeDetailedResultList(): " + message);
				}
				@Override
				public void writeSpatialList(String message) {
					System.out.println("writeSpatialList(): " + message);
				}
				@Override
				public void writeImportErrorList(String message) {
					System.err.println("writeImportErrorList(): " + message);
					bFailed[0] = true;
					errorMessage[0] = message;
				}
			};
			InputStream omexInputStream = BSTSBasedTestSuiteFiles.getBSTSTestCase(this.testCaseFilename);
			Path omexFile = Files.createTempFile("BSTS_OmexFile_", "omex");
			FileUtils.copyInputStreamToFile(omexInputStream, omexFile.toFile());
			ExecuteImpl.singleMode(omexFile.toFile(), outdirPath.toFile(), cliRecorder);
			String errMessage = (errorMessage[0] != null) ? errorMessage[0].replace("\n", " | ") : "";
			Assert.assertFalse("failure: '" + errMessage + "'", bFailed[0]);
			if (knownFault != null){
				throw new RuntimeException("test case passed, but expected " + knownFault.name() + ", remove "
						+ this.testCaseFilename + " from known faults");
			}

		} catch (Exception | AssertionError e){
			FAULT fault = this.determineFault(e);
			if (knownFault == fault) {
				System.err.println("Expected error: " + e.getMessage());
				return;
			} 
		
			System.err.println("add FAULT." + fault.name() + " to " + this.testCaseFilename);
			throw new Exception("Test error: " + this.testCaseFilename + " failed improperly", e);
		}
	}

	private FAULT determineFault(Throwable caughtException){ // Throwable because Assertion Error
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
		}

		return FAULT.UNCATETORIZED_FAULT;
	}

}
