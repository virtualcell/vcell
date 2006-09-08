package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class SimStatTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_simstat";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simRef				= new Field("simRef",				"integer",		"NOT NULL "+SimulationTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field equiv				= new Field("equiv",				"number",		"");
	public final Field status				= new Field("status",				"varchar2(255)","");
	public final Field curatorEquiv			= new Field("curatorEquiv",			"integer",		"");
	public final Field comments				= new Field("comments",				"varchar2(255)","");

	private final Field fields[] = {simRef, equiv, status, curatorEquiv, comments };
	
	public static final SimStatTable table = new SimStatTable();

/**
 * ModelTable constructor comment.
 */
private SimStatTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}