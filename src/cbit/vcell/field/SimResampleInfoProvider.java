package cbit.vcell.field;

import cbit.sql.KeyValue;
import cbit.vcell.server.VCDataIdentifier;

public interface SimResampleInfoProvider extends VCDataIdentifier{

	boolean isParameterScanType();
	KeyValue getSimulationKey();
	int getJobIndex();
}
