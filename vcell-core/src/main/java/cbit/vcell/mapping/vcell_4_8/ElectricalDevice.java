/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.mapping.vcell_4_8;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Matchable;
import org.vcell.util.Relatable;
import org.vcell.util.RelationVisitor;
import org.vcell.util.TokenMangler;

import cbit.vcell.model.BioNameScope;
import cbit.vcell.model.Parameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.ScopedSymbolTable;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

abstract class ElectricalDevice implements ScopedSymbolTable {
	private final static Logger lg = LogManager.getLogger(ElectricalDevice.class);

	private String name = null;
	private ElectricalDeviceNameScope nameScope = new ElectricalDeviceNameScope();
	protected MathMapping_4_8 mathMapping_4_8 = null; // for determining NameScope parent only
	private Expression dependentVoltageExpression = null;

	public static final int ROLE_TotalCurrent					= 0;
	public static final int ROLE_TransmembraneCurrent			= 1;
	public static final int ROLE_Voltage						= 2;
	public static final int ROLE_Capacitance					= 3;
	public static final int ROLE_UserDefined					= 4;
	public static final int NUM_ROLES		= 5;
	public static final String DefaultNames[] = {
		"I",
		"F",
		"V",
		"Capacitance",
		null
	};
	private ElectricalDevice.ElectricalDeviceParameter[] fieldParameters = null;
	
	public class ElectricalDeviceNameScope extends BioNameScope {
		private final NameScope children[] = new NameScope[0]; // always empty
		public ElectricalDeviceNameScope(){
			super();
		}
		public NameScope[] getChildren() {
			//
			// no children to return
			//
			return children;
		}
		public String getName() {
			return TokenMangler.fixTokenStrict(ElectricalDevice.this.getName());
		}
		public NameScope getParent() {
			if (ElectricalDevice.this.mathMapping_4_8 != null){
				return ElectricalDevice.this.mathMapping_4_8.getNameScope();
			}else{
				return null;
			}
		}
		public ScopedSymbolTable getScopedSymbolTable() {
			return ElectricalDevice.this;
		}
		@Override
		public NamescopeType getNamescopeType() {
			return NamescopeType.electricalDeviceType;
		}
	}

	public class ElectricalDeviceParameter extends Parameter {

		private int fieldParameterRole = -1;
		private String fieldParameterName = null;
		private Expression fieldParameterExpression = null;
		private VCUnitDefinition fieldVCUnitDefinition = null;


		public ElectricalDeviceParameter(String parmName, Expression argExpression, int argRole, VCUnitDefinition argVCUnitDefinition) {
			super();
			fieldParameterName = parmName;
			fieldParameterExpression = argExpression;
			if (argRole >= 0 && argRole < NUM_ROLES){
				this.fieldParameterRole = argRole;
			}else{
				throw new IllegalArgumentException("parameter 'role' = "+argRole+" is out of range");
			}
			fieldVCUnitDefinition = argVCUnitDefinition;
		}

		@Override
		public boolean compareEqual(Matchable obj) {
			if (!(obj instanceof ElectricalDeviceParameter)){
				return false;
			}
			ElectricalDeviceParameter edp = (ElectricalDeviceParameter)obj;
			if (!super.compareEqual0(edp)){
				return false;
			}
			if (fieldParameterRole != edp.fieldParameterRole){
				return false;
			}

			return true;
		}

		@Override
		public boolean relate(Relatable obj, RelationVisitor rv) {
			if (!(obj instanceof ElectricalDeviceParameter)){
				return false;
			}
			ElectricalDeviceParameter edp = (ElectricalDeviceParameter)obj;
			if (!super.relate0(edp, rv)){
				return false;
			}
			if (fieldParameterRole != edp.fieldParameterRole){
				return false;
			}

			return true;
		}

		public boolean isExpressionEditable(){
			return true;
		}

		public boolean isUnitEditable(){
			return false;
		}

		public void setUnitDefinition(VCUnitDefinition unit) {
			throw new RuntimeException("Unit is not editable");
		}
		
		public boolean isNameEditable(){
			return true;
		}

		public NameScope getNameScope(){
			return ElectricalDevice.this.getNameScope();
		}

