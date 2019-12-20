/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;
import org.vcell.util.Matchable;

import cbit.vcell.mapping.SimulationContext.Kind;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContextEntity;
import cbit.vcell.math.Function;
import cbit.vcell.math.VariableType;
import cbit.vcell.parser.Expression;
/**
 * Insert the type's description here.
 * Creation date: (1/29/2004 11:48:16 AM)
 * @author: Anuradha Lakshminarayana
 */
public class AnnotatedFunction extends Function implements Matchable, SimulationContextEntity {
	private java.lang.String fieldErrorString = null;
	private VariableType fieldFunctionType = null;
	private final FunctionCategory functionCategory;
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private String displayName = null;
	
	public enum FunctionCategory {
		PREDEFINED,
		OLDUSERDEFINED,
		OUTPUTFUNCTION, 
		POSTPROCESSFUNCTION
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
	functionCategory = fc;
}

/**
 * AnnotatedFunction copy constructor.
 */
public AnnotatedFunction(AnnotatedFunction argAnnotatedFn) {
	super(argAnnotatedFn.getName(), argAnnotatedFn.getExpression(), argAnnotatedFn.getDomain());
	this.displayName = argAnnotatedFn.displayName; 
	if (argAnnotatedFn.getName().indexOf(" ") > 0) {
		throw new RuntimeException("Spaces are not allowed in user-defined function names. Try adding the function without spaces in its name.");
	}
	//fieldSimplifiedExpression = null;
	fieldErrorString = argAnnotatedFn.fieldErrorString;
	fieldFunctionType = argAnnotatedFn.fieldFunctionType;
	functionCategory = argAnnotatedFn.functionCategory;
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
	return functionCategory.equals(FunctionCategory.PREDEFINED);
}

public boolean isOldUserDefined() {
	return functionCategory.equals(FunctionCategory.OLDUSERDEFINED);
}

public boolean isOutputFunction() {
	return functionCategory.equals(FunctionCategory.OUTPUTFUNCTION);
}
public boolean isPostProcessFunction() {
	return functionCategory.equals(FunctionCategory.POSTPROCESSFUNCTION);
}
public String getDisplayName() {
	return displayName;
}

public final FunctionCategory getFunctionCatogery() {
	return functionCategory;
}

@Override
public Kind getSimulationContextKind() {
	return SimulationContext.Kind.SIMULATIONS_KIND;
}

}
