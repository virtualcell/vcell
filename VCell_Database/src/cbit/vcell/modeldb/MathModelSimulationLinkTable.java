package cbit.vcell.modeldb;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.sql.ResultSet;
import java.sql.SQLException;

import org.vcell.util.document.KeyValue;

import cbit.sql.Field;
import cbit.sql.Table;
/**
 * This type was created in VisualAge.
 */
public class MathModelSimulationLinkTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_mathmodelsim";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field mathModelRef 		= new Field("mathmodelRef",		"integer",	"NOT NULL "+MathModelTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field simRef			= new Field("simRef",			"integer",	"NOT NULL "+SimulationTable.REF_TYPE);

	private final Field fields[] = {mathModelRef,simRef};
	
	public static final MathModelSimulationLinkTable table = new MathModelSimulationLinkTable();
/**
 * ModelTable constructor comment.
 */
private MathModelSimulationLinkTable() {
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
public String getSQLValueList(KeyValue key, KeyValue mathModelKey, KeyValue simulationKey) {

	StringBuffer buffer = new StringBuffer();
	buffer.append("(");
	buffer.append(key+",");
	buffer.append(mathModelKey+",");
	buffer.append(simulationKey+")");

	return buffer.toString();
}
}
