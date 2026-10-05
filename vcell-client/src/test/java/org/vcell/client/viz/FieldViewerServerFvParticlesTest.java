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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * The particle layer of a finite-volume hybrid run: vcell-fvsolver writes each saved time's molecule positions
 * ({@code SimID_<key>_<job>__<NNN>.smoldynOutput}) when the simulation's "save particle files" option is on, and
 * the field viewer serves them through the data manager ({@code getParticleDataExists}, {@code
 * getParticleDataBlock}) in {@code /info} ({@code particleSpecies}) and {@code /particles}.
 */
@Tag("Fast")
@ResourceLock("fieldViewerServer")
public class FieldViewerServerFvParticlesTest {

	private Path root;
	private int port;

	@BeforeEach
	public void setup() throws Exception {
		root = Files.createTempDirectory("FieldViewerServerFvParticlesTest_");
		port = FieldViewerServerFvTest.registerFvFixtures(root);
		Assertions.assertTrue(port > 0);
	}

	@AfterEach
	public void teardown() throws Exception {
		FieldViewerServer.stop();
		try (Stream<Path> walk = Files.walk(root)) {
			walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}

	private HttpResponse<String> send(String sim, String path, String query) throws Exception {
		return HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=" + sim + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private JsonObject get(String path, String query) throws Exception {
		HttpResponse<String> r = send(FieldViewerServerFvTest.SIM_HYBRID, path, query);
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return JsonParser.parseString(r.body()).getAsJsonObject();
	}

	@Test
	public void infoListsTheParticleSpecies() throws Exception {
		JsonObject info = get("/info", "");
		JsonArray species = info.getAsJsonArray("particleSpecies");
		Assertions.assertEquals(1, species.size());
		Assertions.assertEquals("A", species.get(0).getAsString()); // "A(solution)" in the file
		Assertions.assertEquals(5, info.getAsJsonArray("times").size());
		// a run saved without particle files lists none
		JsonObject plain = JsonParser.parseString(send(FieldViewerServerFvTest.SIM_3D, "/info", "").body()).getAsJsonObject();
		Assertions.assertFalse(plain.has("particleSpecies"));
	}

	@Test
	public void particlesFollowTheSavedTimes() throws Exception {
		JsonArray times = get("/info", "").getAsJsonArray("times");
		for (int i = 0; i < times.size(); i++) {
			JsonObject p = get("/particles", "&time=" + times.get(i).getAsDouble());
			Assertions.assertEquals(i, p.get("timeIndex").getAsInt());
			JsonObject a = p.getAsJsonArray("species").get(0).getAsJsonObject();
			Assertions.assertEquals("A", a.get("name").getAsString());
			int expected = FieldViewerServerFvTest.HYBRID_PARTICLE_COUNTS[i];
			Assertions.assertEquals(expected, a.get("count").getAsInt());
			JsonArray points = a.getAsJsonArray("points");
			Assertions.assertEquals(3 * expected, points.size());
			for (int k = 0; k < points.size(); k += 3) { // inside the 2 x 2 x 1 um box
				Assertions.assertTrue(points.get(k).getAsDouble() >= 0 && points.get(k).getAsDouble() <= 2);
				Assertions.assertTrue(points.get(k + 2).getAsDouble() >= 0 && points.get(k + 2).getAsDouble() <= 1);
			}
		}
		JsonObject capped = get("/particles", "&time=0&max=50").getAsJsonArray("species").get(0).getAsJsonObject();
		Assertions.assertEquals(196, capped.get("count").getAsInt());
		Assertions.assertEquals(49, capped.get("shown").getAsInt()); // stride 4 through 196
	}

	@Test
	public void runsWithoutParticleFilesAreBadRequests() throws Exception {
		Assertions.assertEquals(400, send(FieldViewerServerFvTest.SIM_3D, "/particles", "").statusCode());
		Assertions.assertEquals(404, send("424242", "/particles", "").statusCode());
	}
}
