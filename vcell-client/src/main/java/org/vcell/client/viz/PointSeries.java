package org.vcell.client.viz;

import java.util.List;
import org.vcell.vis.vtk.VtuGridParser;

/**
 * Time courses at lab-frame points: parsing the {@code points=} list of a {@code /timeseries} request,
 * the per-point loop shared by the body-fitted data sources (FEniCSx bundles, Chombo, MovingBoundary),
 * and the multi-point JSON response every mode answers with.
 * <p>
 * The loop makes ONE pass over the saved times whatever the number of points: a mesh is searched only
 * when it differs from the previous row's (a static mesh once, a moving mesh per row), and a row's values
 * are read once for all the points it holds. More points therefore cost nothing extra over the remote
 * seam, where reading a row is the expensive step.
 */
final class PointSeries {

	/** Points per request. The viewer caps its probes at 12 (for distinct colours); this is the server's limit. */
	static final int MAX_POINTS = 64;

	private PointSeries() {
	}

	/**
	 * Parses {@code x1,y1,z1;x2,y2,z2;…}. A point may leave out {@code z} only in 2D, where it takes the
	 * mesh plane's {@code z} (not 0: a 2D mesh need not lie at z = 0).
	 *
	 * @param planeZ the mesh plane's z for a 2D domain, or null for a 3D one
	 * @throws IllegalArgumentException (a 400) for a malformed list, a non-finite coordinate, a missing z in
	 *             3D, or more than {@link #MAX_POINTS} points
	 */
	static double[][] parsePoints(String param, Double planeZ) {
		return parsePoints(param, planeZ, "points", MAX_POINTS, "points");
	}

	/**
	 * {@link #parsePoints(String, Double)} for any list of lab-frame points, such as a kymograph's
	 * {@code path}.
	 *
	 * @param name the query parameter, for messages
	 * @param max the most entries allowed
	 * @param noun what an entry is called in the "at most" message, e.g. "points" or "path vertices"
	 */
	static double[][] parsePoints(String param, Double planeZ, String name, int max, String noun) {
		if (param == null || param.isBlank()) {
			throw new IllegalArgumentException("'" + name + "' is empty; expected x,y,z;x,y,z;…");
		}
		String[] entries = param.split(";");
		List<double[]> points = new java.util.ArrayList<>();
		for (String entry : entries) {
			if (entry.isBlank()) {
				continue; // tolerate a trailing ';'
			}
			String[] parts = entry.split(",");
			if (parts.length < 2 || parts.length > 3) {
				throw new IllegalArgumentException("malformed point '" + entry + "' in '" + name + "'; expected x,y,z or, in 2D, x,y");
			}
			double[] p = new double[3];
			for (int k = 0; k < parts.length; k++) {
				try {
					p[k] = Double.parseDouble(parts[k].trim());
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("malformed coordinate '" + parts[k] + "' in '" + name + "'");
				}
				if (!Double.isFinite(p[k])) {
					throw new IllegalArgumentException("non-finite coordinate '" + parts[k] + "' in '" + name + "'");
				}
			}
			if (parts.length == 2) {
				if (planeZ == null) {
					throw new IllegalArgumentException("point '" + entry + "' has no z, but the domain is 3D");
				}
				p[2] = planeZ;
			}
			points.add(p);
			if (points.size() > max) {
				throw new IllegalArgumentException("at most " + max + " " + noun);
			}
		}
		if (points.isEmpty()) {
			throw new IllegalArgumentException("'" + name + "' is empty; expected x,y,z;x,y,z;…");
		}
		return points.toArray(new double[0][]);
	}

	/** How a located cell turns a row of values into a value at a point. */
	enum Location {
		/** one value per cell, constant in it (finite volume, Chombo, MovingBoundary) */
		CELL("cell"),
		/** one value per mesh vertex, interpolated linearly in the cell (FEniCSx P1) */
		POINT("point");

		final String json;

		Location(String json) {
			this.json = json;
		}
	}

