package cbit.htc;

import java.io.Serializable;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

public class PbsJobID implements Serializable, Matchable {
	private String jobID;
	
	public PbsJobID(String jobID){
		this.jobID = jobID;
	}
	
	public String toString(){
		return getID();
	}
	
	public String getID(){
		return this.jobID;
	}
	
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof PbsJobID){
			PbsJobID other = (PbsJobID)obj;
			if (!Compare.isEqual(jobID,other.jobID)){
				return false;
			}
			return true;
		}
		return false;
	}
}
