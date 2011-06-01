/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.server;

import javax.swing.SwingUtilities;

public class VCellThreadChecker {
	public static void checkRemoteInvocation() {
		if (SwingUtilities.isEventDispatchThread()) {
			System.out.println("!!!!!!!!!!!!!! --calling remote method from swing thread-----");
			Thread.dumpStack();
		}
	}
	
	public static void checkSwingInvocation() {
		if (!SwingUtilities.isEventDispatchThread()) {
			System.out.println("!!!!!!!!!!!!!! --calling swing from non-swing thread-----");
			Thread.dumpStack();
		}
	}
	
	public static void checkCpuIntensiveInvocation() {
		if (SwingUtilities.isEventDispatchThread()) {
			System.out.println("!!!!!!!!!!!!!! --calling cpu intensive method from swing thread-----");
			Thread.dumpStack();
		}
	}
}
