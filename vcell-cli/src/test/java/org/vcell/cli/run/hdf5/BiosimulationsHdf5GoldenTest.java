package org.vcell.cli.run.hdf5;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.vcell.cli.messaging.CLIRecorder;
import org.vcell.cli.run.ExecuteImpl;
import org.vcell.cli.run.ExecutionJob;
import org.vcell.cli.run.UnifiedReportsComparison;
import org.vcell.util.VCellUtilityHub;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Golden comparison: executes OMEX archives through the CLI with {@link ExecutionJob#UNIFIED_REPORTS_PROPERTY} set, so
 * each run writes its reports twice, once through the legacy code ({@link BiosimulationsHdf5Writer} and the report
 * CSVs) and once through the new data system, and requires both to be the same (see {@link UnifiedReportsComparison}).
 * <p>
 * The archives cover parameter scans, several SED-ML documents in one archive, and 2D plots, in both BioSimulators mode
 * (sub-task dimensions, plots reported) and plain mode. BSTSBasedOmexExecTest makes the same comparison over the whole
 * BioSimulators test suite.
 */
@Tag("Fast")
public class BiosimulationsHdf5GoldenTest {

    private static final String SYNTHS = "/bsts-omex/synths/sedml/";

    // one directory for the whole class: the CLI's log manager keeps files in it until the hub shuts down
    @TempDir
    static Path temp;

    static Stream<Arguments> archives() {
        List<String> archives = List.of(
                "/BioModel1.omex",                              // a scanned time course, reported and plotted
                "/bsts-omex/misc-projects/BIOMD0000000932.omex", // two SED-ML documents, a scan, several plots
                "/bsts-omex/synths/sedml/SimulatorSupportsRepeatedTasksWithNestedRepeatedTasks/1.execution-should-succeed.omex");
        List<Arguments> cases = new ArrayList<>();
        for (String archive : archives)
            for (boolean bioSimMode : new boolean[]{true, false}) cases.add(Arguments.of(archive, bioSimMode));
        return cases.stream();
    }

    @BeforeAll
    public static void setup() {
        PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());
        VCellUtilityHub.startup(VCellUtilityHub.MODE.CLI);
        PropertyLoader.setProperty(PropertyLoader.cliWorkingDir, new File("../vcell-cli-utils").getAbsolutePath());
        VCMongoMessage.enabled = false;
        System.setProperty(ExecutionJob.UNIFIED_REPORTS_PROPERTY, "true");
    }

    @AfterAll
    public static void teardown() {
        System.clearProperty(ExecutionJob.UNIFIED_REPORTS_PROPERTY);
        VCellUtilityHub.shutdown();
    }

    @ParameterizedTest(name = "{0} bioSimMode={1}")
    @MethodSource("archives")
    public void newWriterMatchesLegacyWriter(String archive, boolean bioSimMode) throws Exception {
        Path run = Files.createTempDirectory(temp, "run");
        Path omex = run.resolve("archive.omex");
        try (InputStream in = BiosimulationsHdf5GoldenTest.class.getResourceAsStream(archive)) {
            assertNotNull(in, "missing test resource " + archive);
            Files.copy(in, omex);
        }
        Path output = Files.createDirectory(run.resolve("out"));
        ExecuteImpl.singleMode(omex.toFile(), output.toFile(), new CLIRecorder(output.toFile(), false, false), bioSimMode);

        Path legacy = output.resolve("reports.h5");
        assertTrue(Files.exists(legacy), "the legacy writer produced no reports for " + archive);
        assertFalse(Hdf5Comparison.datasetShapes(legacy).isEmpty(), "no reports to compare in " + archive);
        List<String> differences = UnifiedReportsComparison.differences(output);
        assertTrue(differences.isEmpty(), String.join("\n", differences));
    }
}
