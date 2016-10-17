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

import java.beans.PropertyVetoException;

import org.vcell.util.Issue.IssueSource;
import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;

import cbit.vcell.units.VCUnitDefinition;

public class Membrane extends Structure implements IssueSource {
	private MembraneVoltage fieldMembraneVoltage = null;

	public final static String MEMBRANE_VOLTAGE_REGION_NAME = "SpatialMembraneVoltage";	
	
	public class MembraneVoltage extends ModelQuantity {

		@Override
		public String getDescription() {
			return "voltage across membrane "+Membrane.this.getName();
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
			return getModel().getUnitSystem().getVoltageUnit();
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
	setName0(name);
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
		if (!getMembraneVoltage().compareEqual(m.getMembraneVoltage())){
			return false;
		}
		return true;
	}else{
		return false;
	}
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
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append("Membrane@"+Integer.toHexString(hashCode())+"(name='"+getName());
	
	sb.append(")");
	
	return sb.toString();
}

@Override
public int getDimension() {
	return 2;
}


@Override
public String getTypeName() {
	return TYPE_NAME_MEMBRANE;
}


@Override
public void setName(String name,boolean bFromGUI) throws PropertyVetoException {
	setName0(name);
	if(bFromGUI){
		getStructureSize().setName(Structure.getDefaultStructureSizeName(name));
		getMembraneVoltage().setName(Membrane.getDefaultMembraneVoltageName(name));
	}
}

/*
public String checkNewParent(Structure structure) {
	if (structure instanceof Feature){
		Feature feature = (Feature)structure;
		//
		// check for cyclic parenthood
		//
		Structure s = feature.getParentStructure();
		while (s!=null){
			if (s == this){
				return "cannot make parent relationship cyclic";
			}
			s = s.getParentStructure();
		}
	} else{
		return "parent structure must be a Feature";
	}
	return null;
}
 */

}
