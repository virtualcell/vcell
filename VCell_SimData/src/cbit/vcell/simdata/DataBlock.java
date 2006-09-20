package cbit.vcell.simdata;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;

class DataBlock implements java.io.Serializable
{
	final static int blockSize = 128 + 32 + 32;
	private String varName = null;
	private int variableTypeInt = 0;
	private int size = 0;
	private int dataOffset = 0;
   DataBlock()
   {
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
   void writeBlockHeader(RandomAccessFile fp) throws IOException
   {
	  byte varNameBytes[] = new byte[124];
	  for (int i=0;i<varNameBytes.length;i++){
	  varNameBytes[i] = (byte)0;
	  }
	  for (int i=0;i<varName.getBytes().length;i++){
	  varNameBytes[i] = (varName.getBytes())[i];
	  }
	  fp.write(varNameBytes);
	  fp.writeInt(variableTypeInt);
	  fp.writeInt(size);
	  fp.writeInt(dataOffset);    
   }               
}
