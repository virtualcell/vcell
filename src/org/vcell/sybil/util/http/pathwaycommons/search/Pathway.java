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

public class Pathway { 

	protected String primaryId = "", name = ""; 
	protected DataSource dataSource;
	
	public Pathway(Element element) {
		primaryId = element.getAttribute("primary_id");
		name = DOMUtil.firstChildContent(element, "name");
		Element dataSourceElement = DOMUtil.firstChildElement(element, "data_source");
		if(dataSourceElement != null) { dataSource = new DataSource(dataSourceElement); }
	}

	public String primaryId() { return primaryId; }
	public String name() { return name; }
	public DataSource dataSource() { return dataSource; }
	
	public String toString() {
		return "[Pathway: primaryId=\"" + primaryId + "\"; name=\"" + name + "\";\n" + 
		"dataSource=" + (dataSource != null ? dataSource.toString() : "null") + "]"; 
	}
}
