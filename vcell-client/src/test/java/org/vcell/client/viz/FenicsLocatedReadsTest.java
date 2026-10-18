package org.vcell.client.viz;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.vcell.solver.fenics.BundleStore;
import org.vcell.solver.fenics.FenicsBundle;
import org.vcell.solver.fenics.FenicsLocatedSamples;
import org.vcell.solver.fenics.FenicsSamples;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import cbit.vcell.parser.Expression;

/**
 * A moving (ALE) run read through a data server that locates the points itself ({@link BundleStore#locateRows}):
 * kymographs, probes and functions must give the SAME numbers as reading the bundle from disk -- bit for bit,
 * compared as their JSON -- with one located call per moving segment and no variable row read whole; a data
 * server that samples but does not locate (8.2.0.15/16) reads whole rows and still agrees.
 */
@Tag("Fast")
@ResourceLock("fieldViewerServer")
public class FenicsLocatedReadsTest {

	private int port;

	@BeforeEach
	public void setup() {
		port = FieldViewerServer.start();
		Assertions.assertTrue(port > 0);
	}

	@AfterEach
	public void teardown() {
		FieldViewerServer.stop();
	}

	/** A data server seen from the desktop: whole files, sampled reads, and (if {@code locates}) located reads. */
	private static final class RemoteLike implements BundleStore {
		final BundleStore disk;
		final boolean locates;
		final AtomicInteger valueRowsReadWhole = new AtomicInteger();
		final AtomicInteger coordsRowsReadWhole = new AtomicInteger();
		final AtomicInteger locateCalls = new AtomicInteger();

		RemoteLike(File dir, boolean locates) {
			this.disk = BundleStore.directory(dir);
			this.locates = locates;
		}

		@Override
		public byte[] read(String relativePath) throws IOException {
			if (relativePath.matches(".*/_coords/\\d+(\\.0)+")) {
				coordsRowsReadWhole.incrementAndGet();
			} else if (relativePath.matches(".*/\\d+(\\.0)+") && !relativePath.startsWith("stats/") && !relativePath.contains("/stats/")) {
				valueRowsReadWhole.incrementAndGet();
			}
			return disk.read(relativePath);
		}

		@Override
		public byte[][] readAll(List<String> relativePaths) throws IOException {
			byte[][] out = new byte[relativePaths.size()][];
			for (int i = 0; i < out.length; i++) {
				out[i] = read(relativePaths.get(i));
			}
			return out;
		}

		@Override
		public FenicsSamples sampleRows(String[] arrayPaths, int[] rows, int[] indices) throws IOException {
			return FenicsBundle.gather(disk, arrayPaths, indices, rows, FenicsBundle.MAX_SAMPLE_VALUES);
		}

		@Override
		public FenicsLocatedSamples locateRows(String meshPath, String coordsPath, String[] arrayPaths, double[] points, boolean snap,
				int[] rows) throws IOException {
			if (!locates) {
				return null;
			}
			locateCalls.incrementAndGet();
			return FenicsBundle.locate(disk, meshPath, coordsPath, arrayPaths, points, snap, rows, FenicsBundle.MAX_SAMPLE_VALUES,
					FenicsBundle.MAX_LOCATE_WORK);
		}

		@Override
		public String describe() {
			return "remote-like " + disk.describe();
		}
	}

	private static File clientResource(String name) throws Exception {
		return new File(FenicsLocatedReadsTest.class.getResource(name + "/.zattrs").toURI()).getParentFile();
	}

	/** vcell-core's moving_remesh: two moving segments, each with its own mesh */
	private static File movingRemesh() {
		File dir = new File("../vcell-core/src/test/resources/org/vcell/solver/fenics/bundles/moving_remesh.fenics");
		Assertions.assertTrue(new File(dir, ".zattrs").isFile(), "fixture " + dir.getAbsolutePath());
		return dir;
	}

	private String get(String sim, String route, String query) throws Exception {
		HttpResponse<String> r = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + route + "?sim=" + sim + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return r.body();
	}

	private static String enc(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}

