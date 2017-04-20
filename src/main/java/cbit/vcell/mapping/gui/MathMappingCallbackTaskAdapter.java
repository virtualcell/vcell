package cbit.vcell.mapping.gui;

import org.vcell.util.ClientTaskStatusSupport;

import cbit.vcell.mapping.SimulationContext.MathMappingCallback;

public class MathMappingCallbackTaskAdapter implements MathMappingCallback {
	
	private final ClientTaskStatusSupport clientTaskStatusSupport;
		
	public MathMappingCallbackTaskAdapter(ClientTaskStatusSupport clientTaskStatusSupport){
		this.clientTaskStatusSupport = clientTaskStatusSupport;
	}
	
	@Override
	public void setProgressFraction(float fractionDone) {
		if (clientTaskStatusSupport!=null){
			clientTaskStatusSupport.setProgress((int)(fractionDone*100));
		}
	}
	
	@Override
	public void setMessage(String message) {
		if (clientTaskStatusSupport!=null){
			clientTaskStatusSupport.setMessage(message);
		}
	}
	
	@Override
	public boolean isInterrupted() {
		if (clientTaskStatusSupport!=null){
			return clientTaskStatusSupport.isInterrupted();
		}else{
			return false;
		}
	}

}
