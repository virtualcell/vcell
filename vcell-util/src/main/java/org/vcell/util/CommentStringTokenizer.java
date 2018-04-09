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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Parse a block of text, including comments  <br>
 * "After" comments begin with // and go to end of line. They are assigned to first Token on line. <br>
 * "Before" comments begin with slash-asterisk and go to next asterisk-slash. The are assigned to first token
 * on next line read. Before comments in the middle of line may not parse correctly; they will be silently discarded.
 */
public class CommentStringTokenizer {
	//private StringTokenizer lineTokenizer = null;
	BufferedReader reader = null;
	private final static String TAB = "\t";
	private final static String SLASH = "/";
	private final static String QUOTE = "\"";
	private final static String SPACE = " ";
	private final static String DELIMITERS= SPACE + TAB + QUOTE; 
	private final static Logger lg = LogManager.getLogger(CommentStringTokenizer.class);
	
	private final static char SPACE_CHAR = ' ';
	
	private LinkedList<Token> tokenLinkedList = new LinkedList<Token>();
	private String beforeComment = null;
	int currLine = 0;
	int currCol = 0;

/**
 * support reading from multiple sources (e.g. file) 
 * @param str BufferedReader 
 */
public CommentStringTokenizer(BufferedReader str) {
	reader = str;
	//lineTokenizer = new StringTokenizer(str, " \t\n\r"+QuoteCharacter, true);
	refreshCurrTokenizer();
}

/**
 * read from String
 * @param str
 */
public CommentStringTokenizer(String str) {
	this(new BufferedReader(new StringReader(str)));
}

/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int columnIndex() {
	return currCol;
}

/**
 * This method was created by a SmartGuide.
 * @return boolean
 */
public boolean hasMoreTokens() {
	refreshCurrTokenizer();
	return tokenLinkedList.size() > 0;
}

public String readToSemicolon(){
	String expressionString = new String();
	String token = null;
	while (hasMoreTokens()){
		token = nextToken();
		if (token.equals(";")){
			break;
		}	
		if (token.charAt(token.length()-1) == ';'){
			expressionString += token.substring(0,token.length()-1);
			break;
		}	
		expressionString += token;
	}
	return expressionString;
}

/**
 * This method was created by a SmartGuide.
 * @return int
 */
public int lineIndex() {
	return currLine;
}


/**
 * return next token as String
 * @return next token
 * @throws NoSuchElementException if {@link #hasMoreTokens()} is false
 */
public String nextToken() {
	return next( ).getValue();
}

/**
 * return next token as full object
 * @return next token
 * @throws NoSuchElementException if {@link #hasMoreTokens()} is false
 */
public Token next( ) {
	refreshCurrTokenizer();
	if (tokenLinkedList.size() == 0) {
		throw new NoSuchElementException();
	}
	Token token = tokenLinkedList.removeFirst();	
	if (lg.isInfoEnabled()) {
		//show all if logging "debug". Show only tokens with comments if logging "info"
		Token.CommentState cs = token.getCommentInfo();
		String msg = token.toString() + " comments " + cs; 
		switch (cs) {
		case NONE:
			lg.debug(msg);
			break;
		case BEFORE:
			msg += ": " + token.getBeforeComment();
			lg.info(msg);
			break;
		case AFTER:
			msg += ": " + token.getAfterComment();
			lg.info(msg);
			break;
		case BOTH:
			msg += " /*" + token.getBeforeComment() + "*/ //" + token.getAfterComment();
			lg.info(msg);
			break;
		}
	}
	return token;
}


/**
 * Insert the method's description here.
 * Creation date: (8/6/2002 1:13:53 PM)
 * @param token java.lang.String
 */
public void pushToken(String token) {
	Token newToken = new Token(token,columnIndex(),lineIndex());
	tokenLinkedList.addFirst(newToken);
}

/**
 * remove leading space from string if present
 * @param in -- may not be null
 * @return in or in minus leading space
 */
private String removeLeadingSpace(String in) {
	if (in.length( ) > 0 && in.charAt(0) == SPACE_CHAR) {
		return in.substring(1);
	}
	return in;
	
}
/**
 * consume input from {@link #reader} until {@link Commented#END_BEFORE_COMMENT} found. Store
 * comment in {@link #beforeComment}
 * @param nextLine line currently being parsed 
 * @return new StringTokenizer for remaining part of file
 */
private StringTokenizer parseAndConsumeBeforeComment(String nextLine) {
	String restOfLine = nextLine.substring(currCol + Commented.BEFORE_COMMENT_LENGTH);
	int endMark  = restOfLine.indexOf(Commented.END_BEFORE_COMMENT);
	if (endMark != Commented.CHAR_NOT_FOUND) {
		beforeComment = removeLeadingSpace(restOfLine.substring(0,endMark));
		String remainingLine =restOfLine.substring(endMark + Commented.END_BEFORE_COMMENT_LENGTH);
		currCol += Commented.BEFORE_COMMENT_LENGTH + beforeComment.length() + Commented.END_BEFORE_COMMENT_LENGTH;
		return new StringTokenizer(remainingLine, DELIMITERS, true);		
	}
	assert restOfLine.indexOf(Commented.END_BEFORE_COMMENT) == Commented.CHAR_NOT_FOUND;
	//String cmt = restOfLine;
	StringBuilder sb = new StringBuilder(restOfLine);
	char NEWLINE = '\n';
	for (;;) {
		sb.append(NEWLINE);
		currLine++;
		String line; 
		try {
			line  = reader.readLine();
		} catch (IOException e) {			
			e.printStackTrace();
			throw new NoSuchElementException(e.getMessage());
		}
		int mark = line.indexOf(Commented.END_BEFORE_COMMENT);
		if (mark == Commented.CHAR_NOT_FOUND) {
			sb.append(line);
			continue;
		}
		String endOfComment = line.substring(0,mark);
		sb.append(endOfComment);
		//remove single leading and/or trailing spaces
		int begin = 0;
		if (sb.charAt(0) == SPACE_CHAR) {
			begin = 1;
		}
		int last = sb.length() - 1;
		if (sb.charAt(last) == SPACE_CHAR) {
			--last;
		}
		beforeComment = sb.substring(begin,last);
		String remainingLine = line.substring(mark + Commented.END_BEFORE_COMMENT_LENGTH);
		currCol = beforeComment.length() + Commented.END_BEFORE_COMMENT_LENGTH;
		return new StringTokenizer(remainingLine, DELIMITERS, true);		
	}
}


/**
 * This method was created by a SmartGuide.
 */
private void refreshCurrTokenizer() {
	if (tokenLinkedList.size() != 0) {
		return;
	}
	
	//
	// if tokenLinkedList is empty, fill with tokens from next non-empty line of text
	//
	while (tokenLinkedList.size() == 0) { // read until there is something in the list
		String nextLine = null;
		try {
			nextLine = reader.readLine();
			lg.debug(nextLine);
		} catch (IOException e) {			
			e.printStackTrace();
			throw new NoSuchElementException(e.getMessage());
		}
		if (nextLine == null) {
			return;
		}		
		StringTokenizer lineTokenizer = new StringTokenizer(nextLine, DELIMITERS, true);		
		currLine ++;
		currCol = 0;		
		boolean bInsideQuotes = false;
		Token quotedToken = null;
		Token firstToken = null;
		while (lineTokenizer.hasMoreElements()){
			String nextToken = lineTokenizer.nextToken();
			//
			// comments use the C++/Java convention for line comments "//"
			//
			// flag comments until end of line (reset only by \n)
			// comment token is ignored within quotes
			//
			// if not inside quotes, forget about comment, space and tab
			if (!bInsideQuotes) {
				if (nextToken.equals(SPACE) || nextToken.equals(TAB)){
					currCol += nextToken.length();
					continue;
				}
				if (nextToken.startsWith(SLASH)) {
					if (nextToken.startsWith(Commented.AFTER_COMMENT)) {
						if (firstToken != null) {
							String comment = nextLine.substring(currCol + Commented.AFTER_COMMENT_LENGTH);
							firstToken.setAfterComment(comment);
						}
						break;
					}
					if (nextToken.startsWith(Commented.BEFORE_COMMENT)) {
						lineTokenizer = parseAndConsumeBeforeComment(nextLine);
						continue;
					}
				}
			}
			
			if (nextToken.equals(QUOTE)){
				if (bInsideQuotes){
					//
					// if ending a quoted string, add current quotedToken to the linkedList
					// quotedToken is the accumulator for tokens within the current quoted string.
					//					
					if (quotedToken!=null){
						tokenLinkedList.addLast(quotedToken);
						quotedToken = null;
					}
					bInsideQuotes = false;
				}else{
					bInsideQuotes = true;
				}
				currCol ++;
				
				continue;
			}
			
			Token newToken = new Token(nextToken,currCol,currLine);
			if (firstToken == null) {
				firstToken = newToken;
				firstToken.setBeforeComment(beforeComment);
				beforeComment = null;
			}
			currCol += nextToken.length();
			
			if (bInsideQuotes){
				//
				// start a new quotedToken or append to current one, add to linked list after receiving end-quote
				//
				if (quotedToken==null){
					quotedToken = newToken;
				}else{
					quotedToken.join(newToken);
				}
			} else {
				tokenLinkedList.addLast(newToken);				
			}	
		}	
		if (bInsideQuotes){
			throw new RuntimeException("unterminated string, expected a \" before end of line");
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 2:30:23 PM)
 */
public void show() {
	for (Token token :  tokenLinkedList){
		System.out.println(token);
	}
}
}
