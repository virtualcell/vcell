package cbit.vcell.geometry;
import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Tree;
import cbit.util.graph.Node;
import cbit.vcell.client.task.ClientTaskStatusSupport;
import cbit.vcell.geometry.surface.OrigSurface;
import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.Quadrilateral;
import cbit.vcell.geometry.surface.Surface;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.geometry.surface.TaubinSmoothing;
import cbit.vcell.geometry.surface.TaubinSmoothingSpecification;
import cbit.vcell.geometry.surface.TaubinSmoothingWrong;
import cbit.vcell.render.Vect3d;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.io.Serializable;
import java.util.*;

import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.ObjectReferenceWrapper;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.image.VCImage;

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
	this(vcImage,dimension,extent,origin,filterCutoffFrequency,null);}
public RegionImage(VCImage vcImage,int dimension,Extent extent,Origin origin,double filterCutoffFrequency,ClientTaskStatusSupport clientTaskStatusSupport) throws cbit.image.ImageException {
	this.numX = vcImage.getNumX();
	this.numY = vcImage.getNumY();
	this.numZ = vcImage.getNumZ();
	this.numXY = numX*numY;
	this.filterCutoffFrequency = filterCutoffFrequency;
	
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
private void calculateRegions_New(VCImage vcImage,int dimension,Extent extent, Origin origin,ClientTaskStatusSupport clientTaskStatusSupport) throws cbit.image.ImageException {

	long startTime = System.currentTimeMillis();
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
	
	if (surfaceCollection != null){
		correctQuadVertexOrdering();
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

//	System.out.println("Total Num Regions = "+regionsV.size());
//	System.out.println("Total Size = "+totalSize);
}

private void correctQuadVertexOrdering() {
	for (int s=0;s<surfaceCollection.getSurfaceCount();s++){
		Surface surface = surfaceCollection.getSurfaces(s);
		for (int p=0;p<surface.getPolygonCount();p++){
			Quadrilateral quad = (Quadrilateral)surface.getPolygons(p);
			
			// average the polygon vertices to get the center of the quad
			// this is also halfway between the coordinates of the inside and outside volume elements.
			cbit.vcell.geometry.surface.Node[] nodes = quad.getNodes();
			double centerx = (nodes[0].getX() + nodes[1].getX() + nodes[2].getX() + nodes[3].getX())/4.0;
			double centery = (nodes[0].getY() + nodes[1].getY() + nodes[2].getY() + nodes[3].getY())/4.0;
			double centerz = (nodes[0].getZ() + nodes[1].getZ() + nodes[2].getZ() + nodes[3].getZ())/4.0;
			// have normal go in direction from low region index to high region index
			int lowVolIndex = quad.getVolIndexNeighbor1();
			int hiVolIndex = quad.getVolIndexNeighbor2();
			if (getRegionInfoFromOffset(lowVolIndex).getRegionIndex() > getRegionInfoFromOffset(hiVolIndex).getRegionIndex()){
				int temp = lowVolIndex;
				lowVolIndex = hiVolIndex;
				hiVolIndex = temp;
			}
			Vect3d v0 = new Vect3d(nodes[0].getX(),nodes[0].getY(),nodes[0].getZ());
			Vect3d v1 = new Vect3d(nodes[1].getX(),nodes[1].getY(),nodes[1].getZ());
			Vect3d v2 = new Vect3d(nodes[2].getX(),nodes[2].getY(),nodes[2].getZ());
			Vect3d v3 = new Vect3d(nodes[3].getX(),nodes[3].getY(),nodes[3].getZ());
			int volNormalDiff = hiVolIndex - lowVolIndex;
			Vect3d v01 = Vect3d.sub(v1, v0);
			Vect3d v02 = Vect3d.sub(v2, v0);
			Vect3d unit012 = v01.cross(v02);
			unit012.unit();
			Vect3d v03 = Vect3d.sub(v3, v0);
			Vect3d unit023 = v02.cross(v03);
			unit023.unit();
			Vect3d gridNormal = null;
			if (volNormalDiff==1){
				// y-z plane, normal is [1 0 0]
				gridNormal = new Vect3d(1,0,0);
			}else if (volNormalDiff==-1){
				// y-z plane, normal is [-1 0 0]
				gridNormal = new Vect3d(-1,0,0);
			}else if (volNormalDiff==getNumX()){
				// y-z plane, normal is [0 1 0]
				gridNormal = new Vect3d(0,1,0);
			}else if (volNormalDiff==-getNumX()){
				// y-z plane, normal is [0 -1 0]
				gridNormal = new Vect3d(0,-1,0);
			}else if (volNormalDiff==getNumX()*getNumY()){
				// y-z plane, normal is [0 0 1]
				gridNormal = new Vect3d(0,0,1);
			}else if (volNormalDiff==-getNumX()*getNumY()){
				// y-z plane, normal is [0 0 -1]
				gridNormal = new Vect3d(0,0,-1);
			}
			if (Math.abs(unit012.dot(unit023)-1.0)>1e-8){
				System.out.println("");
				System.out.println("two triangles contradicted themselves");
				System.out.println("normal_012 = ["+unit012.getX()+" "+unit012.getY()+" "+unit012.getZ()+"]");
				System.out.println("normal_023 = ["+unit023.getX()+" "+unit023.getY()+" "+unit023.getZ()+"]");
				System.out.println("gridNormal = ["+gridNormal.getX()+" "+gridNormal.getY()+" "+gridNormal.getZ()+"]");
				System.out.println("");
			}else if (Math.abs(unit012.dot(gridNormal)-1.0)>1e-8){
				System.out.println("");
				System.out.println("triangles contradict grid normal");
				System.out.println("normal_012 = ["+unit012.getX()+" "+unit012.getY()+" "+unit012.getZ()+"]");
				System.out.println("normal_023 = ["+unit023.getX()+" "+unit023.getY()+" "+unit023.getZ()+"]");
				System.out.println("gridNormal = ["+gridNormal.getX()+" "+gridNormal.getY()+" "+gridNormal.getZ()+"]");
				System.out.println("");
			}else{
				System.out.println("normals ok");
			}
		}
	}	
}

private void generateSurfaceCollection(int numRegions,
		VCImage vcImage,//int[] mapImageIndexToLinkRegion,int[] mapLinkRegionToDistinctRegion,
		BitSet xSurfElements,BitSet ySurfElements,BitSet zSurfElements,
		int dimension,Extent extent,Origin origin)
{
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
							nodeArr, numRegions);
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
							nodeArr, numRegions);

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
							nodeArr, numRegions);

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
	surfaceCollection = new SurfaceCollection();
	cbit.vcell.geometry.surface.Node[] allNodes = new cbit.vcell.geometry.surface.Node[nodeListV.size()];
	nodeListV.copyInto(allNodes);
	surfaceCollection.setNodes(allNodes);
	for (int i = 0; i < surfQuadsV.size(); i++) {
		Vector<Quadrilateral> surfV = surfQuadsV.elementAt(i);
		OrigSurface surface =
			new OrigSurface(
//					mapImageIndexToRegionIndex.getValue(surfV.elementAt(0).getVolIndexNeighbor1()),//surfV.elementAt(0).getVolIndexNeighbor1(),
//					mapImageIndexToRegionIndex.getValue(surfV.elementAt(0).getVolIndexNeighbor2())//surfV.elementAt(0).getVolIndexNeighbor2()
					mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[surfV.elementAt(0).getVolIndexNeighbor1()]],//surfV.elementAt(0).getVolIndexNeighbor1(),
					mapLinkRegionToDistinctRegion[mapImageIndexToLinkRegion[surfV.elementAt(0).getVolIndexNeighbor2()]]//surfV.elementAt(0).getVolIndexNeighbor2()
				);
		for (int j = 0; j < surfV.size(); j++) {
			surface.addPolygon(surfV.elementAt(j));
		}
		surfaceCollection.addSurface(surface);
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

