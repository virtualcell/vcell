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
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Displayable;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.Issue.IssueSource;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ExpressionContainer;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProxyParameter;
import cbit.vcell.model.RbmKineticLaw;
import cbit.vcell.model.SimpleBoundsIssue;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.AbstractNameScope;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;
import cbit.vcell.parser.VCUnitEvaluator;
import cbit.vcell.units.UnitSystemProvider;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitException;
import cbit.vcell.units.VCUnitSystem;
import net.sourceforge.interval.ia_math.RealInterval;

public class ParameterContext implements Matchable, ScopedSymbolTable, Serializable {

	private final static Logger logger = LogManager.getLogger(ParameterContext.class);
	public interface ParameterRoleEnum {
		// uses == semantics, must be implemented by an enumeration
		String getDescription();
	}
	
	public interface ParameterPolicy {
		public boolean isUserDefined(LocalParameter localParameter);

		public boolean isExpressionEditable(LocalParameter localParameter);

		public boolean isUnitEditable(LocalParameter localParameter);

		public boolean isNameEditable(LocalParameter localParameter);

		public ParameterRoleEnum getUserDefinedRole();

		public IssueSource getIssueSource();

		public RealInterval getConstraintBounds(ParameterRoleEnum role);
	}

	public class LocalParameter extends Parameter implements ExpressionContainer, IssueSource {
		private Expression fieldParameterExpression = null;
		private String fieldParameterName = null;
 		private final ParameterRoleEnum fieldParameterRole;
 		private VCUnitDefinition fieldUnitDefinition = null;

		public LocalParameter(String parmName, Expression argExpression, ParameterRoleEnum argRole, VCUnitDefinition argUnitDefinition, String argDescription) {
			super();
			fieldParameterName = parmName;
			fieldParameterExpression = argExpression;
			if (argRole != null){
				this.fieldParameterRole = argRole;
			}else{
				throw new IllegalArgumentException("parameter \""+parmName+"\" 'role' not set");
			}
			fieldUnitDefinition = argUnitDefinition;
			setDescription(argDescription);
		}

