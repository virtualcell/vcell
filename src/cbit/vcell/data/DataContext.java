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

/**
 * Creation date: (5/25/2010 11:12:01 AM)
 * @author dan
 * @version $Revision: 1.0 $
 */
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.parser.NameScope;

/* 
 * Container for all data symbols
 */
public class DataContext implements Matchable, Serializable {

	private DataSymbol[] dataSymbols = new DataSymbol[0];
	
	private final NameScope namescope;
	private transient PropertyChangeSupport propertyChangeSupport = null;
	
	public DataContext(NameScope nameScope){
		this.namescope = nameScope;
//TODO: get rid of this next line
//		try {
//			Expression exp = new Expression("field(dataset1,var1,0.0,Volume)");
//			FunctionInvocation[] functionInvocations = exp.getFunctionInvocations(null);
//			addDataSymbol(new FieldDataSymbol("myFieldDataVariable", this, VCUnitDefinition.UNIT_TBD, new FieldFunctionArguments(functionInvocations[0])));
//		}catch (ExpressionException e){
//			e.printStackTrace(System.out);
//			throw new RuntimeException(e.getMessage());
//		}
	}
	
	public NameScope getNameScope(){
		return namescope;
	}
	
	public DataSymbol[] getDataSymbols() {
		return dataSymbols.clone();
	}
	
	public void addDataSymbol(DataSymbol dataSymbol) {
		if(contains(dataSymbol)) {		// data symbol name must be unique
			throw new RuntimeException("data symbol already exists");
		}
		DataSymbol[] newArray = (DataSymbol[])BeanUtils.addElement(dataSymbols,dataSymbol);
		setDataSymbols(newArray);
	}
	
	public void setDataSymbols(DataSymbol[] newDataSymbols) {
		DataSymbol[] oldValue = this.dataSymbols;
		this.dataSymbols = newDataSymbols;
		firePropertyChange("dataSymbols", oldValue, dataSymbols);
	}
	
	public DataSymbol getDataSymbol(String dataRef) {
		for (DataSymbol dataSymbol : dataSymbols){
			if (dataSymbol.getName().equals(dataRef)) {
				return dataSymbol;
			}
		}
		return null;
	}
	
	public void removeDataSymbol(DataSymbol dataSymbol) {
		if (!BeanUtils.arrayContains(dataSymbols,dataSymbol)){
			throw new RuntimeException("data symbol doesn't exist");
		}
		DataSymbol[] newArray = (DataSymbol[])BeanUtils.removeElement(dataSymbols,dataSymbol);
		setDataSymbols(newArray);
	}

	public boolean contains(DataSymbol dataSymbol) {
		for (DataSymbol ds : dataSymbols){
			if (ds.getName().equals(dataSymbol.getName())){
				return true;
			}
		}
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

	public boolean compareEqual(Matchable obj) {
		DataContext dataContext = null;
		if (!(obj instanceof DataContext)){
			return false;
		}else{
			dataContext = (DataContext)obj;
		}
		if(!Compare.isEqualOrNull(dataSymbols, dataContext.dataSymbols)){
			return false;
		}
		return true;
	}
}
