package cbit.vcell.VirtualMicroscopy;

import cbit.image.ImageSizeInfo;
import cbit.vcell.field.FieldDataFileConversion;
import loci.formats.*;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.vcellij.ImageDatasetReader;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class BioformatsImageImpl implements ImageDatasetReader {
    private static final Logger logger = org.apache.logging.log4j.LogManager.getLogger(BioformatsImageImpl.class);

    public static final boolean BIO_FORMATS_DEBUG = false;

    public ImageSizeInfo getImageSizeInfo(String fileName, Integer forceZSize) throws Exception {
        ImageSizeInfo imageSizeInfo = null;
        if(fileName.toUpperCase().endsWith(".ZIP")){
            if(forceZSize != null){
                throw new RuntimeException("ZIP file unexpected forceZSize");
            }
            ImageDataset[] imageDatasets  =  readZipFile(fileName, false, false,null);
            ZipFile zipFile = null;
            try{
                zipFile = new ZipFile(new File(fileName),ZipFile.OPEN_READ);
                ISize iSize = new ISize(imageDatasets[0].getAllImages()[0].getNumX(), imageDatasets[0].getAllImages()[0].getNumY(), zipFile.size());
                double[] times = new double[] {0};
                imageSizeInfo = new ImageSizeInfo(fileName,iSize, imageDatasets.length, times, 0);
            }finally{
                if(zipFile != null){
                    try{zipFile.close();}catch(Exception e){e.printStackTrace();/*ignore and continue*/}
                }
            }
        }else{
            ImageReader imageReader = getImageReader(fileName);
            return getImageSizeInfo(imageReader, fileName, forceZSize);
        }
        return imageSizeInfo;
    }

    public ImageSizeInfo getImageSizeInfo(ImageReader imageReader, String fileName, Integer forceZSize) throws Exception{
        ImageSizeInfo imageSizeInfo = null;
        DomainInfo domainInfo = getDomainInfo(imageReader);
        ISize iSize = (forceZSize == null?domainInfo.getiSize():new ISize(domainInfo.getiSize().getX(), domainInfo.getiSize().getY(), forceZSize));
        Time[] times = getTimes(imageReader);
        double[] times_double = new double[times.length];
        for (int i=0;i<times.length;i++){
            times_double[i] = times[i].value().doubleValue();
        }
        imageSizeInfo = new ImageSizeInfo(fileName, iSize,imageReader.getSizeC(),times_double,0);
        return imageSizeInfo;
    }

    public ImageReader getImageReader(String imageID) throws FormatException,IOException{
        ImageReader imageReader = new ImageReader();
        imageReader.setNormalized(true);//normalize floats

        MetadataStore store = MetadataTools.createOMEXMLMetadata();
        store.createRoot();
        imageReader.setMetadataStore(store);
        imageReader.setId(imageID);
        return imageReader;
    }

    private ImageDataset[] readZipFile(String imageID,boolean bAll,boolean bMergeChannels,ISize resize) throws Exception{
        ZipFile zipFile = new ZipFile(new File(imageID),ZipFile.OPEN_READ);
        Vector<Vector<ImageDataset>> imageDataForEachChannelV = new Vector<Vector<ImageDataset>>();
        Enumeration<? extends ZipEntry> enumZipEntry = zipFile.entries();
        int numChannels = -1;
        //Sort entryNames because ZipFile doesn't guarantee order
        TreeMap<String, Integer> sortedChannelsTreeMap = new TreeMap<String, Integer>();
        int zipCntr = 0;
        while (enumZipEntry.hasMoreElements()){
            logger.info("Reading zip "+(zipCntr+1)+" of "+zipFile.size());
            zipCntr++;
            ZipEntry entry = enumZipEntry.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            File tempImageFile = FieldDataFileConversion.getFileFromZipEntry(entry, zipFile);
            String entryName = entry.getName();
            ImageDataset[] imageDatasetChannels = null;
            try {
                imageDatasetChannels = readImageDatasetChannelsAlgo(tempImageFile.getAbsolutePath(),bMergeChannels,null,resize);
            } catch (UnknownFormatException ufe) {
                //we check the exception, rather than testing a priori, because this is a rare use case
                if (HiddenNonImageFile.isHidden(entryName)) {
                    continue;
                }
                throw ufe;
            }
            if(numChannels == -1){
                numChannels = imageDatasetChannels.length;
                for (int i = 0; i < numChannels; i++) {
                    imageDataForEachChannelV.add(new Vector<ImageDataset>());
                }
            }
            if(numChannels != imageDatasetChannels.length){
                throw new RuntimeException("ZipFile reader found images with different number of channels");
            }

            sortedChannelsTreeMap.put(entryName, imageDataForEachChannelV.elementAt(0).size());
            for (int i = 0; i < numChannels; i++) {
                imageDataForEachChannelV.elementAt(i).add(imageDatasetChannels[i]);
            }

            tempImageFile.delete();
            if(!bAll){
                break;
            }
        }
        zipFile.close();
        ImageDataset[] completeImageDatasetChannels = new ImageDataset[imageDataForEachChannelV.size()];
        Integer[] sortIndexes = sortedChannelsTreeMap.values().toArray(new Integer[0]);
        for (int i = 0; i < completeImageDatasetChannels.length; i++) {
            //sort based on entryName
            ImageDataset[] unSortedChannel = imageDataForEachChannelV.elementAt(i).toArray(new ImageDataset[0]);
            ImageDataset[] sortedChannel = new ImageDataset[unSortedChannel.length];
            for (int j = 0; j < unSortedChannel.length; j++) {
                sortedChannel[j] = unSortedChannel[sortIndexes[j]];
            }
            completeImageDatasetChannels[i] = createZStack(sortedChannel);
        }
        return completeImageDatasetChannels;

    }

    @Override
    public ImageSizeInfo getImageSizeInfoForceZ(String fileName, Integer forceZSize) throws Exception {
        return getImageSizeInfo(fileName, forceZSize);
    }

    @Override
    public ImageDataset readImageDataset(String imageID, ClientTaskStatusSupport status) throws Exception {
        var imageDataset = readImageDatasetChannelsAlgo(imageID,true,null,null)[0];
        if (status != null){
            status.setProgress(100);
        }else{
            logger.warn("ClientTaskStatusSupport var status is null");
        }
        return imageDataset;
    }

    @Override
    public ImageDataset[] readImageDatasetChannels(String imageID, ClientTaskStatusSupport status, boolean bMergeChannels, Integer timeIndex, ISize resize) throws Exception {
        ImageDataset[] imageDatasets;
        if (imageID.toUpperCase().endsWith(".ZIP")){
            imageDatasets = readZipFile(imageID, true, bMergeChannels,resize);
        } else{
            imageDatasets = readImageDatasetChannelsAlgo(imageID, bMergeChannels, timeIndex, resize);
        }
        System.gc();
        if (status != null){
            status.setProgress(100);
        }else{
            logger.warn("ClientTaskStatusSupport var status is null");
        }
        return imageDatasets;
    }

    // Direct call to this function
    @Override
    public ImageDataset readImageDatasetFromMultiFiles(File[] files, ClientTaskStatusSupport status, boolean isTimeSeries, double timeInterval) throws Exception {
        var imageDataset = readImageDatasetFromMultiFiles(files, isTimeSeries, timeInterval);
        if (status != null){
            status.setProgress(100);
        }else{
            logger.warn("ClientTaskStatusSupport var status is null");
        }
        return imageDataset;
    }

    public static class DomainInfo{
        private ISize iSize;
        private Extent extent;
        private Origin origin;
        public DomainInfo(ISize iSize, Extent extent, Origin origin) {
            super();
            this.iSize = iSize;
            this.extent = extent;
            this.origin = origin;
        }
        public ISize getiSize() {
            return iSize;
        }
        public Extent getExtent() {
            return extent;
        }
        public Origin getOrigin(){
            return origin;
        }
    }

    public static DomainInfo getDomainInfo(ImageReader imageReader){
        MetadataRetrieve metadataRetrieve = (MetadataRetrieve)imageReader.getMetadataStore();
        int sizeX = metadataRetrieve.getPixelsSizeX(0).getValue();
        int sizeY = metadataRetrieve.getPixelsSizeY(0).getValue();
        int sizeZ = metadataRetrieve.getPixelsSizeZ(0).getValue();
        ISize iSize = new ISize(sizeX, sizeY, sizeZ);
        Number pixelSizeX_m = um(metadataRetrieve.getPixelsPhysicalSizeX(0));
        Number pixelSizeY_m = um(metadataRetrieve.getPixelsPhysicalSizeY(0));
        Number pixelSizeZ_m = um(metadataRetrieve.getPixelsPhysicalSizeZ(0));
        if (pixelSizeX_m==null || pixelSizeX_m.doubleValue()==0){
            pixelSizeX_m = .3e-6;
        }
        if (pixelSizeY_m==null || pixelSizeY_m.doubleValue()==0){
            pixelSizeY_m = .3e-6;
        }
        if (pixelSizeZ_m==null || pixelSizeZ_m.doubleValue()==0 || pixelSizeZ_m.doubleValue()==1){
            pixelSizeZ_m = 0.9e-6;
        }

        Extent extent = null;
        if (pixelSizeX_m.doubleValue()>0 &&
                pixelSizeY_m.doubleValue()>0 &&
                pixelSizeZ_m.doubleValue()>0){
            extent = new Extent(pixelSizeX_m.doubleValue()*iSize.getX(),
                    pixelSizeY_m.doubleValue()*iSize.getY(),
                    pixelSizeZ_m.doubleValue()*iSize.getZ());
        }else{
            extent = new Extent(1,1,1);
        }
        Origin origin = new Origin(0,0,0);
        return new DomainInfo(iSize, extent,origin);
    }

    // Get an image, read its different channels, and return each channel as an Image Data set that has been read
    public ImageDataset[] readImageDatasetChannelsAlgo(String imageID, boolean bMergeChannels, Integer timeIndex, ISize resize) throws Exception {
        ImageReader imageReader = getImageReader(imageID);
        DomainInfo domainInfo = getDomainInfo(imageReader);
        IFormatReader formatReader = imageReader.getReader(imageID);
        if(BIO_FORMATS_DEBUG){
            printInfo(imageReader, imageID, formatReader);
        }
        try{
            int channelRGBSize = bMergeChannels ? 1 : formatReader.getRGBChannelCount()*formatReader.getEffectiveSizeC();
            int tzSize = timeIndex == null? formatReader.getSizeT()*formatReader.getSizeZ() : formatReader.getSizeZ();
            UShortImage[][] ushortImageCTZArr = new UShortImage[channelRGBSize][tzSize];
            int tzIndex = 0;
            int startTime = timeIndex==null ? 0 : timeIndex;
            int endTime = timeIndex==null ? formatReader.getSizeT() -1 : timeIndex;
            short[][] shorts = new short[formatReader.getRGBChannelCount()][formatReader.getSizeX()*formatReader.getSizeY()];

            checkImageDimensionsSize(formatReader.getSizeX(), formatReader.getSizeY(), formatReader.getSizeZ(),
                    formatReader.getEffectiveSizeC(),
                    formatReader.getSizeT());

            for (int tndx = startTime; tndx <= endTime; tndx++) {
                for (int zndx = 0; zndx < formatReader.getSizeZ(); zndx++) {
                    int resized = resize == null ? (domainInfo.getiSize().getX() * domainInfo.getiSize().getY()) : (resize.getX() * resize.getY());
                    short[] mergePixels = (bMergeChannels ? new short[resized] : null);
                    for (int cndx = 0; cndx < formatReader.getEffectiveSizeC(); cndx++) {
                        channelAnalysis(new BufferedImageReader(imageReader), tndx, zndx, cndx,
                                bMergeChannels, resize, domainInfo,
                                ushortImageCTZArr, mergePixels, tzIndex, shorts);
                    }

                    if(bMergeChannels) {
                        ushortImageCTZArr[0][tzIndex] =
                                new UShortImage(mergePixels,domainInfo.getOrigin(),domainInfo.getExtent(),
                                        (resize==null?domainInfo.getiSize().getX():resize.getX()),
                                        (resize==null?domainInfo.getiSize().getY():resize.getY()),
                                        1);
                    }
                    tzIndex++;
                }
            }

            int numZ = Math.max(1,formatReader.getSizeZ());
            Time[] times = getTimes(imageReader);
            if(timeIndex != null){
                times = new Time[] {times[timeIndex]};
            }
            ImageDataset[] imageDataset = new ImageDataset[ushortImageCTZArr.length];
            double[] times_double = new double[times.length];
            for (int i = 0; i < times.length; i++) {
                times_double[i] = times[i].value().doubleValue();
            }
            for (int c = 0; c < imageDataset.length; c++) {
                ArrayList<UShortImage> images = new ArrayList<UShortImage>();
                for (UShortImage s : ushortImageCTZArr[c]){
                    images.add(s);
                }
                imageDataset[c] = new ImageDataset(ushortImageCTZArr[c],times_double,numZ);
            }
            return imageDataset;
        }finally{
            // after each image, release the buffers created
            if(formatReader != null){
                formatReader.close();
            }
        }
    }


//	  /** Outputs timing details per timepoint. */
//	  public static void printTimingPerTimepoint(IMetadata meta, int series) {
//	    System.out.println();
//	    System.out.println(
//	      "Timing information per timepoint (from beginning of experiment):");
//	    int planeCount = meta.getPlaneCount(series);
//	    for (int i = 0; i < planeCount; i++) {
//	      Double deltaT = meta.getPlaneDeltaT(series, i);
//	      if (deltaT == null) continue;
//	      // convert plane ZCT coordinates into image plane index
//	      int z = meta.getPlaneTheZ(series, i).getValue().intValue();
//	      int c = meta.getPlaneTheC(series, i).getValue().intValue();
//	      int t = meta.getPlaneTheT(series, i).getValue().intValue();
//	      if (z == 0 && c == 0) {
//	        System.out.println("\tTimepoint #" + t + " = " + deltaT + " s");
//	      }
//	    }
//	  }

    private void channelAnalysis(IFormatReader formatReader, int tndx, int zndx, int cndx,
                                 boolean bMergeChannels, ISize resize, DomainInfo domainInfo,
                                 UShortImage[][] ushortImageCTZArr, short[] mergePixels, int tzIndex,
                                 short[][] shorts) throws Exception {
        // Channel variables

            logger.info("T={} Z={} C={}", tndx, zndx, cndx);

            int index = formatReader.getIndex(zndx, cndx, tndx);

            byte[] bytes = formatReader.openBytes(index);
            ByteBuffer bb = ByteBuffer.wrap(formatReader.openBytes(index));
            bb.order((formatReader.isLittleEndian()?ByteOrder.LITTLE_ENDIAN:ByteOrder.BIG_ENDIAN));

            boolean bInterleave = formatReader.isInterleaved();
            int indexer = 0;
            // Channel RGB
            for (int channels = 0; channels < formatReader.getRGBChannelCount(); channels++) {
                short processed = 0;
                for (int i = 0; i < shorts[channels].length; i++) {

                    if(formatReader.getBitsPerPixel() == 8) {
                        processed = (short)(0x00FF&bb.get());
                    }else if(formatReader.getBitsPerPixel() == 16) {
                        processed = bb.getShort();
                    }else if(formatReader.getPixelType() == FormatTools.UINT32) {
                        processed = (short)(bb.getInt());
                    }else if(formatReader.getPixelType() == FormatTools.FLOAT) {
                        processed = (short)(bb.getFloat()*65535);
                    }else if(formatReader.getPixelType() == FormatTools.DOUBLE) {
                        processed = (short)(bb.getDouble()*65535);
                    }else {
                        throw new Exception("Expecting bitsPerPixel to be 8 or 16 but got "+formatReader.getBitsPerPixel());
                    }
                    int rgbChannelIndex = (bInterleave?indexer%formatReader.getRGBChannelCount():channels);
                    int xyIndex = (bInterleave?(int)(indexer/formatReader.getRGBChannelCount()):i);
                    shorts[rgbChannelIndex][xyIndex] = processed;
//								System.out.println((indexer%formatReader.getRGBChannelCount())+" "+((int)(indexer/formatReader.getRGBChannelCount()))+" "+processed);
                    indexer++;
                }
            }

            // Channel RGB
        for (int channels = 0; channels < formatReader.getRGBChannelCount(); channels++) {
                BufferedImage[] bufImgChannels = new BufferedImage[formatReader.getRGBChannelCount()];
                bufImgChannels[channels] = AWTImageTools.makeImage(shorts[channels], formatReader.getSizeX(), formatReader.getSizeY(), false);
                if(resize != null){
                    double scaleFactor = (double)resize.getX()/(double)formatReader.getSizeX();
                    AffineTransform scaleAffineTransform = AffineTransform.getScaleInstance(scaleFactor,scaleFactor);
                    AffineTransformOp scaleAffineTransformOp = new AffineTransformOp( scaleAffineTransform, (bufImgChannels[0].getColorModel() instanceof IndexColorModel?AffineTransformOp.TYPE_NEAREST_NEIGHBOR:AffineTransformOp.TYPE_BILINEAR));
                    BufferedImage scaledImage = new BufferedImage(resize.getX(),resize.getY(),bufImgChannels[channels].getType());
                    bufImgChannels[channels] = scaleAffineTransformOp.filter( bufImgChannels[channels], scaledImage);
                }
                if(!bMergeChannels) {
                    ushortImageCTZArr[channels + cndx][tzIndex] =
                            new UShortImage(AWTImageTools.getShorts(bufImgChannels[channels])[0],domainInfo.getOrigin(),domainInfo.getExtent(),
                                    (resize==null?domainInfo.getiSize().getX():resize.getX()),
                                    (resize==null?domainInfo.getiSize().getY():resize.getY()),
                                    1);
                }else {
                    short[] chanshorts = AWTImageTools.getShorts(bufImgChannels[channels])[0];
                    for (int i = 0; i < mergePixels.length; i++) {
                        mergePixels[i] = (short)Math.max(mergePixels[i], chanshorts[i]);

                    }
                }
            }
    }

    private Time[] getTimes(ImageReader imageReader){
        MetadataRetrieve meta = (MetadataRetrieve)imageReader.getMetadataStore();
        Time[] timeFArr = new Time[imageReader.getSizeT()];
        int planeCount = meta.getPlaneCount(0);
        Unit<Time> unit_time = ome.units.UNITS.SECOND;
        //Read raw times
        for (int i = 0; i < planeCount; i++) {
            Time deltaT = null;
            Time planeDeltaT = meta.getPlaneDeltaT(0, i);
            if (planeDeltaT == null){
                deltaT = new Time(0, unit_time);
            }else{
                deltaT = new Time(planeDeltaT.value(unit_time), unit_time);
            }

////			Float timeF = meta.getPlaneTimingDeltaT(0, 0, i);
//			// convert plane ZCT coordinates into image plane index
            int z = meta.getPlaneTheZ(0, i).getValue().intValue();//0;//(meta.getPlaneTheZ(0, 0, i) == null?0:meta.getPlaneTheZ(0, 0, i).intValue());
            int c = meta.getPlaneTheC(0, i).getValue().intValue();//0;//(meta.getPlaneTheC(0, 0, i) == null?0:meta.getPlaneTheC(0, 0, i).intValue());
            int t = meta.getPlaneTheT(0, i).getValue().intValue();//i;//(meta.getPlaneTheT(0, 0, i) == null?0:meta.getPlaneTheT(0, 0, i).intValue());
            if (z == 0 && c == 0) {
                timeFArr[t] = deltaT;
            }

//			int index = imageReader.getIndex(z, c, t);
//			Double timeF = meta.getPlaneDeltaT(0, i);
//			timeFArr[t] = timeF;
////			System.out.println("times[" + index + "] = " + timeFArr[index]);
        }
        //Subtract time zero
        Time[] times = new Time[timeFArr.length];
        for (int i = 0; i < times.length; i++) {
            if(timeFArr[i] == null){
                if((i==times.length-1) && (times.length > 2)){
                    timeFArr[i] = new Time(timeFArr[i-1].value().doubleValue() + timeFArr[i-1].value().doubleValue() - timeFArr[i-2].value().doubleValue(),unit_time);
                }else{
                    times = null;
                    break;
                }
            }
            times[i] = new Time(timeFArr[i].value().doubleValue()-timeFArr[0].value().doubleValue(),unit_time);
        }
        //If can't read properly then fill in with integers
        if(times == null){
            times = new Time[imageReader.getSizeT()];
            for (int i = 0; i < times.length; i++) {
                times[i] = new Time(i,unit_time);
            }
        }
        return times;
    }

    public ImageDataset readImageDatasetFromMultiFiles(File[] files, boolean isTimeSeries, double timeInterval) throws Exception
    {
        int numImages = files.length;
        UShortImage[] images = new UShortImage[numImages];


        // Added Feb, 2008. The varaibles added below are used for calculating the time used.
        //we want to update the loading progress every 2 seconds.
        long start = System.currentTimeMillis();
        long end;
        //Added Feb, 2008. Calculate the progress only when loading data to Virtual Microscopy
        int imageCount = 0;
        for (int i = 0; i < numImages; i++)
        {
            ImageReader imageReader = new ImageReader();
            MetadataStore store = MetadataTools.createOMEXMLMetadata();
            MetadataRetrieve meta = (MetadataRetrieve) store;
            store.createRoot();
            imageReader.setMetadataStore(store);
//		    FormatReader.debug = true;
            String imageID = files[i].getAbsolutePath();
            imageReader.setId(imageID);
            IFormatReader formatReader = imageReader.getReader(imageID);
            formatReader.setId(imageID);

            try{
                BufferedImage origBufferedImage = BufferedImageReader.makeBufferedImageReader(formatReader).openImage(0);//only one image each loop
                short[][] pixels = AWTImageTools.getShorts(origBufferedImage);
                int minValue = ((int)pixels[0][0])&0xffff;
                int maxValue = ((int)pixels[0][0])&0xffff;
                for (int j = 0; j < pixels[0].length; j++) {
                    minValue = Math.min(minValue,0xffff&((int)pixels[0][i]));
                    maxValue = Math.max(maxValue,0xffff&((int)pixels[0][i]));
                }
                Length physSizeX = meta.getPixelsPhysicalSizeX(0);
                Length physSizeY = meta.getPixelsPhysicalSizeX(1);
                Length physSizeZ = meta.getPixelsPhysicalSizeX(2);
                Float pixelSizeX_m = (physSizeX==null?null:um(physSizeX).floatValue());
                Float pixelSizeY_m = (physSizeY==null?null:um(physSizeY).floatValue());
                Float pixelSizeZ_m = (physSizeZ==null?null:um(physSizeZ).floatValue());
                if (pixelSizeX_m==null || pixelSizeX_m==0f){
                    pixelSizeX_m = 0.3e-6f;
                }
                if (pixelSizeY_m==null || pixelSizeY_m==0f){
                    pixelSizeY_m = 0.3e-6f;
                }
                if (pixelSizeZ_m==null || pixelSizeZ_m==0f || pixelSizeZ_m==1f){
                    pixelSizeZ_m = 0.9e-6f;
                }
                int sizeX = meta.getPixelsSizeX(0).getValue();
                int sizeY = meta.getPixelsSizeY(0).getValue();
                int sizeZ = meta.getPixelsSizeZ(0).getValue();

                if (sizeZ > 1){
//					throw new RuntimeException("3D images not yet supported");
                }
                Extent extent = null;
                if (pixelSizeX_m!=null && pixelSizeY_m!=null && pixelSizeZ_m!=null && pixelSizeX_m>0 && pixelSizeY_m>0 && pixelSizeZ_m>0){
//					extent = new Extent(pixelSizeX_m*sizeX*1e6,pixelSizeY_m*sizeY*1e6,pixelSizeZ_m*sizeZ*1e6);
                    extent = new Extent(pixelSizeX_m*sizeX,pixelSizeY_m*sizeY,pixelSizeZ_m*sizeZ);
                }
//				ArrayList<Short> pixelList = new ArrayList<Short>();
//				for (short s : pixels[0]){
//					pixelList.add(new Short(s));
//				}
                images[i] = new UShortImage(pixels[0],new Origin(0,0,0),extent,sizeX,sizeY,sizeZ);//new UShortImage(pixelList,new ISize(sizeX,sizeY,1),extent,new Origin(0,0,0));
                imageCount ++;
                //added Jan 2008, calculate the progress only when loading data to Virtual Microscopy
                // added Jan 2008, calculate the progress only when loading data to Virtual Microscopy

                for (int j=0; j<formatReader.getSeriesCount(); j++) {
                    formatReader.setSeries(j);
                }

            }finally{
                formatReader.close();
            }// end of try
        }// end of for loop

        // Read in the time stamps for individual time series images from formatReader.
        double[] times = null;
        int numZ = 1;
        if(isTimeSeries)
        {
            times = new double[numImages];
            for (int i = 0; i < times.length; i++) {
                times[i] = i * timeInterval ;
            }
        }
        else
        {
            numZ = numImages;
        }
//		List<UShortImage> imageList = Arrays.asList(images);
//		ArrayList<Double> timeList = new ArrayList<Double>();
//		for (double t : times){
//			timeList.add(t);
//		}
        ImageDataset imageDataset = new ImageDataset(images,times,numZ);//new ImageDataset(imageList,timeList,numZ);
        return imageDataset;
    }

    private static Number um(final Length length) {
        if(length != null){
            return length.value();
        }
        return null;
    }

    private ImageDataset createZStack(ImageDataset[] argImageDatasets) {
        // Error checking
        if (argImageDatasets.length == 0) {
            throw new RuntimeException("Cannot perform FRAP analysis on null image.");
        }

        int tempNumX = argImageDatasets[0].getAllImages()[0].getNumX();
        int tempNumY = argImageDatasets[0].getAllImages()[0].getNumY();
        int tempNumZ = argImageDatasets[0].getSizeZ();
        int tempNumC = 1;
        int tempNumT = argImageDatasets[0].getImageTimeStamps().length;
        if (tempNumZ!=1 || tempNumC!=1 || tempNumT!=1){
            throw new RuntimeException("each ImageDataset in z-stack must be 2D, single channel, and one time");
        }
        UShortImage[] ushortImages = new UShortImage[argImageDatasets.length];
        ushortImages[0] = argImageDatasets[0].getAllImages()[0];
        for (int i = 1; i < argImageDatasets.length; i++) {
            UShortImage img = argImageDatasets[i].getAllImages()[0];
            if (img.getNumX()!=tempNumX || img.getNumY()!=tempNumY || img.getNumZ()!=tempNumZ){
                throw new RuntimeException("All ImageDataset sub-images must be same dimension xyz img(0) "+tempNumX+","+tempNumY+","+tempNumZ+" not equal img("+i+") "+img.getNumX()+","+img.getNumY()+","+img.getNumZ());
            }
            ushortImages[i] = img;
            ushortImages[i].setExtent(new Extent(img.getExtent().getX(),img.getExtent().getY(),img.getExtent().getZ()*argImageDatasets.length));
        }
        return new ImageDataset(ushortImages,argImageDatasets[0].getImageTimeStamps(),ushortImages.length);
    }

    public static void checkImageDimensionsSize(int x, int y, int z, int t, int c){
        int megaBytes = 1024 * 1024;

        // 2 Bytes per pixel
        if ((x * y * z * t * c) * 2 * 8 > 500 * megaBytes){
            String error = String.format("The dimensions of the image you provided are to large, and the resulting field data will take over 500MB. " +
                    "Please provide an image with less time points, channels, or with cropped X,Y,Z. x: %d, y: %d, z: %d, t: %d, c: %d", x, y, z, t, c);
            throw new IllegalArgumentException(error);
        }
    }

    private void printInfo(ImageReader imageReader, String imageID, IFormatReader formatReader){
        MetadataRetrieve meta = (MetadataRetrieve)imageReader.getMetadataStore();
        System.out.println("from Metadata Store("+
                meta.getPixelsSizeX(0).getValue()+","+
                meta.getPixelsSizeY(0).getValue()+","+
                meta.getPixelsSizeZ(0).getValue()+","+
                meta.getPixelsSizeC(0).getValue()+","+
                meta.getPixelsSizeT(0).getValue()+")");

        int ii = 0;
        System.out.println("creationDate: "+meta.getImageAcquisitionDate(ii));
        System.out.println("description: "+meta.getImageDescription(ii));
        System.out.println("dimension order: "+meta.getPixelsDimensionOrder(ii));
        System.out.println("image name: "+meta.getImageName(ii));
        System.out.println("pixel type: "+meta.getPixelsType(ii));
        try{
            System.out.println("stage name: "+meta.getStageLabelName(ii));
            System.out.println("stage X: "+meta.getStageLabelX(ii));
            System.out.println("stage Y: "+meta.getStageLabelY(ii));
            System.out.println("stage Z: "+meta.getStageLabelZ(ii));
        }catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
        System.out.println("big endian: "+meta.getPixelsBinDataBigEndian(ii, ii));
        System.out.println("pixel size X: "+meta.getPixelsPhysicalSizeX(ii));
        System.out.println("pixel size Y: "+meta.getPixelsPhysicalSizeY(ii));
        System.out.println("pixel size Z: "+meta.getPixelsPhysicalSizeZ(ii));
        if (meta.getPixelsPhysicalSizeX(0)!=null){
            System.out.println("   image Size X: "+(meta.getPixelsSizeX(ii).getValue()*um(meta.getPixelsPhysicalSizeX(0)).doubleValue())+" microns");
        }
        if (meta.getPixelsPhysicalSizeY(0)!=null){
            System.out.println("   image Size Y: "+(meta.getPixelsSizeY(ii).getValue()*um(meta.getPixelsPhysicalSizeY(0)).doubleValue())+" microns");
        }
        System.out.println("size X: "+meta.getPixelsSizeX(ii).getValue());
        System.out.println("size Y: "+meta.getPixelsSizeY(ii).getValue());
        System.out.println("size Z: "+meta.getPixelsSizeZ(ii).getValue());
        System.out.println("size C: "+meta.getPixelsSizeC(ii).getValue());
        System.out.println("size T: "+meta.getPixelsSizeT(ii).getValue());

        for (int i=0; i<imageReader.getSeriesCount(); i++) {
            imageReader.setSeries(i);

            System.out.println("image size from imageReader("+
                    imageReader.getSizeX()+","+
                    imageReader.getSizeY()+","+
                    imageReader.getSizeZ()+","+
                    imageReader.getSizeC()+","+
                    imageReader.getSizeT()+")");
            System.out.println("image size from Metadata Store("+
                    meta.getPixelsSizeX(i).getValue()+","+
                    meta.getPixelsSizeY(i).getValue()+","+
                    meta.getPixelsSizeZ(i).getValue()+","+
                    meta.getPixelsSizeC(i).getValue()+","+
                    meta.getPixelsSizeT(i).getValue()+")");

        }
        boolean bUnsigned = formatReader.getPixelType()==1 || formatReader.getPixelType()==3 || formatReader.getPixelType()==5;
        //BIOFormats Image API documentation
        //42 - image width (getSizeX())
        //43 - image height (getSizeY())
        //44 - number of series per file (getSeriesCount())
        //45 - total number of images per series (getImageCount())
        //46 - number of slices in the current series (getSizeZ())
        //47 - number of timepoints in the current series (getSizeT())
        //48 - number of actual channels in the current series (getSizeC())
        //49 - number of channels per image (getRGBChannelCount())
        //50 - the ordering of the images within the current series (getDimensionOrder())
        //51 - whether each image is RGB (isRGB())
        //52 - whether the pixel bytes are in little-endian order (isLittleEndian())
        //53 - whether the channels in an image are interleaved (isInterleaved())
        //54 - the type of pixel data in this file (getPixelType())
        System.out.println("image Info from imageReader("+
                "file="+imageID+","+
                "x="+formatReader.getSizeX()+","+
                "y="+formatReader.getSizeY()+","+
                "z="+formatReader.getSizeZ()+","+
                "c="+formatReader.getSizeC()+","+
                "effective c="+formatReader.getEffectiveSizeC()+","+//how to interpret rgbChannelCount
//				  /**
//				   * Gets the effective size of the C dimension, guaranteeing that
//				   * getEffectiveSizeC() * getSizeZ() * getSizeT() == getImageCount()
//				   * regardless of the result of isRGB().
//				   */
//				  int getEffectiveSizeC();
                "t="+formatReader.getSizeT()+","+
                "seriesCnt="+formatReader.getSeriesCount()+","+
                "imageCnt="+formatReader.getImageCount()+","+
                "isRGB="+formatReader.isRGB()+","+
                "RGBChannelCnt="+formatReader.getRGBChannelCount()+","+
                "dimOrder="+formatReader.getDimensionOrder()+","+
                "littleEndian="+formatReader.isLittleEndian()+","+
                "isInterleave="+formatReader.isInterleaved()+","+
                "pixelType="+formatReader.getPixelType()+" ("+FormatTools.getPixelTypeString(formatReader.getPixelType())+")"+
                "unsigedPixelType="+bUnsigned+")"+
                ")");
    }



}