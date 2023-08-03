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
import java.beans.PropertyVetoException;
import java.util.*;

import org.vcell.util.BeanUtils;
import org.vcell.util.Pair;
import org.vcell.util.VCellThreadChecker;

import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.BioEvent.BioEventParameterType;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.MicroscopeMeasurement.ConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.ExperimentalPSF;
import cbit.vcell.mapping.MicroscopeMeasurement.GaussianConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.ProjectionZKernel;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.SimulationContext.SimulationContextParameter;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecProxyParameter;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.mapping.potential.CurrentClampElectricalDevice;
import cbit.vcell.mapping.potential.ElectricalDevice;
import cbit.vcell.mapping.potential.MembraneElectricalDevice;
import cbit.vcell.mapping.potential.PotentialMapping;
import cbit.vcell.mapping.potential.VoltageClampElectricalDevice;
import cbit.vcell.mapping.spatial.PointObject;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.mapping.spatial.SurfaceRegionObject;
import cbit.vcell.mapping.spatial.VolumeRegionObject;
import cbit.vcell.mapping.spatial.processes.PointKinematics;
import cbit.vcell.mapping.spatial.processes.PointLocation;
import cbit.vcell.mapping.spatial.processes.SpatialProcess;
import cbit.vcell.mapping.spatial.processes.SpatialProcess.SpatialProcessParameterType;
import cbit.vcell.mapping.spatial.processes.SurfaceKinematics;
import cbit.vcell.mapping.spatial.processes.VolumeKinematics;
import cbit.vcell.math.CommentedBlockObject;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.ComputeMembraneMetricEquation;
import cbit.vcell.math.ComputeMembraneMetricEquation.MembraneMetricComponent;
import cbit.vcell.math.Constant;
import cbit.vcell.math.ConvFunctionDefinition;
import cbit.vcell.math.ConvolutionDataGenerator;
import cbit.vcell.math.ConvolutionDataGenerator.GaussianConvolutionDataGeneratorKernel;
import cbit.vcell.math.Equation;
import cbit.vcell.math.Event;
import cbit.vcell.math.Event.Delay;
import cbit.vcell.math.FastInvariant;
import cbit.vcell.math.FastRate;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.FieldFunctionDefinition;
import cbit.vcell.math.Function;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.MathUtilities;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.PointSubDomain;
import cbit.vcell.math.PointVariable;
import cbit.vcell.math.ProjectionDataGenerator;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableHash;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRegionEquation;
import cbit.vcell.math.VolumeRegionVariable;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.ReservedSymbolRole;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.ASTFuncNode.FunctionType;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
/**
 * The MathMapping class performs the Biological to Mathematical transformation once upon calling getMathDescription().
 * This is not a "live" transformation, so that an updated SimulationContext must be given to a new MathMapping object
 * to get an updated MathDescription.
 * 		capacitances must not be overridden and must be constant (used as literals in KVL)
 */
public class DiffEquMathMapping extends AbstractMathMapping {
	protected static final String MATH_FUNC_SUFFIX_EVENTASSIGN_OR_RATERULE_INIT = "_protocol_init";
	protected static final String MATH_FUNC_SUFFIX_EVENTASSIGN_OR_RATERULE_INIT_old = "_init";
	private static final String MATH_FUNC_SUFFIX_RATERULE_RATE = "_rate";
	private static final String MATH_FUNC_SUFFIX_ASSIGNMENTRULE = "_assign";
	public static final int PARAMETER_ROLE_TOTALMASS = 0;
	public static final int PARAMETER_ROLE_DEPENDENT_VARIABLE = 1;
	public static final int PARAMETER_ROLE_EVENTASSIGN_OR_RATERULE_INITCONDN = 7;
	public static final int PARAMETER_ROLE_RATERULE_RATE = 8;
	private Vector<StructureAnalyzer> structureAnalyzerList = new Vector<StructureAnalyzer>();

