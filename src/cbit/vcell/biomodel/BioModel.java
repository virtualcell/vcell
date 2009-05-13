package cbit.vcell.biomodel;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.geometry.Geometry;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.KineticsDescription;
import cbit.vcell.model.Model;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.ReservedSymbol;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.math.MathDescription;
import java.beans.PropertyVetoException;
import java.util.Vector;

import org.jdom.Element;
import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.Version;

import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.stoch.FluxSolver;
import cbit.vcell.solver.stoch.MassActionSolver;
import cbit.vcell.xml.MIRIAMAnnotatable;
import cbit.vcell.xml.MIRIAMAnnotation;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.gui.VCellNames;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (10/17/00 3:12:16 PM)
 * @author: 
 */
public class BioModel
	implements
		org.vcell.util.document.VCDocument, org.vcell.util.Matchable, java.beans.VetoableChangeListener, java.beans.PropertyChangeListener,
		MIRIAMAnnotatable
{
	private org.vcell.util.document.Version fieldVersion = null;
	private java.lang.String fieldName = new String("NoName");
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.model.Model fieldModel = new cbit.vcell.model.Model("unnamed");
	private cbit.vcell.mapping.SimulationContext[] fieldSimulationContexts = new cbit.vcell.mapping.SimulationContext[0];
	private cbit.vcell.solver.Simulation[] fieldSimulations = new cbit.vcell.solver.Simulation[0];
	private java.lang.String fieldDescription = new String();
	private java.util.Hashtable simSimContextHash = new java.util.Hashtable();
	private MIRIAMAnnotation miriamAnnotation;

/**
 * BioModel constructor comment.
 */
public BioModel(Version version) {
	super();
	addVetoableChangeListener(this);
	addPropertyChangeListener(this);
	try {
		setVersion(version);
	} catch (PropertyVetoException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}

public MIRIAMAnnotation getMIRIAMAnnotation() {
	return miriamAnnotation;
}
public void setMIRIAMAnnotation(MIRIAMAnnotation miriamAnnotation) {
	this.miriamAnnotation = miriamAnnotation;
	
}


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 3:31:00 PM)
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 * @exception java.beans.PropertyVetoException The exception description.
 */
public SimulationContext addNewSimulationContext(String newSimulationContextName, boolean bStoch ) throws java.beans.PropertyVetoException {
	SimulationContext simContext = new SimulationContext(getModel(),new Geometry("non-spatial",0), bStoch);
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
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(propertyName, listener);
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
		setSimulations((Simulation[])BeanUtils.addElement(fieldSimulations,simulation));
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
		setSimulationContexts((SimulationContext[])BeanUtils.addElement(fieldSimulationContexts,simulationContext));
	}
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(listener);
}


/**
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
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
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (!(obj instanceof BioModel)){
		return false;
	}
	BioModel bioModel = (BioModel)obj;
	if (!org.vcell.util.Compare.isEqualOrNull(getName(),bioModel.getName())){
		return false;
	}
	if (!org.vcell.util.Compare.isEqualOrNull(getDescription(),bioModel.getDescription())){
		return false;
	}
	if (!getModel().compareEqual(bioModel.getModel())){
		return false;
	}
	if (!org.vcell.util.Compare.isEqualOrNull(getSimulationContexts(),bioModel.getSimulationContexts())){
		return false;
	}
	if (!org.vcell.util.Compare.isEqualOrNull(getSimulations(),bioModel.getSimulations())){
		return false;
	}
	if(!Compare.isEqualOrNull(getMIRIAMAnnotation(), bioModel.getMIRIAMAnnotation())){
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
public boolean contains(cbit.vcell.mapping.SimulationContext simulationContext) {
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


/**
 * Insert the method's description here.
 * Creation date: (1/19/01 3:31:00 PM)
 * @param simulationContext cbit.vcell.mapping.SimulationContext
 * @exception java.beans.PropertyVetoException The exception description.
 */
