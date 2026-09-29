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
import org.vcell.util.Coordinate;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataJobID;

import cbit.vcell.geometry.CurveSelectionInfo;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionMembrane;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.MeshDisplayAdapter;

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
import java.util.Map;
import java.util.stream.Stream;

/**
 * Finite-volume membrane variables (docs/plan-plotting.md P7): shown on their membrane's faces ({@code /info},
 * {@code /grid}, {@code /field}), probed ({@code /timeseries?points=}), and kymographs along the membrane that are
 * the desktop's own ({@code /kymograph}, {@link FvMembraneCurve}). Fixtures: the 2D run with its test membrane
 * function {@code xy_PM = x + 2y + 10t}, and MembraneFrap3D (21³, {@code r_PM} and {@code rf_PM} on a ball's
 * membrane).
 */
@Tag("Fast")
@ResourceLock("fieldViewerServer")
public class FieldViewerServerMembraneTest {

	private static final String MEM_2D = "Cyt_EC_membrane";
	private static final String MEM_3D = "subdomain0_subdomain1_membrane";

	private Path root;
	private VCDataManager dataManager;
	private int port;

	@BeforeEach
	public void setup() throws Exception {
		root = Files.createTempDirectory("FieldViewerServerMembraneTest_");
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

	private HttpResponse<String> send(String sim, String route, String query) throws Exception {
		return HttpClient.newHttpClient().send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + route + "?sim=" + sim + "&job=0" + query)).build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private JsonObject get(String sim, String route, String query) throws Exception {
		HttpResponse<String> r = send(sim, route, query);
		Assertions.assertEquals(200, r.statusCode(), r.body());
		return JsonParser.parseString(r.body()).getAsJsonObject();
	}

	private static String enc(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}

	private static double[] doubles(JsonArray a) {
		double[] d = new double[a.size()];
		for (int i = 0; i < d.length; i++) {
			d[i] = a.get(i).isJsonNull() ? Double.NaN : a.get(i).getAsDouble();
		}
		return d;
	}

	private static int[] ints(JsonArray a) {
		int[] d = new int[a.size()];
		for (int i = 0; i < d.length; i++) {
			d[i] = a.get(i).getAsInt();
		}
		return d;
	}

	private CartesianMesh mesh(String sim) throws Exception {
		return dataManager.getMesh(FieldViewerServerFvTest.vcdID(sim));
	}

	private static OutputContext noFunctions() {
		return new OutputContext(new AnnotatedFunction[0]);
	}

	/** The desktop's time courses at {@code indices}: {@code {times, values at indices[0], …}}. */
	private double[][] desktopSeries(String sim, String var, int[] indices, int tstep) throws Exception {
		VCSimulationDataIdentifier vcdID = FieldViewerServerFvTest.vcdID(sim);
		double[] times = dataManager.getDataSetTimes(vcdID);
		TimeSeriesJobSpec spec = new TimeSeriesJobSpec(new String[] { var }, new int[][] { indices }, null, times[0], tstep,
				times[times.length - 1], VCDataJobID.createVCDataJobID(vcdID.getOwner(), true));
		return ((TSJobResultsNoStats) dataManager.getTimeSeriesValues(noFunctions(), vcdID, spec)).getTimesAndValuesForVariable(var);
	}

