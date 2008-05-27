package cbit.vcell.solvers;
import cbit.vcell.parser.Expression;
import cbit.vcell.simdata.VariableType;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import cbit.vcell.math.AnnotatedFunction;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import cbit.vcell.math.Function;
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
public static synchronized Vector<AnnotatedFunction> readFunctionsFile(File functionsFile) throws java.io.FileNotFoundException, java.io.IOException {
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
	while (lineTokenizer.hasMoreTokens()) {
		token1 = lineTokenizer.nextToken();
		FunctionFileGenerator.FuncFileLineInfo funcFileLineInfo = readFunctionLine(token1);
		if (funcFileLineInfo != null && 
				funcFileLineInfo.functionName != null && 
				funcFileLineInfo.functionExpr != null && funcFileLineInfo.funcVarType != null) {
			
			Expression functionExpr = null;
			try {
				functionExpr = new Expression(funcFileLineInfo.functionExpr);
			} catch (cbit.vcell.parser.ExpressionException e) {
				throw new RuntimeException("Error in reading expression '"+
					funcFileLineInfo.functionExpr+"' for function \""+
					funcFileLineInfo.functionName+"\"");
			}
			Expression functionSimplifiedExpr = null;
			if(funcFileLineInfo.functionSimplifiedExpr != null){
				try {
					functionSimplifiedExpr = new Expression(funcFileLineInfo.functionSimplifiedExpr);
				} catch (cbit.vcell.parser.ExpressionException e) {
					System.out.println("Error in reading simplified expression '"+
						funcFileLineInfo.functionSimplifiedExpr+"' for function \""+
						funcFileLineInfo.functionName+"\"");
				}
			}
			AnnotatedFunction annotatedFunc =
				new AnnotatedFunction(
						funcFileLineInfo.functionName,
						functionExpr,
						funcFileLineInfo.errorString,
						funcFileLineInfo.funcVarType,
						funcFileLineInfo.funcIsUserDefined);
			if(functionSimplifiedExpr != null){
				annotatedFunc.setSimplifiedExpression(functionSimplifiedExpr);
			}
			annotatedFunctionsVector.addElement(annotatedFunc);
		}

		j++;
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
}