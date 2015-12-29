package cbit.vcell.message.server.htc.sge;

import cbit.vcell.message.server.htc.HtcJobID;

@SuppressWarnings("serial")
public class SgeJobID extends HtcJobID {

	public SgeJobID(String jobID){
		super(jobID,BatchSystemType.SGE);
	}

	public long getSgeJobNumber() {
		return getJobNumber();
	}
}
