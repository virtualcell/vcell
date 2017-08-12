package cbit.vcell.message.server.htc.slurm;

import cbit.vcell.server.HtcJobID;

@SuppressWarnings("serial")
public class SlurmJobID extends HtcJobID {

	public SlurmJobID(String jobID){
		super(jobID);
	}

	@Override
	public BatchSystemType getBatchSystemType() {
		return BatchSystemType.SLURM;
	}
	@Override
	public boolean equals(Object other) {
		if (other instanceof SlurmJobID) {
			return sameNumberAndServer((HtcJobID) other);
		}
		return false;
	}
}
