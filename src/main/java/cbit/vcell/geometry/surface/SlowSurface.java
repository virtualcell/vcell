/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.surface;

/**
 * Insert the type's description here.
 * Creation date: (6/28/2003 12:10:01 AM)
 * @author: John Wagner
 */
public class SlowSurface implements Surface {

	private int fieldInteriorRegionIndex = 0;
	private int fieldExteriorRegionIndex = 0;

	// masks are temporary and are used during algorithms.
	// the ray-tracer assigns interior/exterior masks to (1<<(2*N) and 1<<(2*N+1)) for surface N.
	private long interiorMask = 0;
	private long exteriorMask = 0;

	private java.util.Vector polygonList = new java.util.Vector();

	//Associates Polygon index with Node indexes (implicit reference to polygonList, references nodeList indexes in this polygon int[x]
	private java.util.Vector polyNodeIndexes = new java.util.Vector();
	
	//Contains unique reference to each node(vertex) in this surface
	private java.util.Vector nodeList = new java.util.Vector();
	
	//Associate edges with their common node (implicit ref to nodeList,references edgeList int[x])
	private java.util.Vector nodeEdgeList = new java.util.Vector();
	
	//Contains unique reference to each edge in this surface (int[2] nodeIndex1,nodeIndex2)
	private java.util.Vector edgeList = new java.util.Vector();
	private static final int EDGE_NODE1 = 0;
	private static final int EDGE_NODE2 = 1;
	
