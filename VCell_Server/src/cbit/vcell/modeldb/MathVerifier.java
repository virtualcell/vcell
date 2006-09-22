package cbit.vcell.modeldb;
import java.sql.ResultSet;
import java.sql.Statement;
import cbit.util.BigString;
import cbit.util.DataAccessException;
import cbit.util.KeyValue;
import cbit.util.PermissionException;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.util.UserInfo;
import cbit.vcell.math.MathDescription;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.rmi.RemoteException;

import cbit.gui.PropertyLoader;
import java.sql.SQLException;
import cbit.vcell.server.AdminDatabaseServer;
import cbit.vcell.server.solvers.SolverResultSetInfo;
import cbit.vcell.simulation.Simulation;
import cbit.vcell.simulation.SimulationInfo;
import cbit.sql.DBCacheTable;
import cbit.sql.ConnectionFactory;
import java.beans.*;
import java.util.Vector;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.sql.VersionableType;
import cbit.util.BeanUtils;
import java.io.File;


import cbit.vcell.biomodel.BioModelInfo;
import cbit.vcell.math.MathException;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.model.ModelException;
import cbit.vcell.modelapp.SimulationContext;
import cbit.vcell.parser.ExpressionException;
import cbit.sql.KeyFactory;
import cbit.vcell.biomodel.BioModel;
/**
 * Insert the type's description here.
 * Creation date: (2/2/01 2:57:33 PM)
 * @author: Jim Schaff
 */
