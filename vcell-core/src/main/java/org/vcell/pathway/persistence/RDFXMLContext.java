/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.persistence;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.util.StringUtil;
import org.vcell.pathway.id.URIUtil;

public class RDFXMLContext {
	
	protected final String defaultBaseURI;
	
	protected static final String DEFAULT_BASE_URI = "http://vcell.org/biopax/";
	private static final Namespace rdf = Namespace.getNamespace("rdf", DefaultNameSpaces.RDF.uri);

	public static final int CACHE_MAX_SIZE = 100;
	
	protected Map<Element, String> baseURIsCache1 = new HashMap<Element, String>();
	protected Map<Element, String> baseURIsCache2 = new HashMap<Element, String>();
	
	public RDFXMLContext() { this(DEFAULT_BASE_URI); }
	public RDFXMLContext(String baseURI) { this.defaultBaseURI = baseURI; }
	
	public String getDefaultBaseURI() { return defaultBaseURI; }
	
	protected String getBaseURIFromCache(Element element) {
		String baseURI = baseURIsCache1.get(element);
		if(baseURI == null) { baseURI = baseURIsCache2.get(element); }
		return baseURI;
	}
	
	protected void addBaseURIToCache(Element element, String baseURI) {
		baseURIsCache1.put(element, baseURI);
		if(baseURIsCache1.size()*2 >= CACHE_MAX_SIZE - 1) {
			baseURIsCache2 = baseURIsCache1;
			baseURIsCache1 = new HashMap<Element,String>();
		}
	}
	
	public String getBaseURI(Element element) {
		String baseURI = getBaseURIFromCache(element);
		if(StringUtil.isEmpty(baseURI)) {
			baseURI = element.getAttributeValue("base", Namespace.XML_NAMESPACE);
			if(StringUtil.isEmpty(baseURI)) {
				Parent parent = element.getParent();
				// This change from previous version needed to be made since JDOM 1.1.3 api is slightly different from JDOM 1.0.
				// in JDOM 1.1.3, element.getParent() returns Parent not Element (as in JDOM 1.0). Parent interface is implemented by Document and Element.
				// If element.getParent() returns an element, recursively call this method. 
				// At the root level, element.getParent() returns Document, which is non-null, assign defaultBaseURI to 'baseURI'.
				if(parent != null) {
					if (parent instanceof Element) {
						baseURI = getBaseURI((Element)parent);
					} else {
						baseURI = defaultBaseURI;
					}
				} else {
					baseURI = defaultBaseURI;
				}
			}
			addBaseURIToCache(element, baseURI);
		}
		return baseURI;
	}
	
	public String relativizeURI(Element element, String uri) {
		return URIUtil.relativizeURI(uri, getBaseURI(element));
	}
	
	public String unRelativizeURI(Element element, String uri) {
		return URIUtil.unRelativizeURI(uri, getBaseURI(element));
	}
	
	public String abbreviateURI(Element element, String uri) {		
		return URIUtil.abbreviateURI(uri, getBaseURI(element));
	}
	
	public String unAbbreviateURI(Element element, String uri) {
		return URIUtil.unAbbreviateURI(uri, getBaseURI(element));
	}
	
	public void addIDToObject(Element element, String id) {
		if(URIUtil.isAbsoluteURI(id)) {
			element.setAttribute("about", id, rdf);				
		} else {
			element.setAttribute("nodeID", id, rdf);			
		}
	}
	
	public void addIDToProperty(Element element, String id) {
		if(URIUtil.isAbsoluteURI(id)) {
			element.setAttribute("resource", relativizeURI(element, id), rdf);
		} else {
			element.setAttribute("nodeID", id, rdf);
		}
	}
	
}