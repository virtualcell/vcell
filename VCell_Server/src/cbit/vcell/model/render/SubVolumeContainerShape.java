package cbit.vcell.model.render;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.LayoutException;
import cbit.gui.graph.Shape;
import cbit.image.VCPixelClass;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.simdata.DisplayAdapterService;
/**
 * This type was created in VisualAge.
 */
public class SubVolumeContainerShape extends ContainerShape implements java.awt.image.ImageObserver, PropertyChangeListener {
	Geometry geom = null;
	//Hashtable subVolumeAttachmentPoints = null;
	//java.awt.Image sampledGeometryImage = null;
	//cbit.image.VCImage sampledGeometryHandles = null;
	//SubVolume lastPickedSubVolume = null;
	private static final int SAMPLED_GEOM_SIZE_MAX = 150;
	private int REAL_SAMPLE_X = 0;
	private int REAL_SAMPLE_Y = 0;
	//
	//private javax.swing.ImageIcon[] pixClassImageIconScaled = null;
	//private javax.swing.ImageIcon[] pixClassImageIconScaledHighlight = null;
	//private byte[][] pixClassScaled = null;
	//private VCPixelClass[] pixClassHandles = null;
	//private int highlight = -1;
	private java.awt.image.BufferedImage brightImage = null;
	//private java.awt.image.BufferedImage darkImage = null;
	//private java.awt.Image darkImage2 = null;

/**
 * ImageShape constructor comment.
 * @param graphModel cbit.vcell.graph.GraphModel
 */
public SubVolumeContainerShape(Geometry argGeom,GraphModel graphModel) {
	super(graphModel);
	geom = argGeom;
	geom.getGeometrySpec().addPropertyChangeListener(this);
	try {
		refreshImageShape();
	}catch (GeometryException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2003 11:35:52 AM)
 */
private void calculateRealScaling() {

	REAL_SAMPLE_X = 0;
	REAL_SAMPLE_Y = 0;
	if(geom != null){
		if(geom.getDimension() > 0){
			//Calc Scaling parameters
			double srcScaleX = (double)SAMPLED_GEOM_SIZE_MAX/geom.getExtent().getX();
			double srcScaleY = (double)SAMPLED_GEOM_SIZE_MAX/geom.getExtent().getY();
			
			if(srcScaleX < srcScaleY){
				REAL_SAMPLE_X = SAMPLED_GEOM_SIZE_MAX;
				REAL_SAMPLE_Y = Math.max((int)(srcScaleX*geom.getExtent().getY()),1);
			}
			else{
				REAL_SAMPLE_Y = SAMPLED_GEOM_SIZE_MAX;
				REAL_SAMPLE_X = Math.max((int)(srcScaleY*geom.getExtent().getX()),1);		
			}
		}
	}
}

/**
 * getModelObject method comment.
 */
public Object getModelObject() {
	return geom;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 * @param g java.awt.Graphics
 */
public Dimension getPreferedSize(java.awt.Graphics2D g) {
	Dimension d = new Dimension(REAL_SAMPLE_X,REAL_SAMPLE_Y);
	return d;
}


/**
 * This method was created by a SmartGuide.
 * @return int
 */
public Point getSeparatorDeepCount() {	
	return new Point(0,0);
}


/**
 * This method was created in VisualAge.
 * @return boolean
 * @param img Image
 * @param info int
 * @param x int
 * @param y int
 * @param width int
 * @param height int
 */
public boolean imageUpdate(java.awt.Image img,int info,int x,int y,int width,int height){
	if((info & (ImageObserver.ERROR + ImageObserver.ABORT)) != 0){
		throw new RuntimeException("Error in ImageShape.imageUpdate");
	}

	if(((info & ImageObserver.WIDTH) != 0) && ((info & ImageObserver.HEIGHT) != 0)){
		return false;//No further update needed, have width and height
	}
//System.out.println("imageObserver info = "+info);
	return true;//More Info needed
}


/**
 * This method was created in VisualAge.
 */
public void layout() throws LayoutException {

	super.layout();

	//for (int i=0;i<childShapeList.size();i++){
		//SubvolumeShape shape = (SubvolumeShape)childShapeList.elementAt(i);
		//Point attachPoint = getLocalAttachmentPoint((SubVolume)shape.getModelObject());
////System.out.println("attachPoint for "+((SubVolume)shape.getModelObject()).getName()+" is "+attachPoint);
		//shape.screenPos.x = attachPoint.x - shape.screenSize.width/2;
		//shape.screenPos.y = attachPoint.y - shape.screenSize.height/2;
		//shape.layout();
	//}
}


/**
 * paint method comment.
 */
public void paint(java.awt.Graphics2D g, int parentOffsetX, int parentOffsetY) {
	
	int absPosX = screenPos.x + parentOffsetX;
	int absPosY = screenPos.y + parentOffsetY;

	//
	// draw background image (of handles)
	//
	//g.drawImage(sampledGeometryImage, absPosX, absPosY, this);

	if(brightImage != null){
		g.drawImage(brightImage,absPosX, absPosY, this);
	}
	//if(pixClassImageIconScaled != null){
		//g.drawImage(brightImage,absPosX, absPosY, this);
		////for(int i =0;i < pixClassImageIconScaled.length;i+= 1){
			////g.drawImage(pixClassImageIconScaled[i].getImage(), absPosX, absPosY, this);
		////}
		//if(highlight != -1 && pixClassImageIconScaledHighlight != null && pixClassHandles != null){
			//for(int i =0;i<pixClassHandles.length;i+= 1){
				//if(pixClassHandles[i].getPixel() == highlight && pixClassImageIconScaledHighlight[i] != null){
					//g.drawImage(darkImage2,absPosX, absPosY, this);
					//g.drawImage(pixClassImageIconScaledHighlight[i].getImage(), absPosX, absPosY, this);
				//}
			//}
		//}
	//}
	
	//
	// draw attachment points within image (or they can be hidden)
	//
	for (int i = 0; i < childShapeList.size(); i++) {
		Shape child = (Shape) childShapeList.elementAt(i);
		child.paint(g, absPosX, absPosY);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/5/00 4:19:22 PM)
 * @param event java.beans.PropertyChangeEvent
 */
public void propertyChange(PropertyChangeEvent event) {
	Geometry geometry = (Geometry) getModelObject();
	if (geometry != null && event.getSource() == geometry.getGeometrySpec() && event.getPropertyName().equals("sampledImage")){
		try {
			refreshImageShape();
			graphModel.notifyChangeEvent();
		}catch (Throwable e){
			e.printStackTrace(System.out);
		}
	}
}


/**
 * This method was created in VisualAge.
 */
private void refreshImageShape() throws GeometryException{
	try {		
		//Get a geometry image(handles) with combined image and analytic
		calculateRealScaling();
		//setHighlightHandle(-1);
		GeometrySpec geometrySpec = geom.getGeometrySpec();
		if (geometrySpec.getDimension() > 0) {
			//
			brightImage = new java.awt.image.BufferedImage(REAL_SAMPLE_X,REAL_SAMPLE_Y,java.awt.image.BufferedImage.TYPE_INT_RGB);
			java.awt.Graphics2D brightG2D = brightImage.createGraphics();
			brightG2D.setColor(java.awt.Color.white);
			brightG2D.fillRect(0,0,REAL_SAMPLE_X,REAL_SAMPLE_Y);
			//darkImage = new java.awt.image.BufferedImage(REAL_SAMPLE_X,REAL_SAMPLE_Y,java.awt.image.BufferedImage.TYPE_INT_RGB);
			//java.awt.Graphics2D darkG2D = darkImage.createGraphics();
			//darkG2D.setColor(java.awt.Color.white);
			//darkG2D.fillRect(0,0,REAL_SAMPLE_X,REAL_SAMPLE_Y);
			//
			cbit.image.VCImage sampledImage = geometrySpec.getSampledImage();
			java.awt.image.IndexColorModel handleColorMap = DisplayAdapterService.getHandleColorMap();
			byte[] reds = new byte[256];
			handleColorMap.getReds(reds);
			byte[] greens = new byte[256];
			handleColorMap.getGreens(greens);
			byte[] blues = new byte[256];
			handleColorMap.getBlues(blues);
			//Create projections of each subvolume handle
			VCPixelClass[] pixClassHandles = sampledImage.getPixelClasses();
			//pixClassImageIconScaled = new javax.swing.ImageIcon[pixClassHandles.length];
			//pixClassImageIconScaledHighlight = new javax.swing.ImageIcon[pixClassHandles.length];
			//pixClassScaled = new byte[pixClassHandles.length][];
			//
			//byte[] gray = new byte[256];
			//byte[] transp = new byte[256];
			//for(int i = 0;i < gray.length;i+= 1){gray[i] = (byte)i;transp[i] = (i==0?(byte)0:(byte)(255/pixClassHandles.length));}
			//java.awt.image.IndexColorModel icm = new java.awt.image.IndexColorModel(8,256,gray,gray,gray,transp);
			byte[] pixels = sampledImage.getPixels();
			for(int i = 0;i < pixClassHandles.length;i+= 1){
				byte[] zBuf = new byte[sampledImage.getNumX()*sampledImage.getNumY()];
				java.util.Arrays.fill(zBuf,(byte)0);
				//Project z
				for(int j =0;j<pixels.length;j+= 1){
					if(pixels[j] == pixClassHandles[i].getPixel()){
						zBuf[j%zBuf.length] = (byte)1;
					}
				}
				//Scale X-Y
				int cmapIndex = (int)(pixClassHandles[i].getPixel()&0xff);
				byte ired = reds[cmapIndex];
				byte igrn = greens[cmapIndex];
				byte iblu = blues[cmapIndex];
				MemoryImageSource mis1 = new MemoryImageSource(sampledImage.getNumX(), 
											sampledImage.getNumY(), 
											new java.awt.image.IndexColorModel(8,2,
												new byte[]{0,ired},
												new byte[]{0,igrn},
												new byte[]{0,iblu},
												new byte[]{0,(byte)(200)}),
											zBuf, 
											0, sampledImage.getNumX());
				//pixClassImageIconScaled[i] =
				javax.swing.ImageIcon theImageIcon =
					new javax.swing.ImageIcon(
						java.awt.Toolkit.getDefaultToolkit().createImage(mis1).
							getScaledInstance(REAL_SAMPLE_X,REAL_SAMPLE_Y,java.awt.Image.SCALE_AREA_AVERAGING));

				//brightG2D.drawImage(pixClassImageIconScaled[i].getImage(),0,0,pixClassImageIconScaled[i].getImageObserver());
				brightG2D.drawImage(theImageIcon.getImage(),0,0,theImageIcon.getImageObserver());
				
				//java.awt.image.PixelGrabber pg =
		        	//new java.awt.image.PixelGrabber(
			        	//pixClassImageIconScaled[i].getImage(), 0, 0, REAL_SAMPLE_X,REAL_SAMPLE_Y, false);
		        //try {
		            //pg.grabPixels();
		        //} catch (InterruptedException e) {
		            //throw new java.io.IOException("java.awt.Image Interrupted waiting for pixels!");
		        //}
		        //if ((pg.getStatus() & java.awt.image.ImageObserver.ABORT) != 0) {
		            //throw new java.io.IOException("java.awt.Image fetch aborted or errored");
		        //}
		        //int w = pg.getWidth();
		        //int h = pg.getHeight();
		        //Object grabbedPixels = pg.getPixels();
		        //pixClassScaled[i] = new byte[java.lang.reflect.Array.getLength(grabbedPixels)];
		        //for (int k = 0; k < pixClassScaled[i].length; k++){
			        //if(grabbedPixels instanceof int[]){
		        		//pixClassScaled[i][k] = (((int[])grabbedPixels)[k] != 0 ?(byte)1:0);
			        //}else if(grabbedPixels instanceof byte[]){
				        //pixClassScaled[i][k] = (((byte[])grabbedPixels)[k] != 0 ?(byte)1:0);
			        //}else{
			        //}
		        //}
		        //				
				//MemoryImageSource mis2 = new MemoryImageSource(REAL_SAMPLE_X,REAL_SAMPLE_Y,
											//new java.awt.image.IndexColorModel(8,2,
												//new byte[]{0,(byte)255},
												//new byte[]{0,(byte)0},
												//new byte[]{0,(byte)0},
												//new byte[]{(byte)0,(byte)(128)}),
											//pixClassScaled[i], 
											//0, REAL_SAMPLE_X);
				//pixClassImageIconScaledHighlight[i] =
					//new javax.swing.ImageIcon(
						//java.awt.Toolkit.getDefaultToolkit().createImage(mis2));

			}
			//darkImage2 = java.awt.Toolkit.getDefaultToolkit().createImage(
				//new java.awt.image.FilteredImageSource(
					//brightImage.getSource(),
					//new java.awt.image.BufferedImageFilter(new java.awt.image.RescaleOp(.5f,0f,null))));
			
			////Object[] objArr = new Object[sampledImage.getPixelClasses().length];
			////for(int i=0;i<objArr.length;i+= 1){
				////objArr[i] = new Object[3];
				////((Object[])objArr[i])[0] = pixClassHandles[i];
				////((Object[])objArr[i])[1] = pixClassScaled[i];
				////((Object[])objArr[i])[2] = pixClassImageIconScaled;
			////}
			////java.util.Arrays.sort((Object[])(sampledImage.getPixelClasses().clone()),
				////new java.util.Comparator() {
					////public int compare(Object obj1, Object obj2){
						////long obj1Cover = 0;
						////long obj2Cover = 0;
						////byte[] obj1IIS = (byte[])(((Object[])obj1)[3]);
						////byte[] obj2IIS = (byte[])(((Object[])obj2)[3]);
						////for(int i=0;i < obj1IIS.length;i+= 1){
							////obj1Cover+= obj1IIS[i];
							////obj2Cover+= obj2IIS[i];
						////}
						////return (obj1Cover == obj2Cover?0:(obj1Cover<obj2Cover?-1:1));
					////}
					////public boolean equals(Object obj){
						////return false;
					////}
				////}
			////);
		}


	}catch (GeometryException e){
		e.printStackTrace(System.out);
		throw e;
	} catch (Throwable e) {
		e.printStackTrace(System.out);
		throw new RuntimeException("Imageshape refreshImage Error " + e.getMessage());
	}
}


/**
 * refreshLabel method comment.
 */
public void refreshLabel() {}


/**
 * This method was created by a SmartGuide.
 * @param newSize java.awt.Dimension
 */
public void resize(java.awt.Graphics2D g, Dimension newSize) {
	return;
}
}