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
	
	public boolean includeAllComponents = false;

	public void expandSelection(PathwayModel pathwayModel, List<BioPaxObject> selectedList, boolean isNeighborsIncluded) {
		includeAllComponents = isNeighborsIncluded;
		Set<BioPaxObject> selectedSet = new HashSet<BioPaxObject> (selectedList);
		Set<BioPaxObject> selectedToAdd = new HashSet<BioPaxObject>();
		// Add all entity ancestors (e.g. complexes for components, interactions for participants, etc.)
		if(includeAllComponents){
			do {
				selectedToAdd.clear();
				for(BioPaxObject selected : selectedSet) {
					ArrayList<BioPaxObject> parents = pathwayModel.getParents(selected);
					if(parents != null){
						for(BioPaxObject parent : parents){
							if((!selectedSet.contains(parent)) && (parent instanceof Entity)) {
								selectedToAdd.add(parent);							
							}
						}
					}				
				}
				selectedSet.addAll(selectedToAdd);
			} while(selectedToAdd.size() > 0);
		}
		// Add certain children (e.g. participants, components, controlled interactions)
		do {
			selectedToAdd.clear();
			for(BioPaxObject selected : selectedSet) {
				if(selected instanceof InteractionImpl) {
					for(InteractionParticipant participant : ((Interaction) selected).getParticipants()) {
						PhysicalEntity physicalEntity = participant.getPhysicalEntity();
						if(!selectedSet.contains(physicalEntity)) {
							selectedToAdd.add(physicalEntity);							
						}
					}
					if(selected instanceof Control) {
						Interaction controlledInteraction = 
							((Control) selected).getControlledInteraction();
						if(controlledInteraction != null && !selectedSet.contains(controlledInteraction)) {
							selectedToAdd.add(controlledInteraction);
						}
					} else if(selected instanceof Complex && includeAllComponents) {
						ArrayList<PhysicalEntity> components = ((Complex) selected).getComponents();
						for(PhysicalEntity component : components) {
							if(!selectedSet.contains(component)) {
								selectedToAdd.add(component);								
							}
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
	
}
