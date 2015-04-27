/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export.nrrd;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cbit.vcell.export.server.FileDataContainerManager.FileDataContainerID;

/**
 * Insert the type's description here.
 * Creation date: (4/23/2004 2:45:58 PM)
 * @author: Ion Moraru
 */
public class NrrdInfo {
	// always the same
	public static final String MAGIC = "NRRD0002";
	public static final String ENDIAN = "big";
	// supported formats
	public static final String[] types = {"double"};
	public static final String[] encodings = {"raw"};
	// required fields; initialized in factory constructor
	private int dimension = 0;
	private int[] sizes = null;
	private String type = null;
	private String encoding = null;
	// optional fields; setters should check for validity
	private FileDataContainerID dataFileID = null;
	private String content = "";
	private double min = Double.NaN;
	private double max = Double.NaN;
	private int lineskip = 0;
	private int byteskip = 0;
	private double[] spacings = null;
	private double[] axismins = null;
	private double[] axismaxs = null;
	private String[] centers = null;
	private String[] labels = null;
	private String[] units = null;
	// other entries
	private String mainComment = null;
	private boolean separateHeader = false;
	private boolean hasData = false;
	private FileDataContainerID headerFileID = null;
	private String canonicalFileName;
	
	public static enum NRRDAxisNames {X,Y,Z,T,V};
	private HashMap<NRRDAxisNames, Integer> axisToSizeIndexMap;
	
/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 10:46:51 AM)
 */
private NrrdInfo() {
	// always use factory methods	
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:31:24 PM)
 * @return cbit.vcell.export.nrrd.NrrdInfo
 * @param dimension int
 * @param sizes int[]
 * @param type java.lang.String
 * @param encoding java.lang.String
 * @exception java.lang.IllegalArgumentException The exception description.
 */
public static NrrdInfo createBasicNrrdInfo(int dimension, int[] sizes, String type, String encoding,HashMap<NRRDAxisNames, Integer> axisToSizeIndexMap) throws IllegalArgumentException {
	if (dimension < 1) throw new IllegalArgumentException("NRRD dimension must be greater than 0");
	if (sizes == null || sizes.length != dimension) throw new IllegalArgumentException("NRRD size array not same length with dimension");
	if (!org.vcell.util.BeanUtils.arrayContains(types, type)) throw new IllegalArgumentException("Unsupported NRRD type");
	if (!org.vcell.util.BeanUtils.arrayContains(encodings, encoding)) throw new IllegalArgumentException("Unsupported NRRD encoding");
	NrrdInfo info = new NrrdInfo();
	info.setDimension(dimension);
	info.setSizes(sizes);
	info.setType(type);
	info.setEncoding(encoding);
	info.axisToSizeIndexMap = axisToSizeIndexMap;
	return info;
}

public static HashMap<NRRDAxisNames, Integer> createXYZTVMap(){
	HashMap<NRRDAxisNames, Integer> xyztvMap = new HashMap<>();
	xyztvMap.put(NRRDAxisNames.X,0);
	xyztvMap.put(NRRDAxisNames.Y,1);
	xyztvMap.put(NRRDAxisNames.Z,2);
	xyztvMap.put(NRRDAxisNames.T,3);
	xyztvMap.put(NRRDAxisNames.V,4);
	return xyztvMap;
}
public static HashMap<NRRDAxisNames, Integer> createXYZVMap(){
	HashMap<NRRDAxisNames, Integer> xyztvMap = new HashMap<>();
	xyztvMap.put(NRRDAxisNames.X,0);
	xyztvMap.put(NRRDAxisNames.Y,1);
	xyztvMap.put(NRRDAxisNames.Z,2);
	xyztvMap.put(NRRDAxisNames.V,3);
	return xyztvMap;
}
public static HashMap<NRRDAxisNames, Integer> createXYZTMap(){
	HashMap<NRRDAxisNames, Integer> xyztvMap = new HashMap<>();
	xyztvMap.put(NRRDAxisNames.X,0);
	xyztvMap.put(NRRDAxisNames.Y,1);
	xyztvMap.put(NRRDAxisNames.Z,2);
	xyztvMap.put(NRRDAxisNames.T,3);
	return xyztvMap;
}
public void setCanonicalFileNamePrefix(String canonicalfileanme){
	this.canonicalFileName = canonicalfileanme;
}

public int getAxisSize(NRRDAxisNames axisName){
	return sizes[axisToSizeIndexMap.get(axisName)];
}
public String getCanonicalFilename(boolean bHeader){
	return canonicalFileName+(bHeader?"_header":"_data")+".nrrd";
}

public HashMap<NRRDAxisNames, Integer> getAxisToSizeInexMap(){
	return axisToSizeIndexMap;
}

