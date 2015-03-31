package cbit.vcell.mapping;

import cbit.vcell.bionetgen.BNGOutputSpec;

public interface BioNetGenUpdaterCallback {
	
	void updateBioNetGenOutput(BNGOutputSpec outputSpec);
	void setNewCallbackMessage(TaskCallbackMessage newCallbackMessage);
	boolean isInterrupted();
}
