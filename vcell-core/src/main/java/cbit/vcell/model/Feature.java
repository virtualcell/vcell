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

import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;


public class Feature extends Structure
{
public Feature(KeyValue key, String name) throws java.beans.PropertyVetoException {
	super(key);
	setName0(name);
}


public Feature(String name) throws java.beans.PropertyVetoException {
	super(null);
	setName0(name);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(Matchable obj) {
	Feature f = null;
	if (!(obj instanceof Feature)){
		return false;
	}
	f = (Feature)obj;

	if (!compareEqual0(f)){
		return false;
	}
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2006 5:16:07 PM)
 */
/*public boolean isInnerMostFeature()
{
	Structure[] structures = this.getModel().getStructures();
	for(int i=0; i<structures.length; i++)
	{
		if(structures[i] instanceof Membrane)
		{
			if(((Membrane)structures[i]).getOutsideFeature().compareEqual(this))
				return false;
		}
	}
	return true;
}*/


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append("Feature@"+Integer.toHexString(hashCode())+"(name="+getName());
	
	sb.append(")");

	return sb.toString();
}



@Override
public int getDimension() {
	return 3;
}


@Override
public String getTypeName() {
	return TYPE_NAME_FEATURE;
}


@Override
public void setName(String name,boolean bFromGUI) throws PropertyVetoException{
	setName0(name);
	if(bFromGUI){
		getStructureSize().setName(Structure.getDefaultStructureSizeName(name));
	}
}

/* 
public String checkNewParent(Structure structure) {
	if (structure instanceof Membrane){
		Membrane membrane = (Membrane)structure;
		//
		// check for cyclic parenthood
		//
		Structure s = membrane.getParentStructure();
		while (s!=null){
			if (s == this){
				return "cannot make parent relationship cyclic";
			}
			s = s.getParentStructure();
		}
	} else {
		return "parent structure must be a Membrane";
	}
	return null;
}

 */
}
