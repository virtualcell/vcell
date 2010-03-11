package org.vcell.sybil.util.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Set;

/*   PointOp  --- by Oliver Ruebenacker, UCHC --- December 2007 to February 2008
 *   A few basic operations for awt.Point
 */

public class PointOp {
	
	public static Point sum(Point p1, Dimension p2) { return new Point(p1.x + p2.width, p1.y + p2.height); }
	public static Dimension diff(Point p1, Point p2) { return new Dimension(p1.x - p2.x, p1.y - p2.y); }
	public static void add(Point p1, Dimension p2) { p1.x += p2.width; p1.y += p2.height; }
	public static void subtract(Point p1, Dimension p2) { p1.x -= p2.width; p1.y -= p2.height; }
	public static Point center(Point p, Dimension d) { return new Point(p.x + d.width/2, p.y + d.height/2); }
	public static Point mean(Point p1, Point p2) { return new Point((p1.x + p2.x)/2, (p1.y + p2.y)/2); }
	
	public static Point mean(Set<Point> points) {
		int num = 0;
		int xSum = 0;
		int ySum = 0;
		for(Point point : points) {
			xSum += point.x;
			ySum += point.y;
			++ num;
		}
		if(num == 0) { return new Point(); }
		else { return new Point( xSum / num, ySum / num); }
	}
	
}
