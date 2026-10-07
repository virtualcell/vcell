package org.vcell.solver.fenics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.vcell.vis.vtk.VtuGridParser;

/**
 * Reads a FEniCSx results bundle, {@code <SimID_..._>.fenics/} (vcell-fenics ADR 010, schema 1): a zarr
 * v2 group whose {@code .zattrs} holds the manifest under {@code vcell_fenics}, a VTU mesh per domain,
 * and per variable a {@code (T, N)} array of P1 point values plus a {@code (T, 4)} statistics array.
 * <p>
 * Follows the ADR's reader rules: a newer {@code schema} is refused and unknown keys are ignored; the
 * number of rows comes from the manifest's {@code times} (per segment, {@code count}), never from an
 * array's shape, and a row whose chunk has not been written reads as NaN — so a bundle can be read
 * while the solver is still writing it ({@link #refresh()} re-reads the manifest). Only the array form
 * the writer produces is supported: little-endian float64, one row per chunk, zlib or no compression.
 * <p>
 * <b>Particles</b> (optional; a hybrid PDE/particle run, e.g. one recorded by viva-pde-particle). A second
 * root key, {@code particles}, sits next to the manifest:
 * {@code {"schema": 1, "species": {name: {"xyz": path, "count": path}}}}. Per species, {@code xyz} is a
 * {@code (T, cap, 3)} array of lab-frame molecule positions (NaN past the row's count) and {@code count} a
 * {@code (T, 1)} array of the molecules in each row, both stored like the field arrays (float64, one row
 * per chunk). Rows are global output rows. A bundle without the key has no particles; a newer extension
 * schema is ignored, so the fields stay viewable.
 */
public final class FenicsBundle {

	public static final int SUPPORTED_SCHEMA = 1;
	static final String MANIFEST_KEY = "vcell_fenics";

	public record Domain(String name, String kind, int dim, int gdim, String mesh, int numPoints, int numCells, int cellType) {
		public boolean isMembrane() {
			return "membrane".equals(kind);
		}
	}

	public record Variable(String name, String domain, String assoc, String element, String path, String stats) {
	}

	public record Segment(int index, double t0, int count, String motion, String prefix) {
	}

	/** a particle species' arrays (see the class comment): positions {@code (T, cap, 3)}, counts {@code (T, 1)} */
	public record ParticleSpecies(String name, String xyz, String count) {
	}

	public static final int SUPPORTED_PARTICLES_SCHEMA = 1;
	static final String PARTICLES_KEY = "particles";

	private final BundleStore store;
	private final String root; // for messages
	private final int schema;
	private final String profile;
	private final String status;
	private final String message;
	private final double progress;
	private final List<Double> times;
	private final Map<String, Domain> domains;
	private final List<Variable> variables;
	private final List<Segment> segments;
	private final List<String> statsColumns;
	private final Map<String, ParticleSpecies> particleSpecies;

	private FenicsBundle(BundleStore store, JsonObject manifest, JsonObject particles) {
		this.store = store;
		this.root = store.describe();
		this.schema = manifest.get("schema").getAsInt();
		if (schema > SUPPORTED_SCHEMA) {
			throw new IllegalArgumentException(root + ": results bundle schema " + schema
					+ " is newer than this VCell reads (" + SUPPORTED_SCHEMA + "); update VCell");
		}
		this.profile = string(manifest, "profile", "fixed");
		this.status = string(manifest, "status", "unknown");
		this.message = string(manifest, "message", null);
		this.progress = manifest.has("progress") && !manifest.get("progress").isJsonNull() ? manifest.get("progress").getAsDouble() : Double.NaN;

		List<Double> t = new ArrayList<>();
		for (JsonElement e : array(manifest, "times")) {
			t.add(e.getAsDouble());
		}
		this.times = Collections.unmodifiableList(t);

		Map<String, Domain> d = new LinkedHashMap<>();
		JsonObject domainsJson = manifest.getAsJsonObject("domains");
		for (Map.Entry<String, JsonElement> e : domainsJson.entrySet()) {
			JsonObject o = e.getValue().getAsJsonObject();
			d.put(e.getKey(), new Domain(e.getKey(), string(o, "kind", "volume"), o.get("dim").getAsInt(), o.get("gdim").getAsInt(),
					o.get("mesh").getAsString(), o.get("n_points").getAsInt(), o.get("n_cells").getAsInt(), o.get("cell_type").getAsInt()));
		}
		this.domains = Collections.unmodifiableMap(d);

		List<Variable> v = new ArrayList<>();
		for (JsonElement e : array(manifest, "variables")) {
			JsonObject o = e.getAsJsonObject();
			v.add(new Variable(o.get("name").getAsString(), o.get("domain").getAsString(), string(o, "assoc", "point"),
					string(o, "element", "P1"), o.get("path").getAsString(), o.get("stats").getAsString()));
		}
		this.variables = Collections.unmodifiableList(v);

		List<Segment> s = new ArrayList<>();
		for (JsonElement e : array(manifest, "segments")) {
			JsonObject o = e.getAsJsonObject();
			s.add(new Segment(o.get("index").getAsInt(), o.get("t0").getAsDouble(), o.get("count").getAsInt(),
					string(o, "motion", "none"), string(o, "prefix", "")));
		}
		if (s.isEmpty()) {
			s.add(new Segment(0, t.isEmpty() ? 0 : t.get(0), t.size(), "none", ""));
		}
		this.segments = Collections.unmodifiableList(s);

		List<String> c = new ArrayList<>();
		for (JsonElement e : array(manifest, "stats_columns")) {
			c.add(e.getAsString());
		}
		this.statsColumns = Collections.unmodifiableList(c);

		Map<String, ParticleSpecies> p = new LinkedHashMap<>();
		if (particles != null && particles.has("schema") && particles.get("schema").getAsInt() <= SUPPORTED_PARTICLES_SCHEMA
				&& particles.has("species") && particles.get("species").isJsonObject()) {
			for (Map.Entry<String, JsonElement> e : particles.getAsJsonObject("species").entrySet()) {
				JsonObject o = e.getValue().getAsJsonObject();
				p.put(e.getKey(), new ParticleSpecies(e.getKey(), o.get("xyz").getAsString(), o.get("count").getAsString()));
			}
		}
		this.particleSpecies = Collections.unmodifiableMap(p);
	}

