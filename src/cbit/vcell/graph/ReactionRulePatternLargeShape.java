package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Displayable;

import cbit.vcell.client.desktop.biomodel.ReactionRuleEditorPropertiesPanel;
import cbit.vcell.client.desktop.biomodel.ReactionRuleParticipantSignaturePropertiesPanel;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRuleParticipant;

public class ReactionRulePatternLargeShape extends AbstractComponentShape implements HighlightableShapeInterface {

	public static final int DistanceBetweenSpeciesPatterns = 50;
	
	int xPos;
	int yPos;
	int height;
	
	LargeShapePanel shapePanel;
	private final int distanceBetweenSpeciesPatterns;
	
	ReactionRule rr;
	boolean isReactants;
	boolean bWriteName = false;
	int xOffset;
	PointLocationInShapeContext locationContext = null;
	
	List<SpeciesPatternLargeShape> speciesPatternShapeList = new ArrayList<SpeciesPatternLargeShape>();
	
	
	public ReactionRulePatternLargeShape(int xPos, int yPos, int height, LargeShapePanel shapePanel, Displayable owner, boolean isReactants) {

		this.xPos = xPos;
		this.yPos = yPos;
		this.height = height;
		this.shapePanel = shapePanel;
		this.rr = (ReactionRule)owner;
		this.isReactants = isReactants;
//		this.xOffset = ReactionRuleEditorPropertiesPanel.xOffsetInitial+xPos;
		this.xOffset = xPos;
		
		speciesPatternShapeList.clear();
		ReactionRule rr = (ReactionRule)owner;

		if(shapePanel == null) {
			distanceBetweenSpeciesPatterns = DistanceBetweenSpeciesPatterns;
		} else {
			int Ratio = 1;	// arbitrary factor, to be determined
			int zoomFactor = shapePanel.getZoomFactor() * Ratio;	// negative if going smaller
			distanceBetweenSpeciesPatterns = DistanceBetweenSpeciesPatterns + zoomFactor;
		}
		
		if(isReactants) {
			List<ReactantPattern> rpList = rr.getReactantPatterns();
			if(rpList == null || rpList.size() == 0) {
				return;
			}
			for(int i = 0; i<rpList.size(); i++) {
				SpeciesPattern sp = rpList.get(i).getSpeciesPattern();
				SpeciesPatternLargeShape sps = new SpeciesPatternLargeShape(xOffset, yPos, -1, sp, shapePanel, rr);
//				if(i==0) { sps.setHighlight(true); }
				if(i < rpList.size()-1) {
					sps.addEndText("+");
				} else {
					if(rr.isReversible()) {
						sps.addEndText("<->");
					} else {
						sps.addEndText("->");
					}
				}
				xOffset = sps.getRightEnd() + distanceBetweenSpeciesPatterns;	// distance between species patterns
				speciesPatternShapeList.add(sps);
			}
		} else {
			List<ProductPattern> ppList = rr.getProductPatterns();
			if(ppList == null || ppList.size() == 0) {
				return;
			}
			for(int i = 0; i<ppList.size(); i++) {
				SpeciesPattern sp = ppList.get(i).getSpeciesPattern();
				SpeciesPatternLargeShape sps = new SpeciesPatternLargeShape(xOffset, yPos, -1, sp, shapePanel, rr);
//				if(i==0) { sps.setHighlight(true); }
				if(i < ppList.size()-1) {
					sps.addEndText("+");
				}
				xOffset = sps.getRightEnd() + distanceBetweenSpeciesPatterns;	// distance between species patterns
				speciesPatternShapeList.add(sps);
			}
		}
	}
	
	public void setWriteName(boolean bWriteName) {
		this.bWriteName = bWriteName;
	}
	public void setPointLocationInShapeContext(PointLocationInShapeContext locationContext) {
		this.locationContext = locationContext;
	}
	public ReactionRule getReactionRule() {
		return rr;
	}
	public List<SpeciesPatternLargeShape> getSpeciesPatternShapeList() {
		return speciesPatternShapeList;
	}
	public int getXOffset() {
		return xOffset;
	}
	public int getRightEnd(){		// get the x of the right end of the species pattern
		if(speciesPatternShapeList.isEmpty()) {
			return xPos + MolecularTypeLargeShape.getDummyWidth(shapePanel);
		}
		int xRightmostMolecularType = 0;
		for(SpeciesPatternLargeShape spls : speciesPatternShapeList) {
			int theirs = spls.getRightEnd();
			if(xRightmostMolecularType < theirs) {
				xRightmostMolecularType = theirs;
			}
		}
		return xRightmostMolecularType;
	}
	
