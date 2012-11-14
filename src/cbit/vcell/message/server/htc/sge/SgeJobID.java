package cbit.vcell.message.server.htc.sge;

import cbit.vcell.message.server.htc.HtcJobID;

public class SgeJobID extends HtcJobID {
	
	public SgeJobID(String jobID){
		super(jobID,BatchSystemType.SGE);
		int jobIdInteger = Integer.parseInt(jobID); // attempt to temporarily parse the jobID for input validation.
	}
		
	public String getSgeJobID() {
		return getJobID();
	}
}
