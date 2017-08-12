package org.vcell.vmicro.workflow.data;

import java.io.FileNotFoundException;

import cbit.vcell.simdata.DataSetControllerImpl;

public interface LocalContext {

	String getDefaultSimDataDirectory();

	DataSetControllerImpl getDataSetControllerImpl() throws FileNotFoundException;

}
