package org.vcell.vis.chombo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.vcell.vis.core.Face;
import org.vcell.vis.core.Vect3D;

public class ChomboBoundaries {
	

	public static class Point extends Vect3D {
		public Point(double x, double y, double z) {
			super(x, y, z);
		}
	}
	
	public static class Segment {
		private final int p1;
		private final int p2;
		private final int chomboIndex;
		
		public Segment(int chomboIndex,int p1,int p2){
			this.p1 = p1;
			this.p2 = p2;
			this.chomboIndex = chomboIndex;
		}

		public int getP1() {
			return p1;
		}

		public int getP2() {
			return p2;
		}
		
		public int getChomboIndex(){
			return this.chomboIndex;
		}
	}
	
	public static class SurfaceTriangle {
		private final int p1;
		private final int p2;
		private final int p3;
		
		private final int chomboIndex;
		private final Face face;
		
		public SurfaceTriangle(int chomboIndex, Face face, int p1, int p2, int p3){
			this.chomboIndex = chomboIndex;
			this.face = face;
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		}

		public int getP1() {
			return p1;
		}

		public int getP2() {
			return p2;
		}
		
		public int getP3() {
			return p3;
		}
		
		public int getChomboIndex(){
			return this.chomboIndex;
		}
		
		public Face getFace(){
			return this.face;
		}
	}
	
	public static class BorderCellInfo {
		final int index;
		final int level;
		final int i;
		final int j;
		final int k;
		final Vect3D center;  // x,y,z
		final Vect3D normal;  // normalX, normalY, normalZ
		final double volfract;
		final double areaFract;
		final int membraneId;
		final int cornerPhaseMask;
		
		public BorderCellInfo(int index, int level, int i, int j, int k,
				Vect3D center, Vect3D normal, double volfract,
				double areaFract, int membraneId, int cornerPhaseMask) {
			super();
			this.index = index;
			this.level = level;
			this.i = i;
			this.j = j;
			this.k = k;
			this.center = center;
			this.normal = normal;
			this.volfract = volfract;
			this.areaFract = areaFract;
			this.membraneId = membraneId;
			this.cornerPhaseMask = cornerPhaseMask;
		}

		// if the bit is 1, it's in phase 1
		public boolean isVertexInPhase1(int vertexIndex) {
			if (vertexIndex >= 0 && vertexIndex < 8)
			{
			  return (cornerPhaseMask & (0x1 << vertexIndex)) != 0;
			}
			else
			{
				throw new RuntimeException("unexpected vertex index "+vertexIndex);
			}
		}
	}
	
	public static class MeshMetrics {
		private final ArrayList<BorderCellInfo> borderCellInfos = new ArrayList<ChomboBoundaries.BorderCellInfo>();
		
		public MeshMetrics(){
		}
		
		public void addBorderCellInfo(BorderCellInfo borderCellInfo){
			this.borderCellInfos.add(borderCellInfo);
		}
		
		public BorderCellInfo getBorderCellInfo(int index){
			BorderCellInfo borderCellInfo = this.borderCellInfos.get(index);
			if (borderCellInfo.index!=index){
				throw new RuntimeException("index didn't match");
			}
			return borderCellInfo;
		}
	}
	
	private final MeshMetrics meshMetrics = new MeshMetrics();
	private final ArrayList<Segment> segments = new ArrayList<Segment>();
	private final ArrayList<Point> vertices = new ArrayList<Point>();
	private final ArrayList<SurfaceTriangle> surfaceTriangles = new ArrayList<ChomboBoundaries.SurfaceTriangle>();
	private HashMap<String,Integer> pointDict = new HashMap<String,Integer>();
//	private final int viewLevel;
	
	public ChomboBoundaries(){
	}
	
	public void addPoint(Point point){
		this.vertices.add(point);
	}
	
	public List<Point> getPoints(){
		return vertices;
	}

	public void addSegment(Segment segment){
		this.segments.add(segment);
	}
	
	public List<Segment> getSegments(){
		return segments;
	}
	
	public MeshMetrics getMeshMetrics() {
		return meshMetrics;
	}

	public int getOrCreatePoint(double x, double y, double z) {
		Point newPoint = new Point(x,y,z);
		String coordKey = newPoint.toStringKey();
		Integer index = pointDict.get(coordKey);
		if (index!=null){
			return index;
		}else{
			this.vertices.add(newPoint);
			index = this.vertices.size() - 1;
			pointDict.put(coordKey, index);
			return index;
		}
	}

	public void addSurfaceTriangle(SurfaceTriangle surfaceTriangle) {
		this.surfaceTriangles.add(surfaceTriangle);
	}
	
	public List<SurfaceTriangle> getSurfaceTriangles(){
		return this.surfaceTriangles;
	}

}
