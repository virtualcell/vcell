package org.vcell.vis.mapping.vcell;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.vcell.util.ISize;
import org.vcell.vis.core.Box3D;
import org.vcell.vis.vcell.CartesianMesh;
import org.vcell.vis.vcell.MembraneElement;
import org.vcell.vis.vismesh.thrift.FiniteVolumeIndex;
import org.vcell.vis.vismesh.thrift.Vect3D;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPoint;
import org.vcell.vis.vismesh.thrift.VisPolygon;
import org.vcell.vis.vismesh.thrift.VisVoxel;

public class CartesianMeshMapping {
	
	public CartesianMeshMapping(){
		
	}

	public VisMesh fromMeshData(CartesianMesh cartesianMesh, String domainName, boolean bVolume){
		int dimension = cartesianMesh.getDimension();
		if (dimension==2 && bVolume){
			return fromMesh2DVolume(cartesianMesh, domainName);
		}else if (dimension==3 && bVolume){
			return fromMesh3DVolume(cartesianMesh, domainName);
		}else if (dimension==3 && !bVolume){
			return fromMesh3DMembrane(cartesianMesh, domainName);
		}else{
			throw new RuntimeException("unsupported: mesh dimension = "+dimension+", volumeDomain ="+bVolume);
		}
	}
	
	public String toStringKey(VisPoint visPoint){
		return toStringKey(visPoint, 8);
	}
	
	public String toStringKey(VisPoint visPoint, int precision){
		String formatString = "%."+precision+"f";
		return "("+String.format(formatString,visPoint.x)+","+String.format(formatString,visPoint.y)+","+String.format(formatString,visPoint.z)+")";
	}

	private VisMesh fromMesh2DVolume(CartesianMesh cartesianMesh, String domainName){
	    ISize size = cartesianMesh.getSize();
	    int numX = size.getX();
	    int numY = size.getY();
	    int numZ = size.getZ();
	    int dimension = 2;
	   
	    int z = 0;
	    Vect3D origin = new Vect3D(cartesianMesh.getOrigin().x,cartesianMesh.getOrigin().y,cartesianMesh.getOrigin().z);
	    Vect3D extent = new Vect3D(cartesianMesh.getExtent().x,cartesianMesh.getExtent().y,cartesianMesh.getExtent().z);
	    VisMesh visMesh = new VisMesh(dimension,origin, extent); // invoke VisMesh() constructor
	    int currPointIndex = 0;
	    HashMap<String,Integer> pointDict = new HashMap<String,Integer>();
	    List<Integer> volumeRegionIDs = cartesianMesh.getVolumeRegionIDs(domainName);
	    
	    int volumeIndex = 0;
	    for (int k=0;k<numZ;k++){
		    for (int j=0;j<numY;j++){
			    for (int i=0;i<numX;i++){
	    	    	int regionIndex = cartesianMesh.getVolumeRegionIndex(volumeIndex);
	    	    	if (volumeRegionIDs.contains(regionIndex)){
		    	    	Box3D element = cartesianMesh.getVolumeElementBox(i,j,k);
	                    double minX = element.x_lo;
	                    double maxX = element.x_hi;
	                    double minY = element.y_lo;
	                    double maxY = element.y_hi;
	
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
	        
	                    VisPolygon quad = new VisPolygon(Arrays.asList(new Integer[] { i1,i2,i3,i4 }));
	                    quad.setFiniteVolumeIndex(new FiniteVolumeIndex(volumeIndex,cartesianMesh.getVolumeRegionIndex(volumeIndex)));
	                  //  print('adding a cell at level '+str(currLevel.getLevel())+" from "+str(p1Coord)+" to "+str(p3Coord))
	                    visMesh.addToPolygons(quad);
		            } // end if
	    		    volumeIndex++;
			    } // end i
	        } // end j
	    }
	    return visMesh;
	}

