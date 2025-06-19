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
import org.vcell.util.document.User;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * This type was created in VisualAge.
 */
public class UserIdentityTable extends Table {
    public static final String TABLE_NAME = "vc_useridentity";
    public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    public static final Field userRef = new Field("userRef", SQLDataType.integer, "NOT NULL " + UserTable.REF_TYPE);
    public static final Field authSubject = new Field("authSubject", SQLDataType.varchar_128, "NOT NULL");
    public static final Field authIssuer = new Field("authIssuer", SQLDataType.varchar_128, "NOT NULL");
    public static final Field insertDate	= new Field("insertDate", SQLDataType.date,"NOT NULL");


    public static final UserIdentityTable table = new UserIdentityTable();

    /**
     * ModelTable constructor comment.
     */
    private UserIdentityTable(){
        super(TABLE_NAME);
        Field[] fields = {userRef, authSubject, authIssuer, insertDate};
        addFields(fields);
    }

    public UserIdentity getUserIdentity(ResultSet rset, User user) throws SQLException {
        return getUserIdentity(rset, user, this.id.getUnqualifiedColName());
    }


    public UserIdentity getUserIdentity(ResultSet rset, User user, String idColName) throws SQLException{

        BigDecimal id = rset.getBigDecimal(idColName);
        String subject = rset.getString(authSubject.getUnqualifiedColName());
        String issuer =	rset.getString(authIssuer.getUnqualifiedColName());
        //
        // Format Date
        //
        java.sql.Date DBDate = rset.getDate(insertDate.getUnqualifiedColName());
        java.sql.Time DBTime = rset.getTime(insertDate.getUnqualifiedColName());
        LocalDateTime insertDate =    LocalDateTime.of(DBDate.toLocalDate(), DBTime.toLocalTime());

        return new UserIdentity(id, user, subject, issuer, insertDate);
    }

    public LocalDateTime getUserIdentityDate(ResultSet rset) throws SQLException {
        //
        // Format Date
        //
        java.sql.Date DBDate = rset.getDate(insertDate.getUnqualifiedColName());
        java.sql.Time DBTime = rset.getTime(insertDate.getUnqualifiedColName());
        return LocalDateTime.of(DBDate.toLocalDate(), DBTime.toLocalTime());
    }
}
