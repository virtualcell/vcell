package cbit.vcell.vcml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Vector;
import cbit.util.BeanUtils;

/**	Create a mathematica notebook containing SBMLRead, SBMLNDSolve and dataTable methods 
 *	for each SBML file in 'fileNames' which, when executed in Mathematica, will generate a corresponding .csv file
 *	for each SBML file in the specified location.
 * 
 * @author anu
 */

public class MathSBMLFileWriter {

	/**
	 * @param args
	 */
	private static final String fileNames[] = {
//		"Bindschadler2001_coupled_Ca_oscillators.xml",
//		"Borghans1997_CaOscillation_model1.xml",
//		"Borghans1997_CaOscillation_model2.xml",
//		"Borghans1997_CaOscillation_model3.xml",
//		"Bornheimer2004_GTPaseCycle.xml",
		"Brands2002_MonosaccharideCasein_NEW.xml",
//		"Chassagnole2001_Threonine_Synthesis.xml",
//		"Cronwright2002_Glycerol_Synthesis.xml",
//		"Curien2003_MetThr_synthesis.xml",
//		"Edelstein1996_EPSP_AChSpecies.xml",
//		"Elowitz2000_Repressilator.xml",
//		"Ferreira2003_CML_generation2.xml",
//		"Field1974_Oregonator.xml",
//		"Fung2005_Metabolic_Oscillator.xml",
//		"Fuss2006_MitoticActivation.xml",
//		"Gardner1998_CellCycle_Goldbeter.xml",
//		"Goldbeter1991_MinMitOscil.xml",
//		"Goldbeter1991_MinMitOscil_ExplInact.xml",
		"Goldbeter2006_weightCycling_NEW.xml",
//		"Hoefnagel2002_PyruvateBranches.xml",
//		"Holzhutter2004_Erythrocyte_Metabolism.xml",
//		"Hornberg2005_ERKcascade.xml",
//		"Huang1996_MAPK_ultrasens.xml",
//		"Keizer1996_Rynodine_receptor_adaptation.xml",
//		"Kholodenko1999_EGFRsignaling.xml",
		"Kholodenko2000_MAPK_feedback_NEW.xml",
//		"Leloup2003_CircClock_DD_NEW.xml",
//		"Leloup2003_CircClock_DD_REV-ERBalpha_NEW.xml",
//		"Leloup2003_CircClock_LD_NEW.xml",
//		"Leloup2003_CircClock_LD_REV-ERBalpha_NEW.xml",
//		"Levchenko2000_MAPK_noScaffold.xml",
//		"Levchenko2000_MAPK_Scaffold.xml",
//		"Markevich2004_MAPK_orderedElementary.xml",
//		"Markevich2004_MAPK_orderedMM2kinases.xml",
//		"Markevich2004_MAPK_orderedMM.xml",
//		"Markevich2004_MAPK_phosphoRandomElementary.xml",
//		"Markevich2004_MAPK_phosphoRandomMM.xml",
//		"Markevich2005_MAPK_AllRandomElementary.xml",
//		"Martins2003_AmadoriDegradation.xml",
//		"Marwan_Genetics_2003.xml",
//		"Maurya2006_GTPaseCycle_reducedOrder.xml",
//		"Nielsen1998_Glycolysis.xml",
//		"Olsen2003_peroxidase.xml"
//		"Poolman2004_CalvinCycle.xml",
//		"Rohwer2000_Phosphotransferase_System.xml",
//		"Sneyd2002_IP3_Receptor.xml",
//		"Thomsen1988_AdenylateCyclase_Inhibition.xml",
//		"Thomsen1989_AdenylateCyclase.xml",
//		"Tyson1991_CellCycle_6var.xml",
//		"Vilar2002_Oscillator.xml"
//		"Yildirim2003_Lac_Operon.xml"
	};
	public static class TestSpecs {
		private String[] varsList = null;
		private double endTime = 0.0;
		private int numTimeSteps = 0;
		
