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
		private final Map<String, VtuGridParser.VtuGrid> grids = new ConcurrentHashMap<>();

		BundleSource(String simId, int jobIndex, BundleStore store, String simName) {
			this.simId = simId;
			this.jobIndex = jobIndex;
			this.store = store;
			this.simName = simName;
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

		VtuGridParser.VtuGrid grid(FenicsBundle bundle, String domain, int row) throws Exception {
			FenicsBundle.Segment segment = bundle.segmentOf(row).segment();
			String key = segment.index() + "/" + domain;
			VtuGridParser.VtuGrid grid = grids.get(key);
			if (grid == null) {
				grid = VtuGridParser.parse(bundle.meshBytes(domain, row));
				grids.put(key, grid);
			}
			return grid;
		}
	}

	private FenicsBundleViews() {
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

	private static String geometryId(BundleSource source, FenicsBundle bundle, String domain, int row) {
		String base = source.simId + "/" + domain;
		return bundle.isFixed() ? base : base + "@s" + bundle.segmentOf(row).segment().index();
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
		sb.append("]}");
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
			domain = domainOfVariable(bundle, varName);
		}
		int row = rowFor(bundle, q);
		double[] values = bundle.field(domain, varName, row);
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

	private static String domainOfVariable(FenicsBundle bundle, String varName) {
		for (FenicsBundle.Variable v : bundle.getVariables()) {
			if (v.name().equals(varName)) {
				return v.domain();
			}
		}
		throw new IllegalArgumentException("unknown variable '" + varName + "'");
	}

	/**
	 * {@code /timeseries?...&x=&y=[&z=]}: the variable's time course at a lab-frame point, interpolated
	 * from the P1 values of the cell containing it (in each segment's own mesh). Times where the point
	 * is outside the domain are {@code null}.
	 */
	static String timeSeries(BundleSource source, Map<String, String> q) throws Exception {
		FenicsBundle bundle = open(source);
		String varName = requireVar(q);
		String domain = q.get("domain");
		if (domain == null || domain.isEmpty()) {
			domain = domainOfVariable(bundle, varName);
		}
		if (q.get("x") == null || q.get("y") == null) {
			throw new IllegalArgumentException("a FEniCSx time series is addressed by lab-frame point: 'x' and 'y' are required");
		}
		double x = Double.parseDouble(q.get("x"));
		double y = Double.parseDouble(q.get("y"));
		double z = q.get("z") != null ? Double.parseDouble(q.get("z")) : 0;
		double[] times = times(bundle);
		double[] values = new double[times.length];
		int inside = 0;
		VtuGridParser.VtuGrid located = null;
		int cell = -1;
		double[] weights = null;
		for (int i = 0; i < times.length; i++) {
			VtuGridParser.VtuGrid grid = source.grid(bundle, domain, i);
			if (grid != located) {
				located = grid;
				cell = VtuGridParser.locateCell(grid, x, y, z);
				weights = cell < 0 ? null : VtuGridParser.vertexWeights(grid, cell, x, y, z);
			}
			if (cell < 0) {
				values[i] = Double.NaN;
				continue;
			}
			double[] row = bundle.field(domain, varName, i);
			double v = 0;
			int[] vertices = grid.cells[cell];
			for (int k = 0; k < vertices.length; k++) {
				v += weights[k] * row[vertices[k]];
			}
			values[i] = v;
			inside++;
		}
		StringBuilder sb = new StringBuilder(32 * times.length + 256);
		sb.append("{\"name\":\"").append(FieldViewerServer.jsonEscape(varName)).append('"');
		sb.append(",\"domain\":\"").append(FieldViewerServer.jsonEscape(domain)).append('"');
		sb.append(",\"x\":").append(x).append(",\"y\":").append(y).append(",\"z\":").append(z);
		sb.append(",\"insideCount\":").append(inside);
		sb.append(",\"times\":");
		FieldViewerServer.appendDoubles(sb, times, times.length);
		sb.append(",\"values\":");
		FieldViewerServer.appendDoubles(sb, values, values.length);
		sb.append('}');
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
			throw new IllegalArgumentException("no variables match " + varParam);
		}
		List<String> columns = bundle.getStatsColumns();
		int iMean = columns.indexOf("mean"), iTotal = columns.indexOf("total"), iMin = columns.indexOf("min"), iMax = columns.indexOf("max");
		if (iMean < 0 || iTotal < 0 || iMin < 0 || iMax < 0) {
			throw new IOException(source.store.describe() + ": statistics columns " + columns + " lack mean/total/min/max");
		}
		double[] times = times(bundle);
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
