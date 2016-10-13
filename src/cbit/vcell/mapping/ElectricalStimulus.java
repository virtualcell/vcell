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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.CommentStringTokenizer;
import org.vcell.util.Compare;
import org.vcell.util.Matchable;
import org.vcell.util.TokenMangler;

import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.ParameterContext.ParameterPolicy;
import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.model.Parameter;
import cbit.vcell.model.VCMODL;
import cbit.vcell.parser.AutoCompleteSymbolFilter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTable;
import cbit.vcell.units.UnitSystemProvider;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.units.VCUnitSystem;
/**
 * Insert the type's description here.
 * Creation date: (4/8/2002 11:14:58 AM)
 * @author: Anuradha Lakshminarayana
 */

public abstract class ElectricalStimulus implements Matchable, java.io.Serializable {
	private final ElectricalStimulusNameScope nameScope = new ElectricalStimulusNameScope();
	private class MyParameterPolicy implements ParameterPolicy, Serializable {

		public boolean isUserDefined(LocalParameter localParameter) {
			return (localParameter.getRole() == ROLE_UserDefined);
		}

		public boolean isExpressionEditable(LocalParameter localParameter) {
			return (localParameter.getExpression()!=null);
		}

		public boolean isNameEditable(LocalParameter localParameter) {
			return true;
		}

		public boolean isUnitEditable(LocalParameter localParameter) {
			return isUserDefined(localParameter);
		}
		
		
	};
	private final MyParameterPolicy parameterPolicy = new MyParameterPolicy();
	
	private class MyUnitSystemProvider implements UnitSystemProvider, Serializable {

		@Override
		public VCUnitSystem getUnitSystem() {
			return getSimulationContext().getModel().getUnitSystem();
		}
	};
	
	protected final ParameterContext parameterContext = new ParameterContext(nameScope,parameterPolicy, new MyUnitSystemProvider());

	private static final String GENERAL_PROTOCOL = "General_Protocol";
	
	public class ElectricalStimulusNameScope extends BioNameScope {
		private NameScope[] children = new NameScope[0];
		public ElectricalStimulusNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			return children;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(ElectricalStimulus.this.getName());
		}
		public NameScope getParent() {
			return ElectricalStimulus.this.simulationContext.getNameScope();
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return ElectricalStimulus.this.parameterContext;
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.electricalStimulusType;
		}
	}

		
	protected transient java.beans.VetoableChangeSupport vetoPropertyChange;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private Electrode fieldElectrode = null;
	private java.lang.String fieldName = null;
	private java.lang.String fieldAnnotation = null;
	private ElectricalStimulus.ElectricalStimulusNameScope fieldNameScope = new ElectricalStimulus.ElectricalStimulusNameScope();
	private SimulationContext simulationContext = null;
	private transient boolean bReading = false;
	//
	// for Voltage Clamp:  CurrentParameter.exp == null and VoltageParameter.exp != null
	// for Current Clamp:  CurrentParameter.exp != null and VoltageParameter.exp == null
	//
	public static final int NUM_PARAMETER_ROLES = 3;
	public static final int ROLE_UserDefined = 0;
	public static final int ROLE_CurrentDensity = 1;
	public static final int ROLE_TotalCurrent = 2;
	public static final int ROLE_Voltage = 3;

	private static final String RoleTags[] = {
		VCMODL.ES_Role_UserDefined,
		VCMODL.ES_Role_CurrentDensity,
		VCMODL.ES_Role_TotalCurrent,
		VCMODL.ES_Role_Voltage
	};
	public static final String DefaultNames[] = {
		null,
		"currentDensity",
		"totalCurrent",
		"V",
	};
	public static final String RoleDescs[] = {
		"user defined",
		"current density",  // supported for old models.
		"current",
		"potential difference",
	};
	public static String OLD_ROLEDESC_FOR_CURRENT_DENSITY = "total current";
	
/**
 * ElectricalStimulus constructor comment.
 */
