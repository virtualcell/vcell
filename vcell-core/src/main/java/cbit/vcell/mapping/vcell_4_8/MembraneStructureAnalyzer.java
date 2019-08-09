/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.vcell_4_8;

import java.util.Vector;

import cbit.vcell.geometry.SubVolume;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.GeometryContext;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ReservedSymbolRole;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
/**
 * This type was created in VisualAge.
 */
class MembraneStructureAnalyzer extends StructureAnalyzer {
	private SubVolume innerSubVolume = null;
	private SubVolume outerSubVolume = null;
	private Membrane membrane = null;
	private ResolvedFlux resolvedFluxes[] = null;

	public static boolean bResolvedFluxCorrectionBug = false;
	public static boolean bResolvedFluxCorrectionBugExercised = false;
	public static boolean bNoFluxIfFixed = false;
	public static boolean bNoFluxIfFixedExercised = false;
/**
 * MembraneStructureAnalyzer constructor comment.
 * @param mathMapping_4_8 cbit.vcell.mapping.MathMapping
 * @param subVolume cbit.vcell.geometry.SubVolume
 */
MembraneStructureAnalyzer(MathMapping_4_8 mathMapping_4_8, Membrane membrane, SubVolume innerSubVolume, SubVolume outerSubVolume) {
	super(mathMapping_4_8);
	this.innerSubVolume = innerSubVolume;
	this.outerSubVolume = outerSubVolume;
	this.membrane = membrane;
	//refresh();
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.SubVolume
 */
SubVolume getInnerSubVolume() {
	return innerSubVolume;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Membrane
 */
Membrane getMembrane() {
	return membrane;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.SubVolume
 */
SubVolume getOuterSubVolume() {
	return outerSubVolume;
}
/**
 * This method was created in VisualAge.
 * @return UndefinedObject[]
 */
ResolvedFlux[] getResolvedFluxes() {
	return resolvedFluxes;
}
/**
 * This method was created in VisualAge.
 */
void refresh() {
	super.refresh();
	try {
		refreshResolvedFluxes();
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 */
void refreshResolvedFluxes() throws Exception {

//System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes()");

	GeometryContext geoContext = mathMapping_4_8.getSimulationContext().getGeometryContext();
	StructureTopology structTopology = mathMapping_4_8.getSimulationContext().getModel().getStructureTopology();
	Vector<ResolvedFlux> resolvedFluxList = new Vector<ResolvedFlux>();

	//
	// for each reaction, get all fluxReactions associated with this membrane
	//
	Vector<ReactionStep> fluxList = new Vector<ReactionStep>();
	ReactionSpec reactionSpecs[] = mathMapping_4_8.getSimulationContext().getReactionContext().getReactionSpecs();
	for (int j=0;j<reactionSpecs.length;j++){
		if (reactionSpecs[j].isExcluded()){
			continue;
		}
		ReactionStep rs = reactionSpecs[j].getReactionStep();
		if (rs.getStructure()==getMembrane()){
			if (rs instanceof FluxReaction){
				fluxList.addElement(rs);
			}
		}
	}

	//
	// for each FluxReaction, add fluxes to influx and subtract from outflux
	//
	for (int i=0;i<fluxList.size();i++){
		FluxReaction fr = (FluxReaction)fluxList.elementAt(i);
		Species fluxCarrier = null;
		for (ReactionParticipant rp : fr.getReactionParticipants()) {
			if (rp instanceof Reactant || rp instanceof Product) {
				if (fluxCarrier == null) {
					fluxCarrier = rp.getSpecies();
				} else {
					if (fluxCarrier != rp.getSpecies()) {
						throw new Exception("Flux reaction '" + fr.getName() + "' with multiple species not allowed in VCell 4.8.");
					}
				}
			}
		}
		if (fluxCarrier == null) {
			continue;
		}
		ResolvedFlux rf = null;
		for (int j=0;j<resolvedFluxList.size();j++){
			ResolvedFlux rf_tmp = (ResolvedFlux)resolvedFluxList.elementAt(j);
			if (rf_tmp.getSpecies() == fluxCarrier){
				rf = rf_tmp;
			}
		}
		//
		// if "inside" speciesContext is not "fixed", add flux to ResolvedFlux
		//
		SpeciesContext insideSpeciesContext = mathMapping_4_8.getSimulationContext().getModel().getSpeciesContext(fluxCarrier, structTopology.getInsideFeature(getMembrane()));
		SpeciesContextSpec insideSpeciesContextSpec = mathMapping_4_8.getSimulationContext().getReactionContext().getSpeciesContextSpec(insideSpeciesContext);
		//
		// introduce bug compatability mode for NoFluxIfFixed bug
		//
		//if (!insideSpeciesContextSpec.isConstant()){
		if (bNoFluxIfFixed || !insideSpeciesContextSpec.isConstant()){
			if (bNoFluxIfFixed && insideSpeciesContextSpec.isConstant()){
				bNoFluxIfFixedExercised = true;
			}
			if (rf == null){
				rf = new ResolvedFlux(fluxCarrier, fr.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getUnitDefinition());
				resolvedFluxList.addElement(rf);
			}
			FeatureMapping insideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping((structTopology.getInsideFeature((Membrane)fr.getStructure())));
			
			Expression residualVolumeFraction = mathMapping_4_8.getResidualVolumeFraction(insideFeatureMapping).renameBoundSymbols(mathMapping_4_8.getNameScope());
			Expression insideFluxCorrection = Expression.invert(residualVolumeFraction);
			//
			// introduce bug compatability mode for resolved flux correction
			//
			if (bResolvedFluxCorrectionBug && !residualVolumeFraction.compareEqual(new Expression(1.0))){
				bResolvedFluxCorrectionBugExercised = true;
				System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
				insideFluxCorrection = new Expression(1.0);
			}
			//
			// add flux term to ResolvedFlux.inFlux
			//
			if (fr.getKinetics() instanceof DistributedKinetics){
				Expression reactionRateParameter = new Expression(((DistributedKinetics)fr.getKinetics()).getReactionRateParameter(), mathMapping_4_8.getNameScope());
				if (rf.inFluxExpression.isZero()){
					rf.inFluxExpression = Expression.mult(reactionRateParameter,insideFluxCorrection).flatten();
				}else{
					rf.inFluxExpression = Expression.add(rf.inFluxExpression,Expression.mult(reactionRateParameter,insideFluxCorrection).flatten());
				}
			}else if (fr.getKinetics() instanceof LumpedKinetics){
				throw new RuntimeException("Lumped Kinetics for fluxes not yet supported");
			}else{
				throw new RuntimeException("unexpected Kinetic type in MembraneStructureAnalyzer.refreshResolvedFluxes()");
			}
//			rf.inFlux.bindExpression(mathMapping);
		}
		SpeciesContext outsideSpeciesContext = mathMapping_4_8.getSimulationContext().getModel().getSpeciesContext(fluxCarrier, structTopology.getOutsideFeature(getMembrane()));
		SpeciesContextSpec outsideSpeciesContextSpec = mathMapping_4_8.getSimulationContext().getReactionContext().getSpeciesContextSpec(outsideSpeciesContext);
		//
		// introduce bug compatability mode for NoFluxIfFixed bug
		//
		//if (!outsideSpeciesContextSpec.isConstant()){
		if (bNoFluxIfFixed || !outsideSpeciesContextSpec.isConstant()){
			if (bNoFluxIfFixed && outsideSpeciesContextSpec.isConstant()){
				bNoFluxIfFixedExercised = true;
			}
			if (rf == null){
				rf = new ResolvedFlux(fluxCarrier,  fr.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getUnitDefinition());
				resolvedFluxList.addElement(rf);
			}
			FeatureMapping outsideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(structTopology.getOutsideFeature((Membrane)fr.getStructure()));
			Expression residualVolumeFraction = mathMapping_4_8.getResidualVolumeFraction(outsideFeatureMapping).renameBoundSymbols(mathMapping_4_8.getNameScope());
			Expression outsideFluxCorrection = Expression.invert(residualVolumeFraction);
			//
			// introduce bug compatability mode for resolved flux correction
			//
			if (bResolvedFluxCorrectionBug && !residualVolumeFraction.compareEqual(new Expression(1.0))){
				bResolvedFluxCorrectionBugExercised = true;
				System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
				outsideFluxCorrection = new Expression(1.0);
			}
			//
			// sub flux term to resolvedFlux.outFlux
			//
			if (fr.getKinetics() instanceof DistributedKinetics){
				Expression reactionRateParameter = new Expression(((DistributedKinetics)fr.getKinetics()).getReactionRateParameter(), mathMapping_4_8.getNameScope());
				if (rf.outFluxExpression.isZero()){
					rf.outFluxExpression = Expression.mult(Expression.negate(reactionRateParameter),outsideFluxCorrection).flatten();
				}else{
					rf.outFluxExpression = Expression.add(rf.outFluxExpression,Expression.mult(Expression.negate(reactionRateParameter),outsideFluxCorrection).flatten());
				}
			}else if (fr.getKinetics() instanceof LumpedKinetics){
				throw new RuntimeException("Lumped Kinetics not yet supported for Flux Reaction: "+fr.getName());
			}else{
				throw new RuntimeException("unexpected Kinetics type for Flux Reaction "+fr.getName());
			}
//			rf.outFlux.bindExpression(mathMapping);
		}
	}
	
	//
	// for each reaction, incorporate all reactionSteps involving binding with volumetric species
	//
	double kMoleValue = Model.reservedConstantsMap.get(ReservedSymbolRole.KMOLE);
	for (int i=0;i<reactionSpecs.length;i++){
		if (reactionSpecs[i].isExcluded()){
			continue;
		}
		ReactionStep rs = reactionSpecs[i].getReactionStep();
		if (rs.getStructure()==getMembrane()){
			if (rs instanceof SimpleReaction){
				SimpleReaction sr = (SimpleReaction)rs;
				ReactionParticipant rp_Array[] = sr.getReactionParticipants();
				for (int k = 0; k < rp_Array.length; k++) {
					if (rp_Array[k] instanceof Reactant || rp_Array[k] instanceof Product){
						SpeciesContextSpec scs = mathMapping_4_8.getSimulationContext().getReactionContext().getSpeciesContextSpec(rp_Array[k].getSpeciesContext());
						//
						// for volume species that are not "fixed", add fluxes to "ResolvedFlux"
						//

						//
						// introduce bug compatability mode for NoFluxIfFixed bug
						//
						// if (rp_Array[k].getStructure() instanceof Feature && !scs.isConstant()){
						if (rp_Array[k].getStructure() instanceof Feature && (bNoFluxIfFixed || !scs.isConstant())){
							if (bNoFluxIfFixed && scs.isConstant()){
								bNoFluxIfFixedExercised = true;
							}
							//
							// for each Reactant or Product binding to this membrane...
							//

							//
							// get ResolvedFlux for this species
							//
							ResolvedFlux rf = null;
							for (int j=0;j<resolvedFluxList.size();j++){
								ResolvedFlux rf_tmp = (ResolvedFlux)resolvedFluxList.elementAt(j);
								if (rf_tmp.getSpecies() == rp_Array[k].getSpecies()){
									rf = rf_tmp;
								}
							}
							if (rf == null){
								rf = new ResolvedFlux(rp_Array[k].getSpecies(), sr.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getUnitDefinition());
								resolvedFluxList.addElement(rf);
							}
							
							Expression reactionRateExpression = getReactionRateExpression(sr,rp_Array[k]).renameBoundSymbols(mathMapping_4_8.getNameScope());
							if (rp_Array[k].getStructure() == structTopology.getInsideFeature(getMembrane())){
								//
								// for binding on inside, add to ResolvedFlux.inFlux
								//
								FeatureMapping insideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(structTopology.getInsideFeature(getMembrane()));
								Expression residualVolumeFraction = mathMapping_4_8.getResidualVolumeFraction(insideFeatureMapping).renameBoundSymbols(mathMapping_4_8.getNameScope());
								Expression insideFluxCorrection = Expression.div(new Expression(kMoleValue), residualVolumeFraction).flatten();
								//
								// introduce bug compatability mode for resolved flux correction
								//
								if (bResolvedFluxCorrectionBug && !residualVolumeFraction.compareEqual(new Expression(1.0))){
									bResolvedFluxCorrectionBugExercised = true;
									System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
									insideFluxCorrection = new Expression(kMoleValue);
								}
								if (rf.inFluxExpression.isZero()){
									rf.inFluxExpression = Expression.mult(insideFluxCorrection, reactionRateExpression);
								}else{
									rf.inFluxExpression = Expression.add(rf.inFluxExpression,Expression.mult(insideFluxCorrection, reactionRateExpression));
								}
//								rf.inFlux.bindExpression(mathMapping);
							}else if (rp_Array[k].getStructure() == structTopology.getOutsideFeature(getMembrane())){
								//
								// for binding on outside, add to ResolvedFlux.outFlux
								//
								FeatureMapping outsideFeatureMapping = (FeatureMapping)geoContext.getStructureMapping(structTopology.getOutsideFeature(getMembrane()));
								Expression residualVolumeFraction = mathMapping_4_8.getResidualVolumeFraction(outsideFeatureMapping).renameBoundSymbols(mathMapping_4_8.getNameScope());
								Expression outsideFluxCorrection = Expression.div(new Expression(kMoleValue), residualVolumeFraction).flatten();
								//
								// introduce bug compatability mode for resolved flux correction
								//
								if (bResolvedFluxCorrectionBug && !residualVolumeFraction.compareEqual(new Expression(1.0))){
									bResolvedFluxCorrectionBugExercised = true;
									System.out.println("MembraneStructureAnalyzer.refreshResolvedFluxes() ... 'ResolvedFluxCorrection' bug compatability mode");
									outsideFluxCorrection = new Expression(kMoleValue);
								}
								if (rf.outFluxExpression.isZero()){
									rf.outFluxExpression = Expression.mult(outsideFluxCorrection, reactionRateExpression);
								}else{
									rf.outFluxExpression = Expression.add(rf.outFluxExpression,Expression.mult(outsideFluxCorrection, reactionRateExpression));
								}
//								rf.outFlux.bindExpression(mathMapping);
							}else{
								throw new Exception("SpeciesContext "+rp_Array[k].getSpeciesContext().getName()+" doesn't border membrane "+getMembrane().getName()+" but reacts there");
							}
						}
					}
				}					
			}
		}
	}

	
	//
	// copy Vector into resolvedFluxes[] array
	//
	if (resolvedFluxList.size()>0){
		resolvedFluxes = new ResolvedFlux[resolvedFluxList.size()];
		resolvedFluxList.copyInto(resolvedFluxes);
	}else{
		resolvedFluxes=null;
	}
}
/**
 * Build list of structures (just one membrane) that are mapped to this volume subdomain
 */
protected void refreshStructures() {
	structures = new Structure[1];
	structures[0] = membrane;
}
}
