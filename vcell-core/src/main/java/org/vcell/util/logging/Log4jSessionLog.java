package org.vcell.util.logging;

import java.rmi.server.RemoteServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.SessionLog;

/**
 * adapt SessionLog for log4j
 * @author gweatherby
 */
public class Log4jSessionLog implements SessionLog {
	
	private final Logger lg;
	private String host; 

	/**
	 * create logger. User userid as logger name so it appears in messages 
	 * @param userid
	 */
	public Log4jSessionLog(String userid) {
		if (userid == null) {
			userid = "default";
		}
		lg = LogManager.getLogger(userid);
	}
	/**
	 * create logger using existing {@link Logger}
	 * @param lg
	 */
	public Log4jSessionLog(Logger lg) {
		this.lg = lg;
	}
	
	/**
	 * @return underlying log4j logger
	 */
	public Logger getLogger( ) {
		return lg;
	}
	
	/**
	 * lazy evaluate host: determine on first logging -- to avoid startup race conditions
	 * @return host name plus space 
	 */
	private String getHost( ) {
		if (host != null) {
			return host;
		}
		try {
			host = "(remote:"+RemoteServer.getClientHost()+") ";
		}catch (Exception e){
			host = "(localhost) ";
		}
		return host;
	}

	@Override
	public void alert(String message) {
		if (lg.isWarnEnabled())  {
			lg.warn(message); 
		}

	}

	@Override
	public void exception(Throwable e) {
		lg.error(e.getMessage(),e); 
	}

	@Override
	public void print(String message) {
		if (lg.isDebugEnabled()) {
			lg.debug(message);
		}
	}
}
