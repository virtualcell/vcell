package cbit.image.gui;

import cbit.util.Origin;
import cbit.util.Extent;
import cbit.util.ISize;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometryTest;
import cbit.vcell.geometry.RegionImage;
import cbit.vcell.simdata.SourceDataInfo;
import cbit.image.ImageException;

import java.util.Date;
/**
 * Insert the type's description here.
 * Creation date: (3/28/2002 12:05:35 PM)
 * @author: Jim Schaff
 */
public class RegionImageTest {
/**
 * Insert the method's description here.
 * Creation date: (5/21/2002 1:29:33 PM)
 * @param originalImage cbit.image.VCImage
 */
public static RegionImage createRegionImage(cbit.image.VCImage vcImage) throws ImageException {
			
	int nX = vcImage.getNumX();
	int nY = vcImage.getNumY();
	int nZ = vcImage.getNumZ();

	//
	// print out original image
	//
	StringBuffer buffer = new StringBuffer();
	buffer.append("------------original image-------------\n");
	for (int j = 0; j < Math.min(100,nY); j++){
		for (int i = 0; i < Math.min(100,nX); i++){
			buffer.append(vcImage.getPixel(i,j,0));
		}
		buffer.append('\n');
	}
	System.out.println(buffer);

	//
	// create region image
	//
	Date startDate = new Date();
	RegionImage regionImage = new RegionImage(vcImage);
	Date endDate = new Date();
	System.out.println("RegionImage construction took " + (endDate.getTime() - startDate.getTime())/1000.0 + " seconds");
	
	//
	// print out region statistics
	//
	RegionImage.RegionInfo[] infos = regionImage.getRegionInfos();
	for (int i = 0; i < infos.length; i++){
		System.out.println(infos[i].toString());
	}
	//
	// print out region image
	//
	buffer = new StringBuffer();
	buffer.append("------------region image-------------\n");
	for (int j = 0; j < Math.min(100,nY); j++){
		for (int i = 0; i < Math.min(100,nX); i++){
			for (int r = 0; r < infos.length; r++){
				if (infos[r].isIndexInRegion(i+nX*j)){
					buffer.append((char)('A' + infos[r].getRegionIndex()));
				}
			}
		}
		buffer.append('\n');
	}
	System.out.println(buffer);

	return regionImage;
		
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2002 2:04:24 PM)
 * @param image cbit.image.VCImage
 * @param regionImage cbit.image.VCImage
 */
public static void displayImages(cbit.image.VCImage image1, cbit.image.VCImage image2) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		cbit.image.gui.ImagePaneView aImagePaneView1 = getImagePaneView(image1);
		cbit.image.gui.ImagePaneView aImagePaneView2 = aImagePaneView1;
		javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(2,1));
		panel.add(aImagePaneView1);
		panel.add(aImagePaneView2);
		frame.setContentPane(panel);
		panel.setSize(400,800);
		System.out.println("panel size = "+panel.getSize());
		frame.setSize(panel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		aImagePaneView1.setSize(800,800);
		frame.setSize(aImagePaneView1.getWidth() + insets.left + insets.right, 2*aImagePaneView1.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2002 1:21:44 PM)
 * @return cbit.image.VCImage
 */
public static cbit.image.VCImage getExample1() throws ImageException {
	cbit.util.Extent extent = new cbit.util.Extent(1.0,1.0,1.0);
	int nX = 40;
	int nY = 40;
	int nZ = 1;
	byte pixels[] = {  // 20 x 20 x 1
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,3,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,3,3,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0, 
		0,0,0,0,0,1,1,1,1,1,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,2,2,2,0,0,0, 
		0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,2,2,0,0,0, 
		0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,2,0,0,0,0, 
		0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,0,0,0,0,0,
		0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,0,0,0,0,0, 
		0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,3,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,3,3,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0, 
		0,0,0,0,0,1,1,1,1,1,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,2,2,2,0,0,0, 
		0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,2,2,0,0,0, 
		0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,2,0,0,0,0, 
		0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,0,0,0,0,0,
		0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,2,0,0,0,0,0, 
		0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 
		0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
	};
	
	return new cbit.image.VCImageUncompressed(null,pixels,extent,nX,nY,nZ);
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2002 1:21:44 PM)
 * @return cbit.image.VCImage
 */
public static cbit.image.VCImage getGeometryExample1(ISize sampleSize) throws org.vcell.expression.ExpressionException, ImageException, GeometryException {
	Geometry geometry = GeometryTest.getImageExample2D();
	return geometry.getGeometrySpec().createSampledImage(sampleSize);
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2002 1:50:09 PM)
 * @return cbit.image.VCImage
 * @param regionImage cbit.vcell.geometry.RegionImage
 */
public static cbit.image.VCImage getImage(RegionImage regionImage) throws ImageException {
	//
	// create out region image
	//
	int nX = regionImage.getNumX();
	int nY = regionImage.getNumY();
	int nZ = regionImage.getNumZ();

	RegionImage.RegionInfo infos[] = regionImage.getRegionInfos();
	byte pixels[] = new byte[nX*nY*nZ];
	int index = 0;
	for (int i = 0; i < pixels.length; i++){
		for (int r = 0; r < infos.length; r++){
			if (infos[r].isIndexInRegion(i)){
				pixels[i] = (byte)r;
			}
		}
	}
	cbit.util.Extent extent = new cbit.util.Extent(1.0,1.0,1.0);
	cbit.image.VCImage vcImage = new cbit.image.VCImageUncompressed(null,pixels,extent,nX,nY,nZ);
	return vcImage;
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2002 2:04:24 PM)
 * @param image cbit.image.VCImage
 * @param regionImage cbit.image.VCImage
 */
public static cbit.image.gui.ImagePaneView getImagePaneView(cbit.image.VCImage image) throws ImageException {
	cbit.image.gui.ImagePaneView aImagePaneView = new cbit.image.gui.ImagePaneView();
	int nX = image.getNumX();
	int nY = image.getNumY();
	int nZ = image.getNumZ();
	cbit.util.Range range = new cbit.util.Range(0,image.getNumPixelClasses());
	cbit.vcell.simdata.SourceDataInfo sdi = new cbit.vcell.simdata.SourceDataInfo(cbit.vcell.simdata.SourceDataInfo.INDEX_TYPE,image.getPixels(),new Extent(1,1,1),new Origin(0,0,0),range,0,nX,1,nY,nX,nZ,nX*nY);
	aImagePaneView.getImagePaneModel().setMode(cbit.image.gui.ImagePaneModel.NORMAL_MODE);
	cbit.vcell.simdata.DisplayAdapterService das = new cbit.vcell.simdata.DisplayAdapterService();
	das.setActiveScaleRange(range);
	das.addColorModelForIndexes(das.createContrastColorModel(),"Contrast");
	das.setActiveColorModelID("Contrast");
	aImagePaneView.getImagePaneModel().setViewport(new java.awt.Rectangle(400,400));
	aImagePaneView.getImagePaneModel().setDisplayAdapterService(das);
	aImagePaneView.getImagePaneModel().setSourceData(sdi);
	aImagePaneView.setSize(400,400);
	aImagePaneView.setMinimumSize(new java.awt.Dimension(400,400));
	return aImagePaneView;
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
				
//		cbit.image.VCImage vcImage = getGeometryExample1(new ISize(50,50,1));
		cbit.image.VCImage vcImage = getGeometryExample1(new ISize(100,100,1));
//		cbit.image.VCImage vcImage = getExample1();

		RegionImage regionImage = createRegionImage(vcImage);
		cbit.image.VCImage regionColoredImage = getImage(regionImage);

		displayImages(vcImage, regionColoredImage);
		
	}catch (Throwable e){
		System.out.println("uncaught exception in RegionImageTest.main()");
		e.printStackTrace(System.out);
	}
}
}
