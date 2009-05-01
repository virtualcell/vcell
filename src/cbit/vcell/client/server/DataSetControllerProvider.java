package cbit.vcell.client.server;

import org.vcell.util.DataAccessException;

import cbit.vcell.server.DataSetController;

public interface DataSetControllerProvider {

	DataSetController getDataSetController() throws DataAccessException;
}
