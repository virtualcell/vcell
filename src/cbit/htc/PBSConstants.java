/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.htc;

import org.vcell.util.document.KeyValue;



public class PBSConstants {
	
	public enum PBSJobStatus {
		Completed("C"),
		Exiting("E"),
		Held("H"),
		Queued("Q"),
		Running("R"),
		Moving("T"),
		Waiting("W"),
		Suspended("S");
		
		private String pbsCommandLetter;
		private PBSJobStatus(String pbsCommandLetter){
			this.pbsCommandLetter = pbsCommandLetter;
		}
		public String getPBSCommandLetter(){
			return pbsCommandLetter;
		}

		public boolean isCompleted() {
			return this.equals(Completed);
		}
		public boolean isExiting() {
			return this.equals(Exiting);
		}
		public boolean isHeld() {
			return this.equals(Held);
		}
		public boolean isQueued() {
			return this.equals(Queued);
		}
		public boolean isRunning() {
			return this.equals(Running);
		}
		public boolean isMoving() {
			return this.equals(Moving);
		}
		public boolean isWaiting() {
			return this.equals(Waiting);
		}
		public boolean isSuspended() {
			return this.equals(Suspended);
		}
		public String getDescription() {
			return name();
		}
		public static PBSJobStatus fromPBSCommandLetter(String pbsCommandLetter) {
			for (PBSJobStatus status : values()){
				if (status.getPBSCommandLetter().equals(pbsCommandLetter)){
					return status;
				}
			}
			return null;
		}
	};

//	static final int PBS_STATUS_UNKNOWN = -1; 			//Not a status in PBS. 
//	static final int PBS_STATUS_JOBARRAYSTARTED = 0; 	//Job arrays only: job array has started
//	static final int PBS_STATUS_EXITING = 1; 			// Job is exiting after having run
//	static final int PBS_STATUS_HELD = 2; 				// Job is held. A job is put into a held state by the server or by a user or
//																// administrator. A job stays in a held state until it is released by a user or
//																// administrator.
//	static final int PBS_STATUS_QUEUED = 3; 				//Job is queued, eligible to run or be routed
//	static final int PBS_STATUS_RUNNING = 4; 			//Job is running
//	static final int PBS_STATUS_SUSPENDEDBYSERVER = 5; 	//Job is suspended by server. A job is put into the suspended state when a
//																//higher priority job needs the resources.
//	static final int PBS_STATUS_TRANSITING = 6; 			//Job is in transition (being moved to a new location)
//	static final int PBS_STATUS_SUSPENDEDBYUSER = 7; 	//Job is suspended due to workstation becoming busy
//	static final int PBS_STATUS_WAITING = 8; 			//Job is waiting for its requested execution time to be reached or job specified
//																//a stage-in request which failed for some reason.
//	static final int PBS_STATUS_SUBJOBFINISHED = 9;		//Subjobs only; subjob is finished (expired.)
//	
//	private static final String[] PBS_JOB_STATUS = {"B", "E", "H", "Q", "R", "S", "T", "U", "W", "X"};

	/*
	The exit value of a job may fall in one of three ranges: X < 0, 0 <=X < 128, X >=128.
	
	X < 0:
	This is a PBS special return value indicating that the job could not be executed. These
	negative values are listed in the table below.
	
	0 <= X < 128 (or 256):
	This is the exit value of the top process in the job, typically the shell. This may be the exit
	value of the last command executed in the shell or the .logout script if the user has such a
	script (csh).
	
	X >= 128 (or 256 depending on the system)
	This means the job was killed with a signal. The signal is given by X modulo 128 (or
	256). For example an exit value of 137 means the job's top process was killed with signal
	9 (137 % 128 = 9).
	 */
	
	public enum PBSJobExitCode {
		OK(0,"job exec successful"),
		FAIL1(-1,"Job exec failed, before files, no retry"),
		FAIL2(-2,"Job exec failed, after files, no retry"),
		RETRY(-3,"Job execution failed, do retry"),
		INITABT(-4,"Job aborted on MOM initialization"),
		INITRST(-5,"Job aborted on MOM init, chkpt, no migrate"),
		INITRMG(-6,"Job aborted on MOM init, chkpt, ok migrate"),
		BADRESRT(-7,"Job restart failed"),
		GLOBUS_INIT_RETRY(-8,"Init. globus job failed. do retry"),
		GLOBUS_INIT_FAIL(-9,"Init. globus job failed. no retry"),
		FAILUID(-10,"invalid uid/gid for job"),
		RERUN(-11,"Job rerun"),
		CHKP(-12,"Job was checkpointed and killed"),
		FAIL_PASSWORD(-13,"Job failed due to a bad password");
	
		private int pbsExitCode;
		private String desc;
		private PBSJobExitCode(int pbsExitCode, String desc){
			this.pbsExitCode = pbsExitCode;
			this.desc = desc;
		}

		public static PBSJobExitCode fromPBSJobExitCode(int retcode) {
			for (PBSJobExitCode exitCode : PBSJobExitCode.values()){
				if (exitCode.getPbsReturnCode() == retcode){
					return exitCode;
				}
			}
			return null;
		}
	
