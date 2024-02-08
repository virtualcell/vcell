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
import org.janelia.saalfeldlab.n5.DataBlock;
import org.janelia.saalfeldlab.n5.*;
import org.junit.jupiter.api.*;
import org.vcell.util.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@Tag("Fast")
public class N5ExporterTest {

    private N5Reader n5Reader;
    private VCData controlModel;
    private DataSetControllerImpl controlModelController;
    private  VCSimulationDataIdentifier vcDataID;
    private ArrayList<DataIdentifier> variables;
    private N5Exporter n5Exporter;
    private static final String fourDModelID = "597714292";
    private static final String fiveDModelID = "1136922340";
    private final ArrayList<String> testModels = new ArrayList<>(Arrays.asList(
            fourDModelID, //FRAP Tutorial in VCell, has no Z axis
            fiveDModelID //PH-GFP Tutorial in VCell
    ));

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

        for(String model: testModels){
            for(String extension: fileExtensions){
                String currentFileNameString = String.format(simFileNameTemplate, model, extension);
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

    public void makeN5Model(N5Specs.CompressionLevel compressionLevel, int startTimeIndex, int endTimeIndex, String modelID) throws MathException, IOException, DataAccessException {
        OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);

        VariableSpecs variableSpecs = new VariableSpecs(variables.stream().map(di -> di.getName()).toList(), Integer.parseInt(modelID));
        GeometrySpecs geometrySpecs = new GeometrySpecs(new SpatialSelection[0], 0, 0, 0);
        N5Specs n5Specs = new N5Specs(ExportConstants.DataType.PDE_VARIABLE_DATA, ExportFormat.N5, compressionLevel, modelID);

        double[] allTimes = n5Exporter.getDataSetController().getDataSetTimes(n5Exporter.getVcDataID());
        TimeSpecs timeSpecs = new TimeSpecs(startTimeIndex, endTimeIndex, allTimes, variableSpecs.getModeID());
        ExportSpecs exportSpecs = new ExportSpecs(n5Exporter.getVcDataID(), ExportFormat.N5, variableSpecs, timeSpecs, geometrySpecs, n5Specs, "", "");
        FileDataContainerManager fileDataContainerManager = new FileDataContainerManager();

        ExportOutput exportOutput = n5Exporter.makeN5Data(outputContext, 0, exportSpecs, fileDataContainerManager);
//        File file = new File(PropertyLoader.getRequiredProperty(PropertyLoader.n5DataDir) + "/" + n5Exporter.getN5FileNameHash() + ".N5");
//        FileOutputStream fileOut = new FileOutputStream(file);
//        BufferedOutputStream out= new BufferedOutputStream(fileOut);
//        exportOutput.writeDataToOutputStream(out, fileDataContainerManager);
//        out.close();

        if(n5Reader != null){
            n5Reader.close();
        }
        this.n5Reader = new N5FSReader(n5Exporter.getN5FileAbsolutePath());
    }

    public void initalizeModel(String simKeyID) throws IOException, DataAccessException, MathException {
        ExportServiceImpl exportService = new ExportServiceImpl();
        long jobId = 0;

        n5Exporter = new N5Exporter(exportService);

        if (simKeyID.equals(fourDModelID)){
            // the test model can only support one species at this time
            n5Exporter.initalizeDataControllers(fourDModelID, "ezequiel23", "258925427", jobId);
            dataIdentifiers = new ArrayList<>(Arrays.asList(n5Exporter.dataSetController.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), n5Exporter.getVcDataID())));
            this.variables = new ArrayList<>(Arrays.asList(
                    getRandomDISpecificVariable(VariableType.VOLUME),
                    getRandomDISpecificVariable(VariableType.VOLUME_REGION),
                    getRandomDISpecificVariable(VariableType.MEMBRANE_REGION),
                    getRandomDI()
            ));
        }
        else if (simKeyID.equals(fiveDModelID)){
            n5Exporter.initalizeDataControllers(fiveDModelID, "ezequiel23", "258925427", jobId);
            dataIdentifiers = new ArrayList<>(Arrays.asList(n5Exporter.dataSetController.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), n5Exporter.getVcDataID())));
            this.variables = new ArrayList<>(Arrays.asList(
                    getRandomDISpecificVariable(VariableType.VOLUME),
                    getRandomDISpecificVariable(VariableType.POSTPROCESSING),
                    getRandomDISpecificVariable(VariableType.VOLUME_REGION),
                    getRandomDISpecificVariable(VariableType.MEMBRANE_REGION),
                    getRandomDI()
            ));
        }
        this.controlModel = n5Exporter.getVCData();
        this.vcDataID = n5Exporter.getVcDataID();
        this.controlModelController = n5Exporter.getDataSetController();


    }


    @Test
    public void testMetaData() throws MathException, DataAccessException, IOException {

        for(String model: testModels){
            this.initalizeModel(model);
            this.makeN5Model(N5Specs.CompressionLevel.RAW, 0, controlModel.getDataTimes().length - 1, model);
            //X, Y, T, Z, Channels
            long[] controlDimensions = {controlModel.getMesh().getSizeX(), controlModel.getMesh().getSizeY(), variables.size(), controlModel.getMesh().getSizeZ(), controlModel.getDataTimes().length};
            // tests the metadata, and the metadata may be accurate but the actual raw array of data may be wrong
            DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(model);
            long[] exportDimensions = datasetAttributes.getDimensions();
            assertArrayEquals(controlDimensions, exportDimensions, "Testing dimension results for model " + model);

            assertSame(DataType.FLOAT64, datasetAttributes.getDataType(),"Data Type of model " + model);

            int[] expectedBlockSize = {controlModel.getMesh().getSizeX(), controlModel.getMesh().getSizeY(), 1, controlModel.getMesh().getSizeZ(), 1};
            assertArrayEquals(expectedBlockSize, datasetAttributes.getBlockSize(),"Block Size of model " + model);
        }
    }

    @Test
    public void testRandomTimeSlices() throws MathException, IOException, DataAccessException {
        for (String model: testModels){
            initalizeModel(model);
            for (int k=0; k<8; k++){ //try 8 randomly chosen time slice combinations
                double[] times = controlModel.getDataTimes();
                Random random = new Random();
                int startTimeIndex = random.nextInt(0, times.length);
                int endTimeIndex = random.nextInt(startTimeIndex, times.length);
                OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);

                makeN5Model(N5Specs.CompressionLevel.RAW, startTimeIndex, endTimeIndex, model);
                DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(model);
                long attributesTimeSize = startTimeIndex + (datasetAttributes.getDimensions()[4] - 1); //minus 1 since we are already starting at startTimeIndex

                for (int i = 0; i < variables.size(); i++){
                    for(int timeSlice = startTimeIndex; timeSlice <= attributesTimeSize; timeSlice++){
                        DataBlock<?> dataBlock = n5Reader.readBlock(model, datasetAttributes, new long[]{0, 0, i, 0, timeSlice});

                        double[] exportedRawData = (double[]) dataBlock.getData();
                        assertArrayEquals(
                                controlModelController.getSimDataBlock(outputContext, this.vcDataID, variables.get(i).getName(), times[timeSlice]).getData(),
                                exportedRawData,
                                0,
                                "Equal raw data of model " + model + " with species " + variables.get(i).getName() +
                                        " with type " + variables.get(i).getVariableType() + " at time " + timeSlice);
                    }
                }

            }
        }
    }

    @Test
    public void testRawDataEquivelance() throws IOException, DataAccessException, MathException {
        // Is the histogram over entire slice of an image result in the same for both parties
        // is it the same for some random region of space in the slice for the intensities should be the same

        // Try to get the block data all at once, load dataset into memory

        //each block is entire XYZ, broken in time and channels

        for(String model: testModels){
            this.initalizeModel(model);
            OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
            double[] times = controlModel.getDataTimes();
            int endTimeIndex = times.length - 1;
            makeN5Model(N5Specs.CompressionLevel.RAW, 0, endTimeIndex, model);

            for(int i = 0; i < variables.size(); i++){
                for(int timeSlice = 0; timeSlice < times.length; timeSlice++){
                    DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(model);
                    DataBlock<?> dataBlock = n5Reader.readBlock(model, datasetAttributes, new long[]{0, 0, i, 0, timeSlice});

                    double[] exportedRawData = (double[]) dataBlock.getData();
                    assertArrayEquals(
                            controlModelController.getSimDataBlock(outputContext, this.vcDataID, variables.get(i).getName(), times[timeSlice]).getData(),
                            exportedRawData,
                            0,
                            "Equal raw data of model " + model + " with species " + variables.get(i).getName() +
                                    " with type " + variables.get(i).getVariableType() + " at time " + timeSlice);
                }
            }
        }
    }

    // Test only one time slice for each compression type for data equivalence, the time slice is chosen randomly
    // and random variable, this way it doesn't take forever to test
    @Test
    public void testDataCompressionEquivelance() throws MathException, IOException, DataAccessException {
        ArrayList<N5Specs.CompressionLevel> compressions = new ArrayList<>(Arrays.asList(
                N5Specs.CompressionLevel.BZIP,
                N5Specs.CompressionLevel.GZIP
        ));
        Random random = new Random(5);

        for (N5Specs.CompressionLevel compression: compressions){
                for(String model: testModels){
                    initalizeModel(model);
                    double[] times = controlModel.getDataTimes();
                    int endTimeIndex = times.length - 1;
                    makeN5Model(compression, 0 , endTimeIndex, model);
                    OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
                    DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(model);
                    for(int j = 0; j< 8; j++){
                        int timeSlice = random.nextInt(endTimeIndex);
                        int chosenVariable = random.nextInt(variables.size());
                        DataBlock<?> dataBlock = n5Reader.readBlock(model, datasetAttributes, new long[]{0, 0, chosenVariable, 0, timeSlice});

                        double[] exportedData = (double[]) dataBlock.getData();
                        Assertions.assertArrayEquals(
                                controlModelController.getSimDataBlock(outputContext, this.vcDataID, variables.get(chosenVariable).getName(), times[timeSlice]).getData(),
                                exportedData,
                                0,
                                "Equal data with " + compression + " compression");
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
