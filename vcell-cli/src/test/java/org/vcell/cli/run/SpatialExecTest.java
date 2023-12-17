package org.vcell.cli.run;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.PropertyLoader;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.vcell.cli.CLIPythonManager;
import org.vcell.cli.CLIRecordable;
import org.vcell.cli.PythonStreamException;
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

import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("Spatial_IT")
public class SpatialExecTest {
    @BeforeAll
    public static void setup() throws PythonStreamException, IOException {
        PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
        NativeLib.HDF5.load();
        NativeLib.combinej.load();
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
        UNSUPPORTED_NONSPATIAL_STOCH_HISTOGRAM

    }

    static Set<String> blacklistedModels(){
        HashSet<String> blacklistSet = new HashSet<>();
        // Hooray! Nothing unsupported yet!
        return blacklistSet;
    }

    static Map<String, SpatialExecTest.FAULT> knownFaults() {
        HashMap<String, SpatialExecTest.FAULT> faults = new HashMap<>();
        // Hooray! no known faults yet!
        return faults;
    }

    public static Collection<String> testCases() {
        Set<String> modelsToFilter = new HashSet<>(blacklistedModels());
        Predicate<String> filter = (t) -> !modelsToFilter.contains(t);

        return Arrays.stream(SpatialArchiveFiles.getSpatialTestCases()).filter(filter).collect(Collectors.toList());
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void testSpatialOmex(String testCaseFilename) throws Exception {
        SpatialExecTest.FAULT knownFault = knownFaults().get(testCaseFilename);
        try {
            System.out.println("running test " + testCaseFilename);
            final boolean[] bFailed = new boolean[1];
            final String[] errorMessage = new String[1];

            Path outdirPath = Files.createTempDirectory("Spatial_OmexExecTest");
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
            InputStream omexInputStream = SpatialArchiveFiles.getSpatialTestCase(testCaseFilename);
            Path omexFile = Files.createTempFile("Spatial_OmexFile_", "omex");
            FileUtils.copyInputStreamToFile(omexInputStream, omexFile.toFile());
            ExecuteImpl.singleMode(omexFile.toFile(), outdirPath.toFile(), cliRecorder);
            String errMessage = (errorMessage[0] != null) ? errorMessage[0].replace("\n", " | ") : "";
            assertFalse(bFailed[0], "failure: '" + errMessage + "'");
            if (knownFault != null){
                throw new RuntimeException("test case passed, but expected " + knownFault.name() + ", remove "
                        + testCaseFilename + " from known faults");
            }

        } catch (Exception | AssertionError e){
            SpatialExecTest.FAULT fault = this.determineFault(e);
            if (knownFault == fault) {
                System.err.println("Expected error: " + e.getMessage());
                return;
            }

            System.err.println("add FAULT." + fault.name() + " to " + testCaseFilename);
            throw new Exception("Test error: " + testCaseFilename + " failed improperly", e);
        }
    }

    private SpatialExecTest.FAULT determineFault(Throwable caughtException){ // Throwable because Assertion Error
        String errorMessage = caughtException.getMessage();
        if (errorMessage == null) errorMessage = ""; // Prevent nullptr exception

        if (caughtException instanceof Error && caughtException.getCause() != null)
            errorMessage = caughtException.getCause().getMessage();

        if (errorMessage.contains("refers to either a non-existent model")) { //"refers to either a non-existent model (invalid SED-ML) or to another model with changes (not supported yet)"
            return SpatialExecTest.FAULT.SEDML_UNSUPPORTED_MODEL_REFERENCE;
        } else if (errorMessage.contains("System IO encountered a fatal error")){
            Throwable subException = caughtException.getCause();
            //String subMessage = (subException == null) ? "" : subException.getMessage();
            if (subException instanceof FileAlreadyExistsException){
                return SpatialExecTest.FAULT.HDF5_FILE_ALREADY_EXISTS;
            }
        } else if (errorMessage.contains("error while processing outputs: null")){
            Throwable subException = caughtException.getCause();
            if (subException instanceof ArrayIndexOutOfBoundsException){
                return SpatialExecTest.FAULT.ARRAY_INDEX_OUT_OF_BOUNDS;
            }
        } else if (errorMessage.contains("nconsistent unit system in SBML model") ||
                errorMessage.contains("ust be of type")){
            return SpatialExecTest.FAULT.SEDML_ERRONEOUS_UNIT_SYSTEM;
        }

        return SpatialExecTest.FAULT.UNCATETORIZED_FAULT;
    }
}
