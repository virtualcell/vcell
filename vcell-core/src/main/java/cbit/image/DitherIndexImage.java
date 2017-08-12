/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;

import java.util.Enumeration;
import java.util.Vector;
/**
 * This type was created in VisualAge.
 */
public class DitherIndexImage {

	private static int DONE_FLAG = 0x02;
	private static int CHANGE_FLAG = 0x01;
/**
 * BrowseImage constructor comment.
 */
public DitherIndexImage() {
	super();

}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
private static boolean bSolidBlock(byte[] destPixels, int destXSize, int destYSize, int xCenter, int yCenter, int blockRadius) {
	//
	final int xl = xCenter - blockRadius;
	final int xh = xCenter + blockRadius;
	final int yl = yCenter - blockRadius;
	final int yh = yCenter + blockRadius;
	if ((xl < 0) || (xh >= destXSize)) {
		return false;
	}
	if ((yl < 0) || (yh >= destYSize)) {
		return false;
	}
	byte middlePixel = destPixels[xCenter + (yCenter * destXSize)];
	int start = xl + yl * destXSize;
	for (int y = yl; y <= yh; y += 1) {
		int index = start;
		for (int x = xl; x <= xh; x += 1) {
			if (destPixels[index] != middlePixel) {
				return false;
			}
			index += 1;
		}
		start += destXSize;
	}
	return true;
}
/**
 * This method was created in VisualAge.
 */
public static VCImageUncompressed dither(byte[] pixels,int srcX,int srcY,int srcZ,int destXMax,int destYMax) throws ImageException {
/*Paste into scrapbook
//java.io.File fileIn = new java.io.File("d:\\temp\\ditherSrc.raw");
//java.io.FileInputStream fis = new java.io.FileInputStream(fileIn);
//byte[] pixels = new byte[(int)fileIn.length()];
//fis.read(pixels);
//fis.close();
int srcXSize = 64;
int srcYSize = 64;
int srcZSize = 255;
int srcPlaneSize = srcXSize*srcYSize;
int srcSize = srcPlaneSize*srcZSize;

byte[] pixels = new byte[srcSize];
int count = 0;
for(int z= 0;z<srcZSize;z+= 1){
for(int y= 0;y<srcYSize;y+= 1){
for(int x= 0;x<srcXSize;x+= 1){
if((x>32) &&(y>32) && (x<40) &&(y<40)){
pixels[count] = (byte)(z+1);
}else{
pixels[count] = 0;
}
count+= 1;
}
}
}

int destXSize = 64;
int destYSize = 64;
cbit.image.VCImageUncompressed vci = dither.DitherIndexImage.dither(pixels, srcXSize, srcYSize, srcZSize, destXSize, destYSize);
System.out.println("FinalX="+vci.getNumX()+" FinalY="+vci.getNumY());
java.io.File fileOut = new java.io.File("d:\\temp\\ditherDest.raw");
java.io.FileOutputStream fos = new java.io.FileOutputStream(fileOut);
fos.write(vci.getPixels());
fos.close();
System.exit(0);
End Paste into scrapbook*/

	final int srcYSize = srcX;
	final int srcZPlaneSize = srcX*srcY;

	//Calc Scaling parameters
	float srcScaleX = (float)destXMax/(float)srcX;
	float srcScaleY = (float)destYMax/(float)srcY;
	
	int destXSize;
	int destYSize;
	float srcStepX;
	float srcStepY;
	
	if(srcScaleX < srcScaleY){
		destXSize = destXMax;
		destYSize = (int)(srcScaleX*(float)srcY);
		srcStepX = (float)1.0/srcScaleX;
		srcStepY = (float)srcY/(float)destYSize;
	}
	else{
		destYSize = destYMax;
		destXSize = (int)(srcScaleY*(float)srcX);		
		srcStepX =  (float)srcX/(float)destXSize;
		srcStepY = (float)1.0/srcScaleY;
	}
	int destSize = destXSize*destYSize;
	byte destAllPixels[][] = new byte[destSize][];
	//End calc scaling parameters

//	System.out.println(	"srcX="+srcX+" srcY="+srcY+" destX="+destXMax+" destY="+destYMax+"\n"+
//						"desYSize = "+destYSize+"\n"+
//						"destXSize = "+destXSize+"\n"+
//						"srcStepX = "+srcStepX+"\n"+
//						"srcStepY = "+srcStepY);

	//Scale to dest Size and collect Pixels
	float currentDestX =0;
	float currentDestY = 0;
	float currentSrcXL = 0;
	float currentSrcYL = 0;
	int destInc = 0;
	do{
		float currentSrcXH  = currentSrcXL + srcStepX;
		float currentSrcYH  = currentSrcYL + srcStepY;

	//System.out.print("destInc="+destInc+" destSize="+destSize+" DestX="+currentDestX+" DestY="+currentDestY+" ");
	destAllPixels[destInc] = getAllPixels(pixels,srcYSize,srcZPlaneSize,srcZ,
												currentSrcXL,currentSrcYL,currentSrcXH,currentSrcYH);

//if(destAllPixels[destInc].length == 0){
//	System.out.println("destInc="+destInc+" destSize="+destSize+" DestX="+currentDestX+" DestY="+currentDestY+" ");
//	System.out.println("get all pixels len = 0 died at"+destInc);System.exit(1);
//}
		currentDestX += 1.0;
		currentSrcXL  += srcStepX;
		if(currentDestX == destXSize){
			currentDestX = 0;
			currentSrcXL = 0;
			currentDestY += 1.0;
			currentSrcYL  += srcStepY;
		}
		
		destInc+= 1;
	}while(destInc < destSize);

	//Get Unique Pixel Values from src
	Vector uniquePixels = new Vector();
	for(int c = 0;c<pixels.length;c+= 1){
		Byte bytePixel = new Byte(pixels[c]);
		if(!uniquePixels.contains(bytePixel)){
			//System.out.println("unique="+bytePixel);
			uniquePixels.addElement(bytePixel);
		}
	}
	boolean bPixelsChanged = false;
	boolean bDone = true;
	int resultFlag;
	int neighborOffset = 1;
	int scatterMax = 1;
	//System.out.println("Original");
	//printDestArray(destAllPixels,destXSize,destYSize,null);
	do{
	do{
		//Remove
		bPixelsChanged = false;
		for(int c = 0;c <uniquePixels.size();c+= 1){
			//System.out.println("Check pixel "+((Byte)uniquePixels.elementAt(c)).byteValue());
			resultFlag = removeCommon(destAllPixels,destXSize,destYSize,((Byte)uniquePixels.elementAt(c)).byteValue(),neighborOffset);
			bDone = ((resultFlag & DONE_FLAG) != 0);
			if(bDone){
				break;
			}
			boolean bRemovePixelsChanged = ((resultFlag & CHANGE_FLAG) != 0);
			if(bRemovePixelsChanged){
				bPixelsChanged = true;
			}
		}
			if(bDone){
				break;
			}
		if(!bPixelsChanged){
			if(neighborOffset < scatterMax){
				neighborOffset+= 1;
			}else{
				break;
			}
		}
		}while(true);
		
		//Scatter
		if(!bDone){
			do{
				//System.out.println("Scatter");
				resultFlag = scatter(destAllPixels,destXSize,destYSize,neighborOffset);
				bDone = ((resultFlag & DONE_FLAG) != 0);
				if(bDone){
					break;
				}
				boolean bScatterPixelsChanged = ((resultFlag & CHANGE_FLAG) != 0);
				if(bScatterPixelsChanged){
					neighborOffset = 1;
					break;
				}else{
					neighborOffset+= 1;
					if(neighborOffset > scatterMax){
						scatterMax = neighborOffset;
					}
				}
				//printDestArray(destAllPixels,destXSize,destYSize,null);
			}while(true);
		}
	}while(!bDone);

	//System.out.println("Scattered");
	//printDestArray(destAllPixels, destXSize, destYSize, null);
	byte[] destPixels = new byte[destAllPixels.length];
	for(int c= 0;c<destPixels.length;c+= 1){
		destPixels[c] = destAllPixels[c][0];
	}
	return new VCImageUncompressed(null,destPixels,new org.vcell.util.Extent(1,1,1),destXSize,destYSize,1);
/*
	//Make arrays of all the different src pixel values in the z plane for each x,y location
	for (int x = 0; x < xs; x += 1) {
		for (int y = 0; y < ys; y += 1) {
			int pixXYIndex = xs * y + x;
			int pixZIndex = pixXYIndex;
			byte allZPixels[] = new byte[zs];
			int allZPCount = 0;
			for (int z = 0; z < zs; z += 1) {
				byte zPixel = pixels[pixZIndex];
				histogram[((int)zPixel)&0x00ff]+= 1;
				boolean bHavePixel = false;
				for (int c = 0; c < allZPCount; c += 1) {
					if (zPixel == allZPixels[c]) {
						bHavePixel = true;
						break;
					}
					if (!bHavePixel) {
						allZPixels[allZPCount] = zPixel;
						allZPCount += 1;
					}
				}
				pixZIndex += srcPlaneSize;
			}
			diffPixels[pixXYIndex] = new byte[allZPCount];
			System.arraycopy(allZPixels, 0, diffPixels[pixXYIndex], 0, allZPCount);
		}
	}

	//Calc histogram of src pixels so we can use to calc dest pixel counts
	int srcMaxBin = 0;
	int srcMaxBinVal = 0;
	Hashtable srcBins = new Hashtable();
	for(int c=0;c<256;c+=1){
		if(histogram[c] > 0){
			srcBins.put(new Integer(((int)c)&0x00ff),new Integer(histogram[c]));
		if(histogram[c]>srcMaxBinVal){
			srcMaxBinVal = histogram[c];
			srcMaxBin = c;
		}
		}
	}

	
	//Calc how many destination pixels we need in proportion to src and normalize src histogram
	Hashtable normBins = new Hashtable();
	Hashtable destBins = new Hashtable();
	Enumeration enum1 = null;
	enum1 = srcBins.keys();
	//int destPixelCheckCount = 0;
	while(enum1.hasMoreElements()){
		Integer srcPixel = (Integer)enum1.nextElement();
		Integer srcPixelCount = (Integer)srcBins.get(srcPixel);
		float normPixelCount = (float)srcPixelCount.intValue()/(float)srcPlaneSize;
		normBins.put(srcPixel,new Float(normPixelCount));
		int destPixelCount = (int)(normPixelCount*destSize);
		//destPixelCheckCount+= destPixelCount;
		destBins.put(srcPixel,new Integer(destPixelCount));
	}
	
	//Make sure roundoff doesn't make the wrong total number of dest pixels,
	tryNewIdeaCheckSize(destBins,destSize);

	//Redistribute dest pixel count if some pixel counts too small when scaled proportional
	int minPixCount = (int)(((float)destSize)*.0035);
	while(true){
		boolean bAdjusted = false;
		enum1 = destBins.keys();
		while(enum1.hasMoreElements()){
			Integer destPixel = (Integer)enum1.nextElement();
			int destPixelCount = ((Integer)destBins.get(destPixel)).intValue();
			int diff = destPixelCount-minPixCount;
			if((diff < 0) && (destPixelCount < ((Integer)srcBins.get(destPixel)).intValue())){
				bAdjusted = true;
				Integer smallDestPixelCount = (Integer)destBins.remove(destPixel);
				destBins.put(destPixel,new Integer(minPixCount));
				tryNewIdeaAdjust(destPixel,diff,destBins,normBins,minPixCount,destSize);
				break;
			}
		}
		if(!bAdjusted){
			break;
		}
	}
*/
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImageUncompressed
 * @param vci cbit.image.VCImage
 * @param destX int
 * @param destY int
 */
public static VCImageUncompressed dither(VCImage vci, int destXMax, int destYMax) throws ImageException{

/*
try{
	java.io.File fileOut = new java.io.File("d:\\temp\\sampledGeometry_X"+vci.getNumX()+"_Y"+vci.getNumY()+"_Z"+vci.getNumZ()+".raw");
	java.io.FileOutputStream fos = new java.io.FileOutputStream(fileOut);
	fos.write(vci.getPixels());
	fos.close();
	}catch(Throwable e){
	}

	VCImageUncompressed vcic = dither(vci.getPixels(),vci.getNumX(),vci.getNumY(),vci.getNumZ(),destXMax,destYMax);
	try{
	java.io.File fileOut = new java.io.File("d:\\temp\\ditheredGeometry_X"+vcic.getNumX()+"_Y"+vcic.getNumY()+"_Z"+vcic.getNumZ()+".raw");
	java.io.FileOutputStream fos = new java.io.FileOutputStream(fileOut);
	fos.write(vcic.getPixels());
	fos.close();
	}catch(Throwable e){
	}
	return vcic;
*/
return dither(vci.getPixels(),vci.getNumX(),vci.getNumY(),vci.getNumZ(),destXMax,destYMax);
}
/**
 * This method was created in VisualAge.
 * @return byte[]
 * @param pixels byte[]
 * @param srcYSize int
 * @param srcZSize int
 * @param srcXL float
 * @param srcYL float
 * @param srcXH float
 * @param srcYH float
 */
private static byte[] getAllPixels(	byte[] srcPixels, int srcYSize, int srcZPlaneSize, int srcZPlanes, 
									float srcXL, float srcYL, float srcXH, float srcYH) {
									
	int newSrcXL = (int) (srcXL + .000001);
	int newSrcYL = (int) (srcYL + .000001);
	
	int newSrcXH = (int) (srcXH + .000001);
	if ((newSrcXH > srcXL) && (newSrcXH > newSrcXL)) {
		newSrcXH -= 1;
	}
	int newSrcYH = (int) (srcYH + .000001);
	if ((newSrcYH > srcYL) && (newSrcYH > newSrcYL)) {
		newSrcYH -= 1;
	}
	
	Vector allPixelVals = new Vector();
	int xIndex = newSrcXL + (newSrcYL * srcYSize);
	for (int x = newSrcXL; x <= newSrcXH; x += 1) {
		int xyIndex = xIndex;
		for (int y = newSrcYL; y <= newSrcYH; y += 1) {
			int xyzIndex = xyIndex;
			for (int z = 0; z < srcZPlanes; z += 1) {
				Byte pixel = new Byte(srcPixels[xyzIndex]);
				if (!allPixelVals.contains(pixel)) {
					allPixelVals.addElement(pixel);
				}
				xyzIndex += srcZPlaneSize;
			}
			xyIndex += srcYSize;
		}
		xIndex += 1;
	}
	byte[] pVals = new byte[allPixelVals.size()];
	for (int c = 0; c < allPixelVals.size(); c += 1) {
		pVals[c] = ((Byte) allPixelVals.elementAt(c)).byteValue();
	}
//if(pVals.length == 0){
//	System.out.println("sxl="+srcXL+" syl="+srcYL+" sxh="+srcXH+" syh"+srcYH);
//	System.out.println("sxl="+newSrcXL+" syl="+newSrcYL+" sxh="+newSrcXH+" syh"+newSrcYH);
	
//}
	return pVals;
}
/**
 * This method was created in VisualAge.
 * @return java.util.Vector[]
 * @param destPixels byte[][]
 * @param destXSize int
 * @param destYSize int
 * @param xCenter int
 * @param yCenter int
 */
private static Vector getNeighbors(byte[][] destPixels, int destXSize, int destYSize, int xCenter, int yCenter,int neighborOffset) {
	
	Vector neighbors = new Vector();
	final int xl = xCenter - neighborOffset;
	final int xh = xCenter + neighborOffset;
	final int yl = yCenter - neighborOffset;
	final int yh = yCenter + neighborOffset;
	for (int x = xl; x <= xh; x += 1) {
		for (int y = yl; y <= yh; y += 1) {
			if ((x > xl) && (x < xh) && (y > yl) && (y < yh)) {
				continue;
			}
			if ((x < 0) || (x >= destXSize)) {
				continue;
			}
			if ((y < 0) || (y >= destYSize)) {
				continue;
			}
			int index = x + (y * destXSize);
			neighbors.addElement(new Neighbor(index, destPixels[index],x,y));
		}
	}
	return neighbors;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param pVals byte[]
 */
private static boolean hasByte(byte[] pVals,byte checkByte) {
	for(int c = 0;c<pVals.length;c+= 1){
		if(pVals[c] == checkByte){
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImageUncompressed
 * @param srcPixels byte[]
 * @param x int
 * @param y int
 * @param a int
 * @param b int
 */
public static VCImageUncompressed make2D(byte[] pixels, int srcX, int srcY, int srcZ, int destXMax, int destYMax, int edgeSize) throws ImageException {
	//
	int zSliceSize = srcX * srcY;
	boolean[] falseArray = new boolean[zSliceSize];
	for (int c = 0; c < zSliceSize; c += 1) {
		falseArray[c] = false;
	}
	byte[] finalCombined = null;
	int finalCombinedZCount = 0;
	byte[] combinedPixels = new byte[zSliceSize];
	System.arraycopy(pixels, 0, combinedPixels, 0, zSliceSize);
	Vector unPlacedPixels = new Vector();
	for (int slice = 1; slice < srcZ; slice += 1) {
		int index;
		boolean[] replaceable = new boolean[zSliceSize];
		System.arraycopy(falseArray, 0, replaceable, 0, zSliceSize); //make all false;

		//Find which are replaceable
		index = 0;
		for (int y = 0; y < srcY; y += 1) {
			for (int x = 0; x < srcX; x += 1) {
				if (bSolidBlock(combinedPixels, srcX, srcY, x, y, edgeSize)) {
					replaceable[index] = true;
				}
				index += 1;
			}
		}
		//replace pixels with next slice
		byte[] slicePixels = new byte[zSliceSize];
		System.arraycopy(pixels, slice * zSliceSize, slicePixels, 0, zSliceSize);
		index = 0;
		for (int y = 0; y < srcY; y += 1) {
			for (int x = 0; x < srcX; x += 1) {
				boolean bReplaceOK = true;
				//int pixIndex = ((slice - 1) * zSliceSize) + index;
				int pixIndex = index;
				//Se if we need to place this(slicePixels[index]) pixel color
				//Don't replace pixels that have already replaced pixels of the same color
				for (int checkZ = 1; checkZ < slice; checkZ += 1) { //search Z planes
					if (pixels[pixIndex] == slicePixels[index]) {
						bReplaceOK = false; //Found a previous color that we already replaced, don't replace
						break;
					}
					pixIndex += zSliceSize; //Zplane increment
				}
				if (bReplaceOK) { //we need to place the new pixel(for this zsection) over the old one
					if (replaceable[index]) { //See if old one is replaceable
						combinedPixels[index] = slicePixels[index]; //Do the replacement
					} else { //The old one is not replaceable, save new pixel for ditherer
						int[] upp = new int[2];
						upp[0] = index;
						upp[1] = slicePixels[index];
						unPlacedPixels.addElement(upp);
					}
				}
				index += 1;
			}
		}
	}
	//
	//
	//Put UnPlacedPixels in pseudo Z section(s) for use by ditherer
	//To retain as much simple stacking as possible through the ditherer
	//Each pseudo Z is filled with the same previously combined pixels
	//Couldn't make pseudoZ's while checking slices beacuse we didn't have final combined pixels

	//Simple task that turned out to be unbelievably horrendous!!
	if (unPlacedPixels.size() != 0) {
		//This stuff serves to add unPlacedPixels in as few new PseudoZ's as possible
		int pseudoZCount = 0;
		byte[] pseudoZ = null;
		boolean[] pseudoZLock = null;
		boolean bAllDone = false;
		do {
			pseudoZCount += 1;
			//Grow the pseudoZ if a lock occurred and we need a whole new Section to stack pixels over each other
			byte[] pseudoZTemp = new byte[zSliceSize * pseudoZCount];
			if (pseudoZ != null) {
				System.arraycopy(pseudoZ, 0, pseudoZTemp, 0, pseudoZ.length);
			}
			//Copy in the combined pixels to prevent dithering where unnecessary
			System.arraycopy(combinedPixels, 0, pseudoZTemp, zSliceSize * (pseudoZCount - 1), combinedPixels.length);
			pseudoZ = pseudoZTemp;
			//Grow lock.  Lock tells us if we used a pixel position(x,y) in a particular section yet
			//UnplacedPixels can only occupy one xy position in a section.
			boolean[] pseudoZLockTemp = new boolean[zSliceSize * pseudoZCount];
			if (pseudoZLock != null) {
				System.arraycopy(pseudoZLock, 0, pseudoZLockTemp, 0, pseudoZLock.length);
			}
			System.arraycopy(falseArray, 0, pseudoZLockTemp, zSliceSize * (pseudoZCount - 1), falseArray.length);
			pseudoZLock = pseudoZLockTemp;
			//
			Enumeration enum1 = unPlacedPixels.elements(); //Get all unPlacedPixels that are left
			while (enum1.hasMoreElements()) {
				int[] upp = (int[]) enum1.nextElement(); //Next UnPlacedPixel
				int upp_index = upp[0]; //Index(xy position)
				byte upp_pixelColor = (byte) (upp[1] & 0xff); //color
				boolean bLocked = true;
				for (int c = 0; c < pseudoZCount; c += 1) { //Look through pseudoZ's to find unlocked xy position
					int pzIndex = zSliceSize * c + upp_index;
					if (pseudoZLock[pzIndex]) { //Look at pseudoZ xy position
						continue; //the xy position in this pseudoZ is being used, try another
					} else {
						pseudoZLock[pzIndex] = true;
						bLocked = false;
						pseudoZ[pzIndex] = upp_pixelColor; //set color in this pseudoZ
						unPlacedPixels.removeElement(upp); //remove unPlacedPixels
						if (unPlacedPixels.size() == 0) { //If there are no more unPlacedPixels, we are done
							bAllDone = true;
						}
						break;
					}
				}
				//Either we are done or
				//we searched all pseudoZ's and didn't find a free xy index, have to make new pseudoZ
				if (bLocked || bAllDone) {
					break;
				}
				//Continue and get next unPlacedPixel
			}
		} while (bAllDone = false);
		finalCombinedZCount = pseudoZCount + 1;
		finalCombined = new byte[zSliceSize * (finalCombinedZCount)];
		System.arraycopy(combinedPixels, 0, finalCombined, 0, zSliceSize);
		System.arraycopy(pseudoZ, 0, finalCombined, zSliceSize, pseudoZ.length);
	} else {
		finalCombinedZCount = 1;
		finalCombined = combinedPixels;
	}
	//Return dithered of combined pixels
	return dither(finalCombined, srcX, srcY, finalCombinedZCount, destXMax, destYMax);
}
/**
 * This method was created in VisualAge.
 * @param pVals byte[]
 * @param neighbors java.util.Vector
 * @param lock boolean[]
 */
private static boolean movePixel(byte[][] destPixels, int index, Vector neighbors, boolean[] lock) {
	Vector nDistr = new Vector();
	Vector centralMoved = new Vector();
	byte[] pVals = destPixels[index];
	int pValsCntr = 0;
	for (int c = 0; c < neighbors.size(); c += 1) {
		Neighbor cNeighbor = ((Neighbor) neighbors.elementAt(c));
		if ((!lock[cNeighbor.getIndex()]) && (cNeighbor.getPixels().length == 1)) {
			Byte cnVal = new Byte(cNeighbor.getPixels()[0]);
			if (nDistr.contains(cnVal)) {
				byte[] newNeighbor = new byte[1];
//System.out.println("PVals lenght = "+pVals.length+" PValsCnt = "+pValsCntr);
				newNeighbor[0] = pVals[pValsCntr];
				destPixels[cNeighbor.getIndex()] = newNeighbor;
				centralMoved.addElement(new Byte(pVals[pValsCntr]));
				pValsCntr += 1;
				if (pValsCntr == (pVals.length - 1)) {
					break;
				}
			} else {
				nDistr.addElement(cnVal);
				lock[cNeighbor.getIndex()] = true;
			}
		}
	}
	if (centralMoved.size() > 0) {
		int newCenterCntr = 0;
		byte[] newCenter = new byte[pVals.length - centralMoved.size()];
		for (int c = 0; c < pVals.length; c += 1) {
			Byte b = new Byte(pVals[c]);
			if (!centralMoved.contains(b)) {
				newCenter[newCenterCntr] = pVals[c];
				newCenterCntr += 1;
			}
		}
		destPixels[index] = newCenter;
		return true;
	} else {
		return false;
	}
}
/**
 * This method was created in VisualAge.
 * @param destPixels byte[][]
 * @param destXSize int
 * @param destYSize int
 */
private static void printDestArray(byte[][] destPixels, int destXSize, int destYSize,boolean[] lock) {
	
	for (int y = 0; y < destYSize; y += 1) {
		for (int x = 0; x < destXSize; x += 1) {
			int index = x + y * destXSize;
			System.out.print("(");
			if((lock != null) && (lock[index])){
				System.out.print("*");
			}
			byte[] pVals = destPixels[index];
			for (int p = 0; p < pVals.length; p += 1) {
				System.out.print(pVals[p]);
				if ((p + 1) != pVals.length) {
					System.out.print(",");
				}
			}
			System.out.print(")");
		}
		System.out.println("");
	}
}
/**
 * This method was created in VisualAge.
 * @param destPixels byte[][]
 * @param destXSize int
 * @param destYSize int
 */
private static int removeCommon(byte[][] destPixels, int destXSize, int destYSize, byte checkByte, int neighborOffset) {
	boolean bPixelsChanged = false;
	boolean bDone = true;
	boolean lock[] = new boolean[destXSize * destYSize];
	for (int c = 0; c < lock.length; c += 1) {
		lock[c] = false;
	}
	for (int y = 0; y < destYSize; y += 1) {
		for (int x = 0; x < destXSize; x += 1) {
			int index = x + y * destXSize;
			byte[] pVals = destPixels[index];
//if(pVals.length == 0){System.out.println("Pvals = 0 at x="+x+" y="+y+" index="+index+" checkbyte="+checkByte+" noff="+neighborOffset);System.exit(1);}
			if (pVals.length == 1) {
				continue;
			}
			if (lock[index] || !hasByte(pVals, checkByte)) {
				bDone = false;
				continue;
			}
			Vector neighbors = getNeighbors(destPixels, destXSize, destYSize, x, y, neighborOffset);
			if (removePVal(destPixels, index, neighbors, lock, checkByte)) {
				bPixelsChanged = true;
			}
			if (destPixels[index].length > 1) {
				bDone = false;
			}
		}
	}
	//System.out.println("RemoveCommon neighborOffset=" + neighborOffset + " checkByte=" + checkByte);
	//printDestArray(destPixels, destXSize, destYSize, lock);
	//System.out.println("");
	return ((bDone ? 2 : 0) + (bPixelsChanged ? 1 : 0));
}
/**
 * This method was created in VisualAge.
 * @param pVals byte[]
 * @param neighbors java.util.Vector
 * @param lock boolean[]
 */
private static boolean removePVal(byte[][] destPixels,int index,Vector neighbors,boolean[] lock,byte checkByte) {
	
	byte pVals[] = destPixels[index];
	for(int c = 0;c < neighbors.size();c+= 1){
		Neighbor currentNeighbor = ((Neighbor)neighbors.elementAt(c));
		if(lock[currentNeighbor.getIndex()]){
			continue;
		}
		byte[] nPVals = currentNeighbor.getPixels();
		for(int i = 0;i < nPVals.length;i+= 1){
			if(nPVals[i] == checkByte){
				for(int j = 0;j <neighbors.size();j+= 1){
					lock[((Neighbor)neighbors.elementAt(j)).getIndex()] = true;
				}
				byte adjustedPVals[] = new byte[pVals.length-1];
				int adjC = 0;
				for(int j = 0;j <pVals.length;j+= 1){
					if(pVals[j] == checkByte){
						continue;
					}
					adjustedPVals[adjC] = pVals[j];
					adjC+= 1;
				}
				destPixels[index] = adjustedPVals;
				return true;
			}
		}
	}
	return false;
	
}
/**
 * This method was created in VisualAge.
 * @param destPixlels byte[][]
 * @param destXSize int
 * @param destYSize int
 */
private static int scatter(byte[][] destPixels, int destXSize, int destYSize, int neighborOffset) {
	
	boolean bPixelsChanged = false;
	boolean bDone = true;
	boolean lock[] = new boolean[destXSize * destYSize];
	for (int c = 0; c < lock.length; c += 1) {
		lock[c] = false;
	}
	for (int y = 0; y < destYSize; y += 1) {
		for (int x = 0; x < destXSize; x += 1) {
			int index = x + y * destXSize;
			byte[] pVals = destPixels[index];
			if (pVals.length == 1) {
				continue;
			}
			Vector neighbors = getNeighbors(destPixels, destXSize, destYSize, x, y,neighborOffset);
			if(movePixel(destPixels, index, neighbors,lock)){
				bPixelsChanged = true;
			}
			if(destPixels[index].length > 1){
				bDone = false;
			}
		}
	}
	//System.out.println("Scatter neighborOffset="+neighborOffset);
	//printDestArray(destPixels, destXSize, destYSize, lock);
	//System.out.println("");
	return ((bDone ? 2 : 0) + (bPixelsChanged ? 1 : 0));
}
}
