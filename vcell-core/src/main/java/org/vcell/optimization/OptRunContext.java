package org.vcell.optimization;

import java.io.File;
import java.io.IOException;

import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRun;
import org.vcell.optimization.thrift.OptRunStatus;

public class OptRunContext {
	public final String optimizationId;
	final File optProblemDirectory;
	public OptRunStatus optRunStatus;
	
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
	
	public OptRunContext(String optimizationId, File optProblemDir, OptRunStatus optRunStatus){
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