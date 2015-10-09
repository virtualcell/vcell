package cbit.vcell.graph;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmElementAbstract;
import org.vcell.model.rbm.SpeciesPattern;

import cbit.vcell.graph.MolecularComponentLargeShape.ComponentStateLargeShape;
import cbit.vcell.model.ReactionRule;

public class PointLocationInShapeContext {
	// Hierarchy of shapes containing a Point
	// Used to describe which shapes (if any) are located under the cursor.
	
	public Point point = null;
	
	public ReactionRulePatternLargeShape rrps = null;
	public SpeciesPatternLargeShape sps = null;
	public MolecularTypeLargeShape mts = null;
	public MolecularComponentLargeShape mcs = null;
	public ComponentStateLargeShape csls = null;

	public PointLocationInShapeContext(Point point) {
		this.point = point;
	}
	
	public HighlightableShapeInterface getDeepestShape() {
		
		if(csls != null) {
			return csls;
		}
		if(mcs != null) {
			return mcs;
		}
		if(mts != null) {
			return mts;
		}
		if(sps != null) {
			return sps;
		}
		if(rrps != null) {
			return rrps;
		}
		return null;
	}
	
	public ComponentStateDefinition getComponentStateDefinition() {
		if(csls != null) {
			return csls.getComponentStateDefinition();
		}
		return null;
	}
	public ComponentStatePattern getComponentStatePattern() {
		if(csls != null) {
			return csls.getComponentStatePattern();
		}
		return null;
	}
	public MolecularComponentPattern getMolecularComponentPattern() {
		if(mcs != null) {
			return mcs.getMolecularComponentPattern();
		}
		return null;
	}
	public MolecularTypePattern getMolecularTypePattern() {
		if(mts != null) {
			return mts.getMolecularTypePattern();
		}
		return null;
	}
	public SpeciesPattern getSpeciesPattern() {
		if(sps != null) {
			return sps.getSpeciesPattern();
		}
		return null;
	}
	public ReactionRule getReactionRule() {
		if(rrps != null) {
			return rrps.getReactionRule();
		}
		return null;
	}
	public boolean isReactantsZone() {		// clicked within reactants area
		if(rrps != null) {
			return rrps.isReactants;
		}
		return false;
	}
	public boolean isProductsZone() {		// clicked within products area
		if(rrps != null) {
			return !rrps.isReactants;
		}
		return false;
	}

	public boolean highlightDeepestShape() {
		if(csls != null) {
			csls.setHighlight(true, false);
			if(sps != null) sps.setHighlight(true, false);				// we always highlight the sps if present
			if(rrps != null) rrps.setHighlight(true, rrps.isReactants);	// we always highlight the rrps if present
			return true;
		}
		if(mcs != null) {
			mcs.setHighlight(true, false);
			if(sps != null) sps.setHighlight(true, false);
			if(rrps != null) rrps.setHighlight(true, rrps.isReactants);
			return true;
		}
		if(mts != null) {
			mts.setHighlight(true, false);
			if(sps != null) sps.setHighlight(true, false);
			if(rrps != null) rrps.setHighlight(true, rrps.isReactants);
			return true;
		}
		if(sps != null) {
			sps.setHighlight(true, false);
			if(rrps != null) rrps.setHighlight(true, rrps.isReactants);
			return true;
		}
		if(rrps != null) {
			rrps.setHighlight(true, rrps.isReactants);
			return true;
		}
		return false;		// couldn't find anything to highlight
	}

//	public void paintContour(Graphics graphics, Rectangle2D rect) {
//		SpeciesPatternLargeShape.paintContour(graphics, rect);
//	}

	public void paintDeepestShape(Graphics graphics) {
		
		if(csls != null) {
			if(sps != null) sps.paintSelf(graphics);
			else csls.paintSelf(graphics);
			return;
		}
		if(mcs != null) {
			if(sps != null) sps.paintSelf(graphics);
			else mcs.paintSelf(graphics);
			return;
		}
		if(mts != null) {
			if(sps != null) sps.paintSelf(graphics);
			else mts.paintSelf(graphics);
			return;
		}
		if(sps != null) {
			sps.paintSelf(graphics);
			return;
		}
		if(rrps != null) {
			rrps.paintSelf(graphics);
			return;
		}

	}

	public boolean isInside(Rectangle2D rectangle) {
		return rectangle.contains(point);
	}
	
}
