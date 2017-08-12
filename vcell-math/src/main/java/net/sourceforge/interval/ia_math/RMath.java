

package net.sourceforge.interval.ia_math;

/**
 * RMath.java 
 *   -- classes implementing directed rounding of real numbers,
 *      as part of the "ia_math library" version 0.1beta1, 10/97
 * 
 * <p>
 * Copyright (C) 2000 Timothy J. Hickey
 * <p>
 * License: <a href="http://interval.sourceforge.net/java/ia_math/licence.txt">zlib/png</a>
 * <p>
 * 
 * the class RoundedMath contains methods and constants for
 * controling the rounding of the elementary arithmetic operations
 * on floating point numbers. 
 * <p>
 * The current Java 1.1 API does not contain any methods for performing
 * rounded arithmetic besides the default (round toward nearest).
 * <p>
 * This version strikes a compromise between efficiency and accuracy.
 * @author Tim Hickey
 * @version 0.0
 */ 
public class RMath
{
  static double Zero;
  static double NegZero;

  static {
    Zero = 0.0;
    NegZero = (-1.0)*(0.0);
  }


  /**
   * return the next larger double precision number
   */
  public static double nextfp(double x) {
    double y;

    if (x==0)
      return Double.longBitsToDouble(1);
    else if (x < Double.POSITIVE_INFINITY) {
      long xx = Double.doubleToLongBits(x);
      if (x > 0)
         y = Double.longBitsToDouble(xx+1);
      else if (x==0)   // this case should never happen
	  y = Double.longBitsToDouble(1); 
      else
         y = Double.longBitsToDouble(xx-1);
      return(y);
    }else 
     return (x);
  }

  public static double prevfp(double x) {
    if (x==0) 
      return(-nextfp(0.0));
    else 
      return(-nextfp(-x));
  }

  public static double add_lo(double x, double y) {
    return(prevfp(x+y));
  }

  public static double add_hi(double x, double y) {
    return(nextfp(x+y));
  }

  public static double sub_lo(double x, double y) {
    return(prevfp(x-y));
  }

  public static double sub_hi(double x, double y) {
    return(nextfp(x-y));
  }

  public static double mul_lo(double x, double y) {
    if ((x==0.0)||(y==0.0)) return(0.0);
    return(prevfp(x*y));
  }

  public static double mul_hi(double x, double y) {
    if ((x==0.0)||(y==0.0)) return(0.0);
    return(nextfp(x*y));
  }

  public static double div_lo(double x, double y) {
    if (x==0.0) return(0.0);
    return(prevfp(x/y));
  }

  public static double div_hi(double x, double y) {
    if (x==0.0) return(0.0);
    return(nextfp(x/y));
  }

  public static double exp_lo(double x) {
    if (x==Double.NEGATIVE_INFINITY)
      return(0.0);
    else if (x < Double.POSITIVE_INFINITY)
      return(Math.max(0.0,prevfp(Math.exp(x))));
    else return(x);
  }

  public static double exp_hi(double x) {
    if (x==Double.NEGATIVE_INFINITY)
      return(0.0);
    else if (x < Double.POSITIVE_INFINITY)
      return(nextfp(Math.exp(x)));
    else return(x);
  }

  public static double log_lo(double x) {
    if (x < 0.0)
      return(Double.NaN);
    else if (x < Double.POSITIVE_INFINITY)
      return(prevfp(Math.log(x)));
    else return(x);
  }
  public static double log_hi(double x) {
    if (x < 0.0)
      return(Double.NaN);
    else if (x < Double.POSITIVE_INFINITY)
      return(nextfp(Math.log(x)));
    else return(x);
  }


  public static double sin_lo(double x) {
    return(prevfp(Math.sin(x)));
  }
  public static double sin_hi(double x) {
    return(nextfp(Math.sin(x)));
  }

  public static double cos_lo(double x) {
    return(prevfp(Math.cos(x)));
  }
  public static double cos_hi(double x) {
    return(nextfp(Math.cos(x)));
  }

  public static double tan_lo(double x) {
    return(prevfp(Math.tan(x)));
  }
  public static double tan_hi(double x) {
    return(nextfp(Math.tan(x)));
  }

  public static double asin_lo(double x) {
    return(prevfp(Math.asin(x)));
  }
  public static double asin_hi(double x) {
    return(nextfp(Math.asin(x)));
  }

