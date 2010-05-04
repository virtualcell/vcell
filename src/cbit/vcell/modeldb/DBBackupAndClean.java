package cbit.vcell.modeldb;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DBBackupAndClean {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{

		if(args.length != 6){
			System.out.println("user/password oracleDBNetName workingDir exportDir schemaToExport dbServerName");
			return;
		}
		String credentials = args[0];
		String dbName = args[1];
		File workingDir = new File(args[2]);
		File exportDir = new File(args[3]);
		String userSchema = args[4];
		String dbServerName = args[5];

		String baseFileName = null;
		final StringBuffer sbOut = new StringBuffer();
		final StringBuffer sbErr = new StringBuffer();
		try{
			String datePart = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
			baseFileName = dbServerName+"_"+dbName+"_"+userSchema+"_"+datePart;
			File backupFile = new File(workingDir,baseFileName+".dmp");
			String exportCommand =
				"exp "+credentials+"@"+dbName+" FILE="+backupFile.getAbsolutePath()+" OWNER="+userSchema+" CONSISTENT=Y";
				
			//Create export Process
			final Process exportProcess = Runtime.getRuntime().exec(exportCommand);
			//Listen for output
			final boolean[] outReadFlag = new boolean[1];
			final boolean[] errReadFlag = new boolean[1];
			new Thread(new Runnable() {public void run(){outReadFlag[0] = readStream(exportProcess,false,sbOut);}}).start();
			new Thread(new Runnable() {public void run(){errReadFlag[0] = readStream(exportProcess,true,sbErr);}}).start();
			//Make sure process finished
			exportProcess.waitFor();
			//Deal with stream listener errors
			if(outReadFlag[0] || errReadFlag[0]){
				String combinedOutput = combineStreamStrings(sbOut, sbErr);
				throw new Exception("Error in script StreamRead:\n"+combinedOutput);
			}
			//Finish
			int returnCode = exportProcess.exitValue();
			if(returnCode == 0){
				Process moveProcess = Runtime.getRuntime().exec("cmd /c MOVE /y "+backupFile+" "+exportDir);
				moveProcess.waitFor();
			}else{
				String combinedOutput = combineStreamStrings(sbOut, sbErr);
				writeErrorFile(workingDir, baseFileName, "Export Error: return code="+returnCode+"\n"+combinedOutput, exportDir);
			}
		}catch(Exception e){
			writeErrorFile(workingDir, baseFileName, "Script Error: return\n"+e.getClass().getName()+" "+e.getMessage(), exportDir);
		}
	}

	private static String combineStreamStrings(StringBuffer sbOut,StringBuffer sbErr){
		String combinedOutput = 
		    (sbOut.length() != 0?"StdOut:\n"+sbOut.toString():"")+
	    	(sbOut.length() != 0 && sbErr.length() != 0?"\n":"")+
	    	(sbErr.length() != 0?"StdErr:\n"+sbErr.toString():"");
		return combinedOutput;
	}
	
	private static boolean readStream(Process process,boolean bError,StringBuffer sbStream){
		try {
			BufferedReader readerStream = null;
			if(bError){
				readerStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			}else{
				readerStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
			}
			String line;
			while (true){
				line = readerStream.readLine();
				if(line == null){
					break;
				}
				sbStream.append((sbStream.length()==0?"":"\n")+line);
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			sbStream.append((sbStream.length()==0?"":"\n")+e.getClass().getName()+" "+e.getMessage());
			return true;
		}
	}
	private static void writeErrorFile(File workingDir,String baseFileName,String errText,File exportDir) throws Error{
		try {
			File errorFile = new File(workingDir,baseFileName+"_error"+".txt");
			FileOutputStream fos = new FileOutputStream(errorFile);
			fos.write(errText.getBytes());
			fos.close();
			Process copyProcess = Runtime.getRuntime().exec("cmd /c COPY /y "+errorFile+" "+exportDir.getAbsolutePath());
			copyProcess.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("Error writing Error File:\n:"+e.getMessage(), e);
		}
	}
}
