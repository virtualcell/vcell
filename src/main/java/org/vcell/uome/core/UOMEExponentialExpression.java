/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome.core;

import java.util.List;

import org.vcell.sybil.util.lists.ListUtil;
import org.vcell.uome.normalform.UOMENumberUtil;

public class UOMEExponentialExpression extends UOMESingleUnitExpression {
	
	protected double exponent;
	
	public void setExponent(double exponent) { this.exponent = exponent; }
	public double getExponent() { return exponent; }
	
	public String buildString(List<String> strings) {
		List<String> stringsFilled = ListUtil.fillGaps(strings, "?", 1);
		String exponentString = UOMENumberUtil.toString(exponent);
		if(exponent < 0.0) { exponentString = "(" + exponentString + ")"; }
		return stringsFilled.get(0) + "^" + exponentString;
	}

}
