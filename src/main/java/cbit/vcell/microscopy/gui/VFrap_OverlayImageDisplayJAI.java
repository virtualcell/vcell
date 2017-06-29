/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy.gui;

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
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.CompositeDescriptor;
import javax.media.jai.operator.ExtremaDescriptor;
import javax.media.jai.operator.LookupDescriptor;
import javax.media.jai.operator.RescaleDescriptor;
import javax.media.jai.operator.ScaleDescriptor;

import org.vcell.util.Range;

import com.sun.media.jai.widget.DisplayJAI;

import cbit.vcell.geometry.gui.OverlayEditorPanelJAI.BrushToolHelper.BrushRefresh;

/**
 */
@SuppressWarnings("serial")
public class VFrap_OverlayImageDisplayJAI extends DisplayJAI implements BrushRefresh{
	private BufferedImage underlyingImage = null;
	private BufferedImage highlightImage = null;
	private RenderedImage alphaImageUnderlying = null;
	private RenderedImage alphaImageHightlight = null;
	private short[] highlightImageWritebackBuffer = null;
	private float zoom = 1.0f;
	
	private int contrastFactor = 0;
	private static final int CONTRAST_BOUND = 4;
	private Rectangle cropRect = null;
	
	private static final double SCALE_MAX = Math.pow(2, 8)-1;

	private Range minmaxPixelValues = null;
	
	public VFrap_OverlayImageDisplayJAI(){
		super();
	}
	
	/**
	 * Method setUnderlyingImage.
	 * @param argUnderlyingImage BufferedImage
	 */
	public void setUnderlyingImage(BufferedImage argUnderlyingImage,boolean bNew,Range argMinMaxPixelValues){
		this.minmaxPixelValues = argMinMaxPixelValues;
		this.underlyingImage = argUnderlyingImage;
		if(bNew){
			resetGUI();
		}
		refreshImage();
	}
	private void resetGUI(){
		zoom = 1.0f;
		contrastFactor = 0;
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
	
	/**
	 * Method getHighlightImageWritebackImageBuffer.
	 * @return short[]
	 */
	public short[] getHighlightImageWritebackImageBuffer(){
		return highlightImageWritebackBuffer;
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
	private RenderedImage makeAlpha(RenderedImage src, float b) {
	      // get color band
	      int band = src.getColorModel().getNumColorComponents();

	      // ignore alpha channel
	      band = band > 3 ? 3 : band;

	      // make alpha channel paramenter

	      Byte[] bandValues = new Byte[band];
	      for ( int i = 0 ; i < band ; i++ ) {
	             bandValues[i] = new Byte((byte)(b*SCALE_MAX));
	      }

	        // make alpha channel paramenter
	        ParameterBlock pb = new ParameterBlock();
	        pb.add((float)src.getWidth());
	        pb.add((float)src.getHeight());
	        pb.add(bandValues);

	        // make alpha channel
	        return JAI.create("constant", pb, null);
	 }



	public void refreshImage(){
		if (underlyingImage!=null && highlightImage!=null){
			PlanarImage sourceOverlay = null;
			alphaImageUnderlying = makeAlpha(underlyingImage, .6f);
			alphaImageHightlight = makeAlpha(highlightImage, .3f);
			RenderedImage contrastEnhancedUnderlyingImage = underlyingImage;
			if(contrastFactor > 0){
				//Contrast stretch
				double[][] minmaxArr = null;
				if(minmaxPixelValues != null){
					minmaxArr = new double[][] {{minmaxPixelValues.getMin()},{minmaxPixelValues.getMax()}};
				}else{
					minmaxArr = (double[][])ExtremaDescriptor.create(underlyingImage, null, 1, 1, false, 1, null).getProperty("extrema");
				}
				if((minmaxArr[1][0]-minmaxArr[0][0]) != 0){
					double offset = (SCALE_MAX*minmaxArr[0][0])/(minmaxArr[0][0]-minmaxArr[1][0]);
					double scale = (SCALE_MAX)/(minmaxArr[1][0]-minmaxArr[0][0]);
					contrastEnhancedUnderlyingImage =
						RescaleDescriptor.create(underlyingImage,new double[]{scale},new double[]{offset},null);
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
					LookupTableJAI table = new LookupTableJAI(tableData);
					contrastEnhancedUnderlyingImage = LookupDescriptor.create(contrastEnhancedUnderlyingImage, table, null);
				}
			}
			
			
		     sourceOverlay =
				CompositeDescriptor.create(
						contrastEnhancedUnderlyingImage, highlightImage,
					alphaImageUnderlying, alphaImageHightlight,
					false, CompositeDescriptor.NO_DESTINATION_ALPHA, null);

		     sourceOverlay =
				ScaleDescriptor.create(
					sourceOverlay, zoom, zoom, 0f, 0f,
					Interpolation.getInstance(Interpolation.INTERP_NEAREST),null);

			set(sourceOverlay, 0, 0);
		}else{
			set(new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB),0,0);
		}
	}
		
	public void drawHighlight(int x, int y, int radius, boolean erase,Color highlightColor,Point lastHighlightPoint){
		if(lastHighlightPoint == null){
			lastHighlightPoint = new Point(x,y);
		}
		Color drawingColor = highlightColor;
		if (erase){
			drawingColor = Color.black;
		}
		float zoom = getZoom();
		if (getHighlightImage()!=null){
			Graphics graphicsUnscaled = getHighlightImage().getGraphics();
			graphicsUnscaled.setColor(drawingColor);
			int size = radius;//(int)(radius/zoom/*+radius/zoom*/+1);
			//-----Interpolate between paint points for continuous lines
			double currentX = lastHighlightPoint.x;
			double currentY = lastHighlightPoint.y;
			int dx = x-lastHighlightPoint.x;
			int dy = y-lastHighlightPoint.y;
			double delta = 1.0/(dx==0 && dy==0?1.0:Math.max(Math.abs(dx), Math.abs(dy)));
			int lastX = (int)currentX;
			int lastY = (int)currentY;
			for(int i=0;i<=Math.max(Math.abs(dx), Math.abs(dy));i++){
				if(i== 0 || (int)currentX != lastX || (int)currentY != lastY){
					lastX = (int)currentX;
					lastY = (int)currentY;
					if(graphicsUnscaled != null){
						if(size >= 2){
							graphicsUnscaled.fillOval((int)((lastX/zoom-(size/2))),(int)((lastY/zoom-(size/2))), size, size);
						}else{
							graphicsUnscaled.drawRect((int)((lastX/zoom-(size/2))),(int)((lastY/zoom-(size/2))), size, size);
						}
					}
				}
				currentX = lastHighlightPoint.x+(i+1)*delta*dx;
				currentY = lastHighlightPoint.y+(i+1)*delta*dy;
			}
			//------
			graphicsUnscaled.dispose();
//			graphicsScaled.dispose();
			refreshImage();
		}
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
			g.setColor(Color.green);
			g.drawRect(getCrop().x,getCrop().y,getCrop().width,getCrop().height);
		}else if(brushShape != null){
			g.setColor(Color.green);
			((Graphics2D)g).draw(brushShape);
		}
	}

}

