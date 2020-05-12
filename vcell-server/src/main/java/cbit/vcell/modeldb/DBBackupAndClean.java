/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.modeldb;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.util.NumberUtils;
import org.vcell.util.document.KeyValue;

import cbit.util.xml.XmlUtil;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.util.AmplistorUtils;


public class DBBackupAndClean {

	private static boolean bCheckIntegrity = false;
	
	/**
	 * @param args
	 */
	public static final String OP_BACKUP = "backup";
	public static final String OP_CLEAN = "clean";
	public static final String OP_DELSIMSDISK = "delsimsdisk";
	public static final String OP_DELSIMSDISK2 = "delsimsdisk2";
	public static final String OP_CLEAN_AND_BACKUP = "cleanandbackup";
	public static final String OP_IMPORT = "import";
	
	
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
		if(args.length <= 1){
			usageExit();
		}
		try {
			PropertyLoader.loadProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		String action = args[0];
		String[] actionArgs = new String[args.length-1];
		System.arraycopy(args, 1, actionArgs, 0, actionArgs.length);

		if(action.equals(OP_BACKUP) && actionArgs != null){
			backup(actionArgs);
		}else if(action.equals(OP_CLEAN) && actionArgs != null){
			clean(actionArgs);
		}else if(action.equals(OP_DELSIMSDISK2) && actionArgs != null){
			try {
				deleteSimsFromDiskMethod2(actionArgs);
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}else if(action.equals(OP_CLEAN_AND_BACKUP) && actionArgs != null){
			clean(actionArgs);
			backup(actionArgs);
		}else if(action.equals(OP_IMPORT) && actionArgs != null){
			importOp(actionArgs);
		}else{
			usageExit();
		}
	}
	
	private static void usageExit(){
		System.out.println(DBBackupAndClean.class.getName()+" "+OP_BACKUP+          " dbHostName vcellSchema password dbSrvcName workingDir exportDir {amplistorUser amplistorPasswd}");
		System.out.println(DBBackupAndClean.class.getName()+" "+OP_CLEAN+           " dbHostName vcellSchema password dbSrvcName workingDir exportDir {amplistorUser amplistorPasswd}");
		System.out.println(DBBackupAndClean.class.getName()+" "+OP_CLEAN_AND_BACKUP+" dbHostName vcellSchema password dbSrvcName workingDir exportDir {amplistorUser amplistorPasswd}");
		System.out.println(DBBackupAndClean.class.getName()+" "+OP_IMPORT+" importServerName dumpFileHostPrefix vcellSchema password importSrvcName workingDir exportDir");
		System.out.println(DBBackupAndClean.class.getName()+" "+OP_DELSIMSDISK2+     " dbHostName vcellSchema password/pwdfile dbSrvcName tmpDir usersDir {thisUserOnly}");
		     System.out.println("     (exmpl: "+DBBackupAndClean.class.getName()+" "+OP_DELSIMSDISK2+" vcell-db vcell vcpassword/pwdfile vcelldborcl.cam.uchc.edu /tmp /share/apps/vcell3/users"+")");
		System.exit(1);
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
	
	private static String createBaseFileName(String dbHostName,String dbSrvcName,String vcellSchema){
		String datePart = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Calendar.getInstance().getTime());
		return dbHostName+"_"+dbSrvcName+"_"+vcellSchema+"_"+datePart;
		
	}
	private static void importOp(String[] args){
		if(args.length != 7){
			usageExit();
		}
		String importServerName = args[0];
		final String dumpFileHostPrefix = args[1];
		String vcellSchema = args[2];
		String password = args[3];
		String importDBSrvcName = args[4];
		File workingDir = new File(args[5]);
		File dumpFilesourceDir = new File(args[6]);

		String baseFileName = OP_IMPORT+"_"+createBaseFileName(importServerName, importDBSrvcName, vcellSchema);;

		StringBuffer logStringBuffer = new StringBuffer();
		ConnectionFactory connectionFactory = null;
		Connection con = null;
		try{
			if(dumpFileHostPrefix.indexOf('.') != -1){
				throw new Exception("Not expecting .(dot) in 'dumpFileHostPrefix'");
			}
			
			if(!workingDir.exists()){
				throw new Exception("Working directory"+workingDir.getAbsolutePath()+" does not exist");
			}
			if(!dumpFilesourceDir.exists()){
				throw new Exception("Export directory"+dumpFilesourceDir.getAbsolutePath()+" does not exist");
			}
//			Executable exec = new Executable(new String[] {"hostname"});
//			exec.start();
//			String localHostName = exec.getStdoutString().trim();
			
//			String inetLocalHost = InetAddress.getLocalHost().getHostName();
//			InetAddress[] inet0 = InetAddress.getAllByName(inetLocalHost);
//			InetAddress[] inet1 = InetAddress.getAllByName(dumpFileHostName);

			File[] oracleDumpFiles = dumpFilesourceDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().startsWith(dumpFileHostPrefix+"_") && pathname.getName().endsWith(".dmp");
				}
			});
			Arrays.sort(oracleDumpFiles, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					if(o1.lastModified() > o2.lastModified()){
						return -1;
					}else if(o1.lastModified() < o2.lastModified()){
						return 1;
					}
					return 0;
				}
			});
			
			if(oracleDumpFiles == null || oracleDumpFiles.length == 0){
				throw new Exception("No dump files found to import");
			}
			
//			for (int i = 0; i < oracleDumpFiles.length; i++) {
//				String datePart = new SimpleDateFormat("MM/dd/yyyy HH_mm_ss").format(oracleDumpFiles[i].lastModified());
//				System.out.println(oracleDumpFiles[i].getName()+" "+oracleDumpFiles[i].lastModified()+" "+datePart);
//			}
			
			File latestDump = oracleDumpFiles[0];
			//sanity check
//			long latestDumpDate = latestDump.lastModified();
//			Calendar.getInstance().getTimeInMillis();
			long timediff = Calendar.getInstance().getTimeInMillis()-latestDump.lastModified();
			if(timediff <= 0){
				throw new Exception("Expecting dump time to be earlier than current time");
			}
			if(timediff > (1000*60*60*48)){
				throw new Exception("Not expecting very old dump");
			}
			
			final String beginWith = dumpFileHostPrefix.toLowerCase()+"_";
			if(!latestDump.getName().toLowerCase().startsWith(beginWith)){
				throw new Exception("Expecting latestdump found name to begin with "+beginWith);
			}
