package org.vcell.vis.mapping.movingboundary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.vcell.util.ISize;
import org.vcell.vis.core.Box3D;
import org.vcell.vis.vcell.CartesianMesh;
import org.vcell.vis.vcell.MembraneElement;
import org.vcell.vis.vismesh.thrift.FiniteVolumeIndex;
import org.vcell.vis.vismesh.thrift.MovingBoundaryVolumeIndex;
import org.vcell.vis.vismesh.thrift.Vect3D;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vismesh.thrift.VisPoint;
import org.vcell.vis.vismesh.thrift.VisPolygon;
import org.vcell.vis.vismesh.thrift.VisVoxel;

import cbit.vcell.solvers.mb.MovingBoundaryReader;
import cbit.vcell.solvers.mb.Vect3Didx;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.Element;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.Element.Position;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.MeshInfo;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.Plane;
import cbit.vcell.solvers.mb.PointIndex;

public class MovingBoundaryMeshMapping {
	
	public MovingBoundaryMeshMapping(){
		
	}

	private String toStringKey(VisPoint visPoint){
		return toStringKey(visPoint, 8);
	}
	
	private String toStringKey(VisPoint visPoint, int precision){
		String formatString = "%."+precision+"f";
		return "("+String.format(formatString,visPoint.x)+","+String.format(formatString,visPoint.y)+","+String.format(formatString,visPoint.z)+")";
	}


	/**
	 * 
	 * @param reader
	 * @param domainType : INSIDE, OUTSIDE, MEMBRANE
	 * @return
	 */
	public enum DomainType {
		INSIDE,
		OUTSIDE,
		MEMBRANE
	}
	public VisMesh fromReader(MovingBoundaryReader reader, DomainType domainType, int timeIndex) {
	    MeshInfo meshInfo = reader.getMeshInfo();
		ISize size = new ISize(meshInfo.xinfo.number(),meshInfo.yinfo.number(),1);
		Plane plane = reader.getPlane(timeIndex);
	    int numX = size.getX();
	    int numY = size.getY();
	    int dimension = 2;
	   
	    Vect3D origin = new Vect3D(meshInfo.xinfo.start, meshInfo.yinfo.start, 0.0);
	    Vect3D extent = new Vect3D(meshInfo.xinfo.end-meshInfo.xinfo.start, meshInfo.yinfo.end-meshInfo.yinfo.start, 1.0);
	    VisMesh visMesh = new VisMesh(dimension,origin, extent); // invoke VisMesh() constructor
	    int currPointIndex = 0;
	    HashMap<String,Integer> pointDict = new HashMap<String,Integer>();
	    PointIndex pointIndex = reader.getPointIndex();
	    if (domainType == DomainType.INSIDE || domainType == DomainType.OUTSIDE){
		    int volumeIndex = 0;
		    for (int j=0;j<numY;j++){
		    	for (int i=0;i<numX;i++){
			    	Element mbElement = plane.get(i,j);
			    	if ((domainType == DomainType.INSIDE && (mbElement.position == Position.INSIDE || mbElement.position == Position.BOUNDARY)) ||
			    		(domainType == DomainType.OUTSIDE && mbElement.position == Position.OUTSIDE)){

			    		ArrayList<Integer> elementVisIndices = new ArrayList<Integer>();
			    		int[] elementPointIndices = mbElement.boundary();
			    		int numUniqueElementIndices = elementPointIndices.length-1; // first and last index is same
			    		for (int p=0; p < numUniqueElementIndices; p++){
			    			Vect3Didx point = pointIndex.lookup(elementPointIndices[p]);
		                    VisPoint visPoint = new VisPoint(point.x,point.y,point.z);
		                    String visPointKey = toStringKey(visPoint);
		                    Integer visPointIndex = pointDict.get(visPointKey);
		                    if (visPointIndex == null){
		                    	visPointIndex = currPointIndex++;
		                        pointDict.put(visPointKey,visPointIndex);
		                        visMesh.addToPoints(visPoint);
		                    }
		                    elementVisIndices.add(visPointIndex);
			    		}
	        
	                    VisPolygon polygon = new VisPolygon(elementVisIndices);
	                    polygon.setMovingBoundaryVolumeIndex(new MovingBoundaryVolumeIndex(volumeIndex));
	                    visMesh.addToPolygons(polygon);
	                    volumeIndex++;
		            } // end if
			    } // end i
	        } // end j
	    }else if (domainType == DomainType.MEMBRANE){
	    	throw new RuntimeException("DomainType MEMBRANE not yet implemented for moving boundary vtk processing");
	    }
	    return visMesh;
	}
	
}
