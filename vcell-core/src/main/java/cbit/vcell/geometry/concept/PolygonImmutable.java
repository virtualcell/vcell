package cbit.vcell.geometry.concept;

import java.util.ArrayList;
import java.util.List;


public class PolygonImmutable implements PolygonConcept {
	final ArrayList<ThreeSpacePoint> points;
	
	public PolygonImmutable(ThreeSpacePoint ...point) {
		points = new ArrayList<>(point.length);
		for (ThreeSpacePoint p : point) {
			points.add(p);
		}
	}

	@Override
	public List<ThreeSpacePoint> pointList() {
		return points;
	}
	
	public ThreeSpacePoint getNodes(int i) {
		return points.get(i);
	}

}
