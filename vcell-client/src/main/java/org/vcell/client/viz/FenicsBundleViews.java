package org.vcell.client.viz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.vcell.solver.fenics.BundleStore;
import org.vcell.solver.fenics.FenicsBundle;

/**
 * The field viewer's endpoints for a FEniCSx results bundle ({@code <SimID_..._>.fenics/}, vcell-fenics
 * ADR 010) — the same JSON contract {@link FieldViewerServer} serves for its other data sources, with
 * two differences that follow from the finite-element solution: values are <b>point</b> data (one per
 * mesh vertex, {@code "location":"point"}), and the statistics are the solver's own integrals, read
 * from the bundle rather than recomputed ({@code "weighting":"integral"}).
 * <p>
 * The bundle is re-read on every request, so a run that is still writing is viewable: the times grow
 * as rows land. Meshes are parsed once per segment and domain.
 */
final class FenicsBundleViews {

	/** A bundle a window has made available, keyed like the other data sources by sim key and job. */
	static final class BundleSource {
		final String simId;
		final int jobIndex;
		final BundleStore store;
		final String simName;
		/** the run's VCell functions (from its simulation), evaluated on the bundle's variables */
		final FenicsFunctions functions;
		private final Map<String, VtuGridParser.VtuGrid> grids = new ConcurrentHashMap<>();
		private final Map<String, int[]> pointMaps = new ConcurrentHashMap<>();

		BundleSource(String simId, int jobIndex, BundleStore store, String simName) {
			this(simId, jobIndex, store, simName, FenicsFunctions.NONE);
		}

		BundleSource(String simId, int jobIndex, BundleStore store, String simName, FenicsFunctions functions) {
			this.simId = simId;
			this.jobIndex = jobIndex;
			this.store = store;
			this.simName = simName;
			this.functions = functions == null ? FenicsFunctions.NONE : functions;
		}

		FenicsBundle bundle() throws IOException {
			return FenicsBundle.open(store);
		}

		private final Map<VtuGridParser.VtuGrid, Double> measures = new ConcurrentHashMap<>();

		/** the length, area or volume of {@code domain} in the segment holding {@code row} */
		double measure(FenicsBundle bundle, String domain, int row) throws Exception {
			VtuGridParser.VtuGrid grid = grid(bundle, domain, row);
			Double m = measures.get(grid);
			if (m == null) {
				double sum = 0;
				for (double cm : VtuGridParser.cellMeasures(grid)) {
					sum += cm;
				}
				m = sum;
				measures.put(grid, m);
			}
			return m;
		}

		/**
		 * The membrane's point map onto an adjacent {@code compartment} in the segment holding {@code row}
		 * ({@link FenicsBundle#adjacentPoints}), read once per segment.
		 */
		int[] pointMap(FenicsBundle bundle, String membrane, String compartment, int row) throws IOException {
			String key = bundle.segmentOf(row).segment().index() + "/" + membrane + "/" + compartment;
			int[] map = pointMaps.get(key);
			if (map == null) {
				map = bundle.adjacentPoints(membrane, compartment, row);
				pointMaps.put(key, map);
			}
			return map;
		}

		/**
		 * The domain's mesh at output row {@code row}: the segment's mesh, and — in a moving (ALE) segment —
		 * with that row's recorded point positions (same topology, moved points).
		 */
		VtuGridParser.VtuGrid grid(FenicsBundle bundle, String domain, int row) throws Exception {
			FenicsBundle.Segment segment = bundle.segmentOf(row).segment();
			boolean moving = "ale".equals(segment.motion());
			String key = segment.index() + "/" + domain + (moving ? "@t" + row : "");
			VtuGridParser.VtuGrid grid = grids.get(key);
			if (grid == null) {
				String segmentKey = segment.index() + "/" + domain;
				VtuGridParser.VtuGrid mesh = grids.get(segmentKey);
				if (mesh == null) {
					mesh = VtuGridParser.parse(bundle.meshBytes(domain, row));
					grids.put(segmentKey, mesh);
				}
				grid = moving
						? new VtuGridParser.VtuGrid(bundle.coords(domain, row), mesh.cells, mesh.cellTypes, mesh.cellFaces)
						: mesh;
				grids.put(key, grid);
			}
			return grid;
		}
	}

	private FenicsBundleViews() {
	}

	/** the most bytes of rows one prefetch window asks for (see {@link RowPrefetcher}) */
	static final long PREFETCH_WINDOW_BYTES = 64L * 1024 * 1024;

	/** the most rows one prefetch window asks for */
	static final int PREFETCH_WINDOW_ROWS = 256;

	/**
	 * Fetches a run of rows ahead of the loop that reads them, a window at a time, so a bundle read from
	 * the data server costs a few batched calls per window instead of one call per row
	 * ({@link BundleStore#prefetch}). The window is sized to the rows' raw bytes, so a fine 3D mesh does
	 * not pull more into memory than the cache keeps. {@code rows} is the order the loop reads them in.
	 */
	static final class RowPrefetcher {
		interface Fetch {
			void rows(int[] rows) throws IOException;
		}

