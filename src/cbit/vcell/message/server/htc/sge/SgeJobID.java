package cbit.vcell.message.server.htc.sge;

import cbit.vcell.message.server.htc.HtcJobID;

public class SgeJobID extends HtcJobID {
	
	public SgeJobID(String jobID){
		super(jobID,BatchSystemType.SGE);
	}
		
	public String toString(){
		return "<<<DONT CALL TOSTRING()>>>";
	}

	public String getSgeJobID() {
		return getJobID();
	}
}
