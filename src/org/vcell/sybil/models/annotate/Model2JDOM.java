package org.vcell.sybil.models.annotate;

import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.vcell.sybil.rdf.NSMap;
import org.vcell.sybil.rdf.NameSpace;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;


/*   DOM2JModel  --- May 2009
 *   Generates a JDOM tree representing RDF/XML from a Model
 *   Last change: Oliver Ruebenacker
 */

public class Model2JDOM {
	
	protected Element root;
	protected Map<Resource, Element> resourceToElement = 
		new HashMap<Resource, Element>();
	protected Map<Resource, String> bNodeToID = 
		new HashMap<Resource, String>();

	public static Namespace nsRDF = 
		Namespace.getNamespace(NameSpace.RDF.prefix, NameSpace.RDF.uri);
	public static final String TYPELESS_NODE_NAME = "Description";
	protected NSMap nsMap = new NSMap(NameSpace.defaultMap);
	
	public Model2JDOM() { this(new Element("RDF", nsRDF)); }
	public Model2JDOM(Element root) { this.root = root; }

	public Element root() { return root; }
	
	public void addModel(Model model, String baseURI) {

//		RDFWriter writer = model.getWriter("RDF/XML-ABBREV");
//		StringWriter sw = new StringWriter();
//		writer.write(model, sw, baseURI);
//		root = XmlUtil.stringToXML(sw.getBuffer().toString(), null);

		StmtIterator stmtIter = model.listStatements();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.nextStatement();
			Resource subject = statement.getSubject();
			Element elementS = addSubjectElement(model, subject, root);
			Property predicate = statement.getPredicate();
			Element elementP = createPredicateElement(predicate);
			RDFNode object = statement.getObject();
			if(object instanceof Resource) {
				Resource objectR = (Resource) object;
				if(!isTypeSetAsName(elementS, subject, predicate, object)) {
					elementS.addContent(elementP);
					addObjectElement(model, objectR, elementP);										
				}
			} else {
				elementS.addContent(elementP);
				Literal objectL = (Literal) object;
				elementP.addContent(new Text(objectL.getLexicalForm()));
				String dataTypeURI = objectL.getDatatypeURI();
				if(dataTypeURI != null) {
					elementP.setAttribute("datatype", dataTypeURI, nsRDF);
				}
				String languageTag = objectL.getLanguage();
				if(languageTag != null) {
					elementP.setAttribute("lang", languageTag, 
							Namespace.XML_NAMESPACE);				
				}
			}
		}
	}
	
	protected Element addSubjectElement(Model model, Resource resource, Element defaultParent) {
		Element element = resourceToElement.get(resource);
		if(element == null) {
			element = createElement(model, resource);
			resourceToElement.put(resource, element);
			defaultParent.addContent(element);
		}
		return element;
	}

	protected Element createPredicateElement(Property predicate) {
		NameSpace ns = nsMap.provideNamesSpace(predicate.getNameSpace());
		return new Element(predicate.getLocalName(), 
				Namespace.getNamespace(ns.prefix, ns.uri));
	}
	
	protected Element addObjectElement(Model model, Resource resource, Element defaultParent) {
		Element element = createElement(model, resource);
		if(resourceToElement.get(resource) == null) { resourceToElement.put(resource, element); } 
		defaultParent.addContent(element);
		return element;
	}
	
	protected Element createElement(Model model, Resource resource) {
		Element element = null;
		StmtIterator stmtIter = model.listStatements(resource, RDF.type, (RDFNode) null);
		while(stmtIter.hasNext() && element == null) {
			RDFNode object = stmtIter.nextStatement().getObject();
			if(object.isURIResource()) {
				Resource type = (Resource) object;
				String nameSpaceURI = type.getNameSpace();
				String localName = type.getLocalName();
				if(localName != null && localName.length() > 0 && nameSpaceURI != null &&
						nameSpaceURI.length() > 0) {
					NameSpace ns = nsMap.provideNamesSpace(nameSpaceURI);
					element = new Element(localName, Namespace.getNamespace(ns.prefix, ns.uri));
				}				
			}
		} 
		if(element == null){
			element = new Element(TYPELESS_NODE_NAME, nsRDF);			
		}
		if(resource.isURIResource()) {
			element.setAttribute("about", resource.getURI(), nsRDF);
		} else {
			element.setAttribute("nodeID", blankNodeID(resource), nsRDF);
		}
		return element;
	}

	protected boolean isTypeSetAsName(Element elementS, Resource subject, Property predicate,
			RDFNode object) {
		boolean isSetAsName = false;
		if(RDF.type.equals(predicate) && object.isURIResource()) {
			Resource objectR = (Resource) object;
			isSetAsName = (elementS.getName().equals(objectR.getLocalName())) 
			&& (elementS.getNamespaceURI().equals(objectR.getNameSpace()));
		}
		return isSetAsName;
	}
	
	protected String blankNodeID(Resource node) {
		String id = bNodeToID.get(node);
		if(id == null) {
			id = "node" + bNodeToID.size();
			bNodeToID.put(node, id);
		}
		return id;
	}
	
}
