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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The field viewer's MovingBoundary endpoints end to end, over {@link FakeMovingBoundaryRun}: a disk moving
 * along x, served through the VTU seam with a different mesh at every saved time.
 */
@Tag("Fast")
// one static server per JVM: classes that start and stop it must not run concurrently
@ResourceLock("fieldViewerServer")
public class FieldViewerServerMovingBoundaryTest {

	private int port;

	@BeforeEach
	public void setup() {
		FakeMovingBoundaryRun.register();
		port = FieldViewerServer.start();
		Assertions.assertTrue(port > 0);
	}

	@AfterEach
	public void teardown() {
		FieldViewerServer.stop();
	}

	private JsonObject get(String path, String query) throws Exception {
		HttpResponse<String> r = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=" + FakeMovingBoundaryRun.SIM
						+ "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return JsonParser.parseString(r.body()).getAsJsonObject();
	}

	@Test
	public void theMeshMovesFromTimeToTime() throws Exception {
		JsonObject info = get("/info", "");
		Assertions.assertEquals("cell", info.getAsJsonArray("domains").get(0).getAsString());
		Assertions.assertEquals(5, info.getAsJsonArray("times").size());
		JsonObject first = get("/grid", "&domain=cell&time=0");
		JsonObject last = get("/grid", "&domain=cell&time=1");
		Assertions.assertTrue(first.get("bodyFitted").getAsBoolean());
		Assertions.assertEquals(2, first.get("dimension").getAsInt());
		Assertions.assertNotEquals(first.get("geometryId").getAsString(), last.get("geometryId").getAsString());
	}

	@Test
	public void severalLabPointsInOnePassWithGapsWhereTheBoundaryHasPassed() throws Exception {
		// (2.6, 5.1) is inside the disk only at t = 0; (8.1, 5.1) only from t = 0.25 on
		JsonObject multi = get("/timeseries", "&domain=cell&var=C&points=2.6,5.1;8.1,5.1");
		Assertions.assertEquals("cell", multi.get("location").getAsString());
		JsonArray series = multi.getAsJsonArray("series");
		JsonArray trailing = series.get(0).getAsJsonObject().getAsJsonArray("values");
		JsonArray leading = series.get(1).getAsJsonObject().getAsJsonArray("values");
		double[] times = FakeMovingBoundaryRun.TIMES;
		for (int i = 0; i < times.length; i++) {
			// each value is the containing lattice square's, at its centre
			if (i == 0) {
				Assertions.assertEquals(FakeMovingBoundaryRun.value(2.75, 5.25, 0), trailing.get(i).getAsDouble(), 1e-12);
				Assertions.assertTrue(leading.get(i).isJsonNull(), "not yet reached by the disk");
			} else {
				Assertions.assertTrue(trailing.get(i).isJsonNull(), "the boundary has moved past it");
				Assertions.assertEquals(FakeMovingBoundaryRun.value(8.25, 5.25, times[i]), leading.get(i).getAsDouble(), 1e-12);
			}
		}
		for (int s = 0; s < 2; s++) {
			JsonObject se = series.get(s).getAsJsonObject();
			Assertions.assertFalse(se.has("cell"), "a moving mesh gives a point no single cell");
			Assertions.assertTrue(se.get("inDomain").getAsBoolean());
		}
		// the same as the single-point form, one point at a time
		Assertions.assertEquals(get("/timeseries", "&domain=cell&var=C&x=2.6&y=5.1").getAsJsonArray("values"), trailing);
	}

	private HttpResponse<String> send(String path, String query) throws Exception {
		return HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=" + FakeMovingBoundaryRun.SIM
						+ "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
	}

	/** Several points read every saved time over the remote seam: a heavy job, one at a time. */
	@Test
	public void severalPointsWaitForAHeavyJobAlreadyRunning() throws Exception {
		Assertions.assertTrue(FieldViewerServer.HEAVY_JOBS.tryAcquire());
		try {
			HttpResponse<String> busy = send("/timeseries", "&domain=cell&var=C&points=2.6,5.1%3B8.1,5.1");
			Assertions.assertEquals(503, busy.statusCode(), busy.body());
			Assertions.assertTrue(JsonParser.parseString(busy.body()).getAsJsonObject().get("busy").getAsBoolean());
		} finally {
			FieldViewerServer.HEAVY_JOBS.release();
		}
		Assertions.assertEquals(200, send("/timeseries", "&domain=cell&var=C&points=2.6,5.1%3B8.1,5.1").statusCode());
	}

