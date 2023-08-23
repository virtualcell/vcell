package cbit.vcell.simdata.n5;

import cbit.vcell.export.server.ExportConstants;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.util.Arrays;

public class N5Exporter implements ExportConstants {
    private final static Logger lg = LogManager.getLogger(N5Exporter.class);

    public N5Exporter() {

    }

    public static void main(String[] args) throws Exception {
        String primaryDirStr = "~/.vcell/simdata/temp";
        PropertyLoader.loadProperties();
        //System.setProperty(PropertyLoader.installationRoot,"/Users/schaff/workspace/vcell");
        User user = new User("schaff", new KeyValue("17"));
        KeyValue simKey = new KeyValue("1128909032");
        VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, user);
        VCSimulationDataIdentifier vcdID = new VCSimulationDataIdentifier(vcSimID, 0);

        Cachetable cachetable = new Cachetable(10 * Cachetable.minute, 1000000L);
        File primaryDir = new File(primaryDirStr);
        DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(cachetable, primaryDir, null);
        double[] allTimes = dataSetControllerImpl.getDataSetTimes(vcdID);

        // get dataset identifier for vcSimID
        VCData vcData = dataSetControllerImpl.getVCData(vcdID);
        OutputContext outputContext = new OutputContext(null);
        DataIdentifier[] dataIdentifiers = vcData.getVarAndFunctionDataIdentifiers(outputContext);
        DataIdentifier DexDataIdentifier = Arrays.stream(dataIdentifiers).filter(di -> di.getName().equals("Dex")).findFirst().get();
        SimDataBlock simDataBlock = vcData.getSimDataBlock(outputContext, DexDataIdentifier.getName(), allTimes[0]);
        double[] Dex_dataBlockValues = simDataBlock.getData(); // X,Y,Z raster for time 0 for variable cAMP
        System.out.println("Dex_dataBlockValues.length = " + Dex_dataBlockValues.length);
        System.out.println("Dex_dataBlockValues[0] = " + Dex_dataBlockValues[0]);

    }
}