	protected DiffEquMathMapping(SimulationContext simContext, MathMappingCallback callback, NetworkGenerationRequirements networkGenerationRequirements) {
		super(simContext,callback,networkGenerationRequirements);
}

private EventAssignmentOrRateRuleInitParameter addEventAssignmentOrRateRuleInitParameter(SymbolTableEntry targetName, Expression expr, int role, VCUnitDefinition unitDefn) throws PropertyVetoException {

	String argName = targetName.getName() + MATH_FUNC_SUFFIX_EVENTASSIGN_OR_RATERULE_INIT;
	EventAssignmentOrRateRuleInitParameter newParameter = new EventAssignmentOrRateRuleInitParameter(argName, expr, role, unitDefn);
	MathMappingParameter previousParameter = getMathMappingParameter(argName);
	if(previousParameter != null) {
		System.out.println("MathMapping.MathMappingParameter addEventAssignInitParameter found duplicate parameter for name " + targetName.getName());
		if(!previousParameter.compareEqual(newParameter)) {
			throw new RuntimeException("MathMapping.MathMappingParameter addEventAssignInitParameter found duplicate parameter for name '" + targetName.getName() + "'.");
		}
		return (EventAssignmentOrRateRuleInitParameter)previousParameter;
	}
	MathMappingParameter newParameters[] = (MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters, newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

private RateRuleRateParameter addRateRuleRateParameter(SymbolTableEntry target, Expression expr, int role, VCUnitDefinition unitDefn) throws PropertyVetoException {

	String argName = target.getName() + MATH_FUNC_SUFFIX_RATERULE_RATE;
	RateRuleRateParameter newParameter = new RateRuleRateParameter(argName, expr, role, unitDefn);
	MathMappingParameter previousParameter = getMathMappingParameter(argName);
	if(previousParameter != null) {
		System.out.println("MathMapping.MathMappingParameter addRateRuleRateParameter found duplicate parameter for name " + argName);
		if(!previousParameter.compareEqual(newParameter)) {
			throw new RuntimeException("MathMapping.MathMappingParameter addRateRuleRateParameter found duplicate parameter for name '" + argName + "'.");
		}
		return (RateRuleRateParameter)previousParameter;
	}
	MathMappingParameter newParameters[] = (MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters, newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

private MembraneStructureAnalyzer getMembraneStructureAnalyzer(SurfaceClass surfaceClass) {
	Enumeration<StructureAnalyzer> enum1 = getStructureAnalyzers();
	while (enum1.hasMoreElements()){
		StructureAnalyzer sa = enum1.nextElement();
		if (sa instanceof MembraneStructureAnalyzer){
			MembraneStructureAnalyzer msa = (MembraneStructureAnalyzer)sa;
			if (msa.getSurfaceClass()==surfaceClass){
				return msa;
			}
		}
	}
	return null;
}


/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 */
private java.util.Enumeration<StructureAnalyzer> getStructureAnalyzers() {
	return structureAnalyzerList.elements();
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.StructureAnalyzer
 * @param subVolume cbit.vcell.geometry.SubVolume
 */
private VolumeStructureAnalyzer getVolumeStructureAnalyzer(SubVolume subVolume) {
	Enumeration<StructureAnalyzer> enum1 = getStructureAnalyzers();
	while (enum1.hasMoreElements()){
		StructureAnalyzer sa = enum1.nextElement();
		if (sa instanceof VolumeStructureAnalyzer){
			VolumeStructureAnalyzer vsa = (VolumeStructureAnalyzer)sa;
			if (vsa.getSubVolume()==subVolume){
				return vsa;
			}
		}
	}
	return null;
}

@Override
protected void refresh(MathMappingCallback callback) throws MappingException, ExpressionException, MatrixException, MathException, ModelException {
//	System.out.println("MathMapping.refresh Start");
	VCellThreadChecker.checkCpuIntensiveInvocation();

	localIssueList.clear();
//	refreshKFluxParameters();
	refreshSpeciesContextMappings();
	refreshStructureAnalyzers();

	if(callback != null) {
		callback.setProgressFraction(52.0f/100.0f);
	}
	refreshVariables();
	refreshLocalNameCount();
	resolveMathSymbolConflicts();
	refreshMathDescription();        // we create math based on the transformed sim context
	reconcileWithOriginalModel();    // we relate the symbols in the math to the symbols in the original sim context
//	System.out.println("MathMapping.refresh End");
}

/**
 * see if names match
 * @param name may not be null
 * @param cbo may null
 * @return true if cbo != null and names match
 */
private boolean sameName(String name, CommentedBlockObject cbo) {
	if (cbo != null) {
		return name.equals(cbo.getName());
	}
	return false;
}

private static class MembraneSubdomainContext {
	final MembraneSubDomain membraneSubdomain;
	final Domain domain;
	final SurfaceClass surfaceClass;
	final SubVolume innerSubvolume;
	final SubVolume outerSubvolume;
	final SurfaceRegionObject[] surfaceRegionObjects;

	private MembraneSubdomainContext(MembraneSubDomain membraneSubdomain, Domain domain, SurfaceClass surfaceClass,
									 SubVolume innerSubvolume, SubVolume outerSubvolume, SurfaceRegionObject[] surfaceRegionObjects) {
		super();
		this.membraneSubdomain = membraneSubdomain;
		this.domain = domain;
		this.surfaceClass = surfaceClass;
		this.innerSubvolume = innerSubvolume;
		this.outerSubvolume = outerSubvolume;
		this.surfaceRegionObjects = surfaceRegionObjects;
	}
}
private static class CompartmentSubdomainContext {
	final CompartmentSubDomain compartmentSubdomain;
	final SubVolume subvolume;
	final Domain domain;

	private CompartmentSubdomainContext(CompartmentSubDomain compartmentSubdomain, SubVolume subvolume, Domain domain) {
		super();
		this.compartmentSubdomain = compartmentSubdomain;
		this.subvolume = subvolume;
		this.domain = domain;
	}
}

/**
 * This method was created in VisualAge.
 */
@SuppressWarnings("deprecation")
private void refreshMathDescription() throws MappingException, MatrixException, MathException, ExpressionException, ModelException {

	//All sizes must be set for new ODE models and ratios must be set for old ones.
	simContext.checkValidity();

	//
	// temporarily place all variables in a hashtable (before binding) and discarding duplicates (check for equality)
	//
	VariableHash varHash = new VariableHash();
	StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();

	//
	// verify that all structures are mapped to subvolumes and all subvolumes are mapped to a structure
	//
	//Structure structures[] =
	simContext.getGeometryContext().getModel().getStructures();
//	for (int i = 0; i < structures.length; i++){
//		StructureMapping sm = simContext.getGeometryContext().getStructureMapping(structures[i]);
//		if (sm==null || (sm.getGeometryClass() == null)){
//			localIssueList.add(new Issue(structures[i], IssueCategory.StructureNotMapped,"In Application '" + simContext.getName() + "', model structure '"+structures[i].getName()+"' not mapped to a geometry subdomain",Issue.SEVERITY_WARNING));
//		}
//	}
	//SubVolume subVolumes[] = simContext.getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
//	for (int i = 0; i < subVolumes.length; i++){
//		Structure[] mappedStructures = simContext.getGeometryContext().getStructuresFromGeometryClass(subVolumes[i]);
//		if (mappedStructures==null || mappedStructures.length==0){
//			localIssueList.add(new Issue(subVolumes[i], IssueCategory.GeometryClassNotMapped,"In Application '" + simContext.getName() + "', geometry subVolume '"+subVolumes[i].getName()+"' not mapped from a model structure",Issue.SEVERITY_WARNING));
//		}
//	}

	// deals with model parameters
	HashMap<VolVariable, EventAssignmentOrRateRuleInitParameter> eventVolVarHash = new HashMap<VolVariable, EventAssignmentOrRateRuleInitParameter>();
	HashMap<Variable, RateRuleRateParameter> rateRuleRateParamHash = new HashMap<Variable, RateRuleRateParameter>();
	ArrayList<SymbolTableEntry> rateRuleVarTargets = new ArrayList<SymbolTableEntry>();
	ArrayList<SymbolTableEntry> assignmentRuleVarTargets = new ArrayList<SymbolTableEntry>();
	ArrayList<SymbolTableEntry> eventAssignTargets = new ArrayList<SymbolTableEntry>();

	Model model = simContext.getModel();
	ModelUnitSystem modelUnitSystem = model.getUnitSystem();
	VCUnitDefinition timeUnit = modelUnitSystem.getTimeUnit();
	ModelParameter[] modelParameters = model.getModelParameters();
	if (simContext.getGeometry().getDimension() == 0) {
		//
		// global parameters from model (that presently are constants)
		//
		BioEvent[] bioEvents = simContext.getBioEvents();
		if (bioEvents != null && bioEvents.length > 0) {
			for (BioEvent be : bioEvents) {
				ArrayList<EventAssignment> eventAssignments = be.getEventAssignments();
				if(eventAssignments != null){
					for (EventAssignment ea : eventAssignments) {
						if (!eventAssignTargets.contains(ea.getTarget())) {
							eventAssignTargets.add(ea.getTarget());
						}
					}
				}
			}
		}

		RateRule[] rrs = simContext.getRateRules();
		if (rrs != null && rrs.length > 0) {
			for (RateRule rr : rrs) {
				SymbolTableEntry rrVar = rr.getRateRuleVar();
				if (!rateRuleVarTargets.contains(rrVar)) {
					rateRuleVarTargets.add(rrVar);
				}
			}
		}
		AssignmentRule[] ars = simContext.getAssignmentRules();
		if (ars != null && ars.length > 0) {
			for (AssignmentRule ar : ars) {
				SymbolTableEntry arVar = ar.getAssignmentRuleVar();
				if (!assignmentRuleVarTargets.contains(arVar)) {
					assignmentRuleVarTargets.add(arVar);
				}
			}
		}

		for (int j=0;j<modelParameters.length;j++){
			Expression modelParamExpr = modelParameters[j].getExpression();
			GeometryClass geometryClass = getDefaultGeometryClass(modelParamExpr);
			VCUnitDefinition paramUnit = modelParameters[j].getUnitDefinition();
			modelParamExpr = getIdentifierSubstitutions(modelParamExpr, paramUnit, geometryClass);
//			if (eventAssignTargets.contains(modelParameters[j]) || rateRuleVarTargets.contains(modelParameters[j])) {
			if (eventAssignTargets.contains(modelParameters[j])) {
				EventAssignmentOrRateRuleInitParameter eap = null;
				try {
					eap = addEventAssignmentOrRateRuleInitParameter(modelParameters[j], modelParamExpr, PARAMETER_ROLE_EVENTASSIGN_OR_RATERULE_INITCONDN, paramUnit);
				} catch (PropertyVetoException e) {
					throw new MappingException(e.getMessage(), e);
				}

				if(geometryClass == null) {
					GeometryClass[] geometryClasses = simContext.getGeometryContext().getGeometry().getGeometryClasses();
					geometryClass = geometryClasses[0];
				}
				Domain domain = null;
				if(geometryClass != null) {
					domain = new Domain(geometryClass);    // the volume variable will look like Compartment::g0 rather than just g0
				}
				VolVariable volVar = new VolVariable(modelParameters[j].getName(), domain);
				varHash.addVariable(volVar);
				eventVolVarHash.put(volVar, eap);
			} else if(rateRuleVarTargets.contains(modelParameters[j])) {
				; // do nothing, will do elsewhere
			} else if(assignmentRuleVarTargets.contains(modelParameters[j])) {
				; // do nothing, will do elsewhere
			} else {
				Variable variable = newFunctionOrConstant(getMathSymbol(modelParameters[j], geometryClass), modelParamExpr, geometryClass);
				varHash.addVariable(variable);
			}
		}
	} else {
		for (int j = 0; j < modelParameters.length; j++){
			Expression modelParamExpr = modelParameters[j].getExpression();
			GeometryClass geometryClass = getDefaultGeometryClass(modelParamExpr);
			modelParamExpr = getIdentifierSubstitutions(modelParamExpr, modelParameters[j].getUnitDefinition(), geometryClass);
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(modelParameters[j], geometryClass), modelParamExpr,geometryClass));
		}
	}

	//
	// add application parameters
	//
	for (SimulationContextParameter scParameter : simContext.getSimulationContextParameters()){
		Expression scParameterExpression = scParameter.getExpression();
		GeometryClass gc = getDefaultGeometryClass(scParameterExpression);
		varHash.addVariable(newFunctionOrConstant(getMathSymbol(scParameter, gc), getIdentifierSubstitutions(scParameter.getExpression(), scParameter.getUnitDefinition(), gc), gc));
	}

	//
	// add functions for field data symbols
	//
	for (DataSymbol dataSymbol : simContext.getDataContext().getDataSymbols()){
		if (dataSymbol instanceof FieldDataSymbol){
			FieldDataSymbol fieldDataSymbol = (FieldDataSymbol)dataSymbol;
			GeometryClass geometryClass = null;
			FieldFunctionArguments ffs = new FieldFunctionArguments(fieldDataSymbol.getExternalDataIdentifier().getName(), fieldDataSymbol.getFieldDataVarName(),
					new Expression(fieldDataSymbol.getFieldDataVarTime()), VariableType.getVariableTypeFromVariableTypeName(fieldDataSymbol.getFieldDataVarType()));
			Expression exp = new Expression(ffs.infix());
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(dataSymbol, geometryClass),getIdentifierSubstitutions(exp,dataSymbol.getUnitDefinition(),geometryClass),geometryClass));
		}else{
			throw new RuntimeException("In Application '" + simContext.getName() + "', dataSymbol type '"+dataSymbol.getClass().getName()+"' not yet supported for math generation");
		}
	}
	//
	// gather only those reactionSteps that are not "excluded"
	//
	ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
	Vector<ReactionStep> rsList = new Vector<ReactionStep>();
	for (int i = 0; i < reactionSpecs.length; i++){
		if (reactionSpecs[i].isExcluded()==false){
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
			throw new MappingException("In Application '" + simContext.getName() + "', " + reactionSteps[i].getDisplayType()+" '"+reactionSteps[i].getName()+"' contains unresolved identifier(s): "+buffer);
		}
	}

	//
	// create new MathDescription (based on simContext's previous MathDescription if possible)
	//
	MathDescription oldMathDesc = simContext.getMathDescription();
	mathDesc = null;
	if (oldMathDesc != null){
		if (oldMathDesc.getVersion() != null){
			mathDesc = new MathDescription(oldMathDesc.getVersion(), mathSymbolMapping);
		}else{
			mathDesc = new MathDescription(oldMathDesc.getName(), mathSymbolMapping);
		}
	}else{
		mathDesc = new MathDescription(simContext.getName()+"_generated", mathSymbolMapping);
	}

	//
	// volume variables
	//
	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = enum1.nextElement();
		if (scm.getVariable() instanceof VolVariable){
			if (!(varHash.getVariable(scm.getVariable().getName()) instanceof VolVariable)){
				varHash.addVariable(scm.getVariable());
			}
		}
	}
	//
	// membrane variables
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable() instanceof MemVariable){
			varHash.addVariable(scm.getVariable());
		}
	}

	//
	// volume region variables
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable() instanceof VolumeRegionVariable){
			varHash.addVariable(scm.getVariable());
		}
	}

	//
	// membrane region variables
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable() instanceof MembraneRegionVariable){
			varHash.addVariable(scm.getVariable());
		}
	}

	//
	// add compartment and membrane subdomains
	//
	ArrayList<CompartmentSubdomainContext> compartmentSubdomainContexts = new ArrayList<CompartmentSubdomainContext>();
	ArrayList<MembraneSubdomainContext> membraneSubdomainContexts = new ArrayList<MembraneSubdomainContext>();
	addSubdomains(model, compartmentSubdomainContexts, membraneSubdomainContexts);

	addSpatialProcesses(varHash, compartmentSubdomainContexts, membraneSubdomainContexts); // membrane velocities set on MembraneSubdomains later.

	varHash.addVariable(new Constant(getMathSymbol(model.getPI_CONSTANT(),null),getIdentifierSubstitutions(model.getPI_CONSTANT().getExpression(),model.getPI_CONSTANT().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getFARADAY_CONSTANT(),null),getIdentifierSubstitutions(model.getFARADAY_CONSTANT().getExpression(),model.getFARADAY_CONSTANT().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getFARADAY_CONSTANT_NMOLE(),null),getIdentifierSubstitutions(model.getFARADAY_CONSTANT_NMOLE().getExpression(),model.getFARADAY_CONSTANT_NMOLE().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getGAS_CONSTANT(),null),getIdentifierSubstitutions(model.getGAS_CONSTANT().getExpression(),model.getGAS_CONSTANT().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getTEMPERATURE(),null),getIdentifierSubstitutions(new Expression(simContext.getTemperatureKelvin()), model.getTEMPERATURE().getUnitDefinition(),null)));

	//
	// only calculate potential if at least one MembraneMapping has CalculateVoltage == true
	//
	boolean bCalculatePotential = false;
	for (int i = 0; i < structureMappings.length; i++){
		if (structureMappings[i] instanceof MembraneMapping){
			if (((MembraneMapping)structureMappings[i]).getCalculateVoltage()){
				bCalculatePotential = true;
			}
		}
	}

	potentialMapping = new PotentialMapping(simContext,this);
	if (bCalculatePotential){
		potentialMapping.computeMath();
		//
		// copy functions for currents and constants for capacitances
		//
		ElectricalDevice devices[] = potentialMapping.getElectricalDevices();
		for (int j = 0; j < devices.length; j++){

			if (devices[j] instanceof MembraneElectricalDevice){
				MembraneElectricalDevice membraneElectricalDevice = (MembraneElectricalDevice)devices[j];
				MembraneMapping memMapping = membraneElectricalDevice.getMembraneMapping();
				Parameter specificCapacitanceParm = memMapping.getParameterFromRole(MembraneMapping.ROLE_SpecificCapacitance);
				varHash.addVariable(new Constant(getMathSymbol(specificCapacitanceParm,memMapping.getGeometryClass()),
						getIdentifierSubstitutions(specificCapacitanceParm.getExpression(),specificCapacitanceParm.getUnitDefinition(),memMapping.getGeometryClass())));

				ElectricalDevice.ElectricalDeviceParameter transmembraneCurrentParm = membraneElectricalDevice.getParameterFromRole(ElectricalDevice.ROLE_TransmembraneCurrent);
				ElectricalDevice.ElectricalDeviceParameter totalCurrentParm = membraneElectricalDevice.getParameterFromRole(ElectricalDevice.ROLE_TotalCurrent);
				ElectricalDevice.ElectricalDeviceParameter capacitanceParm = membraneElectricalDevice.getParameterFromRole(ElectricalDevice.ROLE_Capacitance);

				GeometryClass geometryClass = membraneElectricalDevice.getMembraneMapping().getGeometryClass();
				if (totalCurrentParm!=null && /* totalCurrentDensityParm.getExpression()!=null && */ memMapping.getCalculateVoltage()){
					Expression totalCurrentDensityExp = (totalCurrentParm.getExpression()!=null)?(totalCurrentParm.getExpression()):(new Expression(0.0));
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrentParm,geometryClass),
							getIdentifierSubstitutions(totalCurrentDensityExp,totalCurrentParm.getUnitDefinition(),geometryClass),
							geometryClass));
				}
				if (transmembraneCurrentParm!=null && transmembraneCurrentParm.getExpression()!=null && memMapping.getCalculateVoltage()){
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(transmembraneCurrentParm,geometryClass),
							getIdentifierSubstitutions(transmembraneCurrentParm.getExpression(),transmembraneCurrentParm.getUnitDefinition(),geometryClass),
							geometryClass));
				}
				if (capacitanceParm!=null && capacitanceParm.getExpression()!=null && memMapping.getCalculateVoltage()){
					StructureMappingParameter sizeParameter = membraneElectricalDevice.getMembraneMapping().getSizeParameter();
					if (simContext.getGeometry().getDimension() == 0 && (sizeParameter.getExpression() == null || sizeParameter.getExpression().isZero())) {
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(capacitanceParm,geometryClass),
								getIdentifierSubstitutions(Expression.mult(memMapping.getNullSizeParameterValue(), specificCapacitanceParm.getExpression()),capacitanceParm.getUnitDefinition(),geometryClass),geometryClass));
					} else {
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(capacitanceParm,geometryClass),
								getIdentifierSubstitutions(capacitanceParm.getExpression(),capacitanceParm.getUnitDefinition(),geometryClass),geometryClass));
					}
				}
				//
				//
				// membrane ode
				//
				if (membraneElectricalDevice.getDependentVoltageExpression()==null){  // is Voltage Independent?
					StructureMapping.StructureMappingParameter initialVoltageParm = memMapping.getInitialVoltageParameter();
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(initialVoltageParm,memMapping.getGeometryClass()),
							getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),memMapping.getGeometryClass()),
							memMapping.getGeometryClass()));
				}
				//
				// membrane forced potential
				//
				else {
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(memMapping.getMembrane().getMembraneVoltage(),memMapping.getGeometryClass()),
							getIdentifierSubstitutions(membraneElectricalDevice.getDependentVoltageExpression(),memMapping.getMembrane().getMembraneVoltage().getUnitDefinition(),memMapping.getGeometryClass()),
							memMapping.getGeometryClass()));
				}
			}else if (devices[j] instanceof CurrentClampElectricalDevice){
				CurrentClampElectricalDevice currentClampDevice = (CurrentClampElectricalDevice)devices[j];
				// total current = current source (no capacitance)
				Parameter totalCurrentParm = currentClampDevice.getParameterFromRole(CurrentClampElectricalDevice.ROLE_TotalCurrent);
				Parameter currentParm = currentClampDevice.getParameterFromRole(CurrentClampElectricalDevice.ROLE_TransmembraneCurrent);
				//Parameter dependentVoltage = currentClampDevice.getCurrentClampStimulus().getVoltageParameter();
				Feature deviceElectrodeFeature = currentClampDevice.getCurrentClampStimulus().getElectrode().getFeature();
				Feature groundElectrodeFeature = simContext.getGroundElectrode().getFeature();
				Membrane membrane = model.getStructureTopology().getMembrane(deviceElectrodeFeature, groundElectrodeFeature);
				GeometryClass geometryClass = null;
				if (membrane!=null){
					StructureMapping membraneStructureMapping = simContext.getGeometryContext().getStructureMapping(membrane);
					geometryClass = membraneStructureMapping.getGeometryClass();
				}

				varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrentParm,geometryClass),getIdentifierSubstitutions(totalCurrentParm.getExpression(),totalCurrentParm.getUnitDefinition(),geometryClass),geometryClass));
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(currentParm,geometryClass),getIdentifierSubstitutions(currentParm.getExpression(),currentParm.getUnitDefinition(),geometryClass),geometryClass));
				//varHash.addVariable(newFunctionOrConstant(getMathSymbol(dependentVoltage,null),getIdentifierSubstitutions(currentClampDevice.getDependentVoltageExpression(),dependentVoltage.getUnitDefinition(),null)));

				//
				// add user-defined parameters
				//
				ElectricalDevice.ElectricalDeviceParameter[] parameters = currentClampDevice.getParameters();
				for (int k = 0; k < parameters.length; k++){
					if (parameters[k].getExpression()!=null){ // guards against voltage parameters that are "variable".
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameters[k],null),getIdentifierSubstitutions(parameters[k].getExpression(),parameters[k].getUnitDefinition(),geometryClass),geometryClass));
					}
				}
			}else if (devices[j] instanceof VoltageClampElectricalDevice){
				VoltageClampElectricalDevice voltageClampDevice = (VoltageClampElectricalDevice)devices[j];
				Feature deviceElectrodeFeature = voltageClampDevice.getVoltageClampStimulus().getElectrode().getFeature();
				Feature groundElectrodeFeature = simContext.getGroundElectrode().getFeature();
				Membrane membrane = model.getStructureTopology().getMembrane(deviceElectrodeFeature, groundElectrodeFeature);
				GeometryClass geometryClass = null;
				if (membrane!=null){
					StructureMapping membraneStructureMapping = simContext.getGeometryContext().getStructureMapping(membrane);
					geometryClass = membraneStructureMapping.getGeometryClass();
				}
				// total current = current source (no capacitance)
				Parameter totalCurrent = voltageClampDevice.getParameterFromRole(VoltageClampElectricalDevice.ROLE_TotalCurrent);
				Parameter totalCurrentParm = voltageClampDevice.getParameterFromRole(VoltageClampElectricalDevice.ROLE_TotalCurrent);
				Parameter voltageParm = voltageClampDevice.getParameterFromRole(VoltageClampElectricalDevice.ROLE_Voltage);

				varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrent,geometryClass),getIdentifierSubstitutions(totalCurrent.getExpression(),totalCurrent.getUnitDefinition(),geometryClass),geometryClass));
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrentParm,geometryClass),getIdentifierSubstitutions(totalCurrentParm.getExpression(),totalCurrentParm.getUnitDefinition(),geometryClass),geometryClass));
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(voltageParm,geometryClass),getIdentifierSubstitutions(voltageParm.getExpression(),voltageParm.getUnitDefinition(),geometryClass),geometryClass));

				//
				// add user-defined parameters
				//
				ElectricalDevice.ElectricalDeviceParameter[] parameters = voltageClampDevice.getParameters();
				for (int k = 0; k < parameters.length; k++){
					if (parameters[k].getRole() == ElectricalDevice.ROLE_UserDefined){
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameters[k],geometryClass),getIdentifierSubstitutions(parameters[k].getExpression(),parameters[k].getUnitDefinition(),geometryClass),geometryClass));
					}
				}
			}
		}
	} else {    // (bCalculatePotential == false) ... don't solve for any potentials, but still need them
		//
		// copy functions for currents and constants for capacitances
		//
		for (int j = 0; j < structureMappings.length; j++){
			if (structureMappings[j] instanceof MembraneMapping){
				MembraneMapping memMapping = (MembraneMapping)structureMappings[j];
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(memMapping.getMembrane().getMembraneVoltage(),memMapping.getGeometryClass()),getIdentifierSubstitutions(memMapping.getInitialVoltageParameter().getExpression(),memMapping.getInitialVoltageParameter().getUnitDefinition(),memMapping.getGeometryClass()),memMapping.getGeometryClass()));
			}
		}
	}

	//
	// add constants for R,F,T and Initial Voltages
	//
	for (int j=0;j<structureMappings.length;j++){
		if (structureMappings[j] instanceof MembraneMapping){
			MembraneMapping membraneMapping = (MembraneMapping)structureMappings[j];
			Membrane.MembraneVoltage membraneVoltage = membraneMapping.getMembrane().getMembraneVoltage();
			ElectricalDevice membraneDevices[] = potentialMapping.getElectricalDevices(membraneMapping.getMembrane());
			//ElectricalDevice membraneDevice = null;
			for (int i = 0; i < membraneDevices.length; i++){
				if (membraneDevices[i].hasCapacitance() && membraneDevices[i].getDependentVoltageExpression()==null){
					GeometryClass geometryClass = membraneMapping.getGeometryClass();
					if(geometryClass == null){
						throw new MappingException("Application '"+getSimulationContext().getName()+"'\nGeometry->StructureMapping->("+structureMappings[j].getStructure().getTypeName()+")'"+structureMappings[j].getStructure().getName()+"' must be mapped to geometry domain.\n(see 'Problems' tab)");
					}
					Domain domain = new Domain(geometryClass);
					if (membraneMapping.getCalculateVoltage() && bCalculatePotential){
						if (geometryClass instanceof SurfaceClass){
							//
							// spatially resolved membrane, and must solve for potential ....
							//   make single MembraneRegionVariable for all resolved potentials
							//
							if (varHash.getVariable(Membrane.MEMBRANE_VOLTAGE_REGION_NAME)==null){
								//varHash.addVariable(new MembraneRegionVariable(MembraneVoltage.MEMBRANE_VOLTAGE_REGION_NAME));
								varHash.addVariable(new MembraneRegionVariable(getMathSymbol(membraneVoltage,geometryClass),domain));
							}
						}else{
							//
							// spatially unresolved membrane, and must solve for potential ... make VolVariable for this compartment
							//
							varHash.addVariable(new VolVariable(getMathSymbol(membraneVoltage,geometryClass),domain));
						}
						Parameter initialVoltageParm = membraneMapping.getInitialVoltageParameter();
						Variable initVoltageFunction = newFunctionOrConstant(getMathSymbol(initialVoltageParm,geometryClass),getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),geometryClass),geometryClass);
						varHash.addVariable(initVoltageFunction);
					}else{
						//
						// don't calculate voltage, still may need it though
						//
						Parameter initialVoltageParm = membraneMapping.getInitialVoltageParameter();
						Variable voltageFunction = newFunctionOrConstant(getMathSymbol(membraneMapping.getMembrane().getMembraneVoltage(),geometryClass),getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),geometryClass),geometryClass);
						varHash.addVariable(voltageFunction);
					}
				}
			}
		}
	}

	//
	// kinetic parameters (functions or constants)
	//
	for (int j=0;j<reactionSteps.length;j++){
		ReactionStep rs = reactionSteps[j];
		if (simContext.getReactionContext().getReactionSpec(rs).isExcluded()){
			continue;
		}
		Kinetics.KineticsParameter parameters[] = rs.getKinetics().getKineticsParameters();
		GeometryClass geometryClass = null;
		if (rs.getStructure()!=null){
			geometryClass = simContext.getGeometryContext().getStructureMapping(rs.getStructure()).getGeometryClass();
		}
		if (parameters != null){
			for (int i=0;i<parameters.length;i++){
				if (((parameters[i].getRole() == Kinetics.ROLE_CurrentDensity)||(parameters[i].getRole() == Kinetics.ROLE_LumpedCurrent)) && (parameters[i].getExpression()==null || parameters[i].getExpression().isZero())){
					continue;
				}
				String mathSymbol = getMathSymbol(parameters[i],geometryClass);
				Expression expr = getIdentifierSubstitutions(parameters[i].getExpression(),parameters[i].getUnitDefinition(),geometryClass);
				varHash.addVariable(newFunctionOrConstant(mathSymbol, expr,geometryClass));
			}
		}
	}

	//
	// initial conditions (either function or constant) for rate rule variables that are model parameters
	//
	Map<ModelParameter, Variable> initModelParameterHashTmp = new HashMap<> ();        // the init variables with expressions still containing variables
	Map<EventAssignmentOrRateRuleInitParameter, ModelParameter> rateRuleInitToModelParamMapping = new HashMap<> ();        // here we store the init parameter of the model parameter
	Map<ModelParameter, EventAssignmentOrRateRuleInitParameter> modelParamTorateRuleInitMapping = new HashMap<> ();        // here we store the init parameter of the model parameter
	for(ModelParameter mp : modelParameters) {        // initial assignment for global parameter used as rate rule variable
		RateRule rr = simContext.getRateRule(mp);
		if(rr == null) {
			continue;        // we only care about global parameters that are rate rule variables
		}
		Variable var = varHash.getVariable(mp.getName());
		if(var != null) {
			if(eventVolVarHash.containsKey(var)) {
				System.out.println("Global Parameters that are rate rule Variables should be unmapped at this point, unless they are EventAssignments too.");
			} else {
				throw new MappingException("Global Parameters that are rate rule Variables should be unmapped at this point.");
			}
		}
		Expression modelParamExpr = mp.getExpression();
		if(modelParamExpr == null) {
			continue;
		}
		GeometryClass gc = getDefaultGeometryClass(modelParamExpr);
		VCUnitDefinition paramUnit = modelUnitSystem.getInstance_TBD();
		if (mp.getUnitDefinition() != null && !mp.getUnitDefinition().equals(modelUnitSystem.getInstance_TBD())) {
			paramUnit = mp.getUnitDefinition();
		}

		// TODO: is this really needed? or could I directly use modelParamExpr in addEventAssignmentOrRateRuleInitParameter()
		Expression mpInitExpr = getIdentifierSubstitutions(modelParamExpr, paramUnit, gc);
		EventAssignmentOrRateRuleInitParameter mpInitParam;
		try {
			mpInitParam = addEventAssignmentOrRateRuleInitParameter(mp, mpInitExpr, PARAMETER_ROLE_EVENTASSIGN_OR_RATERULE_INITCONDN, paramUnit);
		} catch (PropertyVetoException e) {
			throw new MappingException(e.getMessage(), e);
		}
		rateRuleInitToModelParamMapping.put(mpInitParam, mp);
		modelParamTorateRuleInitMapping.put(mp, mpInitParam);
	}

	//
	// conserved constants  (e.g. K = A + B + C) and init functions for transformed global parameters (event of rate rule targets)
	//
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		GeometryClass geometryClass = fieldMathMappingParameters[i].getGeometryClass();
		fieldMathMappingParameters[i].getExpression().bindExpression(this);
		Expression exp = getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(), fieldMathMappingParameters[i].getUnitDefinition(), geometryClass);
		Variable var = newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),exp, geometryClass);
		varHash.addVariable(var);
		ModelParameter mp = rateRuleInitToModelParamMapping.get(fieldMathMappingParameters[i]);
		if (mp != null) {
			initModelParameterHashTmp.put(mp, var);
		}
	}

	//
	// initial conditions (either function or constant) for species variables
	//
	SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
	for (int i = 0; i < speciesContextSpecs.length; i++){
		// add initial count if present (!= null)
		SpeciesContextSpecParameter initCountParm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_InitialCount);
		SpeciesContext speciesContext = speciesContextSpecs[i].getSpeciesContext();
		if (initCountParm!=null && initCountParm.getExpression()!=null){
			Expression initCountExpr = new Expression(initCountParm.getExpression());
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContext.getStructure());
			String[] symbols = initCountExpr.getSymbols();
			// Check if 'initExpr' has other speciesContexts in its expression, need to replace it with 'spContext_init'
			for (int j = 0; symbols != null && j < symbols.length; j++) {
				// if symbol is a speciesContext, replacing it with a reference to initial condition for that speciesContext.
				SpeciesContext spC = null;
				SymbolTableEntry ste = initCountExpr.getSymbolBinding(symbols[j]);
				if (ste instanceof SpeciesContextSpecProxyParameter) {
					SpeciesContextSpecProxyParameter spspp = (SpeciesContextSpecProxyParameter)ste;
					if (spspp.getTarget() instanceof SpeciesContext) {
						spC = (SpeciesContext)spspp.getTarget();
						SpeciesContextSpec spcspec = simContext.getReactionContext().getSpeciesContextSpec(spC);
						SpeciesContextSpecParameter spCInitParm = spcspec.getParameterFromRole(SpeciesContextSpec.ROLE_InitialCount);
						// need to get init condn expression, but can't get it from getMathSymbol() (mapping between bio and math), hence get it as below.
						Expression scsInitExpr = new Expression(spCInitParm, getNameScope());
						initCountExpr.substituteInPlace(new Expression(spC.getName()), scsInitExpr);
					}
				}
			}
			// now create the appropriate function for the current speciesContextSpec.
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(initCountParm,sm.getGeometryClass()),getIdentifierSubstitutions(initCountExpr,initCountParm.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
		// add initial concentration (may be derived from initial count if necessary)
		SpeciesContextSpecParameter initConcParm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
		if (initConcParm!=null){
			Expression initConcExpr = null;
			if (initConcParm.getExpression()!=null){
				initConcExpr = new Expression(initConcParm.getExpression());
			}else if (initCountParm!=null && initCountParm.getExpression()!=null){
				Expression structureSizeExpr = new Expression(speciesContext.getStructure().getStructureSize(),getNameScope());
				VCUnitDefinition concUnit = initConcParm.getUnitDefinition();
				VCUnitDefinition countDensityUnit = initCountParm.getUnitDefinition().divideBy(speciesContext.getStructure().getStructureSize().getUnitDefinition());
				Expression unitFactor = getUnitFactor(concUnit.divideBy(countDensityUnit));
				initConcExpr = Expression.mult(unitFactor, Expression.div(new Expression(initCountParm,getNameScope()),structureSizeExpr)).simplifyJSCL();
			}
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContext.getStructure());
			String[] symbols = initConcExpr.getSymbols();
			// Check if 'initExpr' has other speciesContexts in its expression, need to replace it with 'spContext_init'
			for (int j = 0; symbols != null && j < symbols.length; j++) {
				// if symbol is a speciesContext, replacing it with a reference to initial condition for that speciesContext.
				SpeciesContext spC = null;
				SymbolTableEntry ste = initConcExpr.getSymbolBinding(symbols[j]);
				if (ste == null) {
					String msg = initConcParm.getName() == null ? "??" : initConcParm.getName();
					System.out.println("Unexpected NULL symbol in the initial expression of " + msg);
				} else if(ste instanceof SpeciesContextSpecProxyParameter) {
					SpeciesContextSpecProxyParameter spspp = (SpeciesContextSpecProxyParameter)ste;
					if (spspp.getTarget() instanceof SpeciesContext) {
						spC = (SpeciesContext)spspp.getTarget();
						SpeciesContextSpec spcspec = simContext.getReactionContext().getSpeciesContextSpec(spC);
						SpeciesContextSpecParameter spCInitParm = spcspec.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
						// if initConc param expression is null, try initCount
						if (spCInitParm.getExpression() == null) {
							spCInitParm = spcspec.getParameterFromRole(SpeciesContextSpec.ROLE_InitialCount);
						}
						// need to get init condn expression, but can't get it from getMathSymbol() (mapping between bio and math), hence get it as below.
						Expression scsInitExpr = new Expression(spCInitParm, getNameScope());
						initConcExpr.substituteInPlace(new Expression(spC.getName()), scsInitExpr);
					}
				} else if (ste instanceof ModelParameter) {
					ModelParameter mpArg = (ModelParameter)ste;
					System.out.println(mpArg.getName());
					if(simContext.getRateRule(mpArg) == null) {
						continue;        // only globals that are RateRule variables need to be replaced with their _init variable
					}
					EventAssignmentOrRateRuleInitParameter mpInitParam = modelParamTorateRuleInitMapping.get(mpArg);
					if(mpInitParam != null) {        // we already made it, we only need to use it
						Expression mpArgInitExpr = new Expression(mpInitParam, getNameScope());
						initConcExpr.substituteInPlace(new Expression(ste.getName()), mpArgInitExpr);
					}
				} else {
					String msg = ste.getName() == null ? "??" : ste.getName();
					String msg2 = initConcParm.getName() == null ? "??" : initConcParm.getName();
					System.out.println("Unexpected symbol type for " + msg + " in the initial expression of " + msg2);
				}
			}
			// now create the appropriate function for the current speciesContextSpec.
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(initConcParm,sm.getGeometryClass()),getIdentifierSubstitutions(initConcExpr,initConcParm.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
	}

	//
	// diffusion constants (either function or constant)
	//
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextMapping scm = getSpeciesContextMapping(speciesContextSpecs[i].getSpeciesContext());
		SpeciesContextSpec.SpeciesContextSpecParameter diffParm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_DiffusionRate);
		if (diffParm!=null && (scm.isPDERequired())){
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(diffParm,sm.getGeometryClass()),getIdentifierSubstitutions(diffParm.getExpression(),diffParm.getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass()));
		}
	}

	//
	// Boundary conditions (either function or constant)
	//
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextSpec.SpeciesContextSpecParameter bc_xm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueXm);
		StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
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
		StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
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
	varHash.addVariable(new Constant(getMathSymbol(model.getKMOLE(),null),getIdentifierSubstitutions(model.getKMOLE().getExpression(),model.getKMOLE().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getN_PMOLE(),null),getIdentifierSubstitutions(model.getN_PMOLE().getExpression(),model.getN_PMOLE().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getKMILLIVOLTS(),null),getIdentifierSubstitutions(model.getKMILLIVOLTS().getExpression(),model.getKMILLIVOLTS().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(getMathSymbol(model.getK_GHK(),null),getIdentifierSubstitutions(model.getK_GHK().getExpression(),model.getK_GHK().getUnitDefinition(),null)));
	//
	// geometric functions
	//
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (simContext.getGeometry().getDimension()==0){
			StructureMappingParameter sizeParm = sm.getSizeParameter();
			if (sizeParm!=null && sizeParm.getExpression()!=null){
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(sizeParm,sm.getGeometryClass()),getIdentifierSubstitutions(sizeParm.getExpression(), sizeParm.getUnitDefinition(), sm.getGeometryClass()), sm.getGeometryClass()));
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
		StructureMappingParameter sizeParm = sm.getSizeParameter();
		if (sm.getGeometryClass() != null && sizeParm!=null){
			if (simContext.getGeometry().getDimension()==0){
				if (sizeParm.getExpression()!=null){
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(sizeParm,sm.getGeometryClass()),getIdentifierSubstitutions(sizeParm.getExpression(), sizeParm.getUnitDefinition(), sm.getGeometryClass()),sm.getGeometryClass()));
				}
			}else{
				String compartmentName = sm.getGeometryClass().getName();
				VCUnitDefinition sizeUnit = sm.getSizeParameter().getUnitDefinition();
				String sizeFunctionName = null;
				if (sm instanceof MembraneMapping){
					MembraneMapping mm = (MembraneMapping)sm;
					if (mm.getGeometryClass() instanceof SurfaceClass){
						sizeFunctionName = MathFunctionDefinitions.Function_regionArea_current.getFunctionName();
					}else if (mm.getGeometryClass() instanceof SubVolume){
						sizeFunctionName = MathFunctionDefinitions.Function_regionVolume_current.getFunctionName();
					}
				}else if (sm instanceof FeatureMapping){
					sizeFunctionName = MathFunctionDefinitions.Function_regionVolume_current.getFunctionName();
				}else{
					throw new RuntimeException("structure mapping "+sm.getClass().getName()+" not yet supported");
				}
				Expression totalVolumeCorrection = sm.getStructureSizeCorrection(simContext,this);
				Expression sizeFunctionExpression = Expression.function(sizeFunctionName, new Expression[] {new Expression("'"+compartmentName+"'")} );
//				sizeFunctionExpression.bindExpression(mathDesc);
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(sizeParm,sm.getGeometryClass()),getIdentifierSubstitutions(Expression.mult(totalVolumeCorrection,sizeFunctionExpression),sizeUnit,sm.getGeometryClass()),sm.getGeometryClass()));
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
			// check if speciesContext has a rateRule; then the speciesContext should not be added as a constant
			if (simContext.getRateRule(scm.getSpeciesContext()) == null) {
				StructureMapping sm = simContext.getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
				if (sm.getGeometryClass() == null) {
					Structure s = sm.getStructure();
					if (s != null) {
						throw new RuntimeException("unmapped structure " + s.getName());
					}
					throw new RuntimeException("structure mapping with no structure or mapping");
				}
				Variable dependentVariable = newFunctionOrConstant(getMathSymbol(scm.getSpeciesContext(),sm.getGeometryClass()),getIdentifierSubstitutions(scm.getDependencyExpression(),scm.getSpeciesContext().getUnitDefinition(),sm.getGeometryClass()),sm.getGeometryClass());
				dependentVariable.setDomain(new Domain(sm.getGeometryClass()));
				varHash.addVariable(dependentVariable);
			}
		}
	}
	BioEvent[] bioevents = simContext.getBioEvents();
	if(bioevents != null && bioevents.length > 0) {
		for (BioEvent be : bioevents) {
			// transform the bioEvent trigger/delay to math Event
			for (LocalParameter p : be.getEventParameters()) {
				if (p.getExpression() != null) {
					String name = getMathSymbol(p,null);    // ex: eventName.delay and eventName.triggerFunction
					Expression exp = getIdentifierSubstitutions(p.getExpression(), p.getUnitDefinition(), null);
					Variable var = newFunctionOrConstant(name, exp, null);
					varHash.addVariable(var);
				}else if (be.getParameter(BioEventParameterType.GeneralTriggerFunction) == p){
					//
					// use generated function here.
					//
					String name = getMathSymbol(p, null);
					Expression exp = getIdentifierSubstitutions(be.generateTriggerExpression(), p.getUnitDefinition(), null);
					Variable var = newFunctionOrConstant(name, exp, null);
					varHash.addVariable(var);
				}
			}
		}
	}

	//
	// substitute init functions for event assignment variables
	//
