/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.relationship;


import java.util.HashSet;

import org.vcell.pathway.BioPaxObject;
import org.vcell.util.document.Identifiable;

import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.model.BioModelEntityObject;

public class RelationshipObject implements Identifiable{
	private BioPaxObject biopaxObject = null;
	private BioModelEntityObject bioModelObject = null;	
	
	public RelationshipObject(BioModelEntityObject bioModelObject, BioPaxObject biopaxObject) {
		super();
		if(bioModelObject == null || biopaxObject == null){
			throw new IllegalArgumentException("Relationship objects cannot be null, BioModelEntityObject="+bioModelObject+" BioPaxObject"+biopaxObject);
		}
		this.bioModelObject = bioModelObject;
		this.biopaxObject = biopaxObject;
	}

	public BioPaxObject getBioPaxObject(){
		return biopaxObject;
	}
	
	public BioModelEntityObject getBioModelEntityObject(){
		return bioModelObject;
	}
	
	public final boolean compare(HashSet<RelationshipObject> theirRelationshipObjects, IdentifiableProvider provider){
		for (RelationshipObject theirRelationshipObject : theirRelationshipObjects){
			VCID vcidBpOurs = provider.getVCID(getBioPaxObject());
			VCID vcidBpTheirs = provider.getVCID(theirRelationshipObject.getBioPaxObject());
			VCID vcidBmOurs = provider.getVCID(getBioModelEntityObject());
			VCID vcidBmTheirs = provider.getVCID(theirRelationshipObject.getBioModelEntityObject());
			if(vcidBpOurs.equals(vcidBpTheirs) && vcidBmOurs.equals(vcidBmTheirs)) {
				return true;
			}
		}
		return false;
	}
	public String getTypeLabel(){
		String typeName = getClass().getName();
		typeName = typeName.replace(getClass().getPackage().getName(),"");
		typeName = typeName.replace(".","");
		return typeName;
	}
	
	public static RelationshipObject createRelationshipObject(VCID bioModelObjectID, VCID bioPaxObjectID, IdentifiableProvider provider){
		if(bioModelObjectID == null || bioPaxObjectID == null) {
			return null;
		}
		BioModelEntityObject bmo = (BioModelEntityObject)provider.getIdentifiableObject(bioModelObjectID);
		BioPaxObject bpo = (BioPaxObject)provider.getIdentifiableObject(bioPaxObjectID);
		return new RelationshipObject(bmo, bpo);
	}
}
