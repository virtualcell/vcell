package org.vcell.util;

public class LifeSignThread extends Thread{

private SessionLog sessionLog = null;
private int interval;
/**
 * alive message left in log
 */
public static final String ALIVE_MESSAGE = "Still alive";

public LifeSignThread(SessionLog aSessionLog, int anInterval) {
	super("LifeSignThread");
	this.sessionLog = aSessionLog;
	this.interval = anInterval;
	this.setDaemon(true);
}

public void run(){
	for ( ; ; ) {
		sessionLog.alert(ALIVE_MESSAGE);
		try {
			sleep(interval); 
		} catch (InterruptedException e) {
			sessionLog.alert("Interrupted sleep in LifeSignThread.run()\n"+e.getStackTrace().toString());
			e.printStackTrace();
		}
	}
}
}
