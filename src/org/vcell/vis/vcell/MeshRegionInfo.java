/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vis.vcell;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Vector;
/**
 * Insert the type's description here.
 * Creation date: (7/4/2001 5:04:16 PM)
 * @author: Frank Morgan
 */
public class MeshRegionInfo {
	//
	private Vector<MeshRegionInfo.VolumeRegionMapSubvolume> volumeRegionMapSubvolume = new Vector<MeshRegionInfo.VolumeRegionMapSubvolume>();
	private Vector<MembraneRegionMapVolumeRegion> membraneRegionMapVolumeRegion = new Vector<MembraneRegionMapVolumeRegion>();
	private int numVolumeElements;
	private byte[] fieldCompressedVolumeElementMapVolumeRegion;
	private byte[] fieldVolumeElementMapVolumeRegion;
	private int[] fieldMembraneElementMapMembraneRegion = null;
	//
	public class VolumeRegionMapSubvolume implements java.io.Serializable {
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
public java.util.Vector<MembraneRegionMapVolumeRegion> getMembraneRegionMapVolumeRegion() {
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

public int getNumVolumeRegions() {
	return volumeRegionMapSubvolume.size();
}

public java.util.Vector<VolumeRegionMapSubvolume> getVolumeRegionMapSubvolume() {
	return volumeRegionMapSubvolume;
}

public void mapMembraneRegionToVolumeRegion(int membraneRegionID, int volumeRegionInsideID, int volumeRegionOutsideID, double membraneRegionSurface) {
	//Integer membraneRegionIDIint = new Integer(membraneRegionID);
	MembraneRegionMapVolumeRegion mrmvr = new MembraneRegionMapVolumeRegion(membraneRegionID,volumeRegionInsideID,volumeRegionOutsideID,membraneRegionSurface);
	//membraneRegionMapVolumeRegion.put(membraneRegionIDIint,mrmvr);
	membraneRegionMapVolumeRegion.add(mrmvr);
}

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

public int getUncompressedVolumeElementMapVolumeRegionLength() {
	return getVolumeElementMapVolumeRegion().length;
}

public void mapMembraneElementsToMembraneRegions(int[] membraneElementMapMembraneRegion) {
	//MembraneElement is implicit in index of membraneElementMapMembraneRegion array
	fieldMembraneElementMapMembraneRegion = membraneElementMapMembraneRegion;
}

public int getVolumeElementMapVolumeRegion(int index) {
    
    if (isUnsignedShortDataType()) {
    	// unsigned short
    	return (int)((0x000000ff & getVolumeElementMapVolumeRegion()[2 * index]) | ((0x000000ff & getVolumeElementMapVolumeRegion()[2 * index + 1]) << 8));
    } else {
    	// byte
    	return (int)(0x000000ff & getVolumeElementMapVolumeRegion()[index]);
    }
}

private boolean isUnsignedShortDataType(){
	return getVolumeElementMapVolumeRegion().length == 2 * numVolumeElements;
}

public List<String> getVolumeDomainNames(){
	ArrayList<String> subvolumeNames = new ArrayList<String>();
	for (VolumeRegionMapSubvolume vMapSubvolume : volumeRegionMapSubvolume){
		if (!subvolumeNames.contains(vMapSubvolume.subdomain)){
			subvolumeNames.add(vMapSubvolume.subdomain);
		}
	}
	return subvolumeNames;
}

public List<Integer> getVolumeRegionIDs(String subvolumeName){
	ArrayList<Integer> regionIDs = new ArrayList<Integer>();
	for (VolumeRegionMapSubvolume vMapSubvolume : volumeRegionMapSubvolume){
		if (vMapSubvolume.subdomain.equals(subvolumeName)){
			regionIDs.add(vMapSubvolume.volumeRegionID);
		}
	}
	return regionIDs;
}

}
