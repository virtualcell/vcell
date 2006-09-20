package cbit.vcell.simdata;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.Simulation;

import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.math.FilamentRegionVariable;
import cbit.vcell.math.FilamentVariable;
import cbit.vcell.math.Function;
import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.MemVariable;
import cbit.vcell.math.MembraneRegionVariable;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.math.Variable;
import cbit.vcell.math.VariableType;
import cbit.vcell.math.VolVariable;
import cbit.vcell.math.VolumeRegionVariable;

import java.io.File;
import java.io.PrintWriter;
import java.util.Vector;
import java.io.FileInputStream;
import cbit.util.TokenMangler;
/**
 * Insert the type's description here.
 * Creation date: (1/12/2004 11:06:55 AM)
 * @author: Anuradha Lakshminarayana
 */
public class FunctionFileGenerator {
	private AnnotatedFunction[] annotatedFunctionList;
	private java.lang.String basefileName;

/**
 * FuntionFileGenerator constructor comment.
 */
public FunctionFileGenerator(String argFileName, AnnotatedFunction[] argAnnotatedFunctionList) {
	basefileName = argFileName;
	annotatedFunctionList = argAnnotatedFunctionList;	
}


/**
 * Insert the method's description here.
 * Creation date: (1/12/2004 11:09:26 AM)
 * @return cbit.vcell.math.Function[]
 */

public void generateFunctionFile() throws Exception {
	java.io.FileOutputStream osFunc = null;
	try {
		osFunc = new java.io.FileOutputStream(basefileName);
	}catch (java.io.IOException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("error opening code file '"+basefileName+": "+e.getMessage());
	}	
		
	PrintWriter functionFile = new PrintWriter(osFunc);
	writefunctionFile(functionFile);
	functionFile.close();
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
 * Insert the method's description here.
 * Creation date: (1/12/2004 11:14:41 AM)
 * @return java.lang.String
 */
public AnnotatedFunction[] getFunctionList() {
	return annotatedFunctionList;
}


/**
 * This method was created in VisualAge.
 * @param logFile java.io.File
 */
public static synchronized Vector readFunctionsFile(File functionsFile) throws java.io.FileNotFoundException, java.io.IOException {
	// Check if file exists
	if (!functionsFile.exists()){
		throw new java.io.FileNotFoundException("functions file "+functionsFile.getPath()+" not found");
	}

	//
	// Read characters from functionFile into character array and transfer into string buffer.
	//
	Vector annotatedFunctionsVector = new Vector();
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
	String token2 = new String("");
	String semicolonDelimiters = ";";
	int j=0;

	//
	// Each token is a line representing a function name and function expression, 
	// separated by a semicolon
	// 
	while (lineTokenizer.hasMoreTokens()) {
		token1 = lineTokenizer.nextToken();
		if (token1.startsWith("#")) {
			continue;
		}		
		StringTokenizer nextLine = new StringTokenizer(token1, semicolonDelimiters);
		int i=0;
		String functionName = null;
		Expression functionExpr = null;
		Expression functionSimplifiedExpr = null;
		String errorString = null;
		VariableType funcVarType = null;
		boolean funcIsUserDefined = false;
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
				functionName = TokenMangler.fixTokenStrict(token2);
			} else if (i == 1) {
				try {
					functionExpr = new Expression(token2);
				} catch (cbit.vcell.parser.ExpressionException e) {
					throw new RuntimeException("Error in reading expression for function \""+functionName+"\"");
				}
			} else if (i == 2) {
				errorString = token2;
			} else if (i == 3) {
				String varType = TokenMangler.fixTokenStrict(token2);
				varType = varType.substring(0, varType.indexOf("_VariableType"));
				if (varType.equals("Volume")) {
					funcVarType = VariableType.VOLUME;
				} else if (varType.equals("Membrane")) {
					funcVarType = VariableType.MEMBRANE;
				} else if (varType.equals("Contour")) {
					funcVarType = VariableType.CONTOUR;
				} else if (varType.equals("Volume_Region")) {
					funcVarType = VariableType.VOLUME_REGION;
				} else if (varType.equals("Membrane_Region")) {
					funcVarType = VariableType.MEMBRANE_REGION;
				} else if (varType.equals("Contour_Region")) {
					funcVarType = VariableType.CONTOUR_REGION;
				} else if (varType.equals("Nonspatial")) {
					funcVarType = VariableType.NONSPATIAL;
				} 
			} else if (i == 4) {
				funcIsUserDefined = Boolean.valueOf(token2).booleanValue();
			} else if (i == 5) {
				try {
					functionSimplifiedExpr = new Expression(token2);
				} catch (cbit.vcell.parser.ExpressionException e) {
					System.out.println("Error in reading simplified expression for function \""+functionName+"\"");
				}
			}
			i++;
		}
		if (functionName != null && functionExpr != null && funcVarType != null) {
			AnnotatedFunction annotatedFunc = new AnnotatedFunction(functionName, functionExpr, errorString, funcVarType, funcIsUserDefined);
			annotatedFunc.setSimplifiedExpression(functionSimplifiedExpr);
			annotatedFunctionsVector.addElement(annotatedFunc);
		}		
		j++;
	}

