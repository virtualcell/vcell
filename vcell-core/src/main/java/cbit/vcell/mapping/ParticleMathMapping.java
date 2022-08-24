/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.vcell.util.Pair;
import org.vcell.util.TokenMangler;
import org.vcell.util.VCellThreadChecker;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecProxyParameter;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.math.Action;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Function;
import cbit.vcell.math.InteractionRadius;
import cbit.vcell.math.JumpProcessRateDefinition;
import cbit.vcell.math.MacroscopicRateConstant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MembraneParticleVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleJumpProcess.ProcessSymmetryFactor;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionConcentration;
import cbit.vcell.math.ParticleProperties.ParticleInitialConditionCount;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableHash;
import cbit.vcell.math.VolumeParticleVariable;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.DistributedKinetics;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.LumpedKinetics;
import cbit.vcell.model.MassActionSolver;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
import ucar.units_vcell.RationalNumber;
/**
 * The MathMapping class performs the Biological to Mathematical transformation once upon calling getMathDescription().
 * This is not a "live" transformation, so that an updated SimulationContext must be given to a new MathMapping object
 * to get an updated MathDescription.
 */
public class ParticleMathMapping extends AbstractMathMapping {
	
/**
 * This method was created in VisualAge.
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 */
protected ParticleMathMapping(SimulationContext simContext, MathMappingCallback callback, NetworkGenerationRequirements networkGenerationRequirements) {
	super(simContext, callback, networkGenerationRequirements);
}


/**
 * This method was created in VisualAge.
 */
private void refreshMathDescription() throws MappingException, MatrixException, MathException, ExpressionException, ModelException {

	getSimulationContext().checkValidity();
	
	if (getSimulationContext().getGeometry().getDimension()==0){
		throw new MappingException("particle math mapping requires spatial geometry - dimension >= 1");
	}
	
	StructureMapping structureMappings[] = getSimulationContext().getGeometryContext().getStructureMappings();
	for (int i = 0; i < structureMappings.length; i++){
		if (structureMappings[i] instanceof MembraneMapping){
			if (((MembraneMapping)structureMappings[i]).getCalculateVoltage()){
				throw new MappingException("electric potential not yet supported for particle models");
			}
		}	
	}
	
	//
	// fail if any events
	//
	BioEvent[] bioEvents = getSimulationContext().getBioEvents();
	if (bioEvents!=null && bioEvents.length>0){
		throw new MappingException("events not yet supported for particle-based models");
	}
	
	//
	// gather only those reactionSteps that are not "excluded"
	//
	ReactionSpec reactionSpecs[] = getSimulationContext().getReactionContext().getReactionSpecs();
	Vector<ReactionStep> rsList = new Vector<ReactionStep>();
	for (int i = 0; i < reactionSpecs.length; i++){
		if (reactionSpecs[i].isExcluded()==false){
			if (reactionSpecs[i].isFast()){
				throw new MappingException("fast reactions not supported for particle models");
			}
			rsList.add(reactionSpecs[i].getReactionStep());
		}
	}
	ReactionStep reactionSteps[] = new ReactionStep[rsList.size()];
	rsList.copyInto(reactionSteps);

	//
	// fail if any unresolved parameters
	//
	for (int i = 0; i < reactionSteps.length; i++){
		Kinetics.UnresolvedParameter unresolvedParameters[] = reactionSteps[i].getKinetics().getUnresolvedParameters();
		if (unresolvedParameters!=null && unresolvedParameters.length>0){
			StringBuffer buffer = new StringBuffer();
			for (int j = 0; j < unresolvedParameters.length; j++){
				if (j>0){
					buffer.append(", ");
				}
				buffer.append(unresolvedParameters[j].getName());
			}
			throw new MappingException(reactionSteps[i].getDisplayType()+" '"+reactionSteps[i].getName()+"' contains unresolved identifier(s): "+buffer);
		}
	}

	//
	// temporarily place all variables in a hashtable (before binding) and discarding duplicates (check for equality)
	//
	VariableHash varHash = new VariableHash();
	
//	//
//	// verify that all structures are mapped to geometry classes and all geometry classes are mapped to a structure
//	//
//	Structure structures[] = getSimulationContext().getGeometryContext().getModel().getStructures();
//	for (int i = 0; i < structures.length; i++){
//		StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(structures[i]);
//		if (sm==null || (sm.getGeometryClass() == null)){
//			throw new MappingException("model structure '"+structures[i].getName()+"' not mapped to a geometry subdomain");
//		}
//		if (sm.getUnitSizeParameter()!=null){
//			Expression unitSizeExp = sm.getUnitSizeParameter().getExpression();
//			if(unitSizeExp != null)
//			{
//				try {
//					double unitSize = unitSizeExp.evaluateConstant();
//					if (unitSize != 1.0){
//						throw new MappingException("model structure '"+sm.getStructure().getName()+"' unit size = "+unitSize+" != 1.0 ... partial volume or surface mapping not yet supported for particles");
//					}
//				}catch (ExpressionException e){
//					e.printStackTrace(System.out);
//					throw new MappingException("couldn't evaluate unit size for model structure '"+sm.getStructure().getName()+"' : "+e.getMessage());
//				}
//			}
//		}
//	}
//	{
//		GeometryClass[] geometryClass = getSimulationContext().getGeometryContext().getGeometry().getGeometryClasses();
//		for (int i = 0; i < geometryClass.length; i++){
//			Structure[] mappedStructures = getSimulationContext().getGeometryContext().getStructuresFromGeometryClass(geometryClass[i]);
//			if (mappedStructures==null || mappedStructures.length==0){
//				throw new MappingException("geometryClass '"+geometryClass[i].getName()+"' not mapped from a model structure");
//			}
//		}
//	}

	// deals with model parameters
	Model model = getSimulationContext().getModel();
	ModelUnitSystem modelUnitSystem = model.getUnitSystem();
	ModelParameter[] modelParameters = model.getModelParameters();
	// populate in globalParameterVariants hashtable
	for (int j = 0; j < modelParameters.length; j++){
		Expression modelParamExpr = modelParameters[j].getExpression();
		GeometryClass geometryClass = getDefaultGeometryClass(modelParamExpr);
		modelParamExpr = getIdentifierSubstitutions(modelParamExpr, modelParameters[j].getUnitDefinition(), geometryClass);
		varHash.addVariable(newFunctionOrConstant(getMathSymbol(modelParameters[j], geometryClass), modelParamExpr,geometryClass));
	}
	
	//
	// create new MathDescription (based on simContext's previous MathDescription if possible)
	//
	MathDescription oldMathDesc = getSimulationContext().getMathDescription();
	mathDesc = null;
	if (oldMathDesc != null){
		if (oldMathDesc.getVersion() != null){
			mathDesc = new MathDescription(oldMathDesc.getVersion(), mathSymbolMapping);
		}else{
			mathDesc = new MathDescription(oldMathDesc.getName(), mathSymbolMapping);
		}
	}else{
		mathDesc = new MathDescription(getSimulationContext().getName()+"_generated", mathSymbolMapping);
	}

	//
	// volume particle variables
	//
	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = enum1.nextElement();
		if (scm.getVariable() instanceof ParticleVariable){
			if (!(mathDesc.getVariable(scm.getVariable().getName()) instanceof ParticleVariable)){
				varHash.addVariable(scm.getVariable());
			}
		}
	}

