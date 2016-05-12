/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.Serializable;
import java.util.Map;

import org.vcell.util.Cacheable;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.Identifiable;
import org.vcell.util.document.KeyValue;

import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

@SuppressWarnings("serial")
public abstract class Structure implements Serializable, ScopedSymbolTable, Matchable, Cacheable, VetoableChangeListener,
		Identifiable, IssueSource
{
	public final static String TYPE_NAME_FEATURE = "Compartment";
	public final static String TYPE_NAME_MEMBRANE = "Membrane";
	
	private String fieldName = new String();
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private KeyValue fieldKey = null;
	private StructureNameScope fieldNameScope = new Structure.StructureNameScope();
	private transient Model fieldModel = null;
	private StructureSize fieldStructureSize = null;
	private Structure sbmlParentStructure = null;

	// store SBML unit for compartment size from SBML.
	private transient VCUnitDefinition sbmlCompartmentSizeUnit = null;

	

	public class StructureNameScope extends BioNameScope {
		private NameScope children[] = new NameScope[0];
		
		public StructureNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(Structure.this.getName());
		}
		public NameScope getParent() {
			if (Structure.this.fieldModel != null){
				return Structure.this.fieldModel.getNameScope();
			}else{
				return null;
			}
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return Structure.this;
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.structureType;
		}
	}

	public class StructureSize extends ModelQuantity {

		@Override
		public String getDescription() {
			return "structure size";
		}

		public StructureSize(String name) {
			super(name);
		}

		public NameScope getNameScope() {
			return Structure.this.getNameScope();
		}
		
		public Structure getStructure(){
			return Structure.this;
		}

		public VCUnitDefinition getUnitDefinition() {
			ModelUnitSystem modelUnitSystem = getModel().getUnitSystem();
			switch (getDimension()){
				case 0: {
					return modelUnitSystem.getInstance_DIMENSIONLESS();
				}
				case 1: {
					return modelUnitSystem.getLengthUnit();
				}
				case 2: {
					return modelUnitSystem.getAreaUnit();
				}
				case 3: {
					return modelUnitSystem.getVolumeUnit();
				}
				default:{
					throw new RuntimeException("unexpected structure dimension: "+getDimension());
				}
			}
		}

		public boolean isUnitEditable() {
			return false;
		}

		public void setUnitDefinition(VCUnitDefinition unit) {
			throw new RuntimeException("cannot set units on structure sizes, structure '"+getStructure().getName()+"' is in units of "+getUnitDefinition().getSymbol());
		}
	}
	
protected Structure(KeyValue key){
	this.fieldKey = key;
	addVetoableChangeListener(this);
}      

public StructureSize getStructureSize(){
	if (fieldStructureSize == null){
		fieldStructureSize = new StructureSize(getDefaultStructureSizeName(fieldName));
	}
	return fieldStructureSize;
}

public static String getDefaultStructureSizeName(String structureName){
	return TokenMangler.fixTokenStrict(structureName);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
protected boolean compareEqual0(Structure s) {
	if (s == null){
		return false;
	}

	if (!getName().equals(s.getName())){
		return false;
	}
	return true;
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}
/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(java.lang.String identifierString) {
	
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
	return getNameScope().getExternalEntry(identifierString,this);
}
/**
 * Gets the key property (cbit.sql.KeyValue) value.
 * @return The key property value.
 * @see #setKey
 */
public KeyValue getKey() {
	return fieldKey;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 9:59:19 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getLocalEntry(java.lang.String identifier) {
	
	SymbolTableEntry ste = getModel().getReservedSymbolByName(identifier);
	if (ste != null){
		if (ste.equals(getModel().getX()) || ste.equals(getModel().getY()) || ste.equals(getModel().getZ())){
			throw new RuntimeException("can't use x, y, or z, Physiological Models must be spatially independent");
		}
//		if (ste.equals(MathFunctionDefinitions.fieldFunctionDefinition)){
//			throw new ExpressionBindingException("can't use field functions, Physiological Models must be spatially independent");
//		}
		return ste;
	}	

	if (this instanceof Membrane){
		Membrane.MembraneVoltage membraneVoltage = ((Membrane)this).getMembraneVoltage();
		if (membraneVoltage.getName().equals(identifier)){
			return membraneVoltage;
		}
	}
	
	if (getStructureSize().getName().equals(identifier)){
		return getStructureSize();
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 10:09:25 AM)
 * @return cbit.vcell.model.Model
 */
Model getModel() {
	return fieldModel;
}

public abstract int getDimension();

/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public String getName() {
	return fieldName;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 9:59:19 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return fieldNameScope;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}
/**
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
protected void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION --------- in Feature");
	exception.printStackTrace(System.out);
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 10:09:25 AM)
 * @param newModel cbit.vcell.model.Model
 */
void setModel(Model newModel) {
	fieldModel = newModel;
}
/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
protected void setName0(String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}
public abstract void setName(String name,boolean bFromGUI) throws PropertyVetoException;
/**
 * This method was created in VisualAge.
 * @param e java.beans.PropertyChangeEvent
 */
public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
	TokenMangler.checkNameProperty(this, "structure", e);
}

public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {	
	entryMap.put(getStructureSize().getName(), getStructureSize());
	if (this instanceof Membrane){
		Membrane.MembraneVoltage membraneVoltage = ((Membrane)this).getMembraneVoltage();
		entryMap.put(membraneVoltage.getName(), membraneVoltage);
	}
}

public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	getNameScope().getExternalEntries(entryMap);		
}

public abstract String getTypeName();
//public abstract String checkNewParent(Structure structure);

public VCUnitDefinition getSbmlCompartmentSizeUnit() {
	return sbmlCompartmentSizeUnit;
}


public void setSbmlCompartmentSizeUnit(VCUnitDefinition sbmlUnit) {
	this.sbmlCompartmentSizeUnit = sbmlUnit;
}

public Structure getSbmlParentStructure() {
	return sbmlParentStructure;
}

public void setSbmlParentStructure(Structure argSbmlParentStructure) {
	this.sbmlParentStructure = argSbmlParentStructure;
}

}

