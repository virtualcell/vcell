package org.vcell.sybil.util.text;

/*   StringUil  --- by Oliver Ruebenacker, UCHC --- April 2007 to March 2009
 *   Utilities for String manipulation
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;

import org.vcell.sybil.util.exception.CatchUtil;

public class StringUtil {
	
	public static boolean notEmpty(String s) { return s != null && s.trim().length() > 0; }
	
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

	public static Document documentFromText(String text) {
		Document document = new PlainDocument();
		try { document.insertString(0, text, SimpleAttributeSet.EMPTY); } 
		catch (BadLocationException e) { CatchUtil.handle(e); }
		return document;
	}
	
	public static Document documentSetText(Document document, String text) {
		try {
			document.remove(0, document.getLength());
			document.insertString(0, text, SimpleAttributeSet.EMPTY);
		} catch (BadLocationException e) {
			CatchUtil.handle(e);
		}
		return document;
	}
	
	public static Document documentResetText(Document document) {
		try {
			document.remove(0, document.getLength());
		} catch (BadLocationException e) {
			CatchUtil.handle(e);
		}
		return document;
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
	
}
