/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.ObjectReferenceWrapper;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Node;
import cbit.util.graph.Tree;
import cbit.vcell.geometry.surface.OrigSurface;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Quadrilateral;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.TaubinSmoothing;
import cbit.vcell.geometry.surface.TaubinSmoothingSpecification;
import cbit.vcell.geometry.surface.TaubinSmoothingWrong;
import cbit.vcell.render.Vect3d;
import cbit.vcell.solvers.MembraneElement;

public class RegionImage implements Serializable {
	
	public static final double NO_SMOOTHING = .6;
	
	private int[] mapImageIndexToLinkRegion;
	private int[] mapLinkRegionToDistinctRegion;
//	private CompactUnsignedIntStorage mapImageIndexToRegionIndex;
	private SurfaceCollection surfaceCollection;
	private double filterCutoffFrequency;
	
	private int numX = 0;
	private int numY = 0;
	private int numZ = 0;
	private int numXY = 0;
	private RegionInfo regionInfos[] = null;
	private final static int NOT_VISITED = -1;
	private final static int DEPTH_END_SEED = -2;
	
	public static boolean debug_bCheckPolygonQuality = false;
	public static double debug_maxQuadAngle = 180;
	public static Double debug_filterCutoffFrequencyOverride = null;

	public static class SurfAndFace {
		private int surf;
		private int face;
		public SurfAndFace(int surf, int face) {
			super();
			this.surf = surf;
			this.face = face;
		}
		public int getSurf() {
			return surf;
		}
		public int getFace() {
			return face;
		}
	}
	ArrayList<SurfAndFace> quadIndexToSurfAndFace = new ArrayList<>();

	
//	public static class CompactUnsignedIntStorage{
//		private byte[] smallRange;
//		private int SMALL_RANGE_SIZE = 256;
//		private short[] mediumRange;
//		private int MEDIUM_RANGE_SIZE = 256*256;
//		private int[] longRange;
//
//		
//		public CompactUnsignedIntStorage(int fixedArraySize){
//			smallRange = new byte[fixedArraySize];
//		}
//		public int getValue(int index){
//			if(longRange != null){
//				return longRange[index];
//			}else if(mediumRange != null){
//				return (int)(0x00000000 | mediumRange[index]);
//			}else{
//				return (int)(0x00000000 | smallRange[index]);
//			}
//		}
//		public void setValue(int index,int value){
//			if(value < SMALL_RANGE_SIZE){
//				if(longRange != null){
//					longRange[index] = value;
//				}else if(mediumRange != null){
//					mediumRange[index] = (short)(0x0000 | value);
//				}else{
//					smallRange[index] = (byte)(0x00 | value);
//				}
//			}else if(value < MEDIUM_RANGE_SIZE){
//				if(longRange != null){
//					longRange[index] = value;
//				}else{
//					if(mediumRange == null){
//						mediumRange = new short[smallRange.length];
//						for (int i = 0; i < smallRange.length; i++) {
//							mediumRange[i] = (short)(0x0000 | smallRange[i]);
//						}
//						smallRange = null;
//					}
//					mediumRange[index] = (short)(0x0000 | value);
//				}
//			}else{
//				if(longRange == null){
//					longRange = new int[(smallRange == null?mediumRange.length:smallRange.length)];
//					if(smallRange != null){
//						for (int i = 0; i < smallRange.length; i++) {
//							longRange[i] = (int)(0x00000000 | smallRange[i]);
//						}
//						smallRange = null;
//					}else{
//						for (int i = 0; i < mediumRange.length; i++) {
//							longRange[i] = (int)(0x00000000 | mediumRange[i]);
//						}
//						mediumRange = null;
//					}
//				}
//				longRange[index] = value;
//			}
//		}
//	}
	
	public class RegionInfo implements Serializable {
		private int pixelValue;
		private int numPixels;
		private int regionIndex;
		private BitSet mask;
		
		RegionInfo(int argPixelValue, int argNumPixels, int argRegionIndex, BitSet argMask){
			pixelValue = argPixelValue;
			numPixels = argNumPixels;
			regionIndex = argRegionIndex;
			mask = argMask;
		}
		public int getPixelValue(){
			return pixelValue;
		}
		public int getNumPixels(){
			return numPixels;
		}
		public int getRegionIndex(){
			return regionIndex;
		}
		public boolean isIndexInRegion(int index){
			if(mask == null){
				return mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[index]] == regionIndex;
//				return mapImageIndexToRegionIndex.getValue(index) == regionIndex;
			}
			return mask.get(index);
		}
		public String toString(){
			return "RegionInfo(regionIndex="+regionIndex+", numPixel="+numPixels+", imageValue="+pixelValue+")";
		}
	};
	
	public byte[] getShortEncodedRegionIndexImage(){
		int imageSize = numX*numY*numZ;
		byte[] regionIndexImage = new byte[2 * imageSize];
		for (int i = 0; i < imageSize; i ++) {
			int regionIndex  = mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[i]];
			regionIndexImage[2 * i] = (byte)(regionIndex & 0x000000ff);				
			regionIndexImage[2 * i + 1] = (byte)((regionIndex & 0x0000ff00) >> 8);
		}
		return regionIndexImage;
	}

	private static class RegionMask{
		BitSet mask = null;
		int offset = 0;
		byte pixelValue = 0;
		int regionIndex;
		RegionMask(int argMaskSize, byte argPixelValue, int argRegionIndex, int argOffset){
			mask = new BitSet(argMaskSize);
			pixelValue = argPixelValue;
			regionIndex = argRegionIndex;
			offset = argOffset;
		}
	}

	private static class FloodFill2DLine {
		private byte pixelValues[] = null;
		private int regionIndexes[] = null;
		private int offset = 0;
		private int regionIndex = -1;
		private int numX = 0;
		private int numY = 0;
		private RegionMask regionMask = null;
		private final static int MAXDEPTH_TIMES_4 = 10000;

		private int sp = 0;
		private static int stack[] = new int[MAXDEPTH_TIMES_4];	// stack[sp+0] = xl
															// stack[sp+1] = xr
															// stack[sp+1] = y
															// stack[sp+1] = dy

		FloodFill2DLine(byte argPixelValues[], int argRegionIndexes[], int argRegionIndex, int argOffset, int argNumX, int argNumY, RegionMask argRegionMask){
			pixelValues = argPixelValues;
			regionIndexes = argRegionIndexes;
			regionIndex = argRegionIndex;
			offset = argOffset;
			numX = argNumX;
			numY = argNumY;
			regionMask = argRegionMask;
		}
		private void push(int xl, int xr, int y, int dy){
			if (sp+4 < MAXDEPTH_TIMES_4){
				throw new RuntimeException("stack overflow");
			}
			if (y+dy >= 0 && y+dy < numY){
				stack[sp++] = xl;
				stack[sp++] = xr;
				stack[sp++] = y;
				stack[sp++] = dy;
			}
		}
		
		// Fill background
		int fill(int x, int y)
		{
		    int left=-1, x1, x2, dy;
			int count = 0;
		    
		    int offsetXY = x+y*numX;
		    byte seedColor = pixelValues[offset+offsetXY]; //  GetPixel(x, y);
		    if( regionMask.mask.get(offsetXY)){ 			// already visited here
		        return 0;
		    }
		    
		    if( x < 0 || x >= numX || y < 0 || y >= numY ){
		        return 0;
		    }

		    push(x, x, y, 1);        /* needed in some cases */
		    push(x, x, y+1, -1);    /* seed segment (popped 1st) */

		    while( sp > 0 ) { // while not empty

			    //
			    // POP(x1, x2, y, dy);
			    //
			    dy = stack[--sp];
			    y = stack[--sp]+dy;
			    x2 = stack[--sp];
			    x1 = stack[--sp];

			    int yOffset = numX*y;
		        for(x = x1; x >= 0 && pixelValues[offset+x+yOffset] == seedColor && !regionMask.mask.get(x+yOffset); --x ){
					//
					// SetPixel(x, y, new_color);
					//
					regionIndexes[x+yOffset] = regionIndex;
					regionMask.mask.set(x+yOffset);
					count++;
		        }
		        boolean bSkip = false;
		        if( x >= x1 ){
			        bSkip = true;
		        }

		        if (!bSkip){ 
			        left = x+1;
			        if( left < x1 ){
			            push(y, left, x1-1, -dy);    /* leak on left? */
			        }
			        x = x1+1;
		        }
		        
		        do {
			        if (!bSkip){
				        yOffset = numX*y;
			            for( ; x<numX && pixelValues[offset+yOffset+x] == seedColor && !regionMask.mask.get(x+yOffset); ++x ){
			            	//
			            	// SetPixel(x, y, new_color);
			            	//
							regionIndexes[x+yOffset] = regionIndex;
							regionMask.mask.set(x+yOffset);
							count++;
			            }

						push(left, x-1, y, dy);

						if( x > x2+1 ){
							push(x2+1, x-1, y, -dy);    /* leak on right? */
						}
			        }
			        bSkip = false;
					// SKIP: label
					yOffset = numX*y;
			        for( ++x; x <= x2 && pixelValues[x+yOffset] != seedColor; ++x ) {
					}

		            left = x;
		        } while( x<=x2 );
		    }
		    return count;
		}
	}
	
	private static class FloodFill2D {
		private byte pixelValues[] = null;
		private int regionIndexes[] = null;
		private int offset = 0;
		private int regionIndex = -1;
		private int numX = 0;
		private int numY = 0;
		private RegionMask regionMask = null;
		
		private final int MAX_DEPTH = 10;
		
		FloodFill2D(byte argPixelValues[], int argRegionIndexes[], int argRegionIndex, int argOffset, int argNumX, int argNumY, RegionMask argRegionMask){
			pixelValues = argPixelValues;
			regionIndexes = argRegionIndexes;
			regionIndex = argRegionIndex;
			offset = argOffset;
			numX = argNumX;
			numY = argNumY;
			regionMask = argRegionMask;
		}
		
		int fill(int xIndex, int yIndex, int index){
			return fill0(xIndex, yIndex, index, 0);
		}
			
		private int fill0(int xIndex, int yIndex, int index, int depth){
			int count = 1;
			if (depth>MAX_DEPTH){
				regionIndexes[index] = RegionImage.DEPTH_END_SEED;
				return 0;
			}else{
				regionIndexes[index] = regionIndex;
				regionMask.mask.set(index);
			}
			if ((xIndex+1 < numX) && (regionIndexes[index+1] < 0) && (pixelValues[offset+index] == pixelValues[offset+index+1])){
				count += fill0(xIndex+1,yIndex,index+1,depth+1);
			}
			if ((xIndex-1 >= 0) && (regionIndexes[index-1] < 0) && (pixelValues[offset+index] == pixelValues[offset+index-1])){
				count += fill0(xIndex-1,yIndex,index-1,depth+1);
			}
			if ((yIndex+1 < numY) && (regionIndexes[index+numX] < 0) && (pixelValues[offset+index] == pixelValues[offset+index+numX])){
				count += fill0(xIndex,yIndex+1,index+numX,depth+1);
			}
			if ((yIndex-1 >= 0) && (regionIndexes[index-numX] < 0) && (pixelValues[offset+index] == pixelValues[offset+index-numX])){
				count += fill0(xIndex,yIndex-1,index-numX,depth+1);
			}
			return count;
		}
	};

public RegionImage(VCImage vcImage,int dimension,Extent extent,Origin origin,double filterCutoffFrequency) throws cbit.image.ImageException {
	this(vcImage,dimension,extent,origin,filterCutoffFrequency,null);
}

