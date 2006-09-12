package cbit.vcell.server.bionetgen;
import java.util.Random;
import java.io.File;
import java.io.FileOutputStream;

import cbit.gui.PropertyLoader;
import cbit.util.DataAccessException;
import cbit.util.SessionLog;
import cbit.util.User;

import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Insert the type's description here.
 * Creation date: (6/23/2005 2:41:27 PM)
 * @author: Anuradha Lakshminarayana
 */

 
public class BNGServerImpl {
	private SessionLog log = null;
	private File workingDir = null;
	private final static String suffix_input = ".bngl";
	private final static String prefix = "vcell_bng_";

/**
 * LocalBioNetGen constructor comment.
 */
public BNGServerImpl(SessionLog arg_log) {
	super();
	log = arg_log;
	workingDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.tempDirProperty));
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2005 3:57:30 PM)
 */
private File createInputFileFromRules(BNGInput rulesInput, File tempDir) {
	// create an input file in the BioNetGen/NEW directory, so that the perl script knows where to look for it ,,,
	File bngInputFile = null;
	FileOutputStream fos = null;
	
	try {
		bngInputFile = File.createTempFile(prefix, suffix_input, tempDir);
		fos = new java.io.FileOutputStream(bngInputFile);
	}catch (java.io.IOException e){
		e.printStackTrace(System.out);
		throw new RuntimeException("error opening input file '"+bngInputFile.getName()+": "+e.getMessage());
	}	
		
	PrintWriter inputFile = new PrintWriter(fos);
	inputFile.print(rulesInput.getInputString());
	inputFile.close();

	return bngInputFile;
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 3:46:33 PM)
 * @return java.io.File
 * @param parentDir java.io.File
 */
private File createTempDirectory(String prefix, File parentDir) {
	while (true) {
		int  counter = new Random().nextInt() & 0xffff;
		
		File tempDir = new File(parentDir, prefix + Integer.toString(counter));
		if (!tempDir.exists()) {
			tempDir.mkdir();
			return tempDir;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2005 3:57:30 PM)
 */
public BNGOutput executeBNG(User user, BNGInput bngRules) throws DataAccessException {
	//
	// create an input file for the BNG in the BNG directory
	// create the perl command to run BioNetGenerator on the input file to generate the net file and any other options (say SBML file).
	//

	/**
		Usign the perl command below caused the exception shown below. 
		Hence using the other style path for now. Will rectify the problem later.
		
	 	String perlCommand = perl h:\BioNetGen_2.0.33\Perl2\BNG2.pl h:\BioNetGen_2.0.33\Models2\toy-jim.bngl;		
	**/

	File tempDir = createTempDirectory(prefix, workingDir);
	File bngInputFile = createInputFileFromRules(bngRules, tempDir);
	
 	String perlCommand = "\"" + PropertyLoader.getRequiredProperty(PropertyLoader.vcellBNGPerl) + "\" \"" 
 		+ PropertyLoader.getRequiredProperty(PropertyLoader.vcellBNGScript) + "\" \"" + bngInputFile.getAbsolutePath() + "\"";

 	BNGOutput bngOutput = null;
 	
	try {
		log.print("-------------Starting BNG ...-------------------------------");
		cbit.util.Executable executable = new cbit.util.Executable(perlCommand);
		executable.start();

		File[] files = tempDir.listFiles();
		String[] filenames = new String[files.length];
		String[] filecontents = new String[files.length];

		for (int i = 0; i < files.length; i ++) {
			filenames[i] = files[i].getName();
			filecontents[i] = getFileContentFromFileName(files[i]);
			files[i].delete();
		}
		tempDir.delete();
		
		bngOutput = new BNGOutput(executable.getStdoutString(), filenames, filecontents);
	
		log.print("--------------Finished BNG----------------------------");
	} catch (Exception e) {
		log.exception(e);
		throw new DataAccessException("Failed running BioNetGen: " + e.getMessage());
	} 

	return bngOutput;
}


/**
 * Insert the method's description here.
 * Creation date: (6/30/2005 5:22:16 PM)
 * @return cbit.util.BigString
 */
private String getFileContentFromFileName(File file) {
	if (!file.exists()) {
		log.print(file + " doesn't exists!");
		return "";
	}
	
	// Read characters from input file into character array and transfer into string buffer.
	StringBuffer stringBuffer = new StringBuffer();
	FileInputStream fis = null;
	try {
		fis = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(reader);
		char charArray[] = new char[10000];
		while (true) {
			int numRead = br.read(charArray, 0, charArray.length);
			if (numRead > 0) {
				stringBuffer.append(charArray,0,numRead);
			} else if (numRead == -1) {
				break;
			}
		}
		fis.close();
	} catch (IOException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Errors reading input file .... ");
	}
	if (stringBuffer == null || stringBuffer.length() == 0){
		System.out.println("<<<SYSOUT ALERT>>> null input file");
	}

	return stringBuffer.toString();
}
}