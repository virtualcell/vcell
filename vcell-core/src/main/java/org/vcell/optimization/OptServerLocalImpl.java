package org.vcell.optimization;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRun;
import org.vcell.optimization.thrift.OptRunStatus;

import cbit.vcell.resource.PythonSupport;
import cbit.vcell.resource.ResourceUtil;

public class OptServerLocalImpl implements OptService {
	
	private Random random = new Random();
	private final Vector<OptRunContext> optRunContexts = new Vector<OptRunContext>();
	private Thread thread = null;
	
	public OptServerLocalImpl() {
	}
	
	/* (non-Javadoc)
	 * @see org.vcell.optimization.OptService#submit(org.vcell.optimization.thrift.OptProblem)
	 */
	@Override
	public String submit(OptProblem optProblem) throws IOException{
		start();
		synchronized (optRunContexts){
			String optimizationId = Integer.toString(random.nextInt(1000000));
			File optDir = new File(ResourceUtil.getOptimizationRootDir(),optimizationId);
			OptRunContext optRunContext = new OptRunContext(optimizationId,optDir,OptRunStatus.Queued);
			CopasiServicePython.writeOptProblem(optRunContext.getOptProblemBinaryFile(), optProblem);
			optRunContexts.add(optRunContext);
			return optimizationId;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.vcell.optimization.OptService#getOptRunContextByOptimizationId(java.lang.String)
	 */
	@Override
	public OptRunContext getOptRunContextByOptimizationId(String optimizationId){
		for (OptRunContext context : optRunContexts){
			if (context.optimizationId.equals(optimizationId)){
				return context;
			}
		}
		return null;
	}
	
	private void start() throws IOException {
		if (thread==null){
			PythonSupport.verifyInstallation();
			Runnable runnable = new Runnable(){
				public void run() {
					while (true){
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
						runNextWaitingOptProblem();
					}
					
				}
			};
			thread = new Thread(runnable,"optimization thread");
			thread.setDaemon(true);
			thread.start();
		}
	}
	
	private void runNextWaitingOptProblem(){
		OptRunContext optRunContext = null;
		synchronized (optRunContexts) {
			for (int i=0;i<optRunContexts.size();i++){
				if (optRunContexts.get(i).optRunStatus == OptRunStatus.Queued){
					optRunContext = optRunContexts.get(i);
					optRunContext.optRunStatus = OptRunStatus.Running;
					System.err.println("OptServerImpl - starting optimizationId "+optRunContext.optimizationId);
					break;
				}
			}
		}
		try {
			if (optRunContext != null){
				OptProblem optProblem = CopasiServicePython.readOptProblem(optRunContext.getOptProblemBinaryFile());
				CopasiServicePython.writeOptProblem(optRunContext.getOptProblemBinaryFile(), optProblem);
				CopasiServicePython.runCopasiPython(optRunContext.getOptProblemBinaryFile(), optRunContext.getOptRunBinaryFile());
				OptRun optRun = CopasiServicePython.readOptRun(optRunContext.getOptRunBinaryFile());
				System.out.println("ran optRun id "+optRunContext.optimizationId+": status="+optRun.statusMessage+": ("+optRun.getOptResultSet().toString()+")");
				synchronized (optRunContexts) {
					optRunContext.optRunStatus = OptRunStatus.Complete;
					System.err.println("OptServerImpl - optimizationId "+optRunContext.optimizationId+" status="+OptRunStatus.Complete);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			synchronized (optRunContexts) {
				optRunContext.optRunStatus = OptRunStatus.Failed;
				System.err.println("OptServerImpl - optimizationId "+optRunContext.optimizationId+" status="+OptRunStatus.Failed);
			}
		}
	}

}
