package org.vcell.client.viz;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.vcell.util.Coordinate;

import cbit.vcell.geometry.CurveSelectionInfo;
import cbit.vcell.geometry.SampledCurve;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionMembrane;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.MeshDisplayAdapter;

/**
 * A curve along a finite-volume membrane, selected and sampled the way the desktop's PDE data viewer does it
 * (docs/plan-plotting.md §1.1, P7): in a slice, the membrane is a set of curves
 * ({@link MeshDisplayAdapter#getCurvesAndMembraneIndexes}), one segment per membrane element crossed by the
 * slice; a selection is a run of consecutive segments of one curve ({@link CurveSelectionInfo}), sampled by
 * {@link SpatialSelectionMembrane#getIndexSamples()}: one sample per segment, at its start, plus the run's end,
 * each carrying its segment's membrane index, with arc length along the curve. So the samples, their indices and
 * arc lengths are the desktop's by construction.
 * <p>
 * The desktop picks the segments with the mouse in its 2D slice. Here the picks are waypoints, each snapping to
 * the nearest segment of the membrane domain in the slice (within {@link #REACH} segment lengths); all must lie
 * on one curve. The run goes from the first waypoint's segment to the last one's, through the others in order;
 * on a closed curve with two waypoints, the shorter way round.
 * <p>
 * In 2D the slice is the mesh's only one. In 3D it is the slice the viewer's crop cuts at, given as a normal axis;
 * its index is the solver slice nearest the first waypoint along that axis, and the waypoints are projected
 * onto it, as the desktop projects its curves onto the slice.
 */
final class FvMembraneCurve {

	/** How far from a membrane segment a waypoint may lie: this many lengths of the segment. */
	static final double REACH = 4;

	/** The membrane index of each sample (as {@code SSHelper.getSampledIndexes}). */
	final int[] membraneIndex;
	final double[] arcLength;
	final double[][] points;
	/** the waypoints, snapped onto their segments */
	final double[][] waypoints;
	/** {@link #points} and {@link #waypoints} as the viewer draws them ({@link #toDrawn}) */
	final double[][] drawnPoints;
	final double[][] drawnWaypoints;
	final int normalAxis;
	final int slice;

	private FvMembraneCurve(CartesianMesh mesh, SpatialSelection.SSHelper samples, double[][] waypoints, int normalAxis,
			int slice, double planeZ) {
		this.membraneIndex = samples.getSampledIndexes();
		this.arcLength = samples.getWorldCoordinateLengths();
		Coordinate[] c = samples.getSampleCoordinates();
		this.points = new double[c.length][];
		this.drawnPoints = new double[c.length][];
		for (int i = 0; i < c.length; i++) {
			points[i] = new double[] { c[i].getX(), c[i].getY(), c[i].getZ() };
			drawnPoints[i] = toDrawn(mesh, points[i], planeZ);
		}
		this.waypoints = waypoints;
		this.drawnWaypoints = new double[waypoints.length][];
		for (int k = 0; k < waypoints.length; k++) {
			drawnWaypoints[k] = toDrawn(mesh, waypoints[k], planeZ);
		}
		this.normalAxis = normalAxis;
		this.slice = slice;
	}

	/*
	 * Two frames. The solver's Cartesian mesh is node-centred: element i of an axis sits at o + i·E/(N−1), and the
	 * desktop's membrane curves are drawn in that frame (a segment's ends at the membrane face's corners, halfway
	 * between element centres). The viewer instead draws N equal boxes of E/N (CartesianMeshMapping), element i
	 * spanning o + i·E/N to o + (i+1)·E/N. Mapping a point by its fractional index f, o + f·E/(N−1) ↦
	 * o + (f + ½)·E/N, takes each desktop segment exactly onto the drawn face between the same two elements. So
	 * picks, made on the drawn membrane, are taken into the solver's frame before they snap, and the samples come
	 * back in both: the desktop's (for the arc lengths, exports and parity) and the drawn ones (for the overlay).
	 */

	/** A point of the viewer's drawn frame in the solver's; an axis of one element (z in 2D) is left as it is. */
	static double[] toSolver(CartesianMesh mesh, double[] drawn) {
		double[] o = { mesh.getOrigin().getX(), mesh.getOrigin().getY(), mesh.getOrigin().getZ() };
		double[] e = { mesh.getExtent().getX(), mesh.getExtent().getY(), mesh.getExtent().getZ() };
		int[] n = { mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ() };
		double[] p = drawn.clone();
		for (int a = 0; a < 3; a++) {
			if (n[a] > 1) {
				double f = (drawn[a] - o[a]) * n[a] / e[a] - 0.5;
				p[a] = o[a] + f * e[a] / (n[a] - 1);
			}
		}
		return p;
	}

