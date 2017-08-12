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
/**
 * Insert the type's description here.
 * Creation date: (10/7/2005 1:30:02 PM)
 * @author: Anuradha Lakshminarayana
 */
public class MatlabServicesTest {
/**
 * Insert the method's description here.
 * Creation date: (10/7/2005 1:30:31 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {

	try {
		File directory = new File("C:\\MATLAB7\\work");
		MatlabServices matlabServices = MatlabServices.getMatlabServices();
		
		MatlabFunctionResults matlabResults = matlabServices.executeCommand("sin([1 2 3 4 5 6 7 8 9])", new String[] {"sine"});
		matlabResults.show();
		
		matlabResults = matlabServices.executeCommand("sin([1 2 3 4 5 6 7 8 9])", null);
		matlabResults.show();
		
		matlabResults = matlabServices.executeCommand("magic(4)", null);
		matlabResults.show();

		matlabResults = matlabServices.executeCommand("magic(4)", new String[] {"m"});
		matlabResults.show();

		matlabServices.setDebug(true);

		matlabResults = matlabServices.executeCommand("abcdefg(4)", new String[] {"m"});
		matlabResults.show();

		matlabResults = matlabServices.executeCommand("clear m", null);
		matlabResults.show();
		matlabResults = matlabServices.executeCommand("abcdefg(4)", new String[] {"m"});
		matlabResults.show();

		String fileText = "function [a, b] = myFunc()\n a = 5; b = 10; end";
		String filename = "myFunc";
		matlabResults = matlabServices.executeFunction(directory, fileText, filename, new String[] {"x", "y"});
		matlabResults.show();

		
	} catch (Throwable e) {
		e.printStackTrace(System.out);
	} finally {
		System.exit(0);
	}
	
}
}
