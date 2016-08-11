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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.List;

import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.gui.ShapePaintUtil;

import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModelPreferences;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.MutableVisualState;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.ReactionRuleParticipant;
import cbit.vcell.model.RuleParticipantSignature;

public class RuleParticipantSignatureDiagramShape extends ElipseShape {
	RuleParticipantSignature ruleParticipantSignature = null;
	private static final int RADIUS = 10;
	public static final int DIAMETER = 2*RADIUS;
	private Color darkerBackground = null;
	private Area icon = null;

	private static final int SCS_LABEL_WIDTHPARM = 3;
	private static final String SCS_LABEL_TRUCATED = "...";
	private String smallLabel = null;
	protected Dimension smallLabelSize = new Dimension();
	protected Point smallLabelPos = new Point(0,0);

	private boolean bTruncateLabelName = true;
	private boolean bDisplayLabel = false;
	
	protected String linkText;

	public RuleParticipantSignatureDiagramShape(RuleParticipantSignature ruleParticipantSignature, ModelCartoon graphModel) {
		super(graphModel);
		this.ruleParticipantSignature = ruleParticipantSignature;
		defaultBG = java.awt.Color.red;
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
		darkerBackground = backgroundColor.darker().darker();
	}

	@Override
	public VisualState createVisualState() { 
		return new MutableVisualState(this, VisualState.PaintLayer.NODE); 
	}

	@Override
	public Object getModelObject() {
		return ruleParticipantSignature;
	}

	public Dimension getSmallLabelSize() { return smallLabelSize; }
	public Point getSmallLabelPos() { return smallLabelPos; }
	
	@Override
	public Dimension getPreferedSizeSelf(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		setLabelSize(fm.stringWidth(getLabel()), fm.getMaxAscent() + fm.getMaxDescent());
		smallLabelSize.width = (smallLabel != null ? fm.stringWidth(smallLabel) : getLabelSize().width);
		smallLabelSize.height = getLabelSize().height;
		getSpaceManager().setSizePreferred(DIAMETER, DIAMETER);
		return getSpaceManager().getSizePreferred();
	}

	public RuleParticipantSignature getRuleParticipantSignature() {
		return ruleParticipantSignature;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}
	public void setDisplayLabel(boolean bDisplayLabel) {
		this.bDisplayLabel = bDisplayLabel;
	}
	
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
		
		int displacement = 7;		// distance between circles representing molecular types
		int circleDiameter = 14;
		int shapeHeight = getSpaceManager().getSize().height;
		int shapeWidth = getSpaceManager().getSize().width;
		int offsetX = (shapeWidth-circleDiameter) / 2;
		int offsetY = (shapeHeight-circleDiameter) / 2;
		Graphics2D g2D = g;
		
		Model model = ((ReactionCartoon)graphModel).getModel();
		RbmModelContainer rbmmc = model.getRbmModelContainer();
		List<MolecularType> mtList = rbmmc.getMolecularTypeList();
		List<MolecularType> ruleSignatureMolecularTypes = ruleParticipantSignature.getMolecularTypes();
		for(int i=0; i<ruleSignatureMolecularTypes.size(); i++) {
			icon = new Area();
			icon.add(new Area(new Ellipse2D.Double(offsetX + displacement*i, offsetY,circleDiameter,circleDiameter)));
				//icon.add(new Area(new RoundRectangle2D.Double(offsetX, offsetY,circleDiameter,circleDiameter,circleDiameter/2,circleDiameter/2)));
			Area movedIcon = icon.createTransformedArea(
				AffineTransform.getTranslateInstance(absPosX, absPosY));
	
			MolecularType mt = ruleSignatureMolecularTypes.get(i);
			int index = mtList.indexOf(mt);
			index = index%7;

//			defaultBG = MolecularTypeLargeShape.colorTable[index];		// take color from molecular type color selection
			defaultBG = Color.lightGray;
			backgroundColor = defaultBG;
			darkerBackground = backgroundColor.darker().darker();
	
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Color exterior = !isSelected()?darkerBackground:backgroundColor;
	//		Color interior = exterior.brighter().brighter();
			Point2D center = new Point2D.Float(absPosX + displacement*i + circleDiameter/2, absPosY+circleDiameter/2);
			float radius = circleDiameter*0.5f;
			Point2D focus = new Point2D.Float(absPosX + displacement*i + circleDiameter/2+1, absPosY+circleDiameter/2+1);
			float[] dist = {0.1f, 1.0f};
			Color[] colors = {Color.white, exterior};
	//		Color[] colors = {interior, exterior};
			RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
			g2D.setPaint(p);
			g2D.fill(movedIcon);
			
			g.setColor(forgroundColor);
			g2D.draw(movedIcon);
	//		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}		

		// draw label
		if (getLabel()!=null && getLabel().length()>0){
			if(isSelected()) {		//clear background and outline to make selected label stand out
				Rectangle outlineRectangle = getLabelOutline(absPosX, absPosY);
				drawRaisedOutline(
						outlineRectangle.x, 
						outlineRectangle.y,
						outlineRectangle.width, outlineRectangle.height, g, 
						Color.white, forgroundColor, Color.gray);
			}
			if(bDisplayLabel || isSelected()) {
				g.setColor(forgroundColor);
				g.drawString(
					(isSelected() || smallLabel == null ? getLabel():smallLabel),
					(isSelected() || smallLabel == null ? getLabelPos().x : smallLabelPos.x) + 
					absPosX, getLabelPos().y + absPosY);
			}
		}
		if(linkText != null && linkText != "") {
			ShapePaintUtil.paintLinkMark(g2D, this, Color.BLACK);
		}
	}

	@Override
	public void refreshLabel() {
		switch (GraphModelPreferences.getInstance().getSpeciesContextDisplayName()) {
		case GraphModelPreferences.DISPLAY_COMMON_NAME:
		case GraphModelPreferences.DISPLAY_CONTEXT_NAME:
			setLabel(ruleParticipantSignature.getLabel());
			break;
		default:
			setLabel("no label");
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
