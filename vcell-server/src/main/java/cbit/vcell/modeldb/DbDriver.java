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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.pub.Publication;
import org.vcell.util.BeanUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.Preference;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.CurateSpec;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.GroupAccess;
import org.vcell.util.document.GroupAccessAll;
import org.vcell.util.document.GroupAccessNone;
import org.vcell.util.document.GroupAccessSome;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.PublicationInfo;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.document.VCInfoContainer;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;
import org.vcell.util.document.VersionInfo;
import org.vcell.util.document.Versionable;
import org.vcell.util.document.VersionableFamily;
import org.vcell.util.document.VersionableRelationship;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;
import org.vcell.util.document.User.SPECIALS;

import cbit.image.VCImageInfo;
import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.InsertHashtable;
import cbit.sql.RecordChangedException;
import cbit.sql.StarField;
import cbit.sql.Table;
import cbit.vcell.biomodel.BioModelMetaData;
import cbit.vcell.field.FieldDataDBOperationResults;
import cbit.vcell.field.FieldDataDBOperationSpec;
import cbit.vcell.field.db.FieldDataDBOperationDriver;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModelMetaData;
import cbit.vcell.modeldb.MathVerifier.LoadModelsStatTable;
import cbit.vcell.numericstest.AddTestCasesOP;
import cbit.vcell.numericstest.AddTestCasesOPBioModel;
import cbit.vcell.numericstest.AddTestCasesOPMathModel;
import cbit.vcell.numericstest.AddTestCriteriaOPBioModel;
import cbit.vcell.numericstest.AddTestCriteriaOPMathModel;
import cbit.vcell.numericstest.AddTestResultsOP;
import cbit.vcell.numericstest.AddTestSuiteOP;
import cbit.vcell.numericstest.ChangeTestCriteriaErrorLimitOP;
import cbit.vcell.numericstest.EditTestCasesOP;
import cbit.vcell.numericstest.EditTestCriteriaOPBioModel;
import cbit.vcell.numericstest.EditTestCriteriaOPMathModel;
import cbit.vcell.numericstest.EditTestCriteriaOPReportStatus;
import cbit.vcell.numericstest.EditTestSuiteOP;
import cbit.vcell.numericstest.LoadTestInfoOP;
import cbit.vcell.numericstest.LoadTestInfoOP.LoadTestOpFlag;
import cbit.vcell.numericstest.LoadTestInfoOpResults;
import cbit.vcell.numericstest.LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp;
import cbit.vcell.numericstest.ModelGeometryOP;
import cbit.vcell.numericstest.ModelGeometryOPResults;
import cbit.vcell.numericstest.QueryTestCriteriaCrossRefOP;
import cbit.vcell.numericstest.RemoveTestCasesOP;
import cbit.vcell.numericstest.RemoveTestCriteriaOP;
import cbit.vcell.numericstest.RemoveTestResultsOP;
import cbit.vcell.numericstest.RemoveTestSuiteOP;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCaseNewBioModel;
import cbit.vcell.numericstest.TestCaseNewMathModel;
import cbit.vcell.numericstest.TestCriteriaCrossRefOPResults;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.TestCriteriaNewBioModel;
import cbit.vcell.numericstest.TestCriteriaNewMathModel;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
import cbit.vcell.numericstest.TestSuiteOP;
import cbit.vcell.numericstest.TestSuiteOPResults;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.test.VariableComparisonSummary;

/**
 * This type was created in VisualAge.
 */
public abstract class DbDriver {
	protected static final Logger lg = LogManager.getLogger(DbDriver.class);
	//
	private static final int VARCHAR_SIZE_LIMIT = 4000;
	public static final String INSERT_VARCHAR2_HERE = "INSERT_VARCHAR2_HERE";
	public static final String INSERT_CLOB_HERE = "INSERT_CLOB_HERE";
	//

	protected final DatabaseSyntax dbSyntax;

	protected final KeyFactory keyFactory;
	
//	protected DBCacheTable dbc = null;
	private static Hashtable<BigDecimal, GroupAccess> groupAccessHash = new Hashtable<BigDecimal, GroupAccess>();

