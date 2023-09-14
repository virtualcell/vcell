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
import cbit.vcell.simdata.*;
import cbit.vcell.simdata.n5.N5Exporter;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import org.janelia.saalfeldlab.n5.*;
import org.junit.Test;
import org.vcell.util.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;

public class N5ExporterTest {

    private N5Reader n5Reader;
    private VCData controlModel;
    private String dataSetName;
    private DataSetControllerImpl controlModelController;
    private  VCSimulationDataIdentifier vcDataID;
    private ArrayList<DataIdentifier> variables;
    private final ArrayList<String> testModels = new ArrayList<>(Arrays.asList(
            "4DModel", //FRAP Tutorial in VCell, has no Z axis
            "5DModel" //PH-GFP Tutorial in VCell
    ));

    public void initalizeModel(String simKeyID) throws IOException, DataAccessException, MathException {
        File n5File = new File("src/test/resources/simdata/n5/N5ExportData");
//        File n5File = new File("/home/zeke/Downloads/fullTest.n5");
        File vSimModel = new File("src/test/resources/simdata/n5");
        String n5FilePath = n5File.getAbsolutePath();
        N5Exporter n5Exporter = new N5Exporter();

        if (simKeyID.equals("4DModel")){
            // the test model can only support one species at this time
            n5Exporter.initalizeDataControllers(vSimModel.getAbsolutePath(), "123971881", "/media/zeke/DiskDrive/App_Installations/VCell_Rel", "ezequiel23", "258925427");
            this.variables = new ArrayList<>(Arrays.asList(
                    n5Exporter.getRandomDI()
            ));
        }
        else if (simKeyID.equals("5DModel")){
            n5Exporter.initalizeDataControllers(vSimModel.getAbsolutePath(), "1115478432", "/media/zeke/DiskDrive/App_Installations/VCell_Rel", "ezequiel23", "258925427");
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

        n5Exporter.exportToN5(n5FilePath, variables);
        this.n5Reader = new N5FSReader(n5FilePath);
        this.dataSetName = this.controlModel.getResultsInfoObject().getDataKey().toString() + this.controlModel.getResultsInfoObject().getID();
    }


    @Test
    public void testMetaData() throws MathException, DataAccessException, IOException {

        for(String model: testModels){
            this.initalizeModel(model);
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
            this.initalizeModel(model);
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


}
