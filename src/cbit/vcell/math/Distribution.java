/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.math;

import java.io.Serializable;

import org.vcell.util.Matchable;

import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTable;

public abstract class Distribution implements Matchable, Serializable {
	public abstract void bind(SymbolTable symbolTable) throws ExpressionBindingException;	
	public abstract String getVCML();
	public abstract void flatten(MathSymbolTable simSymbolTable,boolean bRoundCoefficients) throws ExpressionException, MathException;
}
