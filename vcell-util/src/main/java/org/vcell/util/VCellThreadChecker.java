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

	private static final ThreadLocal<Integer> cpuSuppressed = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};

	private static final ThreadLocal<Integer> remoteSuppressed = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
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
		}else if (guiThreadChecker.isEventDispatchThread() && remoteSuppressed.get( ) == 0) {
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
		}else if (guiThreadChecker.isEventDispatchThread() && cpuSuppressed.get() == 0) {
			System.out.println("!!!!!!!!!!!!!! --calling cpu intensive method from swing thread-----");
			Thread.dumpStack();
		}
	}

	/**
	 * try-with-resources compatible object to increment / decrement suppression count
	 * e.g. try (VCellThreadChecker.SuppressIntensive si = new VCellThreadChecker.SuppressIntensive()) {
	 *  ...
	 *  }
	 */
	public static class SuppressIntensive extends Suppressor {
		public SuppressIntensive() {
			super(cpuSuppressed);
		}
	}
	public static class SuppressRemote extends Suppressor {
		public SuppressRemote() {
			super(remoteSuppressed);
		}
	}
	private static class Suppressor implements AutoCloseable {
		private final ThreadLocal<Integer> counter;

		protected Suppressor(ThreadLocal<Integer> counter) {
			this.counter = counter;
			int oneMore = counter.get( ) + 1;
			counter.set(oneMore);
		}


		@Override
		public void close() {
			int oneLess = counter.get( ) - 1;
			counter.set(oneLess);
		}
	}
}