public RegionImage(VCImage vcImage,int dimension,Extent extent,Origin origin,double filterCutoffFrequency,ClientTaskStatusSupport clientTaskStatusSupport) throws cbit.image.ImageException {
	this.numX = vcImage.getNumX();
	this.numY = vcImage.getNumY();
	this.numZ = vcImage.getNumZ();
	this.numXY = numX*numY;
	this.filterCutoffFrequency = filterCutoffFrequency;
	if (debug_filterCutoffFrequencyOverride!=null){
		this.filterCutoffFrequency = debug_filterCutoffFrequencyOverride;
	}
	
//	long startTime = System.currentTimeMillis();
	calculateRegions_New(vcImage,dimension,extent,origin,clientTaskStatusSupport);
//	RegionInfo[] tempRI = regionInfos;
//	regionInfos = null;
//	System.out.println("Total time for new regions calc="+((double)(System.currentTimeMillis()-startTime)/1000.0));
//	if(true){
//		throw new ImageException();
//	}
//	startTime = System.currentTimeMillis();
//	calculateRegions(vcImage);
//	System.out.println("Total time for old regions calc="+((double)(System.currentTimeMillis()-startTime)/1000.0));
//	if(tempRI.length != regionInfos.length){
//		throw new ImageException("Num regions not match");
//	}
//	for (int i = 0; i < tempRI.length; i++) {
//		boolean bFound = false;
//		for (int j = 0; j < regionInfos.length; j++) {
//			if(regionInfos[j].regionIndex == tempRI[i].regionIndex){
//				if(regionInfos[j].numPixels != tempRI[i].numPixels){
//					throw new ImageException("NumPixels not match in regions");
//				}
//				if(regionInfos[j].pixelValue != tempRI[i].pixelValue){
//					throw new ImageException("PixelValues not match in regions");
//				}
//				if(!regionInfos[j].mask.equals(tempRI[i].mask)){
//					throw new ImageException("Mask not match in regions");
//				}
//				bFound = true;
//				break;
//			}
//		}
//		if(!bFound){
//			throw new ImageException("RegionIndex not match in regions");
//		}
//	}
//	if(true){
//		throw new ImageException();
//	}
}


