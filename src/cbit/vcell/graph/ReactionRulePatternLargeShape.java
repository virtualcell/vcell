package cbit.vcell.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.Displayable;

import cbit.vcell.client.desktop.biomodel.ReactionRuleEditorPropertiesPanel;
import cbit.vcell.model.ProductPattern;
import cbit.vcell.model.ReactantPattern;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRuleParticipant;

public class ReactionRulePatternLargeShape extends AbstractComponentShape implements HighlightableShapeInterface {

	int xPos;
	int yPos;
	int height;
	Graphics gc;
	ReactionRule rr;
	boolean isReactants;
	int xOffset;
	PointLocationInShapeContext locationContext = null;
	
	List<SpeciesPatternLargeShape> speciesPatternShapeList = new ArrayList<SpeciesPatternLargeShape>();
	
	
	public ReactionRulePatternLargeShape(int xPos, int yPos, int height, Graphics gc, Displayable owner, boolean isReactants) {

		this.xPos = xPos;
		this.yPos = yPos;
		this.height = height;
		this.gc = gc;
		this.rr = (ReactionRule)owner;
		this.isReactants = isReactants;
		this.xOffset = ReactionRuleEditorPropertiesPanel.xOffsetInitial;
		
		speciesPatternShapeList.clear();
		ReactionRule rr = (ReactionRule)owner;

		if(isReactants) {
			List<ReactantPattern> rpList = rr.getReactantPatterns();
			if(rpList == null || rpList.size() == 0) {
				return;
			}
			for(int i = 0; i<rpList.size(); i++) {
				SpeciesPattern sp = rpList.get(i).getSpeciesPattern();
				SpeciesPatternLargeShape sps = new SpeciesPatternLargeShape(xOffset, yPos, -1, sp, gc, rr);
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
				xOffset = sps.getRightEnd() + 42;	// distance between species patterns
				speciesPatternShapeList.add(sps);
			}
		} else {
			List<ProductPattern> ppList = rr.getProductPatterns();
			if(ppList == null || ppList.size() == 0) {
				return;
			}
			for(int i = 0; i<ppList.size(); i++) {
				SpeciesPattern sp = ppList.get(i).getSpeciesPattern();
				SpeciesPatternLargeShape sps = new SpeciesPatternLargeShape(xOffset, yPos, -1, sp, gc, rr);
//				if(i==0) { sps.setHighlight(true); }
				if(i < ppList.size()-1) {
					sps.addEndText("+");
				}
				xOffset = sps.getRightEnd() + 42;	// distance between species patterns
				speciesPatternShapeList.add(sps);
			}
		}
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
		
		if(locationContext != null && this == locationContext.getDeepestShape()) {
			paintGhost(g, true);
		} else {
			paintGhost(g, false);
		}
		
		for(SpeciesPatternLargeShape stls : speciesPatternShapeList) {
			stls.paintSelf(g, bPaintContour);
		}
	}
	public void paintGhost(Graphics g, boolean bShow) {
		if(height == -1) {
			height = 80;
		}
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
			
		int xOffset = xPos-SpeciesPatternLargeShape.xExtent;
		for(SpeciesPatternLargeShape spls : speciesPatternShapeList) {
			xOffset = Math.max(xOffset, spls.getRightEnd());
		}
		xOffset += 30;
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
	public void paintContour(Graphics g) {
		if(height == -1) {
			height = 80;
		}
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		Paint paintOld = g2.getPaint();
			
		Color paleBlue = Color.getHSBColor(0.6f, 0.05f, 1.0f);		// hue, saturation, brightness
		Color darkerBlue = Color.getHSBColor(0.6f, 0.22f, 1.0f);	// a bit darker for border
		
		// compute the dimensions of the SP contour
		// TODO: keep this code in sync!
		int xSp = xPos-SpeciesPatternLargeShape.xExtent;
		int ySP = yPos-3;
		int hSP = SpeciesPatternLargeShape.defaultHeight-2+ReactionRuleEditorPropertiesPanel.ReservedSpaceForNameOnYAxis;
		// the dimensions of participants contour is exactly 1 pixel wider than the dimensions of the species pattern
		Rectangle2D rect = new Rectangle2D.Double(xSp-1, ySP-1, 2000, hSP+2);
		if(isHighlightedReactants() && isReactants) {
			g2.setColor(darkerBlue);
			g2.draw(rect);
		} else if(!isHighlightedReactants() && isReactants) {
//			g2.setPaint(Color.white);
//			g2.fill(rect);
			g2.setColor(Color.white);
			g2.draw(rect);
		}
		else if(isHighlightedProducts() && !isReactants) {
			g2.setColor(darkerBlue);
			g2.draw(rect);
		} else if(!isHighlightedProducts() && !isReactants) {
//			g2.setPaint(Color.white);
//			g2.fill(rect);
			g2.setColor(Color.white);
			g2.draw(rect);
		}
	    g2.setPaint(paintOld);
		g2.setColor(colorOld);
	}

	public boolean isHighlightedReactants() {
		if(!rr.isHighlighted()) {
			return false;
		}
		return rr.isHighlightedReactants();
	}
	public boolean isHighlightedProducts() {
		if(!rr.isHighlighted()) {
			return false;
		}
		return !rr.isHighlightedReactants();
	}
	@Override
	@Deprecated
	public boolean isHighlighted() {	// don't use this, is insufficient; use isHighlightedReactants() or isHighlightedProducts() 
		return rr.isHighlighted();
	}
	@Override
	public void setHighlight(boolean highlight, boolean isReactants) {
		if(rr == null) {
			// TODO: add code here, what if a plain reaction?
			return;
		}
		rr.setHighlighted(highlight, isReactants);		
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

	
}
