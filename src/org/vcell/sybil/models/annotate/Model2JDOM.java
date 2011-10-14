/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.annotate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.sbpax.schemas.util.NSMap;
import org.sbpax.schemas.util.NameSpace;


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
		Namespace.getNamespace(DefaultNameSpaces.RDF.prefix, DefaultNameSpaces.RDF.uri);
	public static final String TYPELESS_NODE_NAME = "Description";
	protected NSMap nsMap = new NSMap(DefaultNameSpaces.defaultMap);
	
	public Model2JDOM() { this(new Element("RDF", nsRDF)); }
	public Model2JDOM(Element root) { this.root = root; }

	public Element root() { return root; }
	
	public void addModel(Graph model, String baseURI) {

//		RDFWriter writer = model.getWriter("RDF/XML-ABBREV");
//		StringWriter sw = new StringWriter();
//		writer.write(model, sw, baseURI);
//		root = XmlUtil.stringToXML(sw.getBuffer().toString(), null);

		for(Statement statement : model) {
			Resource subject = statement.getSubject();
			Element elementS = addSubjectElement(model, subject, root);
			URI predicate = statement.getPredicate();
			Element elementP = createPredicateElement(predicate);
			Value object = statement.getObject();
			if(object instanceof Resource) {
				Resource objectR = (Resource) object;
				if(!isTypeSetAsName(elementS, subject, predicate, object)) {
					elementS.addContent(elementP);
					addObjectElement(model, objectR, elementP);										
				}
			} else {
				elementS.addContent(elementP);
				Literal objectL = (Literal) object;
				elementP.addContent(new Text(objectL.stringValue()));
				URI dataTypeURI = objectL.getDatatype();
				if(dataTypeURI != null) {
					elementP.setAttribute("datatype", dataTypeURI.stringValue(), nsRDF);
				}
				String languageTag = objectL.getLanguage();
				if(languageTag != null) {
					elementP.setAttribute("lang", languageTag, 
							Namespace.XML_NAMESPACE);				
				}
			}
		}
	}
	
	protected Element addSubjectElement(Graph model, Resource resource, Element defaultParent) {
		Element element = resourceToElement.get(resource);
		if(element == null) {
			element = createElement(model, resource);
			resourceToElement.put(resource, element);
			defaultParent.addContent(element);
		}
		return element;
	}

	protected Element createPredicateElement(URI predicate) {
		NameSpace ns = nsMap.provideNamesSpace(predicate.getNamespace());
		return new Element(predicate.getLocalName(), 
				Namespace.getNamespace(ns.prefix, ns.uri));
	}
	
	protected Element addObjectElement(Graph model, Resource resource, Element defaultParent) {
		Element element = createElement(model, resource);
		if(resourceToElement.get(resource) == null) { resourceToElement.put(resource, element); } 
		defaultParent.addContent(element);
		return element;
	}
	
	protected Element createElement(Graph model, Resource resource) {
		Element element = null;
		Iterator<Statement> stmtIter = model.match(resource, RDF.TYPE, null);
		while(stmtIter.hasNext()) {
			Value object = stmtIter.next().getObject();
			if(object instanceof URI) {
				URI type = (URI) object;
				String nameSpaceURI = type.getNamespace();
				String localName = type.getLocalName();
				if(localName != null && localName.length() > 0 && nameSpaceURI != null &&
						nameSpaceURI.length() > 0) {
					NameSpace ns = nsMap.provideNamesSpace(nameSpaceURI);
					element = new Element(localName, Namespace.getNamespace(ns.prefix, ns.uri));
					break;
				}				
			}
		} 
		if(element == null){
			element = new Element(TYPELESS_NODE_NAME, nsRDF);			
		}
		if(resource instanceof URI) {
			element.setAttribute("about", resource.stringValue(), nsRDF);
		} else {
			element.setAttribute("nodeID", blankNodeID(resource), nsRDF);
		}
		return element;
	}

	protected boolean isTypeSetAsName(Element elementS, Resource subject, URI predicate,
			Value object) {
		boolean isSetAsName = false;
		if(RDF.TYPE.equals(predicate) && object instanceof URI) {
			URI objectR = (URI) object;
			isSetAsName = (elementS.getName().equals(objectR.getLocalName())) 
			&& (elementS.getNamespaceURI().equals(objectR.getNamespace()));
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
