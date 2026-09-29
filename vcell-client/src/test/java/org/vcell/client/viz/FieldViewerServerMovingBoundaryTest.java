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
}
