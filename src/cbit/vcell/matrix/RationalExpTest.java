package cbit.vcell.matrix;

import java.math.BigInteger;

/**
 * Insert the type's description here.
 * Creation date: (3/28/2003 5:59:43 PM)
 * @author: Jim Schaff
 */
public class RationalExpTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	// Insert code to start the application here.
	try {
		RationalExp r1 = RationalExp.ONE;
		System.out.println("r1 = "+r1);
		RationalExp r2 = new RationalExp("C1");
		System.out.println("r2 = "+r2);
		RationalExp r3 = r1.add(r2);
		System.out.println("r3 = r1+r2 = "+r3);
		
		RationalExp r4 = new RationalExp("C2");
		System.out.println("r4 = "+r4);
		RationalExp r5 = r3.sub(r4);
		System.out.println("r5 = r3-r4 = "+r5);
		
		RationalExp r6 = new RationalExp("C3");
		System.out.println("r6 = "+r6);
		RationalExp r7 = r5.div(r6);
		System.out.println("r7 = r5/r6 = "+r7);
		
		RationalExp r8 = new RationalExp("C4");
		System.out.println("r8 = "+r8);
		RationalExp r9 = r7.mult(r8).mult(new RationalExp(BigInteger.valueOf(2)));
		System.out.println("r9 = 2*r7*r8 = "+r9);
		
		RationalExp r10 = (new RationalExp("C4")).mult(new RationalExp(BigInteger.valueOf(2)));
		
		System.out.println("r10 = "+r10);
		RationalExp r11 = r9.div(r10);
		System.out.println("r11 = r9/r10 = "+r11);
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	} finally {
		System.exit(0);
	}
}

public static void testRationalNumber(){
	java.util.Random rand = new java.util.Random(0);
	int numPassed = 0;
	for(int i=0;i<100;i++){
		int pow = rand.nextInt(100)-50;
		double a = ((rand.nextDouble()-0.5)*Math.pow(10,pow));	
		cbit.vcell.matrix.RationalNumber rationalNumberOld = cbit.vcell.matrix.RationalNumber.getApproximateFractionOld(a);
		cbit.vcell.matrix.RationalNumber rationalNumberNew = cbit.vcell.matrix.RationalNumber.getApproximateFraction(a);
		if (!rationalNumberOld.equals(rationalNumberNew)){
			System.out.println(i+" FAILED: double = "+a+", orig ratnum = "+rationalNumberOld.toString()+", new ratnum = "+rationalNumberNew.toString()+",  valueOf="+cbit.vcell.matrix.RationalNumber.valueOf(a));
		}else{
			System.out.println(i+" PASSED: double = "+a+", ratNum = "+rationalNumberOld+",  valueOf="+cbit.vcell.matrix.RationalNumber.valueOf(a));
			numPassed++;
		}
	}
	System.out.println("num passed = "+numPassed);

}
}
