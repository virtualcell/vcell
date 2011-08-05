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

/*   Organism  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Organism from a web request using command search from Pathway Commons
 */

import org.vcell.sybil.util.xml.DOMUtil;
import org.w3c.dom.Element;

public class Organism { 

	protected String ncbiOrganismId = "", commonName = "", speciesName = ""; 
	
	public Organism(Element element) {
		ncbiOrganismId = element.getAttribute("ncbi_organism_id");
		commonName = DOMUtil.firstChildContent(element, "common_name");
		speciesName = DOMUtil.firstChildContent(element, "species_name");
	}

	public String ncbiOrganismId() { return ncbiOrganismId; }
	public String commonName() { return commonName; }
	public String speciesName() { return speciesName; }
	
	@Override
	public String toString() {
		return "[Organism: ncbiOrganismId=\"" + ncbiOrganismId + "\"; commonName=\"" + commonName + 
		"\"; speciesName=\"" + speciesName + "\"]"; 
	}
}
