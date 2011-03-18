package org.vcell.relationship;


import java.util.HashSet;

import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.VCID;

import org.vcell.pathway.BioPaxObject;
import cbit.vcell.model.BioModelEntityObject;

public class RelationshipObject implements Identifiable{
	private BioPaxObject biopaxObject = null;
	private BioModelEntityObject bioModelObject = null;	
	
	public RelationshipObject(BioModelEntityObject bioModelObject, BioPaxObject biopaxObject) {
		super();
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
		return new RelationshipObject((BioModelEntityObject)provider.getIdentifiableObject(bioModelObjectID), 
										(BioPaxObject)provider.getIdentifiableObject(bioPaxObjectID));
	}
}
