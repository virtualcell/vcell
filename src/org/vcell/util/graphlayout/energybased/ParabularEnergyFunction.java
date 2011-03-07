package org.vcell.util.graphlayout.energybased;

import java.util.List;

import org.vcell.util.geometry2d.Point2D;
import org.vcell.util.geometry2d.Vector2D;
import org.vcell.util.graphlayout.energybased.EnergySum.EnergyFunction;

public class ParabularEnergyFunction implements EnergyFunction {

	protected final double k;
	
	public ParabularEnergyFunction(double k) { this.k = k; }
	
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
		return k*(dx*dx + dy*dy);
	}

	public Vector2D getForce(List<Point2D> ps, int index) {
		if(ps.size() < 2) {
			throw 
			EnergySum.Default.getWrongNUmberOfParametersException(getNumberOfParameters(), ps.size());
		}
		switch(index) {
		case 0: {				
			Point2D p0 = ps.get(0);
			Point2D p1 = ps.get(1);
			double dx = p1.x - p0.x;
			double dy = p1.y - p0.y;
			return new Vector2D(-2*k*dx, -2*k*dy);				
		}
		case 1: {
			Point2D p0 = ps.get(0);
			Point2D p1 = ps.get(1);
			double dx = p0.x - p1.x;
			double dy = p0.y - p1.y;
			return new Vector2D(-2*k*dx, -2*k*dy);				
		}
		}
		return new Vector2D();
	}

}