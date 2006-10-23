package cbit.vcell.model;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;

import cbit.util.Matchable;
import cbit.util.document.KeyValue;

public class Feature extends Structure
{
	public Membrane membrane = null;

public Feature(KeyValue key, String name) throws java.beans.PropertyVetoException {
	super(key);
	setName(name);
}


public Feature(String name) throws java.beans.PropertyVetoException {
	super(null);
	setName(name);
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
	if ((getMembrane()!=null && f.getMembrane()==null) ||
		(getMembrane()==null && f.getMembrane()!=null)){
		return false;
	}
	if (getMembrane()!=null && !getMembrane().getName().equals(f.getMembrane().getName())){
		return false;
	}
	return true;
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
	if (getMembrane() != null){
		return getMembrane().enclosedBy(parentStructure);
	}	
	return false;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.Feature
 */
public Membrane getMembrane() {
	return membrane;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Structure
 */
public Structure getParentStructure() {
	return getMembrane();
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getPriority() {
	int priority = 1;
	Feature feature = this;
	while (feature.membrane!=null){
		feature = feature.membrane.getOutsideFeature();
		priority++;
	}	
	return priority;
}


/**
 * This method was created by a SmartGuide.
 * @param feature cbit.vcell.model.Feature
 */
public void setMembrane(Membrane membrane) {
	this.membrane = membrane;
	if (membrane != null && membrane.getInsideFeature()!=this){
		membrane.setInsideFeature(this);
	}
}


/**
 * This method was created in VisualAge.
 * @param structure cbit.vcell.model.Structure
 */
public void setParentStructure(Structure structure) throws ModelException {
	if (structure instanceof Membrane){
		Membrane membrane = (Membrane)structure;
		//
		// check for cyclic parenthood
		//
		Structure s = membrane.getParentStructure();
		while (s!=null){
			if (s == this){
				throw new ModelException("cannot make parent relationship cyclic");
			}
			s = s.getParentStructure();
		}
		setMembrane(membrane);
	}else{
		throw new ModelException("parent structure must be a Membrane");
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append("Feature@"+Integer.toHexString(hashCode())+"(name="+getName());
	
	if (membrane != null){ 
		sb.append(", membrane='"+membrane.getName()+"'");
	}
	sb.append(")");

	return sb.toString();
}


/**
 * This method was created by a SmartGuide.
 * @param ps java.io.PrintStream
 * @exception java.lang.Exception The exception description.
 */
public void writeTokens(java.io.PrintWriter pw, Model model) {
	
	//
	// write Feature description
	//
	pw.println(VCMODL.Feature+" "+getName()+" "+VCMODL.BeginBlock);

	SpeciesContext structSC[] = model.getSpeciesContexts(this);
	for (int i=0;i<structSC.length;i++){
		structSC[i].writeTokens(pw);	
	}

	pw.println(VCMODL.EndBlock);
	
}
}