	/** The rows of a body-fitted run, as the loop reads them. */
	interface Rows {
		/** The mesh of {@code row}. Rows sharing a mesh must return the same instance: identity is what says "not moved". */
		VtuGridParser.VtuGrid grid(int row) throws Exception;

		/** The variable's values at {@code row}, one per cell ({@link Location#CELL}) or per mesh vertex ({@link Location#POINT}). */
		double[] values(int row) throws Exception;

		/**
		 * Told, once the points are located on a mesh (at the first row, and at every row whose mesh differs),
		 * which cell each point fell in ({@code -1}: outside), before that row's values are asked for. A source
		 * that can read just those cells' values -- a remote bundle sampled on the data server -- may fetch them
		 * here; the loop then reads only those entries of {@link #values}.
		 */
		default void located(VtuGridParser.VtuGrid grid, int row, int[] cells) throws Exception {
		}

		/**
		 * Told, before the loop, which points it will locate and whether it snaps them: a source that can locate
		 * them itself -- a moving mesh located on the data server, next to its point positions -- may do so for
		 * all its rows here or on the first {@link #grid} call.
		 */
		default void willLocate(double[][] points, boolean snap) throws Exception {
		}

		/**
		 * Where the points lie in {@link #grid}{@code (row)}, if the source located them itself, by the loop's own
		 * steps ({@link VtuGridParser#locate}, then {@link VtuGridParser#nearestOnMesh} when snapping) on the row's
		 * whole mesh; null to have the loop locate them in that grid. When answered, the grid need only hold the
		 * located cells' vertex positions: the weights and a function's x, y, z read nothing else.
		 */
		default Located locatedBySource(int row) throws Exception {
			return null;
		}
	}

	/**
	 * Points located by a {@link Rows} source: the cell of each ({@code -1} outside), and where a snap moved
	 * each ({@code snapped[p]}, null where it did not).
	 */
	record Located(int[] cells, double[][] snapped) {
	}

	/** The loop's output, one entry per requested point. */
	static final class Result {
		/** {@code values[p][row]}; NaN (serialized as null) where the point is outside the domain at that row */
		final double[][] values;
		/** rows at which each point lay in the domain */
		final int[] insideCount;
		/** the located cell of each point (-1 outside), meaningful only when {@link #singleMesh} */
		final int[] cell;
		/** true when every row had the same mesh, so a point's cell is one number */
		boolean singleMesh = true;
		/** where {@code snap} moved each point, in the first mesh that point snapped to; null where it did not move */
		final double[][] snapped;

		Result(int nPoints, int nRows) {
			values = new double[nPoints][nRows];
			insideCount = new int[nPoints];
			cell = new int[nPoints];
			java.util.Arrays.fill(cell, -1);
			snapped = new double[nPoints][];
		}
	}

