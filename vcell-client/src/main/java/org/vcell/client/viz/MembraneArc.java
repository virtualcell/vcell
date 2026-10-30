package org.vcell.client.viz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import org.vcell.vis.vtk.VtuGridParser;

/**
 * A curve along a 2D membrane: a line mesh ({@code VTK_LINE} cells, a FEniCSx membrane domain of a 2D model),
 * between points picked on or beside it. A straight line almost never lies on a curve, so a membrane kymograph
 * is taken along the membrane itself:
 * <ul>
 * <li>each waypoint snaps to the nearest point on the line cells (within {@link #REACH} diameters of that
 * cell);</li>
 * <li>consecutive waypoints are joined by the <b>shortest path along the line cells</b> (Dijkstra over the
 * mesh's vertices, entering and leaving through the snapped points' own cells), so on a closed membrane two
 * picks take the shorter way round; a third pick in between chooses the other way;</li>
 * <li>the samples are the snapped ends and every mesh vertex the path passes, in order; a vertex's value is its
 * P1 value, a snapped end's is interpolated along its cell;</li>
 * <li>the arc length is accumulated along the path, so the arc's length is the sum of its edge lengths (the end
 * edges partly).</li>
 * </ul>
 * Built on one mesh: a membrane that moves or is remeshed is refused by the caller.
 */
final class MembraneArc {

	/** How far from the membrane a waypoint may lie: this many lengths of the nearest line cell. */
	static final double REACH = 4;

	/** The arc's samples, in order along it. */
	final double[][] waypoints; // the snapped waypoints: the arc's "path"
	final double[][] points;
	final double[] arcLength;
	/** each sample's value is {@code (1 − w)·v[a] + w·v[b]}; a mesh vertex has {@code a == b}, {@code w == 0} */
	final int[] a;
	final int[] b;
	final double[] w;
	/** the mesh vertex at each sample, or -1 for a snapped end inside a cell */
	final int[] vertex;

	private MembraneArc(double[][] waypoints, List<Sample> samples) {
		this.waypoints = waypoints;
		int n = samples.size();
		points = new double[n][];
		arcLength = new double[n];
		a = new int[n];
		b = new int[n];
		w = new double[n];
		vertex = new int[n];
		for (int i = 0; i < n; i++) {
			Sample s = samples.get(i);
			points[i] = s.point;
			a[i] = s.a;
			b[i] = s.b;
			w[i] = s.w;
			vertex[i] = s.w == 0 ? s.a : -1;
			arcLength[i] = i == 0 ? 0 : arcLength[i - 1] + distance(points[i - 1], points[i]);
		}
	}

	int size() {
		return points.length;
	}

	double length() {
		return arcLength[arcLength.length - 1];
	}

	/** The arc's values in one row of P1 (per-vertex) values. */
	double[] values(double[] vertexValues) {
		double[] v = new double[points.length];
		for (int i = 0; i < v.length; i++) {
			v[i] = w[i] == 0 ? vertexValues[a[i]] : (1 - w[i]) * vertexValues[a[i]] + w[i] * vertexValues[b[i]];
		}
		return v;
	}

	/** A point on the mesh: cell {@code (a, b)} at {@code w} from a; a vertex is {@code (v, v, 0)}. */
	private record Sample(int a, int b, double w, double[] point) {
		static Sample vertex(double[] p, int v) {
			return new Sample(v, v, 0, new double[] { p[3 * v], p[3 * v + 1], p[3 * v + 2] });
		}
	}

	/** Where a waypoint snapped: line cell {@code cell} = (u, v), at parameter {@code t} from u. */
	private record Snap(int cell, int u, int v, double t, double[] point) {
		Sample sample(double[] p) {
			if (t == 0) {
				return Sample.vertex(p, u);
			}
			if (t == 1) {
				return Sample.vertex(p, v);
			}
			return new Sample(u, v, t, point);
		}
	}

	/**
	 * The arc through {@code waypoints} along the line mesh {@code grid}.
	 *
	 * @throws IllegalArgumentException (a 400) when the mesh is not a line mesh, a waypoint is not near it,
	 *             two waypoints lie on separate pieces of it, the arc has zero length, or it has more than
	 *             {@code maxSamples} samples
	 */
	static MembraneArc build(VtuGridParser.VtuGrid grid, double[][] waypoints, int maxSamples) {
		for (int type : grid.cellTypes) {
			if (type != VtuGridParser.VTK_LINE) {
				throw new IllegalArgumentException("membrane curves are served on 2D membranes (line meshes) only;"
						+ " curves on a 3D membrane surface are not supported yet");
			}
		}
		double[] p = grid.points;
		int nv = grid.numPoints();
		// the vertex graph: each line cell is an edge
		List<List<int[]>> adjacent = new ArrayList<>(nv);
		for (int i = 0; i < nv; i++) {
			adjacent.add(new ArrayList<>(2));
		}
		for (int c = 0; c < grid.cells.length; c++) {
			int u = grid.cells[c][0], v = grid.cells[c][1];
			adjacent.get(u).add(new int[] { v, c });
			adjacent.get(v).add(new int[] { u, c });
		}
		Snap[] snaps = new Snap[waypoints.length];
		double[][] snapped = new double[waypoints.length][];
		for (int k = 0; k < waypoints.length; k++) {
			snaps[k] = snap(grid, waypoints[k], k);
			snapped[k] = snaps[k].point;
		}
		List<Sample> samples = new ArrayList<>();
		for (int k = 1; k < snaps.length; k++) {
			for (Sample s : leg(grid, adjacent, snaps[k - 1], snaps[k], k)) {
				if (samples.isEmpty() || !Arrays.equals(samples.get(samples.size() - 1).point, s.point)) {
					samples.add(s);
				}
				if (samples.size() > maxSamples) {
					throw new IllegalArgumentException("the arc passes more than " + maxSamples
							+ " mesh vertices; pick a shorter one");
				}
			}
		}
		if (samples.size() < 2) {
			throw new IllegalArgumentException("the arc has zero length: its picks snap to one point of the membrane");
		}
		return new MembraneArc(snapped, samples);
	}

