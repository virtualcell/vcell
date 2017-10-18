package org.vcell.rest.server;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import org.vcell.optimization.CopasiServicePython;
import org.vcell.optimization.OptRunContext;
import org.vcell.optimization.OptService;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRun;
import org.vcell.optimization.thrift.OptRunStatus;

import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.resource.ResourceUtil;

public class OptServerMessaging implements OptService {
	
	private Random random = new Random();
	private final Vector<OptRunContext> optRunContexts = new Vector<OptRunContext>();
	private VCMessagingService vcMessagingService = null;

	public OptServerMessaging(VCMessagingService vcMessagingService){
		this.vcMessagingService = vcMessagingService;
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.vcell.optimization.OptService#submit(org.vcell.optimization.thrift.OptProblem)
	 */
	@Override
	public String submit(OptProblem optProblem) throws IOException{
		synchronized (optRunContexts){
			String optimizationId = Integer.toString(random.nextInt(1000000));
			File optDir = new File(ResourceUtil.getOptimizationRootDir(),optimizationId);
			OptRunContext optRunContext = new OptRunContext(optimizationId,optDir,OptRunStatus.Queued);
			CopasiServicePython.writeOptProblem(optRunContext.getOptProblemBinaryFile(), optProblem);
			optRunContexts.add(optRunContext);
			
			//
			// submit request to message queue
			//
			VCMessageSession producerSession = vcMessagingService.createProducerSession();
			try {
				VCMessage message = producerSession.createObjectMessage("optimization request");
				message.setStringProperty("optProblemFile", optRunContext.getOptProblemBinaryFile().getAbsolutePath());
				message.setStringProperty("optRunFile", optRunContext.getOptRunBinaryFile().getAbsolutePath());
				producerSession.sendQueueMessage(VCellQueue.OptReqQueue, message, true, 30000L);
			} catch (VCMessagingException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to send message to Optimization Service: "+e.getMessage(),e);
			}finally{
				producerSession.close();
			}
			
			return optimizationId;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.vcell.optimization.OptService#getOptRunContextByOptimizationId(java.lang.String)
	 */
	@Override
	public OptRunContext getOptRunContextByOptimizationId(String optimizationId){
		for (OptRunContext optRunContext : optRunContexts){
			if (optRunContext.optimizationId.equals(optimizationId)){
				try {
					OptRun optRun = CopasiServicePython.readOptRun(optRunContext.getOptRunBinaryFile());
					System.out.println("ran optRun id "+optRunContext.optimizationId+": status="+optRun.statusMessage+": ("+optRun.getOptResultSet().toString()+")");
					synchronized (optRunContexts) {
						optRunContext.optRunStatus = OptRunStatus.Complete;
						System.err.println("OptServerImpl - optimizationId "+optRunContext.optimizationId+" status="+OptRunStatus.Complete);
					}
					return optRunContext;
				}catch (Exception e){
					return optRunContext;
				}
			}
		}
		return null;
	}
	
}
