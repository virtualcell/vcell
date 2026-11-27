package org.vcell.client.viz;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.vcell.solver.fenics.BundleStore;
import org.vcell.solver.fenics.FenicsBundle;
import org.vcell.solver.fenics.FenicsSamples;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * A cluster run's bundle read through a data server that samples ({@link BundleStore#sampleRows}): kymographs
 * (volume, 2D membrane, 3D) and probes must give the SAME numbers as reading the bundle from disk -- bit for
 * bit, compared as their JSON -- while reading no row's chunk whole; a remeshed run samples once per segment;
 * a moving (ALE) run, and a data server that does not sample, read whole rows and still agree.
 */
@Tag("Fast")
@ResourceLock("fieldViewerServer")
public class FenicsSampledReadsTest {

	@TempDir
	File tmp;

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

	/** A data server, seen from the desktop: whole files, batches of files and (if {@code samples}) sampled reads. */
	private static final class RemoteLike implements BundleStore {
		final BundleStore disk;
		final boolean samples;
		final AtomicInteger rowsReadWhole = new AtomicInteger();
		final AtomicInteger sampleCalls = new AtomicInteger();

		RemoteLike(File dir, boolean samples) {
			this.disk = BundleStore.directory(dir);
			this.samples = samples;
		}

		private void count(String path) {
			if (path.matches(".*/\\d+(\\.0)+")) {
				rowsReadWhole.incrementAndGet(); // a row chunk crossing the wire
			}
		}

		@Override
		public byte[] read(String relativePath) throws IOException {
			count(relativePath);
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
			if (!samples) {
				return null;
			}
			sampleCalls.incrementAndGet();
			return FenicsBundle.gather(disk, arrayPaths, indices, rows, FenicsBundle.MAX_SAMPLE_VALUES);
		}

		@Override
		public String describe() {
			return "remote-like " + disk.describe();
		}
	}

