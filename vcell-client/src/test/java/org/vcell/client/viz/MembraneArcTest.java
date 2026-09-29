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

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Membrane curves on a 2D FEniCSx membrane (docs/plan-plotting.md P7): {@code /kymograph} on a line-mesh domain
 * runs along the membrane between the snapped picks ({@link MembraneArc}). The fixture {@code receptor_2d.fenics}
 * is a disk of radius 0.5 in a 2 × 2 box, with the receptor {@code R} on its membrane {@code mem_dom}, a closed
 * curve of 61 line cells; R starts at {@code 10·(x + 0.5) + 4·y}, so it varies along the membrane.
 */
@Tag("Fast")
@ResourceLock("fieldViewerServer")
public class MembraneArcTest {

	private static final String SIM = "556";
	private static final String Q = "&domain=mem_dom&var=R";

	private int port;

	@BeforeEach
	public void setup() throws Exception {
		File bundle = new File(MembraneArcTest.class.getResource("receptor_2d.fenics/.zattrs").toURI()).getParentFile();
		FieldViewerServer.registerBundle(SIM, 0, bundle, "receptor 2d");
		port = FieldViewerServer.start();
		Assertions.assertTrue(port > 0);
	}

	@AfterEach
	public void teardown() {
		FieldViewerServer.stop();
	}