public class MathVerifier {
	//
	// list of files to discard ???
	//
//	private java.util.Vector garbageFileList = new java.util.Vector();
	//
	// key   = KeyValue(simulation)
	// value = File() object of .log file
	//
//	private java.util.Hashtable resolvedFileHash = new java.util.Hashtable();
	private AdminDatabaseServer adminDbServer = null;
	private cbit.sql.ConnectionFactory conFactory = null;
	private DatabaseServerImpl dbServerImpl = null;
	private cbit.sql.KeyFactory keyFactory = null;
	private DBCacheTable cacheTable = null;
	private cbit.util.SessionLog log = null;
	private cbit.vcell.modeldb.MathDescriptionDbDriver mathDescDbDriver = null;
	private java.util.HashSet skipHash = new java.util.HashSet(); // holds KeyValues of BioModels to skip

/**
 * ResultSetCrawler constructor comment.
 */
public MathVerifier(ConnectionFactory argConFactory, KeyFactory argKeyFactory, AdminDatabaseServer argAdminDbServer, SessionLog argSessionLog, DBCacheTable dbCacheTable) throws DataAccessException, SQLException {
	this.conFactory = argConFactory;
	this.keyFactory = argKeyFactory;
	this.log = argSessionLog;
	this.adminDbServer = argAdminDbServer;
	this.cacheTable = dbCacheTable;
	GeomDbDriver geomDB = new GeomDbDriver(dbCacheTable,argSessionLog);
	this.mathDescDbDriver = new MathDescriptionDbDriver(dbCacheTable,geomDB,argSessionLog);
	this.dbServerImpl = new DatabaseServerImpl(conFactory,keyFactory,dbCacheTable,argSessionLog);
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
public void scan(User users[], boolean bUpdateDatabase, KeyValue bioModelKey, int ordinal) throws MathException, MappingException, SQLException, DataAccessException, ModelException, ExpressionException {
	java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
//	calendar.set(2002,java.util.Calendar.MAY,7+1);
	calendar.set(2002,java.util.Calendar.JULY,1);
	final java.util.Date fluxCorrectionOrDisablingBugFixDate = calendar.getTime();
//	calendar.set(2001,java.util.Calendar.JUNE,13+1);
	calendar.set(2002,java.util.Calendar.JANUARY,1);
	final java.util.Date totalVolumeCorrectionFixDate = calendar.getTime();
	

	for (int i=0;i<users.length;i++){
		User user = users[i];
		BioModelInfo bioModelInfos[] = dbServerImpl.getBioModelInfos(user,false);
		SessionLog userLog = new cbit.util.StdoutSessionLog(user.toString());
		userLog.print("Testing user '"+user+"'");

		//
		// for each bioModel, load BioModelMetaData (to get list of keys for simulations and simContexts
		//
		for (int j = 0; j < bioModelInfos.length; j++){
			//
			// if a single bioModel is requested, then filter all else out
			//
			if (bioModelKey!=null && !bioModelInfos[j].getVersion().getVersionKey().compareEqual(bioModelKey)){
				continue;
			}

			//
			// filter out any bioModelKeys present in the "SkipList"
			//
			if (skipHash.contains(bioModelInfos[j].getVersion().getVersionKey())){
				System.out.println("skipping bioModel with key '"+bioModelInfos[j].getVersion().getVersionKey()+"'");
				continue;
			}

			try {
				//
				// read in the BioModel from the database
				//
				BigString bioModelXML = dbServerImpl.getBioModelXML(user, bioModelInfos[j].getVersion().getVersionKey());
				BioModel bioModelFromDB = cbit.vcell.xml.XmlHelper.XMLToBioModel(bioModelXML.toString());
				BioModel bioModelNewMath = cbit.vcell.xml.XmlHelper.XMLToBioModel(bioModelXML.toString());
				bioModelFromDB.refreshDependencies();
				bioModelNewMath.refreshDependencies();

				//
				// get all Simulations for this model
				//
				Simulation modelSimsFromDB[] = bioModelFromDB.getSimulations();
				SolverResultSetInfo rsetInfos[] = new SolverResultSetInfo[modelSimsFromDB.length];
				for (int k = 0; k < modelSimsFromDB.length; k++){
					try {
						rsetInfos[k] = dbServerImpl.getResultSetInfo(user, modelSimsFromDB[k].getVersion().getVersionKey(),0);
					}catch (Throwable e){
						userLog.exception(e);
						userLog.alert("failure reading ResultSetInfo for Simulation("+modelSimsFromDB[k]+") of BioModel("+bioModelFromDB.getVersion().getVersionKey()+") '"+bioModelFromDB.getName()+"'  "+bioModelFromDB.getVersion().getDate());
					}
				}

				
				//
				// for each application, recompute mathDescription, and verify it is equivalent
				// then check each associated simulation to ensure math overrides are applied in an equivalent manner also.
				//
				SimulationContext simContextsFromDB[] = bioModelFromDB.getSimulationContexts();
				SimulationContext simContextsNewMath[] = bioModelNewMath.getSimulationContexts();
				for (int k = 0; k < simContextsFromDB.length; k++){
					SimulationContext simContextFromDB = simContextsFromDB[k];
					Simulation appSimsFromDB[] = simContextFromDB.getSimulations();
					SimulationContext simContextNewMath = simContextsNewMath[k];
					Simulation appSimsNewMath[] = simContextNewMath.getSimulations();
					String mathEquivalency = null;
					try {
						MathDescription origMathDesc = simContextFromDB.getMathDescription();
						//
						// find out if any simulation belonging to this Application has data
						//
						boolean bApplicationHasData = false;
						for (int l = 0; l < rsetInfos.length; l++){
							if (rsetInfos[l]!=null){
								bApplicationHasData = true;
							}
						}
						//
						// bug compatability mode off
						//
						cbit.vcell.mapping.MembraneStructureAnalyzer.bResolvedFluxCorrectionBug = false;
						cbit.vcell.modelapp.MembraneMapping.bFluxCorrectionBugMode = false;
						cbit.vcell.modelapp.FeatureMapping.bTotalVolumeCorrectionBug = false;
						cbit.vcell.mapping.MembraneStructureAnalyzer.bNoFluxIfFixed = false;

						//
						// make sure geometry is up to date on "simContextNewMath"
						//
						try {
							if (simContextNewMath.getGeometry().getDimension()>0 && simContextNewMath.getGeometry().getGeometrySurfaceDescription().getGeometricRegions()==null){
								simContextNewMath.getGeometry().getGeometrySurfaceDescription().updateAll();
							}
						}catch (Exception e){
							e.printStackTrace(System.out);
						}
						
						//
						// updated mathdescription loaded into copy of biomodel, then test for equivalence.
						//
						cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContextNewMath);
						MathDescription newMathDesc = mathMapping.getMathDescription();
						String issueString = null;
						cbit.util.Issue issues[] = mathMapping.getIssues();
						if (issues!=null && issues.length>0){
							StringBuffer buffer = new StringBuffer("Issues("+issues.length+"):");
							for (int l = 0; l < issues.length; l++){
								buffer.append(" <<"+issues[l].toString()+">>");
							}
							issueString = buffer.toString();
						}
						simContextNewMath.setMathDescription(newMathDesc);

						StringBuffer reasonForDecision = new StringBuffer();
						boolean bEquivalent = cbit.vcell.math.MathDescriptionTest.testIfSame(origMathDesc,newMathDesc,new StringBuffer());
						mathEquivalency = MathDescription.testEquivalency(origMathDesc,newMathDesc,reasonForDecision);
						StringBuffer buffer = new StringBuffer();
						buffer.append(">>>BioModel("+bioModelFromDB.getVersion().getVersionKey()+") '"+bioModelFromDB.getName()+"':"+bioModelFromDB.getVersion().getDate()+", Application("+simContextFromDB.getKey()+") '"+simContextFromDB.getName()+"' <<EQUIV="+mathEquivalency+">>: "+reasonForDecision);
						//
						// update Database Status for SimContext
						//
						if (bUpdateDatabase){
							java.sql.Connection con = null;
							java.sql.Statement stmt = null;
							try {
								con = conFactory.getConnection(new Object());
								stmt = con.createStatement();
								//KeyValue mathKey = origMathDesc.getKey();
								String UPDATESTATUS = "UPDATE "+SimContextStat2Table.table.getTableName()+
													  " SET "  +SimContextStat2Table.table.hasData.getUnqualifiedColName()+" = "+((bApplicationHasData)?(1):(0))+", "+
													            SimContextStat2Table.table.equiv.getUnqualifiedColName()+" = "+(mathEquivalency.equals(MathDescription.MATH_DIFFERENT)?(0):(1))+", "+
													            SimContextStat2Table.table.status.getUnqualifiedColName()+" = '"+cbit.util.TokenMangler.getSQLEscapedString(mathEquivalency+": "+reasonForDecision.toString())+"'"+
													            ((issueString!=null)?(", "+SimContextStat2Table.table.comments.getUnqualifiedColName()+" = '"+cbit.util.TokenMangler.getSQLEscapedString(issueString,255)+"'"):(""))+
													  " WHERE "+SimContextStat2Table.table.simContextRef.getUnqualifiedColName()+" = "+simContextFromDB.getKey();
								int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
								if (numRowsChanged!=1){
									System.out.println("failed to update status");
								}
								con.commit();
								userLog.print("-------------- Update=true, saved 'newMath' for Application '"+simContextFromDB.getName()+"'");
							}catch (SQLException e){
								userLog.exception(e);
								userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE MATH for Application '"+simContextFromDB.getName()+"'");
							}finally{
								if (stmt != null){
									stmt.close();
								}
								con.close();
							}
						}
					}catch (Throwable e){
						log.exception(e); // exception in SimContext
						if (bUpdateDatabase){
							java.sql.Connection con = null;
							java.sql.Statement stmt = null;
							try {
								con = conFactory.getConnection(new Object());
								stmt = con.createStatement();
								//KeyValue mathKey = origMathDesc.getKey();
								String UPDATESTATUS = "UPDATE "+SimContextStat2Table.table.getTableName()+
													  " SET "  +SimContextStat2Table.table.status.getUnqualifiedColName()+" = 'EXCEPTION: "+cbit.util.TokenMangler.getSQLEscapedString(e.toString())+"'"+
													  " WHERE "+SimContextStat2Table.table.simContextRef.getUnqualifiedColName()+" = "+simContextFromDB.getKey();
								int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
								if (numRowsChanged!=1){
									System.out.println("failed to update status with exception");
								}
								con.commit();
								userLog.print("-------------- Update=true, saved exception for Application '"+simContextFromDB.getName()+"'");
							}catch (SQLException e2){
								userLog.exception(e2);
								userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for Application '"+simContextFromDB.getName()+"'");
							}finally{
								if (stmt != null){
									stmt.close();
								}
								con.close();
							}
						}
					}
					//
					// now, verify each associated simulation will apply overrides in an equivalent way
					//
					for (int l = 0; l < appSimsFromDB.length; l++){
						try {
							boolean bSimEquivalent = Simulation.testEquivalency(appSimsNewMath[l], appSimsFromDB[l], mathEquivalency);
							userLog.print("Application("+simContextFromDB.getKey()+") '"+simContextFromDB.getName()+"', "+
										  "Simulation("+modelSimsFromDB[l].getKey()+") '"+modelSimsFromDB[l].getName()+"':"+modelSimsFromDB[l].getVersion().getDate()+
										  "mathEquivalency="+mathEquivalency+", simEquivalency="+bSimEquivalent);
							//
							// update Database Status for Simulation
							//
							if (bUpdateDatabase){
								java.sql.Connection con = null;
								java.sql.Statement stmt = null;
								try {
									con = conFactory.getConnection(new Object());
									stmt = con.createStatement();
									String UPDATESTATUS = "UPDATE "+SimStatTable.table.getTableName()+
														  " SET "  +SimStatTable.table.equiv.getUnqualifiedColName()+" = "+((bSimEquivalent)?(1):(0))+ ", "+
														            SimStatTable.table.status.getUnqualifiedColName()+" = '"+cbit.util.TokenMangler.getSQLEscapedString(mathEquivalency)+"'" +
														  " WHERE "+SimStatTable.table.simRef.getUnqualifiedColName()+" = "+appSimsFromDB[l].getKey();
									int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
									if (numRowsChanged!=1){
										System.out.println("failed to update status");
									}
									con.commit();
									userLog.print("-------------- Update=true, saved 'simulation status for simulation '"+appSimsFromDB[l].getName()+"'");
								}catch (SQLException e){
									userLog.exception(e);
									userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE status for simulation '"+appSimsFromDB[l].getName()+"'");
								}finally{
									if (stmt != null){
										stmt.close();
									}
									con.close();
								}
							}
						}catch (Throwable e){
							log.exception(e); // exception in SimContext
							if (bUpdateDatabase){
								java.sql.Connection con = null;
								java.sql.Statement stmt = null;
								try {
									con = conFactory.getConnection(new Object());
									stmt = con.createStatement();
									//KeyValue mathKey = origMathDesc.getKey();
									String UPDATESTATUS = "UPDATE "+SimStatTable.table.getTableName()+
														  " SET "  +SimStatTable.table.status.getUnqualifiedColName()+" = 'EXCEPTION: "+cbit.util.TokenMangler.getSQLEscapedString(e.toString())+"'"+
														  " WHERE "+SimStatTable.table.simRef.getUnqualifiedColName()+" = "+appSimsFromDB[l].getKey();
									int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
									if (numRowsChanged!=1){
										System.out.println("failed to update status with exception");
									}
									con.commit();
									userLog.print("-------------- Update=true, saved exception for Simulation '"+appSimsFromDB[l].getName()+"'");
								}catch (SQLException e2){
									userLog.exception(e2);
									userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for simulation '"+appSimsFromDB[l].getName()+"'");
								}finally{
									if (stmt != null){
										stmt.close();
									}
									con.close();
								}
							}
						}
					}
				}
			}catch (Throwable e){
	            log.exception(e); // exception in whole BioModel
	            // can't update anything in database, since we don't know what simcontexts are involved
			}
		}	
	}
}



/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
public void scanBioModels(boolean bUpdateDatabase, KeyValue[] bioModelKeys) throws MathException, MappingException, SQLException, DataAccessException, ModelException, ExpressionException {
	java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
//	calendar.set(2002,java.util.Calendar.MAY,7+1);
	calendar.set(2002,java.util.Calendar.JULY,1);
	final java.util.Date fluxCorrectionOrDisablingBugFixDate = calendar.getTime();
//	calendar.set(2001,java.util.Calendar.JUNE,13+1);
	calendar.set(2002,java.util.Calendar.JANUARY,1);
	final java.util.Date totalVolumeCorrectionFixDate = calendar.getTime();
	

	User user = new User("Administrator", new cbit.util.KeyValue("2"));
	SessionLog userLog = new cbit.util.StdoutSessionLog(user.toString());
	for (int i=0;i<bioModelKeys.length;i++){
		BioModelInfo bioModelInfo = dbServerImpl.getBioModelInfo(user,bioModelKeys[i]);
		userLog.print("Testing bioModel with key '"+bioModelKeys[i]+"'");
        java.sql.Connection con = null;
        java.sql.Statement stmt = null;

		try {
			//
			// read in the BioModel from the database
			//
			BigString bioModelXML = dbServerImpl.getBioModelXML(user, bioModelInfo.getVersion().getVersionKey());
			BioModel bioModelFromDB = cbit.vcell.xml.XmlHelper.XMLToBioModel(bioModelXML.toString());
			BioModel bioModelNewMath = cbit.vcell.xml.XmlHelper.XMLToBioModel(bioModelXML.toString());
			bioModelFromDB.refreshDependencies();
			bioModelNewMath.refreshDependencies();

			//
			// get all Simulations for this model
			//
			Simulation modelSimsFromDB[] = bioModelFromDB.getSimulations();
			SolverResultSetInfo rsetInfos[] = new SolverResultSetInfo[modelSimsFromDB.length];
			for (int k = 0; k < modelSimsFromDB.length; k++){
				try {
					rsetInfos[k] = dbServerImpl.getResultSetInfo(user, modelSimsFromDB[k].getVersion().getVersionKey(),0);
				}catch (Throwable e){
					userLog.exception(e);
					userLog.alert("failure reading ResultSetInfo for Simulation("+modelSimsFromDB[k]+") of BioModel("+bioModelFromDB.getVersion().getVersionKey()+") '"+bioModelFromDB.getName()+"'  "+bioModelFromDB.getVersion().getDate());
				}
			}

			
			//
			// for each application, recompute mathDescription, and verify it is equivalent
			// then check each associated simulation to ensure math overrides are applied in an equivalent manner also.
			//
			SimulationContext simContextsFromDB[] = bioModelFromDB.getSimulationContexts();
			SimulationContext simContextsNewMath[] = bioModelNewMath.getSimulationContexts();
			for (int k = 0; k < simContextsFromDB.length; k++){
				SimulationContext simContextFromDB = simContextsFromDB[k];
				Simulation appSimsFromDB[] = simContextFromDB.getSimulations();
				SimulationContext simContextNewMath = simContextsNewMath[k];
				Simulation appSimsNewMath[] = simContextNewMath.getSimulations();
				String mathEquivalency = null;
				try {
					MathDescription origMathDesc = simContextFromDB.getMathDescription();
					//
					// find out if any simulation belonging to this Application has data
					//
					boolean bApplicationHasData = false;
					for (int l = 0; l < rsetInfos.length; l++){
						if (rsetInfos[l]!=null){
							bApplicationHasData = true;
						}
					}
					//
					// bug compatability mode off
					//
					cbit.vcell.mapping.MembraneStructureAnalyzer.bResolvedFluxCorrectionBug = false;
					cbit.vcell.modelapp.MembraneMapping.bFluxCorrectionBugMode = false;
					cbit.vcell.modelapp.FeatureMapping.bTotalVolumeCorrectionBug = false;
					cbit.vcell.mapping.MembraneStructureAnalyzer.bNoFluxIfFixed = false;

					//
					// make sure geometry is up to date on "simContextNewMath"
					//
					try {
						if (simContextNewMath.getGeometry().getDimension()>0 && simContextNewMath.getGeometry().getGeometrySurfaceDescription().getGeometricRegions()==null){
							simContextNewMath.getGeometry().getGeometrySurfaceDescription().updateAll();
						}
					}catch (Exception e){
						e.printStackTrace(System.out);
					}
					
					//
					// updated mathdescription loaded into copy of biomodel, then test for equivalence.
					//
					cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContextNewMath);
					MathDescription newMathDesc = mathMapping.getMathDescription();
					String issueString = null;
					cbit.util.Issue issues[] = mathMapping.getIssues();
					if (issues!=null && issues.length>0){
						StringBuffer buffer = new StringBuffer("Issues("+issues.length+"):");
						for (int l = 0; l < issues.length; l++){
							buffer.append(" <<"+issues[l].toString()+">>");
						}
						issueString = buffer.toString();
					}
					simContextNewMath.setMathDescription(newMathDesc);

					StringBuffer reasonForDecision = new StringBuffer();
					boolean bEquivalent = cbit.vcell.math.MathDescriptionTest.testIfSame(origMathDesc,newMathDesc,new StringBuffer());
					mathEquivalency = MathDescription.testEquivalency(origMathDesc,newMathDesc,reasonForDecision);
					StringBuffer buffer = new StringBuffer();
					buffer.append(">>>BioModel("+bioModelFromDB.getVersion().getVersionKey()+") '"+bioModelFromDB.getName()+"':"+bioModelFromDB.getVersion().getDate()+", Application("+simContextFromDB.getKey()+") '"+simContextFromDB.getName()+"' <<EQUIV="+mathEquivalency+">>: "+reasonForDecision);
					//
					// update Database Status for SimContext
					//
					if (bUpdateDatabase){
						con = null;
						stmt = null;
						try {
							con = conFactory.getConnection(new Object());
							stmt = con.createStatement();
							//KeyValue mathKey = origMathDesc.getKey();
							String UPDATESTATUS = "UPDATE "+SimContextStat2Table.table.getTableName()+
												  " SET "  +SimContextStat2Table.table.hasData.getUnqualifiedColName()+" = "+((bApplicationHasData)?(1):(0))+", "+
												            SimContextStat2Table.table.equiv.getUnqualifiedColName()+" = "+(mathEquivalency.equals(MathDescription.MATH_DIFFERENT)?(0):(1))+", "+
												            SimContextStat2Table.table.status.getUnqualifiedColName()+" = '"+cbit.util.TokenMangler.getSQLEscapedString(mathEquivalency+": "+reasonForDecision.toString())+"'"+
												            ((issueString!=null)?(", "+SimContextStat2Table.table.comments.getUnqualifiedColName()+" = '"+cbit.util.TokenMangler.getSQLEscapedString(issueString,255)+"'"):(""))+
												  " WHERE "+SimContextStat2Table.table.simContextRef.getUnqualifiedColName()+" = "+simContextFromDB.getKey();
							int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
							if (numRowsChanged!=1){
								System.out.println("failed to update status");
							}
							con.commit();
							userLog.print("-------------- Update=true, saved 'newMath' for Application '"+simContextFromDB.getName()+"'");
						}catch (SQLException e){
							userLog.exception(e);
							userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE MATH for Application '"+simContextFromDB.getName()+"'");
						}finally{
							if (stmt != null){
								stmt.close();
							}
							con.close();
						}
					}
				}catch (Throwable e){
					log.exception(e); // exception in SimContext
					if (bUpdateDatabase){
						con = null;
						stmt = null;
						try {
							con = conFactory.getConnection(new Object());
							stmt = con.createStatement();
                            String status = "'EXCEPTION: "+cbit.util.TokenMangler.getSQLEscapedString(e.toString())+"'";
                            if (status.length() > 255) status = status.substring(0,254)+"'";
                            String UPDATESTATUS = "UPDATE "+SimContextStat2Table.table.getTableName()+
					                              " SET "+SimContextStat2Table.table.status.getUnqualifiedColName()+" = "+status+
												  " WHERE "+SimContextStat2Table.table.simContextRef.getUnqualifiedColName()+" = "+simContextFromDB.getKey();
							int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
							if (numRowsChanged!=1){
								System.out.println("failed to update status with exception");
							}
							con.commit();
							userLog.print("-------------- Update=true, saved exception for Application '"+simContextFromDB.getName()+"'");
						}catch (SQLException e2){
							userLog.exception(e2);
							userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for Application '"+simContextFromDB.getName()+"'");
						}finally{
							if (stmt != null){
								stmt.close();
							}
							con.close();
						}
					}
				}
				//
				// now, verify each associated simulation will apply overrides in an equivalent way
				//
				for (int l = 0; l < appSimsFromDB.length; l++){
					try {
						boolean bSimEquivalent = Simulation.testEquivalency(appSimsNewMath[l], appSimsFromDB[l], mathEquivalency);
						userLog.print("Application("+simContextFromDB.getKey()+") '"+simContextFromDB.getName()+"', "+
									  "Simulation("+modelSimsFromDB[l].getKey()+") '"+modelSimsFromDB[l].getName()+"':"+modelSimsFromDB[l].getVersion().getDate()+
									  "mathEquivalency="+mathEquivalency+", simEquivalency="+bSimEquivalent);
						//
						// update Database Status for Simulation
						//
						if (bUpdateDatabase){
							con = null;
							stmt = null;
							try {
								con = conFactory.getConnection(new Object());
								stmt = con.createStatement();
								String UPDATESTATUS = "UPDATE "+SimStatTable.table.getTableName()+
													  " SET "  +SimStatTable.table.equiv.getUnqualifiedColName()+" = "+((bSimEquivalent)?(1):(0))+ ", "+
													            SimStatTable.table.status.getUnqualifiedColName()+" = '"+cbit.util.TokenMangler.getSQLEscapedString(mathEquivalency)+"'" +
													  " WHERE "+SimStatTable.table.simRef.getUnqualifiedColName()+" = "+appSimsFromDB[l].getKey();
								int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
								if (numRowsChanged!=1){
									System.out.println("failed to update status");
								}
								con.commit();
								userLog.print("-------------- Update=true, saved 'simulation status for simulation '"+appSimsFromDB[l].getName()+"'");
							}catch (SQLException e){
								userLog.exception(e);
								userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE status for simulation '"+appSimsFromDB[l].getName()+"'");
							}finally{
								if (stmt != null){
									stmt.close();
								}
								con.close();
							}
						}
					}catch (Throwable e){
						log.exception(e); // exception in SimContext
						if (bUpdateDatabase){
							con = null;
							stmt = null;
							try {
								con = conFactory.getConnection(new Object());
								stmt = con.createStatement();
	                            String status = "'EXCEPTION: "+cbit.util.TokenMangler.getSQLEscapedString(e.toString())+"'";
	                            if (status.length() > 255) status = status.substring(0,254)+"'";
	                            String UPDATESTATUS = "UPDATE "+SimStatTable.table.getTableName()+
                                					  " SET "+SimStatTable.table.status.getUnqualifiedColName()+" = "+status+
													  " WHERE "+SimStatTable.table.simRef.getUnqualifiedColName()+" = "+appSimsFromDB[l].getKey();
								int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
								if (numRowsChanged!=1){
									System.out.println("failed to update status with exception");
								}
								con.commit();
								userLog.print("-------------- Update=true, saved exception for Simulation '"+appSimsFromDB[l].getName()+"'");
							}catch (SQLException e2){
								userLog.exception(e2);
								userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for simulation '"+appSimsFromDB[l].getName()+"'");
							}finally{
								if (stmt != null){
									stmt.close();
								}
								con.close();
							}
						}
					}
				}
			}
		}catch (Throwable e){
            log.exception(e); // exception in whole BioModel
            // can't update anything in database, since we don't know what simcontexts are involved
		}
	}
}



