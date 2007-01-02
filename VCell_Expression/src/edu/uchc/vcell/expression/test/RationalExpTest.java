package edu.uchc.vcell.expression.test;

import org.vcell.expression.RationalExpression;


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
		RationalExpression r1 = new RationalExpression(1);
		System.out.println("r1 = "+r1);
		RationalExpression r2 = new RationalExpression("C1");
		System.out.println("r2 = "+r2);
		RationalExpression r3 = r1.add(r2);
		System.out.println("r3 = r1+r2 = "+r3);
		
		RationalExpression r4 = new RationalExpression("C2");
		System.out.println("r4 = "+r4);
		RationalExpression r5 = r3.sub(r4);
		System.out.println("r5 = r3-r4 = "+r5);
		
		RationalExpression r6 = new RationalExpression("C3");
		System.out.println("r6 = "+r6);
		RationalExpression r7 = r5.div(r6);
		System.out.println("r7 = r5/r6 = "+r7);
		
		RationalExpression r8 = new RationalExpression("C4");
		System.out.println("r8 = "+r8);
		RationalExpression r9 = r7.mult(r8).mult(new RationalExpression(2));
		System.out.println("r9 = 2*r7*r8 = "+r9);
		
		RationalExpression r10 = (new RationalExpression("C4")).mult(new RationalExpression(2));
		
		System.out.println("r10 = "+r10);
		RationalExpression r11 = r9.div(r10);
		System.out.println("r11 = r9/r10 = "+r11);
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	} finally {
		System.exit(0);
	}
}
}