	@Test
	public void infoListsMembraneVariablesAndDomains() throws Exception {
		JsonObject info2d = get(FieldViewerServerFvTest.SIM_2D, "/info", "");
		Assertions.assertEquals("[\"EC\",\"Cyt\",\"Cyt_EC_membrane\"]", info2d.getAsJsonArray("domains").toString(),
				"the volume domains first, as before, then the membranes");
		JsonArray vars = info2d.getAsJsonArray("variables");
		Assertions.assertEquals("Dex", vars.get(0).getAsJsonObject().get("name").getAsString(), "volume variables first");
		JsonObject xy = vars.get(vars.size() - 1).getAsJsonObject();
		Assertions.assertEquals("xy_PM", xy.get("name").getAsString());
		Assertions.assertEquals(MEM_2D, xy.get("domain").getAsString());
		Assertions.assertTrue(xy.get("membrane").getAsBoolean());
		for (int v = 0; v < vars.size() - 1; v++) {
			Assertions.assertFalse(vars.get(v).getAsJsonObject().has("membrane"), "a volume variable is as before");
		}
		// the membrane region functions (Size_PM, …) have one value per region: not listed
		Assertions.assertFalse(info2d.toString().contains("Size_PM"));

		JsonObject info3d = get(FieldViewerServerFvTest.SIM_MEMBRANE_3D, "/info", "");
		Assertions.assertTrue(info3d.getAsJsonArray("domains").toString().contains(MEM_3D));
		Assertions.assertTrue(info3d.toString().contains("{\"name\":\"r_PM\",\"domain\":\"" + MEM_3D
				+ "\",\"isFunction\":false,\"membrane\":true}"), info3d.toString());
	}

	/** Each face of the served membrane carries its element's value; a 2D membrane is segments, a 3D one quads. */
	@Test
	public void theMembraneIsServedAsItsFacesWithTheirValues() throws Exception {
		JsonObject g3 = get(FieldViewerServerFvTest.SIM_MEMBRANE_3D, "/grid", "&domain=" + MEM_3D);
		Assertions.assertEquals(3, g3.get("dimension").getAsInt());
		Assertions.assertEquals(9, g3.get("cellType").getAsInt(), "VTK_QUAD");
		Assertions.assertTrue(g3.get("membrane").getAsBoolean());
		Assertions.assertFalse(g3.has("sinc"), "drawn as they are, not smoothed");
		CartesianMesh mesh3 = mesh(FieldViewerServerFvTest.SIM_MEMBRANE_3D);
		Assertions.assertEquals(mesh3.getNumMembraneElements(), g3.getAsJsonArray("cells").size(), "one face per element");

		JsonObject f3 = get(FieldViewerServerFvTest.SIM_MEMBRANE_3D, "/field", "&domain=" + MEM_3D + "&var=r_PM&time=0.3");
		double[] served = doubles(f3.getAsJsonArray("values"));
		SimDataBlock block = dataManager.getSimDataBlock(noFunctions(), FieldViewerServerFvTest.vcdID(FieldViewerServerFvTest.SIM_MEMBRANE_3D), "r_PM", 0.3);
		double[] raw = block.getData();
		java.util.Arrays.sort(served);
		double[] sortedRaw = raw.clone();
		java.util.Arrays.sort(sortedRaw);
		Assertions.assertArrayEquals(sortedRaw, served, 0.0, "every element's value, once");

		JsonObject g2 = get(FieldViewerServerFvTest.SIM_2D, "/grid", "&domain=" + MEM_2D);
		Assertions.assertEquals(2, g2.get("dimension").getAsInt());
		Assertions.assertEquals(3, g2.get("cellType").getAsInt(), "VTK_LINE");
		// xy_PM = x + 2y + 10t at each element's coordinate (the desktop's: halfway between its two voxels)
		JsonObject f2 = get(FieldViewerServerFvTest.SIM_2D, "/field", "&domain=" + MEM_2D + "&var=xy_PM&time=1.5");
		double[] v2 = doubles(f2.getAsJsonArray("values"));
		JsonArray probes = get(FieldViewerServerFvTest.SIM_2D, "/timeseries", "&domain=" + MEM_2D + "&var=xy_PM&points="
				+ enc(faceCentres(g2, 0, v2.length))).getAsJsonArray("series");
		CartesianMesh mesh2 = mesh(FieldViewerServerFvTest.SIM_2D);
		for (int c = 0; c < v2.length; c++) {
			JsonObject s = probes.get(c).getAsJsonObject();
			Assertions.assertEquals(c, s.get("cell").getAsInt(), "a face's centre probes that face");
			Coordinate at = mesh2.getCoordinateFromMembraneIndex(s.get("membraneIndex").getAsInt());
			Assertions.assertEquals(at.getX() + 2 * at.getY() + 15, v2[c], 1e-9, "face " + c);
		}
	}

