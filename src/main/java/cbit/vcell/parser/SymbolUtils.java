package cbit.vcell.parser;

import org.vcell.util.TokenMangler;

public class SymbolUtils {

	//private static final char badChars[] = { ' ', '-', '+', '(', ')', '/', '*', '.', '&', ';', ':', ',', '=', '<', '>', '\n', '\t', '\012', '\016' };
	public static final String MATLAB_PREFIX = "mlabfix";
	private static final String ECLiPSe_PREFIX = "VAR_";

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public static String getEscapedTokenECLiPSe(String inputString) {
		if (inputString == null){
			throw new IllegalArgumentException("input string is null");
		}
		StringBuilder buffer = new StringBuilder(inputString.length()*2);
	
		for (int i=0;i<inputString.length();i++){
			char currChar = inputString.charAt(i);
			switch (currChar){
				case '.':
						buffer.append("ddoott");
						break;
				default:
						buffer.append(currChar);
						break;
			}
		}
		return ECLiPSe_PREFIX + buffer.toString();
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public static String getEscapedTokenJSCL(String inputString) {
		if (inputString == null){
			throw new IllegalArgumentException("input string is null");
		}
		StringBuilder buffer = new StringBuilder(inputString.length()*2);
	
		for (int i=0;i<inputString.length();i++){
			char currChar = inputString.charAt(i);
			switch (currChar){
				case '_':
						buffer.append("underscore");
						break;
				case '.':
						buffer.append("ddoott");
						break;
				default:
						buffer.append(currChar);
						break;
			}
		}
		return buffer.toString();
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public static String getEscapedTokenMatlab(String name) {
		//
		// Matlab cannot handle leading underscores
		//
		if (name.startsWith("_")){
			return MATLAB_PREFIX+name;
		}else{
			return name;
		}
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public static String getRestoredStringJSCL(String inputString) {
	
		String[] escapeSeq =    {"underscore","ddoott"};
		char[]   escapedChar =  {     '_'    ,  '.'   };
		
		return TokenMangler.getRestoredString(inputString, escapeSeq, escapedChar);
	}

	/**
	 * strip {@link MATLAB_PREFIX}	if present
	 * @param inputString
	 * @return inputString or string with prefix removed
	 */
	public static String getRestoredTokenMatlab(String inputString) {
	
		if (inputString.startsWith(MATLAB_PREFIX)){
			return inputString.substring(MATLAB_PREFIX.length());
		}else{
			return inputString;
		}
	}

}
