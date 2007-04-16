package cbit.vcell.field;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import cbit.sql.KeyValue;
import cbit.util.TokenMangler;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.ExternalDataTable;
import cbit.vcell.modeldb.MathDescExternalDataLinkTable;
import cbit.vcell.modeldb.MathDescTable;
import cbit.vcell.modeldb.SimulationTable;
import cbit.vcell.modeldb.UserTable;
import cbit.vcell.server.DataAccessException;
import cbit.vcell.server.User;
import cbit.vcell.simdata.ExternalDataIdentifier;

public class FieldDataDBOperationDriver {

	public static FieldDataDBOperationResults fieldDataDBOperation(Connection con, User user,
			FieldDataDBOperationSpec fieldDataDBOperationSpec) throws SQLException, DataAccessException {
		
		if(fieldDataDBOperationSpec.opType == FieldDataDBOperationSpec.FDDBOS_COPY_NO_CONFLICT){
			//get all current ExtDataIDs
			ExternalDataIdentifier[] existingExtDataIDArr =
				FieldDataDBOperationDriver.getExternaldataIdentifiers(con, user);
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
				FieldDataDBOperationDriver.getExternaldataIdentifiers(con, user);
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
	
	public static ExternalDataIdentifier[] getExternaldataIdentifiers(Connection con,User owner)
		throws SQLException,DataAccessException{
		return
			FieldDataDBOperationDriver.fieldDataDBOperation(
					con, owner,
					FieldDataDBOperationSpec.createGetExtDataIDsSpec(owner)
			).extDataIDArr;
	}
	
	public static HashMap<ExternalDataIdentifier,Vector<KeyValue>> getExtDataIDsAndSimRefs(Connection con,User owner)
		throws SQLException,DataAccessException{
		
		return
		FieldDataDBOperationDriver.fieldDataDBOperation(
				con, owner,
				FieldDataDBOperationSpec.createGetExtDataIDsSpecWithSimRefs(owner)
		).extdataIDAndSimRefH;
		
	}
	
	public static HashMap<User, Vector<ExternalDataIdentifier>> getAllExternalDataIdentifiers(Connection con)
		throws SQLException{
		
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
				
		Statement stmt = con.createStatement();
		HashMap<User, Vector<ExternalDataIdentifier>> allUserExtDataIDH =
			new HashMap<User, Vector<ExternalDataIdentifier>>();
		try {
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
		} finally {
			stmt.close();
		}
		return allUserExtDataIDH;
	}

}
