package org.vcell.restq.apiclient;

import cbit.vcell.math.VariableType;
import cbit.vcell.resource.PropertyLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.FieldDataResourceApi;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.AnalyzedResultsFromFieldData;
import org.vcell.restclient.model.FieldDataReference;
import org.vcell.restclient.model.FieldDataSaveResults;
import org.vcell.restclient.model.FieldDataShape;
import org.vcell.restclient.utils.DtoModelTransforms;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class FieldDataAPITest {

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;
    private static String previousPrimarySimDir;
    private static String previousSecondarySimDir;

    private final static File temporaryFolder = new File(System.getProperty("java.io.tmpdir") + "/fieldDataTest");

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients() throws ApiException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
        UsersResourceApi usersResourceApi = new UsersResourceApi(aliceAPIClient);
        usersResourceApi.mapUser(TestEndpointUtils.administratorUserLoginInfo);

        temporaryFolder.mkdirs();

        previousPrimarySimDir = PropertyLoader.getProperty(PropertyLoader.primarySimDataDirInternalProperty, null);
        PropertyLoader.setProperty(PropertyLoader.primarySimDataDirInternalProperty, temporaryFolder.getAbsolutePath());

        previousSecondarySimDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirExternalProperty, null);
        System.setProperty(PropertyLoader.secondarySimDataDirExternalProperty, temporaryFolder.getAbsolutePath());
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException, IOException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
        if (previousPrimarySimDir != null){
            PropertyLoader.setProperty(PropertyLoader.primarySimDataDirInternalProperty, previousPrimarySimDir);
        }
        if (previousSecondarySimDir != null){
            System.setProperty(PropertyLoader.secondarySimDataDirExternalProperty, previousSecondarySimDir);
        }

        FileUtils.cleanDirectory(temporaryFolder);
    }


    @Test
    public void testAddAndDeleteFieldDataFromFile() throws ApiException {
        FieldDataResourceApi fieldDataResourceApi = new FieldDataResourceApi(aliceAPIClient);
        List<List<List<Integer>>> matrix = new ArrayList<>();
        List<String> varNames = new ArrayList<>(){{add("Variable 1");}};
        List<Double> times = new ArrayList<>(); times.add(0.0); times.add(1.0);

        double x = 5;
        double y = 4;
        double z = 3;
        for (int t = 0; t < times.size(); t++){
            List<List<Integer>> tArr = new ArrayList<>();
            for (int vars = 0; vars < varNames.size(); vars++){
                List<Integer> vArr = new ArrayList<>();
                for (int i = 0; i < (x * y * z); i++){
                    vArr.add(i);
                }
                tArr.add(vArr);
            }
            matrix.add(tArr);
        }
        VariableType varType = VariableType.getVariableTypeFromInteger(1);
        Origin origin = new Origin(0.0, 0.0, 0.0);
        Extent extent = new Extent(x, y, z);
        ISize iSize = new ISize(1, 1, 1);

        /////////////////////
        // Add Field Data //
        ///////////////////
        AnalyzedResultsFromFieldData saveFieldDataFromFile = new AnalyzedResultsFromFieldData();
        saveFieldDataFromFile.setShortSpecData(matrix); saveFieldDataFromFile.varNames(varNames);
        saveFieldDataFromFile.times(times); saveFieldDataFromFile.origin(DtoModelTransforms.originToDTO(origin)); saveFieldDataFromFile.extent(DtoModelTransforms.extentToDTO(extent));
        saveFieldDataFromFile.isize(DtoModelTransforms.iSizeToDTO(iSize)); saveFieldDataFromFile.annotation("test annotation"); saveFieldDataFromFile.name("TestFile");
        FieldDataSaveResults results = fieldDataResourceApi.createFieldDataFromAnalyzedFile(saveFieldDataFromFile);

        // File is Saved on File System
        FieldDataShape fieldDataInfo = fieldDataResourceApi.getFieldDataShapeFromID(results.getFieldDataID());
        Assertions.assertEquals(saveFieldDataFromFile.getName(), results.getFieldDataName());
        Assertions.assertTrue(origin.compareEqual(DtoModelTransforms.dtoToOrigin(fieldDataInfo.getOrigin())));
        Assertions.assertTrue(extent.compareEqual(DtoModelTransforms.dtoToExtent(fieldDataInfo.getExtent())));
        Assertions.assertTrue(iSize.compareEqual(DtoModelTransforms.dtoToISize(fieldDataInfo.getIsize())));
        Assertions.assertEquals(times, fieldDataInfo.getTimes());

        // It's in the DB
        List<FieldDataReference> references = fieldDataResourceApi.getAllFieldDataIDs();
        Assertions.assertEquals(saveFieldDataFromFile.getAnnotation(), references.get(0).getExternalDataAnnotation());
        Assertions.assertEquals(results.getFieldDataID(), references.get(0).getExternalDataIdentifier().getKey().getValue().toString());
        Assertions.assertEquals(0, references.get(0).getExternalDataIDSimRefs().size());

        ///////////////////////
        // Delete Field Data //
        //////////////////////
        fieldDataResourceApi.deleteFieldData(results.getFieldDataID());

        // No Longer on File System
        try{
            fieldDataResourceApi.getFieldDataShapeFromID(results.getFieldDataID());
        } catch (ApiException e){
            Assertions.assertEquals(404, e.getCode());
        }

        // No Longer in DB
        references = fieldDataResourceApi.getAllFieldDataIDs();
        Assertions.assertEquals(0, references.size());
    }


    @Test
    public void testFileUploading() throws ApiException, IOException {
        FieldDataResourceApi fieldDataResourceApi = new FieldDataResourceApi(aliceAPIClient);
        File testFile = TestEndpointUtils.getResourceFile("/flybrain-035.tif");
        try{
            fieldDataResourceApi.analyzeFieldDataFile(testFile, "invalid N@me #Ezequiel");
        } catch (ApiException e){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST_400, e.getCode());
        }

        AnalyzedResultsFromFieldData results = fieldDataResourceApi.analyzeFieldDataFile(testFile, "bob");

        Assertions.assertNotNull(results);
        Assertions.assertNotNull(results.getShortSpecData());

        FieldDataSaveResults saveResults = fieldDataResourceApi.createFieldDataFromAnalyzedFile(results);
        Assertions.assertNotNull(saveResults);

        FieldDataShape fieldDataInfo = fieldDataResourceApi.getFieldDataShapeFromID(saveResults.getFieldDataID());
        Assertions.assertNotNull(fieldDataInfo);
        Assertions.assertTrue(DtoModelTransforms.dtoToOrigin(fieldDataInfo.getOrigin()).compareEqual(new Origin(0.0, 0.0, 0.0)));
        Assertions.assertTrue(DtoModelTransforms.dtoToExtent(fieldDataInfo.getExtent()).compareEqual(new Extent(684.9333393978472, 684.9333393978472, 1)));
        Assertions.assertTrue(DtoModelTransforms.dtoToISize(fieldDataInfo.getIsize()).compareEqual(new ISize(256, 256, 1)));
        Assertions.assertEquals(1, fieldDataInfo.getTimes().size());

        fieldDataResourceApi.deleteFieldData(saveResults.getFieldDataID());

        try{
            fieldDataResourceApi.getFieldDataShapeFromID(saveResults.getFieldDataID());
        } catch (ApiException e){
            Assertions.assertEquals(404, e.getCode());
        }

        // No Longer in DB
        List<FieldDataReference> references = fieldDataResourceApi.getAllFieldDataIDs();
        Assertions.assertEquals(0, references.size());
    }

}
