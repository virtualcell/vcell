package cbit.plot;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.geom.*;
/**
 * Just a wrapper so we can collect arrays of different lengths.
 * Creation date: (2/9/2001 2:17:26 AM)
 * @author: Ion Moraru
 */
public class Point2DSet {
	private Point2D[] points = new Point2D[0];
	Point2DSet(Point2D[] points) {
		if (points != null) {
			this.points = points;
		}
	}
	public int getNumberOfPoints() {
		return points.length;
	}
	public Point2D[] getPoints() {
		return points;
	}
	public void setPoints(Point2D[] newPoints) {
		if (newPoints == null) {
			newPoints = new Point2D[0];
		}
		points = newPoints;
	}
}
