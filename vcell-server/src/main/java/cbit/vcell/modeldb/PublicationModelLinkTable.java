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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.vcell.pub.Publication;
import org.vcell.pub.PublishedModels;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class PublicationModelLinkTable extends Table {
	private static final String TABLE_NAME = "vc_publicationmodellink";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field pubRef				= new Field("pubRef",			SQLDataType.integer,	"NOT NULL "+PublicationTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field bioModelRef	 		= new Field("biomodelRef",		SQLDataType.integer,	" "+BioModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field mathModelRef	 		= new Field("mathmodelRef",		SQLDataType.integer,	" "+MathModelTable.REF_TYPE+" ON DELETE CASCADE");
	
	private final Field fields[] = {pubRef, bioModelRef, mathModelRef };
	
	public static final PublicationModelLinkTable table = new PublicationModelLinkTable();

/**
 * ModelTable constructor comment.
 */
private PublicationModelLinkTable() {
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
public PublishedModels getInfo(ResultSet rset, Connection con, Publication vcellPub) throws SQLException,DataAccessException, MalformedURLException {
	
	ArrayList<KeyValue> bioModelKeys = new ArrayList<KeyValue>();
	ArrayList<KeyValue> mathModelKeys = new ArrayList<KeyValue>();
	while (rset.next()){
		KeyValue pubRef = new KeyValue(rset.getBigDecimal(PublicationModelLinkTable.table.pubRef.toString()));
		if (pubRef.compareEqual(vcellPub.key)){
			throw new DataAccessException("unexpected publication key");
		}
		BigDecimal bmRefDecimal = rset.getBigDecimal(PublicationModelLinkTable.table.bioModelRef.toString());
		if (!rset.wasNull()){
			bioModelKeys.add(new KeyValue(bmRefDecimal));
		}
		BigDecimal mmRefDecimal = rset.getBigDecimal(PublicationModelLinkTable.table.mathModelRef.toString());
		if (!rset.wasNull()){
			mathModelKeys.add(new KeyValue(mmRefDecimal));
		}
	}	
	return new PublishedModels(vcellPub, bioModelKeys.toArray(new KeyValue[0]), mathModelKeys.toArray(new KeyValue[0]));
}


}
