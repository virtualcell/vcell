package cbit.vcell.xml;

import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.text.html.HTML;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;

import cbit.gui.ZEnforcer;
import cbit.util.BeanUtils;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.Structure;

public class MIRIAMHelper {

//	private Hashtable<MIRIAMAnnotatable, Vector<Element>> miriamAnnotElements =
//		new Hashtable<MIRIAMAnnotatable, Vector<Element>>();
	
	private static class AttributeDescriptiveHeirarchy extends DescriptiveHeirarchy{
		private Attribute attribute;
		
		public AttributeDescriptiveHeirarchy(Attribute attribute){
			this.attribute = attribute;
		}
		public Attribute getAttribute(){
			return attribute;
		}
	}
	
	public static abstract class DescriptiveHeirarchy{
		private Vector<Element> heirarchy = new Vector<Element>();
		private Element listElement;
		private Element typeElement;
		
		private Element getCreatorElement(){
			for (int i = 0; i < heirarchy.size(); i++) {
				if(	heirarchy.get(i).getNamespaceURI().equals(XMLTags.DUBCORE_NAMESPACE_URI) &&
					heirarchy.get(i).getName().equals("creator")){
					return heirarchy.get(i);
				}
			}
			return null;
		}
//		private Element getRDFItemElement(){
//			for (int i = 0; i < heirarchy.size(); i++) {
//				if(	heirarchy.get(i).getNamespaceURI().equals(XMLTags.RDF_NAMESPACE_URI) &&
//					heirarchy.get(i).getName().equals("li")){
//					return heirarchy.get(i);
//				}
//			}
//			return null;
//			
//		}
		public Vector<Element> getHeirarchy(){
			return  heirarchy;
		}
		public boolean isCreatorChild(){
			if(heirarchy == null){
				return false;
			}
			
			return(getCreatorElement() != null);
		}
		public boolean isSameCreator(DescriptiveHeirarchy descrHeirarchy){
			return
				this.listElement != null &&
				descrHeirarchy.listElement != null &&
				this.listElement == descrHeirarchy.listElement;
//			Element thisCreator = getCreatorElement();
//			Element argCreator = descrHeirarchy.getCreatorElement();
//			if(thisCreator != null && argCreator != null && thisCreator == argCreator){
//				Element thisItem = getRDFItemElement();
//				Element argItem = descrHeirarchy.getRDFItemElement();
//				if(thisItem != null && argItem != null && thisItem == argItem){
//					return true;
//				}
//			}
//			return false;
		}
		public void setListElement(Element listElement){
			this.listElement = listElement;
		}
		public void setTypeElement(Element typeElement){
			this.typeElement = typeElement;
		}
		public Element getTypeElement(){
			return typeElement;
		}
		public Element getListElement(){
			return listElement;
		}
	}

	public static class MIRIAMTableRow{
		public MIRIAMAnnotatable miriamAnnotatable;
		public String[] rowData;
		public DescriptiveHeirarchy descriptiveHeirarchy;
	}
	private static class TextDescriptiveHeirarchy extends DescriptiveHeirarchy{
		private Text text;
		
		public TextDescriptiveHeirarchy(Text text){
			this.text = text;
		}
		public Text getText(){
			return text;
		}
	}
	private static final Namespace rdfNameSpace = Namespace.getNamespace(XMLTags.RDF_NAMESPACE_URI);
	
	public static final String[] MIRIAM_ANNOT_COLUMNS = new String[] {"Model Component","Component Name","Annotation Scheme","Annotation Qualifier","Authoritative Identifier"};
	