	public boolean contains(PointLocationInShapeContext locationContext) {
		
		// first we check if the point is inside a subcomponent of "this"
		for(SpeciesPatternLargeShape spls : speciesPatternShapeList) {
			if(spls.contains(locationContext)) {
				// since point is inside one of our components it's also inside "this"
				locationContext.rrps = this;
				return true;
			}
		}
		// even if the point it's not inside one of our subcomponents it may still be inside "this"
		int y = locationContext.point.y;
		// compute the dimensions of the SP contour
		// TODO: keep this code in sync!
		int ySP = yPos-3;
		int hSP = SpeciesPatternLargeShape.defaultHeight-2+ReactionRuleEditorPropertiesPanel.ReservedSpaceForNameOnYAxis;
		if(height > 0 && y > ySP && y < ySP + hSP) {
			locationContext.rrps = this;
			return true;
		}
		return false;
	}

	public void paintSelf(Graphics g) {
		paintSelf(g, true);
	}
	public void paintSelf(Graphics g, boolean bPaintContour) {
		if(bPaintContour) {
			paintContour(g);
		}
		
		if(bWriteName) {	// flag is set only when we display the list of rules where a participant signature diagram shape belongs to.
			paintName(g);
		}
		
		if(locationContext != null && this == locationContext.getDeepestShape()) {
			paintGhost(g, true);
		} else {
			paintGhost(g, false);
		}
		
		for(SpeciesPatternLargeShape stls : speciesPatternShapeList) {
			stls.paintSelf(g, bPaintContour);
		}
	}
	
	private void paintName(Graphics g) {

		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color colorOld = g2.getColor();
		Font fontOld = g2.getFont();
		
		g.setColor(Color.black);
		int fontSize = fontOld.getSize();
			
		int textX = xPos - 7;
		int textY =  yPos - ReactionRuleParticipantSignaturePropertiesPanel.ReservedSpaceForNameOnYAxis + fontSize;
		g2.drawString("Rule: ", textX, textY);

		Font font = fontOld.deriveFont((float) (fontOld.getSize())).deriveFont(Font.BOLD);
		g.setFont(font);
		fontSize = font.getSize();
			
		textX = textX + 30;
		g2.drawString(rr.getDisplayName(), textX, textY);

	    g2.setFont(fontOld);
		g2.setColor(colorOld);
	}
	
	private void paintGhost(Graphics g, boolean bShow) {
		if(height == -1) {
			height = SpeciesPatternLargeShape.defaultHeight;
		}
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
			
		int xOffset = xPos-SpeciesPatternLargeShape.calculateXExtent(shapePanel);
		for(SpeciesPatternLargeShape spls : speciesPatternShapeList) {
			xOffset = Math.max(xOffset, spls.getRightEnd());
		}
		xOffset += 30;	// offset from the left margin of the panel
		// compute the dimensions of the SP contour
		// TODO: keep this code in sync! xOffset is now a bit off to the right than the "contains" area, because it 
		//		 would interfere with the painting of the -> or <-> (reaction arrow)
		int ySP = yPos-3;
		int hSP = SpeciesPatternLargeShape.defaultHeight-2+ReactionRuleEditorPropertiesPanel.ReservedSpaceForNameOnYAxis;
		// the dimensions of participants contour is exactly 1 pixel wider than the dimensions of the species pattern
		Rectangle2D rect = new Rectangle2D.Double(xOffset, ySP, 2000, hSP);
		
		Color paleBlue = Color.getHSBColor(0.6f, 0.05f, 1.0f);		// hue, saturation, brightness
		Color darkerBlue = Color.getHSBColor(0.6f, 0.12f, 1.0f);	// a bit darker for border
		if(bShow) {
			g2.setPaint(paleBlue);
			g2.fill(rect);
			g2.setColor(darkerBlue);
			g2.draw(rect);
		} else {
			g2.setPaint(Color.white);
			g2.fill(rect);
			g2.setColor(Color.white);
			g2.draw(rect);
		}
	    g2.setPaint(paintOld);
		g2.setColor(colorOld);
	}
	private void paintContour(Graphics g) {
		
		 if(shapePanel.isViewSingleRow()) {
			 return;
		 }
		if(height == -1) {
			height = SpeciesPatternLargeShape.defaultHeight;
		}
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
			
		Color paleBlue = Color.getHSBColor(0.6f, 0.05f, 1.0f);		// hue, saturation, brightness
		Color darkerBlue = Color.getHSBColor(0.6f, 0.22f, 1.0f);	// a bit darker for border
		
		// compute the dimensions of the SP contour
		// TODO: keep this code in sync!
		int xSp = xPos-SpeciesPatternLargeShape.calculateXExtent(shapePanel);
		int ySP = yPos-3;
		int hSP = SpeciesPatternLargeShape.defaultHeight-2+ReactionRuleEditorPropertiesPanel.ReservedSpaceForNameOnYAxis;
		
		// the dimensions of participants contour is exactly 1 pixel wider than the dimensions of the species pattern
		Rectangle2D rect = new Rectangle2D.Double(xSp-1, ySP-1, 2000, hSP+2);
		
		if(shapePanel == null || shapePanel.isViewSingleRow()) {
			// we don't show contour when we display single row (view only, no edit), hence always paint white
			if(isHighlightedReactants() && isReactants) {
				g2.setColor(Color.white);
				g2.draw(rect);
			} else if(!isHighlightedReactants() && isReactants) {
				g2.setColor(Color.white);
				g2.draw(rect);
			}
			else if(isHighlightedProducts() && !isReactants) {
				g2.setColor(Color.white);
				g2.draw(rect);
			} else if(!isHighlightedProducts() && !isReactants) {
				g2.setColor(Color.white);
				g2.draw(rect);
			}
		} else {
			if(isHighlightedReactants() && isReactants) {
				g2.setColor(darkerBlue);
				g2.draw(rect);
			} else if(!isHighlightedReactants() && isReactants) {
				g2.setColor(Color.white);
				g2.draw(rect);
			}
			else if(isHighlightedProducts() && !isReactants) {
				g2.setColor(darkerBlue);
				g2.draw(rect);
			} else if(!isHighlightedProducts() && !isReactants) {
				g2.setColor(Color.white);
				g2.draw(rect);
			}
		}
	    g2.setPaint(paintOld);
		g2.setColor(colorOld);
	}

