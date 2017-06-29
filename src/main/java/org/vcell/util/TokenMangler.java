/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;


/**
 * This type was created in VisualAge.
 */
public class TokenMangler {
	/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String fixToken(String name) {
	if (name==null){ 
		return "";
	}
	String string = name.trim();
	if (string.equals(".")){
		return "";
	}
	StringBuilder newString = new StringBuilder(string);
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
	if (name == null) {
		return ""; 
	}
	String string = name.trim();
	if (string.length() == 0){ 
		return string;
	}
	StringBuilder newString = new StringBuilder(string);
	char charAt0 = newString.charAt(0);
	if (!Character.isLetter(charAt0) && charAt0 != '_'){
		newString.insert(0,'_');
	}
	for (int i=1;i<newString.length();i++){
		char charAtI = newString.charAt(i);
		if (!Character.isLetterOrDigit(charAtI) && charAtI != '_'){
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
	StringBuilder buffer = new StringBuilder();

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

	String[] escapeSeq = {"&#010;"};
	char[] escapedChar = {'\n'};
	
	return getRestoredString(inputString, escapeSeq, escapedChar);
}


/**
 * @return java.lang.String
 */
public static String getEscapedString(String inputString) {
	if (inputString == null){
		throw new IllegalArgumentException("input string is null");
	}
	StringBuilder buffer = new StringBuilder(inputString.length()*2);

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
 * replace instances of \ with \\
 * @param inputString java.lang.String
 */
public static String getEscapedString_C(String inputString) {
	// escape '\'
	StringBuilder sb = new StringBuilder();
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
//	System.out.println("original = '"+originalToken+"', baseName = '"+baseName+"', digits = '"+digits+"', count = "+count);
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
	//System.out.println("original = '"+originalToken+"', baseName = '"+baseName+"', newName="+newToken);
	return newToken;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getRestoredString(String inputString) {

	String[] escapeSeq =    {"&lt;","&gt;","&amp;","&apos;","&quot;","&#09;","&#10;","&#13;"};
	char[]   escapedChar =  {'<'   ,'>'   ,'&'    ,'\''    ,'"'     ,'\t'   ,'\n'   ,'\r'};
	
	return getRestoredString(inputString, escapeSeq, escapedChar);
}


/**
 * 
 * @param inputString; may not be null
 * @param escapeSeq sequences to look for; may not be null
 * @param escapedChar replacements for sequences; may not be null
 * @return string with substitutions made 
 */
public static String getRestoredString(String inputString, String[] escapeSeq, char[] escapedChar) {

	if (inputString == null){
		throw new IllegalArgumentException("TokenMangler.getRestoredString(), input string is null");
	}

	boolean bChanged = true;
	while (bChanged){
		bChanged = false;
		for (int i = 0; i < escapeSeq.length; i ++){
			int replaceIndex = inputString.indexOf(escapeSeq[i]);
			if (replaceIndex != -1){
				if (replaceIndex == 0){
					inputString = escapedChar[i] + inputString.substring(escapeSeq[i].length(),inputString.length());
				}else if (replaceIndex == inputString.length() - escapeSeq[i].length()){
					inputString = inputString.substring(0,replaceIndex) + escapedChar[i];
				}else{
					inputString = inputString.substring(0,replaceIndex) + escapedChar[i] +
									inputString.substring(replaceIndex + escapeSeq[i].length(),inputString.length());
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
public static String getSQLEscapedString(String inputString) {
	return getSQLEscapedString(inputString,0);
}


/**
 * @param maxLength maximum return length
 * replace single quotes with &apos;
 * @return String truncated if necessary 
 */
public static String getSQLEscapedString(String inputString, int maxLength) {
	StringBuilder buffer = new StringBuilder(inputString.length()*2);

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
 * replace single quotes with &apos;
 */
public static String getSQLRestoredString(String inputString) {

	String escapeSeq[] = {"&apos;"};
	char escapedChar[] = {'\''};
	
	return getRestoredString(inputString, escapeSeq, escapedChar);
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
	StringBuilder newString = new StringBuilder(string);
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
	
	StringBuilder buffer = new StringBuilder(origString);
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

/**
 * 
 * @param source
 * @param owner
 * @param evt may not be null
 * @throws PropertyVetoException
 */
public static void checkNameProperty(Object source, String owner, PropertyChangeEvent evt) throws PropertyVetoException {
	if (evt.getSource() == source && evt.getPropertyName().equals("name") && evt.getNewValue()!=null){
		if (evt.getNewValue() == null || ((String)evt.getNewValue()).trim().length()==0){
			throw new PropertyVetoException("A name must be given to save " + owner + "s", evt);
		} 
//		else if (((String)evt.getNewValue()).contains("'")){
//			throw new PropertyVetoException("Apostrophe is not allowed in " + owner + " names",evt);
//		}
	}
}

/**
 * add double quotes to ends of string if it contains a space
 * @param pathname  (may not be null)
 * @return original string or new double quote terminated one
 */
public static String getEscapedPathName(String pathname) {
	String escapedPathName = pathname;
	if (pathname.indexOf(' ') > 0) {
		escapedPathName = "\"" + pathname + "\"";
	}
	return escapedPathName;
}


/**
 * This method is only for VCID used in VCMetaData. The vcid for entities is represented as EntityType(Entity name); for example:
 * 		Biomodel(newBiomodel). Sometimes, the entity names can have parantheses in their names, which can be a problem while 
 * 		parsing VCId. So replace all occurrences of  '(' with '&lpar;' & ')' with '&rpar;' in the entity name before creating VCId.
 *
 *
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String mangleVCId(String name) {
	//Check is not null or empty
	if (name==null){ 
		throw new IllegalArgumentException("TokenMangler.mangleVCId(), input string is null");
	}
	StringBuilder buffer = new StringBuilder();

	for (int i = 0; i < name.length(); i ++){
		char currChar = name.charAt(i);
		switch (currChar){
			case '(':
				buffer.append("&lpar;");
				break;
			case ')':
				buffer.append("&rpar;");
				break;
			case '\'':
				buffer.append("&apos;");
				break;
			case '"':
				buffer.append("&quot;");
				break;
			default:
				buffer.append(currChar);
				break;
		}
	}

	return buffer.toString();
}

public static String unmangleVCId(String name) {
	String[] escapeSeq =    {"&lpar;","&rpar;", "&apos;", "&quot;"};
	char[]   escapedChar =  {'(', ')', '\'', '"'};

	
	return getRestoredString(name, escapeSeq, escapedChar);
}

}
