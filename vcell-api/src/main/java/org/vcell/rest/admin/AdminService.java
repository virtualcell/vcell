package org.vcell.rest.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;

import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;

public class AdminService {
	
	private final AdminDBTopLevel adminDbTop;
	private final DatabaseServerImpl dbServerImpl;
	Logger lg = LoggerFactory.getLogger(AdminService.class);

	public AdminService(AdminDBTopLevel adminDbTopLevel, DatabaseServerImpl dbServerImpl) throws java.io.IOException, java.io.FileNotFoundException, org.jdom.JDOMException, javax.jms.JMSException {
		super();
		this.adminDbTop = adminDbTopLevel;
		this.dbServerImpl = dbServerImpl;
	}

	public SimpleJobStatus[] query(SimpleJobStatusQuerySpec querySpec) throws ObjectNotFoundException, DataAccessException {
		SimulationDatabase simDatabase = new SimulationDatabaseDirect(adminDbTop, dbServerImpl, false);
		SimpleJobStatus[] resultList = simDatabase.getSimpleJobStatus(null,querySpec);
		return resultList;
	}

}
