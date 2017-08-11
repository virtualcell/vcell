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
 * This type was created in VisualAge.
 */
public class CommentStringTokenizerTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	String testString = "\n\nhello there \"hello there\"\n \nthis is before // the comment\n this is after // the \n comment";
	System.out.println("input:\n");
	System.out.println(testString);
	System.out.println("\ntokens:\n");
	CommentStringTokenizer commentTokenizer = new CommentStringTokenizer(testString);
	while (commentTokenizer.hasMoreTokens()){
		System.out.println("line("+(commentTokenizer.lineIndex()+1)+") '"+commentTokenizer.nextToken()+"'");
	}	
}
}
