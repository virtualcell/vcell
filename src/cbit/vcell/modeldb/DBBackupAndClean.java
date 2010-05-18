package cbit.vcell.modeldb;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import oracle.jdbc.pool.OracleDataSource;


public class DBBackupAndClean {

	/**
	 * @param args
	 */
	public static final String OP_BACKUP = "backup";
	public static final String OP_CLEAN = "clean";
	
	
	private static class StreamReader extends Thread {
		private InputStream is;
		private StringBuilder stringBuilder;
		private Exception readerException;
		
		public StreamReader(InputStream is) {
			this.is = is;
			stringBuilder = new StringBuilder();
		}

		public synchronized void run() {
			try {
				int c;
				while ((c = is.read()) != -1){
					stringBuilder.append((char)c);
				}
			} catch (Exception e) {
				readerException = e;
			}
		}

		public synchronized String getString() {
			return stringBuilder.toString();
		}
		public synchronized Exception getReaderException(){
			return readerException;
		}
	}

	public static void main(String[] args){
		String action = args[0];
		String[] actionArgs = null;
		if(args.length > 1){
			actionArgs = new String[args.length-1];
			System.arraycopy(args, 1, actionArgs, 0, actionArgs.length);
			
		}
		if(action.equals(OP_BACKUP) && actionArgs != null){
			backup(actionArgs);
		}else if(action.equals(OP_CLEAN) && actionArgs != null){
			clean(actionArgs);
		}
	}

	private static class ProcessInfo{
		public StreamReader normalOutput;
		public StreamReader errorOutput;
		public int returnCode;
		public ProcessInfo(int returnCode,StreamReader normalOutput, StreamReader errorOutput) {
			super();
			this.normalOutput = normalOutput;
			this.errorOutput = errorOutput;
			this.returnCode = returnCode;
		}
	}
	
