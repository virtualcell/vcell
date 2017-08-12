package cbit.vcell.mapping.spatial.processes;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import org.vcell.util.Compare;
import org.vcell.util.Issue;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext;
import org.vcell.util.Matchable;

import cbit.vcell.mapping.ParameterContext.LocalParameter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.spatial.PointObject;
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
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

	public PointLocation(PointLocation argPointLocation, SimulationContext argSimContext) {
		super(argPointLocation, argSimContext);
		this.pointObject = (PointObject)argSimContext.getSpatialObject(argPointLocation.getPointObject().getName());
	}

	@Override
	public List<SpatialQuantity> getReferencedSpatialQuantities() {
		ArrayList<SpatialQuantity> spatialQuantities = new ArrayList<SpatialQuantity>();
		if (pointObject!=null){
			spatialQuantities.add(pointObject.getSpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.X));
			spatialQuantities.add(pointObject.getSpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.Y));
			spatialQuantities.add(pointObject.getSpatialQuantity(QuantityCategory.PointPosition,QuantityComponent.Z));
		}
		return spatialQuantities;
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
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Point Location Process '"+getName()+"' refers to missing PointObject '"+pointObject.getName()+" (see Spatial Objects)", Issue.Severity.ERROR));
			}
			if (!pointObject.isQuantityCategoryEnabled(QuantityCategory.PointPosition)){
				issueList.add(new Issue(pointObject, issueContext, IssueCategory.Identifiers, "Point Location Process '"+getName()+"' refers to disabled quantity '"+QuantityCategory.PointPosition.description+"', please enable it.", Issue.Severity.ERROR));
			}
		}
	}
	
}