	private VisMesh fromMesh3DMembrane(CartesianMesh cartesianMesh, String domainName){
	    ISize size = cartesianMesh.getSize();
	    int numX = size.getX();
	    int numY = size.getY();
	    int dimension = 3;
	   
	    Vect3D origin = new Vect3D(cartesianMesh.getOrigin().x,cartesianMesh.getOrigin().y,cartesianMesh.getOrigin().z);
	    Vect3D extent = new Vect3D(cartesianMesh.getExtent().x,cartesianMesh.getExtent().y,cartesianMesh.getExtent().z);
	    VisMesh visMesh = new VisMesh(dimension, origin, extent); // invoke VisMesh() constructor
	    int currPointIndex = 0;
	    HashMap<String,Integer> pointDict = new HashMap<String,Integer>();
	    List<MembraneElement> membraneElements = cartesianMesh.getMembraneElements(domainName);
	    
	    for (MembraneElement membraneElement : membraneElements){
    		// inside
    		int insideVolumeIndex = membraneElement.getInsideVolumeIndex();
			int insideI = insideVolumeIndex % numX;
			int insideJ = (insideVolumeIndex % (numX*numY))/numX;
    		int insideK = insideVolumeIndex / (numX*numY);
    		Box3D insideBox = cartesianMesh.getVolumeElementBox(insideI, insideJ, insideK);
    		// outside
    		int outsideVolumeIndex = membraneElement.getOutsideVolumeIndex();
			int outsideI = outsideVolumeIndex % numX;
    		int outsideJ = (outsideVolumeIndex % (numX*numY))/numX;
    		int outsideK = outsideVolumeIndex / (numX*numY);
    		
            VisPoint p1Coord;
            VisPoint p2Coord;
            VisPoint p3Coord;
            VisPoint p4Coord;

            if (insideI == outsideI + 1){
            	// x-   z cross y
            	double x = insideBox.x_lo; 
                p1Coord = new VisPoint(x,insideBox.y_lo,insideBox.z_lo);
                p2Coord = new VisPoint(x,insideBox.y_lo,insideBox.z_hi);
                p3Coord = new VisPoint(x,insideBox.y_hi,insideBox.z_hi);
                p4Coord = new VisPoint(x,insideBox.y_hi,insideBox.z_lo);
     		}else if (outsideI == insideI + 1){
            	// x+   y cross z
            	double x = insideBox.x_hi; 
                p1Coord = new VisPoint(x,insideBox.y_lo,insideBox.z_lo);
                p2Coord = new VisPoint(x,insideBox.y_hi,insideBox.z_lo);
                p3Coord = new VisPoint(x,insideBox.y_hi,insideBox.z_hi);
                p4Coord = new VisPoint(x,insideBox.y_lo,insideBox.z_hi);
    		}else if (insideJ == outsideJ + 1){
            	// y-   x cross z
            	double y = insideBox.y_lo;
                p1Coord = new VisPoint(insideBox.x_lo,y,insideBox.z_lo);
                p2Coord = new VisPoint(insideBox.x_hi,y,insideBox.z_lo);
                p3Coord = new VisPoint(insideBox.x_hi,y,insideBox.z_hi);
                p4Coord = new VisPoint(insideBox.x_lo,y,insideBox.z_hi);
    		}else if (outsideJ == insideJ + 1){
            	// y+   z cross x
            	double y = insideBox.y_hi; 
                p1Coord = new VisPoint(insideBox.x_lo,y,insideBox.z_lo);
                p2Coord = new VisPoint(insideBox.x_lo,y,insideBox.z_hi);
                p3Coord = new VisPoint(insideBox.x_hi,y,insideBox.z_hi);
                p4Coord = new VisPoint(insideBox.x_hi,y,insideBox.z_lo);
    		}else if (insideK == outsideK + 1){
            	// z-   y cross x
            	double z = insideBox.z_lo; 
                p1Coord = new VisPoint(insideBox.x_lo,insideBox.y_lo,z);
                p2Coord = new VisPoint(insideBox.x_lo,insideBox.y_hi,z);
                p3Coord = new VisPoint(insideBox.x_hi,insideBox.y_hi,z);
                p4Coord = new VisPoint(insideBox.x_hi,insideBox.y_lo,z);    
    		}else if (outsideK == insideK + 1){
            	// z+   x cross y
            	double z = insideBox.z_hi; 
                p1Coord = new VisPoint(insideBox.x_lo,insideBox.y_lo,z);
                p2Coord = new VisPoint(insideBox.x_hi,insideBox.y_lo,z);
                p3Coord = new VisPoint(insideBox.x_hi,insideBox.y_hi,z);
                p4Coord = new VisPoint(insideBox.x_lo,insideBox.y_hi,z);    
    		}else{
    			throw new RuntimeException("inside/outside volume indices not reconciled in membraneElement "+membraneElement.getMembraneIndex()+" in domain "+domainName);
    		}

            //
            // make sure vertices are added to model without duplicates and get the assigned identifier.
            //
            String p1Key = toStringKey(p1Coord);
            Integer i1 = pointDict.get(p1Key);
            if (i1 == null){
                pointDict.put(p1Key,currPointIndex);
                i1 = currPointIndex;
                visMesh.addToPoints(p1Coord);
                currPointIndex++;
            }

            String p2Key = toStringKey(p2Coord);
            Integer i2 = pointDict.get(p2Key);
            if (i2 == null){
                pointDict.put(p2Key,currPointIndex);
                i2 = currPointIndex;
                visMesh.addToPoints(p2Coord);
                currPointIndex++;
            }

            String p3Key = toStringKey(p3Coord);
            Integer i3 = pointDict.get(p3Key);
            if (i3 == null){
                pointDict.put(p3Key, currPointIndex);
                i3 = currPointIndex;
                visMesh.addToPoints(p3Coord);
                currPointIndex++;
            }

            String p4Key = toStringKey(p4Coord);
            Integer i4 = pointDict.get(p4Key);
            if (i4 == null){
                pointDict.put(p4Key,currPointIndex);
                i4 = currPointIndex;
                visMesh.addToPoints(p4Coord);
                currPointIndex++;
            }

            VisPolygon quad = new VisPolygon(Arrays.asList( new Integer[] { i1,i2,i3,i4 } ));
            quad.setFiniteVolumeIndex(new FiniteVolumeIndex(membraneElement.getMembraneIndex(),cartesianMesh.getMembraneRegionIndex(membraneElement.getMembraneIndex())));
          //  print('adding a cell at level '+str(currLevel.getLevel())+" from "+str(p1Coord)+" to "+str(p3Coord))
            visMesh.addToPolygons(quad);

	    }
	    return visMesh;
	}

