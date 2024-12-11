package cbit.vcell.field;

import cbit.image.ImageException;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.math.VariableType;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.vcellij.ImageDatasetReader;
import org.vcell.vcellij.ImageDatasetReaderService;

import java.awt.*;
import java.io.File;
import java.util.zip.DataFormatException;

public class FieldDataFileConversion {
    public static FieldDataFileOperationSpec createFDOSFromImageFile(File imageFile, boolean bCropOutBlack,
                                                                     Integer saveOnlyThisTimePointIndex) throws DataFormatException, ImageException {
        try {
            ImageDatasetReader imageDatasetReader = ImageDatasetReaderService.getInstance().getImageDatasetReader();
            ImageDataset[] imagedataSets = imageDatasetReader.readImageDatasetChannels(imageFile.getAbsolutePath(),
                    null, false, saveOnlyThisTimePointIndex, null);
            if (imagedataSets != null && bCropOutBlack) {
                for (int i = 0; i < imagedataSets.length; i++) {
                    Rectangle nonZeroRect = imagedataSets[i].getNonzeroBoundingRectangle();
                    if (nonZeroRect != null) {
                        imagedataSets[i] = imagedataSets[i].crop(nonZeroRect);
                    }
                }
            }
            return createFDOSWithChannels(imagedataSets, null);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new DataFormatException(e.getMessage());
        }
    }

    public static FieldDataFileOperationSpec createFDOSWithChannels(ImageDataset[] imagedataSets,
                                                                    Integer saveOnlyThisTimePointIndex) {
        final FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();

        // [time][var][data]
        int numXY = imagedataSets[0].getISize().getX() * imagedataSets[0].getISize().getY();
        int numXYZ = imagedataSets[0].getSizeZ() * numXY;
        fdos.variableTypes = new VariableType[imagedataSets.length];
        fdos.varNames = new String[imagedataSets.length];
        short[][][] shortData = new short[(saveOnlyThisTimePointIndex != null ? 1
                : imagedataSets[0].getSizeT())][imagedataSets.length][numXYZ];
        for (int c = 0; c < imagedataSets.length; c += 1) {
            fdos.variableTypes[c] = VariableType.VOLUME;
            fdos.varNames[c] = "Channel" + c;
            for (int t = 0; t < imagedataSets[c].getSizeT(); t += 1) {
                if (saveOnlyThisTimePointIndex != null && saveOnlyThisTimePointIndex.intValue() != t) {
                    continue;
                }
                int zOffset = 0;
                for (int z = 0; z < imagedataSets[c].getSizeZ(); z += 1) {
                    UShortImage ushortImage = imagedataSets[c].getImage(z, 0, t);
                    System.arraycopy(ushortImage.getPixels(), 0,
                            shortData[(saveOnlyThisTimePointIndex != null ? 0 : t)][c], zOffset, numXY);
//				shortData[t][c] = ushortImage.getPixels();
                    zOffset += numXY;
                }
            }
        }
        fdos.shortSpecData = shortData;
        fdos.times = imagedataSets[0].getImageTimeStamps();
        if (fdos.times == null) {
            fdos.times = new double[imagedataSets[0].getSizeT()];
            for (int i = 0; i < fdos.times.length; i += 1) {
                fdos.times[i] = i;
            }
        }

        fdos.origin = (imagedataSets[0].getAllImages()[0].getOrigin() != null
                ? imagedataSets[0].getAllImages()[0].getOrigin()
                : new Origin(0, 0, 0));
        fdos.extent = (imagedataSets[0].getExtent() != null) ? (imagedataSets[0].getExtent()) : (new Extent(1, 1, 1));
        fdos.isize = imagedataSets[0].getISize();

        return fdos;

    }
}
