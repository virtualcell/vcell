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

import org.vcell.util.NumberUtils;

/**
 * Insert the type's description here.
 * Creation date: (7/19/2003 5:40:19 PM)
 * @author: Frank Morgan
 */
public class ImagePaneRuler extends javax.swing.JPanel {
	//
	public static final String ORIENT_HORIZONTAL = "horiz";
	public static final String ORIENT_VERTICAL = "vert";
	private java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat();
	private static final int VERT_TEXT_CHAR_LIMIT = 9;
	//
	//private java.awt.Font lastBigFont = null;
	private java.awt.Font lastSmallFont = null;
	private java.awt.Font titleFont = null;
	//
	private java.lang.String fieldOrientation = ORIENT_HORIZONTAL;
	private double fieldWorldOrigin = 0;
	private double fieldWorldExtent = 0;
	private int fieldWorldPixelSize = 0;
	private int fieldSingleElementLengthInPixels = 0;
	private ImagePlaneManager fieldImagePlaneManager = null;
	private java.lang.String fieldDescription = null;

/**
 * ImagePaneRuler constructor comment.
 */
public ImagePaneRuler() {
	super();
}


/**
 * ImagePaneRuler constructor comment.
 */
public ImagePaneRuler(String argOrientation) {
	super();
	setOrientation(argOrientation);
	decimalFormat.applyPattern("#.###E0");

	java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
	at.scale(1.5,1.0);
	//if(argOrientation.equals(ORIENT_VERTICAL)){
		titleFont = getFont().deriveFont(at);
	//}else{
		//titleFont = getFont();
	//}
	titleFont = titleFont.deriveFont(java.awt.Font.BOLD,12.0f);

	lastSmallFont = getFont().deriveFont(10f);
}


/**
 * Gets the description property (java.lang.String) value.
 * @return The description property value.
 * @see #setDescription
 */
public java.lang.String getDescription() {
	return fieldDescription;
}


/**
 * Gets the imagePlaneManager property (cbit.image.ImagePlaneManager) value.
 * @return The imagePlaneManager property value.
 * @see #setImagePlaneManager
 */
public ImagePlaneManager getImagePlaneManager() {
	return fieldImagePlaneManager;
}


/**
 * Gets the orientation property (java.lang.String) value.
 * @return The orientation property value.
 * @see #setOrientation
 */
public java.lang.String getOrientation() {
	return fieldOrientation;
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2003 5:40:55 PM)
 */
public java.awt.Dimension getPreferredSize() {
	if(getOrientation() == ORIENT_HORIZONTAL){
		return new java.awt.Dimension(getWorldPixelSize(),20+(getDescription() == null?0:20));
	}else{
		return new java.awt.Dimension(/*60*/50+(getDescription() == null?0:15),getWorldPixelSize());
	}
}


/**
 * Gets the singleElementLengthInPixels property (int) value.
 * @return The singleElementLengthInPixels property value.
 * @see #setSingleElementLengthInPixels
 */
public int getSingleElementLengthInPixels() {
	return fieldSingleElementLengthInPixels;
}


/**
 * Gets the worldExtent property (double) value.
 * @return The worldExtent property value.
 * @see #setWorldExtent
 */
public double getWorldExtent() {
	return fieldWorldExtent;
}


/**
 * Gets the worldOrigin property (double) value.
 * @return The worldOrigin property value.
 * @see #setWorldOrigin
 */
public double getWorldOrigin() {
	return fieldWorldOrigin;
}


/**
 * Gets the worldPixelSize property (int) value.
 * @return The worldPixelSize property value.
 * @see #setWorldPixelSize
 */
public int getWorldPixelSize() {
	return fieldWorldPixelSize;
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2003 5:49:07 PM)
 */
protected void paintComponent(java.awt.Graphics g) {
	super.paintComponent(g);
	//
	if(getImagePlaneManager() == null ){
		return;
	}
	boolean isCellCentered = getImagePlaneManager().isCellCentered();
	//if(lastBigFont == null || !lastBigFont.equals(g.getFont())){
		//lastBigFont = g.getFont();
		//lastSmallFont = lastBigFont.deriveFont(10f);
	//}
	((java.awt.Graphics2D)g).setFont(lastSmallFont);
	//
	int elementLength = getSingleElementLengthInPixels();
	int halfElementLength = elementLength / 2;
	if(getOrientation() == ORIENT_HORIZONTAL){
		//
		int descOffset = 0;
		if(getDescription() != null){
			java.awt.Graphics2D g2D = (java.awt.Graphics2D)g;
			g2D.setFont(titleFont);
			java.awt.geom.Rectangle2D r2d =
				titleFont.getStringBounds(getDescription(),0,getDescription().length(),g2D.getFontRenderContext());
			descOffset = (int)r2d.getHeight();
			java.awt.Rectangle visRect = getVisibleRect();
			int pos = (int)(visRect.getX() + ((visRect.getWidth() - r2d.getWidth())/2));
			g2D.drawString(getDescription(),pos,15);
			((java.awt.Graphics2D)g).setFont(lastSmallFont);
		}
		//
		int start = 0;
		int width = getWorldPixelSize();
		int end = start + width;//drawWidth;
		int len = 6;
		//
		int lastPrintPoint = start;
		int lastLowPoint = -1;
		int lastHighPoint = -1;
		//
		int horzGap = 60;
		//
		for(int x = start;x<end;x+= 1){
			boolean bDrawLine = isCellCentered ? x % elementLength == 0 : (x-halfElementLength)%elementLength == 0;
			if(bDrawLine || x == start || x == (end-1)){
				//mark off the mesh elements
				g.drawLine(x,(int)getPreferredSize().getHeight()-1-(len/2),x,(int)getPreferredSize().getHeight()-1);
				//keep track of boundary between mesh elements
				lastLowPoint = lastHighPoint;
				lastHighPoint = x;
				if(lastLowPoint != -1 && lastHighPoint != -1){//when we start having 2 boundaries we can calculate the middles
					int tempPrintpoint = lastLowPoint+((lastHighPoint-lastLowPoint)/2);
					boolean bPrintPoint = isCellCentered ? tempPrintpoint == halfElementLength || end - tempPrintpoint == halfElementLength : lastLowPoint == 0 || lastHighPoint == (end-1);
					if((bPrintPoint || (tempPrintpoint-lastPrintPoint) >= horzGap && end-tempPrintpoint >= horzGap) && getImagePlaneManager() != null){
						//g.drawLine(lastLowPoint,(int)getPreferredSize().getHeight()-1-len,lastLowPoint,(int)getPreferredSize().getHeight()-1);
						//g.drawLine(lastHighPoint,(int)getPreferredSize().getHeight()-1-len,lastHighPoint,(int)getPreferredSize().getHeight()-1);
						if(isCellCentered || (lastPrintPoint != start && lastHighPoint != (end-1))){//dont print middle marks at begin or end
							g.drawLine(tempPrintpoint,(int)getPreferredSize().getHeight()-1-len+2,tempPrintpoint,(int)getPreferredSize().getHeight()-1-2);
						}
						lastPrintPoint = tempPrintpoint;
						org.vcell.util.Coordinate coord = null;
						if(lastLowPoint == 0){
							coord = getImagePlaneManager().getWorldCoordinateFromUnitized2D((isCellCentered?1.0/(2*getWorldPixelSize()):0),0);
						}else if(lastHighPoint == (end-1)){
							coord = getImagePlaneManager().getWorldCoordinateFromUnitized2D((isCellCentered?1.0-1.0/(2*getWorldPixelSize()):1),0);
						}else{
							coord = getImagePlaneManager().getWorldCoordinateFromUnitized2D((double)lastPrintPoint/(double)getWorldPixelSize(),0);
						}
						String val = "";
						if(coord != null){
							double doubleVal = org.vcell.util.Coordinate.convertAxisFromStandardXYZToNormal(coord,org.vcell.util.Coordinate.X_AXIS,getImagePlaneManager().getNormalAxis());
							val = org.vcell.util.NumberUtils.formatNumber(doubleVal);
						}
						java.awt.geom.Rectangle2D r2d = getFont().getStringBounds(val,0,val.length(),((java.awt.Graphics2D)g).getFontRenderContext());
						java.awt.font.LineMetrics lm = getFont().getLineMetrics(val,((java.awt.Graphics2D)g).getFontRenderContext());
						if(lastLowPoint == 0){
							g.drawString(val,(isCellCentered ? tempPrintpoint : 2) ,(int)lm.getAscent()+descOffset);
						}else if(lastHighPoint == (end-1)){
							g.drawString(val,(isCellCentered ? tempPrintpoint : end-(int)r2d.getWidth()),(int)lm.getAscent()+descOffset);
						}else{
							g.drawString(val,lastPrintPoint-(((int)r2d.getWidth())/2),(int)lm.getAscent()+descOffset);
						}
					}
				}
			}
		}
	}else if(getOrientation() == ORIENT_VERTICAL){
		//
		int descOffset = 0;
		if(getDescription() != null){
			java.awt.Graphics2D g2D = (java.awt.Graphics2D)g;
			g2D.setFont(titleFont);
			java.awt.geom.Rectangle2D r2d =
				titleFont.getStringBounds(getDescription(),0,getDescription().length(),g2D.getFontRenderContext());
			descOffset = (int)r2d.getHeight();
			java.awt.Rectangle visRect = getVisibleRect();
			int pos = (int)((visRect.getY()+visRect.getHeight()/2) + (r2d.getWidth()/2));
			//int pos = (int)((g2D.getClipBounds().getY()+g2D.getClipBounds().getHeight()/2) + (r2d.getWidth()/2));
			java.awt.geom.AffineTransform origAT = g2D.getTransform();
			g2D.rotate(-Math.PI/2.0);
			g2D.drawString(getDescription(),-pos,descOffset);
			g2D.setTransform(origAT);
			((java.awt.Graphics2D)g).setFont(lastSmallFont);
		}
		//
		int start = 0;
		int height = getWorldPixelSize();
		int end = start + height;//drawHeight;
		int len = 6;
		//
		int lastPrintPoint = start;
		int lastLowPoint = -1;
		int lastHighPoint = -1;
		//
		int vertGap = 20;
		//
		for(int y = start;y<end;y+= 1){
			boolean bDrawLine = isCellCentered ? y % elementLength == 0 : (y-halfElementLength)%elementLength == 0;
			if(bDrawLine || y == start || y == (end-1)){
				g.drawLine((int)getPreferredSize().getWidth()-1-(len/2),y,(int)getPreferredSize().getWidth()-1,y);
				//
				lastLowPoint = lastHighPoint;
				lastHighPoint = y;
				if(lastLowPoint != -1 && lastHighPoint != -1){
					int tempPrintpoint = lastLowPoint+((lastHighPoint-lastLowPoint)/2);
					boolean bPrintPoint = isCellCentered ? tempPrintpoint == halfElementLength || end - tempPrintpoint == halfElementLength :  lastLowPoint == 0 || lastHighPoint == (end-1);
					if((bPrintPoint || (tempPrintpoint-lastPrintPoint) >= vertGap && end-tempPrintpoint >= vertGap) && getImagePlaneManager() != null){
						//g.drawLine((int)getPreferredSize().getWidth()-1-len,lastLowPoint,(int)getPreferredSize().getWidth()-1,lastLowPoint);
						//g.drawLine((int)getPreferredSize().getWidth()-1-len,lastHighPoint,(int)getPreferredSize().getWidth()-1,lastHighPoint);						
						if(isCellCentered || (lastPrintPoint != start) && lastHighPoint != (end-1)){//dont print middle marks at begin or end
							g.drawLine((int)getPreferredSize().getWidth()-1-len+2,tempPrintpoint,(int)getPreferredSize().getWidth()-1-2,tempPrintpoint);						
						}
						lastPrintPoint = tempPrintpoint;
						org.vcell.util.Coordinate coord = null;
						if(lastLowPoint == 0){
							coord = getImagePlaneManager().getWorldCoordinateFromUnitized2D(0,(isCellCentered?1.0/(2*getWorldPixelSize()):0));
						}else if(lastHighPoint == (end-1)){
							coord = getImagePlaneManager().getWorldCoordinateFromUnitized2D(0,(isCellCentered?1.0-1.0/(2*getWorldPixelSize()):1));
						}else{
							coord = getImagePlaneManager().getWorldCoordinateFromUnitized2D(0,(double)lastPrintPoint/(double)getWorldPixelSize());
						}
						String val = "";
						if(coord != null){
							double doubleVal = org.vcell.util.Coordinate.convertAxisFromStandardXYZToNormal(coord,org.vcell.util.Coordinate.Y_AXIS,getImagePlaneManager().getNormalAxis());
							val = doubleVal+"";
							val = (val.length() <= VERT_TEXT_CHAR_LIMIT?val:NumberUtils.formatNumber(doubleVal,VERT_TEXT_CHAR_LIMIT));
						}
						java.awt.font.LineMetrics lm = getFont().getLineMetrics(val,((java.awt.Graphics2D)g).getFontRenderContext());
						if(lastLowPoint == 0){
							g.drawString(val,2+descOffset,(isCellCentered ? tempPrintpoint : 0) + (int)lm.getAscent());
						}else if(lastHighPoint == (end-1)){
							g.drawString(val,2+descOffset,(isCellCentered ? tempPrintpoint : (end-1)));
						}else{
							g.drawString(val,2+descOffset,lastPrintPoint+(int)lm.getAscent()/2);
						}
					}
				}
			}
		}
	}
}


/**
 * Sets the description property (java.lang.String) value.
 * @param description The new value for the property.
 * @see #getDescription
 */
public void setDescription(java.lang.String description) {
	String oldValue = fieldDescription;
	fieldDescription = description;
	firePropertyChange("description", oldValue, description);
}


/**
 * Sets the imagePlaneManager property (cbit.image.ImagePlaneManager) value.
 * @param imagePlaneManager The new value for the property.
 * @see #getImagePlaneManager
 */
public void setImagePlaneManager(ImagePlaneManager imagePlaneManager) {
	ImagePlaneManager oldValue = fieldImagePlaneManager;
	fieldImagePlaneManager = imagePlaneManager;
	repaint();
	firePropertyChange("imagePlaneManager", oldValue, imagePlaneManager);
}


/**
 * Sets the orientation property (java.lang.String) value.
 * @param orientation The new value for the property.
 * @see #getOrientation
 */
public void setOrientation(java.lang.String orientation) {
	String oldValue = fieldOrientation;
	fieldOrientation = orientation;
	firePropertyChange("orientation", oldValue, orientation);
}


/**
 * Sets the singleElementLengthInPixels property (int) value.
 * @param singleElementLengthInPixels The new value for the property.
 * @see #getSingleElementLengthInPixels
 */
public void setSingleElementLengthInPixels(int singleElementLengthInPixels) {
	int oldValue = fieldSingleElementLengthInPixels;
	fieldSingleElementLengthInPixels = singleElementLengthInPixels;
	firePropertyChange("singleElementLengthInPixels", new Integer(oldValue), new Integer(singleElementLengthInPixels));
}


/**
 * Sets the worldExtent property (double) value.
 * @param worldExtent The new value for the property.
 * @see #getWorldExtent
 */
public void setWorldExtent(double worldExtent) {
	double oldValue = fieldWorldExtent;
	fieldWorldExtent = worldExtent;
	firePropertyChange("worldExtent", new Double(oldValue), new Double(worldExtent));
}


/**
 * Sets the worldOrigin property (double) value.
 * @param worldOrigin The new value for the property.
 * @see #getWorldOrigin
 */
public void setWorldOrigin(double worldOrigin) {
	double oldValue = fieldWorldOrigin;
	fieldWorldOrigin = worldOrigin;
	firePropertyChange("worldOrigin", new Double(oldValue), new Double(worldOrigin));
}


/**
 * Sets the worldPixelSize property (int) value.
 * @param worldPixelSize The new value for the property.
 * @see #getWorldPixelSize
 */
public void setWorldPixelSize(int worldPixelSize) {
	int oldValue = fieldWorldPixelSize;
	fieldWorldPixelSize = worldPixelSize;
	firePropertyChange("worldPixelSize", new Integer(oldValue), new Integer(worldPixelSize));
}
}
