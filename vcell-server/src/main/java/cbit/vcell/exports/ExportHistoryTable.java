package cbit.vcell.exports;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.export.server.ExportEnums;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.modeldb.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import java.sql.*;
import java.util.StringJoiner;

public class ExportHistoryTable extends Table {
    private static final String TABLE_NAME = "vc_simulation_export_history";

   // public final Field id = new Field(id_ColumnName, Field.SQLDataType.integer,"PRIMARY KEY");

    public final Field jobId = new Field("job_id", Field.SQLDataType.integer, "NOT NULL");

    public final Field userRef = new Field("user_ref", Field.SQLDataType.integer, "NOT NULL " + UserTable.REF_TYPE);
    public final Field bioModelRef = new Field("biomodel_ref", Field.SQLDataType.integer, BioModelTable.REF_TYPE);
    public final Field mathModelRef = new Field("mathmodel_ref", Field.SQLDataType.integer, MathModelTable.REF_TYPE);
    public final Field simulationRef = new Field("simulation_ref", Field.SQLDataType.integer, "NOT NULL " + SimulationTable.REF_TYPE);
    public final Field mathRef = new Field("math_ref", Field.SQLDataType.integer, "NOT NULL " + MathDescTable.REF_TYPE);


    public final Field exportFormat = new Field("export_format", Field.SQLDataType.varchar_50, "NOT NULL");
    public final Field exportDate = new Field("export_date", Field.SQLDataType.date, "NOT NULL");
    public final Field uri = new Field("uri", Field.SQLDataType.varchar_1024, "NOT NULL");

    public final Field variables = new Field("variables", Field.SQLDataType.varchar2_4000, "[] NOT NULL");
    public final Field startTime = new Field("start_time", Field.SQLDataType.number_as_real, "NOT NULL");
    public final Field endTime = new Field("end_time", Field.SQLDataType.number_as_real, "NOT NULL");
    public final Field eventStatus = new Field("event_status", Field.SQLDataType.varchar_50, "NOT NULL");


    //public final Field insertDate = new Field("insert_date", Field.SQLDataType.date, "NOT NULL DEFAULT CURRENT_TIMESTAMP");


//    private final Field fields[] = {jobId, userRef, modelRef, exportFormat, exportDate, uri,
//            dataId, simulationName, applicationName, biomodelName, variables,
//            startTime, endTime, savedFileName, applicationType, nonSpatial,
//            zSlices, tSlices, numVariables};

    public static final ExportHistoryTable table = new ExportHistoryTable();

    private ExportHistoryTable() {
        super(TABLE_NAME);
        // Minus id, which is added by the table abstract class
        // note that primary key id cannot be placed in fields (will causes class initialization error),
        Field[] customFields = {
                // note that primary key id cannot be placed in fields (will causes class initialization error),
                jobId, userRef, bioModelRef, mathModelRef, simulationRef, mathRef,
                exportFormat, exportDate, uri, variables,
                startTime, endTime, eventStatus
        };
        addFields(customFields);
    }

    public String getInsertSQL() {

        StringJoiner cols = new StringJoiner(",", "(", ")");
        Field[] fieldsToInsert = getFields();
        for (Field f : fieldsToInsert) {
            cols.add(f.getUnqualifiedColName());
        }

        StringJoiner ph = new StringJoiner(",", "(", ")");
        for (int i = 0; i < fieldsToInsert.length; i++) {
            ph.add("?");
        }
        return "INSERT INTO " + TABLE_NAME + " " + cols.toString() + " VALUES " + ph.toString();
    }