//	for (Map.Entry<VolVariable,EventAssignmentOrRateRuleInitParameter> entry : eventVolVarHash.entrySet()) {
//		EventAssignmentOrRateRuleInitParameter eap = entry.getValue();
//
//		String argName = eap.getName();
//		Expression modelParamExpr = eap.getExpression();
//		GeometryClass gc = getDefaultGeometryClass(modelParamExpr);
//		VCUnitDefinition paramUnit = eap.getUnitDefinition();
//		Expression mpInitExpr = new Expression(modelParamExpr);
//		String[] symbols = mpInitExpr.getSymbols();
//		if(symbols == null || symbols.length == 0) {
//			continue;
//		}
//		// TODO: this is still not working well
//		// check if 'initExpr' has other speciesContexts or rate rule global parameter variables in its expression
//		// need to replace it with 'spContext_init', modelParameter_init
//		for (String symbol : symbols) {
//			// if symbol is a speciesContext, replacing it with a reference to initial condition for that speciesContext.
//			SymbolTableEntry ste = mpInitExpr.getSymbolBinding(symbol);
//			if (ste == null) {
//				System.out.println("Unexpected NULL symbol in the initial expression of " + argName);
//			} else if (ste instanceof SpeciesContext) {
//				SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec((SpeciesContext)ste);
//				// TODO: what if initial count???
//				SpeciesContextSpecParameter spCInitParm = scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
//				// need to get init condn expression, but can't get it from getMathSymbol() (mapping between bio and math), hence get it as below.
//				Expression scsInitExpr = new Expression(spCInitParm, getNameScope());
//				mpInitExpr.substituteInPlace(new Expression(ste.getName()), scsInitExpr);
//			} else if(ste instanceof ModelParameter) {
//				ModelParameter mpArg = (ModelParameter)ste;
//				System.out.println(mpArg.getName());
//				if(simContext.getRateRule(mpArg) == null) {
//					continue;		// only globals that are RateRule variables need to be replaced with their _init variable
//				}
//				Variable mpArgVar = initModelParameterHashTmp.get(mpArg);
//				if(mpArgVar != null && eventVolVarHash.get(mpArgVar) != null) {
//					EventAssignmentOrRateRuleInitParameter mpInitParam = eventVolVarHash.get(mpArgVar);
//					Expression mpArgInitExpr = new Expression(mpInitParam, getNameScope());
//					mpInitExpr.substituteInPlace(new Expression(ste.getName()), mpArgInitExpr);
//
//				}
//			} else {
//				String msg = ste.getName() == null ? "??" : ste.getName();
//				System.out.println("Unexpected symbol type for " + msg + " in the initial expression of " + argName);
//			}
//		}
//		varHash.removeVariable(argName);
//		Expression exp = getIdentifierSubstitutions(mpInitExpr, paramUnit, gc);
//		Variable varInit = newFunctionOrConstant(argName, exp, gc);
//		varHash.addVariable(varInit);
//	}

	//
	// deal with rate rules
	//
	// first, substitute the init functions for rate rule variables that are model parameters
	// we'll need this init variable (function or constant) for the ODE Equation
	//
	Map<ModelParameter, Variable> initModelParameterHash = new HashMap<> ();    // here we store the init variable with the final substitutions within their expressions
	Map<String, SymbolTableEntry> entryMap = new HashMap<String, SymbolTableEntry>();
	simContext.getEntries(entryMap);
	for (Map.Entry<ModelParameter, Variable> entry : initModelParameterHashTmp.entrySet()) {
		ModelParameter mp = entry.getKey();
		Variable mpInitVariable = entry.getValue();

		String argName = mpInitVariable.getName();
		Expression modelParamExpr = mp.getExpression();
		GeometryClass gc = getDefaultGeometryClass(modelParamExpr);
		Expression mpInitExpr = new Expression(modelParamExpr);
		String[] symbols = mpInitExpr.getSymbols();
		if(symbols == null || symbols.length == 0) {
			// stays as it is in variable hash
			initModelParameterHash.put(mp, mpInitVariable);  // we just move it into the initModelParameterHash
			continue;
		}
// TODO: work in progress
//		mpInitExpr = flattenUnbound(mpInitExpr, simContext, entryMap, modelParamTorateRuleInitMapping, 0);
//		symbols = mpInitExpr.getSymbols();
//		if(symbols == null || symbols.length == 0) {
//			// stays as it is in variable hash
//			initModelParameterHash.put(mp, mpInitVariable);  // we just move it into the initModelParameterHash
//			continue;
//		}

		// check if 'initExpr' has other speciesContexts or rate rule global parameter variables in its expression
		// need to replace it with 'spContext_init', modelParameter_init
		for (String symbol : symbols) {
			// if symbol is a speciesContext, replacing it with a reference to initial condition for that speciesContext.
			SymbolTableEntry ste = mpInitExpr.getSymbolBinding(symbol);
			if (ste == null) {
				System.out.println("Unexpected NULL symbol in the initial expression of " + argName);
			} else if (ste instanceof SpeciesContext) {
				SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec((SpeciesContext)ste);
				// TODO: what if initial count???
				SpeciesContextSpecParameter spCInitParm = scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
				// need to get init condn expression, but can't get it from getMathSymbol() (mapping between bio and math), hence get it as below.
				Expression scsInitExpr = new Expression(spCInitParm, getNameScope());
				mpInitExpr.substituteInPlace(new Expression(ste.getName()), scsInitExpr);
			} else if(ste instanceof ModelParameter) {
				ModelParameter mpArg = (ModelParameter)ste;
				System.out.println(mpArg.getName());
				if(simContext.getRateRule(mpArg) == null) {
					continue;        // only globals that are RateRule variables need to be replaced with their _init variable
				}
				EventAssignmentOrRateRuleInitParameter mpInitParam = modelParamTorateRuleInitMapping.get(mpArg);
				if(mpInitParam != null) {        // we already made it, we only need to use it
					Expression mpArgInitExpr = new Expression(mpInitParam, getNameScope());
					mpInitExpr.substituteInPlace(new Expression(ste.getName()), mpArgInitExpr);
				}
			} else {
				String msg = ste.getName() == null ? "??" : ste.getName();
				System.out.println("Unexpected symbol type for " + msg + " in the initial expression of " + argName);
			}
		}
		VCUnitDefinition paramUnit = modelUnitSystem.getInstance_TBD();
		if (mp.getUnitDefinition() != null && !mp.getUnitDefinition().equals(modelUnitSystem.getInstance_TBD())) {
			paramUnit = mp.getUnitDefinition();
		}

		varHash.removeVariable(mpInitVariable);
		Expression exp = getIdentifierSubstitutions(mpInitExpr, paramUnit, gc);
		mpInitVariable = newFunctionOrConstant(argName, exp, gc);
		varHash.addVariable(mpInitVariable);
		initModelParameterHash.put(mp, mpInitVariable);
	}
	//
	// create the VolVariable for the species context used as rate rule variable
	// create the Variable (function or constant) for its rate (need it for the ODE Equation)
	//
	enum1 = getSpeciesContextMappings();
	while(enum1.hasMoreElements()) {                // species context used as rate rule variable
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		Variable var = scm.getVariable();
		Expression exp = scm.getDependencyExpression();
		if (var == null && exp != null) {
			RateRule rr = simContext.getRateRule(scm.getSpeciesContext());
			if (rr != null && (rr.getRateRuleVar() instanceof SpeciesContext)) {
				SpeciesContext sc = scm.getSpeciesContext();
				StructureMapping sm = simContext.getGeometryContext().getStructureMapping(sc.getStructure());
				if (sm.getGeometryClass() == null) {
					Structure s = sm.getStructure();
					if (s != null) {
						throw new RuntimeException("unmapped structure " + s.getName());
					}
					throw new RuntimeException("structure mapping with no structure or mapping");
				}
				String name = getMathSymbol(scm.getSpeciesContext(),sm.getGeometryClass());
				Expression orig = rr.getRateRuleExpression();
				Expression ex = getIdentifierSubstitutions(orig, scm.getSpeciesContext().getUnitDefinition(), sm.getGeometryClass());
				GeometryClass gc = sm.getGeometryClass();
				Domain domain = null;
				if(gc != null) {
					domain = new Domain(gc);
				}
				if(gc instanceof SurfaceClass) {
					scm.setVariable(new MemVariable(scm.getSpeciesContext().getName(), domain));
				} else {
					scm.setVariable(new VolVariable(scm.getSpeciesContext().getName(), domain));
				}
				Variable oldVariablre = varHash.getVariable(name);
				if(oldVariablre != null) {            // should always be null
					varHash.removeVariable(name);
				}
				varHash.addVariable(scm.getVariable());

//				// create the rate parameter
				SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
				SpeciesContextSpecParameter scsInitParam = scs.getInitialConditionParameter();
				VCUnitDefinition scsInitParamUnit = scsInitParam.getUnitDefinition();
				RateRuleRateParameter rateParam = null;
				try {
					Expression origExp = simContext.getRateRule(sc).getRateRuleExpression();
					VCUnitDefinition rateUnit = modelUnitSystem.getInstance_TBD();
					if (scsInitParamUnit != null && !scsInitParamUnit.equals(modelUnitSystem.getInstance_TBD())) {
						rateUnit = scsInitParamUnit;
					}
					Expression rateExpr = getIdentifierSubstitutions(origExp, rateUnit, gc);
					String argName = sc.getName() + MATH_FUNC_SUFFIX_RATERULE_RATE;
					Variable param = newFunctionOrConstant(argName, rateExpr, gc);
					varHash.addVariable(param);
					rateParam = addRateRuleRateParameter(sc, rateExpr, PARAMETER_ROLE_RATERULE_RATE, rateUnit);
				} catch (PropertyVetoException e) {
					throw new MappingException(e.getMessage(), e);
				}
				rateRuleRateParamHash.put(scm.getVariable(), rateParam);    // we generate the ODE equation elsewhere (later)
			}
		} else if(var != null && exp == null) {
			// could be an event variable AND a rate rule variable - in which case we need a rate parameter for the event ODE equation
			SpeciesContext sc = scm.getSpeciesContext();
			boolean isRateRuleVar = rateRuleVarTargets.contains(sc);
			boolean isEventAssignVar = eventAssignTargets.contains(sc);
			if(isRateRuleVar && isEventAssignVar) {        // is both, so we make a rate parameter, like above
				SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
				SpeciesContextSpecParameter scsInitParam = scs.getInitialConditionParameter();
				VCUnitDefinition scsInitParamUnit = scsInitParam.getUnitDefinition();
				StructureMapping sm = simContext.getGeometryContext().getStructureMapping(sc.getStructure());
				GeometryClass gc = sm.getGeometryClass();
				RateRuleRateParameter rateParam = null;
				try {
					Expression origExp = simContext.getRateRule(sc).getRateRuleExpression();
					VCUnitDefinition rateUnit = modelUnitSystem.getInstance_TBD();
					if (scsInitParamUnit != null && !scsInitParamUnit.equals(modelUnitSystem.getInstance_TBD())) {
						rateUnit = scsInitParamUnit;
					}
					Expression rateExpr = getIdentifierSubstitutions(origExp, rateUnit, gc);
					String argName = sc.getName() + MATH_FUNC_SUFFIX_RATERULE_RATE;
					Variable param = newFunctionOrConstant(argName, rateExpr, gc);
					varHash.addVariable(param);
					rateParam = addRateRuleRateParameter(sc, rateExpr, PARAMETER_ROLE_RATERULE_RATE, rateUnit);
				} catch (PropertyVetoException e) {
					throw new MappingException(e.getMessage(), e);
				}
				rateRuleRateParamHash.put(var, rateParam);        // we generate the ODE equation elsewhere (later)
			}
		}
	}
	//
	// create the VolVariable for the model parameter used as rate rule variable
	// create the Variable (function or constant) for its rate (need it for the ODE Equation)
	// create the ODE Equation
	//
	for(ModelParameter mp : modelParameters) {        // global parameter used as rate rule variable
		Variable var = varHash.getVariable(mp.getName());
		RateRule rr = simContext.getRateRule(mp);
		Expression modelParamExpr = mp.getExpression();
		if(var == null && rr != null) {        // at this point var should be a constant
			// we're under the assumption that it's non-spatial
			GeometryClass[] geometryClasses = simContext.getGeometryContext().getGeometry().getGeometryClasses();
			GeometryClass gc = geometryClasses[0];
//			SubDomain subDomain = mathDesc.getSubDomains().nextElement();
//			GeometryClass gc = getDefaultGeometryClass(modelParamExpr);
			Domain domain = null;
			if(gc != null) {
				domain = new Domain(gc);
			}
			Variable variable;
			if(gc instanceof SurfaceClass) {
				variable = new MemVariable(mp.getName(), domain);
			} else {
				variable = new VolVariable(mp.getName(), domain);
			}
			varHash.addVariable(variable);
			RateRuleRateParameter rateParam = null;
			try {
				Expression origExp = rr.getRateRuleExpression();
				VCUnitDefinition rateUnit = modelUnitSystem.getInstance_TBD();
				if (mp.getUnitDefinition() != null && !mp.getUnitDefinition().equals(modelUnitSystem.getInstance_TBD())) {
					rateUnit = mp.getUnitDefinition().divideBy(timeUnit);
				}
				Expression rateExpr = getIdentifierSubstitutions(origExp, rateUnit, gc);
				String argName = mp.getName() + MATH_FUNC_SUFFIX_RATERULE_RATE;
				Variable param = newFunctionOrConstant(argName, rateExpr, gc);
				varHash.addVariable(param);
				rateParam = addRateRuleRateParameter(mp, rateExpr, PARAMETER_ROLE_RATERULE_RATE, rateUnit);
			} catch (PropertyVetoException e) {
				throw new MappingException(e.getMessage(), e);
			}
			// no need to put it in the hash, we make the ODE Equation right here
			//rateRuleRateParamHash.put(variable, rateParam);

			SubDomain subDomain = mathDesc.getSubDomains().nextElement();    // we know it's non-spatial
			Equation equation = null;
			// TODO: replace the expression with the variable  ex: "g0_protocol_init" computed above
			Expression initial = new Expression(mp.getExpression());
			Variable mpInitVariable = initModelParameterHash.get(mp);    // TODO: can it be null? should check and maybe try mp.getConstantValue() too ???
			if(mpInitVariable != null) {
				initial = new Expression(mpInitVariable.getName());
			}
			Expression rateExpr = new Expression(0.0);
			//RateRuleRateParameter rateParam = rateRuleRateParamHash.get(variable);
			if (rateParam != null) {        // ex: g0_rate
				rateExpr = new Expression(getMathSymbol(rateParam, gc));
			}
			equation = new OdeEquation(variable, initial, rateExpr);    // ODE Equation for rate rule variable being a global parameter
			subDomain.addEquation(equation);
		}
	}

	//
	// deal with assignment rules
	//
	enum1 = getSpeciesContextMappings();
	while(enum1.hasMoreElements()) {            // species context used as assignment rule variable
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable()==null && scm.getDependencyExpression()!=null) {
			AssignmentRule ar = simContext.getAssignmentRule(scm.getSpeciesContext());
			if (ar != null && (ar.getAssignmentRuleVar() instanceof SpeciesContext)) {        // TODO: we limit assignment rules to SpeciesContext for now
				StructureMapping sm = simContext.getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
				if (sm.getGeometryClass() == null) {
					Structure s = sm.getStructure();
					if (s != null) {
						throw new RuntimeException("unmapped structure " + s.getName());
					}
					throw new RuntimeException("structure mapping with no structure or mapping");
				}
				String name = getMathSymbol(scm.getSpeciesContext(),sm.getGeometryClass());
				Expression orig = ar.getAssignmentRuleExpression();
				Expression ex = getIdentifierSubstitutions(orig, scm.getSpeciesContext().getUnitDefinition(), sm.getGeometryClass());
				GeometryClass gc = sm.getGeometryClass();
				Variable dependentVariable = newFunctionOrConstant(name, ex, gc);
				dependentVariable.setDomain(new Domain(sm.getGeometryClass()));
				varHash.removeVariable(name);
				varHash.addVariable(dependentVariable);
			}
		}
	}
	for(ModelParameter mp : modelParameters) {        // global parameter used as assignment rule variable
		Variable var = varHash.getVariable(mp.getName());
		AssignmentRule ar = simContext.getAssignmentRule(mp);
		Expression modelParamExpr = mp.getExpression();
		if(var == null && ar != null) {        // at this point var (global parameter used as assignment rule variable) should be null
			// we're under the assumption that it's non-spatial
			GeometryClass[] geometryClasses = simContext.getGeometryContext().getGeometry().getGeometryClasses();
			GeometryClass gc = geometryClasses[0];
			SubDomain subDomain = mathDesc.getSubDomains().nextElement();

			Expression origExp = ar.getAssignmentRuleExpression();
			VCUnitDefinition rateUnit = modelUnitSystem.getInstance_TBD();
			if (mp.getUnitDefinition() != null && !mp.getUnitDefinition().equals(modelUnitSystem.getInstance_TBD())) {
				rateUnit = mp.getUnitDefinition();
			}
			Expression rateExpr = getIdentifierSubstitutions(origExp, rateUnit, gc);
			String argName = mp.getName();
			Variable param = newFunctionOrConstant(argName, rateExpr, gc);
			varHash.addVariable(param);
		}
	}

	//
	// geometry
	//
	if (simContext.getGeometryContext().getGeometry() != null){
		try {
			mathDesc.setGeometry(simContext.getGeometryContext().getGeometry());
		}catch (java.beans.PropertyVetoException e){
			throw new MappingException("failure setting geometry "+e.getMessage(), e);
		}
	}else{
		throw new MappingException("geometry must be defined");
	}

	//
	// volume subdomains
	//
	for (CompartmentSubdomainContext compartmentSubDomainContext : compartmentSubdomainContexts){
		SubVolume subVolume = compartmentSubDomainContext.subvolume;
		CompartmentSubDomain subDomain = mathDesc.getCompartmentSubDomain(subVolume.getName());
		//
		// assign boundary condition types
		//
		StructureMapping[] mappedSMs = simContext.getGeometryContext().getStructureMappings(subVolume);
		FeatureMapping mappedFM = null;
		for (int i = 0; i < mappedSMs.length; i++) {
			if (mappedSMs[i] instanceof FeatureMapping){
				if (mappedFM!=null){
					lg.info("WARNING:::: MathMapping.refreshMathDescription() ... assigning boundary condition types not unique");
				}
				mappedFM = (FeatureMapping)mappedSMs[i];
			}
		}
		if (mappedFM != null){
			if (simContext.getGeometry().getDimension()>0){
				subDomain.setBoundaryConditionXm(mappedFM.getBoundaryConditionTypeXm());
				subDomain.setBoundaryConditionXp(mappedFM.getBoundaryConditionTypeXp());
			}
			if (simContext.getGeometry().getDimension()>1){
				subDomain.setBoundaryConditionYm(mappedFM.getBoundaryConditionTypeYm());
				subDomain.setBoundaryConditionYp(mappedFM.getBoundaryConditionTypeYp());
			}
			if (simContext.getGeometry().getDimension()>2){
				subDomain.setBoundaryConditionZm(mappedFM.getBoundaryConditionTypeZm());
				subDomain.setBoundaryConditionZp(mappedFM.getBoundaryConditionTypeZp());
			}
		}

		//
		// create equations
		//
		VolumeStructureAnalyzer structureAnalyzer = getVolumeStructureAnalyzer(subVolume);
		Enumeration<SpeciesContextMapping> enumSCM = getSpeciesContextMappings();
		while (enumSCM.hasMoreElements()){
			SpeciesContextMapping scm = enumSCM.nextElement();
			SpeciesContext        sc  = scm.getSpeciesContext();
			StructureMapping      sm  = simContext.getGeometryContext().getStructureMapping(sc.getStructure());
			SpeciesContextSpec    scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
			//
			// if an independent volume variable, then create equation for it (if mapped to this subDomain)
			//
			final GeometryClass gc = sm.getGeometryClass();
			if (gc == null || !gc.getName().equals(subDomain.getName())){
				continue;
			}
			SpeciesContextSpecParameter initConcParameter = scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
			if ((scm.getVariable() instanceof VolumeRegionVariable) && scm.getDependencyExpression()==null){
				VolumeRegionVariable volumeRegionVariable = (VolumeRegionVariable)scm.getVariable();
				Expression initial = getIdentifierSubstitutions(new Expression(initConcParameter, getNameScope()), initConcParameter.getUnitDefinition(), sm.getGeometryClass());
				Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass());
				VolumeRegionEquation volumeRegionEquation = new VolumeRegionEquation(volumeRegionVariable,initial);
				volumeRegionEquation.setVolumeRateExpression(rate);
				subDomain.addEquation(volumeRegionEquation);
			}else if (scm.getVariable() instanceof VolVariable && scm.getDependencyExpression()==null){
				VolVariable variable = (VolVariable)scm.getVariable();
				Equation equation = null;
				if (sm.getGeometryClass() == subVolume){
					if (scm.isPDERequired()){
						//
						// species context belongs to this subDomain
						//
						Expression initial = getIdentifierSubstitutions(new Expression(initConcParameter, getNameScope()), initConcParameter.getUnitDefinition(), sm.getGeometryClass());
						Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass());
						SpeciesContextSpecParameter diffusionParameter = scs.getDiffusionParameter();
						Expression diffusion = getIdentifierSubstitutions(new Expression(diffusionParameter, getNameScope()), diffusionParameter.getUnitDefinition(), sm.getGeometryClass());
						equation = new PdeEquation(variable,initial,rate,diffusion);
						((PdeEquation)equation).setBoundaryXm((scs.getBoundaryXmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryXmParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setBoundaryXp((scs.getBoundaryXpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryXpParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setBoundaryYm((scs.getBoundaryYmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryYmParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setBoundaryYp((scs.getBoundaryYpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryYpParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setBoundaryZm((scs.getBoundaryZmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryZmParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setBoundaryZp((scs.getBoundaryZpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryZpParameter(),sm.getGeometryClass())));

						if (simContext.getGeometry().getDimension()>=1){
							Expression velXExp = null;
							if (scs.getVelocityXParameter().getExpression()!=null){
								velXExp = new Expression(getMathSymbol(scs.getVelocityXParameter(),sm.getGeometryClass()));
							}else{
								SpatialQuantity[] velX_quantities = scs.getVelocityQuantities(QuantityComponent.X);
								if (velX_quantities.length>0){
									int numRegions = simContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(subVolume).length;
									if (velX_quantities.length==1 && numRegions==1){
										velXExp = new Expression(getMathSymbol(velX_quantities[0],sm.getGeometryClass()));
									}else{
										throw new MappingException("multiple advection velocities enabled set for multiple volume domains ");
									}
								}
							}
							((PdeEquation)equation).setVelocityX(velXExp);
						}

						if (simContext.getGeometry().getDimension()>=2){
							Expression velYExp = null;
							if (scs.getVelocityYParameter().getExpression()!=null){
								velYExp = new Expression(getMathSymbol(scs.getVelocityYParameter(),sm.getGeometryClass()));
							}else{
								SpatialQuantity[] velY_quantities = scs.getVelocityQuantities(QuantityComponent.Y);
								if (velY_quantities.length>0){
									int numRegions = simContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(subVolume).length;
									if (velY_quantities.length==1 && numRegions==1){
										velYExp = new Expression(getMathSymbol(velY_quantities[0],sm.getGeometryClass()));
									}else{
										throw new MappingException("multiple advection velocities enabled set for multiple volume domains ");
									}
								}
							}
							((PdeEquation)equation).setVelocityY(velYExp);
						}

						if (simContext.getGeometry().getDimension()==3){
							Expression velZExp = null;
							if (scs.getVelocityZParameter().getExpression()!=null){
								velZExp = new Expression(getMathSymbol(scs.getVelocityZParameter(),sm.getGeometryClass()));
							}else{
								SpatialQuantity[] velZ_quantities = scs.getVelocityQuantities(QuantityComponent.Z);
								if (velZ_quantities.length>0){
									int numRegions = simContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(subVolume).length;
									if (velZ_quantities.length==1 && numRegions==1){
										velZExp = new Expression(getMathSymbol(velZ_quantities[0],sm.getGeometryClass()));
									}else{
										throw new MappingException("multiple advection velocities enabled set for multiple volume domains ");
									}
								}
							}
							((PdeEquation)equation).setVelocityZ(velZExp);
						}

						subDomain.replaceEquation(equation);
					} else {
						//
						// ODE - species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(initConcParameter,null));
						GeometryClass geometryClass = simContext.getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass();
						if (simContext.getAssignmentRule(sc) != null){
							initial = getIdentifierSubstitutions(
									simContext.getAssignmentRule(sc).getAssignmentRuleExpression(),
									initConcParameter.getUnitDefinition(),
									geometryClass);
						}
						Expression rate = new Expression(0);
						if (scm.getRate() != null){
							rate = getIdentifierSubstitutions(
									scm.getRate(),
									scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),
									geometryClass);
						}
						//
						// if it's an event assignment variable AND a rate rule variable
						// we replace the event rate computed above (which should be zero) with the RateRuleParameter expression
						//
						RateRuleRateParameter rateParam = rateRuleRateParamHash.get(variable);
						if (rateParam != null) {
							rate = new Expression(getMathSymbol(rateParam, null));
						}
						equation = new OdeEquation(variable, initial, rate);
						subDomain.replaceEquation(equation);
					}
				}
			} else if(scm.getVariable() instanceof VolVariable && scm.getDependencyExpression() != null) {    // rate rule variables are like this
				RateRule rr = simContext.getRateRule(scm.getSpeciesContext());
				if (rr != null && (rr.getRateRuleVar() instanceof SpeciesContext)) {
					//
					// we generate rate rule ODE equation only for species variable that are NOT event assignment variable (see right above)
					// for global parameters variable we do it elsewhere
					//
					VolVariable variable = (VolVariable)scm.getVariable();
					Equation equation = null;
					if (sm.getGeometryClass() == subVolume) {
						Expression initial = new Expression(getMathSymbol(initConcParameter, null));
						Expression rateExpr = new Expression(0.0);
						RateRuleRateParameter rateParam = rateRuleRateParamHash.get(variable);
						if (rateParam != null) {
							rateExpr = new Expression(getMathSymbol(rateParam, null));
						}
						equation = new OdeEquation(variable, initial, rateExpr);
						subDomain.addEquation(equation);
					}
				}
			}
		}

		//
		// create fast system (if neccessary)
		//
		SpeciesContextMapping fastSpeciesContextMappings[] = structureAnalyzer.getFastSpeciesContextMappings();
		if (fastSpeciesContextMappings!=null){
			FastSystem fastSystem = new FastSystem(mathDesc);
			for (int i=0;i<fastSpeciesContextMappings.length;i++){
				SpeciesContextMapping scm = fastSpeciesContextMappings[i];
				if (scm.getFastInvariant()==null){
					//
					// independant-fast variable, create a fastRate object
					//
					Expression rate = getIdentifierSubstitutions(scm.getFastRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),subVolume);
					FastRate fastRate = new FastRate(rate);
					fastSystem.addFastRate(fastRate);
				}else{
					//
					// dependant-fast variable, create a fastInvariant object
					//
					Expression rate = getIdentifierSubstitutions(scm.getFastInvariant(), modelUnitSystem.getVolumeConcentrationUnit(),subVolume);
					FastInvariant fastInvariant = new FastInvariant(rate);
					fastSystem.addFastInvariant(fastInvariant);
				}
			}
			subDomain.setFastSystem(fastSystem);
		}
		//
		// create ode's for voltages to be calculated on unresolved membranes mapped to this subVolume
		//
		Structure localStructures[] = simContext.getGeometryContext().getStructuresFromGeometryClass(subVolume);
		for (int sIndex = 0; sIndex < localStructures.length; sIndex++){
			if (localStructures[sIndex] instanceof Membrane){
				Membrane membrane = (Membrane)localStructures[sIndex];
				MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
				if ((membraneMapping.getGeometryClass() instanceof SubVolume) && membraneMapping.getCalculateVoltage()){
					MembraneElectricalDevice capacitiveDevice = potentialMapping.getCapacitiveDevice(membrane);
					if (capacitiveDevice.getDependentVoltageExpression()==null){
						VolVariable vVar = (VolVariable)varHash.getVariable(getMathSymbol(capacitiveDevice.getVoltageSymbol(),membraneMapping.getGeometryClass()));
						Expression initExp = new Expression(getMathSymbol(capacitiveDevice.getMembraneMapping().getInitialVoltageParameter(),membraneMapping.getGeometryClass()));
						subDomain.addEquation(new OdeEquation(vVar,initExp,getIdentifierSubstitutions(potentialMapping.getOdeRHS(capacitiveDevice,this), membrane.getMembraneVoltage().getUnitDefinition().divideBy(timeUnit),membraneMapping.getGeometryClass())));
					}else{
						//
						//
						//
					}
				}
			}
		}
	}

	//
	// membrane subdomains
	//
	for (MembraneSubdomainContext memSubdomainContext : membraneSubdomainContexts){
		MembraneSubDomain memSubDomain = memSubdomainContext.membraneSubdomain;
		SurfaceClass surfaceClass = memSubdomainContext.surfaceClass;

		for (SurfaceRegionObject surfaceRegionObject : memSubdomainContext.surfaceRegionObjects){
			if (surfaceRegionObject.isQuantityCategoryEnabled(QuantityCategory.SurfaceVelocity)){
				int dim = simContext.getGeometry().getDimension();
				if (dim!=2){
					throw new MappingException("Membrane Velocity only supported for 2D geometries");
				}
				if (simContext.getGeometry().getDimension()>=1){
					SpatialQuantity velXQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.SurfaceVelocity, QuantityComponent.X);
					Expression velXExp = new Expression(velXQuantity,simContext.getNameScope());
					memSubDomain.setVelocityX(getIdentifierSubstitutions(velXExp, velXQuantity.getUnitDefinition(), surfaceClass));
				}
				if (simContext.getGeometry().getDimension()>=2){
					SpatialQuantity velYQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.SurfaceVelocity, QuantityComponent.Y);
					Expression velYExp = new Expression(velYQuantity,simContext.getNameScope());
					memSubDomain.setVelocityY(getIdentifierSubstitutions(velYExp, velYQuantity.getUnitDefinition(), surfaceClass));
				}
				if (simContext.getGeometry().getDimension()==3){
					SpatialQuantity velZQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.SurfaceVelocity, QuantityComponent.Z);
					Expression velZExp = new Expression(velZQuantity,simContext.getNameScope());
					//memSubDomain.setVelocityZ(getIdentifierSubstitutions(velZExp, velZQuantity.getUnitDefinition(), surfaceClass));
					throw new MappingException("Membrane Velocity not supported for 2D problems");
				}
			}
		}

		//
		// create equations for membrane-bound molecular species
		//
		MembraneStructureAnalyzer membraneStructureAnalyzer = getMembraneStructureAnalyzer(surfaceClass);
		Enumeration<SpeciesContextMapping> enumSCM = getSpeciesContextMappings();
		while (enumSCM.hasMoreElements()){
			SpeciesContextMapping scm = enumSCM.nextElement();
			SpeciesContext        sc  = scm.getSpeciesContext();
			SpeciesContextSpec    scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
			StructureMapping      sm  = simContext.getGeometryContext().getStructureMapping(sc.getStructure());
			//
			// if an independent membrane variable, then create equation for it
			// ...even if the speciesContext is for another membraneSubDomain (e.g. BradykininReceptor in NuclearMembrane)
			//
			if ((scm.getVariable() instanceof MembraneRegionVariable) && scm.getDependencyExpression()==null){
				MembraneRegionEquation equation = null;
				MembraneRegionVariable memRegionVar = (MembraneRegionVariable)scm.getVariable();
				if (sm.getGeometryClass() == surfaceClass){
					//
					// species context belongs to this subDomain
					//
					Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),sm.getGeometryClass()));
					Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass());
					equation = new MembraneRegionEquation(memRegionVar,initial);
					equation.setMembraneRateExpression(rate);
					//equation.setUniformRateExpression(newUniformRateExpression);
					memSubDomain.replaceEquation(equation);
				}
			}else if ((scm.getVariable() instanceof MemVariable) && scm.getDependencyExpression()==null){
				//
				// independant variable, create an equation object
				//
				if (sm.getGeometryClass() == surfaceClass){
					Equation equation = null;
					MemVariable variable = (MemVariable)scm.getVariable();
					if (scm.isPDERequired()){
						//
						// PDE
						//

						//
						// species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),sm.getGeometryClass()));
						Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass());
						Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),sm.getGeometryClass()));
						equation = new PdeEquation(variable,initial,rate,diffusion);
						((PdeEquation)equation).setBoundaryXm((scs.getBoundaryXmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryXmParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setBoundaryXp((scs.getBoundaryXpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryXpParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setBoundaryYm((scs.getBoundaryYmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryYmParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setBoundaryYp((scs.getBoundaryYpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryYpParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setBoundaryZm((scs.getBoundaryZmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryZmParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setBoundaryZp((scs.getBoundaryZpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryZpParameter(),sm.getGeometryClass())));
						memSubDomain.replaceEquation(equation);
					} else {
						//
						// ODE
						//
						//
						// species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),null));
						Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass());
						equation = new OdeEquation(variable,initial,rate);
						memSubDomain.replaceEquation(equation);
					}
				}
			}
		}
		Enumeration<SpeciesContextMapping> enum_scm = getSpeciesContextMappings();
		while (enum_scm.hasMoreElements()){
			SpeciesContextMapping scm = enum_scm.nextElement();
			if (scm.isPDERequired() || scm.getVariable() instanceof VolumeRegionVariable){
				//Species species = scm.getSpeciesContext().getSpecies();
				Variable var = scm.getVariable();
				final Domain dm = var.getDomain( );
				if (dm != null) {
					final String domainName = dm.getName();
					if (sameName(domainName, memSubDomain.getInsideCompartment()) ||sameName(domainName, memSubDomain.getOutsideCompartment())) {
						JumpCondition jc = memSubDomain.getJumpCondition(var);
						if (jc==null){
							//System.out.println("MathMapping.refreshMathDescription(), adding jump condition for diffusing variable "+var.getName()+" on membrane "+membraneStructureAnalyzer.getMembrane().getName());
							if (var instanceof VolVariable){
								jc = new JumpCondition((VolVariable)var);
							}else if (var instanceof VolumeRegionVariable){
								jc = new JumpCondition((VolumeRegionVariable)var);
							} else {
								throw new RuntimeException("unexpected Variable type " + var.getClass().getName());
							}

							memSubDomain.addJumpCondition(jc);
						}
					}
				}
			}
		}
		//
		// set jump conditions for any volume variables or volume region variables that have explicitly defined fluxes
		//
		ResolvedFlux resolvedFluxes[] = membraneStructureAnalyzer.getResolvedFluxes();
		if (resolvedFluxes!=null){
			for (int i=0;i<resolvedFluxes.length;i++){
				SpeciesContext sc = resolvedFluxes[i].getSpeciesContext();
				SpeciesContextMapping scm = getSpeciesContextMapping(sc);
				StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(sc.getStructure());
				if (scm.getVariable() instanceof VolVariable && scm.isPDERequired()){
					VolVariable volVar = (VolVariable)scm.getVariable();
					JumpCondition jc = memSubDomain.getJumpCondition(volVar);
					if (jc==null){
						jc = new JumpCondition(volVar);
						memSubDomain.addJumpCondition(jc);
					}
					Expression flux = getIdentifierSubstitutions(resolvedFluxes[i].getFluxExpression(), resolvedFluxes[i].getUnitDefinition(),membraneStructureAnalyzer.getSurfaceClass());
					if (memSubDomain.getInsideCompartment().getName().equals(sm.getGeometryClass().getName())) {
						jc.setInFlux(flux);
					}else if (memSubDomain.getOutsideCompartment().getName().equals(sm.getGeometryClass().getName())){
						jc.setOutFlux(flux);
					}else{
						throw new RuntimeException("Application  " + simContext.getName() + " : " + scm.getSpeciesContext().getName()+" has spatially resolved flux at membrane "+scm.getSpeciesContext().getStructure().getName()+" with a non-local flux species "+scm.getSpeciesContext().getName());
					}
				}else if (scm.getVariable() instanceof VolumeRegionVariable){
					VolumeRegionVariable volRegionVar = (VolumeRegionVariable)scm.getVariable();
					JumpCondition jc = memSubDomain.getJumpCondition(volRegionVar);
					if (jc==null){
						jc = new JumpCondition(volRegionVar);
						memSubDomain.addJumpCondition(jc);
					}
					Expression flux = getIdentifierSubstitutions(resolvedFluxes[i].getFluxExpression(),resolvedFluxes[i].getUnitDefinition(),membraneStructureAnalyzer.getSurfaceClass());
					if (memSubDomain.getInsideCompartment().getName().equals(sm.getGeometryClass().getName())){
						jc.setInFlux(flux);
					}else if (memSubDomain.getOutsideCompartment().getName().equals(sm.getGeometryClass().getName())){
						jc.setOutFlux(flux);
					}else{
						throw new RuntimeException("Application  " + simContext.getName() + " : " + scm.getSpeciesContext().getName()+" has spatially resolved flux at membrane "+scm.getSpeciesContext().getStructure().getName()+" with a non-local flux species "+scm.getSpeciesContext().getName());
					}
				}else{
					throw new MappingException("Application  " + simContext.getName() + " : " + scm.getSpeciesContext().getName()+" has spatially resolved flux at membrane "+scm.getSpeciesContext().getStructure().getName()+", but doesn't diffuse in compartment "+scm.getSpeciesContext().getStructure().getName());
				}
			}
		}

		//
		// create fast system (if neccessary)
		//
		SpeciesContextMapping fastSpeciesContextMappings[] = membraneStructureAnalyzer.getFastSpeciesContextMappings();
		if (fastSpeciesContextMappings!=null){
			FastSystem fastSystem = new FastSystem(mathDesc);
			for (int i=0;i<fastSpeciesContextMappings.length;i++){
				SpeciesContextMapping scm = fastSpeciesContextMappings[i];
				if (scm.getFastInvariant()==null){
					//
					// independant-fast variable, create a fastRate object
					//
					VCUnitDefinition rateUnit = scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit);
					FastRate fastRate = new FastRate(getIdentifierSubstitutions(scm.getFastRate(),rateUnit,surfaceClass));
					fastSystem.addFastRate(fastRate);
				}else{
					//
					// dependant-fast variable, create a fastInvariant object
					//
					VCUnitDefinition invariantUnit = scm.getSpeciesContext().getUnitDefinition();
					FastInvariant fastInvariant = new FastInvariant(getIdentifierSubstitutions(scm.getFastInvariant(),invariantUnit,surfaceClass));
					fastSystem.addFastInvariant(fastInvariant);
				}
			}
			memSubDomain.setFastSystem(fastSystem);
		}
		//
		// create Membrane-region equations for potential of this resolved membrane
		//
		Structure[] resolvedSurfaceStructures = membraneStructureAnalyzer.getStructures();
		for (int m = 0; m < resolvedSurfaceStructures.length; m++) {
			if (resolvedSurfaceStructures[m] instanceof Membrane){
				Membrane membrane = (Membrane)resolvedSurfaceStructures[m];
				MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
				if (membraneMapping.getCalculateVoltage()){
					ElectricalDevice membraneDevices[] = potentialMapping.getElectricalDevices(membrane);
					int numCapacitiveDevices = 0;
					MembraneElectricalDevice capacitiveDevice = null;
					for (int i = 0; i < membraneDevices.length; i++){
						if (membraneDevices[i] instanceof MembraneElectricalDevice){
							numCapacitiveDevices++;
							capacitiveDevice = (MembraneElectricalDevice)membraneDevices[i];
						}
					}
					if (numCapacitiveDevices!=1){
						throw new MappingException("expecting 1 capacitive electrical device on graph edge for membrane "+membrane.getName()+", found '"+numCapacitiveDevices+"'");
					}
					if (varHash.getVariable(getMathSymbol(capacitiveDevice.getVoltageSymbol(),membraneMapping.getGeometryClass())) instanceof MembraneRegionVariable){
						MembraneRegionVariable vVar = (MembraneRegionVariable)varHash.getVariable(getMathSymbol(capacitiveDevice.getVoltageSymbol(),membraneMapping.getGeometryClass()));
						Parameter initialVoltageParm = capacitiveDevice.getMembraneMapping().getInitialVoltageParameter();
						Expression initExp = getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),capacitiveDevice.getMembraneMapping().getGeometryClass());
						MembraneRegionEquation vEquation = new MembraneRegionEquation(vVar,initExp);
						vEquation.setMembraneRateExpression(getIdentifierSubstitutions(potentialMapping.getOdeRHS(capacitiveDevice,this),membrane.getMembraneVoltage().getUnitDefinition().divideBy(timeUnit),capacitiveDevice.getMembraneMapping().getGeometryClass()));
						memSubDomain.addEquation(vEquation);
					}
				}
			}
		}
	}

	// create equations for event assignment or rate rule targets that are model params/species, etc.
	Set<VolVariable> hashKeySet = eventVolVarHash.keySet();
	Iterator<VolVariable> volVarsIter = hashKeySet.iterator();
	// working under the assumption that we are dealing with non-spatial math, hence only one compartment domain!
	SubDomain subDomain = mathDesc.getSubDomains().nextElement();
	while (volVarsIter.hasNext()) {
		VolVariable volVar = volVarsIter.next();
		EventAssignmentOrRateRuleInitParameter initParam = eventVolVarHash.get(volVar);
		//check event initial condition, it shouldn't contain vars, we have to do it here, coz we want to substitute functions...etc.
		Expression eapExp = MathUtilities.substituteFunctions(initParam.getExpression(), varHash);
		if(eapExp.getSymbols() != null){
			for(String symbol:eapExp.getSymbols()){
				SymbolTableEntry ste = eapExp.getSymbolBinding(symbol);
				if(ste instanceof VolVariable || ste instanceof MemVariable){
					throw new MathException("Variables are not allowed in Event assignment initial condition.\nEvent assignment target: " + volVar.getName()  + " has variable (" +symbol+ ") in its expression.");
				}

			}
		}
		Expression rateExpr = new Expression(0.0);
		RateRuleRateParameter rateParam = rateRuleRateParamHash.get(volVar);
		if (rateParam != null) {
			// this is a rate rule, get its expression.
			rateExpr = new Expression(getMathSymbol(rateParam, null));
		}
		Equation equation = new OdeEquation(volVar, new Expression(getMathSymbol(initParam, null)), rateExpr);
		subDomain.addEquation(equation);
	}

	// events - add events to math desc for event assignments that have parameters as target variables
	if (bioevents != null && bioevents.length > 0) {
		for (BioEvent be : bioevents) {
			// transform the bioEvent trigger/delay to math Event
			LocalParameter genTriggerParam = be.getParameter(BioEventParameterType.GeneralTriggerFunction);
			Expression mathTriggerExpr = getIdentifierSubstitutions(new Expression(genTriggerParam,be.getNameScope()), modelUnitSystem.getInstance_DIMENSIONLESS(), null);

			Delay mathDelay = null;
			LocalParameter delayParam = be.getParameter(BioEventParameterType.TriggerDelay);
			if (delayParam != null && delayParam.getExpression() != null && !delayParam.getExpression().compareEqual(new Expression(0.0))){
				boolean bUseValsFromTriggerTime = be.getUseValuesFromTriggerTime();
				Expression mathDelayExpr = getIdentifierSubstitutions(new Expression(delayParam, be.getNameScope()), timeUnit, null);
				mathDelay = new Delay(bUseValsFromTriggerTime, mathDelayExpr);
			}

			// now deal with (bio)event Assignment translation to math EventAssignment
			ArrayList<EventAssignment> eventAssignments = be.getEventAssignments();
			ArrayList<Event.EventAssignment> mathEventAssignmentsList = new ArrayList<Event.EventAssignment>();
			if(eventAssignments != null){
				for (EventAssignment ea : eventAssignments) {
					SymbolTableEntry ste = simContext.getEntry(ea.getTarget().getName());
					if (ste instanceof StructureSize) {
						throw new RuntimeException("Event Assignment Variable for compartment size is not supported yet");
					}
					VCUnitDefinition eventAssignVarUnit = ste.getUnitDefinition();
					Variable variable = varHash.getVariable(ste.getName());
					Event.EventAssignment mathEA = new Event.EventAssignment(variable, getIdentifierSubstitutions(ea.getAssignmentExpression(), eventAssignVarUnit, null));
					mathEventAssignmentsList.add(mathEA);
				}
			}
			// use the translated trigger, delay and event assignments to create (math) event
			Event mathEvent = new Event(be.getName(), mathTriggerExpr, mathDelay, mathEventAssignmentsList);
			mathDesc.addEvent(mathEvent);
		}
	}

	if (simContext.getMicroscopeMeasurement()!=null && simContext.getMicroscopeMeasurement().getFluorescentSpecies().size()>0){
		MicroscopeMeasurement measurement = simContext.getMicroscopeMeasurement();
		Expression volumeConcExp = new Expression(0.0);
		Expression membraneDensityExp = new Expression(0.0);
		for (SpeciesContext speciesContext : measurement.getFluorescentSpecies()){
			GeometryClass geometryClass = simContext.getGeometryContext().getStructureMapping(speciesContext.getStructure()).getGeometryClass();
			StructureMapping structureMapping = simContext.getGeometryContext().getStructureMapping(speciesContext.getStructure());
			StructureMappingParameter unitSizeParameter = structureMapping.getUnitSizeParameter();
			Expression mappedSpeciesContextExpression = Expression.mult(unitSizeParameter.getExpression(),new Expression(getMathSymbol(speciesContext, geometryClass)));
			VCUnitDefinition mappedSpeciesContextUnit = unitSizeParameter.getUnitDefinition().multiplyBy(speciesContext.getUnitDefinition());
			if (geometryClass instanceof SubVolume){
				// volume function
				int dimension = 3;
				VCUnitDefinition desiredConcUnits = model.getUnitSystem().getInstance("molecules").divideBy(model.getUnitSystem().getLengthUnit().raiseTo(new ucar.units_vcell.RationalNumber(dimension)));
				Expression unitFactor = getUnitFactor(desiredConcUnits.divideBy(mappedSpeciesContextUnit));
				volumeConcExp = Expression.add(volumeConcExp,Expression.mult(unitFactor,mappedSpeciesContextExpression)).simplifyJSCL();
			}else if (geometryClass instanceof SurfaceClass){
				// membrane function
				int dimension = 2;
				VCUnitDefinition desiredSurfaceDensityUnits = model.getUnitSystem().getInstance("molecules").divideBy(model.getUnitSystem().getLengthUnit().raiseTo(new ucar.units_vcell.RationalNumber(dimension)));
				Expression unitFactor = getUnitFactor(desiredSurfaceDensityUnits.divideBy(mappedSpeciesContextUnit));
				membraneDensityExp = Expression.add(membraneDensityExp,Expression.mult(unitFactor,mappedSpeciesContextExpression)).simplifyJSCL();
			}else{
				throw new MathException("unsupported geometry mapping for microscopy measurement");
			}
		}
		ConvolutionKernel kernel = measurement.getConvolutionKernel();
		if (kernel instanceof ExperimentalPSF){
			if (!membraneDensityExp.isZero()){
				throw new MappingException("membrane variables and functions not yet supported for Z projection in Microcopy Measurements");
			}
			ExperimentalPSF psf = (ExperimentalPSF)kernel;
			DataSymbol psfDataSymbol = psf.getPSFDataSymbol();
			if (psfDataSymbol instanceof FieldDataSymbol){
				FieldDataSymbol fieldDataSymbol = (FieldDataSymbol)psfDataSymbol;
				String fieldDataName = ((FieldDataSymbol) psfDataSymbol).getExternalDataIdentifier().getName();

				Expression psfExp = Expression.function(
						FieldFunctionDefinition.FUNCTION_name,
						new Expression("'"+fieldDataName+"'"),
						new Expression("'"+fieldDataSymbol.getFieldDataVarName()+"'"),
						new Expression(fieldDataSymbol.getFieldDataVarTime()),
						new Expression("'"+fieldDataSymbol.getFieldDataVarType()+"'"));
				varHash.addVariable(new Function("__PSF__",psfExp,null));
			}
			Expression convExp = Expression.function(ConvFunctionDefinition.FUNCTION_name,volumeConcExp,new Expression("__PSF__"));
			varHash.addVariable(newFunctionOrConstant(measurement.getName(),convExp,null));

		} else if (kernel instanceof GaussianConvolutionKernel) {
			GaussianConvolutionKernel gaussianConvolutionKernel = (GaussianConvolutionKernel) kernel;
			GaussianConvolutionDataGeneratorKernel mathKernel = new GaussianConvolutionDataGeneratorKernel(gaussianConvolutionKernel.getSigmaXY_um(), gaussianConvolutionKernel.getSigmaZ_um());
			ConvolutionDataGenerator dataGenerator = new ConvolutionDataGenerator(measurement.getName(), mathKernel, volumeConcExp, membraneDensityExp);
			mathDesc.getPostProcessingBlock().addDataGenerator(dataGenerator);
		} else if (kernel instanceof ProjectionZKernel){
			if (mathDesc.getGeometry().getDimension() == 3) {
				if (!membraneDensityExp.isZero()){
					throw new MappingException("membrane variables and functions not yet supported for Z projection in Microcopy Measurements");
				}
				ProjectionDataGenerator dataGenerator = new ProjectionDataGenerator(measurement.getName(), null, ProjectionDataGenerator.Axis.z, ProjectionDataGenerator.Operation.sum, volumeConcExp);
				mathDesc.getPostProcessingBlock().addDataGenerator(dataGenerator);
			} else {
				throw new MappingException("Z Projection is only supported in 3D spatial applications.");
			}
		}

	}

	//
	// add any missing unit conversion factors (they don't depend on anyone else ... can do it at the end)
	//
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		if (fieldMathMappingParameters[i] instanceof UnitFactorParameter){
			GeometryClass geometryClass = fieldMathMappingParameters[i].getGeometryClass();
			Variable variable = newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),geometryClass),fieldMathMappingParameters[i].getGeometryClass());
			if (varHash.getVariable(variable.getName())==null){
				varHash.addVariable(variable);
			}
		}
	}

	//
	// set Variables to MathDescription all at once with the order resolved by "VariableHash"
	//
	mathDesc.setAllVariables(varHash.getAlphabeticallyOrderedVariables());

	//
	// check all fast systems (needs to have bound expressions)
	//
	for (SubDomain subDomain1 : Collections.list(mathDesc.getSubDomains())){
		if (subDomain1.getFastSystem()!=null){
			// constructor calls the 'refresh' method which constructs dependency matrix, dependent/independent vars and pseudoconstants, etc.
			new FastSystemAnalyzer(subDomain1.getFastSystem(), mathDesc);
		}
	}

	mathDesc.refreshDependencies();

	if (!mathDesc.isValid()){
		if (lg.isTraceEnabled()) {
			lg.trace(mathDesc.getVCML_database());
		}
		throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
	}

