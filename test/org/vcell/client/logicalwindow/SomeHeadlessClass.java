package org.vcell.client.logicalwindow;

import org.vcell.hlglue.InteractionContext;

public class SomeHeadlessClass {
	
	public static boolean doSomething(InteractionContext ic) {
		return ic.query("Question for you", "Yes or no?") == InteractionContext.Response.YES;
	}
	

}
