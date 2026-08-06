package org.vcell.client.viz;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.vis.mapping.vcell.CartesianMeshMapping;
import org.vcell.vis.vcell.CartesianMeshBuilder;
import org.vcell.vis.vcell.SubdomainInfo;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPoint;
import org.vcell.vis.vismesh.thrift.VisVoxel;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.VCDataManager;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;

/**
 * Serves finite-volume simulation results to a browser-based vtk.wasm field viewer.
 * <p>
 * Builds the whole-voxel unstructured grid with {@code org.vcell.vis} and emits the raw
 * points/cells/field arrays as JSON. All smoothing, slicing and contouring happens client-side in
 * vtk.wasm, so this server stays a thin byte pump — there is deliberately no VTK, Python or native
 * dependency here.
 * <p>
 * It reaches simulation data only through datasets that results windows {@link #register} with it,
 * so it never decides how to fetch a run: a window backed by a local run and one backed by a remote
 * run register the same way and are served by the same code. Requests are self-describing, naming
 * the dataset they want, which is what lets one server on one port drive several viewer windows at
 * once.
 * <p>
 * Binds to the loopback interface only. Never throws out of {@link #start()}: a visualization
 * convenience must not be able to take down the client.
 */
public final class FieldViewerServer {

	private static final Logger LG = LogManager.getLogger(FieldViewerServer.class);

	private static final int DEFAULT_PORT = 9124;

	/** VTK_VOXEL: the cell type {@link CartesianMeshMapping} emits for 3D volume domains. */
	private static final int VTK_VOXEL = 11;

	/**
	 * Smoothing parameters the client applies to the extracted boundary surface. These match
	 * the reference pipeline in {@code org.vcell.vis.mapping.vcell.CartesianMeshVtkFileWriter}
	 * (and pyvcell's {@code smooth_unstructured_grid_surface}) so the browser reproduces the
	 * same smoothed membrane the desktop/VTK path produces.
	 */
	private static final int SINC_ITERATIONS = 12;
	private static final double SINC_FEATURE_ANGLE = 120.0;
	private static final double SINC_PASS_BAND = 0.05;

	private static HttpServer server;

	/**
	 * Registered datasets, keyed by simulation and job. Comparing results side by side is normal,
	 * so several windows can be registered at once and each request names its own dataset. Keying
	 * by dataset rather than by window means opening the same results twice simply re-registers an
	 * equivalent source, which is why no reference counting is needed.
	 */
	private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();

	/** Grid construction is the expensive step and depends only on the mesh, so cache per sim+domain. */
	private static final Map<String, VisMesh> meshCache = new HashMap<>();

	/** A dataset a results window has made available, and the means of reading it. */
	private static final class DataSource {
		final VCSimulationDataIdentifier vcdID;
		final VCDataManager dataManager;
		final SubdomainInfo subdomainInfo;

		DataSource(VCSimulationDataIdentifier vcdID, VCDataManager dataManager, SubdomainInfo subdomainInfo) {
			this.vcdID = vcdID;
			this.dataManager = dataManager;
			this.subdomainInfo = subdomainInfo;
		}
	}

