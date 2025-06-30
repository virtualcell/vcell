package org.vcell.restq;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiException;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

// TODO: Use GenericVCMLTests class for the principle tests for VCDocuments
@QuarkusTest
public class ExportHistoryTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;


    private DatabaseServerImpl databaseServer;
    private BioModel savedBioModel;


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
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }

    @Test
    public void testAddExportHistory() {
        User user = TestEndpointUtils.administratorUser;


    }

    @Test
    public void testDeleteExportHistory() {

    }

    @Test
    public void testGetExportHistory() {

    }
}
