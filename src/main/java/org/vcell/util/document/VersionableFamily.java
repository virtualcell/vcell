/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.document;

import java.util.Vector;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class VersionableFamily extends java.lang.Object implements java.io.Serializable {
	private Vector familyMembers = new Vector();	// Unique members of this family, all other objects reference familyMembers.  
													// Maintained so object references are same (==)
													
	private Vector dependantRels = new Vector(); 	// Things that depend on target(Dependants)
	private Vector childRels = new Vector(); 		// Things that target depends on(Children)
/**
 * VersionableFamily constructor comment.
 * @param dbc cbit.sql.DBCacheTable
 * @param sessionLog cbit.vcell.server.SessionLog
 */
public VersionableFamily(VersionableTypeVersion argTarget) {
	super();
	// First family member(target) is the object from which relationships extend
	familyMembers.addElement(argTarget);
}
/**
 * This method was created in VisualAge.
 * @param vr cbit.vcell.modeldb.VersionableRelationship
 */
public void addChildRelationship(VersionableRelationship vr) {
	//
	// See if this is a new child relationship we dont have
	//
	if(checkVRExists(vr,childRels)){
		return;
	}
/*
	for (int c = 0; c < childRels.size(); c += 1) {
		VersionableRelationship dvr = (VersionableRelationship) childRels.elementAt(c);
		if (dvr.bSame(vr)) {
			return;
		}
	}
*/
	//
	// Make sure childRels elements(from,to) come from the same (==) familyMembers,
	// not different objects that have the same content value
	// This is required for other VCell code
	//
	VersionableTypeVersion from = getFamilyMember(vr.from());
	VersionableTypeVersion to = getFamilyMember(vr.to());
/*
	for (int c = 0; c < familyMembers.size(); c += 1) {
		VersionableTypeVersion fm = (VersionableTypeVersion) familyMembers.elementAt(c);
		//
		// Make sure that the final childRels vector has only references to things in familyMembers
		//
		if (fm.equals(vr.from())) {// Check by reference (==) -or- value
			if (from != null) {
				throw new RuntimeException("Same from value appears in familyMembers more than once");
			}
			from = fm; // Make sure we end up with only references to familyMembers
		}
		//
		if (fm.equals(vr.to())) {// Check reference (==) -or- by value
			if (to != null) {
				throw new RuntimeException("Same to value appears in familyMembers more than once");
			}
			to = fm; // Make sure we end up with only references to familyMembers
		}
	}
*/
	//
	// Add anything to familyMembers that isn't already there
	//
	if (from == null) {
		from = vr.from();
		familyMembers.addElement(from);
	}
	if (to == null) {
		to = vr.to();
		familyMembers.addElement(to);
	}
	//
	childRels.addElement(new VersionableRelationship(from, to));
}
/**
 * This method was created in VisualAge.
 * @param vr cbit.vcell.modeldb.VersionableRelationship
 */
public void addDependantRelationship(VersionableRelationship vr) {
	//
	// See if this is a new dependant relationship we dont have
	//
	if(checkVRExists(vr,dependantRels)){
		return;
	}
/*
	for (int c = 0; c < dependantRels.size(); c += 1) {
		VersionableRelationship dvr = (VersionableRelationship) dependantRels.elementAt(c);
		if (dvr.bSame(vr)) {
			return;
		}
	}
*/
	//
	// Make sure dependantRels elements(from,to) come from the same (==) familyMembers, not different objects that have the same content value
	// This is required for other VCell code
	//
	VersionableTypeVersion from = getFamilyMember(vr.from());
	VersionableTypeVersion to = getFamilyMember(vr.to());
/*
	for (int c = 0; c < familyMembers.size(); c += 1) {
		VersionableTypeVersion fm = (VersionableTypeVersion) familyMembers.elementAt(c);
		//
		// Make sure that the final dependantRels vector has only references to things in familyMembers
		//
		if (fm.equals(vr.from())) {// Check by reference (==) -or- value
			if (from != null) {
				throw new RuntimeException("Same from value appears in familyMembers more than once");
			}
			from = fm; // Make sure we end up with only references to familyMembers
		}
		//
		if (fm.equals(vr.to())) {// Check reference (==) -or- by value
			if (to != null) {
				throw new RuntimeException("Same to value appears in familyMembers more than once");
			}
			to = fm; // Make sure we end up with only references to familyMembers
		}
	}
*/
	//
	// Add anything to familyMembers that isn't already there
	//
	if (from == null) {
		from = vr.from();
		familyMembers.addElement(from);
	}
	if (to == null) {
		to = vr.to();
		familyMembers.addElement(to);
	}
	//
	dependantRels.addElement(new VersionableRelationship(from, to));
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean bChildren() {
	return (childRels.size() != 0);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean bDependants() {
	return (dependantRels.size() != 0);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param vr cbit.vcell.modeldb.VersionableRelationship
 * @param vrList java.util.Vector
 */
private boolean checkVRExists(VersionableRelationship vr, Vector vrList) {
	for (int c = 0; c < vrList.size(); c += 1) {
		VersionableRelationship dvr = (VersionableRelationship) vrList.elementAt(c);
		if (dvr.bSame(vr)) {
			return true;
		}
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public VersionableRelationship[] getDependantRelationships() {
	VersionableRelationship[] vrArr = new VersionableRelationship[dependantRels.size()];
	dependantRels.copyInto(vrArr);
	return vrArr;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableTypeVersion[]
 */
public VersionableTypeVersion[] getDependantsTopDown() {
	Vector topDown = new Vector();
	boolean allDone;
	VersionableTypeVersion[] uniqueDependants = getUniqueDependants();
	if (uniqueDependants.length > 0) {
		do {
			allDone = true;
			for (int c = 0; c < uniqueDependants.length; c += 1) {
				VersionableTypeVersion checkVTV = (VersionableTypeVersion) uniqueDependants[c];
				if (topDown.contains(checkVTV)) {
					continue;
				}
				allDone = false;
				boolean bHasDependant = false;
				for (int i = 0; i < dependantRels.size(); i += 1) {
					VersionableRelationship vr = (VersionableRelationship) dependantRels.elementAt(i);
					if (topDown.contains(vr.from())) {
						continue;
					}
					if (vr.to() == checkVTV) {
						bHasDependant = true;
						break;
					}
				}
				if (!bHasDependant) {
					topDown.addElement(checkVTV);
					break;
				}
			}
		} while (allDone == false);
	}
	VersionableTypeVersion[] topDownArr = new VersionableTypeVersion[topDown.size()];
	topDown.copyInto(topDownArr);
	return topDownArr;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableTypeVersion
 * @param vtv cbit.vcell.modeldb.VersionableTypeVersion
 */
private VersionableTypeVersion getFamilyMember(VersionableTypeVersion vtv) {
	//
	// Assume VersionableTypeVersion vtv does not exist in familyMembers to begin
	//
	VersionableTypeVersion vtvFound = null;
	//
	// Step through all familyMembers
	//
	for (int c = 0; c < familyMembers.size(); c += 1) {
		// Get Next familyMember
		VersionableTypeVersion fm = (VersionableTypeVersion) familyMembers.elementAt(c);
		//
		// See if VersionableTypeVersion vtv is in familyMembers
		// Check by reference (==) -or- value
		if (fm.equals(vtv)) {
			if (vtvFound != null) {// Already found one before, this is an error
				throw new RuntimeException("Same from value appears in familyMembers more than once");
			}
			// Make sure we end up with only references to familyMembers
			// Even though we found one, don't break yet.
			vtvFound = fm; 
		}
	// Keep searching familyMembers to make sure there is 
	// not more than 1 vtv in familyMembers(Runtime Consistency Check).
	}
	// May return null if no VersionableTypeVersion vtv found in familyMembers. (that's OK)
	return vtvFound;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableTypeVersion
 */
public VersionableTypeVersion getTarget() {
	return (VersionableTypeVersion)familyMembers.elementAt(0);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableTypeVersion[]
 */
public VersionableTypeVersion[] getUniqueChildren() {
	if(!bChildren()){
		throw new RuntimeException("No children found");
	}
	Vector ud = new Vector();
	// Get "from" and "to" from all child realtionships and put them in a list
	// making sure each VersionableRelationship appears only once
	// We do not just return "familyMembers" because it may have dependants as well as children
	for(int c = 0;c < childRels.size();c+= 1){
		VersionableRelationship verrel = (VersionableRelationship)childRels.elementAt(c);
		if(!ud.contains(verrel.from())){
			ud.addElement(verrel.from());
		}
		if(!ud.contains(verrel.to())){
			ud.addElement(verrel.to());
		}
	}
	// Create and return typed array
	VersionableTypeVersion[] vtvArr = new VersionableTypeVersion[ud.size()];
	ud.copyInto(vtvArr);
	return vtvArr;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableTypeVersion[]
 */
public VersionableTypeVersion[] getUniqueDependants() {
	if(!bDependants()){
		throw new RuntimeException("No dependants found");
	}
	Vector ud = new Vector();
	// Get "from" and "to" from all dependent realtionships and put them in a list
	// making sure each VersionableRelationship appears only once
	// We do not just return "familyMembers" because it may have children as well as dependants
	for(int c = 0;c < dependantRels.size();c+= 1){
		VersionableRelationship verrel = (VersionableRelationship)dependantRels.elementAt(c);
		if(!ud.contains(verrel.from())){
			ud.addElement(verrel.from());
		}
		if(!ud.contains(verrel.to())){
			ud.addElement(verrel.to());
		}
	}
	// Create and return typed array
	VersionableTypeVersion[] vtvArr = new VersionableTypeVersion[ud.size()];
	ud.copyInto(vtvArr);
	return vtvArr;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.modeldb.VersionableTypeVersion[]
 */
public VersionableTypeVersion[] getUniqueDependantsByType(org.vcell.util.document.VersionableType vType) {
	Vector ud = new Vector();
	// Get "from" and "to" from all dependent realtionships and put them in a list
	// making sure each VersionableRelationship appears only once
	// We do not just return "familyMembers" because it may have children as well as dependants
	for(int c = 0;c < dependantRels.size();c+= 1){
		VersionableRelationship verrel = (VersionableRelationship)dependantRels.elementAt(c);
		if(!ud.contains(verrel.from()) && vType.equals(verrel.from().getVType())){
			ud.addElement(verrel.from());
		}
	}
	// Create and return typed array
	VersionableTypeVersion[] vtvArr = new VersionableTypeVersion[ud.size()];
	ud.copyInto(vtvArr);
	return vtvArr;
}
}
