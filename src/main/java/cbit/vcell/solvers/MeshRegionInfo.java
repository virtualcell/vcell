/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;
import java.util.BitSet;
import java.util.Vector;
/**
 * Insert the type's description here.
 * Creation date: (7/4/2001 5:04:16 PM)
 * @author: Frank Morgan
 */
public class MeshRegionInfo implements org.vcell.util.Matchable, java.io.Serializable {
	//
	private Vector<MeshRegionInfo.VolumeRegionMapSubvolume> volumeRegionMapSubvolume = new Vector<MeshRegionInfo.VolumeRegionMapSubvolume>();
	private Vector<MembraneRegionMapVolumeRegion> membraneRegionMapVolumeRegion = new Vector<MembraneRegionMapVolumeRegion>();
	private byte[] fieldCompressedVolumeElementMapVolumeRegion = null;
	private transient byte[] fieldVolumeElementMapVolumeRegion = null;
	private int[] fieldMembraneElementMapMembraneRegion = null;
	private int numVolumeElements;
	//
	class VolumeRegionMapSubvolume implements java.io.Serializable {
		public final int volumeRegionID;
		public final int subvolumeID;
		public final double volumeRegionVolume;
		public final String subdomain;
		
		public VolumeRegionMapSubvolume(int argvolumeRegionID,int argsubvolumeID,double argvolumeRegionVolume, String argsubdomain){
			volumeRegionID = argvolumeRegionID;
			subvolumeID = argsubvolumeID;
			volumeRegionVolume = argvolumeRegionVolume;
			subdomain = argsubdomain;
		}
	}

