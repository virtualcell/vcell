package org.vcell.client.viz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.vcell.vis.vtk.VtuGridParser;

/**
 * The kymograph of a body-fitted run (a FEniCSx bundle, Chombo, MovingBoundary): a variable's values at
 * evenly spaced lab-frame points along a polyline, at every saved time (every {@code tstep}-th), rows = times.
 * <p>
 * The samples are located in each row's own mesh by {@link PointSeries#sample}, the loop the multi-point
 * {@code /timeseries} uses: a static mesh is searched once, a moving one per row, and each row's values are read
 * once for all the samples. FEniCSx values are P1, interpolated at each sample ({@code location: "point"});
 * Chombo and MovingBoundary values are per cell ({@code "cell"}). A sample outside the domain at a row is a gap.
 * <p>
 * On a moving mesh (MovingBoundary, a FEniCSx ALE run) the line stays where it was drawn, in the lab frame
 * (an Eulerian line), and a sample's value at each time is that of whichever cell holds its point then: the
 * moving boundary shows as the edge of the gaps. A line that follows the material can't be built from the saved
 * data.
 */
final class BodyFittedKymograph {

	/** Samples per body-fitted kymograph (the plan's limit). */
	static final int MAX_SAMPLES = 2000;
	/** The default sample count's bounds: {@code clamp(ceil(2·L / h̄), 16, 1000)}. */
	static final int MIN_DEFAULT_SAMPLES = 16;
	static final int MAX_DEFAULT_SAMPLES = 1000;

	private BodyFittedKymograph() {
	}

	/**
	 * Two samples per mean cell diameter along the line, {@code clamp(ceil(2·L / h̄), 16, 1000)}: enough to
	 * resolve every cell the line crosses, on average, without sampling one cell many times over.
	 */
	static int defaultSampleCount(double length, double meanDiameter) {
		double n = meanDiameter > 0 ? Math.ceil(2 * length / meanDiameter) : MAX_DEFAULT_SAMPLES;
		return (int) Math.max(MIN_DEFAULT_SAMPLES, Math.min(MAX_DEFAULT_SAMPLES, n));
	}

