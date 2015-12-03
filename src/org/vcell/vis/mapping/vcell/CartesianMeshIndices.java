package org.vcell.vis.mapping.vcell;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

public class CartesianMeshIndices implements Matchable {
	public final int numCells;
	public final double[] regionIndices;
	public final double[] globalIndices;
	
	public CartesianMeshIndices(int numCells, double[] regionIndices,	double[] globalIndices) {
		this.numCells = numCells;
		this.regionIndices = regionIndices;
		this.globalIndices = globalIndices;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof CartesianMeshIndices){
			CartesianMeshIndices other = (CartesianMeshIndices) obj;
			if (numCells != other.numCells){
				return false;
			}
			if (!Compare.isEqual(regionIndices, other.regionIndices)){
				return false;
			}
			if (!Compare.isEqual(globalIndices, other.globalIndices)){
				return false;
			}
			return true;
		}
		return false;
	}
	
}