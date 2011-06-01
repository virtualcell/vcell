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

import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
/**
 * Insert the type's description here.
 * Creation date: (3/8/01 3:24:35 PM)
 * @author: Jim Schaff
 */
public class MixedSessionLog implements SessionLog {
	private StdoutSessionLog combinedLog = null;
	private StringSessionLog localLog = null;
/**
 * MixedSessionLog constructor comment.
 * @param userid java.lang.String
 * @param outStream java.io.PrintStream
 */
public MixedSessionLog(StringSessionLog argLocalLog, StdoutSessionLog argCombinedLog) {
	this.combinedLog = argCombinedLog;
	this.localLog = argLocalLog;
}
/**
 * print method comment.
 */
public synchronized void alert(String message) {
	localLog.alert(message);
	combinedLog.alert(message);
}
/**
 * print method comment.
 */
public synchronized void exception(Throwable exception) {
	localLog.exception(exception);
	combinedLog.exception(exception);
}
/**
 * Insert the method's description here.
 * Creation date: (3/8/01 6:00:33 PM)
 * @return cbit.vcell.server.test.StringSessionLog
 */
public StringSessionLog getLocalSessionLog() {
	return localLog;
}
/**
 * print method comment.
 */
public synchronized void print(String message) {
	localLog.print(message);
	combinedLog.print(message);
}
}
