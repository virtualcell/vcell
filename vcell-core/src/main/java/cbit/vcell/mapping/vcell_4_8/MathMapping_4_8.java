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

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;
import org.vcell.util.VCellThreadChecker;

import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.mapping.BioEvent;
import cbit.vcell.mapping.BioEvent.BioEventParameterType;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.ElectricalStimulus;
import cbit.vcell.mapping.ElectricalStimulus.ElectricalStimulusParameterType;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathSymbolMapping;
import cbit.vcell.mapping.MembraneMapping;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ReactionSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SpeciesContextMapping;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecProxyParameter;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.mapping.StructureMapping.StructureMappingParameter;
import cbit.vcell.mapping.UnitFactorProvider;
import cbit.vcell.math.CompartmentSubDomain;
import cbit.vcell.math.Constant;
import cbit.vcell.math.Equation;
import cbit.vcell.math.Event;
import cbit.vcell.math.Event.Delay;
import cbit.vcell.math.FastInvariant;
import cbit.vcell.math.FastRate;
import cbit.vcell.math.FastSystem;
import cbit.vcell.math.Function;
import cbit.vcell.math.JumpCondition;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionEquation;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.MembraneSubDomain;
import cbit.vcell.math.OdeEquation;
import cbit.vcell.math.PdeEquation;
import cbit.vcell.math.SubDomain;
import cbit.vcell.math.Variable;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.math.VariableHash;
import cbit.vcell.math.VolVariable;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ExpressionContainer;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
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
 */
public class MathMapping_4_8 implements ScopedSymbolTable, UnitFactorProvider, IssueSource {
	
	public static final String BIO_PARAM_SUFFIX_SPECIES_COUNT = "_temp_Count";
	public static final String BIO_PARAM_SUFFIX_SPECIES_CONCENTRATION = "_temp_Conc";
	public static final String MATH_VAR_SUFFIX_SPECIES_COUNT = "";
	public static final String MATH_FUNC_SUFFIX_SPECIES_CONCENTRATION = "_Conc";
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT = "_initCount";
	public static final String MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION = "_init";
	public static final String MATH_FUNC_SUFFIX_EVENTASSIGN_INIT = "_init";
	
	private SimulationContext simContext = null;
	protected MathDescription mathDesc = null;
	private PotentialMapping potentialMapping = null;  // null if don't need it
	protected MathSymbolMapping mathSymbolMapping = new MathSymbolMapping();
	protected Vector<Issue> localIssueList = new Vector<Issue>();
	protected final IssueContext issueContext;

	private MathMapping_4_8.MathMappingParameter[] fieldMathMappingParameters = new MathMappingParameter[0];
	private Hashtable<ModelParameter, Hashtable<String, Expression>> globalParamVariantsHash = new Hashtable<ModelParameter, Hashtable<String,Expression>>();
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
	public static final int PARAMETER_ROLE_EVENTASSIGN_INITCONDN = 7;
	public static final int NUM_PARAMETER_ROLES = 8;
		
	private Vector<StructureAnalyzer> structureAnalyzerList = new Vector<StructureAnalyzer>();
	
	private Vector<SpeciesContextMapping> speciesContextMappingList = new Vector<SpeciesContextMapping>();
	private HashMap<String, Integer> localNameCountHash = new HashMap<String, Integer>();
	
	public final static Domain nullDomain = null;

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
			return MathMapping_4_8.this;
		}
		public boolean isPeer(NameScope nameScope){
			if (super.isPeer(nameScope)){
				return true;
			}
			return (nameScope == MathMapping_4_8.this.simContext.getNameScope() || nameScope == MathMapping_4_8.this.simContext.getModel().getNameScope());
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.mathmappingType;
		}
	}

	public class MathMappingParameter extends Parameter implements ExpressionContainer {
		
		private String fieldParameterName = null;
		private Expression fieldExpression = null;
		private VCUnitDefinition fieldVCUnitDefinition = null;
		private int fieldRole = -1;

		protected MathMappingParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
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
			return MathMapping_4_8.this.getNameScope();
		}

		public void setExpression(Expression argExpression) {
			Expression oldValue = fieldExpression;
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
	public class KFluxParameter extends MathMappingParameter {
		
		private MembraneMapping fieldMembraneMapping = null;
		private Feature fieldFeature = null;

		protected KFluxParameter(String argName, Expression argExpression, VCUnitDefinition argVCUnitDefinition, MembraneMapping argMembraneMapping, Feature argFeature) {
			super(argName,argExpression,PARAMETER_ROLE_KFLUX,argVCUnitDefinition);
			this.fieldMembraneMapping = argMembraneMapping;
			this.fieldFeature = argFeature;
		}

		public MembraneMapping getMembraneMapping() {
			return fieldMembraneMapping;
		}

		public Feature getFeature() {
			return fieldFeature;
		}

	}
	public class ProbabilityParameter extends MathMappingParameter {
		
		private ReactionSpec fieldReactionSpec = null;

		protected ProbabilityParameter(String argName, Expression argExpression, int Role,VCUnitDefinition argVCUnitDefinition, ReactionSpec argReactionSpec) {
			super(argName,argExpression,Role,argVCUnitDefinition);
			this.fieldReactionSpec = argReactionSpec;
		}

		public ReactionSpec getReactionSpec() {
			return fieldReactionSpec;
		}

	}
	
	public class SpeciesConcentrationParameter extends MathMappingParameter {
		private SpeciesContextSpec speciesContextSpec = null;
		
		protected SpeciesConcentrationParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition, SpeciesContextSpec argscSpec) {
			super(argName,argExpression,argRole,argVCUnitDefinition);
			this.speciesContextSpec = argscSpec;
		}

		public SpeciesContextSpec getSpeciesContextSpec() {
			return speciesContextSpec;
		}
	}
	
	public class SpeciesCountParameter extends MathMappingParameter {
		private SpeciesContextSpec speciesContextSpec = null;
		
		protected SpeciesCountParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition, SpeciesContextSpec argscSpec) {
			super(argName,argExpression,argRole,argVCUnitDefinition);
			this.speciesContextSpec = argscSpec;
		}

		public SpeciesContextSpec getSpeciesContextSpec() {
			return speciesContextSpec;
		}
	}

	public class EventAssignmentInitParameter extends MathMappingParameter {
		protected EventAssignmentInitParameter(String argName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
			super(argName,argExpression,argRole,argVCUnitDefinition);
		}
	}

	static {
		System.out.println("MathMapping: capacitances must not be overridden and must be constant (used as literals in KVL)");
	};

/**
 * This method was created in VisualAge.
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 */
public MathMapping_4_8(SimulationContext simContext) {
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
MathMapping_4_8.MathMappingParameter addMathMappingParameter(String name, Expression expression, int role, VCUnitDefinition unitDefinition) throws java.beans.PropertyVetoException, ExpressionBindingException {

	MathMapping_4_8.MathMappingParameter newParameter = new MathMapping_4_8.MathMappingParameter(name,expression,role,unitDefinition);
	MathMapping_4_8.MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addMathMappingParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addMathMappingParameter found duplicate parameter for name "+name);
		}
		return previousParameter;
	}
	expression.bindExpression(this);
	MathMapping_4_8.MathMappingParameter newParameters[] = (MathMapping_4_8.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
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
MathMapping_4_8.ProbabilityParameter addProbabilityParameter(String name, Expression expression, int role, VCUnitDefinition unitDefinition,ReactionSpec argReactionSpec) throws java.beans.PropertyVetoException, ExpressionBindingException {

	MathMapping_4_8.ProbabilityParameter newParameter = new MathMapping_4_8.ProbabilityParameter(name,expression,role,unitDefinition,argReactionSpec);
	MathMapping_4_8.MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addProbabilityParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addProbabilityParameter found duplicate parameter for name "+name);
		}
		return (MathMapping_4_8.ProbabilityParameter)previousParameter;
	}
	//expression.bindExpression(this);
	MathMapping_4_8.MathMappingParameter newParameters[] = (MathMapping_4_8.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

MathMapping_4_8.SpeciesConcentrationParameter addSpeciesConcentrationParameter(String name, Expression expr, int role, VCUnitDefinition unitDefn,SpeciesContextSpec argscSpec) throws PropertyVetoException {

	MathMapping_4_8.SpeciesConcentrationParameter newParameter = new MathMapping_4_8.SpeciesConcentrationParameter(name,expr,role,unitDefn,argscSpec);
	MathMapping_4_8.MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addConcentrationParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addConcentrationParameter found duplicate parameter for name '"+name+"'.");
		}
		return (MathMapping_4_8.SpeciesConcentrationParameter)previousParameter;
	}
	//expression.bindExpression(this);
	MathMapping_4_8.MathMappingParameter newParameters[] = (MathMapping_4_8.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

MathMapping_4_8.SpeciesCountParameter addSpeciesCountParameter(String name, Expression expr, int role, VCUnitDefinition unitDefn,SpeciesContextSpec argscSpec) throws PropertyVetoException {

	MathMapping_4_8.SpeciesCountParameter newParameter = new MathMapping_4_8.SpeciesCountParameter(name,expr,role,unitDefn,argscSpec);
	MathMapping_4_8.MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addCountParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addCountParameter found duplicate parameter for name '"+name+"'.");
		}
		return (MathMapping_4_8.SpeciesCountParameter)previousParameter;
	}
	//expression.bindExpression(this);
	MathMapping_4_8.MathMappingParameter newParameters[] = (MathMapping_4_8.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
	setMathMapppingParameters(newParameters);
	return newParameter;
}

MathMapping_4_8.EventAssignmentInitParameter addEventAssignmentInitParameter(String name, Expression expr, int role, VCUnitDefinition unitDefn) throws PropertyVetoException {

	MathMapping_4_8.EventAssignmentInitParameter newParameter = new MathMapping_4_8.EventAssignmentInitParameter(name,expr,role,unitDefn);
	MathMapping_4_8.MathMappingParameter previousParameter = getMathMappingParameter(name);
	if(previousParameter != null){
		System.out.println("MathMapping.MathMappingParameter addEventAssignInitParameter found duplicate parameter for name "+name);
		if(!previousParameter.compareEqual(newParameter)){
			throw new RuntimeException("MathMapping.MathMappingParameter addEventAssignInitParameter found duplicate parameter for name '"+name+"'.");
		}
		return (MathMapping_4_8.EventAssignmentInitParameter)previousParameter;
	}
	MathMapping_4_8.MathMappingParameter newParameters[] = (MathMapping_4_8.MathMappingParameter[])BeanUtils.addElement(fieldMathMappingParameters,newParameter);
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
public MathMapping_4_8.KFluxParameter getFluxCorrectionParameter(MembraneMapping membraneMapping, Feature feature) throws MappingException, ExpressionException {
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		if (fieldMathMappingParameters[i] instanceof KFluxParameter){
			MathMapping_4_8.KFluxParameter kfluxParameter = (MathMapping_4_8.KFluxParameter)fieldMathMappingParameters[i];
			if (kfluxParameter.getMembraneMapping() == membraneMapping &&
				kfluxParameter.getFeature() == feature){
				return kfluxParameter;
			}
		}
		
	}
	throw new MappingException("KFluxParameter for membrane "+membraneMapping.getMembrane().getName()+" and feature " + feature.getName() + " not found");
}


