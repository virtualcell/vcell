package cbit.vcell.message.server;

import org.vcell.util.SessionLog;

public class LifeSignThread extends Thread{

private SessionLog sessionLog = null;
private int interval;



public LifeSignThread(SessionLog aSessionLog, int anInterval) {
	super("LifeSignThread");
	this.sessionLog = aSessionLog;
	this.interval = anInterval;
	this.setDaemon(true);
}

public void run(){
	for ( ; ; ) {
		sessionLog.alert("Still alive");
		try {
			sleep(interval); 
		} catch (InterruptedException e) {
			sessionLog.alert("Interrupted sleep in LifeSignThread.run()\n"+e.getStackTrace().toString());
			e.printStackTrace();
		}
	}
}
}
