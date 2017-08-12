package org.vcell.client.logicalwindow;

public class SomeHeadlessClass {
	
	public static boolean doSomething(InteractionContext ic) {
		return ic.query("Question for you", "Yes or no?") == InteractionContext.Response.YES;
	}
	

}
