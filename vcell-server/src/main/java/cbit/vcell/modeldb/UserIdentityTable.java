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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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

    public static Field getIdentityField(IdentityProvider identityProvider) {
        switch (identityProvider) {
            case AUTH0:
                return auth0Subject;
            case KEYCLOAK:
                return keycloakSubject;
            default:
                throw new IllegalArgumentException("Unknown identity provider: " + identityProvider);
        }
    }

    public static Field[] getIdentityFields() {
        return new Field[] {auth0Subject, keycloakSubject};
    }

    public enum IdentityProvider {
        AUTH0,
        KEYCLOAK;
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

    public UserIdentity getUserIdentity(ResultSet rset, User user, UserIdentityTable.IdentityProvider identityProvider) throws SQLException {
        return getUserIdentity(rset, user, identityProvider, this.id.getUnqualifiedColName());
    }


    public UserIdentity getUserIdentity(ResultSet rset, User user, UserIdentityTable.IdentityProvider identityProvider, String idColName) throws SQLException{

        BigDecimal id = rset.getBigDecimal(idColName);
        Field identityColumn = getIdentityField(identityProvider);
        String subject =	rset.getString(identityColumn.getUnqualifiedColName());
        if(subject == null || id == null){
            return null;
        }
        //
        // Format Date
        //
        java.sql.Date DBDate = rset.getDate(insertDate.getUnqualifiedColName());
        java.sql.Time DBTime = rset.getTime(insertDate.getUnqualifiedColName());
        LocalDateTime insertDate =    LocalDateTime.of(DBDate.toLocalDate(), DBTime.toLocalTime());

        UserIdentity userIdentity = new UserIdentity(id, user, subject, insertDate);

        return userIdentity;
    }
}
