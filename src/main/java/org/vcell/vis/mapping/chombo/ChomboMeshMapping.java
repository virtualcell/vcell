package org.vcell.vis.mapping.chombo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.vcell.util.ISize;
import org.vcell.vis.chombo.ChomboBoundaries;
import org.vcell.vis.chombo.ChomboBoundaries.BorderCellInfo;
import org.vcell.vis.chombo.ChomboBox;
import org.vcell.vis.chombo.ChomboDataset.ChomboCombinedVolumeMembraneDomain;
import org.vcell.vis.chombo.ChomboLevel;
import org.vcell.vis.chombo.ChomboLevel.Covering;
import org.vcell.vis.chombo.ChomboLevelData;
import org.vcell.vis.chombo.ChomboMesh;
import org.vcell.vis.chombo.ChomboMeshData;
import org.vcell.vis.vismesh.thrift.ChomboSurfaceIndex;
import org.vcell.vis.vismesh.thrift.ChomboVolumeIndex;
import org.vcell.vis.vismesh.thrift.Face;
import org.vcell.vis.vismesh.thrift.PolyhedronFace;
import org.vcell.vis.vismesh.thrift.Vect3D;
import org.vcell.vis.vismesh.thrift.VisIrregularPolyhedron;
import org.vcell.vis.vismesh.thrift.VisLine;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPoint;
import org.vcell.vis.vismesh.thrift.VisPolygon;
import org.vcell.vis.vismesh.thrift.VisSurfaceTriangle;
import org.vcell.vis.vismesh.thrift.VisVoxel;


public class ChomboMeshMapping {
	private static Logger LG = Logger.getLogger(ChomboMeshMapping.class);
	
	public ChomboMeshMapping(){
		
	}

	public VisMesh fromMeshData(ChomboMeshData chomboMeshData, ChomboCombinedVolumeMembraneDomain chomboCombinedVolumeMembraneDomain){
		int dimension = chomboMeshData.getMesh().getDimension();
		if (dimension==2){
			return fromMeshData2D(chomboMeshData);
		}else if (dimension==3){
			return fromMeshData3D(chomboMeshData, chomboCombinedVolumeMembraneDomain);
		}else{
			throw new RuntimeException("unsupported mesh dimension = "+dimension);
		}
	}
	
	private Vect3D toThrift(org.vcell.vis.core.Vect3D vect3D){
		return new Vect3D(vect3D.x,vect3D.y,vect3D.z);
	}
	
	public String toStringKey(VisPoint visPoint){
		return toStringKey(visPoint, 8);
	}
	
