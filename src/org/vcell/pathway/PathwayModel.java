package org.vcell.pathway;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vcell.pathway.persistence.PathwayReader.RdfObjectProxy;

public class PathwayModel {
	private HashSet<BioPaxObject> biopaxObjects = new HashSet<BioPaxObject>();
	protected transient ArrayList<PathwayListener> aPathwayListeners = new ArrayList<PathwayListener>();

	
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
	
}
