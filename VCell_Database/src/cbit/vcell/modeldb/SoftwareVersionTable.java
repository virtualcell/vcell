package cbit.vcell.modeldb;
import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
/**
 * Insert the type's description here.
 * Creation date: (5/4/2005 6:27:59 AM)
 * @author: Frank Morgan
 */
public class SoftwareVersionTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_softwareversion";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field versionableRef		= new Field("versionableRef",		"integer",		"UNIQUE NOT NULL");
	public final Field softwareVersion		= new Field("softwareVersion",		"varchar2(32)",	"NOT NULL");

	private final Field fields[] = {versionableRef,softwareVersion};
	
	public static final SoftwareVersionTable table = new SoftwareVersionTable();

/**
 * SoftwareVersionTable constructor comment.
 * @param argTableName java.lang.String
 */
protected SoftwareVersionTable() {
	super(TABLE_NAME);
	addFields(fields);

}


/**
 * Insert the method's description here.
 * Creation date: (5/4/2005 6:51:42 AM)
 */
public String getSQLValueList(KeyValue newVersionKey) {

	String softwareVersionS =
		org.vcell.util.PropertyLoader.getRequiredProperty(org.vcell.util.PropertyLoader.vcellSoftwareVersion);

	softwareVersionS = org.vcell.util.TokenMangler.getSQLEscapedString(softwareVersionS);
	
	return "("+Table.NewSEQ+","+newVersionKey.toString()+",'"+softwareVersionS+"')";
}
}