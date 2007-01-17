package cbit.vcell.math;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
	while (commentTokenizer.hasMoreElements()){
		System.out.println("line("+(commentTokenizer.lineIndex()+1)+") '"+commentTokenizer.nextElement()+"'");
	}	
}
}
