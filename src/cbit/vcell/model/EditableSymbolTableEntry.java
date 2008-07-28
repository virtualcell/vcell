package cbit.vcell.model;

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

	public void setExpression(Expression expression) throws PropertyVetoException, ExpressionBindingException;

	public void setUnitDefinition(VCUnitDefinition unit) throws PropertyVetoException;
	
	public String getDescription();
	
	public void setDescription(String description) throws PropertyVetoException;
	
	public boolean isDescriptionEditable();

}
