package cbit.vcell.geometry.concept;

import java.util.List;

public interface PolygonConcept {

	List<ThreeSpacePoint> pointList( );

	public abstract ThreeSpacePoint getNodes(int n);



}