public SimulationContext copySimulationContext(SimulationContext simulationContext, String newSimulationContextName, boolean isStoch) throws java.beans.PropertyVetoException {
	//if stoch copy to ode, we need to check is stoch is using particles. If yes, should convert particles to concentraton.
	//the other 3 cases are fine. ode->ode, ode->stoch, stoch-> stoch 
	SimulationContext simContext = new SimulationContext(simulationContext, isStoch);
	if(simulationContext.isStoch() && !simulationContext.isUsingConcentration() && !isStoch)
	{
		try {
			simContext.convertSpeciesIniCondition(true);
		} catch (MappingException e) {
			e.printStackTrace();
			throw new java.beans.PropertyVetoException(e.getMessage(), null);
		}
	}
	simContext.setName(newSimulationContextName);
	addSimulationContext(simContext);
	return simContext;
}

public BioModelChildSummary createBioModelChildSummary() {

	SimulationContext[] simContexts = getSimulationContexts();
	
	String[] scNames = new String[simContexts.length];
	String[] appTypes = new String[simContexts.length];
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
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
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
public void gatherIssues(Vector issueList) {
	getModel().gatherIssues(issueList);
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
 * Insert the method's description here.
 * Creation date: (5/28/2004 3:13:04 PM)
 * @return int
 */
public int getDocumentType() {
	return BIOMODEL_DOC;
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
public cbit.vcell.mapping.SimulationContext[] getSimulationContexts() {
	return fieldSimulationContexts;
}


/**
 * Gets the simulationContexts index property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContexts property value.
 * @param index The index value into the property array.
 * @see #setSimulationContexts
 */
public cbit.vcell.mapping.SimulationContext getSimulationContexts(int index) {
	return getSimulationContexts()[index];
}


/**
 * Gets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @return The simulations property value.
 * @see #setSimulations
 */
public cbit.vcell.solver.Simulation[] getSimulations() {
	return fieldSimulations;
}


/**
 * Gets the simulations index property (cbit.vcell.solver.Simulation) value.
 * @return The simulations property value.
 * @param index The index value into the property array.
 * @see #setSimulations
 */
public cbit.vcell.solver.Simulation getSimulations(int index) {
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
	Vector scSimList = new Vector();
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
public org.vcell.util.document.Version getVersion() {
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
 * Gets the XML property (java.lang.String) value.
 * @return The XML property value.
 */
public java.lang.String getXML() {
	return null;
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
	if (evt.getSource() == this && evt.getPropertyName().equals("simulations") && evt.getNewValue()!=null){
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
	if (evt.getSource() == this && evt.getPropertyName().equals("simulationContexts") && evt.getNewValue()!=null){
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
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
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
	setSimulations((Simulation[])BeanUtils.removeElement(fieldSimulations,simulation));
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
	setSimulationContexts((SimulationContext[])BeanUtils.removeElement(fieldSimulationContexts,simulationContext));
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
 * Sets the model property (cbit.vcell.model.Model) value.
 * @param model The new value for the property.
 * @see #getModel
 */
public void setModel(cbit.vcell.model.Model model) {
	cbit.vcell.model.Model oldValue = fieldModel;
	fieldModel = model;
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


/**
 * Sets the simulationContexts property (cbit.vcell.mapping.SimulationContext[]) value.
 * @param simulationContexts The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulationContexts
 */
public void setSimulationContexts(cbit.vcell.mapping.SimulationContext[] simulationContexts) throws java.beans.PropertyVetoException {
	cbit.vcell.mapping.SimulationContext[] oldValue = fieldSimulationContexts;
	fireVetoableChange("simulationContexts", oldValue, simulationContexts);
	for (int i = 0; oldValue!=null && i < oldValue.length; i++){
		oldValue[i].removePropertyChangeListener(this);
		oldValue[i].removeVetoableChangeListener(this);
		oldValue[i].setBioModel(null);
	}
	fieldSimulationContexts = simulationContexts;
	for (int i = 0; simulationContexts!=null && i < simulationContexts.length; i++){
		simulationContexts[i].addPropertyChangeListener(this);
		simulationContexts[i].addVetoableChangeListener(this);
		simulationContexts[i].setBioModel(this);
	}
	firePropertyChange("simulationContexts", oldValue, simulationContexts);
}


/**
 * Sets the simulations property (cbit.vcell.solver.Simulation[]) value.
 * @param simulations The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSimulations
 */
public void setSimulations(cbit.vcell.solver.Simulation[] simulations) throws java.beans.PropertyVetoException {
	cbit.vcell.solver.Simulation[] oldValue = fieldSimulations;
	fireVetoableChange("simulations", oldValue, simulations);
	for (int i = 0; oldValue!=null && i < oldValue.length; i++){
		oldValue[i].removePropertyChangeListener(this);
		oldValue[i].removeVetoableChangeListener(this);
	}
	fieldSimulations = simulations;
	for (int i = 0; simulations!=null && i < simulations.length; i++){
		simulations[i].addPropertyChangeListener(this);
		simulations[i].addVetoableChangeListener(this);
	}
	firePropertyChange("simulations", oldValue, simulations);
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 3:49:12 PM)
 * @param version cbit.sql.Version
 */
private void setVersion(org.vcell.util.document.Version version) throws PropertyVetoException {
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
	if (evt.getSource() == this && evt.getPropertyName().equals("simulations") && evt.getNewValue()!=null){
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
	if (evt.getSource() == this && evt.getPropertyName().equals("simulationContexts") && evt.getNewValue()!=null){
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
/**
 * This method is modified on Nov 20, 2007. We got to go through the MassActionSolver and FluxSolver here to make sure that everything
 * is being checked before processing further. However, this makes the function become heavy.
 * The function is being referened in four different places, which are ClientRequestManager.runSimulations(), BioModelEditor.newApplication(),
 * SimulationContext.SimulationContext(SimulationContext, boolean) and StochMathMapping.refreshMathDescription().
 * Creation date: (11/16/2006 4:55:16 PM)
 * @return java.lang.String
 */
public String isValidForStochApp()
{
	String returnStr = ""; //sum of all the issues
	String exceptionGenStr = ""; //exception msg from MassActionSolver when parsing general kinetics for reactions
	String exceptionFluxStr = ""; //exception msg from FluxSolver when parsing general density function for fluxes 
	String unTransformableStr = ""; //all untransformable reactions/fluxes, which have kinetic laws rather than General and MassAction.
	String tStr = ""; //untransformable reactions/fluxes with time 't' in parsed forward and reverse rate constants
	String genReacts = "";//To count reactions with general rate law. We have to force user to tansform general to mass action before proceeding further.
	cbit.vcell.model.ReactionStep[] reacSteps = getModel().getReactionSteps();
	// Mass Action and centain form of general Flux can be automatically transformed.
	for (int i = 0; (reacSteps != null) && (i < reacSteps.length); i++)
	{
		if(((reacSteps[i] instanceof SimpleReaction) && (!(reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction)) && !(reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General)))) ||
		  ((reacSteps[i] instanceof FluxReaction) && (!reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General))))
		{
			unTransformableStr = unTransformableStr + " " + reacSteps[i].getName() + ",";
		}
		else
		{
			if(reacSteps[i] instanceof SimpleReaction)
			{
				if(reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.MassAction))
				{
					Expression forwardRate = reacSteps[i].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KForward).getExpression();
					Expression reverseRate = reacSteps[i].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KReverse).getExpression();
					if(forwardRate != null && forwardRate.hasSymbol(ReservedSymbol.TIME.getName()))
					{
						tStr = tStr + " " + reacSteps[i].getName() + ",";
					}
					if(reverseRate != null && reverseRate.hasSymbol(ReservedSymbol.TIME.getName()))
					{
						tStr = tStr + " " + reacSteps[i].getName() + ",";
					}
				}
				else if(reacSteps[i].getKinetics().getKineticsDescription().equals(KineticsDescription.General))
				{
					genReacts = genReacts + " " + reacSteps[i].getName() + ","; 
					Expression rateExp = reacSteps[i].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
					try{
						rateExp = substitueKineticPara(rateExp, reacSteps[i], false);
						MassActionSolver.MassActionFunction maFunc = MassActionSolver.solveMassAction(rateExp, reacSteps[i]);
						if(maFunc.getForwardRate() != null && maFunc.getForwardRate().hasSymbol(ReservedSymbol.TIME.getName()))
						{
							tStr = tStr + " " + reacSteps[i].getName() + ",";
						}
						if(maFunc.getReverseRate() != null && maFunc.getReverseRate().hasSymbol(ReservedSymbol.TIME.getName()))
						{
							tStr = tStr + " " + reacSteps[i].getName() + ",";
						}
					}catch(Exception e)
					{
						exceptionGenStr = exceptionGenStr + " " + reacSteps[i].getName() + " error: " + e.getMessage() + "\n";
					}
				}
			}
			else // flux described by General density function
			{
				Expression rateExp = reacSteps[i].getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ReactionRate).getExpression();
				try{
					rateExp = substitueKineticPara(rateExp, reacSteps[i], false);
					FluxSolver.FluxFunction fluxFunc = FluxSolver.solveFlux(rateExp,(FluxReaction)reacSteps[i]);
					if(fluxFunc.getRateToInside() != null && fluxFunc.getRateToInside().hasSymbol(ReservedSymbol.TIME.getName()))
					{
						tStr = tStr + " " + reacSteps[i].getName() + ",";
					}
					if(fluxFunc.getRateToOutside() != null && fluxFunc.getRateToOutside().hasSymbol(ReservedSymbol.TIME.getName()))
					{
						tStr = tStr + " " + reacSteps[i].getName() + ",";
					}
				}catch(Exception e)
				{
					exceptionFluxStr = exceptionFluxStr + " " + reacSteps[i].getName() + " error: " + e.getMessage() + "\n";
				}
			}
		}
	}
	
	if(unTransformableStr.length() > 0)
	{
		returnStr = returnStr + unTransformableStr.substring(0,(unTransformableStr.length()-1)) + " are unable to transform to stochastic formulation.\n Reactions described by mass action law(all) or General law(certain forms), or fluxes described by general desity function(certain forms) can be automatically transfromed.\n" ;
	}
	if(exceptionGenStr.length() > 0)
	{
		returnStr = returnStr + exceptionGenStr;
	}
	if(exceptionFluxStr.length() > 0)
	{
		returnStr = returnStr + exceptionFluxStr;
	}
	if(tStr.length() > 0)
	{
		returnStr = returnStr + tStr.substring(0,(tStr.length()-1)) + " have symbol \'t\' in propensity. Propensity of a stochastic jump process should not be a functon of time.";
	}
	//If the a stochastic application can be set and there are general law reactions, we force user to transform the general laws to mass action laws first.
	if(returnStr.equals("") && !genReacts.equals(""))
	{
		returnStr = returnStr + "The system is able to set up stochastic application. However reactions " + genReacts + " are described by general kinetic law.\n" + 
		            "Please use menu \'Tool -> Transform to Stochastic Capable\' in main window to transform these reactions before proceeding further.";
	}
	return returnStr;
}

private Expression substitueKineticPara(Expression exp, ReactionStep rs, boolean substituteConst) throws MappingException, ExpressionException
{
	Expression result = new Expression(exp);
	boolean bSubstituted = true;
	while(bSubstituted)
	{
		bSubstituted = false;
		String symbols[] = result.getSymbols();
		for (int k = 0;symbols!=null && k < symbols.length; k++){
			Kinetics.KineticsParameter kp = rs.getKinetics().getKineticsParameter(symbols[k]);
			if (kp != null)
			{
				try{
					Expression expKP = substitueKineticPara(kp.getExpression(), rs, true);
					if(!expKP.flatten().isNumeric()||substituteConst)
					{
						result.substituteInPlace(new Expression(symbols[k]), new Expression(kp.getExpression()));
						bSubstituted = true;
					}
				}catch(ExpressionException e1){
					e1.printStackTrace();
					throw new ExpressionException(e1.getMessage());
				}
			}
		}
		
	}
	return result;
}

}