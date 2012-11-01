package cbit.vcell.message.server.htc.pbs;

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