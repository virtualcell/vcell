package cbit.vcell.modeldb;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.sql.*;
/**
 * This type was created in VisualAge.
 */
public class SimContextStat2Table extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_simcontextstat2";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simContextRef		= new Field("simContextRef",		"integer",		"NOT NULL "+SimContextTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field hasData				= new Field("hasData",				"number",		"");
	public final Field equiv				= new Field("equiv",				"number",		"");
	public final Field status				= new Field("status",				"varchar2(255)","");
	public final Field curatorEquiv			= new Field("curatorEquiv",			"integer",		"");
	public final Field comments				= new Field("comments",				"varchar2(255)","");

	private final Field fields[] = {simContextRef, hasData, equiv, status, curatorEquiv, comments };
	
	public static final SimContextStat2Table table = new SimContextStat2Table();

/**
 * ModelTable constructor comment.
 */
private SimContextStat2Table() {
	super(TABLE_NAME);
	addFields(fields);
}
}