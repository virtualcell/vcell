/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.util.Hashtable;

import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.CompositeDescriptor;
import javax.media.jai.operator.ExtremaDescriptor;
import javax.media.jai.operator.ScaleDescriptor;

import com.sun.media.jai.widget.DisplayJAI;

import cbit.vcell.geometry.gui.OverlayEditorPanelJAI.BrushToolHelper.BrushRefresh;

/**
 */
public class OverlayImageDisplayJAI extends DisplayJAI implements BrushRefresh{
	private BufferedImage underlyingImage = null;
	private BufferedImage highlightImage = null;
	private short[] highlightImageWritebackBuffer = null;
	private float zoom = 1.0f;
//	private boolean bRemoveROIWhenDrawing = false;
	private int blendPercent = 50;
	
	private BufferedImage allROICompositeImage = null;
	
	private int contrastFactor = 0;
	private static final int CONTRAST_BOUND = 4;
	private Rectangle cropRect = null;
	
	private static final double SCALE_MAX = Math.pow(2, 8)-1;

	private OverlayEditorPanelJAI.AllPixelValuesRange allPixelValuesRange = null;
	
	private int[][][] blendARGB = new int[2][256][256];
	private Hashtable<Integer, BufferedImage> contrastHash = new Hashtable<Integer, BufferedImage>();
	private Hashtable<Rectangle, BufferedImage> compositeHash = new Hashtable<Rectangle, BufferedImage>();
	
	public OverlayImageDisplayJAI(){
		super();
		createCompositeLookup();
	}
	
	private BufferedImage computeComposite(RenderedImage contrastEnhancedUnderlyingImage){
		//Improve memory use by saving 1 composite image in hash and re-use if sizes don't change
		Rectangle imageSizeRect = new Rectangle(0, 0, contrastEnhancedUnderlyingImage.getWidth(), contrastEnhancedUnderlyingImage.getHeight());
		BufferedImage result  = compositeHash.get(imageSizeRect);
		if(result == null){
			compositeHash.clear();
			DirectColorModel dcm = new DirectColorModel(32, 0x00FF0000, 0x0000FF00, 0x000000FF);
			WritableRaster newOverlayRaster = dcm.createCompatibleWritableRaster(imageSizeRect.width,imageSizeRect.height);
		    result = new BufferedImage(dcm,newOverlayRaster, false, null);
		    compositeHash.put(imageSizeRect,result);
		}
	    try{
			int[] newOverlayRasterInts = ((DataBufferInt)result.getRaster().getDataBuffer()).getData();
			byte[] contrastUnderlayBytes = ((DataBufferByte)contrastEnhancedUnderlyingImage.getData().getDataBuffer()).getData();
			byte[] roiBytes = (allROICompositeImage==null?null:((DataBufferByte)allROICompositeImage.getData().getDataBuffer()).getData());
			byte[] highlightBytes = (highlightImage==null?null:((DataBufferByte)highlightImage.getData().getDataBuffer()).getData());
			if(roiBytes != null && roiBytes.length != contrastUnderlayBytes.length){
				return result;
			}
			if(highlightBytes != null && highlightBytes.length != contrastUnderlayBytes.length){
				return result;
			}
			int index= 0;
			for (int Y = 0; Y < contrastEnhancedUnderlyingImage.getHeight(); Y++) {
				for (int X = 0; X < contrastEnhancedUnderlyingImage.getWidth(); X++) {
//					if(roiBytes[index] != 0){
//						System.out.println("X "+X+" Y "+Y+
//								" roi="+(0x000000FF&roiBytes[index])+
//								" under="+(underlayBytes[index]&0x000000FF)+
//								" color="+Hex.toString(blendARGB[roiBytes[index]&0x000000FF][underlayBytes[index]&0x000000FF]));
//					}
					newOverlayRasterInts[index] =
						blendARGB
							[(highlightBytes==null?0:(highlightBytes[index]==0?0:1))]
							[(roiBytes==null?0:roiBytes[index]&0x000000FF)]
							[contrastUnderlayBytes[index]&0x000000FF];
					index++;
				}
			}
	
		}catch(Exception e){
			e.printStackTrace();
			//ignore, try to display what you have
		}
	    return result;
	}

