package org.vcell.optimization;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRun;
import org.vcell.optimization.thrift.OptRunStatus;

import cbit.vcell.resource.ResourceUtil;

public class OptServerImpl {
	
	private Random random = new Random();
	private final Vector<OptRunContext> optRunContexts = new Vector<OptRunContext>();
	private Thread thread = null;
	
	public class OptRunContext {
		final String optimizationId;
		final File optProblemDirectory;
		private OptRunStatus optRunStatus;
		
		public OptRunStatus getStatus(){
			return this.optRunStatus;
		}
		
		public File getOptRunBinaryFile() throws IOException{
			return new File(optProblemDirectory,"optRun.bin");
		}
		
		public OptRun readOptRunBinaryFile() throws IOException{
			File optRunFile = getOptRunBinaryFile();
			if (optRunFile.exists()){
				return CopasiServicePython.readOptRun(optRunFile);
			}else{
				throw new RuntimeException("optRunFile "+optRunFile.getAbsolutePath()+" not found");
			}
		}
		
		public File getOptProblemBinaryFile() throws IOException{
			return new File(optProblemDirectory,"optProblem.bin");
		}
		
		public OptProblem readOptProblem() throws IOException{
			File optProblemFile = getOptProblemBinaryFile();
			if (optProblemFile.exists()){
				return CopasiServicePython.readOptProblem(optProblemFile);
			}else{
				throw new RuntimeException("optRunFile "+optProblemFile.getAbsolutePath()+" not found");
			}
		}
		
		private OptRunContext(String optimizationId, File optProblemDir, OptRunStatus optRunStatus){
			this.optimizationId = optimizationId;
			this.optProblemDirectory = optProblemDir;
			this.optRunStatus = optRunStatus;
		}

		@Override
		public int hashCode() {
			return optimizationId.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof OptRunContext){
				OptRunContext other = (OptRunContext)obj;
				return optimizationId.equals(other.optimizationId);
			}
			return false;
		}
		
	}
	
	public String submit(OptProblem optProblem) throws IOException{
		synchronized (optRunContexts){
			String optimizationId = Integer.toString(random.nextInt(1000000));
			File optDir = new File(ResourceUtil.getOptimizationRootDir(),optimizationId);
			OptRunContext optRunContext = new OptRunContext(optimizationId,optDir,OptRunStatus.Queued);
			CopasiServicePython.writeOptProblem(optRunContext.getOptProblemBinaryFile(), optProblem);
			optRunContexts.add(optRunContext);
			return optimizationId;
		}
	}
	
	public OptRunContext getOptRunContextByOptimizationId(String optimizationId){
		for (OptRunContext context : optRunContexts){
			if (context.optimizationId.equals(optimizationId)){
				return context;
			}
		}
		return null;
	}
	
	public void start(){
		if (thread==null){
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
	
	public void runNextWaitingOptProblem(){
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