	public boolean isHighlightedReactants() {
		if(!shapePanel.isHighlighted(rr)) {
			return false;
		}
		return shapePanel.whatIsHighlighted == LargeShapePanel.WhatIsHighlighted.reactant ? true : false;
	}
	public boolean isHighlightedProducts() {
		if(!shapePanel.isHighlighted(rr)) {
			return false;
		}
		return shapePanel.whatIsHighlighted == LargeShapePanel.WhatIsHighlighted.product ? true : false;
	}
	@Override
	@Deprecated
	public boolean isHighlighted() {	// don't use this, is insufficient; use isHighlightedReactants() or isHighlightedProducts() 
		return shapePanel.isHighlighted(rr);
	}
	@Override
	public void setHighlight(boolean highlight, boolean isReactants) {
		if(rr == null || highlight == false) {
			shapePanel.rr = null;
			return;
		}
		shapePanel.rr = rr;
		shapePanel.whatIsHighlighted = isReactants ? LargeShapePanel.WhatIsHighlighted.reactant : LargeShapePanel.WhatIsHighlighted.product;
	}
	@Override
	public void turnHighlightOffRecursive(Graphics g) {
//		if(isReactants && !isHighlightedReactants()) {
//			return;		// if i'm the reactants bar and reactants are not highlighted, nothing to do
//		}
//		if(!isReactants && !isHighlightedProducts()) {
//			return;		// if i'm the products bar and products are not highlighted, nothing to do
//		}
		
		paintGhost(g, false);
		
		if(isReactants && isHighlightedReactants()) {
			// if i'm the reactants bar and reactants are highlighted, need to turn them off
			boolean oldHighlight = isHighlightedReactants();
			if(oldHighlight) {
				setHighlight(false, isReactants);
				paintContour(g);
			}
		} else if(!isReactants && isHighlightedProducts()) {
			// if i'm the product bar and products are highlighted, need to turn them off
			boolean oldHighlight = isHighlightedProducts();
			if(oldHighlight) {
				setHighlight(false, !isReactants);
				paintContour(g);
			}
		}
		for (SpeciesPatternLargeShape sps : speciesPatternShapeList) {
			sps.turnHighlightOffRecursive(g);
		}
	}
	
	public void flash(String matchKey) {
		for (SpeciesPatternLargeShape sps : speciesPatternShapeList) {
			sps.flash(matchKey);
		}
	}
	
	@Override
	public String getDisplayName() {
		throw new UnsupportedOperationException();
	}
	@Override
	public String getDisplayType() {
		return ReactionRule.typeName;
	}
}
