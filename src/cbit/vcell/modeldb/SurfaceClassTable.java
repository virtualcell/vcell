package cbit.vcell.modeldb;

import cbit.sql.Field;
import cbit.sql.Table;

public class SurfaceClassTable extends Table {
	private static final String TABLE_NAME = "vc_surfaceclass";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field name		 		= new Field("name",				"varchar(255)",	"NOT NULL");
	public final Field geometryRef		= new Field("geometryRef",		"integer",		"NOT NULL "+GeometryTable.REF_TYPE+" ON DELETE CASCADE");
	public final Field subVolumeRef1	= new Field("subVolumeRef1",	"integer",	SubVolumeTable.REF_TYPE);
	public final Field subVolumeRef2	= new Field("subVolumeRef2",	"integer",	SubVolumeTable.REF_TYPE);
	
	private final Field fields[] = {name,geometryRef,subVolumeRef1,subVolumeRef2};
	
	public static final SurfaceClassTable table = new SurfaceClassTable();
/**
 * ModelTable constructor comment.
 */
private SurfaceClassTable() {
	super(TABLE_NAME);
	addFields(fields);
}
}
