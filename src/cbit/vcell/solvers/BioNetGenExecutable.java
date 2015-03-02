package cbit.vcell.solvers;

import java.util.ArrayList;
import java.util.List;

import cbit.vcell.mapping.BioNetGenUpdaterCallback;

public class BioNetGenExecutable extends MathExecutable {

	private transient List<BioNetGenUpdaterCallback> callbacks = null;
	
	public BioNetGenExecutable(String[] command) {
		super(command);
	}
	
	public void inheritCallbacks(List<BioNetGenUpdaterCallback> callbacks) {
		this.callbacks = callbacks;
	}
	private List<BioNetGenUpdaterCallback> getCallbacks() {
		if(callbacks == null) {
			callbacks = new ArrayList<BioNetGenUpdaterCallback>();
		}
		return callbacks;
	}
	
	@Override
	protected void setNewOutputString(String newOutputString) {
		if(newOutputString != null) {
			for(BioNetGenUpdaterCallback callback : getCallbacks()) {
				callback.setNewOutputString(newOutputString);
			}
		}
	}
}
