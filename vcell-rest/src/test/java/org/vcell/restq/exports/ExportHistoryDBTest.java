package org.vcell.restq.exports;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.export.server.*;
import cbit.vcell.exports.ExportHistory;
import cbit.vcell.exports.ExportHistoryDBDriver;
import cbit.vcell.exports.ExportHistoryDBRep;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XmlParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import cbit.vcell.export.server.ExportFormat;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.bouncycastle.math.raw.Nat.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.vcell.util.VCAssert.assertTrue;

// TODO: Use GenericVCMLTests class for the principle tests for VCDocuments
@QuarkusTest
public class ExportHistoryDBTest {

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    private BioModel savedBioModel;
    private Simulation savedSimulation;
    private KeyValue simulationKey;

    @BeforeAll
    public static void setupConfig() {
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void initializeDB() throws DataAccessException, PropertyVetoException, XmlParseException, IOException, ExpressionException, SQLException {
        savedBioModel = TestEndpointUtils.insertFrapModel(agroalConnectionFactory);
        savedSimulation = savedBioModel.getSimulation(0);
        simulationKey = savedSimulation.getVersion().getVersionKey();
    }

    private ExportHistoryDBRep getExportHistoryRep(int jobID, String uri, Timestamp timestamp) {
        VCSimulationIdentifier vcSimId = new VCSimulationIdentifier(simulationKey, TestEndpointUtils.administratorUser);
        VCDataIdentifier vcdId = new VCSimulationDataIdentifier(vcSimId, jobID);
        GeometrySpecs geometrySpecs = new GeometrySpecs(null, 1, 1, ExportEnums.GeometryMode.GEOMETRY_FULL);

        TimeSpecs timeSpecs = new TimeSpecs(0, 1, new double[]{0.0, 1.0}, ExportEnums.TimeMode.TIME_RANGE);
        VariableSpecs variableSpecs = new VariableSpecs(new String[]{"X"}, ExportEnums.VariableMode.VARIABLE_ONE);


        List<HumanReadableExportData.DifferentParameterValues> parameterValues = new ArrayList<>() {{
            add(new HumanReadableExportData.DifferentParameterValues("parameter_name", "original_0", "changed_1"));
            add(new HumanReadableExportData.DifferentParameterValues("parameter_name2", "original_0", "changed_1"));
        }};

        return new ExportHistoryDBRep(
                jobID, savedBioModel.getVersion().getVersionKey(),
                null, savedSimulation.getKey(), savedSimulation.getMathDescription().getKey(), ExportFormat.N5,
                timestamp, uri, variableSpecs.getVariableNames(),
                1.2, 3.4, ExportEnums.ExportProgressType.EXPORT_COMPLETE
        );
    }


    @AfterEach
    public void cleanUp() throws SQLException, DataAccessException {
        String sql = "DELETE FROM VC_SIMULATION_EXPORT_HISTORY";
        Connection connection = agroalConnectionFactory.getConnection(null);
        connection.prepareStatement(sql).execute();
        connection.commit();
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }


    @Test
    public void testAddExportHistory() {
        User user = TestEndpointUtils.administratorUser;
        Timestamp now = Timestamp.from(Instant.now());

        try (Connection conn = agroalConnectionFactory.getConnection(null)) {
            ExportHistoryDBDriver driver = new ExportHistoryDBDriver(null, null);
            ExportHistoryDBRep rep = getExportHistoryRep(
                    42,
                    "https://vcell.cam.uchc.edu/n5Data/paulricky/5456fb59b530a19.n5?dataSetName=3681309072",
                    now
            );

            driver.addExportHistory(conn, user, rep, agroalConnectionFactory.getKeyFactory());
            try {
                List<ExportHistory> exportHistoryRecord = driver.getExportHistoryForUser(conn, user);
                Assertions.assertEquals(1, exportHistoryRecord.size(), "expected one record");
                Assertions.assertTrue(historyDBEqualHistory(rep, exportHistoryRecord.get(0)));
                Assertions.assertEquals(savedBioModel.getName(), exportHistoryRecord.get(0).modelName());
                Assertions.assertEquals(savedSimulation.getName(), exportHistoryRecord.get(0).simName());
            } catch (SQLException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeleteExportHistory() throws SQLException {
        User user = TestEndpointUtils.administratorUser;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (Connection conn = agroalConnectionFactory.getConnection(null)) {
            ExportHistoryDBDriver driver = new ExportHistoryDBDriver(null, null);
            ExportHistoryDBRep exportHistoryDBRep = getExportHistoryRep(7, "to-delete", now);
            ExportHistoryDBRep notDeletedRep = getExportHistoryRep(8, "to-keep", now);


            driver.addExportHistory(conn, user,
                    exportHistoryDBRep,
                    agroalConnectionFactory.getKeyFactory()
            );
            driver.addExportHistory(conn, user,
                    notDeletedRep,
                    agroalConnectionFactory.getKeyFactory()
            );


            List<ExportHistory> retrievedExportHistory = driver.getExportHistoryForUser(conn, user);
            Assertions.assertEquals(2, retrievedExportHistory.size(), "expected two records");
            Assertions.assertTrue(historyDBEqualHistory(exportHistoryDBRep, retrievedExportHistory.get(0)));
            Assertions.assertTrue(historyDBEqualHistory(notDeletedRep, retrievedExportHistory.get(1)));

            driver.deleteExportHistory(conn, exportHistoryDBRep.uri());
            retrievedExportHistory = driver.getExportHistoryForUser(conn, user);
            Assertions.assertEquals(1, retrievedExportHistory.size(), "expected one record after deletion");
            Assertions.assertTrue(historyDBEqualHistory(notDeletedRep, retrievedExportHistory.get(0)));
        } catch (DataAccessException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetExportHistory() throws SQLException, DataAccessException, JsonProcessingException {
        User user = TestEndpointUtils.administratorUser;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (Connection conn = agroalConnectionFactory.getConnection(null)) {
            ExportHistoryDBDriver driver = new ExportHistoryDBDriver(null, null);
            ExportHistoryDBRep exportHistoryDBRep = getExportHistoryRep(100, "uri100", now);
            ExportHistoryDBRep exportHistoryDBRep1 = getExportHistoryRep(101, "uri101", now);

            driver.addExportHistory(conn, user,
                    exportHistoryDBRep,
                    agroalConnectionFactory.getKeyFactory()
            );
            driver.addExportHistory(conn, user,
                    exportHistoryDBRep1,
                    agroalConnectionFactory.getKeyFactory()
            );

            List<ExportHistory> retrievedExportHistory = driver.getExportHistoryForUser(conn, user);
            Assertions.assertEquals(2, retrievedExportHistory.size(), "expected two records");
            Assertions.assertTrue(historyDBEqualHistory(exportHistoryDBRep, retrievedExportHistory.get(0)));
            Assertions.assertTrue(historyDBEqualHistory(exportHistoryDBRep1, retrievedExportHistory.get(1)));
        }
    }

    private boolean historyDBEqualHistory(ExportHistoryDBRep dbRep, ExportHistory history) {
        return dbRep.simulationRef().equals(history.simulationRef()) &&
                dbRep.exportFormat().equals(history.exportFormat()) &&
                history.exportDate().equals(dbRep.exportDate().toInstant()) &&
                dbRep.uri().equals(history.uri()) &&
                List.of(dbRep.variables()).equals(List.of(history.variables())) &&
                Double.compare(dbRep.startTimeValue(), history.startTimeValue()) == 0 &&
                Double.compare(dbRep.endTimeValue(), history.endTimeValue()) == 0 &&
                dbRep.eventStatus().toString().equals(history.eventStatus().toString());
    }
}
