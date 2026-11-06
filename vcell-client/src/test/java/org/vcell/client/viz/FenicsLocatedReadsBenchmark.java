package org.vcell.client.viz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.vcell.solver.fenics.BundleStore;
import org.vcell.solver.fenics.FenicsBundle;
import org.vcell.solver.fenics.MeasuredDataServer;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Not a CI test: measures RPCs, reply bytes and wall time of moving-mesh (ALE) kymographs and probes through a real
 * {@link DataSetControllerImpl} on a real bundle, per data-server release, and checks every release answers the
 * same numbers as the disk. Run with
 * {@code -Dvcell.fenics.aleBenchmark=<bundle dir>|<domain>|<var>|<kymograph path>|<probe points>[@@…]}
 * and optionally {@code -Dvcell.fenics.aleBenchmarkLatencyMs=70}.
 */
@ResourceLock("fieldViewerServer")
public class FenicsLocatedReadsBenchmark {

	private static final User OWNER = new User("bench", new KeyValue("1"));

	@TempDir
	File tmp;

	private static String enc(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}

	private static String get(int port, String sim, String route, String query) throws Exception {
		HttpResponse<String> r = HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + route + "?sim=" + sim + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return r.body();
	}

	@Test
	public void measure() throws Exception {
		String spec = System.getProperty("vcell.fenics.aleBenchmark");
		Assumptions.assumeTrue(spec != null && !spec.isEmpty(), "set -Dvcell.fenics.aleBenchmark to run");
		long latency = Long.getLong("vcell.fenics.aleBenchmarkLatencyMs", 70);
		int port = FieldViewerServer.start();
		try {
			int n = 0;
			for (String one : spec.split("@@")) {
				String[] f = one.split("\\|");
				File src = new File(f[0]);
				String key = String.valueOf(900000 + n++);
				File primary = new File(tmp, "primary" + key);
				File target = new File(new File(primary, OWNER.getName()), "SimID_" + key + "_0_.fenics");
				try (Stream<Path> paths = Files.walk(src.toPath())) {
					for (Path p : (Iterable<Path>) paths::iterator) {
						Path dest = target.toPath().resolve(src.toPath().relativize(p).toString());
						if (Files.isDirectory(p)) Files.createDirectories(dest); else Files.copy(p, dest);
					}
				}
				VCSimulationDataIdentifier sim = new VCSimulationDataIdentifier(new VCSimulationIdentifier(new KeyValue(key), OWNER), 0);
				FenicsBundle bundle = FenicsBundle.open(target);
				System.out.printf("%n== %s: %s, %d rows, %d segments, %d points%n", src.getName(), f[1], bundle.getTimes().size(),
						bundle.getSegments().size(), bundle.domain(f[1]).numPoints());
				String[][] queries = {
						{ "kymograph", "/kymograph", "&domain=" + f[1] + "&var=" + f[2] + "&path=" + enc(f[3]) },
						{ "probe", "/timeseries", "&domain=" + f[1] + "&var=" + f[2] + "&points=" + enc(f[4]) },
				};
				FieldViewerServer.registerBundle("d" + key, 0, BundleStore.directory(target), "disk");
				for (String[] q : queries) {
					String disk = get(port, "d" + key, q[1], q[2]);
					for (int level : new int[] { 13, 14, 16, 17 }) {
						for (boolean withLatency : new boolean[] { false, true }) {
							MeasuredDataServer server = new MeasuredDataServer(new DataSetControllerImpl(null, primary, null), sim, level,
									withLatency ? latency : 0);
							String id = "r" + key + "x" + level + (withLatency ? "l" : "");
							FieldViewerServer.registerBundle(id, 0, server.store(), "remote");
							long t0 = System.nanoTime();
							String answer = get(port, id, q[1], q[2]);
							double seconds = (System.nanoTime() - t0) / 1e9;
							Assertions.assertEquals(disk, answer, q[0] + " through a " + level + " data server: the disk's answer");
							System.out.printf("%-10s data server 8.2.0.%d %s: %4d RPCs, %9.3f MB, %7.3f s%n", q[0], level,
									withLatency ? "+" + latency + " ms/RPC" : "local      ", server.rpcs.get(), server.bytes.get() / 1e6, seconds);
						}
					}
				}
				// the data server's work per row: decode the positions, build the row's locator, locate, gather
				FenicsBundle.Segment seg = bundle.getSegments().stream().max(java.util.Comparator.comparingInt(FenicsBundle.Segment::count)).get();
				org.vcell.vis.vtk.VtuGridParser.VtuGrid segMesh = org.vcell.vis.vtk.VtuGridParser.parse(
						BundleStore.directory(target).read(seg.prefix() + bundle.domain(f[1]).mesh()));
				BundleStore disk = BundleStore.directory(target);
				int[] rows = java.util.stream.IntStream.range(0, seg.count()).toArray();
				double[] points = new double[3 * 2000];
				for (int i = 0; i < 2000; i++) { // a kymograph's most samples, across the mesh's box
					points[3 * i] = -5 + 10.0 * i / 1999;
					points[3 * i + 1] = 0.37;
				}
				String[] arrays = { seg.prefix() + bundle.variable(f[1], f[2]).path() };
				for (int warm = 0; warm < 3; warm++) {
					FenicsBundle.locate(disk, seg.prefix() + bundle.domain(f[1]).mesh(), seg.prefix() + f[1] + "/_coords", arrays, points, false,
							rows, FenicsBundle.MAX_SAMPLE_VALUES, FenicsBundle.MAX_LOCATE_WORK);
				}
				long t0 = System.nanoTime();
				int reps = 5;
				for (int k = 0; k < reps; k++) {
					FenicsBundle.locate(disk, seg.prefix() + bundle.domain(f[1]).mesh(), seg.prefix() + f[1] + "/_coords", arrays, points, false,
							rows, FenicsBundle.MAX_SAMPLE_VALUES, FenicsBundle.MAX_LOCATE_WORK);
				}
				System.out.printf("server locate: %.2f ms per row (%d points, %d mesh points, %d cells, 1 array)%n",
						(System.nanoTime() - t0) / 1e6 / reps / rows.length, 2000, segMesh.numPoints(), segMesh.cells.length);
			}
		} finally {
			FieldViewerServer.stop();
		}
	}
}
