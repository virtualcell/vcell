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
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.Issue.Severity;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.Preference;
import org.vcell.util.PropertyChangeListenerProxyVCell;
import org.vcell.util.TokenMangler;
import org.vcell.util.VCAssert;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.DocumentValidUtil;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;

import cbit.image.ImageException;
import cbit.image.VCImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.data.DataContext;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;
import cbit.vcell.geometry.surface.GeometricRegion;
import cbit.vcell.geometry.surface.SurfaceGeometricRegion;
import cbit.vcell.geometry.surface.VolumeGeometricRegion;
import cbit.vcell.mapping.AbstractMathMapping.MathMappingNameScope;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.MicroscopeMeasurement.ProjectionZKernel;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.mapping.spatial.CurveObject;
import cbit.vcell.mapping.spatial.PointObject;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.mapping.spatial.SurfaceRegionObject;
import cbit.vcell.mapping.spatial.VolumeRegionObject;
import cbit.vcell.mapping.spatial.processes.PointKinematics;
import cbit.vcell.mapping.spatial.processes.PointLocation;
import cbit.vcell.mapping.spatial.processes.SpatialProcess;
import cbit.vcell.mapping.spatial.processes.SurfaceKinematics;
import cbit.vcell.mapping.spatial.processes.VolumeKinematics;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ExpressionContainer;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.model.Model.ReservedSymbolRole;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.modelopt.AnalysisTask;
import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.OutputFunctionContext;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.units.VCUnitDefinition;
/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class SimulationContext implements SimulationOwner, Versionable, Matchable, BioNetGenUpdaterCallback,
	ScopedSymbolTable, PropertyChangeListener, VetoableChangeListener, Serializable, IssueSource {
	
	public interface MathMappingCallback {
		public void setMessage(String message);
		public void setProgressFraction(float fractionDone);
		public boolean isInterrupted();
	}

	public static final String PROPERTY_NAME_DESCRIPTION = "description";
	public static final String PROPERTY_NAME_ANALYSIS_TASKS = "analysisTasks";
	public static final String PROPERTY_NAME_BIOEVENTS = "bioevents";
	public static final String PROPERTY_NAME_USE_CONCENTRATION = "UseConcentration";
	public static final String PROPERTY_NAME_RANDOMIZE_INIT_CONDITIONS = "RandomizeInitConditions";
	public static final String PROPERTY_NAME_SPATIALOBJECTS = "spatialObjects";
	public static final String PROPERTY_NAME_SPATIALPROCESSES = "spatialProcesses";
	public static final String PROPERTY_NAME_SIMULATIONCONTEXTPARAMETERS = "simulationContextParameters";

	// for rate rule
	public static final String PROPERTY_NAME_RATERULES = "raterules";
	
	public class SimulationContextNameScope extends BioNameScope {
		private transient NameScope nameScopes[] = null;
		public SimulationContextNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// return list of structureMappings and speciesContextSpecs
			//
			int index = 0;
			nameScopes = new NameScope[geoContext.getStructureMappings().length + 
										reactionContext.getSpeciesContextSpecs().length + 
										reactionContext.getReactionSpecs().length + 
										fieldElectricalStimuli.length +
										((fieldBioEvents!=null)?fieldBioEvents.length:0) +
										((spatialProcesses!=null)?spatialProcesses.length:0)];
			for (int i = 0; i < geoContext.getStructureMappings().length; i++){
				nameScopes[index++] = geoContext.getStructureMappings()[i].getNameScope();
			}
			for (int i = 0; i < reactionContext.getSpeciesContextSpecs().length; i++){
				nameScopes[index++] = reactionContext.getSpeciesContextSpecs()[i].getNameScope();
			}
			for (int i = 0; i < reactionContext.getReactionSpecs().length; i++){
				nameScopes[index++] = reactionContext.getReactionSpecs()[i].getNameScope();
			}
			for (int i = 0; i < fieldElectricalStimuli.length; i++){
				nameScopes[index++] = fieldElectricalStimuli[i].getNameScope();
			}
			for (int i = 0; fieldBioEvents!=null && i < fieldBioEvents.length; i++){
				nameScopes[index++] = fieldBioEvents[i].getNameScope();
			}
			for (int i = 0; spatialProcesses!=null && i < spatialProcesses.length; i++){
				nameScopes[index++] = spatialProcesses[i].getNameScope();
			}
			return nameScopes;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(SimulationContext.this.getName());
		}
		@Override
		public String getPathDescription() {
			return "App("+SimulationContext.this.getName()+")";
		}
		public NameScope getParent() {
			//System.out.println("SimulationContextNameScope.getParent() returning null ... no parent");
			return null;
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return SimulationContext.this;
		}
		public SimulationContext getSimulationContext() {
			return SimulationContext.this;
		}
		public boolean isPeer(NameScope nameScope){
			if (super.isPeer(nameScope)){
				return true;
			}
			return ((nameScope instanceof MathMappingNameScope) && nameScope.isPeer(this));
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.simulationContextType;
		}
	}

	public class SimulationContextParameter extends Parameter implements ExpressionContainer {
		
		private String fieldParameterName = null;
		private Expression fieldParameterExpression = null;
		private int fieldParameterRole = -1;
		private VCUnitDefinition fieldUnitDefinition = null;
		
		public SimulationContextParameter(String argName, Expression expression, int argRole, VCUnitDefinition argUnitDefinition) {
			if (argName == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argName.length()<1){
				throw new IllegalArgumentException("parameter name is zero length");
			}
			this.fieldParameterName = argName;
			this.fieldParameterExpression = expression;
			this.fieldUnitDefinition = argUnitDefinition;
			if (argRole >= 0 && argRole < NUM_ROLES){
				this.fieldParameterRole = argRole;
			}else{
				throw new IllegalArgumentException("parameter 'role' = "+argRole+" is out of range");
			}
		}


		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof SimulationContextParameter)){
				return false;
			}
			SimulationContextParameter mp = (SimulationContextParameter)obj;
			if (!super.compareEqual0(mp)){
				return false;
			}
			if (fieldParameterRole != mp.fieldParameterRole){
				return false;
			}
			
			return true;
		}


		public boolean isExpressionEditable(){
			return true;
		}

		public boolean isUnitEditable(){
			return true;
		}

		public boolean isNameEditable(){
			return true;
		}

		public double getConstantValue() throws ExpressionException {
			return this.fieldParameterExpression.evaluateConstant();
		}      


		public Expression getExpression() {
			return this.fieldParameterExpression;
		}


		public int getIndex() {
			return -1;
		}


		public String getName(){ 
			return this.fieldParameterName; 
		}   


		public NameScope getNameScope() {
			return SimulationContext.this.nameScope;
		}

		public int getRole() {
			return this.fieldParameterRole;
		}

		public VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}

		public void setUnitDefinition(VCUnitDefinition unitDefinition) throws java.beans.PropertyVetoException {
			VCUnitDefinition oldValue = fieldUnitDefinition;
			super.fireVetoableChange("unitDefinition", oldValue, unitDefinition);
			fieldUnitDefinition = unitDefinition;
			super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
		}
		public void setExpression(Expression expression) {
			Expression oldValue = fieldParameterExpression;
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}
		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}
		
		public SimulationContext getSimulationContext(){
			return SimulationContext.this;
		}

	}
	public static final int ROLE_UserDefined	= 0;
	public static final int NUM_ROLES		= 1;
	public static final String RoleDesc = "user defined";

	private Version version = null;
	private GeometryContext geoContext = null;
	private ReactionContext reactionContext = null;
	private MembraneContext membraneContext = null; 
	private final OutputFunctionContext outputFunctionContext = new OutputFunctionContext(this);
	private MathDescription mathDesc = null;
	private Double characteristicSize = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	private java.lang.String fieldName = new String("NoName");
	private java.lang.String fieldDescription = new String();
	private double fieldTemperatureKelvin = 300;
	private ElectricalStimulus[] fieldElectricalStimuli = new ElectricalStimulus[0];
	private BioEvent[] fieldBioEvents = null;
	private Electrode fieldGroundElectrode = null;
	private SimulationContextNameScope nameScope = new SimulationContextNameScope();
	private transient BioModel bioModel = null;
	private SimulationContext.SimulationContextParameter[] fieldSimulationContextParameters = new SimulationContextParameter[0];
	private AnalysisTask[] fieldAnalysisTasks = null;
//	private boolean bStoch;
//	private boolean bRuleBased;
	private final Application applicationType;
	private boolean bMassConservationModelReduction = true;
	private boolean bConcentration = true;
	private boolean bRandomizeInitCondition = false;
	private DataContext dataContext = new DataContext(getNameScope());
	private final MicroscopeMeasurement microscopeMeasurement = new MicroscopeMeasurement(SimulationContext.FLUOR_DATA_NAME,new ProjectionZKernel(), this);
	
	// rate rules
	private RateRule[] fieldRateRules = null;
	public static final String FLUOR_DATA_NAME = "fluor";

	private transient TaskCallbackProcessor tcp = new TaskCallbackProcessor(this);
	// Network Constraints stuff, not related with the cache below but used at the same time. NOT transient
	private NetworkConstraints networkConstraints = null;
	private boolean bInsufficientIterations = false;
	private boolean bInsufficientMaxMolecules = false;
	// Cache of the BNGOutputSpec produced by running bng.exe
	// This operation has no relationship whatsoever with caching of the MathMapping below
	private transient String md5hash = null;
	private transient BNGOutputSpec mostRecentlyCreatedOutputSpec = null;	// valid only if the hash is verified
	
	// Cache the most recent Math Mapping
	// This operation has no relationship whatsoever with caching of the BNGLOutputSpec above
	// warning: we don't verify the validity of the mostRecentlyCreatedMathMapping, use it only when certain that it's still valid!
	private transient MathMapping mostRecentlyCreatedMathMapping;
	
	//don't serialize, derivative info generated from info within class
	private transient UnitInfo unitInfo; 
	
	public enum Application {
		NETWORK_DETERMINISTIC(MathType.Deterministic),
		NETWORK_STOCHASTIC(MathType.Stochastic),
		RULE_BASED_STOCHASTIC(MathType.RuleBased);
		
		final public MathType mathType;

		private Application(MathType mathType) {
			this.mathType = mathType;
		}
	}

	public MicroscopeMeasurement getMicroscopeMeasurement() {
		return microscopeMeasurement;
	}

private static Application legacyType(boolean arg_isStoch, boolean arg_isRuleBased) {
	if (arg_isRuleBased) {
		return Application.RULE_BASED_STOCHASTIC;
	}
	if (arg_isStoch) {
		return Application.NETWORK_STOCHASTIC;
	}
	return Application.NETWORK_DETERMINISTIC;
}
/**
 * Construct a new SimulationContext from an old SimulationContext.
 * Input paras: SimulationContext (the old one), boolean (is stochastic application or not) 
 */
