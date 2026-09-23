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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

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

	private final File root;
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

	private FenicsBundle(File root, JsonObject manifest) {
		this.root = root;
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
	}

	/** @return whether {@code dir} looks like a results bundle (a directory with a manifest) */
	public static boolean isBundle(File dir) {
		return dir.isDirectory() && new File(dir, ".zattrs").isFile();
	}

	public static FenicsBundle open(File root) throws IOException {
		File attrs = new File(root, ".zattrs");
		if (!attrs.isFile()) {
			throw new FileNotFoundException(root + " is not a FEniCSx results bundle (no .zattrs)");
		}
		JsonObject json = JsonParser.parseString(Files.readString(attrs.toPath(), StandardCharsets.UTF_8)).getAsJsonObject();
		if (!json.has(MANIFEST_KEY)) {
			throw new IOException(root + ": .zattrs has no '" + MANIFEST_KEY + "' manifest");
		}
		return new FenicsBundle(root, json.getAsJsonObject(MANIFEST_KEY));
	}

	/** re-reads the manifest: a running solver appends to {@code times} as rows land */
	public FenicsBundle refresh() throws IOException {
		return open(root);
	}

	public File getRoot() { return root; }
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
		return Files.readAllBytes(new File(root, prefix + domain(domain).mesh()).toPath());
	}

	/** the P1 values of a variable at output row {@code row}, in the VTU's point order */
	public double[] field(String domain, String variable, int row) throws IOException {
		SegmentRow sr = segmentOf(row);
		return readRow(sr.segment().prefix() + variable(domain, variable).path(), sr.localRow());
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
		File arrayDir = new File(root, arrayPath);
		JsonObject meta = JsonParser.parseString(Files.readString(new File(arrayDir, ".zarray").toPath(), StandardCharsets.UTF_8)).getAsJsonObject();
		int[] shape = ints(meta.getAsJsonArray("shape"));
		int[] chunks = ints(meta.getAsJsonArray("chunks"));
		String dtype = meta.get("dtype").getAsString();
		if (shape.length != 2 || chunks[0] != 1 || chunks[1] != shape[1] || !"<f8".equals(dtype)
				|| !"C".equals(string(meta, "order", "C"))
				|| (meta.has("filters") && !meta.get("filters").isJsonNull())) {
			throw new IOException(arrayDir + ": unsupported zarr array layout (expected <f8, one row per chunk, no filters)");
		}
		if (row >= shape[0]) {
			throw new IndexOutOfBoundsException(arrayDir + ": row " + row + " beyond shape " + Arrays.toString(shape));
		}
		int n = shape[1];
		String separator = string(meta, "dimension_separator", ".");
		File chunk = new File(arrayDir, row + separator + "0");
		double[] values = new double[n];
		if (!chunk.isFile()) {
			Arrays.fill(values, fillValue(meta));
			return values;
		}
		byte[] raw = Files.readAllBytes(chunk.toPath());
		byte[] decoded = decompress(meta, raw, n * Double.BYTES, chunk);
		ByteBuffer buf = ByteBuffer.wrap(decoded).order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < n; i++) {
			values[i] = buf.getDouble();
		}
		return values;
	}

	private static byte[] decompress(JsonObject meta, byte[] raw, int expectedBytes, File chunk) throws IOException {
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
