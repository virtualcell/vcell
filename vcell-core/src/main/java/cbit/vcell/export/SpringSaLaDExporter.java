/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.export;

import java.io.IOException;
import java.util.List;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.SpeciesPattern;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.solver.Simulation;

public class SpringSaLaDExporter {
	
	/* ********** Strings to represent the different object categories ****/
	public final static String SPATIAL_INFORMATION = "SYSTEM INFORMATION";
	public final static String TIME_INFORMATION = "TIME INFORMATION";
	public final static String MOLECULES = "MOLECULES";
	public final static String MOLECULE_FILES = "MOLECULE FILES";
	public final static String DECAY_REACTIONS = "CREATION/DECAY REACTIONS";
	public final static String TRANSITION_REACTIONS = "STATE TRANSITION REACTIONS";
	public final static String ALLOSTERIC_REACTIONS = "ALLOSTERIC REACTIONS";
	public final static String BINDING_REACTIONS = "BIMOLECULAR BINDING REACTIONS";
	public final static String MOLECULE_COUNTERS = "MOLECULE COUNTERS";
	public final static String STATE_COUNTERS = "STATE COUNTERS";
	public final static String BOND_COUNTERS = "BOND COUNTERS";
	public final static String SITE_PROPERTY_COUNTERS = "SITE PROPERTY COUNTERS";
	public final static String CLUSTER_COUNTERS = "CLUSTER COUNTERS";
	public final static String SYSTEM_ANNOTATION = "SYSTEM ANNOTATIONS";
	public final static String MOLECULE_ANNOTATIONS = "MOLECULE ANNOTATIONS";
	public final static String REACTION_ANNOTATIONS = "REACTION ANNOTATIONS";

	public final static String DEFAULT_FOLDER = "Default Folder";
	private String systemName;		// The system name (same as the file name, usually)
	public final static String DEFAULT_SYSTEM_NAME = "New Model";

	String sFile;
	int ssldMajor;
	int ssldMinor;

	public SpringSaLaDExporter(String sFile, int ssldMajor, int ssldMinor) {
		this.sFile = sFile;
		this.ssldMajor = ssldMajor;
		this.ssldMinor = ssldMinor;
	}