	/** @return whether {@code dir} looks like a results bundle (a directory with a manifest) */
	public static boolean isBundle(File dir) {
		return dir.isDirectory() && new File(dir, ".zattrs").isFile();
	}

	public static FenicsBundle open(File root) throws IOException {
		return open(BundleStore.directory(root));
	}

	public static FenicsBundle open(BundleStore store) throws IOException {
		byte[] attrs = store.read(".zattrs");
		if (attrs == null) {
			throw new FileNotFoundException(store.describe() + " is not a FEniCSx results bundle (no .zattrs)");
		}
		JsonObject json = JsonParser.parseString(new String(attrs, StandardCharsets.UTF_8)).getAsJsonObject();
		if (!json.has(MANIFEST_KEY)) {
			throw new IOException(store.describe() + ": .zattrs has no '" + MANIFEST_KEY + "' manifest");
		}
		JsonElement particles = json.get(PARTICLES_KEY);
		return new FenicsBundle(store, json.getAsJsonObject(MANIFEST_KEY),
				particles != null && particles.isJsonObject() ? particles.getAsJsonObject() : null);
	}

	/** re-reads the manifest: a running solver appends to {@code times} as rows land */
	public FenicsBundle refresh() throws IOException {
		return open(store);
	}

	public BundleStore getStore() { return store; }
	public int getSchema() { return schema; }
	/** {@code fixed} (one mesh) or {@code segmented} (the mesh changes between segments) */
	public String getProfile() { return profile; }
	public boolean isFixed() { return "fixed".equals(profile); }
	/** {@code running}, {@code completed} or {@code failed} */
	public String getStatus() { return status; }
	public String getMessage() { return message; }
	public double getProgress() { return progress; }
	/** the output times written so far; authoritative for the number of rows */
	public List<Double> getTimes() { return times; }
	public Map<String, Domain> getDomains() { return domains; }
	public List<Variable> getVariables() { return variables; }
	public List<Segment> getSegments() { return segments; }
	public List<String> getStatsColumns() { return statsColumns; }
	/** the particle species recorded in the bundle, in file order; empty for a run without particles */
	public Map<String, ParticleSpecies> getParticleSpecies() { return particleSpecies; }

	public Domain domain(String name) {
		Domain d = domains.get(name);
		if (d == null) {
			throw new IllegalArgumentException("no domain '" + name + "' in " + root + " (has " + domains.keySet() + ")");
		}
		return d;
	}

	public Variable variable(String domain, String name) {
		for (Variable v : variables) {
			if (v.domain().equals(domain) && v.name().equals(name)) {
				return v;
			}
		}
		throw new IllegalArgumentException("no variable '" + name + "' on domain '" + domain + "' in " + root);
	}

	/** the segment holding output row {@code row}, and the row's index within it */
	public record SegmentRow(Segment segment, int localRow) {
	}

	public SegmentRow segmentOf(int row) {
		checkRow(row);
		int start = 0;
		for (Segment s : segments) {
			if (row < start + s.count()) {
				return new SegmentRow(s, row - start);
			}
			start += s.count();
		}
		// the manifest lists the time before the segment's count catches up; it belongs to the last one
		Segment last = segments.get(segments.size() - 1);
		return new SegmentRow(last, row - (start - last.count()));
	}