private void addQuadToSurface(
		Vector<Vector<Quadrilateral>> surfQuadsV,
		int[][] mapInsideOutsideToSurface,
		int indexNeighbor1,int neighbor1,
		int indexNeighbor2,int neighbor2,
		cbit.vcell.geometry.surface.Node[] nodeArr,int numRegions){
	
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
	surfQuadsV.elementAt(mapInsideOutsideToSurface[neighbor1][neighbor2]).add(surfQuad);
	
}
public SurfaceCollection getSurfacecollection(){
	return surfaceCollection;
}

//private int createSurfaceNode(
//		Vector nodeListV,int[] mapImageIndexToNodeListIndex,
//		int direction,int xSize,int xySize,
//		int xIndex,int yIndex,int zIndex,
//		double loX,double loY,double loZ,
//		double dX,double dY,double dZ)
//{
//	cbit.vcell.geometry.surface.Node surfNode;
//	if(direction == Coordinate.X_AXIS){
//		
//	}else if(direction == Coordinate.Y_AXIS){
//		
//	}else if(direction == Coordinate.Z_AXIS){
//		
//	}
//	return 0;
//}



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
private int getRegionIndex(int x, int y, int z) throws IndexOutOfBoundsException {
	if (x<0||x>=numX||y<0||y>=numY||z<0||z>=numZ){
		throw new IndexOutOfBoundsException("("+x+","+y+","+z+") is outside (0,0,0) and ("+(numX-1)+","+(numY-1)+","+(numZ-1)+")");
	}
	int index = x + y*numX + z*numXY;
	for (int i = 0; i < regionInfos.length; i++){
		if (regionInfos[i].mask.get(index)){
			return regionInfos[i].regionIndex;
		}
	}
	throw new RuntimeException("pixel("+x+","+y+","+z+") not assigned to a region");
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