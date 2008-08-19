package cbit.vcell.solver;
import cbit.vcell.mapping.MappingException;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.Vector;
import cbit.vcell.parser.Expression;
import java.util.Enumeration;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.ExpressionBindingException;
import java.beans.PropertyVetoException;
import cbit.util.ISize;
import cbit.util.TokenMangler;
import cbit.vcell.math.*;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.math.CommentStringTokenizer;
import cbit.vcell.solver.SimulationInfo;
import cbit.sql.*;
/**
 * Specifies the problem to be solved by a solver.
 * It is subclassed for each type of problem/solver.
 * Creation date: (8/16/2000 11:08:33 PM)
 * @author: John Wagner
 */
public class Simulation implements Versionable, cbit.util.Matchable, cbit.vcell.parser.SymbolTable, MathOverridesListener, java.beans.VetoableChangeListener, java.io.Serializable {
	// size quotas enforced per simulation
	public static final int MAX_LIMIT_ODE_TIMEPOINTS = 100000;
	public static final int MAX_LIMIT_PDE_TIMEPOINTS = 100000;
	public static final int MAX_LIMIT_STOCH_TIMEPOINTS = 1000000000; //stoch
	public static final int MAX_LIMIT_0DE_MEGABYTES = 20;
	public static final int MAX_LIMIT_PDE_MEGABYTES = 20000;
	public static final int MAX_LIMIT_STOCH_MEGABYTES = 200; //stoch
	public static final int WARNING_ODE_TIMEPOINTS = 5000;
	public static final int WARNING_PDE_TIMEPOINTS = 1000;
	public static final int WARNING_STOCH_TIMEPOINTS = 100000000; //stoch
	public static final int WARNING_0DE_MEGABYTES = 5;
	public static final int WARNING_PDE_MEGABYTES = 200;
	public static final int WARNING_STOCH_MEGABYTES = 50; //stoch
	
	public static final int WARNING_SCAN_JOBS = 20;
	public static final int MAX_LIMIT_SCAN_JOBS = 40;
	/**
	 * Database version of the Simulation.
	 */
	private cbit.sql.SimulationVersion fieldSimulationVersion = null;
	/**
	 * Mathematical description of the physiological model.
	 */
	private cbit.vcell.math.MathDescription fieldMathDescription = null;
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
	private MathOverrides fieldMathOverrides = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private SolverTaskDescription fieldSolverTaskDescription = null;
	private java.lang.String fieldSimulationIdentifier = null;
	private MeshSpecification fieldMeshSpecification = null;
	private boolean fieldIsSpatial = false;
	//
	// lists used for binding for use by solvers.
	//
	private transient java.util.Vector localFunctions = null;
	private transient java.util.Vector localConstants = null;
	private boolean fieldIsDirty = false;
	private java.lang.String fieldWarning = null;
	/**
	 * Field for multiplexing and spawning job arrays
	 * Working sims created when necessarry with appropriate index
	 */
	private int index = 0;

/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when creating a new Simulation.
 */
public Simulation(SimulationVersion argSimulationVersion, cbit.vcell.math.MathDescription mathDescription) {
	super();
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
		fieldIsSpatial = true;
	}else{
		fieldIsSpatial = false;
	}
	fieldMathOverrides = new MathOverrides(this);
	fieldMathOverrides.addMathOverridesListener(this);
	fieldSolverTaskDescription = new SolverTaskDescription(this);
	
	rebindAll();  // especially needed to bind Constants so that .substitute() will eliminate Constants that are functions of other Constants.

}


/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when creating a Simulation from the database.
 */
public Simulation(SimulationVersion simulationVersion, MathDescription mathDescription, CommentStringTokenizer mathOverridesTokenizer, CommentStringTokenizer solverTaskDescriptionTokenizer) throws DataAccessException, PropertyVetoException {
	super();
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
			fieldIsSpatial = true;
		}else{
			fieldIsSpatial = false;
		}
	}
	//  Must set the MathDescription before constructing these...
	fieldMathOverrides = new MathOverrides(this, mathOverridesTokenizer);
	fieldMathOverrides.addMathOverridesListener(this);
	fieldSolverTaskDescription = new SolverTaskDescription(this, solverTaskDescriptionTokenizer);
}


