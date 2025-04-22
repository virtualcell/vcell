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

import cbit.vcell.field.FieldDataDBEntry;
import cbit.vcell.field.FieldDataAllDBEntries;
import cbit.vcell.field.io.CopyFieldDataResult;
import cbit.vcell.field.io.FieldDataReferenceInfo;
import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.modeldb.*;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.SimulationData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionableType;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class FieldDataDBOperationDriver{
	private final static Logger lg = LogManager.getLogger(FieldDataDBOperationDriver.class);
	
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
			try{liveConnection.close();}catch(Exception e){lg.error(e.getMessage(), e);}
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
		String dbPassword = PropertyLoader.getSecretValue(PropertyLoader.dbPasswordValue, PropertyLoader.dbPasswordFile);
		try{
			Class.forName(dbDriverName);
			liveConnection = java.sql.DriverManager.getConnection(dbConnectURL,dbSchemaUser,dbPassword);
			liveConnection.setReadOnly(true);
			return liveConnection;
		}catch(Exception e){
			throw new Exception("FieldDataDBOperationDriver: Couldn't get database connection",e);
		}

	}

	public static CopyFieldDataResult copyFieldData(Connection con, KeyFactory keyFactory, User requester,
													ExternalDataIdentifier sourceID, String sourceAnnotation,
													String versionTypeName, String versionName) throws SQLException, DataAccessException {
			//get source ID an

			String newCopiedName = sourceID.getName();

			FieldDataAllDBEntries allRequesterIDs =
					FieldDataDBOperationDriver.getExtraDataIDs(con, keyFactory, requester, false);
			Set<String> usedNames = new HashSet<>();

			for (ExternalDataIdentifier edi : allRequesterIDs.ids){
				usedNames.add(edi.getName());
			}

			// Unique name
			while (usedNames.contains(newCopiedName)){
				newCopiedName = TokenMangler.getNextEnumeratedToken(newCopiedName);
			}

			//Add new ExternalDataIdentifier (FieldData ID) to DB
			//Copy source annotation

			//find orig annotation
			String copiedAnnotation =
				"Copy Field Data name used Field Data function\r\n"+
				"Source type: "+versionTypeName+"\r\n"+
				"Source owner: "+sourceID.getOwner().getName()+"\r\n"+
				"Source name: "+versionName+"\r\n"+
				"Original Field Data name: "+sourceID.getName()+"\r\n"+
				"New Field Data name: "+newCopiedName+"\r\n"+
				"Source Annotation: "+newCopiedName+"\r\n";
			copiedAnnotation += sourceAnnotation;
			//
			FieldDataDBEntry entry = new FieldDataDBEntry();
			entry.name = newCopiedName;
			entry.annotation = copiedAnnotation;
			ExternalDataIdentifier edi =
				saveExtraDataID(con,keyFactory, requester, entry);
//				errorCleanupExtDataIDV.add(fieldDataDBOperationResults.extDataID);
			CopyFieldDataResult copyNoConflictResult = new CopyFieldDataResult();
			copyNoConflictResult.newID = edi;
			copyNoConflictResult.oldID = sourceID;

			return copyNoConflictResult;
	}

	public static FieldDataAllDBEntries getExtraDataIDs(Connection con, KeyFactory keyFactory, User user,
														boolean bIncludeSimRefs) throws SQLException {
		String sql;
			ResultSet rset;
			if(bIncludeSimRefs){
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
						user.getID() +
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
					ExternalDataTable.table.ownerRef + "=" + user.getID() +
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
					if( !bIncludeSimRefs &&
						!extDataIDV.contains(extDataID)){
						extDataIDV.add(extDataID);
						extDataAnnotV.add(ExternalDataTable.table.getExternalDataAnnot(rset));
					}
					if(bIncludeSimRefs){
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
			FieldDataAllDBEntries fieldDataDBOperationResults = new FieldDataAllDBEntries();
			fieldDataDBOperationResults.ids = extDataIDV.toArray(new ExternalDataIdentifier[0]);
			fieldDataDBOperationResults.annotationsForIds = extDataAnnotV.toArray(new String[0]);
			fieldDataDBOperationResults.edisToSimRefs = extDataIDSimRefsH;
			return fieldDataDBOperationResults;
	}

	public static ExternalDataIdentifier saveExtraDataID(Connection con, KeyFactory keyFactory, User user,
															   FieldDataDBEntry fieldDataDBOperationSpec) throws DataAccessException, SQLException {
		if(!fieldDataDBOperationSpec.name.equals(
					TokenMangler.fixTokenStrict(fieldDataDBOperationSpec.name))){
				throw new DataAccessException("Error inserting Field Data name "+
						fieldDataDBOperationSpec.name+"\n"+
						"Field Data names can contain only letters,digits and underscores");
			}

			KeyValue newKey = keyFactory.getNewKey(con);
			String sql =
				"INSERT INTO "+ExternalDataTable.table.getTableName()+" "+
				ExternalDataTable.table.getSQLColumnList()+
				" VALUES "+
				ExternalDataTable.table.getSQLValueList(
						newKey,user,
						fieldDataDBOperationSpec.name,
						fieldDataDBOperationSpec.annotation);

			DbDriver.updateCleanSQL(con,sql);
			ExternalDataIdentifier[] fdiArr =
				FieldDataDBOperationDriver.getExtraDataIDs(con, keyFactory, user, false).ids;
			for (ExternalDataIdentifier edi : fdiArr) {
				if(edi.getName().equals(fieldDataDBOperationSpec.name)){
					return edi;
				}
			}
			throw new DataAccessException(
					"Unable to retrieve inserted ExternalDataIdentifier "+
					fieldDataDBOperationSpec.name);
	}

	public static void deleteFieldData(Connection con, KeyFactory keyFactory, User user,
															   ExternalDataIdentifier edi) throws SQLException {
		String sql =
				"DELETE" + " FROM " + ExternalDataTable.table.getTableName() +
				" WHERE " +
					ExternalDataTable.table.ownerRef + " = " + user.getID() +
					" AND " +
					ExternalDataTable.table.id + " = " + edi.getKey().toString();

			DbDriver.updateCleanSQL(con,sql);
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
			lg.error(e.getMessage(), e);
			throw new DataAccessException("Error: getAllExternalDataIdentifiers",e);
		}finally {
			if(stmt != null){try{stmt.close();}catch(Exception e){lg.error(e.getMessage(), e);}}
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
		lg.error(e.getMessage(), e);
		throw new DataAccessException("Error: getFunctionFileNamesAndSimKeys",e);
	}finally {
		if(stmt != null){try{stmt.close();}catch(Exception e){lg.error(e.getMessage(), e);}}
	}
	return functionNamesH;
}

	public static synchronized FieldDataReferenceInfo getModelDescriptionForSimulation(User user, KeyValue simulationKey)
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
	FieldDataReferenceInfo fieldDataRefInfo = null;
	try {
		stmt = 	getConnection().createStatement();
		ResultSet rset = stmt.executeQuery(sqlMathModel);
		if(rset.next()){
			fieldDataRefInfo = new FieldDataReferenceInfo();
			fieldDataRefInfo.referenceSourceType = VersionableType.MathModelMetaData.getTypeName();
			fieldDataRefInfo.referenceSourceName = rset.getString(1);
			fieldDataRefInfo.simulationName = rset.getString(2);
			fieldDataRefInfo.refSourceVersionDate = rset.getString(3);
			fieldDataRefInfo.refSourceVersionKey = new KeyValue(rset.getBigDecimal(4));
		}else{
			rset.close();
			rset = stmt.executeQuery(sqlBioModel);
			if(rset.next()){
				fieldDataRefInfo = new FieldDataReferenceInfo();
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
					fieldDataRefInfo = new FieldDataReferenceInfo();
					fieldDataRefInfo.referenceSourceType = FieldDataReferenceInfo.FIELDDATATYPENAME;
					fieldDataRefInfo.referenceSourceName = rset.getString(1);
				}				
			}
		}
	}catch(Exception e){
		lg.error(e.getMessage(), e);
		throw new DataAccessException("Error: getModelDescriptionForSimulation",e);
	}finally {
		if(stmt != null){try{stmt.close();}catch(Exception e){lg.error(e.getMessage(), e);}}
	}
	return fieldDataRefInfo;
}
}
