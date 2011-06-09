package org.vcell.sybil.rdf;

/*   StringUtil  --- by Oliver Ruebenacker, UCHC --- September 2008
 *   Utilities for Jena model input and output
 */

import java.io.StringReader;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JenaIOUtil {
	
	public static Model modelFromText(String text, String uri) {
		Model model = ModelFactory.createDefaultModel();
		StringReader strReader = new StringReader(text);
		model.read(strReader, uri);
		return model;
	}
	
}
