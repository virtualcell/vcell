package org.vcell;

import net.imagej.ImageJ;

import java.io.IOException;

/**
 * Created by kevingaffney on 6/23/17.
 */
public class Driver {
    public static void main(String... args) throws IOException {
        ImageJ ij = new ImageJ();
        ij.ui().showUI();
        ij.command().run(MainCommand.class, true);

//        String path = "/Users/kevingaffney/Desktop/op_PM";
//        VCellResultService resultsService = new VCellResultService();
//        Img<FloatType> img = resultsService.importCsv(new File(path), ij.getContext().getService(OpService.class));
//
//        DisplayService displayService = ij.getContext().getService(DisplayService.class);
//        displayService.createDisplay(img);

//        String dir = "/Users/kevingaffney/Desktop/";
//        String geomPath = dir + "geom.tif";
//        String resultsPMPath = dir + "results_PM.tif";
//        String resultsCytPath = dir + "results_Cyt.tif";
//
//        Dataset geom = ij.scifio().datasetIO().open(geomPath);
//        Dataset resultsPM  = ij.scifio().datasetIO().open(resultsPMPath);
//        Dataset resultsCyt = ij.scifio().datasetIO().open(resultsCytPath);
//
//        ij.ui().show(geom);
//        ij.ui().show(resultsPM);
//        ij.ui().show(resultsCyt);
//
//        ij.command().run(ConstructTIRFImage.class, true);
    }
}
