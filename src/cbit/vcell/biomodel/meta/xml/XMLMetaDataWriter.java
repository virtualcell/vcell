package cbit.vcell.biomodel.meta.xml;

import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.Text;

import com.hp.hpl.jena.rdf.model.Resource;

import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.NonRDFAnnotation;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.biomodel.meta.registry.Registry;
import cbit.vcell.biomodel.meta.registry.OpenRegistry.OpenEntry;
import cbit.vcell.biomodel.meta.xml.rdf.XMLRDFWriter;

/**
 * Turns meta data into a JDOM Element
 * @author ruebenacker
 *
 */

public class XMLMetaDataWriter extends XMLMetaData {
	
	public static Element getElement(VCMetaData metaData, IdentifiableProvider identifiableProvider) {
		Element element = new Element(XMLMetaData.VCMETADATA_TAG);
		Element elementRDF = XMLRDFWriter.createElement(metaData);
		// add RDF data
		if(elementRDF != null) {
			element.addContent(elementRDF);
		}

		Element elementNonRDFList = createNonRDFAnnotationElement(metaData,identifiableProvider);
		element.addContent(elementNonRDFList);

		// add resource binding table
		Element bindingListElement = new Element(XMLMetaData.URI_BINDING_LIST_TAG);
		Set<Registry.Entry> resources = metaData.getRegistry().getAllEntries();
		for (Registry.Entry entry : resources) {
			VCID vcid = identifiableProvider.getVCID((Identifiable)entry.getIdentifiable());
			Identifiable identifiable = identifiableProvider.getIdentifiableObject(vcid);
			if (identifiable != null && entry.getNamedThing()!=null){
				Element entryElement = new Element(XMLMetaData.URI_BINDING_TAG);
				Resource resource = entry.getNamedThing().resource();
				if (resource!=null){
					entryElement.setAttribute(XMLMetaData.URI_ATTR_TAG, resource.getURI());				
				}
				entryElement.setAttribute(XMLMetaData.VCID_ATTR_TAG, vcid.toASCIIString());
				bindingListElement.addContent(entryElement);
			}
		}
		element.addContent(bindingListElement);
		
		return element;
	}
	
	/** creating XMLMetaData element for nonRDFAnnotation element here since nonRDFAnnotation was made package level
	 *	this is similar to XMLRDFWriter.createElement(metaData)
	 * 
	 * @return the created NonRDFAnnotationListElement
	 */
	private static Element createNonRDFAnnotationElement(VCMetaData metaData, IdentifiableProvider identifiableProvider) {
		Set<Entry<OpenEntry, NonRDFAnnotation>> allNonRdfAnnotations = metaData.getAllNonRDFAnnotations();
		Element nonRDFAnnotationListElement = new Element(XMLMetaData.NONRDF_ANNOTATION_LIST_TAG);
		Iterator<Entry<OpenEntry, NonRDFAnnotation>> iter = allNonRdfAnnotations.iterator();
		while (iter.hasNext()){
			Entry<OpenEntry, NonRDFAnnotation> mapEntry = iter.next();
			OpenEntry openEntry = mapEntry.getKey();
			NonRDFAnnotation nonRDFAnnotation = mapEntry.getValue();
			VCID vcid = identifiableProvider.getVCID((Identifiable)openEntry.getIdentifiable());
			Identifiable identifiable = identifiableProvider.getIdentifiableObject(vcid);
			// only write out nonRDF annotation if identifiable in identifiableProvider is not null
			if (identifiable != null) {
				if (!nonRDFAnnotation.isEmpty()){
					Element nonRDFAnnotationElement = new Element(XMLMetaData.NONRDF_ANNOTATION_TAG);
					nonRDFAnnotationElement.setAttribute(XMLMetaData.VCID_ATTR_TAG, vcid.toASCIIString());
					nonRDFAnnotationListElement.addContent(nonRDFAnnotationElement);
					String freeTextAnnotation = nonRDFAnnotation.getFreeTextAnnotation();
					if (freeTextAnnotation!=null && freeTextAnnotation.length()>0){
						Element freeTextAnnotationElement = new Element(XMLMetaData.FREETEXT_TAG);
						freeTextAnnotationElement.addContent(new Text(freeTextAnnotation));
						nonRDFAnnotationElement.addContent(freeTextAnnotationElement);
					}
					Element xhtmlNotes = nonRDFAnnotation.getXhtmlNotes();
					if (xhtmlNotes!=null){
						// Element notesElement = new Element(XMLMetaData.NOTES_TAG);
						// notesElement.addContent(xhtmlNotes));
						nonRDFAnnotationElement.addContent(xhtmlNotes.detach());
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
		}
		return nonRDFAnnotationListElement;
	}
	
}