protected ElectricalStimulus(Electrode argElectrode, String argName, SimulationContext argSimulationContext) {
	if (argElectrode==null){ throw new IllegalArgumentException("electrode must not be null"); }
	if (argName==null){ throw new IllegalArgumentException("name must not be null"); }
	if (argSimulationContext==null){ throw new IllegalArgumentException("simulation context must not be null"); }
	fieldElectrode = argElectrode;
	fieldName = argName;
	simulationContext = argSimulationContext;
	
	parameterContext.addPropertyChangeListener(new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	});
}

protected ElectricalStimulus(ElectricalStimulus otherStimulus, SimulationContext argSimulationContext){
	this(new Electrode(otherStimulus.getElectrode()),otherStimulus.getName(),argSimulationContext);
	
	LocalParameter[] otherLocalParameters = otherStimulus.getLocalParameters();
	LocalParameter[] newParameters = new LocalParameter[otherLocalParameters.length];
	for (int i = 0; i < otherLocalParameters.length; i++) {
		newParameters[i] = parameterContext.new LocalParameter(
								otherLocalParameters[i].getName(), 
								new Expression(otherLocalParameters[i].getExpression()), 
								otherLocalParameters[i].getRole(), 
								otherLocalParameters[i].getUnitDefinition(), 
								otherLocalParameters[i].getDescription());
	}

	try {
		parameterContext.setLocalParameters(newParameters);
	} catch (PropertyVetoException e) {
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
}

/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2003 9:51:49 AM)
 * @param parameterName java.lang.String
 */
public void addUnresolvedParameter(String parameterName) {
	parameterContext.addUnresolvedParameter(parameterName);
}


/**
 * Insert the method's description here.
 * Creation date: (9/22/2003 9:51:49 AM)
 * @param parameterName java.lang.String
 */
