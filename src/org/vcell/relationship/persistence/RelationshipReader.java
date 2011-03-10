package org.vcell.relationship.persistence;

import org.jdom.Attribute;
import org.jdom.Element;

import org.vcell.relationship.RelationshipModel;
import org.vcell.relationship.RelationshipObject;
import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCID.InvalidVCIDException;
import cbit.vcell.xml.XMLTags;
import static org.vcell.pathway.PathwayXMLHelper.*;

public class RelationshipReader {

	private RelationshipModel relationshipModel = new RelationshipModel();
		
	public RelationshipModel parse(Element rootElement, IdentifiableProvider provider) {
		
		for (Object child : rootElement.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (childElement.getName().equals(XMLTags.relationshipObjectTag)){
					relationshipModel.addRelationshipObject(addObjectRelationshipObject(childElement, provider));
				}else{
					showUnexpected(childElement);
				}
			}
		}
		return relationshipModel;
	}

	private RelationshipObject addObjectRelationshipObject(Element element, IdentifiableProvider provider) {
		try {
			VCID bioPaxObjectID = null;
			VCID bioModelObjectID = null;
			for (Object attr : element.getAttributes()){
				Attribute attribute = (Attribute)attr;
				if (attribute.getName().equals(XMLTags.bioPaxObjectIdTag)){
					bioPaxObjectID = VCID.fromString(attribute.getValue());
				} else if (attribute.getName().equals(XMLTags.bioModelObjectIdTag)){
					bioModelObjectID = VCID.fromString(attribute.getValue());
				}
			}
			RelationshipObject ro = RelationshipObject.createRelationshipObject(bioModelObjectID, bioPaxObjectID, provider);
			return ro;
		} catch (InvalidVCIDException e) {
			e.printStackTrace();
		}
		return null;
	}

}