	/** the bytes of {@code domain}'s VTU mesh for the segment holding {@code row} */
	public byte[] meshBytes(String domain, int row) throws IOException {
		String prefix = times.isEmpty() ? segments.get(0).prefix() : segmentOf(row).segment().prefix();
		String path = prefix + domain(domain).mesh();
		byte[] bytes = store.read(path);
		if (bytes == null) {
			throw new FileNotFoundException(root + ": no mesh " + path);
		}
		return bytes;
	}

	/** the P1 values of a variable at output row {@code row}, in the VTU's point order */
	public double[] field(String domain, String variable, int row) throws IOException {
		SegmentRow sr = segmentOf(row);
		return readRow(sr.segment().prefix() + variable(domain, variable).path(), sr.localRow());
	}

	/**
	 * The domain's point coordinates at output row {@code row} as x,y,z triples (the mesh's point order):
	 * a moving ({@code motion: ale}) segment records them per row in {@code <prefix><domain>/_coords};
	 * otherwise null, meaning the segment mesh's own points.
	 */
	public double[] coords(String domain, int row) throws IOException {
		SegmentRow sr = segmentOf(row);
		if (!"ale".equals(sr.segment().motion())) {
			return null;
		}
		domain(domain); // rejects an unknown name
		return readRow(sr.segment().prefix() + domain + "/_coords", sr.localRow());
	}

	/**
	 * The positions of {@code species}' molecules at output row {@code row}, as x,y,z triples (lab frame);
	 * empty when the row has none, or its chunks have not been written yet.
	 */
	public double[] particles(String species, int row) throws IOException {
		checkRow(row);
		ParticleSpecies p = particleSpecies.get(species);
		if (p == null) {
			throw new IllegalArgumentException("unknown particle species '" + species + "' in " + root);
		}
		double count = readRow(p.count(), row)[0];
		if (!(count > 0)) {
			return new double[0];
		}
		double[] xyz = readRow(p.xyz(), row);
		return Arrays.copyOf(xyz, 3 * (int) Math.min(Math.round(count), xyz.length / 3));
	}

	/**
	 * Tells the store which chunks {@link #field} is about to read for {@code rows}, so a remote store
	 * fetches them in a few batched calls instead of one call per row ({@link BundleStore#prefetch}).
	 * Rows not written yet are skipped.
	 */
	public void prefetchField(String domain, String variable, int[] rows) throws IOException {
		String path = variable(domain, variable).path();
		prefetchRows(rows, row -> path);
	}

	/** {@link #prefetchField} for {@link #stats} */
	public void prefetchStats(String domain, String variable, int[] rows) throws IOException {
		String path = variable(domain, variable).stats();
		prefetchRows(rows, row -> path);
	}

	/** {@link #prefetchField} for {@link #coords}: the recorded point positions of moving rows */
	public void prefetchCoords(String domain, int[] rows) throws IOException {
		domain(domain);
		java.util.List<Integer> moving = new ArrayList<>();
		for (int row : rows) {
			if (row >= 0 && row < times.size() && "ale".equals(segmentOf(row).segment().motion())) {
				moving.add(row);
			}
		}
		prefetchRows(moving.stream().mapToInt(Integer::intValue).toArray(), row -> domain + "/_coords");
	}

	private interface ArrayOfRow {
		String path(int row);
	}

	private void prefetchRows(int[] rows, ArrayOfRow arrayOfRow) throws IOException {
		List<String> chunks = new ArrayList<>(rows.length);
		Map<String, JsonObject> metas = new LinkedHashMap<>();
		for (int row : rows) {
			if (row < 0 || row >= times.size()) {
				continue;
			}
			SegmentRow sr = segmentOf(row);
			String arrayPath = sr.segment().prefix() + arrayOfRow.path(row);
			JsonObject meta = metas.get(arrayPath);
			if (meta == null) {
				meta = arrayMeta(arrayPath);
				metas.put(arrayPath, meta);
			}
			int[] shape = ints(meta.getAsJsonArray("shape"));
			if (sr.localRow() < shape[0]) {
				chunks.add(chunkPath(arrayPath, meta, sr.localRow()));
			}
		}
		store.prefetch(chunks);
	}

	private JsonObject arrayMeta(String arrayPath) throws IOException {
		return arrayMeta(store, root, arrayPath);
	}

	private static JsonObject arrayMeta(BundleStore store, String root, String arrayPath) throws IOException {
		byte[] zarray = store.read(arrayPath + "/.zarray");
		if (zarray == null) {
			throw new FileNotFoundException(root + "/" + arrayPath + ": no .zarray");
		}
		return JsonParser.parseString(new String(zarray, StandardCharsets.UTF_8)).getAsJsonObject();
	}

