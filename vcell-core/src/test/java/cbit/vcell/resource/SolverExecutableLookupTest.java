package cbit.vcell.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vcell.util.OperatingSystemInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Each solver repo unpacks into its own subdirectory of localsolvers/&lt;os&gt;/, so one repo's bundled
 * libraries never replace another's. The lookup finds an executable in whichever subdirectory has it,
 * and a Linux run puts only that subdirectory on its library path.
 * <p>
 * The lookups are given a temporary solvers directory rather than finding it from the installation root: that is a
 * system property other tests set while this one runs (test classes run in parallel), so it could change between
 * setting up a test and checking it.
 */
@Tag("Fast")
public class SolverExecutableLookupTest {

	@TempDir
	File solversDir;

	private String suffix;

	@BeforeEach
	public void setUp() {
		suffix = OperatingSystemInfo.getInstance().getExeBitSuffix();
	}

	private File touch(String dir, String name) throws IOException {
		File d = new File(solversDir, dir);
		Files.createDirectories(d.toPath());
		File f = new File(d, name);
		Files.createFile(f.toPath());
		return f;
	}

	@Test
	public void findsTheExecutableInItsRepoSubdirectory() throws IOException {
		File hybrid = touch("vcell-hy3s", "Hybrid_EM" + suffix);
		touch("vcell-chombo", "VCellChombo2D" + suffix);

		assertEquals(hybrid, ResourceUtil.findSolverExecutable("Hybrid_EM", solversDir));
		assertEquals(hybrid.getParentFile().getCanonicalFile(),
				ResourceUtil.getSolverLibraryDirectory(hybrid.getAbsolutePath(), solversDir));
	}

	@Test
	public void fallsBackToTheFlatPath() throws IOException {
		// an older flat install, or a server whose command path SlurmProxy strips to the bare name
		assertEquals(new File(solversDir, "FiniteVolume" + suffix), ResourceUtil.findSolverExecutable("FiniteVolume", solversDir));
	}

	@Test
	public void refusesAnExecutableInTwoSubdirectories() throws IOException {
		touch("vcell-fvsolver", "FiniteVolume" + suffix);
		touch("vcell-other", "FiniteVolume" + suffix);
		assertThrows(IOException.class, () -> ResourceUtil.findSolverExecutable("FiniteVolume", solversDir));
	}

	@Test
	public void anExecutableOutsideLocalSolversKeepsTheSolversDirectory() throws IOException {
		assertEquals(solversDir.getCanonicalFile(), ResourceUtil.getSolverLibraryDirectory("/usr/bin/docker", solversDir));
	}
}
