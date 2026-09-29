package org.vcell.client.viz;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.vcell.SubdomainInfo;

import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.LocalDataSetController;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * The field viewer's finite-volume endpoints over real FV runs, read the way the desktop reads a local
 * run: a {@link DataSetControllerImpl} over a {@link Cachetable}, behind a {@link VCDataManager}.
 * <p>
 * Two small runs (copied from vcell-core's {@code simdata/n5/ezequiel23}, the {@code N5ExporterTest}
 * fixtures, into this module's test resources under {@code fv/}):
 * <ul>
 * <li>{@code 597714292} — 2D, 15 × 15, {@code Cyt} inside {@code EC} with a membrane; one volume
 * variable, {@code Dex}, in {@code Cyt}; five saved times;</li>
 * <li>{@code 868220316} — 3D, 5 × 5 × 5, {@code subdomain1} and {@code subdomain0} with a membrane;
 * volume variables {@code s0}, {@code s1}, … in {@code subdomain0}; six saved times.</li>
 * </ul>
 */
@Tag("Fast")
// one static server per JVM: classes that start and stop it must not run concurrently
@ResourceLock("fieldViewerServer")
public class FieldViewerServerFvTest {

	static final String SIM_2D = "597714292";
	static final String SIM_3D = "868220316";
	private static final String[] EXTENSIONS = { ".functions", ".log", ".mesh", ".meshmetrics", ".subdomains", "00.zip" };
	private static final User OWNER = new User("ezequiel23", new KeyValue("258925427"));

	private Path root;
	private int port;

	@BeforeEach
	public void setup() throws Exception {
		root = Files.createTempDirectory("FieldViewerServerFvTest_");
		port = registerFvFixtures(root);
		Assertions.assertTrue(port > 0);
	}

	@AfterEach
	public void teardown() throws Exception {
		FieldViewerServer.stop();
		try (Stream<Path> walk = Files.walk(root)) {
			walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}

	/**
	 * Stages both FV fixture runs under {@code root/<user>/}, registers them with the field viewer and
	 * starts it.
	 *
	 * @return the server's port
	 */
	static int registerFvFixtures(Path root) throws Exception {
		stageFvFixtures(root);
		return FieldViewerServer.start();
	}

	/**
	 * Stages both FV fixture runs under {@code root/<user>/} and registers them with the field viewer,
	 * without starting it.
	 *
	 * @return the data manager they are registered with, for reading the runs directly as the desktop does
	 */
	static VCDataManager stageFvFixtures(Path root) throws Exception {
		Path userDir = Files.createDirectories(root.resolve(OWNER.getName()));
		DataSetControllerImpl controller = new DataSetControllerImpl(
				new Cachetable(10 * Cachetable.minute, 100_000_000L), root.toFile(), root.toFile());
		LocalDataSetController local = new LocalDataSetController(null, controller, null, OWNER);
		VCDataManager dataManager = new VCDataManager(() -> local);
		for (String sim : new String[] { SIM_2D, SIM_3D }) {
			for (String ext : EXTENSIONS) {
				String name = "SimID_" + sim + "_0_" + ext;
				try (InputStream in = FieldViewerServerFvTest.class.getResourceAsStream("fv/" + name)) {
					Assertions.assertNotNull(in, name);
					Files.copy(in, userDir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
				}
			}
			VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(
					new VCSimulationIdentifier(new KeyValue(sim), OWNER), 0);
			SubdomainInfo subdomains = SubdomainInfo.read(userDir.resolve("SimID_" + sim + "_0_.subdomains").toFile());
			FieldViewerServer.register(vcdID, dataManager, subdomains, sim.equals(SIM_2D) ? "fv::2d" : "fv::3d");
		}
		return dataManager;
	}

	/** The data identifier of a fixture run, as {@link #stageFvFixtures} registers it. */
	static VCSimulationDataIdentifier vcdID(String sim) {
		return new VCSimulationDataIdentifier(new VCSimulationIdentifier(new KeyValue(sim), OWNER), 0);
	}

	private HttpResponse<String> send(String sim, String path, String query) throws Exception {
		return HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=" + sim + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private String body(String sim, String path, String query) throws Exception {
		HttpResponse<String> r = send(sim, path, query);
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return r.body();
	}

	private JsonObject get(String sim, String path, String query) throws Exception {
		return JsonParser.parseString(body(sim, path, query)).getAsJsonObject();
	}

	/** The single-cell responses, recorded before multi-point {@code /timeseries} existed: they must not change. */
	@Test
	public void legacyCellResponsesAreUnchanged() throws Exception {
		Assertions.assertEquals("{\"name\":\"Dex\",\"domain\":\"Cyt\",\"cell\":7,\"volumeIndex\":36,"
				+ "\"times\":[0.0,0.5,1.0,1.5,2.0],"
				+ "\"values\":[10.0,7.016619792068857,6.406713063573952,6.253046105341611,6.214465325502035]}",
				body(SIM_2D, "/timeseries", "&domain=Cyt&var=Dex&cell=7"));
		Assertions.assertEquals("{\"name\":\"s0\",\"domain\":\"subdomain0\",\"cell\":11,\"volumeIndex\":11,"
				+ "\"times\":[0.0,0.1,0.2,0.3,0.4,0.5],"
				+ "\"values\":[11.0,22.01159422934578,28.081025539930113,30.585769707096624,31.130207016823828,30.5077369091229]}",
				body(SIM_3D, "/timeseries", "&domain=subdomain0&var=s0&cell=11"));
	}

	/** the centre of each served cell, as the viewer sends a picked voxel: {@code x,y,z;…} */
	private static double[][] cellCentres(JsonObject grid) {
		JsonArray points = grid.getAsJsonArray("points");
		JsonArray cells = grid.getAsJsonArray("cells");
		double[][] centres = new double[cells.size()][3];
		for (int c = 0; c < cells.size(); c++) {
			JsonArray cell = cells.get(c).getAsJsonArray();
			for (int k = 0; k < cell.size(); k++) {
				int v = cell.get(k).getAsInt();
				for (int a = 0; a < 3; a++) {
					centres[c][a] += points.get(3 * v + a).getAsDouble() / cell.size();
				}
			}
		}
		return centres;
	}

	private static String pointsParam(double[][] points, int from, int to) {
		StringBuilder sb = new StringBuilder("&points=");
		for (int i = from; i < to; i++) {
			sb.append(i > from ? ";" : "").append(points[i][0]).append(',').append(points[i][1]).append(',').append(points[i][2]);
		}
		return sb.toString();
	}

	@Test
	public void everyServedVoxelCentreMapsBackToItsOwnCell() throws Exception {
		for (String[] run : new String[][] { { SIM_2D, "Cyt", "Dex" }, { SIM_2D, "EC", "Dex" }, { SIM_3D, "subdomain0", "s0" } }) {
			double[][] centres = cellCentres(get(run[0], "/grid", "&domain=" + run[1]));
			for (int from = 0; from < centres.length; from += PointSeries.MAX_POINTS) {
				int to = Math.min(centres.length, from + PointSeries.MAX_POINTS);
				JsonArray series = get(run[0], "/timeseries", "&domain=" + run[1] + "&var=" + run[2]
						+ pointsParam(centres, from, to)).getAsJsonArray("series");
				for (int c = from; c < to; c++) {
					JsonObject s = series.get(c - from).getAsJsonObject();
					Assertions.assertEquals(c, s.get("cell").getAsInt(), run[0] + " " + run[1] + " cell " + c);
				}
			}
		}
	}

	@Test
	public void severalPointsMatchTheSingleCellTimeSeries() throws Exception {
		JsonObject grid = get(SIM_3D, "/grid", "&domain=subdomain0");
		double[][] centres = cellCentres(grid);
		int[] picks = { 0, 11, 17, centres.length - 1 };
		double[][] points = new double[picks.length][];
		for (int i = 0; i < picks.length; i++) {
			points[i] = centres[picks[i]];
		}
		JsonObject multi = get(SIM_3D, "/timeseries", "&domain=subdomain0&var=s0" + pointsParam(points, 0, points.length));
		Assertions.assertEquals("cell", multi.get("location").getAsString());
		Assertions.assertEquals("s0", multi.get("name").getAsString());
		Assertions.assertEquals("subdomain0", multi.get("domain").getAsString());
		JsonArray series = multi.getAsJsonArray("series");
		Assertions.assertEquals(picks.length, series.size());
		for (int i = 0; i < picks.length; i++) {
			JsonObject single = get(SIM_3D, "/timeseries", "&domain=subdomain0&var=s0&cell=" + picks[i]);
			JsonObject s = series.get(i).getAsJsonObject();
			Assertions.assertTrue(s.get("inDomain").getAsBoolean());
			Assertions.assertEquals(picks[i], s.get("cell").getAsInt());
			Assertions.assertEquals(single.get("volumeIndex").getAsInt(), s.get("volumeIndex").getAsInt());
			Assertions.assertEquals(single.getAsJsonArray("values"), s.getAsJsonArray("values"));
			Assertions.assertEquals(single.getAsJsonArray("times"), multi.getAsJsonArray("times"));
		}
	}

	@Test
	public void pointsOutsideTheDomainOrTheMeshAreGaps() throws Exception {
		// a voxel of the OTHER compartment: in the mesh, not in the variable's domain
		double[] other = cellCentres(get(SIM_3D, "/grid", "&domain=subdomain1"))[0];
		double[] inside = cellCentres(get(SIM_3D, "/grid", "&domain=subdomain0"))[0];
		double[][] points = { inside, other, { 100, 100, 100 } };
		JsonArray series = get(SIM_3D, "/timeseries", "&domain=subdomain0&var=s0" + pointsParam(points, 0, 3))
				.getAsJsonArray("series");
		JsonObject inOther = series.get(1).getAsJsonObject();
		Assertions.assertFalse(inOther.get("inDomain").getAsBoolean());
		Assertions.assertEquals(-1, inOther.get("cell").getAsInt());
		Assertions.assertTrue(inOther.get("volumeIndex").getAsInt() >= 0, "in the mesh, just not in the domain");
		for (com.google.gson.JsonElement v : inOther.getAsJsonArray("values")) {
			Assertions.assertTrue(v.isJsonNull(), "masked, not the other compartment's numbers");
		}
		JsonObject outside = series.get(2).getAsJsonObject();
		Assertions.assertFalse(outside.get("inDomain").getAsBoolean());
		Assertions.assertEquals(-1, outside.get("volumeIndex").getAsInt());
		Assertions.assertTrue(series.get(0).getAsJsonObject().get("inDomain").getAsBoolean());
	}

	@Test
	public void aTwoDimensionalPointMayLeaveOutZ() throws Exception {
		double[] c = cellCentres(get(SIM_2D, "/grid", "&domain=Cyt"))[7];
		JsonObject withZ = get(SIM_2D, "/timeseries", "&domain=Cyt&var=Dex&points=" + c[0] + "," + c[1] + "," + c[2]);
		JsonObject withoutZ = get(SIM_2D, "/timeseries", "&domain=Cyt&var=Dex&points=" + c[0] + "," + c[1]);
		Assertions.assertEquals(withZ, withoutZ, "z is filled in from the mesh plane");
		JsonObject s = withZ.getAsJsonArray("series").get(0).getAsJsonObject();
		Assertions.assertEquals(7, s.get("cell").getAsInt());
		Assertions.assertEquals(36, s.get("volumeIndex").getAsInt());
	}

	@Test
	public void badPointListsAreBadRequests() throws Exception {
		StringBuilder many = new StringBuilder("&points=");
		for (int i = 0; i <= PointSeries.MAX_POINTS; i++) {
			many.append(i > 0 ? ";" : "").append("1,1,1");
		}
		HttpResponse<String> tooMany = send(SIM_3D, "/timeseries", "&domain=subdomain0&var=s0" + many);
		Assertions.assertEquals(400, tooMany.statusCode());
		Assertions.assertTrue(tooMany.body().contains("at most 64 points"), tooMany.body());
		Assertions.assertEquals(400, send(SIM_3D, "/timeseries", "&domain=subdomain0&var=s0&points=1,1").statusCode(),
				"a 3D point needs its z");
		Assertions.assertEquals(400, send(SIM_3D, "/timeseries", "&domain=subdomain0&var=s0&points=1,x,1").statusCode());
		Assertions.assertEquals(400, send(SIM_3D, "/timeseries", "&domain=subdomain0&var=s0&points=").statusCode());
		Assertions.assertEquals(400, send(SIM_3D, "/timeseries", "&domain=nowhere&var=s0&points=1,1,1").statusCode());
	}
}
