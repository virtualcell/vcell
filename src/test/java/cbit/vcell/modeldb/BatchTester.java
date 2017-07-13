package cbit.vcell.modeldb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.VCMultiBioVisitor;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlParseException;

public class BatchTester extends VCDatabaseScanner {
	/**
	 * database column name (primary key)
	 */
	public static final String ID = "id";
	/**
	 * database column name 
	 */
	public static final String USER_ID = "user_id";
	/**
	 * database column name 
	 */
	public static final String MODEL_ID = "model_id";
	/**
	 * database column name 
	 */
	public static final String PROCESS_IDENT = "scan_process";
	/**
	 * database column name 
	 */
	public static final String SCANNED = "scanned";
	/**
	 * database column name 
	 */
	public final String GOOD = "good";
	/**
	 * database column name 
	 */
	public final String RAN_LONG = "ran_long";
	/**
	 * database column name 
	 */
	public final String EXCEPTION = "exception";
	/**
	 * special user with access to all models (see {@link DatabasePolicySQL#enforceOwnershipSelect(User, cbit.sql.Field[], cbit.sql.Table[], String, String)
	 */
	public static final User ADMINISTRATOR = new User(PropertyLoader.ADMINISTRATOR_ACCOUNT,new KeyValue(PropertyLoader.ADMINISTRATOR_ID));

	public BatchTester(SessionLog log) throws Exception {
		super(log);
	}
	
	/**
	 * convert KeyValue to long
	 */
	private static long convert(KeyValue kv) {
		return Long.parseLong(kv.toString());
	}
	
	/**
	 * convert long to KeyValue
	 */
	private static KeyValue convert(long v) {
		return new KeyValue(Long.toString(v));
	}

