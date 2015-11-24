package org.vcell.vis.vismesh;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

public class VisIrregularPolyhedron implements VisPolyhedron {
	
	private final int level;
	private final int boxNumber;
	private final int boxIndex;
	private final double fraction;
	private final int regionIndex;

	public static class PolyhedronFace implements Matchable {
		private final int[] vertices;
		
		public PolyhedronFace(int[] vertices){
			this.vertices = vertices;
		}
		
		public int[] getVertices(){
			return this.vertices;
		}

		@Override
		public boolean compareEqual(Matchable obj) {
			if (obj instanceof PolyhedronFace){
				PolyhedronFace other = (PolyhedronFace)obj;
				if (!Compare.isEqual(vertices, other.vertices)){
					return false;
				}
				return true;
			}
			return false;
		}
	}

	private final ArrayList<PolyhedronFace> polyhedronFaces = new ArrayList<PolyhedronFace>();
	
	
	
	public VisIrregularPolyhedron(int level, int boxNumber, int boxIndex, double fraction, int regionIndex) {
		super();
		this.level = level;
		this.boxNumber = boxNumber;
		this.boxIndex = boxIndex;
		this.fraction = fraction;
		this.regionIndex = regionIndex;
	}

	public int getLevel() {
		return level;
	}

	public int getBoxNumber() {
		return boxNumber;
	}

	public int getBoxIndex() {
		return boxIndex;
	}

	public double getFraction() {
		return fraction;
	}
	
	public int getRegionIndex() {
		return regionIndex;
	}

	public void addFace(PolyhedronFace polyhedronFace){
		this.polyhedronFaces.add(polyhedronFace);
	}
	
	public List<PolyhedronFace> getFaces(){
		return this.polyhedronFaces;
	}
	
	public int[] getPointIndices() {
		HashSet<Integer> pointIndicesSet = new HashSet<Integer>();
		for (PolyhedronFace face : polyhedronFaces){
			for (int pointIndex : face.vertices){
				pointIndicesSet.add(pointIndex);
			}
		}
		int[] pointArray = new int[pointIndicesSet.size()];
		int i = 0;
		for (Integer uniquePointIndex : pointIndicesSet){
			pointArray[i] = uniquePointIndex;
			i++;
		}
		return pointArray;
	}

	public int[] getVtkFaceStream() {
		ArrayList<Integer> faceStream = new ArrayList<Integer>();
		faceStream.add(polyhedronFaces.size());
		for (PolyhedronFace polyhedronFace : polyhedronFaces){
			faceStream.add(polyhedronFace.getVertices().length);
			for (int v : polyhedronFace.getVertices()){
				faceStream.add(v);
			}
		}
		int[] intFaceStream = new int[faceStream.size()];
		for (int i=0;i<intFaceStream.length;i++){
			intFaceStream[i] = faceStream.get(i);
		}
		return intFaceStream;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof VisIrregularPolyhedron){
			VisIrregularPolyhedron other = (VisIrregularPolyhedron) obj;
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
			if (!Compare.isEqualStrict(polyhedronFaces, other.polyhedronFaces)){
				return false;
			}
			return true;
		}
		return false;
	}
}
