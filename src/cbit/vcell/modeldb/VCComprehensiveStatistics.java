package cbit.vcell.modeldb;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.ode.gui.SimulationStatus;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class VCComprehensiveStatistics {
	private OracleConnectionPoolDataSource oracleConnection = null;
	private static String dbConnectURL = "jdbc:oracle:thin:@dbs4.vcell.uchc.edu:1521:orcl";
	private static String dbDriverName = "oracle.jdbc.driver.OracleDriver";
	private static String dbUserid = "vcell";
	private static String dbPassword = "cbittech";
	private ArrayList<UserStat> userStatList = new ArrayList<UserStat>();
	private static String simDataDir = "\\\\cfs01\\raid\\vcell\\users\\";
	
	private int timeConstraint = 6 * MONTH_IN_DAY;
	private static int MONTH_IN_DAY = 30; // in days
	private static long DAY_IN_MS = 24 * 3600 * 1000;
	private static long MONTH_IN_MS = MONTH_IN_DAY * DAY_IN_MS;
	
	private ArrayList<String> internalUsers = new ArrayList<String>();
	private ArrayList<String> internalDeveloper = new ArrayList<String>();
	
	private DatabaseServerImpl dbServerImpl = null;
	private SessionLog log = null;
	private LocalAdminDbServer localAdminDbServer = null;
	private ArrayList<User> userList = new ArrayList<User>();
	private PrintWriter statOutputPW = null;
	
	class UserStat {
		String username;
		int id;
		Date lastLogin;
		boolean isDeveloper = false;
		boolean isInternal = false;
		private UserStat(int userid, String username, Date lastLogin) {
			super();
			this.id = userid;
			this.username = username;
			this.lastLogin = lastLogin;
			
		}
	}
	
	VCComprehensiveStatistics() throws Exception {
		new PropertyLoader();
		
		DatabasePolicySQL.bSilent = true;
		DatabasePolicySQL.bAllowAdministrativeAccess = true;
		
		SessionLog sessionLog = new StdoutSessionLog("VCComprehensiveStatistics");
		ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
		KeyFactory keyFactory = new OracleKeyFactory();
		DbDriver.setKeyFactory(keyFactory);
		
		localAdminDbServer = new LocalAdminDbServer(conFactory, keyFactory, sessionLog);
		dbServerImpl = new DatabaseServerImpl(conFactory,keyFactory,sessionLog);
		
		oracleConnection = new OracleConnectionPoolDataSource();
		oracleConnection.setURL(dbConnectURL);
		oracleConnection.setUser(dbUserid);
		oracleConnection.setPassword(dbPassword);
		
		internalDeveloper.add("fgao");
		internalDeveloper.add("anu");
		internalDeveloper.add("liye");
		internalDeveloper.add("frm");
		internalDeveloper.add("ion");
		internalDeveloper.add("danv");
		internalDeveloper.add("curoli");
		internalDeveloper.add("schaff");
		internalDeveloper.add("jdutton");
		internalDeveloper.add("tutorial");
		internalDeveloper.add("Education");
		internalDeveloper.add("CellMLRep");
		internalDeveloper.add("raquel");
		internalDeveloper.add("vcelltestaccount");
		
		internalUsers.add("les");
		internalUsers.add("boris");
		internalUsers.add("mblinov");
		internalUsers.add("ACowan");
		internalUsers.add("ignovak");
		internalUsers.add("DCResasco");
		internalUsers.add("pavelkr");
		internalUsers.add("pjmichal");
		internalUsers.add("falkenbe");
	}
	public static void main(String[] args) {
		try {
			VCComprehensiveStatistics vcstat = new VCComprehensiveStatistics();
			vcstat.startStatistics();			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	
	public void retrieveUsers() throws DataAccessException {
		if (userList.size() == 0) {
			UserInfo[] allUserInfos = localAdminDbServer.getUserInfos();
			for (UserInfo userInfo : allUserInfos) {
				userList.add(new User(userInfo.userid, userInfo.id));
			}
		}
	}
	
	public void startStatistics() {
		try {
			statOutputPW = new PrintWriter("vcstat.txt");
			
			statOutputPW.println("1. User Statistics");
			statOutputPW.println("====================================================");
			collectUserStats();
			statOutputPW.println("1. BioModel Statistics");
			statOutputPW.println("====================================================");
			collectBioModelStats();
			collectMathModelStats();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (statOutputPW != null) {
				statOutputPW.close();
			}
			System.exit(0);
		}
	}
	
	private void collectMathModelStats() throws DataAccessException {
		// TODO Auto-generated method stub
		retrieveUsers();
		
	}
	private void collectBioModelStats() throws DataAccessException {
		retrieveUsers();
		int count1mon_inside = 0;
		int count3mon_inside = 0;
		int count6mon_inside = 0;
		int count1mon_outside = 0;
		int count3mon_outside = 0;
		int count6mon_outside = 0;
		
		int count1mon_outside_simcomplete = 0;
		int count3mon_outside_simcomplete = 0;
		int count6mon_outside_simcomplete = 0;
		int count1mon_inside_simcomplete = 0;
		int count3mon_inside_simcomplete = 0;
		int count6mon_inside_simcomplete = 0;
		for (User user : userList) {
			if (!internalDeveloper.contains(user.getName())) {
				BioModelInfo bioModelInfos[] = dbServerImpl.getBioModelInfos(user,false);
				for (BioModelInfo bmi : bioModelInfos){
					Date createDate = bmi.getVersion().getDate();
					long t = System.currentTimeMillis() - createDate.getTime();
					if (t < 1 * MONTH_IN_MS) {
						if (internalUsers.contains(user.getName())) {
							count1mon_inside ++;
							count3mon_inside ++;
							count6mon_inside ++;
						} else {
							count1mon_outside ++;
							count3mon_outside ++;
							count6mon_outside ++;							
						}
					} else if (t < 3  * MONTH_IN_MS) {
						if (internalUsers.contains(user.getName())) {
							count3mon_inside ++;
							count6mon_inside ++;
						} else {
							count3mon_outside ++;
							count6mon_outside ++;							
						}
					} else if (t < 6 * MONTH_IN_MS) {
						if (internalUsers.contains(user.getName())) {
							count6mon_inside ++;
						} else {
							count6mon_outside ++;							
						}
					}
					try {
						BigString bioModelXML = dbServerImpl.getBioModelXML(user, bmi.getVersion().getVersionKey());
						BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
						bioModel.refreshDependencies();
						
						boolean bHasCompletedSim = false;
						for (Simulation sim : bioModel.getSimulations()) {
							SimulationStatus ss = dbServerImpl.getSimulationStatus(sim.getKey());
							for (int scan = 0; scan < sim.getScanCount(); scan ++) {
								if (ss.getJobStatus(scan) != null && ss.getJobStatus(scan).getSchedulerStatus() == SimulationJobStatus.SCHEDULERSTATUS_COMPLETED) {									
									bHasCompletedSim = true;
									break;
								}
							}
							if (bHasCompletedSim) {
								break;
							}
						}
						if (bHasCompletedSim) {
							if (t < 1 * MONTH_IN_MS) {
								if (internalUsers.contains(user.getName())) {
									count1mon_inside_simcomplete ++;
									count3mon_inside_simcomplete ++;
									count6mon_inside_simcomplete ++;
								} else {
									count1mon_outside_simcomplete ++;
									count3mon_outside_simcomplete ++;
									count6mon_outside_simcomplete ++;							
								}
							} else if (t < 3  * MONTH_IN_MS) {
								if (internalUsers.contains(user.getName())) {
									count3mon_inside_simcomplete ++;
									count6mon_inside_simcomplete ++;
								} else {
									count3mon_outside_simcomplete ++;
									count6mon_outside_simcomplete ++;							
								}
							} else if (t < 6 * MONTH_IN_MS) {
								if (internalUsers.contains(user.getName())) {
									count6mon_inside_simcomplete ++;
								} else {
									count6mon_outside_simcomplete ++;							
								}
							}
						}
					} catch (Exception e2){
						e2.printStackTrace(System.out);
					}
				}
			}
		}
		statOutputPW.println("\tOutside users");
		statOutputPW.println("========================================");
		statOutputPW.println("number of biomodels created in last 1 month  :\t" + count1mon_outside);
		statOutputPW.println("number of biomodels created in last 3 months :\t" + count3mon_outside);
		statOutputPW.println("number of biomodels created in last 6 months :\t" + count6mon_outside);
		statOutputPW.println("number of biomodels that has at least 1 completed simulation in last 1 month  :\t" + count1mon_outside_simcomplete);
		statOutputPW.println("number of biomodels that has at least 1 completed simulation created in last 3 months :\t" + count3mon_outside_simcomplete);
		statOutputPW.println("number of biomodels that has at least 1 completed simulation created in last 6 months :\t" + count6mon_outside_simcomplete);
		
		statOutputPW.println("\tInside users");
		statOutputPW.println("========================================");
		statOutputPW.println("number of biomodels created in last 1 month  :\t" + count1mon_inside);
		statOutputPW.println("number of biomodels created in last 3 months :\t" + count3mon_inside);
		statOutputPW.println("number of biomodels created in last 6 months :\t" + count6mon_inside);
		statOutputPW.println("number of biomodels that has at least 1 completed simulation in last 1 month  :\t" + count1mon_inside_simcomplete);
		statOutputPW.println("number of biomodels that has at least 1 completed simulation created in last 3 months :\t" + count3mon_inside_simcomplete);
		statOutputPW.println("number of biomodels that has at least 1 completed simulation created in last 6 months :\t" + count6mon_inside_simcomplete);		
	}
	
	private void collectUserStats() throws SQLException {
		Connection con = oracleConnection.getConnection();
		Statement stmt = con.createStatement();
		try {			
			String sql = "select vc_userinfo.id, vc_userinfo.userid, vc_userstat.lastlogin from vc_userinfo, vc_userstat where vc_userstat.userref=vc_userinfo.id";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {		
				UserStat user = new UserStat(rset.getInt(1), rset.getString(2), VersionTable.getDate(rset, "lastlogin"));
				if (internalDeveloper.contains(user.username) || user.username.startsWith("fgao")) {
					user.isDeveloper = true;
				}
				if (internalUsers.contains(user.username)) {
					user.isInternal = true;
				}
				userStatList.add(user);
			}
			rset.close();			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		} finally{
			stmt.close();
			con.close();
		}
		
		int count1mon = 0;
		int count3mon = 0;
		int count6mon = 0;
		for (UserStat ui : userStatList) {
			if (!ui.isDeveloper) {
				if (!ui.isInternal && ui.lastLogin != null) {
					long t = System.currentTimeMillis() - ui.lastLogin.getTime();
					if (t < 1 * MONTH_IN_MS) {
						count1mon ++;
						count3mon ++;
						count6mon ++;
					} else if (t < 3  * MONTH_IN_MS) {
						count3mon ++;
						count6mon ++;
					} else if (t < 6 * MONTH_IN_MS) {
						count6mon ++;
					}
				}
			}
		}
		statOutputPW.println("\tOutside users");
		statOutputPW.println("========================================");
		statOutputPW.println("number of users in last 1 month  :\t" + count1mon);
		statOutputPW.println("number of users in last 3 months :\t" + count3mon);
		statOutputPW.println("number of users in last 6 months :\t" + count6mon);
		
		statOutputPW.println("Internal users (totally " + internalUsers.size() + ") are : ");
		int i = 1;
		for (String s : internalUsers) {
			statOutputPW.print("\t\t" + s);
			if (i++ % 5 == 0) {
				System.out.println();
			}
		}
		statOutputPW.println();
		count1mon = 0;
		count3mon = 0;
		count6mon = 0;
		for (UserStat ui : userStatList) {
			if (!ui.isDeveloper) {
				if (ui.isInternal && ui.lastLogin != null) {
					long t = System.currentTimeMillis() - ui.lastLogin.getTime();
					if (t < 1 * MONTH_IN_MS) {
						count1mon ++;
						count3mon ++;
						count6mon ++;
					} else if (t < 3  * MONTH_IN_MS) {
						count3mon ++;
						count6mon ++;
					} else if (t < 6 * MONTH_IN_MS) {
						count6mon ++;
					}
				}
			}
		}
		statOutputPW.println("\tInside users");
		statOutputPW.println("========================================");		
		statOutputPW.println("number of users in last 1 month  :\t" + count1mon);
		statOutputPW.println("number of users in last 3 months :\t" + count3mon);
		statOutputPW.println("number of users in last 6 months :\t" + count6mon);
	}
}
