package cbit.vcell.VirtualMicroscopy;


import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import loci.formats.AWTImageTools;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;

import org.vcell.util.Extent;
import org.vcell.util.Origin;

import cbit.image.ImageException;
import cbit.vcell.client.task.ClientTaskStatusSupport;

public class ImageDatasetReader {
	
	public static double[] getTimesOnly(String fileName) throws Exception{
		if(fileName.toUpperCase().endsWith(".ZIP")){
			return new double[] {0.0};
		}else{
			return getTimes(getImageReader(fileName));
		}
	}
	public static int getChannelCount(String fileName) throws Exception{
		if(fileName.toUpperCase().endsWith(".ZIP")){
			return readZipFile(fileName, false, false).length;
		}else{
			return getImageReader(fileName).getSizeC();
		}
	}
	private static ImageReader getImageReader(String imageID) throws FormatException,IOException{
		ImageReader imageReader = new ImageReader();
		// create OME-XML metadata store of the latest schema version
		MetadataStore store = MetadataTools.createOMEXMLMetadata();
		// or if you want a specific schema version, you can use:
		//MetadataStore store = MetadataTools.createOMEXMLMetadata(null, "2007-06");
//		MetadataRetrieve meta = (MetadataRetrieve) store;
		store.createRoot();
		imageReader.setMetadataStore(store);
		FormatReader.debug = true;
		imageReader.setId(imageID);
		return imageReader;

	}
	
	public static ImageDataset readImageDataset(String imageID, ClientTaskStatusSupport status) throws FormatException, IOException, ImageException {
		return ImageDatasetReader.readImageDatasetChannels(imageID, status,true)[0];
	}
	
