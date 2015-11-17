package cbit.vcell.graph;

import cbit.vcell.graph.SpeciesPatternSmallShape.DisplayRequirements;

// base class for all shapes
public interface AbstractShape {

	public abstract AbstractShape getParentShape();
	public abstract DisplayRequirements getDisplayRequirements();
}
