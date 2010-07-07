package cbit.vcell.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.units.VCUnitDefinition;

public class FieldDataSymbol extends DataSymbol {
	private FieldFunctionArguments fieldFunctionArguments = null;
	private String expression = null;
	
	public FieldFunctionArguments getFieldFunctionArguments() {
		return fieldFunctionArguments;
	}

	public void setFieldFunctionArguments(
			FieldFunctionArguments fieldFunctionArguments) {
		this.fieldFunctionArguments = fieldFunctionArguments;
	}

	public FieldDataSymbol(String name, DataContext dataContext, VCUnitDefinition vcUnitDefinition, FieldFunctionArguments fieldFunctionArguments){
		super(name,dataContext,vcUnitDefinition);
		this.fieldFunctionArguments = fieldFunctionArguments;
	}
}
