package cbit.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

import cbit.vcell.field.FieldFunctionArguments;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class TokenMangler {
	//private static final char badChars[] = { ' ', '-', '+', '(', ')', '/', '*', '.', '&', ';', ':', ',', '=', '<', '>', '\n', '\t', '\012', '\016' };
	private static final String MATLAB_PREFIX = "mlabfix";
	private static final String ECLiPSe_PREFIX = "VAR_";
	private static final String C_FIELDVAR_PREFIX = "vcell_fieldvar_";
	private static final String C_LOCALVAR_PREFIX = "vcell_localvar_";
	private static final String C_LOCALFIELD_PREFIX = "vcell_localfield_";
	private static final String C_GLOBALFIELD_PREFIX = "vcell_globalfield_";

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String fixToken(String name) {
	String string = name.trim();
	if (string==null){ 
		return "";
	}
	if (string.equals(".")){
		return "";
	}
	StringBuffer newString = new StringBuffer(string);
	if (Character.isJavaIdentifierStart(newString.charAt(0))==false){
		newString.insert(0,'_');
	}
	for (int i=1;i<newString.length();i++){
		if (Character.isJavaIdentifierPart(newString.charAt(i))==false){
			newString.setCharAt(i,'_');
		}
	}
	return newString.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String fixTokenStrict(String name) {
	return fixTokenStrict(name,0);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String fixTokenStrict(String name, int maxLength) {
	String string = name.trim();
	if (string==null){ 
		return "";
	}
	StringBuffer newString = new StringBuffer(string);
	if (!Character.isLetter(newString.charAt(0)) && newString.charAt(0)!='_'){
		newString.insert(0,'_');
	}
	for (int i=1;i<newString.length();i++){
		if (!Character.isLetterOrDigit(newString.charAt(i)) && newString.charAt(i)!='_'){
			newString.setCharAt(i,'_');
		}
	}
	String token = newString.toString();
	if (maxLength>0 && token.length()>maxLength){
		token = token.substring(0,maxLength);
	}
	return token;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getChildSummaryElementEscapedString(String inputString) {
	//
	//Guarantees there will be no unescaped '\n' characters in return
	//
	if(inputString == null || inputString.length() == 0){
		return "";
	}
	StringBuffer buffer = new StringBuffer();

	for (int i=0;i<inputString.length();i++){
		char currChar = inputString.charAt(i);
		switch (currChar){
			case '\n':
					buffer.append("&#010;");
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
public static String getChildSummaryElementRestoredString(String inputString) {

	String escapeSeq = "&#010;";

	while (true){
		int replaceIndex = inputString.indexOf(escapeSeq);
		if (replaceIndex==-1){
			break;
		}
		if (replaceIndex==0){
			inputString = "\n"+inputString.substring(escapeSeq.length(),inputString.length());
		}else if (replaceIndex==inputString.length() - escapeSeq.length()){
			inputString = inputString.substring(0,replaceIndex)+"\n";
		}else{
			inputString = inputString.substring(0,replaceIndex) + "\n" +
							inputString.substring(replaceIndex+escapeSeq.length(),inputString.length());
		}
	}
	return inputString;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getEscapedString(String inputString) {
	if (inputString == null){
		throw new IllegalArgumentException("input string is null");
	}
	StringBuffer buffer = new StringBuffer(inputString.length()*2);

	for (int i=0;i<inputString.length();i++){
		char currChar = inputString.charAt(i);
		switch (currChar){
			case '<':
					buffer.append("&lt;");
					break;
			case '>':
					buffer.append("&gt;");
					break;
			case '&':
					buffer.append("&amp;");
					break;
			case '\'':
					buffer.append("&apos;");
					break;
			case '"':
					buffer.append("&quot;");
					break;
			case '\t':
					buffer.append("&#09;");
					break;
			case '\n':
					buffer.append("&#10;");
					break;
			case '\r':
					buffer.append("&#13;");
					break;
			default:
					buffer.append(currChar);
					break;
		}
	}
	return buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (9/20/2006 9:06:41 AM)
 * @return java.lang.String
 * @param inputString java.lang.String
 */
public static String getEscapedString_C(String inputString) {
	// escape '\'
	StringBuffer sb = new StringBuffer();
	for (int i = 0;i < inputString.length(); i ++){
		if (inputString.charAt(i) == '\\'){
			sb.append(inputString.charAt(i));
			sb.append(inputString.charAt(i));
		} else{
			sb.append(inputString.charAt(i));
		}
	}

	return sb.toString();
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getEscapedFieldVariableName_C(String var) {
	if (var == null){
		throw new IllegalArgumentException("input string is null");
	}
	return C_FIELDVAR_PREFIX + var;
}

public static String getEscapedLocalVariableName_C(String var) {
	if (var == null){
		throw new IllegalArgumentException("input string is null");
	}
	return C_LOCALVAR_PREFIX + var;
}

public static String getEscapedLocalFieldVariableName_C(FieldFunctionArguments fieldFuncArgs) {
	if (fieldFuncArgs == null) {
		throw new IllegalArgumentException("input fieldFuncArgs is null");
	}
	return C_LOCALFIELD_PREFIX + fieldFuncArgs.getUniqueID();
}

public static String getEscapedLocalFieldVariableName_C(String fieldname, String varname, String timeExp, String varType) {	
	if (fieldname == null || varname == null || timeExp == null) {
		throw new IllegalArgumentException("TokenMangler::getEscapedLocalFieldVariableName_C() : input args can't be null");
	}
	try {
		return C_LOCALFIELD_PREFIX + FieldFunctionArguments.getUniqueID(fieldname, varname, new Expression(timeExp), varType);
	} catch (ExpressionException e) {		
		e.printStackTrace(System.out);
		throw new IllegalArgumentException("TokenMangler::getEscapedLocalFieldVariableName_C() : time expression is not correctly formatted");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2006 2:51:03 PM)
 * @return java.lang.String
 */
public static String getEscapedGlobalFieldVariableName_C(FieldFunctionArguments fieldFuncArgs){
	return C_GLOBALFIELD_PREFIX + fieldFuncArgs.getUniqueID();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getEscapedTokenECLiPSe(String inputString) {
	if (inputString == null){
		throw new IllegalArgumentException("input string is null");
	}
	StringBuffer buffer = new StringBuffer(inputString.length()*2);

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
	StringBuffer buffer = new StringBuffer(inputString.length()*2);

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
public static String getNextEnumeratedToken(String originalToken) {
	
	if (originalToken == null){
		return null;
	}
	//
	// form baseName deleting trailing numerals
	//
	String baseName = originalToken;
	while (true) {
		int length = baseName.length();
		if (length > 0 && Character.isDigit(baseName.charAt(length - 1))) {
			baseName = baseName.substring(0,length-1);
		}else{
			break;
		}
	}
	//
	// get string of remaining characters (digits)
	//
	String digits = "0";  // default
	if (baseName.length()<originalToken.length()){
		digits = originalToken.substring(baseName.length(),originalToken.length());
	}
	int count = Integer.parseInt(digits);
	System.out.println("original = '"+originalToken+"', baseName = '"+baseName+"', digits = '"+digits+"', count = "+count);
	return baseName + (count+1);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getNextRandomToken(String originalToken) {
	
	if (originalToken == null){
		return null;
	}
	//
	// form baseName deleting trailing numerals
	//
	String baseName = originalToken;
	while (true) {
		int length = baseName.length();
		if (length > 0 && Character.isDigit(baseName.charAt(length - 1))) {
			baseName = baseName.substring(0,length-1);
		}else{
			break;
		}
	}
	//
	// create new random number to append
	//
	int randomInt = (int)(Math.abs(Math.random()*Integer.MAX_VALUE));
	String newToken = baseName+randomInt;
	System.out.println("original = '"+originalToken+"', baseName = '"+baseName+"', newName="+newToken);
	return newToken;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getRestoredString(String inputString) {

	if (inputString == null){
		throw new IllegalArgumentException("input string is null");
	}

	String[] escapeSeq =    {"&lt;","&gt;","&amp;","&apos;","&quot;","&#09;","&#10;","&#13;"};
	char[]   escapedChar =  {'<'   ,'>'   ,'&'    ,'\''    ,'"'     ,'\t'   ,'\n'   ,'\r'};

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
public static String getRestoredStringECLiPSe(String inputString) {

	if (inputString == null){
		throw new IllegalArgumentException("input string is null");
	}

	String[] escapeSeq =    {"ddoott"};
	char[]   escapedChar =  {'.'   };

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
	if (inputString.startsWith(ECLiPSe_PREFIX)){
		return inputString.substring(ECLiPSe_PREFIX.length());
	}else{
		return inputString;
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
public static String getRestoredTokenMatlab(String inputString) {

	if (inputString.startsWith(MATLAB_PREFIX)){
		return inputString.substring(MATLAB_PREFIX.length());
	}else{
		return inputString;
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getSQLEscapedString(String inputString) {
	return getSQLEscapedString(inputString,0);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getSQLEscapedString(String inputString, int maxLength) {
	StringBuffer buffer = new StringBuffer(inputString.length()*2);

	for (int i=0;i<inputString.length();i++){
		char currChar = inputString.charAt(i);
		switch (currChar){
			case '\'':
					buffer.append("&apos;");
					break;
			default:
					buffer.append(currChar);
					break;
		}
	}
	if (maxLength<=0){
		return buffer.toString();
	}else{
		return buffer.substring(0,Math.min(maxLength,buffer.length()));
	}
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getSQLRestoredString(String inputString) {

	String escapeSeq = "&apos;";

	while (true){
		int replaceIndex = inputString.indexOf(escapeSeq);
		if (replaceIndex==-1){
			break;
		}
		if (replaceIndex==0){
			inputString = "'"+inputString.substring(escapeSeq.length(),inputString.length());
		}else if (replaceIndex==inputString.length() - escapeSeq.length()){
			inputString = inputString.substring(0,replaceIndex)+"'";
		}else{
			inputString = inputString.substring(0,replaceIndex) + "'" +
							inputString.substring(replaceIndex+escapeSeq.length(),inputString.length());
		}
	}
	return inputString;
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
 * Insert the method's description here.
 * Creation date: (9/7/2004 2:52:38 PM)
 * @return java.lang.String
 * @param origString java.lang.String
 * @param targetSubstring java.lang.String
 * @param newSubstring java.lang.String
 */
public static String replaceSubString(String origString, String targetSubstring, String newSubstring) {
	if (origString == null || targetSubstring == null || newSubstring == null){
		throw new IllegalArgumentException("input string is null");
	}
	if ( targetSubstring.equals("") ) {
		throw new IllegalArgumentException("targetSubstring must have content.");
	}

	if (targetSubstring.equals(newSubstring)) {
		return origString;
	}

	if (newSubstring.indexOf(targetSubstring) >= 0) {
		throw new IllegalArgumentException("targetSubstring is a substring of newSubstring");
	}
	
	StringBuffer buffer = new StringBuffer(origString);
	boolean bDone = false;
	while (!bDone){
		bDone = true;
		int startIndex = buffer.toString().indexOf(targetSubstring);
		if (startIndex > -1){
			buffer.replace(startIndex,(startIndex+targetSubstring.length()),newSubstring);
			bDone = false;
		}
	}
	return buffer.toString();
}

public static void checkLoginID(String loginID) throws IllegalArgumentException {	
	if (loginID == null){
		throw new IllegalArgumentException("Login ID must not be empty");
	} 
	for (int i = 0; i < loginID.length(); i ++) {
		char ch = loginID.charAt(i);
		if (!Character.isDigit(ch) && !Character.isLetter(ch) && ch != '.' && ch != '-' && ch != '_') {
			throw new IllegalArgumentException("Login ID contains illegal character '" + ch + "' at position " + i + ". Login ID can contain only letters, numbers, periods (.), hyphens (-), and underscores (_). " 
					+ "Special characters or accented letters are not allowed. Please type a different Login ID.");
		}
	}	
}

public static void checkNameProperty(Object source, String owner, PropertyChangeEvent evt) throws PropertyVetoException {
	if (evt.getSource() == source && evt.getPropertyName().equals("name") && evt.getNewValue()!=null){
		if (evt.getNewValue() == null || ((String)evt.getNewValue()).trim().length()==0){
			throw new PropertyVetoException("A name must be given to save " + owner + "s", evt);
		} else if (((String)evt.getNewValue()).contains("'")){
			throw new PropertyVetoException("Apostrophe is not allowed in " + owner + " names",evt);
		}
	}
}
}