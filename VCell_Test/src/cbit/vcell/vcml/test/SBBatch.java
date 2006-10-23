package cbit.vcell.vcml.test;

import java.io.File;

import cbit.util.PropertyLoader;

/**
Tests a bunch of SBML files.
 * Creation date: (7/31/2003 9:29:39 AM)
 * @author: Rashad Badrawi
 */
public class SBBatch {

	public SBBatch(String directory, String testType) {
	
		File dir = new File(directory);
		if (!dir.isDirectory())
			throw new IllegalArgumentException("This is a batch program. It only accepts " +
					  "directory names as input.\n");
		File files [] = dir.listFiles();
		String path;
		TranslationTestSuite tts = new TranslationTestSuite(testType);
		for (int i = 0; i < files.length; i++) {
			path = files[i].getAbsolutePath();
			System.out.println("Testing file: " + path);
			tts.runTests(path);
		}
	}
	public static void main (String args[]) {

		if (args.length < 2) {
			StringBuffer buf = new StringBuffer();
			buf.append("Usage: SBBatch DirectoryName TestType\n");
			buf.append("DirectoryName: Name of the directory which only contains SBML files.\n");
			buf.append("TestType can be any of the following:\n");
			buf.append("SBVC_1: Run roundtrip tests for SBML to VCML translator (level 1)\n");
			buf.append("SBVC_2: Run roundtrip tests for SBML to VCML translator (level 2)\n");
			System.err.println(buf);
			System.exit(0);
		}
		try {
			PropertyLoader.loadProperties();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		new SBBatch(args[0], args[1]);
	}
}
