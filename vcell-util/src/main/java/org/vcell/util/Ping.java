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

/**
 * This type was created in VisualAge.
 */
public class Ping extends Thread {
	private long sleepTimeMillisec;
	private Pingable target;
	private boolean bKilled = false;
/**
 * Ping constructor comment.
 */
public Ping(Pingable target, long waitTimeMillisec, String name) {
	this.target = target;
	this.sleepTimeMillisec = waitTimeMillisec;
	setDaemon(true);
	setName(name);
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/01 9:20:24 AM)
 */
public void kill() {
	bKilled = true;
	target = null;
}
/**
 * This method was created in VisualAge.
 */
public void run() {
	while (!bKilled) {
		try {
			sleep(sleepTimeMillisec);
		} catch (InterruptedException e) {
			// ignore it!
		}
		if (!bKilled){
			try {
				target.ping();
			}catch (Throwable e){
				e.printStackTrace(System.out);
				System.out.println("Ping.run("+getName()+") Exception: "+e.getMessage());
			}
		}
	}
}
}
