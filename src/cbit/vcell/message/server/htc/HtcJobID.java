package cbit.vcell.message.server.htc;

import java.io.Serializable;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.message.server.htc.pbs.PbsJobID;
import cbit.vcell.message.server.htc.sge.SgeJobID;

public abstract class HtcJobID implements Serializable, Matchable {
	
	public enum BatchSystemType {
		PBS,
		SGE
	}
	private BatchSystemType batchSystemType = null;
	private String jobID;
	
	protected HtcJobID(String jobID, BatchSystemType batchSystemType){
		this.jobID = jobID;
		this.batchSystemType = batchSystemType;
	}
	
	public String toDatabase(){
		return batchSystemType.name()+":"+this.jobID;
	}
	
	public static HtcJobID fromDatabase(String databaseString){
		String PBS_Prefix = BatchSystemType.PBS.name()+":";
		String SGE_Prefix = BatchSystemType.SGE.name()+":";
		if (databaseString.startsWith(PBS_Prefix)){
			return new PbsJobID(databaseString.substring(PBS_Prefix.length()));
		}else if (databaseString.startsWith(SGE_Prefix)){
			return new SgeJobID(databaseString.substring(SGE_Prefix.length()));
		}else {
			return new PbsJobID(databaseString);
		}
	}
	
	public String toString(){
		return toDatabase();
	}
	
	protected String getJobID(){
		return this.jobID;
	}
	
	public BatchSystemType getBatchSystemType(){
		return this.batchSystemType;
	}
	
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof HtcJobID){
			HtcJobID other = (HtcJobID)obj;
			if (!Compare.isEqual(jobID,other.jobID)){
				return false;
			}
			if (batchSystemType != other.batchSystemType){
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof HtcJobID){
			HtcJobID other = (HtcJobID)obj;
			return toDatabase().equals(other.toDatabase());
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return toDatabase().hashCode();
	}
	
}
