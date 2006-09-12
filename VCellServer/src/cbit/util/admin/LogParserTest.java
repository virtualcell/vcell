package cbit.util.admin;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;

import cbit.util.SessionLog;
/**
 * Insert the type's description here.
 * Creation date: (8/27/01 12:06:11 PM)
 * @author: Jim Schaff
 */
public class LogParserTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
    try {
        if (args.length < 8 || !args[0].equalsIgnoreCase("-version") || !args[2].equalsIgnoreCase("-database") || !args[7].equalsIgnoreCase("-files")) {
            System.out.println("usage: LogParserTest -version swVersion -database host sid user password -files file1 [file2] [file3] ...");
            System.out.println("       LogParserTest -version swVersion -database host sid user password -files directory1 [directory2]");
            throw new Exception("usage");
        }
        String swVersion = args[1];
        String driverName = "oracle.jdbc.driver.OracleDriver";
        String host = args[3];
        String db = args[4];
        String connectURL = "jdbc:oracle:thin:@" + host + ":1521:" + db;
        String dbSchemaUser = args[5];
        String dbPassword = args[6];
        SessionLog sessionLog = new cbit.util.StdoutSessionLog("logParser");
        cbit.sql.ConnectionFactory connFactory = new cbit.sql.OraclePoolingConnectionFactory(sessionLog,driverName,connectURL,dbSchemaUser,dbPassword);
        File files[] = null;
		java.util.Vector fileList = new java.util.Vector();
		for (int i = 8; i < args.length; i++) {
			fileList.add(new File(args[i]));
		}
		//
		// multi-pass, non-recursive gathering of all non-directory files in directories that were passed in.
		//
		boolean bDirectoriesToSubstitute = true;
		while (bDirectoriesToSubstitute){
			bDirectoriesToSubstitute = false;
			for (int i = 0; i < fileList.size(); i++){
				File file = (File)fileList.elementAt(i);
				if (file.isDirectory()){
					bDirectoriesToSubstitute = true;
					//
					// if directory, remove directory and add children
					//
					fileList.remove(file);
					File dir = file;
					String fileNames[] = dir.list();
					for (int j = 0;fileNames!=null && j < fileNames.length; j++){
						File childFile = new File(dir, fileNames[j]);
						fileList.add(childFile);
					}
				}
			}
		}
		files = (File[])cbit.util.BeanUtils.getArray(fileList,File.class);
		System.out.println("Files:");
		for (int i = 0; i < files.length; i++){
			System.out.println(files[i].getAbsolutePath());
		}

        LogParser logParser = new LogParser(files, swVersion, connFactory);
        System.out.println(logParser.showSummary());
    } catch (Throwable e) {
        e.printStackTrace(System.out);
    } finally {
        System.exit(0);
    }
}
}
