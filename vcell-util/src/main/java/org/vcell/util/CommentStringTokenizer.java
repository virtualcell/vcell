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
	private final static String TAB = "\t";
	private final static String SLASH = "/";
	private final static String QUOTE = "\"";
	private final static String SPACE = " ";
	private final static String DELIMITERS = SPACE + TAB + QUOTE;
	private final static Logger lg = LogManager.getLogger(CommentStringTokenizer.class);

	private final static char SPACE_CHAR = ' ';

	private final BufferedReader reader;
	private final LinkedList<Token> tokenLinkedList = new LinkedList<>();
	private String beforeComment = null;
	int currLine = 0;
	int currCol = 0;

	/**
	 * Initialize the `CommentStringTokenizer` with a `BufferedReader`
	 * <br/>
	 * BufferedReader can be chain-initialized to handle multiple different types of readers, including files, streams, etc.
	 * @see BufferedReader
	 * @param str BufferedReader
	 */
	public CommentStringTokenizer(BufferedReader str) {
		this.reader = str;
		this.refreshCurrTokenizer();
	}

	/**
	 * Initialize the `CommentStringTokenizer` with an in-memory string
	 *
	 * @param str the data to tokenize
	 */
	public CommentStringTokenizer(String str) {
		this(new BufferedReader(new StringReader(str)));
	}

	/**
	 * Fetches the index of the current column currently being processed.
 	 * @return int the column index
	 */
	public int columnIndex() {
		return this.currCol;
	}

	/**
	 * Fetches the index of the current row/line currently being processed.
	 * @return int the line index
	 */
	public int lineIndex() {
		return this.currLine;
	}

	/**
	 * Checks if there are any more tokens left to process
	 * @return boolean whether more tokens exist to process.
	 */
	public boolean hasMoreTokens() {
		this.refreshCurrTokenizer();
		return !this.tokenLinkedList.isEmpty();
	}

	/**
	 * Consumes tokens until a semicolon is reached.
	 * @return all collected tokens, condensed into a single string
	 */
	public String readToSemicolon() {
		StringBuilder expressionString = new StringBuilder();

		while (this.hasMoreTokens()) {
			String token = this.nextToken();
			if (token.equals(";")) break;
			if (token.endsWith(";")) {
				expressionString.append(token, 0, token.length() - 1);
				break;
			}
			expressionString.append(token);
		}
		return expressionString.toString();
	}


	/**
	 * Accesses the internal buffer and processes the next token.
	 *
	 * @return next token, as a raw string
	 * @throws NoSuchElementException if {@link #hasMoreTokens()} is false
	 */
	public String nextToken() {
		return this.next().getValue();
	}

	/**
	 * return next token as full object
	 *
	 * @return next token, as a wrapped object
	 * @throws NoSuchElementException if {@link #hasMoreTokens()} is false
	 */
	public Token next() {
		// `this::refreshCurrTokenizer()` is called by `this.hasMoreTokens()`
		if (!this.hasMoreTokens()) throw new NoSuchElementException();

		Token token = this.tokenLinkedList.removeFirst();
		if (!lg.isTraceEnabled()) return token;
		//show all if logging "trace".
		Token.CommentState cs = token.getCommentInfo();
		String msg = token + " comments " + cs;
		switch (cs) {
			case NONE:
				lg.trace(msg);
				break;
			case BEFORE:
				msg += ": " + token.getBeforeComment();
				lg.trace(msg);
				break;
			case AFTER:
				msg += ": " + token.getAfterComment();
				lg.trace(msg);
				break;
			case BOTH:
				msg += " /*" + token.getBeforeComment() + "*/ //" + token.getAfterComment();
				lg.trace(msg);
				break;
		}
		return token;
	}


	/**
	 * Pushes token into the internal buffer in the first position.
	 * <br/>
	 * Note that this function does not adjust column / line index.
	 * There may be a token that has the same column and line index as the pushed token
	 * @param token token to add to the front of the queue/buffer.
	 */
	public void pushToken(String token) {
		Token newToken = new Token(token, this.columnIndex(), this.lineIndex());
		this.tokenLinkedList.addFirst(newToken);
	}

	/**
	 * remove leading space from string if present
	 *
	 * @param in -- may not be null
	 * @return in or in minus leading space
	 */
	private String removeLeadingSpace(String in) {
		if (!in.startsWith(SPACE)) return in;
		return in.substring(1);
	}


	/**
	 * if tokenLinkedList is empty, fill with tokens from next non-empty line of text
	 */
	private void refreshCurrTokenizer() {
		if (!this.tokenLinkedList.isEmpty()) return;

		lg.trace("Refreshing tokenizer...");
		while (this.tokenLinkedList.isEmpty()) { // read until there is something in the list
			String nextLine;
			try {
				nextLine = this.reader.readLine();
			} catch (IOException e) {
				throw new NoSuchElementException(e.getMessage());
			}
			if (null == nextLine) return;
			lg.trace("Next line: `{}`", nextLine);

			StringTokenizer lineTokenizer = new StringTokenizer(nextLine, DELIMITERS, true);
			this.currLine++;
			this.currCol = 0;
			boolean currentlyInsideQuotes = false;
			Token quotedToken = null;
			Token firstToken = null;
			// 1. comments use C-style line comments "//"
			// 2. flag comments until end of line (reset only by \n)
			// 3. comment token is ignored within quotes
			// 4. if not inside quotes, forget about comment, space and tab
			while (lineTokenizer.hasMoreElements()) {
				String nextToken = lineTokenizer.nextToken();

				// Handle white space and special characters if we're not inside a quotes section
				if (!currentlyInsideQuotes) {
					if (nextToken.equals(SPACE) || nextToken.equals(TAB)) {
						this.currCol += nextToken.length();
						continue;
					}
					if (nextToken.startsWith(Commented.BEFORE_COMMENT)) {
						if (firstToken != null) {
							String comment = nextLine.substring(this.currCol + Commented.BEFORE_COMMENT.length());
							firstToken.setAfterComment(comment);
						}
						break;
					}
					if (nextToken.startsWith(Commented.AFTER_COMMENT)) {
						lineTokenizer = this.parseAndConsumeBeforeComment(nextLine);
						continue;
					}
				}

				if (nextToken.equals(QUOTE)) {
					if (currentlyInsideQuotes && quotedToken != null){
						this.tokenLinkedList.addLast(quotedToken);
						quotedToken = null;
					}
					currentlyInsideQuotes = !currentlyInsideQuotes;
					this.currCol++;
					continue;
				}

				Token newToken = new Token(nextToken, this.currCol, this.currLine);
				if (firstToken == null) {
					firstToken = newToken;
					firstToken.setBeforeComment(this.beforeComment);
					this.beforeComment = null;
				}
				this.currCol += nextToken.length();

				if (currentlyInsideQuotes) {
					// start a new quotedToken or append to current one, add to linked list after receiving end-quote
					if (quotedToken == null) {
						quotedToken = newToken;
					} else {
						quotedToken.join(newToken);
					}
				} else {
					this.tokenLinkedList.addLast(newToken);
				}
			}
			if (currentlyInsideQuotes) {
				throw new RuntimeException("unterminated string, expected a \" before end of line");
			}
		}
	}

	/**
	 * consume input from {@link #reader} until {@link Commented#END_BEFORE_COMMENT} found. Store
	 * comment in {@link #beforeComment}
	 *
	 * @param nextLine line currently being parsed
	 * @return new StringTokenizer for remaining part of file
	 */
	private StringTokenizer parseAndConsumeBeforeComment(String nextLine) {
		String restOfLine = nextLine.substring(this.currCol + Commented.AFTER_COMMENT.length());
		int endMark = restOfLine.indexOf(Commented.END_BEFORE_COMMENT);
		if (endMark != Commented.CHAR_NOT_FOUND) {
			this.beforeComment = this.removeLeadingSpace(restOfLine.substring(0, endMark));
			String remainingLine = restOfLine.substring(endMark + Commented.END_BEFORE_COMMENT.length());
			this.currCol += Commented.AFTER_COMMENT.length() + this.beforeComment.length() + Commented.END_BEFORE_COMMENT.length();
			return new StringTokenizer(remainingLine, DELIMITERS, true);
		}
		assert !restOfLine.contains(Commented.END_BEFORE_COMMENT);

		StringBuilder sb = new StringBuilder(restOfLine);
		char NEWLINE = '\n';
		while (true) {
			sb.append(NEWLINE);
			this.currLine++;
			String line;
			try {
				line = this.reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				throw new NoSuchElementException(e.getMessage());
			}
			int mark = line.indexOf(Commented.END_BEFORE_COMMENT);
			if (mark == Commented.CHAR_NOT_FOUND) {
				sb.append(line);
				continue;
			}
			String endOfComment = line.substring(0, mark);
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
			this.beforeComment = sb.substring(begin, last);
			String remainingLine = line.substring(mark + Commented.END_BEFORE_COMMENT.length());
			this.currCol = this.beforeComment.length() + Commented.END_BEFORE_COMMENT.length();
			return new StringTokenizer(remainingLine, DELIMITERS, true);
		}
	}
}
