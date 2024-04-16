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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.SpeciesPattern;
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

import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.Kind;
import cbit.vcell.mapping.ReactionContext;
import cbit.vcell.mapping.ReactionRuleSpec;
import cbit.vcell.mapping.ReactionRuleSpec.Subtype;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContextEntity;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.MathCompareResults;
import cbit.vcell.math.MathDescription;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VCML;
import cbit.vcell.model.Model;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.solver.SolverDescription.SolverFeature;
/**
 * Specifies the problem to be solved by a solver.
 * It is subclassed for each type of problem/solver.
 * Creation date: (8/16/2000 11:08:33 PM)
 * @author: John Wagner
 */
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
	private SimulationVersion simulationVersion = null;
	/**
	 * Mathematical description of the physiological model.
	 */
	private MathDescription mathDescription = null;
	/**
	 * An ASCII description of the run.
	 */
	private String description = "";
	/**
	 * The name of the run, also used as a version name.
	 */
	private String name = "NoName";
	/**
	 * Settings that override those specified in the MathDescription.
	 */
	private transient SimulationOwner simulationOwner = null;
	private DataProcessingInstructions dataProcessingInstructions = null;
	private MathOverrides mathOverrides = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SolverTaskDescription solverTaskDescription = null;
	private java.lang.String simulationIdentifier = null;
	private MeshSpecification meshSpecification = null;
	private boolean isDirty = false;
	private String importedTaskID;

	public static class Counters {		// SpringSaLaD post processing counters, structure to be determined, will be moved appropriately

		public static void writeMoleculeCounters(SimulationContext simContext, StringBuilder sb) {
			if(simContext == null) {
				sb.append("\n");
				return;
			}
			if(simContext.getApplicationType() != Application.SPRINGSALAD) {
				sb.append("\n");
				return;
			}
			Model model = simContext.getBioModel().getModel();
			SpeciesContext[] speciesContexts = model.getSpeciesContexts();
			for(SpeciesContext sc : speciesContexts) {
				if(SpeciesContextSpec.SourceMoleculeString.equals(sc.getName()) || SpeciesContextSpec.SinkMoleculeString.equals(sc.getName())) {
					continue;	// skip the Source and the Sink molecules (use in Creation / Destruction reactions)
				}
				SpeciesPattern sp = sc.getSpeciesPattern();
				if(sp == null || sp.getMolecularTypePatterns() == null || sp.getMolecularTypePatterns().isEmpty()) {
					continue;
				}
				// Each MolecularType can be used for only one SpeciesContext
				MolecularType mt = sp.getMolecularTypePatterns().get(0).getMolecularType();
				sb.append("'").append(mt.getName()).append("' : ")
						.append("Measure Total Free Bound");
				sb.append("\n");
			}
		}

		public static void writeStateCounters(SimulationContext simContext, StringBuilder sb) {
			if(simContext == null) {
				sb.append("\n");
				return;
			}
			if(simContext.getApplicationType() != Application.SPRINGSALAD) {
				sb.append("\n");
				return;
			}
			Model model = simContext.getBioModel().getModel();
			SpeciesContext[] speciesContexts = model.getSpeciesContexts();
			for(SpeciesContext sc : speciesContexts) {
				SpeciesPattern sp = sc.getSpeciesPattern();
				if(sp == null || sp.getMolecularTypePatterns() == null || sp.getMolecularTypePatterns().isEmpty()) {
					continue;
				}
				MolecularType mt = sp.getMolecularTypePatterns().get(0).getMolecularType();
				List<MolecularComponent> mcList = mt.getComponentList();
				for(MolecularComponent mc : mcList) {
					List<ComponentStateDefinition> csdList = mc.getComponentStateDefinitions();
					for(ComponentStateDefinition csd : csdList) {
						sb.append("'").append(mt.getName()).append("' : ")
							.append("'").append(mc.getName()).append("' : ")
							.append("'").append(csd.getName()).append("' : ")
							.append("Measure Total Free Bound");
						sb.append("\n");
					}
				}
			}
		}

		public static void writeBondCounters(SimulationContext simContext, StringBuilder sb) {
			if(simContext == null) {
				sb.append("\n");
				return;
			}
			if(simContext.getApplicationType() != Application.SPRINGSALAD) {
				sb.append("\n");
				return;
			}
			ReactionContext reactionContext = simContext.getReactionContext();
			ReactionRuleSpec[] reactionRuleSpecs = reactionContext.getReactionRuleSpecs();
			for(ReactionRuleSpec rrs : reactionRuleSpecs) {
				if(rrs.isExcluded()) {
					continue;
				}
				Map<String, Object> analysisResults = new LinkedHashMap<> ();
				rrs.analizeReaction(analysisResults);
				Subtype subtype = rrs.getSubtype(analysisResults);
				if(subtype == ReactionRuleSpec.Subtype.BINDING) {
					sb.append("'").append(rrs.getReactionRule().getName()).append("' : ") // was ("' : '")
						.append("Counted");
					sb.append("\n");
				}
			}
		}
		
		public static void writeSitePropertyCounters(SimulationContext simContext, StringBuilder sb) {
			if(simContext == null) {
				sb.append("\n");
				return;
			}
			if(simContext.getApplicationType() != Application.SPRINGSALAD) {
				sb.append("\n");
				return;
			}
			Model model = simContext.getBioModel().getModel();
			SpeciesContext[] speciesContexts = model.getSpeciesContexts();
			for(SpeciesContext sc : speciesContexts) {
				SpeciesPattern sp = sc.getSpeciesPattern();
				if(sp == null || sp.getMolecularTypePatterns() == null || sp.getMolecularTypePatterns().isEmpty()) {
					continue;
				}
				MolecularType mt = sp.getMolecularTypePatterns().get(0).getMolecularType();
				List<MolecularComponent> mcList = mt.getComponentList();
				for(MolecularComponent mc : mcList) {
					sb.append("'").append(mt.getName()).append("' ").append("Site ").append(mc.getIndex() - 1).append(" : ")
						.append("Track Properties true");
					sb.append("\n");
				}
			}
		}
	}
	
