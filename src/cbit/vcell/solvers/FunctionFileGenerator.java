/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import org.vcell.util.TokenMangler;

import cbit.vcell.math.MathFunctionDefinitions;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.Variable.Domain;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.parser.SimpleSymbolTable.SimpleSymbolTableEntry;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.AnnotatedFunction.FunctionCategory;
/**
 * Insert the type's description here.
 * Creation date: (1/12/2004 11:06:55 AM)
 * @author: Anuradha Lakshminarayana
 */
public class FunctionFileGenerator {
	private Vector <AnnotatedFunction> annotatedFunctionList;
	private java.lang.String basefileName;
	
	private static final String SEMICOLON_DELIMITERS = ";";
	public static class FuncFileLineInfo{
		public String functionName;
		public String functionExpr;
		public String functionSimplifiedExpr;
		public String errorString;
		public VariableType funcVarType;
		public boolean funcIsUserDefined = false;
	};


/**
 * FuntionFileGenerator constructor comment.
 */
public FunctionFileGenerator(String argFileName, Vector<AnnotatedFunction> argAnnotatedFunctionList) {
	basefileName = argFileName;
	annotatedFunctionList = argAnnotatedFunctionList;	
}


/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 11:09:26 AM)
 * @return cbit.vcell.math.Function[]
 */

public void generateFunctionFile() throws Exception {
	PrintWriter functionFile = null;
	try {
		FileOutputStream osFunc = new java.io.FileOutputStream(basefileName);
		functionFile = new PrintWriter(osFunc);
		writefunctionFile(functionFile);
	}catch (java.io.IOException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("error opening code file '"+basefileName+": "+e.getMessage());
	}finally{
		if(functionFile != null){try{functionFile.close();}catch(Exception e){e.printStackTrace();}}		
	}
		
}


/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 11:14:41 AM)
 * @return java.lang.String
 */
public java.lang.String getBasefileName() {
	return basefileName;
}


/**
 * This method was created in VisualAge.
 * @param logFile java.io.File
 */