		public void setName(java.lang.String name) throws java.beans.PropertyVetoException {
			String oldValue = fieldParameterName;
			super.fireVetoableChange("name", oldValue, name);
			fieldParameterName = name;
			super.firePropertyChange("name", oldValue, name);
		}

		public void setExpression(Expression expression) {
			expression = new Expression(expression);
//			try {
//				expression.bindExpression(ElectricalDevice.this);
//			} catch (ExpressionBindingException e) {
//				lg.error(e);
//				throw new PropertyVetoException(e.getMessage(),null);
//			}
			Expression oldValue = fieldParameterExpression;
			fieldParameterExpression = expression;
			super.firePropertyChange("expression", oldValue, expression);
		}

		public double getConstantValue() throws ExpressionException {
			return fieldParameterExpression.evaluateConstant();
		}

		public Expression getExpression() {
			return fieldParameterExpression;
		}

		public VCUnitDefinition getUnitDefinition(){
			return fieldVCUnitDefinition;
		}
		
		public String getName() {
			return fieldParameterName;
		}

		public int getIndex() {
			return -1;
		}

		public int getRole() {
			return fieldParameterRole;
		}

		public boolean isDescriptionEditable() {
			return false;
		}

	}

ElectricalDevice(String argName, MathMapping_4_8 argMathMapping) {
	this.name = argName;
	this.mathMapping_4_8 = argMathMapping;
}

abstract boolean getCalculateVoltage();


Expression getDependentVoltageExpression() {
	return dependentVoltageExpression;
}


/**
 * getEntry method comment.
 */
public SymbolTableEntry getEntry(java.lang.String identifierString) {
	
	SymbolTableEntry ste = getLocalEntry(identifierString);
	if (ste != null){
		return ste;
	}
			
	return getNameScope().getExternalEntry(identifierString,this);
}

public void getLocalEntries(Map<String, SymbolTableEntry> entryMap) {
	for (SymbolTableEntry ste : fieldParameters) {
		entryMap.put(ste.getName(), ste);
	}
}

public void getEntries(Map<String, SymbolTableEntry> entryMap) {	
	getNameScope().getExternalEntries(entryMap);	
}

final SymbolTableEntry getTotalCurrentSymbol() {
	return getParameterFromRole(ROLE_TotalCurrent);
}


public SymbolTableEntry getLocalEntry(String identifier) {
	ElectricalDevice.ElectricalDeviceParameter parameter = getParameter(identifier);
	
	return parameter;
}

final String getName() {
	return name;
}


public NameScope getNameScope() {
	return nameScope;
}


ElectricalDevice.ElectricalDeviceParameter getParameter(String argName) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i].getName().equals(argName)){
			return fieldParameters[i];
		}
	}
	return null;
}

ElectricalDeviceParameter getParameterFromRole(int role) {
	for (int i = 0; i < fieldParameters.length; i++){
		if (fieldParameters[i] instanceof ElectricalDeviceParameter){
			ElectricalDeviceParameter electricalDeviceParameter = (ElectricalDeviceParameter)fieldParameters[i];
			if (electricalDeviceParameter.getRole() == role){
				return electricalDeviceParameter;
			}
		}
	}
	return null;
}

ElectricalDevice.ElectricalDeviceParameter[] getParameters() {
	return fieldParameters;
}

ElectricalDevice.ElectricalDeviceParameter getParameters(int index) {
	return getParameters()[index];
}

abstract boolean getResolved();

final SymbolTableEntry getSourceSymbol() {
	return getParameterFromRole(ROLE_TransmembraneCurrent);
}

abstract SymbolTableEntry getVoltageSymbol();

abstract boolean hasCapacitance();

abstract boolean isVoltageSource();

void setDependentVoltageExpression(Expression newDependentVoltageExpression) {
	dependentVoltageExpression = newDependentVoltageExpression;
}

void setParameters(ElectricalDevice.ElectricalDeviceParameter[] parameters) {
	fieldParameters = parameters;
	for (int i = 0; i < fieldParameters.length; i++){
		try {
			if (fieldParameters[i].getExpression()!=null){
				fieldParameters[i].getExpression().bindExpression(this);
			}
		}catch (ExpressionBindingException e){
			lg.error(e);
		}
	}
}

public String toString() {
	return getName();
}
}
