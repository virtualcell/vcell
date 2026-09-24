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
		assertTrue(sd.supports(SolverDescription.SolverFeature.Feature_Moving), "2D and 3D moving boundaries, species inside the front");
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
	public void testUnsupportedGeometryIsAnIssueBeforeTheRun() throws Exception {
		// an analytic 2D geometry is fine
		SimulationTask analytic = fenicsSimTask();
		assertEquals(List.of(), FenicsSolver.unsupportedReasons(analytic.getSimulation()));
		assertTrue(fenicsIssues(analytic).isEmpty());

		// a 3D image geometry (ec / cytosol / Nucleus) is realized too — body-fitted from its smoothed label
		// field (vcell-fenics ADR 012) — so it raises no issue (the ERROR-issue path that stops a quick run
		// is covered by the moving-boundary refusals below)
		SimulationTask image = fenicsSimTask("image3d_SimID_274630052_0__0.simtask.xml");
		assertEquals(List.of(), FenicsSolver.unsupportedReasons(image.getSimulation()));
		assertTrue(fenicsIssues(image).isEmpty());
	}

	private static final String MOVING = "moving_SimID_274641196_0__0.simtask.xml";

	@Test
	public void testMovingBoundaryIsOffered() throws Exception {
		// VCell's moving-boundary fixture: a 2D disk swept by a front at (sin t, cos t), species inside it only
		SimulationTask moving = fenicsSimTask(MOVING);
		assertTrue(moving.getSimulation().getMathDescription().isMovingMembrane());
		assertEquals(List.of(), FenicsSolver.unsupportedReasons(moving.getSimulation()));
	}

	@Test
	public void testMovingBoundarySpeciesOutsideTheFrontAreAnIssue() throws Exception {
		// the same model with a species in the exterior compartment, which the moving path does not solve yet
		String xml = resourceText(MOVING)
				.replace("<VolumeVariable Name=\"C_cyt\" Domain=\"cell\" />",
						"<VolumeVariable Name=\"C_cyt\" Domain=\"cell\" /><VolumeVariable Name=\"E_ec\" Domain=\"ec\" />")
				.replace("    </CompartmentSubDomain>\n    <MembraneSubDomain",
						"      <PdeEquation Name=\"E_ec\" SolutionType=\"Unknown\"><Rate>0.0</Rate><Diffusion>1.0</Diffusion><Initial>1.0</Initial></PdeEquation>\n"
								+ "    </CompartmentSubDomain>\n    <MembraneSubDomain")
				.replace("<JumpCondition Name=\"C_cyt\">",
						"<JumpCondition Name=\"E_ec\"><InFlux>0.0</InFlux><OutFlux>0.0</OutFlux></JumpCondition><JumpCondition Name=\"C_cyt\">");
		SimulationTask simTask = XmlHelper.XMLToSimTask(xml);
		simTask.getSimulation().getSolverTaskDescription().setSolverDescription(SolverDescription.FEniCSx);
		List<String> reasons = FenicsSolver.unsupportedReasons(simTask.getSimulation());
		assertEquals(1, reasons.size(), reasons.toString());
		assertTrue(reasons.get(0).contains("inside the moving front ('cell') only") && reasons.get(0).contains("'ec'"), reasons.get(0));
		assertEquals(1, fenicsIssues(simTask).size());
	}

	private static final String MOVING_3D = "moving3d_furrow_SimID_516481304_0__0.simtask.xml";

	@Test
	public void testA3DMovingBoundaryIsOfferedToFenicsOnly() throws Exception {
		// the cleavage furrow in 3D: a sphere pinched by an axisymmetric ring, its front velocity with a Z component
		SimulationTask moving = fenicsSimTask(MOVING_3D);
		cbit.vcell.math.MathDescription math = moving.getSimulation().getMathDescription();
		assertTrue(math.isMovingMembrane());
		cbit.vcell.math.MembraneSubDomain front = (cbit.vcell.math.MembraneSubDomain) java.util.Collections.list(math.getSubDomains())
				.stream().filter(s -> s instanceof cbit.vcell.math.MembraneSubDomain).findFirst().orElseThrow();
		assertNotNull(front.getVelocityZ(), "the <Velocity><Z> is read");
		assertEquals(List.of(), FenicsSolver.unsupportedReasons(moving.getSimulation()));
		assertTrue(fenicsIssues(moving).isEmpty());

		// the Z component survives the SimulationTask XML round trip
		String xml = XmlHelper.simTaskToXML(moving);
		assertTrue(xml.contains("<Z>sobj_Cyt1_EC0_velZ</Z>"), "the writer emits <Velocity><Z>");
		cbit.vcell.math.MembraneSubDomain reread = (cbit.vcell.math.MembraneSubDomain) java.util.Collections
				.list(XmlHelper.XMLToSimTask(xml).getSimulation().getMathDescription().getSubDomains()).stream()
				.filter(s -> s instanceof cbit.vcell.math.MembraneSubDomain).findFirst().orElseThrow();
		assertEquals(front.getVelocityZ().infix(), reread.getVelocityZ().infix());

		// the native Moving Boundary solver writes a 2D (x/y) input only: 3D is refused for it
		moving.getSimulation().getSolverTaskDescription().setSolverDescription(SolverDescription.MovingBoundary);
		List<org.vcell.util.Issue> issues = new java.util.ArrayList<>();
		moving.getSimulation().gatherIssues(new org.vcell.util.IssueContext(), issues);
		assertTrue(issues.stream().anyMatch(i -> i.getCategory() == org.vcell.util.Issue.IssueCategory.MovingBoundary_Dimension_NotSupported
				&& i.getSeverity() == org.vcell.util.Issue.Severity.ERROR), issues.toString());
	}

	private static String resourceText(String resource) throws Exception {
		try (InputStream in = FenicsSolverTest.class.getResourceAsStream(resource)) {
			assertNotNull(in, resource);
			return new String(in.readAllBytes(), StandardCharsets.UTF_8).replace("\r\n", "\n");
		}
	}

	private static List<org.vcell.util.Issue> fenicsIssues(SimulationTask simTask) {
		List<org.vcell.util.Issue> issues = new java.util.ArrayList<>();
		simTask.getSimulation().gatherIssues(new org.vcell.util.IssueContext(), issues);
		issues.removeIf(i -> i.getCategory() != org.vcell.util.Issue.IssueCategory.FEniCSx_Geometry_NotSupported);
		return issues;
	}

	@Test
	public void testFailureShowsTheSolversErrorLine() {
		String stderr = "[vcell-fenics] loaded simtask model: {...}\n"
				+ "[vcell-fenics] realizing interface-coupled geometry 'g' at h = 1.04\n"
				+ "error: RealizationError: 3D geometry 'g' subvolume 'ec' must be 'analytic' with an expression to be realized (got type 'image')\n\n\n"
				+ "(/usr/local/bin/docker run --rm ... vcell-fenics --simtask ...)";
		assertEquals("FEniCSx solver error: RealizationError: 3D geometry 'g' subvolume 'ec' must be 'analytic' with an expression to be realized (got type 'image')",
				FenicsSolver.errorLineOf("Could not execute code: " + stderr));
		assertEquals("something else", FenicsSolver.errorLineOf("something else"), "no error line: unchanged");
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
