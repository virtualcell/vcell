package org.vcell.pathway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;
import cbit.vcell.biomodel.meta.Identifiable;

public class PathwayModel {
	private HashSet<BioPaxObject> biopaxObjects = new HashSet<BioPaxObject>();
	private HashSet<String> diagramObjectsID = new HashSet<String>();
	protected transient ArrayList<PathwayListener> aPathwayListeners = new ArrayList<PathwayListener>();

	protected Map<BioPaxObject, HashSet<BioPaxObject>> parentMap = 
		new Hashtable<BioPaxObject, HashSet<BioPaxObject>>();
	
	public Set<BioPaxObject> getBiopaxObjects(){
		return biopaxObjects;
	}
//	public Set<BioPaxObject> getBiopaxObjects(){
//		return Collections.unmodifiableSet(biopaxObjects);
//	}

	public Set<String> getDiagramObjects(){
		return diagramObjectsID;
	}

	public void populateDiagramObjects() {
		diagramObjectsID.clear();
		for (BioPaxObject bpObject : biopaxObjects){
			diagramObjectsID.add(bpObject.getID());
		}
	}

	public void filterDiagramObjects() {
		if(diagramObjectsID.size() == 0) {
			return;
		}
		HashSet<BioPaxObject> newBiopaxObjects = new HashSet<BioPaxObject>();
		for (BioPaxObject bpObject : biopaxObjects){
			if(diagramObjectsID.contains(bpObject.getID())) {
				newBiopaxObjects.add(bpObject);
			}
		}
		biopaxObjects = newBiopaxObjects;
	}
	
	public String show(boolean bIncludeChildren) {
		StringBuffer stringBuffer = new StringBuffer();
		for (BioPaxObject bpObject : biopaxObjects){
			if (bIncludeChildren){
				bpObject.show(stringBuffer);
			}else{
				stringBuffer.append(bpObject.toString()+"\n");
			}
		}
		return stringBuffer.toString();
	}
	
	public BioPaxObject find(BioPaxObject theirBpObject) {

		for (BioPaxObject ourBpObject : biopaxObjects){
			if(ourBpObject != null && ourBpObject.getID().equals(theirBpObject.getID())) {
				return ourBpObject;
			}
		}
		return null;
	}
	
	public boolean compare(HashSet<BioPaxObject> theirBiopaxObjects) {

		if(biopaxObjects.size() != theirBiopaxObjects.size()) {
			return false;			// different number of objects
		}
		
		for (BioPaxObject ourBpObject : biopaxObjects){
			if(!fastCompareIDs(ourBpObject, theirBiopaxObjects)) {
				return false;		// one object was replaced with some other object
			}
		}
		
		for (BioPaxObject bpObject : biopaxObjects){
			if(!bpObject.fullCompare(theirBiopaxObjects)) {
				return false;
			}
		}
		return true;
	}
	private boolean fastCompareIDs(BioPaxObject ourBpObject, HashSet<BioPaxObject> theirBiopaxObjects) {
		for (BioPaxObject theirBpObject : theirBiopaxObjects){
			if(ourBpObject.getID().equals(theirBpObject.getID())) {
				return true;
			}
		}
		return false;
	}

	public void merge(PathwayModel pathwayModel) {
		diagramObjectsID.addAll(pathwayModel.diagramObjectsID);
		biopaxObjects.addAll(pathwayModel.biopaxObjects);
		firePathwayChanged(new PathwayEvent(this, PathwayEvent.CHANGED));
	}
	
	private ArrayList<PathwayListener> getPathwayListeners(){
		if (aPathwayListeners == null) {
			aPathwayListeners = new ArrayList<PathwayListener>();
		}
		return aPathwayListeners;
	}
	protected void firePathwayChanged(PathwayEvent event) {
		for (PathwayListener l : getPathwayListeners()){
			l.pathwayChanged(event);
		}
	}

	public void addPathwayListener(PathwayListener listener) {
		getPathwayListeners().add(listener);
	}

	public void removePathwayListener(PathwayListener listener) {
		getPathwayListeners().remove(listener);
	}

	public BioPaxObject add(BioPaxObject bioPaxObject){
		if (bioPaxObject==null){
			throw new RuntimeException("added a null object to pathway model");
		}
		biopaxObjects.add(bioPaxObject);
//		System.err.println("add all BioPaxObject children of this object to pathwayModel");
		firePathwayChanged(new PathwayEvent(this,PathwayEvent.CHANGED));
		return bioPaxObject;
	}

