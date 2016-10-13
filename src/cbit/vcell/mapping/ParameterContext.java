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
import java.util.Map;

import org.vcell.util.BeanUtils;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ExpressionContainer;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.ProxyParameter;
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
import cbit.vcell.units.VCUnitSystem;

public class ParameterContext implements Matchable, ScopedSymbolTable, Serializable {
	
	public interface ParameterPolicy {
		public boolean isUserDefined(LocalParameter localParameter);

		public boolean isExpressionEditable(LocalParameter localParameter);

		public boolean isUnitEditable(LocalParameter localParameter);

		public boolean isNameEditable(LocalParameter localParameter);
	}

	public class LocalParameter extends Parameter implements ExpressionContainer {
		private Expression fieldParameterExpression = null;
		private String fieldParameterName = null;
 		private int fieldParameterRole = -1;
 		private VCUnitDefinition fieldUnitDefinition = null;

		public LocalParameter(String parmName, Expression argExpression, int argRole, VCUnitDefinition argUnitDefinition, String argDescription) {
			super();
			fieldParameterName = parmName;
			fieldParameterExpression = argExpression;
			if (argRole >= 0){
				this.fieldParameterRole = argRole;
			}else{
				throw new IllegalArgumentException("parameter 'role' = "+argRole+" is out of range");
			}
			fieldUnitDefinition = argUnitDefinition;
			setDescription(argDescription);
		}

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

		public NameScope getNameScope(){
			return ParameterContext.this.getNameScope();
		}

		public boolean isExpressionEditable() {
			return parameterPolicy.isExpressionEditable(this);
		}

		public boolean isUnitEditable() {
			return parameterPolicy.isUnitEditable(this);
		}

		public boolean isNameEditable() {
			return parameterPolicy.isNameEditable(this);
		}

		public VCUnitDefinition getUnitDefinition() {
			return fieldUnitDefinition;
		}
		
		public void setExpression(Expression expression) throws PropertyVetoException, ExpressionBindingException {
			if (expression!=null){
				expression = new Expression(expression);
				expression.bindExpression(ParameterContext.this);
			}
			Expression oldValue = fieldParameterExpression;
			super.fireVetoableChange("expression", oldValue, expression);
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
		
		public int getIndex() { // used for evaluation evaluateVector(double[])
			return -1;
		}

		public int getRole() {
			return fieldParameterRole;
		}
	}
	
	public class UnresolvedParameter extends Parameter {
		
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

		public void setExpression(Expression expression) throws PropertyVetoException, ExpressionBindingException {
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
					
				}catch (ExpressionException e2){
					e2.printStackTrace(System.out);
				}catch (PropertyVetoException e3){
					e3.printStackTrace(System.out);
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
				e.printStackTrace(System.out);
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
	
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
			
	ste = getNameScope().getExternalEntry(identifierString,this);

	if (ste instanceof SymbolTableFunctionEntry){
		return ste;
	}
	
	if (ste!=null){
		if (ste instanceof SymbolTableFunctionEntry){
			return ste;
		} else {
			return addProxyParameter(ste);
		}
	}
	
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
public ParameterContext.LocalParameter getLocalParameterFromRole(int role) {
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
			System.out.println("error binding expression '"+fieldParameters[i].getExpression().infix()+"', "+e.getMessage());
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
		parameter.setName(newName);
		
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
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
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
				//System.out.println("successfully completed Kinetics.resolveUndefinedUnits() for ReactionStep '"+getReactionStep()+"'");
			}
		}catch (ExpressionBindingException e){
			System.out.println("ParameterContext.resolveUndefinedUnits(): EXCEPTION: "+e.getMessage());
		}catch (Exception e){
			System.out.println("ParameterContext.resolveUndefinedUnits(): EXCEPTION: "+e.getMessage());
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
			} catch (PropertyVetoException e) {
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
				System.out.println("error binding expression '"+exp.infix()+"': "+e.getMessage());
			}
		}
	}
	resolveUndefinedUnits();
}

/**
 * Insert the method's description here.
 * Creation date: (5/11/2004 6:19:00 PM)
 * @param bReading boolean
 */
//public void reading(boolean argReading) {
//	if (argReading == bReading){
//		throw new RuntimeException("flag conflict");
//	}
//	this.bReading = argReading;
//	if (!bReading){
//		resolveUndefinedUnits();
//	}
//}

/**
 * Sets the parameters property (cbit.vcell.model.Parameter[]) value.
 * @param parameters The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getParameters
 */
public void setLocalParameters(LocalParameter[] parameters) throws java.beans.PropertyVetoException {
	LocalParameter[] oldValue = fieldParameters;
	fireVetoableChange("localParameters", oldValue, parameters);
	fieldParameters = parameters;
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

public LocalParameter addLocalParameter(String name, Expression exp, int role, VCUnitDefinition unit, String description) throws PropertyVetoException {
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


}
