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
	
	@Override
	public String toString() {
		return "[Pathway: primaryId=\"" + primaryId + "\"; name=\"" + name + "\";\n" + 
		"dataSource=" + (dataSource != null ? dataSource.toString() : "null") + "]"; 
	}
}