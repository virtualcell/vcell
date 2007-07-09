package cbit.vcell.bionetgen.server;
import java.io.*;

/**
 * Insert the type's description here.
 * Creation date: (9/13/2006 9:26:26 AM)
 * @author: Fei Gao
 */
public class BNGUtils {
	private final static boolean bWindows = System.getProperty("os.name").indexOf("Windows") >= 0 ? true : false;
	private final static String EXE_SUFFIX = bWindows ? ".exe" : "";
	private final static String EXE_BNG = "BNG2" + EXE_SUFFIX;
	private final static String RES_EXE_BNG = "/cbit/vcell/client/bionetgen/BNG2" + EXE_SUFFIX;
	private final static String DLL_CYGWIN = "cygwin1.dll";
	private final static String RES_DLL_CYGWIN = "/cbit/vcell/client/bionetgen/cygwin1.dll";
	private final static String EXE_RUN_NETWORK = "run_network" + EXE_SUFFIX;
	private final static String RES_EXE_RUN_NETWORK = "/cbit/vcell/client/bionetgen/run_network" + EXE_SUFFIX;

	private final static String suffix_input = ".bngl";
	private final static String prefix = "vcell_bng_";

	private static File workingDir = null;
	private static org.vcell.util.Executable executable = null;
	private static boolean bFirstTime = true;

	private static File file_exe_bng = null;
	private static File file_dll_cygwin = null;
	private static File file_exe_run_network = null;

/**
 * BNGUtils constructor comment.
 */
public BNGUtils() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 3:46:33 PM)
 * @return java.io.File
 * @param parentDir java.io.File
 */
