package org.vcell.solver.fenics;

import java.io.Serializable;

/**
 * Some values of some arrays of a FEniCSx results bundle: for each array, at each answered row, the values
 * at a set of indices along the row ({@link FenicsBundle#gather}). It is what the data server sends a desktop
 * that asked for a kymograph's or probe's vertices, instead of the arrays' whole rows
 * ({@link cbit.vcell.server.DataSetController#getFenicsBundleSamples}).
 */
public final class FenicsSamples implements Serializable {

	private static final long serialVersionUID = 1L;

	/** the rows answered: a prefix of the rows asked for (the reply stops at its size budget) */
	public final int[] rows;

	/** {@code values[array][row][index]}, an unwritten row holding the array's fill value as a whole read does */
	public final double[][][] values;

	/** {@code written[array][row]}: false where the row's chunk is not written yet (a running solver) */
	public final boolean[][] written;

	public FenicsSamples(int[] rows, double[][][] values, boolean[][] written) {
		this.rows = rows;
		this.values = values;
		this.written = written;
	}
}
