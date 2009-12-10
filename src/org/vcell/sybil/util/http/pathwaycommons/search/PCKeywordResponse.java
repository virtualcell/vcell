package org.vcell.sybil.util.http.pathwaycommons.search;

/*   Response  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Response from a web request using command search from Pathway Commons
 */

import java.util.List;
import java.util.Vector;

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.vcell.sybil.util.text.StringUtil;
import org.vcell.sybil.util.xml.DOMUtil;
import org.w3c.dom.Element;

public class PCKeywordResponse extends PathwayCommonsResponse {

	protected int totalNumHits;
	protected List<Hit> hits;
	
	public PCKeywordResponse(PCKeywordRequest request, Element searchResponse) { 
		super(request);
		if(searchResponse != null) {
			totalNumHits = Integer.parseInt(searchResponse.getAttribute("total_num_hits"));
			List<Element> hitElements = DOMUtil.childElements(searchResponse, "search_hit");
			hits = new Vector<Hit>();
			for(Element hitElement : hitElements) { hits.add(new Hit(hitElement)); }				
		}
	}
	
	public int totalNumHits() { return totalNumHits; }
	public List<Hit> hits() { return hits; }
	public PCKeywordRequest request() { return (PCKeywordRequest) super.request(); }
	
	public String toString() {
		return "[Response: totalNumHits=" + totalNumHits + ";\n" +
		"hits=(" + StringUtil.concat(hits, ";\n") +
		"]\n";
	}
}