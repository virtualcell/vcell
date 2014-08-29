/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.ColorModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;

import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.ImmutableVisualState;
import cbit.image.DisplayAdapterService;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;

public class GeometryClassLegendShape extends ElipseShape implements PropertyChangeListener {
	GeometryClass geometryClass = null;
	Geometry geometry = null;
	int radius = 1;

	public GeometryClassLegendShape(GeometryClass geometryClass, Geometry geometry, GraphModel graphModel) {
		this(geometryClass,geometry,graphModel,1);
	}

	public GeometryClassLegendShape(GeometryClass geometryClass, Geometry geometry, GraphModel graphModel, int argRadius) {
		super(graphModel);
		this.geometryClass = geometryClass;
		this.geometry = geometry;
		defaultBG = java.awt.Color.red;
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
		this.radius = argRadius;
	}

	@Override
	public VisualState createVisualState() {
		return new ImmutableVisualState(this, VisualState.PaintLayer.NODE);
	}

	@Override
	public Point getAttachmentLocation(int attachmentType) {
		return new Point(radius, radius);
	}

	@Override
	public Object getModelObject() {
		return geometryClass;
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		java.awt.FontMetrics fm = g.getFontMetrics();
		setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		getSpaceManager().setSizePreferred((radius*2 + getLabelSize().width), (radius*2));
		return getSpaceManager().getSizePreferred();
	}

	private Color getSubvolumeColor(SubVolume subVolume) {
		ColorModel colorModel = DisplayAdapterService.getHandleColorMap();
		int handle = subVolume.getHandle();
		return new Color(colorModel.getRGB(handle));
	}

	public void refreshLayoutSelf() {
		labelPos.x = 2*radius + 5; 
		labelPos.y = getLabelSize().height;
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {
		// draw elipse
		if (geometryClass instanceof SubVolume){
			SubVolume subVolume = (SubVolume)geometryClass;
			Color fillColor = getSubvolumeColor(subVolume);
			g.setColor(fillColor);
			g.fill3DRect(absPosX+1,absPosY+1,2*radius-1,2*radius-1,true);
			g.setColor(forgroundColor);
			g.draw3DRect(absPosX,absPosY,2*radius,2*radius,true);
		}else if (geometryClass instanceof SurfaceClass){
			SurfaceClass surfaceClass = (SurfaceClass)geometryClass;
			Set<SubVolume> adjacentSubVolumes = surfaceClass.getAdjacentSubvolumes();
			Iterator<SubVolume> iterator = adjacentSubVolumes.iterator();
			SubVolume subVolume1 = iterator.next();
			SubVolume subVolume2 = iterator.next();
			Color fillColor1 = null;
			Color fillColor2 = null;
			// make same choice each time so that repaint don't flicker
			if (subVolume1.getName().compareTo(subVolume2.getName()) > 0) {
				fillColor1 = getSubvolumeColor(subVolume1);
				fillColor2 = getSubvolumeColor(subVolume2);	
			} else {
				fillColor1 = getSubvolumeColor(subVolume2);
				fillColor2 = getSubvolumeColor(subVolume1);
			}
			g.setColor(fillColor1);
			g.fill3DRect(absPosX+1,absPosY+1,radius,2*radius-1,true);
			g.setColor(fillColor2);
			g.fill3DRect(absPosX+1+radius,absPosY+1,radius-1,2*radius-1,true);
			g.setColor(forgroundColor);
			g.draw3DRect(absPosX,absPosY,2*radius,2*radius,true);
		}
		int textX = getLabelPos().x + absPosX;
		int textY = getLabelPos().y + absPosY;
		g.setColor(forgroundColor);
		if (getLabel()!=null && getLabel().length()>0){
			g.drawString(getLabel(),textX,textY);
		}
		return;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() == getModelObject() && event.getPropertyName().equals("name")){
			refreshLabel();
			graphModel.notifyChangeEvent();
		}
	}

	@Override
	public void refreshLabel() {
		setLabel(geometryClass.getName());
	}

}
