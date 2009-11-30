package cbit.vcell.biomodel.meta.xml;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.Text;

import com.hp.hpl.jena.rdf.model.Resource;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.NonRDFAnnotation;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.registry.OpenRegistry.OpenEntry;
import cbit.vcell.biomodel.meta.xml.rdf.XMLRDFWriter;

/**
 * Turns meta data into a JDOM Element
 * @author ruebenacker
 *
 */

public class XMLMetaDataWriter extends XMLMetaData {
	
	public static Element getElement(VCMetaData metaData, cbit.vcell.biomodel.BioModel bioModel) {
		Element element = new Element(XMLMetaData.VCMETADATA_TAG, VCMetaData.nsVCML);
		Element elementRDF = XMLRDFWriter.createElement(metaData);
		// add RDF data
		if(elementRDF != null) {
			element.addContent(elementRDF);
		}

		Element elementNonRDFList = createNonRDFAnnotationElement(metaData,bioModel);
		element.addContent(elementNonRDFList);

/*		Set<Entry<OpenEntry, NonRDFAnnotation>> allNonRdfAnnotations = metaData.getAllNonRDFAnnotations();
		Element nonRDFAnnotationListElement = new Element(XMLMetaData.NONRDF_ANNOTATION_LIST_TAG);
		Iterator<Entry<OpenEntry, NonRDFAnnotation>> iter = allNonRdfAnnotations.iterator();
		while (iter.hasNext()){
			Entry<OpenEntry, NonRDFAnnotation> entry = iter.next();
			OpenEntry openEntry = entry.getKey();
			NonRDFAnnotation nonRDFAnnotation = entry.getValue();
			if (!nonRDFAnnotation.isEmpty()){
				Element nonRDFAnnotationElement = new Element(XMLMetaData.NONRDF_ANNOTATION_TAG);
				nonRDFAnnotationElement.setAttribute(XMLMetaData.VCID_ATTR_TAG, VCID.getVCID(bioModel, (Identifiable)openEntry.object()).toASCIIString(), XMLMetaData.nsVCML);
				nonRDFAnnotationListElement.addContent(nonRDFAnnotationElement);
				String freeTextAnnotation = nonRDFAnnotation.getFreeTextAnnotation();
				if (freeTextAnnotation!=null && freeTextAnnotation.length()>0){
					Element freeTextAnnotationElement = new Element(XMLMetaData.FREETEXT_TAG);
					freeTextAnnotationElement.addContent(new Text(freeTextAnnotation));
					nonRDFAnnotationElement.addContent(freeTextAnnotationElement);
				}
				Element xhtmlNotes = nonRDFAnnotation.getXhtmlNotes();
				if (xhtmlNotes!=null){
					Element notesElement = new Element(XMLMetaData.NOTES_TAG);
					notesElement.addContent(new Text(freeTextAnnotation));
					nonRDFAnnotationElement.addContent(notesElement);
				}
				Element[] otherAnnotations = nonRDFAnnotation.getXmlAnnotations();
				if (otherAnnotations!=null && otherAnnotations.length>0){
					Element annotationListElement = new Element(XMLMetaData.ANNOTATION_LIST_TAG);
					nonRDFAnnotationElement.addContent(annotationListElement);
					for (int i = 0; i < otherAnnotations.length; i++) {
						annotationListElement.addContent(otherAnnotations[i]);
					}
				}
			}
		}
*/			
		// add resource binding table
		Element bindingListElement = new Element(XMLMetaData.URI_BINDING_LIST_TAG,VCMetaData.nsVCML);
		Set<Resource> resources = metaData.getRegistry().getResources();
		for (Iterator<Resource> iterator = resources.iterator(); iterator.hasNext();) {
			Resource uri = iterator.next();
			OpenEntry entry = metaData.getRegistry().forResource(uri);
			Element entryElement = new Element(XMLMetaData.URI_BINDING_TAG, VCMetaData.nsVCML);
			entryElement.setAttribute(XMLMetaData.URI_ATTR_TAG, uri.getURI(), VCMetaData.nsVCML);
			VCID vcid = VCID.getVCID(bioModel, (Identifiable)entry.object());
			entryElement.setAttribute(XMLMetaData.VCID_ATTR_TAG, vcid.toASCIIString(), VCMetaData.nsVCML);
			bindingListElement.addContent(entryElement);
		}
		element.addContent(bindingListElement);
		
		return element;
	}
	
