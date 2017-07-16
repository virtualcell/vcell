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

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.vcell.pub.Publication;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import cbit.vcell.modeldb.DatabaseServerImpl.OrderBy;
/**
 * This type was created in VisualAge.
 */
public class PublicationTable extends Table {
	private static final String TABLE_NAME = "vc_publication";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field title				= new Field("title",			SQLDataType.varchar2_4000,	"");
	public final Field authors				= new Field("authors",			SQLDataType.varchar2_4000,	"");
	public final Field year					= new Field("year",				SQLDataType.integer,		"");
	public final Field citation				= new Field("citation",			SQLDataType.varchar2_4000,	"");
	public final Field pubmedid				= new Field("pubmedid",			SQLDataType.varchar2_64,	"");
	public final Field doi					= new Field("doi",				SQLDataType.varchar2_128,	"");
	public final Field endnoteid			= new Field("endnoteid",		SQLDataType.integer,		"");
	public final Field url					= new Field("url",				SQLDataType.varchar2_128,	"");
	public final Field wittid				= new Field("wittid",			SQLDataType.integer,		"");
	public final Field pubdate				= new Field("pubDate",			SQLDataType.date,			"");
	
	private final Field fields[] = {title,authors,year,citation,pubmedid,doi,endnoteid,url,wittid,pubdate };
	
