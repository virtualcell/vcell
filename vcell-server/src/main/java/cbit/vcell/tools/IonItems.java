package cbit.vcell.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDocument.VCDocumentType;

import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.modeldb.BioModelSimulationLinkTable;
import cbit.vcell.modeldb.BioModelTable;
import cbit.vcell.modeldb.MathModelSimulationLinkTable;
import cbit.vcell.modeldb.MathModelTable;
import cbit.vcell.modeldb.UserTable;
import cbit.vcell.modeldb.VersionTable;
import cbit.vcell.simdata.SimulationData;

public class IonItems {

	public static void main(String[] args) {
		if(args.length != 3) {
			System.out.println("cbit.vcell.tools.IonItems jdbc:oracle:thin:@VCELL_DB_HOST:1521:VCEL_DB_NAME vcellUsersRootDir {true,false print more info}");
			System.exit(1);
		}
		//"jdbc:oracle:thin:@VCELL_DB_HOST:1521:VCEL_DB_NAME"
		String connectionStr = args[0];
		// /share/apps/vcell3/users
		File vcellUsersRootDir = new File(args[1]);
		boolean bMoreInfo = new Boolean(args[2]).booleanValue();
		//String password = new String(System.console().readPassword("Enter VCell Password: "));
//		select vc_biomodelsim.simref from vc_biomodel,vc_biomodelsim where vc_biomodel.privacy=0 and vc_biomodelsim.biomodelref=vc_biomodel.id  order by vc_biomodelsim.simref;
//		select vc_mathmodelsim.simref from vc_mathmodel,vc_mathmodelsim where vc_mathmodel.privacy=0 and vc_mathmodelsim.mathmodelref=vc_mathmodel.id  order by vc_mathmodelsim.simref;
		Connection con = null;//oracleConnection.getConnection(new Object());
		Statement stmt = null;//con.createStatement();
		ArrayList<ArrayList<FoundSimDataInfo>> foundBMSims = null;
		ArrayList<ArrayList<FoundSimDataInfo>> foundMMSims = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        System.out.print("Enter VCell Password: ");
	        String password = br.readLine();
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = java.sql.DriverManager.getConnection(connectionStr, "vcell",password);
			con.setAutoCommit(false);
			con.setReadOnly(true);
			stmt = con.createStatement();
			
			String bmsql = createSQL(VCDocumentType.BIOMODEL_DOC);
			System.out.println(bmsql+";");
			foundBMSims = findSims(stmt, bmsql);
			
			String mmsql = createSQL(VCDocumentType.MATHMODEL_DOC);
			System.out.println(mmsql+";");
			foundMMSims = findSims(stmt, mmsql);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally{
			if(stmt != null){try{stmt.close();}catch(Exception e){e.printStackTrace();}}
			if(con != null){try{con.close();}catch(Exception e){e.printStackTrace();}}
		}
		if(bMoreInfo) {
			System.out.println("'SimID','VCDocumentType','simLogFile','jobIndex','logExists?','oldStyle?','lastTaskID'");
		}
		checkSimData(foundBMSims,vcellUsersRootDir,VCDocumentType.BIOMODEL_DOC,bMoreInfo);
		checkSimData(foundMMSims,vcellUsersRootDir,VCDocumentType.MATHMODEL_DOC,bMoreInfo);

	}

