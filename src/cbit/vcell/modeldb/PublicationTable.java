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
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.pub.Publication;
import org.vcell.util.DataAccessException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
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
	public final Field endnodeid			= new Field("endnoteid",		"Integer",			"");
	public final Field url					= new Field("url",				"VARCHAR2(128)",	"");
	public final Field wittid				= new Field("wittid",			"integer",			"");
	
	private final Field fields[] = {title,authors,year,citation,pubmedid,doi,endnodeid,url,wittid };
	
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
	
	return new Publication(key, title, authors, citation, pubmedid, doi);
}


}
