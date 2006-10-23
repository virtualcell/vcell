package cbit.vcell.solver.ode;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.PropertyLoader;
import cbit.util.SessionLog;
/**
 * Insert the type's description here.
 * Creation date: (3/23/2001 1:23:14 PM)
 * @author: John Wagner
 */
public class Compiler {
	private SessionLog fieldSessionLog = null;
	private java.lang.String fieldIncludes = new String();
	private java.lang.String fieldDefines = new String();
	private java.lang.String fieldOptions = new String();
	private java.lang.String fieldLibraries = new String();

/**
 * Compiler constructor comment.
 */
public Compiler(SessionLog sessionLog) {
	super();
	fieldSessionLog = sessionLog;
}


/**
 * Compiler constructor comment.
 */
public int compile(String inputFilename, String outputFilename) throws Exception {
	String Compile = System.getProperty(PropertyLoader.compilerProperty);				// "cl /c";
	String ObjOutputSpecifier = System.getProperty(PropertyLoader.objOutputProperty);	// "/Fo";
	//
	String compileCommand =
		Compile + " "
			+ inputFilename + " "
			+ getOptions() + " "
			+ getDefines() + " "
			+ getIncludes() + " "
			+ ObjOutputSpecifier
			+ outputFilename;
	System.out.println("%%%%%%Compiling with: " + compileCommand);
	//
	cbit.util.Executable executable = new cbit.util.Executable(compileCommand);
	executable.start();
	//
	String stdoutString = executable.getStdoutString();
	String stderrString = executable.getStderrString();
	//
	int retcode = executable.getExitValue().intValue();
	if (retcode == 0) {
		getSessionLog().print("Compile successful (return code = " + retcode + ")");
	} else {
		getSessionLog().print("Compile failed (return code = " + retcode + ")");
		getSessionLog().print("stderr:\n" + stderrString);
		getSessionLog().print("stdout:\n" + stdoutString);
		throw new Exception("compilation failed, return code = " + retcode + "\n" + stderrString);
	}
	//
	return(executable.getExitValue().intValue());
}


/**
 * Gets the defines property (java.lang.String) value.
 * @return The defines property value.
 * @see #setDefines
 */
public java.lang.String getDefines() {
	return fieldDefines;
}


/**
 * Gets the includes property (java.lang.String) value.
 * @return The includes property value.
 * @see #setIncludes
 */
public java.lang.String getIncludes() {
	return fieldIncludes;
}


/**
 * Gets the libraries property (java.lang.String) value.
 * @return The libraries property value.
 * @see #setLibraries
 */
public java.lang.String getLibraries() {
	return fieldLibraries;
}


/**
 * Gets the options property (java.lang.String) value.
 * @return The options property value.
 * @see #setOptions
 */
public java.lang.String getOptions() {
	return fieldOptions;
}


/**
 * Gets the sessionLog property (cbit.vcell.server.SessionLog) value.
 * @return The sessionLog property value.
 */
public SessionLog getSessionLog() {
	return fieldSessionLog;
}


/**
 * Linker.  Later we will have addIncludeFile() and
 * addLibrary() calls in the class, so we won't have
 * all these libraries hard-coded HERE...they can
 * get hardcoded in the caller.
 */
public int link(String inputFilenames[], String outputFilename) throws Exception {
	//String Compile = System.getProperty(PropertyLoader.compilerProperty);				// "cl /c";
	String Link = System.getProperty(PropertyLoader.linkerProperty);					// "cl";
	String exeOutputSpecifier = System.getProperty(PropertyLoader.exeOutputProperty);	// "/Fe";
	//
	String libs = System.getProperty(PropertyLoader.libsProperty);						// libraryDir+"VCLIB.lib";
	String exeSuffix = System.getProperty(PropertyLoader.exesuffixProperty);			// ".exe";
	//
	String objectFiles = new String();
	for (int i = 0; i < inputFilenames.length; i++) {
		objectFiles = objectFiles + " " + inputFilenames[i];
	}
	//
	String linkCommand =
		Link + " "
		+ getOptions() + " "
		+ objectFiles + " "
		+ getLibraries() + " "
		+ exeOutputSpecifier + outputFilename;
	System.out.println("%%%%%%Linking with: " + linkCommand);
	//
	cbit.util.Executable executable = new cbit.util.Executable(linkCommand);
	executable.start();
	//
	String stdoutString = executable.getStdoutString();
	String stderrString = executable.getStderrString();
	//
	int retcode = executable.getExitValue().intValue();
	if (retcode == 0) {
		getSessionLog().print("link successful (return code = " + retcode + ")");
	} else {
		getSessionLog().print("link failed (return code = " + retcode + ")");
		getSessionLog().print("stderr:\n" + stderrString);
		getSessionLog().print("stdout:\n" + stdoutString);
		throw new Exception("link failed, return code = " + retcode + "\n" + stderrString);
	}
	//
	return(executable.getExitValue().intValue());
}


/**
 * Sets the defines property (java.lang.String) value.
 * @param defines The new value for the property.
 * @see #getDefines
 */
public void setDefines(java.lang.String defines) {
	fieldDefines = defines;
}


/**
 * Sets the includes property (java.lang.String) value.
 * @param includes The new value for the property.
 * @see #getIncludes
 */
public void setIncludes(java.lang.String includes) {
	fieldIncludes = includes;
}


/**
 * Sets the libraries property (java.lang.String) value.
 * @param libraries The new value for the property.
 * @see #getLibraries
 */
public void setLibraries(java.lang.String libraries) {
	fieldLibraries = libraries;
}


/**
 * Sets the options property (java.lang.String) value.
 * @param options The new value for the property.
 * @see #getOptions
 */
public void setOptions(java.lang.String options) {
	fieldOptions = options;
}
}