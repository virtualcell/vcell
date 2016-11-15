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
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import org.vcell.model.rbm.MolecularType;
import org.vcell.util.gui.ShapePaintUtil;

import cbit.gui.graph.ElipseShape;
import cbit.gui.graph.GraphModelPreferences;
import cbit.gui.graph.visualstate.VisualState;
import cbit.gui.graph.visualstate.imp.MutableVisualState;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.RuleParticipantSignature;

abstract class RuleParticipantSignatureDiagramShape extends ElipseShape {
	RuleParticipantSignature ruleParticipantSignature = null;
	boolean bVisible = true;				// we may want to hide this shape (not paint it) without destroying it
	
	private static final int height = 16;	// fixed (originally was 16, considered too large)
	private int width = 16;					// this will be recalculated based on the number of molecules in the species pattern
											// TODO: when the number of molecules changes and the RuleParticipantSignature object gets updated,
											// we need to adjust the width here as well
	
	private static final int displacement = 7;		// distance between circles representing molecular types
	private static final int circleDiameter = 12;	// diameter of circles representing molecular types

	private static final int leftmargin = 10;		// space left empty to the left before we start drawing the molecules
//	private static final int leftmargin = 9;		// space left empty to the left before we start drawing the molecules
	private static final int rightmargin = 0;
//	private static final int rightmargin = 7;
	
	private static final int SCS_LABEL_WIDTHPARM = 3;
	private static final String SCS_LABEL_TRUCATED = "...";
	private String smallLabel = null;
	protected Dimension smallLabelSize = new Dimension();
	protected Point smallLabelPos = new Point(0,0);

	private boolean bTruncateLabelName = true;
	private boolean bDisplayLabel = true;
	
	protected String linkText;

	public RuleParticipantSignatureDiagramShape(RuleParticipantSignature ruleParticipantSignature, ModelCartoon graphModel) {
		super(graphModel);
		// initialize with default widh / height, we compute width dynamically when we paint
		getSpaceManager().setSize(width, height);		// TODO: or setSizePreferred??
		
		this.ruleParticipantSignature = ruleParticipantSignature;
		defaultBG = Color.white;
		defaultFGselect = java.awt.Color.black;
		backgroundColor = defaultBG;
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
		getSpaceManager().setSizePreferred(width, height);
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
		getLabelPos().y + absPosY - getLabelSize().height,
		getLabelSize().width + 10, getLabelSize().height);
	}

	// TODO: Override ElipseShape::isInside()
	// our shape here is rounded rectangle while IsInside thinks it's an ellipse, which means 
	// that clicking near the corners of a long shape (with many molecules) fails to select the shape
	// TODO: the RoundRectangle2D contour below needs to be recalculated with updated width, using the 
	// species pattern in the RuleParticipantSignature 
	
	@Override
	public void paintSelf(Graphics2D g, int absPosX, int absPosY ) {
		if(!bVisible) {
			return;
		}
		
		int numMolecules = Math.max(1, ruleParticipantSignature.getSpeciesPattern().getMolecularTypePatterns().size());		// we reserve space for at least 1 molecule
		width = leftmargin + circleDiameter + displacement*(numMolecules-1) +1;
		getSpaceManager().setSize(width, height);
		
		int shapeHeight = getSpaceManager().getSize().height;
		int shapeWidth = getSpaceManager().getSize().width;
		Graphics2D g2D = g;
		
		Paint oldPaint = g2D.getPaint();
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color exterior;
		// draw the contour around the whole rule participant
		// no need to really draw it if the shapes of the molecules fill it perfectly, because it won't be visible anyway
		// however, the "isInside" function will need exactly this RoundRectangle2D to compute whether the point is inside the shape
		RoundRectangle2D contour = new RoundRectangle2D.Double(absPosX, absPosY, shapeWidth, shapeHeight, shapeHeight, shapeHeight);
		g2D.setPaint(isSelected() ? AbstractComponentShape.componentMediumPalePink : AbstractComponentShape.componentPaleGreen);
		g2D.fill(contour);
		exterior = isSelected() ? Color.red.darker().darker() : Color.darkGray;
		g.setColor(exterior);
		g2D.draw(contour);
		
		Model model = ((ReactionCartoon)graphModel).getModel();
		RbmModelContainer rbmmc = model.getRbmModelContainer();
		List<MolecularType> mtList = rbmmc.getMolecularTypeList();
		List<MolecularType> ruleSignatureMolecularTypes = ruleParticipantSignature.getMolecularTypes();
		
		// draw the molecules (they are properly drawn because we use the sp as it is in the associated RuleParticipantSignature object)
		for(int i=0; i<ruleSignatureMolecularTypes.size(); i++) {
			
			double offsetx = leftmargin + i*displacement -1.4;
			int offsety = getSpaceManager().getSize().height - circleDiameter -2;
			Ellipse2D icon = new Ellipse2D.Double(absPosX + offsetx, absPosY + offsety, circleDiameter, circleDiameter);
//			int offsetx = leftmargin + i*displacement;
//			int offsety = 0;
//			Rectangle2D icon = new Rectangle2D.Double(absPosX + offsetx, absPosY + offsety, displacement, shapeHeight-1);
	
			MolecularType mt = ruleSignatureMolecularTypes.get(i);
			int index = mtList.indexOf(mt);
			index = index%7;

			defaultBG = Color.lightGray;
			Color interior = Color.white;
			if(graphModel instanceof ReactionCartoon && ((ReactionCartoon)graphModel).getRuleParticipantGroupingCriteria() == RuleParticipantSignature.Criteria.full) {
				defaultBG = MolecularTypeLargeShape.colorTable[index];		// take color from molecular type color selection
			}
			backgroundColor = defaultBG;
//			darkerBackground = backgroundColor.darker().darker();
			exterior = !isSelected() ? backgroundColor.darker().darker() : backgroundColor.darker();
			Color[] colors = {interior, exterior};
	
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Point2D center = new Point2D.Double(absPosX + offsetx + circleDiameter/2, absPosY + offsety + circleDiameter/2);
			float radius = circleDiameter*0.5f;
			Point2D focus = new Point2D.Double(absPosX + offsetx + circleDiameter/2-2, absPosY + offsety + circleDiameter/2-2);
			float[] dist = {0.1f, 1.0f};
			RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);
//			Paint p = defaultBG.darker().darker();
			g2D.setPaint(p);
			g2D.fill(icon);
			g.setColor(forgroundColor);
			g2D.draw(icon);
		}		

		// draw label
		// TODO: we should regenerate the label using the species pattern in the RuleParticipantSignature (because 
		// the species pattern may have been changed or because a molecular type has been renamed)
		// TODO: see if RefreshLabel below works properly, if it does make a similar call to refresh the width!!!
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
					absPosX, getLabelPos().y - 2 + absPosY);
			}
		}
		if(linkText != null && linkText != "") {
			ShapePaintUtil.paintLinkMark(g2D, this, Color.BLACK);
		}
		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2D.setPaint(oldPaint);
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

	public void setVisible(boolean bVisible) {
		this.bVisible = bVisible;
	}
	public boolean isVisible() {
		return this.bVisible;
	}
}
