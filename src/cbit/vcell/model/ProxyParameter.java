/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolProxy;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.parser.SymbolTableFunctionEntry;
import cbit.vcell.units.VCUnitDefinition;

public abstract class ProxyParameter extends Parameter implements SymbolProxy {
	
	private SymbolTableEntry target = null;
	
	public class ProxyPropertyChangeListener implements PropertyChangeListener {
		//
		// refire property change events locally.
		//
		public void propertyChange(PropertyChangeEvent evt) {
			targetPropertyChange(evt);
		}
	};

	public ProxyParameter(SymbolTableEntry targetSymbolTableEntry){
		if (targetSymbolTableEntry instanceof SymbolTableFunctionEntry){
			throw new RuntimeException("internal error: tried to make a proxy parameter for a function definition");
		}
		this.target = targetSymbolTableEntry;
		try {
			Method addPropertyChangeListenerMethod = target.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class);
			if (addPropertyChangeListenerMethod!=null){
				addPropertyChangeListenerMethod.invoke(target, new ProxyPropertyChangeListener());
			}
		}catch (NoSuchMethodException e1){
			//System.err.println(e1.getMessage());
		}catch (IllegalAccessException e2){
		}catch (InvocationTargetException e3){
		}
	}

	public final double getConstantValue() throws ExpressionException {
		return target.getConstantValue();
	}

/**
 * targetPropertyChange : recasting and throwing PropertyChangeEvent - can be overridden for specific behavior in implementor/extender,
 * e.g., in KineticsProxyParameter.
 * @param evt
 */
	public void targetPropertyChange(PropertyChangeEvent evt) {
		PropertyChangeEvent pce = new PropertyChangeEvent(this,evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		pce.setPropagationId(evt.getPropagationId());
		firePropertyChange(pce);
	}
	
	public final Expression getExpression() {
		return target.getExpression();
	}

	public final String getName() {
		return target.getName();
	}

	public final VCUnitDefinition getUnitDefinition() {
		return target.getUnitDefinition();
	}

	public String getDescription() {
		if (target instanceof EditableSymbolTableEntry){
			return ((EditableSymbolTableEntry)target).getDescription();
		}else{
			return null;
		}
	}

	public final boolean isExpressionEditable() {
		if (target instanceof EditableSymbolTableEntry){
			return ((EditableSymbolTableEntry)target).isExpressionEditable();
		}else{
			return false;
		}
	}

	public final boolean isDescriptionEditable() {
		if (target instanceof EditableSymbolTableEntry){
			return ((EditableSymbolTableEntry)target).isDescriptionEditable();
		}else{
			return false;
		}
	}

	public final boolean isNameEditable() {
		if (target instanceof EditableSymbolTableEntry){
			return ((EditableSymbolTableEntry)target).isNameEditable();
		}else{
			return false;
		}
	}

	public final boolean isUnitEditable() {
		if (target instanceof EditableSymbolTableEntry){
			return ((EditableSymbolTableEntry)target).isUnitEditable();
		}else{
			return false;
		}
	}

	public final int getIndex() {
		return -1;
	}
	
	public final SymbolTableEntry getTarget(){
		return target;
	}

	public final void setDescription(String description){
		if (target instanceof Parameter){
			((Parameter)target).setDescription(description);
		}else{
			throw new RuntimeException("ProxyParameter.setDescription(): cannot set description on target symbol '"+getName()+"'");
		}
	}
	
	public final void setExpression(Expression expression) throws ExpressionBindingException {
		if (target instanceof EditableSymbolTableEntry){
			expression.bindExpression(target.getNameScope().getScopedSymbolTable());
			((EditableSymbolTableEntry)target).setExpression(expression);
		}else{
			throw new RuntimeException("ProxyParameter.setExpression(): cannot set expression on target symbol '"+getName()+"'");
		}
	}

	public final void setUnitDefinition(VCUnitDefinition unitDefinition) throws PropertyVetoException {
		if (target instanceof EditableSymbolTableEntry){
			((EditableSymbolTableEntry)target).setUnitDefinition(unitDefinition);
		}else{
			throw new RuntimeException("ProxyParameter.setUnitDefinition(): cannot set unit on target symbol '"+getName()+"'");
		}
	}

	public final void setName(String name) throws PropertyVetoException{
		if (target instanceof EditableSymbolTableEntry){
			((EditableSymbolTableEntry)target).setName(name);
		}else{
			throw new RuntimeException("ProxyParameter.setName(): cannot set name on target symbol '"+getName()+"'");
		}
	}

}