//System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string begin ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
//System.out.println(mathDesc.getVCML());
//System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string end ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
}

// TODO: work in progress
//private static Expression flattenUnbound(Expression originalExpression, SimulationContext simContext, Map<String, SymbolTableEntry> entryMap, 
//		Map<ModelParameter, EventAssignmentOrRateRuleInitParameter> modelParamTorateRuleInitMapping, int depth) {
//	if(depth > 20) {
//		return originalExpression;	// we just exit gracefully, will get meaningful exception during math consistency verification
//	}
//	Model model = simContext.getModel();
//	GeometryContext gc = simContext.getGeometryContext();
//	String[] originalSymbols = originalExpression.getSymbols();
//	if(originalSymbols == null || originalSymbols.length == 0) {
//		return originalExpression;
//	}
//	Expression newExpression = new Expression(originalExpression);
//	
//
//
//
//
//
//	// check if 'initExpr' has other speciesContexts or rate rule global parameter variables in its expression
//	// need to replace it with 'spContext_init', modelParameter_init
//	for (String symbol : originalSymbols) {
//		// if symbol is a speciesContext, replacing it with a reference to initial condition for that speciesContext.
//		SymbolTableEntry ste = newExpression.getSymbolBinding(symbol);
//		if (ste == null) {
//			System.out.println("Unexpected NULL symbol in the initial expression");
//		} else if (ste instanceof SpeciesContext) {
//			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec((SpeciesContext)ste);
//			// TODO: what if initial count???
//			SpeciesContextSpecParameter spCInitParm = scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
//			// need to get init condn expression, but can't get it from getMathSymbol() (mapping between bio and math), hence get it as below.
//			Expression scsInitExpr = new Expression(spCInitParm, getNameScope());
//			mpInitExpr.substituteInPlace(new Expression(ste.getName()), scsInitExpr);
//		} else if(ste instanceof ModelParameter) {
//			ModelParameter mpArg = (ModelParameter)ste;
//			System.out.println(mpArg.getName());
//			if(simContext.getRateRule(mpArg) == null) {
//				continue;		// only globals that are RateRule variables need to be replaced with their _init variable
//			}
//			EventAssignmentOrRateRuleInitParameter mpInitParam = modelParamTorateRuleInitMapping.get(mpArg);
//			if(mpInitParam != null) {		// we already made it, we only need to use it
//				Expression mpArgInitExpr = new Expression(mpInitParam, getNameScope());
//				mpInitExpr.substituteInPlace(new Expression(ste.getName()), mpArgInitExpr);
//			}
//		} else {
//			String msg = ste.getName() == null ? "??" : ste.getName();
//			System.out.println("Unexpected symbol type for " + msg + " in the initial expression of " + argName);
//		}
//	}
//
//	
//	
//	for(String symbol : originalSymbols) {
//		SymbolTableEntry ste = entryMap.get(symbol);
//		Expression steExp = ste.getExpression();
//		if(ste instanceof Structure.StructureSize) {
//			Structure symbolStructure = model.getStructure(symbol);
//			StructureMapping.StructureMappingParameter mapping = gc.getStructureMapping(symbolStructure).getSizeParameter();
//			steExp = mapping.getExpression();
//		}
//		if(steExp == null) {
//			return originalExpression;	// we just exit gracefully, will get meaningful exception during math consistency verification
//		}
//		try {
//			newExpression.substituteInPlace(new Expression(symbol), new Expression(steExp));
//		} catch(ExpressionException e) {
//			return originalExpression;	// we just exit gracefully, will get meaningful exception during math consistency verification
//		}
//	}
//	String[] newSymbols = newExpression.getSymbols();
//	if(newSymbols == null || newSymbols.length == 0) {
//		return originalExpression;
//	} else if(newSymbols.length != originalSymbols.length) {
//		depth++;
//		return flattenUnbound(newExpression, simContext, entryMap, modelParamTorateRuleInitMapping, depth);
//	} else if(newSymbols.length == originalSymbols.length) {
//		Arrays.sort(originalSymbols);
//		Arrays.sort(newSymbols);
//		for(int i=0; i<newSymbols.length; i++) {
//			if(!originalSymbols[i].contentEquals(newSymbols[i])) {
//				depth++;
//				return flattenUnbound(newExpression, simContext, entryMap, modelParamTorateRuleInitMapping, depth);
//			}
//		}
//	}
//	return newExpression;
//}

