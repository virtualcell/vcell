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
import cbit.vcell.simdata.*;
import cbit.vcell.simdata.n5.N5Exporter;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import ncsa.hdf.object.Dataset;
import org.janelia.saalfeldlab.n5.*;
import org.junit.Before;
import org.junit.Test;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;

public class N5ExporterTest {

    private N5Reader n5Reader;
    private VCData controlModel;
    private String dataSetName;

    private ArrayList<String> species;

    @Before
    public void initalizeModels() throws IOException, DataAccessException, MathException {
        File n5File = new File("src/test/resources/simdata/n5/N5ExportData");
        File vSimModel = new File("src/test/resources/simdata/n5");
        String n5FilePath = n5File.getAbsolutePath();

        this.controlModel = N5Exporter.getVCData(vSimModel.getAbsolutePath(), "1115478432");
        this.species = new ArrayList<>(Arrays.asList("IP3_Cyt"));

        N5Exporter.exportToN5(this.controlModel, n5FilePath, species);
        this.n5Reader = new N5FSReader(n5FilePath);

        this.dataSetName = this.controlModel.getResultsInfoObject().getDataKey().toString() + this.controlModel.getResultsInfoObject().getID();
    }


    @Test
    public void testMetaData() throws MathException, DataAccessException, IOException {
        //X, Y, T, Z, Channels
        long[] controlDimensions = {controlModel.getMesh().getSizeX(), controlModel.getMesh().getSizeY(), controlModel.getDataTimes().length, controlModel.getMesh().getSizeZ(), species.size()};
        // tests the metadata, and the metadata may be accurate but the actual raw array of data may be wrong
        DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(dataSetName);
        long[] exportDimensions = datasetAttributes.getDimensions();
        Assert.assertArrayEquals("Testing dimension results", controlDimensions, exportDimensions);

        Assert.assertSame("Data Type", DataType.FLOAT64, datasetAttributes.getDataType());

        int[] expectedBlockSize = {controlModel.getMesh().getSizeX(), controlModel.getMesh().getSizeY(), 1, controlModel.getMesh().getSizeZ(), 1};
        Assert.assertArrayEquals("Block Size", expectedBlockSize, datasetAttributes.getBlockSize());
    }

    @Test
    public void testHistogram() throws IOException, DataAccessException {
        // Is the histogram over entire slice of an image result in the same for both parties
        // is it the same for some random region of space in the slice for the intensities should be the same

        // Try to get the block data all at once, load dataset into memory

        //each block is entire XYZ, broken in time and channels

        OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);

        Random rand = new Random();
        double[] times = controlModel.getDataTimes();
        int max = times.length;
        int min = 0;
        int timeSlice = rand.nextInt((max - min + 1) + min);


        DatasetAttributes datasetAttributes = n5Reader.getDatasetAttributes(dataSetName);
        DataBlock<?> dataBlock = n5Reader.readBlock(dataSetName, datasetAttributes, new long[]{0, 0, 0, 0, timeSlice});

        double[] exportedRawData = (double[]) dataBlock.getData();
        Assert.assertArrayEquals("Equal raw data", controlModel.getSimDataBlock(outputContext, species.get(0), times[timeSlice]).getData(), exportedRawData, 0);
    }


}
