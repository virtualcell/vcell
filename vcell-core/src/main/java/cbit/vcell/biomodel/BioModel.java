/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.biomodel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.model.rbm.MolecularType;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.PathwayModel;
import org.vcell.relationship.RelationshipModel;
import org.vcell.relationship.RelationshipObject;
import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.Matchable;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.Identifiable;
import org.vcell.util.document.PropertyConstants;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.Version;

import cbit.vcell.biomodel.meta.IdentifiableProvider;
import cbit.vcell.biomodel.meta.VCID;
import cbit.vcell.biomodel.meta.VCMetaData;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.model.BioModelEntityObject;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.model.VCellSbmlName;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (10/17/00 3:12:16 PM)
 * @author:
 */
@SuppressWarnings("serial")
public class BioModel implements VCDocument, Matchable, VetoableChangeListener, PropertyChangeListener,
Identifiable, IdentifiableProvider, IssueSource, Displayable, VCellSbmlName
{
	public static final String PROPERTY_NAME_SIMULATION_CONTEXTS = "simulationContexts";
	public final static String SIMULATION_CONTEXT_DISPLAY_NAME = "Application";
	public final static String SIMULATION_DISPLAY_NAME = "Simulation";
	private Version fieldVersion = null;
	private String fieldName = null;
	private String sbmlId = null;
	private String sbmlName = null;

	protected transient VetoableChangeSupport vetoPropertyChange;
	protected transient PropertyChangeSupport propertyChange;
	private Model fieldModel = null;
	private SimulationContext[] fieldSimulationContexts = new SimulationContext[0];
	private Simulation[] fieldSimulations = new Simulation[0];
	private String fieldDescription = new String();
	private VCMetaData vcMetaData = null;

	private final PathwayModel pathwayModel = new PathwayModel();
	private final RelationshipModel relationshipModel = new RelationshipModel();
	private static final Logger lg = LogManager.getLogger(BioModel.class);

	public BioModel(Version version, ModelUnitSystem modelUnitSystem){
		super();
		fieldName = new String("NoName");
		vcMetaData = new VCMetaData(this, null);
		setModel(new Model("unnamed",modelUnitSystem));
		addVetoableChangeListener(this);
		addPropertyChangeListener(this);
		try {
			setVersion(version);
		} catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
	/**
	 * BioModel constructor comment.
	 */
	public BioModel(Version version) {
		this(version, ModelUnitSystem.createDefaultVCModelUnitSystem());
	}

	/**
 * Insert the method's description here.
 * Creation date: (1/19/01 3:31:00 PM)
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 * @exception java.beans.PropertyVetoException The exception description.
 */
public SimulationContext addNewSimulationContext(String newSimulationContextName, SimulationContext.Application app) throws java.beans.PropertyVetoException {
	SimulationContext simContext = new SimulationContext(getModel(),new Geometry("non-spatial",0), null,null,app);
	simContext.setName(newSimulationContextName);
	addSimulationContext(simContext);
	return simContext;
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 3:31:00 PM)
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 * @exception java.beans.PropertyVetoException The exception description.
 */
public void addSimulation(Simulation simulation) throws java.beans.PropertyVetoException {
	if (contains(simulation)){
		throw new IllegalArgumentException("BioModel.addSimulation() simulation already present in BioModel");
	}
	if (getNumSimulations()==0){
		setSimulations(new Simulation[] { simulation });
	}else{
		setSimulations(BeanUtils.addElement(fieldSimulations,simulation));
	}

}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 3:31:00 PM)
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 * @exception java.beans.PropertyVetoException The exception description.
 */
public void addSimulationContext(SimulationContext simulationContext) throws java.beans.PropertyVetoException {
	if (contains(simulationContext)){
		throw new IllegalArgumentException("BioModel.addSimulationContext() simulationContext already present in BioModel");
	}
	if (getNumSimulationContexts()==0){
		setSimulationContexts(new SimulationContext[] { simulationContext });
	}else{
		setSimulationContexts(BeanUtils.addElement(fieldSimulationContexts,simulationContext));
	}
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * Insert the method's description here.
 * Creation date: (4/24/2003 3:39:06 PM)
 */
public void clearVersion(){
	fieldVersion = null;
}


/**
 * Insert the method's description here.
 * Creation date: (11/29/00 2:11:43 PM)
 * @return boolean
 * @param obj cbit.util.Matchable
 */
public boolean compareEqual(Matchable obj) {
	if (!(obj instanceof BioModel)){
		return false;
	}
	BioModel bioModel = (BioModel)obj;
	if (!Compare.isEqualOrNull(getName(),bioModel.getName())){
		return false;
	}
	if (!Compare.isEqualOrNull(getSbmlName(), bioModel.getSbmlName())) {
		return false;
	}
	if (!Compare.isEqualOrNull(getSbmlId(), bioModel.getSbmlId())) {
		return false;
	}
	if (!Compare.isEqualOrNull(getDescription(),bioModel.getDescription())){
		return false;
	}
	if (!getModel().compareEqual(bioModel.getModel())){
		return false;
	}
	if (!getPathwayModel().compare((HashSet<BioPaxObject>) bioModel.getPathwayModel().getBiopaxObjects())){
		return false;
	}
	if (!(getRelationshipModel()).compare((HashSet<RelationshipObject>) bioModel.getRelationshipModel().getRelationshipObjects(), bioModel)){
		return false;
	}
	if (!Compare.isEqualOrNull(getSimulationContexts(),bioModel.getSimulationContexts())){
		return false;
	}
	if (!Compare.isEqualOrNull(getSimulations(),bioModel.getSimulations())){
		return false;
	}
	if(!getVCMetaData().compareEquals(bioModel.getVCMetaData())){
		return false;
	}

	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/01 12:51:23 PM)
 * @return boolean
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 */
public boolean contains(SimulationContext simulationContext) {
	if (simulationContext == null){
		throw new IllegalArgumentException("simulationContext was null");
	}
	SimulationContext simContexts[] = getSimulationContexts();
	if (simContexts == null){
		return false;
	}
	boolean bFound = false;
	for (int i=0;i<simContexts.length;i++){
		if (simContexts[i] == simulationContext){
			bFound = true;
		}
	}
	return bFound;
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/01 12:51:23 PM)
 * @return boolean
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 */
public boolean contains(Simulation simulation) {
	if (simulation == null){
		throw new IllegalArgumentException("simulation was null");
	}
	Simulation sims[] = getSimulations();
	if (sims == null){
		return false;
	}
	boolean bFound = false;
	for (int i=0;i<sims.length;i++){
		if (sims[i] == simulation){
			bFound = true;
		}
	}
	return bFound;
}

public BioModelChildSummary createBioModelChildSummary() {

	SimulationContext[] simContexts = getSimulationContexts();

	String[] scNames = new String[simContexts.length];
	MathType[] appTypes = new MathType[simContexts.length];
	String[] scAnnots = new String[simContexts.length];
	String[] geoNames = new String[simContexts.length];
	int[] geoDims = new int[simContexts.length];
	String[][] simNames = new String[simContexts.length][];
	String[][] simAnnots = new String[simContexts.length][];

	for(int i=0;i<simContexts.length;i+= 1){
		scNames[i] = simContexts[i].getName();
		appTypes[i] = simContexts[i].getMathType();
		scAnnots[i]= simContexts[i].getDescription();
		geoNames[i] = simContexts[i].getGeometry().getName();
		geoDims[i] = simContexts[i].getGeometry().getDimension();

		Simulation[] sims = simContexts[i].getSimulations();
		simNames[i] = new String[sims.length];
		simAnnots[i] =  new String[sims.length];
		for(int j=0;j< sims.length;j+= 1){
			simNames[i][j] = sims[j].getName();
			simAnnots[i][j] = sims[j].getDescription();
		}
	}
	return new BioModelChildSummary(scNames,appTypes, scAnnots,simNames,simAnnots,geoNames,geoDims);
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
public void forceNewVersionAnnotation(Version newVersion) throws PropertyVetoException {
	if (getVersion().getVersionKey().equals(newVersion.getVersionKey())) {
		setVersion(newVersion);
	} else {
		throw new RuntimeException("biomodel.forceNewVersionAnnotation failed : version keys not equal");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2004 10:38:12 PM)
 * @param issueList java.util.Vector
 */
@Override
public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
	long start_time = 0;
	if (lg.isInfoEnabled()) {
		start_time = System.currentTimeMillis();
	}
	issueContext = issueContext.newChildContext(ContextType.BioModel, this);
	getModel().gatherIssues(issueContext, issueList);
	for (SimulationContext simulationContext : fieldSimulationContexts) {
		boolean bIgnoreMathDescription = false;
		simulationContext.gatherIssues(issueContext, issueList, bIgnoreMathDescription);
	}
	if(sbmlName != null && sbmlName.isEmpty()) {
		String message = "SbmlName cannot be an empty string.";
		issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
	}
	if(sbmlId != null && sbmlId.isEmpty()) {
		String message = "SbmlId cannot be an empty string.";
		issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, message, Issue.Severity.ERROR));
	}
	if (lg.isInfoEnabled()) {
		long end_time = System.currentTimeMillis();
//		lg.info("Time spent on Issue detection: " + (end_time - start_time));
	}
//	for (Simulation simulation : fieldSimulations) {
//		simulation.gatherIssues(issueContext,issueList);
//	}
}


/**
 * Gets the description property (java.lang.String) value.
 * @return The description property value.
 * @see #setDescription
 */
@Deprecated
public java.lang.String getDescription() {
	return fieldDescription;
}


/**
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:13:04 PM)
 * @return int
 */
public VCDocumentType getDocumentType() {
	return VCDocumentType.BIOMODEL_DOC;
}


/**
 * Gets the model property (cbit.vcell.model.Model) value.
 * @return The model property value.
 * @see #setModel
 */
public cbit.vcell.model.Model getModel() {
	return fieldModel;
}


/**
 * Gets the name property (java.lang.String) value.
 * @return The name property value.
 * @see #setName
 */
public java.lang.String getName() {
	return fieldName;
}
public String getSbmlId() {
	return sbmlId;
}
public String getSbmlName() {
	return sbmlName;
}

/**
 * Insert the method's description here.
 * Creation date: (11/29/00 2:15:36 PM)
 * @return int
 */
public int getNumSimulationContexts() {
	if (getSimulationContexts()==null){
		return 0;
	}
	return getSimulationContexts().length;
}


/**
 * Insert the method's description here.
 * Creation date: (11/29/00 2:15:36 PM)
 * @return int
 */
public int getNumSimulations() {
	if (getSimulations()==null){
		return 0;
	}
	return getSimulations().length;
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
 * Creation date: (1/17/01 12:59:40 PM)
 * @return cbit.vcell.solver.Simulation[]
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 */
public SimulationContext getSimulationContext(Simulation simulation) throws ObjectNotFoundException {
	if (simulation == null){
		throw new IllegalArgumentException("simulation was null");
	}
	if (!contains(simulation)){
		throw new IllegalArgumentException("simulation doesn't belong to this BioModel");
	}
	SimulationContext simContexts[] = getSimulationContexts();
	if (simContexts == null){
		return null;
	}
	for (int i=0;i<simContexts.length;i++){
		if (simContexts[i].getMathDescription() == simulation.getMathDescription()){
			return simContexts[i];
		}
	}
	throw new ObjectNotFoundException("could not find Application for simulation "+simulation.getName());
}


/**
 * Gets the simulationContexts property (cbit.vcell.mapping.SimulationContext[]) value.
 * @return The simulationContexts property value.
 * @see #setSimulationContexts
 */
public SimulationContext[] getSimulationContexts() {
	return fieldSimulationContexts;
}


/**
 * Gets the simulationContexts index property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContexts property value.
 * @param index The index value into the property array.
 * @see #setSimulationContexts
 */
public SimulationContext getSimulationContext(int index) {
	return getSimulationContexts()[index];
}

public SimulationContext getSimulationContexts(String name) {
	for (SimulationContext simContext : fieldSimulationContexts){
		if (simContext.getName().equals(name)){
			return simContext;
		}
	}
	return null;
}


/**
 * Gets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @return The simulations property value.
 * @see #setSimulations
 */
public Simulation[] getSimulations() {
	return fieldSimulations;
}


/**
 * Gets the simulations index property (cbit.vcell.solver.Simulation) value.
 * @return The simulations property value.
 * @param index The index value into the property array.
 * @see #setSimulations
 */
public Simulation getSimulation(int index) {
	return getSimulations()[index];
}


/**
 * Insert the method's description here.
 * Creation date: (1/17/01 12:59:40 PM)
 * @return cbit.vcell.solver.Simulation[]
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 */
public Simulation[] getSimulations(SimulationContext simulationContext) {
	if (simulationContext == null){
		throw new IllegalArgumentException("simulationContext was null");
	}
	if (!contains(simulationContext)){
		throw new IllegalArgumentException("simulationContext doesn't belong to this BioModel");
	}
	Simulation sims[] = getSimulations();
	if (sims == null){
		return null;
	}
	Vector<Simulation> scSimList = new Vector<Simulation>();
	for (int i=0;i<sims.length;i++){
		if (sims[i].getMathDescription() == simulationContext.getMathDescription()){
			scSimList.addElement(sims[i]);
		}
	}
	Simulation[] scSimArray = new Simulation[scSimList.size()];
	scSimList.copyInto(scSimArray);
	return scSimArray;
}


/**
 * Gets the version property (cbit.sql.Version) value.
 * @return The version property value.
 */
public Version getVersion() {
	return fieldVersion;
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
	 * This method gets called when a bound property is changed.
	 * @param evt A PropertyChangeEvent object describing the event source
	 *   	and the property that has changed.
	 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {

	//
	// propagate mathDescription changes from SimulationContexts to Simulations
	//
	if (evt.getSource() instanceof SimulationContext && evt.getPropertyName().equals("mathDescription") && evt.getNewValue()!=null){
		if (fieldSimulations!=null){
			for (int i=0;i<fieldSimulations.length;i++){
				if (fieldSimulations[i].getMathDescription() == evt.getOldValue()){
					try {
						fieldSimulations[i].setMathDescription((MathDescription)evt.getNewValue());
					}catch (PropertyVetoException e){
						System.out.println("error propagating math from SimulationContext '"+((SimulationContext)evt.getSource()).getName()+"' to Simulation '"+fieldSimulations[i].getName());
						e.printStackTrace(System.out);
					}
				}
			}
		}
	}

	//
	// make sure that simulations and simulationContexts are listened to
	//
	if (evt.getSource() == this && evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_SIMULATIONS) && evt.getNewValue()!=null){
		//
		// unregister for old
		//
		if (evt.getOldValue()!=null){
			Simulation simulations[] = (Simulation[])evt.getOldValue();
			for (int i=0;i<simulations.length;i++){
				simulations[i].removeVetoableChangeListener(this);
				simulations[i].removePropertyChangeListener(this);
			}
		}
		//
		// register for new
		//
		if (evt.getOldValue()!=null){
			Simulation simulations[] = (Simulation[])evt.getNewValue();
			for (int i=0;i<simulations.length;i++){
				simulations[i].addVetoableChangeListener(this);
				simulations[i].addPropertyChangeListener(this);
			}
		}
	}
	if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_CONTEXTS) && evt.getNewValue()!=null){
		//
		// unregister for old
		//
		if (evt.getOldValue()!=null){
			SimulationContext simulationContexts[] = (SimulationContext[])evt.getOldValue();
			for (int i=0;i<simulationContexts.length;i++){
				simulationContexts[i].removeVetoableChangeListener(this);
				simulationContexts[i].removePropertyChangeListener(this);
			}
		}
		//
		// register for new
		//
		if (evt.getOldValue()!=null){
			SimulationContext simulationContexts[] = (SimulationContext[])evt.getNewValue();
			for (int i=0;i<simulationContexts.length;i++){
				simulationContexts[i].addVetoableChangeListener(this);
				simulationContexts[i].addPropertyChangeListener(this);
			}
		}
	}

	if (evt.getSource() == fieldModel && (evt.getPropertyName().equals(Model.PROPERTY_NAME_SPECIES_CONTEXTS)
			|| evt.getPropertyName().equals(Model.PROPERTY_NAME_REACTION_STEPS))){
		//remove the relationship objects if the biomodelEntity objects were removed
		Set<BioModelEntityObject> removedObjects = relationshipModel.getBioModelEntityObjects();
		for(SpeciesContext sc : fieldModel.getSpeciesContexts()){
			removedObjects.remove(sc);
		}
		for(ReactionStep rs : fieldModel.getReactionSteps()){
			removedObjects.remove(rs);
		}
		for(MolecularType mt : fieldModel.getRbmModelContainer().getMolecularTypeList()) {
			removedObjects.remove(mt);
		}
		for(ReactionRule rr : fieldModel.getRbmModelContainer().getReactionRuleList()) {
			removedObjects.remove(rr);
		}
		relationshipModel.removeRelationshipObjects(removedObjects);
	}
	// adjust the relationship model when a molecule gets deleted
	if (evt.getSource() == fieldModel && (evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_MOLECULAR_TYPE_LIST))) {
		@SuppressWarnings("unchecked")
		List<MolecularType> oldListCopy = new ArrayList<MolecularType>((List<MolecularType>)evt.getOldValue());
		@SuppressWarnings("unchecked")
		List<MolecularType> newList = (List<MolecularType>)evt.getNewValue();
		if(newList != null && oldListCopy != null && oldListCopy.size() > newList.size()) {
			// something got deleted
			oldListCopy.removeAll(newList);
			for(MolecularType removedMt : oldListCopy) {
				relationshipModel.removeRelationshipObject(removedMt);
			}
		}
	}
	if (evt.getSource() == fieldModel && (evt.getPropertyName().equals(RbmModelContainer.PROPERTY_NAME_REACTION_RULE_LIST))) {
		@SuppressWarnings("unchecked")
		List<ReactionRule> oldListCopy = new ArrayList<ReactionRule>((List<ReactionRule>)evt.getOldValue());
		@SuppressWarnings("unchecked")
		List<ReactionRule> newList = (List<ReactionRule>)evt.getNewValue();
		if(newList != null && oldListCopy != null && oldListCopy.size() > newList.size()) {
			// something got deleted
			oldListCopy.removeAll(newList);
			for(ReactionRule removedRr : oldListCopy) {
				relationshipModel.removeRelationshipObject(removedRr);
			}
		}
	}
	if (evt.getSource() == fieldModel && (evt.getPropertyName().equals(Model.PROPERTY_NAME_MODEL_ENTITY_NAME))) {
		firePropertyChange(Model.PROPERTY_NAME_MODEL_ENTITY_NAME, evt.getOldValue(), evt.getNewValue());
	}

}


