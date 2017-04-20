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

/**
 * Insert the type's description here.
 * Creation date: (4/18/2001 11:10:00 AM)
 * @author: Jim Schaff
 */
public class Token {
	private String value = null;
	private int startColIndex = -1;
	private int lineIndex = -1;
	/**
	 * comment before token
	 */
	private String beforeComment;
	/**
	 * comment after token
	 */
	private String afterComment;
/**
 * Token constructor comment.
 */
public Token(String argTokenString, int argStartColIndex, int argLineIndex) {
	if (argTokenString==null || argTokenString.length()==0){
		throw new IllegalArgumentException("tokenString is null or zero length");
	}
	this.value = argTokenString;
	this.startColIndex = argStartColIndex;
	this.lineIndex = argLineIndex;
	beforeComment = null;
	afterComment = null;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 2:18:22 PM)
 * @return int
 */
public int getEndColumnIndex() {
	return startColIndex+value.length()-1;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 2:18:22 PM)
 * @return int
 */
public int getLineIndex() {
	return lineIndex;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 2:18:22 PM)
 * @return int
 */
public int getStartColumnIndex() {
	return startColIndex;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 11:38:59 AM)
 * @return java.lang.String
 */
public String getValue() {
	return value;
}
public String getBeforeComment() {
	return beforeComment;
}
public void setBeforeComment(String beforeComment) {
	this.beforeComment = beforeComment;
}
public String getAfterComment() {
	return afterComment;
}
public void setAfterComment(String afterComment) {
	this.afterComment = afterComment;
}

/**
 * indicate presence of comments for a given Token
 */
public enum CommentState {
	NONE,
	BEFORE,
	AFTER,
	BOTH,
}

public CommentState getCommentInfo( ) {
	final boolean haveBefore = beforeComment != null; 
	final boolean haveAfter = afterComment != null; 
	if (!haveBefore && !haveAfter) {
		return CommentState.NONE;
	}
	if (haveBefore && !haveAfter) {
		return CommentState.BEFORE;
	}
	if (!haveBefore && haveAfter) {
		return CommentState.AFTER;
	}
	if (haveBefore && haveAfter) {
		return CommentState.BOTH;
	}
	throw new  RuntimeException("invalid programming logic");
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 11:32:16 AM)
 * @param token cbit.vcell.math.Token
 */
public void join(Token token) {
	if (this.lineIndex != token.lineIndex){
		throw new IllegalArgumentException("can't join tokens from different lines, "+toString()+", "+token.toString());
	}
	if (this.getEndColumnIndex() != token.startColIndex-1){
		throw new IllegalArgumentException("can't join tokens that aren't contiguous, "+toString()+", "+token.toString());
	}
	this.value += token.value;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 11:15:07 AM)
 * @return java.lang.String
 */
public String toString() {
	return "Token('"+value+"', line="+lineIndex+", col="+startColIndex+")";
}
}
