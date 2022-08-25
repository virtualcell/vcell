/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.DataAccessException;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.Identifiable;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.document.SimulationVersion;
import org.vcell.util.document.Version;
import org.vcell.util.document.Versionable;

import cbit.vcell.mapping.SimulationContext.Kind;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContextEntity;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VCML;
import cbit.vcell.solver.SolverDescription.SolverFeature;
/**
 * Specifies the problem to be solved by a solver.
 * It is subclassed for each type of problem/solver.
 * Creation date: (8/16/2000 11:08:33 PM)
 * @author: John Wagner
 */
@SuppressWarnings("serial")
public class Simulation implements Versionable, Matchable, java.beans.VetoableChangeListener, java.io.Serializable, PropertyChangeListener,
		SimulationContextEntity, IssueSource, Identifiable, Displayable {

	public static final String PSF_FUNCTION_NAME = "__PSF__";
	/**
	 * {@link PropertyChangeEvent#getPropertyName()} value
	 */
	public static final String PROPERTY_NAME_SOLVER_TASK_DESCRIPTION = "solverTaskDescription";
	/**
	 * {@link PropertyChangeEvent#getPropertyName()} value
	 */
	public static final String PROPERTY_NAME_MATH_DESCRIPTION = "mathDescription";
	// size quotas enforced per simulation
	public static final int MAX_LIMIT_NON_SPATIAL_TIMEPOINTS = 100000;
	public static final int MAX_LIMIT_SPATIAL_TIMEPOINTS = 100000;
	public static final int MAX_LIMIT_0DE_MEGABYTES = 200; //gcw 3/18/2015 bump for now so Phd candiate can get her work done
	public static final int MAX_LIMIT_PDE_MEGABYTES = 200000;
	public static final int MAX_LIMIT_STOCH_MEGABYTES = 200; //stoch
	public static final int WARNING_NON_SPATIAL_TIMEPOINTS = 5000;
	public static final int WARNING_SPATIAL_TIMEPOINTS = 1000;
	public static final int WARNING_0DE_MEGABYTES = 5;
	public static final int WARNING_PDE_MEGABYTES = 200;
	public static final int WARNING_STOCH_MEGABYTES = 100; //stoch

	public static final int WARNING_SCAN_JOBS = 20;
	public static final int MAX_LIMIT_SCAN_JOBS = 100;
	/**
	 * Database version of the Simulation.
	 */
	private SimulationVersion fieldSimulationVersion = null;
	/**
	 * Mathematical description of the physiological model.
	 */
	private MathDescription fieldMathDescription = null;
	/**
	 * An ASCII description of the run.
	 */
	private java.lang.String fieldDescription = new String();
	/**
	 * The name of the run, also used as a version name.
	 */
	private java.lang.String fieldName = new String("NoName");
	/**
	 * Settings that override those specified in the MathDescription.
	 */
	private transient SimulationOwner simulationOwner = null;
	private DataProcessingInstructions dataProcessingInstructions = null;
	private MathOverrides fieldMathOverrides = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SolverTaskDescription fieldSolverTaskDescription = null;
	private java.lang.String fieldSimulationIdentifier = null;
	private MeshSpecification fieldMeshSpecification = null;
	private boolean fieldIsDirty = false;
	private String fieldImportedTaskID;

private Simulation( ) {
}
/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when creating a new Simulation.
 */
public Simulation(SimulationVersion argSimulationVersion, MathDescription mathDescription) {
	this( );
	addVetoableChangeListener(this);
	this.fieldSimulationVersion = argSimulationVersion;
	if (fieldSimulationVersion != null) {
		if (fieldSimulationVersion.getParentSimulationReference()!=null){
			this.fieldSimulationIdentifier = null;
		}else{
			this.fieldSimulationIdentifier = createSimulationID(fieldSimulationVersion.getVersionKey());
		}
	}

	this.fieldName = argSimulationVersion.getName();
	this.fieldDescription = argSimulationVersion.getAnnot();

	try {
		setMathDescription(mathDescription);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
	//  Must set the MathDescription before constructing these...
	if (mathDescription.getGeometry().getDimension()>0){
		fieldMeshSpecification = new MeshSpecification(mathDescription.getGeometry());
	}
	fieldMathOverrides = new MathOverrides(this);
	fieldSolverTaskDescription = new SolverTaskDescription(this);
	refreshDependencies();
}


/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when creating a Simulation from the database.
 */
public Simulation(SimulationVersion simulationVersion, MathDescription mathDescription, CommentStringTokenizer mathOverridesTokenizer, CommentStringTokenizer solverTaskDescriptionTokenizer) throws DataAccessException, PropertyVetoException {
	this( );
	addVetoableChangeListener(this);

	fieldSimulationVersion = simulationVersion;
	if (simulationVersion!=null){
		fieldName = simulationVersion.getName();
		fieldDescription = simulationVersion.getAnnot();
		if (simulationVersion.getParentSimulationReference()!=null){
			fieldSimulationIdentifier = null;
		}else{
			fieldSimulationIdentifier = createSimulationID(simulationVersion.getVersionKey());
		}
	}
	if (mathDescription != null){
		setMathDescription(mathDescription);
		if (mathDescription.getGeometry().getDimension()>0){
			fieldMeshSpecification = new MeshSpecification(mathDescription.getGeometry());
		}
	}
	//  Must set the MathDescription before constructing these...
	fieldMathOverrides = new MathOverrides(this, mathOverridesTokenizer);
	fieldSolverTaskDescription = new SolverTaskDescription(this, solverTaskDescriptionTokenizer);
	refreshDependencies();
}


/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when creating a new Simulation.
 */
public Simulation(MathDescription mathDescription) {
	this( );
	addVetoableChangeListener(this);

	try {
		setMathDescription(mathDescription);
	} catch (java.beans.PropertyVetoException e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
	fieldName = mathDescription.getName()+"_"+Math.random();
	//  Must set the MathDescription before constructing these...
	if (mathDescription.getGeometry().getDimension()>0){
		fieldMeshSpecification = new MeshSpecification(mathDescription.getGeometry());
	}
	fieldMathOverrides = new MathOverrides(this);
	fieldSolverTaskDescription = new SolverTaskDescription(this);

}


/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when copying a Simulation from an existing one.
 */
public Simulation(Simulation simulation) {
	this(simulation,false);
}


/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when copying a Simulation from an existing one.
 */
public Simulation(Simulation simulation, boolean bCloneMath) {
	this( );
	addVetoableChangeListener(this);

	fieldImportedTaskID = simulation.getImportedTaskID();
	fieldSimulationVersion = null;
	fieldName = simulation.getName();
	fieldDescription = simulation.getDescription();
	fieldSimulationIdentifier = null;
	if (bCloneMath){
		fieldMathDescription = new MathDescription(simulation.getMathDescription());
	}else{
		fieldMathDescription = simulation.getMathDescription();
	}
	if (simulation.getMeshSpecification()!=null){
		fieldMeshSpecification = new MeshSpecification(simulation.getMeshSpecification());
	}else{
		fieldMeshSpecification = null;
	}
	//  Must set the MathDescription before constructing these...
	fieldMathOverrides = new MathOverrides (this, simulation.getMathOverrides());
	fieldSolverTaskDescription = new SolverTaskDescription(this, simulation.getSolverTaskDescription());
	dataProcessingInstructions = simulation.dataProcessingInstructions;
	refreshDependencies();
}


public SimulationOwner getSimulationOwner() {
	return simulationOwner;
}


public void setSimulationOwner(SimulationOwner simulationOwner) {
	this.simulationOwner = simulationOwner;
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
 * Insert the method's description here.
 * Creation date: (4/24/2003 3:33:12 PM)
 */
public void clearVersion() {
	fieldSimulationVersion = null;
}


/**
 * compareEqual method comment.
 */
public boolean compareEqual(Matchable object) {
	if (this == object) {
		return (true);
	}
	if (object != null && object instanceof Simulation) {
		Simulation simulation = (Simulation) object;
		//
		// check for content
		//
		if (!compareEqualMathematically(simulation)){
			return false;
		}
		//
		// check for true equality
		//
		if (!Compare.isEqual(getName(),simulation.getName())){
			return false;
		}
		if (!Compare.isEqualOrNull(getDescription(),simulation.getDescription())){
			return false;
		}
		return true;
	}
	return false;

}


/**
 * compareEqual method comment.
 */
private boolean compareEqualMathematically(Simulation simulation) {
	if (this == simulation) {
		return true;
	}
	if (!getMathDescription().compareEqual(simulation.getMathDescription())) return (false);
	if (!getMathOverrides().compareEqual(simulation.getMathOverrides())) return (false);
	if (!getSolverTaskDescription().compareEqual(simulation.getSolverTaskDescription())) return (false);
	if (!Compare.isEqualOrNull(getMeshSpecification(),simulation.getMeshSpecification())) return (false);
	if (!Compare.isEqualOrNull(dataProcessingInstructions, simulation.dataProcessingInstructions)) return (false);

	return true;
}

/**
 * Insert the method's description here.
 * Creation date: (10/25/00 1:53:36 PM)
 * @return java.lang.String
 * @param version cbit.sql.Version
 */
public static String createSimulationID(KeyValue simKey) {
	return "SimID_"+simKey;
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
 * Insert the method's description here.
 * Creation date: (3/18/2004 1:54:51 PM)
 * @param newVersion cbit.sql.Version
 */
public void forceNewVersionAnnotation(SimulationVersion newSimulationVersion) throws PropertyVetoException {
	if (getVersion().getVersionKey().equals(newSimulationVersion.getVersionKey())) {
		setVersion(newSimulationVersion);
	} else {
		throw new RuntimeException("Simulation.forceNewVersionAnnotation failed : version keys not equal");
	}
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
 * Gets the isDirty property (boolean) value.
 * @return The isDirty property value.
 * @see #setIsDirty
 */
public boolean getIsDirty() {
	return fieldIsDirty;
}

/**
 * Gets the isSpatial property (boolean) value.
 * @return {@link #fieldMathDescription#isSpatial()}
 */
public boolean isSpatial() {
	assert fieldMathDescription != null ;
	return fieldMathDescription.isSpatial();
}


//public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
//
//	getMathOverrides().gatherIssues(issueContext, issueList);
//
//	//
//	// Check if the math corresponding to this simulation has fast systems and if the solverTaskDescription contains a non-null sensitivity parameter.
//	// If so, the simulation is invalid.
//	//
//	if (fieldMathDescription != null && getSolverTaskDescription() != null) {
//		if (getMathDescription().hasFastSystems() && (getSolverTaskDescription().getSensitivityParameter() != null)) {
//			Issue issue = new Issue(this, issueContext, IssueCategory.Simulation_SensAnal_And_FastSystem,
//									VCellErrorMessages.getErrorMessage(VCellErrorMessages.SIMULATION_SENSANAL_FASTSYSTEM,getName()),
//									Issue.SEVERITY_ERROR);
//			issueList.add(issue);
//		}
//	}
//	if (fieldMathDescription==null || !fieldMathDescription.isValid()){
//		Issue issue = new Issue(this, issueContext, IssueCategory.MathDescription_MathException,fieldMathDescription.getWarning(),Issue.SEVERITY_ERROR);
//		issueList.add(issue);
//	}
//
//	Set<SolverFeature> supportedFeatures = getSolverTaskDescription().getSolverDescription().getSupportedFeatures();
//	Set<SolverFeature> missingFeatures = getRequiredFeatures();
//	missingFeatures.removeAll(supportedFeatures);
//
//	String text = "The selected Solver does not support the following required features: \n";
//	for (SolverFeature sf : missingFeatures) {
//		text += sf.getName() + "\n";
//	}
//
//	if (!missingFeatures.isEmpty()) {
//		System.out.println(this.getKey());
//		String tooltip = "The selected Solver " + getSolverTaskDescription().getSolverDescription().getDisplayLabel() +
//				" does not support the following required features: <br>";
//		for (SolverFeature sf : missingFeatures) {
//			tooltip += "&nbsp;&nbsp;&nbsp;" + sf.getName() + "<br>";
//		}
//		Collection<SolverDescription >goodSolvers = SolverDescription.getSolverDescriptions(getRequiredFeatures());
//		assert goodSolvers != null;
//		if (!goodSolvers.isEmpty()) {
//			tooltip += "Please choose one of the solvers : <br>";
//			for (SolverDescription sd : goodSolvers) {
//				tooltip += "&nbsp;&nbsp;&nbsp;" + sd.getDisplayLabel() + "<br>";
//			}
//		}
//		Issue issue = new Issue(this,issueContext, IssueCategory.MathDescription_MathException, text, tooltip, Issue.SEVERITY_ERROR);
//		issueList.add(issue);
//	}
//}

public Set<SolverFeature> getRequiredFeatures() {
	Set<SolverFeature> requiredFeatures = new HashSet<SolverFeature>();
	final MathDescription md = getMathDescription();
	if (isSpatial()) {
		requiredFeatures.add(SolverFeature.Feature_Spatial);
	} else {
		requiredFeatures.add(SolverFeature.Feature_NonSpatial);
	}

	final boolean hybrid = md.isSpatialHybrid();
	final boolean stoch = md.isNonSpatialStoch() || md.isSpatialStoch();
	final boolean ruleBased = md.isRuleBased();
	if (hybrid) {
		requiredFeatures.add(SolverFeature.Feature_Hybrid);
	}
	if (stoch && !hybrid) {
		requiredFeatures.add(SolverFeature.Feature_Stochastic);
	}
	if (!stoch && !ruleBased) {
		requiredFeatures.add(SolverFeature.Feature_Deterministic);
	}
	if (md.hasFastSystems()) {
		requiredFeatures.add(SolverFeature.Feature_FastSystem);
	}
	if (md.hasPeriodicBoundaryCondition()) {
		requiredFeatures.add(SolverFeature.Feature_PeriodicBoundaryCondition);
	}
	if (md.hasEvents()) {
		requiredFeatures.add(SolverFeature.Feature_Events);
	}
	if (md.hasRandomVariables()) {
		requiredFeatures.add(SolverFeature.Feature_RandomVariables);
	}
	if (getSolverTaskDescription().getStopAtSpatiallyUniformErrorTolerance() != null) {
		requiredFeatures.add(SolverFeature.Feature_StopAtSpatiallyUniform);
	}
	if (getDataProcessingInstructions() != null) {
		requiredFeatures.add(SolverFeature.Feature_DataProcessingInstructions);
	}
	if (md.getVariable(PSF_FUNCTION_NAME) != null) {
		requiredFeatures.add(SolverFeature.Feature_PSF);
	}
	if (isSerialParameterScan()) {
		requiredFeatures.add(SolverFeature.Feature_SerialParameterScans);
	}
	if (md.hasVolumeRegionEquations()) {
		requiredFeatures.add(SolverFeature.Feature_VolumeRegionEquations);
	}
	if (md.hasRegionSizeFunctions()) {
		requiredFeatures.add(SolverFeature.Feature_RegionSizeFunctions);
	}
	if (md.hasGradient()) {
		requiredFeatures.add(SolverFeature.Feature_GradientSourceTerm);
	}
	if (md.getPostProcessingBlock().getNumDataGenerators() > 0) {
		requiredFeatures.add(SolverFeature.Feature_PostProcessingBlock);
	}
	if (md.isRuleBased()){
		requiredFeatures.add(SolverFeature.Feature_Rulebased);
	}
	return requiredFeatures;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/00 1:36:01 PM)
 * @return cbit.sql.KeyValue
 */
public KeyValue getKey() {
	return (getVersion()!=null)?(getVersion().getVersionKey()):(null);
}


/**
 * Gets the mathDescription property (cbit.vcell.math.MathDescription) value.
 * @return The mathDescription property value.
 */
public MathDescription getMathDescription() {
	return fieldMathDescription;
}


/**
 * Gets the mathOverrides property (cbit.vcell.solver.MathOverrides) value.
 * @return The mathOverrides property value.
 */
public MathOverrides getMathOverrides() {
	return fieldMathOverrides;
}


/**
 * Gets the meshSpecification property (cbit.vcell.mesh.MeshSpecification) value.
 * @return The meshSpecification property value.
 * @see #setMeshSpecification
 */
public MeshSpecification getMeshSpecification() {
	return fieldMeshSpecification;
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
 * Creation date: (10/5/2005 1:02:08 PM)
 * @return int
 */
public int getScanCount() {
	return getMathOverrides().getScanCount();
}


/**
 * Gets the simulationIdentifier property (java.lang.String) value.
 * @return The simulationIdentifier property value.
 */
public java.lang.String getSimulationID() {
	return fieldSimulationIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (10/30/00 11:48:21 AM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public SimulationInfo getSimulationInfo() {
	if (getVersion() != null) {
		return new SimulationInfo(
			getMathDescription().getKey(),
			getSimulationVersion(),null);
	} else {
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 1:34:10 PM)
 * @return cbit.sql.Version
 */
public SimulationVersion getSimulationVersion() {
	return fieldSimulationVersion;
}


/**
 * Gets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @return The solverTaskDescription property value.
 * @see #setSolverTaskDescription
 */
public SolverTaskDescription getSolverTaskDescription() {
	return fieldSolverTaskDescription;
}


/**
 * Insert the method's description here.
 * Creation date: (10/30/00 11:56:03 AM)
 * @return java.lang.String
 */
public String getVCML() throws MathException {

	StringBuffer buffer = new StringBuffer();

	String name = (getVersion()!=null)?(getVersion().getName()):"unnamedSimulation";
	buffer.append(VCML.Simulation+" "+name+" {\n");

	//
	// write MathDescription
	//
	buffer.append(VCML.MathDescription+" "+getMathDescription().getVCML_database()+"\n");

	//
	// write SolverTaskDescription
	//
	buffer.append(getSolverTaskDescription().getVCML()+"\n");

	//
	// write SolverTaskDescription
	//
	buffer.append(getMathOverrides().getVCML()+"\n");

	//
	// write MeshSpecification
	//
	if (getMeshSpecification()!=null){
		buffer.append(getMeshSpecification().getVCML()+"\n");
	}

	buffer.append("}\n");
	return buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 1:34:10 PM)
 * @return cbit.sql.Version
 */
public Version getVersion() {
	return fieldSimulationVersion;
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
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}

/**
 * Insert the method's description here.
 * Creation date: (5/11/01 4:00:35 PM)
 */
public void refreshDependencies() {
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
	if (getMeshSpecification()!=null){
		getMeshSpecification().refreshDependencies();
	}
	getSolverTaskDescription().refreshDependencies();
	getMathOverrides().refreshDependencies();

	getMathDescription().removePropertyChangeListener(this);
	getMathDescription().addPropertyChangeListener(this);

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
 * Sets the description property (java.lang.String) value.
 * @param description The new value for the property.
 * @see #getDescription
 */
public void setDescription(java.lang.String description) throws java.beans.PropertyVetoException {
	java.lang.String oldValue = fieldDescription;
	fireVetoableChange("description", oldValue, description);
	fieldDescription = description;
	firePropertyChange("description", oldValue, description);
}


/**
 * Sets the isDirty property (boolean) value.
 * @param isDirty The new value for the property.
 * @see #getIsDirty
 */
public void setIsDirty(boolean isDirty) {
	boolean oldValue = fieldIsDirty;
	fieldIsDirty = isDirty;
	firePropertyChange("isDirty", new Boolean(oldValue), new Boolean(isDirty));
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 1:17:37 PM)
 * @param mathDesc cbit.vcell.math.MathDescription
 */
public void setMathDescription(MathDescription mathDescription) throws java.beans.PropertyVetoException {
	MathDescription oldValue = fieldMathDescription;
	fireVetoableChange("mathDescription", oldValue, mathDescription);
	fieldMathDescription = mathDescription;

	if(oldValue != null){
		oldValue.removePropertyChangeListener(this);
	}
	if(fieldMathDescription != null){
		fieldMathDescription.removePropertyChangeListener(this);
		fieldMathDescription.addPropertyChangeListener(this);
	}
	refreshMeshSpec();

	//
	// refresh MathOverrides
	//
	if (mathDescription!=null && getMathOverrides()!=null && oldValue != fieldMathDescription){
		getMathOverrides().updateFromMathDescription();
	}

	//
	// refresh SolverTaskDescription (reset if oldMath is spatial and newMath is non-spatial .... or opposite).
	//
	if (oldValue==null || mathDescription==null || oldValue.isSpatial()!=mathDescription.isSpatial()){
		fieldSolverTaskDescription = new SolverTaskDescription(this);
	}

	firePropertyChange("mathDescription", oldValue, mathDescription);
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2001 7:13:50 PM)
 * @param newMathOverrides cbit.vcell.solver.MathOverrides
 * @exception java.beans.PropertyVetoException The exception description.
 */
public void setMathOverrides(MathOverrides mathOverrides) {
	MathOverrides oldValue = fieldMathOverrides;
	fieldMathOverrides = mathOverrides;
	// update overrides
	mathOverrides.setSimulation(this);
	mathOverrides.updateFromMathDescription();
	firePropertyChange("mathOverrides", oldValue, mathOverrides);
}


/**
 * Sets the meshSpecification property (cbit.vcell.mesh.MeshSpecification) value.
 * @param meshSpecification The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getMeshSpecification
 */
public void setMeshSpecification(MeshSpecification meshSpecification) throws java.beans.PropertyVetoException {
	MeshSpecification oldValue = fieldMeshSpecification;
	fireVetoableChange("meshSpecification", oldValue, meshSpecification);
	fieldMeshSpecification = meshSpecification;
	firePropertyChange("meshSpecification", oldValue, meshSpecification);
}


/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @see #setName
 */
public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
	java.lang.String oldValue = fieldName;
	fireVetoableChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, name);
	fieldName = name;
	firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, name);
}


/**
 * Sets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @param solverTaskDescription The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSolverTaskDescription()
 */
public void setSolverTaskDescription(SolverTaskDescription solverTaskDescription) throws java.beans.PropertyVetoException {
	SolverTaskDescription oldValue = fieldSolverTaskDescription;
	fireVetoableChange(PROPERTY_NAME_SOLVER_TASK_DESCRIPTION, oldValue, solverTaskDescription);
	fieldSolverTaskDescription = solverTaskDescription;
	if (solverTaskDescription != null && solverTaskDescription.getSimulation() != this) {
		throw new IllegalArgumentException("SolverTaskDescription simulation field points to wrong simulation");
	}
	firePropertyChange(PROPERTY_NAME_SOLVER_TASK_DESCRIPTION, oldValue, solverTaskDescription);
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 3:49:12 PM)
 * @param version cbit.sql.Version
 */
private void setVersion(SimulationVersion simulationVersion) throws PropertyVetoException {
	this.fieldSimulationVersion = simulationVersion;
	if (simulationVersion != null){
		setName(simulationVersion.getName());
		setDescription(simulationVersion.getAnnot());
	}
}

/**
 * Insert the method's description here.
 * Creation date: (9/28/2004 5:50:22 PM)
 * @return java.lang.String
 * @param memoryMathDescription cbit.vcell.math.MathDescription
 * @param databaseMathDescription cbit.vcell.math.MathDescription
 */
public static boolean testEquivalency(Simulation memorySimulation, Simulation databaseSimulation, MathCompareResults mathCompareResults) {

	if (memorySimulation == databaseSimulation){
		return true;
	}

	if (!mathCompareResults.isEquivalent()){
		return false;
	}else{
		if (!memorySimulation.getSolverTaskDescription().compareEqual(databaseSimulation.getSolverTaskDescription())){
			return false;
		}
		if (!Compare.isEqualOrNull(memorySimulation.getMeshSpecification(),databaseSimulation.getMeshSpecification())){
			return false;
		}
		//
		// math overrides are only influence the solution if they actually override something.
		//
		// if maths are equal/equivalent and overridden parameters (where actual value != default value) are same
		// then the MathDescriptions equality/equivalence is upheld.
		// otherwise, they are always different
		//
		// if (!memorySimulation.getMathOverrides().compareEqualIgnoreDefaults(databaseSimulation.getMathOverrides())){
		// now only non-defaults are stored in overrides...
		if (!memorySimulation.getMathOverrides().compareEquivalent(databaseSimulation.getMathOverrides())){
			return false;
		}
		return true;
	}
}


public String toString() {
	String mathStr = (getMathDescription()!=null)?("Math@"+Integer.toHexString(getMathDescription().hashCode())+"("+getMathDescription().getName()+","+getMathDescription().getKey()+")"):"null";
	return "Simulation@"+Integer.toHexString(hashCode())+"("+getName()+"), "+mathStr;
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
	TokenMangler.checkNameProperty(this, "simulation", evt);
}


	public DataProcessingInstructions getDataProcessingInstructions() {
		return dataProcessingInstructions;
	}


	public void setDataProcessingInstructions(DataProcessingInstructions dataProcessingInstructions) {
		DataProcessingInstructions oldValue = this.dataProcessingInstructions;
		this.dataProcessingInstructions = dataProcessingInstructions;
		firePropertyChange("dataProcessingInstructions", oldValue, dataProcessingInstructions);
}

	public boolean isSerialParameterScan() {
		if (getSolverTaskDescription().isSerialParameterScan() && getScanCount() > 1) {
			return true;
		}
		return false;
	}
	public boolean isTimeoutDisabled() {
		if (getSolverTaskDescription().isTimeoutDisabled()) {
			return true;
		}
		return false;
	}
	public boolean isBorderExtrapolationDisabled() {
		if (getSolverTaskDescription().isBorderExtrapolationDisabled()) {
			return true;
		}
		return false;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		MathDescription md = getMathDescription();
		boolean bIsMath = evt.getSource() == md;
		if(bIsMath && evt.getPropertyName().equals("geometry")){
			try{
				refreshMeshSpec();
			}catch(PropertyVetoException e){
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(),e);
			}
		}
	}

	private void refreshMeshSpec() throws PropertyVetoException{
		//
		// refresh MeshSpecification
		//
		if (getMathDescription().getGeometry().getDimension()>0){
			if (getMeshSpecification()!=null){
				getMeshSpecification().setGeometry(getMathDescription().getGeometry());
			}else{
				setMeshSpecification(new MeshSpecification(getMathDescription().getGeometry()));
			}
		}else{
			setMeshSpecification(null);
		}
	}

	public boolean hasCellCenteredMesh()
	{
		return getSolverTaskDescription() != null && getSolverTaskDescription().getSolverDescription() != null
				&& getSolverTaskDescription().getSolverDescription().hasCellCenteredMesh();
	}
	/**
	 * forward to {@link #simWarning}
	 * @param issueContext
	 * @param issueList
	 */
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		
		MathOverrides mo = getMathOverrides();
		if(mo!=null && mo.hasUnusedOverrides()) {
			String msg = "The Simulation has unused Math Overrides.";
			String tip = "Remove the unused Math Overrides.";
			issueList.add(new Issue(this, issueContext, IssueCategory.Simulation_Override_NotFound, msg, tip, Issue.Severity.ERROR));
		}
		
		SimulationWarning.gatherIssues(this, issueContext, issueList);
	}
	
	@Override
	public Kind getSimulationContextKind() {
		return SimulationContext.Kind.SIMULATIONS_KIND;
	}
	public void setImportedTaskID(String id) {
		fieldImportedTaskID = id;
	}
	public String getImportedTaskID() {
		return fieldImportedTaskID;
	}
	
	
	public static final String typeName = "Simulation";
	@Override
	public String getDisplayName() {
		return getName();
	}
	@Override
	public String getDisplayType() {
		return typeName;
	}
}
