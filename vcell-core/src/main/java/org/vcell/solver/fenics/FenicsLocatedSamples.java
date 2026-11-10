package org.vcell.solver.fenics;

import java.io.Serializable;

/**
 * Lab-frame points located in a moving (ALE) FEniCSx mesh row by row, with what the desktop needs to interpolate
 * there ({@link FenicsBundle#locate}): per answered row, the cell each point fell in (its index, type and vertices),
 * the vertices of those cells with their positions in that row, and the values of some arrays at those vertices. It
 * is what the data server sends a desktop drawing a kymograph or probe of a moving run, instead of every row's whole
 * point positions and values ({@link cbit.vcell.server.DataSetController#getFenicsBundleLocatedSamples}).
 * <p>
 * The cells are found by {@link org.vcell.vis.vtk.VtuGridParser#locate} (and {@code nearestOnMesh} when snapping)
 * on the row's whole mesh, exactly as the desktop finds them; the P1 weights depend only on a cell's own vertex
 * positions, which are sent, so the desktop interpolates to the same bits.
 */
public final class FenicsLocatedSamples implements Serializable {

	private static final long serialVersionUID = 1L;

	/** the rows answered: a prefix of the rows asked for (the reply stops at its budgets) */
	public final int[] rows;

	/** {@code cells[row][point]}: the cell holding the point in that row's mesh, -1 outside */
	public final int[][] cells;

	/**
	 * {@code cellVertices[row][point]}: that cell's vertices in the mesh's order (null outside), so the desktop needs
	 * no copy of a remeshed segment's mesh to interpolate in it
	 */
	public final int[][][] cellVertices;

	/** {@code cellTypes[row][point]}: that cell's VTK type, -1 outside */
	public final int[][] cellTypes;

	/**
	 * {@code snapped[row]}: null when no point of the row was moved; else x,y,z per point where the snap moved it
	 * onto the mesh, NaN where it did not
	 */
	public final double[][] snapped;

	/** {@code vertices[row]}: the located cells' vertices, strictly increasing */
	public final int[][] vertices;

	/** {@code coords[row]}: x,y,z of each of {@code vertices[row]} in that row's mesh */
	public final double[][] coords;

	/** {@code coordsWritten[row]}: false where the row's point positions are not written yet (all NaN) */
	public final boolean[] coordsWritten;

	/** {@code values[array][row][i]}: the array's value at {@code vertices[row][i]} */
	public final double[][][] values;

	/** {@code written[array][row]}: false where the row's chunk is not written yet (its fill value) */
	public final boolean[][] written;

	public FenicsLocatedSamples(int[] rows, int[][] cells, int[][][] cellVertices, int[][] cellTypes, double[][] snapped,
			int[][] vertices, double[][] coords, boolean[] coordsWritten, double[][][] values, boolean[][] written) {
		this.rows = rows;
		this.cells = cells;
		this.cellVertices = cellVertices;
		this.cellTypes = cellTypes;
		this.snapped = snapped;
		this.vertices = vertices;
		this.coords = coords;
		this.coordsWritten = coordsWritten;
		this.values = values;
		this.written = written;
	}
}
