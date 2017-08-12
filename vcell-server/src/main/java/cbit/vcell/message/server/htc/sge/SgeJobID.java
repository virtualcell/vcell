package cbit.vcell.message.server.htc.sge;

import cbit.vcell.server.HtcJobID;

@SuppressWarnings("serial")
public class SgeJobID extends HtcJobID {

	public SgeJobID(String jobID){
		super(jobID);
	}

	@Override
	public BatchSystemType getBatchSystemType() {
		return BatchSystemType.SGE;
	}
	@Override
	public boolean equals(Object other) {
		if (other instanceof SgeJobID) {
			return sameNumberAndServer((HtcJobID) other);
		}
		return false;
	}
}
