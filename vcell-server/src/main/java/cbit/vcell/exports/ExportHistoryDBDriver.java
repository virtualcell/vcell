package cbit.vcell.exports;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExportHistoryDBDriver {

    /**
     * LocalDBManager constructor comment.
     */
    public ExportHistoryDBDriver(DatabaseSyntax databaseSyntax, KeyFactory keyFactory) {

    }

    private boolean isOracleConnection(Connection connection) throws SQLException {
        String productName = connection.getMetaData().getDatabaseProductName().toLowerCase();
        return productName.contains("oracle");
    }

    public void addExportHistory(Connection conn, User user, ExportHistoryDBRep exportHistory, KeyFactory keyFactory)
            throws SQLException, DependencyException, PermissionException, DataAccessException, ObjectNotFoundException {

        // 1) insert into vc_model_export_history
        String ehSQL = ExportHistoryTable.table.getInsertSQL();
        KeyValue keyValue = keyFactory.getNewKey(conn);

        try (PreparedStatement ps = conn.prepareStatement(ehSQL)) {
            ExportHistoryTable.table.bindForInsert(ps,
                    keyValue,
                    exportHistory.jobID(),
                    Integer.parseInt(user.getID().toString()),
                    exportHistory.bioModelRef() == null ? null : Integer.parseInt(exportHistory.bioModelRef().toString()),
                    exportHistory.mathModelRef() == null ? null : Integer.parseInt(exportHistory.mathModelRef().toString()),
                    Integer.parseInt(exportHistory.simulationRef().toString()),
                    Integer.parseInt(exportHistory.mathRef().toString()),
                    exportHistory.exportFormat(),
                    exportHistory.exportDate(),
                    exportHistory.uri(),
                    conn.createArrayOf("VARCHAR", exportHistory.variables()),
                    exportHistory.startTimeValue(),
                    exportHistory.endTimeValue(),
                    exportHistory.eventStatus().toString()
            );
            ps.executeUpdate();
        }

    }

    public void deleteExportHistory(Connection conn, String uri) throws SQLException {
        // Concern, data id could mean multiple items get deleted
        String deleteSQL = "DELETE FROM vc_simulation_export_history WHERE uri = ?";
        try (PreparedStatement psDel = conn.prepareStatement(deleteSQL)) {
            psDel.setString(1, uri);
            psDel.executeUpdate();
        }
    }

    // sub selection of sim ref key for that export event, then tie it back to biomodel/sim for getting metadata (name, application, etc)
    public List<ExportHistory> getExportHistoryForUser(Connection conn, User user) throws SQLException, JsonProcessingException, DataAccessException {
        String sql = """
        SELECT eh.*, sim.name as sim_name, bio.name as bio_name, bio.childSummaryLRG, bio.childSummarySML, math.name as math_model_name,
        sim.mathoverrides, sim.mathOverridesLRG, sim.mathOverridesSML, simContext.name as application_name, simContext.appComponentsLRG, simContext.appComponentsSML
        FROM vc_simulation_export_history eh 
        INNER JOIN vc_simulation sim ON sim.id = eh.simulation_ref
        LEFT JOIN vc_biomodel bio ON bio.id = eh.biomodel_ref
        LEFT JOIN vc_mathmodel math ON math.id = eh.mathmodel_ref
        LEFT JOIN vc_simcontext simContext ON simContext.mathRef = eh.math_ref
        WHERE user_ref = ?
""";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, Long.parseLong(user.getID().toString()));
        ResultSet resultSet = ps.executeQuery();
        List<ExportHistory> exportHistoryDBReps = new ArrayList<>();
        while (resultSet.next()) {
            exportHistoryDBReps.add(ExportHistoryTable.table.getExportHistoryRecord(resultSet));
        }
        return exportHistoryDBReps;
    }

}
