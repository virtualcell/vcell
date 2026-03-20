package cbit.vcell.modeldb;

import cbit.sql.Field;
import cbit.sql.Table;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.HumanReadableExportData.DifferentParameterValues;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.vcell.util.document.KeyValue;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.StringJoiner;

public class ExportHistoryTable extends Table {
    private static final String TABLE_NAME = "vc_simulation_export_history";

   // public final Field id = new Field(id_ColumnName, Field.SQLDataType.integer,"PRIMARY KEY");

    public final Field jobId = new Field("job_id", Field.SQLDataType.varchar_50, "NOT NULL");

    public final Field userRef = new Field("user_ref", Field.SQLDataType.integer, "NOT NULL REFERENCES" + UserTable.REF_TYPE);

    public final Field simulationRef = new Field("simulation_ref", Field.SQLDataType.integer, "NOT NULL REFERENCES" + SimulationTable.REF_TYPE);

    public final Field exportFormat = new Field("export_format", Field.SQLDataType.varchar_50, "NOT NULL");
    public final Field exportDate = new Field("export_date", Field.SQLDataType.date, "NOT NULL");

    public final Field uri = new Field("uri", Field.SQLDataType.varchar2_4000, "NOT NULL");
    public final Field dataId = new Field("data_id", Field.SQLDataType.varchar_255, "NOT NULL");
    public final Field simulationName = new Field("simulation_name", Field.SQLDataType.varchar_255, "NOT NULL");
    public final Field applicationName = new Field("application_name", Field.SQLDataType.varchar_255, "NOT NULL");
    public final Field biomodelName = new Field("biomodel_name", Field.SQLDataType.varchar_255, "NOT NULL");

    public final Field variables = new Field("variables", Field.SQLDataType.varchar_255, "[] NOT NULL");
    public final Field changedParameters = new Field("parameter_values", Field.SQLDataType.json, "");
    public final Field startTime = new Field("start_time", Field.SQLDataType.number_as_real, "NOT NULL");
    public final Field endTime = new Field("end_time", Field.SQLDataType.number_as_real, "NOT NULL");
    public final Field savedFileName = new Field("saved_file_name", Field.SQLDataType.varchar_255, "NOT NULL");
    public final Field applicationType = new Field("application_type", Field.SQLDataType.varchar_50, "NOT NULL");
    public final Field nonSpatial = new Field("non_spatial", Field.SQLDataType.varchar_50, "NOT NULL");
    public final Field zSlices = new Field("z_slices", Field.SQLDataType.integer, "NOT NULL DEFAULT 0");
    public final Field tSlices = new Field("t_slices", Field.SQLDataType.integer, "NOT NULL DEFAULT 0");
    public final Field numVariables = new Field("num_variables", Field.SQLDataType.integer, "NOT NULL DEFAULT 0");

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
                jobId, userRef, simulationRef, exportFormat, exportDate, uri, dataId,
                simulationName, applicationName, biomodelName, variables, changedParameters,
                startTime, endTime, savedFileName, applicationType, nonSpatial,
                zSlices, tSlices, numVariables
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
            int        jobIdValue,
            int          userRefValue,
            int          simulationRef,
            ExportFormat  fmt,
            Timestamp     exportDateValue,
            String        uriValue,
            String        dataIdValue,
            String        simName,
            String        appName,
            String        bioName,
            Array      variables,
            Object        parameterValues,
            double      startTimeValue,
            double      endTimeValue,
            String        savedFileNameValue,
            String        applicationTypeValue,
            boolean       nonSpatialValue,
            int           zSlicesValue,
            int           tSlicesValue,
            int           numVariablesValue
    ) throws SQLException {
        int i = 1;
        ps.setInt     (i++,  Integer.parseInt(keyValue.toString()));
        ps.setLong    (i++, jobIdValue);
        ps.setLong       (i++, userRefValue);
        ps.setLong       (i++, simulationRef);
        ps.setString     (i++, fmt.name());
        ps.setTimestamp  (i++, exportDateValue);
        ps.setString     (i++, uriValue);
        ps.setString     (i++, dataIdValue);
        ps.setString     (i++, simName);
        ps.setString     (i++, appName);
        ps.setString     (i++, bioName);
        ps.setArray       (i++, variables);
        ps.setObject      (i++, parameterValues);
        ps.setDouble      (i++, startTimeValue);
        ps.setDouble     (i++, endTimeValue);
        ps.setString     (i++, savedFileNameValue);
        ps.setString     (i++, applicationTypeValue);
        ps.setBoolean    (i++, nonSpatialValue);
        ps.setInt        (i++, zSlicesValue);
        ps.setInt        (i++, tSlicesValue);
        ps.setInt        (i++, numVariablesValue);

    }

    public ExportHistoryRep getExportHistoryRecord(ResultSet rset) throws SQLException, SQLException, JsonProcessingException {

        int simulationRef         = rset.getInt(this.simulationRef.getUnqualifiedColName());

        // enums & timestamps
        ExportFormat fmt      = ExportFormat.valueOf(rset.getString(this.exportFormat.getUnqualifiedColName()));
        Timestamp date        = rset.getTimestamp(this.exportDate.getUnqualifiedColName());

        // strings
        int jobId            = rset.getInt(this.jobId.getUnqualifiedColName());
        String uriVal         = rset.getString(this.uri.getUnqualifiedColName());
        String dataIdVal      = rset.getString(this.dataId.getUnqualifiedColName());
        String simNameVal     = rset.getString(this.simulationName.getUnqualifiedColName());
        String appNameVal     = rset.getString(this.applicationName.getUnqualifiedColName());
        String bioNameVal     = rset.getString(this.biomodelName.getUnqualifiedColName());

        // text array of variables
        String[] variables          = (String[]) rset.getArray(this.variables.getUnqualifiedColName()).getArray();
        List<DifferentParameterValues> parameterValues = DifferentParameterValues.fromJSON(rset.getString(this.changedParameters.getUnqualifiedColName()));

        // numeric ranges
        double startBt    = rset.getDouble(this.startTime.getUnqualifiedColName());
        double endBt      = rset.getDouble(this.endTime.getUnqualifiedColName());

        // more strings & flags
        String savedFileVal   = rset.getString(this.savedFileName.getUnqualifiedColName());
        String appTypeVal     = rset.getString(this.applicationType.getUnqualifiedColName());
        boolean nonSp         = rset.getBoolean(this.nonSpatial.getUnqualifiedColName());
        int zS                = rset.getInt(this.zSlices.getUnqualifiedColName());
        int tS                = rset.getInt(this.tSlices.getUnqualifiedColName());
        int numV              = rset.getInt(this.numVariables.getUnqualifiedColName());



        return new ExportHistoryRep(
                jobId, new KeyValue("" + simulationRef), fmt, date,
                uriVal, dataIdVal, simNameVal, appNameVal, bioNameVal, variables,
                parameterValues, startBt, endBt, savedFileVal, appTypeVal,
                nonSp, zS, tS, numV
        );
    }
}
