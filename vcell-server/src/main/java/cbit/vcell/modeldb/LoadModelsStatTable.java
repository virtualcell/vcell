package cbit.vcell.modeldb;

import cbit.sql.Field;
import cbit.sql.Table;

public class LoadModelsStatTable extends Table {
    private static final String TABLE_NAME = "loadmodelstat";
    public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    private static final String[] loadModelsStatTableConstraintOracle =
            new String[]{
                    "ldmdlstat_only_1 CHECK(" +
                            "DECODE(bioModelRef,NULL,0,bioModelRef,1)+" +
                            "DECODE(mathModelRef,NULL,0,mathModelRef,1) = 1)"
            };

    private static final String[] loadModelsStatTableConstraintPostgres =
            new String[]{
                    "ldmdlstat_only_1 CHECK(" +
                            "(CASE WHEN bioModelRef IS NULL THEN 0 ELSE 1 END)+" +
                            "(CASE WHEN mathModelRef IS NULL THEN 0 ELSE 1 END) = 1)"
            };

    public static final int RESULTFLAG_SUCCESS = 0;
    public static final int RESULTFLAG_FAILURE = 1;
    public static final int MAX_ERROR_MSG_SIZE = 255; // see varchar2_255 below
    public static final int SOFTWARE_VERS_SIZE = 32;  // see varchar2_32 below

    public final Field bioModelRef = new Field("bioModelRef", Field.SQLDataType.integer, BioModelTable.REF_TYPE + " ON DELETE CASCADE");
    public final Field mathModelRef = new Field("mathModelRef", Field.SQLDataType.integer, MathModelTable.REF_TYPE + " ON DELETE CASCADE");
    public final Field resultFlag = new Field("resultFlag", Field.SQLDataType.integer, "");
    public final Field errorMessage = new Field("errorMessage", Field.SQLDataType.varchar2_255, "");
    public final Field timeStamp = new Field("timeStamp", Field.SQLDataType.varchar2_32, "NOT NULL");
    public final Field loadTime = new Field("loadTime", Field.SQLDataType.integer, "");
    public final Field softwareVers = new Field("softwareVers", Field.SQLDataType.varchar2_32, "NOT NULL");
    public final Field loadOriginalXMLTime = new Field("loadOriginalXMLTime", Field.SQLDataType.integer, "");
    public final Field loadUnresolvedTime = new Field("loadUnresolvedTime", Field.SQLDataType.integer, "");
    public final Field bSameCachedAndNotCachedXML = new Field("bSameCachedAndNotCachedXML", Field.SQLDataType.integer, "");
    public final Field bSameCachedAndNotCachedObj = new Field("bSameCachedAndNotCachedObj", Field.SQLDataType.integer, "");
    public final Field bSameSelfXMLCachedRoundtrip = new Field("bSameSelfXMLCachedRoundtrip", Field.SQLDataType.integer, "");
    public final Field bSameCachedAndNotCachedXMLExc = new Field("bSameCachedAndNotCachedXMLExc", Field.SQLDataType.varchar2_255, "");
    public final Field bSameCachedAndNotCachedObjExc = new Field("bSameCachedAndNotCachedObjExc", Field.SQLDataType.varchar2_255, "");
    public final Field bSameSelfXMLCachedRoundtripExc = new Field("bSameSelfXMLCachedRoundtripExc", Field.SQLDataType.varchar2_255, "");
    public final Field bIssuesErrors = new Field("bIssuesErrors", Field.SQLDataType.varchar2_255, "");

    private final Field[] fields = new Field[]{
            bioModelRef, mathModelRef, resultFlag, errorMessage, timeStamp, loadTime, softwareVers,
            loadOriginalXMLTime, loadUnresolvedTime,
            bSameCachedAndNotCachedXML, bSameCachedAndNotCachedObj, bSameSelfXMLCachedRoundtrip,
            bSameCachedAndNotCachedXMLExc, bSameCachedAndNotCachedObjExc, bSameSelfXMLCachedRoundtripExc,
            bIssuesErrors};

    public static final LoadModelsStatTable table = new LoadModelsStatTable();

    /**
     * ModelTable constructor comment.
     */
    private LoadModelsStatTable() {
        super(TABLE_NAME, loadModelsStatTableConstraintOracle, loadModelsStatTableConstraintPostgres);
        addFields(fields);
    }

}
