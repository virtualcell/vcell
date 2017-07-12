package org.vcell;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import org.sbml.jsbml.SBMLDocument;

import java.io.File;
import java.util.concurrent.*;

/**
 * Created by kevingaffney on 7/10/17.
 */
public class VCellService {

    private VCellResultService vCellResultService;

    public VCellService(VCellResultService vCellResultService) {
        this.vCellResultService = vCellResultService;
    }

    public Future<VCellResult> runSimulation(SBMLDocument sbmlDocument) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<VCellResult> callable = () -> {

            // TODO: Run actual VCell simulation
            // Instead of importing results locally, we will have VCell run an actual simulation and return the result
            // VCell will use the sbmlDocument parameter to construct the model and run the simulation
            String dir = "/Users/kevingaffney/Desktop/myproj/";
            Dataset membraneDataset = vCellResultService.importCsv(new File(dir + "op_PM"));
            Dataset volumeDataset = vCellResultService.importCsv(new File(dir + "op_Cyt"));
            Thread.sleep(5000);

            return new VCellResult(membraneDataset, volumeDataset);
        };

        return executorService.submit(callable);
    }
}
