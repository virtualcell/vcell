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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.vcell.util.BeanUtils;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;
import org.vcell.util.VCellThreadChecker;

import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.data.DataSymbol;
import cbit.vcell.data.FieldDataSymbol;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.geometry.CompartmentSubVolume;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.MicroscopeMeasurement.ConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.ExperimentalPSF;
import cbit.vcell.mapping.MicroscopeMeasurement.GaussianConvolutionKernel;
import cbit.vcell.mapping.MicroscopeMeasurement.ProjectionZKernel;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecProxyParameter;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.mapping.potential.CurrentClampElectricalDevice;
import cbit.vcell.mapping.potential.ElectricalDevice;
import cbit.vcell.mapping.potential.MembraneElectricalDevice;
import cbit.vcell.mapping.potential.PotentialMapping;
import cbit.vcell.mapping.potential.VoltageClampElectricalDevice;
import cbit.vcell.math.CompartmentSubDomain;
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
import cbit.vcell.matrix.RationalExp;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ExpressionContainer;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.VCUnitEvaluator;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitException;
/**
 * The MathMapping class performs the Biological to Mathematical transformation once upon calling getMathDescription().
 * This is not a "live" transformation, so that an updated SimulationContext must be given to a new MathMapping object
 * to get an updated MathDescription.
 * 		capacitances must not be overridden and must be constant (used as literals in KVL)
 */
public class MathMapping implements ScopedSymbolTable, UnitFactorProvider {
	private static final String PARAMETER_VELOCITY_X_SUFFIX = "_velocityX";
	private static final String PARAMETER_VELOCITY_Y_SUFFIX = "_velocityY";
	private static final String PARAMETER_VELOCITY_Z_SUFFIX = "_velocityZ";
	private static final String PARAMETER_BOUNDARY_XM_SUFFIX = "_boundaryXm";
	private static final String PARAMETER_BOUNDARY_XP_SUFFIX = "_boundaryXp";
	private static final String PARAMETER_BOUNDARY_YM_SUFFIX = "_boundaryYm";
	private static final String PARAMETER_BOUNDARY_YP_SUFFIX = "_boundaryYp";
	private static final String PARAMETER_BOUNDARY_ZM_SUFFIX = "_boundaryZm";
	private static final String PARAMETER_BOUNDARY_ZP_SUFFIX = "_boundaryZp";
	private static final String PARAMETER_DIFFUSION_RATE_SUFFIX = "_diffusionRate";
	private static final String PARAMETER_TRANSMEMBRANE_CURRENT_PREFIX = "F_";
	private static final String PARAMETER_VOLTAGE_PREFIX = "V_";
	private static final String PARAMETER_TOTAL_CURRENT_PREFIX = "I_";
	private static final String PARAMETER_SPECIFIC_CAPACITANCE_PREFIX = "C_";
	private static final String PARAMETER_K_FLUX_PREFIX = "KFlux_";
	private static final String PARAMETER_K_UNITFACTOR_PREFIX = "UnitFactor_";

	
//	public static final String PARAMETER_SURF_TO_VOL_PREFIX = "SurfToVol_";
//	public static final String PARAMETER_VOL_FRACT_PREFIX = "VolFract_";
	public static final String PARAMETER_SIZE_FUNCTION_PREFIX = "Size_";
	public static final String PARAMETER_MASS_CONSERVATION_PREFIX = "K_";
	public static final String PARAMETER_MASS_CONSERVATION_SUFFIX = "_total";
//	public static final String PARAMETER_K_FLUX_PREFIX = "KFlux_";
	
	static final String BIO_PARAM_SUFFIX_SPECIES_COUNT = "_temp_Count";
	static final String BIO_PARAM_SUFFIX_SPECIES_CONCENTRATION = "_temp_Conc";
	private static final String MATH_VAR_SUFFIX_SPECIES_COUNT = "";
	private static final String MATH_FUNC_SUFFIX_SPECIES_CONCENTRATION = "_Conc";
	private static final String MATH_FUNC_SUFFIX_EVENTASSIGN_OR_RATERULE_INIT = "_protocol_init";
	private static final String MATH_FUNC_SUFFIX_RATERULE_RATE = "_rate";
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT = "_initCount";
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_old = "_init";
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_old_molecules_per_um2 = "_init_molecules_per_um2";  // initial surf dens - VCell 5.1 and earlier (hard coded name)
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_old_molecules_um_2 = "_init_molecules_um_2";        // initial surf dens - from VCell 5.2 (mangled "molecules.um-2")
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION_old_uM = "_init_uM";
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONC_UNIT_PREFIX = "_init_";
	
	private SimulationContext simContext = null;
	protected MathDescription mathDesc = null;
	private PotentialMapping potentialMapping = null;  // null if don't need it
	protected MathSymbolMapping mathSymbolMapping = new MathSymbolMapping();
	protected Vector<Issue> localIssueList = new Vector<Issue>();
	protected final IssueContext issueContext;

	
	protected MathMapping.MathMappingParameter[] fieldMathMappingParameters = new MathMappingParameter[0];
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private NameScope nameScope = new MathMappingNameScope();

	public static final int PARAMETER_ROLE_TOTALMASS = 0;
	public static final int PARAMETER_ROLE_DEPENDENT_VARIABLE = 1;
	public static final int PARAMETER_ROLE_KFLUX = 2;
	public static final int PARAMETER_ROLE_P = 3;
	public static final int PARAMETER_ROLE_P_reverse = 4;
	public static final int PARAMETER_ROLE_CONCENRATION = 5;
	public static final int PARAMETER_ROLE_COUNT = 6;
	public static final int PARAMETER_ROLE_EVENTASSIGN_OR_RATERULE_INITCONDN = 7;
	public static final int PARAMETER_ROLE_RATERULE_RATE = 8;
	public static final int PARAMETER_ROLE_UNITFACTOR = 9;
	public static final int NUM_PARAMETER_ROLES = 10;
		
	private Vector<StructureAnalyzer> structureAnalyzerList = new Vector<StructureAnalyzer>();
	
	protected Vector<SpeciesContextMapping> speciesContextMappingList = new Vector<SpeciesContextMapping>();
	private HashMap<String, Integer> localNameCountHash = new HashMap<String, Integer>();
	protected static final Logger lg = Logger.getLogger(MathMapping.class);

