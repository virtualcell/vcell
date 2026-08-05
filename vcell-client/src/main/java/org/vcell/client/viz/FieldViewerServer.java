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
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.vis.io.CartesianMeshFileReader;
import org.vcell.vis.io.VCellSimFiles;
import org.vcell.vis.mapping.vcell.CartesianMeshMapping;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPoint;
import org.vcell.vis.vismesh.thrift.VisVoxel;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;

/**
 * Serves finite-volume simulation results to a browser-based vtk.wasm field viewer.
 * <p>
 * Reads a local run straight out of {@code ~/.vcell/simdata} in-process (no RPC), builds the
 * whole-voxel unstructured grid with {@code org.vcell.vis}, and emits the raw points/cells/field
 * arrays as JSON. All smoothing, slicing and contouring happens client-side in vtk.wasm, so this
 * server stays a thin, stateless byte pump — there is deliberately no VTK, Python or native
 * dependency here.
 * <p>
 * Binds to the loopback interface only. Never throws out of {@link #start()}: a visualization
 * convenience must not be able to take down the client.
 */
public final class FieldViewerServer {

	private static final Logger LG = LogManager.getLogger(FieldViewerServer.class);

	private static final String PORT_PROP = "vcell.fieldViewer.port";
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
	private static DataSetControllerImpl dataSetController;

	/** Grid construction is the expensive step and depends only on the mesh, so cache per sim+domain. */
	private static final Map<String, VisMesh> meshCache = new HashMap<>();

	private FieldViewerServer() {
	}

	// ---------------------------------------------------------------------
	// Lifecycle
	// ---------------------------------------------------------------------