    public void bindForInsert(
            PreparedStatement ps,
            KeyValue keyValue,
            long        jobIdValue,
            int          userRefValue,
            Integer         bioModelRef,
            Integer         mathModelRef,
            int          simulationRef,
            int         mathRef,
            ExportFormat  fmt,
            Timestamp     exportDateValue,
            String        uriValue,
            Array      variables,
            double      startTimeValue,
            double      endTimeValue,
            String        eventStatusValue
    ) throws SQLException {
        int i = 1;
        ps.setInt     (i++,  Integer.parseInt(keyValue.toString()));
        ps.setLong    (i++, jobIdValue);
        ps.setLong       (i++, userRefValue);
        if (bioModelRef != null) {ps.setLong(i++, bioModelRef);} else {
            ps.setNull(i++, java.sql.Types.INTEGER);
        }
        if (mathModelRef != null) {ps.setLong(i++, mathModelRef);} else {
            ps.setNull(i++, java.sql.Types.INTEGER);
        }
        ps.setLong       (i++, simulationRef);
        ps.setLong       (i++, mathRef);
        ps.setString     (i++, fmt.name());
        ps.setTimestamp  (i++, exportDateValue);
        ps.setString     (i++, uriValue);
        ps.setArray       (i++, variables);
        ps.setDouble      (i++, startTimeValue);
        ps.setDouble     (i++, endTimeValue);
        ps.setString     (i++, eventStatusValue);
    }

    public ExportHistory getExportHistoryRecord(ResultSet rset) throws SQLException, SQLException, DataAccessException {

        long simulationRef         = rset.getLong(this.simulationRef.getUnqualifiedColName());
        long mathRef               = rset.getLong(this.mathRef.getUnqualifiedColName());
        Long bioModelRef           = (Long) rset.getObject(this.bioModelRef.getUnqualifiedColName());
        Long mathModelRef          = (Long) rset.getObject(this.mathModelRef.getUnqualifiedColName());
        // enums & timestamps
        ExportFormat fmt      = ExportFormat.valueOf(rset.getString(this.exportFormat.getUnqualifiedColName()));
        Timestamp date        = rset.getTimestamp(this.exportDate.getUnqualifiedColName());

        // strings
        long jobId            = rset.getLong(this.jobId.getUnqualifiedColName());
        String uriVal         = rset.getString(this.uri.getUnqualifiedColName());
        String simNameVal     = rset.getString("sim_name");
        String mathModelNameVal     = rset.getString("math_model_name");
        String bioNameVal     = rset.getString("bio_name");
        String modelName = mathModelNameVal == null ? bioNameVal : mathModelNameVal;

        String[] variables          = (String[]) rset.getArray(this.variables.getUnqualifiedColName()).getArray();

        // numeric ranges
        double startBt    = rset.getDouble(this.startTime.getUnqualifiedColName());
        double endBt      = rset.getDouble(this.endTime.getUnqualifiedColName());

        // more strings & flags
        String eventStatusVal   = rset.getString(this.eventStatus.getUnqualifiedColName());


//        String mathOverrides = rset.getString("mathOverrides");
//        mathOverrides = mathOverrides == null ? rset.getString("mathOverridesSML") : mathOverrides;
//        mathOverrides = mathOverrides == null ? rset.getString("mathOverridesLRG") : mathOverrides;
//        List<MathOverrides.Element> overrides = MathOverrides.parseOverrideElementsFromVCML(new CommentStringTokenizer(mathOverrides));

//        String childSummary = rset.getString("childSummarySML");
//        childSummary = childSummary == null ? rset.getString("childSummaryLRG") : childSummary;
//        BioModelChildSummary bioModelChildSummary = BioModelChildSummary.fromDatabaseSerialization(childSummary);


        return new ExportHistory(
                jobId, new KeyValue("" + simulationRef), bioModelRef == null ? null : new KeyValue("" + bioModelRef),
                mathModelRef == null ? null : new KeyValue("" + mathModelRef), new KeyValue("" + mathRef),  fmt, date.toInstant(),
                uriVal, simNameVal, modelName, variables,
                startBt, endBt, ExportEnums.ExportProgressType.valueOf(eventStatusVal)
        );
    }
}
