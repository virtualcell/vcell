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

import cbit.vcell.client.ClientSimManager;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.LocalDataSetController;
import cbit.vcell.simdata.VCDataManager;
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
import java.util.List;
import java.util.stream.Stream;

/**
 * The field viewer over a real, <b>locally run</b> MovingBoundary result, read the way the desktop reads a quick
 * run: a {@link DataSetControllerImpl} behind a {@link LocalDataSetController}, addressed by a
 * {@link ClientSimManager.LocalVCSimulationDataIdentifier}. Unlike {@link FieldViewerServerMovingBoundaryTest}, whose
 * {@link FakeMovingBoundaryRun} stubs the VTU seam, every mesh here is built by the real path —
 * {@code MovingBoundaryVtkFileWriter} → the pure-Java VTU writer — with no server data directory
 * ({@code vcell.primarySimdatadir.internal}) and no Python VTK service ({@code vcell.vtk.pythonDir}) configured.
 * <p>
 * The fixture ({@code mb/}) is {@code biomodel_165181964.vcml}'s Simulation0 (a disc of radius 1 in a 3 × 3 box,
 * its boundary pushed outward at speed {@code C}, {@code C(0) = 1}) run by {@code MovingBoundary_x64} 1.0.5 on a
 * coarsened 12 × 12 grid to t = 0.2 ({@code SimID_1737498523_0_mb.xml} is the input), its HDF5 output repacked
 * with gzip.
 */
@Tag("Fast")
// one static server per JVM: classes that start and stop it must not run concurrently
@ResourceLock("fieldViewerServer")
public class FieldViewerServerMovingBoundaryLocalRunTest {

	static final String SIM = "1737498523";
	static final String DOMAIN = "fakeInsideDomain";
	private static final String[] FILES = { "SimID_1737498523_0_.h5", "SimID_1737498523_0_.log", "SimID_1737498523_0_.functions" };

	private Path root;
	private Path localSimDir;
	private int port;

