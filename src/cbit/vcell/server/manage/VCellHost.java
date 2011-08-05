/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server.manage;

import java.io.*;
/**
 * Insert the type's description here.
 * Creation date: (10/24/2001 6:46:47 PM)
 * @author: Ion Moraru
 */
public class VCellHost {
	public final static int UNKNOWN_SERVER = -1;
	public final static int VCELL_SERVER = 1000;
	public final static int DATA_SERVER = 1001;
	public final static int COMPUTE_SERVER = 1002;
	public final static int DATABASE_SERVER = 1003;
	private int type = 0;
	private String name = null;
	private int port = 0;
	private String startCommand = null;
	private String stopCommand = null;
	private File logFile = null;
	private boolean restartIfDead = false;
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 7:00:22 PM)
 * @param type int
 */
public VCellHost(int type) {
	this.type=type;
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2001 7:14:37 PM)
 * @return java.io.File
 */
public java.io.File getLogFile() {
	return logFile;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:48:51 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:48:51 PM)
 * @return int
 */
public int getPort() {
	return port;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:48:51 PM)
 * @return java.lang.String
 */
public java.lang.String getStartCommand() {
	return startCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:48:51 PM)
 * @return java.lang.String
 */
public java.lang.String getStopCommand() {
	return stopCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:57:37 PM)
 * @return int
 */
public int getType() {
	return type;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:48:51 PM)
 * @return boolean
 */
public boolean isRestartIfDead() {
	return restartIfDead;
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/2001 7:14:37 PM)
 * @param newLogFile java.io.File
 */
void setLogFile(java.io.File newLogFile) {
	logFile = newLogFile;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:54:52 PM)
 * @param newName java.lang.String
 */
void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:54:52 PM)
 * @param newPort int
 */
void setPort(int newPort) {
	port = newPort;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:54:52 PM)
 * @param newRestartIfDead boolean
 */
void setRestartIfDead(boolean newRestartIfDead) {
	restartIfDead = newRestartIfDead;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:54:52 PM)
 * @param newStartCommand java.lang.String
 */
void setStartCommand(java.lang.String newStartCommand) {
	startCommand = newStartCommand;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 6:54:52 PM)
 * @param newStopCommand java.lang.String
 */
void setStopCommand(java.lang.String newStopCommand) {
	stopCommand = newStopCommand;
}
}
