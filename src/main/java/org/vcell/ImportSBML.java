/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package org.vcell;

import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.Cursor;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.ext.spatial.Geometry;
import org.sbml.jsbml.ext.spatial.SampledField;
import org.sbml.jsbml.ext.spatial.SpatialModelPlugin;
import org.scijava.command.Command;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

/**
 *
 */
@Plugin(type = Command.class, menuPath = "Plugins>SBML>Import SBML")
public class ImportSBML<T extends RealType<T>> implements Command {
    //
    // Feel free to add more parameters here...
    //

    @Parameter(persist = false)
    private File file;

    @Parameter
    private OpService ops;

    @Parameter
    private DisplayService displayService;

    @Override
    public void run() {

        // Read SBML document
        SBMLDocument document = null;
        try {
            document = SBMLReader.read(file);
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }

        // Get SBML geometry
        SpatialModelPlugin modelPlugin = (SpatialModelPlugin) document.getModel().getPlugin("spatial");
        Geometry geometry = modelPlugin.getGeometry();
        SampledField sampledField = geometry.getListOfSampledFields().get(0);


        // Parse pixel values to int
        String[] imgStringArray = sampledField.getSamples().split(" ");
        int[] imgArray = new int[imgStringArray.length];
        for (int i = 0; i < imgStringArray.length; i++) {
            imgArray[i] = Integer.parseInt(imgStringArray[i]);
        }

        // Create the image and display
        int width = sampledField.getNumSamples1();
        int height = sampledField.getNumSamples2();
        ArrayImg<UnsignedIntType, IntArray> img = ArrayImgs.unsignedInts(imgArray, width, height);
        displayService.createDisplay(img);

        // Erode the image (just an example)
        ArrayImg eroded = (ArrayImg) ops.morphology().erode(img, new HyperSphereShape(10));
        displayService.createDisplay(eroded);

        StringBuffer sb = new StringBuffer();
        Cursor cursor = eroded.localizingCursor();
        while (cursor.hasNext()) {
            sb.append(cursor.next() + " ");
        }
        sampledField.setSamples(sb.toString().trim());

//        File destination = new File(file.getPath() + "/eroded");
//        SBMLWriter writer = new SBMLWriter();
//        try {
//            writer.write(document, destination);
//        } catch (XMLStreamException | IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * This main function serves for development purposes.
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {

//        String dir = "/Users/kevingaffney/Desktop/";
//        String fluorPath = dir + "SimID_113986066_0__9times_1vars_data.nrrd";
//        String geomPath = dir + "geom.tif";
//
//
//        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
//        Dataset fluor = ij.scifio().datasetIO().open(fluorPath);
//        Dataset geom = ij.scifio().datasetIO().open(geomPath);
//        ij.ui().show(fluor);
//        ij.ui().show(geom);
//        ij.command().run(DeconstructGeometry.class, true);

//
//        String dir = "/Users/kevingaffney/Documents/UConn/Model Data/1J23_1I66_1J60COS7p/p1/";
//        String path1 = dir + "TIRF_594_RFPm_495LP.tif";
//        String path2 = dir + "TIRF_515_YFPm_495LP.tif";
//
//        Dataset data1 = ij.scifio().datasetIO().open(path1);
//        Dataset data2 = ij.scifio().datasetIO().open(path2);
//        ij.ui().show(data1);
//        ij.ui().show(data2);
//        ij.command().run(RatioMap.class, true);

        ij.command().run(VirtualTIRF.class, true);
    }
}
