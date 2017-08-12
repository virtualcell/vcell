/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Insert the type's description here.
 * Creation date: (9/3/00 11:49:20 AM)
 * @author: 
 */
public class ImagePaneModel implements java.beans.PropertyChangeListener{
	protected transient java.beans.PropertyChangeSupport propertyChange;
	private int fieldZoom = 1;
	private int fieldMode = NORMAL_MODE;
	private java.awt.Dimension fieldDimension = null;
	private java.awt.Rectangle fieldViewport = null;
	private java.awt.Color fieldBackgroundColor = java.awt.Color.black;
	private SourceDataInfo fieldSourceData = null;
	private DisplayAdapterService fieldDisplayAdapterService = null;
	private java.awt.image.BufferedImage fieldViewPortImage = null;
	//
	public static final int NORMAL_MODE = 0;
	public static final int MESH_MODE = 1;
	//
	private final int[] backgroundFillHelper = new int[2000];
//

/**
 * ImagePaneZoomableModel constructor comment.
 */
public ImagePaneModel() {
	super();
	/*
	addPropertyChangeListener("originalImage",this);
	addPropertyChangeListener("mode",this);
	addPropertyChangeListener("zoom",this);
	//Create default image
	int w = 100;
	int h = 100;
	int[] pixels = new int[w * h];
	for (int y = 0; y < h; y += 1) {
		for (int x = 0; x < w; x += 1) {
			if (y % 2 == 0) {
				if (x % 2 == 0) {
					pixels[x + (y * w)] = 0x00ff0000;
				}
			} else {
				if (x % 2 != 0) {
					pixels[x + (y * w)] = 0x00ff0000;
				}
			}
		}
	}
	memoryImageSourceNewPixels(w, h, pixels);
	*/
}


/**
 * ImagePaneZoomableModel constructor comment.
 */
public ImagePaneModel(ImagePaneModel argIPM) {
	super();

	this.fieldZoom = argIPM.fieldZoom;
	this.fieldMode = argIPM.fieldMode;
	this.fieldDimension = new Dimension(argIPM.fieldDimension);
	this.fieldViewport = new Rectangle(argIPM.fieldViewport);
	this.fieldBackgroundColor = argIPM.fieldBackgroundColor;
	this.fieldSourceData = argIPM.fieldSourceData;
	this.fieldDisplayAdapterService = new DisplayAdapterService(argIPM.fieldDisplayAdapterService);
}


/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}


/**
 * Gets the dimension property (java.awt.Dimension) value.
 * @return The dimension property value.
 */
private java.awt.Dimension calculateDimension() {
	if (getSourceData() == null) {
		return null;
	}
	int w = getScaledLength(getSourceData().getXSize());
	int h = getScaledLength(getSourceData().getYSize());
	return new java.awt.Dimension(w, h);
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/00 4:34:18 PM)
 */
public java.awt.geom.Point2D.Double calculateImagePointUnitized(java.awt.Point pixelPoint) {
	if (getDimension() == null) {
		return null;
	}
	double x = 0;
	double y = 0;
	if (getDimension().width <= 1){
		throw new RuntimeException("ImagePaneModel dimension.width=="+getDimension().width+" should be > 1");
	}
	if (getDimension().height <= 1){
		throw new RuntimeException("ImagePaneModel dimension.height=="+getDimension().height+" should be > 1");
	}
	if (fieldMode == NORMAL_MODE) {
		x = (double) pixelPoint.x / (double) getDimension().width;
		y = (double) pixelPoint.y / (double) getDimension().height;
	} else {
		x = (double) pixelPoint.x / (double) (getDimension().width-1);
		y = (double) pixelPoint.y / (double) (getDimension().height-1);
	}
	return new java.awt.geom.Point2D.Double(x, y);
}


/**
 * Insert the method's description here.
 * Creation date: (3/30/2001 12:14:15 PM)
 * @return boolean
 */
