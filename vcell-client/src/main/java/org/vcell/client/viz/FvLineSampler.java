package org.vcell.client.viz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Coordinate;
import org.vcell.util.CoordinateIndex;

import cbit.vcell.geometry.CurveSelectionInfo;
import cbit.vcell.geometry.PolyLine;
import cbit.vcell.math.VariableType;
import cbit.vcell.simdata.SpatialSelection;
import cbit.vcell.simdata.SpatialSelectionVolume;
import cbit.vcell.solvers.CartesianMesh;

/**
 * The samples of a finite-volume kymograph: which voxels a lab-frame polyline crosses, in order, with
 * the arc length of each sample along the line.
 * <p>
 * It uses the desktop's own sampling, {@link SpatialSelectionVolume#getIndexSamples(double, double)} on a
 * {@link PolyLine} through the vertices — exactly what the desktop PDE viewer's kymograph does
 * ({@code PDEDataViewer.showKymograph}) — so a kymograph here matches the desktop's by construction: the
 * same voxels, the same arc lengths, and the two samples (one per side) where the line crosses a membrane,
 * with their membrane index for the {@code _INSIDE}/{@code _OUTSIDE} correction.
 * <p>
 * That code was written for lines in a 2D slice and throws on some 3D lines, where consecutive samples
 * touch only at a voxel vertex ("Couldn't adjust for corners"). Then this falls back to a 3D-DDA walk
 * (Amanatides and Woo) through the solver's voxels, with no membrane-crossing correction.
 * <p>
 * Both use the solver mesh's own voxel rule: VCell's Cartesian mesh is node-centred, element {@code i} at
 * {@code origin + i·extent/(N−1)}, so a point belongs to the element its fractional index rounds to (as
 * {@code /timeseries?points=} and the desktop map a point). The two end elements of an axis are half as
 * wide as the others.
 */
final class FvLineSampler {

	private static final Logger LG = LogManager.getLogger(FvLineSampler.class);

	/** Vertices of a kymograph path, a straight line being two. */
	static final int MAX_PATH_VERTICES = 64;

	enum Sampling {
		/** the desktop's {@code SpatialSelectionVolume} sampling, with membrane crossings */
		VOXEL_CROSSING("voxel-crossing"),
		/** the fallback lattice walk, one sample per voxel crossed, no membrane crossings */
		DDA("dda");

		final String json;

		Sampling(String json) {
			this.json = json;
		}
	}

	/**
	 * One kymograph's samples, all arrays parallel.
	 *
	 * @param volumeIndex the solver's volume index of each sample
	 * @param membraneIndex the membrane crossed at each sample of a crossing pair, -1 elsewhere; null when
	 *            the line crosses no membrane (as {@code SSHelper.getMembraneIndexesInOut()} gives it)
	 * @param arcLength the accumulated distance from the first sample, in the mesh's units
	 * @param points each sample's lab-frame point
	 */
	record Samples(Sampling sampling, int[] volumeIndex, int[] membraneIndex, double[] arcLength, double[][] points) {
		int size() {
			return volumeIndex.length;
		}
	}

	private FvLineSampler() {
	}

	/**
	 * Checks a path against the mesh: each vertex must lie in the mesh's box (to a relative 1e-6, and then
	 * is clamped into it), consecutive repeats are dropped, and the path must have positive length. The z
	 * of a 2D mesh is not checked: its one element spans every z.
	 *
	 * @return the path to sample, at least two vertices
	 * @throws IllegalArgumentException (a 400) for a vertex outside the mesh or a path of zero length
	 */
	static double[][] checkPath(CartesianMesh mesh, double[][] path) {
		double[] origin = { mesh.getOrigin().getX(), mesh.getOrigin().getY(), mesh.getOrigin().getZ() };
		double[] extent = { mesh.getExtent().getX(), mesh.getExtent().getY(), mesh.getExtent().getZ() };
		int[] size = { mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ() };
		List<double[]> kept = new ArrayList<>();
		for (double[] vertex : path) {
			double[] v = vertex.clone();
			for (int a = 0; a < 3; a++) {
				if (size[a] <= 1) {
					continue;
				}
				double slack = 1e-6 * extent[a];
				if (v[a] < origin[a] - slack || v[a] > origin[a] + extent[a] + slack) {
					throw new IllegalArgumentException("path vertex (" + vertex[0] + ", " + vertex[1] + ", " + vertex[2]
							+ ") lies outside the mesh, which spans " + Arrays.toString(origin) + " to "
							+ Arrays.toString(new double[] { origin[0] + extent[0], origin[1] + extent[1], origin[2] + extent[2] }));
				}
				v[a] = Math.max(origin[a], Math.min(origin[a] + extent[a], v[a]));
			}
			if (kept.isEmpty() || distance(kept.get(kept.size() - 1), v) > 0) {
				kept.add(v);
			}
		}
		if (kept.size() < 2) {
			throw new IllegalArgumentException("the path has zero length; a kymograph needs two distinct vertices");
		}
		return kept.toArray(new double[0][]);
	}