	varHash.addVariable(new Constant(getMathSymbol(model.getPI_CONSTANT(),null),getIdentifierSubstitutions(model.getPI_CONSTANT().getExpression(),model.getPI_CONSTANT().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getFARADAY_CONSTANT(),null),getIdentifierSubstitutions(model.getFARADAY_CONSTANT().getExpression(),model.getFARADAY_CONSTANT().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getFARADAY_CONSTANT_NMOLE(),null),getIdentifierSubstitutions(model.getFARADAY_CONSTANT_NMOLE().getExpression(),model.getFARADAY_CONSTANT_NMOLE().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getGAS_CONSTANT(),null),getIdentifierSubstitutions(model.getGAS_CONSTANT().getExpression(),model.getGAS_CONSTANT().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getTEMPERATURE(),null),getIdentifierSubstitutions(new Expression(getSimulationContext().getTemperatureKelvin()), model.getTEMPERATURE().getUnitDefinition(), null)));

	//
	// add Initial Voltages and Voltage Symbols (even though not computing potential).
	//
	for (int j=0;j<structureMappings.length;j++){
		if (structureMappings[j] instanceof MembraneMapping){
			MembraneMapping membraneMapping = (MembraneMapping)structureMappings[j];
			GeometryClass geometryClass = membraneMapping.getGeometryClass();
			//
			// don't calculate voltage, still may need it though
			//
			Parameter initialVoltageParm = membraneMapping.getInitialVoltageParameter();
			Variable voltageFunction = newFunctionOrConstant(getMathSymbol(membraneMapping.getMembrane().getMembraneVoltage(),geometryClass),getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),geometryClass),geometryClass);
			varHash.addVariable(voltageFunction);
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(membraneMapping.getMembrane().getMembraneVoltage(),membraneMapping.getGeometryClass()),getIdentifierSubstitutions(membraneMapping.getInitialVoltageParameter().getExpression(),membraneMapping.getInitialVoltageParameter().getUnitDefinition(),membraneMapping.getGeometryClass()),membraneMapping.getGeometryClass()));
		}
	}

	//
	// kinetic parameters (functions or constants)
	//
	for (int j=0;j<reactionSteps.length;j++){
		ReactionStep rs = reactionSteps[j];
		if (getSimulationContext().getReactionContext().getReactionSpec(rs).isExcluded()){
			continue;
		}
		Kinetics.KineticsParameter parameters[] = rs.getKinetics().getKineticsParameters();
		GeometryClass geometryClass = null;
		if (rs.getStructure()!=null){
			geometryClass = getSimulationContext().getGeometryContext().getStructureMapping(rs.getStructure()).getGeometryClass();
		}
		if (parameters != null){
			for (int i=0;i<parameters.length;i++){
				//Reaction rate, currentDensity, LumpedCurrent and null parameters are not going to displayed in the particle math description.
				if (((parameters[i].getRole() == Kinetics.ROLE_CurrentDensity)||(parameters[i].getRole() == Kinetics.ROLE_LumpedCurrent) || (parameters[i].getRole() == Kinetics.ROLE_ReactionRate)) ||
					 (parameters[i].getExpression()==null)){
					continue;
				}
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameters[i],geometryClass), getIdentifierSubstitutions(parameters[i].getExpression(),parameters[i].getUnitDefinition(),geometryClass),geometryClass));
			}
		}
	}
	//
	// initial constants (either function or constant)
	//
	SpeciesContextSpec speciesContextSpecs[] = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextSpecParameter initParm = null;
		Expression initExpr = null;
		if (getSimulationContext().isUsingConcentration()) {
			initParm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
			initExpr = new Expression(initParm.getExpression());
//			if (speciesContextSpecs[i].getSpeciesContext().getStructure() instanceof Feature) {
//				initExpr = Expression.div(initExpr, new Expression(model.getKMOLE, getNameScope())).flatten();
//			}
		} else {
			initParm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_InitialCount);
			initExpr = new Expression(initParm.getExpression());
		}
		if (initExpr != null) {
			StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
			String[] symbols = initExpr.getSymbols();
			// Check if 'initExpr' has other speciesContexts in its expression, need to replace it with 'spContext_init'
			for (int j = 0; symbols != null && j < symbols.length; j++) {
				// if symbol is a speciesContext, replacing it with a reference to initial condition for that speciesContext.
				SpeciesContext spC = null;
				SymbolTableEntry ste = initExpr.getSymbolBinding(symbols[j]);
				if (ste instanceof SpeciesContextSpecProxyParameter) {
					SpeciesContextSpecProxyParameter spspp = (SpeciesContextSpecProxyParameter)ste;
					if (spspp.getTarget() instanceof SpeciesContext) {
						spC = (SpeciesContext)spspp.getTarget();
						SpeciesContextSpec spcspec = getSimulationContext().getReactionContext().getSpeciesContextSpec(spC);
						SpeciesContextSpecParameter spCInitParm = spcspec.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
						// if initConc param expression is null, try initCount
						if (spCInitParm.getExpression() == null) {
							spCInitParm = spcspec.getParameterFromRole(SpeciesContextSpec.ROLE_InitialCount);
						}
						// need to get init condn expression, but can't get it from getMathSymbol() (mapping between bio and math), hence get it as below.
						Expression scsInitExpr = new Expression(spCInitParm, getNameScope());
//						scsInitExpr.bindExpression(this);
						initExpr.substituteInPlace(new Expression(spC.getName()), scsInitExpr);
					}
				}
			}
			// now create the appropriate function for the current speciesContextSpec.
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(initParm,sm.getGeometryClass()),getIdentifierSubstitutions(initExpr,initParm.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
	}
	
	//
	// diffusion constants (either function or constant)
	//
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextSpec.SpeciesContextSpecParameter diffParm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_DiffusionRate);
		if (diffParm!=null){
			StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(diffParm,sm.getGeometryClass()),getIdentifierSubstitutions(diffParm.getExpression(),diffParm.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
	}

	//
	// Boundary conditions (either function or constant)
	//
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextSpec.SpeciesContextSpecParameter bc_xm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueXm);
		StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
		if (bc_xm!=null && (bc_xm.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_xm,sm.getGeometryClass()),getIdentifierSubstitutions(bc_xm.getExpression(),bc_xm.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter bc_xp = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueXp);
		if (bc_xp!=null && (bc_xp.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_xp,sm.getGeometryClass()),getIdentifierSubstitutions(bc_xp.getExpression(),bc_xp.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter bc_ym = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueYm);
		if (bc_ym!=null && (bc_ym.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_ym,sm.getGeometryClass()),getIdentifierSubstitutions(bc_ym.getExpression(),bc_ym.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter bc_yp = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueYp);
		if (bc_yp!=null && (bc_yp.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_yp,sm.getGeometryClass()),getIdentifierSubstitutions(bc_yp.getExpression(),bc_yp.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter bc_zm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueZm);
		if (bc_zm!=null && (bc_zm.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_zm,sm.getGeometryClass()),getIdentifierSubstitutions(bc_zm.getExpression(),bc_zm.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter bc_zp = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueZp);
		if (bc_zp!=null && (bc_zp.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_zp,sm.getGeometryClass()),getIdentifierSubstitutions(bc_zp.getExpression(),bc_zp.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
	}
	
	//
	// advection terms (either function or constant)
	//
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextSpec.SpeciesContextSpecParameter advection_velX = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_VelocityX);
		StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
		GeometryClass geometryClass = sm.getGeometryClass();
		if (advection_velX!=null && (advection_velX.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(advection_velX,geometryClass),getIdentifierSubstitutions(advection_velX.getExpression(),advection_velX.getUnitDefinition(),geometryClass),geometryClass));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter advection_velY = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_VelocityY);
		if (advection_velY!=null && (advection_velY.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(advection_velY,geometryClass),getIdentifierSubstitutions(advection_velY.getExpression(),advection_velY.getUnitDefinition(),geometryClass),geometryClass));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter advection_velZ = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_VelocityZ);
		if (advection_velZ!=null && (advection_velZ.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(advection_velZ,geometryClass),getIdentifierSubstitutions(advection_velZ.getExpression(),advection_velZ.getUnitDefinition(),geometryClass),geometryClass));
		}
	}
	
	//
	// constant species (either function or constant)
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable() instanceof Constant){
			varHash.addVariable(scm.getVariable());
		}
	}
	//
	// conversion factors
	//
	varHash.addVariable(new Constant(getMathSymbol(model.getKMOLE(), null), getIdentifierSubstitutions(model.getKMOLE().getExpression(),model.getKMOLE().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getN_PMOLE(), null), getIdentifierSubstitutions(model.getN_PMOLE().getExpression(),model.getN_PMOLE().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getKMILLIVOLTS(), null),getIdentifierSubstitutions(model.getKMILLIVOLTS().getExpression(),model.getKMILLIVOLTS().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getK_GHK(), null),getIdentifierSubstitutions(model.getK_GHK().getExpression(),model.getK_GHK().getUnitDefinition(),null)));
	//
	// geometric functions
	//
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (getSimulationContext().getGeometry().getDimension()==0){
			StructureMappingParameter sizeParm = sm.getSizeParameter();
			if (sizeParm!=null && sizeParm.getExpression()!=null){
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(sizeParm,sm.getGeometryClass()),getIdentifierSubstitutions(sizeParm.getExpression(), sizeParm.getUnitDefinition(), sm.getGeometryClass()), sm.getGeometryClass()));
			} else {
				if (sm instanceof MembraneMapping) {
					MembraneMapping mm = (MembraneMapping)sm;
					StructureMappingParameter volFrac = mm.getVolumeFractionParameter();
					if (volFrac!=null && volFrac.getExpression()!=null){
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(volFrac,sm.getGeometryClass()),
								getIdentifierSubstitutions(volFrac.getExpression(), volFrac.getUnitDefinition(), sm.getGeometryClass()), sm.getGeometryClass()));
					}
					StructureMappingParameter surfToVol = mm.getSurfaceToVolumeParameter();
					if (surfToVol!=null && surfToVol.getExpression()!=null){
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(surfToVol,sm.getGeometryClass()),
								getIdentifierSubstitutions(surfToVol.getExpression(), surfToVol.getUnitDefinition(), sm.getGeometryClass()), sm.getGeometryClass()));
					}
				}				
			}
		}else{
			Parameter parm = sm.getParameterFromRole(StructureMapping.ROLE_AreaPerUnitArea);
			if (parm!=null && parm.getExpression()!=null && sm.getGeometryClass() instanceof SurfaceClass){
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm.getGeometryClass()),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm.getGeometryClass()), sm.getGeometryClass()));
			}
			parm = sm.getParameterFromRole(StructureMapping.ROLE_AreaPerUnitVolume);
			if (parm!=null && parm.getExpression()!=null && sm.getGeometryClass() instanceof SubVolume){
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm.getGeometryClass()),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm.getGeometryClass()), sm.getGeometryClass()));
			}
			parm = sm.getParameterFromRole(StructureMapping.ROLE_VolumePerUnitArea);
			if (parm!=null && parm.getExpression()!=null && sm.getGeometryClass() instanceof SurfaceClass){
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm.getGeometryClass()),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm.getGeometryClass()), sm.getGeometryClass()));
			}
			parm = sm.getParameterFromRole(StructureMapping.ROLE_VolumePerUnitVolume);
			if (parm!=null && parm.getExpression()!=null && sm.getGeometryClass() instanceof SubVolume){
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm.getGeometryClass()),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm.getGeometryClass()), sm.getGeometryClass()));
			}
			parm = sm.getParameterFromRole(StructureMapping.ROLE_Size);
			if (parm!=null && parm.getExpression()!=null){
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm.getGeometryClass()),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm.getGeometryClass()), sm.getGeometryClass()));
			}
		}
	}

	//
	// functions
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable()==null && scm.getDependencyExpression()!=null){
			StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
			Variable dependentVariable = newFunctionOrConstant(getMathSymbol(scm.getSpeciesContext(),sm.getGeometryClass()),getIdentifierSubstitutions(scm.getDependencyExpression(),scm.getSpeciesContext().getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass());
			dependentVariable.setDomain(new Domain(sm.getGeometryClass()));
			varHash.addVariable(dependentVariable);
		}
	}
	
	//
	// include required UnitRateFactors
	//
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		if (fieldMathMappingParameters[i] instanceof UnitFactorParameter){
			GeometryClass geometryClass = fieldMathMappingParameters[i].getGeometryClass();
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),geometryClass),fieldMathMappingParameters[i].getGeometryClass()));
		}
	}

	//
	// set Variables to MathDescription all at once with the order resolved by "VariableHash"
	//
	mathDesc.setAllVariables(varHash.getAlphabeticallyOrderedVariables());
	
	
	//
	// geometry
	//
	if (getSimulationContext().getGeometryContext().getGeometry() != null){
		try {
			mathDesc.setGeometry(getSimulationContext().getGeometryContext().getGeometry());
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new MappingException("failure setting geometry "+e.getMessage());
		}
	}else{
		throw new MappingException("geometry must be defined");
	}

	//
	// create subdomains (volume and surfaces)
	//
	GeometryClass[] geometryClasses = getSimulationContext().getGeometryContext().getGeometry().getGeometryClasses();
	for (int k=0;k<geometryClasses.length;k++){
		if (geometryClasses[k] instanceof SubVolume){
			SubVolume subVolume = (SubVolume)geometryClasses[k];
			//
			// get priority of subDomain
			//
			int priority = k; // now does not have to match spatial feature, *BUT* needs to be unique
			
			//
			// create subDomain
			//
			CompartmentSubDomain subDomain = new CompartmentSubDomain(subVolume.getName(),priority);
			mathDesc.addSubDomain(subDomain);

			//
			// assign boundary condition types
			//
			StructureMapping[] mappedSMs = getSimulationContext().getGeometryContext().getStructureMappings(subVolume);
			FeatureMapping mappedFM = null;
			for (int i = 0; i < mappedSMs.length; i++) {
				if (mappedSMs[i] instanceof FeatureMapping){
					if (mappedFM!=null){
						lg.warn("WARNING:::: MathMapping.refreshMathDescription() ... assigning boundary condition types not unique");
					}
					mappedFM = (FeatureMapping)mappedSMs[i];
				}
			}
			if (mappedFM != null){
				subDomain.setBoundaryConditionXm(mappedFM.getBoundaryConditionTypeXm());
				subDomain.setBoundaryConditionXp(mappedFM.getBoundaryConditionTypeXp());
				if (getSimulationContext().getGeometry().getDimension()>1){
					subDomain.setBoundaryConditionYm(mappedFM.getBoundaryConditionTypeYm());
					subDomain.setBoundaryConditionYp(mappedFM.getBoundaryConditionTypeYp());
				}
				if (getSimulationContext().getGeometry().getDimension()>2){
					subDomain.setBoundaryConditionZm(mappedFM.getBoundaryConditionTypeZm());
					subDomain.setBoundaryConditionZp(mappedFM.getBoundaryConditionTypeZp());
				}
			}
		}else if (geometryClasses[k] instanceof SurfaceClass){
			SurfaceClass surfaceClass = (SurfaceClass)geometryClasses[k];
			// determine membrane inside and outside subvolume
			// this preserves backward compatibility so that membrane subdomain
			// inside and outside correspond to structure hierarchy when present
			Pair<SubVolume,SubVolume> ret = DiffEquMathMapping.computeBoundaryConditionSource(model, simContext, surfaceClass);
			SubVolume innerSubVolume = ret.one;
			SubVolume outerSubVolume = ret.two;

			//
			// create subDomain
			//
			CompartmentSubDomain outerCompartment = mathDesc.getCompartmentSubDomain(outerSubVolume.getName());
			CompartmentSubDomain innerCompartment = mathDesc.getCompartmentSubDomain(innerSubVolume.getName());

			MembraneSubDomain memSubDomain = new MembraneSubDomain(innerCompartment,outerCompartment,surfaceClass.getName());
			mathDesc.addSubDomain(memSubDomain);
		}
	}
	
	//
	// create Particle Contexts for all Particle Variables
	//
	Enumeration<SpeciesContextMapping> enumSCM = getSpeciesContextMappings();
	Expression unitFactor = getUnitFactor(modelUnitSystem.getStochasticSubstanceUnit().divideBy(modelUnitSystem.getVolumeSubstanceUnit()));
	while (enumSCM.hasMoreElements()){
		SpeciesContextMapping scm = enumSCM.nextElement();
		SpeciesContext        sc  = scm.getSpeciesContext();
		StructureMapping      sm  = getSimulationContext().getGeometryContext().getStructureMapping(sc.getStructure());
		SpeciesContextSpec    scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);

		if (scm.getVariable() instanceof ParticleVariable && scm.getDependencyExpression()==null){
			ParticleVariable particleVariable = (ParticleVariable)scm.getVariable();
			
			//
			// initial distribution of particles
			//
			ArrayList<ParticleInitialCondition> particleInitialConditions = new ArrayList<ParticleInitialCondition>();
			ParticleInitialCondition pic = null;
			if (getSimulationContext().isUsingConcentration()) {
				Expression initialDistribution = scs.getInitialConcentrationParameter().getExpression() == null ? null : new Expression(getMathSymbol(scs.getInitialConcentrationParameter(),sm.getGeometryClass()));
				if(particleVariable instanceof VolumeParticleVariable)
				{
					initialDistribution = Expression.mult(unitFactor, initialDistribution).flattenFactors("KMOLE");
				}
				pic = new ParticleInitialConditionConcentration(initialDistribution);
			} else {
				Expression initialCount = scs.getInitialCountParameter().getExpression() == null ? null : new Expression(getMathSymbol(scs.getInitialCountParameter(),sm.getGeometryClass()));
				if (initialCount==null){
					throw new MappingException("initialCount not defined for speciesContext "+scs.getSpeciesContext().getName());
				}
				Expression locationX = new Expression("u");
				Expression locationY = new Expression("u");
				Expression locationZ = new Expression("u");
				pic = new ParticleInitialConditionCount(initialCount, locationX, locationY, locationZ);
			}
			particleInitialConditions.add(pic);

			//
			// diffusion
			//
			Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),sm.getGeometryClass()));

			Expression driftXExp = null;
			if (scs.getVelocityXParameter().getExpression()!=null){
				driftXExp = new Expression(getMathSymbol(scs.getVelocityXParameter(),sm.getGeometryClass()));
			}else{
				SpatialQuantity[] velX_quantities = scs.getVelocityQuantities(QuantityComponent.X);
				if (velX_quantities.length>0){
					int numRegions = simContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(sm.getGeometryClass()).length;
					if (velX_quantities.length==1 && numRegions==1){
						driftXExp = new Expression(getMathSymbol(velX_quantities[0],sm.getGeometryClass()));
					}else{
						throw new MappingException("multiple advection velocities enabled set for multiple volume domains ");
					}
				}
			}

			Expression driftYExp = null;
			if (scs.getVelocityYParameter().getExpression()!=null){
				driftYExp = new Expression(getMathSymbol(scs.getVelocityYParameter(),sm.getGeometryClass()));
			}else{
				SpatialQuantity[] velY_quantities = scs.getVelocityQuantities(QuantityComponent.Y);
				if (velY_quantities.length>0){
					int numRegions = simContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(sm.getGeometryClass()).length;
					if (velY_quantities.length==1 && numRegions==1){
						driftYExp = new Expression(getMathSymbol(velY_quantities[0],sm.getGeometryClass()));
					}else{
						throw new MappingException("multiple advection velocities enabled set for multiple volume domains ");
					}
				}
			}
			
			Expression driftZExp = null;
			if (scs.getVelocityZParameter().getExpression()!=null){
				driftZExp = new Expression(getMathSymbol(scs.getVelocityZParameter(),sm.getGeometryClass()));
			}else{
				SpatialQuantity[] velZ_quantities = scs.getVelocityQuantities(QuantityComponent.Z);
				if (velZ_quantities.length>0){
					int numRegions = simContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(sm.getGeometryClass()).length;
					if (velZ_quantities.length==1 && numRegions==1){
						driftZExp = new Expression(getMathSymbol(velZ_quantities[0],sm.getGeometryClass()));
					}else{
						throw new MappingException("multiple advection velocities enabled set for multiple volume domains ");
					}
				}
			}

			ParticleProperties particleProperties = new ParticleProperties(particleVariable, diffusion, driftXExp, driftYExp, driftZExp, particleInitialConditions);
			GeometryClass myGC = sm.getGeometryClass();
			if(myGC == null){
				throw new MappingException("Application '"+getSimulationContext().getName()+"'\nGeometry->StructureMapping->("+sm.getStructure().getTypeName()+")'"+sm.getStructure().getName()+"' must be mapped to geometry domain.\n(see 'Problems' tab)");
			}
			SubDomain subDomain = mathDesc.getSubDomain(myGC.getName());
			subDomain.addParticleProperties(particleProperties);
		}
	}
	
	for (ReactionStep reactionStep : reactionSteps){
		
		Kinetics kinetics = reactionStep.getKinetics();
		StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(reactionStep.getStructure());
		GeometryClass reactionStepGeometryClass = sm.getGeometryClass();
		SubDomain subdomain = mathDesc.getSubDomain(reactionStepGeometryClass.getName());
		KineticsParameter reactionRateParameter = null;
		if (kinetics instanceof LumpedKinetics){
			reactionRateParameter = ((LumpedKinetics)kinetics).getLumpedReactionRateParameter();
		}else{
			reactionRateParameter = ((DistributedKinetics)kinetics).getReactionRateParameter();
		}
		
		//macroscopic_irreversible/Microscopic_irreversible for bimolecular membrane reactions. They will NOT go through MassAction solver.
		if(kinetics.getKineticsDescription().equals(KineticsDescription.Macroscopic_irreversible) || kinetics.getKineticsDescription().equals(KineticsDescription.Microscopic_irreversible)) 
		{
			Expression radiusExp = getIdentifierSubstitutions(reactionStep.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_Binding_Radius).getExpression(), 
					                                    modelUnitSystem.getBindingRadiusUnit(), reactionStepGeometryClass);
			if(radiusExp != null)
			{
				Expression expCopy = new Expression(radiusExp);
				try{
					MassActionSolver.substituteParameters(expCopy, true).evaluateConstant();
				}catch(ExpressionException e)
				{
					throw new MathException(VCellErrorMessages.getMassActionSolverMessage(reactionStep.getName(), "Problem in binding radius of " + reactionStep.getName() +":  '" + radiusExp.infix() + "', " + e.getMessage()));
				}
			}
			else
			{
				throw new MathException(VCellErrorMessages.getMassActionSolverMessage(reactionStep.getName(), "Binding radius of " + reactionStep.getName() +" is null."));
			}
			
			List<ParticleVariable> reactantParticles = new ArrayList<ParticleVariable>();
			List<ParticleVariable> productParticles = new ArrayList<ParticleVariable>();
			List<Action> forwardActions = new ArrayList<Action>();

			for (ReactionParticipant rp : reactionStep.getReactionParticipants())
			{
				SpeciesContext sc = rp.getSpeciesContext();
				SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);
				GeometryClass scGeometryClass = getSimulationContext().getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass();
				String varName = getMathSymbol(sc, scGeometryClass);
				Variable var = mathDesc.getVariable(varName);
				if (var instanceof ParticleVariable)
				{
					ParticleVariable particle = (ParticleVariable)var;
					if(rp instanceof Reactant)
					{
						reactantParticles.add(particle);
						if (!scs.isConstant() && !scs.isForceContinuous()) {
							for (int i = 0; i < Math.abs(rp.getStoichiometry()); i++) {
								if (radiusExp!=null) {
									forwardActions.add(Action.createDestroyAction(particle));
								}					 
							}
						}
						
					}
					else if(rp instanceof Product)
					{
						productParticles.add(particle);
						if (!scs.isConstant() && !scs.isForceContinuous()) {
							for (int i = 0; i < Math.abs(rp.getStoichiometry()); i++) {
								if (radiusExp!=null) {
									forwardActions.add(Action.createCreateAction(particle));
								}					 
							}
						}
					}
			    }
				else{
					throw new MappingException("particle variable '"+varName+"' not found");
				}
			}
			JumpProcessRateDefinition bindingRadius = new InteractionRadius(radiusExp);
			
			// get jump process name
			String jpName = TokenMangler.mangleToSName(reactionStep.getName());
			ProcessSymmetryFactor processSymmetryFactor = null; // only for NFSim/Rules for now.
			if (forwardActions.size() > 0){
				ParticleJumpProcess forwardProcess = new ParticleJumpProcess(jpName, reactantParticles, bindingRadius, forwardActions, processSymmetryFactor);
				subdomain.addParticleJumpProcess(forwardProcess);
			}			
		}
		else //other type of reactions
		{
			/* check the reaction rate law to see if we need to decompose a reaction(reversible) into two jump processes.
			   rate constants are important in calculating the probability rate.
			   for Mass Action, we use KForward and KReverse, 
			   for General Kinetics we parse reaction rate J to see if it is in Mass Action form.
			 */
			Expression forwardRate = null;
			Expression reverseRate = null;
			
			// Using the MassActionFunction to write out the math description 
			MassActionSolver.MassActionFunction maFunc = null;

			if(kinetics.getKineticsDescription().equals(KineticsDescription.MassAction) ||
			   kinetics.getKineticsDescription().equals(KineticsDescription.General) || 
			   kinetics.getKineticsDescription().equals(KineticsDescription.GeneralPermeability))
			{
				Expression rateExp = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
				Parameter forwardRateParameter = null;
				Parameter reverseRateParameter = null;
				if (kinetics.getKineticsDescription().equals(KineticsDescription.MassAction)){
					forwardRateParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward);
					reverseRateParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse);
				}else if (kinetics.getKineticsDescription().equals(KineticsDescription.GeneralPermeability)){
					forwardRateParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Permeability);
					reverseRateParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_Permeability);
				}
				maFunc = MassActionSolver.solveMassAction(forwardRateParameter, reverseRateParameter, rateExp, reactionStep);
				if(maFunc.getForwardRate() == null && maFunc.getReverseRate() == null)
				{
					throw new MappingException("Cannot generate stochastic math mapping for the reaction:" + reactionStep.getName() + "\nLooking for the rate function according to the form of k1*Reactant1^Stoir1*Reactant2^Stoir2...-k2*Product1^Stoip1*Product2^Stoip2.");
				}
				else
				{
					if(maFunc.getForwardRate() != null)
					{
						forwardRate = maFunc.getForwardRate();
					}
					if(maFunc.getReverseRate() != null)
					{
						reverseRate = maFunc.getReverseRate();
					}
				}
			}
			
			if(maFunc != null)
			{
				// if the reaction has forward rate (Mass action,HMMs), or don't have either forward or reverse rate (some other rate laws--like general)
				// we process it as forward reaction
				List<ParticleVariable> reactantParticles = new ArrayList<ParticleVariable>();
				List<ParticleVariable> productParticles = new ArrayList<ParticleVariable>();
				List<Action> forwardActions = new ArrayList<Action>();
				List<Action> reverseActions = new ArrayList<Action>();
				List<ReactionParticipant> reactants = maFunc.getReactants();
				List<ReactionParticipant> products = maFunc.getProducts();
		
				for (ReactionParticipant rp : reactants){
					SpeciesContext sc = rp.getSpeciesContext();
					SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);
					GeometryClass scGeometryClass = getSimulationContext().getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass();
					String varName = getMathSymbol(sc, scGeometryClass);
					Variable var = mathDesc.getVariable(varName);
					if (var instanceof ParticleVariable){
						ParticleVariable particle = (ParticleVariable)var;
						reactantParticles.add(particle);
						if (!scs.isConstant() && !scs.isForceContinuous()) {
							for (int i = 0; i < Math.abs(rp.getStoichiometry()); i++) {
								if (forwardRate!=null) {
									forwardActions.add(Action.createDestroyAction(particle));
								}					 
								if (reverseRate!=null) {
									reverseActions.add(Action.createCreateAction(particle));
								}
							}
						}
					}else{
						throw new MappingException("particle variable '"+varName+"' not found");
					}
				}
				for (ReactionParticipant rp : products){
					SpeciesContext sc = rp.getSpeciesContext();
					SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);
					GeometryClass scGeometryClass = getSimulationContext().getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass();
					String varName = getMathSymbol(sc, scGeometryClass);
					Variable var = mathDesc.getVariable(varName);
					if (var instanceof ParticleVariable){
						ParticleVariable particle = (ParticleVariable)var;
						productParticles.add(particle);
						if (!scs.isConstant() && !scs.isForceContinuous()) {
							for (int i = 0; i < Math.abs(rp.getStoichiometry()); i++) {
								if (forwardRate!=null) {
									forwardActions.add(Action.createCreateAction(particle));
								}					 
								if (reverseRate!=null) {
									reverseActions.add(Action.createDestroyAction(particle));
								}
							}
						}
					}else{
						throw new MappingException("particle variable '"+varName+"' not found");
					}
				}
				//
				// There are two unit conversions required:
				//
				// 1) convert entire reaction rate from vcell reaction units to Smoldyn units (molecules/lengthunit^dim/timeunit)
				//    (where dim is 2 for membrane reactions and 3 for volume reactions)
				//
				// for forward rates:
				// 2) convert each reactant from Smoldyn units (molecules/lengthunit^dim) to VCell units 
				//    (where dim is 2 for membrane reactants and 3 for volume reactants)
				//
				// or
				//
				// for reverse rates:
				// 2) convert each product from Smoldyn units (molecules/lengthunit^dim) to VCell units 
				//    (where dim is 2 for membrane products and 3 for volume products)
				//
				RationalNumber reactionLocationDim = new RationalNumber(reactionStep.getStructure().getDimension());
				VCUnitDefinition timeUnit = modelUnitSystem.getTimeUnit();
				VCUnitDefinition smoldynReactionSizeUnit = modelUnitSystem.getLengthUnit().raiseTo(reactionLocationDim);
				VCUnitDefinition smoldynSubstanceUnit = modelUnitSystem.getStochasticSubstanceUnit();
				VCUnitDefinition smoldynReactionRateUnit = smoldynSubstanceUnit.divideBy(smoldynReactionSizeUnit).divideBy(timeUnit);
				VCUnitDefinition vcellReactionRateUnit = reactionRateParameter.getUnitDefinition();
				VCUnitDefinition reactionUnitFactor = smoldynReactionRateUnit.divideBy(vcellReactionRateUnit);
				
				if (forwardRate!=null)
				{
					VCUnitDefinition smoldynReactantsUnit = modelUnitSystem.getInstance_DIMENSIONLESS();
					VCUnitDefinition forwardUnitFactor = reactionUnitFactor; // start with factor to translate entire reaction rate.
					//
					// convert each reactant from Smoldyn units (molecules/lengthunit^dim) to VCell units 
					// (where dim is 2 for membrane reactants and 3 for volume reactants)
					//
					for (ReactionParticipant reactant : maFunc.getReactants()){
						VCUnitDefinition vcellReactantUnit = reactant.getSpeciesContext().getUnitDefinition();
						
						boolean bForceContinuous = simContext.getReactionContext().getSpeciesContextSpec(reactant.getSpeciesContext()).isForceContinuous();
						VCUnitDefinition smoldynReactantUnit = null;
						if (bForceContinuous){ // reactant is continuous (vcell units)
							smoldynReactantUnit = reactant.getSpeciesContext().getUnitDefinition();
						}else{ // reactant is a particle (smoldyn units)
							RationalNumber reactantLocationDim = new RationalNumber(reactant.getStructure().getDimension());
							VCUnitDefinition smoldynReactantSize = modelUnitSystem.getLengthUnit().raiseTo(reactantLocationDim);
							smoldynReactantUnit = smoldynSubstanceUnit.divideBy(smoldynReactantSize);
						}
						smoldynReactantsUnit = smoldynReactantsUnit.multiplyBy(smoldynReactantUnit); // keep track of units of all reactants 
						
						RationalNumber reactantStoichiometry = new RationalNumber(reactant.getStoichiometry());
						VCUnitDefinition reactantUnitFactor = (vcellReactantUnit.divideBy(smoldynReactantUnit)).raiseTo(reactantStoichiometry);
						forwardUnitFactor = forwardUnitFactor.multiplyBy(reactantUnitFactor); // accumulate unit factors for all reactants
					}

					forwardRate = Expression.mult(forwardRate, getUnitFactor(forwardUnitFactor)).flattenFactors("KMOLE");
					VCUnitDefinition smoldynExpectedForwardRateUnit = smoldynReactionRateUnit.divideBy(smoldynReactantsUnit);

					// get probability
					Expression exp = getIdentifierSubstitutions(forwardRate, smoldynExpectedForwardRateUnit, reactionStepGeometryClass).flatten();
					JumpProcessRateDefinition partRateDef = new MacroscopicRateConstant(exp);
					// create particle jump process
					String jpName = TokenMangler.mangleToSName(reactionStep.getName());
					ProcessSymmetryFactor processSymmetryFactor = null; // only for NFSim/Rules for now.
					if (forwardActions.size() > 0){
						ParticleJumpProcess forwardProcess = new ParticleJumpProcess(jpName, reactantParticles, partRateDef, forwardActions, processSymmetryFactor);
						subdomain.addParticleJumpProcess(forwardProcess);
					}
				} // end of forward rate not null
				if (reverseRate!=null)
				{
					VCUnitDefinition smoldynProductsUnit = modelUnitSystem.getInstance_DIMENSIONLESS();
					VCUnitDefinition reverseUnitFactor = reactionUnitFactor; // start with factor to translate entire reaction rate.
					//
					// convert each product from Smoldyn units (molecules/lengthunit^dim) to VCell units 
					// (where dim is 2 for membrane products and 3 for volume products)
					//
					for (ReactionParticipant product : maFunc.getProducts()){
						VCUnitDefinition vcellProductUnit = product.getSpeciesContext().getUnitDefinition();

						boolean bForceContinuous = simContext.getReactionContext().getSpeciesContextSpec(product.getSpeciesContext()).isForceContinuous();
						VCUnitDefinition smoldynProductUnit = null;
						if (bForceContinuous){
							smoldynProductUnit = product.getSpeciesContext().getUnitDefinition();
						}else{
							RationalNumber productLocationDim = new RationalNumber(product.getStructure().getDimension());
							VCUnitDefinition smoldynProductSize = modelUnitSystem.getLengthUnit().raiseTo(productLocationDim);
							smoldynProductUnit = smoldynSubstanceUnit.divideBy(smoldynProductSize);
						}
						smoldynProductsUnit = smoldynProductsUnit.multiplyBy(smoldynProductUnit); // keep track of units of all products 
						
						RationalNumber productStoichiometry = new RationalNumber(product.getStoichiometry());
						VCUnitDefinition productUnitFactor = (vcellProductUnit.divideBy(smoldynProductUnit)).raiseTo(productStoichiometry);						
						reverseUnitFactor = reverseUnitFactor.multiplyBy(productUnitFactor); // accumulate unit factors for all products
					}

					reverseRate = Expression.mult(reverseRate, getUnitFactor(reverseUnitFactor)).flattenFactors("KMOLE");
					VCUnitDefinition smoldynExpectedReverseRateUnit = smoldynReactionRateUnit.divideBy(smoldynProductsUnit);
							
					// get probability
					Expression exp = getIdentifierSubstitutions(reverseRate, smoldynExpectedReverseRateUnit, reactionStepGeometryClass).flatten();
					JumpProcessRateDefinition partProbRate = new MacroscopicRateConstant(exp);
					
					// get jump process name
					String jpName = TokenMangler.mangleToSName(reactionStep.getName()+"_reverse");
					ProcessSymmetryFactor processSymmetryFactor = null; // only for NFSim/Rules for now.
					if (reverseActions.size() > 0){
						ParticleJumpProcess reverseProcess = new ParticleJumpProcess(jpName, productParticles, partProbRate, reverseActions, processSymmetryFactor);
						subdomain.addParticleJumpProcess(reverseProcess);
					}
				} //end of reverse rate not null
			} //end of maFunc not null
		} // end of reaction step for loop
	}
	
	//
	// add any missing unit conversion factors (they don't depend on anyone else ... can do it at the end)
	//
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		if (fieldMathMappingParameters[i] instanceof UnitFactorParameter){
			GeometryClass geometryClass = fieldMathMappingParameters[i].getGeometryClass();
			Variable variable = newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),geometryClass),fieldMathMappingParameters[i].getGeometryClass());
			if (mathDesc.getVariable(variable.getName())==null){
				mathDesc.addVariable(variable);
			}
		}
	}
	

	if (!mathDesc.isValid()){
		lg.warn(mathDesc.getVCML_database());
		throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
	}

	if (lg.isDebugEnabled()) {
		System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string begin ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
		System.out.println(mathDesc.getVCML());
		System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string end ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
	}
}