		public TestSpecs(String[] argVarsList, double argEndTime, int argSteps) {
			if (argVarsList == null) {
				throw new IllegalArgumentException("No variables in list");
			}
			this.varsList = argVarsList;
			this.endTime = argEndTime;
			this.numTimeSteps = argSteps;
		}
		public String getVarsListString() {
			// Write out the variables from file as {v1,v2,...,vn} - one string as required by dataTable in Mathematica.
			String varsListStr = "{";
			for (int i = 0; i < varsList.length; i++) {
				varsListStr += varsList[i];
				if (i < varsList.length-1) { 
					varsListStr += ","; 
				}
			}
			varsListStr += "}";
			return varsListStr;
		}
		public double getEndTime(){
			return endTime;
		}
		public int getNumTimeSteps() {
			return numTimeSteps;
		}
	}
	public static void main(String[] args) {
		String mathematicaFileName = "C:\\MathSBML\\MathSBML-2.6\\BMDB_MathSBML.nb";
		java.io.FileOutputStream outputStream = null;
		try {
			outputStream = new java.io.FileOutputStream(mathematicaFileName);
		}catch (java.io.IOException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("error opening mathematica notebook file '"+mathematicaFileName+": "+e.getMessage());
		}	
		PrintWriter mathPrintWriter = new PrintWriter(outputStream);

		// Write the mathematica notebook.
		/** Structure of the MathSBML code written out for each SBML file in filenames.

			<< mathsbml.m
			SetDirectory["C:/VCell/SBML_Testing/New_SBMLRepModels/"]
			m0 = SBMLRead["Bindschadler2001_coupled_Ca_oscillators.xml", context -> None]
			n0 = SBMLNDSolve[m0, 30.0, MaxSteps -> Infinity];
			dataTable[{c1, c2}, {t, 0, 30.0, 0.1}, Flatten[n0], file -> "Bindschadler2001_coupled_Ca_oscillators.csv", format -> "CSV"]

		**/
		String filePath = "C:/VCell/SBML_Testing/New_SBMLRepModels/";
		mathPrintWriter.println("<<mathsbml.m\n\n");
		mathPrintWriter.println("SetDirectory[\""+filePath+"\"]\n");
		TestSpecs testSpecs = null;
		for (int i = 0; i < fileNames.length; i++) {
			String justFileName = fileNames[i].substring(0, fileNames[i].indexOf(".xml"));
			String fullFileName = filePath + justFileName;
			try {
				testSpecs = readTestSpecsFile(fullFileName+".test");
			} catch (Exception e) {
				e.printStackTrace(System.out);
				throw new RuntimeException("Error reading test file");
			}
			mathPrintWriter.println("m"+i+" = SBMLRead[\"" + fileNames[i] + "\", context -> None]");
			mathPrintWriter.println("n"+i+" = SBMLNDSolve[m"+i+", " + testSpecs.getEndTime() + ", MaxSteps -> Infinity];");
			String varStringList = testSpecs.getVarsListString();
			double endTime = testSpecs.getEndTime();
			int steps = testSpecs.getNumTimeSteps();
			double interval = (endTime - 0)/(double)steps;
			mathPrintWriter.println("dataTable[" + varStringList + ", {t, 0, "+endTime+", "+interval+"}, Flatten[n"+i+"], file -> \"" + justFileName + ".csv" + "\", format -> \"CSV\"]");
		}
		mathPrintWriter.println("");
		
		mathPrintWriter.close();

	}
	
/**
 * Read the *.test file corresponding to each SBML file to get the end time, num TimeSteps and variables
		Format of a .test file : (same format as the SBML validation test suite .test file)

		TIME 30
		STEPS 300
		SPECIES c1 c2
		URL Bindschadler2001_coupled_Ca_oscillators_Test
		REM tests Bindschadler2001_coupled_Ca_oscillators

 * @param fileName
 * @return
 */
	private static TestSpecs readTestSpecsFile(String fileName) throws FileNotFoundException, IOException {
		File testFile = new File(fileName);
		long fileLength = testFile.length();
		// Check if file exists
		if (!testFile.exists()){
			throw new FileNotFoundException("File "+testFile.getPath()+" not found");
		}
		// Read characters from file into character array and transfer into string buffer.
		StringBuffer stringBuffer = new StringBuffer();
		FileInputStream is = null;
		try {
			is = new FileInputStream(fileName);
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
			System.out.println("<<<SYSOUT ALERT>>>testFile.read(), read "+stringBuffer.length()+" of "+fileLength+" bytes of input file");
		}

		String newLineDelimiters = "\n\r";
		StringTokenizer lineTokenizer = new StringTokenizer(stringBuffer.toString(),newLineDelimiters);
		
		String token1 = new String("");
		String token2 = new String("");
		String blankDelimiters = " \t";
		double time = 0.0;
		int steps = 0;
		Vector<String> varsVector = new Vector<String>();
		String[] varsStr = null;

		while (lineTokenizer.hasMoreTokens()) {
			token1 = lineTokenizer.nextToken();
			java.util.StringTokenizer nextLine = new java.util.StringTokenizer(token1, blankDelimiters);
			while (nextLine.hasMoreTokens()) {
				token2 = nextLine.nextToken();
				if (token2.equals("TIME")) {
					token2 = nextLine.nextToken().trim();
					time = Double.parseDouble(token2);
				} else if (token2.equals("STEPS")) {
					token2 = nextLine.nextToken().trim();
					steps = Integer.parseInt(token2);
				} else if (token2.equals("SPECIES")){
					while (nextLine.hasMoreTokens()) {
						token2 = nextLine.nextToken().trim();
						varsVector.addElement(token2);
					}
					varsStr = (String[])BeanUtils.getArray(varsVector, String.class);
				}
			}
		}
		TestSpecs ts = new TestSpecs(varsStr, time, steps);
		return ts;
	}

}

