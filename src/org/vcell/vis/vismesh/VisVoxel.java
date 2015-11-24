package org.vcell.vis.vismesh;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.vis.core.Vect3D;

public class VisVoxel implements VisPolyhedron {
	
	private int[] pointIndices;
	private final int level;
	private final int boxNumber;
	private final int boxIndex;
	private final double fraction;
	private final int regionIndex;
	
	public VisVoxel(int[] pointIndices, int level, int boxNumber, int boxIndex, double fraction, int regionIndex) {
		this.level = level;
		this.pointIndices = pointIndices;
		this.boxNumber = boxNumber;
		this.boxIndex = boxIndex;
		this.fraction = fraction;
		this.regionIndex = regionIndex;
	}

	public int[] getPointIndices() {
		return pointIndices;
	}

	@Override
	public int getLevel(){
		return level;
	}
	
	@Override
	public int getBoxNumber() {
		return boxNumber;
	}

	@Override
	public int getBoxIndex() {
		return boxIndex;
	}

	public double getFraction() {
		return fraction;
	}
	
	public int getRegionIndex() {
		return regionIndex;
	}
	
	public void setPointIndices(int[] pointIndices){
		this.pointIndices = pointIndices;
	}
	
	public String toString(){
		return "VisPolyhedra@"+hashCode()+": level="+level+", box="+boxNumber+", boxIndex="+boxIndex+", fraction="+fraction+", vertices="+pointIndices;
	}

	public Vect3D getCentroid(VisMesh visMesh) {
		double x=0;
		double y=0;
		double z=0;
		int numP = pointIndices.length;
		for (int pointIndex : pointIndices){
			VisPoint point = visMesh.getPoints().get(pointIndex);
			x += point.x;
			y += point.y;
			z += point.z;
		}
		return new Vect3D(x/numP,y/numP,z/numP);
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof VisVoxel){
			VisVoxel other = (VisVoxel) obj;
			if (level != other.level){
				return false;
			}
			if (boxNumber != other.boxNumber){
				return false;
			}
			if (boxIndex != other.boxIndex){
				return false;
			}
			if (fraction != other.fraction){
				return false;
			}
			if (regionIndex != other.regionIndex){
				return false;
			}
			if (!Compare.isEqual(pointIndices, other.pointIndices)){
				return false;
			}
			return true;
		}
		return false;
	}

}