	public Pathway getTopLevelPathway(){
		ArrayList<Pathway> allPathways = new ArrayList<Pathway>();
		for (BioPaxObject bpObject : biopaxObjects){
			if (bpObject instanceof Pathway){
				allPathways.add((Pathway)bpObject);
			}
		}
		ArrayList<Pathway> topLevelPathways = new ArrayList<Pathway>(allPathways);
		for (Pathway pathway : allPathways){
			for (Pathway childPathway : pathway.getPathwayComponentPathway()){
				topLevelPathways.remove(childPathway);
			}
		}
		if (topLevelPathways.size()>0){
			return topLevelPathways.get(0);
		}else{
			return null;
		}
	}

	public void reconcileReferences() {
		ArrayList<RdfObjectProxy> proxiesToDelete = new ArrayList<RdfObjectProxy>();
		for (BioPaxObject bpObject : biopaxObjects){
			if (bpObject instanceof RdfObjectProxy){
				RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)bpObject;
				if (rdfObjectProxy.getResource() != null){
					BioPaxObject concreteObject = findFromResourceID(rdfObjectProxy.getResource());
					if (concreteObject != null){
						//System.out.println("replacing "+rdfObjectProxy.toString()+" with "+concreteObject.toString());
						replace(rdfObjectProxy,concreteObject);
						proxiesToDelete.add(rdfObjectProxy);
					}else{
						System.out.println("unable to resolve reference to "+rdfObjectProxy.toString());
					}
				}else{
					System.out.println("rdfProxy had no resource set "+rdfObjectProxy.toString());
				}
			}
		}
		biopaxObjects.removeAll(proxiesToDelete);
		
		// get rid of all UtilityClass objects for display purposes
		// the producer will recreate them from memory at the time when we save
		HashSet<BioPaxObject> newBiopaxObjects = new HashSet<BioPaxObject>();
		for (BioPaxObject bpObject : biopaxObjects){
			if(!(bpObject instanceof UtilityClass)) {
				newBiopaxObjects.add(bpObject);
			}
		}
		biopaxObjects = newBiopaxObjects;
		
		// get rid of all unresolved proxies
		HashSet<BioPaxObject> new2BiopaxObjects = new HashSet<BioPaxObject>();
		int unresolvedProxiesCount = 0;
		for (BioPaxObject bpObject : biopaxObjects){
			if(!(bpObject instanceof RdfObjectProxy)) {
				new2BiopaxObjects.add(bpObject);
			} else {
				unresolvedProxiesCount++;
			}
		}
		System.out.println("Unresolved proxies: " + unresolvedProxiesCount);
		biopaxObjects = new2BiopaxObjects;
		
