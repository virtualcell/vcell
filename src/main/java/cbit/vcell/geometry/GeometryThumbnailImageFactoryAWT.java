package cbit.vcell.geometry;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

import javax.swing.ImageIcon;

import cbit.image.DisplayAdapterService;
import cbit.image.ImageException;
import cbit.image.ThumbnailImage;
import cbit.image.VCImage;
import cbit.image.VCPixelClass;


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
				IndexColorModel colorModel = new IndexColorModel(8,2,
						new byte[]{0,ired},
						new byte[]{0,igrn},
						new byte[]{0,iblu},
						new byte[]{0,(byte)(200)});
				int width = currSampledImage.getNumX();
				int height = currSampledImage.getNumY();
				BufferedImage bufferedImage = getImage(colorModel, zBuf, width, height);
				ImageIcon theImageIcon = new ImageIcon(bufferedImage.getScaledInstance(REAL_SAMPLE_X,REAL_SAMPLE_Y,Image.SCALE_AREA_AVERAGING));
				brightG2D.drawImage(theImageIcon.getImage(), 0, 0, theImageIcon.getImageObserver());
			}
			int rgb[] = brightImage.getRGB(0, 0, REAL_SAMPLE_X, REAL_SAMPLE_Y, null, 0, REAL_SAMPLE_X);
			return new ThumbnailImage(rgb, REAL_SAMPLE_X, REAL_SAMPLE_Y, brightImage);
		}
		return null;
	}
	
	private static BufferedImage getImage(IndexColorModel indexColorModel, byte[] imageIndiciesIntoPalette, int width, int height){
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, indexColorModel);
		WritableRaster raster = bufferedImage.getRaster();
		DataBufferByte dataBuffer = (DataBufferByte)raster.getDataBuffer();
		System.arraycopy(imageIndiciesIntoPalette, 0, dataBuffer.getData(), 0,  imageIndiciesIntoPalette.length);
		return bufferedImage;
	}
	
//	public static void main(String args[]){
//		try {
//			byte[] imagePixels = new byte[100*100];
//			for (int i=0;i<100;i++){
//				for (int j=0;j<100;j++){
//					double radius = Math.sqrt(((i-50)*(i-50)) + ((j-50)*(j-50)));
//					imagePixels[i+100*j] = (radius>50)?((byte)1):((byte)0);
//				}
//			}
//			IndexColorModel colorModel = new IndexColorModel(8,2,
//					new byte[]{0,(byte)200},
//					new byte[]{0,(byte)0},
//					new byte[]{0,(byte)0},
//					new byte[]{(byte)200,(byte)200});
//			int width = 100;
//			int height = 100;
//			BufferedImage bufferedImage = getImage(colorModel, imagePixels, width, height);
//			ImageIO.write(bufferedImage, "gif", new File("C:\\temp\\GeometryThumbnailImageFactoryAWT_test.gif"));
//			
//			System.out.println("done");
//		}catch (Exception e){
//			e.printStackTrace(System.out);
//		}
//	}
}
