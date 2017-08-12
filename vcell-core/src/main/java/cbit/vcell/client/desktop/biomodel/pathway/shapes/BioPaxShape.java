/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel.pathway.shapes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Entity;

import cbit.gui.graph.Shape;
import cbit.gui.graph.ShapeSpaceManager;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.MutableVisualState;
import cbit.vcell.graph.ShapePaintUtil;

public abstract class BioPaxShape extends Shape {
	BioPaxObject bioPaxObject = null;
	private static final int SCS_LABEL_WIDTHPARM = 7;
	private static final String SCS_LABEL_TRUCATED = "...";
	protected String smallLabel = null;
	protected Dimension smallLabelSize = new Dimension();
	protected Point smallLabelPos = new Point(0,0);

	protected Color darkerBackground = null;

	protected boolean hasRelationships = false;
	
	private boolean bTruncateLabelName = true;
	
	private Area icon = null;

	public BioPaxShape(BioPaxObject bioPaxObject, PathwayGraphModel graphModel) {
		super(graphModel);
		this.bioPaxObject = bioPaxObject;
		defaultBG = getDefaultBackgroundColor();
		defaultFGselect = getDefaultSelectedForegroundColor();
		backgroundColor = defaultBG;
		darkerBackground = backgroundColor.darker();
	}
	
	protected abstract int getPreferredWidth();
	protected abstract int getPreferredHeight();
	protected Color getDefaultBackgroundColor() { return Color.pink; }
	protected Color getDefaultSelectedForegroundColor() { return Color.black; }

	@Override
	public PathwayGraphModel getGraphModel() { 
		return (PathwayGraphModel) super.getGraphModel();
	}
	
	public void setHasRelationships(boolean hasRelationships) { this.hasRelationships = hasRelationships; }
	
	@Override
	public VisualState createVisualState() { 
		return new MutableVisualState(this, VisualState.PaintLayer.NODE); 
	}

	@Override
	public BioPaxObject getModelObject() {
		return bioPaxObject;
	}

	public BioPaxObject getBioPaxObject() {
		return bioPaxObject;
	}

	protected boolean hasPCLink(){
		return false;
	}
	
	@Override
	public void refreshLabel() {
		String name = "[" + bioPaxObject.getIDShort() + "]";
		if (bioPaxObject instanceof Entity){
			Entity entity = (Entity)bioPaxObject;
			ArrayList<String> names = entity.getName();
			if (names.size()>0){
				name = names.get(0);
			} 
		}
		setLabel(name);

		smallLabel = getLabel();
		if(bTruncateLabelName && getLabel().length() > (2*SCS_LABEL_WIDTHPARM + SCS_LABEL_TRUCATED.length())){
			smallLabel =
				getLabel().substring(0,SCS_LABEL_WIDTHPARM)+
				SCS_LABEL_TRUCATED+
				getLabel().substring(getLabel().length()-SCS_LABEL_WIDTHPARM);
		}
	}

	public void truncateLabelName(boolean bTruncate) {

		bTruncateLabelName = bTruncate;
	}
	
	final double getRadius(Point pick) {
		ShapeSpaceManager spaceManager = getSpaceManager();
		int centerX = spaceManager.getRelX() + spaceManager.getSize().width / 2;
		int centerY = spaceManager.getRelY() + spaceManager.getSize().height / 2;
		double radiusX = pick.x - centerX;
		double radiusY = pick.y - centerY;
		double b = spaceManager.getSize().height / 2;
		double a = spaceManager.getSize().width / 2;
		double radius = radiusX * radiusX / (a * a) + radiusY * radiusY
				/ (b * b);

		return radius;
	}
	
	@Override
	public final boolean isInside(Point p) {
		if (getRadius(p) < 1.0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		smallLabelSize.width = (smallLabel != null ? fm.stringWidth(smallLabel) : getLabelSize().width);
		smallLabelSize.height = getLabelSize().height;
		getSpaceManager().setSizePreferred(getPreferredWidth(), getPreferredHeight());
		return getSpaceManager().getSizePreferred();
	}

	@Override
	public void refreshLayoutSelf() {
		int centerX = getSpaceManager().getSize().width/2;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = 0;
		smallLabelPos.x = centerX - smallLabelSize.width/2;
		smallLabelPos.y = getLabelPos().y;
	}
	
	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {
		
		RenderingHints oldRenderingHints = g.getRenderingHints();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int shapeHeight = getSpaceManager().getSize().height;
		int shapeWidth = getSpaceManager().getSize().width;
		int offsetX = (shapeWidth-getPreferredWidth()) / 2;
		int offsetY = (shapeHeight-getPreferredHeight()) / 2;
		Graphics2D g2D = g;
		
		if (icon == null) {
			icon = new Area();
			icon.add(new Area(new Ellipse2D.Double(offsetX, offsetY, getPreferredWidth(), getPreferredHeight())));
		}
		Area movedIcon = icon.createTransformedArea(AffineTransform.getTranslateInstance(absPosX, absPosY));
		g.setColor((!hasPCLink() && !isSelected()?darkerBackground:backgroundColor));
		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color exterior = !hasPCLink() && !isSelected() ? darkerBackground : backgroundColor;
		Color interior = exterior.brighter();
		exterior = exterior.darker();
		Point2D center = new Point2D.Float(absPosX+getPreferredWidth()/2, absPosY+getPreferredWidth()/2);
		float radius = getPreferredWidth()*0.5f;
		Point2D focus = new Point2D.Float(absPosX+getPreferredWidth()/2-2, absPosY+getPreferredWidth()/2-2);
		float[] dist = {0.1f, 1.0f};
//		Color[] colors = {Color.white, exterior};
		Color[] colors = {interior, exterior};
		RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
		g2D.setPaint(p);
		g2D.fill(movedIcon);
		g.setColor(forgroundColor);
		g2D.draw(movedIcon);
		
		// draw label
		if (getLabel()!=null && getLabel().length()>0){
			if(isSelected()){//clear background and outline to make selected label stand out
				drawRaisedOutline(
						getLabelPos().x + absPosX - 5, 
						getLabelPos().y + absPosY - getLabelSize().height + 3,
						getLabelSize().width + 10, getLabelSize().height, g, 
						Color.white, forgroundColor, Color.gray);
			}
			g.setColor(forgroundColor);
			g.drawString(
					(isSelected() || smallLabel == null ? getLabel():smallLabel),
					(isSelected() || smallLabel == null ? getLabelPos().x : smallLabelPos.x) + 
					absPosX, getLabelPos().y + absPosY);
		}
		if(hasRelationships) {
			ShapePaintUtil.paintLinkMark(g2D, this, Color.BLACK);			
		}
		
		g.setRenderingHints(oldRenderingHints);
	}

}
