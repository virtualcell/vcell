package org.vcell.solver.fenics;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solvers.ApplicationMessage;
import cbit.vcell.xml.XmlHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("Fast")
public class FenicsSolverTest {

	private static final String SIMTASK_RESOURCE = "SimID_274514696_0__0.simtask.xml";

	private final Map<String, String> savedProperties = new HashMap<>();

	@TempDir
	File userDir;

	private void setProperty(String key, String value) {
		savedProperties.putIfAbsent(key, System.getProperty(key));
		System.setProperty(key, value);
	}

	@BeforeEach
	public void setup() {
		setProperty(PropertyLoader.jmsSimHostExternal, "broker.example.org");
		setProperty(PropertyLoader.jmsSimRestPortExternal, "30163");
	}

	@AfterEach
	public void teardown() {
		savedProperties.forEach((key, value) -> {
			if (value == null) System.clearProperty(key); else System.setProperty(key, value);
		});
		savedProperties.clear();
	}

	/** the finite-volume fixture, switched to FEniCSx */
	private static SimulationTask fenicsSimTask() throws Exception {
		return fenicsSimTask(SIMTASK_RESOURCE);
	}

	/** a simtask fixture (a finite-volume sim), switched to FEniCSx */
	static SimulationTask fenicsSimTask(String resource) throws Exception {
		try (InputStream in = FenicsSolverTest.class.getResourceAsStream(resource)) {
			assertNotNull(in, resource);
			SimulationTask simTask = XmlHelper.XMLToSimTask(new String(in.readAllBytes(), StandardCharsets.UTF_8));
			simTask.getSimulation().getSolverTaskDescription().setSolverDescription(SolverDescription.FEniCSx);
			return simTask;
		}
	}

	static FenicsSolver createSolver(File userDir, String resource, boolean messaging) throws Exception {
		return (FenicsSolver) SolverFactory.createSolver(userDir, fenicsSimTask(resource), messaging);
	}

	@Test
	public void testSolverDescription() {
		SolverDescription sd = SolverDescription.FEniCSx;
		assertSame(sd, SolverDescription.fromDatabaseName("FEniCSx"));
		assertTrue(sd.isFenicsSolver());
		assertTrue(sd.isSpatial());
		assertNull(sd.getSolverExecutable(), "runs in a container, no native executable");
		assertFalse(sd.supports(SolverDescription.SolverFeature.Feature_Moving));
		assertFalse(sd.supports(SolverDescription.SolverFeature.Feature_FastSystem));
		assertFalse(sd.supports(SolverDescription.SolverFeature.Feature_PeriodicBoundaryCondition));
	}

	@Test
	public void testOfferedOnlyWhenEnabled() {
		setProperty(PropertyLoader.fenicsEnabled, "false");
		assertFalse(SolverDescription.FEniCSx.isOffered());
		assertTrue(SolverDescription.SundialsPDE.isOffered());
		setProperty(PropertyLoader.fenicsEnabled, "true");
		assertTrue(SolverDescription.FEniCSx.isOffered());
		assertFalse(SolverDescription.Comsol.isOffered(), "deprecated solvers stay hidden");
	}

	@Test
	public void testSimTaskXmlRoundTrip() throws Exception {
		SimulationTask simTask = fenicsSimTask();
		SimulationTask reread = XmlHelper.XMLToSimTask(XmlHelper.simTaskToXML(simTask));
		assertSame(SolverDescription.FEniCSx, reread.getSimulation().getSolverTaskDescription().getSolverDescription());
	}

	@Test
	public void testFactoryAndLocalCommand() throws Exception {
		Solver solver = SolverFactory.createSolver(userDir, fenicsSimTask(), false);
		assertInstanceOf(FenicsSolver.class, solver);
		FenicsSolver fenics = (FenicsSolver) solver;

		File simTaskFile = new File(userDir, "SimID_274514696_0__0.simtask.xml");
		assertTrue(simTaskFile.exists(), "SolverFactory writes the task file the solver reads");
		assertEquals(simTaskFile.getAbsoluteFile(), fenics.getSimTaskFile().getAbsoluteFile());
		assertEquals(new File(userDir, "SimID_274514696_0_.fenics").getAbsoluteFile(), fenics.getBundleDirectory().getAbsoluteFile());

		assertEquals(List.of("vcell-fenics", "--simtask", simTaskFile.getAbsolutePath(),
				"--out", userDir.getAbsolutePath(), "--vc-print-status"),
				Arrays.asList(fenics.getMathExecutableCommand()));

		fenics.setCommandPrefix(List.of("docker", "run", "--rm", "img"));
		assertEquals(List.of("docker", "run", "--rm", "img", "vcell-fenics"),
				Arrays.asList(fenics.getMathExecutableCommand()).subList(0, 5));
	}

	@Test
	public void testMessagingCommandAndConfig() throws Exception {
		FenicsSolver fenics = (FenicsSolver) SolverFactory.createSolver(userDir, fenicsSimTask(), true);
		String[] cmd = fenics.getMathExecutableCommand();
		assertEquals("--vc-send-status-config=" + fenics.getMessagingConfigFilename(), cmd[cmd.length - 1]);

		fenics.initialize();
		List<String> config = Files.readAllLines(new File(fenics.getMessagingConfigFilename()).toPath());
		assertTrue(fenics.getMessagingConfigFilename().endsWith("SimID_274514696_0_.fenicsMessagingConfig"));
		assertEquals(List.of(
				"broker_host=broker.example.org",
				"broker_port=30163",
				"broker_username=admin",
				"broker_password=admin",
				"vc_username=schaff",
				"simKey=274514696",
				"taskID=0",
				"jobIndex=0"), config);
	}

	@Test
	public void testInitializeWritesMissingSimTask() throws Exception {
		FenicsSolver fenics = (FenicsSolver) SolverFactory.createSolver(userDir, fenicsSimTask(), false);
		assertTrue(fenics.getSimTaskFile().delete());
		fenics.initialize();
		assertTrue(fenics.getSimTaskFile().exists());
		assertNotNull(fenics.getMathExecutable());
	}

	@Test
	public void testStatusMarkers() throws Exception {
		FenicsSolver fenics = (FenicsSolver) SolverFactory.createSolver(userDir, fenicsSimTask(), false);
		ApplicationMessage progress = fenics.getApplicationMessage("progress:42.5%");
		assertEquals(ApplicationMessage.PROGRESS_MESSAGE, progress.getMessageType());
		assertEquals(0.425, progress.getProgress(), 1e-12);

		ApplicationMessage data = fenics.getApplicationMessage("data:1.25");
		assertEquals(ApplicationMessage.DATA_MESSAGE, data.getMessageType());
		assertEquals(1.25, data.getTimepoint(), 0.0);
		assertEquals(1.25, fenics.getCurrentTime(), 0.0);

		assertThrows(RuntimeException.class, () -> fenics.getApplicationMessage("hello"));
	}
}
