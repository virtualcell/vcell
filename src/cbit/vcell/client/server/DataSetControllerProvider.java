package cbit.vcell.client.server;

import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.DataSetController;

public interface DataSetControllerProvider {

	DataSetController getDataSetController() throws DataAccessException;
}