		@Override
		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof LocalParameter)){
				return false;
			}
			LocalParameter smp = (LocalParameter)obj;
			if (!super.compareEqual0(smp)){
				return false;
			}
			if (fieldParameterRole != smp.fieldParameterRole){
				return false;
			}
			
			return true;
		}

		@Override
		public NameScope getNameScope(){
			return ParameterContext.this.getNameScope();
		}

		@Override
		public boolean isExpressionEditable() {
			return parameterPolicy.isExpressionEditable(this);
		}

		@Override
		public boolean isUnitEditable() {
			return parameterPolicy.isUnitEditable(this);
		}

		@Override
		public boolean isNameEditable() {
			return parameterPolicy.isNameEditable(this);
		}

		@Override
		public VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}
		
		@Override
		public void setExpression(Expression expression) throws ExpressionBindingException {
			if (expression!=null){
				expression = new Expression(expression);
				expression.bindExpression(ParameterContext.this);
			}
			Expression oldValue = fieldParameterExpression;
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}
		
		@Override
		public double getConstantValue() throws ExpressionException {
			return fieldParameterExpression.evaluateConstant();
		}
		
		@Override
		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = getName();
			if(name.equals(oldValue)) {
				return;
			}
			super.fireVetoableChange("name", oldValue, name);
			try {
				renameLocalParameter(getName(), name);
			} catch (ExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to change parameter "+oldValue+" to "+name+": "+e.getMessage(),e);
			}
			super.firePropertyChange("name", oldValue, name);
		}
		
		@Override
		public void setUnitDefinition(VCUnitDefinition unitDefinition) {
			VCUnitDefinition oldValue = fieldUnitDefinition;
			fieldUnitDefinition = unitDefinition;
			super.firePropertyChange("unitDefinition", oldValue, unitDefinition);
		}
		
		@Override
		public String getName(){
			return fieldParameterName;
		}
		
		@Override
		public Expression getExpression(){
			return fieldParameterExpression;
		}
		
		@Override
		public int getIndex() { // used for evaluation evaluateVector(double[])
			return -1;
		}

		public ParameterRoleEnum getRole() {
			return fieldParameterRole;
		}
	}
	
	public class UnresolvedParameter extends Parameter implements IssueSource {
		
		private String fieldParameterName = null;

		protected UnresolvedParameter(String argName) {
			if (argName == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argName.length()<1){
				throw new IllegalArgumentException("parameter name is zero length");
			}
			this.fieldParameterName = argName;
			setDescription("unresolved");
		}

		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof UnresolvedParameter)){
				return false;
			}
			UnresolvedParameter up = (UnresolvedParameter)obj;
			if (!super.compareEqual0(up)){
				return false;
			}			
			return true;
		}

		public double getConstantValue() throws ExpressionException {
			throw new ExpressionException("no expression defined for UnresolvedParameter '"+fieldParameterName+"'");
		}      

		public Expression getExpression() {
			return null;
		}

		public boolean isExpressionEditable(){
			return false;
		}

		public boolean isNameEditable(){
			return false;
		}

		public boolean isUnitEditable(){
			return false;
		}

		public void setExpression(Expression expression) throws ExpressionBindingException {
			throw new RuntimeException("expression is not editable");
		}

		public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException {
			throw new RuntimeException("unit is not editable");
		}
		
		public int getIndex() {
			return -1;
		}

		public String getName(){ 
			return this.fieldParameterName; 
		}

		public VCUnitDefinition getUnitDefinition() {
			return unitSystemProvider.getUnitSystem().getInstance_TBD();
		}

		public NameScope getNameScope() {
			return nameScope;
		}

		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}

		public boolean isDescriptionEditable() {
			return false;
		}
	}
		
	public class LocalProxyParameter extends ProxyParameter {

		public LocalProxyParameter(SymbolTableEntry target){
			super(target);
		}
		
		public NameScope getNameScope(){
			return ParameterContext.this.getNameScope();
		}

		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof LocalProxyParameter)){
				return false;
			}
			LocalProxyParameter other = (LocalProxyParameter)obj;
			if (getTarget() instanceof Matchable && other.getTarget() instanceof Matchable &&
				Compare.isEqual((Matchable)getTarget(), (Matchable)other.getTarget())){
				return true;
			}else{
				return false;
			}
		}

		
		@Override
		public String getDescription() {
			if (getTarget() instanceof SpeciesContext) {
				return "Species Concentration";
			} else {
				return super.getDescription();
			}
		}

		@Override
		public void targetPropertyChange(PropertyChangeEvent evt) {
			super.targetPropertyChange(evt);
			if (evt.getPropertyName().equals("name")){
				String oldName = (String)evt.getOldValue();
				String newName = (String)evt.getNewValue();
				try {
					LocalParameter newParameters[] = new LocalParameter[fieldParameters.length];
					System.arraycopy(fieldParameters, 0, newParameters, 0, fieldParameters.length);
					//
					// go through all parameters' expressions and replace references to 'oldName' with 'newName'
					//
					for (int i = 0; i < newParameters.length; i++){ 
						Expression exp = ParameterContext.this.fieldParameters[i].getExpression();
						if (exp != null) {
							newParameters[i].setExpression(exp.renameBoundSymbols(getNameScope()));
						}
					}
					setLocalParameters(newParameters);
	
					// 
					// rebind all expressions
					//
					for (int i = 0; i < newParameters.length; i++){
						if (newParameters[i].getExpression() != null) {
							newParameters[i].getExpression().bindExpression(ParameterContext.this);
						}
					}
					
				}catch (ExpressionException | PropertyVetoException e2){
					logger.error("property change failed", e2);
				}
			} 
		} 
		
	}

	private LocalParameter[] fieldParameters = new LocalParameter[0];
	private LocalProxyParameter[] fieldProxyParameters = new LocalProxyParameter[0];
	private UnresolvedParameter[] fieldUnresolvedParameters = new UnresolvedParameter[0];
	private BioNameScope nameScope = null;
	private ParameterPolicy parameterPolicy = null;
	private UnitSystemProvider unitSystemProvider = null;

	protected transient java.beans.PropertyChangeSupport propertyChange;
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	private transient boolean bResolvingUnits = false;
	private transient boolean bReading = false;
	
	private class MyPropertyChangeListener implements PropertyChangeListener, Serializable {
		
		public void propertyChange(PropertyChangeEvent event) {
			try {
				if (event.getSource() == this && event.getPropertyName().equals("localParameters")){
					LocalParameter oldValues[] = (LocalParameter[])event.getOldValue();
					for (int i = 0; oldValues!=null && i < oldValues.length; i++){
						oldValues[i].removePropertyChangeListener(this);
					}
					LocalParameter newValues[] = (LocalParameter[])event.getNewValue();
					for (int i = 0; newValues != null && i < newValues.length; i++){
						newValues[i].addPropertyChangeListener(this);
					}
					cleanupParameters();
					resolveUndefinedUnits();
				}
				if (event.getSource() instanceof LocalParameter){
					cleanupParameters();
					resolveUndefinedUnits();
				}
			}catch (Throwable e){
				logger.error("property change failed", e);
			}
		}
	};
	private MyPropertyChangeListener listener = new MyPropertyChangeListener();
	