	/** creating XMLMetaData element for nonRDFAnnotation element here since nonRDFAnnotation was made package level
	 *	this is similar to XMLRDFWriter.createElement(metaData)
	 * 
	 * @return the created NonRDFAnnotationListElement
	 */
	private static Element createNonRDFAnnotationElement(VCMetaData metaData, BioModel bioModel) {
		Set<Entry<OpenEntry, NonRDFAnnotation>> allNonRdfAnnotations = metaData.getAllNonRDFAnnotations();
		Element nonRDFAnnotationListElement = new Element(XMLMetaData.NONRDF_ANNOTATION_LIST_TAG);
		Iterator<Entry<OpenEntry, NonRDFAnnotation>> iter = allNonRdfAnnotations.iterator();
		while (iter.hasNext()){
			Entry<OpenEntry, NonRDFAnnotation> entry = iter.next();
			OpenEntry openEntry = entry.getKey();
			NonRDFAnnotation nonRDFAnnotation = entry.getValue();
			if (!nonRDFAnnotation.isEmpty()){
				Element nonRDFAnnotationElement = new Element(XMLMetaData.NONRDF_ANNOTATION_TAG);
				nonRDFAnnotationElement.setAttribute(XMLMetaData.VCID_ATTR_TAG, VCID.getVCID(bioModel,(Identifiable)openEntry.object()).toASCIIString(), VCMetaData.nsVCML);
				nonRDFAnnotationListElement.addContent(nonRDFAnnotationElement);
				String freeTextAnnotation = nonRDFAnnotation.getFreeTextAnnotation();
				if (freeTextAnnotation!=null && freeTextAnnotation.length()>0){
					Element freeTextAnnotationElement = new Element(XMLMetaData.FREETEXT_TAG);
					freeTextAnnotationElement.addContent(new Text(freeTextAnnotation));
					nonRDFAnnotationElement.addContent(freeTextAnnotationElement);
				}
				Element xhtmlNotes = nonRDFAnnotation.getXhtmlNotes();
				if (xhtmlNotes!=null){
					Element notesElement = new Element(XMLMetaData.NOTES_TAG);
					notesElement.addContent(new Text(freeTextAnnotation));
					nonRDFAnnotationElement.addContent(notesElement);
				}
				Element[] otherAnnotations = nonRDFAnnotation.getXmlAnnotations();
				if (otherAnnotations!=null && otherAnnotations.length>0){
					Element annotationListElement = new Element(XMLMetaData.ANNOTATION_LIST_TAG);
					nonRDFAnnotationElement.addContent(annotationListElement);
					for (int i = 0; i < otherAnnotations.length; i++) {
						annotationListElement.addContent(otherAnnotations[i]);
					}
				}
			}
		}
		return nonRDFAnnotationListElement;
	}

//	public static Element getRDFElement(VCMetaData metaData, cbit.vcell.biomodel.BioModel bioModel, Identifiable identifiable) {
//		Element element = new Element(XMLMetaData.VCMETADATA_TAG, nsVCML);
//		Element elementRDF = XMLRDFWriter.createElement(metaData);
//		// add RDF data
//		if(elementRDF != null) {
//			element.addContent(elementRDF);
//		}
//		// add resource binding table
//		Element bindingListElement = new Element(XMLMetaData.URI_BINDING_LIST_TAG,nsVCML);
//		URI uri = metaData.registry().forObject(identifiable).uri();
//		if (uri!=null){
//			OpenEntry entry = metaData.registry().forURI(uri);
//			Element entryElement = new Element(XMLMetaData.URI_BINDING_TAG, nsVCML);
//			entryElement.setAttribute(XMLMetaData.URI_ATTR_TAG, uri.toASCIIString(), nsVCML);
//			VCID vcid = VCID.getVCID(bioModel, (Identifiable)entry.object());
//			entryElement.setAttribute(XMLMetaData.VCID_ATTR_TAG, vcid.toASCIIString(), nsVCML);
//			bindingListElement.addContent(entryElement);
//		}
//		element.addContent(bindingListElement);
//		
//		return element;
//	}
	
}
