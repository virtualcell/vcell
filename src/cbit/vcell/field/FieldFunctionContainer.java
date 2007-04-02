package cbit.vcell.field;

import java.util.Hashtable;

import cbit.vcell.math.MathException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.ExternalDataIdentifier;

public interface FieldFunctionContainer {

	public FieldFunctionArguments[] getFieldFunctionArguments()
		throws MathException, ExpressionException;
	
	public void substituteFieldFuncNames(Hashtable<String, ExternalDataIdentifier> oldNameNewID)
		throws MathException, ExpressionException;

}