	private HttpResponse<String> send(String route, String query) throws Exception {
		return HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + route + "?sim=" + SIM + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private JsonObject get(String route, String query) throws Exception {
		HttpResponse<String> r = send(route, query);
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

	private static int[] ints(JsonArray a) {
		int[] d = new int[a.size()];
		for (int i = 0; i < d.length; i++) {
			d[i] = a.get(i).getAsInt();
		}
		return d;
	}

	/** a point at {@code degrees} round the membrane, {@code r} from the disk's centre */
	private static String at(double degrees, double r) {
		double a = Math.toRadians(degrees);
		return r * Math.cos(a) + "," + r * Math.sin(a);
	}

	private static double angleOf(double[] p) {
		double a = Math.toDegrees(Math.atan2(p[1], p[0]));
		return a < 0 ? a + 360 : a;
	}

	/**
	 * The plan's done-when: the arc's length is the sum of its edge lengths, its interior samples are mesh
	 * vertices whose values are the P1 vertex values ({@code /field}) at every time, and its snapped ends are
	 * the membrane probes ({@code /timeseries?snap=nearest}) at the picks.
	 */
	@Test
	public void anArcRunsAlongTheMembraneThroughItsVertices() throws Exception {
		// picks just outside and just inside the membrane, a quarter turn apart
		String path = at(10, 0.52) + ";" + at(100, 0.48);
		JsonObject k = get("/kymograph", Q + "&path=" + enc(path));
		Assertions.assertEquals("membrane", k.get("sampling").getAsString());
		Assertions.assertEquals("point", k.get("location").getAsString());
		Assertions.assertFalse(k.get("movingMesh").getAsBoolean());

		JsonObject grid = get("/grid", Q);
		double[] p = doubles(grid.getAsJsonArray("points"));
		JsonObject samples = k.getAsJsonObject("samples");
		double[] arc = doubles(samples.getAsJsonArray("arcLength"));
		double[] flat = doubles(samples.getAsJsonArray("points"));
		int[] vertex = ints(samples.getAsJsonArray("vertex"));
		int n = arc.length;
		Assertions.assertEquals(n, vertex.length);
		Assertions.assertTrue(n >= 15 && n <= 20, "a quarter of 61 edges, plus the ends: " + n);

		// the ends: the snapped picks, inside a cell, on the membrane
		Assertions.assertEquals(-1, vertex[0]);
		Assertions.assertEquals(-1, vertex[n - 1]);
		double[] first = { flat[0], flat[1], flat[2] };
		double[] last = { flat[3 * n - 3], flat[3 * n - 2], flat[3 * n - 1] };
		Assertions.assertEquals(10, angleOf(first), 1.0);
		Assertions.assertEquals(100, angleOf(last), 1.0);
		Assertions.assertEquals(0.5, Math.hypot(first[0], first[1]), 2e-3, "on the membrane");
		JsonArray waypoints = k.getAsJsonArray("path");
		Assertions.assertArrayEquals(first, doubles(waypoints.get(0).getAsJsonArray()), 0.0, "the path is the snapped picks");
		Assertions.assertArrayEquals(last, doubles(waypoints.get(1).getAsJsonArray()), 0.0);

		// the interior samples are consecutive mesh vertices, each at its own point, running anticlockwise
		double sum = 0;
		for (int i = 1; i < n; i++) {
			if (i < n - 1) {
				Assertions.assertTrue(vertex[i] >= 0, "sample " + i + " is a mesh vertex");
				for (int a = 0; a < 3; a++) {
					Assertions.assertEquals(p[3 * vertex[i] + a], flat[3 * i + a], 0.0);
				}
			}
			double edge = Math.sqrt(Math.pow(flat[3 * i] - flat[3 * i - 3], 2) + Math.pow(flat[3 * i + 1] - flat[3 * i - 2], 2)
					+ Math.pow(flat[3 * i + 2] - flat[3 * i - 1], 2));
			sum += edge;
			Assertions.assertEquals(sum, arc[i], 1e-12, "arc length accumulates the edges");
			Assertions.assertTrue(angleOf(new double[] { flat[3 * i], flat[3 * i + 1] }) > angleOf(new double[] { flat[3 * i - 3], flat[3 * i - 2] }));
		}
		Assertions.assertEquals(0, arc[0]);
		Assertions.assertEquals(sum, k.get("pathLength").getAsDouble(), 1e-12, "the arc's length is the sum of its edges");
		Assertions.assertEquals(Math.PI / 4, sum, 0.01, "a quarter of the circumference, less the polygon's shortfall");

		// values: the P1 vertex values at every time, and the membrane probes at the ends
		JsonArray rows = k.getAsJsonArray("values");
		double[] times = doubles(k.getAsJsonArray("times"));
		Assertions.assertEquals(4, times.length);
		for (int r = 0; r < times.length; r++) {
			double[] field = doubles(get("/field", Q + "&time=" + times[r]).getAsJsonArray("values"));
			double[] row = doubles(rows.get(r).getAsJsonArray());
			for (int i = 1; i < n - 1; i++) {
				Assertions.assertEquals(field[vertex[i]], row[i], 0.0, "row " + r + " sample " + i);
			}
		}
		JsonArray series = get("/timeseries", Q + "&snap=nearest&points=" + enc(path)).getAsJsonArray("series");
		for (int end = 0; end < 2; end++) {
			double[] probe = doubles(series.get(end).getAsJsonObject().getAsJsonArray("values"));
			for (int r = 0; r < times.length; r++) {
				Assertions.assertEquals(probe[r], rows.get(r).getAsJsonArray().get(end == 0 ? 0 : n - 1).getAsDouble(), 1e-12,
						"end " + end + " row " + r + " is the membrane probe at the pick");
			}
		}
		// R starts at 10·(x + 0.5) + 4·y: the initial row follows it along the arc
		double[] row0 = doubles(rows.get(0).getAsJsonArray());
		for (int i = 0; i < n; i++) {
			Assertions.assertEquals(10 * (flat[3 * i] + 0.5) + 4 * flat[3 * i + 1], row0[i], 0.05, "sample " + i);
		}
	}

	@Test
	public void twoPicksTakeTheShorterWayRoundAndAThirdChoosesTheOther() throws Exception {
		JsonObject shortWay = get("/kymograph", Q + "&path=" + enc(at(20, 0.5) + ";" + at(300, 0.5)));
		double shortLength = shortWay.get("pathLength").getAsDouble();
		Assertions.assertEquals(2 * Math.PI * 0.5 * 80 / 360, shortLength, 0.01, "80° through 0°, not 280°");
		double[] flat = doubles(shortWay.getAsJsonObject("samples").getAsJsonArray("points"));
		for (int i = 0; i < flat.length / 3; i++) {
			Assertions.assertTrue(flat[3 * i] > 0.08, "stays on the right of the disk");
		}
		// a waypoint on the far side: 20° → 160° → 300°, the long way
		JsonObject longWay = get("/kymograph", Q + "&path=" + enc(at(20, 0.5) + ";" + at(160, 0.5) + ";" + at(300, 0.5)));
		Assertions.assertEquals(2 * Math.PI * 0.5 * 280 / 360, longWay.get("pathLength").getAsDouble(), 0.02);
		Assertions.assertEquals(3, longWay.getAsJsonArray("path").size());
		double[] arc = doubles(longWay.getAsJsonObject("samples").getAsJsonArray("arcLength"));
		for (int i = 1; i < arc.length; i++) {
			Assertions.assertTrue(arc[i] > arc[i - 1], "no repeated sample where the legs meet");
		}
	}

	@Test
	public void theStrideApplies() throws Exception {
		JsonObject k = get("/kymograph", Q + "&tstep=2&path=" + enc(at(0, 0.5) + ";" + at(45, 0.5)));
		Assertions.assertEquals("[0,2]", k.getAsJsonArray("timeIndices").toString());
		Assertions.assertEquals(2, k.getAsJsonArray("values").size());
	}

	@Test
	public void badRequests() throws Exception {
		String[][] cases = {
				{ Q + "&path=" + enc("0.9,0.9;" + at(45, 0.5)), "is not on the membrane" },
				{ Q + "&path=" + enc("0.5,0"), "zero length" },
		};
		for (String[] c : cases) {
			HttpResponse<String> r = send("/kymograph", c[0]);
			Assertions.assertEquals(400, r.statusCode(), c[0] + " → " + r.body());
			Assertions.assertTrue(r.body().contains(c[1]), c[0] + " → " + r.body());
		}
	}

	/** Two separate curves, a pick on a vertex, and the sample limit, on hand-built line meshes. */
	@Test
	public void onHandBuiltLineMeshes() {
		// two polylines: (0,0)-(1,0)-(2,0) and (0,5)-(1,5)
		VtuGridParser.VtuGrid grid = new VtuGridParser.VtuGrid(
				new double[] { 0, 0, 0, 1, 0, 0, 2, 0, 0, 0, 5, 0, 1, 5, 0 },
				new int[][] { { 0, 1 }, { 1, 2 }, { 3, 4 } },
				new int[] { VtuGridParser.VTK_LINE, VtuGridParser.VTK_LINE, VtuGridParser.VTK_LINE });
		MembraneArc arc = MembraneArc.build(grid, new double[][] { { 1, 0.1, 0 }, { 1.5, -0.1, 0 } }, 100);
		Assertions.assertArrayEquals(new int[] { 1, -1 }, arc.vertex, "a pick beside a vertex snaps onto it, once");
		Assertions.assertArrayEquals(new double[] { 0, 0.5 }, arc.arcLength, 1e-15);
		Assertions.assertArrayEquals(new double[] { 20, 25 }, arc.values(new double[] { 10, 20, 30, 0, 0 }), 1e-12);

		arc = MembraneArc.build(grid, new double[][] { { 0.25, 0, 0 }, { 1.75, 0, 0 } }, 100);
		Assertions.assertArrayEquals(new int[] { -1, 1, -1 }, arc.vertex);
		Assertions.assertEquals(1.5, arc.length(), 1e-15);

		IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
				() -> MembraneArc.build(grid, new double[][] { { 1, 0.1, 0 }, { 1, 0.2, 0 } }, 100));
		Assertions.assertTrue(e.getMessage().contains("zero length"), e.getMessage());
		e = Assertions.assertThrows(IllegalArgumentException.class,
				() -> MembraneArc.build(grid, new double[][] { { 0.5, 0, 0 }, { 0.5, 5, 0 } }, 100));
		Assertions.assertTrue(e.getMessage().contains("separate pieces"), e.getMessage());
		e = Assertions.assertThrows(IllegalArgumentException.class,
				() -> MembraneArc.build(grid, new double[][] { { 0.25, 0, 0 }, { 1.75, 0, 0 } }, 2));
		Assertions.assertTrue(e.getMessage().contains("more than 2 mesh vertices"), e.getMessage());

		VtuGridParser.VtuGrid triangles = new VtuGridParser.VtuGrid(new double[] { 0, 0, 0, 1, 0, 0, 0, 1, 1 },
				new int[][] { { 0, 1, 2 } }, new int[] { 5 /* VTK_TRIANGLE */ });
		e = Assertions.assertThrows(IllegalArgumentException.class,
				() -> MembraneArc.build(triangles, new double[][] { { 0, 0, 0 }, { 1, 0, 0 } }, 100));
		Assertions.assertTrue(e.getMessage().contains("3D membrane surface"), e.getMessage());
	}
}