/**
 * One of three ways to construct a Simulation.  This constructor
 * is used when creating a new Simulation.
 */
public Simulation(cbit.vcell.math.MathDescription mathDescription) {
	super();
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
		fieldIsSpatial = true;
	}else{
		fieldIsSpatial = false;
	}
	fieldMathOverrides = new MathOverrides(this);
	fieldMathOverrides.addMathOverridesListener(this);
	fieldSolverTaskDescription = new SolverTaskDescription(this);
	
	rebindAll();  // especially needed to bind Constants so that .substitute() will eliminate Constants that are functions of other Constants.

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
	super();
	addVetoableChangeListener(this);

	fieldSimulationVersion = null;
	fieldName = simulation.getName();
	fieldDescription = simulation.getDescription();
	fieldSimulationIdentifier = null;
	if (bCloneMath){
		fieldMathDescription = new MathDescription(simulation.getMathDescription());
	}else{
		fieldMathDescription = simulation.getMathDescription();
	}
	fieldIsSpatial = simulation.getIsSpatial();
	if (simulation.getMeshSpecification()!=null){
		fieldMeshSpecification = new MeshSpecification(simulation.getMeshSpecification());
	}else{
		fieldMeshSpecification = null;
	}
	//  Must set the MathDescription before constructing these...
	fieldMathOverrides = new MathOverrides (this, simulation.getMathOverrides());
	fieldMathOverrides.addMathOverridesListener(this);
	fieldSolverTaskDescription = new SolverTaskDescription(this, simulation.getSolverTaskDescription());

	rebindAll();  // especially needed to bind Constants so that .substitute() will eliminate Constants that are functions of other Constants.
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
 * Creation date: (10/9/2002 10:54:06 PM)
 * @return cbit.vcell.math.MathDescription
 */
