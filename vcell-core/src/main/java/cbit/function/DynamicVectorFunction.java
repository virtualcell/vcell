/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.function;

import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (4/3/2002 4:10:45 PM)
 * @author: Michael Duff
 */
public class DynamicVectorFunction extends DefaultVectorFunction {
	private Expression[] functionExps = null;
	private Expression[][] jacobianExps = null;
	private String[] identifiers = null;
/**
 * DynamicVectorFunction constructor comment.
 */
public DynamicVectorFunction(Expression argFunctions[], String argIdentifiers[]) throws cbit.vcell.parser.ExpressionException {
	identifiers = (String[])argIdentifiers.clone();
	// copy array and copy elements
	functionExps = (Expression[])argFunctions.clone();
	for (int i = 0; i < functionExps.length; i++){
		functionExps[i] = new Expression(functionExps[i]);
	}

	//
	// create temporary symbolTable to assign indices to identifiers
	//
	cbit.vcell.parser.SimpleSymbolTable symbolTable = new cbit.vcell.parser.SimpleSymbolTable(identifiers);

	//
	// bind expressions to temporary symbol table
	//
	for (int i = 0; i < functionExps.length; i++){
		functionExps[i].bindExpression(symbolTable);
	}

	//
	// analytically evaluate jacobian and bind to same symbol table (gives same indices for value vector)
	//
	initializeJacobian(symbolTable);
	
}
public double[][] evaluateJacobian(double[] x) {
    try {
		int n = getSystemDimension();
		int m = getNumArgs();
        double[][] jj = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                jj[i][j] = jacobianExps[i][j].evaluateVector(x);
            }
        }
        return jj;
    } catch (cbit.vcell.parser.ExpressionException e) {
        throw new RuntimeException(e.getMessage());
    }
}
/**
 * f method comment.
 */
public double[] f(double[] x) {
    try {
        double[] y = new double[getSystemDimension()];
        for (int i = 0; i < functionExps.length; i++) {
            y[i] = functionExps[i].evaluateVector(x);
        }
        return y;
    } catch (cbit.vcell.parser.ExpressionException e) {
        throw new RuntimeException(e.getMessage());
    }
}
/**
 * Insert the method's description here.
 * Creation date: (4/26/2002 4:08:58 PM)
 * @return int
 */
public int getNumArgs() {
	return identifiers.length;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 4:10:45 PM)
 * @return int
 */
public int getSystemDimension() {
	return functionExps.length;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 5:37:12 PM)
 */
private void initializeJacobian(cbit.vcell.parser.SymbolTable symbolTable) throws ExpressionException {
	int n = getSystemDimension();
	int m = getNumArgs();
	jacobianExps = new Expression[n][m];
	for (int i=0;i<n;i++){
		for (int j=0;j<m;j++){
			jacobianExps[i][j] = functionExps[i].differentiate(identifiers[j]);
			//
			// simplifies
			//
			jacobianExps[i][j] = jacobianExps[i][j].flatten();

			//
			// binds jacobian expression to symbol table in preparation for evaluation with a vector
			//
			jacobianExps[i][j].bindExpression(symbolTable);
		}
	}
}
}
