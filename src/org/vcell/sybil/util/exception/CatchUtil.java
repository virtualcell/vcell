/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.exception;

import org.vcell.sybil.util.keys.KeyOfOne;

/*   CatchUtil  --- by Oliver Ruebenacker, UCHC --- July 2008 to February 2009
 *   A centralized exception and error handling
 */

public class CatchUtil {

	public static class HandleMode extends KeyOfOne<String> {
		public HandleMode(String label) { super(label); }
		public String label() { return a(); }
		public String toString() { return "HandleMode(" + label() + ")"; }
	} 
	
	public static HandleMode RaiseAlert = new HandleMode("RaiseAlert");
	public static HandleMode JustPrint = new HandleMode("JustPrint");
	public static HandleMode RecordSilently = new HandleMode("RecordSilently");
	
	public static interface Handler { 
		public void handle(Throwable t); 
		public void handle(Throwable t, HandleMode mode); 
	}

	public static class DefaultHandler implements Handler {
		public void handle(Throwable t) { handle(t, CatchUtil.RaiseAlert); }
		public void handle(Throwable t, HandleMode mode) { 
			if(RaiseAlert.equals(mode) || JustPrint.equals(mode))
			t.printStackTrace(); 
		}
	}
	
	private static Handler handler = new DefaultHandler();
	
	public static void setHandler(Handler handler) { CatchUtil.handler = handler; }
	public static Handler handler() { return handler; }
	
	public static void handle(Throwable t) { handler.handle(t); }
	public static void handle(Throwable t, HandleMode mode) { handler.handle(t, mode); }
		
}
