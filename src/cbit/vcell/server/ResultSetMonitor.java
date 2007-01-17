package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class ResultSetMonitor implements Runnable {
	private long sleepTimeMS = 0;
	private cbit.vcell.modeldb.ResultSetCrawler resultSetCrawler = null;
	private SessionLog log = null;
/**
 * ThreadMonitor constructor comment.
 */
public ResultSetMonitor(long argSleepTimeMS, cbit.vcell.modeldb.ResultSetCrawler argResultSetCrawler, SessionLog argSessionLog) {
	super();
	this.sleepTimeMS = argSleepTimeMS;
	this.resultSetCrawler = argResultSetCrawler;
	this.log = argSessionLog;
}
/**
 * This method was created in VisualAge.
 */
public void run() {
	while (true){
		//
		// sleep for a while
		//
		try {
			Thread.sleep(sleepTimeMS);
		}catch (InterruptedException e){
		}

		//
		// scan all users
		//
		try {
			log.print("<<<RESULT SET MONITOR>>> Starting Scan");
			resultSetCrawler.scanAllUsers();			
			log.print("<<<RESULT SET MONITOR>>> Ending Scan");
		}catch (Throwable e){
			log.exception(e);
			log.print("<<<RESULT SET MONITOR>>> Scan aborted with exception "+e.getMessage());
		}finally{
		}

	}	
}
}
