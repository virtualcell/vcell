package org.vcell.modelapp;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.parser.*;
import cbit.vcell.model.*;
import cbit.vcell.math.VCML;
import cbit.vcell.units.VCUnitDefinition;
import java.beans.PropertyVetoException;
import cbit.util.*;

import java.io.*;

public class ReactionSpec implements ScopedSymbolTable, Matchable, Serializable, java.beans.VetoableChangeListener {
	private ReactionStep reactionStep = null;

	public final static int INCLUDED = 0;
	public final static int EXCLUDED = 1;
	public final static int FAST = 2;
	public final static int MOLECULAR_ONLY = 3;
	public final static int CURRENT_ONLY = 4;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private int fieldReactionMapping = INCLUDED;
	private final static String[] mappings = {"included", "excluded", "fast", "molecular-only", "current-only"};
	
	
	private ReactionSpecParameter[] fieldReactionSpecParameters = new ReactionSpecParameter[0];
	private ReactionSpecNameScope nameScope = new ReactionSpecNameScope();
	private transient SimulationContext fieldSimulationContext = null;

	
	public static final int ROLE_NominalRate			= 0;
	public static final int ROLE_OverridenRate			= 1;
	public static final int NUM_ROLES		= 2;
	
	public static final String RoleNames[] = {
		"J",
		"J_forced",
	};
	public static final String RoleDescriptions[] = {
		"nominal kinetic rate",
		"forced rate",
	};

