package cbit.vcell.resource;

import org.junit.jupiter.api.AfterEach;
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
 */
@Tag("Fast")
public class SolverExecutableLookupTest {

	@TempDir
	File installDir;

	private String previousInstallDir;
	private File solversDir;
	private String suffix;

	@BeforeEach
	public void setUp() throws IOException {
		previousInstallDir = System.getProperty(PropertyLoader.installationRoot);
		PropertyLoader.setProperty(PropertyLoader.installationRoot, installDir.getAbsolutePath());
		solversDir = ResourceUtil.getLocalSolversDirectory();
		Files.createDirectories(solversDir.toPath());
		suffix = OperatingSystemInfo.getInstance().getExeBitSuffix();
	}

	@AfterEach
	public void tearDown() {
		if (previousInstallDir != null) {
			PropertyLoader.setProperty(PropertyLoader.installationRoot, previousInstallDir);
		} else {
			System.clearProperty(PropertyLoader.installationRoot);
		}
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

		assertEquals(hybrid, ResourceUtil.findSolverExecutable("Hybrid_EM"));
		assertEquals(hybrid.getParentFile().getCanonicalFile(),
				ResourceUtil.getSolverLibraryDirectory(hybrid.getAbsolutePath()));
	}

	@Test
	public void fallsBackToTheFlatPath() throws IOException {
		// an older flat install, or a server whose command path SlurmProxy strips to the bare name
		assertEquals(new File(solversDir, "FiniteVolume" + suffix), ResourceUtil.findSolverExecutable("FiniteVolume"));
	}

	@Test
	public void refusesAnExecutableInTwoSubdirectories() throws IOException {
		touch("vcell-fvsolver", "FiniteVolume" + suffix);
		touch("vcell-other", "FiniteVolume" + suffix);
		assertThrows(IOException.class, () -> ResourceUtil.findSolverExecutable("FiniteVolume"));
	}

	@Test
	public void anExecutableOutsideLocalSolversKeepsTheSolversDirectory() throws IOException {
		assertEquals(solversDir.getCanonicalFile(), ResourceUtil.getSolverLibraryDirectory("/usr/bin/docker"));
	}
}
