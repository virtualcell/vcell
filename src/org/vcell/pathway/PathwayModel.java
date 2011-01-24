package org.vcell.pathway;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

public class PathwayModel {
	private HashSet<BioPaxObject> biopaxObjects = new HashSet<BioPaxObject>();
	protected transient ArrayList<PathwayListener> aPathwayListeners = new ArrayList<PathwayListener>();

	protected Hashtable<BioPaxObject, HashSet<BioPaxObject>> parentHashtable = 
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
		System.err.println("add all BioPaxObject children of this object to pathwayModel");
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
	
	public Hashtable<BioPaxObject, HashSet<BioPaxObject>> getParentHashtable(){
		return parentHashtable;
	}
	
	public void refreshParentHashtable(){
		parentHashtable =  new Hashtable<BioPaxObject, HashSet<BioPaxObject>>();
		for (BioPaxObject bpObject : biopaxObjects){
			// only build the parent hashtable for Entity
			if (bpObject instanceof PhysicalEntity){ // for PhysicalEntity
				System.out.println("PhysicalEntity ...");
				if(((PhysicalEntity) bpObject).getMemberPhysicalEntity() != null)
					addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((PhysicalEntity) bpObject).getMemberPhysicalEntity()));
				if(bpObject instanceof Complex){// for Complex
					System.out.println("Complex ...");
					if(((Complex) bpObject).getComponent() != null)
						addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((Complex) bpObject).getComponent()));
				}
			}else if(bpObject instanceof InteractionImpl){// for Interaction
				System.out.println("Interaction ..");
				if(((InteractionImpl) bpObject).getParticipants() != null)
					addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((InteractionImpl) bpObject).getParticipants()));
				if(bpObject instanceof Control){// for Control
					System.out.println("Control ..");
					if(((Control) bpObject).getPathwayControllers() != null)
						addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((Control) bpObject).getPathwayControllers()));
					if(((Control) bpObject).getPhysicalControllers() != null)
						addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((Control) bpObject).getPhysicalControllers()));
					ArrayList<BioPaxObject> controled = new ArrayList<BioPaxObject>();
					if(((Control) bpObject).getControlledInteraction() != null)
						controled.add(((BioPaxObject)((Control) bpObject).getControlledInteraction()));
					if(((Control) bpObject).getControlledPathway() != null)
						controled.add(((BioPaxObject)((Control) bpObject).getControlledPathway()));
					addToParentHashtable(bpObject, controled);
					if(bpObject instanceof Catalysis){// for Catalysis
						System.out.println("Catalysis ..");
						if(((Catalysis) bpObject).getCofactors() != null)
							addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((Catalysis) bpObject).getCofactors()));
					}
				}else if(bpObject instanceof Conversion){// for Conversion
					System.out.println("Conversion ..");
					if(((Conversion) bpObject).getLeftSide() != null)
						addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((Conversion) bpObject).getLeftSide()));
					if(((Conversion) bpObject).getRightSide() != null)
						addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((Conversion) bpObject).getRightSide()));
				}else if(bpObject instanceof TemplateReaction){// for TemplateReaction
					System.out.println("TemplateReaction ..");
					if(((TemplateReaction) bpObject).getProductDna() != null)
						addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((TemplateReaction) bpObject).getProductDna()));
					if(((TemplateReaction) bpObject).getProductProtein() != null)
						addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((TemplateReaction) bpObject).getProductProtein()));
					if(((TemplateReaction) bpObject).getProductRna() != null)
						addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((TemplateReaction) bpObject).getProductRna()));
					ArrayList<BioPaxObject> template = new ArrayList<BioPaxObject>();
					if(((TemplateReaction) bpObject).getTemplateDna() != null)
						template.add(((BioPaxObject)((TemplateReaction) bpObject).getTemplateDna()));
					if(((TemplateReaction) bpObject).getTemplateDnaRegion() != null)
						template.add(((BioPaxObject)((TemplateReaction) bpObject).getTemplateDnaRegion()));
					if(((TemplateReaction) bpObject).getTemplateRna() != null)
						template.add(((BioPaxObject)((TemplateReaction) bpObject).getTemplateRna()));
					if(((TemplateReaction) bpObject).getTemplateRnaRegion() != null)
						template.add(((BioPaxObject)((TemplateReaction) bpObject).getTemplateRnaRegion()));
					addToParentHashtable(bpObject, template);
				}
			}else if(bpObject instanceof Pathway){// for Pathway
				System.out.println("Pathway ..");
				if(((Pathway) bpObject).getPathwayComponentInteraction() != null)
					addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((Pathway) bpObject).getPathwayComponentInteraction()));
				if(((Pathway) bpObject).getPathwayComponentPathway() != null)
				addToParentHashtable(bpObject, new ArrayList<BioPaxObject>(((Pathway) bpObject).getPathwayComponentPathway()));
			}else{ // for Gene
			}
		}		
		// print out the hashtable
		System.out.println("ParentHashtable size is "+ parentHashtable.size());
		for(BioPaxObject bp : parentHashtable.keySet()){
			System.out.println(bp);
			for (BioPaxObject vbp : parentHashtable.get(bp)){
				System.out.println("=> "+ vbp);
			}
		}
	}
	// add elements to hash table. 
	private void addToParentHashtable(BioPaxObject parent, ArrayList<BioPaxObject> children){
//		System.out.println("Adding...");
		if(children.size() != 0){
			for(BioPaxObject bpObject : children){
				if (!parentHashtable.containsKey(bpObject)){
					parentHashtable.put(bpObject, new HashSet<BioPaxObject>());
//					System.err.println("create key "+bpObject.getID()+" to hash");
				}
					parentHashtable.get(bpObject).add(parent);
//					System.err.println("add "+bpObject.getID()+" to hash");
//					if(parent instanceof Entity && bpObject instanceof Entity) {
//						Entity parentEntity = (Entity) parent;
//						Entity childEntity = (Entity) bpObject;
//						System.out.println("Parent " + parentEntity.getName() + " - child " + 
//								childEntity.getName());
//					}
			}
		}
	}
	
	// get parents list for one Biopax object
	public ArrayList<BioPaxObject> getParents(BioPaxObject bpObject){
		ArrayList<BioPaxObject> parentObjects = new ArrayList<BioPaxObject>();
		HashSet<BioPaxObject> parentSet = parentHashtable.get(bpObject);
		if(parentSet == null){
			return null;
		}else{
			for(BioPaxObject bp : parentSet){
				parentObjects.add(bp);
			}
		}
		return parentObjects;
	}
	
}