public static void sortSurfaceCollection(SurfaceCollection surfCollection){
	//Sort nodes
	Object[] nodeObjArr = new Object[surfCollection.getNodes().length];
	for (int i = 0; i < nodeObjArr.length; i++) {
		Object[] temp = new Object[2];
		temp[0] = new Integer(i);
		temp[1] = surfCollection.getNodes()[i];
		nodeObjArr[i] = temp;
	}
	Arrays.sort(nodeObjArr, new Comparator<Object> (){
		public int compare(Object obj1, Object obj2) {
			cbit.vcell.geometry.surface.Node o1 = (cbit.vcell.geometry.surface.Node)((Object[])obj1)[1];
			cbit.vcell.geometry.surface.Node o2 = (cbit.vcell.geometry.surface.Node)((Object[])obj2)[1];
			double xdiff = o1.getX()-o2.getX();
			double xmin = Math.min(Math.abs(o1.getX()),Math.abs(o2.getX()));
			double xlimit = (1e-12*(xmin>= 1.0?(Math.pow(10,(int)Math.log10(xmin)+1)):1));
			double ydiff = o1.getY()-o2.getY();
			double ymin = Math.min(Math.abs(o1.getY()),Math.abs(o2.getY()));
			double ylimit = (1e-12*(ymin>= 1.0?(Math.pow(10,(int)Math.log10(ymin)+1)):1));
			double zdiff = o1.getZ()-o2.getZ();
			double zmin = Math.min(Math.abs(o1.getZ()),Math.abs(o2.getZ()));
			double zlimit = (1e-12*(zmin>= 1.0?(Math.pow(10,(int)Math.log10(zmin)+1)):1));
			if(Math.abs(zdiff) < zlimit){
				if(Math.abs(ydiff) < ylimit){
					return (int)Math.signum((Math.abs(xdiff)<xlimit?0:xdiff));
				}
				return (int)Math.signum((Math.abs(ydiff)<ylimit?0:ydiff));
			}
			return (int)Math.signum((Math.abs(zdiff)<zlimit?0:zdiff));
		}});

	int[] remap = new int[nodeObjArr.length];
	Arrays.fill(remap, -1);
	cbit.vcell.geometry.surface.Node[] sortedNodes = new cbit.vcell.geometry.surface.Node[nodeObjArr.length];
	for (int i = 0; i < nodeObjArr.length; i++) {
		sortedNodes[i] = (cbit.vcell.geometry.surface.Node)((Object[])nodeObjArr[i])[1];
		if(remap[sortedNodes[i].getGlobalIndex()] == -1){
			remap[sortedNodes[i].getGlobalIndex()] = i;
		}else{
			throw new RuntimeException("SORT error: duplicate nodes");
		}
	}
//	surfCollection.setNodes(sortedNodes);
	System.arraycopy(sortedNodes, 0, surfCollection.getNodes(), 0,sortedNodes.length);
	HashSet<cbit.vcell.geometry.surface.Node> remapHashSet = new HashSet<cbit.vcell.geometry.surface.Node>();
	for (int i = 0; i < surfCollection.getSurfaceCount(); i++) {
		Surface surf = surfCollection.getSurfaces(i);
		Polygon[] sortedPolygonArr = new Polygon[surf.getPolygonCount()];
		for (int j = 0; j < surf.getPolygonCount(); j++) {
			Polygon poly = surf.getPolygons(j);
			for (int k = 0; k < poly.getNodes().length; k++) {
				cbit.vcell.geometry.surface.Node node = poly.getNodes(k);
				if(!remapHashSet.contains(node)){
					node.setGlobalIndex(remap[node.getGlobalIndex()]);
					remapHashSet.add(node);
				}
			}
			sortedPolygonArr[j] = poly;
		}
		Arrays.sort(sortedPolygonArr, new Comparator<Polygon> (){
			public int compare(Polygon obj1, Polygon obj2) {
				Coordinate o1 = ((Quadrilateral)obj1).calculateCentroid();
				Coordinate o2 = ((Quadrilateral)obj2).calculateCentroid();
				double xdiff = o1.getX()-o2.getX();
				double xmin = Math.min(Math.abs(o1.getX()),Math.abs(o2.getX()));
				double xlimit = (1e-12*(xmin>= 1.0?(Math.pow(10,(int)Math.log10(xmin)+1)):1));
				double ydiff = o1.getY()-o2.getY();
				double ymin = Math.min(Math.abs(o1.getY()),Math.abs(o2.getY()));
				double ylimit = (1e-12*(ymin>= 1.0?(Math.pow(10,(int)Math.log10(ymin)+1)):1));
				double zdiff = o1.getZ()-o2.getZ();
				double zmin = Math.min(Math.abs(o1.getZ()),Math.abs(o2.getZ()));
				double zlimit = (1e-12*(zmin>= 1.0?(Math.pow(10,(int)Math.log10(zmin)+1)):1));
				if(Math.abs(zdiff) < zlimit){
					if(Math.abs(ydiff) < ylimit){
						return (int)Math.signum((Math.abs(xdiff)<xlimit?0:xdiff));
					}
					return (int)Math.signum((Math.abs(ydiff)<ylimit?0:ydiff));
				}
				return (int)Math.signum((Math.abs(zdiff)<zlimit?0:zdiff));
			}});
		OrigSurface sortedSurface = new OrigSurface(surf.getInteriorRegionIndex(),surf.getExteriorRegionIndex());
		for (int k = 0; k < sortedPolygonArr.length; k++) {
			int minGlobalIndex = sortedPolygonArr[k].getNodes(0).getGlobalIndex();
//			System.out.print("Surf "+i+" poly "+k+" nodeGI - ");
			for (int j = 0; j < sortedPolygonArr[k].getNodeCount(); j++) {
				if(sortedPolygonArr[k].getNodes(j).getGlobalIndex() < minGlobalIndex){
					minGlobalIndex = sortedPolygonArr[k].getNodes(j).getGlobalIndex();
				}
//				System.out.print(sortedPolygonArr[k].getNodes(j).getGlobalIndex()+" ");
			}
			while(sortedPolygonArr[k].getNodes(0).getGlobalIndex() != minGlobalIndex){
				cbit.vcell.geometry.surface.Node lastNode = sortedPolygonArr[k].getNodes(sortedPolygonArr[k].getNodeCount()-1);
				System.arraycopy(sortedPolygonArr[k].getNodes(), 0, sortedPolygonArr[k].getNodes(), 1, sortedPolygonArr[k].getNodeCount()-1);
				sortedPolygonArr[k].getNodes()[0] = lastNode;
			}
//			System.out.println();
			sortedSurface.addPolygon(sortedPolygonArr[k]);
		}
		surfCollection.setSurfaces(i, sortedSurface);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 10:30:20 AM)
 * @param image cbit.image.VCImage
 */
private void calculateRegions(VCImage vcImage) throws cbit.image.ImageException {

	long time1 = System.currentTimeMillis();
	RegionMask regionMasks[][] = new RegionMask[numZ][];
	for (int k = 0; k < vcImage.getNumZ(); k++){
		regionMasks[k] = calculateRegions3D(vcImage.getPixels(),k*numXY,numX,numY);
	}
	long time2 = System.currentTimeMillis();
	//time2 = System.currentTimeMillis();
	//RegionMask regionMasksFast[][] = new RegionMask[numZ][];
	//for (int k = 0; k < vcImage.getNumZ(); k++){
		//regionMasksFast[k] = calculateRegions3Dfaster(vcImage.getPixels(),k*numXY,numX,numY);
	//}
	//long time3 = System.currentTimeMillis();
	//System.out.println("4way recursive took "+((time2-time1)/1000.0)+" s, nonrecursive line took "+((time3-time2)/1000.0)+" s");
	
	//
	// consolidate "off-plane contiguous" region indexes
	// build a graph of all 2D regions that touch
	//
	// the final 3D regions are the set of nodes in each of the spanning trees.
	//

	//
	// build graph with 1 node per 2d region
	//
	Graph connectionGraph = new Graph();
	for (int k = 0; k < numZ; k++){
		for (int i = 0; i < regionMasks[k].length; i++){
			Node node = new Node(k+","+i, new org.vcell.util.ObjectReferenceWrapper(regionMasks[k][i]));
			connectionGraph.addNode(node);
		}
	}

	//
	// add edges for any slice-slice touching of regions with same pixel value
	//
	BitSet zeroBitSet = new BitSet();
	BitSet intersection = new BitSet(numXY);
	for (int k = 0; k < numZ-1; k++){
		for (int i = 0; i < regionMasks[k].length; i++){
			Node node_i = connectionGraph.getNode(k+","+i);
			for (int j = 0; j < regionMasks[k+1].length; j++){
				if (regionMasks[k][i].pixelValue == regionMasks[k+1][j].pixelValue){
					intersection.and(zeroBitSet); // clear mask
					intersection.or(regionMasks[k][i].mask);
					intersection.and(regionMasks[k+1][j].mask);
					if (!intersection.equals(zeroBitSet)){
						Node node_j = connectionGraph.getNode((k+1)+","+j);
						connectionGraph.addEdge(new Edge(node_i, node_j));
					}
				}
			}
		}
	}

	//
	// get spanning forest, and for each spanning tree, assign a single regionID to each contained 2d mask
	//
	Tree spanningForest[] = connectionGraph.getSpanningForest();
	regionInfos = new RegionInfo[spanningForest.length];
	for (int i = 0; i < spanningForest.length; i++){
		Node nodes[] = spanningForest[i].getNodes();
		int pixelValue = -1;
		int numPixels = 0;
		BitSet fullMask = new BitSet(numXY*numZ);
		for (int j = 0; j < nodes.length; j++){
			RegionMask regionMask = (RegionMask)((ObjectReferenceWrapper)nodes[j].getData()).getObject();
			pixelValue = regionMask.pixelValue;
			for (int k = 0; k < numXY; k++){
				if (regionMask.mask.get(k)){
					fullMask.set(regionMask.offset+k);
					numPixels++;
				}
			}
		}
		regionInfos[i] = new RegionInfo(pixelValue,numPixels,i,fullMask);
	}
	long time3 = System.currentTimeMillis();
	System.out.println("4way recursive on slices took "+((time2-time1)/1000.0)+" s, total RegionImage time "+((time3-time1)/1000.0)+" s");
}

private void createLink(Vector<Integer>[] regionLinkArr,int currentRegion,int[] regionPixels,int masterIndex){
	if(regionLinkArr[currentRegion] == null){
		regionLinkArr[currentRegion] = new Vector<Integer>();
	}
	if(!regionLinkArr[currentRegion].contains(regionPixels[masterIndex])){
		regionLinkArr[currentRegion].add(regionPixels[masterIndex]);
	}
}

//
//Calculate regions using single pass algorithm.  Creates information
//used to generate surfaces as well
//
private void calculateRegions_New(VCImage vcImage,int dimension,Extent extent, Origin origin,ClientTaskStatusSupport clientTaskStatusSupport) throws ImageException {

	//Find linked pixel values in x,y,z and surface elements locations
	final int EMPTY_REGION = 0;
	final int FIRST_REGION = 1;
	final int ARRAY_SIZE_INCREMENT = 1000;
	byte[] imagePixels = vcImage.getPixels();
	int[] regionPixels = new int[imagePixels.length];
	BitSet xSurfElements = new BitSet(imagePixels.length);
	BitSet ySurfElements = new BitSet(imagePixels.length);
	BitSet zSurfElements = new BitSet(imagePixels.length);
	Vector<Integer>[] regionLinkArr = new Vector[ARRAY_SIZE_INCREMENT];
	int[] regionSizeArr = new int[ARRAY_SIZE_INCREMENT];
	Vector<Byte> regionImagePixelV = new Vector<Byte>();
	regionImagePixelV.add((byte)0);//0 not used
	byte currentImagePixel;
	int currentRegion;
	int masterIndex = 0;
	int nextAvailableRegion = FIRST_REGION;
	for (int zIndex = 0; zIndex < vcImage.getNumZ(); zIndex++) {
		if(clientTaskStatusSupport != null){
			if(clientTaskStatusSupport.isInterrupted()){
				return;
			}
		}
		int zForwardIndex = ((zIndex+1)< vcImage.getNumZ()?masterIndex+(vcImage.getNumX()*vcImage.getNumY()):-1);
		for (int yIndex = 0; yIndex < vcImage.getNumY(); yIndex++) {
			int yForwardIndex = ((yIndex+1)< vcImage.getNumY()?masterIndex+vcImage.getNumX():-1);
			currentImagePixel = imagePixels[masterIndex];
			if(regionPixels[masterIndex] != EMPTY_REGION){
				currentRegion = regionPixels[masterIndex];
			}else{
				currentRegion = nextAvailableRegion;
				if(currentRegion >= regionLinkArr.length){
//					make more room for arrays
					Vector<Integer>[] temp = new Vector[regionLinkArr.length+ARRAY_SIZE_INCREMENT];
					System.arraycopy(regionLinkArr, 0, temp, 0, regionLinkArr.length);
					regionLinkArr = temp;
					int[] temp2 = new int[regionSizeArr.length+ARRAY_SIZE_INCREMENT];
					System.arraycopy(regionSizeArr, 0, temp2, 0, regionSizeArr.length);
					regionSizeArr = temp2;
				}
				regionPixels[masterIndex] = currentRegion;
				if(regionImagePixelV.size() != currentRegion){
					throw new ImageException("Mismatch between region and pixel buffer");
				}
				regionImagePixelV.add(currentImagePixel);
				nextAvailableRegion+= 1;
			}
			for (int xIndex = 0; xIndex < vcImage.getNumX(); xIndex++) {
				if(imagePixels[masterIndex] == currentImagePixel){
					if(regionPixels[masterIndex] != EMPTY_REGION){
						if(currentRegion != regionPixels[masterIndex]){
							createLink(regionLinkArr, currentRegion, regionPixels, masterIndex);
						}
					}else{
						regionPixels[masterIndex] = currentRegion;
					}
				}else{
					xSurfElements.set(masterIndex-1);
					currentImagePixel = imagePixels[masterIndex];
					if(regionPixels[masterIndex] != EMPTY_REGION){
						currentRegion = regionPixels[masterIndex];
					}else{
						currentRegion = nextAvailableRegion;
						if(currentRegion >= regionLinkArr.length){
//							make more room for arrays
							Vector<Integer>[] temp = new Vector[regionLinkArr.length+ARRAY_SIZE_INCREMENT];
							System.arraycopy(regionLinkArr, 0, temp, 0, regionLinkArr.length);
							regionLinkArr = temp;
							int[] temp2 = new int[regionSizeArr.length+ARRAY_SIZE_INCREMENT];
							System.arraycopy(regionSizeArr, 0, temp2, 0, regionSizeArr.length);
							regionSizeArr = temp2;
						}
						regionPixels[masterIndex] = currentRegion;
						if(regionImagePixelV.size() != currentRegion){
							throw new ImageException("Mismatch between region and pixel buffer");
						}
						regionImagePixelV.add(currentImagePixel);
						nextAvailableRegion+= 1;
					}
				}
				regionSizeArr[currentRegion]+= 1;
				//Look Ahead
//				if((xIndex+1) < vcImage.getNumX()){
//					if(imagePixels[masterIndex+1] == currentImagePixel){
//						if(regionPixels[masterIndex+1] == EMPTY_REGION){
//							regionPixels[masterIndex+1] = currentRegion;
//						}else if(currentRegion != regionPixels[masterIndex+1]){
//							createLink(regionLinkArr, currentRegion, regionPixels, masterIndex+1);
//						}
//					}
//				}
				if(yForwardIndex != -1){
					if(imagePixels[yForwardIndex] == currentImagePixel){
						if(regionPixels[yForwardIndex] == EMPTY_REGION){
							regionPixels[yForwardIndex] = currentRegion;
						}else if(currentRegion != regionPixels[yForwardIndex]){
							createLink(regionLinkArr, currentRegion, regionPixels, yForwardIndex);
						}
					}else{
						ySurfElements.set(masterIndex);
					}
					yForwardIndex+= 1;
				}
				if(zForwardIndex != -1){
					if(imagePixels[zForwardIndex] == currentImagePixel){
						if(regionPixels[zForwardIndex] == EMPTY_REGION){
							regionPixels[zForwardIndex] = currentRegion;
						}else if(currentRegion != regionPixels[zForwardIndex]){
							createLink(regionLinkArr, currentRegion, regionPixels, zForwardIndex);
						}
					}else{
						zSurfElements.set(masterIndex);
					}
					zForwardIndex+= 1;
				}
				
				masterIndex+= 1;
			}
		}
	}
//	System.out.println(xSurfElements.cardinality()+" "+ySurfElements.cardinality()+" "+zSurfElements.cardinality());
	//System.out.println("----------link time "+((System.currentTimeMillis()-startTime)/1000.0));
	//startTime = System.currentTimeMillis();
	

	//Distribute links
	Vector<Integer>[] collector = new Vector[nextAvailableRegion/*regionLinkArr.length*/];
	for (int i = 1; i < nextAvailableRegion/*regionSizeArr.length*/; i++) {// 0 not used
		if(regionSizeArr[i] != 0){
			collector[i] = (Vector)(regionLinkArr[i] != null?regionLinkArr[i].clone():null);
		}
	}
	for (int i = 1; i < nextAvailableRegion/*regionSizeArr.length*/; i++) {// 0 not used
		if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
			return;
		}
//		System.out.print("region="+i+" size="+regionSizeArr[i]);
		Vector<Integer> intV = regionLinkArr[i];
		for (int j = 0; intV!= null && j < intV.size();j++) {
//			System.out.print((j==0?" - ":" ")+intV.elementAt(j));
			//Collect
			Vector<Integer> collectIntV = collector[intV.elementAt(j)];
			if(collectIntV == null){
				collectIntV = new Vector<Integer>();
				collector[intV.elementAt(j)] = collectIntV;
				if(regionLinkArr[intV.elementAt(j)] != null){
					collectIntV.addAll(regionLinkArr[intV.elementAt(j)]);
				}
			}
			if(!collectIntV.contains(i)){
				collectIntV.add(i);
			}
		}
//		System.out.println();
	}
	//System.out.println("----------distribute link time "+((System.currentTimeMillis()-startTime)/1000.0));
	//startTime = System.currentTimeMillis();
	
//	for (int i = 1; i < collector.length; i++) {// 0 not used
//		if(regionSizeArr[i] != 0){
//			System.out.print("Collect region="+i+" size="+regionSizeArr[i]);
//			Vector<Integer> intV = collector[i];
//			for (int j = 0; intV!= null && j < intV.size();j++) {
//				System.out.print((j==0?" - ":" ")+intV.elementAt(j));
//			}
//			System.out.println();
//		}
//	}
	
	//Gather links into distinct regions
	int[] linkRegionMap = new int[collector.length];//Map link-regions(implicit) to distinct-regions
	int totalSize = 0;
	Vector<Vector<Integer>> regionsV = new Vector<Vector<Integer>>();
	Vector<Integer> regionsSizeV = new Vector<Integer>();
	BitSet checkFlagBS = new BitSet(collector.length);
	for (int i = 1; i < collector.length; i++) {// 0 not used
		if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
			return;
		}
		if(checkFlagBS.get(i)){
			continue;
		}
		checkFlagBS.set(i);
		Vector<Integer> holderV = new Vector<Integer>();
		holderV.add(i);
		if(collector[i] != null){
			holderV.addAll(collector[i]);
		}
		int checkIndex = 0;
		//
		boolean[] holderVContainsFlag = new boolean[collector.length];
		for (int j = 0; j < holderV.size(); j++) {
			holderVContainsFlag[holderV.elementAt(j)] = true;
		}
		//
		while(true){
			if(collector[holderV.elementAt(checkIndex)] != null){
				Vector<Integer> newLinksV = collector[holderV.elementAt(checkIndex)];
				for (int j = 0; j < newLinksV.size(); j++) {
					if(!checkFlagBS.get(newLinksV.elementAt(j)) && 
						!holderVContainsFlag[newLinksV.elementAt(j)]
//						!holderV.contains(newLinksV.elementAt(j))
					){
						holderV.add(newLinksV.elementAt(j));
						holderVContainsFlag[newLinksV.elementAt(j)] = true;
					}
				}
			}
			checkFlagBS.set(holderV.elementAt(checkIndex));
			checkIndex+= 1;
			if(checkIndex == holderV.size()){
				break;
			}
		}
		regionsV.add(holderV);
		if(regionsV.size() > 0x0000FFFF){//unsigned short max, must match getShortEncodedRegionIndexImage()
			throw new ImageException("Error: image segmentation contains more than "+(0x0000FFFF)+" distinct regions.");
		}
		int regionSize = 0;
		byte pixelCheck = regionImagePixelV.elementAt(holderV.elementAt(0));
		for (int j = 0; j < holderV.size(); j++) {
			if(pixelCheck != regionImagePixelV.elementAt(holderV.elementAt(j))){
				throw new ImageException("Linked regions have different image pixel values");
			}
			
			linkRegionMap[holderV.elementAt(j)] = regionsV.size()-1;
//			System.out.print((j!=0?" ":"")+holderV.elementAt(j));
			regionSize+= regionSizeArr[holderV.elementAt(j)];
			totalSize+= regionSizeArr[holderV.elementAt(j)];
		}
		regionsSizeV.add(regionSize);
//		System.out.println();
	}
	if(totalSize != vcImage.getNumXYZ()){
		throw new ImageException("Accumulated regions size does not equal image size");
	}
	//System.out.println("----------gather link distinct regions time "+((System.currentTimeMillis()-startTime)/1000.0));
	//startTime = System.currentTimeMillis();
	
//	//Create bitmasks of distinct regions
//	BitSet[] regionBitMaskBS = new BitSet[regionsV.size()];
//	for (int i = 0; i < regionBitMaskBS.length; i++) {
//		regionBitMaskBS[i] = new BitSet(regionPixels.length);
//	}
//	for (int i = 0; i < regionPixels.length; i++) {
//		regionBitMaskBS[linkRegionMap[regionPixels[i]]].set(i);
//	}
//	System.out.println("----------bitmask time "+((System.currentTimeMillis()-startTime)/1000.0));
//	startTime = System.currentTimeMillis();
	
	//Create RegionInfos
	regionInfos = new RegionInfo[regionsV.size()];
	for (int i = 0; i < regionsV.size(); i++) {
		regionInfos[i] =
			new RegionInfo(
					regionImagePixelV.elementAt(regionsV.elementAt(i).elementAt(0))&0x000000FF,
					regionsSizeV.elementAt(i),
					i,
					null//regionBitMaskBS[i]
				);
	}
	//System.out.println("----------regioninfo time "+((System.currentTimeMillis()-startTime)/1000.0));
	//startTime = System.currentTimeMillis();
	
	mapImageIndexToLinkRegion = regionPixels;
	mapLinkRegionToDistinctRegion = linkRegionMap;
