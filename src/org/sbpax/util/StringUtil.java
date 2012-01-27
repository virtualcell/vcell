/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.util;

/*   StringUil  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2009
 *   Utilities for String manipulation
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

public class StringUtil {
	
	public static boolean notEmpty(String s) { return s != null && s.trim().length() > 0; }
	public static boolean isEmpty(String s) { return s == null || s.trim().length() == 0; }
	
	public static String trim(String string, int maxLength) {
		if (string==null){
			return "";
		}
		if(string.length() > maxLength) { return string.substring(0, maxLength-3) + "..."; } 
		else { return string; }
	}
	
	public static String trimJavaIdentifier(String longName) {
		int length = longName.length();
		for(int ind = 0; ind < length; ++ind) {
			int pos = length - ind - 1;
			if(!Character.isJavaIdentifierPart(longName.charAt(pos))) {
				if(pos == length) {
					return "";
				} else {
					return longName.substring(pos + 1);					
				}
			}
		}
		return longName;
	}
	
	public static String multiply(String text, int factor) {
		StringBuilder buffer = new StringBuilder();
		for(int ind = 0; ind < factor; ++ind) {
			buffer.append(text);
		}
		return buffer.toString();
	}
	
	public static String textFromFile(File file) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		StringBuffer stringBuffer = new StringBuffer();
		String line = new String();
		while((line = bufferedReader.readLine()) != null) { stringBuffer.append(line + "\n"); }
		return stringBuffer.toString();
	}

	public static String textFromInputStream(InputStream is) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		StringBuffer stringBuffer = new StringBuffer();
		String line = new String();
		while((line = bufferedReader.readLine()) != null) { stringBuffer.append(line + "\n"); }
		return stringBuffer.toString();
	}
	
	public static String textFromInputStream(InputStream is, String encoding) throws IOException {
		Writer writer = new StringWriter(); 
		char[] buffer = new char[1024]; 
		try { 
			Reader reader = new BufferedReader(new InputStreamReader(is, encoding)); 
			int n; 
			while ((n = reader.read(buffer)) != -1) { 
				writer.write(buffer, 0, n); 
			} 
		} finally { 
			is.close(); 
		} 
		return writer.toString(); 

	}

	public static String concat(Collection<?> tokens, String separator) {
		String chain = "";
		for(Object token : tokens) {
			if(chain.equals("")) { chain = token.toString(); }
			else { chain = chain + separator + token; }
		}
		return chain;
	}
	
	public static String concat(Collection<?> tokens, String separator, String prefix, String suffix) {
		String chain = "";
		for(Object token : tokens) {
			if(chain.equals("")) { chain = prefix + token + suffix; }
			else { chain = chain + separator + prefix + token + suffix; }
		}
		return chain;
	}

	public static final String [] consonants = 
	{"B", "C", "D", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "V", "W", "X", "Y", "Z"},
	vowels = {"A", "E", "I", "O", "U"};
	public static final int nConsonants = consonants.length, nVowels = vowels.length;
	
	public static String createMnemonicRandomString(long n) {
		String mnemoString = "";
		while(n > 0) {
			mnemoString = consonants[(int) (n % nConsonants)] + mnemoString;
			n /= nConsonants;
			mnemoString = vowels[(int) (n % nVowels)] + mnemoString;
			n /= nVowels;
			mnemoString = consonants[(int) (n % nConsonants)] + mnemoString;
			n /= nConsonants;
		}
		return mnemoString;
	}
	
}