//determine membrane inside and outside subvolume
public static Pair<SubVolume,SubVolume> computeBoundaryConditionSource(Model model, SimulationContext simContext, SurfaceClass surfaceClass) {

	SubVolume outerSubVolume = null;
	SubVolume innerSubVolume = null;
	Structure[] mappedStructures = simContext.getGeometryContext().getStructuresFromGeometryClass(surfaceClass);
	// this preserves backward compatibility so that membrane subdomain
	// inside and outside correspond to structure hierarchy when present
	for (Structure s : mappedStructures) {
		if (s instanceof Membrane) {
			Membrane m = (Membrane)s;
			Feature infeature = model.getStructureTopology().getInsideFeature(m);
			if (infeature != null) {
				FeatureMapping insm = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(infeature);
				if (insm.getGeometryClass() instanceof SubVolume) {
					innerSubVolume = (SubVolume)insm.getGeometryClass();
				}
			}
			Feature outfeature = model.getStructureTopology().getOutsideFeature(m);
			if (outfeature != null) {
				FeatureMapping outsm = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(outfeature);
				if (outsm.getGeometryClass() instanceof SubVolume) {
					outerSubVolume = (SubVolume)outsm.getGeometryClass();
				}
			}
		}
	}
	// if structure hierarchy not present, alphabetically choose inside and outside
	// make the choice deterministic
	if (innerSubVolume == null || outerSubVolume == null || innerSubVolume == outerSubVolume){
		Set<SubVolume> sv = surfaceClass.getAdjacentSubvolumes();
		Iterator<SubVolume> iterator = sv.iterator();
		innerSubVolume = iterator.next();
		outerSubVolume = iterator.next();
		if (innerSubVolume.getName().compareTo(outerSubVolume.getName()) > 0) {
			SubVolume temp = innerSubVolume;
			innerSubVolume = outerSubVolume;
			outerSubVolume = temp;
		}
	}
	Pair<SubVolume,SubVolume> ret = new Pair<>(innerSubVolume, outerSubVolume);
	return ret;
}

