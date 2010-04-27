package cbit.vcell.math;
import org.vcell.util.Matchable;

import cbit.vcell.parser.Expression;
import cbit.vcell.simdata.VariableType;
/**
 * Insert the type's description here.
 * Creation date: (1/29/2004 11:48:16 AM)
 * @author: Anuradha Lakshminarayana
 */
public class AnnotatedFunction extends Function implements Matchable {
	private java.lang.String fieldErrorString = null;
	private VariableType fieldFunctionType = null;
	private final FunctionCategory functionCatogery;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private String displayName = null;
	
	public enum FunctionCategory {
		PREDEFINED,
		OLDUSERDEFINED,
		OUTPUTFUNCTION,
	}

	public AnnotatedFunction(String argFunctionName, Expression argFunctionExpression, Domain domain, String argErrString, VariableType argFunctionType, FunctionCategory fc) {
		this(argFunctionName, argFunctionExpression, domain, argFunctionName, argErrString, argFunctionType, fc);
	}
	
/**
 * AnnotatedFunction constructor comment.
 */
public AnnotatedFunction(String argFunctionName, Expression argFunctionExpression, Domain domain, String argDisplayName, String argErrString, VariableType argFunctionType, FunctionCategory fc) {
	super(argFunctionName, argFunctionExpression, domain);
	this.displayName = argDisplayName; 
	if (argFunctionName.indexOf(" ") > 0) {
		throw new RuntimeException("Spaces are not allowed in user-defined function names. Try adding the function without spaces in its name.");
	}
	//fieldSimplifiedExpression = null;
	fieldErrorString = argErrString;
	fieldFunctionType = argFunctionType;
	functionCatogery = fc;
}


/**
 * Insert the method's description here.
 * Creation date: (1/29/2004 11:53:36 AM)
 * @return java.lang.String
 */
public java.lang.String getErrorString() {
	return fieldErrorString;
}


/**
 * Insert the method's description here.
 * Creation date: (1/29/2004 2:22:09 PM)
 * @return cbit.vcell.simdata.VariableType
 */
public VariableType getFunctionType() {
	return fieldFunctionType;
}

/**
 * Insert the method's description here.
 * Creation date: (2/20/2004 11:05:24 AM)
 * @return boolean
 */
public boolean isPredefined() {
	return functionCatogery.equals(FunctionCategory.PREDEFINED);
}

public boolean isOldUserDefined() {
	return functionCatogery.equals(FunctionCategory.OLDUSERDEFINED);
}

public boolean isOutputFunction() {
	return functionCatogery.equals(FunctionCategory.OUTPUTFUNCTION);
}

public String getDisplayName() {
	return displayName;
}

public final FunctionCategory getFunctionCatogery() {
	return functionCatogery;
}

}