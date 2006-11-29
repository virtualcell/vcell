package org.vcell.expression;

import java.util.Random;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;


public class ExpressionUtilities {
	private static ISymbolicProcessor defaultSymbolicProcessor = null;

	/**
	 * Insert the method's description here.
	 * Creation date: (10/17/2002 12:42:18 AM)
	 * @return boolean
	 * @param exp1 cbit.vcell.parser.Expression
	 * @param exp2 cbit.vcell.parser.Expression
	 */
	public static boolean derivativeFunctionallyEquivalent(IExpression exp, String diffSymbol, IExpression diff, double relativeTolerance, double absoluteTolerance) {
		try {
			String symbolsExp[] = exp.getSymbols();
			String symbolsDiff[] = diff.getSymbols();
			boolean bHasDiffSymbol = false;
			for (int i = 0; symbolsExp!=null && i < symbolsExp.length; i++){
				if (symbolsExp[i].equals(diffSymbol)){
					bHasDiffSymbol = true;
				}
			}
			if (!bHasDiffSymbol){
				//
				// if expression doesn't reference 'diffSymbol', then derivative must be 0.
				//
				if (diff.isZero()){
					return true;
				}else{
					return false;
				}
			}
			
			String symbols[] = null;
			if (symbolsDiff==null){
				symbols = symbolsExp;
			}else{
				//
				// make combined list (without duplicates)
				//
				java.util.HashSet hashSet = new java.util.HashSet();
				for (int i = 0; i < symbolsExp.length; i++){
					hashSet.add(symbolsExp[i]);
				}
				for (int i = 0; i < symbolsDiff.length; i++){
					hashSet.add(symbolsDiff[i]);
				}
				symbols = (String[])hashSet.toArray(new String[hashSet.size()]);
			}
	
			org.vcell.expression.SymbolTable symbolTable = new SimpleSymbolTable(symbols);
			exp.bindExpression(symbolTable);
			diff.bindExpression(symbolTable);
			int diffSymbolIndex = exp.getSymbolBinding(diffSymbol).getIndex();
			double values[] = new double[symbols.length];
			//
			// go through 20 different sets of random inputs to test for equivalence
			// ignore cases of Exceptions during evaluations (out of domain of imbedded functions), keep trying until 20 successful evaluations
			//
			Random rand = new Random();
			final int MAX_TRIES = 1000;
			final int REQUIRED_NUM_EVALUATIONS = 20;
			int numEvaluations = 0;
			Exception savedException = null;
			for (int i = 0; i < MAX_TRIES && numEvaluations < REQUIRED_NUM_EVALUATIONS; i++){
				for (int j = 0; j < values.length; j++){
					values[j] = i*rand.nextGaussian();
				}
				try {
					//
					// evalutate "exact" differential
					//
					double resultDiff = diff.evaluateVector(values);
	
					//
					// central difference approximation from expression w.r.t "P" (diffSymbol)
					// choose "delta" about 100 times greater than error.
					//
					double nominalSymbolValue = values[diffSymbolIndex];
					double deltaP = Math.max(Math.abs(nominalSymbolValue*10*relativeTolerance),absoluteTolerance*10);
					double lowSymbolValue = nominalSymbolValue - deltaP;
					double highSymbolValue = nominalSymbolValue + deltaP;
					values[diffSymbolIndex] = lowSymbolValue;
					double result_low = exp.evaluateVector(values);
					values[diffSymbolIndex] = highSymbolValue;
					double result_high = exp.evaluateVector(values);
					if (Double.isInfinite(resultDiff) || Double.isNaN(resultDiff) || Math.abs(resultDiff)>1e10){
						throw new RuntimeException("diff = '"+diff+"' evaluates to "+resultDiff);
					}
					if (Double.isInfinite(result_low) || Double.isNaN(result_low)){
						throw new RuntimeException("low("+exp+") = "+result_low);
					}
					if (Double.isInfinite(result_high) || Double.isNaN(result_high)){
						throw new RuntimeException("high("+exp+") = "+result_high);
					}
					double resultCentralDifference = (result_high-result_low)/(2*deltaP);
					double scale = Math.abs(resultDiff)+Math.abs(resultCentralDifference);
					double absdiff = Math.abs(resultDiff-resultCentralDifference);
					if (scale > absoluteTolerance){ // if scale < absoluteTolerance, they are both close enough to zero.
						if (absdiff > relativeTolerance*10*scale){
							System.out.println("ExpressionUtils.derivativeFunctionallyEquivalent() 'exact' = "+resultDiff+", approx = "+resultCentralDifference+", absDiff = "+absdiff+", f_low = "+result_low+", f_high = "+result_high+", "+diffSymbol+" = "+nominalSymbolValue+" +/- "+deltaP);
							return false;
						}
					}
					numEvaluations++;
				}catch (Exception e){
					savedException = e;
				}
			}
			if (numEvaluations < REQUIRED_NUM_EVALUATIONS){
				savedException.printStackTrace(System.out);
				throw new RuntimeException("too many failed evaluations ("+numEvaluations+" of "+REQUIRED_NUM_EVALUATIONS+") ("+savedException.getMessage()+")");
			}
			return true;
		}catch (org.vcell.expression.ExpressionException e){
			e.printStackTrace(System.out);
			return false;
		}
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public static String getRestoredStringJSCL(String inputString) {

		if (inputString == null){
			throw new IllegalArgumentException("input string is null");
		}

		String[] escapeSeq =    {"underscore","ddoott"};
		char[]   escapedChar =  {     '_'    ,  '.'   };

		boolean bChanged = true;
		while (bChanged){
			bChanged = false;
			for (int i=0;i<escapeSeq.length;i++){
				int replaceIndex = inputString.indexOf(escapeSeq[i]);
				if (replaceIndex!=-1){
					if (replaceIndex==0){
						inputString = "'"+inputString.substring(escapeSeq[i].length(),inputString.length());
					}else if (replaceIndex==inputString.length() - escapeSeq[i].length()){
						inputString = inputString.substring(0,replaceIndex)+escapedChar[i];
					}else{
						inputString = inputString.substring(0,replaceIndex) + escapedChar[i] +
										inputString.substring(replaceIndex+escapeSeq[i].length(),inputString.length());
					}
					bChanged = true;
				}
			}
		}
		return inputString;
	}


	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public static String getEscapedTokenJSCL(String inputString) {
		if (inputString == null){
			throw new IllegalArgumentException("input string is null");
		}
		StringBuffer buffer = new StringBuffer(inputString.length()*2);

		for (int i=0;i<inputString.length();i++){
			char currChar = inputString.charAt(i);
			switch (currChar){
//				case '_':
//						buffer.append("underscore");
//						break;
//				case '.':
//						buffer.append("ddoott");
//						break;
				default:
						buffer.append(currChar);
						break;
			}
		}
		return buffer.toString();
	}



	public static ISymbolicProcessor getDefaultSymbolicProcessor(){
		if (defaultSymbolicProcessor == null){
	    	IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.vcell.expression","SymbolicProcessor");
	    	IExtension[] extensions = extensionPoint.getExtensions();
	    	for (int i = 0; i < extensions.length; i++) {
	    		IConfigurationElement[] extensionElements = extensions[i].getConfigurationElements();
				if (extensionElements!=null && extensionElements.length>0){
					try {
						Object object = extensionElements[0].createExecutableExtension("class");
						defaultSymbolicProcessor = (ISymbolicProcessor)object;
						break;
					} catch (CoreException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		return defaultSymbolicProcessor;
	}
	
	public static void setDefaultSymbolicProcessor(ISymbolicProcessor symbolicProcessor){
		defaultSymbolicProcessor = symbolicProcessor;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/17/2002 12:42:18 AM)
	 * @return boolean
	 * @param exp1 cbit.vcell.parser.Expression
	 * @param exp2 cbit.vcell.parser.Expression
	 */
	public static boolean functionallyEquivalent(IExpression exp1, IExpression exp2) {
		boolean verifySameSymbols = true;
		return functionallyEquivalent(exp1,exp2,verifySameSymbols);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/17/2002 12:42:18 AM)
	 * @return boolean
	 * @param exp1 cbit.vcell.parser.Expression
	 * @param exp2 cbit.vcell.parser.Expression
	 */
	public static boolean functionallyEquivalent(IExpression exp1, IExpression exp2, boolean bVerifySameSymbols) {
		double defaultAbsoluteTolerance = 1e-12;
		double defaultRelativeTolerance = 1e-10;
		return functionallyEquivalent(exp1,exp2,bVerifySameSymbols,defaultRelativeTolerance,defaultAbsoluteTolerance);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/17/2002 12:42:18 AM)
	 * @return boolean
	 * @param exp1 cbit.vcell.parser.Expression
	 * @param exp2 cbit.vcell.parser.Expression
	 */
	public static boolean functionallyEquivalent(IExpression exp1, IExpression exp2, boolean verifySameSymbols, double relativeTolerance, double absoluteTolerance) {
		try {
			String symbols1[] = exp1.getSymbols();
			String symbols2[] = exp2.getSymbols();
			if (symbols1==null && symbols2==null){
				//
				// compare solutions
				//
				try {
					double result1 = exp1.evaluateConstant();
					double result2 = exp2.evaluateConstant();
					if (Double.isInfinite(result1) || Double.isNaN(result1)){
						throw new RuntimeException("exp1 = '"+exp1+"' evaluates to "+result1);
					}
					if (Double.isInfinite(result2) || Double.isNaN(result2)){
						throw new RuntimeException("exp1 = '"+exp2+"' evaluates to "+result2);
					}
					double scale = Math.abs(result1)+Math.abs(result2);
					double absdiff = Math.abs(result1-result2);
					if (scale > absoluteTolerance){
						if (absdiff > relativeTolerance*scale){
							System.out.println("EXPRESSIONS DIFFERENT: no symbols, delta eval: "+(result2-result1));
							return false;
						}
					} else {
						return true;
					}
				}catch (Throwable e){
					throw new RuntimeException("unexpected exception ("+e.getMessage()+") while evaluating functional equivalence");
				}
			}
			String symbols[] = null;
			if (verifySameSymbols){
				if (symbols1==null || symbols2==null){
					System.out.println("EXPRESSIONS DIFFERENT: symbols null");
					return false;
				}
				if (symbols1.length!=symbols2.length){
					System.out.println("EXPRESSIONS DIFFERENT: symbols different number");
					return false;
				}
				//
				// make sure every symbol in symbol1 is in symbol2
				//
				for (int i = 0; i < symbols1.length; i++){
					boolean bFound = false;
					for (int j = 0; j < symbols2.length; j++){
						if (symbols1[i].equals(symbols2[j])){
							bFound = true;
							break;
						}
					}
					if (!bFound){
						System.out.println("EXPRESSIONS DIFFERENT: symbols don't match");
						return false;
					}
				}
				//
				// make sure every symbol in symbol2 is in symbol1
				//
				for (int i = 0; i < symbols2.length; i++){
					boolean bFound = false;
					for (int j = 0; j < symbols1.length; j++){
						if (symbols2[i].equals(symbols1[j])){
							bFound = true;
							break;
						}
					}
					if (!bFound){
						System.out.println("EXPRESSIONS DIFFERENT: symbols don't match");
						return false;
					}
				}
				symbols = symbols1;
			}else{ // don't verify symbols
				if (symbols1==null && symbols2!=null){
					symbols = symbols2;
				}else if (symbols1!=null && symbols2==null){
					symbols = symbols1;
				}else{
					//
					// make combined list (without duplicates)
					//
					java.util.HashSet hashSet = new java.util.HashSet();
					for (int i = 0; i < symbols1.length; i++){
						hashSet.add(symbols1[i]);
					}
					for (int i = 0; i < symbols2.length; i++){
						hashSet.add(symbols2[i]);
					}
					symbols = (String[])hashSet.toArray(new String[hashSet.size()]);
				}
			}
			org.vcell.expression.SymbolTable symbolTable = new SimpleSymbolTable(symbols);
			exp1.bindExpression(symbolTable);
			exp2.bindExpression(symbolTable);
			double values[] = new double[symbols.length];
			//
			// go through 20 different sets of random inputs to test for equivalence
			// ignore cases of Exceptions during evaluations (out of domain of imbedded functions), keep trying until 20 successful evaluations
			//
			Random rand = new Random();
			final int MAX_TRIES = 1000;
			final int REQUIRED_NUM_EVALUATIONS = 20;
			int numEvaluations = 0;
			Exception savedException = null;
			for (int i = 0; i < MAX_TRIES && numEvaluations < REQUIRED_NUM_EVALUATIONS; i++){
				for (int j = 0; j < values.length; j++){
					values[j] = 0.01*(i+1)*rand.nextGaussian();
				}
				try {
					double result1 = exp1.evaluateVector(values);
					double result2 = exp2.evaluateVector(values);
					if (Double.isInfinite(result1) || Double.isNaN(result1)){
						throw new RuntimeException("exp1 = '"+exp1+"' evaluates to "+result1);
					}
					if (Double.isInfinite(result2) || Double.isNaN(result2)){
						throw new RuntimeException("exp1 = '"+exp2+"' evaluates to "+result2);
					}
					double scale = Math.abs(result1)+Math.abs(result2);
					double absdiff = Math.abs(result1-result2);
					if (scale > absoluteTolerance){
						if (absdiff > relativeTolerance*scale){
							System.out.println("EXPRESSIONS DIFFERENT: numerical test "+numEvaluations+", tolerance exceeded by "+(int)Math.log(absdiff/(relativeTolerance*scale))+"digits");
							return false;
						}
					}
					numEvaluations++;
				}catch (Exception e){
					savedException = e;
				}
			}
			if (numEvaluations < REQUIRED_NUM_EVALUATIONS){
				//savedException.printStackTrace(System.out);
				throw new RuntimeException("too many failed evaluations ("+numEvaluations+" of "+REQUIRED_NUM_EVALUATIONS+") ("+savedException.getMessage()+") of expressions '"+exp1+"' and '"+exp2+"'");
			}
			return true;
		}catch (org.vcell.expression.ExpressionException e){
			e.printStackTrace(System.out);
			System.out.println("EXPRESSIONS DIFFERENT: "+e);
			return false;
		}
	}

}
