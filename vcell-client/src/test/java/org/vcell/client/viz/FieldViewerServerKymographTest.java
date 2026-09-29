package org.vcell.client.viz;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.vcell.util.Coordinate;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataJobID;

import cbit.vcell.geometry.CurveSelectionInfo;
import cbit.vcell.geometry.PolyLine;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionVolume;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solvers.CartesianMesh;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.vcell.client.viz.FieldViewerServerFvTest.SIM_2D;
import static org.vcell.client.viz.FieldViewerServerFvTest.SIM_3D;

/**
 * {@code /kymograph} on the two finite-volume fixture runs (see {@link FieldViewerServerFvTest}).
 * <p>
 * The 3D run ({@code 868220316}) is 5 × 5 × 5 with unit spacing, origin 0: element {@code (i,j,k)} sits at
 * {@code (i,j,k)}. {@code subdomain1} is a small ball in the middle (the 3 × 3 block at z = 2 and a plus
 * shape at z = 1 and 3); {@code subdomain0}, where {@code s0} lives, is everything around it.
 */
@Tag("Fast")
@ResourceLock("fieldViewerServer")
public class FieldViewerServerKymographTest {

	private Path root;
	private int port;
	private VCDataManager dataManager;

	@BeforeEach
	public void setup() throws Exception {
		root = Files.createTempDirectory("FieldViewerServerKymographTest_");
		dataManager = FieldViewerServerFvTest.stageFvFixtures(root);
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

	private HttpResponse<String> send(String sim, String query) throws Exception {
		return HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/kymograph?sim=" + sim + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private JsonObject kymograph(String sim, String query) throws Exception {
		HttpResponse<String> r = send(sim, query);
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return JsonParser.parseString(r.body()).getAsJsonObject();
	}

	private static String path(double[][] vertices) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < vertices.length; i++) {
			sb.append(i > 0 ? ";" : "").append(vertices[i][0]).append(',').append(vertices[i][1]).append(',').append(vertices[i][2]);
		}
		return "&path=" + URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8);
	}

	private static int[] ints(JsonArray a) {
		int[] out = new int[a.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = a.get(i).getAsInt();
		}
		return out;
	}

	private static double[] doubles(JsonArray a) {
		double[] out = new double[a.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = a.get(i).isJsonNull() ? Double.NaN : a.get(i).getAsDouble();
		}
		return out;
	}

	/**
	 * The desktop's kymograph of {@code var} along {@code vertices}, built as the desktop builds it:
	 * {@code PDEDataViewer.showKymograph}'s {@code SpatialSelectionVolume(...).getIndexSamples(0, 1)} on a
	 * {@link PolyLine}, then {@code KymographPanel.initDataManagerVariable}'s one {@link TimeSeriesJobSpec}
	 * with the membrane-crossing indices, run on the same data.
	 */
	private Desktop desktop(String sim, String var, double[][] vertices) throws Exception {
		VCSimulationDataIdentifier vcdID = FieldViewerServerFvTest.vcdID(sim);
		CartesianMesh mesh = dataManager.getMesh(vcdID);
		Coordinate[] coords = new Coordinate[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			coords[i] = new Coordinate(vertices[i][0], vertices[i][1], vertices[i][2]);
		}
		SpatialSelection.SSHelper ssh = new SpatialSelectionVolume(new CurveSelectionInfo(new PolyLine(coords)),
				VariableType.VOLUME, mesh).getIndexSamples(0.0, 1.0);
		double[] times = dataManager.getDataSetTimes(vcdID);
		TimeSeriesJobSpec spec = new TimeSeriesJobSpec(new String[] { var }, new int[][] { ssh.getSampledIndexes() },
				ssh.getMembraneIndexesInOut() != null ? new int[][] { ssh.getMembraneIndexesInOut() } : null,
				times[0], 1, times[times.length - 1], VCDataJobID.createVCDataJobID(vcdID.getOwner(), true));
		TSJobResultsNoStats results = (TSJobResultsNoStats) dataManager.getTimeSeriesValues(
				new OutputContext(new AnnotatedFunction[0]), vcdID, spec);
		return new Desktop(ssh, results.getTimesAndValuesForVariable(var));
	}

	private record Desktop(SpatialSelection.SSHelper ssh, double[][] timesAndValues) {
	}

	/**
	 * Desktop parity (plan §6.1): with {@code raw=1}, {@code /kymograph} has the desktop kymograph's samples,
	 * arc lengths, membrane crossings and values, value for value.
	 *
	 * @return the {@code raw=1} response
	 */
	private JsonObject assertDesktopParity(String sim, String domain, String var, double[][] vertices) throws Exception {
		Desktop d = desktop(sim, var, vertices);
		JsonObject k = kymograph(sim, "&domain=" + domain + "&var=" + var + "&raw=1" + path(vertices));
		Assertions.assertEquals("voxel-crossing", k.get("sampling").getAsString());
		Assertions.assertEquals("cell", k.get("location").getAsString());
		JsonObject samples = k.getAsJsonObject("samples");
		int[] indices = d.ssh().getSampledIndexes();
		Assertions.assertArrayEquals(indices, ints(samples.getAsJsonArray("volumeIndex")));
		Assertions.assertArrayEquals(d.ssh().getWorldCoordinateLengths(), doubles(samples.getAsJsonArray("arcLength")));
		int[] crossing = d.ssh().getMembraneIndexesInOut();
		int[] served = ints(samples.getAsJsonArray("membraneIndex"));
		for (int i = 0; i < indices.length; i++) {
			Assertions.assertEquals(crossing != null ? crossing[i] : -1, served[i], "membrane index of sample " + i);
		}
		Coordinate[] wc = d.ssh().getSampleCoordinates();
		double[] points = doubles(samples.getAsJsonArray("points"));
		for (int i = 0; i < wc.length; i++) {
			Assertions.assertArrayEquals(new double[] { wc[i].getX(), wc[i].getY(), wc[i].getZ() },
					new double[] { points[3 * i], points[3 * i + 1], points[3 * i + 2] });
		}
		Assertions.assertArrayEquals(d.timesAndValues()[0], doubles(k.getAsJsonArray("times")));
		JsonArray rows = k.getAsJsonArray("values");
		Assertions.assertEquals(d.timesAndValues()[0].length, rows.size());
		for (int r = 0; r < rows.size(); r++) {
			double[] row = doubles(rows.get(r).getAsJsonArray());
			for (int i = 0; i < indices.length; i++) {
				Assertions.assertEquals(d.timesAndValues()[1 + i][r], row[i], 0.0, "time " + r + ", sample " + i);
			}
		}
		return k;
	}

	@Test
	public void matchesTheDesktopKymographAcrossTheMembraneIn3D() throws Exception {
		// in the z = 2 slice, as the desktop draws lines: an oblique line (the desktop's general sampling)
		// and an axis-aligned one (its lattice walk), both through the ball of subdomain1
		JsonObject oblique = assertDesktopParity(SIM_3D, "subdomain0", "s0", new double[][] { { 0, 0.3, 2 }, { 4, 3.4, 2 } });
		JsonObject straight = assertDesktopParity(SIM_3D, "subdomain0", "s0", new double[][] { { 0, 2, 2 }, { 4, 2, 2 } });
		for (JsonObject k : new JsonObject[] { oblique, straight }) {
			int crossings = 0;
			for (int m : ints(k.getAsJsonObject("samples").getAsJsonArray("membraneIndex"))) {
				crossings += m >= 0 ? 1 : 0;
			}
			Assertions.assertTrue(crossings >= 4, "two membrane crossings, two samples each: " + crossings);
		}
		// the straight line walks the voxel centres (0,2,2) … (4,2,2), with a pair of samples where it crosses
		// the membrane faces at x = 0.5 and 3.5
		Assertions.assertArrayEquals(new double[] { 0, 0.5, 0.5, 1, 2, 3, 3.5, 3.5, 4 },
				doubles(straight.getAsJsonObject("samples").getAsJsonArray("arcLength")));
	}

	@Test
	public void matchesTheDesktopKymographIn2D() throws Exception {
		// Cyt is the disk in the middle of EC; Dex lives in Cyt. A 2D path lies in the served grid's plane.
		double z = kymograph(SIM_2D, "&domain=Cyt&var=Dex&path=-10,-9%3B9,8").getAsJsonArray("path").get(0)
				.getAsJsonArray().get(2).getAsDouble();
		JsonObject k = assertDesktopParity(SIM_2D, "Cyt", "Dex", new double[][] { { -10, -9, z }, { 9, 8, z } });
		Assertions.assertNotNull(k.getAsJsonObject("samples").getAsJsonArray("membraneIndex"));
		assertDesktopParity(SIM_2D, "Cyt", "Dex", new double[][] { { -10, 0, z }, { 0, 0, z }, { 3, 10, z } });
	}

	@Test
	public void samplesOutsideTheDomainAreGapsUnlessRaw() throws Exception {
		double[][] line = { { 0, 2, 2 }, { 4, 2, 2 } };
		JsonObject masked = kymograph(SIM_3D, "&domain=subdomain0&var=s0" + path(line));
		JsonObject raw = kymograph(SIM_3D, "&domain=subdomain0&var=s0&raw=1" + path(line));
		Assertions.assertFalse(masked.get("raw").getAsBoolean());
		JsonArray inDomain = masked.getAsJsonObject("samples").getAsJsonArray("inDomain");
		int[] indices = ints(masked.getAsJsonObject("samples").getAsJsonArray("volumeIndex"));
		// voxels (0..4, 2, 2): the ball holds x = 1..3; the crossing pairs put one sample on each side
		boolean[] expected = new boolean[indices.length];
		for (int i = 0; i < indices.length; i++) {
			int x = indices[i] % 5;
			expected[i] = x == 0 || x == 4;
			Assertions.assertEquals(expected[i], inDomain.get(i).getAsBoolean(), "sample " + i);
		}
		for (int r = 0; r < masked.getAsJsonArray("values").size(); r++) {
			JsonArray m = masked.getAsJsonArray("values").get(r).getAsJsonArray();
			JsonArray w = raw.getAsJsonArray("values").get(r).getAsJsonArray();
			for (int i = 0; i < indices.length; i++) {
				if (expected[i]) {
					Assertions.assertEquals(w.get(i), m.get(i));
				} else {
					Assertions.assertTrue(m.get(i).isJsonNull(), "masked: time " + r + ", sample " + i);
					Assertions.assertFalse(w.get(i).isJsonNull(), "raw keeps the other compartment's value");
				}
			}
		}
		// samples 0 and 1 are both voxel (0,2,2): sample 1 is its side of the crossing pair at x = 0.5, and
		// carries the membrane index the _INSIDE/_OUTSIDE correction uses (this run saves no membrane-adjacent
		// data, so its value is the voxel's own; desktop parity covers the corrected values)
		Assertions.assertEquals(indices[0], indices[1]);
		JsonArray membrane = masked.getAsJsonObject("samples").getAsJsonArray("membraneIndex");
		Assertions.assertEquals(-1, membrane.get(0).getAsInt());
		Assertions.assertTrue(membrane.get(1).getAsInt() >= 0);
		Assertions.assertEquals(membrane.get(1), membrane.get(2), "one membrane, two sides");
	}

	@Test
	public void aDiagonalThroughVoxelCornersFallsBackToTheDdaWalk() throws Exception {
		// centre to centre through the vertices at (0.5,0.5,0.5), (1.5,1.5,1.5), …: the desktop's sampling throws
		double[][] diagonal = { { 0, 0, 0 }, { 4, 4, 4 } };
		Assertions.assertThrows(RuntimeException.class, () -> desktop(SIM_3D, "s0", diagonal),
				"the desktop's own sampling can't take this line");
		JsonObject k = kymograph(SIM_3D, "&domain=subdomain0&var=s0" + path(diagonal));
		Assertions.assertEquals("dda", k.get("sampling").getAsString());
		JsonObject samples = k.getAsJsonObject("samples");
		// one sample per voxel on the diagonal, stepping through each vertex, never beside it
		Assertions.assertArrayEquals(new int[] { 0, 31, 62, 93, 124 }, ints(samples.getAsJsonArray("volumeIndex")));
		Assertions.assertArrayEquals(new int[] { -1, -1, -1, -1, -1 }, ints(samples.getAsJsonArray("membraneIndex")));
		double s3 = Math.sqrt(3);
		double[] arc = doubles(samples.getAsJsonArray("arcLength"));
		Assertions.assertArrayEquals(new double[] { 0, s3, 2 * s3, 3 * s3, 4 * s3 }, arc, 1e-9);
		Assertions.assertEquals(4 * s3, k.get("pathLength").getAsDouble(), 1e-12);
		// (2,2,2) is in the ball: a gap; the rest read the same numbers as /timeseries at their points
		boolean[] in = { true, true, false, true, true };
		JsonArray inDomain = samples.getAsJsonArray("inDomain");
		double[] points = doubles(samples.getAsJsonArray("points"));
		StringBuilder pts = new StringBuilder("&points=");
		for (int i = 0; i < 5; i++) {
			Assertions.assertEquals(in[i], inDomain.get(i).getAsBoolean());
			pts.append(i > 0 ? ";" : "").append(points[3 * i]).append(',').append(points[3 * i + 1]).append(',').append(points[3 * i + 2]);
		}
		JsonObject ts = JsonParser.parseString(HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(
				"http://127.0.0.1:" + port + "/timeseries?sim=" + SIM_3D + "&job=0&domain=subdomain0&var=s0"
						+ pts.toString().replace(";", "%3B"))).build(), HttpResponse.BodyHandlers.ofString()).body()).getAsJsonObject();
		JsonArray rows = k.getAsJsonArray("values");
		for (int i = 0; i < 5; i++) {
			JsonArray series = ts.getAsJsonArray("series").get(i).getAsJsonObject().getAsJsonArray("values");
			for (int r = 0; r < rows.size(); r++) {
				Assertions.assertEquals(series.get(r), rows.get(r).getAsJsonArray().get(i), "sample " + i + ", time " + r);
			}
		}
	}

	@Test
	public void tstepStridesOverTheSavedTimes() throws Exception {
		double[][] line = { { 0, 0.3, 2 }, { 4, 3.4, 2 } };
		JsonObject all = kymograph(SIM_3D, "&domain=subdomain0&var=s0" + path(line));
		JsonObject every2 = kymograph(SIM_3D, "&domain=subdomain0&var=s0&tstep=2" + path(line));
		Assertions.assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5 }, ints(all.getAsJsonArray("timeIndices")));
		Assertions.assertArrayEquals(new int[] { 0, 2, 4 }, ints(every2.getAsJsonArray("timeIndices")));
		Assertions.assertArrayEquals(new double[] { 0.0, 0.2, 0.4 }, doubles(every2.getAsJsonArray("times")), 1e-12);
		for (int r = 0; r < 3; r++) {
			Assertions.assertEquals(all.getAsJsonArray("values").get(2 * r), every2.getAsJsonArray("values").get(r));
		}
		Assertions.assertEquals(all.getAsJsonObject("samples"), every2.getAsJsonObject("samples"));
	}

	@Test
	public void tooManyValuesSuggestsAStride() throws Exception {
		double[][] line = { { 0, 0.3, 2 }, { 4, 3.4, 2 } };
		int n = kymograph(SIM_3D, "&domain=subdomain0&var=s0" + path(line)).getAsJsonObject("samples")
				.getAsJsonArray("volumeIndex").size();
		// room for n samples × 2 times: of 6 saved times, a stride of 3 returns two (indices 0 and 3)
		System.setProperty("vcell.fieldViewer.maxKymographValues", Integer.toString(2 * n));
		try {
			HttpResponse<String> r = send(SIM_3D, "&domain=subdomain0&var=s0" + path(line));
			Assertions.assertEquals(400, r.statusCode(), r.body());
			JsonObject err = JsonParser.parseString(r.body()).getAsJsonObject();
			Assertions.assertEquals(3, err.get("suggestedTstep").getAsInt(), r.body());
			JsonObject retried = kymograph(SIM_3D, "&domain=subdomain0&var=s0&tstep=3" + path(line));
			Assertions.assertArrayEquals(new int[] { 0, 3 }, ints(retried.getAsJsonArray("timeIndices")));
		} finally {
			System.clearProperty("vcell.fieldViewer.maxKymographValues");
		}
	}

	@Test
	public void aTwoDimensionalPathMayLeaveOutZ() throws Exception {
		JsonObject withZ = kymograph(SIM_2D, "&domain=Cyt&var=Dex" + path(new double[][] { { -10, -9, 7 }, { 9, 8, 7 } }));
		JsonObject withoutZ = kymograph(SIM_2D, "&domain=Cyt&var=Dex&path=-10,-9%3B9,8");
		Assertions.assertEquals(withZ.getAsJsonObject("samples").get("volumeIndex"), withoutZ.getAsJsonObject("samples").get("volumeIndex"));
		Assertions.assertEquals(withZ.get("values"), withoutZ.get("values"));
	}

	@Test
	public void aLineOutsideTheDomainIsAllGaps() throws Exception {
		// wholly inside the ball: in the mesh, not in subdomain0
		JsonObject k = kymograph(SIM_3D, "&domain=subdomain0&var=s0" + path(new double[][] { { 1.8, 2, 2 }, { 2.2, 2, 2 } }));
		for (JsonElement b : k.getAsJsonObject("samples").getAsJsonArray("inDomain")) {
			Assertions.assertFalse(b.getAsBoolean());
		}
		for (JsonElement row : k.getAsJsonArray("values")) {
			for (JsonElement v : row.getAsJsonArray()) {
				Assertions.assertTrue(v.isJsonNull());
			}
		}
	}

	@Test
	public void badRequestsAreRefused() throws Exception {
		String q = "&domain=subdomain0&var=s0";
		String[][] cases = {
				{ q + "&path=1,1,1", "zero length" }, // one vertex
				{ q + "&path=1,1,1%3B1,1,1", "zero length" },
				{ q + "&path=1,1,1%3B9,1,1", "outside the mesh" },
				{ q + "&path=1,1%3B2,2", "no z" },
				{ q + "&path=", "empty" },
				{ q + "&path=1,1,1%3B2,2,2&tstep=0", "tstep" },
				{ "&domain=subdomain0&var=nothing&path=1,1,1%3B2,2,2", "unknown variable" },
				{ "&domain=subdomain0&var=sobj_subdomain11_subdomain00_size&path=1,1,1%3B2,2,2", "membrane kymographs are not supported yet" },
				{ "&domain=subdomain0&var=Size_c0&path=1,1,1%3B2,2,2", "volume variable" },
				{ "&domain=nowhere&var=s0&path=1,1,1%3B2,2,2", "unknown volume domain" },
		};
		for (String[] c : cases) {
			HttpResponse<String> r = send(SIM_3D, c[0]);
			Assertions.assertEquals(400, r.statusCode(), c[0] + " → " + r.body());
			Assertions.assertTrue(r.body().contains(c[1]), c[0] + " → " + r.body());
		}
		StringBuilder many = new StringBuilder("&path=");
		for (int i = 0; i <= FvLineSampler.MAX_PATH_VERTICES; i++) {
			many.append(i > 0 ? "%3B" : "").append(i % 2 == 0 ? "1,1,1" : "2,2,2");
		}
		HttpResponse<String> r = send(SIM_3D, q + many);
		Assertions.assertEquals(400, r.statusCode());
		Assertions.assertTrue(r.body().contains("at most 64 path vertices"), r.body());
	}

	@Test
	public void aSecondHeavyJobIsTurnedAwayAsBusy() throws Exception {
		double[][] line = { { 0, 0.3, 2 }, { 4, 3.4, 2 } };
		Assertions.assertTrue(FieldViewerServer.HEAVY_JOBS.tryAcquire(), "nothing else is running");
		try {
			HttpResponse<String> busy = send(SIM_3D, "&domain=subdomain0&var=s0" + path(line));
			Assertions.assertEquals(503, busy.statusCode(), busy.body());
			Assertions.assertTrue(JsonParser.parseString(busy.body()).getAsJsonObject().get("busy").getAsBoolean());
		} finally {
			FieldViewerServer.HEAVY_JOBS.release();
		}
		kymograph(SIM_3D, "&domain=subdomain0&var=s0" + path(line));
	}
}
