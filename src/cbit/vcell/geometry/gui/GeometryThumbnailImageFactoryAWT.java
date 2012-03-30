package cbit.vcell.geometry.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import javax.swing.ImageIcon;

import cbit.image.ImageException;
import cbit.image.ThumbnailImage;
import cbit.image.VCImage;
import cbit.image.VCPixelClass;
import cbit.image.gui.DisplayAdapterService;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.GeometryThumbnailImageFactory;


public class GeometryThumbnailImageFactoryAWT implements GeometryThumbnailImageFactory {

	private static final int SAMPLED_GEOM_SIZE_MAX = 150;

	public ThumbnailImage getThumbnailImage(GeometrySpec geometrySpec) throws ImageException {

		int REAL_SAMPLE_X = 0;
		int REAL_SAMPLE_Y = 0;
		if(geometrySpec.getDimension() > 0){
			//Calc Scaling parameters
			double srcScaleX = SAMPLED_GEOM_SIZE_MAX/geometrySpec.getExtent().getX();
			double srcScaleY = SAMPLED_GEOM_SIZE_MAX/geometrySpec.getExtent().getY();

			if(srcScaleX < srcScaleY){
				REAL_SAMPLE_X = SAMPLED_GEOM_SIZE_MAX;
				REAL_SAMPLE_Y = Math.max((int)(srcScaleX*geometrySpec.getExtent().getY()),1);
			}
			else{
				REAL_SAMPLE_Y = SAMPLED_GEOM_SIZE_MAX;
				REAL_SAMPLE_X = Math.max((int)(srcScaleY*geometrySpec.getExtent().getX()),1);		
			}
		}
		if (geometrySpec.getDimension() > 0) {
			BufferedImage brightImage = new BufferedImage(REAL_SAMPLE_X,REAL_SAMPLE_Y, BufferedImage.TYPE_INT_RGB);
			Graphics2D brightG2D = brightImage.createGraphics();
			brightG2D.setColor(java.awt.Color.white);
			brightG2D.fillRect(0,0,REAL_SAMPLE_X,REAL_SAMPLE_Y);
			VCImage currSampledImage = geometrySpec.getSampledImage().getCurrentValue();
			java.awt.image.IndexColorModel handleColorMap = DisplayAdapterService.getHandleColorMap();
			byte[] reds = new byte[256];
			handleColorMap.getReds(reds);
			byte[] greens = new byte[256];
			handleColorMap.getGreens(greens);
			byte[] blues = new byte[256];
			handleColorMap.getBlues(blues);
			//Create projections of each subvolume handle
			VCPixelClass[] pixClassHandles = currSampledImage.getPixelClasses();
			byte[] pixels = currSampledImage.getPixels();
			for(int i = 0;i < pixClassHandles.length;i+= 1){
				byte[] zBuf = new byte[currSampledImage.getNumX()*currSampledImage.getNumY()];
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
				MemoryImageSource mis1 = new MemoryImageSource(currSampledImage.getNumX(), 
						currSampledImage.getNumY(), 
						new java.awt.image.IndexColorModel(8,2,
								new byte[]{0,ired},
								new byte[]{0,igrn},
								new byte[]{0,iblu},
								new byte[]{0,(byte)(200)}),
								zBuf, 
								0, currSampledImage.getNumX());
				ImageIcon theImageIcon =
					new ImageIcon(Toolkit.getDefaultToolkit().createImage(mis1).
							getScaledInstance(REAL_SAMPLE_X,REAL_SAMPLE_Y,
									Image.SCALE_AREA_AVERAGING));
				brightG2D.drawImage(theImageIcon.getImage(), 0, 0, theImageIcon.getImageObserver());
			}
			int rgb[] = brightImage.getRGB(0, 0, REAL_SAMPLE_X, REAL_SAMPLE_Y, null, 0, REAL_SAMPLE_X);
			return new ThumbnailImage(rgb, REAL_SAMPLE_X, REAL_SAMPLE_Y, brightImage);
		}
		return null;
	}

}