	@SuppressWarnings("serial")
	public class MathMappingNameScope extends BioNameScope {
		private NameScope nameScopes[] = null;
		public MathMappingNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// return model namescope
			//
			ElectricalDevice electricalDevices[] = (potentialMapping!=null)?(potentialMapping.getElectricalDevices()):(new ElectricalDevice[0]);
			NameScope simContextChildren[] = simContext.getNameScope().getChildren();
			NameScope modelChildren[] = simContext.getModel().getNameScope().getChildren();
			int childCount = simContextChildren.length + modelChildren.length + electricalDevices.length;
			if (nameScopes==null || nameScopes.length != childCount){
				nameScopes = new NameScope[childCount];
				int index = 0;
				for (int i=0;i<electricalDevices.length;i++){
					nameScopes[index++] = electricalDevices[i].getNameScope();
				}
				for (int i=0;i<simContextChildren.length;i++){
					nameScopes[index++] = simContextChildren[i];
				}
				for (int i=0;i<modelChildren.length;i++){
					nameScopes[index++] = modelChildren[i];
				}
			}
			return nameScopes;
		}
		public String getName() {
			return "MathMapping_for_"+TokenMangler.fixTokenStrict(simContext.getName());
		}
		public NameScope getParent() {
			//System.out.println("MathMappingNameScope.getParent() returning null ... no parent");
			return null;
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return MathMapping.this;
		}
		public boolean isPeer(NameScope nameScope){
			if (super.isPeer(nameScope)){
				return true;
			}
			return (nameScope == MathMapping.this.simContext.getNameScope() || nameScope == MathMapping.this.simContext.getModel().getNameScope());
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.mathmappingType;
		}
	}

	@SuppressWarnings("serial")
	public class MathMappingParameter extends Parameter implements ExpressionContainer {
		
		private String fieldParameterName = null;
		private Expression fieldExpression = null;
		private VCUnitDefinition fieldVCUnitDefinition = null;
		private GeometryClass fieldGeometryClass = null;
		private int fieldRole = -1;

		protected MathMappingParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition, GeometryClass geometryClass) {
			if (argName == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argName.length()<1){
				throw new IllegalArgumentException("parameter name is zero length");
			}
			if (argExpression == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argRole<0 || argRole>=NUM_PARAMETER_ROLES){
				throw new IllegalArgumentException("parameter role is invalid");
			}
			this.fieldParameterName = argName;
			this.fieldExpression = argExpression;
			this.fieldRole = argRole;
			this.fieldVCUnitDefinition = argVCUnitDefinition;
			this.fieldGeometryClass = geometryClass;
		}

		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof MathMappingParameter)){
				return false;
			}
			MathMappingParameter mmp = (MathMappingParameter)obj;
			if (!super.compareEqual0(mmp)){
				return false;
			}			
			return true;
		}
		
		public GeometryClass getGeometryClass(){
			return this.fieldGeometryClass;
		}

		public double getConstantValue() throws ExpressionException {
			throw new ExpressionException("no expression defined for MathMappingParameter '"+fieldParameterName+"'");
		}      

		public Expression getExpression() {
			return this.fieldExpression;
		}

		public boolean isExpressionEditable(){
			return true;
		}

		public boolean isUnitEditable(){
			return false;
		}

		public boolean isNameEditable(){
			return true;
		}

		public int getIndex() {
			return -1;
		}

		public VCUnitDefinition getUnitDefinition() {
			return fieldVCUnitDefinition;
		}

		public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException {
			throw new RuntimeException("units are not editable");
		}
		
		public int getRole() {
			return fieldRole;
		}

		public String getName(){ 
			return this.fieldParameterName; 
		}   

		public NameScope getNameScope() {
			return MathMapping.this.getNameScope();
		}

		public void setExpression(Expression argExpression) throws java.beans.PropertyVetoException {
			Expression oldValue = fieldExpression;
			super.fireVetoableChange("expression", oldValue, argExpression);
			fieldExpression = argExpression;
			super.firePropertyChange("expression", oldValue, argExpression);
		}

		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}


		}
	@SuppressWarnings("serial")
	public class KFluxParameter extends MathMappingParameter {

		private StructureMapping sourceStructureMapping = null;
		private StructureMapping targetStructureMapping = null;

		protected KFluxParameter(String argName, Expression argExpression, VCUnitDefinition argVCUnitDefinition, StructureMapping sourceStructureMapping, StructureMapping targetStructureMapping) {
			super(argName,argExpression,PARAMETER_ROLE_KFLUX,argVCUnitDefinition,sourceStructureMapping.getGeometryClass());
			this.sourceStructureMapping = sourceStructureMapping;
			this.targetStructureMapping = targetStructureMapping;
		}

		public StructureMapping getSourceStructureMapping() {
			return sourceStructureMapping;
		}

		public StructureMapping getTargetStructureMapping() {
			return targetStructureMapping;
		}

	}
	@SuppressWarnings("serial")
	public class UnitFactorParameter extends MathMappingParameter {

		protected UnitFactorParameter(String argName, Expression argExpression, VCUnitDefinition argVCUnitDefinition) {
			super(argName,argExpression,PARAMETER_ROLE_KFLUX,argVCUnitDefinition,null);
		}
	}
	
	@SuppressWarnings("serial")
	public class ProbabilityParameter extends MathMappingParameter {
		
		private ReactionSpec fieldReactionSpec = null;

		protected ProbabilityParameter(String argName, Expression argExpression, int Role,VCUnitDefinition argVCUnitDefinition, ReactionSpec argReactionSpec, GeometryClass geometryClass) {
			super(argName,argExpression,Role,argVCUnitDefinition,geometryClass);
			this.fieldReactionSpec = argReactionSpec;
		}

		public ReactionSpec getReactionSpec() {
			return fieldReactionSpec;
		}

	}
	
	@SuppressWarnings("serial")
	public class SpeciesConcentrationParameter extends MathMappingParameter {
		private SpeciesContextSpec speciesContextSpec = null;
		
		protected SpeciesConcentrationParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition, SpeciesContextSpec argscSpec, GeometryClass geometryClass) {
			super(argName,argExpression,argRole,argVCUnitDefinition,geometryClass);
			this.speciesContextSpec = argscSpec;
		}

		public SpeciesContextSpec getSpeciesContextSpec() {
			return speciesContextSpec;
		}
	}
	
	@SuppressWarnings("serial")
	public class SpeciesCountParameter extends MathMappingParameter {
		private SpeciesContextSpec speciesContextSpec = null;
		
		protected SpeciesCountParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition, SpeciesContextSpec argscSpec, GeometryClass geometryClass) {
			super(argName,argExpression,argRole,argVCUnitDefinition, geometryClass);
			this.speciesContextSpec = argscSpec;
		}

		public SpeciesContextSpec getSpeciesContextSpec() {
			return speciesContextSpec;
		}
	}

	@SuppressWarnings("serial")
	public class EventAssignmentOrRateRuleInitParameter extends MathMappingParameter {
		protected EventAssignmentOrRateRuleInitParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
			super(argName,argExpression,argRole,argVCUnitDefinition,null);
		}
	}

	@SuppressWarnings("serial")
	public class RateRuleRateParameter extends MathMappingParameter {
		protected RateRuleRateParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
			super(argName,argExpression,argRole,argVCUnitDefinition,null);
		}
	}

/**
 * This method was created in VisualAge.
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 */
protected MathMapping(SimulationContext simContext) {
	this.simContext = simContext;
	this.issueContext = new IssueContext(ContextType.SimContext,simContext,null).newChildContext(ContextType.MathMapping,this);
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 12:44:00 AM)
 * @return cbit.vcell.mapping.MathMapping.MathMappingParameter
 * @param name java.lang.String
 * @param expression cbit.vcell.parser.Expression
 * @param role int
 */
MathMapping.MathMappingParameter addMathMappingParameter(String name, Expression expression, int role, VCUnitDefinition unitDefinition, GeometryClass geometryClass) throws java.beans.PropertyVetoException, ExpressionBindingException {

	MathMapping.MathMappingParameter newParameter = new MathMapping.MathMappingParameter(name,expression,role,unitDefinition, geometryClass);
	MathMapping.MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addMathMappingParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addMathMappingParameter found duplicate parameter for name "+name);
		}
		return previousParameter;
	}
	expression.bindExpression(this);
	MathMapping.MathMappingParameter newParameters[] = (MathMapping.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 12:44:00 AM)
 * @return cbit.vcell.mapping.MathMapping.MathMappingParameter
 * @param name java.lang.String
 * @param expression cbit.vcell.parser.Expression
 * @param role int
 */
MathMapping.ProbabilityParameter addProbabilityParameter(String name, Expression expression, int role, VCUnitDefinition unitDefinition,ReactionSpec argReactionSpec) throws java.beans.PropertyVetoException, ExpressionBindingException {

	GeometryClass geometryClass = null;
	if (argReactionSpec.getReactionStep().getStructure()!=null){
		geometryClass = simContext.getGeometryContext().getStructureMapping(argReactionSpec.getReactionStep().getStructure()).getGeometryClass();
	}
	MathMapping.ProbabilityParameter newParameter = new MathMapping.ProbabilityParameter(name,expression,role,unitDefinition,argReactionSpec,geometryClass);
	MathMapping.MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addProbabilityParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addProbabilityParameter found duplicate parameter for name "+name);
		}
		return (MathMapping.ProbabilityParameter)previousParameter;
	}
	//expression.bindExpression(this);
	MathMapping.MathMappingParameter newParameters[] = (MathMapping.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

MathMapping.SpeciesConcentrationParameter addSpeciesConcentrationParameter(String name, Expression expr, int role, VCUnitDefinition unitDefn,SpeciesContextSpec argscSpec) throws PropertyVetoException {

	GeometryClass geometryClass = simContext.getGeometryContext().getStructureMapping(argscSpec.getSpeciesContext().getStructure()).getGeometryClass();
	MathMapping.SpeciesConcentrationParameter newParameter = new MathMapping.SpeciesConcentrationParameter(name,expr,role,unitDefn,argscSpec,geometryClass);
	MathMapping.MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addConcentrationParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addConcentrationParameter found duplicate parameter for name '"+name+"'.");
		}
		return (MathMapping.SpeciesConcentrationParameter)previousParameter;
	}
	//expression.bindExpression(this);
	MathMapping.MathMappingParameter newParameters[] = (MathMapping.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

MathMapping.SpeciesCountParameter addSpeciesCountParameter(String name, Expression expr, int role, VCUnitDefinition unitDefn,SpeciesContextSpec argscSpec) throws PropertyVetoException {

	GeometryClass geometryClass = simContext.getGeometryContext().getStructureMapping(argscSpec.getSpeciesContext().getStructure()).getGeometryClass();
	MathMapping.SpeciesCountParameter newParameter = new MathMapping.SpeciesCountParameter(name,expr,role,unitDefn,argscSpec,geometryClass);
	MathMapping.MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addCountParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addCountParameter found duplicate parameter for name '"+name+"'.");
		}
		return (MathMapping.SpeciesCountParameter)previousParameter;
	}
	//expression.bindExpression(this);
	MathMapping.MathMappingParameter newParameters[] = (MathMapping.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

MathMapping.EventAssignmentOrRateRuleInitParameter addEventAssignmentOrRateRuleInitParameter(SymbolTableEntry targetName, Expression expr, int role, VCUnitDefinition unitDefn) throws PropertyVetoException {

	String argName = targetName.getName() + MATH_FUNC_SUFFIX_EVENTASSIGN_OR_RATERULE_INIT;
	MathMapping.EventAssignmentOrRateRuleInitParameter newParameter = new MathMapping.EventAssignmentOrRateRuleInitParameter(argName,expr,role,unitDefn);
	MathMapping.MathMappingParameter previousParameter = getMathMappingParameter(argName);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addEventAssignInitParameter found duplicate parameter for name "+targetName.getName());
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addEventAssignInitParameter found duplicate parameter for name '"+targetName.getName()+"'.");
		}
		return (MathMapping.EventAssignmentOrRateRuleInitParameter)previousParameter;
	}
	MathMapping.MathMappingParameter newParameters[] = (MathMapping.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

MathMapping.RateRuleRateParameter addRateRuleRateParameter(SymbolTableEntry target, Expression expr, int role, VCUnitDefinition unitDefn) throws PropertyVetoException {

	String argName = target.getName()+ MATH_FUNC_SUFFIX_RATERULE_RATE;
	MathMapping.RateRuleRateParameter newParameter = new MathMapping.RateRuleRateParameter(argName,expr,role,unitDefn);
	MathMapping.MathMappingParameter previousParameter = getMathMappingParameter(argName);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addRateRuleRateParameter found duplicate parameter for name "+argName);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addRateRuleRateParameter found duplicate parameter for name '"+argName+"'.");
		}
		return (MathMapping.RateRuleRateParameter)previousParameter;
	}
	MathMapping.MathMappingParameter newParameters[] = (MathMapping.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

SpeciesCountParameter getSpeciesCountParameter(SpeciesContext sc) {
	MathMappingParameter[] mmParams = getMathMappingParameters();
	for (int i = 0; i < mmParams.length; i++) {
		if ( (mmParams[i] instanceof SpeciesCountParameter) && (((SpeciesCountParameter)mmParams[i]).getSpeciesContextSpec().getSpeciesContext() == sc)) {
			return (SpeciesCountParameter)mmParams[i];
		}
	}
	return null;
}

SpeciesConcentrationParameter getSpeciesConcentrationParameter(SpeciesContext sc) {
	MathMappingParameter[] mmParams = getMathMappingParameters();
	for (int i = 0; i < mmParams.length; i++) {
		if ( (mmParams[i] instanceof SpeciesConcentrationParameter) && (((SpeciesConcentrationParameter)mmParams[i]).getSpeciesContextSpec().getSpeciesContext() == sc)) {
			return (SpeciesConcentrationParameter)mmParams[i];
		}
	}
	return null;
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}

/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}

