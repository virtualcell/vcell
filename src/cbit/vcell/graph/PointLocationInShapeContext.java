package cbit.vcell.graph;

import java.awt.Point;

public class PointLocationInShapeContext {
	// Hierarchy of shapes containing a Point
	// Used to describe which shapes (if any) are located under the cursor.
	
	public Point point = null;
	
	
	public SpeciesPatternLargeShape sps = null;
	public MolecularTypeLargeShape mts = null;
	public MolecularComponentLargeShape mcs = null;
	

	public PointLocationInShapeContext(Point point) {
		this.point = point;
	}
	
	
	
	public Object getDeepestShape() {
		
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
}