/**
 * Insert the method's description here.
 * Creation date: (2/2/01 3:40:29 PM)
 */
public void scanSimContexts(boolean bUpdateDatabase, KeyValue[] simContextKeys) throws MathException, MappingException, SQLException, DataAccessException, ModelException, ExpressionException {
    java.util.Calendar calendar = java.util.GregorianCalendar.getInstance();
    //	calendar.set(2002,java.util.Calendar.MAY,7+1);
    calendar.set(2002, java.util.Calendar.JULY, 1);
    final java.util.Date fluxCorrectionOrDisablingBugFixDate = calendar.getTime();
    //	calendar.set(2001,java.util.Calendar.JUNE,13+1);
    calendar.set(2002, java.util.Calendar.JANUARY, 1);
    final java.util.Date totalVolumeCorrectionFixDate = calendar.getTime();

    User user = new User("Administrator", new cbit.util.KeyValue("2"));
    SessionLog userLog = new cbit.util.StdoutSessionLog(user.toString());
    for (int i = 0; i < simContextKeys.length; i++) {
        userLog.print("Testing SimContext with key '" + simContextKeys[i] + "'");
        // get biomodel refs
        java.sql.Connection con = null;
        java.sql.Statement stmt = null;
        con = conFactory.getConnection(new Object());
        cbit.vcell.modeldb.BioModelSimContextLinkTable bmscTable = cbit.vcell.modeldb.BioModelSimContextLinkTable.table;
        String sql = "SELECT "+bmscTable.bioModelRef.getQualifiedColName()+
        			 " FROM "+bmscTable.getTableName()+
        			 " WHERE "+bmscTable.simContextRef.getQualifiedColName()+" = "+simContextKeys[i];
        java.util.Vector keys = new java.util.Vector();
        stmt = con.createStatement();
        try {
            ResultSet rset = stmt.executeQuery(sql);
            while (rset.next()) {
                KeyValue key = new KeyValue(rset.getBigDecimal(bmscTable.bioModelRef.getUnqualifiedColName()));
                keys.addElement(key);
            }
        } finally {
			if (stmt != null) {
				stmt.close();
			}
			con.close();
        }

        KeyValue[] bmKeys = (cbit.util.KeyValue[]) cbit.util.BeanUtils.getArray(keys, cbit.util.KeyValue.class);
        try {
			// use the first biomodel...
	        BioModelInfo bioModelInfo = dbServerImpl.getBioModelInfo(user, bmKeys[0]);
	        //
            // read in the BioModel from the database
            //
            BigString bioModelXML = dbServerImpl.getBioModelXML(user, bioModelInfo.getVersion().getVersionKey());
            BioModel bioModelFromDB = cbit.vcell.xml.XmlHelper.XMLToBioModel(bioModelXML.toString());
            BioModel bioModelNewMath = cbit.vcell.xml.XmlHelper.XMLToBioModel(bioModelXML.toString());
            bioModelFromDB.refreshDependencies();
            bioModelNewMath.refreshDependencies();

            //
            // get all Simulations for this model
            //
            Simulation modelSimsFromDB[] = bioModelFromDB.getSimulations();
            SolverResultSetInfo rsetInfos[] = new SolverResultSetInfo[modelSimsFromDB.length];
            for (int k = 0; k < modelSimsFromDB.length; k++) {
                try {
                    rsetInfos[k] = dbServerImpl.getResultSetInfo(user, modelSimsFromDB[k].getVersion().getVersionKey(),0);
                } catch (Throwable e) {
                    userLog.exception(e);
                    userLog.alert("failure reading ResultSetInfo for Simulation("+modelSimsFromDB[k]+") of BioModel("+bioModelFromDB.getVersion().getVersionKey()+") '"+bioModelFromDB.getName()+"'  "+bioModelFromDB.getVersion().getDate());
                }
            }

            //
            // ---> only for the SimContext we started with...
            // recompute mathDescription, and verify it is equivalent
            // then check each associated simulation to ensure math overrides are applied in an equivalent manner also.
            //
            SimulationContext simContextsFromDB[] = bioModelFromDB.getSimulationContexts();
            SimulationContext simContextsNewMath[] = bioModelNewMath.getSimulationContexts();
            SimulationContext simContextFromDB = null;
            SimulationContext simContextNewMath = null;
            for (int k = 0; k < simContextsFromDB.length; k++) {
	            // find it...
	            if (simContextsFromDB[k].getKey().equals(simContextKeys[i])) {
		            simContextFromDB = simContextsFromDB[k];
					simContextNewMath = simContextsNewMath[k];
		            break;
	            }
            }
            if (simContextFromDB == null) {
	            throw new RuntimeException("BioModel referred to by this SimContext does not contain this SimContext");
            } else {
                Simulation appSimsFromDB[] = simContextFromDB.getSimulations();
                Simulation appSimsNewMath[] = simContextNewMath.getSimulations();
                String mathEquivalency = null;
                try {
                    MathDescription origMathDesc = simContextFromDB.getMathDescription();
                    //
                    // find out if any simulation belonging to this Application has data
                    //
                    boolean bApplicationHasData = false;
                    for (int l = 0; l < rsetInfos.length; l++) {
                        if (rsetInfos[l] != null) {
                            bApplicationHasData = true;
                        }
                    }
                    //
                    // bug compatability mode off
                    //
                    cbit.vcell.mapping.MembraneStructureAnalyzer.bResolvedFluxCorrectionBug = false;
                    cbit.vcell.modelapp.MembraneMapping.bFluxCorrectionBugMode = false;
                    cbit.vcell.modelapp.FeatureMapping.bTotalVolumeCorrectionBug = false;
                    cbit.vcell.mapping.MembraneStructureAnalyzer.bNoFluxIfFixed = false;

                    //
                    // make sure geometry is up to date on "simContextNewMath"
                    //
                    try {
                        if (simContextNewMath.getGeometry().getDimension() > 0 && simContextNewMath.getGeometry().getGeometrySurfaceDescription().getGeometricRegions() == null) {
                            simContextNewMath.getGeometry().getGeometrySurfaceDescription().updateAll();
                        }
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                    }

                    //
                    // updated mathdescription loaded into copy of biomodel, then test for equivalence.
                    //
                    cbit.vcell.mapping.MathMapping mathMapping = new cbit.vcell.mapping.MathMapping(simContextNewMath);
                    MathDescription newMathDesc = mathMapping.getMathDescription();
                    String issueString = null;
                    cbit.util.Issue issues[] = mathMapping.getIssues();
                    if (issues != null && issues.length > 0) {
                        StringBuffer buffer = new StringBuffer("Issues(" + issues.length + "):");
                        for (int l = 0; l < issues.length; l++) {
                            buffer.append(" <<" + issues[l].toString() + ">>");
                        }
                        issueString = buffer.toString();
                    }
                    simContextNewMath.setMathDescription(newMathDesc);

                    StringBuffer reasonForDecision = new StringBuffer();
                    boolean bEquivalent = cbit.vcell.math.MathDescriptionTest.testIfSame(origMathDesc, newMathDesc, new StringBuffer());
                    mathEquivalency = MathDescription.testEquivalency(origMathDesc, newMathDesc, reasonForDecision);
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(">>>BioModel("+bioModelFromDB.getVersion().getVersionKey()+") '"+bioModelFromDB.getName()+"':"+bioModelFromDB.getVersion().getDate()+", Application("+simContextFromDB.getKey()+") '"+simContextFromDB.getName()+"' <<EQUIV="+mathEquivalency+">>: "+reasonForDecision);
                    //
                    // update Database Status for SimContext
                    //
                    if (bUpdateDatabase) {
                        con = null;
                        stmt = null;
                        try {
                            con = conFactory.getConnection(new Object());
                            stmt = con.createStatement();
                            //KeyValue mathKey = origMathDesc.getKey();
                            String UPDATESTATUS = "UPDATE "+SimContextStat2Table.table.getTableName()+
				                             	  " SET "+SimContextStat2Table.table.hasData.getUnqualifiedColName()+" = "+((bApplicationHasData)?(1):(0))+", "
    			                       				     +SimContextStat2Table.table.equiv.getUnqualifiedColName()+" = "+(mathEquivalency.equals(MathDescription.MATH_DIFFERENT) ? (0) : (1))+", "
       				                       			     +SimContextStat2Table.table.status.getUnqualifiedColName()+" = '"+cbit.util.TokenMangler.getSQLEscapedString(mathEquivalency+": "+reasonForDecision.toString())+"'"
           				                     		     +((issueString != null) ? (", "+SimContextStat2Table.table.comments.getUnqualifiedColName()+" = '"+cbit.util.TokenMangler.getSQLEscapedString(issueString, 255)+"'") : (""))+
           				                     	  " WHERE "+SimContextStat2Table.table.simContextRef.getUnqualifiedColName()+" = "+simContextFromDB.getKey();
                            int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
                            if (numRowsChanged != 1) {
                                System.out.println("failed to update status");
                            }
                            con.commit();
                            userLog.print("-------------- Update=true, saved 'newMath' for Application '"+simContextFromDB.getName()+"'");
                        } catch (SQLException e) {
                            userLog.exception(e);
                            userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE MATH for Application '"+simContextFromDB.getName()+"'");
                        } finally {
                            if (stmt != null) {
                                stmt.close();
                            }
                            con.close();
                        }
                    }
                } catch (Throwable e) {
                    log.exception(e); // exception in SimContext
                    if (bUpdateDatabase) {
                        con = null;
                        stmt = null;
                        try {
                            con = conFactory.getConnection(new Object());
                            stmt = con.createStatement();
                            String status = "'EXCEPTION: "+cbit.util.TokenMangler.getSQLEscapedString(e.toString())+"'";
                            if (status.length() > 255) status = status.substring(0,254)+"'";
                            String UPDATESTATUS = "UPDATE "+SimContextStat2Table.table.getTableName()+
					                              " SET "+SimContextStat2Table.table.status.getUnqualifiedColName()+" = "+status+
       						                      " WHERE "+ SimContextStat2Table.table.simContextRef.getUnqualifiedColName()+" = "+simContextFromDB.getKey();
                            int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
                            if (numRowsChanged != 1) {
                                System.out.println("failed to update status with exception");
                            }
                            con.commit();
                            userLog.print("-------------- Update=true, saved exception for Application '"+simContextFromDB.getName()+"'");
                        } catch (SQLException e2) {
                            userLog.exception(e2);
                            userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for Application '"+simContextFromDB.getName()+"'");
                        } finally {
                            if (stmt != null) {
                                stmt.close();
                            }
                            con.close();
                        }
                    }
                }
                //
                // now, verify each associated simulation will apply overrides in an equivalent way
                //
                for (int l = 0; l < appSimsFromDB.length; l++) {
                    try {
                        boolean bSimEquivalent = Simulation.testEquivalency(appSimsNewMath[l],appSimsFromDB[l],mathEquivalency);
                        userLog.print("Application("+simContextFromDB.getKey()+") '"+simContextFromDB.getName()+"', "+"Simulation("+modelSimsFromDB[l].getKey()+") '"+modelSimsFromDB[l].getName()+"':"+modelSimsFromDB[l].getVersion().getDate()+"mathEquivalency="+mathEquivalency+", simEquivalency="+bSimEquivalent);
                        //
                        // update Database Status for Simulation
                        //
                        if (bUpdateDatabase) {
                            con = null;
                            stmt = null;
                            try {
                                con = conFactory.getConnection(new Object());
                                stmt = con.createStatement();
                                String UPDATESTATUS = "UPDATE "+SimStatTable.table.getTableName()+
                                					  " SET "+SimStatTable.table.equiv.getUnqualifiedColName()+" = "+((bSimEquivalent) ? (1) : (0))+", "+SimStatTable.table.status.getUnqualifiedColName()+" = '"+cbit.util.TokenMangler.getSQLEscapedString(mathEquivalency)+"'"+
                                					  " WHERE "+SimStatTable.table.simRef.getUnqualifiedColName()+" = "+appSimsFromDB[l].getKey();
                                int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
                                if (numRowsChanged != 1) {
                                    System.out.println("failed to update status");
                                }
                                con.commit();
                                userLog.print("-------------- Update=true, saved 'simulation status for simulation '"+appSimsFromDB[l].getName()+"'");
                            } catch (SQLException e) {
                                userLog.exception(e);
                                userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO UPDATE status for simulation '"+appSimsFromDB[l].getName()+"'");
                            } finally {
                                if (stmt != null) {
                                    stmt.close();
                                }
                                con.close();
                            }
                        }
                    } catch (Throwable e) {
                        log.exception(e); // exception in SimContext
                        if (bUpdateDatabase) {
                            con = null;
                            stmt = null;
                            try {
                                con = conFactory.getConnection(new Object());
                                stmt = con.createStatement();
	                            String status = "'EXCEPTION: "+cbit.util.TokenMangler.getSQLEscapedString(e.toString())+"'";
	                            if (status.length() > 255) status = status.substring(0,254)+"'";
	                            String UPDATESTATUS = "UPDATE "+SimStatTable.table.getTableName()+
                                					  " SET "+SimStatTable.table.status.getUnqualifiedColName()+" = "+status+
                                					  " WHERE "+SimStatTable.table.simRef.getUnqualifiedColName()+" = "+appSimsFromDB[l].getKey();
                                int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
                                if (numRowsChanged != 1) {
                                    System.out.println("failed to update status with exception");
                                }
                                con.commit();
                                userLog.print("-------------- Update=true, saved exception for Simulation '"+appSimsFromDB[l].getName()+"'");
                            } catch (SQLException e2) {
                                userLog.exception(e2);
                                userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for simulation '"+appSimsFromDB[l].getName()+"'");
                            } finally {
                                if (stmt != null) {
                                    stmt.close();
                                }
                                con.close();
                            }
                        }
                    }
                }
        	}
        } catch (Throwable e) {
            log.exception(e); // exception in whole BioModel
	        // update database, since we know the simcontext...
			if (bUpdateDatabase) {
				con = null;
				stmt = null;
				try {
					con = conFactory.getConnection(new Object());
					stmt = con.createStatement();
					//KeyValue mathKey = origMathDesc.getKey();
					String UPDATESTATUS = "UPDATE "+SimContextStat2Table.table.getTableName()+
										  " SET "+SimContextStat2Table.table.status.getUnqualifiedColName()+" = 'BIOMODEL EXCEPTION: "+cbit.util.TokenMangler.getSQLEscapedString(e.toString())+"'"+
										  " WHERE "+ SimContextStat2Table.table.simContextRef.getUnqualifiedColName()+" = "+simContextKeys[i];
					int numRowsChanged = stmt.executeUpdate(UPDATESTATUS);
					if (numRowsChanged != 1) {
						System.out.println("failed to update status with exception");
					}
					con.commit();
					userLog.print("-------------- Update=true, saved exception for Application with key '"+simContextKeys[i]+"'");
				} catch (SQLException e2) {
					userLog.exception(e2);
					userLog.alert("*&*&*&*&*&*&*& Update=true, FAILED TO save exception status for Application with key '"+simContextKeys[i]+"'");
				} finally {
					if (stmt != null) {
						stmt.close();
					}
					con.close();
				}
			}
        }
    }
}


/**
 * Insert the method's description here.
 * Creation date: (6/7/2004 12:01:12 PM)
 * @param simContexts cbit.sql.KeyValue[]
 */
public void setSkipList(KeyValue[] simContextKeys) {
	for (int i = 0; i < simContextKeys.length; i++){
		skipHash.add(simContextKeys[i]);
	}
}
}