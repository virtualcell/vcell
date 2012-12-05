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
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class SBOUtil {

	public static SBOTerm createTerm(
			String index, 
			String symbol, 
			String name, 
			String description, 
			String isA)
//			Map<String, SBOTerm> map)
	{
		SBOTerm sboTerm = SBOUtil.createSBOTermFromIndex(index, symbol, name, description, isA);
		SBOListEx.sboMap.put(index, sboTerm);
		return sboTerm;
	}

	public static SBOTerm createSBOTermFromIndex(String index, String symbol, String name, String description, String isA) {
		SBOTerm sboTerm = new SBOTerm(index);
		sboTerm.setSymbol(symbol);
		sboTerm.setName(name);
		sboTerm.setDescription(description);
		sboTerm.setIsA(isA);
		return sboTerm;
	}
	
	public static Set<SBOTerm> getSubClasses(SBOTerm term) {
		Set<SBOTerm> subClasses = new HashSet<SBOTerm>();

		Iterator<String> iterator = SBOListEx.sboMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			SBOTerm candidate = SBOListEx.sboMap.get(key);
			String candidateIsa = candidate.getIsA();
			String termIndex = term.getIndex();
//			System.out.println("is " + termIndex + " a parent of " + key + " ( " + candidateIsa + " ) ?" );
			if(candidateIsa.contains(termIndex)) {
				subClasses.add(candidate);
			}
		}
        return subClasses;
	}
	public static Set<SBOTerm> getSuperClasses(SBOTerm term) {
		Set<SBOTerm> superClasses = new HashSet<SBOTerm>();

		String isA = term.getIsA();
		if(isA.contains(",")) {	// 2 super classes, comma separated
			String id1 = isA.substring(0,isA.indexOf(","));
			String id2 = isA.substring(isA.indexOf(",")+1);
			superClasses.add(SBOListEx.sboMap.get(id1));
			superClasses.add(SBOListEx.sboMap.get(id2));
//			System.out.println(id1 + " ,   " + id2);
		} else {
			superClasses.add(SBOListEx.sboMap.get(isA));
		}
		return superClasses;
	}
	
	@Deprecated
	public static String getIndexFromId(String id) {
		if(id.length()<7) {
			id = "0000000" + id; 
		}
		return id.substring(id.length() - 7);
	}
	
	/*
	 * retrieves an entry from SBOListEx by recovering the key using the SBVocabulary id 
	 * format: http://sabio.h-its.org/biopax#SBO:0000xyz 
	 * the recovered key being  SBO:0000xyz  
	 */
	public static SBOTerm getSBOTermFromVocabularyId(String id) {
		String key = id.substring(id.lastIndexOf('#')+1);
		SBOTerm sboT = SBOListEx.sboMap.get(key);
		return sboT;
	}
	
	@Deprecated
	public static SBOTerm createSBOTermFromId(String id) {
		return new SBOTerm(getIndexFromURI(id));
	}
	
	@Deprecated
	public static String getIndexFromURI(String uri) {
		return uri.substring(uri.length() - 7);
	}
	
	@Deprecated
	public static SBOTerm createSBOTermFromURI(String uri) {
		return new SBOTerm(getIndexFromURI(uri));
	}
	
	@Deprecated
	public static void setChild(SBOTerm parent, SBOTerm child) {
		parent.getSubClasses().add(child);
		child.getSuperClasses().add(parent);
	}
	
	@Deprecated
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
