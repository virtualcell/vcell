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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

@Tag("BSTS_IT")
public class BSTSBasedOmexExecTest {
	static List<OmexTestCase> omexTestCases;

	@BeforeAll
	public static void setup() throws IOException {
		PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
		VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);

		PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, new File("../vcell-cli-utils").getAbsolutePath());
		VCMongoMessage.enabled = false;

		omexTestCases = OmexTestingDatabase.loadOmexTestCases();
	}

	@AfterAll
	public static void teardown() throws Exception {
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
			if (!errorEvents.isEmpty()){
				throw new RuntimeException("failure: '" + errorMessages + "'");
			}
			if (knownFault != null) {
				throw new RuntimeException("Expected error " + knownFault.name() + " but found no error");
			}
		} catch (Exception e){
			System.err.println("========== Begin Tracer report ==========");
			Tracer.reportErrors(true);
			System.err.println("=========== End Tracer report ===========");
			e.printStackTrace(System.err);
			List<org.vcell.trace.TraceEvent> errorEvents = Tracer.getErrors();
			FailureType fault = OmexTestingDatabase.determineFault(e, errorEvents);
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
}