	private static File resource(String name) throws Exception {
		return new File(FenicsSampledReadsTest.class.getResource(name + "/.zattrs").toURI()).getParentFile();
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

	/**
	 * Asks {@code route} of the bundle in {@code dir} from disk and through a remote-like store, asserts the two
	 * answers are identical, and returns the remote store for its counters.
	 */
	private RemoteLike assertSameAsDisk(File dir, boolean samples, String route, String query) throws Exception {
		RemoteLike remote = new RemoteLike(dir, samples);
		FieldViewerServer.registerBundle("8801", 0, dir, "disk");
		FieldViewerServer.registerBundle("8802", 0, BundleStore.cached(remote), "remote");
		JsonObject disk = JsonParser.parseString(get("8801", route, query)).getAsJsonObject();
		JsonObject sampled = JsonParser.parseString(get("8802", route, query)).getAsJsonObject();
		String key = disk.has("values") ? "values" : "series";
		Assertions.assertEquals(disk.get(key).toString(), sampled.get(key).toString(), route + query + ": the same numbers, bit for bit");
		return remote;
	}

	private static String at(double degrees, double r) {
		double a = Math.toRadians(degrees);
		return r * Math.cos(a) + "," + r * Math.sin(a);
	}

	@Test
	public void kymographsAndProbesAreSampledAndIdentical() throws Exception {
		Object[][] cases = {
				{ "membrane_efflux.fenics", "/kymograph", "&domain=cytosol_dom&var=u&path=" + enc("-0.7,0.013;0.7,0.013") },
				{ "membrane_efflux.fenics", "/timeseries", "&domain=cytosol_dom&var=u&points=" + enc("0.1,0.2;-0.3,0.05;0.9,0.9") },
				{ "membrane_efflux.fenics", "/timeseries", "&domain=cytosol_dom&var=u&x=0.1&y=0.2" },
				{ "receptor_3d.fenics", "/kymograph", "&domain=cyto_dom&var=s_cyto&samples=37&path=" + enc("-0.9,0.05,0.03;0.9,0.05,0.03") },
				{ "receptor_3d.fenics", "/kymograph", "&domain=ext_dom&var=s_ext&samples=37&path=" + enc("-0.9,0.05,0.03;0.9,0.05,0.03") },
				{ "receptor_3d.fenics", "/timeseries", "&domain=mem_dom&var=R&snap=nearest&points=" + enc("0.52,0.01,0.02") },
				{ "receptor_2d.fenics", "/kymograph", "&domain=mem_dom&var=R&path=" + enc(at(20, 0.5) + ";" + at(160, 0.5)) },
				{ "receptor_2d.fenics", "/kymograph", "&domain=mem_dom&var=R&tstep=2&path=" + enc(at(0, 0.5) + ";" + at(45, 0.5)) },
		};
		for (Object[] c : cases) {
			RemoteLike remote = assertSameAsDisk(resource((String) c[0]), true, (String) c[1], (String) c[2]);
			Assertions.assertEquals(1, remote.sampleCalls.get(), c[0] + " " + c[2] + ": one sampled call");
			Assertions.assertEquals(0, remote.rowsReadWhole.get(), c[0] + " " + c[2] + ": no row read whole");
		}
	}

	/** an older data server: whole rows (batched), the same numbers */
	@Test
	public void aDataServerThatDoesNotSampleGivesTheSameNumbers() throws Exception {
		RemoteLike remote = assertSameAsDisk(resource("membrane_efflux.fenics"), false, "/kymograph",
				"&domain=cytosol_dom&var=u&path=" + enc("-0.7,0.013;0.7,0.013"));
		Assertions.assertEquals(0, remote.sampleCalls.get());
		Assertions.assertTrue(remote.rowsReadWhole.get() > 0, "whole rows instead");
	}

	/** a moving mesh: the cell changes with the row, so whole rows are read; the numbers still agree */
	@Test
	public void aMovingMeshReadsWholeRows() throws Exception {
		for (String route : new String[] { "/kymograph", "/timeseries" }) {
			String q = route.equals("/kymograph") ? "&domain=cell&var=C_cyt&samples=81&path=" + enc("1,5.01;9,5.01")
					: "&domain=cell&var=C_cyt&points=" + enc("5,5.01;7.5,5");
			RemoteLike remote = assertSameAsDisk(resource("moving_translate.fenics"), true, route, q);
			Assertions.assertEquals(0, remote.sampleCalls.get(), route + ": no sampling on a moving mesh");
		}
	}

	/**
	 * A remeshed run (segments with a mesh each, not moving within one): one sampled call per segment. Made from
	 * vcell-core's {@code moving_remesh} fixture with its segments marked as not moving.
	 */
	@Test
	public void aRemeshedRunSamplesOncePerSegment() throws Exception {
		File src = new File("../vcell-core/src/test/resources/org/vcell/solver/fenics/bundles/moving_remesh.fenics");
		Assertions.assertTrue(new File(src, ".zattrs").isFile(), "fixture " + src.getAbsolutePath());
		Path dst = new File(tmp, "remeshed.fenics").toPath();
		try (Stream<Path> paths = Files.walk(src.toPath())) {
			for (Path p : (Iterable<Path>) paths::iterator) {
				Path to = dst.resolve(src.toPath().relativize(p).toString());
				if (Files.isDirectory(p)) {
					Files.createDirectories(to);
				} else {
					Files.copy(p, to);
				}
			}
		}
		Path zattrs = dst.resolve(".zattrs");
		String manifest = Files.readString(zattrs).replace("\"ale\"", "\"none\"");
		Files.writeString(zattrs, manifest);
		FenicsBundle bundle = FenicsBundle.open(dst.toFile());
		Assertions.assertEquals(2, bundle.getSegments().size());
		Assertions.assertFalse(bundle.isMoving());

		for (String[] c : new String[][] { { "/kymograph", "&domain=cell&var=C_cyt&samples=81&path=" + enc("1,5.01;9,5.01") },
				{ "/timeseries", "&domain=cell&var=RanC_cyt&points=" + enc("5,5.01;6,4") } }) {
			RemoteLike remote = assertSameAsDisk(dst.toFile(), true, c[0], c[1]);
			Assertions.assertEquals(2, remote.sampleCalls.get(), c[0] + ": one sampled call per segment");
			Assertions.assertEquals(0, remote.rowsReadWhole.get(), c[0] + ": no row read whole");
		}
	}
}
