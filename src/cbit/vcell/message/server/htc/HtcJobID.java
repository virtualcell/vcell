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
	//
	// database name = PBS:1200725.master.cm.cluster
	// jobID = 1200725.master.cm.cluster
	//   or
	// jobID = 1200725
	//
	private BatchSystemType batchSystemType = null;
	private long jobNumber;  // required (e.g. 1200725)
	private String server;     // optional (e.g. "master.cm.cluster")

	protected HtcJobID(String jobID, BatchSystemType batchSystemType){
		if (jobID.contains(".")){
			int indexOfFirstPeriod = jobID.indexOf(".");
			this.jobNumber = Long.parseLong(jobID.substring(0,indexOfFirstPeriod));
			this.server = jobID.substring(indexOfFirstPeriod+1);
		}else{
			this.jobNumber = Long.parseLong(jobID);
			this.server = null;
		}
		this.batchSystemType = batchSystemType;
	}

	public String toDatabase(){
		if (server!=null){
			return batchSystemType.name()+":"+this.jobNumber+"."+server;
		}else{
			return batchSystemType.name()+":"+this.jobNumber;
		}
	}

	private String toDatabaseShort(){
		return batchSystemType.name()+":"+this.jobNumber;
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

	public long getJobNumber(){
		return this.jobNumber;
	}

	public String getServer(){
		return this.server;
	}

	public BatchSystemType getBatchSystemType(){
		return this.batchSystemType;
	}

	/**
	 * broken
	 */
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof HtcJobID){
			HtcJobID other = (HtcJobID)obj;
			if (jobNumber != jobNumber){ //error: comparing identical expressions
				return false;
			}
			if (server!=null && other.server!=null){
				if (!Compare.isEqual(server,other.server)){
					return false;
				}
			}
			if (batchSystemType != other.batchSystemType){
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * broken
	 */
	@Override
	public boolean equals(Object obj){
		if (obj instanceof HtcJobID){
			HtcJobID other = (HtcJobID)obj;
			return compareEqual(other);
		}
		return false;
	}

	@Override
	public int hashCode(){
		return toDatabaseShort().hashCode();
	}

}
