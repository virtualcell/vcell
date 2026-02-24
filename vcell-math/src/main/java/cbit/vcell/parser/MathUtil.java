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
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 *          Notice:
	 *  Any function that has a chance of having a zero for a
	 *  denominator, shall throw a "Divide by Zero Exception", as the
	 *  IEEE double-precision floating point specification allows for
	 *  edge cases of the infinities, and NaN. Java uses these special
	 *  cases rather than throwing a NPE, however for both internal
	 *  consistency, and for interoperability we will manually throw
	 *  exceptions to maintain the restriction.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	static double sin(double arg) {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double result = AccurateMath.sin(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double sinh(double arg) {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double result = AccurateMath.sinh(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double cos(double arg) {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double result = AccurateMath.cos(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double cosh(double arg) {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double result = AccurateMath.cosh(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double tan(double arg) {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double result = AccurateMath.tan(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double tanh(double arg) {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double result = AccurateMath.tanh(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double acos(double arg) {
		if (arg<-1 || arg > 1)
			throw new ArithmeticException("argument out of range");
		double ret = AccurateMath.acos(arg);
		if (Double.isNaN(ret))
			throw new ArithmeticException("argument out of range");
		if (Double.isInfinite(ret))
			throw new ArithmeticException("argument out of range");

		return ret;
	}

	static double acosh(double arg){
		if (arg<1 || Double.isInfinite(arg)) throw new ArithmeticException("argument out of range");
		double result = AccurateMath.acosh(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
//	    return Math.log(arg+Math.sqrt(arg*arg-1));
	}

	static double acot(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg)) throw new ArithmeticException("argument out of range");
		if (arg == 0.0) throw new DivideByZeroException();
		return MathUtil.atan(1.0/arg);
	}

	static double acoth(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg)) throw new ArithmeticException("argument out of range");
		if (arg == 0.0) throw new DivideByZeroException();
		return MathUtil.atanh(1.0/arg);
//	    return 0.5*Math.log((arg + 1.0)/(arg - 1.0));
	}

	static double acsc(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg)) throw new ArithmeticException("argument out of range");
	    if (arg == 0.0) throw new DivideByZeroException();
		return MathUtil.asin(1.0/arg);
	}

	static double acsch(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg)) throw new ArithmeticException("argument out of range");
		if (arg == 0.0) throw new DivideByZeroException();
		return MathUtil.asinh(1.0/arg);
//		if (arg > 0) {
//			return Math.log( (1.0 + Math.sqrt(1 + arg*arg)) / arg );
//		} else if (arg < 0) {
//			return Math.log( (1.0 - Math.sqrt(1 + arg*arg)) / arg );
//		}
//		throw new DivideByZeroException();
	} 

	static double asec(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg)) throw new ArithmeticException("argument out of range");
		if (arg == 0.0) throw new DivideByZeroException();
		return MathUtil.acos(1.0/arg);
	}

	static double asech(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg)) throw new ArithmeticException("argument out of range");
		if (arg == 0.0) throw new DivideByZeroException();
		return MathUtil.acosh(1.0/arg);
//		return Math.log( (1.0 + Math.sqrt(1.0 - arg*arg)) / arg );
	}

	static double asin(double arg) {
		if (arg<-1 || arg > 1) throw new ArithmeticException("argument out of range");
		double result = AccurateMath.asin(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double asinh(double arg){
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double result = AccurateMath.asinh(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
//	    return Math.log(arg+Math.sqrt(arg*arg+1));
	}

	static double atan(double arg) {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double result = AccurateMath.atan(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double atan2(double arg1, double arg2) {
		if (Double.isInfinite(arg1) || Double.isInfinite(arg2))throw new ArithmeticException("argument out of range");
		double result = AccurateMath.atan2(arg1, arg2);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double atanh(double arg){
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double result = AccurateMath.atanh(arg);
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
//	    return 0.5*(Math.log( (1.0 + arg)/(1.0 - arg) ));
	}

	static double cot(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double tangent = AccurateMath.tan(arg);
		if (0.0 == tangent) throw new DivideByZeroException();
		if (Double.isInfinite(tangent)) throw new ArithmeticException("argument out of range");
		double result = 1.0/tangent;
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double coth(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double hypertangent = AccurateMath.tanh(arg);
		if (0.0 == hypertangent) throw new DivideByZeroException();
		if (Double.isInfinite(hypertangent)) throw new ArithmeticException("argument out of range");
		double result = 1.0/hypertangent;
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double csc(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double sine = AccurateMath.sin(arg);
		if (0.0 == sine) throw new DivideByZeroException();
		if (Double.isInfinite(sine)) throw new ArithmeticException("argument out of range");
		double result = 1.0/sine;
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double csch(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double hypersine = AccurateMath.sinh(arg);
		if (0.0 == hypersine) throw new DivideByZeroException();
		if (Double.isInfinite(hypersine)) throw new ArithmeticException("argument out of range");
		double result = 1.0/hypersine;
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double factorial(double arg){
		if (arg > 18) throw new ArithmeticException("argument out of range");
		if (arg % 1 != 0) throw new ArithmeticException("Can not perform factorial on non-integer number `"+arg+"`");
		if (arg < 0) throw new ArithmeticException("Can not perform factorial on non-positive number `"+arg+"`");
	    double f = 1.0;
	    int n = (int)arg;
	    for(int i = 1; i <= n; i ++) {
			f *= i;
	    }
	    return f;
	}
	
	static double sec(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double cosine = AccurateMath.cos(arg);
		if (0.0 == cosine) throw new DivideByZeroException();
		if (Double.isInfinite(cosine)) throw new ArithmeticException("argument out of range");
		double result = 1.0/cosine;
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}

	static double sech(double arg) throws DivideByZeroException, ArithmeticException {
		if (Double.isInfinite(arg))throw new ArithmeticException("argument out of range");
		double hypercosine = AccurateMath.cosh(arg);
		if (0.0 == hypercosine) throw new DivideByZeroException();
		if (Double.isInfinite(hypercosine)) throw new ArithmeticException("argument out of range");
		double result = 1.0/hypercosine;
		if (Double.isInfinite(result))throw new ArithmeticException("result out of range");
		return result;
	}
}
