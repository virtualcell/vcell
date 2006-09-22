package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.ResultSet;
import java.sql.SQLException;

import cbit.sql.Field;
import cbit.sql.Table;

/**
 * This type was created in VisualAge.
 */
public class UserPreferenceTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_userpref";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field userRef 			= new Field("userRef",				"integer",			"NOT NULL "+UserTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field userPrefKey 		= new Field("userPrefKey",			"varchar(32)",	"NOT NULL");
	public final Field userPrefValue	= new Field("userPrefValue",		"varchar(4000)", "NOT NULL");
	

	private final Field fields[] = {userRef,userPrefKey,userPrefValue};
	
	public static final UserPreferenceTable table = new UserPreferenceTable();

/**
 * ModelTable constructor comment.
 */
private UserPreferenceTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * Insert the method's description here.
 * Creation date: (6/10/2004 4:12:51 PM)
 * @return java.util.Dictionary
 * @param rset java.sql.ResultSet
 */
public cbit.util.Preference[] getUserPreferences(ResultSet rset) throws SQLException{

	java.util.Vector preferenceList = new java.util.Vector();
	while (rset.next()){
		String propKey = rset.getString(UserPreferenceTable.table.userPrefKey.getUnqualifiedColName());
		String propValue = rset.getString(UserPreferenceTable.table.userPrefValue.getUnqualifiedColName());
		preferenceList.add(new cbit.util.Preference(cbit.util.TokenMangler.getSQLRestoredString(propKey),cbit.util.TokenMangler.getSQLRestoredString(propValue)));
	}

	return (cbit.util.Preference[])cbit.util.BeanUtils.getArray(preferenceList,cbit.util.Preference.class);
}
}