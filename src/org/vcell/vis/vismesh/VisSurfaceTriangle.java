package org.vcell.vis.vismesh;

import org.vcell.vis.core.Face;
import org.vcell.vis.core.Vect3D;

public class VisSurfaceTriangle implements ChomboVisMembraneIndex {
	
	private int[] pointIndices;
	private final int chomboIndex;
	private final Face face;
	
	public VisSurfaceTriangle(int[] pointIndices, int chomboIndex, Face face) {
		this.pointIndices = pointIndices;
		this.chomboIndex = chomboIndex;
		this.face = face;
	}

	public int[] getPointIndices() {
		return pointIndices;
	}

	@Override
	public int getChomboIndex(){
		return chomboIndex;
	}
	
	public Face getFace() {
		return face;
	}
	
	public void setPointIndices(int[] pointIndices){
		this.pointIndices = pointIndices;
	}
	
	public String toString(){
		return "VisSurfaceTriangle@"+hashCode()+": face="+face+", chomboIndex="+chomboIndex+", vertices="+pointIndices;
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

}