public boolean changeZoomToFillViewport() {

	if(getViewport() == null){
		return false;
	}
	int minXZoom = getViewport().width/getScaledLength(getSourceData().getXSize(),1,getMode());
	int minYZoom = getViewport().height/getScaledLength(getSourceData().getYSize(),1,getMode());
	int minZoom = Math.min(minXZoom,minYZoom);
	if(getZoom() < minZoom){
		setZoom(minZoom);
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/00 4:34:18 PM)
 */
private int convertToImageIndex(int mouseIndex) {
	mouseIndex /= fieldZoom;
	if (fieldMode == MESH_MODE) {
		mouseIndex = (mouseIndex + 1) / 2;
	}
	else if (fieldMode == NORMAL_MODE)
	{
		mouseIndex /= 2; 
	}
	return mouseIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (10/7/00 7:21:04 PM)
 * @param zoomDelta int
 */
public void deltaZoom(int zoomDelta) {
    setZoom(getZoom() + zoomDelta);
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/00 2:14:59 PM)
 */
private void fillBackGround_NOT_USED(int[] destination,int subImageStartX,int subImageSizeX ,int subImageStartY,int subImageSizeY) {
	//Maybe we will need this, but just try fill everything first
	//Whole top rows
	int yIndex = 0;
	for(int i = 0;i < subImageStartY;i+= 1){
		System.arraycopy(backgroundFillHelper,0,destination,yIndex,fieldViewport.width);
		yIndex+= fieldViewport.width;
	}
	int lastY = subImageStartY+subImageSizeY;
	//middle rows
	for(int i = subImageStartY;i < lastY;i+= 1){
		//left
		//middle
		//right
		//yIndex+= fieldViewport.width;
		
	}	
	//
	//Whole bottom rows
	for(int i = lastY;i < fieldViewport.height;i+= 1){
		System.arraycopy(backgroundFillHelper,0,destination,yIndex,fieldViewport.width);
		yIndex+= fieldViewport.width;
	}
	
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.beans.PropertyChangeEvent evt) {
	getPropertyChange().firePropertyChange(evt);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, int oldValue, int newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, java.lang.Object oldValue, java.lang.Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(java.lang.String propertyName, boolean oldValue, boolean newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}


/**
 * Gets the backgroundColor property (java.awt.Color) value.
 * @return The backgroundColor property value.
 * @see #setBackgroundColor
 */
public java.awt.Color getBackgroundColor() {
	return fieldBackgroundColor;
}


/**
 * Gets the dimension property (java.awt.Dimension) value.
 * @return The dimension property value.
 */
public java.awt.Dimension getDimension() {
    return fieldDimension;
}


/**
 * Gets the displayAdapterService property (cbit.image.DisplayAdapterService) value.
 * @return The displayAdapterService property value.
 * @see #setDisplayAdapterService
 */
public DisplayAdapterService getDisplayAdapterService() {
	return fieldDisplayAdapterService;
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/00 4:34:18 PM)
 */
public java.awt.Point getImagePoint(java.awt.Point pixelPoint) {
    if (!isPointOnImage(pixelPoint)) {
        return null;
    }
    return new java.awt.Point(convertToImageIndex(pixelPoint.x), convertToImageIndex(pixelPoint.y));
}


/**
 * Gets the mode property (int) value.
 * @return The mode property value.
 * @see #setMode
 */
public int getMode() {
	return fieldMode;
}


/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
	if (propertyChange == null) {
		propertyChange = new java.beans.PropertyChangeSupport(this);
	};
	return propertyChange;
}


/**
 * Insert the method's description here.
 * Creation date: (10/21/00 1:04:07 PM)
 * @return int
 * @param unScaledValue int
 */
public int getScaledLength(int unScaledLength) {
	//
	return getScaledLength(unScaledLength,getZoom(),getMode());
	/*
	int result = 0;
	if (getMode() == MESH_MODE) {
		result = getZoom() * 2 * (unScaledLength - 1);
	} else if (getMode() == NORMAL_MODE) {
		result = getZoom() * unScaledLength;
	}
	return result;
	*/
}


/**
 * Insert the method's description here.
 * Creation date: (10/21/00 1:04:07 PM)
 * @return int
 * @param unScaledValue int
 */
private int getScaledLength(int unScaledLength,int zoom,int mode) {
	int result = 0;
	if (mode == MESH_MODE) {
		//
		// if unScaledLength is 1, then just stretching display of y and z axis in 1-D, and z axis in 2-D.
		//
		if (unScaledLength == 1){
			result = getZoom() * 5;
		}else{
			result = zoom * 2 * (unScaledLength - 1);
		}
	} else if (mode == NORMAL_MODE) {
		result = zoom * 2 * unScaledLength;
	}
	return result;
}


/**
 * Gets the sourceData property (cbit.image.SourceDataInfo) value.
 * @return The sourceData property value.
 * @see #setSourceData
 */
public SourceDataInfo getSourceData() {
	return fieldSourceData;
}


/**
 * Gets the viewport property (java.awt.Rectangle) value.
 * @return The viewport property value.
 * @see #setViewport
 */
public java.awt.Rectangle getViewport() {
	return fieldViewport;
}


/**
 * Gets the viewPortImage property (java.awt.image.BufferedImage) value.
 * @return The viewPortImage property value.
 */
public java.awt.image.BufferedImage getViewPortImage() {
	return fieldViewPortImage;
}


/**
 * Gets the zoom property (int) value.
 * @return The zoom property value.
 * @see #setZoom
 */
public int getZoom() {
	return fieldZoom;
}


/**
 * The hasListeners method was generated to support the propertyChange field.
 */
public synchronized boolean hasListeners(java.lang.String propertyName) {
	return getPropertyChange().hasListeners(propertyName);
}


/**
 * Insert the method's description here.
 * Creation date: (11/11/2000 1:55:35 PM)
 * @return boolean
 * @param pixelPoint java.awt.Point
 */
public boolean isPointOnImage(Point pixelPoint) {
	if ((getDimension() == null) || pixelPoint.x < 0 || pixelPoint.x >= getDimension().width || pixelPoint.y < 0 || pixelPoint.y >= getDimension().height) {
		return false;
	}
	return true;
}


/**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   and the property that has changed.
     */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
    //updateViewPortImage();
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}


/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.lang.String propertyName, java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(propertyName, listener);
}


