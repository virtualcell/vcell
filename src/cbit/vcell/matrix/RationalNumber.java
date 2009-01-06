package cbit.vcell.matrix;

import java.math.BigInteger;
/**
 * Insert the type's description here.
 * Creation date: (3/27/2003 12:16:31 PM)
 * @author: Jim Schaff
 */
public class RationalNumber extends Number {
	private BigInteger bignum = null;
	private BigInteger bigden = null;
	public final static RationalNumber ZERO = new RationalNumber(0);
	public final static RationalNumber ONE = new RationalNumber(1);
/**
 * RationalNumber constructor comment.
 */
public RationalNumber(long integer) {
//	this.num = integer;
//	this.den = 1;
	this.bignum = BigInteger.valueOf(integer);
	this.bigden = BigInteger.ONE;
}
/**
 * RationalNumber constructor comment.
 */
public RationalNumber(long numerator, long denominator) {
	this(BigInteger.valueOf(numerator),BigInteger.valueOf(denominator));
//	long sign = (numerator<0 != denominator<0)?(-1):(1);
//	long gcf = getGreatestCommonFactor(numerator,denominator);
//	this.num = Math.abs(numerator)*sign/gcf;
//	this.den = Math.abs(denominator)/gcf;
}
/**
 * RationalNumber constructor comment.
 */
public RationalNumber(BigInteger numerator, BigInteger denominator) {
	if (numerator == null || denominator == null){
		throw new IllegalArgumentException("numerator or denominator was null");
	}
	if (denominator.equals(BigInteger.ZERO)){
		throw new IllegalArgumentException("denominator cannot be zero");
	}
	int ns = numerator.signum();
	int ds = denominator.signum();
	if (ns == 0){
		this.bignum = BigInteger.ZERO;
		this.bigden = BigInteger.ONE;
	}else{
		if (ns*ds==1){ // positive
			if (ds==-1){ // -/-
				this.bignum = numerator.abs();
				this.bigden = denominator.abs();
			}else{
				this.bignum = numerator;
				this.bigden = denominator;
			}
		}else{ // negative
			if (ns==-1){  // negative sign is already in numerator
				this.bignum = numerator;
				this.bigden = denominator;
			}else{ // negative sign moved from denominator to numerator
				this.bignum = numerator.negate();
				this.bigden = denominator.negate();
			}
		}
		BigInteger gcf = numerator.gcd(denominator);
		this.bignum = this.bignum.divide(gcf);
		this.bigden = this.bigden.divide(gcf);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:50:27 PM)
 * @return cbit.vcell.mapping.RationalNumber
 * @param num cbit.vcell.mapping.RationalNumber
 */
public RationalNumber add(RationalNumber rational) {
	if (rational == null){
		throw new IllegalArgumentException("rational argument cannot be null");
	}
	// if denominators are same, just add numerators
	if (this.bigden.equals(rational.bigden)){
		return new RationalNumber(this.bignum.add(rational.bignum),this.bigden);
	}else{
		BigInteger newNumerator = this.bignum.multiply(rational.bigden).add(rational.bignum.multiply(this.bigden));
		BigInteger newDenominator = this.bigden.multiply(rational.bigden);
		return new RationalNumber(newNumerator,newDenominator);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:50:27 PM)
 * @return cbit.vcell.mapping.RationalNumber
 * @param num cbit.vcell.mapping.RationalNumber
 */
public RationalNumber div(RationalNumber rational) {
	if (rational == null){
		throw new IllegalArgumentException("rational argument cannot be null");
	}
	BigInteger newNumerator = this.bignum.multiply(rational.bigden);
	BigInteger newDenominator = this.bigden.multiply(rational.bignum);
	return new RationalNumber(newNumerator,newDenominator);
}
	/**
	 * Returns the value of the specified number as a <code>double</code>.
	 * This may involve rounding.
	 *
	 * @return  the numeric value represented by this object after conversion
	 *          to type <code>double</code>.
	 */
public double doubleValue() {
	return getNumBigInteger().doubleValue()/getDenBigInteger().doubleValue();
}
/**
 * Insert the method's description here.
 * Creation date: (4/13/2004 6:54:29 AM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	// This test is just an optimization, which may or may not help
	if (obj == this)
	    return true;

	if (!(obj instanceof RationalNumber)){
	    return false;
	}
	RationalNumber r = (RationalNumber) obj;

	if (!this.bignum.equals(r.bignum)){
		return false;
	}else{
		if (this.bignum.equals(BigInteger.ZERO) && r.bignum.equals(BigInteger.ZERO)){
			// if numerators are both zero, then denominators don't matter.
			return true;
		}
	}
	if (!this.bigden.equals(r.bigden)){
		return false;
	}
	return true;
}
	/**
	 * Returns the value of the specified number as a <code>float</code>.
	 * This may involve rounding.
	 *
	 * @return  the numeric value represented by this object after conversion
	 *          to type <code>float</code>.
	 */
public float floatValue() {
	return (float)doubleValue();
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2003 1:05:26 PM)
 * @return cbit.vcell.matrix.RationalNumber
 * @param value double
 */
public static RationalNumber getApproximateFraction(double value) {
	
	final double tolerance = 1e-5;
	
	double X = Math.abs(value);
	double Z = X;
	long D0 = 0;
	long D1 = 1;
	long D2 = 1;
	long N=0;

	if (Math.abs(X - Math.floor(X))<tolerance){
		if (value<0){
			return new RationalNumber((long)-X);
		}else{
			return new RationalNumber((long)X);
		}
	}

	for (int i=0;i<30 && Math.abs(((double)N)/((double)D2) - X) > tolerance;i++){
		//System.out.println("Z = "+Z+", N = "+N+",  D = "+D1+", N/D = "+(((double)N)/((double)D1)));
		Z = 1.0/(Z - Math.floor(Z));
		D2 = D1*((long)Math.floor(Z)) + D0;
		N = Math.round(X*D2);
		D0 = D1;
		D1 = D2;
	}
	if (value>0){
		return new RationalNumber(N,D2);
	}else{
		return new RationalNumber(-N,D2);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/12/2003 12:41:36 PM)
 * @return java.math.BigInteger
 */
public BigInteger getDenBigInteger() {
	return bigden;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:26:41 PM)
 * @return long
 * @param a long
 * @param b long
 */
public static long getGreatestCommonFactor(long a, long b) {
	long t1 = Math.abs(a);
	long t2 = Math.abs(b);

	long remainder = t1%t2;
	while (remainder != 0){
		t1 = t2;
		t2 = remainder;	
		remainder = t1%t2;
	}
	return t2;
}
/**
 * Insert the method's description here.
 * Creation date: (5/12/2003 12:41:36 PM)
 * @return java.math.BigInteger
 */
public BigInteger getNumBigInteger() {
	return bignum;
}
/**
 * Insert the method's description here.
 * Creation date: (4/13/2004 7:06:51 AM)
 * @return int
 */
public int hashCode() {
	return Double.valueOf(doubleValue()).hashCode();
}
	/**
	 * Returns the value of the specified number as an <code>int</code>.
	 * This may involve rounding.
	 *
	 * @return  the numeric value represented by this object after conversion
	 *          to type <code>int</code>.
	 */
public int intValue() {
	return (int)doubleValue();
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:31:43 PM)
 * @return cbit.vcell.mapping.RationalNumber
 */
public RationalNumber inverse() {
	return new RationalNumber(this.bigden, this.bignum);
}
/**
 * Insert the method's description here.
 * Creation date: (5/12/2003 5:06:15 PM)
 * @return boolean
 */
public boolean isZero() {
	if (bignum.equals(BigInteger.ZERO)){
		return true;
	}
	return false;
}
	/**
	 * Returns the value of the specified number as a <code>long</code>.
	 * This may involve rounding.
	 *
	 * @return  the numeric value represented by this object after conversion
	 *          to type <code>long</code>.
	 */
public long longValue() {
	return (long)doubleValue();
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 2:28:03 PM)
 * @return cbit.vcell.matrixtest.RationalNumber
 */
public RationalNumber minus() {
	return new RationalNumber(this.bignum.negate(),this.bigden);
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:50:27 PM)
 * @return cbit.vcell.mapping.RationalNumber
 * @param num cbit.vcell.mapping.RationalNumber
 */
public RationalNumber mult(RationalNumber rational) {

	if (rational == null){
		throw new IllegalArgumentException("rational argument cannot be null");
	}
	if (this.equals(RationalNumber.ZERO) || rational.equals(RationalNumber.ZERO)){
		return RationalNumber.ZERO;
	}
	return new RationalNumber(this.bignum.multiply(rational.bignum),this.bigden.multiply(rational.bigden));
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:50:27 PM)
 * @return cbit.vcell.mapping.RationalNumber
 * @param num cbit.vcell.mapping.RationalNumber
 */
public RationalNumber sub(RationalNumber rational) {
	if (rational == null){
		throw new IllegalArgumentException("rational argument cannot be null");
	}
	// if denominators are same, just add numerators
	if (this.bigden.equals(rational.bigden)){
		return new RationalNumber(this.bignum.subtract(rational.bignum),this.bigden);
	}else{
		BigInteger newNumerator = this.bignum.multiply(rational.bigden).subtract(rational.bignum.multiply(this.bigden));
		BigInteger newDenominator = this.bigden.multiply(rational.bigden);
		return new RationalNumber(newNumerator,newDenominator);
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:54:12 PM)
 * @return java.lang.String
 */
public String toString() {
	if (this.bigden.equals(BigInteger.ONE)){
		return this.bignum.toString();
	}else{
		return this.bignum+"/"+this.bigden;
	}
}

}
