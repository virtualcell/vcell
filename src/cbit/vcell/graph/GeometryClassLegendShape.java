package cbit.vcell.graph;
/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;

import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.ImmutableVisualState;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.geometry.SubVolume;
import cbit.vcell.geometry.SurfaceClass;

public class GeometryClassLegendShape extends ElipseShape implements PropertyChangeListener {
	GeometryClass geometryClass = null;
	Geometry geometry = null;
	int radius = 1;

	/**
	 * SpeciesShape constructor comment.
	 * @param label java.lang.String
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public GeometryClassLegendShape(GeometryClass geometryClass, Geometry geometry, GraphModel graphModel) {
		this(geometryClass,geometry,graphModel,1);
	}


	/**
	 * SpeciesShape constructor comment.
	 * @param label java.lang.String
	 * @param graphModel cbit.vcell.graph.GraphModel
	 */
	public GeometryClassLegendShape(GeometryClass geometryClass, Geometry geometry, GraphModel graphModel, int argRadius) {
		super(graphModel);
		this.geometryClass = geometryClass;
		this.geometry = geometry;
		geometryClass.addPropertyChangeListener(this);
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


	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 */
	@Override
	public Object getModelObject() {
		return geometryClass;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public Dimension getPreferedSize(Graphics2D g) {
		java.awt.FontMetrics fm = g.getFontMetrics();
		labelSize.height = fm.getMaxAscent() + fm.getMaxDescent();
		labelSize.width = fm.stringWidth(getLabel());
		//	preferedSize.height = radius*2 + labelSize.height;
		//	preferedSize.width = Math.max(radius*2,labelSize.width);
		preferredSize.height = radius*2;
		preferredSize.width = radius*2 + labelSize.width;
		return preferredSize;
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 */
	@Override
	public Point getSeparatorDeepCount() {	
		return new Point(1,1);
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Color
	 * @param subVolume cbit.vcell.geometry.SubVolume
	 */
	private java.awt.Color getSubvolumeColor(SubVolume subVolume) {
		java.awt.image.ColorModel colorModel = GeometrySpec.getHandleColorMap();
		int handle = subVolume.getHandle();
		return new java.awt.Color(colorModel.getRGB(handle));
	}


	/**
	 * This method was created by a SmartGuide.
	 * @return int
	 * @param g java.awt.Graphics
	 */
	@Override
	public void refreshLayout() {

		//
		// position label
		//
		labelPos.x = 2*radius + 5;
		labelPos.y = labelSize.height;
	}

	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {

		//
		// draw elipse
		//
		if (geometryClass instanceof SubVolume){
			SubVolume subVolume = (SubVolume)geometryClass;
			java.awt.Color fillColor = getSubvolumeColor(subVolume);
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
			java.awt.Color fillColor1 = null;
			java.awt.Color fillColor2 = null;
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
		//	g.drawRect(screenPos.x+parentOffsetX,screenPos.y+parentOffsetY,screenSize.width,screenSize.height);
		//
		// draw label
		//
		int textX = labelPos.x + absPosX;
		int textY = labelPos.y + absPosY;
		g.setColor(forgroundColor);
		if (getLabel()!=null && getLabel().length()>0){
			g.drawString(getLabel(),textX,textY);
		}
		return;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (6/5/00 10:50:17 PM)
	 * @param event java.beans.PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() == getModelObject() && event.getPropertyName().equals("name")){
			refreshLabel();
			graphModel.notifyChangeEvent();
		}
	}


	/**
	 * This method was created in VisualAge.
	 */
	@Override
	public void refreshLabel() {
		setLabel(geometryClass.getName());
	}


	/**
	 * This method was created by a SmartGuide.
	 * @param newSize java.awt.Dimension
	 */
	@Override
	public void resize(Graphics2D g, Dimension newSize) {
		return;
	}
}