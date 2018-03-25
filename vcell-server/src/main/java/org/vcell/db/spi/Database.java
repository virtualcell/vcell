package org.vcell.db.spi;

import org.scijava.plugin.SciJavaPlugin;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.KeyFactory;

public interface Database extends SciJavaPlugin {
	
	String getDriverClassName();
	
	ConnectionFactory createConnctionFactory(String argDriverName, String argConnectURL, String argUserid, String argPassword);

	KeyFactory createKeyFactory();

}
