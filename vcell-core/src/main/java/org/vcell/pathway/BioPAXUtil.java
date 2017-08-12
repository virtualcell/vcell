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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sbpax.util.sets.SetOfOne;
import org.sbpax.util.sets.SetOfTwo;
import org.vcell.pathway.InteractionParticipant.Type;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.sybil.util.JavaUtil;
import org.vcell.util.TokenMangler;

public class BioPAXUtil {

	public static int MAX_INTERACTION_CONTROL_CHAIN_DEPTH = 10;
	
	public static Interaction getControlledNonControlInteraction(Control control) {
		for(int i = 0; i < MAX_INTERACTION_CONTROL_CHAIN_DEPTH; ++i) {
			Interaction interaction = control.getControlledInteraction();
			if(interaction instanceof Control) {
				control = (Control) interaction;
			} else {
				return interaction;
			}
		}
		return null;
	}
	
	public static Set<Control> getAllControls(PathwayModel pathwayModel) {
		Set<Control> controls = new HashSet<Control>();
		if(pathwayModel == null) {
			return controls;
		}
		Set<BioPaxObject> bpObjects = pathwayModel.getBiopaxObjects();
		for(BioPaxObject bpObject : bpObjects) {
			if(bpObject instanceof Control) {
				Control c = (Control) bpObject;
				controls.add(c);
			}
		}
		return controls;
	}
	private static Set<Conversion> getAllConversions(PathwayModel pathwayModel) {
		Set<Conversion> conversion = new HashSet<Conversion>();
		if(pathwayModel == null) {
			return conversion;
		}
		
		Set<BioPaxObject> bpObjects = pathwayModel.getBiopaxObjects();
		for(BioPaxObject bpObject : bpObjects) {
			if(bpObject instanceof Conversion) {
				Conversion c = (Conversion) bpObject;
				conversion.add(c);
			}
		}
		return conversion;
	}
	// find the controls for which this physical entity is a controller
	private static Set<Control> getControlsOfController(PhysicalEntity controller, PathwayModel pathwayModel) {
		Set<Control> filteredControls = new HashSet<Control>();
		if(pathwayModel == null) {
			return filteredControls;
		}
		Set<Control> allControls = getAllControls(pathwayModel);
		for(Control c : allControls) {
			List<PhysicalEntity> controllers = c.getPhysicalControllers();
			for(PhysicalEntity pE : controllers) {
				if(pE.getID().equals(controller.getID())) {
					filteredControls.add(c);
					break;
				}
			}
		}
		return filteredControls;
	}
	public static boolean isController(PhysicalEntity controller, PathwayModel pathwayModel) {
		if(pathwayModel == null) {
			return false;
		}
		if(getControlsOfController(controller, pathwayModel).isEmpty()) {
			return false;	// not a controller since is not part of any control
		} else {
			return true;
		}
	}
	public static boolean isReactionParticipant(PhysicalEntity physicalEntity, PathwayModel pathwayModel) {
		if(pathwayModel == null) {
			return false;
		}
		Set<Conversion> allConversions = getAllConversions(pathwayModel);
		for(Conversion c : allConversions) {
			List<InteractionParticipant> participants = c.getParticipants();
			for(InteractionParticipant p : participants) {
				if(p.getType() == Type.RIGHT || p.getType() == Type.LEFT) {
					if(p.getPhysicalEntity() == physicalEntity) {
						return true;	// true if participant of at least 1 conversion
					}
				}
			}
		}
		return false;
	}
	// find the kinetic laws of the controls for which this physical entity is a controller
	public static Set<SBEntity> getKineticLawsOfController(PhysicalEntity controller, PathwayModel pathwayModel) {
		Set<SBEntity> kineticLaws = new HashSet<SBEntity>();
		if(pathwayModel == null) {
			return kineticLaws;
		}
		Set<Control> controls = getControlsOfController(controller, pathwayModel);
		for(Control c : controls) {
			ArrayList<SBEntity> kLaws = c.getSBSubEntity();
			for(SBEntity kL : kLaws) {
				kineticLaws.add(kL);
			}
		}
		return kineticLaws;
	}
	
