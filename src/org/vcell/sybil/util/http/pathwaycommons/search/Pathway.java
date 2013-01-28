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

/*   Pathway  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Pathway from a web request using command search from Pathway Commons
 */

import org.vcell.sybil.util.xml.DOMUtil;
import org.w3c.dom.Element;

public final class Pathway implements Comparable { 

	protected String primaryId = "";
	protected String name = "";
	protected Organism organism;
	protected DataSource dataSource;
	
	public Pathway(Element element) {
		primaryId = element.getAttribute("primary_id");
		name = DOMUtil.firstChildContent(element, "name");
		Element dataSourceElement = DOMUtil.firstChildElement(element, "data_source");
		if(dataSourceElement != null) { dataSource = new DataSource(dataSourceElement); }
	}

	public String primaryId() { return primaryId; }
	public String name() { return name; }
	public Organism getOrganism() { return organism; }
	public DataSource dataSource() { return dataSource; }
	
	public void setOrganism(Organism organism) {
		this.organism = organism;
	}
	
	public boolean filterOut(String filterText) {
		if (filterText == null || filterText.length() == 0) {
			return false;	// keep
		}
		String lowerCaseFilterText = filterText.toLowerCase();
		if(name.toLowerCase().contains(lowerCaseFilterText)) {
			return false;
		} else if(organism.speciesName().toLowerCase().contains(lowerCaseFilterText)) {
			return false;
		} else if(dataSource.name().toLowerCase().contains(lowerCaseFilterText)) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof Pathway)) {
			return false;
		}
		if (((Pathway)obj).primaryId.equals(this.primaryId)) {
			 return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[Pathway: primaryId=\"" + primaryId + "\"; name=\"" + name + "\";\n" + 
		"dataSource=" + (dataSource != null ? dataSource.toString() : "null") + "]"; 
	}

	public int compareTo(Object obj) {
		return name.toLowerCase().compareTo(((Pathway)obj).name().toLowerCase());
	}
}