	private void createCompositeLookup(){
		//(a*A) + (1 - a)*(b*B).
		Color highlightColor = Color.yellow.darker();
		float a = (100-blendPercent)/100.0f;
		float b = 1.0f;
		for (int A = 0; A < 256; A++) {
			for (int B = 0; B < 256; B++) {
				//Underlay-ROI composite
				int red = OverlayEditorPanelJAI.CONTRAST_COLORS[A].getRed();
				int br= (int)((a*red) + (1 - a)*(b*B));
				int grn = OverlayEditorPanelJAI.CONTRAST_COLORS[A].getGreen();
				int bg= (int)((a*grn) + (1 - a)*(b*B));
				int blu = OverlayEditorPanelJAI.CONTRAST_COLORS[A].getBlue();
				int bb= (int)((a*blu) + (1 - a)*(b*B));
				
				int argb_Under_ROI_composite = 0x00000000 | br<<16 | bg<<8 | bb;
				blendARGB[0][A][B] = argb_Under_ROI_composite;
				
				float ah = a;
				//Underlay-ROI-highlight composite
				red = highlightColor.getRed();
				br= (int)((ah*red) + (1 - ah)*(b*br));
				grn = highlightColor.getGreen();
				bg= (int)((ah*grn) + (1 - ah)*(b*bg));
				blu = highlightColor.getBlue();
				bb= (int)((ah*blu) + (1 - ah)*(b*bb));

				argb_Under_ROI_composite = 0x00000000 | br<<16 | bg<<8 | bb;
				blendARGB[1][A][B] = argb_Under_ROI_composite;
			}
		}
	}
	
	public void setBlendPercent(int blendPercent){
		if(blendPercent < 0 || blendPercent > 100){
			throw new IllegalArgumentException("blendPercent must be between 0 and 100");
		}
		this.blendPercent = blendPercent;
		createCompositeLookup();
		refreshImage();
	}

	/**
	 * Method setUnderlyingImage.
	 * @param argUnderlyingImage BufferedImage
	 */
	public void setUnderlyingImage(BufferedImage argUnderlyingImage,/*boolean bNew,*/OverlayEditorPanelJAI.AllPixelValuesRange allPixelValuesRange){
		this.allPixelValuesRange = allPixelValuesRange;
		this.underlyingImage = argUnderlyingImage;
		contrastHash.clear();
//		if(bNew){
//			resetGUI();
//		}
		refreshImage();
	}
	private void resetGUI(){
		zoom = 1.0f;
		contrastFactor = 0;
		contrastHash.clear();
		cropRect = null;
	}
	/**
	 * Method setHighlightImageAndWritebackBuffer.
	 * @param argHighlightImage BufferedImage
	 * @param argHighlightImageWritebackBuffer short[]
	 */
	public void setHighlightImageAndWritebackBuffer(BufferedImage argHighlightImage, short[] argHighlightImageWritebackBuffer){
		this.highlightImage = argHighlightImage;
		this.highlightImageWritebackBuffer = argHighlightImageWritebackBuffer;
		refreshImage();
	}
	
	public void setAllROICompositeImage(BufferedImage allROICompositeImage){
		this.allROICompositeImage = allROICompositeImage;
		refreshImage();
	}

	/**
	 * Method getHighlightImageWritebackImageBuffer.
	 * @return short[]
	 */
	public short[] getHighlightImageWritebackImageBuffer(){
		return highlightImageWritebackBuffer;
	}
	
	public BufferedImage getAllROICompositeImage(){
		return allROICompositeImage;
	}
	public BufferedImage getUnderlayImage(){
		return this.underlyingImage;
	}