	private static ImageDataset[] readZipFile(String imageID,boolean bAll,boolean bMergeChannels) throws IOException,ImageException,FormatException{
		ZipFile zipFile = new ZipFile(new File(imageID),ZipFile.OPEN_READ);
		Vector<Vector<ImageDataset>> imageDataForEachChannelV = new Vector<Vector<ImageDataset>>();
		Enumeration<? extends ZipEntry> enumZipEntry = zipFile.entries();
		int noOfZipEntry = 0;//added Jan 2008
		int numChannels = -1;
		//Sort entryNames because ZipFile doesn't guarantee order
		TreeMap<String, Integer> sortedChannelsTreeMap = new TreeMap<String, Integer>();
		while (enumZipEntry.hasMoreElements()){

			ZipEntry entry = enumZipEntry.nextElement();
			String entryName = entry.getName();
			String imageFileSuffix = null;
			int dotIndex = entryName.indexOf(".");
			if(dotIndex != -1){
				imageFileSuffix = entryName.substring(dotIndex);
			}
			InputStream zipInputStream = zipFile.getInputStream(entry);
			File tempImageFile = File.createTempFile("ImgDataSetReader", imageFileSuffix);
			tempImageFile.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempImageFile,false);
			byte[] buffer = new byte[50000];
			while (true){
				int bytesRead = zipInputStream.read(buffer);
				if (bytesRead==-1){
					break;
				}
				fos.write(buffer, 0, bytesRead);
			}
			fos.close();
			zipInputStream.close(); 
			ImageDataset[] imageDatasetChannels = readImageDatasetChannels(tempImageFile.getAbsolutePath(), null,bMergeChannels);
			if(numChannels == -1){
				numChannels = imageDatasetChannels.length;
				for (int i = 0; i < numChannels; i++) {
					imageDataForEachChannelV.add(new Vector<ImageDataset>());
				}
			}
			if(numChannels != imageDatasetChannels.length){
				throw new ImageException("ZipFile reader found images with different number of channels");
			}
			
			sortedChannelsTreeMap.put(entryName, imageDataForEachChannelV.elementAt(0).size());
			for (int i = 0; i < numChannels; i++) {
				imageDataForEachChannelV.elementAt(i).add(imageDatasetChannels[i]);
			}
			
			tempImageFile.delete();
			noOfZipEntry ++;
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
			completeImageDatasetChannels[i] = ImageDataset.createZStack(sortedChannel);			
		}
		return completeImageDatasetChannels;

	}
	public static ImageDataset[] readImageDatasetChannels(String imageID, ClientTaskStatusSupport status, boolean bMergeChannels) throws FormatException, IOException, ImageException {
		if (imageID.toUpperCase().endsWith(".ZIP")){
			return readZipFile(imageID, true, bMergeChannels);
		}


		ImageReader imageReader = ImageDatasetReader.getImageReader(imageID);
		MetadataRetrieve meta = (MetadataRetrieve)imageReader.getMetadataStore();
		IFormatReader formatReader = imageReader.getReader(imageID);
		formatReader.setId(imageID);
//		int seriesCount = formatReader.getSeriesCount();
//		System.out.println("formatReader.getSeriesCount() = "+formatReader.getSeriesCount());
		try{
//			System.out.println("before series, image size from imageReader("+
//					formatReader.getSizeX()+","+
//					formatReader.getSizeY()+","+
//					formatReader.getSizeZ()+","+
//					formatReader.getSizeC()+","+
//					formatReader.getSizeT()+")");
//			System.out.println("imagecount = "+formatReader.getImageCount());
			int numImages = formatReader.getImageCount();
			int desiredNumChannels = (bMergeChannels?1:formatReader.getSizeC());

			UShortImage[][] images = new UShortImage[desiredNumChannels][numImages];
			if(status != null){
				status.setProgress(0);
			}
			int sizeX = meta.getPixelsSizeX(0, 0);
			int sizeY = meta.getPixelsSizeY(0,0);
			int sizeZ = meta.getPixelsSizeZ(0,0);

			Float pixelSizeX_m = meta.getDimensionsPhysicalSizeX(0, 0);
			Float pixelSizeY_m = meta.getDimensionsPhysicalSizeY(0, 0);
			Float pixelSizeZ_m = meta.getDimensionsPhysicalSizeZ(0, 0);
			if (pixelSizeX_m==null || pixelSizeX_m==0f){
				pixelSizeX_m = 0.3e-6f;
			}
			if (pixelSizeY_m==null || pixelSizeY_m==0f){
				pixelSizeY_m = 0.3e-6f;
			}
			if (pixelSizeZ_m==null || pixelSizeZ_m==0f || pixelSizeZ_m==1f){
				pixelSizeZ_m = 0.9e-6f;
			}
			
			Extent extent = null;
			if (pixelSizeX_m!=null && pixelSizeY_m!=null && pixelSizeZ_m!=null && pixelSizeX_m>0 && pixelSizeY_m>0 && pixelSizeZ_m>0){
				extent = new Extent(pixelSizeX_m*sizeX,pixelSizeY_m*sizeY,pixelSizeZ_m*sizeZ);
			}

			Origin origin = new Origin(0,0,0);
//			int tzcIndex = 0;
			for (int i = 0; i < numImages; i++) {
				BufferedImage origBufferedImage = formatReader.openImage(i);
				formatReader.close(true);
//				System.out.println("original image is type "+AWTImageTools.getPixelType(origBufferedImage));
				short[][] pixels = null;
				if(bMergeChannels){
					BufferedImage mergedBufferedImage =
					AWTImageTools.makeBuffered(origBufferedImage, AWTImageTools.makeColorModel(1, DataBuffer.TYPE_BYTE));
					pixels = AWTImageTools.getShorts(mergedBufferedImage);
				}else{
					pixels = AWTImageTools.getShorts(origBufferedImage);
				}
				if(desiredNumChannels != pixels.length){
					throw new ImageException("bMergeChannels="+bMergeChannels+
							" AWTImageTools.getShorts() channels ="+pixels.length+" not match desiredNumChannels="+desiredNumChannels);
				}
//				int zct[] = formatReader.getZCTCoords(i);
//				int minValue = ((int)pixels[0][0])&0xffff;
//				int maxValue = ((int)pixels[0][0])&0xffff;
//				for (int j = 0; j < pixels[0].length; j++) {
//					minValue = Math.min(minValue,0xffff&((int)pixels[0][i]));
//					maxValue = Math.max(maxValue,0xffff&((int)pixels[0][i]));
//				}
//				Float pixelSizeX_m = meta.getDimensionsPhysicalSizeX(0, 0);
//				Float pixelSizeY_m = meta.getDimensionsPhysicalSizeY(0, 0);
//				Float pixelSizeZ_m = meta.getDimensionsPhysicalSizeZ(0, 0);
//				if (pixelSizeX_m==null || pixelSizeX_m==0f){
//					pixelSizeX_m = 0.3e-6f;
//				}
//				if (pixelSizeY_m==null || pixelSizeY_m==0f){
//					pixelSizeY_m = 0.3e-6f;
//				}
//				if (pixelSizeZ_m==null || pixelSizeZ_m==0f || pixelSizeZ_m==1f){
//					pixelSizeZ_m = 0.9e-6f;
//				}
//				
//				Extent extent = null;
//				if (pixelSizeX_m!=null && pixelSizeY_m!=null && pixelSizeZ_m!=null && pixelSizeX_m>0 && pixelSizeY_m>0 && pixelSizeZ_m>0){
//					extent = new Extent(pixelSizeX_m*sizeX,pixelSizeY_m*sizeY,pixelSizeZ_m*sizeZ);
//				}
//				System.out.println("reading image "+i+", z="+zct[0]+", channel="+zct[1]+", time="+zct[2]+", pixelType="+meta.getPixelsPixelType(0, 0)+", numSeries="+seriesCount+", size=("+((pixelSizeX_m!=null)?(pixelSizeX_m*1e6):"?")+","+((pixelSizeY_m!=null)?(pixelSizeY_m*1e6):"?")+","+((pixelSizeZ_m!=null)?(pixelSizeZ_m*1e6):"?")+") um, dim=("+sizeX+","+sizeY+","+sizeZ+"), value in ["+minValue+","+maxValue+"]");
				for (int c = 0; c < desiredNumChannels; c++) {
					images[c][i] = new UShortImage(pixels[c],origin,extent,sizeX,sizeY,1);
				}
				if(status != null){
					status.setProgress(((int)(i*100/numImages)));
				}
			}
			
			
//			System.out.println("before series, image size from Metadata Store("+
//					meta.getPixelsSizeX(0, 0)+","+
//					meta.getPixelsSizeY(0, 0)+","+
//					meta.getPixelsSizeZ(0, 0)+","+
//					meta.getPixelsSizeC(0, 0)+","+
//					meta.getPixelsSizeT(0, 0)+")");
			
//			Integer ii = new Integer(0);			
//			System.out.println("creationDate: "+meta.getImageCreationDate(ii));
//			System.out.println("description: "+meta.getImageDescription(ii));
//			System.out.println("dimension order: "+meta.getPixelsDimensionOrder(ii,ii));
//			System.out.println("image name: "+meta.getImageName(ii));
//			System.out.println("pixel type: "+meta.getPixelsPixelType(ii,ii));
//			System.out.println("stage name: "+meta.getStageLabelName(ii));
//			System.out.println("big endian: "+meta.getPixelsBigEndian(ii,ii));
//			System.out.println("pixel size X: "+meta.getDimensionsPhysicalSizeX(ii,ii));
//			System.out.println("pixel size Y: "+meta.getDimensionsPhysicalSizeY(ii,ii));
//			System.out.println("pixel size Z: "+meta.getDimensionsPhysicalSizeZ(ii,ii));
//			if (meta.getDimensionsPhysicalSizeX(0, 0)!=null){
//				System.out.println("   image Size X: "+(meta.getPixelsSizeX(ii,ii)*meta.getDimensionsPhysicalSizeX(0, 0))+" microns");
//			}
//			if (meta.getDimensionsPhysicalSizeY(0, 0)!=null){
//				System.out.println("   image Size Y: "+(meta.getPixelsSizeY(ii,ii)*meta.getDimensionsPhysicalSizeY(0, 0))+" microns");
//			}
//			System.out.println("size X: "+meta.getPixelsSizeX(ii,ii));
//			System.out.println("size Y: "+meta.getPixelsSizeY(ii,ii));
//			System.out.println("size Z: "+meta.getPixelsSizeZ(ii,ii));
//			System.out.println("size C: "+meta.getPixelsSizeC(ii,ii));
//			System.out.println("size T: "+meta.getPixelsSizeT(ii,ii));
//			System.out.println("stage X: "+meta.getStageLabelX(ii));
//			System.out.println("stage Y: "+meta.getStageLabelY(ii));
//			System.out.println("stage Z: "+meta.getStageLabelZ(ii));
			
//			for (int i=0; i<formatReader.getSeriesCount(); i++) {
//				formatReader.setSeries(i);
//	
//				System.out.println("image size from imageReader("+
//						formatReader.getSizeX()+","+
//						formatReader.getSizeY()+","+
//						formatReader.getSizeZ()+","+
//						formatReader.getSizeC()+","+
//						formatReader.getSizeT()+")");
//				System.out.println("image size from Metadata Store("+
//						meta.getPixelsSizeX(i,0)+","+
//						meta.getPixelsSizeY(i,0)+","+
//						meta.getPixelsSizeZ(i,0)+","+
//						meta.getPixelsSizeC(i,0)+","+
//						meta.getPixelsSizeT(i,0)+")");
//				
//			}

			double[] times = getTimes(imageReader);
			   
			int numZ = Math.max(1,formatReader.getSizeZ());
			
			ImageDataset[] imageDataset = new ImageDataset[desiredNumChannels];
			for (int c = 0; c < desiredNumChannels; c++) {
				imageDataset[c] = new ImageDataset(images[c],times,numZ);
			}
			if(status != null){
				status.setProgress(100);
			}
			return imageDataset;
		}finally{
			if(formatReader != null){
				formatReader.close();
			}
		}
	}
	
	
	private static double[] getTimes(ImageReader imageReader){
		MetadataRetrieve meta = (MetadataRetrieve)imageReader.getMetadataStore();
		Float[] timeFArr = new Float[imageReader.getSizeT()];
		//Read raw times
		for (int i = 0; i < timeFArr.length; i++) {
			Float timeF = meta.getPlaneTimingDeltaT(0, 0, i);
			// convert plane ZCT coordinates into image plane index
			int z = (meta.getPlaneTheZ(0, 0, i) == null?0:meta.getPlaneTheZ(0, 0, i).intValue());
			int c = (meta.getPlaneTheC(0, 0, i) == null?0:meta.getPlaneTheC(0, 0, i).intValue());
			int t = (meta.getPlaneTheT(0, 0, i) == null?0:meta.getPlaneTheT(0, 0, i).intValue());
			int index = imageReader.getIndex(z, c, t);
			timeFArr[index] = timeF;
//			System.out.println("times[" + index + "] = " + timeFArr[index]);
		}
		//Subtract time zero
		double[] times = new double[timeFArr.length];
		for (int i = 0; i < times.length; i++) {
		   if(timeFArr[i] == null){
			   if((i==times.length-1) && (times.length > 2)){
				   timeFArr[i] = new Float(timeFArr[i-1]+timeFArr[i-1]-timeFArr[i-2]);
			   }else{
				   times = null;
				   break;
			   }
		   }
		   times[i] = timeFArr[i]-timeFArr[0];
		}
		//If can't read properly then fill in with integers
		if(times == null){
			times = new double[imageReader.getSizeT()];
			for (int i = 0; i < times.length; i++) {
				times[i] = i;
			}
		}
		return times;
	}

	public static ImageDataset readImageDatasetFromMultiFiles(File[] files, ClientTaskStatusSupport status, boolean isTimeSeries, double timeInterval) throws FormatException, IOException, ImageException 
	{
		int numImages = files.length;
		UShortImage[] images = new UShortImage[numImages];
		
		
		// Added Feb, 2008. The varaibles added below are used for calculating the time used.
		//we want to update the loading progress every 2 seconds.
		long start = System.currentTimeMillis();
		long end;
		//Added Feb, 2008. Calculate the progress only when loading data to Virtual Microscopy
		if(status != null)
		{
			//start the progress display
			status.setProgress(0);
		}
		int imageCount = 0;
		for (int i = 0; i < numImages; i++) 
		{		
			ImageReader imageReader = new ImageReader();
			MetadataStore store = MetadataTools.createOMEXMLMetadata();
			MetadataRetrieve meta = (MetadataRetrieve) store;
		    store.createRoot();
		    imageReader.setMetadataStore(store);
		    FormatReader.debug = true;
		    String imageID = files[i].getAbsolutePath();
		    imageReader.setId(imageID);
			IFormatReader formatReader = imageReader.getReader(imageID);
			formatReader.setId(imageID);
			
			try{
				BufferedImage origBufferedImage = formatReader.openImage(0);//only one image each loop
				short[][] pixels = AWTImageTools.getShorts(origBufferedImage);
				int minValue = ((int)pixels[0][0])&0xffff;
				int maxValue = ((int)pixels[0][0])&0xffff;
				for (int j = 0; j < pixels[0].length; j++) {
					minValue = Math.min(minValue,0xffff&((int)pixels[0][i]));
					maxValue = Math.max(maxValue,0xffff&((int)pixels[0][i]));
				}
				Float pixelSizeX_m = meta.getDimensionsPhysicalSizeX(0, 0);
				Float pixelSizeY_m = meta.getDimensionsPhysicalSizeY(0, 0);
				Float pixelSizeZ_m = meta.getDimensionsPhysicalSizeZ(0, 0);
				if (pixelSizeX_m==null || pixelSizeX_m==0f){
					pixelSizeX_m = 0.3e-6f;
				}
				if (pixelSizeY_m==null || pixelSizeY_m==0f){
					pixelSizeY_m = 0.3e-6f;
				}
				if (pixelSizeZ_m==null || pixelSizeZ_m==0f || pixelSizeZ_m==1f){
					pixelSizeZ_m = 0.9e-6f;
				}
				int sizeX = meta.getPixelsSizeX(0, 0);
				int sizeY = meta.getPixelsSizeY(0,0);
				int sizeZ = meta.getPixelsSizeZ(0,0);
				
				if (sizeZ > 1){
//					throw new RuntimeException("3D images not yet supported");
				}
				Extent extent = null;
				if (pixelSizeX_m!=null && pixelSizeY_m!=null && pixelSizeZ_m!=null && pixelSizeX_m>0 && pixelSizeY_m>0 && pixelSizeZ_m>0){
//					extent = new Extent(pixelSizeX_m*sizeX*1e6,pixelSizeY_m*sizeY*1e6,pixelSizeZ_m*sizeZ*1e6);
					extent = new Extent(pixelSizeX_m*sizeX,pixelSizeY_m*sizeY,pixelSizeZ_m*sizeZ);
				}
				
				images[i] = new UShortImage(pixels[0],new Origin(0,0,0),extent,sizeX,sizeY,1);
				imageCount ++;
				//added Jan 2008, calculate the progress only when loading data to Virtual Microscopy
				if(status != null)
				{
					end = System.currentTimeMillis();
					if((end - start) >= 2000)
					{
						status.setProgress(((int)(i*100/numImages)));
						start = end; 
					}
				}
				// added Jan 2008, calculate the progress only when loading data to Virtual Microscopy
				if(status != null && imageCount == numImages)
				{
					//the progress display
					status.setProgress(100);
				}
				
//				Integer ii = new Integer(0);			
//				if (store.getDimensionsPhysicalSizeX(0, 0)!=null){
//					System.out.println("   image Size X: "+(store.getPixelsSizeX(ii,ii)*store.getDimensionsPhysicalSizeX(0, 0)*1e6)+" microns");
//				}
//
//				if (store.getDimensionsPhysicalSizeY(0, 0)!=null){
//					System.out.println("   image Size Y: "+(store.getPixelsSizeY(ii,ii)*store.getDimensionsPhysicalSizeY(0, 0)*1e6)+" microns");
//				}
				
				for (int j=0; j<formatReader.getSeriesCount(); j++) {
					formatReader.setSeries(j);
				}

			}finally{
				if(formatReader != null){
					formatReader.close();
				}
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
		
		ImageDataset imageDataset = new ImageDataset(images,times,numZ);
		return imageDataset;
	}
}