	public String toStringKey(VisPoint visPoint, int precision){
		String formatString = "%."+precision+"f";
		return "("+String.format(formatString,visPoint.x)+","+String.format(formatString,visPoint.y)+","+String.format(formatString,visPoint.z)+")";
	}

	
	private VisMesh fromMeshData2D(ChomboMeshData chomboMeshData){
	    ChomboMesh chomboMesh = chomboMeshData.getMesh();
	    ChomboLevel finestLevel = chomboMesh.getLevel(chomboMesh.getNumLevels()-1);
	    int finestAbsRefinement = finestLevel.getAbsoluteRefinement();
	    ISize size = finestLevel.getSize();
	    int numX = size.getX();
	    int numY = size.getY();
	    int numZ = size.getZ();
	    int dimension = chomboMeshData.getMesh().getDimension();
	
	    int z = 0;
	    VisMesh visMesh = new VisMesh(chomboMesh.getDimension(),toThrift(chomboMesh.getOrigin()), toThrift(chomboMesh.getExtent())); // invoke VisMesh() constructor
	    int currPointIndex = 0;
	    HashMap<String,Integer> pointDict = new HashMap<String,Integer>();
	    double originX = chomboMesh.getOrigin().x;
	    double originY = chomboMesh.getOrigin().y;
	    double originZ = chomboMesh.getOrigin().z;
	    double extentX = chomboMesh.getExtent().x;
	    double extentY = chomboMesh.getExtent().y;
	    double extentZ = chomboMesh.getExtent().z;
	    ChomboBoundaries chomboBoundaries = chomboMesh.getBoundaries();
	    for (ChomboBoundaries.Point chomboPoint : chomboBoundaries.getPoints()){
	    	
	    	double px = chomboPoint.x;
	    	double py = chomboPoint.y;
	    	double pz = z;
	        px = (px-originX)*(numX)/extentX*2-1;
	        py = (py-originY)*(numY)/extentY*2-1;
	        pz = (pz-originZ)*(numZ)/extentZ*2-1;
	        if (dimension==2){
	        	pz = z;
	        }
	        
	        VisPoint newVisPoint = new VisPoint(px,py,pz);
	        String coordKey = toStringKey(newVisPoint);
	        pointDict.put(coordKey, currPointIndex);
	        visMesh.addToPoints(newVisPoint);
	        visMesh.addToSurfacePoints(newVisPoint);
	        currPointIndex += 1;
	        
	    }
	    for (ChomboBoundaries.Segment segment : chomboBoundaries.getSegments()){
	    	VisLine newVisLine = new VisLine(segment.getP1(),segment.getP2());
	    	newVisLine.setChomboSurfaceIndex(new ChomboSurfaceIndex(segment.getChomboIndex()));
	    	visMesh.addToVisLines(newVisLine);
	    }
	
	    for (int levelIndex=0; levelIndex < chomboMesh.getNumLevels(); levelIndex++){
	        ChomboLevelData chomboLevelData = chomboMeshData.getLevelData(levelIndex);
	        ChomboLevel currLevel = chomboMesh.getLevel(levelIndex);
	        int currAbsRefinement = currLevel.getAbsoluteRefinement();
	        Covering covering = currLevel.getCovering();
	        int[] levelMap = covering.getLevelMap();
	        int[] boxNumberMap = covering.getBoxNumberMap();
	        int[] boxIndexMap = covering.getBoxIndexMap();
	        int levelNumX = currLevel.getSize().getX();
	        int levelNumY = currLevel.getSize().getY();
	        for (int x=0; x<levelNumX; x++){
	            for (int y=0; y<levelNumY; y++){
	                int mapIndex = x + y*levelNumX;
	                if (levelMap[mapIndex] == levelIndex) {
	                    //
	                    // if fraction (volume fraction of element in box) is 0 ... then skip this element
	                    //
	                    int boxNumber = boxNumberMap[mapIndex];
	                    int boxIndex = boxIndexMap[mapIndex];
	                    double fraction = chomboLevelData.getCellFraction(currLevel,boxNumber,boxIndex);
	                    if (fraction > 0){
	                        //
	                        // add cell
	                        //
	
	                        ChomboBox chomboBox = new ChomboBox(currLevel,x,x,y,y,z,z,dimension).getProjectedBox(currAbsRefinement,finestAbsRefinement);
	                        double minX = 2*chomboBox.getMinX()-1;
	                        double maxX = 2*chomboBox.getMaxX()+1;
	                        double minY = 2*chomboBox.getMinY()-1;
	                        double maxY = 2*chomboBox.getMaxY()+1;
	
	                        //
	                        // counter clockwise points for a VisPolygon ... initially a quad ... then may be
	                        //
	                        //  minX,minY
	                        //  minX,maxY
	                        //  maxX,maxY
	                        //  maxX,minY
	                        //
	                        VisPoint p1Coord = new VisPoint(minX,minY,z);
	                        String p1Key = toStringKey(p1Coord);
	                        Integer i1 = pointDict.get(p1Key);
	                        if (i1 == null){
	                            pointDict.put(p1Key,currPointIndex);
	                            i1 = currPointIndex;
	                            visMesh.addToPoints(p1Coord);
	                            currPointIndex++;
	                        }
	
	                        VisPoint p2Coord = new VisPoint(minX,maxY,z);
	                        String p2Key = toStringKey(p2Coord);
	                        Integer i2 = pointDict.get(p2Key);
	                        if (i2 == null){
	                            pointDict.put(p2Key,currPointIndex);
	                            i2 = currPointIndex;
	                            visMesh.addToPoints(p2Coord);
	                            currPointIndex++;
	                        }
	
	                        VisPoint p3Coord = new VisPoint(maxX,maxY,z);
	                        String p3Key = toStringKey(p3Coord);
	                        Integer i3 = pointDict.get(p3Key);
	                        if (i3 == null){
	                            pointDict.put(p3Key, currPointIndex);
	                            i3 = currPointIndex;
	                            visMesh.addToPoints(p3Coord);
	                            currPointIndex++;
	                        }
	
	                        VisPoint p4Coord = new VisPoint(maxX,minY,z);
	                        String p4Key = toStringKey(p4Coord);
	                        Integer i4 = pointDict.get(p4Key);
	                        if (i4 == null){
	                            pointDict.put(p4Key,currPointIndex);
	                            i4 = currPointIndex;
	                            visMesh.addToPoints(p4Coord);
	                            currPointIndex++;
	                        }
	            
	                        VisPolygon quad = new VisPolygon(Arrays.asList(new Integer[] {i1,i2,i3,i4}));
	                        quad.setChomboVolumeIndex(new ChomboVolumeIndex(levelIndex,boxNumber,boxIndex,fraction));
	                      //  print('adding a cell at level '+str(currLevel.getLevel())+" from "+str(p1Coord)+" to "+str(p3Coord))
	                        visMesh.addToPolygons(quad);
	                    }
	                }
	            }
	        }
	    }
	    cropQuads(visMesh);
	    return visMesh;
	}

