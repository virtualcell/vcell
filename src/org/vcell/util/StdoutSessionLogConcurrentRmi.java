package org.vcell.util;

import java.io.OutputStream;

/**
 *  Concurrent {@link SessionLog} with RMI style hostname
 */
public class StdoutSessionLogConcurrentRmi extends StdoutSessionLogConcurrent {

	public StdoutSessionLogConcurrentRmi(String userid, OutputStream outStream, LifeSignInfo lifeSignInfo) {
		super(userid, outStream, lifeSignInfo);
	}

	@Override
	protected String hostInfo() {
		return remoteHostInfo();
	}
}
