package cbit.vcell.mapping.spatial;

import java.beans.PropertyVetoException;

import org.vcell.util.Matchable;

import cbit.vcell.mapping.SimulationContext;


public class PointObject extends SpatialObject {
	
	private SpatialQuantity positionX = new SpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.X);
	private SpatialQuantity positionY = new SpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.Y);
	private SpatialQuantity positionZ = new SpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.Z);
	private SpatialQuantity velocityX = new SpatialQuantity(QuantityCategory.PointVelocity,QuantityComponent.X);
	private SpatialQuantity velocityY = new SpatialQuantity(QuantityCategory.PointVelocity,QuantityComponent.Y);
	private SpatialQuantity velocityZ = new SpatialQuantity(QuantityCategory.PointVelocity,QuantityComponent.Z);
	private SpatialQuantity distanceTo = new SpatialQuantity(QuantityCategory.DistanceToPoint,QuantityComponent.Scalar);
	private SpatialQuantity directionToX = new SpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.X);
	private SpatialQuantity directionToY = new SpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.Y);
	private SpatialQuantity directionToZ = new SpatialQuantity(QuantityCategory.DirectionToPoint,QuantityComponent.Z);
	
	
	public PointObject(PointObject argPointObject, SimulationContext argSimContext) throws PropertyVetoException {
		super(argPointObject, argSimContext);
	}

	public PointObject(String name, SimulationContext simContext) {
		super(name, simContext, 
				new QuantityCategory[] {QuantityCategory.PointPosition, 
										QuantityCategory.PointVelocity, 
										QuantityCategory.DirectionToPoint, 
										QuantityCategory.DistanceToPoint }, 
						new Boolean[] { new Boolean(true), 
										new Boolean(false), 
										new Boolean(true), 
										new Boolean(true) });
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof PointObject){
			if (!compareEqual0(obj)){
				return false;
			}
			return true;
		}
		return true;
	}
	
	@Override
	public String getDescription() {
		return "Point Object";
	}

	@Override
	public SpatialQuantity[] getSpatialQuantities() {
		return new SpatialQuantity[] { positionX, positionY, positionZ, velocityX, velocityY, velocityZ, distanceTo, directionToX, directionToY, directionToZ };
	}


}
