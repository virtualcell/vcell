package org.vcell.util.fractions;

public class FractionUtil {

	public static Fraction convertToFraction(double x, long maxQ) {
		long qBest = 1, pBest = Math.round(x);
		double scoreBest = Math.abs(x - pBest);
		for(long q = 1; q <= maxQ; ++q) {
			long p = Math.round(x*q);
			double score = q*Math.abs(x*q - p);
			if(score < scoreBest) {
				pBest = p;
				qBest = q;
				scoreBest = score;
			}
		}
		return new Fraction(pBest, qBest);
	}
	
}
