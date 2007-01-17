package cbit.vcell.lsf;
/**
 * Insert the type's description here.
 * Creation date: (9/29/2003 10:21:51 AM)
 * @author: Fei Gao
 */
public interface LsfConstants {
	public static final int LSF_STATUS_PEND = 0; //Waiting in a queue for scheduling and dispatch
	public static final int LSF_STATUS_RUN = 1;  //Dispatched to a host and running
	public static final int LSF_STATUS_DONE = 2; //Finished normally with zero exit value
	public static final int LSF_STATUS_EXITED = 3; //Finished with non-zero exit value
	public static final int LSF_STATUS_PSUSP = 4;  //Suspended while pending 
	public static final int LSF_STATUS_USUSP = 5;  //Suspended by user 
	public static final int LSF_STATUS_SSUSP = 6;  //Suspended by the LSF system 
	public static final int LSF_STATUS_POST_DONE = 7; //Post-processing completed without errors 
	public static final int LSF_STATUS_POST_ERR = 8; //Post-processing completed with errors
	public static final int LSF_STATUS_WAIT = 9;   //Members of a chunk job that are waiting to run
	public static final int LSF_STATUS_UNKNOWN = 10;   //Members of a chunk job that are waiting to run 

	public static final String[] LSF_JOB_STATUS = {"PEND", "RUN", "DONE", "EXIT", "PSUSP", "USUSP", "SSUSP",
		"POST_DONE", "POST_ERR", "WAIT", "UNKNOWN"};
	
	public static final String LSF_STDOUT_FILE = "LsfStdout";
	public static final String LSF_STDERR_FILE = "LsfStderr";
}