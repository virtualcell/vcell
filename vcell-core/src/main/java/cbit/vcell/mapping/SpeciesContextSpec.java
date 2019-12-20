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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.mapping.SimulationContext.Kind;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.mapping.spatial.VolumeRegionObject;
import cbit.vcell.matrix.RationalNumber;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ExpressionContainer;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleBoundsIssue;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.VCMODL;
import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;
import cbit.vcell.units.VCUnitDefinition;
import net.sourceforge.interval.ia_math.RealInterval;

@SuppressWarnings("serial")
public class SpeciesContextSpec implements Matchable, ScopedSymbolTable, Serializable, SimulationContextEntity, IssueSource {

	public static final String PARAMETER_NAME_PROXY_PARAMETERS = "proxyParameters";
	private static final String PROPERTY_NAME_WELL_MIXED = "wellMixed";
	private static final String PROPERTY_NAME_FORCECONTINUOUS = "forceContinuous";


	public class SpeciesContextSpecNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public SpeciesContextSpecNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return SpeciesContextSpec.this.getSpeciesContext().getName()+"_scs";
		}
		public NameScope getParent() {
			if (SpeciesContextSpec.this.simulationContext != null){
				return SpeciesContextSpec.this.simulationContext.getNameScope();
			}else{
				return null;
			}
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return SpeciesContextSpec.this;
		}
		@Override
		public String getPathDescription() {
			if (simulationContext != null){
				return "App("+simulationContext.getName()+") / Species("+getSpeciesContext().getName()+")";
			}
			return null;
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.speciesContextSpecType;
		}
	}
	
	public class SpeciesContextSpecParameter extends Parameter implements ExpressionContainer, IssueSource {
		private Expression fieldParameterExpression = null;
		private String fieldParameterName = null;
 		private int fieldParameterRole = -1;
 		private VCUnitDefinition fieldUnitDefinition = null;

		public SpeciesContextSpecParameter(String parmName, Expression argExpression, int argRole, VCUnitDefinition argUnitDefinition, String argDescription) {
			super();
			fieldParameterName = parmName;
			fieldParameterExpression = argExpression;
			if (argRole >= 0 && argRole < NUM_ROLES){
				this.fieldParameterRole = argRole;
				
			}else{
				throw new IllegalArgumentException("parameter 'role' = "+argRole+" is out of range");
			}
			fieldUnitDefinition = argUnitDefinition;
			setDescription(argDescription);
		}
		
		public String getNullExpressionDescription() {
			if (fieldParameterRole == ROLE_BoundaryValueXm
					|| fieldParameterRole == ROLE_BoundaryValueXp
					|| fieldParameterRole == ROLE_BoundaryValueYm
					|| fieldParameterRole == ROLE_BoundaryValueYp
					|| fieldParameterRole == ROLE_BoundaryValueZm
					|| fieldParameterRole == ROLE_BoundaryValueZp) {
				if (fieldUnitDefinition != null) {
					VCUnitDefinition lengthPerTimeUnitDefn = getLengthPerTimeUnit();
					if (fieldUnitDefinition.isEquivalent(getSpeciesContext().getUnitDefinition())) {
						return "<html><i>&lt;initial value&gt;</i></html>"; 
					} else if (fieldUnitDefinition.isEquivalent(getSpeciesContext().getUnitDefinition().multiplyBy(lengthPerTimeUnitDefn))){
						return "<html><i>&lt;zero flux&gt;</i></html>";
					}
				}
			} else if (fieldParameterRole == ROLE_VelocityX
					|| fieldParameterRole == ROLE_VelocityY
					|| fieldParameterRole == ROLE_VelocityZ) {
				QuantityComponent component = null;
				if (fieldParameterRole == ROLE_VelocityX){
					component = QuantityComponent.X;
				}else if (fieldParameterRole == ROLE_VelocityY){
					component = QuantityComponent.Y;
				}else if (fieldParameterRole == ROLE_VelocityZ){
					component = QuantityComponent.Z;
				}
				ArrayList<String> varNames = new ArrayList<String>(); 
				StructureMapping sm = simulationContext.getGeometryContext().getStructureMapping(speciesContext.getStructure());
				if (sm!=null && sm.getGeometryClass()!=null){
					for (SpatialObject spatialObject : simulationContext.getSpatialObjects()){
						if (spatialObject instanceof VolumeRegionObject){
							VolumeRegionObject vro = (VolumeRegionObject)spatialObject;
							if (vro.getSubVolume()==sm.getGeometryClass() && vro.isQuantityCategoryEnabled(QuantityCategory.InteriorVelocity)){
								varNames.add(vro.getSpatialQuantity(QuantityCategory.InteriorVelocity, component).getName());
							}
						}
					}
				}
				if (varNames.size()>0){
					return "<html><i>&lt;"+varNames.toString().replace("[","").replace("]","").replace(","," or ")+"&gt;</i></html>";
				}else{
					return "<html><i>&lt;0.0&gt;</i></html>";
				}
			}
			return null;
		}
		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof SpeciesContextSpecParameter)){
				return false;
			}
			SpeciesContextSpecParameter smp = (SpeciesContextSpecParameter)obj;
			if (!super.compareEqual0(smp)){
				return false;
			}
			if (fieldParameterRole != smp.fieldParameterRole){
				return false;
			}
			
			return true;
		}

		public NameScope getNameScope(){
			return SpeciesContextSpec.this.getNameScope();
		}

		public boolean isExpressionEditable(){
			return true;
		}

		@Override
		public String getDescription() {
			return super.getDescription() + " for "+getSpeciesContext().getName();
		}
		
		public boolean isUnitEditable(){
			return false;
		}

		public boolean isNameEditable(){
			return false;
		}

		public VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}
		
		public void setExpression(Expression expression) throws ExpressionBindingException {
			if (expression!=null){
				expression = new Expression(expression);
				// Need to bind this expression, but changing the binding from SpeciesContextSpec.this to a symbolTable created on the fly.
				// This new symbolTable is this SpeciesContextSpec, but omits its parameters, so that the SpeciesContextSpecParameter
				// expression that is being bound cannot contain other speciesContextSpecParameters in its expression.
				expression.bindExpression(new SymbolTable() {
					public SymbolTableEntry getEntry(String identifierString) {
						SymbolTableEntry ste = SpeciesContextSpec.this.getEntry(identifierString);
						if (ste instanceof SpeciesContextSpecParameter) {
							throw new RuntimeException("\nCannot use a speciesContextSpec parameter (e.g., diff, initConc, Vel_X, etc.) in another speciesContextSpec parameter expression.");
						}
						return ste;
					}

					public void getEntries(Map<String, SymbolTableEntry> entryMap) {
						SpeciesContextSpec.this.getEntries(entryMap);
					}
				});
			}
			Expression oldValue = fieldParameterExpression;
			fieldParameterExpression = expression;
			try {
				SpeciesContextSpec.this.cleanupParameters();
			} catch (ModelException e) {
				e.printStackTrace();
			} catch (ExpressionException e) {
				e.printStackTrace();
			}
			super.firePropertyChange("expression", oldValue, expression);
		}
		
		/**
		 * return 0 if {@link #getRole()} initial concentration, diffusion rate, or initial count
		 */
		@Override
		public Expression getDefaultExpression() {
		 //functionality moved from ParameterPropertiesPanel Dec 2014
			switch (getRole()) {
				case ROLE_InitialConcentration:
				case ROLE_DiffusionRate:
				case ROLE_InitialCount:
					return new Expression(0);
				default:
					return null;
			}
		}

		public double getConstantValue() throws ExpressionException {
			return fieldParameterExpression.evaluateConstant();
		}
		
		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}
		
		public void setUnitDefinition(VCUnitDefinition unitDefinition) throws java.beans.PropertyVetoException {
			VCUnitDefinition oldValue = fieldUnitDefinition;
			super.fireVetoableChange("unitDefinition", oldValue, unitDefinition);
			fieldUnitDefinition = unitDefinition;
			super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
		}
		
		public String getName(){
			return fieldParameterName;
		}
		
		public Expression getExpression(){
			return fieldParameterExpression;
		}
		
		public int getIndex() {
			return -1;
		}

		public int getRole() {
			return fieldParameterRole;
		}
		
		public SpeciesContext getSpeciesContext() {
			return SpeciesContextSpec.this.getSpeciesContext();
		}
	}
	
	public class SpeciesContextSpecProxyParameter extends ProxyParameter {

		public SpeciesContextSpecProxyParameter(SymbolTableEntry target){
			super(target);
		}
		
		public NameScope getNameScope(){
			return SpeciesContextSpec.this.getNameScope();
		}

		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof SpeciesContextSpecProxyParameter)){
				return false;
			}
			SpeciesContextSpecProxyParameter other = (SpeciesContextSpecProxyParameter)obj;
			if (getTarget() instanceof Matchable && other.getTarget() instanceof Matchable &&
				Compare.isEqual((Matchable)getTarget(), (Matchable)other.getTarget())){
				return true;
			}else{
				return false;
			}
		}

		
		@Override
		public String getDescription() {
			if (getTarget() instanceof SpeciesContext) {
				return "Species Concentration";
			} else {
				return super.getDescription();
			}
		}

		@Override
		public void targetPropertyChange(PropertyChangeEvent evt) {
			super.targetPropertyChange(evt);
			if (evt.getPropertyName().equals("name")){
				String oldName = (String)evt.getOldValue();
				String newName = (String)evt.getNewValue();
				try {
					SpeciesContextSpecParameter newParameters[] = new SpeciesContextSpecParameter[fieldParameters.length];
					System.arraycopy(fieldParameters, 0, newParameters, 0, fieldParameters.length);
					//
					// go through all parameters' expressions and replace references to 'oldName' with 'newName'
					//
					for (int i = 0; i < newParameters.length; i++){ 
						Expression exp = getParameter(i).getExpression();
						if (exp != null) {
							Expression newExp = new Expression(exp);
							newExp.substituteInPlace(new Expression(oldName),new Expression(newName));
							newParameters[i] = new SpeciesContextSpecParameter(newParameters[i].getName(),newExp,newParameters[i].getRole(),newParameters[i].getUnitDefinition(), newParameters[i].getDescription());
						}
					}
					setParameters(newParameters);
	
					// 
					// rebind all expressions
					//
					for (int i = 0; i < newParameters.length; i++){
						if (newParameters[i].getExpression() != null) {
							newParameters[i].getExpression().bindExpression(SpeciesContextSpec.this);
						}
					}

					//
					// clean up dangling parameters (those not reachable from the 'required' parameters).
					//
					cleanupParameters();

				}catch (ModelException e1){
					e1.printStackTrace(System.out);
				}catch (ExpressionException e2){
					e2.printStackTrace(System.out);
				}catch (PropertyVetoException e3){
					e3.printStackTrace(System.out);
				}
			} 
		} 
		
	}

	
	private SpeciesContext speciesContext = null;
	private static final boolean DEFAULT_CONSTANT = false;