	/**
	 * scan biomodels for further processing, put into database table if criteria met
	 * @throws DataAccessException 
	 * @throws XmlParseException 
	 */
	public void keyScanBioModels(VCMultiBioVisitor databaseVisitor, Writer writer, User users[],
			boolean bAbortOnDataAccessException, String statusTableName) throws DataAccessException,
			XmlParseException {
				assert users != null;
				try
				{
					PrintWriter printWriter = new PrintWriter(writer);
					//start visiting models and writing log
					printWriter.println("Start scanning bio-models......");
					printWriter.println("\n");
					Object lock = new Object();
					Connection conn = connFactory.getConnection(lock);
					PreparedStatement ps = conn.prepareStatement("insert into " + statusTableName + "(user_id,model_id) values(?,?)");
			
					for (int i=0;i<users.length;i++)
					{
						User user = users[i];
						BioModelInfo bioModelInfos[] = dbServerImpl.getBioModelInfos(user,false);
						for (int j = 0; j < bioModelInfos.length; j++){
							BioModelInfo bmi = bioModelInfos[j];
							if (!databaseVisitor.filterBioModel(bmi)) {
								continue;
							}
							try {
								KeyValue vk = bmi.getVersion().getVersionKey();
								ps.setLong(1,convert(user.getID()));
								ps.setLong(2,convert(vk));
								ps.execute( );
							}catch (Exception e2){
								log.exception(e2);
								printWriter.println("======= " + e2.getMessage());
								if (bAbortOnDataAccessException){
									throw e2;
								}
							}
						}
					}
					
					printWriter.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}

	@SuppressWarnings("static-access")
	public void batchScanBioModels(VCMultiBioVisitor databaseVisitor, String statusTable, int chunkSize)
			throws DataAccessException, XmlParseException, SQLException, IOException {
		PrintStream current = System.out;
		//System.setOut(new PrintStream(new NullStream()));
		try {
				String processHostId = ManagementFactory.getRuntimeMXBean().getName();
				String filename = processHostId + ".txt";
				FileOutputStream fos = new FileOutputStream(filename);
				System.setOut(new PrintStream(fos));
				OutputStreamWriter writer = new OutputStreamWriter(fos);
				PrintWriter printWriter = new PrintWriter(writer, true); //autoflush
				Connection conn = connFactory.getConnection(null);
				conn.setAutoCommit(true);
				printWriter.println("reserving slots");
				try (Statement statement = conn.createStatement()) {
					String query = "Update "  + statusTable + " set scan_process = '" + processHostId 
							+ "', log_file = '" + filename
							+ "' where scanned = 0 and scan_process is null and rownum <= "
							+ chunkSize;
					int uCount = statement.executeUpdate(query);
					if (uCount > chunkSize) {
						throw new Error("logic / SQL bad");
					}
					if (uCount == 0) {
						printWriter.println("No models to scan, exiting");
						System.exit(100);
						
					}
				}
				printWriter.println("finding  ours");
				ArrayList<BioModelIdent> models = new ArrayList<BatchTester.BioModelIdent>();
				try (Statement statement = conn.createStatement()) {
					String query = "Select id, user_id, model_id from " + statusTable +
							" where scan_process ='" + processHostId + "' and scanned = 0";
					ResultSet rs = statement.executeQuery(query);
					while (rs.next( )) {
						BioModelIdent mi = new BioModelIdent(rs);
						models.add(mi);
						printWriter.println("claiming " + mi.statusId);
					}
				}
		
				try
				{
					//start visiting models and writing log
					printWriter.println("Start scanning bio-models......");
					printWriter.println("\n");
					PreparedStatement ps = conn.prepareStatement(
							"Update " + statusTable 
							+ " set scanned = 1, good = ? , exception_type = ?, exception = ?, scan_process = null where id = ?");
					for (BioModelIdent modelIdent : models) {
						ScanStatus scanStatus = ScanStatus.PASS;
						String exceptionMessage = null;
						String exceptionClass = null;
						try {
							User user = new User("", convert(modelIdent.userId));
							KeyValue modelKey = convert(modelIdent.modelId);
							BigString bioModelXML = null; 
							long dbSleepTime = 10; //seconds
							while (bioModelXML == null) {
							 
								try {
									bioModelXML = dbServerImpl.getBioModelXML(user,modelKey);
								} catch (DataAccessException dae) {
									Throwable cause = dae.getCause();
									if (cause.getClass().getSimpleName().equals("UniversalConnectionPoolException")) {
										printWriter.println("No db connection for  " + modelIdent.statusId + ", sleeping " + dbSleepTime + " seconds");
										Thread.currentThread().sleep(dbSleepTime * 1000); 
										dbSleepTime *= 1.5; //wait a little longer next time
									}
									else {
										throw dae; //other exception, just rethrow
									}
								}
							}
							BioModel storedModel = cbit.vcell.xml.XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
							if (databaseVisitor.filterBioModel(storedModel)) {
								storedModel.refreshDependencies();
								boolean goodModel = verifyMathDescriptionsUnchanged(storedModel,printWriter);
								if (goodModel) {
									printWriter.println("Model for " + modelIdent.statusId + " good");
									databaseVisitor.setBioModel(storedModel, printWriter);
									for (BioModel bioModel: databaseVisitor) {
										SimulationContext[] simContexts = bioModel.getSimulationContexts();
										for (SimulationContext sc : simContexts) {
											//try {
											//long start = System.currentTimeMillis();
											sc.createNewMathMapping().getMathDescription();
											//long end = System.currentTimeMillis();
											//printWriter.println("mapping took " + (end - start)/1000.0 + " sec ");
											/*
									} catch (Exception e) {
										//printWriter.println("\t " + bioModel.getName() + " :: " + sc.getName() + " ----> math regeneration failed.s");
										// e.printStackTrace();
									}
											 */
										}
									}
								}
								else {
									throw new MathRegenFail();
								}
							}
							else {
								scanStatus = ScanStatus.FILTERED;
							}
						}catch (Exception e){
							log.exception(e);
							scanStatus = ScanStatus.FAIL;
							exceptionClass = e.getClass().getName();
							exceptionMessage = e.getMessage();
							printWriter.println("failed " + modelIdent.statusId);
							e.printStackTrace(printWriter);
						}
						ps.setInt(1,scanStatus.code);
						ps.setString(2,exceptionClass);
						ps.setString(3,exceptionMessage);
						ps.setLong(4,modelIdent.statusId);
						boolean estat = ps.execute();
						if (estat) {
							throw new Error("logic");
						}
						int uc = ps.getUpdateCount();
						if (uc != 1) {
							throw new Error("logic / sql ");
						}
					}

				}catch(Exception e)
				{
					e.printStackTrace(printWriter);
				}
				printWriter.close();
		}
		finally {
			System.setOut(current);
		}
	}
	
	/*
	private int boolAsInt(boolean b) {
		return b ? 1: 0;
	}
	*/
	
	/**
	 * scan math models for further processing, put into database table if criteria met
	 * @throws DataAccessException 
	 * @throws XmlParseException 
	 */
	public void keyScanMathModels(BadMathVisitor databaseVisitor, Writer writer, 
			boolean bAbortOnDataAccessException, String statusTableName) throws DataAccessException,
			XmlParseException {
				try
				{
					PrintWriter printWriter = new PrintWriter(writer);
					//start visiting models and writing log
					printWriter.println("Start scanning math-models......");
					printWriter.println("\n");
					Object lock = new Object();
					Connection conn = connFactory.getConnection(lock);
					PreparedStatement ps = conn.prepareStatement("insert into " + statusTableName + "(model_id) values(?)");
			
						MathModelInfo mathModelInfos[] = dbServerImpl.getMathModelInfos(BatchTester.ADMINISTRATOR,true);
						for (int j = 0; j < mathModelInfos.length; j++){
							MathModelInfo mmi = mathModelInfos[j];
							if (!databaseVisitor.filterMathModel(mmi)) {
								continue;
							}
							try {
								KeyValue vk = mmi.getVersion().getVersionKey();
								ps.setLong(1,convert(vk));
								ps.execute( );
							}catch (Exception e2){
								log.exception(e2);
								printWriter.println("======= " + e2.getMessage());
								if (bAbortOnDataAccessException){
									throw e2;
								}
							}
						}
					
					printWriter.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}

	@SuppressWarnings("static-access")
	public void batchScanMathModels(BadMathVisitor databaseVisitor, String statusTable, int chunkSize)
			throws DataAccessException, XmlParseException, SQLException, IOException {
		PrintStream current = System.out;
		//System.setOut(new PrintStream(new NullStream()));
		try {
				String processHostId = ManagementFactory.getRuntimeMXBean().getName();
				String filename = processHostId + ".txt";
				FileOutputStream fos = new FileOutputStream(filename);
				System.setOut(new PrintStream(fos));
				OutputStreamWriter writer = new OutputStreamWriter(fos);
				PrintWriter printWriter = new PrintWriter(writer, true); //autoflush
				Connection conn = connFactory.getConnection(null);
				conn.setAutoCommit(true);
				printWriter.println("reserving slots");
				try (Statement statement = conn.createStatement()) {
					String query = "Update "  + statusTable + " set scan_process = '" + processHostId 
							+ "', log_file = '" + filename
							+ "' where scanned = 0 and scan_process is null and rownum <= "
							+ chunkSize;
					int uCount = statement.executeUpdate(query);
					if (uCount > chunkSize) {
						throw new Error("logic / SQL bad");
					}
					if (uCount == 0) {
						printWriter.println("No models to scan, exiting");
						System.exit(100);
						
					}
				}
				printWriter.println("finding  ours");
				ArrayList<MathModelIdent> models = new ArrayList<BatchTester.MathModelIdent>();
				try (Statement statement = conn.createStatement()) {
					String query = "Select model_id from " + statusTable +
							" where scan_process ='" + processHostId + "' and scanned = 0";
					ResultSet rs = statement.executeQuery(query);
					while (rs.next( )) {
						MathModelIdent mmi = new MathModelIdent(rs);
						models.add(mmi);
						printWriter.println("claiming " + mmi.id);
					}
				}
		
				try
				{
					//start visiting models and writing log
					printWriter.println("Start scanning math-models......");
					printWriter.println("\n");
					PreparedStatement ps = conn.prepareStatement(
							"Update " + statusTable 
							+ " set scanned = 1, good = ? , exception_type = ?, exception = ?, scan_process = null where model_id = ?");
					for (MathModelIdent modelIdent : models) {
						ScanStatus scanStatus = ScanStatus.PASS;
						String exceptionMessage = null;
						String exceptionClass = null;
						try {
							KeyValue modelKey = convert(modelIdent.id);
							BigString mathModelXML = null; 
							long dbSleepTime = 10; //seconds
							while (mathModelXML == null) {
								try {
									 mathModelXML = dbServerImpl.getMathModelXML(BatchTester.ADMINISTRATOR, modelKey);
								} catch (DataAccessException dae) {
									Throwable cause = dae.getCause();
									if (cause.getClass().getSimpleName().equals("UniversalConnectionPoolException")) {
										printWriter.println("No db connection for  " + modelIdent.id + ", sleeping " + dbSleepTime + " seconds");
										Thread.currentThread().sleep(dbSleepTime * 1000); 
										dbSleepTime *= 1.5; //wait a little longer next time
									}
									else {
										throw dae; //other exception, just rethrow
									}
								}
							}
							MathModel storedModel = cbit.vcell.xml.XmlHelper.XMLToMathModel( new XMLSource(mathModelXML.toString()));
							databaseVisitor.visitMathModel(storedModel,System.out);
						}catch (Exception e){
							log.exception(e);
							scanStatus = ScanStatus.FAIL;
							exceptionClass = e.getClass().getName();
							exceptionMessage = e.getMessage();
							printWriter.println("failed " + modelIdent.id);
							e.printStackTrace(printWriter);
						}
						ps.setInt(1,scanStatus.code);
						ps.setString(2,exceptionClass);
						ps.setString(3,exceptionMessage);
						ps.setLong(4,modelIdent.id);
						boolean estat = ps.execute();
						if (estat) {
							throw new Error("logic");
						}
						int uc = ps.getUpdateCount();
						if (uc != 1) {
							throw new Error("logic / sql ");
						}
						printWriter.println("model " + modelIdent.id + " " + scanStatus);
					}
	
				printWriter.close();
		}
		finally {
			System.setOut(current);
		}
	}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class BioModelIdent {
		private final long statusId;
		private final long userId;
		private final long modelId;
		BioModelIdent(ResultSet rs) throws SQLException {
			statusId = rs.getLong(ID);
			userId = rs.getLong(USER_ID);
			modelId = rs.getLong(MODEL_ID);
			
		}
	}
	private static class MathModelIdent {
		private final long id; 
		MathModelIdent(ResultSet rs) throws SQLException {
			id = rs.getLong(MODEL_ID);
		}
		
	}
	
	/**
	 * OutputStream which just dumps (ignores) output
	 *
	 */
	public static class NullStream extends OutputStream {

		@Override
		public void write(int ignore) throws IOException {
			
		}
		
	}
	
	@SuppressWarnings("serial")
	private static class MathRegenFail extends RuntimeException {
		public MathRegenFail() {
			super("Math regeneration of stored model failed");
		}
	}
	
	@SuppressWarnings("unused")
	private static class WatchDog implements Runnable{
		final Thread target;
		Thread monitorThread = null;
		int secondsToWait = 0;
		
		public WatchDog(Thread target) {
			super();
			this.target = target;
		}
		
		/**
		 * 
		 * @param seconds
		 */
		public synchronized void killAfter(int seconds) {
			if (monitorThread != null) {
				monitorThread.interrupt();
			}
			secondsToWait = seconds;
			monitorThread = new Thread(this,"Watchdog monitor");
			monitorThread.start();
		}

		@Override
		public void run() {
			try {
				if (secondsToWait > 0) {
					Thread.sleep(secondsToWait * 1000);
					target.interrupt();
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
/*
 * --------------------------------------------------------
--  DDL for Table MODELS_TO_SCAN
--------------------------------------------------------

  CREATE TABLE "GERARD"."MODELS_TO_SCAN" 
   (	"USER_ID" NUMBER DEFAULT 0, 
	"MODEL_ID" NUMBER, 
	"SCANNED" NUMBER(1,0) DEFAULT 0, 
	"GOOD" NUMBER(1,0) DEFAULT 0, 
	"ID" NUMBER(38,0), 
	"SCAN_PROCESS" VARCHAR2(60 BYTE), 
	"RAN_LONG" NUMBER(1,0) DEFAULT 0, 
	"EXCEPTION" VARCHAR2(2000 BYTE), 
	"EXCEPTION_TYPE" VARCHAR2(60 BYTE)
   ) ;
 

   COMMENT ON COLUMN "GERARD"."MODELS_TO_SCAN"."USER_ID" IS 'user id in VC_USERINFO';
 
   COMMENT ON COLUMN "GERARD"."MODELS_TO_SCAN"."MODEL_ID" IS 'biomodel id';
 
   COMMENT ON COLUMN "GERARD"."MODELS_TO_SCAN"."SCANNED" IS 'has this model been scanned for correctness?';
 
   COMMENT ON COLUMN "GERARD"."MODELS_TO_SCAN"."GOOD" IS 'scan was good';
 
   COMMENT ON COLUMN "GERARD"."MODELS_TO_SCAN"."RAN_LONG" IS 'flag indicating java process ran a long time trying to process';
--------------------------------------------------------
--  DDL for Index MODELS_TO_SCAN_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "GERARD"."MODELS_TO_SCAN_PK" ON "GERARD"."MODELS_TO_SCAN" ("ID") 
  ;
--------------------------------------------------------
--  DDL for Index MODELS_TO_SCAN_INDEX1
--------------------------------------------------------

  CREATE UNIQUE INDEX "GERARD"."MODELS_TO_SCAN_INDEX1" ON "GERARD"."MODELS_TO_SCAN" ("MODEL_ID") 
  ;
--------------------------------------------------------
--  Constraints for Table MODELS_TO_SCAN
--------------------------------------------------------

  ALTER TABLE "GERARD"."MODELS_TO_SCAN" ADD CONSTRAINT "MODELS_TO_SCAN_PK" PRIMARY KEY ("ID") ENABLE;
 
  ALTER TABLE "GERARD"."MODELS_TO_SCAN" MODIFY ("MODEL_ID" NOT NULL ENABLE);
 
  ALTER TABLE "GERARD"."MODELS_TO_SCAN" MODIFY ("SCANNED" NOT NULL ENABLE);
 
  ALTER TABLE "GERARD"."MODELS_TO_SCAN" MODIFY ("GOOD" NOT NULL ENABLE);
 
  ALTER TABLE "GERARD"."MODELS_TO_SCAN" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "GERARD"."MODELS_TO_SCAN" MODIFY ("RAN_LONG" NOT NULL ENABLE);
--------------------------------------------------------
--  Ref Constraints for Table MODELS_TO_SCAN
--------------------------------------------------------

  ALTER TABLE "GERARD"."MODELS_TO_SCAN" ADD CONSTRAINT "VALID_GOOD_CODE" FOREIGN KEY ("GOOD")
	  REFERENCES "GERARD"."SCAN_STATUS" ("CODE") ENABLE;
--------------------------------------------------------
--  DDL for Trigger MODELS_TO_SCAN_PK
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "GERARD"."MODELS_TO_SCAN_PK" 
BEFORE INSERT ON MODELS_TO_SCAN 
for each row
BEGIN
  :NEW.ID := MODELS_TO_SCAN_SEQ.NEXTVAL;
END;
/
ALTER TRIGGER "GERARD"."MODELS_TO_SCAN_PK" ENABLE;
*/
	