	private VisMesh fromMeshData3D(ChomboMeshData chomboMeshData, ChomboCombinedVolumeMembraneDomain chomboCombinedVolumeMembraneDomain){
		int dimension = chomboMeshData.getMesh().getDimension();
		if (dimension!=3){
			throw new RuntimeException("expecting a 3D mesh");
		}
	    ChomboMesh chomboMesh = chomboMeshData.getMesh();
	    ChomboLevel finestLevel = chomboMesh.getLevel(chomboMesh.getNumLevels()-1);
	    int finestAbsRefinement = finestLevel.getAbsoluteRefinement();
	    ISize size = finestLevel.getSize();
	    int numX = size.getX();
	    int numY = size.getY();
	    int numZ = size.getZ();
	
	    Vect3D origin = new Vect3D(chomboMesh.getOrigin().x,chomboMesh.getOrigin().y,chomboMesh.getOrigin().z);
	    Vect3D extent = new Vect3D(chomboMesh.getExtent().x,chomboMesh.getExtent().y,chomboMesh.getExtent().z);
	    VisMesh visMesh = new VisMesh(chomboMesh.getDimension(),origin, extent); // invoke VisMesh() constructor
	    int currPointIndex = 0;
	    HashMap<String,Integer> pointDict = new HashMap<String,Integer>();
	    double originX = chomboMesh.getOrigin().x;
	    double originY = chomboMesh.getOrigin().y;
	    double originZ = chomboMesh.getOrigin().z;
	    double extentX = chomboMesh.getExtent().x;
	    double extentY = chomboMesh.getExtent().y;
	    double extentZ = chomboMesh.getExtent().z;
	    ChomboBoundaries chomboBoundaries = chomboMesh.getBoundaries();
	    for (ChomboBoundaries.Point chomboPoint : chomboBoundaries.getPoints()){
	    	
	    	double px = chomboPoint.x;
	    	double py = chomboPoint.y;
	    	double pz = chomboPoint.z;
	        px = (px-originX)*(numX)/extentX*2-1;
	        py = (py-originY)*(numY)/extentY*2-1;
	        pz = (pz-originZ)*(numZ)/extentZ*2-1;
	        
	        VisPoint newVisPoint = new VisPoint(px,py,pz);
	        String coordKey = toStringKey(newVisPoint);
	        pointDict.put(coordKey, currPointIndex);
	        visMesh.addToPoints(newVisPoint);
	        visMesh.addToSurfacePoints(newVisPoint);
	        currPointIndex += 1;	        
	    }
	    for (ChomboBoundaries.SurfaceTriangle surfaceTriangle : chomboBoundaries.getSurfaceTriangles()){
	    	List<Integer> vertices = Arrays.asList(new Integer[] { surfaceTriangle.getP1(),surfaceTriangle.getP2(), surfaceTriangle.getP3() });
			org.vcell.vis.vismesh.thrift.Face face = org.vcell.vis.vismesh.thrift.Face.valueOf(surfaceTriangle.getFace().name());
			VisSurfaceTriangle newVisSurfaceTriangle = new VisSurfaceTriangle(vertices, face);
			newVisSurfaceTriangle.setChomboSurfaceIndex(new ChomboSurfaceIndex(surfaceTriangle.getChomboIndex()));
	    	visMesh.addToSurfaceTriangles(newVisSurfaceTriangle);
	    }
	
	    for (int levelIndex=0; levelIndex < chomboMesh.getNumLevels(); levelIndex++){
	        ChomboLevelData chomboLevelData = chomboMeshData.getLevelData(levelIndex);
	        ChomboLevel currLevel = chomboMesh.getLevel(levelIndex);
	        int currAbsRefinement = currLevel.getAbsoluteRefinement();
	        Covering covering = currLevel.getCovering();
	        int[] levelMap = covering.getLevelMap();
	        int[] boxNumberMap = covering.getBoxNumberMap();
	        int[] boxIndexMap = covering.getBoxIndexMap();
	        int levelNumX = currLevel.getSize().getX();
	        int levelNumY = currLevel.getSize().getY();
	        int levelNumZ = currLevel.getSize().getZ();
	        for (int x=0; x<levelNumX; x++){
	            for (int y=0; y<levelNumY; y++){
		            for (int z=0; z<levelNumZ; z++){
		                int mapIndex = x + y*levelNumX + z*levelNumX*levelNumY;
		                if (levelMap[mapIndex] == levelIndex) {
		                    //
		                    // if fraction (volume fraction of element in box) is 0 ... then skip this element
		                    //
		                    int boxNumber = boxNumberMap[mapIndex];
		                    int boxIndex = boxIndexMap[mapIndex];
		                    double fraction = chomboLevelData.getCellFraction(currLevel,boxNumber,boxIndex);
		                    if (fraction > 0){
		                        //
		                        // add cell
		                        //
		
		                        ChomboBox chomboBox = new ChomboBox(currLevel,x,x,y,y,z,z,dimension).getProjectedBox(currAbsRefinement,finestAbsRefinement);
		                        double minX = 2*chomboBox.getMinX()-1;
		                        double maxX = 2*chomboBox.getMaxX()+1;
		                        double minY = 2*chomboBox.getMinY()-1;
		                        double maxY = 2*chomboBox.getMaxY()+1;
		                        double minZ = 2*chomboBox.getMinZ()-1;
		                        double maxZ = 2*chomboBox.getMaxZ()+1;
		
		                        //
		                        // points for a VisPolyhedra ... initially a hex ... then may be clipped
		                        //
		                        //       p6-------------------p7
		                        //      /|                   /|
		                        //     / |                  / |
		                        //   p4-------------------p5  |
		                        //    |  |                 |  |
		                        //    |  |                 |  |
		                        //    |  |                 |  |         z   y
		                        //    |  p2................|..p3        |  /
		                        //    | /                  | /          | /
		                        //    |/                   |/           |/
		                        //   p0-------------------p1            O----- x
		                        //
		                        //  p0 = (X-,Y-,Z-)
		                        //  p1 = (X+,Y-,Z-)
		                        //  p2 = (X-,Y+,Z-)
		                        //	p3 = (X+,Y+,Z-)
		                        //  p4 = (X-,Y-,Z+)
		                        //  p5 = (X+,Y-,Z+)
		                        //  p6 = (X-,Y+,Z+)
		                        //	p7 = (X+,Y+,Z+)
		                        //
		                        VisPoint[] visPoints = {
		                        		new VisPoint(minX,minY,minZ),  // p0
		                        		new VisPoint(maxX,minY,minZ),  // p1
		                        		new VisPoint(minX,maxY,minZ),  // p2
		                        		new VisPoint(maxX,maxY,minZ),  // p3
		                        		new VisPoint(minX,minY,maxZ),  // p4
		                        		new VisPoint(maxX,minY,maxZ),  // p5
		                        		new VisPoint(minX,maxY,maxZ),  // p6
		                        		new VisPoint(maxX,maxY,maxZ),  // p7
		                        };
		                        Integer[] indices = new Integer[8];
		                        for (int v=0;v<8;v++){
		                        	VisPoint visPoint = visPoints[v];
			                        String key = toStringKey(visPoint);
			                        Integer i = pointDict.get(key);
			                        if (i == null){
			                            pointDict.put(key,currPointIndex);
			                            i = currPointIndex;
			                            visMesh.addToPoints(visPoint);
			                            currPointIndex++;
			                        }
			                        indices[v] = i;
		                        }
		                        VisVoxel voxel = new VisVoxel(Arrays.asList(indices));
		                        voxel.setChomboVolumeIndex(new ChomboVolumeIndex(levelIndex,boxNumber,boxIndex,fraction));
		                      //  print('adding a cell at level '+str(currLevel.getLevel())+" from "+str(p1Coord)+" to "+str(p3Coord))
		                        visMesh.addToVisVoxels(voxel);
		                    }
		                }
	                }
	            }
	        }
	    }
	    cropVoxels(visMesh, chomboBoundaries,chomboCombinedVolumeMembraneDomain);
	    return visMesh;
	}

//	public VisDataset fromChomboDataset(ChomboDataset chomboDataset) {
//		VisDataset visDataset = new VisDataset();
//		for (ChomboDomain chomboDomain : chomboDataset.getDomains()){
//			ChomboMeshData chomboMeshData = chomboDomain.getChomboMeshData();
//			ChomboMeshMapping chomboMeshMapping = new ChomboMeshMapping();
//			VisMesh visMesh = chomboMeshMapping.fromMeshData(chomboMeshData, chomboDomain);
//			
//			boolean bMembrane = false;
//			VisMeshData visMeshVolumeData = new ChomboVisMeshData(chomboMeshData, visMesh, bMembrane);
//			VisDomain visVolumeDomain = new VisDomain(chomboDomain.getName(),visMesh,visMeshVolumeData);
//			visDataset.addDomain(visVolumeDomain);
//			
//			bMembrane = true;
//			VisMeshData visMeshMembraneData = new ChomboVisMeshData(chomboMeshData, visMesh, bMembrane);
//			VisDomain visMembraneDomain = new VisDomain(chomboDomain.getName()+"_MEMBRANE",visMesh,visMeshMembraneData);
//			visDataset.addDomain(visMembraneDomain);
//		}
//		
//		check(visDataset);
//		return visDataset;
//	}
//	
//	public void check(VisDataset visDataset){
//		VisMesh visMesh = visDataset.getDomains().get(0).getVisMesh();
//		for (VisPolyhedron visPolyhedron : visMesh.getPolyhedra()){
//			if (visPolyhedron instanceof VisVoxel){
//				VisVoxel visVoxel = (VisVoxel)visPolyhedron;
//				for (int p : visVoxel.getPointIndices()){
//					VisPoint vp = visMesh.getPoints().get(p);
//					if (vp==null){
//						throw new RuntimeException("couldn't find point "+p);
//					}
//				}
//			}else if (visPolyhedron instanceof VisIrregularPolyhedron){
//				VisIrregularPolyhedron visIrregularPolyhedron = (VisIrregularPolyhedron)visPolyhedron;
//				for (PolyhedronFace face : visIrregularPolyhedron.getFaces()){
//					for (int p : face.getVertices()){
//						VisPoint vp = visMesh.getPoints().get(p);
//						if (vp==null){
//							throw new RuntimeException("couldn't find point "+p);
//						}
//					}
//				}
//			}
//		}
//		LG.debug("ChomboMeshMapping:check() first mesh passed the point test");
//	}

