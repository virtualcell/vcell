package cbit.util.admin;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.text.SimpleDateFormat;
import java.util.TimeZone;
/**
 * Insert the type's description here.
 * Creation date: (8/27/01 12:09:34 PM)
 * @author: Jim Schaff
 */
public class LogEvent {
	private String userid = null;
	private String date = null;
	private String clientIP = null;
	private String message = null;

	public final String EVENT_TYPE_SIMULATIONRUN = "simRun";
	public final String EVENT_TYPE_LOGIN		 = "login";
	public final String EVENT_TYPE_SAVE			 = "save";
	public final String EVENT_TYPE_UNKNOWN		 = "unknown";
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",java.util.Locale.US);

	static {
		formatter.setTimeZone(TimeZone.getDefault());
	};

/**
 * Insert the method's description here.
 * Creation date: (8/27/01 1:58:30 PM)
 */
public LogEvent() {}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 12:20:13 PM)
 * @return java.lang.String
 */
public String getClientIP() {
	return clientIP;
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 12:19:53 PM)
 * @return java.util.Date
 */
public java.util.Date getDate() throws java.text.ParseException {
	String dateString = getDateString();

	java.util.Date date = formatter.parse(dateString);
	
	return date;
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 12:19:53 PM)
 * @return java.util.Date
 */
public String getDateString() {
	return date;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2004 12:53:05 PM)
 * @return java.lang.String
 */
public String getEventType() {
	if (isLogin()){
		return EVENT_TYPE_LOGIN;
	}else if (isSave()){
		return EVENT_TYPE_SAVE;
	}else if (isSimulationRun()){
		return EVENT_TYPE_SIMULATIONRUN;
	}else{
		return EVENT_TYPE_UNKNOWN;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 12:19:12 PM)
 * @return java.lang.String
 */
public String getMessage() {
	return message;
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 12:19:33 PM)
 * @return java.lang.String
 */
public String getUserid() {
	return userid;
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 1:02:30 PM)
 * @return boolean
 */
public boolean isAuthentication() {
	return getMessage().startsWith("UserDbDriver.getUserFromUseridAndPassword");
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 1:00:37 PM)
 * @return boolean
 */
public boolean isLogin() {
//	return getMessage().startsWith("LocalVCellBootstrap.getVCellConnection");
	return //getMessage().startsWith("LocalVCellConnection.getUserMetaDbServer") ||
		   getMessage().startsWith("new LocalVCellConnection");
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 1:02:30 PM)
 * @return boolean
 */
public boolean isSave() {
	return getMessage().startsWith("LocalUserMetaDbServer.insert") || 
			getMessage().startsWith("LocalUserMetaDbServer.update") ||
			getMessage().startsWith("LocalUserMetaDbServerMessaging.save");
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 1:02:30 PM)
 * @return boolean
 */
public boolean isSimulationRun() {
	return getMessage().startsWith("LocalMathControllerProxy.startSimulation") 
		|| getMessage().startsWith("MathControllerImpl.startSimulation")
		|| getMessage().startsWith("LocalSimulationController.startSimulation")
		|| getMessage().startsWith("LocalSimulationControllerMessaging.startSimulation");
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 12:17:26 PM)
 * @param line java.lang.String
 */
public void parseLine(String line) throws IllegalArgumentException {

	final int DATE_LENGTH = 28;
	final String LOCALHOST_STRING = "(localhost)";
	final String REMOTE_STRING = "(remote:";
	final int REMOTE_LENGTH = REMOTE_STRING.length();
	//
	// example:
	//
	//  snail (remote:134.192.148.59) Thu Aug 23 16:33:17 EDT 2001 LocalMathControllerProxy.getSimulationIdentifier()
	//
	//  where:
	//		userid		= snail
	//		clientIP	= 134.192.148.59
	//		date		= Thu Aug 23 16:33:17 EDT 2001
	//		message		= LocalMathControllerProxy.getSimulationIdentifier()
	//

	
	//
	// parse ClientIP first
	//
	String ip = null;
	int startIndexIP = -1;
	int endIndexIP = -1;
	if (line.indexOf(LOCALHOST_STRING)>=0){
		startIndexIP = line.indexOf(LOCALHOST_STRING);
		endIndexIP = line.indexOf(")",startIndexIP);
		ip = "localhost";
	}else if (line.indexOf(REMOTE_STRING)>=0){
		startIndexIP = line.indexOf(REMOTE_STRING)+REMOTE_LENGTH;
		endIndexIP = line.indexOf(")",startIndexIP);
		ip = line.substring(startIndexIP,endIndexIP);
	}else{
		throw new IllegalArgumentException("cannot parse IP address");
	}
//	System.out.println("clientIP = \""+ip+"\"");

	//
	// parse userid
	//
	String userName = null;
	int firstSpace = line.indexOf(" ");
	if (firstSpace>0 && firstSpace<startIndexIP){
		userName = line.substring(0,firstSpace);
	}else{
		throw new IllegalArgumentException("cannot parse userid");
	}
//	System.out.println("userid = \""+userName+"\"");	
	
	
	//
	// parse Date
	//
	int endDateIndex = endIndexIP+2+DATE_LENGTH;
	String dateStr = line.substring(endIndexIP+2,endIndexIP+2+DATE_LENGTH);
//	System.out.println("date = \""+dateStr+"\"");	

	//
	// parse Message
	//
	String msg = line.substring(endDateIndex+1);
//	System.out.println("message = \""+message+"\"");

	this.userid = userName;
	this.date = dateStr;
	this.clientIP = ip;
	this.message = msg;
	
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/01 12:58:00 PM)
 * @return java.lang.String
 */
public String toString() {
	return "LogEvent@"+Integer.toHexString(hashCode())+" user="+userid+", message="+message+", date="+date+", ipAddr="+clientIP;
}
}