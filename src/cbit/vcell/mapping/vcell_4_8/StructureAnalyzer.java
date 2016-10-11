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

import org.vcell.util.BeanUtils;

import cbit.vcell.mapping.DiffusionReactionStep;
import cbit.vcell.mapping.EventReactionStep;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextMapping;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.matrix.RationalMatrix;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.matrix.RationalNumberMatrix;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
/**
 * class StructureAnalyzer
 * 
 * Create full matrix for a subdomain, fluxes (either binding or true fluxes) are
 * decoupled to maintain integer matrix elements
 *  
 * This class fills in the SpeciesContextMapping objects with the kinetics and dependency relationships
 *
 */
abstract class StructureAnalyzer {

 	//
 	// fundamental information about system (sim context stuff)
 	//
	protected MathMapping_4_8 mathMapping_4_8 = null;

	//
	// working list of reactions and speciesContexts for indexing
	//
	// see refreshSpeciesContextMapping()
	//
	// order of speciesContextMapping array is ordered as [fast-non-diffusable , non-diffusable , diffusible ]
	//
	protected Structure             structures[] = null;
	protected ReactionStep          reactionSteps[] = null;
	protected SpeciesContextMapping speciesContextMappings[] = null;
	
	//
	// matrices needed for structural analysis
	//
	private RationalMatrix totalSchemeMatrix = null;
	private RationalMatrix totalNullSpaceMatrix = null;
	
	//
	// working list of fast reactions and associated speciesContexts for indexing
	//
	protected ReactionStep          fastReactionSteps[] = null;
	protected SpeciesContextMapping fastSpeciesContextMappings[] = null;
	
	//
	// matrices needed for fast system analysis
	//
	private RationalMatrix fastSchemeMatrix = null;
	private RationalMatrix fastNullSpaceMatrix = null;

