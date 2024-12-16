package org.vcell.cli.run;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentest4j.AssertionFailedError;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIRecordable;
import org.vcell.cli.PythonStreamException;
import org.vcell.cli.testsupport.FailureType;
import org.vcell.cli.testsupport.OmexTestCase;
import org.vcell.cli.testsupport.OmexTestingDatabase;
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
	static List<OmexTestCase> omexTestCases;

	@BeforeAll
	public static void setup() throws PythonStreamException, IOException {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
		VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);

		PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, new File("../vcell-cli-utils").getAbsolutePath());
		VCMongoMessage.enabled = false;

		CLIPythonManager.getInstance().instantiatePythonProcess();
		omexTestCases = OmexTestingDatabase.loadOmexTestCases();
	}

	@AfterAll
	public static void teardown() throws Exception {
		CLIPythonManager.getInstance().closePythonProcess();
		VCellUtilityHub.shutdown();
	}

	public static Collection<OmexTestCase> testCases() throws IOException {
		Predicate<OmexTestCase> filter = (t) -> !OmexTestCase.Status.SKIP.equals(t.known_status);
		return Arrays.stream(BSTSBasedTestSuiteFiles.getBSTSTestCases()).filter(filter).collect(Collectors.toList());
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
	public void testBSTSBasedOmex(OmexTestCase testCase) throws Exception {
		FailureType knownFault = testCase.known_failure_type;
		try {
			System.out.println("running testCase " + testCase.test_collection + " " + testCase.file_path);

			Path outdirPath = Files.createTempDirectory("BSTS_OmexExecTest");
			InputStream omexInputStream = BSTSBasedTestSuiteFiles.getBSTSTestCase(testCase);
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
			FailureType fault = this.determineFault(e, errorEvents);
			if (knownFault != null) {
				if (knownFault == fault) {
					System.err.println("Found expected error " + knownFault.name() + ": " + e.getMessage());
					return;
				} else {
					fail("Expected error " + knownFault.name() + " but found error " + fault.name() + ": " + e.getMessage());
					return;
				}
			} else {
				fail("unexpected error, add FAULT." + fault.name() + " to " + testCase.test_collection + " in knownFaults()");
			}
		
			throw new Exception("Test error: " + testCase + " failed improperly", e);
		}
	}

	private FailureType determineFault(Throwable caughtException, List<TraceEvent> errorEvents){ // Throwable because Assertion Error
		String errorMessage = caughtException.getMessage();
		if (errorMessage == null) errorMessage = ""; // Prevent nullptr exception

		if (caughtException instanceof Error && caughtException.getCause() != null)
			errorMessage = caughtException.getCause().getMessage();

		if (errorMessage.contains("refers to either a non-existent model")) { //"refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)"
			return FailureType.SEDML_UNSUPPORTED_MODEL_REFERENCE;
		} else if (errorMessage.contains("System IO encountered a fatal error")){
			Throwable subException = caughtException.getCause();
			//String subMessage = (subException == null) ? "" : subException.getMessage();
			if (subException instanceof FileAlreadyExistsException){
				return FailureType.HDF5_FILE_ALREADY_EXISTS;
			}
		} else if (errorMessage.contains("error while processing outputs: null")){
			Throwable subException = caughtException.getCause();
			if (subException instanceof ArrayIndexOutOfBoundsException){
				return FailureType.ARRAY_INDEX_OUT_OF_BOUNDS;
			}
		} else if (errorMessage.contains("nconsistent unit system in SBML model") ||
				errorMessage.contains("ust be of type")){
			return FailureType.SEDML_ERRONEOUS_UNIT_SYSTEM;
		} else if (errorMessage.contains("There are no SED-MLs in the archive to execute")) {
			return FailureType.SEDML_NO_SEDMLS_TO_EXECUTE;
		} else if (errorMessage.contains("MappingException occurred: failed to generate math")) {
			return FailureType.MATH_GENERATION_FAILURE;
		}

		// else check Tracer error events for known faults
		for (TraceEvent event : errorEvents) {
			if (event.hasException(SBMLImportException.class)) {
				return FailureType.SBML_IMPORT_FAILURE;
			}
			if (event.span.getNestedContextName().contains(Span.ContextType.PROCESSING_SEDML.name()+"(preProcessDoc)")){
				return FailureType.SEDML_PREPROCESS_FAILURE;
			}
			if (event.hasException(MappingException.class)) {
				return FailureType.MATH_GENERATION_FAILURE;
			}
		}

		return FailureType.UNCATETORIZED_FAULT;
	}

}
