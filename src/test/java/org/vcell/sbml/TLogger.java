package org.vcell.sbml;

import cbit.util.xml.VCLogger;

public class TLogger extends VCLogger {

	@Override
	public boolean hasMessages() {
		return false;
	}

	@Override
	public void sendAllMessages() {
	}

	@Override
	public void sendMessage(Priority p, ErrorType et, String message)
			throws Exception {
		System.err.println(p + " " + et + ": "  + message);
	}

}