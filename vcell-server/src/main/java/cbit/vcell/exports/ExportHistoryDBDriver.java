package cbit.vcell.exports;

import cbit.vcell.modeldb.*;
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
        String deleteSQL = "DELETE FROM " + ExportHistoryTable.table.tableName + " WHERE " + ExportHistoryTable.table.uri.getUnqualifiedColName() + " = ?";
        try (PreparedStatement psDel = conn.prepareStatement(deleteSQL)) {
            psDel.setString(1, uri);
            psDel.executeUpdate();
        }
    }

    // sub selection of sim ref key for that export event, then tie it back to biomodel/sim for getting metadata (name, application, etc)
    public List<ExportHistory> getExportHistoryForUser(Connection conn, User user, int pageNumber) throws SQLException, JsonProcessingException, DataAccessException {
        String properSQL = "SELECT eh.*, sim." + SimulationTable.table.name.getUnqualifiedColName()
                + " as sim_name, bio." + BioModelTable.table.name.getUnqualifiedColName()
                + " as bio_name, math." + MathModelTable.table.name.getUnqualifiedColName()
                + " as math_model_name, simContext." + SimContextTable.table.name.getUnqualifiedColName() + " as application_name" +
                " FROM " + ExportHistoryTable.table.tableName + " eh " +
                " INNER JOIN " + SimulationTable.table.tableName + " sim ON sim." + SimulationTable.table.id.getUnqualifiedColName() + " = eh." + ExportHistoryTable.table.simulationRef.getUnqualifiedColName() +
                " LEFT JOIN " + BioModelTable.table.tableName + " bio ON bio." + BioModelTable.table.id.getUnqualifiedColName() + " = eh." + ExportHistoryTable.table.bioModelRef.getUnqualifiedColName() +
                " LEFT JOIN " + MathDescTable.table.tableName + " math ON math." + MathModelTable.table.id.getUnqualifiedColName() + " = eh." + ExportHistoryTable.table.mathModelRef.getUnqualifiedColName() +
                " LEFT JOIN " + SimContextTable.table.tableName + " simContext ON simContext." + SimContextTable.table.id.getUnqualifiedColName() + " = eh." + ExportHistoryTable.table.mathRef.getUnqualifiedColName() +
                " WHERE " + ExportHistoryTable.table.userRef.getUnqualifiedColName() + " = ? ORDER BY " + ExportHistoryTable.table.exportDate.getUnqualifiedColName() + " DESC OFFSET ? ROWS FETCH NEXT 100 ROWS ONLY";

        PreparedStatement ps = conn.prepareStatement(properSQL);
        ps.setLong(1, Long.parseLong(user.getID().toString()));
        pageNumber = (0 < pageNumber) && (pageNumber < 10000) ? pageNumber : 0;
        ps.setInt(2, pageNumber * 100);
        ResultSet resultSet = ps.executeQuery();
        List<ExportHistory> exportHistoryDBReps = new ArrayList<>();
        while (resultSet.next()) {
            exportHistoryDBReps.add(ExportHistoryTable.table.getExportHistoryRecord(resultSet));
        }
        return exportHistoryDBReps;
    }

}