	/** The centres of served faces {@code from … to - 1}, as a {@code points} list. */
	private static String faceCentres(JsonObject grid, int from, int to) {
		double[] p = doubles(grid.getAsJsonArray("points"));
		JsonArray cells = grid.getAsJsonArray("cells");
		StringBuilder sb = new StringBuilder();
		for (int c = from; c < to; c++) {
			int[] ids = ints(cells.get(c).getAsJsonArray());
			double[] m = new double[3];
			for (int id : ids) {
				for (int a = 0; a < 3; a++) {
					m[a] += p[3 * id + a] / ids.length;
				}
			}
			sb.append(c > from ? ";" : "").append(m[0]).append(',').append(m[1]).append(',').append(m[2]);
		}
		return sb.toString();
	}

	/**
	 * A membrane probe reads the element of the face it lands on, as the desktop's membrane time plot does: its
	 * values are the desktop's {@link TimeSeriesJobSpec} at that membrane index. A point far from the membrane
	 * is a gap.
	 */
	@Test
	public void aMembraneProbeReadsTheElementOfItsFace() throws Exception {
		String sim = FieldViewerServerFvTest.SIM_MEMBRANE_3D;
		JsonObject grid = get(sim, "/grid", "&domain=" + MEM_3D);
		String points = faceCentres(grid, 100, 110) + ";5,5,5";
		JsonObject ts = get(sim, "/timeseries", "&domain=" + MEM_3D + "&var=r_PM&points=" + enc(points));
		Assertions.assertEquals("cell", ts.get("location").getAsString());
		JsonArray series = ts.getAsJsonArray("series");
		int[] indices = new int[10];
		for (int k = 0; k < 10; k++) {
			JsonObject s = series.get(k).getAsJsonObject();
			Assertions.assertEquals(100 + k, s.get("cell").getAsInt());
			Assertions.assertTrue(s.get("inDomain").getAsBoolean());
			Assertions.assertFalse(s.has("volumeIndex"));
			indices[k] = s.get("membraneIndex").getAsInt();
		}
		double[][] desktop = desktopSeries(sim, "r_PM", indices, 1);
		for (int k = 0; k < 10; k++) {
			Assertions.assertArrayEquals(desktop[1 + k], doubles(series.get(k).getAsJsonObject().getAsJsonArray("values")), 0.0);
		}
		JsonObject centre = series.get(10).getAsJsonObject(); // the ball's centre, 3.6 µm from its membrane
		Assertions.assertFalse(centre.get("inDomain").getAsBoolean());
		Assertions.assertEquals(-1, centre.get("cell").getAsInt());
		for (double v : doubles(centre.getAsJsonArray("values"))) {
			Assertions.assertTrue(Double.isNaN(v));
		}
		// a click beside a 2D membrane snaps onto it
		JsonObject g2 = get(FieldViewerServerFvTest.SIM_2D, "/grid", "&domain=" + MEM_2D);
		String beside = faceCentres(g2, 3, 4);
		String[] xyz = beside.split(",");
		String nudged = (Double.parseDouble(xyz[0]) + 0.3) + "," + (Double.parseDouble(xyz[1]) - 0.2);
		JsonObject s2 = get(FieldViewerServerFvTest.SIM_2D, "/timeseries", "&domain=" + MEM_2D + "&var=xy_PM&points=" + enc(nudged))
				.getAsJsonArray("series").get(0).getAsJsonObject();
		Assertions.assertEquals(3, s2.get("cell").getAsInt());
		Assertions.assertTrue(s2.has("snapped"));
	}