		firePathwayChanged(new PathwayEvent(this,PathwayEvent.CHANGED));
	}

	private void replace(RdfObjectProxy objectProxy, BioPaxObject concreteObject) {
		Iterator<BioPaxObject> bpObjectIter = biopaxObjects.iterator();
		while (bpObjectIter.hasNext()){
			BioPaxObject bpObject = bpObjectIter.next();
			if (bpObject != objectProxy){
				bpObject.replace(objectProxy,concreteObject);
			}
		}
	}

	private BioPaxObject findFromResourceID(String resource) {
		resource = resource.replace("#","");
		for (BioPaxObject bpObject : biopaxObjects){
			if (bpObject.getID()!=null){
				if (bpObject.getID().equals(resource)){
					return bpObject;
				}
//				// contains() won't work, may result in matching objects which are wildly incompatible
//				if (bpObject.getID().contains(resource)){
//					return bpObject;
//				}
			}
		}
		return null;
	}
	
	public Map<BioPaxObject, HashSet<BioPaxObject>> getParentHashtable(){
		return parentMap;
	}
	
	public void refreshParentMap(){
		parentMap =  new Hashtable<BioPaxObject, HashSet<BioPaxObject>>();
		for (BioPaxObject bpObject : biopaxObjects){
			// only build the parent hashtable for Entity
			if (bpObject instanceof PhysicalEntity){
				PhysicalEntity physicalEntity = (PhysicalEntity) bpObject;
				if(physicalEntity.getMemberPhysicalEntity() != null)
					addToParentMap(bpObject, physicalEntity.getMemberPhysicalEntity());
				if(bpObject instanceof Complex){
					Complex complex = (Complex) bpObject;
					if(complex.getComponents() != null)
						addToParentMap(bpObject, complex.getComponents());
				}
			}else if(bpObject instanceof InteractionImpl){
				List<InteractionParticipant> participants = ((Interaction) bpObject).getParticipants();
				if(participants != null) {
					Set<BioPaxObject> physicalEntities = new HashSet<BioPaxObject>();
					for(InteractionParticipant participant : participants) {
						physicalEntities.add(participant.getPhysicalEntity());
						addToParentMap(bpObject, participant.getPhysicalEntity());
					}
				}
				if(bpObject instanceof Control){// for Control
					ArrayList<Pathway> pathwayControllers = ((Control) bpObject).getPathwayControllers();
					if(pathwayControllers != null)
						addToParentMap(bpObject, new ArrayList<BioPaxObject>(pathwayControllers));
					ArrayList<BioPaxObject> controlled = new ArrayList<BioPaxObject>();
					Interaction controlledInteraction = ((Control) bpObject).getControlledInteraction();
					if(controlledInteraction != null){
						controlled.add((controlledInteraction));
					}
					if(((Control) bpObject).getControlledPathway() != null){
						controlled.add((((Control) bpObject).getControlledPathway()));
					}
					addToParentMap(bpObject, controlled);
					if(bpObject instanceof Catalysis){// for Catalysis
					}
				}else if(bpObject instanceof Conversion){// for Conversion
				}else if(bpObject instanceof TemplateReaction){// for TemplateReaction
					TemplateReaction templateReaction = (TemplateReaction) bpObject;
					if(templateReaction.getProductDna() != null){
						addToParentMap(bpObject, templateReaction.getProductDna());
					}
					if(templateReaction.getProductProtein() != null){
						addToParentMap(bpObject, templateReaction.getProductProtein());
					}
					if(templateReaction.getProductRna() != null){
						addToParentMap(bpObject, templateReaction.getProductRna());
					}
					ArrayList<BioPaxObject> template = new ArrayList<BioPaxObject>();
					if(templateReaction.getTemplateDna() != null){
						template.add(templateReaction.getTemplateDna());
					}
					if(templateReaction.getTemplateDnaRegion() != null){
						template.add(templateReaction.getTemplateDnaRegion());
					}
					if(templateReaction.getTemplateRna() != null){
						template.add(templateReaction.getTemplateRna());
					}
					if(templateReaction.getTemplateRnaRegion() != null){
						template.add(templateReaction.getTemplateRnaRegion());
					}
					addToParentMap(bpObject, template);
				}
			}else if(bpObject instanceof Pathway){// for Pathway
				Pathway pathway = (Pathway) bpObject;
				if(pathway.getPathwayComponentInteraction() != null){
					addToParentMap(bpObject, pathway.getPathwayComponentInteraction());
				}
				if(pathway.getPathwayComponentPathway() != null){
					addToParentMap(bpObject, pathway.getPathwayComponentPathway());
				}
			}else{ // for Gene
			}
		}		
		// print out the hashtable
		/*
		System.out.println("ParentHashtable size is "+ parentMap.size());
		for(BioPaxObject bp : parentMap.keySet()){
			System.out.println(bp);
			for (BioPaxObject vbp : parentMap.get(bp)){
				System.out.println("=> "+ vbp);
			}
		}
		*/
	}

	private void addToParentMap(BioPaxObject parent, Collection<? extends BioPaxObject> children){
		if(children.size() != 0){
			for(BioPaxObject bpObject : children){
				addToParentMap(parent, bpObject);
			}
		}
	}

	private void addToParentMap(BioPaxObject parent, BioPaxObject bpObject) {
		HashSet<BioPaxObject> childrenSet = parentMap.get(bpObject);
		if (childrenSet == null){
			childrenSet = new HashSet<BioPaxObject>();
			parentMap.put(bpObject, childrenSet);
		}
		childrenSet.add(parent);
	}

	// get parents list for one Biopax object
	public ArrayList<BioPaxObject> getParents(BioPaxObject bpObject){
		ArrayList<BioPaxObject> parentObjects = new ArrayList<BioPaxObject>();
		HashSet<BioPaxObject> parentSet = parentMap.get(bpObject);
		if(parentSet == null){
			return null;
		}else{
			for(BioPaxObject bp : parentSet){
				parentObjects.add(bp);
			}
		}
		return parentObjects;
	}
	
	public int size(){
		return biopaxObjects.size();
	}

	public Identifiable findBioPaxObject(String rdfId) {
		return findFromResourceID(rdfId);
	}

	public BioPaxObject remove(BioPaxObject bioPaxObject) {
		if (bioPaxObject==null){
			throw new RuntimeException("added a null object to pathway model");
		}
		biopaxObjects.remove(bioPaxObject);
		firePathwayChanged(new PathwayEvent(this,PathwayEvent.CHANGED));
		return bioPaxObject;		
	}
	public void remove(List<BioPaxObject> bioPaxObjects) {
		if (bioPaxObjects==null){
			throw new RuntimeException("added a null object to pathway model");
		}
		biopaxObjects.removeAll(bioPaxObjects);
		firePathwayChanged(new PathwayEvent(this,PathwayEvent.CHANGED));
	}


}
