/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.sbo;

import java.util.HashSet;
import java.util.Set;

public class SBOUtil {

	public static SBOTerm createSBOTermFromIndex(String index, String symbol, 
			String name, String description) {
		SBOTerm sboTerm = createSBOTermFromIndex(index);
		sboTerm.setSymbol(symbol);
		sboTerm.setName(name);
		sboTerm.setDescription(description);
		return sboTerm;
	}
	
	public static SBOTerm createSBOTermFromIndex(String index) { 
		return new SBOTerm(index); 
	}
	
	public static String getIndexFromId(String id) {
		if(id.length()<7) {
			id = "0000000" + id; 
		}
		return id.substring(id.length() - 7);
	}
	
	public static SBOTerm createSBOTermFromId(String id) {
		return new SBOTerm(getIndexFromURI(id));
	}
	
	public static String getIndexFromURI(String uri) {
		return uri.substring(uri.length() - 7);
	}
	
	public static SBOTerm createSBOTermFromURI(String uri) {
		return new SBOTerm(getIndexFromURI(uri));
	}
	
	public static void setChild(SBOTerm parent, SBOTerm child) {
		parent.getSubClasses().add(child);
		child.getSuperClasses().add(parent);
	}
	
	public static Set<SBOTerm> getAllDescendents(SBOTerm term) {
		Set<SBOTerm> descendents = new HashSet<SBOTerm>();
		Set<SBOTerm> descendentsNew = new HashSet<SBOTerm>();			
		descendentsNew.add(term);
		do {
			Set<SBOTerm> descendentsNewNew = new HashSet<SBOTerm>();
			for(SBOTerm descendent : descendentsNew) {
				descendentsNewNew.addAll(descendent.getSubClasses());
			}
			descendents.addAll(descendentsNew);
			descendentsNew = descendentsNewNew;
		} while(!descendentsNew.isEmpty());
		return descendents;
	}
	
}