private void addSubdomains(Model model, ArrayList<CompartmentSubdomainContext> compartmentSubdomainContexts,
						   ArrayList<MembraneSubdomainContext> membraneSubdomainContexts) throws MathException {
	{
		SubVolume[] subVolumes = simContext.getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
		for (int j=0;j<subVolumes.length;j++){
			SubVolume subVolume = (SubVolume)subVolumes[j];
			//
			// get priority of subDomain
			//
			int priority;
			if (simContext.getGeometryContext().getGeometry().getDimension() == 0){
				priority = CompartmentSubDomain.NON_SPATIAL_PRIORITY;
			} else {
				priority = j; // now does not have to match spatial feature, *BUT* needs to be unique
			}
			//
			// create subDomain
			//
			CompartmentSubDomain subDomain = new CompartmentSubDomain(subVolume.getName(), priority);
			Domain domain = new Domain(subDomain);
			mathDesc.addSubDomain(subDomain);
			compartmentSubdomainContexts.add(new CompartmentSubdomainContext(subDomain, subVolume, domain));
		}
		GeometryClass[] geometryClasses = simContext.getGeometryContext().getGeometry().getGeometryClasses();

		for (int k=0;k<geometryClasses.length;k++){
			if (!(geometryClasses[k] instanceof SurfaceClass)){
				continue;
			}

			SurfaceClass surfaceClass = (SurfaceClass)geometryClasses[k];
			// determine membrane inside and outside subvolume
			// this preserves backward compatibility so that membrane subdomain
			// inside and outside correspond to structure hierarchy when present
			Pair<SubVolume,SubVolume> ret = computeBoundaryConditionSource(model, simContext, surfaceClass);
			SubVolume innerSubVolume = ret.one;
			SubVolume outerSubVolume = ret.two;

			//
			// make list of SurfaceRegionObjects for this membrane subdomain
			//
			ArrayList<SurfaceRegionObject> surfaceRegionObjects = new ArrayList<SurfaceRegionObject>();
			for (SpatialObject spatialObject : simContext.getSpatialObjects()) {
				if (spatialObject instanceof SurfaceRegionObject) {
					SurfaceRegionObject surfaceRegionObject = (SurfaceRegionObject) spatialObject;
					if (surfaceRegionObject.isQuantityCategoryEnabled(QuantityCategory.SurfaceVelocity) &&
							(((surfaceRegionObject.getInsideSubVolume() == innerSubVolume) && (surfaceRegionObject.getOutsideSubVolume() == outerSubVolume)) ||
									((surfaceRegionObject.getInsideSubVolume() == outerSubVolume) && (surfaceRegionObject.getOutsideSubVolume() == innerSubVolume)))){
						surfaceRegionObjects.add(surfaceRegionObject);
					}
				}
			}

			//
			// create subDomain
			//
			CompartmentSubDomain outerCompartment = mathDesc.getCompartmentSubDomain(outerSubVolume.getName());
			CompartmentSubDomain innerCompartment = mathDesc.getCompartmentSubDomain(innerSubVolume.getName());


			MembraneSubDomain memSubDomain = new MembraneSubDomain(innerCompartment,outerCompartment,surfaceClass.getName());
			mathDesc.addSubDomain(memSubDomain);
			membraneSubdomainContexts.add(new MembraneSubdomainContext(memSubDomain, new Domain(memSubDomain), surfaceClass, innerSubVolume, outerSubVolume, surfaceRegionObjects.toArray(new SurfaceRegionObject[0])));
		}
	}
}

