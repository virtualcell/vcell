package cbit.vcell.mapping.spatial;

import java.beans.PropertyVetoException;

import org.vcell.util.Matchable;

import cbit.vcell.mapping.SimulationContext;

public class CurveObject extends SpatialObject {

	public CurveObject(CurveObject argCurveObject,	SimulationContext argSimContext) {
		super(argCurveObject, argSimContext);
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof CurveObject){
			if (!compareEqual0(obj)){
				return false;
			}
			return true;
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "CurveObject";
	}

	@Override
	public SpatialQuantity[] getSpatialQuantities() {
		return new SpatialQuantity[0];
	}

	@Override
	public void refreshName() throws PropertyVetoException {
	}

}
