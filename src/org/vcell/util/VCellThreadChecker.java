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
	
	private static final ThreadLocal<Boolean> cpuSuppressed = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false; 
		}
	};
	
	public interface GUIThreadChecker {
		public boolean isEventDispatchThread();
	}
	
	private static GUIThreadChecker guiThreadChecker = new GUIThreadChecker() {
		
		public boolean isEventDispatchThread() {
			return SwingUtilities.isEventDispatchThread();
		}
	};
	
	public static void setGUIThreadChecker(GUIThreadChecker argGuiThreadChecker){
		guiThreadChecker = argGuiThreadChecker;
	}
	
	public static void checkRemoteInvocation() {
		if (guiThreadChecker == null){
			System.out.println("!!!!!!!!!!!!!! --VCellThreadChecker.setGUIThreadChecker() not set");
			Thread.dumpStack();
		}else if (guiThreadChecker.isEventDispatchThread()) {
			System.out.println("!!!!!!!!!!!!!! --calling remote method from swing thread-----");
			Thread.dumpStack();
		}
	}
	
	public static void checkSwingInvocation() {
		if (guiThreadChecker == null){
			System.out.println("!!!!!!!!!!!!!! --VCellThreadChecker.setGUIThreadChecker() not set");
			Thread.dumpStack();
		}else if (!guiThreadChecker.isEventDispatchThread()) {
			System.out.println("!!!!!!!!!!!!!! --calling swing from non-swing thread-----");
			Thread.dumpStack();
		}
	}
	
	/**
	 * turn off cpu intensive warning ... should be followed by {@link #resetCpuIntensive()} in finally block
	 */
	public static void suppressCpuIntensive( ) {
		cpuSuppressed.set(true);
	}
	
	/**
	 * restore cpu intensive
	 */
	public static void resetCpuIntensive( ) {
		cpuSuppressed.set(false);
	}
	
	public static void checkCpuIntensiveInvocation() {
		if (guiThreadChecker == null){
			System.out.println("!!!!!!!!!!!!!! --VCellThreadChecker.setGUIThreadChecker() not set");
			Thread.dumpStack();
		}else if (guiThreadChecker.isEventDispatchThread() && !cpuSuppressed.get()) {
			System.out.println("!!!!!!!!!!!!!! --calling cpu intensive method from swing thread-----");
			Thread.dumpStack();
		}
	}
}