//	private static final boolean DEFAULT_ENABLE_DIFFUSING = true;
	private static final Boolean DEFAULT_WELL_MIXED = false;
	private static final Boolean DEFAULT_FORCECONTINUOUS = false;
	private boolean        bConstant = DEFAULT_CONSTANT;
//	private boolean        bEnableDiffusing = DEFAULT_ENABLE_DIFFUSING;
	private Boolean        bWellMixed = DEFAULT_WELL_MIXED;
	private Boolean        bForceContinuous = DEFAULT_FORCECONTINUOUS;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private SpeciesContextSpecParameter[] fieldParameters = null;
	private SpeciesContextSpecProxyParameter[] fieldProxyParameters = new SpeciesContextSpecProxyParameter[0];
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient SimulationContext simulationContext = null;
	private SpeciesContextSpecNameScope nameScope = new SpeciesContextSpecNameScope();
	public static final int ROLE_InitialConcentration	= 0;
	public static final int ROLE_DiffusionRate			= 1;
	public static final int ROLE_BoundaryValueXm		= 2;
	public static final int ROLE_BoundaryValueXp		= 3;
	public static final int ROLE_BoundaryValueYm		= 4;
	public static final int ROLE_BoundaryValueYp		= 5;
	public static final int ROLE_BoundaryValueZm		= 6;
	public static final int ROLE_BoundaryValueZp		= 7;
	public static final int ROLE_InitialCount			= 8;
	public static final int ROLE_VelocityX				= 9;
	public static final int ROLE_VelocityY				= 10;
	public static final int ROLE_VelocityZ				= 11;
	
	public static final int NUM_ROLES		= 12;
	public static final String RoleTags[] = {
		VCMODL.InitialConcentration,
		VCMODL.DiffusionRate,
		VCMODL.BoundaryConditionXm,
		VCMODL.BoundaryConditionXp,
		VCMODL.BoundaryConditionYm,
		VCMODL.BoundaryConditionYp,
		VCMODL.BoundaryConditionZm,
		VCMODL.BoundaryConditionZp,
		VCMODL.InitialCount,
		VCMODL.VelocityX,
		VCMODL.VelocityY,
		VCMODL.VelocityZ
	};
	public static final String RoleNames[] = {
		"initConc",
		"diff",
		"BC_Xm",
		"BC_Xp",
		"BC_Ym",
		"BC_Yp",
		"BC_Zm",
		"BC_Zp",
		"initCount",
		"Vel_X",
		"Vel_Y",
		"Vel_Z"
	};
	public static final String RoleDescriptions[] = {
		"initial concentration", 
		"diffusion constant",
		"Boundary Condition X-",
		"Boundary Condition X+",
		"Boundary Condition Y-",
		"Boundary Condition Y+",
		"Boundary Condition Z-",
		"Boundary Condition Z+",
		"initial count",
		"Velocity X",
		"Velocity Y",
		"Velocity Z"
	};
	private static RealInterval[] parameterBounds = {
		new RealInterval(0.0, Double.POSITIVE_INFINITY), // init concentration
		new RealInterval(0.0, Double.POSITIVE_INFINITY), // diff rate
		null,	// BC X-
		null,	// BC X+
		null,	// BC Y-
		null,	// BC Y+
		null,	// BC Z-
		null,	// BC Z+
		new RealInterval(0.0, Double.POSITIVE_INFINITY), // init amount
		null, // Velocity X
		null, // Velocity Y
		null  // Velocity Z
	};

