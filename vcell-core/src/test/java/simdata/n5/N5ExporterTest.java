package simdata.n5;



/*
    In order to determine whether the exportation keeps the information intact we have to have two identical
    models. Then, when both models are loaded into ImageJ's image controller class, do some analysis on the models and the
    results should be the exact same.

    Can do analysis manually, such as taking VCell data and doing a histogram that way along with other metrics for variable concentration.

    Can manually create image that has very basic model, single blob in the center with some complex metadata, and make that
    be the control.
 */

import cbit.vcell.math.MathException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.VCData;
import cbit.vcell.simdata.n5.N5Exporter;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import org.janelia.saalfeldlab.n5.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.vcell.test.Fast;
import org.vcell.util.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

@Category(Fast.class)
public class N5ExporterTest {

    private N5Reader n5Reader;
    private VCData controlModel;
    private String dataSetName;
    private DataSetControllerImpl controlModelController;
    private  VCSimulationDataIdentifier vcDataID;
    private ArrayList<DataIdentifier> variables;
    private static final String fourDModelID = "597714292";
    private static final String fiveDModelID = "1107466895";
    private final ArrayList<String> testModels = new ArrayList<>(Arrays.asList(
            fourDModelID, //FRAP Tutorial in VCell, has no Z axis
            fiveDModelID //PH-GFP Tutorial in VCell
    ));

    private static String previousInstallRoot;
    private static String previousPrimarySimDir;

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

    @Before
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

        previousN5Path = PropertyLoader.getProperty(PropertyLoader.n5DataDir, null);
        PropertyLoader.setProperty(PropertyLoader.n5DataDir, n5ExportDir.getAbsolutePath());
    }

    @After
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


        if (n5Reader != null){
            n5Reader.close();
        }

    }

    public void initalizeModel(String simKeyID, Compression compression) throws IOException, DataAccessException, MathException {
        N5Exporter n5Exporter = new N5Exporter();

        if (simKeyID.equals(fourDModelID)){
            // the test model can only support one species at this time
            n5Exporter.initalizeDataControllers(fourDModelID, "ezequiel23", "258925427");
            this.variables = new ArrayList<>(Arrays.asList(
                    n5Exporter.getRandomDI()
            ));
        }
        else if (simKeyID.equals(fiveDModelID)){
            n5Exporter.initalizeDataControllers(fiveDModelID, "ezequiel23", "258925427");
            this.variables = new ArrayList<>(Arrays.asList(
                    n5Exporter.getRandomDI(),
                    n5Exporter.getRandomDI(),
                    n5Exporter.getRandomDI(),
                    n5Exporter.getRandomDI()
            ));
        }
        this.controlModel = n5Exporter.getVCData();
        this.vcDataID = n5Exporter.getVcDataID();
        this.controlModelController = n5Exporter.getDataSetController();

        n5Exporter.exportToN5(variables, compression);
        if(n5Reader != null){
            n5Reader.close();
        }
        this.n5Reader = new N5FSReader(n5Exporter.getN5FileAbsolutePath());
        this.dataSetName = n5Exporter.getN5DatasetName();
    }


    @Test
    public void testMetaData() throws MathException, DataAccessException, IOException {

        for(String model: testModels){
            this.initalizeModel(model, new RawCompression());
            //X, Y, T, Z, Channels
            long[] controlDimensions = {controlModel.getMesh().getSizeX(), controlModel.getMesh().getSizeY(), variables.size(), controlModel.getMesh().getSizeZ(), controlModel.getDataTimes().length};
            // tests the metadata, and the metadata may be accurate but the actual raw array of data may be wrong
            DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(dataSetName);
            long[] exportDimensions = datasetAttributes.getDimensions();
            Assert.assertArrayEquals("Testing dimension results for model " + model, controlDimensions, exportDimensions);

            Assert.assertSame("Data Type of model " + model, DataType.FLOAT64, datasetAttributes.getDataType());

            int[] expectedBlockSize = {controlModel.getMesh().getSizeX(), controlModel.getMesh().getSizeY(), 1, controlModel.getMesh().getSizeZ(), 1};
            Assert.assertArrayEquals("Block Size of model " + model, expectedBlockSize, datasetAttributes.getBlockSize());
        }
    }

    @Test
    public void testRawDataEquivelance() throws IOException, DataAccessException, MathException {
        // Is the histogram over entire slice of an image result in the same for both parties
        // is it the same for some random region of space in the slice for the intensities should be the same

        // Try to get the block data all at once, load dataset into memory

        //each block is entire XYZ, broken in time and channels

        for(String model: testModels){
            this.initalizeModel(model, new RawCompression());
            OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
            double[] times = controlModel.getDataTimes();

            for(int i = 0; i < variables.size(); i++){
                for(int timeSlice = 0; timeSlice < times.length; timeSlice++){
                    DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(dataSetName);
                    DataBlock<?> dataBlock = n5Reader.readBlock(dataSetName, datasetAttributes, new long[]{0, 0, i, 0, timeSlice});

                    double[] exportedRawData = (double[]) dataBlock.getData();
                    Assert.assertArrayEquals("Equal raw data of model " + model + " with species " + variables.get(i).getName() + " with type " + variables.get(i).getVariableType() + " at time " + timeSlice,
                            controlModelController.getSimDataBlock(outputContext, this.vcDataID, variables.get(i).getName(), times[timeSlice]).getData(),
                            exportedRawData,
                            0);
                }
            }
        }
    }

    // Test only one time slice for each compression type for data equivalence, the time slice is chosen randomly
    // and random variable, this way it doesn't take forever to test
    @Test
    public void testDataCompressionEquivelance() throws MathException, IOException, DataAccessException {
        ArrayList<Compression> compressions = new ArrayList<>(Arrays.asList(
                new Bzip2Compression(),
                new GzipCompression()
        ));
        Random random = new Random(5);

        for (Compression compression: compressions){
                for(String model: testModels){
                    this.initalizeModel(model, compression);
                    OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
                    double[] times = controlModel.getDataTimes();
                    DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(dataSetName);
                    for(int j = 0; j< 8; j++){
                        int timeSlice = random.nextInt(times.length);
                        int chosenVariable = random.nextInt(variables.size());
                        DataBlock<?> dataBlock = n5Reader.readBlock(dataSetName, datasetAttributes, new long[]{0, 0, chosenVariable, 0, timeSlice});

                        double[] exportedData = (double[]) dataBlock.getData();
                        Assert.assertArrayEquals("Equal data with " + compression.getType() + " compression",
                                controlModelController.getSimDataBlock(outputContext, this.vcDataID, variables.get(chosenVariable).getName(), times[timeSlice]).getData(),
                                exportedData,
                                0);
                    }
                }
        }

    }


}