/**
 * Substitutes appropriate variables for speciesContext bindings
 *
 * @return cbit.vcell.parser.Expression
 * @param origExp cbit.vcell.parser.Expression
 * @param structureMapping cbit.vcell.mapping.StructureMapping
 */
protected Expression getIdentifierSubstitutions(Expression origExp, VCUnitDefinition desiredExpUnitDef, StructureMapping structureMapping) throws ExpressionException, MappingException {
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
			String newName = getMathSymbol(ste,structureMapping);
			if (!newName.equals(symbols[i])){
				newExp.substituteInPlace(new Expression(symbols[i]),new Expression(newName));
			}
		}
	}
	return newExp;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Function
 */
private Expression getInsideFluxCorrectionExpression(MembraneMapping membraneMapping) throws ExpressionException {
	//
	//
	// use surfaceToVolumeRatio to get from membrane to total inside volume
	// then divide by 1-Sum(child volume fractions)
	//
	//
	FeatureMapping insideFeatureMapping = (FeatureMapping) simContext.getGeometryContext().getStructureMapping(getSimulationContext().getModel().getStructureTopology().getInsideFeature(membraneMapping.getMembrane()));
	Expression insideResidualVolFraction = getResidualVolumeFraction(insideFeatureMapping);
	Expression exp = new Expression(membraneMapping.getSurfaceToVolumeParameter(), simContext.getNameScope());
	exp = Expression.mult(exp,Expression.invert(insideResidualVolFraction));
	exp = exp.flatten();
//	exp.bindExpression(simulationContext);
	return exp;
}


/**
 * Insert the method's description here.
 * Creation date: (11/8/2004 3:36:31 PM)
 * @return cbit.util.Issue
 */