	/**
	 * The time course at each point over {@code nRows} rows.
	 *
	 * @param snap move a point that misses the mesh to the nearest point on it, within one diameter of the
	 *            nearest cell — for a membrane (a line or surface mesh), which a click almost never lands on
	 *            exactly
	 */
	static Result sample(int nRows, double[][] points, Rows rows, Location location, boolean snap) throws Exception {
		int n = points.length;
		Result result = new Result(n, nRows);
		int[] cells = new int[n];
		double[][] weights = new double[n][];
		VtuGridParser.VtuGrid located = null;
		int meshes = 0;
		rows.willLocate(points, snap);
		for (int row = 0; row < nRows; row++) {
			VtuGridParser.VtuGrid grid = rows.grid(row);
			if (grid != located) {
				located = grid;
				meshes++;
				Located bySource = rows.locatedBySource(row);
				for (int p = 0; p < n; p++) {
					double x = points[p][0], y = points[p][1], z = points[p][2];
					int c;
					if (bySource != null) {
						c = bySource.cells()[p];
						double[] moved = bySource.snapped()[p];
						if (moved != null) {
							x = moved[0];
							y = moved[1];
							z = moved[2];
							if (result.snapped[p] == null) {
								result.snapped[p] = new double[] { x, y, z };
							}
						}
					} else {
						c = VtuGridParser.locate(grid, x, y, z);
					}
					if (c < 0 && snap && bySource == null) {
						double[] near = VtuGridParser.nearestOnMesh(grid, x, y, z);
						if (near != null) {
							c = (int) near[3];
							x = near[0];
							y = near[1];
							z = near[2];
							if (result.snapped[p] == null) {
								result.snapped[p] = new double[] { x, y, z };
							}
						}
					}
					cells[p] = c;
					weights[p] = c >= 0 && location == Location.POINT ? VtuGridParser.vertexWeights(grid, c, x, y, z) : null;
					if (meshes == 1) {
						result.cell[p] = c;
					}
				}
				rows.located(grid, row, cells.clone());
			}
			double[] values = null;
			for (int p = 0; p < n; p++) {
				int c = cells[p];
				if (c < 0) {
					result.values[p][row] = Double.NaN; // serializes as null: the point is outside the domain
					continue;
				}
				if (values == null) {
					values = rows.values(row); // read once per row, and only when some point needs it
				}
				double v;
				if (location == Location.CELL) {
					v = c < values.length ? values[c] : Double.NaN;
				} else {
					v = 0;
					int[] vertices = grid.cells[c];
					for (int k = 0; k < vertices.length; k++) {
						v += weights[p][k] * values[vertices[k]];
					}
				}
				result.values[p][row] = v;
				result.insideCount[p]++;
			}
		}
		result.singleMesh = meshes <= 1;
		return result;
	}

	/**
	 * One series of a multi-point response. {@code cell}, {@code volumeIndex} and {@code membraneIndex} are left
	 * out when null.
	 */
	record Series(double[] point, Integer cell, Integer volumeIndex, Integer membraneIndex, boolean inDomain,
			double[] snapped, double[] values) {
	}

	/** The body-fitted series of a {@link Result}: {@code cell} only for a single mesh, {@code inDomain} when inside at any row. */
	static Series[] series(double[][] points, Result r) {
		Series[] out = new Series[points.length];
		for (int p = 0; p < points.length; p++) {
			out[p] = new Series(points[p], r.singleMesh ? Integer.valueOf(r.cell[p]) : null, null, null,
					r.insideCount[p] > 0, r.snapped[p], r.values[p]);
		}
		return out;
	}

	/**
	 * The multi-point response:
	 * {@code {"name","domain","times","location","series":[{"point","cell","volumeIndex","inDomain","values"}…]}}.
	 */
	static String json(String name, String domain, double[] times, Location location, Series[] series) {
		StringBuilder sb = new StringBuilder(256 + series.length * (24 * times.length + 128));
		sb.append("{\"name\":\"").append(FieldViewerServer.jsonEscape(name)).append('"');
		sb.append(",\"domain\":\"").append(FieldViewerServer.jsonEscape(domain)).append('"');
		sb.append(",\"times\":");
		FieldViewerServer.appendDoubles(sb, times, times.length);
		sb.append(",\"location\":\"").append(location.json).append('"');
		sb.append(",\"series\":[");
		for (int s = 0; s < series.length; s++) {
			Series se = series[s];
			sb.append(s > 0 ? ",{" : "{").append("\"point\":");
			FieldViewerServer.appendDoubles(sb, se.point(), 3);
			if (se.snapped() != null) {
				sb.append(",\"snapped\":");
				FieldViewerServer.appendDoubles(sb, se.snapped(), 3);
			}
			if (se.cell() != null) {
				sb.append(",\"cell\":").append(se.cell().intValue());
			}
			if (se.volumeIndex() != null) {
				sb.append(",\"volumeIndex\":").append(se.volumeIndex().intValue());
			}
			if (se.membraneIndex() != null) {
				sb.append(",\"membraneIndex\":").append(se.membraneIndex().intValue());
			}
			sb.append(",\"inDomain\":").append(se.inDomain());
			sb.append(",\"values\":");
			FieldViewerServer.appendDoubles(sb, se.values(), se.values().length);
			sb.append('}');
		}
		sb.append("]}");
		return sb.toString();
	}
}
