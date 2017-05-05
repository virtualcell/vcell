/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel.pathway.shapes;

/*   ArrowPainter  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Creates and paints an arrow head
 */

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class ArrowPainter {

	public static GeneralPath getArrow(Point2D front, Point2D back, double width) {
		double deltaX = back.getX() - front.getX();
		double deltaY = back.getY() - front.getY();
		double distance = Math.sqrt(deltaX*deltaX+deltaY*deltaY);
		double X1 = front.getX();
		double Y1 = front.getY();
		double X2 = front.getX()+1.5*deltaX;
		double Y2 = front.getY()+1.5*deltaY;
		double X3 = X2+0.5*width*deltaY/distance;
		double Y3 = Y2-0.5*width*deltaX/distance;
		double X4 = X2-0.5*width*deltaY/distance;
		double Y4 = Y2+0.5*width*deltaX/distance;
		GeneralPath arrow = new GeneralPath();
		arrow.moveTo((float)X1,(float)Y1);
		arrow.lineTo((float)X3,(float)Y3);
		arrow.lineTo((float)X4,(float)Y4);
		arrow.lineTo((float)X1,(float)Y1);
		arrow.setWindingRule(GeneralPath.WIND_NON_ZERO);
		return arrow;
	}

	public static void paintArrow(Graphics2D g2D, Point start, Point end, double length, double width) {
		double dist = start.distance(end);
		if(dist > 0) {
			double xdiff = end.getX() - start.getX();
			double ydiff = end.getY() - start.getY(); 
			double xb = start.getX() + 0.5*xdiff;
			double yb = start.getY() + 0.5*ydiff;
			double xt = start.getX() + (0.5 + length/dist)*xdiff;
			double yt = start.getY() + (0.5 + length/dist)*ydiff;
			Point2D base = new Point2D.Double(xb, yb);
			Point2D tip = new Point2D.Double(xt, yt);
			g2D.fill(getArrow(tip, base, width));					
		}
	}

	
}
