package cbit.gui.graph;

public class LayoutErrorLog {
	
	public static boolean dumpStackActivated = false;
	
	public static void logErrorMessage(String message) {
		System.out.println(message);
		if(dumpStackActivated) {
			Thread.dumpStack();			
		}
	}

}