/**
 * Insert the method's description here.
 * Creation date: (4/12/01 11:24:12 AM)
 */
public void refreshDependencies() {
	//
	// listen to self
	//
	removePropertyChangeListener(this);
	removeVetoableChangeListener(this);
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);

	fieldModel.refreshDependencies();
	fieldModel.setVcMetaData(getVCMetaData());
	for (int i=0;fieldSimulationContexts!=null && i<fieldSimulationContexts.length;i++){
		fieldSimulationContexts[i].setBioModel(this);
		fieldSimulationContexts[i].removePropertyChangeListener(this);
		fieldSimulationContexts[i].removeVetoableChangeListener(this);
		fieldSimulationContexts[i].addPropertyChangeListener(this);
		fieldSimulationContexts[i].addVetoableChangeListener(this);
		fieldSimulationContexts[i].refreshDependencies();
	}
	for (int i=0;fieldSimulations!=null && i<fieldSimulations.length;i++){
		fieldSimulations[i].removePropertyChangeListener(this);
		fieldSimulations[i].removeVetoableChangeListener(this);
		fieldSimulations[i].addPropertyChangeListener(this);
		fieldSimulations[i].addVetoableChangeListener(this);
		fieldSimulations[i].refreshDependencies();
	}
	updateSimulationOwners();
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 3:31:00 PM)
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 * @exception java.beans.PropertyVetoException The exception description.
 */
