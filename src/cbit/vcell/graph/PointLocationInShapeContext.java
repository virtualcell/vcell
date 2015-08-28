package cbit.vcell.graph;

import java.awt.Graphics;
import java.awt.Point;

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;

public class PointLocationInShapeContext {
	// Hierarchy of shapes containing a Point
	// Used to describe which shapes (if any) are located under the cursor.
	
	public Point point = null;
	
	public SpeciesPatternLargeShape sps = null;
	public MolecularTypeLargeShape mts = null;
	public MolecularComponentLargeShape mcs = null;
	public MolecularComponentLargeShape.ComponentStateLargeShape csls = null;

	public PointLocationInShapeContext(Point point) {
		this.point = point;
	}
	
	public HighlightableShapeInterface getDeepestShape() {
		
		if(mcs != null) {
			return mcs;
		}
		if(mts != null) {
			return mts;
		}
		if(sps != null) {
			return sps;
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
	
	public void highlightDeepestShape() {
		if(mcs != null) {
			mcs.setHighlight(true);
			if(mts != null) mts.setHighlight(false);	// we don't highlight the mts because it's overkill - too much color
			if(sps != null) sps.setHighlight(true);		// we always highlight the sps if present
			return;
		}
		if(mts != null) {
			mts.setHighlight(true);
			if(sps != null) sps.setHighlight(true);
			return;
		}
		if(sps != null) {
			sps.setHighlight(true);
			return;
		}
	}

	public void paintDeepestShape(Graphics graphics) {
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
	}
	
}