	// find controls governing this interaction (Catalysis, Modulation or simply Control)
	// it's all the same for us, in vCell we only have the general concept of Catalysis
	public static Set<Control> getControlsOfInteraction(Interaction interaction, PathwayModel pathwayModel) {
		Set<Control> controls = new HashSet<Control>();
		if(pathwayModel == null) {
			return controls;
		}
		Set<BioPaxObject> bpObjects = pathwayModel.getBiopaxObjects();
		for(BioPaxObject bpObject : bpObjects) {
			if(bpObject instanceof Control) {
				Control c = (Control) bpObject;
				if(c.getControlledInteraction() == interaction){	// TODO: is this reliable or should we check by ID?
					controls.add(c);
				}
			}
		}
		return controls;
	}
	public static Set<PhysicalEntity> getControllersOfInteraction(Interaction interaction, PathwayModel pathwayModel){
		Set<PhysicalEntity> controllers = new HashSet<PhysicalEntity>();
		if(pathwayModel == null){
			return controllers;
		}
		Set<Control> controls = BioPAXUtil.getControlsOfInteraction(interaction, pathwayModel);
		for(Control control : controls) {
			if(control.getPhysicalControllers() != null){
				for(PhysicalEntity ep : control.getPhysicalControllers()){ 
					controllers.add(ep);
				}
			}
		}
		return controllers;
	}
	
@Deprecated
	public static Set<Control> findAllControls(Interaction interaction, PathwayModel pathwayModel) {
		return findAllControls(interaction, pathwayModel, 0);
	}
@Deprecated
	public static Set<Control> findAllControls(Interaction interaction, PathwayModel pathwayModel, int depth) {
		if(depth >= MAX_INTERACTION_CONTROL_CHAIN_DEPTH) {
			return Collections.<Control>emptySet();
		}
		HashSet<Control> controls = new HashSet<Control>();
		List<BioPaxObject> parents = pathwayModel.getParents(interaction);
		for(BioPaxObject bpObject : parents) {
			if(bpObject instanceof Control) {
				Control control = (Control) bpObject;
				controls.add(control);
				controls.addAll(findAllControls(control, pathwayModel, depth + 1));
			}
		}
		return controls;
	}
	
	public static class Process {
		
		protected final Conversion conversion;
		protected final Control control;
		
		public Process(Conversion conversion, Control control) {
			this.conversion = conversion;
			this.control = control;
		}
		
		public Process(Conversion conversion) { this(conversion, null); }
		
		public Conversion getConversion() { return conversion; }
		public Control getControl() { return control; }
		
		public Interaction getTopLevelInteraction() {
			if(control != null) { return control; }
			return conversion;
		}
		
		public Set<Interaction> getInteractions() {
			if(control != null) { return new SetOfTwo<Interaction>(conversion, control); }	
			return new SetOfOne<Interaction>(conversion);			
		}

		public Set<PhysicalEntity> getControllers() {
			HashSet<PhysicalEntity> controllers = new HashSet<PhysicalEntity>();
			
			if(control != null) {
				for(InteractionParticipant participant : control.getParticipants(Type.CONTROLLER)) {
					controllers.add(participant.getPhysicalEntity());
				}
			}
			return controllers;
		}
		
		public int hashCode() {
			return JavaUtil.hashCode(conversion) + JavaUtil.hashCode(control);
		}
		
		public boolean equals(Object o) {
			if(o instanceof Process) {
				Process op = (Process) o;
				return JavaUtil.equals(conversion, op.getConversion()) && 
				JavaUtil.equals(control, op.getControl());
			}
			return false;
		}
	
