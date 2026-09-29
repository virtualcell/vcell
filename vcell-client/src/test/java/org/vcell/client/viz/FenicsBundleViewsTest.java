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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The field viewer's endpoints over a real FEniCSx results bundle (a 2D disk, one variable {@code u}
 * on {@code cytosol_dom}, three output times), through the running loopback server.
 */
@Tag("Fast")
// one static server per JVM: classes that start and stop it must not run concurrently
@ResourceLock("fieldViewerServer")
public class FenicsBundleViewsTest {

	private static final String SIM = "987654321";

	private int port;

	@BeforeEach
	public void setup() throws Exception {
		File bundle = new File(FenicsBundleViewsTest.class.getResource("membrane_efflux.fenics/.zattrs").toURI()).getParentFile();
		FieldViewerServer.registerBundle(SIM, 0, bundle, "disk::efflux");
		port = FieldViewerServer.startForFenics();
		Assertions.assertTrue(port > 0);
	}

	@AfterEach
	public void teardown() {
		FieldViewerServer.stop();
	}

	private JsonObject get(String path, String query) throws Exception {
		HttpResponse<String> r = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=" + SIM + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return JsonParser.parseString(r.body()).getAsJsonObject();
	}

	@Test
	public void info() throws Exception {
		JsonObject info = get("/info", "");
		Assertions.assertEquals(SIM, info.get("simId").getAsString());
		Assertions.assertEquals("disk::efflux", info.get("simName").getAsString());
		Assertions.assertEquals("completed", info.get("status").getAsString());
		Assertions.assertEquals(1.0, info.get("progress").getAsDouble(), 1e-12); // the viewer's live readout
		Assertions.assertEquals(3, info.getAsJsonArray("times").size());
		JsonObject u = info.getAsJsonArray("variables").get(0).getAsJsonObject();
		Assertions.assertEquals("u", u.get("name").getAsString());
		Assertions.assertEquals("cytosol_dom", u.get("domain").getAsString());
		Assertions.assertEquals("point", u.get("location").getAsString());
	}

	@Test
	public void gridAndFieldPairUp() throws Exception {
		JsonObject grid = get("/grid", "&domain=cytosol_dom&time=0.1");
		Assertions.assertEquals(2, grid.get("dimension").getAsInt());
		Assertions.assertTrue(grid.get("bodyFitted").getAsBoolean());
		Assertions.assertEquals(5, grid.get("cellType").getAsInt());
		Assertions.assertEquals(403, grid.get("numPoints").getAsInt());
		Assertions.assertEquals(3 * 403, grid.getAsJsonArray("points").size());
		Assertions.assertEquals(711, grid.getAsJsonArray("cells").size());

		JsonObject field = get("/field", "&domain=cytosol_dom&var=u&time=0.2");
		Assertions.assertEquals(grid.get("geometryId").getAsString(), field.get("geometryId").getAsString(),
				"a fixed-mesh bundle has one geometry for every time");
		Assertions.assertEquals("point", field.get("location").getAsString());
		Assertions.assertEquals(0.2, field.get("time").getAsDouble(), 0.0);
		JsonArray values = field.getAsJsonArray("values");
		Assertions.assertEquals(403, values.size());
		Assertions.assertEquals(0.9985452303304784, values.get(0).getAsDouble(), 0.0);
		Assertions.assertEquals(0.9998503034149014, values.get(402).getAsDouble(), 0.0);
	}

	@Test
	public void statsAreTheSolversIntegrals() throws Exception {
		JsonObject stats = get("/stats", "");
		Assertions.assertEquals("integral", stats.get("weighting").getAsString());
		JsonObject u = stats.getAsJsonArray("series").get(0).getAsJsonObject();
		Assertions.assertEquals(0.9993355966701419, u.getAsJsonArray("mean").get(2).getAsDouble(), 0.0);
		Assertions.assertEquals(0.7830285791419164, u.getAsJsonArray("total").get(2).getAsDouble(), 0.0);
		Assertions.assertEquals(0.9985433178755359, u.getAsJsonArray("min").get(2).getAsDouble(), 0.0);
		Assertions.assertEquals(0.9998755773268853, u.getAsJsonArray("max").get(2).getAsDouble(), 0.0);
		// the mesh's area agrees with the solver's: total / mean
		double measure = u.getAsJsonArray("measure").get(2).getAsDouble();
		Assertions.assertEquals(0.7830285791419164 / 0.9993355966701419, measure, 1e-12 * measure);
	}

