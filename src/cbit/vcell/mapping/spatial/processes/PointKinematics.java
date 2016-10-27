package cbit.vcell.mapping.spatial.processes;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.Issue.IssueCategory;

import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.spatial.PointObject;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.units.VCUnitDefinition;

public class PointKinematics extends SpatialProcess {
	private PointObject pointObject = null;

	public PointKinematics(String name, SimulationContext simContext) {
		super(name, simContext);

		ModelUnitSystem units = simContext.getModel().getUnitSystem();
		VCUnitDefinition velocityUnit = units.getLengthUnit().divideBy(units.getTimeUnit());
		VCUnitDefinition lengthUnit = units.getLengthUnit();
		LocalParameter initX = createNewParameter("initialPosX", SpatialProcessParameterType.PointInitialPositionX, new Expression(0.0), lengthUnit);
		LocalParameter initY = createNewParameter("initialPosY", SpatialProcessParameterType.PointInitialPositionY, new Expression(0.0), lengthUnit);
		LocalParameter initZ = createNewParameter("initialPosZ", SpatialProcessParameterType.PointInitialPositionZ, new Expression(0.0), lengthUnit);
		LocalParameter velX = createNewParameter("velocityX", SpatialProcessParameterType.PointVelocityX, new Expression(0.0), velocityUnit);
		LocalParameter velY = createNewParameter("velocityY", SpatialProcessParameterType.PointVelocityY, new Expression(0.0), velocityUnit);
		LocalParameter velZ = createNewParameter("velocityZ", SpatialProcessParameterType.PointVelocityZ, new Expression(0.0), velocityUnit);
		try {
			setParameters(new LocalParameter[] { initX, initY, initZ, velX, velY, velZ });
		} catch (ExpressionBindingException | PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create parameters: "+e.getMessage(),e);
		}
	}

	public PointKinematics(PointKinematics argPointKinematics, SimulationContext argSimContext) {
		super(argPointKinematics, argSimContext);
		this.pointObject = (PointObject)argSimContext.getSpatialObject(argPointKinematics.getPointObject().getName());
	}
	
	public void setPointObject(PointObject pointObject){
		this.pointObject = pointObject;
	}
	
	public PointObject getPointObject(){
		return this.pointObject;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof PointKinematics){
			PointKinematics other = (PointKinematics)obj;
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
		return "Point Kinematics (initial position, velocity)";
	}

	@Override
	public List<SpatialObject> getSpatialObjects() {
		ArrayList<SpatialObject> spatialObjects = new ArrayList<SpatialObject>();
		spatialObjects.add(pointObject);
		return spatialObjects;
	}

	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		if (simulationContext!=null && pointObject!=null){
			if (simulationContext.getSpatialObject(pointObject.getName()) != pointObject){
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "PointKinematics '"+getName()+"' refers to missing PointObject '"+pointObject.getName()+" (see Spatial Objects)", Issue.Severity.ERROR));
			}
		}
	}
}
