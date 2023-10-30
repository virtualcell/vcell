package cbit.vcell.simdata.n5;

import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.janelia.saalfeldlab.n5.Compression;
import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.DoubleArrayDataBlock;
import org.janelia.saalfeldlab.n5.N5FSWriter;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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
    private DataSetControllerImpl dataSetController;

    private VCSimulationDataIdentifier vcDataID;

    public static final ArrayList<VariableType> unsupportedTypes = new ArrayList<>(Arrays.asList(
            VariableType.MEMBRANE,
            VariableType.VOLUME_REGION,
            VariableType.MEMBRANE_REGION,
            VariableType.UNKNOWN,
            VariableType.POINT_VARIABLE,
            VariableType.NONSPATIAL,
            VariableType.CONTOUR,
            VariableType.CONTOUR_REGION,
            VariableType.POSTPROCESSING
    ));

    private VCData vcData;

        // Have the HDF5 exporter export its data to a file, then read with N5HDF5 reader, then block by block write it into N5
        // Grab the object that has the data for sim files in blocks, then instead of writting the file, feed it into N5FSwriter
        // Get the simulated datablock output, then rewrite that as chunks within the N5 dataset, not having to

    //intake the variables people want, don't just take them all
    public void exportToN5(ArrayList<DataIdentifier> species, Compression compression) throws MathException, DataAccessException, IOException {
        double[] allTimes = vcData.getDataTimes();


        // output context expects a list of annotated functions, vcData seems to already have a set of annotated functions

        OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
        // With Dex's dataset ID get the data block during the first time instance t

        // All variables can be visually represented, its just that some have its data already computed, and others are derived
        // from that data, so if we want to visualize that data we need to compute the results.

        // according to Jim what should happen is that the VCdata should automatically derive the data for me since it knows everything
        // and the only reason why this wouldn't work is due to some misconfiguration, but what is that misconfig

        for (DataIdentifier specie: species){
            if(unsupportedTypes.contains(specie.getVariableType())){
                throw new RuntimeException("Tried to export a variable type that is not supported!");
            }
        }


//        DexDataIdentifier.getVariableType();

        int numVariables = species.size();
        int numTimes = allTimes.length;
        long[] dimensions = {vcData.getMesh().getSizeX(), vcData.getMesh().getSizeY(), numVariables, vcData.getMesh().getSizeZ(), numTimes};
        // 51X, 51Y, 1Z, 1C, 2T
        int[] blockSize = {vcData.getMesh().getSizeX(), vcData.getMesh().getSizeY(), 1, vcData.getMesh().getSizeZ(), 1};
        String dataSet = vcDataID.getID();


        N5FSWriter n5FSWriter = new N5FSWriter(this.getN5FileAbsolutePath(), new GsonBuilder());
        DatasetAttributes datasetAttributes = new DatasetAttributes(dimensions, blockSize, org.janelia.saalfeldlab.n5.DataType.FLOAT64, compression);
        HashMap<String, Object> additionalMetaData = new HashMap<>();

        n5FSWriter.createDataset(dataSet, datasetAttributes);
        N5MetaData.imageJMetaData(n5FSWriter, dataSet, vcData, numVariables, additionalMetaData);



        for (int variableIndex=0; variableIndex < numVariables; variableIndex++){

            //place to add tracking, each variable can be measured in tracking


            for (int timeIndex=0; timeIndex < numTimes; timeIndex++){
                //another place to add tracking, each time index can be used to determine how much has been exported


                // data does get returned, but it does not seem to cover the entire region of space, but only returns regions where there is activity
                double[] data = this.dataSetController.getSimDataBlock(outputContext, this.vcDataID, species.get(variableIndex).getName(), allTimes[timeIndex]).getData();
//                double [] data = vcData.getSimDataBlock(outputContext, volumeDataIDs.get(channelIndex).getName(), allTimes[timeIndex]).getData();
                DoubleArrayDataBlock doubleArrayDataBlock = new DoubleArrayDataBlock(blockSize, new long[]{0, 0, variableIndex, 0, timeIndex}, data);
                n5FSWriter.writeBlock(dataSet, datasetAttributes, doubleArrayDataBlock);
            }
        }

        n5FSWriter.close();
    }

    public void initalizeDataControllers(String simKeyID, String userName, String userKey) throws IOException, DataAccessException {
        // Set my user ID associated with VC database
        // set simulation key
        // make an object that ties the user to that simulation key
        // make an object that then identifies the simulation
        User user = new User(userName, new KeyValue(userKey));

        KeyValue simKey = new KeyValue(simKeyID);
        VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, user);
        this.vcDataID = new VCSimulationDataIdentifier(vcSimID, 0);

        // Point a data controller to the directory where the sim data is and use the vcdID to retrieve information regarding the sim
        Cachetable cachetable = new Cachetable(10 * Cachetable.minute, 1000000L);
        File primaryDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty));
        this.dataSetController = new DataSetControllerImpl(cachetable, primaryDir, null);

        // get dataset identifier from the simulation

        this.vcData = this.dataSetController.getVCData(vcDataID);
    }

    public VCData getVCData(){
        return vcData;
    }

    public DataSetControllerImpl getDataSetController() {
        return dataSetController;
    }

    public VCSimulationDataIdentifier getVcDataID() {
        return vcDataID;
    }

    public Double[] unitConversion(){
        return null;
    }



    public DataIdentifier getRandomDI() throws IOException, DataAccessException {
        ArrayList<DataIdentifier> list = new ArrayList<>(Arrays.asList(dataSetController.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), vcDataID)));
        Random random = new Random();
        DataIdentifier df = list.remove(random.nextInt(list.size()));
        while (unsupportedTypes.contains(df.getVariableType())){
            df = list.remove(random.nextInt(list.size()));
        }
        return df;
    }

    public DataIdentifier getSpecificDI(String diName) throws IOException, DataAccessException {
        ArrayList<DataIdentifier> list = new ArrayList<>(Arrays.asList(dataSetController.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), vcDataID)));
        for(DataIdentifier dataIdentifier: list){
            if(dataIdentifier.getName().equals(diName)){
                list.remove(dataIdentifier);
                return dataIdentifier;
            }
        }
        return null;
    }

    public ArrayList<String> getSupportedSpecies() throws IOException, DataAccessException {
        OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
        DataIdentifier[] dataSetIdentifiers = this.vcData.getVarAndFunctionDataIdentifiers(outputContext);

        ArrayList<String> supportedSpecies = new ArrayList<>();

        for(DataIdentifier specie: dataSetIdentifiers){
            if(!unsupportedTypes.contains(specie.getVariableType())){
                supportedSpecies.add(specie.getName());
            }
        }

        return supportedSpecies;
    }

    public String getN5FileAbsolutePath(){
        File outPutDir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.n5DataDir) + "/" + this.n5FileNameHash() + ".n5");
        return outPutDir.getAbsolutePath();
    }

    public String getN5DatasetName(){
        return vcDataID.getID();
    }

    public String n5FileNameHash(){
        return actualHash(vcDataID.getID(), String.valueOf(vcDataID.getJobIndex()));
    }

    public static String n5FileNameHash(String simID, String jobID){
        return actualHash(simID, jobID);
    }

    private static String actualHash(String simID, String jobID) {
        MessageDigest sha256 = DigestUtils.getSha256Digest();
        sha256.update(simID.getBytes(StandardCharsets.UTF_8));
        sha256.update(jobID.getBytes(StandardCharsets.UTF_8));

        return Hex.encodeHexString(sha256.digest());
    }


}