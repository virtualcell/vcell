package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.ResultSet;
import java.sql.SQLException;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.util.document.KeyValue;
/**
 * This type was created in VisualAge.
 */
public class BioModelSimulationLinkTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_biomodelsim";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field bioModelRef 		= new Field("biomodelRef",		"integer",	"NOT NULL "+BioModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field simRef			= new Field("simRef",			"integer",	"NOT NULL "+SimulationTable.REF_TYPE);

	private final Field fields[] = {bioModelRef,simRef};
	
	public static final BioModelSimulationLinkTable table = new BioModelSimulationLinkTable();
/**
 * ModelTable constructor comment.
 */
private BioModelSimulationLinkTable() {
	super(TABLE_NAME);
	addFields(fields);
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/00 8:08:50 AM)
 * @return cbit.sql.KeyValue
 * @param rset java.sql.ResultSet
 */
public KeyValue getSimulationKey(ResultSet rset) throws SQLException {
	
	KeyValue key = new KeyValue(rset.getBigDecimal(table.simRef.getUnqualifiedColName()));
	if (rset.wasNull()){
		key = null;
	}
	return key;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, KeyValue bioModelKey, KeyValue simulationKey) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append(bioModelKey+",");
	buffer.append(simulationKey+")");

	return buffer.toString();
}
}
