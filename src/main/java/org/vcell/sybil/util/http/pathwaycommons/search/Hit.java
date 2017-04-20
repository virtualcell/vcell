/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.pathwaycommons.search;

/*   Hit  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Hit from a web request using command search from Pathway Commons
 */

import java.util.List;
import java.util.Vector;

import org.sbpax.util.StringUtil;
import org.vcell.sybil.util.xml.DOMUtil;
import org.w3c.dom.Element;

public class Hit {
	
	protected String primaryID = "", entityType = "";
	protected List<String> names, descriptions, synonyms, excerpts;
	protected DataSource dataSource;
	protected List<XRef> xRefs;
	protected Organism organism;
	protected List<Pathway> pathways;
	
	public Hit(Element element) {
		primaryID = element.getAttribute("primary_id");
		names = DOMUtil.childContents(element, "name");
		descriptions = DOMUtil.childContents(element, "description");
		entityType = DOMUtil.firstChildContent(element, "entity_type");
		Element dataSourceElement = DOMUtil.firstChildElement(element, "data_source");
		if(dataSourceElement != null) { dataSource = new DataSource(dataSourceElement); }
		synonyms = DOMUtil.childContents(element, "synonym");
		List<Element> xRefElements = DOMUtil.childElements(element, "xref");
		xRefs = new Vector<XRef>();
		for(Element xRefElement : xRefElements) { xRefs.add(new XRef(xRefElement)); }
		excerpts = DOMUtil.childContents(element, "excerpt");
		excerpts = cleanUp(excerpts); // wei's code: Clean up the <B> and <b> letters in the text of the response tree
		Element organismElement = DOMUtil.firstChildElement(element, "organism");
		if(organismElement != null) { organism = new Organism(organismElement); }
		Element pathwayListElement = DOMUtil.firstChildElement(element, "pathway_list");
		pathways = new Vector<Pathway>();
		if(pathwayListElement != null) {
			List<Element> pathwayElements = DOMUtil.childElements(pathwayListElement, "pathway");
			for(Element pathwayElement : pathwayElements) { pathways.add(new Pathway(pathwayElement)); }
		}
	}
	
	public String primaryID() { return primaryID; }
	public List<String> names() { return names; }
	public List<String> descriptions() { return descriptions; }
	public String entityType() { return entityType; }
	public DataSource dataSource() { return dataSource; }
	public List<String> synonyms() { return synonyms; }
	public List<XRef> xRefs() { return xRefs; }
	public List<String> excerpts() { return excerpts; }
	public Organism organism() { return organism; }
	public List<Pathway> pathways() { return pathways; }
	
	@Override
	public String toString() {
		return "[Hit: \n" + 
		"primaryID=\"" + primaryID + "\";\n" + 
		"names=(" + StringUtil.concat(names, ", ", "\"", "\"") + ");\n" + 
		"descriptions=(" + StringUtil.concat(descriptions, ", ", "\"", "\"") + ");\n" + 
		"synonyms=(" + StringUtil.concat(synonyms, ",\n", "\"", "\"") + ");\n" + 
		"entityType=\"" + entityType + "\";\n" +
		"xRefs=(" + StringUtil.concat(xRefs, ";\n") + ");\n" +
		"organism=" + (organism != null ? organism.toString() : "null") + ";\n" +
		"pathways=(" + StringUtil.concat(pathways, ",\n", "\"", "\"") + ");\n" + 
		"]";
	}
	// wei's code: remove the <b></b> and <B></B> from excerpts 
	private List<String> cleanUp(List<String> excerpts){
		String str = new String();	
		for (int i = 0; i < excerpts.size(); i++){
			str = excerpts.get(i);
			String res = str.replaceAll("<B><b>", "");
			String res1 = res.replaceAll("</b></B>", "");
			excerpts.set(i, res1);
		}
		return excerpts;
	}
	// done

}