/**
 * This method was created in VisualAge.
 */
private void refreshSpeciesContextMappings() throws ExpressionException, MappingException, MathException {
	
	//
	// create a SpeciesContextMapping for each speciesContextSpec.
	//
	// set initialExpression from SpeciesContextSpec.
	// set diffusing
	// set variable (only if "Constant" or "Function", else leave it as null)
	//
	speciesContextMappingList.removeAllElements();
	
	SpeciesContextSpec speciesContextSpecs[] = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	for (int i=0;i<speciesContextSpecs.length;i++){
		SpeciesContextSpec scs = speciesContextSpecs[i];

		SpeciesContextMapping scm = new SpeciesContextMapping(scs.getSpeciesContext());
		scm.setPDERequired(false);
		scm.setHasEventAssignment(false);
		scm.setHasHybridReaction(false);
		for (ReactionSpec reactionSpec : getSimulationContext().getReactionContext().getReactionSpecs()){
			if (!reactionSpec.isExcluded() && reactionSpec.hasHybrid(getSimulationContext(), scs.getSpeciesContext())){
				scm.setHasHybridReaction(true);
			}
		}
		/*if (scs.isConstant()){
			Expression initCount = null;
			if(getSimulationContext().isUsingConcentration()) {
				SpeciesContextSpec.SpeciesContextSpecParameter initConcParm =  scs.getInitialConcentrationParameter();
				initCount = scs.convertConcentrationToParticles(new Expression(initConcParm, getNameScope()));
			} else {
				SpeciesContextSpec.SpeciesContextSpecParameter initCountParm = scs.getInitialCountParameter();
				initCount = new Expression(initCountParm, getNameScope());
			}*/
			
		scm.setDependencyExpression(null);
		/*}*/
		//
		// test if participant in fast reaction step, request elimination if possible
		//
/*		scm.setFastParticipant(false);
		ReactionSpec reactionSpecs[] = getSimulationContext().getReactionContext().getReactionSpecs();
		for (int j=0;j<reactionSpecs.length;j++){
			ReactionSpec reactionSpec = reactionSpecs[j];
			if (reactionSpec.isExcluded()){
				continue;
			}
			ReactionStep rs = reactionSpec.getReactionStep();
			if (rs instanceof SimpleReaction && rs.countNumReactionParticipants(scs.getSpeciesContext()) > 0){
				if (reactionSpec.isFast()){*/
		scm.setFastParticipant(false);
/*				}
			}
		}*/
		speciesContextMappingList.addElement(scm);
	}
}