//	mapImageIndexToRegionIndex = new CompactUnsignedIntStorage(regionPixels.length);
//	for (int i = 0; i < regionPixels.length; i++) {
//		mapImageIndexToRegionIndex.setValue(i, linkRegionMap[regionPixels[i]]);
//	}
//	regionPixels = null;
//	linkRegionMap = null;
	
	
	if(dimension != 0){
		generateSurfaceCollection(regionsV.size(),
				vcImage,//regionPixels,linkRegionMap,
				xSurfElements,ySurfElements,zSurfElements,
				dimension, extent, origin);
	}
	
	if (surfaceCollection != null && debug_bCheckPolygonQuality){
		verifyQuadVertexOrdering(debug_maxQuadAngle);
	}
	//System.out.println("----------create surface time "+((System.currentTimeMillis()-startTime)/1000.0));
	//startTime = System.currentTimeMillis();
	//Taubin smoothing
	if (surfaceCollection != null && filterCutoffFrequency<RegionImage.NO_SMOOTHING){
		TaubinSmoothing taubinSmoothing = new TaubinSmoothingWrong();
		TaubinSmoothingSpecification taubinSpec = TaubinSmoothingSpecification.getInstance(filterCutoffFrequency);
		taubinSmoothing.smooth(surfaceCollection,taubinSpec);
	}
	//System.out.println("----------smooth surface time "+((System.currentTimeMillis()-startTime)/1000.0));
	//startTime = System.currentTimeMillis();
	if (surfaceCollection != null && debug_bCheckPolygonQuality){
		verifyQuadVertexOrdering(debug_maxQuadAngle);
	}

//	System.out.println("Total Num Regions = "+regionsV.size());
//	System.out.println("Total Size = "+totalSize);
}

public void verifyQuadVertexOrdering(double maxAngleDegrees) {
	if (maxAngleDegrees>180 || maxAngleDegrees<0){
		throw new IllegalArgumentException("maxAngleDegrees must be between 0 and 180");
	}
	for (int s=0;s<surfaceCollection.getSurfaceCount();s++){
		Surface surface = surfaceCollection.getSurfaces(s);
		for (int p=0;p<surface.getPolygonCount();p++){
			Quadrilateral quad = (Quadrilateral)surface.getPolygons(p);
			
			// average the polygon vertices to get the center of the quad
			// this is also halfway between the coordinates of the inside and outside volume elements.
			cbit.vcell.geometry.surface.Node[] nodes = quad.getNodes();
			// have normal go in direction from low region index to high region index
			int lowVolumeIndex = quad.getVolIndexNeighbor1();
			int hiVolumeIndex = quad.getVolIndexNeighbor2();
			int lowRegionIndex = getRegionInfoFromOffset(quad.getVolIndexNeighbor1()).getRegionIndex();
			int hiRegionIndex = getRegionInfoFromOffset(quad.getVolIndexNeighbor2()).getRegionIndex();
			if (lowRegionIndex > hiRegionIndex){
				int temp = lowVolumeIndex;
				lowVolumeIndex = hiVolumeIndex;
				hiVolumeIndex = temp;
				temp = lowRegionIndex;
				lowRegionIndex = hiRegionIndex;
				hiRegionIndex = temp;
			}
			if (surface.getInteriorRegionIndex() != lowRegionIndex || surface.getExteriorRegionIndex() != hiRegionIndex){
				StringBuffer buffer = new StringBuffer();
				buffer.append("Surface interiorRegionIndex="+surface.getInteriorRegionIndex()+" and exteriorRegionIndex="+surface.getExteriorRegionIndex());
				buffer.append("Polygon lowRegionIndex="+lowRegionIndex+", hiRegionIndex="+hiRegionIndex);
				throw new RuntimeException("surface and polygon indices don't agree\n"+buffer.toString());
			}
			Vect3d v0 = new Vect3d(nodes[0].getX(),nodes[0].getY(),nodes[0].getZ());
			Vect3d v1 = new Vect3d(nodes[1].getX(),nodes[1].getY(),nodes[1].getZ());
			Vect3d v2 = new Vect3d(nodes[2].getX(),nodes[2].getY(),nodes[2].getZ());
			Vect3d v3 = new Vect3d(nodes[3].getX(),nodes[3].getY(),nodes[3].getZ());
			int volumeIndexNormalDiff = hiVolumeIndex - lowVolumeIndex;
			Vect3d v01 = Vect3d.sub(v1, v0);
			Vect3d v02 = Vect3d.sub(v2, v0);
			Vect3d unit012 = v01.cross(v02);
			unit012.unit();
			Vect3d v03 = Vect3d.sub(v3, v0);
			Vect3d unit023 = v02.cross(v03);
			unit023.unit();
			Vect3d gridNormal = null;
			if (volumeIndexNormalDiff==1){
				// y-z plane, normal is [1 0 0]
				gridNormal = new Vect3d(1,0,0);
			}else if (volumeIndexNormalDiff==-1){
				// y-z plane, normal is [-1 0 0]
				gridNormal = new Vect3d(-1,0,0);
			}else if (volumeIndexNormalDiff==getNumX()){
				// y-z plane, normal is [0 1 0]
				gridNormal = new Vect3d(0,1,0);
			}else if (volumeIndexNormalDiff==-getNumX()){
				// y-z plane, normal is [0 -1 0]
				gridNormal = new Vect3d(0,-1,0);
			}else if (volumeIndexNormalDiff==getNumX()*getNumY()){
				// y-z plane, normal is [0 0 1]
				gridNormal = new Vect3d(0,0,1);
			}else if (volumeIndexNormalDiff==-getNumX()*getNumY()){
				// y-z plane, normal is [0 0 -1]
				gridNormal = new Vect3d(0,0,-1);
			}
			if (this.filterCutoffFrequency<NO_SMOOTHING){
				// after smoothing ... should point in general direction (<90 degrees).
				if (unit012.dot(unit023)<Math.cos(maxAngleDegrees/180.0*Math.PI)){
					StringBuffer buffer = new StringBuffer();
					buffer.append("normal_012 = ["+unit012.getX()+" "+unit012.getY()+" "+unit012.getZ()+"]\n");
					buffer.append("normal_023 = ["+unit023.getX()+" "+unit023.getY()+" "+unit023.getZ()+"]\n");
					buffer.append("gridNormal = ["+gridNormal.getX()+" "+gridNormal.getY()+" "+gridNormal.getZ()+"]\n");
					throw new RuntimeException("quad("+p+") on surface("+s+"): two triangles from same quad (norm1.dot(norm2)="+unit012.dot(unit023)+") are > "+maxAngleDegrees+" degrees or inner product < "+Math.cos(maxAngleDegrees/180.0*Math.PI)+":\n"+buffer.toString());
				}else if (unit012.dot(gridNormal)<Math.cos(maxAngleDegrees/180.0*Math.PI)){
					StringBuffer buffer = new StringBuffer();
					buffer.append("normal_012 = ["+unit012.getX()+" "+unit012.getY()+" "+unit012.getZ()+"]\n");
					buffer.append("normal_023 = ["+unit023.getX()+" "+unit023.getY()+" "+unit023.getZ()+"]\n");
					buffer.append("gridNormal = ["+gridNormal.getX()+" "+gridNormal.getY()+" "+gridNormal.getZ()+"]\n");
					throw new RuntimeException("quad("+p+") on surface("+s+"): quad normal compared with grid normal (norm.dot(gridNormal)="+unit012.dot(unit023)+") is > "+maxAngleDegrees+" degrees or inner product < "+Math.cos(maxAngleDegrees/180.0*Math.PI)+" from orginal staircase:\n"+buffer.toString());
				}else{
					//System.out.println("normals ok");
				}
			}else{
				// no smoothing ... both triangle normals must light up exactly
				if (Math.abs(unit012.dot(unit023)-1.0)>1e-8){
					StringBuffer buffer = new StringBuffer();
					buffer.append("two triangles contradicted themselves\n");
					buffer.append("normal_012 = ["+unit012.getX()+" "+unit012.getY()+" "+unit012.getZ()+"]\n");
					buffer.append("normal_023 = ["+unit023.getX()+" "+unit023.getY()+" "+unit023.getZ()+"]\n");
					buffer.append("gridNormal = ["+gridNormal.getX()+" "+gridNormal.getY()+" "+gridNormal.getZ()+"]\n");
					throw new RuntimeException("two triangles from same quad have normals that are in opposite directions:\n"+buffer.toString());
				}else if (Math.abs(unit012.dot(gridNormal)-1.0)>1e-8){
					StringBuffer buffer = new StringBuffer();
					buffer.append("normal_012 = ["+unit012.getX()+" "+unit012.getY()+" "+unit012.getZ()+"]\n");
					buffer.append("normal_023 = ["+unit023.getX()+" "+unit023.getY()+" "+unit023.getZ()+"]\n");
					buffer.append("gridNormal = ["+gridNormal.getX()+" "+gridNormal.getY()+" "+gridNormal.getZ()+"]\n");
					throw new RuntimeException("triangles contradict grid normal:\n"+buffer.toString());
				}else{
					//System.out.println("normals ok");
				}

			}
		}
	}	
}


//if(idxDiff == 1)
//{
//	meptr[meloop].neighborMEIndex[0] = orthoIndex(meloop, idxlo,idxhi,mex,NEIGHBOR_YP_BOUNDARY);
//	meptr[meloop].neighborMEIndex[1] = orthoIndex(meloop, idxlo,idxhi,-mex*mey,NEIGHBOR_ZM_BOUNDARY);
//	meptr[meloop].neighborMEIndex[2] = orthoIndex(meloop, idxlo,idxhi,-mex,NEIGHBOR_YM_BOUNDARY);
//	meptr[meloop].neighborMEIndex[3] = orthoIndex(meloop, idxlo,idxhi,mex*mey,NEIGHBOR_ZP_BOUNDARY);
//}
//else if (idxDiff == mex)
//{
//	meptr[meloop].neighborMEIndex[0] = orthoIndex(meloop, idxlo,idxhi,1,NEIGHBOR_XP_BOUNDARY);
//	meptr[meloop].neighborMEIndex[1] = orthoIndex(meloop, idxlo,idxhi,-mex*mey,NEIGHBOR_ZM_BOUNDARY);
//	meptr[meloop].neighborMEIndex[2] = orthoIndex(meloop, idxlo,idxhi,-1,NEIGHBOR_XM_BOUNDARY);
//	meptr[meloop].neighborMEIndex[3] = orthoIndex(meloop, idxlo,idxhi,mex*mey,NEIGHBOR_ZP_BOUNDARY);
//}
//else if (idxDiff == (mex*mey))
//{
//	meptr[meloop].neighborMEIndex[0] = orthoIndex(meloop, idxlo,idxhi,mex,NEIGHBOR_YP_BOUNDARY);
//	meptr[meloop].neighborMEIndex[1] = orthoIndex(meloop, idxlo,idxhi,-1,NEIGHBOR_XM_BOUNDARY);
//	meptr[meloop].neighborMEIndex[2] = orthoIndex(meloop, idxlo,idxhi,-mex,NEIGHBOR_YM_BOUNDARY);
//	meptr[meloop].neighborMEIndex[3] = orthoIndex(meloop, idxlo,idxhi,1,NEIGHBOR_XP_BOUNDARY);
//}