		public int getPbsReturnCode(){
			return pbsExitCode;
		}
		public String getDescription(){
			return desc;
		}
		public boolean isOK() {
			return this.equals(OK);
		}
		public boolean isFAIL1() {
			return this.equals(FAIL1);
		}
		public boolean isFAIL2() {
			return this.equals(FAIL2);
		}
		public boolean isRETRY() {
			return this.equals(RETRY);
		}
		public boolean isINITABT() {
			return this.equals(INITABT);
		}
		public boolean isINITRST() {
			return this.equals(INITRST);
		}
		public boolean isINITRMG() {
			return this.equals(INITRMG);
		}
		public boolean isBADRESRT() {
			return this.equals(BADRESRT);
		}
		public boolean isGLOBUS_INIT_RETRY() {
			return this.equals(GLOBUS_INIT_RETRY);
		}
		public boolean isGLOBUS_INIT_FAIL() {
			return this.equals(GLOBUS_INIT_FAIL);
		}
		public boolean isFAILUID() {
			return this.equals(FAILUID);
		}
		public boolean isRERUN() {
			return this.equals(RERUN);
		}
		public boolean isCHKP() {
			return this.equals(CHKP);
		}
		public boolean isFAIL_PASSWORD() {
			return this.equals(FAIL_PASSWORD);
		}

	}
//	static final int JOB_EXEC_OK = 0; 					//	job exec successful
//	static final int JOB_EXEC_FAIL1 =  -1; 				//	"Job exec failed, before files, no retry"
//	static final int JOB_EXEC_FAIL2 =  -2; 				//	"Job exec failed, after files, no retry"
//	static final int JOB_EXEC_RETRY =  -3; 				//	"Job execution failed, do retry"
//	static final int JOB_EXEC_INITABT =  -4; 			//	Job aborted on MOM initialization
//	static final int JOB_EXEC_INITRST =  -5; 			//	"Job aborted on MOM init, chkpt, no migrate"
//	static final int JOB_EXEC_INITRMG =  -6; 			//	"Job aborted on MOM init, chkpt, ok migrate"
//	static final int JOB_EXEC_BADRESRT =  -7; 			//	Job restart failed
//	static final int JOB_EXEC_GLOBUS_INIT_RETRY  =  -8; 	//	Init. globus job failed. do retry
//	static final int JOB_EXEC_GLOBUS_INIT_FAIL =  -9; 	//	Init. globus job failed. no retry
//	static final int JOB_EXEC_FAILUID =  -10; 			//	invalid uid/gid for job
//	static final int JOB_EXEC_RERUN =  -11; 				//	Job rerun
//	static final int JOB_EXEC_CHKP  =  -12; 				//	Job was checkpointed and killed
//	static final int JOB_EXEC_FAIL_PASSWORD = -13;		// Job failed due to a bad password
//	
//	static final String[] PBS_JOB_EXEC_STATUS = {
//		"job exec successful",
//		"Job exec failed, before files, no retry",
//		"Job exec failed, after files, no retry",
//		"Job execution failed, do retry",
//		"Job aborted on MOM initialization",
//		"Job aborted on MOM init, chkpt, no migrate",
//		"Job aborted on MOM init, chkpt, ok migrate",
//		"Job restart failed",
//		"Init. globus job failed. do retry",
//		"Init. globus job failed. no retry",
//		"invalid uid/gid for job",
//		"Job rerun",
//		"Job was checkpointed and killed",
//		"Job failed due to a bad password"
//	};
	
//	public static final int PBS_SIMULATION_JOB = 1;
//	public static final int PBS_SERVICE_JOB = 2;
	
	public static final String PBS_SERVICE_QUEUE_PREFIX = "serviceq";
	public static final String PBS_WORK_QUEUE_PREFIX = "workq";
	public final static String UNKNOWN_JOB_ID_QSTAT_RESPONSE = "Unknown Job Id";
	public final static String PBS_SIMULATION_JOB_NAME_PREFIX = "S_";

	public static String createPBSSimJobName(KeyValue simKey, int simJobIndex) {
		return PBS_SIMULATION_JOB_NAME_PREFIX+simKey.toString()+"_"+simJobIndex;
	}
	
	public static String QSTAT_FULL_CLUSTER_COMMAND_PATH = "//cm//shared//apps//torque//2.5.5//bin//qstat";
	
	public static final String PBS_JOB_TEMPLATE = 
		    "#PBS -m a\r\n"
			+"#PBS -M schaff@neuron.uchc.edu\r\n"
			+"#PBS -j oe\r\n"
			+"#PBS -k oe\r\n"
			+"#PBS -r n\r\n"
			+"#PBS -l nice=10\r\n"
			+"\r\n"
			+"export PATH=/cm/shared/apps/torque/2.5.5/bin/:$PATH\r\n"
			+"export LD_LIBRARY_PATH=/share/apps/sonic/mq8.5.1:$LD_LIBRARY_PATH\r\n";


	public final static String JOB_CMD_SUBMIT = "/cm/shared/apps/torque/2.5.5/bin/qsub";
	public final static String JOB_CMD_DELETE = "/cm/shared/apps/torque/2.5.5/bin/qdel";
	public final static String JOB_CMD_STATUS = "/cm/shared/apps/torque/2.5.5/bin/qstat";	
	public final static String JOB_CMD_HISTORY = "/cm/shared/apps/torque/2.5.5/bin/tracejob";
	//public final static String SERVER_CMD_STATUS = "qstat";
	public final static String SERVER_CMD_STATUS = "/cm/shared/apps/torque/2.5.5/bin/qstat";
	public final static int PBS_MEM_OVERHEAD_MB = 70;
	
	public enum PBSJobCategory {
		PBS_SIMULATION_JOB,
		PBS_SERVICE_JOB;
	}
}
