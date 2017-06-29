/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.field.db;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Timer;

import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionableType;

import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.io.FieldDataFileOperationResults;
import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.modeldb.BioModelSimContextLinkTable;
import cbit.vcell.modeldb.BioModelSimulationLinkTable;
import cbit.vcell.modeldb.BioModelTable;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.ExternalDataTable;
import cbit.vcell.modeldb.MathDescExternalDataLinkTable;
import cbit.vcell.modeldb.MathDescTable;
import cbit.vcell.modeldb.MathModelSimulationLinkTable;
import cbit.vcell.modeldb.MathModelTable;
import cbit.vcell.modeldb.SimContextTable;
import cbit.vcell.modeldb.SimulationTable;
import cbit.vcell.modeldb.UserTable;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.SimulationData;

public class FieldDataDBOperationDriver{
	
	private static String DATE_FORMAT_STRING = "'DD-MON-YYYY HH24:MI:SS'";

	private static Connection liveConnection = null;
	private static Timer liveConnectionTimer = null;
	
	private static synchronized void restartLiveConnectionTimer(){
		if(liveConnectionTimer == null){
			liveConnectionTimer =
				new Timer(5*60*1000,
					new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							closeConnection();
						}}
				);
			liveConnectionTimer.setRepeats(false);
		}
		liveConnectionTimer.restart();
	}
	private static synchronized void closeConnection(){
//		System.err.println("connection Closed");
		if(liveConnection != null){
			try{liveConnection.close();}catch(Exception e){e.printStackTrace();}
			liveConnection = null;
		}
	}
	private static synchronized Connection getConnection() throws Exception{

		restartLiveConnectionTimer();
		
		if(liveConnection != null){
//			System.err.println("connection Re-used");
			return liveConnection;
		}
//		System.err.println("connection Created");
		String dbDriverName = PropertyLoader.getProperty(PropertyLoader.dbDriverName, null);
		String dbConnectURL = PropertyLoader.getProperty(PropertyLoader.dbConnectURL, null);
		String dbSchemaUser = PropertyLoader.getProperty(PropertyLoader.dbUserid, null);
		String dbPassword = PropertyLoader.getProperty(PropertyLoader.dbPassword, null);
		try{
			Class.forName(dbDriverName);
			liveConnection = java.sql.DriverManager.getConnection(dbConnectURL,dbSchemaUser,dbPassword);
			liveConnection.setReadOnly(true);
			return liveConnection;
		}catch(Exception e){
			throw new Exception("FieldDataDBOperationDriver: Couldn't get database connection",e);
		}

	}
	public static FieldDataDBOperationResults fieldDataDBOperation(Connection con, User user,
			FieldDataDBOperationSpec fieldDataDBOperationSpec) throws SQLException, DataAccessException {
		
		if(fieldDataDBOperationSpec.opType == FieldDataDBOperationSpec.FDDBOS_COPY_NO_CONFLICT){
			//get all current ExtDataIDs
			ExternalDataIdentifier[] existingExtDataIDArr =
				FieldDataDBOperationDriver.fieldDataDBOperation(
					con, user,FieldDataDBOperationSpec.createGetExtDataIDsSpec(user)).extDataIDArr;
			//Rename FieldFunc names if necessary
			Hashtable<String,String> newNameOrigNameHash = new Hashtable<String, String>();
			for(int i=0;i<fieldDataDBOperationSpec.sourceNames.length;i+= 1){
				String newFieldFuncName = fieldDataDBOperationSpec.sourceNames[i];
				while(true){
					boolean bNameConflictExists = false;
					for(int j=0;j<existingExtDataIDArr.length;j+= 1){
						if(existingExtDataIDArr[j].getName().equals(newFieldFuncName)){
							bNameConflictExists = true;
							break;
						}
					}
					bNameConflictExists =
						bNameConflictExists || newNameOrigNameHash.containsKey(newFieldFuncName);
					if(!bNameConflictExists){
						newNameOrigNameHash.put(newFieldFuncName,fieldDataDBOperationSpec.sourceNames[i]);
						break;
					}
					newFieldFuncName = TokenMangler.getNextEnumeratedToken(newFieldFuncName);
				}
			}
			//Add new ExternalDataIdentifier (FieldData ID) to DB
			//Copy source annotation
			FieldDataDBOperationResults sourceUserExtDataInfo =
				fieldDataDBOperation(con,user,
						FieldDataDBOperationSpec.createGetExtDataIDsSpec(
								fieldDataDBOperationSpec.sourceOwner.getVersion().getOwner()));
			ExternalDataIdentifier[] sourceUserExtDataIDArr = sourceUserExtDataInfo.extDataIDArr;
			Hashtable<String, ExternalDataIdentifier> oldNameNewIDHash =
				new Hashtable<String, ExternalDataIdentifier>();
			Hashtable<String, KeyValue> oldNameOldExtDataIDKey =
				new Hashtable<String, KeyValue>();
			String[] newFieldFuncNamesArr = newNameOrigNameHash.keySet().toArray(new String[0]);
			for(int i=0;i<newFieldFuncNamesArr.length;i+= 1){
				//find orig annotation
				String origAnnotation =
					"Copy Field Data name used Field Data function\r\n"+
					"Source type: "+fieldDataDBOperationSpec.sourceOwner.getVType().getTypeName()+"\r\n"+
					"Source owner: "+fieldDataDBOperationSpec.sourceOwner.getVersion().getOwner().getName()+"\r\n"+
					"Source name: "+fieldDataDBOperationSpec.sourceOwner.getVersion().getName()+"\r\n"+
					"Original Field Data name: "+newNameOrigNameHash.get(newFieldFuncNamesArr[i])+"\r\n"+
					"New Field Data name: "+newFieldFuncNamesArr[i]+"\r\n"+
					"Source Annotation: "+newFieldFuncNamesArr[i]+"\r\n";
				for(int j=0;j<sourceUserExtDataInfo.extDataAnnotArr.length;j+= 1){
					String originalName = newNameOrigNameHash.get(newFieldFuncNamesArr[i]);
					if(sourceUserExtDataIDArr[j].getName().equals(originalName)){
						oldNameOldExtDataIDKey.put(originalName, sourceUserExtDataInfo.extDataIDArr[j].getKey());
						origAnnotation+= sourceUserExtDataInfo.extDataAnnotArr[j];
						break;
					}
				}
				//
				FieldDataDBOperationResults fieldDataDBOperationResults =
					fieldDataDBOperation(con,user,
							FieldDataDBOperationSpec.createSaveNewExtDataIDSpec(
									user, newFieldFuncNamesArr[i],origAnnotation));
//				errorCleanupExtDataIDV.add(fieldDataDBOperationResults.extDataID);
				String origFieldFuncName =
					newNameOrigNameHash.get(fieldDataDBOperationResults.extDataID.getName());
				if(origFieldFuncName == null){
					throw new DataAccessException("couldn't find original FieldFuncName using new ExternalDataId");
				}
				oldNameNewIDHash.put(origFieldFuncName,fieldDataDBOperationResults.extDataID);
			}
			
			FieldDataDBOperationResults fieldDataDBOperationResults =
				new FieldDataDBOperationResults();
			fieldDataDBOperationResults.oldNameNewIDHash = oldNameNewIDHash;
			fieldDataDBOperationResults.oldNameOldExtDataIDKeyHash = oldNameOldExtDataIDKey;
			return fieldDataDBOperationResults;
		}else if(fieldDataDBOperationSpec.opType == FieldDataDBOperationSpec.FDDBOS_GETEXTDATAIDS){
			String sql;
			ResultSet rset;
			if(fieldDataDBOperationSpec.bIncludeSimRefs){
				sql = 	"SELECT "+
					ExternalDataTable.table.id.getQualifiedColName()+","+
					ExternalDataTable.table.externalDataName.getQualifiedColName()+","+
					ExternalDataTable.table.ownerRef.getQualifiedColName()+","+
					UserTable.table.userid.getQualifiedColName()+ ","+
					SimulationTable.table.id.getQualifiedColName() +
				" FROM " +
					ExternalDataTable.table.getTableName() + ","+
					MathDescTable.table.getTableName() + ","+
					SimulationTable.table.getTableName() + ","+
					MathDescExternalDataLinkTable.table.getTableName() + ","+
					UserTable.table.getTableName()+
				" WHERE " +
					UserTable.table.id.getQualifiedColName() + " = " +
						fieldDataDBOperationSpec.owner.getID() +
					" AND "+
					UserTable.table.id.getQualifiedColName() + " = " + 
						ExternalDataTable.table.ownerRef.getQualifiedColName() +			
					" AND "+
					ExternalDataTable.table.id.getQualifiedColName() + " = " + 
						MathDescExternalDataLinkTable.table.extDataRef.getQualifiedColName() +			
					" AND "+
					MathDescTable.table.id.getQualifiedColName() + " = " + 
						MathDescExternalDataLinkTable.table.mathDescRef.getQualifiedColName() +			
					" AND "+
					MathDescTable.table.id.getQualifiedColName() + " = " + 
						SimulationTable.table.mathRef.getQualifiedColName();				
			}else{
				sql = 	"SELECT "+
					ExternalDataTable.table.getTableName()+".*"+","+
					UserTable.table.userid.getQualifiedColName()+
				" FROM " +
					ExternalDataTable.table.getTableName() + ","+
					UserTable.table.getTableName()+
				" WHERE " +
					ExternalDataTable.table.ownerRef + "=" +fieldDataDBOperationSpec.owner.getID() +
					" AND "+
					UserTable.table.id.getQualifiedColName() + " = " +
						ExternalDataTable.table.ownerRef.getQualifiedColName();
			}
		
			Statement stmt = con.createStatement();
			Vector<ExternalDataIdentifier> extDataIDV = new Vector<ExternalDataIdentifier>();
			Vector<String> extDataAnnotV = new Vector<String>();
			HashMap<ExternalDataIdentifier, Vector<KeyValue>> extDataIDSimRefsH = null;
			try {
				rset = stmt.executeQuery(sql);
				while (rset.next()) {
					ExternalDataIdentifier extDataID =
						ExternalDataTable.table.getExternalDataIdentifier(rset);
					if( !fieldDataDBOperationSpec.bIncludeSimRefs &&
						!extDataIDV.contains(extDataID)){
						extDataIDV.add(extDataID);
						extDataAnnotV.add(ExternalDataTable.table.getExternalDataAnnot(rset));
					}
					if(fieldDataDBOperationSpec.bIncludeSimRefs){
						if(extDataIDSimRefsH == null){
							extDataIDSimRefsH =
								new HashMap<ExternalDataIdentifier, Vector<KeyValue>>();
						}
						Vector<KeyValue> simRefV =
							extDataIDSimRefsH.get(extDataID);
						if(simRefV == null){
							simRefV = new Vector<KeyValue>();
							extDataIDSimRefsH.put(extDataID, simRefV);
						}
						simRefV.add(
							new KeyValue(
								rset.getBigDecimal(SimulationTable.table.id.getUnqualifiedColName())));
					}
				}
			} finally {
				stmt.close();
			}
			FieldDataDBOperationResults fieldDataDBOperationResults = new FieldDataDBOperationResults();
			fieldDataDBOperationResults.extDataIDArr = extDataIDV.toArray(new ExternalDataIdentifier[extDataIDV.size()]);
			fieldDataDBOperationResults.extDataAnnotArr = extDataAnnotV.toArray(new String[extDataAnnotV.size()]);
			fieldDataDBOperationResults.extdataIDAndSimRefH = extDataIDSimRefsH;
			return fieldDataDBOperationResults;
			
		}else if(fieldDataDBOperationSpec.opType == FieldDataDBOperationSpec.FDDBOS_SAVEEXTDATAID){
		
			if(!fieldDataDBOperationSpec.newExtDataIDName.equals(
					TokenMangler.fixTokenStrict(fieldDataDBOperationSpec.newExtDataIDName))){
				throw new DataAccessException("Error inserting Field Data name "+
						fieldDataDBOperationSpec.newExtDataIDName+"\n"+
						"Field Data names can contain only letters,digits and underscores");
			}
			
			KeyValue newKey = DbDriver.getNewKey(con);
			String sql =
				"INSERT INTO "+ExternalDataTable.table.getTableName()+" "+
				ExternalDataTable.table.getSQLColumnList()+
				" VALUES "+
				ExternalDataTable.table.getSQLValueList(
						newKey,user,
						fieldDataDBOperationSpec.newExtDataIDName,
						fieldDataDBOperationSpec.annotation);
		
			DbDriver.updateCleanSQL(con,sql);
			ExternalDataIdentifier[] fdiArr =
				FieldDataDBOperationDriver.fieldDataDBOperation(
					con, user,FieldDataDBOperationSpec.createGetExtDataIDsSpec(user)).extDataIDArr;
			for (int i = 0; i < fdiArr.length; i++) {
				if(fdiArr[i].getName().equals(fieldDataDBOperationSpec.newExtDataIDName)){
					FieldDataDBOperationResults fieldDataDBOperationResults = new FieldDataDBOperationResults();
					fieldDataDBOperationResults.extDataID = fdiArr[i];;
					return fieldDataDBOperationResults;
				}
			}
			throw new DataAccessException(
					"Unable to retrieve inserted ExternalDataIdentifier "+
					fieldDataDBOperationSpec.newExtDataIDName);	

		
		}else if(fieldDataDBOperationSpec.opType == FieldDataDBOperationSpec.FDDBOS_DELETE){
			String sql = 
				"DELETE" + " FROM " + ExternalDataTable.table.getTableName() + 
				" WHERE " +
					ExternalDataTable.table.ownerRef + " = " + user.getID() +
					" AND " +
					ExternalDataTable.table.id + " = " + fieldDataDBOperationSpec.specEDI.getKey().toString();
			
			DbDriver.updateCleanSQL(con,sql);
			
			return new FieldDataDBOperationResults();
		}
		
		throw new DataAccessException("Unknown FieldDataDBOperation "+fieldDataDBOperationSpec.opType);
	}
	
	public static synchronized HashMap<User, Vector<ExternalDataIdentifier>> getAllExternalDataIdentifiers() throws DataAccessException{
		
		String sql =
			"SELECT "+
				ExternalDataTable.table.getTableName()+".*"+","+
				UserTable.table.userid.getQualifiedColName()+
			" FROM " +
				ExternalDataTable.table.getTableName() + ","+
				UserTable.table.getTableName()+
			" WHERE " +
				UserTable.table.id.getQualifiedColName() + " = " +
					ExternalDataTable.table.ownerRef.getQualifiedColName();
		Statement stmt = null;
		HashMap<User, Vector<ExternalDataIdentifier>> allUserExtDataIDH =
			new HashMap<User, Vector<ExternalDataIdentifier>>();
		try {
			stmt = 	getConnection().createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				ExternalDataIdentifier extDataID =
					ExternalDataTable.table.getExternalDataIdentifier(rset);
				Vector<ExternalDataIdentifier> userExtDataIDV =
					allUserExtDataIDH.get(extDataID.getOwner());
				if(userExtDataIDV == null){
					userExtDataIDV = new Vector<ExternalDataIdentifier>();
					allUserExtDataIDH.put(extDataID.getOwner(), userExtDataIDV);
				}
				userExtDataIDV.add(extDataID);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new DataAccessException("Error: getAllExternalDataIdentifiers",e);
		}finally {
			if(stmt != null){try{stmt.close();}catch(Exception e){e.printStackTrace();}}
		}
		return allUserExtDataIDH;
	}

	public static synchronized HashMap<String, KeyValue> getFunctionFileNamesAndSimKeys(User user)
	throws DataAccessException{
	
	String sql =
		"SELECT "+
			SimulationJobTable.table.simRef.getQualifiedColName()+","+
			SimulationJobTable.table.jobIndex.getQualifiedColName()+
		" FROM " +
			SimulationJobTable.table.getTableName()+","+
			SimulationTable.table.getTableName()+
		" WHERE " +
			SimulationTable.table.ownerRef.getQualifiedColName() +" = " + user.getID() +
		" AND "+
		SimulationTable.table.id.getQualifiedColName()+ " = " +SimulationJobTable.table.simRef.getQualifiedColName()+
		" UNION " +
		" SELECT " +
			SimulationTable.table.id.getQualifiedColName()+","+
			"TO_NUMBER(NULL) "+//SimulationJobTable.table.jobIndex.getUnqualifiedColName()+
		" FROM " +
			SimulationTable.table.getTableName()+
		" WHERE " +
			SimulationTable.table.ownerRef.getQualifiedColName()+ "=" +user.getID()+
		" AND " +
			SimulationTable.table.id.getQualifiedColName()+
			" NOT IN (SELECT " +SimulationJobTable.table.simRef.getQualifiedColName()+
					" FROM " +SimulationJobTable.table.getTableName()+" )"+
		" UNION " +
		" SELECT " +
			ExternalDataTable.table.id.getQualifiedColName()+","+
			"TO_NUMBER(0) "+
		" FROM " +
			ExternalDataTable.table.getTableName()+
		" WHERE " +
			ExternalDataTable.table.ownerRef.getQualifiedColName()+ "=" +user.getID();


	Statement stmt = null;
	HashMap<String, KeyValue> functionNamesH = new HashMap<String, KeyValue>();
	try {
		stmt = 	getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			KeyValue simKey = new KeyValue(rset.getBigDecimal(1));
			BigDecimal jobIndex = rset.getBigDecimal(2);
			boolean isOldStyle = rset.wasNull();
			functionNamesH.put(
				SimulationData.createCanonicalFunctionsFileName(
					simKey, (isOldStyle?0:jobIndex.intValue()), isOldStyle),
				simKey
			);
		}
	}catch(Exception e){
		e.printStackTrace();
		throw new DataAccessException("Error: getFunctionFileNamesAndSimKeys",e);
	}finally {
		if(stmt != null){try{stmt.close();}catch(Exception e){e.printStackTrace();}}
	}
	return functionNamesH;
}

	public static synchronized FieldDataFileOperationResults.FieldDataReferenceInfo getModelDescriptionForSimulation(User user,KeyValue simulationKey)
	throws DataAccessException{
	
	String sqlMathModel =
		"SELECT "+
			MathModelTable.table.name.getQualifiedColName()+","+
			SimulationTable.table.name.getQualifiedColName() +","+
			"TO_CHAR("+MathModelTable.table.versionDate.getQualifiedColName()+","+DATE_FORMAT_STRING+")"+","+
			MathModelTable.table.id.getQualifiedColName()+
		" FROM " +
			SimulationTable.table.getTableName()+","+
			MathModelSimulationLinkTable.table.getTableName()+","+
			MathModelTable.table.getTableName() +
		" WHERE " +
			SimulationTable.table.ownerRef.getQualifiedColName() +" = " + user.getID() +
		" AND ("+
			SimulationTable.table.id.getQualifiedColName()+ " = " +simulationKey.toString()+
		" AND "+
			SimulationTable.table.id.getQualifiedColName()+ " = " +
				MathModelSimulationLinkTable.table.simRef.getQualifiedColName()+
		" AND "+
			MathModelSimulationLinkTable.table.mathModelRef.getQualifiedColName()+" = "+
				MathModelTable.table.id.getQualifiedColName()+
		") OR ("+
			SimulationTable.table.versionParentSimRef.getQualifiedColName()+" IS NOT NULL"+
		" AND "+
			SimulationTable.table.versionParentSimRef.getQualifiedColName()+" = "+simulationKey.toString()+
		" AND "+
			SimulationTable.table.id.getQualifiedColName()+ " = " +
				MathModelSimulationLinkTable.table.simRef.getQualifiedColName()+
		" AND "+
			MathModelSimulationLinkTable.table.mathModelRef.getQualifiedColName()+" = "+
				MathModelTable.table.id.getQualifiedColName()+
		")";

	String sqlBioModel =
		"SELECT "+
			BioModelTable.table.name.getQualifiedColName()+","+
			SimContextTable.table.name.getQualifiedColName()+","+
			SimulationTable.table.name.getQualifiedColName() +","+
			"TO_CHAR("+BioModelTable.table.versionDate.getQualifiedColName()+","+DATE_FORMAT_STRING+")"+","+
			BioModelTable.table.id.getQualifiedColName()+
		" FROM " +
			SimulationTable.table.getTableName()+","+
			BioModelSimulationLinkTable.table.getTableName()+","+
			SimContextTable.table.getTableName()+","+
			BioModelSimContextLinkTable.table.getTableName()+","+
			BioModelTable.table.getTableName() +
		" WHERE " +
			SimulationTable.table.ownerRef.getQualifiedColName() +" = " + user.getID() +
		" AND ("+
			SimulationTable.table.id.getQualifiedColName()+ " = " +simulationKey.toString()+
		" AND "+
			SimulationTable.table.id.getQualifiedColName()+ " = " +
				BioModelSimulationLinkTable.table.simRef.getQualifiedColName()+
		" AND "+
			BioModelSimulationLinkTable.table.bioModelRef.getQualifiedColName()+" = "+
				BioModelTable.table.id.getQualifiedColName()+
		" AND "+
			BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+" = "+
				BioModelTable.table.id.getQualifiedColName() +
		" AND "+
			SimContextTable.table.id.getQualifiedColName()+ " = " +
				BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+
		" AND "+
			SimContextTable.table.mathRef.getQualifiedColName()+" = "+
				SimulationTable.table.mathRef.getQualifiedColName()+
		") OR ("+
			SimulationTable.table.versionParentSimRef.getQualifiedColName()+" IS NOT NULL"+
		" AND "+
			SimulationTable.table.versionParentSimRef.getQualifiedColName()+" = "+simulationKey.toString()+
		" AND "+
			SimulationTable.table.id.getQualifiedColName()+ " = " +
				BioModelSimulationLinkTable.table.simRef.getQualifiedColName()+
		" AND "+
			BioModelSimulationLinkTable.table.bioModelRef.getQualifiedColName()+" = "+
				BioModelTable.table.id.getQualifiedColName()+
		" AND "+
			BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+" = "+
				BioModelTable.table.id.getQualifiedColName() +
		" AND "+
			SimContextTable.table.id.getQualifiedColName()+ " = " +
				BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+
		" AND "+
			SimContextTable.table.mathRef.getQualifiedColName()+" = "+
				SimulationTable.table.mathRef.getQualifiedColName()+
		")";

//	   OR
//	   (vc_simulation.parentsimref is not null
//	   AND vc_simulation.parentsimref = 27339386
//	   AND vc_simulation.ID = vc_biomodelsim.simref
//	   AND vc_biomodelsim.biomodelref = vc_biomodel.ID
//	   AND vc_biomodelsimcontext.biomodelref = vc_biomodel.ID
//	   AND vc_simcontext.ID = vc_biomodelsimcontext.simcontextref
//	   AND vc_simcontext.mathref = vc_simulation.mathref
//	   )

	String sqlFieldData =
		"SELECT "+
			ExternalDataTable.table.externalDataName.getQualifiedColName()+
		" FROM " +
			ExternalDataTable.table.getTableName()+
		" WHERE " +
			ExternalDataTable.table.ownerRef.getQualifiedColName() +" = " + user.getID() +
		" AND "+
			ExternalDataTable.table.id.getQualifiedColName()+ " = " +simulationKey.toString();	

	Statement stmt = null;
	FieldDataFileOperationResults.FieldDataReferenceInfo fieldDataRefInfo = null;
	try {
		stmt = 	getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sqlMathModel);
		if(rset.next()){
			fieldDataRefInfo = new FieldDataFileOperationResults.FieldDataReferenceInfo();
			fieldDataRefInfo.referenceSourceType = VersionableType.MathModelMetaData.getTypeName();
			fieldDataRefInfo.referenceSourceName = rset.getString(1);
			fieldDataRefInfo.simulationName = rset.getString(2);
			fieldDataRefInfo.refSourceVersionDate = rset.getString(3);
			fieldDataRefInfo.refSourceVersionKey = new KeyValue(rset.getBigDecimal(4));
		}else{
			rset.close();
			rset = stmt.executeQuery(sqlBioModel);
			if(rset.next()){
				fieldDataRefInfo = new FieldDataFileOperationResults.FieldDataReferenceInfo();
				fieldDataRefInfo.referenceSourceType = VersionableType.BioModelMetaData.getTypeName();
				fieldDataRefInfo.referenceSourceName = rset.getString(1);
				fieldDataRefInfo.applicationName = rset.getString(2);
				fieldDataRefInfo.simulationName = rset.getString(3);
				fieldDataRefInfo.refSourceVersionDate = rset.getString(4);
				fieldDataRefInfo.refSourceVersionKey = new KeyValue(rset.getBigDecimal(5));
			}else{
				rset.close();
				rset = stmt.executeQuery(sqlFieldData);
				if(rset.next()){
					fieldDataRefInfo = new FieldDataFileOperationResults.FieldDataReferenceInfo();
					fieldDataRefInfo.referenceSourceType = FieldDataFileOperationResults.FieldDataReferenceInfo.FIELDDATATYPENAME;
					fieldDataRefInfo.referenceSourceName = rset.getString(1);
				}				
			}
		}
	}catch(Exception e){
		e.printStackTrace();
		throw new DataAccessException("Error: getModelDescriptionForSimulation",e);
	}finally {
		if(stmt != null){try{stmt.close();}catch(Exception e){e.printStackTrace();}}
	}
	return fieldDataRefInfo;
}
}