	/**
	 * A desktop membrane selection: in the slice normal to {@code axis} at {@code slice}, segments {@code s0}
	 * to {@code sN} of the slice's (one) membrane curve, going down when {@code negative}; {@code through}, when
	 * not -1, is a segment the picks pass on the way (to choose the long way round).
	 */
	private record Selection(String sim, String var, int axis, int slice, int s0, int sN, boolean negative, int through) {
	}

	private static final Selection[] SELECTIONS = {
			new Selection(FieldViewerServerFvTest.SIM_2D, "xy_PM", Coordinate.Z_AXIS, 0, 4, 12, false, -1), // the short way
			new Selection(FieldViewerServerFvTest.SIM_2D, "xy_PM", Coordinate.Z_AXIS, 0, 48, 4, false, -1), // through segment 0
			new Selection(FieldViewerServerFvTest.SIM_2D, "xy_PM", Coordinate.Z_AXIS, 0, 4, 12, true, 28), // the long way
			new Selection(FieldViewerServerFvTest.SIM_2D, "xy_PM", Coordinate.Z_AXIS, 0, 20, 20, false, -1), // one segment
			new Selection(FieldViewerServerFvTest.SIM_MEMBRANE_3D, "r_PM", Coordinate.Z_AXIS, 10, 16, 28, false, -1),
			new Selection(FieldViewerServerFvTest.SIM_MEMBRANE_3D, "r_PM", Coordinate.X_AXIS, 7, 40, 56, false, -1),
			new Selection(FieldViewerServerFvTest.SIM_MEMBRANE_3D, "rf_PM", Coordinate.Y_AXIS, 12, 57, 3, false, -1),
	};

	/** The slice's membrane curve, and its segments' membrane indices, as the desktop finds it. */
	private Map.Entry<SampledCurve, int[]> curveOf(Selection s) throws Exception {
		Map<SampledCurve, int[]> curves = new MeshDisplayAdapter(mesh(s.sim)).getCurvesAndMembraneIndexes(s.axis, s.slice);
		Assertions.assertEquals(1, curves.size(), "one membrane curve in the slice");
		return curves.entrySet().iterator().next();
	}

	/** Segment {@code seg}'s midpoint, as the viewer draws it: where a user would pick it. */
	private double[] drawnMidpoint(Selection s, SampledCurve curve, int seg) throws Exception {
		Coordinate[] ab = curve.getControlPointsForSegment(seg);
		double[] mid = { (ab[0].getX() + ab[1].getX()) / 2, (ab[0].getY() + ab[1].getY()) / 2, (ab[0].getZ() + ab[1].getZ()) / 2 };
		return FvMembraneCurve.toDrawn(mesh(s.sim), mid, 0);
	}

	private static String point(double[] p) {
		return p[0] + "," + p[1] + "," + p[2];
	}

