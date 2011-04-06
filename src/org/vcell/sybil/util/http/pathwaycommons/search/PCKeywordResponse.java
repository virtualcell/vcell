package org.vcell.sybil.util.http.pathwaycommons.search;

/*   Response  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Response from a web request using command search from Pathway Commons
 */

import java.util.List;
import java.util.Vector;

import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.vcell.sybil.util.http.uniprot.UniProtExtractor;
import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest;
import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest.Response;
import org.vcell.sybil.util.http.uniprot.box.UniProtBox;
import org.vcell.sybil.util.http.uniprot.box.imp.UniProtBoxImp;
import org.vcell.sybil.util.text.StringUtil;
import org.vcell.sybil.util.xml.DOMUtil;
import org.w3c.dom.Element;

import com.hp.hpl.jena.rdf.model.Model;

public class PCKeywordResponse extends PathwayCommonsResponse {

	protected int totalNumHits;
	protected List<Hit> hits;
	
	// TODO make this nicer
	protected static UniProtBox uniProtBox = new UniProtBoxImp();
	
	public static UniProtBox uniProtBox() { return uniProtBox; }
	
	public PCKeywordResponse(PCKeywordRequest request, Element searchResponse) { 
		super(request);
		if(searchResponse != null) {
			totalNumHits = Integer.parseInt(searchResponse.getAttribute("total_num_hits"));
			List<Element> hitElements = DOMUtil.childElements(searchResponse, "search_hit");
			hits = new Vector<Hit>();
			for(Element hitElement : hitElements) { 
				Hit hit = new Hit(hitElement);
				hits.add(hit); 
				for(XRef xref : hit.xRefs()) {
					if(xref.db().equalsIgnoreCase("uniprot")) {
						UniProtRDFRequest uniProtRequest = new UniProtRDFRequest(xref.id());
						Response uniProtResponse = uniProtRequest.response();
						if(uniProtResponse instanceof UniProtRDFRequest.ModelResponse) {
							Model model = ((UniProtRDFRequest.ModelResponse) uniProtResponse).model();
							uniProtBox.add(UniProtExtractor.extractBox(model));
						}
					}
				}
			}				
		}
	}
	
	public int totalNumHits() { return totalNumHits; }
	public List<Hit> hits() { return hits; }
	@Override
	public PCKeywordRequest request() { return (PCKeywordRequest) super.request(); }
	
	@Override
	public String toString() {
		return "[Response: totalNumHits=" + totalNumHits + ";\n" +
		"hits=(" + StringUtil.concat(hits, ";\n") +
		"]\n";
	}
}