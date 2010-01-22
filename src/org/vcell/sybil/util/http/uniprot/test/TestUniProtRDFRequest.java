package org.vcell.sybil.util.http.uniprot.test;

/*   UniProtRDFRequest  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   Launch a web request to UniProt to get RDF for an id
 */

import org.vcell.sybil.util.http.uniprot.UniProtExtractor;
import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest;
import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest.ExceptionResponse;
import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest.ModelResponse;
import org.vcell.sybil.util.http.uniprot.UniProtRDFRequest.Response;
import org.vcell.sybil.util.http.uniprot.box.UniProtBox;
import org.vcell.sybil.util.http.uniprot.box.imp.UniProtBoxImp;

import com.hp.hpl.jena.rdf.model.Model;

public class TestUniProtRDFRequest {

	public static void main(String[] args) {
		UniProtBox box = new UniProtBoxImp();
		getInfoForID(box, "Q6Q476");

//		getInfoForID(box, "P00533");
//		getInfoForID(box, "Q9H3C9");
//		getInfoForID(box, "Q9BZS2");
//		getInfoForID(box, "P06268");
//		for(UniProtBox.Entry entry : box.entries()) {
//			String recommendedName = entry.recommendedName();
//			// System.out.println("Entry : " + entry.id() + (entry.isObsolete() ? " is obsolete" : ""));
//			System.out.println("UniProt ID :  : " + entry.id() + (entry.isObsolete() ? " is obsolete" : ""));
//			for(UniProtBox.Entry entryReplaced : entry.replacedEntries()) {
//				System.out.println("  replaces " + entryReplaced.id());
//			}
//			for(UniProtBox.Entry entryReplacing : entry.replacingEntries()) {
//				System.out.println("  replaced by " + entryReplacing.id());
//				recommendedName = entryReplacing.recommendedName();
//			}
//			System.out.println("Recommended name: " + recommendedName);
//		}

		// for an obsolete uniprot id (entry), get the replacing id (or entry) and recommended name associated with it.
		String recommendedName = null;
		for(UniProtBox.Entry entry : box.entries()) {
			System.out.println("UniProt ID :  : " + entry.id() + (entry.isObsolete() ? " is obsolete" : ""));
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
		System.out.println("Recommended name: " + recommendedName);
	}

	private static void getInfoForID(UniProtBox box, String id2) {
		UniProtRDFRequest request = new UniProtRDFRequest(id2);
		Response response = request.response();
		if(response instanceof ModelResponse) {
			Model model = ((ModelResponse) response).model();
			// suppressing the output for replaces/replace by properties; will enable later, when required.
			//model.write(System.out, "N3");
			box.add(UniProtExtractor.extractBox(model));
		} else if(response instanceof ExceptionResponse) {
			System.out.println("an exception was thrown");
			((ExceptionResponse) response).exception().printStackTrace();
		} else {
			System.out.println("no model");
		}
	}
	
}
