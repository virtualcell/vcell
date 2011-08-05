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

import java.io.RandomAccessFile;

class Tag
{
   Tag next;

   protected TiffImage       tiffImg;
   protected TIFFTagName     tagType;
   protected TIFFDataType    dataType;
   protected int             length;
   protected int             pRational[];
   protected double          pDouble[];
   protected float           pFloat[];
   protected int             pLong[];
   protected int             pShort[];
   protected byte            pChar[];
   Tag(TiffImage tiffImg) {
      this.tiffImg=tiffImg;
      length = 0;
      pRational = null;
      pDouble = null;
      pFloat = null;
      pLong = null;
      pShort = null;
      pChar = null; 
      next = null;
   }
   Tag(TiffImage tiffImg, TIFFTagName tagType, TIFFDataType dataType, byte data[]) throws TiffException
   {
	  this(tiffImg);
	  this.tagType = tagType;
	  this.dataType = dataType;
	  if (dataType.getType()!=TIFFDataType.TIFF_BYTE &&
		  dataType.getType()!=TIFFDataType.TIFF_ASCII &&
	  dataType.getType()!=TIFFDataType.TIFF_SBYTE &&
	  dataType.getType()!=TIFFDataType.TIFF_UNDEFINED) throw new TiffException("wrong data type");
	  pChar = data; 
	  length = data.length;
   }   
   Tag(TiffImage tiffImg, TIFFTagName tagType, TIFFDataType dataType, double data[]) throws TiffException
   {
	  this(tiffImg);
	  this.tagType = tagType;
	  this.dataType = dataType;
	  if (dataType.getType()!=TIFFDataType.TIFF_DOUBLE) throw new TiffException("wrong data type");
	  pDouble = data;
	  length = data.length;
   }   
   Tag(TiffImage tiffImg, TIFFTagName tagType, TIFFDataType dataType, float data[]) throws TiffException
   {
	  this(tiffImg);
	  this.tagType = tagType;
	  this.dataType = dataType;
	  if (dataType.getType()!=TIFFDataType.TIFF_FLOAT) throw new TiffException("wrong data type");
	  pFloat = data;
	  length = data.length;
   }   
   Tag(TiffImage tiffImg, TIFFTagName tagType, TIFFDataType dataType, int data[]) throws TiffException
   {
	  this(tiffImg);
	  this.tagType = tagType;
	  this.dataType = dataType;
	  switch (dataType.getType()){
		 case TIFFDataType.TIFF_RATIONAL:
		 case TIFFDataType.TIFF_SRATIONAL:
	    pRational = data;
	    break;
		 case TIFFDataType.TIFF_LONG:
		 case TIFFDataType.TIFF_SLONG:
	    pLong = data;
	    break;
		 case TIFFDataType.TIFF_SHORT:
		 case TIFFDataType.TIFF_SSHORT:
	    pShort = data;
	    break;
		 default:
	    throw new TiffException("wrong datatype");
	  }
	  length = data.length;
   }   
   byte[] getChar() throws TiffException
   {
	  if (getDataType().getType()==TIFFDataType.TIFF_BYTE || 
		  getDataType().getType()==TIFFDataType.TIFF_SBYTE || 
		  getDataType().getType()==TIFFDataType.TIFF_UNDEFINED || 
		  getDataType().getType()==TIFFDataType.TIFF_ASCII){
		 return pChar;
	  }else{
		 throw new TiffException("data type not consistent with byte");
	  } 
   }   
   TIFFDataType getDataType() { 
      return dataType; 
   }
   double getFirstValue() throws TiffException
   {
	  if (pChar!=null)
		return (double)pChar[0];
	  if (pShort!=null)
		return (double)pShort[0];
	  if (pLong!=null)
		return (double)pLong[0];
	  if (pFloat!=null)
		return (double)pFloat[0];
	  if (pDouble!=null)
		return pDouble[0];
	  if (pRational!=null)
		return ((double)pRational[0])/pRational[1];
	  throw new TiffException("value not defined");
   }      
   int getLength() {
      return length; 
   }
   int getTagID() { 
      return tagType.getType(); 
   }
   TIFFTagName getTagName() { 
      return tagType; 
   }
   int[] getUnsignedLong() throws TiffException
   {
	  if (getDataType().getType()==TIFFDataType.TIFF_LONG || 
		  getDataType().getType()==TIFFDataType.TIFF_SLONG){
		 return pLong;
	  }else{
		 throw new TiffException("data type not consistent with unsigned long");
	  } 
   }   
   int[] getUnsignedShort() throws TiffException 
   {
	  if (getDataType().getType()==TIFFDataType.TIFF_SHORT ||
		  getDataType().getType()==TIFFDataType.TIFF_SSHORT){
		 return pShort;
	  }else{
		 throw new TiffException("data type not consistent with unsigned short");
	  } 
   }   
   String getValueString() throws TiffException
   {
	  String string = "Unknown Data Type";
	  
	  switch(dataType.getType()){
		 case TIFFDataType.TIFF_ASCII:
	    string = "'" + (new String(pChar)) + "'";
	    break;
		 case TIFFDataType.TIFF_BYTE:
		 case TIFFDataType.TIFF_SBYTE:
		 case TIFFDataType.TIFF_UNDEFINED:{
	    if (pChar==null) throw new TiffException("type requires pChar to have data");
	    StringBuffer buffer = new StringBuffer();
	    buffer.append("'");
	    for (int i=0;i<length;i++){
	       buffer.append(pChar[i]);
 	    }
	    buffer.append("'");
	    string = buffer.toString();
	    break;
		 }
	 case TIFFDataType.TIFF_SSHORT:
		 case TIFFDataType.TIFF_SHORT:{
	    if (pShort==null) throw new TiffException("type requires pShort to have data");
	    StringBuffer buffer = new StringBuffer();
	    for (int i=0;i<length;i++){
	       buffer.append(pShort[i] + ", ");
 	    }
	    string = buffer.toString();
			break;
		 }
		 case TIFFDataType.TIFF_LONG:{
	    if (pLong==null) throw new TiffException("type requires pLong to have data");
	    StringBuffer buffer = new StringBuffer();
	    for (int i=0;i<length;i++){
	       buffer.append(pLong[i] + ", ");
 	    }
	    string = buffer.toString();
			break;
		 }
		 case TIFFDataType.TIFF_FLOAT:{
	    if (pFloat==null) throw new TiffException("type requires pFloat to have data");
	    StringBuffer buffer = new StringBuffer();
	    for (int i=0;i<length;i++){
	       buffer.append(pFloat[i] + ", ");
 	    }
	    string = buffer.toString();
			break;
		 }
		 case TIFFDataType.TIFF_DOUBLE:{
	    if (pDouble==null) throw new TiffException("type requires pDouble to have data");
	    StringBuffer buffer = new StringBuffer();
	    for (int i=0;i<length;i++){
	       buffer.append(pDouble[i] + ", ");
 	    }
	    string = buffer.toString();
			break;
		 }
		 case TIFFDataType.TIFF_RATIONAL:{
	    if (pRational==null) throw new TiffException("type requires pRational to have data");
	    StringBuffer buffer = new StringBuffer();
	    for (int i=0;i<length;i++){
	       buffer.append(((double)pRational[i])/pRational[i+1] + ", ");
 	    }
	    string = buffer.toString();
			break;
		 }
	  }
	  return string; 
   }   
void read(TiffInputSource infile) throws java.io.IOException, TiffException {
	long beginPosition = (int) infile.getFilePointer();
	int tag = tiffImg.readUnsignedShort();
	tagType = new TIFFTagName(tag);
	int entryType = tiffImg.readUnsignedShort();
	dataType = new TIFFDataType(entryType);
	length = tiffImg.readInt();
	long savePosition = (int) infile.getFilePointer();
	StringBuffer buffer = new StringBuffer();
	buffer.append("tag("+beginPosition+":"+savePosition+"): tagType="+tagType+"   dataType="+dataType+"   length="+length+"   ");
	savePosition += 4; // to allow for offset/embedded data
	//System.out.println("savePosition="+savePosition);
	//
	// read value
	//
	switch (dataType.getType()) {
		//
		// 8 bit data types
		//
		case TIFFDataType.TIFF_UNDEFINED :
		case TIFFDataType.TIFF_SBYTE :
		case TIFFDataType.TIFF_ASCII :
		case TIFFDataType.TIFF_BYTE :
			if (length <= 0)
				break;
			pChar = new byte[length];
			//
			// data in offset location
			//
			if (length <= 4) {
				infile.read(pChar);
				//
				// data pointed to by offset
				//
			} else {
				int valueOffset = tiffImg.readInt();
				//System.out.println("valueOffset="+valueOffset);
				infile.seek(valueOffset);
				infile.read(pChar);
			}
			buffer.append("data=[");
			for (int i = 0; i < length; i++){
				buffer.append(pChar[i]+" ");
			}
			buffer.append("])");
			break;
			//
			// 16 bit data types
			//
		case TIFFDataType.TIFF_SHORT :
		case TIFFDataType.TIFF_SSHORT :
			{
				if (length <= 0)
					break;
				pShort = new int[length];
				//
				// data in offset location
				//
				if (length <= 2) {
					for (int i = 0; i < length; i++) {
						pShort[i] = tiffImg.readUnsignedShort();
					}
					//
					// data pointed to by offset
					//
				} else {
					int valueOffset = tiffImg.readInt();
					//System.out.println("valueOffset="+valueOffset);
					infile.seek(valueOffset);
					for (int i = 0; i < length; i++) {
						pShort[i] = tiffImg.readUnsignedShort();
					}
				}
				buffer.append("data=[");
				for (int i = 0; i < length; i++){
					buffer.append(pShort[i]+" ");
				}
				buffer.append("])");
				break;
			}
			//
			// 32 bit data types
			//
		case TIFFDataType.TIFF_LONG :
		case TIFFDataType.TIFF_SLONG :
			{
				if (length <= 0)
					break;
				pLong = new int[length];
				//
				// data in offset location
				//
				if (length == 1) {
					pLong[0] = tiffImg.readInt();
					//
					// data pointed to by offset
					//
				} else {
					int valueOffset = tiffImg.readInt();
					//System.out.println("valueOffset="+valueOffset);
					infile.seek(valueOffset);
					for (int i = 0; i < length; i++) {
						pLong[i] = tiffImg.readInt();
					}
				}
				buffer.append("data=[");
				for (int i = 0; i < length; i++){
					buffer.append(pLong[i]+" ");
				}
				buffer.append("])");
				break;
			}
		case TIFFDataType.TIFF_FLOAT :
			if (length <= 0)
				break;
			pFloat = new float[length];
			//
			// data in offset location
			//
			if (length <= 1) {
				pFloat[0] = Float.intBitsToFloat(tiffImg.readInt());
				//
				// data pointed to by offset
				//
			} else {
				int valueOffset = tiffImg.readInt();
				//System.out.println("valueOffset="+valueOffset);
				infile.seek(valueOffset);
				for (int i = 0; i < length; i++) {
					pFloat[i] = Float.intBitsToFloat(tiffImg.readInt());
				}
			}
			buffer.append("data=[");
			for (int i = 0; i < length; i++){
				buffer.append(pFloat[i]+" ");
			}
			buffer.append("])");
			break;
			//
			// 64 bit data types
			//
		case TIFFDataType.TIFF_RATIONAL :
			{
				if (length <= 0)
					break;
				pRational = new int[length * 2];
				int valueOffset = tiffImg.readInt();
				//System.out.println("valueOffset="+valueOffset);
				infile.seek(valueOffset);
				//
				// data pointed to by offset
				//
				for (int i = 0; i < length; i++) {
					pRational[2 * i] = tiffImg.readInt();
					pRational[2 * i + 1] = tiffImg.readInt();
				}
				buffer.append("data=[");
				for (int i = 0; i < length; i+=2){
					buffer.append(pRational[2*i]+"/"+pRational[2*i+1]+" ");
				}
				buffer.append("])");
				break;
			}
		case TIFFDataType.TIFF_DOUBLE :
			if (length <= 0)
				break;
			pDouble = new double[length];
			int valueOffset = tiffImg.readInt();
			//System.out.println("valueOffset="+valueOffset);
			infile.seek(valueOffset);
			//
			// data pointed to by offset
			//
			for (int i = 0; i < length; i++) {
				pDouble[i] = Double.longBitsToDouble(tiffImg.readLong());
			}
			buffer.append("data=[");
			for (int i = 0; i < length; i++){
				buffer.append(pDouble[i]+" ");
			}
			buffer.append("])");
			break;
		case TIFFDataType.TIFF_NOTYPE :
		default :
			throw new TiffException("unknown data format <" + entryType + "> in Tag::read()");
	}
	System.out.println(buffer.toString());
	//      System.out.println(tagType+" ("+dataType+") = "+getValueString());
	infile.seek(savePosition);
}
void write(RandomAccessFile outfile) throws java.io.IOException, TiffException {
	tiffImg.writeUnsignedShort(tagType.getType());
	tiffImg.writeUnsignedShort(dataType.getType());
	tiffImg.writeInt(length);
	long savePosition = outfile.getFilePointer();
	savePosition += 4; // to allow for offset/embedded data
	//System.out.println("savePosition="+savePosition);
	//
	// read value
	//
	switch (dataType.getType()) {
		//
		// 8 bit data types
		//
		case TIFFDataType.TIFF_UNDEFINED :
		case TIFFDataType.TIFF_SBYTE :
		case TIFFDataType.TIFF_ASCII :
		case TIFFDataType.TIFF_BYTE :
			if (length <= 0)
				break;
			//
			// data in offset location
			//
			if (length <= 4) {
				outfile.write(pChar);
				//
				// data pointed to by offset
				//
			} else {
				throw new TiffException("dont support writing tags with remote data");
				// int valueOffset = tiffImg.writeInt();
				// infile.seek(valueOffset);
				// infile.read(pChar);
			}
			break;
			//
			// 16 bit data types
			//
		case TIFFDataType.TIFF_SHORT :
		case TIFFDataType.TIFF_SSHORT :
			{
				if (length <= 0)
					break;
				//
				// data in offset location
				//
				if (length <= 2) {
					for (int i = 0; i < length; i++) {
						tiffImg.writeUnsignedShort(pShort[i]);
					}
					for (int i = 0; i < 2; i++){
						tiffImg.writeUnsignedShort(0);
					}
					//
					// data pointed to by offset
					//
				} else {
					throw new TiffException("dont support writing tags with remote data");
					// int valueOffset = tiffImg.readInt();
					// infile.seek(valueOffset);
					// for (int i=0;i<length;i++){
					//   pShort[i] = tiffImg.readUnsignedShort();
					// }
				}
				break;
			}
			//
			// 32 bit data types
			//
		case TIFFDataType.TIFF_LONG :
		case TIFFDataType.TIFF_SLONG :
			{
				if (length <= 0)
					break;
				//
				// data in offset location
				//
				if (length == 1) {
					tiffImg.writeInt(pLong[0]);
					//
					// data pointed to by offset
					//
				} else {
					throw new TiffException("dont support writing tags with remote data");
					// int valueOffset = tiffImg.readInt();
					// infile.seek(valueOffset);
					// for (int i=0;i<length;i++){
					//   pLong[i] = tiffImg.readInt();
					// }
				}
				break;
			}
		case TIFFDataType.TIFF_FLOAT :
			if (length <= 0)
				break;
			//
			// data in offset location
			//
			if (length == 1) {
				tiffImg.writeInt(Float.floatToIntBits(pFloat[0]));
				//
				// data pointed to by offset
				//
			} else {
				throw new TiffException("dont support writing tags with remote data");
				// int valueOffset = tiffImg.readInt();
				// infile.seek(valueOffset);
				// for (int i=0;i<length;i++){
				//   pFloat[i] = Float.intBitsToFloat(tiffImg.readInt());
				// }
			}
			break;
			//
			// 64 bit data types
			//
		case TIFFDataType.TIFF_RATIONAL :
			{
				throw new TiffException("dont support writing tags with remote data");
				//  	   if (length<=0) break;
				//	   int valueOffset = tiffImg.readInt();
				//	   infile.seek(valueOffset);
				//	   //
				//	   // data pointed to by offset
				//	   //
				//	   for (int i=0;i<length;i++){
				//	     pRational[2*i] = tiffImg.readInt();
				//	     pRational[2*i+1] = tiffImg.readInt();
				// 	   }
				//           break;
			}
		case TIFFDataType.TIFF_DOUBLE :
			throw new TiffException("dont support writing tags with remote data");
			//  	   if (length<=0) break;
			//	   int valueOffset = tiffImg.readInt();
			//  	   infile.seek(valueOffset);
			//	   //
			//	   // data pointed to by offset
			//	   //
			//	   for (int i=0;i<length;i++){
			//	     pDouble[i] = Double.longBitsToDouble(tiffImg.readLong());
			// 	   }
			//           break;
		case TIFFDataType.TIFF_NOTYPE :
		default :
			throw new TiffException("unknown data format <" + dataType + "> in Tag::read()");
	}

	//      System.out.println("wrote ... "+tagType+" ("+dataType+") = "+getValueString());
	outfile.seek(savePosition);
}
}
