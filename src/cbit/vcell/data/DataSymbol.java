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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

/**
 * Creation date: (5/25/2010 11:12:01 AM)
 * @author dan
 * @version $Revision: 1.0 $
 */

public abstract class DataSymbol implements SymbolTableEntry,Matchable,Serializable {
	private String dataSymbolName = null;					// name of data symbol
	private DataSymbolType dataSymbolType;
	private VCUnitDefinition vcUnitDefinition = null;

	private transient PropertyChangeSupport propertyChangeSupport = null;
	private final DataContext dataSymbols;		// list of data symbols where we belong
	
	protected DataSymbol(String dataSymbolName, DataSymbolType dataSymbolType, DataContext dataSymbols, VCUnitDefinition vcUnitDefinition) {
		this.dataSymbolName = dataSymbolName;
		this.dataSymbolType = dataSymbolType;
		this.dataSymbols = dataSymbols;
		this.vcUnitDefinition = vcUnitDefinition;
	}	
	public boolean compareEqual(Matchable obj) {
		DataSymbol dataSymbol = null;
		if (!(obj instanceof DataSymbol)){
			return false;
		}else{
			dataSymbol = (DataSymbol)obj;
		}
		
		if(!Compare.isEqualOrNull(dataSymbolName, dataSymbol.dataSymbolName)){
			return false;
		}
		if(dataSymbolType != dataSymbol.dataSymbolType){
			return false;
		}
		if(!Compare.isEqualOrNull(vcUnitDefinition, dataSymbol.vcUnitDefinition)){
			return false;
		}
		return true;
	}
	public String getName() {
		return dataSymbolName;
	}
	public void setName(String newName){
		String oldName = this.dataSymbolName;
		this.dataSymbolName = newName;
		firePropertyChange("name", oldName, newName);
	}
	public DataSymbolType getDataSymbolType() {
		return dataSymbolType;
	}
	public void setDataSymbolType(DataSymbolType newType) {
		DataSymbolType oldDataSymbolType = this.dataSymbolType;
		this.dataSymbolType = newType;
		firePropertyChange("type", oldDataSymbolType, newType);
	}
	public final double getConstantValue() throws ExpressionException {
		throw new ExpressionException("cannot get constant value from a data symbol");
	}
	public Expression getExpression() {
		return null;
	}
	public final int getIndex() {
		return -1;
	}
	public final NameScope getNameScope() {
		return dataSymbols.getNameScope();
	}
	public final VCUnitDefinition getUnitDefinition() {
		return vcUnitDefinition;
	}
	public final boolean isConstant() throws ExpressionException {
		return false;
	}

	private PropertyChangeSupport getPropertyChangeSupport(){
		if (propertyChangeSupport==null){
			propertyChangeSupport = new PropertyChangeSupport(this);
		}
		return propertyChangeSupport;
	}
		private void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		getPropertyChangeSupport().firePropertyChange(propertyName, oldValue, newValue);
	}
		public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}
		public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}
	
	public enum DataSymbolType {
	    UNKNOWN					("Unknown",						"UNKNOWN"),
	    GENERIC_SYMBOL			("Generic",						"GENERIC"),
	    POINT_SPREAD_FUNCTION	("Point Spread Function",		"POINT_SPREAD_FUNCTION"),
	    VFRAP_TIMEPOINT			("vFrap Time Point", 			"VFRAP_TIMEPOINT"),
	    VFRAP_PREBLEACH_AVG		("vFrap Prebleach Avg",			"VFRAP_PREBLEACH_AVERAGE"),
	    VFRAP_FIRST_POSTBLEACH	("vFrap Postbleach",			"VFRAP_FIRST_POSTBLEACH"),
	    VFRAP_ROI				("vFrap ROI",					"VFRAP_ROI");

	    private final String displayName;
	    private final String databaseName; // DON'T Change
	    
	    DataSymbolType(String displayName, String databaseName) {
	        this.displayName = displayName;
	        this.databaseName = databaseName;
	    }
	    public String getDisplayName() {
	    	return displayName;
	    }
	    public String getDatabaseName() {
	    	return databaseName;
	    }
	    public static DataSymbolType fromDisplayName(String displayName) {
	        for (DataSymbolType dsType : DataSymbolType.values()){
	        	if (dsType.getDisplayName().equals(displayName)){
	        		return dsType;
	        	}
	        }
	        return null;
	    }
	    public static DataSymbolType fromDatabaseName(String databaseName) {
	        for (DataSymbolType dsType : DataSymbolType.values()){
	        	if (dsType.getDatabaseName().equals(databaseName)){
	        		return dsType;
	        	}
	        }
	        return null;
	    }
	}
}