/**
 * Sets the backgroundColor property (java.awt.Color) value.
 * @param backgroundColor The new value for the property.
 * @see #getBackgroundColor
 */
public void setBackgroundColor(java.awt.Color backgroundColor) {
    if (backgroundColor == null) {
        return;
    }
    if (fieldBackgroundColor != null && backgroundColor.equals(fieldBackgroundColor)) {
        return;
    }
    java.awt.Color oldValue = fieldBackgroundColor;
    fieldBackgroundColor = backgroundColor;
    if (fieldBackgroundColor != null) {
        int color = fieldBackgroundColor.getRGB();
        for (int i = 0; i < backgroundFillHelper.length; i += 1) {
            backgroundFillHelper[i] = color;
        }
    }
    firePropertyChange("backgroundColor", oldValue, backgroundColor);
}


/**
 * Gets the dimension property (java.awt.Dimension) value.
 * @return The dimension property value.
 */
private void setDimension(java.awt.Dimension dimension) {
	if (dimension == fieldDimension) {
		return;
	}
	if (dimension != null && fieldDimension != null) {
		if (dimension.equals(fieldDimension)) {
			return;
		}
	}
	java.awt.Dimension oldValue = fieldDimension;
	fieldDimension = dimension;
	//
	updateViewPortImage();
	//
	firePropertyChange("dimension", oldValue, fieldDimension);
}


/**
 * Sets the displayAdapterService property (cbit.image.DisplayAdapterService) value.
 * @param displayAdapterService The new value for the property.
 * @see #getDisplayAdapterService
 */
public void setDisplayAdapterService(DisplayAdapterService displayAdapterService) {
	DisplayAdapterService oldValue = fieldDisplayAdapterService;
	if(oldValue != null){
		oldValue.removePropertyChangeListener(this);
	}
	fieldDisplayAdapterService = displayAdapterService;
	if(fieldDisplayAdapterService != null){
		fieldDisplayAdapterService.addPropertyChangeListener(this);
	}
	updateViewPortImage();
	firePropertyChange("displayAdapterService", oldValue, displayAdapterService);
}


/**
 * Sets the mode property (int) value.
 * @param mode The new value for the property.
 * @see #getMode
 */