/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}

/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}

/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(java.lang.String identifierString) {
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
	ste = getNameScope().getExternalEntry(identifierString,this);
	if (ste==null){
		System.out.println("MathMapping is unable to bind identifier '"+identifierString+"'");
	}
	return ste;
}


/**
 * This method was created in VisualAge.
 * @return Expression
 */
public MathMapping.KFluxParameter getFluxCorrectionParameter(StructureMapping sourceStructureMapping, StructureMapping targetStructureMapping) throws MappingException, ExpressionException {
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		if (fieldMathMappingParameters[i] instanceof KFluxParameter){
			MathMapping.KFluxParameter kfluxParameter = (MathMapping.KFluxParameter)fieldMathMappingParameters[i];
			if (kfluxParameter.getSourceStructureMapping() == sourceStructureMapping &&
				kfluxParameter.getTargetStructureMapping() == targetStructureMapping){
				return kfluxParameter;
			}
		}
	}
	//
	// not found, add new parameter
	//
	String sourceName = sourceStructureMapping.getStructure().getNameScope().getName();
	String targetName = targetStructureMapping.getStructure().getNameScope().getName();
	Parameter sourceSizeParameter = null;
	Parameter targetSizeParameter = null;
	if (sourceStructureMapping.getGeometryClass() instanceof CompartmentSubVolume){
		sourceSizeParameter = sourceStructureMapping.getSizeParameter();
	}else{
		sourceSizeParameter = sourceStructureMapping.getUnitSizeParameter();
	}
	if (targetStructureMapping.getGeometryClass() instanceof CompartmentSubVolume){
		targetSizeParameter = targetStructureMapping.getSizeParameter();
		if (targetSizeParameter==null || targetSizeParameter.getExpression()==null){
			throw new MappingException("structure mapping sizes not set for application "+simContext.getName());
		}
	}else{
		targetSizeParameter = targetStructureMapping.getUnitSizeParameter();
	}
	Expression fluxCorrectionExp = Expression.div(new Expression(sourceSizeParameter,simContext.getNameScope()),
								                  new Expression(targetSizeParameter,simContext.getNameScope()));
	VCUnitDefinition sourceSizeUnit = sourceSizeParameter.getUnitDefinition();
	VCUnitDefinition targetSizeUnit = targetSizeParameter.getUnitDefinition();
	VCUnitDefinition unit = sourceSizeUnit.divideBy(targetSizeUnit);
	fluxCorrectionExp.bindExpression(this);
	String parameterName = PARAMETER_K_FLUX_PREFIX+sourceName+"_"+targetName;
	KFluxParameter kFluxParameter = new KFluxParameter(parameterName,fluxCorrectionExp, unit, sourceStructureMapping,targetStructureMapping);
	MathMappingParameter[] newMathMappingParameters = (MathMappingParameter[])BeanUtils.addElement(this.fieldMathMappingParameters,kFluxParameter);
	try {
		setMathMapppingParameters(newMathMappingParameters);
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	return kFluxParameter;
}

/**
 * This method was created in VisualAge.
 * @return Expression
 */
public Expression getUnitFactor(VCUnitDefinition unitFactor) {
	if (unitFactor.isEquivalent(getSimulationContext().getModel().getUnitSystem().getInstance_DIMENSIONLESS())){
		return new Expression(1.0);
	}
	for (MathMappingParameter p : fieldMathMappingParameters){
		if (p instanceof UnitFactorParameter && p.getUnitDefinition().isEquivalent(unitFactor)){
			return new Expression(p,getNameScope());
		}
	}
	
	RationalNumber factor = unitFactor.getDimensionlessScale();
	String name = PARAMETER_K_UNITFACTOR_PREFIX + TokenMangler.fixTokenStrict(unitFactor.getSymbol().replace("-","_neg_"));
	UnitFactorParameter unitFactorParameter = new UnitFactorParameter(name, new Expression(factor), unitFactor);
	MathMappingParameter[] newMathMappingParameters = (MathMappingParameter[])BeanUtils.addElement(this.fieldMathMappingParameters,unitFactorParameter);
	try {
		setMathMapppingParameters(newMathMappingParameters);
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	return new Expression(unitFactorParameter,getNameScope());
}

/**
 * This method was created in VisualAge.
 * @return Expression
 */
public RationalExp getUnitFactorAsRationalExp(VCUnitDefinition unitFactor) {
	if (unitFactor.isEquivalent(getSimulationContext().getModel().getUnitSystem().getInstance_DIMENSIONLESS())){
		return RationalExp.ONE;
	}
	for (MathMappingParameter p : fieldMathMappingParameters){
		if (p instanceof UnitFactorParameter && p.getUnitDefinition().isEquivalent(unitFactor)){
			return new RationalExp(p.getName());
		}
	}
	
	RationalNumber factor = unitFactor.getDimensionlessScale();
	String name = PARAMETER_K_UNITFACTOR_PREFIX + TokenMangler.fixTokenStrict(unitFactor.getSymbol().replace("-","_neg_"));
	UnitFactorParameter unitFactorParameter = new UnitFactorParameter(name, new Expression(factor), unitFactor);
	MathMappingParameter[] newMathMappingParameters = (MathMappingParameter[])BeanUtils.addElement(this.fieldMathMappingParameters,unitFactorParameter);
	try {
		setMathMapppingParameters(newMathMappingParameters);
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	return new RationalExp(factor);
}

/**
 * Substitutes appropriate variables for speciesContext bindings
 *
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param structureMapping cbit.vcell.mapping.StructureMapping
 */
protected Expression getIdentifierSubstitutions(Expression origExp, VCUnitDefinition desiredExpUnitDef, GeometryClass geometryClass) throws ExpressionException, MappingException {
	String symbols[] = origExp.getSymbols();
	if (symbols == null){
		return origExp;
	}
	VCUnitDefinition expUnitDef = null;
	try {
		VCUnitEvaluator unitEvaluator = new VCUnitEvaluator(simContext.getModel().getUnitSystem());
		expUnitDef = unitEvaluator.getUnitDefinition(origExp);
		if (desiredExpUnitDef == null){
			String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
			System.out.println("...........exp='"+expStr+"', desiredUnits are null");
			localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=[null], observed=["+expUnitDef.getSymbol()+"]",Issue.SEVERITY_WARNING));
		}else if (expUnitDef == null){
			String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
			System.out.println("...........exp='"+expStr+"', evaluated Units are null");
			localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+desiredExpUnitDef.getSymbol()+"], observed=[null]",Issue.SEVERITY_WARNING));
		}else if (desiredExpUnitDef.isTBD()){
			String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
			System.out.println("...........exp='"+expStr+"', desiredUnits are ["+desiredExpUnitDef.getSymbol()+"] and expression units are ["+expUnitDef.getSymbol()+"]");
			localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+desiredExpUnitDef.getSymbol()+"], observed=["+expUnitDef.getSymbol()+"] for exp = "+expStr,Issue.SEVERITY_WARNING));
		}else if (!desiredExpUnitDef.isEquivalent(expUnitDef) && !expUnitDef.isTBD()){
			String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
			System.out.println("...........exp='"+expStr+"', desiredUnits are ["+desiredExpUnitDef.getSymbol()+"] and expression units are ["+expUnitDef.getSymbol()+"]");
			localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+desiredExpUnitDef.getSymbol()+"], observed=["+expUnitDef.getSymbol()+"] for exp = "+expStr,Issue.SEVERITY_WARNING));
		}
	}catch (VCUnitException e){
		String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
		System.out.println(".........exp='"+expStr+"' exception='"+e.getMessage()+"'");
		localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+((desiredExpUnitDef!=null)?(desiredExpUnitDef.getSymbol()):("null"))+"], exception="+e.getMessage(),Issue.SEVERITY_WARNING));
	}catch (ExpressionException e){
		String expStr = origExp.renameBoundSymbols(getNameScope()).infix();
		System.out.println(".........exp='"+expStr+"' exception='"+e.getMessage()+"'");
		localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+((desiredExpUnitDef!=null)?(desiredExpUnitDef.getSymbol()):("null"))+"], exception="+e.getMessage(),Issue.SEVERITY_WARNING));
	}catch (Exception e){
		e.printStackTrace(System.out);
		localIssueList.add(new Issue(origExp, issueContext, IssueCategory.Units,"expected=["+((desiredExpUnitDef!=null)?(desiredExpUnitDef.getSymbol()):("null"))+"], exception="+e.getMessage(),Issue.SEVERITY_WARNING));
	}
	Expression newExp = new Expression(origExp);
	for (int i=0;i<symbols.length;i++){
		SymbolTableEntry ste = origExp.getSymbolBinding(symbols[i]);
		
		if (ste == null){
			throw new ExpressionBindingException("symbol '"+symbols[i]+"' not bound");
			//ste = simContext.getGeometryContext().getModel().getSpeciesContext(symbols[i]);
		}
		
		if (ste != null){
			String newName = getMathSymbol(ste,geometryClass);
			if (!newName.equals(symbols[i])){
				newExp.substituteInPlace(new Expression(symbols[i]),new Expression(newName));
			}
		}
	}
	return newExp;
}


