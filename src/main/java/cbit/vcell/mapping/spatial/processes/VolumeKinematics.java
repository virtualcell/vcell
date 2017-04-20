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
import cbit.vcell.mapping.spatial.SpatialObject;
import cbit.vcell.mapping.spatial.VolumeRegionObject;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityCategory;
import cbit.vcell.mapping.spatial.SpatialObject.QuantityComponent;
import cbit.vcell.mapping.spatial.SpatialObject.SpatialQuantity;
import cbit.vcell.model.ModelUnitSystem;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.units.VCUnitDefinition;

public class VolumeKinematics extends SpatialProcess {
	private VolumeRegionObject volumeRegionObject = null;

	public VolumeKinematics(String name, SimulationContext simContext) {
		super(name, simContext);
		
		ModelUnitSystem units = simContext.getModel().getUnitSystem();
		VCUnitDefinition velocityUnit = units.getLengthUnit().divideBy(units.getTimeUnit());
//		VCUnitDefinition viscosityUnit = units.getTimeUnit().getInverse();
//		LocalParameter viscosity = createNewParameter("viscosity", SpatialProcessParameterType.Viscosity, new Expression(0.0), viscosityUnit);
//		LocalParameter drag = createNewParameter("externalDrag", SpatialProcessParameterType.ExternalDrag, new Expression(0.0), dragUnit);
		LocalParameter velX = createNewParameter("velocityX", SpatialProcessParameterType.InternalVelocityX, new Expression(0.0), velocityUnit);
		LocalParameter velY = createNewParameter("velocityY", SpatialProcessParameterType.InternalVelocityY, new Expression(0.0), velocityUnit);
		LocalParameter velZ = createNewParameter("velocityZ", SpatialProcessParameterType.InternalVelocityZ, new Expression(0.0), velocityUnit);
		try {
			setParameters(new LocalParameter[] { velX, velY, velZ });
		} catch (ExpressionBindingException | PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create parameters: "+e.getMessage(),e);
		}
	}

	public VolumeKinematics(VolumeKinematics argVolumeKinematics, SimulationContext argSimContext) {
		super(argVolumeKinematics, argSimContext);
		this.volumeRegionObject = (VolumeRegionObject)argSimContext.getSpatialObject(argVolumeKinematics.getVolumeRegionObject().getName());
	}
	
	@Override
	public List<SpatialQuantity> getReferencedSpatialQuantities() {
		ArrayList<SpatialQuantity> spatialQuantities = new ArrayList<SpatialQuantity>();
		if (volumeRegionObject!=null){
			spatialQuantities.add(volumeRegionObject.getSpatialQuantity(QuantityCategory.InteriorVelocity,QuantityComponent.X));
			spatialQuantities.add(volumeRegionObject.getSpatialQuantity(QuantityCategory.InteriorVelocity,QuantityComponent.Y));
			spatialQuantities.add(volumeRegionObject.getSpatialQuantity(QuantityCategory.InteriorVelocity,QuantityComponent.Z));
		}
		return spatialQuantities;
	}
	
	public void setVolumeRegionObject(VolumeRegionObject volumeRegionObject){
		this.volumeRegionObject = volumeRegionObject;
	}
	
	public VolumeRegionObject getVolumeRegionObject(){
		return this.volumeRegionObject;
	}

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof VolumeKinematics){
			VolumeKinematics other = (VolumeKinematics)obj;
			if (!compareEqual0(other)){
				return false;
			}
			if (!Compare.isEqualOrNull(volumeRegionObject, other.volumeRegionObject)){
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Volume Kinematics";
	}

	@Override
	public List<SpatialObject> getSpatialObjects() {
		ArrayList<SpatialObject> spatialObjects = new ArrayList<SpatialObject>();
		spatialObjects.add(volumeRegionObject);
		return spatialObjects;
	}

	@Override
	public void gatherIssues(IssueContext issueContext, List<Issue> issueList) {
		if (simulationContext!=null && volumeRegionObject!=null){
			if (simulationContext.getSpatialObject(volumeRegionObject.getName()) != volumeRegionObject){
				issueList.add(new Issue(this, issueContext, IssueCategory.Identifiers, "Volume Kinematics '"+getName()+"' refers to missing volumeObject '"+volumeRegionObject.getName()+" (see Spatial Objects)", Issue.Severity.ERROR));
			}
			if (!volumeRegionObject.isQuantityCategoryEnabled(QuantityCategory.InteriorVelocity)){
				issueList.add(new Issue(volumeRegionObject, issueContext, IssueCategory.Identifiers, "Volume Kinematics '"+getName()+"' refers to disabled quantity '"+QuantityCategory.InteriorVelocity.description+"', please enable it.", Issue.Severity.ERROR));
			}
		}
	}

}