public LocalParameter addUserDefinedParameter(String parameterName, Expression expression, VCUnitDefinition unit) throws PropertyVetoException {
	return parameterContext.addLocalParameter(parameterName,expression,ROLE_UserDefined,unit,RoleDescs[ROLE_UserDefined]);
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
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
protected final boolean compareEqual0(Matchable obj) {
	if (obj instanceof ElectricalStimulus){
		ElectricalStimulus es = (ElectricalStimulus)obj;
		
		if (!Compare.isEqual(getName(),es.getName())){
			return false;
		}
		if (!Compare.isEqualOrNull(getAnnotation(),es.getAnnotation())){
			return false;
		}
		if (!Compare.isEqual(getElectrode(),es.getElectrode())){
			return false;
		}
		
		return true;
	}else{
		return false;
	}
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
 * Gets the annotation property (java.lang.String) value.
 * @return The annotation property value.
 * @see #setAnnotation
 */
public java.lang.String getAnnotation() {
	return fieldAnnotation;
}


public LocalParameter getLocalParameter(String name) {
	return parameterContext.getLocalParameterFromName(name);
}


/**
 * Gets the electrode property (cbit.vcell.mapping.Electrode) value.
 * @return The electrode property value.
 * @see #setElectrode
 */
public Electrode getElectrode() {
	return fieldElectrode;
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
 * Creation date: (4/6/2004 2:10:26 PM)
 * @return cbit.vcell.parser.NameScope
 */
public NameScope getNameScope() {
	return this.fieldNameScope;
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

public LocalParameter[] getLocalParameters() {
	return parameterContext.getLocalParameters().clone();
}

public Parameter[] getParameters(){
	ArrayList<Parameter> allParameters = new ArrayList<Parameter>();
	allParameters.addAll(Arrays.asList(parameterContext.getLocalParameters()));
	allParameters.addAll(Arrays.asList(parameterContext.getProxyParameters()));
	allParameters.addAll(Arrays.asList(parameterContext.getUnresolvedParameters()));
	return allParameters.toArray(new Parameter[allParameters.size()]);
}

public int getNumParameters(){
	return parameterContext.getLocalParameters().length + 
			parameterContext.getProxyParameters().length + 
			parameterContext.getUnresolvedParameters().length;
}

public ScopedSymbolTable getScopedSymbolTable(){
	return parameterContext;
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
 * This method was created by a SmartGuide.
 * @param tokens java.util.StringTokenizer
 * @exception java.lang.Exception The exception description.
 */
public final void parameterVCMLSet(CommentStringTokenizer tokens) throws ExpressionException,PropertyVetoException{

	if(tokens == null){
		return;
	}
	
	Vector<LocalParameter> esParametersV = new Vector<LocalParameter>();
	
	if (!tokens.nextToken().equalsIgnoreCase(VCMODL.ElectricalStimulus) ||
		!tokens.nextToken().equalsIgnoreCase(GENERAL_PROTOCOL) ||
		!tokens.nextToken().equalsIgnoreCase(VCMODL.BeginBlock)
		){
		throw new RuntimeException(ElectricalStimulus.class.getName()+".parameterVCMLRead, unexpected token ");
	}
	String token = null;
	ModelUnitSystem modelUnitSystem = simulationContext.getModel().getUnitSystem();
	while (tokens.hasMoreTokens()){
		token = tokens.nextToken();
		if (token.equalsIgnoreCase(VCMODL.EndBlock)){
			break;
		}
		if (token.equalsIgnoreCase(VCMODL.Parameter)){
			String roleName = tokens.nextToken();
			int roleInt = -1;
			for(int i =0;i<RoleTags.length;i+= 1){
				if(roleName.equals(RoleTags[i])){
					roleInt = i;
					break;
				}
			}
			if(roleInt == -1){
				throw new RuntimeException(ElectricalStimulus.class.getName()+".parameterVCMLRead, unexpected token for roleName "+roleName);
			}
			String parameterName = tokens.nextToken();
			Expression exp = MathFunctionDefinitions.fixFunctionSyntax(tokens);
			
			String unitsString = tokens.nextToken();
			VCUnitDefinition unitDef = modelUnitSystem.getInstance_TBD();
			if (unitsString.startsWith("[")){
				while (!unitsString.endsWith("]")){
					String tempToken = tokens.nextToken();
					unitsString = unitsString + " " + tempToken;
				}
				//
				// now string starts with '[' and ends with ']'
				//
				unitDef = modelUnitSystem.getInstance(unitsString.substring(1,unitsString.length()-1));
			}else{
				tokens.pushToken(unitsString);
			}
			LocalParameter esp = parameterContext.new LocalParameter(parameterName,exp,roleInt,unitDef,RoleDescs[roleInt]);
			esParametersV.add(esp);
		}else{
			throw new RuntimeException(ElectricalStimulus.class.getName()+".parameterVCMLRead, unexpected token for paramter tag "+token);
		}
	}

	if(esParametersV.size() > 0){
		LocalParameter[] espArr = new LocalParameter[esParametersV.size()];
		esParametersV.copyInto(espArr);
		parameterContext.setLocalParameters(espArr);
	}else{
		parameterContext.setLocalParameters(null);
	}
}


/**
 * This method was created by a SmartGuide.
 * @param ps java.io.PrintStream
 * @exception java.lang.Exception The exception description.
 */
public final void parameterVCMLWrite(java.io.PrintWriter pw) {
	
	//Example:
	//
	//	ElectricalStimulus General_Protocol {
	//      Parameter UserDefined a 10;
	//      Parameter Voltage b 3;
	//      Parameter Current c d/2; [pAmps]
	//  }
	//
	//

	pw.println("\t\t"+VCMODL.ElectricalStimulus+" "+GENERAL_PROTOCOL+" "+VCMODL.BeginBlock+" ");

	LocalParameter parameters[] = parameterContext.getLocalParameters();
	if (parameters!=null){
		for (int i=0;i<parameters.length;i++){
			LocalParameter parm = parameters[i];
			String roleName = RoleTags[parm.getRole()];
			VCUnitDefinition unit = parm.getUnitDefinition();
			pw.println("\t\t\t"+
				VCMODL.Parameter+" "+
				roleName + " " +
				parm.getName()+" "+
				parm.getExpression().infix() + ";" +
				(unit != null?" ["+unit.getSymbol()+"]":""));
		}
	}
	
	pw.println("\t\t"+VCMODL.EndBlock+" ");
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2004 11:30:54 AM)
 */
public void refreshDependencies() {
	parameterContext.refreshDependencies();
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
 * Insert the method's description here.
 * Creation date: (5/24/01 4:05:36 PM)
 */
public void renameParameter(String oldName, String newName) throws ExpressionException, java.beans.PropertyVetoException{
	parameterContext.renameLocalParameter(oldName, newName);
}

public SimulationContext getSimulationContext() {
	return simulationContext;
}

/**
 * Sets the annotation property (java.lang.String) value.
 * @param annotation The new value for the property.
 * @see #getAnnotation
 */
public void setAnnotation(java.lang.String annotation) {
	String oldValue = fieldAnnotation;
	fieldAnnotation = annotation;
	firePropertyChange("annotation", oldValue, annotation);
}

/**
 * Sets the electrode property (cbit.vcell.mapping.Electrode) value.
 * @param electrode The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getElectrode
 */
public void setElectrode(Electrode electrode) throws java.beans.PropertyVetoException {
	Electrode oldValue = fieldElectrode;
	fireVetoableChange("electrode", oldValue, electrode);
	fieldElectrode = electrode;
	firePropertyChange("electrode", oldValue, electrode);
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
 * This method was created by a SmartGuide.
 * @param expressionString java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void setParameterValue(LocalParameter parm, Expression exp) throws ExpressionException, PropertyVetoException {
	Parameter p = parameterContext.getLocalParameterFromName(parm.getName());
	if (p != parm){
		throw new RuntimeException("parameter "+parm.getName()+" not found");
	}
	Expression oldExpression = parm.getExpression();
	boolean bBound = false;
	try {
		LocalParameter newLocalParameters[] = (LocalParameter[])parameterContext.getLocalParameters().clone();
		String symbols[] = exp.getSymbols();
		Vector<String> symbolsToAdd = new Vector<String>();
		for (int i = 0; symbols!=null && i < symbols.length; i++){
			if (parameterContext.getEntry(symbols[i])==null){
				symbolsToAdd.add(symbols[i]);
			}
		}
		ModelUnitSystem modelUnitSystem = simulationContext.getModel().getUnitSystem();
		for (int i = 0; i < symbolsToAdd.size(); i++){
			newLocalParameters = (LocalParameter[])BeanUtils.addElement(newLocalParameters,
				parameterContext.new LocalParameter(symbolsToAdd.elementAt(i),new Expression(0.0),ROLE_UserDefined, modelUnitSystem.getInstance_TBD(),RoleDescs[ROLE_UserDefined]));
		}
		parameterContext.setLocalParameters(newLocalParameters);
		exp.bindExpression(parameterContext);
		parm.setExpression(exp);
		bBound = true;
	}finally{
		try {
			if (!bBound){
				parm.setExpression(oldExpression);
			}
			parameterContext.cleanupParameters();
		}catch (PropertyVetoException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
}

public AutoCompleteSymbolFilter getAutoCompleteSymbolFilter() {
	if (simulationContext == null) {
		return null;
	}
	return simulationContext.getAutoCompleteSymbolFilter();
}

/**
 * Insert the method's description here.
 * Creation date: (5/11/2004 6:19:00 PM)
 * @param bReading boolean
 */
public void reading(boolean argReading) {
	if (argReading == bReading){
		throw new RuntimeException("flag conflict");
	}
	this.bReading = argReading;
	if (!bReading){
		parameterContext.resolveUndefinedUnits();
	}
}

public SymbolTable getSymbolTable() {
	return parameterContext;
}

public abstract Parameter getProtocolParameter();


}
