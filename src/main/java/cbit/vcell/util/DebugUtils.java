package cbit.vcell.util;

public class DebugUtils {
	public static void stop(){
		System.err.println("Stopping execution in Eclipse for debugging purposes.  Look at the stack to see who asked me to.");
		//Place breakpoint at next line
		System.err.println("Resuming execution...");
		return;
	}
	
	public static void stop(String message){
		System.err.println("Stopping execution in Eclipse for debugging purposes.  Look at the stack to see who asked me to.");
		System.err.println("Message: "+message);
		//Place breakpoint at next line
		System.err.println("Resuming execution...");
		return;
	}
	
}
