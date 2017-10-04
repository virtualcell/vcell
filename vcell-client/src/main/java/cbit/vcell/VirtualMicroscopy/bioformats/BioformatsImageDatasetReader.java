package cbit.vcell.VirtualMicroscopy.bioformats;

import java.awt.HeadlessException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.vcell.imagedataset.ImageDatasetService;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;
import org.vcell.util.UserCancelException;
import org.vcell.util.VCellThreadChecker;
import org.vcell.util.exe.Executable;
import org.vcell.util.exe.ExecutableException;
import org.vcell.util.gui.DialogUtils;
import org.vcell.vcellij.ImageDatasetReader;

import cbit.image.ImageException;
import cbit.image.ImageSizeInfo;
import cbit.vcell.VirtualMicroscopy.ImageDataset;
import cbit.vcell.VirtualMicroscopy.UShortImage;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;

@Plugin(type = ImageDatasetReader.class)
public class BioformatsImageDatasetReader extends AbstractService implements ImageDatasetReader {
	
	private static File bioformatsExecutableJarFile = null;
	private static Thread serverThread = null;
	private static int port = 9094;
	private static final String PROPERTY_VCELL_BIOFORMATS_EXTERNAL = "vcell.bioformats.external";
	
	public BioformatsImageDatasetReader() throws Exception{
		boolean bUseExternalBioformatsService = PropertyLoader.getBooleanProperty(PROPERTY_VCELL_BIOFORMATS_EXTERNAL, false);
		if (bUseExternalBioformatsService){
			System.out.println("BioformatsImageDatasetReader.bUseExternalBioformatsService is true, assuming BioFormatsService already running on localhost at port "+port);
			return; // nothing to do, using an external BioformatsService for debugging
		}
		if (BioformatsImageDatasetReader.bioformatsExecutableJarFile != null){
			return;
		}
		VCellThreadChecker.checkRemoteInvocation();
		try {
			File bioformatsJarFile = ResourceUtil.getBioFormatsExecutableJarFile();
			if (bioformatsJarFile!=null && bioformatsJarFile.exists()){
				BioformatsImageDatasetReader.bioformatsExecutableJarFile = bioformatsJarFile;
				if (serverThread==null){
					launchServer(port);
					try {
						Thread.sleep(4000);
					}catch (InterruptedException e){}
				}
			}else{
				final String BIOFORMATS_YES = "yes";
				final String BIOFORMATS_NO = "no";
				String[] options = {BIOFORMATS_YES , BIOFORMATS_NO};
				String downloadApproval = null;
				if(downloadApproval == null){
					downloadApproval = DialogUtils.showWarningDialog(JOptionPane.getRootFrame(), "In order to perform this operation, the BioFormats library plugin, licensed under the General Public License (GPL) must be downloaded.  This only needs to be done once. Do you want to download and install the BioFormats library plugin?", options, BIOFORMATS_YES);
				}
				if (!BIOFORMATS_YES.equals(downloadApproval)) {
					DialogUtils.showInfoDialog(JOptionPane.getRootFrame(), "BioFormats Library Plugin will not be loaded", "If you would like to use the BioFormats Library in the future, just retry the operation and agree to downloading the BioFormats Library under the General Public License.");
					//bail
					throw UserCancelException.CANCEL_GENERIC;
				}else{
					try {
						ResourceUtil.downloadBioformatsJar();
						bioformatsJarFile = ResourceUtil.getBioFormatsExecutableJarFile();
						if (bioformatsJarFile!=null && bioformatsJarFile.exists()){
							BioformatsImageDatasetReader.bioformatsExecutableJarFile = bioformatsJarFile;
							if (serverThread==null){
								launchServer(port);
							}
						}
					}catch (Exception ex){
						ex.printStackTrace(System.err);
						throw new RuntimeException("unable to download and install bioformats plugin",ex);
					}
			    }
			}
		} catch (HeadlessException | MalformedURLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to load bioformats plugin: "+e.getMessage(),e);
		}
	}

	private void launchServer(int port){
		synchronized (BioformatsImageDatasetReader.class) {
			if (serverThread!=null){
				return;
			}
			Runnable runnable = new Runnable(){
				@Override
				public void run() {
					Executable executable = new Executable(new String[] { "java", "-jar", bioformatsExecutableJarFile.getAbsolutePath(), Integer.toString(port) });
					try {
						executable.start();
					} catch (ExecutableException e) {
						e.printStackTrace();
						serverThread = null;
					}
				}
			};
			serverThread = new Thread(runnable,"bioformatsServerThread");
			serverThread.setDaemon(true);
			serverThread.start();
			try {
				// give some time for the service to start listening
				Thread.sleep(2000);
			}catch (InterruptedException e){}

		}
	}
	
	private ImageDataset getImageDataset(org.vcell.imagedataset.ImageDataset t_imageDataset) throws ImageException{
		List<org.vcell.imagedataset.UShortImage> t_images = t_imageDataset.getImages();
		UShortImage[] images = new UShortImage[t_images.size()];
		for (int i=0;i<t_images.size();i++){
			org.vcell.imagedataset.UShortImage t_image = t_images.get(i);
			short[] pixels = new short[t_image.getPixels().size()];
			for (int p=0;p<t_image.getPixelsSize();p++){
				pixels[p] = t_image.getPixels().get(p);
			}
			Origin origin = new Origin(t_image.getOrigin().x, t_image.getOrigin().y, t_image.getOrigin().z);
			Extent extent = new Extent(t_image.getExtent().x, t_image.getExtent().y, t_image.getExtent().z);
			int numX = t_image.getSize().x;
			int numY = t_image.getSize().y;
			int numZ = t_image.getSize().z;
			UShortImage image = new UShortImage(pixels,origin,extent,numX,numY,numZ);
			images[i] = image;
		}
		;
		List<Double> t_imageTimeStamps = t_imageDataset.getImageTimeStamps();
		double[] timestamps = new double[t_imageTimeStamps.size()];
		for (int t=0;t<t_imageTimeStamps.size();t++){
			timestamps[t] = t_imageTimeStamps.get(t);
		}
		ImageDataset imageDataset = new ImageDataset(images,timestamps,t_imageDataset.numZ);
		return imageDataset;
	}
	