	private static class VoxelPoint {
		final Integer p;
		final VisPoint vp;
		final Boolean bIncluded;
		VoxelPoint(Integer p, VisPoint vp, boolean bIncluded) {
			this.p = p;
			this.vp = vp;
			this.bIncluded = bIncluded;
		}
		public String toString(){
			return "VoxelPoint: p="+p+", vp="+vp+", included="+bIncluded;
		}
	}
	
	private static class ClippedVoxel {
		final VoxelFace f0;
		final VoxelFace f1;
		final VoxelFace f2;
		final VoxelFace f3;
		final VoxelFace f4;
		final VoxelFace f5;
		final ArrayList<VisSurfaceTriangle> surfaceTriangles = new ArrayList<VisSurfaceTriangle>();
		
		public ClippedVoxel(VoxelFace f0, VoxelFace f1, VoxelFace f2, VoxelFace f3, VoxelFace f4, VoxelFace f5) {
			super();
			this.f0 = f0;
			this.f1 = f1;
			this.f2 = f2;
			this.f3 = f3;
			this.f4 = f4;
			this.f5 = f5;
		}

		public VisSurfaceTriangle getSurfaceTriangle(Face face) {
			for (VisSurfaceTriangle tri : surfaceTriangles){
				if (tri.getFace() == face){  // enum compare
					return tri;
				}
			}
			return null;
		}
	}
	