private Simulation() {}
/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when creating a new Simulation.
 */
public Simulation(SimulationVersion argSimulationVersion, MathDescription mathDescription, SimulationOwner simulationOwner) {
	this();
	this.addVetoableChangeListener(this);
	this.simulationVersion = argSimulationVersion;
	if (this.simulationVersion != null) {
		if (this.simulationVersion.getParentSimulationReference() != null){
			this.simulationIdentifier = null;
		}else{
			this.simulationIdentifier = createSimulationID(this.simulationVersion.getVersionKey());
		}
	}

	this.name = argSimulationVersion.getName();
	this.description = argSimulationVersion.getAnnot();
	this.simulationOwner = simulationOwner;

	try {
		setMathDescription(mathDescription);
	} catch (java.beans.PropertyVetoException e) {
		throw new RuntimeException(e.getMessage(), e);
	}
	//  Must set the MathDescription before constructing these...
	if (mathDescription.getGeometry().getDimension() > 0){
        this.meshSpecification = new MeshSpecification(mathDescription.getGeometry());
	}
    this.mathOverrides = new MathOverrides(this);
    this.solverTaskDescription = new SolverTaskDescription(this);
	this.refreshDependencies();
}


/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when creating a Simulation from the database.
 */
public Simulation(SimulationVersion simulationVersion, MathDescription mathDescription, CommentStringTokenizer mathOverridesTokenizer, CommentStringTokenizer solverTaskDescriptionTokenizer) throws DataAccessException, PropertyVetoException {
	this();
	this.addVetoableChangeListener(this);

	this.simulationVersion = simulationVersion;
	if (simulationVersion!=null){
		this.name = simulationVersion.getName();
		this.description = simulationVersion.getAnnot();
		if (simulationVersion.getParentSimulationReference()!=null){
			this.simulationIdentifier = null;
		}else{
			this.simulationIdentifier = createSimulationID(simulationVersion.getVersionKey());
		}
	}
	if (mathDescription != null){
		setMathDescription(mathDescription);
		if (mathDescription.getGeometry().getDimension()>0){
			this.meshSpecification = new MeshSpecification(mathDescription.getGeometry());
		}
	}
	//  Must set the MathDescription before constructing these...
	this.mathOverrides = new MathOverrides(this, mathOverridesTokenizer);
	this.solverTaskDescription = new SolverTaskDescription(this, solverTaskDescriptionTokenizer);
	refreshDependencies();
}