/* dead 5/2/14 gcw
private static Expression getRelativeSizeExpression(SimulationContext simulationContext, StructureMapping surfaceStructureMapping, StructureMapping volumeStructureMapping) throws ExpressionException {
	if (surfaceStructureMapping == volumeStructureMapping){
		return new Expression(1.0);
	}
	Expression surfSizeExp = null;
	Expression volSizeExp = null;
	if (surfaceStructureMapping.getGeometryClass() instanceof CompartmentSubVolume){
		volSizeExp = new Expression(volumeStructureMapping.getSizeParameter(), simulationContext.getNameScope());
		surfSizeExp = new Expression(surfaceStructureMapping.getSizeParameter(), simulationContext.getNameScope());
		} else {
		volSizeExp = new Expression(volumeStructureMapping.getUnitSizeParameter(), simulationContext.getNameScope());
		surfSizeExp = new Expression(surfaceStructureMapping.getUnitSizeParameter(), simulationContext.getNameScope());
			}
	Expression exp = Expression.mult(surfSizeExp,Expression.invert(volSizeExp));
	exp = exp.flatten();
	//	exp.bindExpression(simulationContext);
	return exp;
}
*/


/**
 * Insert the method's description here.
 * Creation date: (11/8/2004 3:36:31 PM)
 * @return cbit.util.Issue
 */
public Issue[] getIssues() {	
	Vector<Issue> issueList = new Vector<Issue>();
	getSimulationContext().gatherIssues(issueContext,issueList);
	getSimulationContext().getModel().gatherIssues(issueContext,issueList);
	issueList.addAll(localIssueList);
	return (Issue[])BeanUtils.getArray(issueList,Issue.class);
}


/**
 * Insert the method's description here.
 * Creation date: (4/4/2004 1:01:22 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getLocalEntry(java.lang.String identifier) {

	//
	// the MathMapping "nameScope" is the union of the Model and SimContext namescopes (with the addition of any locally defined parameters)
	//

	
	//
	// try "truely" local first
	//
	SymbolTableEntry localSTE = getMathMappingParameter(identifier);

	//
	// try "model" next
	//
	SymbolTableEntry modelSTE = simContext.getModel().getLocalEntry(identifier);
	
	//
	// try "simContext" next
	//
	SymbolTableEntry simContextSTE = simContext.getLocalEntry(identifier);

	int resolutionCount = 0;
	SymbolTableEntry ste = null;
	if (localSTE!=null){ 
		resolutionCount++;
		ste = localSTE;
	}
	if (modelSTE!=null){
		resolutionCount++;
		ste = modelSTE;
	}
	if (simContextSTE!=null){
		resolutionCount++;
		ste = simContextSTE;
	}

	if (resolutionCount==0 || resolutionCount==1){
		return ste;
	}else if (resolutionCount == 2){
		if (localSTE!=null){
			if (modelSTE!=null){
				// local and model
				throw new RuntimeException("identifier '"+identifier+"' ambiguous, resolved by MathMapping ("+localSTE+") and Model ("+modelSTE+")");
			}else{
				// local and simContext
				throw new RuntimeException("identifier '"+identifier+"' ambiguous, resolved by MathMapping ("+localSTE+") and Application ("+simContextSTE+")");
			}
		}else{
			// model and simContext
			if (!modelSTE.equals(simContextSTE)) {
				throw new RuntimeException("identifier '"+identifier+"' ambiguous, resolved by Model ("+modelSTE+") and Application ("+simContextSTE+")");
			} else {
				return ste;
			}
		}
	}else if (resolutionCount == 3){
		// local and model and simContext
		throw new RuntimeException("identifier '"+identifier+"' ambiguous, resolved by MathMapping ("+localSTE+") and Model ("+modelSTE+") and Application ("+simContextSTE+")");
	}	

	return null;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.MathDescription
 */
public MathDescription getMathDescription() throws MappingException, MathException, MatrixException, ExpressionException, ModelException {
	if (mathDesc==null){
		refresh();
	}
	return mathDesc;
}


/**
 * Gets the mathMappingParameters index property (cbit.vcell.mapping.MathMappingParameter) value.
 * @return The mathMappingParameters property value.
 * @param index The index value into the property array.
 */
public MathMappingParameter getMathMappingParameter(String argName) {
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		if (fieldMathMappingParameters[i].getName().equals(argName)){
			return fieldMathMappingParameters[i];
		}
	}
	return null;
}


/**
 * Gets the mathMappingParameters property (MathMappingParameter[]) value.
 * @return The mathMappingParameters property value.
 */
public MathMappingParameter[] getMathMappingParameters() {
	return fieldMathMappingParameters;
}


/**
 * Gets the mathMappingParameters index property (cbit.vcell.mapping.MathMappingParameter) value.
 * @return The mathMappingParameters property value.
 * @param index The index value into the property array.
 */
public MathMappingParameter getMathMappingParameters(int index) {
	return getMathMappingParameters()[index];
}


/**
 * Substitutes appropriate variables for speciesContext bindings
 *
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param structureMapping cbit.vcell.mapping.StructureMapping
 */
public String getMathSymbol(SymbolTableEntry ste, GeometryClass geometryClass) throws MappingException {

	String mathSymbol = getMathSymbol0(ste,geometryClass);
	
	mathSymbolMapping.put(ste,mathSymbol);

	return mathSymbol;
}


protected void refreshLocalNameCount() {
	localNameCountHash.clear();
	ReactionStep reactionSteps[] = simContext.getModel().getReactionSteps();
	for (int j = 0; j < reactionSteps.length; j++){
		KineticsParameter[] params = reactionSteps[j].getKinetics().getKineticsParameters(); 
		for (KineticsParameter kp : params){
			String name = kp.getName();
			if (localNameCountHash.containsKey(name)) {
				localNameCountHash.put(name, localNameCountHash.get(name) + 1);
			} else {
				localNameCountHash.put(name, 1);
			}
		}
	}
	SpeciesContext scs[] = simContext.getModel().getSpeciesContexts();
	for (SpeciesContext sc : scs) {
		String name = sc.getName();
		if (localNameCountHash.containsKey(name)) {
			localNameCountHash.put(name, localNameCountHash.get(name) + 1);
		} else {
			localNameCountHash.put(name, 1);
		}
	}
	ModelParameter mps[] = simContext.getModel().getModelParameters();
	for (ModelParameter mp : mps) {
		String name = mp.getName();
		if (localNameCountHash.containsKey(name)) {
			localNameCountHash.put(name, localNameCountHash.get(name) + 1);
		} else {
			localNameCountHash.put(name, 1);
		}
	}
}
/**
 * Substitutes appropriate variables for speciesContext bindings
 *
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param structureMapping cbit.vcell.mapping.StructureMapping
 */