/**
 * This method was created in VisualAge.
 * @Override
 */
private void refreshVariables() throws MappingException {

	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
		
	//
	// non-constant independent variables require either a membrane or volume variable
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
//		if (scm.getDependencyExpression() == null && (!scs.isConstant() || getSimulationContext().hasEventAssignment(scs.getSpeciesContext()))){
			StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
			Structure struct = scm.getSpeciesContext().getStructure();
			Domain domain = null;
			if (sm.getGeometryClass()!=null){
				domain = new Domain(sm.getGeometryClass());
			}
			if (struct instanceof Feature || struct instanceof Membrane){
				if (sm.getGeometryClass() instanceof SurfaceClass){
					if (scs.isWellMixed()){
						//scm.setVariable(new StochMembraneRegionVariable(scm.getSpeciesContext().getName(),domain));
						throw new MappingException("stochastic membrane region variables not yet supported");
					}else{
						scm.setVariable(new MembraneParticleVariable(scm.getSpeciesContext().getName(),domain));
					}
				}else{
					if (scs.isWellMixed()){
						throw new MappingException("stochastic volume region variables not yet supported");
						//scm.setVariable(new StochVolumeRegionVariable(scm.getSpeciesContext().getName(),domain));
					}else{
						scm.setVariable(new VolumeParticleVariable(scm.getSpeciesContext().getName(),domain));
					}
				}
			}else{
				throw new MappingException("class "+scm.getSpeciesContext().getStructure().getClass()+" not supported");
			}
			mathSymbolMapping.put(scm.getSpeciesContext(),scm.getVariable().getName());
//		}
	}

}