public static boolean DEBUG = false;
private class SortEdgesLikeCplusplus implements Comparator<MembraneEdgeNeighbor>{
	private MembraneElementIdentifier.PerpendicularTo perpendicularTo;
	private final MembraneEdgeNeighbor bne = new MembraneEdgeNeighbor(-1,-1);//Both Not unchanging in this dimension flag
	private final MembraneEdgeNeighbor noop = new MembraneEdgeNeighbor(-1,-1);//Not used in this plane flag
	@Override
	public int compare(MembraneEdgeNeighbor o1, MembraneEdgeNeighbor o2) {
		cbit.vcell.geometry.surface.Node o1BaseNode = surfaceCollection.getNodes(o1.edgeBaseNodeIndex);
		cbit.vcell.geometry.surface.Node o1Node = surfaceCollection.getNodes(o1.edgeOtherNodeIndex);
		cbit.vcell.geometry.surface.Node o2BaseNode = surfaceCollection.getNodes(o2.edgeBaseNodeIndex);
		cbit.vcell.geometry.surface.Node o2Node = surfaceCollection.getNodes(o2.edgeOtherNodeIndex);
		boolean ux1 = Math.abs(o1BaseNode.getX()-o1Node.getX()) < 1e-6;
		boolean ux2 = Math.abs(o2BaseNode.getX()-o2Node.getX()) < 1e-6;
		MembraneEdgeNeighbor ux = (perpendicularTo != MembraneElementIdentifier.PerpendicularTo.X?(ux1?(ux2?null:o1):(ux2?o2:bne)):noop);
		boolean uy1 = Math.abs(o1BaseNode.getY()-o1Node.getY()) < 1e-6;
		boolean uy2 = Math.abs(o2BaseNode.getY()-o2Node.getY()) < 1e-6;
		MembraneEdgeNeighbor uy = (perpendicularTo != MembraneElementIdentifier.PerpendicularTo.Y?(uy1?(uy2?null:o1):(uy2?o2:bne)):noop);
		boolean uz1 = Math.abs(o1BaseNode.getZ()-o1Node.getZ()) < 1e-6;
		boolean uz2 = Math.abs(o2BaseNode.getZ()-o2Node.getZ()) < 1e-6;
		MembraneEdgeNeighbor uz = (perpendicularTo != MembraneElementIdentifier.PerpendicularTo.Z?(uz1?(uz2?null:o1):(uz2?o2:bne)):noop);
		if(perpendicularTo == MembraneElementIdentifier.PerpendicularTo.X){
			if(DEBUG){System.out.println(o1.getMasterPolygonIndex()+" "+o1.edgeBaseNodeIndex+" "+o1BaseNode+" "+o1Node+" "+o2.getMasterPolygonIndex()+" "+o2.edgeBaseNodeIndex+" "+o2BaseNode+" "+o2Node+" ux"+(ux==bne?"bne":(ux==noop?"noop":(ux==o1?"o1":"o2")))+" uy"+(uy==bne?"bne":(uy==noop?"noop":(uy==o1?"o1":"o2")))+" uz"+(uz==bne?"bne":(uz==noop?"noop":(uz==o1?"o1":"o2"))));};
			if(uy == null){int val=(int) -Math.signum(o1BaseNode.getY()-o2BaseNode.getY());if(DEBUG){System.out.println("type=4 sort="+val);};return val;}
			if(uz == null){int val=(int) Math.signum(o1BaseNode.getZ()-o2BaseNode.getZ());if(DEBUG){System.out.println("type=5 sort="+val);};return val;}
			double uyVal = surfaceCollection.getNodes(uy.edgeBaseNodeIndex).getY();
			double uyMaxZ = Math.max(surfaceCollection.getNodes(uy.edgeBaseNodeIndex).getZ(), surfaceCollection.getNodes(uy.edgeOtherNodeIndex).getZ());
			double uzVal = surfaceCollection.getNodes(uz.edgeBaseNodeIndex).getZ();
			double uzMaxY = Math.max(surfaceCollection.getNodes(uz.edgeBaseNodeIndex).getY(), surfaceCollection.getNodes(uz.edgeOtherNodeIndex).getY());
			boolean uyEqualMaxY = Math.abs((uyVal-uzMaxY)) < 1e-6;
			boolean uzEqualMaxZ = Math.abs((uzVal-uyMaxZ)) < 1e-6;
			if(uyEqualMaxY && !uzEqualMaxZ){int val=(uy==o1?-1:1);if(DEBUG){System.out.println("type=0 sort="+val);};return val;}
			else if(uyEqualMaxY && uzEqualMaxZ){int val=(uy==o1?-1:1);if(DEBUG){System.out.println("type=1 sort="+val);};return val;}
			else if(!uyEqualMaxY && !uzEqualMaxZ){int val=(uy==o1?1:-1);if(DEBUG){System.out.println("type=2 sort="+val);};return val;}
			else {int val=(uy==o1?-1:1);if(DEBUG){System.out.println("type=3 sort="+val);};return val;}
		}else if(perpendicularTo == MembraneElementIdentifier.PerpendicularTo.Y){
			if(DEBUG){System.out.println(o1.getMasterPolygonIndex()+" "+o1.edgeBaseNodeIndex+" "+o1BaseNode+" "+o1Node+" "+o2.getMasterPolygonIndex()+" "+o2.edgeBaseNodeIndex+" "+o2BaseNode+" "+o2Node+" ux"+(ux==bne?"bne":(ux==noop?"noop":(ux==o1?"o1":"o2")))+" uy"+(uy==bne?"bne":(uy==noop?"noop":(uy==o1?"o1":"o2")))+" uz"+(uz==bne?"bne":(uz==noop?"noop":(uz==o1?"o1":"o2"))));};
			if(ux == null){int val=(int) -Math.signum(o1BaseNode.getX()-o2BaseNode.getX());if(DEBUG){System.out.println("type=5 sort="+val);};return val;}
			if(uz == null){int val=(int) Math.signum(o1BaseNode.getZ()-o2BaseNode.getZ());if(DEBUG){System.out.println("type=4 sort="+val);};return val;}
			double uxVal = surfaceCollection.getNodes(ux.edgeBaseNodeIndex).getX();
			double uxMaxZ = Math.max(surfaceCollection.getNodes(ux.edgeBaseNodeIndex).getZ(), surfaceCollection.getNodes(ux.edgeOtherNodeIndex).getZ());
			double uzVal = surfaceCollection.getNodes(uz.edgeBaseNodeIndex).getZ();
			double uzMaxX = Math.max(surfaceCollection.getNodes(uz.edgeBaseNodeIndex).getX(), surfaceCollection.getNodes(uz.edgeOtherNodeIndex).getX());
			boolean uxEqualMaxX = Math.abs((uxVal-uzMaxX)) < 1e-6;
			boolean uzEqualMaxZ = Math.abs((uzVal-uxMaxZ)) < 1e-6;
			if(uzEqualMaxZ && !uxEqualMaxX){int val=(uz==o1?1:-1);if(DEBUG){System.out.println("type=0 sort="+val);};return val;}
			else if(uzEqualMaxZ && uxEqualMaxX){int val=(uz==o1?1:-1);if(DEBUG){System.out.println("type=1 sort="+val);};return val;}
			else if(!uzEqualMaxZ && !uxEqualMaxX){int val=(uz==o1?-1:1);if(DEBUG){System.out.println("type=2 sort="+val);};return val;}
			else {int val=(uz==o1?1:-1);if(DEBUG){System.out.println("type=3 sort="+val);};return val;}
		}else if(perpendicularTo == MembraneElementIdentifier.PerpendicularTo.Z){
			if(DEBUG){System.out.println(o1.getMasterPolygonIndex()+" "+o1.edgeBaseNodeIndex+" "+o1BaseNode+" "+o1Node+" "+o2.getMasterPolygonIndex()+" "+o2.edgeBaseNodeIndex+" "+o2BaseNode+" "+o2Node+" ux"+(ux==bne?"bne":(ux==noop?"noop":(ux==o1?"o1":"o2")))+" uy"+(uy==bne?"bne":(uy==noop?"noop":(uy==o1?"o1":"o2")))+" uz"+(uz==bne?"bne":(uz==noop?"noop":(uz==o1?"o1":"o2"))));};
			if(uy == null){int val=(int) -Math.signum(o1BaseNode.getY()-o2BaseNode.getY());if(DEBUG){System.out.println("type=4 sort="+val);};return val;}
			if(ux == null){int val=(int) Math.signum(o1BaseNode.getX()-o2BaseNode.getX());if(DEBUG){System.out.println("type=5 sort="+val);};return val;}
			double uyVal = surfaceCollection.getNodes(uy.edgeBaseNodeIndex).getY();
			double uyMaxX = Math.max(surfaceCollection.getNodes(uy.edgeBaseNodeIndex).getX(), surfaceCollection.getNodes(uy.edgeOtherNodeIndex).getX());
			double uxVal = surfaceCollection.getNodes(ux.edgeBaseNodeIndex).getX();
			double uxMaxY = Math.max(surfaceCollection.getNodes(ux.edgeBaseNodeIndex).getY(), surfaceCollection.getNodes(ux.edgeOtherNodeIndex).getY());
			boolean uyEqualMaxY = Math.abs((uyVal-uxMaxY)) < 1e-6;
			boolean uxEqualMaxX = Math.abs((uxVal-uyMaxX)) < 1e-6;
			if(uyEqualMaxY && !uxEqualMaxX){int val=(uy==o1?-1:1);if(DEBUG){System.out.println("type=0 sort="+val);};return val;}
			else if(uyEqualMaxY && uxEqualMaxX){int val=(uy==o1?-1:1);if(DEBUG){System.out.println("type=1 sort="+val);};return val;}
			else if(!uyEqualMaxY && !uxEqualMaxX){int val=(uy==o1?1:-1);if(DEBUG){System.out.println("type=2 sort="+val);};return val;}
			else {int val=(uy==o1?-1:1);if(DEBUG){System.out.println("type=3 sort="+val);};return val;}
		}
		return 0;
	}
	public void setPerpendicularAxis(MembraneElementIdentifier.PerpendicularTo parallelTo){
		this.perpendicularTo = parallelTo;
	}
}