public ParameterContext(BioNameScope bioNameScope, ParameterPolicy parameterPolicy, UnitSystemProvider argUnitSystemProvider) {
	this.nameScope = bioNameScope;
	this.parameterPolicy = parameterPolicy;
	this.unitSystemProvider = argUnitSystemProvider;
	addPropertyChangeListener(listener);
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

public LocalProxyParameter addProxyParameter(SymbolTableEntry symbolTableEntry) {
	if (getLocalParameterFromName(symbolTableEntry.getName())!=null){
		throw new RuntimeException("local parameter '"+symbolTableEntry.getName()+"' already exists");
	}
	if (getProxyParameter(symbolTableEntry.getName())!=null){
		throw new RuntimeException("referenced external symbol '"+symbolTableEntry.getName()+"' already exists");
	}
	LocalProxyParameter newProxyParameter = new LocalProxyParameter(symbolTableEntry);
	LocalProxyParameter newProxyParameters[] = (LocalProxyParameter[])BeanUtils.addElement(fieldProxyParameters,newProxyParameter);
	setProxyParameters(newProxyParameters);
	return newProxyParameter;
}

public UnresolvedParameter addUnresolvedParameter(String name){
	if (getLocalParameterFromName(name)!=null){
		throw new RuntimeException("local parameter '"+name+"' already exists");
	}
	if (getProxyParameter(name)!=null){
		throw new RuntimeException("referenced external symbol '"+name+"' already exists");
	}
	UnresolvedParameter newUnresolvedParameter = new UnresolvedParameter(name);
	UnresolvedParameter newUnresolvedParameters[] = (UnresolvedParameter[])BeanUtils.addElement(fieldUnresolvedParameters,newUnresolvedParameter);
	setUnresolvedParameters(newUnresolvedParameters);
	return newUnresolvedParameter;
}

private void setUnresolvedParameters(UnresolvedParameter[] unresolvedParameters) {
	UnresolvedParameter[] oldValue = fieldUnresolvedParameters;
	fieldUnresolvedParameters = unresolvedParameters;
	firePropertyChange("unresolvedParameters", oldValue, unresolvedParameters);
}


/**
 * @return boolean
 * @param object java.lang.Object
 */
public boolean compareEqual(Matchable object) {

	ParameterContext pc = null;
	if (!(object instanceof ParameterContext)){
		return false;
	}
	pc = (ParameterContext)object;

	if (!Compare.isEqual(fieldParameters,pc.fieldParameters)){
		return false;
	}

	return true;
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
public void fireVetoableChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) throws java.beans.PropertyVetoException {
	getVetoPropertyChange().fireVetoableChange(propertyName, oldValue, newValue);
}

/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(String identifierString) {
	
	SymbolTableEntry localSTE = getLocalEntry(identifierString);
	if (localSTE != null && !(localSTE instanceof UnresolvedParameter)){
		return localSTE;
	}
	UnresolvedParameter unresolvedParameter = (UnresolvedParameter)localSTE; // may be null.
			
	SymbolTableEntry externalSTE = getNameScope().getExternalEntry(identifierString,this);

	if (externalSTE instanceof SymbolTableFunctionEntry){
		return externalSTE;
	}	
	//
	// external ste is null and found unresolved parameter, then return unresolved parameter.  
	// (external entry overrides unresolved parameter).
	//
	if (externalSTE!=null){
		if (unresolvedParameter!=null){
			removeUnresolvedParameters(this);
		}
		return addProxyParameter(externalSTE);
	}else if (unresolvedParameter != null){
		return unresolvedParameter;
	}
	
	//TODO: check for x,y,z for reaction rule usage, see ReactionStep.getEntry()
	
	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (12/8/2003 11:46:37 AM)
 * @return SymbolTableEntry
 * @param identifier java.lang.String
 */
public SymbolTableEntry getLocalEntry(java.lang.String identifier)  {
	SymbolTableEntry ste = getLocalParameterFromName(identifier);
	if (ste!=null){
		return ste;
	}

	ste = getProxyParameter(identifier);
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
 * Insert the method's description here.
 * Creation date: (4/3/2004 10:48:38 AM)
 * @return cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
 * @param role int
 */
public ParameterContext.LocalParameter getLocalParameterFromName(String name) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i].getName().equals(name)){
			return fieldParameters[i];
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
public ParameterContext.LocalParameter getLocalParameterFromRole(ParameterRoleEnum role) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i].getRole() == role){
			return fieldParameters[i];
		}
	}
	return null;
}

/**
 * Gets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @return The parameters property value.
 * @see #setParameters
 */
