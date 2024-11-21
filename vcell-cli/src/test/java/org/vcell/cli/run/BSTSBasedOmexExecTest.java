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


		// faults.put("misc-projects/BIOMD0000000005.omex", null); // works
		faults.put("misc-projects/BIOMD0000000302.omex", FAULT.MATH_GENERATION_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_8639905465728503850omex) | SIMULATIONS_RUN(runSimulations) | **** Error: Unable to initialize bioModel for the given selection: MappingException occurred: failed to generate math: generated an invalid mathDescription: Initial condition for variable 'h_post' references variable 'V_post'. Initial conditions cannot reference variables.
		faults.put("misc-projects/BIOMD0000000175.omex", FAULT.MATH_GENERATION_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_6652012719407098827omex) | SIMULATIONS_RUN(runSimulations) | **** Error: Unable to initialize bioModel for the given selection: MappingException occurred: failed to generate math: Unable to sort, unknown identifier I_Net_E44PPI3K_binding
		faults.put("misc-projects/BIOMD0000000618.omex", FAULT.SBML_IMPORT_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_13012177097014737572omex) | SIMULATIONS_RUN(runSimulations) | **** Error: Error processing model: model2 - couldn't find SBase with sid=null in SBMLSymbolMapping
		//faults.put("misc-projects/BIOMD0000000569.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000973.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000520.omex", null); // works!!
		faults.put("misc-projects/BIOMD0000000731.omex", FAULT.SBML_IMPORT_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_7811157837075929926omex) | SIMULATIONS_RUN(runSimulations) | **** Error: Unable to initialize bioModel for the given selection: Failed to translate SBML model into BioModel: Error binding global parameter 'Treg_origin_fraction_CD4' to model: 'func_TRegs_Production_from_CD4' is either not found in your model or is not allowed to be used in the current context. Check that you have provided the correct and full name (e.g. Ca_Cytosol).
		faults.put("misc-projects/BIOMD0000001061.omex", FAULT.SEDML_NO_SEDMLS_TO_EXECUTE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_6151950245083004772omex) | **** Error: writeErrorList(): BSTS_OmexFile_6151950245083004772omex | java.lang.RuntimeException: There are no SED-MLs in the archive to execute
		faults.put("misc-projects/BIOMD0000001064.omex", FAULT.SEDML_NO_SEDMLS_TO_EXECUTE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_855930052238018769omex) | **** Error: writeErrorList(): BSTS_OmexFile_855930052238018769omex | java.lang.RuntimeException: There are no SED-MLs in the archive to execute
		//faults.put("misc-projects/BIOMD0000000651.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000668.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000669.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000676.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000718.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000842.omex", null); // works!!
		//faults.put("misc-projects/BIOMD0000000843.omex", null); // works!!
		faults.put("misc-projects/BIOMD0000000932.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_299806559413585814omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: SED-ML processing for /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/vcell_temp_05f4edae-1193-4e1d-80a9-bf99016edf3d13374866975883240909/Garde2020-Fig2.sedml failed with error: | java.lang.RuntimeException: Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest9311079708082702671/temp/simulation_Garde2020-Fig2.sedml
		faults.put("misc-projects/BIOMD0000000944.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_10652422074989094828omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: SED-ML processing for /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/vcell_temp_c678fc1e-c809-4281-a929-b697475fad5d11034735503698684353/Goldbeter2013.sedml failed with error: | java.lang.RuntimeException: Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest4404087004775987568/temp/simulation_Goldbeter2013.sedml
		faults.put("misc-projects/BIOMD0000000951.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_18094892223264859678omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: SED-ML processing for /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/vcell_temp_9fe6c239-0047-464f-ba8d-033f9950ef5616996166662536534590/Mitrophanov2015.sedml failed with error: | java.lang.RuntimeException: Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest17248149207139727689/temp/simulation_Mitrophanov2015.sedml
		faults.put("misc-projects/BIOMD0000000957.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_15398452662216167908omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: SED-ML processing for /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/vcell_temp_d40b4a5d-9a59-43f3-87fa-6836a78ea0268242714171608426821/Roda2020.sedml failed with error: | java.lang.RuntimeException: Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest15618790497998059623/temp/simulation_Roda2020.sedml
		faults.put("misc-projects/BIOMD0000000968.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_1952836001547640998omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: SED-ML processing for /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/vcell_temp_d2ee93af-bb6c-48ca-97f2-98a106e0b7e26203677341757147488/Palmer2008.sedml failed with error: | java.lang.RuntimeException: Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest14998574499587823528/temp/simulation_Palmer2008.sedml
		faults.put("misc-projects/BIOMD0000000972.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_16129466033006731360omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: SED-ML processing for /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/vcell_temp_65e8c60c-c8bd-41fe-973e-49b215ee11b71447845804155630300/Tang2020.sedml failed with error: | java.lang.RuntimeException: Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest17277361648850041020/temp/simulation_Tang2020.sedml
		faults.put("misc-projects/BIOMD0000000983.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_12538305798901592382omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: SED-ML processing for /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/vcell_temp_0591fd03-88b3-4ad7-a988-c2478671a68812551187952687659692/Zongo2020.sedml failed with error: | java.lang.RuntimeException: Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest12116210176998446933/temp/simulation_Zongo2020.sedml
		faults.put("misc-projects/BIOMD0000000985.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_11036490625279901942omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: SED-ML processing for /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/vcell_temp_321f50e0-1f6e-48fc-a4f8-7233662b5f8b12358921756946262823/Gex-Fabry1984.sedml failed with error: | java.lang.RuntimeException: Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest914620207324900201/temp/simulation_Gex-Fabry1984.sedml
		faults.put("misc-projects/BIOMD0000000989.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_17814250350066747412omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: SED-ML processing for /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/vcell_temp_20fc8778-795e-4ca2-87e5-954ebc6fde8617940965203697577620/MODEL1712050001.sedml failed with error: | java.lang.RuntimeException: Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest8507375305898450638/temp/simulation_MODEL1712050001.sedml
		faults.put("misc-projects/BIOMD0000000991.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_2522047989779973244omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: SED-ML processing for /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/vcell_temp_156e5fee-cd1a-4c28-af49-e6c00cce446e10964600217313052640/Okuonghae2020.sedml failed with error: | java.lang.RuntimeException: Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest10045148909541837809/temp/simulation_Okuonghae2020.sedml
		faults.put("misc-projects/BIOMD0000000997.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_17043350583827454685omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: BSTS_OmexFile_17043350583827454685omex,MODEL1712050006.sedml,Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest9820394719800166079/temp/simulation_MODEL1712050006.sedml
		faults.put("misc-projects/BIOMD0000001010.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_901594480328185461omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: BSTS_OmexFile_901594480328185461omex,Zhang2007_M3_low_DD.sedml,Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest1961470606014028473/temp/simulation_Zhang2007_M3_low_DD.sedml
		faults.put("misc-projects/BIOMD0000001014.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_1371059216516510527omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: BSTS_OmexFile_1371059216516510527omex,Leon-Triana2021 (eqs 3-6).sedml,Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest145372853658544068/temp/simulation_Leon-Triana2021 (eqs 3-6).sedml
		faults.put("misc-projects/BIOMD0000001018.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_17092676956014497901omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: BSTS_OmexFile_17092676956014497901omex,Bakshi2020 properdin model.sedml,Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest9940912637860611784/temp/simulation_Bakshi2020 properdin model.sedml
		faults.put("misc-projects/BIOMD0000001072.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_4412450965004248264omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: BSTS_OmexFile_4412450965004248264omex,Phillips2013.sedml,Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest16782612435251983142/temp/simulation_Phillips2013.sedml
		faults.put("misc-projects/BIOMD0000001077.omex", FAULT.SEDML_PREPROCESS_FAILURE); // | Root(root) | OMEX_EXECUTE(BSTS_OmexFile_11225596004044316029omex) | PROCESSING_SEDML(preProcessDoc) | **** Error: BSTS_OmexFile_11225596004044316029omex,Adlung2021 _model_jakstat_pa.sedml,Failed to create plot file /var/folders/zz/gcfcvgtd5v1cdgj2sw4bjzdr0000gr/T/BSTS_OmexExecTest7806933134886966313/temp/simulation_Adlung2021 _model_jakstat_pa.sedml

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
			String errorMessages = (errorEvents.isEmpty()) ? "" : errorEvents.get(0).message;
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
		} else if (errorMessage.contains("inconsistent unit system in SBML model") ||
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
