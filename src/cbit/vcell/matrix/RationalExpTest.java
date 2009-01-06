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
}
