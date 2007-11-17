package cbit.vcell.VirtualMicroscopy;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFileChooser;

import cbit.image.ImageException;
import cbit.util.Extent;
import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.ImageTools;
import loci.formats.OMEXMLMetadataStore;

public class ImageDatasetReader {
//	public static void main(String[] args) throws Exception{
//		
//		File selectedFile = null;
//		JFileChooser jfc = new JFileChooser("C:\\Temp\\vcell_data.mdb\\");
//		int result = jfc.showOpenDialog(null);
//		if(result == JFileChooser.APPROVE_OPTION){
//			selectedFile = jfc.getSelectedFile();
//			simpleReadImageDataset(selectedFile.getAbsolutePath());
//		}
////		try {
////			PropertyLoader.loadProperties();
////			final int NumArgTypes = 5;
////			final int DataFileIndex = 0;
////			final int BleachMaskIndex = 1;
////			final int CellMaskIndex = 2;
////			final int BackgroundMaskIndex = 3;
////			final int XMLFileIndex = 4;
////			final String[] argKeys = { "-dataset", "-bleachmask", "-cellmask", "-background", "-xml" };
////			String[] argValues = new String[NumArgTypes];
////			for (int i = 0; i < args.length; i+=2) {
////				boolean bProcessedArgument = false;
////				for (int j = 0; j < NumArgTypes; j++) {
////					if (args[i].equalsIgnoreCase(argKeys[j])){
////						if (i<args.length-1){
////							argValues[j] = args[i+1];
////							bProcessedArgument = true;
////							break;
////						}else{
////							throw new Exception("expected value for "+argKeys[i]);
////						}
////					}
////				}
////				if (!bProcessedArgument){
////					throw new Exception("unexpected argument "+args[i]);
////				}
////			}
////			FRAPStudy frapStudy = null;
////			String workspaceDir = null;
////			String originalImageFilePath = null;
////			String xmlFileName = argValues[XMLFileIndex];
////			if (xmlFileName!=null){
////				workspaceDir = (new File(xmlFileName)).getParent();
////				XmlReader xmlReader = new XmlReader(true);
////				frapStudy = xmlReader.getFrapStudy(XmlUtil.stringToXML(XmlUtil.getXMLString(xmlFileName),null));
////				frapStudy.setXmlFilename(xmlFileName);
////			}else{
////				ImageReader imageReader = new ImageReader();
////				String imageID = argValues[DataFileIndex];
////				if (imageID==null){
////					JFileChooser box = imageReader.getFileChooser();
////					int rval = box.showOpenDialog(null);
////					if (rval == JFileChooser.APPROVE_OPTION){
////						File file = box.getSelectedFile();
////						if (file != null){
////							workspaceDir = file.getParent();
////							originalImageFilePath = file.getAbsolutePath();
////							imageID = file.getPath();
////						}
////					}
////				}else{
////					File file = new File(imageID);
////					workspaceDir = file.getParent();
////					originalImageFilePath = file.getAbsolutePath();
////				}
////				ImageDataset imageDataset = readImageDataset(imageID);
////				double[] timeArray = imageDataset.getImageTimeStamps();
////				Extent extent = imageDataset.getImages()[0].getExtent();
////				int numX = imageDataset.getImages()[0].getNumX();
////				int numY = imageDataset.getImages()[0].getNumY();
////				int numZ = imageDataset.getSizeZ();
////				UShortImage cellRoiImage = null;
////				if (argValues[CellMaskIndex]!=null){
////					ImageDataset roiImageDataset = VirtualFrapTest.readImageDataset(argValues[CellMaskIndex]);
////					cellRoiImage = roiImageDataset.getImages()[0];
////				}else{
////					cellRoiImage = new UShortImage(new short[numX*numY*numZ],extent,numX,numY,numZ);
////				}
////				UShortImage bleachedRoiImage = null;
////				if (argValues[BleachMaskIndex]!=null){
////					ImageDataset roiImageDataset = VirtualFrapTest.readImageDataset(argValues[BleachMaskIndex]);
////					bleachedRoiImage = roiImageDataset.getImages()[0];
////				}else{
////					bleachedRoiImage = new UShortImage(new short[numX*numY*numZ],extent,numX,numY,numZ);
////				}
////				UShortImage backgroundRoiImage = null;
////				if (argValues[BackgroundMaskIndex]!=null){
////					ImageDataset roiImageDataset = VirtualFrapTest.readImageDataset(argValues[BackgroundMaskIndex]);
////					backgroundRoiImage = roiImageDataset.getImages()[0];
////				}else{
////					backgroundRoiImage = new UShortImage(new short[numX*numY*numZ],extent,numX,numY,numZ);
////				}
////				FRAPData frapData = new FRAPData(imageDataset, new ROI[] { 
////						new ROI(bleachedRoiImage,RoiType.ROI_BLEACHED),
////						new ROI(cellRoiImage,RoiType.ROI_CELL),
////						new ROI(backgroundRoiImage,RoiType.ROI_BACKGROUND),
////						});
////				frapStudy = new FRAPStudy();
////				frapStudy.setFrapData(frapData);
////				frapStudy.setOriginalImageFilePath(originalImageFilePath);
////			}
////			JFrame frame = new JFrame();
////			frame.addWindowListener(new WindowAdapter(){
////				public void windowClosing(java.awt.event.WindowEvent e) {
////					System.exit(0);
////				};
////			});
////			FRAPStudyPanel frapStudyPanel = new FRAPStudyPanel();
////			User owner = new User("schaff",new KeyValue("17"));
////			String simDataDir = PropertyLoader.getRequiredProperty(PropertyLoader.localSimDataDirProperty);
////			frapStudyPanel.setLocalWorkspace(new LocalWorkspace(workspaceDir,owner,simDataDir));
////			frapStudyPanel.setFrapStudy(frapStudy);
////			frame.add(frapStudyPanel);
////			frame.setSize(640,480);
////			frame.setVisible(true);
////
////		} catch (Exception e) {
////			e.printStackTrace(System.out);
////		}
//
//	}
//	private static byte[][][][] simpleReadImageDataset(String imageID) throws FormatException, IOException, ImageException {
//		
//		byte[][][][] ctzxy = null;
//		ImageReader imageReader = null;
//		try{
//			imageReader = new ImageReader();
////			Object mds = imageReader.getMetadataStoreRoot(imageID);
////			Object tt = mds;		
////			if(true){return null;}
//			
//	//		- image width (getSizeX(String))
//			imageReader.getSizeX(imageID);
//	//		- image height (getSizeY(String))
//			imageReader.getSizeY(imageID);
//	//		- total number of images per file (getImageCount(String))
//			imageReader.getImageCount(imageID);
//	//		- number of slices per file (getSizeZ(String))
//			imageReader.getSizeZ(imageID);
//	//		- number of timepoints per file (getSizeT(String))
//			imageReader.getSizeT(imageID);
//	//		- number of actual channels per file (getSizeC(String))
//			imageReader.getSizeC(imageID);
//	//		- number of channels per image (getRGBChannelCount(String))
//			imageReader.getRGBChannelCount(imageID);
//	//		- the ordering of the images within the file (getDimensionOrder(String))
//			imageReader.getDimensionOrder(imageID);
//	//		- whether each image is RGB (isRGB(String))
//			imageReader.isRGB(imageID);
//	//		- whether the pixel bytes in little-endian order (isLittleEndian(String))
//			imageReader.isLittleEndian(imageID);
//	//		- whether the channels in an image are interleaved (isInterleaved(String))
//			imageReader.isInterleaved(imageID);
//	//		- the type of pixel data in this file (getPixelType(String))
//			imageReader.getPixelType(imageID);
//			
//			// Read in the time stamps for individual time series images from formatReader.
//			double[] times = null;
//			Double firstTimeStamp = (Double)imageReader.getMetadataValue(imageID, "TimeStamp0");
//			if (firstTimeStamp != null) {
//				times = new double[imageReader.getSizeT(imageID)];
//				double firstTimeStampVal = firstTimeStamp.doubleValue();
//				for (int i = 0; i < times.length; i++) {
//					Double timeStamp = (Double)imageReader.getMetadataValue(imageID, "TimeStamp"+i);
//					times[i] = timeStamp.doubleValue() - firstTimeStampVal;
//				}
//			} else{
//				System.out.println(" Specified image file format does not have time stamp values.");
//			}
//
//			
//			
//			ChannelSeparator channelSeparator = new ChannelSeparator(imageReader);//XYC
//			int zSize = imageReader.getSizeZ(imageID);
//			int tSize = imageReader.getSizeT(imageID);
//			int cSize = imageReader.getSizeC(imageID);
//			int numChannelImages = cSize*tSize*zSize;
//			int width = imageReader.getSizeX(imageID);
//			int height = imageReader.getSizeY(imageID);
//			boolean isLittleEndian = imageReader.isLittleEndian(imageID);
//			String dimensionOrder = channelSeparator.getDimensionOrder(imageID);
//			boolean isZBeforeT = dimensionOrder.indexOf('Z') < dimensionOrder.indexOf('T');
//			int pixelType = imageReader.getPixelType(imageID);
//			ctzxy = new byte[cSize][tSize][zSize][];
//			if(pixelType == FormatReader.INT8 || pixelType == FormatReader.UINT8){
//				int cIndex = 0;
//				int zIndex = 0;
//				int tIndex = 0;
//				for(int i=0;i<numChannelImages;i+= 1){
//					ctzxy[cIndex][tIndex][zIndex] = channelSeparator.openBytes(imageID, i);
//					cIndex+= 1;if(cIndex == cSize){cIndex = 0;}
//					if(isZBeforeT){
//						zIndex+= 1;if(zIndex == zSize){zIndex = 0;tIndex+= 1;if(tIndex == tSize){tIndex = 0;}};
//					}else{
//						tIndex+= 1;if(tIndex == tSize){tIndex = 0;zIndex+= 1;if(zIndex == zSize){zIndex = 0;}};
//					}
//				}
//			}
//		}finally{
//			imageReader.close();
//		}
//		return ctzxy;
//	}	
	
	
	