	/** A point of the solver's frame in the viewer's drawn frame; an axis of one element takes {@code planeZ}. */
	static double[] toDrawn(CartesianMesh mesh, double[] solver, double planeZ) {
		double[] o = { mesh.getOrigin().getX(), mesh.getOrigin().getY(), mesh.getOrigin().getZ() };
		double[] e = { mesh.getExtent().getX(), mesh.getExtent().getY(), mesh.getExtent().getZ() };
		int[] n = { mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ() };
		double[] p = solver.clone();
		for (int a = 0; a < 3; a++) {
			if (n[a] > 1) {
				double f = (solver[a] - o[a]) * (n[a] - 1) / e[a];
				p[a] = o[a] + (f + 0.5) * e[a] / n[a];
			} else {
				p[a] = planeZ;
			}
		}
		return p;
	}

	int size() {
		return membraneIndex.length;
	}

	/** A waypoint's segment: curve {@code curve}, segment {@code segment}, at {@code point} on it. */
	private record Snap(int curve, int segment, double[] point) {
	}

	/** The axis a {@code plane} parameter names: {@code x}, {@code y} or {@code z}. */
	static int axisOf(String plane) {
		if (plane == null || plane.isBlank()) {
			throw new IllegalArgumentException("a curve along a 3D membrane lies in a slice: give its normal axis as"
					+ " 'plane=x|y|z' (in the viewer, turn on the crop)");
		}
		return switch (plane.trim().toLowerCase()) {
			case "x" -> Coordinate.X_AXIS;
			case "y" -> Coordinate.Y_AXIS;
			case "z" -> Coordinate.Z_AXIS;
			default -> throw new IllegalArgumentException("'plane' must be x, y or z, not '" + plane + "'");
		};
	}

	/**
	 * The curve through {@code waypoints} along the membrane domain whose elements are {@code inDomain}.
	 *
	 * @param drawnWaypoints the picks, in the viewer's drawn frame
	 * @param normalAxis the slice's normal ({@link Coordinate#Z_AXIS} in 2D)
	 * @param planeZ the drawn plane's z in 2D (the served grid's), for the drawn samples
	 * @throws IllegalArgumentException (a 400) when the slice has no membrane of the domain, a waypoint is not
	 *             near it, the waypoints lie on different curves or out of order, or there are more than
	 *             {@code maxSamples} samples
	 */
	static FvMembraneCurve select(CartesianMesh mesh, BitSet inDomain, double[][] drawnWaypoints, int normalAxis,
			int maxSamples, double planeZ) {
		double[][] waypoints = new double[drawnWaypoints.length][];
		for (int k = 0; k < waypoints.length; k++) {
			waypoints[k] = toSolver(mesh, drawnWaypoints[k]);
		}
		int slice = 0;
		if (mesh.getGeometryDimension() == 3) {
			Coordinate fractional = mesh.getFractionalCoordinateIndex(new Coordinate(waypoints[0][0], waypoints[0][1], waypoints[0][2]));
			int[] size = { mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ() };
			double f = normalAxis == Coordinate.X_AXIS ? fractional.getX() : normalAxis == Coordinate.Y_AXIS ? fractional.getY() : fractional.getZ();
			slice = (int) Math.max(0, Math.min(size[normalAxis] - 1, Math.round(f)));
		} else if (normalAxis != Coordinate.Z_AXIS) {
			throw new IllegalArgumentException("a 2D run has one slice, normal to z");
		}
		Map<SampledCurve, int[]> found = new MeshDisplayAdapter(mesh).getCurvesAndMembraneIndexes(normalAxis, slice);
		// the Hashtable's order is arbitrary: order the curves by their first point, so ties snap the same way every time
		List<Map.Entry<SampledCurve, int[]>> curves = new ArrayList<>(found == null ? List.of() : found.entrySet());
		curves.sort(Comparator.comparingDouble((Map.Entry<SampledCurve, int[]> e) -> e.getKey().getControlPoint(0).getX())
				.thenComparingDouble(e -> e.getKey().getControlPoint(0).getY())
				.thenComparingDouble(e -> e.getKey().getControlPoint(0).getZ()));
		boolean any = false;
		for (Map.Entry<SampledCurve, int[]> e : curves) {
			for (int m : e.getValue()) {
				any |= inDomain.get(m);
			}
		}
		if (!any) {
			throw new IllegalArgumentException("the membrane does not cross this slice"
					+ (mesh.getGeometryDimension() == 3 ? " (" + "xyz".charAt(normalAxis) + " slice " + slice + ")" : ""));
		}

		Snap[] snaps = new Snap[waypoints.length];
		double[][] snapped = new double[waypoints.length][];
		for (int k = 0; k < waypoints.length; k++) {
			snaps[k] = snap(curves, inDomain, waypoints[k], normalAxis, k);
			snapped[k] = snaps[k].point;
			if (snaps[k].curve != snaps[0].curve) {
				throw new IllegalArgumentException("the picks lie on different membrane curves in this slice;"
						+ " a curve runs along one of them");
			}
		}
		SampledCurve curve = curves.get(snaps[0].curve).getKey();
		int[] indexes = curves.get(snaps[0].curve).getValue();
		int count = curve.getSegmentCount();
		int s0 = snaps[0].segment;
		int sN = snaps[snaps.length - 1].segment;
		int[] through = new int[snaps.length - 2];
		for (int k = 1; k < snaps.length - 1; k++) {
			through[k - 1] = snaps[k].segment;
		}
		// the run from s0 to sN in each direction the curve allows; the one through the other picks, the shorter
		Boolean negative = null;
		double best = Double.POSITIVE_INFINITY;
		for (boolean neg : new boolean[] { false, true }) {
			List<Integer> run = run(s0, sN, neg, count, curve.isClosed());
			if (run == null || !passesInOrder(run, through)) {
				continue;
			}
			double length = 0;
			for (int s : run) {
				length += curve.getSegmentSpatialLength(s);
			}
			if (negative == null || length < best - 1e-12 * Math.max(1, best)) {
				best = length;
				negative = neg;
			}
		}
		if (negative == null) {
			throw new IllegalArgumentException("the picks are not in order along the membrane");
		}
		List<Integer> run = run(s0, sN, negative, count, curve.isClosed());
		for (int s : run) {
			if (!inDomain.get(indexes[s])) {
				throw new IllegalArgumentException("the curve leaves the membrane domain between the picks");
			}
		}
		if (run.size() + 1 > maxSamples) {
			throw new IllegalArgumentException("the curve crosses more than " + (maxSamples - 1)
					+ " membrane elements; pick a shorter one");
		}
		CurveSelectionInfo selection = s0 == sN ? new CurveSelectionInfo(curve, s0, s0, false)
				: new CurveSelectionInfo(curve, s0, sN, negative);
		int[] order = selection.getSegmentsInSelectionOrder();
		if (order == null || order.length != run.size()) {
			throw new IllegalStateException("the desktop's selection " + java.util.Arrays.toString(order)
					+ " is not the run " + run);
		}
		for (int i = 0; i < order.length; i++) {
			if (order[i] != run.get(i)) {
				throw new IllegalStateException("the desktop's selection " + java.util.Arrays.toString(order)
						+ " is not the run " + run);
			}
		}
		SpatialSelection.SSHelper samples = new SpatialSelectionMembrane(selection, VariableType.MEMBRANE, mesh, indexes, curve)
				.getIndexSamples();
		return new FvMembraneCurve(mesh, samples, snapped, normalAxis, slice, planeZ);
	}

