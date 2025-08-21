package cbit.vcell.modeldb;

import cbit.vcell.export.server.*;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.document.VersionableType;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringJoiner;

public class ExportHistoryDBDriver {
    public static final ExportHistoryTable exportHistoryTable = ExportHistoryTable.table;
    public static final ModelParameterValuesTable modelParameterValuesTable = ModelParameterValuesTable.table;
    public static final BioModelTable bioModelTable = BioModelTable.table;
    public static final PublicationTable publicationTable = PublicationTable.table;
    public static final UserTable userTable = UserTable.table;
    public static final BioModelSimulationLinkTable bioModelSimLinkTable = BioModelSimulationLinkTable.table;
    public static final BioModelSimContextLinkTable bioModelSimContextLinkTable = BioModelSimContextLinkTable.table;
    public static final SimulationTable simTable = SimulationTable.table;
    public static final SimContextTable simContextTable = SimContextTable.table;


    /**
     * LocalDBManager constructor comment.
     */
    public ExportHistoryDBDriver(DatabaseSyntax databaseSyntax, KeyFactory keyFactory) {

    }

    public record ExportHistory(
            long      jobID,
            long      modelRef,
            ExportFormat exportFormat,
            Timestamp exportDate,
            String    uri,
            ExportSpecs exportSpecs
    ){}

    public void addExportHistory(Connection conn, User user, ExportHistory exportHistory, KeyFactory keyFactory)
            throws SQLException, DependencyException, PermissionException, DataAccessException, ObjectNotFoundException {

        ExportSpecs specs = exportHistory.exportSpecs;
        HumanReadableExportData meta = specs.getHumanReadableExportData();
        TimeSpecs ts = specs.getTimeSpecs();
        String[] vars = specs.getVariableSpecs().getVariableNames();
        // comma-separate the variable names
        String variablesCsv = String.join(",", vars);

        // 1) insert into vc_model_export_history
        String ehSQL = ExportHistoryTable.table.getInsertSQL();
        KeyValue keyValue = keyFactory.getNewKey(conn);
        try (PreparedStatement ps = conn.prepareStatement(ehSQL)) {
            ExportHistoryTable.table.bindForInsert(ps,
                    keyValue,
                    exportHistory.jobID,
                    Long.parseLong(user.getID().toString()),
                    exportHistory.modelRef,
                    exportHistory.exportFormat,
                    exportHistory.exportDate,
                    exportHistory.uri,
                    specs.getVCDataIdentifier().getID(),
                    meta.simulationName,
                    meta.applicationName,
                    meta.biomodelName,
                    conn.createArrayOf("VARCHAR", exportHistory.exportSpecs().getVariableSpecs().getVariableNames()),
                    BigDecimal.valueOf(ts.getBeginTimeIndex()),
                    BigDecimal.valueOf(ts.getEndTimeIndex()),
                    meta.serverSavedFileName,
                    meta.applicationType,
                    meta.nonSpatial,
                    meta.zSlices,
                    meta.tSlices,
                    meta.numChannels
            );
            System.out.println("Data insertion tag:");
            System.out.println(ps.executeUpdate());
            System.out.println(".");
            //ps.executeUpdate();
        }

        // 2) insert each parameter change
        String pvSQL = ModelParameterValuesTable.table.getInsertSQL();
        try (PreparedStatement ps2 = conn.prepareStatement(pvSQL)) {
            for (String entry : meta.differentParameterValues) {
                String[] parts = entry.split(":");
                if (parts.length == 3) {
                    ModelParameterValuesTable.table.bindForInsert(ps2,

                            exportHistory.jobID,
                            Long.parseLong(user.getID().toString()),
                            exportHistory.modelRef,
                            parts[0],
                            new BigDecimal(parts[1]),
                            new BigDecimal(parts[2])
                    );
                    ps2.addBatch();
                }
            }
            ps2.executeBatch();
        }

    }

    public void deleteExportHistory(Connection conn, ExportSpecs exportSpecs) throws SQLException {

        String selectSQL = "SELECT id FROM vc_model_export_history WHERE data_id = ?";
        try (PreparedStatement psSel = conn.prepareStatement(selectSQL)) {
            psSel.setString(1, exportSpecs.getVCDataIdentifier().getID());
            try (ResultSet rs = psSel.executeQuery()) {
                while (rs.next()) {
                    long historyId = rs.getLong(1);

                    try (PreparedStatement psDelParams = conn.prepareStatement(
                            "DELETE FROM vc_model_parameter_values WHERE id = ?")) {
                        psDelParams.setLong(1, historyId);
                        psDelParams.executeUpdate();
                    }

                    try (PreparedStatement psDelHist = conn.prepareStatement(
                            "DELETE FROM vc_model_export_history WHERE id = ?")) {
                        psDelHist.setLong(1, historyId);
                        psDelHist.executeUpdate();
                    }
                }
            }
        }
    }

    public ResultSet getExportHistoryForUser(Connection conn, User user) throws SQLException {
        String sql = "SELECT * FROM vc_model_export_history WHERE user_ref = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, Long.parseLong(user.getID().toString()));
        return ps.executeQuery();
    }

}
