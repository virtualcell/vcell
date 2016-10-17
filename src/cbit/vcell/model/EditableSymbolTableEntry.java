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

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;

public interface EditableSymbolTableEntry extends SymbolTableEntry {
	
	public boolean isExpressionEditable();

	public boolean isUnitEditable();

	public boolean isNameEditable();

	public void setName(String name) throws PropertyVetoException;

	public void setExpression(Expression expression) throws ExpressionBindingException;

	public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException;
	
	public String getDescription();
	
	public void setDescription(String description) throws PropertyVetoException;
	
	public boolean isDescriptionEditable();

	public void addPropertyChangeListener(PropertyChangeListener listener);

	public void removePropertyChangeListener(PropertyChangeListener listener);

}
