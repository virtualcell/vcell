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
import java.io.*;

class FileHeader implements java.io.Serializable
{
   final static int headerSize = 16 + 8 + 4 * 5;
   final static String MAGIC = "VCell Data Dump";
   final static String VERSION = "2.0.1";
   String versionString=null;
   int numBlocks=0;
   int firstBlockOffset=0;
   int sizeX=0;
   int sizeY=0;
   int sizeZ=0;
   
   FileHeader()
   {      
   }      


int read(DataInputStream fp) throws IOException{

	byte magicStringBytes[] = new byte[16];
	byte versionStringBytes[] = new byte[8];
	
	fp.readFully(magicStringBytes);
	fp.readFully(versionStringBytes);
		
	String magicString = new String(magicStringBytes);
	versionString = new String(versionStringBytes);
	if (MAGIC.equals(magicString.trim())==false){
		System.out.println("read magic = '"+magicString+"' MAGIC = '"+MAGIC+"'");
		throw new IOException("bad magic string '" + magicString + "'");
	}
	
	numBlocks = fp.readInt();
	firstBlockOffset = fp.readInt();
	sizeX = fp.readInt();
	sizeY = fp.readInt();
	sizeZ = fp.readInt();
	
	return magicStringBytes.length+versionStringBytes.length+(5*4);
}                  


   void writeNew(DataOutputStream dataOS) throws IOException
   {
	  byte magicStringBytes[] = new byte[16];
	  byte versionStringBytes[] = new byte[8];
	  for (int i=0;i<16;i++){
	  magicStringBytes[i] = (byte)0;
	  }
	  for (int i=0;i<MAGIC.getBytes().length;i++){
	  magicStringBytes[i] = (MAGIC.getBytes())[i];
	  }
	  if (versionString == null) versionString = VERSION;
	  for (int i=0;i<8;i++){
	  versionStringBytes[i] = (byte)0;
	  }
	  for (int i=0;i<versionString.getBytes().length;i++){
	  versionStringBytes[i] = (versionString.getBytes())[i];
	  }
	  dataOS.write(magicStringBytes);
	  dataOS.write(versionStringBytes);
	  dataOS.writeInt(numBlocks);
	  dataOS.writeInt(firstBlockOffset);   
	  dataOS.writeInt(sizeX); 
	  dataOS.writeInt(sizeY); 
	  dataOS.writeInt(sizeZ); 
   }                  
}