	public static void checkSimData(ArrayList<ArrayList<FoundSimDataInfo>> foundBMSims,File vcellUsersRootDir,VCDocumentType docType,boolean bMoreInfo) {
		for(ArrayList<FoundSimDataInfo> arrForSim : foundBMSims) {
			for(FoundSimDataInfo foundSimInfo:arrForSim) {
				try {
					if(!bMoreInfo) {
						System.out.println(foundSimInfo.simID);
						break;
					}
					File userDir = new File(vcellUsersRootDir,foundSimInfo.userid);
					String newStyleSimLogFileName = SimulationData.createCanonicalSimLogFileName(new KeyValue(foundSimInfo.simID+""), foundSimInfo.jobIndex, false);
					File newStyleLogFile = new File(userDir,newStyleSimLogFileName);
					File foundLogFile = null;
					boolean isOldStyle = false;
					if(!newStyleLogFile.exists()) {
						if(foundSimInfo.jobIndex != 0) {//OldStyle can only have 0 as jobindex so don't have to check
							if(bMoreInfo) {
								System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+"couldn't find "+newStyleLogFile.getAbsolutePath()+" (no oldStyle for jobIndex>0)'"+","+foundSimInfo.jobIndex+",'false','false'"+","+foundSimInfo.lastTaskID);
							}
							continue;
						}
						String oldStyleSimLogFileName = SimulationData.createCanonicalSimLogFileName(new KeyValue(foundSimInfo.simID+""), foundSimInfo.jobIndex, true);
						File oldStyleLogFile = new File(userDir,oldStyleSimLogFileName);
						if(!oldStyleLogFile.exists()) {
							if(bMoreInfo) {
								System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+"couldn't find "+oldStyleLogFile.getAbsolutePath()+" or "+newStyleLogFile.getAbsolutePath()+"'"+","+foundSimInfo.jobIndex+",'false','false'"+","+foundSimInfo.lastTaskID);
							}
							continue;
						}else {
							foundLogFile = oldStyleLogFile;
							isOldStyle = true;
						}
						//System.out.println("  "+oldStyleSimLogFileName+" "+oldStyleLogFileExists);
					}else {
						foundLogFile = newStyleLogFile;
						isOldStyle = false;
					}
					if(bMoreInfo) {
						System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+foundLogFile.getAbsolutePath()+"'"+","+foundSimInfo.jobIndex+",'true','"+isOldStyle+"',"+foundSimInfo.lastTaskID);
					}
				} catch (Exception e) {
					if(bMoreInfo) {
						System.out.println(foundSimInfo.simID+","+"'"+docType.name()+"',"+"'"+foundSimInfo.toString()+" "+e.getMessage()+"'"+","+foundSimInfo.jobIndex+",'unknown','unknown',"+foundSimInfo.lastTaskID);
					}
				}
			}
