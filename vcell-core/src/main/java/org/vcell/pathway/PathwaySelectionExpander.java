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
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PathwaySelectionExpander {
	
	public void forPhysicalEntitiesAddComplexes(PathwayModel pathwayModel, List<BioPaxObject> selectedList) {
		Set<BioPaxObject> selectedSet = new HashSet<BioPaxObject> (selectedList);
		Set<BioPaxObject> selectedToAdd = new HashSet<BioPaxObject>();
		do {
			selectedToAdd.clear();
			for(BioPaxObject selected : selectedSet) {
				if(selected instanceof PhysicalEntity) {
					List<BioPaxObject> parents = pathwayModel.getParents(selected);
					for(BioPaxObject parent : parents){
						if((!selectedSet.contains(parent)) && (parent instanceof Complex)) {
							selectedToAdd.add(parent);							
						}
					}				
				}
			}
			selectedSet.addAll(selectedToAdd);
		} while(selectedToAdd.size() > 0);
		for(BioPaxObject selected : selectedSet){
			if(!selectedList.contains(selected)) {
				selectedList.add(selected);				
			}
		}
	}

	public void forComplexesAddComponents(PathwayModel pathwayModel, List<BioPaxObject> selectedList) {
		Set<BioPaxObject> selectedSet = new HashSet<BioPaxObject> (selectedList);
		Set<BioPaxObject> selectedToAdd = new HashSet<BioPaxObject>();
		do {
			selectedToAdd.clear();
			for(BioPaxObject selected : selectedSet) {
				if(selected instanceof Complex) {
					ArrayList<PhysicalEntity> components = ((Complex) selected).getComponents();
					for(PhysicalEntity component : components) {
						if(!selectedSet.contains(component)) {
							selectedToAdd.add(component);													
						}
					}
				}
			}
			selectedSet.addAll(selectedToAdd);
		} while(selectedToAdd.size() > 0);
		for(BioPaxObject selected : selectedSet){
			if(!selectedList.contains(selected)) {
				selectedList.add(selected);				
			}
		}
	}

	
	public void forPhysicalEntitiesAddInteractions(PathwayModel pathwayModel, List<BioPaxObject> selectedList) {
		Set<BioPaxObject> selectedSet = new HashSet<BioPaxObject> (selectedList);
		Set<BioPaxObject> selectedToAdd = new HashSet<BioPaxObject>();
		for(BioPaxObject selected : selectedList) {
			if(selected instanceof PhysicalEntity) {
				List<BioPaxObject> parents = pathwayModel.getParents(selected);
				for(BioPaxObject parent : parents){
					if((!selectedSet.contains(parent)) && (parent instanceof Interaction)) {
						selectedToAdd.add(parent);							
					}
				}
			}
		}
		selectedSet.addAll(selectedToAdd);
		for(BioPaxObject selected : selectedSet){
			if(!selectedList.contains(selected)) {
				selectedList.add(selected);				
			}
		}
	}

	public void forInteractionsAddControls(PathwayModel pathwayModel, List<BioPaxObject> selectedList) {
		Set<BioPaxObject> selectedSet = new HashSet<BioPaxObject> (selectedList);
		Set<BioPaxObject> selectedToAdd = new HashSet<BioPaxObject>();
		// Add all entity ancestors (e.g. complexes for components, interactions for participants, etc.)
		do {
			selectedToAdd.clear();
			for(BioPaxObject selected : selectedSet) {
				if(selected instanceof Interaction) {
					List<BioPaxObject> parents = pathwayModel.getParents(selected);
					for(BioPaxObject parent : parents){
						if((!selectedSet.contains(parent)) && (parent instanceof Control)) {
							selectedToAdd.add(parent);							
						}
					}
				}
			}
			selectedSet.addAll(selectedToAdd);
		} while(selectedToAdd.size() > 0);
		for(BioPaxObject selected : selectedSet){
			if(!selectedList.contains(selected)) {
				selectedList.add(selected);				
			}
		}
	}

	public void forInteractionsAddParticipants(PathwayModel pathwayModel, List<BioPaxObject> selectedList) {
		Set<BioPaxObject> selectedSet = new HashSet<BioPaxObject> (selectedList);
		Set<BioPaxObject> selectedToAdd = new HashSet<BioPaxObject>();
		for(BioPaxObject selected : selectedSet) {
			if(selected instanceof Interaction) {
				for(InteractionParticipant participant : ((Interaction) selected).getParticipants()) {
					PhysicalEntity physicalEntity = participant.getPhysicalEntity();
					if(!selectedSet.contains(physicalEntity)) {
						selectedToAdd.add(physicalEntity);							
					}
				}
			}
		}
		selectedSet.addAll(selectedToAdd);
		for(BioPaxObject selected : selectedSet){
			if(!selectedList.contains(selected)) {
				selectedList.add(selected);				
			}
		}
	}
	
}
