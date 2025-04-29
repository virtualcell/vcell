package org.vcell.restq.apiclient;

import cbit.vcell.VirtualMicroscopy.BioformatsImageImpl;
import cbit.vcell.VirtualMicroscopy.BioformatsImageImplLegacy;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
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
import org.vcell.restclient.model.FieldData;
import org.vcell.restclient.model.FieldDataReference;
import org.vcell.restclient.model.FieldDataSavedResults;
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
        FieldData saveFieldDataFromFile = new FieldData();
        saveFieldDataFromFile.setShortSpecData(matrix); saveFieldDataFromFile.varNames(varNames);
        saveFieldDataFromFile.times(times); saveFieldDataFromFile.origin(DtoModelTransforms.originToDTO(origin)); saveFieldDataFromFile.extent(DtoModelTransforms.extentToDTO(extent));
        saveFieldDataFromFile.isize(DtoModelTransforms.iSizeToDTO(iSize)); saveFieldDataFromFile.annotation("test annotation"); saveFieldDataFromFile.name("TestFile");
        FieldDataSavedResults results = fieldDataResourceApi.save(saveFieldDataFromFile);

        // File is Saved on File System
        FieldDataShape fieldDataInfo = fieldDataResourceApi.getShapeFromID(results.getFieldDataKey());
        Assertions.assertEquals(saveFieldDataFromFile.getName(), results.getFieldDataName());
        Assertions.assertTrue(origin.compareEqual(DtoModelTransforms.dtoToOrigin(fieldDataInfo.getOrigin())));
        Assertions.assertTrue(extent.compareEqual(DtoModelTransforms.dtoToExtent(fieldDataInfo.getExtent())));
        Assertions.assertTrue(iSize.compareEqual(DtoModelTransforms.dtoToISize(fieldDataInfo.getIsize())));
        Assertions.assertEquals(times, fieldDataInfo.getTimes());

        // It's in the DB
        List<FieldDataReference> references = fieldDataResourceApi.getAllIDs();
        Assertions.assertEquals(saveFieldDataFromFile.getAnnotation(), references.get(0).getAnnotation());
        Assertions.assertEquals(results.getFieldDataKey(), references.get(0).getFieldDataID().getKey().getValue().toString());
        Assertions.assertEquals(0, references.get(0).getSimulationsReferencingThisID().size());

        ///////////////////////
        // Delete Field Data //
        //////////////////////
        fieldDataResourceApi.delete(results.getFieldDataKey());

        // No Longer on File System
        try{
            fieldDataResourceApi.getShapeFromID(results.getFieldDataKey());
        } catch (ApiException e){
            Assertions.assertEquals(404, e.getCode());
        }

        // No Longer in DB
        references = fieldDataResourceApi.getAllIDs();
        Assertions.assertEquals(0, references.size());
    }


    @Test
    public void testFileUploading() throws ApiException, IOException {
        FieldDataResourceApi fieldDataResourceApi = new FieldDataResourceApi(aliceAPIClient);
        File testFile = TestEndpointUtils.getResourceFile("/flybrain-035.tif");
        try{
            fieldDataResourceApi.analyzeFile(testFile, "invalid N@me #Ezequiel");
        } catch (ApiException e){
            Assertions.assertEquals(HttpStatus.BAD_REQUEST_400, e.getCode());
        }

        FieldData results = fieldDataResourceApi.analyzeFile(testFile, "bob");

        Assertions.assertNotNull(results);
        Assertions.assertNotNull(results.getShortSpecData());

        FieldDataSavedResults saveResults = fieldDataResourceApi.save(results);
        Assertions.assertNotNull(saveResults);

        FieldDataShape fieldDataInfo = fieldDataResourceApi.getShapeFromID(saveResults.getFieldDataKey());
        Assertions.assertNotNull(fieldDataInfo);
        Assertions.assertTrue(DtoModelTransforms.dtoToOrigin(fieldDataInfo.getOrigin()).compareEqual(new Origin(0.0, 0.0, 0.0)));
        Assertions.assertTrue(DtoModelTransforms.dtoToExtent(fieldDataInfo.getExtent()).compareEqual(new Extent(684.9333393978472, 684.9333393978472, 1)));
        Assertions.assertTrue(DtoModelTransforms.dtoToISize(fieldDataInfo.getIsize()).compareEqual(new ISize(256, 256, 1)));
        Assertions.assertEquals(1, fieldDataInfo.getTimes().size());

        fieldDataResourceApi.delete(saveResults.getFieldDataKey());

        try{
            fieldDataResourceApi.getShapeFromID(saveResults.getFieldDataKey());
        } catch (ApiException e){
            Assertions.assertEquals(404, e.getCode());
        }

        // No Longer in DB
        List<FieldDataReference> references = fieldDataResourceApi.getAllIDs();
        Assertions.assertEquals(0, references.size());
    }

    // Assuming the two part endpoint is correct with the previous tests, use it as a baseline to test against the advanced endpoint
    @Test
    public void testAdvancedEndpoint() throws ApiException, IOException {
        FieldDataResourceApi api = new FieldDataResourceApi(aliceAPIClient);

        File testFile = TestEndpointUtils.getResourceFile("/flybrain-035.tif");
        FieldData analyzedFile = api.analyzeFile(testFile, "test_file1");
        FieldDataSavedResults res = api.save(analyzedFile);
        FieldDataShape ogShape = api.getShapeFromID(res.getFieldDataKey());


        FieldDataSavedResults savedResults =  api.advancedCreate(testFile, "test_file2", analyzedFile.getExtent(),
                analyzedFile.getIsize(), analyzedFile.getVarNames(), analyzedFile.getTimes(), "", analyzedFile.getOrigin()
        );

        FieldDataShape shape = api.getShapeFromID(savedResults.getFieldDataKey());

        Assertions.assertEquals(ogShape.getExtent(), shape.getExtent());
        Assertions.assertEquals(ogShape.getIsize(), shape.getIsize());
        Assertions.assertEquals(ogShape.getTimes(), shape.getTimes());
        Assertions.assertEquals(ogShape.getOrigin(), shape.getOrigin());

        api.delete(res.getFieldDataKey());
        api.delete(savedResults.getFieldDataKey());
    }

    @Test
    public void oldAlgorithmTest() throws Exception {
        BioformatsImageImplLegacy bioformatsImage = new BioformatsImageImplLegacy();
        BioformatsImageImpl bioformatsImageImplNew = new BioformatsImageImpl();

        File simpleTestFile = TestEndpointUtils.getResourceFile("/flybrain-035.tif"); // Single time, channel, and no Z
        File complicatedTestFile = TestEndpointUtils.getResourceFile("/mitosis.tif"); // Multiple time, channels, and Z
        File[] testFiles = {simpleTestFile, complicatedTestFile};

        for (File testFile: testFiles){
            // Merged Channels
            ImageDataset oldAlgorithmImageDataset = bioformatsImage.readImageDataset(testFile.getAbsolutePath(), null);
            ImageDataset newAlgorithmImageDataset = bioformatsImageImplNew.readImageDataset(testFile.getAbsolutePath(), null);

            Assertions.assertTrue(oldAlgorithmImageDataset.compareEqual(newAlgorithmImageDataset));

            ImageDataset[] oldImageDatasets = bioformatsImage.readImageDatasetChannels(testFile.getAbsolutePath(), false, 0, null);
            ImageDataset[] newImageDatasets = bioformatsImageImplNew.readImageDatasetChannelsAlgo(testFile.getAbsolutePath(), false, 0, null);

            // Unmerged channels
            Assertions.assertEquals(oldImageDatasets.length, newImageDatasets.length);
            for (int i = 0; i < oldImageDatasets.length; i++){
                Assertions.assertTrue(oldImageDatasets[i].compareEqual(newImageDatasets[i]));
            }
        }
    }

    /**
     * Ensure the shape/meta data derived from files are appropriate
     */
    @Test
    public void createDefault() throws IOException, ApiException {
        FieldDataResourceApi api = new FieldDataResourceApi(aliceAPIClient);
        File simpleTestFile = TestEndpointUtils.getResourceFile("/flybrain-035.tif"); // Single time, channel, and no Z
        File complicatedTestFile = TestEndpointUtils.getResourceFile("/small-mitosis.tif"); // Multiple time, channels, and Z

        File[] files = new File[]{simpleTestFile, complicatedTestFile};

        for (int i = 0; i < files.length; i++){
            File testFile = files[i];
            FieldDataSavedResults results = api.createFromFile(testFile, "testFile_" + i);
            FieldDataShape resultShape = api.getShapeFromID(results.getFieldDataKey());
            FieldData resultAnalyze = api.analyzeFile(testFile, "testFile2_" + i);

            Assertions.assertEquals(resultAnalyze.getIsize(), resultShape.getIsize());
            Assertions.assertEquals(resultAnalyze.getOrigin(), resultShape.getOrigin());
            Assertions.assertEquals(resultAnalyze.getTimes(), resultShape.getTimes());

            assert resultAnalyze.getExtent() != null;
            assert resultAnalyze.getExtent().getZ() != null;

            Assertions.assertEquals(resultAnalyze.getExtent().getX(), resultShape.getExtent().getX());
            Assertions.assertEquals(resultAnalyze.getExtent().getY(), resultShape.getExtent().getY());
            if (resultAnalyze.getExtent().getZ() != 9.0E-7){
                Assertions.assertEquals(resultAnalyze.getExtent().getZ(), resultShape.getExtent().getZ());
            }
        }
    }

}