	@Test
	public void timeSeriesInterpolatesAtALabPoint() throws Exception {
		// at a triangle's centroid the P1 interpolant is the mean of its three vertex values
		JsonObject grid = get("/grid", "&domain=cytosol_dom");
		JsonArray points = grid.getAsJsonArray("points");
		JsonArray triangle = grid.getAsJsonArray("cells").get(100).getAsJsonArray();
		JsonArray u = get("/field", "&domain=cytosol_dom&var=u&time=0.2").getAsJsonArray("values");
		double x = 0, y = 0, expected = 0;
		for (int k = 0; k < 3; k++) {
			int v = triangle.get(k).getAsInt();
			x += points.get(3 * v).getAsDouble() / 3;
			y += points.get(3 * v + 1).getAsDouble() / 3;
			expected += u.get(v).getAsDouble() / 3;
		}
		JsonObject series = get("/timeseries", "&domain=cytosol_dom&var=u&x=" + x + "&y=" + y);
		Assertions.assertEquals(3, series.get("insideCount").getAsInt());
		Assertions.assertEquals(expected, series.getAsJsonArray("values").get(2).getAsDouble(), 1e-12);

		// outside the disk: no values
		JsonObject outside = get("/timeseries", "&domain=cytosol_dom&var=u&x=100&y=100");
		Assertions.assertEquals(0, outside.get("insideCount").getAsInt());
		Assertions.assertTrue(outside.getAsJsonArray("values").get(0).isJsonNull());
	}

	private String body(String sim, String path, String query) throws Exception {
		HttpResponse<String> r = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=" + sim + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return r.body();
	}

	/** The single-point responses, recorded before multi-point {@code /timeseries} existed: they must not change. */
	@Test
	public void legacySinglePointResponsesAreUnchanged() throws Exception {
		File moving = new File(FenicsBundleViewsTest.class.getResource("moving_translate.fenics/.zattrs").toURI()).getParentFile();
		FieldViewerServer.registerBundle("777", 0, moving, "moving");
		Assertions.assertEquals("{\"name\":\"u\",\"domain\":\"cytosol_dom\",\"x\":0.1,\"y\":0.2,\"z\":0.0,\"insideCount\":3,"
				+ "\"times\":[0.0,0.1,0.2],\"values\":[1.0,0.9999085035980163,0.9997258219674996]}",
				body(SIM, "/timeseries", "&domain=cytosol_dom&var=u&x=0.1&y=0.2"));
		Assertions.assertEquals("{\"name\":\"u\",\"domain\":\"cytosol_dom\",\"x\":100.0,\"y\":100.0,\"z\":0.0,\"insideCount\":0,"
				+ "\"times\":[0.0,0.1,0.2],\"values\":[null,null,null]}",
				body(SIM, "/timeseries", "&domain=cytosol_dom&var=u&x=100&y=100"));
		Assertions.assertEquals("{\"name\":\"u\",\"domain\":\"cytosol_dom\",\"x\":0.3,\"y\":-0.1,\"z\":0.0,\"insideCount\":3,"
				+ "\"times\":[0.0,0.1,0.2],\"values\":[0.999999999999999,0.9998088821182801,0.9995221814992524]}",
				body(SIM, "/timeseries", "&var=u&x=0.3&y=-0.1&z=0"));
		Assertions.assertEquals("{\"name\":\"C_cyt\",\"domain\":\"cell\",\"x\":2.2,\"y\":5.0,\"z\":0.0,\"insideCount\":6,"
				+ "\"times\":[0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0],"
				+ "\"values\":[2.1999999999999815,3.1693396749240588,3.801383512936291,4.300003942076041,4.721658207981124,"
				+ "5.0916313355537755,null,null,null,null,null]}",
				body("777", "/timeseries", "&domain=cell&var=C_cyt&x=2.2&y=5.0"));
	}

	@Test
	public void severalPointsInOnePass() throws Exception {
		JsonObject grid = get("/grid", "&domain=cytosol_dom");
		JsonArray points = grid.getAsJsonArray("points");
		JsonArray u = get("/field", "&domain=cytosol_dom&var=u&time=0.2").getAsJsonArray("values");
		int[] triangles = { 100, 400 };
		double[][] centroids = new double[2][3];
		double[] expected = new double[2];
		for (int t = 0; t < 2; t++) {
			JsonArray triangle = grid.getAsJsonArray("cells").get(triangles[t]).getAsJsonArray();
			for (int k = 0; k < 3; k++) {
				int v = triangle.get(k).getAsInt();
				centroids[t][0] += points.get(3 * v).getAsDouble() / 3;
				centroids[t][1] += points.get(3 * v + 1).getAsDouble() / 3;
				expected[t] += u.get(v).getAsDouble() / 3;
			}
		}
		// 2D: z may be left out, and is on the second point
		String query = "&domain=cytosol_dom&var=u&points=" + centroids[0][0] + "," + centroids[0][1] + ",0;"
				+ centroids[1][0] + "," + centroids[1][1] + ";100,100,0";
		JsonObject multi = get("/timeseries", query);
		Assertions.assertEquals("point", multi.get("location").getAsString());
		Assertions.assertEquals(3, multi.getAsJsonArray("times").size());
		JsonArray series = multi.getAsJsonArray("series");
		Assertions.assertEquals(3, series.size());
		for (int t = 0; t < 2; t++) {
			JsonObject s = series.get(t).getAsJsonObject();
			Assertions.assertTrue(s.get("inDomain").getAsBoolean());
			Assertions.assertEquals(triangles[t], s.get("cell").getAsInt(), "a fixed mesh: one cell per point");
			Assertions.assertEquals(expected[t], s.getAsJsonArray("values").get(2).getAsDouble(), 1e-12);
			JsonObject single = get("/timeseries", "&domain=cytosol_dom&var=u&x=" + centroids[t][0] + "&y=" + centroids[t][1]);
			Assertions.assertEquals(single.getAsJsonArray("values"), s.getAsJsonArray("values"), "same as one point at a time");
		}
		JsonObject outside = series.get(2).getAsJsonObject();
		Assertions.assertFalse(outside.get("inDomain").getAsBoolean());
		Assertions.assertEquals(-1, outside.get("cell").getAsInt());
		for (com.google.gson.JsonElement v : outside.getAsJsonArray("values")) {
			Assertions.assertTrue(v.isJsonNull());
		}
		Assertions.assertFalse(series.get(0).getAsJsonObject().has("volumeIndex"), "volume indices are finite-volume only");
	}

