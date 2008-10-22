package cbit.htc;
/**
 * Insert the type's description here.
 * Creation date: (9/29/2003 10:21:51 AM)
 * @author: Fei Gao
 */
public interface CondorConstants {
	public static final int CONDOR_STATUS_UNEXPANDED = 0; //Never been run
	public static final int CONDOR_STATUS_IDLE = 1;
	public static final int CONDOR_STATUS_RUNNING = 2;
	public static final int CONDOR_STATUS_REMOVED = 3;
	public static final int CONDOR_STATUS_COMPLETED = 4;  	
	public static final int CONDOR_STATUS_ONHOLD = 5;  
	public static final int CONDOR_STATUS_EXITED = 6;  
	public static final int CONDOR_STATUS_UNKNOWN = -1;  

	public static final String[] CONDOR_JOB_STATUS = {"U", "I", "R", "X", "C", "H", "EXITED"};
	
	public final static String JOB_CMD_SUBMIT = "condor_submit";
	public final static String JOB_CMD_DELETE = "condor_rm";
	public final static String JOB_CMD_STATUSDETAILS = "condor_q";
	public final static String JOB_CMD_STATUS = "condor_q";
	//public final static String JOB_CMD_RUNNINGDETAILS = "condor_status -run";
	public final static String JOB_CMD_HISTORY = "condor_history";	
}