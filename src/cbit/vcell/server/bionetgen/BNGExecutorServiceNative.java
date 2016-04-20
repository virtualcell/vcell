/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server.bionetgen;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.util.Executable2;
import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;

import cbit.vcell.client.constants.VCellCodeVersion;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.resource.VersionedLibrary;
import cbit.vcell.solvers.BioNetGenExecutable;

/**
 * Insert the type's description here.
 * Creation date: (9/13/2006 9:26:26 AM)
 * @author: Fei Gao
 */
class BNGExecutorServiceNative implements BNGExecutorService {
	private final static String RUN_NETWORK = "run_network";
	private final static String EXE_SUFFIX; 
	private final static String EXE_BNG; 
	private final static String RES_EXE_BNG; 
	// run_network is called from within BNG2.exe, so the extension is not controlled from here
	private final static String EXE_RUN_NETWORK;
	private final static String RES_EXE_RUN_NETWORK;

	private final static String suffix_input = ".bngl";
	/**
	 * the default standard error String returned by run_network if executed with no args;
	 * used to determine successful execution
	 */
	private final static String RUN_NETWORK_DEFAULT_STRING = "Usage:";

	/**
	 * working directory for files, and sentinel for {@link #initialize()}
	 */
	private static File workingDir = null;
	/**
	 * binary go here; as of BioNetGen-2.2.6 BNG2 expects run_network to be in "bin" subdirectory
	 */
	private static File binDir;
	private BioNetGenExecutable executable = null;
	private final BNGInput bngInput;
	private final Long timeoutDurationMS; // ignore if null, else gives time limit for process
	
	private transient List<BioNetGenUpdaterCallback> callbacks = null;
	private boolean stopped = false;
	private long startTime = System.currentTimeMillis();

	private static File file_exe_bng = null;
	private static File file_exe_run_network = null;
	private static String runNetworkError = null; 
	private static int[] returnCodes = null; 
	static {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		EXE_SUFFIX = osi.isWindows() ? ".exe" : "";
		EXE_BNG = "BNG2" + EXE_SUFFIX;
		//RES_EXE_BNG = ResourceUtil.RES_PACKAGE + "/" + EXE_BNG;
		RES_EXE_BNG = osi.getResourcePackage() + EXE_BNG;
		// run_network is called from within BNG2.exe, so the extension is not controlled from here
		EXE_RUN_NETWORK =RUN_NETWORK  + (osi.isWindows() ? ".exe" : "");
		RES_EXE_RUN_NETWORK = osi.getResourcePackage() + EXE_RUN_NETWORK;
	}
	