		public String getName() {
			String name = BioPAXUtil.getName(conversion);
			if(control != null) {
				List<PhysicalEntity> controllers = control.getParticipantPhysicalEntities(Type.CONTROLLER);
				if(controllers.isEmpty()) {
					name = name + "_unknown";
				} else {
					for(PhysicalEntity catalyst : controllers) {
						name = name + "_" + BioPAXUtil.getName(catalyst);
					}
				}
			}
			return name;
		}
	}
	
	public static Set<Process> getAllProcesses(PathwayModel pathwayModel, Conversion interaction) {
		HashSet<Process> processes = new HashSet<Process>();
		
		Set<Control> controls = getControlsOfInteraction(interaction, pathwayModel);
		if(controls.size() == 0) {
			processes.add(new Process(interaction));
			return processes;
		}
		for(Control control : controls) {
			processes.add(new Process(interaction, control));
		}
		return processes;
	}
//	public static Set<Process> getAllProcesses(PathwayModel pathwayModel, Conversion conversion) {
//		HashSet<Process> processes = new HashSet<Process>();
//		for(BioPaxObject parent : pathwayModel.getParents(conversion)) {
//			
//			StringBuffer s = new StringBuffer();
//			parent.show(s);
//			System.out.println(s);
//			
//			if(parent instanceof Catalysis) {
//				Catalysis catalysis = (Catalysis) parent;
//				boolean hasModulation = false;
//				for(BioPaxObject grandParent : pathwayModel.getParents(parent)) {
//					if(grandParent instanceof Modulation) {
//						Modulation modulation = (Modulation) grandParent;
//						processes.add(new Process(conversion, catalysis, modulation));
//						hasModulation = true;
//					}
//				}
//				if(!hasModulation) {
//					processes.add(new Process(conversion, catalysis));
//				}
//			}
//		}
//		if(processes.isEmpty()) {
//			processes.add(new Process(conversion));
//		}
//		return processes;
//	}
	
	//convert the name of biopax object to safety vcell object name
	public static String getSafetyName(String oldValue){
		return TokenMangler.fixTokenStrict(oldValue, 60);
	} 

	public static String getName(Entity entity) {
		String name = "";
		if(entity instanceof PhysicalEntity) {
			PhysicalEntity pe = (PhysicalEntity)entity;
			if (pe.getCellularLocation() != null) {
				// we don't try to second guess naming if cellular location vocabulary is present
				return getNameOld(entity);
			} else {
				// we try to extract location from id and append it to name
				ArrayList<String> nameList = entity.getName();
				if(!nameList.isEmpty()) {
					name = getSafetyName(nameList.get(0)).trim();
					String postfix = extractLocationFromId(entity);
					return (name+postfix);
					
				}else{					
					getNameOld(entity);
				}
			}
			return name;
		} else {
			return getNameOld(entity);
		}
	}
	
	private static String extractLocationFromId(Entity entity) {
		final String[] locations = {
				"noname",
				"unknown",
				"membrane",
				"nucleus",
				"nucleoplasm",
				"cytoplasm",
				"cytosol",
				"intracellular",
				"extracellular",
				"endosome",
				"reticulum",
				"golgi",
				"lumen",
				"vesicle",
				"mitochondria"
				};
		// HashSet locationsSet = new HashSet(Arrays.asList(locations));

		String id = entity.getID();
		int locationIndex = id.lastIndexOf("_");
		if(locationIndex <= 0) {
			return "";
		}
		String postfix = id.substring(locationIndex);		// contains the '_'
		for(String location : locations) {
			if(postfix.contains(location)) {
				return postfix;
			}
		}
		return "";
	}

	public static String getNameOld(Entity entity) {
		ArrayList<String> nameList = entity.getName();
		if(!nameList.isEmpty()) {
			return (getSafetyName(nameList.get(0))).trim();
		}else{					
			return entity.getIDShort();
		}
	}
	
}