	public String getDocumentAsString(BioModel bioModel, SimulationContext simContext) {
		
		Model model = bioModel.getModel();
		Structure[] structures = model.getStructures();
		SpeciesContext[] speciesContexts = model.getSpeciesContexts();
		List<MolecularType> molecularTypeList = model.getRbmModelContainer().getMolecularTypeList();
		List<ReactionRule> reactionRuleList = model.getRbmModelContainer().getReactionRuleList();
		
		Simulation simulation = simContext.getSimulations(0);
		Geometry geometry = simContext.getGeometry();
		GeometryContext geometryContext = simContext.getGeometryContext();
		GeometrySpec geometrySpec = geometry.getGeometrySpec();
		ReactionContext reactionContext = simContext.getReactionContext();
		SpeciesContextSpec[] speciesContextSpecs = reactionContext.getSpeciesContextSpecs();
		ReactionSpec[] reactionSpecs = reactionContext.getReactionSpecs();
		ReactionRuleSpec[] reactionRuleSpecs = reactionContext.getReactionRuleSpecs();
		
		try {
			reactionContext.convertSpeciesIniCondition(false);	// convert to count if currently is concentration
			
			StringBuilder sb = new StringBuilder();
			/* ********* BEGIN BY WRITING THE TIMES *********/
			sb.append("*** " + TIME_INFORMATION + " ***");
			sb.append("\n");
			simulation.writeData(sb);	// TODO: need proper sim
			sb.append("\n");

			/* ********* WRITE THE SPATIAL INFORMATION **********/
			sb.append("*** " + SPATIAL_INFORMATION + " ***");
			sb.append("\n");
			geometryContext.writeData(sb);	// TODO: need geometry
			sb.append("\n");

			/* ******* WRITE THE SPECIES INFORMATION ***********/
			sb.append("*** " + MOLECULES + " ***");
			sb.append("\n");
			sb.append("\n");
			for(SpeciesContext sc : model.getSpeciesContexts()) {
				SpeciesContextSpec scs = reactionContext.getSpeciesContextSpec(sc);
				scs.writeData(sb);
			}
			/* ******* WRITE THE SPECIES INFORMATION ***********/
			sb.append("*** " + MOLECULE_FILES + " ***");
			sb.append("\n");
			sb.append("\n");
			for(SpeciesContext sc : model.getSpeciesContexts()) {
				SpeciesContextSpec scs = reactionContext.getSpeciesContextSpec(sc);
				sb.append("MOLECULE: " + sc.getName() + " " + scs.getFilename());
				sb.append("\n");
			}
			sb.append("\n");

			/* ******* WRITE THE DECAY REACTIONS ***************/
			sb.append("*** " + DECAY_REACTIONS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(Molecule molecule : molecules) {
//				sb.append(molecule.getDecayReaction().writeReaction());
//				sb.append("\n");
//			}
			sb.append("\n");

			/* ******* WRITE THE TRANSITION REACTIONS **********/
			sb.append("*** " + TRANSITION_REACTIONS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(TransitionReaction reaction : transitionReactions) {
//				sb.append(reaction.writeReaction());
//				sb.append("\n");
//			}
			sb.append("\n");

			/* ******* WRITE THE ALLOSTERIC REACTIONS **********/
			sb.append("*** " + ALLOSTERIC_REACTIONS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(AllostericReaction reaction: allostericReactions) {
//				sb.append(reaction.writeReaction());
//				sb.append("\n");
//			}
			sb.append("\n");

			/* ******* WRITE THE BINDING REACTIONS ************/
			sb.append("*** " + BINDING_REACTIONS + " ***");
			sb.append("\n");
			sb.append("\n");
			
			for(ReactionRuleSpec rrs : reactionRuleSpecs) {
				if(rrs.isExcluded()) {
					continue;
				}
//				sb.append(reaction.writeReaction());
				rrs.writeData(sb);							// TODO: BINDING REACTION
			}
			sb.append("\n");

			/* ****** WRITE THE MOLECULE COUNTERS **********/
			sb.append("*** " + MOLECULE_COUNTERS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(Molecule molecule: molecules) {
//				molecule.getMoleculeCounter().writeMoleculeCounter(sb);
//			}
			sb.append("\n");

			/* ******  WRITE THE STATE COUNTERS *************/
			sb.append("*** " + STATE_COUNTERS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(Molecule molecule : molecules) {
//				for(SiteType type : molecule.getTypeArray()) {
//					for(State state : type.getStates()) {
//						state.getStateCounter().writeStateCounter(sb);
//					}
//				}
//			}
			sb.append("\n");

			/* ***** WRITE THE BOND COUNTERS ***************/
			sb.append("*** " + BOND_COUNTERS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(BindingReaction reaction: bindingReactions) {
//				reaction.getBondCounter().writeBondCounter(sb);
//			}
			sb.append("\n");

			/* ********  WRITE THE SITE PROPERTY COUNTERS ************/
			sb.append("*** " + SITE_PROPERTY_COUNTERS + " ***");
			sb.append("\n");
			sb.append("\n");
//			for(Molecule molecule : molecules) {
//				ArrayList<Site> sites = molecule.getSiteArray();
//				for(Site site : sites) {
//					site.getPropertyCounter().writeSitePropertyCounter(sb);
//				}
//			}
			sb.append("\n");

			/* *************** WRITE THE TRACK CLUSTERS BOOLEAN ***********/
			sb.append("*** " + CLUSTER_COUNTERS + " ***");
			sb.append("\n");
			sb.append("\n");
			sb.append("Track_Clusters: " + SpeciesContextSpec.TrackClusters);
			sb.append("\n");
			sb.append("\n");

			/* ****** WRITE THE SYSTEM ANNOTATION ********************/
			sb.append("*** " + SYSTEM_ANNOTATION + " ***");
			sb.append("\n");
			sb.append("\n");
//			systemAnnotation.printAnnotation(sb);
			sb.append("\n");

			/* ****** WRITE THE MOLECULE ANNOTATIONS *****************/
			sb.append("*** " + MOLECULE_ANNOTATIONS + " ***");
			sb.append("\n");
			sb.append("\n");
//			writeMoleculeAnnotations(sb);
			sb.append("\n");

			/* ****** WRITE THE REACTION ANNOTATIONS *****************/
			sb.append("*** " + REACTION_ANNOTATIONS + " ***");
			sb.append("\n");
			sb.append("\n");
//			writeReactionAnnotations(sb);
			sb.append("\n");

			String ret = sb.toString();
			System.out.println(ret);
			return ret;
		} catch(Exception ex) {
			ex.printStackTrace(System.out);
		}
		return null;
	}

	public void writeDocumentStringToFile(String resultString, String ssldFileName, boolean b) {
		// make directory for the whole project if needed
		// save just the biomodel string as main project file
		// MainGUI.java  saveItemActionPerformed()
		
	}

}
