package cbit.vcell.geometry.concept;

import java.util.List;

import cbit.vcell.geometry.surface.Node;

public interface PolygonConcept {
	
	List<ThreeSpacePoint> pointList( );

	public abstract ThreeSpacePoint getNodes(int n);
	
	

}