/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when creating a new Simulation.
 */
public Simulation(MathDescription mathDescription, SimulationOwner simulationOwner) {
	this();
	addVetoableChangeListener(this);

	try {
		setMathDescription(mathDescription);
	} catch (java.beans.PropertyVetoException e) {
		throw new RuntimeException(e.getMessage(), e);
	}
	this.name = mathDescription.getName()+"_"+Math.random();
	this.simulationOwner = simulationOwner;
	//  Must set the MathDescription before constructing these...
	if (mathDescription.getGeometry().getDimension()>0){
		this.meshSpecification = new MeshSpecification(mathDescription.getGeometry());
	}
	this.mathOverrides = new MathOverrides(this);
	this.solverTaskDescription = new SolverTaskDescription(this);

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
	this();
	this.addVetoableChangeListener(this);

	this.importedTaskID = simulation.getImportedTaskID();
	this.simulationVersion = null;
	this.name = simulation.getName();
	this.description = simulation.getDescription();
	this.simulationIdentifier = null;
	if (bCloneMath){
		this.mathDescription = new MathDescription(simulation.getMathDescription());
	}else{
		this.mathDescription = simulation.getMathDescription();
	}
	if (simulation.getMeshSpecification()!=null){
		this.meshSpecification = new MeshSpecification(simulation.getMeshSpecification());
	}else{
		this.meshSpecification = null;
	}
	this.simulationOwner = simulation.getSimulationOwner();
	//  Must set the MathDescription before constructing these...
	this.mathOverrides = new MathOverrides (this, simulation.getMathOverrides());
	this.solverTaskDescription = new SolverTaskDescription(this, simulation.getSolverTaskDescription());
	this.dataProcessingInstructions = simulation.dataProcessingInstructions;
	this.importedTaskID = simulation.importedTaskID;

	this.refreshDependencies();
}


public SimulationOwner getSimulationOwner() {
	return this.simulationOwner;
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
	this.simulationVersion = null;
}


/**
 * compareEqual method comment.
 */
public boolean compareEqual(Matchable object) {
	if (this == object) return true;
	if (!(object instanceof Simulation simulation)) return false;
	return this.compareEqualMathematically(simulation) // check for content
			&& Compare.isEqual(getName(),simulation.getName()) // check for true equality
			&& Compare.isEqualOrNull(getDescription(), simulation.getDescription());
}


/**
 * compareEqual method comment.
 */
private boolean compareEqualMathematically(Simulation simulation) {
	if (this == simulation) return true;
    return this.getMathDescription().compareEqual(simulation.getMathDescription())
			&& this.getMathOverrides().compareEqual(simulation.getMathOverrides())
			&& this.getSolverTaskDescription().compareEqual(simulation.getSolverTaskDescription())
			&& Compare.isEqualOrNull(getMeshSpecification(),simulation.getMeshSpecification())
			&& Compare.isEqualOrNull(this.dataProcessingInstructions, simulation.dataProcessingInstructions);
}

public static String createSimulationID(KeyValue simKey) {
	return "SimID_"+simKey;
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	this.getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The fireVetoableChange method was generated to support the vetoPropertyChange field.
 */
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	this.getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}


public void forceNewVersionAnnotation(SimulationVersion newSimulationVersion) throws PropertyVetoException {
	if (this.getVersion().getVersionKey().equals(newSimulationVersion.getVersionKey())) {
		this.setVersion(newSimulationVersion);
		return;
	}
	throw new RuntimeException("Simulation.forceNewVersionAnnotation failed : version keys not equal");
}


public java.lang.String getDescription() {
	return this.description;
}


public boolean getIsDirty() {
	return this.isDirty;
}

