/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.field.io;

import org.vcell.restclient.model.AnalyzedResultsFromFieldData;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Insert the type's description here.
 * Creation date: (1/10/2007 11:00:51 AM)
 * @author: Frank Morgan
 */
public class FieldDataFileOperationSpec implements java.io.Serializable{

	public static final int JOBINDEX_DEFAULT = 0;//parameter scan index
	
	public int opType = -1;
	public short[][][] shortSpecData;  //[time][var][data]
	public double[][][] doubleSpecData;  //[time][var][data]
	public CartesianMesh cartesianMesh;
	public ExternalDataIdentifier specEDI;
	public String[] varNames;
	public VariableType[] variableTypes;
	public double[] times;
	public org.vcell.util.document.User owner;
	public Origin origin;
	public Extent extent;
	public ISize isize;
	public String annotation;
	public int sourceSimParamScanJobIndex = -1;
	public KeyValue sourceSimDataKey;
	public User sourceOwner;
	public String fieldDataName;

	public static final int FDOS_ADD = 0;
	public static final int FDOS_DELETE = 1;
	public static final int FDOS_INFO = 2;
	public static final int FDOS_COPYSIM = 3;
	public static final int FDOS_DEPENDANTFUNCS = 4;

/**
 * Insert the method's description here.
 * Creation date: (1/22/2007 9:32:49 AM)
 */
public FieldDataFileOperationSpec() {
}

public FieldDataFileOperationSpec(short[][][] shortSpecData, double[][][] doubleSpecData, CartesianMesh cartesianMesh,
								  ExternalDataIdentifier specEDI, String[] varNames, VariableType[] variableTypes,
								  double[] times, User owner, Origin origin, Extent extent, ISize isize,
								  String annotation, int sourceSimParamScanJobIndex, KeyValue sourceSimDataKey,
								  User sourceOwner){
    this.shortSpecData = shortSpecData;
	this.doubleSpecData = doubleSpecData;
	this.cartesianMesh = cartesianMesh;
	this.specEDI = specEDI;
	this.varNames = varNames;
	this.variableTypes = variableTypes;
	this.times = times;
	this.owner = owner;
	this.origin = origin;
	this.extent = extent;
	this.isize = isize;
	this.annotation = annotation;
	this.sourceSimParamScanJobIndex = sourceSimParamScanJobIndex;
	this.sourceSimDataKey = sourceSimDataKey;
	this.sourceOwner = sourceOwner;
}

public static AnalyzedResultsFromFieldData fieldDataSpecToAnalyzedResultsDTO(FieldDataFileOperationSpec fieldDataFileOperationSpec){
	List<List<List<Integer>>> listVersion = Arrays.stream(fieldDataFileOperationSpec.shortSpecData) // Stream of short[][]
			.map(twoDArray -> Arrays.stream(twoDArray) // Stream of short[]
					.map(oneDArray -> {
						List<Integer> list = new ArrayList<>();
						for (short j : oneDArray) {
							list.add((int) j);
						}
						return list;
					}).collect(Collectors.toList())).collect(Collectors.toList());
	AnalyzedResultsFromFieldData analyzedResultsFromFieldData = new AnalyzedResultsFromFieldData();
	analyzedResultsFromFieldData.annotation(fieldDataFileOperationSpec.annotation); analyzedResultsFromFieldData.isize(ISize.iSizeToDTO(fieldDataFileOperationSpec.isize));
	analyzedResultsFromFieldData.extent(Extent.extentToDTO(fieldDataFileOperationSpec.extent)); analyzedResultsFromFieldData.origin(Origin.originToDTO(fieldDataFileOperationSpec.origin));
	analyzedResultsFromFieldData.times(Arrays.stream(fieldDataFileOperationSpec.times).boxed().toList()); analyzedResultsFromFieldData.setName(fieldDataFileOperationSpec.fieldDataName);
	analyzedResultsFromFieldData.varNames(Arrays.stream(fieldDataFileOperationSpec.varNames).toList()); analyzedResultsFromFieldData.shortSpecData(listVersion);
	return analyzedResultsFromFieldData;
}


@Deprecated
public static FieldDataFileOperationSpec createCopySimFieldDataFileOperationSpec(
		ExternalDataIdentifier newExtDataID,
		KeyValue argSourceSimDataKey,
		User argSourceOwner,
		int argSourceSimParamScanJobIndex,
		User destinationOwner){
	FieldDataFileOperationSpec fieldDataFileOperationSpec =
		new FieldDataFileOperationSpec();
	fieldDataFileOperationSpec.opType = FDOS_COPYSIM;
	fieldDataFileOperationSpec.specEDI = newExtDataID;
	fieldDataFileOperationSpec.sourceOwner = argSourceOwner;
	fieldDataFileOperationSpec.sourceSimDataKey = argSourceSimDataKey;
	fieldDataFileOperationSpec.sourceSimParamScanJobIndex = argSourceSimParamScanJobIndex;
	fieldDataFileOperationSpec.owner = destinationOwner;
	return fieldDataFileOperationSpec;
}

@Deprecated
public static FieldDataFileOperationSpec createInfoFieldDataFileOperationSpec(
		KeyValue argSourceSimDataKey,User argSourceOwner,int argSimParamScanJobIndex){
	FieldDataFileOperationSpec fieldDataFileOperationSpec =
		new FieldDataFileOperationSpec();
	fieldDataFileOperationSpec.opType = FDOS_INFO;
	fieldDataFileOperationSpec.sourceSimDataKey = argSourceSimDataKey;
	fieldDataFileOperationSpec.sourceOwner = argSourceOwner;
	fieldDataFileOperationSpec.sourceSimParamScanJobIndex = argSimParamScanJobIndex;
	return fieldDataFileOperationSpec;
}

@Deprecated
public static FieldDataFileOperationSpec createDeleteFieldDataFileOperationSpec(
		ExternalDataIdentifier deleteExtDataID){
	FieldDataFileOperationSpec fieldDataFileOperationSpec =
		new FieldDataFileOperationSpec();
	fieldDataFileOperationSpec.opType = FDOS_DELETE;
	fieldDataFileOperationSpec.specEDI = deleteExtDataID;
	return fieldDataFileOperationSpec;
}
//too expensive, takes forever
//public static FieldDataFileOperationSpec createDependantFuncsFieldDataFileOperationSpec(
//		ExternalDataIdentifier targetExtDataID){
//	FieldDataFileOperationSpec fieldDataFileOperationSpec =
//		new FieldDataFileOperationSpec();
//	fieldDataFileOperationSpec.opType = FDOS_DEPENDANTFUNCS;
//	fieldDataFileOperationSpec.specEDI = targetExtDataID;
//	return fieldDataFileOperationSpec;
//}
}
