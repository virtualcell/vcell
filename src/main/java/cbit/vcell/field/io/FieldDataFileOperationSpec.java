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

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.math.VariableType;
import cbit.vcell.solvers.CartesianMesh;

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
