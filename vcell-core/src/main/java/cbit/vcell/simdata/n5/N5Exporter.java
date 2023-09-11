package cbit.vcell.simdata.n5;

import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.janelia.saalfeldlab.n5.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/*
    Variables can be considered channels, and each variable may not have a spatial representation, but for the ones that do
    they should be considered separate domains and segmented within their own channel.

    The chunking of content is such that each block contains X,Y,Z information, and they are separated by channels and time.
    This way external libraries won't have to be used to manipulate the matrices represented as arrays and can save computational time due to not
    having to rearrange the already existing matrices by adding time and channel data to them. Although the current chunking may not
    be the most efficient for retrieval.

    It seems that it doesn't matter whether the data is row or column major since N5 data block writer handles both appropriately, only when
    I started to add extra dimensions to the array matrix did it start to misbehave.

      //vcData.addFunction(new AugmentedFunction("function_name", new Expression("x + 100*y + sin(t)"))

    // X,Y,Z raster for time 0 for variable cAMP
        // index in matrix c + nc * ri  2D
        // (c + nc * ri) * block_size + zi 3D
    //positive y goes down instead of up
 */
public class N5Exporter implements ExportConstants {
    private final static Logger lg = LogManager.getLogger(N5Exporter.class);
    private static OutputContext outputContext;

    public N5Exporter() {
        // Have the HDF5 exporter export its data to a file, then read with N5HDF5 reader, then block by block write it into N5
        // Grab the object that has the data for sim files in blocks, then instead of writting the file, feed it into N5FSwriter
        // Get the simulated datablock output, then rewrite that as chunks within the N5 dataset, not having to
    }

    public static void main(String[] args) throws MathException, IOException, DataAccessException {
        String threeDImage = "1115478432";
        String twoDImage = "2132749480";
        String twoDDomain = "L";
        String threeDDomain = "IP3_Cyt";
        ArrayList<String> species = new ArrayList<>(Arrays.asList("IP3_Cyt"));
        VCData vcData = N5Exporter.getVCData("/home/zeke/.vcell/simdata/temp", threeDImage);
        N5Exporter.exportToN5(vcData, "/home/zeke/Downloads/fullTest.n5", species);
    }

    //intake the variables people want, don't just take them all
    public static void exportToN5(VCData vcData, String outPutDir, ArrayList<String> species) throws MathException, DataAccessException, IOException {
        double[] allTimes = vcData.getDataTimes();


        // output context expects a list of annotated functions, vcData seems to already have a set of annotated functions

        OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
        DataIdentifier[] dataIdentifiers = vcData.getVarAndFunctionDataIdentifiers(outputContext);

//        DataIdentifier DexDataIdentifier = Arrays.stream(dataIdentifiers).filter(di -> di.getName().equals(threeDDomain)).findFirst().get();

        // With Dex's dataset ID get the data block during the first time instance t

        // Filter different dataset ID's
        ArrayList<DataIdentifier> volumeDataIDs = new ArrayList<>();
        ArrayList<DataIdentifier> otherDataIDs = new ArrayList<>();

        // need a better way to determine if the variable has simData that can be visually represented, all of these syscalls
        // take a lot of time, also just need a more accurate metric for filtering the different data ID's
        // I know N5 can handle unlimited channels, so the error being thrown must be for some reason

        // Can be the variable just shouldn't be represented in the dataset and is a mistake
        // Can be the way the data is chunked, it being too granular with time and channels, overwhelming N5 file reading

        // All variables can be visually represented, its just that some have its data already computed, and others are derived
        // from that data, so if we want to visualize that data we need to compute the results.


        // according to Jim what should happen is that the VCdata should automatically derive the data for me since it knows everything
        // and the only reason why this wouldn't work is due to some misconfiguration, but what is that misconfig
        for (DataIdentifier dataIdentifier : dataIdentifiers) {
            if (species.contains(dataIdentifier.getName())){
                AnnotatedFunction annotatedFunction;


                volumeDataIDs.add(dataIdentifier);
            }
            else {
                otherDataIDs.add(dataIdentifier);
            }
//            try {
//                vcData.getSimDataBlock(outputContext, dataIdentifier.getName(), allTimes[0]);
//                volumeDataIDs.add(dataIdentifier);
//
//            }
//            catch (Exception e){
//                otherDataIDs.add(dataIdentifier);
//            }
        }


//        DexDataIdentifier.getVariableType();

        int numChannels = volumeDataIDs.size();
        int numTimes = allTimes.length;
        long[] dimensions = {vcData.getMesh().getSizeX(), vcData.getMesh().getSizeY(), numChannels, vcData.getMesh().getSizeZ(), numTimes};
        // 51X, 51Y, 1Z, 1C, 2T
        int[] blockSize = {vcData.getMesh().getSizeX(), vcData.getMesh().getSizeY(), 1, vcData.getMesh().getSizeZ(), 1};
        String dataSet = vcData.getResultsInfoObject().getDataKey().toString() + " 2 " + vcData.getResultsInfoObject().getID();



        N5FSWriter n5FSWriter = new N5FSWriter(outPutDir, new GsonBuilder());
        RawCompression rawCompression = new RawCompression();
        DatasetAttributes datasetAttributes = new DatasetAttributes(dimensions, blockSize, org.janelia.saalfeldlab.n5.DataType.FLOAT64, rawCompression);
        n5FSWriter.createDataset(dataSet, datasetAttributes);
        N5MetaData.imageJMetaData(n5FSWriter, dataSet, vcData, numChannels);



        for (int channelIndex=0; channelIndex < numChannels; channelIndex++){
            for (int timeIndex=0; timeIndex < numTimes; timeIndex++){
                double [] data = vcData.getSimDataBlock(outputContext, volumeDataIDs.get(channelIndex).getName(), allTimes[timeIndex]).getData();
                DoubleArrayDataBlock doubleArrayDataBlock = new DoubleArrayDataBlock(blockSize, new long[]{0, 0, channelIndex, 0, timeIndex}, data);
                n5FSWriter.writeBlock(dataSet, datasetAttributes, doubleArrayDataBlock);
            }
        }

    }

    public static VCData getVCData(String dataDir, String simKeyID) throws IOException, DataAccessException {
        PropertyLoader.loadProperties();

        // tell where installation is
        System.setProperty(PropertyLoader.installationRoot,"/media/zeke/DiskDrive/App_Installations/VCell_Rel");

        // Set my user ID associated with VC database
        // set simulation key
        // make an object that ties the user to that simulation key
        // make an object that then identifies the simulation
        User user = new User("ezequiel23", new KeyValue("258925427"));

        KeyValue simKey = new KeyValue(simKeyID);
        VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, user);
        VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(vcSimID, 0);

        // Point a data controller to the directory where the sim data is and use the vcdID to retrieve information regarding the sim
        Cachetable cachetable = new Cachetable(10 * Cachetable.minute, 1000000L);
        DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(cachetable, new File(dataDir), null);

        // get dataset identifier from the simulation

        return dataSetControllerImpl.getVCData(vcdID);
    }

    public Double dataDeriveration(){
        return null;
    }

    public Double[] unitConversion(){
        return null;
    }
}