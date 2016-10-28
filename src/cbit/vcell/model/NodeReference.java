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

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
/**
 * This type was created in VisualAge.
 */
public class NodeReference implements java.io.Serializable, Matchable {
	public int nodeType = UNKNOWN_NODE;
	public String name = null;
	public java.awt.Point location = null;
	
	public transient SpeciesPattern speciesPattern = null;

	public static final int UNKNOWN_NODE = 0;
	public static final int SIMPLE_REACTION_NODE = 1;
	public static final int FLUX_REACTION_NODE = 2;
	public static final int SPECIES_CONTEXT_NODE = 3;
	public static final int REACTION_RULE_NODE = 4;
	public static final int RULE_PARTICIPANT_SIGNATURE_NODE = 5;

	public static final String nodeNames[] = { "unknown", VCMODL.SimpleReaction, VCMODL.FluxStep, VCMODL.SpeciesContextSpec, VCMODL.ReactionRule, VCMODL.RuleParticipantSignature };
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
	}else if (nodeNames[REACTION_RULE_NODE].equalsIgnoreCase(nodeTypeString)){
		this.nodeType = REACTION_RULE_NODE;
	}else if (nodeNames[RULE_PARTICIPANT_SIGNATURE_NODE].equalsIgnoreCase(nodeTypeString)){
		this.nodeType = RULE_PARTICIPANT_SIGNATURE_NODE;
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
	if(RULE_PARTICIPANT_SIGNATURE_NODE != nodeType) {
		return name;
	} else {
		if (speciesPattern != null) {
			// Maintain consistency between rule participant nodes, signatures and 
			// species pattern when a molecule is being modified.
			name = RuleParticipantSignature.getSpeciesPatternAsString(speciesPattern);
			return name;
		} else {
			return name;
		}
	}
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
