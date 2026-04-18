package cbit.vcell.modeldb;

import cbit.sql.Field;
import cbit.sql.Field.SQLDataType;
import cbit.sql.Table;
import org.vcell.optimization.OptJobRecord;
import org.vcell.optimization.OptJobStatus;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Table definition for vc_optjob — optimization job tracking.
 * This is an operational table (not versioned). Access control is enforced
 * at the REST layer by checking ownerRef.
 */
public class OptJobTable extends Table {
    private static final String TABLE_NAME = "vc_optjob";
    public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

    public final Field ownerRef       = new Field("ownerRef",       SQLDataType.integer,      "NOT NULL " + UserTable.REF_TYPE);
    public final Field status         = new Field("status",         SQLDataType.varchar2_32,  "NOT NULL");
    public final Field optProblemFile = new Field("optProblemFile", SQLDataType.varchar2_512, "NOT NULL");
    public final Field optOutputFile  = new Field("optOutputFile",  SQLDataType.varchar2_512, "NOT NULL");
    public final Field optReportFile  = new Field("optReportFile",  SQLDataType.varchar2_512, "NOT NULL");
    public final Field htcJobId       = new Field("htcJobId",       SQLDataType.varchar2_128, "");
    public final Field statusMessage  = new Field("statusMessage",  SQLDataType.varchar2_4000, "");
    public final Field insertDate     = new Field("insertDate",     SQLDataType.date,         "NOT NULL");
    public final Field updateDate     = new Field("updateDate",     SQLDataType.date,         "NOT NULL");

    private final Field[] fields = {
            ownerRef, status, optProblemFile, optOutputFile, optReportFile,
            htcJobId, statusMessage, insertDate, updateDate
    };

    public static final OptJobTable table = new OptJobTable();

    private OptJobTable() {
        super(TABLE_NAME);
        addFields(fields);
    }

    public String getSQLValueList(KeyValue key, KeyValue ownerKey, OptJobStatus jobStatus,
                                  String problemFile, String outputFile, String reportFile,
                                  String htcJob, String message) {
        return "(" +
                key + "," +
                ownerKey + "," +
                "'" + jobStatus.name() + "'," +
                "'" + TokenMangler.getSQLEscapedString(problemFile) + "'," +
                "'" + TokenMangler.getSQLEscapedString(outputFile) + "'," +
                "'" + TokenMangler.getSQLEscapedString(reportFile) + "'," +
                (htcJob != null ? "'" + TokenMangler.getSQLEscapedString(htcJob) + "'" : "null") + "," +
                (message != null ? "'" + TokenMangler.getSQLEscapedString(message, 4000) + "'" : "null") + "," +
                "current_timestamp," +
                "current_timestamp" +
                ")";
    }

    public String getSQLUpdateList(OptJobStatus jobStatus, String message) {
        return status + "='" + jobStatus.name() + "'," +
                statusMessage + "=" + (message != null ? "'" + TokenMangler.getSQLEscapedString(message, 4000) + "'" : "null") + "," +
                updateDate + "=current_timestamp";
    }

    public String getSQLUpdateListHtcJobId(String htcJob, OptJobStatus jobStatus) {
        return htcJobId + "=" + (htcJob != null ? "'" + TokenMangler.getSQLEscapedString(htcJob) + "'" : "null") + "," +
                status + "='" + jobStatus.name() + "'," +
                updateDate + "=current_timestamp";
    }

    public OptJobRecord getOptJobRecord(ResultSet rset) throws SQLException {
        KeyValue key = new KeyValue(rset.getBigDecimal(id.toString()));
        KeyValue owner = new KeyValue(rset.getBigDecimal(ownerRef.toString()));
        OptJobStatus jobStatus = OptJobStatus.valueOf(rset.getString(status.toString()));
        String problemFile = rset.getString(optProblemFile.toString());
        String outputFile = rset.getString(optOutputFile.toString());
        String reportFile = rset.getString(optReportFile.toString());
        String htcJob = rset.getString(htcJobId.toString());
        String rawMessage = rset.getString(statusMessage.toString());
        String message = rawMessage != null ? TokenMangler.getSQLRestoredString(rawMessage) : null;
        java.sql.Timestamp insertTs = rset.getTimestamp(insertDate.toString());
        java.sql.Timestamp updateTs = rset.getTimestamp(updateDate.toString());

        return new OptJobRecord(
                key, owner, jobStatus, problemFile, outputFile, reportFile,
                htcJob, message,
                insertTs.toInstant(), updateTs.toInstant()
        );
    }
}
