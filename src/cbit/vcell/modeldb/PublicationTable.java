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
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.vcell.pub.Publication;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import sun.security.action.GetIntegerAction;
import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.modeldb.DatabaseServerImpl.OrderBy;
/**
 * This type was created in VisualAge.
 */
public class PublicationTable extends Table {
	private static final String TABLE_NAME = "vc_publication";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field title				= new Field("title",			"VARCHAR2(4000)",	"");
	public final Field authors				= new Field("authors",			"VARCHAR2(4000)",	"");
	public final Field year					= new Field("year",				"integer",			"");
	public final Field citation				= new Field("citation",			"VARCHAR2(4000)",	"");
	public final Field pubmedid				= new Field("pubmedid",			"VARCHAR2(64)",		"");
	public final Field doi					= new Field("doi",				"VARCHAR2(128)",	"");
	public final Field endnoteid			= new Field("endnoteid",		"Integer",			"");
	public final Field url					= new Field("url",				"VARCHAR2(128)",	"");
	public final Field wittid				= new Field("wittid",			"integer",			"");
	
	private final Field fields[] = {title,authors,year,citation,pubmedid,doi,endnoteid,url,wittid };
	
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
		
		   "(select '['||wm_concat("+"SQ1_"+pubModelTable.bioModelRef.getQualifiedColName()+")||']' "+
		   "   from "+pubModelTable.getTableName()+" SQ1_"+pubModelTable.getTableName()+" "+
		   "   where SQ1_"+pubModelTable.pubRef.getQualifiedColName()+" = "+pubTable.id.getQualifiedColName()+") bmKeys,  "+
		
		   "(select '['||wm_concat("+"SQ2_"+pubModelTable.mathModelRef.getQualifiedColName()+")||']' "+
		   "   from "+pubModelTable.getTableName()+"  SQ2_"+pubModelTable.getTableName()+" "+
		   "   where SQ2_"+pubModelTable.mathModelRef.getQualifiedColName()+ " = " + pubTable.id.getQualifiedColName()+") mmKeys  "+
				
		"from "+pubTable.getTableName()+", "+pubModelTable.getTableName()+" ";
	
	String additionalConditionsClause = "where "+pubTable.id.getQualifiedColName()+" = "+pubModelTable.pubRef.getQualifiedColName()+" ";
	if (conditions!=null && conditions.length()>0){
		additionalConditionsClause += " and ("+conditions+") ";
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
	
	String bmKeysString = rset.getString("bmKeys");
	ArrayList<KeyValue> bmKeyList = new ArrayList<KeyValue>();
	String[] bmKeys = bmKeysString.replace("[", "").replace("]", "").split(",");
	for (String bmKey : bmKeys) {
		if (bmKey!=null && bmKey.length()>0){
			bmKeyList.add(new KeyValue(bmKey));
		}
	}
	KeyValue[] bmKeyArray = bmKeyList.toArray(new KeyValue[0]);
	
	String mmKeysString = rset.getString("mmKeys");
	ArrayList<KeyValue> mmKeyList = new ArrayList<KeyValue>();
	String[] mmKeys = mmKeysString.replace("[", "").replace("]", "").split(",");
	for (String mmKey : mmKeys) {
		if (mmKey!=null && mmKey.length()>0){
			mmKeyList.add(new KeyValue(mmKey));
		}
	}
	KeyValue[] mmKeyArray = mmKeyList.toArray(new KeyValue[0]);		
	
	return new PublicationRep(pubKey,title,authorsList.split(";"),year,citation,pubmedid,doi,endnoteid,url,bmKeyArray);
}

}
