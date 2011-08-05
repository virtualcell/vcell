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

/*   XRef  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   XRef from a web request using command search from Pathway Commons
 */

import org.vcell.sybil.util.xml.DOMUtil;
import org.w3c.dom.Element;

public class XRef { 

	protected String db = "", id = "", url = ""; 
	
	public XRef(Element element) {
		db = DOMUtil.firstChildContent(element, "db");
		id = DOMUtil.firstChildContent(element, "id");
		url = DOMUtil.firstChildContent(element, "url");
	}

	public String db() { return db; }
	public String id() { return id; }
	public String url() { return url; }
	
	@Override
	public String toString() {
		return "[XRef: db=\"" + db + "\"; id=\"" + id + "\"; url=\"" + url + "\"]"; 
	}
}
