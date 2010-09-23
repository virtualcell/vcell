package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import javax.swing.ImageIcon;

import cbit.gui.graph.ContainerShape;
import cbit.gui.graph.GraphModel;
import cbit.image.VCImage;
import cbit.image.VCPixelClass;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.GeometrySpec;

public class SubVolumeContainerShape extends ContainerShape{
	Geometry geom = null;
	private static final int SAMPLED_GEOM_SIZE_MAX = 150;
	private BufferedImage brightImage = null;

	public SubVolumeContainerShape(Geometry argGeom, GraphModel graphModel) {
		super(graphModel);
		geom = argGeom;
	}

	@Override
	public Object getModelObject() {
		return geom;
	}

	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		if(brightImage != null){
			return new Dimension(brightImage.getWidth(),brightImage.getHeight());
		}
		return new Dimension(10,10);
	}

	@Override
	public Point getSeparatorDeepCount() {	
		return new Point(0,0);
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY) {
		// draw background image (of handles)
		if(brightImage != null){
			g.drawImage(brightImage,absPosX, absPosY, null);
		}
	}

	public void refreshDisplayImage(BufferedImage newDisplayImage){
		brightImage = newDisplayImage;
	}

	public static BufferedImage CreateDisplayImage(Geometry geom) throws GeometryException{
		try {		
			int REAL_SAMPLE_X = 0;
			int REAL_SAMPLE_Y = 0;
			if(geom != null){
				if(geom.getDimension() > 0){
					//Calc Scaling parameters
					double srcScaleX = SAMPLED_GEOM_SIZE_MAX/geom.getExtent().getX();
					double srcScaleY = SAMPLED_GEOM_SIZE_MAX/geom.getExtent().getY();

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
			GeometrySpec geometrySpec = geom.getGeometrySpec();
			if (geometrySpec.getDimension() > 0) {
				BufferedImage brightImage = 
					new BufferedImage(REAL_SAMPLE_X,REAL_SAMPLE_Y, BufferedImage.TYPE_INT_RGB);
				Graphics2D brightG2D = brightImage.createGraphics();
				brightG2D.setColor(java.awt.Color.white);
				brightG2D.fillRect(0,0,REAL_SAMPLE_X,REAL_SAMPLE_Y);
				VCImage sampledImage = geometrySpec.getSampledImage();
				java.awt.image.IndexColorModel handleColorMap = GeometrySpec.getHandleColorMap();
				byte[] reds = new byte[256];
				handleColorMap.getReds(reds);
				byte[] greens = new byte[256];
				handleColorMap.getGreens(greens);
				byte[] blues = new byte[256];
				handleColorMap.getBlues(blues);
				//Create projections of each subvolume handle
				VCPixelClass[] pixClassHandles = sampledImage.getPixelClasses();
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
					int cmapIndex = (pixClassHandles[i].getPixel()&0xff);
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
					ImageIcon theImageIcon =
						new ImageIcon(Toolkit.getDefaultToolkit().createImage(mis1).
								getScaledInstance(REAL_SAMPLE_X,REAL_SAMPLE_Y,
										Image.SCALE_AREA_AVERAGING));
					brightG2D.drawImage(theImageIcon.getImage(), 0, 0, theImageIcon.getImageObserver());
				}
				return brightImage;
			}
			return null;
		}catch (GeometryException e){
			e.printStackTrace(System.out);
			throw e;
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Imageshape refreshImage Error " + e.getMessage());
		}
	}

	@Override
	public void refreshLabel() {}

	@Override
	public void resize(Graphics2D g, Dimension newSize) {
		return;
	}
}