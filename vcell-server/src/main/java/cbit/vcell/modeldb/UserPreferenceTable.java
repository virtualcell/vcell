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
import java.util.Vector;

import org.vcell.util.BeanUtils;
import org.vcell.util.Preference;
import org.vcell.util.TokenMangler;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;

/**
 * This type was created in VisualAge.
 */
public class UserPreferenceTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_userpref";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field userRef 			= new Field("userRef",		SQLDataType.integer,		"NOT NULL "+UserTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field userPrefKey 		= new Field("userPrefKey",	SQLDataType.varchar_128,	"NOT NULL");
	public final Field userPrefValue	= new Field("userPrefValue",SQLDataType.varchar_4000,	"NOT NULL");


	public static final UserPreferenceTable table = new UserPreferenceTable();

	/**
	 * ModelTable constructor comment.
	 */
	private UserPreferenceTable() {
		super(TABLE_NAME);
		Field[] fields = {userRef, userPrefKey, userPrefValue};
		addFields(fields);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (6/10/2004 4:12:51 PM)
	 * @return java.util.Dictionary
	 * @param rset java.sql.ResultSet
	 */
	public Preference[] getUserPreferences(ResultSet rset) throws SQLException{

		Vector<Preference> preferenceList = new Vector<>();
		while (rset.next()){
			String propKey = rset.getString(UserPreferenceTable.table.userPrefKey.getUnqualifiedColName());
			String propValue = rset.getString(UserPreferenceTable.table.userPrefValue.getUnqualifiedColName());
			preferenceList.add(
				new Preference(
					TokenMangler.getSQLRestoredString(propKey),TokenMangler.getSQLRestoredString(propValue)));
		}

		return preferenceList.toArray(Preference[]::new);
	}
}
