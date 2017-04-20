/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.persistence;

import java.io.File;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import org.sbpax.schemas.util.DefaultNameSpaces;
import org.vcell.pathway.PathwayModel;

import cbit.util.xml.XmlUtil;
import cbit.vcell.xml.XmlParseException;

public class PathwayBiopax3Test {

	private static final Namespace rdf = Namespace.getNamespace("rdf", DefaultNameSpaces.RDF.uri);

	private PathwayBiopax3Test() {}		//no instances allowed
	
	public static void main(String args[]){
		try {
			// sbpax: http://www.signaling-gateway.org/molecule/query?afcsid=A000037&type=sbPAXExport
			// items within items
//			Document document = XmlUtil.readXML(new File("C:\\dan\\reactome biopax\\sbpax3fullexample.xml"));
			
			// biopax with URI-like IDs and resources 
			// http://www.pathwaycommons.org/pc2/get?uri=HTTP:%2F%2FWWW.REACTOME.ORG%2FBIOPAX/48887%23PATHWAY1076_1_9606
			Document document = XmlUtil.readXML(new File("C:\\dan\\reactome biopax\\biopax3_no_id.xml"));	// sbpax3_uri_id.xml 
//			Document document = XmlUtil.readXML(new File("C:\\dan\\reactome biopax\\SBPax3.owl"));
//			Document document = XmlUtil.readXML(new File("C:\\dan\\reactome biopax\\Reactome3_189445.owl"));
			PathwayReaderBiopax3 pathwayReader = new PathwayReaderBiopax3(new RDFXMLContext());
			PathwayModel pathwayModel = pathwayReader.parse(document.getRootElement(),true);
//			System.out.print(pathwayModel.show(true));
			pathwayModel.reconcileReferences(null);
			
			bioModelToXML(pathwayModel);
			
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	static String bioModelToXML(PathwayModel pathwayModel) throws XmlParseException {

		String xmlString = null;
		
		try {
			if (pathwayModel == null){
				throw new IllegalArgumentException("Invalid input for pathwayModel: " + pathwayModel);
			}
			String biopaxVersion = "3.0";
			// create root element 
			Element rootElement = new Element("RDF", rdf);
			rootElement.setAttribute("version", biopaxVersion);
			
			// get element from producer and add it to root element
			PathwayProducerBiopax3 xmlProducer = new PathwayProducerBiopax3(new RDFXMLContext());
			xmlProducer.getXML(pathwayModel, rootElement);	// here is work done

			// create xml doc and convert to string
			Document bioDoc = new Document();
			Comment docComment = new Comment("This pathway model was generated in Biopax Version " + biopaxVersion); 
			bioDoc.addContent(docComment);
			bioDoc.setRootElement(rootElement);
			xmlString = XmlUtil.xmlToString(bioDoc, false);
			System.out.println(xmlString);

		} catch (Exception e) {
			e.printStackTrace();
			throw new XmlParseException("Unable to generate PathwayModel XML", e);
		} 
		
		return xmlString;
	}
	
}
