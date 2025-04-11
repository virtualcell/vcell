package cbit.vcell.field;

import cbit.image.ImageException;
import cbit.image.ImageSizeInfo;
import cbit.vcell.VirtualMicroscopy.BioformatsImageImpl;
import cbit.vcell.VirtualMicroscopy.HiddenNonImageFile;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.field.io.FieldDataFileOperationSpec;
import cbit.vcell.math.VariableType;
import loci.formats.ImageReader;
import loci.formats.UnknownFormatException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.vcellij.ImageDatasetReader;
import org.vcell.vcellij.ImageDatasetReaderService;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class FieldDataFileConversion {
    private static final Logger logger = LogManager.getLogger(FieldDataFileConversion.class);
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

    private static File getFirstFileFromZip(String imageID) throws IOException {
        ZipFile zipFile = new ZipFile(new File(imageID),ZipFile.OPEN_READ);
        Enumeration<? extends ZipEntry> enumZipEntry = zipFile.entries();
        //Sort entryNames because ZipFile doesn't guarantee order
        while (enumZipEntry.hasMoreElements()){
            ZipEntry entry = enumZipEntry.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            String entryName = entry.getName();
            if (entryName.contains("..")) {
                throw new IOException("Invalid zip entry name: " + entryName);
            }
            String imageFileSuffix = null;
            int dotIndex = entryName.indexOf(".");
            if(dotIndex != -1){
                imageFileSuffix = entryName.substring(dotIndex);
            }
            InputStream zipInputStream = zipFile.getInputStream(entry);
            File tempImageFile = File.createTempFile("ImgDataSetReader", imageFileSuffix);
            tempImageFile.deleteOnExit();
            if (!tempImageFile.toPath().normalize().startsWith(new File(System.getProperty("java.io.tmpdir")).toPath())) {
                throw new IOException("Invalid file path: " + tempImageFile.getAbsolutePath());
            }
            FileOutputStream fos = new FileOutputStream(tempImageFile,false);
            fos.write(zipInputStream.readAllBytes());
            fos.close();
            zipInputStream.close();
            zipFile.close();
            return tempImageFile;
        }
        ZipException zipException = new ZipException(String.format("Zip file contains only directories: %s", imageID));
        logger.error(zipException);
        throw zipException;
    }

    public static FieldDataFileOperationSpec analyzeMetaData(File imageFile) throws Exception {
        final FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();
        File analyzedFile = imageFile;
        if (imageFile.getName().toLowerCase().endsWith("zip")){
            analyzedFile = getFirstFileFromZip(imageFile.getAbsolutePath());
        }
        BioformatsImageImpl imageHandler = new BioformatsImageImpl();
        ImageReader imageReader = imageHandler.getImageReader(analyzedFile.getAbsolutePath());
        ImageSizeInfo info = imageHandler.getImageSizeInfo(imageReader, analyzedFile.getAbsolutePath(), null);
        BioformatsImageImpl.DomainInfo domainInfo = BioformatsImageImpl.getDomainInfo(imageReader);

        // [time][var][data]
        fdos.variableTypes = new VariableType[info.getNumChannels()];
        fdos.varNames = new String[info.getNumChannels()];
        // For Channel
        for (int c = 0; c < info.getNumChannels(); c += 1) {
            fdos.variableTypes[c] = VariableType.VOLUME;
            fdos.varNames[c] = "Channel" + c;
        }
        fdos.times = info.getTimePoints();
        if (fdos.times == null) {
            fdos.times = new double[imageReader.getSizeT()];
            for (int i = 0; i < fdos.times.length; i += 1) {
                fdos.times[i] = i;
            }
        }

        fdos.origin = (domainInfo.getOrigin() != null ? domainInfo.getOrigin()
                : new Origin(0, 0, 0));
        fdos.extent = (domainInfo.getExtent() != null) ? (domainInfo.getExtent()) : (new Extent(1, 1, 1));
        fdos.isize = domainInfo.getiSize();

        return fdos;
    }

    public static FieldDataFileOperationSpec createFDOSWithChannels(ImageDataset[] imagedataSets,
                                                                    Integer saveOnlyThisTimePointIndex) {
        final FieldDataFileOperationSpec fdos = new FieldDataFileOperationSpec();

        // [time][var][data]
        int numXY = imagedataSets[0].getISize().getX() * imagedataSets[0].getISize().getY();
        int numXYZ = imagedataSets[0].getSizeZ() * numXY;
        fdos.variableTypes = new VariableType[imagedataSets.length];
        fdos.varNames = new String[imagedataSets.length];
        int timeSize = saveOnlyThisTimePointIndex != null ? 1 : imagedataSets[0].getSizeT();
        short[][][] shortData = new short[timeSize][imagedataSets.length][numXYZ];
        // For Channel
        for (int c = 0; c < imagedataSets.length; c += 1) {
            fdos.variableTypes[c] = VariableType.VOLUME;
            fdos.varNames[c] = "Channel" + c;
            // For Time
            for (int t = 0; t < imagedataSets[c].getSizeT(); t += 1) {
                if (saveOnlyThisTimePointIndex != null && saveOnlyThisTimePointIndex.intValue() != t) {
                    continue;
                }
                int zOffset = 0;
                // For z-slice
                for (int z = 0; z < imagedataSets[c].getSizeZ(); z += 1) {
                    UShortImage ushortImage = imagedataSets[c].getImage(z, 0, t);
                    short[] dest = shortData[(saveOnlyThisTimePointIndex != null ? 0 : t)][c];
                    System.arraycopy(ushortImage.getPixels(), 0, dest, zOffset, numXY);
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
