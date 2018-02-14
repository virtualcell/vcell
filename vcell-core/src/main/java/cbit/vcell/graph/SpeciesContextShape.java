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
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Map;

import org.vcell.sybil.models.miriam.MIRIAMQualifier;

import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModel;
import cbit.gui.graph.GraphModelPreferences;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.MutableVisualState;
import cbit.vcell.biomodel.meta.MiriamManager;
import cbit.vcell.biomodel.meta.MiriamManager.MiriamRefGroup;
import cbit.vcell.model.SpeciesContext;

public class SpeciesContextShape extends ElipseShape {
	SpeciesContext speciesContext = null;
	private static final int RADIUS = 8;
	public static final int DIAMETER = 2*RADIUS;
	private Color darkerBackground = null;
	private Area icon = null;

	private static final int SCS_LABEL_WIDTHPARM = 3;
	private static final String SCS_LABEL_TRUCATED = "...";
	
	private String smallLabel = null;
	protected Dimension smallLabelSize = new Dimension();
	protected Point smallLabelPos = new Point(0,0);
	private int circleDiameter = 14;		// 14 is the default value
	private boolean isCatalyst = false;

	private boolean bTruncateLabelName = true;
	
	protected String linkText;

	public SpeciesContextShape(SpeciesContext speciesContext, GraphModel graphModel) {
		super(graphModel);
		this.speciesContext = speciesContext;
		if(this.speciesContext.getSpeciesPattern() == null) {
			defaultBG = java.awt.Color.green;
		} else {
			defaultBG = java.awt.Color.blue;
		}
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
		darkerBackground = backgroundColor.darker().darker();
	}

	@Override
	public VisualState createVisualState() { 
		return new MutableVisualState(this, VisualState.PaintLayer.NODE); 
	}
	public void setFilters(boolean isCatalyst, Integer weight) {
		if(isCatalyst == true) {
			this.isCatalyst = true;
		} else {
			this.isCatalyst = false;
		}
		if(weight != null) {
			circleDiameter = 14+weight;
		} else {
			circleDiameter = 14;
		}
	}

	@Override
	public Object getModelObject() {
		return speciesContext;
	}

