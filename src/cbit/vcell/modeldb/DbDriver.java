package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.math.BigDecimal;
import java.sql.*;
import cbit.sql.*;
import cbit.vcell.field.FieldDataDBOperationDriver;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.numericstest.EditTestCriteriaOPReportStatus;
import cbit.vcell.numericstest.RemoveTestResultsOP;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaCrossRefOPResults;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.server.*;
import cbit.vcell.simdata.ExternalDataIdentifier;

import java.util.Vector;
import java.util.Hashtable;

/**
 * This type was created in VisualAge.
 */
public abstract class DbDriver {
	//
	private static final int ORACLE_VARCHAR2_SIZE_LIMIT = 4000;
	public static final String INSERT_VARCHAR2_HERE = "INSERT_VARCHAR2_HERE";
	public static final String INSERT_CLOB_HERE = "INSERT_CLOB_HERE";
	//

	protected SessionLog log = null;

	private static KeyFactory keyFactory = null;
	
	protected DBCacheTable dbc = null;
	private static Hashtable groupAccessHash = new Hashtable();

	//private static Hashtable testSuiteHash = new Hashtable();

/**
 * DBService constructor comment.
 */
public DbDriver(DBCacheTable dbc,SessionLog sessionLog) {
	this.log = sessionLog;
	this.dbc = dbc;
}


public static void cleanupDeletedReferences(Connection con,User user,ExternalDataIdentifier extDataID,boolean bPrintOnly) throws SQLException{
	
	KeyValue extDataIDKey = extDataID.getKey();
	
	try {
		final String SIM_SUBQ = "in_sim";
		String sql =
			"DELETE FROM " + SimulationTable.table.getTableName()+						
			" WHERE " +
			SimulationTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
			" AND "+
			SimulationTable.table.id.getQualifiedColName() +
			" IN ( " +
				" SELECT "+SIM_SUBQ+"."+SimulationTable.table.id.getUnqualifiedColName()+
				" FROM "+
					SimulationTable.table.getTableName()+" "+SIM_SUBQ+" ,"+
					MathDescExternalDataLinkTable.table.getTableName()+
				" WHERE " +
					MathDescExternalDataLinkTable.table.extDataRef +" = " + 
						extDataIDKey.toString()+
					" AND "+
					SIM_SUBQ+"."+SimulationTable.table.mathRef.getUnqualifiedColName() +" = "+
						MathDescExternalDataLinkTable.table.mathDescRef.getQualifiedColName() +
			")";
//		"DELETE FROM " + SimulationTable.table.getTableName()+						
//		" WHERE " +
//		SimulationTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
//		" AND "+
//		SimulationTable.table.id.getQualifiedColName() +
//		" IN ( " +
//			" SELECT "+SIM_SUBQ+"."+SimulationTable.table.id.getUnqualifiedColName()+
//			" FROM "+
//				SimulationTable.table.getTableName()+" "+SIM_SUBQ+" ,"+
//				MathDescTable.table.getTableName()+","+
//				MathDescExternalDataLinkTable.table.getTableName()+
//			" WHERE " +
//				MathDescExternalDataLinkTable.table.extDataRef +" = " + 
//					extDataIDKey.toString()+
//				" AND "+
//				MathDescExternalDataLinkTable.table.mathDescRef +" = " + 
//					MathDescTable.table.id.getQualifiedColName()+
//				" AND " +
//				SIM_SUBQ+"."+SimulationTable.table.mathRef.getUnqualifiedColName() +" = "+
//				MathDescTable.table.id.getQualifiedColName() +
//				" AND "+
//				" NOT EXISTS ("+
//					"SELECT "+BioModelSimulationLinkTable.table.id.getQualifiedColName()+
//					" FROM "+BioModelSimulationLinkTable.table.getTableName()+
//					" WHERE "+
//					BioModelSimulationLinkTable.table.simRef.getQualifiedColName()+"="+
//						SIM_SUBQ+"."+SimulationTable.table.id.getUnqualifiedColName()+
//					" UNION "+
//					"SELECT "+MathModelSimulationLinkTable.table.id.getQualifiedColName()+
//					" FROM "+MathModelSimulationLinkTable.table.getTableName()+
//					" WHERE "+
//					MathModelSimulationLinkTable.table.simRef.getQualifiedColName()+"="+
//						SIM_SUBQ+"."+SimulationTable.table.id.getUnqualifiedColName()+
//
//				")"+
//		")";
			if(bPrintOnly){
				System.out.println(sql+";");
			}else{
				updateCleanSQL(con, sql);
			}
		
			
			
		final String SIMCONT_SUBQ = "in_simcont";
		sql =
			"DELETE FROM " + SimContextTable.table.getTableName()+						
			" WHERE " +
			SimContextTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
			" AND "+
			SimContextTable.table.id.getQualifiedColName() +
			" IN ( " +
				" SELECT "+SIMCONT_SUBQ+"."+SimContextTable.table.id.getUnqualifiedColName()+
				" FROM "+
					SimContextTable.table.getTableName()+" "+SIMCONT_SUBQ+" ,"+
					MathDescExternalDataLinkTable.table.getTableName()+
				" WHERE " +
					MathDescExternalDataLinkTable.table.extDataRef +" = " + 
						extDataIDKey.toString()+
					" AND "+
					SIMCONT_SUBQ+"."+SimContextTable.table.mathRef.getUnqualifiedColName() +" = "+
						MathDescExternalDataLinkTable.table.mathDescRef.getQualifiedColName() +
			")";
//		"DELETE FROM " + SimContextTable.table.getTableName()+						
//		" WHERE " +
//		SimContextTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
//		" AND "+
//		SimContextTable.table.id.getQualifiedColName() +
//		" IN ( " +
//			" SELECT "+SIMCONT_SUBQ+"."+SimContextTable.table.id.getUnqualifiedColName()+
//			" FROM "+
//			SimContextTable.table.getTableName()+" "+SIMCONT_SUBQ+" ,"+
//				MathDescTable.table.getTableName()+","+
//				MathDescExternalDataLinkTable.table.getTableName()+
//			" WHERE " +
//				MathDescExternalDataLinkTable.table.extDataRef +" = " + 
//					extDataIDKey.toString()+
//				" AND "+
//				MathDescExternalDataLinkTable.table.mathDescRef +" = " + 
//					MathDescTable.table.id.getQualifiedColName()+
//				" AND " +
//				SIMCONT_SUBQ+"."+SimContextTable.table.mathRef.getUnqualifiedColName() +" = "+
//				MathDescTable.table.id.getQualifiedColName() +
//				" AND "+
//				" NOT EXISTS ("+
//					"SELECT "+BioModelSimContextLinkTable.table.id.getQualifiedColName()+
//					" FROM "+BioModelSimContextLinkTable.table.getTableName()+
//					" WHERE "+
//					BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+"="+
//						SIMCONT_SUBQ+"."+SimContextTable.table.id.getUnqualifiedColName()+
//				")"+
//		")";
			if(bPrintOnly){
				System.out.println(sql+";");
			}else{
				updateCleanSQL(con, sql);
			}

			
			
		sql =
			"DELETE FROM " + MathDescTable.table.getTableName()+						
			" WHERE " +
			MathDescTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
			" AND "+
			MathDescTable.table.id.getQualifiedColName() +" IN "+
			"(SELECT "+
				MathDescExternalDataLinkTable.table.mathDescRef.getQualifiedColName()+
				" FROM "+MathDescExternalDataLinkTable.table.getTableName()+
				" WHERE "+
				MathDescExternalDataLinkTable.table.extDataRef+" = "+extDataIDKey.toString()+
			")";
			if(bPrintOnly){
				System.out.println(sql+";");
			}else{
				updateCleanSQL(con, sql);
			}
			
	} catch (SQLException e) {
		String sqlState = e.getSQLState();
		if(!sqlState.startsWith("23")){//integrity constraint violation 
			throw e;
		}
	}

		
		
		
		
		
				
//	String sql =
//		"DELETE FROM " +SimulationTable.table.getTableName()+							
//		" WHERE " +
//		SimulationTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
//		" AND "+
//		SimulationTable.table.id.getQualifiedColName()+				
//		" IN ( "+									
//			" SELECT "+SimulationTable.table.id.getQualifiedColName()+
//			" FROM "+SimulationTable.table.getTableName()+						
//			" WHERE " +
//				SimulationTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
//			" MINUS "+							
//			" SELECT "+BioModelSimulationLinkTable.table.simRef.getQualifiedColName() +
//			" FROM "+BioModelSimulationLinkTable.table.getTableName()+			
//			" MINUS "+							
//			" SELECT "+MathModelSimulationLinkTable.table.simRef.getQualifiedColName()+
//			" FROM "+MathModelSimulationLinkTable.table.getTableName()+			
//			" MINUS "+								
//			" SELECT DISTINCT "+
//				SimulationTable.table.versionParentSimRef_ColumnName+
//				" FROM "+SimulationTable.table.getTableName()+
//				" WHERE "+SimulationTable.table.versionParentSimRef_ColumnName+" IS NOT NULL"+	
//		")";
//	if(bPrintOnly){
//		System.out.println(sql+";");
//	}else{
//		updateCleanSQL(con, sql);
//	}
//	
//	sql =
//		"DELETE FROM "+SimContextTable.table.getTableName()+					
//		" WHERE "+
//		SimContextTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
//		" AND "+
//		SimContextTable.table.id.getQualifiedColName()+
//		" NOT IN ("+				
//			" SELECT "+BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+
//			" FROM "+BioModelSimContextLinkTable.table.getTableName()+
//		")";	
//	if(bPrintOnly){
//		System.out.println(sql+";");
//	}else{
//		updateCleanSQL(con, sql);
//	}
//	
//	sql = 
//		"DELETE FROM "+MathDescTable.table.getTableName()+						
//		" WHERE "+
//		MathDescTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
//		" AND "+
//		MathDescTable.table.id.getQualifiedColName()+
//		" IN ( "+									
//			" SELECT "+MathDescTable.table.id.getQualifiedColName()+
//			" FROM "+ MathDescTable.table.getTableName()+					
//			" WHERE "+
//				MathDescTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
//			" MINUS	"+							
//			" SELECT "+SimContextTable.table.mathRef.getQualifiedColName()+
//			" FROM "+SimContextTable.table.getTableName()+
//			" WHERE "+SimContextTable.table.mathRef.getQualifiedColName()+" IS NOT NULL "+	
//			" MINUS	"+								
//			" SELECT "+MathModelTable.table.mathRef.getQualifiedColName()+
//			" FROM "+MathModelTable.table.getTableName()+				
//			" MINUS "+							
//			" SELECT "+SimulationTable.table.mathRef.getQualifiedColName()+						
//			" FROM "+SimulationTable.table.getTableName()+
//			" WHERE "+SimulationTable.table.mathRef.getQualifiedColName()+" IS NOT NULL	"+	
//		")";										
//	if(bPrintOnly){
//		System.out.println(sql+";");
//	}else{
//		updateCleanSQL(con, sql);
//	}
//	
//	sql =
//		"DELETE FROM "+GeometryTable.table.getTableName()+							
//		" WHERE	"+								
//		GeometryTable.table.ownerRef.getQualifiedColName()+" = "+user.getID().toString()+
//		" AND "+
//		GeometryTable.table.dimension.getQualifiedColName()+"=0"+						
//		" AND "+								
//		GeometryTable.table.id.getQualifiedColName()+
//		" NOT IN ( "+									
//			" SELECT "+SimContextTable.table.geometryRef.getQualifiedColName()+
//			" FROM "+ SimContextTable.table.getTableName()+		
//			" UNION	"+								
//			" SELECT "+MathDescTable.table.geometryRef.getQualifiedColName()+
//			" FROM "+MathDescTable.table.getTableName()+				
//		")";										
//	if(bPrintOnly){
//		System.out.println(sql+";");
//	}else{
//		updateCleanSQL(con, sql);
//	}
//	
//	sql=
//		"DELETE FROM "+ModelTable.table.getTableName()+						
//		" WHERE "+ModelTable.table.id.getQualifiedColName()+
//		" NOT IN ( "+									
//			"SELECT "+BioModelTable.table.modelRef.getQualifiedColName()+
//			" FROM "+BioModelTable.table.getTableName()+				
//			" UNION	"+							
//			" SELECT "+SimContextTable.table.modelRef.getQualifiedColName()+
//			" FROM "+SimContextTable.table.getTableName()+			
//		")";										
//	if(bPrintOnly){
//		System.out.println(sql+";");
//	}else{
//		updateCleanSQL(con, sql);
//	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 10:44:52 AM)
 */
public static cbit.vcell.document.VCDocumentInfo curate(CurateSpec curateSpec,Connection con,User user,DBCacheTable currentCache) throws DataAccessException,SQLException{

	VersionableType vType = null;
	if(curateSpec.getVCDocumentInfo() instanceof cbit.vcell.biomodel.BioModelInfo){
		vType = VersionableType.BioModelMetaData;
	}else if(curateSpec.getVCDocumentInfo() instanceof cbit.vcell.mathmodel.MathModelInfo){
		vType = VersionableType.MathModelMetaData;
	}else{
		throw new DataAccessException("Expecting BioModelInfo or MathModelInfo but got type="+curateSpec.getVCDocumentInfo().getClass().getName());
	}
	KeyValue vKey = curateSpec.getVCDocumentInfo().getVersion().getVersionKey();

	
	Version dbVersion = getVersionFromKeyValue(con,vType,vKey);
	//Must be owner to curate
	if (!dbVersion.getOwner().compareEqual(user)){
		throw new PermissionException("Cannot curate "+vType.getTypeName()+" \""+dbVersion.getName()+"\" ("+vKey+"), not owned by "+user.getName());
	}
	
	
	VersionFlag updatedVersionFlag = null;
	if(curateSpec.getCurateType() == CurateSpec.ARCHIVE){
		if(!dbVersion.getFlag().compareEqual(VersionFlag.Current)){
			throw new IllegalArgumentException("Only non-archived, non-published documents can be ARCHIVED");
		}
		updatedVersionFlag = VersionFlag.Archived;
	}else if(curateSpec.getCurateType() == CurateSpec.PUBLISH){
		//Must have PUBLISH rights
		if(!dbVersion.getOwner().isPublisher()){
			throw new PermissionException("Cannot curate "+vType.getTypeName()+" \""+dbVersion.getName()+"\" ("+vKey+"), user "+user.getName()+" not granted PUBLISHING rights");
		}
		//Must be ARCHIVED and Public before PUBLISH is allowed
		if(!dbVersion.getFlag().compareEqual(VersionFlag.Archived) || !(dbVersion.getGroupAccess() instanceof GroupAccessAll)){
			throw new IllegalArgumentException("Only ARCHIVED documents with PUBLIC permission can be PUBLISHED");
		}
		updatedVersionFlag = VersionFlag.Published;
	}else{
		throw new DataAccessException("Expecting CurateType "+CurateSpec.ARCHIVE+"(ARCHIVE) or "+CurateSpec.PUBLISH+"(PUBLISH) but got type="+curateSpec.getCurateType());
	}

	VersionTable vTable = VersionTable.getVersionTable(vType);
	String set = vTable.versionFlag.getQualifiedColName() + " = " + updatedVersionFlag.getIntValue();
	String cond = vTable.id.getQualifiedColName() + " = " + vKey;
	String sql = DatabasePolicySQL.enforceOwnershipUpdate(user,vTable,set,cond);
	int numRowsProcessed = updateCleanSQL(con, sql);

	currentCache.remove(vKey);
	//Clear XML
	if(vType.equals(VersionableType.BioModelMetaData)){
		updateCleanSQL(con,"DELETE FROM "+BioModelXMLTable.table.getTableName()+" WHERE "+BioModelXMLTable.table.bioModelRef.getQualifiedColName()+" = "+vKey.toString());
	}else if(vType.equals(VersionableType.MathModelMetaData)){
		updateCleanSQL(con,"DELETE FROM "+MathModelXMLTable.table.getTableName()+" WHERE "+MathModelXMLTable.table.mathModelRef.getQualifiedColName()+" = "+vKey.toString());
	}

	
	cbit.vcell.document.VCDocumentInfo dbVCDocumentInfo = (cbit.vcell.document.VCDocumentInfo)getVersionableInfos(con,null,user,vType,false,vKey,false).elementAt(0);
	return dbVCDocumentInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (9/18/2006 1:20:15 PM)
 * @return cbit.vcell.simdata.FieldDataInfo[]
 * @param conn java.sql.Connection
 * @param user cbit.vcell.server.User
 * @param fieldNames java.lang.String[]
 */
public static FieldDataDBOperationResults fieldDataDBOperation(Connection con, User user,
		FieldDataDBOperationSpec fieldDataDBOperationSpec) throws SQLException, DataAccessException {
	
	return FieldDataDBOperationDriver.fieldDataDBOperation(con, user, fieldDataDBOperationSpec);
}


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
public abstract void deleteVersionable(Connection con, User user,VersionableType vType,KeyValue versionKey) 
					throws DependencyException, ObjectNotFoundException, SQLException,DataAccessException,
							PermissionException;


/**
 * This method was created in VisualAge.
 * @param user cbit.vcell.server.User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
protected void deleteVersionableInit(Connection con, User user, VersionableType vType, KeyValue versionKey) 
					throws DependencyException,ObjectNotFoundException,SQLException,DataAccessException,
							PermissionException {
	
	Vector versionInfoVector = getVersionableInfos(con,log,user,vType,true,versionKey,true);
	if(versionInfoVector.size() == 0){
		throw new ObjectNotFoundException("DbDriver:deleteVersionableInit "+vType.getTypeName()+"("+versionKey+") not found for user="+user);
	}
	else if (versionInfoVector.size() > 1){
		throw new DataAccessException("DbDriver:deleteVersionableInit "+vType.getTypeName()+"("+versionKey+") found more than one entry  DB ERROR,BAD!!!!!MUST CHECK");
	}
	VersionInfo versionInfo = (VersionInfo)versionInfoVector.firstElement();
	//
	// Cannot delete ARCHIVED or PUBLISHED
	//
	if(versionInfo.getVersion().getFlag().compareEqual(VersionFlag.Archived) || versionInfo.getVersion().getFlag().compareEqual(VersionFlag.Published)){
		throw new DataAccessException(
			"DbDriver:deleteVersionableInit "+vType.getTypeName()+"("+versionKey+") Cannot DELETE\n"+
			"because document "+versionInfo.getVersion().getName()+"("+versionInfo.getVersion().getDate()+") is "+
			(versionInfo.getVersion().getFlag().compareEqual(VersionFlag.Archived)?"ARCHIVED":"PUBLISHED")
			);
	}
	//
	// if not owner then getVersionableInfo failed (returned record)
	//
	if(!versionInfo.getVersion().getOwner().compareEqual(user)){
		throw new PermissionException("deletion failed on "+vType.getTypeName()+" '"+versionInfo.getVersion().getName()+"' ("+versionKey+"), not owned by user "+user);
	}
	//
	// if 'Archived', then don't allow deletion
	//
	if(versionInfo.getVersion().getFlag().isArchived()){
		throw new PermissionException("deletion failed on archived "+vType.getTypeName()+" '"+versionInfo.getVersion().getName()+"' ("+versionKey+")");
	}
	//
	if (isBranchPointOrBaseSimulation(con,vType, versionKey)) {
		throw new DependencyException( VersionTable.getVersionTable(vType) + ",id=" + versionKey + " can't be deleted, it is either a branch point or a base Simulation");
	}
	//if(VersionTable.hasExternalRef(con,user,vType,versionKey)){
	//	throw new DependencyException( vTable + ",id=" + versionKey + " has external references");
	//}
	updateCleanSQL(con,
		"DELETE FROM "+SoftwareVersionTable.table.getTableName()+
		" WHERE "+SoftwareVersionTable.table.versionableRef.toString() + " = " + versionKey.toString());






	
}


/**
 * Insert the method's description here.
 * Creation date: (6/20/2004 5:02:15 PM)
 */
public static void failOnExternalRefs(
    java.sql.Connection con,
    cbit.sql.Field refFromField,
    Table refFromTable,
    KeyValue refToKeyValue,
	Table refToTable)
    throws SQLException, DependencyException {

    KeyValue[] externalRefs = getExternalRefs(con, refFromField, refFromTable, refToKeyValue);
    if (externalRefs != null && externalRefs.length > 0) {
        StringBuffer sb = new StringBuffer();
        try{
			sb.append(
			refToTable.getClass().getName()+"("+refToTable.getTableName()+")"
			+ " id="
			+ refToKeyValue
			+ ", has references from "
			+ refFromTable.getClass().getName()+"("+refFromTable.getTableName()+")"
			+ " id"+(externalRefs.length > 1?"s":"")+"={");
	        for (int i = 0; i < externalRefs.length; i += 1) {
	            if (i != 0) {
	                sb.append(",");
	            }
	            sb.append(externalRefs[i].toString());
	        }
	        sb.append("}");
        }catch(Throwable e){
	        sb.append(" --Error creating Message "+e.getMessage());
        }finally{
	        throw new DependencyException(sb.toString());
        }
    }

}


/**
 * This method was created in VisualAge.
 * @return java.util.Hashtable
 */
private static void findAllChildren(java.sql.Connection con, VersionableTypeVersion vtv, VersionableFamily refs) throws DataAccessException, SQLException {

	//Get VersionableTypes(tables) which possibly are children of argument vType
	Vector possibleRefs = VersionTable.getChildVersionableTypes(vtv.getVType());
	java.util.Enumeration enum1 = possibleRefs.elements();
	UserTable userTable = UserTable.table;
	while (enum1.hasMoreElements()) {
		VersionRef vr = (VersionRef) enum1.nextElement();
		
		//BEGIN check VersionableType for children of versionKey
		String sql = null;
		VersionTable table = VersionTable.getVersionTable(vr.getVType());
		VersionTable vtvTable = VersionTable.getVersionTable(vtv.getVType());
		if (vr.getLinkField()==null){
			//
			// direct link between versionable types
			//
			sql = 	"SELECT " + table.id.getQualifiedColName() + "," + 
							table.name.getQualifiedColName() + "," + 
							table.ownerRef.getQualifiedColName() + "," + 
							table.privacy.getQualifiedColName() + "," + 
							table.versionBranchPointRef.getQualifiedColName() + "," + 
							table.versionDate.getQualifiedColName() + "," + 
							table.versionFlag.getQualifiedColName() + "," + 
							table.versionAnnot.getQualifiedColName() + "," + 
							table.versionBranchID.getQualifiedColName() + "," +
							userTable.userid.getQualifiedColName() +
				" FROM " + table.getTableName() + "," + userTable.getTableName() + "," + vtvTable.getTableName()+
				" WHERE " + vr.getRefField().getQualifiedColName() + " = " +  table.id.getQualifiedColName()+
				" AND " + vtvTable.id.getQualifiedColName()+ " = " + vtv.getVersion().getVersionKey()+
				" AND " + table.ownerRef.getQualifiedColName() + " = " + userTable.getTableName() + "." + userTable.id;
		}else{
			//
			// indirect link between versionable types (Link Table ... e.g. BioModelSimulationLinkTable)
			//
			sql = 	"SELECT " + table.id.getQualifiedColName() + "," + 
							table.name.getQualifiedColName() + "," + 
							table.ownerRef.getQualifiedColName() + "," + 
							table.privacy.getQualifiedColName() + "," + 
							table.versionBranchPointRef.getQualifiedColName() + "," + 
							table.versionDate.getQualifiedColName() + "," + 
							table.versionFlag.getQualifiedColName() + "," + 
							table.versionAnnot.getQualifiedColName() + "," + 
							table.versionBranchID.getQualifiedColName() + "," +
							userTable.userid.getQualifiedColName() +
				" FROM " + table.getTableName() + "," + userTable.getTableName() + "," + vr.getLinkField().getTableName() +
				" WHERE " + vr.getRefField().getQualifiedColName() + " = " + table.id.getQualifiedColName() +
				" AND " + vr.getLinkField().getQualifiedColName() + " = " + vtv.getVersion().getVersionKey() +
				" AND " + table.ownerRef.getQualifiedColName() + " = " + userTable.id.getQualifiedColName();
		}
		java.sql.Statement stmt = con.createStatement();
		Vector allChildrenVTV = new Vector();
		try { //Get KeyValues from statement and put into Vector, so we can close statement(good idea because we are recursive)
			java.sql.ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				try{
					BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
					Version version = VersionTable.getVersion(rset,getGroupAccessFromGroupID(con,groupid),null);
					VersionableTypeVersion childVTV = new VersionableTypeVersion(vr.getVType(),version);
					allChildrenVTV.addElement(childVTV);
				} catch(Throwable e) {
					throw new DataAccessException(e.getMessage());
				}
			}
		} finally {
			stmt.close();
		}
		//END check VersionableType for Children of versionKey
		//
		for (int c = 0; c < allChildrenVTV.size(); c += 1) {
			VersionableTypeVersion childVTV = (VersionableTypeVersion)allChildrenVTV.elementAt(c);
			//
			// Add VersionableRelationship to children of refs
			//
			refs.addChildRelationship(new VersionableRelationship(vtv,childVTV));
			//
			// Check referencingVTV for children to it(Recursion)
			//
			findAllChildren(con, childVTV, refs);
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return java.util.Hashtable
 */
private static void findAllReferences(java.sql.Connection con, VersionableTypeVersion vtv, VersionableFamily refs) throws DataAccessException, SQLException {

	//Get VersionableTypes(tables) which possibly have references to argument vType
	Vector possibleRefs = VersionTable.getReferencingVersionableTypes(vtv.getVType());
	java.util.Enumeration enum1 = possibleRefs.elements();
	UserTable userTable = UserTable.table;
	while (enum1.hasMoreElements()) {
		VersionRef vr = (VersionRef) enum1.nextElement();
		
		//BEGIN check VersionableType for references to versionKey
		String sql = null;
		VersionTable table = VersionTable.getVersionTable(vr.getVType());
		if (vr.getLinkField()==null){
			//
			// direct link between versionable types
			//
			sql = 	"SELECT " + table.id.getQualifiedColName() + "," + 
							table.name.getQualifiedColName() + "," + 
							table.ownerRef.getQualifiedColName() + "," + 
							table.privacy.getQualifiedColName() + "," + 
							table.versionBranchPointRef.getQualifiedColName() + "," + 
							table.versionDate.getQualifiedColName() + "," + 
							table.versionFlag.getQualifiedColName() + "," + 
							table.versionAnnot.getQualifiedColName() + "," + 
							table.versionBranchID.getQualifiedColName() + "," +
							userTable.userid.getQualifiedColName() +
				" FROM " + table.getTableName() + "," + userTable.getTableName() +
				" WHERE " + vr.getRefField().getQualifiedColName() + " = " + vtv.getVersion().getVersionKey() +
				" AND " + table.ownerRef.getQualifiedColName() + " = " + userTable.getTableName() + "." + userTable.id;
		}else{
			//
			// indirect link between versionable types (Link Table ... e.g. BioModelSimulationLinkTable)
			//
			sql = 	"SELECT " + table.id.getQualifiedColName() + "," + 
							table.name.getQualifiedColName() + "," + 
							table.ownerRef.getQualifiedColName() + "," + 
							table.privacy.getQualifiedColName() + "," + 
							table.versionBranchPointRef.getQualifiedColName() + "," + 
							table.versionDate.getQualifiedColName() + "," + 
							table.versionFlag.getQualifiedColName() + "," + 
							table.versionAnnot.getQualifiedColName() + "," + 
							table.versionBranchID.getQualifiedColName() + "," +
							userTable.userid.getQualifiedColName() +
				" FROM " + table.getTableName() + "," + userTable.getTableName() + "," + vr.getLinkField().getTableName() + 
				" WHERE " + vr.getRefField().getQualifiedColName() + " = " + vtv.getVersion().getVersionKey() +
				" AND " + vr.getLinkField().getQualifiedColName() + " = " + table.id.getQualifiedColName() +
				" AND " + table.ownerRef.getQualifiedColName() + " = " + userTable.id.getQualifiedColName();
		}
		java.sql.Statement stmt = con.createStatement();
		Vector allReferencingVTV = new Vector();
		try { //Get KeyValues from statement and put into Vector, so we can close statement(good idea because we are recursive)
			java.sql.ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				try{
					BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
					Version version = VersionTable.getVersion(rset,getGroupAccessFromGroupID(con,groupid),null);
					VersionableTypeVersion referencingVTV = new VersionableTypeVersion(vr.getVType(),version);
					allReferencingVTV.addElement(referencingVTV);
				} catch(Throwable e) {
					throw new DataAccessException(e.getMessage());
				}
			}
		} finally {
			stmt.close();
		}
		//END check VersionableType for references to versionKey
		//
		for (int c = 0; c < allReferencingVTV.size(); c += 1) {
			VersionableTypeVersion referencingVTV = (VersionableTypeVersion)allReferencingVTV.elementAt(c);
			//
			// Add VersionableRelationship to dependants of refs
			//
			refs.addDependantRelationship(new VersionableRelationship(referencingVTV,vtv));
			//
			// Check referencingVTV for references to it(Recursion)
			//
			findAllReferences(con, referencingVTV, refs);
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return java.util.Hashtable
 */
public static VersionableFamily getAllReferences(java.sql.Connection con, VersionableType vType, KeyValue keyValue) throws DataAccessException, SQLException {
	VersionableTypeVersion vtv = new VersionableTypeVersion(vType, getVersionFromKeyValue(con,vType,keyValue));
	VersionableFamily refs = new VersionableFamily(vtv);
	findAllReferences(con, refs.getTarget(), refs);
	findAllChildren(con,refs.getTarget(),refs);
	return refs;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param con Connection
 * @param user User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
protected static KeyValue[] getChildrenFromLinkTable(java.sql.Connection con, Table linkTable, Field parentField, Field childField, KeyValue parentKey) 
							throws ObjectNotFoundException, java.sql.SQLException {
								
	String sql = null;
	sql = 	"SELECT " + linkTable.getTableName() + "." + childField + 
			" FROM " + linkTable.getTableName() + 
			" WHERE " + linkTable.getTableName() + "." + parentField + " = " + parentKey;

	java.sql.Statement stmt = con.createStatement();
	Vector keyList = new Vector();
	try {
		java.sql.ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			BigDecimal bigDecimal = rset.getBigDecimal(childField.toString());
			if (!rset.wasNull() && bigDecimal!=null){
				KeyValue keyValue = new KeyValue(bigDecimal);
				keyList.addElement(keyValue);
			}
		}
	} finally {
		stmt.close();
	}
	return (KeyValue[])cbit.util.BeanUtils.getArray(keyList,KeyValue.class);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param con Connection
 * @param user User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
private static KeyValue[] getExternalRefs(java.sql.Connection con,cbit.sql.Field field, Table table, KeyValue versionKey) 
							throws java.sql.SQLException {
								
	String sql = null;
	sql = 	"SELECT " + table.getTableName() + "." + table.id + 
			" FROM " + table.getTableName() + 
			" WHERE " + table.getTableName() + "." + field + " = " + versionKey;
			
	java.sql.Statement stmt = null;
	Vector externalRefsV = new Vector();
	try {
		stmt = con.createStatement();
		java.sql.ResultSet rset = stmt.executeQuery(sql);
		while(rset.next()){
			externalRefsV.add(new KeyValue(rset.getBigDecimal(1)));
		}
	} finally {
		if(stmt != null){
			stmt.close();
		}
	}
	if(externalRefsV.size() > 0){
		return (KeyValue[])externalRefsV.toArray(new KeyValue[externalRefsV.size()]);
	}
	return null;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param con Connection
 * @param user User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
protected static KeyValue getForeignRefByOwner(java.sql.Connection con,cbit.sql.Field field, VersionTable table, KeyValue idKey, VersionTable refTable, User owner) 
							throws ObjectNotFoundException, java.sql.SQLException {
								
	String sql = null;
	sql = 	"SELECT " + table.getTableName() + "." + field + 
			" FROM " + table.getTableName() + ", " + refTable.getTableName() + 
			" WHERE " + table.getTableName() + "." + table.id + " = " + idKey +
			" AND " + refTable.getTableName() + "." + refTable.id + " = " + table.getTableName() + "." + field +
			" AND " + refTable.getTableName() + "." + refTable.ownerRef + " = " + owner.getID();
			
	java.sql.Statement stmt = con.createStatement();
	try {
		java.sql.ResultSet rset = stmt.executeQuery(sql);
		if (rset.next()) {
			BigDecimal bigDecimal = rset.getBigDecimal(field.toString());
			if (!rset.wasNull() && bigDecimal!=null){
				KeyValue keyValue = new KeyValue(bigDecimal);
				return keyValue;
			}
		}
	} finally {
		stmt.close();
	}
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 3:24:17 PM)
 * @return cbit.vcell.server.GroupAccess
 * @param groupid java.math.BigDecimal
 */
public static GroupAccess getGroupAccessFromGroupID(java.sql.Connection con,BigDecimal groupid) throws SQLException{

	if(groupAccessHash.contains(groupid)){
		return (GroupAccess)groupAccessHash.get(groupid);
	}

	GroupAccess groupAccess = null;
	
	if(groupid.equals(GroupAccess.GROUPACCESS_ALL)){
		groupAccess = new GroupAccessAll();
	}else if(groupid.equals(GroupAccess.GROUPACCESS_NONE)){
		groupAccess = new GroupAccessNone();
	}
	else{
		GroupTable groupTable = GroupTable.table;
		StarField groupAllFields = new StarField(groupTable);
		UserTable userTable = UserTable.table;
		String sql =
				"SELECT "+	groupAllFields.getQualifiedColName()+","+
							userTable.userid.getQualifiedColName()+
				" FROM "+	groupTable.getTableName()+","+
							userTable.getTableName()+
				" WHERE "+	groupTable.groupid.getQualifiedColName() + " = " + groupid.toString() +
				" AND "+	userTable.id.getQualifiedColName()+ " = "+groupTable.userRef.getQualifiedColName();
		//
		java.sql.Statement stmt = con.createStatement();
		Vector groupMembers = new Vector();
		Vector hiddenFromOwner= new Vector();
		BigDecimal groupMemberHash = null;
		try {
			java.sql.ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				if(groupMemberHash == null){
					groupMemberHash = rset.getBigDecimal(groupTable.groupMemberHash.toString());
				}
				User user = new User(rset.getString(userTable.userid.toString()),new KeyValue(rset.getBigDecimal(groupTable.userRef.toString())));
				groupMembers.add(user);
				boolean bHidden = (rset.getInt(groupTable.isHiddenFromOwner.toString())) == 1;
				hiddenFromOwner.add(new Boolean(bHidden));
			}
		} finally {
			stmt.close();
		}
		//
		boolean[] hiddenArr = new boolean[hiddenFromOwner.size()];
		for(int i = 0;i<hiddenArr.length;i+=1){
			hiddenArr[i] = ((Boolean)(hiddenFromOwner.get(i))).booleanValue();
		}
		User[] groupMembersArr = new User[groupMembers.size()];
		groupMembers.toArray(groupMembersArr);
		groupAccess = new GroupAccessSome(groupid,groupMemberHash,groupMembersArr,hiddenArr);
	}
	//
	groupAccessHash.put(groupid,groupAccess);
	return groupAccess; 
}


/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 3:50:56 PM)
 * @return java.lang.Object
 * @param rset oracle.jdbc.OracleResultSet
 * @param columnName java.lang.String
 */
protected static Object getLOB(ResultSet rset,String columnName)
	throws DataAccessException, SQLException {
	//This method returns (byte[] for BLOB) or a (String for CLOB)
	//
	//try to get the LOB
	Object lob_object = rset.getObject(columnName);
	if(rset.wasNull()){
		//return null if the column actually contains a null
		return null;
	}
	//If its a BLOB return a byte[]
	if (lob_object instanceof oracle.sql.BLOB) {
		oracle.sql.BLOB blob_object = (oracle.sql.BLOB) lob_object;
		try{
			return blob_object.getBytes((long) 1, (int) blob_object.length());
		}catch(Exception e){
			throw new DataAccessException(e.toString());
		}
	//If its a CLOB return a String
	} else
		if (lob_object instanceof oracle.sql.CLOB) {
			oracle.sql.CLOB clob_object = (oracle.sql.CLOB) lob_object;
			byte[] ins = new byte[(int) clob_object.length()];
			try {
				clob_object.getAsciiStream().read(ins);
			} catch (Exception e) {
				throw new DataAccessException(e.toString());
			}
			return new String(ins);
		}
	//
	throw new DataAccessException(
		"ResultSet column=" + columnName + " was not a BLOB or CLOB");

}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
private java.math.BigDecimal getNewBranchID(Connection con) throws java.sql.SQLException {
	return keyFactory.getUniqueBigDecimal(con);
}


/**
 * This method was created in VisualAge.
 * @return java.util.Date
 * @param con java.sql.Connection
 */
private java.util.Date getNewDate(java.sql.Connection con) {
	return new java.util.Date();
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
private static java.math.BigDecimal getNewGroupID(Connection con) throws java.sql.SQLException {
	return keyFactory.getUniqueBigDecimal(con);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 */
public static KeyValue getNewKey(Connection con) throws java.sql.SQLException {
	return keyFactory.getNewKey(con);
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public static cbit.util.Preference[] getPreferences(Connection con, User user) throws SQLException {
	
	String sql =
		"SELECT " + 
			UserPreferenceTable.table.userPrefKey.getQualifiedColName() + "," +
			UserPreferenceTable.table.userPrefValue.getQualifiedColName() +
		" FROM " + 
			UserPreferenceTable.table.getTableName() +
		" WHERE " + 
			user.getID().toString() + " = " + UserPreferenceTable.table.userRef.getQualifiedColName();
				
			
	Statement stmt = null;
	cbit.util.Preference[] preferences = null;
	try {
		stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		preferences = UserPreferenceTable.table.getUserPreferences(rset);
	} finally {
		if(stmt != null){
			stmt.close();
		}
	}
	return preferences;
}

public static KeyValue[] getMathDescKeysForExternalData(Connection con,User owner,KeyValue extDataKey) throws SQLException{
	Statement stmt;
	String sql;
	ResultSet rset;
	sql = 	"SELECT " + MathDescExternalDataLinkTable.table.mathDescRef.getQualifiedColName() + 
			" FROM " +
				MathDescExternalDataLinkTable.table.getTableName() + "," +
				ExternalDataTable.table.getTableName() +
			" WHERE " + 
				ExternalDataTable.table.ownerRef.getQualifiedColName() + " = " + owner.getID().toString()+
				" AND " +
				ExternalDataTable.table.id.getQualifiedColName() + " = " + extDataKey.toString()+
				" AND " +
				MathDescExternalDataLinkTable.table.extDataRef.getQualifiedColName() + " = " +
					ExternalDataTable.table.id.getQualifiedColName();
			

	stmt = con.createStatement();
	Vector<KeyValue> mathDescKeys = new Vector<KeyValue>();
	try {
		rset = stmt.executeQuery(sql);
		while (rset.next()) {
			mathDescKeys.add(
					new KeyValue(
						rset.getBigDecimal(
								MathDescExternalDataLinkTable.table.mathDescRef.toString()))
					);
		}
	} finally {
		stmt.close();
	}
	return mathDescKeys.toArray(new KeyValue[0]);
	
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
private static User getUserFromUserid(Connection con, String userid) throws SQLException {
	Statement stmt;
	String sql;
	ResultSet rset;
	sql = 	"SELECT " + UserTable.table.id.getQualifiedColName() + 
			" FROM " + UserTable.table.getTableName() + 
			" WHERE " + UserTable.table.userid.getQualifiedColName() + " = '" + userid + "'";
			
	stmt = con.createStatement();
	User user = null;
	try {
		rset = stmt.executeQuery(sql);
		if (rset.next()) {
			KeyValue userKey = new KeyValue(rset.getBigDecimal(UserTable.table.id.toString()));
			user = new User(userid, userKey);
		}
	} finally {
		stmt.close();
	}
	return user;
}


/**
 * Insert the method's description here.
 * Creation date: (9/24/2003 12:54:32 PM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 */
public static VCInfoContainer getVCInfoContainer(User user,Connection con,SessionLog mySessionLog) throws SQLException,DataAccessException{

	VCInfoContainer results = null;
	//
	cbit.image.VCImageInfo[] vcImageInfos = null;
	cbit.vcell.geometry.GeometryInfo[] geometryInfos = null;
	cbit.vcell.mathmodel.MathModelInfo[] mathModelInfos = null;
	cbit.vcell.biomodel.BioModelInfo[] bioModelInfos = null;
	
	//
	StringBuffer sql = null;
	String special = null;
	ResultSet rset = null;
	Vector tempInfos = null;
	Vector distinctV = null;
	boolean enableSpecial = true;
	boolean enableDistinct = true;
	
	Statement stmt = con.createStatement();
	try{
		//
		//BioModelInfos
		//
		{
			double beginTime = System.currentTimeMillis();
			special = 	" ORDER BY " + 
						BioModelTable.table.name.getQualifiedColName() + "," + 
						BioModelTable.table.versionBranchID.getQualifiedColName() + "," + 
						BioModelTable.table.versionDate.getQualifiedColName();
			sql = new StringBuffer(BioModelTable.table.getInfoSQL(user,null,(enableSpecial?special:null)));
			sql.insert(7,Table.SQL_GLOBAL_HINT);
			rset = stmt.executeQuery(sql.toString());
			tempInfos = new Vector();
			distinctV = new Vector();
			while(rset.next()){
				cbit.vcell.biomodel.BioModelInfo versionInfo = (cbit.vcell.biomodel.BioModelInfo)BioModelTable.table.getInfo(rset,con,mySessionLog);
				if(!distinctV.contains(versionInfo.getVersion().getVersionKey().toString())){
					tempInfos.add(versionInfo);
					distinctV.add(versionInfo.getVersion().getVersionKey().toString());
				}
			}
			rset.close();
			if(tempInfos.size() > 0){
				bioModelInfos = new cbit.vcell.biomodel.BioModelInfo[tempInfos.size()];
				tempInfos.copyInto(bioModelInfos);
			}
			System.out.println("BioModelInfo Time="+(((double)System.currentTimeMillis()-beginTime)/(double)1000));
		}
		
		//
		//MathModelInfos
		//
		{
			double beginTime = System.currentTimeMillis();
			special = 	" ORDER BY " + 
						MathModelTable.table.name.getQualifiedColName() + "," + 
						MathModelTable.table.versionBranchID.getQualifiedColName() + "," + 
						MathModelTable.table.versionDate.getQualifiedColName();
			sql = new StringBuffer(MathModelTable.table.getInfoSQL(user,null,(enableSpecial?special:null)));
			sql.insert(7,Table.SQL_GLOBAL_HINT);
			rset = stmt.executeQuery(sql.toString());
			tempInfos = new Vector();
			distinctV = new Vector();
			while(rset.next()){
				cbit.vcell.mathmodel.MathModelInfo versionInfo = (cbit.vcell.mathmodel.MathModelInfo)MathModelTable.table.getInfo(rset,con,mySessionLog);
				if(!distinctV.contains(versionInfo.getVersion().getVersionKey().toString())){
					tempInfos.add(versionInfo);
					distinctV.add(versionInfo.getVersion().getVersionKey().toString());
				}
			}
			rset.close();
			if(tempInfos.size() > 0){
				mathModelInfos = new cbit.vcell.mathmodel.MathModelInfo[tempInfos.size()];
				tempInfos.copyInto(mathModelInfos);
			}
			System.out.println("MathModelInfo Time="+(((double)System.currentTimeMillis()-beginTime)/(double)1000));
		}
		
		//
		//VCImageInfos
		//
		{
			double beginTime = System.currentTimeMillis();
			special = 	" ORDER BY " + 
						ImageTable.table.name.getQualifiedColName() + "," + 
						ImageTable.table.versionBranchID.getQualifiedColName() + "," + 
						ImageTable.table.versionDate.getQualifiedColName();
			sql = new StringBuffer(ImageTable.table.getInfoSQL(user,null,(enableSpecial?special:null),true));
			sql.insert(7,Table.SQL_GLOBAL_HINT);
			rset = stmt.executeQuery(sql.toString());
			tempInfos = new Vector();
			distinctV = new Vector();
			while(rset.next()){
				cbit.image.VCImageInfo versionInfo = (cbit.image.VCImageInfo)ImageTable.table.getInfo(rset,con,mySessionLog);
				if(!distinctV.contains(versionInfo.getVersion().getVersionKey().toString())){
					tempInfos.add(versionInfo);
					distinctV.add(versionInfo.getVersion().getVersionKey().toString());
				}
			}
			rset.close();
			if(tempInfos.size() > 0){
				vcImageInfos = new cbit.image.VCImageInfo[tempInfos.size()];
				tempInfos.copyInto(vcImageInfos);
			}
			System.out.println("ImageInfo Time="+(((double)System.currentTimeMillis()-beginTime)/(double)1000));
		}
		
		//
		//GeometeryInfos
		//
		{
			double beginTime = System.currentTimeMillis();
			special = 	" ORDER BY " + 
						GeometryTable.table.name.getQualifiedColName() + "," + 
						GeometryTable.table.versionBranchID.getQualifiedColName() + "," + 
						GeometryTable.table.versionDate.getQualifiedColName();
			sql = new StringBuffer(GeometryTable.table.getInfoSQL(user,null,(enableSpecial?special:null),true));
			sql.insert(7,Table.SQL_GLOBAL_HINT+(enableDistinct?"DISTINCT ":""));
			rset = stmt.executeQuery(sql.toString());
			tempInfos = new Vector();
			distinctV = new Vector();
			while(rset.next()){
				cbit.vcell.geometry.GeometryInfo versionInfo = (cbit.vcell.geometry.GeometryInfo)GeometryTable.table.getInfo(rset,con,mySessionLog);
				if(!distinctV.contains(versionInfo.getVersion().getVersionKey().toString())){
					tempInfos.add(versionInfo);
					distinctV.add(versionInfo.getVersion().getVersionKey().toString());
				}
			}
			rset.close();
			if(tempInfos.size() > 0){
				geometryInfos = new cbit.vcell.geometry.GeometryInfo[tempInfos.size()];
				tempInfos.copyInto(geometryInfos);
			}
			System.out.println("GeometryInfo Time="+(((double)System.currentTimeMillis()-beginTime)/(double)1000));
		}

	
	}finally{
		if(stmt != null){
			stmt.close();
		}
	}

	results = new VCInfoContainer(user, vcImageInfos, geometryInfos, mathModelInfos, bioModelInfos);

	return results;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param user cbit.vcell.server.User
 * @param vType int
 */
public static Vector getVersionableInfos(Connection con,SessionLog gvilog,User user, VersionableType vType, boolean bAll,KeyValue versionKey,boolean bCheckPermission) 
							throws ObjectNotFoundException, SQLException, DataAccessException {
								
	if (user == null) {
		throw new IllegalArgumentException("Improper parameters for getVersionables");
	}
	//gvilog.print("DbDriver.getVersionableInfo(all=" + bAll + ",user=" + user + ", type="+vType+")");
	String sql;
	StringBuffer conditions = new StringBuffer();
	String special = null;
	VersionTable vTable = VersionTable.getVersionTable(vType);
	boolean bFirstClause = true;
	if (!bAll) {
		conditions.append(vTable.ownerRef.getQualifiedColName() + " = " + user.getID());
		bFirstClause = false;
	}
	if(versionKey != null){
		if (!bFirstClause){
			conditions.append(" AND ");
		}
		conditions.append(vTable.id.getQualifiedColName() + " = " + versionKey);
	}
	special = " ORDER BY " + vTable.name.getQualifiedColName() + "," + vTable.versionBranchID.getQualifiedColName() + "," + vTable.versionDate.getQualifiedColName();


	if (vType.equals(VersionableType.BioModelMetaData)){
		sql = ((BioModelTable)vTable).getInfoSQL(user,conditions.toString(),special);
	}else if (vType.equals(VersionableType.MathModelMetaData)){
		sql = ((MathModelTable)vTable).getInfoSQL(user,conditions.toString(),special);
	}else if (vType.equals(VersionableType.Simulation)){
		sql = ((SimulationTable)vTable).getInfoSQL(user,conditions.toString(),special);
	}else if (vType.equals(VersionableType.Geometry)){
		sql = ((GeometryTable)vTable).getInfoSQL(user,conditions.toString(),special,bCheckPermission);
	}else if (vType.equals(VersionableType.VCImage)){
		sql = ((ImageTable)vTable).getInfoSQL(user,conditions.toString(),special,bCheckPermission);
	}else if (vType.equals(VersionableType.Model)){
		sql = ((ModelTable)vTable).getInfoSQL(user,conditions.toString(),special);
	}else if (vType.equals(VersionableType.SimulationContext)){
		sql = ((SimContextTable)vTable).getInfoSQL(user,conditions.toString(),special);
	}else if (vType.equals(VersionableType.MathDescription)){
		sql = ((MathDescTable)vTable).getInfoSQL(user,conditions.toString(),special);
	}else{
		throw new RuntimeException("VersionInfo not availlable for type '"+vType.getTypeName()+"'");
	}
	//
	StringBuffer optimizedSQL = new StringBuffer(sql);
	optimizedSQL.insert(7, Table.SQL_GLOBAL_HINT);
	sql = optimizedSQL.toString();
	//
	//System.out.println("getVersionableInfo--->"+sql);
	VersionInfo vInfo;
	java.util.Vector vInfoList = new java.util.Vector();
	//Connection con = conFact.getConnection();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			if (vType.equals(VersionableType.BioModelMetaData)){
				vInfo = ((BioModelTable)vTable).getInfo(rset,con,gvilog);
			}else if (vType.equals(VersionableType.MathModelMetaData)){
				vInfo = ((MathModelTable)vTable).getInfo(rset,con,gvilog);
			}else if (vType.equals(VersionableType.Simulation)){
				vInfo = ((SimulationTable)vTable).getInfo(rset,con,gvilog);
			}else if (vType.equals(VersionableType.Geometry)){
				vInfo = ((GeometryTable)vTable).getInfo(rset,con,gvilog);
			}else if (vType.equals(VersionableType.VCImage)){
				vInfo = ((ImageTable)vTable).getInfo(rset,con,gvilog);
			}else if (vType.equals(VersionableType.Model)){
				vInfo = ((ModelTable)vTable).getInfo(rset,con,gvilog);
			}else if (vType.equals(VersionableType.SimulationContext)){
				vInfo = ((SimContextTable)vTable).getInfo(rset,con,gvilog);
			}else if (vType.equals(VersionableType.MathDescription)){
				vInfo = ((MathDescTable)vTable).getInfo(rset,con,gvilog);
			}else{
				throw new RuntimeException("VersionInfo not availlable for type '"+vType.getTypeName()+"'");
			}
			//
			// only add version info record to list if not a duplicate,
			// this occurs because the DatabasePolicySQL.enforceOwnershipSelect() can get the same record several ways.
			//
			VersionInfo previousVInfo = (vInfoList.size()>0)?(VersionInfo)vInfoList.lastElement():null;
			if (previousVInfo==null || !previousVInfo.getVersion().getVersionKey().compareEqual(vInfo.getVersion().getVersionKey())){
				vInfoList.addElement(vInfo);
			}
		}
	} finally {
		stmt.close();
	}
	//Object oInfoArray[] = new Object[oInfoList.size()];
	//oInfoList.copyInto(oInfoArray);
	//return oInfoArray;
	return vInfoList;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public String getVersionableXML(Connection con,VersionableType vType, KeyValue vKey) 
			throws ObjectNotFoundException, SQLException, DataAccessException {

	String xmlTableName = null;
	String versionableRefColName = null;
	String xmlColName = null;
	if (vType.equals(VersionableType.BioModelMetaData)){
		xmlTableName = BioModelXMLTable.table.getTableName();
		versionableRefColName = BioModelXMLTable.table.bioModelRef.toString();
		xmlColName = BioModelXMLTable.table.bmXML.toString();
	}else if(vType.equals(VersionableType.MathModelMetaData)){
		xmlTableName = MathModelXMLTable.table.getTableName();
		versionableRefColName = MathModelXMLTable.table.mathModelRef.toString();
		xmlColName = MathModelXMLTable.table.mmXML.toString();
	}else{
		throw new IllegalArgumentException("vType " + vType + " not supported by " + this.getClass());
	}
	//
	Statement s = null;
	try {
		s = con.createStatement();
		String sql = "SELECT " + xmlColName + " FROM " + xmlTableName +
					" WHERE " + versionableRefColName + " = " + vKey;
		//oracle.jdbc.OracleResultSet rset = (oracle.jdbc.OracleResultSet)s.executeQuery(sql);
		ResultSet rset = s.executeQuery(sql);
		if(rset.next()){
			return (String)getLOB(rset,xmlColName);
		}else{
			throw new ObjectNotFoundException("getVersionableXML for "+vType+" key="+vKey+" Not Found");
		}
	} finally {
		if(s != null){
			s.close();
		}
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Version
 * @param keyValue cbit.sql.KeyValue
 * @param vType cbit.sql.VersionableType
 */
private static Version getVersionFromKeyValue(Connection con,VersionableType vType,KeyValue keyValue) 
							throws SQLException,DataAccessException{
	Version version = null;
//
//Get a version from this keyValue
//
		String sql = null;
		VersionTable versionTable = VersionTable.getVersionTable(vType);
		UserTable userTable = UserTable.table;
		
		sql = 	"SELECT " + versionTable.getTableName() + "." + versionTable.id + "," + 
							versionTable.getTableName() + "." + versionTable.name + "," + 
							versionTable.getTableName() + "." + versionTable.ownerRef + "," + 
							versionTable.getTableName() + "." + versionTable.privacy + "," + 
							versionTable.getTableName() + "." + versionTable.versionBranchPointRef + "," + 
							versionTable.getTableName() + "." + versionTable.versionDate + "," + 
							versionTable.getTableName() + "." + versionTable.versionFlag + "," + 
							versionTable.getTableName() + "." + versionTable.versionAnnot + "," + 
							versionTable.getTableName() + "." + versionTable.versionBranchID + "," +
							userTable.getTableName() + "." + userTable.userid +
				" FROM " + versionTable.getTableName() + "," + userTable.getTableName() +
				" WHERE " + versionTable.getTableName() + "." + versionTable.id + " = " + keyValue +
				" AND " + versionTable.getTableName() + "." + versionTable.ownerRef + " = " + userTable.getTableName() + "." + userTable.id;
		java.sql.Statement stmt = con.createStatement();
		try {
			java.sql.ResultSet rset = stmt.executeQuery(sql);
			rset.next();
			BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
			version = VersionTable.getVersion(rset,getGroupAccessFromGroupID(con,groupid),null);
		} catch(Throwable e){
			e.printStackTrace(System.out);
			throw new DataAccessException("Error "+e.getMessage()+" getting version from VersionableType="+vType+" KeyValue="+keyValue);
		}finally {
			stmt.close();
		}
		
		return version;
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public static void groupAddUser(Connection con,SessionLog newLog, User owner,
										VersionableType vType, KeyValue vKey,
										DBCacheTable currentCache,String userAddToGroupString,boolean isHiddenFromOwner) 
							throws SQLException,ObjectNotFoundException, DataAccessException {


	User userAddToGroup = getUserFromUserid(con,userAddToGroupString);
	if(userAddToGroup == null){
		throw new IllegalArgumentException("User name "+userAddToGroupString+" not found");
	}
	//
	if ((con == null)||(newLog == null)||(vType == null)||(currentCache == null)||(owner == null) || (vKey == null)||(userAddToGroup==null)) {
		throw new IllegalArgumentException("Improper parameters for groupAddUser userAddToGroupString="+(userAddToGroupString == null?"NULL":userAddToGroupString));
	}
	//
	Version currentVersion = permissionInit(con,vType,vKey,owner);

	// If userAddToGroup is already in group it is an error
	// ----- Also can't add members to GroupAccessAll(Public) (CHANGED!!!) -----
	boolean bExists = false;
	if(currentVersion.getGroupAccess() instanceof GroupAccessSome){
		bExists = (((GroupAccessSome)currentVersion.getGroupAccess()).isNormalMember(userAddToGroup) && !isHiddenFromOwner)
					|| (((GroupAccessSome)currentVersion.getGroupAccess()).isHiddenMember(userAddToGroup) && isHiddenFromOwner);
	}
	//else if(currentVersion.getGroupAccess() instanceof GroupAccessAll){
		//bExists = true;
	//}
	if(currentVersion.getOwner().compareEqual(userAddToGroup) || bExists){
		throw new DataAccessException(userAddToGroup+" Already a member of group");
	}
	newLog.print("DbDriver.groupAddUser(user=" + owner + ", type =" + vType + ", key=" + vKey + ")");
	
	VersionTable vTable = VersionTable.getVersionTable(vType);
	//
	//
	BigDecimal newHash = null;
	//
	// Calculate what the new hash will be if we
	// add userAddToGroup to the currentVersion group
	//
	if(currentVersion.getGroupAccess() instanceof GroupAccessSome){
		// Calculate the hash of the currentVersion's group plus a new user
		GroupAccessSome currentGroup = (GroupAccessSome)currentVersion.getGroupAccess();
		newHash = currentGroup.calculateHashWithNewMember(userAddToGroup,isHiddenFromOwner);
	}else if (currentVersion.getGroupAccess() instanceof GroupAccessNone || currentVersion.getGroupAccess() instanceof GroupAccessAll){
		// Calculate hash for a new group with only userAddToGroup in it
		KeyValue[] kvArr = new KeyValue[1];
		boolean[] hiddenArr = new boolean[1];
		kvArr[0] = userAddToGroup.getID();
		hiddenArr[0] = isHiddenFromOwner;
		newHash = GroupAccess.calculateHash(kvArr,hiddenArr);
	}
	//
	BigDecimal updatedGroupID = null;
	//
	// See if the newly calculated hash is present in the database GroupTable
	// indicating a group we can reuse by reference
	//
	String sql = "SELECT groupid FROM "+GroupTable.table.getTableName()+" WHERE groupMemberHash = "+newHash.toString();
	java.sql.Statement stmt = con.createStatement();
	try {
		java.sql.ResultSet rset = stmt.executeQuery(sql);
		if(rset.next()){ //There may be more than one, just get the first, all have the same groupid and hash
			//Group already exists,Re-Use the groupid, we don't have to make a new group
			updatedGroupID = rset.getBigDecimal(GroupTable.table.groupid.toString());
		}
	} finally {
		stmt.close();
	}
	//
	// If hash wasn't found in db, make a new group, leave the old group alone
	//
	if(updatedGroupID == null){
		// Create new Group id
		updatedGroupID = getNewGroupID(con);
		int groupMemberCount = 1;
		// Get all the members of the currentVersion Group or skip if currentVersion group is GroupAccessNone
		// Don't worry about GroupAccessAll, we couldn't have gotten this far
		//
		// Add new User
		//
		sql = "INSERT INTO "+GroupTable.table.getTableName()+
				" VALUES ( "+
				getNewKey(con).toString()+","+
				updatedGroupID+","+
				userAddToGroup.getID().toString()+","+
				(isHiddenFromOwner?"1":"0")+","+
				newHash +
				" )";
		updateCleanSQL(con,sql);

		if(currentVersion.getGroupAccess() instanceof GroupAccessSome){
			// Add all the old Normal Users
			User[] normalUsers = ((GroupAccessSome)currentVersion.getGroupAccess()).getNormalGroupMembers();
			for(int i = 0;normalUsers!=null && i<normalUsers.length;i+= 1){
				String userRef = normalUsers[i].getID().toString();
				sql = "INSERT INTO "+GroupTable.table.getTableName()+
						" VALUES ( "+
						getNewKey(con).toString()+","+
						updatedGroupID+","+
						userRef+","+
						(false?"1":"0")+","+
						newHash +
						" )";
				updateCleanSQL(con,sql);
			}
			// Add all the old Hidden Users
			User[] hiddenUsers = ((GroupAccessSome)currentVersion.getGroupAccess()).getHiddenGroupMembers();
			for(int i = 0;hiddenUsers!=null && i<hiddenUsers.length;i+= 1){
				String userRef = hiddenUsers[i].getID().toString();
				sql = "INSERT INTO "+GroupTable.table.getTableName()+
						" VALUES ( "+
						getNewKey(con).toString()+","+
						updatedGroupID+","+
						userRef+","+
						(true?"1":"0")+","+
						newHash +
						" )";
				updateCleanSQL(con,sql);
			}
		}
	}
	//Update the vTable to point to the new Group
	String set = vTable.privacy.getQualifiedColName() + " = " + updatedGroupID;
	String cond = vTable.id.getQualifiedColName() + " = " + vKey;
				//" AND " + vTable.ownerRef.getQualifiedColName() + " = " + owner.getID();
	sql = DatabasePolicySQL.enforceOwnershipUpdate(owner,vTable,set,cond);
//System.out.println(sql);
	int numRowsProcessed = updateCleanSQL(con, sql);
	
	if (numRowsProcessed != 1){
		//
		// check if update failed
		//
		Vector versionInfoList = getVersionableInfos(con,newLog,owner,vType,false,vKey,true);
		if (versionInfoList.size()==0){
			throw new DataAccessException("Add User "+userAddToGroup+" Permission to access failed, "+vType.getTypeName()+"("+vKey+") record not found");
		}else{
			throw new DataAccessException("Add User "+userAddToGroup+" Permission to access failed "+vType.getTypeName()+"("+vKey+")");
		}
	}
	
	currentCache.remove(vKey);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public static void groupRemoveUser(Connection con,SessionLog newLog, User owner,
										VersionableType vType, KeyValue vKey,
										DBCacheTable currentCache,String userRemoveFromGroupString,boolean isHiddenFromOwner) 
							throws SQLException,ObjectNotFoundException, DataAccessException {


	User userRemoveFromGroup = getUserFromUserid(con,userRemoveFromGroupString);
	if(userRemoveFromGroup == null){
		throw new IllegalArgumentException("User name "+userRemoveFromGroupString+" not found");
	}
	//
	if ((con == null)||(newLog == null)||(vType == null)||(currentCache == null)||(owner == null) || (vKey == null)||(userRemoveFromGroup==null)) {
		throw new IllegalArgumentException("Improper parameters for groupRemoveUser userRemoveFromGroupString="+(userRemoveFromGroupString == null?"NULL":userRemoveFromGroupString));
	}
	//
	Version currentVersion = permissionInit(con,vType,vKey,owner);

	//If userRemoveFromGroup is not in group it is an error, or if not a "real" group
	boolean bExists = false;
	if(currentVersion.getGroupAccess() instanceof GroupAccessSome){
		bExists = (((GroupAccessSome)currentVersion.getGroupAccess()).isNormalMember(userRemoveFromGroup) && !isHiddenFromOwner)
					|| (((GroupAccessSome)currentVersion.getGroupAccess()).isHiddenMember(userRemoveFromGroup) && isHiddenFromOwner);
	}
	if(!bExists){
		throw new DataAccessException(userRemoveFromGroup+" not a member of group");
	}
	newLog.print("DbDriver.groupAccessRemoveUser(user=" + owner + ", type =" + vType + ", key=" + vKey + ")");
	
	GroupAccessSome currentGroup = (GroupAccessSome)currentVersion.getGroupAccess();
	VersionTable vTable = VersionTable.getVersionTable(vType);
	//
	// calculate hash after removing user (null if last user in group)
	//
	BigDecimal newHash = null;
	int count = (currentGroup.getNormalGroupMembers() != null?currentGroup.getNormalGroupMembers().length:0) + 
				(currentGroup.getHiddenGroupMembers() != null?currentGroup.getHiddenGroupMembers().length:0);
	if(count > 1){
		//See if group already exists without member and get it's ID
		newHash = currentGroup.calculateHashWithoutMember(userRemoveFromGroup,isHiddenFromOwner);
	}
	//
	BigDecimal updatedGroupID = null;
	String sql = null;
	//
	// lookup the pre-existing group using the hash calculated above.
	//
	if(newHash != null){
		sql = "SELECT groupid FROM "+GroupTable.table.getTableName()+" WHERE groupMemberHash = "+newHash.toString();
		java.sql.Statement stmt = con.createStatement();
		try {
			java.sql.ResultSet rset = stmt.executeQuery(sql);
			if(rset.next()){
				//Group already exists,Re-Use
				updatedGroupID = rset.getBigDecimal(GroupTable.table.groupid.toString());
			}
		} finally {
			stmt.close();
		}
	}else{
		//
		// user was last user in group, new group is the Private Group (AccessNone), groupID is predefined.
		//
		updatedGroupID = GroupAccess.GROUPACCESS_NONE;
	}
	//
	// group not found, must create new one
	//
	if(updatedGroupID == null){
		updatedGroupID = getNewGroupID(con);
		//
		// Re-Add Normal users not removed
		//
		User[] normalUsers = currentGroup.getNormalGroupMembers();
		for(int i = 0;normalUsers!=null && i<normalUsers.length;i+= 1){
			if(!(normalUsers[i].compareEqual(userRemoveFromGroup)) || isHiddenFromOwner){
				String userRef = normalUsers[i].getID().toString();
				sql = "INSERT INTO "+GroupTable.table.getTableName()+
						" VALUES ( "+
						getNewKey(con).toString()+","+
						updatedGroupID+","+
						userRef+","+
						(false?"1":"0")+","+
						newHash +
						" )";
				updateCleanSQL(con,sql);
			}
		}
		//
		// Re-Add Hidden users not removed
		//
		User[] hiddenUsers = currentGroup.getHiddenGroupMembers();
		for(int i = 0;hiddenUsers!=null && i<hiddenUsers.length;i+= 1){
			if(!(hiddenUsers[i].compareEqual(userRemoveFromGroup)) || !isHiddenFromOwner){
				String userRef = hiddenUsers[i].getID().toString();
				sql = "INSERT INTO "+GroupTable.table.getTableName()+
						" VALUES ( "+
						getNewKey(con).toString()+","+
						updatedGroupID+","+
						userRef+","+
						(true?"1":"0")+","+
						newHash +
						" )";
				updateCleanSQL(con,sql);
			}
		}
	}
	//
	String set = vTable.privacy.getQualifiedColName() + " = " + updatedGroupID;
	String cond = vTable.id.getQualifiedColName() + " = " + vKey;
				//" AND " + vTable.ownerRef.getQualifiedColName() + " = " + owner.getID();
	sql = DatabasePolicySQL.enforceOwnershipUpdate(owner,vTable,set,cond);
//System.out.println(sql);
	int numRowsProcessed = updateCleanSQL(con, sql);
	
	if (numRowsProcessed != 1){
		//
		// check if update failed, or just already updated
		//
		Vector versionInfoList = getVersionableInfos(con,newLog,owner,vType,false,vKey,true);
		if (versionInfoList.size()==0){
			throw new DataAccessException("Remove User "+userRemoveFromGroup+" Permission to access failed, "+vType.getTypeName()+"("+vKey+") record not found");
		}else{
			throw new DataAccessException("Remove User "+userRemoveFromGroup+" Permission to access failed "+vType.getTypeName()+"("+vKey+")");
		}
	}
	
	currentCache.remove(vKey);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param owner cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public static void groupSetPrivate(Connection con,SessionLog newLog,User owner, 
										VersionableType vType, KeyValue vKey,DBCacheTable currentCache) 
							throws SQLException,ObjectNotFoundException, DataAccessException/*, DependencyException*/ {


	if ((con == null)||(newLog == null)||(vType == null)||(currentCache == null)||(owner == null) || (vKey == null)) {
		throw new IllegalArgumentException("Improper parameters for groupAccessSetPrivate");
	}
	//
	Version currentVersion = permissionInit(con,vType,vKey,owner);

	newLog.print("DbDriver.groupAccessSetPrivate(owner=" + owner + ", type =" + vType.getTypeName() + ", key=" + vKey + ")");

	BigDecimal updatedGroupID = GroupAccess.GROUPACCESS_NONE;
	VersionTable vTable = VersionTable.getVersionTable(vType);
	String set = vTable.privacy.getQualifiedColName() + " = " + updatedGroupID;
	String cond = vTable.id.getQualifiedColName() + " = " + vKey;
	String sql = DatabasePolicySQL.enforceOwnershipUpdate(owner,vTable,set,cond);
//System.out.println(sql);
	int numRowsProcessed = updateCleanSQL(con, sql);
	if (numRowsProcessed != 1){
		//
		// check if update failed, or just already updated
		//
		Vector versionInfoList = getVersionableInfos(con,newLog,owner,vType,false,vKey,true);
		if (versionInfoList.size()==0){
			throw new DataAccessException("groupSetPrivate failed "+vType.getTypeName()+"("+vKey+") record not found");
		}else{
			throw new DataAccessException("groupSetPrivate failed "+vType.getTypeName()+"("+vKey+")");
		}
	}
	currentCache.remove(vKey);
	
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param owner cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public static void groupSetPublic(Connection con,SessionLog newLog,User owner, 
										VersionableType vType, KeyValue vKey,DBCacheTable currentCache) 
							throws SQLException,ObjectNotFoundException, DataAccessException {


	if ((con == null)||(newLog == null)||(vType == null)||(currentCache == null)||(owner == null) || (vKey == null)) {
		throw new IllegalArgumentException("Improper parameters for groupAccessSetPublic");
	}
	//
	Version currentVersion = permissionInit(con,vType,vKey,owner);

	newLog.print("DbDriver.groupSetPublic(owner=" + owner + ", type =" + vType + ", key=" + vKey + ")");

	BigDecimal updatedGroupID = GroupAccess.GROUPACCESS_ALL;
	VersionTable vTable = VersionTable.getVersionTable(vType);
	String set = vTable.privacy.getQualifiedColName() + " = " + updatedGroupID;
	String cond = vTable.id.getQualifiedColName() + " = " + vKey;
				//" AND " + vTable.ownerRef.getQualifiedColName() + " = " + owner.getID();
	String sql = DatabasePolicySQL.enforceOwnershipUpdate(owner,vTable,set,cond);
//System.out.println(sql);
	int numRowsProcessed = updateCleanSQL(con, sql);
	if (numRowsProcessed != 1){
		//
		// check if update failed, or just already updated
		//
		Vector versionInfoList = getVersionableInfos(con,newLog,owner,vType,false,vKey,true);
		if (versionInfoList.size()==0){
			throw new DataAccessException("groupSetPublic failed "+vType.getTypeName()+"("+vKey+") record not found");
		}else{
			throw new DataAccessException("groupSetPublic failed "+vType.getTypeName()+"("+vKey+")");
		}
	}
	currentCache.remove(vKey);
}

/**
 * Insert the method's description here.
 * Creation date: (5/4/2005 1:46:01 PM)
 * @param versionableKey cbit.sql.KeyValue
 */
private void insertSoftwareVersion(Connection con, KeyValue versionKey) throws SQLException {
	//
	//Insert Software Version
	//
	updateCleanSQL(con,
		"INSERT INTO "+SoftwareVersionTable.table.getTableName()+" "+
		SoftwareVersionTable.table.getSQLColumnList()+
		" VALUES "+SoftwareVersionTable.table.getSQLValueList(versionKey)
		);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public static void insertVersionableChildSummary(Connection con,String serialDBChildSummary,VersionableType vType,KeyValue vKey)
						throws SQLException,DataAccessException {
						
	Table csTable = null;
	Field csLargeCol = null;
	Field csSmallCol = null;
	
	if (vType.equals(VersionableType.BioModelMetaData)){
		csTable = BioModelTable.table;
		csLargeCol = BioModelTable.table.childSummaryLarge;
		csSmallCol = BioModelTable.table.childSummarySmall;
	}else if(vType.equals(VersionableType.MathModelMetaData)){
		csTable = MathModelTable.table;
		csLargeCol = MathModelTable.table.childSummaryLarge;
		csSmallCol = MathModelTable.table.childSummarySmall;
	}else{
		throw new IllegalArgumentException("vType " + vType + " not supported");
	}
	
	String sql =
		"UPDATE "+ csTable.getTableName() +
		" SET " + 
			(
			varchar2_CLOB_is_Varchar2_OK(serialDBChildSummary)
			?
			csLargeCol.toString() + " = null ," +
			csSmallCol.toString() + " = " + INSERT_VARCHAR2_HERE
			:
			csLargeCol.toString() + " = " + INSERT_CLOB_HERE + " , " +
			csSmallCol.toString() + " = null"			
			) +
		" WHERE " + csTable.id.getUnqualifiedColName()+" = "+vKey.toString();

	varchar2_CLOB_update(con,sql,serialDBChildSummary,csTable,vKey,csLargeCol,csSmallCol);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
protected Version insertVersionableInit(InsertHashtable hash, Connection con, User user, Versionable versionable, String name,String annot,boolean bVersion) 
throws SQLException,DataAccessException{

	if (hash.getDatabaseKey(versionable)!=null){
		throw new DataAccessException(versionable+" already inserted in this transaction");
	}

	VersionableType vType = VersionableType.fromVersionable(versionable);
	if (vType.getIsTopLevel() && isNameUsed(con,vType,user,name)){
		throw new DataAccessException("'"+user.getName()+"' already has a "+vType.getTypeName()+" with name '"+name+"'");
	}

	User owner = user;
	//AccessInfo accessInfo = new AccessInfo(AccessInfo.PRIVATE_CODE);
	GroupAccess accessInfo = new GroupAccessNone();
	KeyValue versionKey = getNewKey(con);
	java.util.Date date = getNewDate(con);
//	if(versionable.getVersion().getVersionKey() != null){
//		throw new DataAccessException("GeomDbDriver:insertVersionable, VersionKey must be null to insert");
//	}

	String versionName = name;
	
	//Check for Archive and Publish not needed in insert because versionflag is always forced to Current
	VersionFlag versionFlag = null;
	//if(bVersion){
		//versionFlag = VersionFlag.Archived;
	//}else{
		versionFlag = VersionFlag.Current;
	//}

	KeyValue PRefKey = null;
	java.math.BigDecimal branchID = getNewBranchID(con);

	//
	//Insert Software Version
	//
	insertSoftwareVersion(con,versionKey);

	//
	// this is overridden in SimulationDbDriver to help form a SimulationVersion object with or without the ParentSimulationReference.
	//
	return new Version(versionKey,versionName,owner,accessInfo,PRefKey,branchID,date,versionFlag,annot);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public static void insertVersionableXML(Connection con,String xml,VersionableType vType,KeyValue vKey) 
					throws DataAccessException, SQLException, RecordChangedException {
						
	String xmlTableName = null;
	String versionableRefColName = null;
	String xmlColName = null;
	if (vType.equals(VersionableType.BioModelMetaData)){
		xmlTableName = BioModelXMLTable.table.getTableName();
		versionableRefColName = BioModelXMLTable.table.bioModelRef.toString();
		xmlColName = BioModelXMLTable.table.bmXML.toString();
	}else if(vType.equals(VersionableType.MathModelMetaData)){
		xmlTableName = MathModelXMLTable.table.getTableName();
		versionableRefColName = MathModelXMLTable.table.mathModelRef.toString();
		xmlColName = MathModelXMLTable.table.mmXML.toString();
	}else{
		throw new IllegalArgumentException("vType " + vType + " not supported");
	}
		updateCleanSQL(con,"DELETE FROM "+xmlTableName +
			" WHERE " + versionableRefColName + " = " + vKey.toString());
		
		updateCleanSQL(con,"INSERT INTO "+xmlTableName+
			" VALUES (NEWSEQ.NEXTVAL,"+vKey.toString()+",EMPTY_CLOB(),SYSDATE)");
		
		updateCleanLOB(con,versionableRefColName,vKey,xmlTableName,xmlColName,xml);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param con Connection
 * @param user User
 * @param vType int
 * @param versionKey cbit.sql.KeyValue
 */
private static boolean isBranchPointOrBaseSimulation(java.sql.Connection con,VersionableType vType, KeyValue versionKey) 
							throws ObjectNotFoundException, java.sql.SQLException {
								
	VersionTable vTable = VersionTable.getVersionTable(vType);
	String sql = null;
	sql = 	"SELECT " + vTable.getTableName() + "." + vTable.id + 
			" FROM " + vTable.getTableName() + 
			" WHERE " + vTable.getTableName() + "." + VersionTable.versionBranchPointRef_ColumnName + " = " + versionKey;
	//
	// for simulations only, include references from "child" simulations
	//
	if (vType.equals(VersionableType.Simulation)){
		sql = sql + " OR " + vTable.getTableName() + "." + VersionTable.versionParentSimRef_ColumnName + " = " + versionKey;
	}
	java.sql.Statement stmt = con.createStatement();
	try {
		java.sql.ResultSet rset = stmt.executeQuery(sql);
		if (rset.next()) {
			return true;
		}
	} finally {
		stmt.close();
	}
	return false;
}


/**
 *
 * Test if there are any records of type 'vType' that use the name 'vName' owned by 'owner'
 *
 * @return boolean
 * @param vType cbit.sql.VersionableType
 * @param owner cbit.vcell.server.User
 * @param vName java.lang.String
 */
protected static boolean isNameUsed(Connection con, VersionableType vType, User owner, String vName) throws SQLException {

	VersionTable vTable = VersionTable.getVersionTable(vType);
	String sql;
	sql =   "SELECT " + vTable.id +
			" FROM " + vTable.getTableName() +
			" WHERE " + vTable.ownerRef + " = " + owner.getID() +
			" AND " + vTable.name + " = " + "'"+vName+"'";

	boolean bNameUsed = false;
	
//System.out.println(sql);
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			bNameUsed = true;
		}
	} finally {
		stmt.close();
	}

	return bNameUsed;
}


/**
 * Insert the method's description here.
 * Creation date: (5/23/2006 11:48:25 AM)
 */
private static Version permissionInit(Connection con,VersionableType vType,KeyValue vKey,User user) throws DataAccessException,SQLException{
	
	if (!vType.getIsTopLevel()) {
		throw new IllegalArgumentException("Versionable type "+vType.getTypeName()+" not top level, can't set permission");
	}

	
	Version version = getVersionFromKeyValue(con,vType,vKey);

	//Must be owner to manipulate group
	if (!version.getOwner().compareEqual(user)){
		throw new PermissionException("Cannot alter group "+vType.getTypeName()+" \""+version.getName()+"\" ("+vKey+"), not owned by "+user.getName());
	}

	//Cannot be PUBLISHED
	if(version.getFlag().compareEqual(VersionFlag.Published)){
		throw new DataAccessException("Cannot change permission of PUBLISHED documents");
	}
	
	return version;
	
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public static void replacePreferences(Connection con, User user, cbit.util.Preference[] preferences) throws SQLException {

	String sql =
		"DELETE FROM " +
			UserPreferenceTable.table.getTableName() +
		" WHERE " +
			UserPreferenceTable.table.userRef.getQualifiedColName() + " = " + user.getID().toString();
			
	updateCleanSQL(con,sql);
	
	PreparedStatement pstmt = null;
	try{
		sql =
			"INSERT INTO "+UserPreferenceTable.table.getTableName() +
			" VALUES (newseq.NEXTVAL,"+user.getID()+",?,?)";
			
		pstmt = con.prepareStatement(sql);

		for (int i = 0; i < preferences.length; i++){
			String key = preferences[i].getKey();
			String value = preferences[i].getValue();
			pstmt.setString(1,cbit.util.TokenMangler.getSQLEscapedString(key));
			pstmt.setString(2,cbit.util.TokenMangler.getSQLEscapedString(value));
			pstmt.executeUpdate();
		}
	}finally{
		if(pstmt != null){
			pstmt.close();
		}
	}
}


/**
 * This method was created in VisualAge.
 * @param keyFactory cbit.sql.KeyFactory
 */
public static void setKeyFactory(KeyFactory aKeyFactory) {
	keyFactory = aKeyFactory;
}


/**
 * This method was created in VisualAge.
 * @param vTable cbit.sql.VersionTable
 * @param versionKey cbit.sql.KeyValue
 */
protected void setVersioned(Connection con, User user,Versionable versionable) throws ObjectNotFoundException,SQLException,DataAccessException {
	String sql;
	if (versionable instanceof cbit.vcell.mapping.SimulationContext){
		cbit.vcell.mapping.SimulationContext sc = (cbit.vcell.mapping.SimulationContext)versionable;
		setVersioned(con,user,sc.getGeometryContext().getGeometry());
		setVersioned(con,user,sc.getGeometryContext().getModel());
		if (sc.getMathDescription()!=null){
			setVersioned(con,user,sc.getMathDescription());
		}
	}else if (versionable instanceof cbit.vcell.math.MathDescription){
		cbit.vcell.math.MathDescription math = (cbit.vcell.math.MathDescription)versionable;
		setVersioned(con,user,math.getGeometry());
	}else if (versionable instanceof cbit.vcell.geometry.Geometry){
		cbit.vcell.geometry.Geometry geo = (cbit.vcell.geometry.Geometry)versionable;
		if (geo.getGeometrySpec().getImage() != null){
			setVersioned(con,user,geo.getGeometrySpec().getImage());
		}
	}
	//
	// if current user is the owner, try to 'version'
	// else, must be already 'versioned'
	//
	if (versionable.getVersion().getOwner().compareEqual(user)){
		VersionTable vTable = VersionTable.getVersionTable(versionable);
		sql = 	"UPDATE " + vTable.getTableName() + 
				" SET " + vTable.versionFlag + " = " + VersionFlag.Archived.getIntValue() +
				" WHERE " + vTable.id + " = " + versionable.getVersion().getVersionKey() +
				//" AND " + vTable.versionFlag + " = " + VersionFlag.CURRENT +
				" AND " + vTable.ownerRef + " = " + user.getID();
				
		if (updateCleanSQL(con,sql)!=1){
			throw new DataAccessException("setVersioned failed for :"+versionable.getVersion());
		}
		dbc.remove(versionable.getVersion().getVersionKey());
	}
}


/**
 * This method was created in VisualAge.
 * @param rset java.sql.ResultSet
 */
public static void showMetaData(ResultSet rset) throws SQLException {
	ResultSetMetaData metaData = rset.getMetaData();
	for (int i = 1; i <= metaData.getColumnCount(); i++) {
		System.out.println("column(" + i + ") = " + metaData.getColumnName(i));
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 2:39:49 PM)
 * @return cbit.vcell.numericstest.TestSuiteInfoNew[]
 */
public static cbit.vcell.numericstest.TestSuiteNew testSuiteGet(BigDecimal getThisTS,Connection con,User user,SessionLog sessionLog)
			throws SQLException, DataAccessException {
	
	if(!user.isTestAccount()){
		throw new PermissionException("User="+user.getName()+" not allowed TestSuiteInfo");
	}

	//if(testSuiteHash.containsKey(getThisTS)){
		//return (cbit.vcell.numericstest.TestSuiteNew)testSuiteHash.get(getThisTS);
	//}
	Hashtable simulationInfoH = new Hashtable();
	Hashtable mathModelInfoH = new Hashtable();
	Hashtable bioModelInfoH = new Hashtable();
	
	Statement stmt = null;
	ResultSet rset = null;
	String sql = null;
	
	boolean origBSilent = DatabasePolicySQL.bSilent;
	try{
		DatabasePolicySQL.bSilent = true;
		
//double begTime=System.currentTimeMillis();
//int counter = 0;
		stmt = con.createStatement();
		// Get VariableComparisonSummaries
		Hashtable vcsH = new Hashtable();
		{
		sql =
			"SELECT "+TFTestResultTable.table.getTableName()+".*" +
			" FROM " +
			TFTestResultTable.table.getTableName()+","+
			TFTestCriteriaTable.table.getTableName()+","+
			TFTestCaseTable.table.getTableName()+","+
			TFTestSuiteTable.table.getTableName()+
			" WHERE "+
			TFTestSuiteTable.table.id.getQualifiedColName()+"="+getThisTS+
			" AND " +
			TFTestSuiteTable.table.id.getQualifiedColName()+"="+TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+
			" AND " +
			TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
			" AND " +
			TFTestCriteriaTable.table.id.getQualifiedColName()+"="+TFTestResultTable.table.testCriteriaRef.getQualifiedColName()+
			" ORDER BY UPPER("+TFTestResultTable.table.varName.getQualifiedColName()+")";

		rset = stmt.executeQuery(sql);
		while(rset.next()){
			BigDecimal tcritRef = rset.getBigDecimal(TFTestResultTable.table.testCriteriaRef.getUnqualifiedColName());
			String varName = rset.getString(TFTestResultTable.table.varName.getUnqualifiedColName());
			double absError = rset.getDouble(TFTestResultTable.table.absError.getUnqualifiedColName());
			double relError = rset.getDouble(TFTestResultTable.table.relError.getUnqualifiedColName());
			double maxRef = rset.getDouble(TFTestResultTable.table.maxRef.getUnqualifiedColName());
			double minRef = rset.getDouble(TFTestResultTable.table.minRef.getUnqualifiedColName());
			double mse = rset.getDouble(TFTestResultTable.table.meanSqrError.getUnqualifiedColName());
			double timeAbsError = rset.getDouble(TFTestResultTable.table.timeAbsError.getUnqualifiedColName());
			int indexAbsError = rset.getInt(TFTestResultTable.table.indexAbsError.getUnqualifiedColName());
			double timeRelError = rset.getDouble(TFTestResultTable.table.timeRelError.getUnqualifiedColName());
			int indexRelError = rset.getInt(TFTestResultTable.table.indexRelError.getUnqualifiedColName());
			Vector v = (Vector)vcsH.get(tcritRef);if(v == null){v = new Vector();vcsH.put(tcritRef,v);}
			v.add(new cbit.vcell.solver.test.VariableComparisonSummary(varName,minRef,maxRef,absError,relError,mse,timeAbsError,indexAbsError,timeRelError,indexRelError));
//counter+= 1;
		}
//System.out.println("VCS count="+counter+" time="+((System.currentTimeMillis()-begTime)/1000));
		rset.close();
		}
		
//begTime=System.currentTimeMillis();
//counter = 0;


		// Get TestCriteria for mathModel based TestCases
		Hashtable tcritH = new Hashtable();
		{
		sql =
			"SELECT "+
				TFTestCriteriaTable.table.getTableName()+".*"+","+
				MathModelSimulationLinkTable.table.getTableName()+".*"+
			" FROM " +
			TFTestCriteriaTable.table.getTableName()+","+
			TFTestCaseTable.table.getTableName()+","+
			TFTestSuiteTable.table.getTableName()+","+
			MathModelSimulationLinkTable.table.getTableName()+","+
			SimulationTable.table.getTableName()+
			" WHERE "+
			TFTestSuiteTable.table.id.getQualifiedColName()+"="+getThisTS+
			" AND " +
			TFTestSuiteTable.table.id.getQualifiedColName()+"="+TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+
			" AND " +
			TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
			" AND " +
			TFTestCaseTable.table.mathModelRef.getQualifiedColName()+" IS NOT NULL "+
			" AND " +
			TFTestCriteriaTable.table.simulationRef.getQualifiedColName()+"="+SimulationTable.table.id.getQualifiedColName()+
			" AND " +
			TFTestCriteriaTable.table.regressionMMSimRef.getQualifiedColName()+"="+MathModelSimulationLinkTable.table.id.getQualifiedColName()+"(+)"+
			" ORDER BY UPPER("+SimulationTable.table.name.getQualifiedColName()+")";
			
//System.out.println(sql);
		rset = stmt.executeQuery(sql);
		while(rset.next()){
			BigDecimal tcritKey = rset.getBigDecimal(TFTestCriteriaTable.table.id.getUnqualifiedColName());
			BigDecimal tcaseRef = rset.getBigDecimal(TFTestCriteriaTable.table.testCaseRef.getUnqualifiedColName());
			BigDecimal simRef = rset.getBigDecimal(TFTestCriteriaTable.table.simulationRef.getUnqualifiedColName());
			BigDecimal simRegrRef = rset.getBigDecimal(MathModelSimulationLinkTable.table.simRef.getUnqualifiedColName());
			if(rset.wasNull()){simRegrRef = null;}
			BigDecimal mathRegrRef = rset.getBigDecimal(MathModelSimulationLinkTable.table.mathModelRef.getUnqualifiedColName());
			if(rset.wasNull()){mathRegrRef = null;}
			Double maxRelError = null;
			double dtemp = rset.getDouble(TFTestCriteriaTable.table.maxRelError.getUnqualifiedColName());
			if(rset.wasNull()){maxRelError = null;}
			else{maxRelError = new Double(dtemp);}
			Double maxAbsError = null;
			dtemp = rset.getDouble(TFTestCriteriaTable.table.maxAbsError.getUnqualifiedColName());
			if(rset.wasNull()){maxAbsError = null;}
			else{maxAbsError = new Double(dtemp);}
			String reportStatus = rset.getString(TFTestCriteriaTable.table.reportStatus.getUnqualifiedColName());
			if(rset.wasNull()){
				reportStatus = cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT;
			}
			String reportStatusMessage = rset.getString(TFTestCriteriaTable.table.reportMessage.getUnqualifiedColName());
			if(rset.wasNull()){
				reportStatusMessage = null;
			}
			if(reportStatusMessage != null && reportStatusMessage.length() > 0){
				reportStatusMessage = cbit.util.TokenMangler.getSQLRestoredString(reportStatusMessage);
			}
			Vector v = (Vector)tcritH.get(tcaseRef);
			if(v == null){
				v = new Vector();
				tcritH.put(tcaseRef,v);
			}

			cbit.vcell.solver.SimulationInfo simInfo = (cbit.vcell.solver.SimulationInfo)simulationInfoH.get(simRef);
			if(simInfo == null){
				Vector simVector = getVersionableInfos(con,sessionLog,user,VersionableType.Simulation,false,new KeyValue(simRef),false);
				if (simVector != null && simVector.size() > 0) {
					simInfo = (cbit.vcell.solver.SimulationInfo)simVector.firstElement();
					simulationInfoH.put(simRef,simInfo);
				}
			}

			cbit.vcell.solver.SimulationInfo regrSimInfo = null;
			cbit.vcell.mathmodel.MathModelInfo regrMathModelInfo = null;
			if(simRegrRef != null){
				regrSimInfo = (cbit.vcell.solver.SimulationInfo)simulationInfoH.get(simRegrRef);
				if(regrSimInfo == null){
					Vector regSimVector = getVersionableInfos(con,sessionLog,user,VersionableType.Simulation,false,new KeyValue(simRegrRef),false);
					if (regSimVector != null && regSimVector.size() > 0) {
						regrSimInfo = (cbit.vcell.solver.SimulationInfo)regSimVector.firstElement();
						simulationInfoH.put(simRegrRef,regrSimInfo);
					}
				}
				regrMathModelInfo = (cbit.vcell.mathmodel.MathModelInfo)mathModelInfoH.get(mathRegrRef);
				if(regrMathModelInfo == null){
					Vector regMathVector = getVersionableInfos(con,sessionLog,user,VersionableType.MathModelMetaData,false,new KeyValue(mathRegrRef),false);
					if (regMathVector != null && regMathVector.size() > 0) {
						regrMathModelInfo = (cbit.vcell.mathmodel.MathModelInfo)regMathVector.firstElement();
						mathModelInfoH.put(mathRegrRef,regrMathModelInfo);
					}
				}
			}
			
			//
			cbit.vcell.solver.test.VariableComparisonSummary[] neededVCSArr = null;
			Vector vcsV = (Vector)vcsH.get(tcritKey);
			if(vcsV != null){
				neededVCSArr = new cbit.vcell.solver.test.VariableComparisonSummary[vcsV.size()];
				vcsV.copyInto(neededVCSArr);
			}
			//
			cbit.vcell.numericstest.TestCriteriaNew tcn = null;
			if (simInfo != null) {
				tcn = new cbit.vcell.numericstest.TestCriteriaNewMathModel(
					tcritKey,simInfo,regrMathModelInfo,regrSimInfo,maxRelError,maxAbsError,neededVCSArr,reportStatus,reportStatusMessage);
				v.add(tcn);
			}
//counter+= 1;
		}
//System.out.println("TCrit count="+counter+" time="+((System.currentTimeMillis()-begTime)/1000));
		rset.close();
		}

		// Get TestCriteria for bioModel based TestCases
		//Hashtable tcritH = new Hashtable();
		{
		final String REGRSIMREF = "REGRSIMREF";
		final String SCNAME = "SCNAME";
		sql =
			"SELECT "+
				TFTestCriteriaTable.table.getTableName()+".*"+","+
				BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+","+
				BioModelSimulationLinkTable.table.simRef.getQualifiedColName()+" "+REGRSIMREF+","+
				SimContextTable.table.name.getQualifiedColName()+" "+SCNAME+
			" FROM " +
			TFTestCriteriaTable.table.getTableName()+","+
			TFTestCaseTable.table.getTableName()+","+
			TFTestSuiteTable.table.getTableName()+","+
			BioModelSimContextLinkTable.table.getTableName()+","+
			SimContextTable.table.getTableName()+","+
			BioModelSimulationLinkTable.table.getTableName()+
			//SimulationTable.table.getTableName()+
			" WHERE "+
			TFTestSuiteTable.table.id.getQualifiedColName()+"="+getThisTS+
			" AND " +
			TFTestSuiteTable.table.id.getQualifiedColName()+"="+TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+
			" AND " +
			TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
			" AND " +
			TFTestCaseTable.table.bmAppRef.getQualifiedColName()+" IS NOT NULL "+
			" AND " +
			TFTestCriteriaTable.table.regressionBMAPPRef.getQualifiedColName()+"="+BioModelSimContextLinkTable.table.id.getQualifiedColName()+"(+)"+
			" AND " +
			BioModelSimContextLinkTable.table.simContextRef+" = "+SimContextTable.table.id.getQualifiedColName()+"(+)"+
			//" AND " +
			//SimContextTable.table.mathRef.getQualifiedColName()+" = "+SimulationTable.table.mathRef.getQualifiedColName()+"(+)" +
			" AND " +
			TFTestCriteriaTable.table.regressionBMSimRef.getQualifiedColName()+" = "+BioModelSimulationLinkTable.table.id.getQualifiedColName()+"(+)";
			
//System.out.println(sql);
		rset = stmt.executeQuery(sql);
		while(rset.next()){
			BigDecimal tcritKey = rset.getBigDecimal(TFTestCriteriaTable.table.id.getUnqualifiedColName());
			BigDecimal tcaseRef = rset.getBigDecimal(TFTestCriteriaTable.table.testCaseRef.getUnqualifiedColName());
			BigDecimal tcSimRef = rset.getBigDecimal(TFTestCriteriaTable.table.simulationRef.getUnqualifiedColName());
			
			BigDecimal regrSimRef = rset.getBigDecimal(REGRSIMREF);
			//BigDecimal regrSimRef = rset.getBigDecimal(SimulationTable.table.id.getUnqualifiedColName());
			if(rset.wasNull()){regrSimRef = null;}
			BigDecimal regrBioModelRef = rset.getBigDecimal(BioModelSimContextLinkTable.table.bioModelRef.getUnqualifiedColName());
			if(rset.wasNull()){regrBioModelRef = null;}
			String regrSCName = rset.getString(SCNAME);
			if(rset.wasNull()){
				regrSCName = null;
			}
			
			Double maxRelError = null;
			double dtemp = rset.getDouble(TFTestCriteriaTable.table.maxRelError.getUnqualifiedColName());
			if(rset.wasNull()){maxRelError = null;}
			else{maxRelError = new Double(dtemp);}
			Double maxAbsError = null;
			dtemp = rset.getDouble(TFTestCriteriaTable.table.maxAbsError.getUnqualifiedColName());
			if(rset.wasNull()){maxAbsError = null;}
			else{maxAbsError = new Double(dtemp);}
			String reportStatus = rset.getString(TFTestCriteriaTable.table.reportStatus.getUnqualifiedColName());
			if(rset.wasNull()){
				reportStatus = cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT;
			}
			String reportStatusMessage = rset.getString(TFTestCriteriaTable.table.reportMessage.getUnqualifiedColName());
			if(rset.wasNull()){
				reportStatusMessage = null;
			}
			if(reportStatusMessage != null && reportStatusMessage.length() > 0){
				reportStatusMessage = cbit.util.TokenMangler.getSQLRestoredString(reportStatusMessage);
			}
			Vector v = (Vector)tcritH.get(tcaseRef);
			if(v == null){
				v = new Vector();
				tcritH.put(tcaseRef,v);
			}

			cbit.vcell.solver.SimulationInfo simInfo = (cbit.vcell.solver.SimulationInfo)simulationInfoH.get(tcSimRef);
			if(simInfo == null){
				Vector simVector = getVersionableInfos(con,sessionLog,user,VersionableType.Simulation,false,new KeyValue(tcSimRef),false);
				if (simVector != null && simVector.size() == 1) {
					simInfo = (cbit.vcell.solver.SimulationInfo)simVector.firstElement();
					simulationInfoH.put(tcSimRef,simInfo);
				}else{
					throw new DataAccessException("Found more than 1 versionable for tcsimRef="+tcSimRef);
				}
			}

			cbit.vcell.solver.SimulationInfo regrSimInfo = null;
			cbit.vcell.biomodel.BioModelInfo regrBioModelInfo = null;
			if(regrSimRef != null){
				regrSimInfo = (cbit.vcell.solver.SimulationInfo)simulationInfoH.get(regrSimRef);
				if(regrSimInfo == null){
					Vector regSimVector = getVersionableInfos(con,sessionLog,user,VersionableType.Simulation,false,new KeyValue(regrSimRef),false);
					if (regSimVector != null && regSimVector.size() == 1) {
						regrSimInfo = (cbit.vcell.solver.SimulationInfo)regSimVector.firstElement();
						simulationInfoH.put(regrSimRef,regrSimInfo);
					}else{
						throw new DataAccessException("Found more than 1 versionable for simregRef="+regrSimRef);
					}
				}
				regrBioModelInfo = (cbit.vcell.biomodel.BioModelInfo)mathModelInfoH.get(regrBioModelRef);
				if(regrBioModelInfo == null){
					Vector regBioModelVector = getVersionableInfos(con,sessionLog,user,VersionableType.BioModelMetaData,false,new KeyValue(regrBioModelRef),false);
					if (regBioModelVector != null && regBioModelVector.size() == 1) {
						regrBioModelInfo = (cbit.vcell.biomodel.BioModelInfo)regBioModelVector.firstElement();
						mathModelInfoH.put(regrBioModelRef,regrBioModelInfo);
					}else{
						throw new DataAccessException("Found more than 1 versionable for reegrbiomodelRef="+regrBioModelRef);
					}
				}
			}
			
			//
			cbit.vcell.solver.test.VariableComparisonSummary[] neededVCSArr = null;
			Vector vcsV = (Vector)vcsH.get(tcritKey);
			if(vcsV != null){
				neededVCSArr = new cbit.vcell.solver.test.VariableComparisonSummary[vcsV.size()];
				vcsV.copyInto(neededVCSArr);
			}
			//
			cbit.vcell.numericstest.TestCriteriaNew tcn = null;
			if (simInfo != null) {
				tcn = new cbit.vcell.numericstest.TestCriteriaNewBioModel(
					tcritKey,simInfo,regrBioModelInfo,regrSCName,regrSimInfo,maxRelError,maxAbsError,neededVCSArr,reportStatus,reportStatusMessage);
				v.add(tcn);
			}
//counter+= 1;
		}
//System.out.println("TCrit count="+counter+" time="+((System.currentTimeMillis()-begTime)/1000));
		rset.close();
		}
		
		// Get TestCases
		Vector tcV = new Vector();
//begTime=System.currentTimeMillis();
//counter = 0;
		{
		//final String BMSCL = "bmscl";
		final String OBTCTYPECOLUMN = "OBTCTYPECOLUMN";
		final String OBNAMECOLUMN = "OBNAMECOLUMN";
		final String BMSCBMRNAME = "BMSCBMRNAME";
		final String BMSCSCRNAME = "BMSCSCRNAME";
		final String SCNAME = "SCNAME";
		final String SORTHELP1 = "SORTHELP1";
		final String SORTHELP2 = "SORTHELP2";
		sql =
			"SELECT "+
				"UPPER("+MathModelTable.table.name.getQualifiedColName()+") "+SORTHELP1+","+"TO_CHAR(NULL) "+SORTHELP2+","+
				TFTestCaseTable.table.getTableName()+".*" +","+
				"TO_NUMBER(NULL) "+BMSCBMRNAME+","+
				"TO_NUMBER(NULL) "+BMSCSCRNAME+","+
				"TO_CHAR(NULL) "+SCNAME+","+
				"'MM' "+OBTCTYPECOLUMN+","+MathModelTable.table.name.getQualifiedColName()+" "+OBNAMECOLUMN+
			" FROM " +
				TFTestCaseTable.table.getTableName()+","+
				TFTestSuiteTable.table.getTableName()+","+
				MathModelTable.table.getTableName()+
			" WHERE "+
				TFTestSuiteTable.table.id.getQualifiedColName()+"="+getThisTS+
				" AND " +
				TFTestSuiteTable.table.id.getQualifiedColName()+"="+TFTestCaseTable.table.testSuiteRef.getQualifiedColName() +
				" AND " +
				TFTestCaseTable.table.mathModelRef.getQualifiedColName()+" IS NOT NULL"+
				" AND "+
				TFTestCaseTable.table.mathModelRef.getQualifiedColName()+" = "+ MathModelTable.table.id.getQualifiedColName()+
			" UNION "+
			"SELECT "+
				"UPPER("+BioModelTable.table.name.getQualifiedColName()+") "+SORTHELP1+",UPPER("+SimContextTable.table.name.getQualifiedColName()+") "+SORTHELP2+","+
				TFTestCaseTable.table.getTableName()+".*" +","+
				BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+" "+BMSCBMRNAME+","+
				BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+" "+BMSCSCRNAME+","+
				SimContextTable.table.name.getQualifiedColName()+" "+SCNAME+","+
				"'BM' "+OBTCTYPECOLUMN+","+
				BioModelTable.table.name.getQualifiedColName()+" "+OBNAMECOLUMN+
			" FROM " +
				BioModelSimContextLinkTable.table.getTableName()+","+
				SimContextTable.table.getTableName()+","+
				TFTestCaseTable.table.getTableName()+","+
				TFTestSuiteTable.table.getTableName()+","+
				BioModelTable.table.getTableName()+
			" WHERE "+
				TFTestSuiteTable.table.id.getQualifiedColName()+"="+getThisTS+
				" AND " +
				TFTestSuiteTable.table.id.getQualifiedColName()+"="+TFTestCaseTable.table.testSuiteRef.getQualifiedColName() +
				" AND " +
				TFTestCaseTable.table.bmAppRef.getQualifiedColName()+" IS NOT NULL"+
				" AND "+
				TFTestCaseTable.table.bmAppRef.getQualifiedColName()+" = "+BioModelSimContextLinkTable.table.id.getQualifiedColName()+
				" AND " +
				BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+" = "+SimContextTable.table.id.getQualifiedColName()+
				" AND "+
				BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+"="+BioModelTable.table.id.getQualifiedColName()+
				//" ORDER BY "+OBTCTYPECOLUMN+","+"UPPER("+OBNAMECOLUMN+")"+","+"UPPER("+SCNAME+")";
				//" ORDER BY 11,"+"UPPER(12)"+","+"UPPER(10)";
				" ORDER BY 13,1,2";

		rset = stmt.executeQuery(sql);
		while(rset.next()){
			BigDecimal tcaseKey = rset.getBigDecimal(TFTestCaseTable.table.id.getUnqualifiedColName());
			BigDecimal mmRef = rset.getBigDecimal(TFTestCaseTable.table.mathModelRef.getUnqualifiedColName());
			if(rset.wasNull()){
				mmRef = null;
			}
			BigDecimal simContextRef = rset.getBigDecimal(BMSCSCRNAME);
			if(rset.wasNull()){
				simContextRef = null;
			}
			BigDecimal bioModelRef = rset.getBigDecimal(BMSCBMRNAME);
			if(rset.wasNull()){
				bioModelRef = null;
			}
			String simContextName = rset.getString(SCNAME);
			if(rset.wasNull()){
				simContextName = null;
			}
			String tcType = rset.getString(TFTestCaseTable.table.tcSolutionType.getUnqualifiedColName());
			String tcAnnot = rset.getString(TFTestCaseTable.table.tcAnnotation.getUnqualifiedColName());
			if(rset.wasNull()){
				tcAnnot = "";
			}else{
				tcAnnot = cbit.util.TokenMangler.getSQLRestoredString(tcAnnot);
			}
			java.util.Date tcDate = VersionTable.getDate(rset,TFTestCaseTable.table.creationDate.getUnqualifiedColName());

			cbit.vcell.mathmodel.MathModelInfo mmInfo = null;
			cbit.vcell.biomodel.BioModelInfo bmInfo = null;
			if(mmRef != null){
				mmInfo = (cbit.vcell.mathmodel.MathModelInfo)mathModelInfoH.get(mmRef);		
				if(mmInfo == null){
					Vector mathVector = getVersionableInfos(con,sessionLog,user,VersionableType.MathModelMetaData,false,new KeyValue(mmRef),false);
					if (mathVector != null && mathVector.size() > 0) {
						mmInfo = (cbit.vcell.mathmodel.MathModelInfo)mathVector.firstElement();
						mathModelInfoH.put(mmRef,mmInfo);
					}
				}
			}else if(bioModelRef != null){
				bmInfo = (cbit.vcell.biomodel.BioModelInfo)bioModelInfoH.get(bioModelRef);		
				if(bmInfo == null){
					Vector bmAppVector = getVersionableInfos(con,sessionLog,user,VersionableType.BioModelMetaData,false,new KeyValue(bioModelRef),false);
					if (bmAppVector != null && bmAppVector.size() > 0) {
						bmInfo = (cbit.vcell.biomodel.BioModelInfo)bmAppVector.firstElement();
						bioModelInfoH.put(bioModelRef,bmInfo);
					}
				}
			}else{
				throw new RuntimeException("Test case in DB does not have MathmodelRef or BioModelAppRef");
			}
			cbit.vcell.numericstest.TestCriteriaNew[] neededTcritArr = null;
			Vector needTcritV = (Vector)tcritH.get(tcaseKey);
			if(needTcritV != null){
				neededTcritArr = new cbit.vcell.numericstest.TestCriteriaNew[needTcritV.size()];
				needTcritV.copyInto(neededTcritArr);
			}
			cbit.vcell.numericstest.TestCaseNew tcn = null;
			if (mmInfo != null) {
				tcn = new cbit.vcell.numericstest.TestCaseNewMathModel(tcaseKey,mmInfo,tcType,tcAnnot,neededTcritArr);
				tcV.add(tcn);
			}else if(bmInfo != null){
				tcn = new cbit.vcell.numericstest.TestCaseNewBioModel(tcaseKey,bmInfo,simContextName,new KeyValue(simContextRef),tcType,tcAnnot,neededTcritArr);
				tcV.add(tcn);
			}else{
				//throw new RuntimeException("Expected testCase MathModelInfo or BioModelInfo to be not null");
			}
			
//counter+= 1;
		}
//System.out.println("TCase count="+counter+" time="+((System.currentTimeMillis()-begTime)/1000));
		rset.close();
		
		}
		
		// Get TestSuite
		{
			cbit.vcell.numericstest.TestCaseNew[] tcnArr = null;
		if(tcV.size() > 0){
			tcnArr = new cbit.vcell.numericstest.TestCaseNew[tcV.size()];
			tcV.copyInto(tcnArr);
		}

		sql =
			"SELECT "+TFTestSuiteTable.table.getTableName()+".*" +
			" FROM " +
			TFTestSuiteTable.table.getTableName()+
			" WHERE "+
			TFTestSuiteTable.table.id.getQualifiedColName()+"="+getThisTS+
			" ORDER BY UPPER("+TFTestSuiteTable.table.tsVersion.getQualifiedColName()+")";

		rset = stmt.executeQuery(sql);
		BigDecimal tsKey = null;
		String tsVersion = null;
		String tsVCBuild = null;
		String tsNumericsBuild = null;
		java.util.Date tsDate = null;
		String tsAnnot = null;
		if(rset.next()){
			tsKey = rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName());
			tsVersion = rset.getString(TFTestSuiteTable.table.tsVersion.getUnqualifiedColName());
			tsVCBuild = rset.getString(TFTestSuiteTable.table.vcBuildVersion.getUnqualifiedColName());
			tsNumericsBuild = rset.getString(TFTestSuiteTable.table.vcNumericsVersion.getUnqualifiedColName());
			tsDate = VersionTable.getDate(rset,TFTestSuiteTable.table.creationDate.getUnqualifiedColName());
			tsAnnot = rset.getString(TFTestSuiteTable.table.tsAnnotation.getUnqualifiedColName());
		}else{
			throw new ObjectNotFoundException("TestSuite with key="+getThisTS+" not found");
		}
		rset.close();

		cbit.vcell.numericstest.TestSuiteInfoNew tsiNew =
			new cbit.vcell.numericstest.TestSuiteInfoNew(tsKey,tsVersion,tsVCBuild,tsNumericsBuild,tsDate,tsAnnot);

		cbit.vcell.numericstest.TestSuiteNew tsn = new cbit.vcell.numericstest.TestSuiteNew(tsiNew,tcnArr);
		//testSuiteHash.put(tsKey,tsn);
		return tsn;
		
		}

	}catch(SQLException e){
		System.out.println(sql);
		throw e;
	}finally{
		DatabasePolicySQL.bSilent = origBSilent;
		if(stmt != null){
			stmt.close();
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/16/2004 2:39:49 PM)
 * @return cbit.vcell.numericstest.TestSuiteInfoNew[]
 */
public static cbit.vcell.numericstest.TestSuiteInfoNew[] testSuiteInfosGet(Connection con,User user,SessionLog sessionLog) throws SQLException{
	
	if(!user.isTestAccount()){
		throw new PermissionException("User="+user.getName()+" not allowed TestSuiteInfo");
	}

	String sql =
		"SELECT * FROM " + TFTestSuiteTable.table.getTableName();

	Vector tsiV = new Vector();
	Statement stmt = null;
	try{
		stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(sql);
		while(rset.next()){
			BigDecimal tsKey = rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName());
			String tsID = rset.getString(TFTestSuiteTable.table.tsVersion.getUnqualifiedColName());
			String vcBuildS = rset.getString(TFTestSuiteTable.table.vcBuildVersion.getUnqualifiedColName());
			String vcNumericS = rset.getString(TFTestSuiteTable.table.vcNumericsVersion.getUnqualifiedColName());
			java.util.Date date = VersionTable.getDate(rset,TFTestSuiteTable.table.creationDate.getUnqualifiedColName());
			String tsAnnot = rset.getString(TFTestSuiteTable.table.tsAnnotation.getUnqualifiedColName());
			tsiV.add(new cbit.vcell.numericstest.TestSuiteInfoNew(tsKey,tsID,vcBuildS,vcNumericS,date,tsAnnot));
		}
	}finally{
		if(stmt != null){
			stmt.close();
		}
	}

	if(tsiV.size() > 0){
		cbit.vcell.numericstest.TestSuiteInfoNew[] temp = new cbit.vcell.numericstest.TestSuiteInfoNew[tsiV.size()];
		tsiV.copyInto(temp);
		return temp;
	}
	return null;

}


/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:55:36 AM)
 * @return cbit.vcell.numericstest.TestSuiteNew
 * @param tsop cbit.vcell.numericstest.TestSuiteOP
 */
public static cbit.vcell.numericstest.TestSuiteOPResults testSuiteOP(cbit.vcell.numericstest.TestSuiteOP tsop,Connection con,User user,SessionLog sessionLog) 
			throws SQLException,DataAccessException{

	java.util.TreeSet<BigDecimal> changedTestSuiteKeys = new java.util.TreeSet<BigDecimal>();
	
	String sql = null;
	Statement stmt = null;
	
	try{
		stmt = con.createStatement();
		
		if(tsop instanceof cbit.vcell.numericstest.AddTestSuiteOP){
			cbit.vcell.numericstest.AddTestSuiteOP addts_tsop = (cbit.vcell.numericstest.AddTestSuiteOP)tsop;
			String annotation = addts_tsop.getTestSuiteAnnotation();
			if(annotation != null){
				if(annotation.length() == 0){
					annotation = null;
				}else{
					annotation = cbit.util.TokenMangler.getSQLEscapedString(annotation);
				}
			}

			BigDecimal changedTSKey = keyFactory.getUniqueBigDecimal(con);
			sql = 
				"INSERT INTO "+TFTestSuiteTable.table.getTableName()+" VALUES("+
					changedTSKey+",'"+addts_tsop.getTestSuiteVersionID()+"',"+
					"'"+addts_tsop.getVCellBuildVersionID()+"'"+","+"'"+addts_tsop.getNumericsBuildVersionID()+"'"+","+
					"SYSDATE,SYSDATE,"+(annotation == null?"NULL":"'"+annotation+"'")+")";
			stmt.executeUpdate(sql);
			if(addts_tsop.getAddTestCasesOPs() != null){
				for(int i=0;i<addts_tsop.getAddTestCasesOPs().length;i+= 1){
					//Set new TSKey and do child OPs
					cbit.vcell.numericstest.AddTestCasesOP atcOP = addts_tsop.getAddTestCasesOPs()[i];
					if(atcOP instanceof cbit.vcell.numericstest.AddTestCasesOPMathModel){
						testSuiteOP(
							new cbit.vcell.numericstest.AddTestCasesOPMathModel(
								changedTSKey,
								((cbit.vcell.numericstest.AddTestCasesOPMathModel)atcOP).getMathModelKey(),
								atcOP.getTestCaseType(),atcOP.getAnnotation(),
								((cbit.vcell.numericstest.AddTestCasesOPMathModel)atcOP).getAddTestCriteriaOPsMathModel()),
							con,user,sessionLog);
					}else if(atcOP instanceof cbit.vcell.numericstest.AddTestCasesOPBioModel){
						testSuiteOP(
							new cbit.vcell.numericstest.AddTestCasesOPBioModel(
								changedTSKey,
								((cbit.vcell.numericstest.AddTestCasesOPBioModel)atcOP).getBioModelKey(),
								((cbit.vcell.numericstest.AddTestCasesOPBioModel)atcOP).getSimContextKey(),
								atcOP.getTestCaseType(),atcOP.getAnnotation(),
								((cbit.vcell.numericstest.AddTestCasesOPBioModel)atcOP).getAddTestCriteriaOPsBioModel()),
							con,user,sessionLog);
					}
				}
			}
			changedTestSuiteKeys.add(changedTSKey);
		}else if(tsop instanceof cbit.vcell.numericstest.AddTestCasesOPBioModel){
			cbit.vcell.numericstest.AddTestCasesOPBioModel addtc_tsop = (cbit.vcell.numericstest.AddTestCasesOPBioModel)tsop;
			//
			BigDecimal bmSimContextLinkRef = null;
			//Convert BioModelKey and SimContextKey to bmsimcontext key
			sql =
			"SELECT "+BioModelSimContextLinkTable.table.id.getQualifiedColName()+
			" FROM "+BioModelSimContextLinkTable.table.getTableName()+
			" WHERE "+
				BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+"="+addtc_tsop.getBioModelKey().toString() +
				" AND " +
				BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+"="+addtc_tsop.getSimContextKey().toString();
			ResultSet rset = stmt.executeQuery(sql);
			if(rset.next()){
				bmSimContextLinkRef = rset.getBigDecimal(BioModelSimContextLinkTable.table.id.getUnqualifiedColName());
				if(rset.next()){
					throw new DataAccessException("Too many MathModelSimLink found for bmKey="+addtc_tsop.getBioModelKey()+
					" simContextKey="+addtc_tsop.getSimContextKey());
				}
			}else{
				throw new DataAccessException(
					"No BioModelSimcontextLink found for bmKey="+addtc_tsop.getBioModelKey()+
					" simContextKey="+addtc_tsop.getSimContextKey());
			}
			rset.close();
			//
			BigDecimal tcKey = keyFactory.getUniqueBigDecimal(con);
			String annotation = addtc_tsop.getAnnotation();
			if(annotation != null){
				annotation = cbit.util.TokenMangler.getSQLEscapedString(annotation);
			}
			stmt.executeUpdate(
				"INSERT INTO "+TFTestCaseTable.table.getTableName()+" VALUES("+
					tcKey.toString()+","+addtc_tsop.getTestSuiteKey().toString()+",NULL,"+
					"'"+addtc_tsop.getTestCaseType()+"'"+","+"'"+annotation+"'"+","+"SYSDATE"+","+bmSimContextLinkRef.toString()+")");
			if(addtc_tsop.getAddTestCriteriaOPsBioModel() != null){
				for(int i=0;i<addtc_tsop.getAddTestCriteriaOPsBioModel().length;i+= 1){
					//Set new TSKey,TCaseKey and do child OPs
					cbit.vcell.numericstest.AddTestCriteriaOPBioModel atcritOP = addtc_tsop.getAddTestCriteriaOPsBioModel()[i];
					testSuiteOP(
						new cbit.vcell.numericstest.AddTestCriteriaOPBioModel(
							tcKey,atcritOP.getBioModelSimKey(),
							atcritOP.getRegressionBioModelKey(),atcritOP.getRegressionBioModelSimKey(),
							atcritOP.getMaxAbsoluteError(),atcritOP.getMaxRelativeError(),atcritOP.getAddTestResultsOP()),
						con,user,sessionLog);
				}
			}
			changedTestSuiteKeys.add(addtc_tsop.getTestSuiteKey());

			
		}else if(tsop instanceof cbit.vcell.numericstest.AddTestCasesOPMathModel){
			cbit.vcell.numericstest.AddTestCasesOPMathModel addtc_tsop = (cbit.vcell.numericstest.AddTestCasesOPMathModel)tsop;
			BigDecimal tcKey = keyFactory.getUniqueBigDecimal(con);
			KeyValue mmKey = addtc_tsop.getMathModelKey();
			String annotation = addtc_tsop.getAnnotation();
			if(annotation != null){
				annotation = cbit.util.TokenMangler.getSQLEscapedString(annotation);
			}
			stmt.executeUpdate(
				"INSERT INTO "+TFTestCaseTable.table.getTableName()+" VALUES("+
					tcKey.toString()+","+addtc_tsop.getTestSuiteKey().toString()+","+mmKey.toString()+","+
					"'"+addtc_tsop.getTestCaseType()+"'"+","+"'"+annotation+"'"+","+"SYSDATE"+",NULL)");
			if(addtc_tsop.getAddTestCriteriaOPsMathModel() != null){
				for(int i=0;i<addtc_tsop.getAddTestCriteriaOPsMathModel().length;i+= 1){
					//Set new TSKey,TCaseKey and do child OPs
					cbit.vcell.numericstest.AddTestCriteriaOPMathModel atcritOP = addtc_tsop.getAddTestCriteriaOPsMathModel()[i];
					testSuiteOP(
						new cbit.vcell.numericstest.AddTestCriteriaOPMathModel(
							tcKey,atcritOP.getMathModelSimKey(),
							atcritOP.getRegressionMathModelKey(),
							atcritOP.getRegressionMathModelSimKey(),
							atcritOP.getMaxAbsoluteError(),atcritOP.getMaxRelativeError(),atcritOP.getAddTestResultsOP()),
						con,user,sessionLog);
				}
			}
			changedTestSuiteKeys.add(addtc_tsop.getTestSuiteKey());			
		}else if(tsop instanceof cbit.vcell.numericstest.RemoveTestCasesOP){
			cbit.vcell.numericstest.RemoveTestCasesOP removetc_tsop = (cbit.vcell.numericstest.RemoveTestCasesOP)tsop;
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<removetc_tsop.getTestCasesKeys().length;i+= 1){
				if(i != 0){sb.append(",");}sb.append(removetc_tsop.getTestCasesKeys()[i].toString());
			}
			ResultSet rset = stmt.executeQuery(
				"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
				" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+
				" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+" IN ("+sb.toString()+")"+
				" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
			while(rset.next()){
				changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
			}
			rset.close();
			
			int numRowsUpdated = stmt.executeUpdate(
				"DELETE FROM "+TFTestCaseTable.table.getTableName()+
				" WHERE "+
				TFTestCaseTable.table.id.getUnqualifiedColName()+" IN ("+sb.toString()+")"
				);
			if(numRowsUpdated != removetc_tsop.getTestCasesKeys().length){
				throw new DataAccessException("Remove TestCase keys="+sb.toString()+
					" from TSKey="+removetc_tsop.getTestSuiteKey()+
					" removed row count="+numRowsUpdated+" expected "+removetc_tsop.getTestCasesKeys().length);
			}
			
		}else if(tsop instanceof cbit.vcell.numericstest.AddTestCriteriaOPMathModel){
			cbit.vcell.numericstest.AddTestCriteriaOPMathModel addtcrit_tsop = (cbit.vcell.numericstest.AddTestCriteriaOPMathModel)tsop;
			BigDecimal tcritKey = keyFactory.getUniqueBigDecimal(con);
			BigDecimal tcKey = addtcrit_tsop.getTestCaseKey();
			KeyValue simKey = addtcrit_tsop.getMathModelSimKey();
			//Check that there is a single mathmodel associated with simkey and parentsimref is null
			sql = "SELECT "+MathModelSimulationLinkTable.table.id.getQualifiedColName()+
			" FROM "+MathModelSimulationLinkTable.table.getTableName()+","+SimulationTable.table.getTableName()+
			" WHERE "+
				MathModelSimulationLinkTable.table.simRef.getQualifiedColName()+"="+simKey.toString()+
				" AND " +
				MathModelSimulationLinkTable.table.simRef.getQualifiedColName()+"="+SimulationTable.table.id.getQualifiedColName()+
				" AND " +
				SimulationTable.table.getTableName()+"."+SimulationTable.table.versionParentSimRef_ColumnName+" IS NULL";
			ResultSet rset = stmt.executeQuery(sql);
			if(rset.next()){
				if(rset.next()){
					throw new DataAccessException("Too many MathModelSimulationLink found for simKey="+simKey);
				}
			}else{
				throw new DataAccessException("No MathModelSimulationLink found for simKey="+simKey);
			}
			rset.close();
			////
			////Make sure TestCase type and regressionRef match
			//sql = "SELECT "+TFTestCaseTable.table.id.getQualifiedColName()+
				//" FROM "+TFTestCaseTable.table.getTableName()+
				//" WHERE "+
					//TFTestCaseTable.table.id.getQualifiedColName()+"="+tcKey.toString()+
					//" AND "+
					//TFTestCaseTable.table.tcSolutionType.getQualifiedColName()+
					//(regrSimKey != null?"=":"!=")+
					//"'"+cbit.vcell.numericstest.TestCaseNew.REGRESSION+"'";
			//rset = stmt.executeQuery(sql);
			//if(!rset.next()){
				//throw new DataAccessException("TestCase type not compatible with regressionSimKey");
			//}
			//rset.close();
			//
			//
			//Get simDataRef from simKey
			BigDecimal simDataRef = null;
			sql =
				"SELECT "+ResultSetMetaDataTable.table.id.getQualifiedColName()+
				" FROM " +ResultSetMetaDataTable.table.getTableName()+
				" WHERE "+
					ResultSetMetaDataTable.table.simRef.getQualifiedColName()+"="+simKey.toString();
			rset = stmt.executeQuery(sql);
			if(rset.next()){
				simDataRef = rset.getBigDecimal(MathModelSimulationLinkTable.table.id.getUnqualifiedColName());
				if(rset.next()){
					throw new DataAccessException("Too many ResultSetMetaData found for simKey="+simKey);
				}
			}
			//else{
				//throw new DataAccessException("No ResultSetMetaData found for simKey="+simKey);
			//}
			rset.close();
			//
			//Insert TestCriteria
			sql =
				"INSERT INTO "+TFTestCriteriaTable.table.getTableName()+
				" VALUES("+
					tcritKey.toString()+","+tcKey.toString()+","+simKey.toString()+","+(simDataRef != null?simDataRef.toString():null)+","+
				"NULL"+","+
				(addtcrit_tsop.getMaxRelativeError() != null?"TO_NUMBER('"+addtcrit_tsop.getMaxRelativeError().toString()+"')":"null")+","+
				(addtcrit_tsop.getMaxAbsoluteError() != null?"TO_NUMBER('"+addtcrit_tsop.getMaxAbsoluteError().toString()+"')":"null")+","+
				"NULL,NULL,"+
				"'"+cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT+"'"+",null"+
				")";
			stmt.executeUpdate(sql);
			if(addtcrit_tsop.getRegressionMathModelSimKey() != null){
				testSuiteOP(
					new cbit.vcell.numericstest.EditTestCriteriaOPMathModel(
						tcritKey,
						addtcrit_tsop.getRegressionMathModelKey(),
						addtcrit_tsop.getRegressionMathModelSimKey(),
						addtcrit_tsop.getMaxAbsoluteError(),addtcrit_tsop.getMaxRelativeError()),
					con,user,sessionLog);
			}
			if(addtcrit_tsop.getAddTestResultsOP() != null){
				cbit.vcell.numericstest.AddTestResultsOP atrOP = addtcrit_tsop.getAddTestResultsOP();
				//Set new TSKey,TCritKey and do child OPs
				testSuiteOP(
					new cbit.vcell.numericstest.AddTestResultsOP(tcritKey,atrOP.getVariableComparisonSummaries()),
					con,user,sessionLog);
			}
			
			rset = stmt.executeQuery(
				"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
				" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+","+TFTestCriteriaTable.table.getTableName()+
				" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
				" AND " +TFTestCriteriaTable.table.id.getQualifiedColName()+"="+tcritKey.toString()+
				" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
			while(rset.next()){
				changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
			}
			rset.close();
				
		}else if(tsop instanceof cbit.vcell.numericstest.AddTestCriteriaOPBioModel){
			cbit.vcell.numericstest.AddTestCriteriaOPBioModel addtcrit_tsop = (cbit.vcell.numericstest.AddTestCriteriaOPBioModel)tsop;
			BigDecimal tcritKey = keyFactory.getUniqueBigDecimal(con);
			BigDecimal tcKey = addtcrit_tsop.getTestCaseKey();
			KeyValue simKey = addtcrit_tsop.getBioModelSimKey();
			//Check that there is a single biomodel associated with simkey and parentsimref is null
			sql = "SELECT "+BioModelSimulationLinkTable.table.id.getQualifiedColName()+
			" FROM "+BioModelSimulationLinkTable.table.getTableName()+","+SimulationTable.table.getTableName()+
			" WHERE "+
				BioModelSimulationLinkTable.table.simRef.getQualifiedColName()+"="+simKey.toString()+
				" AND " +
				BioModelSimulationLinkTable.table.simRef.getQualifiedColName()+"="+SimulationTable.table.id.getQualifiedColName()+
				" AND " +
				SimulationTable.table.getTableName()+"."+SimulationTable.table.versionParentSimRef_ColumnName+" IS NULL";
			ResultSet rset = stmt.executeQuery(sql);
			if(rset.next()){
				if(rset.next()){
					throw new DataAccessException("Too many BioModelSimulationLink found for simKey="+simKey);
				}
			}else{
				throw new DataAccessException("No BioModelSimulationLink found for simKey="+simKey);
			}
			rset.close();
			////
			////Make sure TestCase type and regressionRef match
			//sql = "SELECT "+TFTestCaseTable.table.id.getQualifiedColName()+
				//" FROM "+TFTestCaseTable.table.getTableName()+
				//" WHERE "+
					//TFTestCaseTable.table.id.getQualifiedColName()+"="+tcKey.toString()+
					//" AND "+
					//TFTestCaseTable.table.tcSolutionType.getQualifiedColName()+
					//(regrSimKey != null?"=":"!=")+
					//"'"+cbit.vcell.numericstest.TestCaseNew.REGRESSION+"'";
			//rset = stmt.executeQuery(sql);
			//if(!rset.next()){
				//throw new DataAccessException("TestCase type not compatible with regressionSimKey");
			//}
			//rset.close();
			//
			//
			//Get simDataRef from simKey
			BigDecimal simDataRef = null;
			sql =
				"SELECT "+ResultSetMetaDataTable.table.id.getQualifiedColName()+
				" FROM " +ResultSetMetaDataTable.table.getTableName()+
				" WHERE "+
					ResultSetMetaDataTable.table.simRef.getQualifiedColName()+"="+simKey.toString();
			rset = stmt.executeQuery(sql);
			if(rset.next()){
				simDataRef = rset.getBigDecimal(BioModelSimulationLinkTable.table.id.getUnqualifiedColName());
				if(rset.next()){
					throw new DataAccessException("Too many ResultSetMetaData found for simKey="+simKey);
				}
			}
			//else{
				//throw new DataAccessException("No ResultSetMetaData found for simKey="+simKey);
			//}
			rset.close();			
			//
			//Insert TestCriteria
			sql =
				"INSERT INTO "+TFTestCriteriaTable.table.getTableName()+
				" VALUES("+
					tcritKey.toString()+","+tcKey.toString()+","+simKey.toString()+","+(simDataRef != null?simDataRef.toString():null)+","+
				"null"+","+
				(addtcrit_tsop.getMaxRelativeError() != null?"TO_NUMBER('"+addtcrit_tsop.getMaxRelativeError().toString()+"')":"null")+","+
				(addtcrit_tsop.getMaxAbsoluteError() != null?"TO_NUMBER('"+addtcrit_tsop.getMaxAbsoluteError().toString()+"')":"null")+","+
				"NULL,NULL,"+"'"+cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT+"'"+",null"+
				")";
			stmt.executeUpdate(sql);
			if(addtcrit_tsop.getRegressionBioModelSimKey() != null){
				testSuiteOP(
					new cbit.vcell.numericstest.EditTestCriteriaOPBioModel(
						tcritKey,
						addtcrit_tsop.getRegressionBioModelKey(),
						addtcrit_tsop.getRegressionBioModelSimKey(),
						addtcrit_tsop.getMaxAbsoluteError(),addtcrit_tsop.getMaxRelativeError()),
					con,user,sessionLog);
			}
			if(addtcrit_tsop.getAddTestResultsOP() != null){
				cbit.vcell.numericstest.AddTestResultsOP atrOP = addtcrit_tsop.getAddTestResultsOP();
				//Set new TSKey,TCritKey and do child OPs
				testSuiteOP(
					new cbit.vcell.numericstest.AddTestResultsOP(tcritKey,atrOP.getVariableComparisonSummaries()),
					con,user,sessionLog);
			}			
				
			rset = stmt.executeQuery(
				"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
				" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+","+TFTestCriteriaTable.table.getTableName()+
				" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
				" AND " +TFTestCriteriaTable.table.id.getQualifiedColName()+"="+tcritKey.toString()+
				" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
			while(rset.next()){
				changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
			}
			rset.close();
		}else if(tsop instanceof cbit.vcell.numericstest.RemoveTestCriteriaOP){
			cbit.vcell.numericstest.RemoveTestCriteriaOP removetcrit_tsop = (cbit.vcell.numericstest.RemoveTestCriteriaOP)tsop;
			StringBuffer sb = new StringBuffer();
			for(int i=0;i< removetcrit_tsop.getTestCriteriaKeys().length;i+= 1){
				if(i != 0){sb.append(",");}sb.append(removetcrit_tsop.getTestCriteriaKeys()[i].toString());
			}

			ResultSet rset = stmt.executeQuery(
				"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
				" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+","+TFTestCriteriaTable.table.getTableName()+
				" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
				" AND " +TFTestCriteriaTable.table.id.getQualifiedColName()+" IN ("+sb.toString()+")"+
				" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
			while(rset.next()){
				changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
			}
			rset.close();

			int numRowsUpdated = stmt.executeUpdate(
				"DELETE FROM " + TFTestCriteriaTable.table.getTableName() +
				" WHERE " + TFTestCriteriaTable.table.id.getUnqualifiedColName() + " IN ("+sb.toString() + ")"
				);
			if(numRowsUpdated != removetcrit_tsop.getTestCriteriaKeys().length){
				throw new DataAccessException("Remove TestCriteria keys="+sb.toString()+
					" removed row count="+numRowsUpdated+" expected "+removetcrit_tsop.getTestCriteriaKeys().length);
			}
		}else if(tsop instanceof cbit.vcell.numericstest.RemoveTestSuiteOP){
			cbit.vcell.numericstest.RemoveTestSuiteOP removets_tsop = (cbit.vcell.numericstest.RemoveTestSuiteOP)tsop;
			int numRowsUpdated = stmt.executeUpdate(
				"DELETE FROM "+TFTestSuiteTable.table.getTableName()+
				" WHERE "+
				TFTestSuiteTable.table.id.getUnqualifiedColName()+"="+removets_tsop.getTestSuiteKey().toString());
			if(numRowsUpdated != 1){
				throw new DataAccessException("Remove SINGLE TestSuite - key="+removets_tsop.getTestSuiteKey().toString()+" removed row count="+numRowsUpdated);
			}

			changedTestSuiteKeys.add(removets_tsop.getTestSuiteKey());	
		}else if(tsop instanceof cbit.vcell.numericstest.AddTestResultsOP){
			cbit.vcell.numericstest.AddTestResultsOP addtr_tsop = (cbit.vcell.numericstest.AddTestResultsOP)tsop;
			cbit.vcell.solver.test.VariableComparisonSummary[] vcs = addtr_tsop.getVariableComparisonSummaries();
			if(vcs == null || vcs.length == 0){
				throw new DataAccessException(cbit.vcell.numericstest.RemoveTestCasesOP.class.getName()+" had no TestResults");
			}
			for(int i=0;i<vcs.length;i+= 1){
				if(vcs[i] == null){
					throw new DataAccessException(cbit.vcell.numericstest.RemoveTestCasesOP.class.getName()+" Array element was null");
				}
			}
			ResultSet rset = stmt.executeQuery(
				"SELECT "+TFTestResultTable.table.id.getUnqualifiedColName()+
				" FROM "+TFTestResultTable.table.getTableName()+
				" WHERE " +
					TFTestResultTable.table.testCriteriaRef.getUnqualifiedColName()+"="+
					addtr_tsop.getTestCriteriaKey().toString()
				);
			if(rset.next()){
				throw new DataAccessException("AddTestResultsOP Criteria key="+addtr_tsop.getTestCriteriaKey()+" has results");
			}
			rset.close();
			for(int i=0;i<vcs.length;i+= 1){
				sql = "INSERT INTO "+TFTestResultTable.table.getTableName()+" VALUES("+
					"NEWSEQ.NEXTVAL"+","+addtr_tsop.getTestCriteriaKey().toString()+","+
					"'"+vcs[i].getName()+"'"+","+
					"TO_NUMBER('"+vcs[i].getAbsoluteError()+"')"+","+"TO_NUMBER('"+vcs[i].getRelativeError()+"')"+","+
					"TO_NUMBER('"+vcs[i].getMaxRef()+"')"+","+"TO_NUMBER('"+vcs[i].getMinRef()+"')"+","+"TO_NUMBER('"+vcs[i].getMeanSqError()+"')"+","+
					"TO_NUMBER('"+vcs[i].getTimeAbsoluteError()+"')"+","+"TO_NUMBER('"+vcs[i].getIndexAbsoluteError()+"')"+","+
					"TO_NUMBER('"+vcs[i].getTimeRelativeError()+"')"+","+"TO_NUMBER('"+vcs[i].getIndexRelativeError()+"')"+
					")";
				stmt.executeUpdate(sql);
			}
			
			rset = stmt.executeQuery(
				"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
				" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+","+TFTestCriteriaTable.table.getTableName()+
				" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
				" AND " +TFTestCriteriaTable.table.id.getQualifiedColName()+"="+addtr_tsop.getTestCriteriaKey().toString()+
				" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
			while(rset.next()){
				changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
			}
			rset.close();
		}else if(tsop instanceof cbit.vcell.numericstest.RemoveTestResultsOP){
			cbit.vcell.numericstest.RemoveTestResultsOP removetr_tsop = (cbit.vcell.numericstest.RemoveTestResultsOP)tsop;
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<removetr_tsop.getTestCriteriaKeys().length;i+= 1){
				if(i != 0){sb.append(",");}sb.append(removetr_tsop.getTestCriteriaKeys()[i].toString());
				stmt.executeUpdate(
					"DELETE FROM "+TFTestResultTable.table.getTableName()+
					" WHERE "+
					TFTestResultTable.table.testCriteriaRef.getUnqualifiedColName()+"="+removetr_tsop.getTestCriteriaKeys()[i].toString());
			}

			ResultSet rset = stmt.executeQuery(
				"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
				" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+","+TFTestCriteriaTable.table.getTableName()+
				" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
				" AND " +TFTestCriteriaTable.table.id.getQualifiedColName()+" IN ("+sb.toString()+")"+
				" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
			while(rset.next()){
				changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
			}
			rset.close();
		}else if(tsop instanceof cbit.vcell.numericstest.EditTestCriteriaOPReportStatus){
			cbit.vcell.numericstest.EditTestCriteriaOPReportStatus edittcrit_tsop = (cbit.vcell.numericstest.EditTestCriteriaOPReportStatus)tsop;
			BigDecimal tcritKey = edittcrit_tsop.getTestCriteriaKey();
			if(tcritKey == null){
				throw new DataAccessException(tsop.getClass().getName()+" had no TestCriteria keys");
			}
			String newRS = edittcrit_tsop.getNewReportStatus();
			if(newRS == null ||
				(!newRS.equals(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT) &&
				!newRS.equals(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_FAILEDVARS) &&
				!newRS.equals(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NODATA) &&
				!newRS.equals(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NOREFREGR) &&
				!newRS.equals(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_PASSED) &&
				!newRS.equals(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_RPERROR) &&
				!newRS.equals(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_SIMFAILED) &&
				!newRS.equals(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_SIMRUNNING) &&
				!newRS.equals(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_SIMNOTRUNFAILDONE))){
				throw new DataAccessException("Unsupported ReportStatus="+edittcrit_tsop.getNewReportStatus());
			}
			String reportStatusMessage = edittcrit_tsop.getNewReportStatusMessage();
			if(reportStatusMessage != null){
				reportStatusMessage = cbit.util.TokenMangler.getSQLEscapedString(reportStatusMessage);
				reportStatusMessage =
					reportStatusMessage.substring(0,Math.min(TFTestCriteriaTable.MAX_MESSAGE_SIZE,reportStatusMessage.length()));
			}
			stmt.executeUpdate(
				"UPDATE "+TFTestCriteriaTable.table.getTableName()+
				" SET "+
					TFTestCriteriaTable.table.reportStatus.getQualifiedColName()+"="+(newRS != null?"'"+newRS+"'":"null")+","+
					TFTestCriteriaTable.table.reportMessage.getQualifiedColName()+"="+(reportStatusMessage!= null?"'"+reportStatusMessage+"'":"null")+
				" WHERE "+TFTestCriteriaTable.table.id.getQualifiedColName()+"="+tcritKey.toString()
				);
			if(newRS.equals(cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT)){
				testSuiteOP(new RemoveTestResultsOP(new BigDecimal[] {tcritKey}), con, user, sessionLog);
			}

			ResultSet rset = stmt.executeQuery(
				"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
				" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+","+TFTestCriteriaTable.table.getTableName()+
				" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
				" AND " +TFTestCriteriaTable.table.id.getQualifiedColName()+" = "+tcritKey.toString()+
				" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
			while(rset.next()){
				changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
			}
			rset.close();
		}else if(tsop instanceof cbit.vcell.numericstest.EditTestCriteriaOPMathModel){
			cbit.vcell.numericstest.EditTestCriteriaOPMathModel edittcrit_tsop = (cbit.vcell.numericstest.EditTestCriteriaOPMathModel)tsop;
			BigDecimal tcritKey = edittcrit_tsop.getTestCriteriaKey();
			if(tcritKey == null){
				throw new DataAccessException(tsop.getClass().getName()+" had no TestCriteria keys");
			}
			Double maxAbsError = edittcrit_tsop.getNewMaxAbsError();
			Double maxRelError = edittcrit_tsop.getNewMaxRelError();
			KeyValue regrSimRef = edittcrit_tsop.getMathModelRegressionSimRef();
			//Convert regrSimKey to regrMathModelSimLinkKey
			BigDecimal regrMathModelSimLink = null;
			if(regrSimRef != null){
				sql =
				"SELECT "+MathModelSimulationLinkTable.table.id.getQualifiedColName()+
				" FROM "+MathModelSimulationLinkTable.table.getTableName()+
				" WHERE "+
					MathModelSimulationLinkTable.table.simRef.getQualifiedColName()+"="+regrSimRef.toString();
				ResultSet rset = stmt.executeQuery(sql);
				if(rset.next()){
					regrMathModelSimLink = rset.getBigDecimal(MathModelSimulationLinkTable.table.id.getUnqualifiedColName());
					if(rset.next()){
						throw new DataAccessException("Too many MathModelSimLink found for regrSimKey="+regrSimRef);
					}
				}else{
					throw new DataAccessException("No MathModelSimLink found for regrSimKey="+regrSimRef);
				}
				rset.close();
			}
			stmt.executeUpdate(
				"UPDATE "+TFTestCriteriaTable.table.getTableName()+
				" SET "+
					TFTestCriteriaTable.table.maxAbsError.getQualifiedColName()+"="+(maxAbsError != null?"TO_NUMBER("+maxAbsError.toString()+")":"null")+","+
					TFTestCriteriaTable.table.maxRelError.getQualifiedColName()+"="+(maxRelError != null?"TO_NUMBER("+maxRelError.toString()+")":"null")+","+
					TFTestCriteriaTable.table.regressionMMSimRef.getQualifiedColName()+"="+(regrMathModelSimLink != null?regrMathModelSimLink.toString():"null")+
				" WHERE "+TFTestCriteriaTable.table.id.getQualifiedColName()+"="+tcritKey.toString()
				);
			testSuiteOP(new EditTestCriteriaOPReportStatus(tcritKey,cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null), con, user, sessionLog);

			ResultSet rset = stmt.executeQuery(
				"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
				" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+","+TFTestCriteriaTable.table.getTableName()+
				" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
				" AND " +TFTestCriteriaTable.table.id.getQualifiedColName()+" = "+tcritKey.toString()+
				" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
			while(rset.next()){
				changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
			}
			rset.close();
		}else if(tsop instanceof cbit.vcell.numericstest.EditTestCriteriaOPBioModel){
			cbit.vcell.numericstest.EditTestCriteriaOPBioModel edittcrit_tsop = (cbit.vcell.numericstest.EditTestCriteriaOPBioModel)tsop;
			BigDecimal tcritKey = edittcrit_tsop.getTestCriteriaKey();
			if(tcritKey == null){
				throw new DataAccessException(tsop.getClass().getName()+" had no TestCriteria keys");
			}
			Double maxAbsError = edittcrit_tsop.getNewMaxAbsError();
			Double maxRelError = edittcrit_tsop.getNewMaxRelError();
			BigDecimal bmscAppKey = null;
			BigDecimal bmsltSimKey = null;
			final String BMSLT = "BMSLT";
			if(edittcrit_tsop.getBioModelRegressionSimRef() != null){
				sql =
					"SELECT "+
						BioModelSimContextLinkTable.table.id.getQualifiedColName()+","+
						BioModelSimulationLinkTable.table.id.getQualifiedColName()+" "+BMSLT+
					" FROM " +
						BioModelSimContextLinkTable.table.getTableName()+","+
						SimContextTable.table.getTableName()+","+
						SimulationTable.table.getTableName()+","+
						BioModelSimulationLinkTable.table.getTableName()+
					" WHERE "+
						BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+"="+edittcrit_tsop.getBioModelRegressionRef()+
						" AND " +
						BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+"="+SimContextTable.table.id.getQualifiedColName()+
						" AND " +
						SimContextTable.table.mathRef.getQualifiedColName()+"="+SimulationTable.table.mathRef.getQualifiedColName()+
						" AND " +
						SimulationTable.table.id.getQualifiedColName()+"="+edittcrit_tsop.getBioModelRegressionSimRef().toString()+
						" AND " +
						BioModelSimulationLinkTable.table.bioModelRef.getQualifiedColName()+" = "+BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+
						" AND " +
						BioModelSimulationLinkTable.table.simRef.getQualifiedColName()+" = "+ SimulationTable.table.id.getQualifiedColName();
				ResultSet rset = stmt.executeQuery(sql);
				if(rset.next()){
					bmscAppKey = rset.getBigDecimal(BioModelSimContextLinkTable.table.id.getUnqualifiedColName());
					bmsltSimKey = rset.getBigDecimal(BMSLT);
					if(rset.next()){
						throw new DataAccessException("Too many ResultSetMetaData found for simKey="+edittcrit_tsop.getBioModelRegressionSimRef());
					}
				}
				rset.close();
			}

			stmt.executeUpdate(
				"UPDATE "+TFTestCriteriaTable.table.getTableName()+
				" SET "+
					TFTestCriteriaTable.table.maxAbsError.getQualifiedColName()+"="+(maxAbsError != null?"TO_NUMBER("+maxAbsError.toString()+")":"null")+","+
					TFTestCriteriaTable.table.maxRelError.getQualifiedColName()+"="+(maxRelError != null?"TO_NUMBER("+maxRelError.toString()+")":"null")+","+
					TFTestCriteriaTable.table.regressionBMAPPRef.getQualifiedColName()+"="+(bmscAppKey != null?bmscAppKey.toString():"NULL")+","+
					TFTestCriteriaTable.table.regressionBMSimRef.getQualifiedColName()+"="+(bmsltSimKey != null?bmsltSimKey.toString():"NULL")+
				" WHERE "+TFTestCriteriaTable.table.id.getQualifiedColName()+"="+tcritKey.toString()
				);
			testSuiteOP(new EditTestCriteriaOPReportStatus(tcritKey,cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null), con, user, sessionLog);
			
			ResultSet rset = stmt.executeQuery(
				"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
				" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+","+TFTestCriteriaTable.table.getTableName()+
				" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
				" AND " +TFTestCriteriaTable.table.id.getQualifiedColName()+" = "+tcritKey.toString()+
				" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
			while(rset.next()){
				changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
			}
			rset.close();
		}else if(tsop instanceof cbit.vcell.numericstest.ChangeTestCriteriaErrorLimitOP){
			cbit.vcell.numericstest.ChangeTestCriteriaErrorLimitOP edittcrit_tsop = (cbit.vcell.numericstest.ChangeTestCriteriaErrorLimitOP)tsop;
			BigDecimal[] tcritKeyArr = edittcrit_tsop.getTestCriteriaKeys();
			double[] maxAbsErrorArr = edittcrit_tsop.getAbsErrorLimits();
			double[] maxRelErrorArr = edittcrit_tsop.getRelErrorLimits();
			if(tcritKeyArr == null || (maxAbsErrorArr != null && maxAbsErrorArr.length != tcritKeyArr.length) || (maxAbsErrorArr != null && maxRelErrorArr.length != tcritKeyArr.length)){
				throw new DataAccessException(tsop.getClass().getName()+" Improper arguments.");
			}
			StringBuffer tcritList = new StringBuffer();
			for (int i = 0; i < tcritKeyArr.length; i++) {
				if(i!= 0){tcritList.append(",");}
				tcritList.append(tcritKeyArr[i]);
				stmt.executeUpdate(
					"UPDATE "+TFTestCriteriaTable.table.getTableName()+
					" SET "+
					(maxAbsErrorArr != null?TFTestCriteriaTable.table.maxAbsError.getQualifiedColName()+"="+maxAbsErrorArr[i]:"")+
					(maxAbsErrorArr != null && maxRelErrorArr != null?",":"")+
					(maxRelErrorArr != null?TFTestCriteriaTable.table.maxRelError.getQualifiedColName()+"="+maxRelErrorArr[i]:"")+
					" WHERE "+TFTestCriteriaTable.table.id.getQualifiedColName()+"="+tcritKeyArr[i].toString()
					);
				testSuiteOP(new EditTestCriteriaOPReportStatus(tcritKeyArr[i],cbit.vcell.numericstest.TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null), con, user, sessionLog);
			}
			ResultSet rset = stmt.executeQuery(
					"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
					" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+","+TFTestCriteriaTable.table.getTableName()+
					" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+TFTestCriteriaTable.table.testCaseRef.getQualifiedColName()+
					" AND " +TFTestCriteriaTable.table.id.getQualifiedColName()+" IN ("+tcritList.toString()+")"+
					" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
				while(rset.next()){
					changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
				}

		}else if(tsop instanceof cbit.vcell.numericstest.EditTestCasesOP){
			cbit.vcell.numericstest.EditTestCasesOP edittc_tsop = (cbit.vcell.numericstest.EditTestCasesOP)tsop;
			BigDecimal[] tcaseKeys = edittc_tsop.getTestCasesKeys();
			String[] annots = edittc_tsop.getNewAnnotations();
			boolean[] newSteadyStates = edittc_tsop.getNewSteadyStates();
			if(tcaseKeys == null || tcaseKeys.length == 0){
				throw new DataAccessException(tsop.getClass().getName()+" had no TestCase keys");
			}
			StringBuffer sb = new StringBuffer();
			
			for(int i=0;i<tcaseKeys.length;i+= 1){
				if(i != 0){sb.append(",");}sb.append(tcaseKeys[i].toString());
				if(annots != null){
					String annotation = annots[i];
					if(annotation != null){
						if(annotation.length() == 0){
							annotation = null;
						}else{
							annotation = cbit.util.TokenMangler.getSQLEscapedString(annotation);
						}
					}
					stmt.executeUpdate(
						"UPDATE "+TFTestCaseTable.table.getTableName()+
						" SET "+
							TFTestCaseTable.table.tcAnnotation.getQualifiedColName()+"="+(annotation == null?"NULL":"'"+annotation+"'")+
						" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+tcaseKeys[i].toString()
						);
				}
				if(newSteadyStates != null){
					//Make sure the change is for EXACT type
					ResultSet rset = stmt.executeQuery(
							"SELECT DISTINCT "+TFTestCriteriaTable.table.id.getQualifiedColName()+
							" FROM "+
							TFTestCaseTable.table.getTableName() +","+
							TFTestCriteriaTable.table.getTableName()+
							" WHERE " +
								TFTestCaseTable.table.id.getQualifiedColName()+"="+tcaseKeys[i].toString()+
								" AND " +
								"("+
									TFTestCaseTable.table.tcSolutionType.getQualifiedColName()+"='"+TestCaseNew.EXACT+"'"+
									" OR "+
									TFTestCaseTable.table.tcSolutionType.getQualifiedColName()+"='"+TestCaseNew.EXACT_STEADY+"'"+
								")"+
								" AND "+
								TFTestCriteriaTable.table.testCaseRef+"="+TFTestCaseTable.table.id.getQualifiedColName()
							);
					//Get TestCriteria Keys for Test case
					Vector<BigDecimal> tcritKeyV = new Vector<BigDecimal>();
					while(rset.next()){
						tcritKeyV.add(rset.getBigDecimal(TFTestCriteriaTable.table.id.getUnqualifiedColName()));
					}
					if(tcritKeyV.size() == 0){
						throw new DataAccessException("Updating SteadyState on TestCase that is not EXACT is NOT allowed.");						
					}
					rset.close();
					//Change SteadyState type for TestCase
					boolean newSteadyState = newSteadyStates[i];
					stmt.executeUpdate(
						"UPDATE "+TFTestCaseTable.table.getTableName()+
						" SET "+
							TFTestCaseTable.table.tcSolutionType.getQualifiedColName()+"="+
							(newSteadyState?"'"+TestCaseNew.EXACT_STEADY+"'":"'"+TestCaseNew.EXACT+"'")+
						" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+"="+tcaseKeys[i].toString()
						);
					//Change Report status
					for (int j = 0; j < tcritKeyV.size(); j++) {
						EditTestCriteriaOPReportStatus etcors = 
							new EditTestCriteriaOPReportStatus(tcritKeyV.elementAt(j),TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null);
						testSuiteOP(etcors, con, user, sessionLog);
						
					}
				}
			}
			
			ResultSet rset = stmt.executeQuery(
				"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
				" FROM "+TFTestSuiteTable.table.getTableName()+","+TFTestCaseTable.table.getTableName()+
				" WHERE "+TFTestCaseTable.table.id.getQualifiedColName()+" IN ("+sb.toString()+")"+
				" AND " +TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+"="+TFTestSuiteTable.table.id.getQualifiedColName());
			while(rset.next()){
				changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
			}
			rset.close();
		}else if(tsop instanceof cbit.vcell.numericstest.EditTestSuiteOP){
			cbit.vcell.numericstest.EditTestSuiteOP editts_tsop = (cbit.vcell.numericstest.EditTestSuiteOP)tsop;
			BigDecimal[] tsKeys = editts_tsop.getTestSuiteKeys();
			String[] annots = editts_tsop.getNewAnnotations();
			if(tsKeys == null || tsKeys.length == 0){
				throw new DataAccessException(tsop.getClass().getName()+" had no TestCase keys");
			}
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<tsKeys.length;i+= 1){
				if(i != 0){sb.append(",");}sb.append(tsKeys[i].toString());
				if(annots != null){
					String annotation = annots[i];
					if(annotation != null){
						if(annotation.length() == 0){
							annotation = null;
						}else{
							annotation = cbit.util.TokenMangler.getSQLEscapedString(annotation);
						}
					}
					stmt.executeUpdate(
						"UPDATE "+TFTestSuiteTable.table.getTableName()+
						" SET "+
							TFTestSuiteTable.table.tsAnnotation.getQualifiedColName()+"="+(annotation == null?"NULL":"'"+annotation+"'")+
						" WHERE "+TFTestSuiteTable.table.id.getQualifiedColName()+"="+tsKeys[i].toString()
						);
				}
			}			
			
			ResultSet rset = stmt.executeQuery(
					"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
					" FROM "+TFTestSuiteTable.table.getTableName()+
					" WHERE "+TFTestSuiteTable.table.id.getQualifiedColName()+" IN ("+sb.toString()+")");
				while(rset.next()){
					changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
				}
				rset.close();
		}else if(tsop instanceof cbit.vcell.numericstest.QueryTestCriteriaCrossRefOP){
			cbit.vcell.numericstest.QueryTestCriteriaCrossRefOP qtcritxr_tsop = (cbit.vcell.numericstest.QueryTestCriteriaCrossRefOP)tsop;
			Vector<TestCriteriaCrossRefOPResults.CrossRefData> crossRefV = new Vector<TestCriteriaCrossRefOPResults.CrossRefData>();
			//
			//BioModel query
			//
			String TSID = "tsid";
			String TCID = "tcid";
			String TCRID = "tcrid";
			String BMNAME = "bmname";
			String SCNAME = "scname";
			String SIMNAME = "simname";
			String TSALT = "TSALT";
			String TCALT = "TCALT";
			String TCRALT = "TCRALT";
			String BSCALT = "BSCALT";
			String MMSIMALT = "MMSIMALT";
			String MODELID = "MODELID";
			sql =
				"SELECT DISTINCT " +
				TFTestSuiteTable.table.tsVersion.getQualifiedColName() +"," +
				TFTestSuiteTable.table.id.getQualifiedColName()+" "+TSID+","+
				TFTestCaseTable.table.id.getQualifiedColName() +" "+TCID+","+
				TFTestCriteriaTable.table.id.getQualifiedColName() +" "+TCRID+","+
				BioModelTable.table.name.getQualifiedColName() +" "+BMNAME+","+
				SimContextTable.table.name.getQualifiedColName() +" "+SCNAME+","+
				SimulationTable.table.name.getQualifiedColName() +" "+SIMNAME+","+
				TFTestCriteriaTable.table.maxAbsError.getQualifiedColName()+","+
				TFTestCriteriaTable.table.maxRelError.getQualifiedColName()+","+
				TFTestResultTable.table.varName.getQualifiedColName()+","+
				TFTestResultTable.table.minRef.getQualifiedColName()+","+
				TFTestResultTable.table.maxRef.getQualifiedColName()+","+
				TFTestResultTable.table.absError.getQualifiedColName()+","+
				TFTestResultTable.table.relError.getQualifiedColName()+","+
				TFTestResultTable.table.meanSqrError.getQualifiedColName()+","+
				TFTestResultTable.table.timeAbsError.getQualifiedColName()+","+
				TFTestResultTable.table.indexAbsError.getQualifiedColName()+","+
				TFTestResultTable.table.timeRelError.getQualifiedColName()+","+
				TFTestResultTable.table.indexRelError.getQualifiedColName() +","+
				TSALT+"."+TFTestSuiteTable.table.tsVersion.getUnqualifiedColName() +","+
				BSCALT+"."+BioModelSimContextLinkTable.table.bioModelRef.getUnqualifiedColName() +","+
				TFTestCaseTable.table.tcSolutionType.getQualifiedColName() +","+
				BioModelTable.table.id.getQualifiedColName() +" "+MODELID+
//				"DECODE("+
//					TSALT+"."+TFTestSuiteTable.table.id.getUnqualifiedColName()+",NULL,DECODE("+
//					TCRALT+"."+TFTestCriteriaTable.table.regressionBMAPPRef.getUnqualifiedColName()+",NULL,NULL,'regressionBMAPPRef exist BUT outside of TestSuites')"+
//					","+TSALT+"."+TFTestSuiteTable.table.tsVersion.getUnqualifiedColName()+")"+
				" FROM " +
				TFTestSuiteTable.table.getTableName()+","+
				TFTestCaseTable.table.getTableName()+","+
				TFTestCriteriaTable.table.getTableName()+","+
				TFTestResultTable.table.getTableName()+","+
				BioModelSimContextLinkTable.table.getTableName()+","+
				BioModelTable.table.getTableName()+","+
				SimContextTable.table.getTableName()+","+
				SimulationTable.table.getTableName() +
				","+
		BioModelSimulationLinkTable.table.getTableName()+","+
				TFTestSuiteTable.table.getTableName()+" "+TSALT+","+
				TFTestCaseTable.table.getTableName()+" "+TCALT+","+
				TFTestCriteriaTable.table.getTableName()+" "+TCRALT+
				","+BioModelSimContextLinkTable.table.getTableName()+" "+BSCALT+
				" WHERE " +
				TFTestSuiteTable.table.id.getQualifiedColName() +"="+ TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+
				" AND " +
				TFTestCriteriaTable.table.testCaseRef.getQualifiedColName() +"="+ TFTestCaseTable.table.id.getQualifiedColName()+
				" AND " +
				TFTestResultTable.table.testCriteriaRef.getQualifiedColName()+"(+)" +"="+ TFTestCriteriaTable.table.id.getQualifiedColName()+
				(qtcritxr_tsop.getVarName() != null?" AND "+TFTestResultTable.table.varName.getQualifiedColName()+"(+)"+"="+"'"+qtcritxr_tsop.getVarName()+"'":"")+
				" AND " +
				TFTestCaseTable.table.bmAppRef.getQualifiedColName() +"="+ BioModelSimContextLinkTable.table.id.getQualifiedColName()+
				" AND " +
				BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName() +"="+ BioModelTable.table.id.getQualifiedColName()+
				" AND " +
				BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName() +"="+ SimContextTable.table.id.getQualifiedColName()+
				" AND " +
		BioModelSimulationLinkTable.table.bioModelRef.getQualifiedColName() +"="+ BioModelTable.table.id.getQualifiedColName()+
		" AND " +
		BioModelSimulationLinkTable.table.simRef.getQualifiedColName() +"="+ SimulationTable.table.id.getQualifiedColName()+
				" AND " +
					BioModelTable.table.versionBranchID.getQualifiedColName() +"="+
					"("+
						"SELECT DISTINCT " +
						BioModelTable.table.versionBranchID.getQualifiedColName()+
						" FROM " + 
						TFTestCaseTable.table.getTableName()+","+
						TFTestCriteriaTable.table.getTableName()+","+
						BioModelSimContextLinkTable.table.getTableName()+","+
						BioModelTable.table.getTableName()+
						" WHERE " +
						TFTestCriteriaTable.table.id.getQualifiedColName() +"="+ qtcritxr_tsop.getTestCriterium()+
						" AND " +
						TFTestCriteriaTable.table.testCaseRef.getQualifiedColName() +"="+ TFTestCaseTable.table.id.getQualifiedColName()+
						" AND " +
						TFTestCaseTable.table.bmAppRef.getQualifiedColName() +"="+ BioModelSimContextLinkTable.table.id.getQualifiedColName()+
						" AND " +
						BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName() +"="+ BioModelTable.table.id.getQualifiedColName()+
					")"+
				" AND " +
					SimContextTable.table.name.getQualifiedColName() +"="+
					"("+
						"SELECT DISTINCT " +
						SimContextTable.table.name.getQualifiedColName()+
						" FROM " +
						TFTestCaseTable.table.getTableName()+","+
						TFTestCriteriaTable.table.getTableName()+","+
						BioModelSimContextLinkTable.table.getTableName()+","+
						SimContextTable.table.getTableName()+
						" WHERE " +
						TFTestCriteriaTable.table.id.getQualifiedColName() +"="+ qtcritxr_tsop.getTestCriterium()+
						" AND " +
						TFTestCriteriaTable.table.testCaseRef.getQualifiedColName() +"="+ TFTestCaseTable.table.id.getQualifiedColName()+
						" AND " +
						TFTestCaseTable.table.bmAppRef.getQualifiedColName() +"="+ BioModelSimContextLinkTable.table.id.getQualifiedColName()+
						" AND " +
						BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName() +"="+SimContextTable.table.id.getQualifiedColName() +
					")"+
				" AND " +
					SimulationTable.table.name.getQualifiedColName()+"="+
					"("+
						"SELECT DISTINCT " +
						SimulationTable.table.name.getQualifiedColName()+
						" FROM " +
						TFTestCriteriaTable.table.getTableName()+","+
						SimulationTable.table.getTableName()+
						" WHERE " +
						TFTestCriteriaTable.table.id.getQualifiedColName() +"="+ qtcritxr_tsop.getTestCriterium()+
						" AND " +
						TFTestCriteriaTable.table.simulationRef.getQualifiedColName() +"="+ SimulationTable.table.id.getQualifiedColName()+
					")"+
				//reference info
				" AND " +
				TSALT+"."+TFTestSuiteTable.table.id.getUnqualifiedColName()+"(+)" +"="+ TCALT+"."+TFTestCaseTable.table.testSuiteRef.getUnqualifiedColName()+
				" AND "+
				BSCALT+"."+BioModelSimContextLinkTable.table.id.getUnqualifiedColName()+"(+)" +"="+ TCRALT+"."+TFTestCriteriaTable.table.regressionBMAPPRef.getUnqualifiedColName()+
				" AND "+
				TCALT+"."+TFTestCaseTable.table.bmAppRef.getUnqualifiedColName()+"(+)" +"="+ TCRALT+"."+TFTestCriteriaTable.table.regressionBMAPPRef.getUnqualifiedColName()+
				" AND "+
				TCRALT+"."+TFTestCriteriaTable.table.id.getUnqualifiedColName() +"="+ TFTestCriteriaTable.table.id.getQualifiedColName()+
				" ORDER BY "+TSID;

			ResultSet rset = stmt.executeQuery(sql);
			while(rset.next()){
				crossRefV.add(new TestCriteriaCrossRefOPResults.CrossRefData(rset,true));	
			}
			rset.close();
			
			
			
			
			//
			//MathModel query
			//
			String MMNAME = "mmname";
			sql =
				"SELECT DISTINCT " +
				TFTestSuiteTable.table.tsVersion.getQualifiedColName() +"," +
				TFTestSuiteTable.table.id.getQualifiedColName()+" "+TSID+","+
				TFTestCaseTable.table.id.getQualifiedColName() +" "+TCID+","+
				TFTestCriteriaTable.table.id.getQualifiedColName() +" "+TCRID+","+
				MathModelTable.table.name.getQualifiedColName() +" "+MMNAME+","+
				SimulationTable.table.name.getQualifiedColName() +" "+SIMNAME+","+
				TFTestCriteriaTable.table.maxAbsError.getQualifiedColName()+","+
				TFTestCriteriaTable.table.maxRelError.getQualifiedColName()+","+
				TFTestResultTable.table.varName.getQualifiedColName()+","+
				TFTestResultTable.table.minRef.getQualifiedColName()+","+
				TFTestResultTable.table.maxRef.getQualifiedColName()+","+
				TFTestResultTable.table.absError.getQualifiedColName()+","+
				TFTestResultTable.table.relError.getQualifiedColName()+","+
				TFTestResultTable.table.meanSqrError.getQualifiedColName()+","+
				TFTestResultTable.table.timeAbsError.getQualifiedColName()+","+
				TFTestResultTable.table.indexAbsError.getQualifiedColName()+","+
				TFTestResultTable.table.timeRelError.getQualifiedColName()+","+
				TFTestResultTable.table.indexRelError.getQualifiedColName() +","+
				TSALT+"."+TFTestSuiteTable.table.tsVersion.getUnqualifiedColName() +","+
				MMSIMALT+"."+MathModelSimulationLinkTable.table.mathModelRef.getUnqualifiedColName() +","+
				TFTestCaseTable.table.tcSolutionType.getQualifiedColName() +","+
				MathModelTable.table.id.getQualifiedColName() +" "+MODELID+
//				"DECODE("+
//				TSALT+"."+TFTestSuiteTable.table.id.getUnqualifiedColName()+",NULL,DECODE("+
//				TCRALT+"."+TFTestCriteriaTable.table.regressionMMSimRef.getUnqualifiedColName()+",NULL,NULL,'regressionMMSimRef exist BUT outside of TestSuites')"+
//				","+TSALT+"."+TFTestSuiteTable.table.tsVersion.getUnqualifiedColName()+")"+
				" FROM " +
				TFTestSuiteTable.table.getTableName()+","+
				TFTestCaseTable.table.getTableName()+","+
				TFTestCriteriaTable.table.getTableName()+","+
				TFTestResultTable.table.getTableName()+","+
				MathModelTable.table.getTableName()+","+
				SimulationTable.table.getTableName() +
				","+
		MathModelSimulationLinkTable.table.getTableName()+","+
				TFTestSuiteTable.table.getTableName()+" "+TSALT+","+
				TFTestCaseTable.table.getTableName()+" "+TCALT+","+
				TFTestCriteriaTable.table.getTableName()+" "+TCRALT+","+
				MathModelSimulationLinkTable.table.getTableName()+" "+MMSIMALT+
				" WHERE " +
				TFTestSuiteTable.table.id.getQualifiedColName()+"="+ TFTestCaseTable.table.testSuiteRef.getQualifiedColName()+
				" AND " +
				TFTestCriteriaTable.table.testCaseRef.getQualifiedColName() +"="+ TFTestCaseTable.table.id.getQualifiedColName()+
				" AND " +
				TFTestResultTable.table.testCriteriaRef.getQualifiedColName()+"(+)" +"="+ TFTestCriteriaTable.table.id.getQualifiedColName()+
				(qtcritxr_tsop.getVarName() != null?" AND "+TFTestResultTable.table.varName.getQualifiedColName()+"(+)"+"="+"'"+qtcritxr_tsop.getVarName()+"'":"")+
				" AND " +
				TFTestCaseTable.table.mathModelRef.getQualifiedColName() +"="+ MathModelTable.table.id.getQualifiedColName()+
				" AND " +
				TFTestCriteriaTable.table.simulationRef.getQualifiedColName() +"="+ SimulationTable.table.id.getQualifiedColName()+
				" AND "+
		MathModelSimulationLinkTable.table.mathModelRef.getQualifiedColName() +"="+ MathModelTable.table.id.getQualifiedColName()+
		" AND " +
		MathModelSimulationLinkTable.table.simRef.getQualifiedColName() +"="+ SimulationTable.table.id.getQualifiedColName()+
				" AND " +
				MathModelTable.table.versionBranchID.getQualifiedColName() +"="+
				"("+
				"SELECT DISTINCT " +
				MathModelTable.table.versionBranchID.getQualifiedColName()+
				" FROM " + 
				TFTestCaseTable.table.getTableName()+","+
				TFTestCriteriaTable.table.getTableName()+","+
				MathModelTable.table.getTableName()+
				" WHERE " +
				TFTestCriteriaTable.table.id.getQualifiedColName() +"="+ qtcritxr_tsop.getTestCriterium()+
				" AND " +
				TFTestCriteriaTable.table.testCaseRef.getQualifiedColName() +"="+ TFTestCaseTable.table.id.getQualifiedColName()+
				" AND " +
				TFTestCaseTable.table.mathModelRef.getQualifiedColName() +"="+ MathModelTable.table.id.getQualifiedColName()+
				")"+
				" AND " +
				SimulationTable.table.name.getQualifiedColName()+"="+
				"("+
				"SELECT DISTINCT " +
				SimulationTable.table.name.getQualifiedColName()+
				" FROM " +
				TFTestCriteriaTable.table.getTableName()+","+
				SimulationTable.table.getTableName()+
				" WHERE " +
				TFTestCriteriaTable.table.id.getQualifiedColName() +"="+ qtcritxr_tsop.getTestCriterium()+
				" AND " +
				TFTestCriteriaTable.table.simulationRef.getQualifiedColName() +"="+ SimulationTable.table.id.getQualifiedColName()+
				")"+
				//reference info
				" AND " +
				TSALT+"."+TFTestSuiteTable.table.id.getUnqualifiedColName()+"(+)"  +"="+ TCALT+"."+TFTestCaseTable.table.testSuiteRef.getUnqualifiedColName()+
				" AND "+
				TCALT+"."+TFTestCaseTable.table.mathModelRef.getUnqualifiedColName()+"(+)"  +"="+ MMSIMALT+"."+MathModelSimulationLinkTable.table.mathModelRef.getUnqualifiedColName()+
				" AND "+
				TCRALT+"."+TFTestCriteriaTable.table.regressionMMSimRef.getUnqualifiedColName()+"="+ MMSIMALT+"."+MathModelSimulationLinkTable.table.id.getUnqualifiedColName()+"(+)" +
				" AND "+
				TCRALT+"."+TFTestCriteriaTable.table.id.getUnqualifiedColName() +"="+ TFTestCriteriaTable.table.id.getQualifiedColName()+
				" ORDER BY "+TSID;

			rset = stmt.executeQuery(sql);
			while(rset.next()){
				crossRefV.add(new TestCriteriaCrossRefOPResults.CrossRefData(rset,false));	
			}
			rset.close();
				
			return new TestCriteriaCrossRefOPResults(qtcritxr_tsop.getTestSuiteKey(),qtcritxr_tsop.getTestCriterium(),crossRefV);

		}else{
			throw new IllegalArgumentException("Unsupported OP+"+tsop);
		}
	}finally{
		if(stmt != null){
			stmt.close();
		}
	}

	Object[] changedTSKeys = changedTestSuiteKeys.toArray();
	for(int i=0;i<changedTSKeys.length;i+= 1){
		System.out.println("TestSuite "+changedTSKeys[i].toString()+" changed");
		//testSuiteHash.remove(changedTSKeys[i]);
	}
	return new cbit.vcell.numericstest.TestSuiteOPResults(null);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public static void updateAnnotation(Connection con,SessionLog newLog,User user, 
									VersionableType vType, KeyValue vKey, String annot,DBCacheTable currentCache) 
							throws SQLException,ObjectNotFoundException, DataAccessException {
	if ((user == null) || (vKey == null)) {
		throw new IllegalArgumentException("Improper parameters for updateAnnotation");
	}
	Version version = getVersionFromKeyValue(con,vType,vKey);
	if (!version.getOwner().compareEqual(user)){
		throw new PermissionException("Cannot unpublish "+vType.getTypeName()+" \""+version.getName()+"\" ("+vKey+"), not owned by "+user.getName());
	}
	annot = cbit.util.TokenMangler.getSQLEscapedString(annot);
	newLog.print("GeomDbDriver.updateAnnotation(user=" + user + ", key=" + vKey + "Annot="+annot+")");
	VersionTable vTable = VersionTable.getVersionTable(vType);
	String set = vTable.versionAnnot.getQualifiedColName() + " = " + "'" + annot + "'";
	String cond = vTable.id.getQualifiedColName() + " = " + vKey + 
				" AND " + vTable.ownerRef.getQualifiedColName() + " = " + user.getID();
	String sql = DatabasePolicySQL.enforceOwnershipUpdate(user,vTable,set,cond);
//System.out.println(sql);
	if (updateCleanSQL(con, sql)!=1){
		throw new DataAccessException("updateAnnotation failed versionable with key = "+vKey);
	}
	currentCache.remove(vKey);
}


/**
 * This method was created in VisualAge.
 * @param sql java.lang.String
 */
protected static void updateCleanLOB(Connection con,String conditionalColumnName,KeyValue conditionalValue,String table,String column,Object lob_data) throws SQLException,DataAccessException {
	if (table == null || con == null || column == null || lob_data == null) {
		throw new IllegalArgumentException("Improper parameters for updateCleanLOB");
	}
	if(!(lob_data instanceof byte[]) && !(lob_data instanceof String)){
		throw new IllegalArgumentException("Wrong DataType "+lob_data.getClass().toString()+" for updateCleanLOB.   only byte[] and String allowed");
	}
	//Select the LOB(column) from the table
	String sql = "SELECT "+	column + " FROM " + table +
				" WHERE "+conditionalColumnName+" = " + conditionalValue +
				" FOR UPDATE OF " + table+"."+column;
	//
	//System.out.println(sql);
	//
	Statement s = con.createStatement();
	try {
		ResultSet rset = s.executeQuery(sql);
		if(rset.next()){
			Object lob_object = rset.getObject(column);
			if(lob_data instanceof byte[] && lob_object instanceof oracle.sql.BLOB){
				//oracle.sql.BLOB selectedBLOB = ((oracle.jdbc.OracleResultSet) rset).getBLOB(1);
				oracle.sql.BLOB selectedBLOB = (oracle.sql.BLOB)lob_object;
				selectedBLOB.putBytes(1,(byte[])lob_data);
			}else if (lob_data instanceof String && lob_object instanceof oracle.sql.CLOB){
				//oracle.sql.CLOB selectedCLOB = ((oracle.jdbc.OracleResultSet) rset).getCLOB(1);
				oracle.sql.CLOB selectedCLOB = (oracle.sql.CLOB)lob_object;
				selectedCLOB.putString(1,(String)lob_data);
			}
			else{
				throw new DataAccessException("DataType="+lob_data.getClass().toString()+" to store did not match column="+column+" DataType="+lob_object.getClass().toString());
			}
		}else{
			throw new DataAccessException("updateCleanLOB: No results on select "+conditionalColumnName+"="+conditionalValue+" from "+table+"."+column+" for LOB update");
		}
	} finally {
		s.close();
	}
}


/**
 * This method was created in VisualAge.
 * @param sql java.lang.String
 */
public static int updateCleanSQL(Connection con, String sql) throws SQLException {
	if (sql == null || con == null) {
		throw new IllegalArgumentException("Improper parameters for updateClean");
	}
System.out.println("updateClean SQL "+sql);
	Statement s = con.createStatement();
	try {
		return s.executeUpdate(sql);
	} finally {
		s.close();
	}
}


/**
 * This method was created in VisualAge.
 * @return cbit.image.VCImage
 * @param user cbit.vcell.server.User
 * @param image cbit.image.VCImage
 */
protected Version updateVersionableInit(InsertHashtable hash, Connection con, User user, Versionable versionable, boolean bVersion) 
			throws DataAccessException, SQLException, RecordChangedException{

	if (hash.getDatabaseKey(versionable)!=null){
		throw new DataAccessException(versionable+" already inserted in this transaction");
	}

	if (versionable.getVersion() == null || versionable.getVersion().getVersionKey() == null) {
		throw new DataAccessException(versionable + " Not expecting null key before update.  Update Failed");
	}
	//Can only update things we own
	if(!versionable.getVersion().getOwner().equals(user)){
		throw new PermissionException("Versionable name="+versionable.getName()+" type="+VersionableType.fromVersionable(versionable)+"\nuser="+versionable.getVersion().getOwner()+" Not Equal to client user="+user);
	}
	
	//
	// get new Version info
	//
	User owner = user;
	GroupAccess accessInfo = versionable.getVersion().getGroupAccess();
	//
	//Make sure if the user has changed permission on an open Biomodel,MathModel or Geometry
	//that we use the database permission
	//
	if(versionable instanceof cbit.vcell.biomodel.BioModelMetaData ||
		versionable instanceof cbit.vcell.mathmodel.MathModelMetaData ||
		versionable instanceof cbit.vcell.geometry.Geometry){
		Statement stmt = null;
		try{
			stmt = con.createStatement();
			String sql =
				"SELECT "+VersionTable.privacy_ColumnName+
				" FROM "+VersionTable.getVersionTable(versionable).getTableName()+
				" WHERE "+VersionTable.id_ColumnName+" = " +versionable.getVersion().getVersionKey();
			ResultSet rset = stmt.executeQuery(sql);
			BigDecimal dbgrpid = null;
			if(rset.next()){
				dbgrpid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
			}
			rset.close();
			if(!dbgrpid.equals(versionable.getVersion().getGroupAccess().getGroupid())){
				accessInfo = getGroupAccessFromGroupID(con,dbgrpid);
			}
		}catch(Throwable e){
			//Don't fail, just keep the permission versionable came in with
		}finally{
			if(stmt != null){
				stmt.close();
			}
		}
	}

	
	KeyValue versionKey = getNewKey(con);
	java.util.Date date = getNewDate(con);

	//
	// always use the previous BranchPointReference unless branching
	//
	KeyValue branchPointRefKey = versionable.getVersion().getBranchPointRefKey();

	//Check for Archive and Publish not needed in update because versionflag is always forced to Current
	VersionFlag versionFlag = null;
	//if (bVersion){
		//versionFlag = VersionFlag.Archived;
	//}else{
		versionFlag = VersionFlag.Current;
	//}

	String versionName = versionable.getVersion().getName();
	java.math.BigDecimal branchID = versionable.getVersion().getBranchID();
	String annot = versionable.getDescription();

	//
	//Insert Software Version
	//
	insertSoftwareVersion(con,versionKey);

	//
	// this is overridden in SimulationDbDriver to help form a SimulationVersion object with or without the ParentSimulationReference.
	//
	return new Version(versionKey,versionName,owner,accessInfo,branchPointRefKey,branchID,date,versionFlag,annot);
}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 2:29:38 PM)
 * @return java.lang.String
 * @param size int
 */
public static String varchar2_CLOB_get(ResultSet rset,Field varchar2Field,Field clobField) throws SQLException,DataAccessException{

	String results;
	
	String temp = (String) DbDriver.getLOB(rset,clobField.getUnqualifiedColName());
	if(rset.wasNull() || temp == null || temp.length() == 0){
		temp = rset.getString(varchar2Field.getUnqualifiedColName());
		if(rset.wasNull() || temp == null || temp.length() == 0){
			results = null;
		}else{
			//Strings have to be SQL unmangled
			results = cbit.util.TokenMangler.getSQLRestoredString(temp);
		}
	}else{
		//CLOBs do not have to be SQL unmangled
		results = temp;
	}

	return results;

}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 2:29:38 PM)
 * @return java.lang.String
 * @param size int
 */
public static boolean varchar2_CLOB_is_Varchar2_OK(String data) {

	return (cbit.util.TokenMangler.getSQLEscapedString(data).length() <= ORACLE_VARCHAR2_SIZE_LIMIT);
	
}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 2:29:38 PM)
 * @return java.lang.String
 * @param size int
 */
public static void varchar2_CLOB_update(
    Connection con,
    String sql,//marked sql
    String data,
    Table targetTable,//Where data gets stored
    KeyValue targetID,//for clob if necessary
    Field targetCLOBField,
    Field targetVarchar2Field)
		throws SQLException,DataAccessException{

    int marker_index;
    if ((marker_index = sql.indexOf(INSERT_CLOB_HERE)) != -1) {
	    //Store in CLOB
        StringBuffer sb = new StringBuffer(sql);
        sb.replace(
            marker_index,
            marker_index + INSERT_CLOB_HERE.length(),
            "EMPTY_CLOB()");
        updateCleanSQL(con, sb.toString());
        updateCleanLOB(
            con,
            targetTable.id.getUnqualifiedColName(),
            targetID,
            targetTable.tableName,
            targetCLOBField.getUnqualifiedColName(),
            data);
    } else if ((marker_index = sql.indexOf(INSERT_VARCHAR2_HERE)) != -1){
	    //Store in VARCHAR2
        StringBuffer sb = new StringBuffer(sql);
        sb.replace(
            marker_index,
            marker_index + INSERT_VARCHAR2_HERE.length(),
            "'" + cbit.util.TokenMangler.getSQLEscapedString(data) + "'");
        updateCleanSQL(con, sb.toString());
    }else{
	    throw new RuntimeException("Expected charchar2_CLOB Marker Not Found in sql");
    }
}
}