public SpeciesContextSpec(SpeciesContextSpec speciesContextSpec, SimulationContext argSimulationContext) {
	this.speciesContext = speciesContextSpec.speciesContext;
	this.bConstant = speciesContextSpec.bConstant;
//	this.bEnableDiffusing = speciesContextSpec.bEnableDiffusing;
	this.bWellMixed = speciesContextSpec.bWellMixed;
	this.bForceContinuous = speciesContextSpec.bForceContinuous;
	this.simulationContext = argSimulationContext;
	fieldParameters = new SpeciesContextSpecParameter[speciesContextSpec.fieldParameters.length];
	for (int i = 0; i < speciesContextSpec.fieldParameters.length; i++){
		SpeciesContextSpecParameter otherParm = speciesContextSpec.fieldParameters[i];
		Expression otherParmExp = (otherParm.getExpression()==null)?(null):(new Expression(otherParm.getExpression()));
		fieldParameters[i] = new SpeciesContextSpecParameter(otherParm.getName(),otherParmExp,otherParm.getRole(),otherParm.getUnitDefinition(),otherParm.getDescription());
	}
	refreshDependencies();
}            


public SpeciesContextSpec(SpeciesContext speciesContext, SimulationContext argSimulationContext) {
	this.speciesContext = speciesContext;
	this.simulationContext = argSimulationContext;

	// avoid unit computation for case of micromolar
	fieldParameters = new SpeciesContextSpecParameter[NUM_ROLES];
	ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
	VCUnitDefinition fluxUnits = computeFluxUnit();
	VCUnitDefinition spContextUnit = speciesContext.getUnitDefinition();
	VCUnitDefinition stochasticSubstanceUnit = modelUnitSystem.getStochasticSubstanceUnit();

	if(argSimulationContext == null)//called from XmlReader.getSpeciesContextSpec(Element)
	{
		fieldParameters[ROLE_InitialConcentration] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialConcentration], null,
															ROLE_InitialConcentration,spContextUnit,
															RoleDescriptions[ROLE_InitialConcentration]);
		
		fieldParameters[ROLE_InitialCount] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialCount], null,
															ROLE_InitialCount, stochasticSubstanceUnit,
															RoleDescriptions[ROLE_InitialCount]);
	}
	else //called from ReactionContext.refreshSpeciesContextSpecs()
	{
		if(argSimulationContext.isUsingConcentration())
		{
			fieldParameters[ROLE_InitialConcentration] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialConcentration], new Expression(0),
					ROLE_InitialConcentration,spContextUnit,
					RoleDescriptions[ROLE_InitialConcentration]);

			fieldParameters[ROLE_InitialCount] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialCount], null,
					ROLE_InitialCount, stochasticSubstanceUnit,
					RoleDescriptions[ROLE_InitialCount]);
		}
		else
		{
			fieldParameters[ROLE_InitialConcentration] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialConcentration], null,
					ROLE_InitialConcentration,spContextUnit,
					RoleDescriptions[ROLE_InitialConcentration]);

			fieldParameters[ROLE_InitialCount] = new SpeciesContextSpecParameter(RoleNames[ROLE_InitialCount], new Expression(0),
					ROLE_InitialCount, stochasticSubstanceUnit,
					RoleDescriptions[ROLE_InitialCount]);
		}
	}
	fieldParameters[ROLE_DiffusionRate] = new SpeciesContextSpecParameter(RoleNames[ROLE_DiffusionRate],new Expression(0.0),
														ROLE_DiffusionRate, modelUnitSystem.getDiffusionRateUnit(),
														RoleDescriptions[ROLE_DiffusionRate]);

	fieldParameters[ROLE_BoundaryValueXm] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueXm],null,
														ROLE_BoundaryValueXm,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueXm]);
	
	fieldParameters[ROLE_BoundaryValueXp] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueXp],null,
														ROLE_BoundaryValueXp,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueXp]);

	fieldParameters[ROLE_BoundaryValueYm] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueYm],null,
														ROLE_BoundaryValueYm,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueYm]);
	
	fieldParameters[ROLE_BoundaryValueYp] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueYp],null,
														ROLE_BoundaryValueYp,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueYp]);

	fieldParameters[ROLE_BoundaryValueZm] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueZm],null,
														ROLE_BoundaryValueZm,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueZm]);
	
	fieldParameters[ROLE_BoundaryValueZp] = new SpeciesContextSpecParameter(RoleNames[ROLE_BoundaryValueZp],null,
														ROLE_BoundaryValueZp,fluxUnits,
														RoleDescriptions[ROLE_BoundaryValueZp]);
	VCUnitDefinition velocityUnit = getLengthPerTimeUnit();
	fieldParameters[ROLE_VelocityX] = new SpeciesContextSpecParameter(RoleNames[ROLE_VelocityX], null,
														ROLE_VelocityX, velocityUnit,	RoleDescriptions[ROLE_VelocityX]);
	
	fieldParameters[ROLE_VelocityY] = new SpeciesContextSpecParameter(RoleNames[ROLE_VelocityY], null,
														ROLE_VelocityY, velocityUnit,	RoleDescriptions[ROLE_VelocityY]);
	
	fieldParameters[ROLE_VelocityZ] = new SpeciesContextSpecParameter(RoleNames[ROLE_VelocityZ], null,
														ROLE_VelocityZ, velocityUnit,	RoleDescriptions[ROLE_VelocityZ]);

	resetDefaults();
	refreshDependencies();
}


private VCUnitDefinition getLengthPerTimeUnit() {
	ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
	VCUnitDefinition lengthPerTimeUnit = modelUnitSystem.getLengthUnit().divideBy(modelUnitSystem.getTimeUnit());
	return lengthPerTimeUnit;
}            

