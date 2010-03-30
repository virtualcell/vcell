package org.vcell.sbml.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.Vector;

import org.vcell.sbml.SimSpec;
import org.vcell.sbml.vcell.VCellSBMLSolver;

public class VCellSBMLTestSuiteWrapper {
	private static int X = 2;		// indicates level of SBML
	private static int Y = 3;		// indicates version of SBML
	
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Incorrect number of arguments: " + args.length);
			for (int i = 0; i < args.length; i++) {
				System.out.println(args[i]);
			}
			System.out.println("usage: VCellSBMLTestSuiteWrapper %d %n %o");
			System.out.println("where %d : path to the directory containing all test cases");
			System.out.println("      %n : current test case number (of the form NNNNN)");
			System.out.println("      %o : directory where the CSV output file should be written");
			System.exit(1);
		}
		try {
	        // read in arguments and 
			File testCasesRootDir = new File(args[0]);
			if (!testCasesRootDir.isDirectory()) {
				throw new RuntimeException("Invalid directory : '" + testCasesRootDir.getAbsolutePath() + "'. Please specify root directory path of the test cases.");
			}
			File csvOutputDir = new File(args[2]);
			if (!csvOutputDir.isDirectory()) {
				throw new RuntimeException("Invalid directory : '" + csvOutputDir.getAbsolutePath() + "'. Please specify directory to store output CSV files.");
			}	
			String testCaseNum = args[1];
			
			File stdoutFile = new File(csvOutputDir, testCaseNum + ".stdout"); 
			System.setOut(new PrintStream(stdoutFile));
	
			File testCaseDir = new File(testCasesRootDir, testCaseNum);		
			File testCaseSBMLFile = new File(testCaseDir, testCaseNum+"-sbml-l" + X + "v" + Y + ".xml");
		
			// parse settings file to get SimSpec (start/end time, interval, vars, etc to run simulation
			File testCaseSettingsFile = new File(testCaseDir, testCaseNum+"-settings.txt");
			SimSpec testCaseSimSpec = parseSettingsFile(testCaseSettingsFile);
	       
			// read the SBML file to be imported/tested
			VCellSBMLSolver vcellSBMLSolver = new VCellSBMLSolver();
			// TODO try with round-trip later.
			vcellSBMLSolver.setRoundTrip(false);
			// import to VCell, solve and write results into CSV file
			File resultFile = vcellSBMLSolver.solveVCell(testCaseNum, csvOutputDir, testCaseSBMLFile.getAbsolutePath(), testCaseSimSpec);
		} catch (Exception e) {
			e.printStackTrace(System.out);
//			throw new RuntimeException("Unable to read SBML file into VCell and solve : " + e.getMessage());
		}
	}

	private static SimSpec parseSettingsFile(File settingsFile) throws IOException {
		long fileLength = settingsFile.length();
		// Check if file exists
		if (!settingsFile.exists()) {
			throw new FileNotFoundException("Settings File '"+settingsFile.getPath()+"' not found");
		}
		// Read characters from file into character array and transfer into string buffer.
		StringBuffer stringBuffer = new StringBuffer();
		FileInputStream is = null;
		try {
			is = new FileInputStream(settingsFile);
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(reader);
			char charArray[] = new char[10000];
			while (true) {
				int numRead = br.read(charArray, 0, charArray.length);
				if (numRead > 0) {
					stringBuffer.append(charArray,0,numRead);
				} else if (numRead == -1) {
					break;
				}
			}
		}finally{
			if (is != null){
				is.close();
			}
		}

		if (stringBuffer.length() != fileLength){
			System.out.println("<<<SYSOUT ALERT>>>settingsFile.read(), read "+stringBuffer.length()+" of "+fileLength+" bytes of input file");
		}

		String newLineDelimiters = "\n\r";
		StringTokenizer lineTokenizer = new StringTokenizer(stringBuffer.toString(),newLineDelimiters);
		
		String lineToken = new String("");
		String wordToken = new String("");
		String blankDelimiters = " \t,";
		double startTime = 0.0;
		double duration = 0.0;
		double endTime = 0.0;
		int steps = 0;
		double absTolerance = 1e-7;
		double relTolerance = 1e-4;
		Vector<String> varsVector = new Vector<String>();
		String[] varsStr = null;

		while (lineTokenizer.hasMoreTokens()) {
			lineToken = lineTokenizer.nextToken();
			java.util.StringTokenizer nextLine = new java.util.StringTokenizer(lineToken, blankDelimiters);
			while (nextLine.hasMoreTokens()) {
				wordToken = nextLine.nextToken();
				if (wordToken.equals("start:")) {
					wordToken = nextLine.nextToken().trim();
					startTime = Double.parseDouble(wordToken);
					if (startTime != 0.0) {
						throw new RuntimeException("Unable to start simulation : start time for VCell simulation HAS to be 0.0");
					}
				} else if (wordToken.equals("duration:")) {
					wordToken = nextLine.nextToken().trim();
					duration = Double.parseDouble(wordToken);
					endTime = startTime + duration;
				} else if (wordToken.equals("steps:")) {
					wordToken = nextLine.nextToken().trim();
					steps = Integer.parseInt(wordToken);
				} else if (wordToken.equals("variables:")){
					while (nextLine.hasMoreTokens()) {
						wordToken = nextLine.nextToken().trim();
						varsVector.addElement(wordToken);
					}
					varsStr = varsVector.toArray(new String[varsVector.size()]);
				} else if (wordToken.equals("absolute:")) {
					wordToken = nextLine.nextToken().trim();
					absTolerance = Double.parseDouble(wordToken);
				} else if (wordToken.equals("relative:")) {
					wordToken = nextLine.nextToken().trim();
					relTolerance = Double.parseDouble(wordToken);
				}
			}
		}
		SimSpec ts = new SimSpec(varsStr, endTime, steps, absTolerance, relTolerance);
		return ts;

	}
}