	@Test
	public void severalPointsOnAMovingMesh() throws Exception {
		File moving = new File(FenicsBundleViewsTest.class.getResource("moving_translate.fenics/.zattrs").toURI()).getParentFile();
		FieldViewerServer.registerBundle("777", 0, moving, "moving");
		JsonObject multi = get777("/timeseries", "&domain=cell&var=C_cyt&points=2.2,5.0;5,5");
		JsonArray series = multi.getAsJsonArray("series");
		JsonObject trailing = series.get(0).getAsJsonObject();
		Assertions.assertEquals(get777("/timeseries", "&domain=cell&var=C_cyt&x=2.2&y=5.0").getAsJsonArray("values"),
				trailing.getAsJsonArray("values"));
		Assertions.assertFalse(trailing.has("cell"), "the mesh moves, so a point has no single cell");
		Assertions.assertTrue(trailing.get("inDomain").getAsBoolean(), "inside at some of the times");
		JsonArray centre = series.get(1).getAsJsonObject().getAsJsonArray("values");
		for (com.google.gson.JsonElement v : centre) {
			Assertions.assertFalse(v.isJsonNull(), "the disk's centre stays inside it");
		}
	}

	@Test
	public void tooManyPointsIsABadRequest() throws Exception {
		StringBuilder many = new StringBuilder("&var=u&points=");
		for (int i = 0; i <= PointSeries.MAX_POINTS; i++) {
			many.append(i > 0 ? ";" : "").append("0,0");
		}
		HttpResponse<String> r = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/timeseries?sim=" + SIM + "&job=0" + many)).build(),
				HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals(400, r.statusCode(), r.body());
		Assertions.assertTrue(r.body().contains("at most 64 points"), r.body());
	}

	@Test
	public void aMovingMeshServesEachRowsGeometry() throws Exception {
		File bundle = new File(FenicsBundleViewsTest.class.getResource("moving_translate.fenics/.zattrs").toURI()).getParentFile();
		FieldViewerServer.registerBundle("777", 0, bundle, "moving");
		JsonObject first = get777("/grid", "&domain=cell&time=0");
		JsonObject last = get777("/grid", "&domain=cell&time=1");
		Assertions.assertEquals("777/cell@t0", first.get("geometryId").getAsString());
		Assertions.assertEquals("777/cell@t10", last.get("geometryId").getAsString());
		// the same topology, moved points: a rigid shift of the whole mesh
		Assertions.assertEquals(first.getAsJsonArray("cells"), last.getAsJsonArray("cells"));
		double dx = last.getAsJsonArray("points").get(0).getAsDouble() - first.getAsJsonArray("points").get(0).getAsDouble();
		Assertions.assertEquals(6.501388098098372 - 6.0, dx, 1e-12);
		// a field names the geometry of its own time, so the viewer re-fetches the moved mesh
		Assertions.assertEquals("777/cell@t10", get777("/field", "&domain=cell&var=C_cyt&time=1").get("geometryId").getAsString());

		// a lab point just inside the disk's trailing (left) edge at t = 0 is behind the front by t = 1
		JsonObject series = get777("/timeseries", "&domain=cell&var=C_cyt&x=2.2&y=5.0");
		JsonArray values = series.getAsJsonArray("values");
		Assertions.assertFalse(values.get(0).isJsonNull(), "inside the cell at t = 0");
		Assertions.assertTrue(values.get(values.size() - 1).isJsonNull(), "the front has moved past it");
		Assertions.assertTrue(series.get("insideCount").getAsInt() < values.size());

		// the measure is each row's own mesh's (a rigid motion keeps it), and agrees with the solver's total / mean
		JsonObject c = get777("/stats", "&var=C_cyt").getAsJsonArray("series").get(0).getAsJsonObject();
		double measure = c.getAsJsonArray("measure").get(10).getAsDouble();
		Assertions.assertEquals(141.15024535730737 / 5.000005025056392, measure, 1e-9 * measure);
	}

	private JsonObject get777(String path, String query) throws Exception {
		HttpResponse<String> r = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=777&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return JsonParser.parseString(r.body()).getAsJsonObject();
	}

	@Test
	public void unknownVariableIsABadRequest() throws Exception {
		HttpResponse<String> r = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/field?sim=" + SIM + "&job=0&var=nope")).build(),
				HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals(400, r.statusCode(), r.body());
	}
}
