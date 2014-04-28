package cbit.vcell.modeldb;

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
import org.vcell.util.document.User;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.model.VCMultiBioVisitor;
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

	public BatchTester(SessionLog log) throws Exception {
		super(log);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (2/2/01 3:40:29 PM)
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
								ps.setLong(1,user.getID().longValue());
								ps.setLong(2,vk.longValue());
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

	public void batchScanBioModels(VCMultiBioVisitor databaseVisitor, Writer writer, String statusTable, int chunkSize)
			throws DataAccessException, XmlParseException, SQLException {
				String processHostId = ManagementFactory.getRuntimeMXBean().getName();
				Connection conn = connFactory.getConnection(null);
				System.err.println(conn.getAutoCommit());
				try (Statement statement = conn.createStatement()) {
					String query = "Update "  + statusTable + " set scan_process = '" + processHostId 
							+ "' where scanned = 0 and scan_process is null and rownum <= "
							+ chunkSize;
					int uCount = statement.executeUpdate(query);
					if (uCount > chunkSize) {
						throw new Error("logic / SQL bad");
					}
				}
				ArrayList<ModelIdent> models = new ArrayList<BatchTester.ModelIdent>();
				try (Statement statement = conn.createStatement()) {
					String query = "Select id, user_id, model_id from " + statusTable +
							" where scan_process ='" + processHostId + "' and scanned = 0";
					ResultSet rs = statement.executeQuery(query);
					while (rs.next( )) {
						ModelIdent mi = new ModelIdent(rs);
						models.add(mi);
						System.err.println(mi.statusId);
					}
				}
		
				try
				{
					PrintWriter printWriter = new PrintWriter(writer);
					//start visiting models and writing log
					printWriter.println("Start scanning bio-models......");
					printWriter.println("\n");
					PreparedStatement ps = conn.prepareStatement("Update " + statusTable + " set scanned = 1, good = ? where id = ?");
					for (ModelIdent modelIdent : models) {
						boolean goodModel;
						try {
							User user = new User("", new KeyValue(modelIdent.userId));
							KeyValue modelKey = new KeyValue(modelIdent.modelId);
							BigString bioModelXML = dbServerImpl.getBioModelXML(user,modelKey);
							BioModel storedModel = cbit.vcell.xml.XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
							storedModel.refreshDependencies();
							goodModel = verifyMathDescriptionsUnchanged(storedModel,printWriter);
							if (goodModel) {
								databaseVisitor.setBioModel(storedModel, printWriter);
								for (BioModel bioModel: databaseVisitor) {
									SimulationContext[] simContexts = bioModel.getSimulationContexts();
									for (SimulationContext sc : simContexts) {
										//try {
										sc.createNewMathMapping().getMathDescription();
										/*
									} catch (Exception e) {
										//printWriter.println("\t " + bioModel.getName() + " :: " + sc.getName() + " ----> math regeneration failed.s");
										// e.printStackTrace();
									}
										 */
									}
								}
							}
						}catch (Exception e2){
							log.exception(e2);
							goodModel = false;
							//printWriter.println("======= " + e2.getMessage());
						}
						ps.setInt(1,boolAsInt(goodModel));
						ps.setLong(2,modelIdent.statusId);
						boolean estat = ps.execute();
						if (estat) {
							throw new Error("logic");
						}
						int uc = ps.getUpdateCount();
						if (uc != 1) {
							throw new Error("logic / sql ");
						}
						
					}

					printWriter.close();
				}catch(Exception e)
				{
					e.printStackTrace();
				}
	}
	
	private int boolAsInt(boolean b) {
		return b ? 1: 0;
	}
	
	private static class ModelIdent {
		private final long statusId;
		private final long userId;
		private final long modelId;
		ModelIdent(ResultSet rs) throws SQLException {
			statusId = rs.getLong(ID);
			userId = rs.getLong(USER_ID);
			modelId = rs.getLong(MODEL_ID);
			
		}
		
		
	}
	

}
