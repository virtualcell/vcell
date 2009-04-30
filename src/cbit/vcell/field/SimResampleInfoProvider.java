package cbit.vcell.field;

import org.vcell.util.document.KeyValue;

import cbit.vcell.server.VCDataIdentifier;

public interface SimResampleInfoProvider extends VCDataIdentifier{

	boolean isParameterScanType();
	KeyValue getSimulationKey();
	int getJobIndex();
}
