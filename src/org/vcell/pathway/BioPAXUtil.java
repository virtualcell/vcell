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
import org.sbpax.util.sets.SetOfThree;
import org.sbpax.util.sets.SetOfTwo;
import org.vcell.pathway.InteractionParticipant.Type;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.sybil.util.JavaUtil;
import org.vcell.util.TokenMangler;

import cbit.vcell.biomodel.BioModel;
//import cbit.vcell.client.desktop.biomodel.BioPaxObjectPropertiesPanel.BioPaxObjectProperty;

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
	
	public static Set<Control> findAllControls(Interaction interaction, PathwayModel pathwayModel) {
		return findAllControls(interaction, pathwayModel, 0);
	}
	
	// internal use only
	private static Set<Catalysis> getCatalyses(BioModel bioModel) {
		Set<Catalysis> catalyses = new HashSet<Catalysis>();
		if(bioModel == null) {
			return catalyses;
		}
		PathwayModel pathwayModel = bioModel.getPathwayModel();
		
		Set<BioPaxObject> bpObjects = pathwayModel.getBiopaxObjects();
		for(BioPaxObject bpObject : bpObjects) {
			if(bpObject instanceof Catalysis) {
				Catalysis catalysis = (Catalysis) bpObject;
				catalyses.add(catalysis);
			}
		}
		return catalyses;
	}
	// find the catalyses for which this physical entity is a controller
	private static Set<Catalysis> getCatalysesOfController(PhysicalEntity controller, BioModel bioModel) {
		Set<Catalysis> filteredCatalyses = new HashSet<Catalysis>();
		if(bioModel == null) {
			return filteredCatalyses;
		}
		Set<Catalysis> allCatalyses = getCatalyses(bioModel);
		for(Catalysis catalysis : allCatalyses) {
			List<PhysicalEntity> controllers = catalysis.getPhysicalControllers();
			for(PhysicalEntity pE : controllers) {
				if(pE.getID().equals(controller.getID())) {
					filteredCatalyses.add(catalysis);
					break;
				}
			}
		}
		return filteredCatalyses;
	}
	// find the kinetic laws of the catalyses for which this physical entity is a controller
	public static Set<SBEntity> getKineticLawsOfController(PhysicalEntity controller, BioModel bioModel) {
		Set<SBEntity> kineticLaws = new HashSet<SBEntity>();
		if(bioModel == null) {
			return kineticLaws;
		}
		Set<Catalysis> catalyses = getCatalysesOfController(controller, bioModel);
		for(Catalysis catalysis : catalyses) {
			ArrayList<SBEntity> kLaws = catalysis.getSBSubEntity();
			for(SBEntity kL : kLaws) {
				kineticLaws.add(kL);
			}
		}
		return kineticLaws;
	}
	// find catalysts of this interaction
	public static Set<Control> getCatalystsOfInteraction(Interaction interaction, BioModel bioModel) {
		Set<Control> catalysts = new HashSet<Control>();
		if(bioModel == null) {
			return catalysts;
		}
		PathwayModel pathwayModel = bioModel.getPathwayModel();
		Set<BioPaxObject> bpObjects = pathwayModel.getBiopaxObjects();
		for(BioPaxObject bpObject : bpObjects) {
			if(bpObject instanceof Control) {
				Control catalysis = (Control) bpObject;
				if(catalysis.getControlledInteraction() == interaction){	// TODO: is this reliable or should we check by ID?
					catalysts.add(catalysis);
				}
			}
		}
		return catalysts;
	}
	

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
	
	public static Set<Process> getAllProcesses(BioModel bioModel, Conversion interaction) {
		HashSet<Process> processes = new HashSet<Process>();
		
		Set<Control> controls = getControlsOfInteraction(interaction, bioModel);
		if(controls.size() == 0) {
			processes.add(new Process(interaction));
			return processes;
		}
		for(Control control : controls) {
			processes.add(new Process(interaction, control));
		}
		return processes;
	}
	// find controls governing this interaction (Catalysis, Modulation or simply Control)
	// it's all the same for us, in vCell we only have the general concept of Catalysis
	public static Set<Control> getControlsOfInteraction(Interaction interaction, BioModel bioModel) {
		Set<Control> controls = new HashSet<Control>();
		if(bioModel == null) {
			return controls;
		}
		PathwayModel pathwayModel = bioModel.getPathwayModel();
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

	
	
//	public static class Process {
//		
//		protected final Conversion conversion;
//		protected final Catalysis catalysis;
//		protected final Modulation modulation;
//		
//		public Process(Conversion conversion, Catalysis catalysis, Modulation modulation) {
//			this.conversion = conversion;
//			this.catalysis = catalysis;
//			this.modulation = modulation;
//		}
//		
//		public Process(Conversion conversion, Catalysis catalysis) { 
//			this(conversion, catalysis, null); 
//		}
//		
//		public Process(Conversion conversion) { this(conversion, null); }
//		
//		public Conversion getConversion() { return conversion; }
//		public Catalysis getCatalysis() { return catalysis; }
//		public Modulation getModulation() { return modulation; }
//		
//		public Interaction getTopLevelInteraction() {
//			if(modulation != null) { return modulation; }
//			if(catalysis != null) { return catalysis; }	
//			return conversion;
//		}
//		
//		public Set<Interaction> getInteractions() {
//			if(modulation != null) { 
//				return new SetOfThree<Interaction>(conversion, catalysis, modulation); 
//			}
//			if(catalysis != null) { return new SetOfTwo<Interaction>(conversion, catalysis); }	
//			return new SetOfOne<Interaction>(conversion);			
//		}
//
//		public Set<PhysicalEntity> getControllers() {
//			HashSet<PhysicalEntity> controllers = new HashSet<PhysicalEntity>();
//			if(catalysis != null) {
//				for(InteractionParticipant participant : catalysis.getParticipants(Type.CONTROLLER)) {
//					controllers.add(participant.getPhysicalEntity());
//				}
//			}
//			if(modulation != null) {
//				for(InteractionParticipant participant : modulation.getParticipants(Type.CONTROLLER)) {
//					controllers.add(participant.getPhysicalEntity());
//				}
//			}
//			return controllers;
//		}
//		
//		public int hashCode() {
//			return JavaUtil.hashCode(conversion) + JavaUtil.hashCode(catalysis) 
//			+ JavaUtil.hashCode(modulation);
//		}
//		
//		public boolean equals(Object o) {
//			if(o instanceof Process) {
//				Process op = (Process) o;
//				return JavaUtil.equals(conversion, op.getConversion()) && 
//				JavaUtil.equals(catalysis, op.getCatalysis()) && 
//				JavaUtil.equals(modulation, op.getModulation());
//			}
//			return false;
//		}
//	
//		public String getName() {
//			String name = BioPAXUtil.getName(conversion);
//			if(catalysis != null) {
//				List<PhysicalEntity> catalysts = 
//					catalysis.getParticipantPhysicalEntities(Type.CONTROLLER);
//				if(catalysts.isEmpty()) {
//					name = name + "_unknown";
//				} else {
//					for(PhysicalEntity catalyst : catalysts) {
//						name = name + "_" + BioPAXUtil.getName(catalyst);
//					}
//				}
//			}
//			if(modulation != null) {
//				List<PhysicalEntity> modulators = 
//					modulation.getParticipantPhysicalEntities(Type.CONTROLLER);
//				if(modulators.isEmpty()) {
//					name = name + "_unknown";
//				} else {
//					for(PhysicalEntity catalyst : modulators) {
//						name = name + "_" + BioPAXUtil.getName(catalyst);
//					}
//				}
//			}
//			return name;
//		}
//		
//	}
//	
//	public static Set<Process> getAllProcesses(PathwayModel pathwayModel, Conversion conversion) {
//		HashSet<Process> processes = new HashSet<Process>();
//		for(BioPaxObject parent : pathwayModel.getParents(conversion)) {
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
		ArrayList<String> nameList = entity.getName();
		if(!nameList.isEmpty()) {
			return (getSafetyName(nameList.get(0))).trim();
		}else{					
			return entity.getIDShort();
		}
	}
	
}
