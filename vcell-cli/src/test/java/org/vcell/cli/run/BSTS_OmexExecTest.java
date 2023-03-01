package org.vcell.cli.run;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.vcell.test.SEDML_SBML_IT;
import org.vcell.util.VCellUtilityHub;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;

@RunWith(Parameterized.class)
@Category({SEDML_SBML_IT.class})
public class BSTS_OmexExecTest {
	private final String testCaseFilename;

	public BSTS_OmexExecTest(String testCase){
		this.testCaseFilename = testCase;
	}

	@BeforeClass
	public static void setup() throws IOException {
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

	public enum FAULT {
		EXPRESSIONS_DIFFERENT,
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
		ARRAY_INDEX_OUT_OF_BOUNDS,
		UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM,
		SEDML_UNSUPPORTED_ENTITY,
		SEDML_DIFF_NUMBER_OF_BIOMODELS,
		SEDML_NO_MODELS_IN_OMEX,
		SEDML_ERROR_CONSTRUCTING_SIMCONTEXT,
		SEDML_NONSPATIAL_STOCH_HISTOGRAM,
		SEDML_MATH_OVERRIDE_NOT_EQUIVALENT,
		SEDML_MATH_OVERRIDE_NAMES_DIFFERENT,
		SEDML_SIMCONTEXT_NOT_FOUND_BY_NAME,
		SEDML_SIMULATION_NOT_FOUND_BY_NAME,

		SEDML_UNSUPPORTED_MODEL_REFERENCE, // Model refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)

		HDF5_FILE_ALREADY_EXISTS, // reports.h5 file already exists, so action is blocked. Fixed in branch to be merged in.
		OPERATION_NOT_SUPPORTED, // VCell simply doesn't have the necessary features to run this archive.

		UNCATETORIZED_FAULT
	};

	static Set<String> slowModels(){
		HashSet<String> slowSet = new HashSet<>();
		slowSet.add("synths/sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/3.execute-should-fail.omex");
		slowSet.add("synths/sedml/SimulatorSupportsAddReplaceRemoveModelElementChanges/1.execute-should-fail.omex");
		slowSet.add("synths/sedml/SimulatorSupportsModelAttributeChanges/1.execute-should-fail.omex");
		slowSet.add("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-discrete-NRM.omex");
		slowSet.add("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-discrete-SSA.omex");
		slowSet.add("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock.omex");
		slowSet.add("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-continuous.omex");
		slowSet.add("synths/sedml/SimulatorCanResolveModelSourcesDefinedByUriFragments/1.execution-should-succeed.omex");
		slowSet.add("synths/sedml/SimulatorCanResolveModelSourcesDefinedByUriFragmentsAndInheritChanges/1.execution-should-succeed.omex");
		return slowSet;
	}

	static Map<String, FAULT> knownFaults() {
		HashMap<String, FAULT> faults = new HashMap<>();
//		faults.put("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-discrete-NRM.omex", FAULT.SEDML_UNSUPPORTED_MODEL_REFERENCE); // Model refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)
//		faults.put("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-discrete-SSA.omex", FAULT.SEDML_UNSUPPORTED_MODEL_REFERENCE); // Model refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)
//		faults.put("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock.omex", FAULT.SEDML_UNSUPPORTED_MODEL_REFERENCE); // Model refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)
//		faults.put("sbml-core/Vilar-PNAS-2002-minimal-circardian-clock-continuous.omex", FAULT.SEDML_UNSUPPORTED_MODEL_REFERENCE); // Model refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)
//		faults.put("synths/sedml/SimulatorSupportsDataSetsWithDifferentShapes/1.execution-should-succeed.omex", FAULT.UNCATETORIZED_FAULT); // java.lang.ArrayIndexOutOfBoundsException at org.vcell.cli.run.hdf5.Hdf5DataPreparer.prepareNonspacialData(Hdf5DataPreparer.java:140)
//		faults.put("synths/sedml/SimulatorSupportsRepeatedTasksWithSubTasksOfMixedTypes/1.execution-should-succeed.omex", FAULT.UNCATETORIZED_FAULT); // ERROR (SEDMLImporter.java:589) - sequential RepeatedTask not yet supported, task __repeated_task_1 is being skipped []{}
//		faults.put("synths/sedml/SimulatorSupportsRepeatedTasksWithSubTasksOfMixedTypes/2.execution-should-succeed.omex", FAULT.UNCATETORIZED_FAULT); // ERROR (SEDMLImporter.java:589) - sequential RepeatedTask not yet supported, task __repeated_task_1 is being skipped []{}
		faults.put("synths/combine_archive/WhenACombineArchiveHasNoMasterFileSimulatorExecutesAllSedDocuments/1.execution-should-succeed.omex", FAULT.HDF5_FILE_ALREADY_EXISTS);
		faults.put("synths/combine_archive/CombineArchiveHasSedDocumentsWithSameNamesInDifferentInNestedDirectories/1.execution-should-succeed.omex", FAULT.HDF5_FILE_ALREADY_EXISTS);
		faults.put("synths/sedml/SimulatorSupportsDataGeneratorsWithDifferentShapes/1.execution-should-succeed.omex", FAULT.ARRAY_INDEX_OUT_OF_BOUNDS);
		faults.put("synths/sedml/SimulatorSupportsRepeatedTasksWithSubTasksOfMixedTypes/1.execution-should-succeed.omex", FAULT.OPERATION_NOT_SUPPORTED);		
		faults.put("synths/sedml/SimulatorSupportsRepeatedTasksWithSubTasksOfMixedTypes/2.execution-should-succeed.omex", FAULT.OPERATION_NOT_SUPPORTED);	
return faults;
	}

	@Parameterized.Parameters
	public static Collection<String> testCases() {
		Predicate<String> filter = (t) -> !slowModels().contains(t);
		List<String> testCases = Arrays.stream(BSTS_TestSuiteFiles.getBSTSTestCases()).filter(filter).collect(Collectors.toList());
		return testCases;
	}

	@Test
	public void test_sedml_roundtrip_SBML() throws Exception {
		FAULT knownFault = knownFaults().get(testCaseFilename);
		try {
			System.out.println("running test " + testCaseFilename);
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
			InputStream omexInputStream = BSTS_TestSuiteFiles.getBSTSTestCase(testCaseFilename);
			Path omexFile = Files.createTempFile("BSTS_OmexFile_", "omex");
			FileUtils.copyInputStreamToFile(omexInputStream, omexFile.toFile());
			ExecuteImpl.singleMode(omexFile.toFile(), outdirPath.toFile(), cliRecorder);
			String errMessage = (errorMessage[0] != null) ? errorMessage[0].replace("\n", " | ") : "";
			Assert.assertFalse("failure: '" + errMessage + "'", bFailed[0]);
			if (knownFault != null){
				throw new RuntimeException("test case passed, but expected "+knownFault.name()+", remove "+testCaseFilename+" from known faults");
			}
			Assert.assertNull("file "+testCaseFilename+" passed, but knownFault was set", knownFault);

		}catch (Exception | AssertionError e){
			String errMsg = e.getMessage();
			if (errMsg != null && errMsg.contains("refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)")){
				if (knownFault == FAULT.SEDML_UNSUPPORTED_MODEL_REFERENCE) {
					System.err.println("Expected error: "+e.getMessage());
					return;
				} else {
					System.err.println("add FAULT.SEDML_UNSUPPORTED_MODEL_REFERENCE to " + testCaseFilename);
				}
			}
			throw e;
		}
	}

	private FAULT determineFault(String errorMessage){
		FAULT determinedFault = FAULT.UNCATETORIZED_FAULT;
		if (errorMessage.contains("refers to either a non-existent model")) { //"refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)"
			determinedFault = FAULT.SEDML_UNSUPPORTED_MODEL_REFERENCE;
		}

		return determinedFault;
	}

}
