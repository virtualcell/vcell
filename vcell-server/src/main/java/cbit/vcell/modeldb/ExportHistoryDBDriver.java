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

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

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
        BigDecimal startTime = new BigDecimal(ts.getBeginTimeIndex());
        BigDecimal endTime = new BigDecimal(ts.getEndTimeIndex());

        ps.setLong(1, 1);   //change once key values issue resolved
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
                ps2.setLong       (1, 1);   //change once key values issue resolved
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

    public void deleteExportHistory(Connection conn, ExportSpecs exportSpecs) throws SQLException {

        String selectSQL = "SELECT id FROM vc_model_export_history WHERE data_id = ?";
        try (PreparedStatement psSel = conn.prepareStatement(selectSQL)) {
            psSel.setString(1, exportSpecs.getVCDataIdentifier().getID());
            try (ResultSet rs = psSel.executeQuery()) {
                while (rs.next()) {
                    long historyId = rs.getLong(1);

                    try (PreparedStatement psDelParams = conn.prepareStatement(
                            "DELETE FROM vc_model_parameter_values WHERE export_id = ?")) {
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


    public static void main(String[] args) throws SQLException, PermissionException, DataAccessException {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "quarkus", "quarkus")){
            ExportHistoryDBDriver dbDriver = new ExportHistoryDBDriver(null, null);

            GeometrySpecs testGeometrySpecs = new GeometrySpecs(null, 100, 100, 1);
            TimeSpecs testTimeSpecs = new TimeSpecs(0, 1, new double[]{1, 1}, 1);
            String[] testVarNames = new String[]{"A", "B"};
            VariableSpecs testVariableSpecs = new VariableSpecs(testVarNames, 1);

            KeyValue simKey = new KeyValue("12345");


            User owner   = new User("Administrator", new KeyValue("2"));


            VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, owner);

            VCDataIdentifier vcdID = new VCSimulationDataIdentifier(vcSimID,456);
            String dataID = vcdID.getID();

            FormatSpecificSpecs testFormatSpecificSpecs = new FormatSpecificSpecs() {
                @Override
                public boolean equals(Object obj) {

                    return this == obj;
                }
                @Override
                public String toString() {
                    return "DummyFormatSpecificSpecs";
                }
            };



            HumanReadableExportData meta = new HumanReadableExportData(
                    "MySimulation",
                    "MyApp",
                    "MyBioModel",
                    new ArrayList<>(),
                    "test",
                    "result.n5",
                    false,
                    new HashMap<>()
            );

            Timestamp testExportDate = new Timestamp(System.currentTimeMillis());

            ExportSpecs exportSpecs = new ExportSpecs(vcdID, ExportFormat.N5, testVariableSpecs, testTimeSpecs, testGeometrySpecs, testFormatSpecificSpecs,
                    "testsim", "testname");
            exportSpecs.setExportMetaData(meta);

            dbDriver.addExportHistory(connection, new User("Administrator", new KeyValue("2")), 1, 2, ExportFormat.N5, testExportDate, "https://vcell.cam.uchc.edu/n5Data/paulricky/5456fb59b530a19.n5?dataSetName\\u003d3681309072", exportSpecs);

            System.out.println("=== Test get before deletion ===");
            try (ResultSet rs = dbDriver.getExportHistoryForUser(connection, owner)) {
                while (rs.next()) {
                    System.out.println("id=" + rs.getLong("id")
                            + ", data_id=" + rs.getString("data_id")
                            + ", export_format=" + rs.getString("export_format"));
                }
            }
            dbDriver.deleteExportHistory(connection, exportSpecs);


        } catch (SQLException e){
            System.err.println("SQLException: " + e.getMessage());
        }

    }

}
