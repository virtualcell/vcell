/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sbml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import cbit.vcell.model.Model.ReservedSymbol;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.NameScope;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.units.VCUnitDefinition;
import cbit.vcell.xml.XMLTags;

public abstract class SBMLUtils {

	public static final String SBML_NS_1 = "http://www.sbml.org/sbml/level1";
	public static final String SBML_NS_2 = "http://www.sbml.org/sbml/level2";
	public static final String SBML_NS_2_2 = "http://www.sbml.org/sbml/level2/version2";
	public static final String SBML_NS_2_3 = "http://www.sbml.org/sbml/level2/version3";
	public static final String SBML_NS_2_4 = "http://www.sbml.org/sbml/level2/version4";
	public static final String SBML_NS_3_1 = "http://www.sbml.org/sbml/level3/version1/core";
	public static final String SBML_NS_3_1_SPATIAL = "http://www.sbml.org/sbml/level3/version1/spatial/version1";
	public static final String SBML_NS_3_1_REQUIREDELEMENTS = "http://www.sbml.org/sbml/level3/version1/requiredElements/version1";
	public static final String SBML_VCELL_NS = XMLTags.SBML_VCELL_NS;
	public static final String SBML_SPATIAL_NS_PREFIX = XMLTags.SBML_SPATIAL_NS_PREFIX;
	public static final String SBML_REQUIREDELEMENTS_NS_PREFIX = "req";

	public static class SBMLUnitParameter implements SymbolTableEntry {
		private String paramName = null;
		private cbit.vcell.parser.Expression paramExpression = null;
		private cbit.vcell.parser.NameScope paramNameScope = null;
		private cbit.vcell.units.VCUnitDefinition paramUnitDefinition = null;
		
		public SBMLUnitParameter(String argName, Expression argExpr,  VCUnitDefinition argUnitDefn, NameScope argNameScope) {
			if (argName == null){
				throw new IllegalArgumentException("parameter name is null");
			}
			if (argName.length()<1){
				throw new IllegalArgumentException("parameter name is zero length");
			}
			this.paramName = argName;
			this.paramExpression = argExpr;
			this.paramUnitDefinition = argUnitDefn;
			this.paramNameScope = argNameScope;
		}
		public SBMLUnitParameter(String argName, Expression argExpr,  VCUnitDefinition argUnitDefn) {
			this(argName, argExpr, argUnitDefn, null);
		}
		
		public cbit.vcell.parser.Expression getExpression() {
			return this.paramExpression;
		}
		public int getIndex() {
			return -1;
		}
		public String getName(){ 
			return this.paramName; 
		}   
		public cbit.vcell.units.VCUnitDefinition getUnitDefinition() {
			return paramUnitDefinition;
		}
		public cbit.vcell.parser.NameScope getNameScope() {
			return paramNameScope;
		}
		public double getConstantValue() throws ExpressionException {
			return 0;
		}
		public boolean isConstant() throws ExpressionException {
			return false;
		}
	}
	
	public static void writeStringToFile(String xmlString, String filename, boolean bUseUTF8) throws IOException {
		File outputFile = new File(filename);
		OutputStreamWriter fileOSWriter = null;
		if (bUseUTF8){
			fileOSWriter = new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8");
		}else{
			fileOSWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
		}
		fileOSWriter.write(xmlString);
		fileOSWriter.flush();
		fileOSWriter.close();
	}

