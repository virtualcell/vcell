package cbit.vcell.biomodel.meta.xml.rdf;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.vcell.sybil.models.annotate.JDOM2Model;
import org.xml.sax.SAXParseException;

import cbit.vcell.biomodel.meta.VCMetaData;

/**
 * Extracts RDF statements from JDOM elements and adds them to the Jena Model
 * @author ruebenacker
 *
 */

public class XMLRDFReader extends XMLRDF {
	
	public static void addToModelFromElement(VCMetaData metaData, Element element) 
	throws SAXParseException, JDOMException {
		JDOM2Model jdom2model = new JDOM2Model(metaData.getRdf());
		jdom2model.addJDOM(element, metaData.getBaseURI());
	}
	
}
