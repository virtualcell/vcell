package cbit.vcell.export;



/*
    In order to determine whether the exportation keeps the information intact we have to have two identical
    models. Then, when both models are loaded into ImageJ's image controller class, do some analysis on the models and the
    results should be the exact same.

    Can do analysis manually, such as taking VCell data and doing a histogram that way along with other metrics for variable concentration.

    Can manually create image that has very basic model, single blob in the center with some complex metadata, and make that
    be the control.
 */

import cbit.vcell.export.server.*;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solvers.CartesianMesh;
import com.google.gson.internal.LinkedTreeMap;
import org.janelia.saalfeldlab.n5.DataBlock;
import org.janelia.saalfeldlab.n5.*;
import org.junit.jupiter.api.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@Tag("Fast")
public class N5ExporterTest {

    enum TestModels{
        fourDModel("597714292"),
        fiveDModel("868220316"), // Ezequiel's Blank 3D Model
        fiveDModelPostProcess("868220316");

        private final String simID;
        TestModels(String simID){
            this.simID = simID;
        }
    }

    private N5Reader n5Reader;
    private DataServerImpl dataServer;
    private  VCSimulationDataIdentifier vcDataID;
    private ArrayList<DataIdentifier> variables;
    private N5Exporter n5Exporter;
    private User testUser;
    private CartesianMesh modelMesh;
    private double[] times;
    private ExportServiceImpl exportService = new ExportServiceImpl();


    private ArrayList<DataIdentifier> dataIdentifiers;

    private static String previousInstallRoot;
    private static String previousPrimarySimDir;
    private static String previousSecondarySimDir;
    private static String previousSecondaryInternalSimDir;
    private static String previousSimCacheSize;
    private static String previousN5Path;

    public final File temporaryFolder = new File(System.getProperty("java.io.tmpdir"));

    private static final String simFileNameTemplate = "SimID_%s_0_%s";

    // Had to add specific .zip and .log files for test to run
    private static final ArrayList<String> fileExtensions = new ArrayList<>(Arrays.asList(
            ".functions",
            ".fvinput",
            ".hdf5",
            ".log",
            ".mesh",
            ".meshmetrics",
            ".subdomains",
            ".vcg",
            "00.zip",
            "_0.simtask.xml"
    ));

    @BeforeEach
    public void setUp() throws IOException {
        // setup the resources folder as a temp dir


        File tmpSimDataDir = new File(temporaryFolder.getAbsolutePath() + "/ezequiel23");
        tmpSimDataDir.mkdir();
        File n5ExportDir = new File(temporaryFolder.getAbsolutePath() + "/N5DataExporter");
        n5ExportDir.mkdir();

        for(TestModels model: TestModels.values()){
            for(String extension: fileExtensions){
                String currentFileNameString = String.format(simFileNameTemplate, model.simID, extension);
                InputStream inputStream = Objects.requireNonNull(N5ExporterTest.class.getResourceAsStream("/simdata/n5/ezequiel23/" + currentFileNameString));
                File currentFile = new File(tmpSimDataDir.getAbsolutePath() + "/" + currentFileNameString);
                org.apache.commons.io.FileUtils.copyInputStreamToFile(inputStream, currentFile);
            }
        }

        previousInstallRoot = PropertyLoader.getProperty(PropertyLoader.installationRoot, null);
        PropertyLoader.setProperty(PropertyLoader.installationRoot, new File("..").getAbsolutePath());

        previousPrimarySimDir = PropertyLoader.getProperty(PropertyLoader.primarySimDataDirInternalProperty, null);
        PropertyLoader.setProperty(PropertyLoader.primarySimDataDirInternalProperty, temporaryFolder.getAbsolutePath());

        previousSecondarySimDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirExternalProperty, null);
        System.setProperty(PropertyLoader.secondarySimDataDirExternalProperty, temporaryFolder.getAbsolutePath());

        previousSecondarySimDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirExternalProperty, null);
        System.setProperty(PropertyLoader.secondarySimDataDirExternalProperty, temporaryFolder.getAbsolutePath());

        previousN5Path = PropertyLoader.getProperty(PropertyLoader.n5DataDir, null);
        PropertyLoader.setProperty(PropertyLoader.n5DataDir, n5ExportDir.getAbsolutePath());

        previousSecondarySimDir = PropertyLoader.getProperty(PropertyLoader.secondarySimDataDirInternalProperty, null);
        PropertyLoader.setProperty(PropertyLoader.secondarySimDataDirInternalProperty, "k");

        previousSimCacheSize = PropertyLoader.getProperty(PropertyLoader.simdataCacheSizeProperty, null);
        PropertyLoader.setProperty(PropertyLoader.simdataCacheSizeProperty, "100000");

        Cachetable cachetable = new Cachetable(10 * Cachetable.minute, Long.parseLong(PropertyLoader.getRequiredProperty(PropertyLoader.simdataCacheSizeProperty)));
        File primaryDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty));
        File secodaryDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirInternalProperty));
        DataSetControllerImpl dataSetController = new DataSetControllerImpl(cachetable, primaryDir, secodaryDir);
        DataServerImpl dataServer = new DataServerImpl(dataSetController, exportService);

        testUser = new User("ezequiel23", new KeyValue("258925427"));
        this.dataServer = dataServer;
    }

    @AfterEach
    public void restore() throws IOException {
        // tear down the temp dir

        if (previousInstallRoot != null) {
            PropertyLoader.setProperty(PropertyLoader.installationRoot, previousInstallRoot);
        }
        if (previousPrimarySimDir != null){
            PropertyLoader.setProperty(PropertyLoader.primarySimDataDirInternalProperty, previousPrimarySimDir);
        }

        if (previousN5Path != null){
            PropertyLoader.setProperty(PropertyLoader.n5DataDir, previousN5Path);
        }

        if (previousSecondarySimDir != null){
            System.setProperty(PropertyLoader.secondarySimDataDirExternalProperty, previousSecondarySimDir);
        }

        if (previousSecondaryInternalSimDir != null){
            System.setProperty(PropertyLoader.secondarySimDataDirInternalProperty, previousSecondaryInternalSimDir);
        }

        if (previousSimCacheSize != null){
            System.setProperty(PropertyLoader.simdataCacheSizeProperty, previousSimCacheSize);
        }

        if (n5Reader != null){
            n5Reader.close();
        }

    }

    public void makeN5FileWithSpecificSimulationResults(N5Specs.CompressionLevel compressionLevel, int startTimeIndex, int endTimeIndex, String modelID) throws Exception {
        OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);


        VariableSpecs variableSpecs = new VariableSpecs(variables.stream().map(di -> di.getName()).toList(), Integer.parseInt(modelID));
        GeometrySpecs geometrySpecs = new GeometrySpecs(new SpatialSelection[0], 0, 0, 0);
        N5Specs n5Specs = new N5Specs(ExportConstants.DataType.PDE_VARIABLE_DATA, ExportFormat.N5, compressionLevel, modelID);

        double[] allTimes = dataServer.getDataSetTimes(testUser,n5Exporter.getVcDataID());
        TimeSpecs timeSpecs = new TimeSpecs(startTimeIndex, endTimeIndex, allTimes, variableSpecs.getModeID());
        ExportSpecs exportSpecs = new ExportSpecs(n5Exporter.getVcDataID(), ExportFormat.N5, variableSpecs, timeSpecs, geometrySpecs, n5Specs, "", "");
        HashMap<Integer, String> dummyMaskInfo = new HashMap<>(){{put(0, "Dummy"); put(1, "Test");}};
        exportSpecs.setExportMetaData(new HumanReadableExportData("", "", "", new ArrayList<>(), "", "", false, dummyMaskInfo));
        FileDataContainerManager fileDataContainerManager = new FileDataContainerManager();

        ExportOutput exportOutput = n5Exporter.makeN5Data(outputContext, Integer.parseInt(modelID), exportSpecs, fileDataContainerManager);

        if(n5Reader != null){
            n5Reader.close();
        }
        this.n5Reader = new N5FSReader(n5Exporter.getN5FileSystemPath());
    }

    private void setExportTestState(TestModels simModel) throws IOException, DataAccessException, MathException {
        VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(new KeyValue(simModel.simID), testUser);

        vcDataID = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
        n5Exporter = new N5Exporter(exportService, testUser, dataServer, vcDataID);
        dataIdentifiers = new ArrayList<>(Arrays.asList(dataServer.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), testUser, vcDataID)));

        modelMesh = dataServer.getMesh(testUser, vcDataID);
        times = dataServer.getDataSetTimes(testUser, vcDataID);
        if (simModel == TestModels.fiveDModelPostProcess) {
            this.variables = new ArrayList<>(Arrays.asList(
                getRandomDISpecificVariable(VariableType.POSTPROCESSING)
            ));
        } else {
            this.variables = new ArrayList<>(Arrays.asList(
                getRandomDISpecificVariable(VariableType.VOLUME)
            ));
        }
    }


    @Test
    public void testMetaData() throws Exception {

        for(TestModels model: TestModels.values()){
            this.setExportTestState(model);
            this.makeN5FileWithSpecificSimulationResults(N5Specs.CompressionLevel.RAW, 0, times.length - 1, model.simID);

            int sizeX = modelMesh.getSizeX(),
                    sizeY = modelMesh.getSizeY(),
                    sizeZ = modelMesh.getSizeZ();

            if(!model.equals(TestModels.fiveDModelPostProcess)){
                sizeX = MeshToImage.newNumElements(sizeX);
                sizeY = MeshToImage.newNumElements(sizeY);
                sizeZ = MeshToImage.newNumElements(sizeZ);
            }


            //X, Y, T, Z, Channels
            long[] controlDimensions = {sizeX, sizeY, variables.size() + 1, sizeZ, times.length};
            // tests the metadata, and the metadata may be accurate but the actual raw array of data may be wrong
            DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(model.simID);
            long[] exportDimensions = datasetAttributes.getDimensions();
            assertArrayEquals(controlDimensions, exportDimensions, "Testing dimension results for model " + model);
            ((N5FSReader) n5Reader).getAttributes(model.simID);

            LinkedTreeMap<String, String> dummyMaskInfo = (LinkedTreeMap<String, java.lang.String>)((N5FSReader) n5Reader).getAttribute(model.simID, N5Specs.maskingMetaDataName, LinkedTreeMap.class);

            assertSame(DataType.FLOAT64, datasetAttributes.getDataType(),"Data Type of model " + model);
            assert("Dummy".equals(dummyMaskInfo.get("0")));
            assert("Test".equals(dummyMaskInfo.get("1")));

            int[] expectedBlockSize = {sizeX, sizeY, 1, 1, 1};
            assertArrayEquals(expectedBlockSize, datasetAttributes.getBlockSize(),"Block Size of model " + model);
        }
    }

    @Test
    public void testRawDataEquivelance() throws Exception {
        // Is the histogram over entire slice of an image result in the same for both parties
        // is it the same for some random region of space in the slice for the intensities should be the same

        // Try to get the block data all at once, load dataset into memory

        //each block is entire XYZ, broken in time and channels

        for(TestModels model: TestModels.values()){
            compareData(N5Specs.CompressionLevel.RAW, model, model.equals(TestModels.fiveDModelPostProcess));
        }
    }

    // Test only one time slice for each compression type for data equivalence, the time slice is chosen randomly
    // and random variable, this way it doesn't take forever to test
    @Test
    public void testDataCompressionEquivelance() throws Exception {
        ArrayList<N5Specs.CompressionLevel> compressions = new ArrayList<>(Arrays.asList(
                N5Specs.CompressionLevel.BZIP,
                N5Specs.CompressionLevel.GZIP
        ));

        for (N5Specs.CompressionLevel compression: compressions){
                for(TestModels model: TestModels.values()){
                    compareData(compression, model, model.equals(TestModels.fiveDModelPostProcess));
                }
        }

    }

    private void compareData(N5Specs.CompressionLevel compressionLevel, TestModels model, boolean postProcessVariable) throws Exception {
        this.setExportTestState(model);
        OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
        int endTimeIndex = times.length - 1;
        makeN5FileWithSpecificSimulationResults(compressionLevel, 0, endTimeIndex, model.simID);

        int sizeZ = postProcessVariable ? modelMesh.getSizeZ() : MeshToImage.newNumElements(modelMesh.getSizeZ());
        int sizeX = postProcessVariable ? modelMesh.getSizeX() : MeshToImage.newNumElements(modelMesh.getSizeX());
        int sizeY = postProcessVariable ? modelMesh.getSizeY() : MeshToImage.newNumElements(modelMesh.getSizeY());

        for(int i = 0; i < variables.size(); i++){
            for(int timeSlice = 0; timeSlice < times.length; timeSlice++){
                double[] constData = dataServer.getSimDataBlock(outputContext, testUser, this.vcDataID, variables.get(i).getName(), times[timeSlice]).getData();
                double[] meshToImage = MeshToImage.convertMeshIntoImage(constData, modelMesh).data();
                for (int z = 0; z < sizeZ; z++){
                    DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(model.simID);
                    DataBlock<?> dataBlock = n5Reader.readBlock(model.simID, datasetAttributes, new long[]{0, 0, i, z, timeSlice});

                    double[] testConst;
                    double[] exportedRawData = (double[]) dataBlock.getData();
                    if(!postProcessVariable){
                        double[] testMeshToImage = MeshToImage.getXYFromXYZArray(meshToImage, sizeX, sizeY, z);
                        double[] imageToMesh = MeshToImage.convertImageIntoMesh(exportedRawData, sizeX, sizeY, sizeZ, false).data();
                        testConst = MeshToImage.getXYFromXYZArray(constData, modelMesh, (int) Math.ceil(z / 2.0));

                        assertArrayEquals(testMeshToImage, exportedRawData, 0,
                                "Data of model " + model + " with species " + variables.get(i).getName() +
                                        " with type " + variables.get(i).getVariableType() + " at time " + timeSlice +
                                        ". The compression is " + compressionLevel + " and conversion of mesh to image.");

                        assertArrayEquals(testConst, imageToMesh, 0,
                                "Data of model " + model + " with species " + variables.get(i).getName() +
                                        " with type " + variables.get(i).getVariableType() + " at time " + timeSlice +
                                        ". The compression is " + compressionLevel + " and conversion of image to mesh.");

                    } else{
                        testConst = MeshToImage.getXYFromXYZArray(constData, modelMesh, z);
                        assertArrayEquals(testConst, exportedRawData, 0,
                                "Data of model " + model + " with species " + variables.get(i).getName() +
                                        " with type " + variables.get(i).getVariableType() + " at time " + timeSlice);
                    }
                }
            }
        }
    }

    public DataIdentifier getRandomDI() throws IOException, DataAccessException {
        Random random = new Random();
        DataIdentifier df = dataIdentifiers.remove(random.nextInt(dataIdentifiers.size()));
        while (N5Exporter.unsupportedTypes.contains(df.getVariableType())){
            df = dataIdentifiers.remove(random.nextInt(dataIdentifiers.size()));
        }
        return df;
    }

    public DataIdentifier getRandomDISpecificVariable(VariableType variableType){
        for(DataIdentifier dataIdentifier: dataIdentifiers){
            if(dataIdentifier.getVariableType().equals(variableType)){
                dataIdentifiers.remove(dataIdentifier);
                return dataIdentifier;
            }
        }
        return null;
    }

    // Test annotated functions, and multiple different parameters for data conversion, for multiple scans use different template file name


}
