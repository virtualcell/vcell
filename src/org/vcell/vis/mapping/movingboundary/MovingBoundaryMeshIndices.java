package org.vcell.vis.mapping.movingboundary;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

public class MovingBoundaryMeshIndices implements Matchable {
	public final int numCells;
	public final double[] globalIndices;  // globalIndex = yIndex*sizeX + xIndex  (typical image indexing).
	
	public MovingBoundaryMeshIndices(int numCells, double[] globalIndices) {
		this.numCells = numCells;
		this.globalIndices = globalIndices;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof MovingBoundaryMeshIndices){
			MovingBoundaryMeshIndices other = (MovingBoundaryMeshIndices) obj;
			if (numCells != other.numCells){
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