	private VisMesh fromMesh3DVolume(CartesianMesh cartesianMesh, String domainName){
	    ISize size = cartesianMesh.getSize();
	    int numX = size.getX();
	    int numY = size.getY();
	    int numZ = size.getZ();
	    int dimension = 3;
	   
	    Vect3D origin = new Vect3D(cartesianMesh.getOrigin().x,cartesianMesh.getOrigin().y,cartesianMesh.getOrigin().z);
	    Vect3D extent = new Vect3D(cartesianMesh.getExtent().x,cartesianMesh.getExtent().y,cartesianMesh.getExtent().z);
	    VisMesh visMesh = new VisMesh(dimension, origin, extent); // invoke VisMesh() constructor
	    int currPointIndex = 0;
	    HashMap<String,Integer> pointDict = new HashMap<String,Integer>();
	    List<Integer> volumeRegionIDs = cartesianMesh.getVolumeRegionIDs(domainName);
	    
	    int volumeIndex = 0;
	    for (int k=0;k<numZ;k++){
		    for (int j=0;j<numY;j++){
			    for (int i=0;i<numX;i++){
	    	    	int regionIndex = cartesianMesh.getVolumeRegionIndex(volumeIndex);
	    	    	if (volumeRegionIDs.contains(regionIndex)){
		    	    	Box3D element = cartesianMesh.getVolumeElementBox(i,j,k);
	                    double minX = element.x_lo;
	                    double maxX = element.x_hi;
	                    double minY = element.y_lo;
	                    double maxY = element.y_hi;
	                    double minZ = element.z_lo;
	                    double maxZ = element.z_hi;
	
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
	                        Integer p = pointDict.get(key);
	                        if (p == null){
	                            pointDict.put(key,currPointIndex);
	                            p = currPointIndex;
	                            visMesh.addToPoints(visPoint);
	                            currPointIndex++;
	                        }
	                        indices[v] = p;
	                    }
	                    VisVoxel voxel = new VisVoxel(Arrays.asList( indices ));
	                    voxel.setFiniteVolumeIndex(new FiniteVolumeIndex(volumeIndex,cartesianMesh.getVolumeRegionIndex(volumeIndex)));
	                  //  print('adding a cell at level '+str(currLevel.getLevel())+" from "+str(p1Coord)+" to "+str(p3Coord))
	                    visMesh.addToVisVoxels(voxel);
	 	            } // end if
	    		    volumeIndex++;
		        } // end for i
		    } // end for j
	    } // end for k
	    return visMesh;
	}

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
//		System.out.println("ChomboMeshMapping:check() first mesh passed the point test");
//	}
	
}