public void initializeForSpatial() {
	if(getDiffusionParameter() != null && getDiffusionParameter().getExpression() != null && getDiffusionParameter().getExpression().isZero()) {
		Expression e = null;
		ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
		VCUnitDefinition micronsqpersecond = modelUnitSystem.getInstance("um2.s-1");
		if(speciesContext.getStructure() instanceof Feature) {
			RationalNumber rn = RationalNumber.getApproximateFraction(micronsqpersecond.convertTo(10, getDiffusionParameter().getUnitDefinition()));
			e = new Expression(rn.doubleValue());
		} else if(speciesContext.getStructure() instanceof Membrane) {
			RationalNumber rn = RationalNumber.getApproximateFraction(micronsqpersecond.convertTo(0.1, getDiffusionParameter().getUnitDefinition()));
			e = new Expression(rn.doubleValue());
		} else {
			RationalNumber rn = RationalNumber.getApproximateFraction(micronsqpersecond.convertTo(1.0, getDiffusionParameter().getUnitDefinition()));
			e = new Expression(rn.doubleValue());
		}
		try {
			getDiffusionParameter().setExpression(e);
		} catch (ExpressionBindingException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Error while initializing diffusion rate, " + e1.getMessage());
		}
	}
}

public VCUnitDefinition computeFluxUnit() {
	return speciesContext.getUnitDefinition().multiplyBy(getLengthPerTimeUnit());
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

public SpeciesContextSpecProxyParameter addProxyParameter(SymbolTableEntry symbolTableEntry) {
	if (getParameterFromName(symbolTableEntry.getName())!=null){
		throw new RuntimeException("local parameter '"+symbolTableEntry.getName()+"' already exists");
	}
	if (getProxyParameter(symbolTableEntry.getName())!=null){
		throw new RuntimeException("referenced external symbol '"+symbolTableEntry.getName()+"' already exists");
	}
	SpeciesContextSpecProxyParameter newProxyParameter = new SpeciesContextSpecProxyParameter(symbolTableEntry);
	SpeciesContextSpecProxyParameter newProxyParameters[] = (SpeciesContextSpecProxyParameter[])BeanUtils.addElement(fieldProxyParameters,newProxyParameter);
	setProxyParameters(newProxyParameters);
	return newProxyParameter;
}

/**
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {

	SpeciesContextSpec scs = null;
	if (!(object instanceof SpeciesContextSpec)){
		return false;
	}
	scs = (SpeciesContextSpec)object;

	if (!Compare.isEqual(speciesContext,scs.speciesContext)){
		return false;
	}

	if (bConstant != scs.bConstant){
		return false;
	}
	
//	if (bEnableDiffusing != scs.bEnableDiffusing){
//		return false;
//	}

	if (bWellMixed!=null && scs.bWellMixed!=null){
		if (!bWellMixed.equals(scs.bWellMixed)){
			return false;
		}
	}else if (bWellMixed!=null || scs.bWellMixed!=null){
		return false; // one is specified and one isn't ... 
	}
	
	if (bForceContinuous!=null && scs.bForceContinuous!=null){
		if (!bForceContinuous.equals(scs.bForceContinuous)){
			return false;
		}
	}else if (bForceContinuous!=null || scs.bForceContinuous!=null){
		return false; // one is specified and one isn't ... 
	}

	if (!Compare.isEqual(fieldParameters,scs.fieldParameters)){
		return false;
	}

	return true;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(evt);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, int oldValue, int newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, boolean oldValue, boolean newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 10:03:46 AM)
 * @param issueVector java.util.Vector
 */
public void gatherIssues(IssueContext issueContext, List<Issue> issueVector) {
	issueContext = issueContext.newChildContext(ContextType.SpeciesContextSpec, this);
	//
	// add constraints (simpleBounds) for predefined parameters
	//
	for (int i = 0; i < fieldParameters.length; i++){
		RealInterval simpleBounds = parameterBounds[fieldParameters[i].getRole()];
		if (simpleBounds!=null){
			String parmName = fieldParameters[i].getNameScope().getName()+"."+fieldParameters[i].getName();
			issueVector.add(new SimpleBoundsIssue(fieldParameters[i], issueContext, simpleBounds, "parameter "+parmName+": must be within "+simpleBounds.toString()));
		}
	}
	if(bForceContinuous && !bConstant && getSimulationContext().isStoch() && (getSimulationContext().getGeometry().getDimension()>0)) {	// if it's particle or constant we're good
		SpeciesContext sc = getSpeciesContext();
		ReactionContext rc = getSimulationContext().getReactionContext();
		ReactionSpec[] rsArray = rc.getReactionSpecs();
		for(ReactionSpec rs : rsArray) {
			if(!rs.isExcluded()) {	// we only care about reactions which are not excluded
				boolean iAmParticipant = false;	// true if "this" is part of current reaction
				boolean haveParticle = false;	// true if current reaction has at least a particle participant
				ReactionStep step = rs.getReactionStep();
				for(ReactionParticipant p : step.getReactionParticipants()) {
					if(p instanceof Product || p instanceof Reactant) {
						SpeciesContextSpec candidate = rc.getSpeciesContextSpec(p.getSpeciesContext());
						if(candidate == this) {
							iAmParticipant = true;
						}
						else if(!candidate.isForceContinuous() && !candidate.isConstant()) {
							haveParticle = true;
						} 
					}
				}
				if(iAmParticipant && haveParticle) {
					String msg = "Continuous Species won't conserve mass in particle reaction "+rs.getReactionStep().getName()+".";
					String tip = "Mass conservation for reactions of binding between discrete and continuous species is handled approximately. <br>" +
							"To avoid any algorithmic approximation, which may produce undesired results, the user is advised to indicate <br>" +
							"the continuous species in those reactions as modifiers (i.e. 'catalysts') in the physiology.";
					issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.SEVERITY_WARNING));
					break;	// we issue warning as soon as we found the first reaction which satisfies criteria
				}
			}
		}
	}
	if(!bForceContinuous && bConstant) {
		if(getSimulationContext().isStoch() && (getSimulationContext().getGeometry().getDimension()>0)) {
			String msg = "Clamped Species must be continuous rather than particles.";
			String tip = "If choose 'clamped', must also choose 'forceContinuous'";
			issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.SEVERITY_ERROR));
		}
	}
	if(bForceContinuous && !bConstant) {
		if(getSimulationContext().isStoch() && (getSimulationContext().getGeometry().getDimension()==0)) {
			String msg = "Non-constant species is forced continuous, not supported for nonspatial stochastic applications.";
			issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, Issue.SEVERITY_ERROR));
		}
	}
	if(getSimulationContext().getAssignmentRules() != null && getSimulationContext().getAssignmentRules().length > 0) {
		for(AssignmentRule ar : getSimulationContext().getAssignmentRules()) {
			if(ar.getAssignmentRuleVar() == null) {
				continue;				// some assignment rule variable may be still not initialized
			}
			SpeciesContext sc = getSpeciesContext();
			if(sc.getName().equals(ar.getAssignmentRuleVar().getName())) {
				if(!bConstant) {		// the assignment rule variables must be clamped
					String msg = "Assignment rule species variables must be Clamped";
					String tip = "Used in " + AssignmentRule.typeName + " '" + ar.getDisplayName() + "'";
					issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
				}
			}
		}
	}
	if(getSimulationContext().getRateRules() != null && getSimulationContext().getRateRules().length > 0) {
		for(RateRule ar : getSimulationContext().getRateRules()) {
			if(ar.getRateRuleVar() == null) {
				continue;				// some rate rule variable may be still not initialized
			}
			SpeciesContext sc = getSpeciesContext();
			if(sc.getName().equals(ar.getRateRuleVar().getName())) {
				if(!bConstant) {		// the rate rule variables must be clamped
					String msg = "Rate rule species variables must be Clamped";
					String tip = "Used in " + RateRule.typeName + " '" + ar.getDisplayName() + "'";
					issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, msg, tip, Issue.Severity.ERROR));
				}
			}
		}
	}
}

