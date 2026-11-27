package org.vcell.solver.fenics;

import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solver.server.SolverStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Runs a real FEniCSx quick run: {@link FenicsSolver} in the local Docker image, as
 * {@code ClientSimManager.createQuickRunSolver} configures it. Needs a running Docker daemon with the
 * solver image already pulled ({@code docker pull} {@link FenicsDocker#image()}); skipped otherwise.
 * Not in the Fast group - run with {@code mvn test -pl vcell-core -Dgroups=FEniCSx_IT}.
 */
@Tag("FEniCSx_IT")
public class FenicsDockerRunTest {

	@TempDir
	File userDir;

	@Test
	public void testQuickRunInDocker() throws Exception {
		FenicsDocker docker;
		try {
			docker = FenicsDocker.find();
		} catch (Exception e) {
			assumeTrue(false, "docker not installed: " + e.getMessage());
			return;
		}
		assumeTrue(docker.isDaemonRunning(), "docker daemon not running");
		String image = FenicsDocker.image();
		Optional<String> platform = docker.localImagePlatform(image);
		assumeTrue(platform.isPresent(), image + " not pulled");

		// a small 3D reaction-diffusion model the solver handles (a vcell-fenics fixture)
		FenicsSolver fenics = FenicsSolverTest.createSolver(userDir, "SimID_1585623750_0__0.simtask.xml", false);
		fenics.configureDocker(docker, image, platform.get());

		List<Double> progress = new ArrayList<>();
		List<Double> dataTimes = new ArrayList<>();
		CountDownLatch done = new CountDownLatch(1);
		fenics.addSolverListener(new SolverListener() {
			public void solverStarting(SolverEvent event) { }
			public void solverProgress(SolverEvent event) { progress.add(event.getProgress()); }
			public void solverPrinted(SolverEvent event) { dataTimes.add(event.getTimePoint()); }
			public void solverFinished(SolverEvent event) { done.countDown(); }
			public void solverAborted(SolverEvent event) { done.countDown(); }
			public void solverStopped(SolverEvent event) { done.countDown(); }
		});
		fenics.startSolver();
		assertTrue(done.await(10, TimeUnit.MINUTES), "solver did not finish");

		SolverStatus status = fenics.getSolverStatus();
		assertEquals(SolverStatus.SOLVER_FINISHED, status.getStatus(),
				status.getSimulationMessage().getDisplayMessage() + "\n" + fenics.getMathExecutable().getStderrString());
		assertFalse(progress.isEmpty(), "no progress markers");
		assertEquals(1.0, progress.get(progress.size() - 1), 1e-9);
		assertFalse(dataTimes.isEmpty(), "no data markers");

		File bundle = fenics.getBundleDirectory();
		assertTrue(new File(bundle, ".zattrs").isFile(), "no results bundle at " + bundle);
		assertTrue(new File(bundle, "mesh").isDirectory());
	}
}