	@BeforeEach
	public void setup() throws Exception {
		Assertions.assertNull(System.getProperty("vcell.primarySimdatadir.internal"), "a desktop has no server data directory");
		root = Files.createTempDirectory("FieldViewerServerMovingBoundaryLocalRunTest_");
		// a quick run's files sit in the local root's temp-user directory, as ResourceUtil.getLocalSimDir lays them out
		localSimDir = Files.createDirectories(root.resolve(User.tempUser.getName()));
		for (String name : FILES) {
			try (InputStream in = getClass().getResourceAsStream("mb/" + name)) {
				Assertions.assertNotNull(in, name);
				Files.copy(in, localSimDir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
			}
		}
		DataSetControllerImpl controller = new DataSetControllerImpl(
				new Cachetable(10 * Cachetable.minute, 100_000_000L), root.toFile(), null);
		LocalDataSetController local = new LocalDataSetController(null, controller, null, User.tempUser);
		VCDataManager dataManager = new VCDataManager(() -> local);
		ClientSimManager.LocalVCSimulationDataIdentifier vcdID = new ClientSimManager.LocalVCSimulationDataIdentifier(
				new VCSimulationIdentifier(new KeyValue(SIM), User.tempUser), 0, localSimDir.toFile());
		FieldViewerServer.register(vcdID, dataManager, (org.vcell.vis.vcell.SubdomainInfo) null, "mb::local expanding disc");
		port = FieldViewerServer.start();
		Assertions.assertTrue(port > 0);
	}

	@AfterEach
	public void teardown() throws Exception {
		FieldViewerServer.stop();
		try (Stream<Path> walk = Files.walk(root)) {
			walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
	}

	private JsonObject get(String path, String query) throws Exception {
		HttpResponse<String> r = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=" + SIM + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return JsonParser.parseString(r.body()).getAsJsonObject();
	}

	@Test
	public void aLocalRunsMovingMeshIsBuiltInJavaAndServed() throws Exception {
		JsonObject info = get("/info", "");
		Assertions.assertEquals("MovingBoundary", info.get("solver").getAsString());
		Assertions.assertEquals(DOMAIN, info.getAsJsonArray("domains").get(0).getAsString());
		JsonArray times = info.getAsJsonArray("times");
		Assertions.assertEquals(3, times.size());

		JsonObject first = get("/grid", "&domain=" + DOMAIN + "&time=0");
		JsonObject last = get("/grid", "&domain=" + DOMAIN + "&time=" + times.get(2).getAsString());
		Assertions.assertTrue(first.get("bodyFitted").getAsBoolean());
		Assertions.assertEquals(2, first.get("dimension").getAsInt());
		Assertions.assertNotEquals(first.get("geometryId").getAsString(), last.get("geometryId").getAsString(),
				"the disc grows: each saved time has its own mesh");

		// the meshes and their indices were written beside the run, and nothing was handed to Python
		try (Stream<Path> files = Files.list(localSimDir)) {
			List<String> names = files.map(p -> p.getFileName().toString()).toList();
			Assertions.assertTrue(names.contains("SimID_" + SIM + "_0_" + DOMAIN + "_000000.vtu"), names.toString());
			Assertions.assertTrue(names.contains("SimID_" + SIM + "_0_" + DOMAIN + "_000002.movingboundaryindex"), names.toString());
			Assertions.assertTrue(names.stream().noneMatch(n -> n.endsWith(".visMesh")), names.toString());
		}

		// the centre of the disc is always inside it: C starts at 1 and is diluted as the disc grows
		JsonArray values = get("/timeseries", "&domain=" + DOMAIN + "&var=C&x=0.01&y=0.01").getAsJsonArray("values");
		Assertions.assertEquals(3, values.size());
		Assertions.assertEquals(1.0, values.get(0).getAsDouble(), 1e-9);
		for (int i = 1; i < values.size(); i++) {
			double c = values.get(i).getAsDouble();
			Assertions.assertTrue(c > 0 && c < values.get(i - 1).getAsDouble(), "C falls as the domain grows: " + values);
		}
		// a corner of the box is outside the disc at every time
		JsonArray corner = get("/timeseries", "&domain=" + DOMAIN + "&var=C&x=-1.45&y=-1.45").getAsJsonArray("values");
		for (int i = 0; i < corner.size(); i++) {
			Assertions.assertTrue(corner.get(i).isJsonNull(), "the corner is never inside: " + corner);
		}

		// a kymograph across the disc reads every time's mesh
		JsonObject k = get("/kymograph", "&domain=" + DOMAIN + "&var=C&samples=30&path=-1.4,0.01%3B1.4,0.01");
		Assertions.assertTrue(k.get("movingMesh").getAsBoolean());
		Assertions.assertEquals(3, k.getAsJsonArray("values").size());
	}

	/**
	 * What the viewer sends together on a variable switch: the probes' series and the kymograph, both heavy jobs.
	 * The second waits for the first instead of failing with "busy".
	 */
	@Test
	public void aProbeSeriesAndAKymographRequestedTogetherBothSucceed() throws Exception {
		get("/info", "");
		java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(2);
		try {
			for (int round = 0; round < 3; round++) {
				java.util.concurrent.Future<HttpResponse<String>> kymograph = pool.submit(() -> send("/kymograph",
						"&domain=" + DOMAIN + "&var=C&samples=30&path=-1.4,0.01%3B1.4,0.01"));
				java.util.concurrent.Future<HttpResponse<String>> probes = pool.submit(() -> send("/timeseries",
						"&domain=" + DOMAIN + "&var=C&points=0.01,0.01%3B0.5,0.2&snap=nearest"));
				Assertions.assertEquals(200, kymograph.get().statusCode(), kymograph.get().body());
				Assertions.assertEquals(200, probes.get().statusCode(), probes.get().body());
			}
		} finally {
			pool.shutdownNow();
		}
	}

	private HttpResponse<String> send(String path, String query) throws Exception {
		return HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path + "?sim=" + SIM + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
	}
}
