package net.sourceforge.interval.ia_math;
/**
 * IAMath.java 
 *   -- classes implementing interval arithmetic versions
 *      of the arithmetic and elementary functions,
 *      as part of the "ia_math library" version 0.1beta1, 10/97
 * 
 * <p>
 * Copyright (C) 2000 Timothy J. Hickey
 * <p>
 * License: <a href="http://interval.sourceforge.net/java/ia_math/licence.txt">zlib/png</a>
 * <p>
 * the class IAMath contains methods for performing basic
 * arithmetic operations on intervals. Currently the
 * elementary functions rely on the underlying implementation
 * which uses the netlib fdlibm library. The resulting code
 * is therefore probably unsound for the transcendental functions.
 */

public class IAMath
{
	static {
		System.out.println("Some of the vcell_* functions (trigonometric and hyperbolic) have unacceptably wide inclusion functions.");
	}

  public static RealInterval acos(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = RMath.acos_lo(x.hi);
    z.hi = RMath.acos_hi(x.lo);
    return(z);
  }


  public static RealInterval acos2pi(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = RMath.acos2pi_lo(x.hi);
    z.hi = RMath.acos2pi_hi(x.lo);
    return(z);
  }


  public static RealInterval add(RealInterval x, RealInterval y) {
    RealInterval z = new RealInterval();
    z.lo = RMath.add_lo(x.lo,y.lo);
    z.hi = RMath.add_hi(x.hi,y.hi);
    return(z);
  }


  public static RealInterval asin(RealInterval x) 
    throws IAException {
    RealInterval z = new RealInterval();
    x.intersect(new RealInterval(-1.0,1.0));
    z.lo = RMath.asin_lo(x.lo);
    z.hi = RMath.asin_hi(x.hi);
    return(z);
  }


  public static RealInterval asin2pi(RealInterval x) 
    throws IAException {
    RealInterval z = new RealInterval();
    x.intersect(new RealInterval(-1.0,1.0));
    z.lo = RMath.asin2pi_lo(x.lo);
    z.hi = RMath.asin2pi_hi(x.hi);
    return(z);
  }


  public static RealInterval atan(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = RMath.atan_lo(x.lo);
    z.hi = RMath.atan_hi(x.hi);
    return(z);
  }


  public static RealInterval atan2pi(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = RMath.atan2pi_lo(x.lo);
    z.hi = RMath.atan2pi_hi(x.hi);
    return(z);
  }


  public static RealInterval cos(RealInterval x) {
    RealInterval y = new RealInterval();
    RealInterval z = new RealInterval();
    y = div(x,new RealInterval(RMath.prevfp(2*Math.PI),RMath.nextfp(2*Math.PI)));
    z = cos2pi(y);
    return(z);
  }


  public static RealInterval cos2pi(RealInterval x) {
    RealInterval r = new RealInterval();
    RealInterval z=null;
    RealInterval y1=null,y2=null;
    int a=0,b=0;
    double t1=0,t2=0;
    double w;

    double m1,m2,n1,n2,z1,z2,width;
    int j1,j2;
    long mlo,mhi;

    if (Double.isInfinite(x.lo) ||
        Double.isInfinite(x.hi)) {
      return new RealInterval(-1.0,1.0);
    }

    m1 = Math.rint(4*x.lo);
    j1 = (int) Math.round(m1 - 4*Math.floor(m1/4.0));
    z1 = RMath.sub_lo(x.lo,m1/4.0);
    n1 = Math.floor(m1/4.0);

    m2 = Math.rint(4*x.hi);
    j2 = (int) Math.round(m2 - 4*Math.floor(m2/4.0));
    z2 = RMath.sub_hi(x.hi,m2/4.0);
    n2 = Math.floor(m2/4.0);

    if ((z1<= -0.25) || (z1 >= 0.25) ||
        (z2<= -0.25) || (z2 >= 0.25)) 
      return new RealInterval(-1.0,1.0);

    mlo = (z1>=0)?j1:j1-1;
    mhi = (z2<=0)?j2:j2+1;

    width = (mhi-mlo+4*(n2-n1));

    if (width > 4)
      return new RealInterval(-1.0,1.0);


    y1 = eval_sin2pi(z1,(j1+1)%4);
    y2 = eval_sin2pi(z2,(j2+1)%4);

    z = union(y1,y2);

    a = (int) ((mlo +4+1)%4);
    b = (int) ((mhi +3+1)%4);
   

    //    System.out.println("in sin2pi: "+" y1="+y1+" y2="+y2+" z="+z+
    //               "\n  j1="+j1+" j2="+j2+" mlo="+mlo+" mhi="+mhi +
    //               "\n  w ="+width+" a="+a+" b="+b+"\n  sinRange="+sinRange(a,b));
    //    if (r.lo < 0) a = (a+3)%4;
    //    if (r.hi < 0) b = (b+3)%4;

    if (width <= 1)
      return z;
    else {
      //      return union(z,sinRange(a,b));
      return union(z,sinRange(a,b));
    }
  }


  static RealInterval cos2pi0DI(double x) {
     return new RealInterval(RMath.cos2pi_lo(x),RMath.cos2pi_hi(x));
  }


  /**
   * The Natural Extension of division in Interval Arithmetic 
   * @param x an interval
   * @param y an interval
   * @returns the smallest IEEE interval containing the set x/y.
   * @exception IAException
   *  is thrown with the message <code>Division by Zero</code>.
   */
  public static RealInterval div(RealInterval x, RealInterval y) {
    if ((y.lo==0.0)&&(y.hi==0.0))
      throw new IAException("div(X,Y): Division by Zero");
    else return odiv(x,y);
  }


  /* this returns an interval containing sin(x+a/4)
     assuming -1/4 <= x < 1/4, and a in {0,1,2,3}
     */
  static RealInterval eval_sin2pi(double x, int a) {
    switch (a) {
    case 0:  return sin2pi0DI(x);
    case 1:  return cos2pi0DI(x);
    case 2:  return uminus(sin2pi0DI(x));
    case 3:  return uminus(cos2pi0DI(x));
    }
    System.out.println("ERROR in eval_sin2pi("+x+","+a+")");
    return new RealInterval();
  }