	public static String readStringFromFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String temp;
		StringBuffer buf = new StringBuffer();
		while ((temp = br.readLine()) != null) {
			buf.append(temp);
			buf.append("\n");
		}
		return buf.toString();
	}

	public static String xmlToString(Element root,boolean bTrimAllWhiteSpace) {
		XMLOutputter xmlOut = new XMLOutputter();
	    // xmlOut.setNewlines(true);
	    if (bTrimAllWhiteSpace) {
	    	xmlOut.getFormat().setTextMode(Format.TextMode.TRIM_FULL_WHITE);
	    } 

		return xmlOut.outputString(root);		        
	}

	public static Element readXML(Reader reader) throws IOException, SbmlException {
		try {
			SAXBuilder builder = new SAXBuilder(false);
			Document sDoc = builder.build(reader);
			Element root = sDoc.getRootElement();
			return root;
		}catch (JDOMException e){
			e.printStackTrace(System.out);
			throw new SbmlException(e.getMessage());
		}
	}

	/**
	 * This method should ensure to return a string compliant to:
	 *  letter ::= 'a'..'z','A'..'Z'
	 *  digit  ::= '0'..'9'
	 *  SName  ::= { '_' } letter { letter | '_' | digit
	 *
	 * this is for SBML level 1
	 *
	 *
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public static String mangleToSName(String name) {
		//Check is not null or empty
		if (name==null || name.length()==0){ 
			return "";
		}
		//remove extra spaces
		String string = name.trim();
		//
		StringBuffer newString = new StringBuffer(string);
		//Replace any character which is not a {letter, number, '_'} with an '_'.
		for (int i=0;i<newString.length();i++){
			if (!Character.isLetterOrDigit(newString.charAt(i)) && newString.charAt(i)!='_'){
				newString.setCharAt(i,'_');
			}
		}
		//If the first character is a letter just return
		if (Character.isLetter(newString.charAt(0))) {
			return (newString.toString());
		}
		//feed map
		String[] map = {"_zero_","_one_", "_two_", "_three_", "_four_", "_five_", "_six_", "_seven_","_eight_", "_nine_", "_underscore_"};
		
		//At this point the string should start with a series of '_' or a number
		int index =0;
		while (index<newString.length() && newString.charAt(index)=='_') {
			index++;		
		}
		//Mangle strings made only of '_'
		if (index>=newString.length()) {
			//replace the last underscore
			newString.replace(index-1,index, map[10]);
			//return this string
			return newString.toString();
		}
		//make sure the index points to a number
		if (Character.isDigit(newString.charAt(index))) {
			//mangle the first number to its text version
			newString.replace(index,index+1, map[Character.getNumericValue(newString.charAt(index))]);
		}
	
		return newString.toString();
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
	static SimSpec readTestSpecsFile(String fileName) throws FileNotFoundException, IOException {
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
					varsStr = varsVector.toArray(new String[varsVector.size()]);
				}
			}
		}
		SimSpec ts = new SimSpec(varsStr, time, steps);
		return ts;
	}
	/**
 * 	getConcUnitFactor : 
 * 		Calculates species concentration unit conversion from 'fromUnit' to 'toUnit'. 
 * 		If they are directly compatible, it computes the non-dimensional conversion factor/
 * 		If the 'fromUnit' is in item and 'toUnit' is in moles, it checks compatibility of fromUnit/KMOLE with toUnit.
 * 		Note : KMOLE is the Virtual VCell-defined reserved work (constant) = 1/602.
 * 		If the 'toUnit' is in item and 'fromUnit' is in moles, it checks compatibility of fromUnit*KMOLE with toUnit.
 * 		 
 * @param fromUnit
 * @param toUnit
 * @return	non-dimensional (numerical) conversion factor
 * @throws ExpressionException
 */
public static SBMLUnitParameter getConcUnitFactor(String name, VCUnitDefinition fromUnit, VCUnitDefinition toUnit, ReservedSymbol kMoleSymbol) throws ExpressionException {
	SBMLUnitParameter sbmlUnitsParam = null;
	if (fromUnit.isCompatible(toUnit)) {
		Expression factorExpr = new Expression(fromUnit.convertTo(1.0, toUnit));
		sbmlUnitsParam = new SBMLUnitParameter(name, factorExpr, toUnit.divideBy(fromUnit));
	} else if (fromUnit.divideBy(kMoleSymbol.getUnitDefinition()).isCompatible(toUnit)) {
		// if SBML substance unit is 'item'; VC substance unit is 'moles'
		fromUnit = fromUnit.divideBy(kMoleSymbol.getUnitDefinition());
		Expression factorExpr = new Expression(fromUnit.convertTo(1.0, toUnit));
		factorExpr = Expression.div(factorExpr, kMoleSymbol.getExpression());
		sbmlUnitsParam = new SBMLUnitParameter(name, factorExpr, toUnit.divideBy(fromUnit));
	} else if (fromUnit.multiplyBy(kMoleSymbol.getUnitDefinition()).isCompatible(toUnit)) {
		// if VC substance unit is 'item'; SBML substance unit is 'moles' 
		fromUnit = fromUnit.multiplyBy(kMoleSymbol.getUnitDefinition());
		Expression factorExpr = new Expression(fromUnit.convertTo(1.0, toUnit));
		factorExpr = Expression.mult(factorExpr,kMoleSymbol.getExpression());
		sbmlUnitsParam = new SBMLUnitParameter(name, factorExpr, toUnit.divideBy(fromUnit));
	}  else {
		throw new RuntimeException("Unable to scale the species unit from: " + fromUnit + " -> " + toUnit.getSymbol());
	}
	return sbmlUnitsParam;
}	

public static String getNamespaceFromLevelAndVersion(long level, long version) {
	String namespaceStr = SBMLUtils.SBML_NS_2;
	if ((level == 1) && version == 2) {
		namespaceStr = SBMLUtils.SBML_NS_1;
	} 
	if (level == 2) {
		if (version == 1) {
			namespaceStr = SBMLUtils.SBML_NS_2;
		} else if (version == 2) {
			namespaceStr = SBMLUtils.SBML_NS_2_2;
		} else if (version == 3) {
			namespaceStr = SBMLUtils.SBML_NS_2_3;
		} else if (version == 4) {
			namespaceStr = SBMLUtils.SBML_NS_2_4;
		} 
	}
	if (level == 3) {
		namespaceStr = SBMLUtils.SBML_NS_3_1;
	}
	return namespaceStr;
}

}
