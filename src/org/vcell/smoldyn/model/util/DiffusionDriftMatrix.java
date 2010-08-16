package org.vcell.smoldyn.model.util;

import org.vcell.smoldyn.model.SpeciesState;


/**
 * Specifies diffusion or drift characteristics of a {@link SpeciesState}.  Smoldyn uses an array of size equal to the square of
 * the number of dimensions in the system to specify anisotropic diffusion rates.  The same format is used to specify anisotropic drift
 * characteristics.  The semantics of the array are currently not understood.  Only 3-d simulations are currently supported.
 * 
 * <p>
 * Smoldyn's diffusion drift matrix
 * <ul>
 * <li>D is the (symmetric) diffusion matrix</li>
 * <li>the size of D is the square of the number of dimensions in the system</li>
 * <li>Smoldyn uses the square root of the diffusion matrix because the square root is much more convenient for calculating 
 * expectation molecule displacements</li>
 * <li>the symmetric property of D implies some symmetry properties for its square root as well (for example, a symmetric square 
 * root leads to a symmetric D)</li>
 * <li>if D is diagonal, the square root of the matrix is found by simply replacing each element with its square root </li>
 * <li>if D is equal to the identity matrix times a constant, D, the equation reduces to the standard isotropic diffusion equation</li>
 * </ul>
 * </p>
 * 
 * @author mfenwick
 *
 */
public class DiffusionDriftMatrix {

	private Double [] matrix;
	private Integer dimensionality;
	
	
	/**
	 * Builds a new DiffusionDriftMatrix with the specified number of dimenions and values.
	 * <strong class="warning">WARNING:  the semantics of the values are NOT understood.  Smoldyn 
	 * expects the square root of the actual diffusion/drift matrix.</strong>
	 * 
	 * @param dimensions
	 * @param matrix
	 * @throws RuntimeException if length of matrix not equal to square of dimensions
	 * @throws RuntimeException if dimensions not equal to 3
	 */
	public DiffusionDriftMatrix(Integer dimensions, Double [] matrix) {
		if (dimensions != 3) {
			throw new RuntimeException("system must have at least 1 dimension (got "+dimensions+" dimensions)");
		}
		if (matrix.length != dimensions * dimensions) {
			throw new RuntimeException("diffusion/drift matrix size must be equal to square of dimensionality:   size was: "+matrix.length+", dimensionality was:  "+dimensions+",  expected size was:  "+(dimensions*dimensions));
		}
		this.dimensionality = dimensions;
	}
	
	
	public Integer getDimensionality() {
		return this.dimensionality;
	}
	public Double [] getMatrix() {
		return this.matrix;
	}
}
