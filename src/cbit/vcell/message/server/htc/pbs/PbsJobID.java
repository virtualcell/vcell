package cbit.vcell.message.server.htc.pbs;

import cbit.vcell.message.server.htc.HtcJobID;

@SuppressWarnings("serial")
public class PbsJobID extends HtcJobID {

	public PbsJobID(String jobID){
		super(jobID);
	}

	@Override
	public BatchSystemType getBatchSystemType() {
		return BatchSystemType.PBS;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof PbsJobID) {
			return sameNumberAndServer((HtcJobID) other);
		}
		return false;
	}

}
