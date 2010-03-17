package cbit.vcell.biomodel.meta.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXParseException;

import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.VCID.InvalidVCIDException;
import cbit.vcell.biomodel.meta.registry.OpenRegistry.OpenEntry;
import cbit.vcell.biomodel.meta.xml.rdf.XMLRDF;
import cbit.vcell.biomodel.meta.xml.rdf.XMLRDFReader;
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
					OpenEntry openEntry = metaData.getRegistry().forURI(uri);
					// create VCID
					VCID vcid = VCID.fromString(vcidString);
					// lookup Identifiable object using VCID ... add to entry.
					openEntry.setObject(identifiableProvider.getIdentifiableObject(vcid));
				} catch (VCID.InvalidVCIDException e){
					e.printStackTrace();
					throw new XmlParseException(e.getMessage());
				}
			}
		}
		Element rdfElement = metadataElement.getChild(XMLRDF.tagRDF, XMLRDF.nsRDF);
		if (rdfElement!=null){
			// read RDF
			try { 
				XMLRDFReader.addToModelFromElement(metaData, rdfElement);
			}catch (SAXParseException e) {
				e.printStackTrace();
				throw new XmlParseException(e.getMessage());
			}catch (JDOMException e) {
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
			}
		}
	}
	
}
