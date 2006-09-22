package cbit.vcell.mapping;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.parser.Expression;
import cbit.vcell.model.Structure;
import cbit.vcell.model.Feature;
import cbit.vcell.model.BioNameScope;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.*;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.model.Model;
import cbit.util.*;
import cbit.vcell.math.MathDescription;
import java.util.*;

import org.vcell.modelapp.analysis.IAnalysisTask;

import cbit.vcell.parser.NameScope;
import cbit.vcell.model.Parameter;
/**
 * This type was created in VisualAge.
 */
public class SimulationContext implements cbit.util.Versionable, Matchable, cbit.vcell.simulation.SimulationOwner, cbit.vcell.parser.ScopedSymbolTable, PropertyChangeListener, VetoableChangeListener, java.io.Serializable {

	public class SimulationContextNameScope extends BioNameScope {
		private transient cbit.vcell.parser.NameScope nameScopes[] = null;
		public SimulationContextNameScope(){
			super();
		}
		public cbit.vcell.parser.NameScope[] getChildren() {
			//
			// return list of structureMappings and speciesContextSpecs
			//
			int index = 0;
			nameScopes = new NameScope[geoContext.getStructureMappings().length + 
										reactionContext.getSpeciesContextSpecs().length + 
										reactionContext.getReactionSpecs().length + 
										fieldElectricalStimuli.length];
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
			return nameScopes;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(SimulationContext.this.getName());
		}
		public cbit.vcell.parser.NameScope getParent() {
			//System.out.println("SimulationContextNameScope.getParent() returning null ... no parent");
			return null;
		}
		public cbit.vcell.parser.ScopedSymbolTable getScopedSymbolTable() {
			return SimulationContext.this;
		}
		public boolean isPeer(cbit.vcell.parser.NameScope nameScope){
			return ((nameScope instanceof cbit.vcell.mapping.MathMapping.MathMappingNameScope) && nameScope.isPeer(this));
		}
	}

	public class SimulationContextParameter extends Parameter {
		
		private String fieldParameterName = null;
		private cbit.vcell.parser.Expression fieldParameterExpression = null;
		private int fieldParameterRole = -1;
		private cbit.vcell.units.VCUnitDefinition fieldUnitDefinition = null;
		
		protected SimulationContextParameter(String argName, cbit.vcell.parser.Expression expression, int argRole, cbit.vcell.units.VCUnitDefinition argUnitDefinition) {
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

		public double getConstantValue() throws cbit.vcell.parser.ExpressionException {
			return this.fieldParameterExpression.evaluateConstant();
		}      


		public cbit.vcell.parser.Expression getExpression() {
			return this.fieldParameterExpression;
		}


		public int getIndex() {
			return -1;
		}


		public String getName(){ 
			return this.fieldParameterName; 
		}   


		public cbit.vcell.parser.NameScope getNameScope() {
			return SimulationContext.this.nameScope;
		}

		public int getRole() {
			return this.fieldParameterRole;
		}

		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}

