package org.vcell.relationship.persistence;

import java.util.HashSet;
import org.jdom.Element;

import org.vcell.relationship.RelationshipModel;
import org.vcell.relationship.RelationshipObject;
import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.xml.XMLTags;
import static org.vcell.pathway.PathwayXMLHelper.*;

public class RelationshipProducer {

	public Element relationshipElement = null;
	private HashSet<RelationshipObject> relationshipObjects = null;

	public RelationshipProducer() {
	}
	
	public void getXML(RelationshipModel relationshipModel, Element rootElement, IdentifiableProvider provider) {

		relationshipElement = rootElement;
		relationshipObjects = (HashSet<RelationshipObject>)(relationshipModel.getRelationshipObjects());

		for (RelationshipObject relationshipObject : relationshipObjects){
			if(relationshipObject == null) {
				System.out.println("null object!");
				break;
			}
			
			String className = relationshipObject.getClass().getName().substring(1+relationshipObject.getClass().getName().lastIndexOf('.'));
			if (className.equals(XMLTags.relationshipObjectTag)){
				relationshipElement.addContent(addObjectRelationshipObject(relationshipObject, className, provider));
			}else{
				showUnexpected(relationshipObject);
			}
		}
		return;
	}

	private Element addObjectRelationshipObject(RelationshipObject relationshipObject, String className, IdentifiableProvider provider) {
		Element element = new Element(className);
		element = addAttributes(relationshipObject, element, provider);
		return element;
	}

	private Element addAttributes(RelationshipObject relationshipObject, Element element, IdentifiableProvider provider) {
		String attr = null;
		if(relationshipObject.getBioPaxObject().getID() != null) {
			attr = provider.getVCID(relationshipObject.getBioPaxObject()).toASCIIString();
			element.setAttribute(XMLTags.bioPaxObjectIdTag, attr);
		}
		if(relationshipObject.getBioModelEntityObject().toString() != null) {
			attr = provider.getVCID(relationshipObject.getBioModelEntityObject()).toASCIIString();
			element.setAttribute(XMLTags.bioModelObjectIdTag, attr);
		}
		return element;
	}
	

	
}
