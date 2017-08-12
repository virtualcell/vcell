/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import cbit.vcell.model.Model.StructureTopology;

/*  Useful static methods concerning structures
 *  November 2010
 */

public class StructureUtil {

	public static boolean isAdjacentMembrane(Membrane membrane, Feature feature, StructureTopology structTopology) {
		return structTopology.getInsideFeature(membrane) == feature || structTopology.getOutsideFeature(membrane) == feature;
	}
	
	public static boolean areAdjacent(Structure structure1, Structure structure2) {
		Model model1 = structure1.getModel();
		Model model2 = structure2.getModel();
		// This should never happen!
		if (!model1.compareEqual(model2)) {
			throw new RuntimeException("'" + structure1.getName() + "' & '" + structure2.getName() +"' are not in the same model!");
		}
		StructureTopology structTopology = model1.getStructureTopology();

		if(structure1 instanceof Membrane && structure2 instanceof Feature) {
			return isAdjacentMembrane((Membrane) structure1, (Feature) structure2, structTopology);
		} else if(structure2 instanceof Membrane && structure1 instanceof Feature) {
			return isAdjacentMembrane((Membrane) structure2, (Feature) structure1, structTopology);
		}
		return false;
	}
	
	public static boolean reactionHereCanHaveParticipantThere(Structure structureReaction,
			Structure structureSpeciesContext) {
			return true;
//		if(structureReaction == null || structureSpeciesContext == null) { return false; }
//		if(structureReaction == structureSpeciesContext) {
//			return true;
//		} 
//		Model model1 = structureReaction.getModel();
//		Model model2 = structureSpeciesContext.getModel();
//		// This should never happen!
//		if (!model1.compareEqual(model2)) {
//			throw new RuntimeException("Reaction and speciesContext are not in the same model!");
//		}
//		StructureTopology structTopology = model1.getStructureTopology();
//		if(structureReaction instanceof Membrane && structureSpeciesContext instanceof Feature) {
//			Membrane membraneReaction = (Membrane) structureReaction;
//			Feature featureSpeciesContext = (Feature) structureSpeciesContext;
//			return isAdjacentMembrane(membraneReaction, featureSpeciesContext, structTopology);
//		}
//		return false;
		} 
	
}