	class MembraneRegionMapVolumeRegion implements java.io.Serializable {
		public final int membraneRegionID;
		public final int volumeRegionInsideID;
		public final int volumeRegionOutsideID;
		public final double membraneRegionSurface;
		public MembraneRegionMapVolumeRegion(int argmembraneRegionID,int argvolumeRegionInsideID,int argvolumeRegionOutsideID,double argmembraneRegionSurface){
			membraneRegionID = 		argmembraneRegionID;
			volumeRegionInsideID = 	argvolumeRegionInsideID;
			volumeRegionOutsideID = argvolumeRegionOutsideID;
			membraneRegionSurface = argmembraneRegionSurface;
		}
	}

/**
 * VolumeRegion constructor comment.
 */
public MeshRegionInfo() {
	super();
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (7/4/2001 6:00:49 PM)
 * @return byte[]
 */
private void compress() throws java.io.IOException {
    java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
    java.util.zip.DeflaterOutputStream dos =
        new java.util.zip.DeflaterOutputStream(bos);
    dos.write(
        fieldVolumeElementMapVolumeRegion,
        0,
        fieldVolumeElementMapVolumeRegion.length);
    dos.flush();
    dos.close();
    fieldCompressedVolumeElementMapVolumeRegion = bos.toByteArray();
    bos.close();
}


/**
 * Insert the method's description here.
 * Creation date: (7/4/2001 5:50:56 PM)
 * @param cvemvr byte[]
 */
public byte[] getCompressedVolumeElementMapVolumeRegion() {
    if (fieldCompressedVolumeElementMapVolumeRegion == null) {
        if (fieldVolumeElementMapVolumeRegion != null) {
            try {
                compress();
            } catch (java.io.IOException e) {
                throw new RuntimeException(e.toString());
            }
        } else {
            return null;
        }
    }
    return fieldCompressedVolumeElementMapVolumeRegion;
}


/**
 * Insert the method's description here.
 * Creation date: (7/5/2001 12:25:30 PM)
 * @return int
 * @param membraneElementIndex int
 */
public int getMembraneRegionForMembraneElement(int membraneElementIndex) {
	return fieldMembraneElementMapMembraneRegion[membraneElementIndex];
}


/**
 * Insert the method's description here.
 * Creation date: (8/8/2005 11:33:14 AM)
 * @return java.util.Vector
 */
java.util.Vector<MembraneRegionMapVolumeRegion> getMembraneRegionMapVolumeRegion() {
	return membraneRegionMapVolumeRegion;
}

public BitSet getVolumeROIFromVolumeRegionID(int volumeRegionID){
	BitSet roiVolumeRegionID = new BitSet(numVolumeElements);
    if (isUnsignedShortDataType()) {
    	// unsigned short
    	for (int i = 0; i < numVolumeElements; i++) {
    		if(((int)((0x000000ff & getVolumeElementMapVolumeRegion()[2 * i]) | ((0x000000ff & getVolumeElementMapVolumeRegion()[2 * i + 1]) << 8))) == volumeRegionID){
    			roiVolumeRegionID.set(i);
    		}
    	}
    } else {
    	// byte
    	for (int i = 0; i < numVolumeElements; i++) {
    		if((int)(0x000000ff & getVolumeElementMapVolumeRegion()[i]) == volumeRegionID){
    			roiVolumeRegionID.set(i);
    		}
    	}
    }
	return roiVolumeRegionID;
}

public BitSet getMembraneROIFromMembraneRegionID(int membraneRegionID){
	BitSet roiMembraneRegionID = new BitSet(fieldMembraneElementMapMembraneRegion.length);
	for (int i = 0; i < fieldMembraneElementMapMembraneRegion.length; i++) {
		if(fieldMembraneElementMapMembraneRegion[i] == membraneRegionID){
			roiMembraneRegionID.set(i);
		}
	}
	return roiMembraneRegionID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/3/2002 11:35:24 PM)
 * @return int
 */
public int getNumMembraneRegions() {
	return membraneRegionMapVolumeRegion.size();
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/2002 11:35:24 PM)
 * @return int
 */
public int getNumVolumeRegions() {
	return volumeRegionMapSubvolume.size();
}


/**
 * Insert the method's description here.
 * Creation date: (9/20/2005 4:58:05 PM)
 * @return double
 * @param membraneIndex int
 */
public double getRegionMembraneSurfaceAreaFromMembraneIndex(int membraneIndex) {

	for(int i=0;i<membraneRegionMapVolumeRegion.size();i+= 1){
		MembraneRegionMapVolumeRegion mrmvr = (MembraneRegionMapVolumeRegion)membraneRegionMapVolumeRegion.elementAt(i);
		if(mrmvr.membraneRegionID == getMembraneRegionForMembraneElement(membraneIndex)){
			return mrmvr.membraneRegionSurface;
		}
	}
	
	throw new RuntimeException("Couldn't find surface area from membraneIndex="+membraneIndex);
}


/**
 * Insert the method's description here.
 * Creation date: (8/20/2003 1:39:54 PM)
 * @return int
 * @param volRegion int
 */
public int getSubVolumeIDfromVolRegion(int volRegion) {
	for (int i = 0; i < volumeRegionMapSubvolume.size(); i++){
		MeshRegionInfo.VolumeRegionMapSubvolume	map = (MeshRegionInfo.VolumeRegionMapSubvolume)volumeRegionMapSubvolume.elementAt(volRegion);
		if (map.volumeRegionID == volRegion) {
			return map.subvolumeID;
		}
	}
	throw new RuntimeException("Volume Region "+volRegion+" not found!");
}


public String getSubdomainNamefromVolIndex(int volIndex) {
	int volRegion = getVolumeElementMapVolumeRegion(volIndex);
	if (volRegion > volumeRegionMapSubvolume.size()) {
		throw new RuntimeException("Volume Region not found for volume index " + volIndex + "!");
	}
	
	if (volumeRegionMapSubvolume.elementAt(volRegion).subdomain == null) {
		throw new RuntimeException("Subdomain name not found for volume index " + volIndex + "!");
	}
	return volumeRegionMapSubvolume.elementAt(volRegion).subdomain;	
}

/**
 * Insert the method's description here.
 * Creation date: (7/5/2001 1:05:24 PM)
 * @return java.lang.String
 */
public String getVCMLMembraneRegionsMapVolumeRegion() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+cbit.vcell.math.VCML.MembraneRegionsMapVolumeRegion+" {\n");
	int numMembraneRegionsMapVolumeRegion = membraneRegionMapVolumeRegion.size();
	buffer.append("\t"+numMembraneRegionsMapVolumeRegion+"\n");
	for(int i = 0;i<numMembraneRegionsMapVolumeRegion;i+= 1){
		MembraneRegionMapVolumeRegion mrmvr = (MembraneRegionMapVolumeRegion)membraneRegionMapVolumeRegion.elementAt(i);
		buffer.append("\t\t"+mrmvr.membraneRegionID+" "+mrmvr.volumeRegionInsideID+" "+mrmvr.volumeRegionOutsideID+" "+mrmvr.membraneRegionSurface+"\n");
	}
	buffer.append("\t}\n");

	return buffer.toString();

}


/**
 * Insert the method's description here.
 * Creation date: (7/5/2001 1:05:24 PM)
 * @return java.lang.String
 */
public String getVCMLVolumeRegionMapSubvolume() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+cbit.vcell.math.VCML.VolumeRegionsMapSubvolume+" {\n");
	int numVolumeRegionMapSubvolume = volumeRegionMapSubvolume.size();
	buffer.append("\t"+numVolumeRegionMapSubvolume+"\n");
	for(int i = 0;i<numVolumeRegionMapSubvolume;i+= 1){
		VolumeRegionMapSubvolume vrms = (VolumeRegionMapSubvolume)volumeRegionMapSubvolume.elementAt(i);
		buffer.append("\t\t"+vrms.volumeRegionID+" "+vrms.subvolumeID+" "+vrms.volumeRegionVolume+"\n");
	}
	buffer.append("\t}\n");

	return buffer.toString();

}

