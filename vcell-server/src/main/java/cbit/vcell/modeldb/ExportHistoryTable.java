package cbit.vcell.modeldb;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.export.server.ExportFormat;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ExportHistoryTable extends Table {
    private static final String TABLE_NAME = "vc_model_export_history";

    public final Field jobId = new Field("job_id", Field.SQLDataType.varchar_50, "NOT NULL");

    public final Field userRef = new Field("user_ref", Field.SQLDataType.integer, "NOT NULL REFERENCES" + UserTable.REF_TYPE);

    public final Field modelRef = new Field("model_ref", Field.SQLDataType.integer, "NOT NULL " + ModelTable.REF_TYPE);

    public final Field exportFormat = new Field("export_format", Field.SQLDataType.varchar_50, "NOT NULL");
    public final Field exportDate = new Field("export_date", Field.SQLDataType.date, "NOT NULL");

    public final Field uri = new Field("uri", Field.SQLDataType.varchar2_4000, "NOT NULL");
    public final Field dataId = new Field("data_id", Field.SQLDataType.varchar_255, "NOT NULL");
    public final Field simulationName = new Field("simulation_name", Field.SQLDataType.varchar_255, "NOT NULL");
    public final Field applicationName = new Field("application_name", Field.SQLDataType.varchar_255, "NOT NULL");
    public final Field biomodelName = new Field("biomodel_name", Field.SQLDataType.varchar_255, "NOT NULL");

    public final Field variables = new Field("variables", Field.SQLDataType.varchar_255, "[] NOT NULL");
    public final Field startTime = new Field("start_time", Field.SQLDataType.integer_as_numeric, "NOT NULL");
    public final Field endTime = new Field("end_time", Field.SQLDataType.integer_as_numeric, "NOT NULL");
    public final Field savedFileName = new Field("saved_file_name", Field.SQLDataType.varchar_255, "NOT NULL");
    public final Field applicationType = new Field("application_type", Field.SQLDataType.varchar_50, "NOT NULL");
    public final Field nonSpatial = new Field("non_spatial", Field.SQLDataType.varchar_50, "NOT NULL");
    public final Field zSlices = new Field("z_slices", Field.SQLDataType.integer, "NOT NULL DEFAULT 0");
    public final Field tSlices = new Field("t_slices", Field.SQLDataType.integer, "NOT NULL DEFAULT 0");
    public final Field numVariables = new Field("num_variables", Field.SQLDataType.integer, "NOT NULL DEFAULT 0");

    //public final Field insertDate = new Field("insert_date", Field.SQLDataType.date, "NOT NULL DEFAULT CURRENT_TIMESTAMP");


    private final Field fields[] = {jobId, userRef, modelRef, exportFormat, exportDate, uri,
            dataId, simulationName, applicationName, biomodelName, variables,
            startTime, endTime, savedFileName, applicationType, nonSpatial,
            zSlices, tSlices, numVariables};

    private final Field[] insertFields = {
            jobId, userRef, modelRef, exportFormat, exportDate, uri, dataId,
            simulationName, applicationName, biomodelName, variables,
            startTime, endTime, savedFileName, applicationType, nonSpatial,
            zSlices, tSlices, numVariables
    };

    public static final ExportHistoryTable table = new ExportHistoryTable();

    private ExportHistoryTable() {
        super(TABLE_NAME);
        addFields(insertFields);
    }

    public String getInsertSQL() {

        StringJoiner cols = new StringJoiner(",", "(", ")");
        for (Field f : insertFields) {
            cols.add(f.getUnqualifiedColName());
        }

        StringJoiner ph = new StringJoiner(",", "(", ")");
        for (int i = 0; i < insertFields.length; i++) {
            ph.add("?");
        }
        return "INSERT INTO " + TABLE_NAME + " " + cols.toString() + " VALUES " + ph.toString();
    }
    public void bindForInsert(
            PreparedStatement ps,
            long        jobIdValue,
            long          userRefValue,
            long          modelRefValue,
            ExportFormat  fmt,
            Timestamp     exportDateValue,
            String        uriValue,
            String        dataIdValue,
            String        simName,
            String        appName,
            String        bioName,
            BigDecimal    startTimeValue,
            BigDecimal    endTimeValue,
            String        savedFileNameValue,
            String        applicationTypeValue,
            boolean       nonSpatialValue,
            int           zSlicesValue,
            int           tSlicesValue,
            int           numVariablesValue
    ) throws SQLException {
        int i = 1;
        ps.setLong    (i++, jobIdValue);
        ps.setLong       (i++, userRefValue);
        ps.setLong       (i++, modelRefValue);
        ps.setString     (i++, fmt.name());
        ps.setTimestamp  (i++, exportDateValue);
        ps.setString     (i++, uriValue);
        ps.setString     (i++, dataIdValue);
        ps.setString     (i++, simName);
        ps.setString     (i++, appName);
        ps.setString     (i++, bioName);
        ps.setBigDecimal (i++, startTimeValue);
        ps.setBigDecimal (i++, endTimeValue);
        ps.setString     (i++, savedFileNameValue);
        ps.setString     (i++, applicationTypeValue);
        ps.setBoolean    (i++, nonSpatialValue);
        ps.setInt        (i++, zSlicesValue);
        ps.setInt        (i++, tSlicesValue);
        ps.setInt        (i++, numVariablesValue);
    }
//    public record ExportHistoryRecord(String jobID, long userRef, long modelRef, ExportFormat format, Timestamp date,
//                                      String uri, String dataID, String simName, String appName, String bioName,
//                                      BigDecimal startTime, BigDecimal endTime, String savedFile, String appType,
//                                      boolean nonSpatial, int zSlices, int tSlices, int numVars) {
//    }
//
//    public ExportHistoryRecord getExportHistoryRecord(ResultSet rset, ExportHistoryRecord exportHistoryRecord) throws SQLException, SQLException {
//
//        long userRef          = rset.getLong((int) exportHistoryRecord.userRef);
//        long modelRef         = rset.getLong((int) exportHistoryRecord.modelRef);
//
//        // enums & timestamps
//        ExportFormat fmt      = ExportFormat.valueOf(rset.getString(exportFormat.toString()));
//        Timestamp date        = rset.getTimestamp(exportDate.toString());
//
//        // strings
//        String jobId            = rset.getString(exportHistoryRecord.jobID);
//        String uriVal         = rset.getString(uri.toString());
//        String dataIdVal      = rset.getString(dataId.toString());
//        String simNameVal     = rset.getString(simulationName.toString());
//        String appNameVal     = rset.getString(applicationName.toString());
//        String bioNameVal     = rset.getString(biomodelName.toString());
//
//        // text array of variables
//        Array sqlArr          = rset.getArray(variables.toString());
//
//        // numeric ranges
//        BigDecimal startBt    = rset.getBigDecimal(startTime.toString());
//        BigDecimal endBt      = rset.getBigDecimal(endTime.toString());
//
//        // more strings & flags
//        String savedFileVal   = rset.getString(savedFileName.toString());
//        String appTypeVal     = rset.getString(applicationType.toString());
//        boolean nonSp         = rset.getBoolean(nonSpatial.toString());
//        int zS                = rset.getInt(zSlices.toString());
//        int tS                = rset.getInt(tSlices.toString());
//        int numV              = rset.getInt(numVariables.toString());
//
//
//
//        return new ExportHistoryRecord(
//                jobId, userRef, modelRef, fmt, date,
//                uriVal, dataIdVal, simNameVal, appNameVal, bioNameVal,
//                startBt, endBt, savedFileVal, appTypeVal,
//                nonSp, zS, tS, numV
//        );
//    }
}
