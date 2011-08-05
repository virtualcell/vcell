/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.graphlayout.energybased;

import java.util.List;

import org.vcell.util.geometry2d.Point2D;
import org.vcell.util.geometry2d.Vector2D;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyFunction;

public class NailShapeEnergyFunction implements EnergyFunction {
	
	protected final double rMax, k, rMaxSquared;
	
	public NailShapeEnergyFunction(double rMax, double k) { 
		this.rMax = rMax; this.k = k; this.rMaxSquared = rMax*rMax;
	}
	
	public int getNumberOfParameters() { return 2; }
			
	public double getEnergy(List<Point2D> ps) {
		if(ps.size() < 2) {
			throw 
			EnergySum.Default.getWrongNUmberOfParametersException(getNumberOfParameters(), ps.size());
		}
		Point2D p0 = ps.get(0);
		Point2D p1 = ps.get(1);
		double dx = p1.x - p0.x;
		double dy = p1.y - p0.y;
		double rSquared = dx*dx + dy*dy;
		return rSquared < rMaxSquared ? k*(rMaxSquared/rSquared - 1.0) : 0.0;
	}

	public Vector2D getForce(List<Point2D> ps, int index) {
		if(ps.size() < 2) {
			throw 
			EnergySum.Default.getWrongNUmberOfParametersException(getNumberOfParameters(), ps.size());
		}
		Point2D p0 = ps.get(0);
		Point2D p1 = ps.get(1);
		double dx = 0.0, dy = 0.0;
		switch(index) {
		case 0: {				
			dx = p1.x - p0.x;
			dy = p1.y - p0.y;
			double r = Math.sqrt(dx*dx + dy*dy);
			double r3 = r*r*r;
			return new Vector2D(k*dx/r3, k*dy/r3);				
		}
		case 1: {
			dx = p0.x - p1.x;
			dy = p0.y - p1.y;
		}
		}
		double rSquared = dx*dx + dy*dy;
		if(rSquared < rMaxSquared) {
			double factor = 2*k*rMaxSquared/(rSquared*rSquared);
			return new Vector2D(factor*dx, factor*dy);								
		} else {
			return new Vector2D();
		}
	}

}