public SimulationContext(SimulationContext oldSimulationContext,Geometry newClonedGeometry, Application appType) throws PropertyVetoException {
	
	if(appType == Application.NETWORK_STOCHASTIC) 
	{
		String msg = oldSimulationContext.getModel().isValidForStochApp();
		if(!msg.equals(""))
		{
			throw new RuntimeException("Error constructing a new simulation context:\n" + msg); //no need to show popup here, the exception passes to upper level.
		}
		this.bConcentration = oldSimulationContext.bConcentration;
	} else {
		this.bConcentration = true; //deterministic method use concentration only.
	}
	this.geoContext = new GeometryContext(oldSimulationContext.getGeometryContext(),this, newClonedGeometry);
	geoContext.addPropertyChangeListener(this);
	this.reactionContext = new ReactionContext(oldSimulationContext.getReactionContext(),this);
	this.membraneContext = new MembraneContext(this);
	this.version = null;
	this.characteristicSize = oldSimulationContext.getCharacteristicSize();
	this.fieldName = "copied_from_"+oldSimulationContext.getName();
	this.setFieldDescription("(copied from "+oldSimulationContext.getName()+") "+oldSimulationContext.getDescription());
	this.bioModel = oldSimulationContext.getBioModel();
	if(oldSimulationContext.networkConstraints != null) {
		this.networkConstraints = new NetworkConstraints(oldSimulationContext.networkConstraints);
	}
	applicationType = appType;
	
	//
	// copy electrical stimuli and ground
	//
	if (oldSimulationContext.getGroundElectrode()!=null){
		this.fieldGroundElectrode = new Electrode(oldSimulationContext.getGroundElectrode());
	}
	this.fieldElectricalStimuli = (ElectricalStimulus[])oldSimulationContext.getElectricalStimuli().clone();
	for (int i = 0; i < fieldElectricalStimuli.length; i++){
		if (fieldElectricalStimuli[i] instanceof TotalCurrentClampStimulus){
			TotalCurrentClampStimulus otherStimulus = (TotalCurrentClampStimulus)fieldElectricalStimuli[i];
			fieldElectricalStimuli[i] = new TotalCurrentClampStimulus(otherStimulus,this);
		}else if (fieldElectricalStimuli[i] instanceof CurrentDensityClampStimulus){
			CurrentDensityClampStimulus otherStimulus = (CurrentDensityClampStimulus)fieldElectricalStimuli[i];
			fieldElectricalStimuli[i] = new CurrentDensityClampStimulus(otherStimulus,this);
		}else if (fieldElectricalStimuli[i] instanceof VoltageClampStimulus){
			VoltageClampStimulus otherStimulus = (VoltageClampStimulus)fieldElectricalStimuli[i];
			fieldElectricalStimuli[i] = new VoltageClampStimulus(otherStimulus,this);
		}else{
			throw new RuntimeException("");
		}
	}
	
	if (applicationType == Application.NETWORK_DETERMINISTIC) {
		// copy events
		BioEvent[] bioEvents = oldSimulationContext.getBioEvents();
		if (bioEvents != null) {
			fieldBioEvents = new BioEvent[bioEvents.length];
			for (int i = 0; i < bioEvents.length; i++) {
				fieldBioEvents[i] = new BioEvent(bioEvents[i], this);
			}
		}
		
		// copy SpatialObjects
		if(newClonedGeometry.getDimension() > 0) {
			SpatialObject[] otherSpatialObjects = oldSimulationContext.getSpatialObjects();
			if (otherSpatialObjects != null) {
				this.spatialObjects = new SpatialObject[otherSpatialObjects.length];
				for (int i = 0; i < otherSpatialObjects.length; i++) {
					if (otherSpatialObjects[i] instanceof CurveObject){
						this.spatialObjects[i] = new CurveObject((CurveObject) otherSpatialObjects[i], this);
					}else if (otherSpatialObjects[i] instanceof PointObject){
						this.spatialObjects[i] = new PointObject((PointObject) otherSpatialObjects[i], this);
					}else if (otherSpatialObjects[i] instanceof SurfaceRegionObject){
						this.spatialObjects[i] = new SurfaceRegionObject((SurfaceRegionObject) otherSpatialObjects[i], this);
					}else if (otherSpatialObjects[i] instanceof VolumeRegionObject){
						this.spatialObjects[i] = new VolumeRegionObject((VolumeRegionObject) otherSpatialObjects[i], this);
					}else{
						throw new RuntimeException("unexpected Spatial object in SimulationContext()");
					}
				}
			}
		}
		
		// copy application parameters
		SimulationContextParameter otherSimContextParameters[] = oldSimulationContext.getSimulationContextParameters();
		if (otherSimContextParameters!=null){
			this.fieldSimulationContextParameters = new SimulationContextParameter[otherSimContextParameters.length];
			for (int i=0;i<otherSimContextParameters.length;i++){
				SimulationContextParameter p = otherSimContextParameters[i];
				VCUnitDefinition unit = getModel().getUnitSystem().getInstance(p.getUnitDefinition().getSymbol());
				Expression exp = new Expression(p.getExpression());
				this.fieldSimulationContextParameters[i] = new SimulationContextParameter(p.getName(), exp, p.getRole(), unit);
			}
		}
		
		// copy SpatialProcesses
		if(newClonedGeometry.getDimension() > 0) {
			SpatialProcess[] otherSpatialProcesses = oldSimulationContext.getSpatialProcesses();
			if (otherSpatialProcesses != null) {
				this.spatialProcesses = new SpatialProcess[otherSpatialProcesses.length];
				for (int i = 0; i < otherSpatialProcesses.length; i++) {
					if (otherSpatialProcesses[i] instanceof PointLocation){
						this.spatialProcesses[i] = new PointLocation((PointLocation) otherSpatialProcesses[i], this);
					}else if (otherSpatialProcesses[i] instanceof PointKinematics){
						this.spatialProcesses[i] = new PointKinematics((PointKinematics) otherSpatialProcesses[i], this);
					}else if (otherSpatialProcesses[i] instanceof SurfaceKinematics){
						this.spatialProcesses[i] = new SurfaceKinematics((SurfaceKinematics) otherSpatialProcesses[i], this);
					}else if (otherSpatialProcesses[i] instanceof VolumeKinematics){
						this.spatialProcesses[i] = new VolumeKinematics((VolumeKinematics) otherSpatialProcesses[i], this);
					}else{
						throw new RuntimeException("unexpected Spatial process in SimulationContext()");
					}
				}
			}
		}
		
		// copy rate rules
		RateRule[] rateRules = oldSimulationContext.getRateRules();
		if (rateRules != null) {
			fieldRateRules = new RateRule[rateRules.length];
			for (int i = 0; i < rateRules.length; i++) {
				fieldRateRules[i] = new RateRule(rateRules[i], this);
			}
		}
	}
	if (oldSimulationContext.applicationType != Application.NETWORK_STOCHASTIC && applicationType == Application.NETWORK_STOCHASTIC && geoContext.getGeometry().getDimension()>0) {
		initializeForSpatial();
	}
	
	if(oldSimulationContext.fieldAnalysisTasks != null)
	{
		try {
			AnalysisTask[] analysisTasks = new AnalysisTask[oldSimulationContext.fieldAnalysisTasks.length];
			for(int i=0; i<oldSimulationContext.fieldAnalysisTasks.length; i++)
			{
				analysisTasks[i] = new ParameterEstimationTask(this, (ParameterEstimationTask)oldSimulationContext.fieldAnalysisTasks[i]);
			}
			setAnalysisTasks(analysisTasks);
		} catch (ExpressionException e) {
			//if cannot create parameter estimation task, we don't want to block the vcell running.
			//it will leave an empty task, which users can actually use "new" button to create parameter estimation task later.
			e.printStackTrace(System.out);
		}
	}
	refreshDependencies();
}


/**
 * SimulationContext constructor.
 * @deprecated use {@link #SimulationContext(Model, Geometry, MathDescription, Version, Application)
 * Please see new constructor which is in effect from Sept 28, 2006.
 * Creation date: (9/29/2006 9:53:45 AM)
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 * @exception java.beans.PropertyVetoException The exception description.
 */
@Deprecated
public SimulationContext(Model argModel, Geometry argGeometry) throws java.beans.PropertyVetoException
{
	this(argModel,argGeometry,null,null,Application.NETWORK_DETERMINISTIC);
}

/**
 * @deprecated use {@link #SimulationContext(Model, Geometry, MathDescription, Version, Application)
 * @param model
 * @param geometry
 * @param argMathDesc
 * @param argVersion
 * @param bStoch
 * @param bRuleBased
 * @throws PropertyVetoException
 */
@Deprecated
public SimulationContext(Model model, Geometry geometry, MathDescription argMathDesc, Version argVersion, boolean bStoch, boolean bRuleBased) throws PropertyVetoException {
	this(model,geometry,argMathDesc,argVersion,legacyType(bStoch, bRuleBased));
}
/**
 * Preferred SimulationContext constructor.
 * @param model
 * @param geometry
 * @param argMathDesc null okay
 * @param argVersion null okay
 * @param type non-null
 */
public SimulationContext(Model model, Geometry geometry, MathDescription argMathDesc, Version argVersion, Application type) throws PropertyVetoException {
	applicationType = type; 
	addVetoableChangeListener(this);
	geoContext = new GeometryContext(model,geometry,this);
	geoContext.addPropertyChangeListener(this);
	refreshCharacteristicSize();
	
	this.reactionContext = new ReactionContext(model,this);
	this.membraneContext = new MembraneContext(this);
	this.mathDesc = argMathDesc;
	this.version = argVersion;
	geometry.getGeometrySpec().addPropertyChangeListener(this);
	if (argVersion!=null){
		this.fieldName = argVersion.getName();
		this.setFieldDescription(argVersion.getAnnot());
	} else {
		this.fieldName = "Application_with_"+geometry.getName();
	}
}


/**
 * SimulationContext constructor.
 * This constructor differs with the previous one with one more boolean input parameter, which specifies whether
 * the new application is a stochastic application or not.
 */
//@Deprecated
//public SimulationContext(Model model, Geometry geometry, boolean bStoch, boolean bRuleBased) throws PropertyVetoException {
//	this(model,geometry,null,null,legacyType(bStoch, bRuleBased));
//}



@Override
public UnitInfo getUnitInfo() throws UnsupportedOperationException {
	if (unitInfo != null) {
		return unitInfo;
	}
	if (bioModel == null) {
		throw new UnsupportedOperationException("bioModel not set yet");
	}

	Model m = bioModel.getModel();
	ModelUnitSystem unitSys = m.getUnitSystem();
	VCUnitDefinition tu = unitSys.getTimeUnit();
	String sym = tu.getSymbol();
	unitInfo = new UnitInfo(sym);
	return unitInfo;
}

@Override
protected Object clone() throws CloneNotSupportedException {
	// TODO Auto-generated method stub
	return super.clone();
}

/**
 * @return non-null applicationType
 */
public Application getApplicationType() {
	return applicationType;
}

/**
 * Sets the analysisTasks index property (cbit.vcell.modelopt.AnalysisTask[]) value.
 * @param index The index value into the property array.
 * @param analysisTasks The new value for the property.
 * @see #getAnalysisTasks
 */
public void addAnalysisTask(AnalysisTask analysisTask) throws PropertyVetoException {
	if (fieldAnalysisTasks==null){
		setAnalysisTasks(new AnalysisTask[] { analysisTask });
	}else{
		AnalysisTask[] newAnalysisTasks = (AnalysisTask[])BeanUtils.addElement(fieldAnalysisTasks,analysisTask);
		setAnalysisTasks(newAnalysisTasks);
	}
}

public BioEvent createBioEvent() throws PropertyVetoException {
	String preferredName = "event0";
	String eventName = getFreeEventName(preferredName);
	BioEvent bioEvent = new BioEvent(eventName, this);
	return addBioEvent(bioEvent);
}

public BioEvent addBioEvent(BioEvent bioEvent) throws PropertyVetoException {
	if (fieldBioEvents == null){
		setBioEvents(new BioEvent[] { bioEvent });
	}else{
		BioEvent[] newBioEvents = (BioEvent[])BeanUtils.addElement(fieldBioEvents, bioEvent);
		setBioEvents(newBioEvents);
	}
	return bioEvent;
}

/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulations
 */
public Simulation addNewSimulation(String simNamePrefix, MathMappingCallback callback, NetworkGenerationRequirements networkGenerationRequirements) throws java.beans.PropertyVetoException {
	refreshMathDescription(callback, networkGenerationRequirements);
	if (bioModel==null){
		throw new RuntimeException("cannot add simulation, bioModel not set yet");
	}
	//
	// get free name for new Simulation.
	//
	Simulation sims[] = bioModel.getSimulations();
	String newSimName = null;
	for (int i = 0; newSimName==null && i < 100; i++){
		String proposedName = simNamePrefix+i;
		boolean bFound = false;
		for (int j = 0; !bFound && j < sims.length; j++){
			if (sims[j].getName().equals(proposedName)){
				bFound = true;
			}
		}
		if (!bFound){
			newSimName = proposedName;
		}
	}
	if (newSimName==null){
		throw new RuntimeException("failed to find name for new Simulation");
	}

	//
	// create new Simulation and add to BioModel.
	//
	Simulation newSimulation = new Simulation(getMathDescription());
	newSimulation.setName(newSimName);	
	
	bioModel.addSimulation(newSimulation);

	return newSimulation;
}

public SimulationContextParameter createSimulationContextParameter() {
	String name = "appParm0";
	while (getSimulationContextParameter(name)!=null){
		name = TokenMangler.getNextEnumeratedToken(name);
	}
	Expression expression = new Expression(1.0);
	int role = SimulationContext.ROLE_UserDefined;
	VCUnitDefinition unit = getModel().getUnitSystem().getInstance_DIMENSIONLESS();
	SimulationContextParameter parameter = new SimulationContextParameter(name, expression, role, unit);
	try {
		addSimulationContextParameter(parameter);
		return parameter;
	} catch (PropertyVetoException e) {
		e.printStackTrace();
		throw new RuntimeException("error creating new application parameter: "+e.getMessage(),e);
	}
}

public void refreshMathDescription(MathMappingCallback callback, NetworkGenerationRequirements networkGenerationRequirements) {
	//The code below is used to check if the sizes are ready for required models.
	try {
		checkValidity();
	} catch (MappingException e1) {
		e1.printStackTrace(System.out);
		throw new RuntimeException(e1.getMessage());
	}
	
	if (getMathDescription()==null){
//		throw new RuntimeException("Application "+getName()+" has no generated Math, cannot add simulation");
		try {
			setMathDescription(createNewMathMapping(callback, networkGenerationRequirements).getMathDescription());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
				"Application '"+getName()+"' has no generated Math\n"+
				"Failed to generate new Math\n"+
				e.getMessage()
			);
		}
	}
}

	
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	PropertyChangeListenerProxyVCell.addProxyListener(getPropertyChange(), listener);
}


/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulations
 */
public void addSimulation(Simulation newSimulation) throws java.beans.PropertyVetoException {
	if (newSimulation.getMathDescription() == null){
		throw new IllegalArgumentException("cannot add simulation '"+newSimulation.getName()+"', has no MathDescription");
	}
	if (newSimulation.getMathDescription() != getMathDescription()){
		throw new IllegalArgumentException("cannot add simulation '"+newSimulation.getName()+"', has different MathDescription");
	}
	if (bioModel==null){
		throw new RuntimeException("cannot add simulation, bioModel not set yet");
	}
	bioModel.addSimulation(newSimulation);
}


public void addSimulationContextParameter(SimulationContextParameter scParameter) throws PropertyVetoException {
	if (!contains(scParameter)){
		SimulationContextParameter newSCParameters[] = (SimulationContextParameter[])BeanUtils.addElement(fieldSimulationContextParameters,scParameter);
		setSimulationContextParameters(newSCParameters);
	}	
}   

/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (4/24/2003 3:34:38 PM)
 */