private ArrayList<MembraneEdgeNeighbor>[/*surface*/][/*membraneelement*/] membraneEdgeNeighbors;
public ArrayList<MembraneEdgeNeighbor>[][] getMembraneEdgeNeighbors(){
	return membraneEdgeNeighbors;
}
private int[][] remapQuadIndexes;
private void calculateNeighbors(){
	//Find neighbors
	SortEdgesLikeCplusplus sortEdgesLikeCplusplus = new SortEdgesLikeCplusplus();
	membraneEdgeNeighbors = new ArrayList[surfaceCollection.getSurfaceCount()][];
	for(int surfindex = 0;surfindex<surfaceCollection.getSurfaceCount();surfindex++){
		membraneEdgeNeighbors[surfindex] = new ArrayList[surfaceCollection.getSurfaces(surfindex).getPolygonCount()];
		for(int wantNeighborsOfThisQuadUnRemappedIndex=0;wantNeighborsOfThisQuadUnRemappedIndex<surfaceCollection.getSurfaces(surfindex).getPolygonCount();wantNeighborsOfThisQuadUnRemappedIndex++){
			if(DEBUG){System.out.println("neighbors surf="+surfindex+" quad="+wantNeighborsOfThisQuadUnRemappedIndex+" quadmaster="+remapQuadIndexes[surfindex][wantNeighborsOfThisQuadUnRemappedIndex]);}
			membraneEdgeNeighbors[surfindex][wantNeighborsOfThisQuadUnRemappedIndex] = new ArrayList<>();
			Quadrilateral wantNieghborsOfThisQuad = (Quadrilateral)surfaceCollection.getSurfaces(surfindex).getPolygons(wantNeighborsOfThisQuadUnRemappedIndex);
			int wantQuadNodeCount = wantNieghborsOfThisQuad.getNodeCount();
			for(int wantedQuadNodeIndex=0;wantedQuadNodeIndex<wantQuadNodeCount;wantedQuadNodeIndex++){
				int edgeNodePoint1 = wantNieghborsOfThisQuad.getNodes()[wantedQuadNodeIndex].getGlobalIndex();
				int edgeNodePoint2 = wantNieghborsOfThisQuad.getNodes()[(wantedQuadNodeIndex==(wantQuadNodeCount-1)?0:wantedQuadNodeIndex+1)].getGlobalIndex();
				if(edgeNodePoint1 > edgeNodePoint2){
					edgeNodePoint1 = edgeNodePoint2;
					edgeNodePoint2 = wantNieghborsOfThisQuad.getNodes()[wantedQuadNodeIndex].getGlobalIndex();
				}
				TreeSet<MembraneElementIdentifier> neighborsSharingThisEdge = edgeMap.get(edgeNodePoint1).get(edgeNodePoint2);
				Iterator<MembraneElementIdentifier> neighborsOfEdgeIter = neighborsSharingThisEdge.iterator();
				boolean bEdgeHasNeighbors = false;
				while(neighborsOfEdgeIter.hasNext()){
					MembraneElementIdentifier neighborOfEdge = neighborsOfEdgeIter.next();
					//add neighbors on my surface (exclude edge share from other surfaces) and exclude myself from neighbor list
					if(neighborOfEdge.surfaceIndex == surfindex){
						if(neighborOfEdge.nonMasterPolygonIndex != wantNeighborsOfThisQuadUnRemappedIndex){
							if(neighborsSharingThisEdge.size() > 2 &&(getRegionInfoFromOffset(((Quadrilateral)surfaceCollection.getSurfaces(neighborOfEdge.surfaceIndex).getPolygons(neighborOfEdge.nonMasterPolygonIndex)).getVolIndexNeighbor2()).regionIndex == getRegionInfoFromOffset(wantNieghborsOfThisQuad.getVolIndexNeighbor2()).regionIndex &&
									((Quadrilateral)surfaceCollection.getSurfaces(neighborOfEdge.surfaceIndex).getPolygons(neighborOfEdge.nonMasterPolygonIndex)).getVolIndexNeighbor2() != wantNieghborsOfThisQuad.getVolIndexNeighbor2())){
								//do nothing
//								Quadrilateral q2 =
//								(Quadrilateral)surfaceCollection.getSurfaces(surfindex).getPolygons(neighborOfEdge.nonMasterPolygonIndex);
//								System.out.println("removed "+remapQuadIndexes[neighborOfEdge.surfaceIndex][neighborOfEdge.nonMasterPolygonIndex]+" "+q2);
							}else{
								bEdgeHasNeighbors = true;
								membraneEdgeNeighbors[surfindex][wantNeighborsOfThisQuadUnRemappedIndex].add(
									new MembraneEdgeNeighbor(neighborOfEdge,remapQuadIndexes[neighborOfEdge.surfaceIndex][neighborOfEdge.nonMasterPolygonIndex],edgeNodePoint1, edgeNodePoint2));
							}
						}else{
							sortEdgesLikeCplusplus.setPerpendicularAxis(neighborOfEdge.planePerpendicularToAxis);
						}
					}
				}
				if(!bEdgeHasNeighbors){
					//Add empty edge neighbor
					membraneEdgeNeighbors[surfindex][wantNeighborsOfThisQuadUnRemappedIndex].add(new MembraneEdgeNeighbor(edgeNodePoint1,edgeNodePoint2));
				}
			}
			
//			//Fix ambiguous
//			Iterator<Entry<Integer, HashMap<Integer, ArrayList<MembraneEdgeNeighbor>>>> iterator1 = foundNeighborsByEdge.entrySet().iterator();
//			while(iterator1.hasNext()){
//				Entry<Integer, HashMap<Integer, ArrayList<MembraneEdgeNeighbor>>> entry1 = iterator1.next();
//				Iterator<Entry<Integer, ArrayList<MembraneEdgeNeighbor>>> iterator2 = entry1.getValue().entrySet().iterator();
//				while(iterator2.hasNext()){
//					Entry<Integer, ArrayList<MembraneEdgeNeighbor>> entry2 = iterator2.next();
//					if(entry2.getValue().size() > 2){
//						ArrayList<MembraneEdgeNeighbor> removethese = new ArrayList<>();
//						for(int i=0;i<entry2.getValue().size();i++){
//							MembraneEdgeNeighbor membraneEdgeNeighbor = entry2.getValue().get(i);
//							Quadrilateral q2 =
//								(Quadrilateral)surfaceCollection.getSurfaces(membraneEdgeNeighbor.getMembraneElementIdentifier().surfaceIndex).getPolygons(membraneEdgeNeighbor.getMembraneElementIdentifier().nonMasterPolygonIndex);
//							if(DEBUG){
//							System.out.println(
//									wantNeighborsOfThisQuadUnRemappedIndex+" "+wantNieghborsOfThisQuad.getVolIndexNeighbor2()+" "+getRegionInfoFromOffset(wantNieghborsOfThisQuad.getVolIndexNeighbor2()).regionIndex+" "+
//									membraneEdgeNeighbor.getMembraneElementIdentifier().nonMasterPolygonIndex+" q2="+
//									q2.getVolIndexNeighbor1()+" "+getRegionInfoFromOffset(q2.getVolIndexNeighbor1()).regionIndex+" "+
//									q2.getVolIndexNeighbor2()+" "+getRegionInfoFromOffset(q2.getVolIndexNeighbor2()).regionIndex+" "+
//									surfaceCollection.getNodes()[membraneEdgeNeighbor.edgeBaseNodeIndex]+" "+surfaceCollection.getNodes()[membraneEdgeNeighbor.edgeOtherNodeIndex]);
//							}
//							if(getRegionInfoFromOffset(q2.getVolIndexNeighbor2()).regionIndex == getRegionInfoFromOffset(wantNieghborsOfThisQuad.getVolIndexNeighbor2()).regionIndex &&
//									q2.getVolIndexNeighbor2() != wantNieghborsOfThisQuad.getVolIndexNeighbor2()){
//								if(DEBUG){System.out.println("remove "+membraneEdgeNeighbor.getMembraneElementIdentifier().nonMasterPolygonIndex);}
//								removethese.add(membraneEdgeNeighbor);
//							}
//						}
//						for(int i=0;i<removethese.size();i++){
//							membraneEdgeNeighbors[surfindex][wantNeighborsOfThisQuadUnRemappedIndex].remove(removethese.get(i));
//						}
//						if(DEBUG){System.out.println("");}
////						System.out.println("Quad "+wantNeighborsOfThisQuadUnRemappedIndex+" has Edge "+entry1.getKey()+" "+entry2.getKey()+" with "+entry2.getValue().size()+" neighbors");
//					}
//				}
//			}

			Collections.sort(membraneEdgeNeighbors[surfindex][wantNeighborsOfThisQuadUnRemappedIndex], sortEdgesLikeCplusplus);
		}
	}

}

public static class MembraneEdgeNeighbor {
	private MembraneElementIdentifier membraneElementIdentifier;
	private int masterPolygonIndex;
	public int edgeBaseNodeIndex;
	public int edgeOtherNodeIndex;
	public MembraneEdgeNeighbor(MembraneElementIdentifier membraneElementIdentifier,int masterPolygonIndex, int edgeBaseNodeIndex,int edgeOtherNodeIndex) {
		this(edgeBaseNodeIndex,edgeOtherNodeIndex);
		this.membraneElementIdentifier = membraneElementIdentifier;
		this.masterPolygonIndex = masterPolygonIndex;
	}
	public MembraneEdgeNeighbor( int edgeBaseNodeIndex,int edgeOtherNodeIndex){
		this.edgeBaseNodeIndex = edgeBaseNodeIndex;
		this.edgeOtherNodeIndex = edgeOtherNodeIndex;
		this.masterPolygonIndex = MembraneElement.NEIGHBOR_UNDEFINED;
	}
	public MembraneElementIdentifier getMembraneElementIdentifier(){
		return membraneElementIdentifier;
	}
	public int getMasterPolygonIndex() {
		return masterPolygonIndex;
	}
}

private void generateSurfaceCollection(int numRegions,
		VCImage vcImage,//int[] mapImageIndexToLinkRegion,int[] mapLinkRegionToDistinctRegion,
		BitSet xSurfElements,BitSet ySurfElements,BitSet zSurfElements,
		int dimension,Extent extent,Origin origin)
{
	bMembraneNeighborCalculationFailed = false;
	int masterIndex = 0;
	double dX = extent.getX() / (vcImage.getNumX() -1);
	double dY = extent.getY() / (vcImage.getNumY() -1);
	double dZ = extent.getZ() / (vcImage.getNumZ() -1);
	//
	double loX = origin.getX() - 0.5 * dX;
	double loY = origin.getY() - 0.5 * dY;
	double loZ = origin.getZ() - 0.5 * dZ;

	Vector<Vector<Quadrilateral>> surfQuadsV = new Vector<Vector<Quadrilateral>>();
	int[][] mapInsideOutsideToSurface = new int[numRegions][];
	Vector<cbit.vcell.geometry.surface.Node> nodeListV = new Vector<cbit.vcell.geometry.surface.Node>();
	cbit.vcell.geometry.surface.Node[][][] mapImageIndexToNode =
		new cbit.vcell.geometry.surface.Node[2][vcImage.getNumY()+1][vcImage.getNumX()+1];
	double zComp,zpComp;
	double yComp,ypComp;
	double xComp,xpComp;
	final int yStep = vcImage.getNumX();
	final int zStep = vcImage.getNumX()*vcImage.getNumY();
	boolean bMvXm,bMvYm,bMvZm;
	boolean bMvXp,bMvYp,bMvZp;
	double xmEdge = origin.getX();
	double xpEdge = origin.getX() + extent.getX();
	double ymEdge = origin.getY();
	double ypEdge = origin.getY() + extent.getY();
	double zmEdge = origin.getZ();
	double zpEdge = origin.getZ() + extent.getZ();
	for (int zIndex = 0; zIndex < vcImage.getNumZ(); zIndex++) {
		mapImageIndexToNode[0] = mapImageIndexToNode[1];
		mapImageIndexToNode[1] = new cbit.vcell.geometry.surface.Node[vcImage.getNumY()+1][vcImage.getNumX()+1];
		zComp = loZ + zIndex*dZ;
		zpComp = zComp+dZ;
		bMvZm = (zIndex != 0);
		bMvZp = (zIndex != (vcImage.getNumZ()-1));
		for (int yIndex = 0; yIndex < vcImage.getNumY(); yIndex++) {
			yComp = loY+yIndex*dY;
			ypComp = yComp+dY;
			bMvYm = (yIndex != 0);
			bMvYp = (yIndex != (vcImage.getNumY()-1));
			for (int xIndex = 0; xIndex < vcImage.getNumX(); xIndex++) {
				if(xSurfElements.get(masterIndex)){
					bMvXp = (xIndex != (vcImage.getNumX()-1));
					xpComp = loX+(xIndex+1)*dX;
					cbit.vcell.geometry.surface.Node[] nodeArr = new cbit.vcell.geometry.surface.Node[4];
					nodeArr[0] = mapImageIndexToNode[0][yIndex][xIndex+1];
					if(nodeArr[0] == null){
//						nodeArr[0] = new cbit.vcell.geometry.surface.Node(xpComp,yComp,zComp);
						nodeArr[0] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXp?xpComp:xpEdge),(bMvYm?yComp:ymEdge),(bMvZm?zComp:zmEdge));
						nodeArr[0].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[0]);
						nodeArr[0].setMoveX(bMvXp);
						nodeArr[0].setMoveY(bMvYm);
						nodeArr[0].setMoveZ(bMvZm);
						mapImageIndexToNode[0][yIndex][xIndex+1] = nodeArr[0];
					}
					nodeArr[1] = mapImageIndexToNode[0][yIndex+1][xIndex+1];
					if(nodeArr[1] == null){
//						nodeArr[1] = new cbit.vcell.geometry.surface.Node(xpComp,ypComp,zComp);
						nodeArr[1] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXp?xpComp:xpEdge),(bMvYp?ypComp:ypEdge),(bMvZm?zComp:zmEdge));
						nodeArr[1].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[1]);
						nodeArr[1].setMoveX(bMvXp);
						nodeArr[1].setMoveY(bMvYp);
						nodeArr[1].setMoveZ(bMvZm);
						mapImageIndexToNode[0][yIndex+1][xIndex+1] = nodeArr[1];
					}
					nodeArr[2] = mapImageIndexToNode[1][yIndex+1][xIndex+1];
					if(nodeArr[2] == null){
//						nodeArr[2] = new cbit.vcell.geometry.surface.Node(xpComp,ypComp,zpComp);
						nodeArr[2] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXp?xpComp:xpEdge),(bMvYp?ypComp:ypEdge),(bMvZp?zpComp:zpEdge));
						nodeArr[2].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[2]);
						nodeArr[2].setMoveX(bMvXp);
						nodeArr[2].setMoveY(bMvYp);
						nodeArr[2].setMoveZ(bMvZp);
						mapImageIndexToNode[1][yIndex+1][xIndex+1] = nodeArr[2];
					}
					nodeArr[3] = mapImageIndexToNode[1][yIndex][xIndex+1];
					if(nodeArr[3] == null){
//						nodeArr[3] = new cbit.vcell.geometry.surface.Node(xpComp,yComp,zpComp);
						nodeArr[3] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXp?xpComp:xpEdge),(bMvYm?yComp:ymEdge),(bMvZp?zpComp:zpEdge));
						nodeArr[3].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[3]);
						nodeArr[3].setMoveX(bMvXp);
						nodeArr[3].setMoveY(bMvYm);
						nodeArr[3].setMoveZ(bMvZp);
						mapImageIndexToNode[1][yIndex][xIndex+1] = nodeArr[3];
					}
					addQuadToSurface(surfQuadsV, mapInsideOutsideToSurface,
//							masterIndex,mapImageIndexToRegionIndex.getValue(masterIndex),
//							masterIndex+1,mapImageIndexToRegionIndex.getValue(masterIndex+1),
							masterIndex,mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex]],
							masterIndex+1,mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex+1]],
							nodeArr, numRegions,MembraneElementIdentifier.PerpendicularTo.X);
