package cbit.vcell.mapping.spatial.processes;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;
import org.vcell.util.Issue.IssueCategory;

import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SurfaceRegionObject;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.units.VCUnitDefinition;

public class SurfaceKinematics extends SpatialProcess {
	private SurfaceRegionObject surfaceRegionObject = null;

	public SurfaceKinematics(String name, SimulationContext simContext) {
		super(name, simContext);
		
		ModelUnitSystem units = simContext.getModel().getUnitSystem();
		VCUnitDefinition velocityUnit = units.getLengthUnit().divideBy(units.getTimeUnit());
		LocalParameter velX = createNewParameter("velocityX", SpatialProcessParameterType.SurfaceVelocityX, new Expression(0.0), velocityUnit);
		LocalParameter velY = createNewParameter("velocityY", SpatialProcessParameterType.SurfaceVelocityY, new Expression(0.0), velocityUnit);
		LocalParameter velZ = createNewParameter("velocityZ", SpatialProcessParameterType.SurfaceVelocityZ, new Expression(0.0), velocityUnit);
		try {
			setParameters(new LocalParameter[] { velX, velY, velZ });
		} catch (ExpressionBindingException | PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create parameters: "+e.getMessage(),e);
		}
	}

	public SurfaceKinematics(SurfaceKinematics argSurfaceKinematics, SimulationContext argSimContext) {
		super(argSurfaceKinematics, argSimContext);
		this.surfaceRegionObject = (SurfaceRegionObject)argSimContext.getSpatialObject(argSurfaceKinematics.getSurfaceRegionObject().getName());
	}
	
	public void setSurfaceRegionObject(SurfaceRegionObject surfaceRegionObject){
		this.surfaceRegionObject = surfaceRegionObject;
	}
	
	public SurfaceRegionObject getSurfaceRegionObject(){
		return this.surfaceRegionObject;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof SurfaceKinematics){
			SurfaceKinematics other = (SurfaceKinematics)obj;
			if (!compareEqual0(other)){
				return false;
			}
			if (!Compare.isEqualOrNull(surfaceRegionObject, other.surfaceRegionObject)){
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Membrane Kinematics";
	}

	@Override
	public List<SpatialObject> getSpatialObjects() {
		ArrayList<SpatialObject> spatialObjects = new ArrayList<SpatialObject>();
		spatialObjects.add(surfaceRegionObject);
		return spatialObjects;
	}

	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
	//	issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "SurfaceKinematics ..... FAKE ISSUE ...  not find corresponding surface region in geometry", Issue.Severity.ERROR));
	}
}
