package cbit.vcell.model;
import cbit.vcell.parser.ExpressionException;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;

import org.vcell.util.Matchable;
import org.vcell.util.document.KeyValue;


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
 * Get surface volume ratio expression for this feature.
 * Creation date: (12/8/2006 1:28:11 PM)
 */
public cbit.vcell.parser.Expression getSurfVolRatio() throws ExpressionException
{
	String expStr = "";
	Structure parentStructure = getParentStructure();
	if(parentStructure instanceof Membrane)
	{
		expStr = "Size_"+ parentStructure.getNameScope().getName()+"/"+"Size_"+ this.getNameScope().getName();
		try
		{
			cbit.vcell.parser.Expression surfVolRatio = new cbit.vcell.parser.Expression(expStr);
			return surfVolRatio;
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new ExpressionException("Failed to parse surface volume ratio for feature "+this.getName()+".");
		}
	}
	else return null;
}


/**
 * get volume fraction expression for this feature.
 * Creation date: (12/8/2006 1:32:16 PM)
 */
public cbit.vcell.parser.Expression getVolFrac() throws ExpressionException
{
	String expStr1 = ""; //Numerator
	String expStr2 = ""; //Denominator
	// get this feature's sub features.
	Enumeration selfAndChildren = this.getSubFeatures();
	// get this feature's sub features and it's parent feature and sibling features(including their sub features)
	Structure parentStructure = getParentStructure();// the membrane
	Structure parentFeature = null;
	Enumeration parentSiblingSelfAndChildren = null;
	if (parentStructure == null) return null; // the feature is the out most feature. Volumn fraction is not needed.
	if((parentStructure != null) && (parentStructure instanceof Membrane))
	{
		parentFeature = ((Membrane)parentStructure).getOutsideFeature();
	}
	if(parentFeature != null)
	{
		parentSiblingSelfAndChildren = parentFeature.getSubFeatures();
	}
	else
	{
		parentSiblingSelfAndChildren = selfAndChildren;
	}
	// accumulate feature itself and it's sub features together	
	while(selfAndChildren.hasMoreElements())
	{
		Feature feature = ((Feature)selfAndChildren.nextElement());
		expStr1 = expStr1 + "Size_" + feature.getNameScope().getName() + "+";
	}
	if(expStr1.length() > 0)
		expStr1 = expStr1.substring(0, (expStr1.length()-1));
	// accumulate feature's parent feature, sibling features(& their sub features), itself and it's sub features
	while(parentSiblingSelfAndChildren.hasMoreElements())
	{
		Feature feature = ((Feature)parentSiblingSelfAndChildren.nextElement());
		expStr2 = expStr2 + "Size_" + feature.getNameScope().getName() + "+";
	}
	if (expStr2.length() > 0)
		expStr2 = expStr2.substring(0,(expStr2.length()-1));
	try
	{
		cbit.vcell.parser.Expression volFrac = new cbit.vcell.parser.Expression("("+expStr1+")/("+expStr2+")");
		return volFrac;
	}catch(Exception e)
	{
		e.printStackTrace();
		throw new ExpressionException("Failed to volume fraction for feature "+this.getName()+".");
	}
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



@Override
public int getDimension() {
	return 3;
}
}