	/**
	 * The samples along a checked path ({@link #checkPath}): the desktop's sampling, or the DDA walk when
	 * that throws or returns something unusable.
	 */
	static Samples sample(CartesianMesh mesh, double[][] path) {
		try {
			Samples s = desktopSamples(mesh, path);
			if (usable(mesh, s)) {
				return s;
			}
			LG.debug("SpatialSelectionVolume sampling returned unusable samples; using the DDA walk");
		} catch (RuntimeException e) {
			LG.debug("SpatialSelectionVolume sampling failed ({}); using the DDA walk", e.getMessage());
		}
		return dda(mesh, path);
	}

	/** Exactly the desktop kymograph's sampling of a volume variable along this polyline. */
	static Samples desktopSamples(CartesianMesh mesh, double[][] path) {
		Coordinate[] coords = new Coordinate[path.length];
		for (int i = 0; i < path.length; i++) {
			coords[i] = new Coordinate(path[i][0], path[i][1], path[i][2]);
		}
		SpatialSelectionVolume ssv = new SpatialSelectionVolume(new CurveSelectionInfo(new PolyLine(coords)),
				VariableType.VOLUME, mesh);
		SpatialSelection.SSHelper helper = ssv.getIndexSamples(0.0, 1.0);
		Coordinate[] wc = helper.getSampleCoordinates();
		double[][] points = new double[wc.length][];
		for (int i = 0; i < wc.length; i++) {
			points[i] = new double[] { wc[i].getX(), wc[i].getY(), wc[i].getZ() };
		}
		return new Samples(Sampling.VOXEL_CROSSING, helper.getSampledIndexes(), helper.getMembraneIndexesInOut(),
				helper.getWorldCoordinateLengths(), points);
	}