	//private static Hashtable testSuiteHash = new Hashtable();

/**
 * DBService constructor comment.
 */
public DbDriver(DatabaseSyntax dbSyntax, KeyFactory keyFactory) {
	this.dbSyntax = dbSyntax;
	this.keyFactory = keyFactory;
}

public static void publishDirectly(Connection con,KeyValue[] publishTheseBiomodels,KeyValue[] publishTheseMathmodels,User user,DatabaseSyntax databaseSyntax) throws SQLException,DataAccessException{
	TreeMap<SPECIALS, TreeMap<User, String>> specialUsers = getSpecialUsers(user,con,databaseSyntax);
	TreeMap<User, String> usersAllowedToModifyPublications = specialUsers.get(User.SPECIALS.publication);
	if(usersAllowedToModifyPublications == null || !usersAllowedToModifyPublications.containsKey(user)) {
		throw new DataAccessException("User "+user.getName()+" does not have permission to publish directly");
	}
	if(publishTheseBiomodels != null && publishTheseBiomodels.length>0) {
		String sql = "UPDATE "+BioModelTable.table.getTableName()+
			" SET "+BioModelTable.table.privacy.getUnqualifiedColName()+"="+GroupAccess.GROUPACCESS_ALL.toString()+
			" WHERE "+BioModelTable.table.id.getUnqualifiedColName()+" IN ("+StringUtils.join(publishTheseBiomodels, ',')+")";
		updateCleanSQL(con, sql);
		sql = "UPDATE "+BioModelTable.table.getTableName()+" SET "+BioModelTable.table.versionFlag.getUnqualifiedColName()+"="+VersionFlag.Published.getIntValue()+
			" WHERE "+BioModelTable.table.id.getUnqualifiedColName()+" IN ("+StringUtils.join(publishTheseBiomodels, ',')+")";
		updateCleanSQL(con, sql);
	}
	if(publishTheseMathmodels != null && publishTheseMathmodels.length>0) {
		String sql = "UPDATE "+MathModelTable.table.getTableName()+
			" SET "+MathModelTable.table.privacy.getUnqualifiedColName()+"="+GroupAccess.GROUPACCESS_ALL.toString()+
			" WHERE "+MathModelTable.table.id.getUnqualifiedColName()+" IN ("+StringUtils.join(publishTheseBiomodels, ',')+")";
		updateCleanSQL(con, sql);
		sql = "UPDATE "+MathModelTable.table.getTableName()+" SET "+MathModelTable.table.versionFlag.getUnqualifiedColName()+"="+VersionFlag.Published.getIntValue()+
			" WHERE "+MathModelTable.table.id.getUnqualifiedColName()+" IN ("+StringUtils.join(publishTheseBiomodels, ',')+")";
		updateCleanSQL(con, sql);
	}
}

public static KeyValue savePublicationRep(Connection con,PublicationRep publicationRep,User user,DatabaseSyntax databaseSyntax) throws SQLException, DataAccessException{
	
	String pubID = null;
	TreeMap<SPECIALS, TreeMap<User, String>> specialUsers = getSpecialUsers(user,con,databaseSyntax);
	TreeMap<User, String> usersAllowedToModifyPublications = specialUsers.get(User.SPECIALS.publication);
	if(usersAllowedToModifyPublications == null || !usersAllowedToModifyPublications.containsKey(user)) {
		throw new DataAccessException("User "+user.getName()+" does not have permission to edit publications");
	}
	final String YMD_FORMAT_STRING = "yyyy-MM-dd";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YMD_FORMAT_STRING);
	String sql = null;
	Statement stmt = null;
	try {
		if(publicationRep.getPubKey() == null) {
			stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery("select newseq.nextval from dual");
			rset.next();
			pubID = rset.getString(1);
			rset.close();
			stmt.close();
			sql = "INSERT INTO "+PublicationTable.table.getTableName()+" VALUES (" +
			pubID.toString()+","+
			(publicationRep.getTitle()==null?"NULL":"'"+publicationRep.getTitle()+"'")+","+
			(publicationRep.getAuthors()==null || publicationRep.getAuthors().length==0?"NULL":"'"+StringUtils.join(publicationRep.getAuthors(), ';')+"'")+","+
			(publicationRep.getYear()==null?"NULL":publicationRep.getYear())+","+
			(publicationRep.getCitation()==null?"NULL":"'"+publicationRep.getCitation()+"'")+","+
			(publicationRep.getPubmedid()==null?"NULL":"'"+publicationRep.getPubmedid()+"'")+","+
			(publicationRep.getDoi()==null?"NULL":"'"+publicationRep.getDoi()+"'")+","+
			(publicationRep.getEndnoteid()==null || publicationRep.getEndnoteid().length()==0?"NULL":publicationRep.getEndnoteid())+","+
			(publicationRep.getUrl()==null?"NULL":"'"+publicationRep.getUrl()+"'")+","+
			(publicationRep.getWittid()==null|| publicationRep.getWittid().length()==0?"NULL":publicationRep.getWittid())+","+
			(publicationRep.getDate()==null?"NULL":" TO_DATE('" + simpleDateFormat.format(publicationRep.getDate()) + "','"+YMD_FORMAT_STRING+"') ")+
			")";		
		}else {
			pubID = publicationRep.getPubKey().toString();
			sql = "UPDATE "+PublicationTable.table.getTableName()+" SET "+
			PublicationTable.table.title.getUnqualifiedColName()+"="+(publicationRep.getTitle()==null?"NULL":"'"+publicationRep.getTitle()+"'")+","+
			PublicationTable.table.authors.getUnqualifiedColName()+"="+(publicationRep.getAuthors()==null || publicationRep.getAuthors().length==0?"NULL":"'"+StringUtils.join(publicationRep.getAuthors(), ';')+"'")+","+
			PublicationTable.table.year.getUnqualifiedColName()+"="+(publicationRep.getYear()==null?"NULL":publicationRep.getYear())+","+
			PublicationTable.table.citation.getUnqualifiedColName()+"="+(publicationRep.getCitation()==null?"NULL":"'"+publicationRep.getCitation()+"'")+","+
			PublicationTable.table.pubmedid.getUnqualifiedColName()+"="+(publicationRep.getPubmedid()==null?"NULL":"'"+publicationRep.getPubmedid()+"'")+","+
			PublicationTable.table.doi.getUnqualifiedColName()+"="+(publicationRep.getDoi()==null?"NULL":"'"+publicationRep.getDoi()+"'")+","+
			PublicationTable.table.endnoteid.getUnqualifiedColName()+"="+(publicationRep.getEndnoteid()==null || publicationRep.getEndnoteid().length()==0?"NULL":publicationRep.getEndnoteid())+","+
			PublicationTable.table.url.getUnqualifiedColName()+"="+(publicationRep.getUrl()==null?"NULL":"'"+publicationRep.getUrl()+"'")+","+
			PublicationTable.table.wittid.getUnqualifiedColName()+"="+(publicationRep.getWittid()==null|| publicationRep.getWittid().length()==0?"NULL":publicationRep.getWittid())+","+
			PublicationTable.table.pubdate.getUnqualifiedColName()+"="+(publicationRep.getDate()==null?"NULL":" TO_DATE('" + simpleDateFormat.format(publicationRep.getDate()) + "','"+YMD_FORMAT_STRING+"') ")+
			" WHERE "+PublicationTable.table.id.getUnqualifiedColName()+"="+ pubID.toString();		
		}
		updateCleanSQL(con, sql);
		//Remove all current links to this publication
		updateCleanSQL(con, "DELETE FROM "+PublicationModelLinkTable.table.getTableName()+" WHERE "+PublicationModelLinkTable.table.pubRef.getUnqualifiedColName()+"="+pubID.toString());
		//Add link table entry
			if(publicationRep.getBiomodelReferenceReps() != null && publicationRep.getBiomodelReferenceReps().length > 0) {
			for(BioModelReferenceRep bioModelReferenceRep:publicationRep.getBiomodelReferenceReps()) {
				try {
					updateCleanSQL(con, "INSERT INTO "+PublicationModelLinkTable.table.getTableName()+" VALUES (newseq.nextval,"+pubID.toString()+","+bioModelReferenceRep.getBmKey().toString()+",NULL)");
				} catch (Exception e) {
					e.printStackTrace();
					throw new SQLException("Error inserting biomodelKey="+bioModelReferenceRep.getBmKey().toString()+" link to publicationID="+pubID.toString()+"\n"+e.getMessage(),e);
				}
			}
		}
		if(publicationRep.getMathmodelReferenceReps() != null && publicationRep.getMathmodelReferenceReps().length > 0) {
			for(MathModelReferenceRep mathModelReferenceRep:publicationRep.getMathmodelReferenceReps()) {
				try {
					updateCleanSQL(con, "INSERT INTO "+PublicationModelLinkTable.table.getTableName()+" VALUES (newseq.nextval,"+pubID.toString()+",NULL,"+mathModelReferenceRep.getMmKey().toString()+")");
				} catch (Exception e) {
					e.printStackTrace();
					throw new SQLException("Error inserting mathmodelKey="+mathModelReferenceRep.getMmKey().toString()+" link to publicationID="+pubID.toString()+"\n"+e.getMessage(),e);
				}
			}
		}
		return new KeyValue(pubID.toString());
	}finally {
		if(stmt != null) {try{stmt.close();}catch(Exception e) {e.printStackTrace();}}
	}
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
public static VCDocumentInfo curate(CurateSpec curateSpec,Connection con,User user,DatabaseSyntax dbSyntax) throws DataAccessException,SQLException{

	VersionableType vType = null;
	if(curateSpec.getVCDocumentInfo() instanceof BioModelInfo){
		vType = VersionableType.BioModelMetaData;
	}else if(curateSpec.getVCDocumentInfo() instanceof MathModelInfo){
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

	//Clear XML
	if(vType.equals(VersionableType.BioModelMetaData)){
		updateCleanSQL(con,"DELETE FROM "+BioModelXMLTable.table.getTableName()+" WHERE "+BioModelXMLTable.table.bioModelRef.getQualifiedColName()+" = "+vKey.toString());
	}else if(vType.equals(VersionableType.MathModelMetaData)){
		updateCleanSQL(con,"DELETE FROM "+MathModelXMLTable.table.getTableName()+" WHERE "+MathModelXMLTable.table.mathModelRef.getQualifiedColName()+" = "+vKey.toString());
	}

	
	VCDocumentInfo dbVCDocumentInfo = (VCDocumentInfo)getVersionableInfos(con,user,vType,false,vKey,false,dbSyntax).elementAt(0);
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
public static FieldDataDBOperationResults fieldDataDBOperation(Connection con, KeyFactory keyFactory, User user,
		FieldDataDBOperationSpec fieldDataDBOperationSpec) throws SQLException, DataAccessException {
	
	return FieldDataDBOperationDriver.fieldDataDBOperation(con, keyFactory, user, fieldDataDBOperationSpec);
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
	
	Vector<VersionInfo> versionInfoVector = getVersionableInfos(con,user,vType,true,versionKey,true,dbSyntax);
	if(versionInfoVector.size() == 0){
		throw new ObjectNotFoundException("DbDriver:deleteVersionableInit "+vType.getTypeName()+"("+versionKey+") not found for user="+user);
	}
	else if (versionInfoVector.size() > 1){
		throw new DataAccessException("DbDriver:deleteVersionableInit "+vType.getTypeName()+"("+versionKey+") found more than one entry  DB ERROR,BAD!!!!!MUST CHECK");
	}
	VersionInfo versionInfo = versionInfoVector.firstElement();
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
    Field refFromField,
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
	Vector<VersionRef> possibleRefs = VersionTable.getChildVersionableTypes(vtv.getVType());
	Enumeration<VersionRef> enum1 = possibleRefs.elements();
	UserTable userTable = UserTable.table;
	while (enum1.hasMoreElements()) {
		VersionRef vr = enum1.nextElement();
		
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
		Vector<VersionableTypeVersion> allChildrenVTV = new Vector<VersionableTypeVersion>();
		try { //Get KeyValues from statement and put into Vector, so we can close statement(good idea because we are recursive)
			java.sql.ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				try{
					BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
					Version version = VersionTable.getVersion(rset,getGroupAccessFromGroupID(con,groupid));
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
			VersionableTypeVersion childVTV = allChildrenVTV.elementAt(c);
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
	Vector<VersionRef> possibleRefs = VersionTable.getReferencingVersionableTypes(vtv.getVType());
	Enumeration<VersionRef> enum1 = possibleRefs.elements();
	UserTable userTable = UserTable.table;
	while (enum1.hasMoreElements()) {
		VersionRef vr = enum1.nextElement();
		
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
		Vector<VersionableTypeVersion> allReferencingVTV = new Vector<VersionableTypeVersion>();
		try { //Get KeyValues from statement and put into Vector, so we can close statement(good idea because we are recursive)
			java.sql.ResultSet rset = stmt.executeQuery(sql);
			while (rset.next()) {
				try{
					BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
					Version version = VersionTable.getVersion(rset,getGroupAccessFromGroupID(con,groupid));
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
			VersionableTypeVersion referencingVTV = allReferencingVTV.elementAt(c);
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
	Vector<KeyValue> keyList = new Vector<KeyValue>();
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
	return (KeyValue[])BeanUtils.getArray(keyList,KeyValue.class);
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
	Vector<KeyValue> externalRefsV = new Vector<KeyValue>();
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

	GroupAccess groupAccess = groupAccessHash.get(groupid);
	if(groupAccess == null){
		if(groupid.equals(GroupAccess.GROUPACCESS_ALL)){
			groupAccess = new GroupAccessAll();
		}else if(groupid.equals(GroupAccess.GROUPACCESS_NONE)){
			groupAccess = new GroupAccessNone();
		}
		else{
			GroupTable groupTable = GroupTable.table;
			StarField groupAllFields = new StarField(groupTable);
			UserTable userTable = UserTable.table;
			// getting all group access objects to avoid multiple database calls.
			// store group access objects in static hash table.
			String sql =
					"SELECT "+	groupAllFields.getQualifiedColName()+","+
								userTable.userid.getQualifiedColName()+
					" FROM "+	groupTable.getTableName()+","+
								userTable.getTableName()+
					" WHERE "/*+	groupTable.groupid.getQualifiedColName() + " = " + groupid.toString() +	" AND "*/
							+	userTable.id.getQualifiedColName()+ " = "+groupTable.userRef.getQualifiedColName()
							+ " ORDER BY " + groupTable.groupid.getQualifiedColName();
			//
			if (lg.isTraceEnabled()) {
				lg.trace("getGroupAccessFromGroupID(), sql = " + sql);
			}
			java.sql.Statement stmt = con.createStatement();
			BigDecimal currGroupId = null;
			BigDecimal rowGroupId = null;
			ArrayList<User> groupMembers = new ArrayList<User>();
			ArrayList<Boolean> hiddenFromOwner= new ArrayList<Boolean>();
			BigDecimal groupMemberHash = null;
			try {
				java.sql.ResultSet rset = stmt.executeQuery(sql);
				while (true) {
					boolean hasNext = rset.next();
					if (hasNext) {
						rowGroupId = rset.getBigDecimal(groupTable.groupid.toString());
						if(rowGroupId.equals(GroupAccess.GROUPACCESS_ALL) 
								|| rowGroupId.equals(GroupAccess.GROUPACCESS_NONE)){
							continue;
						}
						if (currGroupId == null) {
							currGroupId = rowGroupId;
						}
					}
					if (!hasNext || !currGroupId.equals(rowGroupId)) {
						boolean[] hiddenArr = new boolean[hiddenFromOwner.size()];
						for(int i = 0;i<hiddenArr.length;i+=1){
							hiddenArr[i] = ((hiddenFromOwner.get(i))).booleanValue();
						}
						User[] groupMembersArr = new User[groupMembers.size()];
						groupMembers.toArray(groupMembersArr);
						groupAccess = new GroupAccessSome(currGroupId,groupMemberHash,groupMembersArr,hiddenArr);
						groupAccessHash.put(currGroupId,groupAccess);
					
						groupMembers.clear();
						hiddenFromOwner.clear();
					}
					
					if (hasNext) {					
						currGroupId = rowGroupId;				
						groupMemberHash = rset.getBigDecimal(groupTable.groupMemberHash.toString());
						User user = new User(rset.getString(userTable.userid.toString()),new KeyValue(rset.getBigDecimal(groupTable.userRef.toString())));
						groupMembers.add(user);
						boolean bHidden = (rset.getInt(groupTable.isHiddenFromOwner.toString())) == 1;
						hiddenFromOwner.add(new Boolean(bHidden));
					} else {
						break;
					}
				}
			} finally {
				stmt.close();
			}
			groupAccess = groupAccessHash.get(groupid);
		}
	}	
	return groupAccess; 
}


/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 3:50:56 PM)
 * @return java.lang.Object
 * @param rset oracle.jdbc.OracleResultSet
 * @param columnName java.lang.String
 */
protected final static Object getLOB(ResultSet rset,Field column,DatabaseSyntax dbSyntax) throws DataAccessException, SQLException {
	
	switch (dbSyntax){
	case ORACLE:{
		//This method returns (byte[] for BLOB) or a (String for CLOB)
		//
		//try to get the LOB
		Object lob_object = rset.getObject(column.getUnqualifiedColName());
		if(rset.wasNull()){
			//return null if the column actually contains a null
			return null;
		}
		//If its a BLOB return a byte[]
		if (lob_object instanceof java.sql.Blob) {
			java.sql.Blob blob_object = (java.sql.Blob) lob_object;
			try{
				return blob_object.getBytes((long) 1, (int) blob_object.length());
			}catch(Exception e){
				throw new DataAccessException(e.toString());
			}
		//If its a CLOB return a String
		} else
			if (lob_object instanceof java.sql.Clob) {
				java.sql.Clob clob_object = (java.sql.Clob) lob_object;
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
			"ResultSet column=" + column.getUnqualifiedColName() + " was not a BLOB or CLOB");
	}
	case POSTGRES:{
		if (column.getSqlDataType()==SQLDataType.blob_bytea){
			//
			// binary data (bytea)
			//
			byte[] bytes = rset.getBytes(column.getUnqualifiedColName());
			if (rset.wasNull()){
				return null;
			}else{
				return bytes;
			}
		}else if (column.getSqlDataType()==SQLDataType.clob_text){
			//
			// text data
			//
			String result = rset.getString(column.getUnqualifiedColName());
			if (rset.wasNull()){
				return null;
			}else{
				return result;
			}
		}else{
			throw new DataAccessException("unexpected SQLDataType "+column.getSqlDataType());
		}
	}
	default:{
		throw new DataAccessException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}

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
private static java.math.BigDecimal getNewGroupID(Connection con, KeyFactory keyFactory) throws java.sql.SQLException {
	return keyFactory.getUniqueBigDecimal(con);
}


/**
 * This method was created in VisualAge.
 * @return int
 * @param user java.lang.String
 * @param imageName java.lang.String
 */
public static Preference[] getPreferences(Connection con, User user) throws SQLException {
	
	String sql =
		"SELECT " + 
			UserPreferenceTable.table.userPrefKey.getQualifiedColName() + "," +
			UserPreferenceTable.table.userPrefValue.getQualifiedColName() +
		" FROM " + 
			UserPreferenceTable.table.getTableName() +
		" WHERE " + 
			user.getID().toString() + " = " + UserPreferenceTable.table.userRef.getQualifiedColName();
				
			
	Statement stmt = null;
	Preference[] preferences = null;
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

public static TreeMap<User.SPECIALS,TreeMap<User,String>>  getSpecialUsers(User user,Connection con, DatabaseSyntax dbSyntax) throws DataAccessException, java.sql.SQLException{
	TreeMap<User.SPECIALS,TreeMap<User,String>> result = new TreeMap<>();
	String sql = "SELECT "+
			SpecialUsersTable.table.userRef.getQualifiedColName()+" userref,"+
			SpecialUsersTable.table.special.getQualifiedColName()+" special,"+
			SpecialUsersTable.table.userDetail.getQualifiedColName() +" userdetail,"+
			UserTable.table.userid.getQualifiedColName()+" userid"+
		" FROM "+SpecialUsersTable.table.getTableName()+","+UserTable.table.getTableName()+
		" WHERE "+SpecialUsersTable.table.userRef.getQualifiedColName()+ " = " + UserTable.table.id.getQualifiedColName();
	try (Statement stmt = con.createStatement();){
		try(ResultSet rset = stmt.executeQuery(sql);){
			while(rset.next()) {
				String specialStr = rset.getString("special");
				User.SPECIALS special = User.SPECIALS.valueOf(specialStr);
				String userdetail = rset.getString("userdetail");
				String userRef = rset.getString("userref");
				String userId = rset.getString("userid");
				if(!result.containsKey(special)) {
					result.put(special, new TreeMap<User,String>(new User.UserNameComparator()));
				}
				TreeMap<User,String> userMapDetail = result.get(special);
				userMapDetail.put(new User(userId,new KeyValue(userRef)), userdetail);
			}
		}
	}
	return result;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2003 12:54:32 PM)
 * @return cbit.vcell.modeldb.VCInfoContainer
 */
public static VCInfoContainer getVCInfoContainer(User user,Connection con, DatabaseSyntax dbSyntax) throws SQLException,DataAccessException{

	VCInfoContainer results = null;
	//
	VCImageInfo[] vcImageInfos = null;
	GeometryInfo[] geometryInfos = null;
	MathModelInfo[] mathModelInfos = null;
	BioModelInfo[] bioModelInfos = null;
	PublicationInfo[] publicationInfos = null;
	
	//
	StringBuffer sql = null;
	String special = null;
	ResultSet rset = null;
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
			sql = new StringBuffer(BioModelTable.table.getInfoSQL(user,null,(enableSpecial?special:null),dbSyntax));
			sql.insert(7,Table.SQL_GLOBAL_HINT);
			rset = stmt.executeQuery(sql.toString());
			ArrayList<BioModelInfo> tempInfos = new ArrayList<BioModelInfo>();
			Set<String> distinctV = new HashSet<String>();
			while(rset.next()){
				BioModelInfo versionInfo = (BioModelInfo)BioModelTable.table.getInfo(rset,con,dbSyntax);
				if(!distinctV.contains(versionInfo.getVersion().getVersionKey().toString())){
					tempInfos.add(versionInfo);
					distinctV.add(versionInfo.getVersion().getVersionKey().toString());
				}
			}
			rset.close();
			if(tempInfos.size() > 0){
				bioModelInfos = new BioModelInfo[tempInfos.size()];
				tempInfos.toArray(bioModelInfos);
			}
			if (lg.isInfoEnabled()) {
				lg.info("BioModelInfo Time="+(((double)System.currentTimeMillis()-beginTime)/(double)1000));
			}
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
			sql = new StringBuffer(MathModelTable.table.getInfoSQL(user,null,(enableSpecial?special:null),dbSyntax));
			sql.insert(7,Table.SQL_GLOBAL_HINT);
			rset = stmt.executeQuery(sql.toString());
			ArrayList<MathModelInfo> tempInfos = new ArrayList<MathModelInfo>();
			Set<String> distinctV = new HashSet<String>();
			while(rset.next()){
				MathModelInfo versionInfo = (MathModelInfo)MathModelTable.table.getInfo(rset,con,dbSyntax);
				if(!distinctV.contains(versionInfo.getVersion().getVersionKey().toString())){
					tempInfos.add(versionInfo);
					distinctV.add(versionInfo.getVersion().getVersionKey().toString());
				}
			}
			rset.close();
			if(tempInfos.size() > 0){
				mathModelInfos = new MathModelInfo[tempInfos.size()];
				tempInfos.toArray(mathModelInfos);
			}
			if (lg.isInfoEnabled()) {
				lg.info("MathModelInfo Time="+(((double)System.currentTimeMillis()-beginTime)/(double)1000));
			}
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
			sql = new StringBuffer(ImageTable.table.getInfoSQL(user,null,(enableSpecial?special:null),true,dbSyntax));
			sql.insert(7,Table.SQL_GLOBAL_HINT);
			rset = stmt.executeQuery(sql.toString());
			ArrayList<VCImageInfo> tempInfos = new ArrayList<VCImageInfo>();
			Set<String> distinctV = new HashSet<String>();
			while(rset.next()){
				VCImageInfo versionInfo = (VCImageInfo)ImageTable.table.getInfo(rset,con,dbSyntax);
				if(!distinctV.contains(versionInfo.getVersion().getVersionKey().toString())){
					tempInfos.add(versionInfo);
					distinctV.add(versionInfo.getVersion().getVersionKey().toString());
				}
			}
			rset.close();
			if(tempInfos.size() > 0){
				vcImageInfos = new VCImageInfo[tempInfos.size()];
				tempInfos.toArray(vcImageInfos);
			}
			if (lg.isInfoEnabled()) {
				lg.info("ImageInfo Time="+(((double)System.currentTimeMillis()-beginTime)/(double)1000));
			}
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
			sql = new StringBuffer(GeometryTable.table.getInfoSQL(user,null,(enableSpecial?special:null),true,dbSyntax));
			sql.insert(7,Table.SQL_GLOBAL_HINT+(enableDistinct?"DISTINCT ":""));
			rset = stmt.executeQuery(sql.toString());
			ArrayList<GeometryInfo> tempInfos = new ArrayList<GeometryInfo>();
			Set<String> distinctV = new HashSet<String>();
			while(rset.next()){
				GeometryInfo versionInfo = (GeometryInfo)GeometryTable.table.getInfo(rset,con);
				if(!distinctV.contains(versionInfo.getVersion().getVersionKey().toString())){
					tempInfos.add(versionInfo);
					distinctV.add(versionInfo.getVersion().getVersionKey().toString());
				}
			}
			rset.close();
			if(tempInfos.size() > 0){
				geometryInfos = new GeometryInfo[tempInfos.size()];
				tempInfos.toArray(geometryInfos);
			}
			if (lg.isInfoEnabled()) {
				lg.info("GeometryInfo Time="+(((double)System.currentTimeMillis()-beginTime)/(double)1000));
			}
		}	
		try {
			Vector<VersionInfo> bm_mm_VCDocumentInfos = new Vector<>();
			bm_mm_VCDocumentInfos.addAll(Arrays.asList((bioModelInfos!=null?bioModelInfos:new BioModelInfo[0])));
			bm_mm_VCDocumentInfos.addAll(Arrays.asList((mathModelInfos!=null?mathModelInfos:new MathModelInfo[0])));
			DbDriver.addPublicationInfos(con, stmt, bm_mm_VCDocumentInfos);
		} catch (Exception e) {
			e.printStackTrace();
			//Don't fail if something goes wrong with setting publication info
		}
	}finally{
		if(stmt != null){
			stmt.close();
		}
	}
	results = new VCInfoContainer(user, vcImageInfos, geometryInfos, mathModelInfos, bioModelInfos);

	return results;
}


public static void addPublicationInfos(Connection con,Statement stmt,Vector<VersionInfo> mm_and_bm_VersionInfos) throws SQLException,DataAccessException{
	StringBuffer publicationSB = new StringBuffer();
	TreeMap<Long, VersionInfo> mapModelIdToVersionInfo = new TreeMap<>(new Comparator<Long>() {
		@Override
		public int compare(Long o1, Long o2) {
			return  o1.compareTo(o2);
		}
	});
	for (int i = 0; i < mm_and_bm_VersionInfos.size(); i++) {
		if(mm_and_bm_VersionInfos.get(i).getVersion().getFlag().isPublished()) {
			publicationSB.append((publicationSB.length()==0?"":",")+mm_and_bm_VersionInfos.get(i).getVersion().getVersionKey().toString());
			mapModelIdToVersionInfo.put(Long.parseLong(mm_and_bm_VersionInfos.get(i).getVersion().getVersionKey().toString()), mm_and_bm_VersionInfos.get(i));
		}
	}
	if(publicationSB.length() == 0) {
		return;
	}
	//
	//PublicationInfos
	//
	final String DOCTYPE_COL="doctype";
	final String DOCID_COL="docid";
	String sql = new String(
			"select vc_publication.*,'bm' "+DOCTYPE_COL+",vc_biomodel.id "+DOCID_COL+",vc_biomodel.ownerref,vc_userinfo.userid"+
			" from vc_publication,vc_publicationmodellink,vc_biomodel,vc_userinfo" +
			" where"+
			" vc_biomodel.id=vc_publicationmodellink.biomodelref and vc_userinfo.id=vc_biomodel.ownerref"+
			" and vc_publication.id=vc_publicationmodellink.pubref and vc_publicationmodellink.pubref is not null"+
			" and vc_publicationmodellink.biomodelref is not null and vc_publicationmodellink.biomodelref in ("+publicationSB.toString()+")"+
			" UNION ALL" +
			" select vc_publication.*,'mm' "+DOCTYPE_COL+",vc_mathmodel.id "+DOCID_COL+",vc_mathmodel.ownerref,vc_userinfo.userid"+
			" from vc_publication,vc_publicationmodellink,vc_mathmodel,vc_userinfo" +
			" where"+
			" vc_mathmodel.id=vc_publicationmodellink.mathmodelref and vc_userinfo.id=vc_mathmodel.ownerref"+
			" and vc_publication.id=vc_publicationmodellink.pubref and vc_publicationmodellink.pubref is not null"+
			" and vc_publicationmodellink.mathmodelref is not null and vc_publicationmodellink.mathmodelref in ("+publicationSB.toString()+")");
	System.out.println(sql);
	ResultSet rset = null;
	try {
		rset = stmt.executeQuery(sql);
		while(rset.next()){
				Publication publication = PublicationTable.table.getInfo(rset,con);
				String docType = rset.getString(DOCTYPE_COL);
				BigDecimal modelVersionID = rset.getBigDecimal(DOCID_COL);
				KeyValue versionKey = new KeyValue(modelVersionID);
				Timestamp timestamp = rset.getTimestamp("pubdate");
				PublicationInfo publicationInfo = new PublicationInfo(versionKey, publication.title, publication.authors, publication.citation, publication.pubmedid, publication.doi, publication.url,
					(docType.equals("bm")?VCDocumentType.BIOMODEL_DOC:VCDocumentType.MATHMODEL_DOC), new User(rset.getString(UserTable.table.userid.getUnqualifiedColName()),new KeyValue(rset.getBigDecimal("ownerref").toString())),timestamp);
				if(mapModelIdToVersionInfo.containsKey(modelVersionID.longValue()) && mapModelIdToVersionInfo.get(modelVersionID.longValue()) instanceof VCDocumentInfo) {
					((VCDocumentInfo)mapModelIdToVersionInfo.get(modelVersionID.longValue())).addPublicationInfo(publicationInfo);				
				}
				//publicationInfoArr.add(publicationInfo);
		}
	} catch (Exception e) {
		e.printStackTrace();
		throw new DataAccessException(DbDriver.class.getName()+".getPublicationInfo(...) Error -"+e.getMessage(), e);
	}finally {
		if(rset != null) {rset.close();}
	}

//	return publicationInfoArr.toArray(new PublicationInfo[0]);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param user cbit.vcell.server.User
 * @param vType int
 */
public static Vector<VersionInfo> getVersionableInfos(Connection con,User user, VersionableType vType, boolean bAll,KeyValue versionKey,boolean bCheckPermission, DatabaseSyntax dbSyntax) 
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
		sql = ((BioModelTable)vTable).getInfoSQL(user,conditions.toString(),special,dbSyntax);
	}else if (vType.equals(VersionableType.MathModelMetaData)){
		sql = ((MathModelTable)vTable).getInfoSQL(user,conditions.toString(),special,dbSyntax);
	}else if (vType.equals(VersionableType.Simulation)){
		sql = ((SimulationTable)vTable).getInfoSQL(user,conditions.toString(),special,dbSyntax);
	}else if (vType.equals(VersionableType.Geometry)){
		sql = ((GeometryTable)vTable).getInfoSQL(user,conditions.toString(),special,bCheckPermission,dbSyntax);
	}else if (vType.equals(VersionableType.VCImage)){
		sql = ((ImageTable)vTable).getInfoSQL(user,conditions.toString(),special,bCheckPermission,dbSyntax);
	}else if (vType.equals(VersionableType.Model)){
		sql = ((ModelTable)vTable).getInfoSQL(user,conditions.toString(),special,dbSyntax);
	}else if (vType.equals(VersionableType.SimulationContext)){
		sql = ((SimContextTable)vTable).getInfoSQL(user,conditions.toString(),special,dbSyntax);
	}else if (vType.equals(VersionableType.MathDescription)){
		sql = ((MathDescTable)vTable).getInfoSQL(user,conditions.toString(),special,dbSyntax);
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
	Vector<VersionInfo> vInfoList = new Vector<VersionInfo>();
	//Connection con = conFact.getConnection();
	Statement stmt = con.createStatement();
	try {
		ResultSet rset = stmt.executeQuery(sql);
		while (rset.next()) {
			if (vType.equals(VersionableType.BioModelMetaData)){
				vInfo = ((BioModelTable)vTable).getInfo(rset,con,dbSyntax);
			}else if (vType.equals(VersionableType.MathModelMetaData)){
				vInfo = ((MathModelTable)vTable).getInfo(rset,con,dbSyntax);
			}else if (vType.equals(VersionableType.Simulation)){
				vInfo = ((SimulationTable)vTable).getInfo(rset,con);
			}else if (vType.equals(VersionableType.Geometry)){
				vInfo = ((GeometryTable)vTable).getInfo(rset,con);
			}else if (vType.equals(VersionableType.VCImage)){
				vInfo = ((ImageTable)vTable).getInfo(rset,con,dbSyntax);
			}else if (vType.equals(VersionableType.Model)){
				vInfo = ((ModelTable)vTable).getInfo(rset,con);
			}else if (vType.equals(VersionableType.SimulationContext)){
				vInfo = ((SimContextTable)vTable).getInfo(rset,con);
			}else if (vType.equals(VersionableType.MathDescription)){
				vInfo = ((MathDescTable)vTable).getInfo(rset,con);
			}else{
				throw new RuntimeException("VersionInfo not availlable for type '"+vType.getTypeName()+"'");
			}
			//
			// only add version info record to list if not a duplicate,
			// this occurs because the DatabasePolicySQL.enforceOwnershipSelect() can get the same record several ways.
			//
			VersionInfo previousVInfo = (vInfoList.size()>0)?vInfoList.lastElement():null;
			if (previousVInfo==null || !previousVInfo.getVersion().getVersionKey().compareEqual(vInfo.getVersion().getVersionKey())){
				vInfoList.addElement(vInfo);
			}
		}
		try {
			DbDriver.addPublicationInfos(con, stmt, vInfoList);
		} catch (Exception e) {
			e.printStackTrace();
			//Don't fail if something goes wring with setting publication info
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
	Field xmlCol = null;
	if (vType.equals(VersionableType.BioModelMetaData)){
		xmlTableName = BioModelXMLTable.table.getTableName();
		versionableRefColName = BioModelXMLTable.table.bioModelRef.toString();
		xmlCol = BioModelXMLTable.table.bmXML;
	}else if(vType.equals(VersionableType.MathModelMetaData)){
		xmlTableName = MathModelXMLTable.table.getTableName();
		versionableRefColName = MathModelXMLTable.table.mathModelRef.toString();
		xmlCol = MathModelXMLTable.table.mmXML;
	}else{
		throw new IllegalArgumentException("vType " + vType + " not supported by " + this.getClass());
	}
	//
	Statement s = null;
	try {
		s = con.createStatement();
		String sql = "SELECT " + xmlCol.getUnqualifiedColName() + " FROM " + xmlTableName +
					" WHERE " + versionableRefColName + " = " + vKey;
		//oracle.jdbc.OracleResultSet rset = (oracle.jdbc.OracleResultSet)s.executeQuery(sql);
		ResultSet rset = s.executeQuery(sql);
		if(rset.next()){
			return (String)getLOB(rset,xmlCol,dbSyntax);
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
			if (rset.next()) {
				BigDecimal groupid = rset.getBigDecimal(VersionTable.privacy_ColumnName);
				version = VersionTable.getVersion(rset,getGroupAccessFromGroupID(con,groupid));
			} else {
				throw new ObjectNotFoundException("Failed to find " + vType.getTypeName()+" (Key=" + keyValue + ")");
			}
		} catch(ObjectNotFoundException e){
			lg.error("objectNotFound: "+e.getMessage(),e);
			throw e;
		} catch(Exception e){
			lg.error("failed to get version: "+e.getMessage(),e);
			throw new DataAccessException("Failed to find " + vType.getTypeName()+" (Key=" + keyValue 
					+ "). \nError: " + e.getMessage());
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
public static void groupAddUser(Connection con, KeyFactory keyFactory, User owner,
										VersionableType vType, KeyValue vKey,
										String userAddToGroupString,boolean isHiddenFromOwner,DatabaseSyntax dbSyntax) 
							throws SQLException,ObjectNotFoundException, DataAccessException {


	User userAddToGroup = getUserFromUserid(con,userAddToGroupString);
	if(userAddToGroup == null){
		throw new IllegalArgumentException("User name "+userAddToGroupString+" not found");
	}
	//
	if ((con == null)||(vType == null)||(owner == null) || (vKey == null)||(userAddToGroup==null)) {
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
	if (lg.isTraceEnabled()) lg.trace("DbDriver.groupAddUser(user=" + owner + ", type =" + vType + ", key=" + vKey + ")");
	
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
		updatedGroupID = getNewGroupID(con,keyFactory);
		int groupMemberCount = 1;
		// Get all the members of the currentVersion Group or skip if currentVersion group is GroupAccessNone
		// Don't worry about GroupAccessAll, we couldn't have gotten this far
		//
		// Add new User
		//
		sql = "INSERT INTO "+GroupTable.table.getTableName()+
				" VALUES ( "+
				keyFactory.getNewKey(con).toString()+","+
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
						keyFactory.getNewKey(con).toString()+","+
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
						keyFactory.getNewKey(con).toString()+","+
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
		Vector<VersionInfo> versionInfoList = getVersionableInfos(con,owner,vType,false,vKey,true,dbSyntax);
		if (versionInfoList.size()==0){
			throw new DataAccessException("Add User "+userAddToGroup+" Permission to access failed, "+vType.getTypeName()+"("+vKey+") record not found");
		}else{
			throw new DataAccessException("Add User "+userAddToGroup+" Permission to access failed "+vType.getTypeName()+"("+vKey+")");
		}
	}
	
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param user cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public static void groupRemoveUser(Connection con,KeyFactory keyFactory, User owner,
										VersionableType vType, KeyValue vKey,
										String userRemoveFromGroupString,boolean isHiddenFromOwner,DatabaseSyntax dbSyntax) 
							throws SQLException,ObjectNotFoundException, DataAccessException {


	User userRemoveFromGroup = getUserFromUserid(con,userRemoveFromGroupString);
	if(userRemoveFromGroup == null){
		throw new IllegalArgumentException("User name "+userRemoveFromGroupString+" not found");
	}
	//
	if ((con == null)||(vType == null)||(owner == null) || (vKey == null)||(userRemoveFromGroup==null)) {
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
	if (lg.isTraceEnabled()) lg.trace("DbDriver.groupAccessRemoveUser(user=" + owner + ", type =" + vType + ", key=" + vKey + ")");
	
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
		updatedGroupID = getNewGroupID(con,keyFactory);
		//
		// Re-Add Normal users not removed
		//
		User[] normalUsers = currentGroup.getNormalGroupMembers();
		for(int i = 0;normalUsers!=null && i<normalUsers.length;i+= 1){
			if(!(normalUsers[i].compareEqual(userRemoveFromGroup)) || isHiddenFromOwner){
				String userRef = normalUsers[i].getID().toString();
				sql = "INSERT INTO "+GroupTable.table.getTableName()+
						" VALUES ( "+
						keyFactory.getNewKey(con).toString()+","+
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
						keyFactory.getNewKey(con).toString()+","+
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
		Vector<VersionInfo> versionInfoList = getVersionableInfos(con,owner,vType,false,vKey,true,dbSyntax);
		if (versionInfoList.size()==0){
			throw new DataAccessException("Remove User "+userRemoveFromGroup+" Permission to access failed, "+vType.getTypeName()+"("+vKey+") record not found");
		}else{
			throw new DataAccessException("Remove User "+userRemoveFromGroup+" Permission to access failed "+vType.getTypeName()+"("+vKey+")");
		}
	}
	
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param owner cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public static void groupSetPrivate(Connection con,User owner, 
										VersionableType vType, KeyValue vKey,DatabaseSyntax dbSyntax) 
							throws SQLException,ObjectNotFoundException, DataAccessException/*, DependencyException*/ {


	if ((con == null)||(vType == null)||(owner == null) || (vKey == null)) {
		throw new IllegalArgumentException("Improper parameters for groupAccessSetPrivate");
	}
	//
	Version currentVersion = permissionInit(con,vType,vKey,owner);

	if (lg.isTraceEnabled()) lg.trace("DbDriver.groupAccessSetPrivate(owner=" + owner + ", type =" + vType.getTypeName() + ", key=" + vKey + ")");

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
		Vector<VersionInfo> versionInfoList = getVersionableInfos(con,owner,vType,false,vKey,true,dbSyntax);
		if (versionInfoList.size()==0){
			throw new DataAccessException("groupSetPrivate failed "+vType.getTypeName()+"("+vKey+") record not found");
		}else{
			throw new DataAccessException("groupSetPrivate failed "+vType.getTypeName()+"("+vKey+")");
		}
	}
	
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.Versionable
 * @param owner cbit.vcell.server.User
 * @param versionable cbit.sql.Versionable
 */
public static void groupSetPublic(Connection con,User owner, 
										VersionableType vType, KeyValue vKey, DatabaseSyntax dbSyntax) 
							throws SQLException,ObjectNotFoundException, DataAccessException {


	if ((con == null)||(vType == null)||(owner == null) || (vKey == null)) {
		throw new IllegalArgumentException("Improper parameters for groupAccessSetPublic");
	}
	//
	Version currentVersion = permissionInit(con,vType,vKey,owner);

	if (lg.isTraceEnabled()) lg.trace("DbDriver.groupSetPublic(owner=" + owner + ", type =" + vType + ", key=" + vKey + ")");

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
		Vector<VersionInfo> versionInfoList = getVersionableInfos(con,owner,vType,false,vKey,true,dbSyntax);
		if (versionInfoList.size()==0){
			throw new DataAccessException("groupSetPublic failed "+vType.getTypeName()+"("+vKey+") record not found");
		}else{
			throw new DataAccessException("groupSetPublic failed "+vType.getTypeName()+"("+vKey+")");
		}
	}
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
		" VALUES "+SoftwareVersionTable.table.getSQLValueList(versionKey,keyFactory)
		);
}


/**
 * This method was created in VisualAge.
 * @return cbit.sql.KeyValue
 * @param versionable cbit.sql.Versionable
 * @param pRef cbit.sql.KeyValue
 * @param bCommit boolean
 */
public static void insertVersionableChildSummary(Connection con,String serialDBChildSummary,VersionableType vType,KeyValue vKey, DatabaseSyntax dbSyntax)
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

	varchar2_CLOB_update(con,sql,serialDBChildSummary,csTable,vKey,csLargeCol,csSmallCol,dbSyntax);
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

	VersionableType vType = VersionTable.versionableTypeFromVersionable(versionable);
	if (vType.getIsTopLevel() && isNameUsed(con,vType,user,name)){
		throw new DataAccessException("'"+user.getName()+"' already has a "+vType.getTypeName()+" with name '"+name+"'");
	}

	User owner = user;
	//AccessInfo accessInfo = new AccessInfo(AccessInfo.PRIVATE_CODE);
	GroupAccess accessInfo = new GroupAccessNone();
	KeyValue versionKey = keyFactory.getNewKey(con);
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
public static void insertVersionableXML(Connection con,String xml,VersionableType vType,KeyValue vKey, KeyFactory keyFactory, DatabaseSyntax dbSyntax) 
					throws DataAccessException, SQLException, RecordChangedException {
	String xmlTableName = null;
	String versionableRefColName = null;
	Field xmlCol = null;
	if (vType.equals(VersionableType.BioModelMetaData)){
		xmlTableName = BioModelXMLTable.table.getTableName();
		versionableRefColName = BioModelXMLTable.table.bioModelRef.toString();
		xmlCol = BioModelXMLTable.table.bmXML;
	}else if(vType.equals(VersionableType.MathModelMetaData)){
		xmlTableName = MathModelXMLTable.table.getTableName();
		versionableRefColName = MathModelXMLTable.table.mathModelRef.toString();
		xmlCol = MathModelXMLTable.table.mmXML;
	}else{
		throw new IllegalArgumentException("vType " + vType + " not supported");
	}
	updateCleanSQL(con,"DELETE FROM "+xmlTableName +
			" WHERE " + versionableRefColName + " = " + vKey.toString());

	switch (dbSyntax){
	case ORACLE:{
		updateCleanSQL(con,"INSERT INTO "+xmlTableName+
			" VALUES ("+keyFactory.nextSEQ()+","+vKey.toString()+",EMPTY_CLOB(),current_timestamp)");
		
		updateCleanLOB(con,versionableRefColName,vKey,xmlTableName,xmlCol,xml,dbSyntax);
		break;
	}
	case POSTGRES:{
		updatePreparedCleanSQL(con,"INSERT INTO "+xmlTableName+
				" VALUES ("+keyFactory.nextSEQ()+","+vKey.toString()+",?,current_timestamp)",xml);
		break;
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
	}
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
			" AND " + vTable.name + " = " + "'"+TokenMangler.getSQLEscapedString(vName)+"'";

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
public static void replacePreferences(Connection con, User user, Preference[] preferences) throws SQLException {

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
			pstmt.setString(1,TokenMangler.getSQLEscapedString(key));
			pstmt.setString(2,TokenMangler.getSQLEscapedString(value));
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
 * @param vTable cbit.sql.VersionTable
 * @param versionKey cbit.sql.KeyValue
 */
protected void setVersioned(Connection con, User user,Versionable versionable) throws ObjectNotFoundException,SQLException,DataAccessException {
	String sql;
	if (versionable instanceof SimulationContext){
		SimulationContext sc = (SimulationContext)versionable;
		setVersioned(con,user,sc.getGeometryContext().getGeometry());
		setVersioned(con,user,sc.getGeometryContext().getModel());
		if (sc.getMathDescription()!=null){
			setVersioned(con,user,sc.getMathDescription());
		}
	}else if (versionable instanceof MathDescription){
		MathDescription math = (MathDescription)versionable;
		setVersioned(con,user,math.getGeometry());
	}else if (versionable instanceof Geometry){
		Geometry geo = (Geometry)versionable;
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
public static TestSuiteNew testSuiteGet(BigDecimal getThisTS,Connection con,User user,DatabaseSyntax dbSyntax)
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
	
	try{
		
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
			v.add(new VariableComparisonSummary(varName,minRef,maxRef,absError,relError,mse,timeAbsError,indexAbsError,timeRelError,indexRelError));
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
				reportStatus = TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT;
			}
			String reportStatusMessage = rset.getString(TFTestCriteriaTable.table.reportMessage.getUnqualifiedColName());
			if(rset.wasNull()){
				reportStatusMessage = null;
			}
			if(reportStatusMessage != null && reportStatusMessage.length() > 0){
				reportStatusMessage = TokenMangler.getSQLRestoredString(reportStatusMessage);
			}
			Vector v = (Vector)tcritH.get(tcaseRef);
			if(v == null){
				v = new Vector();
				tcritH.put(tcaseRef,v);
			}

			SimulationInfo simInfo = (SimulationInfo)simulationInfoH.get(simRef);
			if(simInfo == null){
				Vector<VersionInfo> simVector = getVersionableInfos(con,user,VersionableType.Simulation,false,new KeyValue(simRef),false,dbSyntax);
				if (simVector != null && simVector.size() > 0) {
					simInfo = (SimulationInfo)simVector.firstElement();
					simulationInfoH.put(simRef,simInfo);
				}
			}

			SimulationInfo regrSimInfo = null;
			MathModelInfo regrMathModelInfo = null;
			if(simRegrRef != null){
				regrSimInfo = (SimulationInfo)simulationInfoH.get(simRegrRef);
				if(regrSimInfo == null){
					Vector<VersionInfo> regSimVector = getVersionableInfos(con,user,VersionableType.Simulation,false,new KeyValue(simRegrRef),false,dbSyntax);
					if (regSimVector != null && regSimVector.size() > 0) {
						regrSimInfo = (SimulationInfo)regSimVector.firstElement();
						simulationInfoH.put(simRegrRef,regrSimInfo);
					}
				}
				regrMathModelInfo = (MathModelInfo)mathModelInfoH.get(mathRegrRef);
				if(regrMathModelInfo == null){
					Vector<VersionInfo> regMathVector = getVersionableInfos(con,user,VersionableType.MathModelMetaData,false,new KeyValue(mathRegrRef),false,dbSyntax);
					if (regMathVector != null && regMathVector.size() > 0) {
						regrMathModelInfo = (MathModelInfo)regMathVector.firstElement();
						mathModelInfoH.put(mathRegrRef,regrMathModelInfo);
					}
				}
			}
			
			//
			VariableComparisonSummary[] neededVCSArr = null;
			Vector vcsV = (Vector)vcsH.get(tcritKey);
			if(vcsV != null){
				neededVCSArr = new VariableComparisonSummary[vcsV.size()];
				vcsV.copyInto(neededVCSArr);
			}
			//
			TestCriteriaNew tcn = null;
			if (simInfo != null) {
				tcn = new TestCriteriaNewMathModel(
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
				reportStatus = TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT;
			}
			String reportStatusMessage = rset.getString(TFTestCriteriaTable.table.reportMessage.getUnqualifiedColName());
			if(rset.wasNull()){
				reportStatusMessage = null;
			}
			if(reportStatusMessage != null && reportStatusMessage.length() > 0){
				reportStatusMessage = TokenMangler.getSQLRestoredString(reportStatusMessage);
			}
			Vector v = (Vector)tcritH.get(tcaseRef);
			if(v == null){
				v = new Vector();
				tcritH.put(tcaseRef,v);
			}

			SimulationInfo simInfo = (SimulationInfo)simulationInfoH.get(tcSimRef);
			if(simInfo == null){
				Vector<VersionInfo> simVector = getVersionableInfos(con,user,VersionableType.Simulation,false,new KeyValue(tcSimRef),false,dbSyntax);
				if (simVector != null && simVector.size() == 1) {
					simInfo = (SimulationInfo)simVector.firstElement();
					simulationInfoH.put(tcSimRef,simInfo);
				}else{
					throw new DataAccessException("Found more than 1 versionable for tcsimRef="+tcSimRef);
				}
			}

			SimulationInfo regrSimInfo = null;
			BioModelInfo regrBioModelInfo = null;
			if(regrSimRef != null){
				regrSimInfo = (SimulationInfo)simulationInfoH.get(regrSimRef);
				if(regrSimInfo == null){
					Vector<VersionInfo> regSimVector = getVersionableInfos(con,user,VersionableType.Simulation,false,new KeyValue(regrSimRef),false,dbSyntax);
					if (regSimVector != null && regSimVector.size() == 1) {
						regrSimInfo = (SimulationInfo)regSimVector.firstElement();
						simulationInfoH.put(regrSimRef,regrSimInfo);
					}else{
						throw new DataAccessException("Found more than 1 versionable for simregRef="+regrSimRef);
					}
				}
				regrBioModelInfo = (BioModelInfo)mathModelInfoH.get(regrBioModelRef);
				if(regrBioModelInfo == null){
					Vector<VersionInfo> regBioModelVector = getVersionableInfos(con,user,VersionableType.BioModelMetaData,false,new KeyValue(regrBioModelRef),false,dbSyntax);
					if (regBioModelVector != null && regBioModelVector.size() == 1) {
						regrBioModelInfo = (BioModelInfo)regBioModelVector.firstElement();
						mathModelInfoH.put(regrBioModelRef,regrBioModelInfo);
					}else{
						throw new DataAccessException("Found more than 1 versionable for reegrbiomodelRef="+regrBioModelRef);
					}
				}
			}
			
			//
			VariableComparisonSummary[] neededVCSArr = null;
			Vector vcsV = (Vector)vcsH.get(tcritKey);
			if(vcsV != null){
				neededVCSArr = new VariableComparisonSummary[vcsV.size()];
				vcsV.copyInto(neededVCSArr);
			}
			//
			TestCriteriaNew tcn = null;
			if (simInfo != null) {
				tcn = new TestCriteriaNewBioModel(
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
				tcAnnot = TokenMangler.getSQLRestoredString(tcAnnot);
			}
			java.util.Date tcDate = VersionTable.getDate(rset,TFTestCaseTable.table.creationDate.getUnqualifiedColName());

			MathModelInfo mmInfo = null;
			BioModelInfo bmInfo = null;
			if(mmRef != null){
				mmInfo = (MathModelInfo)mathModelInfoH.get(mmRef);		
				if(mmInfo == null){
					Vector<VersionInfo> mathVector = getVersionableInfos(con,user,VersionableType.MathModelMetaData,false,new KeyValue(mmRef),false,dbSyntax);
					if (mathVector != null && mathVector.size() > 0) {
						mmInfo = (MathModelInfo)mathVector.firstElement();
						mathModelInfoH.put(mmRef,mmInfo);
					}
				}
			}else if(bioModelRef != null){
				bmInfo = (BioModelInfo)bioModelInfoH.get(bioModelRef);		
				if(bmInfo == null){
					Vector<VersionInfo> bmAppVector = getVersionableInfos(con,user,VersionableType.BioModelMetaData,false,new KeyValue(bioModelRef),false,dbSyntax);
					if (bmAppVector != null && bmAppVector.size() > 0) {
						bmInfo = (BioModelInfo)bmAppVector.firstElement();
						bioModelInfoH.put(bioModelRef,bmInfo);
					}
				}
			}else{
				throw new RuntimeException("Test case in DB does not have MathmodelRef or BioModelAppRef");
			}
			TestCriteriaNew[] neededTcritArr = null;
			Vector needTcritV = (Vector)tcritH.get(tcaseKey);
			if(needTcritV != null){
				neededTcritArr = new TestCriteriaNew[needTcritV.size()];
				needTcritV.copyInto(neededTcritArr);
			}
			TestCaseNew tcn = null;
			if (mmInfo != null) {
				tcn = new TestCaseNewMathModel(tcaseKey,mmInfo,tcType,tcAnnot,neededTcritArr);
				tcV.add(tcn);
			}else if(bmInfo != null){
				tcn = new TestCaseNewBioModel(tcaseKey,bmInfo,simContextName,new KeyValue(simContextRef),tcType,tcAnnot,neededTcritArr);
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
		TestCaseNew[] tcnArr = null;
		if(tcV.size() > 0){
			tcnArr = new TestCaseNew[tcV.size()];
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
		boolean islocked = true;
		if(rset.next()){
			tsKey = rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName());
			tsVersion = rset.getString(TFTestSuiteTable.table.tsVersion.getUnqualifiedColName());
			tsVCBuild = rset.getString(TFTestSuiteTable.table.vcBuildVersion.getUnqualifiedColName());
			tsNumericsBuild = rset.getString(TFTestSuiteTable.table.vcNumericsVersion.getUnqualifiedColName());
			tsDate = VersionTable.getDate(rset,TFTestSuiteTable.table.creationDate.getUnqualifiedColName());
			tsAnnot = rset.getString(TFTestSuiteTable.table.tsAnnotation.getUnqualifiedColName());
			islocked = rset.getBoolean(TFTestSuiteTable.table.isLocked.getUnqualifiedColName());
		}else{
			throw new ObjectNotFoundException("TestSuite with key="+getThisTS+" not found");
		}
		rset.close();

		TestSuiteInfoNew tsiNew = new TestSuiteInfoNew(tsKey,tsVersion,tsVCBuild,tsNumericsBuild,tsDate,tsAnnot,islocked);

		TestSuiteNew tsn = new TestSuiteNew(tsiNew,tcnArr);
		//testSuiteHash.put(tsKey,tsn);
		return tsn;
		
		}

	}catch(SQLException e){
		lg.error("failed: ("+sql+"): "+e.getMessage(), e);
		throw e;
	}finally{
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
public static TestSuiteInfoNew[] testSuiteInfosGet(Connection con,User user) throws SQLException{
	
	if(!user.isTestAccount()){
		throw new PermissionException("User="+user.getName()+" not allowed TestSuiteInfo");
	}

	String sql =
		"SELECT * FROM " + TFTestSuiteTable.table.getTableName();

	Vector<TestSuiteInfoNew> tsiV = new Vector<TestSuiteInfoNew>();
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
			boolean islocked = rset.getBoolean(TFTestSuiteTable.table.isLocked.getUnqualifiedColName());
			tsiV.add(new TestSuiteInfoNew(tsKey,tsID,vcBuildS,vcNumericS,date,tsAnnot,islocked));
		}
	}finally{
		if(stmt != null){
			stmt.close();
		}
	}

	if(tsiV.size() > 0){
		TestSuiteInfoNew[] temp = new TestSuiteInfoNew[tsiV.size()];
		tsiV.copyInto(temp);
		return temp;
	}
	return null;

}

private static final String MODEL_TYPE_COLUMN = "TYPE";
private static final String MODEL_ID_COLUMN = "MODELID";
private static final String PERMISSION_COLUMN = "PERMISSION";

private static Object getLoadTestDetails(Connection con,Integer slowLoadThreshold,String loadTestUserQueryCondition) throws SQLException{

	if(slowLoadThreshold != null && loadTestUserQueryCondition != null){
		throw new IllegalArgumentException(
			"SlowLoadThreshold and 'SQL user Query' cannot both be non-null at the same time.");
	}
	
	String specialCondition = null;
	if(slowLoadThreshold != null){
		specialCondition =
			LoadModelsStatTable.table.loadTime.getUnqualifiedColName() + " IS NOT NULL "+
			" AND " +
			LoadModelsStatTable.table.loadTime.getUnqualifiedColName() + " > "+slowLoadThreshold;
	}else if(loadTestUserQueryCondition == null){
		specialCondition = LoadModelsStatTable.table.errorMessage.getUnqualifiedColName()+" IS NOT NULL";
	}else{
		specialCondition = loadTestUserQueryCondition;
	}


	String sql = "SELECT " +
		LoadModelsStatTable.table.softwareVers.getUnqualifiedColName()+","+
		LoadModelsStatTable.table.timeStamp.getUnqualifiedColName()+","+
		"DECODE("+VersionTable.privacy_ColumnName+",0,'PUBLIC',1,'PRIVATE','GROUP') "+PERMISSION_COLUMN+","+
		UserTable.table.userid.getUnqualifiedColName()+","+
		MODEL_TYPE_COLUMN+","+
		VersionTable.name_ColumnName+","+
		MODEL_ID_COLUMN+","+
		VersionTable.versionDate_ColumnName+","+
		LoadModelsStatTable.table.resultFlag.getUnqualifiedColName()+","+
		LoadModelsStatTable.table.loadTime.getUnqualifiedColName()+","+
		LoadModelsStatTable.table.errorMessage.getUnqualifiedColName()+","+
		LoadModelsStatTable.table.bSameCachedAndNotCachedObj.getUnqualifiedColName()+","+
		LoadModelsStatTable.table.bSameCachedAndNotCachedObjExc.getUnqualifiedColName()+","+
		LoadModelsStatTable.table.bSameCachedAndNotCachedXML.getUnqualifiedColName()+","+
		LoadModelsStatTable.table.bSameCachedAndNotCachedXMLExc.getUnqualifiedColName()+","+
		LoadModelsStatTable.table.bSameSelfXMLCachedRoundtrip.getUnqualifiedColName()+","+
		LoadModelsStatTable.table.bSameSelfXMLCachedRoundtripExc.getUnqualifiedColName()+
		" FROM "+
	" ("+
		"SELECT " +
			LoadModelsStatTable.table.softwareVers.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.timeStamp.getUnqualifiedColName()+","+
			VersionTable.privacy_ColumnName+","+
			VersionTable.versionDate_ColumnName+","+
			VersionTable.name_ColumnName+","+
			UserTable.table.userid.getUnqualifiedColName()+","+
			"'"+LoadTestInfoOpResults.MODELTYPE_BIO+"' "+MODEL_TYPE_COLUMN+","+
			BioModelTable.table.id.getQualifiedColName() + " " + MODEL_ID_COLUMN+","+
			LoadModelsStatTable.table.resultFlag.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.loadTime.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.errorMessage.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameCachedAndNotCachedObj.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameCachedAndNotCachedObjExc.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameCachedAndNotCachedXML.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameCachedAndNotCachedXMLExc.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameSelfXMLCachedRoundtrip.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameSelfXMLCachedRoundtripExc.getUnqualifiedColName()+
		" FROM "+
			LoadModelsStatTable.table.getTableName()+","+
			BioModelTable.table.getTableName()+","+
			UserTable.table.getTableName()+
		" WHERE "+
			LoadModelsStatTable.table.bioModelRef.getUnqualifiedColName()+" IS NOT NULL"+
			" AND "+
			BioModelTable.table.id.getQualifiedColName()+ " = "+LoadModelsStatTable.table.bioModelRef.getUnqualifiedColName()+
			" AND "+
			UserTable.table.id.getQualifiedColName()+" = " +BioModelTable.table.ownerRef.getQualifiedColName()+
		" UNION "+
		"SELECT " +
			LoadModelsStatTable.table.softwareVers.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.timeStamp.getUnqualifiedColName()+","+
			VersionTable.privacy_ColumnName+","+
			VersionTable.versionDate_ColumnName+","+
			VersionTable.name_ColumnName+","+
			UserTable.table.userid.getUnqualifiedColName()+","+
			"'"+LoadTestInfoOpResults.MODELTYPE_MATH+"' "+MODEL_TYPE_COLUMN+","+
			MathModelTable.table.id.getQualifiedColName() + " " + MODEL_ID_COLUMN+","+
			LoadModelsStatTable.table.resultFlag.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.loadTime.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.errorMessage.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameCachedAndNotCachedObj.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameCachedAndNotCachedObjExc.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameCachedAndNotCachedXML.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameCachedAndNotCachedXMLExc.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameSelfXMLCachedRoundtrip.getUnqualifiedColName()+","+
			LoadModelsStatTable.table.bSameSelfXMLCachedRoundtripExc.getUnqualifiedColName()+
		" FROM "+
			LoadModelsStatTable.table.getTableName()+","+
			MathModelTable.table.getTableName()+","+
			UserTable.table.getTableName()+
		" WHERE "+
			LoadModelsStatTable.table.mathModelRef.getUnqualifiedColName()+" IS NOT NULL"+
			" AND "+
			MathModelTable.table.id.getQualifiedColName()+ " = "+LoadModelsStatTable.table.mathModelRef.getUnqualifiedColName()+
			" AND "+
			UserTable.table.id.getQualifiedColName()+" = " +MathModelTable.table.ownerRef.getQualifiedColName()+
	" )"+
	" WHERE "+
		specialCondition;
		
	Object loadTestDetailHash = null;
	if(slowLoadThreshold == null && loadTestUserQueryCondition == null){
		loadTestDetailHash =
			new Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestFailDetails>>();
	}else if(slowLoadThreshold != null){
		loadTestDetailHash =
			new Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestSlowDetails>>();
	}else{
		loadTestDetailHash =
			new Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestDetails>>();		
	}
		
	Statement stmt = con.createStatement();
	ResultSet rset = stmt.executeQuery(sql);
	while(rset.next()){
		String softwareVers =
			TokenMangler.getSQLRestoredString(rset.getString(LoadModelsStatTable.table.softwareVers.getUnqualifiedColName().toString()));
		String timeStamp = rset.getString(LoadModelsStatTable.table.timeStamp.getUnqualifiedColName().toString());
		String permission = rset.getString(PERMISSION_COLUMN);
		String userid = rset.getString(UserTable.table.userid.getUnqualifiedColName());
		String modelType = rset.getString(MODEL_TYPE_COLUMN);
		String modelName = rset.getString(VersionTable.name_ColumnName);
		KeyValue modelKeyValue = new KeyValue(rset.getString(MODEL_ID_COLUMN));
		String versionDate = rset.getString(VersionTable.versionDate_ColumnName);
		String errorMessage = null;
		Integer loadTime = null;
		if(slowLoadThreshold == null && loadTestUserQueryCondition == null){
			errorMessage = rset.getString(LoadModelsStatTable.table.errorMessage.getUnqualifiedColName());
		}else if(slowLoadThreshold != null){
			loadTime = rset.getInt(LoadModelsStatTable.table.loadTime.getUnqualifiedColName());
		}
		
		LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp versTimeStamp =
			new LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp(softwareVers,timeStamp);
		if(slowLoadThreshold == null && loadTestUserQueryCondition == null){
			Vector<LoadTestInfoOpResults.LoadTestFailDetails> loadTestFailDetailsV =
				(Vector<LoadTestInfoOpResults.LoadTestFailDetails>)
				((Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestFailDetails>>)loadTestDetailHash).get(versTimeStamp);
			if(loadTestFailDetailsV == null){
				loadTestFailDetailsV =
					new Vector<LoadTestInfoOpResults.LoadTestFailDetails>();
				((Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestFailDetails>>)loadTestDetailHash).put(versTimeStamp, loadTestFailDetailsV);
			}
			LoadTestInfoOpResults.LoadTestFailDetails loadTestFailDetails = 
				new LoadTestInfoOpResults.LoadTestFailDetails(
					permission,userid,modelType,modelName,modelKeyValue,versionDate,errorMessage);
			loadTestFailDetailsV.add(loadTestFailDetails);			
		}else if(slowLoadThreshold != null){
			Vector<LoadTestInfoOpResults.LoadTestSlowDetails> loadTestSlowDetailsV =
				(Vector<LoadTestInfoOpResults.LoadTestSlowDetails>)
				((Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestSlowDetails>>)loadTestDetailHash).get(versTimeStamp);
			if(loadTestSlowDetailsV == null){
				loadTestSlowDetailsV =
					new Vector<LoadTestInfoOpResults.LoadTestSlowDetails>();
				((Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestSlowDetails>>)loadTestDetailHash).put(versTimeStamp, loadTestSlowDetailsV);
			}
			LoadTestInfoOpResults.LoadTestSlowDetails loadTestSlowDetails = 
				new LoadTestInfoOpResults.LoadTestSlowDetails(
					permission,userid,modelType,modelName,modelKeyValue,versionDate,loadTime);
			loadTestSlowDetailsV.add(loadTestSlowDetails);
		}else{
			Vector<LoadTestInfoOpResults.LoadTestDetails> loadTestDetailsUserQueryV =
				(Vector<LoadTestInfoOpResults.LoadTestDetails>)
				((Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestDetails>>)loadTestDetailHash).get(versTimeStamp);
			if(loadTestDetailsUserQueryV == null){
				loadTestDetailsUserQueryV =
					new Vector<LoadTestInfoOpResults.LoadTestDetails>();
				((Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestDetails>>)loadTestDetailHash).put(versTimeStamp, loadTestDetailsUserQueryV);
			}
			LoadTestInfoOpResults.LoadTestDetails loadTestDetailsUserQuery = 
				new LoadTestInfoOpResults.LoadTestDetails(
					permission,userid,modelType,modelName,modelKeyValue,versionDate);
			loadTestDetailsUserQueryV.add(loadTestDetailsUserQuery);
		
		}
	}
	rset.close();
	stmt.close();
	
	return loadTestDetailHash;
}
/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:55:36 AM)
 * @return cbit.vcell.numericstest.TestSuiteNew
 * @param tsop cbit.vcell.numericstest.TestSuiteOP
 */
public static TestSuiteOPResults testSuiteOP(TestSuiteOP tsop,Connection con,User user, KeyFactory keyFactory) 
			throws SQLException,DataAccessException{

	java.util.TreeSet<BigDecimal> changedTestSuiteKeys = new java.util.TreeSet<BigDecimal>();
	
	String sql = null;
	Statement stmt = null;
	
	try{
		if(tsop instanceof ModelGeometryOP){
			ModelGeometryOP modelGeometryOP = (ModelGeometryOP)tsop;
			if(modelGeometryOP.getVCDocumentInfo() instanceof BioModelInfo){
				BioModelInfo bioModelInfo = (BioModelInfo)modelGeometryOP.getVCDocumentInfo();
				sql =
					"SELECT "+SimContextTable.table.geometryRef.getQualifiedColName()+
					" FROM "+
						SimContextTable.table.getTableName()+","+BioModelSimContextLinkTable.table.getTableName()+
					" WHERE "+
						SimContextTable.table.name.getQualifiedColName()+" = '"+modelGeometryOP.getBioModelApplicationName()+"'"+
						" AND "+
						BioModelSimContextLinkTable.table.simContextRef.getQualifiedColName()+" = "+SimContextTable.table.id.getQualifiedColName()+
						" AND "+
						BioModelSimContextLinkTable.table.bioModelRef.getQualifiedColName()+" = "+bioModelInfo.getVersion().getVersionKey().toString();
			}else if(modelGeometryOP.getVCDocumentInfo() instanceof MathModelInfo){
				MathModelInfo mathModelInfo = (MathModelInfo)modelGeometryOP.getVCDocumentInfo();
				sql =
					"SELECT "+MathDescTable.table.geometryRef.getQualifiedColName()+
					" FROM "+
						MathDescTable.table.getTableName()+","+MathModelTable.table.getTableName()+
					" WHERE "+
						MathModelTable.table.id.getQualifiedColName()+" = "+mathModelInfo.getVersion().getVersionKey().toString()+
						" AND "+
						MathModelTable.table.mathRef.getQualifiedColName()+" = "+MathDescTable.table.id.getQualifiedColName();
			}else{
				throw new IllegalArgumentException("UnImplemented VCDocumentInfo type="+modelGeometryOP.getVCDocumentInfo().getClass().getName());
			}
			stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery(sql);
			BigDecimal geometryKey = null;
			if(rset.next()){
				geometryKey = rset.getBigDecimal(1);
				if(rset.next()){
					throw new DataAccessException("Expecting only 1 Geometry but found at least 2");
				}
			}else{
				throw new DataAccessException("No Geometry found using criteria");
			}
			rset.close();
			stmt.close();
			return new ModelGeometryOPResults(new KeyValue(geometryKey));
		}
		//
		//LoadTest operations -------------------------------------------------------------------------------------------------
		//
		else if(tsop instanceof LoadTestInfoOP){
			//
			//Delete LoadTest
			//
			if(((LoadTestInfoOP)tsop).getLoadTestOpFlag() == LoadTestOpFlag.delete){
				//Delete before return details
				LoadTestSoftwareVersionTimeStamp[] deleteTheseVersTimeStamps =
					((LoadTestInfoOP)tsop).getLoadTestSoftwareVersionTimeStamps();
				for (int i = 0; i < deleteTheseVersTimeStamps.length; i++) {
					sql =
						"DELETE FROM "+LoadModelsStatTable.table.getTableName()+
						" WHERE "+
						LoadModelsStatTable.table.softwareVers + " = " + "'"+deleteTheseVersTimeStamps[i].getSoftwareVersion()+"'"+
						" AND " +
						LoadModelsStatTable.table.timeStamp + " = " + "'"+deleteTheseVersTimeStamps[i].getRunTimeStamp()+"'";
					DbDriver.updateCleanSQL(con, sql);
				}
				return null;
			}
			//Check if Date info is requested
			if(((LoadTestInfoOP)tsop).getLoadTestOpFlag() == LoadTestOpFlag.info &&
					((LoadTestInfoOP)tsop).getBeginDate() != null){
				final String YMD_FORMAT_STRING = "yyyy-MM-dd";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YMD_FORMAT_STRING);
				final String BETWEEN_CONDITION = 
					" BETWEEN "+
					" TO_DATE('" + simpleDateFormat.format(((LoadTestInfoOP)tsop).getBeginDate()) + " 00:00:00', '"+YMD_FORMAT_STRING+" HH24:MI:SS') "+
					" AND "+
					" TO_DATE('" + simpleDateFormat.format(((LoadTestInfoOP)tsop).getEndDate()) +   " 23:59:59', '"+YMD_FORMAT_STRING+" HH24:MI:SS') ";

				sql = 
					"SELECT "+
						BioModelTable.table.id.getQualifiedColName()+","+
						UserTable.table.userid.getUnqualifiedColName()+
					" FROM "+
						BioModelTable.table.getTableName()+","+
						UserTable.table.getTableName()+
					" WHERE "+
						UserTable.table.id.getQualifiedColName()+" = "+BioModelTable.table.ownerRef.getQualifiedColName()+
						" AND " +
						BioModelTable.table.versionDate.getQualifiedColName() + BETWEEN_CONDITION +
					" UNION "+
					"SELECT "+
						MathModelTable.table.id.getQualifiedColName()+","+
						UserTable.table.userid.getUnqualifiedColName()+
					" FROM "+
						MathModelTable.table.getTableName()+","+
						UserTable.table.getTableName()+
					" WHERE "+
						UserTable.table.id.getQualifiedColName()+" = "+MathModelTable.table.ownerRef.getQualifiedColName()+
						" AND " +
						MathModelTable.table.versionDate.getQualifiedColName() + BETWEEN_CONDITION;
				stmt = con.createStatement();
				ResultSet rset = stmt.executeQuery(sql);
				TreeSet<String> uniqueUserIDTreeSet = new TreeSet<String>();
				Vector<KeyValue> keyValuesBetweenDatesV = new Vector<KeyValue>();
				while(rset.next()){
					uniqueUserIDTreeSet.add(rset.getString(UserTable.table.userid.getUnqualifiedColName()));
					keyValuesBetweenDatesV.add(new KeyValue(rset.getBigDecimal(1)));
				}
				rset.close();
				stmt.close();
				return new LoadTestInfoOpResults(
					((LoadTestInfoOP)tsop).getBeginDate(), ((LoadTestInfoOP)tsop).getEndDate(),
					keyValuesBetweenDatesV.toArray(new KeyValue[0]), uniqueUserIDTreeSet.toArray(new String[0]));
				
			}
			//
			//Get LoadTest Info
			//
			//Get existing SoftwareVersion-Timestamp  count
			//
			Vector<Integer> loadTestInfoCountV = new Vector<Integer>();
			stmt = con.createStatement();
			sql =
				"SELECT COUNT(*)," +
					LoadModelsStatTable.table.softwareVers.getUnqualifiedColName()+","+
					LoadModelsStatTable.table.timeStamp.getUnqualifiedColName()+
				" FROM "+
					LoadModelsStatTable.table.getTableName()+
				" GROUP BY "+
					LoadModelsStatTable.table.softwareVers.getUnqualifiedColName()+","+
					LoadModelsStatTable.table.timeStamp.getUnqualifiedColName();
				
			Vector<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp> 
			loadTestSoftwareVersionTimeStampsExistingV =
					new Vector<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp>();
			ResultSet rset = stmt.executeQuery(sql);
			while(rset.next()){
				String softwareVersion =
					TokenMangler.getSQLRestoredString(rset.getString(LoadModelsStatTable.table.softwareVers.getUnqualifiedColName()));
				String runTimeStamp = rset.getString(LoadModelsStatTable.table.timeStamp.getUnqualifiedColName());
				loadTestSoftwareVersionTimeStampsExistingV.add(
						new LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp(softwareVersion, runTimeStamp));
				loadTestInfoCountV.add(rset.getInt(1));
			}
			rset.close();
			stmt.close();
			//
			//Get empty test info count (models that haven't been checked yet during a test run)
			//
			stmt = con.createStatement();
			sql =
				"SELECT COUNT(*)," +
					LoadModelsStatTable.table.softwareVers.getUnqualifiedColName()+","+
					LoadModelsStatTable.table.timeStamp.getUnqualifiedColName()+
				" FROM "+
					LoadModelsStatTable.table.getTableName()+
				" WHERE " +
					LoadModelsStatTable.table.resultFlag.getUnqualifiedColName() + " IS NULL"+
				" GROUP BY "+
					LoadModelsStatTable.table.softwareVers.getUnqualifiedColName()+","+
					LoadModelsStatTable.table.timeStamp.getUnqualifiedColName();
				
			Vector<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp> 
				loadTestSoftwareVersionTimeStampsEmptyV =
					new Vector<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp>();
			rset = stmt.executeQuery(sql);
			Integer[] loadTestInfoCountEmptyArr = new Integer[loadTestInfoCountV.size()];
			while(rset.next()){
				String softwareVersion =
					TokenMangler.getSQLRestoredString(rset.getString(LoadModelsStatTable.table.softwareVers.getUnqualifiedColName()));
				String runTimeStamp = rset.getString(LoadModelsStatTable.table.timeStamp.getUnqualifiedColName());
				loadTestSoftwareVersionTimeStampsEmptyV.add(
						new LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp(softwareVersion, runTimeStamp));
				//match to existing array index so full and empty match
				for (int i = 0; i < loadTestSoftwareVersionTimeStampsExistingV.size(); i++) {
					if(loadTestSoftwareVersionTimeStampsExistingV.elementAt(i).getSoftwareVersion().equals(softwareVersion) &&
						loadTestSoftwareVersionTimeStampsExistingV.elementAt(i).getRunTimeStamp().equals(runTimeStamp)){
						loadTestInfoCountEmptyArr[i] = rset.getInt(1);											
					}
				}
			}
			rset.close();
			stmt.close();

			//
			//Get total Math and Bio model count
			//
			int totalBioMathModelCount = 0;
			stmt = con.createStatement();
			sql =
				"SELECT COUNT(*) FROM "+BioModelTable.table.getTableName()+
				" UNION "+
				"SELECT COUNT(*) FROM "+MathModelTable.table.getTableName();
			rset = stmt.executeQuery(sql);
			if(rset.next()){
				totalBioMathModelCount = rset.getInt(1);
				if(rset.next()){
					totalBioMathModelCount+= rset.getInt(1);
				}else{
					totalBioMathModelCount = 0;
				}
			}
			if(totalBioMathModelCount == 0){
				throw new DataAccessException("No results when querying bio and Mathmodel count");
			}
			rset.close();
			stmt.close();
			//
			//Get slow Loads
			//
			Object loadTestSlowHash = null;
			if(((LoadTestInfoOP)tsop).getSlowLoadThresholdMilliSec() != null){
				Integer slowLoaderThreshold = ((LoadTestInfoOP)tsop).getSlowLoadThresholdMilliSec();
				loadTestSlowHash = getLoadTestDetails(con,slowLoaderThreshold,null);			
			}
			//
			//Get failed loads
			//
			Object loadTestFailHash = getLoadTestDetails(con,null,null);
			
			//
			//Get user specified query info.
			//
			Object loadTestUserQueryHash = null;
			if(((LoadTestInfoOP)tsop).getUserQueryCondition() != null){
				loadTestUserQueryHash = getLoadTestDetails(con, null, ((LoadTestInfoOP)tsop).getUserQueryCondition());
			}

			
			return new LoadTestInfoOpResults(
					loadTestInfoCountV.toArray(new Integer[0]),
					loadTestInfoCountEmptyArr,
					totalBioMathModelCount,
					loadTestSoftwareVersionTimeStampsExistingV.toArray(new LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp[0]),
					(Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestFailDetails>>) loadTestFailHash,
					(Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestSlowDetails>>) loadTestSlowHash,
					(Hashtable<LoadTestInfoOpResults.LoadTestSoftwareVersionTimeStamp, Vector<LoadTestInfoOpResults.LoadTestDetails>>) loadTestUserQueryHash,
					((LoadTestInfoOP)tsop).getSlowLoadThresholdMilliSec());

		}

		//
		//TestSuite operations ---------------------------------------------------------------------------------------------------
		//
		stmt = con.createStatement();
		
		if(tsop instanceof AddTestSuiteOP){
			AddTestSuiteOP addts_tsop = (AddTestSuiteOP)tsop;
			String annotation = addts_tsop.getTestSuiteAnnotation();
			if(annotation != null){
				if(annotation.length() == 0){
					annotation = null;
				}else{
					annotation = TokenMangler.getSQLEscapedString(annotation);
				}
			}

			BigDecimal changedTSKey = keyFactory.getUniqueBigDecimal(con);
			final int NOT_LOCKED = 0;
			sql = 
				"INSERT INTO "+TFTestSuiteTable.table.getTableName()+" VALUES("+
					changedTSKey+",'"+addts_tsop.getTestSuiteVersionID()+"',"+
					"'"+addts_tsop.getVCellBuildVersionID()+"'"+","+"'"+addts_tsop.getNumericsBuildVersionID()+"'"+","+
					"current_timestamp,current_timestamp,"+(annotation == null?"NULL":"'"+annotation+"'")+","+NOT_LOCKED+")";
			stmt.executeUpdate(sql);
			if(addts_tsop.getAddTestCasesOPs() != null){
				for(int i=0;i<addts_tsop.getAddTestCasesOPs().length;i+= 1){
					//Set new TSKey and do child OPs
					AddTestCasesOP atcOP = addts_tsop.getAddTestCasesOPs()[i];
					if(atcOP instanceof AddTestCasesOPMathModel){
						testSuiteOP(
							new AddTestCasesOPMathModel(
								changedTSKey,
								((AddTestCasesOPMathModel)atcOP).getMathModelKey(),
								atcOP.getTestCaseType(),atcOP.getAnnotation(),
								((AddTestCasesOPMathModel)atcOP).getAddTestCriteriaOPsMathModel()),
							con,user,keyFactory);
					}else if(atcOP instanceof AddTestCasesOPBioModel){
						testSuiteOP(
							new AddTestCasesOPBioModel(
								changedTSKey,
								((AddTestCasesOPBioModel)atcOP).getBioModelKey(),
								((AddTestCasesOPBioModel)atcOP).getSimContextKey(),
								atcOP.getTestCaseType(),atcOP.getAnnotation(),
								((AddTestCasesOPBioModel)atcOP).getAddTestCriteriaOPsBioModel()),
							con,user,keyFactory);
					}
				}
			}
			changedTestSuiteKeys.add(changedTSKey);
		}else if(tsop instanceof AddTestCasesOPBioModel){
			AddTestCasesOPBioModel addtc_tsop = (AddTestCasesOPBioModel)tsop;
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
				annotation = TokenMangler.getSQLEscapedString(annotation);
			}
			stmt.executeUpdate(
				"INSERT INTO "+TFTestCaseTable.table.getTableName()+" VALUES("+
					tcKey.toString()+","+addtc_tsop.getTestSuiteKey().toString()+",NULL,"+
					"'"+addtc_tsop.getTestCaseType()+"'"+","+"'"+annotation+"'"+","+"current_timestamp"+","+bmSimContextLinkRef.toString()+")");
			if(addtc_tsop.getAddTestCriteriaOPsBioModel() != null){
				for(int i=0;i<addtc_tsop.getAddTestCriteriaOPsBioModel().length;i+= 1){
					//Set new TSKey,TCaseKey and do child OPs
					AddTestCriteriaOPBioModel atcritOP = addtc_tsop.getAddTestCriteriaOPsBioModel()[i];
					testSuiteOP(
						new AddTestCriteriaOPBioModel(
							tcKey,atcritOP.getBioModelSimKey(),
							atcritOP.getRegressionBioModelKey(),atcritOP.getRegressionBioModelSimKey(),
							atcritOP.getMaxAbsoluteError(),atcritOP.getMaxRelativeError(),atcritOP.getAddTestResultsOP()),
						con,user,keyFactory);
				}
			}
			changedTestSuiteKeys.add(addtc_tsop.getTestSuiteKey());

			
		}else if(tsop instanceof AddTestCasesOPMathModel){
			AddTestCasesOPMathModel addtc_tsop = (AddTestCasesOPMathModel)tsop;
			BigDecimal tcKey = keyFactory.getUniqueBigDecimal(con);
			KeyValue mmKey = addtc_tsop.getMathModelKey();
			String annotation = addtc_tsop.getAnnotation();
			if(annotation != null){
				annotation = TokenMangler.getSQLEscapedString(annotation);
			}
			stmt.executeUpdate(
				"INSERT INTO "+TFTestCaseTable.table.getTableName()+" VALUES("+
					tcKey.toString()+","+addtc_tsop.getTestSuiteKey().toString()+","+mmKey.toString()+","+
					"'"+addtc_tsop.getTestCaseType()+"'"+","+"'"+annotation+"'"+","+"current_timestamp"+",NULL)");
			if(addtc_tsop.getAddTestCriteriaOPsMathModel() != null){
				for(int i=0;i<addtc_tsop.getAddTestCriteriaOPsMathModel().length;i+= 1){
					//Set new TSKey,TCaseKey and do child OPs
					AddTestCriteriaOPMathModel atcritOP = addtc_tsop.getAddTestCriteriaOPsMathModel()[i];
					testSuiteOP(
						new AddTestCriteriaOPMathModel(
							tcKey,atcritOP.getMathModelSimKey(),
							atcritOP.getRegressionMathModelKey(),
							atcritOP.getRegressionMathModelSimKey(),
							atcritOP.getMaxAbsoluteError(),atcritOP.getMaxRelativeError(),atcritOP.getAddTestResultsOP()),
						con,user,keyFactory);
				}
			}
			changedTestSuiteKeys.add(addtc_tsop.getTestSuiteKey());			
		}else if(tsop instanceof RemoveTestCasesOP){
			RemoveTestCasesOP removetc_tsop = (RemoveTestCasesOP)tsop;
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
			
		}else if(tsop instanceof AddTestCriteriaOPMathModel){
			AddTestCriteriaOPMathModel addtcrit_tsop = (AddTestCriteriaOPMathModel)tsop;
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
				SimulationTable.table.getTableName()+"."+SimulationTable.versionParentSimRef_ColumnName+" IS NULL";
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
//			sql =
//				"SELECT "+ResultSetMetaDataTable.table.id.getQualifiedColName()+
//				" FROM " +ResultSetMetaDataTable.table.getTableName()+
//				" WHERE "+
//					ResultSetMetaDataTable.table.simRef.getQualifiedColName()+"="+simKey.toString();
//			rset = stmt.executeQuery(sql);
//			if(rset.next()){
//				simDataRef = rset.getBigDecimal(MathModelSimulationLinkTable.table.id.getUnqualifiedColName());
//				if(rset.next()){
//					throw new DataAccessException("Too many ResultSetMetaData found for simKey="+simKey);
//				}
//			}
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
				"'"+TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT+"'"+",null"+
				")";
			stmt.executeUpdate(sql);
			if(addtcrit_tsop.getRegressionMathModelSimKey() != null){
				testSuiteOP(
					new EditTestCriteriaOPMathModel(
						tcritKey,
						addtcrit_tsop.getRegressionMathModelKey(),
						addtcrit_tsop.getRegressionMathModelSimKey(),
						addtcrit_tsop.getMaxAbsoluteError(),addtcrit_tsop.getMaxRelativeError()),
					con,user,keyFactory);
			}
			if(addtcrit_tsop.getAddTestResultsOP() != null){
				AddTestResultsOP atrOP = addtcrit_tsop.getAddTestResultsOP();
				//Set new TSKey,TCritKey and do child OPs
				testSuiteOP(
					new AddTestResultsOP(tcritKey,atrOP.getVariableComparisonSummaries()),
					con,user, keyFactory);
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
				
		}else if(tsop instanceof AddTestCriteriaOPBioModel){
			AddTestCriteriaOPBioModel addtcrit_tsop = (AddTestCriteriaOPBioModel)tsop;
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
				SimulationTable.table.getTableName()+"."+SimulationTable.versionParentSimRef_ColumnName+" IS NULL";
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
//			sql =
//				"SELECT "+ResultSetMetaDataTable.table.id.getQualifiedColName()+
//				" FROM " +ResultSetMetaDataTable.table.getTableName()+
//				" WHERE "+
//					ResultSetMetaDataTable.table.simRef.getQualifiedColName()+"="+simKey.toString();
//			rset = stmt.executeQuery(sql);
//			if(rset.next()){
//				simDataRef = rset.getBigDecimal(BioModelSimulationLinkTable.table.id.getUnqualifiedColName());
//				if(rset.next()){
//					throw new DataAccessException("Too many ResultSetMetaData found for simKey="+simKey);
//				}
//			}
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
				"NULL,NULL,"+"'"+TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT+"'"+",null"+
				")";
			stmt.executeUpdate(sql);
			if(addtcrit_tsop.getRegressionBioModelSimKey() != null){
				testSuiteOP(
					new EditTestCriteriaOPBioModel(
						tcritKey,
						addtcrit_tsop.getRegressionBioModelKey(),
						addtcrit_tsop.getRegressionBioModelSimKey(),
						addtcrit_tsop.getMaxAbsoluteError(),addtcrit_tsop.getMaxRelativeError()),
					con,user,keyFactory);
			}
			if(addtcrit_tsop.getAddTestResultsOP() != null){
				AddTestResultsOP atrOP = addtcrit_tsop.getAddTestResultsOP();
				//Set new TSKey,TCritKey and do child OPs
				testSuiteOP(
					new AddTestResultsOP(tcritKey,atrOP.getVariableComparisonSummaries()),
					con,user,keyFactory);
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
		}else if(tsop instanceof RemoveTestCriteriaOP){
			RemoveTestCriteriaOP removetcrit_tsop = (RemoveTestCriteriaOP)tsop;
			StringBuffer sb = new StringBuffer();
			for(int i=0;i< removetcrit_tsop.getTestCriterias().length;i+= 1){
				if(i != 0){sb.append(",");}sb.append(removetcrit_tsop.getTestCriterias()[i].getTCritKey().toString());
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
			if(numRowsUpdated != removetcrit_tsop.getTestCriterias().length){
				throw new DataAccessException("Remove TestCriteria keys="+sb.toString()+
					" removed row count="+numRowsUpdated+" expected "+removetcrit_tsop.getTestCriterias().length);
			}
		}else if(tsop instanceof RemoveTestSuiteOP){
			RemoveTestSuiteOP removets_tsop = (RemoveTestSuiteOP)tsop;
			int numRowsUpdated = stmt.executeUpdate(
				"DELETE FROM "+TFTestSuiteTable.table.getTableName()+
				" WHERE "+
				TFTestSuiteTable.table.id.getUnqualifiedColName()+"="+removets_tsop.getTestSuiteKey().toString());
			if(numRowsUpdated != 1){
				throw new DataAccessException("Remove SINGLE TestSuite - key="+removets_tsop.getTestSuiteKey().toString()+" removed row count="+numRowsUpdated);
			}

			changedTestSuiteKeys.add(removets_tsop.getTestSuiteKey());	
		}else if(tsop instanceof AddTestResultsOP){
			AddTestResultsOP addtr_tsop = (AddTestResultsOP)tsop;
			VariableComparisonSummary[] vcs = addtr_tsop.getVariableComparisonSummaries();
			if(vcs == null || vcs.length == 0){
				throw new DataAccessException(RemoveTestCasesOP.class.getName()+" had no TestResults");
			}
			for(int i=0;i<vcs.length;i+= 1){
				if(vcs[i] == null){
					throw new DataAccessException(RemoveTestCasesOP.class.getName()+" Array element was null");
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
		}else if(tsop instanceof RemoveTestResultsOP){
			RemoveTestResultsOP removetr_tsop = (RemoveTestResultsOP)tsop;
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
		}else if(tsop instanceof EditTestCriteriaOPReportStatus){
			EditTestCriteriaOPReportStatus edittcrit_tsop = (EditTestCriteriaOPReportStatus)tsop;
			BigDecimal tcritKey = edittcrit_tsop.getTestCriteriaKey();
			if(tcritKey == null){
				throw new DataAccessException(tsop.getClass().getName()+" had no TestCriteria keys");
			}
			String newRS = edittcrit_tsop.getNewReportStatus();
			if(newRS == null ||
				(!newRS.equals(TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT) &&
				!newRS.equals(TestCriteriaNew.TCRIT_STATUS_FAILEDVARS) &&
				!newRS.equals(TestCriteriaNew.TCRIT_STATUS_NODATA) &&
				!newRS.equals(TestCriteriaNew.TCRIT_STATUS_NOREFREGR) &&
				!newRS.equals(TestCriteriaNew.TCRIT_STATUS_PASSED) &&
				!newRS.equals(TestCriteriaNew.TCRIT_STATUS_RPERROR) &&
				!newRS.equals(TestCriteriaNew.TCRIT_STATUS_SIMFAILED) &&
				!newRS.equals(TestCriteriaNew.TCRIT_STATUS_SIMRUNNING) &&
				!newRS.equals(TestCriteriaNew.TCRIT_STATUS_SIMNOTRUNFAILDONE))){
				throw new DataAccessException("Unsupported ReportStatus="+edittcrit_tsop.getNewReportStatus());
			}
			String reportStatusMessage = edittcrit_tsop.getNewReportStatusMessage();
			if(reportStatusMessage != null){
				reportStatusMessage = TokenMangler.getSQLEscapedString(reportStatusMessage);
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
			if(newRS.equals(TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT)){
				testSuiteOP(new RemoveTestResultsOP(new BigDecimal[] {tcritKey}), con, user,keyFactory);
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
		}else if(tsop instanceof EditTestCriteriaOPMathModel){
			EditTestCriteriaOPMathModel edittcrit_tsop = (EditTestCriteriaOPMathModel)tsop;
			BigDecimal tcritKey = edittcrit_tsop.getTestCriteriaKey();
			if(tcritKey == null){
				throw new DataAccessException(tsop.getClass().getName()+" had no TestCriteria keys");
			}
			if((edittcrit_tsop.getMathModelRegressionRef() == null && edittcrit_tsop.getMathModelRegressionSimRef() != null)
					||
					(edittcrit_tsop.getMathModelRegressionRef() != null && edittcrit_tsop.getMathModelRegressionSimRef() == null)){
					throw new DataAccessException(tsop.getClass().getName()+" MathRef and SimRef must both be null or both not null");
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
			testSuiteOP(new EditTestCriteriaOPReportStatus(tcritKey,TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null), con, user,keyFactory);

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
		}else if(tsop instanceof EditTestCriteriaOPBioModel){
			EditTestCriteriaOPBioModel edittcrit_tsop = (EditTestCriteriaOPBioModel)tsop;
			BigDecimal tcritKey = edittcrit_tsop.getTestCriteriaKey();
			if(tcritKey == null){
				throw new DataAccessException(tsop.getClass().getName()+" had no TestCriteria keys");
			}
			if((edittcrit_tsop.getBioModelRegressionRef() == null && edittcrit_tsop.getBioModelRegressionSimRef() != null)
				||
				(edittcrit_tsop.getBioModelRegressionRef() != null && edittcrit_tsop.getBioModelRegressionSimRef() == null)){
				throw new DataAccessException(tsop.getClass().getName()+" ApplicationRef and SimRef must both be null or both not null");
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
			testSuiteOP(new EditTestCriteriaOPReportStatus(tcritKey,TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null), con, user,keyFactory);
			
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
		}else if(tsop instanceof ChangeTestCriteriaErrorLimitOP){
			ChangeTestCriteriaErrorLimitOP edittcrit_tsop = (ChangeTestCriteriaErrorLimitOP)tsop;
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
				testSuiteOP(new EditTestCriteriaOPReportStatus(tcritKeyArr[i],TestCriteriaNew.TCRIT_STATUS_NEEDSREPORT,null), con, user, keyFactory);
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

		}else if(tsop instanceof EditTestCasesOP){
			EditTestCasesOP edittc_tsop = (EditTestCasesOP)tsop;
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
							annotation = TokenMangler.getSQLEscapedString(annotation);
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
						testSuiteOP(etcors, con, user, keyFactory);
						
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
		}else if(tsop instanceof EditTestSuiteOP){
			EditTestSuiteOP editts_tsop = (EditTestSuiteOP)tsop;
			BigDecimal[] tsKeys = editts_tsop.getTestSuiteKeys();
			String[] annots = editts_tsop.getNewAnnotations();
			if(tsKeys == null || tsKeys.length == 0){
				throw new DataAccessException(tsop.getClass().getName()+" had no TestSuite keys");
			}
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<tsKeys.length;i+= 1){
				if(i != 0){sb.append(",");}sb.append(tsKeys[i].toString());
				
				if(editts_tsop.isLock() != null){
					stmt.executeUpdate(
							"UPDATE "+TFTestSuiteTable.table.getTableName()+
							" SET "+
								TFTestSuiteTable.table.isLocked.getQualifiedColName()+"= 1"+
							" WHERE "+TFTestSuiteTable.table.id.getQualifiedColName()+"="+tsKeys[i].toString()
							);
					
				}else{
					if(annots != null){
						String annotation = annots[i];
						if(annotation != null){
							if(annotation.length() == 0){
								annotation = null;
							}else{
								annotation = TokenMangler.getSQLEscapedString(annotation);
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
			}			
			
			ResultSet rset = stmt.executeQuery(
					"SELECT DISTINCT "+TFTestSuiteTable.table.id.getQualifiedColName()+
					" FROM "+TFTestSuiteTable.table.getTableName()+
					" WHERE "+TFTestSuiteTable.table.id.getQualifiedColName()+" IN ("+sb.toString()+")");
				while(rset.next()){
					changedTestSuiteKeys.add(rset.getBigDecimal(TFTestSuiteTable.table.id.getUnqualifiedColName()));			
				}
				rset.close();
		}else if(tsop instanceof QueryTestCriteriaCrossRefOP){
			QueryTestCriteriaCrossRefOP qtcritxr_tsop = (QueryTestCriteriaCrossRefOP)tsop;
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
			String REFTSUITEKEY = "REFTSUITEKEY";
			String REFTCASEKEY = "REFTCASEKEY";
			String REFTCRITKEY = "REFTCRITKEY";
//			String TCRALT2 = "TCRALT2";
//			String SIM2 = "SIM2";
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
				BioModelTable.table.id.getQualifiedColName() +" "+MODELID +","+
				TSALT+"."+TFTestSuiteTable.table.id.getUnqualifiedColName() +" "+REFTSUITEKEY +","+
				TCALT+"."+TFTestCaseTable.table.id.getUnqualifiedColName() +" "+REFTCASEKEY +","+
				"NULL " +REFTCRITKEY +//TCRALT2+"."+TFTestCriteriaTable.table.id.getUnqualifiedColName() +" "+REFTCRITKEY +
				" FROM " +
				TFTestSuiteTable.table.getTableName()+","+
				TFTestCaseTable.table.getTableName()+","+
				TFTestCriteriaTable.table.getTableName()+","+
				TFTestResultTable.table.getTableName()+","+
				BioModelSimContextLinkTable.table.getTableName()+","+
				BioModelTable.table.getTableName()+","+
				SimContextTable.table.getTableName()+","+
				SimulationTable.table.getTableName() +","+
				BioModelSimulationLinkTable.table.getTableName()+","+
				TFTestSuiteTable.table.getTableName()+" "+TSALT+","+
				TFTestCaseTable.table.getTableName()+" "+TCALT+","+
				TFTestCriteriaTable.table.getTableName()+" "+TCRALT+
				","+BioModelSimContextLinkTable.table.getTableName()+" "+BSCALT+
//				","+SimulationTable.table.getTableName()+" "+SIM2+
//				","+TFTestCriteriaTable.table.getTableName()+" "+TCRALT2+
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
//				" AND "+
//				TCRALT2+"."+TFTestCriteriaTable.table.testCaseRef.getUnqualifiedColName()+"(+)" +"="+ TCALT+"."+TFTestCaseTable.table.id.getUnqualifiedColName()+
//				" AND "+
//				TCRALT2+"."+TFTestCriteriaTable.table.simulationRef.getUnqualifiedColName()+"="+ SIM2+"."+SimulationTable.table.id.getUnqualifiedColName()+
//				" AND "+
//				SIM2+"."+SimulationTable.table.name.getUnqualifiedColName()+"="+SimulationTable.table.name.getQualifiedColName()+
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
				MathModelTable.table.id.getQualifiedColName() +" "+MODELID +","+
				TSALT+"."+TFTestSuiteTable.table.id.getUnqualifiedColName() +" "+REFTSUITEKEY +","+
				TCALT+"."+TFTestCaseTable.table.id.getUnqualifiedColName() +" "+REFTCASEKEY +","+
				"NULL " +REFTCRITKEY +//TCRALT2+"."+TFTestCriteriaTable.table.id.getUnqualifiedColName() +" "+REFTCRITKEY +
				" FROM " +
				TFTestSuiteTable.table.getTableName()+","+
				TFTestCaseTable.table.getTableName()+","+
				TFTestCriteriaTable.table.getTableName()+","+
				TFTestResultTable.table.getTableName()+","+
				MathModelTable.table.getTableName()+","+
				SimulationTable.table.getTableName() +","+
				MathModelSimulationLinkTable.table.getTableName()+","+
				TFTestSuiteTable.table.getTableName()+" "+TSALT+","+
				TFTestCaseTable.table.getTableName()+" "+TCALT+","+
				TFTestCriteriaTable.table.getTableName()+" "+TCRALT+","+
				MathModelSimulationLinkTable.table.getTableName()+" "+MMSIMALT+
//				","+SimulationTable.table.getTableName()+" "+SIM2+
//				","+TFTestCriteriaTable.table.getTableName()+" "+TCRALT2+
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
//				" AND "+
//				TCRALT2+"."+TFTestCriteriaTable.table.testCaseRef.getUnqualifiedColName()+"(+)" +"="+ TCALT+"."+TFTestCaseTable.table.id.getUnqualifiedColName()+
//				" AND "+
//				TCRALT2+"."+TFTestCriteriaTable.table.simulationRef.getUnqualifiedColName()+"="+ SIM2+"."+SimulationTable.table.id.getUnqualifiedColName()+
//				" AND "+
//				SIM2+"."+SimulationTable.table.name.getUnqualifiedColName()+"="+SimulationTable.table.name.getQualifiedColName()+
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
	return new TestSuiteOPResults(null);
}

/**
 * This method was created in VisualAge.
 * @param sql java.lang.String
 */
protected final static void updateCleanLOB(Connection con,String conditionalColumnName,KeyValue conditionalValue,String table,Field column,Object lob_data, DatabaseSyntax dbSyntax) throws SQLException,DataAccessException {
	if (table == null || con == null || column == null || lob_data == null) {
		throw new IllegalArgumentException("Improper parameters for updateCleanLOB");
	}
	if(!(lob_data instanceof byte[]) && !(lob_data instanceof String)){
		throw new IllegalArgumentException("Wrong DataType "+lob_data.getClass().toString()+" for updateCleanLOB.   only byte[] and String allowed");
	}
	switch (dbSyntax){
	case ORACLE:{
		//Select the LOB(column) from the table
		String sql = "SELECT "+	column.getUnqualifiedColName() + " FROM " + table +
					" WHERE "+conditionalColumnName+" = " + conditionalValue +
					" FOR UPDATE OF " + table+"."+column.getUnqualifiedColName();
		//
		//System.out.println(sql);
		//
		Statement s = con.createStatement();
		try {
			ResultSet rset = s.executeQuery(sql);
			if(rset.next()){
				Object lob_object = rset.getObject(column.getUnqualifiedColName());
				if(lob_data instanceof byte[] && lob_object instanceof java.sql.Blob){
					//java.sql.Blob selectedBLOB = ((oracle.jdbc.OracleResultSet) rset).getBLOB(1);
					java.sql.Blob selectedBLOB = (java.sql.Blob)lob_object;
					selectedBLOB.setBytes(1,(byte[])lob_data);
				}else if (lob_data instanceof String && lob_object instanceof java.sql.Clob){
					//java.sql.Clob selectedCLOB = ((oracle.jdbc.OracleResultSet) rset).getCLOB(1);
					java.sql.Clob selectedCLOB = (java.sql.Clob)lob_object;
					selectedCLOB.setString(1,(String)lob_data);
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
		break;
	}
	case POSTGRES:{
		throw new RuntimeException("DbDriver.updateCleanLOB() not intended for Postgres - use BYTEA or TEXT instead for large objects.");
	}
	default:{
		throw new RuntimeException("unexpected DatabaseSyntax "+dbSyntax);
	}
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
	if (lg.isTraceEnabled()) {
		lg.trace("updateClean SQL "+sql);
	}
	Statement s = con.createStatement();
	try {
		return s.executeUpdate(sql);
	} finally {
		s.close();
	}
}


public static int updatePreparedCleanSQL(Connection con, String sql, String data) throws SQLException {
	if (sql == null || con == null || data == null) {
		throw new IllegalArgumentException("Improper parameters for DbDriver.updatePreparedCleanSQL");
	}
	if (sql.contains("EMPTY_CLOB()")){
		throw new RuntimeException("CLOBs not supported in DbDriver.updatePreparedCleanSQL(): "+sql);
	}
	if (!sql.contains("?")){
		throw new RuntimeException("expected exactly one '?' in prepared statement in DbDriver.updatePreparedCleanSQL(): "+sql);
	}
	if (lg.isTraceEnabled()) {
		lg.trace("DbDriver.updatePreparedCleanSQL(): "+sql);
	}
	try (PreparedStatement ps = con.prepareStatement(sql)){
		ps.setString(1, data);
		return ps.executeUpdate();
	}
}


public static int updatePreparedCleanSQL(Connection con, String sql, byte[] data) throws SQLException {
	if (sql == null || con == null || data == null) {
		throw new IllegalArgumentException("Improper parameters for DbDriver.updatePreparedCleanSQL");
	}
	if (sql.contains("EMPTY_BLOB()")){
		throw new RuntimeException("BLOBs not supported in DbDriver.updatePreparedCleanSQL(): "+sql);
	}
	if (!sql.contains("?")){
		throw new RuntimeException("expected exactly one '?' in prepared statement in DbDriver.updatePreparedCleanSQL(): "+sql);
	}
	if (lg.isTraceEnabled()) {
		lg.trace("DbDriver.updatePreparedCleanSQL(): "+sql);
	}
	try (PreparedStatement ps = con.prepareStatement(sql)){
		ps.setBytes(1, data);
		return ps.executeUpdate();
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
		throw new PermissionException("Versionable name="+versionable.getName()+" type="+VersionTable.versionableTypeFromVersionable(versionable)+"\nuser="+versionable.getVersion().getOwner()+" Not Equal to client user="+user);
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
	if(versionable instanceof BioModelMetaData ||
		versionable instanceof MathModelMetaData ||
		versionable instanceof Geometry){
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

	
	KeyValue versionKey = keyFactory.getNewKey(con);
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
public static String varchar2_CLOB_get(ResultSet rset,Field varchar2Field,Field clob_or_text_Field, DatabaseSyntax dbSyntax) throws SQLException,DataAccessException{

	if (clob_or_text_Field.getSqlDataType()!=SQLDataType.clob_text){
		throw new DataAccessException("expecting clobField to be data type "+SQLDataType.clob_text);
	}
	
	//
	// first check the varchar field
	//
	String temp = rset.getString(varchar2Field.getUnqualifiedColName());
	if(rset.wasNull() || temp == null || temp.length() == 0){
		//
		// varchar empty, next try the "CLOB" or "TEXT" field
		//
		switch (dbSyntax){
		case ORACLE:{
			//
			// Oracle uses CLOBs
			//
			temp = (String) DbDriver.getLOB(rset,clob_or_text_Field,dbSyntax);
			if(rset.wasNull() || temp == null || temp.length() == 0){
				return null;
			}else{
				return TokenMangler.getSQLRestoredString(temp);
			}
		}
		case POSTGRES:{
			//
			// Postgres uses TEXT
			//
			temp = rset.getString(clob_or_text_Field.getUnqualifiedColName());
			if(rset.wasNull() || temp == null || temp.length() == 0){
				return null;
			}else{
				return TokenMangler.getSQLRestoredString(temp);
			}
		}
		default:{
			throw new DataAccessException("unexpected DatabaseSyntax "+dbSyntax);
		}
		}
	}else{
		return TokenMangler.getSQLRestoredString(temp);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 2:29:38 PM)
 * @return java.lang.String
 * @param size int
 */
public static boolean varchar2_CLOB_is_Varchar2_OK(String data) {
	return (TokenMangler.getSQLEscapedString(data).length() <= VARCHAR_SIZE_LIMIT);
}


/**
 * Insert the method's description here.
 * Creation date: (8/23/2004 2:29:38 PM)
 * @return java.lang.String
 * @param size int
 */
public final static void varchar2_CLOB_update(
    Connection con,
    String sql,//marked sql
    String data,
    Table targetTable,//Where data gets stored
    KeyValue targetID,//for clob if necessary
    Field targetCLOBField,
    Field targetVarchar2Field,
    DatabaseSyntax dbSyntax)
		throws SQLException,DataAccessException{
	
	if (targetCLOBField.getSqlDataType() != SQLDataType.clob_text){
		throw new DataAccessException("unexpected clob field SQLDataType "+targetCLOBField.getSqlDataType());
	}

    int marker_index;
    if ((marker_index = sql.indexOf(INSERT_CLOB_HERE)) != -1) {
    	switch (dbSyntax){
    	case ORACLE:{
    		//
		    //Store in CLOB
    		//
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
	            targetCLOBField,
	            data,
	            dbSyntax);
	        break;
    	}
    	case POSTGRES:{
    		//
		    //Store in TEXT
    		//
	        StringBuffer sb = new StringBuffer(sql);
	        sb.replace(
	            marker_index,
	            marker_index + INSERT_CLOB_HERE.length(),
	            "?");
	        updatePreparedCleanSQL(con, sb.toString(), data);
	        break;
    	}
    	default:{
    		throw new DataAccessException("unexpected DatabaseSyntax "+dbSyntax);
    	}
    	}
    } else if ((marker_index = sql.indexOf(INSERT_VARCHAR2_HERE)) != -1){
	    //Store in VARCHAR2
        StringBuffer sb = new StringBuffer(sql);
        sb.replace(
            marker_index,
            marker_index + INSERT_VARCHAR2_HERE.length(),
            "'" + TokenMangler.getSQLEscapedString(data) + "'");
        int changed = updateCleanSQL(con, sb.toString());
        if (changed != 1 && lg.isWarnEnabled() ){
        	lg.warn("query " + sb.toString() + " changed " + changed +  " rows"); 
        }
    }else{
	    throw new RuntimeException("Expected charchar2_CLOB Marker Not Found in sql");
    }
}

}
