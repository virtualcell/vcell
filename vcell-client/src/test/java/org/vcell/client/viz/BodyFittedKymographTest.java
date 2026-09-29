package org.vcell.client.viz;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * {@code /kymograph} for the body-fitted modes, end to end through the running server (docs/plan-plotting.md
 * P5): FEniCSx bundles (a 2D disk, a 2D disk moving along x, and a 3D ball in a box) and Chombo runs
 * ({@link FakeChomboRun}, 2D and 3D).
 */
@Tag("Fast")
// one static server per JVM: classes that start and stop it must not run concurrently
@ResourceLock("fieldViewerServer")
public class BodyFittedKymographTest {

	private static final String DISK = "987654321";
	private static final String MOVING = "777";
	private static final String RECEPTOR = "555";

	private int port;

	@BeforeEach
	public void setup() throws Exception {
		FieldViewerServer.registerBundle(DISK, 0, bundle("membrane_efflux.fenics"), "disk");
		FieldViewerServer.registerBundle(MOVING, 0, bundle("moving_translate.fenics"), "moving disk");
		FieldViewerServer.registerBundle(RECEPTOR, 0, bundle("receptor_3d.fenics"), "receptor 3d");
		FakeChomboRun.register();
		port = FieldViewerServer.start();
		Assertions.assertTrue(port > 0);
	}

	@AfterEach
	public void teardown() {
		FieldViewerServer.stop();
	}

	private static File bundle(String name) throws Exception {
		return new File(BodyFittedKymographTest.class.getResource(name + "/.zattrs").toURI()).getParentFile();
	}