private byte[] getVolumeElementMapVolumeRegion() {
    if (fieldVolumeElementMapVolumeRegion == null) {
        if (fieldCompressedVolumeElementMapVolumeRegion != null) {
            try{
	            fieldVolumeElementMapVolumeRegion = uncompress(fieldCompressedVolumeElementMapVolumeRegion);
            }catch(java.io.IOException e){
	            throw new RuntimeException(e.getMessage());
            }
        } else {
	        throw new RuntimeException("MeshRegionInfo no compressed volume element map volume region data");
        }
    }
    return fieldVolumeElementMapVolumeRegion;
}
/**
 * Insert the method's description here.
 * Creation date: (7/4/2001 5:50:56 PM)
 * @param cvemvr byte[]
 */
public int getVolumeElementMapVolumeRegion(int index) {
    
    if (isUnsignedShortDataType()) {
    	// unsigned short
    	return (int)((0x000000ff & getVolumeElementMapVolumeRegion()[2 * index]) | ((0x000000ff & getVolumeElementMapVolumeRegion()[2 * index + 1]) << 8));
    } else {
    	// byte
    	return (int)(0x000000ff & getVolumeElementMapVolumeRegion()[index]);
    }
}

public int getUncompressedVolumeElementMapVolumeRegionLength() {
	return getVolumeElementMapVolumeRegion().length;
}

/**
 * Insert the method's description here.
 * Creation date: (8/8/2005 11:24:31 AM)
 * @return java.util.Vector
 */
java.util.Vector<VolumeRegionMapSubvolume> getVolumeRegionMapSubvolume() {
	return volumeRegionMapSubvolume;
}

private boolean isUnsignedShortDataType(){
	return getVolumeElementMapVolumeRegion().length == 2 * numVolumeElements;
}
/**
 * Insert the method's description here.
 * Creation date: (7/5/2001 11:03:19 AM)
 * @param membraneElementMapMembraneRegion byte[]
 */
public void mapMembraneElementsToMembraneRegions(int[] membraneElementMapMembraneRegion) {
	//MembraneElement is implicit in index of membraneElementMapMembraneRegion array
	fieldMembraneElementMapMembraneRegion = membraneElementMapMembraneRegion;
}


/**
 * Insert the method's description here.
 * Creation date: (7/4/2001 5:07:49 PM)
 * @param volumeRegionID int
 * @param subvolumeID int
 * @param volume double
 */
public void mapMembraneRegionToVolumeRegion(int membraneRegionID, int volumeRegionInsideID, int volumeRegionOutsideID, double membraneRegionSurface) {
	//Integer membraneRegionIDIint = new Integer(membraneRegionID);
	MembraneRegionMapVolumeRegion mrmvr = new MembraneRegionMapVolumeRegion(membraneRegionID,volumeRegionInsideID,volumeRegionOutsideID,membraneRegionSurface);
	//membraneRegionMapVolumeRegion.put(membraneRegionIDIint,mrmvr);
	membraneRegionMapVolumeRegion.add(mrmvr);
}


/**
 * Insert the method's description here.
 * Creation date: (7/4/2001 5:07:49 PM)
 * @param volumeRegionID int
 * @param subvolumeID int
 * @param volume double
 */
public void mapVolumeRegionToSubvolume(int volumeRegionID, int subvolumeID, double volumeRegionVolume, String subdomain) {
	//Integer volumeRegionIDIint = new Integer(volumeRegionID);
	VolumeRegionMapSubvolume vrms = new VolumeRegionMapSubvolume(volumeRegionID,subvolumeID,volumeRegionVolume,subdomain);
	//volumeRegionMapSubvolume.put(volumeRegionIDIint,vrms);
	volumeRegionMapSubvolume.add(vrms);
}


/**
 * Insert the method's description here.
 * Creation date: (7/4/2001 5:50:56 PM)
 * @param cvemvr byte[]
 */
public void setCompressedVolumeElementMapVolumeRegion(byte[] cvemvr, int nve) throws java.io.IOException{
    fieldCompressedVolumeElementMapVolumeRegion = cvemvr;
    numVolumeElements = nve;
}


/**
 * Insert the method's description here.
 * Creation date: (7/4/2001 6:00:49 PM)
 * @return byte[]
 */
private byte[] uncompress(byte[] compressedIN) throws java.io.IOException {
    java.io.ByteArrayInputStream bis =
        new java.io.ByteArrayInputStream(compressedIN);
    java.util.zip.InflaterInputStream iis =
        new java.util.zip.InflaterInputStream(bis);
    int temp;
    byte buf[] = new byte[65536];
    java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
    while ((temp = iis.read(buf, 0, buf.length)) != -1) {
        bos.write(buf, 0, temp);
    }
    byte[] tempArr = bos.toByteArray();
    iis.close();
    bos.close();
    return tempArr;
}
}
