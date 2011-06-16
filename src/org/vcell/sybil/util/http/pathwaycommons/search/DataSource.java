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

/*   DataSource  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   DataSource from a web request using command search from Pathway Commons
 */

import org.vcell.sybil.util.xml.DOMUtil;
import org.w3c.dom.Element;

public class DataSource { 

	protected String primaryId = "", name = ""; 
	
	public DataSource(Element element) {
		primaryId = element.getAttribute("primary_id");
		name = DOMUtil.firstChildContent(element, "name");
	}

	public String primaryId() { return primaryId; }
	public String name() { return name; }
	
	@Override
	public String toString() {
		return "[DataSource: primaryId=\"" + primaryId + "\"; name=\"" + name + "\"]"; 
	}
}
