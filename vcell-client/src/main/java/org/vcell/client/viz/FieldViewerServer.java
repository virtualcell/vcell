package org.vcell.client.viz;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.TSJobResultsNoStats;
import org.vcell.util.document.TSJobResultsSpaceStats;
import org.vcell.util.document.TimeSeriesJobSpec;
import org.vcell.util.document.VCDataJobID;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;
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
import cbit.vcell.solvers.CartesianMeshChombo;
import cbit.vcell.solvers.CartesianMeshMovingBoundary;
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

	/** VTK_QUAD: the in-plane cell type it emits for 2D volume domains, and for the faces of a 3D membrane. */
	private static final int VTK_QUAD = 9;

	/** VTK_LINE: the cell type of a 2D membrane, one segment per membrane element. */
	private static final int VTK_LINE = 3;

	private static final String INDEX_HTML = "index.html";

	/**
	 * Default smoothing parameters for the viewer's deformed display mesh. Feature angle and
	 * pass band match the reference pipeline in
	 * {@code org.vcell.vis.mapping.vcell.CartesianMeshVtkFileWriter} (and pyvcell's
	 * {@code smooth_unstructured_grid_surface}); the iteration count is deliberately 16 where
	 * the pyvcell/VisIt reference uses 12 — chosen by eye in the installed client as the better
	 * default. The viewer's smoothing slider can still reach the 12-iteration reference.
	 */
	private static final int SINC_ITERATIONS = 16;
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

	/**
	 * FEniCSx results bundles, keyed like {@link #dataSources}. They are read straight from their
	 * directory, not through a {@link VCDataManager}: a bundle is not a VCell dataset.
	 */
	private static final Map<String, FenicsBundleViews.BundleSource> bundleSources = new ConcurrentHashMap<>();

	/** Grid construction is the expensive step and depends only on the mesh, so cache per sim+domain. */
	private static final Map<String, VisMesh> meshCache = new HashMap<>();

	/** A dataset a results window has made available, and the means of reading it. */
	private static final class DataSource {
		final VCSimulationDataIdentifier vcdID;
		final VCDataManager dataManager;
		final SubdomainInfo subdomainInfo;
		/** Human-readable name of the run, e.g. "model::app::sim"; may be null — the ID always exists, a name may not. */
		final String simName;
		/** Lazily detected; non-null for runs served through the VTU seam (see {@link VtuMode}). */
		volatile VtuMode vtuMode;
		volatile boolean vtuModeResolved;
		volatile VtuVarInfo[] vtuVarInfos;
		/** the membrane domains with membrane elements, read once from the mesh */
		volatile List<String> membraneDomains;
		/*
		 * A run's mesh and variables do not change while it is registered, and for a run on the data server each
		 * read is a round trip carrying the whole mesh (about 0.6 MB for 101 × 101 × 36). They were read again by
		 * every request, up to three times by one kymograph; now once per dataset (see solverMesh, visMesh and
		 * dataIdentifiers). The saved times are still read per request: a running simulation adds to them.
		 */
		/** the solver's mesh, read once */
		volatile cbit.vcell.solvers.CartesianMesh solverMesh;
		/** the solver's mesh with its domains named, built once from {@link #solverMesh} */
		volatile org.vcell.vis.vcell.CartesianMesh visMesh;
		/** the run's variables and functions, read once (the viewer always asks with an empty output context) */
		volatile DataIdentifier[] dataIdentifiers;
		/** the last few kymographs' time series ({@link #kymographSeries}), most recently used last */
		final Map<String, double[][]> kymographSeries = new java.util.LinkedHashMap<>(8, 0.75f, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<String, double[][]> eldest) {
				return size() > KYMOGRAPH_SERIES_KEPT;
			}
		};

		DataSource(VCSimulationDataIdentifier vcdID, VCDataManager dataManager, SubdomainInfo subdomainInfo,
				String simName) {
			this.vcdID = vcdID;
			this.dataManager = dataManager;
			this.subdomainInfo = subdomainInfo;
			this.simName = simName;
		}
	}

	/**
	 * Runs served through the VTU-era {@link VCDataManager} seam rather than the raw Cartesian
	 * path: body-fitted meshes the solver itself produced. MovingBoundary meshes change per saved
	 * time; Chombo (retired solver, but its stored solutions remain viewable) has one static mesh
	 * with per-time values.
	 */
	private enum VtuMode {
		TIME_VARYING, // MovingBoundary: a different geometry at every saved time
		STATIC // Chombo: one embedded-boundary mesh, values vary over time
	}

	/**
	 * Detected from the mesh type the data manager returns rather than from the simulation, so it
	 * works identically for a re-opened remote run where only the data still exists. Chombo runs
	 * only on the server, where its .vtu is written by the Python VTK service; MovingBoundary also
	 * runs locally (a desktop quick run), and its .vtu is written in pure Java on either side.
	 */
	private static VtuMode vtuMode(DataSource source) throws Exception {
		if (!source.vtuModeResolved) {
			Object mesh = solverMesh(source);
			source.vtuMode = mesh instanceof CartesianMeshMovingBoundary ? VtuMode.TIME_VARYING
					: mesh instanceof CartesianMeshChombo ? VtuMode.STATIC : null;
			source.vtuModeResolved = true;
		}
		return source.vtuMode;
	}

	private static VtuVarInfo[] vtuVarInfos(DataSource source) throws Exception {
		VtuVarInfo[] infos = source.vtuVarInfos;
		if (infos == null) {
			infos = source.dataManager.getVtuVarInfos(emptyOutputContext(), source.vcdID);
			source.vtuVarInfos = infos;
		}
		return infos;
	}

	/** Raised when a request names a dataset no window has registered; reported as 404. */
	private static final class NoSuchDatasetException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		NoSuchDatasetException(String message) {
			super(message);
		}
	}

	/** Raised when a heavy job is already running; reported as 503 so the viewer retries. */
	private static final class BusyException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		BusyException(String message) {
			super(message);
		}
	}

	/**
	 * Raised when a kymograph would return more values than {@link #maxKymographValues()}; a 400 whose JSON
	 * also carries {@code suggestedTstep}, the smallest stride over the saved times that fits.
	 */
	static final class TooManyValuesException extends IllegalArgumentException {
		private static final long serialVersionUID = 1L;
		final int suggestedTstep;

		TooManyValuesException(String message, int suggestedTstep) {
			super(message);
			this.suggestedTstep = suggestedTstep;
		}
	}

	/**
	 * Heavy jobs run one at a time: a kymograph, and a multi-point time series on Chombo or MovingBoundary
	 * (which reads every saved time over the remote seam). One more may wait for its turn, up to
	 * {@link #heavyWaitMillis()}, in {@link #HEAVY_JOB_WAITER}; any other gets a 503 at once, so a long job
	 * cannot pile work up behind it. One waiter is enough for what the viewer itself sends together — a
	 * variable switch asks for the probes' series and the kymograph at the same moment — which used to
	 * fail one of the two with "busy".
	 */
	static final Semaphore HEAVY_JOBS = new Semaphore(1);

	/** the one request allowed to wait for {@link #HEAVY_JOBS} */
	static final Semaphore HEAVY_JOB_WAITER = new Semaphore(1);

	/** How long a heavy request waits for the running one before a 503 ({@code vcell.fieldViewer.heavyWaitMillis}). */
	static long heavyWaitMillis() {
		return Long.getLong("vcell.fieldViewer.heavyWaitMillis", 30_000L);
	}

	private interface HeavyJob {
		String run() throws Exception;
	}

	private static String heavy(HeavyJob job) throws Exception {
		return heavy(job, null);
	}

	/** {@link #heavy(HeavyJob)}, noting on {@code timer} the moment the job starts. */
	private static String heavy(HeavyJob job, StageTimer timer) throws Exception {
		if (!HEAVY_JOBS.tryAcquire()) {
			if (!HEAVY_JOB_WAITER.tryAcquire()) {
				throw new BusyException("the field viewer is busy with another kymograph or time series; try again");
			}
			try {
				if (!HEAVY_JOBS.tryAcquire(heavyWaitMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)) {
					throw new BusyException("the field viewer is busy with another kymograph or time series; try again");
				}
			} finally {
				HEAVY_JOB_WAITER.release();
			}
		}
		try {
			if (timer != null) {
				timer.lap("acquire");
			}
			return job.run();
		} finally {
			HEAVY_JOBS.release();
		}
	}

	/** How many kymographs' time series each dataset keeps ({@link #kymographSeries}). */
	static final int KYMOGRAPH_SERIES_KEPT = 4;

	/** How many kymograph time-series jobs have been run, not answered from memory ({@link #kymographSeries}); for tests. */
	static final java.util.concurrent.atomic.AtomicInteger kymographJobsRun = new java.util.concurrent.atomic.AtomicInteger();

	/**
	 * One kymograph's values: {@code timesAndValues} (row 0 the times, row 1 + i the values at sample i) of ONE
	 * {@link TimeSeriesJobSpec} over the samples' volume indices, with their membrane-crossing indices when not
	 * null, or over membrane indices for a membrane variable.
	 * <p>
	 * The job reads every returned saved time, twice with membrane crossings (once more for the values at the
	 * crossings): that is nearly all of a kymograph's time, about 1 s for 101 times of a 101 × 101 × 36 run read
	 * locally. So the last few are kept per dataset, keyed by everything the job depends on, the number of saved
	 * times included (a running simulation's next time makes a new job). The viewer asks for the same series
	 * again for its desktop CSV ({@code raw=1}: the same values, gaps kept), on a variable switched back to, and on
	 * a line redrawn; those are now answered from memory.
	 */
	private static double[][] kymographSeries(DataSource source, String varName, int[] indices, int[] crossingIndices,
			boolean membrane, double[] allTimes, int tstep) throws Exception {
		String key = (membrane ? "membrane " : "volume ") + varName + " tstep=" + tstep + " times=" + allTimes.length
				+ " last=" + allTimes[allTimes.length - 1] + " indices=" + Arrays.toString(indices)
				+ " crossings=" + Arrays.toString(crossingIndices);
		synchronized (source.kymographSeries) {
			double[][] kept = source.kymographSeries.get(key);
			if (kept != null) {
				return kept;
			}
		}
		TimeSeriesJobSpec spec = new TimeSeriesJobSpec(new String[] { varName }, new int[][] { indices },
				crossingIndices != null ? new int[][] { crossingIndices } : null, allTimes[0], tstep,
				allTimes[allTimes.length - 1], VCDataJobID.createVCDataJobID(source.vcdID.getOwner(), true));
		TSJobResultsNoStats results = (TSJobResultsNoStats) source.dataManager
				.getTimeSeriesValues(emptyOutputContext(), source.vcdID, spec);
		kymographJobsRun.incrementAndGet();
		double[][] timesAndValues = results.getTimesAndValuesForVariable(varName);
		synchronized (source.kymographSeries) {
			source.kymographSeries.put(key, timesAndValues);
		}
		return timesAndValues;
	}

	/** Samples × returned times per kymograph; {@code -Dvcell.fieldViewer.maxKymographValues} overrides it. */
	static final int DEFAULT_MAX_KYMOGRAPH_VALUES = 500_000;

	static int maxKymographValues() {
		return Integer.getInteger("vcell.fieldViewer.maxKymographValues", DEFAULT_MAX_KYMOGRAPH_VALUES);
	}

	/** How many of {@code nTimes} saved times a stride of {@code tstep} returns: indices 0, k, 2k, … as {@link TimeSeriesJobSpec} steps. */
	static int strideCount(int nTimes, int tstep) {
		return (nTimes - 1) / tstep + 1;
	}

	/**
	 * Refuses a kymograph of {@code nSamples} over the times a stride returns when that is more values than
	 * the limit, suggesting the smallest stride that fits.
	 */
	static void checkValueLimit(int nSamples, int nTimes, int tstep) {
		int limit = maxKymographValues();
		if ((long) nSamples * strideCount(nTimes, tstep) <= limit) {
			return;
		}
		if (nSamples > limit) {
			throw new IllegalArgumentException("the line has " + nSamples + " samples, more than the limit of "
					+ limit + " values per kymograph; draw a shorter line");
		}
		int k = Math.max(tstep + 1, (int) Math.ceil((double) nSamples * nTimes / limit));
		while ((long) nSamples * strideCount(nTimes, k) > limit) {
			k++;
		}
		throw new TooManyValuesException(nSamples + " samples × " + strideCount(nTimes, tstep) + " times is more than "
				+ limit + " values; use tstep=" + k, k);
	}

	private FieldViewerServer() {
	}

	// ---------------------------------------------------------------------
	// Lifecycle
	// ---------------------------------------------------------------------

	/**
	 * Whether the browser-based 3D field viewer is switched on. On by default;
	 * {@code -Dvcell.fieldViewer.enabled=false}, which an installed client can carry in its
	 * {@code vmoptions.txt}, turns it off. Gates both this server and the "View in 3D" button that opens it.
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
		return startServer();
	}

	/**
	 * Start the server for viewing FEniCSx results, which have no other viewer in VCell: allowed
	 * whenever the FEniCSx solver itself is switched on, whether or not the "View in 3D" button is.
	 *
	 * @return the listening port, or -1 if the server could not be started
	 */
	public static synchronized int startForFenics() {
		return startServer();
	}

	private static synchronized int startServer() {
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
		s.createContext("/timeseries", wrap(FieldViewerServer::handleTimeSeries));
		s.createContext("/stats", wrap(FieldViewerServer::handleStats));
		s.createContext("/kymograph", wrap(FieldViewerServer::handleKymograph));
		Path viewerRoot = staticRoot();
		if (viewerRoot != null) {
			// least-specific context: the data routes above still win, this catches the rest
			s.createContext("/", ex -> serveStatic(ex, viewerRoot));
			LG.info("field viewer page served from {}", viewerRoot);
		}
		// four threads: a heavy job (one at a time, see HEAVY_JOBS) must not starve the viewer's other requests
		s.setExecutor(Executors.newFixedThreadPool(4, r -> {
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

	/**
	 * Directory of built viewer web content, or null if we are not serving the page.
	 * <p>
	 * Serving the page from here puts it on the same origin as the data, which removes the
	 * cross-origin fetch entirely — no CORS, no preflight, and no HTTPS-page-to-loopback question,
	 * because no HTTPS page is involved. Without it the browser is sent to
	 * {@link PropertyLoader#fieldViewerUrl} instead, which is how a developer points at a dev server.
	 */
	private static Path staticRoot() {
		String configured = PropertyLoader.getProperty(PropertyLoader.fieldViewerStaticDir, null);
		Path dir = null;
		if (configured != null && !configured.isEmpty()) {
			dir = Path.of(configured);
		} else {
			String installDir = PropertyLoader.getProperty(PropertyLoader.installationRoot, null);
			if (installDir != null && !installDir.isEmpty()) {
				dir = Path.of(installDir, "webviewer");
			}
		}
		if (dir == null || !Files.isDirectory(dir) || !Files.isRegularFile(dir.resolve(INDEX_HTML))) {
			return null;
		}
		return dir.toAbsolutePath().normalize();
	}

	/**
	 * The viewer page URL for a dataset, as a results window opens it: this server's own page when it
	 * serves one (same origin as the data), else {@link PropertyLoader#fieldViewerUrl} (a developer's
	 * viewer), with the dataset in the query.
	 */
	public static String viewerUrl(int port, String simKey, int jobIndex) {
		String viewer = isServingViewerPage() ? "http://127.0.0.1:" + port + "/"
				: PropertyLoader.getProperty(PropertyLoader.fieldViewerUrl, "http://localhost:4400/");
		return viewer + (viewer.contains("?") ? "&" : "?")
				+ "base=" + java.net.URLEncoder.encode("http://127.0.0.1:" + port, StandardCharsets.UTF_8)
				+ "&sim=" + java.net.URLEncoder.encode(simKey, StandardCharsets.UTF_8) + "&job=" + jobIndex;
	}

	/** True when the page is served from this server, so callers can point the browser at us. */
	public static boolean isServingViewerPage() {
		return staticRoot() != null;
	}

	/**
	 * Serves the built viewer: {@code index.html} at the root, files by path below it.
	 * <p>
	 * The requested path selects a file, so it is normalized and then required to stay under the
	 * root; anything that escapes is treated as not found rather than served.
	 * <p>
	 * A missing file is a 404 rather than a fallback to {@code index.html}. The viewer is a plain
	 * static page with no client-side router, so a fallback would answer a missing script or a
	 * missing wasm bundle with HTML and a 200, turning a clear failure into a confusing one.
	 */
	private static void serveStatic(HttpExchange ex, Path root) throws IOException {
		String requested = ex.getRequestURI().getPath();
		Path file = root.resolve(INDEX_HTML);
		if (requested != null && !requested.equals("/")) {
			Path candidate = root.resolve(requested.substring(1)).normalize();
			if (!candidate.startsWith(root) || !Files.isRegularFile(candidate)) {
				respond(ex, 404, "text/plain; charset=utf-8",
						"not found".getBytes(StandardCharsets.UTF_8));
				return;
			}
			file = candidate;
		}
		byte[] body = Files.readAllBytes(file);
		respond(ex, 200, contentType(file), body);
	}

	private static String contentType(Path file) {
		String name = file.getFileName().toString();
		int dot = name.lastIndexOf('.');
		String ext = dot < 0 ? "" : name.substring(dot + 1).toLowerCase();
		switch (ext) {
			case "html": return "text/html; charset=utf-8";
			case "js": case "mjs": return "text/javascript";
			case "css": return "text/css";
			case "json": case "map": return "application/json";
			case "wasm": return "application/wasm";
			case "gz": case "tgz": return "application/gzip";
			case "svg": return "image/svg+xml";
			case "png": return "image/png";
			case "jpg": case "jpeg": return "image/jpeg";
			case "ico": return "image/x-icon";
			case "woff2": return "font/woff2";
			default: return "application/octet-stream";
		}
	}

	/** Stop the server and forget every dataset. */
	public static synchronized void stop() {
		if (server != null) {
			server.stop(0);
			server = null;
		}
		meshCache.clear();
		domainIndexCache.clear();
		mbGridCache.clear();
		dataSources.clear();
		bundleSources.clear();
	}

	// ---------------------------------------------------------------------
	// Handlers
	// ---------------------------------------------------------------------

	/** {@code /info?sim=<simKey>&job=<n>} — the variables, times and domains available for a run. */
	private static String handleInfo(HttpExchange ex) throws Exception {
		FenicsBundleViews.BundleSource bundle = bundleSourceFor(query(ex));
		if (bundle != null) {
			return FenicsBundleViews.info(bundle);
		}
		DataSource source = sourceFor(query(ex));
		VCSimulationDataIdentifier vcdID = source.vcdID;
		VtuMode vtuMode = vtuMode(source);
		if (vtuMode != null) {
			return handleInfoVtu(source, vtuMode);
		}

		double[] times = source.dataManager.getDataSetTimes(vcdID);
		DataIdentifier[] ids = dataIdentifiers(source);
		List<String> domains = new ArrayList<>(readMesh(source).getVolumeDomainNames());
		domains.addAll(membraneDomains(source)); // after the volume domains, so the first is still a volume

		StringBuilder sb = new StringBuilder(1024);
		sb.append("{\"simId\":\"").append(jsonEscape(vcdID.getID())).append('"');
		if (source.simName != null && !source.simName.isEmpty()) {
			sb.append(",\"simName\":\"").append(jsonEscape(source.simName)).append('"');
		}
		sb.append(",\"jobIndex\":").append(vcdID.getJobIndex());
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
		// volume variables are drawn on the whole-voxel grid, membrane variables on their membrane's faces (a
		// surface of quads in 3D, a curve of segments in 2D); region variables (one value per region) are not listed
		for (boolean membrane : new boolean[] { false, true }) {
			for (DataIdentifier id : ids) {
				if (!id.getVariableType().equals(membrane ? cbit.vcell.math.VariableType.MEMBRANE : cbit.vcell.math.VariableType.VOLUME)) {
					continue;
				}
				if (!first) {
					sb.append(',');
				}
				first = false;
				sb.append("{\"name\":\"").append(jsonEscape(id.getName())).append('"');
				sb.append(",\"domain\":\"").append(jsonEscape(id.getDomain() == null ? "" : id.getDomain().getName())).append('"');
				sb.append(",\"isFunction\":").append(id.isFunction());
				sb.append(membrane ? ",\"membrane\":true}" : "}");
			}
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
		FenicsBundleViews.BundleSource bundle = bundleSourceFor(query(ex));
		if (bundle != null) {
			return FenicsBundleViews.grid(bundle, query(ex));
		}
		Map<String, String> q = query(ex);
		DataSource source = sourceFor(q);
		VtuMode gridVtuMode = vtuMode(source);
		if (gridVtuMode != null) {
			return handleGridVtu(source, q, gridVtuMode);
		}
		String domain = domainOf(q, source);
		VisMesh visMesh = grid(source, domain);

		// let the cache decide whether a re-request costs a round trip. Short max-age plus an ETag
		// rather than "immutable": a moving-boundary run's geometry does change over time, and a
		// client that had cached it forever would draw a stale shape.
		ex.getResponseHeaders().set("ETag", '"' + geometryId(source, domain) + '"');
		ex.getResponseHeaders().set("Cache-Control", "public, max-age=60");

		List<VisPoint> points = visMesh.getPoints();
		Cells cells = new Cells(visMesh);

		boolean membrane = isMembraneDomain(source, domain);
		StringBuilder sb = new StringBuilder(32 * points.size() + 32 * cells.size() + 512);
		sb.append("{\"geometryId\":\"").append(jsonEscape(geometryId(source, domain))).append('"');
		sb.append(",\"dimension\":").append(cells.dimension);
		if (membrane) {
			// the membrane's faces, drawn as they are: no smoothing (that is the voxel grid's), one value per face
			sb.append(",\"membrane\":true");
		}
		sb.append(",\"numPoints\":").append(points.size());
		sb.append(",\"points\":[");
		for (int i = 0; i < points.size(); i++) {
			VisPoint p = points.get(i);
			if (i > 0) {
				sb.append(',');
			}
			sb.append(p.getX()).append(',').append(p.getY()).append(',').append(p.getZ());
		}
		sb.append("],\"cellType\":").append(cells.vtkCellType);
		sb.append(",\"cells\":[");
		for (int c = 0; c < cells.size(); c++) {
			List<Integer> idx = cells.pointIndices.get(c);
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
		if (!membrane) {
			sb.append(",\"sinc\":{\"iterations\":").append(SINC_ITERATIONS)
				.append(",\"feature_angle\":").append(SINC_FEATURE_ANGLE)
				.append(",\"pass_band\":").append(SINC_PASS_BAND).append('}');
		}
		sb.append('}');
		return sb.toString();
	}

	// ---------------------------------------------------------------------
	// MovingBoundary (mbsolver) — per-timestep body-fitted meshes via the VTU seam (#1879)
	// ---------------------------------------------------------------------

	/**
	 * MovingBoundary runs serve a DIFFERENT geometry per saved time: the solver's body-fitted 2D
	 * mesh, whose boundary cells are true cut polygons. These handlers reach it through the
	 * VTU-era {@link VCDataManager} methods, which are already time-indexed for MovingBoundary
	 * and already pair each mesh with a per-cell value array by shared ordinal — and which work
	 * against today's deployed servers with no interface changes. The data arrives over the remote
	 * seam for a server run and from the desktop's own data set controller for a local quick run;
	 * either way the .vtu is written by the pure-Java writer ({@code org.vcell.vis.vtk.VtuWriter}),
	 * so no Python is needed. {@link VtuGridParser} converts those bytes to the viewer's
	 * JSON contract; the geometryId carries the time index, which is what tells the viewer to
	 * re-fetch geometry as time moves.
	 */
	private static String handleInfoVtu(DataSource source, VtuMode mode) throws Exception {
		VCSimulationDataIdentifier vcdID = source.vcdID;
		double[] times = source.dataManager.getDataSetTimes(vcdID);
		StringBuilder sb = new StringBuilder(1024);
		sb.append("{\"simId\":\"").append(jsonEscape(vcdID.getID())).append('"');
		if (source.simName != null && !source.simName.isEmpty()) {
			sb.append(",\"simName\":\"").append(jsonEscape(source.simName)).append('"');
		}
		sb.append(",\"jobIndex\":").append(vcdID.getJobIndex());
		// which body-fitted mode: the viewer's tools differ (a static mesh or one per time; voxels and polyhedra in 3D)
		sb.append(",\"solver\":\"").append(mode == VtuMode.STATIC ? "Chombo" : "MovingBoundary").append('"');
		sb.append(",\"times\":");
		appendDoubles(sb, times, times.length);
		List<String> domains = vtuDomains(source);
		sb.append(",\"domains\":[");
		for (int i = 0; i < domains.size(); i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append('"').append(jsonEscape(domains.get(i))).append('"');
		}
		sb.append("]");
		sb.append(",\"variables\":[");
		boolean first = true;
		for (VtuVarInfo var : vtuVarInfos(source)) {
			if (var.bMeshVariable || var.dataType != VtuVarInfo.DataType.CellData) {
				continue;
			}
			if (!first) {
				sb.append(',');
			}
			first = false;
			sb.append("{\"name\":\"").append(jsonEscape(var.name)).append('"');
			sb.append(",\"domain\":\"").append(jsonEscape(var.domainName == null ? "" : var.domainName)).append('"');
			sb.append(",\"isFunction\":").append(var.functionExpression != null).append('}');
		}
		sb.append("]}");
		return sb.toString();
	}

	/** Distinct domains of the run's cell-data variables, in first-seen order. */
	private static List<String> vtuDomains(DataSource source) throws Exception {
		List<String> domains = new ArrayList<>();
		for (VtuVarInfo var : vtuVarInfos(source)) {
			if (var.bMeshVariable || var.dataType != VtuVarInfo.DataType.CellData) {
				continue;
			}
			if (var.domainName != null && !domains.contains(var.domainName)) {
				domains.add(var.domainName);
			}
		}
		if (domains.isEmpty()) {
			throw new IllegalArgumentException("run " + source.vcdID.getID()
					+ " exposes no cell-data variables over the VTU seam");
		}
		return domains;
	}

	/** Snaps the requested time to the nearest saved index; defaults to the last one. */
	private static int timeIndexFor(DataSource source, Map<String, String> q) throws Exception {
		double[] times = source.dataManager.getDataSetTimes(source.vcdID);
		if (times == null || times.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}
		String requested = q.get("time");
		if (requested == null || requested.isEmpty()) {
			return times.length - 1;
		}
		double target = Double.parseDouble(requested);
		int best = 0;
		for (int i = 1; i < times.length; i++) {
			if (Math.abs(times[i] - target) < Math.abs(times[best] - target)) {
				best = i;
			}
		}
		return best;
	}

	private static String vtuGeometryId(DataSource source, String domain, VtuMode mode, int timeIndex) {
		String base = source.vcdID.getID() + "/" + domain;
		return mode == VtuMode.TIME_VARYING ? base + "@t" + timeIndex : base;
	}

	/** Parsed per-time meshes; a run's meshes are dropped with it in {@link #unregister}. */
	private static final Map<String, VtuGridParser.VtuGrid> mbGridCache = new HashMap<>();

	private static synchronized VtuGridParser.VtuGrid mbGrid(DataSource source, String domain,
			int timeIndex) throws Exception {
		// internal cache key is always time-qualified; the public geometryId may not be
		String key = source.vcdID.getID() + "/" + domain + "@t" + timeIndex;
		VtuGridParser.VtuGrid cached = mbGridCache.get(key);
		if (cached != null) {
			return cached;
		}
		VtuFileContainer container = source.dataManager.getEmptyVtuMeshFiles(source.vcdID, timeIndex);
		for (VtuFileContainer.VtuMesh mesh : container.vtuMeshes) {
			if (mesh.domainName.equals(domain)) {
				VtuGridParser.VtuGrid grid = VtuGridParser.parse(mesh.vtuMeshContents);
				if (vtuMode(source) == VtuMode.STATIC) {
					grid = chomboToPhysical(grid, source);
				}
				mbGridCache.put(key, grid);
				return grid;
			}
		}
		throw new IllegalArgumentException("no VTU mesh for domain '" + domain + "' at time index "
				+ timeIndex + "; this run has "
				+ container.vtuMeshes.stream().map(m -> m.domainName).toList());
	}

	/**
	 * A Chombo .vtu carries {@code ChomboMeshMapping}'s doubled-index coordinates — vertex
	 * {@code v = (p-origin)*N/extent*2 - 1}, chosen there so mesh vertices land on exact integers —
	 * not physical coordinates (a legacy of the VisIt-era consumers). Everything the viewer does
	 * with the mesh (axes, lab-frame points, cell measures) needs microns, so invert that map as
	 * the mesh comes over the seam: {@code p = origin + (v+1)*extent/(2N)}.
	 */
	private static VtuGridParser.VtuGrid chomboToPhysical(VtuGridParser.VtuGrid grid,
			DataSource source) throws Exception {
		cbit.vcell.solvers.CartesianMesh mesh = solverMesh(source);
		double[] origin = { mesh.getOrigin().getX(), mesh.getOrigin().getY(), mesh.getOrigin().getZ() };
		double[] extent = { mesh.getExtent().getX(), mesh.getExtent().getY(), mesh.getExtent().getZ() };
		int[] n = { mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ() };
		double[] physical = new double[grid.points.length];
		for (int i = 0; i < grid.points.length; i++) {
			int axis = i % 3;
			physical[i] = n[axis] > 1
					? origin[axis] + (grid.points[i] + 1) * extent[axis] / (2.0 * n[axis])
					: grid.points[i];
		}
		return new VtuGridParser.VtuGrid(physical, grid.cells, grid.cellTypes, grid.cellFaces);
	}

	private static String handleGridVtu(DataSource source, Map<String, String> q, VtuMode mode) throws Exception {
		String domain = q.getOrDefault("domain", "");
		if (domain.isEmpty()) {
			domain = vtuDomains(source).get(0);
		}
		// a Chombo mesh is static — the server refuses any other index — while a MovingBoundary
		// mesh is a different geometry at every saved time
		int timeIndex = mode == VtuMode.TIME_VARYING ? timeIndexFor(source, q) : 0;
		VtuGridParser.VtuGrid grid = mbGrid(source, domain, timeIndex);

		boolean threeD = false;
		boolean uniform = true;
		for (int c = 0; c < grid.cellTypes.length; c++) {
			// tets (10), voxels (11), hexes (12), wedges (13), pyramids (14) and polyhedra (42)
			// are volume cells
			threeD |= grid.cellTypes[c] >= 10 && grid.cellTypes[c] <= 14
					|| grid.cellTypes[c] == VtuGridParser.VTK_POLYHEDRON;
			uniform &= grid.cellTypes[c] == grid.cellTypes[0];
		}
		StringBuilder sb = new StringBuilder(32 * grid.numPoints() + 32 * grid.cells.length + 512);
		sb.append("{\"geometryId\":\"").append(jsonEscape(vtuGeometryId(source, domain, mode, timeIndex))).append('"');
		// the embedding dimension, not the cell dimension: a membrane of a 3D run is a surface in 3D
		sb.append(",\"dimension\":").append(threeD || spansZ(grid) ? 3 : 2).append(",\"bodyFitted\":true");
		sb.append(",\"timeIndex\":").append(timeIndex);
		sb.append(",\"numPoints\":").append(grid.numPoints());
		sb.append(",\"points\":");
		appendDoubles(sb, grid.points, grid.points.length);
		sb.append(",\"cellType\":").append(grid.cellTypes.length > 0 ? grid.cellTypes[0] : 7);
		if (!uniform) {
			// Chombo 3D mixes whole voxels with the polyhedra it cuts at the boundary; the viewer
			// inserts per-cell types when this array is present
			sb.append(",\"cellTypes\":[");
			for (int c = 0; c < grid.cellTypes.length; c++) {
				if (c > 0) {
					sb.append(',');
				}
				sb.append(grid.cellTypes[c]);
			}
			sb.append(']');
		}
		sb.append(",\"cells\":[");
		for (int c = 0; c < grid.cells.length; c++) {
			if (c > 0) {
				sb.append(',');
			}
			sb.append('[');
			for (int v = 0; v < grid.cells[c].length; v++) {
				if (v > 0) {
					sb.append(',');
				}
				sb.append(grid.cells[c][v]);
			}
			sb.append(']');
		}
		sb.append(']');
		if (grid.cellFaces != null) {
			// a polyhedron carries its own faces: [numFaces, numPoints, ids…, numPoints, ids…],
			// null at every cell that is not one
			sb.append(",\"cellFaces\":[");
			for (int c = 0; c < grid.cells.length; c++) {
				if (c > 0) {
					sb.append(',');
				}
				int[][] faces = grid.facesOf(c);
				if (faces == null) {
					sb.append("null");
					continue;
				}
				sb.append('[').append(faces.length);
				for (int[] face : faces) {
					sb.append(',').append(face.length);
					for (int v : face) {
						sb.append(',').append(v);
					}
				}
				sb.append(']');
			}
			sb.append(']');
		}
		sb.append(",\"domain\":\"").append(jsonEscape(domain)).append('"');
		sb.append('}');
		return sb.toString();
	}

	private static String handleFieldVtu(DataSource source, Map<String, String> q, VtuMode mode) throws Exception {
		String varName = q.get("var");
		if (varName == null || varName.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'var'");
		}
		String domain = q.getOrDefault("domain", "");
		if (domain.isEmpty()) {
			domain = vtuDomains(source).get(0);
		}
		int timeIndex = timeIndexFor(source, q);
		double[] times = source.dataManager.getDataSetTimes(source.vcdID);
		double time = times[timeIndex];

		VtuVarInfo varInfo = vtuVarInfoFor(source, varName);
		// the VTU seam's contract: values are ordered by the same sequential inside-cell ordinal
		// the mesh's cells were written in, so values[i] belongs to cells[i] of the SAME timeIndex
		double[] values = source.dataManager.getVtuMeshData(emptyOutputContext(), source.vcdID, varInfo, time);
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (double v : values) {
			if (!Double.isNaN(v)) {
				min = Math.min(min, v);
				max = Math.max(max, v);
			}
		}
		if (min > max) {
			min = 0;
			max = 0;
		}
		StringBuilder sb = new StringBuilder(16 * values.length + 256);
		sb.append("{\"geometryId\":\"").append(jsonEscape(vtuGeometryId(source, domain, mode, timeIndex))).append('"');
		sb.append(",\"name\":\"").append(jsonEscape(varName)).append('"');
		sb.append(",\"domain\":\"").append(jsonEscape(domain)).append('"');
		sb.append(",\"time\":").append(time);
		sb.append(",\"location\":\"cell\",\"values\":");
		appendDoubles(sb, values, values.length);
		sb.append(",\"range\":[").append(min).append(',').append(max).append("]}");
		return sb.toString();
	}

	private static VtuVarInfo vtuVarInfoFor(DataSource source, String varName) throws Exception {
		for (VtuVarInfo v : vtuVarInfos(source)) {
			if (v.name.equals(varName)) {
				return v;
			}
		}
		throw new IllegalArgumentException("unknown variable '" + varName + "'");
	}

	/**
	 * {@code /timeseries?sim=&job=&domain=&var=&x=&y=[&z=]} for VTU-mode runs — the variable's
	 * time course at a fixed LAB-FRAME point. Cell ordinals are per-mesh, not solver raster
	 * indices, so the browser addresses a spatial point instead. TIME_VARYING locates the point in
	 * each saved time's own body-fitted mesh; STATIC locates it once in the one embedded-boundary
	 * mesh. Times where the point lies outside the domain (for a moving boundary: the boundary has
	 * moved past it) serialize as {@code null} — a physically meaningful gap, not missing data.
	 * <p>
	 * {@code &points=x,y,z;…} asks for several points in the same single pass over the times (the
	 * multi-point response, {@link PointSeries#json}): each time's values cross the remote seam once,
	 * however many points read them. {@code &snap=nearest} moves a point that misses a membrane variable's mesh
	 * (a Chombo membrane: lines in 2D, a triangulated surface in 3D) onto it, within one cell diameter.
	 */
	private static String handleTimeSeriesVtu(DataSource source, Map<String, String> q,
			VtuMode mode) throws Exception {
		String varName = q.get("var");
		if (varName == null || varName.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'var'");
		}
		String domain = q.getOrDefault("domain", "");
		if (domain.isEmpty()) {
			domain = vtuDomains(source).get(0);
		}
		String pointsParam = q.get("points");
		if (pointsParam == null && (q.get("x") == null || q.get("y") == null)) {
			throw new IllegalArgumentException(
					"a body-fitted time series is addressed by lab-frame point: 'x' and 'y' are required"
							+ " ('cell' ordinals are per-mesh, not solver raster indices)");
		}
		VtuVarInfo varInfo = vtuVarInfoFor(source, varName);
		double[] times = source.dataManager.getDataSetTimes(source.vcdID);
		if (times == null || times.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}
		// a click almost never lands exactly on a membrane (Chombo's lines or surface triangles): it snaps onto it
		boolean snap = "nearest".equals(q.get("snap"))
				&& varInfo.variableDomain == cbit.vcell.math.VariableType.VariableDomain.VARIABLEDOMAIN_MEMBRANE;
		final String dom = domain;
		PointSeries.Rows rows = new PointSeries.Rows() {
			@Override
			public VtuGridParser.VtuGrid grid(int row) throws Exception {
				// a Chombo mesh is static; a MovingBoundary mesh is a different geometry at every saved time
				return mbGrid(source, dom, mode == VtuMode.TIME_VARYING ? row : 0);
			}

			@Override
			public double[] values(int row) throws Exception {
				return source.dataManager.getVtuMeshData(emptyOutputContext(), source.vcdID, varInfo, times[row]);
			}
		};
		if (pointsParam != null) {
			VtuGridParser.VtuGrid first = rows.grid(0);
			// a 2D point may leave out z: it takes the plane of a 2D mesh (a 3D run's membrane surface has none)
			double[][] points = PointSeries.parsePoints(pointsParam, isVolume3D(first) || spansZ(first) ? null : first.points[2]);
			PointSeries.Result r = PointSeries.sample(times.length, points, rows, PointSeries.Location.CELL, snap);
			return PointSeries.json(varName, domain, times, PointSeries.Location.CELL, PointSeries.series(points, r));
		}
		double x = Double.parseDouble(q.get("x"));
		double y = Double.parseDouble(q.get("y"));
		double z = q.get("z") != null ? Double.parseDouble(q.get("z")) : 0;
		PointSeries.Result r = PointSeries.sample(times.length, new double[][] { { x, y, z } }, rows,
				PointSeries.Location.CELL, snap);
		double[] values = r.values[0];

		StringBuilder sb = new StringBuilder(32 * times.length + 256);
		sb.append("{\"name\":\"").append(jsonEscape(varName)).append('"');
		sb.append(",\"domain\":\"").append(jsonEscape(domain)).append('"');
		sb.append(",\"x\":").append(x).append(",\"y\":").append(y);
		sb.append(",\"insideCount\":").append(r.insideCount[0]);
		sb.append(",\"times\":");
		appendDoubles(sb, times, times.length);
		sb.append(",\"values\":");
		appendDoubles(sb, values, values.length);
		sb.append('}');
		return sb.toString();
	}

	/**
	 * True when the grid's points do not all share one z: a mesh embedded in 3D, such as the membrane surface of a
	 * 3D Chombo run (triangles only, so not {@link #isVolume3D}). A 2D run's grid lies in one plane.
	 */
	private static boolean spansZ(VtuGridParser.VtuGrid grid) {
		for (int i = 5; i < grid.points.length; i += 3) {
			if (grid.points[i] != grid.points[2]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * True when the grid has volume cells: tets (10), voxels (11), hexes (12), wedges (13), pyramids
	 * (14) or polyhedra (42). A 2D run's grid has only polygons.
	 */
	private static boolean isVolume3D(VtuGridParser.VtuGrid grid) {
		for (int type : grid.cellTypes) {
			if (type >= 10 && type <= 14 || type == VtuGridParser.VTK_POLYHEDRON) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@code /stats?sim=&job=[&var=a,b,c]} for VTU-mode runs — per saved time, min, max and
	 * measure-weighted mean over the body-fitted mesh (each saved time's own mesh for
	 * TIME_VARYING; the one embedded-boundary mesh for STATIC). The statistics follow the domain:
	 * each frame integrates over the cells the domain actually occupies then, weighting each cell
	 * by its area or volume (the display-mesh statistics family — self-consistent with what is
	 * drawn). Each series also carries the domain's total measure per time — for a moving boundary,
	 * the size of the moving region itself.
	 */
	private static String handleStatsVtu(DataSource source, Map<String, String> q, VtuMode mode)
			throws Exception {
		String domain = q.getOrDefault("domain", "");
		if (domain.isEmpty()) {
			domain = vtuDomains(source).get(0);
		}
		Set<String> requested = null;
		String varParam = q.get("var");
		if (varParam != null && !varParam.isEmpty()) {
			requested = new LinkedHashSet<>(Arrays.asList(varParam.split(",")));
		}
		List<VtuVarInfo> chosen = new ArrayList<>();
		for (VtuVarInfo var : vtuVarInfos(source)) {
			if (var.bMeshVariable || var.dataType != VtuVarInfo.DataType.CellData) {
				continue;
			}
			if (requested == null || requested.contains(var.name)) {
				chosen.add(var);
			}
		}
		if (chosen.isEmpty()) {
			throw new IllegalArgumentException("no cell-data variables match " + varParam);
		}
		double[] times = source.dataManager.getDataSetTimes(source.vcdID);
		if (times == null || times.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}

		int nv = chosen.size();
		double[][] mins = new double[nv][times.length];
		double[][] maxs = new double[nv][times.length];
		double[][] means = new double[nv][times.length];
		double[] domainMeasure = new double[times.length];
		double[] staticMeasures = mode == VtuMode.STATIC
				? VtuGridParser.cellMeasures(mbGrid(source, domain, 0))
				: null;
		for (int i = 0; i < times.length; i++) {
			double[] measures = staticMeasures != null ? staticMeasures
					: VtuGridParser.cellMeasures(mbGrid(source, domain, i));
			double total = 0;
			for (double m : measures) {
				total += m;
			}
			domainMeasure[i] = total;
			for (int v = 0; v < nv; v++) {
				double[] values = source.dataManager.getVtuMeshData(emptyOutputContext(),
						source.vcdID, chosen.get(v), times[i]);
				double min = Double.POSITIVE_INFINITY;
				double max = Double.NEGATIVE_INFINITY;
				double weighted = 0;
				double weight = 0;
				int n = Math.min(values.length, measures.length);
				for (int c = 0; c < n; c++) {
					if (Double.isNaN(values[c])) {
						continue;
					}
					min = Math.min(min, values[c]);
					max = Math.max(max, values[c]);
					weighted += values[c] * measures[c];
					weight += measures[c];
				}
				mins[v][i] = min <= max ? min : Double.NaN;
				maxs[v][i] = min <= max ? max : Double.NaN;
				means[v][i] = weight > 0 ? weighted / weight : Double.NaN;
			}
		}

		StringBuilder sb = new StringBuilder(64 * times.length * nv + 512);
		sb.append("{\"times\":");
		appendDoubles(sb, times, times.length);
		sb.append(",\"weighting\":\"measure\"");
		sb.append(",\"series\":[");
		for (int v = 0; v < nv; v++) {
			if (v > 0) {
				sb.append(',');
			}
			sb.append("{\"name\":\"").append(jsonEscape(chosen.get(v).name)).append('"');
			sb.append(",\"domain\":\"").append(jsonEscape(domain)).append('"');
			sb.append(",\"min\":");
			appendDoubles(sb, mins[v], times.length);
			sb.append(",\"max\":");
			appendDoubles(sb, maxs[v], times.length);
			sb.append(",\"mean\":");
			appendDoubles(sb, means[v], times.length);
			sb.append(",\"measure\":");
			appendDoubles(sb, domainMeasure, times.length);
			sb.append('}');
		}
		sb.append("]}");
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
		FenicsBundleViews.BundleSource bundle = bundleSourceFor(query(ex));
		if (bundle != null) {
			return FenicsBundleViews.field(bundle, query(ex));
		}
		Map<String, String> q = query(ex);
		DataSource source = sourceFor(q);
		VtuMode fieldVtuMode = vtuMode(source);
		if (fieldVtuMode != null) {
			return handleFieldVtu(source, q, fieldVtuMode);
		}
		String domain = domainOf(q, source);
		String varName = q.get("var");
		if (varName == null || varName.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'var'");
		}
		double time = parseTime(q, source);
		checkVariableFitsDomain(source, varName, domain);
		Cells cells = new Cells(grid(source, domain));

		// each cell carries the mesh's global index of the element it came from, which is the
		// position of that cell's value in the solver's data array
		SimDataBlock block = source.dataManager.getSimDataBlock(emptyOutputContext(), source.vcdID, varName, time);
		double[] meshData = block.getData();
		double[] values = new double[cells.size()];
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int c = 0; c < cells.size(); c++) {
			double value = meshData[cells.globalIndices[c]];
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

	/**
	 * {@code /timeseries?sim=<simKey>&job=<n>&domain=<name>&var=<name>&cell=<c>} — one variable's
	 * full time course at one grid cell, for the viewer's click-to-plot.
	 * <p>
	 * The reduction happens HERE, next to the reader, through the same
	 * {@link VCDataManager#getTimeSeriesValues} path the desktop's own time plots use — the design
	 * the viewer must not replicate is fetching every timestep to the browser to build one curve.
	 * The browser addresses the cell by its index in the served grid; this is where that maps to
	 * the solver's global volume index.
	 */
	private static String handleTimeSeries(HttpExchange ex) throws Exception {
		FenicsBundleViews.BundleSource bundle = bundleSourceFor(query(ex));
		if (bundle != null) {
			return FenicsBundleViews.timeSeries(bundle, query(ex));
		}
		Map<String, String> q = query(ex);
		DataSource source = sourceFor(q);
		VtuMode tsMode = vtuMode(source);
		if (tsMode != null) {
			// several points read every saved time over the remote seam: a heavy job
			return q.get("points") != null ? heavy(() -> handleTimeSeriesVtu(source, q, tsMode))
					: handleTimeSeriesVtu(source, q, tsMode);
		}
		String domain = domainOf(q, source);
		String varName = q.get("var");
		if (varName == null || varName.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'var'");
		}
		if (q.get("points") != null) {
			return isMembraneDomain(source, domain) ? handleTimeSeriesMembranePoints(source, domain, varName, q.get("points"))
					: handleTimeSeriesPoints(source, domain, varName, q.get("points"));
		}
		String cellParam = q.get("cell");
		if (cellParam == null || cellParam.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'cell' (or 'points')");
		}
		Cells cells = new Cells(grid(source, domain));
		int cell = Integer.parseInt(cellParam);
		if (cell < 0 || cell >= cells.size()) {
			throw new IllegalArgumentException("cell " + cell + " out of range; domain '" + domain
					+ "' has " + cells.size() + " cells");
		}
		int globalIndex = cells.globalIndices[cell];

		double[] times = source.dataManager.getDataSetTimes(source.vcdID);
		if (times == null || times.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}
		TimeSeriesJobSpec spec = new TimeSeriesJobSpec(new String[] { varName },
				new int[][] { { globalIndex } }, null, times[0], 1, times[times.length - 1],
				VCDataJobID.createVCDataJobID(source.vcdID.getOwner(), true));
		TSJobResultsNoStats results = (TSJobResultsNoStats) source.dataManager
				.getTimeSeriesValues(emptyOutputContext(), source.vcdID, spec);
		// row 0 is the times, row 1 the values at our single index
		double[][] timesAndValues = results.getTimesAndValuesForVariable(varName);

		StringBuilder sb = new StringBuilder(32 * timesAndValues[0].length + 256);
		sb.append("{\"name\":\"").append(jsonEscape(varName)).append('"');
		sb.append(",\"domain\":\"").append(jsonEscape(domain)).append('"');
		sb.append(",\"cell\":").append(cell);
		sb.append(",\"volumeIndex\":").append(globalIndex);
		sb.append(",\"times\":");
		appendDoubles(sb, timesAndValues[0], timesAndValues[0].length);
		sb.append(",\"values\":");
		appendDoubles(sb, timesAndValues[1], timesAndValues[1].length);
		sb.append('}');
		return sb.toString();
	}

	/**
	 * {@code /timeseries?…&points=x,y,z;…} for a finite-volume run: each lab-frame point maps to the
	 * voxel containing it exactly as the desktop's point selections do
	 * ({@code SpatialSelectionVolume.getIndex(0)}: {@link cbit.vcell.solvers.CartesianMesh#getFractionalCoordinateIndex}
	 * rounded to a {@code CoordinateIndex}, then its volume index), and every point inside the domain is
	 * answered by ONE {@link TimeSeriesJobSpec}, as the desktop's multi-point time plot is.
	 * <p>
	 * The raw data array covers the whole mesh, so a point in another compartment would read that
	 * compartment's numbers; it is masked instead (its values are null and {@code inDomain} false), using
	 * the domain's own cells — the same set {@code /grid} serves. A point outside the mesh has
	 * {@code volumeIndex} -1; {@code cell} is the point's cell in {@code /grid}'s list, -1 outside it.
	 */
	private static String handleTimeSeriesPoints(DataSource source, String domain, String varName,
			String pointsParam) throws Exception {
		cbit.vcell.solvers.CartesianMesh mesh = solverMesh(source);
		VisMesh visMesh = grid(source, domain);
		// a 2D point may leave out z: it takes the plane the served grid lies in
		Double planeZ = mesh.getGeometryDimension() < 3 ? visMesh.getPoints().get(0).getZ() : null;
		double[][] points = PointSeries.parsePoints(pointsParam, planeZ);
		DomainIndex domainIndex = domainIndex(source, domain);

		int n = points.length;
		int[] volumeIndex = new int[n];
		int[] cell = new int[n];
		// in-domain voxels, each once: two probes in one voxel share a column of the job
		Map<Integer, Integer> column = new java.util.LinkedHashMap<>();
		for (int p = 0; p < n; p++) {
			volumeIndex[p] = volumeIndexAt(mesh, points[p]);
			cell[p] = volumeIndex[p] >= 0 ? domainIndex.cellOf(volumeIndex[p]) : -1;
			if (cell[p] >= 0) {
				column.putIfAbsent(volumeIndex[p], column.size());
			}
		}

		double[] times = source.dataManager.getDataSetTimes(source.vcdID);
		if (times == null || times.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}
		double[][] timesAndValues = null;
		if (!column.isEmpty()) {
			int[] indices = column.keySet().stream().mapToInt(Integer::intValue).toArray();
			TimeSeriesJobSpec spec = new TimeSeriesJobSpec(new String[] { varName }, new int[][] { indices },
					null, times[0], 1, times[times.length - 1],
					VCDataJobID.createVCDataJobID(source.vcdID.getOwner(), true));
			TSJobResultsNoStats results = (TSJobResultsNoStats) source.dataManager
					.getTimeSeriesValues(emptyOutputContext(), source.vcdID, spec);
			// row 0 is the times, row 1 + k the values at indices[k]
			timesAndValues = results.getTimesAndValuesForVariable(varName);
			times = timesAndValues[0];
		}
		PointSeries.Series[] series = new PointSeries.Series[n];
		for (int p = 0; p < n; p++) {
			double[] values;
			if (cell[p] >= 0) {
				values = timesAndValues[1 + column.get(volumeIndex[p])];
			} else {
				values = new double[times.length];
				Arrays.fill(values, Double.NaN);
			}
			series[p] = new PointSeries.Series(points[p], cell[p], volumeIndex[p], null, cell[p] >= 0, null, values);
		}
		return PointSeries.json(varName, domain, times, PointSeries.Location.CELL, series);
	}

	/**
	 * {@code /timeseries?…&points=x,y,z;…} for a finite-volume membrane variable: each point snaps to the nearest
	 * membrane face of the domain (the faces {@code /grid} serves, within one face diameter: a click almost never
	 * lands exactly on a membrane), whose membrane element it reads, and every point that found one is answered
	 * by ONE {@link TimeSeriesJobSpec} over their membrane indices, as the desktop's time plot of membrane points
	 * is. A point with no face near it is a gap ({@code inDomain} false). Each series gives the face's
	 * {@code cell} in {@code /grid}'s list, its {@code membraneIndex}, and where the point {@code snapped} to.
	 */
	private static String handleTimeSeriesMembranePoints(DataSource source, String domain, String varName,
			String pointsParam) throws Exception {
		checkVariableFitsDomain(source, varName, domain);
		VisMesh visMesh = grid(source, domain);
		Cells cells = new Cells(visMesh);
		List<VisPoint> visPoints = visMesh.getPoints();
		double[] xyz = new double[3 * visPoints.size()];
		for (int i = 0; i < visPoints.size(); i++) {
			xyz[3 * i] = visPoints.get(i).getX();
			xyz[3 * i + 1] = visPoints.get(i).getY();
			xyz[3 * i + 2] = visPoints.get(i).getZ();
		}
		int[][] faces = new int[cells.size()][];
		int[] types = new int[cells.size()];
		for (int c = 0; c < faces.length; c++) {
			faces[c] = cells.pointIndices.get(c).stream().mapToInt(Integer::intValue).toArray();
			types[c] = cells.vtkCellType;
		}
		VtuGridParser.VtuGrid membrane = new VtuGridParser.VtuGrid(xyz, faces, types);
		Double planeZ = cells.dimension < 3 ? xyz[2] : null;
		double[][] points = PointSeries.parsePoints(pointsParam, planeZ);

		int n = points.length;
		int[] cell = new int[n];
		double[][] snapped = new double[n][];
		Map<Integer, Integer> column = new java.util.LinkedHashMap<>();
		for (int p = 0; p < n; p++) {
			double[] near = VtuGridParser.nearestOnMesh(membrane, points[p][0], points[p][1], points[p][2]);
			cell[p] = near == null ? -1 : (int) near[3];
			if (near != null) {
				snapped[p] = new double[] { near[0], near[1], near[2] };
				column.putIfAbsent(cells.globalIndices[cell[p]], column.size());
			}
		}
		double[] times = source.dataManager.getDataSetTimes(source.vcdID);
		if (times == null || times.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}
		double[][] timesAndValues = null;
		if (!column.isEmpty()) {
			int[] indices = column.keySet().stream().mapToInt(Integer::intValue).toArray();
			TimeSeriesJobSpec spec = new TimeSeriesJobSpec(new String[] { varName }, new int[][] { indices },
					null, times[0], 1, times[times.length - 1],
					VCDataJobID.createVCDataJobID(source.vcdID.getOwner(), true));
			TSJobResultsNoStats results = (TSJobResultsNoStats) source.dataManager
					.getTimeSeriesValues(emptyOutputContext(), source.vcdID, spec);
			timesAndValues = results.getTimesAndValuesForVariable(varName);
			times = timesAndValues[0];
		}
		PointSeries.Series[] series = new PointSeries.Series[n];
		for (int p = 0; p < n; p++) {
			double[] values;
			Integer membraneIndex = null;
			if (cell[p] >= 0) {
				membraneIndex = cells.globalIndices[cell[p]];
				values = timesAndValues[1 + column.get(membraneIndex)];
			} else {
				values = new double[times.length];
				Arrays.fill(values, Double.NaN);
			}
			series[p] = new PointSeries.Series(points[p], cell[p], null, membraneIndex, cell[p] >= 0, snapped[p], values);
		}
		return PointSeries.json(varName, domain, times, PointSeries.Location.CELL, series);
	}

	/**
	 * The volume index of the voxel containing a lab-frame point, or -1 outside the mesh. VCell's
	 * Cartesian mesh is node-centred (element i at {@code origin + i·extent/(N−1)}), so this rounds the
	 * fractional index, as the desktop does.
	 */
	static int volumeIndexAt(cbit.vcell.solvers.CartesianMesh mesh, double[] point) {
		double[] origin = { mesh.getOrigin().getX(), mesh.getOrigin().getY(), mesh.getOrigin().getZ() };
		double[] extent = { mesh.getExtent().getX(), mesh.getExtent().getY(), mesh.getExtent().getZ() };
		int[] size = { mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ() };
		for (int axis = 0; axis < 3; axis++) {
			// an axis the mesh doesn't resolve (z in 2D) doesn't bound the point
			double slack = 1e-9 * extent[axis];
			if (size[axis] > 1 && (point[axis] < origin[axis] - slack || point[axis] > origin[axis] + extent[axis] + slack)) {
				return -1;
			}
		}
		org.vcell.util.CoordinateIndex ci = mesh.getCoordinateIndexFromFractionalIndex(
				mesh.getFractionalCoordinateIndex(new org.vcell.util.Coordinate(point[0], point[1], point[2])));
		int[] index = { ci.x, ci.y, ci.z };
		for (int axis = 0; axis < 3; axis++) {
			if (index[axis] < 0 || index[axis] >= size[axis]) {
				return -1;
			}
		}
		return mesh.getVolumeIndex(ci);
	}

	/**
	 * The cells of one domain of a finite-volume run, as {@code /grid} serves them, indexed by the
	 * solver's volume index: which voxels are in the domain (the mask), and each one's position in the
	 * served list.
	 */
	private static final class DomainIndex {
		final java.util.BitSet inDomain = new java.util.BitSet();
		private final Map<Integer, Integer> cellByVolumeIndex = new HashMap<>();

		DomainIndex(Cells cells) {
			for (int c = 0; c < cells.size(); c++) {
				inDomain.set(cells.globalIndices[c]);
				cellByVolumeIndex.put(cells.globalIndices[c], c);
			}
		}

		/** the served cell holding volume index {@code v}, or -1 when the voxel is not in the domain */
		int cellOf(int v) {
			return inDomain.get(v) ? cellByVolumeIndex.get(v) : -1;
		}
	}

	/** Per sim+domain, like {@link #meshCache}; dropped with it. */
	private static final Map<String, DomainIndex> domainIndexCache = new HashMap<>();

	/**
	 * {@code /kymograph?sim=&job=&domain=&var=<name>&path=x1,y1,z1;x2,y2,z2[;…][&tstep=k][&raw=1]} — one
	 * variable's values along a polyline at every saved time (every {@code tstep}-th), rows = times, as the
	 * desktop's kymograph shows them.
	 * <p>
	 * A FEniCSx bundle or a run served through the VTU seam (Chombo, MovingBoundary) is sampled evenly
	 * ({@link BodyFittedKymograph}; a MovingBoundary line is a fixed lab-frame line through each time's mesh).
	 * For finite volume, the samples are the desktop's ({@link FvLineSampler}): one per voxel the
	 * line crosses, two at each membrane crossing. The values come from ONE {@link TimeSeriesJobSpec} over
	 * all samples with the membrane-crossing indices, exactly as {@code KymographPanel.initDataManagerVariable}
	 * builds it, so the two samples at a crossing carry the {@code _INSIDE}/{@code _OUTSIDE} membrane values.
	 * The job runs next to the reader: only samples × times values travel.
	 * <p>
	 * Samples outside the variable's domain are gaps (null), since the raw array would give another
	 * compartment's numbers there; {@code raw=1} keeps the raw values, as the desktop shows them. A heavy job
	 * ({@link #heavy}), refused over {@link #maxKymographValues()} with a suggested {@code tstep}.
	 */
	private static String handleKymograph(HttpExchange ex) throws Exception {
		Map<String, String> q = query(ex);
		StageTimer timer = new StageTimer(LG, "kymograph sim=" + q.get("sim") + " job=" + q.getOrDefault("job", "0")
				+ " domain=" + q.get("domain") + " var=" + q.get("var") + " path=" + q.get("path")
				+ (q.containsKey("tstep") ? " tstep=" + q.get("tstep") : ""));
		String json = kymograph(q, timer);
		timer.done(json.length() + " chars");
		return json;
	}

	private static String kymograph(Map<String, String> q, StageTimer timer) throws Exception {
		int tstep = parseTstep(q);
		FenicsBundleViews.BundleSource bundle = bundleSourceFor(q);
		if (bundle != null) {
			return heavy(() -> FenicsBundleViews.kymograph(bundle, q, tstep), timer);
		}
		DataSource source = sourceFor(q);
		VtuMode mode = vtuMode(source);
		timer.lap("source");
		if (mode != null) {
			return heavy(() -> vtuKymograph(source, q, mode, tstep, timer), timer);
		}
		String domain = domainOf(q, source);
		timer.lap("domain");
		String varName = q.get("var");
		if (varName == null || varName.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'var'");
		}
		DataIdentifier variable = null;
		for (DataIdentifier id : dataIdentifiers(source)) {
			if (id.getName().equals(varName)) {
				variable = id;
			}
		}
		if (variable == null) {
			throw new IllegalArgumentException("unknown variable '" + varName + "'");
		}
		timer.lap("variable");
		cbit.vcell.math.VariableType type = variable.getVariableType();
		if (type.equals(cbit.vcell.math.VariableType.MEMBRANE)) {
			// a curve along the membrane, as the desktop selects and samples it
			String membraneDomain = variable.getDomain() != null ? variable.getDomain().getName() : domain;
			if (!isMembraneDomain(source, membraneDomain)) {
				throw new IllegalArgumentException("'" + varName + "' is a membrane variable; its domain is not a membrane");
			}
			return heavy(() -> fvMembraneKymograph(source, membraneDomain, varName, q.get("path"), q.get("plane"), tstep,
					timer), timer);
		}
		if (type.equals(cbit.vcell.math.VariableType.MEMBRANE_REGION)) {
			throw new IllegalArgumentException("a kymograph of a membrane region variable is not supported ('" + varName
					+ "' has one value per membrane region)");
		}
		if (!type.equals(cbit.vcell.math.VariableType.VOLUME)) {
			throw new IllegalArgumentException("a kymograph needs a volume variable; '" + varName + "' is "
					+ type.getTypeName());
		}
		boolean raw = "1".equals(q.get("raw")) || "true".equalsIgnoreCase(q.get("raw"));
		final String dom = domain;
		return heavy(() -> fvKymograph(source, dom, varName, q.get("path"), tstep, raw, timer), timer);
	}

	/** {@code tstep}, the stride over the saved times: 1 when absent. */
	private static int parseTstep(Map<String, String> q) {
		String param = q.get("tstep");
		if (param == null || param.isEmpty()) {
			return 1;
		}
		int tstep;
		try {
			tstep = Integer.parseInt(param.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("malformed 'tstep' '" + param + "'");
		}
		if (tstep < 1) {
			throw new IllegalArgumentException("'tstep' must be at least 1");
		}
		return tstep;
	}

	/**
	 * {@code /kymograph} for a run served through the VTU seam: evenly spaced samples along the line
	 * ({@link BodyFittedKymograph}), each reading the value of the cell holding it, one {@code getVtuMeshData}
	 * per returned time for all samples. Chombo's mesh is static, so the samples are located once. A
	 * MovingBoundary mesh differs per saved time, so the samples are located again at every time: a fixed
	 * lab-frame line the moving boundary passes through. {@code raw} is finite-volume only and ignored here.
	 */
	private static String vtuKymograph(DataSource source, Map<String, String> q, VtuMode mode, int tstep, StageTimer timer)
			throws Exception {
		String varName = q.get("var");
		if (varName == null || varName.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'var'");
		}
		String domain = q.getOrDefault("domain", "");
		if (domain.isEmpty()) {
			domain = vtuDomains(source).get(0);
		}
		VtuVarInfo varInfo = vtuVarInfoFor(source, varName);
		if (varInfo.bMeshVariable || varInfo.dataType != VtuVarInfo.DataType.CellData) {
			throw new IllegalArgumentException("a kymograph needs a cell-data variable; '" + varName + "' is not one");
		}
		if (varInfo.variableDomain == cbit.vcell.math.VariableType.VariableDomain.VARIABLEDOMAIN_MEMBRANE) {
			throw new IllegalArgumentException("membrane kymographs are not supported yet ('" + varName
					+ "' is a membrane variable)");
		}
		double[] times = source.dataManager.getDataSetTimes(source.vcdID);
		if (times == null || times.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}
		final String dom = domain;
		PointSeries.Rows rows = new PointSeries.Rows() {
			@Override
			public VtuGridParser.VtuGrid grid(int row) throws Exception {
				return mbGrid(source, dom, mode == VtuMode.TIME_VARYING ? row : 0);
			}

			@Override
			public double[] values(int row) throws Exception {
				return source.dataManager.getVtuMeshData(emptyOutputContext(), source.vcdID, varInfo, times[row]);
			}
		};
		VtuGridParser.VtuGrid first = rows.grid(0);
		timer.lap("grid");
		double[][] path = BodyFittedKymograph.parsePath(q.get("path"), isVolume3D(first) ? null : first.points[2]);
		int n = BodyFittedKymograph.sampleCount(q.get("samples"), FvLineSampler.length(path), first.meanCellDiameter());
		String json = BodyFittedKymograph.json(varName, domain, PointSeries.Location.CELL, path, n, times, tstep, rows,
				mode == VtuMode.TIME_VARYING);
		timer.lap("rows[" + n + " samples × " + strideCount(times.length, tstep) + " times]");
		return json;
	}

	private static String fvKymograph(DataSource source, String domain, String varName, String pathParam,
			int tstep, boolean raw, StageTimer timer) throws Exception {
		cbit.vcell.solvers.CartesianMesh mesh = solverMesh(source);
		timer.lap("mesh");
		boolean flat = mesh.getGeometryDimension() < 3;
		// a 2D path lies in the plane the served grid lies in; its vertices may leave out z
		Double planeZ = flat ? grid(source, domain).getPoints().get(0).getZ() : null;
		double[][] path = PointSeries.parsePoints(pathParam, planeZ, "path", FvLineSampler.MAX_PATH_VERTICES, "path vertices");
		if (flat) {
			for (double[] v : path) {
				v[2] = planeZ;
			}
		}
		path = FvLineSampler.checkPath(mesh, path);
		FvLineSampler.Samples samples = FvLineSampler.sample(mesh, path);
		int n = samples.size();
		timer.lap("sample");

		double[] allTimes = source.dataManager.getDataSetTimes(source.vcdID);
		if (allTimes == null || allTimes.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}
		timer.lap("times");
		checkValueLimit(n, allTimes.length, tstep);
		// row 0 the times, row 1 + i the values at sample i
		double[][] timesAndValues = kymographSeries(source, varName, samples.volumeIndex(), samples.membraneIndex(), false,
				allTimes, tstep);
		timer.lap("timeseries[" + n + " samples, " + (samples.membraneIndex() != null ? "with" : "no") + " crossings]");
		double[] times = timesAndValues[0];
		int[] timeIndices = new int[times.length];
		for (int r = 0, k = 0; r < times.length; r++) {
			while (k < allTimes.length - 1 && allTimes[k] != times[r]) {
				k++;
			}
			timeIndices[r] = k;
		}

		DomainIndex domainIndex = domainIndex(source, domain);
		timer.lap("domainIndex");
		boolean[] inDomain = new boolean[n];
		int[] cell = new int[n];
		for (int i = 0; i < n; i++) {
			cell[i] = domainIndex.cellOf(samples.volumeIndex()[i]);
			inDomain[i] = cell[i] >= 0;
		}
		double[][] values = new double[times.length][n];
		for (int r = 0; r < times.length; r++) {
			for (int i = 0; i < n; i++) {
				values[r][i] = raw || inDomain[i] ? timesAndValues[1 + i][r] : Double.NaN;
			}
		}
		int[] membraneIndex = samples.membraneIndex();
		if (membraneIndex == null) {
			membraneIndex = new int[n];
			Arrays.fill(membraneIndex, -1);
		}
		String json = fvKymographJson(varName, domain, samples.sampling().json, ",\"raw\":" + raw, path,
				FvLineSampler.length(path), times, timeIndices, samples.arcLength(), samples.points(), samples.volumeIndex(),
				membraneIndex, inDomain, cell, values);
		timer.lap("json");
		return json;
	}

	/**
	 * {@code /kymograph} of a finite-volume membrane variable: a curve along the membrane between the picks,
	 * selected and sampled as the desktop does ({@link FvMembraneCurve}: one sample per membrane element, arc
	 * length along the membrane), in the only slice of a 2D run or, in 3D, the slice normal to {@code plane}
	 * nearest the first pick. The values come from ONE {@link TimeSeriesJobSpec} over the samples' membrane
	 * indices, as {@code KymographPanel} builds it for a membrane variable (no crossing indices).
	 */
	private static String fvMembraneKymograph(DataSource source, String domain, String varName, String pathParam,
			String planeParam, int tstep, StageTimer timer) throws Exception {
		cbit.vcell.solvers.CartesianMesh mesh = solverMesh(source);
		timer.lap("mesh");
		boolean flat = mesh.getGeometryDimension() < 3;
		Double planeZ = flat ? grid(source, domain).getPoints().get(0).getZ() : null;
		double[][] path = BodyFittedKymograph.parsePath(pathParam, planeZ);
		int axis = flat ? org.vcell.util.Coordinate.Z_AXIS : FvMembraneCurve.axisOf(planeParam);
		DomainIndex domainIndex = domainIndex(source, domain);
		FvMembraneCurve curve = FvMembraneCurve.select(mesh, domainIndex.inDomain, path, axis, BodyFittedKymograph.MAX_SAMPLES,
				flat ? planeZ : 0);
		int n = curve.size();
		timer.lap("curve");

		double[] allTimes = source.dataManager.getDataSetTimes(source.vcdID);
		if (allTimes == null || allTimes.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}
		timer.lap("times");
		checkValueLimit(n, allTimes.length, tstep);
		double[][] timesAndValues = kymographSeries(source, varName, curve.membraneIndex, null, true, allTimes, tstep);
		timer.lap("timeseries[" + n + " samples]");
		double[] times = timesAndValues[0];
		int[] timeIndices = new int[times.length];
		for (int r = 0, k = 0; r < times.length; r++) {
			while (k < allTimes.length - 1 && allTimes[k] != times[r]) {
				k++;
			}
			timeIndices[r] = k;
		}
		int[] volumeIndex = new int[n];
		Arrays.fill(volumeIndex, -1);
		boolean[] inDomain = new boolean[n];
		Arrays.fill(inDomain, true);
		int[] cell = new int[n];
		double[][] values = new double[times.length][n];
		for (int i = 0; i < n; i++) {
			cell[i] = domainIndex.cellOf(curve.membraneIndex[i]); // the sample's face in /grid's list
			for (int r = 0; r < times.length; r++) {
				values[r][i] = timesAndValues[1 + i][r];
			}
		}
		StringBuilder extra = new StringBuilder();
		if (!flat) {
			extra.append(",\"slice\":{\"axis\":\"").append("xyz".charAt(curve.normalAxis)).append("\",\"index\":")
					.append(curve.slice).append('}');
		}
		// the curve as the viewer draws it: the desktop's points and snapped picks, mapped onto the drawn membrane faces
		extra.append(",\"drawnPath\":[");
		for (int k = 0; k < curve.drawnWaypoints.length; k++) {
			extra.append(k > 0 ? "," : "");
			appendDoubles(extra, curve.drawnWaypoints[k], 3);
		}
		extra.append("],\"drawnPoints\":[");
		for (int i = 0; i < n; i++) {
			for (int a = 0; a < 3; a++) {
				extra.append(i > 0 || a > 0 ? "," : "").append(curve.drawnPoints[i][a]);
			}
		}
		extra.append(']');
		String json = fvKymographJson(varName, domain, "membrane", extra.toString(), curve.waypoints, curve.arcLength[n - 1],
				times, timeIndices, curve.arcLength, curve.points, volumeIndex, curve.membraneIndex, inDomain, cell, values);
		timer.lap("json");
		return json;
	}

	/**
	 * A finite-volume kymograph response ({@code location: "cell"}).
	 *
	 * @param extra more top-level fields, as {@code ,"key":value…} (or empty)
	 * @param values {@code values[row][sample]}, NaN for a gap
	 */
	private static String fvKymographJson(String varName, String domain, String sampling, String extra, double[][] path,
			double pathLength, double[] times, int[] timeIndices, double[] arcLength, double[][] points, int[] volumeIndex,
			int[] membraneIndex, boolean[] inDomain, int[] cell, double[][] values) {
		int n = arcLength.length;
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (double[] row : values) {
			for (double v : row) {
				if (Double.isFinite(v)) {
					min = Math.min(min, v);
					max = Math.max(max, v);
				}
			}
		}
		if (min > max) { // no value: the line lies outside the domain
			min = 0;
			max = 0;
		}

		StringBuilder sb = new StringBuilder(512 + 64 * n + 20 * n * times.length);
		sb.append("{\"name\":\"").append(jsonEscape(varName)).append('"');
		sb.append(",\"domain\":\"").append(jsonEscape(domain)).append('"');
		sb.append(",\"location\":\"cell\"");
		sb.append(",\"sampling\":\"").append(sampling).append('"');
		sb.append(extra);
		sb.append(",\"path\":[");
		for (int v = 0; v < path.length; v++) {
			sb.append(v > 0 ? "," : "");
			appendDoubles(sb, path[v], 3);
		}
		sb.append("],\"pathLength\":").append(pathLength);
		sb.append(",\"times\":");
		appendDoubles(sb, times, times.length);
		sb.append(",\"timeIndices\":").append(Arrays.toString(timeIndices).replace(" ", ""));
		sb.append(",\"samples\":{\"arcLength\":");
		appendDoubles(sb, arcLength, n);
		sb.append(",\"points\":[");
		for (int i = 0; i < n; i++) {
			double[] p = points[i];
			sb.append(i > 0 ? "," : "");
			for (int a = 0; a < 3; a++) {
				sb.append(a > 0 ? "," : "").append(p[a]);
			}
		}
		sb.append("],\"volumeIndex\":").append(Arrays.toString(volumeIndex).replace(" ", ""));
		sb.append(",\"membraneIndex\":").append(Arrays.toString(membraneIndex).replace(" ", ""));
		sb.append(",\"inDomain\":").append(Arrays.toString(inDomain).replace(" ", ""));
		// the sample's voxel (or membrane face) in /grid's list, -1 outside the domain: the viewer probes a sample there
		sb.append(",\"cell\":").append(Arrays.toString(cell).replace(" ", ""));
		sb.append("},\"values\":[");
		for (int r = 0; r < times.length; r++) {
			sb.append(r > 0 ? "," : "");
			appendDoubles(sb, values[r], n);
		}
		sb.append("],\"range\":[").append(min).append(',').append(max).append("]}");
		return sb.toString();
	}

	private static synchronized DomainIndex domainIndex(DataSource source, String domain) throws Exception {
		String key = source.vcdID.getID() + "/" + domain;
		DomainIndex cached = domainIndexCache.get(key);
		if (cached == null) {
			cached = new DomainIndex(new Cells(grid(source, domain)));
			domainIndexCache.put(key, cached);
		}
		return cached;
	}

	/**
	 * {@code /stats?sim=<simKey>&job=<n>[&var=<a,b,c>]} — min, max and spatial mean per saved time
	 * for the named volume and membrane variables (default: all of them), each computed over its own domain.
	 * <p>
	 * This is the aggregation the issue insists must NOT happen client-side: computing these curves
	 * in the browser would mean pulling every timestep of every selected variable. One
	 * {@link TimeSeriesJobSpec} with {@code calcSpaceStats} carries all the variables, so the
	 * reduction runs next to the reader in a single pass.
	 */
	private static String handleStats(HttpExchange ex) throws Exception {
		FenicsBundleViews.BundleSource bundle = bundleSourceFor(query(ex));
		if (bundle != null) {
			return FenicsBundleViews.stats(bundle, query(ex));
		}
		Map<String, String> q = query(ex);
		DataSource source = sourceFor(q);
		VtuMode statsMode = vtuMode(source);
		if (statsMode != null) {
			return handleStatsVtu(source, q, statsMode);
		}
		DataIdentifier[] ids = dataIdentifiers(source);
		Set<String> requested = null;
		String varParam = q.get("var");
		if (varParam != null && !varParam.isEmpty()) {
			requested = new LinkedHashSet<>(Arrays.asList(varParam.split(",")));
		}
		List<DataIdentifier> chosen = new ArrayList<>();
		for (DataIdentifier id : ids) {
			// volume variables over their voxels, membrane variables over their membrane elements (area-weighted)
			if (!id.getVariableType().equals(cbit.vcell.math.VariableType.VOLUME)
					&& !id.getVariableType().equals(cbit.vcell.math.VariableType.MEMBRANE)) {
				continue;
			}
			if (requested == null || requested.contains(id.getName())) {
				chosen.add(id);
			}
		}
		if (chosen.isEmpty()) {
			throw new IllegalArgumentException("no volume or membrane variables match " + varParam);
		}
		double[] times = source.dataManager.getDataSetTimes(source.vcdID);
		if (times == null || times.length == 0) {
			throw new IllegalArgumentException("run " + source.vcdID.getID() + " has no saved times");
		}

		// each variable's stats run over ITS domain's cells; domains repeat, so index once each
		List<String> domains = readMesh(source).getVolumeDomainNames();
		Map<String, int[]> indicesByDomain = new HashMap<>();
		String[] names = new String[chosen.size()];
		String[] varDomains = new String[chosen.size()];
		int[][] indices = new int[chosen.size()][];
		for (int v = 0; v < chosen.size(); v++) {
			DataIdentifier id = chosen.get(v);
			names[v] = id.getName();
			varDomains[v] = id.getDomain() != null ? id.getDomain().getName() : domains.get(0);
			final String dom = varDomains[v];
			indices[v] = indicesByDomain.computeIfAbsent(dom, d -> {
				try {
					return new Cells(grid(source, d)).globalIndices.clone();
				} catch (Exception e) {
					throw new RuntimeException("building index set for domain '" + d + "'", e);
				}
			});
		}

		TimeSeriesJobSpec spec = new TimeSeriesJobSpec(names, indices, times[0], 1,
				times[times.length - 1], true, false,
				VCDataJobID.createVCDataJobID(source.vcdID.getOwner(), true));
		TSJobResultsSpaceStats stats = (TSJobResultsSpaceStats) source.dataManager
				.getTimeSeriesValues(emptyOutputContext(), source.vcdID, spec);
		// on a uniform Cartesian grid the volume weighting is uniform; prefer weighted when present
		double[][] means = stats.getWeightedMean() != null ? stats.getWeightedMean()
				: stats.getUnweightedMean();

		double[] statTimes = stats.getTimes();
		StringBuilder sb = new StringBuilder(64 * statTimes.length * names.length + 512);
		sb.append("{\"times\":");
		appendDoubles(sb, statTimes, statTimes.length);
		sb.append(",\"series\":[");
		for (int v = 0; v < names.length; v++) {
			if (v > 0) {
				sb.append(',');
			}
			sb.append("{\"name\":\"").append(jsonEscape(names[v])).append('"');
			sb.append(",\"domain\":\"").append(jsonEscape(varDomains[v])).append('"');
			sb.append(",\"min\":");
			appendDoubles(sb, stats.getMinimums()[v], statTimes.length);
			sb.append(",\"max\":");
			appendDoubles(sb, stats.getMaximums()[v], statTimes.length);
			sb.append(",\"mean\":");
			appendDoubles(sb, means[v], statTimes.length);
			sb.append('}');
		}
		sb.append("]}");
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
		if (!domains.contains(domain) && !membraneDomains(source).contains(domain)) {
			// an unknown name otherwise yields an empty mesh and a confusing NPE downstream
			throw new IllegalArgumentException("unknown domain '" + domain + "'; this run has " + domains
					+ " and the membranes " + membraneDomains(source));
		}
		return domain;
	}

	/** The run's membrane domains that have membrane elements, in the subdomain file's order. */
	private static List<String> membraneDomains(DataSource source) throws Exception {
		List<String> cached = source.membraneDomains;
		if (cached == null) {
			org.vcell.vis.vcell.CartesianMesh mesh = readMesh(source);
			List<String> out = new ArrayList<>();
			for (String name : mesh.getMembraneDomainNames()) {
				if (!mesh.getMembraneElements(name).isEmpty()) {
					out.add(name);
				}
			}
			source.membraneDomains = cached = List.copyOf(out);
		}
		return cached;
	}

	private static boolean isMembraneDomain(DataSource source, String domain) throws Exception {
		return membraneDomains(source).contains(domain);
	}

	/**
	 * Refuses a variable on the other kind of domain: a membrane variable's values are indexed by membrane
	 * element and a volume variable's by voxel, so reading one through the other's cells gives nonsense.
	 */
	private static void checkVariableFitsDomain(DataSource source, String varName, String domain) throws Exception {
		boolean membraneDomain = isMembraneDomain(source, domain);
		for (DataIdentifier id : dataIdentifiers(source)) {
			if (id.getName().equals(varName)) {
				boolean membraneVariable = id.getVariableType().equals(cbit.vcell.math.VariableType.MEMBRANE);
				if (membraneVariable != membraneDomain) {
					throw new IllegalArgumentException("'" + varName + "' is a " + (membraneVariable ? "membrane" : "volume")
							+ " variable, but '" + domain + "' is a " + (membraneDomain ? "membrane" : "volume") + " domain");
				}
				return;
			}
		}
	}

	/**
	 * Identifies the geometry a response belongs to. Today a dataset and domain pin it down; for a
	 * moving boundary this is where the ALE segment would join the key.
	 */
	private static String geometryId(DataSource source, String domain) {
		return source.vcdID.getID() + "/" + domain;
	}

	/**
	 * The finite-volume cells of a mesh, uniformly for 2D and 3D. A 3D domain arrives as
	 * {@link VisVoxel}s and a 2D one as in-plane {@link VisPolygon} quads — thrift types with no
	 * common interface, and the one NOT produced is left null, which is how a 2D run used to 500
	 * on {@code /grid}. Everything downstream (geometry, field values, time series, statistics)
	 * needs only the point indices and the solver's global index, so that is the whole interface.
	 */
	private static final class Cells {
		final int vtkCellType;
		final List<List<Integer>> pointIndices;
		/** the solver's index of each cell's element: a volume index, or a membrane index on a membrane domain */
		final int[] globalIndices;
		/** the mesh's dimension: a 3D membrane's quads are 3D, a 2D domain's quads 2D */
		final int dimension;

		Cells(VisMesh visMesh) {
			dimension = visMesh.getDimension();
			List<VisVoxel> voxels = visMesh.getVisVoxels();
			List<org.vcell.vis.vismesh.thrift.VisPolygon> polygons = visMesh.getPolygons();
			List<org.vcell.vis.vismesh.thrift.VisLine> lines = visMesh.getVisLines();
			if (voxels != null && !voxels.isEmpty()) {
				vtkCellType = VTK_VOXEL;
				pointIndices = new ArrayList<>(voxels.size());
				globalIndices = new int[voxels.size()];
				for (int c = 0; c < voxels.size(); c++) {
					pointIndices.add(voxels.get(c).getPointIndices());
					globalIndices[c] = voxels.get(c).getFiniteVolumeIndex().getGlobalIndex();
				}
			} else if (polygons != null && !polygons.isEmpty()) {
				vtkCellType = VTK_QUAD;
				pointIndices = new ArrayList<>(polygons.size());
				globalIndices = new int[polygons.size()];
				for (int c = 0; c < polygons.size(); c++) {
					pointIndices.add(polygons.get(c).getPointIndices());
					globalIndices[c] = polygons.get(c).getFiniteVolumeIndex().getGlobalIndex();
				}
			} else if (lines != null && !lines.isEmpty()) {
				vtkCellType = VTK_LINE;
				pointIndices = new ArrayList<>(lines.size());
				globalIndices = new int[lines.size()];
				for (int c = 0; c < lines.size(); c++) {
					pointIndices.add(List.of(lines.get(c).getP1(), lines.get(c).getP2()));
					globalIndices[c] = lines.get(c).getFiniteVolumeIndex().getGlobalIndex();
				}
			} else {
				throw new IllegalArgumentException("mesh has neither voxels nor polygons; "
						+ "1D runs are not supported by the field viewer");
			}
		}

		int size() {
			return globalIndices.length;
		}
	}

	// ---------------------------------------------------------------------
	// Data access
	// ---------------------------------------------------------------------

	/**
	 * Make a dataset available to the viewer. The caller supplies the data manager it already uses
	 * for that run, which is what keeps local and remote runs on one code path here.
	 *
	 * @param mathDesc supplies the domain names; the mesh carries subvolume numbers but not names
	 * @param simName human-readable name for the run (registration is the only moment anyone holds
	 *            it — the data files carry only keys); may be null
	 */
	public static void register(VCSimulationDataIdentifier vcdID, VCDataManager dataManager,
			MathDescription mathDesc, String simName) throws MathException {
		register(vcdID, dataManager, CartesianMeshBuilder.fromMathDescription(mathDesc), simName);
	}

	/** For callers that already hold the domain naming and have no math description to hand. */
	public static void register(VCSimulationDataIdentifier vcdID, VCDataManager dataManager,
			SubdomainInfo subdomainInfo, String simName) {
		dataSources.put(key(vcdID), new DataSource(vcdID, dataManager, subdomainInfo, simName));
		LG.debug("field viewer dataset registered: {}", vcdID.getID());
	}

	/**
	 * Make a FEniCSx results bundle available to the viewer, under the simulation key and job the
	 * viewer URL names. It is read from its directory on every request, so a bundle still being
	 * written can be viewed.
	 */
	public static void registerBundle(String simKey, int jobIndex, java.io.File bundleDir, String simName) {
		registerBundle(simKey, jobIndex, org.vcell.solver.fenics.BundleStore.directory(bundleDir), simName);
	}

	/** A bundle read through any store, e.g. from the data server for a cluster run (cached). */
	public static void registerBundle(String simKey, int jobIndex, org.vcell.solver.fenics.BundleStore store, String simName) {
		bundleSources.put(simKey + ":" + jobIndex, new FenicsBundleViews.BundleSource(simKey, jobIndex, store, simName));
		LG.debug("field viewer FEniCSx bundle registered: {} job {} from {}", simKey, jobIndex, store.describe());
	}

	public static void unregisterBundle(String simKey, int jobIndex) {
		bundleSources.remove(simKey + ":" + jobIndex);
	}

	private static FenicsBundleViews.BundleSource bundleSourceFor(Map<String, String> q) {
		String sim = q.get("sim");
		if (sim == null || sim.isEmpty()) {
			return null;
		}
		return bundleSources.get(sim + ":" + (q.containsKey("job") ? q.get("job") : "0"));
	}

	/** Drop a dataset, so later requests fail cleanly rather than serving results nobody is viewing. */
	public static synchronized void unregister(VCSimulationDataIdentifier vcdID) {
		if (dataSources.remove(key(vcdID)) != null) {
			meshCache.keySet().removeIf(cached -> cached.startsWith(vcdID.getID() + "/"));
			domainIndexCache.keySet().removeIf(cached -> cached.startsWith(vcdID.getID() + "/"));
			mbGridCache.keySet().removeIf(cached -> cached.startsWith(vcdID.getID() + "/"));
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
		org.vcell.vis.vcell.CartesianMesh mesh = source.visMesh;
		if (mesh == null) {
			mesh = CartesianMeshBuilder.fromSolverMesh(solverMesh(source), source.subdomainInfo);
			source.visMesh = mesh;
		}
		return mesh;
	}

	/** The solver's mesh, read from the data manager once per dataset ({@link DataSource#solverMesh}). */
	private static cbit.vcell.solvers.CartesianMesh solverMesh(DataSource source) throws Exception {
		cbit.vcell.solvers.CartesianMesh mesh = source.solverMesh;
		if (mesh == null) {
			mesh = source.dataManager.getMesh(source.vcdID);
			source.solverMesh = mesh;
		}
		return mesh;
	}

	/**
	 * The run's variables and functions, read from the data manager once per dataset
	 * ({@link DataSource#dataIdentifiers}); an empty answer, as from a run with no output yet, is not kept.
	 */
	private static DataIdentifier[] dataIdentifiers(DataSource source) throws Exception {
		DataIdentifier[] ids = source.dataIdentifiers;
		if (ids == null) {
			ids = source.dataManager.getDataIdentifiers(emptyOutputContext(), source.vcdID);
			if (ids != null && ids.length > 0) {
				source.dataIdentifiers = ids;
			}
		}
		return ids;
	}

	private static synchronized VisMesh grid(DataSource source, String domainName) throws Exception {
		String key = source.vcdID.getID() + "/" + domainName;
		VisMesh cached = meshCache.get(key);
		if (cached != null) {
			return cached;
		}
		VisMesh visMesh = new CartesianMeshMapping().fromMeshData(readMesh(source), domainName,
				!isMembraneDomain(source, domainName));
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
			} catch (BusyException e) {
				respond(ex, 503, "application/json",
						("{\"error\":\"" + jsonEscape(e.getMessage()) + "\",\"busy\":true}").getBytes(StandardCharsets.UTF_8));
			} catch (TooManyValuesException e) {
				respond(ex, 400, "application/json", ("{\"error\":\"" + jsonEscape(e.getMessage())
						+ "\",\"suggestedTstep\":" + e.suggestedTstep + "}").getBytes(StandardCharsets.UTF_8));
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
	static void appendDoubles(StringBuilder sb, double[] values, int count) {
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

	static String jsonEscape(String s) {
		return String.valueOf(s).replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