private static File createTempDirectory(String prefix, File parentDir) {
	while (true) {
		int  counter = new java.util.Random().nextInt() & 0xffff;
		
		File tempDir = new File(parentDir, prefix + Integer.toString(counter));
		if (!tempDir.exists()) {
			tempDir.mkdir();
			return tempDir;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2006 1:33:20 PM)
 * @return java.lang.String
 * @param str java.lang.String
 */
private static String escapeSpace(String str) {
	if (bWindows) {
		return "\"" + str + "\"";
	}

	return str;

	//For now, don't escape on Unix
	
	//StringBuffer sb = new StringBuffer();
	//for (int i = 0; i < str.length(); i ++) {
		//if (str.charAt(i) == ' ') {
			//sb.append("\)
		//}
	//return null;
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2005 3:57:30 PM)
 */
public static BNGOutput executeBNG(BNGInput bngRules) throws Exception {
	if (executable != null) {
		throw new RuntimeException("You can only run BNG one at a time!");
	}

	BNGOutput bngOutput = null;
	try {
		// prepare executables and working directory;
		
		initialize();
		
		File bngInputFile = null;
		FileOutputStream fos = null;


		String tempFilePrefix = workingDir.getName();
		try {
			bngInputFile = new File(workingDir, tempFilePrefix + suffix_input);
			fos = new java.io.FileOutputStream(bngInputFile);
		}catch (java.io.IOException e){
			e.printStackTrace(System.out);
			throw new RuntimeException("error opening input file '"+bngInputFile.getName()+": "+e.getMessage());
		}	
			
		PrintWriter inputFile = new PrintWriter(fos);
		inputFile.print(bngRules.getInputString());
		inputFile.close();
		
		System.out.println("-------------Starting BNG ...-------------------------------");
		// run BNG
		String cmd = escapeSpace(file_exe_bng.getAbsolutePath()) + " " + escapeSpace(bngInputFile.getAbsolutePath());
		executable = new org.vcell.util.Executable(cmd);
		executable.start();
		
		String stdoutString = executable.getStdoutString();
		if (executable.getStatus() == org.vcell.util.ExecutableStatus.STOPPED && stdoutString.length() == 0) {
			stdoutString = "Stopped by user. No output from BioNetGen.";	
		}
		
		File[] files = workingDir.listFiles();
		String[] filenames = new String[files.length];
		String[] filecontents = new String[files.length];
		
		for (int i = 0; i < files.length; i ++) {
			filenames[i] = files[i].getName();
			filecontents[i] = readFileToString(files[i]);
			files[i].delete();
		}		
		workingDir.delete();
		
		bngOutput = new BNGOutput(stdoutString, filenames, filecontents);
		System.out.println("--------------Finished BNG----------------------------");
		
	} catch(org.vcell.util.ExecutableException ex ){
		if (executable.getStderrString().trim().length() == 0) {
			throw ex;
		}
		throw new org.vcell.util.ExecutableException(executable.getStderrString());
	} finally {
		if (workingDir != null && workingDir.exists()) {
			File[] files = workingDir.listFiles();
			
			for (int i = 0; i < files.length; i ++) {
				files[i].delete();
			}		
			workingDir.delete();
		}
		
		workingDir = null;
		executable = null;		
	}

	return bngOutput;
}


/**
 * Insert the method's description here.
 * Creation date: (9/19/2006 11:36:49 AM)
 */
private static void initialize() throws Exception {
	File userHome = new File(System.getProperty("user.home"));
	if (!userHome.exists()) {
		userHome = new File(".");		
	}
	File bngHome = new File(userHome, ".BioNetGen");
	if (!bngHome.exists()) {
		bngHome.mkdirs();
	}
	
	workingDir = createTempDirectory(prefix, bngHome);	
	System.out.println("BNG working directory is " + bngHome.getAbsolutePath());
	
	file_exe_bng = new java.io.File(bngHome, EXE_BNG);
	file_dll_cygwin = new java.io.File(bngHome, DLL_CYGWIN);
	file_exe_run_network = new java.io.File(bngHome, EXE_RUN_NETWORK);
	if (bFirstTime) {
		try {
			file_exe_bng.delete();
			if (bWindows) {
				file_dll_cygwin.delete();
			}
			file_exe_run_network.delete();

			writeFileFromResource(RES_EXE_BNG, file_exe_bng);
			if (bWindows) {
				writeFileFromResource(RES_DLL_CYGWIN, file_dll_cygwin);
			}
			writeFileFromResource(RES_EXE_RUN_NETWORK, file_exe_run_network);
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
		}
		bFirstTime = false;		
	} else {			
		if (!file_exe_bng.exists()) {
			writeFileFromResource(RES_EXE_BNG, file_exe_bng);
		}
		if (bWindows && !file_dll_cygwin.exists()) {
			writeFileFromResource(RES_DLL_CYGWIN, file_dll_cygwin);
		}
		if (!file_exe_run_network.exists()) {	
			writeFileFromResource(RES_EXE_RUN_NETWORK, file_exe_run_network);
		}
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (6/30/2005 5:22:16 PM)
 * @return cbit.util.BigString
 */
private static String readFileToString(File file) throws IOException {
		
	// Read characters from input file into character array and transfer into string buffer.
	BufferedReader br = null;
	StringBuffer stringBuffer = new StringBuffer();
	try {
		br = new BufferedReader(new FileReader(file));
		char charArray[] = new char[10000];
		while (true) {
			int numRead = br.read(charArray, 0, charArray.length);
			if (numRead == -1) {
				break;
			}
			if (numRead > 0) {
				stringBuffer.append(charArray,0,numRead);
			}
		}
	} finally {
		if (br != null) {
			br.close();
		}
	}

	return stringBuffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (6/23/2005 3:57:30 PM)
 */
public static void stopBNG() throws Exception {
	if (executable != null) {
		executable.stop();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (9/8/2006 2:57:35 PM)
 * @param resname java.lang.String
 * @param filename java.lang.String
 */
private static void writeFileFromResource(String resname, File file) throws Exception {
	java.net.URL url = BNGUtils.class.getResource(resname);
	if (url == null) {
		throw new RuntimeException("Can't get resource for " + resname);
	}
	
	BufferedInputStream bis = null;
	BufferedOutputStream bos = null;

	try {
		bis = new BufferedInputStream(url.openConnection().getInputStream());
		bos = new BufferedOutputStream(new FileOutputStream(file));
		byte byteArray[] = new byte[10000];
		while (true) {
			int numRead = bis.read(byteArray, 0, byteArray.length);
			if (numRead == -1) {
				break;
			}
			
			bos.write(byteArray, 0, numRead);
		}
		if (!bWindows) {
			System.out.println("Make " + file + " executable");
			org.vcell.util.Executable exe = new org.vcell.util.Executable("chmod 755 " + file);
			exe.start();
		}
	} finally {
		if (bis != null) {
			bis.close();
		}
		if (bos != null) {
			bos.close();
		}
	}
		
}
}