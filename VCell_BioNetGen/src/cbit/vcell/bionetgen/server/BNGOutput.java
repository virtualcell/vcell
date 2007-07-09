package cbit.vcell.bionetgen.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;

import org.vcell.util.BigString;
/**
 * Insert the type's description here.
 * Creation date: (6/27/2005 2:59:47 PM)
 * @author: Anuradha Lakshminarayana
 */
public class BNGOutput implements java.io.Serializable {
	private org.vcell.util.BigString consoleOutput;
	private org.vcell.util.BigString[] bng_fileContents;
	private String[] bng_filenames;

/**
 * BNGOutput constructor comment.
 */
public BNGOutput(String argConsoleOutput, String[] filenames, String[] filecontents) {
	super();
	consoleOutput = new BigString(argConsoleOutput);
	if (filenames.length != filecontents.length) {
		throw new RuntimeException("The lengths of filenames and filecontents don't match");
	}
	bng_filenames = filenames;
	bng_fileContents = new BigString[filecontents.length];
	for (int i = 0; i < filecontents.length; i ++) {
		bng_fileContents[i] = new BigString(filecontents[i]);
	}		
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 12:03:34 PM)
 * @return cbit.util.BigString
 */
public String getBNGFileContent(int index) {
	return bng_fileContents[index].toString();
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 3:34:34 PM)
 * @return java.lang.String[]
 */
public java.lang.String[] getBNGFilenames() {
	return bng_filenames;
}


/**
 * Insert the method's description here.
 * Creation date: (6/27/2005 3:57:21 PM)
 * @return cbit.util.BigString
 */
public String getConsoleOutput() {
	return consoleOutput.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 12:03:34 PM)
 * @return cbit.util.BigString
 */
public int getNumBNGFiles() {
	return bng_filenames.length;
}
}