/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;
import cbit.vcell.parser.*;
/**
 * Insert the type's description here.
 * Creation date: (3/3/00 12:09:47 AM)
 * @author: 
 */
public class Constraint {
	private Expression exp = null;
	private ConstraintType constraintType = null;

/**
 * Constraint constructor comment.
 * @param name java.lang.String
 * @param exp cbit.vcell.parser.Expression
 */
public Constraint(ConstraintType constraintType, cbit.vcell.parser.Expression exp) {
	if (exp==null){
		throw new IllegalArgumentException("expression cannot be null");
	}
	if (exp.isLogical() || exp.isRelational()){
		throw new RuntimeException("constraint expression should evaluate to a real, not a boolean");
	}
	this.exp = exp;
	this.constraintType = constraintType;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:34:17 AM)
 * @return cbit.vcell.opt.ConstraintType
 */
public ConstraintType getConstraintType() {
	return constraintType;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:33:43 AM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getExpression() {
	return exp;
}


/**
 * Insert the method's description here.
 * Creation date: (3/3/00 12:33:43 AM)
 * @return cbit.vcell.parser.Expression
 */
public Expression getLogicalExpression() {

	try {
		if (constraintType.isEquality()) {
			return new Expression(exp.infix()+" == 0");
		} else {
			return new Expression(exp.infix()+" <= 0");
		}
	} catch (ExpressionException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Error in exp : "+e.getMessage());
	}
}
}
