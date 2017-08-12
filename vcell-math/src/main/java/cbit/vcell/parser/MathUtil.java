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

class MathUtil {
	static double acosh(double arg){
	    return Math.log(arg+Math.sqrt(arg*arg-1));
	}

	static double acot(double arg){
	    return Math.atan(1.0/arg);
	}

	static double acoth(double arg){
	    return 0.5*Math.log((arg + 1.0)/(arg - 1.0));
	}

	static double acsc(double arg){
	    return Math.asin(1.0/arg);
	}

	static double acsch(double arg) throws DivideByZeroException {
		if (arg > 0) {
			return Math.log( (1.0 + Math.sqrt(1 + arg*arg)) / arg );
		} else if (arg < 0) {
			return Math.log( (1.0 - Math.sqrt(1 + arg*arg)) / arg );
		}
		throw new DivideByZeroException();
	} 

	static double asec(double arg){
	    return Math.acos(1.0/arg);
	}

	static double asech(double arg){
		return Math.log( (1.0 + Math.sqrt(1.0 - arg*arg)) / arg );
	}

	static double asinh(double arg){
	    return Math.log(arg+Math.sqrt(arg*arg+1));
	}

	static double atanh(double arg){
	    return 0.5*(Math.log( (1.0 + arg)/(1.0 - arg) ));
	}

	static double cot(double arg){
	    return 1.0/Math.tan(arg);
	}

	static double coth(double arg){
	    return 1.0/Math.tanh(arg);
	}

	static double csc(double arg){
	    return 1.0/Math.sin(arg);
	}

	static double csch(double arg){
	    return 1.0/Math.sinh(arg);
	}

	static double factorial(double arg){
	    double f = 1.0;
	    int n = (int)arg;
	    for(int i = 1; i <= n; i ++) {
			f *= i;
	    }
	    return f;
	}
	
	static double sec(double arg){
	    return 1.0/Math.cos(arg);
	}

	static double sech(double arg){
	    return 1.0/Math.cosh(arg);
	}
}