	private static final FenicsFunctions FUNCTIONS;

	static {
		try {
			FUNCTIONS = new FenicsFunctions(List.of(
					new FenicsFunctions.Definition("prod", new Expression("RanC_cyt*Ran_cyt + x"), "cell", false, null),
					new FenicsFunctions.Definition("where", new Expression("x*y - t"), "cell", false, null)), Set.of());
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	/** asks {@code route} from disk and through a remote-like store; asserts identical answers; returns the store */
	private RemoteLike assertSameAsDisk(File dir, boolean locates, String route, String query) throws Exception {
		RemoteLike remote = new RemoteLike(dir, locates);
		FieldViewerServer.registerBundle("8901", 0, BundleStore.directory(dir), "disk", FUNCTIONS);
		FieldViewerServer.registerBundle("8902", 0, BundleStore.cached(remote), "remote", FUNCTIONS);
		JsonObject disk = JsonParser.parseString(get("8901", route, query)).getAsJsonObject();
		JsonObject remoteAnswer = JsonParser.parseString(get("8902", route, query)).getAsJsonObject();
		String key = disk.has("values") ? "values" : "series";
		Assertions.assertEquals(disk.get(key).toString(), remoteAnswer.get(key).toString(), route + query + ": the same numbers, bit for bit");
		Assertions.assertTrue(disk.get(key).toString().matches(".*\\d.*"), route + query + ": some point lies in the mesh");
		return remote;
	}

	private static final String[][] CASES = {
			{ "/kymograph", "&domain=cell&var=C_cyt&samples=81&path=" + enc("1,5.01;9,5.01") },
			{ "/kymograph", "&domain=cell&var=RanC_cyt&tstep=2&samples=40&path=" + enc("2,3;8,7") },
			{ "/kymograph", "&domain=cell&var=prod&samples=81&path=" + enc("1,5.01;9,5.01") },
			{ "/timeseries", "&domain=cell&var=C_cyt&points=" + enc("5,5.01;7.5,5;0.2,0.2") },
			{ "/timeseries", "&domain=cell&var=Ran_cyt&x=5&y=5.01" },
			{ "/timeseries", "&domain=cell&var=where&points=" + enc("5,5.01;6,4") },
	};

	@Test
	public void aMovingMeshIsLocatedOnTheDataServerAndIdentical() throws Exception {
		for (String[] c : CASES) {
			RemoteLike remote = assertSameAsDisk(clientResource("moving_translate.fenics"), true, c[0], c[1]);
			Assertions.assertEquals(1, remote.locateCalls.get(), c[1] + ": one located call for the one moving segment");
			Assertions.assertEquals(0, remote.valueRowsReadWhole.get(), c[1] + ": no variable row read whole");
			Assertions.assertTrue(remote.coordsRowsReadWhole.get() <= 1, c[1] + ": at most the first row's positions read whole (its mesh)");
		}
	}

	@Test
	public void aMovingRemeshedRunIsLocatedOncePerSegment() throws Exception {
		for (String[] c : CASES) {
			RemoteLike remote = assertSameAsDisk(movingRemesh(), true, c[0], c[1]);
			// rows 0-9 and row 10 (strided: 0, 2, ... 8 and 10)
			Assertions.assertEquals(2, remote.locateCalls.get(), c[1] + ": one located call per moving segment");
			Assertions.assertEquals(0, remote.valueRowsReadWhole.get(), c[1] + ": no variable row read whole");
		}
	}

	/** a data server that samples but does not locate (8.2.0.15/16): whole rows, the same numbers */
	@Test
	public void aDataServerThatDoesNotLocateGivesTheSameNumbers() throws Exception {
		for (String[] c : CASES) {
			RemoteLike remote = assertSameAsDisk(clientResource("moving_translate.fenics"), false, c[0], c[1]);
			Assertions.assertEquals(0, remote.locateCalls.get());
			Assertions.assertTrue(remote.coordsRowsReadWhole.get() > 1, c[1] + ": whole rows' positions instead");
		}
	}
}
