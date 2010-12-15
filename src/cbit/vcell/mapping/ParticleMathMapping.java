package cbit.vcell.mapping;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.vcell.util.TokenMangler;

import cbit.vcell.client.server.VCellThreadChecker;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecProxyParameter;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.math.Action;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.MacroscopicRateConstant;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.MembraneParticleVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.ParticleJumpProcess;
import cbit.vcell.math.ParticleProbabilityRate;
import cbit.vcell.math.ParticleProperties;
import cbit.vcell.math.ParticleVariable;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VolumeParticleVariable;
import cbit.vcell.math.ParticleProperties.ParticleInitialCondition;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.Feature;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.stoch.MassActionSolver;
import cbit.vcell.units.VCUnitDefinition;
/**
 * The MathMapping class performs the Biological to Mathematical transformation once upon calling getMathDescription().
 * This is not a "live" transformation, so that an updated SimulationContext must be given to a new MathMapping object
 * to get an updated MathDescription.
 */
public class ParticleMathMapping extends MathMapping {

/**
 * This method was created in VisualAge.
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 */
protected ParticleMathMapping(SimulationContext simContext) {
	super(simContext);
}


/**
 * This method was created in VisualAge.
 */
@Override
protected void refreshMathDescription() throws MappingException, MatrixException, MathException, ExpressionException, ModelException {

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
			throw new MappingException(reactionSteps[i].getTerm()+" '"+reactionSteps[i].getName()+"' contains unresolved identifier(s): "+buffer);
		}
	}
	

	//
	// temporarily place all variables in a hashtable (before binding) and discarding duplicates (check for equality)
	//
	VariableHash varHash = new VariableHash();
	
	//
	// verify that all structures are mapped to geometry classes and all geometry classes are mapped to a structure
	//
	Structure structures[] = getSimulationContext().getGeometryContext().getModel().getStructures();
	for (int i = 0; i < structures.length; i++){
		StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(structures[i]);
		if (sm==null || (sm.getGeometryClass() == null)){
			throw new MappingException("model structure '"+structures[i].getName()+"' not mapped to a geometry subdomain");
		}
		if (sm.getUnitSizeParameter()!=null){
			Expression unitSizeExp = sm.getUnitSizeParameter().getExpression();
			if(unitSizeExp != null)
			{
				try {
					double unitSize = unitSizeExp.evaluateConstant();
					if (unitSize != 1.0){
						throw new MappingException("model structure '"+sm.getStructure().getName()+"' unit size = "+unitSize+" != 1.0 ... partial volume or surface mapping not yet supported for particles");
					}
				}catch (ExpressionException e){
					e.printStackTrace(System.out);
					throw new MappingException("couldn't evaluate unit size for model structure '"+sm.getStructure().getName()+"' : "+e.getMessage());
				}
			}
		}
	}
	{
		GeometryClass[] geometryClass = getSimulationContext().getGeometryContext().getGeometry().getGeometryClasses();
		for (int i = 0; i < geometryClass.length; i++){
			Structure[] mappedStructures = getSimulationContext().getGeometryContext().getStructuresFromGeometryClass(geometryClass[i]);
			if (mappedStructures==null || mappedStructures.length==0){
				throw new MappingException("geometryClass '"+geometryClass[i].getName()+"' not mapped from a model structure");
			}
		}
	}

	// deals with model parameters
	ModelParameter[] modelParameters = getSimulationContext().getModel().getModelParameters();
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
			mathDesc = new MathDescription(oldMathDesc.getVersion());
		}else{
			mathDesc = new MathDescription(oldMathDesc.getName());
		}
	}else{
		mathDesc = new MathDescription(getSimulationContext().getName()+"_generated");
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

//	varHash.addVariable(new Constant(getMathSymbol(ReservedSymbol.PI,null),getIdentifierSubstitutions(ReservedSymbol.PI.getExpression(),ReservedSymbol.PI.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(ReservedSymbol.FARADAY_CONSTANT,null),getIdentifierSubstitutions(ReservedSymbol.FARADAY_CONSTANT.getExpression(),ReservedSymbol.FARADAY_CONSTANT.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(ReservedSymbol.FARADAY_CONSTANT_NMOLE,null),getIdentifierSubstitutions(ReservedSymbol.FARADAY_CONSTANT_NMOLE.getExpression(),ReservedSymbol.FARADAY_CONSTANT_NMOLE.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(ReservedSymbol.GAS_CONSTANT,null),getIdentifierSubstitutions(ReservedSymbol.GAS_CONSTANT.getExpression(),ReservedSymbol.GAS_CONSTANT.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(ReservedSymbol.TEMPERATURE,null),getIdentifierSubstitutions(new Expression(getSimulationContext().getTemperatureKelvin()),VCUnitDefinition.UNIT_K,null)));


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
				if (((parameters[i].getRole() == Kinetics.ROLE_CurrentDensity)||(parameters[i].getRole() == Kinetics.ROLE_LumpedCurrent)) && (parameters[i].getExpression()==null || parameters[i].getExpression().isZero())){
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
			initExpr = speciesContextSpecs[i].convertConcentrationToParticles(initExpr);			
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
		SpeciesContextMapping scm = getSpeciesContextMapping(speciesContextSpecs[i].getSpeciesContext());
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
	varHash.addVariable(new Constant(ReservedSymbol.KMOLE.getName(),getIdentifierSubstitutions(ReservedSymbol.KMOLE.getExpression(),ReservedSymbol.KMOLE.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(ReservedSymbol.N_PMOLE.getName(),getIdentifierSubstitutions(ReservedSymbol.N_PMOLE.getExpression(),ReservedSymbol.N_PMOLE.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(ReservedSymbol.KMILLIVOLTS.getName(),getIdentifierSubstitutions(ReservedSymbol.KMILLIVOLTS.getExpression(),ReservedSymbol.KMILLIVOLTS.getUnitDefinition(),null)));
	varHash.addVariable(new Constant(ReservedSymbol.K_GHK.getName(),getIdentifierSubstitutions(ReservedSymbol.K_GHK.getExpression(),ReservedSymbol.K_GHK.getUnitDefinition(),null)));
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
		}
//		StructureMappingParameter sizeParm = sm.getSizeParameter();
//		if (sizeParm!=null){
//			if (getSimulationContext().getGeometry().getDimension()==0){
//				if (sizeParm.getExpression()!=null){
//					varHash.addVariable(newFunctionOrConstant(getMathSymbol(sizeParm,sm.getGeometryClass()),getIdentifierSubstitutions(sizeParm.getExpression(), sizeParm.getUnitDefinition(), sm.getGeometryClass()),sm.getGeometryClass()));
//				}
//			}else{
//				String compartmentName = sm.getGeometryClass().getName();
//				VCUnitDefinition sizeUnit = null;
//				String sizeFunctionName = null;
//				if (sm instanceof MembraneMapping){
//					MembraneMapping mm = (MembraneMapping)sm;
//					sizeUnit = VCUnitDefinition.UNIT_um2;
//					if (mm.getGeometryClass() instanceof SurfaceClass){
//						sizeFunctionName = MathFunctionDefinitions.Function_regionArea_current.getFunctionName();
//					}else if (mm.getGeometryClass() instanceof SubVolume){
//						sizeFunctionName = MathFunctionDefinitions.Function_regionVolume_current.getFunctionName();
//					}
//				}else if (sm instanceof FeatureMapping){
//					sizeUnit = VCUnitDefinition.UNIT_um3;
//					sizeFunctionName = MathFunctionDefinitions.Function_regionVolume_current.getFunctionName();
//				}else{
//					throw new RuntimeException("structure mapping "+sm.getClass().getName()+" not yet supported");
//				}
//				Expression totalVolumeCorrection = sm.getStructureSizeCorrection(getSimulationContext());
//				Expression sizeFunctionExpression = Expression.function(sizeFunctionName, new Expression[] {new Expression("'"+compartmentName+"'")} );
//				sizeFunctionExpression.bindExpression(mathDesc);
//				varHash.addVariable(newFunctionOrConstant(getMathSymbol(sizeParm,sm.getGeometryClass()),getIdentifierSubstitutions(Expression.mult(totalVolumeCorrection,sizeFunctionExpression),sizeUnit,sm.getGeometryClass()),sm.getGeometryClass()));
//
//			}
//		}

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
						System.out.println("WARNING:::: MathMapping.refreshMathDescription() ... assigning boundary condition types not unique");
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
			//
			// if there is a spatially resolved membrane surrounding this subVolume, then create a membraneSubDomain
			//
			structures = getSimulationContext().getGeometryContext().getStructuresFromGeometryClass(surfaceClass);
			Set<SubVolume> sv = surfaceClass.getAdjacentSubvolumes();
			Iterator<SubVolume> iterator = sv.iterator();
			SubVolume innerSubVolume = iterator.next();
			SubVolume outerSubVolume = iterator.next();
			if (innerSubVolume.getName().compareTo(outerSubVolume.getName()) > 0) {
				SubVolume temp = innerSubVolume;
				innerSubVolume = outerSubVolume;
				outerSubVolume = temp;
			}
	
			//
			// create subDomain
			//
			CompartmentSubDomain outerCompartment = mathDesc.getCompartmentSubDomain(outerSubVolume.getName());
			CompartmentSubDomain innerCompartment = mathDesc.getCompartmentSubDomain(innerSubVolume.getName());
	
			MembraneSubDomain memSubDomain = new MembraneSubDomain(innerCompartment,outerCompartment);
			mathDesc.addSubDomain(memSubDomain);
		}
	}
	
	//
	// create Particle Contexts for all Particle Variables
	//
	Enumeration<SpeciesContextMapping> enumSCM = getSpeciesContextMappings();
	while (enumSCM.hasMoreElements()){
		SpeciesContextMapping scm = enumSCM.nextElement();
		SpeciesContext        sc  = scm.getSpeciesContext();
		StructureMapping      sm  = getSimulationContext().getGeometryContext().getStructureMapping(sc.getStructure());
		SpeciesContextSpec    scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(sc);

		if (scm.getVariable() instanceof ParticleVariable && scm.getDependencyExpression()==null){
			ParticleVariable volumeParticleVariable = (ParticleVariable)scm.getVariable();
			
			//
			// initial distribution of particles
			//
			ArrayList<ParticleInitialCondition> particleInitialConditions = new ArrayList<ParticleInitialCondition>();
//Expression initialCount = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialCount),sm.getGeometryClass()));
			Expression initialCount = new Expression(getMathSymbol(scs.getInitialConditionParameter(),sm.getGeometryClass()));
			if (initialCount==null){
				throw new MappingException("initialCount not defined for speciesContext "+scs.getSpeciesContext().getName());
			}
			Expression locationX = new Expression("u");
			Expression locationY = new Expression("u");
			Expression locationZ = new Expression("u");
			particleInitialConditions.add(new ParticleInitialCondition(initialCount, locationX, locationY, locationZ));

			//
			// diffusion
			//
			Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),sm.getGeometryClass()));

			//
			// advection
			//
			Expression velocityX = (scs.getVelocityXParameter().getExpression()==null)?(null) : new Expression(getMathSymbol(scs.getVelocityXParameter(),sm.getGeometryClass()));
			Expression velocityY = (scs.getVelocityYParameter().getExpression()==null)?(null) : new Expression(getMathSymbol(scs.getVelocityYParameter(),sm.getGeometryClass()));
			Expression velocityZ = (scs.getVelocityZParameter().getExpression()==null)?(null) : new Expression(getMathSymbol(scs.getVelocityZParameter(),sm.getGeometryClass()));

			//
			// boundary conditions
			//
			Expression bcXm = (scs.getBoundaryXmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryXmParameter(),sm.getGeometryClass()));
			Expression bcXp = (scs.getBoundaryXpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryXpParameter(),sm.getGeometryClass()));
			Expression bcYm = (scs.getBoundaryYmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryYmParameter(),sm.getGeometryClass()));
			Expression bcYp = (scs.getBoundaryYpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryYpParameter(),sm.getGeometryClass()));
			Expression bcZm = (scs.getBoundaryZmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryZmParameter(),sm.getGeometryClass()));
			Expression bcZp = (scs.getBoundaryZpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryZpParameter(),sm.getGeometryClass()));
			
			ParticleProperties particleProperties = new ParticleProperties(volumeParticleVariable, diffusion, particleInitialConditions);
			SubDomain subDomain = mathDesc.getSubDomain(sm.getGeometryClass().getName());
			subDomain.addParticleProperties(particleProperties);
		}
	}
	for (ReactionStep reactionStep : reactionSteps){
		Kinetics kinetics = reactionStep.getKinetics();
		StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(reactionStep.getStructure());
		GeometryClass reactionStepGeometryClass = sm.getGeometryClass();
		SubDomain subdomain = mathDesc.getSubDomain(reactionStepGeometryClass.getName());

		if (reactionStep instanceof SimpleReaction) { // simple reactions
			// check the reaction rate law to see if we need to decompose a reaction(reversible) into two jump processes.
			// rate constants are important in calculating the probability rate.
			// for Mass Action, we use KForward and KReverse, 
			// for General Kinetics we parse reaction rate J to see if it is in Mass Action form.
			VCUnitDefinition forwardRateUnit = null;
			VCUnitDefinition reverseRateUnit = null;
			Expression forwardRate = null;
			Expression reverseRate = null;
			if (kinetics.getKineticsDescription().equals(KineticsDescription.MassAction)) {
				Parameter forwardRateParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KForward);
				Parameter reverseRateParameter = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_KReverse);
				forwardRate = forwardRateParameter.getExpression();
				forwardRateUnit = forwardRateParameter.getUnitDefinition();
				reverseRate = reverseRateParameter.getExpression();
				reverseRateUnit = reverseRateParameter.getUnitDefinition();
			}
			else if (kinetics.getKineticsDescription().equals(KineticsDescription.General))
			{
				Expression rateExp = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
				rateExp = reactionStep.substitueKineticParameter(rateExp, false);
				MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(rateExp, reactionStep);
				if(maFunc.getForwardRate() == null && maFunc.getReverseRate() == null)
				{
					throw new MappingException("Cannot generate stochastic math mapping for the reaction:" + reactionStep.getName() + "\nLooking for the rate function according to the form of k1*Reactant1^Stoir1*Reactant2^Stoir2...-k2*Product1^Stoip1*Product2^Stoip2.");
				}
				else
				{
					if(maFunc.getForwardRate() != null)
					{
						forwardRate = maFunc.getForwardRate();
						forwardRateUnit = maFunc.getForwardRateUnit();
					}
					if(maFunc.getReverseRate() != null)
					{
						reverseRate = maFunc.getReverseRate();
						reverseRateUnit = maFunc.getReverseRateUnit();
					}
				}
			}
			
			//
			// insert conversion factors to go from micromolar to molecules/um^3.  ... KMOLE.
			//
