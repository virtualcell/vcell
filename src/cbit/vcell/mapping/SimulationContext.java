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

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Extent;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.PropertyChangeListenerProxyVCell;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;

import cbit.image.VCImage;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.data.DataContext;
import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.field.FieldUtilities;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryOwner;
import cbit.vcell.mapping.BioEvent.EventAssignment;
import cbit.vcell.mapping.MicroscopeMeasurement.ProjectionZKernel;
import cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ExpressionContainer;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.model.Model.ReservedSymbolRole;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
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
public class SimulationContext implements SimulationOwner, Versionable, Matchable, 
	ScopedSymbolTable, PropertyChangeListener, VetoableChangeListener, Serializable {

	public static final String PROPERTY_NAME_DESCRIPTION = "description";
	public static final String PROPERTY_NAME_ANALYSIS_TASKS = "analysisTasks";
	public static final String PROPERTY_NAME_BIOEVENTS = "bioevents";
	public static final String PROPERTY_NAME_USE_CONCENTRATION = "UseConcentration";
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
										((fieldBioEvents!=null)?fieldBioEvents.length:0)];
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
			return nameScopes;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(SimulationContext.this.getName());
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
			return ((nameScope instanceof MathMapping.MathMappingNameScope) && nameScope.isPeer(this));
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
		
		protected SimulationContextParameter(String argName, Expression expression, int argRole, VCUnitDefinition argUnitDefinition) {
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
		public void setExpression(Expression expression) throws java.beans.PropertyVetoException {
			Expression oldValue = fieldParameterExpression;
			super.fireVetoableChange("expression", oldValue, expression);
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}
		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}

	}
	public static final int ROLE_UserDefined	= 0;
	public static final int NUM_ROLES		= 1;
	private Version version = null;
	private GeometryContext geoContext = null;
	private ReactionContext reactionContext = null;
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
	private boolean bStoch;
	private boolean bConcentration = true;
	private boolean bRandomizeInitCondition = false;
	private DataContext dataContext = new DataContext(getNameScope());
	private final MicroscopeMeasurement microscopeMeasurement = new MicroscopeMeasurement(SimulationContext.FLUOR_DATA_NAME,new ProjectionZKernel(), this);
	
	// rate rules
	private RateRule[] fieldRateRules = null;
	public static final String FLUOR_DATA_NAME = "fluor";


	public MicroscopeMeasurement getMicroscopeMeasurement() {
		return microscopeMeasurement;
	}

/**
 * Construct a new SimulationContext from an old SimulationContext.
 * Input paras: SimulationContext (the old one), boolean (is stochastic application or not) 
 */
