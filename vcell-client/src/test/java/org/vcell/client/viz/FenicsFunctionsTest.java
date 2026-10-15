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
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.vcell.solver.fenics.BundleStore;
import org.vcell.solver.fenics.FenicsBundle;

import cbit.vcell.parser.Expression;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * VCell functions of a FEniCSx run in the field viewer ({@link FenicsFunctions}): listed by {@code /info},
 * evaluated AT THE VERTICES from the stored variables, the vertex's x, y, z (a moving mesh's per row) and the row's
 * t, then interpolated -- so a kymograph or probe of a function is, bit for bit, the P1 interpolation of the
 * {@code /field} vertex values, the order that agrees with the 3D view (and not the function of interpolated
 * variables). Read from disk, through a sampling data server, on a moving mesh and on a remeshed run; and the
 * functions the bundle cannot supply are refused with a message.
 */
@Tag("Fast")
@ResourceLock("fieldViewerServer")
public class FenicsFunctionsTest {

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

	private static File resource(String name) throws Exception {
		return new File(FenicsFunctionsTest.class.getResource(name + "/.zattrs").toURI()).getParentFile();
	}

	private static FenicsFunctions.Definition def(String name, String exp, String domain, boolean membrane) throws Exception {
		return new FenicsFunctions.Definition(name, new Expression(exp), domain, membrane, null);
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

	/** the function at every vertex, computed here in plain Java: the reference */
	interface Nodal {
		double at(double t, double x, double y, double z, double[] args);
	}

	/** vertex values of a function of {@code vars} at {@code row}, computed in plain Java from the bundle */
	private static double[] nodal(FenicsBundleViews.BundleSource source, FenicsBundle bundle, String domain, String[] vars, int row,
			Nodal f) throws Exception {
		double[][] values = new double[vars.length][];
		for (int k = 0; k < vars.length; k++) {
			values[k] = bundle.field(domain, vars[k], row);
		}
		VtuGridParser.VtuGrid grid = source.grid(bundle, domain, row);
		double t = bundle.getTimes().get(row);
		double[] out = new double[grid.numPoints()];
		double[] args = new double[vars.length];
		for (int v = 0; v < out.length; v++) {
			for (int k = 0; k < vars.length; k++) {
				args[k] = values[k][v];
			}
			out[v] = f.at(t, grid.points[3 * v], grid.points[3 * v + 1], grid.points[3 * v + 2], args);
		}
		return out;
	}

	private static double[] doubles(JsonArray a) {
		double[] d = new double[a.size()];
		for (int i = 0; i < d.length; i++) {
			d[i] = a.get(i).isJsonNull() ? Double.NaN : a.get(i).getAsDouble();
		}
		return d;
	}

	/**
	 * The reference kymograph: the response's own samples and rows, P1-interpolated (PointSeries) from the
	 * vertex values {@code nodalAt} gives per row.
	 */
	private static double[][] reference(FenicsBundleViews.BundleSource source, FenicsBundle bundle, String domain, JsonObject k,
			java.util.function.IntFunction<double[]> nodalAt) throws Exception {
		double[] flat = doubles(k.getAsJsonObject("samples").getAsJsonArray("points"));
		double[][] points = new double[flat.length / 3][];
		for (int i = 0; i < points.length; i++) {
			points[i] = new double[] { flat[3 * i], flat[3 * i + 1], flat[3 * i + 2] };
		}
		JsonArray idx = k.getAsJsonArray("timeIndices");
		int[] rows = new int[idx.size()];
		for (int r = 0; r < rows.length; r++) {
			rows[r] = idx.get(r).getAsInt();
		}
		PointSeries.Result result = PointSeries.sample(rows.length, points, new PointSeries.Rows() {
			@Override
			public VtuGridParser.VtuGrid grid(int r) throws Exception {
				return source.grid(bundle, domain, rows[r]);
			}

			@Override
			public double[] values(int r) {
				return nodalAt.apply(rows[r]);
			}
		}, PointSeries.Location.POINT, false);
		return result.values; // [sample][row]
	}

	/** asserts the kymograph's values are the reference's, bit for bit (null where NaN) */
	private static void assertBitIdentical(double[][] expected, JsonObject k, String what) {
		JsonArray rows = k.getAsJsonArray("values");
		int compared = 0;
		for (int r = 0; r < rows.size(); r++) {
			JsonArray row = rows.get(r).getAsJsonArray();
			for (int i = 0; i < row.size(); i++) {
				JsonElement v = row.get(i);
				if (Double.isNaN(expected[i][r])) {
					Assertions.assertTrue(v.isJsonNull(), what + " sample " + i + " row " + r + " is a gap");
				} else {
					Assertions.assertEquals(Double.doubleToLongBits(expected[i][r]), Double.doubleToLongBits(v.getAsDouble()),
							what + " sample " + i + " row " + r + ": " + expected[i][r] + " vs " + v);
					compared++;
				}
			}
		}
		Assertions.assertTrue(compared > 0, what + ": compared no values");
	}

	private static FenicsBundleViews.BundleSource source(BundleStore store) {
		return new FenicsBundleViews.BundleSource("ref", 0, store, "ref");
	}

	/**
	 * {@code u·(x + 2)} on the 2D disk: {@code /info} lists it, {@code /field} is the plain-Java vertex values, and
	 * the kymograph is their P1 interpolation bit for bit -- from disk and through a sampling data server alike --
	 * while the function of the interpolated u and x differs.
	 */
	@Test
	public void aNonlinearFunctionIsEvaluatedAtTheVerticesThenInterpolated() throws Exception {
		File dir = resource("membrane_efflux.fenics");
		FenicsFunctions functions = new FenicsFunctions(List.of(def("ux", "u*(x+2)", "cytosol_dom", false)), Set.of());
		Nodal f = (t, x, y, z, a) -> a[0] * (x + 2);
		FieldViewerServer.registerBundle("9101", 0, BundleStore.directory(dir), "disk", functions);
		FieldViewerServer.registerBundle("9102", 0, SamplingBundleStore.of(dir), "sampling", functions);
		FenicsBundle bundle = FenicsBundle.open(dir);
		FenicsBundleViews.BundleSource ref = source(BundleStore.directory(dir));

		JsonObject info = get("9101", "/info", "");
		boolean listed = false;
		for (JsonElement e : info.getAsJsonArray("variables")) {
			JsonObject v = e.getAsJsonObject();
			if (v.get("name").getAsString().equals("ux")) {
				listed = true;
				Assertions.assertTrue(v.get("isFunction").getAsBoolean());
				Assertions.assertEquals("cytosol_dom", v.get("domain").getAsString());
			} else {
				Assertions.assertFalse(v.get("isFunction").getAsBoolean());
			}
		}
		Assertions.assertTrue(listed, info.toString());

		// /field: the vertex values
		JsonObject field = get("9101", "/field", "&var=ux&time=0.1");
		Assertions.assertEquals("cytosol_dom", field.get("domain").getAsString(), "a function's domain, found without being named");
		double[] expected = nodal(ref, bundle, "cytosol_dom", new String[] { "u" }, 1, f);
		double[] got = doubles(field.getAsJsonArray("values"));
		Assertions.assertEquals(expected.length, got.length);
		for (int v = 0; v < got.length; v++) {
			Assertions.assertEquals(Double.doubleToLongBits(expected[v]), Double.doubleToLongBits(got[v]), "vertex " + v);
		}

		String q = "&domain=cytosol_dom&var=ux&path=" + enc("-0.7,0.013;0.7,0.013");
		JsonObject kDisk = get("9101", "/kymograph", q);
		JsonObject kSampled = get("9102", "/kymograph", q);
		Assertions.assertEquals(kDisk.getAsJsonArray("values").toString(), kSampled.getAsJsonArray("values").toString(),
				"read whole from disk or sampled on the data server: the same numbers");
		double[][] reference = reference(ref, bundle, "cytosol_dom", kDisk, row -> {
			try {
				return nodal(ref, bundle, "cytosol_dom", new String[] { "u" }, row, f);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		assertBitIdentical(reference, kDisk, "u·(x+2)");

		// the other order -- the function of the interpolated u and x -- is not what is drawn
		double[][] uInterp = reference(ref, bundle, "cytosol_dom", kDisk, row -> {
			try {
				return bundle.field("cytosol_dom", "u", row);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		double[][] xInterp = reference(ref, bundle, "cytosol_dom", kDisk, row -> {
			try {
				return nodal(ref, bundle, "cytosol_dom", new String[0], row, (t, x, y, z, a) -> x);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		int differ = 0;
		for (int i = 0; i < reference.length; i++) {
			for (int r = 0; r < reference[i].length; r++) {
				double other = uInterp[i][r] * (xInterp[i][r] + 2);
				if (!Double.isNaN(other) && Math.abs(other - reference[i][r]) > 1e-12 * Math.max(1, Math.abs(other))) {
					differ++;
				}
			}
		}
		Assertions.assertTrue(differ > 0, "interpolate-then-evaluate differs somewhere for a nonlinear function");

		// a probe: its time course is the same interpolation
		JsonObject probe = get("9102", "/timeseries", "&var=ux&points=" + enc("0.1,0.2;-0.3,0.05"));
		JsonObject probeDisk = get("9101", "/timeseries", "&var=ux&points=" + enc("0.1,0.2;-0.3,0.05"));
		Assertions.assertEquals(probeDisk.get("series").toString(), probe.get("series").toString());
	}

	/** x, y, z and t: a linear function of position and time is reproduced exactly by P1, at vertices and between them */
	@Test
	public void positionAndTimeAreTheVertexsAndTheRows() throws Exception {
		File dir = resource("receptor_3d.fenics");
		FenicsFunctions functions = new FenicsFunctions(List.of(def("lin", "x + 2*y - 3*z + 10*t", "cyto_dom", false)), Set.of());
		FieldViewerServer.registerBundle("9103", 0, SamplingBundleStore.of(dir), "sampling", functions);
		FenicsBundle bundle = FenicsBundle.open(dir);
		FenicsBundleViews.BundleSource ref = source(BundleStore.directory(dir));
		int last = bundle.getTimes().size() - 1;
		double t = bundle.getTimes().get(last);
		double[] got = doubles(get("9103", "/field", "&domain=cyto_dom&var=lin").getAsJsonArray("values"));
		double[] p = ref.grid(bundle, "cyto_dom", last).points;
		for (int v = 0; v < got.length; v++) {
			Assertions.assertEquals(p[3 * v] + 2 * p[3 * v + 1] - 3 * p[3 * v + 2] + 10 * t, got[v], 1e-12, "vertex " + v);
		}
		double[] at = { 0.1, -0.05, 0.2 };
		JsonObject probe = get("9103", "/timeseries", "&domain=cyto_dom&var=lin&points=" + enc(at[0] + "," + at[1] + "," + at[2]));
		double[] series = doubles(probe.getAsJsonArray("series").get(0).getAsJsonObject().getAsJsonArray("values"));
		for (int r = 0; r < series.length; r++) {
			Assertions.assertEquals(at[0] + 2 * at[1] - 3 * at[2] + 10 * bundle.getTimes().get(r), series[r], 1e-9, "row " + r);
		}
	}

	/** a membrane function along a 2D membrane curve: the arc's interpolation of the function's vertex values */
	@Test
	public void aMembraneFunctionAlongAMembraneCurve() throws Exception {
		File dir = resource("receptor_2d.fenics");
		FenicsFunctions functions = new FenicsFunctions(List.of(def("Rsq", "R*R*(1+x*x)", "mem_dom", true)), Set.of());
		Nodal f = (t, x, y, z, a) -> a[0] * a[0] * (1 + x * x);
		FieldViewerServer.registerBundle("9104", 0, BundleStore.directory(dir), "disk", functions);
		FieldViewerServer.registerBundle("9105", 0, SamplingBundleStore.of(dir), "sampling", functions);
		FenicsBundle bundle = FenicsBundle.open(dir);
		FenicsBundleViews.BundleSource ref = source(BundleStore.directory(dir));
		String q = "&domain=mem_dom&var=Rsq&path=" + enc("0.4698463103929542,0.17101007166283436;-0.4698463103929542,0.17101007166283436");
		JsonObject k = get("9104", "/kymograph", q);
		Assertions.assertEquals(k.getAsJsonArray("values").toString(), get("9105", "/kymograph", q).getAsJsonArray("values").toString());
		VtuGridParser.VtuGrid grid = ref.grid(bundle, "mem_dom", 0);
		double z = grid.points[2];
		double[][] waypoints = { { 0.4698463103929542, 0.17101007166283436, z }, { -0.4698463103929542, 0.17101007166283436, z } };
		MembraneArc arc = MembraneArc.build(grid, waypoints, BodyFittedKymograph.MAX_SAMPLES);
		JsonArray rows = k.getAsJsonArray("values");
		for (int r = 0; r < rows.size(); r++) {
			double[] expected = arc.values(nodal(ref, bundle, "mem_dom", new String[] { "R" }, r, f));
			double[] got = doubles(rows.get(r).getAsJsonArray());
			Assertions.assertEquals(expected.length, got.length);
			for (int i = 0; i < got.length; i++) {
				Assertions.assertEquals(Double.doubleToLongBits(expected[i]), Double.doubleToLongBits(got[i]), "row " + r + " sample " + i);
			}
		}
	}

	/** a moving (ALE) mesh: x is the moved vertex's, per row; read from whole rows, bit for bit */
	@Test
	public void onAMovingMeshXIsThatRowsPosition() throws Exception {
		File dir = resource("moving_translate.fenics");
		FenicsFunctions functions = new FenicsFunctions(List.of(def("prod", "RanC_cyt*Ran_cyt + x", "cell", false)), Set.of());
		Nodal f = (t, x, y, z, a) -> a[0] * a[1] + x;
		FieldViewerServer.registerBundle("9106", 0, SamplingBundleStore.of(dir), "sampling", functions);
		FenicsBundle bundle = FenicsBundle.open(dir);
		FenicsBundleViews.BundleSource ref = source(BundleStore.directory(dir));
		JsonObject k = get("9106", "/kymograph", "&domain=cell&var=prod&samples=81&path=" + enc("1,5.01;9,5.01"));
		Assertions.assertTrue(k.get("movingMesh").getAsBoolean());
		double[][] reference = reference(ref, bundle, "cell", k, row -> {
			try {
				return nodal(ref, bundle, "cell", new String[] { "RanC_cyt", "Ran_cyt" }, row, f);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		assertBitIdentical(reference, k, "ALE");
	}

	/**
	 * A function of two stored variables on a remeshed run (vcell-core's {@code moving_remesh} with its segments
	 * marked static): both variables sampled in one call per segment, bit for bit.
	 */
	@Test
	public void aProductOfTwoVariablesOnARemeshedRun() throws Exception {
		File src = new File("../vcell-core/src/test/resources/org/vcell/solver/fenics/bundles/moving_remesh.fenics");
		Assertions.assertTrue(new File(src, ".zattrs").isFile(), src.getAbsolutePath());
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
		Files.writeString(dst.resolve(".zattrs"), Files.readString(dst.resolve(".zattrs")).replace("\"ale\"", "\"none\""));
		java.util.concurrent.atomic.AtomicInteger sampleCalls = new java.util.concurrent.atomic.AtomicInteger();
		java.util.concurrent.atomic.AtomicInteger arraysPerCall = new java.util.concurrent.atomic.AtomicInteger();
		BundleStore disk = BundleStore.directory(dst.toFile());
		BundleStore sampling = BundleStore.cached(new BundleStore() {
			@Override
			public byte[] read(String relativePath) throws java.io.IOException {
				return disk.read(relativePath);
			}

			@Override
			public org.vcell.solver.fenics.FenicsSamples sampleRows(String[] arrayPaths, int[] rows, int[] indices) throws java.io.IOException {
				sampleCalls.incrementAndGet();
				arraysPerCall.set(arrayPaths.length);
				return FenicsBundle.gather(disk, arrayPaths, indices, rows, FenicsBundle.MAX_SAMPLE_VALUES);
			}

			@Override
			public String describe() {
				return "sampling";
			}
		});
		FenicsFunctions functions = new FenicsFunctions(List.of(def("ab", "RanC_cyt*Ran_cyt", "cell", false)), Set.of());
		Nodal f = (t, x, y, z, a) -> a[0] * a[1];
		FieldViewerServer.registerBundle("9107", 0, sampling, "sampling", functions);
		FenicsBundle bundle = FenicsBundle.open(dst.toFile());
		FenicsBundleViews.BundleSource ref = source(disk);
		JsonObject k = get("9107", "/kymograph", "&domain=cell&var=ab&samples=81&path=" + enc("1,5.01;9,5.01"));
		Assertions.assertEquals(2, sampleCalls.get(), "one sampled call per segment");
		Assertions.assertEquals(2, arraysPerCall.get(), "both variables in the same call");
		double[][] reference = reference(ref, bundle, "cell", k, row -> {
			try {
				return nodal(ref, bundle, "cell", new String[] { "RanC_cyt", "Ran_cyt" }, row, f);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		assertBitIdentical(reference, k, "remeshed a·b");
	}

	/** what the bundle cannot supply is refused with a message, and not listed */
	@Test
	public void unsupportedFunctionsAreRefusedWithAReason() throws Exception {
		File dir = resource("receptor_2d.fenics");
		FenicsFunctions functions = new FenicsFunctions(List.of(
				def("nx", "R*normalX()", "mem_dom", true),
				def("size", "s_cyto/vcRegionVolume('cyto_dom')", "cyto_dom", false),
				def("fd", "s_cyto*vcField('fd','v',0.0,'Volume')", "cyto_dom", false),
				def("region", "s_cyto*RV", "cyto_dom", false),
				def("adjacent", "R*s_cyto", "mem_dom", true),
				def("ok", "s_cyto*2", "cyto_dom", false),
				new FenicsFunctions.Definition("broken", new Expression("1"), null, false, "it could not be flattened")),
				Set.of("RV"));
		FieldViewerServer.registerBundle("9108", 0, BundleStore.directory(dir), "disk", functions);
		Set<String> listed = new java.util.HashSet<>();
		for (JsonElement e : get("9108", "/info", "").getAsJsonArray("variables")) {
			listed.add(e.getAsJsonObject().get("name").getAsString());
		}
		Assertions.assertTrue(listed.contains("ok"));
		String[][] refused = {
				{ "nx", "mem_dom", "normal" },
				{ "size", "cyto_dom", "region size" },
				{ "fd", "cyto_dom", "field data" },
				{ "region", "cyto_dom", "region variable" },
				{ "adjacent", "mem_dom", "adjacent volume" },
				{ "broken", "cyto_dom", "could not be flattened" },
		};
		for (String[] c : refused) {
			Assertions.assertFalse(listed.contains(c[0]), c[0] + " is not offered");
			for (String route : new String[] { "/field", "/kymograph" }) {
				HttpResponse<String> r = send("9108", route, "&domain=" + c[1] + "&var=" + c[0] + "&path=" + enc("-0.5,0.01;0.5,0.01"));
				Assertions.assertEquals(400, r.statusCode(), c[0] + " " + route + ": " + r.body());
				Assertions.assertTrue(r.body().contains(c[2]), c[0] + " " + route + ": " + r.body());
			}
		}
		HttpResponse<String> stats = send("9108", "/stats", "&var=ok");
		Assertions.assertEquals(400, stats.statusCode());
		Assertions.assertTrue(stats.body().contains("functions have none"), stats.body());
	}
	/**
	 * Where the definitions come from: the simulation's MathDescription, flattened as a finite-volume run's
	 * {@code .functions} file is, with this job's constants substituted, and each function's membrane/volume
	 * kind and domain kept (vcell-core's Membrane_Frap model: membrane and volume functions).
	 */
	@Test
	public void definitionsComeFromTheSimulation() throws Exception {
		File vcml = new File("../vcell-core/src/test/resources/simdata/MembraneFrap3D/Membrane_Frap.vcml");
		Assertions.assertTrue(vcml.isFile(), vcml.getAbsolutePath());
		cbit.vcell.biomodel.BioModel bioModel = cbit.vcell.xml.XmlHelper.XMLToBioModel(new cbit.vcell.xml.XMLSource(Files.readString(vcml.toPath())));
		cbit.vcell.solver.Simulation sim = null;
		for (cbit.vcell.solver.Simulation s : bioModel.getSimulations()) {
			if (s.getMathDescription().isSpatial()) {
				sim = s;
				break;
			}
		}
		Assertions.assertNotNull(sim);
		FenicsFunctions functions = FenicsFunctions.fromSimulation(sim, 0);
		Assertions.assertFalse(functions.isEmpty());
		java.lang.reflect.Field f = FenicsFunctions.class.getDeclaredField("definitions");
		f.setAccessible(true);
		@SuppressWarnings("unchecked")
		java.util.Map<String, FenicsFunctions.Definition> defs = (java.util.Map<String, FenicsFunctions.Definition>) f.get(functions);
		cbit.vcell.math.MathDescription math = sim.getMathDescription();
		boolean sawMembrane = false, sawVolume = false;
		for (FenicsFunctions.Definition d : defs.values()) {
			cbit.vcell.math.Variable mathVar = math.getVariable(d.name());
			Assertions.assertTrue(mathVar instanceof cbit.vcell.math.Function, d.name() + " is a math function");
			if (d.domain() != null) {
				Assertions.assertEquals(mathVar.getDomain().getName(), d.domain(), d.name());
				boolean onMembrane = math.getSubDomain(d.domain()) instanceof cbit.vcell.math.MembraneSubDomain;
				if (d.error() == null) {
					Assertions.assertEquals(onMembrane, d.membrane(), d.name() + " kind");
				}
				sawMembrane |= onMembrane;
				sawVolume |= !onMembrane;
			}
			if (d.expression() != null && d.error() == null) {
				for (String symbol : d.expression().getSymbols() == null ? new String[0] : d.expression().getSymbols()) {
					cbit.vcell.math.Variable v = math.getVariable(symbol);
					boolean coordinate = Set.of("t", "x", "y", "z").contains(symbol);
					Assertions.assertTrue(coordinate || !(v instanceof cbit.vcell.math.Constant) && !(v instanceof cbit.vcell.math.Function),
							d.name() + ": '" + symbol + "' should be a state variable or a coordinate once flattened");
				}
			}
		}
		Assertions.assertTrue(sawMembrane && sawVolume, "both membrane and volume functions");
		Assertions.assertSame(FenicsFunctions.NONE, FenicsFunctions.fromSimulation(null, 0));
	}
}