  public static double acos_lo(double x) {
    return(prevfp(Math.acos(x)));
  }
  public static double acos_hi(double x) {
    return(nextfp(Math.acos(x)));
  }

  public static double atan_lo(double x) {
    return(prevfp(Math.atan(x)));
  }
  public static double atan_hi(double x) {
    return(nextfp(Math.atan(x)));
  }








  /*
    These are meant only to be called with
          -1/4 <= x < 1/4.
    They are only to be used in the 
    ia_math package.
   */
   static double sin2pi_lo(double x) {
    return(prevfp(Math.sin(prevfp(Math.PI*2*x))));
  }
   static double sin2pi_hi(double x) {
    return(nextfp(Math.sin(nextfp(Math.PI*2*x))));
  }

   static double cos2pi_lo(double x) {
    if (x > 0) 
      return(prevfp(Math.cos(nextfp(Math.PI*2*x))));
    else
      return(prevfp(Math.cos(prevfp(Math.PI*2*x))));

  }
   static double cos2pi_hi(double x) {
    if (x > 0) 
      return(nextfp(Math.cos(prevfp(Math.PI*2*x))));
    else
      return(nextfp(Math.cos(nextfp(Math.PI*2*x))));
  }

   static double tan2pi_lo(double x) {
    return(prevfp(Math.tan(prevfp(Math.PI*2*x))));
  }
   static double tan2pi_hi(double x) {
    return(nextfp(Math.tan(nextfp(Math.PI*2*x))));
  }

  /*
     These are meant to be called with
            0<=x<=1
     where asin2pi(x) = asin(x)/(2*pi), etc.
   */

   static double asin2pi_lo(double x) {
    return(prevfp(Math.asin(x)/nextfp(Math.PI*2)));
  }
   static double asin2pi_hi(double x) {
    return(nextfp(Math.asin(x)/prevfp(Math.PI*2)));
  }

   static double acos2pi_lo(double x) {
    return(prevfp(Math.acos(x)/nextfp(Math.PI*2)));
  }
   static double acos2pi_hi(double x) {
    return(nextfp(Math.acos(x)/prevfp(Math.PI*2)));
  }

   static double atan2pi_lo(double x) {
    return(prevfp(Math.atan(x)/nextfp(Math.PI*2)));
  }
   static double atan2pi_hi(double x) {
    return(nextfp(Math.atan(x)/prevfp(Math.PI*2)));
  }






  /**
   * returns lower bound on x**y assuming x>0
   */
  public static double pow_lo(double x,double y) {
    if (x < 0)
      return Double.NaN;
    else if (x == 0.0)
      return 0.0;
    else if (y > 0) {
      if (x >= 1)
        return  exp_lo(mul_lo(y,log_lo(x)));
      else if (x==1)
        return 1.0;
      else 
        return  exp_lo(mul_lo(y,log_hi(x)));
    } 
    else if (y == 0) 
        return 1.0;
    else {
      if (x >= 1)
        return  exp_lo(mul_lo(y,log_hi(x)));
      else if (x==1)
        return 1.0;
      else 
        return  exp_lo(mul_lo(y,log_lo(x)));
    }
  }

  /**
   * returns upper bound on x**y assuming x>0
   */
  public static double pow_hi(double x,double y) {
    if (x < 0)
      return Double.NaN;
    else if (x == 0.0)
      return 0.0;
    else if (y > 0) {
      if (x >= 1)
        return  exp_hi(mul_hi(y,log_hi(x)));
      else if (x==1)
        return 1.0;
      else 
        return  exp_hi(mul_hi(y,log_lo(x)));
    } 
    else if (y == 0) 
        return 1.0;
    else {
      if (x >= 1)
        return  exp_lo(mul_hi(y,log_lo(x)));
      else if (x==1)
        return 1.0;
      else 
        return  exp_lo(mul_hi(y,log_hi(x)));
    }
  }


  public static void main(String argv[]) {
    double a = 1.0e-300;;
    System.out.println("   Zero = "+Zero); 
    System.out.println("NegZero = "+NegZero); 

    System.out.println("prevfp(Zero) = "+prevfp(Zero)); 
    System.out.println("nextfp(Zero) = "+nextfp(Zero)); 

    System.out.println("a = "+a);
    System.out.println("mul_lo(a,a) = "+mul_lo(a,a));
    System.out.println("mul_hi(a,a) = "+mul_hi(a,a));


  }



}