		public void setUnitDefinition(cbit.vcell.units.VCUnitDefinition unitDefinition) throws java.beans.PropertyVetoException {
			cbit.vcell.units.VCUnitDefinition oldValue = fieldUnitDefinition;
			super.fireVetoableChange("unitDefinition", oldValue, unitDefinition);
			fieldUnitDefinition = unitDefinition;
			super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
		}
		public void setExpression(cbit.vcell.parser.Expression expression) throws java.beans.PropertyVetoException {
			cbit.vcell.parser.Expression oldValue = fieldParameterExpression;
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
	private MathDescription mathDesc = null;
	private Double characteristicSize = null;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient VetoableChangeSupport vetoPropertyChange;
	private java.lang.String fieldName = new String("NoName");
	private java.lang.String fieldDescription = new String();
	private double fieldTemperatureKelvin = 300;
	private cbit.vcell.mapping.ElectricalStimulus[] fieldElectricalStimuli = new ElectricalStimulus[0];
	private Electrode fieldGroundElectrode = null;
	private SimulationContextNameScope nameScope = new SimulationContextNameScope();
	private transient SimulationContextOwner simContextOwner = null;
	private SimulationContext.SimulationContextParameter[] fieldSimulationContextParameters = new SimulationContextParameter[0];
	private IAnalysisTask[] fieldAnalysisTasks = null;

/**
 * SimulationContext constructor comment.
 */
public SimulationContext(SimulationContext simulationContext) throws PropertyVetoException {
	this.geoContext = new GeometryContext(simulationContext.getGeometryContext(),this);
	this.reactionContext = new ReactionContext(simulationContext.getReactionContext(),this);
	this.version = null;
	this.characteristicSize = simulationContext.getCharacteristicSize();
	this.fieldName = "copied_from_"+simulationContext.getName();
	this.fieldDescription = "(copied from "+simulationContext.getName()+") "+simulationContext.getDescription();
	this.simContextOwner = simulationContext.getSimulationContextOwner();
	//
	// copy electrical stimuli and ground
	//
	if (simulationContext.getGroundElectrode()!=null){
		this.fieldGroundElectrode = new Electrode(simulationContext.getGroundElectrode());
	}
	this.fieldElectricalStimuli = (ElectricalStimulus[])simulationContext.getElectricalStimuli().clone();
	for (int i = 0; i < fieldElectricalStimuli.length; i++){
		if (fieldElectricalStimuli[i] instanceof CurrentClampStimulus){
			CurrentClampStimulus otherStimulus = (CurrentClampStimulus)fieldElectricalStimuli[i];
			fieldElectricalStimuli[i] = new CurrentClampStimulus(
												new Electrode(otherStimulus.getElectrode()),
												otherStimulus.getName(),
												new Expression(otherStimulus.getCurrentParameter().getExpression()),this);
		}else if (fieldElectricalStimuli[i] instanceof VoltageClampStimulus){
			VoltageClampStimulus otherStimulus = (VoltageClampStimulus)fieldElectricalStimuli[i];
			fieldElectricalStimuli[i] = new VoltageClampStimulus(
												new Electrode(otherStimulus.getElectrode()),
												otherStimulus.getName(),
												new Expression(otherStimulus.getVoltageParameter().getExpression()),this);
		}else{
			throw new RuntimeException("");
		}
	}
	refreshDependencies();
}


/**
 * SimulationContext constructor comment.
 */
public SimulationContext(cbit.vcell.model.Model model, cbit.vcell.geometry.Geometry geometry) throws PropertyVetoException {

	addVetoableChangeListener(this);
	
	setGeometryContext(new GeometryContext(model,geometry,this));
	this.reactionContext = new ReactionContext(model,this);
	this.version = null;
	getGeometryContext().addPropertyChangeListener(this);
	geometry.getGeometrySpec().addPropertyChangeListener(this);
	this.fieldName = "Application_with_"+geometry.getName();
}


/**
 * SimulationContext constructor comment.
 */
public SimulationContext(Model model, Geometry geometry, MathDescription argMathDesc, Version argVersion) throws PropertyVetoException {

	addVetoableChangeListener(this);
	
	setGeometryContext(new GeometryContext(model,geometry,this));
	this.reactionContext = new ReactionContext(model,this);
	this.mathDesc = argMathDesc;
	this.version = argVersion;
	getGeometryContext().addPropertyChangeListener(this);
	geometry.getGeometrySpec().addPropertyChangeListener(this);
	if (argVersion!=null){
		this.fieldName = argVersion.getName();
		this.fieldDescription = argVersion.getAnnot();
	} else {
		this.fieldName = "Application_with_"+geometry.getName();
	}
}


/**
 * Sets the analysisTasks index property (cbit.vcell.modelopt.AnalysisTask[]) value.
 * @param index The index value into the property array.
 * @param analysisTasks The new value for the property.
 * @see #getAnalysisTasks
 */
public void addAnalysisTask(org.vcell.modelapp.analysis.IAnalysisTask analysisTask) throws PropertyVetoException {
	if (fieldAnalysisTasks==null){
		setAnalysisTasks(new org.vcell.modelapp.analysis.IAnalysisTask[] { analysisTask });
	}else{
		IAnalysisTask[] newAnalysisTasks = (org.vcell.modelapp.analysis.IAnalysisTask[])BeanUtils.addElement(fieldAnalysisTasks,analysisTask);
		setAnalysisTasks(newAnalysisTasks);
	}
}


/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulations
 */
public Simulation addNewSimulation() throws java.beans.PropertyVetoException {
	if (getMathDescription()==null){
//		throw new RuntimeException("Application "+getName()+" has no generated Math, cannot add simulation");
		try {
			setMathDescription((new MathMapping(this)).getMathDescription());
		} catch (Exception e) {
			throw new RuntimeException(
				"Application "+getName()+" has no generated Math\n"+
				"Failed to generate new Math\n"+
				e.getMessage()
			);
		}
	}
	if (simContextOwner==null){
		throw new RuntimeException("cannot add simulation, bioModel not set yet");
	}
	//
	// get free name for new Simulation.
	//
	Simulation sims[] = simContextOwner.getSimulations();
	String newSimName = null;
	for (int i = 0; newSimName==null && i < 100; i++){
		String proposedName = "Simulation"+i;
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
	// create new Simulation and add to SimulationContextOwner.
	//
	Simulation newSimulation = new Simulation(getMathDescription());
	newSimulation.setName(newSimName);
	
	simContextOwner.addSimulation(newSimulation);

	return newSimulation;
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulations
 */
public void addSimulation(cbit.vcell.simulation.Simulation newSimulation) throws java.beans.PropertyVetoException {
	if (newSimulation.getMathDescription() == null){
		throw new IllegalArgumentException("cannot add simulation '"+newSimulation.getName()+"', has no MathDescription");
	}
	if (newSimulation.getMathDescription() != getMathDescription()){
		throw new IllegalArgumentException("cannot add simulation '"+newSimulation.getName()+"', has different MathDescription");
	}
	if (simContextOwner==null){
		throw new RuntimeException("cannot add simulation, bioModel not set yet");
	}
	simContextOwner.addSimulation(newSimulation);
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

	if (!cbit.util.Compare.isEqual(getName(),simContext.getName())){
		return false;
	}
	if (!cbit.util.Compare.isEqual(getDescription(),simContext.getDescription())){
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
public IAnalysisTask copyAnalysisTask(IAnalysisTask analysisTask) throws java.beans.PropertyVetoException, cbit.vcell.parser.ExpressionException, MappingException, cbit.vcell.math.MathException {

	String newAnalysisTaskName = analysisTask.getName()+" Copy";

	IAnalysisTask analysisTasks[] = getAnalysisTasks();
	boolean found = true;
	while (found) {
		found = false;
		newAnalysisTaskName = cbit.util.TokenMangler.getNextEnumeratedToken(newAnalysisTaskName);
		for (int i = 0;analysisTasks!=null && i < analysisTasks.length; i++){
			if (analysisTasks[i].getName().equals(newAnalysisTaskName)){
				found = true;
				continue;
			}
		}
	}

	IAnalysisTask newAnalysisTask = analysisTask.createClone(newAnalysisTaskName);
	addAnalysisTask(newAnalysisTask);
	return newAnalysisTask;
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
	if (simContextOwner==null){
		throw new RuntimeException("cannot add simulation, bioModel not set yet");
	}
	//
	// get free name for copied Simulation.
	//
	Simulation sims[] = simContextOwner.getSimulations();
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
	// create new Simulation and add to SimulationContextOwner.
	//
	Simulation newSimulation = new Simulation(simulation);
	newSimulation.setName(newSimName);
	
	simContextOwner.addSimulation(newSimulation);

	return newSimulation;
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 1:41:57 PM)
 * @return cbit.vcell.solver.Simulation[]
 * @param allSimulations cbit.vcell.solver.Simulation[]
 */
private Simulation[] extractLocalSimulations(Simulation[] allSimulations) {
	Vector list = new Vector();
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
public void gatherIssues(Vector issueVector) {
	getReactionContext().gatherIssues(issueVector);
	getGeometryContext().gatherIssues(issueVector);
}


/**
 * Gets the analysisTasks property (cbit.vcell.modelopt.AnalysisTask[]) value.
 * @return The analysisTasks property value.
 * @see #setAnalysisTasks
 */
public IAnalysisTask[] getAnalysisTasks() {
	return fieldAnalysisTasks;
}


/**
 * Gets the analysisTasks index property (cbit.vcell.modelopt.AnalysisTask) value.
 * @return The analysisTasks property value.
 * @param index The index value into the property array.
 * @see #setAnalysisTasks
 */
public IAnalysisTask getAnalysisTasks(int index) {
	return getAnalysisTasks()[index];
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 11:54:10 AM)
 * @return SimulationContextOwner
 */
public SimulationContextOwner getSimulationContextOwner() {
	return simContextOwner;
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
public cbit.vcell.mapping.ElectricalStimulus[] getElectricalStimuli() {
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


/**
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	
	cbit.vcell.parser.SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
	return getNameScope().getExternalEntry(identifierString);
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
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public cbit.vcell.parser.SymbolTableEntry getLocalEntry(java.lang.String identifier) throws cbit.vcell.parser.ExpressionBindingException {
	cbit.vcell.parser.SymbolTableEntry ste = cbit.vcell.model.ReservedSymbol.fromString(identifier);
	if (ste!=null){
		return ste;
	}
	
	//
	// if simulationContext parameter exists, then return it
	//
	ste = getSimulationContextParameter(identifier);
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


/**
 * This method was created in VisualAge.
 */
public Model getModel() {
	return geoContext.getModel();
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
 * @return cbit.vcell.parser.NameScope
 */
public cbit.vcell.parser.NameScope getNameScope() {
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
public cbit.vcell.simulation.Simulation[] getSimulations() {
	return extractLocalSimulations(simContextOwner.getSimulations());
}


/**
 * Gets the simulations index property (cbit.vcell.solver.Simulation) value.
 * @return The simulations property value.
 * @param index The index value into the property array.
 * @see #setSimulations
 */
public cbit.vcell.simulation.Simulation getSimulations(int index) {
	return getSimulations()[index];
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
	if (event.getSource() == getGeometryContext() && event.getPropertyName().equals("geometry")){
		firePropertyChange("geometry", event.getOldValue(), event.getNewValue());
	}
	if (event.getSource() == getGeometryContext() && event.getPropertyName().equals("model")){
		firePropertyChange("model", event.getOldValue(), event.getNewValue());
	}
	if (event.getSource() == getSimulationContextOwner() && event.getPropertyName().equals("simulations")){
		Simulation oldSimulations[] = extractLocalSimulations((Simulation[])event.getOldValue());
		Simulation newSimulations[] = extractLocalSimulations((Simulation[])event.getNewValue());
		firePropertyChange("simulations",oldSimulations,newSimulations);
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
	cbit.util.Extent extent = geo.getExtent();
	
	if (characteristicSize == null){
		//
		// if characteristicSize is not specified, estimate a 'good' value
		//
		cbit.image.VCImage image = geo.getGeometrySpec().getImage();
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
	}
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
public void removeAnalysisTask(IAnalysisTask analysisTask) throws PropertyVetoException {
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
	IAnalysisTask[] newAnalysisTasks = (IAnalysisTask[])BeanUtils.removeElement(fieldAnalysisTasks,analysisTask);
	setAnalysisTasks(newAnalysisTasks);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulations
 */
public void removeSimulation(cbit.vcell.simulation.Simulation simulation) throws java.beans.PropertyVetoException {
	if (simulation.getMathDescription() != getMathDescription()){
		throw new IllegalArgumentException("cannot remove simulation '"+simulation.getName()+"', has different MathDescription");
	}
	simContextOwner.removeSimulation(simulation);
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
public void setAnalysisTasks(IAnalysisTask[] analysisTasks) throws java.beans.PropertyVetoException {
	IAnalysisTask[] oldValue = fieldAnalysisTasks;
	fireVetoableChange("analysisTasks", oldValue, analysisTasks);
	fieldAnalysisTasks = analysisTasks;
	firePropertyChange("analysisTasks", oldValue, analysisTasks);
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 11:54:10 AM)
 * @param newBioModel SimulationContextOwner
 */
public void setSimulationContextOwner(SimulationContextOwner newBioModel) {
	if (this.simContextOwner!=null){
		this.simContextOwner.removePropertyChangeListener(this);
		this.simContextOwner.removeVetoableChangeListener(this);
	}
	simContextOwner = newBioModel;
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
	fireVetoableChange("description", oldValue, description);
	fieldDescription = description;
	firePropertyChange("description", oldValue, description);
}


/**
 * Sets the electricalStimuli property (cbit.vcell.mapping.ElectricalStimulus[]) value.
 * @param electricalStimuli The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getElectricalStimuli
 */
public void setElectricalStimuli(cbit.vcell.mapping.ElectricalStimulus[] electricalStimuli) throws java.beans.PropertyVetoException {
	cbit.vcell.mapping.ElectricalStimulus[] oldValue = fieldElectricalStimuli;
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
// now firing from geoContext
//		firePropertyChange("geometry",oldGeometry,geometry);
	}
}


/**
 * This method was created in VisualAge.
 * @param geoContext cbit.vcell.mapping.GeometryContext
 */
private void setGeometryContext(GeometryContext argGeoContext) throws java.beans.PropertyVetoException {
	this.geoContext = argGeoContext;
	refreshCharacteristicSize();
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
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
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
private void setVersion(cbit.util.Version newVersion) throws PropertyVetoException {
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
	if (evt.getSource() == this && evt.getPropertyName().equals("name")){
		if (evt.getNewValue() == null || ((String)evt.getNewValue()).trim().length()==0){
			throw new PropertyVetoException("SimulationContext name '"+evt.getNewValue()+"' invalid",evt);
		}
	}
	if (evt.getSource() == getSimulationContextOwner() && evt.getPropertyName().equals("simulations")){
		Simulation oldSimulations[] = extractLocalSimulations((Simulation[])evt.getOldValue());
		Simulation newSimulations[] = extractLocalSimulations((Simulation[])evt.getNewValue());
		fireVetoableChange("simulations",oldSimulations,newSimulations);
	}
}
}