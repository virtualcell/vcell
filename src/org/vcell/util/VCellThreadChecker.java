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

import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;

public class VCellThreadChecker {
	
	public interface GUIThreadChecker {
		public boolean isEventDispatchThread();
	}
	
	private static GUIThreadChecker guiThreadChecker = new GUIThreadChecker() {
		
		public boolean isEventDispatchThread() {
			if (GraphicsEnvironment.isHeadless()) {
				return false;
			}
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
	
	public static void checkCpuIntensiveInvocation() {
		if (guiThreadChecker == null){
			System.out.println("!!!!!!!!!!!!!! --VCellThreadChecker.setGUIThreadChecker() not set");
			Thread.dumpStack();
		}else if (guiThreadChecker.isEventDispatchThread()) {
			System.out.println("!!!!!!!!!!!!!! --calling cpu intensive method from swing thread-----");
			Thread.dumpStack();
		}
	}
}
