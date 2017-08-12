package cbit.vcell.server.test;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import org.vcell.util.SessionLog;

import cbit.vcell.resource.StdoutSessionLog;
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