	/**
	 * The desktop parity of plan §6.1 for membranes: picks at the drawn midpoints of the desktop selection's first
	 * and last segments (and one it passes, for the long way) give exactly the desktop's
	 * {@code SpatialSelectionMembrane.getIndexSamples()} — its membrane indices, sample points and arc lengths —
	 * and its {@link TimeSeriesJobSpec}'s values, every one.
	 */
	@Test
	public void membraneKymographsAreTheDesktops() throws Exception {
		for (Selection s : SELECTIONS) {
			String label = s.toString();
			Map.Entry<SampledCurve, int[]> e = curveOf(s);
			SampledCurve curve = e.getKey();
			CurveSelectionInfo csi = s.s0 == s.sN ? new CurveSelectionInfo(curve, s.s0, s.s0, false)
					: new CurveSelectionInfo(curve, s.s0, s.sN, s.negative);
			SpatialSelection.SSHelper desktop = new SpatialSelectionMembrane(csi, VariableType.MEMBRANE, mesh(s.sim),
					e.getValue(), curve).getIndexSamples();
			double[][] desktopValues = desktopSeries(s.sim, s.var, desktop.getSampledIndexes(), 1);

			String path = point(drawnMidpoint(s, curve, s.s0))
					+ (s.through >= 0 ? ";" + point(drawnMidpoint(s, curve, s.through)) : "")
					+ ";" + point(drawnMidpoint(s, curve, s.sN == s.s0 ? s.s0 : s.sN));
			String domain = s.sim.equals(FieldViewerServerFvTest.SIM_2D) ? MEM_2D : MEM_3D;
			String plane = s.sim.equals(FieldViewerServerFvTest.SIM_2D) ? "" : "&plane=" + "xyz".charAt(s.axis);
			if (s.s0 == s.sN) { // two picks on one segment: a little apart along it
				double[] a = drawnMidpoint(s, curve, s.s0);
				Coordinate[] ab = curve.getControlPointsForSegment(s.s0);
				double[] b = a.clone();
				b[0] += 0.1 * (ab[1].getX() - ab[0].getX());
				b[1] += 0.1 * (ab[1].getY() - ab[0].getY());
				path = point(a) + ";" + point(b);
			}
			JsonObject k = get(s.sim, "/kymograph", "&domain=" + domain + "&var=" + s.var + plane + "&path=" + enc(path));
			Assertions.assertEquals("membrane", k.get("sampling").getAsString(), label);
			Assertions.assertEquals("cell", k.get("location").getAsString(), label);
			JsonObject samples = k.getAsJsonObject("samples");
			Assertions.assertArrayEquals(desktop.getSampledIndexes(), ints(samples.getAsJsonArray("membraneIndex")), label);
			Assertions.assertArrayEquals(desktop.getWorldCoordinateLengths(), doubles(samples.getAsJsonArray("arcLength")), 0.0, label);
			double[] points = doubles(samples.getAsJsonArray("points"));
			Coordinate[] want = desktop.getSampleCoordinates();
			for (int i = 0; i < want.length; i++) {
				Assertions.assertArrayEquals(new double[] { want[i].getX(), want[i].getY(), want[i].getZ() },
						new double[] { points[3 * i], points[3 * i + 1], points[3 * i + 2] }, 0.0, label + " point " + i);
			}
			JsonArray rows = k.getAsJsonArray("values");
			Assertions.assertEquals(desktopValues[0].length, rows.size());
			for (int r = 0; r < rows.size(); r++) {
				double[] row = doubles(rows.get(r).getAsJsonArray());
				for (int i = 0; i < row.length; i++) {
					Assertions.assertEquals(desktopValues[1 + i][r], row[i], 0.0, label + " row " + r + " sample " + i);
				}
			}
			Assertions.assertEquals(desktop.getWorldCoordinateTotalLength(), k.get("pathLength").getAsDouble(), 0.0);
			if (!plane.isEmpty()) {
				JsonObject slice = k.getAsJsonObject("slice");
				Assertions.assertEquals(String.valueOf("xyz".charAt(s.axis)), slice.get("axis").getAsString());
				Assertions.assertEquals(s.slice, slice.get("index").getAsInt(), label);
			}
			// every sample is on a served face of its element: its cell, and its drawn point on that face
			int[] cell = ints(samples.getAsJsonArray("cell"));
			JsonObject grid = get(s.sim, "/grid", "&domain=" + domain);
			double[] gp = doubles(grid.getAsJsonArray("points"));
			JsonArray cells = grid.getAsJsonArray("cells");
			double[] drawn = doubles(k.getAsJsonArray("drawnPoints"));
			for (int i = 0; i < cell.length; i++) {
				Assertions.assertTrue(cell[i] >= 0, label + " sample " + i);
				int[] face = ints(cells.get(cell[i]).getAsJsonArray());
				double[] lo = { Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE };
				double[] hi = { -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE };
				for (int id : face) {
					for (int a = 0; a < 3; a++) {
						lo[a] = Math.min(lo[a], gp[3 * id + a]);
						hi[a] = Math.max(hi[a], gp[3 * id + a]);
					}
				}
				for (int a = 0; a < 3; a++) {
					double x = drawn[3 * i + a];
					Assertions.assertTrue(x >= lo[a] - 1e-9 && x <= hi[a] + 1e-9,
							label + " sample " + i + " axis " + a + ": " + x + " not in [" + lo[a] + ", " + hi[a] + "]");
				}
			}
		}
	}

