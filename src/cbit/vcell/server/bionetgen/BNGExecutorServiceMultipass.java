package cbit.vcell.server.bionetgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cbit.vcell.mapping.BioNetGenUpdaterCallback;

public class BNGExecutorServiceMultipass implements BNGExecutorService {

	private final Long timeoutDurationMS;
	private final BNGInput bngInput;
	private transient List<BioNetGenUpdaterCallback> callbacks = null;
	private boolean bStopped = false;
	private BNGExecutorServiceNative onepassBngService;
	private long startTime = -1;
	
	BNGExecutorServiceMultipass(BNGInput bngInput, Long timeoutDurationMS) {
		this.bngInput = bngInput;
		this.timeoutDurationMS = timeoutDurationMS;
	}
	
	@Override
	public void registerBngUpdaterCallback(BioNetGenUpdaterCallback callbackOwner) {
		getCallbacks().add(callbackOwner);
	}

	@Override
	public List<BioNetGenUpdaterCallback> getCallbacks() {
		if(callbacks == null) {
			callbacks = new ArrayList<BioNetGenUpdaterCallback>();
		}
		return Collections.unmodifiableList(callbacks);
	}

	@Override
	public BNGOutput executeBNG() throws BNGException {
		this.startTime = System.currentTimeMillis();
//		BNGLParser parser = new BNGLParser(new StringReader(bngInput.getInputString()));
//		ASTModel astModel = parser.Model();
//		"maxIter==>"dldldl,
//		
//		for (int iteration=0; iteration<this.bngInput.getInputString)
		this.onepassBngService = new BNGExecutorServiceNative(bngInput, timeoutDurationMS);
		for (BioNetGenUpdaterCallback callback : getCallbacks()){
			this.onepassBngService.registerBngUpdaterCallback(callback);
		}
		BNGOutput bngOutput = this.onepassBngService.executeBNG();
		this.onepassBngService = null;
//		for (BioNetGenUpdaterCallback callback : getCallbacks()){
//			callback.updateBioNetGenOutput(outputSpec);
//		}
		return bngOutput;
	}

	@Override
	public void stopBNG() throws Exception {
		if (onepassBngService!=null){
			onepassBngService.stopBNG();
		}
		this.bStopped = true;
	}

	@Override
	public boolean isStopped() {
		return bStopped;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

}