	private static final Logger LG = Logger.getLogger(BNGExecutorServiceNative.class);

/**
 * BNGUtils constructor comment.
 */
BNGExecutorServiceNative(BNGInput bngInput, Long timeoutDurationMS) {
	super();
	this.bngInput = bngInput;
	this.timeoutDurationMS = timeoutDurationMS;
}

@Override
public void registerBngUpdaterCallback(BioNetGenUpdaterCallback callbackOwner) {
	getCallbacks().add(callbackOwner);
}

@Override
public List<BioNetGenUpdaterCallback> getCallbacks() {
	if(callbacks == null) {
		callbacks = new ArrayList<BioNetGenUpdaterCallback>();
	}
	return callbacks;
}

/**
 * Insert the method's description here.
 * Creation date: (6/23/2005 3:57:30 PM)
 */
@Override
public BNGOutput executeBNG() throws BNGException {
	if (executable != null) {
		throw new BNGException("You can only run BNG one at a time!");
	}

	BNGOutput bngOutput = null;
	startTime = System.currentTimeMillis();

	initialize();	// prepare executables and working directory
	
	try {
		File bngInputFile = null;
		FileOutputStream fos = null;

		String tempFilePrefix = workingDir.getName();
		try {
			bngInputFile = new File(workingDir, tempFilePrefix + suffix_input);
			fos = new java.io.FileOutputStream(bngInputFile);
		}catch (java.io.IOException e){
			if (LG.isEnabledFor(Level.WARN) ) {
				LG.warn("error opening input file '"+bngInputFile,e);
			}
			e.printStackTrace(System.out);
			throw new RuntimeException("error opening input file '"+bngInputFile.getName()+": "+e.getMessage());
		}	

		PrintWriter inputFile = new PrintWriter(fos);
		System.out.println("Input file is: " + bngInputFile.getPath());
		inputFile.print(bngInput.getInputString());
		inputFile.close();

		System.out.println("BNGExecutorService.executeBNG(): input = \n"+bngInput.getInputString());

		// run BNG
		String[] cmd = new String[] {file_exe_bng.getAbsolutePath(), bngInputFile.getAbsolutePath()};
		//		executable = new org.vcell.util.Executable(cmd);
		long timeoutMS = 0;
		if (timeoutDurationMS != null){
			timeoutMS = timeoutDurationMS.longValue();
		}
		executable = new BioNetGenExecutable(cmd,timeoutMS);
		executable.setWorkingDir(workingDir);
		executable.inheritCallbacks(getCallbacks());
		executable.start( returnCodes ); 

		String stdoutString; 
		if (executable.getStatus() != org.vcell.util.ExecutableStatus.STOPPED) { 
			stdoutString = executable.getStdoutString();
		}
		else {
			stdoutString = "Stopped by user. Output from BioNetGen may be truncated";	
		}
		if (executable.getExitValue() == 1) {
			String stderrString = executable.getStderrString();
			if (stderrString.contains(RUN_NETWORK)) {
				stdoutString = "run_network not supported on this operating system; partial data may be available\n"
						+ executable.getStdoutString();
			}
		}

		File[] files = workingDir.listFiles();
		ArrayList<String> filenames = new ArrayList<String>();
		ArrayList<String> filecontents = new ArrayList<String>();

		for (File file : files) {
			if (file.equals(binDir)) {
				continue;
			}
			String filename = file.getName();
			if (LG.isDebugEnabled()) {
				LG.debug("BNG executor trying to read " + filename);
			}
			filenames.add(filename);
			filecontents.add(FileUtils.readFileToString(file) );
		}		
		bngOutput = new BNGOutput(stdoutString, filenames.toArray(new String[0]), filecontents.toArray(new String[0]));

	} catch(ExecutableException | IOException ex ){
		if (LG.isEnabledFor(Level.WARN) ) {
			LG.warn("error executable BNG", ex); 
		}
		if (executable==null || executable.getStderrString().trim().length() == 0) {
			throw new BNGException("Error executing BNG", ex); 
		}
		throw new BNGException(executable.getStderrString(),ex);
	} finally {
		File[] files = workingDir.listFiles();
		for ( File file : files) {
			if (!file.equals(binDir)) {
				file.delete( );
			}
		}
	}
	return bngOutput;
}

/**
 * @throws BNGException if problem setting up executables / directory
 */
private static synchronized void initialize() {
	if (workingDir == null) {
		try {
			File bngHome = new File(ResourceUtil.getVcellHome(), "BioNetGen" + VCellCodeVersion.CURRENT_MAJOR + '-' + VCellCodeVersion.CURRENT_MINOR);
			binDir = new File(bngHome,"bin"); //As of 2-2-6 BNG2 expects run_network to be in "bin" subdirectory, so just go with it
			binDir.mkdirs();

			workingDir = bngHome;//createTempDirectory(prefix, bngHome);	
			System.out.println("BNG working directory is " + bngHome.getAbsolutePath());

			file_exe_bng = new java.io.File(binDir, EXE_BNG);
			file_exe_run_network = new java.io.File(binDir, EXE_RUN_NETWORK);
			ResourceUtil.loadExecutable(EXE_BNG, VersionedLibrary.CYGWIN_DLL_BIONETGEN,binDir); 

			if (!file_exe_bng.exists()) {
				ResourceUtil.writeResourceToExecutableFile(RES_EXE_BNG, file_exe_bng);
			}
			if (!file_exe_run_network.exists()) {	
				ResourceUtil.writeResourceToExecutableFile(RES_EXE_RUN_NETWORK, file_exe_run_network);
			}
			testRunNetwork();
			Runnable r= ( )-> org.apache.commons.io.FileUtils.deleteQuietly(workingDir);
			Runtime.getRuntime().addShutdownHook( new Thread(r)  );
		} catch (IOException ioe) {
			workingDir = null;
			ioe.printStackTrace();
			throw new BNGException("Can't setup BioNetGen ", ioe);
		}
	}
}

private static void testRunNetwork( ) {
	Executable2 e2 = new Executable2(file_exe_run_network.getAbsolutePath());
	try {
		e2.start();
		runNetworkError = e2.getStderrString();
		if (runNetworkError.startsWith(RUN_NETWORK_DEFAULT_STRING)) {
			//expected output, 
			runNetworkError = null;
			returnCodes = new int[]{0};
			return;
		}
	} catch (ExecutableException e) {
		runNetworkError = e.getMessage(); 
		if (LG.isInfoEnabled() ) {
			LG.info(file_exe_run_network + " test error output " + runNetworkError);
		}
	}
	returnCodes = new int[]{0,1};
}

/**
 * Insert the method's description here.
 * Creation date: (6/23/2005 3:57:30 PM)
 */
@Override
public void stopBNG() throws Exception {
	setToStopped();
	if (executable != null) {
		executable.stop();
	}
}

private void setToStopped() {
	this.stopped = true;
}

@Override
public boolean isStopped() {
	return stopped;
}

@Override
public final long getStartTime() {
	return startTime;
}
}