public LocalParameter[] getLocalParameters() {
	return fieldParameters;
}

public LocalProxyParameter getProxyParameter(String pName){
	if (fieldProxyParameters == null){
		return null;
	}
	for (int i=0;i<fieldProxyParameters.length;i++){
		LocalProxyParameter parm = fieldProxyParameters[i];
		if (pName.equals(parm.getName())){
			return parm;
		}
	}
	return null;
}

public LocalProxyParameter[] getProxyParameters() {
	return fieldProxyParameters;
}

public UnresolvedParameter[] getUnresolvedParameters() {
	return fieldUnresolvedParameters;
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
 * Accessor for the vetoPropertyChange field.
 */
protected java.beans.VetoableChangeSupport getVetoPropertyChange() {
	if (vetoPropertyChange == null) {
		vetoPropertyChange = new java.beans.VetoableChangeSupport(this);
	};
	return vetoPropertyChange;
}


/**
 */
public void refreshDependencies() {
	for (int i = 0; i < fieldParameters.length; i++){
		try {
			if (fieldParameters[i].getExpression()!=null){
				fieldParameters[i].getExpression().bindExpression(this);
			}
		}catch (ExpressionException e){
			logger.error("error binding expression '"+fieldParameters[i].getExpression().infix()+"', "+e.getMessage(), e);
		}
	}
	for (int i = 0; i < fieldParameters.length; i++){
		fieldParameters[i].removePropertyChangeListener(listener);
		fieldParameters[i].addPropertyChangeListener(listener);
	}
	
	//refreshUnits();
	removeUnresolvedParameters(this);
	resolveUndefinedUnits();


}

/**
 * Insert the method's description here.
 * Creation date: (5/24/01 4:05:36 PM)
 */
public void renameLocalParameter(String oldName, String newName) throws ExpressionException, java.beans.PropertyVetoException{
	if (oldName==null || newName==null){
		throw new RuntimeException("renameParameter from '"+oldName+"' to '"+newName+"', nulls are not allowed");
	}
	NameScope nameScope = getNameScope();
	String prefix = AbstractNameScope.getPrefix(newName);
	String strippedName = AbstractNameScope.getStrippedIdentifier(newName);
	if (prefix!=null){
		NameScope prefixNameScope = nameScope.getNameScopeFromPrefix(prefix);
		if (prefixNameScope != nameScope){ // from different namescope, then strip any prefix.
			throw new ExpressionException("reaction parameter cannot be renamed to '"+newName+"', name is scoped to '"+prefixNameScope.getName()+"'");
		}
	}
	newName = strippedName;
	if (oldName.equals(newName)){
		throw new RuntimeException("renameParameter from '"+oldName+"' to '"+newName+"', same name not allowed");
	}
	LocalParameter existingParameter = getLocalParameterFromName(newName);
	if (existingParameter != null) {
		throw new RuntimeException("Parameter '"+newName+"' already exists.");
	}
	
	LocalParameter parameter = getLocalParameterFromName(oldName);
	if (parameter!=null){
		//
		// must change name in ElectricalStimulusParameter directly
		// then change all references to this name in the other parameter's expressions.
		//
		LocalParameter newParameters[] = (LocalParameter[])getLocalParameters().clone();
		//
		// replaces parameter with name 'oldName' with new parameter with name 'newName' and original expression.
		//
		parameter.fieldParameterName = newName;
		
		//
		// go through all parameters' expressions and replace references to 'oldName' with 'newName'
		//
		for (int i = 0; i < newParameters.length; i++){ 
			if (newParameters[i].getExpression()!=null){
				Expression newExp = newParameters[i].getExpression().renameBoundSymbols(getNameScope());
				newParameters[i].setExpression(newExp);
			}
		}
		setLocalParameters(newParameters);
		
		//
		// clean up dangling parameters (those not reachable from the 'required' parameters).
		//
		try {
			cleanupParameters();
		}catch (Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
		parameter.firePropertyChange("name",oldName,newName);
	}
}



public void resolveUndefinedUnits() {
	//
	// try to fix units for UserDefined parameters
	//
	if (!bResolvingUnits){
		VCUnitSystem unitSystem = unitSystemProvider.getUnitSystem();
		try {
			bResolvingUnits = true;
			boolean bAnyTBDUnits = false;
			for (int i=0;i<fieldParameters.length;i++){
				if (fieldParameters[i].getUnitDefinition()==null){
					return; // not ready to resolve units yet
				}else if (fieldParameters[i].getUnitDefinition().isTBD()){
					bAnyTBDUnits = true;
				}
			}
			//
			// try to resolve TBD units (will fail if units are inconsistent) ... but these errors are collected in Kinetics.getIssues().
			//
			if (bAnyTBDUnits){
				VCUnitEvaluator unitEvaluator = new VCUnitEvaluator(unitSystem);
				VCUnitDefinition vcUnitDefinitions[] = unitEvaluator.suggestUnitDefinitions(fieldParameters);
				for (int i = 0; i < fieldParameters.length; i++){
					if (!fieldParameters[i].getUnitDefinition().isEquivalent(vcUnitDefinitions[i])){
						fieldParameters[i].setUnitDefinition(vcUnitDefinitions[i]);
					}
				}
			}
		}catch (ExpressionBindingException e){
			logger.warn("error resolving units: "+e.getMessage());
		}catch (Exception e){
			logger.error("error resolving units: "+e.getMessage(), e);
		}finally{
			bResolvingUnits = false;
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (4/3/2004 11:13:04 AM)
 * @param parameter cbit.vcell.mapping.SpeciesContextSpec.SpeciesContextSpecParameter
 */
private void removeParameter(ParameterContext.LocalParameter parameter) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i] == parameter){
			LocalParameter newParameters[] = (LocalParameter[])BeanUtils.removeElement(fieldParameters,parameter);
			try {
				setLocalParameters(newParameters);
			} catch (PropertyVetoException | ExpressionBindingException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
			return;
		}
	}
	throw new RuntimeException(parameter.getName()+"' not found");
}

protected void removeProxyParameter(LocalProxyParameter parameter) {
	for (int i = 0; i < fieldProxyParameters.length; i++){
		if (fieldProxyParameters[i] == parameter){
			LocalProxyParameter newProxyParameters[] = (LocalProxyParameter[])BeanUtils.removeElement(fieldProxyParameters,parameter);
			setProxyParameters(newProxyParameters);
			return;
		}
	}
	throw new RuntimeException(parameter.getName()+"' not found");
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

void removeUnresolvedParameters(SymbolTable symbolTable) {
	UnresolvedParameter unresolvedParms[] = fieldUnresolvedParameters.clone();
	for (int i = 0; i < unresolvedParms.length; i++){
		SymbolTableEntry ste = symbolTable.getEntry(unresolvedParms[i].getName());
		if (ste != unresolvedParms[i]){
			unresolvedParms = (UnresolvedParameter[])BeanUtils.removeElement(unresolvedParms,unresolvedParms[i]);
			i--;
		}
	}
	setUnresolvedParameters(unresolvedParms);
}

/**
 * This method was created in VisualAge.
 */
final void cleanupParameters() throws ExpressionException, PropertyVetoException {
	if (bReading){
		return;
	}
	//
	// for each parameter, see if it is used, if not delete it
	//
	if (fieldParameters != null){
		for (int i=0;i<fieldParameters.length;i++){
			if (parameterPolicy.isUserDefined(fieldParameters[i]) && !isReferenced(fieldParameters[i],0)){
				removeParameter(fieldParameters[i]);
				i--;
			}
		}
	}
	if (fieldProxyParameters != null){
		for (int i=0;i<fieldProxyParameters.length;i++){
			if (!isReferenced(fieldProxyParameters[i],0)){
				removeProxyParameter(fieldProxyParameters[i]);
				i--;
			}
		}
	}
//	if (fieldUnresolvedParameters != null){
//		for (int i=0;i<fieldUnresolvedParameters.length;i++){
//			if (!isReferenced(fieldUnresolvedParameters[i],0)){
//				removeUnresolvedParameter(fieldUnresolvedParameters[i]);
//				i--;
//			}
//		}
//	}

	for (int i = 0;fieldParameters!=null && i < fieldParameters.length; i++){
		Expression exp = fieldParameters[i].getExpression();
		if (exp!=null){
			try {
				exp.bindExpression(this);
			}catch (ExpressionBindingException e){
				logger.warn("error binding expression '"+exp.infix()+"': "+e.getMessage());
			}
		}
	}
	resolveUndefinedUnits();
}


/**
 * Sets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @param parameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @throws ExpressionBindingException 
 * @see #getParameters
 */
public void setLocalParameters(LocalParameter[] parameters) throws java.beans.PropertyVetoException, ExpressionBindingException {
	LocalParameter[] oldValue = fieldParameters;
	fireVetoableChange("localParameters", oldValue, parameters);
	fieldParameters = parameters;
	for (LocalParameter p : parameters){
		if (p.getExpression()!=null){
			p.getExpression().bindExpression(this);
		}
	}
	firePropertyChange("localParameters", oldValue, parameters);
}

private void setProxyParameters(LocalProxyParameter[] proxyParameters) {
	LocalProxyParameter[] oldValue = fieldProxyParameters;
	fieldProxyParameters = proxyParameters;
	firePropertyChange("proxyParameters", oldValue, proxyParameters);
}


public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {	
	for (SymbolTableEntry ste : fieldProxyParameters) {
		entryMap.put(ste.getName(), ste);
	}
	for (SymbolTableEntry ste : fieldParameters) {
		entryMap.put(ste.getName(), ste);
	}
}


public void getEntries(Map<String, SymbolTableEntry> entryMap) {
	getNameScope().getExternalEntries(entryMap);		
}

public LocalParameter addLocalParameter(String name, Expression exp, ParameterRoleEnum role, VCUnitDefinition unit, String description) throws PropertyVetoException, ExpressionBindingException {
	if (getLocalParameterFromName(name)!=null){
		throw new RuntimeException("local parameter '"+name+"' already exists");
	}
	if (getProxyParameter(name)!=null){
		throw new RuntimeException("referenced external symbol '"+name+"' already exists");
	}
	LocalParameter newLocalParameter = new LocalParameter(name,exp,role,unit,description);
	LocalParameter newParameters[] = (LocalParameter[])BeanUtils.addElement(fieldParameters,newLocalParameter);
	setLocalParameters(newParameters);
	return newLocalParameter;
}

private boolean isReferenced(Parameter parm, int level) throws ExpressionException {
	//
	// check for unbounded recursion (level > 10)
	//
	if (level >= 10){
		throw new RuntimeException("there is a loop in the parameter definitions");
	}
	
	////
	//// if parameter is referenced in rate expression, then it is OK
	////
	if (parm instanceof LocalParameter && !parameterPolicy.isUserDefined((LocalParameter)parm)){
		return true;
	}

	//
	// else, if parameter is referenced in another parameter's expression, continue with that expression
	//
	boolean bReferenced = false;
	if (fieldParameters != null){
		for (int i=0;i<fieldParameters.length;i++){
			Parameter parentParm = fieldParameters[i];
			Expression exp = parentParm.getExpression();
			if (parentParm.getExpression()!=null){
				String[] symbols = exp.getSymbols();
				if (symbols!=null){
					for (int j=0;j<symbols.length;j++){
						if (AbstractNameScope.getStrippedIdentifier(symbols[j]).equals(parm.getName())){
							bReferenced = true;
							if (isReferenced(parentParm,level+1)){
								return true;
							}
						}
					}
				}
			}
		}
	}
	return false;
}

public interface GlobalParameterContext {
	ScopedSymbolTable getSymbolTable();
	Parameter getParameter(String name);
	Parameter addParameter(String name, Expression exp, VCUnitDefinition unit) throws PropertyVetoException;
}

public void convertParameterType(Parameter param, boolean bConvertToGlobal, GlobalParameterContext globalParameterContext) throws PropertyVetoException, ExpressionBindingException {
	Expression expression = param.getExpression();
	if (!bConvertToGlobal) {
		// need to convert model parameter (the proxyparam/global) to local (kinetics) parameter 
		if (!(param instanceof LocalProxyParameter)) {
			throw new RuntimeException("Parameter : \'" + param.getName() + "\' is not a proxy (global) parameter, cannot convert it to a local parameter.");
		} else {
			// first remove proxy param, 
			removeProxyParameter((LocalProxyParameter)param);
			// then add it as local param
			if (expression != null) {
				Expression newExpr = new Expression(expression);
				newExpr.bindExpression(this);
				addLocalParameter(param.getName(), newExpr, parameterPolicy.getUserDefinedRole(), param.getUnitDefinition(), RbmKineticLaw.RbmKineticLawParameterType.UserDefined.getDescription());
			} 
		}
	} else {
		// need to convert local (the kinetics parameter) to model (proxy) parameter
		if (!(param instanceof LocalParameter)) {
			throw new RuntimeException("Parameter : \'" + param.getName() + "\' is not a local parameter, cannot convert it to a global (proxy) parameter.");
		} else {
			// first check if kinetic param is the 'authoritative' kinetic parame; if so, cannot remove it.
			// else remove local param - should have already been checked in parameterTableModel
			// First add param as a model parameter, if it is not already present
			Parameter globalParameter = globalParameterContext.getParameter(param.getName());
			if (globalParameter == null) {
				Expression newExpr = new Expression(expression);
				newExpr.bindExpression(globalParameterContext.getSymbolTable());
				globalParameter = globalParameterContext.addParameter(param.getName(),  newExpr,  param.getUnitDefinition());
			}
			// Then remove param as a kinetic param (if 'param' is a model param, it is automatically added as a (proxy/global) param, 
			// since it is present in the reaction rate equn.
			removeParameter((LocalParameter)param);
			// addProxyParameter(globalParameter);  not needed because it is added lazily during expression binding (don't add twice)
		}
	}
}


public void setParameterValue(LocalParameter parm, Expression exp, boolean autocreateLocalParameters) throws PropertyVetoException, ExpressionException {
	LocalParameter p = getLocalParameterFromName(parm.getName());
	if (p != parm){
		throw new RuntimeException("parameter "+parm.getName()+" not found");
	}
	Expression oldExpression = parm.getExpression();
	boolean bBound = false;
	try {
		if (autocreateLocalParameters){
			//
			// create local parameters for any unknown symbols.
			//
			LocalParameter newLocalParameters[] = (LocalParameter[])getLocalParameters().clone();
//			LocalProxyParameter newProxyParameters[] = (LocalProxyParameter[])getProxyParameters().clone();
			String symbols[] = exp.getSymbols();
			VCUnitSystem modelUnitSystem = unitSystemProvider.getUnitSystem();
			for (int i = 0; symbols!=null && i < symbols.length; i++){
				SymbolTableEntry ste = getEntry(symbols[i]);
				if (ste==null){
					newLocalParameters = (LocalParameter[])BeanUtils.addElement(newLocalParameters,new LocalParameter(symbols[i],new Expression(0.0),RbmKineticLaw.RbmKineticLawParameterType.UserDefined, modelUnitSystem.getInstance_TBD(),RbmKineticLaw.RbmKineticLawParameterType.UserDefined.getDescription()));
				}
			}
			setLocalParameters(newLocalParameters);
//			setProxyParameters(newProxyParameters);
		}		
		exp.bindExpression(this);
		parm.setExpression(exp);
		bBound = true;
	}finally{
		try {
			if (!bBound){
				parm.setExpression(oldExpression);
			}
			cleanupParameters();
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
}

public boolean contains(LocalParameter parameter) {
	for (LocalParameter p : fieldParameters){
		if (p == parameter){
			return true;
		}
	}
	return false;
}

public void gatherIssues(IssueContext issueContext, List<Issue> issueList, ParameterRoleEnum userDefinedRole) {
	//
	// for each user unresolved parameter, make an issue
	//
	for (int i = 0; fieldUnresolvedParameters!=null && i < fieldUnresolvedParameters.length; i++){
		issueList.add(new Issue(fieldUnresolvedParameters[i],issueContext, IssueCategory.UnresolvedParameter,"Unresolved parameter '"+fieldUnresolvedParameters[i].getName(),Issue.SEVERITY_ERROR));
	}
	//
	// for each user defined parameter, see if it is used, if not make an issue
	//
	for (int i=0;fieldParameters!=null && i<fieldParameters.length;i++){
		if (fieldParameters[i].getRole()==userDefinedRole){
			try {
				if (!isReferenced(fieldParameters[i],0)){
					issueList.add(new Issue(fieldParameters[i],issueContext, IssueCategory.KineticsUnreferencedParameter,"Unreferenced Kinetic Parameter '"+fieldParameters[i].getName(),Issue.SEVERITY_WARNING));
				}
			}catch (ExpressionException e){
				issueList.add(new Issue(fieldParameters[i],issueContext,IssueCategory.KineticsExpressionError, "error resolving expression " + e.getMessage(),Issue.SEVERITY_WARNING));
			}
		}
	}

	//
	// check for use of symbol bindings that are species contexts that are not reaction participants
	//
	if (fieldParameters!=null) {
		for (LocalParameter parameter : this.fieldParameters){
			if (parameter.getExpression()==null){
				issueList.add(new Issue(parameter,issueContext,IssueCategory.KineticsExpressionMissing,"expression is missing",Issue.SEVERITY_INFO));
			}else{
				Expression exp = parameter.getExpression();
				String symbols[] = exp.getSymbols();
				String issueMessagePrefix = "parameter '" + parameter.getName() + "' ";
				if (symbols!=null) { 
					for (int j = 0; j < symbols.length; j++){
						SymbolTableEntry ste = exp.getSymbolBinding(symbols[j]);
						if (ste instanceof LocalProxyParameter) {
							ste = ((LocalProxyParameter) ste).getTarget();
						}
						if (ste == null) {
							issueList.add(new Issue(parameter,issueContext,IssueCategory.KineticsExpressionUndefinedSymbol, issueMessagePrefix + "references undefined symbol '"+symbols[j]+"'",Issue.SEVERITY_ERROR));
//						} else if (ste instanceof SpeciesContext) {
//							if (!getReactionStep().getModel().contains((SpeciesContext)ste)) {
//								issueList.add(new Issue(parameter,issueContext,IssueCategory.KineticsExpressionUndefinedSymbol, issueMessagePrefix + "references undefined species '"+symbols[j]+"'",Issue.SEVERITY_ERROR));
//							}
//							if (reactionStep.countNumReactionParticipants((SpeciesContext)ste) == 0){
//								issueList.add(new Issue(parameter,issueContext,IssueCategory.KineticsExpressionNonParticipantSymbol, issueMessagePrefix + "references species context '"+symbols[j]+"', but it is not a reactant/product/catalyst of this reaction",Issue.SEVERITY_WARNING));
//							}
//						} else if (ste instanceof ModelParameter) {
//							if (!getReactionStep().getModel().contains((ModelParameter)ste)) {
//								issueList.add(new Issue(parameter,issueContext,IssueCategory.KineticsExpressionUndefinedSymbol, issueMessagePrefix + "references undefined global parameter '"+symbols[j]+"'",Issue.SEVERITY_ERROR));
//							}
						}
					}
				}
			}
		}
		
		// looking for local param which masks a global and issueing a warning
		for (LocalParameter parameter : fieldParameters){
			String name = parameter.getName();
			SymbolTableEntry ste = nameScope.getExternalEntry(name, this);
			String steName;
			if(ste != null) {
				if(ste instanceof Displayable) {
					steName = ((Displayable) ste).getDisplayType() + " " + ste.getName();
				} else {
					steName = ste.getClass().getSimpleName() + " " + ste.getName();
				}
				String msg = steName + " is overriden by a local parameter " + name;
				issueList.add(new Issue(parameter,issueContext,IssueCategory.Identifiers,msg ,Issue.SEVERITY_WARNING));
			}
		}
	}

	try {
		//
		// determine unit consistency for each expression
		//
		VCUnitSystem unitSystem = unitSystemProvider.getUnitSystem();
		VCUnitEvaluator unitEvaluator = new VCUnitEvaluator(unitSystem);
		for (int i = 0; i < fieldParameters.length; i++){
			if (fieldParameters[i].getExpression()==null){
				continue;
			}
			try {
				VCUnitDefinition paramUnitDef = fieldParameters[i].getUnitDefinition();
				VCUnitDefinition expUnitDef = unitEvaluator.getUnitDefinition(fieldParameters[i].getExpression());
				if (paramUnitDef == null){
					issueList.add(new Issue(fieldParameters[i], issueContext, IssueCategory.Units,"defined unit is null",Issue.SEVERITY_WARNING));
				}else if (paramUnitDef.isTBD()){
					issueList.add(new Issue(fieldParameters[i], issueContext, IssueCategory.Units,"undefined unit " + unitSystem.getInstance_TBD().getSymbol(),Issue.SEVERITY_WARNING));
				}else if (expUnitDef == null){
					issueList.add(new Issue(fieldParameters[i], issueContext, IssueCategory.Units,"computed unit is null",Issue.SEVERITY_WARNING));
				}else if (paramUnitDef.isTBD() || (!paramUnitDef.isEquivalent(expUnitDef) && !expUnitDef.isTBD())){
					issueList.add(new Issue(fieldParameters[i], issueContext, IssueCategory.Units,"inconsistent units, defined=["+fieldParameters[i].getUnitDefinition().getSymbol()+"], computed=["+expUnitDef.getSymbol()+"]",Issue.SEVERITY_WARNING));
				}
			}catch (VCUnitException e){
				issueList.add(new Issue(fieldParameters[i], issueContext, IssueCategory.Units, e.getMessage(),Issue.SEVERITY_WARNING));
			}catch (ExpressionException e){
				issueList.add(new Issue(fieldParameters[i],issueContext,IssueCategory.Units, e.getMessage(),Issue.SEVERITY_WARNING));
			}
		}
	}catch (Throwable e){
		issueList.add(new Issue(parameterPolicy.getIssueSource(),issueContext,IssueCategory.Units,"unexpected exception: "+e.getMessage(),Issue.SEVERITY_INFO));
	}

	//
	// add constraints (simpleBounds) for predefined parameters
	//
	for (int i = 0; i < fieldParameters.length; i++){
		RealInterval simpleBounds = parameterPolicy.getConstraintBounds(fieldParameters[i].getRole());
		if (simpleBounds!=null){
			String parmName = fieldParameters[i].getName();
			issueList.add(new SimpleBoundsIssue(fieldParameters[i], issueContext, simpleBounds, "parameter "+parmName+": must be within "+simpleBounds.toString()));
		}
	}
	
}


}
