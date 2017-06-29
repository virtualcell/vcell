/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import cbit.image.AxisCommand;
import cbit.image.Command;
import cbit.image.CommandCache;
import cbit.image.CommandListener;
/**
 * This type was created in VisualAge.
 */
public class ImageContainerPanelTool implements CommandListener, MouseListener, MouseMotionListener {
	protected ImageContainerPanel imageContainerPanel = null;
	protected ImageContainer imageContainer = null;
	protected CommandCache commandCache = null;
	private Point beginPick = null;
	private Point endPick = null;
	private boolean bPickMode = false;

	protected transient java.beans.PropertyChangeSupport propertyChange;
	private org.vcell.util.CoordinateIndex fieldClickedPoint = new org.vcell.util.CoordinateIndex();
	private org.vcell.util.CoordinateIndex fieldBeginLine = new org.vcell.util.CoordinateIndex();
	private org.vcell.util.CoordinateIndex fieldEndLine = new org.vcell.util.CoordinateIndex();
	private int fieldSliceNumber = 0;
	private int fieldSlicePlane = 2;
/**
 * ImageContainerTool constructor comment.
 */
public ImageContainerPanelTool(ImageContainerPanel imageContainerPanel, ImageContainer imageContainer) {
	this.imageContainerPanel = imageContainerPanel;
	imageContainerPanel.getImagePaneScroller().getImagePaneView().addMouseListener(this);
	imageContainerPanel.getImagePaneScroller().getImagePaneView().addMouseMotionListener(this);
	this.imageContainer = imageContainer;
	commandCache = new CommandCache(this);
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * This method was created in VisualAge.
 * @param bBusy boolean
 */
public void busy(boolean bBusy) {
}
/**
 * This method was created in VisualAge.
 * @param command cbit.image.Command
 */
public void command(Command command) {
	if (command instanceof AxisCommand){
		setAxis(((AxisCommand)command).getAxis());
	}else if (command instanceof SliceCommand){
		setSlice(((SliceCommand)command).getSliceOffset());
	}
}
/**
 * This method was created in VisualAge.
 */
public void decrementSlice() {
	commandCache.queueCommand(new SliceCommand(-1));
}
/**
 * This method was created in VisualAge.
 */
public void decrementSlice10() {
	commandCache.queueCommand(new SliceCommand(-10));
}
/**
 * This method was created in VisualAge.
 */
protected void finalize() {
	commandCache.killThread();
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Gets the beginLine property (cbit.vcell.math.CoordinateIndex) value.
 * @return The beginLine property value.
 * @see #setBeginLine
 */
public org.vcell.util.CoordinateIndex getBeginLine() {
	return fieldBeginLine;
}
/**
 * Gets the clickedPoint property (cbit.vcell.math.CoordinateIndex) value.
 * @return The clickedPoint property value.
 * @see #setClickedPoint
 */
public org.vcell.util.CoordinateIndex getClickedPoint() {
	return fieldClickedPoint;
}
/**
 * Gets the endLine property (cbit.vcell.math.CoordinateIndex) value.
 * @return The endLine property value.
 * @see #setEndLine
 */
public org.vcell.util.CoordinateIndex getEndLine() {
	return fieldEndLine;
}
/**
 * This method was created in VisualAge.
 * @return cbit.image.ImageContainer
 */
public ImageContainer getImageContainer() {
	return imageContainer;
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
 * Gets the sliceNumber property (int) value.
 * @return The sliceNumber property value.
 * @see #setSliceNumber
 */
public int getSliceNumber() {
	return fieldSliceNumber;
}
/**
 * Gets the slicePlane property (int) value.
 * @return The slicePlane property value.
 * @see #setSlicePlane
 */
public int getSlicePlane() {
	return fieldSlicePlane;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * This method was created in VisualAge.
 */
public void incrementSlice() {
	commandCache.queueCommand(new SliceCommand(+1));
}
/**
 * This method was created in VisualAge.
 */
public void incrementSlice10() {
	commandCache.queueCommand(new SliceCommand(+10));
}
/**
 * mouseClicked method comment.
 */
public void mouseClicked(java.awt.event.MouseEvent e) {
	try {
		Point currentPick = new Point(e.getX(), e.getY());
		Point currentImage = imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(currentPick);
		if(currentImage == null){
			return;
		}
		org.vcell.util.CoordinateIndex currentCI = getImageContainer().getCoordinateIndexFromDisplay(currentImage.x, currentImage.y);
		setClickedPoint(currentCI);
	} catch (Exception exc) {
		exc.printStackTrace();
	}
}
/**
 * mouseDragged method comment.
 */
public void mouseDragged(java.awt.event.MouseEvent event) {
	if (imageContainerPanel.getSpatialPlotEnabled()){
		if (beginPick==null){
			System.out.println("ImageContainerPanelTool.mouseMoved(), in pick mode but beginPick is null");
			return;
		}
		if (endPick==null){
			System.out.println("ImageContainerPanelTool.mouseMoved(), in pick mode but endPick is null");
			return;
		}
		java.awt.Graphics canvasGc = imageContainerPanel.getImagePaneScroller().getImagePaneView().getGraphics();
		canvasGc.setXORMode(Color.white);
		canvasGc.setColor(Color.red);
		if (imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(new Point(event.getX(),event.getY()))!=null){	
			canvasGc.drawLine(beginPick.x,beginPick.y,endPick.x,endPick.y);
			endPick.x = event.getX();
			endPick.y = event.getY();
			canvasGc.drawLine(beginPick.x,beginPick.y,endPick.x,endPick.y);
		}	
	}	
	refreshStatus(event.getPoint());
}
/**
 * mouseEntered method comment.
 */
public void mouseEntered(java.awt.event.MouseEvent e) {
}
/**
 * mouseExited method comment.
 */
public void mouseExited(java.awt.event.MouseEvent e) {
}
/**
 * mouseMoved method comment.
 */
public void mouseMoved(java.awt.event.MouseEvent event) {
	refreshStatus(event.getPoint());
}
/**
 * mousePressed method comment.
 */
public void mousePressed(java.awt.event.MouseEvent event) {
	if (imageContainerPanel.getSpatialPlotEnabled()){
		//
		// if old line, erase it first
		//
		if ((beginPick != null) && (endPick != null)){
			imageContainerPanel.repaintImage();
		}
		beginPick = null;
		endPick = null;
					
		if (imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(new Point(event.getX(),event.getY()))!= null){
			bPickMode = true;
			beginPick = new Point(event.getX(),event.getY());
			endPick = new Point(event.getX(),event.getY());
		}else{
//			getStatusLabel().setText("pick missed");
//			plotFrame.setVisible(false);
			return;
		}		
		java.awt.Graphics canvasGc = imageContainerPanel.getImagePaneScroller().getImagePaneView().getGraphics();
		canvasGc.setXORMode(Color.white);
		canvasGc.setColor(Color.red);
		canvasGc.drawLine(beginPick.x,beginPick.y,endPick.x,endPick.y);
	}	
}
/**
 * mouseReleased method comment.
 */
public void mouseReleased(java.awt.event.MouseEvent event) {
	if (imageContainerPanel.getSpatialPlotEnabled()){
		if (bPickMode){
			bPickMode = false;
			//
			// givin screen coordinates, get coordinates of current projection corrected for current zoom
			//
			Point beginImage = imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(beginPick);

			//
			//?????????change this when you rubber band the lines
			//
			//
			endPick = new Point(event.getX(),event.getY());
			Point endImage = imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(endPick);
			if (endImage==null){
				//
				// end point is outside the image;
				//
				imageContainerPanel.refreshImage();
				return;
			}
			try {
				//
				// get real image coordinates from display coordinates
				//
				org.vcell.util.CoordinateIndex beginCI = getImageContainer().getCoordinateIndexFromDisplay(beginImage.x,beginImage.y);
				org.vcell.util.CoordinateIndex endCI = getImageContainer().getCoordinateIndexFromDisplay(endImage.x,endImage.y);
				cbit.plot.PlotData lineScan = getImageContainer().getLineScan(beginImage,endImage);
				String beginCoordString = getImageContainer().getCoordinateString(beginCI.x,beginCI.y,beginCI.z);
				String endCoordString = getImageContainer().getCoordinateString(endCI.x,endCI.y,endCI.z);
				if (lineScan == null){
					System.out.println("error getting line scan from "+beginCoordString+" to "+endCoordString);
					return;
				}
			// everything OK so update bound fields
				setBeginLine(beginCI);
				setEndLine(endCI);
			//
				//plotFrame.setTitle("line scan of "+getImageContainer().getValueLabel()+" from "+beginCoordString+" to "+endCoordString);
				//plotFrame.setXLabel(getImageContainer().getDisplacementLabel());
				//String yLabel = getImageContainer().getValueLabel();
				//if (getImageContainer().getUnits().length()>0){
					//yLabel += " ("+getImageContainer().getUnits()+")";
				//}
				//plotFrame.setYLabel(yLabel);
				//plotFrame.setPlotData(lineScan);
				//plotFrame.setVisible(true);
			}catch (Exception e){
				e.printStackTrace();
			}
		}	
	}	
}
/**
 * This method was created by a SmartGuide.
 */
public void refreshStatus(java.awt.Point canvasPoint) {
	java.awt.Point imagePoint = imageContainerPanel.getImagePaneScroller().getImagePaneView().getImagePoint(canvasPoint);
	String statusText = new String("");
	double value;
	org.vcell.util.CoordinateIndex ci;
	if (imagePoint!=null){	
		try {
			ci = getImageContainer().getCoordinateIndexFromDisplay(imagePoint.x,imagePoint.y);
			if (ci==null){
				statusText="";
			}else{
				statusText = getImageContainer().getPointString(ci.x,ci.y,ci.z);
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
			imageContainerPanel.setStatusText("error communicating with the DataSetController");
			return;
		}			
	}
	imageContainerPanel.setStatusText(statusText);
	return;
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
	getPropertyChange().removePropertyChangeListener(listener);
}
/**
 * This method was created in VisualAge.
 * @param axis int
 */
private void setAxis(int axis) {
	try {
		getImageContainer().setImagePlane(axis,getImageContainer().getSlice());
		imageContainerPanel.setSliceLabelText(Integer.toString(getImageContainer().getSlice()));
		imageContainerPanel.refreshImage();
	// also the bound fields
		setSlicePlane(axis);
		setSliceNumber(getImageContainer().getSlice());
	//
	}catch (Exception e){
		e.printStackTrace(System.out);
	}		
}
/**
 * This method was created in VisualAge.
 */
public void setAxisX() {
	commandCache.queueCommand(new AxisCommand(ImageContainer.X_AXIS));
}
/**
 * This method was created in VisualAge.
 */
public void setAxisY() {
	commandCache.queueCommand(new AxisCommand(ImageContainer.Y_AXIS));
}
/**
 * This method was created in VisualAge.
 */
public void setAxisZ() {
	commandCache.queueCommand(new AxisCommand(ImageContainer.Z_AXIS));
}
/**
 * Sets the beginLine property (cbit.vcell.math.CoordinateIndex) value.
 * @param beginLine The new value for the property.
 * @see #getBeginLine
 */
private void setBeginLine(org.vcell.util.CoordinateIndex beginLine) {
	fieldBeginLine = beginLine;
}
/**
 * Sets the clickedPoint property (cbit.vcell.math.CoordinateIndex) value.
 * @param clickedPoint The new value for the property.
 * @see #getClickedPoint
 */
private void setClickedPoint(org.vcell.util.CoordinateIndex clickedPoint) {
	org.vcell.util.CoordinateIndex oldValue = fieldClickedPoint;
	fieldClickedPoint = clickedPoint;
	firePropertyChange("clickedPoint", oldValue, clickedPoint);
}
/**
 * Sets the endLine property (cbit.vcell.math.CoordinateIndex) value.
 * @param endLine The new value for the property.
 * @see #getEndLine
 */
private void setEndLine(org.vcell.util.CoordinateIndex endLine) {
	org.vcell.util.CoordinateIndex oldValue = fieldEndLine;
	fieldEndLine = endLine;
	firePropertyChange("endLine", oldValue, endLine);
}
/**
 * This method was created in VisualAge.
 * @param imageContainer cbit.image.ImageContainer
 */
public void setImageContainer(ImageContainer imageContainer) {
	this.imageContainer = imageContainer;
}
/**
 * This method was created in VisualAge.
 * @param offset int
 */
private void setSlice(int offset) {
	try {
		int currSlice = imageContainer.getSlice();
		imageContainer.setImagePlane(imageContainer.getNormalAxis(),imageContainer.getSlice()+offset);
		if (imageContainer.getSlice() == currSlice){
			return;
		}
		imageContainerPanel.setSliceLabelText(Integer.toString(imageContainer.getSlice()));
		busy(true);
		imageContainerPanel.refreshImage();
	// also the bound field
		setSliceNumber(imageContainer.getSlice());
	//
		busy(false);
	}catch (Exception e){
		e.printStackTrace(System.out);
	}		
}
/**
 * Sets the sliceNumber property (int) value.
 * @param sliceNumber The new value for the property.
 * @see #getSliceNumber
 */
private void setSliceNumber(int sliceNumber) {
	int oldValue = fieldSliceNumber;
	fieldSliceNumber = sliceNumber;
	firePropertyChange("sliceNumber", new Integer(oldValue), new Integer(sliceNumber));
}
/**
 * Sets the slicePlane property (int) value.
 * @param slicePlane The new value for the property.
 * @see #getSlicePlane
 */
private void setSlicePlane(int slicePlane) {
	int oldValue = fieldSlicePlane;
	fieldSlicePlane = slicePlane;
	firePropertyChange("slicePlane", new Integer(oldValue), new Integer(slicePlane));
}
}
