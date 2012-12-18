package cbit.vcell.solver.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
/*
 * Generating multiple PBS submission files for hybrid multiple trials.
 * Generated .sub filies should be copied to the server directory where the multiple trials run.
 * The auto submission of all these PBS files is in the shell script submission.
 */
public class BatchJobSubmissionGenerator {
	public static final String FILE_DIR = "D:\\testHybrid\\batchSubmissionFiles\\";
	public static final String FILE_BASE_NAME = "job";
    public static final String FILE_EXT = ".sub";
    public static final int TOTAL_NUM_JOB = 50;
    public static final String SERVER_DIR = "/share/apps/vcell/deployed/test/testHybrid/";
    public static final String SCRIPT_NAME = "runhybridtest";
    public static final String ARG_1 = SERVER_DIR + "HybridTest_Calcium_sparks_nonConstRate.vcml";
    public static final String ARG_4 = "U_average:Go_total";
    public static final int TOTAL_NUM_TRIAL = 20000;
    
    public static void main(String args[]){
    	for(int j=0; j<TOTAL_NUM_JOB; j++)
		{
			File file = new File(FILE_DIR, FILE_BASE_NAME + (j+1) + FILE_EXT);
			PrintWriter pw = null;
			try {
				pw = new PrintWriter(file);
				pw.println("#PBS -N hybrid" + (j+1));
				pw.println("#PBS -m a");
				pw.println("#PBS -M lye@uchc.edu");
				pw.println("#PBS -j oe");
				pw.println("#PBS -k oe");
				pw.println("#PBS -r n");
				pw.println("#PBS -l nice=10");
				String cmdLine = SERVER_DIR + SCRIPT_NAME + " " + ARG_1 + " " + (j*(20000/50)) + " " + (20000/50) + " " + ARG_4; 
				pw.println(cmdLine);
				pw.close();	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(pw != null) pw.close();
			}
		}
    }
}
