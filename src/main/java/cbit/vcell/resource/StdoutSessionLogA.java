package cbit.vcell.resource;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.rmi.server.RemoteServer;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.StringTokenizer;

import org.vcell.util.SessionLog;

import cbit.vcell.mongodb.VCMongoMessage;

/**
 * StdoutSessionLog (abstract)
 * 
 * Implement standard formatting for messages; delegate delivery to
 * subclasses 
 */
public abstract class StdoutSessionLogA implements SessionLog {
	private static final String LINE_TERMINATOR = System.getProperty("line.separator");

	private final String userid;
	/**
	 * thread local calendar
	 */
	private final ThreadLocal<Calendar> calendar;
	
	/**
	 * log to specified print stream
	 * @param userid not null
	 * @param outStream not null
	 */
	public StdoutSessionLogA(String userid) {
		Objects.requireNonNull(userid);
		this.userid = userid;
		calendar = new ThreadCalendar();
	}
	
	public void alert(String message) {
		String host = hostInfo(); 
		String time = currentTime( ).toString();
		output("<<<ALERT>>> "+userid+" "+host+" "+time+" "+message + LINE_TERMINATOR);
		return;
	}
	

	/**
	 * log exception
	 * @param exception not null
	 */
	public void exception(Throwable exception) {
		Objects.requireNonNull(userid);
		String host = hostInfo(); 
		String time = currentTime( ).toString();
	
		//
		// capture stack in String
		//
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(bos);
		exception.printStackTrace(pw);
		pw.close();
		String stack = bos.toString();
	
		//
		// parse stack string, and prefix each line with identifying info
		//
		StringBuilder buffer = new StringBuilder();
		StringTokenizer tokens = new StringTokenizer(stack,"\n");
		while (tokens.hasMoreTokens()){
			String line = tokens.nextToken();
			buffer.append("<<<EXCEPTION>>> "+userid+" "+host+" "+time+" "+line+LINE_TERMINATOR);
		}
	
		//
		// print to log (stdout)
		//
		output(buffer.toString());
		VCMongoMessage.sendException(stack,exception.getMessage());
		return;
	}

	/**
	 * print method comment.
	 */
	public void print(String message) {
		String host = hostInfo(); 
		String time = currentTime( ).toString();
		output(userid+" "+host+" "+time+" "+message+LINE_TERMINATOR);
	}
	
	/**
	 *  update local calendar to now
	 * @return current time
	 */
	private Date currentTime( ) {
		Calendar c = calendar.get();
		c.setTimeInMillis(System.currentTimeMillis());
		return c.getTime();
	}
	
	/**
	 * @return desired hostinfo String 
	 */
	abstract protected String hostInfo( );
	
	/**
	 * available implementation of {@link #hostInfo()} 
	 * @return "(localhost)" 
	 */
	protected final String localHostInfo( ) {
		return "(localhost)";
	}
	
	/**
	 * available implementation of {@link #hostInfo()} 
	 * get ip of rmi client, if present
	 */
	protected final String remoteHostInfo() {
		try {
			return  "(remote:"+RemoteServer.getClientHost()+")";
		}catch (Exception e){
			return localHostInfo();
		}
	}

	protected static class ThreadCalendar extends ThreadLocal<Calendar> {
		@Override
		protected Calendar initialValue() {
			return Calendar.getInstance();
		}
	}
	
	/**
	 * @param message
	 */
	abstract protected void output(String message);
}
