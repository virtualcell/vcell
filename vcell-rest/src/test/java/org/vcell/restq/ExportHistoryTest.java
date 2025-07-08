package org.vcell.restq;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.export.server.*;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ExportHistoryDBDriver;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.SpatialSelection;
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
import org.vcell.restq.handlers.ExportResource;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.DependencyException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.export.server.ExportSpecs;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.bouncycastle.math.raw.Nat.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.vcell.sybil.util.http.pathwaycommons.PCRParameter.Output.xml;
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

    private ExportSpecs exportSpecs;


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


        VCSimulationIdentifier vcSimId = new VCSimulationIdentifier(
                new KeyValue("000"), TestEndpointUtils.administratorUser
        );
        VCDataIdentifier vcdId = new VCSimulationDataIdentifier(vcSimId, 0);


        GeometrySpecs geometrySpecs = new GeometrySpecs(null, 1, 1, 1);


        double[] times = new double[]{0.0, 1.0};
        TimeSpecs timeSpecs = new TimeSpecs(0, 1, times, times.length);


        VariableSpecs variableSpecs = new VariableSpecs(new String[]{"X"}, 1);


        FormatSpecificSpecs formatSpecific = new FormatSpecificSpecs() {
            @Override public boolean equals(Object o) { return this==o; }
            @Override public String toString() { return "DUMMY"; }
        };


        HumanReadableExportData meta = new HumanReadableExportData(
                "simName",                      // simulation name
                "appName",                      // application name
                savedBioModel.getName(),        // biomodel name
                new ArrayList<String>(),        // no parameter changes
                vcdId.getID(),                  // dataId
                "out.n5",                       // serverSavedFileName
                false,                          // nonSpatial?
                new HashMap<>()                 // no per‐slice parameters
        );


        exportSpecs = new ExportSpecs(
                vcdId,
                ExportFormat.N5,
                variableSpecs,
                timeSpecs,
                geometrySpecs,
                formatSpecific,
                "simName",
                savedBioModel.getName()
        );
        exportSpecs.setExportMetaData(meta);

    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }


    @Test
    public void testAddExportHistory() {
        User user = TestEndpointUtils.administratorUser;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (Connection conn = agroalConnectionFactory.getConnection(null)) {
            ExportHistoryDBDriver driver = new ExportHistoryDBDriver(null, null);


            driver.addExportHistory(
                    conn,
                    user,
                    new ExportHistoryDBDriver.ExportHistory(
                            42L,
                            Long.parseLong(savedBioModel.getModel().getVersion().getVersionKey().toString()),
                            ExportFormat.N5,
                            now,
                            "https://vcell.cam.uchc.edu/n5Data/paulricky/5456fb59b530a19.n5?dataSetName=3681309072",
                            exportSpecs
                    )
            );

            try (ResultSet rs = driver.getExportHistoryForUser(conn, user)) {
                Assertions.assertTrue(rs.next(), "expected one record");
                Assertions.assertEquals(42L, rs.getLong("job_id"));
                Assertions.assertEquals("https://vcell.cam.uchc.edu/n5Data/paulricky/5456fb59b530a19.n5?dataSetName=3681309072", rs.getString("uri"));
                Assertions.assertEquals("N5", rs.getString("export_format"));
                Assertions.assertFalse(rs.next(), "only one record should exist");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ObjectNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DependencyException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    public void testDeleteExportHistory() throws SQLException {
        User user = TestEndpointUtils.administratorUser;
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try (Connection conn = agroalConnectionFactory.getConnection(null)) {
            ExportHistoryDBDriver driver = new ExportHistoryDBDriver(null, null);


            driver.addExportHistory(conn, user,
                    new ExportHistoryDBDriver.ExportHistory(7L, Long.parseLong(savedBioModel.getModel().getVersion().getVersionKey().toString()), ExportFormat.N5, now, "to-delete", exportSpecs)
            );


            try (ResultSet rs = driver.getExportHistoryForUser(conn, user)) {
                Assertions.assertTrue(rs.next());
                Assertions.assertEquals("to-delete", rs.getString("uri"));
            }


            driver.deleteExportHistory(conn, exportSpecs);

            try (ResultSet rs = driver.getExportHistoryForUser(conn, user)) {
                Assertions.assertFalse(rs.next());
                Assertions.assertNotEquals("to-delete", rs.getString("uri"));
            }


            driver.deleteExportHistory(conn, exportSpecs);


            try (ResultSet rs = driver.getExportHistoryForUser(conn, user)) {
                assertFalse(rs.next(),"No rows should remain after deletion");
            }
        } catch (ObjectNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DependencyException e) {
            throw new RuntimeException(e);
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

            driver.addExportHistory(conn, user,
                    new ExportHistoryDBDriver.ExportHistory(100L, Long.parseLong(savedBioModel.getModel().getVersion().getVersionKey().toString()), ExportFormat.N5, now, "uri100", exportSpecs)
            );
            driver.addExportHistory(conn, user,
                    new ExportHistoryDBDriver.ExportHistory(101L, Long.parseLong(savedBioModel.getModel().getVersion().getVersionKey().toString()), ExportFormat.N5, now, "uri101", exportSpecs)
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
