package org.vcell.solver.fenics;

import cbit.vcell.resource.PropertyLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vcell.util.OperatingSystemInfo;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Tag("Fast")
public class FenicsDockerTest {

	@TempDir
	File dir;

	@AfterEach
	public void teardown() {
		System.clearProperty(PropertyLoader.fenicsImage);
	}

	@Test
	public void testImageProperty() {
		System.clearProperty(PropertyLoader.fenicsImage);
		assertEquals(FenicsDocker.DEFAULT_IMAGE, FenicsDocker.image());
		System.setProperty(PropertyLoader.fenicsImage, " ghcr.io/virtualcell/vcell-fenics:latest ");
		assertEquals("ghcr.io/virtualcell/vcell-fenics:latest", FenicsDocker.image());
	}

	@Test
	public void testRunPrefix() {
		FenicsDocker docker = new FenicsDocker(new File("/usr/local/bin/docker"));
		assertEquals(List.of("/usr/local/bin/docker", "run", "--rm", "--init", "--name", "c1",
						"--platform", "linux/amd64", "-v", "/data/sims:/data/sims", "--user", "501:20", "img:tag"),
				docker.runPrefix("img:tag", "linux/amd64", "c1", new File("/data/sims"), "/data/sims", "501:20"));
		assertEquals(List.of("/usr/local/bin/docker", "run", "--rm", "--init", "--name", "c2", "-v", "/d:/simdata", "img"),
				docker.runPrefix("img", null, "c2", new File("/d"), "/simdata", null));
	}

	@Test
	public void testEnvironmentPutsDockerDirectoryOnPath() {
		Map<String, String> env = new FenicsDocker(new File("/Applications/Docker.app/Contents/Resources/bin/docker")).environment();
		assertTrue(env.get("PATH").startsWith("/Applications/Docker.app/Contents/Resources/bin"));
	}

	@Test
	public void testContainerDirectory() {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		String containerDir = FenicsDocker.containerDirectory(dir, osi);
		if (osi.isWindows()) {
			assertEquals("/simdata", containerDir);
		} else {
			assertEquals(dir.getAbsolutePath(), containerDir);
		}
	}

	@Test
	public void testOwnerOf() {
		assumeTrue(!OperatingSystemInfo.getInstance().isWindows());
		assertTrue(FenicsDocker.ownerOf(dir).matches("\\d+:\\d+"), FenicsDocker.ownerOf(dir));
	}

	@Test
	public void testSolverRunsInDocker() throws Exception {
		FenicsSolver fenics = FenicsSolverTest.createSolver(dir, "SimID_274514696_0__0.simtask.xml", false);
		FenicsDocker docker = new FenicsDocker(new File("/usr/local/bin/docker"));
		fenics.configureDocker(docker, "img:tag", "linux/amd64");
		List<String> cmd = List.of(fenics.getMathExecutableCommand());
		int exe = cmd.indexOf(FenicsSolver.EXECUTABLE_NAME);
		assertTrue(exe > 0);
		assertEquals(List.of("/usr/local/bin/docker", "run", "--rm", "--init", "--name", fenics.getContainerName()), cmd.subList(0, 6));
		assertTrue(fenics.getContainerName().startsWith("vcell-fenics-SimID_274514696_0_-"));
		assertEquals("img:tag", cmd.get(exe - 1));
		String containerDir = FenicsDocker.containerDirectory(dir.getAbsoluteFile(), OperatingSystemInfo.getInstance());
		assertEquals(List.of("--simtask", containerDir + "/SimID_274514696_0__0.simtask.xml", "--out", containerDir, "--vc-print-status"),
				cmd.subList(exe + 1, cmd.size()));
	}
}