/**
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryXmParameter() {
	return getParameterFromRole(ROLE_BoundaryValueXm);		
}


/**
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryXpParameter() {
	return getParameterFromRole(ROLE_BoundaryValueXp);		
}

/**
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryYmParameter() {
	return getParameterFromRole(ROLE_BoundaryValueYm);		
}

/**
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryYpParameter() {
	return getParameterFromRole(ROLE_BoundaryValueYp);		
}

/**
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryZmParameter() {
	return getParameterFromRole(ROLE_BoundaryValueZm);		
}

/**
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getBoundaryZpParameter() {
	return getParameterFromRole(ROLE_BoundaryValueZp);		
}

/**
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getDiffusionParameter() {
	return getParameterFromRole(ROLE_DiffusionRate);		
}

public SpeciesContextSpec.SpeciesContextSpecParameter getVelocityXParameter() {
	return getParameterFromRole(ROLE_VelocityX);		
}

/**
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getVelocityYParameter() {
	return getParameterFromRole(ROLE_VelocityY);		
}

/**
 * @return cbit.vcell.parser.Expression
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getVelocityZParameter() {
	return getParameterFromRole(ROLE_VelocityZ);		
}

/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(String identifierString) {
	
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
			
	ste = getNameScope().getExternalEntry(identifierString,this);

	if (ste instanceof SymbolTableFunctionEntry){
		return ste;
	}
	
	if (ste!=null){
		if (ste instanceof SymbolTableFunctionEntry){
			return ste;
		} else {
			return addProxyParameter(ste);
		}
	}
	
	return null;
}

/**
 * @return java.lang.String
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getInitialConditionParameter()
{
	SpeciesContextSpec.SpeciesContextSpecParameter initParam = null;
	if(getParameterFromRole(ROLE_InitialConcentration).getExpression() != null)
	{
		initParam = getParameterFromRole(ROLE_InitialConcentration);
	}
	else if(getParameterFromRole(ROLE_InitialCount).getExpression() != null)
	{
		initParam = getParameterFromRole(ROLE_InitialCount);
	}
	
	return initParam;
}

public SpeciesContextSpec.SpeciesContextSpecParameter getInitialConcentrationParameter() {
	return getParameterFromRole(ROLE_InitialConcentration);		
}

public SpeciesContextSpec.SpeciesContextSpecParameter getInitialCountParameter() {
	return getParameterFromRole(ROLE_InitialCount);		
}

/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 11:46:37 AM)
 * @return SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getLocalEntry(java.lang.String identifier) {
	SymbolTableEntry ste = null;

	ste = getParameterFromName(identifier);
	if (ste!=null){
		return ste;
	}

	ste = getProxyParameter(identifier);
	if (ste!=null){
		return ste;
	}

	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 11:46:37 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return nameScope;
}


/**
 * Insert the method's description here.
 * Creation date: (4/3/2004 10:48:38 AM)
 * @return cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
 * @param role int
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getParameterFromName(String name) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i].getName().equals(name)){
			return fieldParameters[i];
		}
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (4/3/2004 10:48:38 AM)
 * @return cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
 * @param role int
 */
public SpeciesContextSpec.SpeciesContextSpecParameter getParameterFromRole(int role) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i].getRole() == role){
			return fieldParameters[i];
		}
	}
	return null;
}


/**
 * Gets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @return The parameters property value.
 * @see #setParameters
 */
public Parameter[] getParameters() {
	return fieldParameters;
}


/**
 * Gets the parameters index property (cbit.vcell.model.Parameter) value.
 * @return The parameters property value.
 * @param index The index value into the property array.
 * @see #setParameters
 */
public Parameter getParameter(int index) {
//	please reference getNumDisplayableParameters()(hardcoded number of parameters) to get more understanding.
	Parameter param = null;
	if(index == ROLE_InitialConcentration)//means take the initial conidtion parameter and concentration is being used
	{
		param = getInitialConditionParameter();
		
	}else if (index < getParameters().length){
		param = getParameters()[index]; //using iniAmount || other parameters now is one index greater than previous index(since iniAmount has added) 
	}else{
		param = getProxyParameters()[index-getParameters().length];
	}
	return param;
}

public SpeciesContextSpecProxyParameter getProxyParameter(String pName){
	if (fieldProxyParameters == null){
		return null;
	}
	for (int i=0;i<fieldProxyParameters.length;i++){
		SpeciesContextSpecProxyParameter parm = fieldProxyParameters[i];
		if (pName.equals(parm.getName())){
			return parm;
		}
	}
	return null;
}

