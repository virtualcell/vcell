package org.vcell.restq;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.model.Feature;
import cbit.vcell.model.Kinetics;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.CustomObjectMapper;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.Publication;
import org.vcell.restclient.model.UserLoginInfoForMapping;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class TestEndpointUtils {

    public static final String userAdminKey = "2";
    public static final String userNagiosKey = "3";
    public static final User vcellNagiosUser = new User("vcellNagios", new KeyValue(userNagiosKey));
    public static final User administratorUser = new User("Administrator", new KeyValue(userAdminKey));
    public static final User vcellSupportUser = new User("VCellSupport", new KeyValue("4"));
    public static final User randomUser = new User("randomUser", new KeyValue("5"));

    public static final UserLoginInfoForMapping vcellNagiosUserLoginInfo = new UserLoginInfoForMapping().userID("vcellNagios").password("1700596370261");
    public static final UserLoginInfoForMapping administratorUserLoginInfo = new UserLoginInfoForMapping().userID("Administrator").password("1700596370260");

    public static void removeAllMappings(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException, SQLException {
        Object object = new Object();
        Connection connection = agroalConnectionFactory.getConnection(object);
        connection.prepareStatement("DELETE FROM VC_USERIDENTITY").execute();
        connection.commit();
        connection.close();
    }

    public static void mapApiClientToAdmin(ApiClient apiClient) throws ApiException {
        boolean mapped = new UsersResourceApi(apiClient).mapUser(administratorUserLoginInfo);
        if (!mapped) throw new ApiException("Mapping failed");
    }

    public static void mapApiClientToNagios(ApiClient apiClient) throws ApiException {
        boolean mapped = new UsersResourceApi(apiClient).mapUser(vcellNagiosUserLoginInfo);
        if (!mapped) throw new ApiException("Mapping failed");
    }

    public enum TestOIDCUsers{
        alice,
        bob;
    }

    public static ApiClient createAuthenticatedAPIClient(KeycloakTestClient keycloakClient, int testPort, TestOIDCUsers oidcUser){
        ApiClient apiClient = createUnAuthenticatedAPIClient(testPort);
        String oidcAccessToken = keycloakClient.getAccessToken(oidcUser.name());
        apiClient.setObjectMapper(new CustomObjectMapper());
        apiClient.setRequestInterceptor(request -> request.header("Authorization", "Bearer " + oidcAccessToken));
        return apiClient;
    }

    public static ApiClient createUnAuthenticatedAPIClient(int testPort){
        ApiClient apiClient = new ApiClient();
        apiClient.setObjectMapper(new CustomObjectMapper());
        apiClient.setScheme("http");
        apiClient.setHost("localhost");
        apiClient.setPort(testPort);
        return apiClient;
    }

    public static cbit.vcell.biomodel.BioModel getTestBioModel() throws XmlParseException, PropertyVetoException, IOException {
        String vcmlString = IOUtils.toString(TestEndpointUtils.class.getResourceAsStream("/TestVCML.vcml"));
        cbit.vcell.biomodel.BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlString));
        bioModel.setName("TestBioModel");
        bioModel.clearVersion();
        return bioModel;
    }

    public static MathModel getTestMathModel() throws XmlParseException, PropertyVetoException, IOException {
        String mathString = IOUtils.toString(TestEndpointUtils.class.getResourceAsStream("/TestMath.vcml"));
        MathModel mathModel = XmlHelper.XMLToMathModel(new XMLSource(mathString));
        mathModel.setName("TestMathModel");
        mathModel.clearVersion();
        return mathModel;
    }

    public static Publication defaultPublication(){
        Publication publication = new Publication();
        publication.setAuthors(Arrays.asList("author1", "author2"));
        publication.setCitation("citation");
        publication.setDoi("doi");
        publication.setEndnoteid(0);
        publication.setPubmedid("pubmedId");
        publication.setTitle("publication 1");
        publication.setUrl("url");
        publication.setWittid(0);
        publication.setYear(1994);
        publication.setBiomodelRefs(new ArrayList<>());
        publication.setMathmodelRefs(new ArrayList<>());
        publication.setDate(LocalDate.now());
        return publication;
    }

    public static BioModel defaultBiomodel() throws Exception {
        BioModel biomodel = new BioModel(null);
        Feature comp = biomodel.getModel().createFeature();
        SpeciesContext sc = biomodel.getModel().createSpeciesContext(comp);
        SimpleReaction simpleReaction = biomodel.getModel().createSimpleReaction(comp);
        simpleReaction.addReactant(sc,1);
        simpleReaction.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_KForward).setExpression(new Expression(1.0));
        SimulationContext simContext1 = biomodel.addNewSimulationContext("application1", SimulationContext.Application.NETWORK_DETERMINISTIC);
        SimulationContext.MathMappingCallback callback = new SimulationContext.MathMappingCallback() {
            @Override public void setMessage(String message) {}
            @Override public void setProgressFraction(float fractionDone) {}
            @Override public boolean isInterrupted() { return false;}
        };
        Simulation simulation = simContext1.addNewSimulation("simulation1", callback, SimulationContext.NetworkGenerationRequirements.ComputeLongTimeout);
        simulation.getSolverTaskDescription().setTimeBounds(new TimeBounds(0, 10));
        return biomodel;
    }

    public static File getResourceFile(String relativeResourcePath) throws IOException {
        InputStream inputStream = Objects.requireNonNull(TestEndpointUtils.class.getResourceAsStream(relativeResourcePath));
        File tmpFile = File.createTempFile("tmp-" + RandomStringUtils.randomAlphabetic(10), ".api-test-file");
        tmpFile.deleteOnExit();
        FileUtils.copyInputStreamToFile(inputStream, tmpFile);
        return tmpFile;
    }

    public static String getResourceString(String relativeResourcePath) throws IOException {
        InputStream inputStream = Objects.requireNonNull(TestEndpointUtils.class.getResourceAsStream(relativeResourcePath));
        return IOUtils.toString(inputStream);
    }

    /**
     * Empties out all BioModel, Geometry, math, and simulation related tables
     */
    public static void clearAllBioModelEntries(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException, SQLException {
        Object object = new Object();
        Connection connection = agroalConnectionFactory.getConnection(object);
        connection.prepareStatement("DELETE FROM VC_BIOMODEL").execute();
        connection.prepareStatement("DELETE FROM VC_BIOMODELXML").execute();
        connection.prepareStatement("DELETE FROM VC_BIOMODELSIM").execute();
        connection.prepareStatement("DELETE FROM VC_METADATA").execute();
        connection.prepareStatement("DELETE FROM VC_BIOMODELSIMCONTEXT").execute();

        connection.prepareStatement("DELETE FROM VC_MATHMODELSIM").execute();
        connection.prepareStatement("DELETE FROM VC_SIMCONTEXT").execute();
        connection.prepareStatement("DELETE FROM VC_SIMULATION").execute();

        connection.prepareStatement("DELETE FROM VC_MATHMODELXML").execute();
        connection.prepareStatement("DELETE FROM VC_MATHMODEL").execute();
        connection.prepareStatement("DELETE FROM VC_MATH").execute();

        connection.prepareStatement("DELETE FROM VC_IMAGE").execute();
        connection.prepareStatement("DELETE FROM VC_IMAGEDATA").execute();
        connection.prepareStatement("DELETE FROM VC_IMAGEREGION").execute();

        connection.prepareStatement("DELETE FROM VC_GEOMETRY").execute();
        connection.prepareStatement("DELETE FROM VC_GEOMETRICREGION").execute();
        connection.prepareStatement("DELETE FROM VC_GEOMEXTENT").execute();

        connection.commit();
        new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory()).cleanupDatabase();
    }
}