	/** Segments s0 → sN, stepping down when {@code negative}; null where an open curve can't go that way. */
	private static List<Integer> run(int s0, int sN, boolean negative, int count, boolean closed) {
		List<Integer> run = new ArrayList<>();
		if (s0 == sN) {
			run.add(s0);
			return run;
		}
		if (!closed && (negative ? s0 < sN : s0 > sN)) {
			return null;
		}
		for (int s = s0;; s = Math.floorMod(s + (negative ? -1 : 1), count)) {
			run.add(s);
			if (s == sN) {
				return run;
			}
		}
	}

	/** Whether {@code run} meets each of {@code through}'s segments, in that order. */
	private static boolean passesInOrder(List<Integer> run, int[] through) {
		int at = 0;
		for (int s : through) {
			while (at < run.size() && run.get(at) != s) {
				at++;
			}
			if (at == run.size()) {
				return false;
			}
		}
		return true;
	}

	/** The nearest segment of the domain's membrane to {@code q}, measured in the slice. */
	private static Snap snap(List<Map.Entry<SampledCurve, int[]>> curves, BitSet inDomain, double[] q, int normalAxis, int k) {
		double best = Double.POSITIVE_INFINITY;
		Snap nearest = null;
		double nearestLength = 0;
		for (int c = 0; c < curves.size(); c++) {
			SampledCurve curve = curves.get(c).getKey();
			int[] indexes = curves.get(c).getValue();
			for (int s = 0; s < curve.getSegmentCount(); s++) {
				if (!inDomain.get(indexes[s])) {
					continue;
				}
				Coordinate[] ends = curve.getControlPointsForSegment(s);
				double[] a = { ends[0].getX(), ends[0].getY(), ends[0].getZ() };
				double[] b = { ends[1].getX(), ends[1].getY(), ends[1].getZ() };
				double[] p = q.clone();
				p[normalAxis] = a[normalAxis]; // project onto the slice
				double len2 = 0, dot = 0;
				for (int x = 0; x < 3; x++) {
					len2 += (b[x] - a[x]) * (b[x] - a[x]);
					dot += (p[x] - a[x]) * (b[x] - a[x]);
				}
				double t = len2 == 0 ? 0 : Math.max(0, Math.min(1, dot / len2));
				double[] on = new double[3];
				double d2 = 0;
				for (int x = 0; x < 3; x++) {
					on[x] = a[x] + t * (b[x] - a[x]);
					d2 += (p[x] - on[x]) * (p[x] - on[x]);
				}
				if (d2 < best) {
					best = d2;
					nearest = new Snap(c, s, on);
					nearestLength = Math.sqrt(len2);
				}
			}
		}
		if (nearest == null || Math.sqrt(best) > REACH * nearestLength) {
			throw new IllegalArgumentException("vertex " + (k + 1) + " of the path (" + q[0] + ", " + q[1] + ", " + q[2]
					+ ", in the solver's frame) is not on the membrane in this slice: pick on or just beside it");
		}
		return nearest;
	}
}
