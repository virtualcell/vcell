package cbit.htc;

public class PBSConstants {
	public static final int PBS_STATUS_UNKNOWN = -1; 			//Not a status in PBS. 
	public static final int PBS_STATUS_JOBARRAYSTARTED = 0; 	//Job arrays only: job array has started
	public static final int PBS_STATUS_EXISTING = 1; 			// Job is exiting after having run
	public static final int PBS_STATUS_HELD = 2; 				// Job is held. A job is put into a held state by the server or by a user or
																// administrator. A job stays in a held state until it is released by a user or
																// administrator.
	public static final int PBS_STATUS_QUEUED = 3; 				//Job is queued, eligible to run or be routed
	public static final int PBS_STATUS_RUNNING = 4; 			//Job is running
	public static final int PBS_STATUS_SUSPENDEDBYSERVER = 5; 	//Job is suspended by server. A job is put into the suspended state when a
																//higher priority job needs the resources.
	public static final int PBS_STATUS_TRANSITING = 6; 			//Job is in transition (being moved to a new location)
	public static final int PBS_STATUS_SUSPENDEDBYUSER = 7; 	//Job is suspended due to workstation becoming busy
	public static final int PBS_STATUS_WAITING = 8; 			//Job is waiting for its requested execution time to be reached or job specified
																//a stage-in request which failed for some reason.
	public static final int PBS_STATUS_SUBJOBFINISHED = 9;		//Subjobs only; subjob is finished (expired.)
	
	public static final String[] PBS_JOB_STATUS = {"B", "E", "H", "Q", "R", "S", "T", "U", "W", "X"};

	public static final int JOB_EXEC_OK = 0; 					//	job exec successful
	public static final int JOB_EXEC_FAIL1 =  -1; 				//	"Job exec failed, before files, no retry"
	public static final int JOB_EXEC_FAIL2 =  -2; 				//	"Job exec failed, after files, no retry"
	public static final int JOB_EXEC_RETRY =  -3; 				//	"Job execution failed, do retry"
	public static final int JOB_EXEC_INITABT =  -4; 			//	Job aborted on MOM initialization
	public static final int JOB_EXEC_INITRST =  -5; 			//	"Job aborted on MOM init, chkpt, no migrate"
	public static final int JOB_EXEC_INITRMG =  -6; 			//	"Job aborted on MOM init, chkpt, ok migrate"
	public static final int JOB_EXEC_BADRESRT =  -7; 			//	Job restart failed
	public static final int JOB_EXEC_GLOBUS_INIT_RETRY  =  -8; 	//	Init. globus job failed. do retry
	public static final int JOB_EXEC_GLOBUS_INIT_FAIL =  -9; 	//	Init. globus job failed. no retry
	public static final int JOB_EXEC_FAILUID =  -10; 			//	invalid uid/gid for job
	public static final int JOB_EXEC_RERUN =  -11; 				//	Job rerun
	public static final int JOB_EXEC_CHKP  =  -12; 				//	Job was checkpointed and killed
	
	public final static String JOB_CMD_SUBMIT = "qsub";
	public final static String JOB_CMD_DELETE = "qdel";
	public final static String JOB_CMD_STATUS = "qstat";	
	public final static String JOB_CMD_HISTORY = "tracejob";
	public final static String SERVER_CMD_STATUS = "qstat -B";
}