	public static ImageDataset readImageDataset(String imageID) throws FormatException, IOException, ImageException {
		if (imageID.toUpperCase().endsWith(".ZIP")){
			ZipFile zipFile = new ZipFile(new File(imageID),ZipFile.OPEN_READ);
			ArrayList<ImageDataset> imageDatasetList = new ArrayList<ImageDataset>();
			Enumeration<? extends ZipEntry> enumZipEntry = zipFile.entries();
			while (enumZipEntry.hasMoreElements()){
				ZipEntry entry = enumZipEntry.nextElement();
				String entryName = entry.getName();
				InputStream inputStream = zipFile.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(entryName,false);
				byte[] buffer = new byte[50000];
				while (true){
					int bytesRead = inputStream.read(buffer);
					if (bytesRead==-1){
						break;
					}
					fos.write(buffer, 0, bytesRead);
				}
				fos.close();
				ImageDataset imageDataset = readImageDataset(entryName);
				imageDatasetList.add(imageDataset);
			}
			ImageDataset completeImageDataset = ImageDataset.createZStack(imageDatasetList.toArray(new ImageDataset[imageDatasetList.size()]));
			return completeImageDataset;
		}
		
//		FormatReader.setDebugLevel(3);
//		FormatReader.setDebug(true);
		ImageReader imageReader = new ImageReader();
	    OMEXMLMetadataStore store = new OMEXMLMetadataStore();
	    store.createRoot();
	    imageReader.setMetadataStore(store);
	    FormatReader.debug = true;
		IFormatReader formatReader = imageReader.getReader(imageID);
		int seriesCount = imageReader.getSeriesCount(imageID);
		System.out.println("formatReader.getSeriesCount() = "+formatReader.getSeriesCount(imageID));
		try{
			System.out.println("before series, image size from imageReader("+
					formatReader.getSizeX(imageID)+","+
					formatReader.getSizeY(imageID)+","+
					formatReader.getSizeZ(imageID)+","+
					formatReader.getSizeC(imageID)+","+
					formatReader.getSizeT(imageID)+")");
			System.out.println("imagecount = "+formatReader.getImageCount(imageID));
			int numImages = formatReader.getImageCount(imageID);
			int numChannels = formatReader.getSizeC(imageID);
			if (numChannels>1){
//				throw new RuntimeException("multi-channel images not yet supported");
			}
			int numTimes = formatReader.getSizeT(imageID);
			UShortImage[] images = new UShortImage[numImages];
			for (int i = 0; i < numImages; i++) {
				BufferedImage origBufferedImage = formatReader.openImage(imageID, i);
				System.out.println("original image is type "+ImageTools.getPixelType(origBufferedImage));
				BufferedImage ushortBufferedImage = ImageTools.makeType(origBufferedImage, DataBuffer.TYPE_USHORT);
				System.out.println("ushort image is type "+ImageTools.getPixelType(ushortBufferedImage));
				int zct[] = formatReader.getZCTCoords(imageID, i);
				int pixelType = ImageTools.getPixelType(ushortBufferedImage);
				short[][] pixels = ImageTools.getShorts(ushortBufferedImage);
				int minValue = ((int)pixels[0][0])&0xffff;
				int maxValue = ((int)pixels[0][0])&0xffff;
				for (int j = 0; j < pixels[0].length; j++) {
					minValue = Math.min(minValue,0xffff&((int)pixels[0][i]));
					maxValue = Math.max(maxValue,0xffff&((int)pixels[0][i]));
				}
				Float pixelSizeX_m = store.getPixelSizeX(0);
				Float pixelSizeY_m = store.getPixelSizeY(0);
				Float pixelSizeZ_m = store.getPixelSizeZ(0);
				if (pixelSizeX_m==null || pixelSizeX_m==0f){
					pixelSizeX_m = 0.3e-6f;
				}
				if (pixelSizeY_m==null || pixelSizeY_m==0f){
					pixelSizeY_m = 0.3e-6f;
				}
				if (pixelSizeZ_m==null || pixelSizeZ_m==0f || pixelSizeZ_m==1f){
					pixelSizeZ_m = 0.9e-6f;
				}
				int sizeX = store.getSizeX(0);
				int sizeY = store.getSizeY(0);
				int sizeZ = store.getSizeZ(0);
				
				if (sizeZ > 1){
//					throw new RuntimeException("3D images not yet supported");
				}
				Extent extent = null;
				if (pixelSizeX_m!=null && pixelSizeY_m!=null && pixelSizeZ_m!=null && pixelSizeX_m>0 && pixelSizeY_m>0 && pixelSizeZ_m>0){
					extent = new Extent(pixelSizeX_m*sizeX*1e6,pixelSizeY_m*sizeY*1e6,pixelSizeZ_m*sizeZ*1e6);
				}
				System.out.println("reading image "+i+", z="+zct[0]+", channel="+zct[1]+", time="+zct[2]+", pixelType="+pixelType+", numSeries="+seriesCount+", size=("+((pixelSizeX_m!=null)?(pixelSizeX_m*1e6):"?")+","+((pixelSizeY_m!=null)?(pixelSizeY_m*1e6):"?")+","+((pixelSizeZ_m!=null)?(pixelSizeZ_m*1e6):"?")+") um, dim=("+sizeX+","+sizeY+","+sizeZ+"), value in ["+minValue+","+maxValue+"]");
				images[i] = new UShortImage(pixels[0],extent,sizeX,sizeY,1);
			}
			
			System.out.println("before series, image size from Metadata Store("+
					store.getSizeX(0)+","+
					store.getSizeY(0)+","+
					store.getSizeZ(0)+","+
					store.getSizeC(0)+","+
					store.getSizeT(0)+")");
			
			Integer ii = new Integer(0);			
			System.out.println("creationDate: "+store.getCreationDate(ii));
			System.out.println("description: "+store.getDescription("description??", ii));
			System.out.println("dimension order: "+store.getDimensionOrder(ii));
			System.out.println("image name: "+store.getImageName(ii));
			System.out.println("pixel type: "+store.getPixelType(ii));
			System.out.println("stage name: "+store.getStageName("stage name??", ii));
			System.out.println("big endian: "+store.getBigEndian(ii));
			System.out.println("pixel size X: "+store.getPixelSizeX(ii));
			System.out.println("pixel size Y: "+store.getPixelSizeY(ii));
			System.out.println("pixel size Z: "+store.getPixelSizeZ(ii));
			System.out.println("pixel size C: "+store.getPixelSizeC(ii));
			System.out.println("pixel size T: "+store.getPixelSizeT(ii));
			Float pixelSizeX = store.getPixelSizeX(ii);
			if (pixelSizeX!=null){
				System.out.println("   image Size X: "+(store.getSizeX(ii)*pixelSizeX.floatValue()*1e6)+" microns");
			}
			Float pixelSizeY = store.getPixelSizeX(ii);
			if (pixelSizeY!=null){
				System.out.println("   image Size Y: "+(store.getSizeY(ii)*pixelSizeY.floatValue()*1e6)+" microns");
			}
			System.out.println("size X: "+store.getSizeX(ii));
			System.out.println("size Y: "+store.getSizeY(ii));
			System.out.println("size Z: "+store.getSizeZ(ii));
			System.out.println("size C: "+store.getSizeC(ii));
			System.out.println("size T: "+store.getSizeT(ii));
			System.out.println("stage X: "+store.getStageX(ii));
			System.out.println("stage Y: "+store.getStageY(ii));
			System.out.println("stage Z: "+store.getStageZ(ii));
			
			for (int i=0; i<formatReader.getSeriesCount(imageID); i++) {
				formatReader.setSeries(imageID, i);
	
				System.out.println("image size from imageReader("+
						formatReader.getSizeX(imageID)+","+
						formatReader.getSizeY(imageID)+","+
						formatReader.getSizeZ(imageID)+","+
						formatReader.getSizeC(imageID)+","+
						formatReader.getSizeT(imageID)+")");
				System.out.println("image size from Metadata Store("+
						store.getSizeX(new Integer(i))+","+
						store.getSizeY(new Integer(i))+","+
						store.getSizeZ(new Integer(i))+","+
						store.getSizeC(new Integer(i))+","+
						store.getSizeT(new Integer(i))+")");
				
			}
			
			// Read in the time stamps for individual time series images from formatReader.
			double[] times = null;
			Double firstTimeStamp = (Double)formatReader.getMetadataValue(imageID, "TimeStamp0");
			if (firstTimeStamp != null) {
				times = new double[numTimes];
				double firstTimeStampVal = firstTimeStamp.doubleValue();
				for (int i = 0; i < times.length; i++) {
					Double timeStamp = (Double)formatReader.getMetadataValue(imageID, "TimeStamp"+i);
					times[i] = timeStamp.doubleValue() - firstTimeStampVal;
				}
			} else{
				System.out.println(" Specified image file format does not have time stamp values.");
			}
	
			int numZ = Math.max(1,formatReader.getSizeZ(imageID));
			ImageDataset imageDataset = new ImageDataset(images,times,numZ);
	
			return imageDataset;
		}finally{
			if(formatReader != null){
				formatReader.close();
			}
		}
	}
}
