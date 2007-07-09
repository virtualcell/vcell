package cbit.vcell.modelopt;

import org.vcell.expression.IExpression;
import org.vcell.expression.SymbolTableEntry;

import cbit.vcell.model.Parameter;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.modelapp.SimulationContext;
import cbit.util.graph.Node;
import cbit.vcell.math.*;
/**
 * Insert the type's description here.
 * Creation date: (8/22/2005 9:21:42 AM)
 * @author: Jim Schaff
 */
public class ModelOptimizationSpec implements java.io.Serializable, org.vcell.util.Matchable {
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private cbit.vcell.modelapp.SimulationContext fieldSimulationContext = null;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private cbit.vcell.modelopt.ParameterMappingSpec[] fieldParameterMappingSpecs = null;
	private cbit.vcell.opt.ReferenceData fieldReferenceData = null;
	private cbit.vcell.modelopt.ReferenceDataMappingSpec[] fieldReferenceDataMappingSpecs = null;

/**
 * ModelOptimizationSpec constructor comment.
 */
public ModelOptimizationSpec(SimulationContext argSimulationContext) throws org.vcell.expression.ExpressionException {
	super();
	this.fieldSimulationContext = argSimulationContext;
	initializeParameterMappingSpecs();
}


/**
 * ModelOptimizationSpec constructor comment.
 */
public ModelOptimizationSpec(ModelOptimizationSpec modelOptimizationSpecToCopy) throws org.vcell.expression.ExpressionException {
	super();
	this.fieldSimulationContext = modelOptimizationSpecToCopy.fieldSimulationContext;
	fieldParameterMappingSpecs = new ParameterMappingSpec[modelOptimizationSpecToCopy.fieldParameterMappingSpecs.length];
	for (int i = 0; i < fieldParameterMappingSpecs.length; i++){
		fieldParameterMappingSpecs[i] = new ParameterMappingSpec(modelOptimizationSpecToCopy.fieldParameterMappingSpecs[i]);
	}
	if (modelOptimizationSpecToCopy.fieldReferenceData!=null){
		fieldReferenceData = new cbit.vcell.opt.SimpleReferenceData(modelOptimizationSpecToCopy.fieldReferenceData);
	}
	if (modelOptimizationSpecToCopy.fieldReferenceDataMappingSpecs!=null){
		fieldReferenceDataMappingSpecs = new ReferenceDataMappingSpec[modelOptimizationSpecToCopy.fieldReferenceDataMappingSpecs.length];
		for (int i = 0; i < fieldReferenceDataMappingSpecs.length; i++){
			fieldReferenceDataMappingSpecs[i] = new ReferenceDataMappingSpec(modelOptimizationSpecToCopy.fieldReferenceDataMappingSpecs[i]);
		}
	}
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
 * The addVetoableChangeListener method was generated to support the vetoPropertyChange field.
 */
public synchronized void addVetoableChangeListener(java.lang.String propertyName, java.beans.VetoableChangeListener listener) {
	getVetoPropertyChange().addVetoableChangeListener(propertyName, listener);
}


/**
 * Insert the method's description here.
 * Creation date: (11/29/2005 5:10:51 PM)
 * @return cbit.vcell.parser.SymbolTableEntry[]
 */
public org.vcell.expression.SymbolTableEntry[] calculateTimeDependentModelObjects() {

	cbit.util.graph.Graph digraph = new cbit.util.graph.Graph();

	//
	// add time
	//
	Node timeNode = new cbit.util.graph.Node("t",cbit.vcell.model.ReservedSymbol.TIME);
	digraph.addNode(timeNode);
	
	//
	// add all species concentrations (that are not fixed with a constant initial condition).
	//
	cbit.vcell.modelapp.SpeciesContextSpec scs[] = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	for (int i = 0;scs!=null && i < scs.length; i++){
		if (!scs[i].isConstant() || !scs[i].getInitialConditionParameter().getExpression().isNumeric()){
			String speciesContextScopedName = scs[i].getSpeciesContext().getNameScope().getAbsoluteScopePrefix()+scs[i].getSpeciesContext().getName();
			Node speciesContextNode = new cbit.util.graph.Node(speciesContextScopedName,scs[i].getSpeciesContext());
			digraph.addNode(speciesContextNode);
			digraph.addEdge(new cbit.util.graph.Edge(speciesContextNode,timeNode));
		}
	}

	//
	// add all parameters that are not simple constants
	//
	ReactionStep[] reactionSteps = getSimulationContext().getModel().getReactionSteps();
	for (int i = 0; reactionSteps!=null && i < reactionSteps.length; i++){
		Parameter[] parameters = reactionSteps[i].getKinetics().getKineticsParameters();
		for (int j = 0; parameters!=null && j < parameters.length; j++){
			IExpression exp = parameters[j].getExpression();
			if (exp!=null){
				String symbols[] = exp.getSymbols();
				if (symbols!=null && symbols.length>0){
					//
					// add parameter to graph as a node (if not already there).
					//
					String parameterScopedName = parameters[j].getNameScope().getAbsoluteScopePrefix()+parameters[j].getName();
					Node parameterNode = digraph.getNode(parameterScopedName);
					if (parameterNode==null){
						parameterNode = new Node(parameterScopedName,parameters[j]);
						digraph.addNode(parameterNode);
					}
					//
					// add all dependencies to graph also (if not already there).
					//
					for (int k = 0; symbols!=null && k < symbols.length; k++){
						org.vcell.expression.SymbolTableEntry ste = exp.getSymbolBinding(symbols[k]);
						if (ste==null){
							throw new RuntimeException("Error, symbol '"+symbols[k]+"' not bound in parameter '"+parameters[j]+"'");
						}
						String symbolScopedName = ste.getNameScope().getAbsoluteScopePrefix()+ste.getName();
						Node symbolNode = digraph.getNode(symbolScopedName);
						if (symbolNode==null){
							symbolNode = new Node(symbolScopedName,ste);
							digraph.addNode(symbolNode);
						}
						digraph.addEdge(new cbit.util.graph.Edge(parameterNode,symbolNode));
					}
				}
			}
		}
	}

	Node[] timeDependentNodes = digraph.getDigraphAttractorSet(timeNode);
	
	SymbolTableEntry steArray[] = new SymbolTableEntry[timeDependentNodes.length];
	for (int i = 0; i < steArray.length; i++){
		steArray[i] = (SymbolTableEntry)timeDependentNodes[i].getData();
	}

	return steArray;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof ModelOptimizationSpec){
		ModelOptimizationSpec mos = (ModelOptimizationSpec)obj;

		if (!org.vcell.util.Compare.isEqual(fieldParameterMappingSpecs,mos.fieldParameterMappingSpecs)){
			return false;
		}

		if (!org.vcell.util.Compare.isEqualOrNull(fieldReferenceData,mos.fieldReferenceData)){
			return false;
		}

		if (!org.vcell.util.Compare.isEqualOrNull(fieldReferenceDataMappingSpecs,mos.fieldReferenceDataMappingSpecs)){
			return false;
		}


		return true;
	}
	return false;
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
 * Creation date: (8/22/2005 10:38:04 AM)
 * @return cbit.vcell.model.Parameter[]
 */
private cbit.vcell.model.Parameter[] getModelParameters() {
	java.util.Vector modelParameterList = new java.util.Vector();
	cbit.vcell.model.Model model = getSimulationContext().getModel();
	//
	// get kinetic parameters that are numbers
	//
	cbit.vcell.model.ReactionStep[] reactionSteps = model.getReactionSteps();
	for (int i = 0; i < reactionSteps.length; i++){
		//
		// make sure ReactionSteps are "enabled"
		//
		cbit.vcell.modelapp.ReactionSpec reactionSpec = getSimulationContext().getReactionContext().getReactionSpec(reactionSteps[i]);
		if (reactionSpec.isExcluded()){
			continue;
		}
		cbit.vcell.model.Kinetics.KineticsParameter[] kineticsParameters = reactionSteps[i].getKinetics().getKineticsParameters();
		for (int j = 0; j < kineticsParameters.length; j++){
			if (kineticsParameters[j].getExpression()!=null && kineticsParameters[j].getExpression().isNumeric()){
				if (kineticsParameters[j].getRole() == cbit.vcell.model.Kinetics.ROLE_Current && 
					reactionSteps[i].getPhysicsOptions() == cbit.vcell.model.ReactionStep.PHYSICS_MOLECULAR_ONLY){
					continue;
				}
				if (kineticsParameters[j].getRole() == cbit.vcell.model.Kinetics.ROLE_Rate && 
					reactionSteps[i].getPhysicsOptions() == cbit.vcell.model.ReactionStep.PHYSICS_ELECTRICAL_ONLY){
					continue;
				}
				modelParameterList.add(kineticsParameters[j]);
			}
		}
	}
	//
	// get initial conditions that are numbers
	//
	cbit.vcell.modelapp.SpeciesContextSpec[] speciesContextSpecs = getSimulationContext().getReactionContext().getSpeciesContextSpecs();
	for (int i = 0; i < speciesContextSpecs.length; i++){
		cbit.vcell.modelapp.SpeciesContextSpec.SpeciesContextSpecParameter initCondParam = speciesContextSpecs[i].getInitialConditionParameter();
		if (initCondParam.getExpression()!=null && initCondParam.getExpression().isNumeric()){
			modelParameterList.add(initCondParam);
		}
	}

	//
	// get structure parameters
	//
	cbit.vcell.modelapp.StructureMapping[] structureMappings = getSimulationContext().getGeometryContext().getStructureMappings();
	for (int i = 0; i < structureMappings.length; i++){
		cbit.vcell.modelapp.StructureMapping.StructureMappingParameter[] parameters = structureMappings[i].getParameters();
		for (int j = 0; j < parameters.length; j++){
			if (parameters[j].getRole() == cbit.vcell.modelapp.StructureMapping.ROLE_SpecificCapacitance &&
				structureMappings[i] instanceof cbit.vcell.modelapp.MembraneMapping &&
				!((cbit.vcell.modelapp.MembraneMapping)structureMappings[i]).getCalculateVoltage()){
				continue;
			}
			modelParameterList.add(parameters[j]);
		}
	}
	try {
		cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(getSimulationContext());
		cbit.vcell.math.MathDescription mathDesc = mathMapping.getMathDescription();
		cbit.vcell.mapping.MathSystemHash mathSystemHash = cbit.vcell.mapping.MathSystemTest.fromMath(mathDesc);
		cbit.util.graph.Graph graph = mathSystemHash.getDependencyGraph(mathSystemHash.getSymbols());
		cbit.util.graph.Tree[] spanningTrees = graph.getSpanningForest();
		//
		// remove trees without any State Variables
		//
		for (int i = 0; i < spanningTrees.length; i++){
			cbit.util.graph.Node[] treeNodes = spanningTrees[i].getNodes();
			boolean bHasStateVariables = false;
			for (int j = 0; j < treeNodes.length; j++){
				cbit.util.graph.Node node = treeNodes[j];
				cbit.vcell.math.Variable var = mathDesc.getVariable(node.getName());
				if (var instanceof VolVariable || var instanceof MemVariable || var instanceof FilamentVariable ||
					var instanceof VolumeRegionVariable || var instanceof MembraneRegionVariable || var instanceof FilamentRegionVariable){
					bHasStateVariables = true;
					break;
				}
			}
			if (!bHasStateVariables){
				spanningTrees = (cbit.util.graph.Tree[])org.vcell.util.BeanUtils.removeElement(spanningTrees,spanningTrees[i]);
				i--;
			}
		}

		//
		// remove parameters not mapped to a surviving tree (not coupled to any state variables
		//
		for (int i = 0; i < modelParameterList.size(); i++){
			cbit.vcell.model.Parameter parameter = (cbit.vcell.model.Parameter)modelParameterList.elementAt(i);
			String mathName = mathMapping.getMathSymbol(parameter,null);
			boolean bFoundInTree = false;
			for (int j = 0; j < spanningTrees.length; j++){
				cbit.util.graph.Node node = spanningTrees[j].getNode(mathName);
				if (node != null){
					bFoundInTree = true;
				}
			}
			if (!bFoundInTree){
				modelParameterList.removeElementAt(i);
				i--;
			}
		}
	}catch (Exception e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
	
	cbit.vcell.model.Parameter[] modelParameters = (cbit.vcell.model.Parameter[])org.vcell.util.BeanUtils.getArray(modelParameterList,cbit.vcell.model.Parameter.class);
	return modelParameters;
}


/**
 * Insert the method's description here.
 * Creation date: (5/5/2006 5:18:19 PM)
 * @return cbit.vcell.modelopt.ParameterMappingSpec
 * @param parameter cbit.vcell.model.Parameter
 */
public ParameterMappingSpec getParameterMappingSpec(Parameter parameter) {
	for (int i = 0;fieldParameterMappingSpecs!=null && i < fieldParameterMappingSpecs.length; i++){
		if (fieldParameterMappingSpecs[i].getModelParameter().equals(parameter)){
			return fieldParameterMappingSpecs[i];
		}
	}
	return null;
}


/**
 * Gets the parameterMapping property (cbit.vcell.modelopt.ParameterMapping[]) value.
 * @return The parameterMapping property value.
 * @see #setParameterMapping
 */
public cbit.vcell.modelopt.ParameterMappingSpec[] getParameterMappingSpecs() {
	return fieldParameterMappingSpecs;
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
 * Gets the constraintData property (cbit.vcell.opt.ConstraintData) value.
 * @return The constraintData property value.
 * @see #setConstraintData
 */
public cbit.vcell.opt.ReferenceData getReferenceData() {
	return fieldReferenceData;
}


/**
 * Gets the referenceDataMappingSpecs property (cbit.vcell.modelopt.ReferenceDataMappingSpec[]) value.
 * @return The referenceDataMappingSpecs property value.
 * @see #setReferenceDataMappingSpecs
 */
public cbit.vcell.modelopt.ReferenceDataMappingSpec getReferenceDataMappingSpec(String dataColumnName) {
	for (int i = 0; fieldReferenceDataMappingSpecs!=null && i < fieldReferenceDataMappingSpecs.length; i++){
		if (fieldReferenceDataMappingSpecs[i].getReferenceDataColumnName().equals(dataColumnName)){
			return fieldReferenceDataMappingSpecs[i];
		}
	}
	return null;
}


/**
 * Gets the referenceDataMappingSpecs property (cbit.vcell.modelopt.ReferenceDataMappingSpec[]) value.
 * @return The referenceDataMappingSpecs property value.
 * @see #setReferenceDataMappingSpecs
 */
public cbit.vcell.modelopt.ReferenceDataMappingSpec[] getReferenceDataMappingSpecs() {
	return fieldReferenceDataMappingSpecs;
}


/**
 * Gets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @return The simulationContext property value.
 * @see #setSimulationContext
 */
public cbit.vcell.modelapp.SimulationContext getSimulationContext() {
	return fieldSimulationContext;
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
 * Creation date: (8/22/2005 10:35:28 AM)
 */
private void initializeParameterMappingSpecs() throws org.vcell.expression.ExpressionException {
	cbit.vcell.model.Parameter modelParameters[] = getModelParameters();

	ParameterMappingSpec[] parameterMappingSpecs = new ParameterMappingSpec[modelParameters.length];
	
	java.util.Vector issueList = new java.util.Vector();
	getSimulationContext().gatherIssues(issueList);
	getSimulationContext().getModel().gatherIssues(issueList);
	org.vcell.util.Issue[] issues = (org.vcell.util.Issue[])org.vcell.util.BeanUtils.getArray(issueList,org.vcell.util.Issue.class);
	
	for (int i = 0; i < parameterMappingSpecs.length; i++){
		parameterMappingSpecs[i] = new ParameterMappingSpec(modelParameters[i]);
		for (int j = 0; j < issues.length; j++){
			if (issues[j].getSource() == modelParameters[i]){
				if (issues[j] instanceof cbit.vcell.model.SimpleBoundsIssue){
					cbit.vcell.model.SimpleBoundsIssue simpleBoundsIssue = (cbit.vcell.model.SimpleBoundsIssue)issues[j];
					net.sourceforge.interval.ia_math.RealInterval bounds = simpleBoundsIssue.getBounds();
					parameterMappingSpecs[i].setLow(bounds.lo());
					parameterMappingSpecs[i].setHigh(bounds.hi());
				}
			}
		}
	}

	this.fieldParameterMappingSpecs = parameterMappingSpecs;
}


/**
 * Insert the method's description here.
 * Creation date: (12/19/2005 3:20:34 PM)
 */
public void refreshDependencies() {
	System.out.println("ModelOptimizationSpec.refreshDependencies() not yet implemented");
}


/**
 * Insert the method's description here.
 * Creation date: (8/22/2005 10:35:28 AM)
 */
private void refreshReferenceDataMappingSpecs() {
	if (fieldReferenceData == null || fieldReferenceData.getColumnNames()==null || fieldReferenceData.getColumnNames().length == 0){
		setReferenceDataMappingSpecs(null);
	}

	String refDataColumnNames[] = fieldReferenceData.getColumnNames();

	//
	// 1. create new mappingSpecs array (one new mappingSpec for each column in referenceData)
	// 2. copy existing mappings (model objects from previous mappingSpec) if already mapped.
	//
	ReferenceDataMappingSpec[] newReferenceDataMappingSpecs = new ReferenceDataMappingSpec[getReferenceData().getColumnNames().length];
	int numUnmapped = 0;
	for (int i = 0; i < newReferenceDataMappingSpecs.length; i++){
		newReferenceDataMappingSpecs[i] = new ReferenceDataMappingSpec(refDataColumnNames[i]);
		for (int j = 0; fieldReferenceDataMappingSpecs!=null && j < fieldReferenceDataMappingSpecs.length; j++){
			if (fieldReferenceDataMappingSpecs[j].getReferenceDataColumnName().equals(refDataColumnNames[i])){
				try {
					newReferenceDataMappingSpecs[i].setModelObject(fieldReferenceDataMappingSpecs[j].getModelObject());
				}catch (java.beans.PropertyVetoException e){
					e.printStackTrace(System.out);
					System.out.println("exception consumed, refresh mapping");
				}
			}
		}
		if (newReferenceDataMappingSpecs[i].getModelObject()==null){
			numUnmapped++;
		}
	}
	//
	// 3. for referenceData columns that remain unmapped, see if there are obvious mappings
	//
	if (numUnmapped>0){
		SymbolTableEntry[] stes = calculateTimeDependentModelObjects();
		for (int i = 0; i < newReferenceDataMappingSpecs.length; i++){
			if (newReferenceDataMappingSpecs[i].getModelObject()==null){
				for (int j = 0;stes!=null && j < stes.length; j++){
					if (stes[j].getName().equals(newReferenceDataMappingSpecs[i].getReferenceDataColumnName())){
						try {
							newReferenceDataMappingSpecs[i].setModelObject(stes[j]);
						}catch (java.beans.PropertyVetoException e){
							e.printStackTrace(System.out);
							System.out.println("exception consumed, refresh mapping");
						}
					}
				}
			}
		}
	}
	
	
	setReferenceDataMappingSpecs(newReferenceDataMappingSpecs);
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
 * Sets the parameterMapping property (cbit.vcell.modelopt.ParameterMapping[]) value.
 * @param parameterMapping The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getParameterMapping
 */
public void setParameterMappingSpecs(cbit.vcell.modelopt.ParameterMappingSpec[] parameterMappingSpecs) throws java.beans.PropertyVetoException {
	cbit.vcell.modelopt.ParameterMappingSpec[] oldValue = fieldParameterMappingSpecs;
	fireVetoableChange("parameterMappingSpecs", oldValue, parameterMappingSpecs);
	fieldParameterMappingSpecs = parameterMappingSpecs;
	firePropertyChange("parameterMappingSpecs", oldValue, parameterMappingSpecs);
}


/**
 * Sets the constraintData property (cbit.vcell.opt.ConstraintData) value.
 * @param constraintData The new value for the property.
 * @see #getConstraintData
 */
public void setReferenceData(cbit.vcell.opt.ReferenceData referenceData) {
	cbit.vcell.opt.ReferenceData oldValue = fieldReferenceData;
	fieldReferenceData = referenceData;

	refreshReferenceDataMappingSpecs();
	
	firePropertyChange("referenceData", oldValue, referenceData);
}


/**
 * Sets the referenceDataMappingSpecs property (cbit.vcell.modelopt.ReferenceDataMappingSpec[]) value.
 * @param referenceDataMappingSpecs The new value for the property.
 * @see #getReferenceDataMappingSpecs
 */
private void setReferenceDataMappingSpecs(cbit.vcell.modelopt.ReferenceDataMappingSpec[] referenceDataMappingSpecs) {
	cbit.vcell.modelopt.ReferenceDataMappingSpec[] oldValue = fieldReferenceDataMappingSpecs;
	fieldReferenceDataMappingSpecs = referenceDataMappingSpecs;
	firePropertyChange("referenceDataMappingSpecs", oldValue, referenceDataMappingSpecs);
}
}