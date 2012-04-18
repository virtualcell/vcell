/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.simdata;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class DataBlock implements java.io.Serializable
{
	final static int blockSize = 124 + 3 * 4;
	private String varName = null;
	private int variableTypeInt = 0;
	private int size = 0;
	private int dataOffset = 0;

   DataBlock()
   {
   }      


/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:27:07 PM)
 * @return cbit.vcell.simdata.DataBlock
 */
public static DataBlock createDataBlock(String arg_varName, int arg_varType, int arg_size, int arg_dataOffset) {
	DataBlock db = new DataBlock();
	db.varName = arg_varName;
	db.variableTypeInt = arg_varType;
	db.size = arg_size;
	db.dataOffset = arg_dataOffset;
	
	return db;
}


/**
 * This method was created in VisualAge.
 * @return long
 */
long getDataOffset() {
	return dataOffset;
}


/**
 * This method was created in VisualAge.
 * @return long
 */
int getSize() {
	return size;
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/01 1:40:35 PM)
 * @return int
 */
int getVariableTypeInteger() {
	return variableTypeInt;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
String getVarName() {
	return varName;
}


int readBlockHeader(DataInputStream fp) throws IOException{

	byte varNameBytes[] = new byte[124];
	fp.readFully(varNameBytes);
	varName = new String(varNameBytes);
	varName = varName.trim();
	variableTypeInt = fp.readInt();
	size = fp.readInt();
	dataOffset = fp.readInt();

	return varNameBytes.length+(3*4);
}      


/**
 * This method was created in VisualAge.
 * @param offset int
 */
void setDataOffset(int offset) {
}


void show() {
	System.out.println("var = '" + varName + "'");
/*
	try {
		getDoubleData();
		for (int i=0;i<size;i++){
			System.out.println("value("+i+") = " + doubleData[i]);
		}
	}catch (Exception e){
	}
*/
}   


   void writeBlockHeaderNew(DataOutputStream dos) throws IOException
   {
	  byte varNameBytes[] = new byte[124];
	  for (int i=0;i<varNameBytes.length;i++){
		  varNameBytes[i] = (byte)0;
	  }
	  byte[] bytes = varName.getBytes();
	  for (int i=0;i<bytes.length;i++){
		  varNameBytes[i] = bytes[i];
	  }
	  dos.write(varNameBytes);
	  dos.writeInt(variableTypeInt);
	  dos.writeInt(size);
	  dos.writeInt(dataOffset);    
   }               

}
