/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

import java.io.PrintStream;

import cbit.vcell.resource.StdoutSessionLogA;
/**
 * Direct implementation of writing to designed output stream; use object level
 * synchronization for concurrency control
 */
public class StdoutSessionLog extends StdoutSessionLogA {
	

protected final PrintStream out;

/**
 * use System.out default
 * @param userid
 */
public StdoutSessionLog(String userid) {
	this(userid,System.out);
}

public StdoutSessionLog(String userid, PrintStream outStream) {
	super(userid);
	out = outStream;
}

/**
 * use {@link #remoteHostInfo()} 
 */
@Override
protected String hostInfo() {
	return remoteHostInfo();
}

/**
 * print directly to {@link StdoutSessionLogA#out}
 */
@Override
protected synchronized void output(String message) {
	out.append(message);
}

}