	return annotatedFunctionsVector;
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
		for (int i=0;i<annotatedFunctionList.length;i++){
			out.print(annotatedFunctionList[i].getName() + "; " + annotatedFunctionList[i].getExpression().infix() + "; " + annotatedFunctionList[i].getErrorString() + "; " + annotatedFunctionList[i].getFunctionType().toString()+ "; " + annotatedFunctionList[i].isUserDefined());
			if (annotatedFunctionList[i].getSimplifiedExpression() != null) {			
				out.print("; " + annotatedFunctionList[i].getSimplifiedExpression().infix());
			}
			out.println();
		}
	}
	out.println("");
}


/**
 * Insert the method's description here.
 * Creation date: (2/19/2004 11:17:15 AM)
 * @return cbit.vcell.simdata.VariableType
 * @param function cbit.vcell.math.Function
 * @param variableNames java.lang.String[]
 * @param variableTypes cbit.vcell.simdata.VariableType[]
 */
public static VariableType getFunctionVariableType(Function function, String[] variableNames, VariableType[] variableTypes, boolean isSpatial) {
	VariableType funcType = null;
	Expression exp = function.getExpression();
	String symbols[] = exp.getSymbols();
	if (symbols != null) {
		for (int j = 0; j < symbols.length; j++){
			boolean bFound = false;
			for (int k = 0; !bFound && k < variableNames.length; k++){
				if (symbols[j].equals(variableNames[k])) {
					bFound = true;
					if (funcType == null){
						funcType = variableTypes[k];
					}else{
						//
						// example: if VOLUME_REGION and VOLUME data are used in same function,
						// then function must be evaluated at each volume index (hence VOLUME wins).
						//
						if (variableTypes[k].isExpansionOf(funcType)){
							funcType = variableTypes[k];
						}
					}
				}
				if (symbols[j].equals(variableNames[k]+"_INSIDE") || symbols[j].equals(variableNames[k]+"_OUTSIDE")){
					bFound=true;
					if (variableTypes[k].equals(VariableType.VOLUME)){
						funcType = VariableType.MEMBRANE;
					}else if (funcType == null && variableTypes[k].equals(VariableType.VOLUME_REGION)){
						funcType = VariableType.MEMBRANE_REGION;
					}
				}
			}
		}
	}
	//
	// if determined to be a volume region or membrane region function, 
	// then if it is an explicit function of space, promote type to corresponding non-region type (e.g. volRegion --> volume)
	//
	boolean bExplicitFunctionOfSpace = false;
	if (symbols != null) {
		for (int i = 0; i < symbols.length; i++){
			if (symbols[i].equals(cbit.vcell.math.ReservedVariable.X.toString()) ||
				symbols[i].equals(cbit.vcell.math.ReservedVariable.Y.toString()) ||
				symbols[i].equals(cbit.vcell.math.ReservedVariable.Z.toString())){
				bExplicitFunctionOfSpace = true;
			}
		}
	}

		
	if (funcType == null){
		//
		// set default VariableType's for functions that have no variables (best guess).
		//
		if (!isSpatial) {
			funcType = VariableType.NONSPATIAL;
		} else {
			funcType = VariableType.VOLUME;
		}
	}else{
		if (funcType.equals(VariableType.MEMBRANE_REGION) && bExplicitFunctionOfSpace){
			funcType = VariableType.MEMBRANE;
		}else if (funcType.equals(VariableType.VOLUME_REGION) && bExplicitFunctionOfSpace){
			funcType = VariableType.VOLUME;
		}else if (funcType.equals(VariableType.CONTOUR_REGION) && bExplicitFunctionOfSpace){
			funcType = VariableType.CONTOUR;
		}
	}
	return funcType;
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/2004 5:31:41 PM)
 * @return cbit.vcell.simdata.AnnotatedFunction[]
 * @param simulation cbit.vcell.solver.Simulation
 */
