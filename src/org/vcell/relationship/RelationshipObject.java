package org.vcell.relationship;


import org.vcell.pathway.BioPaxObject;

import cbit.vcell.biomodel.meta.Identifiable;
import cbit.vcell.model.Species;

public class RelationshipObject implements Identifiable{
	private BioPaxObject biopaxObject = null;
	private Species species = null;
	
	public BioPaxObject getBioPaxObject(){
		return biopaxObject;
	}
	
	public void setBioPaxObjecte(BioPaxObject biopaxObject){
		this.biopaxObject = biopaxObject;
	}
	
	public Species getSpecies(){
		return species;
	}
	
	public void setSpecies(Species species){
		this.species = species;
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
			return biopaxObject.equals(reObject.biopaxObject) && species.equals(reObject.species);
		}
		return false;
	}

	public int hashCode() {
		return biopaxObject.hashCode() + species.hashCode();
	}
}
