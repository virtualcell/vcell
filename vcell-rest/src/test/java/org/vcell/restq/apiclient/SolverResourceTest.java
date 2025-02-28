package org.vcell.restq.apiclient;

import cbit.util.xml.VCLoggerException;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solvers.FunctionFileGenerator;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.*;
import org.testcontainers.shaded.com.google.common.io.Files;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.SolverResourceApi;
import org.vcell.restclient.api.SpatialResourceApi;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.handlers.SolverResource;
import org.vcell.util.DataAccessException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

@QuarkusTest
public class SolverResourceTest {
    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;
    private static String previousServerID;
    private static String previousMongoHost;
    private static String previousMongoPort;
    private static String previousMongoDB;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
        previousServerID = PropertyLoader.getProperty(PropertyLoader.vcellServerIDProperty, null);
        PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, "TEST2");

        previousMongoDB = PropertyLoader.getProperty(PropertyLoader.mongodbDatabase, null);
        PropertyLoader.setProperty(PropertyLoader.mongodbDatabase, "test");

        previousMongoHost = PropertyLoader.getProperty(PropertyLoader.mongodbHostInternal, null);
        PropertyLoader.setProperty(PropertyLoader.mongodbHostInternal, "localhost");

        previousMongoPort = PropertyLoader.getProperty(PropertyLoader.mongodbPortInternal, null);
        PropertyLoader.setProperty(PropertyLoader.mongodbPortInternal, "9080");
    }

    @AfterAll
    public static void resetConfig(){
        if (previousServerID != null) {PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, previousServerID);};
        if (previousMongoHost != null) {PropertyLoader.setProperty(PropertyLoader.mongodbHostInternal, previousMongoHost);}
        if (previousMongoDB != null) {PropertyLoader.setProperty(PropertyLoader.mongodbDatabase, previousMongoDB);}
        if (previousMongoPort != null) {PropertyLoader.setProperty(PropertyLoader.mongodbPortInternal, previousMongoPort);}
    }

    @BeforeEach
    public void createClients() throws JoseException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }

    // Ensure the round trip from request to resulting Zip is equivalent to a local run of input file generation
    @Test
    public void testSpacialSBMLResults() throws ApiException, IOException, VCLoggerException, SolverException, ExpressionException, PropertyVetoException, MappingException {
        SolverResourceApi spatialResourceApi = new SolverResourceApi(aliceAPIClient);
        File sbmlFile = TestEndpointUtils.getResourceFile("/TinySpacialProject_Application0.xml");

        File zip = spatialResourceApi.getFVSolverInputFromSBML(sbmlFile, 5.0, 0.1);
        File unzipDir = Files.createTempDir();
        File outputDir = Files.createTempDir();

        SolverResource.sbmlToFiniteVolumeInput(sbmlFile, outputDir, 5.0, 0.1);

        ZipFile zipFile = new ZipFile(zip);
        zipFile.extractAll(unzipDir.getAbsolutePath());
        zipFile.close();


        for (int i = 0; i < outputDir.listFiles().length; i++) {
            File output = outputDir.listFiles()[i];
            File unzip = Arrays.stream(unzipDir.listFiles()).filter(
                    f -> f.getName().split("\\.")[1].equals(output.getName().split("\\.")[1])
            ).findFirst().orElse(null);
            if (output.getName().split("\\.")[1].equals("functions")){
                Vector<AnnotatedFunction> outPutAnnotated = FunctionFileGenerator.readFunctionsFile(output, "0");
                Vector<AnnotatedFunction> unzipAnnotated = FunctionFileGenerator.readFunctionsFile(unzip, "0");
                for (int j = 0; j < outPutAnnotated.size(); j++) {
                    Assertions.assertTrue(outPutAnnotated.get(j).compareEqual(unzipAnnotated.get(j)));
                }
            } else if (output.getName().split("\\.")[1].equals("fvinput")) {
                Scanner outputReader = new Scanner(output);
                Scanner unzipReader = new Scanner(unzip);

                while (outputReader.hasNextLine()){
                    String outputLine = outputReader.nextLine();
                    String unzipLine = unzipReader.nextLine();
                    // Don't compare the file path
                    if (!outputLine.contains("FILE")){
                        Assertions.assertEquals(outputLine, unzipLine);
                    }
                }
            } else{
                Assertions.assertTrue(FileUtils.contentEquals(output, unzip));
            }
        }
    }

    // Ensure the round trip from request to resulting Zip is equivalent to a local run of input file generation
    @Test
    public void testVCMLResults() throws ApiException, IOException, SolverException, ExpressionException, MappingException, XmlParseException {
        SolverResourceApi spatialResourceApi = new SolverResourceApi(aliceAPIClient);
        File vcmlFile = TestEndpointUtils.getResourceFile("/TinySpacialProject_Application0.vcml");

        String simulation_name = "Simulation0";
        File zip = spatialResourceApi.getFVSolverInputFromVCML(vcmlFile, simulation_name);
        File unzipDir = Files.createTempDir();
        File outputDir = Files.createTempDir();

        SolverResource.vcmlToFiniteVolumeInput(vcmlFile, simulation_name, outputDir);

        ZipFile zipFile = new ZipFile(zip);
        zipFile.extractAll(unzipDir.getAbsolutePath());
        zipFile.close();


        for (int i = 0; i < outputDir.listFiles().length; i++) {
            File output = outputDir.listFiles()[i];
            File unzip = Arrays.stream(unzipDir.listFiles()).filter(
                    f -> f.getName().split("\\.")[1].equals(output.getName().split("\\.")[1])
            ).findFirst().orElse(null);
            if (output.getName().split("\\.")[1].equals("functions")){
                Vector<AnnotatedFunction> outPutAnnotated = FunctionFileGenerator.readFunctionsFile(output, "0");
                Vector<AnnotatedFunction> unzipAnnotated = FunctionFileGenerator.readFunctionsFile(unzip, "0");
                for (int j = 0; j < outPutAnnotated.size(); j++) {
                    Assertions.assertTrue(outPutAnnotated.get(j).compareEqual(unzipAnnotated.get(j)));
                }
            } else if (output.getName().split("\\.")[1].equals("fvinput")) {
                Scanner outputReader = new Scanner(output);
                Scanner unzipReader = new Scanner(unzip);

                while (outputReader.hasNextLine()){
                    String outputLine = outputReader.nextLine();
                    String unzipLine = unzipReader.nextLine();
                    // Don't compare the file path
                    if (!outputLine.contains("FILE")){
                        Assertions.assertEquals(outputLine, unzipLine);
                    }
                }
            } else{
                Assertions.assertTrue(FileUtils.contentEquals(output, unzip));
            }
        }
    }
}