		private final int[] rows;
		private final Map<Integer, Integer> position = new java.util.HashMap<>();
		private final int window;
		private final Fetch fetch;
		private int fetchedUpTo = 0;

		RowPrefetcher(int[] rows, long bytesPerRow, Fetch fetch) {
			this.rows = rows;
			for (int i = rows.length - 1; i >= 0; i--) {
				position.put(rows[i], i);
			}
			this.window = (int) Math.max(1, Math.min(PREFETCH_WINDOW_ROWS, PREFETCH_WINDOW_BYTES / Math.max(1, bytesPerRow)));
			this.fetch = fetch;
		}

		/** called before {@code row} is read: fetches its window if it has not been fetched */
		synchronized void before(int row) throws IOException {
			Integer at = position.get(row);
			if (at == null || at < fetchedUpTo) {
				return;
			}
			int end = Math.min(rows.length, at + window);
			fetch.rows(Arrays.copyOfRange(rows, at, end));
			fetchedUpTo = end;
		}

		static int[] allRows(int count) {
			int[] rows = new int[count];
			for (int i = 0; i < count; i++) {
				rows[i] = i;
			}
			return rows;
		}

		static int[] stridedRows(int count, int tstep) {
			int n = FieldViewerServer.strideCount(count, tstep);
			int[] rows = new int[n];
			for (int i = 0; i < n; i++) {
				rows[i] = i * tstep;
			}
			return rows;
		}
	}

	/** a prefetcher for a variable's values over {@code rows} */
	private static RowPrefetcher fieldPrefetcher(FenicsBundle bundle, String domain, String varName, int[] rows) {
		long bytesPerRow = 8L * bundle.domain(domain).numPoints();
		return new RowPrefetcher(rows, bytesPerRow, r -> bundle.prefetchField(domain, varName, r));
	}

	/**
	 * The rows of one variable -- stored, or a function of stored variables ({@link FenicsFunctions}) -- for
	 * {@link PointSeries#sample}, read the cheapest way the bundle allows. Once the points are located on a
	 * segment's mesh ({@link PointSeries.Rows#located}), the located cells' vertices are read for all of that
	 * segment's rows in one sampled call ({@link FenicsBundle#sampleFields}; a function's variables together):
	 * from a data server that samples, a kymograph's 201 rows cost one request carrying only those vertices.
	 * Otherwise -- a local bundle, an older data server, or a moving (ALE) mesh, where the cell changes every
	 * row -- whole rows are read, prefetched in windows ({@link RowPrefetcher}).
	 * <p>
	 * A row is handed back as a mesh-sized array holding the values at the needed vertices: a stored variable's
	 * own values, or the function evaluated AT each vertex (its variables there, the vertex's x, y, z in that
	 * row's mesh, the row's t). The interpolation then reads the same numbers in the same order as from a whole
	 * row, and a function's values are exactly those {@code /field} draws: bit-identical either way.
	 * <p>
	 * A membrane function of the adjacent volume values reads those from each compartment at the compartment
	 * vertices the membrane's vertices map to (the bundle's point map): one sampled call per domain read, per
	 * segment, since a sampled call reads arrays of one domain.
	 */
	static final class SampledRows implements PointSeries.Rows {
		private final BundleSource source;
		private final FenicsBundle bundle;
		private final String domain;
		private final String[] reads; // the stored variables read: the variable itself, or the function's arguments
		private final String[] readDomains; // the domain each is read on: this one, or a compartment beside a membrane
		private final FenicsFunctions.Compiled function; // null for a stored variable
		private final double[] times;
		private final int[] rows;
		private final RowPrefetcher[] wholeRows;
		private final RowPrefetcher coords;
		private boolean sampling;
		private final Map<Integer, double[][]> sampled = new java.util.HashMap<>();
		private int[] vertices = new int[0];
		private double[] scratch;
		private double[][] scratchArgs;
		private VtuGridParser.VtuGrid lastGrid;
		private int lastGridRow = -1;

		/** @param rows the output rows the loop will read, in its order */
		SampledRows(BundleSource source, FenicsBundle bundle, String domain, String varName, int[] rows) {
			this.source = source;
			this.bundle = bundle;
			this.domain = domain;
			this.function = source.functions.has(varName) && !isStored(bundle, domain, varName)
					? source.functions.compile(bundle, domain, varName) : null;
			this.reads = function != null ? function.variables : new String[] { varName };
			this.readDomains = function != null ? function.domains : new String[] { domain };
			this.times = times(bundle);
			this.rows = rows;
			this.wholeRows = new RowPrefetcher[reads.length];
			for (int k = 0; k < reads.length; k++) {
				wholeRows[k] = fieldPrefetcher(bundle, readDomains[k], reads[k], rows);
			}
			this.coords = coordsPrefetcher(bundle, domain, rows);
			this.sampling = !bundle.isMoving() && reads.length > 0;
		}

