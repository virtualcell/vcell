package org.vcell.restq;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.export.server.*;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ExportHistoryDBDriver;
import cbit.vcell.modeldb.ExportHistoryRep;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.BigString;
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
import static org.vcell.util.VCAssert.assertFalse;
import static org.vcell.util.VCAssert.assertTrue;

// TODO: Use GenericVCMLTests class for the principle tests for VCDocuments
@QuarkusTest
public class ExportHistoryTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    private ApiClient apiClient;
    private User testUser;

    private DatabaseServerImpl databaseServer;
    private BioModel savedBioModel;
    private Simulation savedSimulation;
    private KeyValue simulationKey;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients() throws ApiException, DataAccessException, PropertyVetoException, XmlParseException, IOException {
        databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        BioModel bioModel = TestEndpointUtils.getTestBioModel();
        BigString bioModelXML = databaseServer.saveBioModel(TestEndpointUtils.administratorUser, new BigString(XmlHelper.bioModelToXML(bioModel)), new String[]{});
        savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelXML.toString()));
        savedSimulation = savedBioModel.getSimulation(0);
        simulationKey = savedSimulation.getVersion().getVersionKey();
    }

    private ExportHistoryRep getExportHistoryRep(int jobID, String uri, Timestamp timestamp){
        VCSimulationIdentifier vcSimId = new VCSimulationIdentifier(simulationKey, TestEndpointUtils.administratorUser);
        VCDataIdentifier vcdId = new VCSimulationDataIdentifier(vcSimId, jobID);
        GeometrySpecs geometrySpecs = new GeometrySpecs(null, 1, 1, ExportEnums.GeometryMode.GEOMETRY_FULL);

        TimeSpecs timeSpecs = new TimeSpecs(0, 1, new double[]{0.0, 1.0}, ExportEnums.TimeMode.TIME_RANGE);
        VariableSpecs variableSpecs = new VariableSpecs(new String[]{"X"}, ExportEnums.VariableMode.VARIABLE_ONE);

        N5Specs n5Specs = new N5Specs(ExportEnums.ExportableDataType.PDE_VARIABLE_DATA, ExportFormat.N5,
                N5Specs.CompressionLevel.RAW, "test dataset name");


        List<HumanReadableExportData.DifferentParameterValues> parameterValues = new ArrayList<>(){{
            add(new HumanReadableExportData.DifferentParameterValues("parameter_name", "original_0", "changed_1"));
        }};

        return new ExportHistoryRep(
                jobID, vcSimId.getSimulationKey(), n5Specs.getFormatType(),
                timestamp, uri, vcdId.getID(), savedSimulation.getName(),
                "Application Name", savedBioModel.getName(), variableSpecs.getVariableNames(),
                parameterValues, timeSpecs.getAllTimes()[0], timeSpecs.getAllTimes()[1],
                "file_on_server", n5Specs.getDataType().name(), false,
                0, 1, 1
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


            driver.addExportHistory(
                    conn,
                    user,
                    getExportHistoryRep(
                            42,
                            "https://vcell.cam.uchc.edu/n5Data/paulricky/5456fb59b530a19.n5?dataSetName=3681309072",
                            now
                    ),
                    agroalConnectionFactory.getKeyFactory()
            );

            try (ResultSet rs = driver.getExportHistoryForUser(conn, user)) {
                Assertions.assertTrue(rs.next(), "expected one record");
                Assertions.assertEquals(42L, rs.getInt("job_id"));
                Assertions.assertEquals("https://vcell.cam.uchc.edu/n5Data/paulricky/5456fb59b530a19.n5?dataSetName=3681309072", rs.getString("uri"));
                Assertions.assertEquals("N5", rs.getString("export_format"));
                Assertions.assertFalse(rs.next(), "only one record should exist");
            } catch (SQLException e) {
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
            ExportHistoryRep exportHistoryRep = getExportHistoryRep(7, "to-delete", now);
            ExportHistoryRep notDeletedRep = getExportHistoryRep(8, "to-keep", now);


            driver.addExportHistory(conn, user,
                    exportHistoryRep,
                    agroalConnectionFactory.getKeyFactory()
            );
            driver.addExportHistory(conn, user,
                    notDeletedRep,
                    agroalConnectionFactory.getKeyFactory()
            );


            try (ResultSet rs = driver.getExportHistoryForUser(conn, user)) {
                Assertions.assertTrue(rs.next());
                Assertions.assertEquals("to-delete", rs.getString("uri"));
                Assertions.assertTrue(rs.next());
                Assertions.assertEquals("to-keep", rs.getString("uri"));
            }

            driver.deleteExportHistory(conn, exportHistoryRep.uri());
            try (ResultSet rs = driver.getExportHistoryForUser(conn, user)) {
                Assertions.assertTrue(rs.next());
                Assertions.assertEquals("to-keep", rs.getString("uri"));
                assertFalse(rs.next(),"No rows should remain after deletion");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetExportHistory() throws SQLException, DataAccessException {
        User user = TestEndpointUtils.administratorUser;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (Connection conn = agroalConnectionFactory.getConnection(null)) {
            ExportHistoryDBDriver driver = new ExportHistoryDBDriver(null, null);
            ExportHistoryRep exportHistoryRep = getExportHistoryRep(100, "uri100", now);
            ExportHistoryRep exportHistoryRep1 = getExportHistoryRep(101, "uri101", now);

            driver.addExportHistory(conn, user,
                    exportHistoryRep,
                    agroalConnectionFactory.getKeyFactory()
            );
            driver.addExportHistory(conn, user,
                    exportHistoryRep1,
                    agroalConnectionFactory.getKeyFactory()
            );

            int count = 0;
            try (ResultSet rs = driver.getExportHistoryForUser(conn, user)) {
                while (rs.next()) {
                    count++;
                    Assertions.assertTrue(rs.getString("uri").startsWith("uri"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Assertions.assertEquals(2, count);
        }
    }
}
