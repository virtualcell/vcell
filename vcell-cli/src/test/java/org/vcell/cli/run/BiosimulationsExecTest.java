package org.vcell.cli.run;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Tag;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIRecordable;
import org.vcell.cli.PythonStreamException;
import org.vcell.test.BSTS_IT;
import org.vcell.util.VCellUtilityHub;
import org.vcell.util.exe.Executable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RunWith(Parameterized.class)
@Category(BSTS_IT.class)
@Tag("BSTS_IT")
public class BiosimulationsExecTest {
	private final String testCaseProjectID;

	public BiosimulationsExecTest(String testCaseProjectID){
		this.testCaseProjectID = testCaseProjectID;
	}

	@BeforeClass
	public static void setup() throws PythonStreamException, IOException {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
		NativeLib.HDF5.load();
		NativeLib.combinej.load();
		VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);

		PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, new File("../vcell-cli-utils").getAbsolutePath());
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
	static Map<String, FAULT> knownFaults() {
		HashMap<String, FAULT> faults = new HashMap<>();
		return faults;
	}

	@Parameterized.Parameters
	public static Collection<String> testCases() {
		Predicate<String> projectFilter;
		projectFilter = (t) -> true; // don't skip any for now.
		return Arrays.stream(BiosimulationsFiles.getProjectIDs()).filter(projectFilter).collect(Collectors.toList());
	}

    static class TestRecorder implements CLIRecordable {
        public boolean bFailed = false;
        public String errorMessage = "";

        @Override
        public void writeDetailedErrorList(String message) {
            System.err.println("writeDetailedErrorList(): " + message);
            bFailed = true;
            errorMessage = message;
        }
        @Override
        public void writeFullSuccessList(String message) {
            System.out.println("writeFullSuccessList(): " + message);
        }
        @Override
        public void writeErrorList(String message) {
            System.err.println("writeErrorList(): " + message);
            bFailed = true;
            errorMessage = message;
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
            bFailed = true;
            errorMessage = message;
        }
    }

	@Test
	public void testBiosimulationsProject() throws Exception {
		FAULT knownFault = knownFaults().get(this.testCaseProjectID);
		try {
			System.out.println("running test " + this.testCaseProjectID);

			Path outdirPath = Files.createTempDirectory("BiosimulationsExecTest");
			InputStream omexInputStream = BiosimulationsFiles.getOmex(testCaseProjectID);
            Path omexFile = Files.createTempFile("BiosimulationsExec_", "omex");
			FileUtils.copyInputStreamToFile(omexInputStream, omexFile.toFile());

			TestRecorder cliRecorder = new TestRecorder();
			ExecuteImpl.singleMode(omexFile.toFile(), outdirPath.toFile(), cliRecorder);
			Path computedH5File = outdirPath.resolve("report.h5");

			String errMessage = cliRecorder.errorMessage.replace("\n", " | ");
			Assert.assertFalse("failure: '" + errMessage + "'", cliRecorder.bFailed);
			if (knownFault != null){
				throw new RuntimeException("test case passed, but expected " + knownFault.name() + ", remove "
						+ this.testCaseProjectID + " from known faults");
			}

//			// verify log file has status of 'SUCCEEDED'
//			Gson gson = new Gson();
//			try (Reader jsonReader = new )
//			}
//			gson.newJsonReader(new )

			// compare hdf5 files to within absolute tolerance of 1e-9
			double absTolerance = 1e-9;
			InputStream h5fileStream = BiosimulationsFiles.getH5(testCaseProjectID);
			Path expectedH5File = Files.createTempFile("BiosimulationsExec_", "h5");
			FileUtils.copyInputStreamToFile(h5fileStream, expectedH5File.toFile());
			Executable command = new Executable(new String[]{ "sh", "-c", "h5diff", "-r", "--delta", Double.toString(absTolerance),
					expectedH5File.toAbsolutePath().toString(),
					computedH5File.toAbsolutePath().toString()
			});
			command.start(new int[] { 0, 1 });
			Assert.assertFalse("H5 files have significant differences: "+
					command.getStdoutString().substring(0,300),
					command.getStdoutString().contains("position"));

		} catch (Exception | AssertionError e){
			FAULT fault = this.determineFault(e);
			if (knownFault == fault) {
				System.err.println("Expected error: " + e.getMessage());
				return;
			}

			System.err.println("add FAULT." + fault.name() + " to " + this.testCaseProjectID);
			throw new Exception("Test error: " + this.testCaseProjectID + " failed improperly", e);
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
