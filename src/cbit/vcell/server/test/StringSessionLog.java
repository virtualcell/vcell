/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server.test;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
/**
 * Insert the type's description here.
 * Creation date: (3/8/01 5:54:05 PM)
 * @author: Jim Schaff
 */
public class StringSessionLog implements org.vcell.util.SessionLog {
	private java.io.ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	private org.vcell.util.SessionLog log = null;
/**
 * StringSessionLog constructor comment.
 */
public StringSessionLog(String name) {
	java.io.PrintStream logPrintStream = new java.io.PrintStream(byteArrayOutputStream);
	log = new org.vcell.util.StdoutSessionLog(name,logPrintStream);
}
/**
 * This method was created in VisualAge.
 * @param message java.lang.String
 */
public void alert(String message) {
	log.alert(message);
}
/**
 * This method was created in VisualAge.
 * @param e java.lang.Throwable
 */
public void exception(Throwable e) {
	log.exception(e);
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/01 5:58:07 PM)
 * @return java.lang.String
 */
public String getLog() {
	try {
		byteArrayOutputStream.flush();
	}catch (IOException e){
	}
	return byteArrayOutputStream.toString();
}
/**
 * This method was created in VisualAge.
 * @param message java.lang.String
 */
public void print(String message) {
	log.print(message);
}
}