public void clearVersion() {
	version = null;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {
	SimulationContext simContext = null;
	if (!(object instanceof SimulationContext)){
		return false;
	}else{
		simContext = (SimulationContext)object;
	}
	if(simContext.applicationType != applicationType)
	{
		return false;
	}
	
	if(simContext.bConcentration != bConcentration)
	{
		return false;
	}
	 
	if (!Compare.isEqual(getName(),simContext.getName())){
		return false;
	}
	if (!Compare.isEqual(getDescription(),simContext.getDescription())){
		return false;
	}
	
//	if (!Compare.isEqualOrNull(meshSpec,simContext.meshSpec)){
//		return false;
//	}
	
	if (!Compare.isEqualOrNull(getCharacteristicSize(),simContext.getCharacteristicSize())){
		return false;
	}
	
	if (!Compare.isEqual(geoContext,simContext.geoContext)){
		return false;
	}
	
	if (!Compare.isEqual(reactionContext,simContext.reactionContext)){
		return false;
	}

	if (!Compare.isEqualOrNull(mathDesc,simContext.mathDesc)){
		return false;
	}

	if (!Compare.isEqual(fieldElectricalStimuli,simContext.fieldElectricalStimuli)){
		return false;
	}

	if (!Compare.isEqualOrNull(fieldGroundElectrode,simContext.fieldGroundElectrode)){
		return false;
	}

	if (!Compare.isEqualOrNull(fieldAnalysisTasks,simContext.fieldAnalysisTasks)){
		return false;
	}
	
	if (!Compare.isEqualOrNull(fieldBioEvents,simContext.fieldBioEvents)){
		return false;
	}

	if (!Compare.isEqualOrNull(spatialObjects,simContext.spatialObjects)){
		return false;
	}

	if (!Compare.isEqualOrNull(spatialProcesses,simContext.spatialProcesses)){
		return false;
	}

	if (!outputFunctionContext.compareEqual(simContext.outputFunctionContext)){
		return false;
	}
	
	if(!Compare.isEqualOrNull(getDataContext(), simContext.getDataContext())){
		return false;
	}	
	
	if(!Compare.isEqualOrNull(getMicroscopeMeasurement(), simContext.getMicroscopeMeasurement())){
		return false;
	}

	if(!Compare.isEqualOrNull(getNetworkConstraints(), simContext.getNetworkConstraints())){
		return false;
	}

	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:37:02 PM)
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public boolean contains(SimulationContextParameter scParameter) {
	for (int i=0;i<fieldSimulationContextParameters.length;i++){
		if (fieldSimulationContextParameters[i].equals(scParameter)){
			return true;
		}
	}
	return false;
}

/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulations
 */
public AnalysisTask copyAnalysisTask(AnalysisTask analysisTask) throws java.beans.PropertyVetoException, ExpressionException, MappingException, MathException {

	if (analysisTask instanceof ParameterEstimationTask){
		String parameterEstimationName = analysisTask.getName()+" Copy";

		AnalysisTask analysisTasks[] = getAnalysisTasks();
		boolean found = true;
		while (found) {
			found = false;
			parameterEstimationName = TokenMangler.getNextEnumeratedToken(parameterEstimationName);
			for (int i = 0;analysisTasks!=null && i < analysisTasks.length; i++){
				if (analysisTasks[i].getName().equals(parameterEstimationName)){
					found = true;
					continue;
				}
			}
		}

		ParameterEstimationTask newParameterEstimationTask = new ParameterEstimationTask(this, (ParameterEstimationTask)analysisTask);
		newParameterEstimationTask.setName(parameterEstimationName);
		addAnalysisTask(newParameterEstimationTask);
		return newParameterEstimationTask;
	}else{
		throw new RuntimeException("don't know how to copy AnalysisTask of type "+analysisTask.getClass().getName());
	}
}


/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulations
 */
public Simulation copySimulation(Simulation simulation) throws java.beans.PropertyVetoException {
	if (getMathDescription()==null){
		throw new RuntimeException("Application "+getName()+" has no generated Math, cannot add simulation");
	}
	if (simulation.getMathDescription() != getMathDescription()){
		throw new IllegalArgumentException("cannot copy simulation '"+simulation.getName()+"', has different MathDescription than Application");
	}
	if (bioModel==null){
		throw new RuntimeException("cannot add simulation, bioModel not set yet");
	}
	//
	// get free name for copied Simulation.
	//
	Simulation sims[] = bioModel.getSimulations();
	String newSimName = null;
	for (int i = 0; newSimName==null && i < 100; i++){
		String proposedName = "Copy of "+simulation.getName() + ((i>0)?(" "+i):(""));
		boolean bFound = false;
		for (int j = 0; !bFound && j < sims.length; j++){
			if (sims[j].getName().equals(proposedName)){
				bFound = true;
			}
		}
		if (!bFound){
			newSimName = proposedName;
		}
	}
	if (newSimName==null){
		throw new RuntimeException("failed to find name for copied Simulation");
	}

	//
	// create new Simulation and add to BioModel.
	//
	Simulation newSimulation = new Simulation(simulation);
	newSimulation.setName(newSimName);
	
	bioModel.addSimulation(newSimulation);

	return newSimulation;
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 1:41:57 PM)
 * @return cbit.vcell.solver.Simulation[]
 * @param allSimulations cbit.vcell.solver.Simulation[]
 */
private Simulation[] extractLocalSimulations(Simulation[] allSimulations) {
	ArrayList<Simulation> list = new ArrayList<Simulation>();
	for (int i = 0; i < allSimulations.length; i++){
		if (allSimulations[i].getMathDescription()==getMathDescription()){
			list.add(allSimulations[i]);
		}
	}
	Simulation localSimulations[] = list.toArray(new Simulation[list.size()]);
	return localSimulations;
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
public void fireVetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(evt);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, int oldValue, int newValue) throws PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(String propertyName, boolean oldValue, boolean newValue) throws PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}

/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 9:30:09 AM)
 * @param issueVector java.util.Vector
 */
public static final String IssueInsufficientIterations = "Max Iterations number may be insufficient.";
public static final String IssueInsufficientMolecules = "Max Molecules / Species number may be insufficient.";
public void gatherIssues(IssueContext issueContext, List<Issue> issueVector, boolean bIgnoreMathDescription) {
//	issueContext = issueContext.newChildContext(ContextType.SimContext, this);
	if(applicationType.equals(Application.RULE_BASED_STOCHASTIC)) {
		for(ReactionRuleSpec rrs : getReactionContext().getReactionRuleSpecs()) {
			if(rrs.isExcluded()) {
				continue;
			}
			ReactionRule rr = rrs.getReactionRule();
			if(rr.getReactantPatterns().size() > 2) {
				String message = "NFSim doesn't support more than 2 reactants within a reaction rule.";
				issueVector.add(new Issue(rr, issueContext, IssueCategory.Identifiers, message, Issue.Severity.WARNING));
			}
			if(rr.isReversible() && rr.getProductPatterns().size() > 2) {
				String message = "NFSim doesn't support more than 2 products within a reversible reaction rule.";
				issueVector.add(new Issue(rr, issueContext, IssueCategory.Identifiers, message, Issue.Severity.WARNING));
			}
		}
		for(ReactionSpec rrs : getReactionContext().getReactionSpecs()) {
			if(rrs.isExcluded()) {
				continue;
			}
			ReactionStep rs = rrs.getReactionStep();
			if(rs.getNumReactants() > 2) {
				String message = "NFSim doesn't support more than 2 reactants within a reaction step.";
				issueVector.add(new Issue(rs, issueContext, IssueCategory.Identifiers, message, Issue.Severity.WARNING));
			}
			if(rs.isReversible() && rs.getNumProducts() > 2) {
				String message = "NFSim doesn't support more than 2 products within a reversible reaction step.";
				issueVector.add(new Issue(rs, issueContext, IssueCategory.Identifiers, message, Issue.Severity.WARNING));
			}
		}
		// we give warning when we have plain reactions with participants with patterns; 
		// making rules from these may result in inconsistent interpretation for the constant rates
		boolean isParticipantWithPattern = false;
		for(ReactionSpec rrs : getReactionContext().getReactionSpecs()) {
			if(rrs.isExcluded()) {
				continue;
			}
			ReactionStep rs = rrs.getReactionStep();
			for(Reactant r : rs.getReactants()) {
				if(r.getSpeciesContext().hasSpeciesPattern()) {
					isParticipantWithPattern = true;
					break;
				}
			}
			if(isParticipantWithPattern) {
				break;
			}
			for(Product p : rs.getProducts()) {
				if(p.getSpeciesContext().hasSpeciesPattern()) {
					isParticipantWithPattern = true;
					break;
				}
			}
			if(isParticipantWithPattern) {
				break;
			}
		}
		if(isParticipantWithPattern) {
			String message = SimulationContext.rateWarning2;
			String tooltip = SimulationContext.rateWarning;
			issueVector.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, tooltip, Issue.Severity.WARNING));
		}
		for(Structure struct : getModel().getStructures()) {
			String name = struct.getName();
			if (!name.equals(TokenMangler.fixTokenStrict(name))) {
				String msg = "'" + name + "' not legal identifier for rule-based stochastic applications, try '"+TokenMangler.fixTokenStrict(name)+"'.";
				issueVector.add(new Issue(struct, issueContext, IssueCategory.Identifiers, msg, Issue.Severity.ERROR));
			}
		}
	}
	if (fieldBioEvents!=null){
		for (BioEvent bioEvent : fieldBioEvents){
			bioEvent.gatherIssues(issueContext, issueVector);
		}
	}
	if (spatialObjects!=null){
		for (SpatialObject spatialObject : spatialObjects){
			spatialObject.gatherIssues(issueContext, issueVector);
		}
	}
	if (spatialProcesses!=null){
		for (SpatialProcess spatialProcess : spatialProcesses){
			spatialProcess.gatherIssues(issueContext, issueVector);
		}
	}
	
	if(applicationType.equals(Application.NETWORK_DETERMINISTIC) && getModel().getRbmModelContainer().getMolecularTypeList().size() > 0) {
		// we're going to use network transformer to flatten (or we already did)
		if(isInsufficientIterations()) {
			issueVector.add(new Issue(this, issueContext, IssueCategory.RbmNetworkConstraintsBad, IssueInsufficientIterations, Issue.Severity.WARNING));
		}
		if(isInsufficientMaxMolecules()) {
			issueVector.add(new Issue(this, issueContext, IssueCategory.RbmNetworkConstraintsBad, IssueInsufficientMolecules, Issue.Severity.WARNING));
		}
	}
	
	getReactionContext().gatherIssues(issueContext, issueVector);
	getGeometryContext().gatherIssues(issueContext, issueVector);
	if (fieldAnalysisTasks != null) {
		for (AnalysisTask analysisTask : fieldAnalysisTasks) {
			analysisTask.gatherIssues(issueContext, issueVector);
		}
	}
	getOutputFunctionContext().gatherIssues(issueContext, issueVector);
	getMicroscopeMeasurement().gatherIssues(issueContext, issueVector);
	if (getMathDescription()!=null && !bIgnoreMathDescription){
		getMathDescription().gatherIssues(issueContext, issueVector);
	}
	
	if(networkConstraints == null) {
//		issueVector.add(new Issue(this, issueContext, IssueCategory.RbmNetworkConstraintsBad, "Network Constraints is null", Issue.Severity.ERROR));
	} else {
		networkConstraints.gatherIssues(issueContext, issueVector);
	}

}


/**
 * Gets the analysisTasks property (cbit.vcell.modelopt.AnalysisTask[]) value.
 * @return The analysisTasks property value.
 * @see #setAnalysisTasks
 */
public AnalysisTask[] getAnalysisTasks() {
	return fieldAnalysisTasks;
}

public BioEvent[] getBioEvents() {
	return fieldBioEvents;
}

/**
 * Gets the analysisTasks index property (cbit.vcell.modelopt.AnalysisTask) value.
 * @return The analysisTasks property value.
 * @param index The index value into the property array.
 * @see #setAnalysisTasks
 */
public AnalysisTask getAnalysisTasks(int index) {
	return getAnalysisTasks()[index];
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 11:54:10 AM)
 * @return BioModel
 */