//			dumpCopy = new File(workingDir,oracleDumpFiles[0].getName());
//			if(dumpCopy.exists()){
//				dumpCopy = null;
//				throw new Exception("dumpCopy already exists");
//			}
			System.out.println(/*"localHost="+localHostName+*//*" inetLocalHost="+inetLocalHost+*/"importServerName="+importServerName+" importSrvcName="+importDBSrvcName+" dumpFileHostName="+dumpFileHostPrefix+" source="+latestDump.getAbsolutePath());
			

			//			BeanUtils.copyFileChannel(oracleDumpFiles[0], dumpCopy, true);
			
			//jdbc:oracle:<drivertype>:<username/password>@<database>
			//<database> = <host>:<port>:<SID>
			String url = "jdbc:oracle:thin:"+"system"+"/"+password+"@//"+importServerName+":1521/"+importDBSrvcName;
			System.out.println(url);
			String dbDriverName = PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName);
			connectionFactory = DatabaseService.getInstance().createConnectionFactory(dbDriverName, url, "system", password);

			con = connectionFactory.getConnection(new Object());
			con.setAutoCommit(false);

			boolean bHasVCellUser = false;
			final String USERNAME = "username";
			String sql = "select "+USERNAME+" from dba_users where "+USERNAME+"='VCELL'";
			Statement stmt = null;
			stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			while(rset.next()){
				if(rset.getString(USERNAME).equals("VCELL")){
					bHasVCellUser = true;
					break;
				}
			}
			rset.close();
			stmt.close();
			
			final String HOST_NAME = "host_name";
			sql = "select "+HOST_NAME+" from v$instance";
			stmt = con.createStatement();
			rset = stmt.executeQuery(sql);
			rset.next();
			String host_name = rset.getString(HOST_NAME);
			if(host_name.indexOf('.') != -1){
				throw new Exception("Not expecting .(dot) in host_name");
			}
			if(host_name.toLowerCase().contains("dbs6")){
				throw new Exception("Not expecting import host to be 'dbs6', hardcoded to disallow");
			}
			if(!importServerName.toLowerCase().equals(host_name.toLowerCase())){
				throw new Exception("Expecting 'importServerName'="+importServerName+" to match instance 'host_name'="+host_name);
			}
			System.out.println("dumpFileHostPrefix="+dumpFileHostPrefix+" instance host_name="+host_name);
			if(host_name.toLowerCase().equals(dumpFileHostPrefix.toLowerCase())){
				throw new Exception("Not expecting import host and dumpFileHostPrefix to be same");
			}
			rset.close();
			stmt.close();
						
			if(bHasVCellUser){
				sql = "drop user VCELL cascade";
				logStringBuffer.append(sql+"\n");
				executeUpdate(con, sql, logStringBuffer);
				
				sql = "CREATE USER vcell PROFILE \"DEFAULT\" IDENTIFIED BY \""+password+"\" DEFAULT TABLESPACE \"USERS\" TEMPORARY TABLESPACE \"TEMP\" ACCOUNT UNLOCK";
				executeUpdate(con, sql, logStringBuffer);
				executeUpdate(con, "GRANT UNLIMITED TABLESPACE TO vcell",logStringBuffer);
				executeUpdate(con, "GRANT \"CONNECT\" TO vcell",logStringBuffer);
				executeUpdate(con, "GRANT \"RESOURCE\" TO vcell",logStringBuffer);
				executeUpdate(con, "ALTER USER vcell DEFAULT ROLE  ALL",logStringBuffer);
			}
			
			final String importCommandWithPassword = createImportcommand(vcellSchema, password, importServerName, importDBSrvcName, latestDump);
			System.out.println(importCommandWithPassword);
			ProcessInfo exportProcessInfo = spawnProcess(importCommandWithPassword);
			String combinedExportOutput = combineStreamStrings(exportProcessInfo.normalOutput, exportProcessInfo.errorOutput);
			if(exportProcessInfo.normalOutput.getReaderException() != null || exportProcessInfo.errorOutput.getReaderException() != null){
				throw new Exception("Error in script StreamRead:\r\n"+combinedExportOutput);
			}
			final String exportCommandWithoutPassword = createImportcommand(vcellSchema, null, importServerName, importDBSrvcName, latestDump);
			if(exportProcessInfo.returnCode != 0){
				writeFile(workingDir, baseFileName,
					"Export Error: (return code="+exportProcessInfo.returnCode+")\r\n"+
					exportCommandWithoutPassword+"\r\n"+combinedExportOutput+"\r\nLogInfo\r\n"+logStringBuffer.toString(),true, null);
			}else{
				writeFile(workingDir, baseFileName,"Export output:\r\n"+
					exportCommandWithoutPassword+"\r\n"+combinedExportOutput+"\r\nLogInfo\r\n"+logStringBuffer.toString(),false, null);
			}
		}catch(Exception e){
			e.printStackTrace();
			writeFile(workingDir, baseFileName,"Script Error:\r\n"+e.getClass().getName()+" "+e.getMessage()+"\r\nLogInfo\r\n"+logStringBuffer.toString(),true, null);
		}finally{
			if(con != null){
				try{con.close();}catch(Exception e){logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());}
			}
			if(connectionFactory != null){
				try{connectionFactory.close();}catch(Exception e){logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());}
			}
		}
		
	}
	private static String createExportCommand(String vcellSchema,String password,String dbHostName,String dbSrvcName,File backupFile){
		return "exp "+"system"+"/"+(password==null?"xxxxx":password)+"@"+
		"(description\\=(address\\=(protocol\\=tcp)(host\\="+dbHostName+")(port\\=1521))(connect_data\\=(service_name\\="+dbSrvcName+")))"+
//		" TABLES=("+vcellSchema+".vc_userinfo) "+
		" FILE="+backupFile.getAbsolutePath()
		+" OWNER="+vcellSchema+" CONSISTENT=Y"
		;

	}
	private static String createImportcommand(String vcellSchema,String password,String importServerName,String importDBSrvcName,File latestDump){
		return "imp "+"system"+"/"+(password==null?"xxxxx":password)+"@"+
		"(description\\=(address\\=(protocol\\=tcp)(host\\="+importServerName+")(port\\=1521))(connect_data\\=(service_name\\="+importDBSrvcName+")))"+
//		" TABLES=("+vcellSchema+".vc_userinfo) "+
		" FILE="+latestDump.getAbsolutePath()
		+" FROMUSER="+vcellSchema+" TOUSER="+vcellSchema
		;

	}
	private static class DBBackupHelper{
		String dbHostName;
		String vcellSchema;
		String password;
		String dbSrvcName;
		File workingDir;
		File exportDir;
		String thisUserOnly;
//		AmplistorCredential amplistorCredential;
		
		File localDMPFile = null;
		File remoteZipFile = null;
		File remoteDMPFile = null;
		
		Boolean bExportSucceed = null;
		Boolean bCompressSucceed = null;
		Boolean bRobocopySucceed = null;
		Boolean bAmplistorSucceed = null;

		ArrayList<String> infoList = new ArrayList<String>();
		ArrayList<Exception> errorList = new ArrayList<Exception>();
		
		public DBBackupHelper(String[] args){
			if(args.length != 6 && args.length != 7){
				usageExit();
			}
			dbHostName = args[0];
			vcellSchema = args[1];
			password = args[2];
			dbSrvcName = args[3];
			workingDir = new File(args[4]);
			exportDir = new File(args[5]);
			thisUserOnly = (args.length == 7?args[6]:null);
			
			if((new File(password)).exists()) {//password file passed in
				try {
					byte[] pwdArr = Files.readAllBytes(Paths.get(password));
					password = new String(pwdArr);
				} catch (IOException e) {
					throw new IllegalArgumentException(e.getMessage(), e);
				}
			}
//			if(args.length == 8){
//				amplistorCredential = new AmplistorCredential(args[6], args[7]);
//			}
			
			if(!workingDir.exists()){
				throw new IllegalArgumentException("Working directory"+workingDir.getAbsolutePath()+" does not exist");
			}
			if(!exportDir.exists()){
				throw new IllegalArgumentException("Export directory"+exportDir.getAbsolutePath()+" does not exist");
			}
//			actionBaseFileName = action+"_"+createBaseFileName(dbHostName, dbSrvcName, vcellSchema);;
			localDMPFile = new File(workingDir,createBaseFileName(dbHostName, dbSrvcName, vcellSchema)+".dmp");
			remoteZipFile = new File(exportDir,localDMPFile.getName()+".zip");
			remoteDMPFile =  new File(exportDir,localDMPFile.getName());	
		}
	}
	private static String getAmplistorDBBackupURL(){
		return AmplistorUtils.DEFAULT_AMPLI_VCELL_VCDBBACKUP_URL;
	}
	private static void tryExport(DBBackupHelper dbBackupHelper){
		try{
			String exportCommand = createExportCommand(dbBackupHelper.vcellSchema, dbBackupHelper.password, dbBackupHelper.dbHostName, dbBackupHelper.dbSrvcName, dbBackupHelper.localDMPFile);
			ProcessInfo exportProcessInfo = spawnProcess(exportCommand);
			String combinedOutput = combineStreamStrings(exportProcessInfo.normalOutput, exportProcessInfo.errorOutput);
			if(exportProcessInfo.normalOutput.getReaderException() != null || exportProcessInfo.errorOutput.getReaderException() != null){
				throw new Exception("Error in script StreamRead:\r\n"+combinedOutput);
			}
			if(exportProcessInfo.returnCode == 0){
				dbBackupHelper.bExportSucceed = true;
				dbBackupHelper.infoList.add("-----Export Info-----\r\n"+combinedOutput);
			}else{
				String exportCommandWithoutPassword = createExportCommand(dbBackupHelper.vcellSchema, null, dbBackupHelper.dbHostName, dbBackupHelper.dbSrvcName, dbBackupHelper.localDMPFile);
				throw new Exception("Export Error: (return code="+exportProcessInfo.returnCode+")\r\n"+exportCommandWithoutPassword+"\r\n"+combinedOutput);			
			}
		}catch(Exception e){
			dbBackupHelper.bExportSucceed = false;
			dbBackupHelper.errorList.add(e);
		}
	}
	
	private static void tryCompress(DBBackupHelper dbBackupHelper){
		if(!Boolean.TRUE.equals(dbBackupHelper.bExportSucceed)){
			return;
		}
		BufferedInputStream fis = null;
		ZipOutputStream zos = null;
		try{
			fis = new BufferedInputStream(new FileInputStream(dbBackupHelper.localDMPFile),1048576);
			FileOutputStream fos = new FileOutputStream(dbBackupHelper.remoteZipFile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			zos = new ZipOutputStream(bos);
			ZipEntry ze = new ZipEntry(dbBackupHelper.localDMPFile.getName());
			zos.putNextEntry(ze);
			byte[] tempBuffer = new byte[1048576];
			int numRead = -1;
			while(true){
				if((numRead = fis.read(tempBuffer)) == -1){
					break;
				}
				zos.write(tempBuffer, 0, numRead);
			}
			zos.closeEntry();
			zos.finish();
			zos.close();
			fis.close();
			
			//Now check integrity
			if(bCheckIntegrity){
				long begintime = System.currentTimeMillis();
				checkZipIntegrity(dbBackupHelper.localDMPFile, dbBackupHelper.remoteZipFile);
				dbBackupHelper.infoList.add("Integrity check took "+((System.currentTimeMillis()-begintime)/1000)+" seconds");
			}
			
			dbBackupHelper.bCompressSucceed = true;
			double compressionPercent = 100 *(1.0 - (double)dbBackupHelper.remoteZipFile.length()/(double)dbBackupHelper.localDMPFile.length());
			dbBackupHelper.infoList.add("\r\n-----Compress Info-----\r\n"+"Compress succeeded, ratio="+NumberUtils.formatNumber(compressionPercent, 4));


		}catch(Exception e){
			dbBackupHelper.bCompressSucceed = false;
			dbBackupHelper.errorList.add(e);
		}finally{
			if(zos != null){try{zos.close();}catch(Exception e2){dbBackupHelper.errorList.add(e2);}}
			if(fis != null){try{fis.close();}catch(Exception e2){dbBackupHelper.errorList.add(e2);}}
		}

	}
	
	public static void checkZipIntegrity(File uncompressedFile,File ZipCompressedFile) throws Exception{
		BufferedInputStream fis = null;
		ZipFile zipfile = null;
		try{
			fis = (uncompressedFile == null?null:new BufferedInputStream(new FileInputStream(uncompressedFile),1048576));
			zipfile = new ZipFile(ZipCompressedFile);
			ZipEntry zipEntry = zipfile.entries().nextElement();
			InputStream zis = zipfile.getInputStream(zipEntry);
			int fisInt=  -1;
			int zisInt = -1;
			int uncompressIndex = 0;
			int zipIndex = 0;
			int uncompressEnd = 0;
			int zipEnd = 0;
			byte[] uncompressBuffer = new byte[1048576];
			byte[] zipBuffer = new byte[1048576];
			long totalIn = 0;
			CRC32 crc32 = new CRC32();
			while(true){
				if(fis != null && uncompressIndex == uncompressEnd){
					uncompressIndex = 0;
					uncompressEnd = fis.read(uncompressBuffer);
				}
				if(zipIndex == zipEnd){
					zipIndex = 0;
					zipEnd = zis.read(zipBuffer);
					if(zipEnd != -1){
						crc32.update(zipBuffer, 0, zipEnd);
						if(totalIn/100000000 != (totalIn+zipEnd)/100000000){
							System.out.println((totalIn+zipEnd)/100000000);
						}
						totalIn+= zipEnd;
					}
				}
				if((fis == null || uncompressEnd == -1) && zipEnd == -1){
					if(totalIn != (zipEntry.getSize()==-1?totalIn:zipEntry.getSize()) || crc32.getValue() != (zipEntry.getCrc()==-1?crc32.getValue():zipEntry.getCrc())){
						throw new Exception("size or crc32 doesn't match");
					}
					break;
				}else if(uncompressEnd != -1 && zipEnd != -1){
					zisInt = zipBuffer[zipIndex++];
					fisInt = (fis==null?zisInt:uncompressBuffer[uncompressIndex++]);

					if(fisInt != zisInt){
						throw new Exception("Erro: Zip compress check, uncompressed bytes differ");
					}
				}else{
					throw new Exception("Error: Zip compress check, One stream ended before the other");
				}
			}
		}finally{
			if(fis != null){try{fis.close();}catch(Exception e2){}}
			if(zipfile != null){try{zipfile.close();}catch(Exception e2){}}			
		}
	}
	private static void tryRobocopy(DBBackupHelper dbBackupHelper){
		if(!Boolean.TRUE.equals(dbBackupHelper.bExportSucceed) || Boolean.TRUE.equals(dbBackupHelper.bCompressSucceed)){
			return;
		}
		try{
			ProcessInfo moveProcessInfo = spawnProcess("cmd /c robocopy "+dbBackupHelper.workingDir+" "+dbBackupHelper.exportDir+" "+dbBackupHelper.localDMPFile.getName()+" /mov");
			String combinedOutput = combineStreamStrings(moveProcessInfo.normalOutput, moveProcessInfo.errorOutput);
			if(moveProcessInfo.normalOutput.getReaderException() != null || moveProcessInfo.errorOutput.getReaderException() != null){
				throw new Exception("Error in script StreamRead:\r\n"+combinedOutput);
			}
			if(moveProcessInfo.returnCode == 0){
				dbBackupHelper.bRobocopySucceed = true;
				dbBackupHelper.infoList.add("-----Robocopy Info-----\r\n"+combinedOutput);
			}else{
				throw new Exception("Robocopy Error: (return code="+moveProcessInfo.returnCode+")\r\n"+"\r\n"+combinedOutput);
			}
		}catch(Exception e){
			dbBackupHelper.bRobocopySucceed = false;
			dbBackupHelper.errorList.add(e);
		}

	}
	
	private static void tryAmplistor(DBBackupHelper dbBackupHelper){
		if(!Boolean.TRUE.equals(dbBackupHelper.bExportSucceed)){
			return;
		}
//		if(dbBackupHelper.amplistorCredential == null){
//			return;
//		}
		try{
			if(dbBackupHelper.bCompressSucceed && dbBackupHelper.remoteZipFile.exists()){
				AmplistorUtils.uploadFile(new URL(getAmplistorDBBackupURL()),dbBackupHelper.remoteZipFile, null/*dbBackupHelper.amplistorCredential*/);
			}else if(dbBackupHelper.bRobocopySucceed && dbBackupHelper.remoteDMPFile.exists()){
				AmplistorUtils.uploadFile(new URL(getAmplistorDBBackupURL()),dbBackupHelper.remoteDMPFile, null/*dbBackupHelper.amplistorCredential*/);
			}else if(dbBackupHelper.bExportSucceed && dbBackupHelper.localDMPFile.exists()){
				AmplistorUtils.uploadFile(new URL(getAmplistorDBBackupURL()),dbBackupHelper.localDMPFile, null/*dbBackupHelper.amplistorCredential*/);
			}else{
				throw new Exception(
					"Unkown state \n"+
				"bCompressSucceed="+dbBackupHelper.bCompressSucceed+" reoteCompressExists="+dbBackupHelper.remoteZipFile.exists()+"\n"+
				"bRobocopySucceed="+dbBackupHelper.bRobocopySucceed+" remoteDMPExists="+dbBackupHelper.remoteDMPFile.exists()+"\n"+
				"bExportSucceed="+dbBackupHelper.bExportSucceed+" localExportExists="+dbBackupHelper.localDMPFile.exists()+"\n"
				);
			}
			dbBackupHelper.bAmplistorSucceed = true;
			dbBackupHelper.infoList.add("Amplistor xfer succeeded");
		}catch(Exception e){
			dbBackupHelper.bAmplistorSucceed = false;
			dbBackupHelper.errorList.add(e);
		}

	}
	
	private static void tryCleanup(DBBackupHelper dbBackupHelper){
		String errorStr = null;
		if((errorStr = deleteBackup(dbBackupHelper.localDMPFile)) != null){dbBackupHelper.errorList.add(new Exception(errorStr));}
		if(Boolean.FALSE.equals(dbBackupHelper.bCompressSucceed)){
			if((errorStr = deleteBackup(dbBackupHelper.remoteZipFile)) != null){dbBackupHelper.errorList.add(new Exception(errorStr));}
		}if(Boolean.FALSE.equals(dbBackupHelper.bRobocopySucceed)){
			if((errorStr = deleteBackup(dbBackupHelper.remoteDMPFile)) != null){dbBackupHelper.errorList.add(new Exception(errorStr));}
		}
		
		String baseFileName = OP_BACKUP+"_"+createBaseFileName(dbBackupHelper.dbHostName, dbBackupHelper.dbSrvcName, dbBackupHelper.vcellSchema);

		StringBuffer infoSB = new StringBuffer();
		for(String str:dbBackupHelper.infoList){
			infoSB.append(str+"\r\n");
		}
		if(infoSB.length() != 0){
			try{
				writeFile(dbBackupHelper.workingDir, baseFileName, infoSB.toString(), false, dbBackupHelper.exportDir);
				AmplistorUtils.uploadString(getAmplistorDBBackupURL()+getErrorInfoFile(dbBackupHelper.workingDir, baseFileName, false).getName(), null/*dbBackupHelper.amplistorCredential*/,infoSB.toString());
			}catch(Exception e){
				dbBackupHelper.errorList.add(e);
			}
		}
		
		StringBuffer errorSB = new StringBuffer();
		for(Exception e:dbBackupHelper.errorList){
			errorSB.append("--- "+e.getClass().getSimpleName()+"---"+e.getMessage()+"\r\n");
		}
		if(errorSB.length() != 0){
			writeFile(dbBackupHelper.workingDir, baseFileName, errorSB.toString(), true, null);
		}

		
	}
	private static void backup(String[] args){
		DBBackupHelper dbBackupHelper = new DBBackupHelper(args);
		tryExport(dbBackupHelper);
		tryCompress(dbBackupHelper);
		tryRobocopy(dbBackupHelper);
		tryAmplistor(dbBackupHelper);
		tryCleanup(dbBackupHelper);
	}
//	private static File findExecutableOnPath(String executableName)   
//    {   
//        String systemPath = System.getenv("PATH");
//        if(systemPath == null){
//        	systemPath = System.getenv("path");
//        }
//        if(systemPath == null){
//        	return null;
//        }
//        String[] pathDirs = systemPath.split(File.pathSeparator);   
//    
//        File fullyQualifiedExecutable = null;   
//        for (String pathDir : pathDirs)   
//        {   
//            File file = new File(pathDir, executableName);   
//            if (file.isFile())   
//            {   
//                fullyQualifiedExecutable = file;   
//                break;   
//            }   
//        }   
//        return fullyQualifiedExecutable;   
//    }   

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
	private static File getErrorInfoFile(File workingDir,String baseFileName,boolean isErrorFile){
		return new File(workingDir,baseFileName+(isErrorFile?"_error":"_info")+".txt");
	}
	private static void writeFile(File workingDir,String baseFileName,String fileText,boolean isErrorFile,File exportDir) throws Error{
		FileOutputStream fos = null;
		try {
			File outFile = getErrorInfoFile(workingDir, baseFileName, isErrorFile);
			fos = new FileOutputStream(outFile);
			fos.write(fileText.getBytes());
			fos.close();
			if(exportDir != null){
				Path source = outFile.toPath();
				Path newdir = exportDir.toPath();
				Files.copy(source, newdir.resolve(source.getFileName()));
				if(!isErrorFile){
					outFile.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("Error writing Error File:\n:"+e.getMessage(), e);
		}finally{
			if(fos != null){try{fos.close();}catch(Exception e){e.printStackTrace();}}
		}
	}
	
	private static final String selectUnreferencedSimKeySQL = 
		" SELECT "+SimulationTable.table.id.getUnqualifiedColName()+" FROM " + SimulationTable.table.getTableName() +
		" MINUS "+
		" SELECT "+BioModelSimulationLinkTable.table.simRef.getQualifiedColName()+" FROM "+BioModelSimulationLinkTable.table.getTableName()+
		" MINUS "+
		" SELECT "+MathModelSimulationLinkTable.table.simRef.getQualifiedColName()+" FROM "+MathModelSimulationLinkTable.table.getTableName()+
		" MINUS "+
		" SELECT DISTINCT "+SimulationTable.table.versionParentSimRef.getQualifiedColName()+" FROM "+SimulationTable.table.getTableName()+
			" WHERE "+SimulationTable.table.versionParentSimRef.getQualifiedColName()+" IS NOT NULL";


	public static Set<KeyValue> getUnreferencedSimulations(Connection con) throws SQLException {
		String sql = selectUnreferencedSimKeySQL;
				
		HashSet<KeyValue> unreferencedSimKeys = new HashSet<KeyValue>();
		Statement stmt = con.createStatement();
		try {
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()){
				KeyValue simKey = new KeyValue(rset.getBigDecimal(SimulationTable.table.id.toString()));
				unreferencedSimKeys.add(simKey);
			}
		} finally {
			stmt.close();
		}
		return unreferencedSimKeys;
	}

	
	private static void cleanRemoveUnreferencedSimulations(Connection con, StringBuffer logStringBuffer) throws Exception{
		//
		//Remove Simulations not pointed to by MathModels or BioModels
		//
		logStringBuffer.append("-----Remove Simulations not pointed to by MathModels or BioModels\n");
		final String SIMID = "SIMID";
		final String SIMDATE = "SIMDATE";
		String UNREFERENCED_SIMS_CLAUSE = 
			SimulationTable.table.id.getQualifiedColName() + " IN (" + selectUnreferencedSimKeySQL + ")";

		String sql =
			"SELECT "+
				"TO_CHAR(current_timestamp,'dd-Mon-yyyy hh24:mi:ss') deldate "+","+
				UserTable.table.userid.getQualifiedColName()+" userid ,"+
				UserTable.table.id.getQualifiedColName()+" userkey ,"+
				SimulationTable.table.id.getQualifiedColName()+" "+SIMID+","+
				SimulationTable.versionParentSimRef_ColumnName+" simpref ,"+
				"TO_CHAR("+SimulationTable.table.versionDate.getQualifiedColName()+",'dd-Mon-yyyy hh24:mi:ss') "+SIMDATE+","+
				SimulationTable.table.name.getQualifiedColName()+" simname"+","+
				"'"+DelSimStatus.init+"' status"+","+
				"null numfiles"+
			" FROM "+
				SimulationTable.table.getTableName()+","+
				UserTable.table.getTableName()+
			" WHERE "+
				UNREFERENCED_SIMS_CLAUSE +
				" AND "+
				UserTable.table.id.getQualifiedColName()+" = "+SimulationTable.table.ownerRef.getQualifiedColName()+
				" ORDER BY LOWER("+UserTable.table.userid.getQualifiedColName()+")";

		executeQuery(con, sql,logStringBuffer,false);
		
		String saveForDeleteSQL = 
			"insert into vc_simdelfromdisk (deldate,userid,userkey,simid,simpref,simdate,simname,status,numfiles) "+sql;
		executeUpdate(con, saveForDeleteSQL, logStringBuffer);
		
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
		
		executeQuery(con, sql,logStringBuffer,false);
			
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
			
		executeQuery(con, sql,logStringBuffer,false);

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
		
		executeQuery(con, sql,logStringBuffer,false);

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

		executeQuery(con, sql,logStringBuffer,false);
		
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
	private static ArrayList<Object[]> executeQuery(Connection con, String sql,StringBuffer logStringBuffer, boolean bReturnResults)throws Exception{
		Statement stmt = null;
		ArrayList<Object[]> queryValues = null;
		if(bReturnResults){
			queryValues = new ArrayList<>();
		}
		try{
			logStringBuffer.append(sql+";\n");
			stmt = con.createStatement();
			stmt.setFetchSize(1000);
			ResultSet rset = stmt.executeQuery(sql);
			ResultSetMetaData rsetMetaData = rset.getMetaData();
			Object[] rowValues = null;
			while(rset.next()){
				if(bReturnResults){
					rowValues = new Object[rsetMetaData.getColumnCount()];
				}
				for (int i = 0; i < rsetMetaData.getColumnCount(); i++) {
					Object colValue = rset.getObject(i+1);
					if(rset.wasNull()){
						colValue = null;
					}
					if(bReturnResults){
						rowValues[i] = colValue;
					}
					logStringBuffer.append((i==0?"":" ")+(colValue==null?"NULL":colValue.toString()));
				}
				logStringBuffer.append("\n");
				if(bReturnResults){
					queryValues.add(rowValues);
				}
			}
			logStringBuffer.append("\n");
			rset.close();
		}catch(Exception e){
			System.out.println("\n"+e.getClass().getName()+"\n"+e.getMessage());
			logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());
			if(con != null){try{con.rollback();}catch(Exception e2){logStringBuffer.append("\n"+e2.getClass().getName()+"\n"+e2.getMessage());}}
			throw e;
		}finally{
			if(stmt != null){try{stmt.close();}catch(Exception e2){logStringBuffer.append("\n"+e2.getClass().getName()+"\n"+e2.getMessage());}}
		}
		return queryValues;
	}
	private static Connection refreshConnection(Connection[] conHolder,ConnectionFactory connectionFactory) throws SQLException{
		if(conHolder[0] == null || conHolder[0].isClosed()){
			conHolder[0] = connectionFactory.getConnection(new Object());
			conHolder[0].setAutoCommit(false);
		}
		return conHolder[0];
	}
	private static void deleteSimsFromDiskMethod2(String[] args) {
		System.out.println("Getting Backuphelper...");
		DBBackupHelper dbBackupHelper = new DBBackupHelper(args);
		
		ConnectionFactory connectionFactory  = null;
		Connection[] conHolder = new Connection[] {null};
		
		String baseFileName = createBaseFileName(dbBackupHelper.dbHostName, dbBackupHelper.dbSrvcName, dbBackupHelper.vcellSchema);
		baseFileName = OP_DELSIMSDISK2+"_"+baseFileName;

//		StringBuffer logStringBuffer = new StringBuffer();
//		Pattern pattern = Pattern.compile("SimID_([0-9]+)[^0-9]*");
		Pattern pattern = Pattern.compile("SimID_(\\d+)(\\D(\\d+)_+(\\d+)*)*");
		//SimID_(\d+)(\D(\d+)_+(\d+)*)*
		try{
//			//jdbc:oracle:<drivertype>:<username/password>@<database>
//			//<database> = <host>:<port>:<SID>
			String url = "jdbc:oracle:thin:"+dbBackupHelper.vcellSchema+"/"+dbBackupHelper.password+"@//"+dbBackupHelper.dbHostName+":1521/"+dbBackupHelper.dbSrvcName;
			String dbDriverName = PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName);
			System.out.println("Getting DB connection..."+url.toString()+" "+dbDriverName);
			connectionFactory = DatabaseService.getInstance().createConnectionFactory(dbDriverName, url, dbBackupHelper.vcellSchema, dbBackupHelper.password);
			String sql = "SELECT vc_simulation.id from vc_simulation";
//					+",vc_userinfo where userid='frm' and vc_userinfo.id=vc_simulation.ownerref";
			System.out.println("Doing sims query... "+sql);
			ArrayList<Object[]> unorderedKeepTheseSims = executeQuery(refreshConnection(conHolder, connectionFactory), sql, new StringBuffer() /*throw log*/, true);
			TreeSet<String> orderedKeepTheseSims = new TreeSet<>();
			for (Iterator<Object[]> iterator = unorderedKeepTheseSims.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();
				orderedKeepTheseSims.add(object[0].toString());
			}
			System.out.println("Doing fdat query... "+sql);
			sql = "select vc_externaldata.id from vc_externaldata";
			ArrayList<Object[]> unorderedKeepTheseFieldData = executeQuery(refreshConnection(conHolder, connectionFactory), sql, new StringBuffer() /*throw log*/, true);
			TreeSet<String> orderedKeepTheseFdats = new TreeSet<>();
			for (Iterator<Object[]> iterator = unorderedKeepTheseFieldData.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();
				orderedKeepTheseFdats.add(object[0].toString());
			}
			if(orderedKeepTheseSims.size() < 500000) {
				throw new Exception("Sanity check failed, expecting >= 500000 simids to check");
			}

			unorderedKeepTheseSims = null;
			unorderedKeepTheseFieldData = null;
			closeDB(connectionFactory, conHolder, new StringBuffer() /*throw log*/);
			System.out.println("DB simcount="+orderedKeepTheseSims.size());
			System.out.println("DB fdat count="+orderedKeepTheseFdats.size());
//			FileFilter deleteTheseFilesFilter = new FileFilter() {
//				@Override
//				public boolean accept(File pathname) {
//					String name = pathname.getName();
//					Matcher matcher = pattern.matcher(name);
//					if(matcher.find()){
//						String subStr = matcher.group(1);
//						if(userSimIDs.contains(subStr)){
//							return true;
//						}
//					}
//					return false;
//				}
//			};

			File[] userDirs = dbBackupHelper.exportDir.listFiles();
			final int SIMS_QUERY =100;
			for (int i = 0; i < userDirs.length; i++) {
				long bytesRemoved = 0;
				long filesRemoved = 0;
				long bytesKept = 0;
				long filesKept = 0;
				if(userDirs[i].isDirectory() && (dbBackupHelper.thisUserOnly==null?true:dbBackupHelper.thisUserOnly.equals(userDirs[i].getName()))) {
					StringBuffer logStringBuffer = new StringBuffer();
					logStringBuffer.append("DB simcount="+orderedKeepTheseSims.size()+"\n");
					logStringBuffer.append("DB fdat count="+orderedKeepTheseFdats.size());
					logStringBuffer.append("-----USER='"+userDirs[i].getName()+"'"+"\n");
					System.out.println("-----USER='"+userDirs[i].getName()+"'");
					int notInCnt = 0;
					int isInCnt = 0;
					StringBuffer fileToRemoveSims_sb = new StringBuffer();
					StringBuffer fileToKeepSims_sb = new StringBuffer();
//					StringBuffer fileToRemoveFdats_sb = new StringBuffer();
//					StringBuffer fileToKeepFdats_sb = new StringBuffer();
					File[] checkTheseFiles = userDirs[i].listFiles();
					if(checkTheseFiles != null && checkTheseFiles.length > 0) {
						Arrays.sort(checkTheseFiles);
						for (int j = 0; j < checkTheseFiles.length; j++) {
							if(checkTheseFiles[j].isFile()) {
								Matcher matcher = pattern.matcher(checkTheseFiles[j].getName());
								if(matcher.find()){
									String simID = matcher.group(1);
	//								String msubstr = matcher.group(2);
	//								String jobid = matcher.group(3);
	//								String taskid = matcher.group(4);
	//								logStringBuffer.append("match "+matcher.group()+" "+simID+" "+msubstr+" "+jobid+" "+taskid+" from="+checkTheseFiles[j]+"\n");
	//								File testLogFileNew = new File(checkTheseFiles[j].getParentFile(),SimulationData.createCanonicalSimLogFileName(new KeyValue(simID), (jobid != null?Integer.parseInt(jobid):0), false));
	//								if(!testLogFileNew.exists()) {
	//									File testLogFileOld = new File(checkTheseFiles[j].getParentFile(),SimulationData.createCanonicalSimLogFileName(new KeyValue(simID), 0, true));
	//									if(!testLogFileOld.exists()) {
	//										logStringBuffer.append("--nolog simid="+simID+" testfileNew="+testLogFileNew+" testFileOld="+testLogFileOld+"\n");
	//									}
	//								}
									if(orderedKeepTheseSims.contains(simID) || orderedKeepTheseFdats.contains(simID)) {
										logStringBuffer.append("'"+userDirs[i].getName()+"' keep "+checkTheseFiles[j].getAbsolutePath()+"\n");
	//									System.out.println("'"+userDirs[i].getName()+"' keep "+checkTheseFiles[j].getAbsolutePath());
										fileToKeepSims_sb.append((isInCnt%SIMS_QUERY==0?(isInCnt==0?"":")\nunion\n")+"select id from vc_simulation where id in (":"")+simID+( ((isInCnt+1)%SIMS_QUERY==0)?"":","));
										isInCnt++;
										filesKept++;
										bytesKept+= checkTheseFiles[j].length();
									}else {
										logStringBuffer.append("'"+userDirs[i].getName()+"' remove "+checkTheseFiles[j].getAbsolutePath()+"\n");
	//									System.out.println("'"+userDirs[i].getName()+"' remove "+checkTheseFiles[j].getAbsolutePath());
										fileToRemoveSims_sb.append((notInCnt%SIMS_QUERY==0?(notInCnt==0?"":")\nunion\n")+"select id from vc_simulation where id in (":"")+simID+( ((notInCnt+1)%SIMS_QUERY==0)?"":","));
										notInCnt++;
										filesRemoved++;
										bytesRemoved+= checkTheseFiles[j].length();
										if(!deleteFileAndLink(checkTheseFiles[j], logStringBuffer)) {
											if(!Files.getOwner(checkTheseFiles[j].toPath()).getName().equals("root")) {// skip root owned files for now e.g. simtask.xml
												throw new Exception("Failed to delete "+checkTheseFiles[j].getAbsolutePath());
											}	
										}
									}
								}else {
									logStringBuffer.append("no match "+checkTheseFiles[j]+"\n");
								}
							}
						}
					}
					if(fileToRemoveSims_sb.length() > 0 && fileToRemoveSims_sb.charAt(fileToRemoveSims_sb.length()-1) == ',') {
						fileToRemoveSims_sb.deleteCharAt(fileToRemoveSims_sb.length()-1);
					}
					if(fileToKeepSims_sb.length() > 0 && fileToKeepSims_sb.charAt(fileToKeepSims_sb.length()-1) == ',') {
						fileToKeepSims_sb.deleteCharAt(fileToKeepSims_sb.length()-1);
					}
					fileToRemoveSims_sb.append(");");
					fileToKeepSims_sb.append(");");
//					System.out.println("\n--NOT IN\n"+fileToRemoveSims_sb.toString());
					logStringBuffer.append("\n--NOT IN\n"+fileToRemoveSims_sb.toString()+"\n");
//					System.out.println("\n--IS IN\n"+fileToKeepSims_sb.toString());
					logStringBuffer.append("\n--IS IN\n"+fileToKeepSims_sb.toString()+"\n");
					logStringBuffer.append("-----'"+userDirs[i]+"' total files removed ="+filesRemoved+" total bytes removed="+bytesRemoved+" total files kept ="+filesKept+" total bytes kept="+bytesKept+"\n\n");
					if(filesRemoved > 0) {
						writeFile(dbBackupHelper.workingDir, baseFileName+"_"+userDirs[i].getName(), logStringBuffer.toString(),false, dbBackupHelper.exportDir);
					}					
				}
			}
//			File userDir = new File(dbBackupHelper.exportDir,userID);
//			File[] deleteTheseFiles = userDir.listFiles(deleteTheseFilesFilter);
		}catch(Exception e){
			e.printStackTrace();
			writeFile(dbBackupHelper.workingDir, baseFileName, e.getClass().getName()+"\n"+e.getMessage(),true, dbBackupHelper.exportDir);
		}finally{
			closeDB(connectionFactory, conHolder, new StringBuffer() /*throw log*/);
		}
	}

	public static void closeDB(ConnectionFactory connectionFactory, Connection[] conHolder,StringBuffer logStringBuffer) {
		if(conHolder[0] != null){
			try{conHolder[0].close();conHolder[0] = null;}catch(Exception e){logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());e.printStackTrace();}
		}
		if(connectionFactory != null){
			try{connectionFactory.close();connectionFactory = null;}catch(Exception e){logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());e.printStackTrace();}
		}
	}
	private static enum DelSimStatus {init,notfound,delall,delsome,delnone};
	private static void deleteSimsFromDiskMethod1(String[] args) {
//		deleteFileAndLink(new File("/share/apps/vcell3/users/bonniecalizo/SimID_58074590_0_.pbs.sub"));
//		if(true) {
//			return;
//		}
System.out.println("Getting Backuphelper...");
		DBBackupHelper dbBackupHelper = new DBBackupHelper(args);
		
		ConnectionFactory connectionFactory  = null;
		Connection[] conHolder = new Connection[] {null};
		
		String baseFileName = createBaseFileName(dbBackupHelper.dbHostName, dbBackupHelper.dbSrvcName, dbBackupHelper.vcellSchema);
		baseFileName = OP_DELSIMSDISK+"_"+baseFileName;

		StringBuffer logStringBuffer = new StringBuffer();
		Pattern pattern = Pattern.compile("SimID_([0-9]+)[^0-9]*");
		//SimID_([0-9]+)[^0-9]*
		try{
//			//jdbc:oracle:<drivertype>:<username/password>@<database>
//			//<database> = <host>:<port>:<SID>
			String url = "jdbc:oracle:thin:"+dbBackupHelper.vcellSchema+"/"+dbBackupHelper.password+"@//"+dbBackupHelper.dbHostName+":1521/"+dbBackupHelper.dbSrvcName;
			String dbDriverName = PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName);
System.out.println("Getting DB connection..."+url.toString()+" "+dbDriverName);
			connectionFactory = DatabaseService.getInstance().createConnectionFactory(dbDriverName, url, dbBackupHelper.vcellSchema, dbBackupHelper.password);
			
			String sql = 
				"SELECT simid,userid" +
				" from vc_simdelfromdisk " +
				" where" +
//	" lower(userid) like 'm%' and"+
	//" userid='boris' and"+
				" status='"+DelSimStatus.init.name()+"'" +
				" and simid not in (select id from vc_simulation)" +
				" order by userid";
System.out.println("Doing query... "+sql);
			ArrayList<Object[]> deleteTheseSims = executeQuery(refreshConnection(conHolder, connectionFactory), sql, logStringBuffer, true);
System.out.println("Del sim simcount="+deleteTheseSims.size());
			//Organize simIDs by userid
			HashMap<String, TreeSet<String>> userSimsMap = new HashMap<>();
			for(Object[] objs:deleteTheseSims){
				BigDecimal simID = (BigDecimal)objs[0];
				String userid = (String)objs[1];
				TreeSet<String> userSims = userSimsMap.get(userid);
				if(userSims == null){
					userSims = new TreeSet<String>();
					userSimsMap.put(userid, userSims);
				}
				userSims.add(simID.toString());
			}

			Iterator<String> userIter = userSimsMap.keySet().iterator();
			while(userIter.hasNext()){
				String userID = userIter.next();
				TreeSet<String> userSimIDs = userSimsMap.get(userID);

				FileFilter deleteTheseFilesFilter = new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						String name = pathname.getName();
						Matcher matcher = pattern.matcher(name);
						if(matcher.find()){
							String subStr = matcher.group(1);
							if(userSimIDs.contains(subStr)){
								return true;
							}
						}
//						name.startsWith("SimID_");
//						int endIndex = name.indexOf('_', 6);
//						if(endIndex == -1){
//							endIndex = name.indexOf(".ode", 6);
//						}
//						String subStr = name.substring(6, endIndex);
//						if(userSimIDs.contains(subStr)){
//							return true;
//						}
						return false;
					}
				};
				for(String simid:userSimsMap.get(userID)){
					System.out.println("     "+simid);
				}
				File userDir = new File(dbBackupHelper.exportDir,userID);
				File[] deleteTheseFiles = userDir.listFiles(deleteTheseFilesFilter);
				HashMap<String, ArrayList<File>> simidToFilesMap = new HashMap<>();
				if(deleteTheseFiles != null) {
					for (int i = 0; i < deleteTheseFiles.length; i++) {
						String name = deleteTheseFiles[i].getName();
						Matcher matcher = pattern.matcher(name);
						if(matcher.find()){
							String subStr = matcher.group(1);
							if(userSimIDs.contains(subStr)){
								ArrayList<File> fileMatchSimIDFile = simidToFilesMap.get(subStr);
								if(fileMatchSimIDFile == null){
									fileMatchSimIDFile = new ArrayList<>();
									simidToFilesMap.put(subStr, fileMatchSimIDFile);
								}
								
								fileMatchSimIDFile.add(deleteTheseFiles[i]);
							}
						}

//						String subStr = name.substring(6, name.indexOf('_', 6));
//						if(userSimIDs.contains(subStr)){
//							ArrayList<File> fileMatchSimIDFile = simidToFilesMap.get(subStr);
//							if(fileMatchSimIDFile == null){
//								fileMatchSimIDFile = new ArrayList<>();
//								simidToFilesMap.put(subStr, fileMatchSimIDFile);
//							}
//							
//							fileMatchSimIDFile.add(deleteTheseFiles[i]);
//						}
					}
				}
				
				StringBuffer fileMatchSimIDidSB = new StringBuffer();
				for(String str:simidToFilesMap.keySet()){
					fileMatchSimIDidSB.append((fileMatchSimIDidSB.length()>0?",":"")+str);
				}
				
				TreeSet<String> simIDsNotMatchFiles = ((TreeSet<String>)userSimsMap.get(userID).clone());
				simIDsNotMatchFiles.removeAll(simidToFilesMap.keySet());
				ArrayList<StringBuffer> smallSB = new ArrayList<>();// breakup into pieces because oracle limit on lists size
				int simcount = 0;
				for(String simid:simIDsNotMatchFiles){
					if(smallSB.size() == 0 || simcount >= 500){
						simcount = 0;
						smallSB.add(new StringBuffer());
					}
					smallSB.get(smallSB.size()-1).append((smallSB.get(smallSB.size()-1).length()>0?",":"")+simid);
					simcount++;
				}
				
				System.out.println(userID+" SimIDs deleted="+fileMatchSimIDidSB.toString());
				for(int i=0;i<smallSB.size();i++){
					System.out.println(userID+" SimIDs Not Found in files="+smallSB.get(i).toString());					
				}
				
				for(int i=0;i<smallSB.size();i++){
					sql = "update vc_simdelfromdisk set status='"+DelSimStatus.notfound.name()+"' where userid='"+userID+"' and simid in ("+smallSB.get(i).toString()+")";
					executeUpdate(refreshConnection(conHolder, connectionFactory), sql, logStringBuffer);
				}

				for(String simid:simidToFilesMap.keySet()){
					long totalSize = 0;
					ArrayList<File> files = simidToFilesMap.get(simid);
					int delCount = 0;
					for(File file:files){
						if(file.exists()){
							long fileSize = file.length();
							if(deleteFileAndLink(file,logStringBuffer)){
								delCount++;
								totalSize+= fileSize;
	//							System.out.println("deleted "+file.getAbsolutePath());
							}else{
	//							System.out.println("fail delete "+file.getAbsolutePath());
							}
						}
					}
					String newStatus = DelSimStatus.delsome.name();
					if(delCount == 0){
						newStatus = DelSimStatus.delnone.name();
					}else if(delCount == files.size()){
						newStatus = DelSimStatus.delall.name();
					}
					sql = "update vc_simdelfromdisk set status='"+newStatus+"', numfiles="+files.size()+", totalsize="+totalSize+" where userid='"+userID+"' and simid="+simid;
					executeUpdate(refreshConnection(conHolder, connectionFactory), sql, logStringBuffer);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			writeFile(dbBackupHelper.workingDir, baseFileName, e.getClass().getName()+"\n"+e.getMessage(),true, dbBackupHelper.exportDir);
		}finally{
			closeDB(connectionFactory, conHolder, logStringBuffer);
		}		
	}
	
	public static boolean deleteFileAndLink(File deleteThis,StringBuffer logStringBuffer) {
		try {
			ProcessBuilder pb = new ProcessBuilder("readlink","-m",deleteThis.getAbsolutePath());
			pb.redirectErrorStream(true);// out and err in one stream
			
	//			System.out.println("\n--cmd begin--");
	//			Arrays.stream(pb.command().toArray()).map(s->s+" ").forEach(System.out::print);
	//			System.out.println("\n--cmd end--");
	
			final Process p = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			StringBuffer sb = new StringBuffer();
			while((line=br.readLine())!=null){
			   sb.append(line);
			}
			p.waitFor();
			if(p.exitValue() == 0) {
				File linkFile = null;
				linkFile = new File(sb.toString().trim());
				if(!linkFile.exists()) {
					throw new Exception("broken link:'"+linkFile.getAbsolutePath()+"'");
				}else {
					if(linkFile.equals(deleteThis)) {
						logStringBuffer.append("removing (not link):'"+deleteThis.getAbsolutePath()+"'\n");
						if(!deleteThis.delete()) {
							throw new Exception("Failed to delete FILE '"+linkFile.getAbsolutePath()+"'");
						}
						return true;
					}else {
						logStringBuffer.append("remove both (is link):'"+deleteThis.getAbsolutePath()+"' -> '"+linkFile.getAbsolutePath()+"'\n");
						if(!deleteThis.delete()) {//delete link
							throw new Exception("Failed to delete LINK '"+deleteThis.getAbsolutePath()+"'");
						}
						boolean bDeleteActual = linkFile.delete();//delete actual file
						if(!bDeleteActual) {
							throw new Exception("Failed to delete LINKEDTO FILE '"+linkFile.getAbsolutePath()+"'");
						}
						return true;
					}
				}
			}else {
				throw new Exception("process error:'"+deleteThis.getAbsolutePath()+"' exit code="+p.exitValue()+" cmd output='"+sb.toString()+"'");
			}
		}catch(Exception e) {
			logStringBuffer.append("error:'"+deleteThis.getAbsolutePath()+"' mesg="+e.getMessage()+"\n");
			return false;
		}
	}
	private static void clean(String[] args) {
		DBBackupHelper dbBackupHelper = new DBBackupHelper(args);
		
		ConnectionFactory connectionFactory  = null;
		Connection con = null;
		
		String baseFileName = createBaseFileName(dbBackupHelper.dbHostName, dbBackupHelper.dbSrvcName, dbBackupHelper.vcellSchema);
		baseFileName = OP_CLEAN+"_"+baseFileName;

		StringBuffer logStringBuffer = new StringBuffer();

		try{
			//jdbc:oracle:<drivertype>:<username/password>@<database>
			//<database> = <host>:<port>:<SID>
			String url = "jdbc:oracle:thin:"+dbBackupHelper.vcellSchema+"/"+dbBackupHelper.password+"@//"+dbBackupHelper.dbHostName+":1521/"+dbBackupHelper.dbSrvcName;
			String dbDriverName = PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName);
			connectionFactory = DatabaseService.getInstance().createConnectionFactory(dbDriverName, url, dbBackupHelper.vcellSchema, dbBackupHelper.password);

			con = connectionFactory.getConnection(new Object());
			con.setAutoCommit(false);
						
			cleanupDatabase(con, logStringBuffer);

			writeFile(dbBackupHelper.workingDir, baseFileName, logStringBuffer.toString(), false, dbBackupHelper.exportDir);
			
			File remoteCleanfile = getErrorInfoFile(dbBackupHelper.exportDir, baseFileName, false);
			if(remoteCleanfile.exists()){
				AmplistorUtils.uploadFile(new URL(getAmplistorDBBackupURL()), remoteCleanfile, null/*dbBackupHelper.amplistorCredential*/);
			}


		}catch(Exception e){
			writeFile(dbBackupHelper.workingDir, baseFileName, e.getClass().getName()+"\n"+e.getMessage(),true, dbBackupHelper.exportDir);
		}finally{
			if(con != null){
				try{con.close();}catch(Exception e){logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());}
			}
			if(connectionFactory != null){
				try{connectionFactory.close();}catch(Exception e){logStringBuffer.append("\n"+e.getClass().getName()+"\n"+e.getMessage());}
			}
		}
		
	}

	public static void cleanupDatabase(Connection con, StringBuffer logStringBuffer) throws Exception {
		cleanClearVersionBranchPointRef(con,SimulationTable.table, logStringBuffer);
		cleanRemoveUnreferencedSimulations(con, logStringBuffer);

		cleanClearVersionBranchPointRef(con,MathDescTable.table, logStringBuffer);
		cleanRemoveUnreferencedMathDescriptions(con, logStringBuffer);

		cleanRemoveUnReferencedNonSpatialGeometries(con, logStringBuffer);

		cleanClearVersionBranchPointRef(con,SimContextTable.table, logStringBuffer);
		cleanRemoveUnReferencedSimulationContexts(con, logStringBuffer);

		cleanRemoveUnReferencedModels(con, logStringBuffer);
		
		cleanRemoveUnReferencedSotwareVersions(con, logStringBuffer);
	}
}