	@Test
	public void theStrideAndTheValueLimitApply() throws Exception {
		Selection s = SELECTIONS[4];
		SampledCurve curve = curveOf(s).getKey();
		String q = "&domain=" + MEM_3D + "&var=r_PM&plane=z&path="
				+ enc(point(drawnMidpoint(s, curve, s.s0)) + ";" + point(drawnMidpoint(s, curve, s.sN)));
		JsonObject k = get(s.sim, "/kymograph", q + "&tstep=2");
		Assertions.assertEquals("[0,2,4]", k.getAsJsonArray("timeIndices").toString());
		System.setProperty("vcell.fieldViewer.maxKymographValues", "20");
		try {
			HttpResponse<String> r = send(s.sim, "/kymograph", q);
			Assertions.assertEquals(400, r.statusCode(), r.body());
			Assertions.assertTrue(r.body().contains("suggestedTstep"), r.body());
		} finally {
			System.clearProperty("vcell.fieldViewer.maxKymographValues");
		}
	}

	@Test
	public void statsCoverMembraneVariables() throws Exception {
		JsonObject stats = get(FieldViewerServerFvTest.SIM_MEMBRANE_3D, "/stats", "&var=r_PM");
		JsonObject series = stats.getAsJsonArray("series").get(0).getAsJsonObject();
		Assertions.assertEquals(MEM_3D, series.get("domain").getAsString());
		double[] min = doubles(series.getAsJsonArray("min"));
		double[] max = doubles(series.getAsJsonArray("max"));
		Assertions.assertEquals(5, min[0], 1e-12, "r_PM starts at 5 everywhere");
		Assertions.assertEquals(5, max[0], 1e-12);
		Assertions.assertTrue(max[1] > min[1], "then the bleach makes it uneven");
	}

	@Test
	public void badRequests() throws Exception {
		String sim3 = FieldViewerServerFvTest.SIM_MEMBRANE_3D;
		String sim2 = FieldViewerServerFvTest.SIM_2D;
		String[][] cases = {
				{ sim3, "/kymograph", "&domain=" + MEM_3D + "&var=r_PM&path=" + enc("8.5,5,5;5,8.5,5"), "plane=x|y|z" },
				{ sim3, "/kymograph", "&domain=" + MEM_3D + "&var=r_PM&plane=w&path=" + enc("8.5,5,5;5,8.5,5"), "must be x, y or z" },
				{ sim3, "/kymograph", "&domain=" + MEM_3D + "&var=r_PM&plane=z&path=" + enc("5,5,5;5.5,5,5"), "is not on the membrane" },
				{ sim3, "/kymograph", "&domain=" + MEM_3D + "&var=r_PM&plane=z&path=" + enc("5,5,0.1;5.5,5,0.1"), "does not cross this slice" },
				{ sim2, "/kymograph", "&domain=" + MEM_2D + "&var=Size_PM&path=" + enc("0,9.5;3,9.5"), "membrane region" },
				{ sim2, "/field", "&domain=" + MEM_2D + "&var=Dex", "is a volume variable, but 'Cyt_EC_membrane' is a membrane domain" },
				{ sim2, "/field", "&domain=Cyt&var=xy_PM", "is a membrane variable, but 'Cyt' is a volume domain" },
				{ sim2, "/grid", "&domain=nope", "unknown domain" },
		};
		for (String[] c : cases) {
			HttpResponse<String> r = send(c[0], c[1], c[2]);
			Assertions.assertEquals(400, r.statusCode(), c[2] + " → " + r.body());
			Assertions.assertTrue(r.body().contains(c[3]), c[2] + " → " + r.body());
		}
	}
}
