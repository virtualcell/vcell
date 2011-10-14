/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas.util;

/*   NSMap  --- by Oliver Ruebenacker, UCHC --- May 2009
 *   Organize mappings between prefixes and URIs
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class NSMap {

	protected Map<String, NameSpace> uriToNS = 
		new HashMap<String, NameSpace>();
	protected Map<String, NameSpace> prefixToNS = 
		new HashMap<String, NameSpace>();
	protected Set<NameSpace> all = new HashSet<NameSpace>();
	
	protected int countForNew;
	
	public NSMap() {};
	
	public NSMap(NSMap source) {
		for(NameSpace ns : source.all) { register(ns); }
	}
	
	public NameSpace register(String prefix, String uri) {
		return register(new NameSpace(prefix, uri));
	}
	
	public NameSpace register(NameSpace nsNew) {
		uriToNS.put(nsNew.uri, nsNew);
		prefixToNS.put(nsNew.prefix, nsNew);
		all.add(nsNew);
		return nsNew;
	}
	
	public boolean hasPrefix(String prefix) { 
		return prefixToNS.containsKey(prefix); 
	}
	
	public boolean hasURI(String uri) { 
		return uriToNS.containsKey(uri); 
	}
	
	public void unregister(NameSpace ns) {
		uriToNS.remove(ns.uri);
		prefixToNS.remove(ns.prefix);
		all.remove(ns);		
	}
	
	public NameSpace prefixToNS(String prefix) { 
		return prefixToNS.get(prefix); 
	}
	
	public NameSpace uriToNS(String uri) { 
		return uriToNS.get(uri); 
	}
	
	public Set<NameSpace> nameSpaces() { return all; }
	
	public NameSpace provideNamesSpace(String uri) {
		NameSpace ns = uriToNS.get(uri);
		if(ns == null) {
			String prefix;
			while(hasPrefix(prefix = "n" + countForNew)) { ++ countForNew; }
			ns = register(prefix, uri);
		}
		return ns;
	}
	
	public void addFromMap(Map<String, String> map) {
		for(Map.Entry<String, String> entry : map.entrySet()) {
			register(entry.getKey(), entry.getValue());
		}
	}
	
	public Map<String, String> convertToMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		for(NameSpace ns : all) {
			map.put(ns.prefix, ns.uri);
		}
		return map;
	}
	
}