	/**
	 * Start the server if it is not already running. Safe to call repeatedly.
	 *
	 * @return the listening port, or -1 if the server could not be started.
	 */
	public static synchronized int start() {
		if (server != null) {
			return server.getAddress().getPort();
		}
		int configuredPort = Integer.getInteger(PORT_PROP, DEFAULT_PORT);
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

	/** Stop the server and drop cached grids. */
	public static synchronized void stop() {
		if (server != null) {
			server.stop(0);
			server = null;
		}
		meshCache.clear();
		dataSetController = null;
	}

	// ---------------------------------------------------------------------
	// Handlers
	// ---------------------------------------------------------------------

	/** {@code /info?sim=<simKey>&job=<n>} — the variables, times and domains available for a run. */
	private static String handleInfo(HttpExchange ex) throws Exception {
		Map<String, String> q = query(ex);
		VCSimulationDataIdentifier vcdID = simDataId(q);
		DataSetControllerImpl dsci = controller();

		double[] times = dsci.getDataSetTimes(vcdID);
		DataIdentifier[] ids = dsci.getDataIdentifiers(emptyOutputContext(), vcdID);
		List<String> domains = readMesh(dsci, vcdID).getVolumeDomainNames();

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
	 * {@code /grid?sim=<simKey>&job=<n>&domain=<name>[&var=<name>&time=<t>]} — the raw grid, and
	 * optionally one variable's values at one time attached as cell data.
	 */
	private static String handleGrid(HttpExchange ex) throws Exception {
		Map<String, String> q = query(ex);
		VCSimulationDataIdentifier vcdID = simDataId(q);
		DataSetControllerImpl dsci = controller();

		List<String> domains = readMesh(dsci, vcdID).getVolumeDomainNames();
		if (domains.isEmpty()) {
			throw new IllegalArgumentException("run " + vcdID.getID() + " has no volume domains");
		}
		String domain = q.get("domain");
		if (domain == null || domain.isEmpty()) {
			domain = domains.get(0);
		} else if (!domains.contains(domain)) {
			// an unknown name otherwise yields an empty mesh and a confusing NPE downstream
			throw new IllegalArgumentException("unknown volume domain '" + domain + "'; this run has " + domains);
		}
		VisMesh visMesh = grid(dsci, vcdID, domain);

		List<VisPoint> points = visMesh.getPoints();
		List<VisVoxel> voxels = visMesh.getVisVoxels();

		StringBuilder sb = new StringBuilder(32 * points.size() + 32 * voxels.size() + 512);
		sb.append("{\"numPoints\":").append(points.size());
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

		String varName = q.get("var");
		if (varName != null && !varName.isEmpty()) {
			double time = parseTime(q, dsci, vcdID);
			appendField(sb, dsci, vcdID, voxels, varName, time);
			sb.append(",\"time\":").append(time);
		}
		sb.append(",\"domain\":\"").append(jsonEscape(domain)).append('"');
		sb.append(",\"sinc\":{\"iterations\":").append(SINC_ITERATIONS)
			.append(",\"feature_angle\":").append(SINC_FEATURE_ANGLE)
			.append(",\"pass_band\":").append(SINC_PASS_BAND).append('}');
		sb.append('}');
		return sb.toString();
	}

	/**
	 * Reads one variable at one time and remaps it from mesh order onto grid cells. The grid holds
	 * only the cells of the requested domain, so each cell carries the mesh's global index of the
	 * voxel it came from — that index is the position in the solver's data array.
	 */
	private static void appendField(StringBuilder sb, DataSetControllerImpl dsci, VCSimulationDataIdentifier vcdID,
			List<VisVoxel> voxels, String varName, double time) throws Exception {
		SimDataBlock block = dsci.getSimDataBlock(emptyOutputContext(), vcdID, varName, time);
		double[] meshData = block.getData();

		double[] values = new double[voxels.size()];
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int c = 0; c < voxels.size(); c++) {
			int globalIndex = voxels.get(c).getFiniteVolumeIndex().getGlobalIndex();
			double value = meshData[globalIndex];
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

		sb.append(",\"field\":{\"name\":\"").append(jsonEscape(varName)).append('"');
		sb.append(",\"location\":\"cell\",\"values\":");
		appendDoubles(sb, values, values.length);
		sb.append(",\"range\":[").append(min).append(',').append(max).append("]}");
	}

	// ---------------------------------------------------------------------
	// Data access
	// ---------------------------------------------------------------------

	private static synchronized DataSetControllerImpl controller() throws IOException {
		if (dataSetController == null) {
			dataSetController = new DataSetControllerImpl(null, ResourceUtil.getLocalRootDir(), null);
		}
		return dataSetController;
	}

	/**
	 * The mesh flavour matters: {@link CartesianMeshMapping} consumes
	 * {@code org.vcell.vis.vcell.CartesianMesh} from the vis reader, which is a different type from
	 * the {@code cbit.vcell.solvers.CartesianMesh} that {@code DataSetControllerImpl.getMesh} returns.
	 */
	private static org.vcell.vis.vcell.CartesianMesh readMesh(DataSetControllerImpl dsci,
			VCSimulationDataIdentifier vcdID) throws Exception {
		VCellSimFiles simFiles = dsci.getVCellSimFiles(vcdID);
		return new CartesianMeshFileReader().readFromFiles(simFiles);
	}

	private static synchronized VisMesh grid(DataSetControllerImpl dsci, VCSimulationDataIdentifier vcdID,
			String domainName) throws Exception {
		String key = vcdID.getID() + "/" + domainName;
		VisMesh cached = meshCache.get(key);
		if (cached != null) {
			return cached;
		}
		VisMesh visMesh = new CartesianMeshMapping().fromMeshData(readMesh(dsci, vcdID), domainName, true);
		meshCache.put(key, visMesh);
		return visMesh;
	}

	/** Local runs are owned by {@link User#tempUser}, which selects the {@code temp} simdata subdirectory. */
	private static VCSimulationDataIdentifier simDataId(Map<String, String> q) {
		String sim = q.get("sim");
		if (sim == null || sim.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'sim'");
		}
		if (!sim.chars().allMatch(Character::isDigit)) {
			throw new IllegalArgumentException("'sim' must be a numeric simulation key, got '" + sim + "'");
		}
		int job = q.containsKey("job") ? Integer.parseInt(q.get("job")) : 0;
		return new VCSimulationDataIdentifier(new VCSimulationIdentifier(new KeyValue(sim), User.tempUser), job);
	}

	/** Snaps the requested time to the nearest saved time; defaults to the last one. */
	private static double parseTime(Map<String, String> q, DataSetControllerImpl dsci, VCSimulationDataIdentifier vcdID)
			throws Exception {
		double[] times = dsci.getDataSetTimes(vcdID);
		if (times == null || times.length == 0) {
			throw new IllegalArgumentException("run " + vcdID.getID() + " has no saved times");
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
