package org.vcell.rest.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;

import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.server.SimpleJobStatusQuerySpec;
import cbit.vcell.server.SimulationJobStatus;

public class AdminService {
	
	private final AdminDBTopLevel adminDbTop;
	private final DatabaseServerImpl dbServerImpl;
	Logger lg = LogManager.getLogger(AdminService.class);

	public AdminService(AdminDBTopLevel adminDbTopLevel, DatabaseServerImpl dbServerImpl) throws java.io.IOException, java.io.FileNotFoundException, org.jdom.JDOMException, javax.jms.JMSException {
		super();
		this.adminDbTop = adminDbTopLevel;
		this.dbServerImpl = dbServerImpl;
	}

	public SimulationJobStatus[] query(SimpleJobStatusQuerySpec querySpec) throws ObjectNotFoundException, DataAccessException {
		SimulationDatabase simDatabase = new SimulationDatabaseDirect(adminDbTop, dbServerImpl, false);
		SimulationJobStatus[] resultList = simDatabase.queryJobs(querySpec);
		return resultList;
	}

}