public void applyOverrides(MathDescription newMath) throws ExpressionException, cbit.vcell.mapping.MappingException, MathException {
	
	//
	// replace original constants with "Simulation" constants
	//
	Variable newVarArray[] = (Variable[])cbit.util.BeanUtils.getArray(newMath.getVariables(),Variable.class);
	for (int i = 0; i < newVarArray.length; i++){
		if (newVarArray[i] instanceof Constant){
			Constant origConstant = (Constant)newVarArray[i];
			Constant simConstant = getLocalConstant(origConstant);
			newVarArray[i] = new Constant(origConstant.getName(),new Expression(simConstant.getExpression()));
		}
	}
	newMath.setAllVariables(newVarArray);
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
public boolean compareEqual(cbit.util.Matchable object) {
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
		if (!cbit.util.Compare.isEqual(getName(),simulation.getName())){
			return false;
		}
		if (!cbit.util.Compare.isEqualOrNull(getDescription(),simulation.getDescription())){
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
	if (index != simulation.index) return false;
	if (!getMathDescription().compareEqual(simulation.getMathDescription())) return (false);
	if (!getMathOverrides().compareEqual(simulation.getMathOverrides())) return (false);
	if (!getSolverTaskDescription().compareEqual(simulation.getSolverTaskDescription())) return (false);
	if (!cbit.util.Compare.isEqualOrNull(getMeshSpecification(),simulation.getMeshSpecification())) return (false);

	return true;
}


/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
public void constantAdded(MathOverridesEvent event) {
	rebindAll();
}


/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
public void constantChanged(MathOverridesEvent event) {
	rebindAll();
}


/**
 * 
 * @param event cbit.vcell.solver.MathOverridesEvent
 */
public void constantRemoved(MathOverridesEvent event) {
	rebindAll();
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
 * Insert the method's description here.
 * Creation date: (10/7/2005 4:44:10 PM)
 * @return cbit.vcell.solver.Simulation
 * @param sim cbit.vcell.solver.Simulation
 * @param jobIndex int
 */
static Simulation createWorkingSim(Simulation sim, int jobIndex) {
	if (jobIndex < 0 || jobIndex >= sim.getScanCount()) {
		throw new RuntimeException("Bad job index ["+jobIndex+"] for simulation "+ sim.toString());
	}
	Simulation workingSim = null;

	// try/catch block below could be replaced by alternate implementation
	// should profile and choose according to best performance

	try {
		String xml = cbit.vcell.xml.XmlHelper.simToXML(sim);
		workingSim = cbit.vcell.xml.XmlHelper.XMLToSim(xml);
	} catch (cbit.vcell.xml.XmlParseException exc) {
		throw new RuntimeException("Exception occurred while cloning working simulation\n"+exc);
	}

	/*
	--- begin alternate implementation ---

	workingSim = cbit.util.BeanUtils.cloneSerializable(sim);

	--- end alternate implementation   ---
	*/
	
	workingSim.index = jobIndex;
	workingSim.rebindAll();
	return workingSim;
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
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {
	//
	// use MathDescription as the primary SymbolTable, just replace the Constants with the overrides.
	//
	SymbolTableEntry ste = getMathDescription().getEntry(identifierString);
	if (ste instanceof Constant){
		try {
			Constant constant = getLocalConstant((Constant)ste);
			return constant;
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new ExpressionBindingException("Simulation.getEntry(), error getting local Constant (math override)"+identifierString);
		}
	}else if (ste instanceof Function){
		try {
			Function function = getLocalFunction((Function)ste);
			return function;
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new ExpressionBindingException("Simulation.getEntry(), error getting local Function "+identifierString+", "+e.getMessage());
		}
	}else{
		return ste;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/01 11:34:08 AM)
 * @return cbit.vcell.math.Variable[]
 */
public Function[] getFunctions() {
	
	java.util.Vector functList = new java.util.Vector();
	
	//
	// get all variables from MathDescription, but replace MathOverrides
	//
	Variable variables[] = getVariables();
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof Function){
			functList.addElement(variables[i]);
		}
	}

	return (Function[])cbit.util.BeanUtils.getArray(functList,Function.class);
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
 * @return The isSpatial property value.
 * @see #setIsSpatial
 */
public boolean getIsSpatial() {
	return fieldIsSpatial;
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/01 11:25:24 AM)
 * @return boolean
 */
public boolean getIsValid() {
	setWarning(null);

	//
	// Check if the math corresponding to this simulation has fast systems and if the solverTaskDescription contains a non-null sensitivity parameter.
	// If so, the simulation is invalid.
	//
	if (fieldMathDescription != null && getSolverTaskDescription() != null) {
		if (getMathDescription().hasFastSystems() && (getSolverTaskDescription().getSensitivityParameter() != null)) {
			setWarning("Sensitivity Analysis for a math with Fast Systems is not supported yet. Please disable sensitivity analysis for this simulation to run.");
			return false;
		}
	}
	if (fieldMathDescription!=null && fieldMathDescription.isValid()){
		return true;
	}else{
		setWarning(fieldMathDescription.getWarning());
		return false;
	}
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
 * Insert the method's description here.
 * Creation date: (6/6/2001 7:52:15 PM)
 * @return cbit.vcell.math.Function
 * @param functionName java.lang.String
 */
private Constant getLocalConstant(Constant referenceConstant) throws ExpressionException {
	if (localConstants==null){
		localConstants = new Vector();
	}
	for (int i = 0; i < localConstants.size(); i++){
		Constant localConstant = (Constant)localConstants.elementAt(i);
		if (localConstant.getName().equals(referenceConstant.getName())){
			//
			// make sure expression for localConstant is still up to date with MathOverrides table
			//
			Expression exp = getMathOverrides().getActualExpression(referenceConstant.getName(), index);
			if (exp.compareEqual(localConstant.getExpression())){
				//localConstant.bind(this); // update bindings to latest mathOverrides
				return localConstant;
			}else{
				//
				// MathOverride's Expression changed for this Constant, remove and create new one
				//
				localConstants.remove(localConstant);
				break;
			}
		}
	}
	//
	// if local Constant not found, create new one, bind it to the Simulation (which ensures MathOverrides), and add to list
	//
	String name = referenceConstant.getName();
	Constant newLocalConstant = new Constant(name,getMathOverrides().getActualExpression(name, index));
	//newLocalConstant.bind(this);
	localConstants.add(newLocalConstant);
	return newLocalConstant;
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2001 7:52:15 PM)
 * @return cbit.vcell.math.Function
 * @param functionName java.lang.String
 */
private Function getLocalFunction(Function referenceFunction) throws ExpressionException {
	if (localFunctions==null){
		localFunctions = new Vector();
	}
	for (int i = 0; i < localFunctions.size(); i++){
		Function localFunction = (Function)localFunctions.elementAt(i);
		if (localFunction.compareEqual(referenceFunction)){
			//localFunction.bind(this); // update bindings to latest mathOverrides
			return localFunction;
		}
	}
	//
	// if local Function not found, create new one, bind it to the Simulation (which ensures MathOverrides), and add to list
	//
	Function newLocalFunction = new Function(referenceFunction.getName(),new Expression(referenceFunction.getExpression()));
	//newLocalFunction.bind(this);
	localFunctions.add(newLocalFunction);
	return newLocalFunction;
}


/**
 * Gets the mathDescription property (cbit.vcell.math.MathDescription) value.
 * @return The mathDescription property value.
 */
public cbit.vcell.math.MathDescription getMathDescription() {
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
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 * @param exp cbit.vcell.parser.Expression
 */
public Enumeration getRequiredVariables(Expression exp) throws MathException, ExpressionException {
	return MathUtilities.getRequiredVariables(exp,this);
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
public cbit.vcell.solver.SimulationInfo getSimulationInfo() {
	if (getVersion() != null) {
		return new SimulationInfo(
			getMathDescription().getKey(),
			getSimulationVersion()); 
	} else {
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 1:34:10 PM)
 * @return cbit.sql.Version
 */
public cbit.sql.SimulationVersion getSimulationVersion() {
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
 * Creation date: (5/25/01 12:31:53 PM)
 * @return cbit.vcell.math.Variable
 * @param variableName java.lang.String
 */
public Variable getVariable(String variableName) {
	try {
		return (Variable)getEntry(variableName);
	}catch (cbit.vcell.parser.ExpressionBindingException e){
		e.printStackTrace(System.out);
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/25/01 11:34:08 AM)
 * @return cbit.vcell.math.Variable[]
 */
public Variable[] getVariables() {
	
	java.util.Vector varList = new java.util.Vector();
	
	//
	// get all variables from MathDescription, but replace MathOverrides
	//
	java.util.Enumeration enum1 = getMathDescription().getVariables();
	while (enum1.hasMoreElements()) {
		Variable mathDescriptionVar = (Variable)enum1.nextElement();
		//
		// replace all Constants with math overrides
		//
		if (mathDescriptionVar instanceof Constant){
			try {
				Constant overriddenConstant = getLocalConstant((Constant)mathDescriptionVar);
				varList.addElement(overriddenConstant);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("local Constant "+mathDescriptionVar.getName()+" not found for Simulation");
			}
		//
		// replace all Functions with local Functions that are bound to this Simulation
		//
		}else if (mathDescriptionVar instanceof Function){
			try {
				Function overriddenFunction = getLocalFunction((Function)mathDescriptionVar);
				varList.addElement(overriddenFunction);
			}catch (ExpressionException e){
				e.printStackTrace(System.out);
				throw new RuntimeException("local Function "+mathDescriptionVar.getName()+" not found for Simulation");
			}
		//
		// pass all other Variables through
		//
		}else{
			varList.addElement(mathDescriptionVar);
		}
	}

	Variable variables[] = (Variable[])cbit.util.BeanUtils.getArray(varList,Variable.class);

	return variables;
}


/**
 * Insert the method's description here.
 * Creation date: (10/30/00 11:56:03 AM)
 * @return java.lang.String
 */
public String getVCML() throws MathException {
	
	StringBuffer buffer = new StringBuffer();
	
	String name = (getVersion()!=null)?(getVersion().getName()):"unnamedSimulation";
	buffer.append(cbit.vcell.math.VCML.Simulation+" "+name+" {\n");

	//
	// write MathDescription
	//
	buffer.append(cbit.vcell.math.VCML.MathDescription+" "+getMathDescription().getVCML_database()+"\n");

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
public cbit.sql.Version getVersion() {
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
 * Gets the warning property (java.lang.String) value.
 * @return The warning property value.
 */
public java.lang.String getWarning() {
	return fieldWarning;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param volVariable cbit.vcell.math.VolVariable
 */
public boolean hasTimeVaryingDiffusionOrAdvection(Variable variable) throws Exception {
	Enumeration enum1 = getMathDescription().getSubDomains();
	while (enum1.hasMoreElements()){
		SubDomain subDomain = (SubDomain)enum1.nextElement();
		Equation equation = subDomain.getEquation(variable);
		//
		// get diffusion expressions, see if function of time or volume variables
		//
		if (equation instanceof PdeEquation){
			Vector spatialExpressionList = new Vector();
			spatialExpressionList.add(((PdeEquation)equation).getDiffusionExpression());
			if (((PdeEquation)equation).getVelocityX()!=null){
				spatialExpressionList.add(((PdeEquation)equation).getVelocityX());
			}
			if (((PdeEquation)equation).getVelocityY()!=null){
				spatialExpressionList.add(((PdeEquation)equation).getVelocityY());
			}
			if (((PdeEquation)equation).getVelocityZ()!=null){
				spatialExpressionList.add(((PdeEquation)equation).getVelocityZ());
			}
			for (int i = 0; i < spatialExpressionList.size(); i++){
				Expression spatialExp = (Expression)spatialExpressionList.elementAt(i);
				spatialExp = substituteFunctions(spatialExp);
				String symbols[] = spatialExp.getSymbols();
				if (symbols!=null){
					for (int j=0;j<symbols.length;j++){
						SymbolTableEntry entry = spatialExp.getSymbolBinding(symbols[j]);
						if (entry instanceof ReservedVariable){
							if (((ReservedVariable)entry).isTIME()){
								return true;
							}
						}
						if (entry instanceof VolVariable){
							return true;
						}
						if (entry instanceof VolumeRegionVariable){
							return true;
						}
						if (entry instanceof MemVariable || entry instanceof MembraneRegionVariable) {
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
 * Insert the method's description here.
 * Creation date: (6/20/01 12:35:46 PM)
 */
private void rebindAll() {
	//
	// cleanup
	//
	if (localConstants!=null){
		localConstants.removeAllElements();
	}
	if (localFunctions!=null){
		localFunctions.removeAllElements();
	}
	// reload
	getVariables();

	//
	// bind all Constants
	//
	for (int i = 0; localConstants!=null && i < localConstants.size(); i++){
		Constant localConstant = (Constant)localConstants.elementAt(i);
		try {
			localConstant.bind(this); // update bindings to latest mathOverrides
		}catch (ExpressionBindingException e){
			e.printStackTrace(System.out);
		}
	}
	//
	// bind all Functions
	//
	for (int i = 0; localFunctions!=null && i < localFunctions.size(); i++){
		Function localFunction = (Function)localFunctions.elementAt(i);
		try {
			localFunction.bind(this); // update bindings to latest mathOverrides
		}catch (ExpressionBindingException e){
			e.printStackTrace(System.out);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/11/01 4:00:35 PM)
 */
public void refreshDependencies() {
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
	getMathOverrides().removeMathOverridesListener(this);
	getMathOverrides().addMathOverridesListener(this);
	if (getMeshSpecification()!=null){
		getMeshSpecification().refreshDependencies();
	}
	getSolverTaskDescription().refreshDependencies();
	//
	// this ensures proper binding to override constants.
	//
	rebindAll();
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
 * Sets the isSpatial property (boolean) value.
 * @param isSpatial The new value for the property.
 * @see #getIsSpatial
 */
private void setIsSpatial(boolean isSpatial) {
	boolean oldValue = fieldIsSpatial;
	fieldIsSpatial = isSpatial;
	firePropertyChange("isSpatial", new Boolean(oldValue), new Boolean(isSpatial));
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/00 1:17:37 PM)
 * @param mathDesc cbit.vcell.math.MathDescription
 */
public void setMathDescription(cbit.vcell.math.MathDescription mathDescription) throws java.beans.PropertyVetoException {
	cbit.vcell.math.MathDescription oldValue = fieldMathDescription;
	fireVetoableChange("mathDescription", oldValue, mathDescription);
	fieldMathDescription = mathDescription;

	//
	// refresh MeshSpecification
	//
	if (mathDescription.getGeometry().getDimension()>0){
		setIsSpatial(true);
		if (getMeshSpecification()!=null){
			getMeshSpecification().setGeometry(mathDescription.getGeometry());
		}else{
			setMeshSpecification(new MeshSpecification(mathDescription.getGeometry()));
		}
	}else{
		setIsSpatial(false);
		setMeshSpecification(null);
	}
	//
	// refresh MathOverrides
	//
	if (mathDescription!=null && getMathOverrides()!=null){
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
	if (oldValue!=null){
		oldValue.removeMathOverridesListener(this);
	}
	fieldMathOverrides = mathOverrides;
	if (mathOverrides!=null){
		mathOverrides.addMathOverridesListener(this);
	}
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
//System.out.println("calling Simulation.setMeshSpecification(), <<< must be called AFTER setMathDescription() >>>");
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
	fireVetoableChange("name", oldValue, name);
	fieldName = name;
	firePropertyChange("name", oldValue, name);
}


/**
 * Sets the solverTaskDescription property (cbit.vcell.solver.SolverTaskDescription) value.
 * @param solverTaskDescription The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getSolverTaskDescription()
 */
public void setSolverTaskDescription(SolverTaskDescription solverTaskDescription) throws java.beans.PropertyVetoException {
	SolverTaskDescription oldValue = fieldSolverTaskDescription;
	fireVetoableChange("solverTaskDescription", oldValue, solverTaskDescription);
	fieldSolverTaskDescription = solverTaskDescription;
	firePropertyChange("solverTaskDescription", oldValue, solverTaskDescription);
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/00 3:49:12 PM)
 * @param version cbit.sql.Version
 */
private void setVersion(cbit.sql.SimulationVersion simulationVersion) throws PropertyVetoException {
	this.fieldSimulationVersion = simulationVersion;
	if (simulationVersion != null){
		setName(simulationVersion.getName());
		setDescription(simulationVersion.getAnnot());
	}
}


/**
 * Sets the warning property (java.lang.String) value.
 * @param warning The new value for the property.
 * @see #getWarning
 */
private void setWarning(java.lang.String warning) {
	String oldValue = fieldWarning;
	fieldWarning = warning;
	firePropertyChange("warning", oldValue, warning);
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 * @param exp cbit.vcell.parser.Expression
 * @exception java.lang.Exception The exception description.
 */
public Expression substituteFunctions(Expression exp) throws MathException, ExpressionException {
	return MathUtilities.substituteFunctions(exp,this);
}


/**
 * Insert the method's description here.
 * Creation date: (9/28/2004 5:50:22 PM)
 * @return java.lang.String
 * @param memoryMathDescription cbit.vcell.math.MathDescription
 * @param databaseMathDescription cbit.vcell.math.MathDescription
 */
public static boolean testEquivalency(Simulation memorySimulation, Simulation databaseSimulation, String mathEquivalency) {

	if (memorySimulation == databaseSimulation){
		return true;
	}
	
	if (mathEquivalency.equals(MathDescription.MATH_DIFFERENT)){
		return false;
	}else if (mathEquivalency.equals(MathDescription.MATH_SAME) || mathEquivalency.equals(MathDescription.MATH_EQUIVALENT)){
		if (!memorySimulation.getSolverTaskDescription().compareEqual(databaseSimulation.getSolverTaskDescription())){
			return false;
		}
		if (!cbit.util.Compare.isEqualOrNull(memorySimulation.getMeshSpecification(),databaseSimulation.getMeshSpecification())){
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
		if (!memorySimulation.getMathOverrides().compareEqual(databaseSimulation.getMathOverrides())){
			return false;
		}
		return true;
	}else{
		throw new IllegalArgumentException("unknown equivalency choice '"+mathEquivalency+"'");
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
}