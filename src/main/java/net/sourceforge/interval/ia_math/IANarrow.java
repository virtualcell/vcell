package net.sourceforge.interval.ia_math;

/**
 * IANarrow.java 
 *   -- classes implementing narrowing of arithmetic and elementary functions,
 *      as part of the "ia_math library" version 0.1beta1, 10/97
 * 
 * <p>
 * Copyright (C) 2000 Timothy J. Hickey
 * <p>
 * License: <a href="http://interval.sourceforge.net/java/ia_math/licence.txt">zlib/png</a>
 * <p>
 * the class RealIntervalNarrow contains methods for narrowing
 * the arithmetic operations and elementary functions.
 */
public class IANarrow
{

/**
 * IANarrow constructor comment.
 */
public IANarrow() {
	super();
}
  // a = acos(b)
  public static boolean 
    narrow_acos(RealInterval b,RealInterval a) {
    try {
         b.intersect(new RealInterval(-1,1));
         a.intersect(IAMath.acos(b));
         b.intersect(IAMath.cos(a));
         return true;
    } catch (IAException e) {
      return false;
    }
  }
  // a = acos(b)
  public static boolean 
    narrow_acos2pi(RealInterval a,RealInterval b) {
    System.out.println("acos2pi not yet implemented");
    return true;

  }
  public static boolean 
    narrow_add(RealInterval c,RealInterval a,RealInterval b) {
      try {
        c.intersect(IAMath.add(a,b));
        a.intersect(IAMath.sub(c,b));
        b.intersect(IAMath.sub(c,a));
        return true;
      }
      catch (IAException e) {
        return false;
      }
  }
  // a = asin(b)
  public static boolean 
    narrow_asin(RealInterval b,RealInterval a) {
    try {
         b.intersect(new RealInterval(-1.0,1.0));
         a.intersect(IAMath.asin(b));
         b.intersect(IAMath.sin(a));
         return true;
    } catch (IAException e) {
      return false;
    }
  }
  // a = asin(b)
  public static boolean 
    narrow_asin2pi(RealInterval a,RealInterval b) {
    System.out.println("asin2pi not yet implemented");
    return true;
  }
  // a = atan(b) 
  public static boolean 
    narrow_atan(RealInterval b,RealInterval a) {
    try {
      a.intersect(IAMath.atan(b));
      b.intersect(IAMath.tan(a));
      return true;
    } catch (IAException e) {
      return false;
    }
  }
  // a = atan(b) 
  public static boolean 
    narrow_atan2pi(RealInterval a,RealInterval b) {
    System.out.println("atan2pi not yet implemented");
    return true;
  }
  /**
   * z = x^y, where y is an integer
   */
  public static boolean narrow_carot(
       RealInterval z,RealInterval x,RealInterval y) {
    try {
        //          System.out.println("narrow_carot z=x^y with (x,y,z)= "+x+y+z);
        //          System.out.println(" and  x^y = "+IAMath.integerPower(x,y));
        z.intersect(IAMath.integerPower(x,y));
	//	  System.out.println(" did z=z cap x^y with (x,y,z)= "+x+y+z);
        IAMath.intersectIntegerRoot(z,y,x);
	//	  System.out.println(" did x=x cap z^1/y with (x,y,z)= "+x+y+z);
        return true;
    } catch (IAException e) {
      return false;
    }
  }
  public static boolean narrow_colon_equals(
       RealInterval a,RealInterval b,RealInterval c) {
    b.lo = c.lo; b.hi = c.hi;
    return b.nonEmpty();
  }
  public static boolean 
    narrow_cos(RealInterval a,RealInterval b) {
      try {
        b.intersect(IAMath.cos(a));
        return true;
      }
      catch (IAException e) {
        return false;
      }
    //    System.out.println("narrow_cos not yet implemented");
  }
  public static boolean 
    narrow_cos2pi(RealInterval a,RealInterval b) {
    System.out.println("narrow_cos2pi not yet implemented");
    return true;
  }
  public static boolean narrow_div(
       RealInterval a,RealInterval b,RealInterval c) {
    return 
      narrow_mul(b,a,c);
  }
  public static boolean 
  narrow_eq(RealInterval a,RealInterval b,RealInterval c) {
    if ((b.lo==b.hi) && b.equals(c)) {
      a.lo = 1.0; a.hi = 1.0; 
      return(true);
    }
    else
      try {
        b.intersect(c);
        c.intersect(b);
        return true;
    } catch (IAException e) {
        return false;
    }
  }
  public static boolean 
  narrow_equals(RealInterval b,RealInterval c) {
    if ((b.lo==b.hi) && b.equals(c))
      return(true);
    else 
      try {
        b.intersect(c);
        c.intersect(b);
        return true;
    } catch (IAException e) {
        return false;
    }
  }
  public static boolean 
    narrow_exp(RealInterval a,RealInterval b) {
    double tmp;
    try {
      b.intersect(IAMath.exp(a));
      a.intersect(IAMath.log(b));
      return true;
    } catch (IAException e) {
      return false;
    }
  }
  public static boolean narrow_ge(
       RealInterval r,RealInterval x,RealInterval y) {
    return narrow_le(r,y,x);
  }
  public static boolean narrow_gt(
       RealInterval r,RealInterval x,RealInterval y) {
    return narrow_lt(r,y,x);
  }
  public static boolean narrow_le(
       RealInterval r,RealInterval x,RealInterval y) {
    try {
       if (y.lo <= x.lo) y.lo = x.lo;
       if (x.hi >= y.hi) x.hi = y.hi;
       if (y.hi < x.lo)
         return false;
       else if (x.hi <= y.lo) {
         r.lo = 1.0; r.hi=1.0;
       }
       else {
         r.intersect(new RealInterval(0.0,1.0));
       }
       return(x.nonEmpty()&&y.nonEmpty());
    } catch (IAException e) {
      return false;
    }
  }
  public static boolean 
    narrow_log(RealInterval a,RealInterval b) {
    return narrow_exp(b,a);
  }
  /* x < y */
  public static boolean narrow_lt(
       RealInterval result,RealInterval x,RealInterval y) {
    try {
       if (y.lo < x.lo) y.lo = x.lo;
       if (x.hi > y.hi) x.hi = y.hi;
       if (y.hi <= x.lo)
         return false;
       else if (x.hi < y.lo) {
         result.lo = 1.0; result.hi=1.0;
       }
       else {
         result.intersect(new RealInterval(0.0,1.0));
       }
       return(x.nonEmpty()&&y.nonEmpty());
    } catch (IAException e) {
      return false;
    }
  }
  /* z = x*y */
  public static boolean narrow_mul(
       RealInterval z,RealInterval x,RealInterval y) {
    try {
       z.intersect(IAMath.mul(x,y));
       IAMath.intersect_odiv(y,z,x);
       IAMath.intersect_odiv(x,z,y);
       return true;
    } catch (IAException e) {
       return false;
    }
  }
  public static boolean narrow_ne(
       RealInterval r,RealInterval x,RealInterval y) {
    return ((x.lo < x.hi) || (y.lo < y.hi) || (x.lo != y.lo));
  }
  /**
   * z = x**y, assuming x > 0 and y is a real number
   */
  public static boolean narrow_power(
       RealInterval z,RealInterval x,RealInterval y) {
    try {
       z.intersect(IAMath.power(x,y));
       x.intersect(IAMath.power(z,
                          IAMath.odiv(new RealInterval(1.0),y)));
       y.intersect(IAMath.div(IAMath.log(z),IAMath.log(x)));
       return true;
    } catch (IAException e) {
       return false;
    }
  }
  public static boolean narrow_semi(
       RealInterval a,RealInterval b,RealInterval c) {
    return true;
  }
  public static boolean 
    narrow_sin(RealInterval a,RealInterval b) {
      try {
        b.intersect(IAMath.sin(a));
        return true;
      }
      catch (IAException e) {
        return false;
      }
    //    System.out.println("narrow_sin not yet implemented");
  }
  public static boolean 
    narrow_sin2pi(RealInterval a,RealInterval b) {
    System.out.println("narrow_sin2pi not yet implemented");
    return true;
  }
  public static boolean narrow_sub(
       RealInterval c,RealInterval a,RealInterval b) {
    return narrow_add(a,c,b);
  }
  public static boolean 
    narrow_tan(RealInterval a,RealInterval b) {
      try {
        b.intersect(IAMath.tan(a));
        return true;
      }
      catch (IAException e) {
        return false;
      }
      //    System.out.println("narrow_tan not yet implemented");
  }
  public static boolean 
    narrow_tan2pi(RealInterval a,RealInterval b) {
    System.out.println("narrow_tan2pi not yet implemented");
    return true;
  }
  public static boolean narrow_uminus(
       RealInterval a,RealInterval b) {
    try {
      a.intersect(IAMath.uminus(b));
      b.intersect(IAMath.uminus(a));
      return true;
    } catch (IAException e) {
      return false;
    }
  }
public static boolean vcell_narrow_abs(RealInterval b,RealInterval a) {
	//
	// narrow b = abs(a)
	//
	try {
		b.intersect(IAMath.vcell_abs(a));
		// narrow a by taking the union of a.intersect(b) and a.intersect(-b)
		RealInterval int1 = new RealInterval(a.lo,a.hi);
		RealInterval int2 = new RealInterval(a.lo,a.hi);
		//int1.intersect(b);
		int1.lo = Math.max(int1.lo,b.lo);
		int1.hi = Math.min(int1.hi,b.hi);
		
		//int2.intersect(new RealInterval(-b.hi,-b.lo));
		int2.lo = Math.max(int2.lo,-b.hi);
		int2.hi = Math.min(int2.hi,-b.lo);

		if (int1.nonEmpty()){
			if (int2.nonEmpty()){ // int1 is non-empty, int2 is non-empty, a = union(int1,int2)
				a.lo = Math.min(int1.lo,int2.lo);
				a.hi = Math.max(int1.hi,int2.hi);
			}else{ // int1 is non-empty, int2 is empty, a = int1
				a.lo = int1.lo;
				a.hi = int1.hi;
			}
		}else{
			if (int2.nonEmpty()){ // int1 is empty, int2 is non-empty, a = int2
				a.lo = int2.lo;
				a.hi = int2.hi;
			}else{ // int1 is empty, int2 is empty, a = EMPTY
				a.lo = Double.POSITIVE_INFINITY;
				a.hi = Double.NEGATIVE_INFINITY;
			}
		}
		return(a.nonEmpty()&&b.nonEmpty());
	} catch (IAException e) {
		return false;
	}
}
/*
 * result = acosh(arg)
 */
public static boolean vcell_narrow_acosh(RealInterval arg, RealInterval result) {
	try {
		// intersect arg with domain of acosh : [1, +INF); then forward : result intersect with acosh(arg); then backward : arg intersect with cosh(result)
		arg.intersect(new RealInterval(1, Double.POSITIVE_INFINITY));
		result.intersect(IAMath.vcell_acosh(arg));
		arg.intersect(IAMath.vcell_cosh(result));
		return true;
	} catch (IAFunctionDomainException e1) {
		e1.printStackTrace(System.out);
		return false;
	} catch (IAException e) {
		return false;
	}
}
/*
 * result = acot(arg)
 */
public static boolean vcell_narrow_acot(RealInterval arg, RealInterval result) {
	try {
		// intersect arg with domain of acot --- since domain is (-INF, +INF), no need for this step for acot
		
		//then forward : result intersect with acot(arg); then backward : arg intersect with cot(result)
		result.intersect(IAMath.vcell_acot(arg));
		arg.intersect(IAMath.vcell_cot(result));
		return true;
	} catch (IAException e) {
		return false;
	} 
}
/*
 * result = acoth(arg)
 */
public static boolean vcell_narrow_acoth(RealInterval arg, RealInterval result) {
	try {
		// intersect arg with domain of acoth (-INF, -1) U (1, +INF) ; then forward : result intersect with acosh(arg); then backward : arg intersect with cosh(result)
		RealInterval zNeg = (RealInterval)arg.clone();
		RealInterval zPos = (RealInterval)arg.clone();		
		zNeg.intersect(new RealInterval(Double.NEGATIVE_INFINITY, RMath.prevfp(-1.0)));	// since -1 is not inclusive in domain, use prev fl pt. val of -1
		zPos.intersect(new RealInterval(RMath.nextfp(1.0), Double.POSITIVE_INFINITY));	// since +1 is not inclusive in domain, use next fl pt. val of +1
		zNeg.union(zPos);
		arg = (RealInterval)zNeg.clone();		
		
		result.intersect(IAMath.vcell_acoth(arg));
		arg.intersect(IAMath.vcell_coth(result));
		return true;
	} catch (IAFunctionDomainException e1) {
		e1.printStackTrace(System.out);
		return false;
	} catch (IAException e) {
		return false;
	}
}
/*
 * result = arccsc(arg)
 */
public static boolean vcell_narrow_acsc(RealInterval arg, RealInterval result) {
	try {
		// intersect arg with domain of acsc --- since domain is (-INF, -1] U [1, +INF)
		RealInterval zNeg = (RealInterval)arg.clone();
		RealInterval zPos = (RealInterval)arg.clone();		
		zNeg.intersect(new RealInterval(Double.NEGATIVE_INFINITY, -1.0));
		zPos.intersect(new RealInterval(1, Double.POSITIVE_INFINITY));
		zNeg.union(zPos);
		arg = (RealInterval)zNeg.clone();		
		
		//then forward : result intersect with acsc(arg); then backward : arg intersect with csc(result)
		result.intersect(IAMath.vcell_acsc(arg));
		arg.intersect(IAMath.vcell_csc(result));
		return true;
	} catch (IAException e) {
		return false;
	} 
}
/*
 * result = acsch(arg)
 */
public static boolean vcell_narrow_acsch(RealInterval arg, RealInterval result) {
	try {
		// intersect arg with domain of acoth (-INF, 0) U (0, +INF) ; then forward : result intersect with acosh(arg); then backward : arg intersect with cosh(result)
		RealInterval zNeg = (RealInterval)arg.clone();
		RealInterval zPos = (RealInterval)arg.clone();		
		zNeg.intersect(new RealInterval(Double.NEGATIVE_INFINITY, RMath.prevfp(0.0)));	// since 0 is not inclusive in domain, use prev fl pt. val of 0
		zPos.intersect(new RealInterval(RMath.nextfp(0.0), Double.POSITIVE_INFINITY));	// since 0 is not inclusive in domain, use next fl pt. val of 0
		zNeg.union(zPos);
		arg = (RealInterval)zNeg.clone();		
		
		result.intersect(IAMath.vcell_acsch(arg));
		arg.intersect(IAMath.vcell_csch(result));
		return true;
	} catch (IAFunctionDomainException e1) {
		e1.printStackTrace(System.out);
		return false;
	} catch (IAException e) {
		return false;
	}
}
public static boolean vcell_narrow_and(RealInterval c,RealInterval a,RealInterval b) {
	//
	// narrow c = min(a,b)
	//
	try {
		c.intersect(IAMath.vcell_and(a,b));
		// if c is true, then a and b must both be true
		if (c.lo == 1.0 && c.hi == 1.0){
			// make a,b true by excluding zero (if one of the limits ... otherwise don't know how to exclude zero with one interval)
			if (a.lo == 0.0 && a.hi > 0){
				a.lo = RMath.nextfp(0.0);
			}
			if (a.lo < 0.0 && a.hi == 0){
				a.hi = RMath.prevfp(0.0);
			}
			if (b.lo == 0.0 && b.hi > 0){
				a.lo = RMath.nextfp(0.0);
			}
			if (b.lo < 0.0 && b.hi == 0){
				b.hi = RMath.prevfp(0.0);
			}
		}
		// if c is false, then at least one must be false
		// this means if one is strictly true, then the other must be narrowed to strictly zero
		if (c.lo == 0.0 && c.hi == 0.0){
			// a is strictly true, try to narrow b to strictly false
			if (a.hi < 0.0 || a.lo > 0.0){
				if (b.lo <= 0.0 && b.hi >= 0.0){
					b.intersect(new RealInterval(0.0));
				}
			}else if (b.hi < 0.0 || b.lo > 0.0){
				if (a.lo <= 0.0 && a.hi >= 0.0){
					a.intersect(new RealInterval(0.0));
				}
			}
		}
		return(a.nonEmpty()&&b.nonEmpty()&&c.nonEmpty());
	} catch (IAException e) {
		return false;
	}
}
/*
 * result = asec(arg)
 */
public static boolean vcell_narrow_asec(RealInterval arg, RealInterval result) {
	try {
		// intersect arg with domain of asec (-INF, -1] U [1, +INF) ; 
		RealInterval zNeg = (RealInterval)arg.clone();
		RealInterval zPos = (RealInterval)arg.clone();		
		zNeg.intersect(new RealInterval(Double.NEGATIVE_INFINITY, -1.0));	
		zPos.intersect(new RealInterval(1.0, Double.POSITIVE_INFINITY));	
		zNeg.union(zPos);
		arg = (RealInterval)zNeg.clone();		

		// then forward : result intersect with asec(arg); then backward : arg intersect with sec(result)
		result.intersect(IAMath.vcell_asec(arg));
		arg.intersect(IAMath.vcell_sec(result));
		return true;
	} catch (IAException e) {
		return false;
	} 
}
/*
 * result = asech(arg)
 */
public static boolean vcell_narrow_asech(RealInterval arg, RealInterval result) {
	try {
		// intersect arg with domain of asech (0, 1] ; then forward : result intersect with asech(arg); then backward : arg intersect with sech(result)
		arg.intersect(new RealInterval(RMath.nextfp(0.0), 1.0));
		result.intersect(IAMath.vcell_asech(arg));
		arg.intersect(IAMath.vcell_sech(result));
		return true;
	} catch (IAFunctionDomainException e1) {
		e1.printStackTrace(System.out);
		return false;
	} catch (IAException e) {
		return false;
	}
}
/*
 * result = asinh(arg)
 */
public static boolean vcell_narrow_asinh(RealInterval arg, RealInterval result) {
	try {
		// intersect arg with domain of asinh (-INF, +INF) (not needed here); 
		// then forward : result intersect with asinh(arg); then backward : arg intersect with sinh(result)
		result.intersect(IAMath.vcell_asinh(arg));
		arg.intersect(IAMath.vcell_sinh(result));
		return true;
	} catch (IAException e) {
		return false;
	} 
}
/*
 * result = atanh(arg)
 */
public static boolean vcell_narrow_atanh(RealInterval arg, RealInterval result) {
	try {
		// intersect arg with domain of atanh (-1, 1) ; then forward : result intersect with atanh(arg); then backward : arg intersect with tanh(result)
		arg.intersect(new RealInterval(RMath.nextfp(-1.0), RMath.prevfp(1.0)));
		result.intersect(IAMath.vcell_atanh(arg));
		arg.intersect(IAMath.vcell_tanh(result));
		return true;
	} catch (IAFunctionDomainException e1) {
		e1.printStackTrace(System.out);
		return false;
	} catch (IAException e) {
		return false;
	}
}
public static boolean vcell_narrow_ceil(RealInterval result,RealInterval arg) {
	//
	// narrow result = ceil(arg)
	//
	try {
		//
		// result must be two integers???? (check this).
		//
		result.lo = Math.ceil(result.lo);
		result.hi = Math.floor(result.hi);

		//
		// result must be in range of the argument
		//
		result.intersect(IAMath.vcell_ceil(arg));
		
		//
		// inverse operation: result has integer or infinite bounds
		//
		arg.intersect(new RealInterval(result.lo-1, result.hi));
		return(arg.nonEmpty()&&result.nonEmpty());
	} catch (IAException e) {
		return false;
	}}
/* b = cosh(a)
*/
public static boolean vcell_narrow_cosh(RealInterval a, RealInterval b) {
      try {
        b.intersect(IAMath.vcell_cosh(a));
        return true;
      } catch (IAFunctionDomainException e) {
	      e.printStackTrace(System.out);
	      return false;
      } catch (IAException e) {
	      return false;
      }
}
/* b = cot(a)
*/
public static boolean vcell_narrow_cot(RealInterval a, RealInterval b) {
      try {
        b.intersect(IAMath.vcell_cot(a));
        return true;
      } catch (IAException e) {
        return false;
      }
}
/* b = coth(a)
*/
public static boolean vcell_narrow_coth(RealInterval a, RealInterval b) {
      try {
        b.intersect(IAMath.vcell_coth(a));
        return true;
       } catch (IAFunctionDomainException e1) {
	      e1.printStackTrace(System.out);
	      return false;
     } catch (IAException e) {
        return false;
      }
}
/** b = csc(a)
*/
public static boolean vcell_narrow_csc(RealInterval a, RealInterval b) {
    try {
	    b.intersect(IAMath.vcell_csc(a));
 	    return true;
    } catch (IAException e) {
  	  return false;
    }
}
/* b = csch(a)
*/
public static boolean vcell_narrow_csch(RealInterval a, RealInterval b) {
      try {
        b.intersect(IAMath.vcell_csch(a));
        return true;
      } catch (IAFunctionDomainException e1) {
	      e1.printStackTrace(System.out);
	      return false;
      } catch (IAException e) {
	      return false;
      }
}
/*
 *	result = arg!  	// factorial(arg)
 */
public static boolean vcell_narrow_factorial(RealInterval result,RealInterval arg) {
	try {
		// result must be in range of the argument
		result.intersect(IAMath.vcell_factorial(arg));
		return(arg.nonEmpty()&&result.nonEmpty());
	} catch (IAFunctionDomainException e1) {
		e1.printStackTrace(System.out);
	    return false;
	} catch (IAException e) {
		return false;
    }
}
public static boolean vcell_narrow_floor(RealInterval result,RealInterval arg) {
	//
	// narrow b = floor(a)
	//
	try {
		//
		// result must be two integers???? (check this).
		//
		result.lo = Math.ceil(result.lo);
		result.hi = Math.floor(result.hi);

		//
		// result must be in range of the argument
		//
		result.intersect(IAMath.vcell_floor(arg));
		
		//
		// inverse operation: result has integer or infinite bounds
		//
		arg.intersect(new RealInterval(result.lo, result.hi+1));
		return(arg.nonEmpty()&&result.nonEmpty());
	} catch (IAException e) {
		return false;
	}
}
public static boolean vcell_narrow_max(RealInterval c,RealInterval a,RealInterval b) {
	//
	// narrow c = max(a,b)
	//
	try {
		c.intersect(IAMath.vcell_max(a,b));
		if (a.hi > c.hi){
			a.hi = c.hi;
		}
		if (b.hi > c.hi){
			b.hi = c.hi;
		}
		return(a.nonEmpty()&&b.nonEmpty()&&c.nonEmpty());
	} catch (IAException e) {
		return false;
	}
}
public static boolean vcell_narrow_min(RealInterval c,RealInterval a,RealInterval b) {
	//
	// narrow c = min(a,b)
	//
	try {
		c.intersect(IAMath.vcell_min(a,b));
		if (a.lo < c.lo){
			a.lo = c.lo;
		}
		if (b.lo < c.lo){
			b.lo = c.lo;
		}
		return(a.nonEmpty()&&b.nonEmpty()&&c.nonEmpty());
	} catch (IAException e) {
		return false;
	}
}
public static boolean vcell_narrow_not(RealInterval c,RealInterval a) {
	//
	// narrow c = not(a)
	//
	try {
		c.intersect(IAMath.vcell_not(a));
		// if c is strictly true, a must be false
		if (c.lo == 1.0 && c.hi == 1.0){
			a.intersect(new RealInterval(0.0));
		}
		if (c.lo == 0.0 && c.hi == 0.0){
			// if c is false, a must be strictly true
			if (a.lo == 0.0 && a.hi > 0.0){
				a.lo = RMath.nextfp(0.0);
			}
			if (a.lo < 0.0 && a.hi == 0.0){
				a.hi = RMath.prevfp(0.0);
			}
		}
		return(a.nonEmpty()&&c.nonEmpty());
	} catch (IAException e) {
		return false;
	}
}
public static boolean vcell_narrow_or(RealInterval c,RealInterval a,RealInterval b) {
	//
	// narrow c = min(a,b)
	//
	try {
		c.intersect(IAMath.vcell_or(a,b));
		// if c is false, then a and b must both be false
		if (c.lo == 0.0 && c.hi == 0){
			a.intersect(new RealInterval(0.0));
			b.intersect(new RealInterval(0.0));
		}
		// if c is true, then at least one must be strictly true
		// so if one is strictly false, the other must be strictly true (only possible if range is 0.0..n or -n..0.0)
		if (c.lo > 0.0 || c.hi < 0.0){  // c is true
			if (a.lo == 0.0 && a.hi == 0.0){ // a is false
				if (b.lo == 0.0 && b.hi > 0.0){
					b.lo = RMath.nextfp(0.0);
				}else if (b.lo < 0.0 && b.hi == 0.0){
					b.hi = RMath.prevfp(0.0);
				}
			}
			if (b.lo == 0.0 && b.hi == 0.0){ // b is false
				if (a.lo == 0.0 && a.hi > 0.0){
					b.lo = RMath.nextfp(0.0);
				}else if (a.lo < 0.0 && a.hi == 0.0){
					a.hi = RMath.prevfp(0.0);
				}
			}
		}
		return(a.nonEmpty()&&b.nonEmpty()&&c.nonEmpty());
	} catch (IAException e) {
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static boolean vcell_narrow_power(RealInterval c, RealInterval a, RealInterval b) {
  /**
   * c = a**b
   *
   * if b is not an integer, then force a to be non-negative??
   *
   */    
   try {
		boolean bExponentIsInteger = ((b.lo() == b.hi()) && (b.lo() == Math.round(b.lo())));
		if (bExponentIsInteger){
			c.intersect(IAMath.integerPower(a,b));
			a.intersect(IAMath.integerRoot(c,b));
			// cant narrow b (it is already an integer)
		}else{
			c.intersect(IAMath.vcell_power(a,b));
			a.intersect(IAMath.vcell_power(c,IAMath.odiv(new RealInterval(1.0),b)));
			b.intersect(IAMath.div(IAMath.log(c),IAMath.log(a)));
		}
       return true;
    } catch (IAFunctionDomainException e) {  // should never happen ... instead use function domain to narrow??? maybe not
       return false;
    } catch (IAException e) {
       return false;
    }

	//boolean bExponentIsInteger = ((b.lo() == b.hi()) && (b.lo() == Math.round(b.lo())));
	//boolean bExponentIsStrictlyPositive = (b.lo() > 0.0);
	//boolean bExponentIsStrictlyNegative = (b.hi() < 0.0);
	//if (bExponentIsInteger && bExponentIsStrictlyPositive){ // y is positive integer, any x
		//return IAMath.integerPower(a,b);
	//}else if (bExponentIsInteger && b.lo()<=0 && (a.hi()<0.0 || a.lo()>0.0)){ // y non-positive integer, x!=0
		//return IAMath.integerPower(a,b);
	//}else if (a.lo()>0){ // base strictly positive, any y
		//return IAMath.power(a,b);
	//}else if (a.lo()==0 && a.hi()==0.0 && bExponentIsStrictlyPositive){ // base zero, y strictly positive
		//return new RealInterval(0.0);
	//}else if (a.hi()<0.0 && bExponentIsInteger){
		//return IAMath.integerPower(a,b);
	//}else if (a.hi()<0.0 && !bExponentIsInteger){ // special cases when it's ok
		//throw new IAFunctionDomainException("x^y x is negative "+a+", and y is not an integer "+b);
	//}else{
		//throw new IAFunctionDomainException("x^y x includes zero "+a+", and y is not strictly positive "+b);
	//}
}
/* b = sec(a)
*/
public static boolean vcell_narrow_sec(RealInterval a, RealInterval b) {
	try {
		b.intersect(IAMath.vcell_sec(a));
		return true;
	} catch (IAException e) {
		return false;
	}
}
/* b = sech(a)
*/
public static boolean vcell_narrow_sech(RealInterval a, RealInterval b) {
      try {
        b.intersect(IAMath.vcell_sech(a));
        return true;
      } catch (IAException e) {
	      return false;
      }
}
/* b = sinh(a)
*/
public static boolean vcell_narrow_sinh(RealInterval a, RealInterval b) {
      try {
        b.intersect(IAMath.vcell_sinh(a));
        return true;
      } catch (IAException e) {
	      return false;
      }
}
/* b = tanh(a)
*/
public static boolean vcell_narrow_tanh(RealInterval a, RealInterval b) {
      try {
        b.intersect(IAMath.vcell_tanh(a));
        return true;
      } catch (IAException e) {
	      return false;
      }
}
}