	private static class VoxelFace {
		final Face face;
		final VoxelPoint p0;
		final VoxelPoint p1;
		final VoxelPoint p2;
		final VoxelPoint p3;
		
		public VoxelFace(Face face, VoxelPoint p0, VoxelPoint p1, VoxelPoint p2, VoxelPoint p3) {
			super();
			this.face = face;
			this.p0 = p0;
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
		}
	}

	private boolean between(double d, double dLo, double dHi)
	{
		return d >= dLo && d <= dHi || Math.abs(d - dLo) < 1e-12 || Math.abs(d - dHi) < 1e-12;
	}
	
	private boolean inLoHi(VisPoint p, VisPoint pLo, VisPoint pHi)
	{
		return between(p.x, pLo.x, pHi.x)
				&& between(p.y, pLo.y, pHi.y)
				&& between(p.z, pLo.z, pHi.z)
				;
	}
	
	private void cropVoxels(VisMesh visMesh, ChomboBoundaries chomboBoundaries, ChomboCombinedVolumeMembraneDomain chomboCombinedVolumeMembraneDomain){
		if (visMesh.getDimension()!=3){
			throw new RuntimeException("expecting 3D mesh");
		}
		List<VisVoxel> origVoxelList = visMesh.getVisVoxels();
		ArrayList<VisVoxel> newVoxelList = new ArrayList<VisVoxel>();
		List<VisPoint> points = visMesh.getPoints();
		List<VisSurfaceTriangle> triangles = visMesh.getSurfaceTriangles();
		for (VisVoxel visVoxel : origVoxelList){
			List<Integer> polyhedronPointIndices = visVoxel.getPointIndices();
			if (visVoxel.getChomboVolumeIndex().getFraction() < 1.0){
				
				int p0 = polyhedronPointIndices.get(0);
				int p1 = polyhedronPointIndices.get(1);
				int p2 = polyhedronPointIndices.get(2);
				int p3 = polyhedronPointIndices.get(3);
				int p4 = polyhedronPointIndices.get(4);
				int p5 = polyhedronPointIndices.get(5);
				int p6 = polyhedronPointIndices.get(6);
				int p7 = polyhedronPointIndices.get(7);
				
				VisPoint vp0 = points.get(p0);
				VisPoint vp1 = points.get(p1);
				VisPoint vp2 = points.get(p2);
				VisPoint vp3 = points.get(p3);
				VisPoint vp4 = points.get(p4);
				VisPoint vp5 = points.get(p5);
				VisPoint vp6 = points.get(p6);
				VisPoint vp7 = points.get(p7);
				
				ArrayList<VisSurfaceTriangle> intersectingTriangles = new ArrayList<VisSurfaceTriangle>();
				for (VisSurfaceTriangle triangle : triangles){
					boolean bInRange = true;
					for (int pi = 0; pi < 3; pi ++)
					{
						VisPoint tp = points.get(triangle.getPointIndices().get(pi));
						if (!inLoHi(tp, vp0, vp7)) {
							bInRange = false;
						}
					}
					if (bInRange)
					{
						intersectingTriangles.add(triangle);
					}
				}
				if (intersectingTriangles.size()==0){
					LG.info("fraction<1.0 but found no triangles");
					newVoxelList.add(visVoxel);
					continue;
				}

      //       p6-------------------p7
      //      /|                   /|
      //     / |                  / |
      //   p4-------------------p5  |
      //    |  |                 |  |      face number         coordinates
      //    |  |                 |  |
      //    |  |                 |  |         5   3            z   y
      //    |  p2................|..p3        |  /             |  /
      //    | /                  | /          | /              | /
      //    |/                   |/           |/               |/
      //   p0-------------------p1       0 ---'---- 1          '----- x
      //                                     /|
      //								                    / |
      //                                   2  4

				BorderCellInfo borderCellInfo = chomboBoundaries.getMeshMetrics().getBorderCellInfo(intersectingTriangles.get(0).getChomboSurfaceIndex().getIndex());
				//
				// have o flip the inside/outside if domain ordinal is > 0 ... note that "^" is the exclusive or ... to flip a bit
				//
				VoxelPoint[] v = new VoxelPoint[8];
				for (int i = 0; i < 8; ++ i)
				{
					int p = polyhedronPointIndices.get(i);
					VisPoint vp = points.get(p);
					v[i] = new VoxelPoint(p,vp,chomboCombinedVolumeMembraneDomain.shouldIncludeVertex(borderCellInfo.isVertexInPhase1(i)));
				}
				// choosing an arbitrary face (A,B,C,D) see below
				//
				//   pA   pB
				//
				//   pD   pC
				//

				// face 0 (X-)
				VoxelFace face0 = new VoxelFace(Face.Xm, v[0], v[4], v[6], v[2]);
				// face 1 (X+)
				VoxelFace face1 = new VoxelFace(Face.Xp, v[1], v[3], v[7], v[5]);
				// face 2 (Y-)
				VoxelFace face2 = new VoxelFace(Face.Ym, v[0], v[1], v[5], v[4]);
				// face 3 (Y+)
				VoxelFace face3 = new VoxelFace(Face.Yp, v[2], v[3], v[7], v[6]);
				// face 4 (Z-)
				VoxelFace face4 = new VoxelFace(Face.Zm, v[0], v[2], v[3], v[1]);
				// face 5 (Z+)
				VoxelFace face5 = new VoxelFace(Face.Zp, v[4], v[5], v[7], v[6]);

				ClippedVoxel clippedVoxel = new ClippedVoxel(face0,face1,face2,face3,face4,face5);
				clippedVoxel.surfaceTriangles.addAll(intersectingTriangles);
				
				VisIrregularPolyhedron clippedPolyhedron = createClippedPolyhedron(clippedVoxel, visMesh, visVoxel);
//VisIrregularPolyhedron clippedPolyhedron = new VisIrregularPolyhedron(visVoxel.getLevel(),visVoxel.getBoxNumber(),visVoxel.getBoxIndex(),visVoxel.getFraction());
//clippedPolyhedron.addFace(new PolyhedronFace(new int[] { p0, p1, p4} ));
//clippedPolyhedron.addFace(new PolyhedronFace(new int[] { p0, p2, p1} ));
//clippedPolyhedron.addFace(new PolyhedronFace(new int[] { p0, p4, p2} ));
//clippedPolyhedron.addFace(new PolyhedronFace(new int[] { p2, p4, p1} ));
				
				visMesh.addToIrregularPolyhedra(clippedPolyhedron);
//					VisTetrahedron[] delaunayTets = VtkGridUtils.createTetrahedra(clippedPolyhedron, visMesh);
//					for (VisTetrahedron tet : delaunayTets){
//						newPolyhedraList.add(tet);
//					}
			}else{ 
				// fraction >= 1.0
				newVoxelList.add(visVoxel);
			}
		} // for loop (orig polyhedra)
		visMesh.getVisVoxels().clear();
		visMesh.getVisVoxels().addAll(newVoxelList);
	}
	
