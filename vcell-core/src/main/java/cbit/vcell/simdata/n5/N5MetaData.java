package cbit.vcell.simdata.n5;

import cbit.vcell.math.MathException;
import cbit.vcell.simdata.VCData;
import org.janelia.saalfeldlab.n5.DatasetAttributes;
import org.janelia.saalfeldlab.n5.N5FSWriter;
import org.vcell.util.DataAccessException;

import java.io.IOException;
import java.util.HashMap;

/*
    Take the VCell sim data, and create metadata that can be used within different applications
    Focus for now is ImageJ, creating the metadata that can be used for it
    https://github.com/saalfeldlab/n5-ij/wiki/N5-Metadata-Dialects
    Dataset has to already be made, then use the N5FSWriter to add attributes to it, using some sort of hash map with all of the required attributes

    The units vary between different variables so need to ask someone about this??


    pixelWidth: number (float)
        pixel spacing of the x axis (see Calibration.pixelWidth)
        corresponds to scale[0] in (Physical space)
 */
public class N5MetaData {
    //https://github.com/saalfeldlab/n5-ij/wiki/TranslateMetadata#setscale2d
    public static void imageJMetaData(N5FSWriter n5FSWriter, String datasetPath, VCData vcData, int numChannels, HashMap<String, Object> additionalMetData) throws MathException, DataAccessException {
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("name", "TestName");
        metaData.put("fps", 0.0);
        metaData.put("frameInterval", 0.0);
        metaData.put("pixelWidth", 1.0);
        metaData.put("pixelHeight", 1.0);
        metaData.put("pixelDepth", 1.0);
        metaData.put("xOrigin", 0.0);
        metaData.put("yOrigin", 0.0);
        metaData.put("zOrigin", 0.0);
        metaData.put("numChannels", numChannels); //
        metaData.put("numSlices", vcData.getMesh().getSizeZ());
        metaData.put("numFrames", vcData.getDataTimes().length);
        metaData.put("type", 2); //https://imagej.nih.gov/ij/developer/api/ij/ij/ImagePlus.html#getType() Grayscale with float types
        metaData.put("unit", "uM"); //https://imagej.nih.gov/ij/developer/api/ij/ij/measure/Calibration.html#getUnit()
        metaData.put("properties", additionalMetData);

        try {
            n5FSWriter.setAttributes(datasetPath, metaData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



}
