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
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.List;
import java.util.Map;

import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.mapping.SimulationContext.Kind;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ExpressionContainer;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

public class ReactionSpec implements ModelProcessSpec, ScopedSymbolTable, VetoableChangeListener, SimulationContextEntity {
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
	private final SimulationContext fieldSimulationContext;

	
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
		public NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return "mapping_"+ReactionSpec.this.getReactionStep().getName();
		}
		public NameScope getParent() {
			if (ReactionSpec.this.fieldSimulationContext != null){
				return ReactionSpec.this.fieldSimulationContext.getNameScope();
			}else{
				return null;
			}
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return ReactionSpec.this;
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.reactionSpecType;
		}
	}
	public class ReactionCombo implements IssueSource {	// used only for Issue reporting stuff
		final ReactionSpec rs;
		final ReactionContext rc;
		
		public ReactionCombo(ReactionSpec rs, ReactionContext rc) {
			this.rs = rs;
			this.rc = rc;
		}
		public ReactionSpec getReactionSpec() {
			return rs;
		}
		public ReactionContext getReactionContext() {
			return rc;
		}
	}

	public class ReactionSpecParameter extends Parameter implements ExpressionContainer {
		private Expression fieldParameterExpression = null;
		private String fieldParameterName = null;
 		private int fieldParameterRole = -1;
 		private VCUnitDefinition fieldUnitDefinition = null;

		public ReactionSpecParameter(String parmName, Expression argExpression, int argRole, VCUnitDefinition argUnitDefinition, String argDescription) {
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

		public boolean compareEqual(Matchable obj) {
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

		public VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}
		
		public void setExpression(Expression expression) throws ExpressionBindingException {
			if (expression!=null){
				expression = new Expression(expression);
				expression.bindExpression(ReactionSpec.this);
			}
			Expression oldValue = fieldParameterExpression;
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}
		
		public double getConstantValue() throws ExpressionException {
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

	public ReactionSpec(ReactionSpec argReactionSpec, SimulationContext simContext) {
		if (simContext == null){
			throw new IllegalArgumentException("simContext cannot be null in ReactionSpec");
		}
		setReactionStep(argReactionSpec.reactionStep);
		this.fieldReactionMapping = argReactionSpec.fieldReactionMapping;
		this.fieldSimulationContext = simContext;
		refreshDependencies();
	}            


	public ReactionSpec(ReactionStep argReactionStep, SimulationContext simContext) {
		if (simContext == null){
			throw new IllegalArgumentException("simContext cannot be null in ReactionSpec");
		}
		setReactionStep(argReactionStep);
		this.fieldSimulationContext = simContext;
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
public void gatherIssues(IssueContext issueContext, List<Issue> issueList, ReactionContext rc) {
	ReactionCombo r = new ReactionCombo(this, rc);
	ReactionStep step = getReactionStep();
	if(!isExcluded() && rc.getSimulationContext().isStoch() && (rc.getSimulationContext().getGeometry().getDimension()>0)) {
		boolean haveParticle = false;
		boolean haveContinuous = false;
		for(ReactionParticipant p : step.getReactionParticipants()) {
			if(p instanceof Product || p instanceof Reactant) {
				SpeciesContextSpec candidate = rc.getSpeciesContextSpec(p.getSpeciesContext());
				if(candidate.isForceContinuous() && !candidate.isConstant()) {
					haveParticle = true;
				} 
				else if(!candidate.isForceContinuous() && !candidate.isConstant()) {
					haveContinuous = true;
				} 
			}
		}
		if(haveParticle && haveContinuous) {
			String msg = "Reaction " + step.getName() + " has both continuous and particle Participants.";
			String tip = "Mass conservation for reactions of binding between discrete and continuous species is handled approximately. <br>" +
					"To avoid any algorithmic approximation, which may produce undesired results, the user is advised to indicate <br>" +
					"the continuous species in those reactions as modifiers (i.e. 'catalysts') in the physiology.";
			issueList.add(new Issue(r, issueContext, IssueCategory.Identifiers, msg, tip, Issue.SEVERITY_WARNING));
		}
	}
}

/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(java.lang.String identifierString) {

	//
	// look locally (in the ReactionSpec) to resolve identifier
	//	
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}

	
	//
	// travel in namespace
	//
	ste = getNameScope().getExternalEntry(identifierString,this);

	return ste;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 11:46:37 AM)
 * @return cbit.vcell.parser.SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getLocalEntry(java.lang.String identifier)  {
	SymbolTableEntry ste = getReactionSpecParameterFromName(identifier);
	if (ste!=null){
		return ste;
	}

	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 11:46:37 AM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return nameScope;
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
	return mappings[getReactionMapping()];
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
	for (int i=0; i<mappings.length; i++) {
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


	public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
		
		for (SymbolTableEntry ste : fieldReactionSpecParameters) {
			entryMap.put(ste.getName(), ste);
		}
	}


	public void getEntries(Map<String, SymbolTableEntry> entryMap) {
		getNameScope().getExternalEntries(entryMap);		
	}


	public boolean hasHybrid(SimulationContext simContext, SpeciesContext speciesContext) {
		//
		// see if this speciesContext is involved in this reaction
		//
		if (!simContext.isStoch()){
			return false;
		}
		
		boolean bHasForcedContinuous = false;
		boolean bHasStochastic = false;
		boolean bContainsSpeciesContext = false;
		for (ReactionParticipant rp : getReactionStep().getReactionParticipants()){
			if (rp.getSpeciesContext() == speciesContext){
				bContainsSpeciesContext = true;
			}
			SpeciesContextSpec scs = simContext.getReactionContext().getSpeciesContextSpec(rp.getSpeciesContext());
			if (scs.isForceContinuous()){
				bHasForcedContinuous = true;
			}else{
				bHasStochastic = true;
			}
		}
		
		return bContainsSpeciesContext && bHasForcedContinuous && bHasStochastic;
	}


	@Override
	public ReactionStep getModelProcess() {
		return reactionStep;
	}


	@Override
	public Kind getSimulationContextKind() {
		return SimulationContext.Kind.SPECIFICATIONS_KIND;
	}
}
