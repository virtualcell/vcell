package org.vcell.util.executable;

/**
 * superclass than manages lable / name
 */
public class PipedProcessHandler {

	protected String name = null;

	public String toString() {
		String s = getClass( ).getName();
		if (name != null) {
			s += " " + name;
		}
		return s;
	}
}
