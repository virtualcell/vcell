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
import org.vcell.util.document.KeyValue;

import cbit.vcell.simdata.DataIdentifier;

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
	};

	
	public DataIdentifier[] dataIdentifierArr;
	public ISize iSize;
	public Origin origin;
	public Extent extent;
	public double[] times;
	public FieldDataFileOperationResults.FieldDataReferenceInfo[] dependantFunctionInfo;
	
public FieldDataFileOperationResults() {
	super();
}
}
