/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;

import cbit.vcell.resource.ResourceUtil;

/**
 * Insert the type's description here.
 * Creation date: (10/14/2002 5:32:15 PM)
 * @author: Jim Schaff
 */
public class MathExecutableTest {
/**
 * MathExecutableTest constructor comment.
 */
private MathExecutableTest() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/14/2002 5:32:29 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		if (args.length==0){
			System.out.println("usage: MathExecutableTest cmd [arg1] [arg2] ...");
			System.exit(1);
		}
		MathExecutable mathExecutable = new MathExecutable(args, ResourceUtil.getVcellHome());
		mathExecutable.start();
		while (mathExecutable.getStatus().equals(org.vcell.util.exe.ExecutableStatus.RUNNING)){
		}
		System.out.println("finished, return code "+mathExecutable.getExitValue());
		System.out.println("STDOUT");
		System.out.println(mathExecutable.getStdoutString());
		System.out.println("STDERR");
		System.out.println(mathExecutable.getStderrString());
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}
}
