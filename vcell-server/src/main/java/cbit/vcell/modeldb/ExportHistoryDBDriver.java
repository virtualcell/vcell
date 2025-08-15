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
import java.util.StringJoiner;

public class ExportHistoryDBDriver{
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

    public void addExportHistory(Connection conn, User user, ExportHistory exportHistory)
            throws SQLException, DependencyException, PermissionException, DataAccessException, ObjectNotFoundException {

        ExportSpecs specs = exportHistory.exportSpecs;
        HumanReadableExportData meta = specs.getHumanReadableExportData();
        TimeSpecs ts = specs.getTimeSpecs();
        String[] vars = specs.getVariableSpecs().getVariableNames();
        // comma-separate the variable names
        String variablesCsv = String.join(",", vars);

        // 1) insert into vc_model_export_history
        String ehSQL = ExportHistoryTable.table.getInsertSQL();
        try (PreparedStatement ps = conn.prepareStatement(ehSQL)) {
            ExportHistoryTable.table.bindForInsert(ps,
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
                            // note: you need the export_id FK—assumes you have it from a sequence
                            exportHistory.jobID,   // if your PK is the jobID, else use the returned PK
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


    public static void main(String[] args) throws SQLException, PermissionException, DataAccessException {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres", "quarkus", "quarkus")) {

            ExportHistoryDBDriver dbDriver = new ExportHistoryDBDriver(null, null);


            GeometrySpecs testGeometrySpecs = new GeometrySpecs(null, 100, 100, 1);
            TimeSpecs testTimeSpecs = new TimeSpecs(0, 1, new double[]{1, 1}, 1);
            String[] testVarNames = new String[]{"A", "B"};
            VariableSpecs testVariableSpecs = new VariableSpecs(testVarNames, 1);
            KeyValue simKey = new KeyValue("12345");
            User user = new User("Administrator", new KeyValue("2"));
            VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, user);
            VCDataIdentifier vcdID = new VCSimulationDataIdentifier(vcSimID, 456);
            FormatSpecificSpecs testFormatSpecificSpecs = new FormatSpecificSpecs() {
                @Override public boolean equals(Object obj) { return this == obj; }
                @Override public String toString() { return "DummyFormatSpecificSpecs"; }
            };
            HumanReadableExportData meta = new HumanReadableExportData(
                    "MySimulation","MyApp","MyBioModel",
                    new ArrayList<>(), "result.n5","N5", false, new HashMap<>()
            );
            Timestamp testExportDate = new Timestamp(System.currentTimeMillis());
            ExportSpecs exportSpecs = new ExportSpecs(
                    vcdID, ExportFormat.N5, testVariableSpecs, testTimeSpecs,
                    testGeometrySpecs, testFormatSpecificSpecs, "testsim","testname"
            );
            exportSpecs.setExportMetaData(meta);


            ExportHistoryDBDriver.ExportHistory history = new ExportHistoryDBDriver.ExportHistory(
                    /*jobID=*/ 1,
                    /*modelRef=*/ 2,
                    /*format=*/ ExportFormat.N5,
                    /*date=*/ testExportDate,
                    /*uri=*/ "https://vcell.cam.uchc.edu/n5Data/paulricky/5456fb59b530a19.n5?dataSetName=3681309072",
                    /*specs=*/ exportSpecs
            );


            dbDriver.addExportHistory(connection, user, history);

            System.out.println("=== Test get before deletion ===");
            try (ResultSet rs = dbDriver.getExportHistoryForUser(connection, user)) {
                while (rs.next()) {
                    System.out.println(
                            "id=" + rs.getLong("id")
                                    + ", data_id=" + rs.getString("data_id")
                                    + ", export_format=" + rs.getString("export_format")
                    );
                }
            }


            dbDriver.deleteExportHistory(connection, exportSpecs);

        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }


}