	private static ProcessInfo spawnProcess(String spawnCommand) throws Exception{
		Process exportProcess = Runtime.getRuntime().exec(spawnCommand);
		//Listen for output
		StreamReader normalOutput = new StreamReader(exportProcess.getInputStream());
		normalOutput.start();
		StreamReader errorOutput = new StreamReader(exportProcess.getErrorStream());
		errorOutput.start();
		//Make sure process finished
		exportProcess.waitFor();
		//Make sure stream readers are finished
		normalOutput.join();
		errorOutput.join();
		
		return new ProcessInfo(exportProcess.exitValue(),normalOutput, errorOutput);

	}
	private static String createExportCommand(String vcellSchema,String password,String dbHostName,String dbSrvcName,File backupFile){
		return "exp "+"system"+"/"+(password==null?"xxxxx":password)+"@"+
		"(description\\=(address\\=(protocol\\=tcp)(host\\="+dbHostName+")(port\\=1521))(connect_data\\=(service_name\\="+dbSrvcName+")))"+
//		" TABLES=(vc_userinfo) "+
		" FILE="+backupFile.getAbsolutePath()
		+" OWNER="+vcellSchema+" CONSISTENT=Y";

	}
	private static void backup(String[] args){
		if(args.length != 6){
			System.out.println(OP_BACKUP+" dbHostName vcellSchema password dbSrvcName workingDir exportDir");
			return;
		}
//		if(args.length != 6){
//			System.out.println(OP_BACKUP+" user/password dbSIDName workingDir exportDir schemaToExport dbServerName");
//			return;
//		}
		String dbHostName = args[0];
		String vcellSchema = args[1];
		String password = args[2];
		String dbSrvcName = args[3];
		File workingDir = new File(args[4]);
		File exportDir = new File(args[5]);

		if(!workingDir.exists()){
			throw new IllegalArgumentException("Working directory"+workingDir.getAbsolutePath()+" does not exist");
		}
		if(!exportDir.exists()){
			throw new IllegalArgumentException("Export directory"+exportDir.getAbsolutePath()+" does not exist");
		}
		String baseFileName = null;
		File backupFile = null;
		try{
			String datePart = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
			baseFileName = dbHostName+"_"+dbSrvcName+"_"+vcellSchema+"_"+datePart;
			backupFile = new File(workingDir,baseFileName+".dmp");
//			String exportCommand =
//				"exp "+credentials+"@"+dbName+" FILE="+backupFile.getAbsolutePath()+" OWNER="+userSchema+" CONSISTENT=Y";
			String exportCommand = createExportCommand(vcellSchema, password, dbHostName, dbSrvcName, backupFile);
//			System.out.println(exportCommand);
			//Create export Process
			ProcessInfo processInfo = spawnProcess(exportCommand);
			//Deal with stream listener errors
			String combinedOutput = combineStreamStrings(processInfo.normalOutput, processInfo.errorOutput);
			if(processInfo.normalOutput.getReaderException() != null || processInfo.errorOutput.getReaderException() != null){
				throw new Exception("Error in script StreamRead:\r\n"+combinedOutput);
			}
			//Finish
			String exportCommandWithoutPassword = createExportCommand(vcellSchema, null, dbHostName, dbSrvcName, backupFile);
			if(processInfo.returnCode == 0){
				spawnProcess("cmd /c MOVE /y "+backupFile+" "+exportDir);
//				Process moveProcess = Runtime.getRuntime().exec("cmd /c MOVE /y "+backupFile+" "+exportDir);
//				moveProcess.waitFor();
//				String combinedOutput = combineStreamStrings(processInfo.normalOutput, processInfo.errorOutput);
				writeFile(workingDir, baseFileName,"Export output:\r\n"+
						exportCommandWithoutPassword+"\r\n"+combinedOutput,false, exportDir);
			}else{
				String delBackupFailMessage = deleteBackup(backupFile);
//				String combinedOutput = combineStreamStrings(processInfo.normalOutput, processInfo.errorOutput);
				writeFile(workingDir, baseFileName,
						"Export Error: (return code="+processInfo.returnCode+")\r\n"+
						(delBackupFailMessage==null?"":delBackupFailMessage+"\r\n")+
						exportCommandWithoutPassword+"\r\n"+combinedOutput,true, exportDir);
			}
		}catch(Exception e){
			String delBackupFailMessage = deleteBackup(backupFile);
			writeFile(workingDir, baseFileName,
				"Script Error:\r\n"+e.getClass().getName()+" "+e.getMessage()+
				(delBackupFailMessage==null?"":"\r\n"+delBackupFailMessage),true, exportDir);
		}

	}
	private static String deleteBackup(File backupFile){
		if(backupFile == null){
			return null;
		}
		try{
			if(backupFile.exists()){
				ProcessInfo processInfo = spawnProcess("cmd /c DEL /q "+backupFile);
				String combinedOutput = combineStreamStrings(processInfo.normalOutput, processInfo.errorOutput);
				if(processInfo.normalOutput.getReaderException() != null || processInfo.errorOutput.getReaderException() != null){
					return "Backup delete failed:\r\n"+combinedOutput;
				}
			}
		}catch(Exception e){
			return e.getClass().getName()+"\r\n"+e.getMessage();
		}
		return null;
	}
	private static String combineStreamStrings(StreamReader readNormalOutput,StreamReader readErrorOutput){
		String combinedOutput = 
			"-----Standard Output-----\r\n"+
			(readNormalOutput.getReaderException()!= null?"StdOut Exception:\r\n"+readNormalOutput.getReaderException().getMessage():"No Exception")+
	    	"\r\n"+
		    (readNormalOutput.getString().length() != 0?"StdOut Text:\r\n"+readNormalOutput.getString():"No Output")+
	    	"\r\n-------------------------------------"+
	    	"\r\n\r\n"+
	    	"-----Standard Error-----\r\n"+
			(readErrorOutput.getReaderException()!= null?"StdErr Exception:\r\n"+readErrorOutput.getReaderException().getMessage():"No Exception")+
	    	"\r\n"+
		    (readErrorOutput.getString().length() != 0?"StdErr Text:\r\n"+readErrorOutput.getString():"No Output")+
	    	"\r\n"+
	    	"-------------------------------------";
		return combinedOutput;
	}
	
//	private static boolean readStream(Process process,boolean bError,StringBuffer sbStream){
//		try {
//			BufferedReader readerStream = null;
//			if(bError){
//				readerStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//			}else{
//				readerStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			}
//			String line;
//			while (true){
//				line = readerStream.readLine();
////				System.out.println(line);
//				if(line == null){
//					break;
//				}
//				sbStream.append((sbStream.length()==0?"":"\n")+line);
////				System.out.println(sbStream.toString());
//			}
//			System.out.println(sbStream.toString());
//			return false;
//		} catch (Exception e) {
//			e.printStackTrace();
//			sbStream.append((sbStream.length()==0?"":"\n")+e.getClass().getName()+" "+e.getMessage());
//			return true;
//		}
//	}
	private static void writeFile(File workingDir,String baseFileName,String fileText,boolean isErrorFile,File exportDir) throws Error{
		try {
			File outFile = new File(workingDir,baseFileName+(isErrorFile?"_error":"_info")+".txt");
			FileOutputStream fos = new FileOutputStream(outFile);
			fos.write(fileText.getBytes());
			fos.close();
			if(exportDir != null){
//				Process transferProcess = null;
				if(isErrorFile){
					spawnProcess("cmd /c COPY /y "+outFile.getAbsolutePath()+" "+exportDir.getAbsolutePath());
//					transferProcess = Runtime.getRuntime().exec("cmd /c COPY /y "+outFile.getAbsolutePath()+" "+exportDir.getAbsolutePath());
				}else{
					spawnProcess("cmd /c MOVE /y "+outFile.getAbsolutePath()+" "+exportDir.getAbsolutePath());
//					transferProcess = Runtime.getRuntime().exec("cmd /c MOVE /y "+outFile.getAbsolutePath()+" "+exportDir.getAbsolutePath());
				}
//				transferProcess.waitFor();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("Error writing Error File:\n:"+e.getMessage(), e);
		}
	}
	
	private static void cleanRemoveUnreferencedSimulations(Connection con, StringBuffer logStringBuffer) throws Exception{
		//
		//Remove Simulations not pointed to by MathModels or BioModels
		//
		logStringBuffer.append("-----Remove Simulations not pointed to by MathModels or BioModels\n");
		final String SIMID = "SIMID";
		final String SIMDATE = "SIMDATE";
		String UNREFERENCED_SIMS_CLAUSE = 
			SimulationTable.table.id.getQualifiedColName()+" IN ("+
			" SELECT "+SimulationTable.table.id.getUnqualifiedColName()+" FROM vc_simulation "+
			" MINUS "+
			" SELECT "+BioModelSimulationLinkTable.table.simRef.getQualifiedColName()+" FROM "+BioModelSimulationLinkTable.table.getTableName()+
			" MINUS "+
			" SELECT "+MathModelSimulationLinkTable.table.simRef.getQualifiedColName()+" FROM "+MathModelSimulationLinkTable.table.getTableName()+
			" MINUS "+
			" SELECT DISTINCT "+SimulationTable.table.versionParentSimRef.getQualifiedColName()+" FROM "+SimulationTable.table.getTableName()+
				" WHERE "+SimulationTable.table.versionParentSimRef.getQualifiedColName()+" IS NOT NULL"+
			")";

		String sql =
			"SELECT "+
				UserTable.table.userid.getQualifiedColName()+","+
				SimulationTable.table.id.getQualifiedColName()+" "+SIMID+","+
				SimulationTable.table.name.getQualifiedColName()+","+
				"TO_CHAR("+SimulationTable.table.versionDate.getQualifiedColName()+",'dd-Mon-yyyy hh24:mi:ss') "+SIMDATE+
			" FROM "+
				SimulationTable.table.getTableName()+","+
				UserTable.table.getTableName()+
			" WHERE "+
				UNREFERENCED_SIMS_CLAUSE +
				" AND "+
				UserTable.table.id.getQualifiedColName()+" = "+SimulationTable.table.ownerRef.getQualifiedColName()+
				" ORDER BY LOWER("+UserTable.table.userid.getQualifiedColName()+")";

		executeQuery(con, sql,logStringBuffer);
		
		sql =
			"DELETE FROM "+SimulationTable.table.getTableName()+
			" WHERE " + UNREFERENCED_SIMS_CLAUSE;

			executeUpdate(con, sql,logStringBuffer);
	}
	
	private static void cleanClearVersionBranchPointRef(Connection con,VersionTable versionTable, StringBuffer logStringBuffer) throws Exception{
		//
		//Clear versionPref (is this needed anymore?)
		//
		String sql =
			"UPDATE "+versionTable.getTableName()+
				" SET "+VersionTable.versionBranchPointRef_ColumnName+" = NULL"+
			" WHERE "+
				VersionTable.versionBranchPointRef_ColumnName+" IS NOT NULL";
		
		executeUpdate(con, sql,logStringBuffer);
	}

	private static void cleanRemoveUnreferencedMathDescriptions(Connection con, StringBuffer logStringBuffer) throws Exception{
		//
		//Remove MathDescriptions not pointed to by SimContexts or MathModels
		//
		logStringBuffer.append("-----Remove MathDescriptions not pointed to by SimContexts or MathModels\n");
		final String MATHID = "MATHID";
		final String MATHDATE = "MATHDATE";
		String UNREFERENCE_MATHDESC_CLAUSE =
			MathDescTable.table.id.getQualifiedColName()+" IN "+
			"("+
			" SELECT "+MathDescTable.table.id.getQualifiedColName()+
				" FROM "+MathDescTable.table.getTableName()+
			" MINUS "+
			" SELECT "+SimContextTable.table.mathRef.getQualifiedColName()+
				" FROM "+SimContextTable.table.getTableName()+
				" WHERE "+SimContextTable.table.mathRef.getQualifiedColName()+" IS NOT NULL"+
			" MINUS "+
			" SELECT "+ MathModelTable.table.mathRef.getQualifiedColName()+
				" FROM "+MathModelTable.table.getTableName()+
			" MINUS "+
			" SELECT "+SimulationTable.table.mathRef.getQualifiedColName()+
				" FROM "+SimulationTable.table.getTableName()+
				" WHERE "+SimulationTable.table.mathRef.getQualifiedColName()+" IS NOT NULL"+
			")";

		String sql =
			"SELECT "+
				UserTable.table.userid.getQualifiedColName()+","+
				MathDescTable.table.id.getQualifiedColName()+" "+MATHID+","+
				MathDescTable.table.name.getQualifiedColName()+","+
				"TO_CHAR("+MathDescTable.table.versionDate.getQualifiedColName()+",'dd-Mon-yyyy hh24:mi:ss') "+MATHDATE+
				" FROM "+
					MathDescTable.table.getTableName()+","+UserTable.table.getTableName()+
				" WHERE "+
					UNREFERENCE_MATHDESC_CLAUSE+
				" AND "+
					UserTable.table.id.getQualifiedColName()+" = "+MathDescTable.table.ownerRef.getQualifiedColName()+
				" ORDER BY LOWER("+UserTable.table.userid.getQualifiedColName()+")";
		
		executeQuery(con, sql,logStringBuffer);
			
			sql =
				"DELETE FROM "+MathDescTable.table.getTableName()+
				" WHERE " + UNREFERENCE_MATHDESC_CLAUSE;
			
			executeUpdate(con, sql,logStringBuffer);
	}

	private static void cleanRemoveUnReferencedNonSpatialGeometries(Connection con, StringBuffer logStringBuffer) throws Exception{
		//
		//Remove non-spatial geometries (dimension==0) that are not pointed to by mathModels or simContexts
		//
		logStringBuffer.append("-----Remove non-spatial geometries (dimension==0) that are not pointed to by mathModels or simContexts\n");
		final String GEOMID = "GEOMID";
		final String GEOMDATE = "GEOMDATE";
		String 	UNREF_NONSPATIAL_GEOM_CLAUSE =
			GeometryTable.table.dimension.getQualifiedColName()+" = 0"+
			" AND "+
			GeometryTable.table.id.getQualifiedColName()+" NOT IN "+
			"("+
			" SELECT "+SimContextTable.table.geometryRef.getQualifiedColName()+" FROM "+SimContextTable.table.getTableName()+
			" UNION "+
			" SELECT "+MathDescTable.table.geometryRef.getQualifiedColName()+" FROM "+MathDescTable.table.getTableName() +
			")";

		String sql =
		"SELECT "+
			UserTable.table.userid.getQualifiedColName()+","+
			GeometryTable.table.id.getQualifiedColName()+" "+GEOMID+","+
			GeometryTable.table.name.getQualifiedColName()+","+
			"TO_CHAR("+GeometryTable.table.versionDate.getQualifiedColName()+",'dd-Mon-yyyy hh24:mi:ss') "+GEOMDATE+
		" FROM "+ 
			GeometryTable.table.getTableName()+","+UserTable.table.getTableName()+
		" WHERE " +
			UNREF_NONSPATIAL_GEOM_CLAUSE+
		" AND "+
			UserTable.table.id.getQualifiedColName()+" = "+GeometryTable.table.ownerRef.getQualifiedColName() +
			" ORDER BY LOWER("+UserTable.table.userid.getQualifiedColName()+")";
			
		executeQuery(con, sql,logStringBuffer);

		sql =
			"DELETE FROM "+GeometryTable.table.getTableName()+
			" WHERE " + UNREF_NONSPATIAL_GEOM_CLAUSE;
		
		executeUpdate(con, sql,logStringBuffer);
	}

	private static void cleanRemoveUnReferencedSimulationContexts(Connection con, StringBuffer logStringBuffer) throws Exception{
		//
		//Remove SimulationContexts that don't have a biomodel
		//
		logStringBuffer.append("-----Remove SimulationContexts that don't have a biomodel\n");
		String 	UNREF_SIMCONTEXT_CLAUSE =
			SimContextTable.table.id.getQualifiedColName()+
			" NOT IN ("+
				"SELECT "+BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+
				" FROM "+BioModelSimContextLinkTable.table.getTableName()+
			")";

		final String SIMCONTID = "SIMCONTID";
		final String SIMCONTDATE = "SIMCONTDATE";
		String sql =
			"SELECT "+
				UserTable.table.userid.getQualifiedColName()+","+
				SimContextTable.table.id.getQualifiedColName()+" "+SIMCONTID+","+
				SimContextTable.table.name.getQualifiedColName()+","+
				"TO_CHAR("+SimContextTable.table.versionDate.getQualifiedColName()+",'dd-Mon-yyyy hh24:mi:ss')"+ SIMCONTDATE+
			" FROM "+
				SimContextTable.table.getTableName()+","+UserTable.table.getTableName()+
			" WHERE "+
				UNREF_SIMCONTEXT_CLAUSE+
			" AND "+
				SimContextTable.table.ownerRef.getQualifiedColName()+" = "+UserTable.table.id.getQualifiedColName()+
			" ORDER BY LOWER("+UserTable.table.userid.getQualifiedColName()+")";
		
		executeQuery(con, sql,logStringBuffer);

		sql =
			"DELETE FROM "+SimContextTable.table.getTableName()+
			" WHERE " + UNREF_SIMCONTEXT_CLAUSE;
		
		executeUpdate(con, sql,logStringBuffer);

	}

	private static void cleanRemoveUnReferencedModels(Connection con, StringBuffer logStringBuffer) throws Exception{
		//
		//Remove Models that are not pointed to by a biomodel
		//and not referenced (erroneously) by a SimulationContext
		//
		logStringBuffer.append("-----Remove Models that are not pointed to by a biomodel and not referenced (erroneously) by a SimulationContext\n");
		String 	UNREF_MODELS_CLAUSE =
			ModelTable.table.id.getQualifiedColName()+" NOT IN "+
			"("+
			" SELECT "+BioModelTable.table.modelRef.getQualifiedColName()+" FROM "+BioModelTable.table.getTableName()+
			" UNION "+
			" SELECT "+SimContextTable.table.modelRef.getQualifiedColName()+" FROM "+SimContextTable.table.getTableName()+
			")";

		final String MODELID = "MODELID";
		final String MODELDATE = "MODELDATE";
		String sql =
			"SELECT DISTINCT "+
				UserTable.table.userid.getQualifiedColName()+","+
				ModelTable.table.id.getQualifiedColName()+" "+ MODELID+","+
				ModelTable.table.name.getQualifiedColName()+","+
				"TO_CHAR("+ModelTable.table.versionDate.getQualifiedColName()+",'dd-Mon-yyyy hh24:mi:ss') "+MODELDATE+
			" FROM "+
				ModelTable.table.getTableName()+","+UserTable.table.getTableName()+
			" WHERE "+
				UNREF_MODELS_CLAUSE+
			" AND "+
				ModelTable.table.ownerRef.getQualifiedColName()+" = "+UserTable.table.id.getQualifiedColName()+
			" ORDER BY LOWER("+UserTable.table.userid.getQualifiedColName()+")";

		executeQuery(con, sql,logStringBuffer);
		
		sql =
			"DELETE FROM "+ModelTable.table.getTableName()+
			" WHERE " + UNREF_MODELS_CLAUSE;
		
		executeUpdate(con, sql,logStringBuffer);
	}

	private static void cleanRemoveUnReferencedSotwareVersions(Connection con, StringBuffer logStringBuffer) throws Exception{
		//
		//Remove Software Versions that are no longer Valid
		//
		logStringBuffer.append("-----Remove Software Versions that are no longer Valid\n");
		String sql =
			"DELETE FROM "+SoftwareVersionTable.table.getTableName()+
			" WHERE "+
			SoftwareVersionTable.table.versionableRef.getQualifiedColName() +" NOT IN ("+
			" SELECT "+BioModelTable.table.id.getQualifiedColName()+" FROM "+BioModelTable.table.getTableName()+
			" UNION "+
			" SELECT "+GeometryTable.table.id.getQualifiedColName()+" FROM "+GeometryTable.table.getTableName()+
			" UNION "+
			" SELECT "+ImageTable.table.id.getQualifiedColName()+" FROM "+ImageTable.table.getTableName()+
			" UNION "+
			" SELECT "+MathDescTable.table.id.getQualifiedColName()+" FROM "+MathDescTable.table.getTableName()+
			" UNION "+
			" SELECT "+MathModelTable.table.id.getQualifiedColName()+" FROM "+MathModelTable.table.getTableName()+
			" UNION "+
			" SELECT "+ModelTable.table.id.getQualifiedColName()+" FROM "+ModelTable.table.getTableName()+
			" UNION "+
			" SELECT "+SimContextTable.table.id.getQualifiedColName()+" FROM "+SimContextTable.table.getTableName()+
			" UNION "+
			" SELECT "+SimulationTable.table.id.getQualifiedColName()+" FROM "+SimulationTable.table.getTableName()+
			")";
		
		executeUpdate(con, sql,logStringBuffer);
	}

	private static void executeUpdate(Connection con, String sql,StringBuffer logStringBuffer)throws Exception{
		Statement stmt = null;
		try{
			logStringBuffer.append(sql+";\n");
			stmt = con.createStatement();
			int updateCount = stmt.executeUpdate(sql);
			logStringBuffer.append("Update count="+updateCount+"\n");
			con.commit();
			logStringBuffer.append("COMMIT\n\n");
		}catch(Exception e){
			logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());
			if(con != null){try{con.rollback();}catch(Exception e2){logStringBuffer.append("\n"+e2.getClass().getName()+"\n"+e2.getMessage());}}
			throw e;
		}finally{
			if(stmt != null){try{stmt.close();}catch(Exception e2){logStringBuffer.append("\n"+e2.getClass().getName()+"\n"+e2.getMessage());}}
		}
	}
	private static void executeQuery(Connection con, String sql,StringBuffer logStringBuffer)throws Exception{
		Statement stmt = null;
		try{
			logStringBuffer.append(sql+";\n");
			stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			ResultSetMetaData rsetMetaData = rset.getMetaData();
			while(rset.next()){
				for (int i = 0; i < rsetMetaData.getColumnCount(); i++) {
					logStringBuffer.append((i==0?"":" ")+rset.getObject(i+1).toString());
				}
				logStringBuffer.append("\n");
			}
			logStringBuffer.append("\n");
			rset.close();
		}catch(Exception e){
			logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());
			if(con != null){try{con.rollback();}catch(Exception e2){logStringBuffer.append("\n"+e2.getClass().getName()+"\n"+e2.getMessage());}}
			throw e;
		}finally{
			if(stmt != null){try{stmt.close();}catch(Exception e2){logStringBuffer.append("\n"+e2.getClass().getName()+"\n"+e2.getMessage());}}
		}
	}

	private static void clean(String[] args) {
		if(args.length != 6){
			System.out.println(OP_CLEAN+" dbHostName vcellSchema password dbSrvcName workingDir exportDir");
			return;
		}
		String dbHostName = args[0];
		String vcellSchema = args[1];
		String password = args[2];
		String dbSrvcName = args[3];
		File workingDir = new File(args[4]);
		File exportDir = new File(args[5]);
		
//		ds = new oracle.jdbc.pool.OracleDataSource(); ds.setURL(myURL); conn = ds.getConnection(user, password); 
//        String driverName = "oracle.jdbc.driver.OracleDriver";
//        String host = args[1];
//        String db = args[2];
//        String connectURL = "jdbc:oracle:thin:@" + host + ":1521:" + db;
//        String dbSchemaUser = args[3];
//        String dbPassword = args[4];

		OracleDataSource oracleDataSource  = null;
		Connection con = null;
		
		String datePart = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
		String baseFileName = dbHostName+"_"+dbSrvcName+"_"+vcellSchema+"_"+datePart;

		StringBuffer logStringBuffer = new StringBuffer();

		try{
			oracleDataSource = new OracleDataSource();
			//jdbc:oracle:<drivertype>:<username/password>@<database>
			//<database> = <host>:<port>:<SID>
			String url = "jdbc:oracle:thin:"+vcellSchema+"/"+password+"@//"+dbHostName+":1521/"+dbSrvcName;
			oracleDataSource.setURL(url);

			con = oracleDataSource.getConnection();
			con.setAutoCommit(false);
			
			cleanRemoveUnreferencedSimulations(con, logStringBuffer);
			cleanClearVersionBranchPointRef(con,SimulationTable.table, logStringBuffer);
			cleanRemoveUnreferencedMathDescriptions(con, logStringBuffer);
			cleanClearVersionBranchPointRef(con,MathDescTable.table, logStringBuffer);
			cleanRemoveUnReferencedNonSpatialGeometries(con, logStringBuffer);
			cleanClearVersionBranchPointRef(con,SimContextTable.table, logStringBuffer);
			cleanRemoveUnReferencedSimulationContexts(con, logStringBuffer);
			cleanRemoveUnReferencedModels(con, logStringBuffer);
			cleanRemoveUnReferencedSotwareVersions(con, logStringBuffer);
			
			writeFile(workingDir, baseFileName, logStringBuffer.toString(), false, exportDir);

		}catch(Exception e){
			writeFile(workingDir, baseFileName, e.getClass().getName()+"\n"+e.getMessage(),true, exportDir);
		}finally{
			if(con != null){
				try{con.close();}catch(Exception e){logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());}
			}
			if(oracleDataSource != null){
				try{oracleDataSource.close();}catch(Exception e){logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());}
			}
		}
		
	}
}

