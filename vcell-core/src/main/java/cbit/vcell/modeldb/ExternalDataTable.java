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
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.util.DataAccessException;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.ExternalDataIdentifier;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class ExternalDataTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_externaldata";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] fieldDataTableConstraintOracle = new String[] {"own_fdn_unique UNIQUE("+VersionTable.ownerRef_ColumnName+",externaldataname)"};

    private static final String[] fieldDataTableConstraintPostgres = new String[] {"own_fdn_unique UNIQUE("+VersionTable.ownerRef_ColumnName+",externaldataname)"};

    public final Field ownerRef 		= new Field(VersionTable.ownerRef_ColumnName,	SQLDataType.integer,		"NOT NULL " + UserTable.REF_TYPE);
	public final Field externalDataName	= new Field("externaldataname",					SQLDataType.varchar_255,	"NOT NULL");
	public final Field annotation		= new Field("annotation",						SQLDataType.varchar_1024,	"");
	
	private final Field fields[] = {ownerRef, externalDataName,annotation};
	    			
	public static final ExternalDataTable table = new ExternalDataTable();

/**
 * ModelTable constructor comment.
 */
private ExternalDataTable() {
	super(TABLE_NAME,fieldDataTableConstraintOracle,fieldDataTableConstraintPostgres);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key,org.vcell.util.document.User owner,String name,String argAnnot)throws DataAccessException {

    StringBuffer buffer = new StringBuffer();
    buffer.append("(");
    buffer.append(key + ",");
    buffer.append(owner.getID() + ",");
    buffer.append("'" + name + "',");
    buffer.append(
    		(argAnnot == null || argAnnot.length()==0?"NULL":"'"+TokenMangler.getSQLEscapedString(argAnnot)+"'")+
    		")");

    return buffer.toString();
}

public String getExternalDataAnnot(ResultSet rset) throws SQLException{
	String annotStr = rset.getString(ExternalDataTable.table.annotation.getUnqualifiedColName());
	if(rset.wasNull() || annotStr == null){
		return "";
	}
	return TokenMangler.getSQLRestoredString(annotStr);
}

public org.vcell.util.document.ExternalDataIdentifier getExternalDataIdentifier(ResultSet rset) throws SQLException{
	
	KeyValue extDataIDKey = new KeyValue(rset.getBigDecimal(ExternalDataTable.table.id.getUnqualifiedColName()));
	String externalDataName = rset.getString(ExternalDataTable.table.externalDataName.getUnqualifiedColName());
	String sourceUserID = rset.getString(UserTable.table.userid.getUnqualifiedColName());
	KeyValue sourceUserKey = new KeyValue(rset.getBigDecimal(ExternalDataTable.table.ownerRef.getUnqualifiedColName()));
	User sourceUser = new User(sourceUserID,sourceUserKey);
//	String annot = rset.getString(ExternalDataTable.table.annotation.getUnqualifiedColName());
	return  new ExternalDataIdentifier(extDataIDKey,sourceUser,externalDataName);
}

}