public void setMode(int mode) {
	if (mode != NORMAL_MODE && mode != MESH_MODE) {
		return;
	}
	if (mode == fieldMode) {
		return;
	}
	int oldValue = fieldMode;
	fieldMode = mode;
	setDimension(calculateDimension());
	firePropertyChange("mode", oldValue, fieldMode);
}


/**
 * Sets the sourceData property (cbit.image.SourceDataInfo) value.
 * @param sourceData The new value for the property.
 * @see #getSourceData
 */
public void setSourceData(SourceDataInfo sourceData) {
	SourceDataInfo oldValue = fieldSourceData;
	fieldSourceData = sourceData;
	Dimension beforeDimension = fieldDimension;
	setDimension(calculateDimension());
	if (fieldSourceData != null && fieldSourceData.isCellCentered())
	{
		fieldMode = NORMAL_MODE;
	}
	if(beforeDimension == getDimension()){
		//Do this since dimension didn't
		if(oldValue != null && sourceData != null && !oldValue.getOrigin().equals(sourceData.getOrigin())){
			//Dimension should be turned into rectangle and all code that used it cleaned up
			firePropertyChange("dimension", null, fieldDimension);	
		}
		//updateViewPortImage();
	}
	updateViewPortImage();
	firePropertyChange("sourceData", oldValue, sourceData);
}


/**
 * Sets the viewport property (java.awt.Rectangle) value.
 * @param viewport The new value for the property.
 * @see #getViewport
 */
public void setViewport(java.awt.Rectangle viewport) {
	if (viewport == fieldViewport) {
		return;
	}
	if (viewport != null) {
	    if(viewport.width <= 0 || viewport.height <= 0){
		    return;
	    }
		if (fieldViewport != null) {
			if (viewport.equals(fieldViewport)) {
				return;
			}
		}
	}
	java.awt.Rectangle oldValue = fieldViewport;
	fieldViewport = viewport;
	//
	if (fieldViewport != null) {
		if (oldValue == null || oldValue.width != fieldViewport.width || oldValue.height != fieldViewport.height) {
			fieldViewPortImage = new BufferedImage(fieldViewport.width, fieldViewport.height, java.awt.image.BufferedImage.TYPE_INT_RGB);
		}
	} else {
		setViewPortImage(null);
	}
	updateViewPortImage();
	//
	firePropertyChange("viewport", oldValue, viewport);
}


/**
 * Gets the viewPortImage property (java.awt.image.BufferedImage) value.
 * @return The viewPortImage property value.
 */
private void setViewPortImage(BufferedImage viewPortImage) {
	BufferedImage oldValue = fieldViewPortImage;
	fieldViewPortImage = viewPortImage;
	firePropertyChange("viewPortImage", null, fieldViewPortImage);
}


/**
 * Sets the zoom property (int) value.
 * @param zoom The new value for the property.
 * @see #getZoom
 */
public void setZoom(int zoom) {
	if (zoom == fieldZoom) {
		return;
	}
	if (zoom < 1) {
		zoom = 1;
	}
	int oldValue = fieldZoom;
	fieldZoom = zoom;
	setDimension(calculateDimension());
	firePropertyChange("zoom", oldValue, fieldZoom);
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/00 6:30:27 PM)
 */