private void addSpatialProcesses(
		VariableHash varHash,
		ArrayList<CompartmentSubdomainContext> compartmentSubdomainContexts,
		ArrayList<MembraneSubdomainContext> membraneSubdomainContexts) throws MathException, MappingException, ExpressionException {

	if (simContext.getGeometry().getDimension()==0){
		return;
	}
	//
	// consider the evaluation of spatial quantities associated with spatial objects
	//
	for (SpatialObject spatialObject : simContext.getSpatialObjects()){
		if (spatialObject instanceof PointObject){
			PointObject pointObject = (PointObject)spatialObject;
			//
			// if true, have to solve for this category
			//
			boolean bPosition = pointObject.isQuantityCategoryEnabled(QuantityCategory.PointPosition);
			boolean bVelocity = pointObject.isQuantityCategoryEnabled(QuantityCategory.PointVelocity);
			boolean bDirection = pointObject.isQuantityCategoryEnabled(QuantityCategory.DirectionToPoint);
			boolean bDistance = pointObject.isQuantityCategoryEnabled(QuantityCategory.PointDistanceMap);

			//
			// either make a point subdomain, or just define functions.
			//
			ArrayList<PointLocation> pointLocationProcesses = new ArrayList<PointLocation>();
			ArrayList<PointKinematics> pointKinematicsProcesses = new ArrayList<PointKinematics>();
			for (SpatialProcess spatialProcess : simContext.getSpatialProcesses()){
				if (spatialProcess instanceof PointLocation && ((PointLocation) spatialProcess).getPointObject() == pointObject){
					pointLocationProcesses.add((PointLocation) spatialProcess);
				}
				if (spatialProcess instanceof PointKinematics && ((PointKinematics) spatialProcess).getPointObject() == pointObject){
					pointKinematicsProcesses.add((PointKinematics) spatialProcess);
				}
			}
			if (pointLocationProcesses.size()==1 && pointKinematicsProcesses.size()==0 && !bVelocity){
				GeometryClass gc = null;
				PointLocation pointLocation = pointLocationProcesses.get(0);
				if (bPosition){
					if (simContext.getGeometry().getDimension()==1){
						SpatialQuantity posXQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.X);
						LocalParameter posXParam = pointLocation.getParameter(SpatialProcessParameterType.PointPositionX);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posXParam, gc),
								getIdentifierSubstitutions(posXParam.getExpression(), posXParam.getUnitDefinition(), gc),
								gc));
						Expression posXExp = new Expression(posXParam,pointLocation.getNameScope());
						Expression xExp = new Expression(simContext.getModel().getReservedSymbolByRole(ReservedSymbolRole.X),simContext.getModel().getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posXQuantity, gc),
								getIdentifierSubstitutions(posXExp,posXQuantity.getUnitDefinition(),gc),
								gc));
						Expression posX_minus_X = Expression.add(posXExp,Expression.negate(xExp));
						Expression signum_posX_minus_X = Expression.function(FunctionType.MIN, new Expression(1.0), Expression.function(FunctionType.MAX,Expression.mult(new Expression(1e10),posX_minus_X),new Expression(-1)));
						if (bDirection){
							SpatialQuantity dirXQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.X);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirXQuantity, gc),
									getIdentifierSubstitutions(signum_posX_minus_X,dirXQuantity.getUnitDefinition(),gc),
									gc));
						}
						if (bDistance){
							Expression abs_X_minux_posX = Expression.function(FunctionType.ABS, posX_minus_X);
							SpatialQuantity distanceQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointDistanceMap,QuantityComponent.Scalar);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(distanceQuantity, gc),
									getIdentifierSubstitutions(abs_X_minux_posX,distanceQuantity.getUnitDefinition(),gc),
									gc));
						}
					}
					if (simContext.getGeometry().getDimension()==2){
						SpatialQuantity posXQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.X);
						LocalParameter posXParam = pointLocation.getParameter(SpatialProcessParameterType.PointPositionX);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posXParam, gc),
								getIdentifierSubstitutions(posXParam.getExpression(), posXParam.getUnitDefinition(), gc),
								gc));
						Expression posXExp = new Expression(posXParam,pointLocation.getNameScope());
						Expression xExp = new Expression(simContext.getModel().getReservedSymbolByRole(ReservedSymbolRole.X),simContext.getModel().getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posXQuantity, gc),
								getIdentifierSubstitutions(new Expression(posXParam,pointLocation.getNameScope()),posXQuantity.getUnitDefinition(),gc),
								gc));
						SpatialQuantity posYQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.Y);
						LocalParameter posYParam = pointLocation.getParameter(SpatialProcessParameterType.PointPositionY);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posYParam, gc),
								getIdentifierSubstitutions(posYParam.getExpression(), posYParam.getUnitDefinition(), gc),
								gc));
						Expression posYExp = new Expression(posYParam,pointLocation.getNameScope());
						Expression yExp = new Expression(simContext.getModel().getReservedSymbolByRole(ReservedSymbolRole.Y),simContext.getModel().getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posYQuantity, gc),
								getIdentifierSubstitutions(new Expression(posYParam,pointLocation.getNameScope()),posYQuantity.getUnitDefinition(),gc),
								gc));
						Expression posX_minux_X = Expression.add(posXExp,Expression.negate(xExp));
						Expression posY_minux_Y = Expression.add(posYExp,Expression.negate(yExp));
						Expression DX2 = Expression.mult(posX_minux_X,posX_minux_X);
						Expression DY2 = Expression.mult(posY_minux_Y,posY_minux_Y);
						Expression sqrt_DX2_DY2 = Expression.function(FunctionType.SQRT, Expression.add(DX2,DY2));
						Expression dirX = Expression.div(posX_minux_X, Expression.add(sqrt_DX2_DY2,new Expression(1e-8)));
						Expression dirY = Expression.div(posY_minux_Y, Expression.add(sqrt_DX2_DY2,new Expression(1e-8)));
						if (bDirection){
							SpatialQuantity dirXQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.X);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirXQuantity, gc),
									getIdentifierSubstitutions(dirX,dirXQuantity.getUnitDefinition(),gc),
									gc));
							SpatialQuantity dirYQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.Y);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirYQuantity, gc),
									getIdentifierSubstitutions(dirY,dirYQuantity.getUnitDefinition(),gc),
									gc));
						}
						if (bDistance){
							SpatialQuantity distanceQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointDistanceMap,QuantityComponent.Scalar);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(distanceQuantity, gc),
									getIdentifierSubstitutions(sqrt_DX2_DY2,distanceQuantity.getUnitDefinition(),gc),
									gc));
						}
					}
					if (simContext.getGeometry().getDimension()==3){
						SpatialQuantity posXQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.X);
						LocalParameter posXParam = pointLocation.getParameter(SpatialProcessParameterType.PointPositionX);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posXParam, gc),
								getIdentifierSubstitutions(posXParam.getExpression(), posXParam.getUnitDefinition(), gc),
								gc));
						Expression posXExp = new Expression(posXParam,pointLocation.getNameScope());
						Expression xExp = new Expression(simContext.getModel().getReservedSymbolByRole(ReservedSymbolRole.X),simContext.getModel().getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posXQuantity, gc),
								getIdentifierSubstitutions(new Expression(posXParam,pointLocation.getNameScope()),posXQuantity.getUnitDefinition(),gc),
								gc));
						SpatialQuantity posYQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.Y);
						LocalParameter posYParam = pointLocation.getParameter(SpatialProcessParameterType.PointPositionY);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posYParam, gc),
								getIdentifierSubstitutions(posYParam.getExpression(), posYParam.getUnitDefinition(), gc),
								gc));
						Expression posYExp = new Expression(posYParam,pointLocation.getNameScope());
						Expression yExp = new Expression(simContext.getModel().getReservedSymbolByRole(ReservedSymbolRole.Y),simContext.getModel().getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posYQuantity, gc),
								getIdentifierSubstitutions(new Expression(posYParam,pointLocation.getNameScope()),posYQuantity.getUnitDefinition(),gc),
								gc));
						SpatialQuantity posZQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.Z);
						LocalParameter posZParam = pointLocation.getParameter(SpatialProcessParameterType.PointPositionZ);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posZParam, gc),
								getIdentifierSubstitutions(posZParam.getExpression(), posZParam.getUnitDefinition(), gc),
								gc));
						Expression posZExp = new Expression(posZParam,pointLocation.getNameScope());
						Expression zExp = new Expression(simContext.getModel().getReservedSymbolByRole(ReservedSymbolRole.Z),simContext.getModel().getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(posZQuantity, gc),
								getIdentifierSubstitutions(new Expression(posZParam,pointLocation.getNameScope()),posZQuantity.getUnitDefinition(),gc),
								gc));
						Expression posX_minux_X = Expression.add(posXExp,Expression.negate(xExp));
						Expression posY_minux_Y = Expression.add(posYExp,Expression.negate(yExp));
						Expression posZ_minux_Z = Expression.add(posZExp,Expression.negate(zExp));
						Expression DX2 = Expression.mult(posX_minux_X,posX_minux_X);
						Expression DY2 = Expression.mult(posY_minux_Y,posY_minux_Y);
						Expression DZ2 = Expression.mult(posZ_minux_Z,posZ_minux_Z);
						Expression sqrt_DX2_DY2_DZ2 = Expression.function(FunctionType.SQRT, Expression.add(DX2,DY2,DZ2));
						Expression dirX = Expression.div(posX_minux_X, Expression.add(sqrt_DX2_DY2_DZ2,new Expression(1e-8)));
						Expression dirY = Expression.div(posY_minux_Y, Expression.add(sqrt_DX2_DY2_DZ2,new Expression(1e-8)));
						Expression dirZ = Expression.div(posZ_minux_Z, Expression.add(sqrt_DX2_DY2_DZ2,new Expression(1e-8)));
						if (bDirection){
							SpatialQuantity dirXQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.X);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirXQuantity, gc),
									getIdentifierSubstitutions(dirX,dirXQuantity.getUnitDefinition(),gc),
									gc));
							SpatialQuantity dirYQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.Y);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirYQuantity, gc),
									getIdentifierSubstitutions(dirY,dirYQuantity.getUnitDefinition(),gc),
									gc));
							SpatialQuantity dirZQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.Z);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirZQuantity, gc),
									getIdentifierSubstitutions(dirZ,dirZQuantity.getUnitDefinition(),gc),
									gc));
						}
						if (bDistance){
							SpatialQuantity distanceQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointDistanceMap,QuantityComponent.Scalar);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(distanceQuantity, gc),
									getIdentifierSubstitutions(sqrt_DX2_DY2_DZ2,distanceQuantity.getUnitDefinition(),gc),
									gc));
						}
					}
				}else{
					throw new MappingException("PointLocation process defined for pointObject '"+pointObject.getName()+"' but Position not enabled");
				}
			}else if (pointLocationProcesses.size()==0 && pointKinematicsProcesses.size()==1){
				GeometryClass gc = null;
				PointKinematics pointKinematics = pointKinematicsProcesses.get(0);
				if (bPosition && bVelocity){
					LocalParameter velXParam = pointKinematics.getParameter(SpatialProcessParameterType.PointVelocityX);
					LocalParameter iniPosXParam = pointKinematics.getParameter(SpatialProcessParameterType.PointInitialPositionX);
					SpatialQuantity posXQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointPosition, QuantityComponent.X);
					SpatialQuantity velXQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointVelocity, QuantityComponent.X);
					Expression posXExp = new Expression(posXQuantity,simContext.getNameScope());
					Expression xExp = new Expression(simContext.getModel().getReservedSymbolByRole(ReservedSymbolRole.X),simContext.getModel().getNameScope());
					Expression posX_minux_X = Expression.add(posXExp,Expression.negate(xExp));

					LocalParameter velYParam = pointKinematics.getParameter(SpatialProcessParameterType.PointVelocityY);
					LocalParameter iniPosYParam = pointKinematics.getParameter(SpatialProcessParameterType.PointInitialPositionY);
					SpatialQuantity posYQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointPosition, QuantityComponent.Y);
					SpatialQuantity velYQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointVelocity, QuantityComponent.Y);
					Expression posYExp = new Expression(posYQuantity,simContext.getNameScope());
					Expression yExp = new Expression(simContext.getModel().getReservedSymbolByRole(ReservedSymbolRole.Y),simContext.getModel().getNameScope());
					Expression posY_minux_Y = Expression.add(posYExp,Expression.negate(yExp));

					LocalParameter velZParam = pointKinematics.getParameter(SpatialProcessParameterType.PointVelocityZ);
					LocalParameter iniPosZParam = pointKinematics.getParameter(SpatialProcessParameterType.PointInitialPositionZ);
					SpatialQuantity posZQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointPosition, QuantityComponent.Z);
					SpatialQuantity velZQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointVelocity, QuantityComponent.Z);
					Expression posZExp = new Expression(posZQuantity,simContext.getNameScope());
					Expression zExp = new Expression(simContext.getModel().getReservedSymbolByRole(ReservedSymbolRole.Z),simContext.getModel().getNameScope());
					Expression posZ_minux_Z = Expression.add(posZExp,Expression.negate(zExp));

					String pointSubdomainName = pointObject.getName();
					Domain domain = new Domain(pointSubdomainName);
					PointSubDomain pointSubdomain = new PointSubDomain(pointSubdomainName);
					mathDesc.addSubDomain(pointSubdomain);

					if (simContext.getGeometry().getDimension()>=1){
						PointVariable posXVar = new PointVariable(getMathSymbol(posXQuantity, gc), domain);
						varHash.addVariable(posXVar);
						Expression initXExp = getIdentifierSubstitutions(new Expression(iniPosXParam,pointKinematics.getNameScope()), iniPosXParam.getUnitDefinition(), gc);
						Expression rateXExp = getIdentifierSubstitutions(new Expression(velXParam,pointKinematics.getNameScope()), velXParam.getUnitDefinition(), gc);
						OdeEquation odeX = new OdeEquation(posXVar, initXExp, rateXExp);
						pointSubdomain.addEquation(odeX);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(iniPosXParam, gc),
								getIdentifierSubstitutions(new Expression(iniPosXParam.getExpression()), iniPosXParam.getUnitDefinition(), gc),
								gc));
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velXParam, gc),
								getIdentifierSubstitutions(new Expression(velXParam.getExpression()), velXParam.getUnitDefinition(), gc),
								gc));
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velXQuantity, gc),
								getIdentifierSubstitutions(new Expression(velXParam,pointKinematics.getNameScope()), velXQuantity.getUnitDefinition(), gc),
								gc));
						pointSubdomain.setPositionX(getIdentifierSubstitutions(new Expression(posXQuantity,simContext.getNameScope()), posXQuantity.getUnitDefinition(), gc));
					}
					if (simContext.getGeometry().getDimension()>=2){
						PointVariable posYVar = new PointVariable(getMathSymbol(posYQuantity, gc), domain);
						varHash.addVariable(posYVar);
						Expression initYExp = getIdentifierSubstitutions(new Expression(iniPosYParam,pointKinematics.getNameScope()), iniPosYParam.getUnitDefinition(), gc);
						Expression rateYExp = getIdentifierSubstitutions(new Expression(velYParam,pointKinematics.getNameScope()), velYParam.getUnitDefinition(), gc);
						OdeEquation odeY = new OdeEquation(posYVar, initYExp, rateYExp);
						pointSubdomain.addEquation(odeY);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(iniPosYParam, gc),
								getIdentifierSubstitutions(new Expression(iniPosYParam.getExpression()), iniPosYParam.getUnitDefinition(), gc),
								gc));
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velYParam, gc),
								getIdentifierSubstitutions(new Expression(velYParam.getExpression()), velYParam.getUnitDefinition(), gc),
								gc));
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velYQuantity, gc),
								getIdentifierSubstitutions(new Expression(velYParam,pointKinematics.getNameScope()), velYQuantity.getUnitDefinition(), gc),
								gc));
						pointSubdomain.setPositionY(getIdentifierSubstitutions(new Expression(posYQuantity,simContext.getNameScope()), posYQuantity.getUnitDefinition(), gc));
					}
					if (simContext.getGeometry().getDimension()==3){
						PointVariable posZVar = new PointVariable(getMathSymbol(posZQuantity, gc), domain);
						varHash.addVariable(posZVar);
						Expression initZExp = getIdentifierSubstitutions(new Expression(iniPosZParam,pointKinematics.getNameScope()), iniPosZParam.getUnitDefinition(), gc);
						Expression rateZExp = getIdentifierSubstitutions(new Expression(velZParam,pointKinematics.getNameScope()), velZParam.getUnitDefinition(), gc);
						OdeEquation odeZ = new OdeEquation(posZVar, initZExp, rateZExp);
						pointSubdomain.addEquation(odeZ);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(iniPosZParam, gc),
								getIdentifierSubstitutions(new Expression(iniPosZParam.getExpression()), iniPosZParam.getUnitDefinition(), gc),
								gc));
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velZParam, gc),
								getIdentifierSubstitutions(new Expression(velZParam.getExpression()), velZParam.getUnitDefinition(), gc),
								gc));
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velZQuantity, gc),
								getIdentifierSubstitutions(new Expression(velZParam,pointKinematics.getNameScope()), velZQuantity.getUnitDefinition(), gc),
								gc));
						pointSubdomain.setPositionZ(getIdentifierSubstitutions(new Expression(posZQuantity,simContext.getNameScope()), posZQuantity.getUnitDefinition(), gc));
					}
					if (simContext.getGeometry().getDimension()==1){
						Expression signum_posX_minus_X = Expression.function(FunctionType.MIN, new Expression(1.0), Expression.function(FunctionType.MAX,Expression.mult(new Expression(1e10),posX_minux_X),new Expression(-1)));
						if (bDirection){
							SpatialQuantity dirXQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.X);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirXQuantity, gc),
									getIdentifierSubstitutions(signum_posX_minus_X,dirXQuantity.getUnitDefinition(),gc),
									gc));
						}
						if (bDistance){
							Expression abs_X_minux_posX = Expression.function(FunctionType.ABS, posX_minux_X);
							SpatialQuantity distanceQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointDistanceMap,QuantityComponent.Scalar);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(distanceQuantity, gc),
									getIdentifierSubstitutions(abs_X_minux_posX,distanceQuantity.getUnitDefinition(),gc),
									gc));
						}
					}
					if (simContext.getGeometry().getDimension()==2){
						Expression DX2 = Expression.mult(posX_minux_X,posX_minux_X);
						Expression DY2 = Expression.mult(posY_minux_Y,posY_minux_Y);
						Expression sqrt_DX2_DY2 = Expression.function(FunctionType.SQRT, Expression.add(DX2,DY2));
						Expression dirX = Expression.div(posX_minux_X, Expression.add(sqrt_DX2_DY2,new Expression(1e-8)));
						Expression dirY = Expression.div(posY_minux_Y, Expression.add(sqrt_DX2_DY2,new Expression(1e-8)));
						if (bDirection){
							SpatialQuantity dirXQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.X);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirXQuantity, gc),
									getIdentifierSubstitutions(dirX,dirXQuantity.getUnitDefinition(),gc),
									gc));
							SpatialQuantity dirYQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.Y);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirYQuantity, gc),
									getIdentifierSubstitutions(dirY,dirYQuantity.getUnitDefinition(),gc),
									gc));
						}
						if (bDistance){
							SpatialQuantity distanceQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointDistanceMap,QuantityComponent.Scalar);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(distanceQuantity, gc),
									getIdentifierSubstitutions(sqrt_DX2_DY2,distanceQuantity.getUnitDefinition(),gc),
									gc));
						}

					}
					if (simContext.getGeometry().getDimension()==3){
						Expression DX2 = Expression.mult(posX_minux_X,posX_minux_X);
						Expression DY2 = Expression.mult(posY_minux_Y,posY_minux_Y);
						Expression DZ2 = Expression.mult(posZ_minux_Z,posZ_minux_Z);
						Expression sqrt_DX2_DY2_DZ2 = Expression.function(FunctionType.SQRT, Expression.add(DX2,DY2,DZ2));
						Expression dirX = Expression.div(posX_minux_X, Expression.add(sqrt_DX2_DY2_DZ2,new Expression(1e-8)));
						Expression dirY = Expression.div(posY_minux_Y, Expression.add(sqrt_DX2_DY2_DZ2,new Expression(1e-8)));
						Expression dirZ = Expression.div(posZ_minux_Z, Expression.add(sqrt_DX2_DY2_DZ2,new Expression(1e-8)));
						if (bDirection){
							SpatialQuantity dirXQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.X);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirXQuantity, gc),
									getIdentifierSubstitutions(dirX,dirXQuantity.getUnitDefinition(),gc),
									gc));
							SpatialQuantity dirYQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.Y);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirYQuantity, gc),
									getIdentifierSubstitutions(dirY,dirYQuantity.getUnitDefinition(),gc),
									gc));
							SpatialQuantity dirZQuantity = pointObject.getSpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.Z);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(dirZQuantity, gc),
									getIdentifierSubstitutions(dirZ,dirZQuantity.getUnitDefinition(),gc),
									gc));
						}
						if (bDistance){
							SpatialQuantity distanceQuantity = pointObject.getSpatialQuantity(QuantityCategory.PointDistanceMap,QuantityComponent.Scalar);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(distanceQuantity, gc),
									getIdentifierSubstitutions(sqrt_DX2_DY2_DZ2,distanceQuantity.getUnitDefinition(),gc),
									gc));
						}
					}
				}else{
					throw new MappingException(pointKinematics.getDescription()+" process defined for pointObject '"+pointObject.getName()+"' but Position and Velocity not enabled");
				}
			}else{
				throw new MappingException("expecting 1 location or kinematics process for point '"+pointObject.getName()+"'");
			}
		}else if (spatialObject instanceof SurfaceRegionObject){
			SurfaceRegionObject surfaceRegionObject = (SurfaceRegionObject)spatialObject;
			SubVolume insideSubvolume = surfaceRegionObject.getInsideSubVolume();
			SubVolume outsideSubvolume = surfaceRegionObject.getOutsideSubVolume();
			SurfaceClass surfaceClass = simContext.getGeometry().getGeometrySurfaceDescription().getSurfaceClass(insideSubvolume, outsideSubvolume);
			MembraneSubdomainContext memSubdomainContext = null;
			for (MembraneSubdomainContext context : membraneSubdomainContexts){
				if (context.surfaceClass == surfaceClass){
					memSubdomainContext = context;
				}
			}

			//
			// if true, have to solve for this category
			//
			boolean bNormal = surfaceRegionObject.isQuantityCategoryEnabled(QuantityCategory.Normal);
			boolean bVelocity = surfaceRegionObject.isQuantityCategoryEnabled(QuantityCategory.SurfaceVelocity);
			boolean bDistance = surfaceRegionObject.isQuantityCategoryEnabled(QuantityCategory.SurfaceDistanceMap);
			boolean bDirection = surfaceRegionObject.isQuantityCategoryEnabled(QuantityCategory.DirectionToSurface);
			boolean bSize = surfaceRegionObject.isQuantityCategoryEnabled(QuantityCategory.SurfaceSize);

			if (bVelocity){
				ArrayList<SurfaceKinematics> surfaceKinematicsList = new ArrayList<SurfaceKinematics>();
				for (SpatialProcess spatialProcess : simContext.getSpatialProcesses()){
					if (spatialProcess instanceof SurfaceKinematics && ((SurfaceKinematics) spatialProcess).getSurfaceRegionObject() == surfaceRegionObject){
						surfaceKinematicsList.add((SurfaceKinematics) spatialProcess);
					}
				}
				if (surfaceKinematicsList.size()==1){
					SurfaceKinematics surfaceKinematics = surfaceKinematicsList.get(0);
					if (simContext.getGeometry().getDimension()>=1){
						SpatialQuantity velXQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.SurfaceVelocity,QuantityComponent.X);
						LocalParameter velXParam = surfaceKinematics.getParameter(SpatialProcessParameterType.SurfaceVelocityX);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velXParam, surfaceClass),
								getIdentifierSubstitutions(velXParam.getExpression(), velXParam.getUnitDefinition(), surfaceClass),
								surfaceClass));
						Expression velXExp = new Expression(velXParam,surfaceKinematics.getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velXQuantity, surfaceClass),
								getIdentifierSubstitutions(velXExp,velXQuantity.getUnitDefinition(),surfaceClass),
								surfaceClass));
						if (bNormal){
							SpatialQuantity normXQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.Normal,QuantityComponent.X);
							Expression normXExp = new Expression(MathFunctionDefinitions.FUNCTION_normalX);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(normXQuantity, surfaceClass),
									getIdentifierSubstitutions(normXExp,normXQuantity.getUnitDefinition(),surfaceClass),
									surfaceClass));
						}
					}
					if (simContext.getGeometry().getDimension()>=2){
						SpatialQuantity velYQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.SurfaceVelocity,QuantityComponent.Y);
						LocalParameter velYParam = surfaceKinematics.getParameter(SpatialProcessParameterType.SurfaceVelocityY);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velYParam, surfaceClass),
								getIdentifierSubstitutions(velYParam.getExpression(), velYParam.getUnitDefinition(), surfaceClass),
								surfaceClass));
						Expression velYExp = new Expression(velYParam,surfaceKinematics.getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velYQuantity, surfaceClass),
								getIdentifierSubstitutions(velYExp,velYQuantity.getUnitDefinition(),surfaceClass),
								surfaceClass));
						if (bNormal){
							SpatialQuantity normYQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.Normal,QuantityComponent.Y);
							Expression normYExp = new Expression(MathFunctionDefinitions.FUNCTION_normalY);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(normYQuantity, surfaceClass),
									getIdentifierSubstitutions(normYExp,normYQuantity.getUnitDefinition(),surfaceClass),
									surfaceClass));
						}
					}
					if (simContext.getGeometry().getDimension()>=3){
						SpatialQuantity velZQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.SurfaceVelocity,QuantityComponent.Z);
						LocalParameter velZParam = surfaceKinematics.getParameter(SpatialProcessParameterType.SurfaceVelocityZ);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velZParam, surfaceClass),
								getIdentifierSubstitutions(velZParam.getExpression(), velZParam.getUnitDefinition(), surfaceClass),
								surfaceClass));
						Expression velYExp = new Expression(velZParam,surfaceKinematics.getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velZQuantity, surfaceClass),
								getIdentifierSubstitutions(velYExp,velZQuantity.getUnitDefinition(),surfaceClass),
								surfaceClass));
						if (bNormal){
							SpatialQuantity normZQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.Normal,QuantityComponent.Z);
							Expression normZExp = new Expression("normalZ_not_implemented()"); // MathFunctionDefinitions.FUNCTION_normalZ);
							varHash.addVariable(newFunctionOrConstant(
									getMathSymbol(normZQuantity, surfaceClass),
									getIdentifierSubstitutions(normZExp,normZQuantity.getUnitDefinition(),surfaceClass),
									surfaceClass));
						}
					}
				}else{
					throw new MappingException("expecting 1 Surface Kinematics process for Surface Object '"+surfaceRegionObject.getName()+"'");
				}
			}
			if (bSize){
				SpatialQuantity sizeQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.SurfaceSize,QuantityComponent.Scalar);
				String funcName = MathFunctionDefinitions.Function_regionArea_current.getFunctionName();
				Expression sizeExp = Expression.function(funcName, new Expression[] {new Expression("'"+surfaceClass.getName()+"'")} );
				varHash.addVariable(newFunctionOrConstant(
						getMathSymbol(sizeQuantity, surfaceClass),
						getIdentifierSubstitutions(sizeExp,sizeQuantity.getUnitDefinition(),surfaceClass),
						surfaceClass));
			}
			if (bDirection){
				SpatialQuantity directionXQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.DirectionToSurface,QuantityComponent.X);
				SpatialQuantity directionYQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.DirectionToSurface,QuantityComponent.Y);
				SpatialQuantity directionZQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.DirectionToSurface,QuantityComponent.Z);
				for (SubVolume adjacentSubvolume : surfaceClass.getAdjacentSubvolumes()){
					Domain domain = new Domain(adjacentSubvolume);
					CompartmentSubDomain compSubdomain = mathDesc.getCompartmentSubDomain(adjacentSubvolume.getName());

					if (simContext.getGeometry().getDimension()>=1){
						String name = adjacentSubvolume.getName()+"_dirX_"+memSubdomainContext.membraneSubdomain.getName();
						LocalizedDirectionToMembraneQuantity localDirectionXQuantity = addLocalizedDirectionToMembraneQuantity(name, surfaceClass, adjacentSubvolume, QuantityComponent.X);
						VolVariable distanceVar = new VolVariable(getMathSymbol(localDirectionXQuantity, adjacentSubvolume), domain);
						varHash.addVariable(distanceVar);
						ComputeMembraneMetricEquation membraneMetricEquation = new ComputeMembraneMetricEquation(distanceVar, MembraneMetricComponent.directionToMembraneX);
						membraneMetricEquation.setTargetMembraneName(memSubdomainContext.membraneSubdomain.getName());
						compSubdomain.addEquation(membraneMetricEquation);
					}
					if (simContext.getGeometry().getDimension()>=2){
						String name = adjacentSubvolume.getName()+"_dirY_"+memSubdomainContext.membraneSubdomain.getName();
						LocalizedDirectionToMembraneQuantity localDirectionYQuantity = addLocalizedDirectionToMembraneQuantity(name, surfaceClass, adjacentSubvolume, QuantityComponent.Y);
						VolVariable distanceVar = new VolVariable(getMathSymbol(localDirectionYQuantity, adjacentSubvolume), domain);
						varHash.addVariable(distanceVar);
						ComputeMembraneMetricEquation membraneMetricEquation = new ComputeMembraneMetricEquation(distanceVar, MembraneMetricComponent.directionToMembraneY);
						membraneMetricEquation.setTargetMembraneName(memSubdomainContext.membraneSubdomain.getName());
						compSubdomain.addEquation(membraneMetricEquation);
					}
					if (simContext.getGeometry().getDimension()==3){
						String name = adjacentSubvolume.getName()+"_dirZ_"+memSubdomainContext.membraneSubdomain.getName();
						LocalizedDirectionToMembraneQuantity localDirectionZQuantity = addLocalizedDirectionToMembraneQuantity(name, surfaceClass, adjacentSubvolume, QuantityComponent.Z);
						VolVariable distanceVar = new VolVariable(getMathSymbol(localDirectionZQuantity, adjacentSubvolume), domain);
						varHash.addVariable(distanceVar);
						ComputeMembraneMetricEquation membraneMetricEquation = new ComputeMembraneMetricEquation(distanceVar, MembraneMetricComponent.directionToMembraneZ);
						membraneMetricEquation.setTargetMembraneName(memSubdomainContext.membraneSubdomain.getName());
						compSubdomain.addEquation(membraneMetricEquation);
					}
				}
			}
			if (bDistance){
				SpatialQuantity distanceQuantity = surfaceRegionObject.getSpatialQuantity(QuantityCategory.SurfaceDistanceMap,QuantityComponent.Scalar);
				for (SubVolume adjacentSubvolume : surfaceClass.getAdjacentSubvolumes()){
					String name = adjacentSubvolume.getName()+"_distanceTo_"+memSubdomainContext.membraneSubdomain.getName();
					LocalizedDistanceToMembraneQuantity localDistanceQuantity = addLocalizedDistanceToMembraneQuantity(name, surfaceClass, adjacentSubvolume);
					CompartmentSubDomain compSubdomain = mathDesc.getCompartmentSubDomain(adjacentSubvolume.getName());
					Domain domain = new Domain(adjacentSubvolume);
					VolVariable distanceVar = new VolVariable(getMathSymbol(localDistanceQuantity, adjacentSubvolume), domain);
					varHash.addVariable(distanceVar);
					ComputeMembraneMetricEquation membraneMetricEquation = new ComputeMembraneMetricEquation(distanceVar, MembraneMetricComponent.distanceToMembrane);
					membraneMetricEquation.setTargetMembraneName(memSubdomainContext.membraneSubdomain.getName());
					compSubdomain.addEquation(membraneMetricEquation);
				}
			}
		}else if (spatialObject instanceof VolumeRegionObject){
			VolumeRegionObject volumeRegionObject = (VolumeRegionObject)spatialObject;
			SubVolume subvolume = volumeRegionObject.getSubVolume();

			boolean bCentroid = volumeRegionObject.isQuantityCategoryEnabled(QuantityCategory.DirectionToSurface);
			boolean bSize = volumeRegionObject.isQuantityCategoryEnabled(QuantityCategory.VolumeSize);
			boolean bVelocity = volumeRegionObject.isQuantityCategoryEnabled(QuantityCategory.InteriorVelocity);

			if (bVelocity){
				ArrayList<VolumeKinematics> volumeKinematicsList = new ArrayList<VolumeKinematics>();
				for (SpatialProcess spatialProcess : simContext.getSpatialProcesses()){
					if (spatialProcess instanceof VolumeKinematics && ((VolumeKinematics) spatialProcess).getVolumeRegionObject() == volumeRegionObject){
						volumeKinematicsList.add((VolumeKinematics) spatialProcess);
					}
				}
				if (volumeKinematicsList.size()==1){
					VolumeKinematics volumeKinematics = volumeKinematicsList.get(0);
					if (simContext.getGeometry().getDimension()>=1){
						SpatialQuantity velXQuantity = volumeRegionObject.getSpatialQuantity(QuantityCategory.InteriorVelocity,QuantityComponent.X);
						LocalParameter velXParam = volumeKinematics.getParameter(SpatialProcessParameterType.InternalVelocityX);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velXParam, subvolume),
								getIdentifierSubstitutions(velXParam.getExpression(), velXParam.getUnitDefinition(), subvolume),
								subvolume));
						Expression velXExp = new Expression(velXParam,volumeKinematics.getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velXQuantity, subvolume),
								getIdentifierSubstitutions(velXExp,velXQuantity.getUnitDefinition(),subvolume),
								subvolume));
					}
					if (simContext.getGeometry().getDimension()>=2){
						SpatialQuantity velYQuantity = volumeRegionObject.getSpatialQuantity(QuantityCategory.InteriorVelocity,QuantityComponent.Y);
						LocalParameter velYParam = volumeKinematics.getParameter(SpatialProcessParameterType.InternalVelocityY);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velYParam, subvolume),
								getIdentifierSubstitutions(velYParam.getExpression(), velYParam.getUnitDefinition(), subvolume),
								subvolume));
						Expression velYExp = new Expression(velYParam,volumeKinematics.getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velYQuantity, subvolume),
								getIdentifierSubstitutions(velYExp,velYQuantity.getUnitDefinition(),subvolume),
								subvolume));
					}
					if (simContext.getGeometry().getDimension()==3){
						SpatialQuantity velZQuantity = volumeRegionObject.getSpatialQuantity(QuantityCategory.InteriorVelocity,QuantityComponent.Z);
						LocalParameter velZParam = volumeKinematics.getParameter(SpatialProcessParameterType.InternalVelocityZ);
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velZParam, subvolume),
								getIdentifierSubstitutions(velZParam.getExpression(), velZParam.getUnitDefinition(), subvolume),
								subvolume));
						Expression velZExp = new Expression(velZParam,volumeKinematics.getNameScope());
						varHash.addVariable(newFunctionOrConstant(
								getMathSymbol(velZQuantity, subvolume),
								getIdentifierSubstitutions(velZExp,velZQuantity.getUnitDefinition(),subvolume),
								subvolume));
					}
				}else{
					throw new MappingException("expecting 1 Volume Kinematics process for Volume Object '"+volumeRegionObject.getName()+"'");
				}


			}
			if (bSize){
				SpatialQuantity sizeQuantity = volumeRegionObject.getSpatialQuantity(QuantityCategory.VolumeSize,QuantityComponent.Scalar);
				String funcName = MathFunctionDefinitions.Function_regionVolume_current.getFunctionName();
				Expression sizeExp = Expression.function(funcName, new Expression[] {new Expression("'"+subvolume.getName()+"'")} );
				varHash.addVariable(newFunctionOrConstant(
						getMathSymbol(sizeQuantity, subvolume),
						getIdentifierSubstitutions(sizeExp,sizeQuantity.getUnitDefinition(),subvolume),
						subvolume));
			}
			if (bCentroid){
				throw new MappingException(QuantityCategory.Centroid.description+" not yet implemented in math generation");
			}
		}
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

	SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
	for (int i=0;i<speciesContextSpecs.length;i++){
		SpeciesContextSpec scs = speciesContextSpecs[i];

		SpeciesContextMapping scm = new SpeciesContextMapping(scs.getSpeciesContext());

		scm.setPDERequired(simContext.isPDERequired(scs.getSpeciesContext()));
		scm.setHasEventAssignment(simContext.hasEventAssignment(scs.getSpeciesContext()));
		scm.setHasHybridReaction(false);
		for (ReactionSpec reactionSpec : getSimulationContext().getReactionContext().getReactionSpecs()){
			if (!reactionSpec.isExcluded() && reactionSpec.hasHybrid(getSimulationContext(), scs.getSpeciesContext())){
				scm.setHasHybridReaction(true);
			}
		}
//		scm.setDiffusing(isDiffusionRequired(scs.getSpeciesContext()));
//		scm.setAdvecting(isAdvectionRequired(scs.getSpeciesContext()));
		if (scs.isClamped()){
			Expression clampedExpression = null;
			if (scs.getInitialConditionParameter() != null) {
				clampedExpression = new Expression(scs.getInitialConditionParameter(), getNameScope());
			}
			AssignmentRule assignmentRule = simContext.getAssignmentRule(scs.getSpeciesContext());
			if (assignmentRule != null){
				clampedExpression = new Expression(assignmentRule.getAssignmentRuleExpression());
			}
			scm.setDependencyExpression(clampedExpression);
			////
			//// determine if a Function is necessary
			////
			//boolean bNeedFunction = false;
			//if (initCond.getSymbols()!=null){
			//bNeedFunction = true;
			//}
			//if (bNeedFunction){
			//scm.setVariable(new Function(scm.getSpeciesContext().getName(),initCond));
			//}else{
			//scm.setVariable(new Constant(scm.getSpeciesContext().getName(),initCond));
			//}
		}
		//
		// test if participant in fast reaction step, request elimination if possible
		//
		scm.setFastParticipant(false);
		ReactionSpec reactionSpecs[] = simContext.getReactionContext().getReactionSpecs();
		for (int j=0;j<reactionSpecs.length;j++){
			ReactionSpec reactionSpec = reactionSpecs[j];
			if (reactionSpec.isExcluded()){
				continue;
			}
			ReactionStep rs = reactionSpec.getReactionStep();
			if (rs instanceof SimpleReaction && rs.countNumReactionParticipants(scs.getSpeciesContext()) > 0){
				if (reactionSpec.isFast()){
					scm.setFastParticipant(true);
				}
			}
		}
		speciesContextMappingList.addElement(scm);
	}
}


