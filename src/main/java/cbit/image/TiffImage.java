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

import java.io.*;

public class TiffImage 
{

   final static int DEFAULT_STRIP_OFFSET = 8;

   private Tag       tagHead;
//   private RandomAccessFile  infile;
   private TiffInputSource infile;
   private RandomAccessFile  outfile;
   private int       photometricInterpretation;
   private byte      bytePixels[];
   private short     shortPixels[];
   private float     floatPixels[];
   private double    doublePixels[];
   private ByteOrder byteOrder = ByteOrder.Unix;
   private int       pStripByteCounts[];
   private int       pStripOffsets[];
   private long      numStrips;
   private int       pTileByteCounts[];
   private int       pTileOffsets[];
   private long      numTiles;
   private long      width;
   private long      height;
   private long      depth;
   private long      widthTile;
   private long      heightTile;
   private long      depthTile;
   private int       bitspersample;
   private long      compression;
   
   private boolean   bValid = false;
   private DataType  dataType = null;

// unsigned long       _rowsPerStrip;
// unsigned long       _stripOffsets[];

   public TiffImage()
   {
	  bytePixels=null;
	  shortPixels=null;
	  floatPixels=null;
	  doublePixels=null;
	  
	  tagHead=null;
   
	  width=0;
	  height=0;
	  depth=1;
	  widthTile=0;
	  heightTile=0;
	  depthTile=1;
	  bitspersample=0;
	  photometricInterpretation=1;
		   
	  pStripByteCounts = null;
	  pStripOffsets = null;
	  numStrips = 0;
	  pTileByteCounts = null;
	  pTileOffsets = null;
	  numTiles = 0;
   }            
public TiffImage(int width, int height, int depth, byte pixels[]) {
    this();
    this.bytePixels = pixels;
    this.width = width;
    this.height = height;
    this.depth = depth;
    bitspersample = 8;
    dataType = DataType.getByte();
    if (pixels == null) {
        throw new IllegalArgumentException("pixel array cannot be null");
    }
    if (width < 1 && height < 1 && depth < 1) {
        throw new IllegalArgumentException("image dimension (" + width + "," + height + "," + depth + ") must be all >= 1");
    }
    if (width * height * depth != pixels.length) {
        throw new IllegalArgumentException("image dimension (" + width + "," + height + "," + depth + ") must be all >= 1");
    }
    bValid = true;
}
public TiffImage(int width, int height, int depth, float pixels[]) {
    this();
    this.floatPixels = pixels;
    this.width = width;
    this.height = height;
    this.depth = depth;
    bitspersample = 32;
    dataType = DataType.getFloat();
    if (pixels == null) {
        throw new IllegalArgumentException("pixel array cannot be null");
    }
    if (width < 1 && height < 1 && depth < 1) {
        throw new IllegalArgumentException("image dimension (" + width + "," + height + "," + depth + ") must be all >= 1");
    }
    if (width * height * depth != pixels.length) {
        throw new IllegalArgumentException("image dimension (" + width + "," + height + "," + depth + ") must be all >= 1");
    }
    bValid = true;
}
//
// this uses an insertion sort to maintain ascending tiff tags
//
private void addTag(Tag tag){

	//
	// if empty list, make the first element
	//
	if (tagHead == null){
		tag.next = null;
		tagHead = tag;
		return;
	}

	//
	// insert within the list
	//
	Tag tagPtr = tagHead;
	while (tagPtr.next != null){
		if (tagPtr.next.getTagID() > tag.getTagID()){
			tag.next = tagPtr.next;
			tagPtr.next = tag;
			return;
		}
		tagPtr = tagPtr.next;
	}

	//
	// append to end of list
	//
	tag.next = null;
	tagPtr.next = tag;
}   
/**
 * This method was created in VisualAge.
 */
public void close() throws IOException{
	
	if(infile != null) infile.close();
	if (outfile != null) outfile.close();
}
   private void createTags() throws TiffException {
	  tagHead = null;   // clear all current tags

	  int pInt[];
	  char pChar[];
	  long pLong[];
	  float pFloat[];
	  double pDouble[];
	  
	  pInt = new int[1];
	  pInt[0] = (int)width;
	  addTag(new Tag(this, new TIFFTagName(TIFFTagName.TIFFTAG_IMAGEWIDTH), 
						   new TIFFDataType(TIFFDataType.TIFF_SHORT), pInt));

	  pInt = new int[1];
	  pInt[0] = (int)height;
	  addTag(new Tag(this, new TIFFTagName(TIFFTagName.TIFFTAG_IMAGELENGTH), 
						   new TIFFDataType(TIFFDataType.TIFF_SHORT), pInt));

	  pInt = new int[1];
	  pInt[0] = (int)depth;
	  addTag(new Tag(this, new TIFFTagName(TIFFTagName.TIFFTAG_IMAGEDEPTH), 
						   new TIFFDataType(TIFFDataType.TIFF_SHORT), pInt));

	  pInt = new int[1];
	  pInt[0] = (int)bitspersample;
	  addTag(new Tag(this, new TIFFTagName(TIFFTagName.TIFFTAG_BITSPERSAMPLE), 
						   new TIFFDataType(TIFFDataType.TIFF_SHORT), pInt));

	  pInt = pStripOffsets;
	  addTag(new Tag(this, new TIFFTagName(TIFFTagName.TIFFTAG_STRIPOFFSETS), 
						   new TIFFDataType(TIFFDataType.TIFF_LONG), pInt));

	  pInt = pStripByteCounts;
	  addTag(new Tag(this, new TIFFTagName(TIFFTagName.TIFFTAG_STRIPBYTECOUNTS), 
						   new TIFFDataType(TIFFDataType.TIFF_LONG), pInt));

	  pInt = new int[1];
	  pInt[0] = (int)photometricInterpretation;
	  addTag(new Tag(this, new TIFFTagName(TIFFTagName.TIFFTAG_PHOTOMETRIC), 
						   new TIFFDataType(TIFFDataType.TIFF_SHORT), pInt));
   }      
public byte[] getBytePixelData() throws ImageException {
	if (bytePixels==null){
		throw new ImageException("TiffImage::getBytePixelData(), not stored in bytes");
	}
	return bytePixels;
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.DataType
 */
public DataType getDataType() throws TiffException {
	if (!bValid){
		throw new TiffException("image not valid");
	}	
	return dataType;
}
public double[] getDoublePixelData() throws TiffException {
	if (doublePixels==null){
		if (floatPixels!=null){
			doublePixels = new double[floatPixels.length];
			for (int i=0;i<floatPixels.length;i++){
				doublePixels[i] = floatPixels[i];
			}
		}else if (shortPixels!=null){
			doublePixels = new double[shortPixels.length];
			for (int i=0;i<shortPixels.length;i++){
				doublePixels[i] = shortPixels[i];
			}
		}else if (bytePixels!=null){
			doublePixels = new double[bytePixels.length];
			for (int i=0;i<bytePixels.length;i++){
				doublePixels[i] = 0xff&(int)bytePixels[i];
			}
		}else{
			throw new TiffException("TiffImage::getDoublePixelData(), image not valid");
		}
	}
	return doublePixels;
}
public float[] getFloatPixelData() throws ImageException {
	if (floatPixels==null){
		if (doublePixels!=null){
			floatPixels = new float[doublePixels.length];
			for (int i=0;i<doublePixels.length;i++){
				floatPixels[i] = (float)doublePixels[i];
			}
		}else if (shortPixels!=null){
			floatPixels = new float[shortPixels.length];
			for (int i=0;i<shortPixels.length;i++){
				floatPixels[i] = shortPixels[i];
			}
		}else if (bytePixels!=null){
			floatPixels = new float[bytePixels.length];
			for (int i=0;i<bytePixels.length;i++){
				floatPixels[i] = 0xff&(int)bytePixels[i];
			}
		}else{
			throw new TiffException("TiffImage::getFloatPixelData(), image not valid");
		}
	}
	return floatPixels;
}
/**
 * This method was created by a SmartGuide.
 * @return int[]
 * @exception java.lang.Exception The exception description.
 */
public int[] getRGB() throws TiffException {
	if (!bValid){
		throw new TiffException("image not valid");
	}	
	if (bitspersample!=8 && bitspersample!=16 && bitspersample!=32){
		throw new TiffException("support for "+bitspersample+" bits per channel is not supported");
	}	
	int rgb[] = new int[(int)(width*height*depth)];
	int red;
	int green;
	int blue;
	int alpha;
	if (bytePixels!=null){
		for (int i=0;i<bytePixels.length;i++){
			red = green = blue = ((int)bytePixels[i])&0xff;
			alpha = 0xff;
			rgb[i] = (alpha<<24)|(red<<16)|(green<<8)|blue;
		}
	}else if (shortPixels!=null){
		for (int i=0;i<shortPixels.length;i++){
			red = green = blue = ((int)shortPixels[i])&0xff;
			alpha = 0xff;
			rgb[i] = (alpha<<24)|(red<<16)|(green<<8)|blue;
		}
	}else if (floatPixels!=null){
		for (int i=0;i<floatPixels.length;i++){
			red = green = blue = ((int)floatPixels[i])&0xff;
			alpha = 0xff;
			rgb[i] = (alpha<<24)|(red<<16)|(green<<8)|blue;
		}
	}else if (doublePixels!=null){
		for (int i=0;i<doublePixels.length;i++){
			red = green = blue = ((int)doublePixels[i])&0xff;
			alpha = 0xff;
			rgb[i] = (alpha<<24)|(red<<16)|(green<<8)|blue;
		}
	}else{
		throw new TiffException("no data stored");
	}
	return rgb;
}
public short[] getShortPixelData() throws ImageException {
	if (shortPixels==null){
		throw new ImageException("TiffImage::getShortPixelData(), not stored in short's");
	}
	return shortPixels;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @exception java.lang.Exception The exception description.
 */
public int getSizeX() throws TiffException {
	if (!bValid){
		throw new TiffException("image not valid");
	}	
	return (int)width;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @exception java.lang.Exception The exception description.
 */
public int getSizeY() throws TiffException {
	if (!bValid){
		throw new TiffException("image not valid");
	}	
	return (int)height;
}
/**
 * This method was created by a SmartGuide.
 * @return int
 * @exception java.lang.Exception The exception description.
 */
public int getSizeZ() throws TiffException {
	if (!bValid){
		throw new TiffException("image not valid");
	}	
	return (int)depth;
}
   private Tag getTag(int tagID)
   {
      Tag ptr = tagHead;

      while (ptr!=null){
         if (ptr.getTagID()==tagID){
	    return ptr;
         }
         ptr = ptr.next;
      }

      return null;
   }
   private void parseTags() throws TiffException
   {
	  Tag tag=null;

	  if ((tag=getTag(TIFFTagName.TIFFTAG_IMAGEWIDTH))!=null){
		 width = (long)tag.getFirstValue();
	  }

	  if ((tag=getTag(TIFFTagName.TIFFTAG_IMAGELENGTH))!=null){
		 height = (long)tag.getFirstValue();
	  }

	  if ((tag=getTag(TIFFTagName.TIFFTAG_IMAGEDEPTH))!=null){
		 depth = (long)tag.getFirstValue();
	  }
   
	  if ((tag=getTag(TIFFTagName.TIFFTAG_BITSPERSAMPLE))!=null){
		 bitspersample = (int)tag.getFirstValue();
	  }
   
	  if ((tag=getTag(TIFFTagName.TIFFTAG_STRIPOFFSETS))!=null){
		 pStripOffsets = tag.getUnsignedLong();
		 numStrips = tag.getLength();
	  }
   
	  if ((tag=getTag(TIFFTagName.TIFFTAG_STRIPBYTECOUNTS))!=null){
		 pStripByteCounts = tag.getUnsignedLong();
	  }
   
	  if ((tag=getTag(TIFFTagName.TIFFTAG_TILEOFFSETS))!=null){
		 pTileOffsets = tag.getUnsignedLong();
		 numTiles = tag.getLength();
	  }
   
	  if ((tag=getTag(TIFFTagName.TIFFTAG_TILEBYTECOUNTS))!=null){
		 pTileByteCounts = tag.getUnsignedLong();
	  }
   
	  if ((tag=getTag(TIFFTagName.TIFFTAG_TILEWIDTH))!=null){
		 widthTile = (long)tag.getFirstValue();
	  }
   
	  if ((tag=getTag(TIFFTagName.TIFFTAG_TILELENGTH))!=null){
		 heightTile = (long)tag.getFirstValue();
	  }
   
	  if ((tag=getTag(TIFFTagName.TIFFTAG_TILEDEPTH))!=null){
		 depthTile = (long)tag.getFirstValue();
	  }
   
	  if ((tag=getTag(TIFFTagName.TIFFTAG_PHOTOMETRIC))!=null){
		 photometricInterpretation = (int)tag.getFirstValue();
	  }
   
	  if (pStripByteCounts==null && pTileByteCounts==null){
		 pStripByteCounts = new int[1];
		 pStripByteCounts[0] = (int)(width*height*depth*(bitspersample/8));
	  }
   
	  if (pStripOffsets==null && pTileOffsets==null){
		 pStripOffsets = new int[1];
		 pStripOffsets[0] = (int)DEFAULT_STRIP_OFFSET;
		 numStrips = 1;
	  }
   
	  if (numStrips!=0){
System.out.println("TIFFImg::parseTags(), numStrips="+numStrips);
for (int i=0;i<numStrips;i++){
System.out.println("strip["+i+"] = "+pStripByteCounts[i]+" bytes at location "+pStripOffsets[i]);
} 
	  }

	  if (numTiles!=0){
System.out.println("TIFFImg::parseTags(), tileWidth="+widthTile);
System.out.println("TIFFImg::parseTags(), tileHeight="+heightTile);
System.out.println("TIFFImg::parseTags(), tileDepth="+depthTile);
System.out.println("TIFFImg::parseTags(), numTiles="+numTiles);
for (int i=0;i<numTiles;i++){
System.out.println("tile["+i+"] = "+pTileByteCounts[i]+" bytes at location "+pTileOffsets[i]);
} 
	  }

System.out.println("TIFFImg::parseTags(), bitsPerSample="+bitspersample);
System.out.println("TIFFImg::parseTags(), width="+width);
System.out.println("TIFFImg::parseTags(), height="+height);
   
	  if (!verifyDepth()){
		 throw new TiffException("TIFFImg::parseTags(), depth="+depth);
	  }
System.out.println("TIFFImg::parseTags(), depth="+depth);
   }                  
public void read(TiffInputSource inputSource) throws TiffException, IOException {

	bValid = false;

	infile = inputSource;

	byte byteOrderTmp[] = new byte[2];

	infile.read(byteOrderTmp);

	String orderString = new String(byteOrderTmp);
	byteOrder = ByteOrder.fromString(orderString);
	if (byteOrder == ByteOrder.Undefined){
		throw new TiffException("undefined byte order");
	}

	int version = readUnsignedShort();

	if (version!=42){
		throw new TiffException("version is "+version+" should be 42, invalid TIFF format");
	}

	int offsetIFD = readInt();
   
System.out.println("reading Image File Directory at " + offsetIFD);

	while ((offsetIFD = readIFD(offsetIFD))!=0){
System.out.println("reading Image File Directory at " + offsetIFD);
	}
   
	parseTags();

	if (numStrips>0){
		readStrips();
	}else{
		readTiles();
	}	
	bValid = true;
}      
private int readIFD(int offset) throws IOException, TiffException {
	int entryCount = 0;
	infile.seek(offset);
	entryCount = readUnsignedShort();
	if (entryCount <= 0) {
		System.out.println("readIFD(), entryCount=" + entryCount);
		return 0;
	}
	System.out.println("IFD has " + entryCount + " entries");
	for (int i = 0; i < entryCount; i++) {
		Tag pTag = new Tag(this);
		pTag.read(infile);
		addTag(pTag);
	}
	offset = readInt();
	return offset;
}
   int readInt() throws IOException {
	  int tmpInt;
	  byte temp[] = new byte[4];
	  infile.read(temp);
	  int intArray[] = new int[4];
	  for (int i=0;i<4;i++) intArray[i] = ((int)temp[i]) & 0x000000FF;
	  if (byteOrder == ByteOrder.PC){ 
		 tmpInt = (intArray[3]<<24) + 
	          (intArray[2]<<16) + 
		  (intArray[1]<<8) + 
		  intArray[0];
	  }else{
		 tmpInt = (intArray[0]<<24) + 
	          (intArray[1]<<16) + 
		  (intArray[2]<<8) + 
		  intArray[3];
	  }  
	  return tmpInt;
   }      
   long readLong() throws IOException {
	  long tmpLong;
	  byte temp[] = new byte[8];
	  infile.read(temp);
	  long longArray[] = new long[8];
	  for (int i=0;i<8;i++) longArray[i] = ((long)temp[i]) & 0x00000000000000FF;
	  if (byteOrder == ByteOrder.PC){ 
		 tmpLong = (longArray[7]<<56) + 
	           (longArray[6]<<48) + 
	           (longArray[5]<<40) + 
	           (longArray[4]<<32) + 
	           (longArray[3]<<24) + 
	           (longArray[2]<<16) + 
		   (longArray[1]<<8) + 
		   longArray[0];
	  }else{
		 tmpLong = (longArray[0]<<56) + 
	           (longArray[1]<<48) + 
	           (longArray[2]<<40) + 
	           (longArray[3]<<32) + 
	           (longArray[4]<<24) + 
	           (longArray[5]<<16) + 
		   (longArray[6]<<8) + 
		   longArray[7];
	  }  
	  return tmpLong;
   }      
private void readStrips() throws TiffException, IOException {
	int ushort;
	int uchar;
	if (numStrips == 0) {
		throw new TiffException("numStrips==0");
	}
	switch ((int) bitspersample) {
		case 8 :
			{
				dataType = DataType.getByte();
				bytePixels = new byte[ (int) (width * height * depth)];
				shortPixels = null;
				floatPixels = null;
				doublePixels = null;
				int pixelOffset = 0;
				for (int i = 0; i < numStrips; i++) {
					infile.seek(pStripOffsets[i]);
					infile.read(bytePixels, pixelOffset, pStripByteCounts[i]);
					pixelOffset += pStripByteCounts[i];
				}
				break;
			}
		case 16 :
			{
				boolean bSwap = byteOrder.equals(ByteOrder.PC);
				int bytesPerPixel = 2;
				dataType = DataType.getShort();
				bytePixels = null;
				floatPixels = null;
				doublePixels = null;
				shortPixels = new short[(int)(width * height * depth)];
				byte bytes[] = new byte[ (int) (width * height * depth)*bytesPerPixel];
				int pixelOffset = 0;
				for (int i = 0; i < numStrips; i++) {
					infile.seek(pStripOffsets[i]);
					infile.read(bytes, pixelOffset, pStripByteCounts[i]);
					pixelOffset += pStripByteCounts[i];
				}
				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				DataInputStream dis = new DataInputStream(bis);
				for (int i=0;i<shortPixels.length;i++){
					if (!bSwap){
						shortPixels[i] = dis.readShort();
					}else{
						int b1 = 0xff & dis.readByte();
						int b2 = 0xff & dis.readByte();
						shortPixels[i] = (short)((b2<<8) + b1);
					}
				}
				break;
			}
		case 32 :
			{
				boolean bSwap = byteOrder.equals(ByteOrder.PC);
				int bytesPerPixel = 4;
				dataType = DataType.getFloat();
				bytePixels = null;
				shortPixels = null;
				floatPixels = new float[(int)(width * height * depth)];
				doublePixels = null;
				byte bytes[] = new byte[ (int) (width * height * depth)*bytesPerPixel];
				int pixelOffset = 0;
				for (int i = 0; i < numStrips; i++) {
					infile.seek(pStripOffsets[i]);
					infile.read(bytes, pixelOffset, pStripByteCounts[i]);
					pixelOffset += pStripByteCounts[i];
				}
				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				DataInputStream dis = new DataInputStream(bis);
				for (int i=0;i<floatPixels.length;i++){
					if (!bSwap){
						floatPixels[i] = dis.readFloat();
					}else{
						int b1 = 0xff & dis.readByte();
						int b2 = 0xff & dis.readByte();
						int b3 = 0xff & dis.readByte();
						int b4 = 0xff & dis.readByte();
						int intValue = (b4<<24) + (b3<<16) + (b2<<8) + b1;
						floatPixels[i] = Float.intBitsToFloat(intValue);
					}
				}
				break;
			}
		case 64 :
			{
				boolean bSwap = byteOrder.equals(ByteOrder.PC);
				int bytesPerPixel = 8;
				dataType = DataType.getDouble();
				bytePixels = null;
				shortPixels = null;
				floatPixels = null;
				doublePixels = new double[(int)(width * height * depth)];
				byte bytes[] = new byte[ (int) (width * height * depth)*bytesPerPixel];
				int pixelOffset = 0;
				for (int i = 0; i < numStrips; i++) {
					infile.seek(pStripOffsets[i]);
					infile.read(bytes, pixelOffset, pStripByteCounts[i]);
					pixelOffset += pStripByteCounts[i];
				}
				ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
				DataInputStream dis = new DataInputStream(bis);
				for (int i=0;i<doublePixels.length;i++){
					if (!bSwap){
						doublePixels[i] = dis.readDouble();
					}else{
						long b1 = 0xff & dis.readByte();
						long b2 = 0xff & dis.readByte();
						long b3 = 0xff & dis.readByte();
						long b4 = 0xff & dis.readByte();
						long b5 = 0xff & dis.readByte();
						long b6 = 0xff & dis.readByte();
						long b7 = 0xff & dis.readByte();
						long b8 = 0xff & dis.readByte();
						long longValue = (b8<<56) + (b7<<48) + (b6<<40) + (b5<<32) + (b4<<24) + (b3<<16) + (b2<<8) + (b1<<0);
						doublePixels[i] = Double.longBitsToDouble(longValue);
					}
				}
				break;
			}
		default :
			throw new TiffException("cannot handle " + bitspersample + " bits per sample");
			//break;
	}
}
private void readTiles() throws IOException, TiffException {
	int ushort;
	byte uchar;
	long tileX = 0;
	long tileY = 0;
	long tileZ = 0;
	long x, y, z;
	boolean bSwap = byteOrder.equals(ByteOrder.PC);
	if (numTiles == 0)
		throw new TiffException("numTiles==0");
	int bytesPerPixel = bitspersample / 8;
	byte pixels[] = new byte[ (int) (width * height * depth) * bytesPerPixel];
	int pixelOffset = 0;
	byte tile[] = new byte[ (int) (widthTile * heightTile * depthTile)*bytesPerPixel];
	int tileOffset = 0;
	for (int i = 0; i < numTiles; i++) {
		infile.seek(pTileOffsets[i]);
		infile.read(tile, 0, pTileByteCounts[i]);
		for (z = tileZ; z < Math.min(depth, depthTile + tileZ); z++) {
			for (y = tileY; y < Math.min(height, heightTile + tileY); y++) {
				for (x = tileX; x < Math.min(width, widthTile + tileX); x++) {
					//	tileOffset = (int)( (x-tileX)*heightTile*widthTile+(y-tileY)*widthTile+(z-tileZ));
					tileOffset = (int) (((z - tileZ) * heightTile + (y - tileY)) * widthTile + (x - tileX));
					pixels[ (int) ((z * height + y) * width + x)*bytesPerPixel] = tile[tileOffset*bytesPerPixel];
					if (bytesPerPixel>=2){
						pixels[ (int) ((z * height + y) * width + x)*bytesPerPixel+1] = tile[tileOffset*bytesPerPixel+1];
					}
					if (bytesPerPixel==4){
						pixels[ (int) ((z * height + y) * width + x)*bytesPerPixel+2] = tile[tileOffset*bytesPerPixel+2];
						pixels[ (int) ((z * height + y) * width + x)*bytesPerPixel+3] = tile[tileOffset*bytesPerPixel+3];
					}
				}
			}
		}
		//
		// test for continuation in X
		//
		if (widthTile < width) {
			tileX += widthTile;
			if (tileX < width) {
				continue;
			}
			tileX = 0;
		}
		//
		// test for continuation in Y
		//
		if (heightTile < height) {
			tileY += heightTile;
			if (tileY < height) {
				continue;
			}
			tileY = 0;
		}
		//
		// test for continuation in Z
		//
		if (depthTile < depth) {
			tileZ += depthTile;
			if (tileZ < depth) {
				continue;
			}
			tileZ = 0;
		}
	}
	ByteArrayInputStream bis = new ByteArrayInputStream(pixels);
	DataInputStream dis = new DataInputStream(bis);
	bytePixels = null;
	shortPixels = null;
	floatPixels = null;
	doublePixels = null;
	switch (bytesPerPixel){
		case 1:{
			bytePixels = new byte[(int)(width*height*depth)];
			for (int i=0;i<bytePixels.length;i++){
				bytePixels[i] = dis.readByte();
			}
			break;
		}
		case 2:{
			shortPixels = new short[(int)(width*height*depth)];
			for (int i=0;i<shortPixels.length;i++){
				if (!bSwap){
					shortPixels[i] = dis.readShort();
				}else{
					int b1 = 0xff & dis.readByte();
					int b2 = 0xff & dis.readByte();
					shortPixels[i] = (short)((b2<<8) + b1);
				}
			}
			break;
		}
		case 4:{
			floatPixels = new float[(int)(width*height*depth)];
			for (int i=0;i<floatPixels.length;i++){
				if (!bSwap){
					floatPixels[i] = dis.readFloat();
				}else{
					int b1 = 0xff & dis.readByte();
					int b2 = 0xff & dis.readByte();
					int b3 = 0xff & dis.readByte();
					int b4 = 0xff & dis.readByte();
					int intValue = (b4<<24) + (b3<<16) + (b2<<8) + b1;
					floatPixels[i] = Float.intBitsToFloat(intValue);
				}
			}
			break;
		}
		case 8:{
			doublePixels = new double[(int)(width*height*depth)];
			for (int i=0;i<doublePixels.length;i++){
				if (!bSwap){
					doublePixels[i] = dis.readDouble();
				}else{
					long b1 = 0xff & dis.readByte();
					long b2 = 0xff & dis.readByte();
					long b3 = 0xff & dis.readByte();
					long b4 = 0xff & dis.readByte();
					long b5 = 0xff & dis.readByte();
					long b6 = 0xff & dis.readByte();
					long b7 = 0xff & dis.readByte();
					long b8 = 0xff & dis.readByte();
					long longValue = (b8<<56) + (b7<<48) + (b6<<40) + (b5<<32) + (b4<<24) + (b3<<16) + (b2<<8) + (b1<<0);
					doublePixels[i] = Double.longBitsToDouble(longValue);
				}
			}
			break;
		}
		default:{
		}
	}
}
   int readUnsignedShort() throws IOException {
	  int tmpInt;
	  byte temp[] = new byte[2];
	  infile.read(temp);
	  int intArray[] = new int[2];
	  for (int i=0;i<2;i++) intArray[i] = ((int)temp[i]) & 0x000000FF;
	  if (byteOrder == ByteOrder.PC){ 
		 tmpInt = (intArray[1]<<8) + intArray[0];
	  }else{
		 tmpInt = (intArray[0]<<8) + intArray[1];
	  }  
	  return tmpInt;
   }      
private boolean verifyDepth() throws TiffException {
	if (depth > 1) {
		return true;
	}
	long numBytesPerPixel = bitspersample/8;
	//
	// determine number of bytes in image
	//
	int numBytes = 0;
	if (numStrips != 0) {
		for (int k = 0; k < numStrips; k++) {
			numBytes += pStripByteCounts[k];
		}
	} else
		if (numTiles != 0) {
			for (int k = 0; k < numTiles; k++) {
				numBytes += pTileByteCounts[k];
			}
			long totalTile = numTiles*widthTile*heightTile*depthTile*bitspersample/8;
			System.out.println("tiles....numBytes="+numBytes+", numTiles*tileWidth*tileHeight*tileDepth*bitsPerPixel/8="+totalTile);
			if (numBytes!=totalTile){
				throw new TiffException("calculated total bytes from doesn't match sum of TileByteCounts");
			}
			long remainderX = widthTile - (width%widthTile);
			long remainderY = heightTile - (height%heightTile);
			long numTilesX = (remainderX==0)?(width/widthTile):(width/widthTile+1);
			long numTilesY = (remainderY==0)?(height/heightTile):(height/heightTile+1);
			long extraPixels = remainderX*numTilesY*heightTile + remainderY*numTilesX*widthTile - remainderX*remainderY;
			numBytes -= extraPixels*numBytesPerPixel;
System.out.println("corrected numBytes="+numBytes+"  correction="+(extraPixels*numBytesPerPixel)+"   remainders ("+remainderX+", "+remainderY+")");
				
		} else {
			throw new TiffException("TiffImage::verifyDepth() - can't determine # data byte");
		}

		//
		// check number of bytes against calculated from W*H for data size
		//
	if (numBytes != width * height * (bitspersample / 8)) {
		System.out.println("warning, # bytes = " + numBytes + ", Width X Height X size = " + width * height * (bitspersample / 8));
		depth = (long) (((double) numBytes) / (width * height * (bitspersample / 8)));
		if (depth * width * height * (bitspersample / 8) == numBytes) {
			System.out.println(" ... resolved depth=" + depth);
			return true;
		} else {
			System.out.println(" ... could not resolve depth");
			return false;
		}
	}
	return true;
}
   public void write(String filename, ByteOrder byteOrder) throws IOException, TiffException {
	 int version=42;
	 int offsetPixels = 8;
	 //
	 // put IFD after pixels
	 //
	 if (dataType==null || dataType.isDouble() || dataType.isShort()){
		 throw new TiffException("writing not supported image type '"+dataType+"'");
	 }

	 int dataLength = (int)(width*height*depth*(bitspersample/8));
	 int offsetIFD = offsetPixels+dataLength;

	 outfile = new RandomAccessFile(filename, "rw");

	 byte byteOrderTmp[] = byteOrder.toString().getBytes();
	 if (byteOrderTmp.length != 2){
	 	throw new TiffException("bad byte conversion of byte order string "+byteOrder.toString());
	 }	
	 outfile.write(byteOrderTmp);
	 writeUnsignedShort(version);
	 writeInt(offsetIFD);
   
	 //
	 // Establish prefered pixel organization (1 big strip)
	 //
	 numStrips=1;
	 pStripOffsets = new int[1];
	 pStripOffsets[0] = 8;
	 pStripByteCounts = new int[1];
	 pStripByteCounts[0] = dataLength;
	 pTileByteCounts=null;
	 pTileOffsets=null;
	 numTiles=0;
	 
	 createTags();

	 write1BigStrip();

//System.out.println("writing Image File Directory at " + offsetIFD);

	 writeIFD(offsetIFD);
	  
	 outfile.close();
   }                  
private void write1BigStrip() throws IOException, TiffException {
	if (dataType==null){
		throw new TiffException("datatype was null");
	}
	if (dataType.isByte()){
		
		if (bitspersample!=8){
			throw new TiffException("bitspersample!=8");
		}
		if (bytePixels==null){
			throw new TiffException("pixels==null");
		}
	 
	//     numStrips=1;
	//     pStripOffsets = new int[1];
	//     pStripOffsets[0] = 8;
	//     pStripByteCounts = new int[1];
	//     pStripByteCounts = pixels.length;
	 
		int pixelOffset = 0;
		for (int i=0;i<numStrips;i++){
			outfile.seek(pStripOffsets[i]); 
			outfile.write(bytePixels, pixelOffset, pStripByteCounts[i]);
			pixelOffset += pStripByteCounts[i];
		}
	}else if (dataType.isFloat()){
		
		if (bitspersample!=32){
			throw new TiffException("bitspersample!=32");
		}
		if (floatPixels==null){
			throw new TiffException("floatPixels==null");
		}
	 
	//     numStrips=1;
	//     pStripOffsets = new int[1];
	//     pStripOffsets[0] = 8;
	//     pStripByteCounts = new int[1];
	//     pStripByteCounts = pixels.length;
	 
		int pixelOffset = 0;
		for (int i=0;i<numStrips;i++){
			outfile.seek(pStripOffsets[i]);
			for (int j = 0; j < floatPixels.length; j++){
				writeInt(Float.floatToIntBits(floatPixels[j]));
			}
			pixelOffset += pStripByteCounts[i];
		}
	}else{
		throw new TiffException("dataType "+dataType+" not supported");
	}
}      
private void writeIFD(int offset) throws IOException, TiffException {
	int entryCount = 0;
	Tag tempTag = tagHead;
	while (tempTag != null) {
		entryCount++;
		tempTag = tempTag.next;
	}
	outfile.seek(offset);
	writeUnsignedShort(entryCount);
	if (entryCount <= 0) {
		//System.out.println("readIFD(), entryCount=" + entryCount);
		return;
	}

	//System.out.println("IFD has "+entryCount+" entries");

	tempTag = tagHead;
	while (tempTag != null) {
		tempTag.write(outfile);
		tempTag = tempTag.next;
	}
	writeInt(0); // offset to next IFD .. there is only one
}
   void writeInt(int data) throws IOException {
	  byte temp[] = new byte[4];
	  if (byteOrder == ByteOrder.PC){ 
		 temp[3] = (byte)((data>>24)&0x000000FF);
		 temp[2] = (byte)((data>>16)&0x000000FF);
		 temp[1] = (byte)((data>>8)&0x000000FF);
		 temp[0] = (byte)((data)&0x000000FF);
	  }else{
		 temp[0] = (byte)((data>>24)&0x000000FF);
		 temp[1] = (byte)((data>>16)&0x000000FF);
		 temp[2] = (byte)((data>>8)&0x000000FF);
		 temp[3] = (byte)((data)&0x000000FF);
	  }  
	  outfile.write(temp);
   }      
   void writeLong(long data) throws IOException {
	  byte temp[] = new byte[8];
	  if (byteOrder == ByteOrder.PC){ 
		 temp[7] = (byte)((data>>56)&0x00000000000000FF);
		 temp[6] = (byte)((data>>48)&0x00000000000000FF);
		 temp[5] = (byte)((data>>40)&0x00000000000000FF);
		 temp[4] = (byte)((data>>32)&0x00000000000000FF);
		 temp[3] = (byte)((data>>24)&0x00000000000000FF);
		 temp[2] = (byte)((data>>16)&0x00000000000000FF);
		 temp[1] = (byte)((data>>8)&0x00000000000000FF);
		 temp[0] = (byte)((data)&0x00000000000000FF);
	  }else{
		 temp[0] = (byte)((data>>56)&0x00000000000000FF);
		 temp[1] = (byte)((data>>48)&0x00000000000000FF);
		 temp[2] = (byte)((data>>40)&0x00000000000000FF);
		 temp[3] = (byte)((data>>32)&0x00000000000000FF);
		 temp[4] = (byte)((data>>24)&0x00000000000000FF);
		 temp[5] = (byte)((data>>16)&0x00000000000000FF);
		 temp[6] = (byte)((data>>8)&0x00000000000000FF);
		 temp[7] = (byte)((data)&0x00000000000000FF);
	  }  
	  outfile.write(temp);
   }      
   void writeUnsignedShort(int data) throws IOException {
	  byte temp[] = new byte[2];
	  if (byteOrder == ByteOrder.PC){ 
		 temp[1] = (byte)((data>>8)&0x000000FF);
		 temp[0] = (byte)((data)&0x000000FF);
	  }else{
		 temp[0] = (byte)((data>>8)&0x000000FF);
		 temp[1] = (byte)((data)&0x000000FF);
	  }  
	  outfile.write(temp);
   }      
}