public String getDimensionDescription(){
	StringBuffer sb = new StringBuffer();
	sb.append(getDimension()+"D(");
	ArrayList<Map.Entry<NRRDAxisNames,Integer>> axisList = new ArrayList<Map.Entry<NRRDAxisNames,Integer>>(axisToSizeIndexMap.entrySet());
	Collections.sort(axisList,  new Comparator<Entry<NRRDAxisNames, Integer>>() {
		@Override
		public int compare(Entry<NRRDAxisNames, Integer> o1, Entry<NRRDAxisNames, Integer> o2) {
			return o1.getValue() - o2.getValue();
		}
	});
	for(Entry<NRRDAxisNames, Integer> entry:axisList){
		sb.append(entry.getKey().toString()+(entry != axisList.get(axisList.size()-1)?",":""));
	}
	sb.append(")");
	return sb.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return double[]
 */
public double[] getAxismaxs() {
	return axismaxs;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return double[]
 */
public double[] getAxismins() {
	return axismins;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return int
 */
public int getByteskip() {
	return byteskip;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getCenters() {
	return centers;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return java.lang.String
 */
public java.lang.String getContent() {
	return content;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return java.lang.String
 */
public FileDataContainerID getDataFileID() {
	return dataFileID;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return int
 */
public int getDimension() {
	return dimension;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return java.lang.String
 */
public java.lang.String getEncoding() {
	return encoding;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 7:42:40 PM)
 * @return java.lang.String
 */
public FileDataContainerID getHeaderFileID() {
	return headerFileID;
}

/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getLabels() {
	return labels;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return int
 */
public int getLineskip() {
	return lineskip;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return java.lang.String
 */
public java.lang.String getMainComment() {
	return mainComment;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return double
 */
public double getMax() {
	return max;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return double
 */
public double getMin() {
	return min;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return int[]
 */
public int[] getSizes() {
	return sizes;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return double[]
 */
public double[] getSpacings() {
	return spacings;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return java.lang.String
 */
public java.lang.String getType() {
	return type;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:25:02 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getUnits() {
	return units;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 7:19:48 PM)
 * @return boolean
 */
public boolean isHasData() {
	return hasData;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 5:22:46 PM)
 * @return boolean
 */
public boolean isSeparateHeader() {
	return separateHeader;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newAxismaxs double[]
 */
public void setAxismaxs(double[] newAxismaxs) {
	axismaxs = newAxismaxs;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newAxismins double[]
 */
public void setAxismins(double[] newAxismins) {
	axismins = newAxismins;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newByteskip int
 */
public void setByteskip(int newByteskip) {
	byteskip = newByteskip;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newCenters java.lang.String[]
 */
public void setCenters(java.lang.String[] newCenters) {
	centers = newCenters;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newContent java.lang.String
 */
public void setContent(java.lang.String newContent) {
	content = newContent;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newDatafile java.lang.String
 */
public void setDataFileID(FileDataContainerID dataFileID) {
	this.dataFileID = dataFileID;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 3:13:22 PM)
 * @param newDimension int
 */
private void setDimension(int newDimension) {
	dimension = newDimension;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 3:13:22 PM)
 * @param newEncoding java.lang.String
 */
private void setEncoding(java.lang.String newEncoding) {
	encoding = newEncoding;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 7:19:48 PM)
 * @param newHasData boolean
 */
public void setHasData(boolean newHasData) {
	hasData = newHasData;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 7:42:40 PM)
 * @param newHeaderfile java.lang.String
 */
public void setHeaderFileID(FileDataContainerID headerFileID) {
	this.headerFileID = headerFileID;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newLabels java.lang.String[]
 */
public void setLabels(java.lang.String[] newLabels) {
	labels = newLabels;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newLineskip int
 */
public void setLineskip(int newLineskip) {
	lineskip = newLineskip;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newMainComment java.lang.String
 */
public void setMainComment(java.lang.String newMainComment) {
	mainComment = newMainComment;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newMax double
 */
public void setMax(double newMax) {
	max = newMax;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newMin double
 */
public void setMin(double newMin) {
	min = newMin;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 5:22:46 PM)
 * @param newSeparateHeader boolean
 */
public void setSeparateHeader(boolean newSeparateHeader) {
	separateHeader = newSeparateHeader;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 3:13:22 PM)
 * @param newSizes int[]
 */
private void setSizes(int[] newSizes) {
	sizes = newSizes;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newSpacings double[]
 */
public void setSpacings(double[] newSpacings) {
	spacings = newSpacings;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 3:13:22 PM)
 * @param newType java.lang.String
 */
private void setType(java.lang.String newType) {
	type = newType;
}


/**
 * Insert the method's description here.
 * Creation date: (4/26/2004 2:26:08 PM)
 * @param newUnits java.lang.String[]
 */
public void setUnits(java.lang.String[] newUnits) {
	units = newUnits;
}
}