		@Override
		public VtuGridParser.VtuGrid grid(int row) throws Exception {
			if (coords != null) {
				coords.before(row);
			}
			lastGrid = source.grid(bundle, domain, row);
			lastGridRow = row;
			return lastGrid;
		}

		/** the mesh of {@code row}: x, y, z of a function's vertices (on a moving mesh, that row's) */
		private VtuGridParser.VtuGrid gridOf(int row) throws Exception {
			return row == lastGridRow ? lastGrid : grid(row);
		}

		@Override
		public void located(VtuGridParser.VtuGrid grid, int row, int[] cells) throws Exception {
			java.util.TreeSet<Integer> needed = new java.util.TreeSet<>();
			for (int c : cells) {
				if (c >= 0) {
					for (int v : grid.cells[c]) {
						needed.add(v);
					}
				}
			}
			sample(needed, row, grid.numPoints());
		}

		/** the vertices {@code needed} from every row of the segment holding {@code row}: sampled if the store can */
		void sample(java.util.SortedSet<Integer> needed, int row, int numPoints) throws Exception {
			sampled.clear();
			vertices = needed.stream().mapToInt(Integer::intValue).toArray();
			if (scratch == null || scratch.length != numPoints) {
				scratch = new double[numPoints];
				Arrays.fill(scratch, Double.NaN);
				scratchArgs = new double[reads.length][numPoints];
			}
			if (!sampling || needed.isEmpty()) {
				return; // no point lies in the domain: no row's values will be asked for
			}
			int segment = bundle.segmentOf(row).segment().index();
			int[] segmentRows = Arrays.stream(rows).filter(r -> bundle.segmentOf(r).segment().index() == segment).toArray();
			double[][][] got = sampleByDomain(segmentRows);
			if (got == null) {
				sampling = false; // the store reads whole rows: so does this, from here on
				return;
			}
			for (int r = 0; r < segmentRows.length; r++) {
				double[][] perRead = new double[reads.length][];
				for (int k = 0; k < reads.length; k++) {
					perRead[k] = got[k][r];
				}
				sampled.put(segmentRows[r], perRead);
			}
		}

		/**
		 * {@code [read][row][i]}: each read's value at {@link #vertices}{@code [i]} of this domain -- for a read on
		 * an adjacent compartment, at the compartment vertex it maps to. One sampled call per domain read; null if
		 * the store does not sample.
		 */
		private double[][][] sampleByDomain(int[] segmentRows) throws Exception {
			double[][][] got = new double[reads.length][][];
			for (String on : new LinkedHashSet<>(Arrays.asList(readDomains))) {
				List<Integer> ks = new ArrayList<>();
				for (int k = 0; k < reads.length; k++) {
					if (readDomains[k].equals(on)) {
						ks.add(k);
					}
				}
				String[] names = ks.stream().map(k -> reads[k]).toArray(String[]::new);
				int[] at = vertices; // where each of this domain's vertices is, in the indices asked for
				int[] indices = vertices;
				if (!on.equals(domain)) {
					int[] map = source.pointMap(bundle, domain, on, segmentRows[0]);
					indices = Arrays.stream(vertices).map(v -> map[v]).filter(m -> m >= 0).sorted().distinct().toArray();
					at = new int[vertices.length];
					for (int i = 0; i < vertices.length; i++) {
						at[i] = map[vertices[i]] < 0 ? -1 : Arrays.binarySearch(indices, map[vertices[i]]);
					}
				}
				double[][][] part = bundle.sampleFields(on, names, segmentRows, indices);
				if (part == null) {
					return null;
				}
				for (int j = 0; j < ks.size(); j++) {
					double[][] perRow = new double[segmentRows.length][vertices.length];
					for (int r = 0; r < segmentRows.length; r++) {
						for (int i = 0; i < vertices.length; i++) {
							perRow[r][i] = on.equals(domain) ? part[j][r][i] : at[i] < 0 ? Double.NaN : part[j][r][at[i]];
						}
					}
					got[ks.get(j)] = perRow;
				}
			}
			return got;
		}

		@Override
		public double[] values(int row) throws Exception {
			double[][] s = sampled.get(row);
			if (function == null) {
				if (s == null) {
					wholeRows[0].before(row);
					return bundle.field(domain, reads[0], row);
				}
				for (int i = 0; i < vertices.length; i++) {
					scratch[vertices[i]] = s[0][i];
				}
				return scratch;
			}
			double[][] args;
			if (s != null) {
				for (int k = 0; k < reads.length; k++) {
					for (int i = 0; i < vertices.length; i++) {
						scratchArgs[k][vertices[i]] = s[k][i];
					}
				}
				args = scratchArgs;
			} else {
				args = new double[reads.length][];
				for (int k = 0; k < reads.length; k++) {
					wholeRows[k].before(row);
					args[k] = argumentRow(source, bundle, domain, readDomains[k], reads[k], row);
				}
			}
			double[] points = gridOf(row).points;
			for (int v : vertices) {
				scratch[v] = function.at(times[row], points, v, args);
			}
			return scratch;
		}
	}