	@Override
	public ImageSizeInfo getImageSizeInfo(String fileName) throws Exception {
        try (TTransport transport = new TSocket("localhost", port);){
            transport.open();
            TProtocol protocol = new  TBinaryProtocol(transport);
            
            ImageDatasetService.Client client = new ImageDatasetService.Client(protocol);

            org.vcell.imagedataset.ImageSizeInfo t_imageSizeInfo = client.getImageSizeInfo(fileName);
            
            org.vcell.imagedataset.ISize t_size = t_imageSizeInfo.getISize();
            double[] timePoints = new double[t_imageSizeInfo.getTimePoints().size()];
            for (int i=0;i<t_imageSizeInfo.getTimePointsSize();i++){
            	timePoints[i] = t_imageSizeInfo.getTimePoints().get(i);
            }
            ImageSizeInfo imageSizeInfo = new ImageSizeInfo(
            		t_imageSizeInfo.imagePath,
            		new ISize(t_size.getX(), t_size.getY(), t_size.getZ()),
            		t_imageSizeInfo.numChannels,
            		timePoints,
            		t_imageSizeInfo.selectedTimeIndex);
            return imageSizeInfo;
        }
	}

	@Override
	public ImageSizeInfo getImageSizeInfoForceZ(String fileName, int forceZSize) throws Exception {
        try (TTransport transport = new TSocket("localhost", port);){
            transport.open();
            TProtocol protocol = new  TBinaryProtocol(transport);
            
            ImageDatasetService.Client client = new ImageDatasetService.Client(protocol);

            org.vcell.imagedataset.ImageSizeInfo t_imageSizeInfo = client.getImageSizeInfoForceZ(fileName, forceZSize);
            
            org.vcell.imagedataset.ISize t_size = t_imageSizeInfo.getISize();
            double[] timePoints = new double[t_imageSizeInfo.getTimePoints().size()];
            for (int i=0;i<t_imageSizeInfo.getTimePointsSize();i++){
            	timePoints[i] = t_imageSizeInfo.getTimePoints().get(i);
            }
            ImageSizeInfo imageSizeInfo = new ImageSizeInfo(
            		t_imageSizeInfo.imagePath,
            		new ISize(t_size.getX(), t_size.getY(), t_size.getZ()),
            		t_imageSizeInfo.numChannels,
            		timePoints,
            		t_imageSizeInfo.selectedTimeIndex);
            return imageSizeInfo;
        }
	}

	@Override
	public ImageDataset readImageDataset(String imageID, ClientTaskStatusSupport status) throws Exception {
        try (TTransport transport = new TSocket("localhost", port);){
            transport.open();
            TProtocol protocol = new  TBinaryProtocol(transport);
            ImageDatasetService.Client client = new ImageDatasetService.Client(protocol);
            
            String fileName = imageID;
            org.vcell.imagedataset.ImageDataset t_imageDataset = client.readImageDataset(fileName);
            
            ImageDataset imageDataset = getImageDataset(t_imageDataset);
            
            return imageDataset;
        }
	}

	@Override
	public ImageDataset[] readImageDatasetChannels(String imageID, ClientTaskStatusSupport status,
			boolean bMergeChannels, Integer timeIndex, ISize resize) throws Exception {
        try (TTransport transport = new TSocket("localhost", port);){
            transport.open();
            TProtocol protocol = new  TBinaryProtocol(transport);
            ImageDatasetService.Client client = new ImageDatasetService.Client(protocol);
            
            String fileName = imageID;
            org.vcell.imagedataset.ISize t_resize = null;
            if (resize!=null){
            	t_resize = new org.vcell.imagedataset.ISize(resize.getX(), resize.getY(), resize.getZ());
            }
            
			List<org.vcell.imagedataset.ImageDataset> t_imageDatasetList = client.readImageDatasetChannels(fileName, bMergeChannels, timeIndex, t_resize);
            
            ImageDataset[] imageDatasets = new ImageDataset[t_imageDatasetList.size()];
            for (int i=0;i<t_imageDatasetList.size();i++){
            	imageDatasets[i] = getImageDataset(t_imageDatasetList.get(i));
            }
    
            return imageDatasets;
        }
	}

	@Override
	public ImageDataset readImageDatasetFromMultiFiles(File[] files, ClientTaskStatusSupport status,
			boolean isTimeSeries, double timeInterval) throws Exception {
        try (TTransport transport = new TSocket("localhost", port);){
            transport.open();
            TProtocol protocol = new  TBinaryProtocol(transport);
            ImageDatasetService.Client client = new ImageDatasetService.Client(protocol);
            
            ArrayList<String> fileList = new ArrayList<String>();
            for (File f : files){
            	fileList.add(f.getAbsolutePath());
            }
			org.vcell.imagedataset.ImageDataset t_imageDataset = client.readImageDatasetFromMultiFiles(fileList, isTimeSeries, timeInterval);
            
            ImageDataset imageDataset = getImageDataset(t_imageDataset);
    
            return imageDataset;
        }
	}

}