	/** Every index a real volume element, and the arc lengths finite and non-decreasing. */
	private static boolean usable(CartesianMesh mesh, Samples s) {
		int n = mesh.getNumVolumeElements();
		if (s.size() < 1 || s.arcLength.length != s.size() || s.points.length != s.size()) {
			return false;
		}
		for (int i = 0; i < s.size(); i++) {
			if (s.volumeIndex[i] < 0 || s.volumeIndex[i] >= n || !Double.isFinite(s.arcLength[i])
					|| (i > 0 && s.arcLength[i] < s.arcLength[i - 1])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * The fallback: a 3D-DDA walk (Amanatides and Woo) through the solver's voxels along each segment, one
	 * sample per run of the path through one voxel. As in the desktop's sampling, the first sample sits at
	 * the path's start, the last at its end, and each other one at the middle of its voxel's stretch of the
	 * path; a path within one voxel gets two samples of it, at its two ends. A path through a voxel vertex
	 * steps diagonally into the next voxel, never through the zero-length stretches of its neighbours.
	 * Arc length is measured along the path.
	 */
	static Samples dda(CartesianMesh mesh, double[][] path) {
		double[] origin = { mesh.getOrigin().getX(), mesh.getOrigin().getY(), mesh.getOrigin().getZ() };
		double[] extent = { mesh.getExtent().getX(), mesh.getExtent().getY(), mesh.getExtent().getZ() };
		int[] size = { mesh.getSizeX(), mesh.getSizeY(), mesh.getSizeZ() };
		double[] h = new double[3];
		double hMin = Double.POSITIVE_INFINITY;
		for (int a = 0; a < 3; a++) {
			h[a] = size[a] > 1 ? extent[a] / (size[a] - 1) : Double.POSITIVE_INFINITY;
			hMin = Math.min(hMin, h[a]);
		}
		double tol = 1e-9 * hMin;

		List<int[]> runVoxel = new ArrayList<>();
		List<double[]> runSpan = new ArrayList<>();
		double s0 = 0;
		for (int seg = 1; seg < path.length; seg++) {
			double[] A = path[seg - 1];
			double[] B = path[seg];
			double len = distance(A, B);
			if (len == 0) {
				continue;
			}
			double[] d = { (B[0] - A[0]) / len, (B[1] - A[1]) / len, (B[2] - A[2]) / len };
			// the start voxel, judged a hair along the segment so a start on a voxel face takes the voxel ahead
			double nudge = Math.min(tol, len / 2);
			int[] idx = new int[3];
			int[] step = new int[3];
			double[] tMax = new double[3];
			double[] tDelta = new double[3];
			for (int a = 0; a < 3; a++) {
				if (size[a] <= 1) {
					idx[a] = 0;
					tMax[a] = Double.POSITIVE_INFINITY;
					continue;
				}
				double f = (A[a] + nudge * d[a] - origin[a]) / h[a];
				idx[a] = Math.max(0, Math.min(size[a] - 1, (int) Math.round(f)));
				if (Math.abs(d[a]) < 1e-12) {
					tMax[a] = Double.POSITIVE_INFINITY;
					continue;
				}
				step[a] = d[a] > 0 ? 1 : -1;
				double face = origin[a] + (idx[a] + 0.5 * step[a]) * h[a];
				tMax[a] = (face - A[a]) / d[a];
				tDelta[a] = h[a] / Math.abs(d[a]);
			}
			double t = 0;
			while (true) {
				double tNext = Math.min(len, Math.min(tMax[0], Math.min(tMax[1], tMax[2])));
				addRun(runVoxel, runSpan, idx, s0 + t, s0 + tNext, tol);
				if (tNext >= len - tol) {
					break;
				}
				boolean inside = true;
				for (int a = 0; a < 3; a++) {
					if (tMax[a] <= tNext + tol) {
						idx[a] += step[a];
						tMax[a] += tDelta[a];
						inside &= idx[a] >= 0 && idx[a] < size[a];
					}
				}
				if (!inside) {
					break; // left the mesh through its last face: only rounding can get here
				}
				t = tNext;
			}
			s0 += len;
		}
		double length = s0;
		int m = runVoxel.size();
		if (m == 0) {
			throw new IllegalStateException("the DDA walk crossed no voxel");
		}
		int n = m == 1 ? 2 : m;
		int[] volumeIndex = new int[n];
		double[] arcLength = new double[n];
		double[][] points = new double[n][];
		for (int j = 0; j < n; j++) {
			int r = Math.min(j, m - 1);
			int[] v = runVoxel.get(r);
			volumeIndex[j] = mesh.getVolumeIndex(new CoordinateIndex(v[0], v[1], v[2]));
			double[] span = runSpan.get(r);
			arcLength[j] = j == 0 ? 0 : j == n - 1 ? length : 0.5 * (span[0] + span[1]);
			points[j] = pointAt(path, arcLength[j]);
		}
		return new Samples(Sampling.DDA, volumeIndex, null, arcLength, points);
	}

	/** Appends a run through a voxel, merging it into the previous run of the same voxel and dropping zero-length ones. */
	private static void addRun(List<int[]> runVoxel, List<double[]> runSpan, int[] idx, double from, double to, double tol) {
		if (to - from <= tol) {
			return;
		}
		int last = runVoxel.size() - 1;
		if (last >= 0 && Arrays.equals(runVoxel.get(last), idx)) {
			runSpan.get(last)[1] = to;
			return;
		}
		runVoxel.add(idx.clone());
		runSpan.add(new double[] { from, to });
	}

	/** The point at arc length {@code s} along the polyline. */
	static double[] pointAt(double[][] path, double s) {
		double walked = 0;
		for (int i = 1; i < path.length; i++) {
			double len = distance(path[i - 1], path[i]);
			if (s <= walked + len || i == path.length - 1) {
				double f = len > 0 ? Math.max(0, Math.min(1, (s - walked) / len)) : 0;
				return new double[] { path[i - 1][0] + f * (path[i][0] - path[i - 1][0]),
						path[i - 1][1] + f * (path[i][1] - path[i - 1][1]),
						path[i - 1][2] + f * (path[i][2] - path[i - 1][2]) };
			}
			walked += len;
		}
		return path[path.length - 1].clone();
	}

	static double length(double[][] path) {
		double l = 0;
		for (int i = 1; i < path.length; i++) {
			l += distance(path[i - 1], path[i]);
		}
		return l;
	}

	private static double distance(double[] a, double[] b) {
		return Math.sqrt((a[0] - b[0]) * (a[0] - b[0]) + (a[1] - b[1]) * (a[1] - b[1]) + (a[2] - b[2]) * (a[2] - b[2]));
	}
}