	public static final PublicationTable table = new PublicationTable();

/**
 * ModelTable constructor comment.
 */
private PublicationTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.GeometryInfo
 * @param rset java.sql.ResultSet
 * @param log cbit.vcell.server.SessionLog
 * @throws MalformedURLException 
 */
public Publication getInfo(ResultSet rset,Connection con,SessionLog log) throws SQLException,DataAccessException, MalformedURLException {
	
	KeyValue key = new KeyValue(rset.getBigDecimal(PublicationTable.table.id.toString()));
	String title = rset.getString(PublicationTable.table.title.toString());
	String authorsString = rset.getString(PublicationTable.table.authors.toString());
	String[] authors = authorsString.split(";");
	String citation = rset.getString(PublicationTable.table.citation.toString());
	String pubmedid = rset.getString(PublicationTable.table.pubmedid.toString());
	String doi = rset.getString(PublicationTable.table.doi.toString());
	String url = rset.getString(PublicationTable.table.url.toString());
	
	return new Publication(key, title, authors, citation, pubmedid, doi, url);
}

public String getPreparedStatement_PublicationReps(String conditions, OrderBy orderBy){

	UserTable userTable = UserTable.table;
	BioModelTable biomodelTable = BioModelTable.table;
	MathModelTable mathmodelTable = MathModelTable.table;
	PublicationTable pubTable = PublicationTable.table;
	PublicationModelLinkTable pubModelTable = PublicationModelLinkTable.table;
	
	String subquery = 			
		"select " +
		    "distinct "+pubTable.id.getQualifiedColName()+", "+
		    pubTable.title.getQualifiedColName()+", "+
		    pubTable.authors.getQualifiedColName()+", "+
		    pubTable.year.getQualifiedColName()+", "+
		    pubTable.citation.getQualifiedColName()+", "+
		    pubTable.pubmedid.getQualifiedColName()+", "+
		    pubTable.doi.getQualifiedColName()+", "+
		    pubTable.endnoteid.getQualifiedColName()+", "+
		    pubTable.url.getQualifiedColName()+", "+
		    pubTable.wittid.getQualifiedColName()+", "+
		    pubTable.pubdate.getQualifiedColName()+", "+
		
		   "(select '['||wm_concat("+"SQ1_"+biomodelTable.id.getQualifiedColName()+"||';'"+
		   						"||"+"SQ1_"+biomodelTable.name.getQualifiedColName()+"||';'"+
		   						"||"+"SQ1_"+userTable.id.getQualifiedColName()+"||';'"+
		   						"||"+"SQ1_"+userTable.userid.getQualifiedColName()+")||']' "+
		   "   from "+pubModelTable.getTableName()+" SQ1_"+pubModelTable.getTableName()+", "+
		              biomodelTable.getTableName()+" SQ1_"+biomodelTable.getTableName()+", "+
		              userTable.getTableName()+" SQ1_"+userTable.getTableName()+" "+
		     " where "+
	          "SQ1_"+pubModelTable.pubRef.getQualifiedColName()+" = "+pubTable.id.getQualifiedColName()+" and  "+
	          "SQ1_"+pubModelTable.bioModelRef.getQualifiedColName()+" = SQ1_"+biomodelTable.id.getQualifiedColName()+" and "+
	          "SQ1_"+userTable.id.getQualifiedColName()+" = SQ1_"+biomodelTable.ownerRef.getQualifiedColName()+") bmRefs,  "+
		
		   "(select '['||wm_concat("+"SQ2_"+mathmodelTable.id.getQualifiedColName()+"||';'"+
								"||"+"SQ2_"+mathmodelTable.name.getQualifiedColName()+"||';'"+
								"||"+"SQ2_"+userTable.id.getQualifiedColName()+"||';'"+
								"||"+"SQ2_"+userTable.userid.getQualifiedColName()+")||']' "+
		   "   from "+pubModelTable.getTableName()+" SQ2_"+pubModelTable.getTableName()+", "+
		              mathmodelTable.getTableName()+" SQ2_"+mathmodelTable.getTableName()+", "+
		              userTable.getTableName()+" SQ2_"+userTable.getTableName()+" "+
		     " where "+
	          "SQ2_"+pubModelTable.pubRef.getQualifiedColName()+" = "+pubTable.id.getQualifiedColName()+" and  "+
	          "SQ2_"+pubModelTable.mathModelRef.getQualifiedColName()+" = SQ2_"+mathmodelTable.id.getQualifiedColName()+" and "+
	          "SQ2_"+userTable.id.getQualifiedColName()+" = SQ2_"+mathmodelTable.ownerRef.getQualifiedColName()+") mmRefs "+
						
		"from "+pubTable.getTableName()+" "+
		"LEFT OUTER JOIN "+pubModelTable.getTableName()+" "+
		"ON "+pubTable.id.getQualifiedColName()+" = "+pubModelTable.pubRef.getQualifiedColName()+" ";
	
	String additionalConditionsClause = "";
	if (conditions!=null && conditions.length()>0){
		additionalConditionsClause += " where ("+conditions+") ";
	}
	
	String orderByClause = "order by "+pubTable.title.getQualifiedColName()+" ASC";
	if (orderBy!=null){
		switch (orderBy){
		case year_asc:{
			orderByClause = "order by "+pubTable.year.getQualifiedColName()+" ASC";
			break;
		}
		case year_desc:{
			orderByClause = "order by "+pubTable.year.getQualifiedColName()+" DESC";
			break;
		}
		}
	}

	// query guarantees authorized access to biomodels based on the supplied User authentication.
	String sql = subquery + additionalConditionsClause + orderByClause;
	
	System.out.println(sql);
	return sql;
}

public void setPreparedStatement_PublicationReps(PreparedStatement stmt, User user) throws SQLException{
	if (user == null) {
		throw new IllegalArgumentException("Improper parameters for getBioModelRepsSQL");
	}
}

public PublicationRep getPublicationRep(User user, ResultSet rset) throws IllegalArgumentException, SQLException {
	
	KeyValue pubKey = new KeyValue(rset.getBigDecimal(table.id.toString()));
	String title = rset.getString(table.title.toString());
	String authorsList = rset.getString(table.authors.toString());
	Integer year = rset.getInt(table.year.toString());
	String citation = rset.getString(table.citation.toString());
	String pubmedid = rset.getString(table.pubmedid.toString());
	String doi = rset.getString(table.doi.toString());
	String endnoteid = rset.getString(table.endnoteid.toString());
	String url = rset.getString(table.url.toString());
	String wittid = rset.getString(table.wittid.toString());
	java.util.Date pubdate = VersionTable.getDate(rset,table.pubdate.toString());
	
	String bmRefsString = rset.getString("bmRefs");
	ArrayList<BioModelReferenceRep> bmRefList = new ArrayList<BioModelReferenceRep>();
	String[] bmRefStrings = bmRefsString.replace("[", "").replace("]", "").split(",");
	for (String bmRefString : bmRefStrings) {
		String bmRefComponents[] = bmRefString.split(";");
		if (bmRefComponents.length==4){
			KeyValue bmKey = new KeyValue(bmRefComponents[0]);
			String bmName = bmRefComponents[1];
			KeyValue ownerKey = new KeyValue(bmRefComponents[2]);
			String ownerUserid = bmRefComponents[3];
			bmRefList.add(new BioModelReferenceRep(bmKey, bmName, new User(ownerUserid,ownerKey)));
		}
	}
	BioModelReferenceRep[] bmRefArray = bmRefList.toArray(new BioModelReferenceRep[0]);
	
	String mmRefsString = rset.getString("mmRefs");
	ArrayList<MathModelReferenceRep> mmRefList = new ArrayList<MathModelReferenceRep>();
	String[] mmRefStrings = mmRefsString.replace("[", "").replace("]", "").split(",");
	for (String mmRefString : mmRefStrings) {
		String mmRefComponents[] = mmRefString.split(";");
		if (mmRefComponents.length==4){
			KeyValue mmKey = new KeyValue(mmRefComponents[0]);
			String mmName = mmRefComponents[1];
			KeyValue ownerKey = new KeyValue(mmRefComponents[2]);
			String ownerUserid = mmRefComponents[3];
			mmRefList.add(new MathModelReferenceRep(mmKey, mmName, new User(ownerUserid,ownerKey)));
		}
	}
	MathModelReferenceRep[] mmRefArray = mmRefList.toArray(new MathModelReferenceRep[0]);
	
	
	return new PublicationRep(pubKey,title,authorsList.split(";"),year,citation,pubmedid,doi,endnoteid,url,bmRefArray,mmRefArray,wittid,pubdate);
}

}