//			if (forwardRate!=null){
//				if (forwardRateUnit==null){
//					throw new MappingException("units not known");
//				}
//				if (forwardRateUnit.equals(VCUnitDefinition.UNIT_per_s)){
//					// do nothing
//				}else if (forwardRateUnit.equals(VCUnitDefinition.UNIT_pper_s)){
//					// do nothing
//				}else if (forwardRateUnit.equals(VCUnitDefinition.UNIT_per_s)){
//					// do nothing
//				}else if (forwardRateUnit.equals(VCUnitDefinition.UNIT_per_s)){
//					// do nothing
//				}else if (forwardRateUnit.equals(VCUnitDefinition.UNIT_per_s)){
//					// do nothing
//				}else if (forwardRateUnit.equals(VCUnitDefinition.UNIT_per_s)){
//					// do nothing
//				}else if (forwardRateUnit.equals(VCUnitDefinition.UNIT_per_s)){
//					// do nothing
//				}else if (forwardRateUnit.equals(VCUnitDefinition.UNIT_per_s)){
//					// do nothing
//				}else if (forwardRateUnit.equals(VCUnitDefinition.UNIT_per_s)){
//					// do nothing
//				}else if (forwardRateUnit.equals(VCUnitDefinition.UNIT_per_s)){
//					// do nothing
//				}else if (forwardRateUnit.equals(VCUnitDefinition.UNIT_per_s)){
//					// do nothing
//				}else 
//			}
		    
			// if the reaction has forward rate (Mass action,HMMs), or don't have either forward or reverse rate (some other rate laws--like general)
			// we process it as forward reaction
			List<ParticleVariable> reactantParticles = new ArrayList<ParticleVariable>();
			List<ParticleVariable> productParticles = new ArrayList<ParticleVariable>();
			List<Action> forwardActions = new ArrayList<Action>();
			List<Action> reverseActions = new ArrayList<Action>();
			for (ReactionParticipant rp : reactionStep.getReactionParticipants()){
				SpeciesContext sc = rp.getSpeciesContext();
				GeometryClass scGeometryClass = getSimulationContext().getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass();
				String varName = getMathSymbol(sc, scGeometryClass);
				Variable var = mathDesc.getVariable(varName);
				if (var instanceof ParticleVariable){
					ParticleVariable particle = (ParticleVariable)var;
					if (rp instanceof Reactant){
						reactantParticles.add(particle);
					}else if (rp instanceof Product){
						productParticles.add(particle);
					}
					for (int i = 0; i < Math.abs(rp.getStoichiometry()); i++) {
						if (forwardRate!=null) {
							if (rp instanceof Reactant){
								forwardActions.add(Action.createDestroyAction(particle));
							} else if (rp instanceof Product){
								forwardActions.add(Action.createCreateAction(particle));
							}
						}					 
						if (reverseRate!=null) {
							if (rp instanceof Reactant){
								reverseActions.add(Action.createCreateAction(particle));
							} else if (rp instanceof Product){
								reverseActions.add(Action.createDestroyAction(particle));
							}
						}
					}
				}else{
					throw new MappingException("particle variable '"+varName+"' not found");
				}
			}
			if (forwardRate!=null)
			{
				String jpName = TokenMangler.mangleToSName(reactionStep.getName());

				Expression exp = getIdentifierSubstitutions(forwardRate, forwardRateUnit, reactionStepGeometryClass);
				ParticleProbabilityRate partProbRate = new MacroscopicRateConstant(exp);
				
//				List<Action> actions = new ArrayList<Action>();
//				for (ParticleVariable reactantVar : reactantParticles){
//					actions.add(Action.createDestroyAction(reactantVar));
//				}
//				for (ParticleVariable productVar : productParticles){
//					actions.add(Action.createCreateAction(productVar));
//				}
				ParticleJumpProcess forwardProcess = new ParticleJumpProcess(jpName, reactantParticles, partProbRate, forwardActions);
				subdomain.addParticleJumpProcess(forwardProcess);
			}
			if (reverseRate!=null)
			{
				// get jump process name
				String jpName = TokenMangler.mangleToSName(reactionStep.getName()+"_reverse");
				// get probability
				
				Expression exp = getIdentifierSubstitutions(reverseRate, reverseRateUnit, reactionStepGeometryClass);
				ParticleProbabilityRate partProbRate = new MacroscopicRateConstant(exp);
				
//				List<Action> actions = new ArrayList<Action>();
//				for (ParticleVariable productVar : productParticles){
//					actions.add(Action.createDestroyAction(productVar));
//				}
//				for (ParticleVariable reactantVar : reactantParticles){
//					actions.add(Action.createCreateAction(reactantVar));
//				}
				ParticleJumpProcess reverseProcess = new ParticleJumpProcess(jpName, productParticles, partProbRate, reverseActions);
				subdomain.addParticleJumpProcess(reverseProcess);
			}
		}
		else if(reactionStep instanceof FluxReaction)// flux reactions
		{
			throw new MappingException("flux reactions not yet supported for Particle Simulations");
//			
//			//we could set jump processes for general flux rate in forms of p1*Sout + p2*Sin
//			if(kinetics.getKineticsDescription().equals(KineticsDescription.General))
//			{
//				Expression fluxRate = kinetics.getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
//				fluxRate = reactionStep.substitueKineticParameter(fluxRate, false);
//				//we have to pass the math description para to flux solver, coz somehow math description in simulation context is not updated.
//				FluxSolver.FluxFunction fluxFunc = FluxSolver.solveFlux(fluxRate, (FluxReaction)reactionStep);
//				//create jump process for forward flux if it exists.
//				if(fluxFunc.getRateToInside() != null && !fluxFunc.getRateToInside().isZero()) 
//				{
//					//jump process name
//					String jpName = TokenMangler.mangleToSName(reactionStep.getName());//+"_reverse";
//										
//					//we do it here instead of fluxsolver, coz we need to use getMathSymbol0(), structuremapping...etc.
//					Expression rate = fluxFunc.getRateToInside();
//					//get species expression (depend on structure, if mem: Species/mem_Size, if vol: species*KMOLE/vol_size)
//					SpeciesContext scOut = fluxFunc.getSpeciesContextOutside();
//					Expression speciesFactor = null;
//					if(scOut.getStructure() instanceof Membrane) {
//						speciesFactor = Expression.invert(new Expression(scOut.getStructure().getStructureSize().getName()));
//					} else {
//						Expression numExpr = new Expression(ReservedSymbol.KMOLE.getName());
//						Expression denomExpr = new Expression(scOut.getStructure().getStructureSize().getName());
//						speciesFactor =  Expression.div(numExpr, denomExpr);
//					}
//					Expression speciesExp = Expression.mult(speciesFactor, new Expression(scOut.getName()));	
//					//get probability expression by adding factor to rate (rate: rate*size_mem/KMOLE)
//					Expression expr1 = Expression.mult(rate, speciesExp);
//					Expression numeratorExpr = Expression.mult(expr1, new Expression(sm.getStructure().getStructureSize().getName()));
//					Expression denominatorExpr = new Expression(ReservedSymbol.KMOLE.getName());
//					Expression probExp = Expression.div(numeratorExpr, denominatorExpr);
//					probExp.bindExpression(reactionStep);//bind symbol table before substitute identifiers in the reaction step
//
//					MathMapping.ProbabilityParameter probParm = null;
//					try{
//						probParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName,probExp,MathMapping.PARAMETER_ROLE_P,VCUnitDefinition.UNIT_molecules_per_s,reactionSpecs[i]);
//					}catch(PropertyVetoException pve){
//						pve.printStackTrace();
//						throw new MappingException(pve.getMessage());
//					}
//					//add probability to function or constant
//					varHash.addVariable(newFunctionOrConstant(getMathSymbol(probParm,sm.getGeometryClass()),getIdentifierSubstitutions(probExp, VCUnitDefinition.UNIT_molecules_per_s, sm.getGeometryClass()),sm.getGeometryClass()));
//									
//					JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol(probParm,sm.getGeometryClass())));
//					// actions
//					Action action = null;
//					SpeciesContext sc = fluxFunc.getSpeciesContextOutside();
//					
//					if (!simContext.getReactionContext().getSpeciesContextSpec(sc).isConstant()) {
//						SpeciesCountParameter spCountParam = getSpeciesCountParameter(sc);
//						action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, sm.getGeometryClass())),new Expression(-1));
//						jp.addAction(action);
//					}	
//					
//					sc = fluxFunc.getSpeciesContextInside();
//					if (!simContext.getReactionContext().getSpeciesContextSpec(sc).isConstant()) {
//						SpeciesCountParameter spCountParam = getSpeciesCountParameter(sc);
//						action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, sm.getGeometryClass())),new Expression(1));
//						jp.addAction(action);
//					}
//						
//					subDomain.addJumpProcess(jp);
//				}
//				if(fluxFunc.getRateToOutside() != null && !fluxFunc.getRateToOutside().isZero()) 
//				{
//					//jump process name
//					String jpName = TokenMangler.mangleToSName(reactionStep.getName())+PARAMETER_PROBABILITY_RATE_REVERSE_SUFFIX;
//										
//					Expression rate = fluxFunc.getRateToOutside();
//					//get species expression (depend on structure, if mem: Species/mem_Size, if vol: species*KMOLE/vol_size)
//					SpeciesContext scIn = fluxFunc.getSpeciesContextInside();
//					Expression speciesFactor = null;
//					if(scIn.getStructure() instanceof Membrane) {
//						speciesFactor = Expression.invert(new Expression(scIn.getStructure().getStructureSize().getName()));
//					} else {
//						Expression numExpr = new Expression(ReservedSymbol.KMOLE.getName());
//						Expression denomExpr = new Expression(scIn.getStructure().getStructureSize().getName());
//						speciesFactor =  Expression.div(numExpr, denomExpr);
//					}
//					Expression speciesExp = Expression.mult(speciesFactor, new Expression(scIn.getName()));	
//					//get probability expression by adding factor to rate (rate: rate*size_mem/KMOLE)
//					Expression expr1 = Expression.mult(rate, speciesExp);
//					Expression numeratorExpr = Expression.mult(expr1, new Expression(sm.getStructure().getStructureSize().getName()));
//					Expression denominatorExpr = new Expression(ReservedSymbol.KMOLE.getName());
//					Expression probRevExp = Expression.div(numeratorExpr, denominatorExpr);
//					probRevExp.bindExpression(reactionStep);//bind symbol table before substitute identifiers in the reaction step
//					
//					MathMapping.ProbabilityParameter probRevParm = null;
//					try{
//						probRevParm = addProbabilityParameter(PARAMETER_PROBABILITYRATE_PREFIX+jpName,probRevExp,MathMapping.PARAMETER_ROLE_P_reverse,VCUnitDefinition.UNIT_molecules_per_s,reactionSpecs[i]);
//					}catch(PropertyVetoException pve){
//						pve.printStackTrace();
//						throw new MappingException(pve.getMessage());
//					}
//					//add probability to function or constant
//					varHash.addVariable(newFunctionOrConstant(getMathSymbol(probRevParm,sm.getGeometryClass()),getIdentifierSubstitutions(probRevExp, VCUnitDefinition.UNIT_molecules_per_s, sm.getGeometryClass()),sm.getGeometryClass()));
//									
//					JumpProcess jp = new JumpProcess(jpName,new Expression(getMathSymbol(probRevParm,sm.getGeometryClass())));
//					// actions
//					Action action = null;
//					SpeciesContext sc = fluxFunc.getSpeciesContextOutside();
//					if (!simContext.getReactionContext().getSpeciesContextSpec(sc).isConstant()) {
//						SpeciesCountParameter spCountParam = getSpeciesCountParameter(sc);
//						action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, sm.getGeometryClass())),new Expression(1));
//						jp.addAction(action);
//					}
//						
//					sc = fluxFunc.getSpeciesContextInside();
//					if (!simContext.getReactionContext().getSpeciesContextSpec(sc).isConstant()) {
//						SpeciesCountParameter spCountParam = getSpeciesCountParameter(sc);
//						action = Action.createIncrementAction(varHash.getVariable(getMathSymbol(spCountParam, sm.getGeometryClass())),new Expression(-1));
//						jp.addAction(action);
//					}
//					
//					subDomain.addJumpProcess(jp);
//				}
//			}
		}//end of if (simplereaction)...else if(fluxreaction)
	} // end of reaction step loop
	
	

	if (!mathDesc.isValid()){
		System.out.println(mathDesc.getVCML_database());
		throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
	}

