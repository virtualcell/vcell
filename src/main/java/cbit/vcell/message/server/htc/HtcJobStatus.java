package cbit.vcell.message.server.htc;

import java.io.Serializable;

import cbit.vcell.message.server.htc.pbs.PBSJobStatus;
import cbit.vcell.message.server.htc.sge.SGEJobStatus;
import cbit.vcell.message.server.htc.slurm.SlurmJobStatus;

public class HtcJobStatus implements Serializable {
	private PBSJobStatus pbsJobStatus = null;
	private SGEJobStatus sgeJobStatus = null;
	private SlurmJobStatus slurmJobStatus = null;

	public HtcJobStatus(PBSJobStatus pbsJobStatus) {
		this.pbsJobStatus = pbsJobStatus;
	}

	public HtcJobStatus(SGEJobStatus sgeJobStatus) {
		this.sgeJobStatus = sgeJobStatus;
	}

	public HtcJobStatus(SlurmJobStatus slurmJobStatus) {
		this.slurmJobStatus = slurmJobStatus;
	}

	public boolean isRunning() {
		if (pbsJobStatus!=null){
			return pbsJobStatus.isRunning();
		}else if (sgeJobStatus!=null){
			return sgeJobStatus.isRunning();
		}else if (slurmJobStatus!=null){
			return slurmJobStatus.isRunning();
		}else{
			throw new RuntimeException("HtcJobStatus, neither pbs nor sge status available");
		}
	}

	public boolean isExiting() {
		if (pbsJobStatus!=null){
			return pbsJobStatus.isExiting();
		}else if (sgeJobStatus!=null){
			return sgeJobStatus.isExiting();
		}else if (slurmJobStatus!=null){
			return slurmJobStatus.isExiting();
		}else{
			throw new RuntimeException("HtcJobStatus, neither pbs nor sge status available");
		}
	}
	
	public String toString(){
		return "HtcJobStatus: isRunning="+isRunning()+", isExiting="+isExiting();
	}

}
