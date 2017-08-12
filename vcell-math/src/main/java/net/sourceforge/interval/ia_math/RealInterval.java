package net.sourceforge.interval.ia_math;

/**
 *
 * RealInterval.java <p>
 *  -- classes implementing real intervals
 *     as part of the "ia_math library" version 0.1beta1, 10/97
 *
 * <p>
 * Copyright (C) 2000 Timothy J. Hickey
 * <p>
 * License: <a href="http://interval.sourceforge.net/java/ia_math/licence.txt">zlib/png</a>
 * <p>
 * the class RealInterval represents closed intervals of real numbers
 */

public class RealInterval implements Cloneable, java.io.Serializable {
  double lo,hi;

  /**
   * construct the interval [-infty,infty]
   */
  public RealInterval() {
    this.lo = java.lang.Double.NEGATIVE_INFINITY;
    this.hi = java.lang.Double.POSITIVE_INFINITY;
  }
  public RealInterval(double x) throws IAException {
    if ((Double.NEGATIVE_INFINITY<x) && (x < Double.POSITIVE_INFINITY)) {
    this.lo = x; this.hi = x;
    }
    else throw new IAException("RealInterval(x): must have -inf<x<inf");

  }
  public RealInterval(double lo, double hi) throws IAException
  {
    if (lo <= hi) {
      this.lo = lo; this.hi = hi;
    }
    else throw new IAException("RealInterval(x="+lo+",y="+hi+"): must have x<=y");
  }
  public Object clone() {
    return new
      RealInterval(this.lo,this.hi);
  }
  private String doubleToString(double x) {
    StringBuffer s = new StringBuffer((new Double(x)).toString());
    int i = s.length(); 
    int j;
    for(j=1;j<20-i;i++) s.append(' ');
    return(s.toString());
  }
  public static RealInterval emptyInterval() {
  RealInterval z = 
    new RealInterval(
          Double.POSITIVE_INFINITY, 
          Double.NEGATIVE_INFINITY);
    return z;
  }
  public boolean equals(RealInterval x) {
    return((this.lo==x.lo)&&(this.hi==x.hi));
  }
  public static RealInterval fullInterval() {
  RealInterval z = 
    new RealInterval(
          Double.NEGATIVE_INFINITY,
          Double.POSITIVE_INFINITY); 
    return z;
  }
  public double hi() {
    return this.hi;
  }
  public void intersect(RealInterval x)
    throws IAException
  {
    this.lo = Math.max(this.lo,x.lo);
    this.hi = Math.min(this.hi,x.hi);

    if (this.lo <= this.hi) return;
    else throw new IAException("this.intersect(X): intersection is empty");
  }
  public double lo() {
    return this.lo;
  }
  /**
   * a test procedure which generates a few intervals
   * and adds and multiplies them
   */
  public static void main(String[] args) {

      /* create several RealInterval objects */
      RealInterval x = new RealInterval(-3.0,-2.0);
      RealInterval y = new RealInterval(-6.0,7.0);
      RealInterval z = new RealInterval();
      RealInterval w = new RealInterval();

      z = IAMath.add(x,y);

      System.out.println("x = [" + x.lo + " , " + x.hi + "]");
      System.out.println("y = [" + y.lo + " , " + y.hi + "]");
      System.out.println("x+y = [" + z.lo + " , " + z.hi + "]");

      w = IAMath.mul(x,y);
      System.out.println("x*y = [" + w.lo + " , " + w.hi + "]");

  }
  public boolean nonEmpty() {
    return(this.lo <= this.hi);
  }
public String toString(){
	if (nonEmpty()){
		return this.toString1();
	}else{
		return "empty "+this.toString1();
	}
}
  private String toString1(){
    return(new String(
        "[" +
         doubleToString(this.lo) +
        " , " +
         doubleToString(this.hi) +
        "]"));}
  private String toString1a(){
    return(new String(
        "[" +
        ((new Double(this.lo)).toString()) +
        " , " +
        ((new Double(this.hi)).toString()) +
        "]"));}
  private String toString2(){
    Double midpoint =  new Double((this.lo + this.hi)/2.0);
    String midpointString =  doubleToString((this.lo + this.hi)/2.0);
    String      hi1String =  doubleToString(this.hi - midpoint.doubleValue());
    if (Math.abs(midpoint.doubleValue()) > (this.hi-this.lo)/2.0)
      return(new String(
		      //        this.toString1() + " = " +
        "("+
        midpointString +
        " +/- " +
        hi1String +
        ") "));
    else
     return(this.toString1());

  }
  public void union(RealInterval x)
    throws IAException
  {
    this.lo = Math.min(this.lo,x.lo);
    this.hi = Math.max(this.hi,x.hi);
  }
}
