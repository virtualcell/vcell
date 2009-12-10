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
	
	public String toString() {
		return "[DataSource: primaryId=\"" + primaryId + "\"; name=\"" + name + "\"]"; 
	}
}