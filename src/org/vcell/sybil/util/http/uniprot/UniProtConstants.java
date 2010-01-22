package org.vcell.sybil.util.http.uniprot;

import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest.ExceptionResponse;
import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest.ModelResponse;
import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest.Response;
import org.vcell.sybil.util.http.uniprot.box.UniProtBox;
import org.vcell.sybil.util.http.uniprot.box.imp.UniProtBoxImp;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/*   UniProtURIs  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   Some information about UniProt useful for web requests.
 *   Note the complexity of the RDF obtained from UniProt:
 *   some URIs begin with "www", others with "purl", some contain
 *   the suffix ".rdf", others do not.
 */

public class UniProtConstants {

	public static final String urlBase = "http://www.uniprot.org/uniprot/";
	public static final String uriBase = "http://purl.uniprot.org/uniprot/";
	public static final String nsDefault = "http://purl.uniprot.org/core/";
	public static final String rdfFileDir = urlBase;
	public static final String rdfFileSuffix = ".rdf";
	
	public static String url(String id) { return urlBase + id; }
	public static String urlRDF(String id) { return rdfFileDir + id + rdfFileSuffix; }
	public static String uri(String id) { return uriBase + id; }
	public static String uriCore(String localName) { return nsDefault + localName; }
	public static String idFromResource(Resource resource) { return resource.getLocalName(); }

	public static Property coreProperty(String localName) {
		return ResourceFactory.createProperty(uriCore(localName));
	}
	
	public static final Property replaces = coreProperty("replaces");
	public static final Property replacedBy = coreProperty("replacedBy");
	public static final Property recommendedName = coreProperty("recommendedName");
	public static final Property fullName = coreProperty("fullName");
	
	public static String getNameFromID(String urnStr) {
		// extract id of entry from urnStr : urn string is of the form "urn:miriam:uniprot:XXXX" ; we need to extract 'XXXX'
		if (!urnStr.toLowerCase().contains("uniprot")) {
			throw new RuntimeException("Resource '" + urnStr + "' is not a UniProt entry");
		}
		String entryId = urnStr.substring(urnStr.lastIndexOf(":")+1);
		System.out.println("EntryId from urn : " + entryId);
		UniProtBox box = new UniProtBoxImp();
		getInfoForID(box, entryId);
		String recommendedName = null;
		for(UniProtBox.Entry entry : box.entries()) {
			// System.out.println("UniProt ID :  : " + entry.id() + (entry.isObsolete() ? " is obsolete" : ""));
			if (entry.isObsolete()) {
				UniProtBox box2 = new UniProtBoxImp();
				for(UniProtBox.Entry entryReplacing : entry.replacingEntries()) {
					// System.out.println("  replaced by " + entryReplacing.id());
					getInfoForID(box2, entryReplacing.id());
					for(UniProtBox.Entry entry1 : box2.entries()) {
						if (!entry1.isObsolete() && recommendedName == null) {
							recommendedName = entry1.recommendedName();
						}
					}
					if (recommendedName != null) {
						break;
					}
				}
				if (recommendedName != null) {
					break;
				}
			} else {
				recommendedName = entry.recommendedName();
			}
		}
		// System.out.println("Recommended name: " + recommendedName);
		
		return recommendedName;
	}
	
	private static void getInfoForID(UniProtBox box, String id) {
		UniProtRDFRequest request = new UniProtRDFRequest(id);
		Response response = request.response();
		if(response instanceof ModelResponse) {
			Model model = ((ModelResponse) response).model();
			box.add(UniProtExtractor.extractBox(model));
		} else if(response instanceof ExceptionResponse) {
			System.out.println("an exception was thrown");
			((ExceptionResponse) response).exception().printStackTrace();
		} else {
			System.out.println("no model");
		}
	}

}