	/** the chunk file of a one-row-per-chunk array holding {@code row} */
	private static String chunkPath(String arrayPath, JsonObject meta, int row) {
		int dims = meta.getAsJsonArray("shape").size();
		String separator = string(meta, "dimension_separator", ".");
		return arrayPath + "/" + row + (separator + "0").repeat(dims - 1);
	}

	/** whether any output row's mesh moves (an ALE segment) */
	public boolean isMoving() {
		return segments.stream().anyMatch(s -> "ale".equals(s.motion()));
	}

	/** the statistics at output row {@code row}, one value per {@link #getStatsColumns()} entry */
	public double[] stats(String domain, String variable, int row) throws IOException {
		SegmentRow sr = segmentOf(row);
		return readRow(sr.segment().prefix() + variable(domain, variable).stats(), sr.localRow());
	}

	private void checkRow(int row) {
		if (row < 0 || row >= times.size()) {
			throw new IndexOutOfBoundsException("row " + row + " not written (" + root + " has " + times.size() + " rows)");
		}
	}

	/** one row of a 2D zarr v2 array stored one row per chunk; a missing chunk is all fill value */
	private double[] readRow(String arrayPath, int row) throws IOException {
		JsonObject meta = arrayMeta(arrayPath);
		int[] shape = checkLayout(meta, root + "/" + arrayPath);
		if (row >= shape[0]) {
			throw new IndexOutOfBoundsException(root + "/" + arrayPath + ": row " + row + " beyond shape " + Arrays.toString(shape));
		}
		double[] values = new double[rowLength(shape)];
		decodeRow(store, arrayPath, meta, row, values);
		return values;
	}

	/**
	 * The array's shape, after checking it is the only layout the writer produces (little-endian float64,
	 * one row per chunk, C order, no filters).
	 */
	private static int[] checkLayout(JsonObject meta, String arrayDir) throws IOException {
		int[] shape = ints(meta.getAsJsonArray("shape"));
		int[] chunks = ints(meta.getAsJsonArray("chunks"));
		String dtype = meta.get("dtype").getAsString();
		boolean wholeRows = shape.length >= 2 && chunks.length == shape.length && chunks[0] == 1;
		for (int axis = 1; wholeRows && axis < shape.length; axis++) {
			wholeRows = chunks[axis] == shape[axis];
		}
		if (!wholeRows || !"<f8".equals(dtype) || !"C".equals(string(meta, "order", "C"))
				|| (meta.has("filters") && !meta.get("filters").isJsonNull())) {
			throw new IOException(arrayDir + ": unsupported zarr array layout (expected <f8, one row per chunk, no filters)");
		}
		return shape;
	}

	/** the values in one row (a row of a (T, N, 3) array is N·3, C order) */
	private static int rowLength(int[] shape) {
		int n = 1;
		for (int axis = 1; axis < shape.length; axis++) {
			n *= shape[axis];
		}
		return n;
	}

