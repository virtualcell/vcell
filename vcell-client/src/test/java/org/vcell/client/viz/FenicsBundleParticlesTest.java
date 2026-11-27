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
import org.vcell.solver.fenics.FenicsBundle;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * The particle layer of a hybrid PDE/particle run: a results bundle's {@code particles} extension
 * ({@link FenicsBundle}), {@code particleSpecies} in {@code /info} and the {@code /particles} endpoint.
 * The fixture is {@code receptor_3d.fenics} with two species added: A with 12, 8 and 4 molecules at the
 * three output times and B with 3 at each, all inside {@code cyto_dom}.
 */
@Tag("Fast")
@ResourceLock("fieldViewerServer")
public class FenicsBundleParticlesTest {

	private static final String SIM = "557";
	private static final String SIM_WITHOUT = "556";

	private int port;
	private File bundleDir;

	@BeforeEach
	public void setup() throws Exception {
		bundleDir = new File(getClass().getResource("receptor_3d_particles.fenics/.zattrs").toURI()).getParentFile();
		File plain = new File(getClass().getResource("receptor_3d.fenics/.zattrs").toURI()).getParentFile();
		FieldViewerServer.registerBundle(SIM, 0, bundleDir, "receptor 3d with particles");
		FieldViewerServer.registerBundle(SIM_WITHOUT, 0, plain, "receptor 3d");
		port = FieldViewerServer.startForFenics();
		Assertions.assertTrue(port > 0);
	}

	@AfterEach
	public void teardown() {
		FieldViewerServer.stop();
	}

	private HttpResponse<String> request(String path, String sim, String query) throws Exception {
		return HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=" + sim + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private JsonObject get(String path, String query) throws Exception {
		HttpResponse<String> r = request(path, SIM, query);
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return JsonParser.parseString(r.body()).getAsJsonObject();
	}

	@Test
	public void bundleReadsParticles() throws Exception {
		FenicsBundle bundle = FenicsBundle.open(bundleDir);
		Assertions.assertEquals(java.util.List.of("A", "B"), java.util.List.copyOf(bundle.getParticleSpecies().keySet()));
		Assertions.assertEquals(3 * 12, bundle.particles("A", 0).length);
		Assertions.assertEquals(3 * 4, bundle.particles("A", 2).length);
		for (double v : bundle.particles("A", 1)) {
			Assertions.assertTrue(Double.isFinite(v)); // no NaN padding past the count
		}
		Assertions.assertTrue(FenicsBundle.open(new File(bundleDir.getParentFile(), "receptor_3d.fenics"))
				.getParticleSpecies().isEmpty());
	}

	@Test
	public void infoListsParticleSpecies() throws Exception {
		JsonArray species = get("/info", "").getAsJsonArray("particleSpecies");
		Assertions.assertEquals(2, species.size());
		Assertions.assertEquals("A", species.get(0).getAsString());
		HttpResponse<String> plain = request("/info", SIM_WITHOUT, "");
		Assertions.assertFalse(JsonParser.parseString(plain.body()).getAsJsonObject().has("particleSpecies"));
	}

	@Test
	public void particlesAtATime() throws Exception {
		JsonObject p = get("/particles", "&time=0.1");
		Assertions.assertEquals(0.1, p.get("time").getAsDouble(), 1e-12);
		Assertions.assertEquals(1, p.get("timeIndex").getAsInt());
		JsonObject a = p.getAsJsonArray("species").get(0).getAsJsonObject();
		Assertions.assertEquals("A", a.get("name").getAsString());
		Assertions.assertEquals(8, a.get("count").getAsInt());
		Assertions.assertEquals(8, a.get("shown").getAsInt());
		Assertions.assertEquals(24, a.getAsJsonArray("points").size());
		// the last time when none is given
		Assertions.assertEquals(4, get("/particles", "").getAsJsonArray("species").get(0).getAsJsonObject().get("count").getAsInt());
	}

	@Test
	public void maxStridesThroughTheMolecules() throws Exception {
		JsonObject a = get("/particles", "&time=0&max=5").getAsJsonArray("species").get(0).getAsJsonObject();
		Assertions.assertEquals(12, a.get("count").getAsInt());
		Assertions.assertEquals(4, a.get("shown").getAsInt()); // stride 3 through 12
		Assertions.assertEquals(12, a.getAsJsonArray("points").size());
	}

	@Test
	public void runWithoutParticlesIsABadRequest() throws Exception {
		Assertions.assertEquals(400, request("/particles", SIM_WITHOUT, "").statusCode());
		Assertions.assertEquals(404, request("/particles", "999999", "").statusCode());
		Assertions.assertEquals(400, request("/particles", SIM, "&max=0").statusCode());
	}
}
