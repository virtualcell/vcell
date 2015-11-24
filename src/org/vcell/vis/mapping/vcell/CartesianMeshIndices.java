package org.vcell.vis.mapping.vcell;

public class CartesianMeshIndices {
	public final int numCells;
	public final double[] regionIndices;
	public final double[] globalIndices;
	
	public CartesianMeshIndices(int numCells, double[] regionIndices,	double[] globalIndices) {
		this.numCells = numCells;
		this.regionIndices = regionIndices;
		this.globalIndices = globalIndices;
	}
}