public BioModel getBioModel() {
	return bioModel;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Double
 */
public Double getCharacteristicSize() {
	return characteristicSize;
}


/**
 * Gets the description property (java.lang.String) value.
 * @return The description property value.
 * @see #setDescription
 */
public java.lang.String getDescription() {
	return getFieldDescription();
}


/**
 * Gets the electricalStimuli property (cbit.vcell.mapping.ElectricalStimulus[]) value.
 * @return The electricalStimuli property value.
 * @see #setElectricalStimuli
 */
public ElectricalStimulus[] getElectricalStimuli() {
	return fieldElectricalStimuli;
}


/**
 * Gets the electricalStimuli index property (cbit.vcell.mapping.ElectricalStimulus) value.
 * @return The electricalStimuli property value.
 * @param index The index value into the property array.
 * @see #setElectricalStimuli
 */
public ElectricalStimulus getElectricalStimuli(int index) {
	return getElectricalStimuli()[index];
}

public Parameter[] getAllParameters(){
	Vector<Parameter> allParameters = new Vector<Parameter>();
	
	SpeciesContextSpec[] speciesContextSpec =
		getReactionContext().getSpeciesContextSpecs();
	for(int i=0;i<speciesContextSpec.length;i+= 1){
		allParameters.addAll(Arrays.asList(speciesContextSpec[i].getParameters()));
	}
	ReactionSpec[] reactionSpecArr = getReactionContext().getReactionSpecs();
	for(int i=0;i<reactionSpecArr.length;i+= 1){
		allParameters.addAll(Arrays.asList(reactionSpecArr[i].getReactionSpecParameters()));
	}
	StructureMapping[] structureMappingArr = getGeometryContext().getStructureMappings();
	for(int i=0;i<structureMappingArr.length;i+= 1){
		allParameters.addAll(Arrays.asList(structureMappingArr[i].getParameters()));
	}
	ElectricalStimulus[] electricalStimulusArr = getElectricalStimuli();
	for(int i=0;i<electricalStimulusArr.length;i+= 1){
		allParameters.addAll(Arrays.asList(electricalStimulusArr[i].getLocalParameters()));
	}
	Parameter[] parameterArr = getSimulationContextParameters();
	allParameters.addAll(Arrays.asList(parameterArr));
	
	return allParameters.toArray(new Parameter[0]);
	
}

private Hashtable<FieldFunctionArguments, Vector<Expression>> collectFieldFuncAndExpressions() throws MathException, ExpressionException {
	// make sure each field only added once
	Hashtable<FieldFunctionArguments, Vector<Expression>> fieldFuncArgsExpHash =
		new Hashtable<FieldFunctionArguments, Vector<Expression>>();
	
	Parameter[] parameterArr = getAllParameters();
	for(int j=0;j<parameterArr.length;j+= 1){
		FieldUtilities.addFieldFuncArgsAndExpToCollection(
				fieldFuncArgsExpHash,
				parameterArr[j].getExpression());
	}
	
	return fieldFuncArgsExpHash;
}

public void substituteFieldFuncNames(Hashtable<String, ExternalDataIdentifier> oldFieldFuncArgsNameNewID) throws MathException, ExpressionException{
	FieldUtilities.substituteFieldFuncNames(
			oldFieldFuncArgsNameNewID, collectFieldFuncAndExpressions());
	
	FieldUtilities.substituteFieldFuncNames(getMathDescription(),oldFieldFuncArgsNameNewID);
}

public FieldFunctionArguments[] getFieldFunctionArguments() throws MathException, ExpressionException {
	
	return collectFieldFuncAndExpressions().keySet().toArray(new FieldFunctionArguments[0]);
}

/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(java.lang.String identifierString) {
	
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
	return getNameScope().getExternalEntry(identifierString,this);
}


/**
 * This method was created in VisualAge.
 */
public Geometry getGeometry() {
	if (geoContext!=null){
		return geoContext.getGeometry();
	}else{
		return null;
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.GeometryContext
 */
public GeometryContext getGeometryContext() {
	return geoContext;
}


/**
 * Gets the groundElectrode property (cbit.vcell.mapping.Electrode) value.
 * @return The groundElectrode property value.
 * @see #setGroundElectrode
 */
public Electrode getGroundElectrode() {
	return fieldGroundElectrode;
}


/**
 * This method was created in VisualAge.
 * @return KeyValue
 */
public KeyValue getKey() {
	return (version!=null)?version.getVersionKey():null;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 10:17:30 AM)
 * @return SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getLocalEntry(java.lang.String identifier) {
	// try field function(s) first
	if (identifier.equals(MathFunctionDefinitions.fieldFunctionDefinition.getName())) {
		return MathFunctionDefinitions.fieldFunctionDefinition;
	}
	
	SymbolTableEntry ste = getSimulationContextParameter(identifier);
	if (ste != null){
		return ste;
	}
	

	for (SpatialObject spatialObject : spatialObjects){
		SpatialQuantity appQuantity = spatialObject.getSpatialQuantity(identifier);
		if (appQuantity!=null){
			return appQuantity;
		}
	}

	// if dataContext parameter exists, then return it
	ste = getDataContext().getDataSymbol(identifier);
	if (ste != null){
		return ste;
	}

	// if not found in simulationContext, try model level
	ste = getModel().getLocalEntry(identifier);
	if (ste != null){
		return ste;
	}
		
	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (3/18/00 1:01:19 AM)
 * @return cbit.vcell.math.MathDescription
 */
public MathDescription getMathDescription() {
	return mathDesc;
}

public OutputFunctionContext getOutputFunctionContext() {
	return outputFunctionContext;
}

/**
 * This method was created in VisualAge.
 */
public Model getModel() {
	if  (geoContext != null) {
	return geoContext.getModel();
	} else {
		return null;
	}
}


/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public java.lang.String getName() {
	return fieldName;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 10:17:30 AM)
 * @return NameScope
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
 * This method was created in VisualAge.
 * @return cbit.vcell.mapping.ReactionContext
 */
public ReactionContext getReactionContext() {
	return reactionContext;
}

/**
 * 
 * @return non-null membrane Context
 */
public MembraneContext getMembraneContext() {
	return membraneContext;
}

/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 10:37:02 PM)
 * @return boolean
 * @param structure cbit.vcell.model.Structure
 */
public SimulationContextParameter getSimulationContextParameter(String parameterName){
	for (int i=0;i<fieldSimulationContextParameters.length;i++){
		if (fieldSimulationContextParameters[i].getName().equals(parameterName)){
			return fieldSimulationContextParameters[i];
		}
	}
	return null;
}


/**
 * Gets the simulationContextParameters property (cbit.vcell.model.Parameter[]) value.
 * @return The simulationContextParameters property value.
 * @see #setSimulationContextParameters
 */
public SimulationContext.SimulationContextParameter[] getSimulationContextParameters() {
	return fieldSimulationContextParameters;
}

/**
 * Gets the simulationContextParameters index property (cbit.vcell.model.Parameter) value.
 * @return The simulationContextParameters property value.
 * @param index The index value into the property array.
 * @see #setSimulationContextParameters
 */
public SimulationContext.SimulationContextParameter getSimulationContextParameters(int index) {
	return getSimulationContextParameters()[index];
}


/**
 * Gets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @return The simulations property value.
 * @see #setSimulations
 */
public Simulation[] getSimulations() {
	return extractLocalSimulations(bioModel.getSimulations());
}


/**
 * Gets the simulations index property (cbit.vcell.solver.Simulation) value.
 * @return The simulations property value.
 * @param index The index value into the property array.
 * @see #setSimulations
 */
public Simulation getSimulations(int index) {
	return getSimulations()[index];
}

public Simulation getSimulation(String simName)
{
	for(Simulation sim:getSimulations())
	{
		if(sim.getName().equals(simName))
		{
			return sim;
		}
	}
	return null;
}

/**
 * Gets the temperatureKelvin property (double) value.
 * @return The temperatureKelvin property value.
 * @see #setTemperatureKelvin
 */
public double getTemperatureKelvin() {
	return fieldTemperatureKelvin;
}



/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 */
public Version getVersion() {
	return version;
}


/**
 * Accessor for the vetoPropertyChange field.
 */
protected VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}


/**
 * The hasListeners method was generated to support the vetoPropertyChange field.
 */
public synchronized boolean hasListeners(String propertyName) {
	return getVetoPropertyChange().hasListeners(propertyName);
}


/**
 * @deprecated use {@link #getApplicationType()}
 * @return boolean
 */
@Deprecated
public boolean isStoch() {
	return applicationType == Application.NETWORK_STOCHASTIC; 
}

/**
 * @deprecated use {@link #getApplicationType()}
 * @return boolean
 */
public boolean isRuleBased(){
	return  applicationType == Application.RULE_BASED_STOCHASTIC;
}

/**
 * Insert the method's description here.
 * Creation date: (5/31/00 11:28:20 PM)
 * @param event java.beans.PropertyChangeEvent
 */
public void propertyChange(java.beans.PropertyChangeEvent event) {
	if (event.getSource() == getGeometry().getGeometrySpec() ) {
		String pname = event.getPropertyName();
		if (pname.equals("extent")) {
			try {
				characteristicSize = null;
				refreshCharacteristicSize();
			}catch (PropertyVetoException e){
				e.printStackTrace(System.out);
			}
		}
		else if (pname.equals(GeometrySpec.PROPERTY_NAME_GEOMETRY_NAME)) {
			for (SpatialObject spatialObject : spatialObjects){
				try {
					spatialObject.refreshName();
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
			}
			for (SimulationContextParameter appParameter : fieldSimulationContextParameters){
				try {
					appParameter.getExpression().renameBoundSymbols(this.getNameScope());
				} catch (ExpressionBindingException e) {
					e.printStackTrace();
				}
			}
			MathMappingCallback  mmc = new MathMappingCallbackTaskAdapter(null);
				try {
					setMathDescription(createNewMathMapping(mmc, null).getMathDescription());
				} catch (PropertyVetoException | MappingException
						| MathException | MatrixException | ExpressionException
						| ModelException e) {
					throw new RuntimeException("Problem updating math after name change " + event.getOldValue() + " to "
							+ event.getNewValue());
				}
		}
	}
	if (event.getSource() == getModel() && event.getPropertyName().equals("structures")){
		refreshElectrodes();
	}
// we propagate events from geometryContext
	if (event.getSource() == getGeometryContext() && event.getPropertyName().equals(GeometryOwner.PROPERTY_NAME_GEOMETRY)){
		firePropertyChange(GeometryOwner.PROPERTY_NAME_GEOMETRY, event.getOldValue(), event.getNewValue());
	}
	if (event.getSource() == getGeometryContext() && event.getPropertyName().equals("model")){
		firePropertyChange("model", event.getOldValue(), event.getNewValue());
	}
	if (event.getSource() == getBioModel() && event.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_SIMULATIONS)){
		Simulation oldSimulations[] = extractLocalSimulations((Simulation[])event.getOldValue());
		Simulation newSimulations[] = extractLocalSimulations((Simulation[])event.getNewValue());
		boolean bShouldFire = false;
		if (oldSimulations != null || newSimulations != null) {
			if (oldSimulations == null || newSimulations == null) {
				bShouldFire = true;
			}
			if (!bShouldFire) {
				if (oldSimulations.length != newSimulations.length) {
					bShouldFire = true;
				}
			}
			if (!bShouldFire) {
				for (int i = 0; i < oldSimulations.length; i ++) {
					if (oldSimulations[i] != newSimulations[i]) {
						bShouldFire = true;
						break;
					}
				}
			}
		}
		if (bShouldFire) {
			firePropertyChange(PropertyConstants.PROPERTY_NAME_SIMULATIONS,oldSimulations,newSimulations);
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Double
 */
private void refreshCharacteristicSize() throws PropertyVetoException {
	
	Geometry geo = getGeometryContext().getGeometry();
	int dimension = geo.getDimension();
	if (dimension==0){
		setCharacteristicSize(null);
		return;
	}
	Extent extent = geo.getExtent();
	
	if (characteristicSize == null){
		//
		// if characteristicSize is not specified, estimate a 'good' value
		//
		VCImage image = geo.getGeometrySpec().getImage();
		if (image != null){
			double pixelSizeX = extent.getX()/image.getNumX();
			double pixelSizeY = extent.getY()/image.getNumY();
			double pixelSizeZ = extent.getZ()/image.getNumZ();
			switch (dimension){
				case 1:{
					setCharacteristicSize(new Double(pixelSizeX));
					break;
				}
				case 2:{
					setCharacteristicSize(new Double(Math.min(pixelSizeX,pixelSizeY)));
					break;
				}
				case 3:{
					setCharacteristicSize(new Double(Math.min(pixelSizeX,Math.min(pixelSizeY,pixelSizeZ))));
					break;
				}
				default:{
					throw new RuntimeException("unexpected geometry dimension = '"+dimension+"'");
				}
			}
		}else{
			//
			// image doesn't exist, apply heuristics
			//
			//			
			//
			// choose spatial steps to give reasonable size mesh ( < MaxNumberElements)
			//
			// start with very small elements (~0.01 * smallest dimension)
			//
			// if number of elements exceed 100,000 then take larger steps
			//
			long MaxNumberElements = 1000000;
			long MaxPerDimension = 500;
			long totalElements = MaxNumberElements+1;
			double elementLength = Math.min(extent.getX(),Math.min(extent.getY(),extent.getZ())) / (MaxPerDimension);
			
			while (true){
				//
				// calculate total elements based on characteristic length
				//
				int numY = -1;
				int numZ = -1;
				int numX=(int)Math.round(extent.getX()/elementLength);
				if (dimension>1){
					numY=(int)Math.round(extent.getY()/elementLength);
				}else{
					numY=1;
				}
				if (dimension>2){
					numZ=(int)Math.round(extent.getZ()/elementLength);
				}else{
					numZ=1;
				}
				totalElements = numX*numY*numZ;
				if ((numX <= MaxPerDimension) && (numY <= MaxPerDimension) && (numZ <= MaxPerDimension) && (totalElements <= MaxNumberElements)){
					break;
				}else{
					elementLength *= 1.125;
				}
			}	
			setCharacteristicSize(new Double(elementLength));
		}
	}
}


/**
 * This method was created in VisualAge.
 */
public void refreshDependencies() {
	refreshDependencies1(true);
}
public void refreshDependencies1(boolean isRemoveUncoupledParameters) {
	removePropertyChangeListener(this);
	removeVetoableChangeListener(this);
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);

	getGeometryContext().refreshDependencies();
	getReactionContext().refreshDependencies();
	getGeometry().refreshDependencies();
	if (getMathDescription()!=null){
		getMathDescription().refreshDependencies();
		getOutputFunctionContext().refreshDependencies();
	}
	getOutputFunctionContext().refreshDependencies();
	getModel().removePropertyChangeListener(this);
	getModel().addPropertyChangeListener(this);
	getGeometry().getGeometrySpec().removePropertyChangeListener(this);
	getGeometry().getGeometrySpec().addPropertyChangeListener(this);
	getGeometryContext().removePropertyChangeListener(this);
	getGeometryContext().addPropertyChangeListener(this);
	
	for (int i=0;i<fieldSimulationContextParameters.length;i++){
		fieldSimulationContextParameters[i].removeVetoableChangeListener(this);
		fieldSimulationContextParameters[i].removePropertyChangeListener(this);
		fieldSimulationContextParameters[i].addVetoableChangeListener(Parameter.PROPERTYNAME_NAME, this);
		fieldSimulationContextParameters[i].addPropertyChangeListener(this);
		try {
			fieldSimulationContextParameters[i].getExpression().bindExpression(this);
		} catch (ExpressionBindingException e) {
			e.printStackTrace(System.out);
//			throw new RuntimeException("Error binding global parameter '" + fieldSimulationContextParameters[i].getName() + "' to model."  + e.getMessage());
		}
	}

	if (fieldElectricalStimuli!=null){
		for (int i = 0; i < fieldElectricalStimuli.length; i++){
			fieldElectricalStimuli[i].refreshDependencies();
		}
	}
	if (fieldAnalysisTasks!=null){
		for (int i = 0; i < fieldAnalysisTasks.length; i++){
			fieldAnalysisTasks[i].refreshDependencies(isRemoveUncoupledParameters);
		}
	}
	
	if (fieldBioEvents != null) {
		for (int i = 0; i < fieldBioEvents.length; i++) {
			fieldBioEvents[i].refreshDependencies();
		}
	}

	if (spatialObjects != null) {
		for (int i = 0; i < spatialObjects.length; i++) {
			spatialObjects[i].refreshDependencies();
		}
	}

	if (fieldRateRules != null) {
		for (int i = 0; i < fieldRateRules.length; i++) {
			fieldRateRules[i].refreshDependencies();
		}
	}
}


/**
 * This method was created by a SmartGuide.
 */
private void refreshElectrodes(){
	//
	// step through all stimuli
	//
	ElectricalStimulus newStimuli[] = fieldElectricalStimuli;  // if any are added or removed, then create new array
	Structure modelStructures[] = getModel().getStructures();
	for (int i = 0;i < newStimuli.length; i++){
		ElectricalStimulus stimulus = newStimuli[i];
		Electrode electrode = stimulus.getElectrode();
		Feature feature = electrode.getFeature();
		Feature newFeature = null;
		//
		// match up with structures defined within model
		//
		for (int j = 0; j < modelStructures.length; j++){
			Structure modelStructure = modelStructures[j];
			if (modelStructure.compareEqual(feature)){
				newFeature = (Feature)modelStructure;
			}
		}
		//
		// update electrode feature if feature is found in current Model.
		// delete any stimuli that whose electrode belongs to a deleted Feature (not in current Model)
		//
		if (newFeature!=null){
			try {
				electrode.setFeature(newFeature);
			}catch (PropertyVetoException e){
				e.printStackTrace(System.out);
			}
		}else{
			newStimuli = (ElectricalStimulus[])BeanUtils.removeElement(newStimuli,stimulus);
			i--;
		}
	}
	if (newStimuli != fieldElectricalStimuli){
		try {
			setElectricalStimuli(newStimuli);
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
	//
	// check ground electrode
	//
	if (getGroundElectrode()!=null){
		Feature feature = getGroundElectrode().getFeature();
		Feature newFeature = null;
		//
		// match up with structures defined within model
		//
		for (int j = 0; j < modelStructures.length; j++){
			Structure modelStructure = modelStructures[j];
			if (modelStructure.compareEqual(feature)){
				newFeature = (Feature)modelStructure;
			}
		}
		//
		// update groundElectrode feature if feature is found in current Model.  Else delete it.
		//
		if (newFeature!=null){
			try {
				getGroundElectrode().setFeature(newFeature);
			}catch (PropertyVetoException e){
				e.printStackTrace(System.out);
			}
		}else{
			try {
				setGroundElectrode(null);
			}catch (PropertyVetoException e){
				e.printStackTrace(System.out);
			}
		}
	}
}


/**
 * Sets the analysisTasks index property (cbit.vcell.modelopt.AnalysisTask[]) value.
 * @param index The index value into the property array.
 * @param analysisTasks The new value for the property.
 * @see #getAnalysisTasks
 */
public void removeAnalysisTask(AnalysisTask analysisTask) throws PropertyVetoException {
	boolean bFound = false;
	for (int i = 0; fieldAnalysisTasks!=null && i < fieldAnalysisTasks.length; i++){
		if (fieldAnalysisTasks[i] == analysisTask){
			bFound = true;
			break;
		}
	}
	if (!bFound){
		throw new RuntimeException("analysis task not found");
	}
	AnalysisTask[] newAnalysisTasks = (AnalysisTask[])BeanUtils.removeElement(fieldAnalysisTasks,analysisTask);
	setAnalysisTasks(newAnalysisTasks);
}

public void removeBioEvent(BioEvent bioEvent) throws PropertyVetoException {
	boolean bFound = false;
	for (int i = 0; fieldBioEvents!=null && i < fieldBioEvents.length; i++){
		if (fieldBioEvents[i] == bioEvent){
			bFound = true;
			break;
		}
	}
	if (!bFound){
		throw new RuntimeException("event not found");
	}
	BioEvent[] newBioEvents = (BioEvent[])BeanUtils.removeElement(fieldBioEvents,bioEvent);
	setBioEvents(newBioEvents);
}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	PropertyChangeListenerProxyVCell.removeProxyListener(getPropertyChange(), listener);
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulations
 */
public void removeSimulation(Simulation simulation) throws java.beans.PropertyVetoException {
	if (simulation.getMathDescription() != getMathDescription()){
		throw new IllegalArgumentException("cannot remove simulation '"+simulation.getName()+"', has different MathDescription");
	}
	bioModel.removeSimulation(simulation);
}


public void removeSimulationContextParameter(SimulationContext.SimulationContextParameter scParameter) throws PropertyVetoException {

	if (scParameter == null){
		return;
	}	
	if (contains(scParameter)){
		SimulationContext.SimulationContextParameter newSCParameters[] = (SimulationContext.SimulationContextParameter[])BeanUtils.removeElement(fieldSimulationContextParameters,scParameter);
		setSimulationContextParameters(newSCParameters);
	}
}         

/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(listener);
}


/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
	getVetoPropertyChange().removeVetoableChangeListener(propertyName, listener);
}


/**
 * Sets the analysisTasks property (cbit.vcell.modelopt.AnalysisTask[]) value.
 * @param analysisTasks The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getAnalysisTasks
 */
public void setAnalysisTasks(AnalysisTask[] analysisTasks) throws java.beans.PropertyVetoException {
	if (getGeometry() != null && getGeometry().getDimension() > 0) {
		fieldAnalysisTasks = null;
		return;
	}
	AnalysisTask[] oldValue = fieldAnalysisTasks;
	fireVetoableChange(PROPERTY_NAME_ANALYSIS_TASKS, oldValue, analysisTasks);
	fieldAnalysisTasks = analysisTasks;
	firePropertyChange(PROPERTY_NAME_ANALYSIS_TASKS, oldValue, analysisTasks);
}

public void setBioEvents(BioEvent[] bioEvents) throws java.beans.PropertyVetoException {
	BioEvent[] oldValue = fieldBioEvents;
	fireVetoableChange(PROPERTY_NAME_BIOEVENTS, oldValue, bioEvents);
	fieldBioEvents = bioEvents;
	firePropertyChange(PROPERTY_NAME_BIOEVENTS, oldValue, bioEvents);
}

/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 11:54:10 AM)
 * @param newBioModel BioModel
 */
public void setBioModel(BioModel newBioModel) {
	if (this.bioModel!=null){
		this.bioModel.removePropertyChangeListener(this);
		this.bioModel.removeVetoableChangeListener(this);
	}
	bioModel = newBioModel;
	if (newBioModel!=null){
		newBioModel.addPropertyChangeListener(this);
		newBioModel.addVetoableChangeListener(this);
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Double
 */
public void setCharacteristicSize(Double size) throws java.beans.PropertyVetoException {

	if (size == null){
		return;
	}else if (!size.equals(characteristicSize)){
		Double oldSize = characteristicSize;
		this.characteristicSize = size;
		firePropertyChange("characteristicSize",oldSize,size);
	}
	
}


/**
 * Sets the description property (java.lang.String) value.
 * @param description The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getDescription
 */
public void setDescription(java.lang.String description) throws java.beans.PropertyVetoException {
	String oldValue = getFieldDescription();
	fireVetoableChange(PROPERTY_NAME_DESCRIPTION, oldValue, description);
	setFieldDescription(description);
	firePropertyChange(PROPERTY_NAME_DESCRIPTION, oldValue, description);
}


private java.lang.String getFieldDescription() {
	return fieldDescription;
}

private void setFieldDescription(java.lang.String newFieldDescription) {
	this.fieldDescription = truncateForDB(newFieldDescription);
}

private String truncateForDB(String s){
	if(s != null && s.length() > 0){
		StringBuffer temp = new StringBuffer(s);
		while(TokenMangler.getSQLEscapedString(temp.toString()).length() >= Preference.MAX_VALUE_LENGTH){
			temp.deleteCharAt(temp.length()-1);
		}
		if ((s.length()-temp.length()) > 0) {
			System.out.println("-----truncated "+(s.length()-temp.length())+" characters of simcontext='"+getName()+"' to fit DB constraint of "+Preference.MAX_VALUE_LENGTH);
		}
		return temp.toString();
	}
	
	return s;
}

/**
 * Sets the electricalStimuli property (cbit.vcell.mapping.ElectricalStimulus[]) value.
 * @param electricalStimuli The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getElectricalStimuli
 */
public void setElectricalStimuli(ElectricalStimulus[] electricalStimuli) throws java.beans.PropertyVetoException {
	ElectricalStimulus[] oldValue = fieldElectricalStimuli;
	fireVetoableChange("electricalStimuli", oldValue, electricalStimuli);
	fieldElectricalStimuli = electricalStimuli;
	firePropertyChange("electricalStimuli", oldValue, electricalStimuli);
}


/**
 * Sets the electricalStimuli index property (cbit.vcell.mapping.ElectricalStimulus[]) value.
 * @param index The index value into the property array.
 * @param electricalStimuli The new value for the property.
 * @see #getElectricalStimuli
 */
public void setElectricalStimuli(int index, ElectricalStimulus electricalStimuli) {
	ElectricalStimulus oldValue = fieldElectricalStimuli[index];
	fieldElectricalStimuli[index] = electricalStimuli;
	if (oldValue != null && !oldValue.equals(electricalStimuli)) {
		firePropertyChange("electricalStimuli", null, fieldElectricalStimuli);
	};
}


/**
 * This method was created in VisualAge.
 */
public void setGeometry(Geometry geometry) throws MappingException {
	Geometry oldGeometry = this.getGeometry();

	if (geometry != oldGeometry){
		getGeometryContext().setGeometry(geometry);
		try {
			refreshCharacteristicSize();
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
		refreshSpatialObjects();
		oldGeometry.getGeometrySpec().removePropertyChangeListener(this);
		getGeometry().getGeometrySpec().addPropertyChangeListener(this);
		if (geometry != null && geometry.getDimension() > 0) {
			try {
				if (fieldBioEvents != null) {
					setBioEvents(null);
				}
				if (fieldRateRules != null) {
					setRateRules(null);
				}
				if (fieldAnalysisTasks != null) {
					setAnalysisTasks(null);
				}
				if (oldGeometry!=null && oldGeometry.getDimension()==0){	// && geometry != null && geometry.getDimension() > 0
					initializeForSpatial();
				}
			} catch (PropertyVetoException e) {				
				e.printStackTrace(System.out);
				throw new MappingException(e.getMessage());
			}
		}
// now firing from geoContext
//		firePropertyChange("geometry",oldGeometry,geometry);
	}
}

public void refreshSpatialObjects() {
	Geometry geometry = getGeometry();
	if (geometry!=null && geometry.getGeometrySurfaceDescription()!=null && geometry.getGeometrySurfaceDescription().getGeometricRegions()!=null){
		ArrayList<SpatialObject> unmappedSpatialObjects = new ArrayList<SpatialObject>(Arrays.asList(spatialObjects));
		ArrayList<GeometricRegion> mappedRegions = new ArrayList<GeometricRegion>();
		//
		// for existing spatial objects, rebind to new geometry
		//
		for (SpatialObject spatialObject : spatialObjects){
			if (spatialObject instanceof VolumeRegionObject){
				VolumeRegionObject volRegionObj = (VolumeRegionObject)spatialObject;
				SubVolume existingSubvolume = volRegionObj.getSubVolume();
				Integer existingRegionID = volRegionObj.getRegionID();
				SubVolume newSubvolume = geometry.getGeometrySpec().getSubVolume(existingSubvolume.getName());
				if (newSubvolume!=null && geometry.getGeometrySurfaceDescription().getGeometricRegions()!=null){
					for (GeometricRegion newRegion : geometry.getGeometrySurfaceDescription().getGeometricRegions(newSubvolume)){
						VolumeGeometricRegion newVolRegion = (VolumeGeometricRegion)newRegion;
						if (newVolRegion.getRegionID() == existingRegionID){
							((VolumeRegionObject) spatialObject).setSubVolume(newSubvolume);
							mappedRegions.add(newVolRegion);
							unmappedSpatialObjects.remove(spatialObject);
						}
					}
				}
			}
			if (spatialObject instanceof SurfaceRegionObject){
				SurfaceRegionObject surfaceRegionObj = (SurfaceRegionObject)spatialObject;
				SubVolume existingInsideSubvolume = surfaceRegionObj.getInsideSubVolume();
				SubVolume existingOutsideSubvolume = surfaceRegionObj.getOutsideSubVolume();
				Integer existingInsideRegionID = surfaceRegionObj.getInsideRegionID();
				Integer existingOutsideRegionID = surfaceRegionObj.getOutsideRegionID();
				SubVolume newInsideSubvolume = geometry.getGeometrySpec().getSubVolume(existingInsideSubvolume.getName());
				SubVolume newOutsideSubvolume = geometry.getGeometrySpec().getSubVolume(existingOutsideSubvolume.getName());
				if (newInsideSubvolume != null && newOutsideSubvolume != null){
					SurfaceClass surfaceClass = geometry.getGeometrySurfaceDescription().getSurfaceClass(newInsideSubvolume, newOutsideSubvolume);
					for (GeometricRegion newRegion : geometry.getGeometrySurfaceDescription().getGeometricRegions(surfaceClass)){
						SurfaceGeometricRegion newSurfaceRegion = (SurfaceGeometricRegion)newRegion;
						GeometricRegion[] adjacentRegions = newSurfaceRegion.getAdjacentGeometricRegions();
						if (adjacentRegions.length==2 && adjacentRegions[0] instanceof VolumeGeometricRegion && adjacentRegions[1] instanceof VolumeGeometricRegion){
							VolumeGeometricRegion adjVolRegion0 = (VolumeGeometricRegion)adjacentRegions[0];
							VolumeGeometricRegion adjVolRegion1 = (VolumeGeometricRegion)adjacentRegions[1];
							if (adjVolRegion0.getSubVolume() == newInsideSubvolume && adjVolRegion0.getRegionID() == existingInsideRegionID &&
								adjVolRegion1.getSubVolume() == newOutsideSubvolume && adjVolRegion1.getRegionID() == existingOutsideRegionID){
								
								surfaceRegionObj.setInsideSubVolume(newInsideSubvolume);
								surfaceRegionObj.setOutsideSubVolume(newOutsideSubvolume);
								mappedRegions.add(newSurfaceRegion);
								unmappedSpatialObjects.remove(spatialObject);
							}
							if (adjVolRegion0.getSubVolume() == newOutsideSubvolume && adjVolRegion0.getRegionID() == existingOutsideRegionID &&
								adjVolRegion1.getSubVolume() == newInsideSubvolume && adjVolRegion1.getRegionID() == existingInsideRegionID){

								surfaceRegionObj.setInsideSubVolume(newInsideSubvolume);
								surfaceRegionObj.setOutsideSubVolume(newOutsideSubvolume);
								mappedRegions.add(newSurfaceRegion);
								unmappedSpatialObjects.remove(spatialObject);
							}
						}
					}
				}
			}
		}
		
		//
		// for geometric regions not represented as spatial objects, add them
		//
		ArrayList<GeometricRegion> unmappedRegions = new ArrayList<GeometricRegion>(Arrays.asList(geometry.getGeometrySurfaceDescription().getGeometricRegions()));
		unmappedRegions.removeAll(mappedRegions);
		for (GeometricRegion unmappedRegion : unmappedRegions){
			if (unmappedRegion instanceof VolumeGeometricRegion){
				VolumeGeometricRegion unmappedVolRegion = (VolumeGeometricRegion) unmappedRegion;
				try {
					VolumeRegionObject vro = new VolumeRegionObject(unmappedVolRegion.getSubVolume(), unmappedVolRegion.getRegionID(), this);
					addSpatialObject(vro);
				} catch (PropertyVetoException e) {
					e.printStackTrace();
				}
			}else if (unmappedRegion instanceof SurfaceGeometricRegion){
				SurfaceGeometricRegion unmappedSurfRegion = (SurfaceGeometricRegion) unmappedRegion;
				GeometricRegion[] adjacentRegions = unmappedSurfRegion.getAdjacentGeometricRegions();
				if (adjacentRegions.length==2 && adjacentRegions[0] instanceof VolumeGeometricRegion && adjacentRegions[1] instanceof VolumeGeometricRegion){
					VolumeGeometricRegion volRegion0 = (VolumeGeometricRegion)adjacentRegions[0];
					VolumeGeometricRegion volRegion1 = (VolumeGeometricRegion)adjacentRegions[1];
					SubVolume insideSubVolume = volRegion0.getSubVolume();
					SubVolume outsideSubVolume = volRegion1.getSubVolume();
					int insideRegionID = volRegion0.getRegionID();
					int outsideRegionID = volRegion1.getRegionID();
					SurfaceClass surfaceClass = geometry.getGeometrySurfaceDescription().getSurfaceClass(insideSubVolume,outsideSubVolume);
					try {
						addSpatialObject(new SurfaceRegionObject(insideSubVolume, insideRegionID, outsideSubVolume, outsideRegionID, this));
					} catch (PropertyVetoException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		//
		// for spatial objects no longer represented in the new geometry, try to delete one-by-one ... and cancel if vetoed.
		//
		try {
			for (SpatialObject unmappedSpatialObject : unmappedSpatialObjects){
				if (unmappedSpatialObject instanceof VolumeRegionObject){
					System.err.println("volume region spatial object '"+unmappedSpatialObject.getName()+"' not found in geometry, delete.");
					removeSpatialObject(unmappedSpatialObject);
				}
				if (unmappedSpatialObject instanceof SurfaceRegionObject){
					System.err.println("surface region spatial object '"+unmappedSpatialObject.getName()+"' not found in geometry, delete.");
					removeSpatialObject(unmappedSpatialObject);
				}
				if (unmappedSpatialObject instanceof PointObject){
					System.err.println("point spatial object '"+unmappedSpatialObject.getName()+"' not found in geometry, this is expected.");
//					removeSpatialObject(unmappedSpatialObject);
				}
			}
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}
}

/**
 * Sets the groundElectrode property (cbit.vcell.mapping.Electrode) value.
 * @param groundElectrode The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getGroundElectrode
 */
public void setGroundElectrode(Electrode groundElectrode) throws java.beans.PropertyVetoException {
	Electrode oldValue = fieldGroundElectrode;
	fireVetoableChange("groundElectrode", oldValue, groundElectrode);
	fieldGroundElectrode = groundElectrode;
	firePropertyChange("groundElectrode", oldValue, groundElectrode);
}


//private void setIsStoch(boolean newIsStoch) {
//	bStoch = newIsStoch;
//}
//
//private void setIsRuleBased(boolean newIsRuleBased) {
//	bRuleBased = newIsRuleBased;
//}


/**
 * This method was created in VisualAge.
 * @param mathDesc cbit.vcell.math.MathDescription
 */
public void setMathDescription(MathDescription argMathDesc) throws PropertyVetoException {
	Object oldValue = this.mathDesc;
	fireVetoableChange("mathDescription",oldValue,argMathDesc);
	this.mathDesc = argMathDesc;
	firePropertyChange("mathDescription",oldValue,argMathDesc);
}

/**
 * This method was created in VisualAge.
 */
public void setModel(Model model) throws MappingException, PropertyVetoException {
	getReactionContext().setModel(model);
	getGeometryContext().setModel(model);
	refreshElectrodes();
// no need to fire event since we propagate from geoContext
}


/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getName
 */
public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
	String oldValue = fieldName;
	fireVetoableChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, name);
	fieldName = name;
	firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, name);
}

/**
 * Sets the simulationContextParameters property (cbit.vcell.model.Parameter[]) value.
 * @param simulationContextParameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulationContextParameters
 */
public void setSimulationContextParameters(SimulationContext.SimulationContextParameter[] simulationContextParameters) throws java.beans.PropertyVetoException {
	SimulationContextParameter[] oldValue = fieldSimulationContextParameters;
	fireVetoableChange(PROPERTY_NAME_SIMULATIONCONTEXTPARAMETERS, oldValue, simulationContextParameters);
	fieldSimulationContextParameters = simulationContextParameters;
	firePropertyChange(PROPERTY_NAME_SIMULATIONCONTEXTPARAMETERS, oldValue, simulationContextParameters);
	
	SimulationContextParameter newValue[] = simulationContextParameters;
	if (oldValue != null) {
		for (int i=0;i<oldValue.length;i++){	
			oldValue[i].removePropertyChangeListener(this);
			oldValue[i].removeVetoableChangeListener(this);
		}
	}
	if (newValue != null) {
		for (int i=0;i<newValue.length;i++){
			newValue[i].addPropertyChangeListener(this);
			newValue[i].addVetoableChangeListener(Parameter.PROPERTYNAME_NAME, this);
		}
	}
	
}


/**
 * Sets the temperatureKelvin property (double) value.
 * @param temperatureKelvin The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getTemperatureKelvin
 */
public void setTemperatureKelvin(double temperatureKelvin) throws java.beans.PropertyVetoException {
	double oldValue = fieldTemperatureKelvin;
	fireVetoableChange("temperatureKelvin", new Double(oldValue), new Double(temperatureKelvin));
	fieldTemperatureKelvin = temperatureKelvin;
	firePropertyChange("temperatureKelvin", new Double(oldValue), new Double(temperatureKelvin));
}

/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	return "SimContext@"+Integer.toHexString(hashCode())+"("+((version!=null)?version.toString():getName())+")";
}


	/**
	 * This method gets called when a constrained property is changed.
	 *
	 * @param     evt a <code>PropertyChangeEvent</code> object describing the
	 *   	      event source and the property that has changed.
	 * @exception PropertyVetoException if the recipient wishes the property
	 *              change to be rolled back.
	 */
public void vetoableChange(java.beans.PropertyChangeEvent evt) throws java.beans.PropertyVetoException {
	TokenMangler.checkNameProperty(this, "application", evt);
	
	if (evt.getSource() == getBioModel() && evt.getPropertyName().equals("simulations")){
		Simulation oldSimulations[] = extractLocalSimulations((Simulation[])evt.getOldValue());
		Simulation newSimulations[] = extractLocalSimulations((Simulation[])evt.getNewValue());
		fireVetoableChange("simulations",oldSimulations,newSimulations);
	}
	
	if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_BIOEVENTS)) {
		BioEvent newBioevents[] = (BioEvent[])evt.getNewValue();
		if (newBioevents==null){
			return;
		}
		//
		// check that names are not duplicated and that no names are ReservedSymbols
		//
		HashSet<String> nameSet = new HashSet<String>();
		for (int i=0;i<newBioevents.length;i++){
			String name = newBioevents[i].getName();
			if (nameSet.contains(name)){
				throw new PropertyVetoException("multiple bioevents with same name '"+name+"' defined",evt);
			}
			if (getEntry(name)!=null){
				throw new PropertyVetoException("cannot use existing symbol '"+name+"' as a bioevent name",evt);
			}
			nameSet.add(name);
		}
	}
}

public BioEvent getBioEvent(String name) {
	if (fieldBioEvents != null) {
		for (BioEvent be : fieldBioEvents) {
			if (be.getName().equals(name)) {
				return be;
			}
		}
	}
	return null;
}

public void checkValidity() throws MappingException
{
	//spatial
	if(getGeometry().getDimension() > 0) {
		//
		// fail if any enabled Reactions have LumpedKinetics.
		//
//		StringBuffer buffer = new StringBuffer();
//		ReactionSpec[] reactionSpecs = getReactionContext().getReactionSpecs();
//		for (int i = 0; i < reactionSpecs.length; i++) {
//			if (!reactionSpecs[i].isExcluded() && reactionSpecs[i].getReactionStep().getKinetics() instanceof LumpedKinetics){
//				buffer.append("reaction \""+reactionSpecs[i].getReactionStep().getName()+"\" in compartment \""+reactionSpecs[i].getReactionStep().getStructure().getName()+"\"\n");
//			}
//		}
//		if (buffer.length()>0){
//			throw new MappingException("Spatial application \""+getName()+"\" cannot process reactions with spatially lumped kinetics, see kinetics for :\n"+buffer.toString());			
//				
//		}
	}else{
		// old-style ODE models should still work
		if (applicationType == Application.NETWORK_DETERMINISTIC && getGeometryContext().isAllVolFracAndSurfVolSpecified() && getGeometryContext().isAllSizeSpecifiedNull()){
			return; // old style ODE models
		}
		// otherwise, all sizes should be present and positive.
		if (!getGeometryContext().isAllSizeSpecifiedPositive()){
			throw new MappingException("Application "+getName()+":\nAll structure sizes must be assigned positive values.\nPlease go to StructureMapping tab to check the sizes.");
		}
		// if rate rules are present, if any species has a rate rules, it should not be a reaction participant in any reaction.
		RateRule[] rateRules = getRateRules();
		if (rateRules != null && rateRules.length > 0) {
			if (getModel() != null) {
				ReactionStep[] reactionSteps = getModel().getReactionSteps();
				ReactionParticipant[] reactionParticipants = null;
				for (ReactionStep rs : reactionSteps) {
					reactionParticipants = rs.getReactionParticipants();
					for (ReactionParticipant rp : reactionParticipants) {
						if (rp instanceof Reactant || rp instanceof Product) {
							if (getRateRule(rp.getSpeciesContext()) != null) {
								throw new RuntimeException("Species '" + rp.getSpeciesContext().getName() + "' is a reactant/product in reaction '" + rs.getName() + "' ; cannot also have a rate rule." );
							}
						}
					}
				}
			}
		}
	}
}
public boolean isUsingConcentration() {
	return bConcentration;
}

public boolean isUsingMassConservationModelReduction() {
	return bMassConservationModelReduction;
}

public void setUsingConcentration(boolean bUseConcentration) /*throws MappingException, PropertyVetoException*/ {
	if(applicationType == Application.NETWORK_STOCHASTIC || applicationType == Application.RULE_BASED_STOCHASTIC)
	{
		boolean oldValue = bConcentration;
		bConcentration = bUseConcentration;
		firePropertyChange(PROPERTY_NAME_USE_CONCENTRATION, oldValue, bConcentration);
	}
}

public boolean isRandomizeInitCondition() {
	return bRandomizeInitCondition;
}

public void setRandomizeInitConditions(boolean bRandomize) {
	if(applicationType == Application.NETWORK_STOCHASTIC || applicationType == Application.RULE_BASED_STOCHASTIC)
	{
		boolean oldValue = bRandomizeInitCondition;
		bRandomizeInitCondition = bRandomize;
		firePropertyChange(PROPERTY_NAME_RANDOMIZE_INIT_CONDITIONS, oldValue, bRandomizeInitCondition);
	}
}


//specially created for loading from database, used in ServerDocumentManager.saveBioModel()
public void updateSpeciesIniCondition(SimulationContext simContext) throws MappingException, PropertyVetoException
{
	boolean bUseConcentration = simContext.isUsingConcentration();
	this.setUsingConcentration(bUseConcentration);
	SpeciesContextSpec[] refScSpec = simContext.getReactionContext().getSpeciesContextSpecs();
//	SpeciesContextSpec[] scSpec = this.getReactionContext().getSpeciesContextSpecs();
	for(int i = 0; i<refScSpec.length; i++ )
	{
		SpeciesContext refSc = refScSpec[i].getSpeciesContext();
		SpeciesContextSpec scSpec = this.getReactionContext().getSpeciesContextSpec(refSc);
		try {
			scSpec.getInitialConcentrationParameter().setExpression(refScSpec[i].getInitialConcentrationParameter().getExpression());
			scSpec.getInitialCountParameter().setExpression(refScSpec[i].getInitialCountParameter().getExpression());
		} catch (ExpressionBindingException e) {
			e.printStackTrace();
			throw new MappingException(e.getMessage());
		} 
	}
}

public void convertSpeciesIniCondition(boolean bUseConcentration) throws MappingException, PropertyVetoException
{
	try {
		getReactionContext().convertSpeciesIniCondition(bUseConcentration);
	} catch (ExpressionException e) {
		e.printStackTrace();
		throw new MappingException(e.getMessage());
	}
}

public MathType getMathType()
{
	return applicationType.mathType;
}

public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
	getModel().getEntries(entryMap);
	for (SymbolTableEntry ste : fieldSimulationContextParameters) {
		entryMap.put(ste.getName(), ste);
	}
	for (SymbolTableEntry ste : dataContext.getDataSymbols()){
		entryMap.put(ste.getName(), ste);
	}
	for (SpatialObject spatialObject : spatialObjects){
		for (SpatialQuantity spatialQuantity : spatialObject.getSpatialQuantities()){
			if (spatialQuantity.isEnabled()){
				entryMap.put(spatialQuantity.getName(),spatialQuantity);
			}
		}
	}
	entryMap.put(MathFunctionDefinitions.fieldFunctionDefinition.getName(), MathFunctionDefinitions.fieldFunctionDefinition);
}


public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	getNameScope().getExternalEntries(entryMap);		
}

public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter() {
	AutoCompleteSymbolFilter stef = new AutoCompleteSymbolFilter() {		
		public boolean accept(SymbolTableEntry ste) {
			if (ste instanceof SpeciesContextSpecParameter) {
				return false;
			}
			int dimension = getGeometry().getDimension();
			if (dimension == 0) {
				if (ste == getModel().getX() || ste == getModel().getY() || ste == getModel().getZ()) {
					return false;
				}
			} else {
				if (dimension < 3) {
					if (ste == getModel().getZ()) {
						return false;
					}
					if (dimension < 2) {
						if (ste == getModel().getY()) {
							return false;
						}
					}
				}
			}
			return true;
		}

		public boolean acceptFunction(String funcName) {
			return true;
		}
	};
	return stef;
}


/**
 * Insert the method's description here.
 * Creation date: (8/8/01 3:46:23 PM)
 * @param speciesContext cbit.vcell.model.SpeciesContext
 * @return boolean
 */
public boolean isPDERequired(SpeciesContext speciesContext) {
	//
	// compartmental models never need diffusion
	//
	int dimension = getGeometryContext().getGeometry().getDimension();
	if (dimension == 0){
		return false;
	}
	
	StructureMapping structureMapping = getGeometryContext().getStructureMapping(speciesContext.getStructure());
	GeometryClass geometryClass = structureMapping.getGeometryClass();
	if (geometryClass instanceof SubVolume){
		if (dimension < 1){
			return false;
		}
	}else if (geometryClass instanceof SurfaceClass){
		if (dimension < 2){
			return false;
		}
	}else{
		throw new RuntimeException("structure "+speciesContext.getStructure().getName()+" not mapped, or unsupported GeometryClass "+geometryClass);
	}

	//
	// check speciesContext needs diffusion/advection
	//
	SpeciesContextSpec scs = getReactionContext().getSpeciesContextSpec(speciesContext);
	if (scs.isDiffusing() || scs.isAdvecting()){
		return true;
	}
	return false;
}

public boolean hasEventAssignment(SpeciesContext speciesContext) {
	//
	// spatial and stochastic models don't have events yet.
	//
	if (getGeometryContext().getGeometry().getDimension() != 0 || applicationType != Application.NETWORK_DETERMINISTIC) { 
		return false;
	}

	//
	// check if speciesContext is a target variable in the (bio)events in the simContext
	//
	BioEvent[] eventsList = getBioEvents();
	if (eventsList != null && eventsList.length > 0) {
		for (BioEvent event : eventsList) {
			ArrayList<EventAssignment> eventAssgnments = event.getEventAssignments();
			if(eventAssgnments != null){
				for (EventAssignment eventAssign : eventAssgnments) {
					if (eventAssign.getTarget() == speciesContext) {
						return true;
					}
				}
			}
		}
	}
	return false;
}

public BioEvent getEvent(String name) {
	if (fieldBioEvents != null && fieldBioEvents.length > 0) {
		for (BioEvent e : fieldBioEvents) {
			if (name.equals(e.getName())) {
				return e;
			}
		}
	}
	return null;
}

public String getFreeEventName(String preferredName) {
	String prefix = "event0";
	if(preferredName != null && preferredName.length() > 0){
		prefix = TokenMangler.fixTokenStrict(preferredName);
	}
	String eventName = prefix;
	while (true) {
		if (getEvent(eventName) == null) {
			return eventName;
		}
		eventName = TokenMangler.getNextEnumeratedToken(eventName);
	}
}

public DataContext getDataContext() {
	return dataContext;
}

/**
 * @return appropriate transformer, may be null
 */
public SimContextTransformer createNewTransformer(){
	switch (applicationType) {
	case RULE_BASED_STOCHASTIC:
		return new RulebasedTransformer();
	case NETWORK_DETERMINISTIC:
	case NETWORK_STOCHASTIC:
//		if(getBioModel().getModel().getRbmModelContainer().getMolecularTypeList().size() > 0) {
		if(getBioModel().getModel().getRbmModelContainer().getReactionRuleList().size() > 0) {
			// if rules are present we need to flatten a deterministic model
			boolean foundEnabledRule = false;
			for (ReactionRuleSpec rrs : getReactionContext().getReactionRuleSpecs()){
				if (!rrs.isExcluded()){
					foundEnabledRule = true;
					break;
				}
			}
			if(foundEnabledRule) {
				return new NetworkTransformer();
			}
		}
		if (getGeometry().getDimension() == 0) {
			if (!getGeometryContext().isAllSizeSpecifiedPositive()) {
				// old models may not have absolute sizes for structures, we clone the original simulation context
				// to one with corrected numbers and work on it while leaving the original unmodified
				return new LegacySimContextTransformer();
			}
		}
	}
	return null; 
}

public static class NetworkGenerationRequirements {
	public enum RequestType {
		AllowTruncatedNetwork,
		ComputeFullNetwork
	};
	public final static long NoTimeoutMS = 0L;
	public final static long StandardTimeoutMS = 60000L;

	public final RequestType requestType;
	public final Long timeoutDurationMS;
	
	/**
	 * 
	 * @param requestType
	 * @param timeoutDurationMS - null or 0 indicates no timeout.
	 */
	private NetworkGenerationRequirements(RequestType requestType, Long timeoutDurationMS){
		this.requestType = requestType;
		this.timeoutDurationMS = timeoutDurationMS;
	}

	public final static NetworkGenerationRequirements ComputeFullNoTimeout = new NetworkGenerationRequirements(RequestType.ComputeFullNetwork, NoTimeoutMS);
	public final static NetworkGenerationRequirements ComputeFullStandardTimeout = new NetworkGenerationRequirements(RequestType.ComputeFullNetwork, StandardTimeoutMS);
	public final static NetworkGenerationRequirements AllowTruncatedStandardTimeout = new NetworkGenerationRequirements(RequestType.AllowTruncatedNetwork, StandardTimeoutMS);

	public static NetworkGenerationRequirements getComputeFull(long timeoutDurationMS) {
		return new NetworkGenerationRequirements(RequestType.ComputeFullNetwork, timeoutDurationMS);
	}
};

public MathMapping createNewMathMapping() {
	MathMappingCallback callback = new MathMappingCallback() {
		@Override
		public void setProgressFraction(float fractionDone) {
			Thread.dumpStack();
			System.out.println("---> stdout mathMapping: progress = "+(fractionDone*100.0)+"% done");
		}
		
		@Override
		public void setMessage(String message) {
			Thread.dumpStack();
			System.out.println("---> stdout mathMapping: message = "+message);
		}
		
		@Override
		public boolean isInterrupted() {
			return false;
		}
	};
	return createNewMathMapping(callback, NetworkGenerationRequirements.ComputeFullStandardTimeout);
}

public MathMapping createNewMathMapping(MathMappingCallback callback, NetworkGenerationRequirements networkGenReq) {
	boolean bIgnoreMathDescription = true;
	DocumentValidUtil.checkIssuesForErrors(this, bIgnoreMathDescription);
	
	mostRecentlyCreatedMathMapping = null;
	switch (applicationType) {
	case NETWORK_STOCHASTIC:
		if (getGeometry().getDimension()==0){
			mostRecentlyCreatedMathMapping = new StochMathMapping(this, callback, networkGenReq);
		}else{
			mostRecentlyCreatedMathMapping = new ParticleMathMapping(this, callback, networkGenReq);
		}
		break;
	case RULE_BASED_STOCHASTIC:
		mostRecentlyCreatedMathMapping = new RulebasedMathMapping(this, callback, null);
		break;
	case NETWORK_DETERMINISTIC: 
		mostRecentlyCreatedMathMapping = new DiffEquMathMapping(this, callback, networkGenReq);
		break;
	}
	VCAssert.assertFalse(mostRecentlyCreatedMathMapping == null, "math mapping not generated" );
	
	return mostRecentlyCreatedMathMapping;
}

public boolean isSameTypeAs(SimulationContext simulationContext) {
	if (getGeometry().getDimension() != simulationContext.getGeometry().getDimension()) {
		return false;
	}
	return applicationType == simulationContext.applicationType;
}

public boolean isValidForFitting() {
	return getGeometry().getDimension() == 0 && applicationType == Application.NETWORK_DETERMINISTIC; 
}

private void initializeForSpatial() {
	SpeciesContextSpec[] speciesContextSpec = getReactionContext().getSpeciesContextSpecs();
	for(int i=0;i<speciesContextSpec.length;i++){
		speciesContextSpec[i].initializeForSpatial();
	}
}

public void createDefaultParameterEstimationTask()
{
	if (applicationType == Application.NETWORK_DETERMINISTIC && this.getGeometry().getDimension() == 0 && fieldAnalysisTasks == null)
	{
		try{
			ParameterEstimationTask peTask = new ParameterEstimationTask(this);
			peTask.setName(ParameterEstimationTask.defaultTaskName);
			addAnalysisTask(peTask);
		}catch(Exception e)
		{
			//if cannot create parameter estimation task, we don't want to block the vcell running.
			//it will leave an empty task, which users can actually use "new" button to create parameter estimation task later.
			e.printStackTrace(System.out);
		}
		
	}
}

public void fixFlags() {
	// takes care of incompatible flag combinations caused by copying while changing application types
	if(!(getGeometry().getDimension() != 0 && applicationType == Application.NETWORK_DETERMINISTIC)) {
		SpeciesContextSpec[] speciesContextSpec = getReactionContext().getSpeciesContextSpecs();
		for(int i=0;i<speciesContextSpec.length;i++){
			speciesContextSpec[i].setWellMixed(false);
		}
	}
}

public RateRule createRateRule(SymbolTableEntry varSTE) throws PropertyVetoException {
	String rateRuleName = getFreeRateRuleName();
	RateRule rateRule = new RateRule(rateRuleName, varSTE, new Expression(0.0), this);
	return addRateRule(rateRule);
}

public RateRule addRateRule(RateRule rateRule) throws PropertyVetoException {
	if (fieldRateRules == null){
		setRateRules(new RateRule[] { rateRule });
	}else{
		RateRule[] newRateRules = (RateRule[])BeanUtils.addElement(fieldRateRules, rateRule);
		setRateRules(newRateRules);
	}
	return rateRule;
}

public void removeRateRule(RateRule rateRule) throws PropertyVetoException {
	boolean bFound = false;
	for (int i = 0; fieldRateRules!=null && i < fieldRateRules.length; i++){
		if (fieldRateRules[i] == rateRule){
			bFound = true;
			break;
		}
	}
	if (!bFound){
		throw new RuntimeException("rate rule '" + rateRule.getName() + "' not found.");
	}
	RateRule[] newRateRules = (RateRule[])BeanUtils.removeElement(fieldRateRules, rateRule);
	setRateRules(newRateRules);
}

public RateRule[] getRateRules() {
	return fieldRateRules;
}


public RateRule getRateRule(String rateRulename) {
	if (fieldRateRules != null) {
		for (RateRule rr : fieldRateRules) {
			if (rr.getName().equals(rateRulename)) {
				return rr;
			}
		}
	}
	return null;
}

public void setRateRules(RateRule[] rateRules) throws java.beans.PropertyVetoException {
	RateRule[] oldValue = fieldRateRules;
	fireVetoableChange(PROPERTY_NAME_RATERULES, oldValue, rateRules);
	fieldRateRules = rateRules;
	firePropertyChange(PROPERTY_NAME_RATERULES, oldValue, rateRules);
}

public String getFreeRateRuleName() {	
	int count = 0;
	while (true) {
		String rateRuleName = "rateRule" + count;
		if (getRateRule(rateRuleName) == null) {
			return rateRuleName;
		}
		count ++;
	}
}

public RateRule getRateRule(SymbolTableEntry rateRuleVar) {
	if (fieldRateRules != null) {
		for (RateRule rr : fieldRateRules) {
			if (rr.getRateRuleVar() == rateRuleVar) {
				return rr;
			}
		}
	}
	return null;
}

public NetworkConstraints getNetworkConstraints() {
	if(networkConstraints == null) {
		networkConstraints = new NetworkConstraints();		// we make the default
	}
	return networkConstraints;
}
public void setNetworkConstraints(NetworkConstraints networkConstraints) {
	this.networkConstraints = networkConstraints;
}
public String getMd5hash() {
	return md5hash;
}
public void setMd5hash(String md5hash) {
	this.md5hash = md5hash;
}
public BNGOutputSpec getMostRecentlyCreatedOutputSpec() {
	return mostRecentlyCreatedOutputSpec;
}
public void setMostRecentlyCreatedOutputSpec(BNGOutputSpec mostRecentlyCreatedOutputSpec) {
	this.mostRecentlyCreatedOutputSpec = mostRecentlyCreatedOutputSpec;
	firePropertyChange("bngOutputChanged", "", "NA");
}
public boolean isInsufficientIterations() {
	return bInsufficientIterations;
}
public void setInsufficientIterations(boolean bInsufficientIterations) {
	this.bInsufficientIterations = bInsufficientIterations;
}
public boolean isInsufficientMaxMolecules() {
	return bInsufficientMaxMolecules;
}
public void setInsufficientMaxMolecules(boolean bInsufficientMaxMolecules) {
	this.bInsufficientMaxMolecules = bInsufficientMaxMolecules;
}

public MathMapping getMostRecentlyCreatedMathMapping(){
	return this.mostRecentlyCreatedMathMapping;
}

public void setTaskCallbackProcessor(TaskCallbackProcessor tcp) {
	this.tcp = tcp;
}
public final TaskCallbackProcessor getTaskCallbackProcessor() {
	return tcp;
}
public void appendToConsole(TaskCallbackMessage message) {
	tcp.appendToConsole(message);
}
public void playConsoleNotificationList() {
	tcp.playConsoleNotificationList();
}

@Override
public Issue gatherIssueForMathOverride(IssueContext issueContext, Simulation simulation, String overriddenConstantName) {
	issueContext = issueContext.newChildContext(ContextType.SimContext, this);
	ReservedSymbol reservedSymbol = getModel().getReservedSymbolByName(overriddenConstantName);
	if (reservedSymbol!=null && reservedSymbol.getRole() == ReservedSymbolRole.KMOLE){
		String msg = "overriding unit factor KMOLE is no longer supported, unit conversion has been completely redesigned";
		return new Issue(simulation,issueContext,Issue.IssueCategory.Simulation_Override_NotSupported,msg,Severity.ERROR);
	}
	if (reservedSymbol!=null && reservedSymbol.getRole() == ReservedSymbolRole.N_PMOLE){
		String msg = "overriding unit factor N_PMOLE is no longer supported, unit conversion has been completely redesigned";
		return new Issue(simulation,issueContext,Issue.IssueCategory.Simulation_Override_NotSupported,msg,Severity.ERROR);
	}
	return null;
}

// ----------------------------------------------------------------------------------------
@Override
public void updateBioNetGenOutput(BNGOutputSpec outputSpec) {
	setMostRecentlyCreatedOutputSpec(outputSpec);
}

@Override
public void setNewCallbackMessage(TaskCallbackMessage message) {
	appendToConsole(message);
}

@Override
public boolean isInterrupted() {
	// TODO Auto-generated method stub
	return false;
}

private SpatialObject[] spatialObjects = new SpatialObject[0];

public SpatialObject[] getSpatialObjects(){
	return Arrays.copyOf(spatialObjects,spatialObjects.length);
}

public void addSpatialObject(SpatialObject spatialObject) throws PropertyVetoException{
	setSpatialObjects(BeanUtils.addElement(this.spatialObjects, spatialObject));
}

public void removeSpatialObject(SpatialObject spatialObject) throws PropertyVetoException{
	setSpatialObjects(BeanUtils.removeElement(this.spatialObjects, spatialObject));
}

public void setSpatialObjects(SpatialObject[] spatialObjects) throws PropertyVetoException{
	SpatialObject[] oldValue = this.spatialObjects;
	fireVetoableChange(PROPERTY_NAME_SPATIALOBJECTS, oldValue, spatialObjects);
	this.spatialObjects = spatialObjects;
	firePropertyChange(PROPERTY_NAME_SPATIALOBJECTS, oldValue, spatialObjects);
}

public SpatialObject getSpatialObject(String name) {
	for (SpatialObject so : spatialObjects){
		if (so.getName().equals(name)){
			return so;
		}
	}
	return null;
}

public PointObject createPointObject() {
	String pointName = "pobj_0";
	while (getSpatialObject(pointName)!=null){
		pointName = TokenMangler.getNextEnumeratedToken(pointName);
	}
	PointObject pointObject = new PointObject(pointName,this);
	try {
		addSpatialObject(pointObject);
//		PointLocation pointLocation = createPointLocation(pointObject);
//		try {
//			pointLocation.getParameter(SpatialProcessParameterType.PointPositionX).setExpression(new Expression(1.0));
//			pointLocation.getParameter(SpatialProcessParameterType.PointPositionY).setExpression(new Expression(2.0));
//			pointLocation.getParameter(SpatialProcessParameterType.PointPositionZ).setExpression(new Expression(3.0));
//		} catch (ExpressionBindingException e) {
//			e.printStackTrace();
//		}
	}catch (PropertyVetoException e){
		throw new RuntimeException(e.getMessage(),e);
	}
	return pointObject;
}

private SpatialProcess[] spatialProcesses = new SpatialProcess[0];
public static String rateWarning = "In converting networks to rules, reactants with symmetric sites should have different "
+ "microscopic rate constants in reaction rules.<br>If this is not properly treated, the resultant rule-based model will "
+ "show different simulation results than its network precursor.";
public static String rateWarning2 = "Rates for rules and reactions have different physical meaning and are not converted automatically.";
public static String rateWarning3 = "Warning: rates for rules and reactions have different physical meaning (first principle "
+ "mass-action vs observed) and are not converted, which may lead to potentially different simulation results";

public SpatialProcess[] getSpatialProcesses(){
	return Arrays.copyOf(spatialProcesses,spatialProcesses.length);
}

public void addSpatialProcess(SpatialProcess spatialProcess) throws PropertyVetoException{
	setSpatialProcesses(BeanUtils.addElement(this.spatialProcesses, spatialProcess));
}

public void removeSpatialProcess(SpatialProcess spatialProcess) throws PropertyVetoException{
	setSpatialProcesses(BeanUtils.removeElement(this.spatialProcesses, spatialProcess));
}

public void setSpatialProcesses(SpatialProcess[] spatialProcesses) throws PropertyVetoException{
	SpatialProcess[] oldValue = this.spatialProcesses;
	fireVetoableChange(PROPERTY_NAME_SPATIALPROCESSES, oldValue, spatialProcesses);
	this.spatialProcesses = spatialProcesses;
	firePropertyChange(PROPERTY_NAME_SPATIALPROCESSES, oldValue, spatialProcesses);
}

public SpatialProcess getSpatialProcess(String name) {
	for (SpatialProcess so : spatialProcesses){
		if (so.getName().equals(name)){
			return so;
		}
	}
	return null;
}

public PointKinematics createPointKinematics(PointObject pointObject) {
	String pointProcName = "pproc_0";
	while (getSpatialProcess(pointProcName)!=null){
		pointProcName = TokenMangler.getNextEnumeratedToken(pointProcName);
	}
	PointKinematics pointKinematics = new PointKinematics(pointProcName,this);
	pointKinematics.setPointObject(pointObject);
	try {
		addSpatialProcess(pointKinematics);
	}catch (PropertyVetoException e){
		throw new RuntimeException(e.getMessage(),e);
	}
	return pointKinematics;
}

public PointLocation createPointLocation(PointObject pointObject) {
	String pointProcName = "pproc_0";
	while (getSpatialProcess(pointProcName)!=null){
		pointProcName = TokenMangler.getNextEnumeratedToken(pointProcName);
	}
	PointLocation pointLocation = new PointLocation(pointProcName,this);
	pointLocation.setPointObject(pointObject);
	try {
		addSpatialProcess(pointLocation);
	}catch (PropertyVetoException e){
		throw new RuntimeException(e.getMessage(),e);
	}
	return pointLocation;
}

public SurfaceKinematics createSurfaceKinematics(SurfaceRegionObject surfaceRegionObject) {
	String surfaceKinematicsName = "sproc_0";
	while (getSpatialProcess(surfaceKinematicsName)!=null){
		surfaceKinematicsName = TokenMangler.getNextEnumeratedToken(surfaceKinematicsName);
	}
	SurfaceKinematics surfaceKinematics = new SurfaceKinematics(surfaceKinematicsName,this);
	surfaceKinematics.setSurfaceRegionObject(surfaceRegionObject);
	try {
		addSpatialProcess(surfaceKinematics);
	}catch (PropertyVetoException e){
		throw new RuntimeException(e.getMessage(),e);
	}
	return surfaceKinematics;
}

public VolumeKinematics createVolumeKinematics(VolumeRegionObject volumeRegionObject) {
	String volumeKinematicsName = "vproc_0";
	while (getSpatialProcess(volumeKinematicsName)!=null){
		volumeKinematicsName = TokenMangler.getNextEnumeratedToken(volumeKinematicsName);
	}
	VolumeKinematics volumeKinematics = new VolumeKinematics(volumeKinematicsName,this);
	volumeKinematics.setVolumeRegionObject(volumeRegionObject);
	try {
		addSpatialProcess(volumeKinematics);
	}catch (PropertyVetoException e){
		throw new RuntimeException(e.getMessage(),e);
	}
	return volumeKinematics;
}

public SpatialObject[] getSpatialObjects(Structure structure) {
	StructureMapping structureMapping = getGeometryContext().getStructureMapping(structure);
	if (structureMapping.getGeometryClass()!=null){
		return getSpatialObjects(structureMapping.getGeometryClass());
	}
	return new SpatialObject[0];
}

public SpatialObject[] getSpatialObjects(GeometryClass geometryClass) {
	ArrayList<SpatialObject> spatialObjectList = new ArrayList<SpatialObject>();
	if (geometryClass instanceof SubVolume){
		for (SpatialObject spatialObject : this.spatialObjects){
			if (spatialObject instanceof VolumeRegionObject){
				if (((VolumeRegionObject)spatialObject).getSubVolume() == geometryClass){
					spatialObjectList.add(spatialObject);
				}
			}
		}
	}
	return spatialObjectList.toArray(new SpatialObject[0]);
}

public static 
	SimulationContext copySimulationContext(SimulationContext srcSimContext, String newSimulationContextName, boolean bSpatial, Application simContextType) 
				throws java.beans.PropertyVetoException, ExpressionException, MappingException, GeometryException, ImageException {
		Geometry newClonedGeometry = null;
		if(bSpatial) {
			newClonedGeometry = new Geometry(srcSimContext.getGeometry());			
		}else {
			newClonedGeometry = new Geometry(srcSimContext.getGeometry().getName(),0);
		}
		newClonedGeometry.precomputeAll(new GeometryThumbnailImageFactoryAWT());
		//if stoch copy to ode, we need to check is stoch is using particles. If yes, should convert particles to concentraton.
		//the other 3 cases are fine. ode->ode, ode->stoch, stoch-> stoch 
		SimulationContext destSimContext = new SimulationContext(srcSimContext,newClonedGeometry, simContextType);
		if(srcSimContext.getApplicationType() == Application.NETWORK_STOCHASTIC && !srcSimContext.isUsingConcentration() && simContextType == Application.NETWORK_DETERMINISTIC)  
		{
			try {
				destSimContext.convertSpeciesIniCondition(true);
			} catch (MappingException e) {
				e.printStackTrace();
				throw new java.beans.PropertyVetoException(e.getMessage(), null);
			}
		}
		if (srcSimContext.getGeometry().getDimension() > 0 && !bSpatial) { // copy the size over
			destSimContext.setGeometry(new Geometry("nonspatial", 0));
			StructureMapping srcStructureMappings[] = srcSimContext.getGeometryContext().getStructureMappings();
			StructureMapping destStructureMappings[] = destSimContext.getGeometryContext().getStructureMappings();
			for (StructureMapping destStructureMapping : destStructureMappings) {
				for (StructureMapping srcStructureMapping : srcStructureMappings) {
					if (destStructureMapping.getStructure() == srcStructureMapping.getStructure()) {
						if (srcStructureMapping.getUnitSizeParameter() != null) {
							Expression sizeRatio = srcStructureMapping.getUnitSizeParameter().getExpression();
							GeometryClass srcGeometryClass = srcStructureMapping.getGeometryClass();
							GeometricRegion[] srcGeometricRegions = srcSimContext.getGeometry().getGeometrySurfaceDescription().getGeometricRegions(srcGeometryClass);
							if (srcGeometricRegions != null) {
								double size = 0;
								for (GeometricRegion srcGeometricRegion : srcGeometricRegions) {
									size += srcGeometricRegion.getSize();
								}
								destStructureMapping.getSizeParameter().setExpression(Expression.mult(sizeRatio, new Expression(size)));
							}
						}
						break;
					}
				}
			}
			//If changing spatial to non-spatial
			//set diffusion to 0, velocity and boundary to null
//			srcSimContext.getReactionContext().getspe
			Parameter[] allParameters = destSimContext.getAllParameters();
			if(allParameters != null && allParameters.length > 0){
				for (int i = 0; i < allParameters.length; i++) {
					if(allParameters[i] instanceof SpeciesContextSpecParameter){
						SpeciesContextSpecParameter speciesContextSpecParameter = (SpeciesContextSpecParameter)allParameters[i];
						int role = speciesContextSpecParameter.getRole();
						if(role == SpeciesContextSpec.ROLE_DiffusionRate){
							speciesContextSpecParameter.setExpression(new Expression(0));
						}else if (role == SpeciesContextSpec.ROLE_BoundaryValueXm
								|| role == SpeciesContextSpec.ROLE_BoundaryValueXp
								|| role == SpeciesContextSpec.ROLE_BoundaryValueYm
								|| role == SpeciesContextSpec.ROLE_BoundaryValueYp
								|| role == SpeciesContextSpec.ROLE_BoundaryValueZm
								|| role == SpeciesContextSpec.ROLE_BoundaryValueZp) {
							speciesContextSpecParameter.setExpression(null);
							
						} else if (role == SpeciesContextSpec.ROLE_VelocityX
								|| role == SpeciesContextSpec.ROLE_VelocityY
								|| role == SpeciesContextSpec.ROLE_VelocityZ) {
							speciesContextSpecParameter.setExpression(null);
						}
					}
				}
			}
			
		}
		destSimContext.fixFlags();
		destSimContext.setName(newSimulationContextName);	
		return destSimContext;
	}



}