	/**
	 * The kymograph is a fixed lab-frame (Eulerian) line: each sample is located in every time's mesh, reads the
	 * cell holding it then, and is a gap where the moving disk is not. The disk (radius 3, centre x = 5 + 4t) moves
	 * 4 µm along +x, so the gaps' edges do too.
	 */
	@Test
	public void aKymographIsAFixedLabFrameLineThroughEachTimesMesh() throws Exception {
		String query = "&domain=cell&var=C";
		// along y = 5.1 (off the lattice lines), samples every 0.2 µm from x = 0.1 to 15.9
		JsonObject k = get("/kymograph", query + "&samples=80&path=0.1,5.1%3B15.9,5.1");
		Assertions.assertTrue(k.get("movingMesh").getAsBoolean(), "the mesh moves: the line is labelled lab frame");
		Assertions.assertEquals("cell", k.get("location").getAsString());
		Assertions.assertEquals("uniform", k.get("sampling").getAsString());
		JsonArray points = k.getAsJsonObject("samples").getAsJsonArray("points");
		JsonArray rows = k.getAsJsonArray("values");
		double[] times = FakeMovingBoundaryRun.TIMES;
		Assertions.assertEquals(times.length, rows.size());
		double h = FakeMovingBoundaryRun.H;
		double[] firstX = new double[times.length];
		double[] lastX = new double[times.length];
		for (int r = 0; r < times.length; r++) {
			firstX[r] = Double.NaN;
			JsonArray row = rows.get(r).getAsJsonArray();
			for (int i = 0; i < row.size(); i++) {
				double x = points.get(3 * i).getAsDouble(), y = points.get(3 * i + 1).getAsDouble();
				// the lattice square holding the sample, inside the disk at this time or not
				double cx = Math.floor(x / h) * h + h / 2, cy = Math.floor(y / h) * h + h / 2;
				double dx = cx - FakeMovingBoundaryRun.centreX(times[r]), dy = cy - 5;
				boolean inside = dx * dx + dy * dy < FakeMovingBoundaryRun.RADIUS * FakeMovingBoundaryRun.RADIUS;
				if (inside) {
					Assertions.assertEquals(FakeMovingBoundaryRun.value(cx, cy, times[r]), row.get(i).getAsDouble(), 1e-12,
							"sample " + i + " at t = " + times[r]);
					if (Double.isNaN(firstX[r])) {
						firstX[r] = x;
					}
					lastX[r] = x;
				} else {
					Assertions.assertTrue(row.get(i).isJsonNull(), "sample " + i + " is outside the disk at t = " + times[r]);
				}
			}
		}
		// the gaps' edges move with the boundary, 4 µm over the run (to within a sample and a lattice square)
		Assertions.assertEquals(4, firstX[times.length - 1] - firstX[0], 0.8);
		Assertions.assertEquals(4, lastX[times.length - 1] - lastX[0], 0.8);
		// and each sample's column is the time series at its point
		StringBuilder list = new StringBuilder();
		for (int i = 0; i < 40; i++) {
			list.append(i > 0 ? "%3B" : "").append(points.get(3 * i).getAsDouble()).append(',').append(points.get(3 * i + 1).getAsDouble());
		}
		JsonArray series = get("/timeseries", query + "&points=" + list).getAsJsonArray("series");
		for (int i = 0; i < 40; i++) {
			JsonArray values = series.get(i).getAsJsonObject().getAsJsonArray("values");
			for (int r = 0; r < times.length; r++) {
				Assertions.assertEquals(values.get(r), rows.get(r).getAsJsonArray().get(i), "sample " + i + " row " + r);
			}
		}
	}

	@Test
	public void aKymographIsAHeavyJobWithinTheValueLimit() throws Exception {
		Assertions.assertTrue(FieldViewerServer.HEAVY_JOBS.tryAcquire());
		try {
			HttpResponse<String> busy = send("/kymograph", "&domain=cell&var=C&path=2,5%3B8,5");
			Assertions.assertEquals(503, busy.statusCode(), busy.body());
		} finally {
			FieldViewerServer.HEAVY_JOBS.release();
		}
		// 50 samples × 5 times over a limit of 100: every 3rd saved time (0 and 3) fits
		System.setProperty("vcell.fieldViewer.maxKymographValues", "100");
		try {
			HttpResponse<String> r = send("/kymograph", "&domain=cell&var=C&samples=50&path=2,5%3B8,5");
			Assertions.assertEquals(400, r.statusCode(), r.body());
			Assertions.assertEquals(3, JsonParser.parseString(r.body()).getAsJsonObject().get("suggestedTstep").getAsInt());
			Assertions.assertEquals("[0,3]", get("/kymograph", "&domain=cell&var=C&samples=50&tstep=3&path=2,5%3B8,5")
					.getAsJsonArray("timeIndices").toString());
		} finally {
			System.clearProperty("vcell.fieldViewer.maxKymographValues");
		}
		Assertions.assertEquals("MovingBoundary", get("/info", "").get("solver").getAsString());
	}
}