	/**
	 * The {@code samples=} parameter, or the default for the line and mesh.
	 *
	 * @throws IllegalArgumentException (a 400) for a malformed count, fewer than 2 or more than {@link #MAX_SAMPLES}
	 */
	static int sampleCount(String param, double length, double meanDiameter) {
		if (param == null || param.isBlank()) {
			return defaultSampleCount(length, meanDiameter);
		}
		int n;
		try {
			n = Integer.parseInt(param.trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("malformed 'samples' '" + param + "'");
		}
		if (n < 2) {
			throw new IllegalArgumentException("'samples' must be at least 2");
		}
		if (n > MAX_SAMPLES) {
			throw new IllegalArgumentException("at most " + MAX_SAMPLES + " samples");
		}
		return n;
	}

	/**
	 * Parses a kymograph's {@code path}: 2 to {@link FvLineSampler#MAX_PATH_VERTICES} vertices. In 2D every
	 * vertex lies in the mesh's plane (z may be left out, and is set to the plane's either way, so arc lengths
	 * are in-plane). Consecutive repeats are dropped; fewer than two distinct vertices is a 400. Unlike a
	 * finite-volume path, a vertex may lie outside the mesh: its samples are gaps.
	 *
	 * @param planeZ the mesh plane's z for a 2D domain, or null for a 3D one
	 */
	static double[][] parsePath(String param, Double planeZ) {
		double[][] parsed = PointSeries.parsePoints(param, planeZ, "path", FvLineSampler.MAX_PATH_VERTICES, "path vertices");
		List<double[]> path = new ArrayList<>();
		for (double[] v : parsed) {
			if (planeZ != null) {
				v[2] = planeZ;
			}
			if (path.isEmpty() || !Arrays.equals(path.get(path.size() - 1), v)) {
				path.add(v);
			}
		}
		if (path.size() < 2) {
			throw new IllegalArgumentException("the path has zero length: it needs two distinct vertices");
		}
		return path.toArray(new double[0][]);
	}

	/** Evenly spaced arc lengths from 0 to the path's length, the first and last at its ends. */
	static double[] arcLengths(double length, int n) {
		double[] s = new double[n];
		for (int i = 0; i < n; i++) {
			s[i] = i == n - 1 ? length : length * i / (n - 1);
		}
		return s;
	}

	/**
	 * The kymograph response over {@code rows} (indexed by saved time).
	 *
	 * @param nSamples from {@link #sampleCount}
	 * @param movingMesh the mesh moves between saved times, so the line is a fixed lab-frame line through it
	 */
	static String json(String name, String domain, PointSeries.Location location, double[][] path, int nSamples,
			double[] allTimes, int tstep, PointSeries.Rows rows, boolean movingMesh) throws Exception {
		FieldViewerServer.checkValueLimit(nSamples, allTimes.length, tstep);
		double length = FvLineSampler.length(path);
		double[] arc = arcLengths(length, nSamples);
		double[][] points = new double[nSamples][];
		for (int i = 0; i < nSamples; i++) {
			points[i] = FvLineSampler.pointAt(path, arc[i]);
		}
		// the returned rows: saved times 0, k, 2k, … as a TimeSeriesJobSpec steps (the finite-volume rule)
		int nt = FieldViewerServer.strideCount(allTimes.length, tstep);
		int[] timeIndices = new int[nt];
		double[] times = new double[nt];
		for (int r = 0; r < nt; r++) {
			timeIndices[r] = r * tstep;
			times[r] = allTimes[r * tstep];
		}
		PointSeries.Result result = PointSeries.sample(nt, points, new PointSeries.Rows() {
			@Override
			public VtuGridParser.VtuGrid grid(int row) throws Exception {
				return rows.grid(timeIndices[row]);
			}

			@Override
			public double[] values(int row) throws Exception {
				return rows.values(timeIndices[row]);
			}

			@Override
			public void located(VtuGridParser.VtuGrid grid, int row, int[] cells) throws Exception {
				rows.located(grid, timeIndices[row], cells);
			}

			@Override
			public void willLocate(double[][] located, boolean snap) throws Exception {
				rows.willLocate(located, snap);
			}

			@Override
			public PointSeries.Located locatedBySource(int row) throws Exception {
				return rows.locatedBySource(timeIndices[row]);
			}
		}, location, false);

		boolean[] inDomain = new boolean[nSamples];
		for (int i = 0; i < nSamples; i++) {
			inDomain[i] = result.insideCount[i] > 0;
		}
		return write(name, domain, location, "uniform", movingMesh, path, length, times, timeIndices, arc, points,
				inDomain, null, result.values);
	}

	/**
	 * Writes a body-fitted kymograph response.
	 *
	 * @param sampling {@code "uniform"} (evenly spaced along a line) or {@code "membrane"} (along a membrane)
	 * @param vertex each sample's mesh vertex (-1 for none), or null to leave the field out
	 * @param values {@code values[sample][row]}, NaN for a gap
	 */
	static String write(String name, String domain, PointSeries.Location location, String sampling, boolean movingMesh,
			double[][] path, double length, double[] times, int[] timeIndices, double[] arc, double[][] points,
			boolean[] inDomain, int[] vertex, double[][] values) {
		int nSamples = arc.length;
		int nt = times.length;
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < nSamples; i++) {
			for (double v : values[i]) {
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

		StringBuilder sb = new StringBuilder(512 + 64 * nSamples + 20 * nSamples * nt);
		sb.append("{\"name\":\"").append(FieldViewerServer.jsonEscape(name)).append('"');
		sb.append(",\"domain\":\"").append(FieldViewerServer.jsonEscape(domain)).append('"');
		sb.append(",\"location\":\"").append(location.json).append('"');
		sb.append(",\"sampling\":\"").append(sampling).append('"');
		sb.append(",\"movingMesh\":").append(movingMesh);
		sb.append(",\"path\":[");
		for (int v = 0; v < path.length; v++) {
			sb.append(v > 0 ? "," : "");
			FieldViewerServer.appendDoubles(sb, path[v], 3);
		}
		sb.append("],\"pathLength\":").append(length);
		sb.append(",\"times\":");
		FieldViewerServer.appendDoubles(sb, times, nt);
		sb.append(",\"timeIndices\":").append(Arrays.toString(timeIndices).replace(" ", ""));
		sb.append(",\"samples\":{\"arcLength\":");
		FieldViewerServer.appendDoubles(sb, arc, nSamples);
		sb.append(",\"points\":[");
		for (int i = 0; i < nSamples; i++) {
			for (int a = 0; a < 3; a++) {
				sb.append(i > 0 || a > 0 ? "," : "").append(points[i][a]);
			}
		}
		sb.append("],\"inDomain\":").append(Arrays.toString(inDomain).replace(" ", ""));
		if (vertex != null) {
			sb.append(",\"vertex\":").append(Arrays.toString(vertex).replace(" ", ""));
		}
		sb.append("},\"values\":[");
		double[] row = new double[nSamples];
		for (int r = 0; r < nt; r++) {
			for (int i = 0; i < nSamples; i++) {
				row[i] = values[i][r];
			}
			sb.append(r > 0 ? "," : "");
			FieldViewerServer.appendDoubles(sb, row, nSamples);
		}
		sb.append("],\"range\":[").append(min).append(',').append(max).append("]}");
		return sb.toString();
	}

	/**
	 * The kymograph along a 2D membrane ({@link MembraneArc}) over a fixed mesh: {@code sampling: "membrane"},
	 * one sample per mesh vertex the arc passes plus its two snapped ends, each row's P1 values read once.
	 *
	 * @param readRow the P1 values of a saved time (by its index in {@code allTimes})
	 */
	static String membraneJson(String name, String domain, MembraneArc arc, double[] allTimes, int tstep,
			RowReader readRow) throws Exception {
		int n = arc.size();
		FieldViewerServer.checkValueLimit(n, allTimes.length, tstep);
		int nt = FieldViewerServer.strideCount(allTimes.length, tstep);
		int[] timeIndices = new int[nt];
		double[] times = new double[nt];
		double[][] values = new double[n][nt];
		for (int r = 0; r < nt; r++) {
			timeIndices[r] = r * tstep;
			times[r] = allTimes[r * tstep];
			double[] row = arc.values(readRow.read(timeIndices[r]));
			for (int i = 0; i < n; i++) {
				values[i][r] = row[i];
			}
		}
		boolean[] inDomain = new boolean[n];
		Arrays.fill(inDomain, true);
		return write(name, domain, PointSeries.Location.POINT, "membrane", false, arc.waypoints, arc.length(), times,
				timeIndices, arc.arcLength, arc.points, inDomain, arc.vertex, values);
	}

	/** One saved time's values. */
	interface RowReader {
		double[] read(int timeIndex) throws Exception;
	}
}
