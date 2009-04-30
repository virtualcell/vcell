package cbit.vcell.model;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.util.*;
/**
 * This type was created in VisualAge.
 */
public class NodeReference implements java.io.Serializable, Matchable {
	public int nodeType = UNKNOWN_NODE;
	public String name = null;
	public java.awt.Point location = null;

	public static final int UNKNOWN_NODE = 0;
	public static final int SIMPLE_REACTION_NODE = 1;
	public static final int FLUX_REACTION_NODE = 2;
	public static final int SPECIES_CONTEXT_NODE = 3;

	public static final String nodeNames[] = { "unknown", VCMODL.SimpleReaction, VCMODL.FluxStep, VCMODL.SpeciesContextSpec };
/**
 * NodeReference constructor comment.
 */
public NodeReference(int nodeType, String name, java.awt.Point location) {
	this.nodeType = nodeType;
	this.name = name;
	this.location = new java.awt.Point(location);
}
/**
 * This method was created in VisualAge.
 * @param nodeTypeString java.lang.String
 * @param name java.lang.String
 * @param location java.awt.Point
 */
public NodeReference(String nodeTypeString, String name, java.awt.Point location) throws IllegalArgumentException {
	if (nodeNames[SIMPLE_REACTION_NODE].equalsIgnoreCase(nodeTypeString)){
		this.nodeType = SIMPLE_REACTION_NODE;
	}else if (nodeNames[FLUX_REACTION_NODE].equalsIgnoreCase(nodeTypeString)){
		this.nodeType = FLUX_REACTION_NODE;
	}else if (nodeNames[SPECIES_CONTEXT_NODE].equalsIgnoreCase(nodeTypeString)){
		this.nodeType = SPECIES_CONTEXT_NODE;
	}else{
		throw new IllegalArgumentException("nodeType '"+nodeTypeString+"' unknown");
	}
	this.name = name;
	this.location = new java.awt.Point(location);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj cbit.util.Matchable
 */
public boolean compareEqual(Matchable obj) {
	if (obj == this){
		return true;
	}
	if (!(obj instanceof NodeReference)){
		return false;
	}
	
	NodeReference nr = (NodeReference)obj;

	if (!Compare.isEqual(getName(),nr.getName())){
		return false;
	}
	
	if (!Compare.isEqual(getTypeString(),nr.getTypeString())){
		return false;
	}
	
	if (!location.equals(nr.location)){   // java.awt.point implements .equals() properly
		return false;
	}
	
	return true;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getName() {
	return name;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getTypeString() {
	return nodeNames[nodeType];
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
void setName(String newName) {
	name = newName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public void write(java.io.PrintWriter pw) {
	pw.println(getTypeString()+" \""+getName()+"\" "+location.x+" "+location.y);
}
}
