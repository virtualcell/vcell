package org.vcell.pathway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

public class PathwayModel {
	private HashSet<BioPaxObject> biopaxObjects = new HashSet<BioPaxObject>();
	protected transient ArrayList<PathwayListener> aPathwayListeners = new ArrayList<PathwayListener>();

	protected Map<BioPaxObject, HashSet<BioPaxObject>> parentMap = 
		new Hashtable<BioPaxObject, HashSet<BioPaxObject>>();
	
	public Set<BioPaxObject> getBiopaxObjects(){
		return biopaxObjects;
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

	public void merge(PathwayModel pathwayModel) {
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
		getBiopaxObjects().add(bioPaxObject);
//		System.err.println("add all BioPaxObject children of this object to pathwayModel");
		firePathwayChanged(new PathwayEvent(this,PathwayEvent.CHANGED));
		return bioPaxObject;
	}

	public BioPaxObject getObjectByResource(String value) {
		// TODO Auto-generated method stub
		return null;
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
				if (bpObject.getID().contains(resource)){
					return bpObject;
				}
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
					if(controlledInteraction != null)
						controlled.add(((BioPaxObject)controlledInteraction));
					if(((Control) bpObject).getControlledPathway() != null)
						controlled.add(((BioPaxObject)((Control) bpObject).getControlledPathway()));
					addToParentMap(bpObject, controlled);
					if(bpObject instanceof Catalysis){// for Catalysis
					}
				}else if(bpObject instanceof Conversion){// for Conversion
				}else if(bpObject instanceof TemplateReaction){// for TemplateReaction
					TemplateReaction templateReaction = (TemplateReaction) bpObject;
					if(templateReaction.getProductDna() != null)
						addToParentMap(bpObject, templateReaction.getProductDna());
					if(templateReaction.getProductProtein() != null)
						addToParentMap(bpObject, templateReaction.getProductProtein());
					if(templateReaction.getProductRna() != null)
						addToParentMap(bpObject, templateReaction.getProductRna());
					ArrayList<BioPaxObject> template = new ArrayList<BioPaxObject>();
					if(templateReaction.getTemplateDna() != null)
						template.add(templateReaction.getTemplateDna());
					if(templateReaction.getTemplateDnaRegion() != null)
						template.add(templateReaction.getTemplateDnaRegion());
					if(templateReaction.getTemplateRna() != null)
						template.add(templateReaction.getTemplateRna());
					if(templateReaction.getTemplateRnaRegion() != null)
						template.add(templateReaction.getTemplateRnaRegion());
					addToParentMap(bpObject, template);
				}
			}else if(bpObject instanceof Pathway){// for Pathway
				Pathway pathway = (Pathway) bpObject;
				if(pathway.getPathwayComponentInteraction() != null)
					addToParentMap(bpObject, pathway.getPathwayComponentInteraction());
				if(pathway.getPathwayComponentPathway() != null)
				addToParentMap(bpObject, pathway.getPathwayComponentPathway());
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

}