public SpeciesContextSpecProxyParameter[] getProxyParameters() {
	return fieldProxyParameters;
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
 * Creation date: (4/3/2004 10:54:49 AM)
 * @return int
 * @param boundaryLocation cbit.vcell.mapping.BoundaryLocation
 */
private int getRole(BoundaryLocation boundaryLocation) {
	int role = -1;
	switch (boundaryLocation.getNum()){
		case BoundaryLocation.BOUNDARY_XM:{
			role = ROLE_BoundaryValueXm;
			break;
		}
		case BoundaryLocation.BOUNDARY_XP:{
			role = ROLE_BoundaryValueXp;
			break;
		}
		case BoundaryLocation.BOUNDARY_YM:{
			role = ROLE_BoundaryValueYm;
			break;
		}
		case BoundaryLocation.BOUNDARY_YP:{
			role = ROLE_BoundaryValueYp;
			break;
		}
		case BoundaryLocation.BOUNDARY_ZM:{
			role = ROLE_BoundaryValueZm;
			break;
		}
		case BoundaryLocation.BOUNDARY_ZP:{
			role = ROLE_BoundaryValueZp;
			break;
		}
	}
	return role;
}


/**
 * This method was created by a SmartGuide.
 * @return cbit.vcell.model.SpeciesContext
 */
public SpeciesContext getSpeciesContext() {
	return speciesContext;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String getVCML() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("\t"+VCMODL.SpeciesContextSpec+" "+getSpeciesContext().getName()+" {\n");
	buffer.append("\t\t"+VCMODL.ForceConstant+" "+isConstant()+"\n");
	buffer.append("\t\t"+VCMODL.EnableDiffusion+" "+isDiffusing()+"\n");
	Expression init = getInitialConditionParameter().getExpression();
	if (init!=null){
		buffer.append("\t\t"+VCMODL.InitialConcentration+" "+init.toString()+";\n");
	}
	Expression diffRate = getDiffusionParameter().getExpression();
	if (diffRate!=null){
		buffer.append("\t\t"+VCMODL.DiffusionRate+" "+diffRate.toString()+";\n");
	}
	//
	// write BoundaryConditions
	//
	for (int i=BoundaryLocation.FIRST;i<=BoundaryLocation.LAST;i++){
		BoundaryLocation bl = BoundaryLocation.fromDirection(i);
		Expression boundExp = getParameterFromRole(getRole(bl)).getExpression();
		if (boundExp!=null){
			buffer.append("\t\t"+VCMODL.BoundaryCondition+" "+bl.toString()+" "+boundExp.toString()+"\n");
		}
	}
	// write velocity x,y,z expressions
	Expression vel_x = getVelocityXParameter().getExpression();
	if (vel_x!=null){
		buffer.append("\t\t"+VCMODL.VelocityX+" "+vel_x.toString()+";\n");
	}
	Expression vel_y = getVelocityYParameter().getExpression();
	if (vel_y!=null){
		buffer.append("\t\t"+VCMODL.VelocityY+" "+vel_y.toString()+";\n");
	}
	Expression vel_z = getVelocityZParameter().getExpression();
	if (vel_z!=null){
		buffer.append("\t\t"+VCMODL.VelocityZ+" "+vel_z.toString()+";\n");
	}

	buffer.append("\t}\n");
	return buffer.toString();		
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
 * The hasListeners method was generated to support the vetoPropertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getVetoPropertyChange().hasListeners(propertyName);
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isConstant() {
	return bConstant;
}


/**
 * @return boolean
 */
public boolean isDiffusing() {
	if (bConstant){
		return false;
	}
	
	if (bWellMixed){
		return false;
	}
	
	try {
		double constantDiffRate = getDiffusionParameter().getExpression().evaluateConstant();
		return (constantDiffRate>0.0);
	}catch (ExpressionException e){
	}

	//
	// can't evaluate to constant, so must be non-zero
	//
	return true;
}

public boolean isAdvecting() {
	if (bWellMixed || bConstant==true){
		return false;
	}
	// If all 3 velocity (x,y,z) parameters are null, there is no advection; return <FALSE>, else return <TRUE>
	Expression eX = getVelocityXParameter().getExpression();
	Expression eY = getVelocityYParameter().getExpression();
	Expression eZ = getVelocityZParameter().getExpression();
	if (eX != null || eY != null || eZ != null) {
		return true;
	}
	if (getVelocityQuantities(QuantityComponent.X).length>0){
		return true;
	}
	if (getVelocityQuantities(QuantityComponent.Y).length>0){
		return true;
	}
	if (getVelocityQuantities(QuantityComponent.Z).length>0){
		return true;
	}
	return false;
}

///**
// * @return boolean
// * @deprecated (no longer used)
// */
//public final boolean isEnableDiffusing() {
//	return bEnableDiffusing;
//}

public final Boolean isWellMixed(){
	return bWellMixed;
}

public final Boolean isForceContinuous(){
	return bForceContinuous;
}

/**
 * This method was created in VisualAge.
 * @return boolean
 * @param parm cbit.vcell.model.Parameter
 */
private boolean isReferenced(Parameter parm) throws ModelException, ExpressionException {

	if (fieldParameters==null){
		return false;
	}
	
	if (parm instanceof SpeciesContextSpec.SpeciesContextSpecParameter){
		SpeciesContextSpecParameter scsParm = (SpeciesContextSpecParameter)parm;
		for (int i = 0; i < fieldParameters.length; i++) {
			if (fieldParameters[i] == parm){
				return true;
			}
		}
		return false;
	} else if (parm instanceof SpeciesContextSpecProxyParameter){
		if (fieldParameters != null){
			for (int i=0;i<fieldParameters.length;i++){
				Expression exp = fieldParameters[i].getExpression();
				if (exp!=null){
					String[] symbols = exp.getSymbols();
					for (int j=0;symbols!=null && j<symbols.length;j++){
						if (AbstractNameScope.getStrippedIdentifier(symbols[j]).equals(parm.getName())){
							return true;
						}
					}
				}
			}
		}
	}
	return false;
}

/**
 * This method was created in VisualAge.
 */
private final void cleanupParameters() throws ModelException, ExpressionException {
	//
	// for each parameter, see if it is used, if not delete it
	//
	if (fieldProxyParameters != null){
		for (int i=0;i<fieldProxyParameters.length;i++){
			if (!isReferenced(fieldProxyParameters[i])){
				removeProxyParameter(fieldProxyParameters[i]);
				i--;
			}
		}
	}
}

/**
 */
public void refreshDependencies() {
	for (int i = 0; i < fieldParameters.length; i++){
		try {
			if (fieldParameters[i].getExpression()!=null){
				fieldParameters[i].getExpression().bindExpression(this);
			}
		}catch (ExpressionException e){
			System.out.println("error binding expression '"+fieldParameters[i].getExpression().infix()+"', "+e.getMessage());
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/3/2004 11:13:04 AM)
 * @param parameter cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
 */
private void removeParameter(SpeciesContextSpec.SpeciesContextSpecParameter parameter) {}

protected void removeProxyParameter(SpeciesContextSpecProxyParameter parameter) {
	for (int i = 0; i < fieldProxyParameters.length; i++){
		if (fieldProxyParameters[i] == parameter){
			SpeciesContextSpecProxyParameter newProxyParameters[] = (SpeciesContextSpecProxyParameter[])BeanUtils.removeElement(fieldProxyParameters,parameter);
			setProxyParameters(newProxyParameters);
			return;
		}
	}
	throw new RuntimeException(parameter.getName()+"' not found");
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
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


/**
 */
private void resetDefaults() {
	bConstant = DEFAULT_CONSTANT;
//	if (getSpeciesContext().getStructure() instanceof Feature || getSpeciesContext().getStructure() instanceof Membrane){
//		bEnableDiffusing = DEFAULT_ENABLE_DIFFUSING;
//	}else{
//		bEnableDiffusing = false;
//	}
	bWellMixed = DEFAULT_WELL_MIXED;
	bForceContinuous = DEFAULT_FORCECONTINUOUS;
}


/**
 * @param isConstant boolean
 */
public void setConstant(boolean isConstant) {
	boolean oldDiffusing = isDiffusing();
	boolean oldConstant = bConstant;
	this.bConstant = isConstant;
	firePropertyChange("constant",new Boolean(oldConstant), new Boolean(isConstant));
	firePropertyChange("diffusing", new Boolean(oldDiffusing), new Boolean(isDiffusing()));
}

///**
// * @param isDiffusing boolean
// * @deprecated
// */
//public void setEnableDiffusing(boolean isEnableDiffusing) throws MappingException {
//	boolean oldEnableDiffusing = bEnableDiffusing;
//
//	this.bEnableDiffusing = isEnableDiffusing;
//
//	firePropertyChange("enableDiffusing",new Boolean(oldEnableDiffusing), new Boolean(isEnableDiffusing));
//}


public void setWellMixed(boolean bWellMixed) {
	Boolean oldValue = this.bWellMixed;
	this.bWellMixed = bWellMixed;
	firePropertyChange(PROPERTY_NAME_WELL_MIXED,oldValue, bWellMixed);
}

public void setForceContinuous(boolean bForceContinuous) {
	Boolean oldValue = this.bForceContinuous;
	this.bForceContinuous = bForceContinuous;
	firePropertyChange(PROPERTY_NAME_FORCECONTINUOUS,oldValue, bForceContinuous);
}


/**
 * Sets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @param parameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getParameters
 */
private void setParameters(SpeciesContextSpecParameter[] parameters) throws java.beans.PropertyVetoException {
	SpeciesContextSpecParameter[] oldValue = fieldParameters;
	fireVetoableChange("parameters", oldValue, parameters);
	fieldParameters = parameters;
	try {
		cleanupParameters();
	} catch (ModelException e) {
		e.printStackTrace();
	} catch (ExpressionException e) {
		e.printStackTrace();
	}
	firePropertyChange("parameters", oldValue, parameters);
}

private void setProxyParameters(SpeciesContextSpecProxyParameter[] proxyParameters) {
	SpeciesContextSpecProxyParameter[] oldValue = fieldProxyParameters;
	fieldProxyParameters = proxyParameters;
	firePropertyChange(PARAMETER_NAME_PROXY_PARAMETERS, oldValue, proxyParameters);
}


/**
 * Insert the method's description here.
 * Creation date: (4/23/2004 7:25:49 AM)
 * @param newSimulationContext cbit.vcell.mapping.SimulationContext
 */
public void setSimulationContext(SimulationContext newSimulationContext) {
	simulationContext = newSimulationContext;
}


/**
 * @param sc cbit.vcell.model.SpeciesContext
 */
void setSpeciesContextReference(SpeciesContext sc) throws MappingException {
	
	if (this.speciesContext == sc){
		return;
	}
	
	if (sc.compareEqual(this.speciesContext)){
		this.speciesContext = sc;
	}else{
		throw new MappingException("replacing speciesContext that is not the same");
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append(getClass().getName()+"\n");
   if (speciesContext != null) { sb.append("speciesContext = '"+speciesContext+"'\n"); }
 	
	return sb.toString();
}

private double computeStructureSize() throws ExpressionException, MappingException {
	Structure structure = getSpeciesContext().getStructure();
	StructureMapping sm = getSimulationContext().getGeometryContext().getStructureMapping(structure);
	double structSize = 0;
	if (getSimulationContext().getGeometry().getDimension() == 0) {
		Expression sizeParameterExpression = sm.getSizeParameter().getExpression();
		if (sizeParameterExpression == null || sizeParameterExpression.isZero()) {
			throw new RuntimeException("\nIn application '" + getSimulationContext().getName() + "', " +
				"size of structure '" + structure.getName() + "' is required to convert " +
				"concentration to number of particles.\n\nPlease go to 'Structure Mapping' tab to check the size.");
		}
		structSize = sizeParameterExpression.evaluateConstant();
	} else {
		StructureMapping structureMapping = getSimulationContext().getGeometryContext().getStructureMapping(structure);
		GeometryClass gc = structureMapping.getGeometryClass();
		if (gc == null) {
			throw new MappingException("model structure '" + structure.getName()+ "' not mapped to a geometry subdomain");
		}
		
		try {
			GeometricRegion[] geometricRegions = getSimulationContext().getGeometry().getGeometrySurfaceDescription().getGeometricRegions(gc);
			if (geometricRegions == null) {
				throw new MappingException("Geometry is not updated.");
			}
			
			for (GeometricRegion gr : geometricRegions) {
				structSize += gr.getSize();
			}
		} catch (Exception ex) {
			
		}
	}
	return structSize;
}

public Expression convertConcentrationToParticles(Expression iniConcentration) throws ExpressionException, MappingException
{
	Expression iniParticlesExpr = null; 
	Structure structure = getSpeciesContext().getStructure();
	double structSize = computeStructureSize();
	
	if (structure instanceof Membrane) {
		// convert concentration(particles/area) to number of particles
		// particles = iniConcentration(molecules/um2)*size(um2)
		try {
			iniParticlesExpr = new Expression((Math.round(iniConcentration.evaluateConstant()* structSize)));
		} catch (ExpressionException e) {
			iniParticlesExpr = Expression.mult(iniConcentration, new Expression(structSize)).flatten();
		}
	} else {
		// convert concentration(particles/volume) to number of particles
		// particles = [iniConcentration(uM)*size(um3)]/KMOLE
		// @Note : 'kMole' variable here is used only as a var name, it does not represent the previously known ReservedSymbol KMOLE.
		ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
		VCUnitDefinition volSubstanceToStochastic = modelUnitSystem.getStochasticSubstanceUnit().divideBy(modelUnitSystem.getVolumeSubstanceUnit());
		double volSubstanceToStochasticScale = volSubstanceToStochastic.getDimensionlessScale().doubleValue();
		try {
			iniParticlesExpr = new Expression((Math.round(iniConcentration.evaluateConstant() * structSize * volSubstanceToStochasticScale)));
		} catch (ExpressionException e) {
			Expression numeratorExpr = Expression.mult(iniConcentration, new Expression(structSize));
			Expression exp = new Expression(volSubstanceToStochasticScale);
			iniParticlesExpr = Expression.mult(numeratorExpr, exp).flatten();
		}
	}
	
	return iniParticlesExpr;
}

public Expression convertParticlesToConcentration(Expression iniParticles) throws ExpressionException, MappingException
{
	Expression iniConcentrationExpr = null;
	Structure structure = getSpeciesContext().getStructure();
	double structSize = computeStructureSize();
	
	if (structure instanceof Membrane) {
		// convert number of particles to concentration(particles/area)
		// iniConcentration(molecules/um2) = particles/size(um2)
		try {
			iniConcentrationExpr = new Expression((iniParticles.evaluateConstant() * 1.0) / structSize); 
		} catch (ExpressionException e) {
			iniConcentrationExpr = Expression.div(iniParticles, new Expression(structSize)).flatten();
		}
	} else {
		// convert concentration(particles/volume) to number of particles
		// particles = [iniParticles(uM)/size(um3)]*KMOLE
		// @Note : 'kMole' variable here is used only as a var name, it does not represent the previously known ReservedSymbol KMOLE.
		ModelUnitSystem modelUnitSystem = getSimulationContext().getModel().getUnitSystem();
		VCUnitDefinition stochasticToVolSubstance = modelUnitSystem.getVolumeSubstanceUnit().divideBy(modelUnitSystem.getStochasticSubstanceUnit());
		double stochasticToVolSubstanceScale = stochasticToVolSubstance.getDimensionlessScale().doubleValue();
		try {
			iniConcentrationExpr = new Expression((iniParticles.evaluateConstant() * stochasticToVolSubstanceScale / structSize));
		} catch (ExpressionException e) {
			Expression numeratorExpr = Expression.mult(iniParticles, new Expression(stochasticToVolSubstanceScale));
			Expression denominatorExpr = new Expression(structSize);
			iniConcentrationExpr = Expression.div(numeratorExpr, denominatorExpr).flatten();
		}
	}
	return iniConcentrationExpr;
}

public SimulationContext getSimulationContext() {
	return simulationContext;
}

public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {	
	for (SymbolTableEntry ste : fieldProxyParameters) {
		entryMap.put(ste.getName(), ste);
	}
	for (SymbolTableEntry ste : fieldParameters) {
		entryMap.put(ste.getName(), ste);
	}
}

public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	getNameScope().getExternalEntries(entryMap);		
}

public boolean hasTransport() {
	if (isConstant() || isWellMixed() || simulationContext == null 
			|| simulationContext.getGeometry() == null || simulationContext.getGeometry().getDimension() == 0) {
		return false;
	}
	int dimension = simulationContext.getGeometry().getDimension();
	SpeciesContext speciesContext = getSpeciesContext();
	if (speciesContext.getStructure() instanceof Membrane){
		if (dimension > 1 && !getDiffusionParameter().getExpression().isZero()) {
			return true;
		}			
	} else if (speciesContext.getStructure() instanceof Feature){
		if (!getDiffusionParameter().getExpression().isZero()) {
			return true;
		}
	
		if (getVelocityXParameter().getExpression() != null && !getVelocityXParameter().getExpression().isZero()) {
			return true;
		}
		SpatialQuantity[] velX_quantities = getVelocityQuantities(QuantityComponent.X);
		if (velX_quantities.length>0){
			return true;
		}
		if (dimension > 1) {
			if (getVelocityYParameter().getExpression() != null && !getVelocityYParameter().getExpression().isZero()) {
				return true;
			}						
		
			if (dimension > 2) {
				if (getVelocityZParameter().getExpression() != null && !getVelocityZParameter().getExpression().isZero()) {
					return true;
				}
			}
		}						
	}	
	return false;
}

public List<SpeciesContextSpecParameter> computeApplicableParameterList() {
	List<SpeciesContextSpecParameter> speciesContextSpecParameterList = new ArrayList<SpeciesContextSpecParameter>();
	speciesContextSpecParameterList.add(getInitialConditionParameter());
	int dimension = simulationContext.getGeometry().getDimension();
	if (!isConstant() && !isWellMixed() && dimension > 0) {
		// diffusion
		speciesContextSpecParameterList.add(getDiffusionParameter());
	}
	if (hasTransport()) {
		SpeciesContext speciesContext = getSpeciesContext();
		if (speciesContext.getStructure() instanceof Membrane) {
			
			// boundary condition
			speciesContextSpecParameterList.add(getBoundaryXmParameter());
			speciesContextSpecParameterList.add(getBoundaryXpParameter());
			speciesContextSpecParameterList.add(getBoundaryYmParameter());
			speciesContextSpecParameterList.add(getBoundaryYpParameter());
	
			if (dimension > 2) {
				speciesContextSpecParameterList.add(getBoundaryZmParameter());
				speciesContextSpecParameterList.add(getBoundaryZpParameter());
			}
			
		} else if (speciesContext.getStructure() instanceof Feature){						
			// boundary condition
			speciesContextSpecParameterList.add(getBoundaryXmParameter());
			speciesContextSpecParameterList.add(getBoundaryXpParameter());
						 
			if (dimension > 1) {
				speciesContextSpecParameterList.add(getBoundaryYmParameter());
				speciesContextSpecParameterList.add(getBoundaryYpParameter());
			
			
				if (dimension > 2) {
					speciesContextSpecParameterList.add(getBoundaryZmParameter());
					speciesContextSpecParameterList.add(getBoundaryZpParameter());
				}
			}
			
			// velocity
			speciesContextSpecParameterList.add(getVelocityXParameter());
			if (dimension > 1) {
				speciesContextSpecParameterList.add(getVelocityYParameter());
			
				if (dimension > 2) {
					speciesContextSpecParameterList.add(getVelocityZParameter());
				}
			}
		}
	}
	return speciesContextSpecParameterList;
}

public SpatialQuantity[] getVelocityQuantities(QuantityComponent component) {
	ArrayList<SpatialQuantity> velocityQuantities = new ArrayList<SpatialQuantity>();
	for (SpatialObject spatialObject : simulationContext.getSpatialObjects(speciesContext.getStructure())){
		if (spatialObject instanceof VolumeRegionObject){
			if (((VolumeRegionObject)spatialObject).isQuantityCategoryEnabled(QuantityCategory.InteriorVelocity)){
				velocityQuantities.add(((VolumeRegionObject)spatialObject).getSpatialQuantity(QuantityCategory.InteriorVelocity, component));
			}
		}
	}
	return velocityQuantities.toArray(new SpatialQuantity[0]);
}


@Override
public Kind getSimulationContextKind() {
	return SimulationContext.Kind.SPECIFICATIONS_KIND;
}

}
