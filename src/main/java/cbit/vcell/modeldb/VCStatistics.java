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

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;

public class VCStatistics {
	private OracleConnectionPoolDataSource oracleConnection = null;
	private ArrayList<String> userNameList = new ArrayList<String>();
	private ArrayList<Integer> userIDList = new ArrayList<Integer>();
	private static String simDataDir = "\\\\cfs02.cam.uchc.edu\\ifs\\raid\\vcell\\users\\";
	private class UserModelCount {
		int userID;
		int modelCount;
		UserModelCount(int arg_userID, int arg_modelCount) {
			userID = arg_userID;
			modelCount = arg_modelCount;
		}
		public int getModelCount() {
			return modelCount;
		}
		public int getUserID() {
			return userID;
		}
	}
	private class UserModelCountHistogram {		
		int[] numberOfUsers; //y
		int maxNumberOfModels; // maxX
		
		UserModelCountHistogram(int maxX) {
			maxNumberOfModels = maxX;
			numberOfUsers = new int[maxX + 1];
		}
		void increment(int X) {
			numberOfUsers[X] ++;
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("X\tY\n");
			for (int i = 1; i < maxNumberOfModels + 1; i ++)
			sb.append(i + "\t" + numberOfUsers[i] + "\n");
			return sb.toString();
		}
	}
	private VCStatistics(String dbServer,String dbName,String dbUserid,String dbPassword) throws Exception {
		oracleConnection = new OracleConnectionPoolDataSource();
		String dbConnectURL = "jdbc:oracle:thin:@"+dbServer+":1521:"+dbName;
		oracleConnection.setURL(dbConnectURL);
		oracleConnection.setUser(dbUserid);
		oracleConnection.setPassword(dbPassword);		
	}
	public static void main(String[] args) {
		if(args.length != 4){
			//e.g in your eclipse debug configuration put arguments: dbs6.cam.uchc.edu orcl ID password
			System.out.println("Usage:VCStatistics dbServer dbName dbUserID dbPassword");
			System.exit(1);
		}
		try {
			VCStatistics vcstat = new VCStatistics(args[0],args[1],args[2],args[3]);
			vcstat.startStatistics();			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	
	private  void startStatistics() {
		PrintWriter pw = null;
		try {
			Date currentDate = new Date();
			SimpleDateFormat sdf0 = new SimpleDateFormat("MMM_dd_yyyy");
			pw = new PrintWriter("VCStatistics_" + sdf0.format(currentDate) + ".txt");
			
			getAllUsers();			
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
			pw.println(sdf.format(currentDate));
			pw.println("==========================================");
			int numUsers = userIDList.size();
			pw.format("%30s - %10d%n", "Total Registered VCell Users", numUsers);		
			System.out.println("Total Registered VCell Users : " + numUsers);
			int numUsersRunSim = howManyUsersWhoRanSimulations();
			pw.format("%30s - %10d%n", "Users Who Ran Simulations", numUsersRunSim);
			System.out.println("Users Who Ran Simulations : " + numUsersRunSim);
			int howmanyBio = howManyBioModels();
			int howmanyMath = howManyMathModels();
			pw.format("%30s - %10d%n", "Total Models", (howmanyBio + howmanyMath));
			System.out.println("Total Models : " + (howmanyBio + howmanyMath));
			int numApplications = howManySimulationContexts();
			int numSims = howManySimulations();
			pw.format("%30s - %10d%n", "Total Applications", numApplications);
			pw.format("%30s - %10d%n", "Total Simulations", numSims);
			System.out.println("Total Applications : " + numApplications);
			System.out.println("Total Simulations : " + numSims);
			int howmanyPublicBio = howManyPublicBioModels();
			int howmanyPublicMath = howManyPublicMathModels();
			pw.format("%30s - %10d%n", "Public Models", (howmanyPublicBio + howmanyPublicMath));
			pw.format("%30s - %10d%n", "Public Simulations", howManyPublicSimulations());
			System.out.println("Public Models : " + (howmanyPublicBio + howmanyPublicMath));
			System.out.println("Public Simulations : " + howManyPublicSimulations());
			pw.println();
			UserModelCountHistogram histogram = getHistogramOnModels();
			if (histogram != null) {
				pw.println("Histgram (X - number of Models, Y - number of Users)");
				pw.println(histogram.toString());
				System.out.println("Histgram (X - number of Models, Y - number of Users):");
				System.out.println(histogram.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			System.exit(0);
		}
	}
	private void failed(Connection con) throws SQLException {
		System.out.println("OraclePoolingConnectionFactory.failed("+con+")");
		release(con);
	}

	private Connection getConnection() throws SQLException {	
		return oracleConnection.getConnection();
	}

	private void release(Connection con) throws SQLException {
		con.close();
	}
	
	private void getAllUsers() throws SQLException {
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		try {			
			String sql = 	"select id, userid from vc_userinfo";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()){
				userIDList.add(rset.getInt(1));
				userNameList.add(rset.getString(2));
			}
			rset.close();			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			failed(con);
		}finally{
			stmt.close();
			release(con);
		}
	}
	
	private int howManyUsersWhoRanSimulations() throws SQLException {
		File dataRootDir = new File(simDataDir);	
		File userDirs[] = dataRootDir.listFiles();
		int howmany = 0;
		
		if (userIDList.size() == 0) {
			getAllUsers();
		}
		for (int i = 0; i < userDirs.length; i ++){		
			if (!userDirs[i].isDirectory()) {
				continue;
			}
			String dirname = userDirs[i].getName();
			if (userNameList.contains(dirname)) {
				howmany ++;
			}
		}
		return howmany;
	}	
	
	private int howManyBioModels() throws SQLException {
		int howmany = 0;
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		try {					
			String sql = "select count(*) from vc_biomodel";
			ResultSet rset = stmt.executeQuery(sql);
			if (rset.next()){
				howmany = rset.getInt(1);
			}
			rset.close();			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			failed(con);
		}finally{
			stmt.close();
			release(con);
		}		
		return howmany;
	}
	
	private int howManyMathModels() throws SQLException {
		int howmany = 0;
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		try {			
			String sql = "select count(*) from vc_mathmodel";
			ResultSet rset = stmt.executeQuery(sql);
			if (rset.next()){
				howmany = rset.getInt(1);
			}
			rset.close();			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			failed(con);
		}finally{
			stmt.close();
			release(con);
		}		
		return howmany;
	}
	
	private int howManySimulations() throws SQLException {
		int howmany = 0;
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		try {			
			String sql = "select count(*) from vc_simulation";
			ResultSet rset = stmt.executeQuery(sql);
			if (rset.next()){
				howmany = rset.getInt(1);
			}			
			rset.close();			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			failed(con);
		}finally{
			stmt.close();
			release(con);
		}		
		return howmany;
	}	
	
	private int howManyPublicBioModels() throws SQLException {
		int howmany = 0;
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		try {			
			String sql = "SELECT COUNT(*) FROM VC_BIOMODEL where PRIVACY=0";
			ResultSet rset = stmt.executeQuery(sql);
			if (rset.next()){
				howmany = rset.getInt(1);
			}			
			rset.close();			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			failed(con);
		}finally{
			stmt.close();
			release(con);
		}		
		return howmany;
	}	
	
	private int howManySimulationContexts() throws SQLException {
		int howmany = 0;
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		try {			
			String sql = "SELECT COUNT(*) FROM VC_SimContext";
			ResultSet rset = stmt.executeQuery(sql);
			if (rset.next()){
				howmany = rset.getInt(1);
			}			
			rset.close();			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			failed(con);
		}finally{
			stmt.close();
			release(con);
		}		
		return howmany;
	}	
	
	private int howManyPublicMathModels() throws SQLException {
		int howmany = 0;
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		try {			
			String sql = "SELECT COUNT(*) FROM VC_MathModel where PRIVACY=0";
			ResultSet rset = stmt.executeQuery(sql);
			if (rset.next()){
				howmany = rset.getInt(1);
			}			
			rset.close();			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			failed(con);
		}finally{
			stmt.close();
			release(con);
		}		
		return howmany;
	}			
	
	private int howManyPublicSimulations() throws SQLException {
		int howmanyPubicBioSims = 0;
		int howmanyPubicMathSims = 0;
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		try {			
			String sql = "SELECT COUNT (*)  FROM VC_SIMULATION WHERE VC_SIMULATION.ID IN (" 
				+ "SELECT DISTINCT VC_SIMULATION.ID FROM VC_BIOMODEL, VC_SIMULATION, VC_BIOMODELSIM " 
				+ "WHERE VC_SIMULATION.ID = VC_BIOMODELSIM.simref " 
				+ "AND VC_BIOMODEL.ID = VC_BIOMODELSIM.biomodelref " 
				+ "AND vc_biomodel.privacy = 0)";
			ResultSet rset = stmt.executeQuery(sql);
			if (rset.next()){
				howmanyPubicBioSims = rset.getInt(1);
			}		
			rset.close();
			sql = "SELECT COUNT (*)  FROM VC_SIMULATION WHERE VC_SIMULATION.ID IN (" 
				+ "SELECT DISTINCT VC_SIMULATION.ID FROM VC_MATHMODEL, VC_SIMULATION, VC_MATHMODELSIM " 
				+ "WHERE VC_SIMULATION.ID = VC_MATHMODELSIM.simref "
				+ "AND VC_MATHMODEL.ID = VC_MATHMODELSIM.mathmodelref " 
				+ "AND VC_MATHMODEL.privacy = 0)";
			rset = stmt.executeQuery(sql);
			if (rset.next()){
				howmanyPubicMathSims = rset.getInt(1);
			}		
			rset.close();			
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			failed(con);
		}finally{
			stmt.close();
			release(con);
		}		
		return howmanyPubicBioSims + howmanyPubicMathSims;
	}		
	
	private UserModelCountHistogram getHistogramOnModels() throws SQLException {
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		Vector<UserModelCount> bioUserModelCount = new Vector<UserModelCount>();
		Vector<UserModelCount> mathUserModelCount = new Vector<UserModelCount>();
		Vector<UserModelCount> totalUserModelCount = new Vector<UserModelCount>();
		try {			
			String sql = "SELECT VC_USERINFO.id, COUNT(*) FROM VC_USERINFO, VC_BIOMODEL " 
				+ "WHERE VC_BIOMODEL.ownerref=VC_USERINFO.id and "
				+ "vc_userinfo.userid not in ('fgao', 'anu', 'vcelltestaccount', 'frm', 'schaff', 'ion', 'liye') " 
				+ "GROUP BY VC_USERINFO.id order by vc_userinfo.id";
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()){
				bioUserModelCount.add(new UserModelCount(rset.getInt(1), rset.getInt(2)));
			}		
			rset.close();
			sql = "SELECT VC_USERINFO.id, COUNT(*) FROM VC_USERINFO, VC_MATHMODEL "				
				+ "WHERE VC_MATHMODEL.ownerref=VC_USERINFO.id and " 
				+ "vc_userinfo.userid not in ('fgao', 'anu', 'vcelltestaccount', 'frm', 'schaff', 'ion', 'liye') "
				+ "GROUP BY VC_USERINFO.id order by vc_userinfo.id";
			rset = stmt.executeQuery(sql);
			while (rset.next()){
				mathUserModelCount.add(new UserModelCount(rset.getInt(1), rset.getInt(2)));
			}		
			rset.close();
			int bioCount = 0;
			int mathCount = 0;
			int maxCount = 0;
			while (bioCount < bioUserModelCount.size() || mathCount < mathUserModelCount.size()) {
				UserModelCount bumc = null;
				UserModelCount mumc = null;
				if (bioCount < bioUserModelCount.size()) {
					bumc = bioUserModelCount.get(bioCount);
				}
				if ( mathCount  < mathUserModelCount.size()) {
					mumc = mathUserModelCount.get(mathCount);
				}								
				// at least one of them is not null
				if (mumc == null && bumc != null || mumc != null && bumc != null && bumc.getUserID() < mumc.getUserID()) {
					totalUserModelCount.add(bumc);
					maxCount = Math.max(maxCount, bumc.getModelCount());
					bioCount ++;			
				} else if (mumc != null && bumc != null && bumc.getUserID() == mumc.getUserID()) {
					UserModelCount newumc = new UserModelCount(bumc.getUserID(), bumc.getModelCount() + mumc.getModelCount());
					totalUserModelCount.add(newumc);
					maxCount = Math.max(maxCount, newumc.getModelCount());
					bioCount ++;
					mathCount ++;
				} else if (bumc == null && mumc != null || mumc != null && bumc != null && bumc.getUserID() > mumc.getUserID()) {
					totalUserModelCount.add(mumc);
					maxCount = Math.max(maxCount, mumc.getModelCount());
					mathCount ++;
				}
			}
			
			UserModelCountHistogram histogram = new UserModelCountHistogram(maxCount);
			for (UserModelCount umc : totalUserModelCount) {
				histogram.increment(umc.getModelCount());
			}			
			return histogram;
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			failed(con);
		}finally{
			stmt.close();
			release(con);
		}
		return null;
	}
}