	public static void addToSBML(Element parent,MIRIAMAnnotation miriamAnnotation,boolean bAdd){
		try {
			if (parent.getName().equalsIgnoreCase(XMLTags.SbmlNotesTag)) {
				// while exporting object (biomodel/species,etc) to SBML, adding notes element from MiriamAnnotation
				addToSBMLNotes(parent, miriamAnnotation);
			} else if (parent.getName().equalsIgnoreCase(XMLTags.SbmlAnnotationTag)) {
				// while exporting object (biomodel/species,etc) to SBML, adding annotation element from MiriamAnnotation
				addToSBMLAnnotation(parent, miriamAnnotation);
			} else {
				// while exporting object (biomodel/species,etc) to VCML, adding annotations and notes elements to appropriate VCML element
				addToSBMLAnnotation(parent, miriamAnnotation);
				addToSBMLNotes(parent, miriamAnnotation);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

private static void addToSBMLAnnotation(Element parent,MIRIAMAnnotation miriamAnnotation){
		try {
			if(parent == null || miriamAnnotation == null || miriamAnnotation.getAnnotation() == null){
				return;
			}
			if(parent.getName().equalsIgnoreCase(XMLTags.SbmlAnnotationTag)){
				Element rdfElement = recurseForElement(miriamAnnotation.getAnnotation(),XMLTags.RDF_RDF_NAME_TAG);
				if (rdfElement != null) {
					parent.addContent(((Element)rdfElement.clone()).detach());
				}
			}else{
//			parent.addContent(miriamAnnotation.getAnnotation().detach());
				parent.addContent(((Element)miriamAnnotation.getAnnotation().clone()).detach());
			}
//		Element annotationElement = null;
//		if(parent.getName().equalsIgnoreCase(XMLTags.SbmlAnnotationTag)){
//			if(bAddAnnotation){
//				throw new IllegalArgumentException("Cannot add annotation to annotation");
//			}
//			annotationElement = parent;
//		}else{
//			annotationElement = parent.getChild(XMLTags.SbmlAnnotationTag);
//			if(annotationElement == null && !bAddAnnotation){
//				throw new IllegalArgumentException("No annotation element found");
//			}
//			if(annotationElement != null && bAddAnnotation){
//				throw new IllegalArgumentException("annotation element already exists");
//			}
//			if(annotationElement == null && bAddAnnotation){
//				annotationElement = new Element(XMLTags.SbmlAnnotationTag);
//				parent.addContent(annotationElement);
//			}
//		}
//		annotationElement.addContent(miriamAnnotation.getAnnotation().detach());
		} catch (Exception e) {
			e.printStackTrace();
		}
}
	
	private static void addToSBMLNotes(Element parent,MIRIAMAnnotation miriamAnnotation){
		try {
			if(parent == null || miriamAnnotation == null || miriamAnnotation.getUserNotes() == null){
				return;
			}
			if(parent.getName().equalsIgnoreCase(XMLTags.SbmlNotesTag)){
				Element htmlElement = recurseForElement(miriamAnnotation.getUserNotes(),HTML.Tag.HTML.toString());
				if (htmlElement != null) {
					parent.addContent(((Element)htmlElement.clone()).detach());
				}
			}else{
				parent.addContent(((Element)miriamAnnotation.getUserNotes().clone()).detach());
			}
//		Element userNotesElement = null;
//		if(parent.getName().equalsIgnoreCase(XMLTags.SbmlNotesTag)){
//			if(bAddNotes){
//				throw new IllegalArgumentException("Cannot add user notes to user notes");
//			}
//			userNotesElement = parent;
//		}else{
//			userNotesElement = parent.getChild(XMLTags.SbmlNotesTag);
//			if(userNotesElement == null && !bAddNotes){
//				throw new IllegalArgumentException("No notes element found");
//			}
//			if(userNotesElement != null && bAddNotes){
//				throw new IllegalArgumentException("notes element already exists");
//			}
//			if(userNotesElement == null && bAddNotes){
//				userNotesElement = new Element(XMLTags.SbmlNotesTag);
//				parent.addContent(userNotesElement);
//			}
//		}
//		userNotesElement.addContent(miriamAnnotation.getUserNotes().detach());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void addIdentifierToAnnotation(Element identiferElement,MIRIAMAnnotatable miriamAnnotatable,String qualifier,URI qualNamespace){
		if(miriamAnnotatable.getMIRIAMAnnotation() == null){
			miriamAnnotatable.setMIRIAMAnnotation(new MIRIAMAnnotation());
		}
		if(miriamAnnotatable.getMIRIAMAnnotation().getAnnotation() == null){
			miriamAnnotatable.getMIRIAMAnnotation().setAnnotation(createAnnotationSkeleton());
		}
		MIRIAMAnnotation miriamAnnotation = miriamAnnotatable.getMIRIAMAnnotation();
		Element qualififierElement = null;
		Vector<Element> pendingElements = new Vector<Element>();
		pendingElements.add(miriamAnnotation.getAnnotation());
		while(pendingElements.size() > 0){
			Element checkElement = pendingElements.remove(0);
			if(checkElement.getNamespaceURI().equals(qualNamespace.toString()) &&
					checkElement.getName().equals(qualifier)){
				qualififierElement = checkElement;
				break;
			}else if(checkElement.getNamespaceURI().equals(qualNamespace.toString()) &&
					checkElement.getName().equals(qualifier)){
				qualififierElement = checkElement;
				break;
			}
			if(checkElement.getChildren().size() > 0){
				pendingElements.addAll(checkElement.getChildren());
			}
		}
		if(qualififierElement == null){
			qualififierElement = addQualifierToAnnotation(miriamAnnotation.getAnnotation(),qualifier,qualNamespace);
		}
		qualififierElement.getChild(
			XMLTags.RDF_BAG_NAME_TAG,Namespace.getNamespace(XMLTags.RDF_NAMESPACE_URI)).addContent(identiferElement);
	}
	private static Element addQualifierToAnnotation(Element annotationElement,String qualifier,URI qualifierNamespace){
		Element rdfElement =
			annotationElement.getChild(XMLTags.RDF_RDF_NAME_TAG,Namespace.getNamespace(XMLTags.RDF_NAMESPACE_URI));
		if(rdfElement != null){
			Element descrElement =
				rdfElement.getChild(XMLTags.RDF_DESCRIPTION_NAME_TAG,Namespace.getNamespace(XMLTags.RDF_NAMESPACE_URI));
			if(descrElement != null){
				String qualifierPrefix = null;
				if(qualifierNamespace.toString().equals(XMLTags.BMBIOQUAL_NAMESPACE_URI)){
					qualifierPrefix = XMLTags.BMBIOQUAL_NAMESPACE_PREFIX;
				}else if(qualifierNamespace.toString().equals(XMLTags.BMMODELQUAL_NAMESPACE_URI)){
					qualifierPrefix = XMLTags.BMMODELQUAL_NAMESPACE_PREFIX;
				}
				Element qualiferElement =
					new Element(qualifier,qualifierPrefix,qualifierNamespace.toString());
				descrElement.addContent(qualiferElement);
				Element rdfBag =
					new Element(XMLTags.RDF_BAG_NAME_TAG,XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI);
				qualiferElement.addContent(rdfBag);
				return qualiferElement;	
			}
		}
		throw new RuntimeException("Couldn't find heirarchy to add qualifier");
	}

	public static Element addDateToAnnotation(MIRIAMAnnotatable miriamAnnotatable,String date,String dateQualifier){
		if(miriamAnnotatable.getMIRIAMAnnotation() == null){
			miriamAnnotatable.setMIRIAMAnnotation(new MIRIAMAnnotation());
		}
		if(miriamAnnotatable.getMIRIAMAnnotation().getAnnotation() == null){
			miriamAnnotatable.getMIRIAMAnnotation().setAnnotation(createAnnotationSkeleton());
		}
//		MIRIAMAnnotation miriamAnnotation = miriamAnnotatable.getMIRIAMAnnotation();
		Element annotationElement = miriamAnnotatable.getMIRIAMAnnotation().getAnnotation();
		Element rdfElement =
			annotationElement.getChild(XMLTags.RDF_RDF_NAME_TAG,Namespace.getNamespace(XMLTags.RDF_NAMESPACE_URI));
		if(rdfElement != null){
			Element descrElement =
				rdfElement.getChild(XMLTags.RDF_DESCRIPTION_NAME_TAG,Namespace.getNamespace(XMLTags.RDF_NAMESPACE_URI));
			if(descrElement != null){
//				String qualifierPrefix = null;
//				if(qualifierNamespace.toString().equals(XMLTags.BMBIOQUAL_NAMESPACE_URI)){
//					qualifierPrefix = XMLTags.BMBIOQUAL_NAMESPACE_PREFIX;
//				}else if(qualifierNamespace.toString().equals(XMLTags.BMMODELQUAL_NAMESPACE_URI)){
//					qualifierPrefix = XMLTags.BMMODELQUAL_NAMESPACE_PREFIX;
//				}
				Element dctermElement =
					new Element(dateQualifier,XMLTags.DUBCORETERMS_NAMESPACE_PREFIX,XMLTags.DUBCORETERMS_NAMESPACE_URI);
				Attribute parsetType =
					new Attribute(XMLTags.RDF_PARSETYPE_ATTR_TAG,XMLTags.RDF_PARSETYPE_ATTR_DATE_VALUE,
							Namespace.getNamespace(XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI));
				dctermElement.setAttribute(parsetType);
				descrElement.addContent(dctermElement);
				Element dateElement =
					new Element(XMLTags.DUBCORETERMS_W3CDTF_NAME_TAG,XMLTags.DUBCORETERMS_NAMESPACE_PREFIX,XMLTags.DUBCORETERMS_NAMESPACE_URI);
				dateElement.addContent(date);
				dctermElement.addContent(dateElement);
				return dctermElement;	
			}
		}
		throw new RuntimeException("Couldn't find heirarchy to add date");
	}

	public static Element addCreatorToAnnotation(MIRIAMAnnotatable miriamAnnotatable,String familyName,String givenName,String email,String organization){
		if(miriamAnnotatable.getMIRIAMAnnotation() == null){
			miriamAnnotatable.setMIRIAMAnnotation(new MIRIAMAnnotation());
		}
		if(miriamAnnotatable.getMIRIAMAnnotation().getAnnotation() == null){
			miriamAnnotatable.getMIRIAMAnnotation().setAnnotation(createAnnotationSkeleton());
		}
		Element annotationElement = miriamAnnotatable.getMIRIAMAnnotation().getAnnotation();
		Element rdfElement =
			annotationElement.getChild(XMLTags.RDF_RDF_NAME_TAG,Namespace.getNamespace(XMLTags.RDF_NAMESPACE_URI));
		if(rdfElement != null){
			Element descrElement =
				rdfElement.getChild(XMLTags.RDF_DESCRIPTION_NAME_TAG,Namespace.getNamespace(XMLTags.RDF_NAMESPACE_URI));
			if(descrElement != null){
				Element creatorElement =
					descrElement.getChild(XMLTags.DUBCORE_CREATOR_NAME_TAG,Namespace.getNamespace(XMLTags.DUBCORE_NAMESPACE_URI));
				if(creatorElement == null){
					creatorElement =
						new Element(XMLTags.DUBCORE_CREATOR_NAME_TAG,XMLTags.DUBCORE_NAMESPACE_PREFIX,XMLTags.DUBCORE_NAMESPACE_URI);
					Attribute parsetType =
						new Attribute(XMLTags.RDF_PARSETYPE_ATTR_TAG,XMLTags.RDF_PARSETYPE_ATTR_DATE_VALUE,
								Namespace.getNamespace(XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI));
					creatorElement.setAttribute(parsetType);
					descrElement.addContent(creatorElement);
				}
				Element creatorBagElement =
					creatorElement.getChild(XMLTags.RDF_BAG_NAME_TAG,Namespace.getNamespace(XMLTags.RDF_NAMESPACE_URI));
				if(creatorBagElement == null){
					creatorBagElement =
						new Element(XMLTags.RDF_BAG_NAME_TAG,XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI);
					creatorElement.addContent(creatorBagElement);
				}
				Element creatorLineElement =
					new Element(XMLTags.RDF_LI_NAME_TAG,XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI);
				Attribute parsetType =
					new Attribute(XMLTags.RDF_PARSETYPE_ATTR_TAG,XMLTags.RDF_PARSETYPE_ATTR_DATE_VALUE,
							Namespace.getNamespace(XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI));
				creatorLineElement.setAttribute(parsetType);
				creatorBagElement.addContent(creatorLineElement);
				Element vCardNameGroupElement =
					new Element(XMLTags.VCARD_NAMEGROUP_NAME_TAG,XMLTags.VCARD_NAMESPACE_PREFIX,XMLTags.VCARD_NAMESPACE_URI);
				parsetType =
					new Attribute(XMLTags.RDF_PARSETYPE_ATTR_TAG,XMLTags.RDF_PARSETYPE_ATTR_DATE_VALUE,
							Namespace.getNamespace(XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI));
				vCardNameGroupElement.setAttribute(parsetType);
				creatorLineElement.addContent(vCardNameGroupElement);
				Element vCardNameFamilyElement =
					new Element(XMLTags.VCARD_NAMEGROUP_FAMILY_NAME_TAG,XMLTags.VCARD_NAMESPACE_PREFIX,XMLTags.VCARD_NAMESPACE_URI);
				Element vCardNameGivenElement =
					new Element(XMLTags.VCARD_NAMEGROUP_GIVEN_NAME_TAG,XMLTags.VCARD_NAMESPACE_PREFIX,XMLTags.VCARD_NAMESPACE_URI);
				vCardNameGroupElement.addContent(vCardNameFamilyElement);
				vCardNameGroupElement.addContent(vCardNameGivenElement);
				Element vCardEmailElement =
					new Element(XMLTags.VCARD_EMAIL_NAME_TAG,XMLTags.VCARD_NAMESPACE_PREFIX,XMLTags.VCARD_NAMESPACE_URI);
				creatorLineElement.addContent(vCardEmailElement);
				Element vCardOrgGroupElement =
					new Element(XMLTags.VCARD_ORGGROUP_NAME_TAG,XMLTags.VCARD_NAMESPACE_PREFIX,XMLTags.VCARD_NAMESPACE_URI);
				creatorLineElement.addContent(vCardOrgGroupElement);
				Element vCardOrgGroupOrgNameElement =
					new Element(XMLTags.VCARD_ORGGROUP_ORGNAME_NAME_TAG,XMLTags.VCARD_NAMESPACE_PREFIX,XMLTags.VCARD_NAMESPACE_URI);
				vCardOrgGroupElement.addContent(vCardOrgGroupOrgNameElement);
				
				vCardNameFamilyElement.addContent(familyName);
				vCardNameGivenElement.addContent(givenName);
				vCardEmailElement.addContent(email);
				vCardOrgGroupOrgNameElement.addContent(organization);
				
				return creatorElement;	
			}
		}
		throw new RuntimeException("Couldn't find heirarchy to add creator");
	}

	public static void cleanEmptySpace(Element element){
		List elementContent = element.getContent();
		for (int i = 0; i < elementContent.size(); i++) {
			if(elementContent.get(i) instanceof Text){
				String trimText = ((Text)elementContent.get(i)).getText().trim();
				if(trimText.length() == 0){
					element.removeContent((Text)elementContent.get(i));
					i= -1;
					continue;
				}
				((Text)elementContent.get(i)).setText(trimText);
			}else if(elementContent.get(i) instanceof Element){
				if(!((Element)elementContent.get(i)).getName().equals(HTML.Tag.PRE.toString())){
					cleanEmptySpace((Element)elementContent.get(i));
				}
			}
		}
	}

	private static Element createAnnotationSkeleton(){
		Element annotation = new Element(XMLTags.SbmlAnnotationTag);
		Element rdf = new Element(XMLTags.RDF_RDF_NAME_TAG,XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI);
		annotation.addContent(rdf);
		Element description = new Element(XMLTags.RDF_DESCRIPTION_NAME_TAG,XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI);		
		rdf.addContent(description);
		return annotation;
	}
	public static Element createRDFIdentifier(String uri,String immortalID){
		Element rdfID = new Element(XMLTags.RDF_LI_NAME_TAG,XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI);
		Attribute attrID =
			new Attribute(XMLTags.RDF_RESOURCE_ATTR_TAG,uri+"#"+immortalID,
					Namespace.getNamespace(XMLTags.RDF_NAMESPACE_PREFIX,XMLTags.RDF_NAMESPACE_URI));
		rdfID.setAttribute(attrID);
		return rdfID;
	}
	public static void deleteDescriptiveHeirarchy(DescriptiveHeirarchy descrHeir){
		if(descrHeir == null){
			return;
		}
		if(descrHeir instanceof TextDescriptiveHeirarchy){
			if(descrHeir.isCreatorChild()){
				deleteElementWithCascade(descrHeir.getListElement());
			}else{
				deleteElementWithCascade(((TextDescriptiveHeirarchy)descrHeir).getText().getParent());
			}
		}else if(descrHeir instanceof AttributeDescriptiveHeirarchy){
			deleteElementWithCascade(((AttributeDescriptiveHeirarchy)descrHeir).getAttribute().getParent());
		}else{
			throw new IllegalArgumentException("Expecting Text or Element but got something else.");
		}
	}
	
	public static void deleteElementWithCascade(Element deleteElement){
//		if(descrHeir == null){
//			return;
//		}
		Element parent = deleteElement.getParent();
		parent.removeContent(deleteElement);
//		if(descrHeir instanceof TextDescriptiveHeirarchy){
//			parent = ((TextDescriptiveHeirarchy)descrHeir).getText().getParent();
//			parent = parent.getParent();
//			parent.removeContent(((TextDescriptiveHeirarchy)descrHeir).getText().getParent());
//		}if(descrHeir instanceof AttributeDescriptiveHeirarchy){
//			parent = ((AttributeDescriptiveHeirarchy)descrHeir).getAttribute().getParent();
//			parent = parent.getParent();
//			parent.removeContent(((AttributeDescriptiveHeirarchy)descrHeir).getAttribute().getParent());
//		}else{
//			throw new IllegalArgumentException("Expecting Text or Element but got something else.");
//		}
		while(true){
			List children;
			boolean bChildFound = false;
			boolean bChildremoved = false;
			if((children = parent.getContent()).size() != 0){
				bChildFound = true;
				for (int i = 0; i < children.size(); i++) {
					if(children.get(i) instanceof Text){
						if(((Text)children.get(i)).getText().trim().length() == 0){
							parent.removeContent((Text)children.get(i));
							bChildremoved = true;
							break;
						}
					}
				}
			}
			if(!bChildFound){
				Element temp = parent.getParent();
				if(temp != null){
					temp.removeContent(parent);
					parent = temp;
				}else{
					break;
				}
			}else if(!bChildremoved){
				break;
			}
		}
	}
	public static void editDescriptiveHeirarchy(DescriptiveHeirarchy descrHeir,String newValue){
		if(descrHeir == null){
			return;
		}
		if(descrHeir instanceof TextDescriptiveHeirarchy){
			((TextDescriptiveHeirarchy)descrHeir).getText().setText(newValue);
		}else if(descrHeir instanceof AttributeDescriptiveHeirarchy){
			((AttributeDescriptiveHeirarchy)descrHeir).getAttribute().setValue(newValue);
		}else{
			throw new IllegalAccessError("Error editing unknown DescriptiveHeirarchy type.");
		}
	}
	
	public static Element extractHTMLFromElement(Element userNotesElement){
		userNotesElement = (Element)userNotesElement.clone();
		MIRIAMHelper.cleanEmptySpace(userNotesElement);
		Element html = null;
		if(MIRIAMHelper.recurseForElement(userNotesElement, HTML.Tag.HTML.toString()) == null){
			// if userNotes doesn't have <html> tag, create one
			html = new Element(HTML.Tag.HTML.toString());
			Attribute attrXHTML =
				new Attribute(XMLTags.HTML_XHTML_ATTR_TAG,XMLTags.XHTML_URI);
			html.setAttribute(attrXHTML);
			html.addContent(new Element(HTML.Tag.HEAD.toString()));
			if(MIRIAMHelper.recurseForElement(userNotesElement, HTML.Tag.BODY.toString()) == null){
				// If userNotes doesn't have <body>, create one, and add children of <notes>, typically <p>, to <body>
				// Then add <body> to created <html> tag.
				Element body = new Element(HTML.Tag.BODY.toString());
				Iterator<Element> notesChildren = userNotesElement.getChildren().iterator();
				while (notesChildren.hasNext()) {
					Element noteChild = (Element)notesChildren.next().clone();
					body.addContent(noteChild);
				}
				html.addContent(body);
			}else{
				html.addContent(MIRIAMHelper.recurseForElement(userNotesElement, HTML.Tag.BODY.toString()).detach());
			}
		}else{
			html = MIRIAMHelper.recurseForElement(userNotesElement, HTML.Tag.HTML.toString()).detach();
		}
		return html;
	}

//	public static void addAnnotation(Element parent,MIRIAMAnnotation miriamAnnotation,boolean bAddAnnotIfNecessary){
//		if(parent == null || miriamAnnotation == null){
//			return;
//		}
//		Element annotationElem = parent.getChild(XMLTags.SbmlAnnotationTag);
//		if(annotationElem == null){
//			if(!bAddAnnotIfNecessary){
//				throw new IllegalArgumentException("MIRIAM Annotation element not found");
//			}
//			annotationElem = new org.jdom.Element(XMLTags.SbmlAnnotationTag);
//			parent.addContent(annotationElem);
//		}
//		annotationElem.addContent(miriamAnnotation.getAnnotation().detach());
//	}
	
	private static void getDescriptiveHeirarchy(DescriptiveHeirarchy descriptiveHeirarchy,Element element){
		if(element != null){
			if(isListElement(element)){
				descriptiveHeirarchy.setListElement(element);
			}
			if(descriptiveHeirarchy.getTypeElement() == null && isTypeElement(element)){
				descriptiveHeirarchy.setTypeElement(element);
			}
			if(isDescriptiveScheme(element.getNamespace())){
				descriptiveHeirarchy.getHeirarchy().add(element);
			}
			getDescriptiveHeirarchy(descriptiveHeirarchy, element.getParent());
		}
	}

	public static Element getQualifier(Element element){
		Vector<DescriptiveHeirarchy> descrHeirV = new Vector<DescriptiveHeirarchy>();
		traverse(descrHeirV, element);
		return descrHeirV.get(0).getHeirarchy().get(0);
	}
	public static Attribute[] getResourceElements(Element element){
		Vector<DescriptiveHeirarchy> descrHeirV = new Vector<DescriptiveHeirarchy>();
		traverse(descrHeirV, element);
		if(descrHeirV.size() > 0){
			Vector<Attribute> attrV = new Vector<Attribute>();
			for (int i = 0; i < descrHeirV.size(); i++) {
				if(descrHeirV.get(i) instanceof AttributeDescriptiveHeirarchy){
					attrV.add(((AttributeDescriptiveHeirarchy)descrHeirV.get(i)).getAttribute());
				}
			}
			if(attrV.size() > 0){
				return attrV.toArray(new Attribute[0]);
			}
		}
		return null;
	}
	
	public static Vector<MIRIAMTableRow> getTableFormattedData(TreeMap<MIRIAMAnnotatable, Vector<DescriptiveHeirarchy>> mirimaDescrHeir){

		
		Vector<MIRIAMTableRow> rowV = new Vector<MIRIAMTableRow>();
//		if(mirimaDescrHeir.size() > 0){
			Set<MIRIAMAnnotatable> keys = mirimaDescrHeir.keySet();
			Iterator<MIRIAMAnnotatable> iter = keys.iterator();
			while(iter.hasNext()){
				MIRIAMAnnotatable miriAnno = iter.next();
				Vector<DescriptiveHeirarchy> descrHeirV = mirimaDescrHeir.get(miriAnno);
				String modelComponentType = 
					(miriAnno instanceof BioModel?"BioModel":"")+
					(miriAnno instanceof Species?"Species":"")+
					(miriAnno instanceof Structure?"Structure":"")+
					(miriAnno instanceof ReactionStep?"ReactionStep":"");
				String modelComponentName =
					(miriAnno instanceof BioModel?((BioModel)miriAnno).getName():"")+
					(miriAnno instanceof Species?((Species)miriAnno).getCommonName():"")+
					(miriAnno instanceof Structure?((Structure)miriAnno).getName():"")+
					(miriAnno instanceof ReactionStep?((ReactionStep)miriAnno).getName():"");
				{
				MIRIAMTableRow miriamTableRow = new MIRIAMTableRow();
				miriamTableRow.descriptiveHeirarchy = null;
				miriamTableRow.miriamAnnotatable = miriAnno;
				miriamTableRow.rowData =
					new String[] {
						modelComponentType,modelComponentName,
						(descrHeirV.size() == 0?"-----":null),
						(descrHeirV.size() == 0?"-----":null),
						(descrHeirV.size() == 0?"None Defined":null)
					};
				rowV.add(miriamTableRow);
				}
				for (int i = 0; i < descrHeirV.size(); i++) {
					DescriptiveHeirarchy descrHeir = descrHeirV.get(i);
					String[] row = new String[MIRIAM_ANNOT_COLUMNS.length];
					row[0] = null;
					row[1] = null;
					row[4] =
						(descrHeir instanceof TextDescriptiveHeirarchy?((TextDescriptiveHeirarchy)descrHeir).getText().getText():"")+
						(descrHeir instanceof AttributeDescriptiveHeirarchy?((AttributeDescriptiveHeirarchy)descrHeir).getAttribute().getValue():"");
					if(descrHeir.getHeirarchy().size() > 0){
						row[3] = descrHeir.getHeirarchy().firstElement().getName();
						for (int j = 1; j < descrHeir.getHeirarchy().size(); j++) {
							row[3] = descrHeir.getHeirarchy().get(j).getName() + " " +row[3];
						}
						row[2] = descrHeir.getHeirarchy().lastElement().getNamespaceURI();
						for (int j = descrHeir.getHeirarchy().size()-2; j >= 0; j--) {
							if(!descrHeir.getHeirarchy().get(j).getNamespaceURI().equals(
									descrHeir.getHeirarchy().get(j+1).getNamespaceURI())){
								row[2] = row[2] + " , " + descrHeir.getHeirarchy().get(j).getNamespaceURI();
							}
						}
					}
					{
					MIRIAMTableRow miriamTableRow = new MIRIAMTableRow();
					miriamTableRow.descriptiveHeirarchy = descrHeir;
					miriamTableRow.miriamAnnotatable = miriAnno;
					miriamTableRow.rowData = row;
					rowV.add(miriamTableRow);
					}
//					rowV.add(row);
				}
				
			}
			return rowV;//rowV.toArray(new String[0][]);
	
	}
	
	private static boolean isDescriptiveScheme(Namespace nameSpace){
		return
		nameSpace.getURI().equals(XMLTags.VCARD_NAMESPACE_URI) ||
		nameSpace.getURI().equals(XMLTags.DUBCORE_NAMESPACE_URI) ||
		nameSpace.getURI().equals(XMLTags.DUBCORETERMS_NAMESPACE_URI) ||
		nameSpace.getURI().equals(XMLTags.BMBIOQUAL_NAMESPACE_URI) ||
		nameSpace.getURI().equals(XMLTags.BMMODELQUAL_NAMESPACE_URI);
		
	}
	
	private static boolean isListElement(Element element){
		return
			element.getNamespaceURI().equals(rdfNameSpace.getURI())
			&&
			element.getName().equals("li");
	}
	private static boolean isTypeElement(Element element){
		return
			element.getName().equals("model") ||
			element.getName().equals("reaction") ||
			element.getName().equals("species") ||
			element.getName().equals("compartment");
	}

	public static void parseAnnoationFiles(String[] directories) throws Exception{
		TreeSet<String> namespaceURITreeSet = new TreeSet<String>();
		HashMap<String, Vector<String>> namespaceURIToType  = new HashMap<String, Vector<String>>();
		for(int i=0;i<directories.length;i+= 1){
			File dirFile = new File(directories[i]);
			File[] fileArr = dirFile.listFiles();
			for (int j = 0; j < fileArr.length; j++) {
				if(fileArr[j].isFile() && fileArr[j].getName().endsWith("xml")){
					FileReader fileReader = null;
					try{
						fileReader = new FileReader(fileArr[j]);
						char[] cbuf = new char[(int)fileArr[j].length()];
						int currentSize = 0;
						while(currentSize != cbuf.length){
							currentSize+= fileReader.read(cbuf, currentSize, cbuf.length-currentSize);
						}
						fileReader.close();
						String fileStr = new String(cbuf);
						Element element = XmlUtil.stringToXML(fileStr, null);
						Vector<DescriptiveHeirarchy> descrHeirV = new Vector<DescriptiveHeirarchy>();
						recurseForAnnotation(element, descrHeirV);
						System.out.println("num elements found= "+descrHeirV.size());
						for (int k = 0; k < descrHeirV.size(); k++) {
							if(descrHeirV.get(k) instanceof AttributeDescriptiveHeirarchy){
								try{
									URI uri =
										new URI(((AttributeDescriptiveHeirarchy)descrHeirV.get(k)).getAttribute().getValue());
									if(uri.getAuthority() != null){
										String authorityAndPath =
											uri.getScheme()+
											(uri.getScheme().equalsIgnoreCase("http")?"://":"")+
											uri.getAuthority()+uri.getPath();
										namespaceURITreeSet.add(authorityAndPath);
										Vector<String> typeV = namespaceURIToType.get(authorityAndPath);
										if(typeV == null){
											typeV = new Vector<String>();
											namespaceURIToType.put(authorityAndPath, typeV);
										}
										if(!typeV.contains(descrHeirV.get(k).getTypeElement().getName())){
											typeV.add(descrHeirV.get(k).getTypeElement().getName());
										}
									}else{
										System.out.println("NULL Auth ="+uri);
									}
								}catch(Exception e){
									//ignore
								}
								System.out.println("attr= "+
										((AttributeDescriptiveHeirarchy)descrHeirV.get(k)).getAttribute().getName()+
										" "+((AttributeDescriptiveHeirarchy)descrHeirV.get(k)).getAttribute().getValue()+
										" "+descrHeirV.get(k).getTypeElement().getName());
							}else if(descrHeirV.get(k) instanceof TextDescriptiveHeirarchy){
								System.out.println("text= "+
										((TextDescriptiveHeirarchy)descrHeirV.get(k)).getText().getText()+
										" "+descrHeirV.get(k).getTypeElement().getName());
							}else{
								System.out.println("Unknown");
							}
						}
					}finally{
						try{fileReader.close();}catch(Exception e){/*ignore*/}
					}
				}
			}
		}
		String[] uniqueURIArr = namespaceURITreeSet.toArray(new String[0]);
		Arrays.sort(uniqueURIArr);
		for (int k = 0; k < uniqueURIArr.length; k++) {
			System.out.println("uri= "+uniqueURIArr[k]);
			Vector<String> typeV = namespaceURIToType.get(uniqueURIArr[k]);
			if(typeV != null){
				for (int i = 0; i < typeV.size(); i++) {
					System.out.println("     "+typeV.get(i));
				}
			}
		}

	}
	private static void recurseForAnnotation(Element element,Vector<DescriptiveHeirarchy> descrHeirV){
		Element annotationElment = recurseForElement(element, XMLTags.SbmlAnnotationTag);
		if(annotationElment != null){
			traverse(descrHeirV, element);
		}
//		if(element.getName().equals(XMLTags.SbmlAnnotationTag)){
//			traverse(descrHeirV, element);
//			return;
//		}
//		List children = element.getChildren();
//		for (int i = 0; i < children.size(); i++) {
//			if(children.get(i) instanceof Element){
//				recurseForAnnotation((Element)children.get(i),descrHeirV);
//			}
//		}
	}
	
	public static Element recurseForElement(Element element,String elementTagName){
		if(element.getName().equals(elementTagName)){
			return element;
		}
		List children = element.getChildren();
		for (int i = 0; i < children.size(); i++) {
			if(children.get(i) instanceof Element){
				Element foundElement =
					recurseForElement((Element)children.get(i),elementTagName);
				if(foundElement != null){
					return foundElement;
				}
			}
		}
		return null;
	}
	public static void setFromSBMLAnnotation(MIRIAMAnnotatable miriamAnnotatable,Element annotationElement){
		try {
			if(annotationElement == null){
				return;
			}
				Namespace ns = annotationElement.getNamespace();
				if(!annotationElement.getName().equalsIgnoreCase(XMLTags.SbmlAnnotationTag)){
					annotationElement = annotationElement.getChild(XMLTags.SbmlAnnotationTag, ns);
					if(annotationElement == null){
						return;
					}
				}
				MIRIAMAnnotation miriamAnnotation = miriamAnnotatable.getMIRIAMAnnotation();
				if(miriamAnnotatable.getMIRIAMAnnotation() == null){
					miriamAnnotation = new MIRIAMAnnotation();
					miriamAnnotatable.setMIRIAMAnnotation(miriamAnnotation);
				}
				miriamAnnotation.setAnnotation((Element)annotationElement.clone());
				//----------------------------------
				Element vcInfoElement = recurseForElement(annotationElement, XMLTags.VCellInfoTag);
				if(vcInfoElement != null){
//							System.out.println(XmlUtil.xmlToString(vcInfoElement));
				}
				//----------------------------------
		} catch (Exception e) {
			e.printStackTrace();
		}
}

public static void setFromSBMLNotes(MIRIAMAnnotatable miriamAnnotatable,Element notesElement){
	try {
		if(notesElement == null){
			return;
		}
		Namespace ns = notesElement.getNamespace();
		if(!notesElement.getName().equalsIgnoreCase(XMLTags.SbmlNotesTag)){
			notesElement = notesElement.getChild(XMLTags.SbmlNotesTag, ns);
			if(notesElement == null){
				return;
			}
		}
		MIRIAMAnnotation miriamAnnotation = miriamAnnotatable.getMIRIAMAnnotation();
		if(miriamAnnotatable.getMIRIAMAnnotation() == null){
			miriamAnnotation = new MIRIAMAnnotation();
			miriamAnnotatable.setMIRIAMAnnotation(miriamAnnotation);
		}
		Element newNotesElement = null;
		if(recurseForElement(notesElement, HTML.Tag.HTML.toString()) != null) {
			newNotesElement = (Element)notesElement.clone();
		} else {
			newNotesElement = new Element(XMLTags.SbmlNotesTag);
			newNotesElement.addContent((Element)extractHTMLFromElement(notesElement).clone());
		}
		miriamAnnotation.setUserNotes((Element)newNotesElement.clone());
	} catch (Exception e) {
		e.printStackTrace();
	}
}
	
//	public static MIRIAMAnnotation getMIRIAMAnnotation(String annotationStr){
//		Element annotationElement = cbit.util.xml.XmlUtil.stringToXML(annotationStr, null);
//		Element rdfElement = annotationElement.getChild(XMLTags.RDF_TAG,rdfNameSpace);
//		if(rdfElement == null){
//			return null;
//		}
//		Element descrElement = rdfElement.getChild(XMLTags.RDF_DESCR_TAG,rdfNameSpace);
//		List<Element> elementList = descrElement.getChildren();
//		for (int i = 0; i < elementList.size(); i++) {
//			Element descrItemElement = elementList.get(i);
//			if(descrItemElement.getNamespacePrefix().equals(XMLTags.BMBIOQUAL_NAMESPACE_PREFIX) ||
//				descrItemElement.getNamespacePrefix().equals(XMLTags.BMMODELQUAL_NAMESPACE_PREFIX)){
//				
//				List<Element> uriList = ((Element)descrItemElement.getChildren().get(0)).getChildren();
//				for (int j = 0; j < uriList.size(); j++) {
//					Element uriElement = (Element)uriList.get(j);
//					String uri = uriElement.getAttributeValue("resource",rdfNameSpace);
//					System.out.println(uri);
//				}
//			}
//		}
//		return null;
//	}
//	
//	public void addAnnotation(MIRIAMAnnotatable miriamAnnotatable,String annotationStr){
//		if(annotationStr == null || annotationStr.length() == 0){
//			return;
//		}
//		getMIRIAMAnnotation(annotationStr);
//	}
	public static void setFromSBMLAnnotation(MIRIAMAnnotatable miriamAnnotatable,String annotationStr){
		try {
			if(annotationStr == null || annotationStr.length() == 0){
				return;
			}
			Element annotationElement = cbit.util.xml.XmlUtil.stringToXML(annotationStr, null);
			setFromSBMLAnnotation(miriamAnnotatable, annotationElement);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void setFromSBMLNotes(MIRIAMAnnotatable miriamAnnotatable,String notesStr){
		try {
			if(notesStr == null || notesStr.length() == 0){
				return;
			}
			Element notesElement = cbit.util.xml.XmlUtil.stringToXML(notesStr, null);
			setFromSBMLNotes(miriamAnnotatable, notesElement);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showMIRIAMAnnotationDialog(MIRIAMAnnotatable miriamAnnotatable){
//		final cbit.vcell.xml.MIRIAMAnnotationPanel miriamAnnotationPanel =
//			new cbit.vcell.xml.MIRIAMAnnotationPanel();
//		miriamAnnotationPanel.setMIRIAMAnnotation(miriamAnnotatable.getMIRIAMAnnotation());
//		final javax.swing.JDialog jd = new javax.swing.JDialog();
//		jd.setTitle("View/Add/Delete/Edit MIRIAM Annotation");
//		jd.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
//		jd.setModal(true);
//		jd.getContentPane().add(miriamAnnotationPanel);
//		jd.setSize(700,400);
//		ZEnforcer.showModalDialogOnTop(jd);
//		miriamAnnotatable.setMIRIAMAnnotation(miriamAnnotationPanel.getMIRIAMAnnotation());
		
	}
	public static TreeMap<MIRIAMAnnotatable, Vector<DescriptiveHeirarchy>> showList(BioModel bioModel){
		TreeMap<MIRIAMAnnotatable, Vector<DescriptiveHeirarchy>> mirimaDescrHeir =
			new TreeMap<MIRIAMAnnotatable, Vector<DescriptiveHeirarchy>>(
					new Comparator<MIRIAMAnnotatable>(){
						public int compare(MIRIAMAnnotatable o1, MIRIAMAnnotatable o2) {
							int nameComp =
							(
								(o1 instanceof BioModel?((BioModel)o1).getName():"")+
								(o1 instanceof Species?((Species)o1).getCommonName():"")+
								(o1 instanceof Structure?((Structure)o1).getName():"")+
								(o1 instanceof ReactionStep?((ReactionStep)o1).getName():"")
							).compareToIgnoreCase(
								(o2 instanceof BioModel?((BioModel)o2).getName():"")+
								(o2 instanceof Species?((Species)o2).getCommonName():"")+
								(o2 instanceof Structure?((Structure)o2).getName():"")+
								(o2 instanceof ReactionStep?((ReactionStep)o2).getName():"")
							);
							int typecomp =
								((o1 instanceof BioModel?0:0)+
								(o1 instanceof Structure?1:0)+
								(o1 instanceof ReactionStep?2:0)+
								(o1 instanceof Species?3:0))
								-
								((o2 instanceof BioModel?0:0)+
								(o2 instanceof Structure?1:0)+
								(o2 instanceof ReactionStep?2:0)+
								(o2 instanceof Species?3:0));
							
							return (typecomp == 0?nameComp:typecomp);
						}}
			);
		if(bioModel.getMIRIAMAnnotation() != null){
			mirimaDescrHeir.put(bioModel,traverse(new Vector<DescriptiveHeirarchy>(),bioModel.getMIRIAMAnnotation().getAnnotation()));
		}else{
			mirimaDescrHeir.put(bioModel,new Vector<DescriptiveHeirarchy>());
		}
		
		{
			Species[] speciesArr = bioModel.getModel().getSpecies();
			for (int i = 0; i < speciesArr.length; i++) {
				if(speciesArr[i].getMIRIAMAnnotation() != null){
					mirimaDescrHeir.put(
							speciesArr[i],
							traverse(
									new Vector<DescriptiveHeirarchy>(),
									speciesArr[i].getMIRIAMAnnotation().getAnnotation()));
				}else{
					mirimaDescrHeir.put(speciesArr[i],new Vector<DescriptiveHeirarchy>());
				}
			}
		}
		{
			Structure[] structArr = bioModel.getModel().getStructures();
			for (int i = 0; i < structArr.length; i++) {
				if(structArr[i].getMIRIAMAnnotation() != null){
					mirimaDescrHeir.put(structArr[i],traverse(
							new Vector<DescriptiveHeirarchy>(),structArr[i].getMIRIAMAnnotation().getAnnotation()));
				}else{
					mirimaDescrHeir.put(structArr[i],new Vector<DescriptiveHeirarchy>());
				}
			}
		}
		{
			ReactionStep[] reactArr = bioModel.getModel().getReactionSteps();
			for (int i = 0; i < reactArr.length; i++) {
				if(reactArr[i].getMIRIAMAnnotation() != null){
					mirimaDescrHeir.put(reactArr[i],traverse(
							new Vector<DescriptiveHeirarchy>(),reactArr[i].getMIRIAMAnnotation().getAnnotation()));
				}else{
					mirimaDescrHeir.put(reactArr[i],new Vector<DescriptiveHeirarchy>());
				}
			}
		}			
		return mirimaDescrHeir;
	}
	private static Vector<DescriptiveHeirarchy> traverse(Vector<DescriptiveHeirarchy> descrHeirV,Object content){
		if(content instanceof Text){
			TextDescriptiveHeirarchy textDescrHeir = null;
			if(((Text)content).getText() != null && ((Text)content).getText().trim().length() > 0){//!((Text)content).getText().equals("\n")){
				textDescrHeir = new TextDescriptiveHeirarchy((Text)content);
				getDescriptiveHeirarchy(textDescrHeir,((Text)content).getParent());
				if(textDescrHeir.getHeirarchy().size() > 0){
					descrHeirV.add(textDescrHeir);
				}
			}
			return descrHeirV;
		}
		if(content instanceof Element){
			List children = ((Element)content).getContent();
			if(children != null && children.size() > 0){
				for(int i=0;i<children.size();i+= 1){
					traverse(descrHeirV,children.get(i));
				}
				return descrHeirV;
			}
//			if(isDescriptiveScheme(((Element)content).getNamespace())){
				List<Attribute> attributes = ((Element)content).getAttributes();
				if(attributes != null && attributes.size() > 0){
					for(int i=0;i<attributes.size();i+= 1){
						AttributeDescriptiveHeirarchy attrDescrHeir =
							new AttributeDescriptiveHeirarchy(attributes.get(i));
						getDescriptiveHeirarchy(attrDescrHeir,attributes.get(i).getParent());
						if(attrDescrHeir.getHeirarchy().size()>0){
							descrHeirV.add(attrDescrHeir);
						}
					}
					return descrHeirV;
				}
//			}
		}
		return descrHeirV;
	}
//	private static void traverse(Vector<String> miriamList,Object content){
//		if(content instanceof Text){
//			if(((Text)content).getText() != null && !((Text)content).getText().equals("\n")){
//				miriamList.add(getDescriptiveHeirarchy(((Text)content).getText(), ((Text)content).getParent()));
//			}
//			return;
//		}
//		if(content instanceof Element){
//			List children = ((Element)content).getContent();
//			if(children != null && children.size() > 0){
//				for(int i=0;i<children.size();i+= 1){
//					traverse(miriamList, children.get(i));
//				}
//				return;
//			}
//			List<Attribute> attributes = ((Element)content).getAttributes();
//			if(attributes != null && attributes.size() > 0){
//				for(int i=0;i<attributes.size();i+= 1){
//					miriamList.add(getDescriptiveHeirarchy(attributes.get(i).getValue(), attributes.get(i).getParent()));
//				}
//			}
//		}
//	}
}