public static AnnotatedFunction[] createAnnotatedFunctionsList(Simulation simulation) {
	Function[] functions = simulation.getFunctions();

	// Get the list of (volVariables) in the simulation. Needed to determine 'type' of  functions
	Variable[] allVariables = simulation.getVariables();
	Vector varVector = new Vector();
	for (int i = 0; i < allVariables.length; i++){
		if ( (allVariables[i] instanceof VolVariable) || (allVariables[i] instanceof VolumeRegionVariable) || (allVariables[i] instanceof MemVariable) || 
			 (allVariables[i] instanceof MembraneRegionVariable) || (allVariables[i] instanceof FilamentVariable) || (allVariables[i] instanceof FilamentRegionVariable) ||
			 (allVariables[i] instanceof InsideVariable) || (allVariables[i] instanceof OutsideVariable) ) {
				 
			varVector.addElement(allVariables[i]);
		}
	}
	Variable[] variables = (Variable[])cbit.util.BeanUtils.getArray(varVector, Variable.class);
	String[] variableNames = new String[variables.length];
	for (int i = 0; i < variableNames.length; i++){
		variableNames[i] = variables[i].getName();
	}

	// Lookup table for variableType for each variable in 'variables' array.	
	VariableType[] variableTypes = new VariableType[variables.length];
	for (int i = 0; i < variables.length; i++){
		if (variables[i] instanceof VolVariable) {
			variableTypes[i] = VariableType.VOLUME;
		} else if (variables[i] instanceof VolumeRegionVariable) {
			variableTypes[i] = VariableType.VOLUME_REGION;
		} else if (variables[i] instanceof MemVariable) {
			variableTypes[i] = VariableType.MEMBRANE;
		} else if (variables[i] instanceof MembraneRegionVariable) {
			variableTypes[i] = VariableType.MEMBRANE_REGION;
		} else if (variables[i] instanceof FilamentVariable) {
			variableTypes[i] = VariableType.CONTOUR;
		} else if (variables[i] instanceof FilamentRegionVariable) {
			variableTypes[i] = VariableType.CONTOUR_REGION;
		} else if (variables[i] instanceof InsideVariable) {
			variableTypes[i] = VariableType.MEMBRANE;
		} else if (variables[i] instanceof OutsideVariable) {
			variableTypes[i] = VariableType.MEMBRANE;
		} else {
			variableTypes[i] = null;
		}
	}

	//
	// Bind and substitute functions to simulation before storing them in the '.functions' file
	//
	Vector annotatedFunctionVector = new Vector();
	for (int i = 0; i < functions.length; i++){
		if (FunctionFileGenerator.isFunctionSaved(functions[i])) {
			String errString = "";
			VariableType funcType = null;		
			try {
				Expression substitutedExp = simulation.substituteFunctions(functions[i].getExpression());
				substitutedExp.bindExpression(simulation);
				functions[i].setExpression(substitutedExp.flatten());
			}catch (cbit.vcell.math.MathException e){
				e.printStackTrace(System.out);
				errString = errString+", "+e.getMessage();	
				// throw new RuntimeException(e.getMessage());
			}catch (cbit.vcell.parser.ExpressionException e){
				e.printStackTrace(System.out);
				errString = errString+", "+e.getMessage();				
				// throw new RuntimeException(e.getMessage());
			}

			//
			// get function's data type from the types of it's identifiers
			//
			funcType = getFunctionVariableType(functions[i], variableNames, variableTypes, simulation.getIsSpatial());

			AnnotatedFunction annotatedFunc = new AnnotatedFunction(functions[i].getName(), functions[i].getExpression(), errString, funcType, false);
			annotatedFunctionVector.addElement(annotatedFunc);
		}
	}

	AnnotatedFunction[] annotatedFunctionList = (AnnotatedFunction[])cbit.util.BeanUtils.getArray(annotatedFunctionVector, AnnotatedFunction.class);
	return annotatedFunctionList;	
}


/**
 * Insert the method's description here.
 * Creation date: (6/6/2001 10:57:51 AM)
 * @return boolean
 * @param function cbit.vcell.math.Function
 */
public static boolean isFunctionSaved(cbit.vcell.math.Function function) {
	String name = function.getName();
	if (!name.startsWith("SurfToVol_") && 
		!name.startsWith("VolFract_") && 
		!name.startsWith("Kflux_") && 
		!name.endsWith("_init") && 
		!name.endsWith("_total")){
		return true;
	}else{
		return false;
	}
}
}