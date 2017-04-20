package cbit.vcell.message.server.htc;

import java.io.Serializable;

import org.vcell.util.Matchable;

import cbit.vcell.message.server.htc.pbs.PbsJobID;
import cbit.vcell.message.server.htc.sge.SgeJobID;
import cbit.vcell.message.server.htc.slurm.SlurmJobID;

@SuppressWarnings("serial")
public abstract class HtcJobID implements Serializable, Matchable {

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

	protected HtcJobID(String jobID){
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

	public static HtcJobID fromDatabase(String databaseString){
		String PBS_Prefix = BatchSystemType.PBS.name()+":";
		String SGE_Prefix = BatchSystemType.SGE.name()+":";
		String SLURM_Prefix = BatchSystemType.SLURM.name()+":";
		if (databaseString.startsWith(PBS_Prefix)){
			return new PbsJobID(databaseString.substring(PBS_Prefix.length()));
		}else if (databaseString.startsWith(SLURM_Prefix)){
			return new SlurmJobID(databaseString.substring(SLURM_Prefix.length()));
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

	public abstract BatchSystemType getBatchSystemType( );

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
