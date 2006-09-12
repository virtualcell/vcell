package cbit.util.admin;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
/**
 * Insert the type's description here.
 * Creation date: (8/27/01 12:06:50 PM)
 * @author: Jim Schaff
 */
public class EventSummary {
	private int numLogins = 0;
	private int numAuthentications = 0;
	private int numSaves = 0;
	private int numSimulations = 0;
	private int numUnknownEvents = 0;
/**
 * LogParser constructor comment.
 */
public EventSummary() {
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/01 1:12:31 PM)
 */
public void add(LogEvent logEvent) {
	if (logEvent.isLogin()){
		this.numLogins++;
//		System.out.println("login:["+logEvent.getDateString()+"] user="+logEvent.getUserid());
	}else if (logEvent.isAuthentication()){
		this.numAuthentications++;
	}else if (logEvent.isSimulationRun()){
		this.numSimulations++;
//		System.out.println("simulation:["+logEvent.getDateString()+"] user="+logEvent.getUserid());
	}else if (logEvent.isSave()){
		this.numSaves++;
//		System.out.println("saved:["+logEvent.getDateString()+"] user="+logEvent.getUserid());
	}else{
		this.numUnknownEvents++;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2002 3:01:47 PM)
 * @return int
 */
public int getNumLogins() {
	return numLogins;
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2002 3:01:47 PM)
 * @return int
 */
public int getNumSaves() {
	return numSaves;
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2002 3:01:47 PM)
 * @return int
 */
public int getNumSimulations() {
	return numSimulations;
}
/**
 * Insert the method's description here.
 * Creation date: (8/27/01 1:10:21 PM)
 * @return java.lang.String
 */
public String toString() {
//	return "num logins = "+numLogins+", num authentications = "+numAuthentications+", num simulations = "+numSimulations;
	return "num logins = "+numLogins+", num saves = "+numSaves+", num simulations = "+numSimulations;
}
}
