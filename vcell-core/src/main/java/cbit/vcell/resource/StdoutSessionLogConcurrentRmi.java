package cbit.vcell.resource;

import java.io.OutputStream;

import org.vcell.util.SessionLog;

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