/**
 * Gets the isSpatial property (boolean) value.
 * @return {@link #mathDescription#isSpatial()}
 */
public boolean isSpatial() {
	assert this.mathDescription != null ;
	return this.mathDescription.isSpatial();
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

// this doesn't seem to be called anywhere, we deprecate it (also, it doesn't know about Feature_Springs)
// jul 2023 danv
@Deprecated
public Set<SolverFeature> getRequiredFeatures() {
	Set<SolverFeature> requiredFeatures = new HashSet<>();
	final MathDescription md = getMathDescription();
	if (this.isSpatial()) {
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
	return this.mathDescription;
}


/**
 * Gets the mathOverrides property (cbit.vcell.solver.MathOverrides) value.
 * @return The mathOverrides property value.
 */
public MathOverrides getMathOverrides() {
	return this.mathOverrides;
}


/**
 * Gets the meshSpecification property (cbit.vcell.mesh.MeshSpecification) value.
 * @return The meshSpecification property value.
 * @see #setMeshSpecification
 */
public MeshSpecification getMeshSpecification() {
	return this.meshSpecification;
}


/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public java.lang.String getName() {
	return this.name;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (this.propertyChange == null) {
        this.propertyChange = new java.beans.PropertyChangeSupport(this);
	}
	return this.propertyChange;
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
	return this.simulationIdentifier;
}


/**
 * Insert the method's description here.
 * Creation date: (10/30/00 11:48:21 AM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public SimulationInfo getSimulationInfo() {
	if (this.getVersion() == null) return null;
	return new SimulationInfo(getMathDescription().getKey(), getSimulationVersion(),null);
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 1:34:10 PM)
 * @return cbit.sql.Version
 */
public SimulationVersion getSimulationVersion() {
	return this.simulationVersion;
}


/**
 * Gets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @return The solverTaskDescription property value.
 * @see #setSolverTaskDescription
 */
public SolverTaskDescription getSolverTaskDescription() {
	return this.solverTaskDescription;
}


/**
 * Insert the method's description here.
 * Creation date: (10/30/00 11:56:03 AM)
 * @return java.lang.String
 */
public String getVCML() throws MathException {

	StringBuilder buffer = new StringBuilder();

	String name = (this.getVersion() != null) ? this.getVersion().getName() : "unnamedSimulation";
	buffer.append(VCML.Simulation + " ").append(name).append(" {\n");

	// write MathDescription
	buffer.append(VCML.MathDescription + " ").append(getMathDescription().getVCML_database()).append("\n");

	// write SolverTaskDescription
	buffer.append(this.getSolverTaskDescription().getVCML()).append("\n");

	// write SolverTaskDescription
	buffer.append(this.getMathOverrides().getVCML()).append("\n");

	// write MeshSpecification
	if (this.getMeshSpecification() != null) buffer.append(this.getMeshSpecification().getVCML()).append("\n");

	return buffer.append("}\n").toString();
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 1:34:10 PM)
 * @return cbit.sql.Version
 */
public Version getVersion() {
	return this.simulationVersion;
}


/**
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (this.vetoPropertyChange == null) {
        this.vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	}
	return this.vetoPropertyChange;
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
	this.removeVetoableChangeListener(this);
	this.addVetoableChangeListener(this);
	if (this.getMeshSpecification() != null){
		this.getMeshSpecification().refreshDependencies();
	}
	this.getSolverTaskDescription().refreshDependencies();
	this.getMathOverrides().refreshDependencies();

	this.getMathDescription().removePropertyChangeListener(this);
	this.getMathDescription().addPropertyChangeListener(this);

}

/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	this.getPropertyChange().removePropertyChangeListener(listener);
}

/**
 * The removeVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	this.getVetoPropertyChange().removeVetoableChangeListener(listener);
}

/**
 * Sets the description property (java.lang.String) value.
 * @param description The new value for the property.
 * @see #getDescription
 */
public void setDescription(java.lang.String description) throws java.beans.PropertyVetoException {
	String oldValue = this.description;
	this.fireVetoableChange("description", oldValue, description);
	this.description = description;
	this.firePropertyChange("description", oldValue, description);
}


/**
 * Sets the isDirty property (boolean) value.
 * @param isDirty The new value for the property.
 * @see #getIsDirty
 */
public void setIsDirty(boolean isDirty) {
	boolean oldValue = this.isDirty;
	this.isDirty = isDirty;
	this.firePropertyChange("isDirty", oldValue, isDirty);
}


public void setMathDescription(MathDescription mathDescription) throws java.beans.PropertyVetoException {
	MathDescription oldValue = this.mathDescription;
	fireVetoableChange("mathDescription", oldValue, mathDescription);
	this.mathDescription = mathDescription;

	if(oldValue != null){
		oldValue.removePropertyChangeListener(this);
	}
	if(this.mathDescription != null){
		this.mathDescription.removePropertyChangeListener(this);
		this.mathDescription.addPropertyChangeListener(this);
	}
	this.refreshMeshSpec();

	// refresh MathOverrides
	if (mathDescription != null && this.getMathOverrides() != null && oldValue != this.mathDescription){
		this.getMathOverrides().updateFromMathDescription(true);
	}

	//
	// refresh SolverTaskDescription (reset if oldMath is spatial and newMath is non-spatial .... or opposite).
	//
	if (oldValue == null || mathDescription == null || oldValue.isSpatial() != mathDescription.isSpatial()){
		this.solverTaskDescription = new SolverTaskDescription(this);
	}

	this.firePropertyChange("mathDescription", oldValue, mathDescription);
}


public void setMathOverrides(MathOverrides mathOverrides) {
	MathOverrides oldValue = this.mathOverrides;
	this.mathOverrides = mathOverrides;
	// update overrides
	mathOverrides.setSimulation(this);
	mathOverrides.updateFromMathDescription(false);
	this.firePropertyChange("mathOverrides", oldValue, mathOverrides);
}


/**
 * Sets the meshSpecification property (cbit.vcell.mesh.MeshSpecification) value.
 * @param meshSpecification The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getMeshSpecification
 */
public void setMeshSpecification(MeshSpecification meshSpecification) throws java.beans.PropertyVetoException {
	MeshSpecification oldValue = this.meshSpecification;
	this.fireVetoableChange("meshSpecification", oldValue, meshSpecification);
	this.meshSpecification = meshSpecification;
	this.firePropertyChange("meshSpecification", oldValue, meshSpecification);
}


/**
 * Sets the name property (java.lang.String) value.
 * @param name The new value for the property.
 * @see #setName
 */
public void setName(String name) throws java.beans.PropertyVetoException {
	String oldValue = this.name;
	this.fireVetoableChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, name);
	this.name = name;
	this.firePropertyChange(PropertyConstants.PROPERTY_NAME_NAME, oldValue, name);
}


/**
 * Sets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @param solverTaskDescription The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSolverTaskDescription()
 */
public void setSolverTaskDescription(SolverTaskDescription solverTaskDescription) throws java.beans.PropertyVetoException {
	SolverTaskDescription oldValue = this.solverTaskDescription;
	this.fireVetoableChange(PROPERTY_NAME_SOLVER_TASK_DESCRIPTION, oldValue, solverTaskDescription);
	this.solverTaskDescription = solverTaskDescription;
	if (solverTaskDescription != null && solverTaskDescription.getSimulation() != this) {
		throw new IllegalArgumentException("SolverTaskDescription simulation field points to wrong simulation");
	}
	this.firePropertyChange(PROPERTY_NAME_SOLVER_TASK_DESCRIPTION, oldValue, solverTaskDescription);
}


private void setVersion(SimulationVersion simulationVersion) throws PropertyVetoException {
	this.simulationVersion = simulationVersion;
	if (simulationVersion != null){
		this.setName(simulationVersion.getName());
		this.setDescription(simulationVersion.getAnnot());
	}
}

	/**
	 * Tests equivalency of two sims and their math comparison results
	 * <p>
	 * NOTE:
	 * math overrides are only influence the solution if they actually override something.
	 * <p>
	 * if maths are equal/equivalent and overridden parameters (where actual value != default value) are same
	 * then the MathDescriptions equality/equivalence is upheld.
	 * otherwise, they are always different
	 * <p>
	 * `if (!memorySimulation.getMathOverrides().compareEqualIgnoreDefaults(databaseSimulation.getMathOverrides()))...`
	 * now only non-defaults are stored in overrides...
	 * @param memorySimulation simulation in memory
	 * @param databaseSimulation simulation from db
	 * @param mathCompareResults the comparison results
	 * @return the equivlancy as a boolean
	 */
	public static boolean testEquivalency(Simulation memorySimulation, Simulation databaseSimulation, MathCompareResults mathCompareResults) {

	if (memorySimulation == databaseSimulation){
		return true;
	}
	return mathCompareResults.isEquivalent()
			&& memorySimulation.getSolverTaskDescription().compareEqual(databaseSimulation.getSolverTaskDescription())
			&& Compare.isEqualOrNull(memorySimulation.getMeshSpecification(),databaseSimulation.getMeshSpecification())
			&& memorySimulation.getMathOverrides().compareEquivalent(databaseSimulation.getMathOverrides());

}


public String toString() {
	String simStringFormat = "Simulation@%s(%s), %s";
	String hashString = Integer.toHexString(this.hashCode());
	if (this.getMathDescription() != null) return String.format(simStringFormat, hashString, this.getName(), "null");
	String mathStr = "Math@" + Integer.toHexString(this.getMathDescription().hashCode()) + "(" + this.getMathDescription().getName() + "," + getMathDescription().getKey() + ")";
	return String.format(simStringFormat, hashString, this.getName(), mathStr);
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
		return this.dataProcessingInstructions;
	}


	public void setDataProcessingInstructions(DataProcessingInstructions dataProcessingInstructions) {
		DataProcessingInstructions oldValue = this.dataProcessingInstructions;
		this.dataProcessingInstructions = dataProcessingInstructions;
		firePropertyChange("dataProcessingInstructions", oldValue, dataProcessingInstructions);
}

	public boolean isSerialParameterScan() {
        return this.getSolverTaskDescription().isSerialParameterScan()
				&& this.getScanCount() > 1;
    }
	public boolean isTimeoutDisabled() {
        return this.getSolverTaskDescription().isTimeoutDisabled();
    }
	public boolean isBorderExtrapolationDisabled() {
        return this.getSolverTaskDescription().isBorderExtrapolationDisabled();
    }

	public void propertyChange(PropertyChangeEvent evt) {
		MathDescription md = getMathDescription();
		boolean isNotMath = evt.getSource() != md;
		if(isNotMath || !evt.getPropertyName().equals("geometry")) return;
		try {
			this.refreshMeshSpec();
		} catch(PropertyVetoException e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void refreshMeshSpec() throws PropertyVetoException {
		// refresh MeshSpecification
		if (this.getMathDescription().getGeometry().getDimension() > 0){
			if (this.getMeshSpecification() != null){
				this.getMeshSpecification().setGeometry(getMathDescription().getGeometry());
			} else {
				this.setMeshSpecification(new MeshSpecification(getMathDescription().getGeometry()));
			}
		} else {
			this.setMeshSpecification(null);
		}
	}

	public boolean hasCellCenteredMesh(){
		SolverTaskDescription taskDescription;
		SolverDescription description;
		return (taskDescription = this.getSolverTaskDescription()) != null
				&& (description = taskDescription.getSolverDescription()) != null
				&& description.hasCellCenteredMesh();
	}

	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		MathOverrides mo = this.getMathOverrides();
		if( mo != null && mo.hasUnusedOverrides()) {
			String msg = "The Simulation '" + this.getName() + "' has unused Math Overrides.";
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
        this.importedTaskID = id;
	}

	public String getImportedTaskID() {
		return this.importedTaskID;
	}
	
	public static final String typeName = "Simulation";

	@Override
	public String getDisplayName() {
		return this.getName();
	}

	@Override
	public String getDisplayType() {
		return typeName;
	}
}