	private HttpResponse<String> send(String sim, String route, String query) throws Exception {
		return HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + route + "?sim=" + sim + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private JsonObject get(String sim, String route, String query) throws Exception {
		HttpResponse<String> r = send(sim, route, query);
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return JsonParser.parseString(r.body()).getAsJsonObject();
	}

	private static String enc(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}

	private static double[] doubles(JsonArray a) {
		double[] d = new double[a.size()];
		for (int i = 0; i < d.length; i++) {
			d[i] = a.get(i).isJsonNull() ? Double.NaN : a.get(i).getAsDouble();
		}
		return d;
	}

	private static double[][] samplePoints(JsonObject k) {
		double[] flat = doubles(k.getAsJsonObject("samples").getAsJsonArray("points"));
		double[][] p = new double[flat.length / 3][];
		for (int i = 0; i < p.length; i++) {
			p[i] = new double[] { flat[3 * i], flat[3 * i + 1], flat[3 * i + 2] };
		}
		return p;
	}

	/** Each sample's column of the kymograph equals {@code /timeseries} at the sample's point (in chunks of 64). */
	private void assertColumnsAreTheTimeSeries(String sim, String query, JsonObject k) throws Exception {
		double[][] points = samplePoints(k);
		JsonArray rows = k.getAsJsonArray("values");
		for (int from = 0; from < points.length; from += PointSeries.MAX_POINTS) {
			StringBuilder list = new StringBuilder();
			int to = Math.min(points.length, from + PointSeries.MAX_POINTS);
			for (int i = from; i < to; i++) {
				list.append(i > from ? ";" : "").append(points[i][0]).append(',').append(points[i][1]).append(',').append(points[i][2]);
			}
			JsonArray series = get(sim, "/timeseries", query + "&points=" + enc(list.toString())).getAsJsonArray("series");
			for (int i = from; i < to; i++) {
				double[] values = doubles(series.get(i - from).getAsJsonObject().getAsJsonArray("values"));
				for (int r = 0; r < rows.size(); r++) {
					JsonElement v = rows.get(r).getAsJsonArray().get(i);
					if (Double.isNaN(values[r])) {
						Assertions.assertTrue(v.isJsonNull(), "sample " + i + " row " + r + " is a gap in the time series too");
					} else {
						Assertions.assertEquals(values[r], v.getAsDouble(), 0.0, "sample " + i + " row " + r);
					}
				}
			}
		}
	}

	private static boolean[] inDomain(JsonObject k) {
		JsonArray a = k.getAsJsonObject("samples").getAsJsonArray("inDomain");
		boolean[] b = new boolean[a.size()];
		for (int i = 0; i < b.length; i++) {
			b[i] = a.get(i).getAsBoolean();
		}
		return b;
	}

	@Test
	public void aDiameterOfTheDiskIsThePointTimeSeriesAtEachSample() throws Exception {
		String query = "&domain=cytosol_dom&var=u";
		JsonObject k = get(DISK, "/kymograph", query + "&samples=41&path=" + enc("-0.7,0.013;0.7,0.013"));
		Assertions.assertEquals("point", k.get("location").getAsString(), "P1 values, interpolated");
		Assertions.assertEquals("uniform", k.get("sampling").getAsString());
		Assertions.assertFalse(k.get("movingMesh").getAsBoolean());
		Assertions.assertEquals(1.4, k.get("pathLength").getAsDouble(), 1e-12);
		double[] arc = doubles(k.getAsJsonObject("samples").getAsJsonArray("arcLength"));
		Assertions.assertEquals(41, arc.length);
		for (int i = 0; i < arc.length; i++) {
			Assertions.assertEquals(1.4 * i / 40, arc[i], 1e-12, "evenly spaced, from 0 to the path's length");
		}
		double[][] points = samplePoints(k);
		Assertions.assertArrayEquals(new double[] { -0.7, 0.013, 0 }, points[0], 1e-12);
		Assertions.assertArrayEquals(new double[] { 0.7, 0.013, 0 }, points[40], 1e-12);
		Assertions.assertArrayEquals(new double[] { 0.0, 0.1, 0.2 }, doubles(k.getAsJsonArray("times")), 0.0);
		Assertions.assertEquals("[0,1,2]", k.getAsJsonArray("timeIndices").toString());
		// the disk has radius 0.5: the samples beyond it are gaps at every time, the ones within have values
		boolean[] in = inDomain(k);
		for (int i = 0; i < 41; i++) {
			boolean inside = Math.hypot(points[i][0], points[i][1]) < 0.49;
			boolean outside = Math.hypot(points[i][0], points[i][1]) > 0.51;
			if (inside) {
				Assertions.assertTrue(in[i], "sample " + i);
			}
			if (outside) {
				Assertions.assertFalse(in[i], "sample " + i);
				for (JsonElement row : k.getAsJsonArray("values")) {
					Assertions.assertTrue(row.getAsJsonArray().get(i).isJsonNull());
				}
			}
		}
		assertColumnsAreTheTimeSeries(DISK, query, k);
		double[] range = doubles(k.getAsJsonArray("range"));
		Assertions.assertTrue(range[0] > 0.99 && range[1] <= 1.0 + 1e-12, java.util.Arrays.toString(range));
	}

	@Test
	public void theDefaultSampleCountIsTwoPerMeanCellDiameter() throws Exception {
		JsonObject grid = get(DISK, "/grid", "&domain=cytosol_dom");
		JsonArray pts = grid.getAsJsonArray("points");
		double sum = 0;
		JsonArray cells = grid.getAsJsonArray("cells");
		for (JsonElement cell : cells) {
			JsonArray c = cell.getAsJsonArray();
			double d = 0;
			for (int a = 0; a < c.size(); a++) {
				for (int b = a + 1; b < c.size(); b++) {
					double dx = pts.get(3 * c.get(a).getAsInt()).getAsDouble() - pts.get(3 * c.get(b).getAsInt()).getAsDouble();
					double dy = pts.get(3 * c.get(a).getAsInt() + 1).getAsDouble() - pts.get(3 * c.get(b).getAsInt() + 1).getAsDouble();
					d = Math.max(d, Math.hypot(dx, dy));
				}
			}
			sum += d;
		}
		double meanDiameter = sum / cells.size();
		JsonObject k = get(DISK, "/kymograph", "&domain=cytosol_dom&var=u&path=" + enc("-0.7,0.013;0.7,0.013"));
		int expected = (int) Math.max(16, Math.min(1000, Math.ceil(2 * 1.4 / meanDiameter)));
		Assertions.assertTrue(expected > 16 && expected < 1000, "the rule, not a bound: " + expected);
		Assertions.assertEquals(expected, k.getAsJsonObject("samples").getAsJsonArray("arcLength").size());
		// a short line takes the lower bound
		JsonObject shortLine = get(DISK, "/kymograph", "&domain=cytosol_dom&var=u&path=" + enc("0,0;0.01,0"));
		Assertions.assertEquals(16, shortLine.getAsJsonObject("samples").getAsJsonArray("arcLength").size());
	}

	@Test
	public void onAnAleRunTheLineStaysInTheLabFrameAndTheGapsMoveWithTheBoundary() throws Exception {
		String query = "&domain=cell&var=C_cyt";
		JsonObject k = get(MOVING, "/kymograph", query + "&samples=81&path=" + enc("1,5.01;9,5.01"));
		Assertions.assertTrue(k.get("movingMesh").getAsBoolean());
		JsonArray rows = k.getAsJsonArray("values");
		Assertions.assertEquals(11, rows.size());
		int[] first = new int[rows.size()];
		int[] last = new int[rows.size()];
		for (int r = 0; r < rows.size(); r++) {
			double[] row = doubles(rows.get(r).getAsJsonArray());
			first[r] = -1;
			for (int i = 0; i < row.length; i++) {
				if (!Double.isNaN(row[i])) {
					if (first[r] < 0) {
						first[r] = i;
					}
					last[r] = i;
				}
			}
			Assertions.assertTrue(first[r] > 0 && last[r] < 80, "the disk lies within the line at every time");
			if (r > 0) {
				Assertions.assertTrue(first[r] >= first[r - 1] && last[r] >= last[r - 1], "the null band moves along +x");
			}
		}
		// the disk (radius 3 about x = 5 at t = 0) moves about 0.5 along x by t = 1: five samples of 0.1
		Assertions.assertEquals(10, first[0], 1);
		Assertions.assertEquals(70, last[0], 1);
		Assertions.assertTrue(first[10] - first[0] >= 4 && last[10] - last[0] >= 4,
				"from " + first[0] + "…" + last[0] + " to " + first[10] + "…" + last[10]);
		assertColumnsAreTheTimeSeries(MOVING, query, k);
	}

	@Test
	public void aLineThroughTwoDomainsIsAGapInTheOther() throws Exception {
		// a chord of the box through the ball (radius about 0.5) at its centre, off the mesh's symmetry planes
		String path = "&samples=37&path=" + enc("-0.9,0.05,0.03;0.9,0.05,0.03");
		JsonObject inner = get(RECEPTOR, "/kymograph", "&domain=cyto_dom&var=s_cyto" + path);
		JsonObject outer = get(RECEPTOR, "/kymograph", "&domain=ext_dom&var=s_ext" + path);
		boolean[] inBall = inDomain(inner);
		boolean[] inBox = inDomain(outer);
		double[][] points = samplePoints(inner);
		for (int i = 0; i < points.length; i++) {
			double r = Math.abs(points[i][0]);
			if (r < 0.4) {
				Assertions.assertTrue(inBall[i] && !inBox[i], "sample " + i + " at x = " + points[i][0] + " is in the ball");
			} else if (r > 0.55) {
				Assertions.assertTrue(!inBall[i] && inBox[i], "sample " + i + " at x = " + points[i][0] + " is outside it");
			}
			Assertions.assertFalse(inBall[i] && inBox[i], "one domain or the other, not both, at " + points[i][0]);
		}
		assertColumnsAreTheTimeSeries(RECEPTOR, "&domain=cyto_dom&var=s_cyto", inner);
		assertColumnsAreTheTimeSeries(RECEPTOR, "&domain=ext_dom&var=s_ext", outer);
	}

	@Test
	public void theStrideAndTheValueLimit() throws Exception {
		String query = "&domain=cell&var=C_cyt&samples=40&path=" + enc("1,5;9,5");
		JsonObject strided = get(MOVING, "/kymograph", query + "&tstep=3");
		Assertions.assertEquals("[0,3,6,9]", strided.getAsJsonArray("timeIndices").toString());
		Assertions.assertArrayEquals(new double[] { 0, 0.3, 0.6, 0.9 }, doubles(strided.getAsJsonArray("times")), 1e-12);
		JsonObject full = get(MOVING, "/kymograph", query);
		Assertions.assertEquals(full.getAsJsonArray("values").get(3), strided.getAsJsonArray("values").get(1),
				"row 1 of the stride is saved time 3");

		// 40 samples × 11 times is over a limit of 100: the smallest stride that fits returns 2 rows (0 and 6)
		System.setProperty("vcell.fieldViewer.maxKymographValues", "100");
		try {
			HttpResponse<String> r = send(MOVING, "/kymograph", query);
			Assertions.assertEquals(400, r.statusCode(), r.body());
			Assertions.assertEquals(6, JsonParser.parseString(r.body()).getAsJsonObject().get("suggestedTstep").getAsInt());
			Assertions.assertEquals("[0,6]", get(MOVING, "/kymograph", query + "&tstep=6").getAsJsonArray("timeIndices").toString());
		} finally {
			System.clearProperty("vcell.fieldViewer.maxKymographValues");
		}
	}

	@Test
	public void badRequests() throws Exception {
		String[][] cases = {
				{ DISK, "&var=u&samples=2001&path=" + enc("0,0;0.1,0"), "at most 2000 samples" },
				{ DISK, "&var=u&samples=1&path=" + enc("0,0;0.1,0"), "at least 2" },
				{ DISK, "&var=u&samples=many&path=" + enc("0,0;0.1,0"), "malformed 'samples'" },
				{ DISK, "&var=u&path=" + enc("0.1,0;0.1,0"), "zero length" },
				{ DISK, "&var=u&tstep=0&path=" + enc("0,0;0.1,0"), "'tstep' must be at least 1" },
				{ DISK, "&var=nope&path=" + enc("0,0;0.1,0"), "unknown variable" },
				{ RECEPTOR, "&domain=ext_dom&var=s_ext&path=" + enc("0,0;0.1,0"), "the domain is 3D" },
				{ RECEPTOR, "&domain=mem_dom&var=R&path=" + enc("0,0,0;0.1,0,0"), "membrane kymographs are not supported yet" },
				{ FakeChomboRun.SIM_2D, "&domain=cyt&var=nope&path=" + enc("1,3;7,3"), "unknown variable" },
				{ FakeChomboRun.SIM_2D, "&domain=cyt&var=C&samples=2001&path=" + enc("1,3;7,3"), "at most 2000 samples" },
		};
		for (String[] c : cases) {
			HttpResponse<String> r = send(c[0], "/kymograph", c[1]);
			Assertions.assertEquals(400, r.statusCode(), c[1] + " → " + r.body());
			Assertions.assertTrue(r.body().contains(c[2]), c[1] + " → " + r.body());
		}
		StringBuilder many = new StringBuilder();
		for (int i = 0; i <= FvLineSampler.MAX_PATH_VERTICES; i++) {
			many.append(i > 0 ? ";" : "").append(0.001 * i).append(",0");
		}
		HttpResponse<String> r = send(DISK, "/kymograph", "&var=u&path=" + enc(many.toString()));
		Assertions.assertEquals(400, r.statusCode(), r.body());
		Assertions.assertTrue(r.body().contains("at most 64 path vertices"), r.body());
	}

	@Test
	public void aLineOutsideTheDomainIsAllGaps() throws Exception {
		JsonObject k = get(DISK, "/kymograph", "&var=u&path=" + enc("2,2;3,3"));
		for (boolean b : inDomain(k)) {
			Assertions.assertFalse(b);
		}
		Assertions.assertEquals("[0.0,0.0]", k.getAsJsonArray("range").toString());
	}

	@Test
	public void aBodyFittedKymographIsAHeavyJob() throws Exception {
		Assertions.assertTrue(FieldViewerServer.HEAVY_JOBS.tryAcquire());
		try {
			for (String sim : new String[] { DISK, FakeChomboRun.SIM_2D }) {
				HttpResponse<String> busy = send(sim, "/kymograph", "&var=" + (sim.equals(DISK) ? "u" : "C") + "&path=" + enc("1,3;2,3"));
				Assertions.assertEquals(503, busy.statusCode(), busy.body());
				Assertions.assertTrue(JsonParser.parseString(busy.body()).getAsJsonObject().get("busy").getAsBoolean());
			}
		} finally {
			FieldViewerServer.HEAVY_JOBS.release();
		}
	}

	/** the value of the Chombo lattice cell holding a point (its centre's value), or NaN outside the disk or ball */
	private static double chomboValue(double[] p, double t, boolean threeD) {
		double h = FakeChomboRun.H;
		double cx = Math.floor(p[0] / h) * h + h / 2, cy = Math.floor(p[1] / h) * h + h / 2;
		double cz = threeD ? Math.floor(p[2] / h) * h + h / 2 : 0;
		double[] c = threeD ? FakeChomboRun.CENTRE_3D : FakeChomboRun.CENTRE_2D;
		double r = threeD ? FakeChomboRun.RADIUS_3D : FakeChomboRun.RADIUS_2D;
		double d2 = (cx - c[0]) * (cx - c[0]) + (cy - c[1]) * (cy - c[1]) + (threeD ? (cz - c[2]) * (cz - c[2]) : 0);
		return d2 < r * r ? FakeChomboRun.value(cx, cy, cz, t) : Double.NaN;
	}

	@Test
	public void aChomboKymographReadsTheCellHoldingEachSample() throws Exception {
		Assertions.assertEquals("Chombo", get(FakeChomboRun.SIM_2D, "/info", "").get("solver").getAsString());
		// 2D: through the disk, off the lattice lines (a sample on a cell face would read either neighbour)
		JsonObject k = get(FakeChomboRun.SIM_2D, "/kymograph", "&domain=cyt&var=C&samples=31&path=" + enc("0.6,3.1;7.4,3.1"));
		Assertions.assertEquals("cell", k.get("location").getAsString(), "a value per cell");
		Assertions.assertEquals("uniform", k.get("sampling").getAsString());
		Assertions.assertFalse(k.get("movingMesh").getAsBoolean(), "Chombo's mesh is static");
		double[][] points = samplePoints(k);
		Assertions.assertEquals(0.0, points[0][2], 0.0, "the 2D mesh's plane");
		JsonArray rows = k.getAsJsonArray("values");
		Assertions.assertEquals(FakeChomboRun.TIMES.length, rows.size());
		int gaps = 0;
		for (int r = 0; r < rows.size(); r++) {
			for (int i = 0; i < points.length; i++) {
				double expected = chomboValue(points[i], FakeChomboRun.TIMES[r], false);
				JsonElement v = rows.get(r).getAsJsonArray().get(i);
				// the cut corner of a boundary pentagon is outside the mesh: a gap where the lattice cell would read
				if (v.isJsonNull()) {
					gaps++;
					continue;
				}
				Assertions.assertEquals(expected, v.getAsDouble(), 1e-9, "sample " + i + " at " + points[i][0]);
			}
		}
		Assertions.assertTrue(gaps >= 2 * rows.size(), "the ends of the line are outside the disk");
		assertColumnsAreTheTimeSeries(FakeChomboRun.SIM_2D, "&domain=cyt&var=C", k);

		// 3D: voxels inside, polyhedra at the ball's surface
		JsonObject k3 = get(FakeChomboRun.SIM_3D, "/kymograph", "&domain=cyt&var=C&samples=25&path=" + enc("0.3,3.1,2.9;5.7,3.1,2.9"));
		double[][] p3 = samplePoints(k3);
		JsonArray last = k3.getAsJsonArray("values").get(FakeChomboRun.TIMES.length - 1).getAsJsonArray();
		int inside = 0;
		for (int i = 0; i < p3.length; i++) {
			double expected = chomboValue(p3[i], FakeChomboRun.TIMES[FakeChomboRun.TIMES.length - 1], true);
			if (Double.isNaN(expected)) {
				Assertions.assertTrue(last.get(i).isJsonNull(), "sample " + i + " is outside the ball");
			} else {
				inside++;
				Assertions.assertEquals(expected, last.get(i).getAsDouble(), 1e-9, "sample " + i);
			}
		}
		Assertions.assertTrue(inside > 10, "most of the chord is inside the ball");
		assertColumnsAreTheTimeSeries(FakeChomboRun.SIM_3D, "&domain=cyt&var=C", k3);
	}
}
