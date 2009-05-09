package cbit.vcell.field;

import java.util.Hashtable;

import org.vcell.util.document.ExternalDataIdentifier;

import cbit.vcell.math.MathException;
import cbit.vcell.parser.ExpressionException;

public interface FieldFunctionContainer {

	public FieldFunctionArguments[] getFieldFunctionArguments()
		throws MathException, ExpressionException;
	
	public void substituteFieldFuncNames(Hashtable<String, ExternalDataIdentifier> oldNameNewID)
		throws MathException, ExpressionException;

}