//					surfQuad =
//						new Quadrilateral(nodeArr,
//								mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex]],
//								mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex+1]]);
//					if(surfQuad.getVolIndexNeighbor1() > surfQuad.getVolIndexNeighbor2()){
//						surfQuad.reverseDirection();
//					}
//					if(mapInsideOutsideToSurface[surfQuad.getVolIndexNeighbor1()] == null){
//						mapInsideOutsideToSurface[surfQuad.getVolIndexNeighbor1()] = new int[numRegions];
//						Arrays.fill(mapInsideOutsideToSurface[surfQuad.getVolIndexNeighbor1()], -1);
//					}
//					if(mapInsideOutsideToSurface[surfQuad.getVolIndexNeighbor1()][surfQuad.getVolIndexNeighbor2()] == -1){
//						mapInsideOutsideToSurface[surfQuad.getVolIndexNeighbor1()][surfQuad.getVolIndexNeighbor2()] = surfQuadsV.size();
//						surfQuadsV.add(new Vector<Quadrilateral>());
//					}
//					surfQuadsV.elementAt(mapInsideOutsideToSurface[surfQuad.getVolIndexNeighbor1()][surfQuad.getVolIndexNeighbor2()]).add(surfQuad);
				}
				if(ySurfElements.get(masterIndex)){
					bMvXm = (xIndex != 0);
					bMvXp = (xIndex != (vcImage.getNumX()-1));
					xComp = loX+xIndex*dX;
					xpComp = loX+(xIndex+1)*dX;
					cbit.vcell.geometry.surface.Node[] nodeArr = new cbit.vcell.geometry.surface.Node[4];
					nodeArr[0] = mapImageIndexToNode[0][yIndex+1][xIndex+1];
					if(nodeArr[0] == null){
//						nodeArr[0] = new cbit.vcell.geometry.surface.Node(xpComp,ypComp,zComp);
						nodeArr[0] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXp?xpComp:xpEdge),(bMvYp?ypComp:ypEdge),(bMvZm?zComp:zmEdge));
						nodeArr[0].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[0]);
						nodeArr[0].setMoveX(bMvXp);
						nodeArr[0].setMoveY(bMvYp);
						nodeArr[0].setMoveZ(bMvZm);
						mapImageIndexToNode[0][yIndex+1][xIndex+1] = nodeArr[0];
					}
					nodeArr[1] = mapImageIndexToNode[0][yIndex+1][xIndex];
					if(nodeArr[1] == null){
//						nodeArr[1] = new cbit.vcell.geometry.surface.Node(xComp,ypComp,zComp);
						nodeArr[1] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXm?xComp:xmEdge),(bMvYp?ypComp:ypEdge),(bMvZm?zComp:zmEdge));
						nodeArr[1].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[1]);
						nodeArr[1].setMoveX(bMvXm);
						nodeArr[1].setMoveY(bMvYp);
						nodeArr[1].setMoveZ(bMvZm);
						mapImageIndexToNode[0][yIndex+1][xIndex] = nodeArr[1];
					}
					nodeArr[2] = mapImageIndexToNode[1][yIndex+1][xIndex];
					if(nodeArr[2] == null){
//						nodeArr[2] = new cbit.vcell.geometry.surface.Node(xComp,ypComp,zpComp);
						nodeArr[2] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXm?xComp:xmEdge),(bMvYp?ypComp:ypEdge),(bMvZp?zpComp:zpEdge));
						nodeArr[2].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[2]);
						nodeArr[2].setMoveX(bMvXm);
						nodeArr[2].setMoveY(bMvYp);
						nodeArr[2].setMoveZ(bMvZp);
						mapImageIndexToNode[1][yIndex+1][xIndex] = nodeArr[2];
					}
					nodeArr[3] = mapImageIndexToNode[1][yIndex+1][xIndex+1];
					if(nodeArr[3] == null){
//						nodeArr[3] = new cbit.vcell.geometry.surface.Node(xpComp,ypComp,zpComp);
						nodeArr[3] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXp?xpComp:xpEdge),(bMvYp?ypComp:ypEdge),(bMvZp?zpComp:zpEdge));
						nodeArr[3].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[3]);
						nodeArr[3].setMoveX(bMvXp);
						nodeArr[3].setMoveY(bMvYp);
						nodeArr[3].setMoveZ(bMvZp);
						mapImageIndexToNode[1][yIndex+1][xIndex+1] = nodeArr[3];
					}
					addQuadToSurface(surfQuadsV, mapInsideOutsideToSurface,
//							masterIndex,mapImageIndexToRegionIndex.getValue(masterIndex),
//							masterIndex+yStep,mapImageIndexToRegionIndex.getValue(masterIndex+yStep),
							masterIndex,mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex]],
							masterIndex+yStep,mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex+yStep]],
							nodeArr, numRegions,MembraneElementIdentifier.PerpendicularTo.Y);

//					surfQuad =
//						new Quadrilateral(nodeArr,
//								mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex]],
//								mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex+yStep]]);
//					if(surfQuad.getVolIndexNeighbor1() > surfQuad.getVolIndexNeighbor2()){
//						surfQuad.reverseDirection();
//					}
//					surfQuadsV[surfQuad.getVolIndexNeighbor1()].add(surfQuad);
				}
				if(zSurfElements.get(masterIndex)){
					bMvXm = (xIndex != 0);
					bMvXp = (xIndex != (vcImage.getNumX()-1));
					xComp = loX+xIndex*dX;
					xpComp = loX+(xIndex+1)*dX;
					cbit.vcell.geometry.surface.Node[] nodeArr = new cbit.vcell.geometry.surface.Node[4];
					nodeArr[0] = mapImageIndexToNode[1][yIndex][xIndex+1];
					if(nodeArr[0] == null){
//						nodeArr[0] = new cbit.vcell.geometry.surface.Node(xpComp,yComp,zpComp);
						nodeArr[0] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXp?xpComp:xpEdge),(bMvYm?yComp:ymEdge),(bMvZp?zpComp:zpEdge));
						nodeArr[0].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[0]);
						nodeArr[0].setMoveX(bMvXp);
						nodeArr[0].setMoveY(bMvYm);
						nodeArr[0].setMoveZ(bMvZp);
						mapImageIndexToNode[1][yIndex][xIndex+1] = nodeArr[0];
					}
					nodeArr[1] = mapImageIndexToNode[1][yIndex+1][xIndex+1];
					if(nodeArr[1] == null){
//						nodeArr[1] = new cbit.vcell.geometry.surface.Node(xpComp,ypComp,zpComp);
						nodeArr[1] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXp?xpComp:xpEdge),(bMvYp?ypComp:ypEdge),(bMvZp?zpComp:zpEdge));
						nodeArr[1].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[1]);
						nodeArr[1].setMoveX(bMvXp);
						nodeArr[1].setMoveY(bMvYp);
						nodeArr[1].setMoveZ(bMvZp);
						mapImageIndexToNode[1][yIndex+1][xIndex+1] = nodeArr[1];
					}
					nodeArr[2] = mapImageIndexToNode[1][yIndex+1][xIndex];
					if(nodeArr[2] == null){
//						nodeArr[2] = new cbit.vcell.geometry.surface.Node(xComp,ypComp,zpComp);
						nodeArr[2] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXm?xComp:xmEdge),(bMvYp?ypComp:ypEdge),(bMvZp?zpComp:zpEdge));
						nodeArr[2].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[2]);
						nodeArr[2].setMoveX(bMvXm);
						nodeArr[2].setMoveY(bMvYp);
						nodeArr[2].setMoveZ(bMvZp);
						mapImageIndexToNode[1][yIndex+1][xIndex] = nodeArr[2];
					}
					nodeArr[3] = mapImageIndexToNode[1][yIndex][xIndex];
					if(nodeArr[3] == null){
//						nodeArr[3] = new cbit.vcell.geometry.surface.Node(xComp,yComp,zpComp);
						nodeArr[3] =
							new cbit.vcell.geometry.surface.Node(
								(bMvXm?xComp:xmEdge),(bMvYm?yComp:ymEdge),(bMvZp?zpComp:zpEdge));
						nodeArr[3].setGlobalIndex(nodeListV.size());
						nodeListV.add(nodeArr[3]);
						nodeArr[3].setMoveX(bMvXm);
						nodeArr[3].setMoveY(bMvYm);
						nodeArr[3].setMoveZ(bMvZp);
						mapImageIndexToNode[1][yIndex][xIndex] = nodeArr[3];
					}
					addQuadToSurface(surfQuadsV, mapInsideOutsideToSurface,
//							masterIndex,mapImageIndexToRegionIndex.getValue(masterIndex),
//							masterIndex+zStep,mapImageIndexToRegionIndex.getValue(masterIndex+zStep),
							masterIndex,mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex]],
							masterIndex+zStep,mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex+zStep]],
							nodeArr, numRegions,MembraneElementIdentifier.PerpendicularTo.Z);

//					surfQuad =
//						new Quadrilateral(nodeArr,
//								mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex]],
//								mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[masterIndex+zStep]]);
//					if(surfQuad.getVolIndexNeighbor1() > surfQuad.getVolIndexNeighbor2()){
//						surfQuad.reverseDirection();
//					}
//					surfQuadsV[surfQuad.getVolIndexNeighbor1()].add(surfQuad);
				}
				masterIndex+= 1;
			}
		}
	}
	remapQuadIndexes = new int[surfQuadsV.size()][];
	int quadCounter = 0;
	surfaceCollection = new SurfaceCollection();
	cbit.vcell.geometry.surface.Node[] allNodes = new cbit.vcell.geometry.surface.Node[nodeListV.size()];
	nodeListV.copyInto(allNodes);
	surfaceCollection.setNodes(allNodes);
	for (int surfaceIndex = 0; surfaceIndex < surfQuadsV.size(); surfaceIndex++) {
		Vector<Quadrilateral> surfV = surfQuadsV.elementAt(surfaceIndex);
		remapQuadIndexes[surfaceIndex] = new int[surfV.size()];
		OrigSurface surface =
			new OrigSurface(
//					mapImageIndexToRegionIndex.getValue(surfV.elementAt(0).getVolIndexNeighbor1()),//surfV.elementAt(0).getVolIndexNeighbor1(),
//					mapImageIndexToRegionIndex.getValue(surfV.elementAt(0).getVolIndexNeighbor2())//surfV.elementAt(0).getVolIndexNeighbor2()
					mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[surfV.elementAt(0).getVolIndexNeighbor1()]],//surfV.elementAt(0).getVolIndexNeighbor1(),
					mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[surfV.elementAt(0).getVolIndexNeighbor2()]]//surfV.elementAt(0).getVolIndexNeighbor2()
				);
		for (int faceIndex = 0; faceIndex < surfV.size(); faceIndex++) {
			surface.addPolygon(surfV.elementAt(faceIndex));
			remapQuadIndexes[surfaceIndex][faceIndex] = quadCounter;
			quadIndexToSurfAndFace.add(new SurfAndFace(surfaceIndex,faceIndex));
			quadCounter++;
		}
		surfaceCollection.addSurface(surface);
	}
	
	try{
		if(!bMembraneNeighborCalculationFailed){
			calculateNeighbors();
			surfaceCollection.setMembraneEdgeNeighbors(membraneEdgeNeighbors);
		}
	}catch(Exception e){
		//If MembraneNeighbors fails somewhere let Surfacecollection generate without membraneneighbors (original behavior just in case)
		e.printStackTrace();
		bMembraneNeighborCalculationFailed = true;
	}finally{
		//these aren't needed after fail or surfacecollection.set(...)
		remapQuadIndexes = null;
		edgeMap = null;
	}
	
