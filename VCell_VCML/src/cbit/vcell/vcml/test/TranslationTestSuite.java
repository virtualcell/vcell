package cbit.vcell.vcml.test;
import cbit.gui.PropertyLoader;
import cbit.vcell.vcml.Translator;
import junit.framework.Test;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration; 
/**
 * Controller for all the translation testing classes.
 * @author: Rashad Badrawi 
 */ 
public class TranslationTestSuite {

	private String testType;
	private static String fileName;
	private static PrintStream ps;

	public TranslationTestSuite(String testType) {

		this(null, testType);
	}


	public TranslationTestSuite(String outputFile, String testType) {

		PrintStream ps = null;
		
		try {
			if (outputFile != null)
				ps = new PrintStream(new FileOutputStream(outputFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ps == null)
			this.ps = System.out;
		else
			this.ps = ps;
		this.testType = testType;
	}


	protected static PrintStream getLogFile() { return ps; }


	//in this case, a file.
	protected static String getTestFixtures() { return fileName; }


	public static void main (String args []) {

		if (args.length < 2) {
			StringBuffer buf = new StringBuffer();
			buf.append("Usage: TranslationTestSuite FileName TestType\n");
			buf.append("FileName: Name of the file for the test fixtures.\n");
			buf.append("TestType can be any of the following:\n");
			buf.append("VCSB_1: Run tests for VCML to SBML translator (level 1)\n");
			buf.append("VCSB_2: Run tests for VCML to SBML translator (level 2)\n");
			buf.append("SBVC_1: Run tests for SBML to VCML translator (level 1)\n");
			buf.append("SBVC_2: Run tests for SBML to VCML translator (level 2)\n");
			buf.append("VCQualCell: Run tests for VCML to CellML translator.\n");
			buf.append("VCQuanCell: Run tests for VCML to CellML translator.\n");
			System.out.println(buf);
			System.exit(0);
		}
		try {
			PropertyLoader.loadProperties();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		new TranslationTestSuite(args[1]).runTests(args[0]);
	}


	//returning null is a good thing.
	public TestResult runTests(String testFile) {

		this.fileName = testFile;
		TestSuite ts = new TestSuite();

		if (testType.equalsIgnoreCase(Translator.VC_QUAN_CELL))
			ts.addTestSuite(VCQuanCRoundTripTest.class);
		else if (testType.equalsIgnoreCase(Translator.VC_QUAL_CELL))
			ts.addTestSuite(VCQualCRoundTripTest.class);
		else {
			ps.println("Test Type: " + testType + " was not found.\n");
			System.exit(0);
		}
		ps.println("Testing file: " + fileName);
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
}