  /**
   * this is the Natural Interval extension of <code>|x|**y<\code}
   * where <code>x</code> is an interval and
   * <code>y</code> is a double.
   */
  public static RealInterval evenPower(RealInterval x, double y)
    throws IAException
  {
    double zlo,zhi;
    //    System.out.println("evenPower: x^y with (x,y) = "+x+" "+y);

    if (y == 0.0)
      return(new RealInterval(1.0));
    else if (y > 0.0) {
      if (x.lo >=0) {
        zlo = RMath.pow_lo(x.lo,y);
        zhi = RMath.pow_hi(x.hi,y);
      }else if (x.hi <=0) {
        zlo = RMath.pow_lo(-x.hi,y);
        zhi = RMath.pow_hi(-x.lo,y);
      }else {
        zlo = 0.0;
        zhi = Math.max(RMath.pow_lo(-x.lo,y),RMath.pow_hi(x.hi,y));
      }
    }
    else if (y < 0.0) {
      return div(new RealInterval(1.0),evenPower(x,-y));
    }
    else
      throw new IAException("evenPower(X,y): y=Nan not allowed");

    //    System.out.println("evenPower: computed x^y = ["+zlo+","+zhi+"]");

    return new RealInterval(zlo,zhi);
  }


  /**
   * this is the Natural Interval extension of <code>xpos**(1/y)<\code}
   * where <code>x</code> is an interval and <code>xpos</code> is the
   * set of positive numbers contained in x and
   * <code>y</code> is a non-zero double.
   */
  public static RealInterval evenRoot(RealInterval x, double y)
    throws IAException
  {
    double ylo,yhi,zlo,zhi,zlo1,zhi1;

    //    System.out.println("evenRoot x^(1/y) with (x,y) = "+x+y);

    if (y == 0.0)
      throw new IAException("evenRoot(X,y): y=0 not allowed");
    else if (y > 0.0) {
      ylo = RMath.div_lo(1.0,y);
      yhi = RMath.div_hi(1.0,y);

      //   System.out.println("evenRoot with (ylo,yhi) = "+ylo+yhi);

      if (x.lo >= 1.0)
        zlo = RMath.pow_lo(x.lo,ylo);
      else if (x.lo >=  0.0)
        zlo = RMath.pow_lo(x.lo,yhi);
      else
        zlo = 0.0;

      //   System.out.println("evenRoot with zlo = "+zlo);

      if (x.hi >= 1.0)
        zhi = RMath.pow_hi( x.hi,yhi);
      else if (x.lo >=  0.0)
        zhi = RMath.pow_hi( x.hi,ylo);
      else 
        throw new IAException("evenRoot(X,y): X <=0 not allowed");

      //   System.out.println("evenRoot with zhi = "+zhi);

      return new RealInterval(zlo,zhi);
    }
    else if (y < 0.0) {
      return div(new RealInterval(1.0),evenRoot(x,-y));
    }
    else
      throw new IAException("evenRoot(X,y): y=NaN not allowed");
  }


  public static RealInterval exp(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = RMath.exp_lo(x.lo);
    z.hi = RMath.exp_hi(x.hi);
    //    System.out.println("exp("+x+")= "+z);
    return(z);
  }


  /**
   * returns (x**y) assuming that y is restricted to integer values
   * currently returns (-infty,infty) if y is not bound to an
   * interval containing a single integer
   */
  public static RealInterval integerPower(RealInterval x, RealInterval y)
    throws IAException
 {
    double yy;

    //    System.out.println("integerPower: x^y with (x,y) = "+x+" "+y);

    if ((y.lo!=y.hi) || 
      (Math.IEEEremainder(y.lo,1.0)!=0.0)) 
          return RealInterval.fullInterval();
          //             throw new IAException("integerPower(x,y): y must be a constant integer interval [i,i]");

    yy = y.lo;

    //    System.out.println("integerPower: calling even/odd power");

    if (Math.IEEEremainder(yy,2.0) == 0.0)
         return evenPower(x,yy);
    else return oddPower(x,yy);
 }


  /**
   * returns (x**1/y) assuming that y is restricted to integer values
   * currently returns (-infty,infty) if y is not bound to an
   * interval containing a single integer
   */
  public static RealInterval integerRoot(RealInterval x, RealInterval y) 
    throws IAException
  {
    double yy;

    if ((y.lo!=y.hi) || 
      (Math.IEEEremainder(y.lo,1.0)!=0.0)) 
      return RealInterval.fullInterval();
      //       throw new IAException("intgerRoot(x,y): y must be a constant integer interval [i,i]");

    yy = y.lo;

    if (Math.IEEEremainder(yy,2.0) == 0.0)
         return evenRoot(x,yy);
    else return oddRoot(x,yy);
    
  }


  public static RealInterval intersect(RealInterval x, RealInterval y) 
    throws IAException
  {
    return
      new RealInterval(Math.max(x.lo,y.lo),Math.min(x.hi,y.hi));
  }


  /**
   * this performs (y := y intersect z/x) and succeeds if
   * y is nonempty.
   */
  public static boolean intersect_odiv(
       RealInterval y,RealInterval z,RealInterval x)
    throws IAException
  {
    if ((x.lo >= 0) || (x.hi <= 0)) {
      y.intersect(IAMath.odiv(z,x));
      return true;
    }else
    if (z.lo >0) {
      double tmp_neg = RMath.div_hi(z.lo,x.lo);
      double tmp_pos = RMath.div_lo(z.lo,x.hi);
      if (   ((y.lo > tmp_neg) || (y.lo == 0))
          && (y.lo < tmp_pos)) y.lo = tmp_pos;
      if (   ((y.hi < tmp_pos) || (y.hi == 0))
          && (y.hi > tmp_neg)) y.hi = tmp_neg;
      if (y.lo <= y.hi) return true;
      else throw new IAException("intersect_odiv(Y,Z,X): intersection is an Empty Interval");
    }
    else if (z.hi < 0) {
      double tmp_neg = RMath.div_hi(z.hi,x.hi);
      double tmp_pos = RMath.div_lo(z.hi,x.lo);
      if (   ((y.lo > tmp_neg) || (y.lo == 0))
          && (y.lo < tmp_pos)) y.lo = tmp_pos;
      if (   ((y.hi < tmp_pos) || (y.hi == 0))
          && (y.hi > tmp_neg)) y.hi = tmp_neg;
      if (y.lo <= y.hi) return true;
      else throw new IAException("intersect_odiv(Y,Z,X): intersection is an Empty Interval");
    }
    else return(true);
  }


