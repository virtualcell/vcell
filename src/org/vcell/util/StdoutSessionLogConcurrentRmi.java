package org.vcell.util;

import java.io.OutputStream;

/**
 *  Concurrent {@link SessionLog} with RMI style hostname
 */
public class StdoutSessionLogConcurrentRmi extends StdoutSessionLogConcurrent {

	public StdoutSessionLogConcurrentRmi(String userid, OutputStream outStream) {
		super(userid, outStream);
	}

	@Override
	protected String hostInfo() {
		return remoteHostInfo();
	}
}