	//Associates face pairs of polygons with edge(implicit)  (int[2] polygonIndex1,polygonIndex2)
	private java.util.Vector faceList = new java.util.Vector();
	private static final int FACE_POLYGON1 = 0;
	private static final int FACE_POLYGON2 = 1;
/**
 * Boundary constructor comment.
 */
public SlowSurface(int argInteriorRegionIndex, int argExteriorRegionIndex) {
	this.fieldInteriorRegionIndex = argInteriorRegionIndex;
	this.fieldExteriorRegionIndex = argExteriorRegionIndex;
}
/**
 * Boundary constructor comment.
 */
public SlowSurface(int argInteriorRegionIndex, int argExteriorRegionIndex, Polygon polygon) {
	this(argInteriorRegionIndex,argExteriorRegionIndex);
	addPolygon(polygon);
}
public long getInteriorMask() {
	return interiorMask;
}
public long getExteriorMask() {
	return exteriorMask;
}
public void setInteriorMask(long mask) {
	this.interiorMask = mask;
}
public void setExteriorMask(long mask) {
	this.exteriorMask = mask;
}
/**
 * Boundary constructor comment.
 */
public void addPolygon(Polygon argPolygon) {

	//Do we already have this polygon
	for(int i = 0;i < polygonList.size();i+= 1){
		if(polygonList.elementAt(i).equals(argPolygon)){
			return;
		}
	}
		
	//Add new Nodes (Vertexes)
	//Keep track of their nodeList index so we can use later
	int[] nodeIndex = new int[argPolygon.getNodeCount()];
	for(int i = 0;i < argPolygon.getNodeCount();i+= 1){
		Node newNode = argPolygon.getNodes(i);
		boolean bFound = false;
		for(int j = 0; j < nodeList.size();j+= 1){
			if(nodeList.elementAt(j).equals(newNode)){
				bFound = true;
				nodeIndex[i] = j;
				break;
			}
		}
		if(!bFound){
			nodeIndex[i] = nodeList.size();
			nodeList.add(newNode);
			nodeEdgeList.add(new int[]{});
		}
	}

	
	//Add new Polygon
	polygonList.addElement(argPolygon);

	//Add new PolyNodes
	polyNodeIndexes.add(nodeIndex);

	
	//Add new Edges
	//Assume that Polygons return Nodes on consecutive edges
	//Keep track of their edgeIndex so we can use later
	int[] edgeIndex = new int[nodeIndex.length];
	for(int i = 0;i < nodeIndex.length;i+= 1){
		int newEdgeIndexBegin = nodeIndex[i];
		int newEdgeIndexEnd = nodeIndex[(i+1)%nodeIndex.length];
		boolean bFound = false;
		for(int j = 0; j < edgeList.size();j+= 1){
			int[] edge = (int[])edgeList.elementAt(j);
			if( (edge[EDGE_NODE1] == newEdgeIndexBegin && edge[EDGE_NODE2] == newEdgeIndexEnd) ||
				(edge[EDGE_NODE2] == newEdgeIndexBegin && edge[EDGE_NODE1] == newEdgeIndexEnd) ){
				bFound = true;
				edgeIndex[i] = j;
				break;
			}
		}
		if(!bFound){
			edgeIndex[i] = edgeList.size();
			edgeList.add(new int[] {newEdgeIndexBegin,newEdgeIndexEnd});
		}
		//-----Add Edges to nodeEdgeList
		boolean bNEFound = false;
		int[] nodeEdge1 = (int[])nodeEdgeList.elementAt(newEdgeIndexBegin);
		for(int j=0;j<nodeEdge1.length;j+= 1){
			if(edgeIndex[i] == nodeEdge1[j]){
				bNEFound = true;
				break;
			}
		}
		if(!bNEFound){
			int[] temp = new int[nodeEdge1.length+1];
			System.arraycopy(nodeEdge1,0,temp,1,nodeEdge1.length);
			temp[0] = edgeIndex[i];
			nodeEdgeList.setElementAt(temp,newEdgeIndexBegin);
		}
		//
		bNEFound = false;
		int[] nodeEdge2 = (int[])nodeEdgeList.elementAt(newEdgeIndexEnd);
		for(int j=0;j<nodeEdge2.length;j+= 1){
			if(edgeIndex[i] == nodeEdge2[j]){
				bNEFound = true;
				break;
			}
		}
		if(!bNEFound){
			int[] temp = new int[nodeEdge2.length+1];
			System.arraycopy(nodeEdge2,0,temp,1,nodeEdge2.length);
			temp[0] = edgeIndex[i];
			nodeEdgeList.setElementAt(temp,newEdgeIndexEnd);
		}
		//-----
	}	

	//Add new Faces
	for(int i=0;i < edgeIndex.length;i+= 1){
		int[] edge = (int[])edgeList.elementAt(edgeIndex[i]);
		int polygonIndexFirstFace = -1;
		int polygonIndexSecondFace = -1;
		//find polygons that have the 2 nodes on this edge
		for(int j =0;j< polyNodeIndexes.size();j+= 1){
			int[] polyNodesArr = (int[])polyNodeIndexes.elementAt(j);
			int sharedEdgeNodeCount = 0;
			for(int k=0;k<polyNodesArr.length;k+= 1){
				if(polyNodesArr[k] == edge[EDGE_NODE1] || polyNodesArr[k] == edge[EDGE_NODE2]){
					sharedEdgeNodeCount+= 1;
				}
			}
			if(sharedEdgeNodeCount == 2){
				if(polygonIndexFirstFace == -1){
					polygonIndexFirstFace = j;
				}else if(polygonIndexSecondFace == -1){
					polygonIndexSecondFace = j;
					////Find if Face entry already exists
					//boolean bFoundFace = false;
					//for(int k = 0;k < faceList.size();k+= 1){
						//int[] face = (int[])faceList.elementAt(k);
						//if( (face[FACE_POLYGON1] == polygonIndexFirstFace && face[FACE_POLYGON2] == polygonIndexSecondFace) ||
							//(face[FACE_POLYGON2] == polygonIndexSecondFace && face[FACE_POLYGON1] == polygonIndexFirstFace)){
							//bFoundFace = true;
							//break;
						//}
					//}
					//if(!bFoundFace){//Add new Face
						//if(faceList.size() >= edgeIndex[i]){
							//faceList.setSize(edgeIndex[i]+1);
						//}
						//faceList.setElementAt(new int[] {polygonIndexFirstFace,polygonIndexSecondFace},edgeIndex[i]);
						////faceList.add(new int[] {edgeIndex[i],polygonIndexFirstFace,polygonIndexSecondFace});
					//}
				}else{
					throw new RuntimeException("Surface.addPolygon: More than 2 Faces found for an edge");
				}
				//
				if(edgeIndex[i] >= faceList.size()){
					faceList.setSize(edgeIndex[i]+1);
				}
				faceList.setElementAt(new int[] {polygonIndexFirstFace,polygonIndexSecondFace},edgeIndex[i]);
			}else if(sharedEdgeNodeCount > 2){
				throw new RuntimeException("Surface.addPolygon: More than 2 Nodes found for an edge");
			}
		}
	}
}
/**
 * Boundary constructor comment.
 */
public void addSurface(Surface surface) {
	for (int i = 0; i < surface.getPolygonCount(); i++) {
		addPolygon(surface.getPolygons(i));
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:08:34 PM)
 * @return cbit.vcell.geometry.surface.Surface
 */
public SlowSurface createCentroidSurface() {

	return createCentroidSurface2();
	
	//SlowSurface centroidSurface = null;
	//Node[] centroids = null;

	//boolean[] bAnchorForbiddenArr = new boolean[getPolygonCount()];
	//java.util.Arrays.fill(bAnchorForbiddenArr,false);
	
	//for(int i=0;i<polyNodeIndexes.size();i+= 1){
		//if(bAnchorForbiddenArr[i]){
			//continue;
		//}
		//int[] polygonNIArr = (int[])polyNodeIndexes.elementAt(i);
		//for(int j=0;j<polygonNIArr.length;j+= 1){
			//int neighborBeginNI = polygonNIArr[j];
			//int neighborCornerNI = polygonNIArr[(j+1)%polygonNIArr.length];
			//int neighborEndNI = polygonNIArr[(j+2)%polygonNIArr.length];
			//int polygonBeginPI = -1;
			//int[] polygonCornerPIArr = null;
			//int polygonEndPI = -1;
			////find edge neighbors
			//for(int k=0;k<edgeList.size();k+= 1){
				//int[] edge = (int[])edgeList.elementAt(k);
				//boolean bNeighborFlag;
				//if((edge[EDGE_NODE1] == neighborBeginNI && edge[EDGE_NODE2] == neighborCornerNI) ||
					//(edge[EDGE_NODE2] == neighborBeginNI && edge[EDGE_NODE1] == neighborCornerNI)){
						//bNeighborFlag = true;
				//}else if((edge[EDGE_NODE1] == neighborEndNI && edge[EDGE_NODE2] == neighborCornerNI) ||
					//(edge[EDGE_NODE2] == neighborEndNI && edge[EDGE_NODE1] == neighborCornerNI)){
						//bNeighborFlag = false;
				//}else{
					//continue;
				//}
				//for(int l=0;l<faceList.size();l+= 1){
					//int[] face = (int[])faceList.elementAt(l);
					//if(l == k){
						//if(face[FACE_POLYGON1] == i || face[FACE_POLYGON2] == i){
							//if(bNeighborFlag){
								//polygonBeginPI = (face[FACE_POLYGON1] == i?face[FACE_POLYGON2]:face[FACE_POLYGON1]);
							//}else{
								//polygonEndPI = (face[FACE_POLYGON1] == i?face[FACE_POLYGON2]:face[FACE_POLYGON1]);
							//}
						//}else{
							//throw new RuntimeException("Unexpected face finding");
						//}
					//}
				//}
			//}
			////Find corner neighbor
			//for(int k=0;k<polyNodeIndexes.size();k+= 1){
				//if(k == i || k == polygonBeginPI || k == polygonEndPI){
					//continue;
				//}
				//int[] pniArr = (int[])polyNodeIndexes.elementAt(k);
				//for(int l=0;l<pniArr.length;l+= 1){
					//if(pniArr[l] == neighborCornerNI){
						//if(polygonCornerPIArr == null){
							//polygonCornerPIArr = new int[1];
						//}else{
							//int[] temp = new int[polygonCornerPIArr.length+1];
							//System.arraycopy(polygonCornerPIArr,0,temp,0,polygonCornerPIArr.length);
							//polygonCornerPIArr = temp;
						//}
						//polygonCornerPIArr[polygonCornerPIArr.length-1] = k;
						////if(polygonCornerPI == -1){
							////polygonCornerPI = k;
						////}else{
							////throw new RuntimeException("Finding 2 Corner neighbors not implemented");
						////}
					//}
				//}
			//}

			//int[] orderedPolygonCornerPIArr = null;
			////boolean bSkip = false;
			//////Check missing neighbors (on an edge)
			////if(polygonBeginPI == -1 || polygonCornerPIArr == null || polygonEndPI == -1){
				////bSkip = true;
			////}
			////if(!bSkip){
			//if(polygonCornerPIArr != null){
				//////Check neighbor not anchor so we don't duplicate faces
				////if(bAnchorArr[polygonBeginPI] || bAnchorArr[polygonEndPI]){
					////bSkip = true;
				////}
				////else if(polygonCornerPIArr.length > 1){
					////Put corner neighbors in order from polygonBeginPI to polygonEndPI
					//if(polygonCornerPIArr.length == 1){
						//orderedPolygonCornerPIArr = polygonCornerPIArr;
					//}else{
						//int sharedFacePolygonIndex = polygonBeginPI;
						//orderedPolygonCornerPIArr = new int[polygonCornerPIArr.length];
						//int orderCount = 0;
						//boolean bBreak = false;
						//while(orderCount < polygonCornerPIArr.length){
							//for(int k=0;k<polygonCornerPIArr.length;k+= 1){
								//for(int l=0;l<faceList.size();l+= 1){
									//int[] face = (int[])faceList.elementAt(l);
									//if((face[FACE_POLYGON1] == sharedFacePolygonIndex &&  face[FACE_POLYGON2] == polygonCornerPIArr[k]) ||
										//(face[FACE_POLYGON2] == sharedFacePolygonIndex &&  face[FACE_POLYGON1] == polygonCornerPIArr[k])){
											//orderedPolygonCornerPIArr[orderCount] = polygonCornerPIArr[k];
											//sharedFacePolygonIndex = orderedPolygonCornerPIArr[orderCount];
											//orderCount+= 1;
											//bBreak = true;
									//}
								//}
								//if(bBreak){bBreak = false;break;}
							//}
						//}
					//}
				////}
			//}

			//if((polygonBeginPI != -1 && polygonEndPI != -1) || (orderedPolygonCornerPIArr != null)){
				
				////bAnchorArr[i] = true;
				
				////Find Centroids
				//if(centroids == null){
					//centroids = new Node[polygonList.size()];
				//}
				////int[] neighbors = new int[3+polygonCornerPIArr.length];
				////Add verts in order
				//int[] vNeighbors = new int[1 + (polygonBeginPI != -1?1:0) + (polygonEndPI != -1?1:0) + (orderedPolygonCornerPIArr != null?orderedPolygonCornerPIArr.length:0)];
				//int vertexCounter = 0;
				//vNeighbors[vertexCounter] = i;
				//vertexCounter+= 1;
				//if(polygonBeginPI != -1){
					//vNeighbors[vertexCounter] = polygonBeginPI;
					//vertexCounter+= 1;
				//}
				//if(orderedPolygonCornerPIArr != null){
					//System.arraycopy(orderedPolygonCornerPIArr,0,vNeighbors,vertexCounter,orderedPolygonCornerPIArr.length);
					//vertexCounter+= orderedPolygonCornerPIArr.length;
				//}
				//if(polygonEndPI != -1){
					//vNeighbors[vertexCounter] = polygonEndPI;
					//vertexCounter+= 1;
				//}
				////neighbors[1] = polygonBeginPI;
				////System.arraycopy(polygonCornerPIArr,0,neighbors,2,polygonCornerPIArr.length);
				////neighbors[2+polygonCornerPIArr.length] = polygonEndPI;
				//////{i,polygonBeginPI,polygonCornerPI,polygonEndPI};
				//for(int k=0;k<vNeighbors.length;k+= 1){
					//if(vNeighbors[k] == -1){
						//continue;
					//}
					//if(centroids[vNeighbors[k]] == null){
						//Polygon polygon = ((Polygon)polygonList.elementAt(vNeighbors[k]));
						//double x = 0;
						//double y = 0;
						//double z = 0;
						//int N = polygon.getNodeCount();
						//for(int l=0;l<N;l+= 1){
							//Node node = polygon.getNodes(l);
							//x+= node.getX();
							//y+= node.getY();
							//z+= node.getZ();
						//}
						//x/= N;
						//y/= N;
						//z/= N;
						//centroids[vNeighbors[k]] = new Node(x,y,z);
					//}
				//}

				//if(vNeighbors.length > 2){
					////Add polygons to surface
					//if(centroidSurface == null){
						//centroidSurface = new SlowSurface(0,1);
					//}
					//for(int k=1;k<vNeighbors.length-1;k+= 1){
						//if(	centroidSurface.getNumFacesForEdge(centroids[vNeighbors[0]],centroids[vNeighbors[k]]) < 2 &&
							//centroidSurface.getNumFacesForEdge(centroids[vNeighbors[k]],centroids[vNeighbors[k+1]]) < 2 && 
							//centroidSurface.getNumFacesForEdge(centroids[vNeighbors[0]],centroids[vNeighbors[k+1]]) < 2
						//){
							//centroidSurface.addPolygon(
								//new Triangle(
									//new Node[] {centroids[vNeighbors[0]],centroids[vNeighbors[k]],centroids[vNeighbors[k+1]]}));
							//if(vNeighbors[k] == polygonBeginPI || vNeighbors[k] == polygonEndPI){
								//bAnchorForbiddenArr[vNeighbors[k]] = true;
							//}
							//if(vNeighbors[k+1] == polygonBeginPI || vNeighbors[k+1] == polygonEndPI){
								//bAnchorForbiddenArr[vNeighbors[k+1]] = true;
							//}
						//}
					//}
					//////Begin
					////if(	centroidSurface.getNumFacesForEdge(centroids[i],centroids[polygonBeginPI]) < 2 &&
						////centroidSurface.getNumFacesForEdge(centroids[polygonBeginPI],centroids[polygonCornerPIArr[0]]) < 2 && 
						////centroidSurface.getNumFacesForEdge(centroids[i],centroids[polygonCornerPIArr[0]]) < 2){
						////centroidSurface.addPolygon(new Triangle(new Node[] {centroids[i],centroids[polygonBeginPI],centroids[polygonCornerPIArr[0]]}));
					////}
					//////Corners
					////for(int k = 0;k < polygonCornerPIArr.length-1;k+= 1){
						////if(	centroidSurface.getNumFacesForEdge(centroids[i],centroids[polygonCornerPIArr[k]]) < 2 &&
							////centroidSurface.getNumFacesForEdge(centroids[polygonCornerPIArr[k]],centroids[polygonCornerPIArr[k+1]]) < 2 && 
							////centroidSurface.getNumFacesForEdge(centroids[i],centroids[polygonCornerPIArr[k+1]]) < 2){
							////centroidSurface.addPolygon(new Triangle(new Node[] {centroids[i],centroids[polygonCornerPIArr[k]],centroids[polygonCornerPIArr[k+1]]}));
						////}
					////}
					//////End
					////if(	centroidSurface.getNumFacesForEdge(centroids[i],centroids[polygonEndPI]) < 2 &&
						////centroidSurface.getNumFacesForEdge(centroids[polygonEndPI],centroids[polygonCornerPIArr[polygonCornerPIArr.length-1]]) < 2 && 
						////centroidSurface.getNumFacesForEdge(centroids[i],centroids[polygonCornerPIArr[polygonCornerPIArr.length-1]]) < 2){
						////centroidSurface.addPolygon(new Triangle(new Node[] {centroids[i],centroids[polygonEndPI],centroids[polygonCornerPIArr[polygonCornerPIArr.length-1]]}));
					////}
				//}
			//}
		//}
	//}
////System.out.println(centroidSurface.getInfo());
	//return centroidSurface;
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:08:34 PM)
 * @return cbit.vcell.geometry.surface.Surface
 */
private SlowSurface createCentroidSurface2() {

	SlowSurface centroidSurface = new SlowSurface(0,1);
	//Calculate centroids
	Node[] centroids = new Node[polygonList.size()];
	for(int i=0;i<polyNodeIndexes.size();i+= 1){
		int[] nodeIArr = (int[])polyNodeIndexes.elementAt(i);
		double x = 0;
		double y = 0;
		double z = 0;
		for(int l=0;l<nodeIArr.length;l+= 1){
			Node node = (Node)nodeList.elementAt(nodeIArr[l]);
			x+= node.getX();
			y+= node.getY();
			z+= node.getZ();
		}
		x/= nodeIArr.length;
		y/= nodeIArr.length;
		z/= nodeIArr.length;
		centroids[i] = new Node(x,y,z);
	}
	//Create centroid mesh
	int[] orderedPolygons = new int[25];
	for(int i=0;i<nodeEdgeList.size();i+= 1){
		int[] edgesI = (int[])nodeEdgeList.elementAt(i);
		int[] edgesICopy = (int[])edgesI.clone();
		java.util.Arrays.fill(orderedPolygons,-1);
		int orderedPolygonCount = 0;
		do{
			int newPolygonI = -1;
			for(int j = 0;j< edgesICopy.length;j+= 1){
				if(edgesICopy[j] == -1){
					continue;
				}
				int[] polygonsI = (int[])faceList.elementAt(edgesICopy[j]);
				if(orderedPolygons[0] == -1){
					orderedPolygons[0] = polygonsI[FACE_POLYGON1];
					orderedPolygonCount = 1;
					newPolygonI = polygonsI[FACE_POLYGON2];
				}else{
					if(polygonsI[FACE_POLYGON1] == orderedPolygons[orderedPolygonCount-1]){
						newPolygonI = polygonsI[FACE_POLYGON2];
					}
					if(polygonsI[FACE_POLYGON2] == orderedPolygons[orderedPolygonCount-1]){
						newPolygonI = polygonsI[FACE_POLYGON1];
					}
				}
				if(newPolygonI == orderedPolygons[0]){
					newPolygonI = -1;
					break;
				}
				if(newPolygonI != -1){
					edgesICopy[j] = -1;
					orderedPolygons[orderedPolygonCount] = newPolygonI;
					orderedPolygonCount+= 1;
					if(orderedPolygonCount >= 3){
						//System.out.println(orderedPolygons[0]+","+orderedPolygons[orderedPolygonCount-2]+","+orderedPolygons[orderedPolygonCount-1]);
						centroidSurface.addPolygon(
							new Triangle(
									centroids[orderedPolygons[0]],
									centroids[orderedPolygons[orderedPolygonCount-2]],
									centroids[orderedPolygons[orderedPolygonCount-1]]
								)
						);
					}
					break;
				}
			}
			if(newPolygonI == -1){
				//System.out.print("N="+i+" (");
				//for(int j=0;j<orderedPolygonCount;j+= 1){
					//System.out.print((j != 0?",":"")+orderedPolygons[j]);
				//}
				//System.out.println(")");
				break;
			}
		}while(true);
		
	}

	return centroidSurface;
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2004 2:41:40 PM)
 * @return double
 */
public double getArea() {
	double area = 0.0;
	for (int i = 0; i < polygonList.size(); i++){
		area += ((Polygon)polygonList.elementAt(i)).getArea();
	}
	return area;
}
/**
 * Boundary constructor comment.
 */
public int getExteriorRegionIndex() {
	return(fieldExteriorRegionIndex);
}
/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 4:36:42 PM)
 */
public String getInfo() {
	StringBuffer sb = new StringBuffer();
	sb.append(
		"Polygons = "+polygonList.size() +"\n"+
		"Nodes    = "+nodeList.size() + "\n"+
		"Edges    = "+edgeList.size() + "\n"+
		"Faces    = "+faceList.size() + "\n\n"
	);

	for(int i=0;i<polygonList.size();i+= 1){
		Polygon p = (Polygon)polygonList.elementAt(i);
		sb.append("Polygon "+i+"  ");
		for(int j=0;j<p.getNodeCount();j+= 1){
			for(int k=0;k<nodeList.size();k+= 1){
				Node n = (Node)nodeList.elementAt(k);
				if(n.equals(p.getNodes(j))){
					sb.append(k+(j != (p.getNodeCount()-1)?",":""));
					continue;
				}
			}
		}
		sb.append("\n");
	}
	
	sb.append("\n");
	for(int i=0;i<nodeList.size();i+= 1){
		Node node = (Node)nodeList.elementAt(i);
		sb.append("N="+i+" "+node.getX()+","+node.getY()+","+node.getZ()+"  ");
		int[] nodeEdges = (int[])nodeEdgeList.elementAt(i);
		sb.append("E=(");
		for(int j=0;j<nodeEdges.length;j+= 1){
			sb.append((j!= 0?",":"")+nodeEdges[j]);
		}
		sb.append(")\n");
	}
	
	sb.append("\n");
	for(int i=0;i <faceList.size();i+= 1){
		int[] face = (int[])faceList.elementAt(i);
		//int edgeIndex = face[FACE_EDGEINDEX];
		int edgeIndex = i;
		int[] edge = (int[])edgeList.elementAt(edgeIndex);
		int polyIndex1 = face[FACE_POLYGON1];
		int polyIndex2 = face[FACE_POLYGON2];
		sb.append("E="+edgeIndex+" "+"N=("+edge[EDGE_NODE1]+","+edge[EDGE_NODE2]+")"+" F=("+polyIndex1+","+polyIndex2+")\n");
	}
	sb.append("\n\n");
	for(int i=0;i<edgeList.size();i+= 1){
		int[] edge = (int[])edgeList.elementAt(i);
		int[] face = (int[])faceList.elementAt(i);
		sb.append("E="+i+" FC="+(0+(face[FACE_POLYGON1] != -1?1:0)+(face[FACE_POLYGON2] != -1?1:0))+"\n");
	}
	return sb.toString();
}
/**
 * Boundary constructor comment.
 */
public int getInteriorRegionIndex() {
	return(fieldInteriorRegionIndex);
}
/**
 * Insert the method's description here.
 * Creation date: (5/8/2004 1:26:13 PM)
 * @return int
 * @param edgeBegin cbit.vcell.geometry.surface.Node
 * @param edgeEnd cbit.vcell.geometry.surface.Node
 */
private int getNumFacesForEdge(Node edgeBegin, Node edgeEnd) {
	
	for(int j = 0; j < edgeList.size();j+= 1){
		int[] edge = (int[])edgeList.elementAt(j);
		if( (nodeList.elementAt(edge[EDGE_NODE1]).equals(edgeBegin) && nodeList.elementAt(edge[EDGE_NODE2]).equals(edgeEnd)) ||
			(nodeList.elementAt(edge[EDGE_NODE2]).equals(edgeBegin) && nodeList.elementAt(edge[EDGE_NODE1]).equals(edgeEnd))){
			int[] face = (int[])faceList.elementAt(j);
			return (face[FACE_POLYGON1] != -1?1:0)+(face[FACE_POLYGON2] != -1?1:0);
		}
	}

	return 0;
	//throw new RuntimeException("No faces found for nodes "+edgeBegin+","+edgeEnd);
}
/**
 * Boundary constructor comment.
 */
public int getPolygonCount() {
	return(polygonList.size());
}
/**
 * Boundary constructor comment.
 */
public Polygon getPolygons(int i) {
	return((Polygon) polygonList.elementAt(i));
}
/**
 * Quadrilateral constructor comment.
 */
public void reverseDirection() {
	int regionIndex = fieldInteriorRegionIndex;
	fieldInteriorRegionIndex = fieldExteriorRegionIndex;
	fieldExteriorRegionIndex = regionIndex;
	for (int i = 0; i < getPolygonCount(); i++) {
		getPolygons(i).reverseDirection();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/5/2004 6:12:39 PM)
 */
public static final void runTest() {

	cbit.vcell.geometry.surface.Node n1 = new cbit.vcell.geometry.surface.Node(0,0,0);
	cbit.vcell.geometry.surface.Node n2 = new cbit.vcell.geometry.surface.Node(1,0,0);
	cbit.vcell.geometry.surface.Node n3 = new cbit.vcell.geometry.surface.Node(1,1,0);
	cbit.vcell.geometry.surface.Node n4 = new cbit.vcell.geometry.surface.Node(0,1,0);

	cbit.vcell.geometry.surface.Node n5 = new cbit.vcell.geometry.surface.Node(-1,2,0);
	cbit.vcell.geometry.surface.Node n6 = new cbit.vcell.geometry.surface.Node(0,2,0);
	cbit.vcell.geometry.surface.Node n7 = new cbit.vcell.geometry.surface.Node(1,2,0);
	cbit.vcell.geometry.surface.Node n8 = new cbit.vcell.geometry.surface.Node(2,2,0);
	cbit.vcell.geometry.surface.Node n9 = new cbit.vcell.geometry.surface.Node(-1,1,0);
	cbit.vcell.geometry.surface.Node n10 = new cbit.vcell.geometry.surface.Node(2,1,0);
	cbit.vcell.geometry.surface.Node n11 = new cbit.vcell.geometry.surface.Node(-1,0,0);
	cbit.vcell.geometry.surface.Node n12 = new cbit.vcell.geometry.surface.Node(2,0,0);
	cbit.vcell.geometry.surface.Node n13 = new cbit.vcell.geometry.surface.Node(-1,-1,0);
	cbit.vcell.geometry.surface.Node n14 = new cbit.vcell.geometry.surface.Node(0,-1,0);
	cbit.vcell.geometry.surface.Node n15 = new cbit.vcell.geometry.surface.Node(1,-1,0);
	cbit.vcell.geometry.surface.Node n16 = new cbit.vcell.geometry.surface.Node(2,-1,0);

	Quadrilateral quad1 = new Quadrilateral(n1,n2,n3,n4);
	Quadrilateral quad2 = new Quadrilateral(n9,n4,n6,n5);
	Quadrilateral quad3 = new Quadrilateral(n4,n3,n7,n6);
	Quadrilateral quad4 = new Quadrilateral(n3,n10,n8,n7);
	Quadrilateral quad5 = new Quadrilateral(n11,n1,n4,n9);
	Quadrilateral quad6 = new Quadrilateral(n2,n12,n10,n3);
	Quadrilateral quad7 = new Quadrilateral(n13,n14,n1,n11);
	Quadrilateral quad8 = new Quadrilateral(n14,n15,n2,n1);
	Quadrilateral quad9 = new Quadrilateral(n15,n16,n12,n2);
	
	cbit.vcell.geometry.surface.SlowSurface surf = new cbit.vcell.geometry.surface.SlowSurface(0,1,quad1);
	surf.addPolygon(quad2);
	surf.addPolygon(quad3);
	surf.addPolygon(quad4);
	surf.addPolygon(quad5);
	surf.addPolygon(quad6);
	surf.addPolygon(quad7);
	surf.addPolygon(quad8);
	surf.addPolygon(quad9);
	
	System.out.println("Original\n"+surf.getInfo());
	System.out.println("Centroid\n"+surf.createCentroidSurface().getInfo());
	System.out.println("Centroid2\n"+surf.createCentroidSurface2());
}
}