	/**
	 * Method getHighlightImage.
	 * @return BufferedImage
	 */
	public BufferedImage getHighlightImage(){
		return highlightImage;
	}
		
	/**
	 * Method setZoom.
	 * @param argZoom float
	 */
	public void setZoom(float argZoom){
		zoom = argZoom;
		refreshImage();
	}
//	public void setDirty(){
//	}
	/**
	 * Method getZoom.
	 * @return float
	 */
	public float getZoom(){
		return zoom;
	}
	
	/**
	 * Method makeAlpha.
	 * @param src RenderedImage
	 * @param b float
	 * @return RenderedImage
	 */
	private RenderedImage makeAlpha(int width,int height, float b) {
	      // get color band
	      int band = 1;//src.getColorModel().getNumColorComponents();

	      // ignore alpha channel
	      band = band > 3 ? 3 : band;

	      // make alpha channel paramenter

	      Byte[] bandValues = new Byte[band];
	      for ( int i = 0 ; i < band ; i++ ) {
	             bandValues[i] = new Byte((byte)(b*SCALE_MAX));
	      }

	        // make alpha channel paramenter
	        ParameterBlock pb = new ParameterBlock();
	        pb.add((float)width);
	        pb.add((float)height);
	        pb.add(bandValues);

	        // make alpha channel
	        return JAI.create("constant", pb, null);
	 }

	private BufferedImage createContrastEnhancedUnderlyingImage(){
		if(contrastHash.containsKey(getDisplayContrastFactor())){
			return contrastHash.get(getDisplayContrastFactor());
		}
		contrastHash.clear();
		BufferedImage contrastEnhancedUnderlyingImage = null;
		if(underlyingImage != null){
			contrastEnhancedUnderlyingImage = underlyingImage;
			if(contrastFactor > 0){
				//Contrast stretch
				double[][] minmaxArr = null;
				boolean bAlreadyScaled = false;
				if(allPixelValuesRange != null){
					bAlreadyScaled = (allPixelValuesRange.getScaleFactor()< 1.0?true:false);
					minmaxArr = new double[][] {{allPixelValuesRange.getMin()},{allPixelValuesRange.getMax()}};
				}else{
					minmaxArr = (double[][])ExtremaDescriptor.create(underlyingImage, null, 1, 1, false, 1, null).getProperty("extrema");
				}
				if((minmaxArr[1][0]-minmaxArr[0][0]) != 0){
					double offset = (SCALE_MAX*minmaxArr[0][0])/(minmaxArr[0][0]-minmaxArr[1][0]);
					double scale = (SCALE_MAX)/(minmaxArr[1][0]-minmaxArr[0][0]);
					contrastEnhancedUnderlyingImage = new BufferedImage(
							underlyingImage.getWidth(), underlyingImage.getHeight(),
							underlyingImage.getType());
					byte[] fromData = ((DataBufferByte)underlyingImage.getRaster().getDataBuffer()).getData();
					byte[] toData = ((DataBufferByte)contrastEnhancedUnderlyingImage.getRaster().getDataBuffer()).getData();
					if(bAlreadyScaled){
						System.arraycopy(fromData, 0, toData, 0, toData.length);
					}else{
						for (int i = 0; i < toData.length; i++) {
							toData[i] = (byte)((int)((0x000000FF&fromData[i])*scale+offset));
						}
					}
				}
				//enhance with gamma function
				if(contrastFactor > 1){
	
					byte[] tableData = new byte[256];
					for (int i = 0; i < 256; i++) {
						double normalizedWithGamma = Math.pow((i/255.0), 1/(1.0+(contrastFactor-1)/5.0));
						int val = (int)(normalizedWithGamma*255);
						if(val > 255){val = 255;}
						tableData[i] = (byte)(val&0xFF);
					}
					BufferedImage tempImage = contrastEnhancedUnderlyingImage;
					contrastEnhancedUnderlyingImage = new BufferedImage(
							tempImage.getWidth(), tempImage.getHeight(),
							tempImage.getType());
					byte[] fromData = ((DataBufferByte)tempImage.getRaster().getDataBuffer()).getData();
					byte[] toData = ((DataBufferByte)contrastEnhancedUnderlyingImage.getRaster().getDataBuffer()).getData();
					for (int i = 0; i < toData.length; i++) {
						toData[i] = tableData[fromData[i]&0x000000FF];
					}
				}
			}
		}
		if(contrastEnhancedUnderlyingImage != null){
			contrastHash.put(getDisplayContrastFactor(),contrastEnhancedUnderlyingImage);
		}
		return contrastEnhancedUnderlyingImage;
	}

