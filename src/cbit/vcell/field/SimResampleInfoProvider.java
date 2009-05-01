package cbit.vcell.field;

import org.vcell.util.VCDataIdentifier;
import org.vcell.util.document.KeyValue;


public interface SimResampleInfoProvider extends VCDataIdentifier{

	boolean isParameterScanType();
	KeyValue getSimulationKey();
	int getJobIndex();
}
