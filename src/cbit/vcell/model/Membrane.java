package cbit.vcell.model;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.PropertyVetoException;
import java.util.*;

import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;

import cbit.vcell.parser.Expression;
import cbit.vcell.units.VCUnitDefinition;

public class Membrane extends Structure {
	public Feature insideFeature = null;
	public Feature outsideFeature = null;
	private MembraneVoltage fieldMembraneVoltage = null;

	public final static String MEMBRANE_VOLTAGE_REGION_NAME = "SpatialMembraneVoltage";	
	
	public class MembraneVoltage extends ModelQuantity {

		@Override
		public String getDescription() {
			return "membrane voltage";
		}

		public MembraneVoltage(String name) {
			super(name);
		}

		public cbit.vcell.parser.NameScope getNameScope() {
			return Membrane.this.getNameScope();
		}
		
		public Membrane getMembrane(){
			return Membrane.this;
		}

		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return cbit.vcell.units.VCUnitDefinition.UNIT_mV;
		}

		public void setUnitDefinition(VCUnitDefinition unit) {
			throw new RuntimeException("Cannot set units on Membrane Voltage, only "+getUnitDefinition().getSymbol()+" is supported");
		}

		public boolean isUnitEditable() {
			return false;
		}

	}
	
	
	
public Membrane(KeyValue key, String name) throws java.beans.PropertyVetoException {
	super(key);
	setName(name);
	fieldMembraneVoltage = new MembraneVoltage(getDefaultMembraneVoltageName(name));
}


public Membrane(String name) throws java.beans.PropertyVetoException {
	this(null,name);
}

public static String getDefaultMembraneVoltageName(String structureName){
	return org.vcell.util.TokenMangler.fixTokenStrict("Voltage_"+structureName);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	if (obj instanceof Membrane){
		Membrane m = (Membrane)obj;
		if (!compareEqual0(m)){
			return false;
		}
		if ((getInsideFeature()!=null && m.getInsideFeature()==null) ||
			(getInsideFeature()==null && m.getInsideFeature()!=null)){
			return false;
		}
		if (getInsideFeature()!=null && !getInsideFeature().getName().equals(m.getInsideFeature().getName())){
			return false;
		}
		if ((getOutsideFeature()!=null && m.getOutsideFeature()==null) ||
			(getOutsideFeature()==null && m.getOutsideFeature()!=null)){
			return false;
		}
		if (getOutsideFeature()!=null && !getOutsideFeature().getName().equals(m.getOutsideFeature().getName())){
			return false;
		}
		if (!getMembraneVoltage().compareEqual(m.getMembraneVoltage())){
			return false;
		}
		return true;
	}else{
		return false;
	}
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public boolean enclosedBy(Structure parentStructure){
	if (parentStructure == this){
		return true;
	}	
	return getOutsideFeature().enclosedBy(parentStructure);
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Feature
 */
public Feature getInsideFeature() {
	return insideFeature;
}


/**
 * Gets the membraneVoltage property (cbit.vcell.model.MembraneVoltage) value.
 * @return The membraneVoltage property value.
 * @see #setMembraneVoltage
 */
public MembraneVoltage getMembraneVoltage() {
	return fieldMembraneVoltage;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Feature
 */
public Feature getOutsideFeature() {
	return outsideFeature;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Structure
 */
public Structure getParentStructure() {
	return getOutsideFeature();
}


/**
 * This method was created by a SmartGuide.
 * @param insideFeature cbit.vcell.model.Feature
 */
public void setInsideFeature(Feature insideFeature) {
	this.insideFeature = insideFeature;
	if (insideFeature!=null && insideFeature.getMembrane() != this){
		insideFeature.setMembrane(this);
	}
}

/**
 * This method was created by a SmartGuide.
 * @param insideFeature cbit.vcell.model.Feature
 */
public void setOutsideFeature(Feature outsideFeature) {
	this.outsideFeature = outsideFeature;
}


/**
 * This method was created in VisualAge.
 * @param structure cbit.vcell.model.Structure
 */
public void setParentStructure(Structure structure) throws ModelException {
	if (structure instanceof Feature){
		Feature feature = (Feature)structure;
		//
		// check for cyclic parenthood
		//
		Structure s = feature.getParentStructure();
		while (s!=null){
			if (s == this){
				throw new ModelException("cannot make parent relationship cyclic");
			}
			s = s.getParentStructure();
		}
		setOutsideFeature(feature);
	}else{
		throw new ModelException("parent structure must be a Feature");
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append("Membrane@"+Integer.toHexString(hashCode())+"(name='"+getName());
	
	if (insideFeature != null){ sb.append(", insideFeature = '"+insideFeature.getName()+"'"); }
	if (outsideFeature != null){ sb.append(", outsideFeature = '"+outsideFeature.getName()+"'"); }
	sb.append(")");
	
	return sb.toString();
}

@Override
public int getDimension() {
	return 2;
}
}