@Override
protected void refresh(MathMappingCallback callback) throws MappingException, ExpressionException, MatrixException, MathException, ModelException {
	VCellThreadChecker.checkCpuIntensiveInvocation();
	
	localIssueList.clear();
	//refreshKFluxParameters();
	refreshSpeciesContextMappings();
	//refreshStructureAnalyzers();
	if(callback != null) {
		callback.setProgressFraction(52.0f/100.0f);
	}
	refreshVariables();
	refreshLocalNameCount();
	refreshMathDescription();
	combineHybrid();
	reconcileWithOriginalModel();
}

private void combineHybrid() throws MappingException, ExpressionException, MatrixException, MathException, ModelException{
	ArrayList<SpeciesContext> continuousSpecies = new ArrayList<SpeciesContext>();
	ArrayList<ParticleVariable> continuousSpeciesParticleVars = new ArrayList<ParticleVariable>();
	ArrayList<SpeciesContext> stochSpecies = new ArrayList<SpeciesContext>();

	//
	// categorize speciesContexts as continuous and stochastic
	//
	SpeciesContextSpec[] scsArray = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	continuousSpecies = new ArrayList<SpeciesContext>();
	stochSpecies = new ArrayList<SpeciesContext>();
	for (SpeciesContextSpec speciesContextSpec : scsArray) {
		if (!getSimulationContext().isStoch() || speciesContextSpec.isForceContinuous()){
			
			continuousSpecies.add(speciesContextSpec.getSpeciesContext());
			
			Variable variable = getMathSymbolMapping().getVariable(speciesContextSpec.getSpeciesContext());
			if (variable instanceof ParticleVariable){
				continuousSpeciesParticleVars.add((ParticleVariable)variable);
			}
		}else{
			stochSpecies.add(speciesContextSpec.getSpeciesContext());
		}
	}
	if (continuousSpecies.isEmpty()){
		return;
	}
		
	//
	// create continuous mathDescription ... add stochastic variables and processes to the continuous Math and use this.
	//
	DiffEquMathMapping mathMapping = new DiffEquMathMapping(getSimulationContext(),callback,networkGenerationRequirements);
	mathMapping.refresh(null);
	MathDescription contMathDesc = mathMapping.getMathDescription();
	
	//
	// get list of all continuous variables
	//
	HashMap<String,Variable> allContinuousVars = new HashMap<String,Variable>();
	Enumeration<Variable> enumVar = contMathDesc.getVariables();
	while (enumVar.hasMoreElements()){
		Variable var = enumVar.nextElement();
		allContinuousVars.put(var.getName(),var);
	}
	
	//
	// replace those continuous variables and equations for stochastic speciesContexts 
	// with the particleVariables and particleProperties 
	// (ParticleJumpProcesses removed later)
	//
	ModelUnitSystem unitSystem = getSimulationContext().getModel().getUnitSystem();
	for (SpeciesContext stochSpeciesContext : stochSpecies){
		
		Variable contVar = mathMapping.getMathSymbolMapping().getVariable(stochSpeciesContext);
		Variable stochVar = getMathSymbolMapping().getVariable(stochSpeciesContext);
		allContinuousVars.put(stochVar.getName(),stochVar);
		
		//
		// replace continuous "concentration" VolVariable/MemVariable for this particle with a Function for concentration
		//
		allContinuousVars.remove(contVar);
		VCUnitDefinition sizeUnit = unitSystem.getLengthUnit().raiseTo(new RationalNumber(stochSpeciesContext.getStructure().getDimension()));
		VCUnitDefinition stochasticDensityUnit = unitSystem.getStochasticSubstanceUnit().divideBy(sizeUnit);
		VCUnitDefinition continuousDensityUnit = unitSystem.getConcentrationUnit(stochSpeciesContext.getStructure());
		if (stochasticDensityUnit.isEquivalent(continuousDensityUnit)){
			allContinuousVars.put(contVar.getName(),new Function(contVar.getName(),new Expression(stochVar,getNameScope()),contVar.getDomain()));
		}else{
			Expression conversionFactorExp = getUnitFactor(continuousDensityUnit.divideBy(stochasticDensityUnit));
			allContinuousVars.put(contVar.getName(),new Function(contVar.getName(),Expression.mult(conversionFactorExp, new Expression(stochVar,getNameScope())),contVar.getDomain()));
		}
		
		//
		// remove continuous equation
		//
		Enumeration<SubDomain> contSubDomains = contMathDesc.getSubDomains();
		while (contSubDomains.hasMoreElements()){
			SubDomain contSubDomain = contSubDomains.nextElement();
			contSubDomain.removeEquation(contVar);
			if (contSubDomain instanceof MembraneSubDomain){
				((MembraneSubDomain)contSubDomain).removeJumpCondition(contVar);
			}
		}
		
		//
		// remove all continuous variables for speciesContextSpec parameters (e.g. initial conditions, diffusion rates, boundary conditions, velocities)
		//
		SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(stochSpeciesContext);
		Parameter[] scsParameters = scs.getParameters();
		for (Parameter parameter : scsParameters) {
			Variable continuousScsParmVariable = mathMapping.getMathSymbolMapping().getVariable(parameter);
			allContinuousVars.remove(continuousScsParmVariable);
		}
		
		//
		// copy ParticleJumpProcess and ParticleProperties to the continuous math
		//
		SubDomain contSubDomain = contMathDesc.getSubDomain(contVar.getDomain().getName());
		SubDomain stochSubDomain = mathDesc.getSubDomain(stochVar.getDomain().getName());
		ParticleProperties particleProperties = stochSubDomain.getParticleProperties(stochVar);
		contSubDomain.addParticleProperties(particleProperties);
	}
		

	//
	// add all ParticleJumpProcesses to the continuous model
	//
	Enumeration<SubDomain> enumStochSubdomains = mathDesc.getSubDomains();
	while (enumStochSubdomains.hasMoreElements()){
		SubDomain stochSubdomain = enumStochSubdomains.nextElement();
		SubDomain contSubdomain = contMathDesc.getSubDomain(stochSubdomain.getName());
		for (ParticleJumpProcess particleJumpProcess : stochSubdomain.getParticleJumpProcesses()){
			//
			// modify "selection list" (particleVariables), probability rate, and actions if referenced particleVariable is to be "forced continuous"
			//
			ParticleVariable[] selectedParticles = particleJumpProcess.getParticleVariables();
			for (ParticleVariable particleVariable : selectedParticles) {
				if (continuousSpeciesParticleVars.contains(particleVariable)){
					particleJumpProcess.remove(particleVariable);

					JumpProcessRateDefinition jumpProcessRateDefinition = particleJumpProcess.getParticleRateDefinition();
					if (jumpProcessRateDefinition instanceof MacroscopicRateConstant){
						MacroscopicRateConstant macroscopicRateConstant = (MacroscopicRateConstant)jumpProcessRateDefinition;
						macroscopicRateConstant.setExpression(Expression.mult(macroscopicRateConstant.getExpression(),new Expression(particleVariable,null)));
					}else if (jumpProcessRateDefinition instanceof InteractionRadius){
						throw new MappingException("cannot adjust interaction radius for reaction process "+particleJumpProcess.getName()+", particle "+particleVariable.getName()+" is continuous");
					}else{
						throw new MappingException("rate definition type "+jumpProcessRateDefinition.getClass().getSimpleName()+" not yet implemented for hybrid PDE/Particle math generation");
					}
				}
				Iterator<Action> iterAction = particleJumpProcess.getActions().iterator();
				while (iterAction.hasNext()){
					Action action = iterAction.next();
					if (continuousSpeciesParticleVars.contains(action.getVar())){
						iterAction.remove();
					}
				}
			}
			if (!particleJumpProcess.getActions().isEmpty()){
				contSubdomain.addParticleJumpProcess(particleJumpProcess);
			}
		}
	}
	
	//
	// add unit conversion factors from the particle math mapping that aren't already present in the continuous math
	//
	for (MathMappingParameter mathMappingParameter : fieldMathMappingParameters) {
		if (mathMappingParameter instanceof UnitFactorParameter){
			String name = mathMappingParameter.getName();
			if (!allContinuousVars.containsKey(name)){
				allContinuousVars.put(name,newFunctionOrConstant(name, mathMappingParameter.getExpression(),null));
			}
		}
	}
	
	//
	// add constants and functions from the particle math that aren't already defined in the continuous math
	//
	Enumeration<Variable> enumVars = mathDesc.getVariables();
	while (enumVars.hasMoreElements()){
		Variable var = enumVars.nextElement();
		if (var instanceof Constant || var instanceof Function){
			String name = var.getName();
			if (!allContinuousVars.containsKey(name)){
				allContinuousVars.put(name,var);
			}
		}
	}
	
	contMathDesc.setAllVariables(allContinuousVars.values().toArray(new Variable[0]));
	
	mathDesc = contMathDesc;	
	
	//
	// add any missing unit conversion factors (they don't depend on anyone else ... can do it at the end)
	//
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		if (fieldMathMappingParameters[i] instanceof UnitFactorParameter){
			GeometryClass geometryClass = fieldMathMappingParameters[i].getGeometryClass();
			Variable variable = newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),geometryClass),fieldMathMappingParameters[i].getGeometryClass());
			if (mathDesc.getVariable(variable.getName())==null){
				mathDesc.addVariable(variable);
			}
		}
	}

	if (!mathDesc.isValid()){
		System.out.println(mathDesc.getVCML_database());
		throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
	}

	System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string begin ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
	System.out.println(mathDesc.getVCML());
	System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string end ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");

}

}