public SimulationContext(SimulationContext simulationContext,Geometry newClonedGeometry, boolean arg_isStoch) throws PropertyVetoException {
	
	if(arg_isStoch)
	{
		String msg = simulationContext.getModel().isValidForStochApp();
		if(!msg.equals(""))
		{
			throw new RuntimeException("Error constructing a new simulation context:\n" + msg); //no need to show popup here, the exception passes to upper level.
		}
		this.bConcentration = simulationContext.bConcentration;
	} else {
		this.bConcentration = true; //deterministic method use concentration only.
	}
	this.geoContext = new GeometryContext(simulationContext.getGeometryContext(),this, newClonedGeometry);
	geoContext.addPropertyChangeListener(this);
	this.reactionContext = new ReactionContext(simulationContext.getReactionContext(),this);
	this.version = null;
	this.characteristicSize = simulationContext.getCharacteristicSize();
	this.fieldName = "copied_from_"+simulationContext.getName();
	this.fieldDescription = "(copied from "+simulationContext.getName()+") "+simulationContext.getDescription();
	this.bioModel = simulationContext.getBioModel();
	this.setIsStoch(arg_isStoch);
	//
	// copy electrical stimuli and ground
	//
	if (simulationContext.getGroundElectrode()!=null){
		this.fieldGroundElectrode = new Electrode(simulationContext.getGroundElectrode());
	}
	this.fieldElectricalStimuli = (ElectricalStimulus[])simulationContext.getElectricalStimuli().clone();
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
	
	if (!bStoch) {
		// copy events
		BioEvent[] bioEvents = simulationContext.getBioEvents();
		if (bioEvents != null) {
			fieldBioEvents = new BioEvent[bioEvents.length];
			for (int i = 0; i < bioEvents.length; i++) {
				fieldBioEvents[i] = new BioEvent(bioEvents[i], this);
			}
		}
		
		// copy rate rules
		RateRule[] rateRules = simulationContext.getRateRules();
		if (rateRules != null) {
			fieldRateRules = new RateRule[rateRules.length];
			for (int i = 0; i < rateRules.length; i++) {
				fieldRateRules[i] = new RateRule(rateRules[i], this);
			}
		}
	}
	if(!simulationContext.isStoch() && bStoch && geoContext.getGeometry().getDimension()>0) {
		initializeForSpatial();
	}
	
	if(simulationContext.fieldAnalysisTasks != null)
	{
		try {
			AnalysisTask[] analysisTasks = new AnalysisTask[simulationContext.fieldAnalysisTasks.length];
			for(int i=0; i<simulationContext.fieldAnalysisTasks.length; i++)
			{
				analysisTasks[i] = new ParameterEstimationTask(this, (ParameterEstimationTask)simulationContext.fieldAnalysisTasks[i]);
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
 * Please see new constructor which is in effect from Sept 28, 2006.
 * Creation date: (9/29/2006 9:53:45 AM)
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 * @exception java.beans.PropertyVetoException The exception description.
 */
public SimulationContext(Model argModel, Geometry argGeometry) throws java.beans.PropertyVetoException
{
	this(argModel,argGeometry,false);
}


/**
 * SimulationContext constructor.
 * New constructor is in effect from sept 28,2006.
 * Creation date: (9/29/2006 9:44:57 AM)
 * @param model cbit.vcell.model.Model
 * @param geometry cbit.vcell.geometry.Geometry
 * @param argMathDesc cbit.vcell.math.MathDescription
 * @param argVersion cbit.sql.Version
 * @exception java.beans.PropertyVetoException The exception description.
 */
public SimulationContext(Model argModel, Geometry argGeometry, MathDescription argMathDesc, Version argVersion) throws java.beans.PropertyVetoException
{
	this(argModel,argGeometry,argMathDesc,argVersion,false);
}


/**
 * SimulationContext constructor.
 * This constructor differs with the previous one with one more boolean input parameter, which specifies whether
 * the new application is a stochastic application or not.
 */
public SimulationContext(Model model, Geometry geometry, MathDescription argMathDesc, Version argVersion, boolean bStoch) throws PropertyVetoException {

	addVetoableChangeListener(this);
	setIsStoch(bStoch);
	geoContext = new GeometryContext(model,geometry,this);
	geoContext.addPropertyChangeListener(this);
	refreshCharacteristicSize();
	
	this.reactionContext = new ReactionContext(model,this);
	this.mathDesc = argMathDesc;
	this.version = argVersion;
	geometry.getGeometrySpec().addPropertyChangeListener(this);
	if (argVersion!=null){
		this.fieldName = argVersion.getName();
		this.fieldDescription = argVersion.getAnnot();
	} else {
		this.fieldName = "Application_with_"+geometry.getName();
	}
}


/**
 * SimulationContext constructor.
 * This constructor differs with the previous one with one more boolean input parameter, which specifies whether
 * the new application is a stochastic application or not.
 */
public SimulationContext(Model model, Geometry geometry, boolean bStoch) throws PropertyVetoException {

	addVetoableChangeListener(this);
	setIsStoch(bStoch);
	geoContext = new GeometryContext(model,geometry,this);
	geoContext.addPropertyChangeListener(this);
	refreshCharacteristicSize();
	
	this.reactionContext = new ReactionContext(model,this);
	this.version = null;
	geometry.getGeometrySpec().addPropertyChangeListener(this);
	this.fieldName = "Application_with_"+geometry.getName();
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
	String eventName = getFreeEventName();
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
public Simulation addNewSimulation(String simNamePrefix) throws java.beans.PropertyVetoException {
	refreshMathDescription();
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


public void refreshMathDescription() {
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
			setMathDescription(createNewMathMapping().getMathDescription());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
				"Application "+getName()+" has no generated Math\n"+
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
	if(simContext.bStoch != bStoch)
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

	if (!outputFunctionContext.compareEqual(simContext.outputFunctionContext)){
		return false;
	}
	
	if(!Compare.isEqualOrNull(getDataContext(), simContext.getDataContext())){
		return false;
	}	
	
	if(!Compare.isEqualOrNull(getMicroscopeMeasurement(), simContext.getMicroscopeMeasurement())){
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
	Vector<Simulation> list = new Vector<Simulation>();
	for (int i = 0; i < allSimulations.length; i++){
		if (allSimulations[i].getMathDescription()==getMathDescription()){
			list.add(allSimulations[i]);
		}
	}
	Simulation localSimulations[] = (Simulation[])BeanUtils.getArray(list,Simulation.class);
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
 * Creation date: (3/18/2004 1:54:51 PM)
 * @param newVersion cbit.sql.Version
 */
public void forceNewVersionAnnotation(Version newVersion) throws PropertyVetoException {
	if (getVersion().getVersionKey().equals(newVersion.getVersionKey())) {
		setVersion(newVersion);
	} else {
		throw new RuntimeException("SimulationContext.forceNewVersionAnnotation failed : version keys not equal");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/1/2005 9:30:09 AM)
 * @param issueVector java.util.Vector
 */
public void gatherIssues(IssueContext issueContext, List<Issue> issueVector) {
	issueContext = issueContext.newChildContext(ContextType.SimContext, this);
	getReactionContext().gatherIssues(issueContext, issueVector);
	getGeometryContext().gatherIssues(issueContext, issueVector);
	if (fieldAnalysisTasks != null) {
		for (AnalysisTask analysisTask : fieldAnalysisTasks) {
			analysisTask.gatherIssues(issueContext, issueVector);
		}
	}
	getOutputFunctionContext().gatherIssues(issueContext, issueVector);
	getMicroscopeMeasurement().gatherIssues(issueContext, issueVector);
	if (getMathDescription()!=null){
		getMathDescription().gatherIssues(issueContext, issueVector);
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
	return fieldDescription;
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
	return geoContext.getGeometry();
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
 * Insert the method's description here.
 * Creation date: (9/22/2006 4:07:16 PM)
 * @return boolean
 */
public boolean isStoch() {
	return bStoch;
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/00 11:28:20 PM)
 * @param event java.beans.PropertyChangeEvent
 */
public void propertyChange(java.beans.PropertyChangeEvent event) {
	if (event.getSource() == getGeometry().getGeometrySpec() && event.getPropertyName().equals("extent")){
		try {
			characteristicSize = null;
			refreshCharacteristicSize();
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
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

	if (fieldElectricalStimuli!=null){
		for (int i = 0; i < fieldElectricalStimuli.length; i++){
			fieldElectricalStimuli[i].refreshDependencies();
		}
	}
	if (fieldAnalysisTasks!=null){
		for (int i = 0; i < fieldAnalysisTasks.length; i++){
			fieldAnalysisTasks[i].refreshDependencies();
		}
	}
	
	if (fieldBioEvents != null) {
		for (int i = 0; i < fieldBioEvents.length; i++) {
			fieldBioEvents[i].refreshDependencies();
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
	String oldValue = fieldDescription;
	fireVetoableChange(PROPERTY_NAME_DESCRIPTION, oldValue, description);
	fieldDescription = description;
	firePropertyChange(PROPERTY_NAME_DESCRIPTION, oldValue, description);
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


/**
 * Insert the method's description here.
 * Creation date: (9/22/2006 4:07:16 PM)
 * @param newIsStoch boolean
 */
private void setIsStoch(boolean newIsStoch) {
	bStoch = newIsStoch;
}


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
	fireVetoableChange("simulationContextParameters", oldValue, simulationContextParameters);
	fieldSimulationContextParameters = simulationContextParameters;
	firePropertyChange("simulationContextParameters", oldValue, simulationContextParameters);
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
 * Insert the method's description here.
 * Creation date: (11/14/00 3:49:12 PM)
 * @param version cbit.sql.Version
 */
private void setVersion(Version newVersion) throws PropertyVetoException {
	this.version = newVersion;
	if (newVersion != null){
		setName(newVersion.getName());
		setDescription(newVersion.getAnnot());
	}
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
		if (!isStoch() && getGeometryContext().isAllVolFracAndSurfVolSpecified() && getGeometryContext().isAllSizeSpecifiedNull()){
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

public void setUsingConcentration(boolean bUseConcentration) /*throws MappingException, PropertyVetoException*/ {
	if(isStoch()) //do it only when it is stochastic application
	{
		boolean oldValue = bConcentration;
		bConcentration = bUseConcentration;
		firePropertyChange(PROPERTY_NAME_USE_CONCENTRATION, oldValue, bConcentration);
	}
}

public boolean isRandomizeInitCondition() {
	return bRandomizeInitCondition;
}

public void setRandomizeInitCondition(boolean bRandomize) {
	if(isStoch()) //do it only when it is stochastic application
	{
		boolean oldValue = bRandomizeInitCondition;
		bRandomizeInitCondition = bRandomize;
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

public String getMathType()
{
	return bStoch ? BioModelChildSummary.TYPE_STOCH_STR : BioModelChildSummary.TYPE_DETER_STR;
}

public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
	getModel().getEntries(entryMap);
	for (SymbolTableEntry ste : fieldSimulationContextParameters) {
		entryMap.put(ste.getName(), ste);
	}
	for (SymbolTableEntry ste : dataContext.getDataSymbols()){
		entryMap.put(ste.getName(), ste);
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
	if (getGeometryContext().getGeometry().getDimension() == 0){
		return false;
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
	if (getGeometryContext().getGeometry().getDimension() != 0 || isStoch()){
		return false;
	}

	//
	// check if speciesContext is a target variable in the (bio)events in the simContext
	//
	BioEvent[] eventsList = getBioEvents();
	if (eventsList != null && eventsList.length > 0) {
		for (BioEvent event : eventsList) {
			ArrayList<EventAssignment> eventAssgnments = event.getEventAssignments();
			for (EventAssignment eventAssign : eventAssgnments) {
				if (eventAssign.getTarget() == speciesContext) {
					return true;
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

public String getFreeEventName() {	
	int count = 0;
	while (true) {
		String eventName = "event" + count;
		if (getEvent(eventName) == null) {
			return eventName;
		}
		count ++;
	}
}

public DataContext getDataContext() {
	return dataContext;
}

public MathMapping createNewMathMapping() {
	MathMapping mathMapping = null;
	if (isStoch())
	{
		if (getGeometry().getDimension()==0){
			mathMapping = new StochMathMapping(this);
		}else{
			mathMapping = new ParticleMathMapping(this);
		}
	}
	else
	{
		mathMapping = new MathMapping(this);
	}
	return mathMapping;
}

public boolean isSameTypeAs(SimulationContext simulationContext) {
	if (getGeometry().getDimension() != simulationContext.getGeometry().getDimension()) {
		return false;
	}
		
	return (isStoch() && simulationContext.isStoch()) || (!isStoch() && !simulationContext.isStoch());
}

public boolean isValidForFitting() {
	return getGeometry().getDimension() == 0 && !isStoch();
}

private void initializeForSpatial() {
	SpeciesContextSpec[] speciesContextSpec = getReactionContext().getSpeciesContextSpecs();
	for(int i=0;i<speciesContextSpec.length;i++){
		speciesContextSpec[i].initializeForSpatial();
	}
}

public void createDefaultParameterEstimationTask()
{
	if(!this.isStoch() && this.getGeometry().getDimension() == 0 && fieldAnalysisTasks == null)
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

public void fixFlags() {	// called when not spatial deterministic
	// takes care of incompatible flag combinations caused by copying while changing application types
	SpeciesContextSpec[] speciesContextSpec = getReactionContext().getSpeciesContextSpecs();
	for(int i=0;i<speciesContextSpec.length;i++){
		speciesContextSpec[i].setWellMixed(false);
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

@Override
public Issue gatherIssueForMathOverride(IssueContext issueContext, Simulation simulation, String overriddenConstantName) {
	issueContext = issueContext.newChildContext(ContextType.SimContext, this);
	ReservedSymbol reservedSymbol = getModel().getReservedSymbolByName(overriddenConstantName);
	if (reservedSymbol!=null && reservedSymbol.getRole() == ReservedSymbolRole.KMOLE){
		String msg = "overriding unit factor KMOLE is no longer supported, unit conversion has been completely redesigned";
		return new Issue(simulation,issueContext,Issue.IssueCategory.Simulation_Override_NotSupported,msg,Issue.SEVERITY_ERROR);
	}
	if (reservedSymbol!=null && reservedSymbol.getRole() == ReservedSymbolRole.N_PMOLE){
		String msg = "overriding unit factor N_PMOLE is no longer supported, unit conversion has been completely redesigned";
		return new Issue(simulation,issueContext,Issue.IssueCategory.Simulation_Override_NotSupported,msg,Issue.SEVERITY_ERROR);
	}
	return null;
}

}

