package org.vcell.cli.run;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.vcell.cli.messaging.CLIRecordable;
import org.vcell.sedml.testsupport.FailureType;
import org.vcell.sedml.testsupport.OmexTestCase;
import org.vcell.sedml.testsupport.OmexTestingDatabase;
import org.vcell.trace.TraceEvent;
import org.vcell.trace.Tracer;
import org.vcell.util.VCellUtilityHub;
import org.vcell.util.exe.Executable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("BSTS_IT")
public class QuantOmexExecTest {
	@BeforeAll
	public static void setup() {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
		VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);

		PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, new File("../vcell-cli-utils").getAbsolutePath());
		VCMongoMessage.enabled = false;
	}

	@AfterAll
	public static void teardown() throws Exception {
		VCellUtilityHub.shutdown();
	}

//	@SuppressWarnings("unused")
//	public enum FailureType {
//		ARRAY_INDEX_OUT_OF_BOUNDS,
//		BAD_EULER_FORWARD,
//		DIVIDE_BY_ZERO,
//		EXPRESSIONS_DIFFERENT,
//		EXPRESSION_BINDING,
//		GEOMETRY_SPEC_DIFFERENT,
//		HDF5_FILE_ALREADY_EXISTS, // reports.h5 file already exists, so action is blocked. Fixed in branch to be merged in.
//		MATHOVERRIDES_SurfToVol,
//		MATH_GENERATION_FAILURE,
//		MATH_OVERRIDES_A_FUNCTION,
//		MATH_OVERRIDES_INVALID,
//		NULL_POINTER_EXCEPTION,
//		OPERATION_NOT_SUPPORTED, // VCell simply doesn't have the necessary features to run this archive.
//		SBML_IMPORT_FAILURE,
//		SEDML_DIFF_NUMBER_OF_BIOMODELS,
//		SEDML_ERRONEOUS_UNIT_SYSTEM,
//		SEDML_ERROR_CONSTRUCTING_SIMCONTEXT,
//		SEDML_MATH_OVERRIDE_NAMES_DIFFERENT,
//		SEDML_MATH_OVERRIDE_NOT_EQUIVALENT,
//		SEDML_NONSPATIAL_STOCH_HISTOGRAM,
//		SEDML_NO_MODELS_IN_OMEX,
//		SEDML_SIMCONTEXT_NOT_FOUND_BY_NAME,
//		SEDML_SIMULATION_NOT_FOUND_BY_NAME,
//		SEDML_UNSUPPORTED_ENTITY,
//		SEDML_UNSUPPORTED_MODEL_REFERENCE, // Model refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)
//		TOO_SLOW,
//		UNCATETORIZED_FailureType,
//		UNITS_EXCEPTION,
//		UNKNOWN_IDENTIFIER,
//		UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM
//
//	}

	@SuppressWarnings("unused")
	static Map<String, FailureType> knownFailureTypes() {
		HashMap<String, FailureType> failureTypes = new HashMap<>();
		return failureTypes;
	}

	public static Collection<OmexTestCase> testCases() throws IOException {
		Predicate<OmexTestCase> projectFilter;
		projectFilter = (t) -> true; // don't skip any for now.
		return Arrays.stream(QuantOmexExecFiles.getTestCases()).filter(projectFilter).collect(Collectors.toList());
	}

    static class TestRecorder implements CLIRecordable {

		public TestRecorder() {
			Tracer.clearTraceEvents();
		}

        @Override
        public void writeDetailedErrorList(Exception e, String message) {
            System.err.println("writeDetailedErrorList(): " + message);
			Tracer.failure(e, "writeDetailedErrorList(): "+message);
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
			Tracer.log("writeDetailedResultList(): "+message);
        }
        @Override
        public void writeSpatialList(String message) {
            System.out.println("writeSpatialList(): " + message);
			Tracer.log("writeSpatialList(): "+message);
        }
        @Override
        public void writeImportErrorList(Exception e, String message) {
            System.err.println("writeImportErrorList(): " + message);
			Tracer.failure(e, "writeImportErrorList(): " + message);
        }
    }

	@ParameterizedTest
	@MethodSource("testCases")
	public void testBiosimulationsProject(OmexTestCase testCase) throws Exception {
		FailureType knownFailureType = testCase.known_failure_type;
		try {
			System.out.println("running testCase " + testCase.test_collection + " " + testCase.file_path);

			Path outdirPath = Files.createTempDirectory("BiosimulationsExecTest");
			InputStream omexInputStream = QuantOmexExecFiles.getOmex(testCase);
            Path omexFile = Files.createTempFile("BiosimulationsExec_", "omex");
			FileUtils.copyInputStreamToFile(omexInputStream, omexFile.toFile());

			TestRecorder cliRecorder = new TestRecorder();
			ExecuteImpl.singleMode(omexFile.toFile(), outdirPath.toFile(), cliRecorder);
			Path computedH5File = outdirPath.resolve("report.h5");

			String errorMessage = (Tracer.getInstance().hasErrors()) ? "failure: '" + Tracer.getInstance().getErrors().get(0).message.replace("\n", " | ") : "";
			if (Tracer.getInstance().hasErrors()) {
				throw new RuntimeException(errorMessage);
			}
			if (knownFailureType != null){
				throw new RuntimeException("test case passed, but expected " + knownFailureType.name() + ", remove "
						+ testCase.file_path + " from known FailureTypes");
			}

			// compare hdf5 files to within absolute tolerance of 1e-9
			double absTolerance = 1e-9;
			InputStream h5fileStream = QuantOmexExecFiles.getH5(testCase);
			Path expectedH5File = Files.createTempFile("BiosimulationsExec_", "h5");
			FileUtils.copyInputStreamToFile(h5fileStream, expectedH5File.toFile());
			Executable command = new Executable(new String[]{ "sh", "-c", "h5diff", "-r", "--delta", Double.toString(absTolerance),
					expectedH5File.toAbsolutePath().toString(),
					computedH5File.toAbsolutePath().toString()
			});
			command.start(new int[] { 0, 1 });
			String stdOutString = command.getStdoutString();
			if (stdOutString.contains("position")){
				throw new RuntimeException("H5 files have significant differences: " + stdOutString.substring(0, Math.min(300, stdOutString.length())));
			}
		} catch (Exception e){
			List<TraceEvent> errorEvents = Tracer.getInstance().getErrors();
			FailureType observedFailure = OmexTestingDatabase.determineFault(e, errorEvents);
			if (knownFailureType == observedFailure) {
				System.err.println("Expected error: " + e.getMessage());
				return;
			}

			System.err.println("add FailureType." + observedFailure.name() + " to " + testCase.file_path);
			throw new Exception("Test error: " + testCase.file_path + " failed improperly", e);
		}
	}
}
