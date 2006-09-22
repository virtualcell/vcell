package cbit.vcell.cellml;

import java.io.PrintStream;
import java.util.Enumeration;

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import cbit.vcell.units.VCUnitDefinition;

/**
 * Simple test for the cbit.vcell.units package.
 * Creation date: (3/4/2004 11:37:43 AM)
 * @author: Rashad Badrawi
 */
public class VCUnitTest  extends TestCase {

	private PrintStream ps;


public VCUnitTest() {

	ps = System.out;
}


	public static void main(String args []) {

		new VCUnitTest().runTests();
		//vct.testVCUnitEvaluator();
		//System.exit(0);
	}


		//returning null is a good thing.
	public TestResult runTests() {

		TestSuite ts = new TestSuite();

		ps.println("Testing the units framework...");
		ts.addTestSuite(VCUnitTest.class);
		TestResult tr = new TestResult();
		ts.run(tr);
		Enumeration e = tr.errors();
		TestFailure tf;
		while (e.hasMoreElements()) {
			tf = (TestFailure)e.nextElement();
			tf.thrownException().printStackTrace(ps);
		}
     	e = tr.failures();
     	while (e.hasMoreElements()) {
        	tf = (TestFailure)e.nextElement();
        	tf.thrownException().printStackTrace(ps);
     	}
     
		ps.println("NumberOfTests: " + tr.runCount() + "\t" + 
							"Errors: " + tr.errorCount() + "\t" + "Failures: " +
							tr.failureCount());
		ps.flush();
		//ps.close();						
		if (tr.wasSuccessful())
			return null;
		else
			return tr; 
	}


	public void testVCUnitDefinition() {
	
		VCUnitDefinition unitDef;
		//try {
			//unitDef = null;
			//unitDef = VCUnitDefinition.getInstance("abcd" + SI.MOLE.getSymbol());
			//fail("didn't reject bogus unit = "+unitDef);
		//}catch(VCUnitException e){
		//}
		//unitDef = VCUnitDefinition.getInstance("m" + SI.MOLE.getSymbol());
		//assertNotNull(unitDef);
		//System.out.println("Symbol: " + unitDef.getSymbol());
		//try {
			//unitDef = VCUnitDefinition.getInstance(null);
			//fail("didn't reject null symbol name");
		//}catch (IllegalArgumentException e){
		//}
		
		String symbols[] = {
			//"um/us",
			//"m",
			//"pF.s-1",
			"0.001 A-1.kg.m2.s-3",
			"mV",
			
			"1.0E-6 1000.0 m-3.mol.s-2",
			"0.99999999999999 1000.0 m-3.mol.s-2",
			//"molecules.um-2",
			//"molecules.um-2.uM-3",
			//"item.um-2",
			//"pA",
			//"uM",
			//"uM.s-1",
			//"s",
			//"molecules/micron^2",
		};
		VCUnitDefinition unitDef2 = VCUnitDefinition.UNIT_TBD;
		for (int i = 0; i < symbols.length; i++){
			unitDef = VCUnitDefinition.getInstance(symbols[i]);
			unitDef.show();
			assertNotNull(unitDef);
			
			System.out.println("original symbol = '"+symbols[i]+"', unit = '"+unitDef.getSymbol()+"'");
			unitDef2 = VCUnitDefinition.getInstance(unitDef.getSymbol());
			assertNotNull(unitDef2);
			System.out.println("original symbol = '"+symbols[i]+"', unit = '"+unitDef.getSymbol()+"', unit2 = '"+unitDef.getSymbol()+"'");
			assertTrue("units '"+unitDef.getSymbol()+"' and '"+unitDef2.getSymbol()+"' are different",unitDef.compareEqual(unitDef2));
		//unitDef2.show();
		System.out.println("\n");
		}
	
	}


	public void testVCUnitEvaluator() {
/*
		try {
			//String str = "(a && b) + (a - b) + (a || b) + (!a) - ((a*b)/b)";
			//String str = "(a^2)/(b)";
			String str = "a + b/5";
			Expression exp = new Expression(str);
			String symbols [] = {"a", "b"};        //, "c"};
			VCUnitDefinition units [] = {VCUnitDefinition.MICROMOLAR, VCUnitDefinition.MICROMOLAR };
									     //VCUnitDefinition.MICROMOLAR};
			SimpleSymbolTable sst = new SimpleSymbolTable(symbols, null, units);
			exp.bindExpression(sst);
			VCUnitDefinition unit = VCUnitEvaluator.getUnitDefinition(exp);
			System.out.println("exp = '"+exp.infix()+"', units='" + " [" + unit.getSymbol() + "]");
			System.out.println("simplified ... units = '"+unit.getSymbol());
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		*/
	}
}