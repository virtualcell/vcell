package cbit.vcell.field;

import cbit.image.ImageException;
import cbit.image.ImageSizeInfo;
import cbit.vcell.VirtualMicroscopy.BioformatsImageImpl;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.field.io.FieldData;
import cbit.vcell.field.io.FieldDataSpec;
import cbit.vcell.math.VariableType;
import loci.formats.ImageReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.Extent;
import org.vcell.util.Origin;
import org.vcell.vcellij.ImageDatasetReader;
import org.vcell.vcellij.ImageDatasetReaderService;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class FieldDataFileConversion {
    private static final Logger logger = LogManager.getLogger(FieldDataFileConversion.class);
    public static FieldData createFDOSFromImageFile(File imageFile, boolean bCropOutBlack,
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

    public static File getFileFromZipEntry(ZipEntry zipEntry, ZipFile zipFile) throws IOException {
        String entryName = zipEntry.getName();
        if (entryName.contains("..")) {
            throw new IOException("Invalid zip entry name: " + entryName);
        }
        String imageFileSuffix = null;
        int dotIndex = entryName.indexOf(".");
        if(dotIndex != -1){
            imageFileSuffix = entryName.substring(dotIndex);
        }
        InputStream zipInputStream = zipFile.getInputStream(zipEntry);
        File tempImageFile = File.createTempFile("ImgDataSetReader", imageFileSuffix);
        tempImageFile.deleteOnExit();
        if (!tempImageFile.toPath().normalize().startsWith(new File(System.getProperty("java.io.tmpdir")).toPath())) {
            throw new IOException("Invalid file path: " + tempImageFile.getAbsolutePath());
        }
        FileOutputStream fos = new FileOutputStream(tempImageFile,false);
        fos.write(zipInputStream.readAllBytes());
        fos.close();
        zipInputStream.close();
        return tempImageFile;
    }

    private static File getFirstFileFromZip(String imageID) throws IOException {
        ZipFile zipFile = new ZipFile(new File(imageID),ZipFile.OPEN_READ);
        Enumeration<? extends ZipEntry> enumZipEntry = zipFile.entries();
        //Sort entryNames because ZipFile doesn't guarantee order
        while (enumZipEntry.hasMoreElements()){
            ZipEntry entry = enumZipEntry.nextElement();
            if (entry.isDirectory()) {
                continue;
            } else if (entry.getName().toLowerCase().contains("jpg") || entry.getName().toLowerCase().contains("jpeg")) {
                throw new UnsupportedEncodingException("JPEG Is Not Supported. Please use an image that is either single channel or has channel information clearly stated.");
            }
            File tempImageFile = getFileFromZipEntry(entry, zipFile);
            zipFile.close();
            return tempImageFile;
        }
        ZipException zipException = new ZipException(String.format("Zip file contains only directories: %s", imageID));
        logger.error(zipException);
        throw zipException;
    }

    public static FieldDataSpec analyzeMetaData(File imageFile) throws Exception {
        FieldDataSpec spec = new FieldDataSpec();
        File analyzedFile = imageFile;
        int timePoints = 0;
        if (imageFile.getName().toLowerCase().endsWith("zip")){
            analyzedFile = getFirstFileFromZip(imageFile.getAbsolutePath());
            try(ZipFile zipFile = new ZipFile(imageFile)){
                Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
                while (enumeration.hasMoreElements()){
                    enumeration.nextElement();
                    timePoints += 1;
                }
            }
        }
        BioformatsImageImpl imageHandler = new BioformatsImageImpl();
        ImageReader imageReader = imageHandler.getImageReader(analyzedFile.getAbsolutePath());
        ImageSizeInfo info = imageHandler.getImageSizeInfo(imageReader, analyzedFile.getAbsolutePath(), null);
        BioformatsImageImpl.DomainInfo domainInfo = BioformatsImageImpl.getDomainInfo(imageReader);


        BioformatsImageImpl.checkImageDimensionsSize(info.getiSize().getX(), info.getiSize().getY(),
                info.getiSize().getZ(),  timePoints > 0 ? timePoints : info.getTimePoints().length, info.getNumChannels());

        // [time][var][data]
        // For Channel
        spec.channelNames = new ArrayList<>();
        for (int c = 0; c < info.getNumChannels(); c += 1) {
            spec.channelNames.add("Channel" + c);
        }
        spec.times = new ArrayList<>();
        double[] times = info.getTimePoints();
        if (times == null) {
            times = new double[imageReader.getSizeT()];
            for (int i = 0; i < times.length; i += 1) {
                times[i] = i;
            }
        }
        for (double tp : times){
            spec.times.add(tp);
        }


        spec.origin = (domainInfo.getOrigin() != null ? domainInfo.getOrigin()
                : new Origin(0, 0, 0));
        spec.extent = (domainInfo.getExtent() != null) ? (domainInfo.getExtent()) : (new Extent(1, 1, 1));
        spec.iSize = domainInfo.getiSize();
        spec.file = imageFile;

        return spec;
    }

    public static FieldData createFDOSWithChannels(ImageDataset[] imagedataSets,
                                                   Integer saveOnlyThisTimePointIndex) {
        FieldData fieldData = new FieldData();

        // [time][var][data]
        int numXY = imagedataSets[0].getISize().getX() * imagedataSets[0].getISize().getY();
        int numXYZ = imagedataSets[0].getSizeZ() * numXY;
        fieldData.variableTypes = new VariableType[imagedataSets.length];
        fieldData.channelNames = new ArrayList<>();
        int timeSize = saveOnlyThisTimePointIndex != null ? 1 : imagedataSets[0].getSizeT();
        short[][][] shortData = new short[timeSize][imagedataSets.length][numXYZ];
        // For Channel
        for (int c = 0; c < imagedataSets.length; c += 1) {
            fieldData.variableTypes[c] = VariableType.VOLUME;
            fieldData.channelNames.add("Channel" + c);
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
        fieldData.data = shortData;

        double[] times = imagedataSets[0].getImageTimeStamps();
        fieldData.times = new ArrayList<>();
        if (times == null) {
            times = new double[imagedataSets[0].getSizeT()];
            for (int i = 0; i < times.length; i += 1) {
                times[i] = i;
            }
        }
        for(double tp : times){
            fieldData.times.add(tp);
        }

        fieldData.origin = (imagedataSets[0].getAllImages()[0].getOrigin() != null
                ? imagedataSets[0].getAllImages()[0].getOrigin()
                : new Origin(0, 0, 0));
        fieldData.extent = (imagedataSets[0].getExtent() != null) ? (imagedataSets[0].getExtent()) : (new Extent(1, 1, 1));
        fieldData.iSize = imagedataSets[0].getISize();

        return fieldData;

    }
}
