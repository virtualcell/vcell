/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.pathway.group.PathwayGrouping;
import org.vcell.pathway.id.URIUtil;
import org.vcell.pathway.kinetics.SBPAXKineticsExtractor;
import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;
import org.vcell.pathway.sbo.SBOListEx;
import org.vcell.pathway.sbo.SBOParam;
import org.vcell.pathway.sbo.SBOTerm;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.SBVocabulary;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.UserCancelException;

//import cbit.vcell.biomodel.BioModel;
import org.vcell.util.document.Identifiable;

public class PathwayModel {
	
	public class SBVocabularyEx extends SBVocabulary {
		boolean inferred = false;
		boolean isInferred() { return this.inferred; }
		void setInferred(boolean inferred) { this.inferred = inferred; }
	}
	
	private HashSet<BioPaxObject> biopaxObjects = new HashSet<BioPaxObject>();
	private HashSet<String> diagramObjectsID = new HashSet<String>();
	protected transient ArrayList<PathwayListener> aPathwayListeners = new ArrayList<PathwayListener>();

	protected Map<BioPaxObject, HashSet<BioPaxObject>> parentMap = 
		new Hashtable<BioPaxObject, HashSet<BioPaxObject>>();
	private Map<BioPaxObject, BioPaxObject> groupMap = new Hashtable<BioPaxObject, BioPaxObject> ();
	
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
			String bpObjectID = bpObject.getID();
			// for some obscure reason the diagramObjectISs have a "http://vcell.org/biopax/#" prefix
			// hence there'll be no match if we simply use   diagramObjectsID.contains(bpObjectID)
			Iterator<String> iterator = diagramObjectsID.iterator();
			while(iterator.hasNext()) {
				String doid = URIUtil.getLocalName(iterator.next());
				if(doid.equals(bpObjectID) || bpObjectID.contains(doid)) {
					newBiopaxObjects.add(bpObject);
				}
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
	private boolean bDisableUpdate = false;
	public void setDisableUpdate(boolean bDisableUpdate){
		this.bDisableUpdate = bDisableUpdate;
	}
	public void firePathwayChanged(PathwayEvent event) {
		for (PathwayListener l : getPathwayListeners()){
			l.pathwayChanged(event);
		}
		if(!bDisableUpdate){
			refreshParentMap();
			refreshGroupMap();
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
		firePathwayChanged(new PathwayEvent(this,PathwayEvent.CHANGED));
		return bioPaxObject;
	}
	
	public ArrayList <BioPaxObject> add(ArrayList <BioPaxObject> bpObjects){
		if (bpObjects==null){
			throw new RuntimeException("added a null object to pathway model");
		}
		for(BioPaxObject bioPaxObject : bpObjects)
			biopaxObjects.add(bioPaxObject);
		firePathwayChanged(new PathwayEvent(this,PathwayEvent.CHANGED));
		return bpObjects;
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

	private void reconcileReferencesPreprocessing() {
		
	}

	// You're not expected to understand this
	public void reconcileReferences(ClientTaskStatusSupport clientTaskStatusSupport) {
		try{
			HashMap<String, BioPaxObject> resourceMap = new HashMap<String, BioPaxObject>();
			HashSet<BioPaxObject> replacedBPObjects = new HashSet<BioPaxObject>();
			for (BioPaxObject bpObject : biopaxObjects){
				if (!(bpObject instanceof RdfObjectProxy) && bpObject.getID() != null ){
					resourceMap.put(bpObject.getID(), bpObject);
				} 
			}

			setDisableUpdate(true);
			ArrayList<RdfObjectProxy> proxiesToDelete = new ArrayList<RdfObjectProxy>();
			reconcileReferencesPreprocessing();
			int count = 0;
			int totalCount = biopaxObjects.size();
			for (BioPaxObject bpObject : biopaxObjects){
				if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
					throw UserCancelException.CANCEL_GENERIC;
				}
				int prog = (int)(count*100.0/totalCount);
				count++;
				if (clientTaskStatusSupport != null) {
					clientTaskStatusSupport.setMessage("Finishing up...");
					clientTaskStatusSupport.setProgress(prog);
				}
				
				if(bpObject instanceof RdfObjectProxy){
					proxiesToDelete.add((RdfObjectProxy)bpObject);
				}else{
					bpObject.replace(resourceMap, replacedBPObjects);
				}
				
//				if (bpObject instanceof RdfObjectProxy){
//					RdfObjectProxy rdfObjectProxy = (RdfObjectProxy)bpObject;
//					if (rdfObjectProxy.getResource() != null){
//						String resource = rdfObjectProxy.getResource().replace("#","");
//						BioPaxObject concreteObject = resourceMap.get(resource);//findFromResourceID(rdfObjectProxy.getResource());
//						if (concreteObject != null){
//							//System.out.println("replacing "+rdfObjectProxy.toString()+" with "+concreteObject.toString());
//							replace(rdfObjectProxy,concreteObject);
//							proxiesToDelete.add(rdfObjectProxy);
//						}else{
////							System.out.println("unable to resolve reference to "+rdfObjectProxy.toString());
//						}
//					}else{
////						System.out.println("rdfProxy had no resource set "+rdfObjectProxy.toString());
//					}
//				}
			}
			biopaxObjects.removeAll(proxiesToDelete);
			
			hideUtilityClassObjects();
			cleanupUnresolvedProxies();
			ConvertModulationToCatalysis();
//			System.out.println(show(false));
			ProcessKineticLaws();
			setDisableUpdate(false);
			firePathwayChanged(new PathwayEvent(this,PathwayEvent.CHANGED));
		}finally{
			setDisableUpdate(false);
		}
	}

	/*
	 * For each kinetic law verify whether the SBOTerm which defines the type of kinetic law exists
	 * If it doesn't exist try to guesstimate it based on the type of parameters
	 * For example, if Km is present we assume it's Michaelis-Menten rate law, otherwise it's mass action
	 */
	private void ProcessKineticLaws() {
		Set<Control> controls = BioPAXUtil.getAllControls(this);
		Iterator<Control> iterator = controls.iterator();
		while (iterator.hasNext()) {
			Control control = iterator.next();
			ArrayList<SBEntity> sbEntities = control.getSBSubEntity();
			for(SBEntity sbE : sbEntities) {
				// the following if clause may not be needed, we KNOW that 
				// the only SBSubEntities allowed in a control are kinetic laws
				if(sbE.getID().contains("kineticLaw")) {
					// build list of the parameters of this kinetic law in sboParams
					ArrayList<SBOParam> sboParams = new ArrayList<SBOParam>();	// params of this kinetic law
					ArrayList<SBEntity> subEntities = sbE.getSBSubEntity();
					for(SBEntity subEntity : subEntities) {
						if(subEntity instanceof SBMeasurable) {
							SBMeasurable m  = (SBMeasurable)subEntity;
							if(!m.hasTerm()) {
								break;	// we don't know what to do with a measurable with no SBTerm
							}
							String termName = m.extractSBOTermAsString();
							SBOTerm sboT = SBOListEx.sboMap.get(termName);
							System.out.println("    " + sboT.getIndex() + "  " + sboT.getName());
							SBOParam sboParam = SBPAXKineticsExtractor.matchSBOParam(sboT);
							
							if(m.hasNumber()) {
								double number = m.getNumber().get(0);
								sboParam.setNumber(number);
							}
							if(m.hasUnit()) {
								String unit = m.extractSBOUnitAsString();
								sboParam.setUnit(unit);
							}
							sboParams.add(sboParam);
						}
					}
					ArrayList<SBVocabulary> sbTerms = sbE.getSBTerm();
					if(sbTerms.isEmpty()) {	// we try to guesstimate kinetic law type based on params found above
						SBVocabularyEx sbTerm = new SBVocabularyEx();
						ArrayList<String> termNames = new ArrayList<String>();
						String id;
						SBOParam kMichaelis = SBPAXKineticsExtractor.extractMichaelisForwardParam(sboParams);
						if(kMichaelis == null) {
							id = new String("SBO:0000012");	// mass action rate law
							System.out.println("   --- matched kinetic law to Mass Action");
						} else {
							id = new String("SBO:0000029");	// irreversible Henri-Michaelis-Menten rate law
							System.out.println("   --- matched kinetic law to Henri-Michaelis-Menten");
						}
						sbTerm.setInferred(true);
						sbTerm.setID(id);
						sbTerms.add(sbTerm);
					}
				}
			}
		}
	}

	private void ConvertModulationToCatalysis() {
		// we cannot represent the case when a physical entity modulates a catalysis interaction
		// instead, we make it appear that the modulation directly regulates the conversion 
		//  controlled by the catalysis		
		Set<BioPaxObject> bpObjects = getBiopaxObjects();
		for(BioPaxObject bpObject : bpObjects) {
			if(bpObject instanceof Modulation) {
				Modulation m = (Modulation) bpObject;
				
				BioPaxObject bpO = m.getControlledInteraction();
				if(bpO != null && bpO instanceof Catalysis) {
					Catalysis c = (Catalysis) bpO;
					bpO = c.getControlledInteraction();
					if(bpO != null && bpO instanceof Conversion) {
						m.setControlledInteraction((Conversion)bpO);
					}
				}
			}
		}
	}

	private BioPaxObject findFromResourceID(String resource) {
		for (BioPaxObject bpObject : biopaxObjects){
			if (bpObject.getID() != null){
				// In case one of the ID is an incomplete URI
				if (bpObject.getID().endsWith(resource) || resource.endsWith(bpObject.getID())){
					return bpObject;
				}
			}
		}
		return null;
	}
	
	public BioPaxObject findFromNameAndType(String name, String type) {
		for (BioPaxObject bpObject : biopaxObjects){
			if(bpObject instanceof PhysicalEntity){
				PhysicalEntity  pe = (PhysicalEntity) bpObject;
				if(pe.getName().contains(name) && type.equals(EntityImpl.TYPE_PHYSICALENTITY)){
					return bpObject;
				}
			}else if(bpObject instanceof Interaction){
				Interaction  in = (Interaction) bpObject;
				if(in.getName().contains(name) && type.equals(EntityImpl.TYPE_INTERACTION)){
					return bpObject;
				}
			}else if(bpObject instanceof Pathway){
				Pathway  pw = (Pathway) bpObject;
				if(pw.getName().contains(name) && type.equals(EntityImpl.TYPE_PATHWAY)){
					return bpObject;
				}
			}else if(bpObject instanceof Gene){
				Gene  gene = (Gene) bpObject;
				if(gene.getName().contains(name) && type.equals(EntityImpl.TYPE_GENE)){
					return bpObject;
				}
			}
			
//				// contains() won't work, may result in matching objects which are wildly incompatible
//				if (bpObject.getID().contains(resource)){
//					return bpObject;
//				}

		}
		return null;
	}
	
	private void hideUtilityClassObjects() {
		// remove all references to UtilityClass objects from the model root (biopaxObjects)
		// they are still embedded within various Entity objects
		// the producer will put them back in biopaxObjects set at the time when we save
		HashSet<BioPaxObject> newBiopaxObjects = new HashSet<BioPaxObject>();
		for (BioPaxObject bpObject : biopaxObjects){
			if(!(bpObject instanceof UtilityClass)) {
				newBiopaxObjects.add(bpObject);
			}
		}
		biopaxObjects = newBiopaxObjects;
	}

	private void cleanupUnresolvedProxies() {
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
		boolean showUnresolvedProxyCount = false;
		if(showUnresolvedProxyCount) {
			System.out.println("Unresolved proxies: " + unresolvedProxiesCount);			
		}
		biopaxObjects = new2BiopaxObjects;
	}
	
	public void refreshGroupMap(){
		Hashtable<BioPaxObject, BioPaxObject> newGroupMap = new Hashtable<BioPaxObject, BioPaxObject> ();
		for(BioPaxObject bpObject : biopaxObjects){
			if(bpObject instanceof GroupObject){
				GroupObject gObject = (GroupObject) bpObject;
				for(BioPaxObject bpo : gObject.getGroupedObjects()){
					newGroupMap.put(bpo, gObject);
				}
			}
		}
		groupMap = newGroupMap;
	}
	
	public Map<BioPaxObject, BioPaxObject> getGroupMap(){
		return groupMap;
	}
	
	public Set<BioPaxObject> getBioPaxElements(BioPaxObject bioPaxObject){
		Set<BioPaxObject> components = new HashSet<BioPaxObject>();
		if(bioPaxObject instanceof PhysicalEntity){// for physicalEntity:: get its members and components
			components.addAll(((PhysicalEntity)bioPaxObject).getMemberPhysicalEntity());
			if(bioPaxObject instanceof Complex){
				components.addAll(((Complex)bioPaxObject).getComponents());
			}
		}else if(bioPaxObject instanceof Interaction){// for interaction:: get its participants
			List<InteractionParticipant> participants = ((Interaction) bioPaxObject).getParticipants();
			if(participants != null) {
				for(InteractionParticipant participant : participants) {
					components.add(participant.getPhysicalEntity());
				}
			}
			if(bioPaxObject instanceof Control){// for Control
				// TODO
				if(bioPaxObject instanceof Catalysis){// for Catalysis
					// TODO
				}
			}else if(bioPaxObject instanceof Conversion){// for Conversion
				// TODO
			}else if(bioPaxObject instanceof TemplateReaction){// for TemplateReaction
				// TODO
			}
		}else if(bioPaxObject instanceof Pathway){// for Pathway
			// TODO
		}else{ // for Gene
			// TODO
		}
		return components;
	}
	
	public Map<BioPaxObject, HashSet<BioPaxObject>> refreshParentMap(){
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
			}else if(bpObject instanceof Interaction){
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
		return parentMap;
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
	public List<BioPaxObject> getParents(BioPaxObject bpObject){
		ArrayList<BioPaxObject> parentObjects = new ArrayList<BioPaxObject>();
		HashSet<BioPaxObject> parentSet = parentMap.get(bpObject);
		if(parentSet != null){
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

	public Set<BioPaxObject> getDisplayableBioPaxObjectList(){
		PathwayGrouping pathwayGrouping = new PathwayGrouping();
		return pathwayGrouping.updateBioPaxObjectList(this);
	}
	public BioPaxObject findTopLevelGroupAncestor(BioPaxObject bpObject){
		Map<BioPaxObject, BioPaxObject> groupMap = getGroupMap();
		PathwayGrouping pathwayGrouping = new PathwayGrouping();
		return pathwayGrouping.findGroupAncestor(groupMap, bpObject);
	}
	
	private Set<BioPaxObject> getLeafElements(GroupObject groupObject){
		Set<BioPaxObject> leaves = new HashSet<BioPaxObject>();
		for(BioPaxObject bpo : groupObject.getGroupedObjects()){
			if(bpo instanceof GroupObject){
				leaves.addAll(getLeafElements((GroupObject)bpo));
			}else{
				leaves.add(bpo);
			}
		}
		return leaves;
	}
	
	public void cleanGroupObjects(){
		ArrayList<BioPaxObject> emptyGroups = new ArrayList<BioPaxObject>();
		for(BioPaxObject bpObject : biopaxObjects){
			if(bpObject instanceof GroupObject){
				boolean isEmpty = true;
				for(BioPaxObject bpo : getLeafElements((GroupObject)bpObject)){
					if(biopaxObjects.contains(bpo)){
						isEmpty = false;
						break;
					}
				}
				if(isEmpty){
					emptyGroups.add(bpObject);
				}
			}
		}
		remove(emptyGroups);
	}
}
