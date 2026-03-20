package cbit.vcell.modeldb;

import cbit.vcell.export.server.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.math.BigDecimal;
import java.sql.*;

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

    public void addExportHistory(Connection conn, User user, ExportHistoryRep exportHistory, KeyFactory keyFactory)
            throws SQLException, DependencyException, PermissionException, DataAccessException, ObjectNotFoundException {

        // 1) insert into vc_model_export_history
        String ehSQL = ExportHistoryTable.table.getInsertSQL();
        KeyValue keyValue = keyFactory.getNewKey(conn);

        Object jsonDBObjectForParamValues;
        try{
            ObjectMapper mapper = new ObjectMapper();
            String jsonVersionOfParamValues = mapper.writeValueAsString(exportHistory.parameterValues());
            if (isOracleConnection(conn)){
                Clob clob = conn.createClob();
                clob.setString(1, jsonVersionOfParamValues);
                jsonDBObjectForParamValues = clob;
            } else {
                PGobject pGobject = new PGobject();
                pGobject.setType("jsonb");
                pGobject.setValue(jsonVersionOfParamValues);
                jsonDBObjectForParamValues = pGobject;
            }
        } catch (JsonProcessingException e){
            throw new DataAccessException(e);
        }


        try (PreparedStatement ps = conn.prepareStatement(ehSQL)) {
            ExportHistoryTable.table.bindForInsert(ps,
                    keyValue,
                    exportHistory.jobID(),
                    Integer.parseInt(user.getID().toString()),
                    Integer.parseInt(exportHistory.simulationRef().toString()),
                    exportHistory.exportFormat(),
                    exportHistory.exportDate(),
                    exportHistory.uri(),
                    exportHistory.dataIdValue(),
                    exportHistory.simName(),
                    exportHistory.appName(),
                    exportHistory.bioName(),
                    conn.createArrayOf("VARCHAR", exportHistory.variables()),
                    jsonDBObjectForParamValues,
                    exportHistory.startTimeValue(),
                    exportHistory.endTimeValue(),
                    exportHistory.savedFileNameValue(),
                    exportHistory.applicationTypeValue(),
                    exportHistory.nonSpatialValue(),
                    exportHistory.zSlicesValue(),
                    exportHistory.tSlicesValue(),
                    exportHistory.numVariablesValue()
            );
            System.out.println("Data insertion tag:");
            System.out.println(ps.executeUpdate());
            System.out.println(".");
            //ps.executeUpdate();
        }

    }

    public void deleteExportHistory(Connection conn, ExportSpecs exportSpecs) throws SQLException {

        // Concern, data id could mean multiple items get deleted
        String selectSQL = "SELECT id FROM vc_simulation_export_history WHERE data_id = ?";
        try (PreparedStatement psSel = conn.prepareStatement(selectSQL)) {
            psSel.setString(1, exportSpecs.getVCDataIdentifier().getID());
            try (ResultSet rs = psSel.executeQuery()) {
                while (rs.next()) {
                    long historyId = rs.getLong(1);

                    try (PreparedStatement psDelHist = conn.prepareStatement(
                            "DELETE FROM vc_simulation_export_history WHERE id = ?")) {
                        psDelHist.setLong(1, historyId);
                        psDelHist.executeUpdate();
                    }
                }
            }
        }
    }

    public ResultSet getExportHistoryForUser(Connection conn, User user) throws SQLException {
        String sql = "SELECT * FROM vc_simulation_export_history WHERE user_ref = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, Long.parseLong(user.getID().toString()));
        return ps.executeQuery();
    }

}
