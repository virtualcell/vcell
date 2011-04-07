package org.vcell.relationship;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.vcell.pathway.BioPaxObject;


import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.model.BioModelEntityObject;


public class RelationshipModel {
	private HashSet<RelationshipObject> relationshipObjects = new HashSet<RelationshipObject>();
	protected transient ArrayList<RelationshipListener> aRelationshipListeners = new ArrayList<RelationshipListener>();

	public void setRelationshipObjects(HashSet<RelationshipObject> relationshipObjects){
		this.relationshipObjects = relationshipObjects;
	}
	public Set<RelationshipObject> getRelationshipObjects(){
		return relationshipObjects;
	}
	
	public void merge(RelationshipModel relationshipModel) {
		relationshipObjects.addAll(relationshipModel.relationshipObjects);
		fireRelationshipChanged(new RelationshipEvent(this, null, RelationshipEvent.CHANGED));
	}
	
	public boolean compare(HashSet<RelationshipObject> theirRelationshipObjects, IdentifiableProvider provider) {

		if(relationshipObjects.size() != theirRelationshipObjects.size()) {
			return false;			// different number of objects
		}
		
		for (RelationshipObject bpObject : relationshipObjects){
			if(!bpObject.compare(theirRelationshipObjects, provider)) {
				return false;
			}
		}
		return true;
	}
	
	private ArrayList<RelationshipListener> getRelationshipListeners(){
		if (aRelationshipListeners == null) {
			aRelationshipListeners = new ArrayList<RelationshipListener>();
		}
		return aRelationshipListeners;
	}
	protected void fireRelationshipChanged(RelationshipEvent event) {
		for (RelationshipListener l : getRelationshipListeners()){
			l.relationshipChanged(event);
		}
	}

	public void addRelationShipListener(RelationshipListener listener) {
		getRelationshipListeners().add(listener);
	}

	public void removeRelationShipListener(RelationshipListener listener) {
		getRelationshipListeners().remove(listener);
	}

	private boolean contains(RelationshipObject relationshipObject) {
		for (RelationshipObject ro : relationshipObjects) {
			if (ro.getBioModelEntityObject() == relationshipObject.getBioModelEntityObject()
					&& ro.getBioPaxObject() == relationshipObject.getBioPaxObject()) {
				return true;
			}
		}
		return false;
	}
	public RelationshipObject addRelationshipObject(RelationshipObject relationshipObject){
		if (relationshipObject==null){
			throw new RuntimeException("added a null object to relationship model");
		}
		if(!contains(relationshipObject)){
			relationshipObjects.add(relationshipObject);
			fireRelationshipChanged(new RelationshipEvent(this,relationshipObject, RelationshipEvent.CHANGED));
		}
		return relationshipObject;
	}
	
	public boolean removeRelationshipObject(RelationshipObject relationshipObject){ // basic remove function
		if (relationshipObject==null){
			throw new RuntimeException("remove a null object to relationship model");
		}
		boolean bContained = relationshipObjects.remove(relationshipObject);
		if (bContained) {
			fireRelationshipChanged(new RelationshipEvent(this,relationshipObject, RelationshipEvent.CHANGED));	
		}
		return bContained;
	}
	
	public boolean removeRelationshipObjects(List<BioPaxObject> bioPaxObjects){ // deletebuttonPressed() in BioModelEditorPathwayDiagramPanel
		if (bioPaxObjects==null){
			throw new RuntimeException("remove a null object from relationship model");
		}
		boolean bRemoved = false;
		for(BioPaxObject bpObject : bioPaxObjects){
			Iterator<RelationshipObject> iter = relationshipObjects.iterator();
			while (iter.hasNext()){
				RelationshipObject relationshipObject = iter.next();
				if(relationshipObject.getBioPaxObject() == bpObject) { 
					iter.remove();
					bRemoved = true;
				}
			}
		}
		if (bRemoved) {
			fireRelationshipChanged(new RelationshipEvent(this, null, RelationshipEvent.CHANGED));	
		}
		return bRemoved;
	}
	
	public boolean removeRelationshipObjects(Set<BioModelEntityObject> biomodelEntityObjects){ // propertyChange() in bioModel
		if (biomodelEntityObjects==null){
			throw new RuntimeException("remove a null object from relationship model");
		}
		boolean bRemoved = false;
		for(BioModelEntityObject bmObject : biomodelEntityObjects){
			Iterator<RelationshipObject> iter = relationshipObjects.iterator();
			while (iter.hasNext()){
				RelationshipObject relationshipObject = iter.next();
				if(relationshipObject.getBioModelEntityObject() == bmObject) { 
					iter.remove();
					bRemoved = true;
				}
			}
		}
		if (bRemoved) {
			fireRelationshipChanged(new RelationshipEvent(this, null, RelationshipEvent.CHANGED));	
		}
		return bRemoved;
	}
	
	public void addRelationshipObjects(HashSet<RelationshipObject> reObjects){  // unused function
		if (reObjects==null){
			throw new RuntimeException("added a null object to relationship model");
		}
		for(RelationshipObject reObject : reObjects)
			relationshipObjects.add(reObject);
		fireRelationshipChanged(new RelationshipEvent(this,null, RelationshipEvent.CHANGED));
		return;
	}

	public HashSet<RelationshipObject> getRelationshipObjects(BioModelEntityObject bioModelObject){
		HashSet<RelationshipObject> associatedReObjects = new HashSet<RelationshipObject>();
		for(RelationshipObject reObject : relationshipObjects){
			if(reObject.getBioModelEntityObject() == bioModelObject)
				associatedReObjects.add(reObject);
		}
		return associatedReObjects;
	}
	
	public HashSet<RelationshipObject> getRelationshipObjects(BioPaxObject bioPaxObject){
		HashSet<RelationshipObject> associatedReObjects = new HashSet<RelationshipObject>();
		for(RelationshipObject reObject : relationshipObjects){
			if(reObject.getBioPaxObject() == bioPaxObject)
				associatedReObjects.add(reObject);
		}
		return associatedReObjects;
	}
	
	public HashSet<BioModelEntityObject> getBioModelEntityObjects(){ // propertyChange() in bioModel
		HashSet<BioModelEntityObject> bioModelEntityReObjects = new HashSet<BioModelEntityObject>();
		for(RelationshipObject reObject : relationshipObjects){
			bioModelEntityReObjects.add(reObject.getBioModelEntityObject());
		}
		return bioModelEntityReObjects;
	} 
	
	public HashSet<BioPaxObject> getBioPaxObjects(){ // unused function
		HashSet<BioPaxObject> bioPaxObject = new HashSet<BioPaxObject>();
		for(RelationshipObject reObject : relationshipObjects){
			bioPaxObject.add(reObject.getBioPaxObject());
		}
		return bioPaxObject;
	} 
	
}