public Issue[] getIssues(IssueContext issueContext) {	
	Vector<Issue> issueList = new Vector<Issue>();
	boolean bIgnoreMathDescription = false;
	getSimulationContext().gatherIssues(issueContext, issueList, bIgnoreMathDescription);
	getSimulationContext().getModel().gatherIssues(issueContext, issueList);
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
public String getMathSymbol(SymbolTableEntry ste, StructureMapping structureMapping) throws MappingException {

	String mathSymbol = getMathSymbol0(ste,structureMapping);
	
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
	Species ss[] = simContext.getModel().getSpecies();
	for (Species s : ss) {
		String name = s.getCommonName();
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
private String getMathSymbol0(SymbolTableEntry ste, StructureMapping structureMapping) throws MappingException {
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
	if (ste instanceof MathMapping_4_8.ProbabilityParameter){ //be careful here, to see if we need mangle the reaction name
		MathMapping_4_8.ProbabilityParameter probParm = (MathMapping_4_8.ProbabilityParameter)ste;
		return probParm.getName();
	}
	if (ste instanceof MathMapping_4_8.SpeciesConcentrationParameter){
		MathMapping_4_8.SpeciesConcentrationParameter concParm = (MathMapping_4_8.SpeciesConcentrationParameter)ste;
		return concParm.getSpeciesContextSpec().getSpeciesContext().getName() + MATH_FUNC_SUFFIX_SPECIES_CONCENTRATION;
	}
	if (ste instanceof MathMapping_4_8.SpeciesCountParameter){
		MathMapping_4_8.SpeciesCountParameter countParm = (MathMapping_4_8.SpeciesCountParameter)ste;
		return countParm.getSpeciesContextSpec().getSpeciesContext().getName() + MATH_VAR_SUFFIX_SPECIES_COUNT;
	}
	if (ste instanceof MathMapping_4_8.EventAssignmentInitParameter){
		MathMapping_4_8.EventAssignmentInitParameter eventInitParm = (MathMapping_4_8.EventAssignmentInitParameter)ste;
		return eventInitParm.getName() + MATH_FUNC_SUFFIX_EVENTASSIGN_INIT;
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
		return getMathSymbol(sizeParameter,structureMapping);
	}
	if (ste instanceof ProxyParameter){
		ProxyParameter pp = (ProxyParameter)ste;
		return getMathSymbol0(pp.getTarget(),structureMapping);
	}
	
	// 
	Model model = simContext.getModel();
	if (ste instanceof ModelParameter) {
		ModelParameter mp = (ModelParameter)ste;
		if (simContext.getGeometry().getDimension() == 0) {
			return mp.getName();
		} else {
			if (mp.getExpression().getSymbols() == null) {
				return mp.getName();
			}
			// check if global param variant name exists in globalVarsHash. If so, return it, else, throw exception.
			Hashtable<String, Expression> smVariantsHash = globalParamVariantsHash.get(mp);
			String variantName = mp.getName()+"_"+TokenMangler.fixTokenStrict(structureMapping.getStructure().getName());
			if (smVariantsHash.get(variantName) != null) {
				return variantName;
			} else {
				// global param variant doesn't exist in the hash, so get the substituted expression for global param and
				// gather all symbols (speciesContexts) that do not match with arg 'structureMapping' to display a proper error message.
				Expression expr = null;
				try {
					expr = substituteGlobalParameters(mp.getExpression());
				} catch (ExpressionException e) {
					e.printStackTrace(System.out);
					throw new RuntimeException("Could not substitute expression for global parameter '" + mp.getName() + "' with expression '" + "'" + e.getMessage());
				}
				// find symbols (typically speciesContexts) in 'exp' that do not match with the arg 'structureMapping' 
				String[] symbols = expr.getSymbols();
				String msg = "";
				if (symbols != null) {
					Vector<String> spContextNamesVector = new Vector<String>();
					for (int j = 0; j < symbols.length; j++) {
						SpeciesContext sc = model.getSpeciesContext(symbols[j]);
						if (sc != null) {
							if (!sc.getStructure().compareEqual(structureMapping.getStructure())) {
								spContextNamesVector.addElement(sc.getName());
							}
						}
					}
					for (int i = 0; (spContextNamesVector != null && i < spContextNamesVector.size()); i++) {
						if (i == 0) {
							msg += "'" + spContextNamesVector.elementAt(i) + ", ";
						} else if (i == spContextNamesVector.size() - 1) {
							msg += spContextNamesVector.elementAt(i) + "'";
						} else {
							msg += spContextNamesVector.elementAt(i) + ", ";
						}
					}
				}
				throw new RuntimeException("Global parameter '" + mp.getName() + "' is not defined in compartment '" + structureMapping.getStructure().getName() + "', but was referenced in that compartment." +
						"\n\nExpression '" + mp.getExpression().infix() + "' for global parameter '" + mp.getName() + "' expands to '" + expr.infix() + "' " +
						"and contains species " + msg + " that is/are not in adjacent compartments.");
			}

			// return (mp.getName()+"_"+TokenMangler.fixTokenStrict(structureMapping.getStructure().getName()));
		}
	}
	
	if (ste instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
		SpeciesContextSpec.SpeciesContextSpecParameter scsParm = (SpeciesContextSpec.SpeciesContextSpecParameter)ste;
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_InitialConcentration){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+ MATH_FUNC_SUFFIX_SPECIES_INIT_CONCENTRATION;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_InitialCount){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+ MATH_FUNC_SUFFIX_SPECIES_INIT_COUNT;
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_DiffusionRate){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_diffusionRate";
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueXm){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_boundaryXm";
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueXp){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_boundaryXp";
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueYm){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_boundaryYm";
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueYp){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_boundaryYp";
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueZm){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_boundaryZm";
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_BoundaryValueZp){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_boundaryZp";
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_VelocityX){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_velocityX";
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_VelocityY){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_velocityY";
		}
		if (scsParm.getRole()==SpeciesContextSpec.ROLE_VelocityZ){
			return ((SpeciesContextSpec)(scsParm.getNameScope().getScopedSymbolTable())).getSpeciesContext().getName()+"_velocityZ";
		}
	}
	if (ste instanceof ElectricalDevice.ElectricalDeviceParameter){
		ElectricalDevice.ElectricalDeviceParameter edParm = (ElectricalDevice.ElectricalDeviceParameter)ste;
		ElectricalDevice electricalDevice = (ElectricalDevice)edParm.getNameScope().getScopedSymbolTable();
		if (electricalDevice instanceof MembraneElectricalDevice){
			String nameWithScope = ((MembraneElectricalDevice)electricalDevice).getMembraneMapping().getMembrane().getNameScope().getName();
			if (edParm.getRole()==ElectricalDevice.ROLE_TotalCurrent){
				return "I_"+nameWithScope;
			}
			if (edParm.getRole()==ElectricalDevice.ROLE_TransmembraneCurrent){
				return "F_"+nameWithScope;
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
		if (esParm.getRole()==ElectricalStimulusParameterType.TotalCurrent){
			return "I_"+nameWithScope;
		} else if (esParm.getRole()==ElectricalStimulusParameterType.Voltage){
			return "V_"+nameWithScope;
		}
	}
	StructureTopology structTopology = model.getStructureTopology();
	if (ste instanceof StructureMapping.StructureMappingParameter){
		StructureMapping.StructureMappingParameter smParm = (StructureMapping.StructureMappingParameter)ste;
		Structure structure = ((StructureMapping)(smParm.getNameScope().getScopedSymbolTable())).getStructure();
		int role = smParm.getRole();
		if (role==StructureMapping.ROLE_VolumeFraction){
			return "VolFract_"+(structTopology.getInsideFeature((Membrane)structure)).getNameScope().getName();
		} else {
			String nameWithScope = structure.getNameScope().getName();
			if (role==StructureMapping.ROLE_SurfaceToVolumeRatio){
				return "SurfToVol_"+nameWithScope;
			} else if (role==StructureMapping.ROLE_InitialVoltage){
				return smParm.getName();
			} else if (role==StructureMapping.ROLE_SpecificCapacitance){
				return "C_"+nameWithScope;
			} else if (role==StructureMapping.ROLE_AreaPerUnitArea){
				return "AreaPerUnitArea_"+nameWithScope;
			} else if (role==StructureMapping.ROLE_AreaPerUnitVolume){
				return "AreaPerUnitVolume_"+nameWithScope;
			} else if (role==StructureMapping.ROLE_VolumePerUnitArea){
				return "VolumePerUnitArea_"+nameWithScope;
			} else if (role==StructureMapping.ROLE_VolumePerUnitVolume){
				return "VolumePerUnitVolume_"+nameWithScope;
			} else if (role==StructureMapping.ROLE_Size){
				if (simContext.getGeometry().getDimension() == 0) {
					// if geometry is compartmental, make sure compartment sizes are set if referenced in model.
					if (smParm.getExpression() == null || smParm.getExpression().isZero()) {
						throw new MappingException("\nIn non-spatial application '" + getSimulationContext().getName() + "', " +
								"size of structure '" + structure.getName() + "' must be assigned a " +
								"positive value if referenced in the model.\n\nPlease go to 'Structure Mapping' tab to check the size.");
					}
				}
				return "Size_"+nameWithScope;
			}
		}
	}
	//
	// substitute Variable or Function if a SpeciesContext
	//
	if (ste instanceof SpeciesContext){
		SpeciesContext sc = (SpeciesContext)ste;
		SpeciesContextMapping scm = getSpeciesContextMapping(sc);
		//
		// for reactions within a feature
		//
		if (structureMapping instanceof FeatureMapping){
			//
			// for any SpeciesContext, replace Symbol name with Variable name
			//				
			if (scm.getVariable()!=null && !scm.getVariable().getName().equals(steName)){
				return scm.getVariable().getName();
			}
		//
		// for reactions within a spatially resolved membrane, may need "_INSIDE" or "_OUTSIDE" for jump condition
		//
		// if the membrane is distributed, then always use the plain variable.
		//
		}else if (structureMapping instanceof MembraneMapping){
			Membrane membrane = ((MembraneMapping)structureMapping).getMembrane();
			//
			// if the speciesContext is also within the membrane, replace SpeciesContext name with Variable name
			//
			if (sc.getStructure() instanceof Membrane || getResolved(structureMapping)==false){
				if (scm.getVariable()!=null && !(scm.getVariable().getName().equals(steName))){
					return scm.getVariable().getName();
				}
			//
			// if the speciesContext is outside the membrane
			//
			} else {
				SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
				if (sc.getStructure() == structTopology.getInsideFeature(membrane) || sc.getStructure() == structTopology.getOutsideFeature(membrane)){
					if (getResolved(structureMapping) && !scs.isConstant()){
						if (!scs.isDiffusing()) {
							throw new MappingException("Enable diffusion in Application '" + simContext.getName() 
									+  "'. This must be done for any species (e.g '" + sc.getName() + "') in flux reactions.\n\n" 
									+ "To save or run simulations, set the diffusion rate to a non-zero " +
											"value in Initial Conditions or disable those reactions in Specifications->Reactions.");
						}					
						return scm.getVariable().getName()+ (sc.getStructure() == structTopology.getInsideFeature(membrane) ? "_INSIDE" : "_OUTSIDE");
					}else{
						return scm.getSpeciesContext().getName();
					}
				}else{
					throw new MappingException(sc.getName()+" shouldn't be involved with structure "+structureMapping.getStructure().getName());
				}
			}
		}
	}
	return getNameScope().getSymbolName(ste);
}

public Structure[] getStructures(SubVolume subVolume) {
	ArrayList<Structure> list = new ArrayList<Structure>();
	StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();
	//
	// first pass through, get all Features mapped to this SubVolume
	//
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (sm instanceof FeatureMapping && ((FeatureMapping)sm).getGeometryClass() == subVolume){
			list.add(sm.getStructure());
		}
	}
	//
	// second pass, get all Membranes enclosing those features
	//
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		if (sm instanceof MembraneMapping){
			Membrane membrane = ((MembraneMapping)sm).getMembrane();
			if (list.contains(simContext.getModel().getStructureTopology().getInsideFeature(membrane))){
				list.add(membrane);
			}
		}
	}

	if (list.size()>0){
		return list.toArray(new Structure[list.size()]);
	}else{
		return null;
	}
}

public Expression getResidualVolumeFraction(FeatureMapping featureMapping) throws ExpressionException {
	Expression exp = new Expression(1.0);
	Structure structures[] = simContext.getGeometryContext().getModel().getStructures();
	StructureTopology structTopology = simContext.getModel().getStructureTopology();
	for (int i=0;i<structures.length;i++){
		//
		// for each membrane that is distributed within this feature, subtract that volume fraction
		// ????? beware, 1 - v1 - v2 ... can result in a negative number if we're not careful.
		//
		Structure struct = structures[i];
		if (struct instanceof Membrane) {
			if ((structTopology.getOutsideFeature((Membrane) struct)) == featureMapping.getFeature()) {
				MembraneMapping mm = (MembraneMapping) simContext.getGeometryContext().getStructureMapping(struct);
				if (getResolved(mm)==false){
					exp = Expression.add(exp, Expression.negate(new Expression(mm.getVolumeFractionParameter(), simContext.getNameScope())));
				}
			}
		}
	}
	return exp;
}

boolean getResolved(StructureMapping structureMapping) {
	if (simContext==null || structureMapping.getStructure()==null){
		throw new RuntimeException("failed to process getResolved()");
	}
	if (structureMapping.getGeometryClass()==null){
		return false;
	}
	StructureTopology structureTopology = simContext.getModel().getStructureTopology();
	Structure parentStructure = structureTopology.getParentStructure(structureMapping.getStructure());
	if (parentStructure != null){
		StructureMapping outsideSM = simContext.getGeometryContext().getStructureMapping(parentStructure);
		if (outsideSM.getGeometryClass()!=null && outsideSM.getGeometryClass()==structureMapping.getGeometryClass()){
			return false;
		}
	}
	return true;
}

protected Feature getResolvedFeature(SubVolume subVolume){
	for (StructureMapping structureMapping : simContext.getGeometryContext().getStructureMappings(subVolume)){
		if (getResolved(structureMapping)){
			if (structureMapping instanceof FeatureMapping){
				return ((FeatureMapping)structureMapping).getFeature();
			}
		}
	}
	return null;
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
protected MembraneStructureAnalyzer getMembraneStructureAnalyzer(Membrane membrane) {
	Enumeration<StructureAnalyzer> enum1 = getStructureAnalyzers();
	while (enum1.hasMoreElements()){
		StructureAnalyzer sa = enum1.nextElement();
		if (sa instanceof MembraneStructureAnalyzer){
			MembraneStructureAnalyzer msa = (MembraneStructureAnalyzer)sa;
			if (msa.getMembrane()==membrane){
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
 * This method was created in VisualAge.
 * @return cbit.vcell.math.Function
 */
private Expression getOutsideFluxCorrectionExpression(SimulationContext simulationContext, MembraneMapping membraneMapping) throws ExpressionException {
	//
	// ?????? only works for 1 distributed organelle
	//
	FeatureMapping outsideFeatureMapping = (FeatureMapping) simulationContext.getGeometryContext().getStructureMapping(simContext.getModel().getStructureTopology().getOutsideFeature(membraneMapping.getMembrane()));
	Expression outsideVolFraction = getResidualVolumeFraction(outsideFeatureMapping);
	Expression surfaceToVolumeParameter = new Expression(membraneMapping.getSurfaceToVolumeParameter(), simulationContext.getNameScope());
	Expression volumeFractionParameter = new Expression(membraneMapping.getVolumeFractionParameter(), simulationContext.getNameScope());	
	Expression exp = Expression.div(Expression.mult(surfaceToVolumeParameter, volumeFractionParameter), outsideVolFraction);
		
	return exp;
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
 * Find the defined variable (VolVariable) for the resolved compartments.
 * This method is used to assure only one variable is created.
 * @return cbit.vcell.math.VolVariable
 * @param species cbit.vcell.model.Species
 */
private VolVariable getResolvedVolVariable(Species species) {
	Enumeration<SpeciesContextMapping> enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = enum1.nextElement();
		if (scm.getSpeciesContext().getSpecies()==species){
			Variable var = scm.getVariable();
			if (var instanceof VolVariable){
				Structure structure = scm.getSpeciesContext().getStructure();
				StructureMapping sm = simContext.getGeometryContext().getStructureMapping(structure);
				if (structure instanceof Feature && getResolved(sm)){
					return (VolVariable)var;
				}
			}
		}
	}
	return new VolVariable(TokenMangler.fixTokenStrict(species.getCommonName()),nullDomain);
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
protected Variable newFunctionOrConstant(String name, Expression exp) {
	if (exp.isNumeric()){
		return new Constant(name,exp);
	}else{
		return new Function(name,exp,nullDomain);
	}
}


/**
 * This method was created in VisualAge.
 * @param obs java.util.Observable
 * @param obj java.lang.Object
 */
private void refresh() throws MappingException, ExpressionException, MatrixException, MathException, ModelException {
//System.out.println("MathMapping.refresh()");
	VCellThreadChecker.checkCpuIntensiveInvocation();
	
	localIssueList.clear();
	refreshKFluxParameters();
	refreshSpeciesContextMappings();
	refreshStructureAnalyzers();
	refreshVariables();
	refreshLocalNameCount();
	refreshMathDescription();
}

/**
 * Insert the method's description here.
 * Creation date: (6/13/2004 8:10:34 AM)
 */
private void refreshKFluxParameters() throws ExpressionException {
	
	MathMapping_4_8.MathMappingParameter[] newMathMappingParameters = (MathMappingParameter[])fieldMathMappingParameters.clone();
	//
	// Remove existing KFlux Parameters
	//
	for (int i = 0; i < newMathMappingParameters.length; i++){
		if (newMathMappingParameters[i].getRole() == PARAMETER_ROLE_KFLUX){
			newMathMappingParameters = (MathMappingParameter[])BeanUtils.removeElement(newMathMappingParameters,newMathMappingParameters[i]);
		}		
	}
	
	
	//
	// Add new KFlux Parameters
	//
	StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();
	StructureTopology structTopology = simContext.getModel().getStructureTopology();
	VCUnitDefinition lengthInverseUnit = simContext.getModel().getX().getUnitDefinition().getInverse();
	for (int i = 0; i < structureMappings.length; i++){
		if (structureMappings[i] instanceof MembraneMapping && !getResolved(structureMappings[i])){
			MembraneMapping membraneMapping = (MembraneMapping)structureMappings[i];
			//
			// add "inside" flux correction
			//
			Expression insideCorrectionExp = getInsideFluxCorrectionExpression(membraneMapping);
			insideCorrectionExp.bindExpression(this);
			Membrane memMappingMembrane = membraneMapping.getMembrane();
			Feature insideFeature = structTopology.getInsideFeature(memMappingMembrane);
			String membraneNameWithScope = membraneMapping.getNameScope().getName();
			String insideName = "KFlux_"+membraneNameWithScope+"_"+insideFeature.getNameScope().getName();
			KFluxParameter insideKFluxParameter = new KFluxParameter(insideName,insideCorrectionExp,lengthInverseUnit,membraneMapping,insideFeature);
			newMathMappingParameters = (MathMappingParameter[])BeanUtils.addElement(newMathMappingParameters,insideKFluxParameter);

			//
			// add "outside" flux correction
			//
			Expression outsideCorrectionExp = getOutsideFluxCorrectionExpression(simContext,membraneMapping);
			outsideCorrectionExp.bindExpression(this);
			Feature outsideFeature = structTopology.getOutsideFeature(memMappingMembrane);
			String outsideName = "KFlux_"+membraneNameWithScope+"_"+outsideFeature.getNameScope().getName();
			KFluxParameter outsideKFluxParameter = new KFluxParameter(outsideName,outsideCorrectionExp,lengthInverseUnit,membraneMapping,outsideFeature);
			newMathMappingParameters = (MathMappingParameter[])BeanUtils.addElement(newMathMappingParameters,outsideKFluxParameter);
		}
	}
	try {
		setMathMapppingParameters(newMathMappingParameters);
	}catch (java.beans.PropertyVetoException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
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
			Expression mpExp = new Expression(mp.getName()+";");
			exp2.substituteInPlace(mpExp,new Expression(mp.getExpression()));
		}
	}
	exp2.bindExpression(simContext.getModel());
	return exp2;
}

protected SubVolume getSubVolume(FeatureMapping featureMapping) {
	if (featureMapping.getGeometryClass() == null){
		return null;
	}
	
	if (featureMapping.getGeometryClass() instanceof SubVolume){
		return (SubVolume)featureMapping.getGeometryClass();
	}
	
	throw new RuntimeException("Feature '"+featureMapping.getFeature().getName()+"' not mapped to a volume, not supported by VCell 4.8 math generation");
}

/**
 * This method was created in VisualAge.
 */
private void refreshMathDescription() throws MappingException, MatrixException, MathException, ExpressionException, ModelException {

	//All sizes must be set for new ODE models and ratios must be set for old ones.
	simContext.checkValidity();
	
	//
	// temporarily place all variables in a hashtable (before binding) and discarding duplicates (check for equality)
	//
	VariableHash varHash = new VariableHash();
	StructureMapping structureMappings[] = simContext.getGeometryContext().getStructureMappings();
	
	Model model = simContext.getModel();
	StructureTopology structTopology = model.getStructureTopology();
	//
	// verify that all structures are mapped to subvolumes and all subvolumes are mapped to a structure
	//
	Structure structures[] = simContext.getGeometryContext().getModel().getStructures();
	for (int i = 0; i < structures.length; i++){
		StructureMapping sm = simContext.getGeometryContext().getStructureMapping(structures[i]);
		if (sm==null || (sm instanceof FeatureMapping && getSubVolume((FeatureMapping)sm) == null)){
			throw new MappingException("model structure '"+structures[i].getName()+"' not mapped to a geometry subdomain");
		}
		if (sm!=null && (sm instanceof MembraneMapping) && ((MembraneMapping)sm).getVolumeFractionParameter()!=null){
			Expression volFractExp = ((MembraneMapping)sm).getVolumeFractionParameter().getExpression();
			if(volFractExp != null)
			{
				try {
					double volFract = volFractExp.evaluateConstant();
					if (volFract>=1.0){
						throw new MappingException("model structure '"+structTopology.getInsideFeature(((MembraneMapping)sm).getMembrane()).getName()+"' has volume fraction >= 1.0");
					}
				}catch (ExpressionException e){
				}
			}
		}
	}
	SubVolume subVolumes[] = simContext.getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
	for (int i = 0; i < subVolumes.length; i++){
		if (getStructures(subVolumes[i])==null || getStructures(subVolumes[i]).length==0){
			throw new MappingException("geometry subdomain '"+subVolumes[i].getName()+"' not mapped from a model structure");
		}
	}

	// deals with model parameters
	Hashtable<VolVariable, EventAssignmentInitParameter> eventVolVarHash = new Hashtable<VolVariable, EventAssignmentInitParameter>();
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
		for (int j=0;j<modelParameters.length;j++){
			Expression modelParamExpr = getIdentifierSubstitutions(modelParameters[j].getExpression(), modelParameters[j].getUnitDefinition(), null);
			if (eventAssignTargets.contains(modelParameters[j])) {
				EventAssignmentInitParameter eap = null;
				try {
					eap = addEventAssignmentInitParameter(modelParameters[j].getName(), modelParameters[j].getExpression(), 
							PARAMETER_ROLE_EVENTASSIGN_INITCONDN, modelParameters[j].getUnitDefinition());
				} catch (PropertyVetoException e) {
					e.printStackTrace(System.out);
					throw new MappingException(e.getMessage());
				}
				// varHash.addVariable(newFunctionOrConstant(getMathSymbol(eap, null), modelParamExpr));
				VolVariable volVar = new VolVariable(modelParameters[j].getName(),nullDomain);
				varHash.addVariable(volVar);
				eventVolVarHash.put(volVar, eap);
			} else {
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(modelParameters[j], null), modelParamExpr));
			}
		}
	} else {
		// populate in globalParameterVariants hashtable
		//
		// to accommodate forward references, we will do this processing in two passes.
		// 1) first pass, create list of model parameter variants without defining expressions (dummy expression VCELL_TEMPORARY_EXPRESSION_PLACEHOLDER).
		// 2) second pass, compute proper expressions that can have forward references (that were defined in pass 1).
		//
		for (int pass = 0; pass < 2; pass ++ ){
			for (int j = 0; j < modelParameters.length; j++){
				Hashtable<String, Expression> structMappingVariantsHash = new Hashtable<String, Expression>();
				for (int k = 0; k < structureMappings.length; k++) {
					String paramVariantName = null;
					Expression paramVariantExpr = null;
					if (modelParameters[j].getExpression().getSymbols() == null) {
						paramVariantName = modelParameters[j].getName();
						paramVariantExpr = getIdentifierSubstitutions(modelParameters[j].getExpression(), modelParameters[j].getUnitDefinition(), null);
					} else {
						paramVariantName = modelParameters[j].getName()+"_"+TokenMangler.fixTokenStrict(structureMappings[k].getStructure().getName());
						// if the expression has symbols that do not belong in that structureMapping, do not create the variant.
						Expression exp1 = modelParameters[j].getExpression();
						Expression flattenedModelParamExpr = substituteGlobalParameters(exp1); 
						String[] symbols = flattenedModelParamExpr.getSymbols();
						boolean bValid = true;
						Structure sm_struct = structureMappings[k].getStructure();
						if (symbols != null) {
							for (int ii = 0; ii < symbols.length; ii++) {
								SpeciesContext sc = model.getSpeciesContext(symbols[ii]); 
								if (sc != null) {
									// symbol[ii] is a speciesContext, check its structure with structureMapping[k].structure. If they are the same or
									// if it is the adjacent membrane(s), allow variant expression to be created. Else, continue.
									Structure sp_struct = sc.getStructure();
									if (sp_struct.compareEqual(sm_struct)) {
										bValid = bValid && true;
									} else {
										// if the 2 structures are not the same, are they adjacent? then 'bValid' is true, else false.
										if ((sm_struct instanceof Feature) && (sp_struct instanceof Membrane)) {
											Feature sm_feature = (Feature)sm_struct;
											Membrane sp_mem = (Membrane)sp_struct;
											if (sp_mem.compareEqual(structTopology.getParentStructure(sm_feature)) || (structTopology.getInsideFeature(sp_mem).compareEqual(sm_feature) || 
													structTopology.getOutsideFeature(sp_mem).compareEqual(sm_feature))) {
												bValid = bValid && true;
											} else {
												bValid = bValid && false;
												break;
											}
										} else if ((sm_struct instanceof Membrane) && (sp_struct instanceof Feature)) {
											Feature sp_feature = (Feature)sp_struct;
											Membrane sm_mem = (Membrane)sm_struct;
											if (sm_mem.compareEqual(structTopology.getParentStructure(sp_feature)) || (structTopology.getInsideFeature(sm_mem).compareEqual(sp_feature) || 
													structTopology.getOutsideFeature(sm_mem).compareEqual(sp_feature))) {
												bValid = bValid && true;
											} else {
												bValid = bValid && false;
												break;
											}
										} else {
											bValid = bValid && false;
											break;
										}
									}
								} 
							}
						}
						if (bValid) {
							if (pass==0){
								paramVariantExpr = new Expression("VCELL_TEMPORARY_EXPRESSION_PLACEHOLDER");
							}else{
								paramVariantExpr = getIdentifierSubstitutions(modelParameters[j].getExpression(), modelParameters[j].getUnitDefinition(), structureMappings[k]);
							}
						}
					}
					if (paramVariantExpr != null) {
						structMappingVariantsHash.put(paramVariantName, paramVariantExpr);
					}
				}
				globalParamVariantsHash.put(modelParameters[j], structMappingVariantsHash);
			}
		}
		//
		// global parameters from model add all variants (due to different structureMappings)
		//
		for (int j=0;j<modelParameters.length;j++){
			if (modelParameters[j].getExpression().getSymbols() == null) {
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(modelParameters[j], null), getIdentifierSubstitutions(modelParameters[j].getExpression(),modelParameters[j].getUnitDefinition(),null)));
			} else {
				Hashtable<String, Expression> smVariantsHash = globalParamVariantsHash.get(modelParameters[j]);
				for (int k = 0; k < structureMappings.length; k++) {
					String variantName = modelParameters[j].getName()+"_"+TokenMangler.fixTokenStrict(structureMappings[k].getStructure().getName());
					Expression variantExpr = smVariantsHash.get(variantName);
					if (variantExpr != null) {
						varHash.addVariable(newFunctionOrConstant(variantName, variantExpr));
					}
				}
			}
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
			throw new MappingException(reactionSteps[i].getDisplayType()+" '"+reactionSteps[i].getName()+"' contains unresolved identifier(s): "+buffer);
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
	//(simContext.getGeometry().getDimension() == 0);
	potentialMapping = new PotentialMapping(simContext,this);
	potentialMapping.computeMath();
	
	if (bCalculatePotential){
		//
		// copy functions for currents and constants for capacitances
		//
		ElectricalDevice devices[] = potentialMapping.getElectricalDevices();
		for (int j = 0; j < devices.length; j++){

			
			if (devices[j] instanceof MembraneElectricalDevice){
				MembraneElectricalDevice membraneElectricalDevice = (MembraneElectricalDevice)devices[j];
				MembraneMapping memMapping = membraneElectricalDevice.getMembraneMapping();
				Parameter specificCapacitanceParm = memMapping.getParameterFromRole(MembraneMapping.ROLE_SpecificCapacitance);
				varHash.addVariable(new Constant(getMathSymbol(specificCapacitanceParm,memMapping),getIdentifierSubstitutions(specificCapacitanceParm.getExpression(),specificCapacitanceParm.getUnitDefinition(),memMapping)));

				ElectricalDevice.ElectricalDeviceParameter transmembraneCurrentParm = membraneElectricalDevice.getParameterFromRole(ElectricalDevice.ROLE_TransmembraneCurrent);
				ElectricalDevice.ElectricalDeviceParameter totalCurrentParm = membraneElectricalDevice.getParameterFromRole(ElectricalDevice.ROLE_TotalCurrent);
				ElectricalDevice.ElectricalDeviceParameter capacitanceParm = membraneElectricalDevice.getParameterFromRole(ElectricalDevice.ROLE_Capacitance);

				if (totalCurrentParm!=null && /* totalCurrentDensityParm.getExpression()!=null && */ memMapping.getCalculateVoltage()){
					Expression totalCurrentDensityExp = (totalCurrentParm.getExpression()!=null)?(totalCurrentParm.getExpression()):(new Expression(0.0));
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrentParm,membraneElectricalDevice.getMembraneMapping()),
							getIdentifierSubstitutions(totalCurrentDensityExp,totalCurrentParm.getUnitDefinition(),membraneElectricalDevice.getMembraneMapping())));
				}
				if (transmembraneCurrentParm!=null && transmembraneCurrentParm.getExpression()!=null && memMapping.getCalculateVoltage()){
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(transmembraneCurrentParm,membraneElectricalDevice.getMembraneMapping()),
							getIdentifierSubstitutions(transmembraneCurrentParm.getExpression(),transmembraneCurrentParm.getUnitDefinition(),membraneElectricalDevice.getMembraneMapping())));
				}
				if (capacitanceParm!=null && capacitanceParm.getExpression()!=null && memMapping.getCalculateVoltage()){
					StructureMappingParameter sizeParameter = membraneElectricalDevice.getMembraneMapping().getSizeParameter();
					if (simContext.getGeometry().getDimension() == 0 && (sizeParameter.getExpression() == null || sizeParameter.getExpression().isZero())) {
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(capacitanceParm,membraneElectricalDevice.getMembraneMapping()),
							getIdentifierSubstitutions(Expression.mult(memMapping.getNullSizeParameterValue(), specificCapacitanceParm.getExpression()),capacitanceParm.getUnitDefinition(),membraneElectricalDevice.getMembraneMapping())));						
					} else {
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(capacitanceParm,membraneElectricalDevice.getMembraneMapping()),
							getIdentifierSubstitutions(capacitanceParm.getExpression(),capacitanceParm.getUnitDefinition(),membraneElectricalDevice.getMembraneMapping())));
					}
				}
				//
				//
				// membrane ode
				//
				if (membraneElectricalDevice.getDependentVoltageExpression()==null){  // is Voltage Independent?
					StructureMapping.StructureMappingParameter initialVoltageParm = memMapping.getInitialVoltageParameter();
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(initialVoltageParm,memMapping),
													getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),memMapping)));
				}
				//
				// membrane forced potential
				//
				else {
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(memMapping.getMembrane().getMembraneVoltage(),memMapping),
													getIdentifierSubstitutions(membraneElectricalDevice.getDependentVoltageExpression(),memMapping.getMembrane().getMembraneVoltage().getUnitDefinition(),memMapping)));
				}
			}else if (devices[j] instanceof CurrentClampElectricalDevice){
				CurrentClampElectricalDevice currentClampDevice = (CurrentClampElectricalDevice)devices[j];
				// total current = current source (no capacitance)
				Parameter totalCurrentParm = currentClampDevice.getParameterFromRole(CurrentClampElectricalDevice.ROLE_TotalCurrent);
				Parameter currentParm = currentClampDevice.getParameterFromRole(CurrentClampElectricalDevice.ROLE_TransmembraneCurrent);
				//Parameter dependentVoltage = currentClampDevice.getCurrentClampStimulus().getVoltageParameter();
				
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrentParm,null),getIdentifierSubstitutions(totalCurrentParm.getExpression(),totalCurrentParm.getUnitDefinition(),null)));
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(currentParm,null),getIdentifierSubstitutions(currentParm.getExpression(),currentParm.getUnitDefinition(),null)));
				//varHash.addVariable(newFunctionOrConstant(getMathSymbol(dependentVoltage,null),getIdentifierSubstitutions(currentClampDevice.getDependentVoltageExpression(),dependentVoltage.getUnitDefinition(),null)));

				//
				// add user-defined parameters
				//
				ElectricalDevice.ElectricalDeviceParameter[] parameters = currentClampDevice.getParameters();
				for (int k = 0; k < parameters.length; k++){
					if (parameters[k].getExpression()!=null){ // guards against voltage parameters that are "variable".
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameters[k],null),getIdentifierSubstitutions(parameters[k].getExpression(),parameters[k].getUnitDefinition(),null)));
					}
				}
			}else if (devices[j] instanceof VoltageClampElectricalDevice){
				VoltageClampElectricalDevice voltageClampDevice = (VoltageClampElectricalDevice)devices[j];
				// total current = current source (no capacitance)
				Parameter totalCurrent = voltageClampDevice.getParameterFromRole(VoltageClampElectricalDevice.ROLE_TotalCurrent);
				Parameter totalCurrentParm = voltageClampDevice.getParameterFromRole(VoltageClampElectricalDevice.ROLE_TotalCurrent);
				Parameter voltageParm = voltageClampDevice.getParameterFromRole(VoltageClampElectricalDevice.ROLE_Voltage);
				
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrent,null),getIdentifierSubstitutions(totalCurrent.getExpression(),totalCurrent.getUnitDefinition(),null)));
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(totalCurrentParm,null),getIdentifierSubstitutions(totalCurrentParm.getExpression(),totalCurrentParm.getUnitDefinition(),null)));
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(voltageParm,null),getIdentifierSubstitutions(voltageParm.getExpression(),voltageParm.getUnitDefinition(),null)));

				//
				// add user-defined parameters
				//
				ElectricalDevice.ElectricalDeviceParameter[] parameters = voltageClampDevice.getParameters();
				for (int k = 0; k < parameters.length; k++){
					if (parameters[k].getRole() == ElectricalDevice.ROLE_UserDefined){
						varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameters[k],null),getIdentifierSubstitutions(parameters[k].getExpression(),parameters[k].getUnitDefinition(),null)));
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
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(memMapping.getMembrane().getMembraneVoltage(),memMapping),getIdentifierSubstitutions(memMapping.getInitialVoltageParameter().getExpression(),memMapping.getInitialVoltageParameter().getUnitDefinition(),memMapping)));
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
					if (membraneMapping.getCalculateVoltage() && bCalculatePotential){
						if (getResolved(membraneMapping)){
							//
							// spatially resolved membrane, and must solve for potential .... 
							//   make single MembraneRegionVariable for all resolved potentials
							//
							if (mathDesc.getVariable(Membrane.MEMBRANE_VOLTAGE_REGION_NAME)==null){
								//varHash.addVariable(new MembraneRegionVariable(MembraneVoltage.MEMBRANE_VOLTAGE_REGION_NAME));
								varHash.addVariable(new MembraneRegionVariable(getMathSymbol(membraneVoltage,membraneMapping),nullDomain));
							}
						}else{
							//
							// spatially unresolved membrane, and must solve for potential ... make VolVariable for this compartment
							//
							varHash.addVariable(new VolVariable(getMathSymbol(membraneVoltage,membraneMapping),nullDomain));
						}
						Parameter initialVoltageParm = membraneMapping.getInitialVoltageParameter();
						Variable initVoltageFunction = newFunctionOrConstant(getMathSymbol(initialVoltageParm,membraneMapping),getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),membraneMapping));
						varHash.addVariable(initVoltageFunction);
					}else{
						//
						// don't calculate voltage, still may need it though
						//
						Parameter initialVoltageParm = membraneMapping.getInitialVoltageParameter();
						Variable voltageFunction = newFunctionOrConstant(getMathSymbol(membraneMapping.getMembrane().getMembraneVoltage(),membraneMapping),getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),membraneMapping));
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
		StructureMapping sm = simContext.getGeometryContext().getStructureMapping(rs.getStructure());
		if (parameters != null){
			for (int i=0;i<parameters.length;i++){
				if (((parameters[i].getRole() == Kinetics.ROLE_CurrentDensity)||(parameters[i].getRole() == Kinetics.ROLE_LumpedCurrent)) && (parameters[i].getExpression()==null || parameters[i].getExpression().isZero())){
					continue;
				}
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(parameters[i],sm), getIdentifierSubstitutions(parameters[i].getExpression(),parameters[i].getUnitDefinition(),sm)));
			}
		}
	}
	//
	// initial constants (either function or constant)
	//
	SpeciesContextSpec speciesContextSpecs[] = simContext.getReactionContext().getSpeciesContextSpecs();
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextSpecParameter initParm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration);
		if (initParm!=null){
			Expression initExpr = new Expression(initParm.getExpression());
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
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
						SpeciesContextSpec spcspec = simContext.getReactionContext().getSpeciesContextSpec(spC);
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
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(initParm,sm),getIdentifierSubstitutions(initExpr,initParm.getUnitDefinition(),sm)));
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
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(diffParm,sm),getIdentifierSubstitutions(diffParm.getExpression(),diffParm.getUnitDefinition(),sm)));
		}
	}

	//
	// Boundary conditions (either function or constant)
	//
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextSpec.SpeciesContextSpecParameter bc_xm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueXm);
		StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
		if (bc_xm!=null && (bc_xm.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_xm,sm),getIdentifierSubstitutions(bc_xm.getExpression(),bc_xm.getUnitDefinition(),sm)));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter bc_xp = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueXp);
		if (bc_xp!=null && (bc_xp.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_xp,sm),getIdentifierSubstitutions(bc_xp.getExpression(),bc_xp.getUnitDefinition(),sm)));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter bc_ym = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueYm);
		if (bc_ym!=null && (bc_ym.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_ym,sm),getIdentifierSubstitutions(bc_ym.getExpression(),bc_ym.getUnitDefinition(),sm)));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter bc_yp = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueYp);
		if (bc_yp!=null && (bc_yp.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_yp,sm),getIdentifierSubstitutions(bc_yp.getExpression(),bc_yp.getUnitDefinition(),sm)));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter bc_zm = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueZm);
		if (bc_zm!=null && (bc_zm.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_zm,sm),getIdentifierSubstitutions(bc_zm.getExpression(),bc_zm.getUnitDefinition(),sm)));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter bc_zp = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_BoundaryValueZp);
		if (bc_zp!=null && (bc_zp.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(bc_zp,sm),getIdentifierSubstitutions(bc_zp.getExpression(),bc_zp.getUnitDefinition(),sm)));
		}
	}

	
	//
	// advection terms (either function or constant)
	//
	for (int i = 0; i < speciesContextSpecs.length; i++){
		SpeciesContextSpec.SpeciesContextSpecParameter advection_velX = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_VelocityX);
		StructureMapping sm = simContext.getGeometryContext().getStructureMapping(speciesContextSpecs[i].getSpeciesContext().getStructure());
		if (advection_velX!=null && (advection_velX.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(advection_velX,sm),getIdentifierSubstitutions(advection_velX.getExpression(),advection_velX.getUnitDefinition(),sm)));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter advection_velY = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_VelocityY);
		if (advection_velY!=null && (advection_velY.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(advection_velY,sm),getIdentifierSubstitutions(advection_velY.getExpression(),advection_velY.getUnitDefinition(),sm)));
		}
		SpeciesContextSpec.SpeciesContextSpecParameter advection_velZ = speciesContextSpecs[i].getParameterFromRole(SpeciesContextSpec.ROLE_VelocityZ);
		if (advection_velZ!=null && (advection_velZ.getExpression() != null)){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(advection_velZ,sm),getIdentifierSubstitutions(advection_velZ.getExpression(),advection_velZ.getUnitDefinition(),sm)));
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
	varHash.addVariable(new Constant(model.getN_PMOLE().getName(),getIdentifierSubstitutions(model.getN_PMOLE().getExpression(),model.getN_PMOLE().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(model.getKMILLIVOLTS().getName(),getIdentifierSubstitutions(model.getKMILLIVOLTS().getExpression(),model.getKMILLIVOLTS().getUnitDefinition(),null)));
	varHash.addVariable(new Constant(model.getK_GHK().getName(),getIdentifierSubstitutions(model.getK_GHK().getExpression(),model.getK_GHK().getUnitDefinition(),null)));
	//
	// geometric functions
	//
	ModelUnitSystem modelUnitSystem = simContext.getModel().getUnitSystem();
	VCUnitDefinition lengthInverseUnit = modelUnitSystem.getLengthUnit().getInverse();
	for (int i=0;i<structureMappings.length;i++){
		StructureMapping sm = structureMappings[i];
		
		Parameter parm = sm.getParameterFromRole(StructureMapping.ROLE_VolumeFraction);
		if (parm!=null && parm.getExpression()!=null && sm.getGeometryClass() instanceof SubVolume){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm)));
		}
		parm = sm.getParameterFromRole(StructureMapping.ROLE_SurfaceToVolumeRatio);
		if (parm!=null && parm.getExpression()!=null && sm.getGeometryClass() instanceof SubVolume){
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm)));
		}
		if (sm instanceof MembraneMapping && !getResolved(sm)){
			MembraneMapping mm = (MembraneMapping)sm;
			parm = ((MembraneMapping)sm).getVolumeFractionParameter();
			if (parm.getExpression()==null){
				throw new MappingException("volume fraction not specified for feature '" + structTopology.getInsideFeature(mm.getMembrane()).getName()+"', please refer to Structure Mapping in Application '"+simContext.getName()+"'");
			}
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(), modelUnitSystem.getInstance_DIMENSIONLESS(), sm)));
			parm = mm.getSurfaceToVolumeParameter();
			if (parm.getExpression()==null){
				throw new MappingException("surface to volume ratio not specified for membrane '"+mm.getMembrane().getName()+"', please refer to Structure Mapping in Application '"+simContext.getName()+"'");
			}
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(), lengthInverseUnit, sm)));
		}
		StructureMappingParameter sizeParm = sm.getSizeParameter();
		if (sizeParm!=null){
			if (simContext.getGeometry().getDimension()==0){
				if (sizeParm.getExpression()!=null){
					try {
						double value = sizeParm.getExpression().evaluateConstant();
						varHash.addVariable(new Constant(getMathSymbol(sizeParm,sm),new Expression(value)));
					}catch (ExpressionException e){
						//varHash.addVariable(new Function(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(),parm.getUnitDefinition(),sm)));
						e.printStackTrace(System.out);
						throw new MappingException("Size of structure:"+sm.getNameScope().getName()+" cannot be evaluated as constant.");
					}
				}
			}else{
				String compartmentName = null;
				VCUnitDefinition sizeUnit = sm.getSizeParameter().getUnitDefinition();
				String sizeFunctionName = null;
				if (sm instanceof MembraneMapping){
					MembraneMapping mm = (MembraneMapping)sm;
					if (getResolved(mm)){
						FeatureMapping fm_inside = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(structTopology.getInsideFeature(mm.getMembrane()));
						FeatureMapping fm_outside = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(structTopology.getOutsideFeature(mm.getMembrane()));
						compartmentName = getSubVolume(fm_inside).getName()+"_"+getSubVolume(fm_outside).getName();
						sizeFunctionName = MathFunctionDefinitions.Function_regionArea_current.getFunctionName();
					}else{
						FeatureMapping fm_inside = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(structTopology.getInsideFeature(mm.getMembrane()));
						FeatureMapping fm_outside = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(structTopology.getOutsideFeature(mm.getMembrane()));
						if (getSubVolume(fm_inside)==getSubVolume(fm_outside)){
							compartmentName = getSubVolume(fm_inside).getName();
							sizeFunctionName = MathFunctionDefinitions.Function_regionVolume_current.getFunctionName();
						}else{
							throw new RuntimeException("unexpected structure mapping for membrane '"+mm.getMembrane().getName()+"'");
						}
					}
				}else if (sm instanceof FeatureMapping){
					FeatureMapping fm = (FeatureMapping)sm;
					compartmentName = getSubVolume(fm).getName();
					sizeFunctionName = MathFunctionDefinitions.Function_regionVolume_current.getFunctionName();
				}else{
					throw new RuntimeException("structure mapping "+sm.getClass().getName()+" not yet supported");
				}
				Expression totalVolumeCorrection = sm.getStructureSizeCorrection(simContext,this);
				Expression sizeFunctionExpression = Expression.function(sizeFunctionName, new Expression[] {new Expression("'"+compartmentName+"'")} );
				sizeFunctionExpression.bindExpression(mathDesc);
				varHash.addVariable(newFunctionOrConstant(getMathSymbol(sizeParm,sm),getIdentifierSubstitutions(Expression.mult(totalVolumeCorrection,sizeFunctionExpression),sizeUnit,sm)));
				
				parm = sm.getParameterFromRole(StructureMapping.ROLE_AreaPerUnitArea);
				if (parm!=null && parm.getExpression()!=null && sm.getGeometryClass() instanceof SurfaceClass){
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm)));
				}
				parm = sm.getParameterFromRole(StructureMapping.ROLE_AreaPerUnitVolume);
				if (parm!=null && parm.getExpression()!=null && sm.getGeometryClass() instanceof SubVolume){
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm)));
				}
				parm = sm.getParameterFromRole(StructureMapping.ROLE_VolumePerUnitArea);
				if (parm!=null && parm.getExpression()!=null && sm.getGeometryClass() instanceof SurfaceClass){
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm)));
				}
				parm = sm.getParameterFromRole(StructureMapping.ROLE_VolumePerUnitVolume);
				if (parm!=null && parm.getExpression()!=null && sm.getGeometryClass() instanceof SubVolume){
					varHash.addVariable(newFunctionOrConstant(getMathSymbol(parm,sm),getIdentifierSubstitutions(parm.getExpression(), parm.getUnitDefinition(), sm)));
				}
			}
		}
	}
	//
	// conserved constants  (e.g. K = A + B + C) (these are treated as functions now)
	//
	for (int i = 0; i < fieldMathMappingParameters.length; i++){
		varHash.addVariable(newFunctionOrConstant(getMathSymbol(fieldMathMappingParameters[i],null),getIdentifierSubstitutions(fieldMathMappingParameters[i].getExpression(),fieldMathMappingParameters[i].getUnitDefinition(),null)));
	}

	//
	// functions
	//
	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = (SpeciesContextMapping)enum1.nextElement();
		if (scm.getVariable()==null && scm.getDependencyExpression()!=null){
			StructureMapping sm = simContext.getGeometryContext().getStructureMapping(scm.getSpeciesContext().getStructure());
			varHash.addVariable(newFunctionOrConstant(getMathSymbol(scm.getSpeciesContext(),sm),getIdentifierSubstitutions(scm.getDependencyExpression(),scm.getSpeciesContext().getUnitDefinition(),sm)));
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
	VCUnitDefinition timeUnit = modelUnitSystem.getTimeUnit();
	for (int j=0;j<subVolumes.length;j++){
		SubVolume subVolume = (SubVolume)subVolumes[j];
		//
		// get priority of subDomain
		//
		int priority;
		Feature spatialFeature = getResolvedFeature(subVolume);
		if (spatialFeature==null){
			if (simContext.getGeometryContext().getGeometry().getDimension()>0){
				throw new MappingException("no compartment (in Physiology) is mapped to subdomain '"+subVolume.getName()+"' (in Geometry)");
			}else{
				priority = CompartmentSubDomain.NON_SPATIAL_PRIORITY;
			}
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
		if (spatialFeature != null){
			FeatureMapping fm = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(spatialFeature);
			subDomain.setBoundaryConditionXm(fm.getBoundaryConditionTypeXm());
			subDomain.setBoundaryConditionXp(fm.getBoundaryConditionTypeXp());
			if (simContext.getGeometry().getDimension()>1){
				subDomain.setBoundaryConditionYm(fm.getBoundaryConditionTypeYm());
				subDomain.setBoundaryConditionYp(fm.getBoundaryConditionTypeYp());
			}
			if (simContext.getGeometry().getDimension()>2){
				subDomain.setBoundaryConditionZm(fm.getBoundaryConditionTypeZm());
				subDomain.setBoundaryConditionZp(fm.getBoundaryConditionTypeZp());
			}
		}

		//
		// create equations
		//
		VolumeStructureAnalyzer structureAnalyzer = getVolumeStructureAnalyzer(subVolume);
		Enumeration<SpeciesContextMapping> enumSCM = getSpeciesContextMappings();
		while (enumSCM.hasMoreElements()){
			SpeciesContextMapping scm = enumSCM.nextElement();
			//
			// if an independent volume variable, then create equation for it
			// ...even if the speciesContext is for another subDomain (e.g. CaCyt in Extracellular)
			//
			if (scm.getVariable() instanceof VolVariable && scm.getDependencyExpression()==null){
				SpeciesContext        sc  = scm.getSpeciesContext();
				StructureMapping      sm  = simContext.getGeometryContext().getStructureMapping(sc.getStructure());
				SpeciesContextSpec    scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
				VolVariable variable = (VolVariable)scm.getVariable();
				Equation equation = null;
				if ( (scm.isPDERequired()) && sm instanceof FeatureMapping){
					//
					// PDE
					//
					if (getSubVolume((FeatureMapping)sm) == subVolume){
						//
						// species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),sm));
						Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(sc.getStructure()));
						Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),sm));
						equation = new PdeEquation(variable,initial,rate,diffusion);
						((PdeEquation)equation).setBoundaryXm((scs.getBoundaryXmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryXmParameter(),sm)));
						((PdeEquation)equation).setBoundaryXp((scs.getBoundaryXpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryXpParameter(),sm)));
						((PdeEquation)equation).setBoundaryYm((scs.getBoundaryYmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryYmParameter(),sm)));
						((PdeEquation)equation).setBoundaryYp((scs.getBoundaryYpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryYpParameter(),sm)));
						((PdeEquation)equation).setBoundaryZm((scs.getBoundaryZmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryZmParameter(),sm)));
						((PdeEquation)equation).setBoundaryZp((scs.getBoundaryZpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryZpParameter(),sm)));
						
						((PdeEquation)equation).setVelocityX((scs.getVelocityXParameter().getExpression()==null)?(null) : new Expression(getMathSymbol(scs.getVelocityXParameter(),sm)));
						((PdeEquation)equation).setVelocityY((scs.getVelocityYParameter().getExpression()==null)?(null) : new Expression(getMathSymbol(scs.getVelocityYParameter(),sm)));
						((PdeEquation)equation).setVelocityZ((scs.getVelocityZParameter().getExpression()==null)?(null) : new Expression(getMathSymbol(scs.getVelocityZParameter(),sm)));
						
						subDomain.replaceEquation(equation);
					}else{
						Expression initial = new Expression(0.0);
						Expression rate = new Expression(0.0);
						Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),sm));
						equation = new PdeEquation(variable,initial,rate,diffusion);
						if (subDomain.getEquation(variable)==null){
							subDomain.addEquation(equation);
						}
					}
				}else{
					//
					// ODE
					//
					SubVolume mappedSubVolume = null;
					if (sm instanceof FeatureMapping){
						mappedSubVolume = getSubVolume((FeatureMapping)sm);
					}else if (sm instanceof MembraneMapping){
						// membrane is mapped to that of the inside feature
						FeatureMapping featureMapping = (FeatureMapping)simContext.getGeometryContext().getStructureMapping(structTopology.getInsideFeature((Membrane)sm.getStructure()));
						mappedSubVolume = getSubVolume(featureMapping);
					}
					if (mappedSubVolume == subVolume){
						//
						// species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),null));
						Expression rate = (scm.getRate()==null) ? new Expression(0.0) : getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(sc.getStructure()));
						equation = new OdeEquation(variable,initial,rate);
						subDomain.replaceEquation(equation);
					}else{
						Expression initial = new Expression(0.0);
						Expression rate = new Expression(0.0);
						equation = new OdeEquation(variable,initial,rate);
						if (subDomain.getEquation(variable)==null){
							subDomain.addEquation(equation);
						}
					}
				}
			}
		}
		//
		// create fast system (if neccessary)
		//
		SpeciesContextMapping fastSpeciesContextMappings[] = structureAnalyzer.getFastSpeciesContextMappings();
		VCUnitDefinition subDomainUnit = modelUnitSystem.getVolumeConcentrationUnit();
		if (fastSpeciesContextMappings!=null){
			FastSystem fastSystem = new FastSystem(mathDesc);
			for (int i=0;i<fastSpeciesContextMappings.length;i++){
				SpeciesContextMapping scm = fastSpeciesContextMappings[i];
				if (scm.getFastInvariant()==null){
					//
					// independant-fast variable, create a fastRate object
					//
					Expression rate = getIdentifierSubstitutions(scm.getFastRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(getResolvedFeature(subVolume)));
					FastRate fastRate = new FastRate(rate);
					fastSystem.addFastRate(fastRate);
				}else{
					//
					// dependant-fast variable, create a fastInvariant object
					//
					Expression rate = getIdentifierSubstitutions(scm.getFastInvariant(),subDomainUnit,simContext.getGeometryContext().getStructureMapping(getResolvedFeature(subVolume)));
					FastInvariant fastInvariant = new FastInvariant(rate);
					fastSystem.addFastInvariant(fastInvariant);
				}
			}
			subDomain.setFastSystem(fastSystem);
			// constructor calls the 'refresh' method which constructs depemdency matrix, dependent/independent vars and pseudoconstants, etc. 
			FastSystemAnalyzer fs_analyzer = new FastSystemAnalyzer(fastSystem, mathDesc);
		}
		//
		// create ode's for voltages to be calculated on unresolved membranes mapped to this subVolume
		//
		Structure localStructures[] = getStructures(subVolume);
		for (int sIndex = 0; sIndex < localStructures.length; sIndex++){
			if (localStructures[sIndex] instanceof Membrane){
				Membrane membrane = (Membrane)localStructures[sIndex];
				MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
				if (!getResolved(membraneMapping) && membraneMapping.getCalculateVoltage()){
					MembraneElectricalDevice capacitiveDevice = potentialMapping.getCapacitiveDevice(membrane);
					if (capacitiveDevice.getDependentVoltageExpression()==null){
						VolVariable vVar = (VolVariable)mathDesc.getVariable(getMathSymbol(capacitiveDevice.getVoltageSymbol(),membraneMapping));
						Expression initExp = new Expression(getMathSymbol(capacitiveDevice.getMembraneMapping().getInitialVoltageParameter(),membraneMapping));
						subDomain.addEquation(new OdeEquation(vVar,initExp,getIdentifierSubstitutions(potentialMapping.getOdeRHS(capacitiveDevice,this), membrane.getMembraneVoltage().getUnitDefinition().divideBy(timeUnit),membraneMapping)));
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
	for (int k=0;k<subVolumes.length;k++){
		SubVolume subVolume = (SubVolume)subVolumes[k];
		//
		// if there is a spatially resolved membrane surrounding this subVolume, then create a membraneSubDomain
		//
		structures = getStructures(subVolume);
		Membrane membrane = null;
		if (structures!=null){
			for (int j=0;j<structures.length;j++){
				if (structures[j] instanceof Membrane && getResolved(simContext.getGeometryContext().getStructureMapping(structures[j]))){
					membrane = (Membrane)structures[j];
				}
			}
		}
		if (membrane==null){
			continue;
		}
		SubVolume outerSubVolume = getSubVolume(((FeatureMapping)simContext.getGeometryContext().getStructureMapping(structTopology.getOutsideFeature(membrane))));
		SubVolume innerSubVolume = getSubVolume(((FeatureMapping)simContext.getGeometryContext().getStructureMapping(structTopology.getInsideFeature(membrane))));
		if (innerSubVolume!=subVolume){
			throw new MappingException("membrane "+membrane.getName()+" improperly mapped to inner subVolume "+innerSubVolume.getName());
		}

		//
		// get priority of subDomain
		//
		//Feature spatialFeature = simContext.getGeometryContext().getResolvedFeature(subVolume);
		//int priority = spatialFeature.getPriority();
		//
		// create subDomain
		//
		CompartmentSubDomain outerCompartment = mathDesc.getCompartmentSubDomain(outerSubVolume.getName());
		CompartmentSubDomain innerCompartment = mathDesc.getCompartmentSubDomain(innerSubVolume.getName());

		SurfaceClass surfaceClass = simContext.getGeometry().getGeometrySurfaceDescription().getSurfaceClass(innerSubVolume, outerSubVolume);
		
		MembraneSubDomain memSubDomain = new MembraneSubDomain(innerCompartment,outerCompartment,surfaceClass.getName());
		mathDesc.addSubDomain(memSubDomain);

		//
		// create equations for membrane-bound molecular species
		//
		MembraneStructureAnalyzer membraneStructureAnalyzer = getMembraneStructureAnalyzer(membrane);
		Enumeration<SpeciesContextMapping> enumSCM = getSpeciesContextMappings();
		while (enumSCM.hasMoreElements()){
			SpeciesContextMapping scm = enumSCM.nextElement();
			SpeciesContext        sc  = scm.getSpeciesContext();
			SpeciesContextSpec    scs = simContext.getReactionContext().getSpeciesContextSpec(sc);
			//
			// if an independent membrane variable, then create equation for it
			// ...even if the speciesContext is for another membraneSubDomain (e.g. BradykininReceptor in NuclearMembrane)
			//
			if ((scm.getVariable() instanceof MemVariable) && scm.getDependencyExpression()==null){
				//
				// independant variable, create an equation object
				//
				Equation equation = null;
				MemVariable variable = (MemVariable)scm.getVariable();
				MembraneMapping mm = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(sc.getStructure());
				if (scm.isPDERequired()){
					//
					// PDE
					//
					if (mm.getMembrane() == membrane){
						//
						// species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),mm));
						Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(sc.getStructure()));
						Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),mm));
						equation = new PdeEquation(variable,initial,rate,diffusion);
						((PdeEquation)equation).setBoundaryXm((scs.getBoundaryXmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryXmParameter(),mm)));
						((PdeEquation)equation).setBoundaryXp((scs.getBoundaryXpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryXpParameter(),mm)));
						((PdeEquation)equation).setBoundaryYm((scs.getBoundaryYmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryYmParameter(),mm)));
						((PdeEquation)equation).setBoundaryYp((scs.getBoundaryYpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryYpParameter(),mm)));
						((PdeEquation)equation).setBoundaryZm((scs.getBoundaryZmParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryZmParameter(),mm)));
						((PdeEquation)equation).setBoundaryZp((scs.getBoundaryZpParameter().getExpression()==null)?(null):new Expression(getMathSymbol(scs.getBoundaryZpParameter(),mm)));
						memSubDomain.replaceEquation(equation);
					}else{
						Expression initial = new Expression(0.0);
						Expression rate = new Expression(0.0);
						Expression diffusion = new Expression(getMathSymbol(scs.getDiffusionParameter(),mm));
						equation = new PdeEquation(variable,initial,rate,diffusion);
						if (memSubDomain.getEquation(variable)==null){
							memSubDomain.addEquation(equation);
						}

					}
				} else {
					//
					// ODE					
					//
					if (mm.getMembrane() == membrane){
						//
						// species context belongs to this subDomain
						//
						Expression initial = new Expression(getMathSymbol(scs.getParameterFromRole(SpeciesContextSpec.ROLE_InitialConcentration),null));
						Expression rate = getIdentifierSubstitutions(scm.getRate(),scm.getSpeciesContext().getUnitDefinition().divideBy(timeUnit),simContext.getGeometryContext().getStructureMapping(sc.getStructure()));
						equation = new OdeEquation(variable,initial,rate);
						memSubDomain.replaceEquation(equation);
					}else{
						Expression initial = new Expression(0.0);
						Expression rate = new Expression(0.0);
						equation = new OdeEquation(variable,initial,rate);
						if (memSubDomain.getEquation(variable)==null){
							memSubDomain.addEquation(equation);
						}
					}
				}
			}
		}
		//
		// create dummy jump conditions for all volume variables that diffuse and/or advect
		//
		Enumeration<SpeciesContextMapping> enum_scm = getSpeciesContextMappings();
		while (enum_scm.hasMoreElements()){
			SpeciesContextMapping scm = enum_scm.nextElement();
			if (scm.isPDERequired()) {
				//Species species = scm.getSpeciesContext().getSpecies();
				Variable var = scm.getVariable();
				if (var instanceof VolVariable && (scm.isPDERequired())){
					JumpCondition jc = memSubDomain.getJumpCondition((VolVariable)var);
					if (jc==null){
//System.out.println("MathMapping.refreshMathDescription(), adding jump condition for diffusing variable "+var.getName()+" on membrane "+membraneStructureAnalyzer.getMembrane().getName());
						jc = new JumpCondition((VolVariable)var);
						memSubDomain.addJumpCondition(jc);
					}
				}
			}
		}
		//
		// create jump conditions for any volume variables that bind to membrane or have explicitly defined fluxes
		//
		ResolvedFlux resolvedFluxes[] = membraneStructureAnalyzer.getResolvedFluxes();
		if (resolvedFluxes!=null){
			for (int i=0;i<resolvedFluxes.length;i++){
				Species species = resolvedFluxes[i].getSpecies();
				SpeciesContext sc = simContext.getReactionContext().getModel().getSpeciesContext(species, structTopology.getInsideFeature(membraneStructureAnalyzer.getMembrane()));
				if (sc==null){
					sc = simContext.getReactionContext().getModel().getSpeciesContext(species, structTopology.getOutsideFeature(membraneStructureAnalyzer.getMembrane()));
				}
				SpeciesContextMapping scm = getSpeciesContextMapping(sc);
				//
				// introduce Bug Compatability mode for NoFluxIfFixed
				//
				// if (scm.getVariable() instanceof VolVariable && scm.isDiffusing()){
				if (scm.getVariable() instanceof VolVariable && ((MembraneStructureAnalyzer.bNoFluxIfFixed || (scm.isPDERequired())))){
					if (MembraneStructureAnalyzer.bNoFluxIfFixed && !scm.isPDERequired()){
						MembraneStructureAnalyzer.bNoFluxIfFixedExercised = true;
					}
					JumpCondition jc = memSubDomain.getJumpCondition((VolVariable)scm.getVariable());
					if (jc==null){
						jc = new JumpCondition((VolVariable)scm.getVariable());
						memSubDomain.addJumpCondition(jc);
					}
					Expression inFlux = getIdentifierSubstitutions(resolvedFluxes[i].inFluxExpression, resolvedFluxes[i].getUnitDefinition(),simContext.getGeometryContext().getStructureMapping(membraneStructureAnalyzer.getMembrane()));
					jc.setInFlux(inFlux);
					Expression outFlux = getIdentifierSubstitutions(resolvedFluxes[i].outFluxExpression, resolvedFluxes[i].getUnitDefinition(),simContext.getGeometryContext().getStructureMapping(membraneStructureAnalyzer.getMembrane()));
					jc.setOutFlux(outFlux);
				}else{
					throw new MappingException("APPLICATION  " + simContext.getName() + " : " + scm.getSpeciesContext().getName()+" has spatially resolved flux at membrane "+membrane.getName()+", but doesn't diffuse in compartment "+scm.getSpeciesContext().getStructure().getName());
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
					MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membraneStructureAnalyzer.getMembrane());
					FastRate fastRate = new FastRate(getIdentifierSubstitutions(scm.getFastRate(),rateUnit,membraneMapping));
					fastSystem.addFastRate(fastRate);
				}else{
					//
					// dependant-fast variable, create a fastInvariant object
					//
					VCUnitDefinition invariantUnit = scm.getSpeciesContext().getUnitDefinition();
					MembraneMapping membraneMapping = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membraneStructureAnalyzer.getMembrane());
					FastInvariant fastInvariant = new FastInvariant(getIdentifierSubstitutions(scm.getFastInvariant(),invariantUnit,membraneMapping));
					fastSystem.addFastInvariant(fastInvariant);
				}
			}
			memSubDomain.setFastSystem(fastSystem);
			// constructor calls the 'refresh' method which constructs depemdency matrix, dependent/independent vars and pseudoconstants, etc. 
			FastSystemAnalyzer fs_analyzer = new FastSystemAnalyzer(fastSystem, mathDesc);
		}
		//
		// create Membrane-region equations for potential of this resolved membrane
		//
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
			if (mathDesc.getVariable(getMathSymbol(capacitiveDevice.getVoltageSymbol(),membraneMapping)) instanceof MembraneRegionVariable){
				MembraneRegionVariable vVar = (MembraneRegionVariable)mathDesc.getVariable(getMathSymbol(capacitiveDevice.getVoltageSymbol(),membraneMapping));
				Parameter initialVoltageParm = capacitiveDevice.getMembraneMapping().getInitialVoltageParameter();
				Expression initExp = getIdentifierSubstitutions(initialVoltageParm.getExpression(),initialVoltageParm.getUnitDefinition(),capacitiveDevice.getMembraneMapping());
				MembraneRegionEquation vEquation = new MembraneRegionEquation(vVar,initExp);
				vEquation.setMembraneRateExpression(getIdentifierSubstitutions(potentialMapping.getOdeRHS(capacitiveDevice,this), membrane.getMembraneVoltage().getUnitDefinition().divideBy(timeUnit),capacitiveDevice.getMembraneMapping()));
				memSubDomain.addEquation(vEquation);
			}
		}
	}

	// create equations for event assign targets that are model params/strutureSize, etc.
	Set<VolVariable> hashKeySet = eventVolVarHash.keySet();
	Iterator<VolVariable> volVarsIter = hashKeySet.iterator();
	// working under teh assumption that we are dealing with non-spatial math, hence only one compartment domain!
	SubDomain subDomain = mathDesc.getSubDomains().nextElement();
	while (volVarsIter.hasNext()) {
		VolVariable volVar = volVarsIter.next();
		EventAssignmentInitParameter eap = eventVolVarHash.get(volVar);
		Expression rateExpr = new Expression(0.0);
		Equation equation = new OdeEquation(volVar, new Expression(getMathSymbol(eap, null)), rateExpr);
		subDomain.addEquation(equation);
	}

	// events - add events to math desc and odes for event assignments that have parameters as target variables

	BioEvent[] bioevents = simContext.getBioEvents();
	if (bioevents != null && bioevents.length > 0) {
		for (BioEvent be : bioevents) {
			// transform the bioEvent trigger/delay to math Event
			Expression mathTriggerExpr = getIdentifierSubstitutions(be.generateTriggerExpression(), modelUnitSystem.getInstance_DIMENSIONLESS(), null);
			Delay mathDelay = null;
			if (be.getParameter(BioEventParameterType.TriggerDelay) != null) {
				boolean bUseValsFromTriggerTime = be.getUseValuesFromTriggerTime();
				Expression mathDelayExpr = getIdentifierSubstitutions(be.getParameter(BioEventParameterType.TriggerDelay).getExpression(), timeUnit, null);
				mathDelay = new Delay(bUseValsFromTriggerTime, mathDelayExpr);
			}
			// now deal with (bio)event Assignment translation to math EventAssignment 
			ArrayList<EventAssignment> eventAssignments = be.getEventAssignments();
			ArrayList<Event.EventAssignment> mathEventAssignmentsList = new ArrayList<Event.EventAssignment>(); 
			for (EventAssignment ea : eventAssignments) {
				SymbolTableEntry ste = simContext.getEntry(ea.getTarget().getName());
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
	
	if (!mathDesc.isValid()){
		throw new MappingException("generated an invalid mathDescription: "+mathDesc.getWarning());
	}

//System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string begin ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
//System.out.println(mathDesc.getVCML());
//System.out.println("]]]]]]]]]]]]]]]]]]]]]] VCML string end ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
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
		if (scs.isConstant()){
			Expression initCond = scs.getInitialConditionParameter() == null? null : scs.getInitialConditionParameter().getExpression();
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
	SubVolume subVolumes[] = simContext.getGeometryContext().getGeometry().getGeometrySpec().getSubVolumes();
	for (int j=0;j<subVolumes.length;j++){
		SubVolume subVolume = (SubVolume)subVolumes[j];
		if (getVolumeStructureAnalyzer(subVolume)==null){
			structureAnalyzerList.addElement(new VolumeStructureAnalyzer(this,subVolume));
		}
		//
		// Add a MembraneStructureAnalyzer if necessary
		//
		// go through list of MembraneMappings and determine if inner and outer compartment
		// are both mapped to subVolumes, then add
		//
		Structure structures[] = getStructures(subVolume);
		if (structures!=null){
			for (int i=0;i<structures.length;i++){
				if (structures[i] instanceof Membrane){
					Membrane membrane = (Membrane)structures[i];
					MembraneMapping mm = (MembraneMapping)simContext.getGeometryContext().getStructureMapping(membrane);
					if (mm != null){
						if (getResolved(mm) && getMembraneStructureAnalyzer(membrane)==null){
							SubVolume outerSubVolume = getSubVolume(((FeatureMapping)simContext.getGeometryContext().getStructureMapping(simContext.getModel().getStructureTopology().getOutsideFeature(membrane))));
							structureAnalyzerList.addElement(new MembraneStructureAnalyzer(this,membrane,subVolume,outerSubVolume));
						}
					}
				}
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
	}

	enum1 = getSpeciesContextMappings();
	while (enum1.hasMoreElements()){
		SpeciesContextMapping scm = enum1.nextElement();
		SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(scm.getSpeciesContext());
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
			if (struct instanceof Feature){
				if (getResolved(sm)){
					scm.setVariable(getResolvedVolVariable(scm.getSpeciesContext().getSpecies()));
				}else{
					scm.setVariable(new VolVariable(scm.getSpeciesContext().getName(),nullDomain));
				}
			}else if (struct instanceof Membrane){
				if (getResolved(sm)){
					scm.setVariable(new MemVariable(scm.getSpeciesContext().getName(),nullDomain));
				}else{
					scm.setVariable(new VolVariable(scm.getSpeciesContext().getName(),nullDomain));
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
void removeMathMappingParameter(MathMapping_4_8.MathMappingParameter mathMappingParameter) throws java.beans.PropertyVetoException {
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
	MathMapping_4_8.MathMappingParameter[] oldValue = fieldMathMappingParameters;
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

public Expression getUnitFactor(VCUnitDefinition unitFactor) {
	return new Expression(unitFactor.getDimensionlessScale());
}

}
