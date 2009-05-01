package cbit.vcell.model;

import cbit.vcell.parser.ExpressionBindingException;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.*;
import java.util.*;

import org.jdom.Element;
import org.vcell.util.Cacheable;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.MIRIAMAnnotatable;
import cbit.vcell.xml.MIRIAMAnnotation;

public abstract class Structure
	implements
		java.io.Serializable, ScopedSymbolTable, Matchable, Cacheable, java.beans.VetoableChangeListener,
		MIRIAMAnnotatable
{
	private String fieldName = new String();
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private org.vcell.util.document.KeyValue fieldKey = null;
	private StructureNameScope fieldNameScope = new Structure.StructureNameScope();
	private transient Model fieldModel = null;
	private MIRIAMAnnotation miriamAnnotation;
	private StructureSize fieldStructureSize = null;
	

	public class StructureNameScope extends BioNameScope {
		private NameScope children[] = new NameScope[0];
		
		public StructureNameScope(){
			super();
		}
		public cbit.vcell.parser.NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return org.vcell.util.TokenMangler.fixTokenStrict(Structure.this.getName());
		}
		public cbit.vcell.parser.NameScope getParent() {
			if (Structure.this.fieldModel != null){
				return Structure.this.fieldModel.getNameScope();
			}else{
				return null;
			}
		}
		public cbit.vcell.parser.ScopedSymbolTable getScopedSymbolTable() {
			return Structure.this;
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

		public cbit.vcell.parser.NameScope getNameScope() {
			return Structure.this.getNameScope();
		}
		
		public Structure getStructure(){
			return Structure.this;
		}

		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			switch (getDimension()){
				case 0: {
					return VCUnitDefinition.UNIT_DIMENSIONLESS;
				}
				case 1: {
					return VCUnitDefinition.UNIT_um;
				}
				case 2: {
					return VCUnitDefinition.UNIT_um2;
				}
				case 3: {
					return VCUnitDefinition.UNIT_um3;
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

public MIRIAMAnnotation getMIRIAMAnnotation() {
	return miriamAnnotation;
}
public void setMIRIAMAnnotation(MIRIAMAnnotation miriamAnnotation) {
	this.miriamAnnotation = miriamAnnotation;
	
}

public StructureSize getStructureSize(){
	if (fieldStructureSize == null){
		fieldStructureSize = new StructureSize(getDefaultStructureSizeName(fieldName));
	}
	return fieldStructureSize;
}

public static String getDefaultStructureSizeName(String structureName){
	return org.vcell.util.TokenMangler.fixTokenStrict(structureName);
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
	if(!Compare.isEqualOrNull(getMIRIAMAnnotation(), s.getMIRIAMAnnotation())){
		return false;
	}
	return true;
}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public abstract boolean enclosedBy(Structure parentStructure);
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
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	
	cbit.vcell.parser.SymbolTableEntry ste = getLocalEntry(identifierString);
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
public org.vcell.util.document.KeyValue getKey() {
	return fieldKey;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2004 9:59:19 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public cbit.vcell.parser.SymbolTableEntry getLocalEntry(java.lang.String identifier) throws cbit.vcell.parser.ExpressionBindingException {
	
	cbit.vcell.parser.SymbolTableEntry ste = ReservedSymbol.fromString(identifier);
	if (ste != null){
		ReservedSymbol rs = (ReservedSymbol)ste;
		if (rs.isX() || rs.isY() || rs.isZ()){
			throw new ExpressionBindingException("can't use x, y, or z, Physiological Models must be spatially independent");
		}
		return rs;
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
public cbit.vcell.parser.NameScope getNameScope() {
	return fieldNameScope;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Structure
 */
public abstract Structure getParentStructure();
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
 * Get sub features inside this structure.
 * If it is a feature, the sub features include all features inside it and itself.
 * If it is a membrance, the sub features include all features inside it.
 * Creation date: (12/11/2006 5:42:07 PM)
 * @return java.util.Enumeration
 */
public Enumeration<Feature> getSubFeatures() 
{
	Vector<Feature> subFeatures = new Vector<Feature>();
	Structure[] structures = this.getModel().getStructures();
	for (int i=0; i<structures.length; i++)
	{
		if((structures[i] instanceof Feature) && (structures[i].enclosedBy(this)))
		{
			subFeatures.addElement((Feature)structures[i]);
		}
	}
	return subFeatures.elements();
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
public void setName(String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}
/**
 * This method was created in VisualAge.
 * @param structure cbit.vcell.model.Structure
 */
public abstract void setParentStructure(Structure structure) throws ModelException;
/**
 * This method was created in VisualAge.
 * @param e java.beans.PropertyChangeEvent
 */
public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
	TokenMangler.checkNameProperty(this, "structure", e);
}
}