private void updateRowFromColorIndex(int[] viewport, int viewPortImageXStart, int viewPortImageXEnd, int modFactor, int modFactorCompare, int currentSourceIndex, int currentViewportIndex) {
	//
	//int[] colorMap = fieldDisplayAdapterService.getActiveColorModel();
	if (getSourceData().getData() instanceof int[]) {
		int[] source = (int[]) getSourceData().getData();
		int sourceXIncrement = fieldSourceData.getXIncrement();
		for (int x = viewPortImageXStart; x < viewPortImageXEnd; x += 1) {
			//
			viewport[currentViewportIndex] = fieldDisplayAdapterService.getColorFromIndex(source[currentSourceIndex]);//colorMap[source[currentSourceIndex]];
			//
			modFactor += 1;
			if (modFactor == modFactorCompare) {
				modFactor = 0;
				currentSourceIndex += sourceXIncrement;
			}
			currentViewportIndex += 1;
		}
	} else if (getSourceData().getData() instanceof byte[]) {
		byte[] source = (byte[]) getSourceData().getData();
		int sourceXIncrement = fieldSourceData.getXIncrement();
		for (int x = viewPortImageXStart; x < viewPortImageXEnd; x += 1) {
			//
			viewport[currentViewportIndex] = fieldDisplayAdapterService.getColorFromIndex((int) (source[currentSourceIndex] & 0x000000FF));//colorMap[(int) (source[currentSourceIndex] & 0xFF)];
			//
			modFactor += 1;
			if (modFactor == modFactorCompare) {
				modFactor = 0;
				currentSourceIndex += sourceXIncrement;
			}
			currentViewportIndex += 1;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/00 6:30:27 PM)
 */
private void updateRowFromIntRGB(int[] viewport, int viewPortImageXStart, int viewPortImageXEnd, int modFactor, int modFactorCompare, int currentSourceIndex, int currentViewportIndex) {
    //
    int[] source = (int[]) getSourceData().getData();
    int sourceXIncrement = fieldSourceData.getXIncrement();
    for (int x = viewPortImageXStart; x < viewPortImageXEnd; x += 1) {
        //
        viewport[currentViewportIndex] = (source != null?source[currentSourceIndex]:0);
        //
        modFactor += 1;
        if (modFactor == modFactorCompare) {
            modFactor = 0;
            currentSourceIndex += sourceXIncrement;
        }
        currentViewportIndex += 1;
    }
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/00 6:30:27 PM)
 */
private void updateRowFromRawValues(int[] viewport, int viewPortImageXStart, int viewPortImageXEnd, int modFactor, int modFactorCompare, int currentSourceIndex, int currentViewportIndex) {
	//
	double[] source = (double[]) getSourceData().getData();
	int sourceXIncrement = fieldSourceData.getXIncrement();
	for (int x = viewPortImageXStart; x < viewPortImageXEnd; x += 1) {
		//
		viewport[currentViewportIndex] =
			(source != null?fieldDisplayAdapterService.getColorFromValue(source[currentSourceIndex]):fieldDisplayAdapterService.getNullColor());
		//
		modFactor += 1;
		if (modFactor == modFactorCompare) {
			modFactor = 0;
			currentSourceIndex += sourceXIncrement;
		}
		currentViewportIndex += 1;
	}
}


/**
 * Gets the zoomImage property (java.awt.Image) value.
 * @return The zoomImage property value.
 */
public void updateViewPortImage() {
	Dimension currentDimension = getDimension();
	boolean bUpdateImage = true;
	if (getSourceData() == null || (getDisplayAdapterService() == null && getSourceData().needsColorConversion()) || getViewport() == null || currentDimension == null) {
		//We don't have enough info to update image
		return;
	}
	//Enforce image always fills viewport
	//if(changeZoomToFillViewport() == true){
		//return;
	//}
	//
	//Make a new viewPort image
	//Find out where the Viewport points to in the sourceData and the final DimensionedData data
	//Account for the image edges being inside the viewport
	int viewPortImageXStart = viewPortImageIndex(fieldViewport.x, currentDimension.width);
	int viewPortXStart = viewPortIndex(fieldViewport.x, fieldViewport.width, currentDimension.width);
	int viewPortImageXEnd = viewPortImageXStart + Math.min(currentDimension.width - viewPortImageXStart, fieldViewport.width - viewPortXStart);
	//
	int viewPortImageYStart = viewPortImageIndex(fieldViewport.y, currentDimension.height);
	int viewPortYStart = viewPortIndex(fieldViewport.y, fieldViewport.height, currentDimension.height);
	int viewPortImageYEnd = viewPortImageYStart + Math.min(currentDimension.height - viewPortImageYStart, fieldViewport.height - viewPortYStart);
	//
	//Calculate the starting index into the sourceData and DimensionedData
	int viewPortStartIndex = viewPortXStart + viewPortYStart * fieldViewport.width;
	int sourceStartIndex = fieldSourceData.getStartIndex() + convertToImageIndex(viewPortImageXStart) * fieldSourceData.getXIncrement() + convertToImageIndex(viewPortImageYStart) * fieldSourceData.getYIncrement();
	//
	//The viewPortImage internal buffer we will write to directly
	//We can do this because our bufferimage was made to have an int[] as its internal buffer
	int[] viewport = ((DataBufferInt) (getViewPortImage().getRaster().getDataBuffer())).getData();
	//fill viewport with background color
	java.util.Arrays.fill(viewport,getBackgroundColor().getRGB());
	//
	if (viewPortImageXStart != -1 && viewPortXStart != -1 && viewPortImageYStart != -1 && viewPortYStart != -1) {
		//The image is visible inside the viewport so fill it in
		int lastRowIndex = 0;
		int modFactorCompareX = 0;
		int modFactorX = 0;
		int modFactorCompareY = 0;
		if (fieldMode == NORMAL_MODE) {
			modFactorCompareX = fieldZoom * 2;
			modFactorX = viewPortImageXStart % modFactorCompareX;
			modFactorCompareY = fieldZoom * 2;
		} else if (fieldMode == MESH_MODE) {
			if (getSourceData().getXSize()==1){
				modFactorCompareX = 5000; // guaranteed not to use next column
				modFactorX = viewPortImageXStart % modFactorCompareX;
			}else{
				modFactorCompareX = 2 * fieldZoom;
				modFactorX = (viewPortImageXStart + fieldZoom) % modFactorCompareX;
			}
			if (getSourceData().getYSize()==1){
				modFactorCompareY = 5000; // guaranteed not to use next row
			}else{
				modFactorCompareY = 2 * fieldZoom;
			}
		}
		//
		boolean bMakeNewRow = true;
		for (int y = viewPortImageYStart; y < viewPortImageYEnd; y += 1) {
			//
			if (fieldMode == NORMAL_MODE) {
				bMakeNewRow = (((y % modFactorCompareY) == 0) || (y == viewPortImageYStart));
			} else if (fieldMode == MESH_MODE) {
				bMakeNewRow = ((((y + fieldZoom) % modFactorCompareY) == 0) || (y == viewPortImageYStart));
			}
			//
			if (bMakeNewRow) {
				switch (getSourceData().getType()) {
					case SourceDataInfo.INT_RGB_TYPE :
						updateRowFromIntRGB(viewport, viewPortImageXStart, viewPortImageXEnd, modFactorX, modFactorCompareX, sourceStartIndex, viewPortStartIndex);
						break;
					case SourceDataInfo.INDEX_TYPE :
						updateRowFromColorIndex(viewport, viewPortImageXStart, viewPortImageXEnd, modFactorX, modFactorCompareX, sourceStartIndex, viewPortStartIndex);
						break;
					case SourceDataInfo.RAW_VALUE_TYPE :
						updateRowFromRawValues(viewport, viewPortImageXStart, viewPortImageXEnd, modFactorX, modFactorCompareX, sourceStartIndex, viewPortStartIndex);
						break;
				}
				//
				lastRowIndex = viewPortStartIndex;
				sourceStartIndex += fieldSourceData.getYIncrement();
			} else {
				System.arraycopy(viewport, lastRowIndex, viewport, viewPortStartIndex, viewPortImageXEnd - viewPortImageXStart);
			}
			//
			viewPortStartIndex += fieldViewport.width;
		}
		//
		setViewPortImage(getViewPortImage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/00 2:18:32 PM)
 * @return int
 * @param originIndex int
 */
private int viewPortImageIndex(int viewDelta,int viewSize) {
	if(Math.abs(viewDelta) >= viewSize){
		//Completely outside viewport
		return -1;
	}else{
		if(viewDelta < 0){
			return 0;
		}else{
			return viewDelta;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/6/00 2:18:32 PM)
 * @return int
 * @param originIndex int
 */
private int viewPortIndex(int viewDelta, int viewPortSize, int viewSize) {
	if (viewDelta < 0) {
		if (Math.abs(viewDelta) >= viewPortSize) {
			//Completely outside viewport
			return -1;
		}
		return -viewDelta;
	} else {
		if (viewDelta >= viewSize) {
			//Completely outside viewport
			return -1;
		}
		return 0;
	}
}
}
