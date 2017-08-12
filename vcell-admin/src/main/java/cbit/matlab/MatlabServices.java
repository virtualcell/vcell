/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.matlab;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
/**
 * Insert the type's description here.
 * Creation date: (8/9/2005 11:31:49 AM)
 * @author: Jim Schaff
 */
public class MatlabServices {
	private static MatlabServices matlabServices = null;
	
	private Class jMatLink_class = null;
	private Object jMatLink_objRef = null;
	private boolean bOpen = false;
	private static boolean bDebug = false;

/**
 * MatlabServices constructor comment.
 */
private MatlabServices() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 11:35:39 AM)
 */
public synchronized void close() throws MatlabException {
	try {
		if (jMatLink_objRef!=null){
			invokeJMatLinkMethod("engClose",new Class[0], new Object[0]);
			jMatLink_objRef = null;
		}
	}finally{
		bOpen = false;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 1:53:16 PM)
 * @param mFileText java.lang.String
 * @param cmdString java.lang.String
 * @param stdout java.lang.StringBuffer
 */
public synchronized MatlabFunctionResults executeCommand(String command, String[] returnVariables) throws MatlabException {

	//
	// run the script
	//
	open();
	//
	// invoke jMatLink.engOutputBuffer()
	//
	invokeJMatLinkMethod("engOutputBuffer",new Class[0], new Object[0]);

	//
	// form the command-line
	//
	StringBuffer cmd = new StringBuffer();
	if (returnVariables==null || returnVariables.length==0){
		cmd.append(command);
	}else if (returnVariables.length==1){
		cmd.append(returnVariables[0]+" = "+command+";");
	}else if (returnVariables.length>1){
		cmd.append("[");
		for (int i = 0; i < returnVariables.length; i++){
			if (i>0){
				cmd.append(",");
			}
			cmd.append(returnVariables[i]);
		}
		cmd.append("] = "+command+";");
	}
	
	//
	// invoke jMatLink.engEvalString(cmd)
	//
	invokeJMatLinkMethod("engEvalString",new Class[] { String.class }, new Object[] { cmd.toString() } );

	MatlabFunctionResults matlabFunctionResults = new MatlabFunctionResults();

	//
	// invoke jMatLink.engGetOutputBuffer():String
	//
	String outputBufferString = (String)invokeJMatLinkMethod("engGetOutputBuffer",new Class[0], new Object[0]);
	
	matlabFunctionResults.setStdout(outputBufferString);
	
	for (int i = 0; returnVariables!=null && i < returnVariables.length; i++){
		//
		// invoke jMatLink.engGetArray(String varName):double[][]
		//
		double[][] value = (double[][])invokeJMatLinkMethod("engGetArray",new Class[] { String.class }, new Object[] { returnVariables[i] } );
		if (value != null) {
			matlabFunctionResults.addVariable(returnVariables[i],value);
		} 
	}
	return matlabFunctionResults;
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 1:53:16 PM)
 * @param mFileText java.lang.String
 * @param cmdString java.lang.String
 * @param stdout java.lang.StringBuffer
 */
public synchronized MatlabFunctionResults executeFunction(File matlabScriptDir, String mFileText, String functionName, String[] returnVariables) throws MatlabException {

	try {
		//
		// Check matlab script directory path
		//
		if (matlabScriptDir == null) {
			throw new IllegalArgumentException("Matlab script directory is null");
		}
		
		if (!matlabScriptDir.exists()) {
			boolean returnVal = matlabScriptDir.mkdir();
			if (!returnVal) {
				throw new RuntimeException("Failed to create matlab script directory "+matlabScriptDir);
			}
		}
		
		if (!matlabScriptDir.isDirectory()) {
			throw new FileNotFoundException(matlabScriptDir+" not a directory");
		}
		
		//
		// write m file to matlab path
		//
		java.io.File tmpFile = new java.io.File(matlabScriptDir,functionName+".m");
		java.io.FileWriter fileWriter = new FileWriter(tmpFile);
		fileWriter.write(mFileText);
		fileWriter.flush();
		fileWriter.close();

		//
		// run the script
		//
		open();
		//
		// invoke jMatLink.engOutputBuffer()
		//
		invokeJMatLinkMethod("engOutputBuffer",new Class[0], new Object[0]);

		//
		// invoke jMatLink.engEvalString('cd matlabPath')
		//
		invokeJMatLinkMethod("engEvalString",new Class[] { String.class }, new Object[] { new String("cd "+matlabScriptDir.getAbsolutePath()) } );

		//
		// form the command-line
		//
		StringBuffer cmd = new StringBuffer();
		if (returnVariables==null || returnVariables.length==0){
			cmd.append(functionName);
		}else if (returnVariables.length==1){
			cmd.append(returnVariables[0]+" = "+functionName+";");
		}else if (returnVariables.length>1){
			cmd.append("[");
			for (int i = 0; i < returnVariables.length; i++){
				if (i>0){
					cmd.append(",");
				}
				cmd.append(returnVariables[i]);
			}
			cmd.append("] = "+functionName+";");
		}
		
		//
		// invoke jMatLink.engEvalString(cmd)
		//
		invokeJMatLinkMethod("engEvalString",new Class[] { String.class }, new Object[] { cmd.toString() } );

		MatlabFunctionResults matlabFunctionResults = new MatlabFunctionResults();

		//
		// invoke jMatLink.engGetOutputBuffer():String
		//
		String outputBufferString = (String)invokeJMatLinkMethod("engGetOutputBuffer",new Class[0], new Object[0]);
		
		matlabFunctionResults.setStdout(outputBufferString);
		
		for (int i = 0; i < returnVariables.length; i++){
			//
			// invoke jMatLink.engGetArray(String varName):double[][]
			//
			double[][] value = (double[][])invokeJMatLinkMethod("engGetArray",new Class[] { String.class }, new Object[] { returnVariables[i] } );
			if (value != null){
				matlabFunctionResults.addVariable(returnVariables[i],value);
			}
		}
		return matlabFunctionResults;
	}catch (IOException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 11:33:06 AM)
 * @return jmatlink.JMatLink
 */
private synchronized Object getEngine() throws MatlabException {
	if (jMatLink_objRef == null){
		//
		// load the JMatLink class if not already loaded
		//
		if (jMatLink_class == null){
			try {
				jMatLink_class = Class.forName("jmatlink.JMatLink");
			} catch (ClassNotFoundException e) {
				e.printStackTrace(System.out);
				throw new MatlabException("class jmatlink.JMatLink not found");
			} catch (Throwable e){
				e.printStackTrace(System.out);
				throw new MatlabException("unknown exception: "+e.getMessage());
			}
		}
		//
		// construct a JMatLink object
		//
		try {
			java.lang.reflect.Constructor jMatLink_constructor = jMatLink_class.getConstructor(new Class[0]);
			jMatLink_objRef = jMatLink_constructor.newInstance(new Object[0]);
		}catch (NoSuchMethodException e){
			e.printStackTrace(System.out);
			throw new MatlabException(e.getMessage());
		}catch (IllegalAccessException e){
			e.printStackTrace(System.out);
			throw new MatlabException(e.getMessage());
		}catch (InstantiationException e){
			e.printStackTrace(System.out);
			throw new MatlabException(e.getMessage());
		}catch (java.lang.reflect.InvocationTargetException e){
			e.printStackTrace(System.out);
			throw new MatlabException(e.getMessage());
		}
	}
	return jMatLink_objRef;
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 2:32:41 PM)
 * @return cbit.vcell.opt.matlab.MatlabServices
 */
public static MatlabServices getMatlabServices() throws MatlabException {
	//
	// should use a singleton
	//
	if (matlabServices==null){
		matlabServices = new MatlabServices();
		matlabServices.open();
	}
	return matlabServices;
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 11:35:13 AM)
 */
private Object invokeJMatLinkMethod(String methodName, Class[] formalParameterTypes, Object[] arguments) throws MatlabException {
	getEngine();
	if (jMatLink_objRef==null || jMatLink_class==null){
		throw new RuntimeException("matlab not connected");
	}
	try {
		java.lang.reflect.Method jMatLink_method_engOpen = jMatLink_class.getMethod(methodName,formalParameterTypes);
		return jMatLink_method_engOpen.invoke(jMatLink_objRef,arguments);
	}catch (NoSuchMethodException e){
		e.printStackTrace(System.out);
		throw new MatlabException(e.getMessage());
	}catch (java.lang.reflect.InvocationTargetException e){
		e.printStackTrace(System.out);
		if (e.getTargetException() instanceof UnsatisfiedLinkError){
			UnsatisfiedLinkError err = (UnsatisfiedLinkError)e.getTargetException();
			throw new MatlabException("(2) Matlab connection failure, 'JMatLink.dll' not found or <matlab>\\bin directory not set properly.\n"+err.getMessage());
		}else{
			throw new MatlabException(e.getMessage());
		}
	}catch (IllegalAccessException e){
		e.printStackTrace(System.out);
		throw new MatlabException(e.getMessage());
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new MatlabException("unexpected Matlab exception: "+e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/12/2005 2:22:23 PM)
 * @return boolean
 */
public static boolean isDebug() {
	return bDebug;
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 11:35:13 AM)
 */
public synchronized void open() throws MatlabException {
	if (bOpen){
		return;
	}
	boolean completed = false;
	try {
		//
		// call jMatLink.engOpen()
		//
		invokeJMatLinkMethod("engOpen",new Class[0],new Object[0]);
		//
		// call jMatLink.setDebug(true)
		//
		invokeJMatLinkMethod("setDebug",new Class[] { boolean.class }, new Object[] { new Boolean(bDebug) });

		completed = true;
	}finally{
		bOpen = completed;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/12/2005 2:22:23 PM)
 * @param newBDebug boolean
 */
public static void setDebug(boolean newBDebug) throws MatlabException {
	bDebug = newBDebug;
	if (matlabServices != null && matlabServices.bOpen) {
		matlabServices.invokeJMatLinkMethod("setDebug",new Class[] { boolean.class }, new Object[] { new Boolean(bDebug) });
	}
}
}
