package org.vcell.expression;


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
		IRationalExpression r1 = RationalExpressionFactory.createRationalExpression(1);
		System.out.println("r1 = "+r1);
		IRationalExpression r2 = RationalExpressionFactory.createRationalExpression("C1");
		System.out.println("r2 = "+r2);
		IRationalExpression r3 = r1.add(r2);
		System.out.println("r3 = r1+r2 = "+r3);
		
		IRationalExpression r4 = RationalExpressionFactory.createRationalExpression("C2");
		System.out.println("r4 = "+r4);
		IRationalExpression r5 = r3.sub(r4);
		System.out.println("r5 = r3-r4 = "+r5);
		
		IRationalExpression r6 = RationalExpressionFactory.createRationalExpression("C3");
		System.out.println("r6 = "+r6);
		IRationalExpression r7 = r5.div(r6);
		System.out.println("r7 = r5/r6 = "+r7);
		
		IRationalExpression r8 = RationalExpressionFactory.createRationalExpression("C4");
		System.out.println("r8 = "+r8);
		IRationalExpression r9 = r7.mult(r8).mult(RationalExpressionFactory.createRationalExpression(2));
		System.out.println("r9 = 2*r7*r8 = "+r9);
		
		IRationalExpression r10 = (RationalExpressionFactory.createRationalExpression("C4")).mult(RationalExpressionFactory.createRationalExpression(2));
		
		System.out.println("r10 = "+r10);
		IRationalExpression r11 = r9.div(r10);
		System.out.println("r11 = r9/r10 = "+r11);
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	} finally {
		System.exit(0);
	}
}
}
