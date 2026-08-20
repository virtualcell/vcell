package cbit.vcell.solvers;



/**
 * Test-only access to {@link CartesianMesh} internals, from inside its package.
 * <p>
 * Membrane elements are only ever populated by the Geometry based factory, which needs a generated surface
 * collection. Code that merely classifies data by length, or writes membrane data through unchanged, does not care
 * what the elements are - only how many there are.
 */
public final class CartesianMeshTestSupport {

	private CartesianMeshTestSupport() {
	}

	/**
	 * Gives a mesh the stated number of membrane elements, so data of that length is classified as
	 * {@link cbit.vcell.math.VariableType#MEMBRANE}. The elements themselves are left null: any code that reads
	 * them needs a real geometry and should not be using this.
	 *
	 * @param mesh the mesh to modify
	 * @param count the number of membrane elements to report
	 * @return the same mesh, for chaining
	 */
	public static CartesianMesh withMembraneElementCount(CartesianMesh mesh, int count) {
		mesh.membraneElements = new MembraneElement[count];
		return mesh;
	}
}
