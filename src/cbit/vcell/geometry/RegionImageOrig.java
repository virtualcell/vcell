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
import java.util.*;
import cbit.image.VCImage;

public class RegionImageOrig implements Serializable {
	private int numX = 0;
	private int numY = 0;
	private int numZ = 0;
	private int numXY = 0;
	private int regionIndexes[] = null;
	private RegionInfo regionInfos[] = null;
	private final static int NOT_VISITED = -1;
	private final static int DEPTH_END_SEED = -2;

	public class RegionInfo implements Serializable {
		private int pixelValue;
		private int numPixels;
		private int regionIndex;
		
		RegionInfo(int argPixelValue, int argNumPixels, int argRegionIndex){
			pixelValue = argPixelValue;
			numPixels = argNumPixels;
			regionIndex = argRegionIndex;
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
		public String toString(){
			return "RegionInfo(regionIndex="+regionIndex+", numPixel="+numPixels+", imageValue="+pixelValue+")";
		}
	};

	private class FloodFill {
		private byte pixelValues[] = null;
		private int regionIndex = -1;
		private final int MAX_DEPTH = 10;
		
		FloodFill(byte argPixelValues[], int argRegionIndex){
			pixelValues = argPixelValues;
			regionIndex = argRegionIndex;
		}
		
		int fill(int xIndex, int yIndex, int zIndex, int index){
			return fill0(xIndex, yIndex, zIndex, index, 0);
		}
			
		private int fill0(int xIndex, int yIndex, int zIndex, int index, int depth){
if (index != xIndex + yIndex*numX + zIndex*numXY){
	throw new RuntimeException("index for x="+xIndex+", y="+yIndex+", z="+zIndex+" is "+index);
}
			int count = 1;
			if (depth>MAX_DEPTH){
				regionIndexes[index] = RegionImageOrig.DEPTH_END_SEED;
				return 0;
			}else{
				regionIndexes[index] = regionIndex;
			}
			if ((xIndex+1 < numX) && (regionIndexes[index+1] < 0) && (pixelValues[index] == pixelValues[index+1])){
				count += fill0(xIndex+1,yIndex,zIndex,index+1,depth+1);
			}
			if ((xIndex-1 >= 0) && (regionIndexes[index-1] < 0) && (pixelValues[index] == pixelValues[index-1])){
				count += fill0(xIndex-1,yIndex,zIndex,index-1,depth+1);
			}
			if ((yIndex+1 < numY) && (regionIndexes[index+numX] < 0) && (pixelValues[index] == pixelValues[index+numX])){
				count += fill0(xIndex,yIndex+1,zIndex,index+numX,depth+1);
			}
			if ((yIndex-1 >= 0) && (regionIndexes[index-numX] < 0) && (pixelValues[index] == pixelValues[index-numX])){
				count += fill0(xIndex,yIndex-1,zIndex,index-numX,depth+1);
			}
			if ((zIndex+1 < numZ) && (regionIndexes[index+numXY] < 0) && (pixelValues[index] == pixelValues[index+numXY])){
				count += fill0(xIndex,yIndex,zIndex+1,index+numXY,depth+1);
			}
			if ((zIndex-1 >= 0) && (regionIndexes[index-numXY] < 0) && (pixelValues[index] == pixelValues[index-numXY])){
				count += fill0(xIndex,yIndex,zIndex-1,index-numXY,depth+1);
			}
			return count;
		}
	};
/**
 * This method was created in VisualAge.
 * @param pix byte[]
 * @param x int
 * @param y int
 * @param z int
 * @param name java.lang.String
 * @param annot java.lang.String
 */
public RegionImageOrig(VCImage vcImage) throws cbit.image.ImageException {
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
private void calculateRegions(VCImage vcImage) throws cbit.image.ImageException {

	Vector regionInfoList = new Vector();
	//
	// initialize all regions to region "NOT_VISITED" (not yet visited)
	//
	int numVolume = getNumX()*getNumY()*getNumZ();
	regionIndexes = new int[getNumX()*getNumY()*getNumZ()];
	for (int index = 0; index < regionIndexes.length; index++){
		regionIndexes[index] = NOT_VISITED;
	}

	byte pixelValues[] = vcImage.getPixels();
	int regionId = 0;
	for(int index=0; index<numVolume; index++){
		//
		// look for index of next region (not visited)
		//
		while((index<numVolume) && (regionIndexes[index]!=NOT_VISITED)){
			index++;
		}
		if(index<numVolume){	
			//
			// create new region
			//
			int seedIndex = index;
			int regionSize = 0;
			FloodFill floodFill = new RegionImageOrig.FloodFill(pixelValues,regionId);
			//
			// fill all of the "islands" (portions of this contiguous region that can be created by the max-recursion constraint)
			//
			while (seedIndex>=0){
				//
				// mark "fill" region (but return if max-recursion achived --- and leave islands marked by "DEPTH_END_SEED")
				//
				int i = seedIndex%getNumX();
				int j = (seedIndex/getNumX())%getNumY();
				int k = seedIndex/(getNumX()*getNumY());
				
				regionSize += floodFill.fill(i,j,k,seedIndex);
				
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
				for (j=i;j<numVolume;j++){
					if (regionIndexes[j]==DEPTH_END_SEED){
						seedIndex = j;
						break;
					}
				}
			}
			regionInfoList.add(new RegionInfo(pixelValues[index],regionSize,regionId));			
			regionId++;
		}
	}
			
	regionInfos = new RegionInfo[regionInfoList.size()];
	regionInfoList.copyInto(regionInfos);
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
public int getRegionIndex(int index) throws IndexOutOfBoundsException {
	return regionIndexes[index];
}
/**
 * This method was created in VisualAge.
 * @return byte
 * @param x int
 * @param y int
 * @param z int
 */
public int getRegionIndex(int x, int y, int z) throws IndexOutOfBoundsException {
	if (x<0||x>=numX||y<0||y>=numY||z<0||z>=numZ){
		throw new IndexOutOfBoundsException("("+x+","+y+","+z+") is outside (0,0,0) and ("+(numX-1)+","+(numY-1)+","+(numZ-1)+")");
	}
	int index = x + y*numX + z*numXY; 
	return regionIndexes[index];
}
/**
 * This method was created in VisualAge.
 * @return byte
 * @param x int
 * @param y int
 * @param z int
 */
public RegionInfo getRegionInfo(int regionIndex) {
	for (int i = 0; i < regionInfos.length; i++){
		if (regionInfos[i].getRegionIndex() == regionIndex){
			return regionInfos[i];
		}
	}
	return null;
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
	return "RegionImageOrig@"+Integer.toHexString(hashCode())+"("+getNumX()+","+getNumY()+","+getNumZ()+")";
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
}
