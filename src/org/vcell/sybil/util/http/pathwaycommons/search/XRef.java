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
	
	public String toString() {
		return "[XRef: db=\"" + db + "\"; id=\"" + id + "\"; url=\"" + url + "\"]"; 
	}
}