	public static class Dependency {
		SpeciesContextMapping speciesContextMapping = null;
		String invariantSymbolName = null;
		Expression conservedMoietyExpression = null;
		Expression dependencyExpression = null;
	};

/**
 * StructureAnalyzer constructor comment.
 */
StructureAnalyzer(MathMapping_4_8 mathMapping_4_8) {
	this.mathMapping_4_8 = mathMapping_4_8;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.SpeciesContextMapping
 */
SpeciesContextMapping[] getFastSpeciesContextMappings() {
	return fastSpeciesContextMappings;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.ReactionStep[]
 */
ReactionStep[] getReactionSteps() {
	return reactionSteps;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.SpeciesContextMapping[]
 */
SpeciesContextMapping[] getSpeciesContextMappings() {
	return speciesContextMappings;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.model.Structure[]
 */
Structure[] getStructures() {
	return structures;
}


/**
 * This method was created in VisualAge.
 * @param o java.util.Observable
 * @param obj java.lang.Object
 */
void refresh() {
	try {
//System.out.println("StructureAnalyzer.refresh()");
		refreshStructures();
		refreshTotalSpeciesContextMappings();
		if (speciesContextMappings!=null && reactionSteps!=null){
			refreshTotalMatrices();
			refreshTotalDependancies();
		}
		refreshFastSpeciesContextMappings();
		if (fastSpeciesContextMappings!=null){
			refreshFastMatrices();
			refreshFastSystem();
			substituteIntoFastSystem();
		}
	} catch (Exception e){
		System.out.println("Exception caught in StructureAnalyzer.update()");
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * This method was created in VisualAge.
 */
private void refreshFastMatrices() throws Exception {

//System.out.println("StructureAnalyzer.refreshFastMatrices()");
	//
	// update scheme matrix for fast system
	//
	fastSchemeMatrix = new RationalNumberMatrix(fastSpeciesContextMappings.length,fastReactionSteps.length);
	for (int i=0;i<fastSpeciesContextMappings.length;i++){
		for (int j=0;j<fastReactionSteps.length;j++){
			fastSchemeMatrix.set_elem(i,j,fastReactionSteps[j].getStoichiometry(fastSpeciesContextMappings[i].getSpeciesContext()));
		}
	}

	//
	// initialize rate expressions for speciesContext's due to scheme matrix
	//
	for (int i=0;i<fastSpeciesContextMappings.length;i++){
		SpeciesContextMapping scm = fastSpeciesContextMappings[i];
		SpeciesContext sc = scm.getSpeciesContext();
		//
		// collect fast rate expression
		//
		Expression exp = new Expression(0.0);
		for (int j=0;j<fastReactionSteps.length;j++){
			int stoichiometry = fastReactionSteps[j].getStoichiometry(sc);
			ReactionSpec reactionSpec = mathMapping_4_8.getSimulationContext().getReactionContext().getReactionSpec(fastReactionSteps[j]);
			if (stoichiometry != 0){
				if (!reactionSpec.isFast()){
					throw new Exception("expected only fast rates");
				}
				if (reactionSpec.isExcluded()){
					throw new Exception("expected only included rates");
				}
				ReactionParticipant[] rps = fastReactionSteps[j].getReactionParticipants();
				ReactionParticipant rp0 = null;
				for (ReactionParticipant rp : rps) {
					if (rp.getSpeciesContext() == sc) {
						rp0 = rp;
						break;
					}
				}
				//
				// if reaction is on membrane and reactionParticipant isn't, then add flux correction
				//
				if (rp0 != null) {
					Structure structure = fastReactionSteps[j].getStructure();
					Expression fastRateExpression = getReactionRateExpression(fastReactionSteps[j],rp0).renameBoundSymbols(mathMapping_4_8.getNameScope());
					if ((structure instanceof Membrane) && (rp0.getStructure()!=structure)){
						Membrane membrane = (Membrane)structure;
						MembraneMapping membraneMapping = (MembraneMapping)mathMapping_4_8.getSimulationContext().getGeometryContext().getStructureMapping(membrane);
						Expression fluxCorrection = new Expression(mathMapping_4_8.getFluxCorrectionParameter(membraneMapping,(Feature)rp0.getStructure()), mathMapping_4_8.getNameScope());
						exp = Expression.add(exp,Expression.mult(fluxCorrection, fastRateExpression));
					}else{
						exp = Expression.add(exp,new Expression(fastRateExpression));
					}
				}
			}
		}
//		exp.bindExpression(mathMapping);
		scm.setFastRate(exp.flatten());
	}	

//	System.out.println("StructureAnalyzer.refreshFastMatrices(), scheme matrix:");
//	fastSchemeMatrix.show();
	
	//
	// update null space matrix
	//
	fastNullSpaceMatrix = fastSchemeMatrix.findNullSpace();

//	if (fastNullSpaceMatrix==null){
//		System.out.println("fast system has full rank");
//	}else{
//		System.out.println("StructureAnalyzer.refreshFastMatrices(), nullSpace matrix:");
//		fastNullSpaceMatrix.show();
//	}
}


/**
 * This method was created in VisualAge.
 */
private void refreshFastSpeciesContextMappings() {

//System.out.println("StructureAnalyzer.refreshFastSpeciesContextMappings()");

	//GeometryContext geoContext = mathMapping.getSimulationContext().getGeometryContext();
	Vector<SpeciesContextMapping> scFastList = new Vector<SpeciesContextMapping>();
	//
	// for each structure, get all independent, non-constant speciesContext's
	//
	if (structures == null){
		return;
	}
	for (int i=0;i<structures.length;i++){
		SpeciesContext speciesContexts[] = mathMapping_4_8.getSimulationContext().getReactionContext().getModel().getSpeciesContexts(structures[i]);
		for (int j=0;j<speciesContexts.length;j++){
			SpeciesContext sc = speciesContexts[j];
			SpeciesContextMapping scm = mathMapping_4_8.getSpeciesContextMapping(sc);
			SpeciesContextSpec scs = mathMapping_4_8.getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);
			if (scm.getDependencyExpression()==null && scs.isConstant()==false){   // right now all independent vars
				scFastList.addElement(scm);
			}
		}
	}
	if (scFastList.size()>0){
		fastSpeciesContextMappings = new SpeciesContextMapping[scFastList.size()];
		scFastList.copyInto(fastSpeciesContextMappings);
//for (int i=0;i<fastSpeciesContextMappings.length;i++){
//System.out.println("fastSpeciesContextMappings["+i+"] = "+fastSpeciesContextMappings[i].getSpeciesContext().getName());
//}
	}else{
		fastSpeciesContextMappings=null;
	}
//System.out.println("StructureAnalyzer.refreshFastSpeciesContextMapping(), fastSpeciesContextMappings.length = "+scFastList.size());
	
	//
	// for each reaction, get all reactionSteps associated with these structures
	//
	Vector<ReactionStep> rsFastList = new Vector<ReactionStep>();
	ReactionSpec reactionSpecs[] = mathMapping_4_8.getSimulationContext().getReactionContext().getReactionSpecs();
	for (int i=0;i<reactionSpecs.length;i++){
		ReactionStep rs = reactionSpecs[i].getReactionStep();
		if (reactionSpecs[i].isExcluded()){
			continue;
		}
		for (int j=0;j<structures.length;j++){
			if (rs.getStructure()==structures[j]){
				if (reactionSpecs[i].isFast()){
					rsFastList.addElement(rs);
				}
			}
		}
	}
	if (rsFastList.size()>0){
		fastReactionSteps = new ReactionStep[rsFastList.size()];
		rsFastList.copyInto(fastReactionSteps);
	}else{
		fastReactionSteps=null;
		fastSpeciesContextMappings=null;
	}
//System.out.println("StructureAnalyzer.refreshFastSpeciesContextMapping(), reactionSteps.length = "+scFastList.size());
}


/**
 * Find which species are fast-independent, use their rates as the "fastRate"s.
 * null space rows correspond to "fastInvariant".
 */
private void refreshFastSystem() throws Exception {

	//
	// reset fast system characteristics for all species contexts
	//
	for (int v=0;v<fastSpeciesContextMappings.length;v++){
		fastSpeciesContextMappings[v].setFastInvariant(null);
	}

	if (fastNullSpaceMatrix == null){
		//System.out.println("the fast matrix has full rank, there are no fast invariants");
		return;
	}
		
	if (fastSpeciesContextMappings.length != fastNullSpaceMatrix.getNumCols()){
		throw new Exception("varName array not same dimension as b matrix");
	}
				
	StructureAnalyzer.Dependency[] dependencies = refreshTotalDependancies(fastNullSpaceMatrix,fastSpeciesContextMappings,mathMapping_4_8,true);

	for (int i = 0; i < dependencies.length; i++){
		//String constantName = dependencies[i].invariantSymbolName;
		Expression constantExp = dependencies[i].conservedMoietyExpression;
		SpeciesContextMapping firstSCM = dependencies[i].speciesContextMapping;
		//Expression exp = dependencies[i].dependencyExpression;
		
		//
		// store fastInvariant (e.g. (K_fast_total= xyz + wzy)
		//
		constantExp.bindExpression(mathMapping_4_8);
		firstSCM.setFastInvariant(constantExp.flatten());
	}	

}


/**
 * This method was created in VisualAge.
 */
protected abstract void refreshStructures();


/**
 * This method was created by a SmartGuide.
 * @param b cbit.vcell.math.Matrix
 * @param vars java.lang.String[]
 */
private void refreshTotalDependancies() throws Exception {

//System.out.println("StructureAnalyzer.refreshTotalDependancies()");
	//SimulationContext simContext = mathMapping.getSimulationContext();

	//
	// reset dependancy relationships for all species contexts
	//
	for (int v=0;v<speciesContextMappings.length;v++){
		speciesContextMappings[v].setDependencyExpression(null);
	}

	if (totalNullSpaceMatrix == null){
		//System.out.println("the matrix has full rank, there are no dependencies");
		return;
	}
		
	if (speciesContextMappings.length != totalNullSpaceMatrix.getNumCols()){
		throw new Exception("varName array not same dimension as b matrix");
	}
				
	StructureAnalyzer.Dependency[] dependencies = refreshTotalDependancies(totalNullSpaceMatrix,speciesContextMappings,mathMapping_4_8,false);
	
	VCUnitDefinition totalMassUnit = mathMapping_4_8.getSimulationContext().getModel().getUnitSystem().getVolumeConcentrationUnit();
	for (int i = 0; i < dependencies.length; i++){
		String constantName = dependencies[i].invariantSymbolName;
		Expression constantExp = dependencies[i].conservedMoietyExpression;
		SpeciesContextMapping firstSCM = dependencies[i].speciesContextMapping;
		Expression exp = dependencies[i].dependencyExpression;
		//
		// store totalMass parameter (e.g. K_xyz_total = xyz_init + wzy_init) 
		//
		MathMapping_4_8.MathMappingParameter totalMassParameter = mathMapping_4_8.addMathMappingParameter(constantName,constantExp.flatten(),MathMapping_4_8.PARAMETER_ROLE_TOTALMASS, totalMassUnit);
		//
		// store dependency parameter (e.g. xyz = K_xyz_total - wzy)
		//
		exp.bindExpression(mathMapping_4_8);
		firstSCM.setDependencyExpression(exp);
	}	
}


/**
 * This method was created by a SmartGuide.
 * @param b cbit.vcell.math.Matrix
 * @param vars java.lang.String[]
 */
static StructureAnalyzer.Dependency[] refreshTotalDependancies(RationalMatrix nullSpaceMatrix, SpeciesContextMapping[] speciesContextMappings, 
		MathMapping_4_8 argMathMapping, boolean bFast) throws Exception {

//System.out.println("StructureAnalyzer.refreshTotalDependancies()");
	SimulationContext argSimContext = argMathMapping.getSimulationContext();

	//
	// reset dependancy relationships for all species contexts
	//
	//for (int v=0;v<speciesContextMappings.length;v++){
		//speciesContextMappings[v].setDependencyExpression(null);
	//}

	if (nullSpaceMatrix == null){
		//System.out.println("the matrix has full rank, there are no dependencies");
		return new StructureAnalyzer.Dependency[0];
	}
		
	if (speciesContextMappings.length != nullSpaceMatrix.getNumCols()){
		//throw new Exception("varName array not same dimension as b matrix");
		System.out.println("varName array not same dimension as b matrix");
		nullSpaceMatrix.show();
		for (int i = 0; i < speciesContextMappings.length; i++){
			System.out.println("scm["+i+"] is "+speciesContextMappings[i].getSpeciesContext().getName());
		}
	}
				
//System.out.println("there are "+nullSpaceMatrix.rows+" dependencies");

	Vector<Dependency> dependencyList = new Vector<Dependency>();
	
	for (int i=0;i<nullSpaceMatrix.getNumRows();i++){
		//
		// find first variable
		//
		Expression exp = null;
		Expression constantExp = null;
		String     constantName = null;
		SpeciesContextMapping firstSCM = null;
		boolean bFirst = true;
		for (int j=0;j<nullSpaceMatrix.getNumCols();j++){
			RationalNumber coeff = nullSpaceMatrix.get_elem(i,j);
			if (coeff.doubleValue() != 0.0){
				if (bFirst){
					if (coeff.doubleValue() != 1.0){
						System.out.println("i="+i+" j="+j);
						nullSpaceMatrix.show();
						throw new Exception("expecting a coefficient of 1.0, instead coeff = "+coeff.infix());
					}
					firstSCM = speciesContextMappings[j];
					//
					// first term of dependancy expression   ("K_CalciumCyt")
					//
					SpeciesContext firstSC = firstSCM.getSpeciesContext();
					constantName = "K_"+firstSC.getName()+"_total";
					exp = new Expression(constantName);
					//
					// first term of K expression
					//
					StructureMapping firstSM = argSimContext.getGeometryContext().getStructureMapping(firstSC.getStructure());
					SpeciesContextSpec firstSCS = argSimContext.getReactionContext().getSpeciesContextSpec(firstSC);
					SymbolTableEntry scSTE = null;
					if (bFast){
						scSTE = firstSCS.getSpeciesContext();
					}else{
						scSTE = firstSCS.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
					}
					constantExp = Expression.mult(new Expression(coeff),firstSM.getNormalizedConcentrationCorrection(argSimContext,argMathMapping,argMathMapping),
																new Expression(scSTE, argMathMapping.getNameScope()));
					bFirst = false;
				}else{
					//
					// add term to dependancy expression     (" - 2*IP3Cyt ")
					//
					SpeciesContextMapping scm = speciesContextMappings[j];
					SpeciesContext sc = scm.getSpeciesContext();
					StructureMapping sm = argSimContext.getGeometryContext().getStructureMapping(sc.getStructure());
					SpeciesContextSpec scs = argSimContext.getReactionContext().getSpeciesContextSpec(sc);
					exp = Expression.add(exp,Expression.negate(Expression.mult(new Expression(coeff), 
							sm.getNormalizedConcentrationCorrection(argSimContext,argMathMapping,argMathMapping), new Expression(sc, argMathMapping.getNameScope()))));
					//
					// add term to K expression
					//
					SymbolTableEntry scSTE = null;
					if (bFast){
						scSTE = sc;
					}else{
						scSTE = scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
					}
					constantExp = Expression.add(constantExp, Expression.mult(new Expression(coeff), sm.getNormalizedConcentrationCorrection(argSimContext,argMathMapping,argMathMapping),
																				new Expression(scSTE, argMathMapping.getNameScope())));
				}
			}
		}
		if (firstSCM!=null){
			//
			// store totalMass parameter (e.g. K_xyz_total = xyz_init + wzy_init) 
			//
			//MathMapping.MathMappingParameter totalMassParameter = mathMapping.addMathMappingParameter(constantName,constantExp.flatten(),MathMapping.PARAMETER_ROLE_TOTALMASS,cbit.vcell.units.VCUnitDefinition.UNIT_uM);
			//
			// store dependency parameter (e.g. xyz = K_xyz_total - wzy)
			//
			StructureMapping sm = argSimContext.getGeometryContext().getStructureMapping(firstSCM.getSpeciesContext().getStructure());
			exp = Expression.mult(exp,Expression.invert(sm.getNormalizedConcentrationCorrection(argSimContext,argMathMapping,argMathMapping)));
			exp = exp.flatten();
			//exp.bindExpression(mathMapping_temp);
			//firstSCM.setDependencyExpression(exp);
			
			Dependency dependency = new Dependency();
			dependency.dependencyExpression = exp;
			dependency.speciesContextMapping = firstSCM;
			dependency.invariantSymbolName = constantName;
			dependency.conservedMoietyExpression = constantExp.flatten();

			dependencyList.add(dependency);
		}
	}
	return (StructureAnalyzer.Dependency[])BeanUtils.getArray(dependencyList,StructureAnalyzer.Dependency.class);
}


/**
 * This method was created in VisualAge.
 */
private void refreshTotalMatrices() throws Exception {

//System.out.println("StructureAnalyzer.refreshTotalMatrices()");
	//
	// update scheme matrix for full system (slow and fast)
	//
	ReactionSpec[] reactionSpecs = new ReactionSpec[reactionSteps.length];
	for (int j=0;j<reactionSteps.length;j++){
		reactionSpecs[j] = mathMapping_4_8.getSimulationContext().getReactionContext().getReactionSpec(reactionSteps[j]);
	}
	//
	// initialize rate expressions for speciesContext's due to scheme matrix
	//
	totalSchemeMatrix = new RationalNumberMatrix(speciesContextMappings.length,reactionSteps.length);
	for (int i=0;i<speciesContextMappings.length;i++){
		SpeciesContextMapping scm = speciesContextMappings[i];
		SpeciesContext sc = scm.getSpeciesContext();
		//
		// collect slow rate expression (fast handled by FastSystem)
		//
		Expression exp = new Expression(0.0);
		for (int j=0;j<reactionSteps.length;j++){
			int stoichiometry = reactionSteps[j].getStoichiometry(sc);
			
			totalSchemeMatrix.set_elem(i,j,stoichiometry);
			
			if (stoichiometry != 0){
				if (!(reactionSteps[j] instanceof DiffusionReactionStep) && !(reactionSteps[j] instanceof EventReactionStep) &&
					!reactionSpecs[j].isFast() && !reactionSpecs[j].isExcluded()){
					ReactionParticipant[] rps1 = reactionSteps[j].getReactionParticipants();
					ReactionParticipant rp0 = null;
					for (ReactionParticipant rp : rps1) {
						if (rp.getSpeciesContext() == sc) {
							rp0 = rp;
							break;
						}
					}
					Structure structure = reactionSteps[j].getStructure();
					//
					// if reaction is in one compartment and reactionParticipant is in another, then add a flux correction.
					// (e.g. if reaction is on membrane and reactionParticipant is in a feature)
					//
					if (rp0 != null) {
						Expression reactRateExp = getReactionRateExpression(reactionSteps[j],rp0).renameBoundSymbols(mathMapping_4_8.getNameScope());
						if ((structure instanceof Membrane) && (sc.getStructure()!=structure)){
							Membrane membrane = (Membrane)structure;
							MembraneMapping membraneMapping = (MembraneMapping)mathMapping_4_8.getSimulationContext().getGeometryContext().getStructureMapping(membrane);
							Parameter fluxCorrectionParameter = mathMapping_4_8.getFluxCorrectionParameter(membraneMapping,(Feature)sc.getStructure());
							Expression fluxCorrection = new Expression(fluxCorrectionParameter, mathMapping_4_8.getNameScope());
							if (reactionSteps[j] instanceof FluxReaction){
								exp = Expression.add(exp, Expression.mult(fluxCorrection, reactRateExp));
								//Expression.add(exp,new Expression(fluxCorrectionParameterSymbolName+"*"+expInfix));
							}else if (reactionSteps[j] instanceof SimpleReaction){
								ModelUnitSystem unitSystem = mathMapping_4_8.getSimulationContext().getModel().getUnitSystem();
								Expression unitFactor = mathMapping_4_8.getUnitFactor(unitSystem.getVolumeSubstanceUnit().divideBy(unitSystem.getMembraneSubstanceUnit()));
								exp = Expression.add(exp, Expression.mult(fluxCorrection, unitFactor, reactRateExp));
//								exp = Expression.add(exp,new Expression(fluxCorrectionParameterSymbolName+"*"+ReservedSymbol.KMOLE.getName()+"*"+expInfix));
							}else{
								throw new RuntimeException("Internal Error: expected ReactionStep "+reactionSteps[j]+" to be of type SimpleReaction or FluxReaction");
							}
						}else{
							exp = Expression.add(exp, reactRateExp);
						}
					}
				}
			}
		}
//		exp.bindExpression(mathMapping);
		scm.setRate(exp.flatten());
	}	
//System.out.println("StructureAnalyzer.refreshTotalMatrices(), reactionSteps");
//for (int i=0;i<reactionSteps.length;i++){
//	System.out.println("reactionStep["+i+"] = "+reactionSteps[i].toString());
//}
	
//	System.out.println("StructureAnalyzer.refreshTotalMatrices(), scheme matrix:");
//	totalSchemeMatrix.show();
	
	//
	// update null space matrix
	//
	if (totalSchemeMatrix.getNumRows()>1){
		totalNullSpaceMatrix = (RationalMatrix)totalSchemeMatrix.findNullSpace();
	}else{
		totalNullSpaceMatrix = null;
	}
		

//	if (totalNullSpaceMatrix==null){
//		System.out.println("total system has full rank");
//	}else{
//		System.out.println("StructureAnalyzer.refreshTotalMatrices(), nullSpace matrix:");
//		totalNullSpaceMatrix.show();
//	}
}


/**
 * This method was created in VisualAge.
 */
private void refreshTotalSpeciesContextMappings() throws java.beans.PropertyVetoException {

	if (structures == null){
		return;
	}
	
//System.out.println("StructureAnalyzer.refreshSpeciesContextMappings()");

	//GeometryContext geoContext = mathMapping.getSimulationContext().getGeometryContext();
	Model model = mathMapping_4_8.getSimulationContext().getReactionContext().getModel();
	//
	// note, the order of species is specified such that the first species have priority
	//       when the null space is solved for dependent variables.  So the order of priority
	//       for elimination are as follows:
	//
	//          1) Species involved with fast reactions.
	//          2) Species not involved with fast reactions.
	//
	Vector<SpeciesContextMapping> scmList = new Vector<SpeciesContextMapping>();

	//
	// for each structure, get all non-constant speciesContext's that participate in fast reactions
	//
	for (int i=0;i<structures.length;i++){
		SpeciesContext speciesContexts[] = model.getSpeciesContexts(structures[i]);
		for (int j=0;j<speciesContexts.length;j++){
			SpeciesContext sc = speciesContexts[j];
			SpeciesContextMapping scm = mathMapping_4_8.getSpeciesContextMapping(sc);
			SpeciesContextSpec scs = mathMapping_4_8.getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);
			if (scm.isFastParticipant() && !scs.isConstant()){
				scmList.addElement(scm);
			}
		}
	}
	//
	// for each structure, get all non-constant speciesContext's that DO NOT participate in fast reactions
	//
	for (int i=0;i<structures.length;i++){
		SpeciesContext speciesContexts[] = model.getSpeciesContexts(structures[i]);
		for (int j=0;j<speciesContexts.length;j++){
			SpeciesContext sc = speciesContexts[j];
			SpeciesContextMapping scm = mathMapping_4_8.getSpeciesContextMapping(sc);
			SpeciesContextSpec scs = mathMapping_4_8.getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);
			if (!scm.isFastParticipant() && !scs.isConstant()){
				scmList.addElement(scm);
			}
		}
	}
	if (scmList.size()>0){
		speciesContextMappings = new SpeciesContextMapping[scmList.size()];
		scmList.copyInto(speciesContextMappings);
		for (int i=0;i<speciesContextMappings.length;i++){
			speciesContextMappings[i].setRate(new Expression(0.0));
//System.out.println("speciesContextMappings["+i+"] = "+speciesContextMappings[i].getSpeciesContext().getName());
		}
	}else{
		speciesContextMappings=null;
	}
//System.out.println("StructureAnalyzer.refreshTotalSpeciesContextMapping(), speciesContextMappings.length = "+scmList.size());
	
	//
	// get all reactionSteps associated with these structures
	//
	Vector<ReactionStep> rsList = new Vector<ReactionStep>();
	ReactionSpec allReactionSpecs[] = mathMapping_4_8.getSimulationContext().getReactionContext().getReactionSpecs();
	for (int i=0;i<allReactionSpecs.length;i++){
		if (allReactionSpecs[i].isExcluded()){
			continue;
		}
		ReactionStep rs = allReactionSpecs[i].getReactionStep();
		for (int j=0;j<structures.length;j++){
			if (rs.getStructure()==structures[j]){
				rsList.addElement(rs);
			}
		}
	}
	//
	// add DiffusionReactionStep for each diffusing species and EventReactionStep for each speciesContext that has 
	// event assignment(s) [that is a target variable in event assignment(s)] to eliminate those species from conserved moieties
	//
	for (int i=0;i<scmList.size();i++){
		SpeciesContextMapping scm = (SpeciesContextMapping)scmList.elementAt(i);
		if (scm.isPDERequired()){
			rsList.addElement(new DiffusionReactionStep("DiffusionReactionStep"+i, model, scm.getSpeciesContext().getStructure(), scm.getSpeciesContext()));
		}
		if (scm.hasEventAssignment()){
			rsList.addElement(new EventReactionStep("EventReactionStep"+i, model, scm.getSpeciesContext().getStructure(), scm.getSpeciesContext()));
		}
	}
	if (rsList.size()>0){
		reactionSteps = new ReactionStep[rsList.size()];
		rsList.copyInto(reactionSteps);
	}else{
		reactionSteps=null;
	}
//System.out.println("StructureAnalyzer.refreshTotalSpeciesContextMapping(), reactionSteps.length = "+scmList.size());
}


/**
 * This method was created in VisualAge.
 */
private void substituteIntoFastSystem() throws Exception {
	
//for (int i=0;i<fastSpeciesContextMappings.length;i++){
//	SpeciesContextMapping scm = fastSpeciesContextMappings[i];
//	if (scm.fastInvariant!=null){
//		System.out.println("fastInvariant for "+scm.getSpeciesContext().getName()+" = "+scm.fastInvariant.toString());
//	}else{
//		System.out.println("fastRate for "+scm.getSpeciesContext().getName()+" = "+scm.fastRate.toString());
//	}
//}

	//
	// substitute independent variables where possible
	//
	for (int j=0;j<speciesContextMappings.length;j++){
		if (speciesContextMappings[j].getDependencyExpression() != null){
			Expression dependentVar = new Expression(speciesContextMappings[j].getSpeciesContext(), mathMapping_4_8.getNameScope());
			Expression dependentExp = new Expression(speciesContextMappings[j].getDependencyExpression());
//System.out.println("trying to substitute '"+dependentExp.toString()+"' for variable '"+dependentVar.toString()+"'"); 
			for (int i=0;i<fastSpeciesContextMappings.length;i++){
				SpeciesContextMapping scm = fastSpeciesContextMappings[i];
				if (scm.getFastInvariant()!=null){
					scm.getFastInvariant().substituteInPlace(dependentVar,dependentExp);
					scm.setFastInvariant(scm.getFastInvariant().flatten());
				}else{
					scm.getFastRate().substituteInPlace(dependentVar,dependentExp);
					scm.setFastRate(scm.getFastRate().flatten());
				}
			}
		}
	}
	
//for (int i=0;i<fastSpeciesContextMappings.length;i++){
//	SpeciesContextMapping scm = fastSpeciesContextMappings[i];
//	if (scm.fastInvariant!=null){
//		System.out.println("fastInvariant for "+scm.getSpeciesContext().getName()+" = "+scm.fastInvariant.toString());
//	}else{
//		System.out.println("fastRate for "+scm.getSpeciesContext().getName()+" = "+scm.fastRate.toString());
//	}
//}
}
public Expression getReactionRateExpression(ReactionStep reactionStep, ReactionParticipant reactionParticipant) throws Exception {
	if (reactionParticipant instanceof Catalyst){
		throw new Exception("Catalyst "+reactionParticipant+" doesn't have a rate for this reaction");
		//return new Expression(0.0);
	}
	double stoich = reactionStep.getStoichiometry(reactionParticipant.getSpeciesContext());
	if (stoich==0.0){
		return new Expression(0.0);
	}
	if (reactionStep.getKinetics() instanceof DistributedKinetics){
		DistributedKinetics distributedKinetics = (DistributedKinetics)reactionStep.getKinetics();
		if (stoich!=1){
			Expression exp = Expression.mult(new Expression(stoich),new Expression(distributedKinetics.getReactionRateParameter(), mathMapping_4_8.getNameScope()));
			return exp;
		}else{
			Expression exp = new Expression(distributedKinetics.getReactionRateParameter(), mathMapping_4_8.getNameScope());
			return exp;
		}
	}else if (reactionStep.getKinetics() instanceof LumpedKinetics){
		Structure.StructureSize structureSize = reactionStep.getStructure().getStructureSize();
		//
		// need to put this into concentration/time with respect to structure for reaction.
		//
		LumpedKinetics lumpedKinetics = (LumpedKinetics)reactionStep.getKinetics();
		Expression factor = null;
		ModelUnitSystem unitSystem = mathMapping_4_8.getSimulationContext().getModel().getUnitSystem();
		if (reactionStep.getStructure() instanceof Feature || ((reactionStep.getStructure() instanceof Membrane) && reactionStep instanceof FluxReaction)){
			VCUnitDefinition lumpedToVolumeSubstance = unitSystem.getVolumeSubstanceUnit().divideBy(unitSystem.getLumpedReactionSubstanceUnit());
			factor = Expression.div(new Expression(lumpedToVolumeSubstance.getDimensionlessScale()),new Expression(structureSize, mathMapping_4_8.getNameScope()));
		}else if (reactionStep.getStructure() instanceof Membrane && reactionStep instanceof SimpleReaction){
			VCUnitDefinition lumpedToVolumeSubstance = unitSystem.getMembraneSubstanceUnit().divideBy(unitSystem.getLumpedReactionSubstanceUnit());
			factor = Expression.div(new Expression(lumpedToVolumeSubstance.getDimensionlessScale()),new Expression(structureSize, mathMapping_4_8.getNameScope()));
		}else{
			throw new RuntimeException("failed to create reaction rate expression for reaction "+reactionStep.getName()+", with kinetic type of "+reactionStep.getKinetics().getClass().getName());
		}
		if (stoich!=1){
			Expression exp = Expression.mult(new Expression(stoich),Expression.mult(new Expression(lumpedKinetics.getLumpedReactionRateParameter(), mathMapping_4_8.getNameScope()),factor));
			return exp;
		}else{
			Expression exp = Expression.mult(new Expression(lumpedKinetics.getLumpedReactionRateParameter(), mathMapping_4_8.getNameScope()),factor);
			return exp;
		}
	}else{
		throw new RuntimeException("unexpected kinetic type "+reactionStep.getKinetics().getClass().getName());
	}
}   

}