	private VisIrregularPolyhedron createClippedPolyhedron(ClippedVoxel clippedVoxel, VisMesh visMesh, VisVoxel oldVoxel){
		VisIrregularPolyhedron visIrregularPolyhedron = new VisIrregularPolyhedron();
		visIrregularPolyhedron.setChomboVolumeIndex(new ChomboVolumeIndex(oldVoxel.getChomboVolumeIndex()));
		
		//
		// add triangles
		//
		for (VisSurfaceTriangle triangle : clippedVoxel.surfaceTriangles){
			List<Integer> triangleIndices = triangle.getPointIndices();
			PolyhedronFace polyhedronFace = new PolyhedronFace(triangleIndices);
			visIrregularPolyhedron.addToPolyhedronFaces(polyhedronFace);
		}
		
		VoxelFace[] voxelFaces = new VoxelFace[] { 
				clippedVoxel.f0, clippedVoxel.f1, clippedVoxel.f2, 
				clippedVoxel.f3, clippedVoxel.f4, clippedVoxel.f5 };
		for (VoxelFace voxelFace : voxelFaces){
			
			VisSurfaceTriangle triangleForThisFace = clippedVoxel.getSurfaceTriangle(voxelFace.face);
			List<Integer> triangleIndices = null;
			VisPoint[] trianglePoints = null;
			if (triangleForThisFace!=null){
				triangleIndices = triangleForThisFace.getPointIndices();
				trianglePoints = new VisPoint[] { visMesh.getPoints().get(triangleIndices.get(0)),  visMesh.getPoints().get(triangleIndices.get(1)),  visMesh.getPoints().get(triangleIndices.get(2)) };
			}

			ArrayList<Integer> indices = new ArrayList<Integer>();
			
			if (voxelFace.p0.bIncluded){
				indices.add(voxelFace.p0.p);
			}
			if (triangleIndices!=null){
				for (int i=0;i<3;i++){
					if (isColinear(voxelFace.p0.vp, trianglePoints[i], voxelFace.p1.vp)){
						indices.add(triangleIndices.get(i));
					}
				}
			}
			if (voxelFace.p1.bIncluded){
				indices.add(voxelFace.p1.p);
			}
			if (triangleIndices!=null){
				for (int i=0;i<3;i++){
					if (isColinear(voxelFace.p1.vp, trianglePoints[i], voxelFace.p2.vp)){
						indices.add(triangleIndices.get(i));
					}
				}
			}
			if (voxelFace.p2.bIncluded){
				indices.add(voxelFace.p2.p);
			}
			if (triangleIndices!=null){
				for (int i=0;i<3;i++){
					if (isColinear(voxelFace.p2.vp, trianglePoints[i], voxelFace.p3.vp)){
						indices.add(triangleIndices.get(i));
					}
				}
			}
			if (voxelFace.p3.bIncluded){
				indices.add(voxelFace.p3.p);
			}
			if (triangleIndices!=null){
				for (int i=0;i<3;i++){
					if (isColinear(voxelFace.p3.vp, trianglePoints[i], voxelFace.p0.vp)){
						indices.add(triangleIndices.get(i));
					}
				}
			}
//indices.add(voxelFace.p0.p);
//indices.add(voxelFace.p1.p);
//indices.add(voxelFace.p2.p);
//indices.add(voxelFace.p3.p);
			if (indices.size()>=3){
				ArrayList<Integer> indexArray = new ArrayList<Integer>(indices);
				PolyhedronFace polyFace = new PolyhedronFace(indexArray);
				visIrregularPolyhedron.addToPolyhedronFaces(polyFace);
			}
		}		
		return visIrregularPolyhedron;
		
	}
	
