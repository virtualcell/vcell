package ucar.units;
import java.math.BigInteger;
/**
 * Insert the type's description here.
 * Creation date: (3/27/2003 12:16:31 PM)
 * @author: Jim Schaff
 */
public class RationalNumber extends Number {
	private long num = 0;
	private long den = 1;
	private BigInteger bignum = null;
	private BigInteger bigden = null;

/**
 * RationalNumber constructor comment.
 */
public RationalNumber(long integer) {
	this.num = integer;
	this.den = 1;
}


/**
 * RationalNumber constructor comment.
 */
public RationalNumber(long numerator, long denominator) {
	long sign = (numerator<0 != denominator<0)?(-1):(1);
	long gcf = getGreatestCommonFactor(numerator,denominator);
	this.num = Math.abs(numerator)*sign/gcf;
	this.den = Math.abs(denominator)/gcf;
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
	int sign = numerator.signum()*denominator.signum();
	switch (sign){
		case 0:{
			this.num = 0;
			this.den = 1;
			break;
		}
		case -1:{
			if (numerator.signum() == 1){
				this.bignum = BigInteger.ZERO.subtract(numerator);
				this.bigden = denominator.abs();
			}else{ // numerator is already negative and denominator is positive
				this.bignum = numerator;
				this.bigden = denominator;
			}
			BigInteger gcf = numerator.gcd(denominator);
			this.bignum = this.bignum.divide(gcf);
			this.bigden = this.bigden.divide(gcf);
			break;
		}
		case 1:{
			if (numerator.signum() == -1){
				this.bignum = numerator.abs();
				this.bigden = denominator.abs();
			}else{
				this.bignum = numerator;
				this.bigden = denominator;
			}
			BigInteger gcf = numerator.gcd(denominator);
			this.bignum = this.bignum.divide(gcf);
			this.bigden = this.bigden.divide(gcf);
			break;
		}
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

	if (this.bignum==null && this.bigden==null && rational.bignum==null && rational.bigden==null){
		try {
			boolean bError = false;
			long A = this.num * rational.den;
			if (this.num!=0 && A/this.num != rational.den){
				//throw new ArithmeticException("overflow in "+this+" + "+rational);
				bError = true;
			} 
			long B = rational.num * this.den;
			if (this.den!=0 && B/this.den != rational.num){
				//throw new ArithmeticException("overflow in "+this+" + "+rational);
				bError = true;
			} 
			long newNumerator = A + B;
			if (newNumerator-A != B){
				//throw new ArithmeticException("overflow in "+this+" + "+rational);
				bError = true;
			} 
			long newDenominator = this.den * rational.den;
			if (this.den!=0 && newDenominator/this.den != rational.den){
				//throw new ArithmeticException("overflow in "+this+" + "+rational);
				bError = true;
			}
			if (!bError){
				return new RationalNumber(newNumerator,newDenominator);
			}
		}catch (ArithmeticException e){
		}
	}
	
	
	BigInteger A = getNumBigInteger().multiply(rational.getDenBigInteger());
	BigInteger B = rational.getNumBigInteger().multiply(getDenBigInteger());
	BigInteger newNumerator = A.add(B);
	BigInteger newDenominator = getDenBigInteger().multiply(rational.getDenBigInteger());

	return new RationalNumber(newNumerator,newDenominator);
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

	if (this.bignum==null && this.bigden==null && rational.bignum==null && rational.bigden==null){
		try {
			boolean bError = false;
			long newNumerator = this.num * rational.den;
			if (this.num!=0 && newNumerator/this.num != rational.den){
				//throw new ArithmeticException("overflow in "+this+" / "+rational);
				bError = true;
			} 
			long newDenominator = this.den * rational.num;
			if (this.den!=0 && newDenominator/this.den != rational.num){
				//throw new ArithmeticException("overflow in "+this+" / "+rational);
				bError = true;
			}
			if (!bError){
				return new RationalNumber(newNumerator,newDenominator);
			}
		}catch (ArithmeticException e){
		}
	}
	
	BigInteger newNumerator = getNumBigInteger().multiply(rational.getDenBigInteger());
	BigInteger newDenominator = getDenBigInteger().multiply(rational.getNumBigInteger());

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
	if (bignum==null && bigden==null){
		return ((double)num)/((double)den);
	}else{
		java.math.BigDecimal answer = (new java.math.BigDecimal(getNumBigInteger())).divide(new java.math.BigDecimal(getDenBigInteger()),java.math.BigDecimal.ROUND_HALF_EVEN);
		return answer.doubleValue();
	}
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

	if (r.den != den || r.num != num) { // if fit within longs
	    return false;
	}
	if (r.bignum==null){
		if (bignum!=null){
			return false;
		}
	}else{
		if (!r.bignum.equals(bignum)){
			return false;
		}
	}
	if (r.bigden==null){
		if (bigden!=null){
			return false;
		}
	}else{
		if (!r.bigden.equals(bigden)){
			return false;
		}
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
 * @return RationalNumber
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
 * Creation date: (3/27/2003 12:25:58 PM)
 * @return long
 */
public long getDen() {
	if (bigden != null){
		throw new RuntimeException("denominator doesn't fit into a long");
	}
	return den;
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2003 12:41:36 PM)
 * @return java.math.BigInteger
 */
public BigInteger getDenBigInteger() {
	if (bigden!=null){
		return bigden;
	}else{
		return BigInteger.valueOf(den);
	}
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
 * Creation date: (3/27/2003 12:25:58 PM)
 * @return long
 */
public long getNum() {
	if (bignum != null){
		throw new RuntimeException("numerator doesn't fit into a long");
	}
	return num;
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2003 12:41:36 PM)
 * @return java.math.BigInteger
 */
public BigInteger getNumBigInteger() {
	if (bignum!=null){
		return bignum;
	}else{
		return BigInteger.valueOf(num);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/13/2004 7:06:51 AM)
 * @return int
 */
public int hashCode() {
	if (bignum!=null && bigden!=null){
		return bignum.hashCode() + bigden.hashCode();
	}else{
		return (int)(num*den);
	}
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
	if (bignum!=null || bigden!=null){
		return new RationalNumber(getDenBigInteger(),getNumBigInteger());
	}else{
		return new RationalNumber(den,num);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/12/2003 5:06:15 PM)
 * @return boolean
 */
public boolean isZero() {
	if (bignum!=null){
		if (bignum.equals(BigInteger.ZERO)){
			return true;
		}
	}else if (num==0L){
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
	if (bignum==null && bigden==null){
		return new RationalNumber(-num,den);
	}else{
		return new RationalNumber(BigInteger.ZERO.subtract(getNumBigInteger()),getDenBigInteger());
	}
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
	if (this.bignum==null && this.bigden==null && rational.bignum==null && rational.bigden==null){
		try {
			boolean bError = false;
			long newNumerator = this.num * rational.num;
			if (this.num!=0 && newNumerator/this.num != rational.num){
				//throw new ArithmeticException("overflow in "+this+" * "+rational);
				bError = true;
			} 
			long newDenominator = this.den * rational.den;
			if (this.den!=0 && newDenominator/this.den != rational.den){
				//throw new ArithmeticException("overflow in "+this+" * "+rational);
				bError = true;
			}
			if (!bError){
				return new RationalNumber(newNumerator,newDenominator);
			}
		}catch (ArithmeticException e){
		}
	}

	BigInteger newNumerator = getNumBigInteger().multiply(rational.getNumBigInteger());
	BigInteger newDenominator = getDenBigInteger().multiply(rational.getDenBigInteger());

	return new RationalNumber(newNumerator,newDenominator);
	
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
	if (this.bignum==null && this.bigden==null && rational.bignum==null && rational.bigden==null){
		try {
			boolean bError = false;
			//long newNumerator = this.num * rational.den - rational.num * this.den;
			//long newDenominator = this.den * rational.den;
			long A = this.num * rational.den;
			if (this.num!=0 && A/this.num != rational.den){
				//throw new ArithmeticException("overflow in "+this+" + "+rational);
				bError = true;
			} 
			long B = rational.num * this.den;
			if (this.den!=0 && B/this.den != rational.num){
				//throw new ArithmeticException("overflow in "+this+" + "+rational);
				bError = true;
			} 
			long newNumerator = A - B;
			if (newNumerator+B != A){
				//throw new ArithmeticException("overflow in "+this+" + "+rational);
				bError = true;
			} 
			long newDenominator = this.den * rational.den;
			if (this.den!=0 && newDenominator/this.den != rational.den){
				//throw new ArithmeticException("overflow in "+this+" + "+rational);
				bError = true;
			}
			if (!bError){
				return new RationalNumber(newNumerator,newDenominator);
			}
		}catch (ArithmeticException e){
		}
	}

	BigInteger A = getNumBigInteger().multiply(rational.getDenBigInteger());
	BigInteger B = rational.getNumBigInteger().multiply(getDenBigInteger());
	BigInteger newNumerator = A.subtract(B);
	BigInteger newDenominator = getDenBigInteger().multiply(rational.getDenBigInteger());

	return new RationalNumber(newNumerator,newDenominator);
	
}


/**
 * Insert the method's description here.
 * Creation date: (3/27/2003 12:54:12 PM)
 * @return java.lang.String
 */
public String toString() {
	if (bignum==null && bigden==null){
		if (den == 1){
			return String.valueOf(num);
		}else{
			return num+"/"+den;
		}
	}else{
		if (getDenBigInteger().equals(BigInteger.ONE)){
			return getNumBigInteger().toString();
		}else{
			return getNumBigInteger()+"/"+getDenBigInteger();
		}
	}
}
}