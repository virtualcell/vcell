package org.vcell.db.spi;

import org.vcell.db.ConnectionFactory;

public interface Database {
	
	String getDriverClassName();
	
	ConnectionFactory createConnctionFactory(String argDriverName, String argConnectURL, String argUserid, String argPassword);

}