public void removeSimulation(Simulation simulation) throws java.beans.PropertyVetoException {
	if (!contains(simulation)){
		throw new IllegalArgumentException("BioModel.removeSimulation() simulation not present in BioModel");
	}
	setSimulations(BeanUtils.removeElement(fieldSimulations,simulation));
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 3:31:00 PM)
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 * @exception java.beans.PropertyVetoException The exception description.
 */
public void removeSimulationContext(SimulationContext simulationContext) throws java.beans.PropertyVetoException {
	if (!contains(simulationContext)){
		throw new IllegalArgumentException("BioModel.removeSimulationContext() simulationContext not present in BioModel");
	}
	setSimulationContexts(BeanUtils.removeElement(fieldSimulationContexts,simulationContext));
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
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getDescription
 */
@Deprecated
public void setDescription(java.lang.String description) throws java.beans.PropertyVetoException {
	String oldValue = fieldDescription;
	fireVetoableChange("description", oldValue, description);
	fieldDescription = description;
	firePropertyChange("description", oldValue, description);
}


/**
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(Model model) {
	Model oldValue = fieldModel;
	fieldModel = model;
	if (oldValue != null){
		oldValue.removePropertyChangeListener(this);
	}
	if (model!=null){
		model.setVcMetaData(getVCMetaData());
		model.addPropertyChangeListener(this);
	}
	firePropertyChange("model", oldValue, model);
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
public void setSbmlName(String newString) throws PropertyVetoException {
	String oldValue = this.sbmlName;
	String newValue = SpeciesContext.fixSbmlName(newString);
	
	fireVetoableChange("sbmlName", oldValue, newValue);
	this.sbmlName = newValue;
	firePropertyChange("sbmlName", oldValue, newValue);
}
public void setSbmlId(String newString) throws PropertyVetoException {	// setable only through SBML import
	this.sbmlId = newString;
}

/**
 * Sets the simulationContexts property (cbit.vcell.mapping.SimulationContext[]) value.
 * @param simulationContexts The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulationContexts
 */
public void setSimulationContexts(SimulationContext[] simulationContexts) throws java.beans.PropertyVetoException {
	SimulationContext[] oldValue = fieldSimulationContexts;
	fireVetoableChange(PROPERTY_NAME_SIMULATION_CONTEXTS, oldValue, simulationContexts);
	for (int i = 0; oldValue!=null && i < oldValue.length; i++){
//		oldValue[i].removePropertyChangeListener(this);
//		oldValue[i].removeVetoableChangeListener(this);
		oldValue[i].setBioModel(null);
	}
	fieldSimulationContexts = simulationContexts;
	for (int i = 0; simulationContexts!=null && i < simulationContexts.length; i++){
//This is done in PropertyChange, not needed here and causes duplication in PropertyChange listener list
//		simulationContexts[i].addPropertyChangeListener(this);
//		simulationContexts[i].addVetoableChangeListener(this);
		simulationContexts[i].setBioModel(this);
	}
	updateSimulationOwners();
	firePropertyChange(PROPERTY_NAME_SIMULATION_CONTEXTS, oldValue, simulationContexts);
}


/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulations
 */
public void setSimulations(Simulation[] simulations) throws java.beans.PropertyVetoException {
	Simulation[] oldValue = fieldSimulations;
	fireVetoableChange(PropertyConstants.PROPERTY_NAME_SIMULATIONS, oldValue, simulations);
	for (int i = 0; oldValue!=null && i < oldValue.length; i++){
		oldValue[i].removePropertyChangeListener(this);
		oldValue[i].removeVetoableChangeListener(this);
		oldValue[i].setSimulationOwner(null); // make sure old simulation instances have null simulationOwners
	}
	fieldSimulations = simulations;
	for (int i = 0; simulations!=null && i < simulations.length; i++){
		simulations[i].addPropertyChangeListener(this);
		simulations[i].addVetoableChangeListener(this);
	}
	updateSimulationOwners();
	firePropertyChange(PropertyConstants.PROPERTY_NAME_SIMULATIONS, oldValue, simulations);
}

private void updateSimulationOwners(){
	for (Simulation sim : fieldSimulations){
		try {
			sim.setSimulationOwner(getSimulationContext(sim));
		} catch (ObjectNotFoundException e) {
			sim.setSimulationOwner(null);
			e.printStackTrace();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 3:49:12 PM)
 * @param version cbit.sql.Version
 */
private void setVersion(Version version) throws PropertyVetoException {
	this.fieldVersion = version;
	if (version != null){
		setName(version.getName());
		setDescription(version.getAnnot());
	}
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
@Override
public String toString() {
	String desc = (getVersion()==null)?getName():getVersion().toString();
	return "BioModel@"+Integer.toHexString(hashCode())+"("+desc+")";
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
	//
	// don't let SimulationContext's MathDescription be set to null if any Simulations depend on it, can't recover from this
	//
	if (evt.getSource() instanceof SimulationContext && evt.getPropertyName().equals("mathDescription") && evt.getNewValue()==null){
		if (fieldSimulations!=null){
			for (int i=0;i<fieldSimulations.length;i++){
				if (fieldSimulations[i].getMathDescription() == evt.getOldValue()){
					throw new PropertyVetoException("error: simulation "+fieldSimulations[i].getName()+" will be orphaned, MathDescription set to null for Application "+((SimulationContext)evt.getSource()).getName(),evt);
				}
			}
		}
	}
	//
	// don't let a Simulation's MathDescription be set to null, can't recover from this
	//
	if (evt.getSource() instanceof Simulation && evt.getPropertyName().equals("mathDescription") && evt.getNewValue()==null){
		throw new PropertyVetoException("error: Simulation "+((Simulation)evt.getSource()).getName()+" will be orphaned (MathDescription set to null)",evt);
	}
	//
	// don't let a Simulation change it's MathDescription unless that MathDescription is from an Application.
	// note that SimulationContext's MathDescription is changed first, then Simulation's MathDescription is updated
	// this is ALWAYS the order of events.
	//
	if (evt.getSource() instanceof Simulation && evt.getPropertyName().equals("mathDescription")){
		MathDescription newMathDescription = (MathDescription)evt.getNewValue();
		if (fieldSimulationContexts!=null){
			boolean bMathFound = false;
			for (int i=0;i<fieldSimulationContexts.length;i++){
				if (fieldSimulationContexts[i].getMathDescription() == newMathDescription){
					bMathFound = true;
				}
			}
			if (!bMathFound){
				throw new PropertyVetoException("error: simulation "+((Simulation)evt.getSource()).getName()+" will be orphaned (MathDescription doesn't belong to any Application",evt);
			}
		}
	}
	if (evt.getSource() == this && evt.getPropertyName().equals(PropertyConstants.PROPERTY_NAME_SIMULATIONS) && evt.getNewValue()!=null){
		//
		// check for name duplication
		//
		Simulation simulations[] = (Simulation[])evt.getNewValue();
		for (int i=0;i<simulations.length-1;i++){
			for (int j=i+1;j<simulations.length;j++){
				if (simulations[i].getName().equals(simulations[j].getName())){
					throw new PropertyVetoException(VCellNames.getName(simulations[i])+" with name "+simulations[i].getName()+" already exists",evt);
				}
			}
		}
		//
		// check for Simulations that don't map to any SimulationContext
		//
		for (int i=0;simulations!=null && i<simulations.length;i++){
			boolean bFound = false;
			for (int j=0;fieldSimulationContexts!=null && j<fieldSimulationContexts.length;j++){
				if (simulations[i].getMathDescription() == fieldSimulationContexts[j].getMathDescription()){
					bFound = true;
				}
			}
			if (!bFound){
				throw new PropertyVetoException("Setting Simulations, Simulation \""+simulations[i].getName()+"\" has no corresponding MathDescription (so no Application)",evt);
			}
		}
	}
	if (evt.getSource() instanceof Simulation && evt.getPropertyName().equals("name") && evt.getNewValue()!=null){
		//
		// check for name duplication
		//
		String simulationName = (String)evt.getNewValue();
		for (int i=0;i<fieldSimulations.length;i++){
			if (fieldSimulations[i].getName().equals(simulationName)){
				throw new PropertyVetoException(VCellNames.getName(fieldSimulations[i])+" with name "+simulationName+" already exists",evt);
			}
		}
	}
	if (evt.getSource() == this && evt.getPropertyName().equals(PROPERTY_NAME_SIMULATION_CONTEXTS) && evt.getNewValue()!=null){
		//
		// check for name duplication
		//
		SimulationContext simulationContexts[] = (SimulationContext[])evt.getNewValue();
		for (int i=0;i<simulationContexts.length-1;i++){
			for (int j=i+1;j<simulationContexts.length;j++){
				if (simulationContexts[i].getName().equals(simulationContexts[j].getName())){
					throw new PropertyVetoException(VCellNames.getName(simulationContexts[i])+" with name "+simulationContexts[i].getName()+" already exists",evt);
				}
			}
		}
		//
		// check for Simulations that don't map to any SimulationContext
		//
		for (int i=0;fieldSimulations!=null && i<fieldSimulations.length;i++){
			boolean bFound = false;
			for (int j=0;simulationContexts!=null && j<simulationContexts.length;j++){
				if (fieldSimulations[i].getMathDescription() == simulationContexts[j].getMathDescription()){
					bFound = true;
				}
			}
			if (!bFound){
				throw new PropertyVetoException("Setting SimulationContexts, Simulation \""+fieldSimulations[i].getName()+"\" has no corresponding MathDescription (so no Application)",evt);
			}
		}
	} else if(evt.getSource() == this && evt.getPropertyName().equals("sbmlId")) {
		;		// not editable, we use what we get from the importer
	} else if(evt.getSource() == this && evt.getPropertyName().equals("sbmlName")) {
		String newName = (String)evt.getNewValue();
		if(newName == null) {
			return;
		}
	}
	if (evt.getSource() instanceof SimulationContext && evt.getPropertyName().equals("name") && evt.getNewValue()!=null){
		//
		// check for name duplication
		//
		String simulationContextName = (String)evt.getNewValue();
		for (int i=0;i<fieldSimulationContexts.length;i++){
			if (fieldSimulationContexts[i].getName().equals(simulationContextName)){
				throw new PropertyVetoException(VCellNames.getName(fieldSimulationContexts[i])+" with name "+simulationContextName+" already exists",evt);
			}
		}
	}
	TokenMangler.checkNameProperty(this, "BioModel", evt);
}

public VCMetaData getVCMetaData() {
	return vcMetaData;
}

public void setVCMetaData(VCMetaData vcMetaData) {
	this.vcMetaData = vcMetaData;
}

public void populateVCMetadata(boolean bMetadataPopulated) {
	// (recursively) populate the identifiables with free text annotations if 'bMetaDataPopulated' is false
	if (!bMetadataPopulated) {
		String annotationText = this.getDescription();
		vcMetaData.setFreeTextAnnotation(this, annotationText);

		// now populate from model downwards
		if  (fieldModel != null) {
			fieldModel.populateVCMetadata(bMetadataPopulated);
		}
	}
}

public Identifiable getIdentifiableObject(VCID vcid) {
	if (vcid.getClassName().equals(VCID.CLASS_BIOPAX_OBJECT)) {
		String rdfId = vcid.getLocalName();
		return getPathwayModel().findBioPaxObject(rdfId);
	}
	if (vcid.getClassName().equals(VCID.CLASS_SPECIES_CONTEXT)) {
		String localName = vcid.getLocalName();
		return getModel().getSpeciesContext(localName);
	}
	if (vcid.getClassName().equals(VCID.CLASS_SPECIES)) {
		String localName = vcid.getLocalName();
		return getModel().getSpecies(localName);
	}
	if (vcid.getClassName().equals(VCID.CLASS_STRUCTURE)) {
		String localName = vcid.getLocalName();
		return getModel().getStructure(localName);
	}
	if (vcid.getClassName().equals(VCID.CLASS_REACTION_STEP)) {
		String localName = vcid.getLocalName();
		return getModel().getReactionStep(localName);
	}
	if (vcid.getClassName().equals(VCID.CLASS_MOLECULE)) {
		String localName = vcid.getLocalName();
		return getModel().getRbmModelContainer().getMolecularType(localName);
	}
	if (vcid.getClassName().equals(VCID.CLASS_REACTION_RULE)) {
		String localName = vcid.getLocalName();
		return getModel().getRbmModelContainer().getReactionRule(localName);
	}
	if (vcid.getClassName().equals(VCID.CLASS_OBSERVABLE)) {
		String localName = vcid.getLocalName();
		return getModel().getRbmModelContainer().getObservable(localName);
	}
	if (vcid.getClassName().equals(VCID.CLASS_MODEL_PARAMETER)) {
		String localName = vcid.getLocalName();
		return getModel().getModelParameter(localName);
	}
	if (vcid.getClassName().equals(VCID.CLASS_APPLICATION)) {
		String localName = vcid.getLocalName();
		return getSimulationContexts(localName);
	}
	if (vcid.getClassName().equals(VCID.CLASS_BIOMODEL)) {
		return this;
	}
	return null;
}

public VCID getVCID(Identifiable identifiable) {
	if(identifiable == null) {
		return null;
	}
	String localName;
	String className;
	if (identifiable instanceof SpeciesContext) {
		localName = ((SpeciesContext)identifiable).getName();
		className = VCID.CLASS_SPECIES_CONTEXT;
	} else if (identifiable instanceof Species) {
		localName = ((Species)identifiable).getCommonName();
		className = VCID.CLASS_SPECIES;
	} else if (identifiable instanceof Structure) {
		localName = ((Structure)identifiable).getName();
		className = VCID.CLASS_STRUCTURE;
	} else if (identifiable instanceof ReactionStep) {
		localName = ((ReactionStep)identifiable).getName();
		className = VCID.CLASS_REACTION_STEP;
	} else if (identifiable instanceof BioModel){
		localName = ((BioModel)identifiable).getName();
		className = VCID.CLASS_BIOMODEL;
	} else if (identifiable instanceof BioPaxObject) {
		localName = ((BioPaxObject)identifiable).getID();
		className = VCID.CLASS_BIOPAX_OBJECT;
	} else if (identifiable instanceof MolecularType) {
		localName = ((MolecularType)identifiable).getName();
		className = VCID.CLASS_MOLECULE;
	} else if (identifiable instanceof ReactionRule) {
		localName = ((ReactionRule)identifiable).getName();
		className = VCID.CLASS_REACTION_RULE;
	} else if (identifiable instanceof RbmObservable) {
		localName = ((RbmObservable)identifiable).getName();
		className = VCID.CLASS_OBSERVABLE;
	} else if (identifiable instanceof Model.ModelParameter) {
		localName = ((Model.ModelParameter)identifiable).getName();
		className = VCID.CLASS_MODEL_PARAMETER;
	} else if (identifiable instanceof SimulationContext) {
		localName = ((SimulationContext)identifiable).getName();
		className = VCID.CLASS_APPLICATION;
	} else {
		throw new RuntimeException("unsupported Identifiable class");
	}
	localName = TokenMangler.mangleVCId(localName);
	VCID vcid;
	try {
		vcid = VCID.fromString(className+"("+localName+")");
	} catch (VCID.InvalidVCIDException e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
	return vcid;
}

public Set<Identifiable> getAllIdentifiables() {
	HashSet<Identifiable> allIdenfiables = new HashSet<Identifiable>();
	allIdenfiables.addAll(Arrays.asList(fieldModel.getSpecies()));
	allIdenfiables.addAll(Arrays.asList(fieldModel.getStructures()));
	allIdenfiables.addAll(Arrays.asList(fieldModel.getReactionSteps()));
//	allIdenfiables.addAll(Arrays.asList(fieldSimulationContexts));
	Set<BioPaxObject> biopaxObjects = getPathwayModel().getBiopaxObjects();
	allIdenfiables.addAll(biopaxObjects);

	allIdenfiables.addAll(fieldModel.getRbmModelContainer().getMolecularTypeList());
	allIdenfiables.addAll(fieldModel.getRbmModelContainer().getReactionRuleList());
	allIdenfiables.addAll(fieldModel.getRbmModelContainer().getObservableList());
	allIdenfiables.add(this);
	return allIdenfiables;
}

public String getFreeSimulationContextName() {
	int count=0;
	while (true) {
		String name = SIMULATION_CONTEXT_DISPLAY_NAME + count;
		if (getSimulationContext(name) == null){
			return name;
		}
		count++;
	}
}

public String getFreeSimulationName() {
	int count=0;
	while (true) {
		String name = SIMULATION_DISPLAY_NAME + count;
		if (getSimulation(name) == null){
			return name;
		}
		count++;
	}
}

private Simulation getSimulation(String name) {
	for (Simulation simulation : fieldSimulations){
		if (simulation.getName().equals(name)) {
			return simulation;
		}
	}
	return null;
}

public SimulationContext getSimulationContext(String name) {
	for (SimulationContext simulationContext : fieldSimulationContexts){
		if (simulationContext.getName().equals(name)) {
			return simulationContext;
		}
	}
	return null;
}

public List<SymbolTableEntry> findReferences(SymbolTableEntry symbolTableEntry){
	ArrayList<SymbolTableEntry> references = new ArrayList<SymbolTableEntry>();
	HashSet<NameScope> visited = new HashSet<NameScope>();

	fieldModel.getNameScope().findReferences(symbolTableEntry, references, visited);

	for (SimulationContext simContext : fieldSimulationContexts){
		simContext.getNameScope().findReferences(symbolTableEntry, references, visited);
	}
	return references;
}

// ------------------------------------------------- Pathway and Relationship objects and utility functions
public PathwayModel getPathwayModel() {
	return pathwayModel;
}
public RelationshipModel getRelationshipModel(){
	return relationshipModel;
}

public static void printObject(BioPaxObject bpo) {
}
private static void printObject(RelationshipObject ro) {
}
// displays on console the bioPaxObjects in the pathway model
public static void printBpModelObjects(Set<BioPaxObject> bpoList) {
	System.out.println(bpoList.size() + " bioPaxObjects in the pathway model");
	for(BioPaxObject bpo : bpoList) {
		printObject(bpo);
	}
}
// displays on console the bioPaxObjects that are part of a relationship
public static void printBpRelationshipObjects(Set<BioPaxObject> bpoList) {
	System.out.println(bpoList.size() + " bioPaxObjects that are part of a relationship");
	for(BioPaxObject bpo : bpoList) {
		printObject(bpo);
	}
}
// displays on console the relationship objects existing in the relationship model
public static void printRelationships(Set<RelationshipObject> roList) {
	System.out.println(roList.size() + " relationshipObjects in the relationship model");
	for(RelationshipObject ro : roList) {
		printObject(ro);
	}
}

public static final String typeName = "BioModel";
@Override
public String getDisplayName() {
	return getName();
}
@Override
public String getDisplayType() {
	return typeName;
}

}
