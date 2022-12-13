package cbit.vcell.modeldb;

import cbit.sql.Field;
import cbit.sql.Table;

/**
 * create table VC_SIMDELFROMDISK
 * (
 *     DELDATE   VARCHAR2(20),
 *     USERID    VARCHAR2(255) not null,
 *     USERKEY   NUMBER,
 *     SIMID     NUMBER,
 *     SIMPREF   NUMBER,
 *     SIMDATE   VARCHAR2(20),
 *     SIMNAME   VARCHAR2(255) not null,
 *     STATUS    CHAR(10),
 *     NUMFILES  NUMBER,
 *     TOTALSIZE NUMBER(16)
 * )
 */
public class SimDelFromDiskTable extends Table {
    private static final String TABLE_NAME = "vc_simdelfromdisk";
    public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    public final Field delDate 				= new Field("deldate",		Field.SQLDataType.varchar2_20,		"");
    public final Field userid 				= new Field("userid",		Field.SQLDataType.varchar2_255,		"NOT NULL");
    public final Field userkey 				= new Field("userkey",		Field.SQLDataType.integer,			"");
    public final Field simid 				= new Field("simid",		Field.SQLDataType.integer,			"");
    public final Field simpref 				= new Field("simpref",		Field.SQLDataType.integer,			"");
    public final Field simdate 				= new Field("simdate",		Field.SQLDataType.varchar2_20,		"");
    public final Field simname 				= new Field("simname",		Field.SQLDataType.varchar2_255,		"NOT NULL");
    public final Field status 				= new Field("status",		Field.SQLDataType.varchar_10,		"");
    public final Field numfiles 			= new Field("numfiles",		Field.SQLDataType.integer,			"");
    public final Field totalsize 			= new Field("totalsize",	Field.SQLDataType.integer,			"");

    private final Field fields[] = {delDate,userid,userkey,simid,simpref,simdate,simname,status,numfiles,totalsize};

    public static final SimDelFromDiskTable table = new SimDelFromDiskTable();

    /**
     * ModelTable constructor comment.
     */
    private SimDelFromDiskTable() {
        super(TABLE_NAME);
        boolean bRemoveIdField = true;
        addFields(fields, bRemoveIdField);
    }

}
