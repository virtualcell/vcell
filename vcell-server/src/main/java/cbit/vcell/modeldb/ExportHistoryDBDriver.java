package cbit.vcell.modeldb;

import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportSpecs;
import cbit.vcell.export.server.HumanReadableExportData;
import cbit.vcell.export.server.TimeSpecs;
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

public class ExportHistoryDBDriver{
    public static final ExportHistoryTable exportHistoryTable = ExportHistoryTable.table;
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


    public void addExportHistory(Connection conn,
                                 User user,
                                 long   jobID,
                                 long   modelRef,
                                 ExportFormat exportFormat,
                                 Timestamp exportDate,
                                 String uri,
                                 ExportSpecs exportSpecs)
            throws SQLException, DependencyException, PermissionException, DataAccessException, ObjectNotFoundException {

        String vcmExpHiSQL =        // SQL statement for inserting into vc_model_export_history table in VCell server
                "INSERT INTO vc_model_export_history (" +
                        "id, job_id, user_ref, model_ref, export_format, export_date, uri," +
                        "data_id, simulation_name, application_name, biomodel_name, variables," +
                        "start_time, end_time, saved_file_name, application_type, non_spatial," +
                        "z_slices, t_slices, num_variables" +
                        ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


        PreparedStatement ps = conn.prepareStatement(vcmExpHiSQL);
        HumanReadableExportData meta = exportSpecs.getHumanReadableExportData();
        TimeSpecs ts = exportSpecs.getTimeSpecs();
        String[] vars = exportSpecs.getVariableSpecs().getVariableNames();

        String timeRange = ts.toString();
        String[] time_parts = timeRange.split("/");
        BigDecimal startTime = new BigDecimal(time_parts[0]);
        BigDecimal endTime = new BigDecimal(time_parts[1]);


        ps.setLong(2, jobID);
        ps.setLong(3, Long.parseLong(user.getID().toString()));
        ps.setLong(4, modelRef);
        ps.setString(5, exportFormat.toString());
        ps.setTimestamp(6, exportDate);
        ps.setString(7, uri);
        ps.setString(8, exportSpecs.getVCDataIdentifier().getID());
        ps.setString(9, meta.simulationName);
        ps.setString(10, meta.applicationName);
        ps.setString(11, meta.biomodelName);
        ps.setArray(12, conn.createArrayOf("text", vars));
        ps.setBigDecimal(13, startTime);
        ps.setBigDecimal(14, endTime);
        ps.setString(15, meta.serverSavedFileName);
        ps.setString(16, meta.applicationType);
        ps.setBoolean(17, meta.nonSpatial);
        ps.setInt(18, meta.zSlices);
        ps.setInt(19, meta.tSlices);
        ps.setInt(20, meta.numChannels);
        ps.executeUpdate();


        String vcmExpHisPVSQL =        // SQL statement for inserting into vc_model_parameter_value table in VCell server
                "INSERT INTO vc_model_parameter_values (" +
                        "export_id, user_ref, model_ref, parameter_name," +
                        "parameter_original, parameter_changed" +
                        ") VALUES (?,?,?,?,?,?)";

        PreparedStatement ps2 = conn.prepareStatement(vcmExpHisPVSQL);


        for (String entry : meta.differentParameterValues) {
            String[] parts = entry.split(":");
            if (parts.length == 3) {
                //psParam.setLong       (1, historyId);
                ps2.setLong(2, Long.parseLong(user.getID().toString()));
                ps2.setLong(3, modelRef);
                ps2.setString(4, parts[0]);
                ps2.setBigDecimal(5, new BigDecimal(parts[1]));
                ps2.setBigDecimal(6, new BigDecimal(parts[2]));
                ps2.addBatch();
            }
        }
        ps2.executeBatch();

    }

    public static void main(String[] args) throws SQLException, PermissionException, DataAccessException {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "quarkus", "quarkus")){
            ExportHistoryDBDriver dbDriver = new ExportHistoryDBDriver(null, null);
            ExportSpecs exportSpecs = new ExportSpecs(null, null, null, null, null, null,
                    null, null);
            dbDriver.addExportHistory(connection, new User("Administrator", new KeyValue("2")), 1, 2, ExportFormat.N5, null, null, exportSpecs);
        } catch (SQLException e){
            System.err.println("SQLException: " + e.getMessage());
        }

    }

}
