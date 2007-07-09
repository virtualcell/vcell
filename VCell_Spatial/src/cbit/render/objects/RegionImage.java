package cbit.render.objects;
import cbit.util.graph.Edge;
import cbit.util.graph.Graph;
import cbit.util.graph.Tree;
import cbit.util.graph.Node;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.Serializable;
import java.util.*;

public class RegionImage implements Serializable {
	private int numX = 0;
	private int numY = 0;
	private int numZ = 0;
	private int numXY = 0;
	private RegionInfo regionInfos[] = null;
	private final static int NOT_VISITED = -1;
	private final static int DEPTH_END_SEED = -2;

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
			return mask.get(index);
		}
		public String toString(){
			return "RegionInfo(regionIndex="+regionIndex+", numPixel="+numPixels+", imageValue="+pixelValue+")";
		}
	};

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

/*©
	 * (C) Copyright University of Connecticut Health Center 2001.
	 * All rights reserved.
	©*/
	/**
	 * This type was created in VisualAge.
	 */
	private static class ObjectReferenceWrapper {
		private Object internalObject = null;
	/**
	 * InsertObjectWrapper constructor comment.
	 */
	public ObjectReferenceWrapper(Object object) {
		if (object == null){
			throw new IllegalArgumentException("null object");
		}
		this.internalObject = object;
	}
	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param object java.lang.Object
	 */
	public boolean equals(Object object) {
		if (object instanceof ObjectReferenceWrapper){
			return (internalObject == ((ObjectReferenceWrapper)object).internalObject);
		}else{
			return false;
		}
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 */
	public Object getObject() {
		return internalObject;
	}
	/**
	 * This method was created in VisualAge.
	 * @return int
	 */
	public int hashCode() {
		//
		// equals() has been overridden to support reference equates
		// hashCode should also be overridden, but as long as it gives the
		// same value every time (immutability), it will work. 
		// because equals() will only match the same object.
		//
		return internalObject.hashCode();  // either uses Object.hashCode or based on value 
	}
	}

/**
 * This method was created in VisualAge.
 * @param pix byte[]
 * @param x int
 * @param y int
 * @param z int
 * @param name java.lang.String
 * @param annot java.lang.String
 */
public RegionImage(ByteImage vcImage) throws ImageException {
	this.numX = vcImage.getNumX();
	this.numY = vcImage.getNumY();
	this.numZ = vcImage.getNumZ();
	this.numXY = numX*numY;
	calculateRegions(vcImage);
}


/**
 * Insert the method's description here.
 * Creation date: (3/28/2002 10:30:20 AM)
 * @param image cbit.image.VCImage
 */
private void calculateRegions(ByteImage vcImage) throws ImageException {

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
			Node node = new Node(k+","+i, new ObjectReferenceWrapper(regionMasks[k][i]));
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
				for (j=i;j<numXY;j++){
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
public int getOffset(int x, int y, int z) {
	//if (x<0||x>=numX||y<0||y>=numY||z<0||z>=numZ){
		//throw new IndexOutOfBoundsException("("+x+","+y+","+z+") is outside (0,0,0) and ("+(numX-1)+","+(numY-1)+","+(numZ-1)+")");
	//}
	return x + y*numX + z*numXY;
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
public RegionInfo getRegionInfo(int x, int y, int z) throws IndexOutOfBoundsException {
	//if (x<0||x>=numX||y<0||y>=numY||z<0||z>=numZ){
		//throw new IndexOutOfBoundsException("("+x+","+y+","+z+") is outside (0,0,0) and ("+(numX-1)+","+(numY-1)+","+(numZ-1)+")");
	//}
	int index = x + y*numX + z*numXY;
	for (int i = 0; i < regionInfos.length; i++){
		if (regionInfos[i].mask.get(index)){
			return regionInfos[i];
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
		if (regionInfos[i].mask.get(offset)){
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


/**
 * Insert the method's description here.
 * Creation date: (12/18/00 2:31:07 PM)
 * @return java.lang.String
 */
public String toString() {
	return "RegionImage@"+Integer.toHexString(hashCode())+"("+getNumX()+","+getNumY()+","+getNumZ()+")";
}
}