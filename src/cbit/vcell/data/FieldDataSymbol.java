/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.data;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.document.ExternalDataIdentifier;

import cbit.vcell.units.VCUnitDefinition;

/**
 * This class implements a data symbol object which refers to general Field Data images
 * @author vasilescu
 *
 */
public class FieldDataSymbol extends DataSymbol {

	private ExternalDataIdentifier dataSetID;	// ID of the field database
	private String fieldDataVarName;		// name of a timepoint in the field database (may be different from data symbol name!)
	private String fieldDataVarType;
	private double fieldDataVarTime;
	
	public ExternalDataIdentifier getExternalDataIdentifier() {
		return dataSetID;
	}
	public String getFieldDataVarName() {
		return fieldDataVarName;
	}

	public String getFieldDataVarType() {
		return fieldDataVarType;
	}

	public double getFieldDataVarTime() {
		return fieldDataVarTime;
	}

	public FieldDataSymbol(String dataSymbolName, DataSymbolType dataSymbolType, 
			DataContext dataContext, VCUnitDefinition vcUnitDefinition,
			ExternalDataIdentifier dataSetID, 
			String fieldDataVarName, String fieldDataVarType, double fieldDataVarTime){
		super(dataSymbolName, dataSymbolType, dataContext, vcUnitDefinition);
		this.dataSetID = dataSetID;
		this.fieldDataVarName = fieldDataVarName;
		this.fieldDataVarType = fieldDataVarType;
		this.fieldDataVarTime = fieldDataVarTime;
	}
	public boolean compareEqual(Matchable obj) {
		FieldDataSymbol fieldDataSymbol = null;
		if (!(obj instanceof FieldDataSymbol)){
			return false;
		}else{
			fieldDataSymbol = (FieldDataSymbol)obj;
		}

		if(!super.compareEqual(obj)){
			return false;
		}
		if(!Compare.isEqualOrNull(dataSetID, fieldDataSymbol.dataSetID)){
			return false;
		}
		if(!Compare.isEqualOrNull(fieldDataVarName, fieldDataSymbol.fieldDataVarName)){
			return false;
		}
		if(!Compare.isEqualOrNull(fieldDataVarType, fieldDataSymbol.fieldDataVarType)){
			return false;
		}
		if(fieldDataVarTime != fieldDataSymbol.fieldDataVarTime){
			return false;
		}
		
		return true;
	}
}
