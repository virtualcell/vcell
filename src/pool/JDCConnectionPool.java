package pool;
import java.sql.*;
import java.util.*;
import java.io.*;


public class JDCConnectionPool {

   private Vector connections;
   private String url, user, password;
   final private long timeout=60000;
   private ConnectionReaper reaper;
   final private int poolsize=10;

   public JDCConnectionPool(String url, String user, String password) {
	  this.url = url;
	  this.user = user;
	  this.password = password;
	  connections = new Vector(poolsize);
	  reaper = new ConnectionReaper(this);
	  reaper.setName("JDCConnectionPool reaper");
	  reaper.start();
   }         


   public synchronized void closeConnections() {
        
      Enumeration connlist = connections.elements();

      while((connlist != null) && (connlist.hasMoreElements())) {
          JDCConnection conn = (JDCConnection)connlist.nextElement();
          removeConnection(conn);
      }
   }


   public synchronized Connection getConnection() throws SQLException {

	   JDCConnection c;
	   for(int i = 0; i < connections.size(); i++) {
		   c = (JDCConnection)connections.elementAt(i);
		   if (!c.isFailed() && c.lease()) {
//log.print("JDCConnectionPool.getConnection() using existing connection = "+c);// +" \n"+toString());
			  return c;
		   }
	   }

	   Connection conn = DriverManager.getConnection(url, user, password);
	   conn.setAutoCommit(false);
	   c = new JDCConnection(conn, this);
	   c.lease();
	   connections.addElement(c);
//printToLog("JDCConnectionPool.getConnection() - using newly added connection = "+c); //+"\n"+toString());
	   return c;
  }                  


/**
 * print method comment.
 */
public synchronized void printToLog(String message) {
	String time = Calendar.getInstance().getTime().toString();
	System.out.println(time+": "+message);
	return;
}


public synchronized void reapConnections() {
	
//printToLog("reapConnection() ... STARTING\n" + toString());
	long stale = System.currentTimeMillis() - timeout;
	
	JDCConnection conArray[] = (JDCConnection[])connections.toArray(new JDCConnection[connections.size()]);
	for (int i = 0; conArray!=null && i < conArray.length; i++){
		JDCConnection conn = conArray[i];
		if (conn.isFailed()){
			//
			// flagged as failed by application code
			//
			//printToLog("<<<<ALERT>>>> reapConnection(), removing 'failed' connection "+conn+" from pool (reason='"+conn.getFailureMessage()+"')\n");
			removeConnection(conn);
		}else if (conn.inUse() && (stale > conn.getLastUse())){
			//
			// timed out (probably didn't return the connection)
			//
			//printToLog("<<<<ALERT>>>> reapConnection(), removing 'timed-out' connection "+conn+" from pool\n");
			removeConnection(conn); 
		}else if (!conn.inUse() && (!conn.validate())) {
			//
			// not in use, but not valid, remove
			//
			//printToLog("<<<<ALERT>>>> reapConnection(), removing 'invalid' connection "+conn+" from pool\n");
			removeConnection(conn);
		}
	}
	
//printToLog("reapConnection() ... DONE\n" + toString());
}


private synchronized void removeConnection(JDCConnection conn) {
	connections.removeElement(conn);
//log.print("removedConnection " + conn + " from pool\n" + toString());
}


   public synchronized void returnConnection(JDCConnection conn) {
	  conn.expireLease();
//log.print("JDCConnectionPool.returnConnection() returned connection = "+conn+" to the pool");//"\n"+toString());
   }            


public synchronized void returnFailedConnection(JDCConnection conn) {
	conn.expireLease();
	reapConnections();
//printToLog("<<<<ALERT>>>> JDCConnectionPool.returnFailedConnection() returned connection = " + conn + " to the pool\n" + toString());
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public synchronized String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("JDCConnectionPool(maxSize = "+poolsize+"):\n");
	for (int i = 0;i<connections.size();i++){
		buffer.append("connection("+i+") = "+connections.elementAt(i)+"\n");
	} 
	return buffer.toString();
}
}