  /**
   * computes 
   * <code> u :=  (u intersect ((x**1/y) union -(x**1/y)))</code>
   * and returns true if u is nonempty
   * Also, assumes that y is a constant integer interval
   */
  public static boolean intersectIntegerRoot
     (RealInterval x, RealInterval y, RealInterval u)
    throws IAException
 {
    double yy;
    RealInterval tmp;

    //      System.out.println("intersectIntegerRoot u = u cap x^(1/y) with (u,x,y) = "+u+x+y);
    if ((y.lo!=y.hi) || 
      (Math.IEEEremainder(y.lo,1.0)!=0.0)) 
      return true; // the conservative answer
      //       throw new IAException("integerRoot(x,y): y must be a constant integer interval [i,i]");

    yy = y.lo;

    if (Math.IEEEremainder(yy,2.0) != 0.0) {
      //             System.out.println("odd case with yy = "+yy);
      //             System.out.println("x^(1/y) = "+oddRoot(x,yy));
       u.intersect(oddRoot(x,yy));
       //             System.out.println("did odd case u = u cap x^(1/y) with (u,x,y) = "+u+x+y);
    }
    else {
      //             System.out.println("even case with yy = "+yy);
      //             System.out.println("x^(1/y) = "+evenRoot(x,yy));
       tmp =  evenRoot(x,yy);
       if (u.hi < tmp.lo)
         u.intersect(uminus(tmp));
       else if (-tmp.lo < u.lo )
         u.intersect(tmp);
       else 
         u.intersect(new RealInterval(-tmp.hi,tmp.hi));

       //              System.out.println("did even case u = u cap x^(1/y) with (u,x,y) = "+u+x+y);
    }

    return true;
 }


  public static RealInterval leftendpoint(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = x.lo;
    if ((Double.NEGATIVE_INFINITY < z.lo) &&
        (Double.POSITIVE_INFINITY > z.lo)) {
      z.hi = z.lo;
      return(z);
    }else {
      z.lo = RMath.nextfp(x.lo);
      z.hi = z.lo;
     return(z);
    }
  }


  public static RealInterval log(RealInterval x) 
    throws IAException {
    RealInterval z = new RealInterval();
    if (x.hi <= 0) 
      throw new IAException("log(X): X<=0 not allowed");

    if (x.lo < 0) x.lo = 0.0;

    z.lo = RMath.log_lo(x.lo);
    z.hi = RMath.log_hi(x.hi);
    //    System.out.println("log("+x+")= "+z);
    return(z);
  }


  public static void main(String argv[]) {

     RealInterval a,b,c;

     a = new RealInterval(5.0);
     b = log(a);
     c = exp(b);

     System.out.println("a= "+a);
     System.out.println("log(a)= "+b);
     System.out.println("exp(log(a))= "+c);

    try {
     a = new RealInterval(-5.0,0.0);
     c = exp(log(a));

     System.out.println("a= "+a);
     System.out.println("exp(log(a))= "+c);
    } catch (Exception e) {
     System.out.println("Caught exception "+e);
    }
  }


  public static RealInterval midpoint(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = (x.lo + x.hi)/2.0;
    z.hi = z.lo;

    if ((Double.NEGATIVE_INFINITY < z.lo) &&
        (Double.POSITIVE_INFINITY > z.lo)) {
      return(z);
    }
    else if ((Double.NEGATIVE_INFINITY == x.lo)) {
      if (x.hi > 0.0) {
        z.lo = 0.0; z.hi = z.lo; return(z);
      } else if (x.hi == 0.0){
        z.lo = -1.0; z.hi = z.lo; return(z);
      } else {
        z.lo = x.hi*2; z.hi = z.lo; return(z);
      }
    } else if ((Double.POSITIVE_INFINITY == x.hi)) {
      if (x.lo < 0.0) {
        z.lo = 0.0; z.hi = z.lo; return(z);
      } else if (x.lo == 0.0){
        z.lo = 1.0; z.hi = z.lo; return(z);
      } else {
        z.lo = x.lo*2; z.hi = z.lo; return(z);
      }
    } else {
      z.lo = x.lo; z.hi = x.hi;
      System.out.println("Error in RealInterval.midpoint");
      return(z);
    }
  }


  public static RealInterval mul(RealInterval x, RealInterval y) {
    RealInterval z = new RealInterval();

    if (((x.lo==0.0)&&(x.hi==0.0)) || ((y.lo==0.0)&&(y.hi==0.0))) {
      z.lo = 0.0; z.hi = RMath.NegZero;
    }
    else if (x.lo >= 0.0) {
      if (y.lo >= 0.0) {
        z.lo = Math.max(0.0,RMath.mul_lo(x.lo,y.lo));
        z.hi = RMath.mul_hi(x.hi,y.hi);
      }
      else if (y.hi <= 0.0) {
        z.lo = RMath.mul_lo(x.hi,y.lo);
        z.hi = Math.min(0.0,RMath.mul_hi(x.lo,y.hi));
      }
      else {
        z.lo = RMath.mul_lo(x.hi,y.lo);
        z.hi = RMath.mul_hi(x.hi,y.hi);
      }
    }
    else if (x.hi <= 0.0) {
      if (y.lo >= 0.0) {
        z.lo = RMath.mul_lo(x.lo,y.hi);
        z.hi = Math.min(0.0,RMath.mul_hi(x.hi,y.lo));
      }
      else if (y.hi <= 0.0) {
        z.lo = Math.max(0.0,RMath.mul_lo(x.hi,y.hi));
        z.hi = RMath.mul_hi(x.lo,y.lo);
      }
      else {
        z.lo = RMath.mul_lo(x.lo,y.hi);
        z.hi = RMath.mul_hi(x.lo,y.lo);
      }
    }
    else {
      if (y.lo >= 0.0) {
        z.lo = RMath.mul_lo(x.lo,y.hi);
        z.hi = RMath.mul_hi(x.hi,y.hi);
      }
      else if (y.hi <= 0.0) {
        z.lo = RMath.mul_lo(x.hi,y.lo);
        z.hi = RMath.mul_hi(x.lo,y.lo);
      }
      else {
        z.lo = Math.min(
                  RMath.mul_lo(x.hi,y.lo),
                  RMath.mul_lo(x.lo,y.hi));
        z.hi = Math.max(
                  RMath.mul_hi(x.lo,y.lo),
                  RMath.mul_hi(x.hi,y.hi));
      }
    }

    //    System.out.println("mul("+x+","+y+")="+z); 

    return(z);
  }


  public static final boolean nonempty(RealInterval x) {
    return (x.lo <= x.hi);
  }


