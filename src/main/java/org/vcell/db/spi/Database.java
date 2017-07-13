package org.vcell.db.spi;

import org.scijava.plugin.SciJavaPlugin;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.KeyFactory;
import org.vcell.util.SessionLog;

public interface Database extends SciJavaPlugin {
	
	ConnectionFactory createConnctionFactory(SessionLog sessionLog, String argDriverName, String argConnectURL, String argUserid, String argPassword);

	KeyFactory createKeyFactory();

}
