package cbit.vcell.biomodel.meta.xml.rdf;

import org.jdom.Element;
import org.openrdf.model.Graph;
import org.vcell.sybil.models.annotate.Model2JDOM;

/**
 * Turns an RDF Graph into a JDOM Element
 * @author ruebenacker
 *
 */

public class XMLRDFWriter extends XMLRDF {
	
	public static Element createElement(Graph rdf, String baseURI) {
		Model2JDOM model2jdom = new Model2JDOM();
		model2jdom.addModel(rdf, baseURI);
		return model2jdom.root();
	}
	
}