System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string begin ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
System.out.println(mathDesc.getVCML());
System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string end ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
}

/**
 * This method was created in VisualAge.
 */
@Override
protected void refreshSpeciesContextMappings() throws ExpressionException, MappingException, MathException {
	
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
		if (scs.isConstant()){
			Expression initCount = null;
			if(getSimulationContext().isUsingConcentration()) {
				SpeciesContextSpec.SpeciesContextSpecParameter initConcParm =  scs.getInitialConcentrationParameter();
				initCount = scs.convertConcentrationToParticles(new Expression(initConcParm, getNameScope()));
			} else {
				SpeciesContextSpec.SpeciesContextSpecParameter initCountParm = scs.getInitialCountParameter();
				initCount = new Expression(initCountParm, getNameScope());
			}
			
			scm.setDependencyExpression(initCount);
		}
		//
		// test if participant in fast reaction step, request elimination if possible
		//
//		scm.setFastParticipant(false);
//		ReactionSpec reactionSpecs[] = getSimulationContext().getReactionContext().getReactionSpecs();
//		for (int j=0;j<reactionSpecs.length;j++){
//			ReactionSpec reactionSpec = reactionSpecs[j];
//			if (reactionSpec.isExcluded()){
//				continue;
//			}
//			ReactionStep rs = reactionSpec.getReactionStep();
//			if (rs instanceof SimpleReaction && rs.countNumReactionParticipants(scs.getSpeciesContext()) > 0){
//				if (reactionSpec.isFast()){
//					scm.setFastParticipant(true);
//				}
//			}
//		}
		speciesContextMappingList.addElement(scm);
	}
}


/**
 * This method was created in VisualAge.
 * @Override
 */
@Override
protected void refreshVariables() throws MappingException {

//System.out.println("MathMapping.refreshVariables()");

	//
	// non-constant dependent variables require a function
	//
	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = enum1.nextElement();
		SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
		if (scm.getDependencyExpression() != null && !scs.isConstant()){
			//scm.setVariable(new Function(scm.getSpeciesContext().getName(),scm.getDependencyExpression()));
			scm.setVariable(null);
		}
	}
	
	//
	// non-constant independent variables require either a membrane or volume variable
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		SpeciesContextSpec scs = getSimulationContext().getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
		if (scm.getDependencyExpression() == null && (!scs.isConstant() || getSimulationContext().hasEventAssignment(scs.getSpeciesContext()))){
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
		}
	}

}


@Override
protected void refresh() throws MappingException, ExpressionException, MatrixException, MathException, ModelException {
	VCellThreadChecker.checkCpuIntensiveInvocation();
	
	localIssueList.clear();
	//refreshKFluxParameters();
	refreshSpeciesContextMappings();
	//refreshStructureAnalyzers();
	refreshVariables();
	refreshLocalNameCount();
	refreshMathDescription();
}

}