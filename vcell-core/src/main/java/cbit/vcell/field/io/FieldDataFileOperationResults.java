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

import org.vcell.restclient.model.FieldDataReferenceInfo;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.document.KeyValue;

import cbit.vcell.simdata.DataIdentifier;

import java.util.Arrays;

/**
 * Insert the type's description here.
 * Creation date: (1/10/2007 11:01:45 AM)
 * @author: Frank Morgan
 */
public class FieldDataFileOperationResults implements java.io.Serializable{
/**
 * FieldDataStorageResults constructor comment.
 */
	public static class FieldDataReferenceInfo{
		public static final String FIELDDATATYPENAME = "FieldData";
		public String referenceSourceType;
		public String referenceSourceName;
		public String applicationName;
		public String simulationName;
		public String refSourceVersionDate;
		public String[] funcNames;
		public KeyValue refSourceVersionKey;

		public static FieldDataReferenceInfo dtoToFielddataFileOperationResults(org.vcell.restclient.model.FieldDataReferenceInfo dto){
			FieldDataReferenceInfo fieldDataReferenceInfo = new FieldDataReferenceInfo();
			fieldDataReferenceInfo.referenceSourceType = dto.getReferenceSourceType();
			fieldDataReferenceInfo.referenceSourceName = dto.getReferenceSourceName();
			fieldDataReferenceInfo.simulationName = dto.getSimulationName();
			fieldDataReferenceInfo.applicationName = dto.getApplicationName();
			fieldDataReferenceInfo.refSourceVersionDate = dto.getRefSourceVersionDate();
			fieldDataReferenceInfo.funcNames = dto.getFuncNames().toArray(new String[0]);
			fieldDataReferenceInfo.refSourceVersionKey = KeyValue.dtoToKeyValue(dto.getRefSourceVersionKey());
			return fieldDataReferenceInfo;
		}
	};

	
	public DataIdentifier[] dataIdentifierArr;
	public ISize iSize;
	public Origin origin;
	public Extent extent;
	public double[] times;
	public FieldDataFileOperationResults.FieldDataReferenceInfo[] dependantFunctionInfo;

	public static FieldDataFileOperationResults dtoToFieldDataFileOperationResults(org.vcell.restclient.model.FieldDataFileOperationResults dto){
		FieldDataFileOperationResults fieldDataFileOperationResults = new FieldDataFileOperationResults();
		fieldDataFileOperationResults.extent = Extent.dtoToExtent(dto.getExtent());
		fieldDataFileOperationResults.iSize = ISize.dtoToISize(dto.getiSize());
		fieldDataFileOperationResults.origin = Origin.dtoToOrigin(dto.getOrigin());
		fieldDataFileOperationResults.dataIdentifierArr = dto.getDataIdentifierArr().stream().map(DataIdentifier::dtoToDataIdentifier).toArray(DataIdentifier[]::new);
        fieldDataFileOperationResults.times = dto.getTimes().stream().mapToDouble(Double::doubleValue).toArray();
		fieldDataFileOperationResults.dependantFunctionInfo = dto.getDependantFunctionInfo().stream().map(FieldDataReferenceInfo::dtoToFielddataFileOperationResults).toArray(FieldDataReferenceInfo[]::new);
		return fieldDataFileOperationResults;
	}
	
public FieldDataFileOperationResults() {
	super();
}
}
