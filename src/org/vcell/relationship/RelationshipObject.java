package org.vcell.relationship;


import cbit.vcell.biomodel.meta.Identifiable;

import org.vcell.pathway.BioPaxObject;
import cbit.vcell.model.BioModelEntityObject;

public class RelationshipObject implements Identifiable{
	private BioPaxObject biopaxObject = null;
	private BioModelEntityObject bioModelObject = null;	
	
	public RelationshipObject(BioModelEntityObject bioModelObject,
			BioPaxObject biopaxObject) {
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
	
	public String getTypeLabel(){
		String typeName = getClass().getName();
		typeName = typeName.replace(getClass().getPackage().getName(),"");
		typeName = typeName.replace(".","");
		return typeName;
	}

	public boolean equals(Object object) {
		if(object instanceof RelationshipObject) {
			RelationshipObject reObject = (RelationshipObject) object;
			return biopaxObject.equals(reObject.biopaxObject) && bioModelObject.equals(reObject.bioModelObject);
		}
		return false;
	}

	public int hashCode() {
		return biopaxObject.hashCode() + bioModelObject.hashCode();
	}
}
