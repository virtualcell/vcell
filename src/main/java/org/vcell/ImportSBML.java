/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package org.vcell;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.ext.spatial.Geometry;
import org.sbml.jsbml.ext.spatial.SampledField;
import org.sbml.jsbml.ext.spatial.SpatialModelPlugin;
import org.scijava.command.Command;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.OpService;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;

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
    }
}
