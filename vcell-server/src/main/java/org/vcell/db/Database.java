package org.vcell.db;


public interface Database {
	
	String getDriverClassName();
	
	ConnectionFactory createConnctionFactory(String argDriverName, String argConnectURL, String argUserid, String argPassword);

}