	public void refreshImage(){
		
		PlanarImage sourceOverlay = null;
		
		RenderedImage contrastEnhancedUnderlyingImage = createContrastEnhancedUnderlyingImage();

		if (underlyingImage!=null && (highlightImage!=null || allROICompositeImage != null)){
			sourceOverlay = PlanarImage.wrapRenderedImage(computeComposite(contrastEnhancedUnderlyingImage));
		}else if(underlyingImage != null){
			sourceOverlay = PlanarImage.wrapRenderedImage(contrastEnhancedUnderlyingImage);
		}else if(highlightImage != null && allROICompositeImage == null){
			sourceOverlay = PlanarImage.wrapRenderedImage(highlightImage);
		}else if(allROICompositeImage != null && highlightImage == null){
			sourceOverlay = PlanarImage.wrapRenderedImage(allROICompositeImage);
		}else if(allROICompositeImage != null && highlightImage != null){
			sourceOverlay = 
				CompositeDescriptor.create(
						highlightImage, allROICompositeImage,
						makeAlpha(allROICompositeImage.getWidth(),allROICompositeImage.getHeight(), (float)(100-blendPercent)/(float)10),
						makeAlpha(highlightImage.getWidth(),highlightImage.getHeight(), (float)(100-blendPercent)/(float)10),
					false, CompositeDescriptor.NO_DESTINATION_ALPHA,null);
		}else{
			sourceOverlay = PlanarImage.wrapRenderedImage(new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB));
		}
		
		if(sourceOverlay != null){
			sourceOverlay =
				ScaleDescriptor.create(
	    			sourceOverlay, (float)zoom, (float)zoom, 0f, 0f,
	    			Interpolation.getInstance(Interpolation.INTERP_NEAREST),null);
		}