	/** whether {@code name} is a stored variable of {@code domain} (a stored variable wins over a function of that name) */
	static boolean isStored(FenicsBundle bundle, String domain, String name) {
		for (FenicsBundle.Variable v : bundle.getVariables()) {
			if (v.name().equals(name) && v.domain().equals(domain)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A function's values at every vertex of {@code domain}'s mesh at {@code row} ({@code /field}): its variables'
	 * whole rows, the row's mesh positions, the row's time.
	 */
	static double[] functionField(BundleSource source, FenicsBundle bundle, String domain, String name, int row) throws Exception {
		FenicsFunctions.Compiled f = source.functions.compile(bundle, domain, name);
		double[][] args = new double[f.variables.length][];
		for (int k = 0; k < args.length; k++) {
			args[k] = argumentRow(source, bundle, domain, f.domains[k], f.variables[k], row);
		}
		VtuGridParser.VtuGrid grid = source.grid(bundle, domain, row);
		double t = bundle.getTimes().get(row);
		double[] values = new double[grid.numPoints()];
		for (int v = 0; v < values.length; v++) {
			values[v] = f.at(t, grid.points, v, args);
		}
		return values;
	}

	/**
	 * A function argument's whole row at {@code domain}'s vertices: {@code variable} on {@code domain} itself, or
	 * -- on a membrane -- on the adjacent compartment {@code on}, carried to the membrane's vertices through the
	 * bundle's point map (NaN at a vertex the map leaves out).
	 */
	static double[] argumentRow(BundleSource source, FenicsBundle bundle, String domain, String on, String variable, int row)
			throws Exception {
		double[] values = bundle.field(on, variable, row);
		if (on.equals(domain)) {
			return values;
		}
		int[] map = source.pointMap(bundle, domain, on, row);
		double[] atMembrane = new double[map.length];
		for (int v = 0; v < map.length; v++) {
			atMembrane[v] = map[v] >= 0 ? values[map[v]] : Double.NaN;
		}
		return atMembrane;
	}

	/** a prefetcher for a moving domain's recorded point positions over {@code rows}; null if nothing moves */
	private static RowPrefetcher coordsPrefetcher(FenicsBundle bundle, String domain, int[] rows) {
		if (!bundle.isMoving()) {
			return null;
		}
		long bytesPerRow = 24L * bundle.domain(domain).numPoints();
		return new RowPrefetcher(rows, bytesPerRow, r -> bundle.prefetchCoords(domain, r));
	}

	private static FenicsBundle open(BundleSource source) throws IOException {
		FenicsBundle bundle = source.bundle();
		if (bundle.getTimes().isEmpty()) {
			throw new IllegalArgumentException("the FEniCSx run " + source.simId + " has not written any output yet");
		}
		return bundle;
	}

	private static String domainOf(FenicsBundle bundle, Map<String, String> q) {
		String domain = q.get("domain");
		if (domain == null || domain.isEmpty()) {
			for (FenicsBundle.Variable v : bundle.getVariables()) {
				return v.domain();
			}
			return bundle.getDomains().keySet().iterator().next();
		}
		bundle.domain(domain); // rejects an unknown name
		return domain;
	}

	/** nearest written row to {@code time}; the last one when no time is given */
	private static int rowFor(FenicsBundle bundle, Map<String, String> q) {
		List<Double> times = bundle.getTimes();
		String requested = q.get("time");
		if (requested == null || requested.isEmpty()) {
			return times.size() - 1;
		}
		double target = Double.parseDouble(requested);
		int best = 0;
		for (int i = 1; i < times.size(); i++) {
			if (Math.abs(times.get(i) - target) < Math.abs(times.get(best) - target)) {
				best = i;
			}
		}
		return best;
	}

	/**
	 * The geometry a row's values belong to: one per bundle for a fixed mesh, one per segment after a
	 * remesh, and one per row where the mesh moves (the viewer re-fetches the geometry when it changes).
	 */
	private static String geometryId(BundleSource source, FenicsBundle bundle, String domain, int row) {
		String base = source.simId + "/" + domain;
		if (bundle.isFixed()) {
			return base;
		}
		FenicsBundle.Segment segment = bundle.segmentOf(row).segment();
		return "ale".equals(segment.motion()) ? base + "@t" + row : base + "@s" + segment.index();
	}

	private static String requireVar(Map<String, String> q) {
		String varName = q.get("var");
		if (varName == null || varName.isEmpty()) {
			throw new IllegalArgumentException("missing required query parameter 'var'");
		}
		return varName;
	}

	private static double[] times(FenicsBundle bundle) {
		return bundle.getTimes().stream().mapToDouble(Double::doubleValue).toArray();
	}

	/** {@code /info} */
	static String info(BundleSource source) throws Exception {
		FenicsBundle bundle = source.bundle();
		double[] times = times(bundle);
		StringBuilder sb = new StringBuilder(1024);
		sb.append("{\"simId\":\"").append(FieldViewerServer.jsonEscape(source.simId)).append('"');
		if (source.simName != null && !source.simName.isEmpty()) {
			sb.append(",\"simName\":\"").append(FieldViewerServer.jsonEscape(source.simName)).append('"');
		}
		sb.append(",\"jobIndex\":").append(source.jobIndex);
		sb.append(",\"solver\":\"FEniCSx\",\"status\":\"").append(FieldViewerServer.jsonEscape(bundle.getStatus())).append('"');
		if (Double.isFinite(bundle.getProgress())) {
			sb.append(",\"progress\":").append(bundle.getProgress()); // fraction 0..1, for the viewer's live readout
		}
		sb.append(",\"times\":");
		FieldViewerServer.appendDoubles(sb, times, times.length);
		sb.append(",\"domains\":[");
		boolean first = true;
		for (String domain : bundle.getDomains().keySet()) {
			sb.append(first ? "" : ",").append('"').append(FieldViewerServer.jsonEscape(domain)).append('"');
			first = false;
		}
		sb.append("],\"variables\":[");
		first = true;
		for (FenicsBundle.Variable v : bundle.getVariables()) {
			sb.append(first ? "" : ",");
			first = false;
			sb.append("{\"name\":\"").append(FieldViewerServer.jsonEscape(v.name())).append('"');
			sb.append(",\"domain\":\"").append(FieldViewerServer.jsonEscape(v.domain())).append('"');
			sb.append(",\"location\":\"point\",\"isFunction\":false}");
		}
		// the run's functions, on each domain where they can be evaluated from the stored variables
		for (String domain : bundle.getDomains().keySet()) {
			for (String name : source.functions.namesFor(bundle, domain)) {
				if (isStored(bundle, domain, name)) {
					continue;
				}
				sb.append(first ? "" : ",");
				first = false;
				sb.append("{\"name\":\"").append(FieldViewerServer.jsonEscape(name)).append('"');
				sb.append(",\"domain\":\"").append(FieldViewerServer.jsonEscape(domain)).append('"');
				sb.append(",\"location\":\"point\",\"isFunction\":true}");
			}
		}
		sb.append(']');
		if (!bundle.getParticleSpecies().isEmpty()) {
			// a hybrid PDE/particle run: the viewer offers a particle layer, fetched from /particles
			sb.append(",\"particleSpecies\":[");
			first = true;
			for (String name : bundle.getParticleSpecies().keySet()) {
				sb.append(first ? "" : ",").append('"').append(FieldViewerServer.jsonEscape(name)).append('"');
				first = false;
			}
			sb.append(']');
		}
		sb.append('}');
		return sb.toString();
	}

	/** {@code /grid}: the domain's body-fitted mesh for the segment holding the requested time */
	static String grid(BundleSource source, Map<String, String> q) throws Exception {
		FenicsBundle bundle = open(source);
		String domain = domainOf(bundle, q);
		int row = rowFor(bundle, q);
		VtuGridParser.VtuGrid grid = source.grid(bundle, domain, row);
		FenicsBundle.Domain d = bundle.domain(domain);
		boolean uniform = true;
		for (int c = 1; c < grid.cellTypes.length; c++) {
			uniform &= grid.cellTypes[c] == grid.cellTypes[0];
		}
		StringBuilder sb = new StringBuilder(32 * grid.numPoints() + 32 * grid.cells.length + 512);
		sb.append("{\"geometryId\":\"").append(FieldViewerServer.jsonEscape(geometryId(source, bundle, domain, row))).append('"');
		// the embedding dimension, not the cell dimension: a membrane of a 3D model is a surface in 3D
		sb.append(",\"dimension\":").append(d.gdim() >= 3 ? 3 : 2).append(",\"bodyFitted\":true");
		sb.append(",\"timeIndex\":").append(row);
		sb.append(",\"numPoints\":").append(grid.numPoints());
		sb.append(",\"points\":");
		FieldViewerServer.appendDoubles(sb, grid.points, grid.points.length);
		sb.append(",\"cellType\":").append(grid.cellTypes.length > 0 ? grid.cellTypes[0] : d.cellType());
		if (!uniform) {
			sb.append(",\"cellTypes\":[");
			for (int c = 0; c < grid.cellTypes.length; c++) {
				sb.append(c > 0 ? "," : "").append(grid.cellTypes[c]);
			}
			sb.append(']');
		}
		sb.append(",\"cells\":[");
		for (int c = 0; c < grid.cells.length; c++) {
			sb.append(c > 0 ? ",[" : "[");
			for (int v = 0; v < grid.cells[c].length; v++) {
				sb.append(v > 0 ? "," : "").append(grid.cells[c][v]);
			}
			sb.append(']');
		}
		sb.append("],\"domain\":\"").append(FieldViewerServer.jsonEscape(domain)).append("\"}");
		return sb.toString();
	}

	/** {@code /field}: one variable at one time, one value per mesh point */
	static String field(BundleSource source, Map<String, String> q) throws Exception {
		FenicsBundle bundle = open(source);
		String varName = requireVar(q);
		String domain = q.get("domain");
		if (domain == null || domain.isEmpty()) {
			domain = domainOfVariable(source, bundle, varName);
		}
		int row = rowFor(bundle, q);
		double[] values = isStored(bundle, domain, varName) ? bundle.field(domain, varName, row)
				: functionField(source, bundle, domain, varName, row);
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
		StringBuilder sb = new StringBuilder(24 * values.length + 256);
		sb.append("{\"geometryId\":\"").append(FieldViewerServer.jsonEscape(geometryId(source, bundle, domain, row))).append('"');
		sb.append(",\"name\":\"").append(FieldViewerServer.jsonEscape(varName)).append('"');
		sb.append(",\"domain\":\"").append(FieldViewerServer.jsonEscape(domain)).append('"');
		sb.append(",\"time\":").append(bundle.getTimes().get(row));
		sb.append(",\"location\":\"point\",\"values\":");
		FieldViewerServer.appendDoubles(sb, values, values.length);
		sb.append(",\"range\":[").append(min).append(',').append(max).append("]}");
		return sb.toString();
	}

	private static String domainOfVariable(BundleSource source, FenicsBundle bundle, String varName) {
		for (FenicsBundle.Variable v : bundle.getVariables()) {
			if (v.name().equals(varName)) {
				return v.domain();
			}
		}
		if (source.functions.has(varName)) {
			return source.functions.domainOf(bundle, varName);
		}
		throw new IllegalArgumentException("unknown variable '" + varName + "'");
	}

	/**
	 * {@code /timeseries?...&x=&y=[&z=]}: the variable's time course at a lab-frame point, interpolated
	 * from the P1 values of the cell containing it (in each segment's own mesh). Times where the point
	 * is outside the domain are {@code null}.
	 * <p>
	 * {@code &points=x,y,z;…} instead asks for several points in one pass (the multi-point response,
	 * {@link PointSeries#json}); {@code &snap=nearest} moves a point that misses a membrane domain's mesh
	 * onto it.
	 */
	static String timeSeries(BundleSource source, Map<String, String> q) throws Exception {
		FenicsBundle bundle = open(source);
		String varName = requireVar(q);
		String domain = q.get("domain");
		if (domain == null || domain.isEmpty()) {
			domain = domainOfVariable(source, bundle, varName);
		}
		double[] times = times(bundle);
		final String dom = domain;
		PointSeries.Rows rows = new SampledRows(source, bundle, dom, varName, RowPrefetcher.allRows(times.length));
		if (q.get("points") != null) {
			FenicsBundle.Domain d = bundle.domain(domain);
			// a 2D point may leave out z: it takes the mesh plane's
			Double planeZ = d.gdim() < 3 ? source.grid(bundle, domain, 0).points[2] : null;
			double[][] points = PointSeries.parsePoints(q.get("points"), planeZ);
			boolean snap = "nearest".equals(q.get("snap")) && d.isMembrane();
			PointSeries.Result r = PointSeries.sample(times.length, points, rows, PointSeries.Location.POINT, snap);
			return PointSeries.json(varName, domain, times, PointSeries.Location.POINT, PointSeries.series(points, r));
		}
		if (q.get("x") == null || q.get("y") == null) {
			throw new IllegalArgumentException("a FEniCSx time series is addressed by lab-frame point: 'x' and 'y' are required");
		}
		double x = Double.parseDouble(q.get("x"));
		double y = Double.parseDouble(q.get("y"));
		double z = q.get("z") != null ? Double.parseDouble(q.get("z")) : 0;
		PointSeries.Result r = PointSeries.sample(times.length, new double[][] { { x, y, z } }, rows,
				PointSeries.Location.POINT, false);
		double[] values = r.values[0];
		StringBuilder sb = new StringBuilder(32 * times.length + 256);
		sb.append("{\"name\":\"").append(FieldViewerServer.jsonEscape(varName)).append('"');
		sb.append(",\"domain\":\"").append(FieldViewerServer.jsonEscape(domain)).append('"');
		sb.append(",\"x\":").append(x).append(",\"y\":").append(y).append(",\"z\":").append(z);
		sb.append(",\"insideCount\":").append(r.insideCount[0]);
		sb.append(",\"times\":");
		FieldViewerServer.appendDoubles(sb, times, times.length);
		sb.append(",\"values\":");
		FieldViewerServer.appendDoubles(sb, values, values.length);
		sb.append('}');
		return sb.toString();
	}

	/**
	 * {@code /kymograph?…&var=&path=x,y,z;…[&samples=N][&tstep=k]}: the variable along a polyline at every
	 * saved time (every {@code tstep}-th), P1-interpolated at evenly spaced samples located in each row's mesh
	 * ({@link BodyFittedKymograph}). On an ALE run the line is fixed in the lab frame and the moving mesh passes
	 * through it. The default sample count follows the mesh's mean cell diameter, in the first row's mesh.
	 * On a 2D membrane (a line mesh) the curve runs along the membrane between the snapped path vertices
	 * ({@link MembraneArc}), sampled at the mesh vertices; {@code samples} is ignored there.
	 * A heavy job for the caller to run ({@link FieldViewerServer#heavy}).
	 */
	static String kymograph(BundleSource source, Map<String, String> q, int tstep) throws Exception {
		FenicsBundle bundle = open(source);
		String varName = requireVar(q);
		String domain = q.get("domain");
		if (domain == null || domain.isEmpty()) {
			domain = domainOfVariable(source, bundle, varName);
		}
		if (!isStored(bundle, domain, varName)) {
			if (!source.functions.has(varName)) {
				throw new IllegalArgumentException("unknown variable '" + varName + "' in domain '" + domain + "'");
			}
			source.functions.compile(bundle, domain, varName); // a 400 saying why it cannot be drawn here
		}
		FenicsBundle.Domain d = bundle.domain(domain);
		final String dom = domain;
		int[] strided = RowPrefetcher.stridedRows(bundle.getTimes().size(), tstep);
		RowPrefetcher coords = coordsPrefetcher(bundle, dom, strided);
		if (coords != null) {
			coords.before(0);
		}
		VtuGridParser.VtuGrid first = source.grid(bundle, domain, 0);
		if (d.isMembrane()) {
			// a straight line almost never lies on a membrane: the curve runs along it, between the snapped picks
			if (d.gdim() >= 3) {
				throw new IllegalArgumentException("membrane curves on a 3D membrane surface are not supported yet ('"
						+ domain + "'); a 2D membrane's curves are");
			}
			if (!bundle.isFixed()) {
				throw new IllegalArgumentException("membrane curves on a moving or remeshed membrane are not supported yet");
			}
			double[][] waypoints = BodyFittedKymograph.parsePath(q.get("path"), first.points[2]);
			MembraneArc arc = MembraneArc.build(first, waypoints, BodyFittedKymograph.MAX_SAMPLES);
			SampledRows arcRows = new SampledRows(source, bundle, dom, varName, strided);
			java.util.TreeSet<Integer> arcVertices = new java.util.TreeSet<>();
			for (int i = 0; i < arc.size(); i++) {
				arcVertices.add(arc.a[i]);
				arcVertices.add(arc.b[i]);
			}
			arcRows.sample(arcVertices, 0, first.numPoints());
			return BodyFittedKymograph.membraneJson(varName, domain, arc, times(bundle), tstep, arcRows::values);
		}
		double[][] path = BodyFittedKymograph.parsePath(q.get("path"), d.gdim() < 3 ? first.points[2] : null);
		int n = BodyFittedKymograph.sampleCount(q.get("samples"), FvLineSampler.length(path), first.meanCellDiameter());
		boolean moving = false;
		for (FenicsBundle.Segment segment : bundle.getSegments()) {
			moving |= "ale".equals(segment.motion());
		}
		PointSeries.Rows rows = new SampledRows(source, bundle, dom, varName, strided);
		return BodyFittedKymograph.json(varName, domain, PointSeries.Location.POINT, path, n, times(bundle), tstep, rows, moving);
	}

	/** the most molecule positions {@code /particles} sends per species unless {@code max} says otherwise */
	static final int DEFAULT_MAX_PARTICLES = 20000;

	/**
	 * {@code /particles[?time=][&max=]}: every particle species' molecule positions at one time (lab frame,
	 * flat x,y,z), at most {@code max} per species (default {@value #DEFAULT_MAX_PARTICLES}), evenly strided
	 * through the molecules when there are more. {@code count} is the species' molecules at that time,
	 * {@code shown} how many positions were sent.
	 */
	static String particles(BundleSource source, Map<String, String> q) throws Exception {
		FenicsBundle bundle = open(source);
		if (bundle.getParticleSpecies().isEmpty()) {
			throw new IllegalArgumentException("the run " + source.simId + " has no particles");
		}
		int row = rowFor(bundle, q);
		Map<String, double[]> species = new java.util.LinkedHashMap<>();
		for (String name : bundle.getParticleSpecies().keySet()) {
			species.put(name, bundle.particles(name, row));
		}
		return particlesJson(bundle.getTimes().get(row), row, species, maxParticles(q));
	}

	/** {@code max} from a {@code /particles} query, default {@value #DEFAULT_MAX_PARTICLES} */
	static int maxParticles(Map<String, String> q) {
		int max = DEFAULT_MAX_PARTICLES;
		if (q.get("max") != null && !q.get("max").isEmpty()) {
			max = Integer.parseInt(q.get("max"));
			if (max < 1) {
				throw new IllegalArgumentException("'max' must be at least 1");
			}
		}
		return max;
	}

	/**
	 * The {@code /particles} response: per species (flat x,y,z positions) its molecule count and at most
	 * {@code max} positions, evenly strided. Shared by results bundles and finite-volume particle files.
	 */
	static String particlesJson(double time, int row, Map<String, double[]> species, int max) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("{\"time\":").append(time).append(",\"timeIndex\":").append(row);
		sb.append(",\"species\":[");
		boolean first = true;
		for (Map.Entry<String, double[]> entry : species.entrySet()) {
			String name = entry.getKey();
			double[] xyz = entry.getValue();
			int count = xyz.length / 3;
			int stride = Math.max(1, (count + max - 1) / max);
			int shown = (count + stride - 1) / stride;
			double[] sent = new double[3 * shown];
			for (int i = 0, k = 0; i < count; i += stride, k++) {
				System.arraycopy(xyz, 3 * i, sent, 3 * k, 3);
			}
			sb.append(first ? "{" : ",{");
			first = false;
			sb.append("\"name\":\"").append(FieldViewerServer.jsonEscape(name)).append('"');
			sb.append(",\"count\":").append(count).append(",\"shown\":").append(shown).append(",\"points\":");
			FieldViewerServer.appendDoubles(sb, sent, sent.length);
			sb.append('}');
		}
		sb.append("]}");
		return sb.toString();
	}

	/**
	 * {@code /stats[?var=a,b]}: per time, min, max and mean of each variable over its own domain, as the
	 * solver computed them (mean = the finite-element integral over the domain's measure), plus the
	 * integral itself ({@code total}) and the domain measure (summed from its mesh).
	 */
	static String stats(BundleSource source, Map<String, String> q) throws Exception {
		FenicsBundle bundle = open(source);
		Set<String> requested = null;
		String varParam = q.get("var");
		if (varParam != null && !varParam.isEmpty()) {
			requested = new LinkedHashSet<>(Arrays.asList(varParam.split(",")));
		}
		List<FenicsBundle.Variable> chosen = new ArrayList<>();
		for (FenicsBundle.Variable v : bundle.getVariables()) {
			if (requested == null || requested.contains(v.name())) {
				chosen.add(v);
			}
		}
		if (chosen.isEmpty()) {
			if (requested != null && requested.stream().anyMatch(source.functions::has)) {
				throw new IllegalArgumentException("statistics are the solver's own integrals of its stored variables;"
						+ " functions have none (" + varParam + ")");
			}
			throw new IllegalArgumentException("no variables match " + varParam);
		}
		List<String> columns = bundle.getStatsColumns();
		int iMean = columns.indexOf("mean"), iTotal = columns.indexOf("total"), iMin = columns.indexOf("min"), iMax = columns.indexOf("max");
		if (iMean < 0 || iTotal < 0 || iMin < 0 || iMax < 0) {
			throw new IOException(source.store.describe() + ": statistics columns " + columns + " lack mean/total/min/max");
		}
		double[] times = times(bundle);
		int[] all = RowPrefetcher.allRows(times.length);
		for (FenicsBundle.Variable var : chosen) {
			bundle.prefetchStats(var.domain(), var.name(), all); // four numbers a row: all of them in one go
		}
		StringBuilder sb = new StringBuilder(96 * times.length * chosen.size() + 512);
		sb.append("{\"times\":");
		FieldViewerServer.appendDoubles(sb, times, times.length);
		sb.append(",\"weighting\":\"integral\",\"series\":[");
		for (int v = 0; v < chosen.size(); v++) {
			FenicsBundle.Variable var = chosen.get(v);
			double[][] cols = new double[5][times.length]; // min, max, mean, total, measure
			for (int i = 0; i < times.length; i++) {
				double[] row = bundle.stats(var.domain(), var.name(), i);
				cols[0][i] = row[iMin];
				cols[1][i] = row[iMax];
				cols[2][i] = row[iMean];
				cols[3][i] = row[iTotal];
				cols[4][i] = source.measure(bundle, var.domain(), i);
			}
			sb.append(v > 0 ? "," : "");
			sb.append("{\"name\":\"").append(FieldViewerServer.jsonEscape(var.name())).append('"');
			sb.append(",\"domain\":\"").append(FieldViewerServer.jsonEscape(var.domain())).append('"');
			String[] names = { "min", "max", "mean", "total", "measure" };
			for (int k = 0; k < names.length; k++) {
				sb.append(",\"").append(names[k]).append("\":");
				FieldViewerServer.appendDoubles(sb, cols[k], times.length);
			}
			sb.append('}');
		}
		sb.append("]}");
		return sb.toString();
	}
}