/**
 * This method was created by a SmartGuide.
 */
private void refreshStructureAnalyzers() {

	structureAnalyzerList.removeAllElements();

	//
	// update structureAnalyzer list if any subVolumes were added
	//
	GeometryClass[] geometryClasses = simContext.getGeometryContext().getGeometry().getGeometryClasses();
	for (int j=0;j<geometryClasses.length;j++){
		if (geometryClasses[j] instanceof SubVolume){
			SubVolume subVolume = (SubVolume)geometryClasses[j];
			if (getVolumeStructureAnalyzer(subVolume)==null){
				structureAnalyzerList.addElement(new VolumeStructureAnalyzer(this,subVolume));
			}
		}else if (geometryClasses[j] instanceof SurfaceClass){
			SurfaceClass surfaceClass = (SurfaceClass)geometryClasses[j];
			if (getMembraneStructureAnalyzer(surfaceClass)==null){
				structureAnalyzerList.addElement(new MembraneStructureAnalyzer(this,surfaceClass));
			}
		}
	}


	//
	// invoke all structuralAnalyzers
	//
	Enumeration<StructureAnalyzer> enum1 = getStructureAnalyzers();
	while (enum1.hasMoreElements()) {
		StructureAnalyzer sa = enum1.nextElement();
		sa.refresh();
	}
}


/**
 * This method was created in VisualAge.
 */
private void refreshVariables() throws MappingException {

//System.out.println("MathMapping.refreshVariables()");

	//
	// non-constant dependent variables require a function
	//
	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = enum1.nextElement();
		SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
		if (scm.getDependencyExpression() != null && !scs.isConstant()){
			//scm.setVariable(new Function(scm.getSpeciesContext().getName(),scm.getDependencyExpression()));
			scm.setVariable(null);
		}
		if (getSimulationContext().hasEventAssignment(scs.getSpeciesContext())){
			scm.setDependencyExpression(null);
		}
	}

	//
	// non-constant independent variables require either a membrane or volume variable
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
		Expression depExp = scm.getDependencyExpression();
		boolean isVariable = !scs.isConstant();
		boolean hasEvent = getSimulationContext().hasEventAssignment(scs.getSpeciesContext());
		if (depExp == null && (isVariable || hasEvent)){
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
			Structure struct = scm.getSpeciesContext().getStructure();
			Domain domain = null;
			if (sm.getGeometryClass()!=null){
				domain = new Domain(sm.getGeometryClass());
			}
			if (struct instanceof Feature || struct instanceof Membrane){
				if (sm.getGeometryClass() instanceof SurfaceClass){
					if (scs.isWellMixed()){
						scm.setVariable(new MembraneRegionVariable(scm.getSpeciesContext().getName(),domain));
					}else if (getSimulationContext().isStoch() && getSimulationContext().getGeometry().getDimension()>0 && !scs.isForceContinuous()){
						scm.setVariable(new MemVariable(scm.getSpeciesContext().getName()+"_conc",domain));
					}else{
						scm.setVariable(new MemVariable(scm.getSpeciesContext().getName(),domain));
					}
				}else{
					if (scs.isWellMixed()){
						scm.setVariable(new VolumeRegionVariable(scm.getSpeciesContext().getName(),domain));
					}else if (getSimulationContext().isStoch() && getSimulationContext().getGeometry().getDimension()>0 && !scs.isForceContinuous()){
						scm.setVariable(new VolVariable(scm.getSpeciesContext().getName()+"_conc",domain));
					}else{
						scm.setVariable(new VolVariable(scm.getSpeciesContext().getName(),domain));
					}
				}
			}else{
				throw new MappingException("class "+scm.getSpeciesContext().getStructure().getClass()+" not supported");
			}
			mathSymbolMapping.put(scm.getSpeciesContext(),scm.getVariable().getName());
		}
	}

}

}
