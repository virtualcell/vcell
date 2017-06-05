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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.vcell.util.ExecutableException;
import org.vcell.util.FileUtils;

import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solvers.BioNetGenExecutable;

/**
 * Insert the type's description here.
 * Creation date: (9/13/2006 9:26:26 AM)
 * @author: Fei Gao
 */
class BNGExecutorServiceNative implements BNGExecutorService {

	private final static String suffix_input = ".bngl";
	/**
	 * the default standard error String returned by run_network if executed with no args;
	 * used to determine successful execution
	 */
	private final static String RUN_NETWORK_DEFAULT_STRING = "Usage:";

	/**
	 * binary go here; as of BioNetGen-2.2.6 BNG2 expects run_network to be in "bin" subdirectory
	 */
	private BioNetGenExecutable executable = null;
	private final BNGInput bngInput;
	private final Long timeoutDurationMS; // ignore if null, else gives time limit for process
	
	private transient List<BioNetGenUpdaterCallback> callbacks = null;
	private boolean stopped = false;
	private long startTime = System.currentTimeMillis();

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

	File workingDir = null;
	try {
		workingDir = Files.createTempDirectory("Bng_working_").toFile();
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

//		System.out.println("BNGExecutorService.executeBNG(): input = \n"+bngInput.getInputString());
		System.out.println("BNGExecutorService.executeBNG()\n");

		File perlExe = ResourceUtil.getPerlExe();
		String bngPerlFilePath = ResourceUtil.getBNG2_perl_file();
		// run BNG
		String[] cmd = new String[] {perlExe.getAbsolutePath(), bngPerlFilePath, bngInputFile.getAbsolutePath()};
		//		executable = new org.vcell.util.Executable(cmd);
		long timeoutMS = 0;
		if (timeoutDurationMS != null){
			timeoutMS = timeoutDurationMS.longValue();
		}
		executable = new BioNetGenExecutable(cmd,timeoutMS, workingDir);
		executable.inheritCallbacks(getCallbacks());
		int[] expectedReturnCodes = new int[] { 0 };
		executable.start( expectedReturnCodes ); 

		String stdoutString; 
		if (executable.getStatus() != org.vcell.util.ExecutableStatus.STOPPED) { 
			stdoutString = executable.getStdoutString();
		}
		else {
			stdoutString = "Stopped by user. Output from BioNetGen may be truncated";	
		}
		if (executable.getExitValue() == 1) {
			String stderrString = executable.getStderrString();
			if (stderrString.contains("run_network")) {
				stdoutString = "run_network not supported on this operating system; partial data may be available\n"
						+ executable.getStdoutString();
			}
		}

		File[] files = workingDir.listFiles();
		ArrayList<String> filenames = new ArrayList<String>();
		ArrayList<String> filecontents = new ArrayList<String>();

		for (File file : files) {
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
		if (workingDir!=null){
			File[] files = workingDir.listFiles();
			for ( File file : files) {
				file.delete( );
			}
			workingDir.delete();
		}
	}
	return bngOutput;
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