	private boolean isColinear(VisPoint vp1, VisPoint vp2, VisPoint vp3){
		int sameCoordCount = 0;
		if (vp1.x == vp2.x && vp1.x == vp3.x){
			sameCoordCount++;
		}
		if (vp1.y == vp2.y && vp1.y == vp3.y){
			sameCoordCount++;
		}
		if (vp1.z == vp2.z && vp1.z == vp3.z){
			sameCoordCount++;
		}
		return sameCoordCount==2;
	}
	
	private void cropQuads(VisMesh visMesh){
		if (visMesh.getDimension()!=2){
			throw new RuntimeException("expecting 2D mesh");
		}
		List<VisPolygon> polygons = visMesh.getPolygons();
		List<VisPoint> points = visMesh.getPoints();
		List<VisLine> lines = visMesh.getVisLines();
		for (VisPolygon polygon : polygons){
			List<Integer> polygonPointIndices = polygon.getPointIndices();
			double fraction = polygon.getChomboVolumeIndex().getFraction();
			if (fraction < 1.0 && polygonPointIndices.size() == 4){
				
				int p0 = polygonPointIndices.get(0);
				int p1 = polygonPointIndices.get(1);
				int p2 = polygonPointIndices.get(2);
				int p3 = polygonPointIndices.get(3);
				
				VisPoint point0 = points.get(p0);
				VisPoint point1 = points.get(p1);
				VisPoint point2 = points.get(p2);
				VisPoint point3 = points.get(p3);
				
				double p0x = point0.x;
				double p0y = point0.y;
				double p1x = point1.x;
				double p1y = point1.y;
				double p2x = point2.x;
				double p2y = point2.y;
				double p3x = point3.x;
				double p3y = point3.y;
				
				double pLowX = p0x;
				double pLowY = p0y;
				double pHiX = p2x;
				double pHiY = p2y;
				
				for (VisLine segment : lines){
					VisPoint segpoint1 = points.get(segment.getP1());
					double s1x = segpoint1.x;
					double s1y = segpoint1.y;
					
					if (s1x <= pHiX && s1x >= pLowX && s1y <= pHiY && s1y >= pLowY) {
	
						VisPoint segpoint2 = points.get(segment.getP2());
						double s2x = segpoint2.x;
						double s2y = segpoint2.y;
	
						if (s2x <= pHiX && s2x >= pLowX && s2y <= pHiY && s2y >= pLowY) {
							// found the segment???
							// if volfract > 0.5 ... voxel center is in new polygon
							//
							// lets first handle the case where it cuts off a triangle - then when it splits
							// it horizontally or vertically
							//
	
							// case 1: (remove point zero and replace with segment)
							//
							//    0  s1  3         0   s1            s1  3
							//    s2          =>   s2       or    s2
							//    1      2                        1      2
							//
							if (p0x == s1x && p0y == s2y) {
							    if (fraction > 0.5) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP2(), segment.getP1(), p1, p2, p3} ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP2(), p0, segment.getP1() } ));
							    }
							} else if (p0x == s2x && p0y == s1y) {
							    if (fraction > 0.5) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP1(), segment.getP2(), p1, p2, p3 } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP1(),p0,segment.getP2() } ));
							    }
							// case 2) { (remove point one && replace with segment
							//
							//    0      3                         0      3
							//    s1          =>  s1        or     s1
							//    1  s2  2        1  s2               s2  2
							//
							} else if (p1x == s1x && p1y == s2y) {
							    if (fraction > 0.5) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { p0,segment.getP1(),segment.getP2(),p2,p3 } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP1(),p1,segment.getP2() } ));
							    }
							} else if (p1x == s2x && p1y == s1y) {
							    if (fraction > 0.5) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { p0,segment.getP2(),segment.getP1(),p2,p3 } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP2(),p1,segment.getP1() } ));
							    }
							// case 3) { (remove point two && replace with segment
							//
							//    0      3                           0      3
							//           s2   =>         s2   or            s2
							//    1  s1  2           s1  2           1  s1   
							//
							} else if (p2x == s1x && p2y == s2y) {
							    if (fraction > 0.5) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { p0,p1,segment.getP2(),segment.getP1(),p3 } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP2(),p2,segment.getP1() } ));
							    }
							} else if (p2x == s2x && p2y == s1y) {
							    if (fraction > 0.5) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { p0,p1,segment.getP1(),segment.getP2(),p3 } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP1(),p2,segment.getP2() } ));
							    }
							// case 4) { (remove point three && replace with segment
							//
							//    0  s2  3           s2  3           0  s2   
							//           s1   =>         s1   or            s1
							//    1      2                           1      2
							//
							} else if (p3x == s1x && p3y == s2y) {
							    if (fraction > 0.5) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { p0,p1,p2,segment.getP1(),segment.getP2() } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP1(),p3,segment.getP2() } ));
							    }
							} else if (p3x == s2x && p3y == s1y) {
							    if (fraction > 0.5) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { p0,p1,p2,segment.getP2(),segment.getP1() } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP2(),p3,segment.getP1() } ));
							    }
							// case 5) { (remove points 0 && 1 verticle cut)
							//
							//    0  s1  3        0  s1                 s1  3
							//               =>               or    
							//    1  s2  2        1  s2                 s2  2
							//
							} else if (p0y == s1y && p1y == s2y) {
							    boolean bigleft = ((s1x-p0x)+(s2x-p0x) > p3x-p0x);
							    if ((fraction > 0.5 && bigleft) || (fraction <= 0.5 && !bigleft)) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { p0,p1,segment.getP2(),segment.getP1() } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP1(),segment.getP2(),p2,p3 } ));
							    }
							}else if (p0y == s2y && p1y == s1y) {
							    boolean bigleft = ((s1x-p0x)+(s2x-p0x) > p3x-p0x);
							    if ((fraction > 0.5 && bigleft) || (fraction <= 0.5 && !bigleft)) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { p0,p1,segment.getP1(),segment.getP2() } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP2(),segment.getP1(),p2,p3 } ));
							    }
							// case 6) { (remove points 0 && 1 horizontal cut)
							//
							//    0      3        0      3                   
							//    s1     s2  =>   s1     s2   or     s1     s2
							//    1      2                           1      2
							//
							} else if (p0x == s1x && p3x == s2x) {
							    boolean bigtop = ((s1y-p0y)+(s2y-p0y) > p1y-p0y);
							    if ((fraction > 0.5 && bigtop) || (fraction <= 0.5 && !bigtop)) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { p0,segment.getP1(),segment.getP2(),p3 } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP2(),segment.getP1(),p1,p2 } ));
							    }
							} else if (p0x == s2x && p3x == s1x) {
							    boolean bigtop = ((s1y-p0y)+(s2y-p0y) > p1y-p0y);
							    if ((fraction > 0.5 && bigtop) || (fraction <= 0.5 && !bigtop)) {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { p0,segment.getP2(),segment.getP1(),p3 } ));
							    } else {
							    	polygon.setPointIndices(Arrays.asList( new Integer[] { segment.getP1(),segment.getP2(),p1,p2 } ));
							    }
							} else {
							    LG.warn("found the segment for this polygon, don't know how to crop this one yet");
							}
						}
					}
				}
			}
		}
	                
		
	}

}