//	RegionImage.sortSurfaceCollection(surfaceCollection);
//	//check
//	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++) {
//		Surface surface = surfaceCollection.getSurfaces(i);
//		for (int j = 0; j < surface.getPolygonCount(); j++) {
//			Polygon polygon = surface.getPolygons(j);
//			for (int k = 0; k < polygon.getNodes().length; k++) {
//				if(surfaceCollection.getNodes()[polygon.getNodes()[k].getGlobalIndex()] != polygon.getNodes()[k]){
//					throw new RuntimeException("Nodes not match");
//				}
//			}
//		}
//	}
}

private boolean bMembraneNeighborCalculationFailed = false;
public static class MembraneElementIdentifier {
	public enum PerpendicularTo {X,Y,Z};
	public int surfaceIndex;
	public int nonMasterPolygonIndex;
	public PerpendicularTo planePerpendicularToAxis;
	public MembraneElementIdentifier(int surfaceIndex, int nonMasterPolygonIndex,PerpendicularTo planePerpendicularToAxis) {
		this.surfaceIndex = surfaceIndex;
		this.nonMasterPolygonIndex = nonMasterPolygonIndex;
		this.planePerpendicularToAxis = planePerpendicularToAxis;
	}
}
private HashMap<Integer, HashMap<Integer, TreeSet<MembraneElementIdentifier>>> edgeMap = new HashMap<>();

private void addQuadToSurface(
		Vector<Vector<Quadrilateral>> surfQuadsV,
		int[][] mapInsideOutsideToSurface,
		int indexNeighbor1,int neighbor1,
		int indexNeighbor2,int neighbor2,
		cbit.vcell.geometry.surface.Node[] nodeArr,int numRegions,MembraneElementIdentifier.PerpendicularTo plane){
	
	Quadrilateral surfQuad =
		new Quadrilateral(nodeArr,indexNeighbor1,indexNeighbor2);
	if(neighbor1 > neighbor2){
		surfQuad.reverseDirection();
		int temp = neighbor1;
		neighbor1 = neighbor2;
		neighbor2 = temp;
	}
	if(mapInsideOutsideToSurface[neighbor1] == null){
		mapInsideOutsideToSurface[neighbor1] = new int[numRegions];
		Arrays.fill(mapInsideOutsideToSurface[neighbor1], -1);
	}
	if(mapInsideOutsideToSurface[neighbor1][neighbor2] == -1){
		mapInsideOutsideToSurface[neighbor1][neighbor2] = surfQuadsV.size();
		surfQuadsV.add(new Vector<Quadrilateral>());
	}
	int surfIndex = mapInsideOutsideToSurface[neighbor1][neighbor2];
	surfQuadsV.elementAt(surfIndex).add(surfQuad);
	
	try{
		if(!bMembraneNeighborCalculationFailed){
			updateEdgeMap(nodeArr, surfIndex, surfQuadsV.elementAt(surfIndex).size()-1,plane);
		}
	}catch(Exception e){
		e.printStackTrace();
		bMembraneNeighborCalculationFailed = true;
	}
}
public SurfaceCollection getSurfacecollection(){
	return surfaceCollection;
}
public ArrayList<SurfAndFace> getQuadIndexToSurfAndFace(){
	return (quadIndexToSurfAndFace==null || quadIndexToSurfAndFace.size()==0?null:quadIndexToSurfAndFace);
}

private Comparator<MembraneElementIdentifier> membraneElementIDComparator = new Comparator<RegionImage.MembraneElementIdentifier>() {
	@Override
	public int compare(MembraneElementIdentifier o1,MembraneElementIdentifier o2) {
		return (int)(o1.surfaceIndex==o2.surfaceIndex?Math.signum(o1.nonMasterPolygonIndex-o2.nonMasterPolygonIndex):Math.signum(o1.surfaceIndex-o2.surfaceIndex));
	}
};
private void updateEdgeMap(cbit.vcell.geometry.surface.Node[] polygonNodes,int surfaceIndex,int nonMasterPolygonIndex,MembraneElementIdentifier.PerpendicularTo plane){
	for(int i=0;i<polygonNodes.length;i++){
		int globalNodeIndex1 = polygonNodes[i].getGlobalIndex();
		int globalNodeIndex2 = polygonNodes[(i==(polygonNodes.length-1)?0:i+1)].getGlobalIndex();
		if(globalNodeIndex1 > globalNodeIndex2){
			globalNodeIndex1 = globalNodeIndex2;
			globalNodeIndex2 = polygonNodes[i].getGlobalIndex();
		}
		HashMap<Integer, TreeSet<MembraneElementIdentifier>> membraneElementIDHash = edgeMap.get(globalNodeIndex1);
		if(membraneElementIDHash == null){
			membraneElementIDHash = new HashMap<Integer, TreeSet<MembraneElementIdentifier>>();
			edgeMap.put(globalNodeIndex1, membraneElementIDHash);
		}
		TreeSet<MembraneElementIdentifier> membraneElementsHavingThisEdge = membraneElementIDHash.get(globalNodeIndex2);
		if(membraneElementsHavingThisEdge == null){
			membraneElementsHavingThisEdge = new TreeSet<MembraneElementIdentifier>(membraneElementIDComparator);
			membraneElementIDHash.put(globalNodeIndex2, membraneElementsHavingThisEdge);
		}
		membraneElementsHavingThisEdge.add(new MembraneElementIdentifier(surfaceIndex, nonMasterPolygonIndex,plane));
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 10:30:20 AM)
 * @param image cbit.image.VCImage
 */
private static RegionMask[] calculateRegions3D(byte[] imageArray, int sliceOffset, int numX, int numY) {
	
	Vector regionMaskList = new Vector();
	//
	// initialize all regions to region "NOT_VISITED" (not yet visited)
	//
	int[] regionIndexes = new int[numX*numY];
	int numXY = numX*numY;
	for (int index = 0; index < numXY; index++){
		regionIndexes[index] = NOT_VISITED;
	}

	int regionId = 0;
	for(int sliceIndex=0; sliceIndex<numXY; sliceIndex++){
		//
		// look for index of next region (not visited)
		//
		while((sliceIndex<numXY) && (regionIndexes[sliceIndex]!=NOT_VISITED)){
			if(regionIndexes[sliceIndex] == DEPTH_END_SEED){
				throw new RuntimeException("Unexpected DepthEndSeed");
			}
			sliceIndex++;
		}
		if(sliceIndex<numXY){	
			//
			// create new region
			//
			int seedIndex = sliceIndex;
			int regionSize = 0;
			RegionMask regionMask = new RegionMask(numXY,imageArray[sliceOffset+seedIndex],regionId,sliceOffset);
			FloodFill2D floodFill2D = new RegionImage.FloodFill2D(imageArray,regionIndexes,regionId,sliceOffset,numX,numY,regionMask);
			//
			// fill all of the "islands" (portions of this contiguous region that can be created by the max-recursion constraint)
			//
			while (seedIndex>=0){
				//
				// mark "fill" region (but return if max-recursion achived --- and leave islands marked by "DEPTH_END_SEED")
				//
				int i = (seedIndex)%numX;
				int j = (seedIndex)/numX;
				
				regionSize += floodFill2D.fill(i,j,seedIndex);
				
				//
				// Islands can be created when terminating the Depth-limited algorithm
				// A DEPTH_END_SEED marks the volume elements of all terminated paths
				// that were not re-visited.  All islands will have at least one
				// DEPTH_END_SEED on the border.
				//
				// re-seed the algorithm on the next island (DEPTH_END_SEED) found.
				//
				// if no more islands, then seedIndex = -1
				//
				seedIndex = -1;
//				for (j=i;j<numXY;j++){
				for (j=sliceIndex;j<numXY;j++){
					if (regionIndexes[j]==DEPTH_END_SEED){
						seedIndex = j;
						break;
					}
				}
			}
			regionMaskList.add(regionMask);
			regionId++;
		}
	}
			
	return (RegionMask[])org.vcell.util.BeanUtils.getArray(regionMaskList,RegionImage.RegionMask.class);
}


/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 10:30:20 AM)
 * @param image cbit.image.VCImage
 */
private static RegionMask[] calculateRegions3Dfaster(byte[] imageArray, int sliceOffset, int numX, int numY) {
	
	Vector regionMaskList = new Vector();
	//
	// initialize all regions to region "NOT_VISITED" (not yet visited)
	//
	int[] regionIndexes = new int[numX*numY];
	int numXY = numX*numY;
	for (int index = 0; index < numXY; index++){
		regionIndexes[index] = NOT_VISITED;
	}

	int regionId = 0;
	for(int seedIndex=0; seedIndex<numXY; seedIndex++){
		//
		// look for index of next region (not visited)
		//
		while((seedIndex<numXY) && (regionIndexes[seedIndex]!=NOT_VISITED)){
			seedIndex++;
		}
		if(seedIndex<numXY){	
			//
			// create new region
			//
			RegionMask regionMask = new RegionMask(numXY,imageArray[sliceOffset+seedIndex],regionId,sliceOffset);
			FloodFill2DLine floodFill2D = new RegionImage.FloodFill2DLine(imageArray,regionIndexes,regionId,sliceOffset,numX,numY,regionMask);
			//
			// fill next contiguous region
			//
			int i = (seedIndex)%numX;
			int j = (seedIndex)/numX;
			
			int regionSize = floodFill2D.fill(i,j);
				
			regionMaskList.add(regionMask);
			regionId++;
		}
	}
			
	return (RegionMask[])org.vcell.util.BeanUtils.getArray(regionMaskList,RegionImage.RegionMask.class);
}


/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 4:59:12 PM)
 * @return int
 */
public int getNumRegions() {
	return regionInfos.length;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumX() {
	return numX;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumXY() {
	return numXY;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumY() {
	return numY;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getNumZ() {
	return numZ;
}

/**
 * This method was created in VisualAge.
 * @return byte
 * @param x int
 * @param y int
 * @param z int
 */
public RegionInfo getRegionInfoFromOffset(int offset) throws IndexOutOfBoundsException {
	//if (x<0||x>=numX||y<0||y>=numY||z<0||z>=numZ){
		//throw new IndexOutOfBoundsException("("+x+","+y+","+z+") is outside (0,0,0) and ("+(numX-1)+","+(numY-1)+","+(numZ-1)+")");
	//}
	for (int i = 0; i < regionInfos.length; i++){
		if (regionInfos[i].isIndexInRegion(offset)){
			return regionInfos[i];
		}
	}
	throw new RuntimeException("pixel("+offset+") not assigned to a region");
}


/**
 * This method was created in VisualAge.
 * @return byte
 * @param x int
 * @param y int
 * @param z int
 */
public RegionInfo[] getRegionInfos() {
	return (RegionInfo[])regionInfos.clone();
}

public double getFilterCutoffFrequency(){
	return filterCutoffFrequency;
}

/**
 * Insert the method's description here.
 * Creation date: (12/18/00 2:31:07 PM)
 * @return java.lang.String
 */
public String toString() {
	return "RegionImage@"+Integer.toHexString(hashCode())+"("+getNumX()+","+getNumY()+","+getNumZ()+")";
}
}
