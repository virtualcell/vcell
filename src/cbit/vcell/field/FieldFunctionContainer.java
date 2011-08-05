/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