//			System.out.println();
		}
	}

	public static class FoundSimDataInfo {
		public long simID;
		public int lastTaskID;
		public int jobIndex;
		public String userid;
		public FoundSimDataInfo(long simID, int lastTaskID, int jobIndex, String userid) {
			super();
			this.simID = simID;
			this.lastTaskID = lastTaskID;
			this.jobIndex = jobIndex;
			this.userid = userid;
		}
		@Override
		public String toString() {
			return "simID="+simID+" lastTaskID="+lastTaskID+" jobIndex="+jobIndex+" userid="+userid;
		}
	}
	public static ArrayList<ArrayList<FoundSimDataInfo>> findSims(Statement stmt, String sql) throws SQLException {
		ResultSet rset = stmt.executeQuery(sql);
		long lastSimID = -1;
		int lastTaskID = -1;
		ArrayList<ArrayList<FoundSimDataInfo>> lastSimDatas = new ArrayList<ArrayList<FoundSimDataInfo>>();
		while(rset.next()) {
			long currSimID = rset.getLong(1);
			int currJobIndex = rset.getInt(3);
			int currTaskID = rset.getInt(4);
			String currUserID = rset.getString(5);
			if(lastSimID != currSimID) {
//					if(lastSimID != -1) {
//						processLastTaskID(lastSimDatas);
//					}
				lastSimDatas.add(new ArrayList<FoundSimDataInfo>());
				lastSimID = currSimID;
				lastTaskID = currTaskID;
			}else if(lastTaskID != currTaskID) {
				lastSimDatas.get(lastSimDatas.size()-1).clear();
				lastTaskID = currTaskID;
			}
			lastSimDatas.get(lastSimDatas.size()-1).add(new FoundSimDataInfo(currSimID,currTaskID,currJobIndex,currUserID));
//				if(rset.isAfterLast()) {
//					processLastTaskID(lastSimDatas);
//				}
		}
		rset.close();
		return lastSimDatas;
	}

	public static String createSQL(VCDocumentType docType) {
		if(docType != VCDocumentType.BIOMODEL_DOC && docType != VCDocumentType.MATHMODEL_DOC) {
			throw new IllegalArgumentException("Only VCDocumentTypes BioModel and mathModel allowed");
		}
		VersionTable vTable = (docType == VCDocumentType.BIOMODEL_DOC?BioModelTable.table:MathModelTable.table);
		//"select vc_biomodelsim.simref from vc_biomodel,vc_biomodelsim where vc_biomodel.privacy="+GroupAccess.GROUPACCESS_ALL.toPlainString()+" and vc_biomodelsim.biomodelref=vc_biomodel.id  order by vc_biomodelsim.simref";
		//"select vc_mathmodelsim.simref from vc_mathmodel,vc_mathmodelsim where vc_mathmodel.privacy=0 and vc_mathmodelsim.mathmodelref=vc_mathmodel.id  order by vc_mathmodelsim.simref";
		return "select "+
			(docType == VCDocumentType.BIOMODEL_DOC?BioModelSimulationLinkTable.table.simRef.getQualifiedColName():MathModelSimulationLinkTable.table.simRef.getQualifiedColName())+","+
			SimulationJobTable.table.hasData.getQualifiedColName()+","+
			SimulationJobTable.table.jobIndex.getQualifiedColName()+","+
			SimulationJobTable.table.taskID+","+
			UserTable.table.userid+
		" From "+
			vTable.getTableName()+","+
			(docType == VCDocumentType.BIOMODEL_DOC?BioModelSimulationLinkTable.table.getTableName():MathModelSimulationLinkTable.table.getTableName())+","+
			SimulationJobTable.table.getTableName()+","+
			UserTable.table.tableName+
		" WHERE "+
			vTable.ownerRef.getQualifiedColName()+"="+UserTable.table.id.getQualifiedColName()+
			" AND "+
			vTable.privacy.getQualifiedColName()+"="+GroupAccess.GROUPACCESS_ALL.toPlainString()+
			" AND "+
			(docType == VCDocumentType.BIOMODEL_DOC?BioModelSimulationLinkTable.table.bioModelRef:MathModelSimulationLinkTable.table.mathModelRef).getQualifiedColName()+"="+vTable.id.getQualifiedColName()+
			" AND "+
			(docType == VCDocumentType.BIOMODEL_DOC?BioModelSimulationLinkTable.table.simRef:MathModelSimulationLinkTable.table.simRef).getQualifiedColName()+"="+SimulationJobTable.table.simRef.getQualifiedColName()+
			" AND "+
			SimulationJobTable.table.hasData.getQualifiedColName()+" IS NOT NULL"+
				" AND "+
				" UPPER("+SimulationJobTable.table.hasData.getQualifiedColName()+")"+"='Y'"+
			" ORDER BY "+SimulationJobTable.table.simRef.getQualifiedColName()+","+SimulationJobTable.table.taskID.getQualifiedColName()+","+SimulationJobTable.table.jobIndex.getQualifiedColName();
	}

	public static void processLastTaskID(ArrayList<ArrayList<Object[]>> lastSimDatas) {
		ArrayList<Object[]> lastArrList = lastSimDatas.get(lastSimDatas.size()-1);
		for(int i=0;i<lastArrList.size();i++) {
			Object[] simInfo = lastArrList.get(i);
			System.out.println("**"+simInfo[0]+" "+simInfo[1]+" "+simInfo[2]+" "+simInfo[3]);
		}
	}
}
