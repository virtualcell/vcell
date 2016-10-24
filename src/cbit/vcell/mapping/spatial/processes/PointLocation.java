package cbit.vcell.mapping.spatial.processes;

import java.beans.PropertyVetoException;

import org.vcell.util.Compare;
import org.vcell.util.Matchable;

import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.spatial.PointObject;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.units.VCUnitDefinition;

public class PointLocation extends SpatialProcess {
	private PointObject pointObject = null;

	public PointLocation(String name, SimulationContext simContext) {
		super(name, simContext);

		ModelUnitSystem units = simContext.getModel().getUnitSystem();
		VCUnitDefinition lengthUnit = units.getLengthUnit();
		LocalParameter posX = createNewParameter("positionX", SpatialProcessParameterType.PointPositionX, new Expression(0.0), lengthUnit);
		LocalParameter posY = createNewParameter("positionY", SpatialProcessParameterType.PointPositionY, new Expression(0.0), lengthUnit);
		LocalParameter posZ = createNewParameter("positionZ", SpatialProcessParameterType.PointPositionZ, new Expression(0.0), lengthUnit);
		try {
			setParameters(new LocalParameter[] { posX, posY, posZ });
		} catch (ExpressionBindingException | PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create parameters: "+e.getMessage(),e);
		}
	}

	public PointLocation(SpatialProcess argSpatialProcess, SimulationContext argSimContext) {
		super(argSpatialProcess, argSimContext);
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof PointLocation){
			PointLocation other = (PointLocation)obj;
			if (!compareEqual0(other)){
				return false;
			}
			if (!Compare.isEqualOrNull(pointObject, other.pointObject)){
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Point Location Process (explicit point position)";
	}

	public void setPointObject(PointObject pointObject) {
		this.pointObject = pointObject;
	}
	
	public PointObject getPointObject(){
		return this.pointObject;
	}

}
