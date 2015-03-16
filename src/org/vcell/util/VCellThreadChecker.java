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

import javax.swing.SwingUtilities;

public class VCellThreadChecker {
	/**
	 * master on / off switch. Turn on for debugging
	 */
	private static final boolean ENABLED = false;
	
	private static final ThreadLocal<Integer> cpuSuppressed = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0; 
		}
	};
	
	private interface GUIThreadChecker {
		public boolean isEventDispatchThread();
	}
	
	private static GUIThreadChecker guiThreadChecker = new GUIThreadChecker() {
		
		public boolean isEventDispatchThread() {
			return SwingUtilities.isEventDispatchThread();
		}
	};
	
	public static void checkRemoteInvocation() {
		if (ENABLED) {
			if (guiThreadChecker == null){
				System.out.println("!!!!!!!!!!!!!! --VCellThreadChecker.setGUIThreadChecker() not set");
				Thread.dumpStack();
			}else if (guiThreadChecker.isEventDispatchThread()) {
				System.out.println("!!!!!!!!!!!!!! --calling remote method from swing thread-----");
				Thread.dumpStack();
			}
		}
	}

	public static void checkSwingInvocation() {
		if (ENABLED) {
			if (guiThreadChecker == null){
				System.out.println("!!!!!!!!!!!!!! --VCellThreadChecker.setGUIThreadChecker() not set");
				Thread.dumpStack();
			}else if (!guiThreadChecker.isEventDispatchThread()) {
				System.out.println("!!!!!!!!!!!!!! --calling swing from non-swing thread-----");
				Thread.dumpStack();
			}
		}
	}

	public static void checkCpuIntensiveInvocation() {
		if (ENABLED) {
			if (guiThreadChecker == null){
				System.out.println("!!!!!!!!!!!!!! --VCellThreadChecker.setGUIThreadChecker() not set");
				Thread.dumpStack();
			}else if (guiThreadChecker.isEventDispatchThread() && cpuSuppressed.get() == 0) {
				System.out.println("!!!!!!!!!!!!!! --calling cpu intensive method from swing thread-----");
				Thread.dumpStack();
			}
		}
	}

	/**
	 * try-with-resources compatible object to increment / decrement suppression count
	 * e.g. try (VCellThreadChecker.SuppressIntensive si = new VCellThreadChecker.SuppressIntensive()) {
	 *  ...
	 *  }
	 */
	public static class SuppressIntensive implements AutoCloseable {
		
		public SuppressIntensive() {
			int oneMore = cpuSuppressed.get( ) + 1;
			cpuSuppressed.set(oneMore);
		}

		@Override
		public void close() {
			int oneLess = cpuSuppressed.get( ) - 1;
			cpuSuppressed.set(oneLess);
		}
	}
}
