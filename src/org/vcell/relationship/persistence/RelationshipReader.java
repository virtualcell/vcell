/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
					try{
						relationshipModel.addRelationshipObject(addObjectRelationshipObject(childElement, provider));
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					showUnexpected(childElement);
				}
			}
		}
		return relationshipModel;
	}

	private RelationshipObject addObjectRelationshipObject(Element element, IdentifiableProvider provider) throws Exception{
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
		return RelationshipObject.createRelationshipObject(bioModelObjectID, bioPaxObjectID, provider);
	}

}
