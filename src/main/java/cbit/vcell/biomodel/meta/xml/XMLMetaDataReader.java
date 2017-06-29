/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel.meta.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.vcell.util.document.Identifiable;

import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCID.InvalidVCIDException;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.xml.rdf.XMLRDF;
import cbit.vcell.xml.XmlParseException;

/**
 * Turns a JDOM Element into meta data 
 * @author ruebenacker
 *
 */

public class XMLMetaDataReader extends XMLMetaData {
	
	@SuppressWarnings("unchecked")
	public static void readFromElement(VCMetaData metaData, IdentifiableProvider identifiableProvider, Element metadataElement) throws XmlParseException {
		Element bindingElement = metadataElement.getChild(XMLMetaData.URI_BINDING_LIST_TAG, VCMetaData.nsVCML);
		if (bindingElement!=null){
			// read binding
			List<Element> entryElements = bindingElement.getChildren(XMLMetaData.URI_BINDING_TAG, VCMetaData.nsVCML);
			for (Iterator<Element> iterator = entryElements.iterator(); iterator.hasNext();) {
				Element entryElement = iterator.next();
				try {
					String uri = entryElement.getAttributeValue(XMLMetaData.URI_ATTR_TAG);
					String vcidString = entryElement.getAttributeValue(XMLMetaData.VCID_ATTR_TAG);
					// make new entry based on URI
					// create VCID
					VCID vcid = VCID.fromString(vcidString);
					// lookup Identifiable object using VCID ... add to entry.
					metaData.getRegistry().newEntry(metaData.getRdfData(), identifiableProvider.getIdentifiableObject(vcid), uri);
					
				} catch (VCID.InvalidVCIDException e){
					e.printStackTrace();
					throw new XmlParseException(e);
				}
			}
		}
		Element rdfElement = metadataElement.getChild(XMLRDF.tagRDF, XMLRDF.nsRDF);
		if (rdfElement!=null){
			// read RDF
			try { 
				metaData.addToModelFromElement(rdfElement);
			} catch (RDFParseException e) {
				e.printStackTrace();
				throw new XmlParseException(e.getMessage());
			} catch (RDFHandlerException e) {
				e.printStackTrace();
				throw new XmlParseException(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				throw new XmlParseException(e.getMessage());
			}
		}
		Element nonRDFAnnotationListElement = metadataElement.getChild(XMLMetaData.NONRDF_ANNOTATION_LIST_TAG, VCMetaData.nsVCML);
		if (nonRDFAnnotationListElement!=null){
			List<Element> nonRDFAnnotationElements = nonRDFAnnotationListElement.getChildren(XMLMetaData.NONRDF_ANNOTATION_TAG, VCMetaData.nsVCML);
			for (Element nonRDFAnnotationElement : nonRDFAnnotationElements){
				String vcidString = nonRDFAnnotationElement.getAttributeValue(XMLMetaData.VCID_ATTR_TAG);
				VCID vcid = null;
				try {
					vcid = VCID.fromString(vcidString);
				} catch (InvalidVCIDException e) {
					e.printStackTrace();
					throw new XmlParseException(e.getMessage());
				}
				Identifiable identifiable = identifiableProvider.getIdentifiableObject(vcid);
				if (identifiable!=null){
					// populate the annotation
					Element freeTextAnnotationElement = nonRDFAnnotationElement.getChild(XMLMetaData.FREETEXT_TAG, VCMetaData.nsVCML);
					if (freeTextAnnotationElement!=null){
						String freeText = freeTextAnnotationElement.getText();
						metaData.setFreeTextAnnotation(identifiable, freeText);
					}
					Element xhtmlNotesElement = nonRDFAnnotationElement.getChild(XMLMetaData.NOTES_TAG, VCMetaData.nsVCML);
					if (xhtmlNotesElement!=null){
						metaData.setXhtmlNotes(identifiable, xhtmlNotesElement);
					}
					Element annotationListElement = nonRDFAnnotationElement.getChild(XMLMetaData.ANNOTATION_LIST_TAG, VCMetaData.nsVCML);
					if (annotationListElement!=null){
						List<?> annotationContents = annotationListElement.getContent();
						List<Element> annotationElements = new ArrayList<Element>();
						for (int i = 0; i < annotationContents.size(); i++) {
							if (annotationContents.get(i) instanceof Element){
								annotationElements.add((Element)annotationContents.get(i));
							}
						}
						metaData.setXmlAnnotations(identifiable, annotationElements.toArray(new Element[annotationElements.size()]));
					}
				}else{
					System.err.println("Cannot find identifiable for vcid : " + vcidString);
				}
			}
		}
	}
	
}
