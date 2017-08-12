package cbit.vcell.solver.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.vcell.util.FileUtils;
/*
 * Generating multiple PBS submission files for hybrid multiple trials.
 * Generated .sub filies should be copied to the server directory where the multiple trials run.
 * Each job.sub will call a script called runhybridtest.
 * The auto submission of all these PBS files is in the shell script submission.
 * After executing this java file, jobxxx.sub files and submission script and runhybridtest script will be saved in the working directory.
 */
public class BatchJobSubmissionGenerator {
	public static final String WORK_DIR = "C:\\testHybrid\\batchSubmissionFiles\\";
	public static final String JOB_FILE_BASE_NAME = "job";
    public static final String JOB_FILE_EXT = ".sub";
    public static final String SUBMISSION_SCRIPT_FILE_NAME = "submission";
    public static final String RUNHYBRID_SCRIPT_FILE_NAME = "runhybridtest";
    public static final int TOTAL_NUM_JOB = 3;
    public static final String SERVER_DIR = "/share/apps/vcell/deployed/test/testHybrid/";
    public static final String SCRIPT_NAME = "runhybridtest";
    public static final String ARG_1 = SERVER_DIR + "HybridTest_Calcium_sparks_nonConstRate.vcml";
    public static final String ARG_4 = "U_average:Go_total";
    public static final int TOTAL_NUM_TRIAL = 3;
    
    public static void main(String args[]){
    	//write out PBS jobs
    	for(int j=0; j<TOTAL_NUM_JOB; j++)
		{
			File file = new File(WORK_DIR, JOB_FILE_BASE_NAME + (j+1) + JOB_FILE_EXT);
			PrintWriter pw1 = null;
			try {
				pw1 = new PrintWriter(file);
				pw1.println("#PBS -N hybrid" + (j+1));
				pw1.println("#PBS -m a");
				pw1.println("#PBS -M boris@neuron.uchc.edu");
				pw1.println("#PBS -j oe");
				pw1.println("#PBS -k oe");
				pw1.println("#PBS -r n");
				pw1.println("#PBS -l nice=10");
				String bPrintTime = "true";
				if(j > 0) {
					bPrintTime = "false";
				}
				String cmdLine = SERVER_DIR + SCRIPT_NAME + " " + ARG_1 + " " + (j*(TOTAL_NUM_TRIAL/TOTAL_NUM_JOB)) + " " + (TOTAL_NUM_TRIAL/TOTAL_NUM_JOB) + " " + ARG_4 + " " + bPrintTime; 
				pw1.println(cmdLine);
				pw1.close();	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(pw1 != null) pw1.close();
			}
		}
    	//write out submission script
    	File submissionFile = new File(WORK_DIR, SUBMISSION_SCRIPT_FILE_NAME);
    	PrintWriter pw2 = null;
		try {
			pw2 = new PrintWriter(submissionFile);
			pw2.print("#!/bin/bash\n");
			pw2.print("for i in {1.." + TOTAL_NUM_JOB + "}\n");
			pw2.print("do\n");
			pw2.print("\techo \"submit job $i ---\"\n");
			pw2.print("\tqsub job$i.sub\n");
			pw2.print("\tsleep 40\n");
			pw2.print("done\n");
			pw2.close();	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(pw2 != null) pw2.close();
		}
		//copy runhybridtest script
		File sourceFile = new File(new File(BatchJobSubmissionGenerator.class.getResource("BatchJobSubmissionGenerator.class").getPath()).getParent(),RUNHYBRID_SCRIPT_FILE_NAME);
		File destFile = new File(WORK_DIR, RUNHYBRID_SCRIPT_FILE_NAME);
		try {
			FileUtils.copyFile(sourceFile, destFile);
		} catch (IOException e) {
			System.out.println("Cannot create runhybridtest script. File is located at cbit/vcell/solver/test, please manually copy it to server.");
			e.printStackTrace(System.out);
		}
    }
}