	/**
	 * Decodes row {@code row} into {@code values} (its length is the row's); a missing chunk is all fill value.
	 *
	 * @return whether the row's chunk is written
	 */
	private static boolean decodeRow(BundleStore store, String arrayPath, JsonObject meta, int row, double[] values) throws IOException {
		String chunk = chunkPath(arrayPath, meta, row);
		byte[] raw = store.read(chunk);
		if (raw == null) {
			Arrays.fill(values, fillValue(meta));
			return false;
		}
		int n = values.length;
		byte[] decoded = decompress(meta, raw, n * Double.BYTES, chunk);
		ByteBuffer buf = ByteBuffer.wrap(decoded).order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < n; i++) {
			values[i] = buf.getDouble();
		}
		return true;
	}

	/** the most values one {@link #gather} answers, over all its arrays and rows (8 MB of doubles) */
	public static final int MAX_SAMPLE_VALUES = 1 << 20;
	/** the most arrays one {@link #gather} reads */
	public static final int MAX_SAMPLE_ARRAYS = 16;
	/** the most indices one {@link #gather} reads per row */
	public static final int MAX_SAMPLE_INDICES = 1 << 16;
	/** the most rows one {@link #gather} may be asked for */
	public static final int MAX_SAMPLE_ROWS = 1000;

	/**
	 * Some values of some arrays of the bundle in {@code store}: for each array of {@code arrayPaths} (bundle
	 * relative, all with the same row length -- one domain of one segment), at each of {@code rows} (rows of
	 * those arrays), the values at {@code indices} (strictly increasing, within the row). The data server's
	 * side of {@link cbit.vcell.server.DataSetController#getFenicsBundleSamples}: the chunks are read and
	 * decoded here, next to the files, and only the asked-for values leave.
	 * <p>
	 * Answers the longest prefix of {@code rows} whose values fit in {@code maxValues} (arrays × rows × indices),
	 * so one reply stays bounded and the caller asks again for the rest; refuses a request of which not even
	 * one row fits, and more than {@link #MAX_SAMPLE_ARRAYS} arrays, {@link #MAX_SAMPLE_INDICES} indices or
	 * {@link #MAX_SAMPLE_ROWS} rows.
	 */
	public static FenicsSamples gather(BundleStore store, String[] arrayPaths, int[] indices, int[] rows, long maxValues)
			throws IOException {
		String root = store.describe();
		if (arrayPaths == null || arrayPaths.length == 0 || indices == null || rows == null) {
			throw new IllegalArgumentException("a sample request needs arrays, indices and rows");
		}
		if (arrayPaths.length > MAX_SAMPLE_ARRAYS || indices.length > MAX_SAMPLE_INDICES || rows.length > MAX_SAMPLE_ROWS) {
			throw new IllegalArgumentException("at most " + MAX_SAMPLE_ARRAYS + " arrays, " + MAX_SAMPLE_INDICES + " indices and "
					+ MAX_SAMPLE_ROWS + " rows per sample request; got " + arrayPaths.length + ", " + indices.length + " and " + rows.length);
		}
		for (int i = 1; i < indices.length; i++) {
			if (indices[i] <= indices[i - 1]) {
				throw new IllegalArgumentException("sample indices must be strictly increasing");
			}
		}
		JsonObject[] metas = new JsonObject[arrayPaths.length];
		int[][] shapes = new int[arrayPaths.length][];
		int n = -1;
		for (int a = 0; a < arrayPaths.length; a++) {
			BundleStore.checkRelativePath(arrayPaths[a]);
			metas[a] = arrayMeta(store, root, arrayPaths[a]);
			shapes[a] = checkLayout(metas[a], root + "/" + arrayPaths[a]);
			int length = rowLength(shapes[a]);
			if (n >= 0 && length != n) {
				throw new IllegalArgumentException("sampled arrays must share one row length (one domain of one segment): "
						+ arrayPaths[0] + " has " + n + ", " + arrayPaths[a] + " has " + length);
			}
			n = length;
		}
		if (indices.length > 0 && (indices[0] < 0 || indices[indices.length - 1] >= n)) {
			throw new IllegalArgumentException("sample index outside a row of " + n + " values");
		}
		for (int row : rows) {
			for (int a = 0; a < arrayPaths.length; a++) {
				if (row < 0 || row >= shapes[a][0]) {
					throw new IndexOutOfBoundsException(root + "/" + arrayPaths[a] + ": row " + row + " beyond shape " + Arrays.toString(shapes[a]));
				}
			}
		}
		long perRow = (long) arrayPaths.length * Math.max(1, indices.length);
		int fit = (int) Math.min(rows.length, maxValues / perRow);
		if (fit < 1 && rows.length > 0) {
			throw new IllegalArgumentException("one row of " + arrayPaths.length + " arrays at " + indices.length
					+ " indices is more than " + maxValues + " values; ask for fewer arrays or indices");
		}
		double[][][] values = new double[arrayPaths.length][fit][indices.length];
		boolean[][] written = new boolean[arrayPaths.length][fit];
		double[] row = new double[n];
		for (int a = 0; a < arrayPaths.length; a++) {
			for (int r = 0; r < fit; r++) {
				written[a][r] = decodeRow(store, arrayPaths[a], metas[a], rows[r], row);
				double[] out = values[a][r];
				for (int i = 0; i < indices.length; i++) {
					out[i] = row[indices[i]];
				}
			}
		}
		return new FenicsSamples(Arrays.copyOf(rows, fit), values, written);
	}

	/**
	 * The values of {@code variables} (all on {@code domain}) at {@code vertices} (strictly increasing) for
	 * {@code rows} (output rows, all in ONE segment, whose mesh the vertices index): {@code [variable][row][vertex]}.
	 * From the store's sampled read ({@link BundleStore#sampleRows}) when it has one; null when it has not, and
	 * the caller reads whole rows instead.
	 */
	public double[][][] sampleFields(String domain, String[] variables, int[] rows, int[] vertices) throws IOException {
		if (rows.length == 0) {
			return new double[variables.length][0][];
		}
		Segment segment = segmentOf(rows[0]).segment();
		int[] localRows = new int[rows.length];
		for (int r = 0; r < rows.length; r++) {
			SegmentRow sr = segmentOf(rows[r]);
			if (sr.segment().index() != segment.index()) {
				throw new IllegalArgumentException("sampled rows must lie in one segment: rows " + rows[0] + " and " + rows[r] + " do not");
			}
			localRows[r] = sr.localRow();
		}
		String[] paths = new String[variables.length];
		for (int v = 0; v < variables.length; v++) {
			paths[v] = segment.prefix() + variable(domain, variables[v]).path();
		}
		FenicsSamples samples = store.sampleRows(paths, localRows, vertices);
		if (samples == null) {
			return null;
		}
		if (samples.rows.length != rows.length) {
			throw new IOException(root + ": sampled read answered " + samples.rows.length + " of " + rows.length + " rows");
		}
		return samples.values;
	}

	/** the most lab-frame points one {@link #locate} places (a kymograph has at most 2,000 samples, a probe 64 points) */
	public static final int MAX_LOCATE_POINTS = 4096;
	/**
	 * the most work one {@link #locate} does, counted per row as the values it decodes (x,y,z and each array at every
	 * mesh point) plus the cells its locator sorts: about 0.9 s of one data-server thread at the ~55 ns a unit
	 * measured on a real moving mesh. It bounds a call on a fine mesh, where the reply's values alone would allow
	 * many rows; the caller asks again for the rest.
	 */
	public static final long MAX_LOCATE_WORK = 1L << 24;

	/**
	 * Lab-frame {@code points} (x,y,z each) located in a moving mesh at each of {@code rows}, with the located cells'
	 * vertices and their positions and the values of {@code arrayPaths} there: the data server's side of
	 * {@link cbit.vcell.server.DataSetController#getFenicsBundleLocatedSamples}. {@code meshPath} is the segment's
	 * VTU mesh (its topology), {@code coordsPath} its {@code (T, N, 3)} point positions, the arrays {@code (T, N)}
	 * values on it; rows are rows of those arrays.
	 * <p>
	 * Each row's mesh is the segment's cells on that row's positions, and each point is found in it by
	 * {@link VtuGridParser#locate} -- the lowest-numbered containing cell -- and, when {@code snap} and it misses,
	 * moved onto the mesh by {@link VtuGridParser#nearestOnMesh}: the desktop's own steps, on the same numbers.
	 * <p>
	 * Answers the longest prefix of {@code rows} whose reply fits in {@code maxValues} values and whose work (values
	 * decoded plus cells sorted, per row) fits in {@code maxWork}, the first row always; refuses a request of which not even one row fits, and more than
	 * {@link #MAX_SAMPLE_ARRAYS} arrays, {@link #MAX_LOCATE_POINTS} points or {@link #MAX_SAMPLE_ROWS} rows.
	 */
	public static FenicsLocatedSamples locate(BundleStore store, String meshPath, String coordsPath, String[] arrayPaths,
			double[] points, boolean snap, int[] rows, long maxValues, long maxWork) throws IOException {
		String root = store.describe();
		if (meshPath == null || coordsPath == null || arrayPaths == null || points == null || rows == null) {
			throw new IllegalArgumentException("a locate request needs a mesh, point positions, arrays, points and rows");
		}
		if (points.length % 3 != 0 || points.length == 0) {
			throw new IllegalArgumentException("points are x,y,z triples; got " + points.length + " numbers");
		}
		int nPoints = points.length / 3;
		if (arrayPaths.length > MAX_SAMPLE_ARRAYS || nPoints > MAX_LOCATE_POINTS || rows.length > MAX_SAMPLE_ROWS) {
			throw new IllegalArgumentException("at most " + MAX_SAMPLE_ARRAYS + " arrays, " + MAX_LOCATE_POINTS + " points and "
					+ MAX_SAMPLE_ROWS + " rows per locate request; got " + arrayPaths.length + ", " + nPoints + " and " + rows.length);
		}
		VtuGridParser.VtuGrid mesh;
		byte[] meshBytes = store.read(BundleStore.checkRelativePath(meshPath));
		if (meshBytes == null) {
			throw new FileNotFoundException(root + ": no mesh " + meshPath);
		}
		try {
			mesh = VtuGridParser.parse(meshBytes);
		} catch (Exception e) {
			throw new IOException(root + "/" + meshPath + ": " + e.getMessage(), e);
		}
		int n = mesh.numPoints();
		BundleStore.checkRelativePath(coordsPath);
		JsonObject coordsMeta = arrayMeta(store, root, coordsPath);
		int[] coordsShape = checkLayout(coordsMeta, root + "/" + coordsPath);
		if (rowLength(coordsShape) != 3 * n) {
			throw new IllegalArgumentException(coordsPath + " holds " + rowLength(coordsShape) + " numbers a row, not x,y,z of the mesh's "
					+ n + " points");
		}
		JsonObject[] metas = new JsonObject[arrayPaths.length];
		int[][] shapes = new int[arrayPaths.length][];
		for (int a = 0; a < arrayPaths.length; a++) {
			BundleStore.checkRelativePath(arrayPaths[a]);
			metas[a] = arrayMeta(store, root, arrayPaths[a]);
			shapes[a] = checkLayout(metas[a], root + "/" + arrayPaths[a]);
			if (rowLength(shapes[a]) != n) {
				throw new IllegalArgumentException(arrayPaths[a] + " holds " + rowLength(shapes[a]) + " values a row, not one per mesh point ("
						+ n + ")");
			}
		}
		for (int row : rows) {
			if (row < 0 || row >= coordsShape[0]) {
				throw new IndexOutOfBoundsException(root + "/" + coordsPath + ": row " + row + " beyond shape " + Arrays.toString(coordsShape));
			}
			for (int a = 0; a < arrayPaths.length; a++) {
				if (row >= shapes[a][0]) {
					throw new IndexOutOfBoundsException(root + "/" + arrayPaths[a] + ": row " + row + " beyond shape " + Arrays.toString(shapes[a]));
				}
			}
		}
		long workPerRow = (long) (3 + arrayPaths.length) * n + mesh.cells.length;
		List<int[]> cells = new ArrayList<>();
		List<int[][]> cellVertices = new ArrayList<>();
		List<int[]> cellTypes = new ArrayList<>();
		List<double[]> snapped = new ArrayList<>();
		List<int[]> vertices = new ArrayList<>();
		List<double[]> coords = new ArrayList<>();
		List<Boolean> coordsWritten = new ArrayList<>();
		List<double[][]> values = new ArrayList<>();
		List<boolean[]> written = new ArrayList<>();
		long sent = 0;
		long work = 0;
		double[] value = new double[n];
		for (int r = 0; r < rows.length; r++) {
			if (r > 0 && work + workPerRow > maxWork) {
				break;
			}
			double[] xyz = new double[3 * n];
			boolean coordsRow = decodeRow(store, coordsPath, coordsMeta, rows[r], xyz);
			VtuGridParser.VtuGrid grid = new VtuGridParser.VtuGrid(xyz, mesh.cells, mesh.cellTypes, mesh.cellFaces);
			int[] rowCells = new int[nPoints];
			int[][] rowCellVertices = new int[nPoints][];
			int[] rowCellTypes = new int[nPoints];
			long rowCellInts = 0;
			double[] rowSnapped = null;
			java.util.TreeSet<Integer> needed = new java.util.TreeSet<>();
			for (int p = 0; p < nPoints; p++) {
				double x = points[3 * p], y = points[3 * p + 1], z = points[3 * p + 2];
				int c = VtuGridParser.locate(grid, x, y, z);
				if (c < 0 && snap) {
					double[] near = VtuGridParser.nearestOnMesh(grid, x, y, z);
					if (near != null) {
						c = (int) near[3];
						if (rowSnapped == null) {
							rowSnapped = new double[3 * nPoints];
							Arrays.fill(rowSnapped, Double.NaN);
						}
						System.arraycopy(near, 0, rowSnapped, 3 * p, 3);
					}
				}
				rowCells[p] = c;
				rowCellTypes[p] = c >= 0 ? mesh.cellTypes[c] : -1;
				if (c >= 0) {
					rowCellVertices[p] = mesh.cells[c]; // one array per cell: serialized once however many points share it
					rowCellInts += mesh.cells[c].length;
					for (int v : mesh.cells[c]) {
						needed.add(v);
					}
				}
			}
			int[] rowVertices = needed.stream().mapToInt(Integer::intValue).toArray();
			long rowValues = 2L * nPoints + rowCellInts + (rowSnapped != null ? 3L * nPoints : 0) + (long) (3 + arrayPaths.length) * rowVertices.length;
			if (sent + rowValues > maxValues) {
				if (r == 0) {
					throw new IllegalArgumentException("one row of " + nPoints + " located points and " + arrayPaths.length
							+ " arrays is more than " + maxValues + " values; ask for fewer points or arrays");
				}
				break;
			}
			double[] rowCoords = new double[3 * rowVertices.length];
			for (int i = 0; i < rowVertices.length; i++) {
				System.arraycopy(xyz, 3 * rowVertices[i], rowCoords, 3 * i, 3);
			}
			double[][] rowArrays = new double[arrayPaths.length][rowVertices.length];
			boolean[] rowWritten = new boolean[arrayPaths.length];
			for (int a = 0; a < arrayPaths.length; a++) {
				rowWritten[a] = decodeRow(store, arrayPaths[a], metas[a], rows[r], value);
				for (int i = 0; i < rowVertices.length; i++) {
					rowArrays[a][i] = value[rowVertices[i]];
				}
			}
			cells.add(rowCells);
			cellVertices.add(rowCellVertices);
			cellTypes.add(rowCellTypes);
			snapped.add(rowSnapped);
			vertices.add(rowVertices);
			coords.add(rowCoords);
			coordsWritten.add(coordsRow);
			values.add(rowArrays);
			written.add(rowWritten);
			sent += rowValues;
			work += workPerRow;
		}
		int fit = cells.size();
		double[][][] byArray = new double[arrayPaths.length][fit][];
		boolean[][] writtenByArray = new boolean[arrayPaths.length][fit];
		boolean[] coordsOk = new boolean[fit];
		for (int r = 0; r < fit; r++) {
			coordsOk[r] = coordsWritten.get(r);
			for (int a = 0; a < arrayPaths.length; a++) {
				byArray[a][r] = values.get(r)[a];
				writtenByArray[a][r] = written.get(r)[a];
			}
		}
		return new FenicsLocatedSamples(Arrays.copyOf(rows, fit), cells.toArray(new int[0][]), cellVertices.toArray(new int[0][][]),
				cellTypes.toArray(new int[0][]), snapped.toArray(new double[0][]),
				vertices.toArray(new int[0][]), coords.toArray(new double[0][]), coordsOk, byArray, writtenByArray);
	}

	/**
	 * {@code points} located in {@code domain}'s moving mesh at {@code rows} (output rows, all in ONE moving segment),
	 * with the located cells' vertices, their positions and the values of {@code variables} there
	 * ({@link #locate}), from the store's located read ({@link BundleStore#locateRows}); the answer's rows are those
	 * rows' indices within the segment. Null when the store has no such read: the caller reads whole rows.
	 */
	public FenicsLocatedSamples locateFields(String domain, String[] variables, int[] rows, double[] points, boolean snap)
			throws IOException {
		if (rows.length == 0) {
			throw new IllegalArgumentException("no rows to locate in");
		}
		Segment segment = segmentOf(rows[0]).segment();
		if (!"ale".equals(segment.motion())) {
			throw new IllegalArgumentException("located reads are for a moving segment; segment " + segment.index() + " does not move");
		}
		int[] localRows = new int[rows.length];
		for (int r = 0; r < rows.length; r++) {
			SegmentRow sr = segmentOf(rows[r]);
			if (sr.segment().index() != segment.index()) {
				throw new IllegalArgumentException("located rows must lie in one segment: rows " + rows[0] + " and " + rows[r] + " do not");
			}
			localRows[r] = sr.localRow();
		}
		String[] paths = new String[variables.length];
		for (int v = 0; v < variables.length; v++) {
			paths[v] = segment.prefix() + variable(domain, variables[v]).path();
		}
		FenicsLocatedSamples located = store.locateRows(segment.prefix() + domain(domain).mesh(), segment.prefix() + domain + "/_coords",
				paths, points, snap, localRows);
		if (located == null) {
			return null;
		}
		if (located.rows.length != rows.length) {
			throw new IOException(root + ": located read answered " + located.rows.length + " of " + rows.length + " rows");
		}
		return located;
	}

	private static byte[] decompress(JsonObject meta, byte[] raw, int expectedBytes, String chunk) throws IOException {
		JsonElement compressor = meta.get("compressor");
		if (compressor == null || compressor.isJsonNull()) {
			if (raw.length != expectedBytes) {
				throw new IOException(chunk + ": " + raw.length + " bytes, expected " + expectedBytes);
			}
			return raw;
		}
		String id = compressor.getAsJsonObject().get("id").getAsString();
		if (!"zlib".equals(id)) {
			throw new IOException(chunk + ": unsupported zarr compressor '" + id + "'");
		}
		Inflater inflater = new Inflater();
		try {
			inflater.setInput(raw);
			byte[] out = new byte[expectedBytes];
			int total = 0;
			while (total < expectedBytes && !inflater.finished()) {
				int count = inflater.inflate(out, total, expectedBytes - total);
				if (count == 0 && (inflater.needsInput() || inflater.needsDictionary())) {
					break;
				}
				total += count;
			}
			if (total != expectedBytes) {
				throw new IOException(chunk + ": decompressed to " + total + " bytes, expected " + expectedBytes);
			}
			return out;
		} catch (DataFormatException e) {
			throw new IOException(chunk + ": corrupt zlib data: " + e.getMessage(), e);
		} finally {
			inflater.end();
		}
	}

	private static double fillValue(JsonObject meta) {
		JsonElement fill = meta.get("fill_value");
		if (fill == null || fill.isJsonNull()) {
			return 0.0;
		}
		if (fill.getAsJsonPrimitive().isString()) {
			return switch (fill.getAsString()) {
				case "NaN" -> Double.NaN;
				case "Infinity" -> Double.POSITIVE_INFINITY;
				case "-Infinity" -> Double.NEGATIVE_INFINITY;
				default -> throw new IllegalArgumentException("unsupported fill_value " + fill);
			};
		}
		return fill.getAsDouble();
	}

	private static int[] ints(JsonArray a) {
		int[] out = new int[a.size()];
		for (int i = 0; i < out.length; i++) {
			out[i] = a.get(i).getAsInt();
		}
		return out;
	}

	private static JsonArray array(JsonObject o, String key) {
		JsonElement e = o.get(key);
		return e == null || e.isJsonNull() ? new JsonArray() : e.getAsJsonArray();
	}

	private static String string(JsonObject o, String key, String fallback) {
		JsonElement e = o.get(key);
		return e == null || e.isJsonNull() ? fallback : e.getAsString();
	}
}