	/** Raised when a request names a dataset no window has registered; reported as 404. */
	private static final class NoSuchDatasetException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		NoSuchDatasetException(String message) {
			super(message);
		}
	}

	private FieldViewerServer() {
	}

	// ---------------------------------------------------------------------
	// Lifecycle
	// ---------------------------------------------------------------------

	/**
	 * Whether the browser-based 3D field viewer is switched on. Off unless
	 * {@code -Dvcell.fieldViewer.enabled=true} is set, which an installed client can carry in its
	 * {@code vmoptions.txt}. Gates both this server and the "View in 3D" button that opens it.
	 */
	public static boolean isEnabled() {
		return PropertyLoader.getBooleanProperty(PropertyLoader.fieldViewerEnabled,
				PropertyLoader.fieldViewerEnabled_default_value);
	}

	/**
	 * Start the server if it is not already running. Safe to call repeatedly.
	 *
	 * @return the listening port, -1 if the server could not be started, or -1 if the feature is
	 *         switched off.
	 */
	public static synchronized int start() {
		if (!isEnabled()) {
			LG.debug("field viewer is disabled ({}=false)", PropertyLoader.fieldViewerEnabled);
			return -1;
		}
		if (server != null) {
			return server.getAddress().getPort();
		}
		int configuredPort = PropertyLoader.getIntProperty(PropertyLoader.fieldViewerPort, DEFAULT_PORT);
		HttpServer s = bind(configuredPort);
		if (s == null) {
			// the fixed port is typically taken by a second client instance; any port will do
			s = bind(0);
		}
		if (s == null) {
			return -1;
		}
		s.createContext("/health", ex -> respond(ex, 200, "text/plain", "ok".getBytes(StandardCharsets.UTF_8)));
		s.createContext("/info", wrap(FieldViewerServer::handleInfo));
		s.createContext("/grid", wrap(FieldViewerServer::handleGrid));
		s.createContext("/field", wrap(FieldViewerServer::handleField));
		s.setExecutor(Executors.newFixedThreadPool(2, r -> {
			Thread t = new Thread(r, "vcell-field-viewer");
			t.setDaemon(true);
			return t;
		}));
		s.start();
		server = s;
		LG.info("VCell field viewer server listening on http://127.0.0.1:{}", s.getAddress().getPort());
		return s.getAddress().getPort();
	}

	private static HttpServer bind(int port) {
		try {
			return HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
		} catch (Exception e) {
			LG.debug("field viewer server could not bind port " + port, e);
			return null;
		}
	}

	/** Stop the server and forget every dataset. */
	public static synchronized void stop() {
		if (server != null) {
			server.stop(0);
			server = null;
		}
		meshCache.clear();
		dataSources.clear();
	}

	// ---------------------------------------------------------------------
	// Handlers
	// ---------------------------------------------------------------------

	/** {@code /info?sim=<simKey>&job=<n>} — the variables, times and domains available for a run. */
	private static String handleInfo(HttpExchange ex) throws Exception {
		DataSource source = sourceFor(query(ex));
		VCSimulationDataIdentifier vcdID = source.vcdID;

		double[] times = source.dataManager.getDataSetTimes(vcdID);
		DataIdentifier[] ids = source.dataManager.getDataIdentifiers(emptyOutputContext(), vcdID);
		List<String> domains = readMesh(source).getVolumeDomainNames();

		StringBuilder sb = new StringBuilder(1024);
		sb.append("{\"simId\":\"").append(jsonEscape(vcdID.getID())).append('"');
		sb.append(",\"times\":");
		appendDoubles(sb, times, times.length);
		sb.append(",\"domains\":[");
		for (int i = 0; i < domains.size(); i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append('"').append(jsonEscape(domains.get(i))).append('"');
		}
		sb.append("],\"variables\":[");
		boolean first = true;
		for (DataIdentifier id : ids) {
			if (!id.getVariableType().equals(cbit.vcell.math.VariableType.VOLUME)) {
				continue; // the viewer renders volume fields on the whole-voxel grid
			}
			if (!first) {
				sb.append(',');
			}
			first = false;
			sb.append("{\"name\":\"").append(jsonEscape(id.getName())).append('"');
			sb.append(",\"domain\":\"").append(jsonEscape(id.getDomain() == null ? "" : id.getDomain().getName())).append('"');
			sb.append(",\"isFunction\":").append(id.isFunction()).append('}');
		}
		sb.append("]}");
		return sb.toString();
	}

	/**
	 * {@code /grid?sim=<simKey>&job=<n>&domain=<name>} — the geometry alone.
	 * <p>
	 * Deliberately NOT documented as immutable for a dataset: it is the geometry <em>for this
	 * dataset at this time</em>, which happens to be constant for a fixed grid but will not be once
	 * moving-boundary runs arrive, where vertices move per frame and topology changes at each
	 * remesh. Callers should re-request it and let the ETag and {@code Cache-Control} decide whether
	 * that costs a round trip, rather than assuming it never changes.
	 */
	private static String handleGrid(HttpExchange ex) throws Exception {
		Map<String, String> q = query(ex);
		DataSource source = sourceFor(q);
		String domain = domainOf(q, source);
		VisMesh visMesh = grid(source, domain);

		// let the cache decide whether a re-request costs a round trip. Short max-age plus an ETag
		// rather than "immutable": a moving-boundary run's geometry does change over time, and a
		// client that had cached it forever would draw a stale shape.
		ex.getResponseHeaders().set("ETag", '"' + geometryId(source, domain) + '"');
		ex.getResponseHeaders().set("Cache-Control", "public, max-age=60");

		List<VisPoint> points = visMesh.getPoints();
		List<VisVoxel> voxels = visMesh.getVisVoxels();

		StringBuilder sb = new StringBuilder(32 * points.size() + 32 * voxels.size() + 512);
		sb.append("{\"geometryId\":\"").append(jsonEscape(geometryId(source, domain))).append('"');
		sb.append(",\"numPoints\":").append(points.size());
		sb.append(",\"points\":[");
		for (int i = 0; i < points.size(); i++) {
			VisPoint p = points.get(i);
			if (i > 0) {
				sb.append(',');
			}
			sb.append(p.getX()).append(',').append(p.getY()).append(',').append(p.getZ());
		}
		sb.append("],\"cellType\":").append(VTK_VOXEL);
		sb.append(",\"cells\":[");
		for (int c = 0; c < voxels.size(); c++) {
			List<Integer> idx = voxels.get(c).getPointIndices();
			if (c > 0) {
				sb.append(',');
			}
			sb.append('[');
			for (int v = 0; v < idx.size(); v++) {
				if (v > 0) {
					sb.append(',');
				}
				sb.append(idx.get(v).intValue());
			}
			sb.append(']');
		}
		sb.append(']');
		sb.append(",\"domain\":\"").append(jsonEscape(domain)).append('"');
		sb.append(",\"sinc\":{\"iterations\":").append(SINC_ITERATIONS)
			.append(",\"feature_angle\":").append(SINC_FEATURE_ANGLE)
			.append(",\"pass_band\":").append(SINC_PASS_BAND).append('}');
		sb.append('}');
		return sb.toString();
	}

	/**
	 * {@code /field?sim=<simKey>&job=<n>&domain=<name>&var=<name>&time=<t>} — one variable at one
	 * time, as one value per grid cell.
	 * <p>
	 * Split from the geometry because the geometry does not change as the viewer scrubs time or
	 * switches variable: shipping both together would move roughly seven times the bytes per step.
	 * <p>
	 * Carries the {@code geometryId} these values belong to. A client that pairs values with a
	 * different geometry draws something silently wrong rather than obviously broken, which is a
	 * real hazard once vertices move over time, so the pairing is explicit.
	 */
	private static String handleField(HttpExchange ex) throws Exception {
		Map<String, String> q = query(ex);
		DataSource source = sourceFor(q);
		String domain = domainOf(q, source);
		String varName = q.get("var");
		if (varName == null || varName.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'var'");
		}
		double time = parseTime(q, source);
		List<VisVoxel> voxels = grid(source, domain).getVisVoxels();

		// each cell carries the mesh's global index of the voxel it came from, which is the position
		// of that cell's value in the solver's data array
		SimDataBlock block = source.dataManager.getSimDataBlock(emptyOutputContext(), source.vcdID, varName, time);
		double[] meshData = block.getData();
		double[] values = new double[voxels.size()];
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int c = 0; c < voxels.size(); c++) {
			double value = meshData[voxels.get(c).getFiniteVolumeIndex().getGlobalIndex()];
			values[c] = value;
			if (!Double.isNaN(value)) {
				min = Math.min(min, value);
				max = Math.max(max, value);
			}
		}
		if (min > max) { // every value was NaN
			min = 0;
			max = 0;
		}

		StringBuilder sb = new StringBuilder(16 * values.length + 256);
		sb.append("{\"geometryId\":\"").append(jsonEscape(geometryId(source, domain))).append('"');
		sb.append(",\"name\":\"").append(jsonEscape(varName)).append('"');
		sb.append(",\"domain\":\"").append(jsonEscape(domain)).append('"');
		sb.append(",\"time\":").append(time);
		sb.append(",\"location\":\"cell\",\"values\":");
		appendDoubles(sb, values, values.length);
		sb.append(",\"range\":[").append(min).append(',').append(max).append("]}");
		return sb.toString();
	}

	/** Resolves the requested domain, defaulting to the first, and rejecting names the run lacks. */
	private static String domainOf(Map<String, String> q, DataSource source) throws Exception {
		List<String> domains = readMesh(source).getVolumeDomainNames();
		if (domains.isEmpty()) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no volume domains");
		}
		String domain = q.get("domain");
		if (domain == null || domain.isEmpty()) {
			return domains.get(0);
		}
		if (!domains.contains(domain)) {
			// an unknown name otherwise yields an empty mesh and a confusing NPE downstream
			throw new IllegalArgumentException("unknown volume domain '" + domain + "'; this run has " + domains);
		}
		return domain;
	}

	/**
	 * Identifies the geometry a response belongs to. Today a dataset and domain pin it down; for a
	 * moving boundary this is where the ALE segment would join the key.
	 */
	private static String geometryId(DataSource source, String domain) {
		return source.vcdID.getID() + "/" + domain;
	}

	// ---------------------------------------------------------------------
	// Data access
	// ---------------------------------------------------------------------

	/**
	 * Make a dataset available to the viewer. The caller supplies the data manager it already uses
	 * for that run, which is what keeps local and remote runs on one code path here.
	 *
	 * @param mathDesc supplies the domain names; the mesh carries subvolume numbers but not names
	 */
	public static void register(VCSimulationDataIdentifier vcdID, VCDataManager dataManager,
			MathDescription mathDesc) throws MathException {
		register(vcdID, dataManager, CartesianMeshBuilder.fromMathDescription(mathDesc));
	}

	/** For callers that already hold the domain naming and have no math description to hand. */
	public static void register(VCSimulationDataIdentifier vcdID, VCDataManager dataManager,
			SubdomainInfo subdomainInfo) {
		dataSources.put(key(vcdID), new DataSource(vcdID, dataManager, subdomainInfo));
		LG.debug("field viewer dataset registered: {}", vcdID.getID());
	}

	/** Drop a dataset, so later requests fail cleanly rather than serving results nobody is viewing. */
	public static synchronized void unregister(VCSimulationDataIdentifier vcdID) {
		if (dataSources.remove(key(vcdID)) != null) {
			meshCache.keySet().removeIf(cached -> cached.startsWith(vcdID.getID() + "/"));
		}
	}

	private static String key(VCSimulationDataIdentifier vcdID) {
		return vcdID.getVcSimID().getSimulationKey() + ":" + vcdID.getJobIndex();
	}

	private static DataSource sourceFor(Map<String, String> q) {
		String sim = q.get("sim");
		if (sim == null || sim.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'sim'");
		}
		String job = q.containsKey("job") ? q.get("job") : "0";
		DataSource source = dataSources.get(sim + ":" + job);
		if (source == null) {
			throw new NoSuchDatasetException("simulation " + sim + " (job " + job + ") is not open in "
					+ "the client; its results window may have been closed");
		}
		return source;
	}

	/**
	 * Builds the visualization mesh from the solver mesh the data manager hands back, rather than
	 * from the simulation's files: the files exist only where the run executed, whereas the mesh
	 * arrives over whatever transport that window already uses. Note the two mesh types are
	 * different — {@link CartesianMeshMapping} consumes {@code org.vcell.vis.vcell.CartesianMesh},
	 * not the {@code cbit.vcell.solvers.CartesianMesh} that comes back here — which is what
	 * {@link CartesianMeshBuilder} bridges.
	 */
	private static org.vcell.vis.vcell.CartesianMesh readMesh(DataSource source) throws Exception {
		return CartesianMeshBuilder.fromSolverMesh(source.dataManager.getMesh(source.vcdID), source.subdomainInfo);
	}

	private static synchronized VisMesh grid(DataSource source, String domainName) throws Exception {
		String key = source.vcdID.getID() + "/" + domainName;
		VisMesh cached = meshCache.get(key);
		if (cached != null) {
			return cached;
		}
		VisMesh visMesh = new CartesianMeshMapping().fromMeshData(readMesh(source), domainName, true);
		meshCache.put(key, visMesh);
		return visMesh;
	}

	/** Snaps the requested time to the nearest saved time; defaults to the last one. */
	private static double parseTime(Map<String, String> q, DataSource source) throws Exception {
		double[] times = source.dataManager.getDataSetTimes(source.vcdID);
		if (times == null || times.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}
		String requested = q.get("time");
		if (requested == null || requested.isEmpty()) {
			return times[times.length - 1];
		}
		double target = Double.parseDouble(requested);
		double best = times[0];
		for (double t : times) {
			if (Math.abs(t - target) < Math.abs(best - target)) {
				best = t;
			}
		}
		return best;
	}

	private static OutputContext emptyOutputContext() {
		return new OutputContext(new AnnotatedFunction[0]);
	}

	// ---------------------------------------------------------------------
	// HTTP helpers
	// ---------------------------------------------------------------------

	private interface ThrowingHandler {
		String handle(HttpExchange ex) throws Exception;
	}

	private static HttpHandler wrap(ThrowingHandler h) {
		return ex -> {
			try {
				respond(ex, 200, "application/json", h.handle(ex).getBytes(StandardCharsets.UTF_8));
			} catch (NoSuchDatasetException e) {
				respond(ex, 404, "application/json",
						("{\"error\":\"" + jsonEscape(e.getMessage()) + "\"}").getBytes(StandardCharsets.UTF_8));
			} catch (IllegalArgumentException e) {
				// a malformed request, not a server fault - report it as such and don't log a stack trace
				respond(ex, 400, "application/json",
						("{\"error\":\"" + jsonEscape(e.getMessage()) + "\"}").getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				LG.error("field viewer handler error for " + ex.getRequestURI(), e);
				String msg = e.getClass().getSimpleName() + ": " + e.getMessage();
				respond(ex, 500, "application/json", ("{\"error\":\"" + jsonEscape(msg) + "\"}").getBytes(StandardCharsets.UTF_8));
			}
		};
	}

	private static void respond(HttpExchange ex, int status, String contentType, byte[] body) throws IOException {
		ex.getResponseHeaders().set("Content-Type", contentType);
		// the viewer page is served from a different origin (the webapp), so it must be allowed to read this
		ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
		ex.sendResponseHeaders(status, body.length);
		try (OutputStream os = ex.getResponseBody()) {
			os.write(body);
		}
	}

	/** JSON has no NaN or Infinity literal, so non-finite values (blanked cells) go out as null. */
	private static void appendDoubles(StringBuilder sb, double[] values, int count) {
		sb.append('[');
		for (int i = 0; i < count; i++) {
			if (i > 0) {
				sb.append(',');
			}
			if (Double.isFinite(values[i])) {
				sb.append(values[i]);
			} else {
				sb.append("null");
			}
		}
		sb.append(']');
	}

	private static Map<String, String> query(HttpExchange ex) {
		Map<String, String> map = new HashMap<>();
		URI uri = ex.getRequestURI();
		String raw = uri.getRawQuery();
		if (raw == null || raw.isEmpty()) {
			return map;
		}
		for (String pair : raw.split("&")) {
			int eq = pair.indexOf('=');
			if (eq < 0) {
				map.put(urlDecode(pair), "");
			} else {
				map.put(urlDecode(pair.substring(0, eq)), urlDecode(pair.substring(eq + 1)));
			}
		}
		return map;
	}

	private static String urlDecode(String s) {
		return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
	}

	private static String jsonEscape(String s) {
		return String.valueOf(s).replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
