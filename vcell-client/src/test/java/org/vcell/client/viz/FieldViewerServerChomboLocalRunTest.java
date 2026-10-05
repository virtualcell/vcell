package org.vcell.client.viz;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cbit.vcell.client.ClientSimManager;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.LocalDataSetController;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * The field viewer over two real Chombo results, each with a <b>membrane</b>, read the way the desktop reads a quick
 * run: a {@link DataSetControllerImpl} behind a {@link LocalDataSetController}, addressed by a
 * {@link ClientSimManager.LocalVCSimulationDataIdentifier}, with no server data directory
 * ({@code vcell.primarySimdatadir.internal}) and no Python VTK service ({@code vcell.vtk.pythonDir}). Unlike
 * {@link FakeChomboRun}, which stubs the VTU seam and has no membrane, every mesh here is built by the real path —
 * {@code ChomboFileReader} → {@code ChomboMeshMapping} → the pure-Java VTU writers.
 * <p>
 * The fixture is vcell-core's {@code org/vcell/vis/chombo} (see {@code ChomboRunFixture} there): Chombo runs of a
 * disk of radius 3 (2D) and a ball of radius 4 (3D) about (5, 5, 5) in a 10 µm box on a 1.25 µm grid, whose
 * {@code s2} (membrane) and {@code RanC_nuc} (volume, ×1e-8, at t = 0) are the {@link #code} of the voxel each value
 * was computed in.
 */
@Tag("Fast")
// one static server per JVM: classes that start and stop it must not run concurrently
@ResourceLock("fieldViewerServer")
public class FieldViewerServerChomboLocalRunTest {

	static final String SIM_2D = "105373152";
	static final String SIM_3D = "104116603";
	static final String VOLUME = "subdomain1.vol0";
	static final String MEMBRANE = "subdomain1.vol0_Membrane";
	/** vcell-core's fixture, from vcell-client (where Maven runs this test) or the repository root (the browser tests) */
	private static final File FIXTURE = Stream.of("../vcell-core", "vcell-core")
			.map(module -> new File(module, "src/test/resources/org/vcell/vis/chombo"))
			.filter(File::isDirectory).findFirst().orElse(new File("../vcell-core/src/test/resources/org/vcell/vis/chombo"));

	private Path root;
	private Path localSimDir;
	private int port;

	/** the voxel of the 1.25 µm grid holding (x, y, z): the fixture's s2, and its RanC_nuc ×1e8 at t = 0 */
	static double code(double x, double y, double z) {
		return Math.floor(x / 1.25) + 100 * Math.floor(y / 1.25) + 10000 * Math.floor(z / 1.25);
	}

	@BeforeEach
	public void setup() throws Exception {
		Assertions.assertNull(System.getProperty("vcell.primarySimdatadir.internal"), "a desktop has no server data directory");
		Assertions.assertNull(System.getProperty("vcell.vtk.pythonDir"), "a desktop has no Python VTK service");
		root = Files.createTempDirectory("FieldViewerServerChomboLocalRunTest_");
		localSimDir = registerChomboFixtures(root);
		port = FieldViewerServer.start();
		Assertions.assertTrue(port > 0);
	}

	/**
	 * Copies the two runs into {@code root}'s temp-user directory (where a quick run's files sit, as
	 * {@code ResourceUtil.getLocalSimDir} lays them out) and registers them, under {@link #SIM_2D} and
	 * {@link #SIM_3D}, as the desktop opens a quick run. Returns that directory.
	 */
	static Path registerChomboFixtures(Path root) throws Exception {
		Assertions.assertTrue(FIXTURE.isDirectory(), FIXTURE.getAbsolutePath());
		Path localSimDir = Files.createDirectories(root.resolve(User.tempUser.getName()));
		for (File f : FIXTURE.listFiles()) {
			Files.copy(f.toPath(), localSimDir.resolve(f.getName()), StandardCopyOption.REPLACE_EXISTING);
		}
		DataSetControllerImpl controller = new DataSetControllerImpl(
				new Cachetable(10 * Cachetable.minute, 100_000_000L), root.toFile(), null);
		LocalDataSetController local = new LocalDataSetController(null, controller, null, User.tempUser);
		VCDataManager dataManager = new VCDataManager(() -> local);
		for (String sim : new String[] { SIM_2D, SIM_3D }) {
			ClientSimManager.LocalVCSimulationDataIdentifier vcdID = new ClientSimManager.LocalVCSimulationDataIdentifier(
					new VCSimulationIdentifier(new KeyValue(sim), User.tempUser), 0, localSimDir.toFile());
			FieldViewerServer.register(vcdID, dataManager, (org.vcell.vis.vcell.SubdomainInfo) null,
					sim.equals(SIM_3D) ? "chombo 3d" : "chombo 2D");
		}
		return localSimDir;
	}

	@AfterEach
	public void teardown() throws Exception {
		FieldViewerServer.stop();
		try (Stream<Path> walk = Files.walk(root)) {
			walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}

	private JsonObject get(String sim, String path, String query) throws Exception {
		HttpResponse<String> r = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=" + sim + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return JsonParser.parseString(r.body()).getAsJsonObject();
	}

	@Test
	public void a3DMembraneIsServedAsATriangulatedSurfaceWithItsValuesPaired() throws Exception {
		check(SIM_3D, 3);
	}

	@Test
	public void a2DMembraneIsServedAsLineSegmentsWithItsValuesPaired() throws Exception {
		check(SIM_2D, 2);
	}

	private void check(String sim, int dimension) throws Exception {
		JsonObject info = get(sim, "/info", "");
		Assertions.assertEquals("Chombo", info.get("solver").getAsString());
		List<String> domains = new ArrayList<>();
		info.getAsJsonArray("domains").forEach(d -> domains.add(d.getAsString()));
		Assertions.assertTrue(domains.contains(VOLUME) && domains.contains(MEMBRANE), domains.toString());
		boolean s2 = false;
		for (var v : info.getAsJsonArray("variables")) {
			JsonObject var = v.getAsJsonObject();
			s2 |= var.get("name").getAsString().equals("s2") && var.get("domain").getAsString().equals(MEMBRANE);
		}
		Assertions.assertTrue(s2, "the membrane variable s2 is listed on the membrane domain: " + info);
		JsonArray times = info.getAsJsonArray("times");
		Assertions.assertEquals(3, times.size());

		// the membrane: one surface of triangles in 3D (a curve of segments in 2D), in microns on the sphere (circle)
		JsonObject grid = get(sim, "/grid", "&domain=" + MEMBRANE);
		Assertions.assertTrue(grid.get("bodyFitted").getAsBoolean());
		Assertions.assertEquals(dimension, grid.get("dimension").getAsInt(), "a 3D run's membrane is a surface in 3D");
		Assertions.assertEquals(dimension == 3 ? 5 : 3, grid.get("cellType").getAsInt(), "VTK_TRIANGLE in 3D, VTK_LINE in 2D");
		Assertions.assertNull(grid.get("cellTypes"), "one cell type");
		double[] points = doubles(grid.getAsJsonArray("points"));
		int[][] cells = cells(grid.getAsJsonArray("cells"));
		double radius = dimension == 3 ? 4 : 3;
		for (int p = 0; p < points.length / 3; p++) {
			double r2 = 0;
			for (int a = 0; a < dimension; a++) {
				r2 += (points[3 * p + a] - 5) * (points[3 * p + a] - 5);
			}
			Assertions.assertEquals(radius, Math.sqrt(r2), 0.125, "membrane point " + p + " in microns, on the boundary");
			if (dimension == 2) {
				Assertions.assertEquals(0, points[3 * p + 2], 0, "a 2D membrane lies in z = 0");
			}
		}

		// one value per drawn cell, and each the value of THAT cell, at every saved time (s2 does not change)
		for (int t = 0; t < times.size(); t++) {
			JsonObject field = get(sim, "/field", "&domain=" + MEMBRANE + "&var=s2&time=" + times.get(t).getAsString());
			Assertions.assertEquals(grid.get("geometryId").getAsString(), field.get("geometryId").getAsString());
			double[] values = doubles(field.getAsJsonArray("values"));
			Assertions.assertEquals(cells.length, values.length, "one value per membrane cell");
			for (int c = 0; c < cells.length; c++) {
				Assertions.assertEquals(codeOfCell(points, cells[c]), values[c], 1e-9, "s2 of membrane cell " + c + " at t index " + t);
			}
		}

		// the volume: its values pair with its cells the same way
		JsonObject volumeGrid = get(sim, "/grid", "&domain=" + VOLUME);
		Assertions.assertEquals(dimension, volumeGrid.get("dimension").getAsInt());
		double[] volumePoints = doubles(volumeGrid.getAsJsonArray("points"));
		int[][] volumeCells = cells(volumeGrid.getAsJsonArray("cells"));
		double[] ranC = doubles(get(sim, "/field", "&domain=" + VOLUME + "&var=RanC_nuc&time=0").getAsJsonArray("values"));
		Assertions.assertEquals(volumeCells.length, ranC.length);
		for (int c = 0; c < volumeCells.length; c++) {
			Assertions.assertEquals(codeOfCell(volumePoints, volumeCells[c]), Math.rint(ranC[c] * 1e8), "RanC_nuc of volume cell " + c);
		}

		// a click beside the membrane (just outside it) snaps onto it and reads that membrane element at every time
		int probeCell = cells.length / 3;
		double[] centre = centroid(points, cells[probeCell]);
		double[] click = new double[3];
		double r = 0;
		for (int a = 0; a < dimension; a++) {
			r += (centre[a] - 5) * (centre[a] - 5);
		}
		r = Math.sqrt(r);
		for (int a = 0; a < 3; a++) {
			click[a] = a < dimension ? 5 + (centre[a] - 5) * (r + 0.05) / r : centre[a];
		}
		String clickParam = String.format(Locale.ROOT, "%.9f,%.9f,%.9f", click[0], click[1], click[2]);
		JsonObject series = get(sim, "/timeseries", "&domain=" + MEMBRANE + "&var=s2&snap=nearest&points=" + clickParam)
				.getAsJsonArray("series").get(0).getAsJsonObject();
		Assertions.assertTrue(series.get("inDomain").getAsBoolean(), series.toString());
		Assertions.assertNotNull(series.get("snapped"), "the click moved onto the membrane: " + series);
		int snappedCell = series.get("cell").getAsInt();
		double[] probeValues = doubles(series.getAsJsonArray("values"));
		Assertions.assertEquals(3, probeValues.length);
		for (double v : probeValues) {
			Assertions.assertEquals(codeOfCell(points, cells[snappedCell]), v, 1e-9, "the snapped cell's s2: " + series);
		}
		// without the snap, the same click misses the membrane
		JsonObject missed = get(sim, "/timeseries", "&domain=" + MEMBRANE + "&var=s2&points=" + clickParam)
				.getAsJsonArray("series").get(0).getAsJsonObject();
		Assertions.assertFalse(missed.get("inDomain").getAsBoolean(), missed.toString());

		// the meshes and their indices were written beside the run, and nothing was handed to Python
		try (Stream<Path> files = Files.list(localSimDir)) {
			List<String> names = files.map(p -> p.getFileName().toString()).toList();
			for (String domain : new String[] { VOLUME, MEMBRANE }) {
				Assertions.assertTrue(names.contains("SimID_" + sim + "_0_" + domain + ".vtu"), names.toString());
				Assertions.assertTrue(names.contains("SimID_" + sim + "_0_" + domain + ".chomboindex"), names.toString());
			}
			Assertions.assertTrue(names.stream().noneMatch(n -> n.endsWith(".visMesh")), names.toString());
		}
	}

	private static double[] doubles(JsonArray a) {
		double[] d = new double[a.size()];
		for (int i = 0; i < d.length; i++) {
			d[i] = a.get(i).getAsDouble();
		}
		return d;
	}

	private static int[][] cells(JsonArray a) {
		int[][] cells = new int[a.size()][];
		for (int c = 0; c < cells.length; c++) {
			JsonArray cell = a.get(c).getAsJsonArray();
			cells[c] = new int[cell.size()];
			for (int v = 0; v < cells[c].length; v++) {
				cells[c][v] = cell.get(v).getAsInt();
			}
		}
		return cells;
	}

	private static double[] centroid(double[] points, int[] cell) {
		double[] c = new double[3];
		for (int p : cell) {
			for (int a = 0; a < 3; a++) {
				c[a] += points[3 * p + a] / cell.length;
			}
		}
		return c;
	}

	/** {@link #code} of the centre of the cell's bounding box */
	private static double codeOfCell(double[] points, int[] cell) {
		double[] lo = { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
		double[] hi = { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
		for (int p : cell) {
			for (int a = 0; a < 3; a++) {
				lo[a] = Math.min(lo[a], points[3 * p + a]);
				hi[a] = Math.max(hi[a], points[3 * p + a]);
			}
		}
		return code((lo[0] + hi[0]) / 2, (lo[1] + hi[1]) / 2, (lo[2] + hi[2]) / 2);
	}
}
