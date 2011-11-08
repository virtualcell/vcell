/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.fractions;

public class Fraction {
	
	protected final long p, q;
	
	public Fraction(long p, long q) {
		if(q < 0) { p = -p; q = -q; }
		long lcd = getLargestCommongDivisor(p, q);
		this.p = p / lcd;
		this.q = q / lcd;
	}
	
	public Fraction(long numerator) { this.p = numerator; q = 1; }
	public Fraction(Fraction f) { this.p = f.getNumerator(); this.q = f.getDenominator(); }
	
	public static long getLargestCommongDivisor(long a, long b) {
		while(b != 0) { long t = b; b = a % b; a = t; }
		return a;
	}
	
	public static long getSmallestCommonMultiple(long a, long b) { return (a/getLargestCommongDivisor(a, b))*b; }
	
	public long getNumerator() { return p; }
	public long getDenominator() { return q; }
	public long getP() { return p; }
	public long getQ() { return q; }

	public Fraction times(long f) { return new Fraction(f*p, q); }
	public Fraction times(Fraction f) { return new Fraction(p*f.getP(), q*f.getQ()); }
	public Fraction dividedBy(long d) { return new Fraction(p, q*d); }
	public Fraction dividedBy(Fraction f) { return new Fraction(p*f.getQ(), q*f.getP()); }

	public Fraction plus(long t) { return new Fraction(p + t*q, q); }
	
	public Fraction plus(Fraction f) {
		long scm = getSmallestCommonMultiple(q, f.getQ());
		return new Fraction(p*(scm/q) + f.getP()*(scm/f.getQ()), scm);
	}
	
	public Fraction minus(long t) { return new Fraction(p - t*q, q); }
	
	public Fraction minus(Fraction f) {
		long scm = getSmallestCommonMultiple(q, f.getQ());
		return new Fraction(p*(scm/q) - f.getP()*(scm/f.getQ()), scm);
	}
	
	public int hashCode() { return (int) q; }
	
	public boolean equals(Object o) {
		if(o instanceof Fraction) {
			Fraction f = (Fraction) o;
			return p == f.getP() && q == f.getQ();
		}
		return false;
	}
	
	public String toString() {
		if(p == 0) {
			return "" + 0;
		} else if(q == 1) {
			return "" + p;
		} else {
			return p + "/" + q;
		}
	}
	
	public double doubleValue() { return ((double) p)/q; }
	
}