  /**
   * this is the Natural Interval extension of <code>sgn(x)*(|x|**y)<\code}
   * where <code>x</code> is an interval and
   * <code>y</code> is a double.
   */
  public static RealInterval oddPower(RealInterval x, double y)
    throws IAException
  {
    double zlo,zhi;

    //    System.out.println("oddPower: x^y with (x,y) = "+x+" "+y);

    if (y == 0.0) {
      if (x.lo > 0.0)
        return(new RealInterval(1.0));
      else if (x.hi < 0.0)
        return(new RealInterval(-1.0));
      else
        return(new RealInterval(-1.0,1.0));
    }
    else if (y > 0.0) {
      if (x.lo >=0) {
         zlo = RMath.pow_lo(x.lo,y); 
         zhi = RMath.pow_hi(x.hi,y); }
      else if (x.hi <=0) {
         zlo = -RMath.pow_hi(-x.lo,y);
         zhi = -RMath.pow_lo(-x.hi,y);}
      else {
        zlo = -RMath.pow_hi(-x.lo,y);
        zhi =  RMath.pow_hi(x.hi,y); }
    }
    else if (y < 0.0) {
      return div(new RealInterval(1.0),oddPower(x,-y));
    }
    else
      throw new IAException("oddPower(X,y): X = NaN not allowed");

    //    System.out.println("oddPower: computed x^y = ["+zlo+","+zhi+"]");

    return new RealInterval(zlo,zhi);



  }


  /**
   * this is the Natural Interval extension of <code>sgn(x)*|x|**(1/y)<\code}
   * where <code>x</code> is an interval and
   * <code>y</code> is a non-zero double.
   */
  public static RealInterval oddRoot(RealInterval x, double y)
    throws IAException
  {
    double ylo,yhi,zlo,zhi;

    if (y == 0.0)
      //      throw new IAException("oddRoot(X,y): y=0 not allowed");
      return RealInterval.fullInterval();
    else if (y > 0.0) {
      ylo = RMath.div_lo(1.0,y);
      yhi = RMath.div_hi(1.0,y);
      if (x.lo >= 1.0)
        zlo = RMath.pow_lo(x.lo,ylo);
      else if (x.lo >=  0.0)
        zlo = RMath.pow_lo(x.lo,yhi);
      else if (x.lo >= -1.0) 
        zlo = -RMath.pow_hi(-x.lo,ylo);
      else 
        zlo = -RMath.pow_hi(-x.lo,yhi);

      if (x.hi >= 1.0)
        zhi = RMath.pow_hi( x.hi,yhi);
      else if (x.hi >=  0.0)
        zhi = RMath.pow_hi( x.hi,ylo);
      else if (x.hi >= -1.0) 
        zhi = -RMath.pow_lo(-x.hi,yhi);
      else 
        zhi = -RMath.pow_lo(-x.hi,ylo);


      return new RealInterval(zlo,zhi);
    }
    else if (y < 0.0) {
      return div(new RealInterval(1.0),oddRoot(x,-y));
    }
    else
      throw new IAException("oddRoot(X,y): y=NaN not allowed");
  }


  /**
   * This is identical to the standard <code>div(x,y)</code> method,
   * except that if <code>y</code> is identically zero, then
   * the infinite interval (-infinity, infinity) is returned.
   */

  public static RealInterval odiv(RealInterval x, RealInterval y) {
    RealInterval z = new RealInterval();
   

    if (((x.lo<=0.0)&&(0.0<=x.hi)) && ((y.lo<=0.0)&&(0.0<=y.hi))) {
      z.lo = Double.NEGATIVE_INFINITY; z.hi = Double.POSITIVE_INFINITY;
    }
    else {
      if (y.lo==0.0) y.lo = 0.0;
      if (y.hi==0.0) y.hi = RMath.NegZero;

    if (x.lo >= 0.0) {
      if (y.lo >= 0.0) {
        z.lo = Math.max(0.0,RMath.div_lo(x.lo,y.hi));
        z.hi = RMath.div_hi(x.hi,y.lo);
      }
      else if (y.hi <= 0.0) {
        z.lo = RMath.div_lo(x.hi,y.hi);
        z.hi = Math.min(0.0,RMath.div_hi(x.lo,y.lo));
      }
      else {
        z.lo = Double.NEGATIVE_INFINITY;
        z.hi = Double.POSITIVE_INFINITY;
      }
    }
    else if (x.hi <= 0.0) {
      if (y.lo >= 0.0) {
        z.lo = RMath.div_lo(x.lo,y.lo);
        z.hi = Math.min(0.0,RMath.div_hi(x.hi,y.hi));
      }
      else if (y.hi <= 0.0) {
        z.lo = Math.max(0.0,RMath.div_lo(x.hi,y.lo));
        z.hi = RMath.div_hi(x.lo,y.hi);
      }
      else {
        z.lo = Double.NEGATIVE_INFINITY;
        z.hi = Double.POSITIVE_INFINITY;
      }
    }
    else {
      if (y.lo >= 0.0) {
        z.lo = RMath.div_lo(x.lo,y.lo);
        z.hi = RMath.div_hi(x.hi,y.lo);
      }
      else if (y.hi <= 0.0) {
        z.lo = RMath.div_lo(x.hi,y.hi);
        z.hi = RMath.div_hi(x.lo,y.hi);
      }
      else {
        z.lo = Double.NEGATIVE_INFINITY;
        z.hi = Double.POSITIVE_INFINITY;
      }
    }
   }

    //  System.out.println("div("+x+","+y+")="+z); 

  return(z);

  }


  /**
   *  returns (x**y) computed as exp(y*log(x))
   */
  public static RealInterval power(RealInterval x, RealInterval y)
    throws IAException
 {

      if (x.hi <= 0) 
        throw new IAException("power(X,Y): X<=0 not allowed");
      else if (x.lo<0) {
        x.lo = 0.0;
      }

      RealInterval z = exp(mul(y,log(x)));

      return z;

  }


  public static RealInterval rightendpoint(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = x.hi;
    if ((Double.NEGATIVE_INFINITY < z.lo) &&
        (Double.POSITIVE_INFINITY > z.lo)) {
      z.hi = z.lo;
      return(z);
    }else {
      z.lo = RMath.prevfp(x.hi);
      z.hi = z.lo;
     return(z);
    }
  }


  public static RealInterval sin(RealInterval x) {
    RealInterval y = new RealInterval();
    RealInterval z = new RealInterval();
    y = div(x,new RealInterval(RMath.prevfp(2*Math.PI),RMath.nextfp(2*Math.PI)));
    z = sin2pi(y);
    return(z);
  }


