/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome.normalform;

import java.text.NumberFormat;
import org.vcell.util.fractions.Fraction;
import org.vcell.util.fractions.FractionUtil;

import java.text.DecimalFormat;

public class UOMENumberUtil {

	public static final double EPSILON = 100*Math.ulp(1.0);
	public static final double NEAR_ZERO = Math.ulp(0.0);
	
	public static boolean almostEqual(double a, double b) {
		return 2*Math.abs(a - b) / (Math.abs(a) + Math.abs(b) + NEAR_ZERO) < EPSILON; 
	}
	
	public static String toString(double x) {
		double xAbs = Math.abs(x);
		if(xAbs > 0.001 && xAbs < 1000.0) {
			Fraction frac = FractionUtil.convertToFraction(x, 1000);
			if(Math.abs(frac.getP()) < 1000 && frac.getQ() < 1000 && almostEqual(x, frac.doubleValue())) {
				return frac.toString();
			}			
		}
		NumberFormat decimalFormat = DecimalFormat.getInstance();
		if(decimalFormat instanceof DecimalFormat) {
			((DecimalFormat) decimalFormat).setMaximumFractionDigits(14);
		}
		return decimalFormat.format(x);
	}
	
}
