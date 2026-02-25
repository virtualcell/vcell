/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.parser;

import org.apache.commons.math4.core.jdkmath.AccurateMath;

class MathUtil {
	static double sin(double arg) {
		return AccurateMath.sin(arg);
	}

	static double sinh(double arg) {
		return AccurateMath.sinh(arg);
	}

	static double cos(double arg) {
		return AccurateMath.cos(arg);
	}

	static double cosh(double arg) {
		return AccurateMath.cosh(arg);
	}

	static double tan(double arg) {
		return AccurateMath.tan(arg);
	}

	static double tanh(double arg) {
		return AccurateMath.tanh(arg);
	}

	static double acos(double arg) {
		return AccurateMath.acos(arg);
	}

	static double acosh(double arg){
		return AccurateMath.acosh(arg);
//	    return Math.log(arg+Math.sqrt(arg*arg-1));
	}

	static double acot(double arg){
		return MathUtil.atan(1.0/arg);
	}

	static double acoth(double arg){
		return MathUtil.atanh(1.0/arg);
//	    return 0.5*Math.log((arg + 1.0)/(arg - 1.0));
	}

	static double acsc(double arg){
		return MathUtil.asin(1.0/arg);
	}

	static double acsch(double arg) throws DivideByZeroException {
		if (arg == 0.0) throw new DivideByZeroException();
		return MathUtil.asinh(1.0/arg);
//		if (arg > 0) {
//			return Math.log( (1.0 + Math.sqrt(1 + arg*arg)) / arg );
//		} else if (arg < 0) {
//			return Math.log( (1.0 - Math.sqrt(1 + arg*arg)) / arg );
//		}
//		throw new DivideByZeroException();
	} 

	static double asec(double arg){
		return MathUtil.acos(1.0/arg);
	}

	static double asech(double arg){
		return MathUtil.acosh(1.0/arg);
//		return Math.log( (1.0 + Math.sqrt(1.0 - arg*arg)) / arg );
	}

	static double asin(double arg) {
		return AccurateMath.asin(arg);
	}

	static double asinh(double arg){
		return AccurateMath.asinh(arg);
//	    return Math.log(arg+Math.sqrt(arg*arg+1));
	}

	static double atan(double arg) {
		return AccurateMath.atan(arg);
	}

	static double atan2(double arg1, double arg2) {
		return AccurateMath.atan2(arg1, arg2);
	}

	static double atanh(double arg){
		return AccurateMath.atanh(arg);
//	    return 0.5*(Math.log( (1.0 + arg)/(1.0 - arg) ));
	}

	static double cot(double arg){
		return 1.0/AccurateMath.tan(arg);
	}

	static double coth(double arg){
		return 1.0/AccurateMath.tanh(arg);
	}

	static double csc(double arg){
		return 1.0/AccurateMath.sin(arg);
	}

	static double csch(double arg){
		return 1.0/AccurateMath.sinh(arg);
	}

	static double factorial(double arg) throws FunctionDomainException {
		if (arg > 18) return Double.POSITIVE_INFINITY;
		if (arg % 1 != 0) throw new FunctionDomainException("Can not perform factorial on non-integer number `"+arg+"`");
	    double f = 1.0;
	    int n = (int)arg;
	    for(int i = 1; i <= n; i ++) {
			f *= i;
	    }
	    return f;
	}
	
	static double sec(double arg){
		return 1.0/AccurateMath.cos(arg);
	}

	static double sech(double arg){
		return 1.0/AccurateMath.cosh(arg);
	}
}