  public static RealInterval sin2pi(RealInterval x) {
    RealInterval r = new RealInterval();
    RealInterval z=null;
    RealInterval y1=null,y2=null;
    int a=0,b=0;
    double t1=0,t2=0;
    double w;

    double m1,m2,n1,n2,z1,z2,width;
    int j1,j2;
    long mlo,mhi;

//    System.out.println("ENTERING sin2pi("+x+")");

    if (Double.isInfinite(x.lo) ||
        Double.isInfinite(x.hi)) {
      return new RealInterval(-1.0,1.0);
    }

    m1 = Math.rint(4*x.lo);
    j1 = (int) Math.round(m1 - 4*Math.floor(m1/4.0));
    z1 = RMath.sub_lo(x.lo,m1/4.0);
    n1 = Math.floor(m1/4.0);

    m2 = Math.rint(4*x.hi);
    j2 = (int) Math.round(m2 - 4*Math.floor(m2/4.0));
    z2 = RMath.sub_hi(x.hi,m2/4.0);
    n2 = Math.floor(m2/4.0);

    //    System.out.println("in sin2pi: "+" x.lo="+x.lo+" x.hi="+x.hi);
    //    System.out.println("         : "+" m1="+m1+" m2="+m2);
    //    System.out.println("         : "+" z1="+z1+" z2="+z2);
    //    System.out.println("         : "+" j1="+j1+" j2="+j2);
    //    System.out.println("         : "+" n1="+n1+" n2="+n2);

    if ((z1<= -0.25) || (z1 >= 0.25) ||
        (z2<= -0.25) || (z2 >= 0.25)) 
      return new RealInterval(-1.0,1.0);

    mlo = (z1>=0)?j1:j1-1;
    mhi = (z2<=0)?j2:j2+1;

    width = (mhi-mlo+4*(n2-n1));

    //    System.out.println("         : "+" mlo="+mlo+" mhi="+mhi);
    //    System.out.println("         : "+" width"+width);

    if (width > 4)
      return new RealInterval(-1.0,1.0);


    y1 = eval_sin2pi(z1,j1);
    y2 = eval_sin2pi(z2,j2);

    z = union(y1,y2);

    a = (int) ((mlo +4)%4);
    b = (int) ((mhi +3)%4);
   

    //    System.out.println("in sin2pi: "+" y1="+y1+" y2="+y2+" z="+z+
    //               "\n  j1="+j1+" j2="+j2+" mlo="+mlo+" mhi="+mhi +
    //               "\n  w ="+width+" a="+a+" b="+b+"\n  sinRange="+sinRange(a,b));
    //    if (r.lo < 0) a = (a+3)%4;
    //    if (r.hi < 0) b = (b+3)%4;

    if (width <= 1)
      return z;
    else {
      //      return union(z,sinRange(a,b));
      return union(z,sinRange(a,b));
    }
  }


  static RealInterval sin2pi0DI(double x) {
     return new RealInterval(RMath.sin2pi_lo(x),RMath.sin2pi_hi(x));
  }


  public static RealInterval sinRange(int a, int b) {
    switch(4*a + b) {
    case  0: return(new RealInterval(-1.0, 1.0));
    case  1: return(new RealInterval( 1.0, 1.0));
    case  2: return(new RealInterval( 0.0, 1.0));
    case  3: return(new RealInterval(-1.0, 1.0));
    case  4: return(new RealInterval(-1.0, 0.0));
    case  5: return(new RealInterval(-1.0, 1.0));
    case  6: return(new RealInterval( 0.0, 0.0));
    case  7: return(new RealInterval(-1.0, 0.0));
    case  8: return(new RealInterval(-1.0, 0.0));
    case  9: return(new RealInterval(-1.0, 1.0));
    case 10: return(new RealInterval(-1.0, 1.0));
    case 11: return(new RealInterval(-1.0,-1.0));
    case 12: return(new RealInterval( 0.0, 0.0));
    case 13: return(new RealInterval( 0.0, 1.0));
    case 14: return(new RealInterval( 0.0, 1.0));
    case 15: return(new RealInterval(-1.0, 1.0));
    }
    System.out.println("ERROR in sinRange("+a+","+b+")");
    return new RealInterval(-1,1);
  }


  public static RealInterval sub(RealInterval x, RealInterval y) {
    RealInterval z = new RealInterval();
    z.lo = RMath.sub_lo(x.lo,y.hi);
    z.hi = RMath.sub_hi(x.hi,y.lo);
    return(z);
  }


  public static RealInterval tan(RealInterval x) {
    RealInterval y = new RealInterval();
    RealInterval z = new RealInterval();
    y = div(x,new RealInterval(RMath.prevfp(2*Math.PI),RMath.nextfp(2*Math.PI)));
    z = tan2pi(y);
    return(z);
  }


  public static RealInterval tan2pi(RealInterval x) {
    return(div(sin2pi(x),cos2pi(x)));
  }


  public static RealInterval uminus(RealInterval x) {
    RealInterval z = new RealInterval();
    z.lo = -x.hi;
    z.hi = -x.lo;
    return(z);
  }


