package cbit.vcell.simdata;

import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Secondary (archive) storage is an older NFS server. On 2026-11-25/26 one of its nodes stopped
 * answering for 7-25 minutes at a time, and because every new SimulationData checked the secondary
 * user directory up front, every data request hung, including requests whose data was on primary.
 * These tests stand in for a hung mount with a directory whose stat calls throw.
 */
@Tag("Fast")
public class SimulationDataSecondaryStorageTest {

    /** A directory that fails the test if anything asks the file system about it. */
    private static final class UnreachableDir extends File {
        UnreachableDir(File dir) { super(dir.getPath()); }
        @Override public boolean exists() { throw new AssertionError("secondary storage was stat-ed: " + getPath()); }
        @Override public boolean isDirectory() { throw new AssertionError("secondary storage was stat-ed: " + getPath()); }
    }

    private static final User OWNER = new User("someuser", new KeyValue("17"));
    private static final KeyValue SIM_KEY = new KeyValue("123456789");

    private static VCSimulationDataIdentifier simDataId() {
        return new VCSimulationDataIdentifier(new VCSimulationIdentifier(SIM_KEY, OWNER), 0);
    }

    private static void writeLog(File dir) throws Exception {
        File log = new File(dir, SimulationData.createCanonicalSimLogFileName(SIM_KEY, 0, false));
        Files.writeString(log.toPath(), "IDAData logfile\n", StandardCharsets.US_ASCII);
    }

    @Test
    public void dataOnPrimary_doesNotTouchSecondary(@TempDir Path tmp) throws Exception {
        File primary = Files.createDirectory(tmp.resolve("primary")).toFile();
        writeLog(primary);
        File secondary = new UnreachableDir(tmp.resolve("secondary").toFile());

        SimulationData.AmplistorHelper helper = new SimulationData.AmplistorHelper(simDataId(), primary, secondary, null);

        assertEquals(primary, helper.getLogFile().getParentFile());
    }

    @Test
    public void dataOnlyOnSecondary_isFoundThere(@TempDir Path tmp) throws Exception {
        File primary = Files.createDirectory(tmp.resolve("primary")).toFile();
        File secondary = Files.createDirectory(tmp.resolve("secondary")).toFile();
        writeLog(secondary);

        SimulationData.AmplistorHelper helper = new SimulationData.AmplistorHelper(simDataId(), primary, secondary, null);

        assertEquals(secondary, helper.getLogFile().getParentFile());
    }

    @Test
    public void missingSecondaryDirectory_reportsNotFound(@TempDir Path tmp) throws Exception {
        File primary = Files.createDirectory(tmp.resolve("primary")).toFile();
        File secondary = tmp.resolve("no-such-user-dir").toFile();

        FileNotFoundException e = assertThrows(FileNotFoundException.class,
                () -> new SimulationData.AmplistorHelper(simDataId(), primary, secondary, null));
        assertTrue(e.getMessage().contains("not exist in primary"), e.getMessage());
    }
}
