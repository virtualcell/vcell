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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.SimulationJobStatusPersistent.SchedulerStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

public class VCComprehensiveStatistics {
	private OracleConnectionPoolDataSource oracleConnection = null;
	private ArrayList<UserStat> userStatList = new ArrayList<UserStat>();
	
	private static double MONTH_IN_DAY = 365.0/12; // in days
	private static long MINUTE_IN_MS = 60 * 1000;
	private static long HOUR_IN_MS = 60 * MINUTE_IN_MS;
	private static long DAY_IN_MS = 24 * HOUR_IN_MS;
	private static double MONTH_IN_MS = MONTH_IN_DAY * DAY_IN_MS;
	
	private ArrayList<String> internalUsers = new ArrayList<String>();
	private ArrayList<String> internalDeveloper = new ArrayList<String>();
	
	private DatabaseServerImpl dbServerImpl = null;
	private SessionLog log = null;
	private LocalAdminDbServer localAdminDbServer = null;
	private ArrayList<User> userList = new ArrayList<User>();
	private ArrayList<String> userConstraintList = new ArrayList<String>();
	private PrintWriter statOutputPW = null;
	private int itemCount = 0;
	
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
	ModelStat[] bioModelStats = null;
	ModelStat[] mathModelStats = null;

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
		oracleConnection.setURL(PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL));
		oracleConnection.setUser(PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid));
		oracleConnection.setPassword(PropertyLoader.getRequiredProperty(PropertyLoader.dbPassword));
		
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
		
		bioModelStats = new ModelStat[2];
		bioModelStats[0] = new ModelStat("Internal users");
		bioModelStats[1] = new ModelStat("Outside users");
		mathModelStats = new ModelStat[2];
		mathModelStats[0] = new ModelStat("Internal users");
		mathModelStats[1] = new ModelStat("Outside users");
	}
	public static void main(String[] args) {
		try {
			if (args.length != 2) {
				System.out.println("Usage : VCComprehensiveStatistics end_date #_of_retro_months ");
				System.out.println("eg : VCComprehensiveStatistics 12/31/2009 6]");
				System.exit(1);
			}
			Date startDate = null;
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			Date endDate = df.parse(args[0]);
			int numMonths = Integer.parseInt(args[1]);
			startDate = new Date(endDate.getTime() - (long)(numMonths * MONTH_IN_MS));
			
			VCComprehensiveStatistics vcstat = new VCComprehensiveStatistics();
			vcstat.startStatistics(startDate, endDate);			
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
		} finally {
			System.exit(0);
		}
	}
	
	private void retrieveUsers() throws DataAccessException {
		if (userList.size() == 0) {
			UserInfo[] allUserInfos = localAdminDbServer.getUserInfos();
			for (UserInfo userInfo : allUserInfos) {
				userList.add(new User(userInfo.userid, userInfo.id));
			}
		}
	}
	
	public void startStatistics(Date startDate, Date endDate) throws DataAccessException, FileNotFoundException, SQLException {
		try {			
			long startDateInMs = startDate.getTime();
			long endDateInMs = endDate.getTime();
			
			DateFormat df = DateFormat.getDateInstance();
			DateFormat sdf = new SimpleDateFormat("MMM_dd_yyyy");
			itemCount = 0;
			statOutputPW = new PrintWriter("VCStatistics_" + PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid) + "_from_" + sdf.format(startDate) + "_to_" + sdf.format(endDate) + ".txt");
			statOutputPW.println("Note: developers are excluded from this statistics");
			statOutputPW.println();
			collectUserStats(startDateInMs, endDateInMs);
			
			statOutputPW.println("\nStatistics between " + df.format(startDate) + " and " + df.format(endDate));
			statOutputPW.println("-------------------------------------------------------------------------");
			statOutputPW.println();
			statOutputPW.flush();
			
			collectBioModelStats(startDateInMs, endDateInMs);
			collectMathModelStats(startDateInMs, endDateInMs);
			
			statOutputPW.println("\nTotal (both internal and outside users");
			statOutputPW.println("-------------------------------------------------------------------------");
			statOutputPW.println("total users who used vcell in this period and later: " + userConstraintList.size());
			int totalBioModels = bioModelStats[0].count_model + bioModelStats[1].count_model;
			int totalMathModels = mathModelStats[0].count_model + mathModelStats[1].count_model;
			int totalApplications = bioModelStats[0].count_app_deterministic + bioModelStats[1].count_app_deterministic
				+ bioModelStats[0].count_app_stochastic + bioModelStats[1].count_app_stochastic;
			int totalBioModelSimulations = bioModelStats[0].count_sim_deterministic + bioModelStats[1].count_sim_deterministic
				+ bioModelStats[0].count_sim_stochastic + bioModelStats[1].count_sim_stochastic;
			int totalMathModelSimulations = mathModelStats[0].count_sim_deterministic + mathModelStats[1].count_sim_deterministic
				+ mathModelStats[0].count_sim_stochastic + mathModelStats[1].count_sim_stochastic;
			statOutputPW.println("total BioModels saved : " + totalBioModels);
			statOutputPW.println("total BioModel Applications saved : " + totalApplications);
			statOutputPW.println("total BioModel simulations run : " + totalBioModelSimulations);
			statOutputPW.println("total MathModel saved : " + totalMathModels);
			statOutputPW.println("total MathModel simulations run : " + totalMathModelSimulations);
			statOutputPW.println("total BioModels and MathModels : " + (totalBioModels + totalMathModels));
			statOutputPW.println("total simulations run : " + (totalBioModelSimulations + totalMathModelSimulations));
			statOutputPW.flush();
		} finally {
			if (statOutputPW != null) {
				statOutputPW.close();
			}
		}
	}
	
	private Object waitForModel(final BigString modelXML,final boolean bMath){
		final Object[] modelArr = new Object[] {null};
		final Exception[] excArr = new Exception[] {null};
		final Boolean[] flagArr = new Boolean[] {false};
		long startTime = System.currentTimeMillis();
		Thread processThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					if(bMath){
						modelArr[0] = XmlHelper.XMLToMathModel(new XMLSource(modelXML.toString()));
						((MathModel)modelArr[0]).refreshDependencies();				
					}else{
						modelArr[0] = XmlHelper.XMLToBioModel(new XMLSource(modelXML.toString()));
						((BioModel)modelArr[0]).refreshDependencies();
					}
					flagArr[0] = true;
				}catch(Exception e){
					e.printStackTrace();
					excArr[0] = e;
				}
			}
		});
		processThread.start();
		boolean bOK = false;
		while(true){
			if((System.currentTimeMillis()-startTime) > 60*1000){
				break;
			}
			if(excArr[0] != null){
				break;
			}
			if(flagArr[0]){
				bOK = true;
				break;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!bOK){
			return null;
		}
		if(bMath){
			return (MathModel)modelArr[0];
		}else{
			return (BioModel)modelArr[0];
		}

	}
	private void collectMathModelStats(long startDateInMs, long endDateInMs) throws DataAccessException {
		retrieveUsers();
		for (User user : userList) {
			if (!userConstraintList.contains(user.getName())) {
				continue;
			}
			if (!internalDeveloper.contains(user.getName())) {
				boolean bInternal = internalUsers.contains(user.getName());
				ModelStat modelStat = mathModelStats[bInternal ? 0 : 1];
				MathModelInfo mathModelInfos[] = dbServerImpl.getMathModelInfos(user,false);
				for (MathModelInfo mmi : mathModelInfos){
					Date createDate = mmi.getVersion().getDate();
					long t = createDate.getTime();
//if(!mmi.getVersion().getVersionKey().toString().equals("")){
//					continue;
//}
					if (t < startDateInMs || t > endDateInMs) {
						continue;
					}
//					modelStat.count_model ++;
					try {
						BigString mathModelXML = dbServerImpl.getMathModelXML(user, mmi.getVersion().getVersionKey());
						MathModel mathModel = (MathModel)waitForModel(mathModelXML, true);
						if(mathModel == null){
							System.out.println("----------          Skipped MathModel "+mmi.getVersion()+"          ----------");
							continue;
						}
						modelStat.count_model ++;								
						boolean bHasCompletedSim = false;
						for (Simulation sim : mathModel.getSimulations()) {
							SimulationStatusPersistent ss = dbServerImpl.getSimulationStatus(sim.getKey());
							for (int scan = 0; scan < sim.getScanCount(); scan ++) {
								SimulationJobStatusPersistent jobStatus = ss.getJobStatus(scan);
								if (jobStatus != null) {
									if (jobStatus.getSchedulerStatus() == SchedulerStatus.COMPLETED) {								
										bHasCompletedSim = true;
										long elapsed = jobStatus.getEndDate().getTime() - jobStatus.getStartDate().getTime();
										if (elapsed < 2 * MINUTE_IN_MS) {
											modelStat.runningTimeHistogram[0] ++;
										} else if (elapsed < 5 * MINUTE_IN_MS) {
											modelStat.runningTimeHistogram[1] ++;
										} else if (elapsed < 20 * MINUTE_IN_MS) {
											modelStat.runningTimeHistogram[2] ++;
										} else if (elapsed < HOUR_IN_MS) {
											modelStat.runningTimeHistogram[3] ++;
										} else if (elapsed < DAY_IN_MS) {
											modelStat.runningTimeHistogram[4] ++;
										} else {
											modelStat.runningTimeHistogram[5] ++;
										}
									}
									int dimension = sim.getMathDescription().getGeometry().getDimension();
									modelStat.count_geoDimSim[dimension] ++;
									if (sim.getMathDescription().isNonSpatialStoch()) {
										modelStat.count_sim_stochastic ++;
									} else {
										modelStat.count_sim_deterministic ++;
									}
									if (dimension > 0) {
										if (sim.getSolverTaskDescription().getSolverDescription().isSemiImplicitPdeSolver()) {
											modelStat.count_semiSim ++;
										} else {
											modelStat.count_fullySim ++;
										}
									}
								}
								
							}
						}
						if (bHasCompletedSim) {
							modelStat.count_model_simcomplete ++;
						}
					} catch (Exception e2){
						e2.printStackTrace(System.out);
					}
				}
			}
		}
		itemCount ++;
		statOutputPW.println(itemCount + ". MathModel Statistics ");
		statOutputPW.println("====================================================");
				
		for (ModelStat modelStat : mathModelStats) {
			statOutputPW.println("\t" + modelStat.title);
			statOutputPW.println("========================================");
			statOutputPW.println("number of mathmodels saved :\t" + modelStat.count_model);
			statOutputPW.println("number of mathmodels that has at least 1 completed simulation :\t" + modelStat.count_model_simcomplete);
			statOutputPW.println();
			
			statOutputPW.println("Simulation statistics (including all simulations (stopped, failed, completed) :");
			statOutputPW.println("number of run simulation ODE :\t" + modelStat.count_geoDimSim[0]);
			statOutputPW.println("number of run simulation 1D :\t" + modelStat.count_geoDimSim[1]);
			statOutputPW.println("number of run simulation 2D :\t" + modelStat.count_geoDimSim[2]);
			statOutputPW.println("number of run simulation 3D :\t" + modelStat.count_geoDimSim[3]);
			statOutputPW.println("number of run simulation Semi-Implicit :\t" + modelStat.count_semiSim);
			statOutputPW.println("number of run simulation Fully-Implicit :\t" + modelStat.count_fullySim);
			statOutputPW.println("number of run simulation stochastic :\t" + modelStat.count_sim_stochastic);
			statOutputPW.println("number of run simulation deterministic :\t" + modelStat.count_sim_deterministic);
			statOutputPW.println();
			
			statOutputPW.println("Running time histogram for completed simulations only:");
			statOutputPW.println("0 ~ 2min:\t" + modelStat.runningTimeHistogram[0]);
			statOutputPW.println("2 ~ 5min:\t" + modelStat.runningTimeHistogram[1]);
			statOutputPW.println("5 ~ 20min:\t" + modelStat.runningTimeHistogram[2]);
			statOutputPW.println("20min ~ 1hr:\t" + modelStat.runningTimeHistogram[3]);
			statOutputPW.println("1hr ~ 1day:\t" + modelStat.runningTimeHistogram[4]);
			statOutputPW.println(">1day:\t" + modelStat.runningTimeHistogram[5]);			
			statOutputPW.println();
			
			statOutputPW.println();
			statOutputPW.flush();
		}		
	}
	private static class ModelStat {
		String title;
		int count_model = 0;
		int count_geoDimApplications[] = new int[4];
		int count_app_stochastic = 0;
		int count_app_deterministic = 0;
		int count_model_simcomplete = 0;
		int count_semiSim = 0;
		int count_fullySim = 0;
		int count_geoDimSim[] = new int[4];
		int count_sim_stochastic = 0;
		int count_sim_deterministic = 0;
		int runningTimeHistogram[] = new int[6]; // 0-2,2-5,5-20,20-1hr,1hr-1day,>1day
		
		ModelStat(String t) {
			title = t;
		}
	}
	private void collectBioModelStats(long startDateInMs, long endDateInMs) throws DataAccessException {
		retrieveUsers();
		for (User user : userList) {
			if (!userConstraintList.contains(user.getName())) {
				continue;
			}
			if (!internalDeveloper.contains(user.getName())) {
				boolean bInternal = internalUsers.contains(user.getName());
				ModelStat modelStat = bioModelStats[bInternal ? 0 : 1];
				BioModelInfo bioModelInfos[] = dbServerImpl.getBioModelInfos(user,false);
				for (BioModelInfo bmi : bioModelInfos){
					Date createDate = bmi.getVersion().getDate();
					long t = createDate.getTime();
//if(!bmi.getVersion().getVersionKey().toString().equals("84787242")){
//	continue;
//}
					if (t < startDateInMs || t > endDateInMs) {
						continue;
					}
//					modelStat.count_model ++;
					try {
						BigString bioModelXML = dbServerImpl.getBioModelXML(user, bmi.getVersion().getVersionKey());
						BioModel bioModel = (BioModel)waitForModel(bioModelXML, false);
						if(bioModel == null){
							System.out.println("----------          Skipped BioModel "+bmi.getVersion()+"          ----------");
							continue;
						}
						modelStat.count_model ++;
						for (SimulationContext simContext : bioModel.getSimulationContexts()) {
							modelStat.count_geoDimApplications[simContext.getGeometry().getDimension()] ++;
							if (simContext.isStoch()) {
								modelStat.count_app_stochastic ++;
							} else {
								modelStat.count_app_deterministic ++;
							}
						}
						boolean bHasCompletedSim = false;
						for (Simulation sim : bioModel.getSimulations()) {
							SimulationStatusPersistent ss = dbServerImpl.getSimulationStatus(sim.getKey());
							if (ss != null) {
								for (int scan = 0; scan < sim.getScanCount(); scan ++) {
									SimulationJobStatusPersistent jobStatus = ss.getJobStatus(scan);
									if (jobStatus != null) {
										if (jobStatus.getSchedulerStatus() == SchedulerStatus.COMPLETED) {								
											bHasCompletedSim = true;
											long elapsed = jobStatus.getEndDate().getTime() - jobStatus.getStartDate().getTime();
											if (elapsed < 2 * MINUTE_IN_MS) {
												modelStat.runningTimeHistogram[0] ++;
											} else if (elapsed < 5 * MINUTE_IN_MS) {
												modelStat.runningTimeHistogram[1] ++;
											} else if (elapsed < 20 * MINUTE_IN_MS) {
												modelStat.runningTimeHistogram[2] ++;
											} else if (elapsed < HOUR_IN_MS) {
												modelStat.runningTimeHistogram[3] ++;
											} else if (elapsed < DAY_IN_MS) {
												modelStat.runningTimeHistogram[4] ++;
											} else {
												modelStat.runningTimeHistogram[5] ++;
											}
										}
										int dimension = sim.getMathDescription().getGeometry().getDimension();
										modelStat.count_geoDimSim[dimension] ++;
										if (sim.getMathDescription().isNonSpatialStoch() || sim.getMathDescription().isSpatialStoch()) {
											modelStat.count_sim_stochastic ++;
										} else {
											modelStat.count_sim_deterministic ++;
										}
										if (dimension > 0) {
											if (sim.getSolverTaskDescription().getSolverDescription().isSemiImplicitPdeSolver()) {
												modelStat.count_semiSim ++;
											} else {
												modelStat.count_fullySim ++;
											}
										}
									}
									
								}
							}
						}
						if (bHasCompletedSim) {
							modelStat.count_model_simcomplete ++;
						}
					} catch (Exception e2){
						e2.printStackTrace(System.out);
					}
				}
			}
		}
		itemCount ++;
		statOutputPW.println(itemCount + ". BioModel Statistics ");
		statOutputPW.println("====================================================");
				
		for (ModelStat modelStat : bioModelStats) {
			statOutputPW.println("\t" + modelStat.title);
			statOutputPW.println("========================================");
			statOutputPW.println("number of biomodels saved :\t" + modelStat.count_model);
			statOutputPW.println("number of biomodels that has at least 1 completed simulation :\t" + modelStat.count_model_simcomplete);
			statOutputPW.println();
			
			statOutputPW.println("Application statistics :");
			statOutputPW.println("number of application ODE :\t" + modelStat.count_geoDimApplications[0]);
			statOutputPW.println("number of application 1D :\t" + modelStat.count_geoDimApplications[1]);
			statOutputPW.println("number of application 2D :\t" + modelStat.count_geoDimApplications[2]);
			statOutputPW.println("number of application 3D :\t" + modelStat.count_geoDimApplications[3]);
			statOutputPW.println("number of application stochastic :\t" + modelStat.count_app_stochastic);
			statOutputPW.println("number of application deterministic :\t" + modelStat.count_app_deterministic);
			statOutputPW.println();
			
			statOutputPW.println("Simulation statistics (including all simulations (stopped, failed, completed) :");
			statOutputPW.println("number of run simulation ODE :\t" + modelStat.count_geoDimSim[0]);
			statOutputPW.println("number of run simulation 1D :\t" + modelStat.count_geoDimSim[1]);
			statOutputPW.println("number of run simulation 2D :\t" + modelStat.count_geoDimSim[2]);
			statOutputPW.println("number of run simulation 3D :\t" + modelStat.count_geoDimSim[3]);
			statOutputPW.println("number of run simulation Semi-Implicit :\t" + modelStat.count_semiSim);
			statOutputPW.println("number of run simulation Fully-Implicit :\t" + modelStat.count_fullySim);
			statOutputPW.println("number of run simulation stochastic :\t" + modelStat.count_sim_stochastic);
			statOutputPW.println("number of run simulation deterministic :\t" + modelStat.count_sim_deterministic);
			statOutputPW.println();
			
			statOutputPW.println("Running time histogram for completed simulations only:");
			statOutputPW.println("0 ~ 2min:\t" + modelStat.runningTimeHistogram[0]);
			statOutputPW.println("2 ~ 5min:\t" + modelStat.runningTimeHistogram[1]);
			statOutputPW.println("5 ~ 20min:\t" + modelStat.runningTimeHistogram[2]);
			statOutputPW.println("20min ~ 1hr:\t" + modelStat.runningTimeHistogram[3]);
			statOutputPW.println("1hr ~ 1day:\t" + modelStat.runningTimeHistogram[4]);
			statOutputPW.println(">1day:\t" + modelStat.runningTimeHistogram[5]);			
			statOutputPW.println();
			
			statOutputPW.println();
			statOutputPW.flush();
		}
	}
	
	private void collectUserStats(long startDateInMs, long endDateInMs) throws SQLException {
		Connection con = oracleConnection.getConnection();
		Statement stmt = con.createStatement();
		try {			
			String sql = "select vc_userinfo.id, vc_userinfo.userid, vc_userstat.lastlogin from vc_userinfo, vc_userstat where vc_userstat.userref=vc_userinfo.id";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {		
				UserStat userStat = new UserStat(rset.getInt(1), rset.getString(2), VersionTable.getDate(rset, "lastlogin"));
				if (internalDeveloper.contains(userStat.username)) {
					userStat.isDeveloper = true;
				}
				if (internalUsers.contains(userStat.username)) {
					userStat.isInternal = true;
				}
				if (userStat.lastLogin != null) {
					if (userStat.lastLogin.getTime() >= startDateInMs && userStat.lastLogin.getTime() <= endDateInMs) {
						userStatList.add(userStat);
						userConstraintList.add(userStat.username);
					}
				}
			}
			rset.close();			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
		} finally{
			stmt.close();
			con.close();
		}
		
		int out_count1mon = 0;
		int out_count3mon = 0;
		int out_count6mon = 0;
		int out_countrest = 0;
		for (UserStat ui : userStatList) {
			if (!ui.isDeveloper) {
				if (!ui.isInternal && ui.lastLogin != null) {
					long t = endDateInMs - ui.lastLogin.getTime();
					if (t < 1 * MONTH_IN_MS) {
						out_count1mon ++;
						out_count3mon ++;
						out_count6mon ++;
					} else if (t < 3  * MONTH_IN_MS) {
						out_count3mon ++;
						out_count6mon ++;
					} else if (t < 6 * MONTH_IN_MS) {
						out_count6mon ++;
					} else {
						out_countrest ++;
					}
				}
			}
		}
		itemCount ++;
		statOutputPW.println(itemCount + ". User Statistics");
		statOutputPW.println("====================================================");

		statOutputPW.println("\tOutside login users");
		statOutputPW.println("========================================");
		statOutputPW.println("number of users in last 1 month  :\t" + out_count1mon);
		statOutputPW.println("number of users in last 3 months :\t" + out_count3mon);
		statOutputPW.println("number of users in last 6 months :\t" + out_count6mon);
		statOutputPW.println("number of users in more than 6 months :\t" + out_countrest);
		statOutputPW.println();
		
		statOutputPW.println("Internal users (totally " + internalUsers.size() + ") are : ");
		int i = 1;
		for (String s : internalUsers) {
			statOutputPW.print(s + " ");
			if (i++ % 5 == 0) {
				System.out.println();
			}
		}
		statOutputPW.println();
		int in_count1mon = 0;
		int in_count3mon = 0;
		int in_count6mon = 0;
		int in_countrest = 0;
		for (UserStat ui : userStatList) {
			if (!ui.isDeveloper) {
				if (ui.isInternal && ui.lastLogin != null) {
					long t = endDateInMs - ui.lastLogin.getTime();
					if (t < 1 * MONTH_IN_MS) {
						in_count1mon ++;
						in_count3mon ++;
						in_count6mon ++;
					} else if (t < 3  * MONTH_IN_MS) {
						in_count3mon ++;
						in_count6mon ++;
					} else if (t < 6 * MONTH_IN_MS) {
						in_count6mon ++;
					} else {
						in_countrest ++;
					}
				}
			}
		}
		statOutputPW.println("\tInternal login users");
		statOutputPW.println("========================================");		
		statOutputPW.println("number of users in last 1 month  :\t" + in_count1mon);
		statOutputPW.println("number of users in last 3 months :\t" + in_count3mon);
		statOutputPW.println("number of users in last 6 months :\t" + in_count6mon);
		statOutputPW.println("number of users in more than 6 months :\t" + in_countrest);
		statOutputPW.println();
		
		statOutputPW.println("\tTotal users (both internal and outside users)");
		statOutputPW.println("========================================");		
		statOutputPW.println("number of users in last 1 month  :\t" + (in_count1mon + out_count1mon));
		statOutputPW.println("number of users in last 3 months :\t" + (in_count3mon + out_count3mon));
		statOutputPW.println("number of users in last 6 months :\t" + (in_count6mon + out_count6mon));
		statOutputPW.println("number of users in more than 6 months :\t" + (in_countrest + out_countrest));
		statOutputPW.println();
		statOutputPW.flush();
	}
}