protected String getMathSymbol0(SymbolTableEntry ste, GeometryClass geometryClass) throws MappingException {
	String steName = ste.getName();
	if (ste instanceof Kinetics.KineticsParameter){
		Integer count = localNameCountHash.get(steName);
		if (count == null) {
			throw new MappingException("KineticsParameter " + steName + " not found in local name count");
		}
		if (count>1 || steName.equals("J")){
			return steName+"_"+ste.getNameScope().getName();
			//return getNameScope().getSymbolName(ste);
		}else{
			return steName;
		}
	}
	if (ste instanceof MathMapping.ProbabilityParameter){ //be careful here, to see if we need mangle the reaction name
		MathMapping.ProbabilityParameter probParm = (MathMapping.ProbabilityParameter)ste;
		return probParm.getName();
	}
	if (ste instanceof MathMapping.SpeciesConcentrationParameter){
		MathMapping.SpeciesConcentrationParameter concParm = (MathMapping.SpeciesConcentrationParameter)ste;
		return concParm.getSpeciesContextSpec().getSpeciesContext().getName() + MATH_FUNC_SUFFIX_SPECIES_CONCENTRATION;
	}
	if (ste instanceof MathMapping.SpeciesCountParameter){
		MathMapping.SpeciesCountParameter countParm = (MathMapping.SpeciesCountParameter)ste;
		return countParm.getSpeciesContextSpec().getSpeciesContext().getName() + MATH_VAR_SUFFIX_SPECIES_COUNT;
	}
	if (ste instanceof MathMapping.EventAssignmentOrRateRuleInitParameter){
		MathMapping.EventAssignmentOrRateRuleInitParameter eventInitParm = (MathMapping.EventAssignmentOrRateRuleInitParameter)ste;
		return eventInitParm.getName(); // + MATH_FUNC_SUFFIX_EVENTASSIGN_OR_RATE_INIT;
	}

	if (ste instanceof MathMapping.RateRuleRateParameter){
		MathMapping.RateRuleRateParameter rateRuleRateParm = (MathMapping.RateRuleRateParameter)ste;
		return rateRuleRateParm.getName(); // + MATH_FUNC_SUFFIX_RATERULE_RATE;
	}

	if (ste instanceof Model.ReservedSymbol){
		return steName;
	}
	if (ste instanceof Membrane.MembraneVoltage){
		return steName;
	}
	if (ste instanceof Structure.StructureSize){
		Structure structure = ((Structure.StructureSize)ste).getStructure();
		StructureMapping.StructureMappingParameter sizeParameter = simContext.getGeometryContext().getStructureMapping(structure).getSizeParameter();
		return getMathSymbol(sizeParameter,geometryClass);
	}
	if (ste instanceof ProxyParameter){
		ProxyParameter pp = (ProxyParameter)ste;
		return getMathSymbol0(pp.getTarget(),geometryClass);
	}
	// 
	if (ste instanceof ModelParameter) {
		ModelParameter mp = (ModelParameter)ste;
		return mp.getName();
	}
	if (ste instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
		SpeciesContextSpec.SpeciesContextSpecParameter scsParm = (SpeciesContextSpec.SpeciesContextSpecParameter)ste;
		SpeciesContext speciesContext = ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext();
		SpeciesContextMapping scm = getSpeciesContextMapping(speciesContext);
		String speciesContextVarName = null;
		if (scm.getVariable()!=null){
			speciesContextVarName = scm.getVariable().getName();
		}else{
			speciesContextVarName = speciesContext.getName();
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_InitialConcentration){
			return speciesContextVarName+ MATH_FUNC_SUFFIX_SPECIES_INIT_CONC_UNIT_PREFIX + TokenMangler.fixTokenStrict(scsParm.getUnitDefinition().getSymbol());
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_InitialCount){
			return speciesContextVarName+ MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_DiffusionRate){
			return speciesContextVarName+PARAMETER_DIFFUSION_RATE_SUFFIX;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueXm){
			return speciesContextVarName+PARAMETER_BOUNDARY_XM_SUFFIX;
	}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueXp){
			return speciesContextVarName+PARAMETER_BOUNDARY_XP_SUFFIX;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueYm){
			return speciesContextVarName+PARAMETER_BOUNDARY_YM_SUFFIX;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueYp){
			return speciesContextVarName+PARAMETER_BOUNDARY_YP_SUFFIX;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueZm){
			return speciesContextVarName+PARAMETER_BOUNDARY_ZM_SUFFIX;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueZp){
			return speciesContextVarName+PARAMETER_BOUNDARY_ZP_SUFFIX;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_VelocityX){
			return speciesContextVarName+PARAMETER_VELOCITY_X_SUFFIX;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_VelocityY){
			return speciesContextVarName+PARAMETER_VELOCITY_Y_SUFFIX;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_VelocityZ){
			return speciesContextVarName+PARAMETER_VELOCITY_Z_SUFFIX;
		}
	}
	if (ste instanceof ElectricalDevice.ElectricalDeviceParameter){
		ElectricalDevice.ElectricalDeviceParameter edParm = (ElectricalDevice.ElectricalDeviceParameter)ste;
		ElectricalDevice electricalDevice = (ElectricalDevice)edParm.getNameScope().getScopedSymbolTable();
		if (electricalDevice instanceof MembraneElectricalDevice){
			String nameWithScope = ((MembraneElectricalDevice)electricalDevice).getMembraneMapping().getMembrane().getNameScope().getName();
			if (edParm.getRole()==ElectricalDevice.ROLE_TotalCurrent){
				return PARAMETER_TOTAL_CURRENT_PREFIX+nameWithScope;
			}
			if (edParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrent){
				return PARAMETER_TRANSMEMBRANE_CURRENT_PREFIX+nameWithScope;
			}
		//}else if (electricalDevice instanceof CurrentClampElectricalDevice) {
			//if (edParm.getRole()==ElectricalDevice.ROLE_TotalCurrentDensity){
				//return "I_"+((CurrentClampElectricalDevice)electricalDevice).getCurrentClampStimulus().getNameScope().getName();
			//}
			//if (edParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrentDensity){
				//return "F_"+((CurrentClampElectricalDevice)electricalDevice).getCurrentClampStimulus().getNameScope().getName();
			//}
		//}else if (electricalDevice instanceof VoltageClampElectricalDevice) {
			//if (edParm.getRole()==ElectricalDevice.ROLE_TotalCurrentDensity){
				//return "I_"+((VoltageClampElectricalDevice)electricalDevice).getVoltageClampStimulus().getNameScope().getName();
			//}
			//if (edParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrentDensity){
				//return "F_"+((VoltageClampElectricalDevice)electricalDevice).getVoltageClampStimulus().getNameScope().getName();
			//}
		}
	}
	if (ste instanceof LocalParameter && ((LocalParameter)ste).getNameScope() instanceof ElectricalStimulus.ElectricalStimulusNameScope){
		LocalParameter esParm = (LocalParameter)ste;
		String nameWithScope = esParm.getNameScope().getName();
		if (esParm.getRole()==ElectricalStimulus.ROLE_TotalCurrent){
			return PARAMETER_TOTAL_CURRENT_PREFIX+nameWithScope;
		} else if (esParm.getRole()==ElectricalStimulus.ROLE_Voltage){
			return PARAMETER_VOLTAGE_PREFIX+nameWithScope;
		}
	}
	if (ste instanceof StructureMapping.StructureMappingParameter){
		StructureMapping.StructureMappingParameter smParm = (StructureMapping.StructureMappingParameter)ste;
		Structure structure = ((StructureMapping)(smParm.getNameScope().getScopedSymbolTable())).getStructure();
		String nameWithScope = structure.getNameScope().getName();
		int role = smParm.getRole();
		if (role==StructureMapping.ROLE_InitialVoltage){
			return smParm.getName();
		} else if (role==StructureMapping.ROLE_SpecificCapacitance){
			return PARAMETER_SPECIFIC_CAPACITANCE_PREFIX+nameWithScope;
		} else if (role==StructureMapping.ROLE_Size){
			if (simContext.getGeometry().getDimension() == 0) {
				// if geometry is compartmental, make sure compartment sizes are set if referenced in model.
				if (smParm.getExpression() == null || smParm.getExpression().isZero()) {
					throw new MappingException("\nIn non-spatial application '" + getSimulationContext().getName() + "', " +
							"size of structure '" + structure.getName() + "' must be assigned a " +
							"positive value if referenced in the model.\n\nPlease go to 'Structure Mapping' tab to check the size.");
				}
			}
			return PARAMETER_SIZE_FUNCTION_PREFIX+nameWithScope;
		} else if (role==StructureMapping.ROLE_AreaPerUnitArea){
			return "AreaPerUnitArea_"+nameWithScope;
		} else if (role==StructureMapping.ROLE_AreaPerUnitVolume){
			return "AreaPerUnitVolume_"+nameWithScope;
		} else if (role==StructureMapping.ROLE_VolumePerUnitArea){
			return "VolumePerUnitArea_"+nameWithScope;
		} else if (role==StructureMapping.ROLE_VolumePerUnitVolume){
			return "VolumePerUnitVolume_"+nameWithScope;
		}
	}
	//
	// substitute Variable or Function if a SpeciesContext
	//
	if (ste instanceof SpeciesContext){
		SpeciesContext sc = (SpeciesContext)ste;
		SpeciesContextMapping scm = getSpeciesContextMapping(sc);
		if (scm == null) {
			throw new RuntimeException("Species '" + sc.getName() + "' is referenced in model but may have been deleted. " +
					"Find its references in '" + GuiConstants.DOCUMENT_EDITOR_FOLDERNAME_BIOMODEL_PARAMETERS + "'.");
		}		
		//
		// for reactions mapped to a subvolume
		//
		if (geometryClass instanceof SubVolume){
			//
			// for any SpeciesContext, replace Symbol name with Variable name
			//				
			if (scm.getVariable()!=null && !scm.getVariable().getName().equals(steName)){
				return scm.getVariable().getName();
			}
		//
		// for reactions within a surface, may need "_INSIDE" or "_OUTSIDE" for jump condition
		//
		}else if (geometryClass instanceof SurfaceClass){
			//
			// if the speciesContext is also within the surface, replace SpeciesContext name with Variable name
			//
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(sc.getStructure());
			if (sm.getGeometryClass() == geometryClass){
				if (scm.getVariable()!=null && !(scm.getVariable().getName().equals(ste.getName()))){
					return scm.getVariable().getName();
				}
			//
			// if the speciesContext is "inside" or "outside" the membrane
			//
			} else if (sm.getGeometryClass() instanceof SubVolume && ((SurfaceClass)geometryClass).isAdjacentTo((SubVolume)sm.getGeometryClass())) {
				SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
				if (!scs.isConstant()){
					if (!scs.isDiffusing() && !scs.isWellMixed()){
						throw new MappingException("Enable diffusion in Application '" + simContext.getName() 
								+  "'. This must be done for any species (e.g '" + sc.getName() + "') in flux reactions.\n\n" 
								+ "To save or run simulations, set the diffusion rate to a non-zero " +
										"value in Initial Conditions or disable those reactions in Specifications->Reactions.");
					}
				}
				if (scm.getVariable()!=null){
					return scm.getVariable().getName();
				}
			} else {
				throw new MappingException("species '"+sc.getName()+"' interacts with surface '"+geometryClass.getName()+"', but is not mapped spatially adjacent");
			}
		}
	}
	return getNameScope().getSymbolName(ste);
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 4:28:57 PM)
 * @return cbit.vcell.mapping.MathSymbolMapping
 */
