/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;
import org.vcell.util.Displayable;
import org.vcell.util.Matchable;
import org.vcell.util.document.Identifiable;

public interface BioPaxObject extends Identifiable, Matchable, Displayable {

	public void setID(String id);
	public String getID();
	public String getIDShort();
	boolean hasID();
	
	public ArrayList<String> getParserWarnings();
	public void addParserWarning(String comment);

	public ArrayList<String> getComments();
	public void setComments(ArrayList<String> comment);
	
	void show(StringBuffer stringBuffer);
	boolean fullCompare(HashSet<BioPaxObject> theirBiopaxObjects);
	boolean compareEqual(Matchable obj);
	void showChildren(StringBuffer stringBuffer, int level);
	
	public void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject);
	public void replace(BioPaxObject keeperObject);
	public void replace(HashMap<String, BioPaxObject> resourceMap, HashSet<BioPaxObject> replacedBPObjects);
	
	String getTypeLabel();

}