	public class ReactionSpecNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public ReactionSpecNameScope(){
			super();
		}
		public cbit.vcell.parser.NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return "mapping_"+ReactionSpec.this.getReactionStep().getName();
		}
		public cbit.vcell.parser.NameScope getParent() {
			if (ReactionSpec.this.fieldSimulationContext != null){
				return ReactionSpec.this.fieldSimulationContext.getNameScope();
			}else{
				return null;
			}
		}
		public cbit.vcell.parser.ScopedSymbolTable getScopedSymbolTable() {
			return ReactionSpec.this;
		}
	}

	public class ReactionSpecParameter extends cbit.vcell.model.Parameter {
		private Expression fieldParameterExpression = null;
		private String fieldParameterName = null;
 		private int fieldParameterRole = -1;
 		private cbit.vcell.units.VCUnitDefinition fieldUnitDefinition = null;

		public ReactionSpecParameter(String parmName, cbit.vcell.parser.Expression argExpression, int argRole, cbit.vcell.units.VCUnitDefinition argUnitDefinition, String argDescription) {
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

		public boolean compareEqual(cbit.util.Matchable obj) {
			if (!(obj instanceof ReactionSpecParameter)){
				return false;
			}
			ReactionSpecParameter smp = (ReactionSpecParameter)obj;
			if (!super.compareEqual0(smp)){
				return false;
			}
			if (fieldParameterRole != smp.fieldParameterRole){
				return false;
			}
			
			return true;
		}

		public NameScope getNameScope(){
			return ReactionSpec.this.getNameScope();
		}

		public boolean isExpressionEditable(){
			return true;
		}

		public boolean isUnitEditable(){
			return false;
		}

		public boolean isNameEditable(){
			return false;
		}

		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}
		
		public void setExpression(Expression expression) throws PropertyVetoException, ExpressionBindingException {
			if (expression!=null){
				expression = new Expression(expression);
				expression.bindExpression(ReactionSpec.this);
			}
			Expression oldValue = fieldParameterExpression;
			super.fireVetoableChange("expression", oldValue, expression);
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}
		
		public double getConstantValue() throws cbit.vcell.parser.ExpressionException {
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
	}

public ReactionSpec(ReactionSpec argReactionSpec) {
	setReactionStep(argReactionSpec.reactionStep);
	this.fieldReactionMapping = argReactionSpec.fieldReactionMapping;
	refreshDependencies();
}            


public ReactionSpec(ReactionStep argReactionStep) {
	setReactionStep(argReactionStep) ;
	refreshDependencies();
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
 * This method was created in VisualAge.
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {

	ReactionSpec reactionSpec = null;
	if (!(object instanceof ReactionSpec)){
		return false;
	}
	reactionSpec = (ReactionSpec)object;

	if (!reactionStep.compareEqual(reactionSpec.reactionStep)){
		return false;
	}
	
	if (fieldReactionMapping != reactionSpec.fieldReactionMapping){
		return false;
	}

	return true;
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
 * Creation date: (11/1/2005 10:06:04 AM)
 * @param issueList java.util.Vector
 */
public void gatherIssues(java.util.Vector issueList) {}


/**
 * getEntry method comment.
 */
public cbit.vcell.parser.SymbolTableEntry getEntry(java.lang.String identifierString) throws cbit.vcell.parser.ExpressionBindingException {

	//
	// look locally (in the ReactionSpec) to resolve identifier
	//	
	cbit.vcell.parser.SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}

	
	//
	// (SPECIAL CASE) see if corresponding ReactionStep "rate" matches this identifier.
	//	
	SymbolTableEntry reactionStepSTE = getReactionStep().getLocalEntry(identifierString);
	if (reactionStepSTE instanceof Kinetics.KineticsParameter && ((Kinetics.KineticsParameter)reactionStepSTE).getRole()==Kinetics.ROLE_Rate){
		return reactionStepSTE;
	}

	//
	// travel in namespace
	//
	ste = getNameScope().getExternalEntry(identifierString);

	return ste;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 11:46:37 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public cbit.vcell.parser.SymbolTableEntry getLocalEntry(java.lang.String identifier) throws cbit.vcell.parser.ExpressionBindingException {
	cbit.vcell.parser.SymbolTableEntry ste = null;

	ste = ReservedSymbol.fromString(identifier);
	if (ste!=null){
		return ste;
	}

	ste = getReactionSpecParameterFromName(identifier);
	if (ste!=null){
		return ste;
	}

	//
	// from within ReactionSpec, SimulationContext locally scoped parameters appear without Scoped notation (no '.')
	//	
	SymbolTableEntry simContextSTE = fieldSimulationContext.getLocalEntry(identifier);
	if (simContextSTE != null){
		return simContextSTE;
	}

	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 11:46:37 AM)
 * @return cbit.vcell.parser.NameScope
 */
public cbit.vcell.parser.NameScope getNameScope() {
	return nameScope;
}


/**
 * This method was created in VisualAge.
 * @return cbit.vcell.parser.Expression
 */
public ReactionSpec.ReactionSpecParameter getNominalRateParameter() {
	return getReactionSpecParameterFromRole(ROLE_NominalRate);		
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public ReactionSpec.ReactionSpecParameter getOverridenRateParameter() {
	return getReactionSpecParameterFromRole(ROLE_OverridenRate);		
}


/**
 * This method returns the string value of the reactionMapping value.
 * Creation date: (1/24/01 1:21:43 PM)
 * @return int
 */
public String[] getPossibleReactionMappings() {
	if (reactionStep instanceof FluxReaction){
		return new String[] { mappings[INCLUDED], mappings[EXCLUDED], mappings[MOLECULAR_ONLY], mappings[CURRENT_ONLY] };
	}else{
		return new String[] { mappings[INCLUDED], mappings[EXCLUDED], mappings[FAST] };
	}
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
 * Gets the reactionMapping property (int) value.
 * @return The reactionMapping property value.
 * @see #setReactionMapping
 */
public int getReactionMapping() {
	return fieldReactionMapping;
}


/**
 * This method returns the string value of the reactionMapping value.
 * Creation date: (1/24/01 1:21:43 PM)
 * @return int
 */
public String getReactionMappingDescription() {
	return this.mappings[getReactionMapping()];
}


/**
 * This method returns the string value of the reactionMapping value.
 * Creation date: (1/24/01 1:21:43 PM)
 * @return int
 */
public static String[] getReactionMappings_Flux() {
	return new String[] { mappings[INCLUDED], mappings[EXCLUDED], mappings[MOLECULAR_ONLY], mappings[CURRENT_ONLY] };
}

/**
 * This method returns the string value of the reactionMapping value.
 * Creation date: (1/24/01 1:21:43 PM)
 * @return int
 */
public static String[] getReactionMappings_NonFlux() {
	return new String[] { mappings[INCLUDED], mappings[EXCLUDED], mappings[FAST] };
}

/**
 * Insert the method's description here.
 * Creation date: (4/3/2004 10:48:38 AM)
 * @return cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
 * @param role int
 */
public ReactionSpec.ReactionSpecParameter getReactionSpecParameterFromName(String name) {
	for (int i = 0; i < fieldReactionSpecParameters.length; i++){
		if (fieldReactionSpecParameters[i].getName().equals(name)){
			return fieldReactionSpecParameters[i];
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
public ReactionSpec.ReactionSpecParameter getReactionSpecParameterFromRole(int role) {
	for (int i = 0; i < fieldReactionSpecParameters.length; i++){
		if (fieldReactionSpecParameters[i].getRole() == role){
			return fieldReactionSpecParameters[i];
		}
	}
	return null;
}


/**
 * Gets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @return The parameters property value.
 * @see #setParameters
 */
public ReactionSpecParameter[] getReactionSpecParameters() {
	return fieldReactionSpecParameters;
}


/**
 * Gets the parameters index property (cbit.vcell.model.Parameter) value.
 * @return The parameters property value.
 * @param index The index value into the property array.
 * @see #setParameters
 */
public ReactionSpecParameter getReactionSpecParameters(int index) {
	return getReactionSpecParameters()[index];
}


/**
 * Insert the method's description here.
 * Creation date: (1/24/01 1:50:51 PM)
 * @return cbit.vcell.model.ReactionStep
 */
public ReactionStep getReactionStep() {
	return reactionStep;
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
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isExcluded() {
	return fieldReactionMapping == EXCLUDED;
}


/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean isFast() {
	return fieldReactionMapping == FAST;
}


/**
 * This method was created in VisualAge.
 */
public void refreshDependencies() {
	removeVetoableChangeListener(this);
	addVetoableChangeListener(this);
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
 * Sets the reactionMapping property (int) value.
 * @param reactionMapping The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getReactionMapping
 */
public void setReactionMapping(int reactionMapping) throws java.beans.PropertyVetoException {
	int oldValue = fieldReactionMapping;
	fireVetoableChange("reactionMapping", new Integer(oldValue), new Integer(reactionMapping));
	fieldReactionMapping = reactionMapping;
	firePropertyChange("reactionMapping", new Integer(oldValue), new Integer(reactionMapping));
}


/**
 * Sets the reactionMapping property (String) value.
 * @param reactionMapping The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getReactionMapping
 */
public void setReactionMapping(String reactionMapping) throws java.beans.PropertyVetoException{
	for (int i=0; i<this.mappings.length; i++) {
		if ( mappings[i].equalsIgnoreCase(reactionMapping) ) {
			setReactionMapping(i);
			return;	//end execution
		}
	}
	//in case of an unknown argument throw an exception
	throw new IllegalArgumentException("An unknown string ReactionMapping value '" + reactionMapping + "', value was found!");
}

/**
 * Sets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @param parameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getParameters
 */
private void setReactionSpecParameters(ReactionSpecParameter[] reactionSpecParameters) throws java.beans.PropertyVetoException {
	ReactionSpecParameter[] oldValue = fieldReactionSpecParameters;
	fireVetoableChange("reactionSpecParameters", oldValue, reactionSpecParameters);
	fieldReactionSpecParameters = reactionSpecParameters;
	firePropertyChange("reactionSpecParameters", oldValue, reactionSpecParameters);
}


/**
 * Insert the method's description here.
 * Creation date: (2/14/2002 5:07:05 PM)
 * @param rs cbit.vcell.model.ReactionStep
 */
void setReactionStep(ReactionStep rs) {
	this.reactionStep = rs;	
}


/**
 * Sets the simulationContext property (cbit.vcell.mapping.SimulationContext) value.
 * @param simulationContext The new value for the property.
 */
public void setSimulationContext(SimulationContext simulationContext) {
	fieldSimulationContext = simulationContext;
}


/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer sb = new StringBuffer();
	
	sb.append(getClass().getName()+"\n");
   if (reactionStep != null) { sb.append(":'"+reactionStep.getName()+"' ("+getReactionMappingDescription()+")\n"); }
 	
	return sb.toString();
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
	if (evt.getSource() == this && evt.getPropertyName().equals("reactionMapping")){
		if (((Integer)evt.getNewValue()).intValue() == FAST && getReactionStep() instanceof FluxReaction){
			throw new java.beans.PropertyVetoException("flux reaction cannot be '"+mappings[FAST]+"'",evt);
		}
		if (((Integer)evt.getNewValue()).intValue() == MOLECULAR_ONLY && !(getReactionStep() instanceof FluxReaction)){
			throw new java.beans.PropertyVetoException("only flux reactions can be mapped as '"+mappings[MOLECULAR_ONLY]+"'",evt);
		}
		if (((Integer)evt.getNewValue()).intValue() == CURRENT_ONLY && !(getReactionStep() instanceof FluxReaction)){
			throw new java.beans.PropertyVetoException("only flux reactions can be mapped as '"+mappings[CURRENT_ONLY]+"'",evt);
		}
	}
}
}