public static synchronized Vector<AnnotatedFunction> readFunctionsFile(File functionsFile, String simJobID) throws java.io.FileNotFoundException, java.io.IOException {
	// Check if file exists
	if (!functionsFile.exists()){
		throw new java.io.FileNotFoundException("functions file "+functionsFile.getPath()+" not found");
	}

	//
	// Read characters from functionFile into character array and transfer into string buffer.
	//
	Vector<AnnotatedFunction> annotatedFunctionsVector = new Vector<AnnotatedFunction>();
	long fnFileLength = functionsFile.length();
	StringBuffer stringBuffer = new StringBuffer();
	FileInputStream is = null;
	try {
		is = new FileInputStream(functionsFile);
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

	if (stringBuffer.length() != fnFileLength){
		System.out.println("<<<SYSOUT ALERT>>>SimulationData.readFunctionFile(), read "+stringBuffer.length()+" of "+fnFileLength+" bytes of input file");
	}

	String newLineDelimiters = "\n\r";
	StringTokenizer lineTokenizer = new StringTokenizer(stringBuffer.toString(),newLineDelimiters);
	
	String token1 = new String("");
	int j=0;

	//
	// Each token is a line representing a function name and function expression, 
	// separated by a semicolon
	// 
	HashSet<String> allSymbols = new HashSet<String>();
	
	while (lineTokenizer.hasMoreTokens()) {
		token1 = lineTokenizer.nextToken();
		FunctionFileGenerator.FuncFileLineInfo funcFileLineInfo = readFunctionLine(token1);
		if (funcFileLineInfo != null && 
				funcFileLineInfo.functionName != null && 
				funcFileLineInfo.functionExpr != null && funcFileLineInfo.funcVarType != null) {
			
			Expression functionExpr = null;
			try {
				functionExpr = new Expression(funcFileLineInfo.functionExpr);
				functionExpr = MathFunctionDefinitions.fixFunctionSyntax(functionExpr);
			} catch (cbit.vcell.parser.ExpressionException e) {
				throw new RuntimeException("Error in reading expression '"+
					funcFileLineInfo.functionExpr+"' for function \""+
					funcFileLineInfo.functionName+"\"");
			}
			Domain domain = Variable.getDomainFromCombinedIdentifier(funcFileLineInfo.functionName); 
			String funcName = Variable.getNameFromCombinedIdentifier(funcFileLineInfo.functionName);
			AnnotatedFunction annotatedFunc =
				new AnnotatedFunction(
						funcName,
						functionExpr,
						domain,
						funcFileLineInfo.errorString,
						funcFileLineInfo.funcVarType,
						funcFileLineInfo.funcIsUserDefined ? FunctionCategory.OLDUSERDEFINED : FunctionCategory.PREDEFINED);

			allSymbols.add(annotatedFunc.getName());
			String[] symbols = annotatedFunc.getExpression().getSymbols();
			if (symbols != null) {
				allSymbols.addAll(Arrays.asList(symbols));
			}
			annotatedFunctionsVector.addElement(annotatedFunc);
		}

		j++;
	}
	
	if (simJobID != null && simJobID.trim().length() > 0) {	
		SimpleSymbolTable simpleSymbolTable = new SimpleSymbolTable(allSymbols.toArray(new String[0]));	
		
		// bind
		for (AnnotatedFunction func : annotatedFunctionsVector) {		
			if (func.isOldUserDefined()) {
				try {
					func.bind(simpleSymbolTable);
				} catch (ExpressionBindingException e) {
					e.printStackTrace();
				}
			}		
		}
		
		// rename symbol table entries
		for (int i = 0; i < annotatedFunctionsVector.size(); i ++) {
			AnnotatedFunction func = annotatedFunctionsVector.get(i);
			if (func.isOldUserDefined()) {
				SimpleSymbolTableEntry ste = (SimpleSymbolTableEntry)simpleSymbolTable.getEntry(func.getName());
				ste.setName(simJobID + "_" + func.getName());
			}		
		}
		
		// rename in the expressions
		for (int i = 0; i < annotatedFunctionsVector.size(); i ++) {
			AnnotatedFunction func = annotatedFunctionsVector.get(i);
			if (func.isOldUserDefined()) {
				try {
					Expression exp = func.getExpression().renameBoundSymbols(simpleSymbolTable.getNameScope());
					AnnotatedFunction newfunc = new AnnotatedFunction(simJobID + "_" + func.getName(), 
							exp, func.getDomain(), func.getName(), func.getErrorString(), func.getFunctionType(), FunctionCategory.OLDUSERDEFINED);
					annotatedFunctionsVector.set(i, newfunc);
				} catch (ExpressionBindingException e) {
					e.printStackTrace();
				}
			}		
		}		
	}
	
	return annotatedFunctionsVector;
}

public static FunctionFileGenerator.FuncFileLineInfo readFunctionLine(String functionFileLine) throws IOException{
	if (functionFileLine.startsWith("#")) {
		return null;
	}
	FunctionFileGenerator.FuncFileLineInfo funcFileInfo =
		new FunctionFileGenerator.FuncFileLineInfo();
	
	String token2 = new String("");
	StringTokenizer nextLine = new StringTokenizer(functionFileLine, SEMICOLON_DELIMITERS);
	int i=0;
	//
	// The first token in each line is the function name 
	// the second token is the function expression.
	//
	while (nextLine.hasMoreTokens()) {
		token2 = nextLine.nextToken();
		if (token2 != null) {
			token2 = token2.trim();
		}
		if (i == 0) {
			// If there is a 'blank' or 'space' in the function name, throw an exception - it is not allowed, since the name
			// might be used in expressions later and such usage does not allow spaces.
			if  (token2.indexOf(" ") > 0) {
				throw new java.io.IOException("Blank spaces are not allowed in function names.");
			}
			funcFileInfo.functionName = token2;
		} else if (i == 1) {
			funcFileInfo.functionExpr = token2;
		} else if (i == 2) {
			funcFileInfo.errorString = token2;
		} else if (i == 3) {
			String varType = TokenMangler.fixTokenStrict(token2);
			varType = varType.substring(0, varType.indexOf("_VariableType"));
			if (varType.equals("Volume")) {
				funcFileInfo.funcVarType = VariableType.VOLUME;
			} else if (varType.equals("Membrane")) {
				funcFileInfo.funcVarType = VariableType.MEMBRANE;
			} else if (varType.equals("Contour")) {
				funcFileInfo.funcVarType = VariableType.CONTOUR;
			} else if (varType.equals("Volume_Region")) {
				funcFileInfo.funcVarType = VariableType.VOLUME_REGION;
			} else if (varType.equals("Membrane_Region")) {
				funcFileInfo.funcVarType = VariableType.MEMBRANE_REGION;
			} else if (varType.equals("Contour_Region")) {
				funcFileInfo.funcVarType = VariableType.CONTOUR_REGION;
			} else if (varType.equals("Nonspatial")) {
				funcFileInfo.funcVarType = VariableType.NONSPATIAL;
			} else if (varType.equals("Unknown")) {
				funcFileInfo.funcVarType = VariableType.UNKNOWN;
			} 
		} else if (i == 4) {
			funcFileInfo.funcIsUserDefined = Boolean.valueOf(token2).booleanValue();
		} else if (i == 5) {
			funcFileInfo.functionSimplifiedExpr = token2;
		}
		i++;
	}
	return funcFileInfo;

}

/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 2:18:45 PM)
 */
public void writefunctionFile(PrintWriter out) {
	out.println("##---------------------------------------------");
	out.println("##  " + basefileName );
	out.println("##---------------------------------------------");
	out.println("");

	if (annotatedFunctionList!=null){
		for (AnnotatedFunction f : annotatedFunctionList){
			out.print(f.getQualifiedName() + "; " + f.getExpression().infix() + "; " + f.getErrorString() + "; " 
					+ f.getFunctionType().toString()+ "; " + f.isOldUserDefined());
			out.println();
		}
	}
	out.println("");
}
}