	public Dimension getSmallLabelSize() { return smallLabelSize; }
	public Point getSmallLabelPos() { return smallLabelPos; }
	
	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		FontMetrics fm = null;
		if (g==null || GraphicsEnvironment.isHeadless()) {
			Font font = new Font("Helvetica",Font.PLAIN,12);
			Canvas c = new Canvas();
			fm = c.getFontMetrics(font);
		}else {
			fm = g.getFontMetrics();
		}
		setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		smallLabelSize.width = (smallLabel != null ? fm.stringWidth(smallLabel) : getLabelSize().width);
		smallLabelSize.height = getLabelSize().height;
		getSpaceManager().setSizePreferred(DIAMETER, DIAMETER);
		return getSpaceManager().getSizePreferred();
	}

	public SpeciesContext getSpeciesContext() {
		return speciesContext;
	}

	public void setLinkText(String linkText) { this.linkText = linkText; }
	
	@Override
	public void refreshLayoutSelf() {
		int centerX = getSpaceManager().getSize().width/2;
		labelPos.x = centerX - getLabelSize().width/2; 
		labelPos.y = 0;
		smallLabelPos.x = centerX - smallLabelSize.width/2;
		smallLabelPos.y = getLabelPos().y;		
	}
	
	public Rectangle getLabelOutline( int absPosX, int absPosY){
		return new Rectangle(getLabelPos().x + absPosX - 5, 
		getLabelPos().y + absPosY - getLabelSize().height + 3,
		getLabelSize().width + 10, getLabelSize().height);
	}
	
	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {
		boolean isBound = false;
		SpeciesContext sc = (SpeciesContext)getModelObject();
		boolean bHasPCLink = false;
		if (graphModel instanceof ModelCartoon) {
			ModelCartoon mc = (ModelCartoon)graphModel;
			// check if species has Pathway Commons link by querying VCMetadata : if it does, need to change color of speciesContext.
			try {
				MiriamManager miriamManager = mc.getModel().getVcMetaData().getMiriamManager();
				Map<MiriamRefGroup,MIRIAMQualifier> miriamRefGroups = miriamManager.getAllMiriamRefGroups(sc.getSpecies());
				if (miriamRefGroups!=null && miriamRefGroups.size()>0){
					bHasPCLink = true;
				}
			}catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
		if(sc.getSpecies().getDBSpecies() != null || bHasPCLink){
			isBound = true;
		}
		
		int shapeHeight = getSpaceManager().getSize().height;
		int shapeWidth = getSpaceManager().getSize().width;
		int offsetX = (shapeWidth-circleDiameter) / 2;
		int offsetY = (shapeHeight-circleDiameter) / 2;
		Graphics2D g2D = g;
//		if (icon == null) {
			icon = new Area();
			icon.add(new Area(new Ellipse2D.Double(offsetX, offsetY,circleDiameter,circleDiameter)));
			//icon.add(new Area(new RoundRectangle2D.Double(offsetX, offsetY,circleDiameter,circleDiameter,circleDiameter/2,circleDiameter/2)));
//		}
		Area movedIcon = icon.createTransformedArea(
			AffineTransform.getTranslateInstance(absPosX, absPosY));

		if(sc.getSpeciesPattern() == null) {
			defaultBG = java.awt.Color.green;
		} else {
			defaultBG = java.awt.Color.blue;
		}
		if(isCatalyst == true) {
			defaultBG = java.awt.Color.magenta;
		}
		backgroundColor = defaultBG;
		darkerBackground = backgroundColor.darker().darker();

		//g.setColor((!isBound && !isSelected()?darkerBackground:backgroundColor));
		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color exterior = !isBound && !isSelected()?darkerBackground:backgroundColor;
//		Color interior = exterior.brighter().brighter();
		Point2D center = new Point2D.Float(absPosX+circleDiameter*0.5f, absPosY+circleDiameter*0.5f);
		float radius = circleDiameter*0.5f;
		Point2D focus = new Point2D.Float(absPosX+circleDiameter*0.4f, absPosY+circleDiameter*0.4f);
		float[] dist = {0.1f, 1.0f};
		Color[] colors = {Color.white, exterior};
//		Color[] colors = {interior, exterior};
		RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
		g2D.setPaint(p);
		g2D.fill(movedIcon);
		
		g.setColor(forgroundColor);
		g2D.draw(movedIcon);
//		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		// draw label
		if (getLabel()!=null && getLabel().length()>0){
			if(isSelected()){//clear background and outline to make selected label stand out
				Rectangle outlineRectangle = getLabelOutline(absPosX, absPosY);
				drawRaisedOutline(
						outlineRectangle.x, 
						outlineRectangle.y,
						outlineRectangle.width, outlineRectangle.height, g, 
						Color.white, forgroundColor, Color.gray);
			}
			g.setColor(forgroundColor);
			g.drawString(
					(isSelected() || smallLabel == null ? getLabel():smallLabel),
					(isSelected() || smallLabel == null ? getLabelPos().x : smallLabelPos.x) + 
					absPosX, getLabelPos().y + absPosY);
		}
		if(linkText != null && linkText != "") {
			ShapePaintUtil.paintLinkMark(g2D, this, Color.BLACK);
		}
	}

	@Override
	public void refreshLabel() {
		switch (GraphModelPreferences.getInstance().getSpeciesContextDisplayName()) {
		case GraphModelPreferences.DISPLAY_COMMON_NAME: {
			setLabel(getSpeciesContext().getSpecies().getCommonName());
			break;
		}
		case GraphModelPreferences.DISPLAY_CONTEXT_NAME: {
			setLabel(getSpeciesContext().getName());
			break;
		}
		}

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
}
