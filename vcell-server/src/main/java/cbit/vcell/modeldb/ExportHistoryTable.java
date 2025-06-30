package cbit.vcell.modeldb;

import cbit.sql.Field;
import cbit.sql.Table;

public class ExportHistoryTable extends Table{
    private static final String TABLE_NAME = "vc_exporthistory";

    public final Field modelRef				= new Field("modelRef",			Field.SQLDataType.integer,		"NOT NULL "+ModelTable.REF_TYPE);
    public final Field childSummaryLarge	= new Field("childSummaryLRG",	Field.SQLDataType.clob_text,		"");
    public final Field childSummarySmall	= new Field("childSummarySML",	Field.SQLDataType.varchar2_4000,	"");

    private final Field fields[] = {modelRef,childSummaryLarge,childSummarySmall};

    public static final ExportHistoryTable table = new ExportHistoryTable();

    private ExportHistoryTable() {
        super(TABLE_NAME);
        addFields(fields);
    }

}
