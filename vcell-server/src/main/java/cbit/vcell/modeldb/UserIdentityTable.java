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

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * This type was created in VisualAge.
 */
public class UserIdentityTable extends Table {
    public static final String TABLE_NAME = "vc_useridentity";
    public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    public static final Field userRef = new Field("userRef", SQLDataType.integer, "NOT NULL " + UserTable.REF_TYPE);
    public static final Field auth0Subject = new Field("auth0Subject", SQLDataType.varchar_128, "");
    public static final Field keycloakSubject = new Field("keycloakSubject", SQLDataType.varchar_128, "");
    public static final Field insertDate	= new Field("insertDate", SQLDataType.date,"NOT NULL");

    public enum IdentityProvider {
        AUTH0(auth0Subject),
        KEYCLOAK(keycloakSubject);
        IdentityProvider(Field tableColumn){
            this.tableColumn = tableColumn;
        }
        public final Field tableColumn;
    }



    public static final UserIdentityTable table = new UserIdentityTable();

    /**
     * ModelTable constructor comment.
     */
    private UserIdentityTable(){
        super(TABLE_NAME);
        Field[] fields = {userRef, auth0Subject, keycloakSubject, insertDate};
        addFields(fields);
    }


    public UserIdentity getUserIdentity(ResultSet rset, User user, UserIdentityTable.IdentityProvider identityProvider) throws SQLException{

        KeyValue id = 		new KeyValue(rset.getBigDecimal(this.id.toString()));
        String subject =	rset.getString(identityProvider.tableColumn.getQualifiedColName());
        if(subject == null){
            return null;
        }
        //
        // Format Date
        //
        java.sql.Date DBDate = rset.getDate(insertDate.getQualifiedColName());
        java.sql.Time DBTime = rset.getTime(insertDate.getQualifiedColName());
        Date insertDate = null;
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US);
            insertDate = sdf.parse(DBDate + " " + DBTime);
        } catch (java.text.ParseException e) {
            throw new java.sql.SQLException(e.getMessage());
        }

        UserIdentity userIdentity = new UserIdentity(id, user, subject, insertDate);

        return userIdentity;
    }
}