	/** The nearest point on the line cells, within {@link #REACH} lengths of that cell. */
	private static Snap snap(VtuGridParser.VtuGrid grid, double[] q, int k) {
		double[] p = grid.points;
		double best = Double.POSITIVE_INFINITY;
		Snap nearest = null;
		double nearestLength2 = 0;
		for (int c = 0; c < grid.cells.length; c++) {
			int u = grid.cells[c][0], v = grid.cells[c][1];
			double[] d = new double[3];
			double[] qu = new double[3];
			double len2 = 0, dot = 0;
			for (int a = 0; a < 3; a++) {
				d[a] = p[3 * v + a] - p[3 * u + a];
				qu[a] = q[a] - p[3 * u + a];
				len2 += d[a] * d[a];
				dot += qu[a] * d[a];
			}
			double t = len2 == 0 ? 0 : Math.max(0, Math.min(1, dot / len2));
			double[] on = new double[3];
			double d2 = 0;
			for (int a = 0; a < 3; a++) {
				on[a] = t == 1 ? p[3 * v + a] : p[3 * u + a] + t * d[a];
				d2 += (q[a] - on[a]) * (q[a] - on[a]);
			}
			if (d2 < best) {
				best = d2;
				nearest = new Snap(c, u, v, t, on);
				nearestLength2 = len2;
			}
		}
		if (nearest == null || best > REACH * REACH * nearestLength2) {
			throw new IllegalArgumentException("vertex " + (k + 1) + " of the path (" + q[0] + ", " + q[1] + ", " + q[2]
					+ ") is not on the membrane: pick on or just beside it");
		}
		return nearest;
	}

	/** The shortest path along the line cells from {@code from} to {@code to}, both ends included. */
	private static List<Sample> leg(VtuGridParser.VtuGrid grid, List<List<int[]>> adjacent, Snap from, Snap to, int k) {
		double[] p = grid.points;
		List<Sample> out = new ArrayList<>();
		if (from.cell == to.cell) { // along one cell: the straight piece between them is the shortest way
			out.add(from.sample(p));
			out.add(to.sample(p));
			return out;
		}
		int nv = grid.numPoints();
		double[] dist = new double[nv];
		int[] prev = new int[nv];
		Arrays.fill(dist, Double.POSITIVE_INFINITY);
		Arrays.fill(prev, -1);
		PriorityQueue<double[]> queue = new PriorityQueue<>((x, y) -> x[0] != y[0] ? Double.compare(x[0], y[0])
				: Double.compare(x[1], y[1]));
		double fromLength = vertexDistance(p, from.u, from.v);
		dist[from.u] = from.t * fromLength;
		dist[from.v] = Math.min(dist[from.v], (1 - from.t) * fromLength);
		queue.add(new double[] { dist[from.u], from.u });
		queue.add(new double[] { dist[from.v], from.v });
		boolean[] done = new boolean[nv];
		while (!queue.isEmpty()) {
			double[] top = queue.poll();
			int u = (int) top[1];
			if (done[u]) {
				continue;
			}
			done[u] = true;
			for (int[] edge : adjacent.get(u)) {
				int v = edge[0];
				double d = dist[u] + vertexDistance(p, u, v);
				if (d < dist[v]) {
					dist[v] = d;
					prev[v] = u;
					queue.add(new double[] { d, v });
				}
			}
		}
		double toLength = vertexDistance(p, to.u, to.v);
		double viaU = dist[to.u] + to.t * toLength;
		double viaV = dist[to.v] + (1 - to.t) * toLength;
		if (!Double.isFinite(Math.min(viaU, viaV))) {
			throw new IllegalArgumentException("vertices " + k + " and " + (k + 1)
					+ " of the path lie on separate pieces of the membrane");
		}
		List<Integer> chain = new ArrayList<>();
		for (int v = viaU <= viaV ? to.u : to.v; v >= 0; v = prev[v]) {
			chain.add(v);
		}
		java.util.Collections.reverse(chain);
		out.add(from.sample(p));
		for (int v : chain) {
			out.add(Sample.vertex(p, v));
		}
		out.add(to.sample(p));
		return out;
	}

	private static double vertexDistance(double[] p, int u, int v) {
		double dx = p[3 * v] - p[3 * u], dy = p[3 * v + 1] - p[3 * u + 1], dz = p[3 * v + 2] - p[3 * u + 2];
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	private static double distance(double[] x, double[] y) {
		double dx = y[0] - x[0], dy = y[1] - x[1], dz = y[2] - x[2];
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
}
