package org.vcell.sybil.models.annotate;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.DOMOutputter;
import org.xml.sax.SAXParseException;

import com.hp.hpl.jena.rdf.arp.DOM2Model;
import com.hp.hpl.jena.rdf.model.Model;

/*   JDOM2Model  --- May 2009
 *   Add RDF from JDOM elements representing RDF/XML
 *   Last change: Oliver Ruebenacker
 */

public class JDOM2Model {

	protected Model model;
	
	public JDOM2Model(Model model) { this.model = model; }
	
	public Model model() { return model; }
	
	public void addJDOM(Element element, String baseURI) 
	throws JDOMException, SAXParseException {
		Document document = new Document((Element) element.clone());
		org.w3c.dom.Document documentDOM = new DOMOutputter().output(document);
		DOM2Model.createD2M(baseURI, model).load(documentDOM);
	}
	
}