		set(sourceOverlay, 0, 0);
		
	}
		
	public void drawPaint(int x, int y, int radius, boolean erase,Color compositeColor,Point lastPaintPoint){
		if(lastPaintPoint == null){
			lastPaintPoint = new Point(x,y);
		}
		
		if (erase){
			compositeColor = Color.black;
		}
		float zoom = getZoom();
		if (allROICompositeImage != null){
			Graphics2D allROICompositeGraphics = (allROICompositeImage != null?allROICompositeImage.createGraphics():null);
			if(allROICompositeGraphics != null){allROICompositeGraphics.setColor(compositeColor);}
			int size = radius;//(int)(radius/zoom);
			//-----Interpolate between paint points for continuous lines
			double currentX = lastPaintPoint.x;
			double currentY = lastPaintPoint.y;
			int dx = x-lastPaintPoint.x;
			int dy = y-lastPaintPoint.y;
			double delta = 1.0/(dx==0 && dy==0?1.0:Math.max(Math.abs(dx), Math.abs(dy)));
			int lastX = (int)currentX;
			int lastY = (int)currentY;
			for(int i=0;i<=Math.max(Math.abs(dx), Math.abs(dy));i++){
				if(i== 0 || (int)currentX != lastX || (int)currentY != lastY){
					lastX = (int)currentX;
					lastY = (int)currentY;
					if(allROICompositeGraphics != null){
						if(size >= 2){
							allROICompositeGraphics.fillOval((int)((lastX/zoom-(size/2))),(int)((lastY/zoom-(size/2))), size, size);
						}else{
							allROICompositeGraphics.drawRect((int)((lastX/zoom-(size/2))),(int)((lastY/zoom-(size/2))), size, size);
						}
					}
				}
				currentX = lastPaintPoint.x+(i+1)*delta*dx;
				currentY = lastPaintPoint.y+(i+1)*delta*dy;
			}
			if(allROICompositeGraphics != null){allROICompositeGraphics.dispose();}
			refreshImage();
		}
	}
	
	public void setContrastToMinMax(){
		contrastFactor = 1;
		refreshImage();
	}
	public void increaseContrast(){
		contrastFactor++;
		refreshImage();
	}
	public void decreaseContrast(){
		if(contrastFactor == 0){
			return;
		}
		contrastFactor--;
		if(contrastFactor < -CONTRAST_BOUND){
			contrastFactor = -CONTRAST_BOUND;
		}
		refreshImage();
	}
	public String getContrastDescription(){
		return
		(contrastFactor == 0?"none":(contrastFactor < 0?"dark"+contrastFactor:"bright+"+contrastFactor));
	}
	public int getDisplayContrastFactor(){
		return contrastFactor;
	}
	public void setDisplayContrastFactor(int contrastFactor){
		this.contrastFactor = contrastFactor;
		refreshImage();
	}
	public void setCrop(Point cropBegin,Point cropEnd){
		if(cropBegin == null || cropEnd == null){
			cropRect = null;
		}else{
			cropRect = new Rectangle(
				(cropBegin.x < cropEnd.x?cropBegin.x:cropEnd.x),
				(cropBegin.y < cropEnd.y?cropBegin.y:cropEnd.y),
				Math.abs(cropBegin.x-cropEnd.x), Math.abs(cropBegin.y-cropEnd.y)
			);
		}
		repaint();
	}
	private Shape starShape;
	public void drawStar(Shape starShape){
		this.starShape = starShape;
		repaint();
	}
	public Rectangle getCrop(){
		return cropRect;
	}
	
	private Shape brushShape;
	private final float[] dash = { 3F, 3F };  
	private final Stroke dashedStroke = new BasicStroke( .5F, BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER, 3F, dash, 0F );  
	public void setBrush(Ellipse2D.Double brushShape){
		if(brushShape == null){
			this.brushShape = null;
			return;
		}
		this.brushShape = dashedStroke.createStrokedShape(brushShape);  ;
	}
	public void paint(Graphics g){
		super.paint(g);
		if(getCrop() != null){
//			Composite origComposite = ((Graphics2D)g).getComposite();
//			((Graphics2D)g).setComposite(AlphaComposite.Xor);
			g.setColor(Color.green);
			g.drawRect(getCrop().x,getCrop().y,getCrop().width,getCrop().height);
//			((Graphics2D)g).setComposite(origComposite);
		}else if(starShape != null){
			Graphics2D tempG = (Graphics2D)g.create();
			try{
				Stroke bigStroke = new BasicStroke(3.0f);
				Stroke smallStroke = new BasicStroke(1.0f);
//				tempG.scale(getZoom(), getZoom());
				Rectangle rect = starShape.getBounds();
				tempG.translate((rect.getX()+rect.getWidth()/2)*(getZoom()-1), (rect.getY()+rect.getHeight()/2)*(getZoom()-1));
				tempG.setColor(Color.black);
				tempG.setStroke(bigStroke);
				tempG.draw(starShape);
				tempG.setColor(Color.white);
				tempG.setStroke(smallStroke);
				tempG.draw(starShape);
			}finally{
				tempG.dispose();
			}
		}else if(brushShape != null){
			g.setColor(Color.green);
			((Graphics2D)g).draw(brushShape);
		}
	}

}
