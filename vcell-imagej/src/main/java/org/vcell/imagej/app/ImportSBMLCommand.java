/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package org.vcell.imagej.app;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.ext.spatial.Geometry;
import org.sbml.jsbml.ext.spatial.SampledField;
import org.sbml.jsbml.ext.spatial.SpatialModelPlugin;
import org.scijava.command.Command;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.vcell.imagej.app.VCellOpenSBML.MyTest;

import net.imagej.ops.OpService;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedIntType;

/**
 *
 */
@Plugin(type = Command.class, menuPath = "Plugins>VCell>Import SBML",initializer = "initParameters")
public class ImportSBMLCommand<T extends RealType<T>> implements Command {
    //
    // Feel free to add more parameters here...
    //

//    @Parameter(persist = false)
//    private File file;

    @Parameter
    private OpService ops;

    @Parameter
    private DisplayService displayService;

    @Override
    public void run() {

//		List<Display<?>> displays = displayService.getDisplays();
//		for(Display<?> disp:displays){
//			System.out.println(disp);
//		}
    	try{
    		if(displayService.getActiveDisplay() == null || !(displayService.getActiveDisplay() instanceof MyTest)){
    			throw new Exception("Expecting active display of type "+MyTest.class.getName());
    		}
			// Read SBML document
	        MyTest activeDisplay = (MyTest)displayService.getActiveDisplay();
//	        activeDisplay.getSwingInputHarvester().processResults(activeDisplay.getSwingInputPanel(), activeDisplay.getDefaultMutableModule());
//    		for(String paramName:activeDisplay.getDefaultMutableModule().getInputs().keySet()){
//    			System.out.println(paramName+" = "+activeDisplay.getDefaultMutableModule().getInput(paramName));
//    		}
//if(true){return;}
			SBMLDocument document = SBMLReader.read(activeDisplay.getSbmlFile());
	
	        // Get SBML geometry
	        SpatialModelPlugin modelPlugin = (SpatialModelPlugin) document.getModel().getPlugin("spatial");
	        Geometry geometry = modelPlugin.getGeometry();
	        SampledField sampledField = geometry.getListOfSampledFields().get(0);
	
	
	        int width = sampledField.getNumSamples1();
	        int height = sampledField.getNumSamples2();
	        int zslices = sampledField.getNumSamples3();
	        // Parse pixel values to int
	        String[] imgStringArray = sampledField.getSamples().split(" ");
	        int[] imgArray = new int[width*height*zslices];
	        for (int i = 0; i < imgArray.length; i++) {
	        	imgArray[i] = Integer.parseInt(imgStringArray[i]);				        }
	
	        // Create the image and display
	        ArrayImg<UnsignedIntType, IntArray> img = ArrayImgs.unsignedInts(imgArray, width, height,zslices);
	        displayService.createDisplay(img);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
	private void initParameters() {
//		file = new File(RunVCellSimFromSBML.class.getResource("ImageJ_FRAP.xml").getFile());
	}

}
