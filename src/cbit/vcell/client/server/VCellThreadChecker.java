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
}
