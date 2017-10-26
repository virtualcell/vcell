package cbit.vcell.server;

import java.io.Serializable;

import org.vcell.util.Matchable;

@SuppressWarnings("serial")
public final class HtcJobID implements Serializable, Matchable {

	public enum BatchSystemType {
		PBS,
		SGE,
		SLURM
	}
	//
	// database name = PBS:1200725.master.cm.cluster
	// jobID = 1200725.master.cm.cluster
	//   or
	// jobID = 1200725
	//
	private long jobNumber;  // required (e.g. 1200725)
	private String server;     // optional (e.g. "master.cm.cluster")
	private final BatchSystemType batchSystemType;

	public HtcJobID(String jobID, BatchSystemType batchSystemType){
		this.batchSystemType = batchSystemType;
		if (jobID.contains(".")){
			int indexOfFirstPeriod = jobID.indexOf(".");
			this.jobNumber = Long.parseLong(jobID.substring(0,indexOfFirstPeriod));
			this.server = jobID.substring(indexOfFirstPeriod+1);
		}else{
			this.jobNumber = Long.parseLong(jobID);
			this.server = null;
		}
	}

	public String toDatabase(){
		BatchSystemType batchSystemType = getBatchSystemType();
		if (server!=null){
			return batchSystemType.name()+":"+this.jobNumber+"."+server;
		}else{
			return batchSystemType.name()+":"+this.jobNumber;
		}
	}

	private String toDatabaseShort(){
		return getBatchSystemType( ).name()+":"+this.jobNumber;
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
	 * compares {@link #jobNumber}, {@link #batchSystemType} and, if both not null, {@link #server}
	 * @return true if equal values
	 */
	@Override
	public boolean compareEqual(Matchable obj) {
		return equals(obj);
	}

	/**
	 * compares {@link #jobNumber} and, if both not null, {@link #server}
	 * @return true if equal values
	 */
	protected boolean sameNumberAndServer(HtcJobID other)  {
		if (jobNumber != other.jobNumber) {
			return false;
		}
		if (server==null || other.server==null){
			return true;
		}
		return server.equals(other.server);
	}

	@Override
	public int hashCode(){
		return toDatabaseShort().hashCode();
	}

}