  public static RealInterval union(RealInterval x, RealInterval y) 
    throws IAException
  {
    return
      new RealInterval(Math.min(x.lo,y.lo),Math.max(x.hi,y.hi));
  }


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_abs(RealInterval a) {
	if (a.lo>=0){		
		// strictly positive
		return (RealInterval)a.clone();
	}else if (a.hi<=0){	
		// strictly negative
		return uminus(a);
	}else{				
		// both positive and negative
		return new RealInterval(0.0,Math.max(a.hi,-a.lo));
	}
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_acosh(RealInterval a) throws IAFunctionDomainException {
	RealInterval z = new RealInterval();

	// Use the formula acosh(u) = ln(u + sqrt(u^2 - 1)) ; for u >= 1

	if (a.lo >= 1.0) {
		RealInterval minus = sub( integerPower(a, new RealInterval(2.0)) , new RealInterval(1.0));
		RealInterval sqrt = integerRoot(minus, new RealInterval(2.0));
		z = log(add(a, sqrt));
		return z;
	} else {
		throw new IAFunctionDomainException("arccosh not valid for values of "+a+" < 1.0"); 
	}
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_acot(RealInterval a) {
    RealInterval z = new RealInterval();
    RealInterval PI = new RealInterval(RMath.prevfp(Math.PI), RMath.nextfp(Math.PI));
     
	if (a.lo == 0.0 && a.hi == 0.0) {
		z.lo = PI.lo/2.0;
		z.hi = PI.hi/2.0;
		return z;
	}
	
    // From -INFINITY to 0
    boolean includeNeg = true;
    RealInterval aNeg = new RealInterval(a.lo, a.hi);
    try {
	    aNeg.intersect(new RealInterval(Double.NEGATIVE_INFINITY, 0));
    } catch (IAException e) {
	    e.printStackTrace(System.out);
	    System.out.println("aNeg : "+aNeg);
	    includeNeg = false;
    }

    // From 0 to -INFINITY 
    boolean includePos = true;
    RealInterval aPos = new RealInterval(a.lo, a.hi);
    try {
	    aPos.intersect(new RealInterval(0, Double.POSITIVE_INFINITY));
    } catch (IAException e) {
	    e.printStackTrace(System.out);
	    System.out.println("aPos : "+aPos);
	    includePos = false;
    }

    if (includeNeg && includePos) {
	    RealInterval aInvNeg = IAMath.div(new RealInterval(1.0),aNeg);
	    RealInterval acotNeg = IAMath.add(PI , atan(aInvNeg));
    	RealInterval aInvPos = IAMath.div(new RealInterval(1.0),aPos);
    	RealInterval acotPos = IAMath.add(PI , atan(aInvPos));
	    z = acotNeg;
	    z.union(acotPos);
    } else if (includeNeg) {
	    RealInterval aInvNeg = IAMath.div(new RealInterval(1.0),aNeg);
	    RealInterval acotNeg = IAMath.add(PI , atan(aInvNeg));
	    z = acotNeg;
    } else if (includePos) {
    	RealInterval aInvPos = IAMath.div(new RealInterval(1.0),aPos);
    	RealInterval acotPos = IAMath.add(PI , atan(aInvPos));
	    z = acotPos;
    } else {
	    throw new RuntimeException("Interval "+a+" is empty");
    }

    
    return(z);
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 * 
 */
public static RealInterval vcell_acoth(RealInterval a) throws IAFunctionDomainException {
	RealInterval z = new RealInterval();

	// Use the formula  (1) acoth(u) = (1/2) * ln( (u+1)/(u-1) ); for u^2 > 1.0; u must be in (-INF, -1) U (1, +INF)
	// or we can use the relation (2) acoth(u) = atanh(1/u)

	if ( (a.hi < -1.0) || (a.lo > 1.0) ) {
		// Tried formula (1)
		//RealInterval num = add(a, new RealInterval(1.0));
		//RealInterval denom = sub(a, new RealInterval(1.0));
		//RealInterval logE = log(div(num, denom));
		//z = mul(div(new RealInterval(1.0), new RealInterval(2.0)), logE);

		// Using the formula (2)
		z = vcell_atanh(div(new RealInterval(1.0), a));
		return z;
	} else {
		throw new IAFunctionDomainException("arccoth not valid for values of "+a+" where -1 <= a <= 1");
	}
}

/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_acsc(RealInterval a) {
	RealInterval z = new RealInterval();
    RealInterval PI = new RealInterval(RMath.prevfp(Math.PI), RMath.nextfp(Math.PI));
     
    // From -INFINITY to -1
    boolean includeNeg = true;
    RealInterval aNeg = (RealInterval)a.clone();
    try {
	    aNeg.intersect(new RealInterval(Double.NEGATIVE_INFINITY, -1.0));
    } catch (IAException e) {
	    e.printStackTrace(System.out);
	    includeNeg = false;
    }

    // From 1 to +INFINITY 
    boolean includePos = true;
    RealInterval aPos = (RealInterval)a.clone();
    try {
	    aPos.intersect(new RealInterval(1, Double.POSITIVE_INFINITY));
    } catch (IAException e) {
	    e.printStackTrace(System.out);
	    includePos = false;
    }

    if (includeNeg && includePos) {
	    RealInterval aInvNeg = IAMath.div(new RealInterval(1.0),aNeg);
	    RealInterval acscNeg = IAMath.add(IAMath.uminus(PI) , IAMath.uminus(asin(aInvNeg)));  // arccsc(a) = - PI - arcsin(1/a) = (-PI) + (-arcsin(1/a))
    	RealInterval aInvPos = IAMath.div(new RealInterval(1.0),aPos);
    	RealInterval acscPos = asin(aInvPos);
	    z = acscNeg;
	    z.union(acscPos);
    } else if (includeNeg) {
	    RealInterval aInvNeg = IAMath.div(new RealInterval(1.0),aNeg);
	    RealInterval acscNeg = IAMath.add(IAMath.uminus(PI) , IAMath.uminus(asin(aInvNeg)));
	    z = acscNeg;
    } else if (includePos) {
    	RealInterval aInvPos = IAMath.div(new RealInterval(1.0),aPos);
    	RealInterval acscPos = asin(aInvPos);
	    z = acscPos;
    } else {
	    throw new RuntimeException("Interval "+a+" is empty");
    }

    return(z);
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_acsch(RealInterval a) throws IAFunctionDomainException {
	RealInterval z = new RealInterval();
	
	// Use the formula (1) : acsch(u) = ln( (1 +- sqrt(1 + u^2)) / u )
	// 		(Use the '+' sign if u.lo > 0 & the '-' sign if u.hi < 0) : For now, we are using only the + sign whether u is + or -
	// Or we can use the relation (2) : acsch(u) = asinh(1/u)

	// Using formula (1)
	RealInterval aInv = (RealInterval)a.clone();
	aInv = div(new RealInterval(1.0), a); 														// 1/a
	RealInterval plus = add(new RealInterval(1.0), integerPower(aInv, new RealInterval(2.0)));	// 1 + (1/a)^2
	RealInterval sqrt = integerRoot(plus, new RealInterval(2.0));								// sqrt(1 + (1/a)^2)

	RealInterval num = new RealInterval();
	if ( (a.hi < 0.0) || (a.lo > 0.0) ) {			// interval is completely negative or positive
		num = add(aInv, sqrt);
	} else if (a.lo == 0.0 || a.hi == 0) {
		throw new IAFunctionDomainException("arccsch not defined at a = 0");
	}

	// Tried Using formula (2)
	//if ( (a.hi < 0.0) || (a.lo > 0.0) ) {		// interval is completely negative or completely positive
		//z = vcell_asinh(div(new RealInterval(1.0), a));
	//}

	// z = log(div(num, a));  // USED in formula (1)
	z = log(num); 
	return z;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_and(RealInterval a, RealInterval b) {
	//
	// if one or both intervals are identically zero, then false
	// if both intervals ommit zero, then true
	// else either true or false
	//
	if ((a.lo==0.0 && a.hi==0.0) || (b.lo==0.0 && b.hi==0.0)){
		return new RealInterval(0.0); // always false
	}else if ((a.hi < 0.0 || a.lo > 0.0) && (b.hi < 0.0 || b.lo > 0.0)){
		return new RealInterval(1.0);  // always true
	}else{
		return new RealInterval(0.0,1.0); // sometimes true
	}
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_asec(RealInterval a) {
	RealInterval z = new RealInterval();
    RealInterval PI = new RealInterval(RMath.prevfp(Math.PI), RMath.nextfp(Math.PI));
     
    // From -INFINITY to -1
    boolean includeNeg = true;
    RealInterval aNeg = (RealInterval)a.clone();
    try {
	    aNeg.intersect(new RealInterval(Double.NEGATIVE_INFINITY, -1.0));
    } catch (IAException e) {
	    e.printStackTrace(System.out);
	    includeNeg = false;
    }

    // From 1 to +INFINITY 
    boolean includePos = true;
    RealInterval aPos = (RealInterval)a.clone();
    try {
	    aPos.intersect(new RealInterval(1, Double.POSITIVE_INFINITY));
    } catch (IAException e) {
	    e.printStackTrace(System.out);
	    includePos = false;
    }

    if (includeNeg && includePos) {
	    RealInterval aInvNeg = IAMath.div(new RealInterval(1.0),aNeg);
	    RealInterval asecNeg = IAMath.uminus(acos(aInvNeg));  				// arcsec(a) = - arccos(1/a) 
		RealInterval aInvPos = IAMath.div(new RealInterval(1.0),aPos);
    	RealInterval asecPos = acos(aInvPos);
	    z = asecNeg;
	    z.union(asecPos);
    } else if (includeNeg) {
	    RealInterval aInvNeg = IAMath.div(new RealInterval(1.0),aNeg);
	    RealInterval asecNeg = IAMath.uminus(acos(aInvNeg));
	    z = asecNeg;
    } else if (includePos) {
    	RealInterval aInvPos = IAMath.div(new RealInterval(1.0),aPos);
    	RealInterval asecPos = acos(aInvPos);
	    z = asecPos;
    } else {
	    throw new RuntimeException("Interval "+a+" is empty");
    }

    return(z);
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_asech(RealInterval a) throws IAFunctionDomainException {
	RealInterval z = new RealInterval();
	
	// Use the formula : asech(u) = ln( (1 + sqrt(1 - u^2)) / u )
	// This formula is valid for 0 < u <= 1

	if ( (a.lo > 0.0) && (a.hi <= 1.0) ) {
		RealInterval minus = sub(new RealInterval(1.0), power(a, new RealInterval(2.0)));
		RealInterval sqrt = power(minus, div(new RealInterval(1.0), new RealInterval(2.0)));

		RealInterval num = add(new RealInterval(1.0), sqrt);
		z = log(div(num, a));
		return z;
	} else {
		throw new IAFunctionDomainException("asech valid only for values of a : "+a+" such that 0 < a <= 1");
	}
}

/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_asinh(RealInterval a) {
	RealInterval z = new RealInterval();

	// Use the formula asinh(u) = ln(u + sqrt(u^2 + 1))

	RealInterval plus = add( integerPower(a, new RealInterval(2.0)) , new RealInterval(1.0));
	RealInterval sqrt = integerRoot(plus, new RealInterval(2.0));
	z = log(add(a, sqrt));
	
	return z;
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_atanh(RealInterval a) throws IAFunctionDomainException {
	RealInterval z = new RealInterval();

	// Use the formula atanh(u) = (1/2) * ln( (1+u)/(1-u) ), for u^2 < 1; arctan(u) is defined in (-1, 1)

	if ( (a.lo > -1.0) && (a.hi < 1.0) ) {
		RealInterval num = add(new RealInterval(1.0), a);
		RealInterval denom = sub(new RealInterval(1.0), a);
		RealInterval logE = log(div(num, denom));
		z = mul(div(new RealInterval(1.0), new RealInterval(2.0)), logE);
		return z;
	} else {
		throw new IAFunctionDomainException("arctanh not valid for values of a^2 >= 1.0; a : "+a);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_ceil(RealInterval a) {
	// both positive and negative
	return new RealInterval(Math.ceil(a.lo), Math.ceil(a.hi));
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_cosh(RealInterval a) throws IAFunctionDomainException {
	RealInterval z = new RealInterval();

	// Use the formula cosh(u) = (e^u + e^(-u)) / 2; 

	RealInterval eU = exp(a);
	RealInterval eMinusU = exp(uminus(a));
	RealInterval plus = add(eU, eMinusU);
	z = div(plus, new RealInterval(2.0));
	return z;
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_cot(RealInterval a) {
	return div(new RealInterval(1.0), tan(a));	
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_coth(RealInterval a) throws IAFunctionDomainException {
	RealInterval z = new RealInterval();

	// Use the formula coth(u) = 1/tanh(u) = (e^u + e^(-u)) / (e^u - e^(-u)); where u is in (-INF, 0) & (0, +INF) : excluding the point zero

	if (a.lo != 0.0 && a.hi != 0.0) {
		RealInterval eU = exp(a);
		RealInterval eMinusU = exp(uminus(a));
		RealInterval minus = sub(eU, eMinusU);
		RealInterval plus = add(eU, eMinusU);
		z = div(plus, minus);
	} else {
		throw new IAFunctionDomainException("coth valid only for values of a != 0; a : "+a);
	}
	
	return z;	
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_csc(RealInterval a) {
	return div(new RealInterval(1.0), sin(a));	
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_csch(RealInterval a) throws IAFunctionDomainException {
	RealInterval z = new RealInterval();

	// Use the formula csch(u) = 1/sinh(u) = 2 / (e^u - e^(-u)) ; where u is in (-INF, 0) & (0, +INF) : excluding the point zero
	
	if (a.lo != 0.0 && a.hi != 0.0) {
		RealInterval eU = exp(a);
		RealInterval eMinusU = exp(uminus(a));
		RealInterval minus = sub(eU, eMinusU);
		z = div(new RealInterval(2.0), minus);
	} else {
		throw new IAFunctionDomainException("cosech valid only for values of a != 0; a : "+a);
	}	
	
	return z;	
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_eq(RealInterval a, RealInterval b) throws IAFunctionDomainException {
	if ((a.lo == a.hi) && (b.lo == b.hi) && (a.lo == b.lo)){
		return new RealInterval(1.0);  // always true if both intervals save point
	}else if (a.hi < b.lo || a.lo > b.hi){
		return new RealInterval(0.0);  // always false if no intersection
	}else{
		return new RealInterval(0.0,1.0); // contains both true and false
	}
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_factorial(RealInterval a) throws IAFunctionDomainException {
	if (a.lo() >= 0.0) {
		if (Double.isInfinite(a.lo())){
			return new RealInterval(Double.POSITIVE_INFINITY);
		}
		//
		// check if this interval contains "any" integers
		//
		if (Double.isInfinite(a.hi())){
			long low = (long)Math.ceil(a.lo());
			double fact_lo = 1.0D;
			for (int i=1; i<=low && !(Double.isInfinite(fact_lo)); i++){
				fact_lo*=i;
			}
			return new RealInterval(fact_lo,Double.POSITIVE_INFINITY);
		}else if (Math.floor(a.hi())<a.lo()){
			throw new IAFunctionDomainException(a+"! : " + a + " contains no integers; NOT allowed ");
		}else{
			// there exists a range of integers (possibly only one), compute low and high
			long low = (long)Math.ceil(a.lo());
			double fact_lo = 1.0D;
			for (int i=1; i<=low && !(Double.isInfinite(fact_lo)); i++){
				fact_lo*=i;
			}
			long high = (long)Math.floor(a.hi());
			double fact_hi = 1.0D;
			for (int i=1; i<=high && !(Double.isInfinite(fact_hi)); i++){
				fact_hi*=i;
			}
			return new RealInterval(fact_lo,fact_hi);
		}
	}else{
		throw new IAFunctionDomainException(a+"! : " + a + " is negative; NOT allowed ");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_floor(RealInterval a) {
	// both positive and negative
	return new RealInterval(Math.floor(a.lo), Math.floor(a.hi));
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_ge(RealInterval a, RealInterval b) throws IAFunctionDomainException {
	return vcell_le(b,a);
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_gt(RealInterval a, RealInterval b) throws IAFunctionDomainException {
	return vcell_lt(b,a);
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_le(RealInterval a, RealInterval b) throws IAFunctionDomainException {
	if (a.hi <= b.lo){
		return new RealInterval(1.0);  // always true
	}else if (a.lo >  b.hi){
		return new RealInterval(0.0);  // always false
	}else{
		return new RealInterval(0.0,1.0); // contains both true and false
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_lt(RealInterval a, RealInterval b) throws IAFunctionDomainException {
	if (a.hi <  b.lo){
		return new RealInterval(1.0);  // always true
	}else if (a.lo >= b.hi){
		return new RealInterval(0.0);  // always false
	}else{
		return new RealInterval(0.0,1.0); // contains both true and false
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_max(RealInterval a, RealInterval b) {
	RealInterval r = new RealInterval();
	r.lo = a.lo;
	r.hi = a.hi;
	if (r.lo < b.lo){
		r.lo = b.lo;
	}
	if (r.hi < b.hi){
		r.hi = b.hi;
	}
	return r;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_min(RealInterval a, RealInterval b) {
	RealInterval r = new RealInterval();
	r.lo = a.lo;
	r.hi = a.hi;
	if (r.lo > b.lo){
		r.lo = b.lo;
	}
	if (r.hi > b.hi){
		r.hi = b.hi;
	}
	return r;
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_ne(RealInterval a, RealInterval b) throws IAFunctionDomainException {
	if ((a.lo == a.hi) && (b.lo == b.hi) && (a.lo == b.lo)){
		return new RealInterval(0.0);  // always false if both intervals save point
	}else if (a.hi < b.lo || a.lo > b.hi){
		return new RealInterval(1.0);  // always true if no intersection
	}else{
		return new RealInterval(0.0,1.0); // contains both true and false
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_not(RealInterval a) {
	//
	// if interval is identically zero, then true
	// if intervals ommit zero, then false
	// else either true or false
	//
	if (a.lo==0.0 && a.hi==0.0){
		return new RealInterval(1.0); // always true
	}else if (a.hi < 0.0 || a.lo > 0.0){
		return new RealInterval(0.0);  // always false
	}else{
		return new RealInterval(0.0,1.0); // sometimes true
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_or(RealInterval a, RealInterval b) {
	//
	// if both intervals are identically zero, then false
	// if onr or both intervals ommit zero, then true
	// else either true or false
	//
	if (a.lo==0.0 && a.hi==0.0 && b.lo==0.0 && b.hi==0.0){
		return new RealInterval(0.0); // always false
	}else if ((a.hi < 0.0 || a.lo > 0.0) || (b.hi < 0.0 || b.lo > 0.0)){
		return new RealInterval(1.0);  // always true
	}else{
		return new RealInterval(0.0,1.0); // sometimes true
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_power(RealInterval a, RealInterval b) throws IAFunctionDomainException {
	boolean bExponentIsInteger = ((b.lo() == b.hi()) && (b.lo() == Math.round(b.lo())));
	boolean bExponentIsStrictlyPositive = (b.lo() > 0.0);
	boolean bExponentIsStrictlyNegative = (b.hi() < 0.0);
	if (bExponentIsInteger && bExponentIsStrictlyPositive){ // b is positive integer, any a
		return IAMath.integerPower(a,b);
	}else if (bExponentIsInteger && b.lo()<=0 && (a.hi()<0.0 || a.lo()>0.0)){ // b non-positive integer, a!=0
		return IAMath.integerPower(a,b);
	}else if (a.lo()>0){ // base strictly positive, any b
		return IAMath.power(a,b);
	}else if (a.lo()==0 && a.hi()==0.0 && bExponentIsStrictlyPositive){ // base zero, b strictly positive
		return new RealInterval(0.0);
	}else if (a.hi()<0.0 && bExponentIsInteger){ // base strictly negative, b is integer
		return IAMath.integerPower(a,b);
	}else if (a.lo()<0.0 && !bExponentIsInteger){ // special cases when it's ok
		throw new IAFunctionDomainException("x^y x is negative "+a+", and y is not an integer "+b);
	}else{
		throw new IAFunctionDomainException("x^y x includes zero "+a+", and y is not strictly positive "+b);
	}
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_sec(RealInterval a) {
	return div(new RealInterval(1.0), cos(a));
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_sech(RealInterval a) {
	RealInterval z = new RealInterval();

	// Use the formula sech(u) = 1/cosh(u) = 2 / (e^u + e^(-u))
	
	RealInterval eU = exp(a);
	RealInterval eMinusU = exp(uminus(a));
	RealInterval plus = add(eU, eMinusU);
	z = div(new RealInterval(2.0), plus);
	
	return z;	
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_sinh(RealInterval a) {
	RealInterval z = new RealInterval();

	// Use the formula sinh(u) = (e^u - e^(-u)) / 2
	
	RealInterval eU = exp(a);
	RealInterval eMinusU = exp(uminus(a));
	RealInterval minus = sub(eU, eMinusU);
	z = div(minus, new RealInterval(2.0));
	
	return z;	
}


/*
 * Insert the method's description here.
 * Creation date: (5/24/2003 6:57:54 AM)
 * @return net.sourceforge.interval.ia_math.RealInterval
 * @param a net.sourceforge.interval.ia_math.RealInterval
 * @param b net.sourceforge.interval.ia_math.RealInterval
 */
public static RealInterval vcell_tanh(RealInterval a) {
	RealInterval z = new RealInterval();

	// Use the formula tanh(u) = sinh(x)/cosh(x) = (e^u - e^(-u)) / (e^u + e^(-u))
	
	RealInterval eU = exp(a);
	RealInterval eMinusU = exp(uminus(a));
	RealInterval minus = sub(eU, eMinusU);
	RealInterval plus = add(eU, eMinusU);
	z = div(minus, plus);
	
	return z;	
}
}