public MathSymbolMapping getMathSymbolMapping()  throws MappingException, MathException, MatrixException, ExpressionException, ModelException {
	
	mathSymbolMapping.reconcileVarNames(getMathDescription());
	
	return mathSymbolMapping;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.MembraneStructureAnalyzer
 * @param membrane cbit.vcell.model.Membrane
 */
protected MembraneStructureAnalyzer getMembraneStructureAnalyzer(SurfaceClass surfaceClass) {
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
 * Insert the method's description here.
 * Creation date: (4/4/2004 1:01:22 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return nameScope;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Insert the method's description here.
 * Creation date: (4/4/01 12:19:27 PM)
 * @return cbit.vcell.mapping.SimulationContext
 */
public SimulationContext getSimulationContext() {
	return simContext;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.SpeciesContextMapping
 * @param speciesContext SpeciesContext
 */
public SpeciesContextMapping getSpeciesContextMapping(SpeciesContext speciesContext) {
	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = enum1.nextElement();
		if (scm.getSpeciesContext()==speciesContext){
			return scm;
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (10/26/2006 4:37:10 PM)
 * @return java.util.Vector
 */
protected Vector<SpeciesContextMapping> getSpeciesContextMappingList() {
	return speciesContextMappingList;
}


/**
 * This method was created in VisualAge.
 * @return java.util.Enumeration
 */
protected Enumeration<SpeciesContextMapping> getSpeciesContextMappings() {
	return speciesContextMappingList.elements();
}


/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 */
public java.util.Enumeration<StructureAnalyzer> getStructureAnalyzers() {
	return structureAnalyzerList.elements();
}


/**
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.StructureAnalyzer
 * @param subVolume cbit.vcell.geometry.SubVolume
 */
protected VolumeStructureAnalyzer getVolumeStructureAnalyzer(SubVolume subVolume) {
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


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (11/2/2005 4:42:01 PM)
 * @return cbit.vcell.math.Variable
 * @param name java.lang.String
 * @param exp cbit.vcell.parser.Expression
 */
protected Variable newFunctionOrConstant(String name, Expression exp, GeometryClass geometryClass) {
	if (exp.isNumeric()){
		return new Constant(name,exp);
	}else{
		if (geometryClass!=null){
			return new Function(name,exp,new Domain(geometryClass));
		}else{
			return new Function(name,exp,null);
		}
	}
}


/**
 * This method was created in VisualAge.
 * @param obs java.util.Observable
 * @param obj java.lang.Object
 */
protected void refresh() throws MappingException, ExpressionException, MatrixException, MathException, ModelException {
//System.out.println("MathMapping.refresh()");
	VCellThreadChecker.checkCpuIntensiveInvocation();
	
	localIssueList.clear();
//	refreshKFluxParameters();
	refreshSpeciesContextMappings();
	refreshStructureAnalyzers();
	refreshVariables();
	refreshLocalNameCount();
	refreshMathDescription();
}

private Expression substituteGlobalParameters(Expression exp) throws ExpressionException {
	Expression exp2 = new Expression(exp);
	//
	// do until no more globals to substitute
	//
	int count = 0;
	ModelParameter[] modelParams = simContext.getModel().getModelParameters();
	while (true){
		if (count++ > 30){
			throw new ExpressionBindingException("infinite loop in eliminating function nesting");
		}
		//
		// get all symbols (identifiers), make list of globals used
		//
		String[] symbols = exp2.getSymbols();
		Vector<ModelParameter> globalsVector = new Vector<ModelParameter>();
		if (symbols != null) {
			for (int i = 0; i < symbols.length; i++) {
				for (int j = 0; j < modelParams.length; j++) {
					if (symbols[i].equals(modelParams[j].getName())) {
						globalsVector.addElement(modelParams[j]);
					}
				}
			}
		}
		//
		// if no more globals, done!
		//
		if (globalsVector.size()==0){
			break;
		}
		
		//
		// substitute out all globals at this level
		//
		for (int i = 0; i < globalsVector.size(); i++){
			ModelParameter mp = globalsVector.elementAt(i);
			Expression mpExp = new Expression(mp.getName());
			exp2.substituteInPlace(mpExp,mp.getExpression());
		}
	}
//	exp2.bindExpression(simContext.getModel());
	return exp2;
}



/**
 * This method was created in VisualAge.
 */
@SuppressWarnings("deprecation")
protected void refreshMathDescription() throws MappingException, MatrixException, MathException, ExpressionException, ModelException {

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
	SubVolume subVolumes[] = simContext.getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
//	for (int i = 0; i < subVolumes.length; i++){
//		Structure[] mappedStructures = simContext.getGeometryContext().getStructuresFromGeometryClass(subVolumes[i]);
//		if (mappedStructures==null || mappedStructures.length==0){
//			localIssueList.add(new Issue(subVolumes[i], IssueCategory.GeometryClassNotMapped,"In Application '" + simContext.getName() + "', geometry subVolume '"+subVolumes[i].getName()+"' not mapped from a model structure",Issue.SEVERITY_WARNING));
//		}
//	}

	// deals with model parameters
	HashMap<VolVariable, EventAssignmentOrRateRuleInitParameter> eventOrRateRuleVolVarHash = new HashMap<VolVariable, EventAssignmentOrRateRuleInitParameter>();
	HashMap<VolVariable, RateRuleRateParameter> rateRuleRateParamHash = new HashMap<VolVariable, RateRuleRateParameter>();
	ArrayList<SymbolTableEntry> rateRuleVarTargets = new ArrayList<SymbolTableEntry>();
	Model model = simContext.getModel();
	ModelUnitSystem modelUnitSystem = model.getUnitSystem();
	VCUnitDefinition timeUnit = modelUnitSystem.getTimeUnit();
	ModelParameter[] modelParameters = model.getModelParameters();
	if (simContext.getGeometry().getDimension() == 0) {
		//
		// global parameters from model (that presently are constants)
		//
		BioEvent[] bioEvents = simContext.getBioEvents();
		ArrayList<SymbolTableEntry> eventAssignTargets = new ArrayList<SymbolTableEntry>();
		if (bioEvents != null && bioEvents.length > 0) {
			for (BioEvent be : bioEvents) {
				for (EventAssignment ea : be.getEventAssignments()) {
					if (!eventAssignTargets.contains(ea.getTarget())) {
						eventAssignTargets.add(ea.getTarget());
					}
				}
			}
		}
		
		/** @author anu : RATE RULES */
		RateRule[] rateRules = simContext.getRateRules();
		if (rateRules != null && rateRules.length > 0) {
			for (RateRule rr : rateRules) {
				SymbolTableEntry rateRuleVar = rr.getRateRuleVar();
				if (!rateRuleVarTargets.contains(rateRuleVar)) {
					rateRuleVarTargets.add(rateRuleVar);
				}
			}
		}
		for (int j=0;j<modelParameters.length;j++){
			Expression modelParamExpr = modelParameters[j].getExpression();
			GeometryClass geometryClass = getDefaultGeometryClass(modelParamExpr);
			VCUnitDefinition paramUnit = modelParameters[j].getUnitDefinition();
			modelParamExpr = getIdentifierSubstitutions(modelParamExpr, paramUnit, geometryClass);
			if (eventAssignTargets.contains(modelParameters[j]) || rateRuleVarTargets.contains(modelParameters[j])) {
				EventAssignmentOrRateRuleInitParameter eap = null;
				try {
					eap = addEventAssignmentOrRateRuleInitParameter(modelParameters[j], modelParamExpr, 
							PARAMETER_ROLE_EVENTASSIGN_OR_RATERULE_INITCONDN, paramUnit);
				} catch (PropertyVetoException e) {
					e.printStackTrace(System.out);
					throw new MappingException(e.getMessage());
				}
				VolVariable volVar = new VolVariable(modelParameters[j].getName(),null);
				varHash.addVariable(volVar);
				eventOrRateRuleVolVarHash.put(volVar, eap);
				/** RATE RULES */
				if (rateRuleVarTargets.contains(modelParameters[j])) {
					RateRuleRateParameter rateParam = null;
					try {
						Expression origExp = simContext.getRateRule(modelParameters[j]).getRateRuleExpression();
						VCUnitDefinition rateUnit = modelUnitSystem.getInstance_TBD();
						if (paramUnit != null && !paramUnit.equals(modelUnitSystem.getInstance_TBD())) {
							rateUnit = paramUnit.divideBy(timeUnit);
						}
						Expression rateExpr = getIdentifierSubstitutions(origExp, rateUnit, geometryClass);
						rateParam = addRateRuleRateParameter(modelParameters[j], rateExpr, PARAMETER_ROLE_RATERULE_RATE, rateUnit);
					} catch (PropertyVetoException e) {
						e.printStackTrace(System.out);
						throw new MappingException(e.getMessage());
					}
					rateRuleRateParamHash.put(volVar, rateParam);
				}

			} else {
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(modelParameters[j], geometryClass), modelParamExpr,geometryClass));
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
			throw new MappingException("In Application '" + simContext.getName() + "', " + reactionSteps[i].getTerm()+" '"+reactionSteps[i].getName()+"' contains unresolved identifier(s): "+buffer);
		}
	}
	
	//
	// create new MathDescription (based on simContext's previous MathDescription if possible)
	//
	MathDescription oldMathDesc = simContext.getMathDescription();
	mathDesc = null;
	if (oldMathDesc != null){
		if (oldMathDesc.getVersion() != null){
			mathDesc = new MathDescription(oldMathDesc.getVersion());
		}else{
			mathDesc = new MathDescription(oldMathDesc.getName());
		}
	}else{
		mathDesc = new MathDescription(simContext.getName()+"_generated");
	}

	//
	// volume variables
	//
	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = enum1.nextElement();
		if (scm.getVariable() instanceof VolVariable){
			if (!(mathDesc.getVariable(scm.getVariable().getName()) instanceof VolVariable)){
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
							if (mathDesc.getVariable(Membrane.MEMBRANE_VOLTAGE_REGION_NAME)==null){
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
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameters[i],geometryClass), getIdentifierSubstitutions(parameters[i].getExpression(),parameters[i].getUnitDefinition(),geometryClass),geometryClass));
			}
		}
	}
	//
	// initial constants (either function or constant)
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
//						scsInitExpr.bindExpression(this);
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
				initConcExpr = Expression.mult(Expression.div(new Expression(initCountParm,getNameScope()),structureSizeExpr),unitFactor);
			}
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContext.getStructure());
			String[] symbols = initConcExpr.getSymbols();
			// Check if 'initExpr' has other speciesContexts in its expression, need to replace it with 'spContext_init'
			for (int j = 0; symbols != null && j < symbols.length; j++) {
				// if symbol is a speciesContext, replacing it with a reference to initial condition for that speciesContext.
				SpeciesContext spC = null;
				SymbolTableEntry ste = initConcExpr.getSymbolBinding(symbols[j]);
				if (ste instanceof SpeciesContextSpecProxyParameter) {
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
//						scsInitExpr.bindExpression(this);
						initConcExpr.substituteInPlace(new Expression(spC.getName()), scsInitExpr);
					}
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
	varHash.addVariable(new Constant(model.getKMOLE().getName(),getIdentifierSubstitutions(model.getKMOLE().getExpression(),model.getKMOLE().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(model.getN_PMOLE().getName(),getIdentifierSubstitutions(model.getN_PMOLE().getExpression(),model.getN_PMOLE().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(model.getKMILLIVOLTS().getName(),getIdentifierSubstitutions(model.getKMILLIVOLTS().getExpression(),model.getKMILLIVOLTS().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(model.getK_GHK().getName(),getIdentifierSubstitutions(model.getK_GHK().getExpression(),model.getK_GHK().getUnitDefinition(),null)));
	//
	// geometric functions
	//
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (simContext.getGeometry().getDimension()==0){
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
	// rate rules for global/model parameters have already been converted to math; need to do the same for other STEs (species, compartments (not allowed in VCell), etc.).
	//
	for (SymbolTableEntry rrvSTE : rateRuleVarTargets) {
		if (rrvSTE instanceof SpeciesContext) {
			SpeciesContext rateRuleSpContext = (SpeciesContext)rrvSTE;
			// check if speciesContext has already been added as a vol/membrane var
			SpeciesContextMapping scm = getSpeciesContextMapping(rateRuleSpContext);
			if (scm.getVariable() instanceof VolVariable || scm.getVariable() instanceof MemVariable) {
				throw new RuntimeException("SpeciesContext '" + rrvSTE.getName() + "' has an equation and is also a rate rule variable, which is not allowed.");
			}

			// get the initial condition expression of the speciesContext.
			SpeciesContextSpec speciesContextSpec = simContext.getReactionContext().getSpeciesContextSpec(rateRuleSpContext);
			SpeciesContextSpecParameter scsInitParam = speciesContextSpec.getInitialConditionParameter();
			Expression scInitExpr = scsInitParam.getExpression();
			GeometryClass geometryClass = getDefaultGeometryClass(scInitExpr);
			VCUnitDefinition scsInitParamUnit = scsInitParam.getUnitDefinition();
			scInitExpr = getIdentifierSubstitutions(scInitExpr, scsInitParamUnit, geometryClass);
			EventAssignmentOrRateRuleInitParameter eap = null;
			try {
				// create an eventAssgnmentOrRateRuleInitParameter for the rate rule variable
				eap = addEventAssignmentOrRateRuleInitParameter(rateRuleSpContext, scInitExpr,	PARAMETER_ROLE_EVENTASSIGN_OR_RATERULE_INITCONDN, scsInitParamUnit);
			} catch (PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new MappingException(e.getMessage());
			}
			// create a volVariable for this speciesContext (shouldn't have one already - since a speciesContext that has a rate rule should not participate in a reaction.
			VolVariable volVar = new VolVariable(rateRuleSpContext.getName(),null);
			varHash.addVariable(volVar);
			eventOrRateRuleVolVarHash.put(volVar, eap);
			// create the rate parameter
			RateRuleRateParameter rateParam = null;
			try {
				Expression origExp = simContext.getRateRule(rateRuleSpContext).getRateRuleExpression();
				VCUnitDefinition rateUnit = modelUnitSystem.getInstance_TBD();
				if (scsInitParamUnit != null && !scsInitParamUnit.equals(modelUnitSystem.getInstance_TBD())) {
					rateUnit = scsInitParamUnit.divideBy(timeUnit);
				}
				Expression rateExpr = getIdentifierSubstitutions(origExp, rateUnit, geometryClass);
				rateParam = addRateRuleRateParameter(rateRuleSpContext, rateExpr, PARAMETER_ROLE_RATERULE_RATE, rateUnit);
			} catch (PropertyVetoException e) {
				e.printStackTrace(System.out);
				throw new MappingException(e.getMessage());
			}
			rateRuleRateParamHash.put(volVar, rateParam);
		}	// end if (ste instanceof SC)
	}	// end - for (ste)
			
	//
	// conserved constants  (e.g. K = A + B + C) (these are treated as functions now)
	//
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		GeometryClass geometryClass = fieldMathMappingParameters[i].getGeometryClass();
		varHash.addVariable(newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],geometryClass),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),geometryClass),fieldMathMappingParameters[i].getGeometryClass()));
	}

	//
	// functions
	//
	enum1 = getSpeciesContextMappings();
	RateRule[] rateRules = simContext.getRateRules();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable()==null && scm.getDependencyExpression()!=null){
			// check if speciesContext has a rateRule; then the speciesContext should not be added as a constant
			if (rateRules == null) {
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
	}

	//
	// set Variables to MathDescription all at once with the order resolved by "VariableHash"
	//
	mathDesc.setAllVariables(varHash.getAlphabeticallyOrderedVariables());
	
	//
	// geometry
	//
	if (simContext.getGeometryContext().getGeometry() != null){
		try {
			mathDesc.setGeometry(simContext.getGeometryContext().getGeometry());
		}catch (java.beans.PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new MappingException("failure setting geometry "+e.getMessage());
		}
	}else{
		throw new MappingException("geometry must be defined");
	}

	//
	// volume subdomains
	//
	subVolumes = simContext.getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
	for (int j=0;j<subVolumes.length;j++){
		SubVolume subVolume = (SubVolume)subVolumes[j];
		//
		// get priority of subDomain
		//
		int priority;
		if (simContext.getGeometryContext().getGeometry().getDimension()==0){
			priority = CompartmentSubDomain.NON_SPATIAL_PRIORITY;
		}else{
			priority = j; // now does not have to match spatial feature, *BUT* needs to be unique
		}
		//
		// create subDomain
		//
		CompartmentSubDomain subDomain = new CompartmentSubDomain(subVolume.getName(),priority);
		mathDesc.addSubDomain(subDomain);

		//
		// assign boundary condition types
		//
		StructureMapping[] mappedSMs = simContext.getGeometryContext().getStructureMappings(subVolume);
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
			{ //local variable scope
				GeometryClass gc = sm.getGeometryClass();
				String sdName = subDomain.getName();
				if (gc != null) {
					String gcName = gc.getName();
					if (!gcName.equals(sdName)) {
						continue;
					}
				}
				else {
					 throw new MappingException("Subdomain " + sdName + " must be mapped to exactly one compartment.");
				}
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
						
						((PdeEquation)equation).setVelocityX((scs.getVelocityXParameter().getExpression()==null)?(null) : new Expression(getMathSymbol(scs.getVelocityXParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setVelocityY((scs.getVelocityYParameter().getExpression()==null)?(null) : new Expression(getMathSymbol(scs.getVelocityYParameter(),sm.getGeometryClass())));
						((PdeEquation)equation).setVelocityZ((scs.getVelocityZParameter().getExpression()==null)?(null) : new Expression(getMathSymbol(scs.getVelocityZParameter(),sm.getGeometryClass())));
						
						subDomain.replaceEquation(equation);
					} else {
						//
						// ODE - species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(initConcParameter,null));
						Expression rate = (scm.getRate()==null) ? new Expression(0.0) : getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(sc.getStructure()).getGeometryClass());
						equation = new OdeEquation(variable,initial,rate);
						subDomain.replaceEquation(equation);
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
			// constructor calls the 'refresh' method which constructs depemdency matrix, dependent/independent vars and pseudoconstants, etc. 
			//FastSystemAnalyzer fs_analyzer = 
				new FastSystemAnalyzer(fastSystem, mathDesc);
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
						VolVariable vVar = (VolVariable)mathDesc.getVariable(getMathSymbol(capacitiveDevice.getVoltageSymbol(),membraneMapping.getGeometryClass()));
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
	GeometryClass[] geometryClasses = simContext.getGeometryContext().getGeometry().getGeometryClasses();
	for (int k=0;k<geometryClasses.length;k++){
		if (!(geometryClasses[k] instanceof SurfaceClass)){
			continue;
		}
		
		SurfaceClass surfaceClass = (SurfaceClass)geometryClasses[k];
		// determine membrane inside and outside subvolume
		// this preserves backward compatibility so that membrane subdomain
		// inside and outside correspond to structure hierarchy when present
		SubVolume outerSubVolume = null;
		SubVolume innerSubVolume = null;
		Structure[] mappedStructures = simContext.getGeometryContext().getStructuresFromGeometryClass(surfaceClass);
		for (Structure s : mappedStructures) {
			if (s instanceof Membrane) {
				Membrane m = (Membrane)s;
				Feature infeature = model.getStructureTopology().getInsideFeature(m);
				if (infeature!=null){
					FeatureMapping insm = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(infeature);
					if (insm.getGeometryClass() instanceof SubVolume) {
						innerSubVolume = (SubVolume)insm.getGeometryClass();
					}
				}
				Feature outfeature = model.getStructureTopology().getOutsideFeature(m);
				if (outfeature!=null){
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

		//
		// create subDomain
		//
		CompartmentSubDomain outerCompartment = mathDesc.getCompartmentSubDomain(outerSubVolume.getName());
		CompartmentSubDomain innerCompartment = mathDesc.getCompartmentSubDomain(innerSubVolume.getName());

		MembraneSubDomain memSubDomain = new MembraneSubDomain(innerCompartment,outerCompartment);
		mathDesc.addSubDomain(memSubDomain);

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
				if (memSubDomain.getInsideCompartment().getName().equals(var.getDomain().getName()) || 
					memSubDomain.getOutsideCompartment().getName().equals(var.getDomain().getName())){ 
					JumpCondition jc = memSubDomain.getJumpCondition(var);
					if (jc==null){
//System.out.println("MathMapping.refreshMathDescription(), adding jump condition for diffusing variable "+var.getName()+" on membrane "+membraneStructureAnalyzer.getMembrane().getName());
						if (var instanceof VolVariable){
							jc = new JumpCondition((VolVariable)var);
						}else if (var instanceof VolumeRegionVariable){
							jc = new JumpCondition((VolumeRegionVariable)var);
						}
						memSubDomain.addJumpCondition(jc);
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
			// constructor calls the 'refresh' method which constructs depemdency matrix, dependent/independent vars and pseudoconstants, etc. 
			//FastSystemAnalyzer fs_analyzer = 
				new FastSystemAnalyzer(fastSystem, mathDesc);
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
					if (mathDesc.getVariable(getMathSymbol(capacitiveDevice.getVoltageSymbol(),membraneMapping.getGeometryClass())) instanceof MembraneRegionVariable){
						MembraneRegionVariable vVar = (MembraneRegionVariable)mathDesc.getVariable(getMathSymbol(capacitiveDevice.getVoltageSymbol(),membraneMapping.getGeometryClass()));
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
	Set<VolVariable> hashKeySet = eventOrRateRuleVolVarHash.keySet();
	Iterator<VolVariable> volVarsIter = hashKeySet.iterator();
	// working under the assumption that we are dealing with non-spatial math, hence only one compartment domain!
	SubDomain subDomain = mathDesc.getSubDomains().nextElement();
	while (volVarsIter.hasNext()) {
		VolVariable volVar = volVarsIter.next();
		EventAssignmentOrRateRuleInitParameter initParam = eventOrRateRuleVolVarHash.get(volVar);
		//check event initial condition, it shouldn't contain vars, we have to do it here, coz we want to substitute functions...etc.
		Expression eapExp = MathUtilities.substituteFunctions(initParam.getExpression(), mathDesc);
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
	BioEvent[] bioevents = simContext.getBioEvents();
	if (bioevents != null && bioevents.length > 0) {
		for (BioEvent be : bioevents) {
			// transform the bioEvent trigger/delay to math Event
			Expression mathTriggerExpr = getIdentifierSubstitutions(be.getTriggerExpression(), modelUnitSystem.getInstance_DIMENSIONLESS(), null);
			Delay mathDelay = null;
			if (be.getDelay() != null) {
				boolean bUseValsFromTriggerTime = be.getDelay().useValuesFromTriggerTime();
				Expression mathDelayExpr = getIdentifierSubstitutions(be.getDelay().getDurationExpression(), timeUnit, null);
				mathDelay = new Delay(bUseValsFromTriggerTime, mathDelayExpr);
			}
			// now deal with (bio)event Assignment translation to math EventAssignment 
			ArrayList<EventAssignment> eventAssignments = be.getEventAssignments();
			ArrayList<Event.EventAssignment> mathEventAssignmentsList = new ArrayList<Event.EventAssignment>(); 
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
				VCUnitDefinition desiredConcUnits = model.getUnitSystem().getInstance("molecules").divideBy(model.getUnitSystem().getLengthUnit().raiseTo(new ucar.units.RationalNumber(dimension)));
				Expression unitFactor = getUnitFactor(desiredConcUnits.divideBy(mappedSpeciesContextUnit));
				volumeConcExp = Expression.add(volumeConcExp,Expression.mult(unitFactor,mappedSpeciesContextExpression)).flatten();
			}else if (geometryClass instanceof SurfaceClass){
				// membrane function
				int dimension = 2;
				VCUnitDefinition desiredSurfaceDensityUnits = model.getUnitSystem().getInstance("molecules").divideBy(model.getUnitSystem().getLengthUnit().raiseTo(new ucar.units.RationalNumber(dimension)));
				Expression unitFactor = getUnitFactor(desiredSurfaceDensityUnits.divideBy(mappedSpeciesContextUnit));
				membraneDensityExp = Expression.add(membraneDensityExp,Expression.mult(unitFactor,mappedSpeciesContextExpression)).flatten();
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
			if (mathDesc.getVariable(variable.getName())==null){
				mathDesc.addVariable(variable);
			}
		}
	}

	
	if (!mathDesc.isValid()){
		System.out.println(mathDesc.getVCML_database());
		throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
	}

//System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string begin ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
//System.out.println(mathDesc.getVCML());
//System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string end ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
}

protected GeometryClass getDefaultGeometryClass(Expression expr) throws ExpressionException, MappingException {
	GeometryClass geometryClass = null;
	if (simContext.getGeometry().getDimension() == 0) {
		return null;
	}
	
	String[] symbols = expr.getSymbols();
	// if expr has no symbols, model param cannot be localized to a domain (its a const).
	if (symbols == null) {
		return null;
	} else {
		Expression modelParamExpr = substituteGlobalParameters(expr); 
		symbols = modelParamExpr.getSymbols();
		for (int k = 0; symbols != null && k < symbols.length; k++) {
			Structure symbolStructure = null;
			SymbolTableEntry ste = modelParamExpr.getSymbolBinding(symbols[k]);
			if (ste instanceof SpeciesContext) {
				symbolStructure = ((SpeciesContext)ste).getStructure();
			} else if (ste instanceof StructureSize) {
				symbolStructure = ((StructureSize)ste).getStructure();
			} else if (ste instanceof MembraneVoltage) {
				symbolStructure = ((MembraneVoltage)ste).getMembrane();
			}
			if (symbolStructure != null) {
				StructureMapping sm = simContext.getGeometryContext().getStructureMapping(symbolStructure);
				GeometryClass symbolGeomClass = sm.getGeometryClass();
				
				if (geometryClass == null) {
					geometryClass = symbolGeomClass;
				} else {
					if (geometryClass != symbolGeomClass) {
						if (geometryClass instanceof SurfaceClass) {
							if (symbolGeomClass instanceof SurfaceClass) {
								throw new MappingException("The expression '" + expr.infix() + "' references variables in surface domain '" + geometryClass.getName() + "' & surface domain '" + symbolGeomClass.getName() + "' that cannot be evaluated.");
							} else if (symbolGeomClass instanceof SubVolume){
								// geomClass : surfaceClass; symbolGeomClass : subVol 
								if (!((SurfaceClass)geometryClass).isAdjacentTo((SubVolume)symbolGeomClass)) {
									throw new MappingException("The expression '" + expr.infix() + "' references variables in surface domain '" + geometryClass.getName() + "' & volume domain '" + symbolGeomClass.getName() + "' that cannot be evaluated.");
								}
							} else {
								throw new MappingException("unexpected geometry class : " + symbolGeomClass.getClass());
							}
						} else if (geometryClass instanceof SubVolume){	// geometryClass is a SubVolume
							if (symbolGeomClass instanceof SubVolume) {
								// check if adjacent; if so, choose separating membrane.
								SurfaceClass surfaceClass = simContext.getGeometry().getGeometrySurfaceDescription().getSurfaceClass((SubVolume)symbolGeomClass, (SubVolume)geometryClass);
								if (surfaceClass != null) {
									geometryClass = surfaceClass;
								} else {
									throw new MappingException("The expression '" + expr.infix() + "' references variables in volume domain '" + geometryClass.getName() + "' & volume domain '" + symbolGeomClass.getName() + "' that cannot be evaluated.");
								}
							} else {
								// geomClass : subVol; symbolGeomClass = surfaceClass
								SurfaceClass surfaceSymbolGeomClass = (SurfaceClass)symbolGeomClass;
								if (!surfaceSymbolGeomClass.isAdjacentTo((SubVolume)geometryClass)) {
									throw new MappingException("The expression '" + expr.infix() + "' references variables in surface domain '" + surfaceSymbolGeomClass.getName() + "' & volume domain '" + geometryClass.getName() + "' that cannot be evaluated.");
								}else{
									geometryClass = symbolGeomClass;
								}
							}
						} else {
							throw new MappingException("unexpected geometry class : " + geometryClass.getClass());
						}
					}
				}
			}
		}
	}
	return geometryClass;
}

/**
 * This method was created in VisualAge.
 */
protected void refreshSpeciesContextMappings() throws ExpressionException, MappingException, MathException {
	
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
//		scm.setDiffusing(isDiffusionRequired(scs.getSpeciesContext()));
//		scm.setAdvecting(isAdvectionRequired(scs.getSpeciesContext()));
		if (scs.isConstant()){
			Expression initCond = scs.getInitialConditionParameter() == null? null : new Expression(scs.getInitialConditionParameter(),getNameScope());
			scm.setDependencyExpression(initCond);
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
protected void refreshStructureAnalyzers() {

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
protected void refreshVariables() throws MappingException {

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
		if (scm.getDependencyExpression() == null && (!scs.isConstant() || getSimulationContext().hasEventAssignment(scs.getSpeciesContext()))){
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



/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 12:59:56 AM)
 * @param mathMappingParameter cbit.vcell.mapping.MathMapping.MathMappingParameter
 */
void removeMathMappingParameter(MathMapping.MathMappingParameter mathMappingParameter) throws java.beans.PropertyVetoException {
	MathMappingParameter newMathMappingParameters[] = (MathMappingParameter[])BeanUtils.removeElement(fieldMathMappingParameters,mathMappingParameter);
	setMathMapppingParameters(newMathMappingParameters);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}

/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * Sets the mathMappingParameters property (MathMapping.MathMappingParameter[]) value.
 * @param kineticsParameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getMathMappingParameters
 */
void setMathMapppingParameters(MathMappingParameter[] mathMappingParameters) throws java.beans.PropertyVetoException {
	MathMapping.MathMappingParameter[] oldValue = fieldMathMappingParameters;
	fireVetoableChange("mathMappingParameters", oldValue, mathMappingParameters);
	fieldMathMappingParameters = mathMappingParameters;
	firePropertyChange("mathMappingParameters", oldValue, mathMappingParameters);
}


public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {	
	simContext.getModel().getLocalEntries(entryMap);
	simContext.getLocalEntries(entryMap);
	for (SymbolTableEntry ste : fieldMathMappingParameters) {
		entryMap.put(ste.getName(), ste);
	}
}


public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	getNameScope().getExternalEntries(entryMap);
}

}
